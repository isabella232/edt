/*******************************************************************************
 * Copyright © 2010, 2013 IBM Corporation and others.
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

import org.eclipse.edt.ide.rui.visualeditor.internal.views.dataview.model.PageDataNode;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


public class PageDataViewLabelProvider extends LabelProvider {

	public void dispose() {
		super.dispose();
	}

	public Image getImage(Object element) {
		PageDataNode pageDataNode = (PageDataNode)element;
		return pageDataNode.getImage();
	}

	public String getText(Object element) {
		PageDataNode pageDataNode = (PageDataNode)element;
		return pageDataNode.getName();
	}

}
