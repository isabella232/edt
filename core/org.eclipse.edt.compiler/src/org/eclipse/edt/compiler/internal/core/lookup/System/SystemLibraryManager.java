/*******************************************************************************
 * Copyright © 2008, 2010 IBM Corporation and others.
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
import org.eclipse.edt.compiler.binding.DataBindingWithImplicitQualifier;
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
    
    private static SystemLibraryManager INSTANCE = new SystemLibraryManager();
    
    public static SystemLibraryManager getInstance() {
        return INSTANCE;
    }
    
    private Map libraries = new HashMap();
    private Map libraryData = new HashMap();
    private Map libraryFunctions =  new HashMap();   

    private SystemLibraryManager() {
        super();
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
    
    public Map getHardCodedLibraries() {
            return initializeLibraries();
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
    
    private Map initializeLibraries() {    
        HashMap templibraries = new HashMap();
        
//        templibraries.put(ConsoleLib.LIBRARY.getName(),		ConsoleLib.LIBRARY);
//        templibraries.put(ConverseLib.LIBRARY.getName(),	ConverseLib.LIBRARY);
//        templibraries.put(ConverseVar.LIBRARY.getName(),	ConverseVar.LIBRARY);
//        templibraries.put(DateTimeLib.LIBRARY.getName(),	DateTimeLib.LIBRARY);
//        templibraries.put(DLILib.LIBRARY.getName(),			DLILib.LIBRARY);
//        templibraries.put(DLIVar.LIBRARY.getName(),			DLIVar.LIBRARY);
//        templibraries.put(JavaLib.LIBRARY.getName(),		JavaLib.LIBRARY);
//        templibraries.put(J2EELib.LIBRARY.getName(),		J2EELib.LIBRARY);
//        templibraries.put(LobLib.LIBRARY.getName(),			LobLib.LIBRARY);
//        templibraries.put(MathLib.LIBRARY.getName(),		MathLib.LIBRARY);
//        templibraries.put(ReportLib.LIBRARY.getName(),		ReportLib.LIBRARY);
//        templibraries.put(ServiceLib.LIBRARY.getName(),		ServiceLib.LIBRARY);
//        templibraries.put(StrLib.LIBRARY.getName(),			StrLib.LIBRARY);
//        templibraries.put(SysLib.LIBRARY.getName(),			SysLib.LIBRARY);
//        templibraries.put(SysVar.LIBRARY.getName(),			SysVar.LIBRARY);        
//        templibraries.put(VGLib.LIBRARY.getName(),			VGLib.LIBRARY);
//        templibraries.put(VGVar.LIBRARY.getName(),			VGVar.LIBRARY);
        return templibraries;
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
            putLibraryData( var.getName(), new DataBindingWithImplicitQualifier(var, library.getStaticLibraryDataBinding()));
        }
        
        i = library.getDeclaredFunctions().iterator();
        while (i.hasNext()) {
        	IDataBinding newFunction = (NestedFunctionBinding) i.next();
        	
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
            libraryFunctions.put(function.getName(), new FunctionBindingWithImplicitQualifier(function, library.getStaticLibraryDataBinding()));
        }
    }
}
