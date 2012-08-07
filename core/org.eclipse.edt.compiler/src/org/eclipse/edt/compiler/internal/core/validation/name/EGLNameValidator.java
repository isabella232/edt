/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.validation.name;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import org.eclipse.edt.compiler.core.EGLKeywordHandler;
import org.eclipse.edt.compiler.core.EGLSQLKeywordHandler;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.DefaultProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;



/**
 * @author Jack Frink
 */

public class EGLNameValidator {
	public static String[] keywords = EGLKeywordHandler.getKeywordNames();
	
	public static Set windowsReservedFileNames = new HashSet();
	static {
		windowsReservedFileNames.add("aux");
		windowsReservedFileNames.add("clock$");
		windowsReservedFileNames.add("com1");
		windowsReservedFileNames.add("com2");
		windowsReservedFileNames.add("com3");
		windowsReservedFileNames.add("com4");
		windowsReservedFileNames.add("com5");
		windowsReservedFileNames.add("com6");
		windowsReservedFileNames.add("com7");
		windowsReservedFileNames.add("com8");
		windowsReservedFileNames.add("com9");
		windowsReservedFileNames.add("com9");
		windowsReservedFileNames.add("com9");
		windowsReservedFileNames.add("con");
		windowsReservedFileNames.add("lpt1");
		windowsReservedFileNames.add("lpt2");
		windowsReservedFileNames.add("lpt3");
		windowsReservedFileNames.add("lpt4");
		windowsReservedFileNames.add("lpt5");
		windowsReservedFileNames.add("lpt6");
		windowsReservedFileNames.add("lpt7");
		windowsReservedFileNames.add("lpt8");
		windowsReservedFileNames.add("lpt9");
		windowsReservedFileNames.add("nul");
		windowsReservedFileNames.add("prn");
	}

	final public static int DEFAULT = 0;
	final public static int PART = 1;
	final public static int PROGRAM = 2;
	final public static int PROGRAM_EXTERNAL_NAME = 3;
	final public static int FUNCTION = 4;
	final public static int RECORD_FILE_NAME = 5;
	final public static int RECORD = 6;
	final public static int STRUCTURE = 7;
	final public static int DATATABLE = 8;
	final public static int TABLE_EXTERNAL_NAME = 9;
	final public static int DATAITEM = 10;
	final public static int BUILD_DESCRIPTOR = 11;
	final public static int LINKAGE_OPTIONS = 12;
	final public static int RESOURCE_ASSOCIATIONS = 13;
	final public static int BIND_CONTROL = 14;
	final public static int LINK_EDIT = 15;
	final public static int TYPEDEF = 16;
	final public static int TABLE_GENERATION = 17;
	final public static int SYMBOLIC_PARAMETER = 18;
	final public static int RESULT_SET_ID = 19;
	//new
	final public static int FORMGROUP = 20;
	final public static int FORM = 21;
	final public static int LIBRARY = 22;
	final public static int PAGEHANDLER = 23;
	final public static int RECORD_REFERENCE = 24;
	final public static int FUNCTION_REFERENCE = 25;
	final public static int DATAITEM_REFERENCE = 26;
	final public static int PROGRAM_REFERENCE = 27;
	final public static int PROPERTY_ALIAS = 28;
	final public static int PROPERTY_MSGTABLEPREFIX = 29;
	final public static int PACKAGE = 30;
	final public static int FILENAME = 31;
	final public static int PART_REFERENCE = 32;
	final public static int IDENTIFIER = 33;
	final public static int KEYITEM = 34;
	final public static int TABLENAME = 35;
	final public static int TABLENAMEVARIABLES = 36;
	final public static int LENGTHITEM = 37;
	final public static int HANDLER = 38;
	final public static int INTO_CLAUSE_DATAITEM_REFERENCE = 39;
	final public static int DELEGATE = 40;
	final public static int INTERNALFIELD = 41;
	
	final public static int substring = 0;
	final public static int subscript = 1;	

	final public static int PART_LENGTH = 128;
	final public static int PROGRAM_LENGTH = PART_LENGTH;
	final public static int DELEGATE_LENGTH = PART_LENGTH;
	final public static int PROGRAM_EXTERNAL_NAME_LENGTH = PART_LENGTH;
	final public static int RECORD_FILE_NAME_LENGTH = 8;
	final public static int FUNCTION_LENGTH = PART_LENGTH;
	final public static int RECORD_LENGTH = PART_LENGTH;
	final public static int STRUCTURE_LENGTH = PART_LENGTH;
	final public static int DATATABLE_LENGTH = PART_LENGTH;
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
	final public static int RESULT_SET_ID_LENGTH = PART_LENGTH;
	final public static int PROJECT_ID_LENGTH = 44;
	final public static int KEYITEM_LENGTH = PART_LENGTH;
	final public static int TABLENAME_LENGTH = PART_LENGTH;
	final public static int TABLENAMEVARIABLES_LENGTH = PART_LENGTH;
	final public static int LENGTHITEM_LENGTH = PART_LENGTH;
	//new
	final public static int FORMGROUP_LENGTH = PART_LENGTH;
	final public static int FORM_LENGTH = PART_LENGTH;
	final public static int LIBRARY_LENGTH = PART_LENGTH;
	final public static int PAGEHANDLER_LENGTH = PART_LENGTH;
	final public static int RECORD_REFERENCE_LENGTH = PART_LENGTH;
	final public static int FUNCTION_REFERENCE_LENGTH = PART_LENGTH;
	final public static int DATAITEM_REFERENCE_LENGTH = PART_LENGTH;
	final public static int PROGRAM_REFERENCE_LENGTH = PART_LENGTH;
	final public static int PROPERTY_ALIAS_LENGTH = PART_LENGTH;
	final public static int PROPERTY_MSGTABLEPREFIX_LENGTH = 125;
	final public static int PACKAGE_LENGTH = PART_LENGTH;
	final public static int FILENAME_LENGTH = PART_LENGTH;
	final public static int PART_REFERENCE_LENGTH = PART_LENGTH;
	final public static int IDENTIFIER_LENGTH = PART_LENGTH;
	final public static int HANDLER_LENGTH = PART_LENGTH;
	final public static int INTO_CLAUSE_ITEM_LENGTH = PART_LENGTH;
	final public static int INTERNALFIELD_LENGTH = PART_LENGTH;

	private static Hashtable partNameLengths = new Hashtable();

