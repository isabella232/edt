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
package org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.handlers;

import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.DataItemBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.InsertDataModel;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.InsertDataNode;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.InsertDataNodeFactory;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.InsertDataModel.Context;

public class ArrayTypeBindingHandler extends DataTypeBindingHandler {

	public void handle(IDataBinding dataBinding, ITypeBinding typeBinding, InsertDataModel insertDataModel) {
		//create self
		ArrayTypeBinding arrayTypeBinding = (ArrayTypeBinding)typeBinding;
		String bindingName = (String)insertDataModel.getContext().get(Context.BINDING_NAME);	
		InsertDataNode insertDataNode = InsertDataNodeFactory.newInsertDataNode(insertDataModel, bindingName, dataBinding.getType().getName());
		insertDataNode.setArray(true);
		insertDataNode.setContainer(false);
		int elementTypeKind = arrayTypeBinding.getElementType().getKind();
		// primitive type array
		if(elementTypeKind == ITypeBinding.PRIMITIVE_TYPE_BINDING){
			PrimitiveTypeBinding primitiveTypeBinding = (PrimitiveTypeBinding)arrayTypeBinding.getElementType();
			insertDataNode.setNodeType(InsertDataNode.NodeType.TYPE_PRIMITIVE_ALL);
			insertDataNode.addNodeTypeDetail(BindingHandlerHelper.getPrimitiveInsertDataNodeTypeDetail(primitiveTypeBinding));
		}
		// dataitem array
		if(elementTypeKind == ITypeBinding.DATAITEM_BINDING){
			DataItemBinding dataItemBinding = (DataItemBinding)arrayTypeBinding.getElementType();
			PrimitiveTypeBinding primitiveTypeBinding = dataItemBinding.getPrimitiveTypeBinding();
			insertDataNode.setNodeType(InsertDataNode.NodeType.TYPE_PRIMITIVE_ALL);
			insertDataNode.addNodeTypeDetail(BindingHandlerHelper.getPrimitiveInsertDataNodeTypeDetail(primitiveTypeBinding));
		}
		// record array
		if(elementTypeKind == ITypeBinding.FLEXIBLE_RECORD_BINDING){
			insertDataNode.setNodeType(InsertDataNode.NodeType.TYPE_RECORD_ALL);
		}
		//set annotation
		IAnnotationBinding displayNameAnnotationBinding = dataBinding.getAnnotation(BindingHandlerHelper.EGLUI, BindingHandlerHelper.ANNOTATION_DISPLAYNAME);
		if(displayNameAnnotationBinding != null){
			insertDataNode.setLabelText((String)displayNameAnnotationBinding.getValue());
		}
//		IAnnotationBinding displayUseAnnotationBinding = dataBinding.getAnnotation(BindingHandlerHelper.EGLUIRUI, BindingHandlerHelper.ANNOTATION_DISPLAYUSE);
//		if(displayUseAnnotationBinding != null){
//			insertDataNode.setDefaultWidgetType((String)displayUseAnnotationBinding.getValue());
//		}
		
		
		//add to model tree
		Object parent = insertDataModel.getContext().get(Context.PARENT_INSERT_DATA_NODE);
		//simple array
		if(parent == null){
			insertDataModel.addRootDataNode(insertDataNode);
		}
		//array embed in record
		else{
			InsertDataNode parentInsertDataNode = (InsertDataNode)parent;
			if(!parentInsertDataNode.getBindingName().equals(insertDataNode.getBindingName())){
//				if(!insertDataNode.getNodeType().equals(InsertDataNode.NodeType.TYPE_PRIMITIVE_ALL)){
					parentInsertDataNode.addChild(insertDataNode);
//				}
				if(parentInsertDataNode.getNodeType().equals(InsertDataNode.NodeType.TYPE_RECORD_ALL)){
					parentInsertDataNode.removeNodeTypeDetail(InsertDataNode.NodeTypeDetail.TYPE_RECORD_SIMPLE);
					if(insertDataNode.getNodeType().equals(InsertDataNode.NodeType.TYPE_PRIMITIVE_ALL)){
						parentInsertDataNode.addNodeTypeDetail(InsertDataNode.NodeTypeDetail.TYPE_RECORD_WITH_PRIMITIVE_ARRAY);
					}
					if(insertDataNode.getNodeType().equals(InsertDataNode.NodeType.TYPE_RECORD_ALL)){
						parentInsertDataNode.addNodeTypeDetail(InsertDataNode.NodeTypeDetail.TYPE_RECORD_WITH_RECORD_ARRAY);
					}
				}
			}
		}
		insertDataModel.getContext().set(Context.PARENT_INSERT_DATA_NODE, insertDataNode);
		
		
		
		//handle others
		BindingHandlerManager.getInstance().handle(dataBinding, arrayTypeBinding.getElementType(), insertDataModel);	
	}

}
