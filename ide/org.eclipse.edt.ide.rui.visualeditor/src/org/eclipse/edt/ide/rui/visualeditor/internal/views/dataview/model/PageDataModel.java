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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageDataModel {
	private List<PageDataNode> rootPageDataNodes;
	
	public PageDataModel(){
		rootPageDataNodes = new ArrayList<PageDataNode>();
	}
	
	public void addRootPageDataNode(PageDataNode pageDataNode){
		rootPageDataNodes.add(pageDataNode);
	}
	
	public List getRootPageDataNodes(){
		return rootPageDataNodes;
	}
	
	public class Context{
		public static final String PARENT_NODE = "PARENT_NODE";
		
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

