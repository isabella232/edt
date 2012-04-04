/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.editor;

public interface IPartitions {
	public final static String EGL_PARTITIONING = "___egl_partitioning"; //$NON-NLS-1$
	public final static String EGL_MULTI_LINE_COMMENT = "__egl_multiline_comment"; //$NON-NLS-1$
	public final static String EGL_SINGLE_LINE_COMMENT = "__egl_singleline_comment"; //$NON-NLS-1$
	public final static String SQL_CONTENT_TYPE = "__egl_sql"; //$NON-NLS-1$
	public final static String SQL_CONDITION_CONTENT_TYPE = "__egl_sqlCondition"; //$NON-NLS-1$
}
