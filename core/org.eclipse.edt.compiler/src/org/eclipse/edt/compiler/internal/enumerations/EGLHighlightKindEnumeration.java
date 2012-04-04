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
public class EGLHighlightKindEnumeration extends EGLEnumeration {

    private static final EGLHighlightKindEnumeration INSTANCE = new EGLHighlightKindEnumeration();
    
    public static final EGLEnumerationValue BLINK = new EGLEnumerationValue(IEGLConstants.MNEMONIC_BLINK, IEGLConstants.HIGHLIGHTKIND_BLINK);
    public static final EGLEnumerationValue NOHIGHLIGHT = new EGLEnumerationValue(IEGLConstants.MNEMONIC_NOHIGHLIGHT, IEGLConstants.HIGHLIGHTKIND_NOHIGHIGHT);
    public static final EGLEnumerationValue REVERSE = new EGLEnumerationValue(IEGLConstants.MNEMONIC_REVERSE, IEGLConstants.HIGHLIGHTKIND_REVERSE);
    public static final EGLEnumerationValue UNDERLINE = new EGLEnumerationValue(IEGLConstants.MNEMONIC_UNDERLINE, IEGLConstants.HIGHLIGHTKIND_UNDERLINE);
    public static final EGLEnumerationValue DEFAULTHIGHLIGHT = new EGLEnumerationValue(IEGLConstants.MNEMONIC_DEFAULTHIGHLIGHT, IEGLConstants.HIGHLIGHTKIND_DEFAULTHIGHLIGHT);
  
    private static final HashMap valuesMap = new HashMap();
    
    static{
        valuesMap.put(BLINK.getName().toUpperCase().toLowerCase(), BLINK);
        valuesMap.put(NOHIGHLIGHT.getName().toUpperCase().toLowerCase(), NOHIGHLIGHT);
        valuesMap.put(REVERSE.getName().toUpperCase().toLowerCase(), REVERSE);
        valuesMap.put(UNDERLINE.getName().toUpperCase().toLowerCase(), UNDERLINE);
        valuesMap.put(DEFAULTHIGHLIGHT.getName().toUpperCase().toLowerCase(), DEFAULTHIGHLIGHT);
    }
    
    private EGLHighlightKindEnumeration() {}
    
    public static EGLHighlightKindEnumeration getInstance(){
        return INSTANCE;
    }
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getName()
     */
    public String getName() {
        return HIGHLIGHTKIND_STRING;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getType()
     */
    public int getType() {
        return IEGLConstants.ENUMERATION_HIGHLIGHTKIND;
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
