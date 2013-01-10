/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package java_cup.runtime;

/**
 * Defines the Scanner interface, which CUP uses in the default
 * implementation of <code>lr_parser.scan()</code>.  Integration
 * of scanners implementing <code>Scanner</code> is facilitated.
 *
 * @version last updated 23-Jul-1999
 * @author David MacMahon <davidm@smartsc.com>
 */

/* *************************************************
  Interface Scanner
  
  Declares the next_token() method that should be
  implemented by scanners.  This method is typically
  called by lr_parser.scan().  End-of-file can be
  indicated either by returning
  <code>new Symbol(lr_parser.EOF_sym())</code> or
  <code>null</code>.
 ***************************************************/
public interface Scanner {
    /** Return the next token, or <code>null</code> on end-of-file. */
    public Symbol next_token() throws java.lang.Exception;
}
