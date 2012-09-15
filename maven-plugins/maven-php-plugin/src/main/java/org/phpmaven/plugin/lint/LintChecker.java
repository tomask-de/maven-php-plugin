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

package org.phpmaven.plugin.lint;

import org.apache.maven.plugin.logging.Log;
import org.phpmaven.plugin.php.PhpException;
import org.phpmaven.plugin.php.PhpMojoHelper;

/**
 * Lint checker runnable that will walk the queue and do lint checks.
 * 
 * @author mepeisen
 */
public class LintChecker implements Runnable {
    
    /**
     * The queue.
     */
    private LintQueue queue;
    
    /**
     * Logging.
     */
    private Log log;
    
    /**
     * helper.
     */
    private PhpMojoHelper helper;
    
    /**
     * Constructor.
     * @param queue the queue.
     * @param log the logger.
     * @param helper php execution helper
     */
    public LintChecker(LintQueue queue, Log log, PhpMojoHelper helper) {
        this.queue = queue;
        this.log = log;
        this.helper = helper;
    }

    @Override
    public void run() {
        while (!this.queue.isTerminated()) {
            final LintExecution execution = this.queue.pop();
            if (execution != null) {
                final String command = "-l \"" + execution.getFile().getAbsolutePath() + "\"";
                this.log.debug("Validating: " + execution.getFile().getAbsolutePath());
                try {
                    this.helper.execute(command, execution.getFile());
                } catch (PhpException e) {
                    execution.setException(e);
                    this.queue.addFailure(execution);
                }
            }
            else {
                this.queue.waitForQueue(50);
            }
        }
    }
    
}
