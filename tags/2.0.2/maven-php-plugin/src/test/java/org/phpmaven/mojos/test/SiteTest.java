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

package org.phpmaven.mojos.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.it.Verifier;
import org.phpmaven.test.AbstractTestCase;

/**
 * Test the site generation.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class SiteTest extends AbstractTestCase {
    
    /**
     * tests the goal "site" with a project containing all default reports.
     *
     * @throws Exception 
     */
    public void testSiteOnClean() throws Exception {
        final Verifier verifier = this.getPhpMavenVerifier("mojos-sites/site-all");
        
        // delete the pom from previous runs
        verifier.deleteArtifact("org.phpmaven.test", "site-all", "0.0.1", "pom");
        verifier.deleteArtifact("org.phpmaven.test", "site-all", "0.0.1", "phar");
        verifier.setAutoclean(true);

        final List<String> goals = new ArrayList<String>();
        goals.add("site");
        verifier.addCliOption("-X");
        verifier.executeGoals(goals);
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();
        
        // phpdocumentor report
        verifier.assertFilePresent("target/site/apidocs/phpdocumentor.html");
        verifier.assertFilePresent("target/site/apidocs/phpdocumentor/index.html");
        verifier.assertFilePresent("target/site/apidocs/phpdocumentor/packages.html");
        verifier.assertFilePresent("target/site/apidocs/phpdocumentor/default/_MyClass.php.html");
        
        // phpunit-coverage report
        verifier.assertFilePresent("target/site/phpunit/coverage.html");
        verifier.assertFilePresent("target/site/phpunit/report.html");
        verifier.assertFilePresent("target/site/phpunit/index.html");
        verifier.assertFilePresent("target/site/phpunit/classes.html");
        verifier.assertFilePresent("target/site/phpunit/classes_MyClass.php.html");
        
        // test report
        verifier.assertFilePresent("target/site/phpunit/report.html");
    }
    
    /**
     * tests the goal "site" with a clover xml report.
     *
     * @throws Exception 
     */
    public void testSiteClover() throws Exception {
        final Verifier verifier = this.getPhpMavenVerifier("mojos-sites/site-clover");
        
        // delete the pom from previous runs
        verifier.deleteArtifact("org.phpmaven.test", "site-all", "0.0.1", "pom");
        verifier.deleteArtifact("org.phpmaven.test", "site-all", "0.0.1", "phar");
        verifier.setAutoclean(true);

        final List<String> goals = new ArrayList<String>();
        goals.add("site");
        verifier.addCliOption("-X");
        verifier.addCliOption("-DcoverageOutputClover=1");
        verifier.executeGoals(goals);
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();
        
        // phpunit-coverage report
        verifier.assertFilePresent("target/site/phpunit/coverage.html");
        verifier.assertFilePresent("target/site/phpunit/index.html");
        verifier.assertFilePresent("target/site/phpunit/classes.html");
        verifier.assertFilePresent("target/site/phpunit/classes_MyClass.php.html");
        
        // clover xml
        verifier.assertFilePresent("target/phpunit-reports/clover.xml");
    }
    
    /**
     * tests the goal "site" with a project containing all default reports.
     *
     * @throws Exception 
     */
    public void testSite() throws Exception {
        final Verifier verifier = this.getPhpMavenVerifier("mojos-sites/site-all");
        
        // delete the pom from previous runs
        verifier.deleteArtifact("org.phpmaven.test", "site-all", "0.0.1", "pom");
        verifier.deleteArtifact("org.phpmaven.test", "site-all", "0.0.1", "phar");
        verifier.setAutoclean(true);

        final List<String> goals = new ArrayList<String>();
        goals.add("compile");
        goals.add("test-compile");
        goals.add("site");
        verifier.addCliOption("-X");
        verifier.executeGoals(goals);
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();
        
        // phpdocumentor report
        verifier.assertFilePresent("target/site/apidocs/phpdocumentor.html");
        verifier.assertFilePresent("target/site/apidocs/phpdocumentor/index.html");
        verifier.assertFilePresent("target/site/apidocs/phpdocumentor/packages.html");
        verifier.assertFilePresent("target/site/apidocs/phpdocumentor/default/_MyClass.php.html");
        
        // phpunit-coverage report
        verifier.assertFilePresent("target/site/phpunit/coverage.html");
        verifier.assertFilePresent("target/site/phpunit/report.html");
        verifier.assertFilePresent("target/site/phpunit/index.html");
        verifier.assertFilePresent("target/site/phpunit/classes.html");
        verifier.assertFilePresent("target/site/phpunit/classes_MyClass.php.html");
        
        // test report
        verifier.assertFilePresent("target/site/phpunit/report.html");
    }
    
    /**
     * tests the goal "site" with a project containing all default reports.
     *
     * @throws Exception 
     */
    public void testSitePhpdocPear() throws Exception {
        final Verifier verifier = this.getPhpMavenVerifier("mojos-sites/site-phpdoc-pear");
        
        // delete the pom from previous runs
        verifier.deleteArtifact("org.phpmaven.test", "site-all", "0.0.1", "pom");
        verifier.deleteArtifact("org.phpmaven.test", "site-all", "0.0.1", "phar");
        verifier.setAutoclean(true);

        final List<String> goals = new ArrayList<String>();
        goals.add("compile");
        goals.add("test-compile");
        goals.add("site");
        verifier.addCliOption("-X");
        verifier.executeGoals(goals);
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();
        
        // phpdocumentor report
        verifier.assertFilePresent("target/site/apidocs/phpdocumentor.html");
        verifier.assertFilePresent("target/site/apidocs/phpdocumentor/index.html");
        verifier.assertFilePresent("target/site/apidocs/phpdocumentor/packages.html");
        verifier.assertFilePresent("target/site/apidocs/phpdocumentor/default/_MyClass.php.html");
    }
    
    /**
     * tests the goal "site" with a project containing all default reports.
     *
     * @throws Exception 
     */
    public void testSitePhpdocPearDeprecated() throws Exception {
        final Verifier verifier = this.getPhpMavenVerifier("mojos-sites/site-phpdoc-dep");
        
        // delete the pom from previous runs
        verifier.deleteArtifact("org.phpmaven.test", "site-all", "0.0.1", "pom");
        verifier.deleteArtifact("org.phpmaven.test", "site-all", "0.0.1", "phar");
        verifier.setAutoclean(true);

        final List<String> goals = new ArrayList<String>();
        goals.add("site");
        verifier.addCliOption("-X");
        verifier.executeGoals(goals);
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();
        
        // phpdocumentor report
        verifier.assertFilePresent("target/site/apidocs/phpdocumentor.html");
        verifier.assertFilePresent("target/site/apidocs/phpdocumentor/index.html");
        verifier.assertFilePresent("target/site/apidocs/phpdocumentor/packages.html");
        verifier.assertFilePresent("target/site/apidocs/phpdocumentor/default/_MyClass.php.html");
        
        @SuppressWarnings("unchecked")
        final List<String> lines = verifier.loadFile(verifier.getBasedir(), verifier.getLogFileName(), false);
        boolean found = false;
        for (final String line : lines) {
            if (line.startsWith("[DEBUG] php.out: Deprecated: Function split()")) {
                found = true;
                break;
            }
        }
        
        assertTrue(found);
    }
    
    /**
     * tests the goal "site" with a project containing all default reports.
     *
     * @throws Exception 
     */
    public void testSitePhpdocPearIgnoreDeprecated() throws Exception {
        final Verifier verifier = this.getPhpMavenVerifier("mojos-sites/site-phpdoc-ndep");
        
        // delete the pom from previous runs
        verifier.deleteArtifact("org.phpmaven.test", "site-all", "0.0.1", "pom");
        verifier.deleteArtifact("org.phpmaven.test", "site-all", "0.0.1", "phar");
        verifier.setAutoclean(true);

        final List<String> goals = new ArrayList<String>();
        goals.add("compile");
        goals.add("test-compile");
        goals.add("site");
        verifier.addCliOption("-X");
        verifier.executeGoals(goals);
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();
        
        // phpdocumentor report
        verifier.assertFilePresent("target/site/apidocs/phpdocumentor.html");
        verifier.assertFilePresent("target/site/apidocs/phpdocumentor/index.html");
        verifier.assertFilePresent("target/site/apidocs/phpdocumentor/packages.html");
        verifier.assertFilePresent("target/site/apidocs/phpdocumentor/default/_MyClass.php.html");
        
        @SuppressWarnings("unchecked")
        final List<String> lines = verifier.loadFile(verifier.getBasedir(), verifier.getLogFileName(), false);
        boolean found = false;
        for (final String line : lines) {
            if (line.startsWith("[DEBUG] php.out: Deprecated: Function split()")) {
                found = true;
                break;
            }
        }
        
        assertFalse(found);
    }
    
    /**
     * tests the goal "site" with a project containing all default reports.
     *
     * @throws Exception 
     */
    public void testSitePhpdocPearIgnoreDeprecated2() throws Exception {
        final Verifier verifier = this.getPhpMavenVerifier("mojos-sites/site-phpdoc-ndep2");
        
        // delete the pom from previous runs
        verifier.deleteArtifact("org.phpmaven.test", "site-all", "0.0.1", "pom");
        verifier.deleteArtifact("org.phpmaven.test", "site-all", "0.0.1", "phar");
        verifier.setAutoclean(true);

        final List<String> goals = new ArrayList<String>();
        goals.add("compile");
        goals.add("test-compile");
        goals.add("site");
        verifier.addCliOption("-X");
        verifier.executeGoals(goals);
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();
        
        // phpdocumentor report
        verifier.assertFilePresent("target/site/apidocs/phpdocumentor.html");
        verifier.assertFilePresent("target/site/apidocs/phpdocumentor/index.html");
        verifier.assertFilePresent("target/site/apidocs/phpdocumentor/packages.html");
        verifier.assertFilePresent("target/site/apidocs/phpdocumentor/default/_MyClass.php.html");
        
        @SuppressWarnings("unchecked")
        final List<String> lines = verifier.loadFile(verifier.getBasedir(), verifier.getLogFileName(), false);
        boolean found = false;
        for (final String line : lines) {
            if (line.startsWith("[DEBUG] php.out: Deprecated: Function split()")) {
                found = true;
                break;
            }
        }
        
        assertFalse(found);
    }
    
    // alpha support dropped.
    
