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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.rui.document.utils.IVEConstants;
import org.eclipse.edt.ide.rui.visualeditor.internal.actions.EvActionWidgetDelete;
import org.eclipse.edt.ide.rui.visualeditor.internal.actions.EvActionWidgetMove;
import org.eclipse.edt.ide.rui.visualeditor.internal.actions.EvActionWidgetProperties;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.edt.ide.rui.visualeditor.internal.preferences.EvPreferences;
import org.eclipse.edt.ide.rui.visualeditor.internal.properties.PropertyChange;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.ColorUtil;
import org.eclipse.edt.ide.rui.visualeditor.internal.views.dataview.model.PageDataNode;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetDescriptor;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetDescriptorRegistry;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetManager;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPropertyDescriptor;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPropertyValue;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.layout.RootWidgetLayout;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.layout.WidgetLayout;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.layout.WidgetLayoutRegistry;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.EvInsertWidgetWizardDialog;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.InsertWidgetWizard;
import org.eclipse.edt.ide.rui.visualeditor.plugin.Activator;
import org.eclipse.gef.dnd.TemplateTransfer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;


/**
 * The overlay sits on top of the browser widget within the design area of the design page.
 * Its role is to draw widget selection indicators and handle drag/drop and mouse events.
 */
public class EvDesignOverlay extends Composite implements DisposeListener, DropTargetListener, FocusListener, IMenuListener, KeyListener, MouseListener, MouseMoveListener, MouseTrackListener, PaintListener, TraverseListener {

	public final static int					DROP_THICKNESS				= 5;
	
	protected Cursor						_cursorWait					= null;
	protected EvActionWidgetDelete			_actionWidgetDelete			= null;
	protected EvActionWidgetMove			_actionWidgetMove			= null;
	protected EvActionWidgetProperties		_actionWidgetProperties		= null;
	protected boolean						_bShowInstructions			= false;
	protected Color							_colorDropTargetPotential	= null;
	protected Color							_colorDropTargetSelected	= null;
	protected Color							_colorWidgetSelected		= null;
	protected EvDesignOverlayDropLocation	_dropLocation				= null;
	protected Image							_imageDoubleBuffer			= null;
	protected int							_iTransparencyAmount		= EvConstants.PREFERENCE_DEFAULT_SEMITRANSPARENCY_AMOUNT;
	protected int							_iTransparencyMode			= EvConstants.PREFERENCE_DEFAULT_SEMITRANSPARENCY_MODE;
	protected Collection					_listDropLocations			= null;
	protected ArrayList						_listSelectionHierarchy		= new ArrayList();										// A list of integers reflecting the selection hierarchy
	protected EvDesignPage					_pageDesign					= null;
	protected EvDesignOverlayPainter		_painter					= null;
	protected Point							_ptDragOffset				= new Point( 0, 0 );
	protected Collection					_ptDropLocations			= null;
	protected Point							_ptMouse					= new Point( 0, 0 );
	protected Point							_ptMouseDown				= new Point( 0, 0 );
	protected WidgetPart					_widgetDragging				= null;													// A widget that is being dragged
	protected WidgetPart					_widgetDropLocation			= null;													// A widget that is being dragged
	protected WidgetPart					_widgetMouseDown			= null;													// The widget under the mouse during mouse down
	protected WidgetPart					_widgetNextSelection		= null;													// The widget that indicates it will be selected if a person clicks on it.
	protected WidgetPart					_widgetSelected				= null;													// Temporary for single selection only.
	protected Collection                    _listClickableAreas          = new ArrayList();
	protected MenuManager 					_menuManager				= null;
	protected Point							_mouseDownPoint				= null;
	
	/**
	 * Constructor. 
	 */
	public EvDesignOverlay( Composite compositeParent, EvDesignPage pageDesign ) {
		super( compositeParent, pageDesign._bGraphicsTransparencyAvailable == true ? SWT.TRANSPARENT : SWT.NO_BACKGROUND );

		_pageDesign = pageDesign;

		_painter = new EvDesignOverlayPainter( this );

		// Initialize colors
		//------------------
		updateColors();

		// Initialize transparency
		//------------------------
		initializeTransparency();

		// Build context menu
		//-------------------
		createContextMenu();

		// Listeners
		//----------
		addDisposeListener( this );
		addDropSupport();
		addFocusListener( this );
		addKeyListener( this );
		addMouseListener( this );
		addMouseMoveListener( this );
		addMouseTrackListener( this );
		addPaintListener( this );
		addTraverseListener( this );

		EvHelp.setHelp( this, EvHelp.DESIGN_AREA );
		EvHelp.setHelp( compositeParent, EvHelp.DESIGN_AREA );
		EvHelp.setHelp( compositeParent.getParent(), EvHelp.DESIGN_AREA );
	}

	/**
	 * Adds support for dropping items onto this composite.
	 * @see org.eclipse.swt.dnd.DND
	 */
	protected void addDropSupport() {
		int iOperations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;

		DropTarget dropTarget = new DropTarget( this, iOperations );

		Transfer[] types = new Transfer[] { TemplateTransfer.getInstance() };

		dropTarget.setTransfer( types );
		dropTarget.addDropListener( this );
	}

	/**
	 * Creates the pop-up context menu for the design area
	 */
	protected void createContextMenu() {
		_menuManager = new MenuManager( EvConstants.DESIGN_AREA_CONTEXT_MENU_ID, EvConstants.DESIGN_AREA_CONTEXT_MENU_ID );
		_menuManager.addMenuListener( this );
		Menu menu = _menuManager.createContextMenu( this );
		setMenu( menu );

		IWorkbenchPart workbenchPart = _pageDesign.getEditor().getSite().getPart();

		// Undo
		//-----
		IAction actionUndo = _pageDesign.getEditor().getPageSource().getAction( ActionFactory.UNDO.getId() );
		_menuManager.add( actionUndo );

		// Redo
		//-----
		IAction actionRedo = _pageDesign.getEditor().getPageSource().getAction( ActionFactory.REDO.getId() );
		_menuManager.add( actionRedo );
		
		_menuManager.add( new Separator() );

		// Save
		//-----
		IWorkbenchAction actionWorkbenchSave = ActionFactory.SAVE.create( workbenchPart.getSite().getWorkbenchWindow() );
		_menuManager.add( actionWorkbenchSave );

		_menuManager.add( new Separator() );

		// Delete widget
		//--------------
		IWorkbenchAction actionWorkbenchDelete = ActionFactory.DELETE.create( workbenchPart.getSite().getWorkbenchWindow() );
		_actionWidgetDelete = new EvActionWidgetDelete( this );
		_actionWidgetDelete.setId(actionWorkbenchDelete.getId());
		_actionWidgetDelete.setText( actionWorkbenchDelete.getText() );
		_actionWidgetDelete.setAccelerator( actionWorkbenchDelete.getAccelerator() );
		_actionWidgetDelete.setImageDescriptor( actionWorkbenchDelete.getImageDescriptor() );
		_actionWidgetDelete.setDescription( actionWorkbenchDelete.getDescription() );
		_actionWidgetDelete.setDisabledImageDescriptor( actionWorkbenchDelete.getDisabledImageDescriptor() );
		_menuManager.add( _actionWidgetDelete );

		// Widget move
		//------------
		_actionWidgetMove = new EvActionWidgetMove( workbenchPart, this );
		_actionWidgetMove.setId(EvActionWidgetMove.ID);
		_actionWidgetMove.setText( Messages.NL_Move );
		_menuManager.add( _actionWidgetMove );

		_menuManager.add( new Separator() );

		// Widget properties
		//------------------
		IWorkbenchAction actionWorkbenchProperties = ActionFactory.PROPERTIES.create( workbenchPart.getSite().getWorkbenchWindow() );
		_actionWidgetProperties = new EvActionWidgetProperties( this );
		_actionWidgetProperties.setId(ActionFactory.PROPERTIES.getId());
		_actionWidgetProperties.setText( actionWorkbenchProperties.getText() );
		_actionWidgetProperties.setAccelerator( actionWorkbenchProperties.getAccelerator() );
		_actionWidgetProperties.setImageDescriptor( actionWorkbenchProperties.getImageDescriptor() );
		_actionWidgetProperties.setDescription( actionWorkbenchProperties.getDescription() );
		_actionWidgetProperties.setDisabledImageDescriptor( actionWorkbenchProperties.getDisabledImageDescriptor() );
		_menuManager.add( _actionWidgetProperties );
	}

