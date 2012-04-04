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
package org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen;

import java.util.List;

import org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.GenNode.Context;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.engine.GenVariableResolverManager;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.InsertDataNode;



public class TemplateComposer {
	
	private static TemplateComposer instance;
	private TemplateComposer(){}
	
	public static TemplateComposer getInstance(){
		if(instance == null){
			instance = new TemplateComposer();
		}
		return instance;
	}
	
	public String compose(GenNode genNode){
		InsertDataNode insertDataNode = genNode.getInsertDataNode();
		String template = insertDataNode.getDataTemplate().getTemplate();
		template = addInsertedLayoutDataTemplate(genNode, template);
		
		StringBuffer sbTemplate = new StringBuffer();
		
		//add name label
		if(insertDataNode.getParent() != null){
			String nameLabelTemplate = insertDataNode.getParent().getDataTemplate().getChildNameLabelTemplate();
			if(nameLabelTemplate != null){
				sbTemplate.append("\t").append(addInsertedLayoutDataTemplate(genNode, nameLabelTemplate));
			}
		}
		
		//add self
		sbTemplate.append("\n\t").append(template);
		
		//for display
		if(insertDataNode.getModel().getPurpose().equals(InsertDataNode.Purpose.FOR_DISPLAY)){
			//add mvc support
			if(insertDataNode.getModel().isAddFormattingAndValidation()){
				//add controller
				addController(insertDataNode, sbTemplate, genNode);
			}
			
			//add a blank line
			addABlankLine(sbTemplate);
		}else{
			//add mvc support
			if(insertDataNode.getModel().isAddFormattingAndValidation()){
				//add controller
				addController(insertDataNode, sbTemplate, genNode);
				
				//add error label
				addErrorLabel(insertDataNode, sbTemplate, genNode);
				
				//add form field
				addFormField(insertDataNode, sbTemplate, genNode);
				
				//add a blank line
				addABlankLine(sbTemplate);
				
				//add form manager
				addFormManger(insertDataNode, sbTemplate);
			}else{
				//add a blank line
				addABlankLine(sbTemplate);
			}	
		}
		
		String newTemplate = sbTemplate.toString();
		
		if(insertDataNode.getParent() != null && insertDataNode.getParent().getDataTemplate().getDataMapping().isGenChildWidget() == false){
			newTemplate = "";
		}

		return newTemplate;
	}
	
	private void addABlankLine(StringBuffer sbTemplate){
		sbTemplate.append("\n");
	}
	
	private void addFormManger(InsertDataNode insertDataNode, StringBuffer sbTemplate){
		String formManagerTemplate = insertDataNode.getDataTemplate().getFormManagerTemplate();
		if(formManagerTemplate != null){
			sbTemplate.append("\n\t").append(formManagerTemplate);
		}
	}
	
	private void addFormField(InsertDataNode insertDataNode, StringBuffer sbTemplate, GenNode genNode){
		if(insertDataNode.getParent() != null && insertDataNode.getDataTemplate().isGenController()){
			String formFieldTemplate = insertDataNode.getParent().getDataTemplate().getChildFormFieldTemplate();
			if(formFieldTemplate != null){
				sbTemplate.append("\n\t").append(getFormFieldTemplate(genNode, formFieldTemplate));
			}
		}
	}
	
	private void addErrorLabel(InsertDataNode insertDataNode, StringBuffer sbTemplate, GenNode genNode){
		if(insertDataNode.getParent() != null && insertDataNode.getModel().isAddErrorMessage() && insertDataNode.getDataTemplate().isGenController()){
			String errorLabelTemplate = insertDataNode.getParent().getDataTemplate().getChildErrorLabelTemplate();
			if(errorLabelTemplate != null){
				sbTemplate.append("\n\t").append(addInsertedLayoutDataTemplate(genNode, errorLabelTemplate));
			}
		}
	}
	
	private void addController(InsertDataNode insertDataNode, StringBuffer sbTemplate, GenNode genNode){
		if(insertDataNode.getParent() != null && insertDataNode.getDataTemplate().isGenController()){
			StringBuffer sbControllerTemplate = new StringBuffer();
			String controllerTemplate = insertDataNode.getParent().getDataTemplate().getChildControllerTemplate();
			String validStateSetterTemplate = insertDataNode.getParent().getDataTemplate().getChildControllerValidStateSetterTemplate();
			String publishMessageHelperTemplate = insertDataNode.getParent().getDataTemplate().getChildControllerPublishMessageHelperTemplate();
			if(controllerTemplate != null){
				if(insertDataNode.getModel().getPurpose().equals(InsertDataNode.Purpose.FOR_DISPLAY)){
					sbControllerTemplate.append(controllerTemplate);
				}else{
					controllerTemplate = controllerTemplate.substring(0, controllerTemplate.lastIndexOf("}")-1);
					if(insertDataNode.getModel().isAddErrorMessage()){
						sbControllerTemplate.append(controllerTemplate).append(", ").append(publishMessageHelperTemplate).append("};");
					}else{
						sbControllerTemplate.append(controllerTemplate).append(", ").append(validStateSetterTemplate).append("};");
					}
				}
		
				sbTemplate.append("\n\t").append(sbControllerTemplate.toString());
			}
		}
	}
	
