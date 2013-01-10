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
package org.eclipse.edt.ide.ui.internal.outline;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;

public class OutlineContentProvider implements ITreePathContentProvider {
	private OutlineAdapterFactory factory;

	public OutlineContentProvider(OutlineAdapterFactory factory) {
		super();
		this.factory = factory;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		return factory.adapt(parentElement).getChildren(parentElement);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		return factory.adapt(element).getParent(element);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		return factory.adapt(element).hasChildren(element);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		return factory.adapt(inputElement).getChildren(inputElement);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
//		System.err.println("dispose() in OutlineContentProvider unimplemented");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
//		System.err.println("inputChanged(viewer, oldInput, newInput) in OutlineContentProvider unimplemented");
	}
	
	public Object[] getChildren(TreePath parentPath){
		return getChildren(parentPath.getLastSegment());
	}

	public boolean hasChildren(TreePath path){
		return hasChildren(path.getLastSegment());
	}


	public TreePath[] getParents(Object element){
		
		ArrayList segments = new ArrayList();
		segments.add(0, getParent(element));
		return new TreePath[] {new TreePath(segments.toArray())};
	}


}
