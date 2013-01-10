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

import java.util.Map;
import java.util.Stack;

import org.eclipse.edt.ide.core.internal.model.util.HashtableOfObject;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IField;
import org.eclipse.edt.ide.core.model.IFunction;
import org.eclipse.edt.ide.core.model.IImportContainer;
import org.eclipse.edt.ide.core.model.IMember;
import org.eclipse.edt.ide.core.model.IPackageDeclaration;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.IProblem;
import org.eclipse.edt.ide.core.model.IProperty;
import org.eclipse.edt.ide.core.model.IPropertyContainer;
import org.eclipse.edt.ide.core.model.IUseDeclaration;
import org.eclipse.edt.ide.core.model.Signature;

import org.eclipse.edt.compiler.core.ast.Part;
import com.ibm.icu.util.StringTokenizer;

/**
 * A requestor for the fuzzy parser, used to compute the children of an IEGLFile.
 */
public class EGLFileStructureRequestor extends AbstractSourceElementRequestor implements ISourceElementRequestor {

	/**
	 * The handle to the compilation unit being parsed
	 */
	protected IEGLFile fUnit;

	/**
	 * The info object for the compilation unit being parsed
	 */
	protected EGLFileElementInfo fUnitInfo;

	/**
	 * The import container info - null until created
	 */
	protected EGLElementInfo fImportContainerInfo= null;

	/**
	 * Hashtable of children elements of the compilation unit.
	 * Children are added to the table as they are found by
	 * the parser. Keys are handles, values are corresponding
	 * info objects.
	 */
	protected Map fNewElements;

	/**
	 * Stack of parent scope info objects. The info on the
	 * top of the stack is the parent of the next element found.
	 * For example, when we locate a method, the parent info object
	 * will be the type the method is contained in.
	 */
	protected Stack fInfoStack;

	/**
	 * Stack of parent handles, corresponding to the info stack. We
	 * keep both, since info objects do not have back pointers to
	 * handles.
	 */
	protected Stack fHandleStack;

	/**
	 * The name of the source file being parsed.
	 */
	protected char[] fSourceFileName= null;

	/**
	 * The dot-separated name of the package the compilation unit
	 * is contained in - based on the package statement in the
	 * compilation unit, and initialized by #acceptPackage.
	 * Initialized to <code>null</code> for the default package.
	 */
	protected char[] fPackageName= null;

	/**
	 * The number of references reported thus far. Used to
	 * expand the arrays of reference kinds and names.
	 */
	protected int fRefCount= 0;

	/**
	 * The initial size of the reference kind and name
	 * arrays. If the arrays fill, they are doubled in
	 * size
	 */
	protected static int fgReferenceAllocation= 50;

	/**
	 * Problem requestor which will get notified of discovered problems
	 */
	protected boolean hasSyntaxErrors = false;
	
	/**
	 * The counter to resolve the duplication of EGLElement.
	 */
	private int occurrenceCounter = 1;
	
	/**
	 * Empty collections used for efficient initialization
	 */
	protected static String[] fgEmptyStringArray = new String[0];
	protected static byte[] fgEmptyByte= new byte[]{};
	protected static char[][] fgEmptyCharChar= new char[][]{};
	protected static char[] fgEmptyChar= new char[]{};


