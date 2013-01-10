/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.enumerations;

import java.util.Collection;
import java.util.HashMap;

import org.eclipse.edt.compiler.internal.IEGLConstants;



/**
 * @author svihovec
 *
 */
public class EGLBooleanEnumeration extends EGLEnumeration {

    private static final EGLBooleanEnumeration INSTANCE = new EGLBooleanEnumeration();
    
    public static final EGLEnumerationValue YES = new EGLEnumerationValue(IEGLConstants.MNEMONIC_YES, IEGLConstants.BOOLEAN_YES);
    public static final EGLEnumerationValue NO = new EGLEnumerationValue(IEGLConstants.MNEMONIC_NO, IEGLConstants.BOOLEAN_NO);

    private static final HashMap valuesMap = new HashMap();
    
    static{
        valuesMap.put(YES.getName().toUpperCase().toLowerCase(), YES);
        valuesMap.put(NO.getName().toUpperCase().toLowerCase(), NO);
    }
    
    private EGLBooleanEnumeration() {}
    
    public static EGLBooleanEnumeration getInstance(){
        return INSTANCE;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getName()
     */
    public String getName() {
        return BOOLEAN_STRING;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getType()
     */
    public int getType() {
        return IEGLConstants.ENUMERATION_BOOLEAN;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getValue(java.lang.String)
     */
    public EGLEnumerationValue getValue(String valueName) {
        return (EGLEnumerationValue)valuesMap.get(valueName.toUpperCase().toLowerCase());
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getValues()
     */
    public Collection getValues() {
        return valuesMap.values();
    }
	public boolean isResolvable() {
		return true;
	}

}
