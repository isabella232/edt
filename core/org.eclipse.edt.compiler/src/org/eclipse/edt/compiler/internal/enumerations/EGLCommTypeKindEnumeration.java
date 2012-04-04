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
public class EGLCommTypeKindEnumeration extends EGLEnumeration {

   private static final EGLCommTypeKindEnumeration INSTANCE = new EGLCommTypeKindEnumeration();
    
   public static final EGLEnumerationValue LOCAL = new EGLEnumerationValue(IEGLConstants.MNEMONIC_LOCAL, IEGLConstants.COMMTYPEKIND_LOCAL);
   public static final EGLEnumerationValue TCPIP = new EGLEnumerationValue(IEGLConstants.MNEMONIC_TCPIP, IEGLConstants.COMMTYPEKIND_TCPIP);
    
   private static final HashMap valuesMap = new HashMap();
   
   static{
       valuesMap.put(LOCAL.getName().toUpperCase().toLowerCase(), LOCAL);
       valuesMap.put(TCPIP.getName().toUpperCase().toLowerCase(), TCPIP);
   }
   
   private EGLCommTypeKindEnumeration(){}
   
   public static EGLCommTypeKindEnumeration getInstance(){
       return INSTANCE;
   }
   
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getName()
     */
    public String getName() {
       return COMMTYPEKIND_STRING;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getType()
     */
    public int getType() {
        return IEGLConstants.ENUMERATION_COMMTYPEKIND;
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
