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
import java.io.StringReader;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.edt.compiler.core.ast.ImportDeclaration;
import org.eclipse.edt.compiler.core.ast.Lexer;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.NodeTypes;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.internal.core.utils.CharOperation;
import org.eclipse.edt.ide.core.internal.model.index.IEntryResult;
import org.eclipse.edt.ide.core.internal.model.index.IIndex;
import org.eclipse.edt.ide.core.internal.model.index.impl.BlocksIndexInput;
import org.eclipse.edt.ide.core.internal.model.index.impl.IndexInput;
import org.eclipse.edt.ide.core.internal.search.IIndexSearchRequestor;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IFunction;
import org.eclipse.edt.ide.core.model.IIndexConstants;
import org.eclipse.edt.ide.core.model.IMember;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.core.search.ISearchPattern;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.FunctionPart;
import org.eclipse.edt.mof.egl.Type;

public abstract class SearchPattern implements ISearchPattern, IIndexConstants, IEGLSearchConstants {

	protected int matchMode;
	protected boolean isCaseSensitive;
	public boolean needsResolve = true;

	/* focus element (used for reference patterns*/
	public IEGLElement focus;

	/* match level */
	public static final int IMPOSSIBLE_MATCH = 0;
	public static final int POSSIBLE_MATCH = 1;
	public static final int ACCURATE_MATCH = 2;
	public static final int INACCURATE_MATCH = 3;

