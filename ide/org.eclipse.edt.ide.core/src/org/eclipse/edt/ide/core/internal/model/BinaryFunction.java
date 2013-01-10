/*******************************************************************************
 * Copyright © 2010, 2013 IBM Corporation and others.
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
import org.eclipse.edt.ide.core.model.IFunction;
import org.eclipse.edt.ide.core.model.IPart;

public class BinaryFunction extends BinaryPart implements IFunction {
	/**
	 * The parameter type signatures of the function - stored locally
	 * to perform equality test. <code>null</code> indicates no
	 * parameters.
	 */
	protected String[] fParameterTypes;	//the signatures for the type
	/**
	 * An empty list of Strings
	 */
	protected static final String[] fgEmptyList= new String[] {};
	
	protected BinaryFunction(IPart parent, String name, String[] parameterTypes) {
		super(FUNCTION, parent, name);
//		Assert.isTrue(name.indexOf('#') == -1);
		if (parameterTypes == null) {
			fParameterTypes= fgEmptyList;
		} else {
			fParameterTypes= parameterTypes;
		}
	}

	public int getNumberOfParameters() {
		return fParameterTypes == null ? 0 : fParameterTypes.length;
	}

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

	public String[] getParameterTypes() {
		return fParameterTypes;
	}

	public String getReturnTypeName() throws EGLModelException {
		SourceFunctionElementInfo info = (SourceFunctionElementInfo) getElementInfo();
//		return Signature.createTypeSignature(info.getReturnTypeName(), false);	
		return String.valueOf(info.getReturnTypeName());
	}

	public String getSignature() throws EGLModelException {
		SourceFunctionElementInfo info = (SourceFunctionElementInfo) getElementInfo();
		return info.getSignature();
	}

	public boolean isSimilar(IFunction function) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public String getElementName() {
		return fName;
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
	
	public boolean[] getNullable() throws EGLModelException{
		SourceFunctionElementInfo info = (SourceFunctionElementInfo) getElementInfo();
		boolean[] areNullable = info.getAreNullable();
		return areNullable;
	}
	
	public String[] getParameterPackages() throws EGLModelException {
		SourceFunctionElementInfo info = (SourceFunctionElementInfo) getElementInfo();
		char[][] parameterPkgs = info.getArgumentPackages();
		if(parameterPkgs == null){
			return null;
		}
		if(parameterPkgs.length == 0){
			return fgEmptyList;
		}
		String[] strings = new String[parameterPkgs.length];
		for (int i= 0; i < parameterPkgs.length; i++) {
			if(parameterPkgs[i] != null)
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
