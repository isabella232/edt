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

import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.jface.text.IRegion;

public class ExternalTypeOutlineAdapter extends AbstractOutlineAdapter {

	public ExternalTypeOutlineAdapter(EGLEditor editor) {
		super(editor);
		nodeIcon = PluginImages.DESC_OBJS_EXTERNALTYPE;
	}
	
	public Object[] getChildren(Object parentElement) {
		ExternalType program = (ExternalType) parentElement;
		return filterOutProperties(program.getContents()).toArray();
	}
	
	/* (non-Javadoc)
	* @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#getText(java.lang.Object)
	*/
	public String getText(Object element) {
		ExternalType item = (ExternalType) element;
		StringBuffer buffer = new StringBuffer();
		buffer.append(item.getName().getCanonicalName());

		if(item.hasSubType()){
			String programType = item.getSubType().getCanonicalString();
			buffer.append(" : " + programType); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return buffer.toString();

	}

	public IRegion getHighlightRange(Object element) {
		ExternalType node = (ExternalType) element;
		return getPartNameHighlightRange(node);
	}
}
