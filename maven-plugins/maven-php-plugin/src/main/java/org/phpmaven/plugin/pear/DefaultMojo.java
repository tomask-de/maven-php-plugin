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

/*                                                                        *
 * This file belongs to the Dev3 maven utilities.                         *
 *                                                                        *
 * It is free software; you can redistribute it and/or modify it under    *
 * the terms of the GNU Lesser General Public License as published by the *
 * Free Software Foundation, either version 3 of the License, or (at your *
 * option) any later version.                                             *
 *                                                                        *
 * This script is distributed in the hope that it will be useful, but     *
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHAN-    *
 * TABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser       *
 * General Public License for more details.                               *
 *                                                                        *
 * You should have received a copy of the GNU Lesser General Public       *
 * License along with the script.                                         *
 * If not, see http://www.gnu.org/licenses/lgpl.html                      *
 *                                                                        *
 * The TYPO3 project - inspiring people to share!                         *
 *                                                                        */

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;


// http://www.sonatype.com/books/mvnref-book/reference/public-book.html

/**
 * Mojo Base class.
 * TODO migrate
 * 
 * @author mepeisen
 */
abstract public class DefaultMojo extends AbstractMojo
{
	
    /**
     * Location of the build directory.
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File buildDirectory;
    
    /**
     * Location of the output directory.
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     */
    private File outputDirectory;
    
    /**
     * Location of the source directory.
     * @parameter expression="${project.build.sourceDirectory}"
     * @required
     */
    private File sourceDirectory;
    
    /**
     * Location of the test output directory.
     * @parameter expression="${project.build.testOutputDirectory}"
     * @required
     */
    private File testOutputDirectory;
    
    /**
     * Location of the test source directory.
     * @parameter expression="${project.build.testSourceDirectory}"
     * @required
     */
    private File testSourceDirectory;
    
    /**
     * @parameter default-value="${project}"
     */
    private MavenProject mavenProject;

	/**
	 * @component
	 * @required
	 */
    private ProjectBuilder mavenProjectBuilder;
    
	/**
	 * @parameter expression="${localRepository}"
	 * @required
	 */
    protected ArtifactRepository localRepository;
    
    /**
     * The Maven session.
     *
     * @parameter expression="${session}"
     * @readonly
     * @required
     * @since 1.0-beta-2
     */
    private MavenSession session;

    
    /**
     * Returns the build directory
     * @return build directory
     */
    protected File getBuildDirectory()
    {
    	return this.buildDirectory;
    }
    
    /**
     * Returns the maven project
     * @return maven project
     */
    protected MavenProject getMavenProject()
    {
    	return this.mavenProject;
    }
    
    protected MavenProject getProjectFromArtifact(final Artifact a) throws ProjectBuildingException
    {
        final ProjectBuildingRequest request = session.getProjectBuildingRequest();
        request.setLocalRepository(session.getLocalRepository());
        request.setRemoteRepositories(this.getMavenProject().getRemoteArtifactRepositories());
        return this.mavenProjectBuilder.build(a, request).getProject();
    }
    
    protected MavenProject getProjectFromPom(final File pom) throws ProjectBuildingException
    {
        final ProjectBuildingRequest request = session.getProjectBuildingRequest();
        request.setLocalRepository(session.getLocalRepository());
        request.setRemoteRepositories(this.getMavenProject().getRemoteArtifactRepositories());
        return this.mavenProjectBuilder.build(pom, request).getProject();
    }
    
    
    
    // *** phar generation
    
    /**
     * The php package file name
     * @parameter default-value="packagePhar.php"
     */
    protected String packagePhpFilename;

    /**
     * Returns the phar file name
     * 
     * @return phar file name
     */
    protected String getPharFilename()
    {
    	return this.getMavenProject().getBuild().getFinalName() + ".phar"; //$NON-NLS-1$
    }
    /**
     * Returns the zip file name
     * 
     * @return zip file name
     */
    protected String getZipFilename()
    {
    	return this.getMavenProject().getBuild().getFinalName() + ".zip"; //$NON-NLS-1$
    }
    
    /**
     * Returns the output directory
     * 
     * @return file
     */
    protected File getOutputDirectory()
    {
    	return this.outputDirectory;
    }
    
    /**
     * Returns the test output directory
     * 
     * @return file
     */
    protected File getTestOutputDirectory()
    {
    	return this.testOutputDirectory;
    }
    
    /**
     * Returns the source directory
     * 
     * @return file
     */
    protected File getSourceDirectory()
    {
    	return this.sourceDirectory;
    }
    
    /**
     * Returns the test source directory
     * 
     * @return file
     */
    protected File getTestSourceDirectory()
    {
    	return this.testSourceDirectory;
    }

    
    
    // *** file copy
    
    protected void copyFile(File from, File to) throws IOException
    {
		if ( to.lastModified() < from.lastModified() )
	    {
			this.getLog().debug("copying " + from + " to " + to); //$NON-NLS-1$ //$NON-NLS-2$
	        FileUtils.copyFile( from, to );
	    }
	}

	protected void copyDirectory(File targetFolder, File sourceFolder, DirectoryScanner scanner)
			throws MojoExecutionException {
				scanner.scan();
			    
			    List<String> includedFiles = Arrays.asList( scanner.getIncludedFiles() );
			    for ( Iterator<String> j = includedFiles.iterator(); j.hasNext(); )
			    {
			        String name = j.next();
			
			        String destination = name;
			
			        File source = new File( sourceFolder, name );
			
			        File destinationFile = new File(targetFolder, destination );
			
			        if ( !destinationFile.getParentFile().exists() )
			        {
			            destinationFile.getParentFile().mkdirs();
			        }
			
			        try
			        {
			            copyFile( source, destinationFile );
			        }
			        catch ( IOException e )
			        {
			            throw new MojoExecutionException( "Error copying resources", e ); //$NON-NLS-1$
			        }
			    }
			}
    
    
    
	protected void reportSection(String section)
	{
		final StringBuffer buffer = new StringBuffer("\n******"); //$NON-NLS-1$
		for (int i = 0; i < section.length(); i++) buffer.append("*"); //$NON-NLS-1$
		buffer.append("******\n***** ").append(section).append(" *****\n******"); //$NON-NLS-1$ //$NON-NLS-2$
		for (int i = 0; i < section.length(); i++) buffer.append("*"); //$NON-NLS-1$
		buffer.append("******"); //$NON-NLS-1$
		getLog().info(buffer);
	}
	
	protected String getPackageKey(final MavenProject prj)
	{
		final String prop = prj.getProperties().getProperty("flow3.ns");
		if (prop != null) return prop;
		return "F3."+prj.getArtifactId();
	}

}
