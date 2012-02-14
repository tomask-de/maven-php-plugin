/*******************************************************************************
 * Copyright (c) 2009, 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
// originally taken from pti

package org.phpmaven.eclipse.core.php;

import org.eclipse.core.resources.IFile;

/**
 * The source file
 */
public interface ISourceFile {
    
    /**
     * Returns the absolute character position for start of given line number
     * 
     * @param lineNumber
     * @return byte position
     * @throws IndexOutOfBoundsException
     *             thrown for invalid line numbers
     */
    public int lineStart(int lineNumber) throws IndexOutOfBoundsException;
    
    /**
     * Returns the absolute character position for end of given line number
     * 
     * @param lineNumber
     * @return byte position
     * @throws IndexOutOfBoundsException
     *             thrown for invalid line numbers
     */
    public int lineEnd(int lineNumber) throws IndexOutOfBoundsException;
    
    /**
     * The tab count for line starting
     * 
     * @param lineNumber
     * @return tab count
     * @throws IndexOutOfBoundsException
     *             thrown for invalid line numbers
     */
    public int lineStartTabCount(int lineNumber) throws IndexOutOfBoundsException;
    
    /**
     * Returns the corresponding file
     * 
     * @return file
     */
    public IFile getFile();
    
    /**
     * Returns the number of lines in this file
     * 
     * @return number of lines
     */
    public int getNumberOfLines();
}