	protected void refreshMenu() {
		IContributionItem deleteContributionItem = _menuManager.find(ActionFactory.DELETE.getId());
		if(deleteContributionItem == null){
			_menuManager.insertBefore(ActionFactory.PROPERTIES.getId(), _actionWidgetDelete);
		}
		
		IContributionItem removeContributionItem = _menuManager.find(EvActionWidgetMove.ID);
		if(removeContributionItem == null){
			_menuManager.insertBefore(ActionFactory.PROPERTIES.getId(), _actionWidgetMove);
			_menuManager.insertBefore(ActionFactory.PROPERTIES.getId(), new Separator() );
		}
			
		
		Collection<EvWidgetContextMenuProvider>	_widgetContextMenuProviders = EvWidgetContextMenuProviderRegister.getInstance().getEvWidgetContextMenuProviders().values();
		Iterator<EvWidgetContextMenuProvider> iterator = _widgetContextMenuProviders.iterator();
		while(iterator.hasNext()){
			iterator.next().refreshContextMenu(_widgetSelected, _menuManager, this, _mouseDownPoint);
		}
		
	}

	/**
	 * Deletes the selected widget
	 */
	public void deleteSelected() {
		if( _widgetSelected != null ) {
			if( _widgetNextSelection == _widgetSelected )
				_widgetNextSelection = null;
			
			if(!isCellInGridLayout(_widgetSelected)){
				doOperationWidgetDelete( _widgetSelected );
				redraw();
			}
		}
	}
	
	private boolean isCellInGridLayout(WidgetPart _widgetSelected){
		if ( _widgetSelected == null || !( "org.eclipse.edt.rui.widgets".equals( _widgetSelected.getPackageName() ) && "GridLayout".equals( _widgetSelected.getTypeName() )) ) {
			return false;
		}
		
		String extrainfo = _widgetSelected.getExtraInfo( "LayoutInfo" );
		if ( extrainfo == null || extrainfo.length() == 0 ) {
			return false;
		}
		
		String[] tdInfo = extrainfo.split(":");
		int row = -1, column = -1;
		try {
			int index = 0;
			int[] cellInfo = new int[7];
			for ( int i = 0; i < tdInfo.length; i ++ ) {
				index = i % 7;
				cellInfo[index] = Integer.parseInt( tdInfo[i] );
				if(_mouseDownPoint != null){
					if ( index == 6 && cellInfo[2] < _mouseDownPoint.x && cellInfo[3] < _mouseDownPoint.y && cellInfo[4] > _mouseDownPoint.x - cellInfo[2] && cellInfo[5] > _mouseDownPoint.y - cellInfo[3] ) {
						row = cellInfo[0];
						column = cellInfo[1];
						break;
					}
				}
				
			}
		} catch ( Exception e ) {
			Activator.getDefault().getLog().log(new Status(Status.ERROR,Activator.PLUGIN_ID,"GridLayoutWidgetContextMenuProvider: Error parse LayoutInfo",e));
		}
		if ( row >= 0 && column >= 0 ) {
			return true;
		}else{
			return false;
		}
	}

	/**
	 * Creates a widget at the specified drop location.  The selection hierarchy of the new
	 * widget is determined and remembered.  The selection hierarchy is restored after receiving
	 * a new set of widgets.
	 */
	public void doOperationWidgetCreate( WidgetDescriptor descriptor, EvDesignOverlayDropLocation location ) {
		if( _cursorWait == null )
			_cursorWait = new Cursor( getDisplay(), SWT.CURSOR_WAIT );

		setCursor( _cursorWait );

		rememberSelectionHierarchyForOperation( location.widgetParent, location.iIndex );
		_pageDesign.doOperationWidgetCreate( descriptor, location );

		setCursor( null );
	}

	/**
	 * Deletes a widget 
	 */
	public void doOperationWidgetDelete( WidgetPart widget ) {
		if( _cursorWait == null )
			_cursorWait = new Cursor( getDisplay(), SWT.CURSOR_WAIT );
		
		setCursor( _cursorWait );

		rememberSelectionHierarchyForOperationDelete( widget );
		_pageDesign.doOperationWidgetDelete( widget );

		setCursor( null );
	}

	/**
	 * Moves the widget to the specified drop location.  The selection hierarchy of the new widget
	 * location is determined and remembered.  The selection hierarchy is restored after receiving
	 * a new set of widgets.
	 */
	public void doOperationWidgetMove( WidgetPart widget, EvDesignOverlayDropLocation location ) {
		if( _cursorWait == null )
			_cursorWait = new Cursor( getDisplay(), SWT.CURSOR_WAIT );

		setCursor( _cursorWait );
		
		rememberSelectionHierarchyForOperation( location.widgetParent, location.iIndex );
		_pageDesign.doOperationWidgetMove( widget, location );
		
		setCursor( null );
	}

	/**
	 * Notifies the design page of a set of property changes.
	 */
	protected void doOperationWidgetPropertyValueChanges( List listPropertyChanges ) {
		if( _cursorWait == null )
			_cursorWait = new Cursor( getDisplay(), SWT.CURSOR_WAIT );

		setCursor( _cursorWait );
		_pageDesign.doOperationWidgetPropertyValueChanges( listPropertyChanges );
				
		setCursor( null );
	}

	/**
	 * Notifies the design page of a click event.
	 */
	protected void doOperationWidgetOnclick( EvDesignOverylayClickableArea area ) {
		try {
			int index = Integer.parseInt( area.propertyValue );
			rememberSelectionHierarchyForOperation( area.widget, index - 1 );
		} catch ( Exception e ) {
			//needn't do anything
		}
		_pageDesign.doOperationWidgetOnclick( area.widget, area.propertyName, area.propertyValue );
	}
	
	public void doSourceOperation(EvSourceOperation operation) {
		if( _widgetSelected != null ) {
			if( _widgetNextSelection == _widgetSelected )
				_widgetNextSelection = null;

			if( _cursorWait == null )
				_cursorWait = new Cursor( getDisplay(), SWT.CURSOR_WAIT );

			setCursor( _cursorWait );

			_pageDesign.doSourceOperation( operation );

			setCursor( null );

			redraw();
		}
	}

	/**
	 * Declared in DragDropListener.  Checks for a GEF transfer template.
	 * If found, the drop locations are shown.
	 */
	public void dragEnter( DropTargetEvent event ) {
		// Do nothing if there is no RUI handler
		//-------------------------------------------------------------------
		EvEditor editor = _pageDesign.getEditor();
		boolean bAllowDrop = editor.isRuiHandler() == true;
		if( bAllowDrop == false )
			return;

		DropTarget dropTarget = (DropTarget)event.getSource();
		Transfer[] transfer = dropTarget.getTransfer();
		
		if( transfer.length == 0 )
			return;

		if( transfer[ 0 ] instanceof TemplateTransfer == false )
			return;

		// Check for GEF template transfer
		//--------------------------------
		TemplateTransfer templateTransfer = (TemplateTransfer)transfer[ 0 ];
		
		if( templateTransfer.getObject() instanceof String == false )
			return;

		// Turn on auto scrolling
		//-----------------------
		_pageDesign.getScroller().activate();

		return;
	}

	/**
	 * Declared in DragDropListener
	 */
	public void dragLeave( DropTargetEvent event ) {
		// Do nothing if there is no RUI handler
		//-------------------------------------------------------------------
		EvEditor editor = _pageDesign.getEditor();
		boolean bAllowDrop = editor.isRuiHandler() == true;
		if( bAllowDrop == false )
			return;

		_ptDropLocations = null;
		_listDropLocations = null;

		_pageDesign.getScroller().deactivate();

		// Do not nullify the drop location
		// This leave method is called just before the drop method
		//--------------------------------------------------------		
		redraw();
	}

	/**
	 * Declared in DragDropListener.  Does nothing.
	 */
	public void dragOperationChanged( DropTargetEvent event ) {
	}

	/**
	 * Declared in DragDropListener
	 */
	public void dragOver( DropTargetEvent event ) {
		// Do nothing if there is no RUI handler
		//-------------------------------------------------------------------
		EvEditor editor = _pageDesign.getEditor();
		boolean bAllowDrop = editor.isRuiHandler() == true;
		if( bAllowDrop == false )
			return;

		// Compute the drop locations
		// Use the display's mouse position instead of the event x and y
		// to work around chinese linux problem
		//--------------------------------------------------------------
		Point ptMouse  = Display.getCurrent().getCursorLocation();
		ptMouse = toControl( ptMouse );
		
		// Auto scroll the design area
		//----------------------------
		_pageDesign.getScroller().autoScroll( ptMouse.x, ptMouse.y );

		setupDropLocations( ptMouse.x, ptMouse.y );
		setupNextDropLocation( ptMouse.x, ptMouse.y );
		redraw();
	}

