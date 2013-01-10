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

import org.eclipse.edt.ide.ui.internal.PluginImages;

public class PageDataNodeFactory {
	public static final int HANDLER_PAGE_DATA_NODE = 1;
	public static final int DATA_FIELD_PAGE_DATA_NODE_PUBLIC = 2;
	public static final int DATA_FIELD_PAGE_DATA_NODE_PRIVATE = 3;
	
	public static PageDataNode newPageDataNode(String name, int nodeType){
		switch(nodeType){
		case HANDLER_PAGE_DATA_NODE:
			return new HandlerPageDataNode(name);
		case DATA_FIELD_PAGE_DATA_NODE_PUBLIC:
			return new DataFieldPageDataNode(name, PluginImages.DESC_OBJS_VARIABLEDECL);
		case DATA_FIELD_PAGE_DATA_NODE_PRIVATE:
			return new DataFieldPageDataNode(name, PluginImages.DESC_OBJS_OBJS_ENV_VAR_PRIVATE);
		default:
			return null;
		}
	}
}
