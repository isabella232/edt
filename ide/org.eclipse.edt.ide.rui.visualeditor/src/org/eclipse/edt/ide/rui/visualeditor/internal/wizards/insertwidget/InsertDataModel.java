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
package org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorInput;

public class InsertDataModel {
	private IProject project;
	private List<InsertDataNode> rootDataNodes;
	private Context context;
	private IEditorInput editorInput;
	private boolean isAddController;
	private boolean isAddPublishMessageHelper; 
	private boolean isAddFormattingAndValidation;
	private boolean isAddErrorMessage; 
	private String purpose;
	
	public InsertDataModel(IProject project, IEditorInput editorInput){
		this.project = project;
		this.isAddController = true;
		this.isAddFormattingAndValidation = true;
		this.isAddPublishMessageHelper = true;
		this.isAddErrorMessage = false;
		this.editorInput = editorInput;
		rootDataNodes = new ArrayList<InsertDataNode>();
		context = new Context();
	}
	
	public IProject getProject() {
		return project;
	}

	
	public IEditorInput getEditorInput() {
		return editorInput;
	}

	public List<InsertDataNode> getRootDataNodes(){
		return rootDataNodes;
	}
	
	public void addRootDataNode(InsertDataNode insertDataNode){
		rootDataNodes.add(insertDataNode);
	}
	
	public Context getContext(){
		return context;
	}
	
	public void updatePurpose(String purpose){
		for(InsertDataNode insertDataNode : rootDataNodes){
			updatePurpose(insertDataNode, purpose);
		}
	}
	
	private void updatePurpose(InsertDataNode parent, String purpose){
		parent.setPurpose(purpose);
		parent.update();
		List<InsertDataNode> children = parent.getChildren();
		for(InsertDataNode child : children){
			updatePurpose(child, purpose);
		}
	}
	
	public class Context{
		public static final String BINDING_NAME = "BINDING_NAME";
		public static final String PARENT_INSERT_DATA_NODE = "PARENT_INSERT_DATA_NODE";
		
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

	public boolean isAddFormattingAndValidation() {
		return isAddFormattingAndValidation;
	}

	public void setAddFormattingAndValidation(boolean isAddFormattingAndValidation) {
		this.isAddFormattingAndValidation = isAddFormattingAndValidation;
	}

	public boolean isAddErrorMessage() {
		return isAddErrorMessage;
	}

	public void setAddErrorMessage(boolean isAddErrorMessage) {
		this.isAddErrorMessage = isAddErrorMessage;
	}

	public boolean isAddController() {
		return isAddController;
	}

	public void setAddController(boolean isAddController) {
		this.isAddController = isAddController;
	}

	public boolean isAddPublishMessageHelper() {
		return isAddPublishMessageHelper;
	}

	public void setAddPublishMessageHelper(boolean isAddPublishMessageHelper) {
		this.isAddPublishMessageHelper = isAddPublishMessageHelper;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
}
