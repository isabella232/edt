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

import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

public class FileOutlineAdapter extends AbstractOutlineAdapter {
	
	private ImportGroup importGroup;
	
	public FileOutlineAdapter(IEGLDocument document, EGLEditor editor) {
		super(editor);
		this.importGroup = new ImportGroup(document);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		File file = (File) parentElement;
		
		ArrayList result = new ArrayList();
		
		if(file.hasPackageDeclaration()) {
			result.add(file.getPackageDeclaration());
		}
		
		// Add imports
		// Because of the artificial grouping, we need to add in the dummy node
		if(file.getImportDeclarations().size() > 0) {
			result.add(importGroup);
		}
		
//		long l = System.currentTimeMillis();
		result.addAll(file.getParts());
//		System.out.println("Getting all the parts took " + (System.currentTimeMillis()-l));
		
		return result.toArray();
	}
	
	public String getText (Object element){
		IEditorInput editorInput = editor.getEditorInput();
		if (editorInput instanceof IFileEditorInput)
			return ((IFileEditorInput)editorInput).getFile().getFullPath().toString();
		return ""; //$NON-NLS-1$
	}
	
}
