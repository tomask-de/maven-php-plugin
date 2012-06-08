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
package org.phpmaven.eclipse.core.internal.phpunit;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.ui.texteditor.IMarkerUpdater;
import org.phpmaven.eclipse.core.phpunit.CoverageMarker;

/**
 */
public class MarkerUpdater implements IMarkerUpdater {
    
    /**
     * @see org.eclipse.ui.texteditor.IMarkerUpdater#getAttribute()
     */
    @Override
    public String[] getAttribute() {
        return new String[] { IMarker.LINE_NUMBER, CoverageMarker.ATTR_CALLS, CoverageMarker.ATTR_LOCATION, CoverageMarker.ATTR_PERCENTAGE, CoverageMarker.ATTR_STATE };
    }
    
    /**
     * @see org.eclipse.ui.texteditor.IMarkerUpdater#getMarkerType()
     */
    @Override
    public String getMarkerType() {
        return CoverageMarker.TYPE_ID;
    }
    
    /**
     * @see org.eclipse.ui.texteditor.IMarkerUpdater#updateMarker(org.eclipse.core.resources.IMarker,
     *      org.eclipse.jface.text.IDocument, org.eclipse.jface.text.Position)
     */
    @Override
    public boolean updateMarker(final IMarker marker, final IDocument document, final Position position) {
        try {
            final CoverageMarker coverage = CoverageMarker.toCoverageMarker(marker);
            if (coverage.isLineCoverage()) {
                // if (position == null)
                // {
                // return true;
                // }
                // if (position.isDeleted())
                // {
                // return false;
                // }
                //
                // boolean offsetsInitialized = false;
                // boolean offsetsChanged = false;
                // int markerStart = MarkerUtilities.getCharStart(marker);
                // int markerEnd = MarkerUtilities.getCharEnd(marker);
                // if (markerStart != -1 && markerEnd != -1)
                // {
                // offsetsInitialized = true;
                // int offset = position.getOffset();
                // if (markerStart != offset)
                // {
                // MarkerUtilities.setCharStart(marker, offset);
                // offsetsChanged = true;
                // }
                // offset += position.getLength();
                // if (markerEnd != offset)
                // {
                // MarkerUtilities.setCharEnd(marker, offset);
                // offsetsChanged = true;
                // }
                // }
                //
                // if (!offsetsInitialized || (offsetsChanged &&
                // MarkerUtilities.getLineNumber(marker) != -1))
                // {
                // try
                // {
                // int drlLineNumber =
                // document.getLineOfOffset(position.getOffset()) + 1;
                // marker.setAttribute(IDroolsDebugConstants.DRL_LINE_NUMBER,
                // drlLineNumber);
                // }
                // catch (Throwable t)
                // {
                // DroolsEclipsePlugin.log(t);
                // }
                // }
                // return true;
            }
        } catch (final CoreException ex) {
            // TODO
        }
        /*
         * try { 40 String JavaDoc id= (String JavaDoc)
         * marker.getAttribute(IJavaSearchUIConstants.ATT_JE_HANDLE_ID); 41 if
         * (id != null) { 42 IJavaElement j= JavaCore.create(id); 43 if (j ==
         * null || !j.exists() || !j.isStructureKnown()) { 44 IResource
         * resource= marker.getResource(); 45 if
         * (MarkerUtilities.getCharStart(marker) != -1 &&
         * MarkerUtilities.getCharEnd(marker) != -1 && resource instanceof
         * IFile) { 46 Object JavaDoc o= JavaCore.create(resource); 47 if (o
         * instanceof ICompilationUnit) { 48 IJavaElement element=
         * ((ICompilationUnit) o).getElementAt(position.getOffset()); 49 if
         * (element != null) { 50
         * marker.setAttribute(IJavaSearchUIConstants.ATT_JE_HANDLE_ID,
         * element.getHandleIdentifier()); 51
         * marker.setAttribute(IJavaSearchUIConstants.ATT_JE_HANDLE_ID_CHANGED,
         * new Boolean JavaDoc(true)); 52 return true; 53 } 54 else 55 return
         * false; 56 } 57 } 58 } else { 59 return true; 60 } 61 } 62 else 63 //
         * no java search marker 64 return true; 65 } catch (CoreException ex) {
         * 66 ExceptionHandler.handle(ex,
         * SearchMessages.getString("Search.Error.markerAttributeAccess.title"),
         * SearchMessages
         * .getString("Search.Error.markerAttributeAccess.message"));
         * //$NON-NLS-2$ //$NON-NLS-1$ 67 } 68 return false;
         * 
         * Read more:
         * http://kickjava.com/src/org/eclipse/jdt/internal/ui/search/
         * JavaSearchMarkerUpdater.java.htm#ixzz1I6NHiojG
         * 
         * // TODO Auto-generated method stub
         */
        // try
        // {
        //
        // }
        // catch (CoreException e)
        // {
        // // TODO
        // }
        return false;
    }
    
}
