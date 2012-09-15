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

package org.phpmaven.plugin.pear;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.MavenExecutionException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.phpmaven.plugin.pear.PearPackageInfo.PearDependency;

/**
 * Goal to copy the resources and classes to the output folder.
 * 
 * TODO migrate
 *
 * @goal pear-create-pom
 * 
 * @author mepeisen
 */
public class PearCreatePomMojo extends DefaultMojo
{

	/**
	 * @parameter expression="${pearChannelAlias}"
	 * @required
	 */
    private String pearChannelAlias;
    
    /**
     * @parameter expression="${pearPackage}"
     * @required
     */
    private String pearPackage;
    
    /**
     * @parameter expression="${pearPackageVersion}
     * @required
     */
    private String pearPackageVersion;
    
    /**
     * @parameter expression="${pearPackageMavenVersion}
     */
    private String pearPackageMavenVersion;
    
    /**
     * @parameter expression="${pomTargetFile}"
     * @required
     */
    private File pomTargetFile;
    
    /**
     * @parameter expression="${pearFetchDependencies}" default-value="false"
     */
    private boolean pearFetchDependencies;
    
    /**
     * @parameter expression="${pearGroupId}
     */
    private String pearGroupId; // org.phpunit
    
    /**
     * @parameter expression="${pearArtifactId}"
     */
    private String pearArtifactId; // phpunit5
    
    /**
     * @parameter expression="${pearName}" default-value="PEAR library"
     */
    private String pearName; // PEAR library
    
    /**
     * @parameter default-value="Official php maven repository (releases)"
     * @required
     */
    private String deployRepositoryName;
    
    /**
     * @parameter default-value="deploy-releases-repo1.php-maven.org"
     * @required
     */
    private String deployRepsitoryId;
    
    /**
     * @parameter default-value="${deploy-releases-repo1.php-maven.org.url}"
     * @required
     */
    private String deployRepositoryUrl;
    
    /**
     * @parameter default-value="Official php maven repository (snapshots)"
     * @required
     */
    private String snapshotsRepositoryName;
    
    /**
     * @parameter default-value="deploy-snapshots-repo1.php-maven.org"
     * @required
     */
    private String snapshotsRepositoryId;
    
    /**
     * @parameter default-value="${deploy-snapshots-repo1.php-maven.org.url}"
     * @required
     */
    private String snapshotsRepositoryUrl;
    
