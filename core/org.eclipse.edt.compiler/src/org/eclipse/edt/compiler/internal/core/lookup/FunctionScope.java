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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.edt.compiler.binding.BindingUtilities;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.LocalVariableBinding;


/**
 * @author winghong
 */
public class FunctionScope extends Scope implements ILocalVariableScope {
    
//	private FunctionContainerScope parentScope;
	
	private Map localVariables = Collections.EMPTY_MAP;
    private Set ioObjects = Collections.EMPTY_SET;
    private List locallyDefinedRecords;
    
    private Map level2Items;
    
    protected Set declaredDataNames = new HashSet();
    
    // For 'usingKeys' resolution rule:
    private List dataBindingsToIgnore = Collections.EMPTY_LIST;
    
    public FunctionScope(Scope parentScope, IFunctionBinding functionBinding) {
        this(parentScope);
        
        // Add function parameters to localVariables collection
        List functionParameters = functionBinding.getParameters();
        if(functionParameters != Collections.EMPTY_LIST) {
        	localVariables = new HashMap();
	        for(Iterator iter = functionParameters.iterator(); iter.hasNext();) {
	        	IDataBinding nextParm = (IDataBinding) iter.next();
	        	localVariables.put(nextParm.getName(), nextParm);
	        }
        }
    }
    
    protected FunctionScope(Scope parentScope) {
    	super(parentScope);
    }

    public IDataBinding findData(String simpleName) {
    	/* 
    	 * 1) Search for constant, item or record in the local storage or
    	 *    parameter list of the function.
    	 */
        IDataBinding result = (IDataBinding)localVariables.get(simpleName);
        if(dataBindingsToIgnore.contains(result)) {
    		result = null;
    	}
        if(result != null) return result;
        
        /* 
         * 2) *Search for structure item contained in Record in local storage
         *        or parameter list of function.
         *     Search for record/form used in an I/O statement in the function.
         *     *Search for structure item/field in I/O record/form used by the
         *        function
         *     *Search for "resourceAssociation" in I/O record used by the
         *        function (note: special rules if more than 1 found!)
         *
         *  * if allowUnqualifiedItemReferences = yes
         */
        result = (IDataBinding) getLevel2Items().get(simpleName);        
    	if(result != null) return result;
        
        return parentScope.findData(simpleName);
    }
    
    public IDataBinding findIOTargetData(String simpleName) {
    	/* 
    	 * 1) Attempt to find a record with the given name in the local storage
    	 *    and parameter list for the function.
    	 */
    	IDataBinding result = (IDataBinding)localVariables.get(simpleName);
    	if(result != null) {
	    	int resultTypeKind = result.getType().getBaseType().getKind();
	    	if(resultTypeKind == ITypeBinding.FIXED_RECORD_BINDING ||
	    	   resultTypeKind == ITypeBinding.FLEXIBLE_RECORD_BINDING) {
	    		return result;
	    	}
    	}
    	
    	/*
    	 * 2) Attempt to find a record with the given name in the global storage
    	 *      and parameter list for the program/pageHandler/library.
    	 *    Attempt to find a form with the given name in the "main" formGroup
    	 *      for the  program
    	 */    	
    	return parentScope.findIOTargetData(simpleName);
    }

    public IFunctionBinding findFunction(String simpleName) {
        return parentScope.findFunction(simpleName);
    }

    public IPackageBinding findPackage(String simpleName) {
        return parentScope.findPackage(simpleName);
    }

    public ITypeBinding findType(String simpleName) {
        return parentScope.findType(simpleName);
    }
    
    /* (non-Javadoc)
	 * @see org.eclipse.edt.compiler.internal.core.lookup.ILocalVariableScope#addLocalVariable(org.eclipse.edt.compiler.binding.LocalVariableBinding)
	 */
    public void addLocalVariable(LocalVariableBinding var) {
        if (localVariables == Collections.EMPTY_MAP) {
            localVariables = new HashMap();
        }
        localVariables.put(var.getName(), var);
        
        locallyDefinedRecords = null;
        level2Items = null;
    }
    
