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

import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.layout.WidgetLayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;


/**
 * This class contains information about where an existing or new widget may be dropped.
 */
public class EvDesignOverlayDropLocation {
	public int			iIndex			= -1;
	public int			iLocation		= SWT.CENTER;
	public WidgetPart	widgetParent	= null;
	public Rectangle	rectDrop		= new Rectangle( 0, 0, 0, 0 );
	public WidgetLayoutData widgetLayoutData = null;
	
	public int          selectedTransparent = 255;
	public int          nonselectedTransparent = 255;
	public int          lineStyle = SWT.LINE_SOLID;
	public int          lineWidth = 2;
	
	/**
	 * 
	 */
	public void setBounds( int iX, int iY, int iWidth, int iHeight ){
		rectDrop.x = iX;
		rectDrop.y = iY;
		rectDrop.width = iWidth;
		rectDrop.height = iHeight;
	}
	
	/**
	 * 
	 */
	public void setBounds( Rectangle rectLocation ){
		rectDrop.x = rectLocation.x;
		rectDrop.y = rectLocation.y;
		rectDrop.width = rectLocation.width;
		rectDrop.height = rectLocation.height;
	}
}
