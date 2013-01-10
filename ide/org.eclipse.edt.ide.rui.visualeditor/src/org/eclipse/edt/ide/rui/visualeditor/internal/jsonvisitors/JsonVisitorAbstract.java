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

import org.eclipse.edt.javart.json.DefaultJsonVisitor;

/**
 * Base class for all widget manager json visitors.
 * This class has methods for printing out debug strings.
 */
public class JsonVisitorAbstract extends DefaultJsonVisitor {
	protected int	_iIndentation	= 0;

	/**
	 * Constructor with zero indentation.
	 */
	protected JsonVisitorAbstract(){
	}
	
	/**
	 * Constructor with specified indentation.
	 */
	protected JsonVisitorAbstract( int iIndentation ){
		_iIndentation = iIndentation;
	}
	
	/**
	 * Add an indentation to the text by prepending spaces 
	 */
	private void indent( StringBuffer strb ) {
		if( false ) {
			for( int i = 0; i < _iIndentation; ++i )
				strb.append( "  " );
		}
	}

	/**
	 * Prints an indented debug string for visits
	 */
	protected void print( String string ) {
		if( false ) {
			if( string.startsWith( "visit" ) == true )
				_iIndentation++;

			if( string.startsWith( "end" ) == false ) {
				StringBuffer strb = new StringBuffer();
				indent( strb );
				strb.append( string.substring( 6 ) );
				System.out.println( strb.toString() );
			}

			else
				_iIndentation--;
		}
	}
}
