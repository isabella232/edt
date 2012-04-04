/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
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

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/** 
 * content provider to show a list of parts.
 */ 
public class ListContentProvide implements IStructuredContentProvider {
	List contents;	

	public ListContentProvide() {
	}
	
	public Object[] getElements(Object input) {
		if (contents != null && contents == input) {
			return contents.toArray();
		}
		return new Object[0];
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof List) {
			contents= (List)newInput;
		}
		else {
			contents= null;
		}
	}

	public void dispose() {
	}
	
	public boolean isDeleted(Object obj) {
		return contents != null && !contents.contains(obj);
	}
}
