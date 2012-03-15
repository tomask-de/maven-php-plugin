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

import java.io.File;

import org.apache.maven.plugin.logging.Log;
import org.phpmaven.plugin.php.PhpMojoHelper;

public class LintHelper {
    
    /**
     * Thread count.
     */
    private static final int THREAD_COUNT = 5;
    
    /**
     * The queue
     */
    private LintQueue queue = new LintQueue();
    
    /**
     * The walkers
     */
    private Thread[] walkers = new Thread[THREAD_COUNT];
    
    /**
     * The lint helper
     */
    public LintHelper(Log log, PhpMojoHelper helper) {
        for (int i = 0; i < walkers.length; i++) {
            walkers[i] = new Thread(new LintChecker(queue, log, helper));
            walkers[i].start();
        }
    }
    
    public void addFile(File file) {
        this.queue.addLintCheck(new LintExecution(file));
    }
    
    public Iterable<LintExecution> waitAndReturnFailures() {
        this.queue.terminate();
        for (final Thread thread : walkers) {
            try {
                thread.join();
            }
            catch (InterruptedException ex) {
                // ignore
            }
        }
        return this.queue.getFailures();
    }
    
}
