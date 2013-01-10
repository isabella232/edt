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
public class EGLEventKindEnumeration extends EGLEnumeration {

    private static final EGLEventKindEnumeration INSTANCE = new EGLEventKindEnumeration();
    
    public static final EGLEnumerationValue AFTERDELETE = new EGLEnumerationValue(IEGLConstants.MNEMONIC_AFTER_DELETE, IEGLConstants.EVENTKIND_AFTERDELETE);
    public static final EGLEnumerationValue AFTERFIELD = new EGLEnumerationValue(IEGLConstants.MNEMONIC_AFTER_FIELD, IEGLConstants.EVENTKIND_AFTERFIELD);
    public static final EGLEnumerationValue AFTERINSERT = new EGLEnumerationValue(IEGLConstants.MNEMONIC_AFTER_INSERT, IEGLConstants.EVENTKIND_AFTERINSERT);
    public static final EGLEnumerationValue AFTEROPENUI = new EGLEnumerationValue(IEGLConstants.MNEMONIC_AFTER_OPENUI, IEGLConstants.EVENTKIND_AFTEROPENUI);
    public static final EGLEnumerationValue AFTERROW = new EGLEnumerationValue(IEGLConstants.MNEMONIC_AFTER_ROW, IEGLConstants.EVENTKIND_AFTERROW);
    public static final EGLEnumerationValue BEFOREDELETE = new EGLEnumerationValue(IEGLConstants.MNEMONIC_BEFORE_DELETE, IEGLConstants.EVENTKIND_BEFOREDELETE);
    public static final EGLEnumerationValue BEFOREFIELD = new EGLEnumerationValue(IEGLConstants.MNEMONIC_BEFORE_FIELD, IEGLConstants.EVENTKIND_BEFOREFIELD);
    public static final EGLEnumerationValue BEFOREINSERT = new EGLEnumerationValue(IEGLConstants.MNEMONIC_BEFORE_INSERT, IEGLConstants.EVENTKIND_BEFOREINSERT);
    public static final EGLEnumerationValue BEFOREOPENUI = new EGLEnumerationValue(IEGLConstants.MNEMONIC_BEFORE_OPENUI, IEGLConstants.EVENTKIND_BEFOREOPENUI);
    public static final EGLEnumerationValue BEFOREROW = new EGLEnumerationValue(IEGLConstants.MNEMONIC_BEFORE_ROW, IEGLConstants.EVENTKIND_BEFOREROW);
    public static final EGLEnumerationValue MENUACTION = new EGLEnumerationValue(IEGLConstants.MNEMONIC_MENU_ACTION, IEGLConstants.EVENTKIND_MENUACTION);
    public static final EGLEnumerationValue ONKEY = new EGLEnumerationValue(IEGLConstants.MNEMONIC_ON_KEY, IEGLConstants.EVENTKIND_ONKEY);

    private static final HashMap valuesMap = new HashMap();
    
    static{
        valuesMap.put(AFTERDELETE.getName().toUpperCase().toLowerCase(), AFTERDELETE);
        valuesMap.put(AFTERFIELD.getName().toUpperCase().toLowerCase(), AFTERFIELD);
        valuesMap.put(AFTERINSERT.getName().toUpperCase().toLowerCase(), AFTERINSERT);
        valuesMap.put(AFTEROPENUI.getName().toUpperCase().toLowerCase(), AFTEROPENUI);
        valuesMap.put(AFTERROW.getName().toUpperCase().toLowerCase(), AFTERROW);
        valuesMap.put(BEFOREDELETE.getName().toUpperCase().toLowerCase(), BEFOREDELETE);
        valuesMap.put(BEFOREFIELD.getName().toUpperCase().toLowerCase(), BEFOREFIELD);
        valuesMap.put(BEFOREINSERT.getName().toUpperCase().toLowerCase(), BEFOREINSERT);
        valuesMap.put(BEFOREOPENUI.getName().toUpperCase().toLowerCase(), BEFOREOPENUI);
        valuesMap.put(BEFOREROW.getName().toUpperCase().toLowerCase(), BEFOREROW);
        valuesMap.put(MENUACTION.getName().toUpperCase().toLowerCase(), MENUACTION);
        valuesMap.put(ONKEY.getName().toUpperCase().toLowerCase(), ONKEY);
    }
    
    private EGLEventKindEnumeration() {}
    
    public static EGLEventKindEnumeration getInstance(){
        return INSTANCE;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getName()
     */
    public String getName() {
        return EVENTKIND_STRING;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getType()
     */
    public int getType() {
        return IEGLConstants.ENUMERATION_EVENTKIND;
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
