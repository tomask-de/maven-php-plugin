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

package org.phpmaven.eclipse.core.internal.mvn;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.maven.execution.MavenExecutionRequest;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.phpmaven.eclipse.core.MavenPhpUtils;
import org.phpmaven.eclipse.core.PhpmavenCorePlugin;
import org.phpmaven.eclipse.core.mvn.IMavenJobData;
import org.phpmaven.eclipse.core.mvn.ISourceLocator;
import org.phpmaven.eclipse.core.mvn.ITestExecutionTooling;
import org.phpmaven.eclipse.core.mvn.MavenJob;
import org.phpmaven.eclipse.core.phpunit.ICoverageInfo;
import org.phpmaven.eclipse.core.phpunit.IPhpUnit;
import org.phpmaven.eclipse.core.phpunit.ITestSuite;

/**
 * Implementation for phpmaven based test execution.
 * 
 * @author Martin Eisengardt
 */
public class PhpmavenTestExecution implements ITestExecutionTooling {
    
    /**
     * Test result implementation
     */
    private static final class TestResult implements ITestResults {
        /** the test suites */
        private final ITestSuite[] suites;
        /** the coverage info */
        private final ICoverageInfo coverageInfo;
        
        /** the test result state */
        private final IStatus state;
        
        /** the stdout */
        private final String stdout;
        
        /** the xml type. */
        private final String xmlType;
        
        /** the coverage type. */
        private final String coverageType;
        
        /** the xml content. */
        private final String xmlContent;
        
        /** the coverage content. */
        private final String coverageContent;
        
        /** the start date. */
        private final long startDate;
        
        /** the end date. */
        private final long endDate;
        
        /**
         * Constructor
         * 
         * @param suites
         * @param coverageInfo
         * @param state
         * @param stdout
         * @param xmlType
         * @param xmlContent
         * @param coverageType
         * @param coverageContent
         * @param startDate
         * @param endDate
         */
        private TestResult(ITestSuite[] suites, ICoverageInfo coverageInfo, IStatus state, String stdout,
                String xmlType, String xmlContent, String coverageType, String coverageContent,
                long startDate, long endDate) {
            this.suites = suites;
            this.coverageInfo = coverageInfo;
            this.state = state;
            this.stdout = stdout;
            this.xmlType = xmlType;
            this.xmlContent = xmlContent;
            this.coverageType = coverageType;
            this.coverageContent = coverageContent;
            this.startDate = startDate;
            this.endDate = endDate;
        }
        
        @Override
        public ITestSuite[] getTestSuites() {
            return this.suites;
        }
        
        @Override
        public ICoverageInfo getCoverageInfo() {
            return this.coverageInfo;
        }
        
        @Override
        public ITestState getState() {
            return new ITestState() {
                
                @Override
                public String getStdout() {
                    return TestResult.this.stdout;
                }
                
                @Override
                public IStatus getStatus() {
                    return TestResult.this.state;
                }

                @Override
                public String getXmlType() {
                    return TestResult.this.xmlType;
                }

                @Override
                public String getCoverageType() {
                    return TestResult.this.coverageType;
                }

                @Override
                public String getXmlContent() {
                    return TestResult.this.xmlContent;
                }

                @Override
                public String getCoverageContent() {
                    return TestResult.this.coverageContent;
                }

                @Override
                public long getDateStarted() {
                    return TestResult.this.startDate;
                }

                @Override
                public long getEndDate() {
                    return TestResult.this.endDate;
                }
            };
        }
    }

    /**
     * @see org.phpmaven.eclipse.core.mvn.ITestExecutionTooling#isResponsibleForProject(org.eclipse.core.resources.IProject,
     *      org.eclipse.dltk.core.IScriptProject)
     */
    @Override
    public boolean isResponsibleForProject(final IProject project, final IScriptProject scriptProject) {
        final IMavenProjectFacade mvnFacade = MavenPhpUtils.fetchProjectFacade(project);
        return MavenPhpUtils.isMavenProject(project) && MavenPhpUtils.isPHPProject(project) && MavenPhpUtils.isPhpmavenProject(project) && "php".equals(mvnFacade.getPackaging()); //$NON-NLS-1$
    }
    
