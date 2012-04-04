/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.editor;

import java.util.HashMap;
import java.util.Map;

public class EvWidgetContextMenuProviderRegister {
	private static EvWidgetContextMenuProviderRegister	_instance	= new EvWidgetContextMenuProviderRegister();
	
	private Map<String, EvWidgetContextMenuProvider>	_widgetContextMenuProviders	= new HashMap<String, EvWidgetContextMenuProvider>();
	
	private EvWidgetContextMenuProviderRegister() {

	}
	
	public static EvWidgetContextMenuProviderRegister getInstance() {
		return _instance;
	}

	public void addEvWidgetContextMenuProvider( EvWidgetContextMenuProvider widgetContextMenuProvider ) {
		_widgetContextMenuProviders.put( widgetContextMenuProvider.getClass().getName(), widgetContextMenuProvider );
	}
	
	public Map<String, EvWidgetContextMenuProvider> getEvWidgetContextMenuProviders() {
		return _widgetContextMenuProviders;
	}
}
