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
public class EGLDLICallInterfaceKindEnumeration extends EGLEnumeration {

	private static final EGLDLICallInterfaceKindEnumeration INSTANCE = new EGLDLICallInterfaceKindEnumeration();
    
   public static final EGLEnumerationValue AIBTDLI = new EGLEnumerationValue(IEGLConstants.MNEMONIC_AIBTDLI, IEGLConstants.DLICALLKIND_AIBTDLI);
   public static final EGLEnumerationValue CBLTDLI = new EGLEnumerationValue(IEGLConstants.MNEMONIC_CBLTDLI, IEGLConstants.DLICALLKIND_CBLTDLI);
    
   private static final HashMap valuesMap = new HashMap();
   
   static{
       valuesMap.put(AIBTDLI.getName().toUpperCase().toLowerCase(), AIBTDLI);
       valuesMap.put(CBLTDLI.getName().toUpperCase().toLowerCase(), CBLTDLI);
   }
	
    private EGLDLICallInterfaceKindEnumeration(){}
    
    public static EGLDLICallInterfaceKindEnumeration getInstance(){
        return INSTANCE;
    }
    
    /* (non-Javadoc)
	 * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getName()
	 */
	public String getName() {
		return DLICALLINTERFACEKIND_STRING;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getType()
	 */
	public int getType() {
		return IEGLConstants.ENUMERATION_DLICALLKIND;
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
