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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Dave Murray
 */
public abstract class FunctionContainerBinding extends PartBinding {

    private List classFields = Collections.EMPTY_LIST;
    private List declaredFunctions = Collections.EMPTY_LIST;
    
    public FunctionContainerBinding(String[] packageName, String caseSensitiveInternedName) {
        super(packageName, caseSensitiveInternedName);
    }

	public FunctionContainerBinding(FunctionContainerBinding old) {
		super(old);

	    if (old.classFields == Collections.EMPTY_LIST) {
	    	old.classFields = new ArrayList();
	    }
	    if (old.declaredFunctions == Collections.EMPTY_LIST) {
	    	old.declaredFunctions = new ArrayList();
	    }
		classFields = old.classFields;
		declaredFunctions = old.declaredFunctions;
	}

    /**
     * @return A list of ClassFieldBinding objects representing the fields
     *         declared inside of this function container.
     */
    public List getClassFields() {
        return classFields;
    }
    
    /**
     * @return A list containing everything in getClassFields(), plus additional
     *         data specific to the function container type, such as program
     *         parameters.
     */
    public List getDeclaredData() {
    	return classFields;
    }
    
    /**
     * 
     * @param includeSuperType
     * @return A list containing everything in getDeclaredData 
     *         and supertype's getDeclaredData
     */
    public List getDeclaredData(boolean includeSuperType){
    	if(!includeSuperType){
    		return(classFields);
    	}

    	List retList = new LinkedList();
    	retList.addAll(classFields);
    	
		//get supertype's declared data
		IPartBinding spBinding = this.getDefaultSuperType();
		if(spBinding instanceof FunctionContainerBinding){
			retList.addAll(((FunctionContainerBinding)spBinding).getDeclaredData(includeSuperType));
		}else if(spBinding instanceof ExternalTypeBinding){
			retList.addAll(((ExternalTypeBinding)spBinding).getDeclaredAndInheritedData());
		}
		
    	return retList;
    }
    
    public void addClassField(ClassFieldBinding fieldBinding) {
        if (classFields == Collections.EMPTY_LIST) {
            classFields = new ArrayList();
        }
        classFields.add(fieldBinding);
    }

    /**
     * @return A list of IFunctionBinding objects representing the functions
     *         declared inside of this function container.
     */
    public List getDeclaredFunctions() {
        return declaredFunctions;
    }
    
    public List getDeclaredFunctions(boolean includeSuperType){
    	if(!includeSuperType){
    		return(declaredFunctions);
    	}

    	List retList = new LinkedList();
    	retList.addAll(declaredFunctions);
    	
		//get supertype's declared functions
		IPartBinding spBinding = this.getDefaultSuperType();
		if(spBinding instanceof FunctionContainerBinding){
			retList.addAll(((FunctionContainerBinding)spBinding).getDeclaredFunctions(includeSuperType));
		}else if(spBinding instanceof ExternalTypeBinding){
			retList.addAll(((ExternalTypeBinding)spBinding).getDeclaredAndInheritedFunctions());
		}
		
    	return retList;
    }

    public void addDeclaredFunction(NestedFunctionBinding functionBinding) {
        if (declaredFunctions == Collections.EMPTY_LIST) {
            declaredFunctions = new ArrayList();
        }
        declaredFunctions.add(functionBinding);
    }
    
    public void addDeclaredFunctions(List functionBindings) {
        if (declaredFunctions == Collections.EMPTY_LIST) {
            declaredFunctions = new ArrayList();
        }
        declaredFunctions.addAll(functionBindings);
    }
    
    public boolean referencedFunctionsAreIncluded() {
        // TODO Auto-generated method stub
        return false;
    }
    
    protected IDataBinding primFindData(String simpleName) {
         for(Iterator iter = classFields.iterator(); iter.hasNext();) {
            IDataBinding binding = (IDataBinding) iter.next();
            if(binding.getName() == simpleName) {
                return binding;
            }
        }
        
        OverloadedFunctionSet functionSet = new OverloadedFunctionSet();
        for(Iterator iter = declaredFunctions.iterator(); iter.hasNext();) {
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
    
	protected IFunctionBinding primFindFunction(String simpleName) {
        for(Iterator iter = declaredFunctions.iterator(); iter.hasNext();) {
            IFunctionBinding binding = (IFunctionBinding) ((NestedFunctionBinding) iter.next()).getType();
            if(binding.getName() == simpleName) {
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
    
    public void clear() {
    	super.clear();
        classFields = Collections.EMPTY_LIST;
        declaredFunctions = Collections.EMPTY_LIST;
    }
    
	public boolean isDeclarablePart() {
		return false;
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
			else if (IDataBinding.CLASS_FIELD_BINDING == result.getKind() && ((ClassFieldBinding) result).isPrivate) {
				return IBinding.NOT_FOUND_BINDING;
			}
		}
		return result;
	}
}
