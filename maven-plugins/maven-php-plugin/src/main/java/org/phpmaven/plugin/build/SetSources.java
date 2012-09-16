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
package org.phpmaven.plugin.build;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * Sets the source folders to php-maven sepcification (src/main/php and src/test/php).
 * 
 * XXX: This seems to be a very bad hack. It copies some stuff from DefaultProjectBuilder in order to analyze the poms
 * itself without having the parent poms influence them. This seems to be the only way to detect if the source folders
 * are inherited from super pom or if they are explicitly set in the projects pom or one of its parent. 
 * 
 * @author Martin Eisengardt
 * @goal set-sources
 */
public class SetSources extends AbstractMojo {

    /**
     * The Maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * The project's base directory.
     *
     * @parameter expression="${project.basedir}"
     * @required
     * @readonly
     */
    private File baseDir;

    /**
     * The project's base directory.
     *
     * @return the project's basedir
     */
    public File getBaseDir() {
        return baseDir;
    }
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final List<String> sourceFolders = this.project.getCompileSourceRoots();
        final File srcJava = new File(this.getBaseDir(), "src/main/java");
        if (sourceFolders.contains(srcJava.getAbsolutePath())) {
            sourceFolders.remove(srcJava.getAbsolutePath());
            sourceFolders.add(new File(this.getBaseDir(), "src/main/php").getAbsolutePath());
        }
        getLog().debug(this.project.getCompileSourceRoots().toString());
        
        final List<String> testFolders = this.project.getTestCompileSourceRoots();
        final File srcTest = new File(this.getBaseDir(), "src/test/java");
        if (testFolders.contains(srcTest.getAbsolutePath())) {
            testFolders.remove(srcTest.getAbsolutePath());
            testFolders.add(new File(this.getBaseDir(), "src/test/php").getAbsolutePath());
        }
        getLog().debug(this.project.getTestCompileSourceRoots().toString());
    }
    
