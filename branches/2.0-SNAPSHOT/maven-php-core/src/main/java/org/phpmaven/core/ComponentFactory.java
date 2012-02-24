/**
 * Copyright 2010-2012 by PHP-maven.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.phpmaven.core;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.DebugConfigurationListener;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.PluginParameterExpressionEvaluator;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.component.configurator.expression.TypeAwareExpressionEvaluator;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * The component lookup factory for components that are configured via pom.xml.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
@Component(role = IComponentFactory.class, instantiationStrategy = "per-lookup")
@SuppressWarnings("deprecation")
public class ComponentFactory implements IComponentFactory {

    /**
     * The plexus container.
     */
    @Requirement
    private PlexusContainer plexusContainer;
    
    /**
     * The logger.
     */
    @Requirement
    private Logger logger;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T lookup(Class<T> clazz, Xpp3Dom configuration, MavenSession session)
        throws ComponentLookupException, PlexusConfigurationException {
        return this.lookup(clazz, configuration == null ? EMPTY_CONFIG : new Xpp3Dom[]{configuration}, session);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T lookup(Class<T> clazz, String roleHint, Xpp3Dom configuration, MavenSession session)
        throws ComponentLookupException, PlexusConfigurationException {
        return this.lookup(
                clazz,
                roleHint,
                configuration == null ? EMPTY_CONFIG : new Xpp3Dom[]{configuration},
                session);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T lookup(Class<T> clazz, Xpp3Dom[] configuration, MavenSession session)
        throws ComponentLookupException, PlexusConfigurationException {
        final T result = this.plexusContainer.lookup(clazz);
        
        final ClassRealm realm = this.plexusContainer.getComponentDescriptor(clazz.getName(), "default").getRealm();
        
        configure(
                clazz,
                configuration,
                session.getCurrentProject(),
                result,
                realm,
                session);
        
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T lookup(Class<T> clazz, String roleHint, Xpp3Dom[] configuration, MavenSession session)
        throws ComponentLookupException, PlexusConfigurationException {
        final T result = this.plexusContainer.lookup(clazz, roleHint);
        
        final ClassRealm realm = this.plexusContainer.getComponentDescriptor(clazz.getName(), roleHint).getRealm();
        
        configure(
                clazz,
                configuration,
                session.getCurrentProject(),
                result,
                realm,
                session);
        
        return result;
    }

    /**
     * Configure the component.
     * @param clazz
     * @param configuration
     * @param mavenProject
     * @param result
     * @param realm
     * @param session
     * @throws PlexusConfigurationException
     */
    private <T> void configure(Class<T> clazz, Xpp3Dom[] configuration,
            MavenProject mavenProject, final T result, final ClassRealm realm, MavenSession session)
        throws PlexusConfigurationException {
        final MojoExecution execution = new MojoExecution(null);
        final ExpressionEvaluator expressionEvaluator = new PluginParameterExpressionEvaluator(session, execution);
        
        Xpp3Dom classAnnotationConfig = null;
        for (final Field field : result.getClass().getDeclaredFields()) {
            final ConfigurationParameter param = field.getAnnotation(ConfigurationParameter.class);
            if (param != null) {
                if (classAnnotationConfig == null) {
                    classAnnotationConfig = new Xpp3Dom("configuration");
                }
                final Xpp3Dom child = new Xpp3Dom(param.name());
                child.setValue(param.expression());
                classAnnotationConfig.addChild(child);
            }
        }
        if (classAnnotationConfig != null) {
            final PlexusConfiguration pomConfiguration = new XmlPlexusConfiguration(classAnnotationConfig);
            populatePluginFields(result, pomConfiguration, expressionEvaluator, realm);
        }
        
        configureFromAnnotation(clazz, mavenProject, result, realm,
                expressionEvaluator);
        configureFromAnnotation(result.getClass(), mavenProject, result, realm,
                expressionEvaluator);
        
        if (configuration == null || configuration.length == 0) {
            final PlexusConfiguration pomConfiguration = new XmlPlexusConfiguration("configuration");
            populatePluginFields(result, pomConfiguration, expressionEvaluator, realm);
        } else {
            for (final Xpp3Dom config : configuration) {
                final PlexusConfiguration pomConfiguration = new XmlPlexusConfiguration(config);
                populatePluginFields(result, pomConfiguration, expressionEvaluator, realm);
            }
        }
    }

    private <T> void configureFromAnnotation(Class<? extends T> clazz,
            MavenProject mavenProject, final T result, final ClassRealm realm,
            final ExpressionEvaluator expressionEvaluator)
        throws PlexusConfigurationException {
        final BuildPluginConfiguration pConfiguration = clazz.getAnnotation(BuildPluginConfiguration.class);
        if (pConfiguration != null) {
            
            final Xpp3Dom config = this.getBuildConfig(
                    mavenProject,
                    pConfiguration.groupId(),
                    pConfiguration.artifactId());
            
            if (config != null) {
                final PlexusConfiguration pomConfiguration = new XmlPlexusConfiguration(config);
                populatePluginFields(result, pomConfiguration, expressionEvaluator, realm);
            }
        }
    }
    
    /**
     * Populates the plugin fields by using the given configuration.
     * @param component the component.
     * @param configuration the configuration.
     * @param expressionEvaluator the expression evaluator.
     * @param realm the class realm.
     * @throws PlexusConfigurationException thrown on configuration errors
     */
    private void populatePluginFields(Object component, PlexusConfiguration configuration,
        ExpressionEvaluator expressionEvaluator, ClassRealm realm)
        throws PlexusConfigurationException {
        ComponentConfigurator configurator = null;
        
        try {
            configurator = this.plexusContainer.lookup(ComponentConfigurator.class, "basic");
            
            final ConfigurationListener listener = new DebugConfigurationListener(this.logger);
            
            logger.debug("Configuring component '" + component.getClass().getName() + "' with basic configurator -->");
            configurator.configureComponent(component, configuration, expressionEvaluator, realm, listener);
            
            logger.debug("-- end configuration --");
        } catch (ComponentConfigurationException e) {
            String message = "Unable to parse configuration of component " + component.getClass().getName();
            if (e.getFailedConfiguration() != null) {
                message += " for parameter " + e.getFailedConfiguration().getName();
            }
            message += ": " + e.getMessage();
            
            throw new IllegalStateException(message, e);
        } catch (ComponentLookupException e) {
            throw new PlexusConfigurationException(
                         "Unable to retrieve component configurator basic "
                             + " for configuration of component " + component.getClass().getName(), e);
        } catch (NoClassDefFoundError e) {
            final ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
            final PrintStream ps = new PrintStream(os);
            ps.println("A required class was missing during configuration of component "
                    + component.getClass().getName() + ": " + e.getMessage());

            throw new PlexusConfigurationException(os.toString(), e);
        } catch (LinkageError e) {
            final ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
            final PrintStream ps = new PrintStream(os);
            ps.println("An API incompatibility was encountered during configuration of component "
                    + component.getClass().getName() + ": " + e.getClass().getName() + ": " + e.getMessage());

            throw new PlexusConfigurationException(os.toString(), e);
        } finally {
            if (configurator != null) {
                try {
                    this.plexusContainer.release(configurator);
                } catch (ComponentLifecycleException e) {
                    logger.debug("Failed to release component configurator - ignoring.");
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Xpp3Dom getBuildConfig(final MavenProject project, String groupid, String artifactId) {
        @SuppressWarnings("unchecked")
        final List<Plugin> plugins = project.getBuildPlugins();
        for (final Plugin plugin : plugins) {
            if (plugin.getGroupId().equals(groupid)
                    && plugin.getArtifactId().equals(artifactId)) {
                return (Xpp3Dom) plugin.getConfiguration();
            }
        }
        return null;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public <T> T filterString(final MavenSession session, final String source, Class<T> type)
        throws ExpressionEvaluationException {
        final MojoExecution execution = new MojoExecution(null);
        final TypeAwareExpressionEvaluator expressionEvaluator = new PluginParameterExpressionEvaluator(
                session, execution);
        return type.cast(expressionEvaluator.evaluate(source, type));
    }

}
