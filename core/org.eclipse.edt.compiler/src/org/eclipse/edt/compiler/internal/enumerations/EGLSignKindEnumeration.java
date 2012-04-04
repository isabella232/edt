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
public class EGLSignKindEnumeration extends EGLEnumeration {

   private static final EGLSignKindEnumeration INSTANCE = new EGLSignKindEnumeration();
    
   public static final EGLEnumerationValue LEADING = new EGLEnumerationValue(IEGLConstants.MNEMONIC_LEADING, IEGLConstants.SIGNKIND_LEADING);
   public static final EGLEnumerationValue NONE = new EGLEnumerationValue(IEGLConstants.MNEMONIC_NONE, IEGLConstants.SIGNKIND_NONE);
   public static final EGLEnumerationValue PARENS = new EGLEnumerationValue(IEGLConstants.MNEMONIC_PARENS, IEGLConstants.SIGNKIND_PARENS);
   public static final EGLEnumerationValue TRAILING = new EGLEnumerationValue(IEGLConstants.MNEMONIC_TRAILING, IEGLConstants.SIGNKIND_TRAILING);
    
   private static final HashMap valuesMap = new HashMap();
   
   static{
       valuesMap.put(LEADING.getName().toUpperCase().toLowerCase(), LEADING);
       valuesMap.put(NONE.getName().toUpperCase().toLowerCase(), NONE);
       valuesMap.put(PARENS.getName().toUpperCase().toLowerCase(), PARENS);
       valuesMap.put(TRAILING.getName().toUpperCase().toLowerCase(), TRAILING);
   }
   
   private EGLSignKindEnumeration(){}
   
   public static EGLSignKindEnumeration getInstance(){
       return INSTANCE;
   }
   
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getName()
     */
    public String getName() {
       return SIGNKIND_STRING;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getType()
     */
    public int getType() {
        return IEGLConstants.ENUMERATION_SIGNKIND;
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
