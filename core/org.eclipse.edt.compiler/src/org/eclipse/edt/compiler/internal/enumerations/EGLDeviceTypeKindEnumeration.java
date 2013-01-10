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
public class EGLDeviceTypeKindEnumeration extends EGLEnumeration{

    private static final EGLDeviceTypeKindEnumeration INSTANCE = new EGLDeviceTypeKindEnumeration();
    
    public static final EGLEnumerationValue SINGLE_BYTE = new EGLEnumerationValue(IEGLConstants.MNEMONIC_SINGLEBYTE, IEGLConstants.DEVICETYPEKIND_SINGLEBYTE);
    public static final EGLEnumerationValue DOUBLE_BYTE = new EGLEnumerationValue(IEGLConstants.MNEMONIC_DOUBLEBYTE, IEGLConstants.DEVICETYPEKIND_DOUBLEBYTE);

    private static final HashMap valuesMap = new HashMap();
    
    static{
        valuesMap.put(SINGLE_BYTE.getName().toUpperCase().toLowerCase(), SINGLE_BYTE);
        valuesMap.put(DOUBLE_BYTE.getName().toUpperCase().toLowerCase(), DOUBLE_BYTE);
    }
    
    private EGLDeviceTypeKindEnumeration() {
        super();
    }
    
    public static EGLDeviceTypeKindEnumeration getInstance(){
        return INSTANCE;
    }
    
    public String getName(){
        return DEVICETYPEKIND_STRING;
    }
    
    public int getType(){
        return IEGLConstants.ENUMERATION_DEVICETYPEKIND;
    }
    
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

