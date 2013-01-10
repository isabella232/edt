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

import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvDesignOverlay;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvDesignOverlayDropLocation;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvDesignPage;
import org.eclipse.edt.ide.rui.visualeditor.internal.views.dataview.model.PageDataNode;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetDescriptor;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetDescriptorRegistry;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.EvInsertWidgetWizardDialog;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.InsertWidgetWizard;
import org.eclipse.gef.dnd.TemplateTransfer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


/**
 * Supports dropping widgets into a tree viewer.
 */
public class WidgetDropAdapter extends ViewerDropAdapter {
	protected Cursor _cursorWait;
	private WidgetTransfer transfer;
	private EvDesignPage designPage;
	private EvDesignOverlay overlay;
	
	public WidgetDropAdapter( TreeViewer viewer, EvDesignPage designPage, WidgetTransfer transfer, EvDesignOverlay overlay ) {
		super(viewer);
		this.transfer = transfer;
		this.designPage = designPage;
		this.overlay = overlay;
	}

	/**
	 * Method declared on ViewerDropAdapter
	 */
	public boolean performDrop(Object data) {
		if ( data == null ) {
			return false;
		}
		if ( _cursorWait == null ) {
			_cursorWait = new Cursor( this.getViewer().getControl().getDisplay(), SWT.CURSOR_WAIT );
		}
		this.getViewer().getControl().setCursor( _cursorWait );
		int location = getCurrentLocation();
		
		WidgetPart target = (WidgetPart) getCurrentTarget();
		if (target == null) {
			target = designPage.getWidgetManager().getWidgetRoot();
		}
		
		int iTargetChildIndex = 0;
		WidgetPart parent = null;
		switch ( location ) {
			case LOCATION_NONE:
			case LOCATION_ON:
				iTargetChildIndex = 0;
				parent = target;
				break;
			case LOCATION_AFTER:
				parent = target.getParent();
				iTargetChildIndex = parent.getChildIndex( target ) + 1;
				break;
			case LOCATION_BEFORE:
				parent = target.getParent();
				iTargetChildIndex = parent.getChildIndex( target );
				break;
		}
		
		EvDesignOverlayDropLocation targetLocation = new EvDesignOverlayDropLocation();
		targetLocation.widgetParent = parent;
		targetLocation.iIndex = iTargetChildIndex;
		
		if( data instanceof String ) {
			//Create a new widget
			String strWidgetID = data.toString();
			WidgetDescriptor descriptor = WidgetDescriptorRegistry.getInstance(this.designPage.getEditor().getProject()).getDescriptor( strWidgetID );
			overlay.doOperationWidgetCreate( descriptor, targetLocation );
		} else if( data instanceof WidgetPart[] ) {
			//Move an existing widget
			WidgetPart[] toDrop = (WidgetPart[]) data;
			for (int i = 0; i < toDrop.length; i++) {
				overlay.doOperationWidgetMove( toDrop[i], targetLocation );
			}
		} else if( data instanceof PageDataNode){
			//Create new widgets from EGL Data View
			PageDataNode pageDataNode = (PageDataNode)data;
			InsertWidgetWizard insertWidgetWizard = new InsertWidgetWizard(pageDataNode, overlay, targetLocation);
			Shell shell = Display.getCurrent().getActiveShell();
			EvInsertWidgetWizardDialog evInsertWidgetWizardDialog = new EvInsertWidgetWizardDialog(shell, insertWidgetWizard);
			evInsertWidgetWizardDialog.setPageSize(600,400);
			evInsertWidgetWizardDialog.open();
		}
		this.getViewer().getControl().setCursor( null );
		
		return true;
	}

	/**
	 * Method declared on ViewerDropAdapter
	 */
	public boolean validateDrop(Object target, int op, TransferData type) {
		if ( target == null ) {
			return false;
		}
		WidgetPart targetWidget = (WidgetPart) target;
		
		//can not dnd a widget onto a DojoTree and DojoTreeNode
		if (targetWidget.getTypeName().equalsIgnoreCase("DojoTree") || targetWidget.getTypeName().equalsIgnoreCase("DojoTreeNode")){
			return false;
		}
		
		if ( transfer.isSupportedType(type) || TemplateTransfer.getInstance().isSupportedType( type ) ) {
			int location = getCurrentLocation();
			if(designPage.getEditor().getEditorProvider().isRUIWidget() && designPage.getWidgetManager().getWidgetRoot().equals(targetWidget.getParent()) && (LOCATION_BEFORE == location || LOCATION_AFTER == location)){
				return false;
			}
			
			//can not dnd a widget onto DojoBorderContainer, can only dnd a widget into DojoContentPane which is in DojoBorderContainer
			if (targetWidget.getTypeName().equalsIgnoreCase("DojoContentPane") && targetWidget.getParent() != null 
					&& (targetWidget.getParent().getTypeName().equalsIgnoreCase("DojoBorderContainer") 
							|| targetWidget.getParent().getTypeName().equalsIgnoreCase("DojoAccordionContainer") 
							|| targetWidget.getParent().getTypeName().equalsIgnoreCase("DojoStackContainer")
							|| targetWidget.getParent().getTypeName().equalsIgnoreCase("DojoTabContainer"))
					&& (LOCATION_BEFORE == location || LOCATION_AFTER == location)){
				return false;
			}
			
			WidgetDescriptorRegistry registry = WidgetDescriptorRegistry.getInstance(designPage.getEditor().getProject());
			WidgetDescriptor descriptor = registry.getDescriptor( targetWidget.getTypeID() );
			if ( descriptor != null && LOCATION_ON == location && ( !isContainer( targetWidget ) || descriptor._layoutDescriptor != null ) ) {
				return false;
			}
			
			descriptor = registry.getDescriptor( targetWidget.getParent().getTypeID() );
			if ( LOCATION_ON != location && descriptor != null && descriptor._layoutDescriptor != null ) {
				return false;
			}
		}
		if ( transfer.isSupportedType(type) ) {
			WidgetPart selectedWidget = (WidgetPart)this.getSelectedObject();
			if ( designPage.getWidgetManager().isChildOf( targetWidget, selectedWidget ) ) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns whether a widget type is a container.
	 */
	protected boolean isContainer( WidgetPart widget ){
		// Legacy support for widgets prior to 1.0.2
		if( widget.getTypeName().equalsIgnoreCase( "box" ) ||widget.getTypeName().equalsIgnoreCase( "div" ) 
				|| widget.getTypeName().equalsIgnoreCase( "span" ) || widget.getTypeName().equalsIgnoreCase( "grouping" ) 
				|| widget.getTypeName().equalsIgnoreCase( "treenode" ) || widget.getTypeName().equalsIgnoreCase( "tree" ) ){
			return true;
		}
		
		WidgetDescriptor descriptor = WidgetDescriptorRegistry.getInstance(designPage.getEditor().getProject()).getDescriptor( widget.getTypeID() );
		if(descriptor != null){
			return descriptor.isContainer();
		}
		return false;
	}

}
