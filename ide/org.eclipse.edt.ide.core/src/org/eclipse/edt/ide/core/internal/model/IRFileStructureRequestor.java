/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
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

import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IField;
import org.eclipse.edt.ide.core.model.IFunction;
import org.eclipse.edt.ide.core.model.IMember;
import org.eclipse.edt.ide.core.model.IPackageDeclaration;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.IProperty;
import org.eclipse.edt.ide.core.model.IPropertyContainer;
import org.eclipse.edt.ide.core.model.Signature;

import com.ibm.icu.util.StringTokenizer;

public class IRFileStructureRequestor extends AbstractSourceElementRequestor implements ISourceElementRequestor {
	/**
	 * The handle to the compilation unit being parsed
	 */
	protected IClassFile fUnit;

	/**
	 * The info object for the compilation unit being parsed
	 */
	protected ClassFileElementInfo fUnitInfo;

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
	protected Stack<EGLElementInfo> fInfoStack;

	/**
	 * Stack of parent handles, corresponding to the info stack. We
	 * keep both, since info objects do not have back pointers to
	 * handles.
	 */
	protected Stack<Object> fHandleStack;
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
	 * Empty collections used for efficient initialization
	 */
	protected static String[] fgEmptyStringArray = new String[0];
	protected static byte[] fgEmptyByte= new byte[]{};
	protected static char[][] fgEmptyCharChar= new char[][]{};
	protected static char[] fgEmptyChar= new char[]{};
	
	public IRFileStructureRequestor(IClassFile unit, ClassFileElementInfo unitInfo, Map newElements) {
		this.fUnit = unit;
		this.fUnitInfo = unitInfo;
		this.fNewElements = newElements;
		this.fSourceFileName= unit.getElementName().toCharArray();
	}
	