	static {
		partNameLengths.put(new Integer(PART), new Integer(PART_LENGTH));
		partNameLengths.put(new Integer(PROGRAM), new Integer(PROGRAM_LENGTH));
		partNameLengths.put(new Integer(DELEGATE), new Integer(DELEGATE_LENGTH));
		partNameLengths.put(new Integer(PROGRAM_EXTERNAL_NAME), new Integer(PROGRAM_EXTERNAL_NAME_LENGTH));
		partNameLengths.put(new Integer(FUNCTION), new Integer(FUNCTION_LENGTH));
		partNameLengths.put(new Integer(RECORD), new Integer(RECORD_LENGTH));
		partNameLengths.put(new Integer(RECORD_FILE_NAME), new Integer(RECORD_FILE_NAME_LENGTH));
		partNameLengths.put(new Integer(STRUCTURE), new Integer(STRUCTURE_LENGTH));
		partNameLengths.put(new Integer(DATATABLE), new Integer(DATATABLE_LENGTH));
		partNameLengths.put(new Integer(TABLE_EXTERNAL_NAME), new Integer(TABLE_EXTERNAL_NAME_LENGTH));
		partNameLengths.put(new Integer(DATAITEM), new Integer(ITEM_LENGTH));
		partNameLengths.put(new Integer(BUILD_DESCRIPTOR), new Integer(BUILD_DESCRIPTOR_LENGTH));
		partNameLengths.put(new Integer(LINKAGE_OPTIONS), new Integer(LINKAGE_OPTIONS_LENGTH));
		partNameLengths.put(new Integer(RESOURCE_ASSOCIATIONS), new Integer(RESOURCE_ASSOCIATIONS_LENGTH));
		partNameLengths.put(new Integer(BIND_CONTROL), new Integer(BIND_CONTROL_LENGTH));
		partNameLengths.put(new Integer(LINK_EDIT), new Integer(LINK_EDIT_LENGTH));
		partNameLengths.put(new Integer(TYPEDEF), new Integer(TYPEDEF_LENGTH));
		partNameLengths.put(new Integer(TABLE_GENERATION), new Integer(TABLE_GENERATION_LENGTH));
		partNameLengths.put(new Integer(SYMBOLIC_PARAMETER), new Integer(SYMBOLIC_PARAMETER_LENGTH));
		partNameLengths.put(new Integer(RESULT_SET_ID), new Integer(RESULT_SET_ID_LENGTH));
		//new
		partNameLengths.put(new Integer(FORMGROUP), new Integer(FORMGROUP_LENGTH));
		partNameLengths.put(new Integer(FORM), new Integer(FORM_LENGTH));
		partNameLengths.put(new Integer(LIBRARY), new Integer(LIBRARY_LENGTH));
		partNameLengths.put(new Integer(PAGEHANDLER), new Integer(PAGEHANDLER_LENGTH));
		partNameLengths.put(new Integer(RECORD_REFERENCE), new Integer(RECORD_REFERENCE_LENGTH));
		partNameLengths.put(new Integer(FUNCTION_REFERENCE), new Integer(FUNCTION_REFERENCE_LENGTH));
		partNameLengths.put(new Integer(DATAITEM_REFERENCE), new Integer(DATAITEM_REFERENCE_LENGTH));
		partNameLengths.put(new Integer(PROGRAM_REFERENCE), new Integer(PROGRAM_REFERENCE_LENGTH));
		partNameLengths.put(new Integer(PROPERTY_ALIAS), new Integer(PROPERTY_ALIAS_LENGTH));
		partNameLengths.put(new Integer(PROPERTY_MSGTABLEPREFIX), new Integer(PROPERTY_MSGTABLEPREFIX_LENGTH));
		partNameLengths.put(new Integer(PACKAGE), new Integer(PACKAGE_LENGTH));
		partNameLengths.put(new Integer(FILENAME), new Integer(FILENAME_LENGTH));
		partNameLengths.put(new Integer(PART_REFERENCE), new Integer(PART_REFERENCE_LENGTH));
		partNameLengths.put(new Integer(IDENTIFIER), new Integer(IDENTIFIER_LENGTH));
		partNameLengths.put(new Integer(KEYITEM), new Integer(KEYITEM_LENGTH));
		partNameLengths.put(new Integer(TABLENAME), new Integer(TABLENAME_LENGTH));
		partNameLengths.put(new Integer(TABLENAMEVARIABLES), new Integer(TABLENAMEVARIABLES_LENGTH));
		partNameLengths.put(new Integer(LENGTHITEM), new Integer(LENGTHITEM_LENGTH));
		partNameLengths.put(new Integer(HANDLER), new Integer(HANDLER_LENGTH));
		partNameLengths.put(new Integer(INTO_CLAUSE_DATAITEM_REFERENCE), new Integer(INTO_CLAUSE_ITEM_LENGTH));
		partNameLengths.put(new Integer(INTERNALFIELD), new Integer(INTERNALFIELD_LENGTH));
	}
	
	private static class OffsetProblemRequestor extends DefaultProblemRequestor {
		int offset;
		private IProblemRequestor problemRequestor;
		
		public OffsetProblemRequestor(int offset, IProblemRequestor problemRequestor) {
			this.offset = offset;
			this.problemRequestor = problemRequestor;
		}
		
