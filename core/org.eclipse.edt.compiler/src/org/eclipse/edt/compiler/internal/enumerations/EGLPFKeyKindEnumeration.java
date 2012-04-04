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
public class EGLPFKeyKindEnumeration extends EGLEnumeration {

   private static final EGLPFKeyKindEnumeration INSTANCE = new EGLPFKeyKindEnumeration();
    
   public static final EGLEnumerationValue PF1 = new EGLEnumerationValue("PF1", IEGLConstants.PFKEYKIND_PF1);
   public static final EGLEnumerationValue PF2 = new EGLEnumerationValue("PF2", IEGLConstants.PFKEYKIND_PF2);
   public static final EGLEnumerationValue PF3 = new EGLEnumerationValue("PF3", IEGLConstants.PFKEYKIND_PF3);
   public static final EGLEnumerationValue PF4 = new EGLEnumerationValue("PF4", IEGLConstants.PFKEYKIND_PF4);
   public static final EGLEnumerationValue PF5 = new EGLEnumerationValue("PF5", IEGLConstants.PFKEYKIND_PF5);
   public static final EGLEnumerationValue PF6 = new EGLEnumerationValue("PF6", IEGLConstants.PFKEYKIND_PF6);
   public static final EGLEnumerationValue PF7 = new EGLEnumerationValue("PF7", IEGLConstants.PFKEYKIND_PF7);
   public static final EGLEnumerationValue PF8 = new EGLEnumerationValue("PF8", IEGLConstants.PFKEYKIND_PF8);
   public static final EGLEnumerationValue PF9 = new EGLEnumerationValue("PF9", IEGLConstants.PFKEYKIND_PF9);
   public static final EGLEnumerationValue PF10 = new EGLEnumerationValue("PF10", IEGLConstants.PFKEYKIND_PF10);
   public static final EGLEnumerationValue PF11 = new EGLEnumerationValue("PF11", IEGLConstants.PFKEYKIND_PF11);
   public static final EGLEnumerationValue PF12 = new EGLEnumerationValue("PF12", IEGLConstants.PFKEYKIND_PF12);
   public static final EGLEnumerationValue PF13 = new EGLEnumerationValue("PF13", IEGLConstants.PFKEYKIND_PF13);
   public static final EGLEnumerationValue PF14 = new EGLEnumerationValue("PF14", IEGLConstants.PFKEYKIND_PF14);
   public static final EGLEnumerationValue PF15 = new EGLEnumerationValue("PF15", IEGLConstants.PFKEYKIND_PF15);
   public static final EGLEnumerationValue PF16 = new EGLEnumerationValue("PF16", IEGLConstants.PFKEYKIND_PF16);
   public static final EGLEnumerationValue PF17 = new EGLEnumerationValue("PF17", IEGLConstants.PFKEYKIND_PF17);
   public static final EGLEnumerationValue PF18 = new EGLEnumerationValue("PF18", IEGLConstants.PFKEYKIND_PF18);
   public static final EGLEnumerationValue PF19 = new EGLEnumerationValue("PF19", IEGLConstants.PFKEYKIND_PF19);
   public static final EGLEnumerationValue PF20 = new EGLEnumerationValue("PF20", IEGLConstants.PFKEYKIND_PF20);
   public static final EGLEnumerationValue PF21 = new EGLEnumerationValue("PF21", IEGLConstants.PFKEYKIND_PF21);
   public static final EGLEnumerationValue PF22 = new EGLEnumerationValue("PF22", IEGLConstants.PFKEYKIND_PF22);
   public static final EGLEnumerationValue PF23 = new EGLEnumerationValue("PF23", IEGLConstants.PFKEYKIND_PF23);
   public static final EGLEnumerationValue PF24 = new EGLEnumerationValue("PF24", IEGLConstants.PFKEYKIND_PF24);
 
   private static final HashMap valuesMap = new HashMap();
   
   static{
       valuesMap.put(PF1.getName().toUpperCase().toLowerCase(), PF1);
       valuesMap.put(PF2.getName().toUpperCase().toLowerCase(), PF2);
       valuesMap.put(PF3.getName().toUpperCase().toLowerCase(), PF3);
       valuesMap.put(PF4.getName().toUpperCase().toLowerCase(), PF4);
       valuesMap.put(PF5.getName().toUpperCase().toLowerCase(), PF5);
       valuesMap.put(PF6.getName().toUpperCase().toLowerCase(), PF6);
       valuesMap.put(PF7.getName().toUpperCase().toLowerCase(), PF7);
       valuesMap.put(PF8.getName().toUpperCase().toLowerCase(), PF8);
       valuesMap.put(PF9.getName().toUpperCase().toLowerCase(), PF9);
       valuesMap.put(PF10.getName().toUpperCase().toLowerCase(), PF10);
       valuesMap.put(PF11.getName().toUpperCase().toLowerCase(), PF11);
       valuesMap.put(PF12.getName().toUpperCase().toLowerCase(), PF12);
       valuesMap.put(PF13.getName().toUpperCase().toLowerCase(), PF13);
       valuesMap.put(PF14.getName().toUpperCase().toLowerCase(), PF14);
       valuesMap.put(PF15.getName().toUpperCase().toLowerCase(), PF15);
       valuesMap.put(PF16.getName().toUpperCase().toLowerCase(), PF16);
       valuesMap.put(PF17.getName().toUpperCase().toLowerCase(), PF17);
       valuesMap.put(PF18.getName().toUpperCase().toLowerCase(), PF18);
       valuesMap.put(PF19.getName().toUpperCase().toLowerCase(), PF19);
       valuesMap.put(PF20.getName().toUpperCase().toLowerCase(), PF20);
       valuesMap.put(PF21.getName().toUpperCase().toLowerCase(), PF21);
       valuesMap.put(PF22.getName().toUpperCase().toLowerCase(), PF22);
       valuesMap.put(PF23.getName().toUpperCase().toLowerCase(), PF23);
       valuesMap.put(PF24.getName().toUpperCase().toLowerCase(), PF24);
   }
   
   private EGLPFKeyKindEnumeration(){}
   
   public static EGLPFKeyKindEnumeration getInstance(){
       return INSTANCE;
   }
   
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getName()
     */
    public String getName() {
        return PFKEYKIND_STRING;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration#getType()
     */
    public int getType() {
       return IEGLConstants.ENUMERATION_PFKEYKIND;
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
