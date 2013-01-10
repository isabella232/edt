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
package org.eclipse.edt.ide.rui.visualeditor.internal.outline;

import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;


/**
 * Supports dragging widgets from a structured viewer.
 */
public class WidgetDragListener extends DragSourceAdapter {
	private StructuredViewer viewer;
	private WidgetTransfer transfer;

	public WidgetDragListener(StructuredViewer viewer, WidgetTransfer transfer) {
		this.viewer = viewer;
		this.transfer = transfer;
	}

	/**
	 * Method declared on DragSourceListener
	 */
	public void dragSetData(DragSourceEvent event) {
		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		WidgetPart[] widgets = (WidgetPart[]) selection.toList().toArray( new WidgetPart[selection.size()] );
		if ( transfer.isSupportedType( event.dataType ) ) {
			event.data = widgets;
		}
	}

	/**
	 * Method declared on DragSourceListener
	 */
	public void dragStart(DragSourceEvent event) {
		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		event.doit = ( selection.size() == 1 );
		
		if(selection.getFirstElement() instanceof WidgetPart){
			WidgetPart widgetPart = (WidgetPart)selection.getFirstElement();
			if("DojoTreeNode".equalsIgnoreCase(widgetPart.getTypeName())){
				event.doit = false;
			};
		}
		
	}
}
