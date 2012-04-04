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
 * @author Dave Murray
 */
public class LibraryBinding extends FunctionContainerBinding {
	
	transient private LibraryDataBinding staticLibraryDataBinding;

    public LibraryBinding(String[] packageName, String caseSensitiveInternedName) {
        this(packageName, caseSensitiveInternedName, false);
    }
    
    public LibraryBinding(String[] packageName, String caseSensitiveInternedName, boolean isSystemLibrary) {
        super(packageName, caseSensitiveInternedName);
        isValid = isSystemLibrary;
    }

	public void clear() {
		super.clear();
	}

	public boolean isStructurallyEqual(IPartBinding anotherPartBinding) {
		// TODO Auto-generated method stub
		return false;
	}

	public int getKind() {
		return LIBRARY_BINDING;
	}
	
	public IDataBinding getStaticLibraryDataBinding() {
		if(staticLibraryDataBinding == null) {
			staticLibraryDataBinding = new LibraryDataBinding(getCaseSensitiveName(), this, this);
		}
		return staticLibraryDataBinding;
	}
	public StaticPartDataBinding getStaticPartDataBinding() {
		return (StaticPartDataBinding)getStaticLibraryDataBinding();
	}
	
	@Override
	public ITypeBinding primGetNullableInstance() {
		return this;
	}
	
}
