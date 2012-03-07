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

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.configurator.BasicComponentConfigurator;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.AbstractConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * The extended component configurator allowing xpp3dom conversion.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
@Component(role = ComponentConfigurator.class, hint = "php-maven", instantiationStrategy = "per-lookup")
public class ExtendedComponentConfigurator extends BasicComponentConfigurator {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void configureComponent(final Object component, final PlexusConfiguration configuration,
                                    final ExpressionEvaluator expressionEvaluator, final ClassRealm containerRealm,
                                    final ConfigurationListener listener)
        throws ComponentConfigurationException {
        this.converterLookup.registerConverter(new Xpp3DomConverter());
        super.configureComponent(component, configuration, expressionEvaluator, containerRealm, listener);
    }
    
    /**
     * Converter for xpp3dom.
     */
    private static final class Xpp3DomConverter extends AbstractConfigurationConverter {

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canConvert(@SuppressWarnings("rawtypes") final Class type) {
            return Xpp3Dom.class.isAssignableFrom(type);
        }
        
        public Object fromConfiguration(
                final ConverterLookup converterLookup,
                final PlexusConfiguration configuration,
                @SuppressWarnings("rawtypes") final Class type,
                @SuppressWarnings("rawtypes") final Class baseType,
                final ClassLoader classLoader,
                final ExpressionEvaluator expressionEvaluator,
                final ConfigurationListener listener)
            throws ComponentConfigurationException {
            
            if (configuration instanceof XmlPlexusConfiguration) {
                final XmlPlexusConfiguration xmlPlexus = (XmlPlexusConfiguration) configuration;
                return toXpp3Dom(xmlPlexus);
            }
            
            return null;
        }

        /**
         * Converts this config to a xpp3dom node.
         * @param childConf config.
         * @return xpp3dom node.
         */
        private Xpp3Dom toXpp3Dom(XmlPlexusConfiguration xmlPlexus) {
            final Xpp3Dom dom = new Xpp3Dom(xmlPlexus.getName());
            for (final String attrName : xmlPlexus.getAttributeNames()) {
                dom.setAttribute(attrName, xmlPlexus.getAttribute(attrName));
            }
            for (final PlexusConfiguration childConf : xmlPlexus.getChildren()) {
                dom.addChild(this.toXpp3Dom((XmlPlexusConfiguration) childConf));
            }
            dom.setValue(xmlPlexus.getValue());
            return dom;
        }
        
    }

}
