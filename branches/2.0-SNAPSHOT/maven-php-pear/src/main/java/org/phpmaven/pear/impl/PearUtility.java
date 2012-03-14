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

package org.phpmaven.pear.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.phpmaven.core.ConfigurationParameter;
import org.phpmaven.core.DeserializePhp;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.exec.IPhpExecutable;
import org.phpmaven.exec.IPhpExecutableConfiguration;
import org.phpmaven.exec.PhpCoreException;
import org.phpmaven.exec.PhpException;
import org.phpmaven.pear.IPearChannel;
import org.phpmaven.pear.IPearUtility;

/**
 * Implementation of a pear utility via PHP.EXE and http-client.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
@Component(role = IPearUtility.class, hint = "PHP_EXE", instantiationStrategy = "per-lookup")
public class PearUtility implements IPearUtility {

    /**
     * The installation directory.
     */
    private File installDir;
    
    /**
     * The php executable.
     */
    private IPhpExecutable exec;
    
    /**
     * The pear channels.
     * 
     * <p>
     * Will be initialized by method {@link #initChannels()}
     * </p>
     */
    private List<IPearChannel> knownChannels;
    
    /**
     * The logger.
     */
    private Log log;
    
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

    private File tempDir;

    private File binDir;

    private File phpDir;

    private File docDir;

    private File dataDir;

    private File cfgDir;

    private File wwwDir;

    private File testDir;

    private File downloadDir;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInstalled() throws PhpException {
        final File installedFile = new File(this.installDir, "pear.conf");
        final File installedFile2 = new File(this.installDir, "pear.ini");
        return installedFile.exists() || installedFile2.exists();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void installPear(boolean autoUpdatePear) throws PhpException {
        if (this.isInstalled()) {
            return;
        }
        
        if (!this.installDir.exists()) {
            this.installDir.mkdirs();
        }
        
        this.clearDirectory(this.installDir);
        
        final File goPear = new File(this.installDir, "go-pear.phar");
        try {
            FileUtils.copyURLToFile(
                    this.getClass().getResource("/org/phpmaven/pear/gophar/go-pear.phar"),
                    goPear);
        } catch (IOException e) {
            throw new PhpCoreException("failed installing pear", e);
        }
        
        final IPhpExecutable php = this.getExec();
        final String result = php.executeCode("",
                "error_reporting(0);\n" +
                "Phar::loadPhar('" +
                goPear.getAbsolutePath().replace("\\", "\\\\") + "', 'go-pear.phar');\n" +
                "require_once 'phar://go-pear.phar/PEAR/Start/CLI.php';\n" +
                "PEAR::setErrorHandling(PEAR_ERROR_DIE);\n" +
                "$a = new PEAR_Start_CLI;\n" +
                "$a->prefix = getcwd();\n" +
                "if (OS_WINDOWS) {\n" +
                "  $a->localInstall = true;\n" +
                "  $a->pear_conf = '$prefix\\\\pear.ini';\n" +
                "} else {\n" +
                "  if (get_current_user() != 'root') {\n" +
                "    $a->pear_conf = $a->safeGetenv('HOME') . '/.pearrc';\n" +
                "  }\n" +
                "}\n" +
                "if (PEAR::isError($err = $a->locatePackagesToInstall())) {\n" +
                "  die();\n" +
                "}\n" +
                "$a->setupTempStuff();\n" +
                "if (PEAR::isError($err = $a->postProcessConfigVars())) {\n" +
                "  die();\n" +
                "}\n" +
                "$a->doInstall();\n"
        );
        
        if (!this.isInstalled()) {
            throw new PhpCoreException("Installation failed.\n" + result);
        }
        
        if (autoUpdatePear) {
            this.executePearCmd("upgrade-all");
        }
    }
    
    /**
     * Clears a directory.
     * @param dir directory to be cleared.
     */
    private void clearDirectory(final File dir) {
        for (final File file : dir.listFiles()) {
            if (file.isDirectory()) {
                this.clearDirectory(file);
            }
            file.delete();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void uninstall() throws PhpException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getInstallDir() {
        return this.installDir;
    }
    
    
    /**
     * Returns the php executable.
     * 
     * @return php executable.
     * 
     * @throws PhpException thrown on configuration or execution errors.
     */
    private IPhpExecutable getExec() throws PhpException {
        if (this.exec != null) {
            return this.exec;
        }
        
        Xpp3Dom execConfig = this.factory.getBuildConfig(
                this.session.getCurrentProject(),
                "org.phpmaven",
                "maven-php-pear");
        if (execConfig != null) {
            execConfig = execConfig.getChild("executableConfig");
        }
        
        try {
            final IPhpExecutableConfiguration config =
                    this.factory.lookup(IPhpExecutableConfiguration.class, execConfig, session);
            // TODO set Working directory.
            config.getEnv().put(
                    "PHP_PEAR_INSTALL_DIR",
                    new File(this.installDir, "PEAR").getAbsolutePath());
            config.getEnv().put(
                    "PHP_PEAR_BIN_DIR",
                    this.installDir.getAbsolutePath());
            config.getEnv().put(
                    "PHP_PEAR_SYSCONF_DIR",
                    this.installDir.getAbsolutePath());
            // TODO PHP_BIN ???
            /*config.getEnv().put(
                    "PHP_PEAR_PHP_BIN",
                    new File(this.installDir, "PEAR").getAbsolutePath());*/
            config.setAdditionalPhpParameters(
                    "-C -d date.timezone=UTC -d output_buffering=1 -d safe_mode=0 -d open_basedir=\"\" " +
                    "-d auto_prepend_file=\"\" -d auto_append_file=\"\" -d variables_order=EGPCS " +
                    "-d register_argc_argv=\"On\" " +
                    // the following parameter is required for the susohin plugin (typically present under debian)
                    "-d suhosin.executor.include.whitelist=\"phar\"");
            config.setWorkDirectory(this.getInstallDir());
            config.getIncludePath().add(new File(this.installDir, "PEAR").getAbsolutePath());
            
            this.exec = config.getPhpExecutable(this.log);
            return this.exec;
        } catch (ComponentLookupException ex) {
            throw new PhpCoreException("Unable to create php executable.", ex);
        } catch (PlexusConfigurationException ex) {
            throw new PhpCoreException("Unable to create php executable.", ex);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String executePearCmd(String arguments) throws PhpException {
        final IPhpExecutable ex = this.getExec();
        final File pearCmd = new File(this.getPhpDir(), "pearcmd.php");
        return ex.execute("\"" + pearCmd.getAbsolutePath() + "\" " + arguments, pearCmd);
    }
    
    /**
     * Initialized the known channels and ensures that there is a pear installation at {@link #installDir}.
     * @throws PhpException thrown if something is wrong.
     */
    private void initChannels() throws PhpException {
        // already installed
        if (this.knownChannels != null) {
            return;
        }
        
        if (!this.isInstalled()) {
            throw new PhpCoreException("Pear not installed in " + this.installDir);
        }
        
        final List<IPearChannel> channels = new ArrayList<IPearChannel>();
        final String output = this.executePearCmd("list-channels");
        final StringTokenizer tokenizer = new StringTokenizer(output.trim(), "\n");
        try {
            // table headers...
            tokenizer.nextToken();
            tokenizer.nextToken();
            tokenizer.nextToken();
            
            while (tokenizer.hasMoreTokens()) {
                final String token = tokenizer.nextToken();
                if (token.startsWith(" ")) continue;
                if (token.startsWith("__uri")) continue;
                // if (token.startsWith("doc.")) continue;
                final PearChannel channel = new PearChannel();
                channel.initialize(this, new StringTokenizer(token, " ").nextToken());
                channels.add(channel);
            }
        } catch (NoSuchElementException ex) {
            throw new PhpCoreException("Unexpected output from pear:\n" + output, ex);
        }
        this.knownChannels = channels;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<IPearChannel> listKnownChannels() throws PhpException {
        this.initChannels();
        return this.knownChannels;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPearChannel channelDiscover(String channelName) throws PhpException {
        for (final IPearChannel channel : this.listKnownChannels()) {
            if (channel.getName().equals(channelName) || channelName.equals(channel.getSuggestedAlias())) {
                return channel;
            }
        }
        
        // TODO fetch result and check if the channel was installed.
        final String output = this.executePearCmd("channel-discover " + channelName);
        final IPearChannel result = new PearChannel();
        result.initialize(this, channelName);
        this.knownChannels.add(result);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPearChannel channelDiscoverLocal(File channel) throws PhpException {
        final IPearChannel result = new PearChannel();
        result.initialize(this, "file://" + channel.getAbsolutePath());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void upgrade() throws PhpException {
        // TODO check result for errors
        this.executePearCmd("upgrade");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPearChannel lookupChannel(String channelName) throws PhpException {
        for (final IPearChannel channel : this.listKnownChannels()) {
            if (channel.getName().equals(channelName)  || channelName.equals(channel.getSuggestedAlias())) {
                return channel;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearCache() throws PhpException {
        this.executePearCmd("clear-cache");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(File dir, Log logger) {
        if (this.installDir != null) {
            throw new IllegalStateException("Must not be called twice!");
        }
        this.installDir = dir;
        this.log = logger;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String convertMavenVersionToPearVersion(String src) {
        return Package.convertMavenVersionToPearVersion(src);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String convertPearVersionToMavenVersion(String src) {
        return Package.convertPearVersionToMavenVersion(src);
    }
    
    private void initConfig() throws PhpException {
        if (this.tempDir == null) {
            final File cfgFile = new File(this.getInstallDir(), "pear.conf");
            final File cfgFile2 = new File(this.getInstallDir(), "pear.ini");
            try {
                String cfgFileContents = FileUtils.fileRead(cfgFile.exists() ? cfgFile : cfgFile2);
                while (cfgFileContents.startsWith("#")) {
                    final int indexOf = cfgFileContents.indexOf("\n");
                    if (indexOf == -1) {
                        cfgFileContents = "";
                    } else {
                        cfgFileContents = cfgFileContents.substring(indexOf).trim();
                    }
                }
                final DeserializePhp parser = new DeserializePhp(cfgFileContents);
                @SuppressWarnings("unchecked")
                final Map<String, Object> value = (Map<String, Object>) parser.parse();
                this.tempDir = new File((String) value.get("temp_dir"));
                this.downloadDir = new File((String) value.get("download_dir"));
                this.binDir = new File((String) value.get("bin_dir"));
                this.phpDir = new File((String) value.get("php_dir"));
                this.docDir = new File((String) value.get("doc_dir"));
                this.dataDir = new File((String) value.get("data_dir"));
                this.cfgDir = new File((String) value.get("cfg_dir"));
                this.wwwDir = new File((String) value.get("www_dir"));
                this.testDir = new File((String) value.get("test_dir"));
            } catch (Exception ex) {
                throw new PhpCoreException("Problems reading and parsing pear configuration", ex);
            }
        }
    }

    @Override
    public File getTempDir() throws PhpException {
        this.initConfig();
        return this.tempDir;
    }

    @Override
    public File getDownloadDir() throws PhpException {
        this.initConfig();
        return this.downloadDir;
    }

    @Override
    public File getBinDir() throws PhpException {
        this.initConfig();
        return this.binDir;
    }

    @Override
    public File getPhpDir() throws PhpException {
        this.initConfig();
        return this.phpDir;
    }

    @Override
    public File getDocDir() throws PhpException {
        this.initConfig();
        return this.docDir;
    }

    @Override
    public File getDataDir() throws PhpException {
        this.initConfig();
        return this.dataDir;
    }

    @Override
    public File getCfgDir() throws PhpException {
        this.initConfig();
        return this.cfgDir;
    }

    @Override
    public File getWwwDir() throws PhpException {
        this.initConfig();
        return this.wwwDir;
    }

    @Override
    public File getTestDir() throws PhpException {
        this.initConfig();
        return this.testDir;
    }
    
    

}
