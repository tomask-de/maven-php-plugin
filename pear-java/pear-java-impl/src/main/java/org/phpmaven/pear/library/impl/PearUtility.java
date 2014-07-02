/**
 * Copyright 2010-2012 by PHP-maven.org
 * 
 * This file is part of pear-java.
 *
 * pear-java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pear-java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pear-java.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.phpmaven.pear.library.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.codehaus.plexus.util.FileUtils;
import org.phpmaven.pear.library.ICategory;
import org.phpmaven.pear.library.IDependency;
import org.phpmaven.pear.library.IMaintainer;
import org.phpmaven.pear.library.IPackage;
import org.phpmaven.pear.library.IPackageVersion;
import org.phpmaven.pear.library.IPearChannel;
import org.phpmaven.pear.library.IPearProxy;
import org.phpmaven.pear.library.IPearUtility;
import org.phpmaven.pear.library.IRestBaseUrl;
import org.phpmaven.pear.library.IRestServer;
import org.phpmaven.pear.library.IServer;
import org.phpmaven.pear.library.ISoapFunction;
import org.phpmaven.pear.library.ISoapServer;
import org.phpmaven.pear.library.IVersion;
import org.phpmaven.pear.library.IXmlRpcFunction;
import org.phpmaven.pear.library.IXmlRpcServer;
import org.phpmaven.phpexec.library.DeserializePhp;
import org.phpmaven.phpexec.library.IPhpExecutable;
import org.phpmaven.phpexec.library.IPhpExecutableConfiguration;
import org.phpmaven.phpexec.library.PhpCoreException;
import org.phpmaven.phpexec.library.PhpException;
import org.phpmaven.phpexec.library.PhpWarningException;

/**
 * Implementation of a pear utility via PHP.EXE and http-client.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 0.1.0
 */
public class PearUtility implements IPearUtility {

    /**
     * The installation directory.
     */
    private File installDir;
    
    /**
     * The pear channels.
     * 
     * <p>
     * Will be initialized by method {@link #initChannels()}
     * </p>
     */
    private List<IPearChannel> knownChannels;

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

	private IPhpExecutable phpExecutable;
	
	private List<IPearProxy> proxies = new ArrayList<IPearProxy>();
	
    /**
     * initializes the proxy for pear.
     * @throws PhpException  
     */
    private void initializeProxy() throws PhpException {
        if (!this.initializedProxy) {
            this.initializedProxy = true;
            for (final IPearProxy proxy : proxies) {
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
                    this.getExec().setSilent(true);
                    try {
                        this.executePearCmd("config-set http_proxy " + proxyString);
                    } finally {
                        this.getExec().setSilent(false);
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
                    this.getClass().getResource("/org/phpmaven/pear/library/impl/go-pear.phar"),
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
    	return this.phpExecutable;
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
                final PearChannel channel = this.createChannelEx();
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
                    this.installDir);
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
            final PearChannel result = this.createChannelEx();
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
        final IPearChannel result = this.createChannelEx();
        result.initialize(this, channelName);
        this.knownChannels.add(result);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPearChannel channelDiscoverLocal(File channel) throws PhpException {
        final IPearChannel result = this.createChannelEx();
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
    public void configure(File dir, IPhpExecutableConfiguration config, Iterable<IPearProxy> proxies) {
        if (this.installDir != null) {
            throw new IllegalStateException("Must not be called twice!");
        }
        for (final IPearProxy proxy : proxies) {
        	this.proxies.add(proxy);
        }
        this.installDir = dir;
        
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
        
        this.phpExecutable = config.getPhpExecutable();
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

	@Override
	public final IPearChannel createChannel() {
		return this.createChannelEx();
	}

	protected PearChannel createChannelEx() {
		return new PearChannel();
	}

	@Override
	public final IPackageVersion createPackageVersion() {
		return this.createPackageVersionEx();
	}

	protected PackageVersion createPackageVersionEx() {
		return new PackageVersion();
	}

	@Override
	public final IVersion createVersion() {
		return this.createVersionEx();
	}

	protected Version createVersionEx() {
		return new Version();
	}

	@Override
	public final IServer createServer() {
		return this.createServerEx();
	}

	protected Server createServerEx() {
		return new Server();
	}

	@Override
	public final IPackage createPackage() {
		return this.createPackageEx();
	}

	protected Package createPackageEx() {
		return new Package();
	}

	@Override
	public final ICategory createCategory() {
		return this.createCategoryEx();
	}

	protected Category createCategoryEx() {
		return new Category();
	}

	@Override
	public final IMaintainer createMaintainer() {
		return this.createMaintainerEx();
	}

	protected Maintainer createMaintainerEx() {
		return new Maintainer();
	}

	@Override
	public final IXmlRpcServer createXmlRpcServer() {
		return this.createXmlRpcServerEx();
	}

	protected XmlRpcServer createXmlRpcServerEx() {
		return new XmlRpcServer();
	}

	@Override
	public final IXmlRpcFunction createXmlRpcServerFunction() {
		return this.createXmlRpcServerFunctionEx();
	}

	protected XmlRpcFunction createXmlRpcServerFunctionEx() {
		return new XmlRpcFunction();
	}

	@Override
	public final IRestServer createRestServer() {
		return this.createRestServerEx();
	}

	protected RestServer createRestServerEx() {
		return new RestServer();
	}

	@Override
	public final IRestBaseUrl createRestBaseUrl() {
		return this.createRestBaseUrlEx();
	}

	protected RestBaseUrl createRestBaseUrlEx() {
		return new RestBaseUrl();
	}

	@Override
	public final ISoapServer createSoapServer() {
		return this.createSoapServerEx();
	}

	protected SoapServer createSoapServerEx() {
		return new SoapServer();
	}

	@Override
	public final ISoapFunction createSoapFunction() {
		return this.createSoapFunctionEx();
	}

	protected SoapFunction createSoapFunctionEx() {
		return new SoapFunction();
	}

	@Override
	public final IDependency createDependency() {
		return this.createDependencyEx();
	}

	protected Dependency createDependencyEx() {
		return new Dependency();
	}

}