	protected HashtableOfObject fieldRefCache;
	protected HashtableOfObject messageRefCache;
	protected HashtableOfObject typeRefCache;
	protected HashtableOfObject unknownRefCache;

protected EGLFileStructureRequestor(IEGLFile unit, EGLFileElementInfo unitInfo, Map newElements) throws EGLModelException {
	this.fUnit = unit;
	this.fUnitInfo = unitInfo;
	this.fNewElements = newElements;
	this.fSourceFileName= unit.getElementName().toCharArray();
} 
/**
 * @see ISourceElementRequestor
 */

public void acceptField(
	int declarationStart,
	int declarationEnd,
	int modifiers,
	char[] type,
	char[] name,
	int nameSourceStart,
	int nameSourceEnd) {
		
		SourcePartElementInfo parentInfo = (SourcePartElementInfo) fInfoStack.peek();
		EGLElement parentHandle= (EGLElement)fHandleStack.peek();
		IField handle = null;
		
		if (parentHandle.getElementType() == IEGLElement.PART) {
			handle = new SourceField((IPart) parentHandle, new String(name));
		}
		else {
			Assert.isTrue(false); // Should not happen
		}
		resolveDuplicates(handle);
		
		SourceFieldElementInfo info = new SourceFieldElementInfo();
		info.setCharName(name);
		info.setNameSourceStart(nameSourceStart);
		info.setNameSourceEnd(nameSourceEnd);
		info.setSourceRangeStart(declarationStart);
		info.setSourceRangeEnd(declarationEnd);
		info.setFlags(modifiers);
		info.setTypeName(type);

		parentInfo.addChild(handle);
		fNewElements.put(handle, info);
}

public void acceptProperty(
	int declarationStart,
	int declarationEnd,
	char[] text) {
		

		SourcePropertyBlockElementInfo parentInfo = (SourcePropertyBlockElementInfo) fInfoStack.peek();
		EGLElement parentHandle= (EGLElement)fHandleStack.peek();
		IProperty handle = null;
		StringTokenizer tokenizer = new StringTokenizer(new String(text),"=\t\f\n\r"); //$NON-NLS-1$
		// TODO Assumes simple property only handle boolean and string types
		String key = tokenizer.nextToken().trim();
		String value = null;
		int valueType = 0;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (!token.equals("=")) value = token.trim(); //$NON-NLS-1$
		}
		// TODO Temporary handling of valueType
		if (value == null || value.equals("yes") || value.equals("no")) //$NON-NLS-1$ //$NON-NLS-2$
			valueType = IProperty.VALUE_TYPE_BOOLEAN;
		else 
			valueType = IProperty.VALUE_TYPE_STRING;
		
		if (parentHandle.getElementType() == IEGLElement.PROPERTY_BLOCK) {
			handle = new SourceProperty((IPropertyContainer) parentHandle, new String(key));
		}
		else {
			Assert.isTrue(false); // Should not happen
		}
		resolveDuplicates(handle);
		
		SourcePropertyElementInfo info = new SourcePropertyElementInfo();
		info.setSourceRangeStart(declarationStart);
		info.setSourceRangeEnd(declarationEnd);
		info.setCharName(key.toCharArray());
		if (value != null) {
			info.setValue(value.toCharArray());
			info.setValueType(valueType);
		}
		parentInfo.addChild(handle);
		fNewElements.put(handle, info);
}

public void acceptPropertyLiteralName(int declarationStart, int declarationEnd, char[] name) {
}

/**
 * @see ISourceElementRequestor
 */
public void acceptImport(int declarationStart, int declarationEnd, char[] name, boolean onDemand) {
	EGLElementInfo parentInfo = (EGLElementInfo) fInfoStack.peek();
	EGLElement parentHandle= (EGLElement)fHandleStack.peek();
//	if (!(parentHandle.getElementType() == IEGLElement.EGL_FILE)) {
//		Assert.isTrue(false); // Should not happen
//	}

	IEGLFile parentCU= (IEGLFile)parentHandle;
	//create the import container and its info
	IImportContainer importContainer= parentCU.getImportContainer();
	if (fImportContainerInfo == null) {
		fImportContainerInfo= new EGLElementInfo();
		fImportContainerInfo.setIsStructureKnown(true);
		parentInfo.addChild(importContainer);
		fNewElements.put(importContainer, fImportContainerInfo);
	}
	
	// tack on the '.*' if it is onDemand
	String importName;
	if (onDemand) {
		importName= new String(name) + ".*"; //$NON-NLS-1$
	} else {
		importName= new String(name);
	}
	
	ImportDeclaration handle = new ImportDeclaration(importContainer, importName);
	resolveDuplicates(handle);
	
	SourceRefElementInfo info = new SourceRefElementInfo();
	info.setSourceRangeStart(declarationStart);
	info.setSourceRangeEnd(declarationEnd);

	fImportContainerInfo.addChild(handle);
	fNewElements.put(handle, info);
}
/*
 * Table of line separator position. This table is passed once at the end
 * of the parse action, so as to allow computation of normalized ranges.
 *
 * A line separator might corresponds to several characters in the source,
 * 
 */