    public void addIOObject(IDataBinding ioObjectDataBinding) {
    	if (ioObjects == Collections.EMPTY_SET) {
    		ioObjects = new HashSet();
    	}
    	ioObjects.add(ioObjectDataBinding);
    }
    
    private List getLocallyDefinedRecords() {
    	if(locallyDefinedRecords == null) {
    		locallyDefinedRecords = new ArrayList();
    		for(Iterator iter = localVariables.values().iterator(); iter.hasNext();) {
    			IDataBinding nextVar = (IDataBinding) iter.next();
    			int nextVarTypeKind = nextVar.getType().getKind();
    			if(nextVarTypeKind == ITypeBinding.FLEXIBLE_RECORD_BINDING ||
    			   nextVarTypeKind == ITypeBinding.FIXED_RECORD_BINDING ||
				   nextVarTypeKind == ITypeBinding.ARRAY_TYPE_BINDING &&
				   nextVar.getType().getBaseType().getKind() == ITypeBinding.FIXED_RECORD_BINDING) {
    				locallyDefinedRecords.add(nextVar);
    			}
    					
    		}
    	}
    	return locallyDefinedRecords;
    }
    
    private Map getLevel2Items() {
    	if(level2Items == null) {    		
    		// Level 2 items are...
    		level2Items = new HashMap();    		
    		
    		// Structure items contained in Record in local storage or parameter
    		// list of function    		
    		if(unqualifiedItemReferencesAreAllowed()) {
            	for(Iterator iter = getLocallyDefinedRecords().iterator(); iter.hasNext();) {
            		IDataBinding nextRecVar = (IDataBinding) iter.next();
            		if(!dataBindingsToIgnore.contains(nextRecVar)) {
            			BindingUtilities.addAllToUnqualifiedBindingNameMap(level2Items, nextRecVar, nextRecVar.getType().getSimpleNamesToDataBindingsMap());
            		}
            	}
            }
    		
        	for(Iterator iter = ioObjects.iterator(); iter.hasNext();) {
        		IDataBinding nextIOObject = (IDataBinding) iter.next();
        		
        		if(!dataBindingsToIgnore.contains(nextIOObject)) {
	        		
	        		// Records/forms used in an I/O statement in the function
	        		BindingUtilities.addToUnqualifiedBindingNameMap(level2Items, null, nextIOObject);
	        		
	        		// Structure items/fields in I/O record/form used by the function
	        		// Search for "resourceAssociation" in I/O record used by the function
	        		if(unqualifiedItemReferencesAreAllowed()) {
	        			if(!getLocallyDefinedRecords().contains(nextIOObject)) {
		        			Map simpleNamesToDataBindingsMap = nextIOObject.getType().getSimpleNamesToDataBindingsMap();
		        			BindingUtilities.addAllToUnqualifiedBindingNameMap(level2Items, nextIOObject, simpleNamesToDataBindingsMap);
	        			}

	        			BindingUtilities.addResourceAssociationBindingToMap(level2Items, nextIOObject);	        			
	        		}
        		}
        	}
    	}
    	return level2Items;
    }
    
	public Scope getScopeForKeywordThis() {
		return parentScope.getScopeForKeywordThis();
	}
	
	public boolean unqualifiedItemReferencesAreAllowed() {
		return parentScope.unqualifiedItemReferencesAreAllowed();
	}
	
	public IPartBinding getPartBinding() {
		return parentScope.getPartBinding();
	}
	
	public void addDeclaredDataName(String name) {
		declaredDataNames.add(name);
	}
	
	public boolean hasDeclaredDataName(String name) {
		return declaredDataNames.contains(name);
	}
	
	public boolean I4GLItemsNullableIsEnabled() {
		return parentScope.I4GLItemsNullableIsEnabled();
	}
}
