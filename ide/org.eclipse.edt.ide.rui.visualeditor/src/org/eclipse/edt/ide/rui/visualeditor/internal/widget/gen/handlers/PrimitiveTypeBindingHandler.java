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
package org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.handlers;

import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.InsertDataModel;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.InsertDataModel.Context;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.InsertDataNode;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.InsertDataNodeFactory;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class PrimitiveTypeBindingHandler extends DataTypeBindingHandler {

	public void handle(Member dataBinding, Type typeBinding, InsertDataModel insertDataModel) {
		//create self
		String bindingName = (String)insertDataModel.getContext().get(Context.BINDING_NAME);
		InsertDataNode insertDataNode = InsertDataNodeFactory.newInsertDataNode(insertDataModel, bindingName, BindingUtil.getShortTypeString(dataBinding.getType(), true));
		insertDataNode.setArray(false);
		insertDataNode.setContainer(false);
		insertDataNode.setNodeType(InsertDataNode.NodeType.TYPE_PRIMITIVE_ALL);
		insertDataNode.addNodeTypeDetail(BindingHandlerHelper.getPrimitiveInsertDataNodeTypeDetail(typeBinding));
		//set annotation
		Annotation displayNameAnnotationBinding = dataBinding.getAnnotation(BindingHandlerHelper.EGLUI + BindingHandlerHelper.ANNOTATION_DISPLAYNAME);
		if(displayNameAnnotationBinding != null){
			insertDataNode.setLabelText((String)displayNameAnnotationBinding.getValue());
		}
		
		//add to model tree
		Object parent = insertDataModel.getContext().get(Context.PARENT_INSERT_DATA_NODE);
		//a simple primitive type field
		if(parent == null){
			//create a layout container for the simple primitive type field
			InsertDataNode parentInsertDataNode = InsertDataNodeFactory.newInsertDataNode(insertDataModel, bindingName, BindingUtil.getShortTypeString(dataBinding.getType(), true));
			parentInsertDataNode.setArray(false);
			parentInsertDataNode.setContainer(true);
			parentInsertDataNode.setNodeType(InsertDataNode.NodeType.TYPE_PRIMITIVE_ALL);
			parentInsertDataNode.addNodeTypeDetail(BindingHandlerHelper.getPrimitiveInsertDataNodeTypeDetail(typeBinding));
			parentInsertDataNode.addChild(insertDataNode);	
			insertDataModel.addRootDataNode(parentInsertDataNode);
		}
		else{
			InsertDataNode parentInsertDataNode = (InsertDataNode)parent;
			//parent is a record or record array
			if(parentInsertDataNode.getNodeType().equals(InsertDataNode.NodeType.TYPE_RECORD_ALL)){
				if(!TypeUtils.Type_ANY.equals(typeBinding)){
					parentInsertDataNode.addChild(insertDataNode);
				}
			}
			//parent is a primitive array, do nothing
		}
	}
}
