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
package org.eclipse.edt.ide.ui.internal;

import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IParent;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.viewsupport.EGLElementLabels;
import org.eclipse.edt.ide.ui.internal.viewsupport.ElementImageProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class WorkbenchAdapter implements IWorkbenchAdapter{
	protected static final Object[] NO_CHILDREN= new Object[0];
	
	private ElementImageProvider fImageProvider;

	public WorkbenchAdapter() {
		fImageProvider= new ElementImageProvider();
	}

	public Object[] getChildren(Object element) {
		if (element instanceof IParent) {
			try {
				return ((IParent)element).getChildren();
			} catch(EGLModelException e) {
				EDTUIPlugin.log(e); 
			}
		}
		return NO_CHILDREN;
	}

	public ImageDescriptor getImageDescriptor(Object element) {
		return fImageProvider.getEGLImageDescriptor(
			(IEGLElement)element, 
			ElementImageProvider.OVERLAY_ICONS | ElementImageProvider.SMALL_ICONS);
	}

	public String getLabel(Object element) {
		return EGLElementLabels.getTextLabel(element, EGLElementLabels.M_PARAMETER_TYPES);
	}

	public Object getParent(Object element) {
		if (element instanceof IEGLElement)
			return ((IEGLElement)element).getParent();
		return null;
	}
}