	/**
	 * Declared in DragDropListener.
	 * A new widget instance is created at the drop location.
	 */
	public void drop( DropTargetEvent event ) {
		// Do nothing if there is no RUI handler
		//-------------------------------------------------------------------
		EvEditor editor = _pageDesign.getEditor();
		boolean bAllowDrop = editor.isRuiHandler() == true;
		if( bAllowDrop == false )
			return;

		// Create a new widget
		// We receive an ID which is a concatenation of:
		//    widget project name, a separator, widget type
		//-------------------------------------------------
		if (  _dropLocation != null ) {
			if( event.data instanceof String == true ) {
				String strWidgetID = event.data.toString();
				WidgetDescriptor descriptor = WidgetDescriptorRegistry.getInstance(this.getDesignPage().getEditor().getProject()).getDescriptor( strWidgetID );
				doOperationWidgetCreate( descriptor, _dropLocation );
			} else if( event.data instanceof PageDataNode){
				PageDataNode pageDataNode = (PageDataNode)event.data;
				InsertWidgetWizard insertWidgetWizard = new InsertWidgetWizard(pageDataNode, this, _dropLocation);
				Shell shell = Display.getCurrent().getActiveShell();
				EvInsertWidgetWizardDialog evInsertWidgetWizardDialog = new EvInsertWidgetWizardDialog(shell, insertWidgetWizard);
				evInsertWidgetWizardDialog.setPageSize(800,400);
				evInsertWidgetWizardDialog.open();
			}
		}
		_ptDropLocations = null;
		_listDropLocations = null;
		_dropLocation = null;
		_widgetDropLocation = null;

		redraw();
	}

	/**
	 * Declared in DragDropListener
	 */
	public void dropAccept( DropTargetEvent event ) {
	}

	/**
	 * Ends a widget move operation.  Either a mouse up, or an enter key press has occurred.
	 */
	protected void endWidgetMove(){
		
		// Moving a statement
		//-------------------
		if( _dropLocation != null ) {
			doOperationWidgetMove( _widgetDragging, _dropLocation );
		}				
		
		// Non-static positioning when dragging a widget
		// If a widget being dragged has position that is absolute
		// we assume that the user wants to relocate
		// the widget position and not the widget statement.
		// Determine whether to move the statement or the widget position
		//---------------------------------------------------------------
		WidgetPropertyValue positionValue = _pageDesign.getEditor().getPropertyValue( _widgetDragging, "position", IVEConstants.STRING_TYPE );
		
		ArrayList listValues = null;
		
		if( positionValue != null )
			listValues = positionValue.getValues();
		
		String strType = null;

		if( listValues != null && listValues.size() > 0 )
			strType = (String)listValues.get( 0 );
		
		if( strType != null ) {
			// Absolute: Relative to the top /right corner of the web page
			// Relative: Relative to where the widget would normally be positioned
			//--------------------------------------------------------------------
			if( strType.equalsIgnoreCase( "absolute" ) || strType.equalsIgnoreCase( "fixed" ) || strType.equalsIgnoreCase( "relative" ) ) {

				// Modify the x and y widget properties
				//-------------------------------------
				WidgetDescriptorRegistry registry = WidgetDescriptorRegistry.getInstance(this.getDesignPage().getEditor().getProject());
				WidgetDescriptor widgetDescriptor = registry.getDescriptor( _widgetDragging.getTypeID() );

				if( widgetDescriptor != null ) {
					
					// Obtain the x property value
					//----------------------------
					String strOldX = null;

					WidgetPropertyDescriptor descriptorX = widgetDescriptor.getPropertyDescriptor( "x" );
					WidgetPropertyValue valueOldX = _pageDesign.getEditor().getPropertyValue( _widgetDragging, "x", IVEConstants.INTEGER_TYPE );

					ArrayList listX = null;
					if( valueOldX != null )
						listX = valueOldX.getValues();

					if( listX != null && listX.size() > 0 )
						strOldX = (String)listX.get( 0 );

					// Obtain the y property value
					//----------------------------
					String strOldY = null;

					WidgetPropertyDescriptor descriptorY = widgetDescriptor.getPropertyDescriptor( "y" );
					WidgetPropertyValue valueOldY = _pageDesign.getEditor().getPropertyValue( _widgetDragging, "y", IVEConstants.INTEGER_TYPE );

					ArrayList listY = null;
					if( valueOldY != null )
						listY = valueOldY.getValues();

					if( listY != null && listY.size() > 0 )
						strOldY = (String)listY.get( 0 );

					int iX = 0;
					int iY = 0;

					// For "fixed", we use the mouse position
					// Fixed is relative to the browser window independent of scrolling
					//-----------------------------------------------------------------
					if( strType.equalsIgnoreCase( "fixed" ) == true ) {
						Rectangle rectDragging = _widgetDragging.getBoundsDragging();
						iX = rectDragging.x;
						iY = rectDragging.y;
					}

					// For "relative" and "absolute" we can compute
					// the delta relative to where the widget was before
					// Relative is relative to its normal position
					// Absolute is relative to its container
					//--------------------------------------------------
					else {
						Point ptWidget = _widgetDragging.getBoundsOrigin();
						Rectangle rectDragging = _widgetDragging.getBoundsDragging();
						
						int iDeltaX = rectDragging.x - ptWidget.x;
						int iDeltaY = rectDragging.y - ptWidget.y;

						int iOldX = 0;
						int iOldY = 0;

						if( strOldX != null ) {
							try {
								iOldX = Integer.valueOf( strOldX ).intValue();
							}
							catch( NumberFormatException ex ) {
							}
						}

						if( strOldY != null ) {
							try {
								iOldY = Integer.valueOf( strOldY ).intValue();
							}
							catch( NumberFormatException ex ) {
							}
						}

						iX = iOldX + iDeltaX;
						iY = iOldY + iDeltaY;
					}

					// Create a list of changes for one undo operation
					//------------------------------------------------
					ArrayList listPropertyChanges = new ArrayList();
					
					// x
					//--
					WidgetPropertyValue valueNew = new WidgetPropertyValue( Integer.toString( iX ) );
					PropertyChange change = new PropertyChange();
					change.strPropertyID = descriptorX.getID();
					change.strPropertyType = descriptorX.getType();
					change.valueNew = valueNew;
					change.valueOld = valueOldX;
					change.widget = _widgetDragging;
					listPropertyChanges.add( change );

					// y
					//--
					valueNew = new WidgetPropertyValue( Integer.toString( iY ) );
					change = new PropertyChange();
					change.strPropertyID = descriptorY.getID();
					change.strPropertyType = descriptorY.getType();
					change.valueNew = valueNew;
					change.valueOld = valueOldY;
					change.widget = _widgetDragging;
					listPropertyChanges.add( change );
					
					doOperationWidgetPropertyValueChanges( listPropertyChanges );					
				}
			}
		}
	}
	
	/**
	 * For a given mouse coordinate, finds the smallest parent part (Box, VBox, HBox).
	 */
	protected WidgetPart findDropLocationWidget( int iX, int iY ) {

		WidgetManager widgetManager = _pageDesign.getWidgetManager();
		if( widgetManager == null )
			return null;

		WidgetPart widgetRoot = widgetManager.getWidgetRoot();
		
		if( widgetManager.getWidgetCount() == 0 )
			return widgetRoot;

		// Do the root children
		//---------------------
		WidgetPart widgetParent = findDropLocationWidgetRecursive( widgetRoot, iX, iY );

		return widgetParent != null ? widgetParent : widgetRoot;
	}

	/**
	 * For a given mouse coordinate, finds the lowest child in the widget hierarchy that contains the coordinate.
	 */
	protected WidgetPart findDropLocationWidgetRecursive( WidgetPart widget, int iX, int iY ) {

		if( widget == _widgetDragging )
			return null;

		WidgetPart widgetOut = null;

		// Recursively do the children first
		//----------------------------------
		List listChildren = widget.getChildren();

		for( int i = 0; i < listChildren.size(); ++i ) {
			WidgetPart widgetChild = (WidgetPart)listChildren.get( i );
			widgetOut = findDropLocationWidgetRecursive( widgetChild, iX, iY );
			if( widgetOut != null )
				return widgetOut;
		}

		// A child was not found to contain the point.
		// See if the point is within the widget itself
		//---------------------------------------------
		if( widget.getBounds().contains( iX, iY ) == true )
			return widget;

		return null;
	}

	/**
	 * Returns the next widget in the list, or the current one if there are no more, or
	 * the first one if the specified widget is not in the list.
	 */
	protected WidgetPart findNextWidgetInList( WidgetPart widget, Collection listWidgets ) {

		// Find the currently selected widget in the list
		//-----------------------------------------------
		int iIndex = -1;

		WidgetPart[] widgets = new WidgetPart[listWidgets.size()];
		System.arraycopy( listWidgets.toArray(), 0, widgets, 0, listWidgets.size() );

		for( int i = 0; i < widgets.length; ++i ) {
			if( widgets[ i ] == widget ) {
				iIndex = i;
				break;
			}
		}

		// The widget is not found in the list
		// Select the first one in the list
		//------------------------------------
		if( iIndex == -1 )
			return widgets[ 0 ];

		// The widget is found in the list
		// Select the next one in the list
		// Wrap around if exceeded size
		//--------------------------------
		else if( iIndex >= 0 ) {
			if( ++iIndex >= widgets.length )
				iIndex = 0;

			return widgets[ iIndex ];
		}

		// There are no more widgets in the list
		// Return the original widget
		//--------------------------------------
		return widget;
	}