//    /**
//     * The project builder
//     */
//    @Requirement
//    private ProjectBuilder projectBuilder;
//    
//    @Requirement
//    private ArtifactFactory artifactFactory;
//    
//    @Requirement
//    private MavenTools mavenTools;
//    
//    @Override
//    public void execute() throws MojoExecutionException, MojoFailureException {
//        // do only set the sources if they are not overwritten by pom;
//        // we need to walk through the poms to see if there is anyone overwriting the source folder.
//        // if we ot the source folder from the super pom we do overwrite the source folders
//        final File pomFile = this.project.getModel().getPomFile();
//        final Model superModel = this.projectBuilder.getSuperModel();
//        
//        boolean overwriteSources = true;
//        boolean overwriteTestSources = true;
//        
//        try {
//            final PomClassicDomainModel domainModel = new PomClassicDomainModel(pomFile);
//            domainModel.setProjectDirectory(pomFile.getParentFile());
//            getLog().info("Analyzing pom " + pomFile);
//            
//            if (domainModel.getModel().getBuild().getSourceDirectory() != null) {
//                getLog().info("Overwrite source folder in " + pomFile + ". ignoring default.");
//                overwriteSources = false;
//            }
//            
//            if (domainModel.getModel().getBuild().getTestSourceDirectory() != null) {
//                getLog().info("Overwrite test source folder in " + pomFile + ". ignoring default.");
//                overwriteTestSources = false;
//            }
//            
//            if ( isParentLocal( domainModel.getModel().getParent(), domainModel.getParentFile() ) )
//            {
//                mavenParents = getDomainModelParentsFromLocalPath( domainModel, resolver, pomFile.getParentFile() );
//            }
//            else
//            {
//                mavenParents = getDomainModelParentsFromRepository( domainModel, resolver );
//            }
//            
//            if ( mavenParents.size() > 0 )
//            {
//                PomClassicDomainModel dm = (PomClassicDomainModel) mavenParents.get( 0 );
//                parentFile = dm.getFile();
//                domainModel.setParentFile( parentFile );
//                lineageCount = mavenParents.size();
//            }
//            
//        } catch (IOException ex) {
//            throw new MojoExecutionException("Problems reading the project model.", ex);
//        }
//        
//        project.getBuild();
//        final List<String> sources = this.project.getScriptSourceRoots();
//    }
//
//    /**
//     * Returns true if the relative path of the specified parent references a pom, otherwise returns false.
//     *
//     * @param parent           the parent model info
//     * @param projectDirectory the project directory of the child pom
//     * @return true if the relative path of the specified parent references a pom, otherwise returns fals
//     */
//    private boolean isParentLocal( Parent parent, File projectDirectory ) {
//        try {
//            File f = new File( projectDirectory, parent.getRelativePath() ).getCanonicalFile();
//            
//            if ( f.isDirectory() ) {
//                f = new File( f, "pom.xml" );
//            }
//            
//            return f.isFile();
//        } catch ( IOException e ) {
//            return false;
//        }
//    }
//
//    private List<DomainModel> getDomainModelParentsFromRepository( PomClassicDomainModel domainModel,
//                                                                   PomArtifactResolver artifactResolver )
//        throws IOException
//    {
//        List<DomainModel> domainModels = new ArrayList<DomainModel>();
//
//        Parent parent = domainModel.getModel().getParent();
//
//        if ( parent == null )
//        {
//            return domainModels;
//        }
//
//        Artifact artifactParent = artifactFactory.createParentArtifact( parent.getGroupId(),
//    parent.getArtifactId(), parent.getVersion() );
//        
//        artifactResolver.resolve( artifactParent );
//
//        PomClassicDomainModel parentDomainModel = new PomClassicDomainModel( artifactParent.getFile() );
//
//        if ( !parentDomainModel.matchesParent( domainModel.getModel().getParent() ) )
//        {
//            logger.debug( "Parent pom ids do not match: Parent File = " + artifactParent.getFile().getAbsolutePath() +
//                ": Child ID = " + domainModel.getModel().getId() );
//            return domainModels;
//        }
//
//        domainModels.add( parentDomainModel );
//        domainModels.addAll( getDomainModelParentsFromRepository( parentDomainModel, artifactResolver ) );
//        return domainModels;
//    }
//
//    /**
//     * Returns list of domain model parents of the specified domain model. The parent domain models are part
//     *
//     * @param domainModel
//     * @param artifactResolver
//     * @param projectDirectory
//     * @return
//     * @throws IOException
//     */
//    private List<DomainModel> getDomainModelParentsFromLocalPath( PomClassicDomainModel domainModel,
//                                                                  PomArtifactResolver artifactResolver,
//                                                                  File projectDirectory )
//        throws IOException
//    {
//        List<DomainModel> domainModels = new ArrayList<DomainModel>();
//
//        Parent parent = domainModel.getModel().getParent();
//
//        if ( parent == null )
//        {
//            return domainModels;
//        }
//
//        Model model = domainModel.getModel();
//
//        File parentFile = new File( projectDirectory, model.getParent().getRelativePath() ).getCanonicalFile();
//        if ( parentFile.isDirectory() )
//        {
//            parentFile = new File( parentFile.getAbsolutePath(), "pom.xml" );
//        }
//
//        if ( !parentFile.isFile() )
//        {
//            throw new IOException( "File does not exist: File = " + parentFile.getAbsolutePath() );
//        }
//
//        PomClassicDomainModel parentDomainModel = new PomClassicDomainModel( parentFile );
//        parentDomainModel.setProjectDirectory( parentFile.getParentFile() );
//
//        if ( !parentDomainModel.matchesParent( domainModel.getModel().getParent() ) )
//        {
//            logger.debug( "Parent pom ids do not match: Parent File = " +
//    parentFile.getAbsolutePath() + ", Parent ID = "
//                    + parentDomainModel.getId() + ", Child ID = " + domainModel.getId() + ", Expected Parent ID = "
//                    + domainModel.getModel().getParent().getId() );
//            
//            List<DomainModel> parentDomainModels = getDomainModelParentsFromRepository(
//    domainModel, artifactResolver );
//            
//            if(parentDomainModels.size() == 0)
//            {
//                throw new IOException("Unable to find parent pom on local path or repo: "
//                        + domainModel.getModel().getParent().getId());
//            }
//            
//            domainModels.addAll( parentDomainModels );
//            return domainModels;
//        }
//
//        domainModels.add( parentDomainModel );
//        if ( parentDomainModel.getModel().getParent() != null )
//        {
//            if ( isParentLocal( parentDomainModel.getModel().getParent(), parentFile.getParentFile() ) )
//            {
//                domainModels.addAll( getDomainModelParentsFromLocalPath( parentDomainModel, artifactResolver,
//                                                                         parentFile.getParentFile() ) );
//            }
//            else
//            {
//                domainModels.addAll( getDomainModelParentsFromRepository( parentDomainModel, artifactResolver ) );
//            }
//        }
//
//        return domainModels;
//    }
    
    

}
