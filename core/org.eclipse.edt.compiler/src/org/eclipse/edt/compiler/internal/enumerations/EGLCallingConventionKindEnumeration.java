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
public class EGLCallingConventionKindEnumeration extends EGLEnumeration {

    private static final EGLCallingConventionKindEnumeration INSTANCE = new EGLCallingConventionKindEnumeration();
    
   public static final EGLEnumerationValue I4GL = new EGLEnumerationValue(IEGLConstants.MNEMONIC_I4GL, IEGLConstants.CALLINGCONVENTION_I4GL);
   public static final EGLEnumerationValue LIBRARY = new EGLEnumerationValue(IEGLConstants.MNEMONIC_LIBRARY, IEGLConstants.CALLINGCONVENTION_LIBRARY);
    
   private static final HashMap valuesMap = new HashMap();
   
   static{
       valuesMap.put(I4GL.getName().toUpperCase().toLowerCase(), I4GL);
       valuesMap.put(LIBRARY.getName().toUpperCase().toLowerCase(), LIBRARY);
   }
   
   private EGLCallingConventionKindEnumeration(){}
   
   public static EGLCallingConventionKindEnumeration getInstance(){
       return INSTANCE;
   }
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getName()
     */
    public String getName() {
        return CALLINGCONVENTIONKIND_STRING;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getType()
     */
    public int getType() {
        return IEGLConstants.ENUMERATION_CALLINGCONVENTIONKIND;
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
