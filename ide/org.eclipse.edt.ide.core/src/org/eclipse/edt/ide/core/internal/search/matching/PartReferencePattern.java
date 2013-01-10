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

import java.io.IOException;

import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NameType;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.utils.CharOperation;
import org.eclipse.edt.ide.core.internal.model.EGLElement;
import org.eclipse.edt.ide.core.internal.model.IRPartType;
import org.eclipse.edt.ide.core.internal.model.SourcePartElementInfo;
import org.eclipse.edt.ide.core.internal.model.index.IEntryResult;
import org.eclipse.edt.ide.core.internal.model.index.impl.IndexInput;
import org.eclipse.edt.ide.core.internal.model.index.impl.IndexedFile;
import org.eclipse.edt.ide.core.internal.model.indexing.AbstractIndexer;
import org.eclipse.edt.ide.core.internal.search.IIndexSearchRequestor;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IFunction;
import org.eclipse.edt.ide.core.model.IIndexConstants;
import org.eclipse.edt.ide.core.model.IMember;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Delegate;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Enumeration;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.Interface;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Program;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.egl.StructuredRecord;

public class PartReferencePattern extends MultipleSearchPattern {

	private char[] qualification;
	private char[] simpleName;

	private char[] decodedSimpleName;

	private static char[][] TAGS = { PART_REF, REF };
	private static char[][] REF_TAGS = { REF };

	/* Optimization: case where simpleName == null */
	private char[][] segments;
	private int currentSegment;
	private char[] decodedSegment;
	
