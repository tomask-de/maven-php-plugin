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
import java.io.FileInputStream;
import java.util.Locale;
import java.util.Properties;

import org.apache.maven.reporting.MavenReportException;
import org.phpmaven.plugin.php.PhpErrorException;

/**
 * A maven 2.0 plugin for generating phpdocumentor documentations. This plugin is
 * used in the <code>site</code> phase.
 *
 * @goal phpdocumentor
 * @phase site
 * @author Christian Wiedemann
 * @author Tobias Sarnowski
 */
public class PhpDocumentor extends AbstractApiDocReport {

    /**
     * Path to phpDoc. If nothing is configured phpdoc is expected in the path.
     *
     * @parameter default-value="phpdoc" expression="${phpDocFilePath}"
     */
    private String phpDocFilePath = "phpdoc";

    /**
     * The phpdoc configuraton file. The default is ${project.basedir}/src/site/phpdoc/phpdoc.config
     *
     * @parameter default-value="${project.basedir}/src/site/phpdoc/phpdoc.config" expression="${phpDocConfigFile}"
     * @required
     */
    private File phpDocConfigFile;

    /**
     * The generated phpDoc file.
     *
     * @parameter expression="${project.build.directory}/temp/phpdoc/phpdoc.ini";
     * @required
     * @readonly
     */
    private File generatedPhpDocConfigFile;
    
    // XXX: Load phpDocumentor via maven dependencies
    // XXX: Configuration option to specifiy the phpDocumentor to be used

    private void writeReport() {
        if (getSink() != null)  {
            getSink().rawText(
                "<a href=\"phpdocumentor/index.html\" target=\"_blank\">" +
                    "Show documention<br>" +
                    "<iframe src=\"phpdocumentor/index.html\"" +
                    "frameborder=0 style=\"border=0px;width:100%;height:400px\">");
        }
    }

    /**
     * Where the PHP source files can be found.
     *
     * @return where the php sources can be found
     */
    public File getSourceDirectory() {
        return new File(this.getProject().getCompileSourceRoots().get(0).toString());
    }
    
    @Override
    protected void executeReport(Locale locale) throws MavenReportException {
        final Properties properties = new Properties();
        try {
            if (phpDocConfigFile.isFile()) {
                getLog().debug("generating phpdoc using config from " + phpDocConfigFile.getAbsolutePath());
                properties.load(new FileInputStream(phpDocConfigFile));
            } else {
                getLog().debug("config file " + phpDocConfigFile.getAbsolutePath() + " not found. ignoring.");
            }
            properties.put("directory", getSourceDirectory().getAbsolutePath());
            properties.put("target", getApiDocOutputDirectory().
                getAbsoluteFile().getPath() + "/" + getFolderName());

            writePropFile(properties, generatedPhpDocConfigFile, "[Parse Data]");
            final String path = System.getProperty("java.library.path");
            getLog().debug("PATH: " + path);
            final String[] paths = path.split(File.pathSeparator);
            File phpDocFile = null;
            if ("phpdoc".equals(phpDocFilePath)) {
                for (int i = 0; i < paths.length; i++) {
                    final File file = new File(paths[i], "phpdoc");
                    if (file.isFile()) {
                        phpDocFile = file;
                        break;
                    }
                }
            } else {
                phpDocFile = new File(phpDocFilePath);
            }
            if (phpDocFile == null || !phpDocFile.isFile()) {
                throw new PhpDocumentorNotFoundException();
            }
            final String command = "\"" + phpDocFile + "\" -c \"" + generatedPhpDocConfigFile.getAbsolutePath() + "\"";
            getLog().debug("Executing PHPDocumentor: " + command);
            // XXX: commandLine.setWorkingDirectory(phpDocFile.getParent());
            final String result = this.getPhpHelper().execute(command, phpDocFile);
            for (final String line : result.split("\n")) {
                if (line.startsWith("ERROR:")) {
                    // this is a error of phpdocumentor.
                    getLog().error("Got error from php-documentor. " +
                        "Enable debug (-X) to fetch the php output.\n" +
                        line);
                    throw new PhpErrorException(phpDocFile, line);
                }
            }
            writeReport();
        /*CHECKSTYLE:OFF*/
        } catch (Exception e) {
        /*CHECKSTYLE:ON*/
            throw new MavenReportException(e.getMessage(), e);
        }
    }

    /**
     * The name to use localized by a locale.
     *
     * @param locale the locale to localize
     * @return the name
     */
    @Override
    public String getName(Locale locale) {
        return "PHPDocumentor";
    }

    /**
     * Returns the description text, dependent of the locale.
     *
     * @param locale the locale to localize
     * @return the text
     */
    @Override
    public String getDescription(Locale locale) {
        return "PHPDocumentor generated documentation";
    }

    @Override
    public String getOutputName() {
        return "apidocs/phpdocumentor";
    }
    @Override
    protected String getFolderName() {
        return "phpdocumentor";
    }

}
