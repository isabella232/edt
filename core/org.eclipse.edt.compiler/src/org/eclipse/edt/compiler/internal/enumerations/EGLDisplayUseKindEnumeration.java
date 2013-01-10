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
public class EGLDisplayUseKindEnumeration extends EGLEnumeration {

    private static final EGLDisplayUseKindEnumeration INSTANCE = new EGLDisplayUseKindEnumeration();
    
    public static final EGLEnumerationValue BUTTON = new EGLEnumerationValue(IEGLConstants.MNEMONIC_BUTTON, IEGLConstants.DISPLAYUSEKIND_BUTTON);
    public static final EGLEnumerationValue HYPERLINK = new EGLEnumerationValue(IEGLConstants.MNEMONIC_HYPERLINK, IEGLConstants.DISPLAYUSEKIND_HYPERLINK);
    public static final EGLEnumerationValue INPUT = new EGLEnumerationValue(IEGLConstants.MNEMONIC_INPUT, IEGLConstants.DISPLAYUSEKIND_INPUT);
    public static final EGLEnumerationValue OUTPUT = new EGLEnumerationValue(IEGLConstants.MNEMONIC_OUTPUT, IEGLConstants.DISPLAYUSEKIND_OUTPUT);
    public static final EGLEnumerationValue SECRET = new EGLEnumerationValue(IEGLConstants.MNEMONIC_SECRET, IEGLConstants.DISPLAYUSEKIND_SECRET);
    public static final EGLEnumerationValue TABLE = new EGLEnumerationValue(IEGLConstants.MNEMONIC_TABLE, IEGLConstants.DISPLAYUSEKIND_TABLE);

    private static final HashMap valuesMap = new HashMap();
    
    static{
        valuesMap.put(BUTTON.getName().toUpperCase().toLowerCase(), BUTTON);
        valuesMap.put(HYPERLINK.getName().toUpperCase().toLowerCase(), HYPERLINK);
        valuesMap.put(INPUT.getName().toUpperCase().toLowerCase(), INPUT);
        valuesMap.put(OUTPUT.getName().toUpperCase().toLowerCase(), OUTPUT);
        valuesMap.put(SECRET.getName().toUpperCase().toLowerCase(), SECRET);
        valuesMap.put(TABLE.getName().toUpperCase().toLowerCase(), TABLE);
    }
    
    private EGLDisplayUseKindEnumeration() {}
    
    public static EGLDisplayUseKindEnumeration getInstance(){
        return INSTANCE;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getName()
     */
    public String getName() {
       return DISPLAYUSEKIND_STRING;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getType()
     */
    public int getType() {
       return IEGLConstants.ENUMERATION_DISPLAYUSEKIND;
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
