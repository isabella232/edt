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

import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.Flags;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IField;
import org.eclipse.edt.ide.core.model.IFunction;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.Signature;

import org.eclipse.edt.compiler.core.ast.Part;

/**
 * @author twilson
 * created	Sep 8, 2003
 */
/** 
 * Element info for an IPart element that originated from source. 
 */
public class SourcePartElementInfo extends MemberElementInfo {
	protected static final IField[] NO_FIELDS = new IField[0];
	protected static final IFunction[] NO_FUNCTIONS = new IFunction[0];
	protected static final IPart[] NO_PARTS = new IPart[0];

	/**
	 * The part type of this element info
	 */	
	protected int fPartType = 0;
	
	/**
	 * The sub type of this element - 
	 * Used for Record and Form and DataTable and DataItem today
	 */
	protected char[] fSubType = null;
	/**
	 * The enclosing type name for this type.
	 *
	 * @see getEnclosingPartName
	 */
	protected char[] fEnclosingPartName = null;

	/**
	 * The names of the interfaces this type implements. These names are NOT fully qualified.
	 */
	protected char[][] interfaceNames;
	
	/**
	 * For a called program
	 * this is a collection of the names of the parameters for this program,
	 * in the order the parameters are delcared. This is an empty array if this
	 * called program's parameter list is empty. For a non called program, this
	 * is null.
	 */
	protected char[][] parameterNames;

	/**
	 * For a called program
	 * this is a collection of the type names of the parameters for this program,
	 * in the order the parameters are delcared. This is an empty array if this
	 * called program's parameter list is empty. For a non called program, this
	 * is null.
	 */
	protected char[][] parameterTypeNames;
	
	protected char[][] usagePartTypes;
	protected char[][] usagePartPackages;
	
	/**
	 * The name of the source file this type is declared in.
	 */
	protected char[] fSourceFileName= null;

	/**
	 * The name of the package this type is contained in.
	 */
	protected char[] fPackageName= null;

	/**
	 * The qualified name of this type.
	 */
	protected char[] fQualifiedName= null;

	/**
	 * The imports in this type's compilation unit
	 */
	protected char[][] fImports= null;

	/**
	 * Backpointer to my type handle - useful for translation
	 * from info to handle.
	 */
	protected IPart fHandle= null;
	
	/**
	 * Hashcode of the string that is the content.
	 * Used to tell if changes have occurred in the
	 * part for the purposes of an EGLElementDeltaBuilder
	 * @param i
	 */
	protected int fContentHashCode;
	
//	/**
//	 * If this part has parameters.
//	 * When the part is a program, we need to verify if it has parameters to 
//	 * decide if it is a called one
//	 * 
//	 */
//	private boolean hasParameters = false;

/**
 * Adds the given import to this type's collection of imports
 */
protected void addImport(char[] i) {
	if (fImports == null) {
		fImports = new char[][] {i};
	} else {
		char[][] copy = new char[fImports.length + 1][];
		System.arraycopy(fImports, 0, copy, 0, fImports.length);
		copy[fImports.length] = i;
		fImports = copy;
	}
}


/**
 * Returns the IPart that is the enclosing type for this
 * type, or <code>null</code> if this type is a top level type.
 */
public IPart getEnclosingPart() {
	IEGLElement parent= fHandle.getParent();
	if (parent != null && parent.getElementType() == IEGLElement.PART) {
		try {
			return (IPart)((EGLElement)parent).getElementInfo();
		} catch (EGLModelException e) {
			return null;
		}
	} else {
		return null;
	}
}
/**
 * @see IPart
 */
public char[] getEnclosingPartName() {
	return fEnclosingPartName;
}
/**
 * @see IPart
 */
public IField[] getFields() {
	int length = fChildren.length;
	if (length == 0) return NO_FIELDS;
	IField[] fields = new IField[length];
	int fieldIndex = 0;
	for (int i = 0; i < length; i++) {
		IEGLElement child = fChildren[i];
		if (child instanceof SourceField) {
			try {
				IField field = (IField)((SourceField)child).getElementInfo();
				fields[fieldIndex++] = field;
			} catch (EGLModelException e) {
			}
		}
	}
	if (fieldIndex == 0) return NO_FIELDS;
	System.arraycopy(fields, 0, fields = new IField[fieldIndex], 0, fieldIndex);
	return fields;
}
/**
 * @see IPart
 */
public char[] getFileName() {
	return fSourceFileName;
}
/**
 * Returns the handle for this type info
 */
public IPart getHandle() {
	return fHandle;
}
/**
 * @see IPart
 */
public char[][] getImports() {
	return fImports;
}

/**
 * @see ISourceType
 */
public char[][] getInterfaceNames() {
	return this.interfaceNames;
}

/**
 * Sets the (unqualified) names of the interfaces this type implements or extends
 */
protected void setInterfaceNames(char[][] interfaceNames) {
	this.interfaceNames = interfaceNames;
}

/**
 * @see IPart
 */
public IPart[] getMemberParts() {
	int length = fChildren.length;
	if (length == 0) return NO_PARTS;
	IPart[] memberTypes = new IPart[length];
	int typeIndex = 0;
	for (int i = 0; i < length; i++) {
		IEGLElement child = fChildren[i];
		if (child instanceof SourcePart) {
			try {
				IPart type = (IPart)((SourcePart)child).getElementInfo();
				memberTypes[typeIndex++] = type;
			} catch (EGLModelException e) {
			}
		}
	}
	if (typeIndex == 0) return NO_PARTS;
	System.arraycopy(memberTypes, 0, memberTypes = new IPart[typeIndex], 0, typeIndex);
	return memberTypes;
}
/**
 * @see IPart
 */
public IFunction[] getFunctions() {
	int length = fChildren.length;
	if (length == 0) return NO_FUNCTIONS;
	IFunction[] functions = new IFunction[length];
	int functionIndex = 0;
	for (int i = 0; i < length; i++) {
		IEGLElement child = fChildren[i];
		if (child instanceof SourceFunction) {
			try {
				IFunction function = (IFunction)((SourceFunction)child).getElementInfo();
				functions[functionIndex++] = function;
			} catch (EGLModelException e) {
			}
		}
	}
	if (functionIndex == 0) return NO_FUNCTIONS;
	System.arraycopy(functions, 0, functions = new IFunction[functionIndex], 0, functionIndex);
	return functions;
}
/**
 * @see IPart
 */
public char[] getPackageName() {
	return fPackageName;
}
/**
 * @see IPart
 */
public char[] getQualifiedName() {
	return fQualifiedName;
}
/**
 * @see IPart
 */
public boolean isBinaryType() {
	return false;
}
/**
 * Sets the (unqualified) name of the type that encloses this type.
 */
protected void setEnclosingPartName(char[] enclosingPartName) {
	fEnclosingPartName = enclosingPartName;
}
/**
 * Sets the handle for this type info
 */
protected void setHandle(IPart handle) {
	fHandle= handle;
}
/**
 * Sets the name of the package this type is declared in.
 */
protected void setPackageName(char[] name) {
	fPackageName= name;
}
/**
 * Sets this type's qualified name.
 */
protected void setQualifiedName(char[] name) {
	fQualifiedName= name;
}
/**
 * Sets the name of the source file this type is declared in.
 */
protected void setSourceFileName(char[] name) {
	fSourceFileName= name;
}
public String toString() {
	if(fHandle != null){
		return "Info for " + fHandle.toString(); //$NON-NLS-1$
	}
	else{
		return super.toString();
	}
}

