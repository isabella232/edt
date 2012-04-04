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

import org.eclipse.edt.compiler.core.ast.DataTable;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.jface.text.IRegion;

public class DataTableOutlineAdapter extends AbstractOutlineAdapter {

	public DataTableOutlineAdapter(EGLEditor editor) {
		super(editor);
		nodeIcon = PluginImages.DESC_OBJS_TABLE;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		DataTable table = (DataTable) parentElement;
		final ArrayList structureItems = new ArrayList();
		table.accept(new DefaultASTVisitor() {
			public boolean visit(StructureItem structureItem) {
				structureItems.add(structureItem);
				return false;
			};
		});
		return structureItems.toArray();
	}

	public String getText(Object element) {
		DataTable table = (DataTable) element;
		Name type = table.getSubType();
		return table.getName().getCanonicalString() + ((type == null) ? "" : " : " + type.getCanonicalString()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public IRegion getHighlightRange(Object element) {
		DataTable node = (DataTable) element;
		return getPartNameHighlightRange(node);
	}

}
