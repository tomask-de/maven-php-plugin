/**
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

package org.phpmaven.plugin.report;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.maven.reporting.MavenReportException;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.phpunit.IPhpunitConfiguration;
import org.phpmaven.phpunit.IPhpunitSupport;
import org.phpmaven.phpunit.IPhpunitTestRequest;
import org.phpmaven.phpunit.IPhpunitTestResult;
import org.phpmaven.plugin.php.IPhpunitConfigurationMojo;
import org.phpmaven.plugin.php.PhpUnitTestfileWalker;

/**
 * A maven 2.0 plugin for generating phpunit reports. This plugin is
 * used in the <code>site</code> phase.
 *
 * @goal phpunit
 * @phase site
 * @execute phase="test-compile"
 * @author Martin Eisengardt
 */
public class PhpUnit extends AbstractPhpUnitReportMojo implements IPhpunitConfigurationMojo {

    /**
     * The output directory of phpunit report.
     *
     * @parameter expression="${project.build.directory}/site/phpunit"
     * @required
     */
    private File outputPhpunitDirectory;

    /**
     * Where the test results should be stored.
     *
     * Default: target/phpunit-coverage-reports
     *
     * @parameter default-value="${project.build.directory}/phpunit-site-reports" expression="${resultFolder}"
     */
    private File resultFolder;

    // properties for IPhpunitConfigurationMojo

    /**
     * Which postfix will be used to find test-cases. The default is "Test" and
     * all php files, ending with Test will be treated as test case files.
     * E.g. Logic1Test.php will be used.
     *
     * Default: Test
     *
     * @parameter default-value="Test" expression="${testPostfix}"
     */
    private String testPostfix;
    
    /**
     * Set this to "true" to skip running tests, but still compile them. Its use is NOT RECOMMENDED, but quite
     * convenient on occasion.
     * 
     * @parameter default-value="false" expression="${skipTests}"
     */
    private boolean skipTests;
    
    /**
     * Set this to "true" to bypass unit tests entirely. Its use is NOT RECOMMENDED, especially if you enable it using
     * the "maven.test.skip" property, because maven.test.skip disables both running the tests and compiling the tests.
     * Consider using the <code>skipTests</code> parameter instead.
     * 
     * @parameter default-value="false" expression="${maven.test.skip}"
     */
    private boolean skip;
    
    /**
     * Additional command line arguments for phpunit. Can be used to parse options (for example
     * the --bootstrap file) but should never be used to set the test file or the xml output script.
     * 
     * @parameter expression="${phpUnitArguments}"
     */
    private String phpUnitArguments;
    
    // end of properties for IPhpunitConfigurationMojo
    
    // methods for IPhpunitConfigurationMojo

    /**
     * Which postfix will be used to find test-cases. The default is "Test" and
     * all php files, ending with Test will be treated as test case files.
     * E.g. Logic1Test.php will be used.
     * 
     * @return The php test case postfix.
     */
    @Override
    public String getTestPostfix() {
        return testPostfix;
    }

    /**
     * Set this to "true" to skip running tests, but still compile them. Its use is NOT RECOMMENDED, but quite
     * convenient on occasion.
     * 
     * @return true to skip the testing
     */
    @Override
    public boolean isSkipTests() {
        return this.skip || this.skipTests;
    }
    
    /**
     * Set this to "true" to cause a failure if there are no tests to run. Defaults to "false".
     * 
     * @return true to fail if there are no tests
     */
    @Override
    public boolean isFailIfNoTests() {
        return false;
    }
    
    // end of methods for IPhpunitConfigurationMojo

    private void writeReport() {
        if (getSink() != null)  {
            getSink().rawText(
                "<a href=\"phpunit/report.html\" target=\"_blank\">" +
                    "Show documention<br>" +
                    "<iframe src=\"phpunit/report.html\"" +
                    "frameborder=0 style=\"border=0px;width:100%;height:400px\">");
        }
    }
    
    @Override
    protected void executeReport(Locale locale) throws MavenReportException {
    	this.loadPluginConfig();
    	
        try {
            
            getLog().info(
                    "\n-------------------------------------------------------\n" +
                    "T E S T S - R E P O R T I N G\n" +
                    "-------------------------------------------------------\n");
            
            // test files
            final Iterable<File> files = new PhpUnitTestfileWalker(this).getTestFiles();
            
            // did we get any test file?
            if (files.iterator().hasNext()) {
                
                final IPhpunitConfiguration config = this.factory.lookup(
                        IPhpunitConfiguration.class,
                        IComponentFactory.EMPTY_CONFIG,
                        this.getSession());
                
                final IPhpunitTestRequest request = this.factory.lookup(
                        IPhpunitTestRequest.class,
                        IComponentFactory.EMPTY_CONFIG,
                        this.getSession());
                for (final File file : files) {
                    request.addTestFile(file);
                }
                final IPhpunitSupport support = config.getPhpunitSupport();
                support.setIsSingleTestInvocation(true);
                if (this.phpUnitArguments != null) {
                	support.setPhpunitArguments(this.phpUnitArguments);
                }
                final File xmlResult = new File(this.resultFolder, "phpunit.xml");
                if (xmlResult.exists()) {
                	xmlResult.delete();
                }
				support.setXmlResult(xmlResult);
                support.setResultFolder(this.resultFolder);
                // TODO generatedPhpUnitTestsuiteFile
                
                getLog().info("Starting tests.");
                
                final IPhpunitTestResult result = support.executeTests(request, getLog());
                
                if (!xmlResult.exists() || xmlResult.length() == 0) {
                	// generate a failed result.
                	if (result.getResults().iterator().hasNext()) {
                		throw result.getResults().iterator().next().getException();
                	}
                	throw new MavenReportException("fatal test failures");
                }
                
                final List<File> reportsDirectoryList = new ArrayList<File>();
                reportsDirectoryList.add(this.resultFolder);
                // TODO source xref linking
                final String sourceFolder = null;
                final boolean showSuccess = true;
                final SurefireReportGenerator report = new SurefireReportGenerator(
                        reportsDirectoryList, locale, showSuccess, sourceFolder);
                report.doGenerateReport(getBundle(locale), getSink());
                                
                getLog().info("\n\nResults :\n\n" + result.toString());
            }
            writeReport();
        /*CHECKSTYLE:OFF*/
        } catch (Exception e) {
        /*CHECKSTYLE:ON*/
            throw new MavenReportException(e.getMessage(), e);
        }
    }

    /**
	 * loads plugin config if there is no site config
	 */
	private void loadPluginConfig() {
		// TODO Auto-generated method stub
		
	}

	/**
     * Returns the locale resource bundle.
     * @param locale locale
     * @return bundle
     */
    private ResourceBundle getBundle(Locale locale) {
        return ResourceBundle.getBundle("phpunit-report", locale, this.getClass().getClassLoader());
    }

    /**
     * The name to use localized by a locale.
     *
     * @param locale the locale to localize
     * @return the name
     */
    @Override
    public String getName(Locale locale) {
        return "PHPUnit";
    }

    /**
     * Returns the description text, dependent of the locale.
     *
     * @param locale the locale to localize
     * @return the text
     */
    @Override
    public String getDescription(Locale locale) {
        return "PHPUnit test report";
    }

    @Override
    public String getOutputName() {
        return "report";
    }

    @Override
    protected String getOutputDirectory() {
        return this.outputPhpunitDirectory.getAbsolutePath();
    }

}
