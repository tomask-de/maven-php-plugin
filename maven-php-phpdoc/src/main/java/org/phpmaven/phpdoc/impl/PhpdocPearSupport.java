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
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.phpmaven.core.BuildPluginConfiguration;
import org.phpmaven.core.ConfigurationParameter;
import org.phpmaven.core.ExecutionUtils;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.exec.IPhpExecutableConfiguration;
import org.phpmaven.pear.IMavenPearUtility;
import org.phpmaven.pear.IPearConfiguration;
import org.phpmaven.phpdoc.IPhpdocRequest;
import org.phpmaven.phpdoc.IPhpdocSupport;
import org.phpmaven.phpexec.library.IPhpExecutable;
import org.phpmaven.phpexec.library.PhpCoreException;
import org.phpmaven.phpexec.library.PhpErrorException;
import org.phpmaven.phpexec.library.PhpException;
import org.phpmaven.phpexec.library.PhpWarningException;

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
     * phpdoc hack script to prevent error_reporting override
     */
    private static final String PHPDOC_INC =
            "\n" +
            "/**\n" +
            " * startup file\n" +
            " * \n" +
            " * phpDocumentor :: automatic documentation generator\n" +
            " * \n" +
            " * PHP versions 4 and 5\n" +
            " *\n" +
            " * Copyright (c) 2000-2007 Joshua Eichorn, Gregory Beaver\n" +
            " * \n" +
            " * LICENSE:\n" +
            " * \n" +
            " * This library is free software; you can redistribute it\n" +
            " * and/or modify it under the terms of the GNU Lesser General\n" +
            " * Public License as published by the Free Software Foundation;\n" +
            " * either version 2.1 of the License, or (at your option) any\n" +
            " * later version.\n" +
            " * \n" +
            " * This library is distributed in the hope that it will be useful,\n" +
            " * but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
            " * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU\n" +
            " * Lesser General Public License for more details.\n" +
            " * \n" +
            " * You should have received a copy of the GNU Lesser General Public\n" +
            " * License along with this library; if not, write to the Free Software\n" +
            " * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA\n" +
            " *\n" +
            " * @category  ToolsAndUtilities\n" +
            " * @package   phpDocumentor\n" +
            " * @author    Joshua Eichorn <jeichorn@phpdoc.org>\n" +
            " * @author    Gregory Beaver <cellog@php.net>\n" +
            " * @copyright 2000-2007 Joshua Eichorn, Gregory Beaver\n" +
            " * @license   http://www.opensource.org/licenses/lgpl-license.php LGPL\n" +
            " * @version   CVS: $Id: phpdoc.inc,v 1.4 2007/10/10 01:18:25 ashnazg Exp $\n" +
            " * @link      http://www.phpdoc.org\n" +
            " * @link      http://pear.php.net/PhpDocumentor\n" +
            " * @since     0.1\n" +
            " * @filesource\n" +
            " * @todo      CS cleanup - change package to PhpDocumentor\n" +
            " */\n" +
            "\n" +
            "\n" +
            "/**\n" +
            " * All command-line handling from previous version has moved to here\n" +
            " *\n" +
            " * Many settings also moved to phpDocumentor.ini\n" +
            " */\n" +
            "$old = error_reporting();\n" +
            "require_once \"phpDocumentor/Setup.inc.php\";\n" +
            "error_reporting($old);\n" +
            "\n" +
            "$phpdoc = new phpDocumentor_setup;\n" +
            "$phpdoc->readCommandLineSettings();\n" +
            "$phpdoc->setupConverters();\n" +
            "$phpdoc->createDocs();\n" +
            "\n" +
            "";

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
            final Xpp3Dom dom = new Xpp3Dom("configuration");
            if (this.executableConfig != null) {
//                final Xpp3Dom configNode = new Xpp3Dom("executableConfig");
//                configNode.addChild(executableConfig);
                dom.addChild(this.executableConfig);
            }
            final IMavenPearUtility util = this.factory.lookup(
                    IPearConfiguration.class,
                    dom,
                    this.session).getUtility(log);

            if (!util.isInstalled()) {
                util.installPear(false);
            }
            // do not try to read remote channels; so that we will work in offline mode etc.
            util.initChannels(false);
            
            boolean disableDeprecatedWarning = false;

            if (this.phpdocVersion.startsWith("1.")) {
                writeIni(log, request, phpDocConfigFile, generatedPhpDocConfigFile);
                util.installFromMavenRepository("net.php", "PhpDocumentor", this.phpdocVersion);
                disableDeprecatedWarning = true;
            } else {
                writeXml(log, request, phpDocConfigFile, generatedPhpDocConfigFile);
                // there is a very strange dependency mismatching in phpdoc.
                // an unknown version 0.17.0 is used as dependency for various things.
                // however it does not really work; maybe we need an empty dummy package.
                util.installFromMavenRepository("org.phpdoc", "phpDocumentor", "0.17.0");
                
                util.installFromMavenRepository("org.phpdoc", "phpDocumentor", this.phpdocVersion);
            }
            
            String phpDoc = ExecutionUtils.searchExecutable(log,
                    "phpdoc.php",
                    util.getBinDir().getAbsolutePath());
            if (phpDoc == null) {
                phpDoc = ExecutionUtils.searchExecutable(log,
                    "phpdoc",
                    util.getBinDir().getAbsolutePath(),
                    false);
                if (phpDoc == null) {
                    throw new PhpCoreException("phpdoc not found in path (" + util.getBinDir() + ")");
                }
            }
            String command = "-c \"" + generatedPhpDocConfigFile.getAbsolutePath() + "\"";
            if (arguments != null && arguments.length() > 0) {
                command += " " + arguments;
            }
            log.debug("Executing PHPDocumentor with args: " + command);
            // XXX: commandLine.setWorkingDirectory(phpDocFile.getParent());
            String result;
            
            try {
                if (!phpDoc.endsWith(".bat")) {
                    final IPhpExecutableConfiguration config = this.factory.lookup(
                            IPhpExecutableConfiguration.class,
                            this.executableConfig,
                            this.session);
                    
                    // phpdoc overwrites error_reporting. we need to hack if we plan to overwrite it by ourselves.
                    final String newErrorReporting = config.getNumErrorReporting() == -1 ? (
                            disableDeprecatedWarning ? "E_ALL & !E_DEPRECATED" : null
                            ) : String.valueOf(config.getNumErrorReporting());
                    
                    // try to find phpdoc.inc
                    final File phpDocInc = new File(util.getPhpDir(), "PhpDocumentor/phpDocumentor/phpdoc.inc");
                    
                    if (newErrorReporting != null) {
                        log.debug("setting error reporting to " + newErrorReporting);
                        log.debug("using phpdoc.inc at " + phpDocInc);
                        config.setErrorReporting(newErrorReporting);
                    }

                    config.getIncludePath().add(util.getPhpDir().getAbsolutePath());
                    if (newErrorReporting == null || !phpDocInc.exists()) {
                        // direct execution
                        final IPhpExecutable exec = config.getPhpExecutable();
                        result = exec.execute("\"" + phpDoc + "\" " + command, new File(phpDoc));
                    } else {
                        // try to hack the deprecated warning
                        config.getIncludePath().add(phpDocInc.getParentFile().getParentFile().getAbsolutePath());
                        final IPhpExecutable exec = config.getPhpExecutable();
                        result = exec.executeCode("", PHPDOC_INC, command);
                    }
                } else {
                    final Commandline commandLine = new Commandline("\"" + phpDoc + "\" " + command);
                    final StringBuilder stdout = new StringBuilder();
                    final StringBuilder stderr = new StringBuilder();
                    CommandLineUtils.executeCommandLine(commandLine, new StreamConsumer() {
                        @Override
                        public void consumeLine(String line) {
                            stdout.append(line);
                            stdout.append("\n");
                        }
                    }, new StreamConsumer() {
                        @Override
                        public void consumeLine(String line) {
                            stderr.append(line);
                            stderr.append("\n");
                        }
                    });
                    result = stdout.toString();
                    log.debug("phpdoc output:\n" + result);
                }
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
                    throw new PhpErrorException(new File(phpDoc), line);
                }
            }
        } catch (PlexusConfigurationException ex) {
            throw new PhpCoreException("Errors invoking phpdoc", ex);
        } catch (IOException ex) {
            throw new PhpCoreException("Errors invoking phpdoc", ex);
        } catch (ComponentLookupException ex) {
            throw new PhpCoreException("Errors invoking phpdoc", ex);
        } catch (CommandLineException ex) {
            throw new PhpCoreException("Errors invoking phpdoc", ex);
        }
    }

}
