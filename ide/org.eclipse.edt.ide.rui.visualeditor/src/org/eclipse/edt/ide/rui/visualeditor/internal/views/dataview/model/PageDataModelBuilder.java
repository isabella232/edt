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
package org.eclipse.edt.ide.rui.visualeditor.internal.views.dataview.model;

import java.util.List;

import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.HandlerFieldsResolver;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.ParameterizedType;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;

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
									Member dataBinding = simpleName.resolveMember();
									Type typeBinding = simpleName.resolveType();
									if(dataBinding != null){
										if(typeBinding != null && isAcceptType(typeBinding)){
											if (typeBinding instanceof ParameterizedType) {
												typeBinding = ((ParameterizedType)typeBinding).getParameterizableType();
											}
											
											//Single
											if(TypeUtils.isPrimitive(typeBinding)
//													|| typeBinding.getKind() == ITypeBinding.DATAITEM_BINDING
													|| typeBinding instanceof Record){	
												if(!TypeUtils.Type_ANY.equals(typeBinding)){
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
											if(typeBinding instanceof ArrayType ){
												ArrayType arrayTypeBinding = (ArrayType)typeBinding;
												Type elementTypeBinding = arrayTypeBinding.getElementType();
												if(TypeUtils.isPrimitive(elementTypeBinding)
//														|| elementTypeBinding.getKind() == ITypeBinding.DATAITEM_BINDING
														|| elementTypeBinding instanceof Record){
													if(!TypeUtils.Type_ANY.equals(elementTypeBinding)){
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
	
	private boolean isAcceptType(Type typeBinding){
		String packageQualifiedName = typeBinding.getTypeSignature();
		if(packageQualifiedName.equals("org.eclipse.edt.rui.mvc.FormField")){
			return false;
		}
		return true;
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
	
	private String getName(Member dataBinding){
		StringBuffer sbName = new StringBuffer(dataBinding.getCaseSensitiveName());
		sbName.append(" : ");//$NON-NLS-1$
		sbName.append(BindingUtil.getShortTypeString(dataBinding.getType(), true));
		return sbName.toString();
	}
}
