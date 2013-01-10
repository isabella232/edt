/*******************************************************************************
 * Copyright © 2010, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.wizards.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.HandlerFieldsResolver;

public class NameFinder {
	private static NameFinder instance;
	private List<String> fieldNames;
	private List<String> functionNames;

	private NameFinder() {
		functionNames = new ArrayList<String>();
	}

	public static NameFinder getInstance() {
		if (instance == null) {
			instance = new NameFinder();
		}
		return instance;
	}
	
	public void initralize(IEditorInput editorInput){
		fieldNames = new ArrayList<String>();
		FileEditorInput fileEditorInput = (FileEditorInput) editorInput;
		HandlerFieldsResolver handlerFieldsResolver = new HandlerFieldsResolver(fileEditorInput.getFile());
		handlerFieldsResolver.resolve();
		Handler handler = handlerFieldsResolver.getRUIHandler();
		if(handler != null){
			List contents = handler.getContents();
			for (int i = 0; i < contents.size(); i++) {
				Object content = contents.get(i);
				if (content instanceof ClassDataDeclaration) {
					ClassDataDeclaration classDataDeclaration = (ClassDataDeclaration)content;
					List names = classDataDeclaration.getNames();
					for(int j=0; j<names.size(); j++){
						SimpleName simpleName = (SimpleName)names.get(j);
						fieldNames.add(simpleName.getCanonicalName());
					}
				}
				if (content instanceof NestedFunction) {
					NestedFunction function = (NestedFunction)content;
					functionNames.add(function.getName().getCanonicalName());
				}
			}
		}
	}

	public boolean isFieldNameExist(String fieldName) {
		for(String _FieldName : fieldNames){
			if(_FieldName.equalsIgnoreCase(fieldName)){
				return true;
			}
		}

		return false;
	}
	
	public boolean isFunctionNameExist(String functionName) {
		for(String _FunctionNames : functionNames){
			if(_FunctionNames.equalsIgnoreCase(functionName)){
				return true;
			}
		}

		return false;
	}
}
