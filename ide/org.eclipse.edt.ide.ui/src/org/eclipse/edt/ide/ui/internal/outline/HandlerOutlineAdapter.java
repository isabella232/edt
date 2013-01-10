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

import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.jface.text.IRegion;

public class HandlerOutlineAdapter extends AbstractOutlineAdapter {

	public HandlerOutlineAdapter(EGLEditor editor) {
		super(editor);
		nodeIcon = PluginImages.DESC_OBJS_HANDLER;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		Handler handler = (Handler) parentElement;
		return filterOutProperties(handler.getContents()).toArray();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		Handler handler = (Handler) element;
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(handler.getName().getCanonicalName());
		
		Name handlerType = handler.getSubType();
		buffer.append(" : "); //$NON-NLS-1$
		if (handlerType != null)
			buffer.append(handlerType.getCanonicalString());
		
		return buffer.toString();
	}
	
	public IRegion getHighlightRange(Object element) {
		Handler node = (Handler) element;
		return getPartNameHighlightRange(node);
	}

}
