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
import java.io.FileWriter;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.phpmaven.plugin.build.AbstractPhpMojo;

/**
 * executes a php command.
 *
 * @goal exec
 * @author Martin Eisengardt
 */
public final class PhpExec extends AbstractPhpMojo {
    
    /**
     * The php file to be executed.
     * 
     * @parameter expression="${phpFile}"
     * @required
     */
    private File phpFile;
    
    /**
     * True to include the test dependencies into include path.
     * 
     * @parameter default-value="false" expression="${testIncludePath}"
     */
    private boolean testIncludePath;
    
    /**
     * True to include the standard compile dependencies into include path.
     * 
     * @parameter default-value="true" expression="${compileIncludePath}"
     */
    private boolean compileIncludePath;
    
    /**
     * Command line arguments for the php file to be executed.
     * 
     * @parameter expression="${additionalPhpFileArguments}"
     */
    private String phpFileArguments;
    
    /**
     * Print php-stdout to given file. If this is not specified the stdout will be logged as info.
     * 
     * @parameter expression="${phpOutToFile}"
     */
    private File phpOutToFile;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        String commandLine = "";
        if (this.compileIncludePath) {
            commandLine = getPhpHelper().defaultIncludePath(null) + " ";
        } else if (this.testIncludePath) {
            commandLine = getPhpHelper().defaultTestIncludePath(null) + " ";
        }
        commandLine += "\"" + this.phpFile.getAbsolutePath() + "\"";
        if (this.phpFileArguments != null && this.phpFileArguments.length() > 0) {
            commandLine += " " + this.phpFileArguments;
        }
        getLog().info("Executing php: " + commandLine);
        try {
            final String result = getPhpHelper().execute(commandLine, this.phpFile);
            if (this.phpOutToFile != null) {
                if (!phpOutToFile.getParentFile().exists()) {
                    phpOutToFile.getParentFile().mkdirs();
                }
                final FileWriter writer = new FileWriter(this.phpOutToFile);
                writer.write(result);
            } else {
                getLog().info("Result:\n" + result);
            }
        } catch (PhpException ex) {
            throw new MojoFailureException("Failed executing php command", ex);
        } catch (IOException ex) {
            throw new MojoFailureException("Failed executing php command", ex);
        }
    }
    

    
}
