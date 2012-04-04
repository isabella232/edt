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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Dave Murray
 */
public class ServiceBinding extends FunctionContainerBinding {
	
	private List extendedInterfaces = Collections.EMPTY_LIST;
	
    public ServiceBinding(String[] packageName, String caseSensitiveInternedName) {
        super(packageName, caseSensitiveInternedName);
    }

    private ServiceBinding(ServiceBinding old) {
        super(old);
        
    	if (old.extendedInterfaces == Collections.EMPTY_LIST) {
    		old.extendedInterfaces = new ArrayList();
    	}
    	extendedInterfaces = old.extendedInterfaces;
    }

    public boolean isReference() {
		return true;
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
    
	public int getKind() {
		return SERVICE_BINDING;
	}
    
	public void clear() {
		super.clear();
		extendedInterfaces = Collections.EMPTY_LIST;
	}

	public boolean isStructurallyEqual(IPartBinding anotherPartBinding) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public IDataBinding findPublicData(String simpleName) {
		return IBinding.NOT_FOUND_BINDING;
	}
	
	public ITypeBinding primGetNullableInstance() {
		ServiceBinding nullable = new ServiceBinding(this);
		nullable.setNullable(true);
		return nullable;
	}
	
	@Override
	public boolean isInstantiable() {
		return false;
	}
	
}
