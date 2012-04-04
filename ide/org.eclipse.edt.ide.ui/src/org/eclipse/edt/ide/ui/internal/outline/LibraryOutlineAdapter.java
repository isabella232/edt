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

import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.jface.text.IRegion;

public class LibraryOutlineAdapter extends AbstractOutlineAdapter {

	public LibraryOutlineAdapter(EGLEditor editor) {
		super(editor);
		nodeIcon = PluginImages.DESC_OBJS_LIBRARY;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		Library lib = (Library) parentElement;
		return filterOutProperties(lib.getContents()).toArray();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		Library lib = (Library) element;
		StringBuffer buffer = new StringBuffer();
		buffer.append(lib.getName().getCanonicalString());
		Name libType = lib.getSubType();
		buffer.append((libType == null) ? "" : " : " + libType.getCanonicalString()); //$NON-NLS-1$ //$NON-NLS-2$
		return buffer.toString();
	}
	public IRegion getHighlightRange(Object element) {
		Library node = (Library) element;
		return getPartNameHighlightRange(node);
	}

}
