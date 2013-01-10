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

import java.util.ArrayList;

import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IField;
import org.eclipse.edt.ide.core.model.IFunction;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.Signature;
import org.eclipse.edt.ide.core.internal.model.codeassist.SelectionEngine;

import org.eclipse.edt.compiler.internal.core.utils.CharOperation;

/**
 * Implementation of <code>ISelectionRequestor</code> to assist with
 * code resolve in a compilation unit. Translates names to elements.
 */
public class SelectionRequestor /* implements ISelectionRequestor */{
	// TODO Code commented out to fit in build
	
	public static boolean DEBUG = false;

	/**
	 * The name lookup facility used to resolve packages
	 */
	protected NameLookup fNameLookup= null;

	/**
	 * Fix for 1FVXGDK
	 *
	 * The compilation unit we are resolving in
	 */
	protected IEGLElement fCodeResolve;

	/**
	 * The collection of resolved elements.
	 */
	protected IEGLElement[] fElements= fgEmptyElements;

	/**
	 * Empty collection used for efficiency.
	 */
	protected static IEGLElement[] fgEmptyElements = new IEGLElement[]{};
/**
 * Creates a selection requestor that uses that given
 * name lookup facility to resolve names.
 *
 * Fix for 1FVXGDK
 */
public SelectionRequestor(NameLookup nameLookup, IEGLElement codeResolve) {
	super();
	fNameLookup = nameLookup;
	fCodeResolve = codeResolve;
}
/**
 * Resolve the binary method
 *
 * fix for 1FWFT6Q
 */
protected void acceptBinaryMethod(IPart type, char[] selector, char[][] parameterPackageNames, char[][] parameterPartNames) {
	String[] parameterParts= null;
	if (parameterPartNames != null) {
		parameterParts= new String[parameterPartNames.length];
		for (int i= 0, max = parameterPartNames.length; i < max; i++) {
			String pkg = IPackageFragment.DEFAULT_PACKAGE_NAME;
			if (parameterPackageNames[i] != null && parameterPackageNames[i].length > 0) {
				pkg = new String(parameterPackageNames[i]) + "."; //$NON-NLS-1$
			}
			
			String typeName = new String(parameterPartNames[i]);
			if (typeName.indexOf('.') > 0) 
				typeName = typeName.replace('.', '$');
			parameterParts[i]= Signature.createTypeSignature(
				pkg + typeName, true);
		}
	}
	IFunction method= type.getFunction(new String(selector), parameterParts);
	if (method.exists()) {
		fElements = growAndAddToArray(fElements, method);
		if(DEBUG){
			System.out.print("SELECTION - accept method("); //$NON-NLS-1$
			System.out.print(method.toString());
			System.out.println(")"); //$NON-NLS-1$
		}
	}
}
/**
 * Resolve the class.
 */
public void acceptPart(char[] packageName, char[] className, boolean needQualification) {
	acceptPart(packageName, className, NameLookup.ACCEPT_PARTS, needQualification);
}
/**
 * Do nothing.
 */
// public void acceptError(IProblem error) {}
/**
 * Resolve the field.
 */
public void acceptField(char[] declaringPartPackageName, char[] declaringPartName, char[] name) {
	IPart type= resolvePart(declaringPartPackageName, declaringPartName,
		NameLookup.ACCEPT_PARTS);
	if (type != null) {
		IField field= type.getField(new String(name));
		if (field.exists()) {
			fElements= growAndAddToArray(fElements, field);
			if(DEBUG){
				System.out.print("SELECTION - accept field("); //$NON-NLS-1$
				System.out.print(field.toString());
				System.out.println(")"); //$NON-NLS-1$
			}
		}
	}
}
/**
 * Resolve the method
 */
public void acceptFunction(char[] declaringPartPackageName, char[] declaringPartName, char[] selector, char[][] parameterPackageNames, char[][] parameterPartNames, boolean isConstructor) {
	IPart type= resolvePart(declaringPartPackageName, declaringPartName,
		NameLookup.ACCEPT_PARTS);
	// fix for 1FWFT6Q
	if (type != null) {
		acceptSourceFunction(type, selector, parameterPackageNames, parameterPartNames);
	}
}
/**
 * Resolve the package
 */
public void acceptPackage(char[] packageName) {
	IPackageFragment[] pkgs = fNameLookup.findPackageFragments(new String(packageName), false);
	if (pkgs != null) {
		for (int i = 0, length = pkgs.length; i < length; i++) {
			fElements = growAndAddToArray(fElements, pkgs[i]);
			if(DEBUG){
				System.out.print("SELECTION - accept package("); //$NON-NLS-1$
				System.out.print(pkgs[i].toString());
				System.out.println(")"); //$NON-NLS-1$
			}
		}
	}
}
/**
 * Resolve the source method
 *
 * fix for 1FWFT6Q
 */
protected void acceptSourceFunction(IPart type, char[] selector, char[][] parameterPackageNames, char[][] parameterPartNames) {
	String name = new String(selector);
	IFunction[] methods = null;
	IEGLElement[] matches = new IEGLElement[] {};
	try {
		methods = type.getFunctions();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getElementName().equals(name) && methods[i].getParameterTypes().length == parameterPartNames.length) {
				matches = growAndAddToArray(matches, methods[i]);
			}
		}
	} catch (EGLModelException e) {
		return; 
	}

	// if no matches, nothing to report
	if (matches.length == 0) {
		// no match was actually found, but a method was originally given -> default constructor
		fElements = growAndAddToArray(fElements, type);
		if(DEBUG){
			System.out.print("SELECTION - accept type("); //$NON-NLS-1$
			System.out.print(type.toString());
			System.out.println(")"); //$NON-NLS-1$
		}
		return;
	}

	// if there is only one match, we've got it
	if (matches.length == 1) {
		fElements = growAndAddToArray(fElements, matches[0]);
		if(DEBUG){
			System.out.print("SELECTION - accept method("); //$NON-NLS-1$
			System.out.print(matches[0].toString());
			System.out.println(")"); //$NON-NLS-1$
		}
		return;
	}

	// more than one match - must match simple parameter types
	for (int i = 0; i < matches.length; i++) {
		IFunction method= (IFunction)matches[i];
		String[] signatures = method.getParameterTypes();
		boolean match= true;
		for (int p = 0; p < signatures.length; p++) {
			String simpleName= Signature.getSimpleName(Signature.toString(signatures[p]));
			char[] simpleParameterName = CharOperation.lastSegment(parameterPartNames[p], '.');
			if (!simpleName.equals(new String(simpleParameterName))) {
				match = false;
				break;
			}
		}
		if (match) {
			fElements = growAndAddToArray(fElements, method);
			if(SelectionEngine.DEBUG){
				System.out.print("SELECTION - accept method("); //$NON-NLS-1$
				System.out.print(method.toString());
				System.out.println(")"); //$NON-NLS-1$
			}
		}
	}
	
}
/**
 * Resolve the type, adding to the resolved elements.
 */
