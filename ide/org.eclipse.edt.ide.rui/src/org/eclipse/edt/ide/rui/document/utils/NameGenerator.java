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
package org.eclipse.edt.ide.rui.document.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import org.eclipse.edt.ide.rui.internal.Activator;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IField;
import org.eclipse.edt.ide.core.model.IFunction;
import org.eclipse.edt.ide.core.model.IPart;

public class NameGenerator {

	private final IFile currentFile;
	
	public NameGenerator(IFile currentFile){
		this.currentFile = currentFile;
	}
	
	public String generateFieldName(String type) {
		String fieldName = null;
		try{
			IEGLFile modelFile = (IEGLFile)EGLCore.create(currentFile);
			IEGLFile sharedWorkingCopy = (IEGLFile)modelFile.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null);
			sharedWorkingCopy.reconcile(false, null);
		
			try{
				String partName = new Path(currentFile.getName()).removeFileExtension().toString();
				final IPart modelPart = sharedWorkingCopy.getPart(partName);
				fieldName = doGenerateFieldName(modelPart, type);
			}catch(Exception e){
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Field Name Generator: Error generating field name", e));
			}finally{
				sharedWorkingCopy.destroy();					
			}
		}catch(Exception e){
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Field Name Generator: Error creating working copy", e));
		}
		return fieldName;
	}
			
	private String doGenerateFieldName(IPart modelPart, String type) throws EGLModelException{
		int i = 0;
		String tempName = type;
		while(hasField(modelPart, tempName) || hasFunction(modelPart, tempName)){
			i++;
			tempName = type + i;
		}
		return tempName;
	}
	
	private boolean hasField(IPart part, String name) throws EGLModelException{
		boolean foundDuplicate = false;
		IField[] fields = part.getFields();
		
		for (int i = 0; i < fields.length; i++) {
			IField field = fields[i];
			if(field.getElementName().equalsIgnoreCase(name)){
				foundDuplicate = true;
				break;
			}
		}
		
		return foundDuplicate;
	}
	
	private boolean hasFunction(IPart part, String name) throws EGLModelException{
		boolean foundDuplicate = false;
		
		IFunction[] functions = part.getFunctions();
		
		for (int i = 0; i < functions.length; i++) {
			IFunction function = functions[i];
			if(function.getElementName().equalsIgnoreCase(name)){
				foundDuplicate = true;
				break;
			}
		}
		return foundDuplicate;
	}
}
