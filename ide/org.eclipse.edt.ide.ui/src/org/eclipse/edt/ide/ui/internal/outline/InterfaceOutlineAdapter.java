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

import java.util.ArrayList;

import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.jface.text.IRegion;

public class InterfaceOutlineAdapter extends AbstractOutlineAdapter {

	public InterfaceOutlineAdapter(EGLEditor editor) {
		super(editor);
		//TODO - need interface icon here
		nodeIcon = PluginImages.DESC_OBJS_INTERFACE;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		Interface interfacex = (Interface) parentElement;
		final ArrayList children = new ArrayList();
		interfacex.accept(new DefaultASTVisitor(){
			public boolean visit(NestedFunction nestedFunction) {
				children.add(nestedFunction);
				return false;
			};
		});
		
		return children.toArray();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		Interface interfacex = (Interface) element;

		StringBuffer buffer = new StringBuffer();
		buffer.append(interfacex.getName().getCanonicalString());

		Name type = interfacex.getSubType();
		//EGLInterfaceType type = interfacex.getInterfaceType();
		buffer.append((type == null) ? "" : " : " + type.getCanonicalString()); //$NON-NLS-1$ //$NON-NLS-2$
		return buffer.toString();
	}

	public IRegion getHighlightRange(Object element) {
		Interface interfaceNode = (Interface) element;
		//Node nameNode = interfaceNode.getSimpleNameNode();
		return getPartNameHighlightRange(interfaceNode);
	}

}