	public boolean isDataItem() {
		return fPartType == Part.DATAITEM;
	}

	public boolean isDataTable() {
		return fPartType == Part.DATATABLE;
	}

	public boolean isForm() {
		return fPartType == Part.FORM;
	}

	public boolean isFormGroup() {
		return fPartType == Part.FORMGROUP;
	}

	public boolean isFunction() {
		return fPartType == Part.FUNCTION;
	}

	public boolean isLibrary() {
		return fPartType == Part.LIBRARY;
	}

	public boolean isProgram() {
		return fPartType == Part.PROGRAM;
	}

	public boolean isClass() {
		return fPartType == Part.CLASS;
	}

	public boolean isCalledProgram() {
		return (isProgram() && parameterNames != null);
	}
	
//	protected void setHasParameters(boolean hasParameters){
//		this.hasParameters = hasParameters;
//	}

	public boolean isPublic() {
		return Flags.isPublic(flags);
	}

	public boolean isExternalType() {
		return fPartType == Part.EXTERNALTYPE;
	}
	
	public boolean isEnumeration() {
		return fPartType == Part.ENUMERATION;
	}

	public boolean isDelegate() {
		return fPartType == Part.DELEGATE;
	}

	public boolean isRecord() {
		return fPartType == Part.RECORD;
	}

	public boolean isHandler() {
		return fPartType == Part.HANDLER;
	}

	public boolean isService() {
		return fPartType == Part.SERVICE;
	}
	
	public boolean isInterface() {
		return fPartType == Part.INTERFACE;
	}
	
	/**
	 * @return
	 */
	public int getPartType() {
		return fPartType;
	}

	/**
	 * @param i
	 */
	public void setPartType(int i) {
		fPartType = i;
	}

	/**
	 * @return
	 */
	public String getSubTypeSignature() {
		if(fSubType != null)
		{
			return Signature.createTypeSignature(this.fSubType, false);
		}
		return null; 
	}
	
	public char[] getSubTypeName()
	{
		return fSubType;
	}

	/**
	 * @param string
	 */
	public void setSubTypeName(char[] string) {
		fSubType = string;
	}

	/**
	 * @return
	 */
	public int getContentHashCode() {
		return fContentHashCode;
	}

	/**
	 * @return
	 */
	public char[] getFPackageName() {
		return fPackageName;
	}

	/**
	 * @param i
	 */
	public void setContentHashCode(int i) {
		fContentHashCode = i;
	}

	/**
	 * @param cs
	 */
	public void setFPackageName(char[] cs) {
		fPackageName = cs;
	}


	public char[][] getParameterNames() {
		return parameterNames;
	}


	protected void setParameterNames(char[][] parameterNames) {
		this.parameterNames = parameterNames;
	}


	public char[][] getParameterTypeNames() {
		return parameterTypeNames;
	}


	protected void setParameterTypeNames(char[][] parameterTypeNames) {
		this.parameterTypeNames = parameterTypeNames;
	}


	public char[][] getUsagePartTypes() {
		return usagePartTypes;
	}


	protected void setUsagePartTypes(char[][] usagePartTypes) {
		this.usagePartTypes = usagePartTypes;
	}


	public char[][] getUsagePartPackages() {
		return usagePartPackages;
	}


	protected void setUsagePartPackages(char[][] usagePartPackages) {
		this.usagePartPackages = usagePartPackages;
	}
	
	
	
	
}
