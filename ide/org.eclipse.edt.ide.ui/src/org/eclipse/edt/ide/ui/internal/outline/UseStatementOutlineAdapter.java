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

import java.util.Iterator;

import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

public class UseStatementOutlineAdapter extends AbstractOutlineAdapter {

	public UseStatementOutlineAdapter(EGLEditor editor) {
		super(editor);
		nodeIcon = PluginImages.DESC_OBJS_USESTATEMENT;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		UseStatement field = (UseStatement) element;
		return "use -> " + getNameIterationText(field.getNames().iterator()); //$NON-NLS-1$
	}

	public IRegion getHighlightRange(Object element) {
		UseStatement node = (UseStatement) element;
		Iterator nameIterator = node.getNames().iterator();
		Name nameNode = (Name)(nameIterator.next());
		return new Region(nameNode.getOffset(), getNameIterationLength(nameIterator, nameNode.getOffset()));
	}
}
