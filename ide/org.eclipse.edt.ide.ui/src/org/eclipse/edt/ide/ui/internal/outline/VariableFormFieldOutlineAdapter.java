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

import org.eclipse.edt.compiler.core.ast.VariableFormField;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

public class VariableFormFieldOutlineAdapter extends AbstractOutlineAdapter {

	public VariableFormFieldOutlineAdapter(EGLEditor editor) {
		super(editor);
		nodeIcon = PluginImages.DESC_OBJS_VARIABLEFORMFIELD;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		VariableFormField field = (VariableFormField)element;		
		String s =  field.getName().getCanonicalName();
//JingNewModel - need bound ast here
//		
//		if (field.hasOccurs())
//			s += ("[" + field.getOccurs() + "] : "); //$NON-NLS-1$ //$NON-NLS-2$
//		else
			s += " : "; //$NON-NLS-1$
		s += formatType(field.getType());
		return s; 
	}
	
	public IRegion getHighlightRange(Object element) {
		VariableFormField node = (VariableFormField) element;
		//Node nameNode = node.getSimpleNameNode();
		return new Region(node.getName().getOffset(), node.getName().getLength());
	}
}
