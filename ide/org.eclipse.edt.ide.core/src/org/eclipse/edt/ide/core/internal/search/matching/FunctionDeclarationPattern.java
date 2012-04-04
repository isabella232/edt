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

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.TopLevelFunction;
import org.eclipse.edt.compiler.internal.core.utils.CharOperation;
import org.eclipse.edt.ide.core.internal.model.EGLElement;
import org.eclipse.edt.ide.core.internal.model.SourcePartElementInfo;
import org.eclipse.edt.ide.core.internal.model.index.IEntryResult;
import org.eclipse.edt.ide.core.internal.model.index.impl.IndexInput;
import org.eclipse.edt.ide.core.internal.model.index.impl.IndexedFile;
import org.eclipse.edt.ide.core.internal.model.indexing.AbstractIndexer;
import org.eclipse.edt.ide.core.internal.search.IIndexSearchRequestor;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IIndexConstants;
import org.eclipse.edt.ide.core.model.IMember;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;

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

public int matchesFunctionPart(TopLevelFunction function){
	return this.matchLevel(function);
}



/**
 * @see SearchPattern#matchLevel(AstNode, boolean)
 */
public int matchLevel(Node node, boolean resolve) {
	if (node instanceof TopLevelFunction){
		TopLevelFunction function = (TopLevelFunction)node;

		if (!this.matchesName(this.selector, function.getName().getCanonicalName().toCharArray()))
			return IMPOSSIBLE_MATCH;

	}else if (node instanceof NestedFunction){
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

protected IPartBinding getPartBinding(Part part){
	IBinding b = part.getName().resolveBinding();
	if (b != null && b != IBinding.NOT_FOUND_BINDING){
		return (IPartBinding)b;
	}else return null;

}

//public int matchLevelForType(char[] simpleNamePattern, char[] qualificationPattern,TopLevelFunction function) {
//	return  IMPOSSIBLE_MATCH;
//}
/**
 * @see SearchPattern#matchLevel(IEGLFunction)
 */
public int matchLevel(TopLevelFunction function) {
	if (function == null) return INACCURATE_MATCH;
	int level;
	
	level = this.matchLevelForType(this.selector, this.declaringQualification, function);
	if (level == IMPOSSIBLE_MATCH) return IMPOSSIBLE_MATCH;

//
//	// look at return type only if declaring type is not specified
//	if (this.declaringSimpleName == null) {
//		int newLevel = this.matchLevelForType(this.returnSimpleName, this.returnQualification, method.returnType);
//		switch (newLevel) {
//			case IMPOSSIBLE_MATCH:
//				return IMPOSSIBLE_MATCH;
//			case ACCURATE_MATCH: // keep previous level
//				break;
//			default: // ie. INACCURATE_MATCH
//				level = newLevel;
//				break;
//		}
//	}
//		
//	// parameter types
//	int parameterCount = this.parameterSimpleNames == null ? -1 : this.parameterSimpleNames.length;
//	if (parameterCount > -1) {
//		int argumentCount = method.parameters == null ? 0 : method.parameters.length;
//		if (parameterCount != argumentCount)
//			return IMPOSSIBLE_MATCH;
//		for (int i = 0; i < parameterCount; i++) {
//			char[] qualification = this.parameterQualifications[i];
//			char[] type = this.parameterSimpleNames[i];
//			int newLevel = this.matchLevelForType(type, qualification, method.parameters[i]);
//			switch (newLevel) {
//				case IMPOSSIBLE_MATCH:
//					return IMPOSSIBLE_MATCH;
//				case ACCURATE_MATCH: // keep previous level
//					break;
//				default: // ie. INACCURATE_MATCH
//					level = newLevel;
//					break;
//			}
//		}
//	}

	return level;
}

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

	public int matchesPart(IPart part){		
		try {
			SourcePartElementInfo partInfo = (SourcePartElementInfo)(((EGLElement)part).getElementInfo());
			if(partInfo.isFunction()){
				return this.matchTopLevelFunctionLevel(part);
			}
		} catch (EGLModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return IMPOSSIBLE_MATCH;
	}
}
