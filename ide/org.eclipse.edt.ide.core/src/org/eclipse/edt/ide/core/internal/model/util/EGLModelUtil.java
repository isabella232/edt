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
package org.eclipse.edt.ide.core.internal.model.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import org.eclipse.edt.compiler.internal.core.utils.CharOperation;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IImportDeclaration;
import org.eclipse.edt.ide.core.model.IMember;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.model.IPart;

//import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;

/**
 * Utility methods for the EGL Model.
 */
public class EGLModelUtil {
	
	/** 
	 * Finds a type by its qualified type name (dot separated).
	 * @param jproject The java project to search in
	 * @param str The fully qualified name (type name with enclosing type names and package (all separated by dots))
	 * @return The type found, or null if not existing
	 */	
	public static IPart findPart(IEGLProject jproject, String fullyQualifiedName) throws EGLModelException {
		IPart type= jproject.findPart(fullyQualifiedName);
		if (type != null)
			return type;
		IPackageFragmentRoot[] roots= jproject.getPackageFragmentRoots();
		for (int i= 0; i < roots.length; i++) {
			IPackageFragmentRoot root= roots[i];
			type= findPart(root, fullyQualifiedName);
			if (type != null && type.exists())
				return type;
		}	
		return null;
	}
	
	/**
	 * Returns <code>true</code> if the given package fragment root is
	 * referenced. This means it is own by a different project but is referenced
	 * by the root's parent. Returns <code>false</code> if the given root
	 * doesn't have an underlying resource.
	 */
	public static boolean isReferenced(IPackageFragmentRoot root) {
		IResource resource= root.getResource();
		if (resource != null) {
			IProject jarProject= resource.getProject();
			IProject container= root.getEGLProject().getProject();
			return !container.equals(jarProject);
		}
		return false;
	}
	
	private static IPart findPart(IPackageFragmentRoot root, String fullyQualifiedName) throws EGLModelException{
		IEGLElement[] children= root.getChildren();
		for (int i= 0; i < children.length; i++) {
			IEGLElement element= children[i];
			if (element.getElementType() == IEGLElement.PACKAGE_FRAGMENT){
				IPackageFragment pack= (IPackageFragment)element;
				if (! fullyQualifiedName.startsWith(pack.getElementName()))
					continue;
				IPart type= findPart(pack, fullyQualifiedName);
				if (type != null && type.exists())
					return type;
			}
		}		
		return null;
	}
	
	private static IPart findPart(IPackageFragment pack, String fullyQualifiedName) throws EGLModelException{
		IEGLFile[] cus= pack.getEGLFiles();
		for (int i= 0; i < cus.length; i++) {
			IEGLFile unit= cus[i];
			// TODO find out why working copy is needed
		//	IEGLFile wc= EGLModelUtil.toWorkingCopy(unit);
			IPart type= findPart(unit, fullyQualifiedName);
			if (type != null && type.exists())
				return type;
		}
		return null;
	}
	
	public static IPart findUnqualifiedPart(IPackageFragment pack, String name) throws EGLModelException{
		IEGLFile[] cus= pack.getEGLFiles();
		for (int i= 0; i < cus.length; i++) {
			IEGLFile unit= cus[i];
			IPart type= findUnqualifiedPart(unit, name);
			if (type != null && type.exists()) {
				return type;
			}
		}
		return null;
	}
	
	private static IPart findPart(IEGLFile cu, String fullyQualifiedName) throws EGLModelException{
		IPart[] types= cu.getAllParts();
		for (int i= 0; i < types.length; i++) {
			IPart type= types[i];
			if (getFullyQualifiedName(type).equals(fullyQualifiedName))
				return type;
		}
		return null;
	}
	
	private static IPart findUnqualifiedPart(IEGLFile cu, String name) throws EGLModelException{
		IPart[] types= cu.getAllParts();
		for (int i= 0; i < types.length; i++) {
			IPart type= types[i];
			if (type.getElementName().equals(name))
				return type;
		}
		return null;
	}
	
