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

package org.phpmaven.eclipse.ui.menus;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.phpmaven.eclipse.core.MavenPhpUtils;
import org.phpmaven.eclipse.core.PhpmavenCorePlugin;
import org.phpmaven.eclipse.ui.PhpmavenUiPlugin;

/**
 * adds the php-maven nature
 * 
 * @author Martin Eisengardt
 */
@SuppressWarnings("restriction")
public class AddNatureAction implements IObjectActionDelegate {
    
    /** The pom.xml initial file contents */
    private static final String POM_CONTENTS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + //$NON-NLS-1$
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " + //$NON-NLS-1$
            "xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" + //$NON-NLS-1$
            "  <modelVersion>4.0.0</modelVersion>\n" + //$NON-NLS-1$
            "  <groupId>org.mydomain.sample</groupId>\n" + //$NON-NLS-1$
            "  <artifactId>${artifactId}</artifactId>\n" + //$NON-NLS-1$
            "  <version>0.0.1-SNAPSHOT</version>\n" + //$NON-NLS-1$
            "  <packaging>php</packaging>\n" + //$NON-NLS-1$
            "  <name>Sampl php-maven project</name>\n" + //$NON-NLS-1$
            "  <description>The generic parent pom for php projects</description>\n" + //$NON-NLS-1$
            "\n" + //$NON-NLS-1$
            "    <build>\n" + //$NON-NLS-1$
            "        <plugins>\n" + //$NON-NLS-1$
            "            <plugin>\n" + //$NON-NLS-1$
            "                <groupId>org.phpmaven</groupId>\n" + //$NON-NLS-1$
            "                <artifactId>maven-php-plugin</artifactId>\n" + //$NON-NLS-1$
            "                <version>2.0-SNAPSHOT</version>\n" + //$NON-NLS-1$
            "                <extensions>true</extensions>\n" + //$NON-NLS-1$
            "            </plugin>\n" + //$NON-NLS-1$
            "            <plugin>\n" + //$NON-NLS-1$
            "                <groupId>org.apache.maven.plugins</groupId>\n" + //$NON-NLS-1$
            "                <artifactId>maven-site-plugin</artifactId>\n" + //$NON-NLS-1$
            "                <version>3.0</version>\n" + //$NON-NLS-1$
            "                <inherited>true</inherited>\n" + //$NON-NLS-1$
            "                <configuration>\n" + //$NON-NLS-1$
            "                    <reportPlugins>\n" + //$NON-NLS-1$
            "                        <plugin>\n" + //$NON-NLS-1$
            "                            <groupId>org.phpmaven</groupId>\n" + //$NON-NLS-1$
            "                            <artifactId>maven-php-plugin</artifactId>\n" + //$NON-NLS-1$
            "                            <reportSets>\n" + //$NON-NLS-1$
            "                                <reportSet>\n" + //$NON-NLS-1$
            "                                    <reports>\n" + //$NON-NLS-1$
            "                                        <report>phpdocumentor</report>\n" + //$NON-NLS-1$
            "                                        <report>phpunit-coverage</report>\n" + //$NON-NLS-1$
            "                                    </reports>\n" + //$NON-NLS-1$
            "                                </reportSet>\n" + //$NON-NLS-1$
            "                            </reportSets>\n" + //$NON-NLS-1$
            "                        </plugin>\n" + //$NON-NLS-1$
            "                        <plugin>\n" + //$NON-NLS-1$
            "                            <groupId>org.apache.maven.plugins</groupId>\n" + //$NON-NLS-1$
            "                            <artifactId>maven-surefire-report-plugin</artifactId>\n" + //$NON-NLS-1$
            "                            <version>2.10</version>\n" + //$NON-NLS-1$
            "                            <reportSets>\n" + //$NON-NLS-1$
            "                                <reportSet>\n" + //$NON-NLS-1$
            "                                    <reports>\n" + //$NON-NLS-1$
            "                                        <report>report-only</report>\n" + //$NON-NLS-1$
            "                                    </reports>\n" + //$NON-NLS-1$
            "                                </reportSet>\n" + //$NON-NLS-1$
            "                            </reportSets>\n" + //$NON-NLS-1$
            "                        </plugin>\n" + //$NON-NLS-1$
            "                    </reportPlugins>\n" + //$NON-NLS-1$
            "                </configuration>\n" + //$NON-NLS-1$
            "            </plugin>\n" + //$NON-NLS-1$
            "        </plugins>\n" + //$NON-NLS-1$
            "    </build>\n" + //$NON-NLS-1$
            "\n" + //$NON-NLS-1$
            "    <properties>\n" + //$NON-NLS-1$
            "        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n" + //$NON-NLS-1$
            "    </properties>\n" + //$NON-NLS-1$
            "\n" + //$NON-NLS-1$
            "</project>"; //$NON-NLS-1$
    
