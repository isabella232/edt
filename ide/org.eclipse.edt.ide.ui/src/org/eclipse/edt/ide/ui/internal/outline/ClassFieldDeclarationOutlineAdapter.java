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

import java.util.Iterator;

import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.graphics.Image;

public class ClassFieldDeclarationOutlineAdapter extends AbstractOutlineAdapter {

	public ClassFieldDeclarationOutlineAdapter(EGLEditor editor) {
		super(editor);
		nodeIcon = PluginImages.DESC_OBJS_VARIABLEDECL;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		ClassDataDeclaration field = (ClassDataDeclaration) element;
		String expressionString = "";  //$NON-NLS-1$
		Expression expn = field.isConstant() ? field.getInitializer() : null;
		if (expn != null)
			expressionString = " - " + expn.getCanonicalString() ; //$NON-NLS-1$
		return getNameIterationText(field.getNames().iterator()) + " : " + formatType(field.getType()) + expressionString; //$NON-NLS-1$
	}

	public IRegion getHighlightRange(Object element) {
		ClassDataDeclaration node = (ClassDataDeclaration) element;
		Iterator iterator = node.getNames().iterator();
		Node nameNode = (Node)iterator.next();
		return new Region(nameNode.getOffset(), getNameIterationLength(iterator, nameNode.getOffset()));
	}
	
	public Image getImage(Object element) {
		ClassDataDeclaration field = (ClassDataDeclaration) element;
		if(field.isPrivate()){
			nodeIcon = PluginImages.DESC_OBJS_OBJS_ENV_VAR_PRIVATE;
		}else{
			nodeIcon = PluginImages.DESC_OBJS_VARIABLEDECL;
		}
		
		return(super.getImage(element));
	}

}
