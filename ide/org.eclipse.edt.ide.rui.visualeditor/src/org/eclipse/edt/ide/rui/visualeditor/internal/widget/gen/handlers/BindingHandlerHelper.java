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

import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.InsertDataNode;

public class BindingHandlerHelper {
	public static final String[] EGLUI = new String[] {"egl", "ui"}; //$NON-NLS-1$
	public static final String[] EGLUIRUI = new String[] {"egl", "ui", "rui"}; //$NON-NLS-1$
	
	public static final String ANNOTATION_DISPLAYNAME = "displayName";  //$NON-NLS-1$
	public static final String ANNOTATION_DISPLAYUSE = "displayUse";  //$NON-NLS-1$
	public static final String ANNOTATION_VALIDVALUES = "validValues";  //$NON-NLS-1$
	
	public static final String Widget_Combo = "Combo";  //$NON-NLS-1$
	public static final String Widget_DojoComboBox = "DojoComboBox";  //$NON-NLS-1$
	
	public static String getPrimitiveInsertDataNodeTypeDetail(PrimitiveTypeBinding primitiveTypeBinding){
		switch(primitiveTypeBinding.getPrimitive().getType()){
		case Primitive.STRING_PRIMITIVE:
			return InsertDataNode.NodeTypeDetail.TYPE_PRIMITIVE_STRING;
		case Primitive.DATE_PRIMITIVE:
			return InsertDataNode.NodeTypeDetail.TYPE_PRIMITIVE_DATE;
		case Primitive.TIME_PRIMITIVE:
			return InsertDataNode.NodeTypeDetail.TYPE_PRIMITIVE_TIME;
		case Primitive.TIMESTAMP_PRIMITIVE:
			return InsertDataNode.NodeTypeDetail.TYPE_PRIMITIVE_TIMESTAMP;
		case Primitive.BIGINT_PRIMITIVE:
			return InsertDataNode.NodeTypeDetail.TYPE_PRIMITIVE_BIGINT;
		case Primitive.BIN_PRIMITIVE:
			return InsertDataNode.NodeTypeDetail.TYPE_PRIMITIVE_BIN;
		case Primitive.DECIMAL_PRIMITIVE:
			return InsertDataNode.NodeTypeDetail.TYPE_PRIMITIVE_DECIMAL;
		case Primitive.FLOAT_PRIMITIVE:
			return InsertDataNode.NodeTypeDetail.TYPE_PRIMITIVE_FLOAT;
		case Primitive.INT_PRIMITIVE:
			return InsertDataNode.NodeTypeDetail.TYPE_PRIMITIVE_INT;
		case Primitive.NUM_PRIMITIVE:
			return InsertDataNode.NodeTypeDetail.TYPE_PRIMITIVE_NUM;
		case Primitive.MONEY_PRIMITIVE:
			return InsertDataNode.NodeTypeDetail.TYPE_PRIMITIVE_MONEY;
		case Primitive.SMALLINT_PRIMITIVE:
			return InsertDataNode.NodeTypeDetail.TYPE_PRIMITIVE_SMALLINT;
		case Primitive.SMALLFLOAT_PRIMITIVE:
			return InsertDataNode.NodeTypeDetail.TYPE_PRIMITIVE_SMALLFLOAT;
		case Primitive.BOOLEAN_PRIMITIVE:
			return InsertDataNode.NodeTypeDetail.TYPE_PRIMITIVE_BOOLEAN;
		default:
			return null;
		}
	}
}