public void acceptLineSeparatorPositions(int[] positions) {}
/**
 * @see ISourceElementRequestor
 */
public void acceptPackage(int declarationStart, int declarationEnd, char[] name) {

		EGLElementInfo parentInfo = (EGLElementInfo) fInfoStack.peek();
		EGLElement parentHandle= (EGLElement)fHandleStack.peek();
		IPackageDeclaration handle = null;
		fPackageName= name;
		
		if (parentHandle.getElementType() == IEGLElement.EGL_FILE) {
			handle = new PackageDeclaration((IEGLFile) parentHandle, new String(name));
		}
//		else {
//			Assert.isTrue(false); // Should not happen
//		}
		resolveDuplicates(handle);
		
		SourceRefElementInfo info = new SourceRefElementInfo();
		info.setSourceRangeStart(declarationStart);
		info.setSourceRangeEnd(declarationEnd);

		parentInfo.addChild(handle);
		fNewElements.put(handle, info);

}
public void acceptProblem(IProblem problem) {
	if ((problem.getID() & IProblem.Syntax) != 0){
		this.hasSyntaxErrors = true;
	}
}
/**
 * Convert these type names to signatures.
 * @see Signature.
 */
/* default */ static String[] convertTypeNamesToSigs(char[][] typeNames) {
	if (typeNames == null)
		return fgEmptyStringArray;
	int n = typeNames.length;
	if (n == 0)
		return fgEmptyStringArray;
	String[] typeSigs = new String[n];
	for (int i = 0; i < n; ++i) {
		typeSigs[i] = Signature.createTypeSignature(typeNames[i], false);
	}
	return typeSigs;
}
/* (non-Javadoc)
 * @see com.ibm.etools.egl.internal.model.internal.core.ISourceElementRequestor#enterUse(int, char[])
 */
public void acceptUse(int declarationStart, int declarationEnd, char[] name) {
	SourcePartElementInfo parentInfo = (SourcePartElementInfo) fInfoStack.peek();
	EGLElement parentHandle= (EGLElement)fHandleStack.peek();
	IUseDeclaration handle = null;
		
	if (parentHandle.getElementType() == IEGLElement.PART) {
		handle = new SourceUseDeclaration((IPart) parentHandle, new String(name));
	}
	else {
		Assert.isTrue(false); // Should not happen
	}
	resolveDuplicates(handle);
		
	SourceUseElementInfo info = new SourceUseElementInfo();
	info.setTypeName(name);
	info.setSourceRangeStart(declarationStart);
	info.setSourceRangeEnd(declarationEnd);

	parentInfo.addChild(handle);
	fNewElements.put(handle, info);
}
/**
 * @see ISourceElementRequestor
 */
public void enterEGLFile() {
	fInfoStack = new Stack();
	fHandleStack= new Stack();
	fInfoStack.push(fUnitInfo);
	fHandleStack.push(fUnit);
}
/**
 * @see ISourceElementRequestor
 */

public void enterField(
	int declarationStart,
	int modifiers,
	char[] type,
	char[] typeDeclaredPackage,
	char[] name,
	int nameSourceStart,
	int nameSourceEnd,
	boolean hasOccurs,
	int declEnd) {
		
		Object parentObj = fInfoStack.peek();
		if (!(parentObj instanceof SourcePartElementInfo)){
			//multiple fields declaration
			//clean up parent stacks by exiting field
			exitField(declEnd);
		}
		
		SourcePartElementInfo parentInfo = (SourcePartElementInfo) fInfoStack.peek();
		EGLElement parentHandle= (EGLElement)fHandleStack.peek();
		IField handle = null;
		
		if (parentHandle.getElementType() == IEGLElement.PART || parentHandle.getElementType() == IEGLElement.FUNCTION) {
			handle = new SourceField((IPart) parentHandle, new String(name));
		}
		else {
			Assert.isTrue(false); // Should not happen
		}
		resolveDuplicates(handle);
		
		SourceFieldElementInfo info = new SourceFieldElementInfo();
		info.setCharName(name);
		info.setNameSourceStart(nameSourceStart);
		info.setNameSourceEnd(nameSourceEnd);
		info.setSourceRangeStart(declarationStart);
		info.setFlags(modifiers);
		info.setTypeName(type);
		info.setHasOccurs(hasOccurs);
		info.setTypeDeclaredPackage(typeDeclaredPackage);

		parentInfo.addChild(handle);
		fNewElements.put(handle, info);

		fInfoStack.push(info);
		fHandleStack.push(handle);
		
}

