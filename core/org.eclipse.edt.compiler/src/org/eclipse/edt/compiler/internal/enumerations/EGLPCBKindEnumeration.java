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
public class EGLPCBKindEnumeration extends EGLEnumeration {

	private static final EGLPCBKindEnumeration INSTANCE = new EGLPCBKindEnumeration();
    
   public static final EGLEnumerationValue TP = new EGLEnumerationValue(IEGLConstants.MNEMONIC_TP, IEGLConstants.PCBKIND_TP);
   public static final EGLEnumerationValue DB = new EGLEnumerationValue(IEGLConstants.MNEMONIC_DB, IEGLConstants.PCBKIND_DB);
   public static final EGLEnumerationValue GSAM = new EGLEnumerationValue(IEGLConstants.MNEMONIC_GSAM, IEGLConstants.PCBKIND_GSAM);
    
   private static final HashMap valuesMap = new HashMap();
   
   static{
       valuesMap.put(TP.getName().toUpperCase().toLowerCase(), TP);
       valuesMap.put(DB.getName().toUpperCase().toLowerCase(), DB);
       valuesMap.put(GSAM.getName().toUpperCase().toLowerCase(), GSAM);
   }
	
    private EGLPCBKindEnumeration(){}
    
    public static EGLPCBKindEnumeration getInstance(){
        return INSTANCE;
    }
    
    /* (non-Javadoc)
	 * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getName()
	 */
	public String getName() {
		return PCBKIND_STRING;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getType()
	 */
	public int getType() {
		return IEGLConstants.ENUMERATION_PCBKIND;
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

	/* (non-Javadoc)
	 * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#isResolvable()
	 */
	public boolean isResolvable() {
		return false;
	}

}
