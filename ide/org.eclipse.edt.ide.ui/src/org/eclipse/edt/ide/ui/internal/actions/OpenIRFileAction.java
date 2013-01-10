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
package org.eclipse.edt.ide.ui.internal.actions;

import org.eclipse.edt.ide.core.internal.model.ClassFile;
import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.edt.ide.ui.internal.editor.BinaryFileEditor;
import org.eclipse.edt.ide.ui.internal.util.EditorUtility;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.actions.OpenFileAction;

public class OpenIRFileAction extends OpenFileAction {
	
	private IClassFile classFile;

	public OpenIRFileAction(IWorkbenchPage page) {
		super(page);
	}
	
	public void setClassFile(IClassFile classFile){
		this.classFile = classFile;
	}
	
	public void run() {
		//if the ir is from a Binary Project, and the BP is with source files in its source folder, then
		//opening the ir is to open the corresponding source file in the source folder
		if(classFile != null){
			if(classFile instanceof ClassFile){
				EditorUtility.openClassFile((ClassFile)classFile, BinaryFileEditor.BINARY_FILE_EDITOR_ID);
			}
		}		
	}

}