public void enterPropertyBlock(
	int declarationStart,
	char[] name) {
		

		SourceRefElementInfo parentInfo = (SourceRefElementInfo) fInfoStack.peek();
		EGLElement parentHandle= (EGLElement)fHandleStack.peek();
		IPropertyContainer handle = new SourcePropertyBlock((IMember) parentHandle, new String(name));
		resolveDuplicates(handle);
		
		SourcePropertyBlockElementInfo info = new SourcePropertyBlockElementInfo();
		info.setCharName(name);
		info.setSourceRangeStart(declarationStart);

		parentInfo.addChild(handle);
		fNewElements.put(handle, info);

		fInfoStack.push(info);
		fHandleStack.push(handle);
		
}
/**
 * @see ISourceElementRequestor
 */

public void enterFunction(
	int declarationStart,
	int modifiers,
	char[] returnType,
	char[] returnTypePackage,
	char[] name,
	int nameSourceStart,
	int nameSourceEnd,
	char[][] parameterTypes,
	char[][] parameterNames,
	char[][] parameterUseTypes,
	boolean[] areNullable, 
	char[][] parameterPackages) {
		
		SourcePartElementInfo parentInfo = (SourcePartElementInfo) fInfoStack.peek();
		EGLElement parentHandle= (EGLElement)fHandleStack.peek();
		IFunction handle = null;

		// translate nulls to empty arrays
		if (parameterTypes == null) {
			parameterTypes= fgEmptyCharChar;
		}
		if (parameterNames == null) {
			parameterNames= fgEmptyCharChar;
		}

		String[] parameterTypeSigs = convertTypeNamesToSigs(parameterTypes);
		if (parentHandle.getElementType() == IEGLElement.PART) {
			handle = new SourceFunction((IPart) parentHandle, new String(name), parameterTypeSigs);
		}
		else {
			Assert.isTrue(false); // Should not happen
		}
		resolveDuplicates(handle);
		
		SourceFunctionElementInfo info = new SourceFunctionElementInfo();
		info.setSourceRangeStart(declarationStart);
		int flags = modifiers;
		info.setPartType(Part.FUNCTION);
		info.setCharName(name);
		info.setNameSourceStart(nameSourceStart);
		info.setNameSourceEnd(nameSourceEnd);
		info.setFlags(flags);
		info.setArgumentNames(parameterNames);
		info.setArgumentTypeNames(parameterTypes);
		info.setReturnType(returnType);
		info.setReturnTypePkg(returnTypePackage);
//		info.setExceptionTypeNames(exceptionTypes);
		info.setUseTypes(parameterUseTypes);
		info.setNullable(areNullable);
		info.setArgumentPackages(parameterPackages);
		
		parentInfo.addChild(handle);
		fNewElements.put(handle, info);
		fInfoStack.push(info);
		fHandleStack.push(handle);
}

/**
 * Common processing for parts.
 */