	/** 
	 * Finds a type by package and type name.
	 * @param jproject the java project to search in
	 * @param pack The package name
	 * @param typeQualifiedName the type qualified name (type name with enclosing type names (separated by dots))
	 * @return the type found, or null if not existing
	 * @deprecated Use IEGLProject.findPart(String, String) instead
	 */	
	public static IPart findPart(IEGLProject jproject, String pack, String typeQualifiedName) throws EGLModelException {
		return jproject.findPart(pack, typeQualifiedName);
	}

	/**
	 * Finds a type container by container name.
	 * The returned element will be of type <code>IPackageFragment</code>.
	 * <code>null</code> is returned if the type container could not be found.
	 * @param jproject The EGL project defining the context to search
	 * @param typeContainerName A dot separarted name of the type container
	 * @see #getTypeContainerName(IPart)
	 */
	public static IEGLElement findPartContainer(IEGLProject jproject, String typeContainerName) throws EGLModelException {
		// find it as package
		IEGLElement result = null;
		IPath path= new Path(typeContainerName.replace('.', '/'));
		result= jproject.findPackageFragment(path);
		if (!(result instanceof IPackageFragment)) {
			result= null;
		}
		return result;
	}	
	
	/** 
	 * Finds a type in a compilation unit. Typical usage is to find the corresponding
	 * type in a working copy.
	 * @param cu the compilation unit to search in
	 * @param typeQualifiedName the type qualified name (type name with enclosing type names (separated by dots))
	 * @return the type found, or null if not existing
	 */		
	public static IPart findPartInEGLFile(IEGLFile cu, String typeQualifiedName) throws EGLModelException {
		IPart[] types= cu.getAllParts();
		for (int i= 0; i < types.length; i++) {
			String currName= types[i].getElementName();
			if (typeQualifiedName.equals(currName)) {
				return types[i];
			}
		}
		return null;
	}
		
	/** 
	 * Finds a a member in a compilation unit. Typical usage is to find the corresponding
	 * member in a working copy.
	 * @param cu the compilation unit (eg. working copy) to search in
	 * @param member the member (eg. from the original)
	 * @return the member found, or null if not existing
	 */		
	public static IMember findMemberInEGLFile(IEGLFile cu, IMember member) throws EGLModelException {
		IEGLElement[] elements= cu.findElements(member);
		if (elements != null && elements.length > 0) {
			return (IMember) elements[0];
		}
		return null;
	}
	
	
	/** 
	 * Returns the element of the given compilation unit which is "equal" to the
	 * given element. Note that the given element usually has a parent different
	 * from the given compilation unit.
	 * 
	 * @param cu the cu to search in
	 * @param element the element to look for
	 * @return an element of the given cu "equal" to the given element
	 */		
	public static IEGLElement findInEGLFile(IEGLFile cu, IEGLElement element) throws EGLModelException {
		IEGLElement[] elements= cu.findElements(element);
		if (elements != null && elements.length > 0) {
			return elements[0];
		}
		return null;
	}
	
	/**
	 * Returns the fully qualified name of the given type using '.' as separators.
	 * This is a replace for IPart.getFullyQualifiedTypeName
	 * which uses '$' as separators. As '$' is also a valid character in an id
	 * this is ambiguous. 
	 */
	public static String getFullyQualifiedName(IPart type) {
		return type.getFullyQualifiedName('.');
	}
	
	/**
	 * Returns the fully qualified name of a type's container. (package name or enclosing type name)
	 */
	public static String getPartContainerName(IPart type) {
		IPart outerType= type.getDeclaringPart();
		if (outerType != null) {
			return outerType.getFullyQualifiedName('.');
		} else {
			return type.getPackageFragment().getElementName();
		}
	}
	
	
	/**
	 * Concatenates two names. Uses a dot for separation.
	 * Both strings can be empty or <code>null</code>.
	 */
	public static String concatenateName(String name1, String name2) {
		StringBuffer buf= new StringBuffer();
		if (name1 != null && name1.length() > 0) {
			buf.append(name1);
		}
		if (name2 != null && name2.length() > 0) {
			if (buf.length() > 0) {
				buf.append('.');
			}
			buf.append(name2);
		}		
		return buf.toString();
	}
	
