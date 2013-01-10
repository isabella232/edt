/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.widget.layout;

import java.util.HashMap;

import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvWidgetContextMenuProviderRegister;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;


public class WidgetLayoutRegistry {
	public static final String VE_HANDLER = "VE-HANDLER";
	public static final String ROOT = "ROOT";

	private static WidgetLayoutRegistry	_instance	= new WidgetLayoutRegistry();
	
	private HashMap	_hashWidgetLayouts	= new HashMap();
	
	private WidgetLayoutRegistry() {
		init();
	}
	
	public static WidgetLayoutRegistry getInstance() {
		return _instance;
	}
	
	public WidgetLayout getWidgetLayout( String widgetId, boolean isContainer ) {
		Class layout = (Class)_hashWidgetLayouts.get( widgetId );
		if ( layout == null && isContainer ) {
			return new DefaultWidgetLayout();
		}
		if (layout != null) {
			try {
				return (WidgetLayout)layout.newInstance();
			} catch ( Exception e ) {
			}
		}
		return null;
	}
	
	public void addWidgetLayout( String widgetId, Class layout ) {
		_hashWidgetLayouts.put( widgetId, layout );
	}
	
	private void init() {
		addWidgetLayout(VE_HANDLER, HandlerWidgetLayout.class );
		addWidgetLayout(ROOT, RootWidgetLayout.class );
		addWidgetLayout("org.eclipse.edt.rui.widgets.GridLayout", GridLayoutWidgetLayout.class );
		
		EvWidgetContextMenuProviderRegister.getInstance().addEvWidgetContextMenuProvider(new GridLayoutWidgetContextMenuProvider() );
	}
	
	public static String getLayoutName( WidgetPart widget ) {
		return ( widget.getPackageName() == null ? "" : widget.getPackageName() + "." )  +  widget.getTypeName();
	}
}
