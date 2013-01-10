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
package org.eclipse.edt.compiler.internal;

/**
 * @author svihovec
 *
 */
public abstract class EGLLib {

    private static final String LIBRARY_PACKAGE = IEGLConstants.EGL_CORE_PACKAGE;
    
    public static class EGLLibConstant{
        private String constantName;
        
        public EGLLibConstant(String name){
            this.constantName = name;
        }
        
        public String getName(){
            return constantName;
        }
    }
    
    public abstract EGLLibConstant getConstant(String name);
    
    /**
     * Given a string reference to a strlib constant, attempt to locate the appropriate constant.
     * 
     * A constant will be located if it the string contains only the name of the constant (i.e. "isoDateFormat"),
     * if it is qualified with the library name (i.e. "strlib.isoDateFormat"), or if it is qualified with the
     * package and library name (i.e. "egl.core.strlib.isoDateFormat").  
     * 
     * All comparisons are case insensitive.
     *  
     * @param resolveString
     * @return null if a constant could not be resolved
     */
    public EGLLibConstant resolve(String libraryName, String resolveString){
        
        EGLLibConstant foundConstant = null;
        boolean shouldResolveString = false;
        String tempResolveString = resolveString;
        
        // If it's qualified, verify that the qualifier is egl.core.strlib or strlib
        if(resolveString.indexOf(IEGLConstants.PACKAGE_SEPARATOR) != -1){
            int lastSeparatorIndex = resolveString.lastIndexOf(IEGLConstants.PACKAGE_SEPARATOR);
            
            String qualifier = resolveString.substring(0, lastSeparatorIndex);
            
            if(qualifier.equalsIgnoreCase(libraryName)
                    || qualifier.equalsIgnoreCase(LIBRARY_PACKAGE + IEGLConstants.PACKAGE_SEPARATOR + libraryName)){
                
                shouldResolveString = true;
                tempResolveString = resolveString.substring(lastSeparatorIndex + 1);
            }
        }else{
            shouldResolveString = true;
            tempResolveString = resolveString;
        }
        
        if(shouldResolveString){
            foundConstant = getConstant(tempResolveString);
	    }
        
        return foundConstant;
    }
    
}
