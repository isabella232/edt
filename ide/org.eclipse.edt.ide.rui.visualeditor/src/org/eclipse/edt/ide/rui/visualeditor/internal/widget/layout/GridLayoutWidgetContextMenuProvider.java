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

import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.rui.document.utils.GridLayoutOperation;
import org.eclipse.edt.ide.rui.server.EvEditorProvider;
import org.eclipse.edt.ide.rui.visualeditor.internal.actions.EvActionWidgetMove;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvDesignOverlay;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvSourceOperation;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvWidgetContextMenuProvider;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;
import org.eclipse.edt.ide.rui.visualeditor.plugin.Activator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.actions.ActionFactory;


public class GridLayoutWidgetContextMenuProvider implements EvWidgetContextMenuProvider {
	private static final String GRIDLAYOUT_CONTEXTMENU_PREFIX = "GRIDLAYOUT_CONTEXTMENU_PREFIX";
	
	private static final String INSERT = "INSERT";
	private static final String INSERT_ROW_ABOVE = "INSERT_ROW_ABOVE";
	private static final String INSERT_ROW_AFTER = "INSERT_ROW_AFTER";
	private static final String INSERT_COLUMN_LEFT = "INSERT_COLUMN_LEFT";
	private static final String INSERT_COLUMN_RIGHT = "INSERT_COLUMN_RIGHT";
	private static final String DELETE = "DELETE";
	private static final String DELETE_ROW = "DELETE_ROW";
	private static final String DELETE_COLUMN = "DELETE_COLUMN";	
	
