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

package org.phpmaven.phar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import org.phpmaven.exec.PhpException;
import org.phpmaven.phar.PharEntry.EntryType;

/**
 * Phar packager implementation to use php-exe.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
@Component(role = IPharPackager.class, instantiationStrategy = "per-lookup")
@BuildPluginConfiguration(groupId = "org.phpmaven", artifactId = "php-maven-phar", filter = {
        "packager", "pharConfig" })
public class PharPackager implements IPharPackager {

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
     * The executable config.
     */
    @Configuration(name = "executableConfig", value = "")
    private Xpp3Dom executableConfig;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void packagePhar(IPharPackagingRequest request, Log log)
        throws PhpException, ComponentLookupException, PlexusConfigurationException {
        final IPhpExecutableConfiguration execConfig = getExecConfig();
        execConfig.getPhpDefines().put("phar.readonly", "0");
        final IPhpExecutable exec = execConfig.getPhpExecutable(log);
        
        final StringBuilder contents = new StringBuilder();
        for (final PharEntry entry : request.getEntries()) {
            if (entry.getType() == EntryType.DIRECTORY) {
                final PharDirectory dir = (PharDirectory) entry;
                final String maskedRelativePath = dir.getRelativePath().replace("\\", "\\\\");
                final String maskedToPackPath = dir.getPathToPack().getAbsolutePath().replace("\\", "\\\\");
                
                contents.append(
                        request.getPackagePhpDirectoryTemplate().
                        replace("$:{pkgdir}", maskedRelativePath).
                        replace("$:{pkgbasedir}", maskedToPackPath));  
            } else {
                final PharFile file = (PharFile) entry;
                final String fileMasked = file.getFile().getAbsolutePath().replace("\\", "\\\\");
                final String fileLocalNameMasked = file.getLocalName().replace("\\", "\\\\");
                contents.append(
                        request.getPackagePhpFileTemplate().
                        replace("$:{filename}", fileLocalNameMasked).
                        replace("$:{filebasepath}", fileMasked));    
            }
        }

        final String targetMasked = request.getTargetDirectory().getAbsolutePath().replace("\\", "\\\\");
        final String stubToUse = request.getStub().replace("\\", "\\\\").replace("'", "\\'");
        
        String compression = "";
        if (request.isCompressed()) {
            if (request.isLargePhar()) {
                compression = "foreach ($phar as $file) { $file->compress(Phar::GZ); }\n";
            } else {
                compression = "$phar->compressFiles(Phar::GZ);\n";
            }
        }

        final String snippet = request.getPackagePhpTemplate().
                replace("$:{pharfilepath}", targetMasked).
                replace("$:{pharfilename}", request.getFilename()).
                replace("$:{pharcontents}", contents.toString()).
                // TODO: May we need to set a compression template????
                replace("$:{pharcompression}", compression).
                replace("$:{pharstub}", "<?php " + stubToUse + " __HALT_COMPILER(); ?>");

        // XXX: respect build directory (set working path)
        exec.executeCode("", snippet);
    }

    private IPhpExecutableConfiguration getExecConfig()
        throws ComponentLookupException, PlexusConfigurationException {
        /*Xpp3Dom executableConfig = this.factory.getBuildConfig(
                this.session.getCurrentProject(),
                "org.phpmaven",
                "maven-php-phar");
        if (executableConfig != null) {
            executableConfig = executableConfig.getChild("executableConfig");
        }*/
        
        final IPhpExecutableConfiguration execConfig = this.factory.lookup(
                IPhpExecutableConfiguration.class,
                this.executableConfig,
                this.session);
        // unset additional params for unphar
        execConfig.setAdditionalPhpParameters("");
        return execConfig;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String readStub(File pharPackage, Log log)
        throws PhpException, ComponentLookupException, PlexusConfigurationException {
        final IPhpExecutableConfiguration execConfig = getExecConfig();
        final IPhpExecutable executable = execConfig.getPhpExecutable(log);
        
        return executable.executeCode("",
                "$phar = new Phar('" + pharPackage.getAbsolutePath().replace("\\", "\\\\") + "');\n" +
                "echo $phar->getStub();");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void extractPharTo(File pharPackage, File targetDirectory, Log log)
        throws PhpException, ComponentLookupException, PlexusConfigurationException {
        final IPhpExecutableConfiguration execConfig = getExecConfig();
        final IPhpExecutable executable = execConfig.getPhpExecutable(log);
        
        // XXX: There seems to be a bug on some php versions. https://bugs.php.net/bug.php?id=50797
        // However extractTo should be used for performance. Manually extracting is slow.
        
        // TODO: Make configurable via build config
        
        final String pharFileName = pharPackage.getAbsolutePath().replace("\\", "\\\\");
        final String targetName = targetDirectory.getAbsolutePath().replace("\\", "\\\\");
        
        executable.executeCode("",
            /*"$phar = new Phar('$:{pharFile}');\n" +
            "// $phar->extractTo('$:{targetDir}', null, true);\n" +
            "foreach ($phar as $file) {\n" +
            "  $contents = file_get_contents($file->getPathName());\n" +
            "  $relPath = substr($file->getPathName(), strlen('phar://$:{pharFile}'));\n" +
            "  $fullPath = '$:{targetDir}'.$relPath;\n" +
            "  $parentDir = dirname($fullPath);\n" +
            "  if (!is_dir($parentDir)) mkdirs($parentDir);\n" +
            "  file_put_contents($fullPath, $contents);\n" +
            "}\n";*/
            "$iter = new RecursiveIteratorIterator(new RecursiveDirectoryIterator(" +
            "'phar://" + pharFileName + "'), RecursiveIteratorIterator::CHILD_FIRST);\n" +
            "if (!function_exists('mkdirs')) {\n" +
            "  function mkdirs($arg) {\n" +
            "    if (strlen($arg) <= 2) return;\n" +
            "    if (!is_dir($arg)) {\n" +
            "      mkdirs(dirname($arg));\n" +
            "      mkdir($arg);\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "foreach ($iter as $file) {\n" +
            "  $relPath = substr($file->getPathName(), strlen('phar://" + pharFileName + "'));\n" +
            "  $fullPath = '" + targetName + "'.$relPath;\n" +
            "  if ($iter->isDir()) {\n" +
            "    mkdirs($fullPath);\n" +
            "  }\n" +
            "  else {\n" +
            "    mkdirs(dirname($fullPath));\n" +
            "    $contents = file_get_contents($file->getPathName());\n" +
            "    file_put_contents($fullPath, $contents);\n" +
            "  }\n" +
            "}\n");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<String> listFiles(File pharPackage, Log log)
        throws PhpException, ComponentLookupException, PlexusConfigurationException {
        final IPhpExecutableConfiguration execConfig = getExecConfig();
        final IPhpExecutable executable = execConfig.getPhpExecutable(log);
            
        final String pharFileName = pharPackage.getAbsolutePath().replace("\\", "\\\\");
        final String files = executable.executeCode("",
            "$iter = new RecursiveIteratorIterator(new RecursiveDirectoryIterator(" +
            "'phar://" + pharFileName + "'));\n" +
            "foreach ($iter as $file) {\n" +
            "  if (!$iter->isDir()) {\n" +
            "    echo substr($file->getPathName(), strlen('phar://" + pharFileName + "'));\n" +
            "    echo \"\\n\";\n" +
            "  }\n" +
            "}\n");
        final List<String> result = new ArrayList<String>();
        for (final String line : files.split("\n")) {
            result.add(line);
        }
        return result;
    }

}
