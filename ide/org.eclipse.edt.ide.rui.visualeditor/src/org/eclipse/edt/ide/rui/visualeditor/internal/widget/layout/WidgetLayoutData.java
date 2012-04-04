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
package org.eclipse.edt.ide.rui.visualeditor.internal.widget.layout;

public class WidgetLayoutData {

	private Object layoutData;
	private WidgetLayout layout;
	
	public WidgetLayoutData( Object layoutData, WidgetLayout layout ) {
		this.layoutData = layoutData;
		this.layout = layout;
	}
	
	public Object getLayoutData() {
		return this.layoutData;
	}
	
	public WidgetLayout getWidgetLayout() {
		return this.layout;
	}
}