		@Override
		public void acceptProblem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts, ResourceBundle bundle) {
	 		if (severity == IMarker.SEVERITY_ERROR) {
	 			setHasError(true);
	 		}
			problemRequestor.acceptProblem(startOffset+offset, endOffset+offset, severity, problemKind, inserts, bundle);
		}
		public boolean shouldReportProblem(int problemKind) {
			return problemRequestor.shouldReportProblem(problemKind);
		}
	}
	
	private static class NodeProblemRequestor extends DefaultProblemRequestor {
		private Node node;
		private IProblemRequestor problemRequestor;
		
		public NodeProblemRequestor(Node node, IProblemRequestor problemRequestor) {
			this.node = node;
			this.problemRequestor = problemRequestor;
		}
		
		@Override
		public void acceptProblem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts, ResourceBundle bundle) {
	 		if (severity == IMarker.SEVERITY_ERROR) {
	 			setHasError(true);
	 		}
			problemRequestor.acceptProblem(node, problemKind, severity, inserts, bundle);
		}
		
		public boolean shouldReportProblem(int problemKind) {
			return problemRequestor.shouldReportProblem(problemKind);
		}

	}

	/**
	 * A method that will be common to all the validation classes to allow for
	 *  the various stages of validation to be called in a similar way.
	 *  This is where whatever is being validated is passed in.
	 * @param EGLAbstractName the EGLAbstractName that is being validated
	 * @param nameType the type of item being validated. Used to check that the
	 *  	  length of its name is not too long. If unsure, use EGLNameValidator.DEFAULT.
	 */
	public static void validate(Name eglName, int nameType, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {

		validate(eglName.getCanonicalName(), nameType, new OffsetProblemRequestor(eglName.getOffset(), problemRequestor), compilerOptions);
	}
	
	public static void validate(String input, int nameType, IProblemRequestor problemRequestor, Node nodeForErrors, ICompilerOptions compilerOptions) {
		validate(input, nameType, new NodeProblemRequestor(nodeForErrors, problemRequestor), compilerOptions);		
	}

	/**
	 * Call specific validation depending on the nameType of the input.
	 * @param String input - the String being validated
	 * @param int nameType - the nameType of the String, chosen from the list
	 * 						 of constants in EGLNameValidator. If unsure which
	 * 						 to use, use EGLNameValidator.DEFAULT.
	 */
	public static void validate(String input, int nameType, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		String type = null; //hold the String version of the nameType, if needed
		EGLNameParser nameParser = new EGLNameParser(input, true, nameType == RECORD_FILE_NAME, problemRequestor, compilerOptions); // the name parser
		
		// for testing!
//		nameParser.getNamesAndSubscripts().outputTrees();		
//		nameParser.printErrors();

 		Iterator simpleNames = null;
 		EGLNameToken nextName = null;
 		int start = 0;
//		String name = null; //each element in simpleNames will be stored here

		if (nameType == DEFAULT) {
			nameType = PART; //default nameType
		}

		switch (nameType) {
			
			case FUNCTION_REFERENCE :
				//no subscripts allowed
				//no substrings allowed 
				//"this", "syslib", "strlib", "sysvar" and "mathlib" keywords are allowed  

	 			validateNameTokenSequence(nameParser.getNames(), input, problemRequestor);
	 			start = 0;
	 
				//now validate each name
 				for (int ii = start; ii < nameParser.getNames().size(); ii++) {
 					nextName = ((EGLNameToken)nameParser.getNames().get(ii));
 					if (nextName.getType() == EGLNameToken.IDENTIFIER) { 
					//	callValidateNoWhitespace(name, input, problemRequestor);
						callStartsWithEZE(nextName, input, problemRequestor);
						callValidateCharacters(nextName, input, problemRequestor, compilerOptions);
						callValidateLength(nextName, input, nameType, problemRequestor);
						callIsKeyword(nextName, input, problemRequestor);
					}
				}

//				restrictSubscripts(nameParser, input, IProblemRequestor.FUNCTION_REFERENCE_CANNOT_BE_SUBSCRIPTED, problemRequestor);
				restrictSubstrings(nameParser, input, problemRequestor);

				break;

			case RECORD_REFERENCE :
				//"this", "syslib", "sysvar" and "mathlib" keywords are allowed  
	 			validateNameTokenSequence(nameParser.getNames(), input, problemRequestor);
	 			start = 0;
	 
				//now validate each name
 				for (int ii = start; ii < nameParser.getNames().size(); ii++) {
 					nextName = ((EGLNameToken)nameParser.getNames().get(ii));
					if (nextName.getType() == EGLNameToken.IDENTIFIER) {
					//	callValidateNoWhitespace(name, input, problemRequestor);
						callStartsWithEZE(nextName, input, problemRequestor);
						callValidateCharacters(nextName, input, problemRequestor, compilerOptions);
						callValidateLength(nextName, input, nameType, problemRequestor);
						callIsKeyword(nextName, input, problemRequestor);
					}
	 			}
				
				validateSubscripts(input, nameParser.getSubscripts(), nameType, problemRequestor, false);
				
				restrictSubstrings(nameParser, input, problemRequestor);
				break;
				
			case INTO_CLAUSE_DATAITEM_REFERENCE :
				break;

			case DATAITEM_REFERENCE :
				// this commented out code was the code when dataitem references were parsed
				// as strings instead of calling EGLDataAccessUtility
//				//"this", "syslib", "sysvar" and "mathlib" keywords are allowed
//				validateNameTokenSequence(nameParser.getNames(), input, problemRequestor);
//	 			start = 0;
// 				start = isQualifiedSystemReference( nameParser.getNames(), errors );
//	 
//				//now validate each name
// 				for (int ii = start; ii < nameParser.getNames().size(); ii++) {
// 					nextName = ((EGLNameToken)nameParser.getNames().get(ii));
//					if (nextName.getType() == EGLNameToken.IDENTIFIER) {
//						callStartsWithEZE(nextName, input, problemRequestor);
//						callValidateCharacters(nextName, input, problemRequestor);
//						callValidateLength(nextName, input, nameType, problemRequestor);
//						callIsKeywordAllowYESorNO(nextName, input, problemRequestor);
//					}
//				}
//				validateSubscripts(input, nameParser.getSubscripts(), nameType, errors, false);
//				validateSubstring(input, nameParser.getSubstrings(), nameType, errors, false);
//				break;
				
				//"this" or any system libary name keyword is allowed
				// system libraries are no longer keywords
				break;
				
			
				
			case PART_REFERENCE :
				// "this", "syslib", "sysvar" and "mathlib" keywords are allowed
				// no subscripts allowed
				validateNameTokenSequence(nameParser.getNames(), input, problemRequestor);
	 			start = 0;
	 
				//now validate each name
 				for (int ii = start; ii < nameParser.getNames().size(); ii++) {
 					nextName = ((EGLNameToken)nameParser.getNames().get(ii));
					if (nextName.getType() == EGLNameToken.IDENTIFIER) {
					//	callValidateNoWhitespace(name, input, problemRequestor);
						callStartsWithEZE(nextName, input, problemRequestor);
						callValidateCharacters(nextName, input, problemRequestor, compilerOptions);
						callValidateLength(nextName, input, nameType, problemRequestor);
						callIsKeyword(nextName, input, problemRequestor);
					}
				}

				restrictSubscripts(nameParser, input, IProblemRequestor.PARTREFERENCE_CANNOT_BE_SUBSCRIPTED, problemRequestor);
				restrictSubstrings(nameParser, input, problemRequestor);
				break;
				
			case TYPEDEF :
				//	no subscripts or substrings allowed
				// can be qualified with a package name. 
				validateNameTokenSequence(nameParser.getNames(), input, problemRequestor);
				// now validate each name
				simpleNames = nameParser.getNames().iterator();
				while (simpleNames.hasNext()) {
					nextName = (EGLNameToken)(simpleNames.next());
					if (nextName.getType() == EGLNameToken.IDENTIFIER) {
					//	callValidateNoWhitespace(name, input, problemRequestor);
						callIsKeyword(nextName, input, problemRequestor);
						callStartsWithEZE(nextName, input, problemRequestor);
						callValidateCharacters(nextName, input, problemRequestor, compilerOptions);
						callValidateLength(nextName, input, nameType, problemRequestor);
					}
				}				
				
				restrictSubscripts(nameParser, input, IProblemRequestor.TYPEDEF_CANNOT_BE_SUBSCRIPTED, problemRequestor);
				restrictSubstrings(nameParser, input, problemRequestor);

				break;
				
			case KEYITEM :
				//	no subscripts or substrings allowed	
				validateNameTokenSequence(nameParser.getNames(), input, problemRequestor);
				// now validate each name
				simpleNames = nameParser.getNames().iterator();
				while (simpleNames.hasNext()) {
					nextName = (EGLNameToken)(simpleNames.next());
					if (nextName.getType() == EGLNameToken.IDENTIFIER) {
					//	callValidateNoWhitespace(name, input, problemRequestor);
						callIsKeyword(nextName, input, problemRequestor);
						callStartsWithEZE(nextName, input, problemRequestor);
						callValidateCharacters(nextName, input, problemRequestor, compilerOptions);
						callValidateLength(nextName, input, nameType, problemRequestor);
					}
				}

				restrictSubscripts(nameParser, input, IProblemRequestor.KEYITEM_CANNOT_BE_SUBSCRIPTED , problemRequestor);
				restrictSubstrings(nameParser, input, problemRequestor);	
				break;

			case LENGTHITEM :
				//	no subscripts or substrings allowed	
				validateNameTokenSequence(nameParser.getNames(), input, problemRequestor);
				// now validate each name
				simpleNames = nameParser.getNames().iterator();
				while (simpleNames.hasNext()) {
					nextName = (EGLNameToken)(simpleNames.next());
					if (nextName.getType() == EGLNameToken.IDENTIFIER) {
					//	callValidateNoWhitespace(name, input, problemRequestor);
						callIsKeyword(nextName, input, problemRequestor);
						callStartsWithEZE(nextName, input, problemRequestor);
						callValidateCharacters(nextName, input, problemRequestor, compilerOptions);
						callValidateLength(nextName, input, nameType, problemRequestor);
					}
				}

				restrictSubscripts(nameParser, input, IProblemRequestor.LENGTHITEM_CANNOT_BE_SUBSCRIPTED, problemRequestor);
				restrictSubstrings(nameParser, input, problemRequestor);
				break;
						
			case TABLENAME :
				simpleNames = nameParser.getNames().iterator();
				while (simpleNames.hasNext()) {
					nextName = (EGLNameToken)(simpleNames.next());
					callIsSQLClauseKeyword(nextName,input,problemRequestor);
				}
				validateSubscripts(input, nameParser.getSubscripts(), nameType, problemRequestor, true);
				restrictSubstrings(nameParser, input, problemRequestor);
				break;		


			case TABLENAMEVARIABLES :
				//"this", "syslib", "sysvar" and "mathlib" keywords are allowed
				validateNameTokenSequence(nameParser.getNames(), input, problemRequestor);
	 			start = 0;
	 
				//now validate each name
 				for (int ii = start; ii < nameParser.getNames().size(); ii++) {
 					nextName = ((EGLNameToken)nameParser.getNames().get(ii));
					if (nextName.getType() == EGLNameToken.IDENTIFIER) {
					//	callValidateNoWhitespace(name, input, problemRequestor);
						callStartsWithEZE(nextName, input, problemRequestor);
						callValidateCharacters(nextName, input, problemRequestor, compilerOptions);
						callValidateLength(nextName, input, nameType, problemRequestor);
						callIsKeyword(nextName, input, problemRequestor);
						callIsSQLClauseKeyword(nextName, input, problemRequestor);
					}
				}
				validateSubscripts(input, nameParser.getSubscripts(), nameType, problemRequestor, true);
				validateSubstring(input, nameParser.getSubstrings(), nameType, problemRequestor, true);
				break;				
				
			case PROPERTY_ALIAS :
				//no subscripts or substrings allowed
				//cannot be qualified
				//can contain quoted reserved words
				// cannot contain any blanks because it is part of a part name
				
				if (input.indexOf(' ')!= -1) {
					// can't have blanks
					problemRequestor.acceptProblem(
						0,
						input.length(),
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.WHITESPACE_NOT_ALLOWED,
						new String[] { input });
				}
				else {	
					validateNameTokenSequence(nameParser.getNames(), input, problemRequestor);
					
					if (nameParser.getNames().size() > 1) {
						problemRequestor.acceptProblem(
							0,
							input.length(),
							IMarker.SEVERITY_ERROR,
							IProblemRequestor.ALIAS_CANNOT_BE_QUALIFIED,
							new String[] { input });
					}
					else {
						
						// now validate each name
						simpleNames = nameParser.getNames().iterator();
						while (simpleNames.hasNext()) {
							nextName = (EGLNameToken)(simpleNames.next());
							if (nextName.getType() == EGLNameToken.IDENTIFIER) {
							//	callValidateNoWhitespace(name, input, problemRequestor);
								callValidateCharacters(nextName, input, problemRequestor, compilerOptions);
								callValidateLength(nextName, input, nameType, problemRequestor);
							}
						}
					}
				}

				restrictSubscripts(nameParser, input, IProblemRequestor.ALIAS_CANNOT_BE_SUBSCRIPTED, problemRequestor);
				restrictSubstrings(nameParser, input, problemRequestor);
				break;
				
			case PROPERTY_MSGTABLEPREFIX :
				//no subscripts or substrings allowed
				//cannot be qualified
				//can contain quoted reserved words
				// can be a reserved word because it is really only a partial name
				// cannot contain any blanks because it is part of a part name
				
				if (input.indexOf(' ')!= -1) {
					// can't have blanks
					problemRequestor.acceptProblem(
						0,
						input.length(),
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.WHITESPACE_NOT_ALLOWED,
						new String[] { input });
				}
				else {
	
					validateNameTokenSequence(nameParser.getNames(), input, problemRequestor);
					
//					if (nameParser.getNames().size() > 1) {
//						errors.add(
//							EGLMessage.createEGLValidationErrorMessage(
//								EGLBasePlugin.EGL_VALIDATION_RESOURCE_BUNDLE,
//								IProblemRequestor.MSGTABLEPREFIX_CANNOT_BE_QUALIFIED,
//								new String[] { input },
//								0,
//								input.length()));
//					}
					
					// now validate each name
					simpleNames = nameParser.getNames().iterator();
					while (simpleNames.hasNext()) {
						nextName = (EGLNameToken)(simpleNames.next());
						if (nextName.getType() == EGLNameToken.IDENTIFIER) {
						//	callValidateNoWhitespace(name, input, problemRequestor);
							callStartsWithEZE(nextName, input, problemRequestor);
							callValidateCharacters(nextName, input, problemRequestor, compilerOptions);
							callValidateLength(nextName, input, nameType, problemRequestor);
						}
					}	
				}

				restrictSubscripts(nameParser, input, IProblemRequestor.MSGTABLEPREFIX_CANNOT_BE_SUBSCRIPTED, problemRequestor);
				restrictSubstrings(nameParser, input, problemRequestor);			
				
				break;

			case INTERNALFIELD :
				
				// now validate each name
				simpleNames = nameParser.getNames().iterator();
				while (simpleNames.hasNext()) {
					nextName = (EGLNameToken)(simpleNames.next());
					if (nextName.getType() == EGLNameToken.IDENTIFIER) {
					//	callValidateNoWhitespace(name, input, problemRequestor);
						callIsKeyword(nextName, input, problemRequestor);
						callStartsWithEZE(nextName, input, problemRequestor);
						callValidateCharacters(nextName, input, problemRequestor, compilerOptions);
						callValidateLength(nextName, input, nameType, problemRequestor);
					}
				}					
				
				restrictSubstrings(nameParser, input, problemRequestor);

				break;
				
			case FUNCTION :
			case RECORD :
			case DATAITEM :
				//no subscripts and no qualifiers
				if (nameType == FUNCTION) {
					type = IEGLConstants.KEYWORD_FUNCTION;
				} else if (nameType == RECORD) {
					type = IEGLConstants.KEYWORD_RECORD;
				} else if (nameType == DATAITEM) {
					type = IEGLConstants.KEYWORD_DATAITEM;
				}

				validateNameTokenSequence(nameParser.getNames(), input, problemRequestor);
				
				if (nameParser.getNames().size() > 1) {
					problemRequestor.acceptProblem(
						0,
						input.length(),
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.TYPE_CANNOT_BE_QUALIFIED,
						new String[] { type, input });
				}
				
				// now validate each name
				simpleNames = nameParser.getNames().iterator();
				while (simpleNames.hasNext()) {
					nextName = (EGLNameToken)(simpleNames.next());
					if (nextName.getType() == EGLNameToken.IDENTIFIER) {
					//	callValidateNoWhitespace(name, input, problemRequestor);
						callIsKeyword(nextName, input, problemRequestor);
						callIsWindowsReservedFileName(nextName, input, problemRequestor);
						callStartsWithEZE(nextName, input, problemRequestor);
						callValidateCharacters(nextName, input, problemRequestor, compilerOptions);
						callValidateLength(nextName, input, nameType, problemRequestor);
					}
				}	
				
				// do here because of two inserts
				if ( nameParser.getSubscripts().size() >0) { 
					problemRequestor.acceptProblem(
						((EGLNameToken)(nameParser.getFirstSubscript().get(0))).getOffset(),
						input.length(),
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.TYPE_CANNOT_BE_SUBSCRIPTED,
						new String[] { type, input });
				}
				
				restrictSubstrings(nameParser, input, problemRequestor);

				break;

			case RECORD_FILE_NAME :
				//use strict validation of characters
				//cannot be subscripted or substringed
				validateNameTokenSequence(nameParser.getNames(), input, problemRequestor);
				
				if (nameParser.getNames().size() > 1) {
					problemRequestor.acceptProblem(
						0,
						input.length(),
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.PART_CANNOT_BE_QUALIFIED,
						new String[] { input });
				}
				else {
					// now validate each name
					simpleNames = nameParser.getNames().iterator();
					while (simpleNames.hasNext()) {
						nextName = (EGLNameToken)(simpleNames.next());
						if (nextName.getType() == EGLNameToken.IDENTIFIER) {
						//	callValidateNoWhitespace(name, input, problemRequestor);
						//	callIsKeyword(nextName, input, problemRequestor);
							callStartsWithEZE(nextName, input, problemRequestor);
							callValidateFileNameCharacters(nextName, input, problemRequestor);
							callValidateLength(nextName, input, nameType, problemRequestor);
						}
					}
				}

				restrictSubscripts(nameParser, input, IProblemRequestor.RECORD_FILENAME_CANNOT_BE_SUBSCRIPTED, problemRequestor);
				restrictSubstrings(nameParser, input, problemRequestor);

				break;
				
			case PACKAGE :
				//package names must pass EGLConventions.validatePackageName(String),
				// and must not contain any EGL or SQL keywords as tokens
				// package names cannot begin or end with a dot, contain
				// consecutive dots or be null.

				validateNameTokenSequence(nameParser.getNames(), input, problemRequestor);

				// now validate each name
				simpleNames = nameParser.getNames().iterator();
				while (simpleNames.hasNext()) {
					nextName = (EGLNameToken)(simpleNames.next());
					if (nextName.getType() == EGLNameToken.IDENTIFIER) {
					    //Jack - check that it follows EGLConventions/ cannot
						//yet as code cannot be imported and is still being
						//implemented
						validatePackageName(nextName.getText(), input, problemRequestor);
						callValidatePkgNameCharacters(nextName, input, problemRequestor, compilerOptions);
						callIsKeyword(nextName, input, problemRequestor);
						callIsWindowsReservedFileName(nextName, input, problemRequestor);
						callStartsWithEZE(nextName, input, problemRequestor);
						callMildValidateCharacters(nextName, input, problemRequestor, compilerOptions);
						callValidateLength(nextName, input, nameType, problemRequestor);
					}
				}	
				
				validatePackageName(input, input, problemRequestor);

				restrictSubscripts(nameParser, input, IProblemRequestor.RECORD_FILENAME_CANNOT_BE_SUBSCRIPTED, problemRequestor);
				restrictSubstrings(nameParser, input, problemRequestor);
				break;

			case IDENTIFIER :
				//no subscripts or substrings allowed
				//cannot be qualified		
				validateNameTokenSequence(nameParser.getNames(), input, problemRequestor);
				
				if (nameParser.getNames().size() > 1) {
					problemRequestor.acceptProblem(
						0,
						input.length(),
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.IDENTIFIER_CANNOT_BE_QUALIFIED,
						new String[] { input });
				}
				
				// now validate each name
				simpleNames = nameParser.getNames().iterator();
				while (simpleNames.hasNext()) {
					nextName = (EGLNameToken)(simpleNames.next());
					if (nextName.getType() == EGLNameToken.IDENTIFIER) {
						callIsKeyword(nextName, input, problemRequestor);
						callStartsWithEZE(nextName, input, problemRequestor);
						callValidateCharacters(nextName, input, problemRequestor, compilerOptions);
						callValidateLength(nextName, input, nameType, problemRequestor);
					}
				}				

				restrictSubscripts(nameParser, input, IProblemRequestor.IDENTIFIER_CANNOT_BE_SUBSCRIPTED, problemRequestor);
				restrictSubstrings(nameParser, input, problemRequestor);

				break;
				
			case FILENAME :
				//a filename cannot be subscripted
				// it could be qualified because we could get asked to validate the name 
				// including an extension
				// cannot contain any blanks because it is part of a part name
				
				if (input.indexOf(' ')!= -1) {
					// can't have blanks
					problemRequestor.acceptProblem(
						0,
						input.length(),
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.WHITESPACE_NOT_ALLOWED,
						new String[] { input });
				}

				validateNameTokenSequence(nameParser.getNames(), input, problemRequestor);

				// now validate each name
				simpleNames = nameParser.getNames().iterator();
				while (simpleNames.hasNext()) {
					nextName = (EGLNameToken)(simpleNames.next());
					if (nextName.getType() == EGLNameToken.IDENTIFIER) {
					//	callValidateNoWhitespace(name, input, problemRequestor);
						callIsKeyword(nextName, input, problemRequestor);
						callIsWindowsReservedFileName(nextName, input, problemRequestor);
						callStartsWithEZE(nextName, input, problemRequestor);
						callValidateCharacters(nextName, input, problemRequestor, compilerOptions);
						callValidateLength(nextName, input, nameType, problemRequestor);
					}
				}				

				restrictSubscripts(nameParser, input, IProblemRequestor.PART_CANNOT_BE_SUBSCRIPTED, problemRequestor);
				restrictSubstrings(nameParser, input, problemRequestor);

				break;

				
			case PROGRAM :
			case PROGRAM_REFERENCE :
			case FORMGROUP:
			case PAGEHANDLER :
			case DATATABLE:
			case LIBRARY:
			case HANDLER:
			case DELEGATE:
				//a Part cannot be qualified or subscripted, just a simple name
				// These have the additional restriction of never allowing a dash
				// in the name whether VG compatibility is on or not.

				validateNameTokenSequence(nameParser.getNames(), input, problemRequestor);
				
				if (nameParser.getNames().size() > 1) {
					problemRequestor.acceptProblem(
						0,
						input.length(),
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.PART_CANNOT_BE_QUALIFIED,
						new String[] { input });
				}
				
				// now validate each name
				simpleNames = nameParser.getNames().iterator();
				while (simpleNames.hasNext()) {
					nextName = (EGLNameToken)(simpleNames.next());
					if (nextName.getType() == EGLNameToken.IDENTIFIER) {
					//	callValidateNoWhitespace(name, input, problemRequestor);
						callIsKeyword(nextName, input, problemRequestor);
						callStartsWithEZE(nextName, input, problemRequestor);
						callIsWindowsReservedFileName(nextName, input, problemRequestor);
						callValidateCharacters(nextName, input, problemRequestor, compilerOptions);
						callValidateLength(nextName, input, nameType, problemRequestor);
					}
				}				

				restrictSubscripts(nameParser, input, IProblemRequestor.PART_CANNOT_BE_SUBSCRIPTED, problemRequestor);
				restrictSubstrings(nameParser, input, problemRequestor);

				if (input.indexOf('-') > 0) { // name contains a dash
					problemRequestor.acceptProblem(
						input.indexOf('-'),
						input.length(),
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.PART_CANNOT_HAVE_DASH);
				}				
				break;
				
				
				
			case PART :
				//a Part cannot be qualified or subscripted, just a simple name

				validateNameTokenSequence(nameParser.getNames(), input, problemRequestor);
				
				if (nameParser.getNames().size() > 1) {
					problemRequestor.acceptProblem(
						0,
						input.length(),
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.PART_CANNOT_BE_QUALIFIED,
						new String[] { input });
				}
				
				// now validate each name
				simpleNames = nameParser.getNames().iterator();
				while (simpleNames.hasNext()) {
					nextName = (EGLNameToken)(simpleNames.next());
					if (nextName.getType() == EGLNameToken.IDENTIFIER) {
					//	callValidateNoWhitespace(name, input, problemRequestor);
						callIsKeyword(nextName, input, problemRequestor);
						callStartsWithEZE(nextName, input, problemRequestor);
						callValidateCharacters(nextName, input, problemRequestor, compilerOptions);
						callValidateLength(nextName, input, nameType, problemRequestor);
					}
				}				

				restrictSubscripts(nameParser, input, IProblemRequestor.PART_CANNOT_BE_SUBSCRIPTED, problemRequestor);
				restrictSubstrings(nameParser, input, problemRequestor);

				break;

			default :
				//else default checking is enabled
				validateNameTokenSequence(nameParser.getNames(), input, problemRequestor);

				// now validate each name
				simpleNames = nameParser.getNames().iterator();
				while (simpleNames.hasNext()) {
					nextName = (EGLNameToken)(simpleNames.next());
					if (nextName.getType() == EGLNameToken.IDENTIFIER) {
					//	callValidateNoWhitespace(name, input, problemRequestor);
						callIsKeyword(nextName, input, problemRequestor);
						callStartsWithEZE(nextName, input, problemRequestor);
						callValidateCharacters(nextName, input, problemRequestor, compilerOptions);
						callValidateLength(nextName, input, nameType, problemRequestor);
					}
				}

				validateSubscripts(input, nameParser.getSubscripts(), nameType, problemRequestor, false);
				validateSubstring(input, nameParser.getSubstrings(), nameType, problemRequestor, false);
				break;
		}
	}

	/**
	 * Validate that the tokens of a name are in the proper sequence and are appropriate
	 * for a name.
	 * @author dollar
	 *
	 * @param - ArrayList of EGLNameTokens from the EGLNameParser 
	 */
	private static void validateNameTokenSequence (ArrayList names, String input, IProblemRequestor problemRequestor) {
		EGLNameToken currentToken = null;
		int notDot = 0;
		int dot = 1;
		int prevTokenType = notDot;
		for (int ii=0; ii < names.size(); ii++) {
			currentToken = ((EGLNameToken)(names.get(ii)));
			switch (currentToken.getType()) {
				case EGLNameToken.DOT :  
					if (ii == 0 || //starts with a dot
						prevTokenType == dot || // dot followed by dot
						ii == names.size() -1 // ends with a dot 
						) {
						problemRequestor.acceptProblem(
							currentToken.getOffset(),
							currentToken.getOffset() + currentToken.getText().length(),
							IMarker.SEVERITY_ERROR,
							IProblemRequestor.INVALID_NAME_TOKEN_SEQUENCE,
							new String[] { currentToken.getText(), input });									
					}
					prevTokenType = dot;
					break;
				 
				case EGLNameToken.IDENTIFIER : 
					if (ii >0 &&
						prevTokenType != dot) {
						//error
						problemRequestor.acceptProblem(
								currentToken.getOffset(),
								currentToken.getOffset() + currentToken.getText().length(),
								IMarker.SEVERITY_ERROR,
								IProblemRequestor.INVALID_NAME_TOKEN_SEQUENCE,
								new String[] { currentToken.getText(), input });
					}
					prevTokenType = notDot;
					break;

				case EGLNameToken.REAL_NUMBER :
				case EGLNameToken.FLOAT_NUMBER :
				case EGLNameToken.QUOTED_STRING :
				case EGLNameToken.COMMA :
				case EGLNameToken.COLON :
				case EGLNameToken.INTEGER :
				case EGLNameToken.L_SQUARE :   // shouldn't ever happen
				case EGLNameToken.R_SQUARE :   // shouldn't ever happen
				case EGLNameToken.UNKNOWN_EGL :
					//error - invalid in a name period
					problemRequestor.acceptProblem(
						currentToken.getOffset(),
						currentToken.getOffset() + currentToken.getText().length(),
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.INVALID_NAME_TOKEN_SEQUENCE,
						new String[] { currentToken.getText(), input });
					prevTokenType = notDot;
					break;
 
				default: {
					// shouldn't ever happen
				}
			}
		
		}
	}
	/**
	 * Validate that the tokens of a name are in the proper sequence and are appropriate
	 * for a name.
	 * @author dollar
	 *
	 * @param - ArrayList of EGLNameTokens from the EGLNameParser 
	 */
	private static void validateSubNameTokenSequence (ArrayList names, String input, IProblemRequestor problemRequestor) {
		EGLNameToken currentToken = null;
		int notDot = 0;
		int dot = 1;
		int prevTokenType = notDot;
		for (int ii=0; ii < names.size(); ii++) {
			currentToken = ((EGLNameToken)(names.get(ii)));
			switch (currentToken.getType()) {
				case EGLNameToken.DOT :  
					if (ii == 0 || //starts with a dot
						prevTokenType == dot || // dot followed by dot
						ii == names.size() -1 // ends with a dot 
						) {
						problemRequestor.acceptProblem(
								currentToken.getOffset(),
								currentToken.getOffset() + currentToken.getText().length(),
								IMarker.SEVERITY_ERROR,
								IProblemRequestor.INVALID_NAME_TOKEN_SEQUENCE,
								new String[] { currentToken.getText(), input });
					}
					prevTokenType = dot;
					break;
				 
				case EGLNameToken.IDENTIFIER : 
					if (ii >0 &&
						prevTokenType != dot) {
						//error
						problemRequestor.acceptProblem(
								currentToken.getOffset(),
								currentToken.getOffset() + currentToken.getText().length(),
								IMarker.SEVERITY_ERROR,
								IProblemRequestor.INVALID_NAME_TOKEN_SEQUENCE,
								new String[] { currentToken.getText(), input });
					}
					prevTokenType = notDot;
					break;

				case EGLNameToken.REAL_NUMBER :
				case EGLNameToken.FLOAT_NUMBER :
				case EGLNameToken.QUOTED_STRING :
					//error - invalid in a name period
					problemRequestor.acceptProblem(
						currentToken.getOffset(),
						currentToken.getOffset() + currentToken.getText().length(),
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.INVALID_REAL_OR_FLOAT_IN_NAME,
						new String[] { currentToken.getText(), input });
					prevTokenType = notDot;
					break;
//				case EGLNameToken.COMMA :
//				case EGLNameToken.COLON :
//				case EGLNameToken.INTEGER :
//				case EGLNameToken.L_SQUARE :   // shouldn't ever happen
//				case EGLNameToken.R_SQUARE :   // shouldn't ever happen
//				case EGLNameToken.UNKNOWN_EGL :
				default: {	
					//error - invalid in a name period
					problemRequestor.acceptProblem(
						currentToken.getOffset(),
						currentToken.getOffset() + currentToken.getText().length(),
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.INVALID_SUBSCRIPT_OR_SUBSTRING,
						new String[] { currentToken.getText(), input });
					prevTokenType = notDot;
				}
			}		
		}
	}	
	/**
	 * Validate the given package name.
	 * Package names cannot be null, start or start with a dot or contain 
	 * consecutive dots.
	 * Note that the given name must be a non-empty package name (that is, attempting to
	 * validate the default package will return an error status.)
	 * Also it must not contain any characters or substrings that are not valid 
	 * on the file system on which workspace root is located.
	 * 
	 * Note that the validateNameTokenSequence method does lots of this checking
	 *@param String name the package name, or part of, that is being validated
	 *@param String input the entire package name
	 *@param ArrayList errors - holds all the errors produced by validating this
	 *							item
	 */
	//Jack = the check that it can't "contain substrings that are not valid on the filesystem" is not being run currently.
	private static void validatePackageName(String name, String input, IProblemRequestor problemRequestor) {

		//Jack - can't implement the below code as the source (EGLConventions.java) doesn't even
		// have the scannedIdentifer method implemented
		/*
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			StringTokenizer st = new StringTokenizer(name, new String(new char[] {fgDot}));
			boolean firstToken = true;
			while (st.hasMoreTokens()) {
				String typeName = st.nextToken();
				typeName = typeName.trim(); // grammar allows spaces
				char[] scannedID = scannedIdentifier(typeName); 
				if (scannedID == null) {
					//return new Status(IStatus.ERROR, EGLCore.PLUGIN_ID, -1, Util.bind("convention.illegalIdentifier", typeName), null); //$NON-NLS-1$
				}
				IStatus status = workspace.validateName(new String(scannedID), IResource.FOLDER);
				if (!status.isOK()) {
					//return status;
				}
				if (firstToken && scannedID.length > 0 && Character.isUpperCase(scannedID[0])) {
					//return new Status(IStatus.WARNING, EGLCore.PLUGIN_ID, -1, Util.bind("convention.package.uppercaseName"), null); //$NON-NLS-1$
				}
				firstToken = false;
			}
		*/
	}

	private static void validateSubscripts(String input, ArrayList subscripts, int nameType, IProblemRequestor problemRequestor, boolean isSQL) {
// Can't validate this at this point anymore.
//		if (nameType == RECORD_REFERENCE)
//			if (subscripts.size() > 1) {
//				errors.add(
//					EGLMessage.createEGLValidationErrorMessage(
//						EGLBasePlugin.EGL_VALIDATION_RESOURCE_BUNDLE,
// 						IProblemRequestor.TOO_MANY_SUBSCRIPTS_ON_RECORD,
//						new String[] { input },
//						((EGLNameToken)(subscripts.get(1))).getOffset(),
//						((EGLNameToken)(subscripts.get(1))).getOffset() +
//							((EGLNameToken)(subscripts.get(1))).getText().length()));
//			}
//		else if (subscripts.size() > 7) {
//			errors.add(
//				EGLMessage.createEGLValidationErrorMessage(
//					EGLBasePlugin.EGL_VALIDATION_RESOURCE_BUNDLE,
//					EGLValidationMessages.TOO_MANY_ITEMS_IN_SUBSCRIPT,
//					new String[] { input },
//					((EGLNameToken)(subscripts.get(7))).getOffset(),
//					((EGLNameToken)(subscripts.get(7))).getOffset() +
//						((EGLNameToken)(subscripts.get(7))).getText().length()));
//		}

		// now loop through each subscript, validating the parts of each subscript name 
		
		ArrayList currentSubscript = null;
		for (int ii=0; ii < subscripts.size(); ii++) {
			currentSubscript = ((ArrayList)(subscripts.get(ii)));	
			if (currentSubscript.size() == 1 &&
				 ( ((EGLNameToken)currentSubscript.get(0)).getType() == EGLNameToken.INTEGER ||  
				   ((EGLNameToken)currentSubscript.get(0)).getType() == EGLNameToken.QUOTED_STRING   ) ) {
					// single integer is OK 
					// single quoted string is a dynamic reference
				}
			else {
				validateSubNameTokenSequence ( currentSubscript, input, problemRequestor); 
				
//	 			int start = isQualifiedSystemReference( currentSubscript, problemRequestor );
//	 
//	 			if ( start >0 ) {
//	 				//must be a qualified system word
//	 				EGLSystemWordValidator.validateSystemName(getCanonicalName(currentSubscript), 
//							EGLStatementValidator.AS_SUBSCRIPT, 
//							((EGLNameToken)currentSubscript.get(0)).getOffset(), 
//							errors);
//	 			}
//				//now validate each name
// 			  	for (int jj = start; jj < currentSubscript.size(); jj++) {
// 					nextSubscriptName = ((EGLNameToken)currentSubscript.get(jj));				
//					if (nextSubscriptName.getType() == EGLNameToken.IDENTIFIER) {
//					//	callValidateNoWhitespace(name, input, problemRequestor);
//						callStartsWithEZE(nextSubscriptName, input, problemRequestor);
//						callValidateCharacters(nextSubscriptName, input, problemRequestor);
//						callValidateLength(nextSubscriptName, input, nameType, problemRequestor);
//						callIsKeyword(nextSubscriptName, input, problemRequestor);
//						if (isSQL)
//							callIsSQLClauseKeyword(nextSubscriptName, input, problemRequestor);
//					}
//				}
			}
		}
		return ;
	}


	private static void validateSubstring(String input, ArrayList substrings, int nameType, IProblemRequestor problemRequestor, boolean isSQL) {

		// now look at the from location and the to location
		
		ArrayList currentSubstring = null;
		for (int ii=0; ii < substrings.size(); ii++) {
			currentSubstring = ((ArrayList)(substrings.get(ii)));	
			if (currentSubstring.size() == 1 &&
				 ((EGLNameToken)currentSubstring.get(0)).getType() == EGLNameToken.INTEGER  ) {
					// single integer is OK 
				}
			else {
				validateSubNameTokenSequence ( currentSubstring, input, problemRequestor); 
				
//	 			int start = isQualifiedSystemReference( currentSubstring, errors );
//	 
//	 			if ( start >0 ) {
//	 				//must be a qualified system word
//	 				EGLSystemWordValidator.validateSystemName(getCanonicalName(currentSubstring), 
//							EGLStatementValidator.AS_SUBSCRIPT, 
//							((EGLNameToken)currentSubstring.get(0)).getOffset(), 
//							errors);
//	 			}
//				//now validate each name
// 			  	for (int jj = start; jj < currentSubstring.size(); jj++) {
// 			  		nextSubstinrgName = ((EGLNameToken)currentSubstring.get(jj));				
//					if (nextSubstinrgName.getType() == EGLNameToken.IDENTIFIER) {
//					//	callValidateNoWhitespace(name, input, problemRequestor);
//						callStartsWithEZE(nextSubstinrgName, input, problemRequestor);
//						callValidateCharacters(nextSubstinrgName, input, problemRequestor);
//						callValidateLength(nextSubstinrgName, input, nameType, problemRequestor);
//						callIsKeyword(nextSubstinrgName, input, problemRequestor);
//						if (isSQL)
//							callIsSQLClauseKeyword(nextSubstinrgName, input, problemRequestor);
//					}
//				}
			}
		}
		return ;
	}
	
	
	private static void callValidateCharacters(EGLNameToken name, String input, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if (validateCharacters(name.getText(), compilerOptions) == false) {
			problemRequestor.acceptProblem(
				name.getOffset(),
				name.getOffset() + name.getText().length(),
				IMarker.SEVERITY_ERROR,
				IProblemRequestor.INVALID_CHARACTER_IN_NAME,
				new String[] { name.getText() });					
		}
	}

	private static void callValidatePkgNameCharacters(EGLNameToken name, String input, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if (validatePkgNameCharacters(name.getText(), compilerOptions) == false) {
			problemRequestor.acceptProblem(
				name.getOffset(),
				name.getOffset() + name.getText().length(),
				IMarker.SEVERITY_ERROR,
				IProblemRequestor.INVALID_CHARACTER_IN_NAME,
				new String[] { name.getText() });					
		}
	}

	private static void callMildValidateCharacters(EGLNameToken name, String input, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if (mildValidateCharacters(name.getText(), compilerOptions) == false) {
			problemRequestor.acceptProblem(
				name.getOffset(),
				name.getOffset() + name.getText().length(),
				IMarker.SEVERITY_ERROR,
				IProblemRequestor.INVALID_CHARACTER_IN_NAME,
				new String[] { name.getText() });
		}
	}

	private static void callValidateFileNameCharacters(EGLNameToken name, String input, IProblemRequestor problemRequestor) {
		if (fileNameValidateCharacters(name.getText()) == false) {
			problemRequestor.acceptProblem(
				name.getOffset(),
				name.getOffset() + name.getText().length(),
				IMarker.SEVERITY_ERROR,
				IProblemRequestor.INVALID_CHARACTER_IN_NAME,
				new String[] { name.getText() });
		}
	}
	
	
	private static void callIsKeyword(EGLNameToken name, String input, IProblemRequestor problemRequestor) {
		if (isKeyword(name.getText())) {
			problemRequestor.acceptProblem(
				name.getOffset(),
				name.getOffset() + name.getText().length(),
				IMarker.SEVERITY_ERROR,
				IProblemRequestor.RESERVED_WORD_NOT_ALLOWED,
				new String[] { name.getText() });
		}
	}

	private static void callIsWindowsReservedFileName(EGLNameToken name, String input, IProblemRequestor problemRequestor) {
		if (isWindowsReservedFileName(name.getText())) {
			problemRequestor.acceptProblem(
				name.getOffset(),
				name.getOffset() + name.getText().length(),
				IMarker.SEVERITY_ERROR,
				IProblemRequestor.RESERVED_WORD_NOT_ALLOWED,
				new String[] { name.getText() });
		}
	}

	private static void callIsSQLClauseKeyword(EGLNameToken name, String input, IProblemRequestor problemRequestor) {
		if ( EGLSQLKeywordHandler.getSQLKeywordNamesToLowerCaseAsSet().contains(name.getText().toLowerCase(Locale.ENGLISH)) ) { 
			problemRequestor.acceptProblem(
				name.getOffset(),
				name.getOffset() + name.getText().length(),
				IMarker.SEVERITY_ERROR,
				IProblemRequestor.SQL_TABLE_NAME_LABEL_VARIABLE_DUPLICATES_CLAUSE,
				new String[] { name.getText(), EGLSQLKeywordHandler.getSQLClauseKeywordNamesCommaSeparatedString()});
		}
	}
	

	private static void callValidateLength(EGLNameToken name, String input, int nameType, IProblemRequestor problemRequestor) {
		if (name.getText().length() > ((Integer) partNameLengths.get(new Integer(nameType))).intValue()) {
			problemRequestor.acceptProblem(
				name.getOffset(),
				name.getOffset() + name.getText().length(),
				IMarker.SEVERITY_ERROR,
				IProblemRequestor.INVALID_NAME_LENGTH,
				new String[] { name.getText(), String.valueOf(name.getText().length()), String.valueOf(partNameLengths.get(new Integer(nameType)))});
		}
	}

	private static void callStartsWithEZE(EGLNameToken name, String input, IProblemRequestor problemRequestor) {
		if (startsWithEZE(name.getText())) {
			problemRequestor.acceptProblem(
				name.getOffset(),
				name.getOffset() + name.getText().length(),
				IMarker.SEVERITY_ERROR,
				IProblemRequestor.EZE_NOT_ALLOWED,
				new String[] { name.getText() });
		}
	}

	/**
	 * Method written by: Jon Shavor
	 * Make sure each character is a valid character
	 * First character must be a java letter
	 * all other characters must be a java letter or a digit
	 *
	 * if name is null return false
	 */
	public static boolean validateCharacters(String name, ICompilerOptions compilerOptions) {

		if (name == null)
			return false;

		if (name == "") //$NON-NLS-1$
			return true;

		if (!Character.isJavaIdentifierStart(name.charAt(0)))
		// '$' and '_' are already allowed by isJavaIdentifierStart 			
			return false;

		for (int i = 1; i < name.length(); i++) {
			if (!Character.isJavaIdentifierPart(name.charAt(i)))
			// '$' and '_' are already allowed by isJavaIdentifierPart 				
				return false;
		}

		return true;
	}

	public static boolean validatePkgNameCharacters(String name, ICompilerOptions compilerOptions) {

		if (name == null)
			return false;

		if (name == "") //$NON-NLS-1$
			return true;
		
		if (name.charAt(0) == '@' || 
			name.charAt(0) == '#' )
			return false;

		return true;
	}
	
	/**
	 * Method written by: Frieda Dollar
	 * Make sure each character is a valid character
	 * First character must be a java letter
	 * all other characters must be a java letter or a digit
	 * Underscore and $ are allowed but not the other vag characters.
	 *
	 * if name is null return false
	 */
	public static boolean mildValidateCharacters(String name, ICompilerOptions compilerOptions) {

		if (name == null)
			return false;

		if (name == "") //$NON-NLS-1$
			return true;

		if (!Character.isJavaIdentifierStart(name.charAt(0)))
			// '$' and '_' are already allowed by isJavaIdentifierStart 			
			return false;

		for (int i = 1; i < name.length(); i++) {
			if (!Character.isJavaIdentifierPart(name.charAt(i)))
				// '$' and '_' are already allowed by isJavaIdentifierPart 				
				return false;
		}

		return true;
	}
	/**
	 * Method written by: Jon Shavor
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
	 * Method written by: Frieda Dollar
	 * Make sure each character is a valid character
	 * First charaacter must be a letter, #, $, @
	 * all other characters must be a letter or a digit, #, $, @
	 * does not allow special characters like isJavaIdentifier does 
	 *
	 * if name is null return false
	 */
	public static boolean fileNameValidateCharacters(String name) {

		if (name == null)
			return false;

		if (name == "") //$NON-NLS-1$
			return true;

		if ( !Character.isLetter(name.charAt(0))
			 && !Character.toString(name.charAt(0)).equalsIgnoreCase("#")   
			 && !Character.toString(name.charAt(0)).equalsIgnoreCase("$") 
			 && !Character.toString(name.charAt(0)).equalsIgnoreCase("@") 
		  )
			return false;

		for (int i = 1; i < name.length(); i++) {
			if (!Character.isLetterOrDigit(name.charAt(i)) 
					 && !Character.toString(name.charAt(i)).equalsIgnoreCase("#")   
					 && !Character.toString(name.charAt(i)).equalsIgnoreCase("$") 
					 && !Character.toString(name.charAt(i)).equalsIgnoreCase("@") 
				  )
				return false;
		}

		return true;
	}
//	/**
//	 * Method written by: Jon Shavor
//	 * Make sure each character is a valid character
//	 * First charaacter must be a java letter
//	 * all other characters must be a java letter or a digit
//	 *
//	 * if name is null return false
//	 */
//	public static boolean validateCharacters(String name, int nameType) {
//
//		if (nameType == RECORD_FILE_NAME)
//			return strictValidateCharacters(name);
//		else
//			return validateCharacters(name);
//	}

	/**
	 * Method written by: Jon Shavor
	 * @return true if it is a keyword, else false
	 */
	public static boolean isKeyword(String name) {
		return EGLKeywordHandler.getKeywordHashSet().contains(name.toLowerCase(Locale.ENGLISH));
//		for (int i = 0; i < keywords.length; i++) {
//			if (keywords[i].equalsIgnoreCase(name))
//				return true;
//		}
//
//		return false;
	}
	
	public static boolean isWindowsReservedFileName(String name) {
		return windowsReservedFileNames.contains(name.toLowerCase(Locale.ENGLISH));
	}
 

	/**
	* Method written by: Jon Shavor
	*/
	public static boolean startsWithEZE(String name) {

		if (name.length() < 3)
			return false;

		return name.toUpperCase().startsWith("EZE"); //$NON-NLS-1$
	}

//	private static void callValidateNoWhitespace(String name, String input, IProblemRequestor problemRequestor) {
//		//Remove trailing whitespace and comments
//		name = removeTrailingCharacters(name);
//		if (name.indexOf(' ') == -1) {
//			return;
//		}
//		errors.add(
//			EGLMessage.createEGLValidationErrorMessage(
//				EGLBasePlugin.EGL_VALIDATION_RESOURCE_BUNDLE,
//				IProblemRequestor.WHITESPACE_NOT_ALLOWED,
//				new String[] { name },
//				getOffset(input, name),
//				getOffset(input, name) + name.length()));
//	}
//
//	/**
//	 * Method written by: Jon Shavor
//	 * Creation date: (10/22/2001 5:25:49 PM)
//	 * @return boolean
//	 * @param candidate java.lang.String
//	 */
//	public static boolean isJavaIdentifier(String candidate) {
//		// Cannot be one of the reserved words
//		for (int i = 0; i < reservedWords.length; i++) {
//			if (reservedWords[i].equals(candidate))
//				return false;
//		}
//
//		if (candidate == null || candidate.length() == 0 || !EGLCharInfo.isIdentifier1(candidate.charAt(0)))
//			return false;
//
//		for (int i = 1; i < candidate.length(); i++)
//			if (!EGLCharInfo.isIdentifier(candidate.charAt(i)))
//				return false;
//
//		return true;
//	}
//
//	/**
//	 * Method written by: Jon Shavor
//	 * Creation date: (10/22/2001 5:21:12 PM)
//	 * @return boolean
//	 * @param candidate java.lang.String
//	 */
//	public static boolean isJavaPackageName(String candidate) {
//		// Candidate must not be null or empty
//		if (candidate == null || candidate.length() == 0)
//			return false;
//
//		// Check each part to see if they are a valid Java identifier
//		StringTokenizer tokenizer = new StringTokenizer(candidate, ".", true); //$NON-NLS-1$
//		// The first token should be a valid Java Identifier
//		if (!isJavaIdentifier(tokenizer.nextToken()))
//			return false;
//
//		// If there is anything more, the next token consists of a "." and a valid 
//		// Java identifier
//		while (tokenizer.hasMoreTokens()) {
//			// We know this token is a ".", so just throw it away
//			tokenizer.nextToken();
//			if (!tokenizer.hasMoreTokens() || !isJavaIdentifier(tokenizer.nextToken()))
//				return false;
//		}
//
//		return true;
//	}
	
	private static void restrictSubscripts (EGLNameParser nameParser, String input, int problemKind, IProblemRequestor problemRequestor) {
		if ( nameParser.getSubscripts().size() > 0) {
			ArrayList firstSubscript = nameParser.getFirstSubscript();
			if(!firstSubscript.isEmpty()) {
				problemRequestor.acceptProblem(
					((EGLNameToken)(firstSubscript.get(0))).getOffset(),
					input.length(),
					IMarker.SEVERITY_ERROR,
					problemKind,
					new String[] { input });					
			}
		}
	}
	
	private static void restrictSubstrings (EGLNameParser nameParser, String input, IProblemRequestor problemRequestor) {
		if ( nameParser.getSubstrings().size() >0) { 
			problemRequestor.acceptProblem(
				((EGLNameToken)(nameParser.getFirstSubscript().get(0))).getOffset(),
				input.length(),
				IMarker.SEVERITY_ERROR,
				IProblemRequestor.REFERENCE_CANNOT_CONTAIN_SUBSTRING,
				new String[] { input });
		}
	}
		
}
