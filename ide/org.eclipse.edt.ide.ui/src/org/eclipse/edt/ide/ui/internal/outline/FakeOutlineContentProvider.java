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
package org.eclipse.edt.ide.ui.internal.outline;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

public class FakeOutlineContentProvider implements ITreeContentProvider {

	private TreeViewer viewer;

	private ArrayList elements;
	
	private static class Element {
		private Element child;
		private int serial;
		
		public Element(int level) {
			if(level > 0) {
				child = new Element(level - 1);
			}
			serial = (int) (Math.random() * 10000);
		}
		
		public Object[] getChildren() {
			return child == null ? null : new Object[] { child };
		}
		
		public String toString() {
			return Integer.toString(serial);
		}

	}
	
	private int NUMELEMENTS;
	private int MAXLEVELS;
	
	public FakeOutlineContentProvider() {
		super();
		
		NUMELEMENTS = 30;
		MAXLEVELS = 1;
		
		elements = new ArrayList();
		for (int i = 0; i < NUMELEMENTS; i++) {
			elements.add(new Element(MAXLEVELS));
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		Element element = (Element) parentElement;
		return element.getChildren();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		Object[] children = getChildren(element);
		return children != null && children.length > 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		return elements.toArray();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		TreeViewer treeViewer = (TreeViewer) viewer;
		this.viewer = treeViewer;
		
//		if (viewer != null && newInput != null) {
//			treeViewer.setSorter(new ViewerSorter() {
//				public int compare(Viewer viewer, Object e1, Object e2) {
//					return elements.indexOf(e1) - elements.indexOf(e2);
//				}
//			});
//		}
		
	}
	
	public void refresh() {
		// Do the model updates
		int pos = (int) Math.round(Math.random() * 10);
		
		Element newElement = new Element(MAXLEVELS); 
		elements.add(pos, newElement);
		
		newElement = new Element(MAXLEVELS);
		elements.add(pos + 3, newElement);
		
		viewer.add(viewer.getInput(), newElement);
	}

}
