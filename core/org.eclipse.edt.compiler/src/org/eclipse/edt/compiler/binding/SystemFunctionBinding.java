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
package org.eclipse.edt.compiler.binding;

/**
 * @author Harmon
 */
public class SystemFunctionBinding extends FunctionBinding {
	
	private int[] validNumbersOfArguments;
	private int systemFunctionType;
	private boolean hasMnemonicArguments = false;
	private LibraryBinding systemLibrary;

    /**
     * @param simpleName
     */
    public SystemFunctionBinding(String caseSensitiveInternedName, int systemFunctionType, LibraryBinding systemLibrary) {
        super(caseSensitiveInternedName, null);
        this.systemFunctionType = systemFunctionType;
        this.systemLibrary = systemLibrary;
    }
    
    public boolean isSystemFunction() {
        return true;
    }
    
    public int[] getValidNumbersOfArguments() {
        return validNumbersOfArguments == null ? super.getValidNumbersOfArguments() : validNumbersOfArguments;
    }
    
	public boolean hasMnemonicArguments() {
		return hasMnemonicArguments;
	}
	
	public void setHasMnemonicArguments(boolean b) {
		hasMnemonicArguments = b;
	}
    
    public void setValidNumbersOfArguemnts(int[] validNumbersOfArguments) {
    	this.validNumbersOfArguments = validNumbersOfArguments;
    }
    
	public int getSystemFunctionType() {
		return systemFunctionType;
	}
	
	public LibraryBinding getSystemLibrary() {
		return systemLibrary;
	}
}
