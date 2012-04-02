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

package org.phpmaven.phpdoc.impl;

import java.io.File;
import java.io.IOException;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Configuration;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.phpmaven.core.BuildPluginConfiguration;
import org.phpmaven.core.ConfigurationParameter;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.exec.IPhpExecutable;
import org.phpmaven.exec.IPhpExecutableConfiguration;
import org.phpmaven.exec.PhpCoreException;
import org.phpmaven.exec.PhpErrorException;
import org.phpmaven.exec.PhpException;
import org.phpmaven.exec.PhpWarningException;
import org.phpmaven.pear.IPackage;
import org.phpmaven.pear.IPackageVersion;
import org.phpmaven.pear.IPearConfiguration;
import org.phpmaven.pear.IPearUtility;
import org.phpmaven.phpdoc.IPhpdocRequest;
import org.phpmaven.phpdoc.IPhpdocSupport;

/**
 * Implementation of phpdoc support invoking the phpdoc via php exe and loaded from repository or dependency.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
@Component(role = IPhpdocSupport.class, instantiationStrategy = "per-lookup", hint = "PEAR")
@BuildPluginConfiguration(groupId = "org.phpmaven", artifactId = "maven-php-phpdoc", filter = {
        "phpdocService", "installPhpdoc", "installFolder", "phpDocFilePath"
        })
public class PhpdocPearSupport extends AbstractPhpdocSupport implements IPhpdocSupport {

    /**
     * The phpdoc configuraton file. The default is ${project.basedir}/src/site/phpdoc/phpdoc.config
     */
    @ConfigurationParameter(name = "phpDocConfigFile", expression = "${project.basedir}/src/site/phpdoc/phpdoc.config")
    private File phpDocConfigFile;

    /**
     * The generated phpDoc file.
     */
    @ConfigurationParameter(
            name = "generatedPhpDocConfigFile",
            expression = "${project.build.directory}/temp/phpdoc/phpdoc.ini")
    private File generatedPhpDocConfigFile;
    
    /**
     * The executable config.
     */
    @Configuration(name = "executableConfig", value = "")
    private Xpp3Dom executableConfig;
    
    /**
     * The component factory.
     */
    @Requirement
    private IComponentFactory factory;
    
    /**
     * The maven session.
     */
    @ConfigurationParameter(name = "session", expression = "${session}")
    private MavenSession session;
    
    /**
     * The phpdoc version to be used.
     */
    @Configuration(name = "phpdocVersion", value = "1.4.2")
    private String phpdocVersion;
    
    /**
     * The additional arguments passed to phpdoc.
     */
    @Configuration(name = "arguments", value = "")
    private String arguments;

    /**
     * {@inheritDoc}
     */
    @Override
    public void generateReport(Log log, IPhpdocRequest request) throws PhpException {
        try {
            final IPearUtility util = this.factory.lookup(
                    IPearConfiguration.class,
                    this.executableConfig,
                    this.session).getUtility(log);

            if (!util.isInstalled()) {
                util.installPear(false);
            }
            IPackage pkg = null;
            if (this.phpdocVersion.startsWith("1.")) {
                writeIni(log, request, phpDocConfigFile, generatedPhpDocConfigFile);
                pkg = util.channelDiscover("pear.php.net").getPackage("PhpDocumentor");
            } else {
                writeXml(log, request, phpDocConfigFile, generatedPhpDocConfigFile);
                pkg = util.channelDiscover("pear.phpdoc.org").getPackage("PhpDocumentor");
            }
            
            final IPackageVersion version = pkg.getVersion(this.phpdocVersion);
            if (pkg.getInstalledVersion() == null) {
                version.install();
            } else if (!pkg.getInstalledVersion().getVersion().getPearVersion().equals(
                    version.getVersion().getPearVersion())) {
                version.install();
            }
            
            final File phpDocFile = new File(util.getBinDir(), "phpdoc");
            if (phpDocFile == null || !phpDocFile.isFile()) {
                throw new PhpCoreException("phpdoc not found in path");
            }
            String command = "\"" + phpDocFile + "\" -c \"" + generatedPhpDocConfigFile.getAbsolutePath() + "\"";
            if (arguments != null && arguments.length() > 0) {
                command += " " + arguments;
            }
            log.debug("Executing PHPDocumentor: " + command);
            // XXX: commandLine.setWorkingDirectory(phpDocFile.getParent());
            String result;
            final IPhpExecutableConfiguration config = this.factory.lookup(
                    IPhpExecutableConfiguration.class,
                    this.executableConfig,
                    this.session);
            config.getIncludePath().add(util.getPhpDir().getAbsolutePath());
            final IPhpExecutable exec = config.getPhpExecutable(log);
            
            try {
                result = exec.execute(command, phpDocFile);
            } catch (PhpWarningException ex) {
                result = ex.getAppendedOutput();
                // silently ignore; only errors are important
            }
            for (final String line : result.split("\n")) {
                if (line.startsWith("ERROR:")) {
                    // this is a error of phpdocumentor.
                    log.error("Got error from php-documentor. " +
                        "Enable debug (-X) to fetch the php output.\n" +
                        line);
                    throw new PhpErrorException(phpDocFile, line);
                }
            }
        } catch (PlexusConfigurationException ex) {
            throw new PhpCoreException("Errors invoking phpdoc", ex);
        } catch (IOException ex) {
            throw new PhpCoreException("Errors invoking phpdoc", ex);
        } catch (ComponentLookupException ex) {
            throw new PhpCoreException("Errors invoking phpdoc", ex);
        }
    }

}
