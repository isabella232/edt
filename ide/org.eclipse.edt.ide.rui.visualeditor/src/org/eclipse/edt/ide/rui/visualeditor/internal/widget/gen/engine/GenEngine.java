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
package org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.engine;

import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetDescriptor;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.GenManager;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.GenModel;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.Templates;



public class GenEngine {
	private static GenEngine instance;
	private WidgetDescriptor widgetDescriptor;
	private GenEngine(){}
	
	public static GenEngine getInstance(){
		if(instance == null){
			instance = new GenEngine();
		}
		return instance;
	}
	
	public WidgetDescriptor gen(GenModel genModel){
		widgetDescriptor = genModel.getRoot().getInsertDataNode().getDataTemplate().getWidgetDescriptor();
		Templates templates = genModel.getTemplate();
		widgetDescriptor.setDataTemplate(templates.getWidgetTemplate());
		widgetDescriptor.setDataFunctionTemplate(templates.getFunctionTemplate());
		GenManager.getInstance().setGenRootWidgetName(genModel.getRoot().getInsertDataNode().getWidgetName());
		return widgetDescriptor;
	}
}
