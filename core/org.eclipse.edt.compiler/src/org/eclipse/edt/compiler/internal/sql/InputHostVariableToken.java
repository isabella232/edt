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
 * Insert the type's description here. Creation date: (6/13/2001 1:59:50 PM)
 * 
 * @author: Paul R. Harmon
 */
public class InputHostVariableToken extends ItemNameToken {

    /**
     * InputHostVariableToken constructor comment.
     */
    public InputHostVariableToken() {
        super();
    }

    /**
     * InputHostVariableToken constructor comment.
     * 
     * @param string
     *            java.lang.String
     * @param sqlIOObject
     *            org.eclipse.edt.compiler.internal.compiler.parts.SQLIOObject
     */
    public InputHostVariableToken(String string, IDataBinding sqlIOObject) {
        super(string, sqlIOObject);
    }

    /**
     * Insert the method's description here. Creation date: (6/14/2001 12:12:24
     * PM)
     * 
     * @return boolean
     */
    public boolean isInputHostVariableToken() {
        return true;
    }
}