    /**
     * @see org.phpmaven.eclipse.core.mvn.ITestExecutionTooling#getTestMode(org.eclipse.core.resources.IProject,
     *      org.eclipse.dltk.core.IScriptProject,
     *      org.eclipse.core.resources.IResource)
     */
    @Override
    public TestMode getTestMode(final IProject project, final IScriptProject scriptProject, final IResource resource) {
        if (resource instanceof IProject) {
            return TestMode.WHOLE_PROJECT;
        }
        
        final IMavenProjectFacade mvnFacade = MavenPhpUtils.fetchProjectFacade(project);
        final IPath relPath = resource.getFullPath();
        // look if we are part of test source folder
        for (final IPath srcPath : MavenPhpUtils.getTestCompileSourceLocations(project, mvnFacade)) {
            if (srcPath.isPrefixOf(relPath)) {
                if (resource.getType() == IResource.FILE) {
                    return TestMode.FILE;
                }
                if (resource.getType() == IResource.FOLDER) {
                    return TestMode.FOLDER;
                }
            }
        }
        
        // look if we are part of test output folder
        if (mvnFacade.getTestOutputLocation().isPrefixOf(relPath)) {
            if (resource.getType() == IResource.FILE) {
                return TestMode.FILE;
            }
            if (resource.getType() == IResource.FOLDER) {
                return TestMode.FOLDER;
            }
        }
        
        return TestMode.WHOLE_PROJECT;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.mvn.ITestExecutionTooling#getTestClassesForResource(org.eclipse.core.resources.IResource)
     */
    @Override
    public String[] getTestClassesForResource(final IResource resource) {
        // not used, because we do not return TEST_CLASSES in getTestMode
        return new String[0];
    }
    
    /**
     * @see org.phpmaven.eclipse.core.mvn.ITestExecutionTooling#testProject(org.eclipse.core.resources.IProject,
     *      org.eclipse.dltk.core.IScriptProject,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public ITestResults testProject(final IProject project, final IScriptProject scriptProject, IProgressMonitor monitor) {
        monitor.beginTask("Analyzing maven config", 100);
        final IMavenProjectFacade mvnFacade = MavenPhpUtils.fetchProjectFacade(project);
        final IPath sourceLocation = MavenPhpUtils.getTestCompileSourceLocations(project, mvnFacade)[0];
        return this.executeTests(project, null, project.getWorkspace().getRoot().getFolder(sourceLocation).getLocation().toOSString(), monitor);
    }
    
    /**
     * Performs the testing for given file or folder
     * 
     * @param project
     *            the project
     * @param testFile
     *            the test file or null to test folders
     * @param testFolder
     *            the test folder or null to test files
     * @param monitor
     *            the progress monitor
     * @return test results
     */
    private ITestResults executeTests(final IProject project, final String testFile, final String testFolder, IProgressMonitor monitor) {
        final long startDate = System.currentTimeMillis();
        monitor.worked(10);
        monitor.setTaskName("Preparing mvn test call");
        try {
            final File resultXml = MavenPhpUtils.createTempFile("phpunit.result", ".xml"); //$NON-NLS-1$ //$NON-NLS-2$
            final File coverageXml = MavenPhpUtils.createTempFile("coverage.clover", ".xml"); //$NON-NLS-1$ //$NON-NLS-2$
            
            final IMavenJobData jobData = new IMavenJobData() {
                
                @Override
                public void manipulateRequest(final MavenExecutionRequest request) {
                    final Properties props = request.getSystemProperties();
                    if (testFile != null) {
                        props.setProperty("testFile", testFile); //$NON-NLS-1$
                    } else {
                        props.setProperty("testFolder", testFolder); //$NON-NLS-1$
                    }
                    props.setProperty("singleTestInvocation", "true"); //$NON-NLS-1$ //$NON-NLS-2$
                    props.setProperty("phpunitXmlResult", resultXml.getAbsolutePath()); //$NON-NLS-1$
                    props.setProperty("phpunitCoverageResultXml", coverageXml.getAbsolutePath()); //$NON-NLS-1$
                    props.setProperty("maven.test.failure.ignore", "true"); //$NON-NLS-1$ //$NON-NLS-2$
                    props.setProperty("failIfNoTests", "true"); //$NON-NLS-1$ //$NON-NLS-2$
                }
                
                @Override
                public IProject getProject() {
                    return project;
                }
                
                @Override
                public String[] getMavenCommands() {
                    return new String[] { "test" }; //$NON-NLS-1$
                }
                
                @Override
                public boolean canProcessRequest(final MavenExecutionRequest request, final IMavenProjectFacade projectFacade) {
                    return true;
                }
            };
            
            final MavenJob job = new MavenJob(jobData);
            monitor.worked(10);
            monitor.setTaskName("execute");
            final SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, 50, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
            final IStatus mavenResult = job.execute(subMonitor);
            subMonitor.done();
            if (mavenResult.isOK()) {
                monitor.setTaskName("Reading test results");

                String coverageString = null;
                String resultString = null;
                
                if (coverageXml.exists() && coverageXml.length() > 0) {
                    final FileInputStream fis = new FileInputStream(coverageXml);
                    final byte[] buffer = new byte[(int) coverageXml.length()];
                    final BufferedInputStream bis = new BufferedInputStream(fis);
                    bis.read(buffer);
                    coverageString = new String(buffer);
                }
                else {
                    coverageString = "<?xml version=\"1.0\" ?><coverage/>"; //$NON-NLS-1$
                }
                
                if (resultXml.exists() && resultXml.length() > 0) {
                    final FileInputStream fis = new FileInputStream(resultXml);
                    final byte[] buffer = new byte[(int) resultXml.length()];
                    final BufferedInputStream bis = new BufferedInputStream(fis);
                    bis.read(buffer);
                    resultString = new String(buffer);
                }
                else {
                    resultString = "<?xml version=\"1.0\" ?><test/>"; //$NON-NLS-1$
                }
                
                monitor.worked(10);
                
                final IPhpUnit phpUnitTooling = PhpmavenCorePlugin.getPhpUnit(project);
                monitor.setTaskName("Parsing coverage info"); //$NON-NLS-1$
                final ICoverageInfo coverageInfo = phpUnitTooling.parseCoverageInfo(coverageString, "clover", project); //$NON-NLS-1$
                monitor.worked(10);
                monitor.setTaskName("Parsing test results"); //$NON-NLS-1$
                final ITestSuite[] suites = phpUnitTooling.parseTestResults(resultString, "xml", project); //$NON-NLS-1$
                monitor.worked(9);
                
                final long endDate = System.currentTimeMillis();
                return new TestResult(
                        suites, coverageInfo, Status.OK_STATUS, job.getSysout(),
                        "xml", resultString, "clover", coverageString, startDate, endDate);  //$NON-NLS-1$//$NON-NLS-2$
            }
            
            final long endDate = System.currentTimeMillis();
            return new TestResult(null, null, mavenResult, job.getSysout(),
                    null, null, null, null, startDate, endDate);
        } catch (final Exception ex) {
            PhpmavenCorePlugin.logError("Error while executing mvn test", ex); //$NON-NLS-1$
            final long endDate = System.currentTimeMillis();
            return new TestResult(
                    null, null, new Status(IStatus.ERROR, PhpmavenCorePlugin.PLUGIN_ID, "Error executing mvn test", ex), null,
                    null, null, null, null, startDate, endDate);
        } finally {
            monitor.done();
        }
    }
    
    /**
     * @see org.phpmaven.eclipse.core.mvn.ITestExecutionTooling#testFile(org.eclipse.core.resources.IProject,
     *      org.eclipse.dltk.core.IScriptProject,
     *      org.eclipse.core.resources.IFile,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public ITestResults testFile(final IProject project, final IScriptProject scriptProject, final IFile toTest, IProgressMonitor monitor) {
        monitor.beginTask("Analyzing maven config", 100);
        final IMavenProjectFacade mvnFacade = MavenPhpUtils.fetchProjectFacade(project);
        
        // find the target file from src directory
        // XXX: Support for multiple source directories; see maven-php-plugin PhpUnitTestfileWalker
        final IPath sourceLocation = MavenPhpUtils.getTestCompileSourceLocations(project, mvnFacade)[0];
        
        if (!sourceLocation.isPrefixOf(toTest.getFullPath())) {
            final ISourceLocator locator = PhpmavenCorePlugin.getSourceLocator(project);
            final IFile file = locator.locateTestSourceFile(toTest, project, scriptProject);
            if (sourceLocation.isPrefixOf(file.getFullPath())) {
                return this.executeTests(project, file.getLocation().toOSString(), null, monitor);
            }
        }
        
        return this.executeTests(project, toTest.getLocation().toOSString(), null, monitor);
    }
    
    /**
     * @see org.phpmaven.eclipse.core.mvn.ITestExecutionTooling#testFolder(org.eclipse.core.resources.IProject,
     *      org.eclipse.dltk.core.IScriptProject,
     *      org.eclipse.core.resources.IFolder,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public ITestResults testFolder(final IProject project, final IScriptProject scriptProject, final IFolder toTest, IProgressMonitor monitor) {
        monitor.beginTask("Analyzing maven config", 100);
        final IMavenProjectFacade mvnFacade = MavenPhpUtils.fetchProjectFacade(project);
        
        // find the target file from src directory
        // XXX: Support for multiple source directories; see maven-php-plugin PhpUnitTestfileWalker
        final IPath sourceLocation = MavenPhpUtils.getTestCompileSourceLocations(project, mvnFacade)[0];
        
        if (!sourceLocation.isPrefixOf(toTest.getFullPath())) {
            final ISourceLocator locator = PhpmavenCorePlugin.getSourceLocator(project);
            final IFolder folder = locator.locateTestSourceFolder(toTest, project, scriptProject);
            if (sourceLocation.isPrefixOf(folder.getFullPath())) {
                return this.executeTests(project, folder.getLocation().toOSString(), null, monitor);
            }
        }
        
        return this.executeTests(project, toTest.getLocation().toOSString(), null, monitor);
    }
    
    /**
     * @see org.phpmaven.eclipse.core.mvn.ITestExecutionTooling#testClasses(org.eclipse.core.resources.IProject,
     *      org.eclipse.dltk.core.IScriptProject, java.lang.String[],
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public ITestResults testClasses(final IProject project, final IScriptProject scriptProject, final String[] toTest, IProgressMonitor monitor) {
        // will not be supported. Should never be called because we do not
        // return TEST_CLASSES in getTestMode
        throw new UnsupportedOperationException("not implemented"); //$NON-NLS-1$
    }
}