    /** the selection */
    private ISelection selection;
    
    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run(final IAction action) {
        if (this.selection instanceof IStructuredSelection) {
            for (@SuppressWarnings("unchecked")
            final Iterator<Object> it = ((IStructuredSelection) this.selection).iterator(); it.hasNext();) {
                final Object element = it.next();
                IProject project = null;
                if (element instanceof IProject) {
                    project = (IProject) element;
                } else if (element instanceof IAdaptable) {
                    project = (IProject) ((IAdaptable) element).getAdapter(IProject.class);
                }
                if (project != null) {
                    this.toggleNature(project);
                }
            }
        }
    }
    
    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged(final IAction action, final ISelection sel) {
        this.selection = sel;
    }
    
    /**
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
     *      org.eclipse.ui.IWorkbenchPart)
     */
    @Override
    public void setActivePart(final IAction action, final IWorkbenchPart targetPart) {
        // empty
    }
    
    /**
     * Adds given nature to project
     * 
     * @param project
     *            project
     * @param natureId
     *            nature
     * @throws CoreException
     *             thrown on errors
     */
    private void addNature(final IProject project, final String natureId) throws CoreException {
        final IProjectDescription description = project.getDescription();
        final String[] natures = description.getNatureIds();
        final String[] newNatures = new String[natures.length + 1];
        System.arraycopy(natures, 0, newNatures, 0, natures.length);
        newNatures[natures.length] = natureId;
        description.setNatureIds(newNatures);
        project.setDescription(description, null);
    }
    
    /**
     * Toggles sample nature on a project
     * 
     * @param project
     *            to have sample nature added or removed
     */
    private void toggleNature(final IProject project) {
        try {
            if (!MavenPhpUtils.isPHPProject(project)) {
                // we need a php project
                final MessageBox box = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_ERROR | SWT.ABORT);
                box.setText(Messages.AddNatureAction_ErrorTitle);
                box.setMessage(Messages.AddNatureAction_NeedPhpProject);
                box.open();
                return;
            }
            
            if (MavenPhpUtils.isPhpmavenProject(project)) {
                // this is already a php-maven project
                final MessageBox box = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_ERROR | SWT.ABORT);
                box.setText(Messages.AddNatureAction_ErrorTitle);
                box.setMessage(Messages.AddNatureAction_AlreadyPhpMavenProject);
                box.open();
                return;
            }
            
            if (!MavenPhpUtils.isMavenProject(project)) {
                // activate maven support and generate a simple pom.
                final IFile pomFile = project.getFile("pom.xml"); //$NON-NLS-1$
                if (!pomFile.exists()) {
                    pomFile.create(new ByteArrayInputStream(AddNatureAction.POM_CONTENTS.replace("${artifactId}", project.getName()).getBytes()), true, new NullProgressMonitor()); //$NON-NLS-1$
                }
                this.addNature(project, IMavenConstants.NATURE_ID);
            } else {
                final IMavenProjectFacade facade = MavenPhpUtils.fetchProjectFacade(project);
                if (!"php".equals(facade.getMavenProject().getPackaging())) { //$NON-NLS-1$
                    final MessageBox box = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_ERROR | SWT.ABORT);
                    box.setText(Messages.AddNatureAction_ErrorTitle);
                    box.setMessage(Messages.AddNatureAction_InvalidPackaging);
                    box.open();
                    return;
                }
            }
            
            // add the phpmaven nature and build container
            this.addNature(project, PhpmavenCorePlugin.PHPMAVEN_NATURE_ID);
            final IScriptProject scriptProject = DLTKCore.create(project);
            final IBuildpathEntry[] entries = scriptProject.getRawBuildpath();
            final IBuildpathEntry[] newEntries = Arrays.copyOf(entries, entries.length + 1);
            newEntries[entries.length] = DLTKCore.newContainerEntry(new Path(PhpmavenCorePlugin.BUILDPATH_CONTAINER_ID));
            scriptProject.setRawBuildpath(newEntries, new NullProgressMonitor());
            
            // XXX: fix the include path (src/main/php and src/test/php)
        } catch (final CoreException e) {
            PhpmavenUiPlugin.logError("Error while toggle PHP-Maven nature", e); //$NON-NLS-1$
        }
    }
    
}
