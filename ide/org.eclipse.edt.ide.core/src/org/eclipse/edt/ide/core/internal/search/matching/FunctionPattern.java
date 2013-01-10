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
package org.eclipse.edt.ide.core.internal.search.matching;

import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.internal.core.utils.CharOperation;
import org.eclipse.edt.ide.core.model.IFunction;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.FunctionPart;
import org.eclipse.edt.mof.egl.Member;

public abstract class FunctionPattern extends SearchPattern {

	// selector	
	protected char[] selector;
	
	//	 declaring type
	protected char[] declaringQualification;
	//protected char[] declaringSimpleName;
	
	protected char[] decodedSelector;
	protected int decodedParameterCount;	
	
public FunctionPattern(int matchMode, boolean isCaseSensitive) {
	super(matchMode, isCaseSensitive);
}
public abstract String getPatternName();
/**
 * @see SearchPattern#matchIndexEntry
 */
protected boolean matchIndexEntry() {

	/* check selector matches */
	if (selector != null){
		switch(matchMode){
			case EXACT_MATCH :
				if (!CharOperation.equals(selector, decodedSelector, isCaseSensitive)){
					return false;
				}
				break;
			case PREFIX_MATCH :
				if (!CharOperation.prefixEquals(selector, decodedSelector, isCaseSensitive)){
					return false;
				}
				break;
			case PATTERN_MATCH :
				if (!CharOperation.match(selector, decodedSelector, isCaseSensitive)){
					return false;
				}
		}
	}
//	if (parameterSimpleNames != null){
//		if (parameterSimpleNames.length != decodedParameterCount) return false;
//	}
	return true;
}
/**
 * Returns whether a method declaration or message send will need to be resolved to 
 * find out if this method pattern matches it.
 */
protected boolean needsResolve() {

	// TODO: (jerome) should need resolve only if declaringSimpleName, declaringQualification, returnQualification or parameterQualifications[i] is not null

	// declaring type
	if (/*declaringSimpleName != null ||*/ declaringQualification != null) return true;

//	// return type
//	if (returnSimpleName != null || returnQualification != null) return true;
//
//	// parameter types
//	if (parameterSimpleNames != null){
//		for (int i = 0, max = parameterSimpleNames.length; i < max; i++){
//			if (parameterQualifications[i] != null || parameterSimpleNames[i] != null) return true;
//		}
//	}
	return false;
}
public String toString(){

	StringBuffer buffer = new StringBuffer(20);
	buffer.append(this.getPatternName());
	if (selector != null) {
		buffer.append(selector);
	} else {
		buffer.append("*"); //$NON-NLS-1$
	}
	buffer.append('(');
	buffer.append(')');
	buffer.append(", "); //$NON-NLS-1$
	switch(matchMode){
		case EXACT_MATCH : 
			buffer.append("exact match, "); //$NON-NLS-1$
			break;
		case PREFIX_MATCH :
			buffer.append("prefix match, "); //$NON-NLS-1$
			break;
		case PATTERN_MATCH :
			buffer.append("pattern match, "); //$NON-NLS-1$
			break;
	}
	if (isCaseSensitive)
		buffer.append("case sensitive"); //$NON-NLS-1$
	else
		buffer.append("case insensitive"); //$NON-NLS-1$
	return buffer.toString();
}

protected int matchLevelForType(char[] simpleNamePattern, char[] qualificationPattern,String packageName,String partName) {
	if (partName == null  ) return INACCURATE_MATCH;
	
	StringBuffer qualifiedPartName = new StringBuffer();
	char[] qualifiedPackageName;

	qualifiedPartName.append(partName);
	
	if(packageName != null) {
		qualifiedPackageName = packageName.toCharArray();
	}else{
		qualifiedPackageName = null;
	}
	char[] qualifiedSourceName = qualifiedPartName.toString().toCharArray();
	
	if (this.matchesType(
			simpleNamePattern, 
			qualificationPattern, 
			qualifiedPackageName == null ? 
				qualifiedSourceName : 
				CharOperation.concat(qualifiedPackageName, qualifiedSourceName, '.'))) {
		return ACCURATE_MATCH;
	} else {
		if(qualificationPattern != null && new String(qualificationPattern).indexOf('.') == -1){
			if(this.matchesType(
					CharOperation.concat(qualificationPattern, simpleNamePattern, '.'),
					null,
					qualifiedPackageName == null ? 
							qualifiedSourceName : 
							CharOperation.concat(qualifiedPackageName, qualifiedSourceName, '.'))){
				return ACCURATE_MATCH;
			}
		}
	}
	return IMPOSSIBLE_MATCH;
	
}

public int matchesFunctionPartType(Name node,FunctionPart partBinding){
	if (partBinding != null){
		return this.matchLevelForType(this.selector, this.declaringQualification, partBinding);
	}
	
	// Nested functions aren't types
	Member m = node.resolveMember();
	
	if (m instanceof FunctionMember){
		Container parent = m.getContainer();
		String packageName;
		String partName;
		if (parent instanceof Classifier) {
			packageName = ((Classifier)parent).getCaseSensitivePackageName().length() > 0 ? ((Classifier)parent).getCaseSensitivePackageName() : null;
			partName = ((Classifier)parent).getCaseSensitiveName() + "." + m.getCaseSensitiveName();
		}
		else {
			packageName = null;
			partName = m.getCaseSensitiveName();
		}
		
		return matchLevelForType(this.selector, this.declaringQualification,packageName,partName);
	}

	return INACCURATE_MATCH;
}

	@Override
	public int matchesFunctionPartType(IFunction function){
		if (function != null){
			return this.matchLevelForType(this.selector, this.declaringQualification, function);
		}

		return INACCURATE_MATCH;
	}

}
