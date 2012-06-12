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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Dave Murray
 */
public class InterfaceBinding extends PartBinding {
	
	private List declaredFunctions = Collections.EMPTY_LIST;	
	private List declaredAndInheritedFunctions = null;
			
	private transient List extendedTypes = Collections.EMPTY_LIST;
	
    public InterfaceBinding(String[] packageName, String caseSensitiveInternedName) {
        super(packageName, caseSensitiveInternedName);
    }

    private InterfaceBinding(InterfaceBinding old) {
        super(old);
 
    	if (old.declaredFunctions == Collections.EMPTY_LIST) {
    		old.declaredFunctions = new ArrayList();
    	}
    	if (old.extendedTypes == Collections.EMPTY_LIST) {
    		old.extendedTypes = new ArrayList();
    	}
        
    	declaredFunctions = old.declaredFunctions;	
    	declaredAndInheritedFunctions = old.declaredAndInheritedFunctions;   			
    	extendedTypes = old.extendedTypes;    
    }

	public boolean isReference() {
		return true;
	}
	
	
    /**
     * @return A list of InterfaceTypeBinding objects representing the types
     *         that this type extends (and the types that those
     *         types extend).
     */
    public List getExtendedTypes() {
    	return getExtendedTypes(new HashSet());
    }
    
    List getExtendedTypes(Set typesAlreadyProcessed) {
		List result = new ArrayList();
		if( !typesAlreadyProcessed.contains( this ) ) {
			typesAlreadyProcessed.add( this );
			for(Iterator iter = extendedTypes.iterator(); iter.hasNext();) {
				ITypeBinding typeBinding = realizeTypeBinding((ITypeBinding) iter.next(), getEnvironment());
				
				if(!typesAlreadyProcessed.contains(typeBinding)) {
					if(typeBinding.getKind() == ITypeBinding.INTERFACE_BINDING) {					
						result.add(typeBinding);
						result.addAll(((InterfaceBinding) typeBinding).getExtendedTypes(typesAlreadyProcessed));
					}
				}
			}
		}
		return result;
    }
    
    public void addExtendedType(ITypeBinding typeBinding) {
    	if(typeBinding != this) {
	    	if(extendedTypes == Collections.EMPTY_LIST) {
	    		extendedTypes = new ArrayList();
	    	}
	    	extendedTypes.add(typeBinding);
	    	declaredAndInheritedFunctions = null;
    	}
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

    public List getDeclaredAndInheritedFunctions() {
    	if(declaredAndInheritedFunctions == null) {
        	List tempDeclaredAndInheritedFunctions = new ArrayList();
        	tempDeclaredAndInheritedFunctions.addAll(getDeclaredFunctions());
    		
    		List extendedTypes = getExtendedTypes();
    		boolean hasEnvironment = (getEnvironment() != null);
    		for(Iterator iter = extendedTypes.iterator(); iter.hasNext();) {
    			InterfaceBinding inter = (InterfaceBinding) iter.next();
    			tempDeclaredAndInheritedFunctions.addAll(inter.getDeclaredFunctions());
    			hasEnvironment = hasEnvironment && (inter.getEnvironment() != null);
    		}
    		
    		if (hasEnvironment) {
    			declaredAndInheritedFunctions = tempDeclaredAndInheritedFunctions;    		}
    		else {
    			return tempDeclaredAndInheritedFunctions;
    		}
    	}
    	return declaredAndInheritedFunctions;
    }
    
	public int getKind() {
		return INTERFACE_BINDING;
	}

	public void clear() {
		super.clear();
		declaredFunctions = Collections.EMPTY_LIST;	
		declaredAndInheritedFunctions = null;		
		extendedTypes = Collections.EMPTY_LIST;
	}

	public boolean isStructurallyEqual(IPartBinding anotherPartBinding) {
		// TODO Auto-generated method stub
		return false;
	}
	
	protected IDataBinding primFindData(String simpleName) {
        OverloadedFunctionSet functionSet = new OverloadedFunctionSet();
        for(Iterator iter = getDeclaredAndInheritedFunctions().iterator(); iter.hasNext();) {
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
	
	protected IFunctionBinding primFindFunction(String simpleName) {
        for(Iterator iter = getDeclaredAndInheritedFunctions().iterator(); iter.hasNext();) {
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
	
	private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(extendedTypes.size());
        for(int i = 0; i < extendedTypes.size(); i++) {
        	writeTypeBindingReference(out, (ITypeBinding) extendedTypes.get(i));
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int numExtendedTypes = in.readInt();
        for(int i = 0; i < numExtendedTypes; i++) {
        	addExtendedType(readTypeBindingReference(in));
        }
    }

	
	public boolean isDeclarablePart() {
		return true;
	}
		
	public boolean containsExtendsFor(InterfaceBinding iface) {
		if (iface == this) {
			return true;
		}
		
		Iterator i = getExtendedTypes().iterator();
		while (i.hasNext()) {
			if (i.next() == iface) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public ITypeBinding primGetNullableInstance() {
		InterfaceBinding nullable = new InterfaceBinding(this);
		nullable.setNullable(true);
		return nullable;
	}

	@Override
	public boolean isInstantiable() {
		return false;
	}
}