	/**
	 * Declared in FocusListener.
	 * Notifies the design page so it can paint the focus indicator.
	 */
	public void focusGained( FocusEvent e ) {
		_pageDesign.overlayFocusChanged( true );
		redraw();
	}

	/**
	 * Declared in FocusListener. 
	 * Notifies the design page so it can paint the focus indicator.
	 */
	public void focusLost( FocusEvent e ) {
		resetMouseDownPoint();
		_pageDesign.overlayFocusChanged( false );
		redraw();
	}

	/**
	 * 
	 */
	public IAction getAction( String strActionId ) {
		if( strActionId.equals( ActionFactory.DELETE.getId() ) == true )
			return _actionWidgetDelete;

		if( strActionId.equals( ActionFactory.PROPERTIES.getId() ) == true )
			return _actionWidgetProperties;

		return null;
	}

	/**
	 * Returns the parent design page. 
	 */
	public EvDesignPage getDesignPage(){
		return _pageDesign;
	}

	/**
	 * Returns the entire list of widgets sorted by statement location.
	 */
	protected Iterator getWidgets() {
		WidgetManager widgetManager = _pageDesign.getWidgetManager();
		return widgetManager.getWidgetList().iterator();
	}

	/**
	 * Returns the widget parts under the mouse position
	 */
	protected List getWidgetsAtPoint( Point point ) {
		WidgetManager widgetManager = _pageDesign.getWidgetManager();
		return widgetManager.getWidgets( point );
	}

	/**
	 * 
	 */
	public WidgetPart getWidgetSelected() {
		return _widgetSelected;
	}

	/**
	 * Called by the move action when a person selects the context menu "Move" menu item. 
	 */
	public void initializeMoveWidgetWithKeyboard(){
		_widgetMouseDown = _widgetSelected;
		_widgetDragging = _widgetSelected;
		_ptDragOffset.x = 0;
		_ptDragOffset.y = 0;
		
		Rectangle rectDragging = _widgetSelected.getBounds(); // The call returns a new Rectangle
		_widgetSelected.setBoundsDragging( rectDragging );
		
		mouseMoved( rectDragging.x + 8, rectDragging.y + 8 );
	}
	
	/**
	 * Initializes the overlay's transparency.
	 */
	protected void initializeTransparency() {
		int iTransparencyMode = EvPreferences.getInt( EvConstants.PREFERENCE_SEMITRANSPARENCY_MODE );
		int iTransparencyAmount = EvPreferences.getInt( EvConstants.PREFERENCE_SEMITRANSPARENCY_AMOUNT );
		setTransparency( iTransparencyMode, iTransparencyAmount );
	}

	
	/**
	 * Returns whether a widget type is a container.
	 */
	protected boolean isContainer( WidgetPart widget ){
		// Legacy support for widgets prior to 1.0.2
		if( widget.getTypeName().equalsIgnoreCase( "div" ) || widget.getTypeName().equalsIgnoreCase( "span" ) || widget.getTypeName().equalsIgnoreCase( "grouping" ) || widget.getTypeName().equalsIgnoreCase( "treenode" ) || widget.getTypeName().equalsIgnoreCase( "tree" ) ){
			return true;
		}
		
		WidgetDescriptor descriptor = WidgetDescriptorRegistry.getInstance(_pageDesign.getEditor().getProject()).getDescriptor( widget.getTypeID() );
		if(descriptor != null){
			return descriptor.isContainer();
		}
		return false;
	}
	
	/**
	 * Returns whether a widget type can be moved to a new location. 
	 */
	public boolean isDraggable( String strType ){
		if( strType.equalsIgnoreCase( "DojoTreeNode" ) )
			return false;
		
		return true;
	}
	
	/**
	 * Declared in KeyListener.  Does nothing.
	 */
	public void keyPressed( KeyEvent event ) {
		if( _widgetDragging == null )
			return;
		
		if( event.keyCode == SWT.ARROW_DOWN ){
			Rectangle rectDragging = _widgetDragging.getBoundsDragging();
			Rectangle rectMove = new Rectangle( rectDragging.x, rectDragging.y + 8, 8, 8 );
			mouseMoved( rectMove.x, rectMove.y );
			showRectangle( rectMove );
		}

		if( event.keyCode == SWT.ARROW_LEFT ){
			Rectangle rectDragging = _widgetDragging.getBoundsDragging();
			Rectangle rectMove = new Rectangle( rectDragging.x - 8, rectDragging.y, 8, 8 );
			mouseMoved( rectMove.x, rectMove.y );
			showRectangle( rectMove );
		}

		if( event.keyCode == SWT.ARROW_RIGHT ){
			Rectangle rectDragging = _widgetDragging.getBoundsDragging();
			Rectangle rectMove = new Rectangle( rectDragging.x + 8, rectDragging.y, 8, 8 );
			mouseMoved( rectMove.x, rectMove.y );
			showRectangle( rectMove );
		}

		else if( event.keyCode == SWT.ARROW_UP ){
			Rectangle rectDragging = _widgetDragging.getBoundsDragging();
			Rectangle rectMove = new Rectangle( rectDragging.x, rectDragging.y - 8, 8, 8 );
			mouseMoved( rectMove.x, rectMove.y );
			showRectangle( rectMove );
		}
	}

	/**
	 * Declared in KeyListener.
	 * Handles page up and page down, because the traverse listener doesn't receive them.
	 */
	public void keyReleased( KeyEvent event ) {
		if( event.keyCode == SWT.PAGE_DOWN ){
			ScrolledComposite scroll = (ScrolledComposite)getParent().getParent();
			Rectangle rectClient = scroll.getClientArea();
			Point ptOrigin = scroll.getOrigin();
			scroll.setOrigin( ptOrigin.x, ptOrigin.y + rectClient.height / 2 );
			_pageDesign.capture();
		}

		else if( event.keyCode == SWT.PAGE_UP ){
			ScrolledComposite scroll = (ScrolledComposite)getParent().getParent();
			Rectangle rectClient = scroll.getClientArea();
			Point ptOrigin = scroll.getOrigin();
			scroll.setOrigin( ptOrigin.x, ptOrigin.y - rectClient.height / 2 );
			_pageDesign.capture();
		}
		
		// End widget dragging
		//------------------------------
		else if( event.character == SWT.ESC ){
			if( _widgetDragging != null ){
				_widgetDragging.setBoundsDragging( new Rectangle( 0, 0, 0, 0 ) );

				_widgetMouseDown = null;
				_widgetDragging = null;
				_ptDropLocations = null;
				_listDropLocations = null;
				_dropLocation = null;
				
				redraw();
			}
		}
	}

	/**
	 * Declared in TraverseListener. 
	 * Traverses to and selects the next or previous widget in the hierarchy.
	 * Traverses to the next and previous tab widgets.
	 * Note: TRAVERSE_PAGE_PREVIOUS and TRAVERSE_PAGE_NEXT do not happen.
	 */
	public void keyTraversed( TraverseEvent event ) {
		// Navigate to previous widget
		//----------------------------
		if( event.detail == SWT.TRAVERSE_ARROW_PREVIOUS ){
			if( _widgetSelected == null )
				return;
			
			if( _widgetDragging != null )
				return;
			
			WidgetPart widgetPrevious = _pageDesign.getWidgetManager().getWidgetPrevious( _widgetSelected );
			if( widgetPrevious == _widgetSelected )
				return;
			
			selectWidget( widgetPrevious );
			showWidget( _widgetSelected );
			redraw();
		}

		// Navigate to next widget
		//------------------------
		else if( event.detail == SWT.TRAVERSE_ARROW_NEXT ){
			if( _widgetSelected == null )
				return;

			if( _widgetDragging != null )
				return;
			
			WidgetPart widgetNext = _pageDesign.getWidgetManager().getWidgetNext( _widgetSelected );
			if( widgetNext == _widgetSelected )
				return;

			selectWidget( widgetNext );
			showWidget( _widgetSelected );
			redraw();
		}

		// Enter key press: Complete widget dragging
		//------------------------------------------
		else if( event.detail == SWT.TRAVERSE_RETURN ){
			if( _widgetDragging != null )
				endWidgetMove();
		}
		
		// Tab previous to the tool bar
		//-----------------------------
		else if( event.detail == SWT.TRAVERSE_TAB_PREVIOUS ){
			_pageDesign._toolbar.setFocus();
		}

		// Tab next no-op
		//---------------
		else if( event.detail == SWT.TRAVERSE_TAB_NEXT ){
		}
	}

	/**
	 * Declared in IMenuListener.  
	 * This method enables and disables the context menu items just before the pop-up menu is shown.
	 */
	public void menuAboutToShow( IMenuManager manager ) {
		refreshMenu();
		
		// Ensure that the overlay has focus.
		// Otherwise a widget move using keyboard will not function
		//---------------------------------------------------------
		setFocus();
		
		// Enable the actions if a widget is selected
		//-------------------------------------------
		_actionWidgetDelete.setEnabled( _widgetSelected != null );
		_actionWidgetProperties.setEnabled( _widgetSelected != null );
		
		boolean bMoveable = false;
		if( _widgetSelected != null )
			if( isDraggable( _widgetSelected.getTypeName() ) == true )
				bMoveable = true;
		
		_actionWidgetMove.setEnabled( bMoveable );
		
		// When the context menu is showing, we do not receive
		// mouse move notifications, so the next selection widget
		// may become invalid
		//-------------------------------------------------------
		_widgetNextSelection = null;
	}

