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
package org.eclipse.edt.ide.rui.visualeditor.internal.views.dataview.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

public class PageDataNode {
	private String name;
	private ImageDescriptor imageDescriptor;
	private PageDataNode parent;
	private List<PageDataNode> children;
	
	public PageDataNode(){}
	
	public PageDataNode(String name, ImageDescriptor imageDescriptor){
		this.name = name;
		this.imageDescriptor = imageDescriptor;
		this.children = new ArrayList<PageDataNode>();
	}
	public String getName() {
		return name;
	}
	public PageDataNode getParent() {
		return parent;
	}
	public void setParent(PageDataNode parent) {
		this.parent = parent;
	}
	public List<PageDataNode> getChildren() {
		return children;
	}
	public void addChild(PageDataNode child){
		this.children.add(child);
		child.setParent(this);
	}
	public boolean hasChildren(){
		if(children.isEmpty()){
			return false;
		}
		return true;
	}
	public Image getImage(){
		return imageDescriptor.createImage();
	}
}