public void enterPart(
	int partType,
	char[] subType,
	int contentCode,
	int declarationStart,
	int modifiers,
	char[] name,
	int nameSourceStart,
	int nameSourceEnd,
	char[][] interfaces,
	char[][] parameterNames,
	char[][] parameterTypes,
	char[][] usagePartTypes, 
	char[][] usagePartPackages,
	String eglFileName) {
	
	char[] qualifiedName= null;
	
	EGLElementInfo parentInfo = (EGLElementInfo) fInfoStack.peek();
	EGLElement parentHandle= (EGLElement)fHandleStack.peek();
	IPart handle = null;
	String nameString= new String(name);
	
	if (parentHandle.getElementType() == IEGLElement.EGL_FILE) {
		handle = ((IEGLFile) parentHandle).getPart(nameString);
		if (fPackageName == null) {
			qualifiedName= nameString.toCharArray();
		} else {
			qualifiedName= (new String(fPackageName) + "." + nameString).toCharArray(); //$NON-NLS-1$
		}
	}
	else if (parentHandle.getElementType() == IEGLElement.PART) {
		handle = ((IPart) parentHandle).getPart(nameString);
		qualifiedName= (new String(((SourcePartElementInfo)parentInfo).getQualifiedName()) + "." + nameString).toCharArray(); //$NON-NLS-1$
	}
	else {
		Assert.isTrue(false); // Should not happen
	}
	resolveDuplicates(handle);
	
	SourcePartElementInfo info = new SourcePartElementInfo();
	info.setHandle(handle);
	info.setPartType(partType);
	info.setSubTypeName(subType);
	info.setContentHashCode(contentCode);
	info.setSourceRangeStart(declarationStart);
	info.setFlags(modifiers);
	info.setCharName(name);
	info.setNameSourceStart(nameSourceStart);
	info.setNameSourceEnd(nameSourceEnd);
	info.setInterfaceNames(interfaces);
	info.setSourceFileName(fSourceFileName);
	info.setPackageName(fPackageName);
	info.setQualifiedName(qualifiedName);
//	if(partType == com.ibm.etools.edt.core.ir.api.Part.PART_PROGRAM){
//		info.setHasParameters(hasParameters);
//	}
	info.setParameterNames(parameterNames);
	info.setParameterTypeNames(parameterTypes);
//	for (Iterator iter = fNewElements.keySet().iterator(); iter.hasNext();){
//		Object object = iter.next();
//		if (object instanceof IImportDeclaration)
//			info.addImport(((IImportDeclaration)object).getElementName().toCharArray());
//	}
	

	parentInfo.addChild(handle);
	fNewElements.put(handle, info);

	fInfoStack.push(info);
	fHandleStack.push(handle);
	
}

/**
 * @see ISourceElementRequestor
 */
public void exitPart(int declarationEnd) {

	exitMember(declarationEnd);
}
/**
 * @see ISourceElementRequestor
 */
public void exitEGLFile(int declarationEnd) {
	fUnitInfo.setSourceLength(declarationEnd + 1);

	// determine if there were any parsing errors
	fUnitInfo.setIsStructureKnown(!this.hasSyntaxErrors);
}
/**
 * @see ISourceElementRequestor
 */

public void exitField(int declarationSourceEnd) {
	
	SourceFieldElementInfo info = (SourceFieldElementInfo) fInfoStack.pop();
	info.setSourceRangeEnd(declarationSourceEnd);
	
	fHandleStack.pop();
}

/**
 * common processing for parts
 */
protected void exitMember(int declarationEnd) {
	SourceRefElementInfo info = (SourceRefElementInfo) fInfoStack.pop();
	info.setSourceRangeEnd(declarationEnd);
	fHandleStack.pop();
}
/**
 * @see ISourceElementRequestor
 */
public void exitFunction(int declarationEnd) {
	exitMember(declarationEnd);
}
/**
 * @see ISourceElementRequestor
 */
public void exitPropertyBlock(int declarationEnd) {
	exitMember(declarationEnd);
}
/**
 * Resolves duplicate handles by incrementing the occurrence count
 * of the handle being created until there is no conflict.
 */
protected void resolveDuplicates(IEGLElement handle) {
	while (fNewElements.containsKey(handle)) {
		EGLElement h = (EGLElement) handle;
		h.setOccurrenceCount( occurrenceCounter ++ );
	}
}

	/* (non-Javadoc)
	 * @see com.ibm.etools.egl.internal.model.internal.core.ISourceElementRequestor#exitUse(int)
	 */
	public void exitUse(int declarationEnd) {
		exitMember(declarationEnd);

	}

}
