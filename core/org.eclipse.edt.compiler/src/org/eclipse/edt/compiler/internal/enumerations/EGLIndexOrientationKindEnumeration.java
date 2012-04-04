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
public class EGLIndexOrientationKindEnumeration extends EGLEnumeration {

   private static final EGLIndexOrientationKindEnumeration INSTANCE = new EGLIndexOrientationKindEnumeration();
    
   public static final EGLEnumerationValue ACROSS = new EGLEnumerationValue(IEGLConstants.MNEMONIC_ACROSS, IEGLConstants.INDEXORIENTATIONKIND_ACROSS);
   public static final EGLEnumerationValue DOWN = new EGLEnumerationValue(IEGLConstants.MNEMONIC_DOWN, IEGLConstants.INDEXORIENTATIONKIND_DOWN);
    
   private static final HashMap valuesMap = new HashMap();
   
   static{
       valuesMap.put(ACROSS.getName().toUpperCase().toLowerCase(), ACROSS);
       valuesMap.put(DOWN.getName().toUpperCase().toLowerCase(), DOWN);
   }
   
   private EGLIndexOrientationKindEnumeration(){}
   
   public static EGLIndexOrientationKindEnumeration getInstance(){
       return INSTANCE;
   }
   
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getName()
     */
    public String getName() {
       return INDEXORIENTATIONKIND_STRING;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getType()
     */
    public int getType() {
        return IEGLConstants.ENUMERATION_INDEXORIENTATIONKIND;
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
