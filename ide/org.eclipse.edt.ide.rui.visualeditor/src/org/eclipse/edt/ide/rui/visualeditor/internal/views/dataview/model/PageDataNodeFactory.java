/*******************************************************************************
 * Copyright © 2010, 2011 IBM Corporation and others.
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


public class PageDataNodeFactory {
	public static final int HANDLER_PAGE_DATA_NODE = 1;
	public static final int DATA_FIELD_PAGE_DATA_NODE = 2;
	
	public static PageDataNode newPageDataNode(String name, int nodeType){
		switch(nodeType){
		case HANDLER_PAGE_DATA_NODE:
			return new HandlerPageDataNode(name);
		case DATA_FIELD_PAGE_DATA_NODE:
			return new DataFieldPageDataNode(name);
		default:
			return null;
		}
	}
}
