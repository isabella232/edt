/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.javascriptdev.ide;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class DevJavascriptGenFolderFilter extends ViewerFilter {
	
	private final IPath PROJECT_RELATIVE_PATH = new Path(Activator.OUTPUT_DIRECTORY);

	@Override
	public boolean select(Viewer arg0, Object parent, Object element) {
		return !(element instanceof IFolder) || !((IFolder)element).getProjectRelativePath().equals(PROJECT_RELATIVE_PATH);
	}
}
