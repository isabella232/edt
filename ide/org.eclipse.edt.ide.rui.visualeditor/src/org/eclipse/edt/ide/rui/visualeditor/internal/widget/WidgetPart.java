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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;


public class WidgetPart {
	protected static int	MIN_PART_HEIGHT		= 16;
	protected static int	MIN_PART_WIDTH		= 16;
	protected static int    COUNTER				= 1;

	protected boolean		_bMouseOver			= false;
	protected boolean		_bMoveable			= true;
	protected boolean		_bSelected			= false;
	protected int			_iStatementLength	= 0;
	protected int			_iStatementOffset	= 0;
	protected List			_listChildren		= Collections.synchronizedList( new ArrayList() );
	protected Rectangle		_rectBounds			= new Rectangle( 0, 0, 0, 0 );
	protected Rectangle		_rectDragging		= new Rectangle( 0, 0, 0, 0 );
	protected String		_strPackageName		= null;
	protected String		_strTypeID			= null;
	protected String		_strTypeName		= null;
	protected String		_strVariableName	= null;
	protected String		_strExtraInfo		= null;
	protected WidgetPart	_widgetParent		= null;
	protected HashMap       _tempInfoCache      = new HashMap();
	
	protected int 			_counter;
	
	private boolean 		_bidiBoundsFixed = false; 	// IBMBIDI Append 

	public WidgetPart() {
		_counter = (COUNTER ++) % Integer.MAX_VALUE;
	}
	
	/**
	 * 
	 */
	public void addChildWidget( WidgetPart widgetChild ) {
		_listChildren.add( widgetChild );
	}

	/**
	 * Returns the bounds of the widget for the design area.  The bounds have a minimum width and height so that the widget can be seen. 
	 * *NOTE* don't change the value of the return rectangle.
	 */
	public Rectangle getBounds() {
		return _rectBounds;
	}

	/**
	 * 
	 */
	public Rectangle getBoundsDragging() {
		return _rectDragging;
	}

	/**
	 * Returns the position and size of the widget. 
	 */
	public Point getBoundsOrigin() {
		return new Point( _rectBounds.x, _rectBounds.y );
	}

	/**
	 * Returns the index of the child widget.
	 * A -1 is returned if the child is not found.
	 */
	public int getChildIndex( WidgetPart widgetChild ){
		return _listChildren.indexOf( widgetChild );
	}
	
	/**
	 * Returns the list of child widgets.
	 */
	public List getChildren() {
		return _listChildren;
	}
	
	/**
	 * Returns a string derived from the widget type name and variable name, such as TextLabel (howtolabel) 
	 */
	public String getLabel(){
		String type = "";
		String var = null;
		if( _strTypeName != null && _strTypeName.length() > 0 ) {
			type = _strTypeName;
		}
		
		if( _strVariableName != null && _strVariableName.length() > 0 ){
			var = _strVariableName;
		}
		
		if ( var == null )
		{
			return type;
		}
		return NLS.bind( Messages.NL_Properties_View_Widget_Label, new Object[]{ type, var } ).trim();
	}
	
	/**
	 * Returns whether the mouse is over this widget.
	 */
	public boolean getMouseOver() {
		return _bMouseOver;
	}
	
	/**
	 * Returns whether this widget can be moved to another place in the widget hierarchy.
	 * This only pertains when the statement offset and lengths are invalid.  The widget
	 * or embedded RUI Handler is not defined in the file being edited, but can be moved.
	 */
	public boolean getMoveable() {
		return _bMoveable;
	}
	
	/**
	 * Returns the package name of where the widget is declared.
	 */
	public String getPackageName(){
		return _strPackageName;
	}
	
	/**
	 * Returns the parent widget.
	 */
	public WidgetPart getParent() {
		return _widgetParent;
	}

	/**
	 * Returns whether this widget is currently selected.
	 */
	public boolean getSelected() {
		return _bSelected;
	}

	/**
	 * Returns the statement's string length within the source document.
	 */
	public int getStatementLength() {
		return _iStatementLength;
	}

	/**
	 * Returns the statement's starting character index location within the source document.
	 */
	public int getStatementOffset() {
		return _iStatementOffset;
	}

	/**
	 * Returns the widget type identifier
	 */
	public String getTypeID(){
		return _strTypeID;
	}

	/**
	 * Returns the simple type name, such as "Button".
	 */
	public String getTypeName() {
		return _strTypeName;
	}

	/**
	 * Returns the widget's variable name as declared in the source code.
	 */
	public String getVariableName(){
		return _strVariableName;
	}
	
	/**
	 * Returns the widget's extra info.
	 */
	public String getExtraInfo( String name ) {
		String extraInfo = (String)getFromCache( name );
		if ( extraInfo != null ) {
			return extraInfo;
		}
		if ( _strExtraInfo == null )
			return null;

		int begin = _strExtraInfo.indexOf( name + "=" );
		if ( begin >= 0 ) {
			int end = _strExtraInfo.indexOf( ';', begin );
			extraInfo = _strExtraInfo.substring( begin + name.length() + 1, end );
			putIntoCache( name, extraInfo );
			return extraInfo;
		}
		return null;
	}
	
	/**
	 * Debug method that prints out the widget hierarchy with indentation. 
	 */
	public void printWidgetTree( int iIndentation ) {
		StringBuffer strb = new StringBuffer();
		for( int i = 0; i < iIndentation; ++i )
			strb.append( "  " );

		strb.append( toString() );
		System.out.println( strb.toString() );

		// Print out children indented more
		//---------------------------------
		for( Iterator iterChildren = _listChildren.iterator(); iterChildren.hasNext() == true; )
			( (WidgetPart)iterChildren.next() ).printWidgetTree( iIndentation + 1 );
	}

