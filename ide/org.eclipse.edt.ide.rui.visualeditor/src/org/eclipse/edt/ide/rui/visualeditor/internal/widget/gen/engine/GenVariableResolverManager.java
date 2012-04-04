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
package org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.engine;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.GenNode;


public class GenVariableResolverManager {
	private static GenVariableResolverManager instance;
	private List<IGenVariableResolver> resolvers;
	private GenVariableResolverManager(){
		resolvers = new ArrayList<IGenVariableResolver>();
		resolvers.add(new RepeatResolver());
		resolvers.add(new WidgetNameResolver());
		resolvers.add(new FormManagerNameResolver());
//		resolvers.add(new WidgetTypeResolver());
		resolvers.add(new ControllerNameResolver());
		resolvers.add(new ValidStateSetterNameResolver());
		resolvers.add(new BindingNameResolver());
		resolvers.add(new ShortBindingNameResolver());
		resolvers.add(new LabelTextResolver());
		resolvers.add(new NameLabelNameResolver());
		resolvers.add(new ErrorLabelNameResolver());
		resolvers.add(new FormFieldNameResolver());
		resolvers.add(new ChildWidgetsResolver());
		resolvers.add(new FormFieldsResolver());
		resolvers.add(new RowsResolver());
		resolvers.add(new RowResolver());
		resolvers.add(new ColumnsResolver());
		resolvers.add(new ColumnResolver());
	};
	
	public static GenVariableResolverManager getInstance(){
		if(instance == null){
			instance = new GenVariableResolverManager();
		}
		return instance;
	}
	
	public void resolve(GenNode genNode){
		for(int i=0; i<resolvers.size(); i++){
			IGenVariableResolver resolver = resolvers.get(i);
			resolver.resolve(genNode);
		}
	}
	
	public void resolve(GenNode genNode, IGenVariableResolver resolver){
		resolver.resolve(genNode);
	}
}