	/**
	 * Concatenates two names. Uses a dot for separation.
	 * Both strings can be empty or <code>null</code>.
	 */
	public static String concatenateName(char[] name1, char[] name2) {
		StringBuffer buf= new StringBuffer();
		if (name1 != null && name1.length > 0) {
			buf.append(name1);
		}
		if (name2 != null && name2.length > 0) {
			if (buf.length() > 0) {
				buf.append('.');
			}
			buf.append(name2);
		}		
		return buf.toString();
	}	
	
	/**
	 * Evaluates if a member (possible from another package) is visible from
	 * elements in a package.
	 * @param member The member to test the visibility for
	 * @param pack The package in focus
	 */
	public static boolean isVisible(IMember member, IPackageFragment pack) throws EGLModelException {
//		if (member.isPublic())
//			return true;
//		
//		IPackageFragment otherpack= (IPackageFragment) findParentOfKind(member, IEGLElement.PACKAGE_FRAGMENT);
//		return (pack != null && pack.equals(otherpack));
		return true;  // TODO fix isVisable
	}
		
	/**
	 * Returns the package fragment root of <code>IEGLElement</code>. If the given
	 * element is already a package fragment root, the element itself is returned.
	 */
	public static IPackageFragmentRoot getPackageFragmentRoot(IEGLElement element) {
		return (IPackageFragmentRoot) element.getAncestor(IEGLElement.PACKAGE_FRAGMENT_ROOT);
	}

	/**
	 * Returns the parent of the supplied java element that conforms to the given 
	 * parent type or <code>null</code>, if such a parent doesn't exit.
	 * @deprecated Use element.getParent().getAncestor(kind);
	 */
	public static IEGLElement findParentOfKind(IEGLElement element, int kind) {
		if (element != null && element.getParent() != null) {
			return element.getParent().getAncestor(kind);
		}
		return null;
	}
	
	/**
	 * Finds a method in a type.
	 * This searches for a method with the same name and signature. Parameter types are only
	 * compared by the simple name, no resolving for the fully qualified type name is done.
	 * Constructors are only compared by parameters, not the name.
	 * @param name The name of the method to find
	 * @param paramTypes The type signatures of the parameters e.g. <code>{"QString;","I"}</code>
	 * @param isConstructor If the method is a constructor
	 * @return The first found method or <code>null</code>, if nothing found
	 */
	/*
	public static IFunction findFunction(String name, String[] paramTypes, boolean isConstructor, IPart type) throws EGLModelException {
		return findFunction(name, paramTypes, isConstructor, type.getFunctions());
	}
	*/
	/**
	 * Finds a method by name.
	 * This searches for a method with a name and signature. Parameter types are only
	 * compared by the simple name, no resolving for the fully qualified type name is done.
	 * Constructors are only compared by parameters, not the name.
	 * @param name The name of the method to find
	 * @param paramTypes The type signatures of the parameters e.g. <code>{"QString;","I"}</code>
	 * @param isConstructor If the method is a constructor
	 * @param methods The methods to search in
	 * @return The found method or <code>null</code>, if nothing found
	 */
	/*
	public static IFunction findFunction(String name, String[] paramTypes, boolean isConstructor, IFunction[] methods) throws EGLModelException {
		for (int i= methods.length - 1; i >= 0; i--) {
			if (isSameFunctionSignature(name, paramTypes, isConstructor, methods[i])) {
				return methods[i];
			}
		}
		return null;
	}
	
	*/
	/**
	 * Tests if a method equals to the given signature.
	 * Parameter types are only compared by the simple name, no resolving for
	 * the fully qualified type name is done. Constructors are only compared by
	 * parameters, not the name.
	 * @param Name of the method
	 * @param The type signatures of the parameters e.g. <code>{"QString;","I"}</code>
	 * @param Specifies if the method is a constructor
	 * @return Returns <code>true</code> if the method has the given name and parameter types and constructor state.
	 */
	/* TODO Handle Functions
	public static boolean isSameFunctionSignature(String name, String[] paramTypes, IFunction curr) throws EGLModelException {
		String[] currParamTypes= curr.getParameterTypes();
		if (paramTypes.length == currParamTypes.length) {
			for (int i= 0; i < paramTypes.length; i++) {
				String t1= Signature.getSimpleName(Signature.toString(paramTypes[i]));
				String t2= Signature.getSimpleName(Signature.toString(currParamTypes[i]));
				if (!t1.equals(t2)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	*/
	/**
	 * Checks whether the given type has a valid main method or not.
	 */
	/*
	public static boolean hasMainFunction(IPart type) throws EGLModelException {
		IFunction[] methods= type.getFunctions();
		for (int i= 0; i < methods.length; i++) {
			if (methods[i].isMainFunction()) {
				return true;
			}
		}
		return false;
	}
	*/
	/**
	 * Checks if the field is boolean.
	 */
	/*
	public static boolean isBoolean(IField field) throws EGLModelException{
		return field.getTypeSignature().equals(Signature.SIG_BOOLEAN);
	}
	*/
	/**
	 * Returns true if the element is on the build path of the given project
	 * @deprecated Use jproject.isOnEGLPath(element);
	 */	
	public static boolean isOnBuildPath(IEGLProject jproject, IEGLElement element) throws EGLModelException {
		return jproject.isOnEGLPath(element);
	}
	
