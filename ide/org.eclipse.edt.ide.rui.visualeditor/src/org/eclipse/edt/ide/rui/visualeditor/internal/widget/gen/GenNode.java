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
package org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.InsertDataNode;





public class GenNode {
	private GenModel genModel;
	private InsertDataNode insertDataNode;
	private String template;
	private Context context;
	
	public GenNode(GenModel genModel, InsertDataNode insertDataNode){
		this.genModel = genModel;
		this.insertDataNode = insertDataNode;
		this.context = new Context();
	}
	
	public GenModel getGenModel(){
		return genModel;
	}
	
	public void setGenModel(GenModel genModel){
		this.genModel = genModel;
	}

	public InsertDataNode getInsertDataNode() {
		return insertDataNode;
	}

	public void setInsertDataNode(InsertDataNode insertDataNode) {
		this.insertDataNode = insertDataNode;
	}
	
	public String getTemplate(){
		if(template == null){
			template = TemplateComposer.getInstance().compose(this);
		}
		return template;
	}
	
	public void setTemplate(String template){
		this.template = template;
	}
	
	public Context getContext() {
		return context;
	}
	
	public class Context{
		public static final String NAME_LABEL_NAME = "NAME_LABEL_NAME";
		public static final String ERROR_LABEL_NAME = "ERROR_LABEL_NAME";
		public static final String FORM_FIELD_NAME = "FORM_FIELD_NAME";
		public static final String FORM_FIELD_MANAGER_NAME = "FORM_FIELD_MANAGER_NAME";
		public static final String CONTROLLER_NODE = "CONTROLLER_NODE";
		public static final String VALID_STATE_SETTER_NAME = "VALID_STATE_SETTER_NAME";
		
		private Map<String, Object> context;
		
		public Context(){
			context = new HashMap<String, Object>();
		}
		
		public void set(String key, Object value){
			context.put(key, value);
		}
		
		public Object get(String key){
			return context.get(key);
		}
	}
}
