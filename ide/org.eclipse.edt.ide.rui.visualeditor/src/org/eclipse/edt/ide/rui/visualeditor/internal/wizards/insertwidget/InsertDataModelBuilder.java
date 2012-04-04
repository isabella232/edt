/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.HandlerFieldsResolver;
import org.eclipse.edt.ide.rui.visualeditor.internal.views.dataview.model.DataFieldPageDataNode;
import org.eclipse.edt.ide.rui.visualeditor.internal.views.dataview.model.PageDataNode;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.handlers.BindingHandlerManager;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.InsertDataModel.Context;


public class InsertDataModelBuilder {
	private static InsertDataModelBuilder instance;
	private InsertDataModel insertDataModel;
	
	public static InsertDataModelBuilder getInstance(){
		if(instance == null){
			instance = new InsertDataModelBuilder();
		}
		return instance;
	}
	
	private InsertDataModelBuilder(){}
	
	public InsertDataModel create(PageDataNode node, IProject project, IEditorInput editorInput){
		insertDataModel = new InsertDataModel(project, editorInput);

		if(node instanceof DataFieldPageDataNode){
			handleDataFieldPageDataNode((DataFieldPageDataNode)node);
		}
		
		return insertDataModel;
	}
	
	private void handleDataFieldPageDataNode(DataFieldPageDataNode dataFieldPageDataNode){
		String bindingName = dataFieldPageDataNode.getDataBindingName();
		IDataBinding dataBinding = findDataBindingByName(bindingName);
		insertDataModel.getContext().set(Context.BINDING_NAME, bindingName);
		BindingHandlerManager.getInstance().handle(dataBinding, dataBinding.getType(), insertDataModel);
	}
	
	private IDataBinding findDataBindingByName(String bindingName){
		IEditorInput editorInput = insertDataModel.getEditorInput();
		if(editorInput instanceof FileEditorInput){
			FileEditorInput fileEditorInput = (FileEditorInput)editorInput;
			
			//process handler
			HandlerFieldsResolver handlerFieldsResolver = new HandlerFieldsResolver(fileEditorInput.getFile());
			handlerFieldsResolver.resolve();
			Handler handler = handlerFieldsResolver.getRUIHandler();
			if(handler != null){
				List contents = handler.getContents();
				for(int i = 0; i < contents.size(); i++){
					if(contents.get(i) instanceof ClassDataDeclaration){
						ClassDataDeclaration classDataDeclaration = (ClassDataDeclaration)contents.get(i);
						List names = classDataDeclaration.getNames();
						for(int j=0; j<names.size(); j++){
							Object oName = names.get(j);
							if(oName instanceof SimpleName){
								SimpleName simpleName = (SimpleName)oName;
								IDataBinding dataBinding = simpleName.resolveDataBinding();
								if(dataBinding != null && dataBinding.getCaseSensitiveName().equals(bindingName)){
									return dataBinding;
								}
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	public InsertDataModel getInsertDataModel(){
		return this.insertDataModel;
	}
}
