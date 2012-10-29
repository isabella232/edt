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

import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
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
import org.eclipse.edt.mof.egl.Type;

public class FunctionDeclarationPattern extends FunctionPattern {
public FunctionDeclarationPattern(
	char[] selector, 
	int matchMode, 
	boolean isCaseSensitive,
	char[] declaringQualification){
	//char[] declaringSimpleName,	
	//char[] returnQualification, 
	//char[] returnSimpleName,
	//char[][] parameterQualifications, 
	//char[][] parameterSimpleNames) {

	super(matchMode, isCaseSensitive);

	this.selector = isCaseSensitive ? selector : CharOperation.toLowerCase(selector);
	this.declaringQualification = isCaseSensitive ? declaringQualification : CharOperation.toLowerCase(declaringQualification);
	//this.declaringSimpleName = isCaseSensitive ? declaringSimpleName : CharOperation.toLowerCase(declaringSimpleName);
	//this.returnQualification = isCaseSensitive ? returnQualification : CharOperation.toLowerCase(returnQualification);
	//this.returnSimpleName = isCaseSensitive ? returnSimpleName : CharOperation.toLowerCase(returnSimpleName);

//	if (parameterSimpleNames != null){
		//this.parameterQualifications = new char[parameterSimpleNames.length][];
		//this.parameterSimpleNames = new char[parameterSimpleNames.length][];
		//for (int i = 0, max = parameterSimpleNames.length; i < max; i++){
		//	this.parameterQualifications[i] = isCaseSensitive ? parameterQualifications[i] : CharOperation.toLowerCase(parameterQualifications[i]);
		//	this.parameterSimpleNames[i] = isCaseSensitive ? parameterSimpleNames[i] : CharOperation.toLowerCase(parameterSimpleNames[i]);
		//}
//	}	
	this.needsResolve = this.needsResolve();
}
public void decodeIndexEntry(IEntryResult entryResult){

	char[] word = entryResult.getWord();
	int size = word.length;
	int lastSeparatorIndex = CharOperation.lastIndexOf(SEPARATOR, word);	

	//don't process entries that are refs
	if(!CharOperation.prefixEquals(IIndexConstants.REF,word)){
		decodedParameterCount = Integer.parseInt(new String(word, lastSeparatorIndex + 1, size - lastSeparatorIndex - 1));
		decodedSelector = CharOperation.subarray(word, FUNCTION_DECL.length, lastSeparatorIndex);
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
			requestor.acceptFunctionDeclaration(path, decodedSelector, decodedParameterCount);
		}
	}
}
public String getPatternName(){
	return "FunctionDeclarationPattern: "; //$NON-NLS-1$
}
/**
 * @see SearchPattern#indexEntryPrefix
 */
public char[] indexEntryPrefix() {

	return AbstractIndexer.bestFunctionDeclarationPrefix(
			selector, 
			-1, // EGL does not search based on arity 
			matchMode, 
			isCaseSensitive);
}
/**
 * @see SearchPattern#matchContainer()
 */
protected int matchContainer() {
	return EGL_FILE | FUNCTION;
}

/**
 * @see SearchPattern#matchLevel(AstNode, boolean)
 */
public int matchLevel(Node node, boolean resolve) {
	if (node instanceof NestedFunction){
		NestedFunction function = (NestedFunction)node;

		if (!this.matchesName(this.selector, function.getName().getCanonicalName().toCharArray()))
			return IMPOSSIBLE_MATCH;
		
	}else return IMPOSSIBLE_MATCH;

	// EGLTODO: Support searching based on return types and parameters?
		// return type
//		TypeReference functionReturnType = function.returnType;
//		if (functionReturnType != null) {
//			char[][] functionReturnTypeName = functionReturnType.getTypeName();
//			char[] sourceName = this.toArrayName(
//				functionReturnTypeName[functionReturnTypeName.length-1], 
//				functionReturnType.dimensions());
//			if (!this.matchesName(this.returnSimpleName, sourceName))
//				return IMPOSSIBLE_MATCH;
//		}
//			
//		// parameter types
//		int parameterCount = this.parameterSimpleNames == null ? -1 : this.parameterSimpleNames.length;
//		if (parameterCount > -1) {
//			int argumentCount = function.arguments == null ? 0 : function.arguments.length;
//			if (parameterCount != argumentCount)
//				return IMPOSSIBLE_MATCH;
//		}

		return POSSIBLE_MATCH ;

}

	@Override
	public int matchLevel(IMember member, boolean resolve) {
		//TODO Rocky
		if (!this.matchesName(this.selector, member.getElementName().toCharArray()))
			return IMPOSSIBLE_MATCH;
		return POSSIBLE_MATCH;
	}

protected org.eclipse.edt.mof.egl.Part getPartBinding(Part part){
	Type type = part.getName().resolveType();
	if (type instanceof org.eclipse.edt.mof.egl.Part){
		return (org.eclipse.edt.mof.egl.Part)type;
	}
	return null;
}

//public int matchLevelForType(char[] simpleNamePattern, char[] qualificationPattern,TopLevelFunction function) {
//	return  IMPOSSIBLE_MATCH;
//}

public int matchTopLevelFunctionLevel(IPart functionPart) {
	if (functionPart == null) return INACCURATE_MATCH;
	int level;
	
	level = this.matchLevelForType(this.selector, this.declaringQualification, functionPart);
	if (level == IMPOSSIBLE_MATCH) return IMPOSSIBLE_MATCH;

	return level;
}

@Override
public int getPatternType() {
	return SearchPattern.DECLARATION;
}
}
