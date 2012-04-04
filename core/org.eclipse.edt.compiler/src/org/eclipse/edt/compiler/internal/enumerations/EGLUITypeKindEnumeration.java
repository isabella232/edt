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
public class EGLUITypeKindEnumeration extends EGLEnumeration {

    private static final EGLUITypeKindEnumeration INSTANCE = new EGLUITypeKindEnumeration();
    
    public static final EGLEnumerationValue UIFORM = new EGLEnumerationValue(IEGLConstants.MNEMONIC_UIFORM, IEGLConstants.UITYPEKIND_UIFORM);
    public static final EGLEnumerationValue HIDDEN = new EGLEnumerationValue(IEGLConstants.MNEMONIC_HIDDEN, IEGLConstants.UITYPEKIND_HIDDEN);
    public static final EGLEnumerationValue INPUT = new EGLEnumerationValue(IEGLConstants.MNEMONIC_INPUT, IEGLConstants.UITYPEKIND_INPUT);
    public static final EGLEnumerationValue INPUTOUTPUT = new EGLEnumerationValue(IEGLConstants.MNEMONIC_INPUTOUTPUT, IEGLConstants.UITYPEKIND_INPUTOUTPUT);
    public static final EGLEnumerationValue NONE = new EGLEnumerationValue(IEGLConstants.MNEMONIC_NONE, IEGLConstants.UITYPEKIND_NONE);
    public static final EGLEnumerationValue OUTPUT = new EGLEnumerationValue(IEGLConstants.MNEMONIC_OUTPUT, IEGLConstants.UITYPEKIND_OUTPUT);
    public static final EGLEnumerationValue PROGRAMLINK = new EGLEnumerationValue(IEGLConstants.MNEMONIC_PROGRAMLINK, IEGLConstants.UITYPEKIND_PROGRAMLINK);
    public static final EGLEnumerationValue SUBMIT = new EGLEnumerationValue(IEGLConstants.MNEMONIC_SUBMIT, IEGLConstants.UITYPEKIND_SUBMIT);
    public static final EGLEnumerationValue SUBMITBYPASS = new EGLEnumerationValue(IEGLConstants.MNEMONIC_SUBMITBYPASS, IEGLConstants.UITYPEKIND_SUBMITBYPASS);
    

    private static final HashMap valuesMap = new HashMap();
    
    static{
        valuesMap.put(UIFORM.getName().toUpperCase().toLowerCase(), UIFORM);
        valuesMap.put(HIDDEN.getName().toUpperCase().toLowerCase(), HIDDEN);
        valuesMap.put(INPUT.getName().toUpperCase().toLowerCase(), INPUT);
        valuesMap.put(INPUTOUTPUT.getName().toUpperCase().toLowerCase(), INPUTOUTPUT);
        valuesMap.put(NONE.getName().toUpperCase().toLowerCase(), NONE);
        valuesMap.put(OUTPUT.getName().toUpperCase().toLowerCase(), OUTPUT);
        valuesMap.put(PROGRAMLINK.getName().toUpperCase().toLowerCase(), PROGRAMLINK);
        valuesMap.put(SUBMIT.getName().toUpperCase().toLowerCase(), SUBMIT);
        valuesMap.put(SUBMITBYPASS.getName().toUpperCase().toLowerCase(), SUBMITBYPASS);
    }
    
    private EGLUITypeKindEnumeration() {}
    
    public static EGLUITypeKindEnumeration getInstance(){
        return INSTANCE;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getName()
     */
    public String getName() {
        return UITypeKind_STRING;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getType()
     */
    public int getType() {
        return IEGLConstants.ENUMERATION_UITYPEKIND;
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
