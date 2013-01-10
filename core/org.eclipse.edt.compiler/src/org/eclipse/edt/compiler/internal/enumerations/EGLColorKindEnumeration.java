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
public class EGLColorKindEnumeration extends EGLEnumeration {

    private static final EGLColorKindEnumeration INSTANCE = new EGLColorKindEnumeration();
    
    public static final EGLEnumerationValue BLACK = new EGLEnumerationValue(IEGLConstants.MNEMONIC_BLACK, IEGLConstants.COLORKIND_BLACK);
    public static final EGLEnumerationValue BLUE = new EGLEnumerationValue(IEGLConstants.MNEMONIC_BLUE, IEGLConstants.COLORKIND_BLUE);
    public static final EGLEnumerationValue CYAN = new EGLEnumerationValue(IEGLConstants.MNEMONIC_CYAN, IEGLConstants.COLORKIND_CYAN);
    public static final EGLEnumerationValue DEFAULTCOLOR = new EGLEnumerationValue(IEGLConstants.MNEMONIC_DEFAULTCOLOR, IEGLConstants.COLORKIND_DEFAULTCOLOR);
    public static final EGLEnumerationValue GREEN = new EGLEnumerationValue(IEGLConstants.MNEMONIC_GREEN, IEGLConstants.COLORKIND_GREEN);
    public static final EGLEnumerationValue MAGENTA = new EGLEnumerationValue(IEGLConstants.MNEMONIC_MAGENTA, IEGLConstants.COLORKIND_MAGENTA);
    public static final EGLEnumerationValue RED = new EGLEnumerationValue(IEGLConstants.MNEMONIC_RED, IEGLConstants.COLORKIND_RED);
    public static final EGLEnumerationValue WHITE = new EGLEnumerationValue(IEGLConstants.MNEMONIC_WHITE, IEGLConstants.COLORKIND_WHITE);
    public static final EGLEnumerationValue YELLOW = new EGLEnumerationValue(IEGLConstants.MNEMONIC_YELLOW, IEGLConstants.COLORKIND_YELLOW);
    

    private static final HashMap valuesMap = new HashMap();
    
    static{
        valuesMap.put(BLACK.getName().toUpperCase().toLowerCase(), BLACK);
        valuesMap.put(BLUE.getName().toUpperCase().toLowerCase(), BLUE);
        valuesMap.put(CYAN.getName().toUpperCase().toLowerCase(), CYAN);
        valuesMap.put(DEFAULTCOLOR.getName().toUpperCase().toLowerCase(), DEFAULTCOLOR);
        valuesMap.put(GREEN.getName().toUpperCase().toLowerCase(), GREEN);
        valuesMap.put(MAGENTA.getName().toUpperCase().toLowerCase(), MAGENTA);
        valuesMap.put(RED.getName().toUpperCase().toLowerCase(), RED);
        valuesMap.put(WHITE.getName().toUpperCase().toLowerCase(), WHITE);
        valuesMap.put(YELLOW.getName().toUpperCase().toLowerCase(), YELLOW);
    }
    
    private EGLColorKindEnumeration() {}
    
    public static EGLColorKindEnumeration getInstance(){
        return INSTANCE;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getName()
     */
    public String getName() {
        return COLORKIND_STRING;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getType()
     */
    public int getType() {
        return IEGLConstants.ENUMERATION_COLORKIND;
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
