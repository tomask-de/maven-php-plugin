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

package org.phpmaven.eclipse.ui.views;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;
import org.phpmaven.eclipse.core.ResourceUtils;
import org.phpmaven.eclipse.core.mvn.ITestExecutionTooling.ITestResults;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * View to display every tool running
 * 
 * @author Martin Eisengardt <mep_eisen@web.de>
 */
public class PhpUnitResultView extends ViewPart implements IResultDisplayPart {
    
    /** the result composite */
    private PhpUnitResultComposite resultComposite;
    
    /**
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl(final Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new FillLayout());
        this.resultComposite = new PhpUnitResultComposite(composite);
        
        /*final IAction saveAction = new SaveAction();
        final IActionBars actionBars = getViewSite().getActionBars();
        final IToolBarManager toolBar = actionBars.getToolBarManager();
        toolBar.add(saveAction);*/
        // TODO test it and enable it.
    }
    
    /**
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
        // ignore
    }
    
    /**
     * Sets the command line and sets the state to running
     * 
     * @param commandline
     *            command line
     */
    @Override
    public void setCommandLine(final String commandline) {
        this.resultComposite.setCommandLine(commandline);
    }
    
    /**
     * Sets the failure info (including stdout report)
     * 
     * @param stdout
     *            std out
     */
    @Override
    public void setFailure(final String stdout) {
        this.resultComposite.setFailure(stdout);
    }
    
    /**
     * Sets the results
     * 
     * @param stdout
     *            stdout
     * @param results
     *            results
     */
    @Override
    public void setResults(final String stdout, final ITestResults results) {
        this.resultComposite.setResults(stdout, results);
    }
    
    /**
     * Save as action.
     */
    private final class SaveAction extends Action {
        
        /**
         * Constructor.
         */
        public SaveAction() {
            this.setText("Save");
            this.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_SAVEAS_EDIT));
        }

        @Override
        public void run() {
            final ITestResults testResults = PhpUnitResultView.this.resultComposite.getResults();
            if (testResults == null) {
                final MessageBox box = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_INFORMATION | SWT.OK);
                box.setMessage("No test result available or non-recoverable error.");
                box.open();
                return;
            }
            
            final SaveAsDialog fileDialog = new SaveAsDialog(getSite().getShell());
            fileDialog.setOriginalName("unnamed.phpmaven.xml");
            fileDialog.create();
            if (fileDialog.open() != Window.CANCEL) {
                final IPath filePath = fileDialog.getResult();
                if (filePath != null) {
                    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
                    final IFile file = workspace.getRoot().getFile(filePath);
                    final IContainer container = file.getParent();
                    try {
                        if (container.getType() == IResource.FOLDER) {
                                ResourceUtils.mkdirs((IFolder) container);
                        }
                        
                        final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                        final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                        final Document reportDoc = docBuilder.parse(new InputSource(new StringReader(testResults.getState().getXmlContent())));
                        final Document coverageDoc = docBuilder.parse(new InputSource(new StringReader(testResults.getState().getCoverageContent())));
                        
                        final Node reportRoot = reportDoc.getChildNodes().item(0);
                        final Node coverageRoot = coverageDoc.getChildNodes().item(0);
                        
                        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        final Transformer transformer = transformerFactory.newTransformer();
                        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); //$NON-NLS-1$
                        transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
                        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); //$NON-NLS-1$
                        
                        final StringWriter reportWriter = new StringWriter();
                        final StringWriter coverageWriter = new StringWriter();
                        final StreamResult reportResult = new StreamResult(reportWriter);
                        final StreamResult coverageResult = new StreamResult(coverageWriter);
                        transformer.transform(new DOMSource(reportRoot), reportResult);
                        transformer.transform(new DOMSource(coverageRoot), coverageResult);
                        
                        final StringBuffer buffer = new StringBuffer();
                        buffer.append("<?xml version=\"1.0\" encoding = \"UTF-8\"?>\n"); //$NON-NLS-1$
                        buffer.append("<testReport>\n"); //$NON-NLS-1$
                        buffer.append("<meta xmlType=\"").append(testResults.getState().getXmlType()).append("\" coverageType=\"").append(testResults.getState().getCoverageType()).append("\" started=\"").append(testResults.getState().getDateStarted()).append("\" ended=\"").append(testResults.getState().getEndDate()).append("\" />"); //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$  //$NON-NLS-4$  //$NON-NLS-5$
                        buffer.append(reportWriter.toString());
                        buffer.append(coverageWriter.toString());
                        buffer.append("</testReport>\n"); //$NON-NLS-1$
                        
                        if (file.exists()) {
                            file.setContents(new ByteArrayInputStream(buffer.toString().getBytes()), IResource.FORCE, new NullProgressMonitor());
                        } else {
                            file.create(new ByteArrayInputStream(buffer.toString().getBytes()), IResource.FORCE, new NullProgressMonitor());
                        }
                        
                        final IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
                        getSite().getWorkbenchWindow().getActivePage().openEditor(new FileEditorInput(file), desc.getId());
                    }
                    catch (Exception ex) {
                        // TODO Exception handling
                    }
                    
                }
            }
        }
        
        
        
    }

}
