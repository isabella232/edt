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
public class EGLLineWrapKindEnumeration extends EGLEnumeration {
    
   private static final EGLLineWrapKindEnumeration INSTANCE = new EGLLineWrapKindEnumeration();
    
   public static final EGLEnumerationValue COMPRESS = new EGLEnumerationValue(IEGLConstants.MNEMONIC_COMPRESS, IEGLConstants.LINEWRAP_COMPRESS);
   public static final EGLEnumerationValue WORD = new EGLEnumerationValue(IEGLConstants.MNEMONIC_WORD, IEGLConstants.LINEWRAP_WORD);
   public static final EGLEnumerationValue CHARACTER = new EGLEnumerationValue(IEGLConstants.MNEMONIC_CHARACTER, IEGLConstants.LINEWRAP_CHARACTER);
       
   private static final HashMap valuesMap = new HashMap();
   
   static{
       valuesMap.put(COMPRESS.getName().toUpperCase().toLowerCase(), COMPRESS);
       valuesMap.put(WORD.getName().toUpperCase().toLowerCase(), WORD);
       valuesMap.put(CHARACTER.getName().toUpperCase().toLowerCase(), CHARACTER);
   }
   
   private EGLLineWrapKindEnumeration(){}
   
   public static EGLLineWrapKindEnumeration getInstance(){
       return INSTANCE;
   }
   
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getName()
     */
    public String getName() {
        return LINEWRAPKIND_STRING;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getType()
     */
    public int getType() {
        return IEGLConstants.ENUMERATION_LINEWRAPKIND;
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
