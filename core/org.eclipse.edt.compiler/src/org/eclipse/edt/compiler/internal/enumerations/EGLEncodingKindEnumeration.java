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
public class EGLEncodingKindEnumeration extends EGLEnumeration {

    private static final EGLEncodingKindEnumeration INSTANCE = new EGLEncodingKindEnumeration();
    
    public static final EGLEnumerationValue XML = new EGLEnumerationValue(IEGLConstants.MNEMONIC_XML, IEGLConstants.ENCODINGKIND_XML);
    public static final EGLEnumerationValue JSON = new EGLEnumerationValue(IEGLConstants.MNEMONIC_JSON, IEGLConstants.ENCODINGKIND_JSON);
    

    private static final HashMap valuesMap = new HashMap();
    
    static{
        valuesMap.put(XML.getName().toUpperCase().toLowerCase(), XML);
        valuesMap.put(JSON.getName().toUpperCase().toLowerCase(), JSON);
    }
    
    private EGLEncodingKindEnumeration() {}
    
    public static EGLEncodingKindEnumeration getInstance(){
        return INSTANCE;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getName()
     */
    public String getName() {
        return ENCODINGKIND_STRING;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getType()
     */
    public int getType() {
        return IEGLConstants.ENUMERATION_ENCODINGKIND;
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