	private String getFormFieldTemplate(GenNode genNode, String template){
		String newTemplate = template;
		if(!genNode.getInsertDataNode().getModel().isAddErrorMessage()){
			int index1 = newTemplate.lastIndexOf(",");
			String firstPart = newTemplate.substring(0, index1);
			newTemplate = firstPart + "};";
		}
		return newTemplate;
	}
	
	private String addInsertedLayoutDataTemplate(GenNode genNode, String template){
		String newTemplate = template;
		if(!(genNode.equals(genNode.getGenModel().getRoot()))){
			String childLayoutDataTemplate = genNode.getGenModel().getRoot().getInsertDataNode().getDataTemplate().getChildLayoutDataTemplate();
			if(childLayoutDataTemplate != null){
				int index1 = template.lastIndexOf("}");
				String startPart = template.substring(0, index1);
				String endPart = template.substring(index1 + 1, template.length());
				int index2 = startPart.lastIndexOf("{");
				if(startPart.substring(index2+1, startPart.length()).trim().length() > 0){
					newTemplate = startPart + ", " + childLayoutDataTemplate + endPart;
				}else{
					newTemplate = startPart + childLayoutDataTemplate + endPart;
				}
			}
		}
		return newTemplate;
	}
	
	public Templates compose(GenModel genModel){
		Templates templates = new Templates();
		ComposeGenNode root = genModel.getRoot();
		
		StringBuffer sbTemplate = new StringBuffer();		
		List<GenNode> genNodes = root.getChildren();
		for(int i=0; i<genNodes.size(); i++){
			GenNode genNode = genNodes.get(i);
			GenVariableResolverManager.getInstance().resolve(genNode);
			String template = genNode.getTemplate();
			sbTemplate.append(template).append("\n");
		}
		
		GenVariableResolverManager.getInstance().resolve(root);
		
		String template = "";
		if(!root.getInsertDataNode().getModel().getPurpose().equals(InsertDataNode.Purpose.FOR_DISPLAY) && root.getInsertDataNode().getDataTemplate().getFormManagerTemplate() != null){
			String rootTemplate = root.getTemplate();
			int index = rootTemplate.indexOf(";");
			String firstPart = rootTemplate.substring(0, index+1);
			String secondPart = rootTemplate.substring(index+1);
			template = firstPart + "\n\n\t" + sbTemplate.toString().trim() + "\t" + secondPart  + getValidStateSetter(root);
		}else{
			template = root.getTemplate() + "\n" +sbTemplate.toString();
		}
		
		int index = template.indexOf("function ");
		if(index != -1){
			String widgetTemplate = template.substring(0, index);
			widgetTemplate = widgetTemplate.substring(0, widgetTemplate.lastIndexOf(";"));
			templates.setWidgetTemplate(widgetTemplate);
			
			String functionTemplate = template.substring(index);
			templates.setFunctionTemplate(functionTemplate);
		}else{
			String widgetTemplate = template.substring(0, template.lastIndexOf(";"));
			templates.setWidgetTemplate(widgetTemplate);
		} 
		
		return templates;
	}
	
	private String getValidStateSetter(ComposeGenNode root){
		StringBuffer sbValidStateSetter = new StringBuffer();
		if(!root.getInsertDataNode().getModel().getPurpose().equals(InsertDataNode.Purpose.FOR_DISPLAY)){
			if(root.getInsertDataNode().getModel().isAddFormattingAndValidation()){
				if(!root.getInsertDataNode().getModel().isAddErrorMessage()){
					Object oValidStateSetter = root.getContext().get(Context.VALID_STATE_SETTER_NAME);
					Object oFormFieldMangerName = root.getContext().get(Context.FORM_FIELD_MANAGER_NAME);
					if(oValidStateSetter != null && oFormFieldMangerName != null){
						String validStateSetter = (String)oValidStateSetter;
						String formFieldMangerName = (String)oFormFieldMangerName;
						sbValidStateSetter.append("\nfunction ").append(validStateSetter).append("(view Widget in, valid boolean in)\n");
						sbValidStateSetter.append("\tfor (n int from ").append(formFieldMangerName).append(".entries.getSize() to 1 decrement by 1)\n");
						sbValidStateSetter.append("\t\tentry ${typeName:org.eclipse.edt.rui.mvc.FormField} = ").append(formFieldMangerName).append(".entries[n];\n");
						sbValidStateSetter.append("\t\tif(entry.controller.view == view)\n");
						sbValidStateSetter.append("\t\t\tif(valid)\n");
						sbValidStateSetter.append("\t\t\t\t// TODO: handle valid value\n");
						sbValidStateSetter.append("\t\t\telse\n");
						sbValidStateSetter.append("\t\t\t\tmsg String? = entry.controller.getErrorMessage();\n");
						sbValidStateSetter.append("\t\t\t\t// TODO: handle invalid value\n");
						sbValidStateSetter.append("\t\t\tend\n");
						sbValidStateSetter.append("\t\tend\n");
						sbValidStateSetter.append("\tend\n");
						sbValidStateSetter.append("end");
					}
				}
			}
		}
		
		return sbValidStateSetter.toString();
	}
}
