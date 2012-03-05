/*******************************************************************************
 * Copyright 漏 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

package org.eclipse.edt.ide.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.ide.ui.editor.EGLCodeFormatterUtil;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

public class SimpleEGLFileOperation extends EGLFileOperation {

	private String fileContent;

	public SimpleEGLFileOperation(EGLFileConfiguration configuration) {
		super(configuration);
	}

	@Override
	protected String getFileContents() throws PartTemplateException {
		
		return fileContent;
	}

	public void setFileContent(String fileContent) {
		
		try {
			Document doc = new Document();
			doc.set(fileContent);
			TextEdit edit = EGLCodeFormatterUtil.format(doc, null);
			edit.apply(doc);
			fileContent = doc.get();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		this.fileContent = fileContent;
	}

	public void perform(IProgressMonitor monitor) throws CoreException,
			InvocationTargetException, InterruptedException {
		execute(monitor);
	}

}