	/**
	 * Tests if the given element is on the class path of its containing project. Handles the case
	 * that the containing project isn't a EGL project.
	 */
	public static boolean isOnEGLPath(IEGLElement element) {
		IEGLProject project= element.getEGLProject();
		if (!project.exists())
			return false;
		return project.isOnEGLPath(element);
	}

	/**
	 * Resolves a type name in the context of the declaring type.
	 * @param refTypeSig the type name in signature notation (for example 'QVector')
	 *                   this can also be an array type, but dimensions will be ignored.
	 * @param declaringType the context for resolving (type where the reference was made in)
	 * @return returns the fully qualified type name or build-in-type name. 
	 *  			if a unresoved type couldn't be resolved null is returned
	 */
	/*
	public static String getResolvedTypeName(String refTypeSig, IPart declaringType) throws EGLModelException {
		int arrayCount= Signature.getArrayCount(refTypeSig);
		char type= refTypeSig.charAt(arrayCount);
		if (type == Signature.C_UNRESOLVED) {
			int semi= refTypeSig.indexOf(Signature.C_SEMICOLON, arrayCount + 1);
			if (semi == -1) {
				throw new IllegalArgumentException();
			}
			String name= refTypeSig.substring(arrayCount + 1, semi);				
			
			String[][] resolvedNames= declaringType.resolveType(name);
			if (resolvedNames != null && resolvedNames.length > 0) {
				return EGLModelUtil.concatenateName(resolvedNames[0][0], resolvedNames[0][1]);
			}
			return null;
		} else {
			return Signature.toString(refTypeSig.substring(arrayCount));
		}
	}
	*/
	/**
	 * Returns if a CU can be edited.
	 */
	@SuppressWarnings("deprecation")
	public static boolean isEditable(IEGLFile cu)  {
		if (cu.isWorkingCopy()) {
			cu= (IEGLFile) cu.getOriginalElement();
		}
		IResource resource= cu.getResource();
		return (resource.exists() && !resource.isReadOnly());
	}

	/**
	 * Finds a qualified import for a type name.
	 */	
	public static IImportDeclaration findImport(IEGLFile cu, String simpleName) throws EGLModelException {
		IImportDeclaration[] existing= cu.getImports();
		for (int i= 0; i < existing.length; i++) {
			String curr= existing[i].getElementName();
			if (curr.endsWith(simpleName)) {
				int dotPos= curr.length() - simpleName.length() - 1;
				if ((dotPos == -1) || (dotPos > 0 && curr.charAt(dotPos) == '.')) {
					return existing[i];
				}
			}
		}	
		return null;
	}
	
