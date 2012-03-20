/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.lookup.System;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.edt.compiler.binding.AmbiguousSystemLibraryFieldDataBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.DataBindingWithImplicitQualifier;
import org.eclipse.edt.compiler.binding.FunctionBinding;
import org.eclipse.edt.compiler.binding.FunctionBindingWithImplicitQualifier;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.LibraryBinding;
import org.eclipse.edt.compiler.binding.NestedFunctionBinding;
import org.eclipse.edt.compiler.binding.OverloadedFunctionSet;
import org.eclipse.edt.compiler.binding.VariableBinding;


/**
 * @author Harmon
 */
public class SystemLibraryManager {
    
    
    private Map libraries = new HashMap();
    private Map libraryData = new HashMap();
    private Map libraryFunctions =  new HashMap();   

    public SystemLibraryManager(SystemLibraryManager parentLib) {
        super();
        if (parentLib != null) {
        	libraries.putAll(parentLib.libraries);
        	libraryData.putAll(parentLib.libraryData);
        	libraryFunctions.putAll(parentLib.libraryFunctions);
        }
    }
    
    public ITypeBinding findType(String simpleName) {
        return (ITypeBinding) getLibraries().get(simpleName);
    }
    
    public IDataBinding findData(String simpleName) {
        return (IDataBinding) getLibraryData().get(simpleName);
    }
    
    public IFunctionBinding findFunction(String simpleName) {
        return (IFunctionBinding) getLibraryFunctions().get(simpleName);
    }
    
    public Map getLibraries() {
        return libraries;
    }
    
    private Map getLibraryData() {
         return libraryData;
    }

    private Map getLibraryFunctions() {
         return libraryFunctions;
    }
        
    public void addSystemLibrary(LibraryBinding libraryBinding){
    	libraries.put(libraryBinding.getName(),libraryBinding);
    	addLibraryData(libraryBinding);
    	addLibraryFunctions(libraryBinding);
    }
    
    
    private void addLibraryData(LibraryBinding library) {
        Iterator i = library.getClassFields().iterator();
        while (i.hasNext()) {
            VariableBinding var = (VariableBinding) i.next();
            if (!var.isPrivate()) {
            	putLibraryData( var.getName(), new DataBindingWithImplicitQualifier(var, library.getStaticLibraryDataBinding()));
            }
        }
        
        i = library.getDeclaredFunctions().iterator();
        while (i.hasNext()) {
        	IDataBinding newFunction = (NestedFunctionBinding) i.next();
        	if (Binding.isValidBinding(newFunction.getType()) && !((IFunctionBinding)newFunction.getType()).isPrivate()) {        	
	        	IDataBinding existingFunction = (IDataBinding) libraryData.get(newFunction.getName());
	        	if(existingFunction == null) {
	        		putLibraryData(newFunction.getName(),  new DataBindingWithImplicitQualifier(newFunction, library.getStaticLibraryDataBinding()));
	        	}
	        	else {
	        		OverloadedFunctionSet functionSet;
	        		existingFunction = ((DataBindingWithImplicitQualifier) existingFunction).getWrappedDataBinding();
	        		if(existingFunction instanceof OverloadedFunctionSet) {
	        			functionSet = (OverloadedFunctionSet) existingFunction;
	        		}
	        		else {
	        			functionSet = new OverloadedFunctionSet();
	        			functionSet.setName(newFunction.getCaseSensitiveName());
	        			functionSet.addNestedFunctionBinding(existingFunction);
	        		}
	        		functionSet.addNestedFunctionBinding(newFunction);
	        		
	        		putLibraryData(newFunction.getName(), new DataBindingWithImplicitQualifier(functionSet, library.getStaticLibraryDataBinding()));
	        	}
        	}
        }
    }

    private void putLibraryData(String name, IDataBinding dBinding) {
    	IDataBinding existingDBinding = (IDataBinding) libraryData.get(name);
    	if(existingDBinding == null || IDataBinding.OVERLOADED_FUNCTION_SET_BINDING == dBinding.getKind()) {
    		libraryData.put(name, dBinding);
    	}
    	else {
    		AmbiguousSystemLibraryFieldDataBinding ambigFieldBinding;
    		if(IDataBinding.AMBIGUOUSSYSTEMLIBRARYFIELD_BINDING == existingDBinding.getKind()) {
    			ambigFieldBinding = (AmbiguousSystemLibraryFieldDataBinding) existingDBinding;
    		}
    		else {
    			ambigFieldBinding = new AmbiguousSystemLibraryFieldDataBinding();
    			//CSTODO Should we pass in the case sensitive name here?
    			ambigFieldBinding.addAllowedQualifier(existingDBinding.getDeclaringPart().getName());
    		}
    		ambigFieldBinding.addAllowedQualifier(dBinding.getDeclaringPart().getName());
    		libraryData.put(name, ambigFieldBinding);
    	}
	}

	private void addLibraryFunctions(LibraryBinding library) {
        Iterator i = library.getDeclaredFunctions().iterator();
        while (i.hasNext()) {
        	IFunctionBinding function = (IFunctionBinding)((NestedFunctionBinding) i.next()).getType();
        	if (!function.isPrivate()) {
        		libraryFunctions.put(function.getName(), new FunctionBindingWithImplicitQualifier(function, library.getStaticLibraryDataBinding()));
        	}
        }
    }
}
