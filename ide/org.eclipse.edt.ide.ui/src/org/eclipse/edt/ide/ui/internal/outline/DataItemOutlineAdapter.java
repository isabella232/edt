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

import org.eclipse.edt.compiler.core.ast.DataItem;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.jface.text.IRegion;

public class DataItemOutlineAdapter extends AbstractOutlineAdapter {

	public DataItemOutlineAdapter(EGLEditor editor) {
		super(editor);
		nodeIcon = PluginImages.DESC_OBJS_DATAITEM;
	}
	/* (non-Javadoc)
	* @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#getText(java.lang.Object)
	*/
	public String getText(Object element) {
		DataItem item = (DataItem) element;
		return item.getName().getCanonicalName() + " : " + formatType(item.getType()); //$NON-NLS-1$
	}

	public IRegion getHighlightRange(Object element) {
		DataItem node = (DataItem) element;
		return getPartNameHighlightRange(node);
	}
}
