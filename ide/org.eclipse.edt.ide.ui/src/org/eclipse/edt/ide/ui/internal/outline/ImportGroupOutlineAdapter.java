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

import java.util.HashMap;

import org.eclipse.edt.compiler.core.ast.ImportDeclaration;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.ide.ui.internal.EGLElementImageDescriptor;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.swt.graphics.Image;

public class ImportGroupOutlineAdapter extends AbstractOutlineAdapter {

	public ImportGroupOutlineAdapter(EGLEditor editor) {
		super(editor);
		nodeIcon = PluginImages.DESC_OBJS_IMPORTS;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		ImportGroup group = (ImportGroup) parentElement;
		return group.getFile().getImportDeclarations().toArray();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		return UINlsStrings.OutlineViewImportGroup;
	}

	// Return the appropriate icon for the node with an overlay, if necessary,
	// to indicate the highest severity of error or warning. 
	public Image getImage(Object element) {

		fImageFlags = 0;
		Image result = null;

		if (anyImportStatementshaveErrorsOrNestedErrors())
			fImageFlags = EGLElementImageDescriptor.ERROR;
		else if (anyImportStatementshaveWarningsOrNestedWarnings())
			fImageFlags = EGLElementImageDescriptor.WARNING;
		
		// Defensive programming -- if the node doesn't have an icon set, just return null
		if (nodeIcon != null)
			result = fImageLabelProvider.getImageLabel(nodeIcon, fImageFlags);

		return result;

	}

	private boolean anyImportStatementshaveErrorsOrNestedErrors() {
		boolean foundImportStatementInError = false;
		HashMap localCopy;
		
		localCopy = editor.getNodesWithSavedErrors();
		
		if (editor != null && localCopy != null) {
			Node[] keys = (Node[]) localCopy.keySet().toArray(new Node[localCopy.size()]);
			for (int i = 0; i < keys.length; i++) {
				Node thisNode = keys[i];
				if (thisNode instanceof ImportDeclaration)
					return true;
			}
		}

		return foundImportStatementInError;
	}

	private boolean anyImportStatementshaveWarningsOrNestedWarnings() {
			boolean foundImportStatementWithWarning = false;
			HashMap localCopy = editor.getNodesWithSavedWarnings();
			if (editor != null && localCopy != null) {
				Node[] keys = (Node[]) localCopy.keySet().toArray(new Node[localCopy.size()]);
				for (int i = 0; i < keys.length; i++) {
					Node thisNode = keys[i];
					if (thisNode instanceof ImportDeclaration)
						return true;
				}
			}

			return foundImportStatementWithWarning;
		}
}
