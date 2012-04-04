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
public class EGLProtectKindEnumeration extends EGLEnumeration {

    private static final EGLProtectKindEnumeration INSTANCE = new EGLProtectKindEnumeration();
    
   public static final EGLEnumerationValue SKIP = new EGLEnumerationValue(IEGLConstants.MNEMONIC_SKIP, IEGLConstants.PROTECTKIND_SKIP);
   public static final EGLEnumerationValue NO = new EGLEnumerationValue(IEGLConstants.MNEMONIC_NO, IEGLConstants.PROTECTKIND_NO);
   public static final EGLEnumerationValue YES = new EGLEnumerationValue(IEGLConstants.MNEMONIC_YES, IEGLConstants.PROTECTKIND_YES);
       
   private static final HashMap valuesMap = new HashMap();
   
   static{
       valuesMap.put(SKIP.getName().toUpperCase().toLowerCase(), SKIP);
       valuesMap.put(NO.getName().toUpperCase().toLowerCase(), NO);
       valuesMap.put(YES.getName().toUpperCase().toLowerCase(), YES);
   }
   
   private EGLProtectKindEnumeration(){}
   
   public static EGLProtectKindEnumeration getInstance(){
       return INSTANCE;
   }
   
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getName()
     */
    public String getName() {
        return PROTECTKIND_STRING;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getType()
     */
    public int getType() {
        return IEGLConstants.ENUMERATION_PROPERTYKIND;
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
