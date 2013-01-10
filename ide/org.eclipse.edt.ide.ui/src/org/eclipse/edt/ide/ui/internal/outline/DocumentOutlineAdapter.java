/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
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

import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;


public class DocumentOutlineAdapter extends AbstractOutlineAdapter {

	public DocumentOutlineAdapter(EGLEditor editor) {
		super(editor);
	}

	public Object[] getChildren(Object parentElement) {
		IEGLDocument doc = (IEGLDocument)parentElement;		
		File[] eglFile = {doc.getNewModelEGLFile()};
		return eglFile;
	}
}
