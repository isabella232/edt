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
package org.eclipse.edt.ide.rui.visualeditor.internal.views.dataview.model;

import org.eclipse.jface.resource.ImageDescriptor;


public class DataFieldPageDataNode extends PageDataNode {
	private String dataBindingName;
	
	public DataFieldPageDataNode(){
		super();
	}
	public DataFieldPageDataNode(String name, ImageDescriptor img) {
		super(name, img);	
	}
	public String getDataBindingName() {
		return dataBindingName;
	}
	public void setDataBindingName(String dataBindingName) {
		this.dataBindingName = dataBindingName;
	}
}
