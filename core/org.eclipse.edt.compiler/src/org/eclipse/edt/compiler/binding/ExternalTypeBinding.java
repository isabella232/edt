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
public class ExternalTypeBinding extends PartBinding {
	
	transient private ExternalTypeDataBinding staticExternalTypeDataBinding;
	
	private List declaredFunctions = Collections.EMPTY_LIST;	
	private List declaredAndInheritedFunctions = null;
	
	private List declaredData = Collections.EMPTY_LIST;
	private List declaredAndInheritedData = null;
	
	private List constructors = Collections.EMPTY_LIST;
	
	private boolean haveExpandedExtendedTypes = false;
	private transient List allExtendedTypes = Collections.EMPTY_LIST;
	
	private transient List extendedTypes = Collections.EMPTY_LIST;

    public ExternalTypeBinding(String[] packageName, String caseSensitiveInternedName) {
        super(packageName, caseSensitiveInternedName);
    }
    
    private ExternalTypeBinding(ExternalTypeBinding old) {
    	super(old);
    	
    	declaredFunctions = old.declaredFunctions;	
    	declaredAndInheritedFunctions = old.declaredAndInheritedFunctions;   	
    	declaredData = old.declaredData;
    	declaredAndInheritedData = old.declaredAndInheritedData;
    	constructors = old.constructors;
    	haveExpandedExtendedTypes = old.haveExpandedExtendedTypes;
    	allExtendedTypes = old.allExtendedTypes;
    	List extendedTypes = old.extendedTypes;
    	
    }
    
	public boolean isReference() {
		return true;
	}
    
    /**
     * @return A list of ExternalTypeBinding objects representing the types
     *         that this type extends (and the types that those
     *         types extend).
     */
    public List getExtendedTypes() {
		if(!haveExpandedExtendedTypes) {
			List newExtendedTypes = getExtendedTypes(new HashSet());
			allExtendedTypes = newExtendedTypes;
			haveExpandedExtendedTypes = true;
		}
    	return allExtendedTypes;
    }
    
    List getExtendedTypes(Set typesAlreadyProcessed) {
		List result = new ArrayList();
		if( !typesAlreadyProcessed.contains( this ) ) {
			typesAlreadyProcessed.add( this );
			for(Iterator iter = extendedTypes.iterator(); iter.hasNext();) {
				ITypeBinding typeBinding = realizeTypeBinding((ITypeBinding) iter.next(), getEnvironment());
				
				if(!typesAlreadyProcessed.contains(typeBinding)) {
					if(typeBinding.getKind() == ITypeBinding.EXTERNALTYPE_BINDING) {					
						result.add(typeBinding);
						result.addAll(((ExternalTypeBinding) typeBinding).getExtendedTypes(typesAlreadyProcessed));
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
	    	haveExpandedExtendedTypes = false;
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
    	declaredAndInheritedFunctions = null;
    }
    
    public List getDeclaredAndInheritedFunctions() {
    	if(declaredAndInheritedFunctions == null) {
    		declaredAndInheritedFunctions = new ArrayList();
    		declaredAndInheritedFunctions.addAll(getDeclaredFunctions());
    		
    		List extendedTypes = getExtendedTypes();
    		for(Iterator iter = extendedTypes.iterator(); iter.hasNext();) {
    			declaredAndInheritedFunctions.addAll(((ExternalTypeBinding) iter.next()).getDeclaredFunctions());
    		}
    	}
    	return declaredAndInheritedFunctions;
    }
    
    public List getDeclaredAndInheritedData() {
    	if(declaredAndInheritedData == null) {
    		declaredAndInheritedData = new ArrayList();
    		declaredAndInheritedData.addAll(getDeclaredData());
    		
    		List extendedTypes = getExtendedTypes();
    		for(Iterator iter = extendedTypes.iterator(); iter.hasNext();) {
    			declaredAndInheritedData.addAll(((ExternalTypeBinding) iter.next()).getDeclaredData());
    		}
    	}
    	return declaredAndInheritedData;
    }
    
    public List getDeclaredData() {
    	return declaredData;
    }
    
    public void addClassField(ClassFieldBinding fieldBinding) {
        if (declaredData == Collections.EMPTY_LIST) {
        	declaredData = new ArrayList();
        }
        declaredData.add(fieldBinding);
    }
    
    public List getConstructors() {
    	return constructors;
    }
    
    public void addConstructor(ConstructorBinding constructor) {
        if (constructors == Collections.EMPTY_LIST) {
        	constructors = new ArrayList();
        }
        constructors.add(constructor);
    }
    
	public int getKind() {
		return EXTERNALTYPE_BINDING;
	}

	public void clear() {
		super.clear();
		constructors = Collections.EMPTY_LIST;
		declaredData = Collections.EMPTY_LIST;
		declaredAndInheritedData = null;
		declaredFunctions = Collections.EMPTY_LIST;	
		declaredAndInheritedFunctions = null;		
		haveExpandedExtendedTypes = false;
		extendedTypes = Collections.EMPTY_LIST;
		allExtendedTypes = Collections.EMPTY_LIST;
	}

	public boolean isStructurallyEqual(IPartBinding anotherPartBinding) {
		// TODO Auto-generated method stub
		return false;
	}
	
	protected IDataBinding primFindData(String simpleName) {
        for(Iterator iter = getDeclaredAndInheritedData().iterator(); iter.hasNext();) {
            IDataBinding binding = (IDataBinding) iter.next();
            if(binding.getName() == simpleName) {
                return binding;
            }
        }
        
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
	
	public boolean isDeclarablePart() {
		return true;
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
    
    public IDataBinding getStaticExternalTypeDataBinding() {
		if(staticExternalTypeDataBinding == null) {
			staticExternalTypeDataBinding = new ExternalTypeDataBinding(getCaseSensitiveName(), this, this);
		}
		return staticExternalTypeDataBinding;
	}
    
	public StaticPartDataBinding getStaticPartDataBinding() {
		return (StaticPartDataBinding)getStaticExternalTypeDataBinding();
	}

	public boolean containsExtendsFor(ExternalTypeBinding et) {
		if (et == this) {
			return true;
		}
		
		Iterator i = getExtendedTypes().iterator();
		while (i.hasNext()) {
			if (i.next() == et) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public ITypeBinding primGetNullableInstance() {
		ExternalTypeBinding nullable = new ExternalTypeBinding(this);
		nullable.setNullable(true);
		return nullable;
	}

	@Override
	public boolean isInstantiable() {
		
		Iterator i = getConstructors().iterator();
		while (i.hasNext()) {
			ConstructorBinding binding = (ConstructorBinding) i.next();
			if (binding.getParameters().size() == 0 && binding.isPrivate()) {
				return false;
			}
		}
		return super.isInstantiable();
	}
}
