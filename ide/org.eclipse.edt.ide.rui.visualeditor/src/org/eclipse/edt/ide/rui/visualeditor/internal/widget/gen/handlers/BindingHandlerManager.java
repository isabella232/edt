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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.InsertDataModel;

public class BindingHandlerManager {
	private static BindingHandlerManager instance;
	private Map<String, IDataTypeBindingHandler> bindingHandlerRegister;
	
	private BindingHandlerManager(){
		bindingHandlerRegister = new HashMap<String, IDataTypeBindingHandler>();
		bindingHandlerRegister.put(parseIntToString(ITypeBinding.FLEXIBLE_RECORD_BINDING), new FlexibleRecordBindingHandler());
		bindingHandlerRegister.put(parseIntToString(ITypeBinding.PRIMITIVE_TYPE_BINDING), new PrimitiveTypeBindingHandler());
		bindingHandlerRegister.put(parseIntToString(ITypeBinding.DATAITEM_BINDING), new DataItemBindingHandler());
		bindingHandlerRegister.put(parseIntToString(ITypeBinding.ARRAY_TYPE_BINDING), new ArrayTypeBindingHandler());
	}
	
	public static BindingHandlerManager getInstance(){
		if(instance == null){
			instance = new BindingHandlerManager();
		}
		return instance;
	}
	
	public void handle(IDataBinding dataBinding, ITypeBinding typeBinding, InsertDataModel insertDataModel){
		IDataTypeBindingHandler handler = bindingHandlerRegister.get(parseIntToString(typeBinding.getKind()));
		if(handler != null){
			handler.handle(dataBinding, typeBinding, insertDataModel);
		}
	}
	
	private String parseIntToString(int i){
		return Integer.toString(i);
	}
}
