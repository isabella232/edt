/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.text.edits.MalformedTreeException;

import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.rui.internal.Activator;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IFunction;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.Signature;

/**
 * Insert a new widget into the RUIHandler
 */
public class GetEventHandlerFunctionsOperation {

	private IEGLDocument currentDocument;
	private IFile currentFile;
	private List functionNames = new ArrayList();
	
	public GetEventHandlerFunctionsOperation(IEGLDocument currentDocument, IFile currentFile){
		this.currentDocument = currentDocument;		
		this.currentFile = currentFile;
	}
	
	public String[] getFunctions(){
		try{
			IEGLFile modelFile = (IEGLFile)EGLCore.create(currentFile);
			IEGLFile sharedWorkingCopy = (IEGLFile)modelFile.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null);
			sharedWorkingCopy.reconcile(false, null);
		
			try{
				final org.eclipse.edt.compiler.core.ast.File fileAST = currentDocument.getNewModelEGLFile();
				String partName = new Path(currentFile.getName()).removeFileExtension().toString();
				final IPart modelPart = sharedWorkingCopy.getPart(partName);
				functionNames = findFunctions(modelPart, fileAST);
			}catch(Exception e){
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Get Event Handler: Error finding functions", e));
			}finally{
				sharedWorkingCopy.destroy();					
			}
		}catch(Exception e){
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Get Event Handler: Error creating working copy", e));
		}
		
		return (String[])functionNames.toArray(new String[functionNames.size()]);
	}

	public int getFunctionFirstLineOffset( String functionName ) {
		try {
			IFunction function = findFunction( functionName );
			//Assuming that first line of the function is the next line to function name. 
			int line = currentDocument.getLineOfOffset( function.getNameRange().getOffset() ) + 1;
			IRegion region = currentDocument.getLineInformation( line );
			int offset = region.getOffset() + region.getLength();
			return offset;
		} catch(Exception e) {
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Get Event Handler: Error finding functions", e));
		}
		return 0;
	}
	
	public int[] getFunctionNameRange( String functionName ) {
		int[] range = new int[2];
		try {
			IFunction function = findFunction( functionName );
			range[0] = function.getNameRange().getOffset();
			//One more char is selected, so subtract 1,
			range[1] = function.getNameRange().getLength() - 1;
			return range;
		} catch(Exception e) {
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Get Event Handler: Error finding functions", e));
		}
		return range;
	}
	
	private IFunction findFunction( String functionName ) {
		
		try{
			IEGLFile modelFile = (IEGLFile)EGLCore.create(currentFile);
			IEGLFile sharedWorkingCopy = (IEGLFile)modelFile.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null);
			sharedWorkingCopy.reconcile(false, null);
		
			try{
				final org.eclipse.edt.compiler.core.ast.File fileAST = currentDocument.getNewModelEGLFile();
				String partName = new Path(currentFile.getName()).removeFileExtension().toString();
				final IPart modelPart = sharedWorkingCopy.getPart(partName);
				IFunction[] functions = modelPart.getFunctions();
				for (int i = 0; i < functions.length; i++) {
					IFunction function = functions[i];
					if(function.getParameterTypes().length == 1 && 
						"Event".equalsIgnoreCase(Signature.toString(function.getParameterTypes()[0])) &&
						functionName.equalsIgnoreCase( function.getElementName() ) ) {
						return function;
					}
				}
			}catch(Exception e){
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Get Event Handler: Error finding functions", e));
			}finally{
				sharedWorkingCopy.destroy();					
			}
		}catch(Exception e){
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Get Event Handler: Error creating working copy", e));
		}
		
		return null;
	}
	
	private List findFunctions(IPart modelPart, org.eclipse.edt.compiler.core.ast.File fileAST) throws EGLModelException, MalformedTreeException, BadLocationException{
		List result = new ArrayList();
		IFunction[] functions = modelPart.getFunctions();
		
		for (int i = 0; i < functions.length; i++) {
			IFunction function = functions[i];
			if(function.getParameterTypes().length == 1 && "Event".equalsIgnoreCase(Signature.toString(function.getParameterTypes()[0]))){
				result.add(function.getElementName());
			}
		}
		
		return result;
	}
}