	public void refreshContextMenu(WidgetPart selectedWidget, MenuManager manager, EvDesignOverlay designOverlay, Point mousePosition) {
		while(manager.findMenuUsingPath(GRIDLAYOUT_CONTEXTMENU_PREFIX + INSERT) != null){
			manager.remove( GRIDLAYOUT_CONTEXTMENU_PREFIX + INSERT );
		}
		while(manager.findMenuUsingPath(GRIDLAYOUT_CONTEXTMENU_PREFIX + DELETE) != null){
			manager.remove( GRIDLAYOUT_CONTEXTMENU_PREFIX + DELETE );
		}
		
		if ( !(selectedWidget != null && "org.eclipse.edt.rui.widgets".equals( selectedWidget.getPackageName() ) && "GridLayout".equals( selectedWidget.getTypeName() )) ) {
			return;
		}
		
		if ( manager.getSize() >= 1 && !manager.getItems()[manager.getSize() - 1].isSeparator() ) {
			manager.add( new Separator() );
		}
		
		String extrainfo = selectedWidget.getExtraInfo( "LayoutInfo" );
		if ( extrainfo == null || extrainfo.length() == 0 ) {
			return;
		}
		String[] tdInfo = extrainfo.split(":");
		int row = -1, column = -1;
		try {
			int index = 0;
			int[] cellInfo = new int[7];
			for ( int i = 0; i < tdInfo.length; i ++ ) {
				index = i % 7;
				cellInfo[index] = Integer.parseInt( tdInfo[i] );
				if ( index == 6 && cellInfo[2] < mousePosition.x && cellInfo[3] < mousePosition.y && cellInfo[4] > mousePosition.x - cellInfo[2] && cellInfo[5] > mousePosition.y - cellInfo[3] ) {
					row = cellInfo[0];
					column = cellInfo[1];
					break;
				}
			}
		} catch ( Exception e ) {
			Activator.getDefault().getLog().log(new Status(Status.ERROR,Activator.PLUGIN_ID,"GridLayoutWidgetContextMenuProvider: Error parse LayoutInfo",e));
		}
		
		if ( row >= 0 && column >= 0 ) {
			// remove delete and move from menu if select a cell
			IContributionItem[] contributionItems = manager.getItems();
			for(int i=0; i<contributionItems.length; i++){
				IContributionItem contributionItem = contributionItems[i];
				if(contributionItem instanceof ActionContributionItem){
					ActionContributionItem actionContributionItem = (ActionContributionItem)contributionItem;
					String id = actionContributionItem.getId();
					if(ActionFactory.DELETE.getId().equals(id) || EvActionWidgetMove.ID.equals(id)){
						manager.remove(actionContributionItem);
					}
				}
			}
			
			
			GridLayoutWidgetContextMenuAction _actionMenu;
			//insert		
			MenuManager subMenuInsert = new MenuManager(Messages.NL_GLWCMA_Insert, null, GRIDLAYOUT_CONTEXTMENU_PREFIX + INSERT);
			_actionMenu = new GridLayoutWidgetContextMenuAction( selectedWidget, designOverlay, row, column, GridLayoutOperation.ACTION_INSERT_ROW_AHEAD );
			_actionMenu.setText( Messages.NL_GLWCMA_Insert_Row_Above );
			_actionMenu.setId( GRIDLAYOUT_CONTEXTMENU_PREFIX + INSERT_ROW_ABOVE );
			subMenuInsert.add(_actionMenu);
			_actionMenu = new GridLayoutWidgetContextMenuAction( selectedWidget, designOverlay, row, column, GridLayoutOperation.ACTION_INSERT_ROW_AFTER );
			_actionMenu.setText( Messages.NL_GLWCMA_Insert_Row_Below );
			_actionMenu.setId( GRIDLAYOUT_CONTEXTMENU_PREFIX + INSERT_ROW_AFTER );
			subMenuInsert.add(_actionMenu);
			_actionMenu = new GridLayoutWidgetContextMenuAction( selectedWidget, designOverlay, row, column, GridLayoutOperation.ACTION_INSERT_COLUMN_AHEAD );
			_actionMenu.setText( Messages.NL_GLWCMA_Insert_Column_To_The_Left );
			_actionMenu.setId( GRIDLAYOUT_CONTEXTMENU_PREFIX + INSERT_COLUMN_LEFT );
			subMenuInsert.add(_actionMenu);
			manager.add( subMenuInsert );
			_actionMenu = new GridLayoutWidgetContextMenuAction( selectedWidget, designOverlay, row, column, GridLayoutOperation.ACTION_INSERT_COLUMN_AFTER );
			_actionMenu.setText( Messages.NL_GLWCMA_Insert_Column_To_The_Right );
			_actionMenu.setId( GRIDLAYOUT_CONTEXTMENU_PREFIX + INSERT_COLUMN_RIGHT );
			subMenuInsert.add(_actionMenu);
			manager.add( subMenuInsert );

			//delete
			MenuManager subMenuDelete = new MenuManager(Messages.NL_GLWCMA_Delete, null, GRIDLAYOUT_CONTEXTMENU_PREFIX + DELETE);
			_actionMenu = new GridLayoutWidgetContextMenuAction( selectedWidget, designOverlay, row, column, GridLayoutOperation.ACTION_DELETE_ROW );
			_actionMenu.setText( Messages.NL_GLWCMA_Delete_Row );
			_actionMenu.setId( GRIDLAYOUT_CONTEXTMENU_PREFIX + DELETE_ROW );
			subMenuDelete.add(_actionMenu);
			_actionMenu = new GridLayoutWidgetContextMenuAction( selectedWidget, designOverlay, row, column, GridLayoutOperation.ACTION_DELETE_COLUMN );
			_actionMenu.setText( Messages.NL_GLWCMA_Delete_Column );
			_actionMenu.setId( GRIDLAYOUT_CONTEXTMENU_PREFIX + DELETE_COLUMN );
			subMenuDelete.add(_actionMenu);
			manager.add( subMenuDelete );
			
			_actionMenu = null;
		}
	}

	

}

class GridLayoutWidgetContextMenuAction extends Action {

	protected EvDesignOverlay	_designOverlay;
	protected WidgetPart 		_selectedWidget;
	protected int				_actionID;
	protected int 				_row;
	protected int 				_column;

	/**
	 * 
	 */
	public GridLayoutWidgetContextMenuAction( WidgetPart selectedWidget, EvDesignOverlay designOverlay, int row, int column, int actionID ){
		_selectedWidget = selectedWidget;
		_designOverlay = designOverlay;
		_actionID = actionID;
		_row = row;
		_column = column;
	}
	
	/**
	 * Declared in IAction.  
	 */
	public void run() {
		_designOverlay.doSourceOperation( new EvSourceOperation(){
			public void doSourceOperation( EvEditorProvider editorProvider ) {
				editorProvider.gridlayoutOpertion(_selectedWidget.getStatementOffset(), _selectedWidget.getStatementLength(), _row, _column, _actionID );
				_designOverlay.resetMouseDownPoint();
			}
		});

	}
}
