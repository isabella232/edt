/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.views.dataview;

import org.eclipse.edt.ide.rui.visualeditor.internal.views.dataview.model.PageDataModel;
import org.eclipse.edt.ide.rui.visualeditor.internal.views.dataview.model.PageDataNode;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


public class PageDataViewContentProvider implements ITreeContentProvider {
	
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof PageDataModel){
			PageDataModel model = (PageDataModel)inputElement;
			return model.getRootPageDataNodes().toArray();
		}else{
			return new Object[0];
		}
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public Object[] getChildren(Object parentElement) {
		PageDataNode pageDataNode = (PageDataNode)parentElement;
		return pageDataNode.getChildren().toArray();
	}

	public Object getParent(Object element) {
		PageDataNode pageDataNode = (PageDataNode)element;
		return pageDataNode.getParent();
	}

	public boolean hasChildren(Object element) {
		PageDataNode pageDataNode = (PageDataNode)element;
		return pageDataNode.hasChildren();
	}

	public void dispose() {
	}

}
