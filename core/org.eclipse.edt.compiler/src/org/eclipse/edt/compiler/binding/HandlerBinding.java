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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;

import org.eclipse.edt.compiler.core.IEGLConstants;


/**
 * @author Dave Murray
 */
public class HandlerBinding extends FunctionContainerBinding {

	private final String[] EGLUIJSF = new String[] {"egl", "ui", "jsf"};
    public HandlerBinding(String[] packageName, String caseSensitiveInternedName) {
        super(packageName, caseSensitiveInternedName);
    }

	public void clear() {
		super.clear();
	}

	public boolean isStructurallyEqual(IPartBinding anotherPartBinding) {
		// TODO Auto-generated method stub
		return false;
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


}
