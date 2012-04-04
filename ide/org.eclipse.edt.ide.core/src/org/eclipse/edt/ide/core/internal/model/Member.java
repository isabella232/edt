/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
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

import java.util.ArrayList;

import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IFunction;
import org.eclipse.edt.ide.core.model.IMember;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.ISourceRange;
import org.eclipse.edt.ide.core.model.Signature;


/**
 * @see IMember
 */

abstract class Member extends SourceRefElement implements IMember {
protected Member(int type, IEGLElement parent, String name) {
	super(type, parent, name);
}
protected boolean areSimilarFunctions(
	String name1, String[] params1, 
	String name2, String[] params2,
	String[] simpleNames1) {
		
	if (name1.equalsIgnoreCase(name2)) {
		int params1Length = params1.length;
		if (params1Length == params2.length) {
			for (int i = 0; i < params1Length; i++) {
				String simpleName1 = 
					simpleNames1 == null ? 
						Signature.getSimpleName(Signature.toString(params1[i])) :
						simpleNames1[i];
				String simpleName2 = Signature.getSimpleName(Signature.toString(params2[i]));
				if (!simpleName1.equals(simpleName2)) {
					return false;
				}
			}
			return true;
		}
	}
	return false;
}
/*
 * Helper function for SourceType.findFunctions and BinaryType.findFunctions
 */
protected IFunction[] findFunctions(IFunction function, IFunction[] functions) {
	String elementName = function.getElementName();
	String[] parameters = function.getParameterTypes();
	int paramLength = parameters.length;
	String[] simpleNames = new String[paramLength];
	for (int i = 0; i < paramLength; i++) {
		simpleNames[i] = Signature.getSimpleName(Signature.toString(parameters[i]));
	}
	ArrayList list = new ArrayList();
	next: for (int i = 0, length = functions.length; i < length; i++) {
		IFunction existingFunction = functions[i];
		if (this.areSimilarFunctions(
				elementName,
				parameters,
				existingFunction.getElementName(),
				existingFunction.getParameterTypes(),
				simpleNames)) {
			list.add(existingFunction);
		}
	}
	int size = list.size();
	if (size == 0) {
		return null;
	} else {
		IFunction[] result = new IFunction[size];
		list.toArray(result);
		return result;
	}
}
/**
 * @see IMember
 */
public IPart getDeclaringPart() {
	EGLElement parent = (EGLElement)getParent();
	if (parent.fLEType == PART) {
		return (IPart) parent;
	}
	return null;
}
/**
 * @see IMember
 */
public int getFlags() throws EGLModelException {
	MemberElementInfo info = (MemberElementInfo) getElementInfo();
	return info.getModifiers();
}
/**
 * @see EGLElement#getHandleMemento()
 */
protected char getHandleMementoDelimiter() {
	return EGLElement.EGLM_PART;
}
/**
 * @see IMember
 */
public ISourceRange getNameRange() throws EGLModelException {
	MemberElementInfo info= (MemberElementInfo)getElementInfo();
	return new SourceRange(info.getNameSourceStart(), info.getNameSourceEnd() - info.getNameSourceStart() + 1);
}
/**
 * @see IMember
 */
public boolean isBinary() {
	return false;
}
/**
 * @see IEGLElement
 */
public boolean isReadOnly() {
	return isBinary();
}
/**
 */
public String readableName() {

	IEGLElement declaringType = getDeclaringPart();
	if (declaringType != null) {
		String declaringName = ((EGLElement) getDeclaringPart()).readableName();
		StringBuffer buffer = new StringBuffer(declaringName);
		buffer.append('.');
		buffer.append(this.getElementName());
		return buffer.toString();
	} else {
		return super.readableName();
	}
}
/**
 * Updates the name range for this element.
 */
protected void updateNameRange(int nameStart, int nameEnd) {
	try {
		MemberElementInfo info = (MemberElementInfo) getElementInfo();
		info.setNameSourceStart(nameStart);
		info.setNameSourceEnd(nameEnd);
	} catch (EGLModelException npe) {
		return;
	}
}

@Override
	public IClassFile getClassFile() {
		// TODO Auto-generated method stub
		return ((EGLElement)getParent()).getClassFile();
	}

}
