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

import org.eclipse.edt.compiler.core.ast.PackageDeclaration;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;


public class PackageDeclarationOutlineAdapter extends AbstractOutlineAdapter {

	public PackageDeclarationOutlineAdapter(EGLEditor editor) {
		super(editor);
		nodeIcon = PluginImages.DESC_OBJS_PACKAGE;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		PackageDeclaration packageDeclaration = (PackageDeclaration) element;
		return packageDeclaration.getName().getCanonicalString();
	}

	public IRegion getHighlightRange(Object element) {
		PackageDeclaration node = (PackageDeclaration) element;
		//Node nameNode = node.getNameNode();
		return new Region(node.getName().getOffset(), node.getName().getLength());
	}
}
