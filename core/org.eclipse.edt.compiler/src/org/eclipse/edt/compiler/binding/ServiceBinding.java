/*******************************************************************************
 * Copyright © 2005, 2010 IBM Corporation and others.
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
	
	private boolean haveExpandedExtendedInterfaces = false;
	private List extendedInterfaces = Collections.EMPTY_LIST;
	
    public ServiceBinding(String[] packageName, String caseSensitiveInternedName) {
        super(packageName, caseSensitiveInternedName);
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
    	if(!haveExpandedExtendedInterfaces) {
			List newExtendedInterfaces = getExtendedInterfaces(new HashSet());
			extendedInterfaces = newExtendedInterfaces;
			haveExpandedExtendedInterfaces = true;
		}
    	return extendedInterfaces;
    }
    
    private List getExtendedInterfaces(Set interfacesAlreadyProcessed) {
		List result = new ArrayList();
		if( !interfacesAlreadyProcessed.contains( this ) ) {
			interfacesAlreadyProcessed.add( this );
			for(Iterator iter = extendedInterfaces.iterator(); iter.hasNext();) {
				ITypeBinding typeBinding = (ITypeBinding) iter.next();
				
				typeBinding = realizeTypeBinding(typeBinding, getEnvironment());
				
				if(typeBinding.getKind() == ITypeBinding.INTERFACE_BINDING) {					
					result.add(typeBinding);
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
		haveExpandedExtendedInterfaces = false;
		extendedInterfaces = Collections.EMPTY_LIST;
	}

	public boolean isStructurallyEqual(IPartBinding anotherPartBinding) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public IDataBinding findPublicData(String simpleName) {
		return IBinding.NOT_FOUND_BINDING;
	}
}