    private static final String POM_TEMPLATE =
            "<?xml version=\"1.0\" encoding= \"UTF-8\"?>\n" + //$NON-NLS-1$
            "<project>\n" + //$NON-NLS-1$
            "    <modelVersion>4.0.0</modelVersion>\n" + //$NON-NLS-1$
            "    <groupId>${TARGET.GROUPID}</groupId>\n" + //$NON-NLS-1$
            "    <artifactId>${TARGET.ARTIFACTID}</artifactId>\n" + //$NON-NLS-1$
            "    <packaging>pear</packaging>\n" + //$NON-NLS-1$
            "    <name>${TARGET.NAME}</name>\n" + //$NON-NLS-1$
            "    <version>${TARGET.VERSION}</version>\n" + //$NON-NLS-1$
            "    \n" + //$NON-NLS-1$
            "    <build>\n" + //$NON-NLS-1$
            "        <extensions>\n" + //$NON-NLS-1$
            "            <extension>\n" + //$NON-NLS-1$
            "                <groupId>org.apache.maven.wagon</groupId>\n" + //$NON-NLS-1$
            "                <artifactId>wagon-webdav-jackrabbit</artifactId>\n" + //$NON-NLS-1$
            "                <version>2.0</version>\n" + //$NON-NLS-1$
            "            </extension>\n" + //$NON-NLS-1$
            "            <extension>\n" + //$NON-NLS-1$
            "                <groupId>org.apache.maven.wagon</groupId>\n" + //$NON-NLS-1$
            "                <artifactId>wagon-ftp</artifactId>\n" + //$NON-NLS-1$
            "                <version>2.0</version>\n" + //$NON-NLS-1$
            "            </extension>\n" + //$NON-NLS-1$
            "        </extensions>\n" + //$NON-NLS-1$
            "        <plugins>\n" + //$NON-NLS-1$
            "            <plugin>\n" + //$NON-NLS-1$
            "                <groupId>org.phpmaven</groupId>\n" + //$NON-NLS-1$
            "                <artifactId>maven-php-plugin</artifactId>\n" + //$NON-NLS-1$
            "                <version>2.0.0-beta-2</version>\n" + //$NON-NLS-1$
            "                <extensions>true</extensions>\n" + //$NON-NLS-1$
            "                <configuration>\n" + //$NON-NLS-1$
            "                    <pearChannels>\n" + //$NON-NLS-1$
            // <channel>pear.phpunit.de</channel>
            "                        ${TARGET.CHANNELS}\n" + //$NON-NLS-1$
            "                    </pearChannels>\n" + //$NON-NLS-1$
            // pear.phpunit.de
            "                    <pearChannelAlias>${TARGET.CHANNELALIAS}</pearChannelAlias>\n" + //$NON-NLS-1$
            // PHPUnit
            "                    <pearPackage>${TARGET.PACKAGENAME}</pearPackage>\n" + //$NON-NLS-1$
            "                    <pearFetchDependencies>${TARGET.FETCHDEPS}</pearFetchDependencies>\n" + //$NON-NLS-1$
            "                    <pearPackageVersion>${TARGET.PEARVERSION}</pearPackageVersion>\n" + //$NON-NLS-1$
            "                </configuration>\n" + //$NON-NLS-1$
            "            </plugin>\n" + //$NON-NLS-1$
            "        </plugins>\n" + //$NON-NLS-1$
            "    </build>\n" + //$NON-NLS-1$
            "\n" + //$NON-NLS-1$
            "\n" + //$NON-NLS-1$
            "    <distributionManagement>\n" + //$NON-NLS-1$
            "        <repository>\n" + //$NON-NLS-1$
            "            <id>${REPOS.ID}</id>\n" + //$NON-NLS-1$
            "            <name>${REPOS.NAME}</name>\n" + //$NON-NLS-1$
            "            <url>${REPOS.URL}</url>\n" + //$NON-NLS-1$
            "        </repository>\n" + //$NON-NLS-1$
            "        <snapshotRepository>\n" + //$NON-NLS-1$
            "            <id>${SNAPSHOTS.ID}</id>\n" + //$NON-NLS-1$
            "            <name>${SNAPSHOTS.NAME}</name>\n" + //$NON-NLS-1$
            "            <url>${SNAPSHOTS.URL}</url>\n" + //$NON-NLS-1$
            "        </snapshotRepository>\n" + //$NON-NLS-1$
            "    </distributionManagement>\n" + //$NON-NLS-1$
            "${DEPENDENCIES}" + //$NON-NLS-1$
            "${ETC}" + //$NON-NLS-1$
            "</project>\n" + //$NON-NLS-1$
            "\n"; //$NON-NLS-1$
    
    private static final String COMMON_XML_TEMPLATE =
            "<?xml version=\"1.0\" encoding= \"UTF-8\"?>\n" + //$NON-NLS-1$
            "<project>\n" + //$NON-NLS-1$
            "    <modelVersion>4.0.0</modelVersion>\n" + //$NON-NLS-1$
            "    <groupId>org.phpmaven.pear-projects</groupId>\n" + //$NON-NLS-1$
            "    <artifactId>${PEAR.CHANNEL}</artifactId>\n" + //$NON-NLS-1$
            "    <packaging>pom</packaging>\n" + //$NON-NLS-1$
            "    <name>${TARGET.NAME}</name>\n" + //$NON-NLS-1$
            "    <version>1</version>\n" + //$NON-NLS-1$
            "    \n" + //$NON-NLS-1$
            "    <build>\n" + //$NON-NLS-1$
            "        <extensions>\n" + //$NON-NLS-1$
            "            <extension>\n" + //$NON-NLS-1$
            "                <groupId>org.apache.maven.wagon</groupId>\n" + //$NON-NLS-1$
            "                <artifactId>wagon-webdav-jackrabbit</artifactId>\n" + //$NON-NLS-1$
            "                <version>2.0</version>\n" + //$NON-NLS-1$
            "            </extension>\n" + //$NON-NLS-1$
            "            <extension>\n" + //$NON-NLS-1$
            "                <groupId>org.apache.maven.wagon</groupId>\n" + //$NON-NLS-1$
            "                <artifactId>wagon-ftp</artifactId>\n" + //$NON-NLS-1$
            "                <version>2.0</version>\n" + //$NON-NLS-1$
            "            </extension>\n" + //$NON-NLS-1$
            "        </extensions>\n" + //$NON-NLS-1$
            "    </build>\n" + //$NON-NLS-1$
            "\n" + //$NON-NLS-1$
            "\n" + //$NON-NLS-1$
            "    <distributionManagement>\n" + //$NON-NLS-1$
            "        <repository>\n" + //$NON-NLS-1$
            "            <id>${REPOS.ID}</id>\n" + //$NON-NLS-1$
            "            <name>${REPOS.NAME}</name>\n" + //$NON-NLS-1$
            "            <url>${REPOS.URL}</url>\n" + //$NON-NLS-1$
            "        </repository>\n" + //$NON-NLS-1$
            "        <snapshotRepository>\n" + //$NON-NLS-1$
            "            <id>${SNAPSHOTS.ID}</id>\n" + //$NON-NLS-1$
            "            <name>${SNAPSHOTS.NAME}</name>\n" + //$NON-NLS-1$
            "            <url>${SNAPSHOTS.URL}</url>\n" + //$NON-NLS-1$
            "        </snapshotRepository>\n" + //$NON-NLS-1$
            "    </distributionManagement>\n" + //$NON-NLS-1$
            "${MODULES}" + //$NON-NLS-1$
            "</project>\n" + //$NON-NLS-1$
            "\n"; //$NON-NLS-1$
    
