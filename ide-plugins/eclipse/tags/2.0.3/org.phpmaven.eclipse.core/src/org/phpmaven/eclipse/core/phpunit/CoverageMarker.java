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
package org.phpmaven.eclipse.core.phpunit;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.texteditor.MarkerUtilities;

/**
 */
public class CoverageMarker {
    
    /**
     * Type id of the coverage marker
     */
    public static String TYPE_ID = "org.phpmaven.eclipse.core.markers.coverage"; //$NON-NLS-1$
    
    /**
     * Type id of the coverage OK marker
     */
    protected static String TYPE_OK_ID = "org.phpmaven.eclipse.core.markers.coverageOK"; //$NON-NLS-1$
    
    /**
     * Type id of the coverage NONE marker
     */
    protected static String TYPE_NONE_ID = "org.phpmaven.eclipse.core.markers.coverageNONE"; //$NON-NLS-1$
    
    /**
     * Attribute: state of the marker
     */
    public static String ATTR_STATE = "state"; //$NON-NLS-1$
    
    /**
     * Attribute: percentage of the coverage
     */
    public static String ATTR_PERCENTAGE = "percentage"; //$NON-NLS-1$
    
    /**
     * Attribute: Number of calls into the target type
     */
    public static String ATTR_CALLS = "calls"; //$NON-NLS-1$
    
    /**
     * Attribute: Location
     */
    public static String ATTR_LOCATION = "location"; //$NON-NLS-1$
    
    /**
     * State: Full coverage (100%)
     */
    protected static String STATE_COVERAGE_FULL = "full"; //$NON-NLS-1$
    
    /**
     * State: Partly coverage (>0% and <100%)
     */
    protected static String STATE_COVERAGE_PART = "full"; //$NON-NLS-1$
    
    /**
     * State: No coverage (0%)
     */
    protected static String STATE_COVERAGE_NONE = "full"; //$NON-NLS-1$
    
    /**
     * State: Not relevant (f.e. empty lines)
     */
    protected static String STATE_COVERAGE_SKIPPED = "full"; //$NON-NLS-1$
    
    /**
     * state enum
     */
    public enum State {
        /** State: Full coverage (100%) */
        FULL(CoverageMarker.STATE_COVERAGE_FULL),
        /** Partly coverage (>0% and <100%) */
        PART(CoverageMarker.STATE_COVERAGE_PART),
        /** State: No coverage (0%) */
        NONE(CoverageMarker.STATE_COVERAGE_NONE),
        /** State: Not relevant (f.e. empty lines) */
        SKIPPED(CoverageMarker.STATE_COVERAGE_SKIPPED);
        
        /** state name */
        String strName;
        
        /**
         * constructor
         * 
         * @param name
         *            state name
         */
        State(final String name) {
            this.strName = name;
        }
        
        @Override
        public String toString() {
            return this.strName;
        }
        
    }
    
    /**
     * Converts string to state
     * 
     * @param name
     *            state name
     * @return state enum value
     */
    protected static State stringToState(final String name) {
        if (State.FULL.strName.equals(name)) {
            return State.FULL;
        }
        if (State.PART.strName.equals(name)) {
            return State.PART;
        }
        if (State.NONE.strName.equals(name)) {
            return State.NONE;
        }
        if (State.SKIPPED.strName.equals(name)) {
            return State.SKIPPED;
        }
        throw new IllegalArgumentException("invalid state name: " + name); //$NON-NLS-1$
    }
    
    /**
     * Creates a line marker
     * 
     * @param res
     *            resource
     * @param state
     *            state
     * @param percentage
     *            percentage
     * @param calls
     *            number of calls
     * @param line
     *            line number
     * @param charEnd
     *            char end
     * @param charStart
     *            char start
     * @return marker
     * @throws CoreException
     *             thrown if the marker could not be created
     */
    public static IMarker createLineMarker(final IResource res, final State state, final float percentage, final int calls, final int line, final int charStart, final int charEnd)
            throws CoreException {
        final IMarker marker = res.createMarker(calls == 0 ? CoverageMarker.TYPE_NONE_ID : CoverageMarker.TYPE_OK_ID);
        marker.setAttribute(CoverageMarker.ATTR_CALLS, calls);
        marker.setAttribute(CoverageMarker.ATTR_LOCATION, "line:" + line); //$NON-NLS-1$
        marker.setAttribute(CoverageMarker.ATTR_PERCENTAGE, Float.valueOf(percentage));
        marker.setAttribute(CoverageMarker.ATTR_STATE, state.strName);
        MarkerUtilities.setLineNumber(marker, line);
        MarkerUtilities.setCharStart(marker, charStart);
        MarkerUtilities.setCharEnd(marker, charEnd);
        marker.setAttribute(IMarker.SEVERITY, calls == 0 ? IMarker.SEVERITY_WARNING : IMarker.SEVERITY_INFO);
        marker.setAttribute(IMarker.MESSAGE, "Code coverage: " + calls + " calls"); //$NON-NLS-1$ //$NON-NLS-2$
        return marker;
    }
    
    /**
     * @return the calls
     */
    public int getCalls() {
        return this.calls;
    }
    
    /**
     * @return the percentage
     */
    public float getPercentage() {
        return this.percentage;
    }
    
    /**
     * @return the line location
     */
    public int getLineNumber() {
        return this.isLineCoverage() ? Integer.parseInt(this.location.substring(5)) : 0;
    }
    
    /**
     * Returns true if this marker represents a line coverage
     * 
     * @return boolean
     */
    public boolean isLineCoverage() {
        return this.location.startsWith("line:"); //$NON-NLS-1$
    }
    
    /**
     * @return the state
     */
    public State getState() {
        return this.state;
    }
    
    /**
     * number of calls
     */
    private int calls;
    
    /**
     * percentage
     */
    private float percentage;
    
    /**
     * full location string
     */
    private String location;
    
    /**
     * the state
     */
    private State state;
    
    /**
     * Converts the marker to a coverage marker
     * 
     * @param marker
     *            marker
     * @return coverage marker
     * @throws CoreException
     *             thrown if the marker could not be accessed
     */
    public static CoverageMarker toCoverageMarker(final IMarker marker) throws CoreException {
        if (!CoverageMarker.TYPE_ID.equals(marker.getType()) && !CoverageMarker.TYPE_OK_ID.equals(marker.getType()) && !CoverageMarker.TYPE_NONE_ID.equals(marker.getType())) {
            throw new IllegalArgumentException("Cannot convert unknown marker type to coverage marker: " + marker.getType()); //$NON-NLS-1$
        }
        final CoverageMarker result = new CoverageMarker();
        result.calls = marker.getAttribute(CoverageMarker.ATTR_CALLS, 0);
        result.percentage = ((Float) marker.getAttribute(CoverageMarker.ATTR_PERCENTAGE)).floatValue();
        result.location = marker.getAttribute(CoverageMarker.ATTR_LOCATION, null);
        result.state = CoverageMarker.stringToState(marker.getAttribute(CoverageMarker.ATTR_STATE, null));
        return result;
    }
    
}
