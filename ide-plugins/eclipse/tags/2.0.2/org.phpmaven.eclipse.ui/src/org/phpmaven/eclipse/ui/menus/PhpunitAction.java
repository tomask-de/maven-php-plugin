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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.ProblemSeverities;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IOpenable;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.phpmaven.eclipse.core.FileProblem;
import org.phpmaven.eclipse.core.MavenPhpUtils;
import org.phpmaven.eclipse.core.PhpmavenCorePlugin;
import org.phpmaven.eclipse.core.mvn.ILauncher;
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
public class PhpunitAction implements IWorkbenchWindowActionDelegate {
    
    /** the workbench window */
    private IWorkbenchWindow window;
    
    /** the selected resources */
    private IResource[] selectedResources;
    
    @Override
    public void dispose() {
        this.selectedResources = null;
    }
    
    @Override
    public void init(final IWorkbenchWindow win) {
        this.selectedResources = new IResource[0];
        this.window = win;
    }
    
    @Override
    public void selectionChanged(final IAction action, final ISelection selection) {
        List<IResource> resources = new ArrayList<IResource>(1);
        if (selection.isEmpty()) {
            this.addActiveEditorFileToList(resources);
        } else if (selection instanceof ITextSelection) {
            this.addActiveEditorFileToList(resources);
        } else if (selection instanceof IStructuredSelection) {
            final IStructuredSelection structuredSelection = (IStructuredSelection) selection;
            resources = new ArrayList<IResource>(structuredSelection.size());
            final Iterator<?> iterator = structuredSelection.iterator();
            while (iterator.hasNext()) {
                final Object entry = iterator.next();
                try {
                    if (entry instanceof IResource) {
                        this.addResourceToList(resources, (IResource) entry);
                    } else if (entry instanceof ISourceModule) {
                        if (((ISourceModule) entry).exists()) {
                            final IFile file = (IFile) ((ISourceModule) entry).getCorrespondingResource();
                            if (MavenPhpUtils.isPhpFile(file)) {
                                this.addResourceToList(resources, file);
                            }
                        }
                    } else if (entry instanceof IOpenable) {
                        if (((IOpenable) entry).exists()) {
                            this.addResourceToList(resources, ((IOpenable) entry).getCorrespondingResource());
                        }
                    } else if (entry instanceof IMember) {
                        if (((IMember) entry).exists()) {
                            this.addResourceToList(resources, ((IMember) entry).getResource());
                        }
                    } else if (entry instanceof IFileEditorInput) {
                        if (((IFileEditorInput) entry).exists()) {
                            this.addResourceToList(resources, ((IFileEditorInput) entry).getFile());
                        }
                    } else if (entry instanceof IScriptFolder) {
                        if (((IScriptFolder) entry).exists()) {
                            this.addResourceToList(resources, ((IScriptFolder) entry).getResource());
                        }
                    }
                } catch (final ModelException e) {
                    PhpmavenUiPlugin.logError("Error on selection changed", e); //$NON-NLS-1$
                }
            }
        } else {
            this.addActiveEditorFileToList(resources);
        }
        
        this.selectedResources = resources.toArray(new IResource[0]);
    }
    
    /**
     * Sets the selected resources
     * 
     * @param resources
     *            resources
     */
    public void setSelectedResources(final IResource[] resources) {
        this.selectedResources = resources;
    }
    
    /**
     * Returns the selected resources
     * 
     * @return selected resources
     */
    public IResource[] getSelectedResources() {
        return this.selectedResources;
    }
    
    /**
     * Adds resource to list
     * 
     * @param list
     * @param resource
     */
    private void addResourceToList(final List<IResource> list, final IResource resource) {
        if (resource != null && resource.exists() && !list.contains(resource) && this.canAddResourceToList(resource)) {
            list.add(resource);
        }
    }
    
    /**
     * Adds active editor file to list
     * 
     * @param list
     */
    protected void addActiveEditorFileToList(final List<IResource> list) {
        if (this.window != null) {
            final IWorkbenchPage page = this.window.getActivePage();
            if (page != null) {
                final IEditorPart editor = page.getActiveEditor();
                if (editor != null) {
                    final IEditorInput input = editor.getEditorInput();
                    if (input != null && input instanceof IFileEditorInput) {
                        this.addResourceToList(list, ((IFileEditorInput) input).getFile());
                    }
                }
            }
        }
    }
    
    /**
     * Returns true if the resource can be added to list
     * 
     * @param resource
     * @return boolean
     */
    protected boolean canAddResourceToList(final IResource resource) {
        return true;
    }
    
    /**
     * The mutex scheduling rule for the jobs.
     */
    final MutexRule rule = new MutexRule() {

        @Override
        public boolean contains(ISchedulingRule schedRule) {
            if (schedRule instanceof IProject) {
                return true;
            }
            return super.contains(schedRule);
        }
        
    };
    
    @Override
    public void run(final IAction action) {
        final IResource[] resources = this.getSelectedResources();
        if (resources.length > 0) {
            final Job job = new UIJob("PHPUnit") {
                @Override
                public IStatus runInUIThread(final IProgressMonitor monitor) {
                    try {
                        final IResultDisplayPart resultsView = (IResultDisplayPart) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                                .showView("org.phpmaven.eclipse.ui.views.phpUnitResults"); //$NON-NLS-1$
                        
                        final Job executionJob = new ExecutionJob(resultsView);
                        
                        executionJob.setRule(PhpunitAction.this.rule);
                        executionJob.setUser(false);
                        executionJob.schedule();
                        
                        return Status.OK_STATUS;
                    } catch (final PartInitException ex) {
                        return new Status(IStatus.ERROR, PhpmavenUiPlugin.PLUGIN_ID, "Error initializing mvn test invocation", ex); //$NON-NLS-1$
                    } finally {
                        monitor.done();
                    }
                }
            };
            job.setRule(this.rule);
            job.setUser(false);
            job.schedule();
        }
    }
    
