/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.internal.ui.java;

import org.eclipse.core.resources.IFile;
import org.eclipse.edt.debug.core.IEGLDebugCoreConstants;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Filters out *.eglsmap files from the viewer.
 */
public class SMAPViewerFilter extends ViewerFilter
{
	@Override
	public boolean select( Viewer arg0, Object parent, Object element )
	{
		return !(element instanceof IFile) || !IEGLDebugCoreConstants.SMAP_EXTENSION.equals( ((IFile)element).getFileExtension() );
	}
}
