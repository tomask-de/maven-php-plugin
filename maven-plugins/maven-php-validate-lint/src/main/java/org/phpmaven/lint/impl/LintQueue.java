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

package org.phpmaven.lint.impl;

import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LintQueue {

    /**
     * The concurrent queue of open lint checks.
     */
    private ConcurrentLinkedQueue<LintExecution> queue = new ConcurrentLinkedQueue<LintExecution>();
    
    /**
     * The mutex.
     */
    private Object mutex = new Object();
    
    /**
     * The failed lint checks.
     */
    private List<LintExecution> failed = new Vector<LintExecution>();

    /**
     * true if the queue was terminated.
     */
    private boolean terminated;
    
    /**
     * Count of checked files.
     */
    private long checkedFileCount;
    
    /**
     * Count of total files.
     */
    private long totalFiles;
    
    /**
     * Count of failures.
     */
    private long failureCount;
    
    /**
     * Adds a new lint execution.
     * @param lint 
     */
    public void addLintCheck(LintExecution lint) {
    	this.totalFiles++;
        this.queue.add(lint);
        synchronized (this.mutex) {
            this.mutex.notify();
        }
    }
    
    /**
     * Increments the checked files
     */
    public void incrementCheckedFiles() {
    	this.checkedFileCount++;
    }
    
    /**
     * Increments the failures
     */
    public void incrementFailures() {
    	this.failureCount++;
    }
    
    /**
	 * @return the checkedFileCount
	 */
	public long getCheckedFileCount() {
		return this.checkedFileCount;
	}

	/**
	 * @return the totalFiles
	 */
	public long getTotalFiles() {
		return this.totalFiles;
	}

	/**
	 * @return the failureCount
	 */
	public long getFailureCount() {
		return this.failureCount;
	}

	/**
     * returns the next lint execution.
     * @return lint execution or null if there is no execution
     */
    public LintExecution pop() {
        return this.queue.poll();
    }
    
    /**
     * Waits for something within the queue.
     * @param timeout 
     */
    public void waitForQueue(long timeout) {
        synchronized (this.mutex) {
            try {
                this.mutex.wait(timeout);
            } catch (InterruptedException ex) {
                // ignore; maybe we received the signal because of method terminate.
                // the terminate fires notifyAll.
            }
        }
    }
    
    /**
     * Terminates the lint queue.
     */
    public void terminate() {
        // wait for the walker threads to handle all lint checks
        while (!this.queue.isEmpty()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                // ignore; should never happen
            }
        }
        this.terminated = true;
        synchronized (this.mutex) {
            this.mutex.notifyAll();
        }
    }
    
    /**
     * Returns true if the lint check is terminated.
     * @return true if lint check is terminated
     */
    public boolean isTerminated() {
        return this.terminated;
    }
    
    /**
     * Returns the failures.
     * @return failures
     */
    public Iterable<LintExecution> getFailures() {
        return Collections.unmodifiableList(this.failed);
    }

    /**
     * Add failure.
     * @param execution Execution.
     */
    public void addFailure(LintExecution execution) {
        this.failed.add(execution);
    }
    
}
