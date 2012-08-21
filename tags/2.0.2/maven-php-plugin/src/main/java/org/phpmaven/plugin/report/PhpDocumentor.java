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
import java.util.Locale;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.reporting.MavenReportException;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.phpdoc.IPhpdocConfiguration;
import org.phpmaven.phpdoc.IPhpdocRequest;
import org.phpmaven.phpdoc.IPhpdocSupport;

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
     * The output directory of phpdocumentor generated documentation.
     *
     * @parameter expression="${project.build.directory}/site/apidocs"
     * @required
     */
    private File outputApiDocDirectory;
    
    /**
     * The Maven session.
     *
     * @parameter expression="${session}"
     * @readonly
     * @required
     */
    private MavenSession session;
    
    /**
     * The configuration factory.
     * @component
     * @required
     */
    protected IComponentFactory factory;

    protected File getApiDocOutputDirectory() {
        return outputApiDocDirectory;
    }

    private void writeReport() {
        if (getSink() != null)  {
            getSink().rawText(
                "<a href=\"phpdocumentor/index.html\" target=\"_blank\">" +
                    "Show documention<br>" +
                    "<iframe src=\"phpdocumentor/index.html\"" +
                    "frameborder=0 style=\"border=0px;width:100%;height:800px\">");
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
        try {
            final IPhpdocConfiguration config = this.factory.lookup(
                    IPhpdocConfiguration.class, IComponentFactory.EMPTY_CONFIG, this.session);
            final IPhpdocSupport support = config.getPhpdocSupport();
            
            final IPhpdocRequest request = this.factory.lookup(IPhpdocRequest.class, IComponentFactory.EMPTY_CONFIG, this.session);
            request.addFolder(getSourceDirectory());
            request.setReportFolder(new File(getApiDocOutputDirectory().
                  getAbsoluteFile().getPath() + "/" + getFolderName()));
            support.generateReport(getLog(), request);
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
    
    protected String getFolderName() {
        return "phpdocumentor";
    }

    @Override
    protected String getOutputDirectory() {
        return getApiDocOutputDirectory().getAbsolutePath();
    }

}
