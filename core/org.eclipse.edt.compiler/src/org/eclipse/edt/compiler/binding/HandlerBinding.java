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


/**
 * @author Dave Murray
 */
public class HandlerBinding extends FunctionContainerBinding {

	private List extendedInterfaces = Collections.EMPTY_LIST;

	private List constructors = Collections.EMPTY_LIST;

	private final static String[] EGLUIJSF = new String[] {"egl", "ui", "jsf"};
	
    public HandlerBinding(String[] packageName, String caseSensitiveInternedName) {
        super(packageName, caseSensitiveInternedName);
    }
    
    private HandlerBinding(HandlerBinding old) {
        super(old);

    	if (old.extendedInterfaces == Collections.EMPTY_LIST) {
    		old.extendedInterfaces = new ArrayList();
    	}
    	if (old.constructors == Collections.EMPTY_LIST) {
    		old.constructors = new ArrayList();
    	}

    	extendedInterfaces = old.extendedInterfaces;
    	constructors = old.constructors;
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
		return HANDLER_BINDING;
	}
	
	protected byte[] getStructurallySignificantBytes() throws IOException{
		if (isJSFHandler()) {
			return calculateOnConstructionSignature();
		}
		return super.getStructurallySignificantBytes();
	}
	
	private boolean isJSFHandler() {
		return getAnnotation(EGLUIJSF, IEGLConstants.PROPERTY_JSFHANDLER) != null;
	}
	
	
	private byte[] calculateOnConstructionSignature() {
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		
		IDataBinding ocfField = getAnnotation(EGLUIJSF, IEGLConstants.PROPERTY_JSFHANDLER).findData(IEGLConstants.PROPERTY_ONCONSTRUCTIONFUNCTION);
		if (ocfField != null && ocfField != IBinding.NOT_FOUND_BINDING) {
			Object value = ((IAnnotationBinding)ocfField).getValue();
			
			if(value instanceof IFunctionBinding) {
				IFunctionBinding funcBinding = (IFunctionBinding) value;
				
				if (funcBinding != null && funcBinding != IBinding.NOT_FOUND_BINDING){
					try{
						ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
						
						try{
							for (Iterator iter = funcBinding.getParameters().iterator(); iter.hasNext();) {
								FunctionParameterBinding parameter = (FunctionParameterBinding) iter.next();
								
								objectOutputStream.writeBoolean(parameter.isInput());
								objectOutputStream.writeBoolean(parameter.isOutput());
								objectOutputStream.writeBoolean(parameter.isField());
								objectOutputStream.writeBoolean(parameter.isSQLNullable());
								
								writeTypeBindingReference(objectOutputStream, parameter.getType());
								
							}
						}finally{
							objectOutputStream.close();
						}
					}catch(IOException e){
						// do nothing
					}
				}
			}
		}
		return byteOutputStream.toByteArray();
	}
	
	public boolean isReference() {
		return true;
	}	
	
	public boolean isDeclarablePart() {
		return true;
	}

	public ITypeBinding primGetNullableInstance() {
		HandlerBinding nullable = new HandlerBinding(this);
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
