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

package org.phpmaven.phpunit.test;

import java.io.File;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.it.Verifier;
import org.apache.maven.monitor.logging.DefaultLog;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.phar.IPharPackager;
import org.phpmaven.phar.IPharPackagerConfiguration;
import org.phpmaven.test.AbstractTestCase;

/**
 * test cases for PHPUNIT support.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public abstract class AbstractVersionTestCase extends AbstractTestCase {

    /**
     * Package.
     */
    protected static final class Pkg {
        
        /**
         * Groupid.
         */
        private String groupId;
        
        /**
         * Artifactid.
         */
        private String artifactId;
        
        /**
         * version.
         */
        private String version;

        /**
         * Constructor to create a package.
         * @param groupId group id.
         * @param artifactId artifact id.
         * @param version version.
         */
        public Pkg(String groupId, String artifactId, String version) {
            this.groupId = groupId;
            this.artifactId = artifactId;
            this.version = version;
        }

        /**
         * The group id.
         * @return the groupId
         */
        public String getGroupId() {
            return this.groupId;
        }

        /**
         * The artifact id.
         * @return the artifactId
         */
        public String getArtifactId() {
            return this.artifactId;
        }

        /**
         * The version.
         * @return the version
         */
        public String getVersion() {
            return this.version;
        }
        
    }
    
    /**
     * Prepare maven with php dependencies.
     * @param v verifier
     * @param session maven session.
     * @param packages packages to be installed
     * @throws Exception 
     */
    protected void prepareMaven(Verifier v, MavenSession session, Pkg[] packages) throws Exception {
        final IComponentFactory factory = lookup(IComponentFactory.class);
        final IPharPackagerConfiguration config = factory.lookup(
                IPharPackagerConfiguration.class,
                IComponentFactory.EMPTY_CONFIG,
                session);
        final IPharPackager packager = config.getPharPackager();
        final DefaultLog logger = new DefaultLog(new ConsoleLogger());

        for (final Pkg p : packages) {
            final Verifier verifier = new Verifier(v.getBasedir(), true);
            verifier.getSystemProperties().put(
                    "artifact",
                    p.getGroupId() + ":" +
                    p.getArtifactId() + ":" +
                    p.getVersion() + ":phar");
            verifier.setLocalRepo(v.localRepo);
            verifier.setAutoclean(false);
            verifier.executeGoal("org.apache.maven.plugins:maven-dependency-plugin:2.4:get");
            verifier.verifyErrorFreeLog();

            final File pharPackage = new File(this.getLocalReposDir(),
                    p.getGroupId().replace(".", "/") + 
                    "/" + p.getArtifactId() +
                    "/" + p.getVersion() + 
                    "/" + p.getArtifactId() + "-" + p.getVersion() + ".phar");
            assertTrue(pharPackage.exists());
            packager.extractPharTo(
                    pharPackage,
                    new File(session.getCurrentProject().getBasedir(), "target/php-test-deps"),
                    logger);
        }
    }

}