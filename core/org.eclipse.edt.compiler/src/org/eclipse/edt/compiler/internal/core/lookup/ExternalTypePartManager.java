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

package org.eclipse.edt.compiler.internal.core.lookup;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.internal.IEGLConstants;

public class ExternalTypePartManager {
	private Map externalTypeLibraries = new HashMap();
	private Map externalTypeException = new HashMap();

	public ExternalTypePartManager(ExternalTypePartManager parent) {
		if (null != parent) {
			externalTypeLibraries.putAll(parent.externalTypeLibraries);
			externalTypeException.putAll(parent.externalTypeException);
		}
	}

	public Map getExternalTypeLibraries() {
		return externalTypeLibraries;
	}

	public void addExternalTypeLibrary(ExternalTypeBinding contentAssistPart) {
		externalTypeLibraries.put(contentAssistPart.getName(), contentAssistPart);
	}
	
	public Map getExternalTypeException(){
		return externalTypeException;
	}

	public void addExternalTypeException(ExternalTypeBinding exception){
		externalTypeException.put(exception.getName(), exception);
	}
	
	/*
	 * If a externalType extends AnyException, it will be considerate as a Exception type
	 */
	public static boolean isExceptionType(ExternalTypeBinding externalPart){
		if(externalPart.getBaseType().getName().equalsIgnoreCase("AnyException")){
			return true;
		}
		
		List extendedTypes = externalPart.getExtendedTypes();
		for (Iterator iterator = extendedTypes.iterator(); iterator.hasNext();) {
			ITypeBinding extendType = (ITypeBinding) iterator.next();

			if(extendType.getName().equalsIgnoreCase("AnyException")){
				return(true);
			}
		}
		
		return false;
	}
	
	public static boolean isLibraryType(ExternalTypeBinding externalPart){
		return (externalPart.containsStaticFunctions());
	}
	
}
