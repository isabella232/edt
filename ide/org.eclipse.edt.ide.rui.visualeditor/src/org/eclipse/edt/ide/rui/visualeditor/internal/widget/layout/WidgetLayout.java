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

import java.util.Collection;
import java.util.List;

import org.eclipse.edt.ide.rui.server.EvEditorProvider;
import org.eclipse.edt.ide.rui.server.PropertyValue;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetDescriptorRegistry;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetManager;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;
import org.eclipse.swt.custom.ScrolledComposite;


public interface WidgetLayout {

	void initialize( ScrolledComposite parentComposite, WidgetPart widget, WidgetPart _widgetDragging, WidgetManager widgetManager, WidgetDescriptorRegistry widgetDescriptorRegistry, EvEditorProvider evEditorProvider );
	void setupDropLocations( Collection listDropLocations );
	List getInnerRectanglesForPaintingWidget(WidgetPart widget);
	
	void updateLayoutData( EvEditorProvider editorProvider, int statementOffset, WidgetPart widget, Object layoutData, PropertyValue oldLayoutData, String layoutDataTemplte );
	String processNewLayoutData( Object layoutData, String template );
	String[] getLayoutDataQualifiedName();
	String[] getLayoutWidgetQualifiedName();
}
