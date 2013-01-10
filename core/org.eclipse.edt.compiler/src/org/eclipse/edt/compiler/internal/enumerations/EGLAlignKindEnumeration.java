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
public class EGLAlignKindEnumeration extends EGLEnumeration{

   private static final EGLAlignKindEnumeration INSTANCE = new EGLAlignKindEnumeration();
    
   public static final EGLEnumerationValue CENTER = new EGLEnumerationValue(IEGLConstants.MNEMONIC_CENTER, IEGLConstants.ALIGN_CENTER);
   public static final EGLEnumerationValue LEFT = new EGLEnumerationValue(IEGLConstants.MNEMONIC_LEFT, IEGLConstants.ALIGN_LEFT);
   public static final EGLEnumerationValue RIGHT = new EGLEnumerationValue(IEGLConstants.MNEMONIC_RIGHT, IEGLConstants.ALIGN_RIGHT);
   public static final EGLEnumerationValue NONE = new EGLEnumerationValue(IEGLConstants.MNEMONIC_NONE, IEGLConstants.ALIGN_NONE);
    
   private static final HashMap valuesMap = new HashMap();
   
   static{
       valuesMap.put(CENTER.getName().toUpperCase().toLowerCase(), CENTER);
       valuesMap.put(LEFT.getName().toUpperCase().toLowerCase(), LEFT);
       valuesMap.put(RIGHT.getName().toUpperCase().toLowerCase(), RIGHT);
       valuesMap.put(NONE.getName().toUpperCase().toLowerCase(), NONE);
   }
   
   private EGLAlignKindEnumeration(){}
   
   public static EGLAlignKindEnumeration getInstance(){
       return INSTANCE;
   }
   
   public String getName(){
       return ALIGNKIND_STRING;
   }
   
   public int getType(){
       return IEGLConstants.ENUMERATION_ALIGNKIND;
   }
   
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
