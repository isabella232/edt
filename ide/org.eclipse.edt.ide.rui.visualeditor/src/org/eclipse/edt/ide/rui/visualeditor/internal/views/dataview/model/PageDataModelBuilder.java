/*******************************************************************************
 * Copyright © 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.views.dataview.model;

import java.util.List;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.DataItemBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.HandlerFieldsResolver;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;

public class PageDataModelBuilder {
	private static PageDataModelBuilder instance;
	
	public static PageDataModelBuilder getInstance(){
		if(instance == null){
			instance = new PageDataModelBuilder();
		}
		return instance;
	}
	
	private PageDataModelBuilder(){}
	
	public PageDataModel create(File file, EGLEditor editor){
		PageDataModel model = new PageDataModel();
		List<Part> parts = file.getParts();
		for(Part part : parts){
			if(part instanceof Handler){
				fillPageDataModel(editor, model);
			}
		}
		return model;
	}
	
	private void fillPageDataModel(EGLEditor editor, PageDataModel model){
		if(editor != null){
			IEditorInput editorInput = editor.getEditorInput();
			if(editorInput instanceof FileEditorInput){
				FileEditorInput fileEditorInput = (FileEditorInput)editorInput;
				
				//process handler
				HandlerFieldsResolver handlerFieldsResolver = new HandlerFieldsResolver(fileEditorInput.getFile());
				handlerFieldsResolver.resolve();
				Handler handler = handlerFieldsResolver.getRUIHandler();
				if(handler != null){
					HandlerPageDataNode handlerPageDataNode = (HandlerPageDataNode)PageDataNodeFactory.newPageDataNode(getName(handler), PageDataNodeFactory.HANDLER_PAGE_DATA_NODE);
					model.addRootPageDataNode(handlerPageDataNode);
					
					//process class fields 
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
									if(dataBinding != null){
										ITypeBinding typeBinding = dataBinding.getType();
										if(typeBinding != null && isAcceptType(typeBinding)){
											//Single
											if(typeBinding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING || typeBinding.getKind() == ITypeBinding.DATAITEM_BINDING || typeBinding.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING){	
												if(!isAnyType(typeBinding)){
													DataFieldPageDataNode dataFieldPageDataNode = null;
													if(classDataDeclaration.isPrivate()){
														dataFieldPageDataNode= (DataFieldPageDataNode)PageDataNodeFactory.newPageDataNode(getName(dataBinding), PageDataNodeFactory.DATA_FIELD_PAGE_DATA_NODE_PRIVATE);
													}else{
														dataFieldPageDataNode= (DataFieldPageDataNode)PageDataNodeFactory.newPageDataNode(getName(dataBinding), PageDataNodeFactory.DATA_FIELD_PAGE_DATA_NODE_PUBLIC);
													}	
													dataFieldPageDataNode.setDataBindingName(dataBinding.getCaseSensitiveName());
													handlerPageDataNode.addChild(dataFieldPageDataNode);
												}
											}
											//Array
											if(typeBinding.getKind() == ITypeBinding.ARRAY_TYPE_BINDING ){
												ArrayTypeBinding arrayTypeBinding = (ArrayTypeBinding)typeBinding;
												ITypeBinding elementTypeBinding = arrayTypeBinding.getElementType();
												if(elementTypeBinding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING || elementTypeBinding.getKind() == ITypeBinding.DATAITEM_BINDING || elementTypeBinding.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING){
													if(!isAnyType(elementTypeBinding)){
														DataFieldPageDataNode dataFieldPageDataNode = null;
														if(classDataDeclaration.isPrivate()){
															dataFieldPageDataNode= (DataFieldPageDataNode)PageDataNodeFactory.newPageDataNode(getName(dataBinding), PageDataNodeFactory.DATA_FIELD_PAGE_DATA_NODE_PRIVATE);
														}else{
															dataFieldPageDataNode= (DataFieldPageDataNode)PageDataNodeFactory.newPageDataNode(getName(dataBinding), PageDataNodeFactory.DATA_FIELD_PAGE_DATA_NODE_PUBLIC);
														}
														dataFieldPageDataNode.setDataBindingName(dataBinding.getCaseSensitiveName());
														handlerPageDataNode.addChild(dataFieldPageDataNode);
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}	
		}
	}
	
	private boolean isAcceptType(ITypeBinding typeBinding){
		String packageQualifiedName = typeBinding.getPackageQualifiedName();
		if(packageQualifiedName.equals("org.eclipse.edt.rui.mvc.FormField")){
			return false;
		}
		return true;
	}
	
	private boolean isAnyType(ITypeBinding typeBinding){
		if(typeBinding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING ){
			PrimitiveTypeBinding primitiveTypeBinding = (PrimitiveTypeBinding)typeBinding;
			if(primitiveTypeBinding.getPrimitive().getType() == Primitive.ANY_PRIMITIVE){
				return true;
			}
		}
		
		if(typeBinding.getKind() == ITypeBinding.DATAITEM_BINDING ){
			DataItemBinding dataItemBinding = (DataItemBinding)typeBinding;
			if(dataItemBinding.getPrimitiveTypeBinding().getPrimitive().getType() == Primitive.ANY_PRIMITIVE){
				return true;
			}
		}
		
		return false;
	}
	
	private String getName(Handler handler){
		StringBuffer sbName = new StringBuffer(handler.getName().getCanonicalName());
		Name handlerType = handler.getSubType();
		if (handlerType != null){
			sbName.append(" : "); //$NON-NLS-1$
			sbName.append(handlerType.getCanonicalString());
		}
		return sbName.toString();
	}
	
	private String getName(IDataBinding dataBinding){
		StringBuffer sbName = new StringBuffer(dataBinding.getCaseSensitiveName());
		sbName.append(" : ");//$NON-NLS-1$
		sbName.append(dataBinding.getType().getName());
		return sbName.toString();
	}
}
