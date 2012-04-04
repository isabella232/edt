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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.InsertDataNode;


public class ComposeGenNode extends GenNode {
	private List<GenNode> children;
	
	public ComposeGenNode(GenModel genModel, InsertDataNode insertDataNode){
		super(genModel, insertDataNode);
		children = new ArrayList<GenNode>();
	}

	public List<GenNode> getChildren(){
		return children;
	}
	
	public void setChildren(List<GenNode> children){
		this.children = children;
	}
	
	public void addChild(GenNode child){
		children.add(child);
		child.setGenModel(getGenModel());
	}
}
