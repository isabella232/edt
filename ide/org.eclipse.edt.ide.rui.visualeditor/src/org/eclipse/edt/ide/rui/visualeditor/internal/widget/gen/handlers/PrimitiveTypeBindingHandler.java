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

import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.InsertDataModel;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.InsertDataNode;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.InsertDataNodeFactory;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.InsertDataModel.Context;

public class PrimitiveTypeBindingHandler extends DataTypeBindingHandler {

	public void handle(IDataBinding dataBinding, ITypeBinding typeBinding, InsertDataModel insertDataModel) {
		//create self
		PrimitiveTypeBinding primitiveTypeBinding = (PrimitiveTypeBinding)typeBinding;
		String bindingName = (String)insertDataModel.getContext().get(Context.BINDING_NAME);
		InsertDataNode insertDataNode = InsertDataNodeFactory.newInsertDataNode(insertDataModel, bindingName, dataBinding.getType().getName());
		insertDataNode.setArray(false);
		insertDataNode.setContainer(false);
		insertDataNode.setNodeType(InsertDataNode.NodeType.TYPE_PRIMITIVE_ALL);
		insertDataNode.addNodeTypeDetail(BindingHandlerHelper.getPrimitiveInsertDataNodeTypeDetail(primitiveTypeBinding));
		//set annotation
		IAnnotationBinding displayNameAnnotationBinding = dataBinding.getAnnotation(BindingHandlerHelper.EGLUI, BindingHandlerHelper.ANNOTATION_DISPLAYNAME);
		if(displayNameAnnotationBinding != null){
			insertDataNode.setLabelText((String)displayNameAnnotationBinding.getValue());
		}
		
		//add to model tree
		Object parent = insertDataModel.getContext().get(Context.PARENT_INSERT_DATA_NODE);
		//a simple primitive type field
		if(parent == null){
			//create a layout container for the simple primitive type field
			InsertDataNode parentInsertDataNode = InsertDataNodeFactory.newInsertDataNode(insertDataModel, bindingName, dataBinding.getType().getName());
			parentInsertDataNode.setArray(false);
			parentInsertDataNode.setContainer(true);
			parentInsertDataNode.setNodeType(InsertDataNode.NodeType.TYPE_PRIMITIVE_ALL);
			parentInsertDataNode.addNodeTypeDetail(BindingHandlerHelper.getPrimitiveInsertDataNodeTypeDetail(primitiveTypeBinding));
			parentInsertDataNode.addChild(insertDataNode);	
			insertDataModel.addRootDataNode(parentInsertDataNode);
		}
		else{
			InsertDataNode parentInsertDataNode = (InsertDataNode)parent;
			//parent is a record or record array
			if(parentInsertDataNode.getNodeType().equals(InsertDataNode.NodeType.TYPE_RECORD_ALL)){
				if(!(primitiveTypeBinding.getPrimitive().getType() == Primitive.ANY_PRIMITIVE)){
					parentInsertDataNode.addChild(insertDataNode);
				}
			}
			//parent is a primitive array, do nothing
		}
	}
}
