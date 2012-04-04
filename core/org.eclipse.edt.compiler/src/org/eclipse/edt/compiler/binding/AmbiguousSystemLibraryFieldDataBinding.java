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

import java.util.Set;
import java.util.TreeSet;

/**
 * DataBinding subclass used to represent a field that exists in more than one
 * system library that is implicitly USEd. Should not be passed outside of the
 * edt.core project -- data accesses that resolve to this should have
 * IBinding.NOT_FOUND_BINDING set as their binding, after the getLibraryNames()
 * method is used to construct a useful error message.
 * 
 * @author Dave Murray
 */
public class AmbiguousSystemLibraryFieldDataBinding extends AmbiguousDataBinding {
	
	private Set allowedQualifiers = new TreeSet();
	
	public AmbiguousSystemLibraryFieldDataBinding() {
		super();
	}
	
	public int getKind() {
		return AMBIGUOUSSYSTEMLIBRARYFIELD_BINDING;
	}
	
	public void addAllowedQualifier(String libraryName) {
		allowedQualifiers.add(libraryName);
	}
	
	public Set getAllowedQualifiers() {
		return allowedQualifiers;
	}
	
	public boolean isValidBinding() {
		return false;
	}
}
