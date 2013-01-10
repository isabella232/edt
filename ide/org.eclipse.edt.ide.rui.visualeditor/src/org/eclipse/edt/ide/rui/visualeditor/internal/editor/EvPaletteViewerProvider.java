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
package org.eclipse.edt.ide.rui.visualeditor.internal.editor;

import org.eclipse.edt.ide.rui.visualeditor.internal.palette.EvPaletteCreationEntry;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.internal.ui.palette.editparts.ToolEntryEditPart;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;


/**
 * This class allows us to listen to establish drag/drop and to listen for an enter key press.
 */
public class EvPaletteViewerProvider extends PaletteViewerProvider implements KeyListener {
	
	protected EvDesignPage _designPage = null;
	protected PaletteViewer _viewer = null;
	
	/**
	 * 
	 */
	public EvPaletteViewerProvider( EditDomain domain, EvDesignPage designPage ){
		super( domain );

		_designPage = designPage;
	}

	/**
	 * Add a template transfer drag source listener.
	 */
	protected void configurePaletteViewer( PaletteViewer viewer ) {
		super.configurePaletteViewer( viewer );
		
		// Do not provide a customizer, since the palette is dynamically populated.
		// Otherwise a Customize menu item appears in the context menu.
		//-------------------------------------------------------------------------
		// viewer.setCustomizer( new EvPaletteCustomizer() );

		viewer.addDragSourceListener( new EvTemplateTransferDragSourceListener( viewer ) );
	}

	/**
	 * Creates the palette viewer and adds a key listener to it.
	 * This allows us to listen for the enter key press.
	 */
	public PaletteViewer createPaletteViewer( Composite compositeParent ){
		_viewer = super.createPaletteViewer( compositeParent );
		_viewer.getControl().addKeyListener( this );					
		return _viewer;
	}

	protected void hookPaletteViewer( PaletteViewer viewer ) {
		super.hookPaletteViewer( viewer );
	}

	/**
	 * Defined in KeyListener.  Does nothing.
	 */
	public void keyPressed( KeyEvent event ) {
	}

	/**
	 * Defined in KeyListener.  Listens for an enter key press and notifies the design page of the currently selected palette item ID.
	 */
	public void keyReleased( KeyEvent event ) {
		if( event.character == '\r' || event.keyCode == 13 ){
			ToolEntry tool = _viewer.getActiveTool();
			if( tool != null ){
				if(tool instanceof EvPaletteCreationEntry){
					// BWS - Will this tool ever be something other than an EvPaletteCreationEntry?
					EvPaletteCreationEntry evTool = (EvPaletteCreationEntry) _viewer.getActiveTool();
					_designPage.paletteItemSelected( (String)evTool.getTemplate() );
				}else{
					_designPage.paletteItemSelected( tool.getId() );
				}
			}
		}
	}
	
	class EvTemplateTransferDragSourceListener extends TemplateTransferDragSourceListener {
		private EditPartViewer viewer;
		public EvTemplateTransferDragSourceListener(EditPartViewer viewer) {
			super(viewer);
			this.viewer = viewer;
		}

		@Override
		public void dragStart(DragSourceEvent event) {
			IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
			if(selection.size() != 1){
				event.doit = false;
			}else{
				Object object = selection.getFirstElement();
				if(object instanceof ToolEntryEditPart){
					ToolEntryEditPart toolEntryEditPart = (ToolEntryEditPart)object;
					Object object2 = toolEntryEditPart.getModel();
					if(object2 instanceof EvPaletteCreationEntry){
						EvPaletteCreationEntry evPaletteCreationEntry = (EvPaletteCreationEntry)object2;
						if(evPaletteCreationEntry.getTemplate().equals(((EvEditor)_designPage.getEditor()).getWidgetId())){
							event.doit = false;
						}else{
							super.dragStart(event);
						}
					}
				}
			}
		}
	}
}
