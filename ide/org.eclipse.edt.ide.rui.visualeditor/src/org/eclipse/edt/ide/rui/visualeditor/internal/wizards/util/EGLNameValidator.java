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
package org.eclipse.edt.ide.rui.visualeditor.internal.wizards.util;


import java.util.ArrayList;
import java.util.Hashtable;

import org.eclipse.edt.compiler.core.EGLKeywordHandler;
import org.eclipse.edt.compiler.internal.util.EGLMessage;

import com.ibm.icu.util.StringTokenizer;

public class EGLNameValidator {

	public static final String copyright = "Licensed Materials -- Property of IBM\n(c) Copyright International Business Machines Corporation, 2000,2002\nUS Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp."; //$NON-NLS-1$
	final public static String PART = "Part"; //$NON-NLS-1$
	final public static String PROGRAM = "Program"; //$NON-NLS-1$
	final public static String PROGRAM_EXTERNAL_NAME = "Program external name"; //$NON-NLS-1$
	final public static String FUNCTION = "Function"; //$NON-NLS-1$
	final public static String RECORD_FILE_NAME = "Logical file name"; //$NON-NLS-1$
	final public static String RECORD = "Record"; //$NON-NLS-1$
	final public static String STRUCTURE = "Structure"; //$NON-NLS-1$
	final public static String TABLE = "Table"; //$NON-NLS-1$
	final public static String FORM = "Form"; //$NON-NLS-1$
	final public static String TABLE_EXTERNAL_NAME = "Table external name"; //$NON-NLS-1$
	final public static String DATAITEM = "DataItem"; //$NON-NLS-1$
	final public static String BUILD_DESCRIPTOR = "BuildDescriptor"; //$NON-NLS-1$
	final public static String LINKAGE_OPTIONS = "LinkageOptions"; //$NON-NLS-1$
	final public static String RESOURCE_ASSOCIATIONS = "ResourceAssociations"; //$NON-NLS-1$
	final public static String BIND_CONTROL = "BindControl"; //$NON-NLS-1$
	final public static String LINK_EDIT = "LinkEdit"; //$NON-NLS-1$
	final public static String TYPEDEF = "typeDef"; //$NON-NLS-1$
	final public static String TABLE_GENERATION = "table generation"; //$NON-NLS-1$
	final public static String SYMBOLIC_PARAMETER = "symbolicParameter"; //$NON-NLS-1$
	final public static String STATEMENT_ID = "statement ID"; //$NON-NLS-1$

	final public static int PART_LENGTH = 128;
	final public static int PROGRAM_LENGTH = PART_LENGTH;
	final public static int PROGRAM_EXTERNAL_NAME_LENGTH = PART_LENGTH;
	final public static int RECORD_FILE_NAME_LENGTH = 8;
	final public static int FUNCTION_LENGTH = PART_LENGTH;
	final public static int RECORD_LENGTH = PART_LENGTH;
	final public static int STRUCTURE_LENGTH = PART_LENGTH;
	final public static int FORM_LENGTH = PART_LENGTH;
	final public static int TABLE_LENGTH = PART_LENGTH;
	final public static int TABLE_EXTERNAL_NAME_LENGTH = PART_LENGTH;
	final public static int ITEM_LENGTH = PART_LENGTH;
	final public static int BUILD_DESCRIPTOR_LENGTH = PART_LENGTH;
	final public static int LINKAGE_OPTIONS_LENGTH = PART_LENGTH;
	final public static int RESOURCE_ASSOCIATIONS_LENGTH = PART_LENGTH;
	final public static int BIND_CONTROL_LENGTH = PART_LENGTH;
	final public static int LINK_EDIT_LENGTH = PART_LENGTH;
	final public static int TYPEDEF_LENGTH = PART_LENGTH;
	final public static int PARAMETER_LENGTH = PART_LENGTH;
	final public static int DECLARATION_LENGTH = PART_LENGTH;
	final public static int TABLE_GENERATION_LENGTH = PART_LENGTH;
	final public static int GLOBAL_IO_OBJECT_LENGTH = PART_LENGTH;
	final public static int SYMBOLIC_PARAMETER_LENGTH = 30;
	final public static int STATEMENT_ID_LENGTH = PART_LENGTH;
	final public static int PROJECT_ID_LENGTH = 44;

	private static Hashtable partNameLengths = new Hashtable();