	/* match container */
	public static final int EGL_FILE = 1;
	public static final int PART = 2;
	public static final int FIELD = 4;
	public static final int FUNCTION = 8;
	public static final int USE = 16;
		
public SearchPattern(int matchMode, boolean isCaseSensitive) {
	this.matchMode = matchMode;
	this.isCaseSensitive = isCaseSensitive;
}
public static SearchPattern createPattern(String patternString, int searchFor, int limitTo, int matchMode, boolean isCaseSensitive) {

	if (patternString == null || patternString.length() == 0)
		return null;

	SearchPattern searchPattern = null;
	switch (searchFor) {

		case IEGLSearchConstants.ALL_ELEMENTS:
			// Search for all declerations, references, or both for all top level parts and nested functions
			searchPattern = new OrPattern(createPartPattern(patternString, PART_SUFFIX, limitTo, matchMode, isCaseSensitive),
					createAllFunctionPattern(patternString, limitTo, matchMode, isCaseSensitive));
			break;
		case IEGLSearchConstants.PROGRAM_PART:
			searchPattern = createPartPattern(patternString, PROGRAM_SUFFIX, limitTo, matchMode, isCaseSensitive);
			break;
		case IEGLSearchConstants.RECORD_PART:
			searchPattern = createPartPattern(patternString, RECORD_SUFFIX, limitTo, matchMode, isCaseSensitive);
			break;
		case IEGLSearchConstants.LIBRARY_PART:
			searchPattern = createPartPattern(patternString, LIBRARY_SUFFIX, limitTo, matchMode, isCaseSensitive);
			break;
		case IEGLSearchConstants.HANDLER_PART:
			searchPattern = createPartPattern(patternString, HANDLER_SUFFIX, limitTo, matchMode, isCaseSensitive);
			break;
		case IEGLSearchConstants.DELEGATE_PART:
			searchPattern = createPartPattern(patternString, DELEGATE_SUFFIX, limitTo, matchMode, isCaseSensitive);
			break;
		case IEGLSearchConstants.EXTERNALTYPE_PART:
			searchPattern = createPartPattern(patternString, EXTERNALTYPE_SUFFIX, limitTo, matchMode, isCaseSensitive);
			break;
		case IEGLSearchConstants.ENUMERATION_PART:
			searchPattern = createPartPattern(patternString, ENUMERATION_SUFFIX, limitTo, matchMode, isCaseSensitive);
			break;
			
		case IEGLSearchConstants.SERVICE_PART:
			searchPattern = createPartPattern(patternString, SERVICE_SUFFIX, limitTo, matchMode, isCaseSensitive);
			break;
		case IEGLSearchConstants.INTERFACE_PART:
			searchPattern = createPartPattern(patternString, INTERFACE_SUFFIX, limitTo, matchMode, isCaseSensitive);
			break;
		case IEGLSearchConstants.PACKAGE:
//			searchPattern = createPackagePattern(patternString, limitTo, matchMode, isCaseSensitive);
			break;
		case IEGLSearchConstants.ALL_FUNCTIONS:
			// Search for nested functions
			searchPattern = createAllFunctionPattern(patternString, limitTo, matchMode, isCaseSensitive);
			break;
		
		case IEGLSearchConstants.ANNOTATION_PART:
			searchPattern = createPartPattern(patternString, ANNOTATION_SUFFIX, limitTo, matchMode, isCaseSensitive );
			break;
		case IEGLSearchConstants.STEREOTYPE_PART:
			searchPattern = createPartPattern(patternString, STEREOTYPE_SUFFIX, limitTo, matchMode, isCaseSensitive );
			break;
		case IEGLSearchConstants.CLASS_PART:
			searchPattern = createPartPattern(patternString, CLASS_SUFFIX, limitTo, matchMode, isCaseSensitive );
			break;
	}
	return searchPattern;
}

//private static SearchPattern createPackagePattern(String patternString, int limitTo, int matchMode, boolean isCaseSensitive) {
//	SearchPattern searchPattern = null;
//	switch (limitTo){
//		case IEGLSearchConstants.DECLARATIONS :
//			searchPattern = new PackageDeclarationPattern(patternString.toCharArray(), matchMode, isCaseSensitive);
//			break;
//		case IEGLSearchConstants.REFERENCES :
//			searchPattern = new PackageReferencePattern(patternString.toCharArray(), matchMode, isCaseSensitive);
//			break;
//		case IEGLSearchConstants.ALL_OCCURRENCES :
//			searchPattern = new OrPattern(
//				new PackageDeclarationPattern(patternString.toCharArray(), matchMode, isCaseSensitive),
//				new PackageReferencePattern(patternString.toCharArray(), matchMode, isCaseSensitive)
//			);
//			break;
//	}
//	return searchPattern;
//}
public static SearchPattern createPattern(IEGLElement element, int limitTo) {
	SearchPattern searchPattern = null;
	switch (element.getElementType()) {
//		case IEGLElement.FIELD :
//			IField field = (IField) element; 
//			String fullDeclaringName = field.getDeclaringPart().getFullyQualifiedName().replace('$', '.');
//			lastDot = fullDeclaringName.lastIndexOf('.');
//			char[] declaringSimpleName = (lastDot != -1 ? fullDeclaringName.substring(lastDot + 1) : fullDeclaringName).toCharArray();
//			char[] declaringQualification = lastDot != -1 ? fullDeclaringName.substring(0, lastDot).toCharArray() : CharOperation.NO_CHAR;
//			char[] name = field.getElementName().toCharArray();
//			char[] typeSimpleName;
//			char[] typeQualification;
//			try {
//				String typeSignature = field.getTypeSignature();
//				lastDot = typeSignature.lastIndexOf('.');
//				typeSimpleName = (lastDot != -1 ? typeSignature.substring(lastDot + 1) : typeSignature).toCharArray();
//				typeQualification = 
//					lastDot != -1 ? 
//						// prefix with a '*' as the full qualification could be bigger (because of an import)
//						CharOperation.concat(ONE_STAR, typeSignature.substring(0, lastDot).toCharArray()) : 
//						null;
//			} catch (EGLModelException e) {
//				return null;
//			}
//			switch (limitTo) {
//				case IEGLSearchConstants.DECLARATIONS :
//					searchPattern = 
//						new FieldDeclarationPattern(
//							name, 
//							EXACT_MATCH, 
//							CASE_SENSITIVE, 
//							declaringQualification, 
//							declaringSimpleName, 
//							typeQualification, 
//							typeSimpleName);
//					break;
//				case IEGLSearchConstants.REFERENCES :
//					searchPattern = 
//						new FieldReferencePattern(
//							name, 
//							EXACT_MATCH, 
//							CASE_SENSITIVE, 
//							declaringQualification, 
//							declaringSimpleName, 
//							typeQualification, 
//							typeSimpleName,
//							true,  // read access
//							true); // write access
//					break;
//				case IEGLSearchConstants.READ_ACCESSES :
//					searchPattern = 
//						new FieldReferencePattern(
//							name, 
//							EXACT_MATCH, 
//							CASE_SENSITIVE, 
//							declaringQualification, 
//							declaringSimpleName, 
//							typeQualification, 
//							typeSimpleName,
//							true,  // read access only
//							false);
//					break;
//				case IEGLSearchConstants.WRITE_ACCESSES :
//					searchPattern = 
//						new FieldReferencePattern(
//							name, 
//							EXACT_MATCH, 
//							CASE_SENSITIVE, 
//							declaringQualification, 
//							declaringSimpleName, 
//							typeQualification, 
//							typeSimpleName,
//							false,
//							true); // write access only
//					break;
//				case IEGLSearchConstants.ALL_OCCURRENCES :
//					searchPattern = new OrPattern(
//						new FieldDeclarationPattern(
//							name, 
//							EXACT_MATCH, 
//							CASE_SENSITIVE, 
//							declaringQualification, 
//							declaringSimpleName, 
//							typeQualification, 
//							typeSimpleName), 
//						new FieldReferencePattern(
//							name, 
//							EXACT_MATCH, 
//							CASE_SENSITIVE, 
//							declaringQualification, 
//							declaringSimpleName, 
//							typeQualification, 
//							typeSimpleName,
//							true,  // read access
//							true)); // write access
//					break;
//			}
//			break;
//		case IEGLElement.IMPORT_DECLARATION :
//			String elementName = element.getElementName();
//			lastDot = elementName.lastIndexOf('.');
//			if (lastDot == -1) return null; // invalid import declaration
//			IImportDeclaration importDecl = (IImportDeclaration)element;
//			if (importDecl.isOnDemand()) {
//				searchPattern = createPackagePattern(elementName.substring(0, lastDot), limitTo, EXACT_MATCH, CASE_SENSITIVE);
//			} else {
//				searchPattern = 
//					createPartPattern(
//						elementName.substring(lastDot+1).toCharArray(),
//						elementName.substring(0, lastDot).toCharArray(),
//						null,
//						limitTo);
//			}
//			break;
//		case IEGLElement.FUNCTION :
//			IFunction function = (IFunction) element;
//			fullDeclaringName = function.getDeclaringPart().getFullyQualifiedName();
//			lastDot = fullDeclaringName.lastIndexOf('.');
//			declaringSimpleName = (lastDot != -1 ? fullDeclaringName.substring(lastDot + 1) : fullDeclaringName).toCharArray();
//			declaringQualification = lastDot != -1 ? fullDeclaringName.substring(0, lastDot).toCharArray() : CharOperation.NO_CHAR;
//			char[] selector = function.getElementName().toCharArray();
//			char[] returnSimpleName;
//			char[] returnQualification;
//			try {
//				String returnType = function.getReturnTypeName();
//				lastDot = returnType.lastIndexOf('.');
//				returnSimpleName = (lastDot != -1 ? returnType.substring(lastDot + 1) : returnType).toCharArray();
//				returnQualification = 
//					lastDot != -1 ? 
//						// prefix with a '*' as the full qualification could be bigger (because of an import)
//						CharOperation.concat(ONE_STAR, returnType.substring(0, lastDot).toCharArray()) : 
//						null;
//			} catch (EGLModelException e) {
//				return null;
//			}
//			String[] parameterTypes = function.getParameterTypes();
//			int paramCount = parameterTypes.length;
//			char[][] parameterSimpleNames = new char[paramCount][];
//			char[][] parameterQualifications = new char[paramCount][];
//			for (int i = 0; i < paramCount; i++) {
//				String signature = Signature.toString(parameterTypes[i]).replace('$', '.');
//				lastDot = signature.lastIndexOf('.');
//				parameterSimpleNames[i] = (lastDot != -1 ? signature.substring(lastDot + 1) : signature).toCharArray();
//				parameterQualifications[i] = 
//					lastDot != -1 ? 
//						// prefix with a '*' as the full qualification could be bigger (because of an import)
//						CharOperation.concat(ONE_STAR, signature.substring(0, lastDot).toCharArray()) : 
//						null;
//			}
//			switch (limitTo) {
//				case IEGLSearchConstants.DECLARATIONS :
//					searchPattern = 
//						new FunctionDeclarationPattern(
//							selector, 
//							EXACT_MATCH, 
//							CASE_SENSITIVE, 
//							declaringQualification, 
//							declaringSimpleName, 
//							returnQualification, 
//							returnSimpleName, 
//							parameterQualifications, 
//							parameterSimpleNames);
//					break;
//				case IEGLSearchConstants.REFERENCES :
//					searchPattern = 
//						new FunctionReferencePattern(
//							selector, 
//							EXACT_MATCH, 
//							CASE_SENSITIVE, 
//							declaringQualification, 
//							declaringSimpleName, 
//							returnQualification, 
//							returnSimpleName, 
//							parameterQualifications, 
//							parameterSimpleNames,
//							function.getDeclaringPart());
//					break;
//				case IEGLSearchConstants.ALL_OCCURRENCES :
//					searchPattern = new OrPattern(
//							new FunctionDeclarationPattern(
//								selector, 
//								EXACT_MATCH, 
//								CASE_SENSITIVE, 
//								declaringQualification, 
//								declaringSimpleName, 
//								returnQualification, 
//								returnSimpleName, 
//								parameterQualifications, 
//								parameterSimpleNames), 
//							new FunctionReferencePattern(
//								selector, 
//								EXACT_MATCH, 
//								CASE_SENSITIVE, 
//								declaringQualification, 
//								declaringSimpleName, 
//								returnQualification, 
//								returnSimpleName, 
//								parameterQualifications, 
//								parameterSimpleNames,
//								function.getDeclaringPart()));
//					break;
//			}
//			break;
		case IEGLElement.PART :
			IPart type = (IPart)element;
			searchPattern = 
				createPartPattern(
					type.getElementName().toCharArray(), 
					type.getPackageFragment().getElementName().toCharArray(),
					enclosingPartNames(type),
					limitTo);
			break;
//		case IEGLElement.PACKAGE_DECLARATION :
//		case IEGLElement.PACKAGE_FRAGMENT :
//			searchPattern = createPackagePattern(element.getElementName(), limitTo, EXACT_MATCH, CASE_SENSITIVE);
//			break;
	}
	if (searchPattern != null) {
		searchPattern.focus = element;
	}
	return searchPattern;
}
private static SearchPattern createPartPattern(char[] simpleName, char[] packageName, char[][] enclosingPartNames, int limitTo) {
	SearchPattern searchPattern = null;
	switch (limitTo) {
		case IEGLSearchConstants.DECLARATIONS :
			searchPattern = 
				new PartDeclarationPattern(
					packageName, 
					enclosingPartNames, 
					simpleName, 
					PART_SUFFIX, 
					EXACT_MATCH, 
					CASE_SENSITIVE);
			break;
//		case IEGLSearchConstants.REFERENCES :
//			searchPattern = 
//				new PartReferencePattern(
//					CharOperation.concatWith(packageName, enclosingPartNames, '.'), 
//					simpleName, 
//					EXACT_MATCH, 
//					CASE_SENSITIVE);
//			break;
//		case IEGLSearchConstants.ALL_OCCURRENCES :
//			searchPattern = new OrPattern(
//				new PartDeclarationPattern(
//					packageName, 
//					enclosingPartNames, 
//					simpleName, 
//					PART_SUFFIX, 
//					EXACT_MATCH, 
//					CASE_SENSITIVE), 
//				new PartReferencePattern(
//					CharOperation.concatWith(packageName, enclosingPartNames, '.'), 
//					simpleName, 
//					EXACT_MATCH, 
//					CASE_SENSITIVE));
//			break;
	}
	return searchPattern;
}

/**
 * An AllFunctionPattern will search for both top level and nested functions as either declarations, references, or both.
 */
private static SearchPattern createAllFunctionPattern(String patternString, int limitTo, int matchMode, boolean isCaseSensitive) {
//	 Remove the whitespace from this string
	Lexer lexer = new Lexer(new StringReader(patternString));
	int token;
	final int InsideSelector = 1;
	final int InsideParameter = 2;
	int mode = InsideSelector;
	
	String declaringType = null, selector = null, parameterType = null;
	String[] parameterTypes = null;
	int parameterCount = -1;
	
	boolean foundClosingParenthesis = false;
	
	try {
		token = lexer.next_token().sym;
	} catch (IOException e) {
		return null;
	}
	while (token != NodeTypes.EOF) {
		switch(mode){
			//	read declaring type and selector
			case InsideSelector :
				switch (token) {
					case NodeTypes.DOT :
						if(declaringType == null){
							if(selector == null) return null;
							declaringType = selector;
						}else {
							String tokenSource = new String(lexer.yytext());
							declaringType += tokenSource + selector;
						}
						selector = null;
						break;
					case NodeTypes.LPAREN :
						parameterTypes = new String[5];
						parameterCount = 0;
						mode = InsideParameter;
						break;
					default: // all other tokens are considered identifiers (see bug 21763 Problem in Java search [search])
						if (selector == null) {
							selector = new String(lexer.yytext());
						} else {
							selector += new String(lexer.yytext());
						}
						break;
				}
				break;
			// read parameter types
			case InsideParameter :
				switch (token) {
					case NodeTypes.COMMA:
						if (parameterType == null) return null;
						if (parameterTypes.length == parameterCount){
							System.arraycopy(parameterTypes, 0, parameterTypes = new String[parameterCount*2], 0, parameterCount);
						}
						parameterTypes[parameterCount++] = parameterType;
						parameterType = null;
						break;
					case NodeTypes.RPAREN:
						foundClosingParenthesis = true;
						if (parameterType != null){
							if (parameterTypes.length == parameterCount){
								System.arraycopy(parameterTypes, 0, parameterTypes = new String[parameterCount*2], 0, parameterCount);
							}
							parameterTypes[parameterCount++] = parameterType;
						}
						break;
					default: // all other tokens are considered identifiers (see bug 21763 Problem in Java search [search])
						if (parameterType == null){
							parameterType = new String(lexer.yytext());
						} else {
							parameterType += new String(lexer.yytext());
						}
				}
				break;
		}
		try {
			token = lexer.next_token().sym;
		} catch (IOException e) {
			return null;
		}
	}
	
	// parenthesis mismatch
	if (parameterCount>0 && !foundClosingParenthesis) return null;
	if (selector == null) return null;

	char[] selectorChars = selector.toCharArray();
	if (selectorChars.length == 1 && selectorChars[0] == '*') selectorChars = null;
		
	char[] declaringTypeQualification = null; //, declaringTypeSimpleName = null;
	//char[] returnTypeQualification = null, returnTypeSimpleName = null;
	char[][] parameterTypeQualifications = null, parameterTypeSimpleNames = null;

	// extract declaring type infos
	if (declaringType != null){
		declaringTypeQualification = declaringType.toCharArray();
		if (declaringTypeQualification.length == 1 && declaringTypeQualification[0] == '*') declaringTypeQualification = null;
		
//		int lastDotPosition = CharOperation.lastIndexOf('.', declaringTypePart);
//		if (lastDotPosition >= 0){
//			declaringTypeQualification = CharOperation.subarray(declaringTypePart, 0, lastDotPosition);
//			if (declaringTypeQualification.length == 1 && declaringTypeQualification[0] == '*') declaringTypeQualification = null;
//			declaringTypeSimpleName = CharOperation.subarray(declaringTypePart, lastDotPosition+1, declaringTypePart.length);
//		} else {
//			declaringTypeQualification = null;
//			declaringTypeSimpleName = declaringTypePart;
//		}
//		if (declaringTypeSimpleName.length == 1 && declaringTypeSimpleName[0] == '*') declaringTypeSimpleName = null;
	}
	// extract parameter types infos
	if (parameterCount >= 0){
		parameterTypeQualifications = new char[parameterCount][];
		parameterTypeSimpleNames = new char[parameterCount][];
		for (int i = 0; i < parameterCount; i++){
			char[] parameterTypePart = parameterTypes[i].toCharArray();
			int lastDotPosition = CharOperation.lastIndexOf('.', parameterTypePart);
			if (lastDotPosition >= 0){
				parameterTypeQualifications[i] = CharOperation.subarray(parameterTypePart, 0, lastDotPosition);
				if (parameterTypeQualifications[i].length == 1 && parameterTypeQualifications[i][0] == '*') {
					parameterTypeQualifications[i] = null;
				} else {
					// prefix with a '*' as the full qualification could be bigger (because of an import)
					parameterTypeQualifications[i] = CharOperation.concat(ONE_STAR, parameterTypeQualifications[i]);
				}
				parameterTypeSimpleNames[i] = CharOperation.subarray(parameterTypePart, lastDotPosition+1, parameterTypePart.length);
			} else {
				parameterTypeQualifications[i] = null;
				parameterTypeSimpleNames[i] = parameterTypePart;
			}
			if (parameterTypeSimpleNames[i].length == 1 && parameterTypeSimpleNames[i][0] == '*') parameterTypeSimpleNames[i] = null;
		}
	}	
//	// extract return type infos
//	if (returnType != null){
//		char[] returnTypePart = returnType.toCharArray();
//		int lastDotPosition = CharOperation.lastIndexOf('.', returnTypePart);
//		if (lastDotPosition >= 0){
//			returnTypeQualification = CharOperation.subarray(returnTypePart, 0, lastDotPosition);
//			if (returnTypeQualification.length == 1 && returnTypeQualification[0] == '*') {
//				returnTypeQualification = null;
//			} else {
//				// because of an import
//				returnTypeQualification = CharOperation.concat(ONE_STAR, returnTypeQualification);
//			}			
//			returnTypeSimpleName = CharOperation.subarray(returnTypePart, lastDotPosition+1, returnTypePart.length);
//		} else {
//			returnTypeQualification = null;
//			returnTypeSimpleName = returnTypePart;
//		}
//		if (returnTypeSimpleName.length == 1 && returnTypeSimpleName[0] == '*') returnTypeSimpleName = null;
//	}
	SearchPattern searchPattern = null;
	switch (limitTo){
		case IEGLSearchConstants.DECLARATIONS :
			searchPattern = new FunctionDeclarationPattern(selectorChars, matchMode, isCaseSensitive, declaringTypeQualification);
			break;
		case IEGLSearchConstants.REFERENCES :
			searchPattern = 
				new FunctionReferencePattern(
					selectorChars, 
					matchMode, 
					isCaseSensitive, 
					declaringTypeQualification, 
					//declaringTypeSimpleName, 
					//returnTypeQualification, 
					//returnTypeSimpleName, 
					//parameterTypeQualifications, 
					//parameterTypeSimpleNames,
					null);
			break;
		case IEGLSearchConstants.ALL_OCCURRENCES :
			searchPattern = new OrPattern(
					new FunctionDeclarationPattern(selectorChars, matchMode, isCaseSensitive, declaringTypeQualification),
				new FunctionReferencePattern(
					selectorChars, 
					matchMode, 
					isCaseSensitive, 
					declaringTypeQualification, 
					//declaringTypeSimpleName, 
					//returnTypeQualification, 
					//returnTypeSimpleName, 
					//parameterTypeQualifications, 
					//parameterTypeSimpleNames,
					null));
			break;
	}
	return searchPattern;
}
/**
 * Part pattern are formed by [qualification.]type
 * e.g. java.lang.Object
 *		Runnable
 *
 */
private static SearchPattern createPartPattern(String patternString, char searchFor, int limitTo, int matchMode, boolean isCaseSensitive) {

	// Remove the whitespace from this string
	Lexer lexer = new Lexer(new StringReader(patternString));
	String part = null;
	int token;
	try {
		token = lexer.next_token().sym;
	} catch (IOException e) {
		return null;
	}
	while (token != NodeTypes.EOF) {
		switch (token) {
			default :
				if (part == null) {
					part = lexer.yytext();
				} else {
					part += lexer.yytext();
				}
		}
		try {
			token = lexer.next_token().sym;
		} catch (IOException e) {
			return null;
		}
	}

	if (part == null) return null;

	char[] qualificationChars = null, typeChars = null;

	//EGLTODO: Brian: extract declaring part infos - will EGL ever have a declaraing part info? (nested functions?)
	if (part != null){
		char[] typePart = part.toCharArray();
		int lastDotPosition = CharOperation.lastIndexOf('.', typePart);
		if (lastDotPosition >= 0){
			qualificationChars = CharOperation.subarray(typePart, 0, lastDotPosition);
			if (qualificationChars.length == 1 && qualificationChars[0] == '*') qualificationChars = null;
			typeChars = CharOperation.subarray(typePart, lastDotPosition+1, typePart.length);
		} else {
			qualificationChars = null;
			typeChars = typePart;
		}
//		if (typeChars.length == 1 && typeChars[0] == '*') typeChars = null;
	}
	SearchPattern searchPattern = null;
	switch (limitTo){
		case IEGLSearchConstants.DECLARATIONS : // cannot search for explicit member types
			searchPattern = new QualifiedPartDeclarationPattern(qualificationChars, typeChars, searchFor, matchMode, isCaseSensitive);
			break;
		case IEGLSearchConstants.REFERENCES :
			searchPattern = new PartReferencePattern(qualificationChars, typeChars, searchFor, matchMode, isCaseSensitive);
			break;
		case IEGLSearchConstants.ALL_OCCURRENCES :
			searchPattern = new OrPattern(
				new QualifiedPartDeclarationPattern(qualificationChars, typeChars, searchFor, matchMode, isCaseSensitive),// cannot search for explicit member types
				new PartReferencePattern(qualificationChars, typeChars, searchFor, matchMode, isCaseSensitive));
			break;
	}
	return searchPattern;

}

protected abstract void decodeIndexEntry(IEntryResult entryResult);
/**
 * Returns the enclosing type names of the given type.
 */
private static char[][] enclosingPartNames(IPart type) {
	IEGLElement parent = type.getParent();
	switch (parent.getElementType()) {
		case IEGLElement.EGL_FILE:
			return CharOperation.NO_CHAR_CHAR;
		case IEGLElement.PART:
			return CharOperation.arrayConcat(
				enclosingPartNames((IPart)parent), 
				parent.getElementName().toCharArray());
		default:
			return null;
	}
}
/**
 * Feed the requestor according to the current search pattern
 */
public abstract void feedIndexRequestor(IIndexSearchRequestor requestor, int detailLevel, int[] references, IndexInput input, IEGLSearchScope scope)  throws IOException ;
/**
 * Query a given index for matching entries. 
 */
public void findIndexMatches(IIndex index, IIndexSearchRequestor requestor, int detailLevel, IProgressMonitor progressMonitor, IEGLSearchScope scope) throws IOException {

	if (progressMonitor != null && progressMonitor.isCanceled()) throw new OperationCanceledException();

	IndexInput input = new BlocksIndexInput(index.getIndexFile());
	try {
		input.open();
		findIndexMatches(input, requestor, detailLevel, progressMonitor,scope);
	} finally {
		input.close();
	}
}
/**
 * Query a given index for matching entries. 
 */
public void findIndexMatches(IndexInput input, IIndexSearchRequestor requestor, int detailLevel, IProgressMonitor progressMonitor, IEGLSearchScope scope) throws IOException {

	if (progressMonitor != null && progressMonitor.isCanceled()) throw new OperationCanceledException();
	
	/* narrow down a set of entries using prefix criteria */
	IEntryResult[] entries = input.queryEntriesPrefixedBy(indexEntryPrefix());
	if (entries == null) return;
	
	/* only select entries which actually match the entire search pattern */
	for (int i = 0, max = entries.length; i < max; i++){

		if (progressMonitor != null && progressMonitor.isCanceled()) throw new OperationCanceledException();

		/* retrieve and decode entry */	
		IEntryResult entry = entries[i];
		decodeIndexEntry(entry);
		if (matchIndexEntry()){
			feedIndexRequestor(requestor, detailLevel, entry.getFileReferences(), input, scope);
		}
	}
}
/**
 * Answers the suitable prefix that should be used in order
 * to query indexes for the corresponding item.
 * The more accurate the prefix and the less false hits will have
 * to be eliminated later on.
 */
public abstract char[] indexEntryPrefix();

/**
 * Returns the type(s) of container for this pattern.
 * It is a bit combination of types, denoting compilation unit, class declarations, field declarations or function declarations.
 */
protected abstract int matchContainer();
/**
 * Finds out whether the given binary info matches this search pattern.
 * Default is to return false.
 */
public boolean matchesBinary(Object binaryInfo, Object enclosingBinaryInfo) {
	return false;
}
/**
 * Check if the given node syntactically matches this pattern.
 * If it does, add it to the match set.
 */
protected void matchCheck(Node node, MatchingNodeSet set) {
	int matchLevel = this.matchLevel(node, false);
	switch (matchLevel) {
		case SearchPattern.POSSIBLE_MATCH:
			set.addPossibleMatch(node);
			break;
		case SearchPattern.ACCURATE_MATCH:
			set.addTrustedMatch(node);
	}
}

protected int matchCheck(Node node) {
	int matchLevel = this.matchLevel(node, false);
	return matchLevel;
}

protected int matchCheck(IMember member) {
	int matchLevel = this.matchLevel(member, false);
	return matchLevel;
}

/**
 * Returns whether the given name matches the given pattern.
 */
protected boolean matchesName(char[] pattern, char[] name) {
	if (pattern == null) return true; // null is as if it was "*"
	if (name != null){
		switch (this.matchMode) {
			case EXACT_MATCH :
				return CharOperation.equals(pattern, name, this.isCaseSensitive);
			case PREFIX_MATCH :
				return CharOperation.prefixEquals(pattern, name, this.isCaseSensitive);
			case PATTERN_MATCH :
				if (!this.isCaseSensitive) {
					pattern = CharOperation.toLowerCase(pattern);
				}
				return CharOperation.match(pattern, name, this.isCaseSensitive);
		}
	}
	return false;
}
/**
 * Returns whether the given type binding matches the given simple name pattern 
 * and qualification pattern.
 */
protected boolean matchesType(char[] simpleNamePattern, char[] qualificationPattern, char[] fullyQualifiedTypeName) {
	char[] pattern;
	if (simpleNamePattern == null) {
		if (qualificationPattern == null) {
			pattern = ONE_STAR;
		} else {
			pattern = CharOperation.concat(qualificationPattern, ONE_STAR, '.');
		}
	} else {
		if (qualificationPattern == null) {
			pattern = CharOperation.concat(ONE_STAR, simpleNamePattern);
		} else {
			pattern = CharOperation.concat(qualificationPattern, simpleNamePattern, '.');
		}
	}
	if (!this.isCaseSensitive) {
		pattern = CharOperation.toLowerCase(pattern);
	}
	return 
		CharOperation.match(
			pattern,
			fullyQualifiedTypeName,
			this.isCaseSensitive
		);
}
/**
 * Checks whether an entry matches the current search pattern
 */
protected abstract boolean matchIndexEntry();

/**
 * Finds out whether the given ast node matches this search pattern.
 * Returns IMPOSSIBLE_MATCH if it doesn't.
 * Returns POSSIBLE_MATCH if it potentially matches this search pattern 
 * and it has not been reolved, and it needs to be resolved to get more information.
 * Returns ACCURATE_MATCH if it matches exactly this search pattern (ie. 
 * it doesn't need to be resolved or it has already been resolved.)
 * Returns INACCURATE_MATCH if it potentially exactly this search pattern (ie. 
 * it has already been resolved but resolving failed.)
 */
public abstract int matchLevel(Node node, boolean resolve);

/**
 * Finds out whether the given ast node matches this search pattern.
 * Returns IMPOSSIBLE_MATCH if it doesn't.
 * Returns POSSIBLE_MATCH if it potentially matches this search pattern 
 * and it has not been reolved, and it needs to be resolved to get more information.
 * Returns ACCURATE_MATCH if it matches exactly this search pattern (ie. 
 * it doesn't need to be resolved or it has already been resolved.)
 * Returns INACCURATE_MATCH if it potentially exactly this search pattern (ie. 
 * it has already been resolved but resolving failed.)
 */
public abstract int matchLevel(IMember member, boolean resolve);

/**
 * Add square brackets to the given simple name
 */
protected char[] toArrayName(char[] simpleName, int dimensions) {
	if (dimensions == 0) return simpleName;
	int length = simpleName.length;
	char[] result = new char[length + dimensions * 2];
	System.arraycopy(simpleName, 0, result, 0, length);
	for (int i = 0; i < dimensions; i++) {
		result[simpleName.length + i*2] = '[';
		result[simpleName.length + i*2 + 1] = ']';
	}
	return result;
}
public String toString(){
	return "SearchPattern"; //$NON-NLS-1$
}

/**
 * Report the match of the given import reference
 */
protected void matchReportImportRef(ImportDeclaration importRef, IEGLElement element, int accuracy, MatchLocator2 locator) throws CoreException {
	// default is to report a match as a regular ref.
	this.matchReportReference(importRef.getName(), element, accuracy, locator);
}

/**
 * Reports the match of the given reference.
 */
protected void matchReportReference(Node reference, IEGLElement element, int accuracy, MatchLocator2 locator) throws CoreException {
	// default is to report a match on the whole node.
	locator.report(reference.getOffset(), reference.getOffset() + reference.getLength(), element, accuracy);
}

protected org.eclipse.edt.mof.egl.Part getPartBinding(Part part){
	return getPartBinding(part.getName());
}

protected org.eclipse.edt.mof.egl.Part getPartBinding(Name name){
	Type type = name.resolveType();
	if (type instanceof org.eclipse.edt.mof.egl.Part) {
		return (org.eclipse.edt.mof.egl.Part)type;
	}
	return null;
}

//protected int matchLevelForType(char[] simpleNamePattern, char[] qualificationPattern, NestedFunction function) {
//	if (function == null) return INACCURATE_MATCH;
//	
//	StringBuffer qualifiedPartName = new StringBuffer();
//	char[] qualifiedPackageName;
//	Part topLevelPart;
//
//
//	topLevelPart = function;
//	if(function instanceof IEGLFunctionDeclaration){
//		topLevelPart = (IEGLPart)((IEGLFunctionDeclaration)function).getFunctionContainer();
//		qualifiedPartName.append(topLevelPart.getName().getName());
//		qualifiedPartName.append("." + function.getName().getName()); //$NON-NLS-1$
//	}else if(function instanceof IEGLAbstractFunction){
//	    topLevelPart = (IEGLPart)((IEGLAbstractFunction)function).getFunctionContainer();
//		qualifiedPartName.append(topLevelPart.getName().getName());
//		qualifiedPartName.append("." + function.getName().getName()); //$NON-NLS-1$
//	}else{
//		topLevelPart = (IEGLPart)function;
//		qualifiedPartName.append(function.getName().getName());
//	}
//		
//	IEGLPackageDeclaration pkgDecl = ((IEGLFile)topLevelPart.getContainer()).getPackageDeclaration();
//	PackageDeclaration pkgDecl = null;
//	if(pkgDecl != null){
//		qualifiedPackageName = pkgDecl.getPackageName().getCanonicalName().toCharArray();
//	}else{
//		qualifiedPackageName = null;
//	}
//	char[] qualifiedSourceName = qualifiedPartName.toString().toCharArray();
//	
//	// The user could be searching for a function name:
//	// w/ no package or part qualifier
//	// w/ just a package qualifier
//	// w/ just a part qualifier
//	// w/ a package and a part qualifier 
//	// This first check handles all cases except for "just a part qualifier"
//	if (this.matchesType(
//			simpleNamePattern, 
//			qualificationPattern, 
//			qualifiedPackageName == null ? 
//				qualifiedSourceName : 
//				CharOperation.concat(qualifiedPackageName, qualifiedSourceName, '.'))) {
//		return ACCURATE_MATCH;
//	} else {
//		// search for a part.function in any package
//		if(new String(qualificationPattern).indexOf('.') == -1){
//			if(this.matchesType(
//					CharOperation.concat(qualificationPattern, simpleNamePattern, '.'),
//					null,
//					qualifiedPackageName == null ? 
//							qualifiedSourceName : 
//							CharOperation.concat(qualifiedPackageName, qualifiedSourceName, '.'))){
//				return ACCURATE_MATCH;
//			}
//					
//		}
//		return IMPOSSIBLE_MATCH;
//	}
//}

/**
 * Returns whether the given part matches the given simple name pattern 
 * and qualification pattern.
 * Returns ACCURATE_MATCH if it does.
 * Returns INACCURATE_MATCH if resolve failed.
 * Returns IMPOSSIBLE_MATCH if it doesn't.
 */
protected int matchLevelForType(char[] simpleNamePattern, char[] qualificationPattern, org.eclipse.edt.mof.egl.Part partBinding) {
	if (partBinding == null  ) return INACCURATE_MATCH;
	
	StringBuffer qualifiedPartName = new StringBuffer();
	char[] qualifiedPackageName;

	qualifiedPartName.append(partBinding.getCaseSensitiveName());
//	
//	IEGLPackageDeclaration pkgDecl = ((IEGLFile)topLevelPart.getContainer()).getPackageDeclaration();
//	PackageDeclaration pkgDecl = null;
//	if(pkgDecl != null){
//		qualifiedPackageName = pkgDecl.getPackageName().getCanonicalName().toCharArray();
//	}else{
	
	String packageName = partBinding.getCaseSensitivePackageName();
	if(packageName != null && packageName.length() > 0) {
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
		return IMPOSSIBLE_MATCH;
	}
}

/**
 * Returns whether the given part matches the given simple name pattern 
 * and qualification pattern.
 * Returns ACCURATE_MATCH if it does.
 * Returns INACCURATE_MATCH if resolve failed.
 * Returns IMPOSSIBLE_MATCH if it doesn't.
 */
protected int matchLevelForType(char[] simpleNamePattern, char[] qualificationPattern, AnnotationType binding) {
	if (binding == null){
		return INACCURATE_MATCH;
	}
	
	StringBuffer qualifiedPartName = new StringBuffer();
	char[] qualifiedPackageName;

	qualifiedPartName.append(binding.getCaseSensitiveName());
	
	String packageName = binding.getCaseSensitivePackageName();
	if(packageName != null && packageName.length() > 0) {
		qualifiedPackageName = packageName.toString().toCharArray();
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
		return IMPOSSIBLE_MATCH;
	}
}

protected int matchLevelForType(char[] simpleNamePattern, char[] qualificationPattern, IPart part) {
	if (part == null  ) return INACCURATE_MATCH;
	
	StringBuffer qualifiedPartName = new StringBuffer();
	qualifiedPartName.append(part.getElementName());
	
	if (this.matchesType(
			simpleNamePattern, 
			qualificationPattern, 
			part.getFullyQualifiedName().toCharArray())) {
		return ACCURATE_MATCH;
	} else {
		if(qualificationPattern != null && new String(qualificationPattern).indexOf('.') == -1){
			if(this.matchesType(
					CharOperation.concat(qualificationPattern, simpleNamePattern, '.'),
					null,
					part.getFullyQualifiedName().toCharArray())){
				return ACCURATE_MATCH;
			}
					
		}
	}
	return IMPOSSIBLE_MATCH;
}

	public int matchesPartType(Name node,org.eclipse.edt.mof.egl.Part partBinding,boolean forceQualification){
		return IMPOSSIBLE_MATCH;
	}
	
	public int matchesPart(Part node){
		return IMPOSSIBLE_MATCH;
	}
	

	public int matchesPart(IPart part){
		return IMPOSSIBLE_MATCH;
	}
	
	public int matchesFunctionPartType(Name node,FunctionPart partBinding){
		return IMPOSSIBLE_MATCH;
	}
	
	public int matchesFunctionPartType(IFunction function){
		return IMPOSSIBLE_MATCH;
	}
	
	public static final int DECLARATION = 1;
	public static final int REFERENCE = 2;
	public static final int ALLOCCUR = 3;
	public abstract int getPatternType();
}
