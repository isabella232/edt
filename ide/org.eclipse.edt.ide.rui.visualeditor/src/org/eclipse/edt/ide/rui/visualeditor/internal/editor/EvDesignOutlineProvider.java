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

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;
import org.eclipse.edt.ide.rui.visualeditor.plugin.Activator;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;


public class EvDesignOutlineProvider extends LabelProvider implements ITreeContentProvider {
	
	
	
	/**
	 * Overrides LabelProvider
	 * Obtains an image using the image name stored in the element.
	 * If the parser is not here, then the parser is asked for it.
	 */
	public Image getImage( Object object ) {
		
		if( object instanceof WidgetPart == false )
			return null;
		
		return Activator.getDefault().getWidgetImage( (WidgetPart)object );
	}
	
	/**
	 * Overrides LabelProvider
	 */
	public String getText( Object element ){
		if( element instanceof WidgetPart ){
			WidgetPart widget = (WidgetPart)element;
			
			// Check for invalid statement because it is in another RUI Handler
			//-----------------------------------------------------------------
//			if( widget.getStatementOffset() < 0 || widget.getStatementLength() <= 0 )
//				return "(" + strText + ")";
			
			return widget.getLabel();
		}
		
		return "";
	}

	/**
	 * Declared in ITreeContentProvider
	 */
	public Object[] getChildren( Object object ) {
		if( object instanceof WidgetPart ) {
			return ( (WidgetPart)object ).getChildren().toArray();
		}

		return new Object[0];
	}

	/**
	 * Declared in ITreeContentProvider
	 */
	public Object getParent( Object object ) {
		if( object instanceof WidgetPart )
			return ( (WidgetPart)object ).getParent();

		return null;
	}

	/**
	 * Declared in ITreeContentProvider
	 */
	public boolean hasChildren( Object object ) {
		if( object instanceof WidgetPart )
			return getChildren( object ).length > 0;

		return false;
	}

	/**
	 * Declared in ITreeContentProvider
	 * Returns an array of child elements parented by the given element.
	 */
	public Object[] getElements( Object object ) {
		return getChildren( object );
	}

	/**
	 * Declared in ITreeContentProvider
	 */
	public void inputChanged( Viewer arg0, Object arg1, Object arg2 ) {
	}
}