	static {
		partNameLengths.put(PART, new Integer(PART_LENGTH));
		partNameLengths.put(PROGRAM, new Integer(PROGRAM_LENGTH));
		partNameLengths.put(PROGRAM_EXTERNAL_NAME, new Integer(PROGRAM_EXTERNAL_NAME_LENGTH));
		partNameLengths.put(FUNCTION, new Integer(FUNCTION_LENGTH));
		partNameLengths.put(RECORD, new Integer(RECORD_LENGTH));
		partNameLengths.put(RECORD_FILE_NAME, new Integer(RECORD_FILE_NAME_LENGTH));
		partNameLengths.put(STRUCTURE, new Integer(STRUCTURE_LENGTH));
		partNameLengths.put(FORM, new Integer(FORM_LENGTH));
		partNameLengths.put(TABLE, new Integer(TABLE_LENGTH));
		partNameLengths.put(TABLE_EXTERNAL_NAME, new Integer(TABLE_EXTERNAL_NAME_LENGTH));
		partNameLengths.put(DATAITEM, new Integer(ITEM_LENGTH));
		partNameLengths.put(BUILD_DESCRIPTOR, new Integer(BUILD_DESCRIPTOR_LENGTH));
		partNameLengths.put(LINKAGE_OPTIONS, new Integer(LINKAGE_OPTIONS_LENGTH));
		partNameLengths.put(RESOURCE_ASSOCIATIONS, new Integer(RESOURCE_ASSOCIATIONS_LENGTH));
		partNameLengths.put(BIND_CONTROL, new Integer(BIND_CONTROL_LENGTH));
		partNameLengths.put(LINK_EDIT, new Integer(LINK_EDIT_LENGTH));
		partNameLengths.put(TYPEDEF, new Integer(TYPEDEF_LENGTH));
		partNameLengths.put(TABLE_GENERATION, new Integer(TABLE_GENERATION_LENGTH));
		partNameLengths.put(SYMBOLIC_PARAMETER, new Integer(SYMBOLIC_PARAMETER_LENGTH));
		partNameLengths.put(STATEMENT_ID, new Integer(STATEMENT_ID_LENGTH));
	}

	private static String[] RESERVED_WORDS = EGLKeywordHandler.getKeywordNames();

	public static String[] keywords = EGLKeywordHandler.getKeywordNames();
	/**
	 * Test constructor comment.
	 */
	public EGLNameValidator() {
		super();
	}
	/**
	 *
	 */
	private static String getResourceBundleName() {

		return org.eclipse.edt.compiler.internal.IEGLBaseConstants.EGL_VALIDATION_RESOURCE_BUNDLE_NAME;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/22/2001 5:25:49 PM)
	 * @return boolean
	 * @param candidate java.lang.String
	 */
	public static boolean isJavaIdentifier(String candidate) {
		// Cannot be one of the reserved words
//		for (int i = 0; i < RESERVED_WORDS.length; i++) {
//			if (RESERVED_WORDS[i].equals(candidate))
//				return false;
//		}

		if (candidate == null || candidate.length() == 0 || !Character.isJavaIdentifierStart(candidate.charAt(0)))
			return false;

		for (int i = 1; i < candidate.length(); i++)
			if (!Character.isJavaIdentifierPart(candidate.charAt(i)))
				return false;

		return true;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/22/2001 5:21:12 PM)
	 * @return boolean
	 * @param candidate java.lang.String
	 */
	public static boolean isJavaPackageName(String candidate) {
		// Candidate must not be null or empty
		if (candidate == null || candidate.length() == 0)
			return false;

		// Check each part to see if they are a valid Java identifier
		StringTokenizer tokenizer = new StringTokenizer(candidate, ".", true); //$NON-NLS-1$

		// The first token should be a valid Java Identifier
		if (!isJavaIdentifier(tokenizer.nextToken()))
			return false;

		// If there is anything more, the next token consists of a "." and a valid Java identifier
		while (tokenizer.hasMoreTokens()) {
			// We know this token is a ".", so just throw it away
			tokenizer.nextToken();
			if (!tokenizer.hasMoreTokens() || !isJavaIdentifier(tokenizer.nextToken()))
				return false;
		}

		return true;
	}
	/**
	 *
	 */
	public static boolean isKeyword(String name) {

		for (int i = 0; i < keywords.length; i++) {
			if (keywords[i].equalsIgnoreCase(name))
				return true;
		}

		return false;
	}
	/**
	 *
	 * return an ArrayList of EGLMessages
	 */
	// EGLTODO - commented out so code will compile
	//	public static ArrayList parserMessages(
	//		ParseResults parseResults,
	//		Object targetObject) {
	//
	//		ArrayList messages = new ArrayList();
	//		if (parseResults == null)
	//			return messages;
	//
	//		Iterator enum = parseResults.problems.iterator();
	//		while (enum.hasNext()) {
	//			ParseProblem problem = (ParseProblem) enum.next();
	//			messages.add(
	//				EGLMessage.createEGLScriptParserErrorMessage(
	//					EGLMessage.getStatementParserResourceBundleName(),
	//					targetObject,
	//					problem));
	//		}
	//
	//		return messages;
	//	}
	/**
	 *
	 */
	public static boolean startsWithEZE(String name) {

		if (name.length() < 3)
			return false;

		return name.toUpperCase().startsWith("EZE"); //$NON-NLS-1$
	}
	/**
	 * Make sure each character is a valid character
	 * First charaacter must be a letter
	 * all other characters must be a letter or a digit
	 * does not allow special characters like isJavaIdentifier does 
	 *
	 * if name is null return false
	 */
	public static boolean strictValidateCharacters(String name) {

		if (name == null)
			return false;

		if (name == "") //$NON-NLS-1$
			return true;

		if (!Character.isLetter(name.charAt(0)))
			return false;

		for (int i = 1; i < name.length(); i++) {
			if (!Character.isLetterOrDigit(name.charAt(i)))
				return false;
		}
		return true;
	}
	/**
	 * Make sure each character is a valid character
	 * First charaacter must be a java letter
	 * all other characters must be a java letter or a digit
	 *
	 * if name is null return false
	 */
	private static boolean validateCharacters(String name) {

		if (name == null)
			return false;

		if (name == "") //$NON-NLS-1$
			return true;

		if (!Character.isJavaIdentifierStart(name.charAt(0)))
			return false;

		for (int i = 1; i < name.length(); i++) {
			if (!Character.isJavaIdentifierPart(name.charAt(i)))
				if (name.charAt(i) != '-')
					return false;
		}
		return true;
	}
	/**
	 * Make sure each character is a valid character
	 * First charaacter must be a java letter
	 * all other characters must be a java letter or a digit
	 *
	 * if name is null return false
	 */
	private static boolean validateCharacters(String name, String nameType) {
	    
	    if (nameType == BUILD_DESCRIPTOR) {
	        return true;
	    }
	    
		if (nameType == RECORD_FILE_NAME)
			return strictValidateCharacters(name);
		else
			return validateCharacters(name);
	}

