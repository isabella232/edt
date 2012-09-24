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

import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.InsertDataModel;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.InsertDataModel.Context;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.InsertDataNode;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.InsertDataNodeFactory;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class ArrayTypeBindingHandler extends DataTypeBindingHandler {
	
	public void handle(Member dataBinding, Type typeBinding, InsertDataModel insertDataModel) {
		//create self
		ArrayType arrayTypeBinding = (ArrayType)typeBinding;
		String bindingName = (String)insertDataModel.getContext().get(Context.BINDING_NAME);	
		InsertDataNode insertDataNode = InsertDataNodeFactory.newInsertDataNode(insertDataModel, bindingName, BindingUtil.getShortTypeString(dataBinding.getType(), true));
		insertDataNode.setArray(true);
		insertDataNode.setContainer(false);
		// primitive type array
		if(TypeUtils.isPrimitive(arrayTypeBinding.getElementType())){
			insertDataNode.setNodeType(InsertDataNode.NodeType.TYPE_PRIMITIVE_ALL);
			insertDataNode.addNodeTypeDetail(BindingHandlerHelper.getPrimitiveInsertDataNodeTypeDetail(arrayTypeBinding.getElementType()));
		}
		//TODO data item not currently supported
//		// dataitem array
//		if(elementTypeKind == ITypeBinding.DATAITEM_BINDING){
//			DataItemBinding dataItemBinding = (DataItemBinding)arrayTypeBinding.getElementType();
//			PrimitiveTypeBinding primitiveTypeBinding = dataItemBinding.getPrimitiveTypeBinding();
//			insertDataNode.setNodeType(InsertDataNode.NodeType.TYPE_PRIMITIVE_ALL);
//			insertDataNode.addNodeTypeDetail(BindingHandlerHelper.getPrimitiveInsertDataNodeTypeDetail(primitiveTypeBinding));
//		}
		// record array
		if(arrayTypeBinding.getElementType() instanceof Record){
			insertDataNode.setNodeType(InsertDataNode.NodeType.TYPE_RECORD_ALL);
		}
		//set annotation
		Annotation displayNameAnnotationBinding = dataBinding.getAnnotation(BindingHandlerHelper.EGLUI + BindingHandlerHelper.ANNOTATION_DISPLAYNAME);
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
