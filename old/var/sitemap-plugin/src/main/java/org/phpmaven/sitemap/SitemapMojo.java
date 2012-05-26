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
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * SitemapMojo will produce a sitemap.xml and a zipped version sitemap.xml.gz file following the standard
 * http://www.sitemaps.org/protocol.php.
 * 
 * <p>
 * The parameters are:
 * </p>
 * <ul>
 * <li><b>input</b> - the site folder that was used for input. Defaults to ${project.basedir}/src/site</li>
 * <li><b>priority</b> - the default priority. Defaults to 0.8</li>
 * <li><b>changefreq</b> - The default change frequency. Defaults to weekly</li>
 * <li><b>urlSet</b> - The url set to declare alternative url definitions for the sitemap. Contains the following parameters:
 *   <ul>
 *   <li><b>loc</b> - The relative location of the content</li>
 *   <li><b>changefreq</b> - The changefreq of the content</li>
 *   <li><b>priorty</b> - The priority of the content</li>
 *   <li><b>src</b> - The source file/folder that is used to fetch the lastmod value.</li>
 *   </ul>
 *   </li>
 * <li><b>target</b> - The target folder to generate the files to. Defaults to ${project.reporting.outputDirectory}.</li>
 * </ul>
 * 
 * @author mepeisen
 * @goal generate 
 * 
 */
public class SitemapMojo extends AbstractMojo
{

    private static final SimpleDateFormat W3C_DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd" );

	/**
     * Filename Parameter for site.xml
     * 
     * @parameter expression="${input}" default-value="${project.basedir}/src/site"
     * @required
     */
    private File input;

    /**
     * Path for target sitemap.xml
     * 
     * @parameter expression="${target}" default-value="${project.reporting.outputDirectory}"
     * @required
     */
    private File target;
    
    /**
     * The default priority to be used
     * 
     * @parameter expression="${priority}" default-value="0.8"
     * @required
     */
    private double priority;
    
    /**
     * The default change ferquency
     * 
     * @parameter expression="${changeFreq}" default-value="weekly"
     * @required
     */
    private String changeFreq;
    
    /**
     * List of url sets for alternative declarations
     * 
     * @parameter
     */
    private List<UrlSet> urlSets = new ArrayList<UrlSet>();
    
    /**
     * @parameter default-value="${project}"
     */
    private MavenProject mavenProject;
    
    /**
     * Url set definition
     */
    public static final class UrlSet
    {
        
        /** the (relative) location of the url */
        private String loc;
        
        /** The change frequency */
        private String changefreq;
        
        /** the priority */
        private double priority;
        
        /** The file of the source file that should be used to fetch the last modification timestamp */
        private File src;

        public String getLoc() {
            return loc;
        }

        public void setLoc(String loc) {
            this.loc = loc;
        }

        public String getChangefreq() {
            return changefreq;
        }

        public void setChangefreq(String changefreq) {
            this.changefreq = changefreq;
        }

        public double getPriority() {
            return priority;
        }

        public void setPriority(double priority) {
            this.priority = priority;
        }

        public File getSrc() {
            return src;
        }

        public void setSrc(File src) {
            this.src = src;
        }
        
    }

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
        target = new File(target, "sitemap.xml");
        
        // populate with individual files
        final Map<String, UrlSet> urls = new HashMap<String, UrlSet>();
        for (final UrlSet url : this.urlSets)
        {
        	urls.put(url.loc, url);
        }
        
        // populate with generated files
        if (input != null && input.exists())
        {
            populateWithDocuments(urls, "apt", ".apt");
            populateWithDocuments(urls, "fml", ".fml");
            populateWithDocuments(urls, "xdoc", ".xml");
        }
        
        // populate with generated reports
        populateWithReports(urls);

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
        
        Element root = xmldoc.createElement( "urlset" );
        root.setAttribute( "xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9" );
        
        addUrlToSitemap(xmldoc, "", new Date(), this.changeFreq, this.priority, root);
        
