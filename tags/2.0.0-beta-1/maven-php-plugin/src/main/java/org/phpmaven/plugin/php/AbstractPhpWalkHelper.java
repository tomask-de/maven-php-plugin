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

package org.phpmaven.plugin.php;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.DirectoryWalkListener;
import org.codehaus.plexus.util.DirectoryWalker;

import com.google.common.collect.Lists;

/**
 * A walker that is able to walk through the source directories.
 * 
 * @author Christian Wiedemann
 * @author Tobias Sarnowski
 * @author Martin Eisengardt
 */
public abstract class AbstractPhpWalkHelper implements DirectoryWalkListener {
    
    /**
     * The logging delegate.
     */
    private Log log;

    /**
     * Files and directories to exclude.
     */
    private String[] excludes = new String[0];

    /**
     * Files and directories to include.
     */
    private String[] includes = new String[0];

    /**
     * collects all exceptions during the file walk.
     */
    private List<Exception> collectedExceptions = Lists.newArrayList();

    /**
     * How php files will be identified after the last point.
     */
    private String phpFileEnding;

    /**
     * Constructor.
     * 
     * @param config The mojo configuration.
     */
    public AbstractPhpWalkHelper(IPhpWalkConfigurationMojo config) {
        this.log = config.getLog();
        this.excludes = config.getExcludes();
        this.includes = config.getIncludes();
        this.phpFileEnding = config.getPhpFileEnding();
    }
    
    /**
     * Triggers the walk process.
     *
     * @param parentFolder the folder to start in
     * @throws MultiException every catched exception collected during the walk
     */
    public final void goRecursiveAndCall(File parentFolder) throws MultiException {
        if (!parentFolder.isDirectory()) {
            this.log.error("Source directory (" + parentFolder.getAbsolutePath() + ")");
            return;
        }

        final DirectoryWalker walker = new DirectoryWalker();

        walker.setBaseDir(parentFolder);
        walker.addDirectoryWalkListener(this);
        walker.addSCMExcludes();

        for (String exclude : excludes) {
            walker.addExclude(exclude);
        }
        for (String include : includes) {
            walker.addInclude(include);
        }

        // new list
        collectedExceptions = Lists.newArrayList();

        // do the action
        walker.scan();

        if (collectedExceptions.size() != 0) {
            throw new MultiException(collectedExceptions);
        }
    }

    /**
     * Nessecary for the DirectoryWalker, do not use.
     *
     * @param message message to log
     * @deprecated use getLog() instead
     */
    @Override
    @Deprecated
    public void debug(String message) {
        this.log.debug(message);
    }

    /**
     * Forced to implement by the {@link org.codehaus.plexus.util.DirectoryWalkListener}.
     *
     * {@inheritDoc}
     */
    @Override
    public void directoryWalkFinished() {
        /* ignore */
    }

    /**
     * Forced to implement by the {@link org.codehaus.plexus.util.DirectoryWalkListener}.
     *
     * {@inheritDoc}
     */
    @Override
    public void directoryWalkStarting(File basedir) {
        /* ignore */
    }

    /**
     * Callback for executing a file.
     *
     * @param file the PHP file to execute
     * @throws MojoExecutionException if something goes wrong during the execution
     */
    protected abstract void handlePhpFile(File file) throws MojoExecutionException;

    /**
     * Callback for file processing.
     *
     * @param file the PHP file to process
     * @throws MojoExecutionException if something goes wrong during the execution
     */
    protected abstract void handleProcessedFile(File file) throws MojoExecutionException;

    /**
     * Will be triggered for every file in the directory.
     *
     * {@inheritDoc}
     */
    @Override
    public void directoryWalkStep(int percentage, File file) {
        try {
            if (file.isFile() && file.getName().endsWith("." + this.phpFileEnding))
                handlePhpFile(file);
            if (file.isFile())
                handleProcessedFile(file);
        /*CHECKSTYLE:OFF*/
        } catch (Exception e) {
        /*CHECKSTYLE:ON*/
            log.debug(e);
            collectedExceptions.add(e);
        }
    }

}
