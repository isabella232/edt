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

import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.graphics.Image;

public class RecordOutlineAdapter extends AbstractOutlineAdapter {

	public RecordOutlineAdapter(EGLEditor editor) {
		super(editor);
		nodeIcon = PluginImages.DESC_OBJS_RECORD;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		Record record = (Record) parentElement;
		final ArrayList structureItems = new ArrayList(); 
		record.accept(new AbstractASTVisitor() {
			public boolean visit(StructureItem structureItem) {
				structureItems.add(structureItem);
				return false;
			};
		});
		return structureItems.toArray();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		Record record = (Record) element;

		StringBuffer buffer = new StringBuffer();
		buffer.append(record.getName().getCanonicalName());
		
		String type = "BasicRecord";
		if(record.hasSubType())
		{
			type = record.getSubType().getCanonicalString();	// Record Type is never null since the Model already supply a default
		}
		buffer.append(" : ");//$NON-NLS-1$
		if (type != null)
			buffer.append(type);
				
		return buffer.toString();
	}

	public IRegion getHighlightRange(Object element) {
		Record node = (Record) element;
		return getPartNameHighlightRange(node);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		nodeIcon = PluginImages.DESC_OBJS_RECORD;
		return super.getImage(element);
	}
}
