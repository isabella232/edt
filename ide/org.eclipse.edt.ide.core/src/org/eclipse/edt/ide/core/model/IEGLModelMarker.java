/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.model;

import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;



/**
 * Markers used by the EGL model.
 * <p>
 * This interface declares constants only; it is not intended to be implemented
 * or extended.
 * </p>
 */
public interface IEGLModelMarker {

    /**
	 * EGL model transient problem marker type (value <code>"com.ibm.etools.egl.internal.model.core.transient_problem"</code>).
	 * This can be used to recognize those markers in the workspace that flag transient
	 * problems detected by the EGL tooling (such as a problem
	 * detected by the outliner, or a problem detected during a code completion)
	 */
	public static final String TRANSIENT_PROBLEM = EGLCore.PLUGIN_ID + ".transient_problem"; //$NON-NLS-1$

	/**
	 * EGL model task marker type (value <code>"com.ibm.etools.egl.internal.model.core.task"</code>).
	 * This can be used to recognize task markers in the workspace that correspond to tasks
	 * specified in EGL source comments and detected during compilation (for example, 'TO-DO: ...').
	 * Tasks are identified by a task tag, which can be customized through <code>EGLCore</code>
	 * option <code>"com.ibm.etools.egl.internal.model.core.compiler.taskTag"</code>.
	 * @since 2.1
	 */
	public static final String TASK_MARKER = EGLCore.PLUGIN_ID + ".task"; //$NON-NLS-1$

    
    /** 
	 * Id marker attribute (value <code>"arguments"</code>).
	 * Arguments are concatenated into one String, prefixed with an argument count (followed with colon
	 * separator) and separated with '#' characters. For example:
	 *     { "foo", "bar" } is encoded as "2:foo#bar",     
	 *     {  } is encoded as "0: "
	 * @since 2.0
	 */
	 public static final String ARGUMENTS = "arguments"; //$NON-NLS-1$
    
	/** 
	 * Id marker attribute (value <code>"id"</code>).
	 */
	 public static final String ID = "id"; //$NON-NLS-1$

	/** 
	 * Flags marker attribute (value <code>"flags"</code>).
	 * Reserved for future use.
	 */
	 public static final String FLAGS = "flags"; //$NON-NLS-1$

	/** 
	 * Cycle detected marker attribute (value <code>"cycleDetected"</code>).
	 * Used only on buildpath problem markers.
	 * The value of this attribute is either "true" or "false".
	 */
	 public static final String CYCLE_DETECTED = "cycleDetected"; //$NON-NLS-1$

	/**
	 * Build path problem marker type (value <code>"com.ibm.etools.egl.internal.model.core.buildpath_problem"</code>).
	 * This can be used to recognize those markers in the workspace that flag problems 
	 * detected by the EGL tooling during eglpath setting.
	 */
	public static final String BUILDPATH_PROBLEM_MARKER = EDTCoreIDEPlugin.PLUGIN_ID + ".buildpath_problem"; //$NON-NLS-1$
	
	/** 
	 * EGLPath file format marker attribute (value <code>"eglpathFileFormat"</code>).
	 * Used only on buildpath problem markers.
	 * The value of this attribute is either "true" or "false".
	 * 
	 * @since 2.0
	 */
	 public static final String EGLPATH_FILE_FORMAT = "eglpathFileFormat"; //$NON-NLS-1$
	
}
