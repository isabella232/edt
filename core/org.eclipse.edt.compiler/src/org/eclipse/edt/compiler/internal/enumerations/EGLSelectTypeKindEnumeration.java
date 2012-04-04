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
package org.eclipse.edt.compiler.internal.enumerations;

import java.util.Collection;
import java.util.HashMap;

import org.eclipse.edt.compiler.internal.IEGLConstants;



/**
 * @author svihovec
 *
 */
public class EGLSelectTypeKindEnumeration extends EGLEnumeration {

    private static final EGLSelectTypeKindEnumeration INSTANCE = new EGLSelectTypeKindEnumeration();
    
    public static final EGLEnumerationValue INDEX = new EGLEnumerationValue(IEGLConstants.MNEMONIC_INDEX, IEGLConstants.SELECTTYPEKIND_INDEX);
    public static final EGLEnumerationValue VALUE = new EGLEnumerationValue(IEGLConstants.MNEMONIC_VALUE, IEGLConstants.SELECTTYPEKIND_VALUE);
    
    private static final HashMap valuesMap = new HashMap();
    
    static{
        valuesMap.put(INDEX.getName().toUpperCase().toLowerCase(), INDEX);
        valuesMap.put(VALUE.getName().toUpperCase().toLowerCase(), VALUE);
    }
    
    private EGLSelectTypeKindEnumeration() {}
    
    public static EGLSelectTypeKindEnumeration getInstance(){
        return INSTANCE;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getName()
     */
    public String getName() {
        return SELECTTYPEKIND_STRING;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getType()
     */
    public int getType() {
        return IEGLConstants.ENUMERATION_SELECTTYPEKIND;
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
		return false;
	}

}
