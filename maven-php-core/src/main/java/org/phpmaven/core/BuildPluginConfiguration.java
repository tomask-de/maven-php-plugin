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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for a component that allows an additional configuration via build plugin.
 * 
 * <p>
 *   Use this annotation on the plugin component (interface class) to load additional configuration
 *   options. Configuration could either be applied on the mojo that is using a component
 *   or it can be applied at plugin level. Set the groupId and the pluginId of the plugin that
 *   provides the component.
 * </p>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BuildPluginConfiguration {
    
    /**
     * Returns the group id used for configuration.
     * @return the group id.
     */
    String groupId();
    
    /**
     * Returns the artifact id used for configuration.
     * @return the artifact id.
     */
    String artifactId();

    /**
     * Returns the path to the configuration.
     * @return xml path to the configuration.
     */
    String path() default "";
    
    /**
     * Filters specific xml nodes from configuration.
     * @return filtered nodes.
     */
    String[] filter() default { };

}
