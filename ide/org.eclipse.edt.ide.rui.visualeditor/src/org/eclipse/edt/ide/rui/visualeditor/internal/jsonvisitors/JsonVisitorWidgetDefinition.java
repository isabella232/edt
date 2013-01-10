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
package org.eclipse.edt.ide.rui.visualeditor.internal.jsonvisitors;

import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;
import org.eclipse.edt.javart.json.IntegerNode;
import org.eclipse.edt.javart.json.NameValuePairNode;
import org.eclipse.edt.javart.json.ObjectNode;
import org.eclipse.edt.javart.json.StringNode;
import org.eclipse.swt.graphics.Rectangle;

/**
 * This visitor is used for "ele" object nodes that contains information about a widget part.
 * <code>
 * NameValuePairNode
 *   StringNode ele
 *   ObjectNode {type : HTML, height : 115, width : 500, offset : 4591, length : 7, x : 96, y : 246, package : packagename, moveable: y }
 *     NameValuePairNode
 *       StringNode type
 *       StringNode HTML
 *     NameValuePairNode
 *       StringNode height
 *       IntegerNode 115
 *     NameValuePairNode
 *       StringNode width
 *       IntegerNode 500
 *     NameValuePairNode
 *       StringNode offset
 *       StringNode 4591
 *     NameValuePairNode
 *       StringNode length
 *       StringNode 7
 *     NameValuePairNode
 *       StringNode x
 *       IntegerNode 96
 *     NameValuePairNode
 *       StringNode y
 *       IntegerNode 246
 *     NameValuePairNode
 *       StringNode varName
 *       StringNode variablename
 * </code>
 */
public class JsonVisitorWidgetDefinition extends JsonVisitorAbstract {
	protected String		_strAttribute	= null;
	protected WidgetPart	_widgetPart		= null;

	public JsonVisitorWidgetDefinition( WidgetPart widgetPart, int iIndentation ) {
		super( iIndentation );
		
		_widgetPart = widgetPart;
	}

	public void endVisit(IntegerNode object) {
		print( "endVisit IntegerNode" );
	}

	public void endVisit(NameValuePairNode object) {
		print( "endVisit NameValuePairNode" );
	}

	public void endVisit(ObjectNode object) {
		print( "endVisit ObjectNode" );
	}

	public void endVisit(StringNode object) {
		print( "endVisit StringNode" );
	}
	
	/**
	 * Looks for integer values of widget part attributes.
	 */
	public boolean visit( IntegerNode i ) throws RuntimeException {
		int iValue = i.getBigIntegerValue().intValue();

		print( "visit IntegerNode " + iValue );

		if( _strAttribute == null )
			return false;

		if( _strAttribute.equals( "offset" ) == true )
			_widgetPart.setStatementOffset( iValue );

		else if( _strAttribute.equals( "len" ) == true )
			_widgetPart.setStatementLength( iValue );

		else if( _strAttribute.equals( "x" ) == true ) {
			Rectangle rectBounds = _widgetPart.getBounds();
			rectBounds.x = iValue;
			_widgetPart.setBounds( rectBounds );
		}

		else if( _strAttribute.equals( "y" ) == true ) {
			Rectangle rectBounds = _widgetPart.getBounds();
			rectBounds.y = iValue;
			_widgetPart.setBounds( rectBounds );
		}

		else if( _strAttribute.equals( "width" ) == true ) {
			Rectangle rectBounds = _widgetPart.getBounds();
			rectBounds.width = iValue;
			_widgetPart.setBounds( rectBounds );
		}

		else if( _strAttribute.equals( "height" ) == true ) {
			Rectangle rectBounds = _widgetPart.getBounds();
			rectBounds.height = iValue;
			_widgetPart.setBounds( rectBounds );
		}

		_strAttribute = null;
		return false;
	}

	/**
	 * 
	 */
	public boolean visit( NameValuePairNode pair ) throws RuntimeException {
		print( "visit NameValuePairNode" );
		return true;
	}

	/**
	 * 
	 */
	public boolean visit( ObjectNode objectNode ) throws RuntimeException {
		print( "visit ObjectNode " + objectNode.toJava() );
		return true;
	}

	/**
	 * Looks for widget part attributes of type string.
	 * Note that sometimes, integer values appear here.
	 */
	public boolean visit( StringNode string ) throws RuntimeException {
		String strValue = string.getJavaValue();
		print( "visit StringNode " + strValue );

		if( _strAttribute == null ) {
			_strAttribute = strValue;
			return false;
		}

		if( _strAttribute.equals( "type" ) == true )
			_widgetPart.setTypeName( strValue );

		else if( _strAttribute.equals( "offset" ) == true )
			_widgetPart.setStatementOffset( Integer.parseInt( strValue ) );

		else if( _strAttribute.equals( "length" ) == true )
			_widgetPart.setStatementLength( Integer.parseInt( strValue ) );

		else if( _strAttribute.equals( "x" ) == true ) {
			Rectangle rectBounds = _widgetPart.getBounds();
			rectBounds.x = Integer.parseInt( strValue );
			_widgetPart.setBounds( rectBounds );
		}

		else if( _strAttribute.equals( "y" ) == true ) {
			Rectangle rectBounds = _widgetPart.getBounds();
			rectBounds.y = Integer.parseInt( strValue );
			_widgetPart.setBounds( rectBounds );
		}

		else if( _strAttribute.equals( "width" ) == true ) {
			Rectangle rectBounds = _widgetPart.getBounds();
			rectBounds.width = Integer.parseInt( strValue );
			_widgetPart.setBounds( rectBounds );
		}

		else if( _strAttribute.equals( "height" ) == true ) {
			Rectangle rectBounds = _widgetPart.getBounds();
			rectBounds.height = Integer.parseInt( strValue );
			_widgetPart.setBounds( rectBounds );
		}
		
		else if( _strAttribute.equals( "package" ) == true ){
			_widgetPart.setPackageName( strValue );
		}
		
		else if( _strAttribute.equals( "varname" ) == true ) {
			_widgetPart.setVariableName( strValue );
		}
		
		else if ( _strAttribute.equals( "moveable" ) == true ) // Either y or n
			_widgetPart.setMoveable( strValue.equals( "y" ) );

		else if( _strAttribute.equals( "extrainfo" ) == true ) // Not used
			_widgetPart.setExtraInfo( strValue );

		else if( _strAttribute.equals( "typeid" ) == true ) // Not used
			_widgetPart.setTypeID( strValue );

		_strAttribute = null;

		// String nodes have no children
		//------------------------------
		return false;
	}
}
