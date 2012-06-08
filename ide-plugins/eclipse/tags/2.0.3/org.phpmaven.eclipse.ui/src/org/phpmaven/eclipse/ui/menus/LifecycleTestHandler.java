/*******************************************************************************
 * Copyright (c) 2011 PHP-Maven.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     PHP-Maven.org
 *******************************************************************************/
// mainly taken from pti

package org.phpmaven.eclipse.ui.menus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.ProblemSeverities;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.phpmaven.eclipse.core.FileProblem;
import org.phpmaven.eclipse.core.MavenPhpUtils;
import org.phpmaven.eclipse.core.PhpmavenCorePlugin;
import org.phpmaven.eclipse.core.mvn.ISourceLocator;
import org.phpmaven.eclipse.core.mvn.ITestExecutionTooling;
import org.phpmaven.eclipse.core.mvn.ITestExecutionTooling.ITestResults;
import org.phpmaven.eclipse.core.php.PHPSourceFile;
import org.phpmaven.eclipse.core.phpunit.CoverageMarker;
import org.phpmaven.eclipse.core.phpunit.CoverageMarker.State;
import org.phpmaven.eclipse.core.phpunit.ICoverageInfo;
import org.phpmaven.eclipse.core.phpunit.IFileCoverage;
import org.phpmaven.eclipse.core.phpunit.ILineCoverage;
import org.phpmaven.eclipse.core.phpunit.ITestCase;
import org.phpmaven.eclipse.core.phpunit.ITestError;
import org.phpmaven.eclipse.core.phpunit.ITestSuite;
import org.phpmaven.eclipse.core.phpunit.ITraceElement;
import org.phpmaven.eclipse.ui.PhpmavenUiPlugin;
import org.phpmaven.eclipse.ui.views.IResultDisplayPart;

/**
 * Menu action to invoke phpunit tests on selected resource.
 * 
 * @author Martin Eisengardt
 */
public class LifecycleTestHandler extends AbstractResourceHandler {

    @Override
    protected Job createJob(IResource[] resources) {
        return new ExecutionUiJob(Messages.LifecycleTestHandler_UiJobTitle, resources);
    }
    
    /**
     * job to fetch the result view and start the phpunit execution
     */
    private static final class ExecutionUiJob extends UIJob {
        /** the resources to be tested */
        private IResource[] resources;

        /**
         * Constructor
         * @param name
         * @param resources
         */
        private ExecutionUiJob(String name, IResource[] resources) {
            super(name);
            this.resources = resources;
        }
        
        @Override
        public IStatus runInUIThread(final IProgressMonitor monitor) {
            try {
                final IResultDisplayPart resultsView = (IResultDisplayPart) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                        .showView("org.phpmaven.eclipse.ui.views.phpUnitResults"); //$NON-NLS-1$
                
                final Job executionJob = new ExecutionJob(resultsView, this.resources);
                
                executionJob.setRule(this.getRule());
                executionJob.setUser(true);
                executionJob.schedule();
                
                monitor.done();
                this.done(Status.OK_STATUS);
                return Status.OK_STATUS;
            } catch (final PartInitException ex) {
                monitor.done();
                final IStatus status = new Status(IStatus.ERROR, PhpmavenUiPlugin.PLUGIN_ID, "Error initializing mvn test invocation", ex); //$NON-NLS-1$
                this.done(status);
                return status;
            }
        }
    }

    /**
     * @author Martin Eisengardt
     */
    private static final class ExecutionJob extends Job {
        
        /** the results view */
        private final IResultDisplayPart resultsView;
        
        /** the resources to be tested */
        private final IResource[] resources;
        
        /**
         * @param resultsView
         * @param resources
         */
        public ExecutionJob(final IResultDisplayPart resultsView, IResource[] resources) {
            super(Messages.LifecycleTestHandler_ExecutionJobTitle);
            this.resultsView = resultsView;
            this.resources = resources;
        }
        