	/**
	 * Returns the original if the given member. If the member is already
	 * an original the input is returned. The returned member must not exist
	 */
	/*
	public static IMember toOriginal(IMember member) {
		if (member instanceof IFunction)
			return toOriginalFunction((IFunction)member);
		IEGLFile cu= member.getEGLFile();
		if (cu != null && cu.isWorkingCopy())
			return (IMember)cu.getOriginal(member);
		return member;
	}
	*/
	/*
	 * XXX workaround for bug 18568
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=18568
	 * to be removed once the bug is fixed
	 */
	/*
	private static IFunction toOriginalFunction(IFunction method) {
		try{
			IEGLFile cu= method.getEGLFile();
			if (cu == null || ! cu.isWorkingCopy())
				return method;
			//use the workaround only if needed	
			if (! method.getElementName().equals(method.getDeclaringType().getElementName()))
				return (IFunction)cu.getOriginal(method);
			
			IPart originalType = (IPart)toOriginal(method.getDeclaringType());
			IFunction[] methods = originalType.findFunctions(method);
			boolean isConstructor = method.isConstructor();
			for (int i=0; i < methods.length; i++) {
			  if (methods[i].isConstructor() == isConstructor) 
				return methods[i];
			}
			return null;
		} catch(EGLModelException e){
			return null;
		}	
	}
	*/

	/**
	 * Returns the original cu if the given cu. If the cu is already
	 * an original the input cu is returned. The returned cu must not exist
	 */
	public static IEGLFile toOriginal(IEGLFile cu) {
		if (cu != null && cu.isWorkingCopy())
			return (IEGLFile) cu.getOriginal(cu);
		return cu;
	}	
	
	/**
	 * Returns the working copy of the given member. If the member is already in a
	 * working copy or the member does not exist in the working copy the input is returned.
	 */
	/*
	public static IMember toWorkingCopy(IMember member) {
		IEGLFile cu= member.getEGLFile();
		if (cu != null && !cu.isWorkingCopy()) {
			IEGLFile workingCopy= EditorUtility.getWorkingCopy(cu);
			if (workingCopy != null) {
				IEGLElement[] members= workingCopy.findElements(member);
				if (members != null && members.length > 0) {
					return (IMember) members[0];
				}
			}
		}
		return member;
	}
	*/

	/**
	 * Returns the working copy CU of the given CU. If the CU is already a
	 * working copy or the CU has no working copy the input CU is returned.
	 */	
	// TODO Handle interface to EGLDocumentProvider
	public static IEGLFile toWorkingCopy(IEGLFile cu) {
//		if (!cu.isWorkingCopy()) {
//			IEGLFile workingCopy= EditorUtility.getWorkingCopy(cu);
//			if (workingCopy != null) {
//				return workingCopy;
//			}
//		}
		return cu;
	}
	/*
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=19253
	 * 
	 * Reconciling happens in a separate thread. This can cause a situation where the
	 * EGL element gets disposed after an exists test has been done. So we should not
	 * log not present exceptions when they happen in working copies.
	 */
	public static boolean filterNotPresentException(CoreException exception) {
		if (!(exception instanceof EGLModelException))
			return true;
		EGLModelException je= (EGLModelException)exception;
		if (!je.isDoesNotExist())
			return true;
		IEGLElement[] elements= je.getEGLModelStatus().getElements();
		for (int i= 0; i < elements.length; i++) {
			IEGLElement element= elements[i];
			IEGLFile unit= (IEGLFile)element.getAncestor(IEGLElement.EGL_FILE);
			if (unit == null)
				return true;
			if (!unit.isWorkingCopy())
				return true;
		}
		return false;		
	}

	
	public static boolean isExcludedPath(IPath resourcePath, IPath[] exclusionPatterns) {
		char[] path = resourcePath.toString().toCharArray();
		for (int i = 0, length = exclusionPatterns.length; i < length; i++) {
			char[] pattern= exclusionPatterns[i].toString().toCharArray();
			if (CharOperation.pathMatch(pattern, path, true, '/')) {
				return true;
			}
		}
		return false;	
	}


	/*
	 * Returns whether the given resource path matches one of the exclusion
	 * patterns.
	 * 
	 * @see IEGLPathEntry#getExclusionPatterns
	 */
	public final static boolean isExcluded(IPath resourcePath, char[][] exclusionPatterns) {
		if (exclusionPatterns == null) return false;
		char[] path = resourcePath.toString().toCharArray();
		for (int i = 0, length = exclusionPatterns.length; i < length; i++)
			if (CharOperation.pathMatch(exclusionPatterns[i], path, true, '/'))
				return true;
		return false;
	}	
	
}
