/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.core;

public interface IEGLDebugCoreConstants
{
	/**
	 * The ID of the EGL Java model presentation.
	 */
	public static final String EGL_JAVA_MODEL_PRESENTATION_ID = "org.eclipse.edt.debug.ui.presentation.java"; //$NON-NLS-1$
	
	/**
	 * The EGL stratum name (as used in the SMAP).
	 */
	public static final String EGL_STRATUM = "egl"; //$NON-NLS-1$
	
	/**
	 * The SMAP file extension.
	 */
	public static final String SMAP_EXTENSION = "eglsmap"; //$NON-NLS-1$
	
	/**
	 * The ID for EGL line breakpoint markers.
	 */
	public static final String EGL_LINE_BREAKPOINT_MARKER_ID = EDTDebugCorePlugin.PLUGIN_ID + ".eglLineBreakpointMarker"; //$NON-NLS-1$
	
	/**
	 * The Run to line attribute for EGL line breakpoints.
	 */
	public static final String RUN_TO_LINE = EDTDebugCorePlugin.PLUGIN_ID + ".runToLine"; //$NON-NLS-1$
	
	/**
	 * The path to the file which the run to line operation was performed.
	 */
	public static final String RUN_TO_LINE_PATH = EDTDebugCorePlugin.PLUGIN_ID + ".runToLinePath"; //$NON-NLS-1$
	
	/**
	 * Preference key for the boolean value indicating if filtering is enabled.
	 */
	public static final String PREFERENCE_TYPE_FILTERS_ENABLED = EDTDebugCorePlugin.PLUGIN_ID + ".typeFiltersEnabled"; //$NON-NLS-1$
	
	/**
	 * Preference key for the string value listing the enablement for each filter.
	 */
	public static final String PREFERENCE_TYPE_FILTER_ENABLEMENT = EDTDebugCorePlugin.PLUGIN_ID + ".typeFilterEnablement"; //$NON-NLS-1$
	
	/**
	 * Preference key for the string value listing the step type for each filter.
	 */
	public static final String PREFERENCE_TYPE_FILTER_STEP_TYPES = EDTDebugCorePlugin.PLUGIN_ID + ".typeFilterStepTypes"; //$NON-NLS-1$
}