    /**
     * @author Martin Eisengardt
     */
    private final class ExecutionJob extends Job {
        
        private final IResultDisplayPart resultsView;
        
        /**
         * @param resultsView
         */
        public ExecutionJob(final IResultDisplayPart resultsView) {
            super("PHPUnit execution job");
            this.resultsView = resultsView;
        }
        
        @Override
        public IStatus run(final IProgressMonitor monitor) {
            try {
                final IResource[] resources = PhpunitAction.this.getSelectedResources();
                monitor.beginTask("Run Test", resources.length * 2);
                for (final IResource resource : resources) {
                    final IProject project = resource.getProject();
                    final ITestExecutionTooling testTooling = PhpmavenCorePlugin.getTestExecutionTooling(project);
                    
                    monitor.setTaskName("Run mvn test for " + resource.getProjectRelativePath().toString());
                    
                    final IScriptProject scriptProject = DLTKCore.create(project);
                    final ILauncher coreLauncher = PhpmavenCorePlugin.getLauncher(project);
                    final IProgressMonitor subProgressMonitor = new SubProgressMonitor(monitor, 1, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
                    try {
                        ITestResults result = null;
                        switch (testTooling.getTestMode(project, scriptProject, resource)) {
                            case FILE:
                                result = testTooling.testFile(project, scriptProject, (IFile) resource, subProgressMonitor);
                                break;
                            case FOLDER:
                                result = testTooling.testFolder(project, scriptProject, (IFolder) resource, subProgressMonitor);
                                break;
                            case TEST_CLASSES:
                                result = testTooling.testClasses(project, scriptProject, testTooling.getTestClassesForResource(resource), subProgressMonitor);
                                break;
                            case WHOLE_PROJECT:
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
                            final Job updateUiJob = new UIJob("PHPUnit") {
                                @Override
                                public IStatus runInUIThread(final IProgressMonitor monitor2) {
                                    monitor.setTaskName("Setting failure information");
                                    ExecutionJob.this.resultsView.setFailure("NULL STATE");
                                    return Status.OK_STATUS;
                                }
                            };
                            updateUiJob.setUser(false);
                            updateUiJob.schedule();
                        } else if (!myResult.getState().getStatus().isOK()) {
                            final Job updateUiJob = new UIJob("PHPUnit") {
                                @Override
                                public IStatus runInUIThread(final IProgressMonitor monitor2) {
                                    monitor.setTaskName("Setting failure information");
                                    ExecutionJob.this.resultsView.setFailure(myResult.getState().getStdout() + "\n" + myResult.getState().getStatus().toString()); //$NON-NLS-1$
                                    return Status.OK_STATUS;
                                }
                            };
                            updateUiJob.setUser(false);
                            updateUiJob.schedule();
                        } else {
                            final Job updateUiJob = new UIJob("PHPUnit") {
                                @Override
                                public IStatus runInUIThread(final IProgressMonitor monitor2) {
                                    monitor.setTaskName("Setting results");
                                    ExecutionJob.this.resultsView.setResults(myResult.getState().getStdout(), myResult);
                                    return Status.OK_STATUS;
                                }
                            };
                            updateUiJob.setUser(false);
                            updateUiJob.schedule();
                            
                            final IProblem[] problems = PhpunitAction.this.parsePHPUnitXmlOutput(project, result.getTestSuites());
                            PhpunitAction.this.createFileMarker(problems);
                            PhpunitAction.this.parseCoverage(project, result.getCoverageInfo());
                        }
                    } catch (final Exception e) {
                        final Job updateUiJob = new UIJob("PHPUnit setting failure") {
                            @Override
                            public IStatus runInUIThread(final IProgressMonitor monitor2) {
                                monitor.setTaskName("Setting failure information");
                                ExecutionJob.this.resultsView.setFailure("Exception: " + e.getMessage());
                                return Status.OK_STATUS;
                            }
                        };
                        updateUiJob.setUser(false);
                        updateUiJob.schedule();
                    }
                    new SubProgressMonitor(monitor, 1).done();
                }
                this.done(Status.OK_STATUS);
                return Status.OK_STATUS;
            } finally {
                monitor.done();
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
    public IProblem[] parsePHPUnitXmlOutput(final IProject project, final ITestSuite[] suites) {
        final ArrayList<IProblem> problems = new ArrayList<IProblem>();
        
        try {
            for (final ITestSuite suite : suites) {
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
        } catch (final Exception e) {
            PhpmavenUiPlugin.logError("Exception parsing xml output", e);
        }
        
        return problems.toArray(new IProblem[0]);
    }
    
    /**
     * @param project
     *            project
     * @param coverage
     *            coverage infos
     */
    public void parseCoverage(final IProject project, final ICoverageInfo coverage) {
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
            PhpmavenUiPlugin.logError("Exception while parsing coverage output", e);
        }
    }
    
    /**
     * Creates the file markers for given problem list
     * 
     * @param problems problem list
     */
    public void createFileMarker(final IProblem[] problems) {
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
                PhpmavenUiPlugin.logError("Exception while creating file markers", e);
            }
        }
    }
    
}