	/**
	 * Validate the name
	 *
	 * 1. name can not start with "EZE" (any case)
	 * 2. name can not be an EGL keyword
	 * 3. validate the length of the name taking the name type into consideration
	 * 4. make sure each character is a valid character
	 *    first character must be a java letter
	 *    all other characters must be a java letter or a digit
	 *
	 * Parameters:
	 *    String name - name to be validated
	 *    String nameType - identifies the type of string to be validated
	 *    Object targetObject - object associated with any EGLMessage created (See EGLMessage)
	 *
	 * Returns:
	 *    ArrayList of EGLMessages
	 *      - multiple errors can be returned
	 *      - if no error, the ArrayList is empty
	 *
	 * Possible errors:
	 *    3000 - name required (name is null or blank)
	 *    3001 - name length invalid
	 *    3002 - invalid character in name
	 *    3003 - invalid EZE prefix in name
	 */
	public static ArrayList validateEGLName(String name, String nameType, Object targetObject) {

		ArrayList eglMessages = new ArrayList();

		//If no name, return 
		if (name == null || name.equals("")) { //$NON-NLS-1$
			return eglMessages;
		}

		//default the type to a generic part
		if (nameType == null)
			nameType = PART;

		//Check if name is a reserved keyword
		if (isKeyword(name))
			eglMessages.add(
				EGLMessage.createEGLValidationErrorMessage(
					EGLMessage.EGLMESSAGE_RESERVED_WORD_NOT_ALLOWED,
					targetObject,
					new String[] { name }));

		eglMessages.addAll(validateEGLNameWithoutReservedValidation(name, nameType, targetObject));
		return eglMessages;
	}
	/**
	 * Validate the name
	 *
	 * 1. name can not start with "EZE" (any case)
	 * 3. validate the length of the name taking the name type into consideration
	 * 4. make sure each character is a valid character
	 *    first character must be a java letter
	 *    all other characters must be a java letter or a digit
	 *
	 * Parameters:
	 *    String name - name to be validated
	 *    String nameType - identifies the type of string to be validated
	 *    Object targetObject - object associated with any EGLMessage created (See EGLMessage)
	 *
	 * Returns:
	 *    ArrayList of EGLMessages
	 *      - multiple errors can be returned
	 *      - if no error, the ArrayList is empty
	 *
	 * Possible errors:
	 *    3000 - name required (name is null or blank)
	 *    3001 - name length invalid
	 *    3002 - invalid character in name
	 *    3003 - invalid EZE prefix in name
	 */
	public static ArrayList validateEGLNameWithoutReservedValidation(String name, String nameType, Object targetObject) {

		ArrayList eglMessages = new ArrayList();

		//If no name, return 
		if (name == null || name.equals("")) { //$NON-NLS-1$
			return eglMessages;
		}

		//default the type to a generic part
		if (nameType == null)
			nameType = PART;

		//Check if name starts with "EZE"
		if (startsWithEZE(name))
			eglMessages.add(
				EGLMessage.createEGLValidationErrorMessage(EGLMessage.EGLMESSAGE_EZE_NOT_ALLOWED, targetObject, new String[] { name }));

		//Check if name length is valid
		if (name.length() > ((Integer) partNameLengths.get(nameType)).intValue())
			eglMessages.add(
				EGLMessage.createEGLValidationErrorMessage(EGLMessage.EGLMESSAGE_INVALID_NAME_LENGTH, targetObject, new String[] { name,  Integer.toString(name.length()), ((Integer) partNameLengths.get(nameType)).toString()}));

		//Check if each character is valid
		if (!validateCharacters(name, nameType))
			eglMessages.add(
				EGLMessage.createEGLValidationErrorMessage(
					EGLMessage.EGLMESSAGE_INVALID_CHARACTER_IN_NAME,
					targetObject,
					new String[] { name }));

		return eglMessages;
	}
}
