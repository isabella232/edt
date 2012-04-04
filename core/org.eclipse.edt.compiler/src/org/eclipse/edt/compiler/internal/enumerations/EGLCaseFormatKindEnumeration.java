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
public class EGLCaseFormatKindEnumeration extends EGLEnumeration {

    private static final EGLCaseFormatKindEnumeration INSTANCE = new EGLCaseFormatKindEnumeration();
    
    public static final EGLEnumerationValue DEFAULTCASE = new EGLEnumerationValue(IEGLConstants.MNEMONIC_DEFAULTCASE, IEGLConstants.CASEFORMAT_DEFAULTCASE);
    public static final EGLEnumerationValue LOWER = new EGLEnumerationValue(IEGLConstants.MNEMONIC_LOWER, IEGLConstants.CASEFORMAT_LOWER);
    public static final EGLEnumerationValue UPPER = new EGLEnumerationValue(IEGLConstants.MNEMONIC_UPPER, IEGLConstants.CASEFORMAT_UPPER);
  
    private static final HashMap valuesMap = new HashMap();
    
    static{
        valuesMap.put(DEFAULTCASE.getName().toUpperCase().toLowerCase(), DEFAULTCASE);
        valuesMap.put(LOWER.getName().toUpperCase().toLowerCase(), LOWER);
        valuesMap.put(UPPER.getName().toUpperCase().toLowerCase(), UPPER);
    }
    
    private EGLCaseFormatKindEnumeration() {}
    
    public static EGLCaseFormatKindEnumeration getInstance(){
        return INSTANCE;
    }
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getName()
     */
    public String getName() {
        return CASEFORMATKIND_STRING;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getType()
     */
    public int getType() {
        return IEGLConstants.ENUMERATION_CASEFORMATKIND;
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