    /**
     * @inheritDoc
     */
    public void execute() throws MojoExecutionException
    {
        if (this.pearGroupId == null)
        {
            this.pearGroupId = this.channelToGroupId(this.pearChannelAlias);
        }
        if (this.pearArtifactId == null)
        {
            this.pearArtifactId = this.pearPackage;
        }
        
        final String[] versions = this.pearPackageVersion.split(",");
        final File path = this.pomTargetFile;
        
        if (versions.length > 1 && this.pearPackageMavenVersion != null) {
            throw new MojoExecutionException("Multiple versions cannot be used with option pearPackageMavenVersion");
        }
        
        final File commonPom = new File(this.pomTargetFile, this.pearGroupId + "/pom.xml");
        if (!commonPom.exists())
        {
            final StringBuffer modulesXmlBuffer = new StringBuffer();
            modulesXmlBuffer.append("    <modules>\n");
            modulesXmlBuffer.append("    </modules>\n");
            
            String pomXml = COMMON_XML_TEMPLATE.replace("${PEAR.CHANNEL}", this.pearGroupId.replace(".", "_"));
            pomXml = pomXml.replace("${TARGET.NAME}", this.pearName);
            pomXml = pomXml.replace("${REPOS.ID}", this.deployRepsitoryId);
            pomXml = pomXml.replace("${REPOS.NAME}", escapeXml(this.deployRepositoryName));
            pomXml = pomXml.replace("${REPOS.URL}", this.deployRepositoryUrl);
            pomXml = pomXml.replace("${SNAPSHOTS.ID}", this.snapshotsRepositoryId);
            pomXml = pomXml.replace("${SNAPSHOTS.NAME}", escapeXml(this.snapshotsRepositoryName));
            pomXml = pomXml.replace("${SNAPSHOTS.URL}", this.snapshotsRepositoryUrl);
            pomXml = pomXml.replace("${MODULES}", modulesXmlBuffer.toString());
            
            this.createPomFile(pomXml, commonPom);
        }
        
        try
        {
            final MavenProject commonProject = this.getProjectFromPom(commonPom);
            
            for (final String version : versions)
            {
                if (versions.length > 1 || this.pearPackageMavenVersion == null) {
                    this.pearPackageMavenVersion = version;
                }
                this.pearPackageVersion = version;
                final String moduleName = this.pearPackage + "-" + this.pearPackageMavenVersion;
                this.pomTargetFile = new File(path, this.pearGroupId + "/" + moduleName + "/pom.xml");
                this.createThePom();
                final List<String> modules = commonProject.getModules();
                if (!modules.contains(moduleName))
                {
                    commonProject.getModel().addModule(moduleName);
                    commonProject.writeModel(new FileWriter(commonPom));
                }
            }
        }
        catch (ProjectBuildingException ex)
        {
            throw new MojoExecutionException("Unable to read common pom " + commonPom, ex);
        }
        catch (IOException ex)
        {
            throw new MojoExecutionException("Unable to write common pom " + commonPom, ex);
        }
    }
    
    private String escapeXml(String input)
    {
        return input.replace("&", "&amp;").replace(">", "&gt;").replace("<", "&lt;");
    }

