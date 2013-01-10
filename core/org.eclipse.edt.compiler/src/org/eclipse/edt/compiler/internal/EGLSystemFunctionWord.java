/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal;

/**
 * @author jshavor
 * 
 * This class represent an EGL system word which is a function system word
 */
public class EGLSystemFunctionWord extends EGLSystemWord {
	private String parameterNames[];
	private String parameterTypes[];
	private String returnType;
	private int returnLength;
	private int[] parameterFunctionModifiers;
	private int[] validArgumentCounts; // negative valid in validArgumentCounts means "at least that many args"
	
	public EGLSystemFunctionWord(
		String name,
		int type,
		int specialFunctionType,
		String library,
		String returnType,
		int returnLength,
		String parameterNames[],
		String[] parameterTypes,
		int[] parameterFunctionModifiers,
		int[] validArgumentCounts) {
			super(name, type, specialFunctionType, library);
			this.returnType = returnType;
			this.returnLength = returnLength;
			this.parameterNames = parameterNames;
			this.parameterTypes = parameterTypes;
			this.parameterFunctionModifiers = parameterFunctionModifiers;
			if (isArrayWord())
				this.additonalInformation = EGLBaseNlsStrings.CAProposalArrayFunctionSystemWord;
			else
				this.additonalInformation = EGLBaseNlsStrings.CAProposalFunctionSystemWord;
			this.validArgumentCounts = validArgumentCounts;
	}

	public int getNumberOfParameters() {
		return parameterNames.length;
	}

	public boolean hasReturnCode() {
		return (type & EGLSystemWordHandler.SYSTEM_WORD_RETURNS) != 0;
	}
	
	public boolean returnTypeIsNullable() {
		return (type & EGLSystemWordHandler.SYSTEM_WORD_RETURN_TYPE_IS_NULLABLE) != 0;
	}

	public boolean isSystemFunction() {
		return true;
	}

	public String toString() {
		return toString(parameterNames.length);
	}

	public String toString(int numberOfParms) {
		//guard against out of boundary exception
		if (numberOfParms > parameterNames.length)
			numberOfParms = parameterNames.length;
		StringBuffer buffer = new StringBuffer(name);
		buffer.append('(');
		for (int i = 0; i < numberOfParms; i++) {
			if (i > 0)
				buffer.append(", "); //$NON-NLS-1$
			buffer.append(parameterNames[i]);
		}
		buffer.append(")"); //$NON-NLS-1$
		return buffer.toString();
	}

	public int getReturnLength() {
		return returnLength;
	}

	public String getReturnType() {
		return returnType;
	}
	
	public int[] getValidArgumentCounts() {
		return validArgumentCounts;
	}

	/**
	 * @return
	 */
	public String[] getParameterNames() {
		return parameterNames;
	}
	public String[] getParameterNames(int numberOfParameters) {
		String[] parameterNamesArray = new String[numberOfParameters];
		for (int i = 0; i < parameterNamesArray.length; i++) {
			parameterNamesArray[i] = parameterNames[i];
		}
		return parameterNamesArray;
	}
	
	public String[] getParameterTypes() {
		return parameterTypes;	
	}
	
	public int[] getParameterFunctionModifiers() {
		return parameterFunctionModifiers;
	}
}