        @Override
        public IStatus run(final IProgressMonitor monitor) {
            try {
                monitor.beginTask(Messages.LifecycleTestHandler_Task_RunTest, this.resources.length * 2);
                for (final IResource resource : this.resources) {
                    final IProject project = resource.getProject();
                    final ITestExecutionTooling testTooling = PhpmavenCorePlugin.getTestExecutionTooling(project);
                    
                    monitor.setTaskName(Messages.LifecycleTestHandler_Task_RunTestFor + resource.getProjectRelativePath().toString());
                    
                    final IScriptProject scriptProject = DLTKCore.create(project);
                    // final ILauncher coreLauncher = PhpmavenCorePlugin.getLauncher(project);
                    final IProgressMonitor subProgressMonitor = new SubProgressMonitor(monitor, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
                    try {
                        ITestResults result = null;
                        switch (testTooling.getTestMode(project, scriptProject, resource)) {
                            case FILE:
                                this.setName(Messages.LifecycleTestHandler_Task_RunTestForFile + resource.getFullPath().toString());
                                result = testTooling.testFile(project, scriptProject, (IFile) resource, subProgressMonitor);
                                break;
                            case FOLDER:
                                this.setName(Messages.LifecycleTestHandler_Task_RunTestForFolder + resource.getFullPath().toString());
                                result = testTooling.testFolder(project, scriptProject, (IFolder) resource, subProgressMonitor);
                                break;
                            case TEST_CLASSES:
                                this.setName(Messages.LifecycleTestHandler_Task_RunTestForMultipleClasses);
                                result = testTooling.testClasses(project, scriptProject, testTooling.getTestClassesForResource(resource), subProgressMonitor);
                                break;
                            case WHOLE_PROJECT:
                                this.setName(Messages.LifecycleTestHandler_Task_RunTestForProject + scriptProject.getProject().getName());
                                result = testTooling.testProject(project, scriptProject, subProgressMonitor);
                                break;
                            default:
                                throw new IllegalStateException(); // should never
                                                                   // happen but to
                                                                   // prevent from
                                                                   // NullPointerException
                                                                   // caused by
                                                                   // future
                                                                   // extensions of
                                                                   // the possible
                                                                   // modes
                        }
                        subProgressMonitor.done();
                        final ITestResults myResult = result;
                        
                        if (myResult == null || myResult.getState() == null || myResult.getState().getStatus() == null) {
                            final Job updateUiJob = new UIJob(Messages.LifecycleTestHandler_UiJobTitle) {
                                @Override
                                public IStatus runInUIThread(final IProgressMonitor monitor2) {
                                    monitor.setTaskName(Messages.LifecycleTestHandler_Task_SettingFailureInfo);
                                    ExecutionJob.this.resultsView.setFailure(Messages.LifecycleTestHandler_Task_NullState);
                                    return Status.OK_STATUS;
                                }
                            };
                            updateUiJob.setUser(false);
                            updateUiJob.schedule();
                        } else if (!myResult.getState().getStatus().isOK()) {
                            final Job updateUiJob = new UIJob(Messages.LifecycleTestHandler_UiJobTitle) {
                                @Override
                                public IStatus runInUIThread(final IProgressMonitor monitor2) {
                                    monitor.setTaskName(Messages.LifecycleTestHandler_Task_SettingFailureInfo);
                                    ExecutionJob.this.resultsView.setFailure(myResult.getState().getStdout() + "\n" + myResult.getState().getStatus().toString()); //$NON-NLS-1$
                                    return Status.OK_STATUS;
                                }
                            };
                            updateUiJob.setUser(false);
                            updateUiJob.schedule();
                        } else {
                            final Job updateUiJob = new UIJob(Messages.LifecycleTestHandler_UiJobTitle) {
                                @Override
                                public IStatus runInUIThread(final IProgressMonitor monitor2) {
                                    monitor.setTaskName(Messages.LifecycleTestHandler_Title_SettingResults);
                                    ExecutionJob.this.resultsView.setResults(myResult.getState().getStdout(), myResult);
                                    return Status.OK_STATUS;
                                }
                            };
                            updateUiJob.setUser(false);
                            updateUiJob.schedule();
                            
                            final IProblem[] problems = parsePHPUnitXmlOutput(project, result.getTestSuites());
                            createFileMarker(problems);
                            parseCoverage(project, result.getCoverageInfo());
                        }
                    } catch (final Exception e) {
                        final Job updateUiJob = new UIJob(Messages.LifecycleTestHandler_Title_SettingFailure) {
                            @Override
                            public IStatus runInUIThread(final IProgressMonitor monitor2) {
                                monitor.setTaskName(Messages.LifecycleTestHandler_Task_SettingFailureInfo);
                                ExecutionJob.this.resultsView.setFailure(Messages.LifecycleTestHandler_ExceptionProlog + e.getMessage());
                                return Status.OK_STATUS;
                            }
                        };
                        updateUiJob.setUser(false);
                        updateUiJob.schedule();
                    }
                    new SubProgressMonitor(monitor, 1).done();
                }
                monitor.done();
                this.done(Status.OK_STATUS);
                return Status.OK_STATUS;
            } catch (Exception ex) {
                monitor.done();
                final IStatus status = new Status(IStatus.ERROR, PhpmavenUiPlugin.PLUGIN_ID, Messages.LifecycleTestHandler_ProblemsRunningTest, ex);
                this.done(status);
                return status;
            }
        }
    }
    
    /**
     * @param project
     *            project
     * @param suites
     *            output
     * @return problems
     */
    protected static IProblem[] parsePHPUnitXmlOutput(final IProject project, final ITestSuite[] suites) {
        final ArrayList<IProblem> problems = new ArrayList<IProblem>();
        
        try {
            parseSuites(project, suites, problems);
        } catch (final Exception e) {
            PhpmavenUiPlugin.logError(Messages.LifecycleTestHandler_ExceptionParsingOutput, e);
        }
        
        return problems.toArray(new IProblem[0]);
    }