	/**
	 * Declared in MouseListener.
	 */
	public void mouseDoubleClick( MouseEvent e ) {
	}
	
	public void resetMouseDownPoint(){
		_mouseDownPoint = null;
	}

	/**
	 * Declared in MouseListener.
	 * Determines which widget is to be used if a mouse move occurs while the mouse is down (drag).
	 * The selected widget is chosen first if it is under the mouse.  Otherwise the widget that is
	 * the mouse over widget is chosen. 
	 */
	public void mouseDown( MouseEvent event ) {
		//This is a right-click, the mouse position info needs to be remembered for the use in context menu.
		if ( event.button == 1 || event.button == 3 ) {
			_mouseDownPoint = new Point( event.x, event.y );
		}
		if( event.button != 1 )
			return;
		
		// Remember for mouse move
		//------------------------
		_ptMouseDown.x = event.x;
		_ptMouseDown.y = event.y;

		// User may start dragging the selected widget
		//--------------------------------------------
		if( _widgetSelected != null ) {
			_widgetMouseDown = null;

			Collection widgetsUnderMouse = getWidgetsAtPoint( new Point( event.x, event.y ) );

			if( widgetsUnderMouse.contains( _widgetSelected ) == true ) {
				_widgetMouseDown = _widgetSelected;

				Point ptWidget = _widgetSelected.getBoundsOrigin();
				_ptDragOffset.x = ptWidget.x - event.x;
				_ptDragOffset.y = ptWidget.y - event.y;
			}
		}

		// Use the next widget to be selected that the mouse is
		// currently over as the one that will be dragged if a
		// mouse move occurs while the mouse is still down.
		//-----------------------------------------------------
		if( _widgetMouseDown == null && _widgetNextSelection != null ) {
			_widgetMouseDown = _widgetNextSelection;

			Point ptWidget = _widgetMouseDown.getBoundsOrigin();
			_ptDragOffset.x = ptWidget.x - event.x;
			_ptDragOffset.y = ptWidget.y - event.y;
		}
	}


	/**
	 * Declared in MouseTrackListener.  Does nothing.
	 */
	public void mouseEnter( MouseEvent e ) {
	}

	/**
	 * Declared in MouseTrackListener.  When the mouse leaves the overlay, the next selection widget is reset to null.
	 */
	public void mouseExit( MouseEvent e ) {
		if( _widgetNextSelection != null ) {
			_widgetNextSelection.setMouseOver( false );
			_widgetNextSelection = null;
			
			redraw();
		}
	}

	/**
	 * Declared in MouseTrackListener.  Does nothing.
	 */
	public void mouseHover( MouseEvent e ) {
	}

	/**
	 * Declared in MouseMoveListener.
	 */
	public void mouseMove( MouseEvent event ) {
		mouseMoved( event.x, event.y );
	}

	/**
	 * Called by the method above, and by the ScrolledCompositeScroller
	 * which scrolls the overlay within its scrolled composite while
	 * a widget is being dragged.  This affects the position of the
	 * dragging rectangle, even though the mouse may not have physically
	 * moved.
	 */
	public void mouseMoved( int iMouseX, int iMouseY ) {
		// A mouse move happens if a mouse down occurs, even if
		// the mouse hasn't really moved.  This workaround checks
		// to see whether the mouse has actually moved or not.
		//-------------------------------------------------------
		if( _ptMouseDown.x == iMouseX && _ptMouseDown.y == iMouseY )
			return;
		
		_ptMouseDown.x = 0;
		_ptMouseDown.y = 0;

		// Mouse is not down. Find the next selection widget
		//--------------------------------------------------
		if( _widgetMouseDown == null ) {
			Iterator iter = _listClickableAreas.iterator();
			
			while( iter.hasNext() == true ) {
				EvDesignOverylayClickableArea area = (EvDesignOverylayClickableArea)iter.next();
				if ( area.clickableRect.contains( iMouseX, iMouseY ) ) {
					setCursor( getDisplay().getSystemCursor( SWT.CURSOR_HAND ) );
					return;
				}
			}
			setCursor( getDisplay().getSystemCursor( SWT.CURSOR_ARROW ) );

			setupNextSelectionWidget( iMouseX, iMouseY );
			redraw();
			return;
		}

		// Prevent dragging if there is only one widget
		//---------------------------------------------
		WidgetManager widgetManager = _pageDesign.getWidgetManager();
		if( widgetManager.getWidgetCount() < 2 )
			return;
		
		// Prevent dragging if this is this is the only top level widget
		//--------------------------------------------------------------
		WidgetPart widgetParent = _widgetMouseDown.getParent();
		if( widgetParent.getTypeName().equals( WidgetLayoutRegistry.ROOT ) == true )
			if( widgetParent.getChildren().size() == 1 )
				return;

		// See if it is constrained by the where the widget is declared
		//-------------------------------------------------------------
		if( _widgetMouseDown.getMoveable() == false )
			return;

		if( isDraggable( _widgetMouseDown.getTypeName() ) == false )
			return;

		_widgetDragging = _widgetMouseDown;

		// Initialize the dragging rectangle
		//----------------------------------
		if( _widgetDragging.getBoundsDragging().isEmpty() )
			_widgetDragging.setBoundsDragging( _widgetDragging.getBounds() );

		// Set the dragging position relative to the mouse position
		//---------------------------------------------------------
		int iX = iMouseX + _ptDragOffset.x;
		int iY = iMouseY + _ptDragOffset.y;

		_widgetDragging.setBoundsDraggingOrigin( new Point( iX, iY ) );

		// Select the widget being dragged if it isn't selected
		//-----------------------------------------------------
		if( _widgetDragging != _widgetSelected )
			selectWidget( _widgetDragging );

		// Setup drop location feedback for object being dragged
		//------------------------------------------------------
		if( _ptDropLocations == null || _listDropLocations == null )
			setupDropLocations( iMouseX, iMouseY );

		setupNextDropLocation( iMouseX, iMouseY );

		redraw();
		return;
	}

	/**
	 * Declared in MouseListener.
	 */
	public void mouseUp( MouseEvent event ) {
		if( event.button != 1 )
			return;
		
		// If we are not dragging a widget
		//--------------------------------
		if( _widgetDragging == null ) {
			Iterator iter = _listClickableAreas.iterator();
			
			while( iter.hasNext() == true ) {
				EvDesignOverylayClickableArea area = (EvDesignOverylayClickableArea)iter.next();
				if ( area.clickableRect.contains( event.x, event.y ) ) {
					doOperationWidgetOnclick( area );
					_widgetMouseDown = null; 
					return;
				}
			}

			List listWidgets = getWidgetsAtPoint( new Point( event.x, event.y ) );

			// If there is no widget under the mouse,
			// do not deselect the currently selected one
			// (selectWidget( null );)
			//-------------------------------------------
			if( listWidgets.size() == 0 ){
				// Do nothing
			}
			
			// If there is only one widget under the mouse, select it
			//-------------------------------------------------------
			else if( listWidgets.size() == 1 ) {
				WidgetPart widget = (WidgetPart)listWidgets.get( 0 );

				if( widget != _widgetSelected )
					selectWidget( widget );
			}

			// Otherwise, select the next selection widget
			//--------------------------------------------
			else if( _widgetNextSelection != null ) {
				selectWidget( _widgetNextSelection );
			}
		}

		// Dragging a widget
		//------------------
		else {
			endWidgetMove();
		}

		if( _widgetDragging != null ){
			_widgetDragging.setBoundsDragging( new Rectangle( 0, 0, 0, 0 ) );
			_widgetDragging = null;
		}
		
		_widgetMouseDown = null;
		_ptDropLocations = null;
		_listDropLocations = null;
		_widgetDropLocation = null;
		_dropLocation = null;

		// Indicate the widget that will be selected if the mouse is over multiple widgets
		//--------------------------------------------------------------------------------
		setupNextSelectionWidget( event.x, event.y );
		redraw();
	}

