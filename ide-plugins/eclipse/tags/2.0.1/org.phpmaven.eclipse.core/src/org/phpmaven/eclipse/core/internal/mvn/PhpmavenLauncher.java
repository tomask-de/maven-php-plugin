/**
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.execution.MavenExecutionRequest;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.phpmaven.eclipse.core.mvn.ILauncher;
import org.phpmaven.eclipse.core.mvn.IMavenJobData;
import org.phpmaven.eclipse.core.mvn.MavenJob;

/**
 * A launcher for php execution within eclipse
 * 
 * @author Martin Eisengardt
 */
public class PhpmavenLauncher implements ILauncher {
    
    /**
     * The runtime path
     */
    private File runtimePath;
    
    /**
     * the command line
     */
    private final List<String> cmdLine = new ArrayList<String>();
    
    /**
     * The php exe to be used
     */
    private String phpExe;
    
    /**
     * The executed php script file
     */
    private File phpScript;
    
    /**
     * the std out from last call
     */
    private String stdOut;
    
    /**
     * the project
     */
    private final IProject project;
    
    /**
     * @param project
     */
    public PhpmavenLauncher(final IProject project) {
        this.project = project;
        this.phpExe = "php"; //$NON-NLS-1$
    }
    
    /**
     * @see org.phpmaven.eclipse.core.mvn.ILauncher#getRuntimePath()
     */
    @Override
    public File getRuntimePath() {
        return this.runtimePath;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.mvn.ILauncher#getCmdLine()
     */
    @Override
    public String[] getCmdLine() {
        return this.cmdLine.toArray(new String[this.cmdLine.size()]);
    }
    
    /**
     * @see org.phpmaven.eclipse.core.mvn.ILauncher#getPhpExe()
     */
    @Override
    public String getPhpExe() {
        return this.phpExe;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.mvn.ILauncher#getPhpScript()
     */
    @Override
    public File getPhpScript() {
        return this.phpScript;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.mvn.ILauncher#setRuntimePath(java.io.File)
     */
    @Override
    public void setRuntimePath(final File runtimePath) {
        this.runtimePath = runtimePath;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.mvn.ILauncher#setRuntimePath(org.eclipse.core.resources.IContainer)
     */
    @Override
    public void setRuntimePath(final IContainer runtimePath) {
        this.runtimePath = runtimePath.getLocation().toFile();
    }
    
    /**
     * @see org.phpmaven.eclipse.core.mvn.ILauncher#setCmdLine(java.lang.String[])
     */
    @Override
    public void setCmdLine(final String[] cmdLine) {
        this.cmdLine.clear();
        for (final String cmd : cmdLine) {
            this.cmdLine.add(cmd);
        }
    }
    
    /**
     * @see org.phpmaven.eclipse.core.mvn.ILauncher#appendCmdLineArg(java.lang.String)
     */
    @Override
    public void appendCmdLineArg(final String arg) {
        this.cmdLine.add(arg);
    }
    
    /**
     * @see org.phpmaven.eclipse.core.mvn.ILauncher#clearCmdLineArgs()
     */
    @Override
    public void clearCmdLineArgs() {
        this.cmdLine.clear();
    }
    
    /**
     * @see org.phpmaven.eclipse.core.mvn.ILauncher#setPhpExe(java.lang.String)
     */
    @Override
    public void setPhpExe(final String exe) {
        this.phpExe = exe;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.mvn.ILauncher#setPhpScript(java.io.File)
     */
    @Override
    public void setPhpScript(final File phpScript) {
        this.phpScript = phpScript;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.mvn.ILauncher#setPhpScript(org.eclipse.core.resources.IFile)
     */
    @Override
    public void setPhpScript(final IFile phpScript) {
        this.phpScript = phpScript.getLocation().toFile();
    }
    
    /**
     * @see org.phpmaven.eclipse.core.mvn.ILauncher#launch()
     */
    @Override
    public String launch() {
        final IMavenJobData data = new IMavenJobData() {
            
            @Override
            public void manipulateRequest(final MavenExecutionRequest request) {
                final Properties props = request.getSystemProperties();
                if (!"php".equals(PhpmavenLauncher.this.getPhpExe())) { //$NON-NLS-1$
                    props.setProperty("phpExecutable", PhpmavenLauncher.this.getPhpExe()); //$NON-NLS-1$
                }
                props.setProperty("phpFile", PhpmavenLauncher.this.getPhpScript().getAbsolutePath()); //$NON-NLS-1$
                props.setProperty("testIncludePath", "true"); //$NON-NLS-1$ //$NON-NLS-2$
                final String[] cmdline = PhpmavenLauncher.this.getCmdLine();
                if (cmdline.length > 0) {
                    final StringBuffer buffer = new StringBuffer();
                    for (final String line : cmdline) {
                        if (buffer.length() > 0) {
                            buffer.append(" "); //$NON-NLS-1$
                        }
                        if (line.contains(" ")) { //$NON-NLS-1$
                            buffer.append("\""); //$NON-NLS-1$
                            buffer.append(line);
                            buffer.append("\""); //$NON-NLS-1$
                        } else {
                            buffer.append(line);
                        }
                    }
                    props.setProperty("phpFileArguments", buffer.toString()); //$NON-NLS-1$
                }
            }
            
            @Override
            public IProject getProject() {
                return PhpmavenLauncher.this.project;
            }
            
            @Override
            public String[] getMavenCommands() {
                return new String[] { "exec" }; //$NON-NLS-1$
            }
            
            @Override
            public boolean canProcessRequest(final MavenExecutionRequest request, final IMavenProjectFacade projectFacade) {
                return true;
            }
        };
        final MavenJob job = new MavenJob(data);
        final IStatus status = job.execute(new NullProgressMonitor());
        if (status.isOK()) {
            // TODO
        }
        return this.stdOut;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.mvn.ILauncher#getStdout()
     */
    @Override
    public String getStdout() {
        return this.stdOut;
    }
    
}