	/**
	 * Removes all child widgets.
	 */
	public void removeAllChildren() {
		_listChildren.clear();
	}

	/**
	 * Sets the position and size of the widget.
	 */
	public void setBounds( Rectangle rectBounds ) {
		_rectBounds.x = rectBounds.x;
		_rectBounds.y = rectBounds.y;
		_rectBounds.width = Math.max( rectBounds.width, MIN_PART_WIDTH );
		_rectBounds.height = Math.max( rectBounds.height, MIN_PART_HEIGHT );
	}

	/**
	 * Sets the location and size of a rectangle that is displayed adjacent to the mouse while the widget is being moved.
	 */
	public void setBoundsDragging( Rectangle rectDragging ) {
		_rectDragging.x = rectDragging.x;
		_rectDragging.y = rectDragging.y;
		_rectDragging.width = rectDragging.width;
		_rectDragging.height = rectDragging.height;
	}

	/**
	 * Sets the position of the rectangle that is displayed adjacent to the mouse while the widget is being moved.
	 */
	public void setBoundsDraggingOrigin( Point ptOrigin ) {
		_rectDragging.x = ptOrigin.x;
		_rectDragging.y = ptOrigin.y;
	}

	/**
	 * Sets the position of the widget.
	 */
	public void setBoundsOrigin( Point ptOrigin ) {
		_rectBounds.x = ptOrigin.x;
		_rectBounds.y = ptOrigin.y;
	}

	/**
	 * Sets whether the mouse is over the widget bounds.
	 */
	public void setMouseOver( boolean bMouseOver ) {
		_bMouseOver = bMouseOver;
	}
	
	/**
	 * Sets whether this widget can be relocated by dragging.
	 */
	public void setMoveable( boolean bMoveable ){
		_bMoveable = bMoveable;
	}

	/**
	 * Sets the widget's package name where the widget is declared.
	 */
	public void setPackageName( String strPackageName ){
		_strPackageName = strPackageName;
	}
	
	/**
	 * Sets the widget's parent widget.
	 */
	public void setParentWidget( WidgetPart widgetParent ) {
		_widgetParent = widgetParent;
	}

	/**
	 * Sets whether the widget is currently selected.
	 */
	public void setSelected( boolean bSelected ) {
		_bSelected = bSelected;
	}

	/**
	 * Stores the statement's length within the source document
	 */
	public void setStatementLength( int iStatementLength ) {
		this._iStatementLength = iStatementLength;
	}

	/**
	 * Stores the statement's starting character index location within the source document
	 */
	public void setStatementOffset( int iStatementOffset ) {
		_iStatementOffset = iStatementOffset;
	}

	/**
	 * Stores the statement's starting character index location and statement string length within the source document.
	 */
	public void setStatementPosition( int iStatementOffset, int iStatementLength ) {
		_iStatementOffset = iStatementOffset;
		_iStatementLength = iStatementLength;
	}

	/**
	 * Sets the widget type ID in the form Project@@Package@@TypeNmae, or Package@@TypeName.
	 */
	public void setTypeID( String strTypeID ) {
		_strTypeID = strTypeID;
	}

	/**
	 * Sets the simple type name, such as 'Button'.
	 */
	public void setTypeName( String strTypeName ) {
		_strTypeName = strTypeName;
	}

	/**
	 * Sets the name of this widget's declared variable name. 
	 */
	public void setVariableName( String strVariableName ){
		if ( strVariableName != null && strVariableName.startsWith( "eze$" ) ) {//this is an internal name, skip it.
			return;
		}
		_strVariableName = strVariableName;
	}
	
	/**
	 * Sets the widget extra info 
	 */
	public void setExtraInfo( String strExtraInfo ) {
		_strExtraInfo = strExtraInfo;
	}
	
	public boolean isSameWith( WidgetPart other ) {
		if ( other == null )
			return false;
		return _counter == other._counter;
	}
	
	public void putIntoCache( String key, Object value ) {
		_tempInfoCache.put( key, value );
	}
	
	public Object getFromCache( String key ) {
		return _tempInfoCache.get( key );
	}

	/**
	 * 
	 */
	public String toString() {
		StringBuffer strb = new StringBuffer();
		strb.append( "typeName[" );
		strb.append( _strTypeName );
		strb.append( "] typeID[" );
		strb.append( _strTypeID );
		strb.append( "] package[" );
		strb.append( _strPackageName );
		strb.append( "] varName[" );
		strb.append( _strVariableName );
		strb.append( "] statement[offset:" );
		strb.append( _iStatementOffset );
		strb.append( " length:" );
		strb.append( _iStatementLength );
		strb.append( "] bounds[x:" );
		strb.append( _rectBounds.x );
		strb.append( " y:" );
		strb.append( _rectBounds.y );
		strb.append( " w:" );
		strb.append( _rectBounds.width );
		strb.append( " h:" );
		strb.append( _rectBounds.height );
		strb.append( "] moveable[" );
		strb.append( _bMoveable );
		strb.append( "] extrainof[" );
		strb.append( _strExtraInfo );
		strb.append( "]" );

		return strb.toString();
	}
	
	// IBMBIDI Append Start
	/**
	 * 
	 */
	public boolean isBidiBoundsFixed(){
		return _bidiBoundsFixed;
	}
	
	/**
	 * 
	 */
	public void setBidiBoundsFixed(boolean b){
		_bidiBoundsFixed = b;
	}
	// IBMBIDI Append End
}
