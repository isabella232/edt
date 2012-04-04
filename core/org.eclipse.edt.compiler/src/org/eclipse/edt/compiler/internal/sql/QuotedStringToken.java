/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.sql;


/**
 * Insert the type's description here.
 * Creation date: (10/29/2001 2:40:46 PM)
 * @author: Yun Wilson
 */
public class QuotedStringToken extends StringToken {
/**
 * QuotedStringToken constructor comment.
 */
public QuotedStringToken() {
	super();
}
/**
 * QuotedStringToken constructor comment.
 * @param string java.lang.String
 */
public QuotedStringToken(String string) {
	super(string);
}

/**
 * Insert the method's description here.
 * 
 * @return boolean
 */
public boolean isNonQuotedStringToken() {
	return false;
}
/**
 * Insert the method's description here.
 * 
 * @return boolean
 */
public boolean isQuotedStringToken() {
	return true;
}
}
