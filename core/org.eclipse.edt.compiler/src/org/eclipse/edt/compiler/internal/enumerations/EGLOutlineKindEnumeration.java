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
public class EGLOutlineKindEnumeration extends EGLEnumeration {

    private static final EGLOutlineKindEnumeration INSTANCE = new EGLOutlineKindEnumeration();
    
    public static final EGLEnumerationValue BOTTOM = new EGLEnumerationValue(IEGLConstants.MNEMONIC_BOTTOM, IEGLConstants.OUTLINEKIND_BOTTOM);
    public static final EGLEnumerationValue LEFT = new EGLEnumerationValue(IEGLConstants.MNEMONIC_LEFT, IEGLConstants.OUTLINEKIND_LEFT);
    public static final EGLEnumerationValue RIGHT = new EGLEnumerationValue(IEGLConstants.MNEMONIC_RIGHT, IEGLConstants.OUTLINEKIND_RIGHT);
    public static final EGLEnumerationValue TOP = new EGLEnumerationValue(IEGLConstants.MNEMONIC_TOP, IEGLConstants.OUTLINEKIND_TOP);

    private static final HashMap valuesMap = new HashMap();
    
    static{
        valuesMap.put(BOTTOM.getName().toUpperCase().toLowerCase(), BOTTOM);
        valuesMap.put(LEFT.getName().toUpperCase().toLowerCase(), LEFT);
        valuesMap.put(RIGHT.getName().toUpperCase().toLowerCase(), RIGHT);
        valuesMap.put(TOP.getName().toUpperCase().toLowerCase(), TOP);
    }
    
    private EGLOutlineKindEnumeration() {}
    
    public static EGLOutlineKindEnumeration getInstance(){
        return INSTANCE;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getName()
     */
    public String getName() {
        return OUTLINEKIND_STRING;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getType()
     */
    public int getType() {
        return IEGLConstants.ENUMERATION_OUTLINEKIND;
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
