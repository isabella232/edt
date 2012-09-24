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

import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.InsertDataNode;
import org.eclipse.edt.mof.egl.ParameterizedType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class BindingHandlerHelper {
	public static final String EGLUI = "egl.ui."; //$NON-NLS-1$
	
	public static final String ANNOTATION_DISPLAYNAME = "displayName";  //$NON-NLS-1$
	public static final String ANNOTATION_DISPLAYUSE = "displayUse";  //$NON-NLS-1$
	public static final String ANNOTATION_VALIDVALUES = "validValues";  //$NON-NLS-1$
	
	public static final String Widget_Combo = "Combo";  //$NON-NLS-1$
	public static final String Widget_DojoComboBox = "DojoComboBox";  //$NON-NLS-1$
	
	public static String getPrimitiveInsertDataNodeTypeDetail(Type primitiveTypeBinding){
		if (primitiveTypeBinding instanceof ParameterizedType) {
			primitiveTypeBinding = ((ParameterizedType)primitiveTypeBinding).getParameterizableType();
		}
		
		if (primitiveTypeBinding.equals(TypeUtils.Type_STRING)) {
			return InsertDataNode.NodeTypeDetail.TYPE_PRIMITIVE_STRING;
		}
		else if (primitiveTypeBinding.equals(TypeUtils.Type_DATE)) {
			return InsertDataNode.NodeTypeDetail.TYPE_PRIMITIVE_DATE;
		}
		else if (primitiveTypeBinding.equals(TypeUtils.Type_TIME)) {
			return InsertDataNode.NodeTypeDetail.TYPE_PRIMITIVE_TIME;
		}
		else if (primitiveTypeBinding.equals(TypeUtils.Type_TIMESTAMP)) {
			return InsertDataNode.NodeTypeDetail.TYPE_PRIMITIVE_TIMESTAMP;
		}
		else if (primitiveTypeBinding.equals(TypeUtils.Type_BIGINT)) {
			return InsertDataNode.NodeTypeDetail.TYPE_PRIMITIVE_BIGINT;
		}
		else if (primitiveTypeBinding.equals(TypeUtils.Type_DECIMAL)) {
			return InsertDataNode.NodeTypeDetail.TYPE_PRIMITIVE_DECIMAL;
		}
		else if (primitiveTypeBinding.equals(TypeUtils.Type_FLOAT)) {
			return InsertDataNode.NodeTypeDetail.TYPE_PRIMITIVE_FLOAT;
		}
		else if (primitiveTypeBinding.equals(TypeUtils.Type_INT)) {
			return InsertDataNode.NodeTypeDetail.TYPE_PRIMITIVE_INT;
		}
		else if (primitiveTypeBinding.equals(TypeUtils.Type_SMALLINT)) {
			return InsertDataNode.NodeTypeDetail.TYPE_PRIMITIVE_SMALLINT;
		}
		else if (primitiveTypeBinding.equals(TypeUtils.Type_SMALLFLOAT)) {
			return InsertDataNode.NodeTypeDetail.TYPE_PRIMITIVE_SMALLFLOAT;
		}
		else if (primitiveTypeBinding.equals(TypeUtils.Type_BOOLEAN)) {
			return InsertDataNode.NodeTypeDetail.TYPE_PRIMITIVE_BOOLEAN;
		}
		else if (primitiveTypeBinding.equals(TypeUtils.Type_BIN)) {
			return InsertDataNode.NodeTypeDetail.TYPE_PRIMITIVE_BIN;
		}
		else if (primitiveTypeBinding.equals(TypeUtils.Type_NUM)) {
			return InsertDataNode.NodeTypeDetail.TYPE_PRIMITIVE_NUM;
		}
		else if (primitiveTypeBinding.equals(TypeUtils.Type_MONEY)) {
			return InsertDataNode.NodeTypeDetail.TYPE_PRIMITIVE_MONEY;
		}
		
		return null;
	}
}
