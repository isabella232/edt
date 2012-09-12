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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.ide.rui.visualeditor.internal.widget.DataTemplate;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetDescriptorRegistry;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.util.NameFinder;
import org.eclipse.edt.ide.rui.visualeditor.plugin.Activator;


public class InsertDataNode {
	private InsertDataNode parent; 
	private List<InsertDataNode> children;
	private InsertDataModel model;
	
	//for widget template mapping
	private String nodeType;
	private Set<String> nodeTypeDetails;
	private boolean isArray;
	private boolean isContainer;
	private String purpose;

	//for generation
	private String bindingName;
	private boolean isGen;
	private List<DataTemplate> dataTemplates;
	private List<DataTemplate> forDisplayDataTemplates;
	private List<DataTemplate> forCreateDataTemplates;
	private List<DataTemplate> forUpdateDataTemplates;
	
	//for display in the wizard
	private String fieldName;
	private String fieldType;
	private String labelText;
	private WidgetType widgetType;
	private WidgetType[] widgetTypes;
	
	private WidgetType[] forDisplayWidgetTypes;
	private WidgetType[] forCreateWidgetTypes;
	private WidgetType[] forUpdateWidgetTypes;
	
	private String widgetName;
	
	public InsertDataNode(InsertDataModel model, String bindingName, String fieldType){
		this.model = model;
		this.children = new ArrayList<InsertDataNode>();
		this.bindingName = bindingName;
		this.fieldType = fieldType;
		this.nodeTypeDetails = new HashSet<String>();
		this.purpose = Purpose.FOR_DISPLAY;
		createFieldName();
	}
	
