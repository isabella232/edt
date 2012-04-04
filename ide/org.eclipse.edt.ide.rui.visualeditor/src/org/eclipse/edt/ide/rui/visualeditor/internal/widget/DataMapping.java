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

public class DataMapping {
	private DataTemplate dataTemplate;
	private boolean forArray;
	private boolean isContainer;
	private boolean genChildWidget;
	private boolean isDefault;
	private EnumerationEntry[] mappings;
	
	public DataMapping(DataTemplate dataTemplate){
		this.dataTemplate = dataTemplate;
		this.forArray = false;
		this.isContainer = false;
		this.genChildWidget = false;
		this.isDefault = false;
		this.mappings = new EnumerationEntry[0];
	}
	
	public boolean isForArray() {
		return forArray;
	}
	public void setForArray(boolean forArray) {
		this.forArray = forArray;
	}
	public boolean isContainer() {
		return isContainer;
	}
	public void setContainer(boolean isContainer) {
		this.isContainer = isContainer;
	}
	public EnumerationEntry[] getMappings() {
		return mappings;
	}
	public void setMappings(EnumerationEntry[] mappings) {
		this.mappings = mappings;
	}
	public boolean isGenChildWidget() {
		return genChildWidget;
	}
	public void setGenChildWidget(boolean genChildWidget) {
		this.genChildWidget = genChildWidget;
	}
	public DataTemplate getDataTemplate() {
		return dataTemplate;
	}
	public void setDataTemplate(DataTemplate dataTemplate) {
		this.dataTemplate = dataTemplate;
	}
	public boolean isDefault() {
		return isDefault;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
}