	//	set to "part"_SUFFIX for only matching a particular part type
	// set to PART_SUFFIX for matching all part types
	protected char partTypes; 
	
public PartReferencePattern(
	char[] qualification,
	char[] simpleName,
	char partTypes,
	int matchMode, 
	boolean isCaseSensitive) {

	super(matchMode, isCaseSensitive);

	this.qualification = isCaseSensitive ? qualification : CharOperation.toLowerCase(qualification);
	this.simpleName = isCaseSensitive ? simpleName : CharOperation.toLowerCase(simpleName);

	if (simpleName == null) {
		this.segments = this.qualification == null ? ONE_STAR_CHAR : CharOperation.splitOn('.', this.qualification);
	}
	this.partTypes = partTypes;
	
	this.needsResolve = true; // always resolve (in case of a simple name reference being a potential match)
}
/**
 * Either decode ref/name, typeRef/name or superRef/superName/name
 */ 
public void decodeIndexEntry(IEntryResult entryResult){

	char[] word = entryResult.getWord();
	int size = word.length;
	int tagLength = currentTag.length;
	int nameLength = CharOperation.indexOf(SEPARATOR, word, tagLength);
	if (nameLength < 0) nameLength = size;
	if (this.simpleName == null) {
		// Optimization, eg. type reference is 'com.ibm.etools.egl.internal.model.core.*'
		this.decodedSegment = CharOperation.subarray(word, tagLength, nameLength);
	} else {
		this.decodedSimpleName = CharOperation.subarray(word, tagLength, nameLength);
	}
}
public void feedIndexRequestor(IIndexSearchRequestor requestor, int detailLevel, int[] references, IndexInput input, IEGLSearchScope scope) throws IOException {
	if (currentTag == REF) {
		foundAmbiguousIndexMatches = true;
	}
	for (int i = 0, max = references.length; i < max; i++) {
		int reference = references[i];
		if (reference != -1) { // if the reference has not been eliminated
			IndexedFile file = input.getIndexedFile(reference);
			String path;
			if (file != null && scope.encloses(path = IndexedFile.convertPath(file.getPath()))) {
				requestor.acceptPartReference(path, decodedSimpleName);
			}
		}
	}
}
protected char[][] getPossibleTags(){
	if (this.simpleName == null) {
		return REF_TAGS;
	} else {
		return TAGS;
	}
}
/**
 * @see AndPattern#hasNextQuery
 */
protected boolean hasNextQuery() {
	if (this.simpleName == null) {
		// Optimization, eg. type reference is 'com.ibm.etools.egl.internal.model.core.*'
		if (this.segments.length > 2) {
			// if package has more than 2 segments, don't look at the first 2 since they are mostly
			// redundant (eg. in 'com.ibm.etools.egl.internal.model.core.*', 'org.eclipse is used all the time)
			return --this.currentSegment >= 2;
		} else {
			return --this.currentSegment >= 0;
		}
	} else {
		return false;
	}
}
/**
 * see SearchPattern.indexEntryPrefix()
 */
public char[] indexEntryPrefix(){

	if (this.simpleName == null) {
		// Optimization, eg. type reference is 'com.ibm.etools.egl.internal.model.core.*'
		return AbstractIndexer.bestReferencePrefix(
			REF,
			this.segments[this.currentSegment],
			matchMode, 
			isCaseSensitive);
	} else {
		return AbstractIndexer.bestReferencePrefix(
			currentTag,
			simpleName,
			matchMode, 
			isCaseSensitive);
	}
}
/**
 * @see SearchPattern#matchContainer()
 */
protected int matchContainer() {
	return EGL_FILE | PART | FUNCTION | FIELD | USE;
}
/**
 * @see SearchPattern#matchIndexEntry
 */
protected boolean matchIndexEntry() {

	/* check type name matches */
	if (simpleName == null) {
		// Optimization, eg. type reference is 'com.ibm.etools.egl.internal.model.core.*'
		switch(matchMode){
			case EXACT_MATCH :
				if (!CharOperation.equals(this.segments[this.currentSegment], this.decodedSegment, isCaseSensitive)){
					return false;
				}
				break;
			case PREFIX_MATCH :
				if (!CharOperation.prefixEquals(this.segments[this.currentSegment], this.decodedSegment, isCaseSensitive)){
					return false;
				}
				break;
			case PATTERN_MATCH :
				if (!CharOperation.match(this.segments[this.currentSegment], this.decodedSegment, isCaseSensitive)){
					return false;
				}
		}
	} else {
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
/**
 * @see AndPattern#resetQuery
 */
protected void resetQuery() {
	if (this.simpleName == null) {
		/* walk the segments from end to start as it will find less potential references using 'lang' than 'java' */
		this.currentSegment = this.segments.length - 1;
	}
}
public String toString(){
	StringBuffer buffer = new StringBuffer(20);
	buffer.append("TypeReferencePattern: pkg<"); //$NON-NLS-1$
	if (qualification != null) buffer.append(qualification);
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

/**
 * @see SearchPattern#matchLevel(Node, boolean)
 */
public int matchLevel(Node node, boolean resolve) {
	final int[] retVal = new int[] {IMPOSSIBLE_MATCH};
	
	if (node instanceof Expression){
		node.accept(new AbstractASTExpressionVisitor(){
			public boolean visitName(Name name){
				retVal[0] = matchLevelName(name);
				return false;
			}
		});
	}
	return retVal[0];
////	if (node instanceof ArrayAccess){
////		node = ((ArrayAccess)node).getArray();
////	}
//	if (node instanceof ArrayType){
//		node = ((ArrayType)node).getBaseType();
//	}
//	
////	if (node instanceof NameType) {
////		return this.matchLevel((NameType)node, resolve);
////	} else 
//	if (node instanceof Part){
//		return matchLevelName(((Part)node).getName());
//	}
//	if (node instanceof Name) {
//		return matchLevelName((Name)node);
//	} 
//	
//	if (node instanceof NestedFunction){
//		return matchLevelName(((NestedFunction)node).getName());
//	}
//
//	if (node instanceof NameType) {
//		return matchLevelName(((NameType)node).getName());
//	} 
////	else if (node instanceof Expression) {
////		return this.matchLevel((Expression)node, resolve);
////	}
//// EGLTODO: EGL Import statements cannot be resolved, so we will not include them in the results at this time
////	else if (node instanceof IEGLImportStatement) {
////		return this.matchLevel((IEGLImportStatement)node, resolve);
////	}
	
//	throw new UnsupportedOperationException();
//	return IMPOSSIBLE_MATCH;
}
	@Override
	public int matchLevel(IMember member, boolean resolve) {
		if (member instanceof IPart){
			IPart type = (IPart)member;
			// type name
			if (this.simpleName != null && !this.matchesName(this.simpleName, type.getElementName().toCharArray())){
				return IMPOSSIBLE_MATCH;
			}
		}
		return POSSIBLE_MATCH ;
	}
/**
 * Returns whether this type pattern matches the given type reference.
 * Look at resolved information only if specified.
 */
private int matchLevel(NameType  partRef, boolean resolve) {
	throw new UnsupportedOperationException();
//	if (!resolve) {
//		if (this.simpleName == null) {
//			return this.needsResolve ? POSSIBLE_MATCH : ACCURATE_MATCH;
//		} else {
//			Name name = partRef.getName();
//			if (name.isSimpleName()) {
//				if (this.matchesName(this.simpleName, name.getCanonicalName().toCharArray())) {
//					return this.needsResolve ? POSSIBLE_MATCH : ACCURATE_MATCH;
//				} else {
//					return IMPOSSIBLE_MATCH;
//				}
//			} else { // QualifiedTypeReference
//				char[][] tokens = CharOperation.splitOn('.', name.getCanonicalName().toCharArray());
//				for (int i = 0, max = tokens.length; i < max; i++){
//					if (this.matchesName(this.simpleName, tokens[i])) {
//						// can only be a possible match since resolution is needed 
//						// to find out if it is a type ref
//						return POSSIBLE_MATCH;
//					}
//				}
//				return IMPOSSIBLE_MATCH;
//			}				
//		} 
//	} else {
////		List referencedParts = partRef.resolve();
////		
////		if(referencedParts.size() != 1)
////		{
////			return INACCURATE_MATCH;
////		}
//		
//		Name name = partRef.getName();
//		ITypeBinding partTypeBinding = name.resolveTypeBinding();
//		IPartBinding partBinding = null;
//		if (partTypeBinding.isPartBinding()){
//			partBinding = (IPartBinding)partTypeBinding;
//		}
//		
//		// Make sure the partbinding matches the type we are looking for
//		if(matchesPartType(partBinding))
//		{
//			if (name.isSimpleName()){
//				return this.matchLevelForType(this.simpleName, this.qualification, (PartBinding)partBinding);
//			} else { // QualifiedTypeReference
//				char[][] tokens = CharOperation.splitOn('.', name.getCanonicalName().toCharArray());
//				int lastIndex = tokens.length-1;
//				// try to match all enclosing types for which the token matches as well.
//				while (partBinding != null && lastIndex >= 0){
//					if (matchesName(this.simpleName, tokens[lastIndex--])) {
//						int level = this.matchLevelForType(this.simpleName, this.qualification, (PartBinding)partBinding);
//						if (level != IMPOSSIBLE_MATCH) {
//							return level;
//						}
//					}
//					
//					// EGLTODO: Handle Nested definitions?
//	//				if (typeBinding instanceof ReferenceBinding){
//	//					typeBinding = ((ReferenceBinding)typeBinding).enclosingType();
//	//				} else {
//	//					typeBinding = null;
//	//				}
//				}
//				return IMPOSSIBLE_MATCH;
//			}			
//		}else {
//			return IMPOSSIBLE_MATCH;
//		}
//	}
}

/**
 * Returns whether this type pattern matches the given name reference.
 * Look at resolved information only if specified.
 */
private int matchLevel(Expression accessRef, boolean resolve) {
	throw new UnsupportedOperationException();
//	return IMPOSSIBLE_MATCH;
	//TODO search
	
//	// A data access can only be a reference to a table or library, filter out all other searches
//	if(partTypes == IIndexConstants.LIBRARY_SUFFIX ||
//			partTypes == IIndexConstants.TABLE_SUFFIX ||
//			partTypes == IIndexConstants.PART_SUFFIX)
//	{
//		if (!resolve) {
//			if (this.simpleName == null) {
//				return this.needsResolve ? POSSIBLE_MATCH : ACCURATE_MATCH;
//			} else {
//				// A data access should always be a quailfied name
//				if (!accessRef.isSimpleAccess()) {
//					char[][] tokens = CharOperation.splitOn('.', accessRef.getCanonicalString().toCharArray());
//					for (int i = 0, max = tokens.length; i < max; i++){
//						if (this.matchesName(this.simpleName, tokens[i])) {
//							// can only be a possible match since resolution is needed 
//							// to find out if it is a type ref
//							return POSSIBLE_MATCH;
//						}
//					}
//					return IMPOSSIBLE_MATCH;
//				} else {
//					return IMPOSSIBLE_MATCH;
//				}
//			}
//		} else {
//			
//			Node node = null;
//			
//			// First resolve as a data binding			
//			IEGLDataBinding[] dataBindings = accessRef.resolveAsDataBinding();
//			IEGLDataBinding dataBinding = null;
//			if(dataBindings.length > 1){
//			    return INACCURATE_MATCH;
//			}else if(dataBindings.length == 0){
//				// Found no data bindings, try to resolve as a function binding
//				IEGLFunctionBinding[] funcBindings = accessRef.resolveAsFunctionBinding();
//				if(funcBindings.length != 1){
//					return INACCURATE_MATCH;
//				}else{
//					IEGLFunctionBinding funcBinding = funcBindings[0];
//					IEGLFunction functionTSN = funcBinding.getFunctionTSN();
//					
//					// This is only a potential match if it is nested
//					if(functionTSN instanceof IEGLFunctionDeclaration){
//					    
//					    dataBinding = funcBinding.getQualifier();
//					    if(dataBinding != null){
//					        // This binding is not null if the function is contained within service, array, dictionary, etc.
//					        // i.e. Library.serviceVar.funcOne();
//					        node = getContainingLibrary(dataBinding);
//					    }else{
//					        // This function is qualified by a library
//					        node = (Node)((IEGLFunctionDeclaration)functionTSN).getFunctionContainer();
//					    }
//					}
//				}
//			}else{
//				// We resolved it as a data binding
//				node = getContainingTableOrLibrary(dataBindings[0]);
//			}
//			
//			if(node != null && node instanceof Part){
//				Part partBinding = (Part) node;
//				if (accessRef.isSimpleAccess()){
//					return this.matchLevelForType(this.simpleName, this.qualification, partBinding);
//				} else { // QualifiedNameReference
//					
//					char[][] tokens =  CharOperation.splitOn('.', accessRef.getCanonicalString().toCharArray());
//					int lastIndex = tokens.length-1;
////					 try to match all enclosing types for which the token matches as well.
//					while (partBinding != null && lastIndex >= 0){
//						if (this.matchesName(this.simpleName, tokens[lastIndex--])) {
//							int level = this.matchLevelForType(this.simpleName, this.qualification, partBinding);
//							if (level != IMPOSSIBLE_MATCH) {
//								return level;
//							}
//						}
//						
////						 EGLTODO: Handle Nested definitions?
////						if (partBinding instanceof ReferenceBinding){
////							partBinding = ((ReferenceBinding)partBinding).enclosingType();
////						} else {
////							partBinding = null;
////						}
//					}
//					return IMPOSSIBLE_MATCH;
//				}
//			}else {
//				return IMPOSSIBLE_MATCH;
//			}
//		}
//	}else{
//		return IMPOSSIBLE_MATCH;
//	}
}

/**
 * Returns whether this type pattern matches the given name reference.
 * Look at resolved information only if specified.
 */
private int matchLevelName(Name nameRef) {
	if (this.simpleName == null) {
		return this.needsResolve ? POSSIBLE_MATCH : ACCURATE_MATCH;
	} else {
		if (nameRef.isSimpleName()) {
			if (this.matchesName(this.simpleName, nameRef.getCanonicalName().toCharArray())) {
				// can only be a possible match since resolution is needed 
				// to find out if it is a type ref
				return POSSIBLE_MATCH;
			} else {
				return IMPOSSIBLE_MATCH;
			}
		} else { // QualifiedNameReference
			char[][] tokens = CharOperation.splitOn('.', nameRef.getCanonicalName().toCharArray());
			for (int i = 0, max = tokens.length; i < max; i++){
				if (this.matchesName(this.simpleName, tokens[i])) {
					// can only be a possible match since resolution is needed 
					// to find out if it is a type ref
					return POSSIBLE_MATCH;
				}
			}
			return IMPOSSIBLE_MATCH;
		}				
	}
	 

//}else {
//		
//		IBinding binding = nameRef.resolveBinding();
//		
//		if (binding == null || binding == IBinding.NOT_FOUND_BINDING){
//			if (nameRef.isQualifiedName()){
//				Name tempname = nameRef;
//				while (tempname.isQualifiedName()){
//					tempname = ((QualifiedName)tempname).getQualifier();
//					binding = tempname.resolveBinding();
//					if (binding != null && binding != IBinding.NOT_FOUND_BINDING){
//						break;
//					}
//				}
//				
//			}
//		}
//		
//		if (binding == null || binding == IBinding.NOT_FOUND_BINDING){
//			return INACCURATE_MATCH;//X
//		}
//		
//		IPartBinding partBinding = null;
//		if (binding.isTypeBinding() && ((ITypeBinding)binding).isPartBinding()){
//			partBinding = (PartBinding)binding;
//		}
//		else if (binding.isDataBinding()){
//			ITypeBinding typeBinding = ((DataBinding)binding).getType();
//			if (typeBinding.getKind() == ITypeBinding.ARRAY_TYPE_BINDING){
//				typeBinding = typeBinding.getBaseType();
//			}
//			if (typeBinding == null || typeBinding == IBinding.NOT_FOUND_BINDING || !typeBinding.isPartBinding()){
//				if (nameRef.isQualifiedName()){
//					if (((DataBinding)binding).getKind() == IDataBinding.STRUCTURE_ITEM_BINDING){
//						typeBinding = ((StructureItemBinding)binding).getEnclosingStructureBinding();
//					}else if (((DataBinding)binding).getKind() == IDataBinding.CLASS_FIELD_BINDING ){
//						typeBinding = ((ClassFieldBinding)binding).getDeclaringPart();
//					}
//				}
//			}
//						
//			if (typeBinding == null || typeBinding == IBinding.NOT_FOUND_BINDING){
//				return INACCURATE_MATCH;//X
//			}
//			
//			if (typeBinding.getKind() == ITypeBinding.FUNCTION_BINDING && !typeBinding.isPartBinding() && !(nameRef.getParent() instanceof NestedFunction)){
//				typeBinding = ((FunctionBinding)typeBinding).getDeclarer();
//			}
//			
//
//			
//			if (typeBinding.isPartBinding() ){
//				partBinding = (IPartBinding)typeBinding;
//			}else if (typeBinding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING && partTypes == IIndexConstants.ITEM_SUFFIX){
//				return ACCURATE_MATCH;
//			}else {
//				return IMPOSSIBLE_MATCH;
//			}
//			
//		}
//		
//		if (partBinding == null){
//			return INACCURATE_MATCH;//x
//		}
//		
//		if(matchesPartType(partBinding))
//		{
//			if (nameRef.isSimpleName()){
//				return this.matchLevelForType(this.simpleName, this.qualification, partBinding);
//			} else { // QualifiedNameReference
//				char[][] tokens =  CharOperation.splitOn('.', nameRef.getCanonicalName().toCharArray());
//				int lastIndex = tokens.length-1;
////				 try to match all enclosing types for which the token matches as well.
//				while (partBinding != null && lastIndex >= 0){
//					if (this.matchesName(this.simpleName, tokens[lastIndex--])) {
//						int level = this.matchLevelForType(this.simpleName, this.qualification, partBinding);
//						if (level != IMPOSSIBLE_MATCH) {
//							return level;
//						}
//					}
//
////TODO search					
////					if(partBinding instanceof NestedForm){
////						partBinding = (Part)partBinding.getContainer();
////					} else {
////						partBinding = null;
////					}
////					 EGLTODO: Handle Nested definitions?
////					if (partBinding instanceof ReferenceBinding){
////						partBinding = ((ReferenceBinding)partBinding).enclosingType();
////					} else {
////						partBinding = null;
////					}
//				}
//				return IMPOSSIBLE_MATCH;
//			}
//		} else 
////			if (nameRef.isQualifiedName()){
////			return matchLevel(((QualifiedName)nameRef).getQualifier(),true);
////		}else 
//			return IMPOSSIBLE_MATCH;
//		
//	}
}

	public int matchesPartType(Name nameRef,Part partBinding,boolean forceQualification){
//		IBinding binding = nameRef.resolveBinding();
//		
//		if (binding == null || binding == IBinding.NOT_FOUND_BINDING){
//			if (nameRef.isQualifiedName()){
//				Name tempname = nameRef;
//				while (tempname.isQualifiedName()){
//					tempname = ((QualifiedName)tempname).getQualifier();
//					binding = tempname.resolveBinding();
//					if (binding != null && binding != IBinding.NOT_FOUND_BINDING){
//						break;
//					}
//				}
//				
//			}
//		}
//		
//		if (binding == null || binding == IBinding.NOT_FOUND_BINDING){
//			return INACCURATE_MATCH;//X
//		}
//		
//		if (binding.isTypeBinding() && ((ITypeBinding)binding).getKind() == ITypeBinding.FORM_BINDING){
//			if (nameRef.getParent() instanceof NestedForm){
//				return  IMPOSSIBLE_MATCH;
//			}
//			
//		}
//		
//		IPartBinding partBinding = null;
//		if (binding.isTypeBinding() && ((ITypeBinding)binding).isPartBinding()){
//			partBinding = (PartBinding)binding;
//		}
//		else if (binding.isDataBinding()){
//			ITypeBinding typeBinding = ((DataBinding)binding).getType();
//			if (typeBinding.getKind() == ITypeBinding.ARRAY_TYPE_BINDING){
//				typeBinding = typeBinding.getBaseType();
//			}
//			if (typeBinding == null || typeBinding == IBinding.NOT_FOUND_BINDING || !typeBinding.isPartBinding()){
//				if (nameRef.isQualifiedName()){
//					if (((DataBinding)binding).getKind() == IDataBinding.STRUCTURE_ITEM_BINDING){
//						typeBinding = ((StructureItemBinding)binding).getEnclosingStructureBinding();
//					}else if (((DataBinding)binding).getKind() == IDataBinding.CLASS_FIELD_BINDING ){
//						typeBinding = ((ClassFieldBinding)binding).getDeclaringPart();
//					}
//				}
//			}
//						
//			if (typeBinding == null || typeBinding == IBinding.NOT_FOUND_BINDING){
//				return INACCURATE_MATCH;//X
//			}
//			
//			if (typeBinding.getKind() == ITypeBinding.FUNCTION_BINDING && !typeBinding.isPartBinding() && !(nameRef.getParent() instanceof NestedFunction)){
//				typeBinding = ((FunctionBinding)typeBinding).getDeclarer();
//			}
//			
//
//			
//			if (typeBinding.isPartBinding() ){
//				partBinding = (IPartBinding)typeBinding;
//			}else if (typeBinding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING && partTypes == IIndexConstants.ITEM_SUFFIX){
//				return ACCURATE_MATCH;
//			}else {
//				return IMPOSSIBLE_MATCH;
//			}
//			
//		}
//		
		if (partBinding == null){
			return INACCURATE_MATCH;//x
		}
		
		if(matchesPartType(partBinding))
		{
			if (forceQualification && qualification == null && nameRef.isQualifiedName()){
				return IMPOSSIBLE_MATCH;
			}
			
			if (nameRef.isSimpleName()){
				return this.matchLevelForType(this.simpleName, this.qualification, partBinding);
			} else { // QualifiedNameReference
				char[][] tokens =  CharOperation.splitOn('.', nameRef.getCanonicalName().toCharArray());
				int lastIndex = tokens.length-1;
//				 try to match all enclosing types for which the token matches as well.
				while (lastIndex >= 0){
					if (this.matchesName(this.simpleName, tokens[lastIndex--])) {
						int level = this.matchLevelForType(this.simpleName, this.qualification, partBinding);
						if (level != IMPOSSIBLE_MATCH) {
							return level;
						}
					}
				

//TODO search					
//					if(partBinding instanceof NestedForm){
//						partBinding = (Part)partBinding.getContainer();
//					} else {
//						partBinding = null;
//					}
//					 EGLTODO: Handle Nested definitions?
//					if (partBinding instanceof ReferenceBinding){
//						partBinding = ((ReferenceBinding)partBinding).enclosingType();
//					} else {
//						partBinding = null;
//					}
				}
				return IMPOSSIBLE_MATCH;
			}
		} 
//		else 
//			if (nameRef.isQualifiedName()){
//			return matchLevel(((QualifiedName)nameRef).getQualifier(),true);
//		}else 
			return IMPOSSIBLE_MATCH;
		

	}
	
	
/**
 * Check to make sure that the part type we have resolved to is of the type we are looking for.
 */
public boolean matchesPartType(Part partBinding){
	boolean match = false;
	
	switch(partTypes)
	{
		case IIndexConstants.PROGRAM_SUFFIX:
			match = partBinding instanceof Program;
			break;
		case IIndexConstants.RECORD_SUFFIX:
			match = partBinding instanceof Record || partBinding instanceof StructuredRecord;
			break;
		case IIndexConstants.LIBRARY_SUFFIX:
			match = partBinding instanceof Library;
			break;
		case IIndexConstants.FUNCTION_SUFFIX:
			match = partBinding instanceof FunctionMember;
			break;
		case IIndexConstants.HANDLER_SUFFIX:
			match = partBinding instanceof Handler;
			break;
		case IIndexConstants.DELEGATE_SUFFIX:
			match = partBinding instanceof Delegate;
			break;
		case IIndexConstants.EXTERNALTYPE_SUFFIX:
			match = partBinding instanceof ExternalType;
			break;
		case IIndexConstants.ENUMERATION_SUFFIX:
			match = partBinding instanceof Enumeration;
			break;
		case IIndexConstants.SERVICE_SUFFIX:
			match = partBinding instanceof Service;
			break;
		case IIndexConstants.INTERFACE_SUFFIX:
			match = partBinding instanceof Interface;
			break;
		case IIndexConstants.STEREOTYPE_SUFFIX:
			match = partBinding instanceof StereotypeType;
			break;
		case IIndexConstants.ANNOTATION_SUFFIX:
			match = partBinding instanceof AnnotationType;
			break;
		case IIndexConstants.CLASS_SUFFIX:
			match = partBinding instanceof EGLClass;
			break;
		case IIndexConstants.PART_SUFFIX:
			match = true;
			break;
	}
	
	return match;
}

	/**
	 * Check to make sure that the part type we have resolved to is of the type we are looking for.
	 */
	private boolean matchesPartType(IPart part){
		boolean match = false;
		try {
			SourcePartElementInfo partInfo = (SourcePartElementInfo)(((EGLElement)part).getElementInfo());
			
			switch(partTypes)
			{
				case IIndexConstants.PROGRAM_SUFFIX:
					match = partInfo.isProgram();
					break;
				case IIndexConstants.RECORD_SUFFIX:
					match = partInfo.isRecord() && !isAnnotation(part);
					break;
				case IIndexConstants.ANNOTATION_SUFFIX:
					match = partInfo.isRecord() && isAnnotation(part);
					break;
				case IIndexConstants.STEREOTYPE_SUFFIX:
					match = partInfo.isRecord() && isStereotype(part);
					break;
				case IIndexConstants.LIBRARY_SUFFIX:
					match = partInfo.isLibrary();
					break;
				case IIndexConstants.FUNCTION_SUFFIX:
					match = partInfo.isFunction();
					break;
				case IIndexConstants.HANDLER_SUFFIX:
					match = partInfo.isHandler();
					break;
				case IIndexConstants.SERVICE_SUFFIX:
					match = partInfo.isService();
					break;
				case IIndexConstants.INTERFACE_SUFFIX:
					match = partInfo.isInterface();
					break;
				case IIndexConstants.DELEGATE_SUFFIX:
					match = partInfo.isDelegate();
					break;
				case IIndexConstants.EXTERNALTYPE_SUFFIX:
					match = partInfo.isExternalType();
					break;
				case IIndexConstants.ENUMERATION_SUFFIX:
					match = partInfo.isEnumeration();
					break;
				case IIndexConstants.CLASS_SUFFIX:
					match = partInfo.isClass();
					break;
				case IIndexConstants.PART_SUFFIX:
					match = true;
					break;
			}
		} catch (EGLModelException e) {
			e.printStackTrace();
		}
		return match;
	}
	
	@Override
	public int matchesFunctionPartType(IFunction function){
		try {
			if(function.getParent() instanceof IPart &&
				((SourcePartElementInfo)((EGLElement)function.getParent()).getElementInfo()).isDelegate()){
				return INACCURATE_MATCH;
			}
		} catch (EGLModelException e) {
			e.printStackTrace();
		}
		return IMPOSSIBLE_MATCH;
	}

	@Override
	public int matchesPart(IPart part) {
		if(!matchesPartType(part)){
			return IMPOSSIBLE_MATCH;
		}
		
		return this.matchLevelForType(this.simpleName, this.qualification, part);
	}
	@Override
	public int getPatternType() {
		return SearchPattern.REFERENCE;
	}
	
	private boolean isAnnotation(IPart part) {
		try {
			SourcePartElementInfo partInfo = (SourcePartElementInfo)(((EGLElement)part).getElementInfo());

			return IRPartType.ANNOTATION.equals(new String(partInfo.getSubTypeName())) || isStereotype(part);
		} catch (EGLModelException e) {
		}
		return false;
	}
	private boolean isStereotype(IPart part) {
		try {
			SourcePartElementInfo partInfo = (SourcePartElementInfo)(((EGLElement)part).getElementInfo());

			return IRPartType.STEREOTYPETYPE.equals(new String(partInfo.getSubTypeName()));
		} catch (EGLModelException e) {
		}
		return false;
	}
}