    private void createThePom() throws MojoExecutionException {
        this.getLog().info("Create the pom " + this.pomTargetFile + " for " + this.pearChannelAlias + "/" + this.pearPackage + "-" + this.pearPackageMavenVersion);
        
        final Pear pear = new Pear();
    	pear.setLog(getLog());
        pear.clearCache();
    	if (!pear.getPearChannels().contains(this.pearChannelAlias))
    	{
    	    this.getLog().info("channel discover " + this.pearChannelAlias); //$NON-NLS-1$
            pear.channelDiscover(this.pearChannelAlias);
    	}
    	
    	// look if the version is already installed
    	final PearPackageInfo info = pear.forceInstall(this.pearChannelAlias, this.pearPackage, this.pearPackageVersion);
        
        // setup module pom.xml
        final StringBuffer channels = new StringBuffer();
        final Set<String> knownChannels = new HashSet<String>();
        knownChannels.add(this.pearChannelAlias);
        channels.append("<channel>").append(this.pearChannelAlias).append("</channel>");
        for (final PearDependency dep : info.getRequiredDependencies())
        {
            final String depChannel = dep.getChannelName();
            if (!knownChannels.contains(depChannel))
            {
                knownChannels.add(depChannel);
                channels.append("\n                        <channel>").append(depChannel).append("</channel>");;
            }
        }
        for (final PearDependency dep : info.getOptionalDependencies())
        {
            final String depChannel = dep.getChannelName();
            if (!knownChannels.contains(depChannel))
            {
                knownChannels.add(depChannel);
                channels.append("\n                        <channel>").append(depChannel).append("</channel>");;
            }
        }
        
        final StringBuffer dependencies = new StringBuffer();
        if (!this.pearFetchDependencies)
        {
            // we create a pom that includes the dependencies
            dependencies.append("    <dependencies>\n");
            for (final PearDependency dep : info.getRequiredDependencies())
            {
                dependencies.append("        <dependency>\n");
                dependencies.append("            <groupId>").append(this.channelToGroupId(dep.getChannelName())).append("</groupId>\n");
                dependencies.append("            <artifactId>").append(dep.getPkgName()).append("</artifactId>\n");
                // pear dependencies to at least version 1.9.0 
                if (dep.getVersion() == null || dep.getChannelName().equalsIgnoreCase("pear.php.net") && dep.getPkgName().equalsIgnoreCase("PEAR")) {
                    final PearPackageInfo depInfo = pear.getPackageInfo(dep.getChannelName(), dep.getPkgName());
                    dependencies.append("            <version>").append(depInfo.getVersion()).append("</version>\n");
                }
                else {
                    dependencies.append("            <version>").append(dep.getVersion()).append("</version>\n");
                }
                dependencies.append("            <type>phar</type>\n");
                dependencies.append("        </dependency>\n");
            }
            for (final PearDependency dep : info.getOptionalDependencies())
            {
                dependencies.append("        <dependency>\n");
                dependencies.append("            <groupId>").append(this.channelToGroupId(dep.getChannelName())).append("</groupId>\n");
                dependencies.append("            <artifactId>").append(dep.getPkgName()).append("</artifactId>\n");
                if (dep.getVersion() == null) {
                    final PearPackageInfo depInfo = pear.getPackageInfo(dep.getChannelName(), dep.getPkgName());
                    dependencies.append("            <version>").append(depInfo.getVersion()).append("</version>\n");
                }
                else {
                    dependencies.append("            <version>").append(dep.getVersion()).append("</version>\n");
                }
                dependencies.append("            <type>phar</type>\n");
                dependencies.append("            <optional>true</optional>\n");
                dependencies.append("        </dependency>\n");
            }
            dependencies.append("    </dependencies>\n");
        }
        
        final StringBuffer etc = new StringBuffer();
        if (info.getDescription() != null)
        {
            etc.append("    <description>").append(escapeXml(info.getDescription())).append("</description>\n");
        }
        if (info.getLicenses().iterator().hasNext())
        {
            etc.append("    <licenses>\n");
            for (final PearLicense license : info.getLicenses())
            {
                etc.append("        <license>\n");
                if (license.getName() != null)
                {
                    etc.append("            <name>").append(escapeXml(license.getName())).append("</name>\n");
                }
                if (license.getUrl() != null)
                {
                    etc.append("            <url>").append(license.getUrl()).append("</url>\n");
                }
                etc.append("        </license>\n");
            }
            etc.append("    </licenses>\n");
        }
        if (info.getMaintainers().iterator().hasNext())
        {
            etc.append("    <developers>\n");
            for (final PearMaintainer maintainer : info.getMaintainers())
            {
                etc.append("        <developer>\n");
                if (maintainer.getName() != null)
                {
                    etc.append("            <name>").append(escapeXml(maintainer.getName())).append("</name>\n");
                }
                if (maintainer.getEmail() != null)
                {
                    etc.append("            <email>").append(escapeXml(maintainer.getEmail())).append("</email>\n");
                }
                if (maintainer.getRole() != null)
                {
                    etc.append("            <roles><role>").append(escapeXml(maintainer.getRole())).append("</role></roles>\n");
                }
                etc.append("        </developer>\n");
            }
            etc.append("    </developers>\n");
        }
        if (info.getReleaseDate() != null || info.getReleaseNotes() != null)
        {
            etc.append("    <properties>\n");
            if (info.getReleaseDate() != null)
            {
                etc.append("        <Pear.Release.Date>").append(escapeXml(info.getReleaseDate())).append("</Pear.Release.Date>\n");
            }
            if (info.getReleaseNotes() != null)
            {
                etc.append("        <Pear.Release.Notes>").append(escapeXml(info.getReleaseNotes())).append("</Pear.Release.Notes>\n");
            }
            etc.append("    </properties>\n");
        }
        
        String pomXml = POM_TEMPLATE.replace("${TARGET.GROUPID}", this.pearGroupId);
        pomXml = pomXml.replace("${TARGET.ARTIFACTID}", this.pearArtifactId);
        pomXml = pomXml.replace("${TARGET.NAME}", escapeXml(info.getSummary()) == null ? this.pearName : info.getSummary());
        pomXml = pomXml.replace("${TARGET.VERSION}", this.pearPackageMavenVersion);
        pomXml = pomXml.replace("${TARGET.PEARVERSION}", this.pearPackageVersion);
        pomXml = pomXml.replace("${TARGET.CHANNELS}", channels.toString());
        pomXml = pomXml.replace("${TARGET.CHANNELALIAS}", this.pearChannelAlias);
        pomXml = pomXml.replace("${TARGET.PACKAGENAME}", this.pearPackage);
        pomXml = pomXml.replace("${REPOS.ID}", this.deployRepsitoryId);
        pomXml = pomXml.replace("${REPOS.NAME}", escapeXml(this.deployRepositoryName));
        pomXml = pomXml.replace("${REPOS.URL}", this.deployRepositoryUrl);
        pomXml = pomXml.replace("${SNAPSHOTS.ID}", this.snapshotsRepositoryId);
        pomXml = pomXml.replace("${SNAPSHOTS.NAME}", escapeXml(this.snapshotsRepositoryName));
        pomXml = pomXml.replace("${SNAPSHOTS.URL}", this.snapshotsRepositoryUrl);
        pomXml = pomXml.replace("${DEPENDENCIES}", dependencies.toString());
        pomXml = pomXml.replace("${ETC}", etc.toString());
        pomXml = pomXml.replace("${TARGET.FETCHDEPS}", this.pearFetchDependencies ? "true" : "false");
        
        // write the pom.
        createPomFile(pomXml, this.pomTargetFile);
    }

    private void createPomFile(String pomXml, File target) throws MojoExecutionException {
        getLog().info("Writing pom file " + target);
        if (!target.getParentFile().exists())
        {
            if (!target.getParentFile().mkdirs())
            {
                throw new MojoExecutionException("Cannot create directories " + target);
            }
        }
        if (target.exists())
        {
            if (!target.delete())
            {
                throw new MojoExecutionException("Cannot delete existing pom file " + target);
            }
        }
        try
        {
            final FileOutputStream fos = new FileOutputStream(target);
            fos.write(pomXml.getBytes());
            fos.close();
        }
        catch (IOException ex)
        {
            throw new MojoExecutionException("Cannot write pom file " + target, ex);
        }
    }

    private String channelToGroupId(String channelName)
    {
        final String[] parts = channelName.split("\\.");
        final StringBuffer result = new StringBuffer();
        for (int i = parts.length - 1; i >= 0; i--)
        {
            if (i == 0 && parts[i].equals("pear"))
            {
                // skip leading "pear"
                continue;
            }
            if (result.length() > 0)
            {
                result.append(".");
            }
            result.append(parts[i]);
        }
        return result.toString();
    }
    
}
