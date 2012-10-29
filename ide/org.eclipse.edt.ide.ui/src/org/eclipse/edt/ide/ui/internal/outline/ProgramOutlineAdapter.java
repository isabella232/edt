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

import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.jface.text.IRegion;

public class ProgramOutlineAdapter extends AbstractOutlineAdapter {

	public ProgramOutlineAdapter(EGLEditor editor) {
		super(editor);
		nodeIcon = PluginImages.DESC_OBJS_PGM;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		Program program = (Program) parentElement;
		return filterOutProperties(program.getContents()).toArray();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		Program program = (Program) element;

		StringBuffer buffer = new StringBuffer();
		buffer.append(program.getName().getCanonicalName());

		String programType = "BasicProgram";
		if(program.hasSubType())
			programType = program.getSubType().getCanonicalString();
		
		buffer.append((programType == null) ? "" : " : " + programType); //$NON-NLS-1$ //$NON-NLS-2$
		return buffer.toString();
	}

	public IRegion getHighlightRange(Object element) {
		Program programNode = (Program) element;
		//Node nameNode = programNode.getSimpleNameNode();
		return getPartNameHighlightRange(programNode);
	}

}
