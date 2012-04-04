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
package org.eclipse.edt.ide.rui.visualeditor.internal.widget;

import org.eclipse.edt.mof.egl.EnumerationEntry;

public class DataTemplate {
	private String name;
	private DataMapping dataMapping;
	private EnumerationEntry[] purposes;
	private boolean genController;
	private String template;
	private String formManagerTemplate;
	private String childLayoutDataTemplate;
	private String childNameLabelTemplate;
	private String childErrorLabelTemplate;
	private String childControllerTemplate;
	private String childControllerValidStateSetterTemplate;
	private String childControllerPublishMessageHelperTemplate;
	private String childFormFieldTemplate;
	private WidgetDescriptor widgetDescriptor;
	
	public DataTemplate(WidgetDescriptor widgetDescriptor){
		this.name = "";
		this.dataMapping = new DataMapping(this);
		this.purposes = new EnumerationEntry[0];
		this.template = "";
		this.widgetDescriptor = widgetDescriptor;
		this.genController = false;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public DataMapping getDataMapping() {
		return dataMapping;
	}
	public EnumerationEntry[] getPurposes() {
		return purposes;
	}
	public void setPurposes(EnumerationEntry[] purposes) {
		this.purposes = purposes;
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public WidgetDescriptor getWidgetDescriptor() {
		return widgetDescriptor;
	}
	public String getChildLayoutDataTemplate() {
		return childLayoutDataTemplate;
	}
	public void setChildLayoutDataTemplate(String childLayoutDataTemplate) {
		this.childLayoutDataTemplate = childLayoutDataTemplate;
	}
	public String getChildNameLabelTemplate() {
		return childNameLabelTemplate;
	}
	public void setChildNameLabelTemplate(String childNameLabelTemplate) {
		this.childNameLabelTemplate = childNameLabelTemplate;
	}
	public String getChildErrorLabelTemplate() {
		return childErrorLabelTemplate;
	}
	public void setChildErrorLabelTemplate(String childErrorLabelTemplate) {
		this.childErrorLabelTemplate = childErrorLabelTemplate;
	}
	public String getChildControllerTemplate() {
		return childControllerTemplate;
	}
	public void setChildControllerTemplate(String childControllerTemplate) {
		this.childControllerTemplate = childControllerTemplate;
	}
	public String getChildFormFieldTemplate() {
		return childFormFieldTemplate;
	}
	public void setChildFormFieldTemplate(String childFormFieldTemplate) {
		this.childFormFieldTemplate = childFormFieldTemplate;
	}
	public String getFormManagerTemplate() {
		return formManagerTemplate;
	}
	public void setFormManagerTemplate(String formManagerTemplate) {
		this.formManagerTemplate = formManagerTemplate;
	}
	public boolean isGenController() {
		return genController;
	}
	public void setGenController(boolean genController) {
		this.genController = genController;
	}
	public String getChildControllerValidStateSetterTemplate() {
		return childControllerValidStateSetterTemplate;
	}
	public void setChildControllerValidStateSetterTemplate(
			String childControllerValidStateSetterTemplate) {
		this.childControllerValidStateSetterTemplate = childControllerValidStateSetterTemplate;
	}
	public String getChildControllerPublishMessageHelperTemplate() {
		return childControllerPublishMessageHelperTemplate;
	}
	public void setChildControllerPublishMessageHelperTemplate(
			String childControllerPublishMessageHelperTemplate) {
		this.childControllerPublishMessageHelperTemplate = childControllerPublishMessageHelperTemplate;
	}
}