    /**
     * Parses the test suites
     * @param project
     * @param suites
     * @param problems
     * @throws CoreException
     * @throws IOException
     * @throws ModelException
     */
    private static void parseSuites(final IProject project, final ITestSuite[] suites, final ArrayList<IProblem> problems) throws CoreException, IOException, ModelException {
        for (final ITestSuite suite : suites) {
            parseSuites(project, suite.getSubSuites(), problems);
            for (final ITestCase testCase : suite.getTestCases()) {
                final ITestError[] errors = testCase.getErrors();
                if (errors.length > 0) {
                    final SearchMatch[] matches = MavenPhpUtils.findClass(testCase.getClassName(), MavenPhpUtils.createProjectScope(DLTKCore.create(project)));
                    if (matches.length > 0) {
                        final IType clsType = (IType) matches[0].getElement();
                        final IFile testFile = (IFile) matches[0].getResource();
                        final IMethod mthObject = clsType.getMethod(testCase.getName());
                        final PHPSourceFile src = new PHPSourceFile(testFile);
                        
                        for (final ITestError error : errors) {
                            final ITraceElement traceElement = error.getTestMethodTraceElement();
                            int lineNumber = -1;
                            if (traceElement == null) {
                                if (mthObject != null) {
                                    lineNumber = src.findLineNumberForOffset(mthObject.getNameRange().getOffset());
                                }
                            } else {
                                lineNumber = traceElement.getLine();
                            }
                            
                            if (lineNumber != -1) {
                                final int lineStart = src.lineStart(lineNumber);
                                final int lineEnd = src.lineEnd(lineNumber);
                                problems.add(new FileProblem(testFile, error.getMessage(), IProblem.Task, new String[0], ProblemSeverities.Error, lineStart, lineEnd, lineNumber));
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * @param project
     *            project
     * @param coverage
     *            coverage infos
     */
    protected static void parseCoverage(final IProject project, final ICoverageInfo coverage) {
        try {
            final ISourceLocator locator = PhpmavenCorePlugin.getSourceLocator(project);
            final IScriptProject scriptProject = DLTKCore.create(project);
            // add markers
            for (final IFileCoverage fileCoverage : coverage.getFileCoverage()) {
                // find the regular source file
                final String targetFileName = fileCoverage.getFileName();
                final IFile[] matches = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(new File(targetFileName).toURI());
                for (final IFile match : matches) {
                    if (project.equals(match.getProject())) {
                        // we found it. locate the source.
                        final IFile srcFile = locator.locateSourceFile(match, project, scriptProject);
                        if (srcFile.exists() && srcFile.isAccessible()) {
                            final PHPSourceFile src = new PHPSourceFile(srcFile);
                            for (final IMarker marker : srcFile.findMarkers(CoverageMarker.TYPE_ID, true, IResource.DEPTH_INFINITE)) {
                                marker.delete();
                            }
                            for (final ILineCoverage lineCoverage : fileCoverage.getLineCoverage()) {
                                int charStart = -1;
                                int charEnd = -1;
                                try {
                                    charStart = src.lineStart(lineCoverage.getLineNumber());
                                    charEnd = src.lineEnd(lineCoverage.getLineNumber());
                                } catch (final ArrayIndexOutOfBoundsException e) {
                                    PhpmavenUiPlugin.logError("Cannot fetch lineStart/lineEnd for line " + lineCoverage.getLineNumber() + " of file " + srcFile); //$NON-NLS-1$ //$NON-NLS-2$
                                }
                                CoverageMarker.createLineMarker(srcFile, lineCoverage.getCalls() > 0 ? State.FULL : State.NONE, 0, lineCoverage.getCalls(), lineCoverage.getLineNumber(), charStart,
                                        charEnd);
                            }
                        }
                        break;
                    }
                }
            }
        } catch (final Exception e) {
            PhpmavenUiPlugin.logError(Messages.LifecycleTestHandler_ExceptionParsingCodeCoverage, e);
        }
    }
    
    /**
     * Creates the file markers for given problem list
     * 
     * @param problems problem list
     */
    protected static void createFileMarker(final IProblem[] problems) {
        for (final IProblem problem : problems) {
            final IFile file = ((FileProblem) problem).getOriginatingFile();
            
            try {
                final IMarker marker = file.createMarker(CoverageMarker.TYPE_ID);
                marker.setAttribute(IMarker.PROBLEM, true);
                marker.setAttribute(IMarker.LINE_NUMBER, problem.getSourceLineNumber());
                
                if (problem.isWarning()) {
                    marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
                } else {
                    marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
                }
                marker.setAttribute(IMarker.CHAR_START, problem.getSourceStart());
                marker.setAttribute(IMarker.CHAR_END, problem.getSourceEnd());
                marker.setAttribute(IMarker.MESSAGE, problem.getMessage());
            } catch (final CoreException e) {
                PhpmavenUiPlugin.logError(Messages.LifecycleTestHandler_ExceptionCreatingFileMarker, e);
            }
        }
    }
    
}