	/**
	 * Paints the overlay.
	 */
	public void paintControl( PaintEvent event ) {
		// Not a RUI Handler
		//------------------
		if( _pageDesign.getEditor().isRuiHandler() == false ){
			_painter.paintBlank( event.gc );
			return;
		}
		
		// Transparency is available
		//--------------------------
		if( _pageDesign._bGraphicsTransparencyAvailable == true ) {
			if( _pageDesign.getBrowser() == null ){
				_painter.paintBlank( event.gc );
				return;
			}
			
			_painter.paintOpaque( event.gc );
			_painter.setMouseDownPoint(_mouseDownPoint);
			_painter.paintWidgets( event.gc );
			_painter.paintInstructions( event.gc );
			_painter.paintDropLocations( event.gc );
			_painter.paintWidgetDragging( event.gc );
			_painter.paintHierarchy( event.gc );
			
			return;
		}

		// Transparency is not available
		// Do nothing if a screen capture of the web browser is occurring
		//---------------------------------------------------------------
		EvDesignCaptureInformation captureInfo = _pageDesign.getCaptureInformation();
		if( captureInfo.bCaptureRunning == true )
			return;
		
		// Double buffering, otherwise the widget indicators flicker
		// since the browser image is drawn first.
		// Create an image buffer
		//----------------------------------------------------------
		Rectangle rectBounds = getBounds();
		if( _imageDoubleBuffer != null ){
			Rectangle rectImage = _imageDoubleBuffer.getBounds();
			if( rectBounds.width != rectImage.width || rectBounds.height != rectImage.height ){
				_imageDoubleBuffer.dispose();
				_imageDoubleBuffer = null;
			}
		}

		if( _imageDoubleBuffer == null )
			_imageDoubleBuffer = new Image( getDisplay(), rectBounds.width, rectBounds.height );
					
		GC gc = null;

		// Sometimes the image still has its memory context
		// and the creation of the graphics context throws an exception
		// Delete the image and try again.
		//-------------------------------------------------------------
		try{
			gc = new GC( _imageDoubleBuffer );
		}
		catch( IllegalArgumentException ex ){
			if( _imageDoubleBuffer != null && _imageDoubleBuffer.isDisposed() == false )
				_imageDoubleBuffer.dispose();
				
			_imageDoubleBuffer = new Image( getDisplay(), rectBounds.width, rectBounds.height );
			gc = new GC( _imageDoubleBuffer );
		}
		
		gc.fillRectangle( rectBounds );
		
		// The screen capture captures more than just the web browser.
		// In order not to draw the stuff around the web browser, we
		// restrict the drawing to the part of the design area that was
		// showing when the screen capture took place.
		//-------------------------------------------------------------
		gc.setClipping( captureInfo.rectCapture );
		
		// Paint the screen capture of the web browser
		//--------------------------------------------
		Image imageBrowser = captureInfo.imageBrowser;
		if( imageBrowser != null ){
			Rectangle r = captureInfo.rectCapture;
			
			try{
				// An exception is thrown if the source width/height is greater than the image width/height
				// The source and destination origin are always zero.
				//-----------------------------------------------------------------------------------------
				int iWidth  = Math.min( imageBrowser.getBounds().width, r.width );
				int iHeight = Math.min( imageBrowser.getBounds().height, r.height );
				gc.drawImage( imageBrowser, r.x, r.y, iWidth, iHeight, r.x, r.y, iWidth, iHeight );
			}
			catch( IllegalArgumentException ex ){
			}
		}
		
		// Paint our things
		//-----------------
		_painter.paintOpaque( gc );
		_painter.setMouseDownPoint(_mouseDownPoint);
		_painter.paintWidgets( gc );
		_painter.paintInstructions( gc );
		_painter.paintDropLocations( gc );
		_painter.paintWidgetDragging( gc );
		_painter.paintHierarchy( gc );

		// Paint the image buffer
		//-----------------------
		event.gc.drawImage( _imageDoubleBuffer, 0, 0 );
		
		// Dispose of the double buffer graphics context
		// This has to be done after the drawing, otherwise
		// we will be unable to create another one
		//-------------------------------------------------
		gc.dispose();
	}

	/**
	 * Called from selectWidget.
	 * Creates a widget selection hierarchy of child indices.
	 * The root is not included in the hierarchy of numbers.
	 * The first number is the index of one of the root's children.
	 * The second number is the index of one of the root's grand children and so on.
	 * Zero indicates the first child in the list of children.
	 * One indicates the second child in the list of children and so on.
	 */
	protected void rememberSelectionHierarchy() {

		_listSelectionHierarchy.clear();

		if( _widgetSelected == null )
			return;

		WidgetManager widgetManager = _pageDesign.getWidgetManager();
		WidgetPart widgetRoot = widgetManager.getWidgetRoot();
		rememberSelectionHierarchyRecursive( widgetRoot );
	}

	/**
	 * Creates the selection hierarchy given a parent widget and a child index.
	 * This is used for the create and move widget operations. 
	 */
	protected void rememberSelectionHierarchyForOperation( WidgetPart widgetParent, int iIndex ) {
		// Go backward through the hierarchy until the root is found
		//----------------------------------------------------------
		ArrayList listHierarchy = new ArrayList();

		listHierarchy.add( new Integer( iIndex ) );
		boolean bFound = rememberSelectionHierarchyForOperationRecursive( widgetParent, listHierarchy );

		// Remember the hierarchy if it is valid
		//--------------------------------------
		if( bFound == true )
			_listSelectionHierarchy = listHierarchy;
	}

	/**
	 * Creates the selection hierarchy given a parent widget and a child index.
	 * This is used for the delete widget operation.
	 * The selection moves to a child with lower index if there is one, or
	 * if this is an only child, the selection moves to the parent.
	 * Otherwise, the index is untouched so a child who inherits the
	 * same index as the deleted child will be selected. 
	 */
	protected void rememberSelectionHierarchyForOperationDelete( WidgetPart widget ) {
		// The last index in the hierarchy list is always the index of the selected widget within its parent
		//--------------------------------------------------------------------------------------------------
		int iListIndex = _listSelectionHierarchy.size() - 1;

		// Do nothing if the list is empty (should never occur)
		//-----------------------------------------------------
		if( iListIndex < 0 )
			return;

		WidgetPart widgetParent = widget.getParent();
		int iNumberOfChildren = widgetParent.getChildren().size();

		// The last child is being removed
		// Select the parent by removing the child level from the list
		//------------------------------------------------------------
		if( iNumberOfChildren == 1 ) {
			_listSelectionHierarchy.remove( iListIndex );
			return;
		}

		// If this is the last child in the list,
		// select the previous child
		//---------------------------------------		
		int iWidgetIndex = widgetParent.getChildIndex( widget );

		if( iWidgetIndex == iNumberOfChildren - 1 ) {
			_listSelectionHierarchy.remove( iListIndex );
			_listSelectionHierarchy.add( new Integer( iWidgetIndex - 1 ) );
			return;
		}
	}

	/**
	 * Returns true if child is found within the parent.
	 */
	protected boolean rememberSelectionHierarchyForOperationRecursive( WidgetPart widget, ArrayList listHierarchy ) {
		if( widget == null )
			return false;

		WidgetPart widgetParent = widget.getParent();

		// Stop if the widget is the root
		//-------------------------------
		if( widgetParent == null )
			return true;

		// Determine the index of the widget within its parent
		//----------------------------------------------------
		int iIndex = widgetParent.getChildIndex( widget );
		if( iIndex < 0 )
			return false;

		// Prepend the index to the hierarchy
		//-----------------------------------
		listHierarchy.add( 0, new Integer( iIndex ) );

		// Recursively do the parent widget
		//----------------------------------
		return rememberSelectionHierarchyForOperationRecursive( widgetParent, listHierarchy );
	}

