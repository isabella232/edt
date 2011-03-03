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
package org.eclipse.edt.compiler.binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Dave Murray
 */
public class InterfaceBinding extends PartBinding {
	
	private List declaredFunctions = Collections.EMPTY_LIST;	
	
    public InterfaceBinding(String[] packageName, String caseSensitiveInternedName) {
        super(packageName, caseSensitiveInternedName);
    }
    
	public boolean isReference() {
		return true;
	}
    
    /**
     * @return A list of IFunctionBinding objects representing the functions
     *         declared in this interface (does not include the functions
     *         from extended interfaces).
     */
    public List getDeclaredFunctions() {
    	return declaredFunctions;
    }
    
    public void addDeclaredFunction(NestedFunctionBinding declaredFunctionBinding) {
    	if(declaredFunctions == Collections.EMPTY_LIST) {
    		declaredFunctions = new ArrayList();
    	}
    	declaredFunctions.add(declaredFunctionBinding);
    }
    
	public int getKind() {
		return INTERFACE_BINDING;
	}

	public void clear() {
		super.clear();
		declaredFunctions = Collections.EMPTY_LIST;	
	}

	public boolean isStructurallyEqual(IPartBinding anotherPartBinding) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public IDataBinding findData(String simpleName) {
        OverloadedFunctionSet functionSet = new OverloadedFunctionSet();
        for(Iterator iter = getDeclaredFunctions().iterator(); iter.hasNext();) {
        	IDataBinding binding = (IDataBinding) iter.next();
            if(binding.getName() == simpleName) {
            	functionSet.setName(binding.getCaseSensitiveName());
                functionSet.addNestedFunctionBinding(binding);
            }        
        }
        List nestedFunctionBindings = functionSet.getNestedFunctionBindings();
		if(nestedFunctionBindings.size() == 1) {
			return (IDataBinding) nestedFunctionBindings.get(0);
		}
		else if(!nestedFunctionBindings.isEmpty()){
			return functionSet;
		}
		
        return IBinding.NOT_FOUND_BINDING;
    }
	
	public IDataBinding findPublicData(String simpleName) {
		IDataBinding result = findData(simpleName);
		if(IBinding.NOT_FOUND_BINDING != result) {
			if(IDataBinding.NESTED_FUNCTION_BINDING == result.getKind()) {
				if(((NestedFunctionBinding) result).isPrivate()) {
					return IBinding.NOT_FOUND_BINDING;
				}
			}
			else if(IDataBinding.OVERLOADED_FUNCTION_SET_BINDING == result.getKind()) {
				OverloadedFunctionSet newSet = new OverloadedFunctionSet();
				newSet.setName(result.getCaseSensitiveName());
				for(Iterator iter = ((OverloadedFunctionSet) result).getNestedFunctionBindings().iterator(); iter.hasNext();) {
					NestedFunctionBinding func = (NestedFunctionBinding) iter.next();
					if(!func.isPrivate()) {
						newSet.addNestedFunctionBinding(func);
					}
				}
				if(newSet.getNestedFunctionBindings().isEmpty()) {
					return IBinding.NOT_FOUND_BINDING;
				}
				else {
					result = newSet;
				}
			}
			else if (((ClassFieldBinding) result).isPrivate) {
				return IBinding.NOT_FOUND_BINDING;
			}
		}
		return result;
	}
	
	public IFunctionBinding findFunction(String simpleName) {
        for(Iterator iter = getDeclaredFunctions().iterator(); iter.hasNext();) {
            IFunctionBinding binding = (IFunctionBinding) ((NestedFunctionBinding) iter.next()).getType();
            if(binding.getName().equals(simpleName)) {
                return binding;
            }
        }
        return IBinding.NOT_FOUND_BINDING;
	}
	
	public IFunctionBinding findPublicFunction(String simpleName) {
		IFunctionBinding result = findFunction(simpleName);
		if(IBinding.NOT_FOUND_BINDING != result && result.isPrivate()) {
			return IBinding.NOT_FOUND_BINDING;
		}
		return result;
	}
	
	public boolean isDeclarablePart() {
		return true;
	}
}
