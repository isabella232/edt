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
package org.eclipse.edt.ide.core.internal.search.matching;

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.core.ast.NestedForm;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.internal.core.utils.CharOperation;
import org.eclipse.edt.ide.core.internal.model.index.IEntryResult;
import org.eclipse.edt.ide.core.model.IIndexConstants;
import org.eclipse.edt.ide.core.model.IPart;

public class QualifiedPartDeclarationPattern extends PartDeclarationPattern {
	
	private char[] qualification;
	private char[] decodedQualification;
	
public QualifiedPartDeclarationPattern(
	char[] qualification,
	char[] simpleName,
	char partTypes,
	int matchMode, 
	boolean isCaseSensitive) {
		
	super(matchMode, isCaseSensitive);

	this.qualification = isCaseSensitive ? qualification : CharOperation.toLowerCase(qualification);
	this.simpleName = isCaseSensitive ? simpleName : CharOperation.toLowerCase(simpleName);
	this.partTypes = partTypes;
	
	this.needsResolve = qualification != null;
}

public void decodeIndexEntry(IEntryResult entryResult){
	
	char[] word = entryResult.getWord();
	isPartDecl = true;
	int size = word.length;
	//don't process entries that are refs
	if(CharOperation.prefixEquals(IIndexConstants.PART_DECL,word)){
		this.decodedPartTypes = word[PART_DECL_LENGTH];
		int oldSlash = PART_DECL_LENGTH+1;
		int slash = CharOperation.indexOf(SEPARATOR, word, oldSlash+1);
		char[] pkgName;
		if (slash == oldSlash+1){ 
			pkgName = CharOperation.NO_CHAR;
		} else {
			pkgName = CharOperation.subarray(word, oldSlash+1, slash);
		}
		this.decodedSimpleName = CharOperation.subarray(word, slash+1, slash = CharOperation.indexOf(SEPARATOR, word, slash+1));
	
		char[][] enclosingTypeNames;
		if (slash+1 < size){
			if (slash+3 == size && word[slash+1] == ONE_ZERO[0]) {
				enclosingTypeNames = ONE_ZERO_CHAR;
			} else {
				enclosingTypeNames = CharOperation.splitOn('/', CharOperation.subarray(word, slash+1, size-1));
			}
		} else {
			enclosingTypeNames = CharOperation.NO_CHAR_CHAR;
		}
		this.decodedQualification = CharOperation.concatWith(pkgName, enclosingTypeNames, '.');
	}else isPartDecl = false;
}


/**
 * see SearchPattern.matchIndexEntry
 */
protected boolean matchIndexEntry(){

	/* check part type nature */
	if (!partTypesMatch()) return false;
	if (!isPartDecl) return false;
	
	/* check qualification */
	if (qualification != null) {
		switch(matchMode){
			case EXACT_MATCH :
				if (!CharOperation.equals(qualification, decodedQualification, isCaseSensitive)){
					return false;
				}
				break;
			case PREFIX_MATCH :
				if (!CharOperation.prefixEquals(qualification, decodedQualification, isCaseSensitive)){
					return false;
				}
				break;
			case PATTERN_MATCH :
				if (!CharOperation.match(qualification, decodedQualification, isCaseSensitive)){
					return false;
				}
		}
	}
	/* check simple name matches */
	if (simpleName != null){
		switch(matchMode){
			case EXACT_MATCH :
				if (!CharOperation.equals(simpleName, decodedSimpleName, isCaseSensitive)){
					return false;
				}
				break;
			case PREFIX_MATCH :
				if (!CharOperation.prefixEquals(simpleName, decodedSimpleName, isCaseSensitive)){
					return false;
				}
				break;
			case PATTERN_MATCH :
				if (!CharOperation.match(simpleName, decodedSimpleName, isCaseSensitive)){
					return false;
				}
		}
	}
	return true;
}

public int matchesNestedFormPart(NestedForm node){
	if (partTypes != IIndexConstants.FORM_SUFFIX && partTypes != IIndexConstants.PART_SUFFIX){
		return IMPOSSIBLE_MATCH;
	}
	
	IPartBinding partBinding = getPartBinding(node.getName());
	if (partBinding != null && partBinding != IBinding.NOT_FOUND_BINDING){
//		char[] enclosingTypeName = this.enclosingTypeNames == null ? null : CharOperation.concatWith(this.enclosingTypeNames, '.');
		return this.matchLevelForType(this.simpleName, this.qualification, null, getPartBinding(node.getName()));
	}else return INACCURATE_MATCH;
}
/**
 * @see SearchPattern#matchLevel(IEGLPart)
 */
public int matchLevel(Part part) {
	return this.matchLevelForType(this.simpleName, this.qualification, getPartBinding(part));
}

public int matchLevel(IPart part){
	return this.matchLevelForType(this.simpleName, this.qualification, part);
}

public String toString(){
	StringBuffer buffer = new StringBuffer(20);
	buffer.append("PartDeclarationPattern: qualification<"); //$NON-NLS-1$
	if (this.qualification != null) buffer.append(this.qualification);
	buffer.append(">, type<"); //$NON-NLS-1$
	if (simpleName != null) buffer.append(simpleName);
	buffer.append(">, "); //$NON-NLS-1$
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
}