	/**
	 * Returns whether the widget was found in the hierarchy and the list is complete.
	 */
	protected boolean rememberSelectionHierarchyRecursive( WidgetPart widget ) {

		boolean bFound = false;
		List listChildren = widget.getChildren();

		for( int i = 0; i < listChildren.size(); i++ ) {
			WidgetPart widgetChild = (WidgetPart)listChildren.get( i );

			// The selected widget is found
			// Save its index into the empty array
			//------------------------------------
			if( widgetChild.getSelected() == true ) {
				_listSelectionHierarchy.add( new Integer( i ) );
				return true;
			}

			// The selected widget has been found as a descendant
			// Prepend the parent index to the array
			//---------------------------------------------------
			else {
				bFound = rememberSelectionHierarchyRecursive( widgetChild );
				if( bFound == true ) {
					_listSelectionHierarchy.add( 0, new Integer( i ) );
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Attempts to restore the widget selection based on a previously saved hierarchy.
	 */
	protected void restoreSelectionHierarchy() {
		WidgetManager widgetManager = _pageDesign.getWidgetManager();
		
		// If there is no widget, select none
		//-----------------------------------
		if( widgetManager.getWidgetCount() == 0 ){
			selectWidget( null );
			return;
		}

		// If there is a newly created single widget, select it
		//-----------------------------------------------------
		if( widgetManager.getWidgetCount() == 1 && _listSelectionHierarchy.size() == 0 )
			_listSelectionHierarchy.add( new Integer( 0 ) );

		if( _listSelectionHierarchy.size() == 0 )
			return;

		WidgetPart widgetRoot = widgetManager.getWidgetRoot();
		restoreSelectionHierarchyRecursive( widgetRoot, 0 );
	}

	/**
	 * Attempts to restore the widget selection based on a previously saved hierarchy.
	 */
	protected void restoreSelectionHierarchyRecursive( WidgetPart widgetParent, int iHierarchyListIndex ) {
		List listChildren = widgetParent.getChildren();

		int iChildIndex = ( (Integer)_listSelectionHierarchy.get( iHierarchyListIndex ) ).intValue();

		// Child no longer exists, select another widget
		//----------------------------------------------
		if( iChildIndex >= listChildren.size() ) {
			// Select the previous child
			//--------------------------
			if( iChildIndex - 1 >= 0 && ( iChildIndex - 1 < listChildren.size() ) )
				selectWidget( (WidgetPart)listChildren.get( iChildIndex - 1 ) );

			// Select the first child
			//-----------------------
			else if( listChildren.size() > 0 )
				selectWidget( (WidgetPart)listChildren.get( 0 ) );

			// Select the parent if it is not the root
			//----------------------------------------
			else if( widgetParent.getParent() != null )
				selectWidget( widgetParent );

			return;
		}

		WidgetPart widgetChild = (WidgetPart)listChildren.get( iChildIndex );

		// End of the array has been reached and the selected widget has been located
		//---------------------------------------------------------------------------
		if( iHierarchyListIndex == _listSelectionHierarchy.size() - 1 )
			selectWidget( widgetChild );

		else {
			restoreSelectionHierarchyRecursive( widgetChild, iHierarchyListIndex + 1 );
		}
	}

	/**
	 * Changes the selected widget to the specified widget.  If the specified widget is null, all widgets are deselected. 
	 */
	protected void selectWidget( WidgetPart widget ) {

		// Scenario: Last widget is deleted, update properties view
		//---------------------------------------------------------
		if( widget == _widgetSelected ){
			if( widget == null )
				_pageDesign.widgetSelectedFromDesignCanvas( null );
			return;
		}

		if( widget == null && _widgetSelected != null ) {
			_widgetSelected.setSelected( false );
			_widgetSelected = null;
		}

		else if( widget != null && _widgetSelected == null ) {
			widget.setSelected( true );
			_widgetSelected = widget;
		}

		else {
			_widgetSelected.setSelected( false );
			widget.setSelected( true );
			_widgetSelected = widget;
		}

		rememberSelectionHierarchy();

		_pageDesign.widgetSelectedFromDesignCanvas( _widgetSelected );

//		_actionWidgetDelete.setEnabled( _widgetSelected != null );
//		_actionWidgetProperties.setEnabled( _widgetSelected != null );
		
		showWidget( widget );
	}

	/**
	 * Sets the transparency mode and amount of the design area based on the toolbar controls.
	 */
	public void setTransparency( int iTransparencyMode, int iTransparencyAmount ) {
		_iTransparencyMode = iTransparencyMode;
		_iTransparencyAmount = iTransparencyAmount;

		redraw();
	}

	/**
	 * 
	 */
	protected void setupDropLocations( int iMouseX, int iMouseY ) {
		WidgetManager widgetManager = _pageDesign.getWidgetManager();
		if( widgetManager == null )
			return;

		// Drop onto a RUI handler
		//------------------------
		if( widgetManager.getWidgetCount() == 0 ) {
			_listDropLocations = new ArrayList();
			
			// Compute bounds which is the client area that is showing minus margins
			//----------------------------------------------------------------------
			ScrolledComposite sc = (ScrolledComposite)getParent().getParent();
			WidgetLayout handlerLayout = WidgetLayoutRegistry.getInstance().getWidgetLayout( "VE-HANDLER", false );
			if ( handlerLayout != null ) {
				handlerLayout.initialize( sc, null, _widgetDragging, widgetManager, WidgetDescriptorRegistry.getInstance(_pageDesign.getEditor().getProject()), _pageDesign.getEditor().getEditorProvider() );
				handlerLayout.setupDropLocations(_listDropLocations);
			}
			return;
		}

		// Non-static positioning when dragging a widget
		// If a widget being dragged has position that is absolute
		// we assume that the user wants to relocate
		// the widget position and not the widget statement.
		// So we do not show drop locations
		//--------------------------------------------------
		if( _widgetDragging != null ) {
			WidgetPropertyValue positionValue = _pageDesign.getEditor().getPropertyValue( _widgetDragging, "position", IVEConstants.STRING_TYPE );
			
			String strType = null;
			ArrayList listValues = null;
			
			if( positionValue != null )
				listValues = positionValue.getValues();
			
			if( listValues != null && listValues.size() > 0 )
				strType = (String)listValues.get( 0 );
			
			if( strType != null ) {
				if( strType.equalsIgnoreCase( "absolute" ) || strType.equalsIgnoreCase( "relative" ) || strType.equalsIgnoreCase( "Fixed" ) )
					return;
			}
		}
		
		// Static positioning: Drop onto a widget or RUI Handler
		// Find the container that we are over
		//------------------------------------------------------
		WidgetPart widget = findDropLocationWidget( iMouseX, iMouseY );

		// Optimization
		//-------------
		if( widget == _widgetDropLocation && _listDropLocations != null )
			return;
		
		_widgetDropLocation = widget;
		_dropLocation = null;
		_listDropLocations = new ArrayList();

		// Do itself and its parent hierarchy
		//-----------------------------------
		while( widget != null ) {
			setupDropLocationsForWidget( widget, _listDropLocations );
			widget = widget.getParent();
		}
	}
	
	/*
	 * setup the clickable area list.
	 */
	protected void setupClickableAreas() {
		Iterator iterWidgets = getWidgets();
		_listClickableAreas.clear();
		
		while( iterWidgets.hasNext() == true ) {
			WidgetPart widget = (WidgetPart)iterWidgets.next();
			String clickableInfo = widget.getExtraInfo( "ClickableAreas" );
			if ( clickableInfo == null || clickableInfo.length() == 0 ) {
				continue;
			}
			String[] areas = clickableInfo.split(":");
			if ( areas == null || areas.length == 0 ) {
				return;
			}
			try {
				String selectionProperty = areas[0];
				for ( int i = 1; i < areas.length; i ++ ) {
					int x = Integer.parseInt( areas[i] );
					int y = Integer.parseInt( areas[++i] );
					int w = Integer.parseInt( areas[++i] );
					int h = Integer.parseInt( areas[++i] );
					String value = areas[++i];

					EvDesignOverylayClickableArea ca = new EvDesignOverylayClickableArea();
					ca.clickableRect = new Rectangle( x, y, w ,h );
					ca.propertyName = selectionProperty;
					ca.propertyValue = value;
					ca.widget = widget;
					_listClickableAreas.add( ca );
				}
			} catch ( Exception e ) {
				//do nothing
			}
		}
	}

	/**
	 * Creates drop locations for a widget container, such as a Box. 
	 */
	protected void setupDropLocationsForWidget( WidgetPart widget, Collection listDropLocations ) {
		// A dragged widget cannot become a sibling of its children
		//---------------------------------------------------------
		if( widget == _widgetDragging )
			return;

		WidgetLayout widgetLayout = WidgetLayoutRegistry.getInstance().getWidgetLayout( WidgetLayoutRegistry.getLayoutName(widget), isContainer( widget ) );
		if ( widgetLayout != null ) {
			WidgetManager widgetManager = _pageDesign.getWidgetManager();
			ScrolledComposite sc = (ScrolledComposite)getParent().getParent();
			widgetLayout.initialize( sc, widget, _widgetDragging, widgetManager, WidgetDescriptorRegistry.getInstance(_pageDesign.getEditor().getProject()), _pageDesign.getEditor().getEditorProvider() );
			// only setup drop location when it is not root widget in RUIWidget
			if(!((widgetLayout instanceof RootWidgetLayout) && getDesignPage().getEditor().getEditorProvider().isRUIWidget())){
				widgetLayout.setupDropLocations(listDropLocations);
			}
		}
	}
	
	/**
	 * Determines the drop location that contains the given mouse position.
	 * The rectangle is painted in paintDropLocations.
	 */
	protected void setupNextDropLocation( int iX, int iY ) {
		if( _listDropLocations == null )
			return;

		// Compute the drop rectangle
		//---------------------------
		_dropLocation = null;
		
		// Remember for the overlay painter
		//---------------------------------
		_ptMouse.x = iX;
		_ptMouse.y = iY;

		// Test for the mouse over a drop location
		//----------------------------------------
		Iterator iterLocations = _listDropLocations.iterator();
		
		while( iterLocations.hasNext() == true ) {
			EvDesignOverlayDropLocation location = (EvDesignOverlayDropLocation)iterLocations.next();

			if( location.rectDrop.contains( iX, iY ) == true ) {
				_dropLocation = location;
				break;
			}
		}

		if( _dropLocation != null )
			return;

		// Proximity test
		//---------------		
		iterLocations = _listDropLocations.iterator();

		while( iterLocations.hasNext() == true ) {
			EvDesignOverlayDropLocation location = (EvDesignOverlayDropLocation)iterLocations.next();
			Rectangle rectProximity = new Rectangle( location.rectDrop.x - 8, location.rectDrop.y - 8, location.rectDrop.width + 16, location.rectDrop.height + 16 );
			
			if( rectProximity.contains( iX, iY ) == true ) {
				_dropLocation = location;
				break;
			}
		}

		if( _dropLocation != null )
			return;

		/* The nearsst drop lacation search will cuase some unexpected results, see WI 66498
		// Find the nearest drop location center
		//--------------------------------------
		double dMinimum = Double.MAX_VALUE;
		EvDesignOverlayDropLocation locationMinimum = null;
		iterLocations = _listDropLocations.iterator();

		while( iterLocations.hasNext() == true ) {
			EvDesignOverlayDropLocation location = (EvDesignOverlayDropLocation)iterLocations.next();

			int iAbsoluteDeltaX = Math.abs( iX - ( location.rectDrop.x + location.rectDrop.width / 2 ) );
			int iAbsoluteDeltaY = Math.abs( iY - ( location.rectDrop.y + location.rectDrop.height / 2 ) );
			double dDistanceSquared = (double)iAbsoluteDeltaX * (double)iAbsoluteDeltaX + (double)iAbsoluteDeltaY * (double)iAbsoluteDeltaY;
			double dDistance = Math.sqrt( dDistanceSquared );

			if( dDistance < dMinimum ) {
				dMinimum = dDistance;
				locationMinimum = location;
			}
		}

		// Allow for not dropping
		//-----------------------
		if( dMinimum < 64 )
			_dropLocation = locationMinimum;
		*/
	}

	/**
	 * Sets the widget that the mouse is over based on the current selection, and the current widget that the mouse is over.
	 */
	protected void setupNextSelectionWidget( int iX, int iY ) {

		List listWidgets = getWidgetsAtPoint( new Point( iX, iY ) );

		WidgetPart widget = null;

		// Mouse is no longer over a widget
		//---------------------------------
		if( listWidgets.size() == 0 ) {
			// No widget in the list
		}

		// If the currently selected widget is in the list, select the next one
		// If not in the list, select the first in the list
		//---------------------------------------------------------------------
		else if( _widgetSelected != null )
			widget = findNextWidgetInList( _widgetSelected, listWidgets );

		// Use the widget with the greatest origin
		//----------------------------------------
		else
			widget = (WidgetPart)listWidgets.get( 0 );

		// Do nothing if the widget found is the same as the current one
		//--------------------------------------------------------------
		if( widget == _widgetNextSelection )
			return;

		// Deactivate any existing mouse over widget
		//------------------------------------------
		if( _widgetNextSelection != null ) {
			_widgetNextSelection.setMouseOver( false );
			_widgetNextSelection = null;
		}

		// Activate the new mouse over widget
		//-----------------------------------
		if( widget != null ) {
			widget.setMouseOver( true );
			_widgetNextSelection = widget;
		}		
	}

	/**
	 * Ensures that the specified rectangle is displayed within the design area. 
	 */
	protected void showRectangle( Rectangle rectShow ){
		ScrolledComposite scroll = (ScrolledComposite)getParent().getParent();
		Rectangle rectClient = scroll.getClientArea();
		Point ptOrigin = scroll.getOrigin();
		rectClient.x = ptOrigin.x;
		rectClient.y = ptOrigin.y;

		if( rectShow.width < 1 )
			rectShow.width = 1;
		
		if( rectShow.height < 1 )
			rectShow.height = 1;
		
		// See if the center portion of the widget is seen
		//------------------------------------------------
		if( rectClient.intersects( rectShow ) == false ){
			final int PADDING = 20;

			if( rectShow.x < rectClient.x )
				rectClient.x = rectShow.x - PADDING;
			else
				rectClient.x = rectShow.x + rectShow.width - rectClient.width + PADDING;

			if( rectShow.y < rectClient.y )
				rectClient.y = rectShow.y - PADDING;
			else
				rectClient.y = rectShow.y + rectShow.height - rectClient.height + PADDING;

			scroll.setOrigin( rectClient.x, rectClient.y );
//			_pageDesign.capture();
		}
	}
	
	/**
	 * Ensures that the specified widget is displayed within the design area
	 */
	protected void showWidget( WidgetPart widget ) {
		if( widget == null )
			return;

		// Use the center portion of the widget
		//-------------------------------------
		Rectangle rectWidget = widget.getBounds();
		Rectangle newRectWidget = new Rectangle(0, 0, 0, 0 );
		newRectWidget.x = rectWidget.x + 4;
		newRectWidget.y = rectWidget.y + 4;
		newRectWidget.width = rectWidget.width - 8;
		newRectWidget.height = rectWidget.height - 8;

		showRectangle( rectWidget );
	}

	/**
	 * Called by this constructor, and by the design page whenever the selection color preference has changed.
	 */
	public void updateColors() {
		// Potential drop target
		//----------------------
		String strColor = EvPreferences.getString( EvConstants.PREFERENCE_COLOR_DROP_LOCATION_POTENTIAL );

		// Convert from 255, 255, 255 to a color
		//--------------------------------------
		Color color = ColorUtil.getColorFromRGBString( Display.getCurrent(), strColor );

		if( color != null ) {
			// Dispose of any existing color
			//------------------------------
			if( _colorDropTargetPotential != null && _colorDropTargetPotential.isDisposed() == false )
				_colorDropTargetPotential.dispose();

			// Establish the new color
			//------------------------
			_colorDropTargetPotential = color;
		}

		// Selected drop target
		//---------------------
		strColor = EvPreferences.getString( EvConstants.PREFERENCE_COLOR_DROP_LOCATION_SELECTED );

		// Convert from 255, 255, 255 to a color
		//--------------------------------------
		color = ColorUtil.getColorFromRGBString( Display.getCurrent(), strColor );

		if( color != null ) {
			// Dispose of any existing color
			//------------------------------
			if( _colorDropTargetSelected != null && _colorDropTargetSelected.isDisposed() == false )
				_colorDropTargetSelected.dispose();

			// Establish the new color
			//------------------------
			_colorDropTargetSelected = color;
		}

		// Widget selection
		//-----------------
		strColor = EvPreferences.getString( EvConstants.PREFERENCE_COLOR_SELECTION );

		// Convert from 255, 255, 255 to a color
		//--------------------------------------
		color = ColorUtil.getColorFromRGBString( Display.getCurrent(), strColor );
		if( color == null )
			return;

		// Dispose of any existing color
		//------------------------------
		if( _colorWidgetSelected != null && _colorWidgetSelected.isDisposed() == false )
			_colorWidgetSelected.dispose();

		// Establish the new color
		//------------------------
		_colorWidgetSelected = color;

		_painter.updateColors();
		redraw();
	}

	/**
	 * Declared in DisposeListener.
	 * Called when this composite is disposed.  All colors are disposed.
	 */
	public void widgetDisposed( DisposeEvent e ) {
		if( _colorDropTargetPotential != null && _colorDropTargetPotential.isDisposed() == false ) {
			_colorDropTargetPotential.dispose();
			_colorDropTargetPotential = null;
		}

		if( _colorDropTargetSelected != null && _colorDropTargetSelected.isDisposed() == false ) {
			_colorDropTargetSelected.dispose();
			_colorDropTargetSelected = null;
		}

		if( _colorWidgetSelected != null && _colorWidgetSelected.isDisposed() == false ) {
			_colorWidgetSelected.dispose();
			_colorWidgetSelected = null;
		}
		
		if( _cursorWait != null && _cursorWait.isDisposed() == false ){
			_cursorWait.dispose();
			_cursorWait = null;
		}
		
		if( _imageDoubleBuffer != null && _imageDoubleBuffer.isDisposed() == false ){
			_imageDoubleBuffer.dispose();
			_imageDoubleBuffer = null;
		}
	}

	/**
	 * The widget manager has a widget set.
	 */
	public void widgetsChanged() {
		_widgetDragging = null;
		_widgetMouseDown = null;
		_widgetNextSelection = null;
		_widgetSelected = null;
		_ptDropLocations = null;
		_listDropLocations = null;
		_dropLocation = null;
				
		// If there are no widgets, display the start instruction when painting
		// and scroll the window to the top left corner so the user can see it
		//---------------------------------------------------------------------
		_bShowInstructions = getWidgets().hasNext() == false;
		
		if( _bShowInstructions == true ){
			ScrolledComposite scroll = (ScrolledComposite)getParent().getParent();
			scroll.setOrigin( 0, 0 );
		}

		setupClickableAreas();
		restoreSelectionHierarchy();
		
		showWidget( _widgetSelected );

		_pageDesign.capture();
		
		// Repaint
		//--------
		Display display = getDisplay();
		if( !display.isDisposed() ) {
			display.asyncExec( new Runnable() {

				public void run() {
					redraw();
				}
			} );
		}
	}
}
