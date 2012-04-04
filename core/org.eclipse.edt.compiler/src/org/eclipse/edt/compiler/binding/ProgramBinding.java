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
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;


/**
 * @author Dave Murray
 */
public class ProgramBinding extends FunctionContainerBinding {
	
	private final String[] EGLCORE = new String[] { "egl", "core" };
	
	transient private ProgramDataBinding staticLibraryDataBinding;

	private List parameters = Collections.EMPTY_LIST;

	private boolean callable;

	public ProgramBinding(String[] packageName, String caseSensitiveInternedName) {
		super(packageName, caseSensitiveInternedName);
	}

	/**
	 * @return A list of ProgramParameterBinding objects.
	 */
	public List getParameters() {
		return parameters;
	}

	public void addParameter(ProgramParameterBinding parameterBinding) {
		if (parameters == Collections.EMPTY_LIST) {
			parameters = new ArrayList();
		}
		parameters.add(parameterBinding);
	}

	public int getKind() {
		return PROGRAM_BINDING;
	}

	public void clear() {
		super.clear();
		parameters = Collections.EMPTY_LIST;
	}

	public boolean isStructurallyEqual(IPartBinding anotherPartBinding) {
		// TODO Auto-generated method stub
		return false;
	}

	protected IDataBinding primFindData(String simpleName) {
		for (Iterator iter = parameters.iterator(); iter.hasNext();) {
			IDataBinding binding = (IDataBinding) iter.next();
			if (binding.getName().equals(simpleName)) {
				return binding;
			}
		}
		return super.primFindData(simpleName);
	}

	public List getDeclaredData() {
		List result = super.getDeclaredData();
		if (parameters != Collections.EMPTY_LIST) {
			if (result == Collections.EMPTY_LIST) {
				result = parameters;
			} else {
				result = new ArrayList(result);
				result.addAll(parameters);
			}
		}
		return result;
	}

	protected byte[] getStructurallySignificantBytes() throws IOException {
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
		objectOutputStream.writeBoolean(isCallable());

		String alias = getAlias();
		if (alias == null) {
			objectOutputStream.writeBoolean(false);
		} else {
			objectOutputStream.writeBoolean(true);
			objectOutputStream.writeUTF(alias);
		}

		IDataBinding inputRecord = getInputRecord();
		if (inputRecord == null) {
			objectOutputStream.writeBoolean(false);
		} else {
			objectOutputStream.writeBoolean(true);
			writeTypeBindingReference(objectOutputStream, inputRecord.getType());
		}

		inputRecord = getInputUIRecord();
		if (inputRecord == null) {
			objectOutputStream.writeBoolean(false);
		} else {
			objectOutputStream.writeBoolean(true);
			writeTypeBindingReference(objectOutputStream, inputRecord.getType());
		}

		for (Iterator iter = getParameters().iterator(); iter.hasNext();) {
			ProgramParameterBinding parameter = (ProgramParameterBinding) iter.next();

			writeTypeBindingReference(objectOutputStream, parameter.getType());

		}

		objectOutputStream.close();
		return byteOutputStream.toByteArray();
	}

	public boolean isCallable() {
		return callable;
	}

	public void setCallable(boolean callable) {
		this.callable = callable;
	}

	private String getAlias() {
		IAnnotationBinding ann = getAnnotation(EGLCORE, IEGLConstants.PROPERTY_ALIAS);
		if (ann != null) {
			return (String) ann.getValue();
		}
		return null;

	}
	
	private IDataBinding getInputRecord() {
		IAnnotationBinding ann = getSubTypeAnnotationBinding();
		if (ann == null) {
			return null;
		}
		IDataBinding field = ann.findData(IEGLConstants.PROPERTY_INPUTRECORD);
		if (field == null || field == IBinding.NOT_FOUND_BINDING) {
			return null;
		}
		Object value = ((AnnotationBinding) field).getValue();
		
		if (value == null || value == IBinding.NOT_FOUND_BINDING) {
			return null;
		}
		
		if (value instanceof IDataBinding) {
			return (IDataBinding)value;
		}
		return null;
		
	}

	private IDataBinding getInputUIRecord() {
		IAnnotationBinding ann = getSubTypeAnnotationBinding();
		if (ann == null) {
			return null;
		}
		IDataBinding field = ann.findData(IEGLConstants.PROPERTY_INPUTUIRECORD);
		if (field == null || field == IBinding.NOT_FOUND_BINDING) {
			return null;
		}
		Object value = ((AnnotationBinding) field).getValue();
		
		if (value == null || value == IBinding.NOT_FOUND_BINDING) {
			return null;
		}
		
		if (value instanceof IDataBinding) {
			return (IDataBinding)value;
		}
		return null;
		
	}
	
	public IDataBinding getStaticProgramDataBinding() {
		if(staticLibraryDataBinding == null) {
			staticLibraryDataBinding = new ProgramDataBinding(getCaseSensitiveName(), this, this);
		}
		return staticLibraryDataBinding;
	}

	public StaticPartDataBinding getStaticPartDataBinding() {
		return (StaticPartDataBinding)getStaticProgramDataBinding();
	}

	@Override
	public ITypeBinding primGetNullableInstance() {
		return this;
	}

}
