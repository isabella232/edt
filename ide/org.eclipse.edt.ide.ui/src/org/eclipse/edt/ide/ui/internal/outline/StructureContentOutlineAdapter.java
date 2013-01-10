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

import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

public class StructureContentOutlineAdapter extends AbstractOutlineAdapter {

	public StructureContentOutlineAdapter(EGLEditor editor) {
		super(editor);
		nodeIcon = PluginImages.DESC_OBJS_STRUCTUREITEM;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		StructureItem item = (StructureItem) element;
		StringBuffer buffer = new StringBuffer();

		// name occurs type;
		if (item.getName() != null) { //isStructureItem both typed and untyped
			buffer.append(item.getName().getCanonicalName());
			buffer.append(" : "); //$NON-NLS-1$
			buffer.append(formatType(item.getType()));
		} 
		return buffer.toString();
	}

	public IRegion getHighlightRange(Object element) {
		
		StructureItem item = (StructureItem) element;
//JingNewModel - no need anymore?		
//		Node nameNode = null;
//		if (item.isStructureItem() item.getName() != null) {
//			EGLStructureItemNode si = (EGLStructureItemNode) item;
//			nameNode = si.getSimpleNameNode();
//		} else if (item.isEmbeddedRecordStructureItem()) {
//			EGLEmbeddedRecordStructureItemNode esi = (EGLEmbeddedRecordStructureItemNode) item;
//			nameNode = esi.getNameNode();
//		} else if (item.isUntypedStructureItem()) {
//			EGLUntypedStructureItemNode usi = (EGLUntypedStructureItemNode) item;
//			nameNode = usi.getSimpleNameNode();
//		} else if (item.isUntypedFillerStructureItem()) {
//			EGLUntypedFillerStructureItemNode ufi = (EGLUntypedFillerStructureItemNode) item;
//			nameNode = (Node) ufi.getChild(1); 
//		} else if (item.isFillerStructureItem()) {
//			EGLFillerStructureItemNode fi = (EGLFillerStructureItemNode) item;
//			nameNode = (Node) fi.getChild(1);
//		}
		
		if(item != null) {
			if(item.getName() != null ) {
				return new Region(item.getName().getOffset(), item.getName().getLength());
			} else {  // embed
				return new Region(item.getOffset(), item.getLength());
			}
		}
		else {
			return new Region(0, 0);
		}
	}

}
