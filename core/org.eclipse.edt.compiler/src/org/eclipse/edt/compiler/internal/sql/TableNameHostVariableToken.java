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

import org.eclipse.edt.compiler.binding.IDataBinding;




/**
 * Insert the type's description here.
 * Creation date: (6/15/2001 2:57:00 PM)
 * @author: Paul R. Harmon
 */
public class TableNameHostVariableToken extends ItemNameToken {
/**
 * TableNameHostVariableToken constructor comment.
 */
public TableNameHostVariableToken() {
	super();
}
/**
 * TableNameHostVariableToken constructor comment.
 * @param string java.lang.String
 * @param sqlIOObject org.eclipse.edt.compiler.internal.compiler.parts.SQLIOObject
 */
public TableNameHostVariableToken(String string, IDataBinding sqlIOObject) {
	super(string, sqlIOObject);
}
/**
 * Insert the method's description here.
 * Creation date: (6/14/2001 12:12:24 PM)
 * @return boolean
 */
public boolean isTableNameHostVariableToken() {
	return true;
}
}
