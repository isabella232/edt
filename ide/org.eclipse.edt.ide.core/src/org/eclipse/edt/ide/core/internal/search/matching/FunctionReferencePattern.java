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

import java.io.IOException;

import org.eclipse.edt.compiler.binding.TopLevelFunctionBinding;
import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.utils.CharOperation;
import org.eclipse.edt.ide.core.internal.model.index.IEntryResult;
import org.eclipse.edt.ide.core.internal.model.index.impl.IndexInput;
import org.eclipse.edt.ide.core.internal.model.index.impl.IndexedFile;
import org.eclipse.edt.ide.core.internal.model.indexing.AbstractIndexer;
import org.eclipse.edt.ide.core.internal.search.IIndexSearchRequestor;
import org.eclipse.edt.ide.core.model.IIndexConstants;
import org.eclipse.edt.ide.core.model.IMember;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;

public class FunctionReferencePattern extends FunctionPattern {
	IPart declaringPart;
	
public FunctionReferencePattern(
	char[] selector, 
	int matchMode, 
	boolean isCaseSensitive,
	char[] declaringQualification,
	//char[] declaringSimpleName,	
	IPart declaringPart) {

	super(matchMode, isCaseSensitive);
	
	this.selector = isCaseSensitive ? selector : CharOperation.toLowerCase(selector);
	this.declaringQualification = isCaseSensitive ? declaringQualification : CharOperation.toLowerCase(declaringQualification);
	//this.declaringSimpleName = isCaseSensitive ? declaringSimpleName : CharOperation.toLowerCase(declaringSimpleName);
	this.declaringPart = declaringPart;
	this.needsResolve = this.needsResolve();
}
public void decodeIndexEntry(IEntryResult entryResult){

	char[] word = entryResult.getWord();
	int size = word.length;
	int lastSeparatorIndex = CharOperation.lastIndexOf(SEPARATOR, word);	
	
	if(CharOperation.prefixEquals(IIndexConstants.REF,word)){
		decodedParameterCount = 0;
		decodedSelector = CharOperation.subarray(word, REF.length,word.length);
	}else{
		decodedParameterCount = Integer.parseInt(new String(word, lastSeparatorIndex + 1, size - lastSeparatorIndex - 1));
		decodedSelector = CharOperation.subarray(word, FUNCTION_REF.length, lastSeparatorIndex);
	}
}
/**
 * see SearchPattern.feedIndexRequestor
 */
public void feedIndexRequestor(IIndexSearchRequestor requestor, int detailLevel, int[] references, IndexInput input, IEGLSearchScope scope) throws IOException {
	for (int i = 0, max = references.length; i < max; i++) {
		IndexedFile file = input.getIndexedFile(references[i]);
		String path;
		if (file != null && scope.encloses(path = IndexedFile.convertPath(file.getPath()))) {
			requestor.acceptFunctionReference(path, decodedSelector, decodedParameterCount);
		}
	}
}
public String getPatternName(){
	return "FunctionReferencePattern: "; //$NON-NLS-1$
}
/**
 * @see SearchPattern#indexEntryPrefix
 */
public char[] indexEntryPrefix() {

	return AbstractIndexer.bestMethodReferencePrefix(
			selector, 
			-1, // EGL does not search based on arity
			matchMode, 
			isCaseSensitive);
}
/**
 * @see SearchPattern#matchContainer()
 */
protected int matchContainer() {
	return FUNCTION | FIELD;
}



/* (non-Javadoc)
 * @see com.ibm.etools.egl.model.internal.core.search.matching.SearchPattern#matchLevel(com.ibm.etools.egl.internal.pgm.INode, boolean)
 */
public int matchLevel(Node node, boolean resolve) {
	if (node instanceof ArrayType){
		node = ((ArrayType)node).getBaseType();
	}
	if (node instanceof Name) {
		// selector
		if (this.selector != null && this.matchesName(this.selector, ((Name)node).getCaseSensitiveIdentifier().toCharArray())){
			return POSSIBLE_MATCH;
		}
	}
	
	return IMPOSSIBLE_MATCH;
}

	@Override
	public int matchLevel(IMember member, boolean resolve) {
		// TODO Rocky
		//TODO verify if correct?
		if (!this.matchesName(this.selector, member.getElementName().toCharArray()))
			return IMPOSSIBLE_MATCH;
		return POSSIBLE_MATCH;
	}
public int matchLevel(TopLevelFunctionBinding function){
	return INACCURATE_MATCH;
	
	//TODO search
//	
//	IEGLFunction function = functionBinding.getFunctionTSN();
//	if(function == null) return INACCURATE_MATCH;
//	int level;
//	
//	level = this.matchLevelForType(this.selector, this.declaringQualification, function);
//	if (level == IMPOSSIBLE_MATCH) return IMPOSSIBLE_MATCH;
//	
//// Use argument types in the future?
//////	 argument types
////	int argumentCount = this.parameterSimpleNames == null ? -1 : this.parameterSimpleNames.length;
////	if (argumentCount > -1) {
////		if (method.parameters == null) {
////			level = INACCURATE_MATCH;
////		} else {
////			int parameterCount = method.parameters.length;
////			if (parameterCount != argumentCount) return IMPOSSIBLE_MATCH;
////			for (int i = 0; i < parameterCount; i++) {
////				char[] qualification = this.parameterQualifications[i];
////				char[] type = this.parameterSimpleNames[i];
////				int newLevel = this.matchLevelForType(type, qualification, method.parameters[i]);
////				switch (newLevel) {
////					case IMPOSSIBLE_MATCH:
////						return IMPOSSIBLE_MATCH;
////					case ACCURATE_MATCH: // keep previous level
////						break;
////					default: // ie. INACCURATE_MATCH
////						level = newLevel;
////						break;
////				}
////			}
////		}
////	}
//	return level;
}
@Override
public int getPatternType() {
	return SearchPattern.REFERENCE;
}
}
