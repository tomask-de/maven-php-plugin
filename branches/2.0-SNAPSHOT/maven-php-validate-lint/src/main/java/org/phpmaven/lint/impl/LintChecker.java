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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.phpmaven.core.ConfigurationParameter;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.lint.ILintChecker;
import org.phpmaven.lint.ILintExecution;

@Component(role = ILintChecker.class, instantiationStrategy = "per-lookup")
public class LintChecker implements ILintChecker {
    
    /**
     * Thread count.
     * TODO configrable
     */
    private static final int THREAD_COUNT = 5;
    
    /**
     * The component factory.
     */
    @Requirement
    private IComponentFactory factory;
    
    /**
     * The maven session.
     */
    @ConfigurationParameter(name = "session", expression = "${session}")
    private MavenSession session;
    
    /**
     * The queue.
     */
    private LintQueue queue = new LintQueue();
    
    /**
     * The walkers.
     */
    private LintThread[] walkers = new LintThread[THREAD_COUNT];
    
    /**
     * The lint helper.
     */
    public LintChecker() {
        this.queue = new LintQueue();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addFileToCheck(File file) {
        this.queue.addLintCheck(new LintExecution(file));
    }
    
    /**
     * waits for thread end and returns the failures.
     * @return failures
     */
    private Iterable<LintExecution> waitAndReturnFailures() {
        this.queue.terminate();
        for (final LintThread thread : walkers) {
            try {
                thread.join();
            } catch (InterruptedException ex) {
                // ignore; should never happen
            }
        }
        return this.queue.getFailures();
    }

    @Override
    public Iterable<ILintExecution> run(Log log) {
        for (int i = 0; i < walkers.length; i++) {
            try {
                walkers[i] = this.factory.lookup(LintThread.class, IComponentFactory.EMPTY_CONFIG, this.session);
                walkers[i].setQueue(queue);
            } catch (ComponentLookupException ex) {
                throw new IllegalStateException(ex);
            } catch (PlexusConfigurationException ex) {
                throw new IllegalStateException(ex);
            }
        }
        for (int i = 0; i < walkers.length; i++) {
            walkers[i].run(log);
        }
        final List<ILintExecution> result = new ArrayList<ILintExecution>();
        for (final LintExecution exec : this.waitAndReturnFailures()) {
            result.add(exec);
        }
        return result;
    }
    
}
