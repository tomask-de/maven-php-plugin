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

package org.phpmaven.sitemap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * SitemapIndexMojo will produce a sitemap_index.xml and a zipped version sitemap_index.xml.gz file following the standard
 * http://www.sitemaps.org/protocol.php.
 * 
 * <p>
 * The parameters are:
 * </p>
 * <ul>
 * <li><b>modules</b> - the modules that are generating a sitemap.</li>
 * <li><b>target</b> - The target folder to generate the files to. Defaults to ${project.reporting.outputDirectory}.</li>
 * </ul>
 * 
 * @author mepeisen
 * @goal generate-index
 * 
 */
public class SitemapIndexMojo extends AbstractMojo
{

    private static final SimpleDateFormat W3C_DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd" );

    private static final String W3C_DATE = W3C_DATE_FORMAT.format(new Date());

    /**
     * Path for target sitemap.xml
     * 
     * @parameter expression="${target}" default-value="${project.reporting.outputDirectory}"
     * @required
     */
    private File target;
    
    /**
     * List of modules
     * 
     * @parameter
     */
    private List<File> modules = new ArrayList<File>();
    
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
     * The Maven session.
     *
     * @parameter expression="${session}"
     * @readonly
     * @required
     * @since 1.0-beta-2
     */
    private MavenSession session;

    /**
     * Generating the sitemap.xml
     */
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        
        // create the target if needed
        if (!target.exists())
        {
            target.mkdirs();
        }
        target = new File(target, "sitemap_index.xml");
        
        final List<String> urls = new ArrayList<String>();
        try {
            parseProject(urls, this.mavenProject);
            for (final File module : this.modules) {
            	final File pomFile = new File(module, "pom.xml");
            	if (pomFile.exists()) {
            		parseProject(urls, this.getProjectFromPom(pomFile));
            	}
            }
        }
        catch (ProjectBuildingException e) {
        	throw new MojoExecutionException( "ProjectBuildingException", e );
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try
        {
            db = dbf.newDocumentBuilder();
        }
        catch ( ParserConfigurationException e )
        {
            throw new MojoExecutionException( "ParserConfigurationException", e );

        }
        Document xmldoc = db.newDocument();
        
        Element root = xmldoc.createElement( "sitemapindex" );
        root.setAttribute( "xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9" );
        
        for (final String url : urls) {
            if (url == null) {
                continue;
            }
        	Element sitemap = xmldoc.createElement( "sitemap" );
    		Element location = xmldoc.createElement( "loc" );
    		location.appendChild( xmldoc.createTextNode( url + "sitemap.xml.gz" ) );
    		sitemap.appendChild( location );
    		Element lastmod = xmldoc.createElement( "lastmod" );
    		lastmod.appendChild( xmldoc.createTextNode( W3C_DATE ) );
    		sitemap.appendChild( lastmod );
    		root.appendChild( sitemap );
        }

        xmldoc.appendChild( root );
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = null;
        try
        {
            transformer = tf.newTransformer();
        }
        catch ( TransformerConfigurationException e )
        {
            throw new MojoExecutionException( "goal failed", e );
        }

        DOMSource source = new DOMSource( xmldoc );
        StreamResult result = new StreamResult( target );

        try
        {
            transformer.transform( source, result );

        }
        catch ( TransformerException e )
        {
            throw new MojoExecutionException( "goal failed", e );
        }

        // now gzip the file
        gzipFile( target );

    }
    
    protected MavenProject getProjectFromPom(final File pom) throws ProjectBuildingException {
        final ProjectBuildingRequest request = session.getProjectBuildingRequest();
        request.setLocalRepository(session.getLocalRepository());
        request.setRemoteRepositories(this.mavenProject.getRemoteArtifactRepositories());
        return this.mavenProjectBuilder.build(pom, request).getProject();
    }

	private void parseProject(final List<String> urls, final MavenProject project) throws ProjectBuildingException {
		for (final Plugin plugin : project.getBuild().getPlugins()) {
			if (plugin.getArtifactId().equals("sitemap-plugin") && plugin.getGroupId().equals("org.phpmaven.sites")) {
				urls.add(project.getUrl());
			}
		}
		
		for (final String module : project.getModules()) {
        	final File moduleFolder = new File(project.getBasedir(), module);
        	final File pomFile = new File(moduleFolder, "pom.xml");
        	if (pomFile.exists()) {
        		this.parseProject(urls, this.getProjectFromPom(pomFile));
        	}
        }
	}

	/**
     * This method generates a gzipped file of the source file. <br>
     * The gzipped file is named sourceFilename.gz.<br>
     * It will be generated in the same directory.
     * 
     * @param source File that should get gzipped
     */
    private void gzipFile( File source )
        throws MojoExecutionException
    {
        File gzipfile = new File( source.getAbsoluteFile() + ".gz" );
        GZIPOutputStream gzipoutputstream = null;
        int bufferSize = 8192;
        byte[] buffer = new byte[bufferSize];
        // Create GZIP Stream
        try
        {
            FileOutputStream outputstream = new FileOutputStream( gzipfile );
            gzipoutputstream = new GZIPOutputStream( outputstream );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "goal failed", e );
        }

        // GZIP the file
        try
        {
            FileInputStream inputstream = new FileInputStream( source );
            int length;
            while ( ( length = inputstream.read( buffer, 0, bufferSize ) ) != -1 )
            {
                gzipoutputstream.write( buffer, 0, length );
            }
            inputstream.close();
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "goal failed", e );
        }
        // Close Stream
        try
        {
            gzipoutputstream.close();
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "goal failed", e );
        }

    }

}
