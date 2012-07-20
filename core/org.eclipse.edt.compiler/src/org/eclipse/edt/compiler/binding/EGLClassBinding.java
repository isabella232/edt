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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.compiler.core.IEGLConstants;


public class EGLClassBinding extends FunctionContainerBinding {

	private List extendedInterfaces = Collections.EMPTY_LIST;

	private List constructors = Collections.EMPTY_LIST;
	
	private EGLClassBinding extendedClass = null;
	
	
    public EGLClassBinding(String[] packageName, String caseSensitiveInternedName) {
        super(packageName, caseSensitiveInternedName);
    }
    
    private EGLClassBinding(EGLClassBinding old) {
        super(old);

    	if (old.extendedInterfaces == Collections.EMPTY_LIST) {
    		old.extendedInterfaces = new ArrayList();
    	}
    	if (old.constructors == Collections.EMPTY_LIST) {
    		old.constructors = new ArrayList();
    	}

    	extendedInterfaces = old.extendedInterfaces;
    	constructors = old.constructors;
    	extendedClass = old.extendedClass;
    }
    
    
	public void clear() {
		super.clear();
		extendedInterfaces = Collections.EMPTY_LIST;
		constructors = Collections.EMPTY_LIST;
	}
	
    /**
     * @return A list of InterfaceBinding objects representing the interfaces
     *         that this service implements (and the interfaces that those
     *         interfaces extend).
     */
	public List getImplementedInterfaces() {
		return getExtendedInterfaces(new HashSet());
	}
    
    private List getExtendedInterfaces(Set interfacesAlreadyProcessed) {
		List result = new ArrayList();
		for(Iterator iter = extendedInterfaces.iterator(); iter.hasNext();) {
			ITypeBinding typeBinding = (ITypeBinding) iter.next();
			
			typeBinding = realizeTypeBinding(typeBinding, getEnvironment());
			
			if(!interfacesAlreadyProcessed.contains(typeBinding)) {
				if(typeBinding.getKind() == ITypeBinding.INTERFACE_BINDING) {					
					result.add(typeBinding);
					result.addAll(((InterfaceBinding) typeBinding).getExtendedTypes(interfacesAlreadyProcessed));
				}
			}
		}
		return result;
    }
    
    public void addExtenedInterface(ITypeBinding interfaceBinding) {
    	if(extendedInterfaces == Collections.EMPTY_LIST) {
    		extendedInterfaces = new ArrayList();
    	}
    	extendedInterfaces.add(interfaceBinding);
    }


	public boolean isStructurallyEqual(IPartBinding anotherPartBinding) {
		// TODO Auto-generated method stub
		return false;
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
		return CLASS_BINDING;
	}
	
	public boolean isReference() {
		return true;
	}	
	
	public boolean isDeclarablePart() {
		return true;
	}

	public ITypeBinding primGetNullableInstance() {
		EGLClassBinding nullable = new EGLClassBinding(this);
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
	
	public EGLClassBinding getExtends() {
		return extendedClass;
	}
	
	public void setExtends(EGLClassBinding classBinding) {
		extendedClass =  classBinding;
	}
	
    public List getDeclaredAndInheritedData() {
		List tempDeclaredAndInheritedData = new ArrayList();
		tempDeclaredAndInheritedData.addAll(getDeclaredData());
		
		if (getExtends() != null) {
    		EGLClassBinding extended = (EGLClassBinding)realizeTypeBinding(getExtends(), getEnvironment());
    		tempDeclaredAndInheritedData.addAll(extended.getDeclaredAndInheritedData());
		}
		
		return tempDeclaredAndInheritedData;
    }

    public List getDeclaredAndInheritedFunctions() {
		List tempDeclaredAndInheritedFunctions = new ArrayList();
		tempDeclaredAndInheritedFunctions.addAll(getDeclaredFunctions());
		
		if (getExtends() != null) {
    		EGLClassBinding extended = (EGLClassBinding)realizeTypeBinding(getExtends(), getEnvironment());
    		tempDeclaredAndInheritedFunctions.addAll(extended.getDeclaredAndInheritedFunctions());
		}
		
		return tempDeclaredAndInheritedFunctions;
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

	protected IFunctionBinding primFindFunction(String simpleName) {
        for(Iterator iter = getDeclaredAndInheritedFunctions().iterator(); iter.hasNext();) {
            IFunctionBinding binding = (IFunctionBinding) ((NestedFunctionBinding) iter.next()).getType();
            if(binding.getName().equals(simpleName)) {
                return binding;
            }
        }
        return IBinding.NOT_FOUND_BINDING;
	}
	
	public List getExtendedHierarchy() {
		List list = new ArrayList();
		if (getExtends() != null) {
			list.add(getExtends());
			list.addAll(getExtends().getExtendedHierarchy());
		}
		return list;
	}

}
