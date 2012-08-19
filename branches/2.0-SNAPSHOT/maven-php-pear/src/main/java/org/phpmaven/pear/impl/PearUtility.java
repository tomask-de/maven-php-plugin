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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.DefaultDependencyResolutionRequest;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.DependencyResolutionException;
import org.apache.maven.project.DependencyResolutionRequest;
import org.apache.maven.project.DependencyResolutionResult;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingResult;
import org.apache.maven.project.ProjectDependenciesResolver;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.settings.Proxy;
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
import org.phpmaven.exec.PhpWarningException;
import org.phpmaven.pear.IPearChannel;
import org.phpmaven.pear.IPearUtility;
import org.sonatype.aether.util.version.GenericVersionScheme;
import org.sonatype.aether.version.InvalidVersionSpecificationException;

/**
 * Implementation of a pear utility via PHP.EXE and http-client.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
@Component(role = IPearUtility.class, hint = "PHP_EXE", instantiationStrategy = "per-lookup")
public class PearUtility implements IPearUtility {

    private static final class LogWrapper implements Log {
        /**
         * underlying log.
         */
        private Log theLog;
        /**
         * true if the log is inactive (used to set the proxies).
         */
        private boolean silent;
        
        /**
         * Sets the log.
         * @param log log
         */
        public void setLog(Log log) {
            this.theLog = log;
        }
        
        /**
         * Sets the silent flag.
         * @param s silent
         */
        public void setSilent(boolean s) {
            this.silent = s;
        }
        
        @Override
        public void debug(CharSequence arg0) {
            if (!this.silent) {
                this.theLog.debug(arg0);
            }
        }
        
        @Override
        public void debug(Throwable arg0) {
            if (!this.silent) {
                this.theLog.debug(arg0);
            }
        }
        
        @Override
        public void debug(CharSequence arg0, Throwable arg1) {
            if (!this.silent) {
                this.theLog.debug(arg0, arg1);
            }
        }
        
        @Override
        public void error(CharSequence arg0) {
            if (!this.silent) {
                this.theLog.error(arg0);
            }
        }
        
        @Override
        public void error(Throwable arg0) {
            if (!this.silent) {
                this.theLog.error(arg0);
            }
        }
        
        @Override
        public void error(CharSequence arg0, Throwable arg1) {
            if (!this.silent) {
                this.theLog.error(arg0, arg1);
            }
        }
        
        @Override
        public void info(CharSequence arg0) {
            if (!this.silent) {
                this.theLog.info(arg0);
            }
        }
        
        @Override
        public void info(Throwable arg0) {
            if (!this.silent) {
                this.theLog.info(arg0);
            }
        }
        
        @Override
        public void info(CharSequence arg0, Throwable arg1) {
            if (!this.silent) {
                this.theLog.info(arg0, arg1);
            }
        }
        
        @Override
        public boolean isDebugEnabled() {
            if (!this.silent) {
                return this.theLog.isDebugEnabled();
            }
            return false;
        }
        
        @Override
        public boolean isErrorEnabled() {
            if (!this.silent) {
                return this.theLog.isErrorEnabled();
            }
            return false;
        }
        
        @Override
        public boolean isInfoEnabled() {
            if (!this.silent) {
                return this.theLog.isInfoEnabled();
            }
            return false;
        }
        
        @Override
        public boolean isWarnEnabled() {
            if (!this.silent) {
                return this.theLog.isWarnEnabled();
            }
            return false;
        }
        
        @Override
        public void warn(CharSequence arg0) {
            if (!this.silent) {
                this.theLog.warn(arg0);
            }
        }
        
        @Override
        public void warn(Throwable arg0) {
            if (!this.silent) {
                this.theLog.warn(arg0);
            }
        }
        
        @Override
        public void warn(CharSequence arg0, Throwable arg1) {
            if (!this.silent) {
                this.theLog.warn(arg0, arg1);
            }
        }
    }
    
    /** the generic version scheme. */
    private static final GenericVersionScheme SCHEME = new GenericVersionScheme();

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
    private LogWrapper log = new LogWrapper();
    
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
     * true if the proxy was initialized.
     */
    private boolean initializedProxy;
    
    /**
     * the repository system.
     */
    @Requirement
    private RepositorySystem reposSystem;
    
    /**
     * The project builder.
     */
    @Requirement
    private ProjectBuilder projectBuilder;
    
    /**
     * The dependencies resolver.
     */
    @Requirement
    private ProjectDependenciesResolver dependencyResolver;
    
    /**
     * initializes the proxy for pear.
     * @throws PhpException  
     */
    private void initializeProxy() throws PhpException {
        if (!this.initializedProxy) {
            this.initializedProxy = true;
            final List<Proxy> proxies = this.session.getRequest().getProxies();
            for (final Proxy proxy : proxies) {
                if (proxy.isActive()) {
                    String proxyString = "";
                    if (proxy.getProtocol() != null && proxy.getProtocol().length() > 0) {
                        proxyString += proxy.getProtocol();
                        proxyString += "://";
                    }
                    if (proxy.getUsername() != null && proxy.getUsername().length() > 0) {
                        proxyString += proxy.getUsername();
                        if (proxy.getPassword() != null && proxy.getPassword().length() > 0) {
                            proxyString += ":";
                            proxyString += proxy.getPassword();
                        }
                        proxyString += "@";
                    }
                    proxyString += proxy.getHost();
                    proxyString += ":";
                    proxyString += proxy.getPort();
                    this.log.setSilent(true);
                    try {
                        this.executePearCmd("config-set http_proxy " + proxyString);
                    } finally {
                        this.log.setSilent(false);
                    }
                    return;
                }
            }
            // no active proxy configured
            this.executePearCmd("config-set http_proxy \" \"");
        }
    }

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
                "  $a->localInstall = true;\n" +
                "  $a->pear_conf = '$prefix/pear.ini';\n" +
                "  $a->temp_dir='$prefix/tmp';\n" +
                "  $a->download_dir='$prefix/tmp';\n" +
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
            this.initializeProxy();
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
        this.initializeProxy();
        final IPhpExecutable ex = this.getExec();
        final File pearCmd = new File(this.getPhpDir(), "pearcmd.php");
        try {
            return ex.execute(
                "\"" + pearCmd.getAbsolutePath() + "\" " +
                "-c \"" + new File(this.getInstallDir().getAbsolutePath(), "pear.ini").getAbsolutePath() + "\" " +
                "-C \"" + new File(this.getInstallDir().getAbsolutePath(), "pear.conf").getAbsolutePath() + "\" " +
                " " + arguments, pearCmd);
        } catch (PhpWarningException e) {
            // ignore it
            return e.getAppendedOutput();
        }
    }
    
    /**
     * Initialized the known channels and ensures that there is a pear installation at {@link #installDir}.
     * @param readRemote true to read the remote channels
     * @throws PhpException thrown if something is wrong.
     */
    public void initChannels(boolean readRemote) throws PhpException {
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
                if (token.startsWith("CHANNEL")) continue;
                // if (token.startsWith("doc.")) continue;
                final PearChannel channel = new PearChannel();
                if (readRemote) {
                    channel.initialize(this, new StringTokenizer(token, " ").nextToken());
                } else {
                    final StringTokenizer tokenizer2 = new StringTokenizer(token, " ");
                    final String name = tokenizer2.nextToken().trim();
                    final String alias = tokenizer2.nextToken().trim();
                    final String summary = tokenizer2.nextToken().trim();
                    channel.initialize(this, name, alias, summary);
                }
                channels.add(channel);
            }
        } catch (NoSuchElementException ex) {
            throw new PhpCoreException("Unexpected output from pear:\n" + output, ex);
        }
        this.knownChannels = channels;
    }
    
    /**
     * Initialized the known channels and ensures that there is a pear installation at {@link #installDir}.
     * @throws PhpException thrown if something is wrong.
     */
    private void initChannels() throws PhpException {
        this.initChannels(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<IPearChannel> listKnownChannels() throws PhpException {
        this.initChannels();
        return this.knownChannels;
    }

    @Override
    public IPearChannel channelAdd(String channelName, String alias, String summary) throws PhpException {
        for (final IPearChannel channel : this.listKnownChannels()) {
            if (channel.getName().equals(channelName) || channelName.equals(channel.getSuggestedAlias())) {
                return channel;
            }
        }
        
        try {
            // TODO fetch result and check if the channel was installed.
            final File tmpChannelXml = File.createTempFile(
                    "channel",
                    ".xml",
                    new File(this.session.getCurrentProject().getBasedir(), "target"));
            tmpChannelXml.deleteOnExit();
            final FileWriter fw = new FileWriter(tmpChannelXml);
            fw.write(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<channel " +
                "version=\"1.0\" " +
                "xsi:schemaLocation=\"http://pear.php.net/channel-1.0 http://pear.php.net/dtd/channel-1.0.xsd\" " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xmlns=\"http://pear.php.net/channel-1.0\" " +
                ">\n" +
                "<name>" + channelName + "</name>\n" +
                "<suggestedalias>" + (alias == null ? channelName : alias) + "</suggestedalias>\n" +
                "<summary>" + summary + "</summary>\n" +
                "<servers><primary><rest>\n" +
                "<baseurl type=\"REST1.0\">rest/</baseurl>\n" +
                "</rest></primary></servers>\n" +
                "</channel>\n");
            fw.close();
            final String output = this.executePearCmd("channel-add \"" + tmpChannelXml.getAbsolutePath() + "\"");
            final PearChannel result = new PearChannel();
            result.initialize(this, channelName, alias, summary);
            this.knownChannels.add(result);
            return result;
        } catch (IOException ex) {
            throw new PhpCoreException("Error adding local channel", ex);
        }
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
        this.log.setLog(logger);
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
    
    /**
     * Resolves the artifact.
     * @param groupId group id
     * @param artifactId artifact id
     * @param version version
     * @param type type
     * @param classifier classifier
     * @return the resolved artifact
     * @throws PhpException thrown on resolve errors
     */
    private Artifact resolveArtifact(String groupId, String artifactId, String version, String type, String classifier)
        throws PhpException {
        final Artifact artifact = this.reposSystem.createArtifactWithClassifier(
                groupId, artifactId, version, type, classifier);
        final ArtifactResolutionRequest request = new ArtifactResolutionRequest();
        request.setArtifact(artifact);
        request.setLocalRepository(this.session.getLocalRepository());
        request.setOffline(this.session.isOffline());
        final Set<ArtifactRepository> setRepos = new HashSet<ArtifactRepository>(
                this.session.getRequest().getRemoteRepositories());
        setRepos.addAll(this.session.getCurrentProject().getRemoteArtifactRepositories());
        request.setRemoteRepositories(new ArrayList<ArtifactRepository>(setRepos));
        final ArtifactResolutionResult result = this.reposSystem.resolve(request);
        if (!result.isSuccess()) {
            throw new PhpCoreException("dependency resolution failed for " +
                groupId + ":" + artifactId + ":" + version);
        }
        
        final Artifact resultArtifact = result.getArtifacts().iterator().next();
        return resultArtifact;
    }

    private void installFromMavenRepository(
            String groupId, String artifactId, String version,
            boolean ignoreCore) throws PhpException {
        final Artifact artifact = this.resolveArtifact(groupId, artifactId, version, "pom", null);
        final File pomFile = artifact.getFile();
        final ProjectBuildingRequest pbr = new DefaultProjectBuildingRequest(this.session.getProjectBuildingRequest());
        try {
            pbr.setProcessPlugins(false);
            final ProjectBuildingResult pbres = this.projectBuilder.build(pomFile, pbr);
            final MavenProject project = pbres.getProject();
            final DependencyResolutionRequest drr = new DefaultDependencyResolutionRequest(
                project, session.getRepositorySession());
            final DependencyResolutionResult drres = this.dependencyResolver.resolve(drr);
            // dependencies may be duplicate. ensure we have only one version (the newest).
            final Map<String, org.sonatype.aether.graph.Dependency> deps =
                new HashMap<String, org.sonatype.aether.graph.Dependency>();
            for (final org.sonatype.aether.graph.Dependency dep : drres.getDependencies()) {
                final String key = dep.getArtifact().getGroupId() + ":" + dep.getArtifact().getArtifactId();
                if (!deps.containsKey(key)) {
                    deps.put(key, dep);
                } else {
                    final org.sonatype.aether.graph.Dependency dep2 = deps.get(key);
                    final org.sonatype.aether.version.Version ver =
                        SCHEME.parseVersion(dep.getArtifact().getVersion());
                    final org.sonatype.aether.version.Version ver2 =
                        SCHEME.parseVersion(dep2.getArtifact().getVersion());
                    if (ver2.compareTo(ver) < 0) {
                        deps.put(key, dep);
                    }
                }
            }
            final List<File> filesToInstall = new ArrayList<File>();
            // first the dependencies
            this.log.debug(
                    "resolving tgz and project for " +
                    groupId + ":" +
                    artifactId + ":" + 
                    version);
            this.resolveTgz(groupId, artifactId, version, filesToInstall, ignoreCore);
            this.resolveChannels(project);
            for (final org.sonatype.aether.graph.Dependency dep : deps.values()) {
                this.log.debug(
                        "resolving tgz and project for " +
                        dep.getArtifact().getGroupId() + ":" +
                        dep.getArtifact().getArtifactId() + ":" + 
                        dep.getArtifact().getVersion());
                if (ignoreCore && this.isMavenCorePackage(
                    dep.getArtifact().getGroupId(),
                    dep.getArtifact().getArtifactId())) {
                    // ignore core packages
                    continue;
                }
                this.resolveTgz(
                    dep.getArtifact().getGroupId(),
                    dep.getArtifact().getArtifactId(),
                    dep.getArtifact().getVersion(),
                    filesToInstall,
                    ignoreCore);
                final Artifact depPomArtifact = this.resolveArtifact(
                    dep.getArtifact().getGroupId(),
                    dep.getArtifact().getArtifactId(),
                    dep.getArtifact().getVersion(),
                    "pom", null);
                final File depPomFile = depPomArtifact.getFile();
                final ProjectBuildingResult depPbres = this.projectBuilder.build(depPomFile, pbr);
                final MavenProject depProject = depPbres.getProject();
                this.resolveChannels(depProject);
            }
            
            Collections.reverse(filesToInstall);
            for (final File file : filesToInstall) {
                this.executePearCmd("install --force --nodeps \"" + file.getAbsolutePath() + "\"");
            }
        } catch (InvalidVersionSpecificationException ex) {
            throw new PhpCoreException(ex);
        } catch (ProjectBuildingException ex) {
            throw new PhpCoreException(ex);
        } catch (DependencyResolutionException ex) {
            throw new PhpCoreException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void installFromMavenRepository(String groupId, String artifactId, String version) throws PhpException {
        this.installFromMavenRepository(groupId, artifactId, version, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void installCoreFromMavenRepository(String groupId, String artifactId, String version) throws PhpException {
        this.installFromMavenRepository(groupId, artifactId, version, false);
    }

    /**
     * resolving the pear channels from given project.
     * @param project the project
     * @throws PhpException thrown on discover errors
     */
    private void resolveChannels(MavenProject project) throws PhpException {
        final Build build = project.getBuild();
        if (build != null) {
            for (final Plugin plugin : build.getPlugins()) {
                if ("org.phpmaven".equals(plugin.getGroupId()) &&
                        "maven-php-plugin".equals(plugin.getArtifactId())) {
                    final Xpp3Dom dom = (Xpp3Dom) plugin.getConfiguration();
                    final Xpp3Dom pearChannelsDom = dom.getChild("pearChannels");
                    if (pearChannelsDom != null) {
                        for (final Xpp3Dom child : pearChannelsDom.getChildren()) {
                            this.channelAdd(child.getValue(), null, "local-pear-channel");
                        }
                    }
                }
            }
        }
    }

    /**
     * Resolves the tgz and adds it to the files for installation.
     * @param groupId group id
     * @param artifactId artifact id
     * @param version version
     * @param filesToInstall files to be installed
     * @param ignoreCore true to ignore core packages
     * @throws PhpException thrown on resolve errors.
     */
    private void resolveTgz(
            String groupId, String artifactId, String version, List<File> filesToInstall, boolean ignoreCore)
        throws PhpException {
        if (!ignoreCore || !this.isMavenCorePackage(groupId, artifactId)) {
            final Artifact artifact = this.resolveArtifact(groupId, artifactId, version, "tgz", "pear-tgz");
            filesToInstall.add(artifact.getFile());
        }
    }

    @Override
    public boolean isMavenCorePackage(String groupId, String artifactId) {
        if (("net.php".equals(groupId)) && (
                "Archive_Tar".equals(artifactId)
                || "Console_Getopt".equals(artifactId)
                || "PEAR".equals(artifactId)
                || "Structures_Graph".equals(artifactId)
                || "XML_Util".equals(artifactId))) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isPearCorePackage(String channel, String pkg) {
        if (("pear".equals(channel) || "pear.php.net".equals(channel)) && (
                "Archive_Tar".equals(pkg)
                || "Console_Getopt".equals(pkg)
                || "PEAR".equals(pkg)
                || "Structures_Graph".equals(pkg)
                || "XML_Util".equals(pkg))) {
            return true;
        }
        return false;
    }

}
