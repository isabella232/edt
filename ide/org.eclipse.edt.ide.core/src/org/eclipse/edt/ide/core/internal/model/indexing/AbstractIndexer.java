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
package org.eclipse.edt.ide.core.internal.model.indexing;

import java.io.IOException;

import org.eclipse.edt.ide.core.internal.model.index.IDocument;
import org.eclipse.edt.ide.core.internal.model.index.IIndexer;
import org.eclipse.edt.ide.core.internal.model.index.IIndexerOutput;
import org.eclipse.edt.ide.core.model.IIndexConstants;

import org.eclipse.edt.compiler.internal.core.utils.CharOperation;

public abstract class AbstractIndexer implements IIndexer, IIndexConstants {
	IIndexerOutput output;

public AbstractIndexer() {
	super();
}
public void addPartDeclaration(char partType, int modifiers, char[] packageName,char[] name,  char[][] enclosingPartNames, char[][] interfaceNames){

	this.output.addRef(encodePartEntry(partType, packageName, enclosingPartNames, name, true));
	
	if (interfaceNames != null) {
		for (int i = 0, max = interfaceNames.length; i < max; i++) {
			addPartReference(interfaceNames[i]);
		}
	}
	
}
public void addFieldDeclaration(char[] typeName, char[] fieldName){
	this.output.addRef(CharOperation.concat(FIELD_DECL, fieldName));
	this.addPartReference(typeName);
}
public void addFieldReference(char[] fieldName){
	this.output.addRef(CharOperation.concat(FIELD_REF, fieldName));	
}
public void addFunctionDeclaration(char[] functionName, char[][] parameterParts, char[] returnPart){
	// Calculate the number of arguments of the function
	int numberOfArguments = 0;
	if (parameterParts != null){
		numberOfArguments = parameterParts.length;
		for (int i = 0; i < numberOfArguments; i++){
			this.addPartReference(parameterParts[i]);
		}
	}
	//convert the number of arguments into a char array
	char[] countChars;
	if (numberOfArguments < 10) {
		countChars = COUNTS[numberOfArguments];
	} else {
		countChars = String.valueOf(numberOfArguments).toCharArray();
	}
	//add the reference
	this.output.addRef(concat(FUNCTION_DECL, functionName, countChars, SEPARATOR));

	if (returnPart != null) this.addPartReference(returnPart);
}
public void addFunctionReference(char[] functionName, int argCount){
	char[] countChars;
	if (argCount < 10) {
		countChars = COUNTS[argCount];
	} else {
		countChars = String.valueOf(argCount).toCharArray();
	}
	this.output.addRef(concat(FUNCTION_REF, functionName, countChars, SEPARATOR));
	
}
public void addNameReference(char[] name){
	this.output.addRef(CharOperation.concat(REF, name));
}
public void addPartReference(char[] typeName){

	this.output.addRef(CharOperation.concat(PART_REF, CharOperation.lastSegment(typeName, '.')));
}
/**
 * Method declaration entries are encoded as follow: 'fieldDecl/' Name
 * 	e.g.&nbsp;fieldDecl/x
 *
 */
 public static final char[] bestFieldDeclarationPrefix(char[] name, int matchMode, boolean isCaseSensitive) {

	if (!isCaseSensitive || name == null) return FIELD_DECL;
	switch(matchMode){
		case EXACT_MATCH :
		case PREFIX_MATCH :
			return CharOperation.concat(FIELD_DECL, name);
		case PATTERN_MATCH :
			int starPos = CharOperation.indexOf('*', name);
			switch(starPos) {
				case -1 :
					return CharOperation.concat(FIELD_DECL, name);
				default : 
					int refLength = FIELD_DECL.length;
					char[] result = new char[refLength+starPos];
					System.arraycopy(FIELD_DECL, 0, result, 0, refLength);
					System.arraycopy(name, 0, result, refLength, starPos);
					return result;
				case 0 : // fall through
			}
		default:
			return FIELD_DECL;
	}
}
/**
 * Method declaration entries are encoded as follow: 'functionDecl/' Selector '/' Arity
 * 	e.g.&nbsp;functionDecl/clone/0&nbsp;functionDecl/append/1
 *
 */
 public static final char[] bestFunctionDeclarationPrefix(char[] selector, int arity, int matchMode, boolean isCaseSensitive) {

	if (!isCaseSensitive || selector == null) return FUNCTION_DECL;
	switch(matchMode){
		case EXACT_MATCH :
			if (arity >= 0){
				char[] countChars;
				if (arity < 10) {
					countChars = COUNTS[arity];
				} else {
					countChars = String.valueOf(arity).toCharArray();
				}
				return concat(FUNCTION_DECL, selector, countChars, SEPARATOR);
			}
		case PREFIX_MATCH :
			return CharOperation.concat(FUNCTION_DECL, selector);
		case PATTERN_MATCH :
			int starPos = CharOperation.indexOf('*', selector);
			switch(starPos) {
				case -1 :
					return CharOperation.concat(FUNCTION_DECL, selector);
				default : 
					int refLength = FUNCTION_DECL.length;
					char[] result = new char[refLength+starPos];
					System.arraycopy(FUNCTION_DECL, 0, result, 0, refLength);
					System.arraycopy(selector, 0, result, refLength, starPos);
					return result;
				case 0 : // fall through
			}
		default:
			return FUNCTION_DECL;
	}
}
/**
 * Method reference entries are encoded as follow: 'functionRef/' Selector '/' Arity
 * 	e.g.&nbsp;functionRef/clone/0&nbsp;functionRef/append/1
 *
 */
 public static final char[] bestMethodReferencePrefix(char[] selector, int arity, int matchMode, boolean isCaseSensitive) {

	if (!isCaseSensitive || selector == null) return FUNCTION_REF;
	switch(matchMode){
		case EXACT_MATCH :
			if (arity >= 0){
				char[] countChars;
				if (arity < 10) {
					countChars = COUNTS[arity];
				} else {
					countChars = String.valueOf(arity).toCharArray();
				}
				return concat(FUNCTION_REF, selector, countChars, SEPARATOR);
			}
		case PREFIX_MATCH :
			return CharOperation.concat(FUNCTION_REF, selector);
		case PATTERN_MATCH :
			int starPos = CharOperation.indexOf('*', selector);
			switch(starPos) {
				case -1 :
					return CharOperation.concat(FUNCTION_REF, selector);
				default : 
					int refLength = FUNCTION_REF.length;
					char[] result = new char[refLength+starPos];
					System.arraycopy(FUNCTION_REF, 0, result, 0, refLength);
					System.arraycopy(selector, 0, result, refLength, starPos);
					return result;
				case 0 : // fall through
			}
		default:
			return FUNCTION_REF;
	}
}
/**
 * Part entries are encoded as follow: '<tag>/' Name 
 * 	e.g.&nbsp;ref/Object&nbsp;ref/x
 */
 public static final char[] bestReferencePrefix(char[] tag, char[] name, int matchMode, boolean isCaseSensitive) {

	if (!isCaseSensitive || name == null) return tag;
	switch(matchMode){
		case EXACT_MATCH :
		case PREFIX_MATCH :
			return CharOperation.concat(tag, name);
		case PATTERN_MATCH :
			int starPos = CharOperation.indexOf('*', name);
			switch(starPos) {
				case -1 :
					return CharOperation.concat(tag, name);
				default : 
					int refLength = tag.length;
					char[] result = new char[refLength+starPos];
					System.arraycopy(tag, 0, result, 0, refLength);
					System.arraycopy(name, 0, result, refLength, starPos);
					return result;
				case 0 : // fall through
			}
		default:
			return tag;
	}
}
/**
 * Part entries are encoded as follow: 'typeDecl/' ('C' | 'I') '/' PackageName '/' PartName:
 * 	e.g.&nbsp;typeDecl/C/java.lang/Object&nbsp;typeDecl/I/java.lang/Cloneable
 *
 * Current encoding is optimized for queries: all classes/interfaces
 */
 public static final char[] bestPartDeclarationPrefix(char[] packageName, char[] typeName, char partType, int matchMode, boolean isCaseSensitive) {
	// index is case sensitive, thus in case attempting case insensitive search, cannot consider
	// type name.
	if (!isCaseSensitive){
		packageName = null;
		typeName = null;
	}
	if (partType != 0) 
		return PART_DECL;
		
	switch(matchMode){
		case EXACT_MATCH :
		case PREFIX_MATCH :
			break;
		case PATTERN_MATCH :
			if (typeName != null){
				int starPos = CharOperation.indexOf('*', typeName);
				switch(starPos) {
					case -1 :
						break;
					case 0 :
						typeName = null;
						break;
					default : 
						typeName = CharOperation.subarray(typeName, 0, starPos);
				}
			}
	}
	int packageLength = packageName.length;
	int typeLength = typeName == null ? 0 : typeName.length;
	int pos;
	char[] result = new char[PART_DECL_LENGTH + packageLength + typeLength + 3];
	System.arraycopy(PART_DECL, 0, result, 0, pos = PART_DECL_LENGTH);
	result[pos++] = partType;
	result[pos++] = SEPARATOR;
	System.arraycopy(packageName, 0, result, pos, packageLength);
	pos += packageLength;
	result[pos++] = SEPARATOR;
	if (typeLength > 0){
		System.arraycopy(typeName, 0, result, pos, typeName.length);
	}
	return result;
}
/**
 * Concat(first, second, third, fourth, fifth, sep) --> [first][second][sep][third][sep][fourth][sep][fifth]
 * in other words, no separator is inserted in between first and second
 */
protected static final char[] concat(char[] firstWithSeparator, char[] second, char[] third, char[] fourth, char[] fifth, char separator) {
	int length1= firstWithSeparator.length;
	int length2= second == null ? 0 : second.length;
	int length3= third == null ? 0 : third.length;
	int length4= fourth == null ? 0 : fourth.length;
	int length5= fifth == null ? 0 : fifth.length;
	char[] result= new char[length1 + length2 + length3 + length4 + length5 + 3 ];
	System.arraycopy(firstWithSeparator, 0, result, 0, length1);
	if (second != null) System.arraycopy(second, 0, result, length1 , length2);
	int pos = length1 + length2;
	result[pos]= separator;
	if (third != null) System.arraycopy(third, 0, result, pos + 1, length3);
	pos += length3+1;
	result[pos]= separator;
	if (fourth != null) System.arraycopy(fourth, 0, result, pos + 1, length4);
	pos += length4+1;
	result[pos]= separator;
	if (fifth != null) System.arraycopy(fifth, 0, result, pos + 1, length5);
	return result;
}
/**
 * Concat(first, second, third, sep) --> [first][second][sep][third]
 * in other words, no separator is inserted in between first and second
 */
protected static final char[] concat(char[] firstWithSeparator, char[] second, char[] third, char separator) {
	int length1= firstWithSeparator.length;
	int length2= second == null ? 0 : second.length;
	int length3= third == null ? 0 : third.length;
	char[] result= new char[length1 + length2 + length3 + 1];
	System.arraycopy(firstWithSeparator, 0, result, 0, length1);
	if (second != null) System.arraycopy(second, 0, result, length1 , length2);
	result[length1 + length2]= separator;
	if (third != null) System.arraycopy(third, 0, result, length1 + length2 + 1, length3);
	return result;
}
/**
 * Concat(first, second, third, charAfterThird, fourth, fifth, sixth, charAfterSixth, last, sep) --> [first][second][sep][third][sep][charAfterThird][sep][fourth][sep][fifth][sep][sixth][sep][charAfterSixth][last]
 * in other words, no separator is inserted in between first and second
 */
protected static final char[] concat(char[] firstWithSeparator, char[] second, char[] third, char charAfterThird, char[] fourth, char[] fifth, char[] sixth, char charAfterSixth, char last, char separator) {
	int length1= firstWithSeparator.length;
	int length2= second == null ? 0 : second.length;
	int length3= third == null ? 0 : third.length;
	int length4= fourth == null ? 0 : fourth.length;
	int length5= fifth == null ? 0 : fifth.length;
	int length6 = sixth == null ? 0 : sixth.length;
	char[] result= new char[length1 + length2 + length3 + length4 + length5 + length6 + 9 ];
	System.arraycopy(firstWithSeparator, 0, result, 0, length1);
	if (second != null) System.arraycopy(second, 0, result, length1 , length2);
	int pos = length1 + length2;
	result[pos]= separator;
	if (third != null) System.arraycopy(third, 0, result, pos + 1, length3);
	pos += length3+1;
	result[pos]= separator;
	result[++pos] = charAfterThird;
	result[++pos] = separator;
	if (fourth != null) System.arraycopy(fourth, 0, result, pos + 1, length4);
	pos += length4+1;
	result[pos]= separator;
	if (fifth != null) System.arraycopy(fifth, 0, result, pos + 1, length5);
	pos += length5+1;
	result[pos]= separator;
	if (sixth != null) System.arraycopy(sixth, 0, result, pos + 1, length6);
	pos += length6+1;
	result[pos]= separator;
	result[++pos] = charAfterSixth;
	result[++pos]=last;
	return result;
}
/**
 * Part entries are encoded as follow: 'partDecl/' (Part type suffix) '/' PackageName '/' PartName '/' EnclosingPartName
 * 	e.g.<ul>
 * 	<li>typeDecl/C/java.lang/Object/</li>
 *	<li>typeDecl/I/java.lang/Cloneable/</li>
 *	<li>typeDecl/C/javax.swing/LazyValue/UIDefaults</li>
 * Current encoding is optimized for queries: all classes/interfaces
 */
 protected static final char[] encodePartEntry(char partType, char[] packageName, char[][] enclosingPartNames, char[] typeName, boolean isClass) {
	int packageLength = packageName == null ? 0 : packageName.length;
	int enclosingPartNamesLength = 0;
	if (enclosingPartNames != null) {
		for (int i = 0, length = enclosingPartNames.length; i < length; i++){
			enclosingPartNamesLength += enclosingPartNames[i].length + 1;
		}
	}
	int pos;
	char[] result = new char[PART_DECL_LENGTH + packageLength + typeName.length + enclosingPartNamesLength + 4];
	System.arraycopy(PART_DECL, 0, result, 0, pos = PART_DECL_LENGTH);
	result[pos++] = partType;
	result[pos++] = SEPARATOR;
	if (packageName != null){
		System.arraycopy(packageName, 0, result, pos, packageLength);
		pos += packageLength;
	}
	result[pos++] = SEPARATOR;
	System.arraycopy(typeName, 0, result, pos, typeName.length);
	pos += typeName.length;
	result[pos++] = SEPARATOR;
	if (enclosingPartNames != null){
		for (int i = 0, length = enclosingPartNames.length; i < length; i++){
			int enclosingPartNameLength = enclosingPartNames[i].length;
			System.arraycopy(enclosingPartNames[i], 0, result, pos, enclosingPartNameLength);
			pos += enclosingPartNameLength;
			result[pos++] = SEPARATOR;
		}
	}
	return result;
}
/**
 * Returns the file types the <code>IIndexer</code> handles.
 */

public abstract String[] getFileTypes();
/**
 * @see IIndexer#index(IDocument document, IIndexerOutput output)
 */
public void index(IDocument document, IIndexerOutput output) throws IOException {
	this.output = output;
	if (shouldIndex(document)) indexFile(document);
}
protected abstract void indexFile(IDocument document) throws IOException;
/**
 * @see IIndexer#shouldIndex(IDocument document)
 */
public boolean shouldIndex(IDocument document) {
	String type = document.getType();
	String[] supportedParts = this.getFileTypes();
	for (int i = 0; i < supportedParts.length; ++i) {
		if (supportedParts[i].equalsIgnoreCase(type))
			return true;
	}
	return false;
}
}
