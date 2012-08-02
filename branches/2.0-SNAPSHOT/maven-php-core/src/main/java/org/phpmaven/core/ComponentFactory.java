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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.codehaus.plexus.component.configurator.converters.ConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.converters.lookup.DefaultConverterLookup;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.component.configurator.expression.TypeAwareExpressionEvaluator;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
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

        ClassRealm realm = null;
        final ComponentDescriptor<?> componentDescriptor =
            this.plexusContainer.getComponentDescriptor(clazz.getName(), "default");
        if (componentDescriptor != null) {
            realm = componentDescriptor.getRealm();
        }
        if (realm == null && clazz.getClassLoader() instanceof ClassRealm) {
            realm = (ClassRealm) clazz.getClassLoader();
        }
        if (realm == null) {
            realm = this.plexusContainer.getContainerRealm();
        }
        
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
        
        ClassRealm realm = null;
        final ComponentDescriptor<?> componentDescriptor =
                this.plexusContainer.getComponentDescriptor(clazz.getName(), roleHint);
        if (componentDescriptor != null) {
            realm = componentDescriptor.getRealm();
        }
        if (realm == null && clazz.getClassLoader() instanceof ClassRealm) {
            realm = (ClassRealm) clazz.getClassLoader();
        }
        if (realm == null) {
            realm = this.plexusContainer.getContainerRealm();
        }
        
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
        throws ComponentLookupException, PlexusConfigurationException {
        final MojoExecution execution = new MojoExecution(null);
        final ExpressionEvaluator expressionEvaluator = new PluginParameterExpressionEvaluator(session, execution);
        
        Xpp3Dom classAnnotationConfig = null;
        Class<?> resultClazz = result.getClass();
        while (resultClazz != null) {
            for (final Field field : resultClazz.getDeclaredFields()) {
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
            resultClazz = resultClazz.getSuperclass();
        }
        if (classAnnotationConfig != null) {
            final PlexusConfiguration pomConfiguration = new XmlPlexusConfiguration(classAnnotationConfig);
            populatePluginFields(result, pomConfiguration, expressionEvaluator, realm);
        }
        
        final Set<Class<?>> classes = this.getAllClasses(result);
        for (final Class<?> cls : classes) {
            configureFromAnnotation(cls, mavenProject, result, realm,
                    expressionEvaluator);
        }
        
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
    
    private Set<Class<?>> getAllClasses(Object obj) {
        final Set<Class<?>> result = new HashSet<Class<?>>();
        final Set<Class<?>> newcls = new HashSet<Class<?>>();
        newcls.add(obj.getClass());
        while (!newcls.isEmpty()) {
            final Class<?> cls = newcls.iterator().next();
            newcls.remove(cls);
            if (result.add(cls)) {
                if (cls.getSuperclass() != null) {
                    newcls.add(cls.getSuperclass());
                }
                for (final Class<?> cls2 : cls.getInterfaces()) {
                    newcls.add(cls2);
                }
            }
        }
        return result;
    }

    private void configureFromAnnotation(Class<?> clazz,
            MavenProject mavenProject, final Object result, final ClassRealm realm,
            final ExpressionEvaluator expressionEvaluator)
        throws ComponentLookupException, PlexusConfigurationException {
        final BuildPluginConfiguration pConfiguration = clazz.getAnnotation(BuildPluginConfiguration.class);
        if (pConfiguration != null) {
            
            Xpp3Dom origConfig = this.getBuildConfig(
                    mavenProject,
                    pConfiguration.groupId(),
                    pConfiguration.artifactId());
            
            for (final String cfg : pConfiguration.path().split("/")) {
                if (cfg.length() > 0) {
                    origConfig = origConfig == null ? null : origConfig.getChild(cfg);
                }
            }
            
            Xpp3Dom config = origConfig;
            
            // filtering needed?
            if (pConfiguration.filter().length > 0 && origConfig != null) {
                final Set<String> filtered = new HashSet<String>();
                for (final String filter : pConfiguration.filter()) {
                    filtered.add(filter);
                }
                
                config = new Xpp3Dom(origConfig);
                for (int i = 0; i < config.getChildCount(); i++) {
                    if (filtered.contains(config.getChild(i).getName())) {
                        config.removeChild(i);
                        i--;
                    }
                }
            }
            
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
        throws ComponentLookupException, PlexusConfigurationException {
        ComponentConfigurator configurator = null;
        
        try {
            configurator = this.plexusContainer.lookup(ComponentConfigurator.class, "php-maven");
            
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
            
            throw new PlexusConfigurationException(message, e);
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
        
        final ConverterLookup convLookup = new DefaultConverterLookup();
        try {
            final ConfigurationConverter converter = convLookup.lookupConverterForType(type);
            
            final Object value = expressionEvaluator.evaluate(source);
            if (value == null) {
                return null;
            }
            final PlexusConfiguration configuration = new XmlPlexusConfiguration("configuration");
            configuration.setValue(value.toString());
            return type.cast(converter.fromConfiguration(
                    convLookup,
                    configuration,
                    type,
                    value.getClass(),
                    this.plexusContainer.getContainerRealm(),
                    expressionEvaluator));
        } catch (ComponentConfigurationException ex) {
            throw new ExpressionEvaluationException("Problems converting filtered string to target class", ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends IService> T[] getServiceImplementations(Class<T> type,
            Xpp3Dom[] config, MavenSession session)
        throws ComponentLookupException, PlexusConfigurationException {
        final List<T> list = this.plexusContainer.lookupList(type);
        @SuppressWarnings("unchecked")
        final T[] result = list.toArray((T[]) Array.newInstance(type, list.size()));
        
        ClassRealm realm = null;
        final ComponentDescriptor<?> componentDescriptor =
                this.plexusContainer.getComponentDescriptor(type.getName(), "default");
        if (componentDescriptor != null) {
            realm = componentDescriptor.getRealm();
        }
        if (realm == null) {
            realm = this.plexusContainer.getContainerRealm();
        }
        
        for (final T res : result) {
            configure(
                    type,
                    config,
                    session.getCurrentProject(),
                    res,
                    realm,
                    session);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends IService> T[] getServiceImplementations(Class<T> type,
            MavenSession session) throws ComponentLookupException,
            PlexusConfigurationException {
        return this.getServiceImplementations(type, EMPTY_CONFIG, session);
    }

}