        for (final UrlSet url : urls.values()) {
            final String strLocation = url.getLoc();
            final Date datLastChange = this.getDateFromLocation(url.getSrc());
            final String strChange = url.getChangefreq();
            final double decPriority = url.getPriority();
            addUrlToSitemap(xmldoc, strLocation, datLastChange, strChange, decPriority, root);
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
    
    private Date getDateFromLocation(File src) {
    	if (src.isDirectory()) {
    		return new Date(this.getMaxDateFromFolder(src));
    	}
		return new Date(src.lastModified());
	}
    
    private long getMaxDateFromFolder(File src) {
    	long result = src.lastModified();
		for (final File child : src.listFiles()) {
			if (child.isDirectory() && !child.getName().equals(".") && !child.getName().equals("..")) {
				final long res2 = getMaxDateFromFolder(child);
				if (res2 > result) {
					result = res2;
				}
			}
			else if (child.isFile()) {
				final long res2 = child.lastModified();
				if (res2 > result) {
					result = res2;
				}
			}
		}
		return result;
	}

	private void addUrlToSitemap(Document xmldoc, final String strLocation,
			final Date datLastChange, final String strChange,
			final double decPriority, Element root) {
		Element url = xmldoc.createElement( "url" );
		Element location = xmldoc.createElement( "loc" );
		location.appendChild( xmldoc.createTextNode( this.mavenProject.getUrl() + strLocation ) );
		url.appendChild( location );
		Element lastmod = xmldoc.createElement( "lastmod" );
		lastmod.appendChild( xmldoc.createTextNode( W3C_DATE_FORMAT.format( datLastChange ) ) );
		url.appendChild( lastmod );
		if (strChange != null) {
			Element changefreq = xmldoc.createElement("changefreq");
		    changefreq.appendChild( xmldoc.createTextNode(strChange) );
		    url.appendChild( changefreq );
		}
		if (decPriority != 0) {
			Element priority = xmldoc.createElement("priority");
		    priority.appendChild( xmldoc.createTextNode(String.valueOf(decPriority)) );
		    url.appendChild( priority );
		}
		root.appendChild( url );
	}

    /**
     * Populates the site map documents with those from src/site/**
     * @param urls
     * @param folderName
     * @param extension
     */
	private void populateWithDocuments(final Map<String, UrlSet> urls,
			final String folderName, final String extension) {
		final File folder = new File(this.input, folderName);
		if (!folder.exists()) return;
		for (final File file : folder.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(extension) || name.endsWith(extension + ".vm");
			}
		}))
		{
			String loc = file.getName();
			if (loc.endsWith(extension))
			{
				loc = loc.substring(0, loc.length() - extension.length()) + ".html";
			}
			else if (loc.endsWith(extension + ".vm"))
			{
				loc = loc.substring(0, loc.length() - extension.length() - 3) + ".html";
			}
			addToUrls(urls, file, loc);
		}
	}

	/**
	 * Adds a file to the url set
	 * @param urls
	 * @param file
	 * @param loc
	 */
	private void addToUrls(final Map<String, UrlSet> urls, final File file, String loc) {
		if (!urls.containsKey(loc)) {
			final UrlSet url = new UrlSet();
			url.setLoc(loc);
			url.setSrc(file);
			urls.put(loc, url);
		}
	}
	
	/**
	 * Populates the site map documents with those from reports
	 * @param urls
	 */
	private void populateWithReports(final Map<String, UrlSet> urls) {
		for (final Plugin plugin : this.mavenProject.getBuild().getPlugins()) {
			if (plugin.getArtifactId().equals("maven-site-plugin")) {
				final Xpp3Dom config = (Xpp3Dom) plugin.getConfiguration();
				final Xpp3Dom reportPlugins = config.getChild("reportPlugins");
				if (reportPlugins != null) {
					for (final Xpp3Dom reportPlugin : reportPlugins.getChildren("plugin")) {
						final String artifactId = reportPlugin.getChild("artifactId").getValue();
						final File pom = this.mavenProject.getFile();
						if (artifactId.equals("maven-project-info-reports-plugin")) {
							this.addToUrls(urls, pom, "dependencies.html");
							this.addToUrls(urls, pom, "dependency-convergence.html");
							this.addToUrls(urls, pom, "distibution-management.html");
							this.addToUrls(urls, pom, "integration.html");
							this.addToUrls(urls, pom, "issue-tracking.html");
							this.addToUrls(urls, pom, "license.html");
							this.addToUrls(urls, pom, "mail-lists.html");
							this.addToUrls(urls, pom, "plugin-management.html");
							this.addToUrls(urls, pom, "plugins.html");
							this.addToUrls(urls, pom, "project-info.html");
							this.addToUrls(urls, pom, "project-summary.html");
							this.addToUrls(urls, pom, "source-repository.html");
							this.addToUrls(urls, pom, "usage.html");
						}
						else if (artifactId.equals("maven-checkstyle-plugin")) {
							this.addToUrls(urls, new File(this.mavenProject.getBuild().getSourceDirectory()), "checkstyle.html");
						}
						else if (artifactId.equals("maven-dependency-plugin")) {
							this.addToUrls(urls, pom, "dependency-analysis.html");
						}
						else if (artifactId.equals("maven-javadoc-plugin")) {
							final File srcFolder = new File(this.mavenProject.getBuild().getSourceDirectory());
							this.addToUrls(urls, srcFolder, "apidocs/allclasses-frame.html");
							this.addToUrls(urls, srcFolder, "apidocs/allclasses-noframe.html");
							this.addToUrls(urls, srcFolder, "apidocs/constant-values.html");
							this.addToUrls(urls, srcFolder, "apidocs/deprecated-list.html");
							this.addToUrls(urls, srcFolder, "apidocs/help-doc.html");
							this.addToUrls(urls, srcFolder, "apidocs/index-all.html");
							this.addToUrls(urls, srcFolder, "apidocs/index.html");
							this.addToUrls(urls, srcFolder, "apidocs/overview-tree.html");
							this.addToUrls(urls, srcFolder, "apidocs/serialized-form.html");
							this.populateWithJavadoc(urls, srcFolder, "apidocs/");
						}
						else if (artifactId.equals("maven-jxr-plugin")) {
							final File srcFolder = new File(this.mavenProject.getBuild().getSourceDirectory());
							this.addToUrls(urls, srcFolder, "xref/allclasses-frame.html");
							this.addToUrls(urls, srcFolder, "xref/index.html");
							this.addToUrls(urls, srcFolder, "xref/overview-frame.html");
							this.addToUrls(urls, srcFolder, "xref/overview-summary.html");
							this.populateWithXref(urls, srcFolder, "xref/");
						}
						else if (artifactId.equals("maven-pmd-plugin")) {
							this.addToUrls(urls, pom, "pmd.html");
						}
						else if (artifactId.equals("maven-surefire-report-plugin")) {
							this.addToUrls(urls, pom.getParentFile(), "surefire-report.html");
						}
						else if (artifactId.equals("taglist-maven-plugin")) {
							this.addToUrls(urls, pom, "taglist.html");
						}
						else if (artifactId.equals("findbugs-maven-plugin")) {
							this.addToUrls(urls, pom, "findbugs.html");
						}
					}
				}
			}
		}
	}

    private void populateWithJavadoc(Map<String, UrlSet> urls, File srcFolder,
			String prefix) {
    	boolean packageAdded = false;
    	if (srcFolder.isDirectory() && srcFolder.exists()) {
    		for (final File child : srcFolder.listFiles()) {
    			if (child.isDirectory() && !child.getName().equals(".") && !child.getName().equals("..")) {
    				this.populateWithJavadoc(urls, child, prefix + child.getName() + "/");
    			}
    			else if (child.isFile() && child.getName().endsWith(".java")) {
    				if (!packageAdded) {
    					this.addToUrls(urls, srcFolder, prefix + "package-frame.html");
    					this.addToUrls(urls, srcFolder, prefix + "package-summary.html");
    					this.addToUrls(urls, srcFolder, prefix + "package-tree.html");
    					this.addToUrls(urls, srcFolder, prefix + "package-use.html");
    					packageAdded = true;
    				}
    				this.addToUrls(urls, child, prefix + child.getName().substring(0, child.getName().length() - 5) + ".html");
    			}
    		}
    	}
	}

    private void populateWithXref(Map<String, UrlSet> urls, File srcFolder,
			String prefix) {
    	boolean packageAdded = false;
    	if (srcFolder.isDirectory() && srcFolder.exists()) {
    		for (final File child : srcFolder.listFiles()) {
    			if (child.isDirectory() && !child.getName().equals(".") && !child.getName().equals("..")) {
    				this.populateWithXref(urls, child, prefix + child.getName() + "/");
    			}
    			else if (child.isFile() && child.getName().endsWith(".java")) {
    				if (!packageAdded) {
    					this.addToUrls(urls, srcFolder, prefix + "package-frame.html");
    					this.addToUrls(urls, srcFolder, prefix + "package-summary.html");
    					packageAdded = true;
    				}
    				this.addToUrls(urls, child, prefix + child.getName().substring(0, child.getName().length() - 5) + ".html");
    			}
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
