/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.model;

import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.Flags;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IFunction;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.Signature;

/**
 * @see IFunction
 */

/* package */public class SourceFunction extends SourcePart implements IFunction {

	/**
	 * The parameter type signatures of the function - stored locally
	 * to perform equality test. <code>null</code> indicates no
	 * parameters.
	 */
	protected String[] fParameterTypes;

	/**
	 * An empty list of Strings
	 */
	protected static final String[] fgEmptyList= new String[] {};
protected SourceFunction(IPart parent, String name, String[] parameterTypes) {
	super(IEGLElement.FUNCTION, parent, name);
	Assert.isTrue(name.indexOf('.') == -1);
	if (parameterTypes == null) {
		fParameterTypes= fgEmptyList;
	} else {
		fParameterTypes= parameterTypes;
	}
}
public boolean equals(Object o) {
	return super.equals(o) && Util.equalArraysOrNull(fParameterTypes, ((SourceFunction)o).fParameterTypes);
}
/**
 * @see EGLElement#getHandleMemento()
 */
public String getHandleMemento() {
	StringBuffer buff = new StringBuffer(((EGLElement) getParent()).getHandleMemento());
	buff.append(getHandleMementoDelimiter());
	buff.append(getElementName());
	for (int i = 0; i < fParameterTypes.length; i++) {
		buff.append(getHandleMementoDelimiter());
		buff.append(fParameterTypes[i]);
	}
	return buff.toString();
}
/**
 * @see EGLElement#getHandleMemento()
 */
protected char getHandleMementoDelimiter() {
	return EGLElement.EGLM_FUNCTION;
}
/**
 * @see IFunction
 */
public int getNumberOfParameters() {
	return fParameterTypes == null ? 0 : fParameterTypes.length;
}
/**
 * @see IFunction
 */
public String[] getParameterNames() throws EGLModelException {
	SourceFunctionElementInfo info = (SourceFunctionElementInfo) getElementInfo();
	char[][] names= info.getArgumentNames();
	if (names == null || names.length == 0) {
		return fgEmptyList;
	}
	String[] strings= new String[names.length];
	for (int i= 0; i < names.length; i++) {
		strings[i]= new String(names[i]);
	}
	return strings;
}
/**
 * @see IFunction
 */
public String[] getParameterTypes() {
	return fParameterTypes;
}
/**
 * @see IFunction
 */
public String getReturnTypeName() throws EGLModelException {
	SourceFunctionElementInfo info = (SourceFunctionElementInfo) getElementInfo();
	return Signature.createTypeSignature(info.getReturnTypeName(), false);
}
/**
 * @see IFunction
 */
public String getSignature() throws EGLModelException {
	SourceFunctionElementInfo info = (SourceFunctionElementInfo) getElementInfo();
	return info.getSignature();
}

/**
 * @see IFunction#isSimilar(IFunction)
 */
public boolean isSimilar(IFunction method) {
	return 
		this.areSimilarFunctions(
			this.getElementName(), this.getParameterTypes(),
			method.getElementName(), method.getParameterTypes(),
			null);
}

/**
 */
public String readableName() {

	StringBuffer buffer = new StringBuffer(super.readableName());
	buffer.append('(');
	String[] parameterTypes = this.getParameterTypes();
	int length;
	if (parameterTypes != null && (length = parameterTypes.length) > 0) {
		for (int i = 0; i < length; i++) {
			buffer.append(Signature.toString(parameterTypes[i]));
			if (i < length - 1) {
				buffer.append(", "); //$NON-NLS-1$
			}
		}
	}
	buffer.append(')');
	return buffer.toString();
}
/**
 * @private Debugging purposes
 */
protected void toStringInfo(int tab, StringBuffer buffer, Object info) {
	buffer.append(this.tabString(tab));
	if (info == null) {
		buffer.append(getElementName());
		buffer.append(" (not open)"); //$NON-NLS-1$
	} else if (info == NO_INFO) {
		buffer.append(getElementName());
	} else {
		try {
			if (Flags.isStatic(this.getFlags())) {
				buffer.append("static "); //$NON-NLS-1$
			}
			buffer.append(this.getElementName());
			buffer.append('(');
			String[] parameterTypes = this.getParameterTypes();
			int length;
			if (parameterTypes != null && (length = parameterTypes.length) > 0) {
				for (int i = 0; i < length; i++) {
					buffer.append(Signature.toString(parameterTypes[i]));
					if (i < length - 1) {
						buffer.append(", "); //$NON-NLS-1$
					}
				}
			}
			buffer.append(')');
		} catch (EGLModelException e) {
			buffer.append("<EGLModelException in toString of " + getElementName()); //$NON-NLS-1$
		}
	}
}
	public String[] getUseTypes() throws EGLModelException {
		SourceFunctionElementInfo info = (SourceFunctionElementInfo) getElementInfo();
		char[][] useTypes = info.getUseTypes();
		if (useTypes == null || useTypes.length == 0) {
			return fgEmptyList;
		}
		String[] strings= new String[useTypes.length];
		for (int i= 0; i < useTypes.length; i++) {
			strings[i]= new String(useTypes[i]);
		}
		return strings;
	}
	public boolean[] getNullable() throws EGLModelException {
		SourceFunctionElementInfo info = (SourceFunctionElementInfo) getElementInfo();
		boolean[] areNullable = info.getAreNullable();
		return areNullable;
	}
	
	public String[] getParameterPackages() throws EGLModelException {
		SourceFunctionElementInfo info = (SourceFunctionElementInfo) getElementInfo();
		char[][] parameterPkgs = info.getArgumentPackages();
		if(parameterPkgs == null || parameterPkgs.length == 0){
			return fgEmptyList;
		}
		String[] strings = new String[parameterPkgs.length];
		for (int i= 0; i < parameterPkgs.length; i++) {
			strings[i]= new String(parameterPkgs[i]);
		}
		return strings;
	}
	public String getReturnTypePackage() throws EGLModelException {
		SourceFunctionElementInfo info = (SourceFunctionElementInfo) getElementInfo();
		char[] returnTypePkgs = info.getReturnTypePkg();
		if(returnTypePkgs == null)	//primitive return type
			return null;
		if(returnTypePkgs.length == 0){	//default pacakge
			return "";
		}
		String string = String.valueOf(returnTypePkgs);		
		return string;
	}
}
