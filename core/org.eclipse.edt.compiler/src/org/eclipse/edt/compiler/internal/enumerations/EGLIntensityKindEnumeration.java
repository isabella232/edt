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
public class EGLIntensityKindEnumeration extends EGLEnumeration {

    private static final EGLIntensityKindEnumeration INSTANCE = new EGLIntensityKindEnumeration();
    
    public static final EGLEnumerationValue BOLD = new EGLEnumerationValue(IEGLConstants.MNEMONIC_BOLD, IEGLConstants.INTENSITY_BOLD);
    public static final EGLEnumerationValue DIM = new EGLEnumerationValue(IEGLConstants.MNEMONIC_DIM, IEGLConstants.INTENSITY_DIM);
    public static final EGLEnumerationValue INVISIBLE = new EGLEnumerationValue(IEGLConstants.MNEMONIC_INVISIBLE, IEGLConstants.INTENSITY_INVISIBLE);
    public static final EGLEnumerationValue NORMALINTENSITY = new EGLEnumerationValue(IEGLConstants.MNEMONIC_NORMALINTENSITY, IEGLConstants.INTENSITY_NORMALINTENSITY);
    public static final EGLEnumerationValue DEFAULTINTENSITY = new EGLEnumerationValue(IEGLConstants.MNEMONIC_DEFAULTINTENSITY, IEGLConstants.INTENSITY_DEFAULTINTENSITY);

    private static final HashMap valuesMap = new HashMap();
    
    static{
        valuesMap.put(BOLD.getName().toUpperCase().toLowerCase(), BOLD);
        valuesMap.put(DIM.getName().toUpperCase().toLowerCase(), DIM);
        valuesMap.put(INVISIBLE.getName().toUpperCase().toLowerCase(), INVISIBLE);
        valuesMap.put(NORMALINTENSITY.getName().toUpperCase().toLowerCase(), NORMALINTENSITY);
        valuesMap.put(DEFAULTINTENSITY.getName().toUpperCase().toLowerCase(), DEFAULTINTENSITY);
    }
    
    private EGLIntensityKindEnumeration() {
        super();
    }
    
    public static EGLIntensityKindEnumeration getInstance(){
        return INSTANCE;
    }
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getName()
     */
    public String getName() {
        return INTENSITYKIND_STRING;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getType()
     */
    public int getType() {
       return IEGLConstants.ENUMERATION_INTENSITYKIND;
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
