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
package org.eclipse.edt.ide.rui.visualeditor.internal.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.eclipse.edt.ide.rui.visualeditor.internal.jsonvisitors.JsonVisitor;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.ComparatorWidgetPositions;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.layout.WidgetLayoutRegistry;
import org.eclipse.edt.javart.json.JsonParser;
import org.eclipse.edt.javart.json.ObjectNode;
import org.eclipse.edt.javart.json.ParseException;
import org.eclipse.swt.graphics.Point;

/**
 * Manages a list of widget parts
 */
public class WidgetManager {

	protected WidgetPart	_widgetRoot	= null;
	protected TreeSet       _widgetPartsTree = null;
	protected List          _listWidgetParts = null;
	/**
	 * Constructor.  A root widget is created that represents the RUI Handler.
	 */
	public WidgetManager() {
		_widgetRoot = new WidgetPart();
		_widgetRoot.setTypeName( WidgetLayoutRegistry.ROOT );
		_widgetPartsTree = new TreeSet( new ComparatorWidgetPositions() );
		_listWidgetParts = new ArrayList();
	}

	/**
	 * Recursively adds widgets to the tree set.
	 */
	protected void addChildrenToSortedSet( TreeSet treeset, WidgetPart widgetPart ) {
		List listChildren = widgetPart.getChildren();

		for( int i = 0; i < listChildren.size(); ++i ) {
			WidgetPart widgetChild = (WidgetPart)listChildren.get( i );
			treeset.add( widgetChild );
			addChildrenToSortedSet( treeset, widgetChild );
		}
	}

	/**
	 * 
	 */
	public void addWidget( WidgetPart widgetParent, WidgetPart widgetChild ) {
		widgetParent.addChildWidget( widgetChild );
	}

	/**
	 * Finds the widget with the given statement offset and length.
	 */
	public WidgetPart getWidget( int iOffset, int iLength ) {
		Collection widgets = getWidgetList();

		for( Iterator iterWidgets = widgets.iterator(); iterWidgets.hasNext(); ) {
			WidgetPart widget = (WidgetPart)iterWidgets.next();
			if( widget.getStatementOffset() == iOffset && widget.getStatementLength() == iLength )
				return widget;
		}

		return null;
	}

	/**
	 * Returns the number of widgets. 
	 */
	public int getWidgetCount() {
		return getWidgetList().size();
	}

	/**
	 * Returns the next widget in the list of widgets sorted by statement location.
	 */
	public WidgetPart getWidgetNext( WidgetPart widget ) {		
		WidgetPart widgetNext = null;

		List listChildren = widget.getChildren();
		
		// Next sibling
		//-------------
		if( listChildren.size() > 0 )
			widgetNext = (WidgetPart)listChildren.get( 0 );

		// Locate the next sibling of an ascendant
		//----------------------------------------
		else {
			WidgetPart widgetParent = widget;

			// Go through the parent hierarchy
			//--------------------------------
			while( true ) {
				widget = widgetParent;
				widgetParent = widgetParent.getParent();
				
				// Parent is the root
				//-------------------
				if( widgetParent == null ){
					widgetNext = (WidgetPart)_widgetRoot.getChildren().get( 0 ); 
					break;
				}
				
				int iIndex = widgetParent.getChildIndex( widget );
				listChildren = widgetParent.getChildren();

				if( iIndex < listChildren.size() - 1 ){
					widgetNext = (WidgetPart)listChildren.get( iIndex + 1 );
					break;
				}
			}
		}
		
		return widgetNext;
	}

	/**
	 * Returns the previous widget in the hierarchy tree.
	 */
	public WidgetPart getWidgetPrevious( WidgetPart widget ) {
		WidgetPart widgetPrevious = null;

		WidgetPart widgetParent = widget.getParent();

		int iIndex = widgetParent.getChildIndex( widget );

		if( iIndex == 0 ) {
			if( widgetParent != _widgetRoot )
				return widgetParent;
			
			widgetPrevious = (WidgetPart)widgetParent.getChildren().get( widgetParent.getChildren().size() - 1 );
		}

		else
			widgetPrevious = (WidgetPart)widgetParent.getChildren().get( iIndex - 1 );

		// Find the lowest leaf
		//---------------------
		while( true ) {
			List listChildren = widgetPrevious.getChildren();
			if( listChildren.size() == 0 )
				break;

			widgetPrevious = (WidgetPart)listChildren.get( listChildren.size() - 1 );
		}
		
		return widgetPrevious;
	}

	/**
	 * Returns the root widget, which represents the RUI Handler.
	 */
	public WidgetPart getWidgetRoot() {
		return _widgetRoot;
	}

	/**
	 * Returns an unordered set of widget parts. 
	 */
	public List getWidgetList(){
		return _listWidgetParts;
	}
	