protected void acceptPart(char[] packageName, char[] typeName, int acceptFlags, boolean needQualification) {
	IPart type= resolvePart(packageName, typeName, acceptFlags);
	if (type != null) {
		fElements= growAndAddToArray(fElements, type);
		if(DEBUG){
			System.out.print("SELECTION - accept type("); //$NON-NLS-1$
			System.out.print(type.toString());
			System.out.println(")"); //$NON-NLS-1$
		}
	} 
	
}
/**
 * Returns the resolved elements.
 */
public IEGLElement[] getElements() {
	return fElements;
}
/**
 * Adds the new element to a new array that contains all of the elements of the old array.
 * Returns the new array.
 */
protected IEGLElement[] growAndAddToArray(IEGLElement[] array, IEGLElement addition) {
	IEGLElement[] old = array;
	array = new IEGLElement[old.length + 1];
	System.arraycopy(old, 0, array, 0, old.length);
	array[old.length] = addition;
	return array;
}
/**
 * Resolve the type
 */
protected IPart resolvePart(char[] packageName, char[] typeName, int acceptFlags) {

	IPart type= null;
	
	if (fCodeResolve instanceof WorkingCopy) {
		WorkingCopy wc = (WorkingCopy) fCodeResolve;
		try {
			if(((packageName == null || packageName.length == 0) && wc.getPackageDeclarations().length == 0) ||
				(!(packageName == null || packageName.length == 0) && wc.getPackageDeclaration(new String(packageName)).exists())) {
					
				char[][] compoundName = CharOperation.splitOn('.', typeName);
				if(compoundName.length > 0) {
					type = wc.getPart(new String(compoundName[0]));
					for (int i = 1, length = compoundName.length; i < length; i++) {
						type = type.getPart(new String(compoundName[i]));
					}
				}
				
				if(type != null && !type.exists()) {
					type = null;
				}
			}
		}catch (EGLModelException e) {
			type = null;
		}
	}

	if(type == null) {
		IPackageFragment[] pkgs = fNameLookup.findPackageFragments(
			(packageName == null || packageName.length == 0) ? IPackageFragment.DEFAULT_PACKAGE_NAME : new String(packageName), 
			false);
		// iterate type lookup in each package fragment
		for (int i = 0, length = pkgs == null ? 0 : pkgs.length; i < length; i++) {
			type= fNameLookup.findPart(new String(typeName), pkgs[i], false, acceptFlags);
			if (type != null) break;	
		}
		if (type == null) {
			String pName= IPackageFragment.DEFAULT_PACKAGE_NAME;
			if (packageName != null) {
				pName = new String(packageName);
			}
			if (fCodeResolve != null && fCodeResolve.getParent().getElementName().equals(pName)) {
				// look inside the type in which we are resolving in
				String tName= new String(typeName);
				tName = tName.replace('.','$');
				IPart[] allParts= null;
				try {
					ArrayList list = ((EGLElement)fCodeResolve).getChildrenOfType(IEGLElement.PART);
					allParts = new IPart[list.size()];
					list.toArray(allParts);
				} catch (EGLModelException e) {
					return null;
				}
//				for (int i= 0; i < allParts.length; i++) {
//					if (allParts[i].getPartQualifiedName().equals(tName)) {
//						return allParts[i];
//					}
//				}
			}
		}
	}
	return type;
}
}
