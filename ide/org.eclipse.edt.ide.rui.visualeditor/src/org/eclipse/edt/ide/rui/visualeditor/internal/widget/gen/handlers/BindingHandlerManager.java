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

import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.InsertDataModel;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class BindingHandlerManager {
	private static BindingHandlerManager instance;
	private final FlexibleRecordBindingHandler recordHandler;
	private final PrimitiveTypeBindingHandler primitiveHandler;
//	private final DataItemBindingHandler dataItemHandler; //TODO data items not currently supported in EDT
	private final ArrayTypeBindingHandler arrayHandler;
	
	private BindingHandlerManager(){
		recordHandler = new FlexibleRecordBindingHandler();
		primitiveHandler = new PrimitiveTypeBindingHandler();
//		dataItemHandler = new DataItemBindingHandler();
		arrayHandler = new ArrayTypeBindingHandler();
	}
	
	public static BindingHandlerManager getInstance(){
		if(instance == null){
			instance = new BindingHandlerManager();
		}
		return instance;
	}
	
	public void handle(Member dataBinding, Type typeBinding, InsertDataModel insertDataModel){
		IDataTypeBindingHandler handler = null;
		if (typeBinding instanceof ArrayType) {
			handler = arrayHandler;
		}
		else if (typeBinding instanceof Record) {
			handler = recordHandler;
		}
		else if (TypeUtils.isPrimitive(typeBinding)) {
			handler = primitiveHandler;
		}
		
		if(handler != null){
			handler.handle(dataBinding, typeBinding, insertDataModel);
		}
	}
}