	/**
	 * Recursively adds widgets to the unordered list.
	 */
	public void getWidgetListRecursive( List listWidgetParts, WidgetPart widgetPart ) {
		List listChildren = widgetPart.getChildren();

		for( int i = 0; i < listChildren.size(); ++i ) {
			WidgetPart widgetChild = (WidgetPart)listChildren.get( i );
			listWidgetParts.add( widgetChild );
			getWidgetListRecursive( listWidgetParts, widgetChild );
		}
	}
	
	/*
	 * Return true is WidgetPart child is really the child of WidgetPart parent, or false.
	 */
	public boolean isChildOf( WidgetPart child, WidgetPart parent ) {
		if ( parent == _widgetRoot && child != _widgetRoot ) {
			return true;
		}
		if ( child.getParent() == _widgetRoot ) {
			return false;
		}
		if ( child.getParent() == parent ) {
			return true;
		}
		
		return isChildOf( child.getParent(), parent );
	}

	/**
	 * Returns a collection of widgets sorted by position order, with smaller origins first.
	 */
	public List getWidgets( Point point ) {
		// Filter the list to those under the point
		//-----------------------------------------
		ArrayList list = new ArrayList();
		Iterator iterWidgets = _widgetPartsTree.iterator();

		while( iterWidgets.hasNext() == true ) {
			WidgetPart widget = (WidgetPart)iterWidgets.next();
			if( widget.getBounds().contains( point ) == true )
				list.add( widget );
		}

		return list;
	}

	/**
	 * Returns whether there are any widgets. 
	 */
	public boolean hasWidgets() {
		return _widgetRoot.getChildren().isEmpty() == false;
	}

	/**
	 * Removes all widgets.
	 */
	public void removeAllWidgets() {
		_widgetRoot.removeAllChildren();
		_widgetPartsTree.clear();
		_listWidgetParts.clear();
	}

	/**
	 * Traverses the widget tree removing invalid widgets from the leaves to the root. 
	 */
	protected void removeInvalidWidgets() {
		removeInvalidWidgetsRecursive( _widgetRoot );
	}

	/**
	 * Recursively removes widgets that have invalid characteristics.
	 */
	protected void removeInvalidWidgetsRecursive( WidgetPart widget ) {
		// Do the children first.
		// Traverse backward through the children so they
		// can be removed while going through the list
		//-----------------------------------------------		
		List listChildren = widget.getChildren();

		for( int i = listChildren.size() - 1; i >= 0; i-- )
			removeInvalidWidgetsRecursive( (WidgetPart)listChildren.get( i ) );

		// If the children are parented by this widget in an external RUI handler,
		// this widget will have an invalid statement.  Remove its children.
		//------------------------------------------------------------------------
		if( widget != _widgetRoot )
			if( widget._iStatementOffset < 0 || widget._iStatementLength <= 0 )
				widget.removeAllChildren();

		// Check the widget for invalid characteristics
		//---------------------------------------------
		if( widget._rectBounds.isEmpty() || widget.getTypeName() == null )
			removeWidget( widget );

		else if( ( widget._iStatementOffset < 0 || widget._iStatementLength <= 0 ) && widget.getMoveable() == false )
			removeWidget( widget );
	}

	/**
	 * Removes a widget from its parent's list of child widgets.
	 */
	public void removeWidget( WidgetPart widgetPart ) {
		WidgetPart widgetParent = widgetPart.getParent();

		if( widgetParent == null )
			return;

		List listChildren = widgetParent.getChildren();
		listChildren.remove( widgetPart );
	}

	/**
	 * A Json string has been received from the browser in response to a get widget information request.
	 * This method removes all existing widget parts and creates new ones by parsing the Json string.
	 */
	public void setWidgetsFromJsonString( String strJson ) {
		removeAllWidgets();

		// The Json parser uses the widget part given to it
		// as the top level part, so we need to create it ahead of time
		// and check it after the parse to see if it was used.
		//-------------------------------------------------------------
		try {
			ObjectNode node = JsonParser.parse( strJson );
			try {
				node.accept( new JsonVisitor( _widgetRoot ) );
			}
			catch( RuntimeException ex ) {
			}
		}
		catch( ParseException ex ) {
		}
		
		// Obtain a list of all widgets sorted by position
		//------------------------------------------------
		addChildrenToSortedSet( _widgetPartsTree, _widgetRoot );
		
		getWidgetListRecursive( _listWidgetParts, _widgetRoot );

		// Debug only
		//-----------
		if( false )
			_widgetRoot.printWidgetTree( 0 );

		// Remove any invalid widgets
		//---------------------------
		// Yun Feng: the invalid widgets have been filtered out in browser.
		//removeInvalidWidgets();
	}
}