	private void createFieldName(){
		if(bindingName.contains(".")){
			fieldName = bindingName.substring(bindingName.lastIndexOf(".")+1, bindingName.length());
		}else{
			fieldName = bindingName;
		}
	}
	public InsertDataModel getModel() {
		return model;
	}
	public String getBindingName() {
		return bindingName;
	}
	public void setBindingName(String bindingName) {
		this.bindingName = bindingName;
	}
	public InsertDataNode getParent() {
		return parent;
	}
	public void setParent(InsertDataNode parent) {
		this.parent = parent;
	}
	public List<InsertDataNode> getChildren() {
		return children;
	}
	public void addChild(InsertDataNode child){
		this.children.add(child);
		child.setParent(this);
	}
	public boolean hasChildren(){
		return !children.isEmpty();
	}
	public void setChildren(List<InsertDataNode> children){
		this.children = children;
	}
	public String getNodeType() {
		return nodeType;
	}
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	public boolean isGen() {
		return isGen;
	}
	public void setGen(boolean isGen) {
		this.isGen = isGen;
	}
	public Set<String> getNodeTypeDetails() {
		return nodeTypeDetails;
	}
	public void addNodeTypeDetail(String nodeTypeDetail) {
		if(nodeTypeDetail != null){
			nodeTypeDetails.add(nodeTypeDetail);
		}
	}
	public void removeNodeTypeDetail(String nodeTypeDetail){
		nodeTypeDetails.remove(nodeTypeDetail);
	}
	public boolean isArray() {
		return isArray;
	}
	public void setArray(boolean isArray) {
		this.isArray = isArray;
	}
	public boolean isContainer() {
		return isContainer;
	}
	public void setContainer(boolean isContainer) {
		this.isContainer = isContainer;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public String getLabelText() {
		if(labelText == null){
			int lastIndex = bindingName.lastIndexOf(".");
			if(lastIndex == -1){
				labelText = bindingName;
			}else{
				labelText = bindingName.substring(lastIndex+1);
			}
		}
		return labelText;
	}
	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public void update(){
		widgetTypes = null;
		widgetType = null;		
	}
	
	
	public WidgetType[] getWidgetTypes(){
		boolean isTimeStamp = fieldType.equalsIgnoreCase("timestamp(hhmmss)") || fieldType.equalsIgnoreCase("timestamp(yyyyMMddhhmmss)") || fieldType.equalsIgnoreCase("timestamp");		
		if(purpose.equals(InsertDataNode.Purpose.FOR_DISPLAY)){
			if(forDisplayWidgetTypes == null){
				forDisplayDataTemplates = WidgetDescriptorRegistry.getInstance(model.getProject()).getMappingDescriptorDataTemplates(purpose, nodeType, nodeTypeDetails, isArray, isContainer);
				if(isTimeStamp){
					//Copy the DataTemplate of TYPE_PRIMITIVE_TIME, and add to the DataTemplate list. 
					Set<String> timeNodeTypeDetails = new HashSet<String>();
					timeNodeTypeDetails.add(NodeTypeDetail.TYPE_PRIMITIVE_TIME);
					List<DataTemplate> dataTemplatesTime = WidgetDescriptorRegistry.getInstance(model.getProject()).getMappingDescriptorDataTemplates(purpose, null, timeNodeTypeDetails, isArray, isContainer);
					for(DataTemplate dataTemplate : dataTemplatesTime){
						if(dataTemplate.getName().equals("DojoTimeTextBox (read only)")){
							forDisplayDataTemplates.add(dataTemplate);
						}
					}
				}				
				forDisplayWidgetTypes = new WidgetType[forDisplayDataTemplates.size()];
				for(int i=0; i<forDisplayWidgetTypes.length; i++){
					DataTemplate dataTemplate = forDisplayDataTemplates.get(i);
					WidgetType widgetType = new WidgetType(dataTemplate.getName(), dataTemplate.getWidgetDescriptor().getType());
					forDisplayWidgetTypes[i] = widgetType;
				}
			}
			dataTemplates = forDisplayDataTemplates;
			widgetTypes = forDisplayWidgetTypes;
		}		
			
		if(purpose.equals(InsertDataNode.Purpose.FOR_CREATE)){
			if(forCreateWidgetTypes == null){
				forCreateDataTemplates = WidgetDescriptorRegistry.getInstance(model.getProject()).getMappingDescriptorDataTemplates(purpose, nodeType, nodeTypeDetails, isArray, isContainer);
				if(isTimeStamp){
					//Copy the DataTemplate of TYPE_PRIMITIVE_TIME, and add to the DataTemplate list. 
					Set<String> timeNodeTypeDetails = new HashSet<String>();
					timeNodeTypeDetails.add(NodeTypeDetail.TYPE_PRIMITIVE_TIME);
					List<DataTemplate> dataTemplatesTime = WidgetDescriptorRegistry.getInstance(model.getProject()).getMappingDescriptorDataTemplates(purpose, null, timeNodeTypeDetails, isArray, isContainer);
					for(DataTemplate dataTemplate : dataTemplatesTime){
						if(dataTemplate.getName().equals("DojoTimeTextBox")){
							forCreateDataTemplates.add(dataTemplate);
						}
					}
				}
				forCreateWidgetTypes = new WidgetType[forCreateDataTemplates.size()];
				for(int i=0; i<forCreateWidgetTypes.length; i++){
					DataTemplate dataTemplate = forCreateDataTemplates.get(i);
					WidgetType widgetType = new WidgetType(dataTemplate.getName(), dataTemplate.getWidgetDescriptor().getType());
					forCreateWidgetTypes[i] = widgetType;
				}
			}
			dataTemplates = forCreateDataTemplates;
			widgetTypes = forCreateWidgetTypes;
		}
		
		if(purpose.equals(InsertDataNode.Purpose.FOR_UPDATE)){
			if(forUpdateWidgetTypes == null){
				forUpdateDataTemplates = WidgetDescriptorRegistry.getInstance(model.getProject()).getMappingDescriptorDataTemplates(purpose, nodeType, nodeTypeDetails, isArray, isContainer);
				if(isTimeStamp){
					Set<String> timeNodeTypeDetails = new HashSet<String>();
					timeNodeTypeDetails.add(NodeTypeDetail.TYPE_PRIMITIVE_TIME);
					List<DataTemplate> dataTemplatesTime = WidgetDescriptorRegistry.getInstance(model.getProject()).getMappingDescriptorDataTemplates(purpose, null, timeNodeTypeDetails, isArray, isContainer);
					for(DataTemplate dataTemplate : dataTemplatesTime){
						if(dataTemplate.getName().equals("DojoTimeTextBox") || dataTemplate.getName().equals("DojoTimeTextBox (read only)")){
							forUpdateDataTemplates.add(dataTemplate);
						}
					}
				}
				forUpdateWidgetTypes = new WidgetType[forUpdateDataTemplates.size()];
				for(int i=0; i<forUpdateWidgetTypes.length; i++){
					DataTemplate dataTemplate = forUpdateDataTemplates.get(i);
					WidgetType widgetType = new WidgetType(dataTemplate.getName(), dataTemplate.getWidgetDescriptor().getType());
					forUpdateWidgetTypes[i] = widgetType;
				}
			}
			dataTemplates = forUpdateDataTemplates;
			widgetTypes = forUpdateWidgetTypes;
		}
				
		WidgetType defaultWidgetType = getDefaultWidgetType();
		if(defaultWidgetType != null){
			for(int i=0; i<widgetTypes.length; i++){
				if(widgetTypes[i].getName().equalsIgnoreCase(defaultWidgetType.getName())){
					widgetTypes[i] = widgetTypes[0];
					widgetTypes[0] = defaultWidgetType;
				}
			}
		}
		

		return widgetTypes;
	}
	
	public WidgetType getDefaultWidgetType() {
		WidgetType defaultWidgetType = null;
		String defaultWidgetTypeName = Activator.getDefault().getPreferenceStore().getString(bindingName + purpose);
		if(defaultWidgetTypeName == null || defaultWidgetTypeName.equals("")){
			for(DataTemplate dataTemplate : dataTemplates){
				if(dataTemplate.getDataMapping().isDefault()){
					defaultWidgetType = new WidgetType(dataTemplate.getName(), dataTemplate.getWidgetDescriptor().getType());
					break;
				};
			}
		}else{
			for(DataTemplate dataTemplate : dataTemplates){
				if(defaultWidgetTypeName.equalsIgnoreCase(dataTemplate.getName())){
					defaultWidgetType = new WidgetType(dataTemplate.getName(), dataTemplate.getWidgetDescriptor().getType());
					break;
				};
			}
		}
		return defaultWidgetType;
	}
	
	public DataTemplate getDataTemplate(){
		for(DataTemplate dataTemplate : dataTemplates){
			if(dataTemplate.getName().equals(widgetType.getName())){
				return dataTemplate;
			}
		}
		return null;
	}
	public WidgetType getWidgetType(){
		if(widgetTypes == null){
			getWidgetTypes();
			if(widgetTypes.length == 0){
				widgetType = new WidgetType(RUIWidgetType.NONE, RUIWidgetType.NONE);
			}else{
				widgetType = widgetTypes[0];
			}
		}
		return widgetType;
	}
	
	public void setWidgetType(WidgetType widgetType) {
		this.widgetType = widgetType;
	}

	public String getWidgetName() {
		return widgetName;
	}
	
	public void setWidgetName(String widgetName) {
		this.widgetName = widgetName;
	}
	
	public String getDefaultWidgetName(){
		String defaultWidgetName;
		int i = 0;
		String tempWidgetName = bindingName.replace(".", "_") + "_" + getShortWidgetType();
		defaultWidgetName = tempWidgetName;
		while(isFieldNameExist(defaultWidgetName)){
			i++;
			defaultWidgetName = tempWidgetName + i; 
		}
		
		return defaultWidgetName;
	}
	
	private String getShortWidgetType(){
		String shortWidgetType;
		if(parent == null){
			shortWidgetType = "ui";
		}else{
			WidgetType widgetType = getWidgetType();
			String widgetTypeType = widgetType.getType();
			
			List<String> widgetTypeShortNames = new ArrayList<String>();
			for(int i=0; i<widgetTypeType.length(); i++){
				char character = widgetTypeType.charAt(i);   
			    if(character>='A' && character<='Z'){
			    	String widgetTypeShortName = widgetTypeType.substring(i);
			    	widgetTypeShortNames.add(widgetTypeShortName);
			    }
			}
			shortWidgetType = widgetTypeShortNames.get(widgetTypeShortNames.size()-1).toLowerCase();
			
			if(shortWidgetType.equals("box")){
				shortWidgetType = widgetTypeShortNames.get(widgetTypeShortNames.size()-2);
				char ch= Character.toLowerCase(shortWidgetType.charAt(0));
				shortWidgetType = ch + shortWidgetType.substring(1, shortWidgetType.length());
			}
		}
		return shortWidgetType;
	}
	
	private boolean isFieldNameExist(String fieldName){
		if(NameFinder.getInstance().isFieldNameExist(fieldName) || isFieldNameExistInInsertWidgetModel(fieldName)){
			return true;
		}
		return false;
	}
	
	private boolean isFieldNameExistInInsertWidgetModel(String widgetName){
		List<InsertDataNode> roots =  model.getRootDataNodes();
		for(InsertDataNode root : roots){
			return isFieldNameExistInInsertWidgetModel(root, widgetName);
		}
		return false;
	}
	
	private boolean isFieldNameExistInInsertWidgetModel(InsertDataNode parent, String widgetName){
		if(parent.equals(this)){
			return false;
		}
		
		if(parent.getWidgetName() == null){
			if(parent.getDefaultWidgetName().equalsIgnoreCase(widgetName)){
				return true;
			}
		}else{
			if(parent.getWidgetName().equalsIgnoreCase(widgetName)){
				return true;
			}
		}
		
		
		List<InsertDataNode> children = parent.getChildren();
		for(InsertDataNode child : children){
			return isFieldNameExistInInsertWidgetModel(child, widgetName);
		}
		return false;
	}
	
	public class Purpose{
		public static final String FOR_DISPLAY = "FOR_DISPLAY";
		public static final String FOR_CREATE = "FOR_CREATE";
		public static final String FOR_UPDATE = "FOR_UPDATE";
	}
	public class NodeType{		
		public static final String TYPE_PRIMITIVE_ALL = "TYPE_PRIMITIVE_ALL";
		public static final String TYPE_RECORD_ALL = "TYPE_RECORD_ALL";
	}
	public class NodeTypeDetail{
		public static final String TYPE_PRIMITIVE_STRING = "TYPE_PRIMITIVE_STRING";
		public static final String TYPE_PRIMITIVE_DATE = "TYPE_PRIMITIVE_DATE";
		public static final String TYPE_PRIMITIVE_TIME = "TYPE_PRIMITIVE_TIME";
		public static final String TYPE_PRIMITIVE_TIMESTAMP = "TYPE_PRIMITIVE_TIMESTAMP";
		public static final String TYPE_PRIMITIVE_BIGINT = "TYPE_PRIMITIVE_BIGINT";
		public static final String TYPE_PRIMITIVE_BIN = "TYPE_PRIMITIVE_BIN";
		public static final String TYPE_PRIMITIVE_DECIMAL = "TYPE_PRIMITIVE_DECIMAL";
		public static final String TYPE_PRIMITIVE_FLOAT = "TYPE_PRIMITIVE_FLOAT";
		public static final String TYPE_PRIMITIVE_INT = "TYPE_PRIMITIVE_INT";
		public static final String TYPE_PRIMITIVE_NUM = "TYPE_PRIMITIVE_NUM";
		public static final String TYPE_PRIMITIVE_MONEY = "TYPE_PRIMITIVE_MONEY";
		public static final String TYPE_PRIMITIVE_SMALLINT = "TYPE_PRIMITIVE_SMALLINT";
		public static final String TYPE_PRIMITIVE_SMALLFLOAT = "TYPE_PRIMITIVE_SMALLFLOAT";
		public static final String TYPE_PRIMITIVE_BOOLEAN = "TYPE_PRIMITIVE_BOOLEAN";
		
		public static final String TYPE_RECORD_SIMPLE = "TYPE_RECORD_SIMPLE";
		public static final String TYPE_RECORD_EMBED_RECORD = "TYPE_RECORD_EMBED_RECORD";
		public static final String TYPE_RECORD_WITH_PRIMITIVE_ARRAY = "TYPE_RECORD_WITH_PRIMITIVE_ARRAY";
		public static final String TYPE_RECORD_WITH_RECORD_ARRAY = "TYPE_RECORD_WITH_RECORD_ARRAY";
	}
	public class RUIWidgetType{
		public static final String NONE = "None"; 
	}
}
