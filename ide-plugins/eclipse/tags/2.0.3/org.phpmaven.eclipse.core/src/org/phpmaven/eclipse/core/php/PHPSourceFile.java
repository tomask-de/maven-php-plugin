/*******************************************************************************
 * Copyright (c) 2009, 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
// originally taken from pti

package org.phpmaven.eclipse.core.php;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.php.internal.core.PHPToolkitUtil;

/**
 * The source file
 */
@SuppressWarnings("restriction")
public class PHPSourceFile implements ISourceFile {
    
    /** the file */
    private final IFile file;
    /** the line starts */
    private ArrayList<Integer> lineStarts;
    /** the line ends */
    private ArrayList<Integer> lineEnds;
    /** the line start tab count */
    private ArrayList<Integer> lineStartTabCount;
    /** the lines count */
    private int linesCount;
    
    /**
     * the source module
     */
    private final ISourceModule module;
    
    /**
     * The php source file
     * 
     * @param file
     *            source file
     * @throws CoreException
     *             core exception
     * @throws IOException
     *             io exception
     */
    public PHPSourceFile(final IFile file) throws CoreException, IOException {
        Assert.isNotNull(file);
        this.file = file;
        this.determineLinePositions();
        this.module = PHPToolkitUtil.getSourceModule(file);
    }
    
    /**
     * Determines the line positions
     * 
     * @throws CoreException
     * @throws IOException
     */
    private void determineLinePositions() throws CoreException, IOException {
        this.lineStarts = new ArrayList<Integer>();
        this.lineEnds = new ArrayList<Integer>();
        this.lineStartTabCount = new ArrayList<Integer>();
        
        InputStreamReader isr;
        isr = new InputStreamReader(this.file.getContents());
        
        int last = -1;
        int i = 0;
        int c;
        boolean countTabs = true;
        int tabCount = 0;
        while ((c = isr.read()) != -1) {
            if ((char) c == '\n') {
                this.lineStarts.add(new Integer(last + 1));
                this.lineEnds.add(new Integer(i));
                this.lineStartTabCount.add(new Integer(tabCount));
                ++this.linesCount;
                last = i;
                countTabs = true;
                tabCount = 0;
            } else if (countTabs && (char) c == '\t') {
                ++tabCount;
            } else if ((char) c != ' ') {
                countTabs = false;
            }
            i++;
        }
        
        this.lineStarts.add(new Integer(last + 1));
        this.lineEnds.add(new Integer(i));
        this.lineStartTabCount.add(new Integer(tabCount));
        ++this.linesCount;
    }
    
    @Override
    public int lineStart(final int lineNumber) throws IndexOutOfBoundsException {
        return this.lineStarts.get(lineNumber - 1);
    }
    
    @Override
    public int lineEnd(final int lineNumber) throws IndexOutOfBoundsException {
        return this.lineEnds.get(lineNumber - 1);
    }
    
    @Override
    public int lineStartTabCount(final int lineNumber) throws IndexOutOfBoundsException {
        return this.lineStartTabCount.get(lineNumber - 1);
    }
    
    @Override
    public IFile getFile() {
        return this.file;
    }
    
    @Override
    public int getNumberOfLines() {
        return this.linesCount;
    }
    
    /**
     * Returns the source module
     * 
     * @return source module
     */
    public ISourceModule getSourceModule() {
        return this.module;
    }
    
    /**
     * Find the line number for offset
     * 
     * @param offset
     * @return line number
     * @throws IndexOutOfBoundsException
     *             thrown for invalid offset
     */
    public int findLineNumberForOffset(final int offset) throws IndexOutOfBoundsException {
        if (offset < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (offset == 0) {
            return 1;
        }
        
        final int count = this.lineEnds.size();
        for (int i = 0; i < count; ++i) {
            final int end = this.lineEnds.get(i);
            if (end > offset) {
                return i + 1;
            }
        }
        
        throw new IndexOutOfBoundsException();
    }
}