//    /**
//     * tests the goal "site" with a project containing all default reports.
//     *
//     * @throws Exception 
//     */
//    public void testSitePhpdoc2Alpha() throws Exception {
//        final Verifier verifier = this.getPhpMavenVerifier("mojos-sites/site-phpdoc2-alpha");
//        
//        // delete the pom from previous runs
//        verifier.deleteArtifact("org.phpmaven.test", "site-all", "0.0.1", "pom");
//        verifier.deleteArtifact("org.phpmaven.test", "site-all", "0.0.1", "phar");
//        verifier.setAutoclean(true);
//
//        final List<String> goals = new ArrayList<String>();
//        goals.add("compile");
//        goals.add("test-compile");
//        goals.add("site");
//        verifier.addCliOption("-X");
//        verifier.executeGoals(goals);
//        verifier.verifyErrorFreeLog();
//        verifier.resetStreams();
//        
//        // phpdocumentor report
//        verifier.assertFilePresent("target/site/apidocs/phpdocumentor.html");
//        verifier.assertFilePresent("target/site/apidocs/phpdocumentor/index.html");
//        verifier.assertFilePresent("target/site/apidocs/phpdocumentor/graph_class.html");
//        verifier.assertFilePresent("target/site/apidocs/phpdocumentor/packages/db_Default.html");
//    }
//    
//    /**
//     * tests the goal "site" with a project containing all default reports.
//     *
//     * @throws Exception 
//     */
//    public void testSitePhpdoc2Alpha2() throws Exception {
//        final Verifier verifier = this.getPhpMavenVerifier("mojos-sites/site-phpdoc2-alpha2");
//        
//        // delete the pom from previous runs
//        verifier.deleteArtifact("org.phpmaven.test", "site-all", "0.0.1", "pom");
//        verifier.deleteArtifact("org.phpmaven.test", "site-all", "0.0.1", "phar");
//        verifier.setAutoclean(true);
//
//        final List<String> goals = new ArrayList<String>();
//        goals.add("compile");
//        goals.add("test-compile");
//        goals.add("site");
//        verifier.addCliOption("-X");
//        verifier.executeGoals(goals);
//        verifier.verifyErrorFreeLog();
//        verifier.resetStreams();
//        
//        // phpdocumentor report
//        verifier.assertFilePresent("target/site/apidocs/phpdocumentor.html");
//        verifier.assertFilePresent("target/site/apidocs/phpdocumentor/index.html");
//        verifier.assertFilePresent("target/site/apidocs/phpdocumentor/graph_class.html");
//        verifier.assertFilePresent("target/site/apidocs/phpdocumentor/packages/Default.html");
//        verifier.assertFilePresent("target/site/apidocs/phpdocumentor/classes/MyMavenTestClass.html");
//    }

}