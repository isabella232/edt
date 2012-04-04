/*******************************************************************************
 * Copyright © 2004, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.search;

import org.eclipse.core.resources.IResource;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class NameSorter extends ViewerSorter
{
	public int compare(Viewer viewer, Object e1, Object e2) {
		String property1= getProperty(e1);
		String property2= getProperty(e2);
		return collator.compare(property1, property2);
	}

	protected String getProperty(Object element) {
		if (element instanceof IEGLElement)
			return ((IEGLElement)element).getElementName();
		if (element instanceof IResource)
			return ((IResource)element).getName();
		return ""; //$NON-NLS-1$
	}

	public boolean isSorterProperty(Object element, String property) {
		return true;
	}
    
}
