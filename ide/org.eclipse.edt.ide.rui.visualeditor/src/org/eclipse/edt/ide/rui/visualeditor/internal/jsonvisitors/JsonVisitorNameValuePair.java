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
import org.eclipse.edt.javart.json.NameValuePairNode;
import org.eclipse.edt.javart.json.ObjectNode;
import org.eclipse.edt.javart.json.StringNode;

/**
 * A visitor for name value pairs of object nodes.  This visitor handles name value pairs for:
 * <ul>
 * <li>ele</li>
 * <li>children</li>
 * <li>a numeric digit</li>
 * </ul>
 */
public class JsonVisitorNameValuePair extends JsonVisitorAbstract {
	protected String		_strAttribute	= null;
	protected WidgetPart	_widgetPart		= null;

	public JsonVisitorNameValuePair( WidgetPart widgetPart, int iIndentation ) {
		super( iIndentation );
		
		_widgetPart = widgetPart;
	}

	/**
	 * Here for debug purposes 
	 */
	public void endVisit(NameValuePairNode object) {
		print( "endVisit NameValuePairNode" );
	}

	/**
	 * Here for debug purposes 
	 */
	public void endVisit(ObjectNode object) {
		print( "endVisit ObjectNode" );
	}

	/**
	 * Here for debug purposes 
	 */
	public void endVisit(StringNode object) {
		print( "endVisit StringNode" );
	}

	/**
	 * Ensure that the child name value pairs are visited.
	 */
	public boolean visit( NameValuePairNode pair ) throws RuntimeException {
		print( "visit NameValuePairNode" );
		return true;
	}

	/**
	 * Handles "ele" which indicates a widget part definition, or a digit which is a child index number
	 */
	public boolean visit( ObjectNode objectNode ) throws RuntimeException {
		print( "visit ObjectNode " + objectNode.toJava() );

		// Widget definition
		//------------------
		if( _strAttribute.equals( "ele" ) == true )
			objectNode.visitChildren( new JsonVisitorWidgetDefinition( _widgetPart, super._iIndentation + 1 ) );

		// If children are following, ensure we visit them
		//------------------------------------------------
		else if( _strAttribute.equals( "children" ) == true )
			return true;

		// Child index number
		// Create a new widget part and a visitor for the object
		//------------------------------------------------------
		else {
			try {
				Integer.parseInt( _strAttribute );

				WidgetPart widgetChild = new WidgetPart();
				widgetChild.setParentWidget( _widgetPart );
				_widgetPart.addChildWidget( widgetChild );

				// Create a new visitor for the this node
				//---------------------------------------
				objectNode.visitChildren( new JsonVisitor( widgetChild, super._iIndentation + 1 ) );
				return false;
			}
			catch( NumberFormatException ex ) {
			}
		}

		return false;
	}

	/**
	 * Save the attribute name so we know what to do when we handle the associated object node.
	 */
	public boolean visit( StringNode string ) throws RuntimeException {
		String strValue = string.getJavaValue();
		print( "visit StringNode " + strValue );

		_strAttribute = strValue;

		// String nodes have no children
		//------------------------------
		return false;
	}
}