	public void acceptField(int declarationStart, int declarationEnd, int modifiers, char[] type, char[] name, int nameSourceStart, int nameSourceEnd) {
		SourcePartElementInfo parentInfo = (SourcePartElementInfo) fInfoStack.peek();
		EGLElement parentHandle= (EGLElement)fHandleStack.peek();
		IField handle = null;
		
		if (parentHandle.getElementType() == IEGLElement.PART) {
			handle = new BinaryField((IPart) parentHandle, new String(name));
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

	public void acceptPackage(int declarationStart, int declarationEnd, char[] name) {
		EGLElementInfo parentInfo = (EGLElementInfo) fInfoStack.peek();
		EGLElement parentHandle= (EGLElement)fHandleStack.peek();
		IPackageDeclaration handle = null;
		fPackageName= name;
		
		if (parentHandle.getElementType() == IEGLElement.CLASS_FILE) {
			handle = new PackageDeclaration((ClassFile) parentHandle, new String(name));
		}
		resolveDuplicates(handle);
		
		SourceRefElementInfo info = new SourceRefElementInfo();
		info.setSourceRangeStart(declarationStart);
		info.setSourceRangeEnd(declarationEnd);

		parentInfo.addChild(handle);
		fNewElements.put(handle, info);
	}

	public void acceptProperty(int declarationStart, int declarationEnd, char[] text) {

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
			handle = new BinaryProperty((IPropertyContainer) parentHandle, new String(key));
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
	
	public void enterEGLFile() {
		fInfoStack = new Stack<EGLElementInfo>();
		fHandleStack = new Stack<Object>();
		fInfoStack.push(fUnitInfo);
		fHandleStack.push(fUnit);
	}

	public void enterField(int declarationStart, int modifiers, char[] type, char[] typeDeclaredPackage, char[] name, int nameSourceStart, int nameSourceEnd, boolean hasOccurs,
			int declarationEnd) {
		Object parentObj = fInfoStack.peek();
		if (!(parentObj instanceof SourcePartElementInfo)){
			//multiple fields declaration
			//clean up parent stacks by exiting field
			exitField(declarationEnd);
		}
		
		SourcePartElementInfo parentInfo = (SourcePartElementInfo) fInfoStack.peek();
		EGLElement parentHandle= (EGLElement)fHandleStack.peek();
		IField handle = null;
		
		if (parentHandle.getElementType() == IEGLElement.PART || parentHandle.getElementType() == IEGLElement.FUNCTION) {
			handle = new BinaryField((IPart) parentHandle, new String(name));
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
			handle = new BinaryFunction((IPart) parentHandle, new String(name), parameterTypeSigs);
		}
		else {
			Assert.isTrue(false); // Should not happen
		}
		resolveDuplicates(handle);
		
		SourceFunctionElementInfo info = new SourceFunctionElementInfo();
		info.setSourceRangeStart(declarationStart);
		int flags = modifiers;
// TODO EDT Uncomment when available		
//		info.setPartType(Part.FUNCTION);
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

	public void enterPart(int partType, char[] subType, int contentCode, int declarationStart, int modifiers, char[] name, int nameSourceStart,
			int nameSourceEnd, char[][] interfaces, char[][] parameterNames, char[][] parameterTypes, char[][] usagePartTypes, char[][] usagePartPackages, String eglFileName) {
		char[] enclosingTypeName= null;
		char[] qualifiedName= null;
		
		EGLElementInfo parentInfo = (EGLElementInfo) fInfoStack.peek();
		EGLElement parentHandle= (EGLElement)fHandleStack.peek();
		IPart handle = null;
		String nameString= new String(name);
		
		if (parentHandle.getElementType() == IEGLElement.CLASS_FILE) {
			handle = ((ClassFile) parentHandle).getPart(nameString);
			if (fPackageName == null) {
				qualifiedName= nameString.toCharArray();
			} else {
				qualifiedName= (new String(fPackageName) + "." + nameString).toCharArray(); //$NON-NLS-1$
			}
		}
		else if (parentHandle.getElementType() == IEGLElement.PART) {
			handle = ((IPart) parentHandle).getPart(nameString);
			enclosingTypeName= ((SourcePartElementInfo)parentInfo).getCharName();
			qualifiedName= (new String(((SourcePartElementInfo)parentInfo).getQualifiedName()) + "." + nameString).toCharArray(); //$NON-NLS-1$
		}
		else {
			Assert.isTrue(false); // Should not happen
		}
		resolveDuplicates(handle);
		
		SourcePartElementInfo info = new SourcePartElementInfo();
		info.setHandle(handle);
		info.setPartType(PartTypeConversion.getType(partType));
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
//		if(partType == com.ibm.etools.edt.core.ir.api.Part.PART_PROGRAM){
//			info.setHasParameters(hasParameters);
//		}
		info.setParameterNames(parameterNames);
		info.setParameterTypeNames(parameterTypes);
		info.setUsagePartTypes(usagePartTypes);
		info.setUsagePartPackages(usagePartPackages);
		
		parentInfo.addChild(handle);
		fNewElements.put(handle, info);
		if(parentInfo instanceof ClassFileElementInfo){
			ClassFileElementInfo classFileInfo = (ClassFileElementInfo)parentInfo;
			if(classFileInfo.getEglFileName() == null){
				String fileName = eglFileName;
				//If the filename contains / separators, assume that all the separators are /
				if (!fileName.contains("/")) {
					fileName.replace("\\", "/");
				}
				int index = fileName.lastIndexOf("/");
				if(index > -1){
					eglFileName = eglFileName.substring(index + 1);
				}
				classFileInfo.setEglFileName(eglFileName);
				classFileInfo.setCaseSensitivePackageName(toStringArray(new String(fPackageName)));
			}
		}

		fInfoStack.push(info);
		fHandleStack.push(handle);
	}

	public static String[] toStringArray(String str) {
		StringTokenizer parser = new StringTokenizer(str, ".");
		String[] names = new String[parser.countTokens()];
		for(int i=0; i<names.length; i++) {
			names[i] = parser.nextToken();
		}
		return names;
	}


	public void enterPropertyBlock(int declarationStart, char[] name) {
		SourceRefElementInfo parentInfo = (SourceRefElementInfo) fInfoStack.peek();
		EGLElement parentHandle= (EGLElement)fHandleStack.peek();
		IPropertyContainer handle = new BinaryPropertyBlock((IMember) parentHandle, new String(name));
		resolveDuplicates(handle);
		
		SourcePropertyBlockElementInfo info = new SourcePropertyBlockElementInfo();
		info.setCharName(name);
		info.setSourceRangeStart(declarationStart);

		parentInfo.addChild(handle);
		fNewElements.put(handle, info);

		fInfoStack.push(info);
		fHandleStack.push(handle);
	}

	public void exitEGLFile(int declarationEnd) {
		//TODO Rocky, any? 5/24
//		fUnitInfo.setSourceLength(declarationEnd + 1);

		// determine if there were any parsing errors
//		fUnitInfo.setIsStructureKnown(!this.hasSyntaxErrors);
	}

	public void exitField(int declarationEnd) {
		SourceFieldElementInfo info = (SourceFieldElementInfo) fInfoStack.pop();
		info.setSourceRangeEnd(declarationEnd);
		fHandleStack.pop();
	}

	public void exitFunction(int declarationEnd) {
		exitMember(declarationEnd);
	}

	public void exitPart(int declarationEnd) {
		exitMember(declarationEnd);
	}

	public void exitPropertyBlock(int declarationEnd) {
		exitMember(declarationEnd);
	}

	public void exitUse(int declarationEnd) {
		exitMember(declarationEnd);
	}
	/**
	 * Resolves duplicate handles by incrementing the occurrence count
	 * of the handle being created until there is no conflict.
	 */
	protected void resolveDuplicates(IEGLElement handle) {
		while (fNewElements.containsKey(handle)) {
			EGLElement h = (EGLElement) handle;
			h.setOccurrenceCount(h.getOccurrenceCount() + 1);
		}
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

	public void acceptUse(int declarationStart, int declarationEnd, char[] name) {
		return;
	}

	public void acceptImport(int declarationStart, int declarationEnd, char[] name, boolean onDemand) {
		return;
	}

	public void acceptLineSeparatorPositions(int[] positions) {
		return;
	}
	
	private static class PartTypeConversion{
		public static int getType(int irType){
			int astType = -1;
			switch(irType){
// TODO EDT Uncomment when supported			
//				case com.ibm.etools.edt.core.ir.api.Part.PART_FUNCTION:	astType = com.ibm.etools.edt.core.ast.Part.FUNCTION; break;
//				case com.ibm.etools.edt.core.ir.api.Part.PART_FORM:		astType = com.ibm.etools.edt.core.ast.Part.FORM; break;
//				case com.ibm.etools.edt.core.ir.api.Part.PART_FORMGROUP : 	astType = com.ibm.etools.edt.core.ast.Part.FORMGROUP; break;
//				case com.ibm.etools.edt.core.ir.api.Part.PART_LIBRARY : 	astType = com.ibm.etools.edt.core.ast.Part.LIBRARY; break;
//				case com.ibm.etools.edt.core.ir.api.Part.PART_PROGRAM : 	astType = com.ibm.etools.edt.core.ast.Part.PROGRAM; break;
//				case com.ibm.etools.edt.core.ir.api.Part.PART_DATATABLE : 	astType = com.ibm.etools.edt.core.ast.Part.DATATABLE; break;
//				case com.ibm.etools.edt.core.ir.api.Part.PART_STRUCTURED_RECORD:
//				case com.ibm.etools.edt.core.ir.api.Part.PART_RECORD : 	astType = com.ibm.etools.edt.core.ast.Part.RECORD; break;
//				case com.ibm.etools.edt.core.ir.api.Part.PART_DATAITEM :	astType = com.ibm.etools.edt.core.ast.Part.DATAITEM; break;
//				case com.ibm.etools.edt.core.ir.api.Part.PART_HANDLER : 	astType = com.ibm.etools.edt.core.ast.Part.HANDLER; break;
//				case com.ibm.etools.edt.core.ir.api.Part.PART_INTERFACE : 	astType = com.ibm.etools.edt.core.ast.Part.INTERFACE; break;
//				case com.ibm.etools.edt.core.ir.api.Part.PART_DELEGATE : 	astType = com.ibm.etools.edt.core.ast.Part.DELEGATE; break;
//				case com.ibm.etools.edt.core.ir.api.Part.PART_EXTERNALTYPE : astType = com.ibm.etools.edt.core.ast.Part.EXTERNALTYPE; break;
//				case com.ibm.etools.edt.core.ir.api.Part.PART_ENUMERATION : astType = com.ibm.etools.edt.core.ast.Part.ENUMERATION; break;
//				case com.ibm.etools.edt.core.ir.api.Part.PART_SERVICE : 	astType = com.ibm.etools.edt.core.ast.Part.SERVICE; break;
			}
			return astType;
		}
	}
}
