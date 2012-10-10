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
package org.eclipse.edt.compiler.internal.core.builder;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Element;


/**
 * @author Dave Murray
 */
public abstract class DefaultProblemRequestor implements IProblemRequestor {
	
	public static final String EGL_VALIDATION_RESOURCE_BUNDLE_NAME = "org.eclipse.edt.compiler.internal.core.builder.EGLValidationResources"; //$NON-NLS-1$
	public static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(EGL_VALIDATION_RESOURCE_BUNDLE_NAME, Locale.getDefault());
	boolean hasError;
	
	@Override
	public abstract void acceptProblem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts, ResourceBundle bundle);
	
	@Override
	public void acceptProblem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts) {
		acceptProblem(startOffset, endOffset, severity, problemKind, inserts, RESOURCE_BUNDLE);
	}
	
	@Override
	public void acceptProblem(Node astNode, int problemKind) {
		acceptProblem(astNode.getOffset(), astNode.getOffset() + astNode.getLength(), IMarker.SEVERITY_ERROR, problemKind, new String[0], RESOURCE_BUNDLE);
	}
	
	@Override
	public boolean shouldReportProblem(int problemKind) {
		return true;
	}
	
	@Override
	public void acceptProblem(Node astNode, int problemKind, int severity) {
		acceptProblem(astNode.getOffset(), astNode.getOffset() + astNode.getLength(), severity, problemKind, new String[0], RESOURCE_BUNDLE);
	}
	
	@Override
	public void acceptProblem(Node astNode, int problemKind, String[] inserts) {
		acceptProblem(astNode.getOffset(), astNode.getOffset() + astNode.getLength(), IMarker.SEVERITY_ERROR, problemKind, inserts, RESOURCE_BUNDLE);
	}
	
	@Override
	public void acceptProblem(Node astNode, int problemKind, int severity, String[] inserts) {
		acceptProblem(astNode.getOffset(), astNode.getOffset() + astNode.getLength(), severity, problemKind, inserts, RESOURCE_BUNDLE);
	}
	
	@Override
	public void acceptProblem(Node astNode, int problemKind, int severity, String[] inserts, ResourceBundle bundle) {
		acceptProblem(astNode.getOffset(), astNode.getOffset() + astNode.getLength(), severity, problemKind, inserts, bundle);
	}
	
	@Override
	public void acceptProblem(int startOffset, int endOffset, int problemKind, boolean isError, String[] inserts) {
		acceptProblem(startOffset, endOffset, problemKind, isError, inserts, RESOURCE_BUNDLE);
	}
	
	@Override
	public void acceptProblem(int startOffset, int endOffset, int problemKind, boolean isError, String[] inserts, ResourceBundle bundle) {
		if (isError) {
			acceptProblem(startOffset, endOffset, IMarker.SEVERITY_ERROR, problemKind, inserts, bundle);
		}
		else {
			acceptProblem(startOffset, endOffset, IMarker.SEVERITY_WARNING, problemKind, inserts, bundle);
		}
	}
	
	@Override
	public void acceptProblem(int startOffset, int endOffset, int problemKind, String[] inserts) {
		acceptProblem(startOffset, endOffset, problemKind, true, inserts);		
	}
	
	@Override
	public void acceptProblem(int startOffset, int endOffset, int severity, int problemKind) {
		acceptProblem(startOffset, endOffset, severity, problemKind, new String[0], RESOURCE_BUNDLE);
	}
	
	@Override
	public void acceptProblem(Element element, int problemKind) {
		acceptProblem(element, problemKind, IMarker.SEVERITY_ERROR, null, RESOURCE_BUNDLE);
	}
	
	@Override
	public void acceptProblem(Element element, int problemKind, int severity) {
		acceptProblem(element, problemKind, severity, null, RESOURCE_BUNDLE);
	}
	
	@Override
	public void acceptProblem(Element element, int problemKind, int severity, String[] inserts) {
		acceptProblem(element, problemKind, severity, inserts, RESOURCE_BUNDLE);
	}
	
	@Override
	public void acceptProblem(Element element, int problemKind, int severity, String[] inserts, ResourceBundle bundle) {
		int startOffset = 0;
		int endOffset = 0;
		Annotation annot = element.getAnnotation(IEGLConstants.EGL_LOCATION);
		if (annot != null) {
			Object val = annot.getValue(IEGLConstants.EGL_PARTOFFSET);
			if (val instanceof Integer) {
				startOffset = ((Integer)val).intValue();
				endOffset = startOffset;
				
				val = annot.getValue(IEGLConstants.EGL_PARTLENGTH);
				if (val instanceof Integer) {
					endOffset += ((Integer)val).intValue();
				}
			}
		}
		acceptProblem(startOffset, endOffset, severity, problemKind, inserts, bundle);
	}
	
	public static String getMessageFromBundle(int problemKind, String[] inserts) {
		return getMessageFromBundle(problemKind, inserts, RESOURCE_BUNDLE);
	}
	
	public static String getMessageFromBundle(int problemKind, String[] inserts, ResourceBundle bundle) {
		String message = bundle.getString(Integer.toString(problemKind));
		if (message == null || inserts == null || inserts.length == 0) {
			return message;
		}
		MessageFormat formatter = new MessageFormat(message);
		formatter.applyPattern(message);
		return formatter.format(insertsWithoutNulls(inserts));
	}
	
	private static Object[] insertsWithoutNulls(Object[] originalInserts) {
		int numberInserts = originalInserts.length;
		Object[] newInserts = new Object[numberInserts];
		for (int i = 0; i < numberInserts; i++) {
			if (originalInserts[i] != null)
				newInserts[i] = originalInserts[i];
			else
				newInserts[i] = ""; //$NON-NLS-1$
		}
		return newInserts;
	}
	
	public static Set messagesWithLineNumberInserts = new HashSet(Arrays.asList(new Integer[] {
		new Integer(REDEFINES_MUST_BE_DECLARATION),
		new Integer(REDEFINES_CANNOT_BE_REDEFINES),
		new Integer(REDEFINES_SIZE_MISMATCH),
		new Integer(REDEFINING_MUST_BE_FIXED_RECORD),
		new Integer(SELECTFROMLIST_MUST_BE_ARRAY),
		new Integer(SELECTTYPE_ITEM_MUST_MATCH_ARRAY),
		new Integer(SELECTTYPE_TARGET_MUST_BE_INT),
		new Integer(ARRAY_LITERAL_SIZE_TOO_LARGE),
		new Integer(ARRAY_PASSED_TO_NON_EGL_PROGRAM),
		new Integer(SUBSTRING_TARGET_NOT_STRING),
		new Integer(SUBSTRING_INDEX_NOT_INTEGER),
		new Integer(CANNOT_PASS_REFERNECE_TYPE_ON_CALL),
		new Integer(JAVA_FUNCTION_NUMERIC_EXPRESSION_AS_ARG),
		new Integer(JAVA_CAST_CHARACTER_TYPE_NEEDED),
		new Integer(JAVA_CAST_NUMERIC_TYPE_NEEDED),
		new Integer(SYSTEM_FUNCTION_NOT_ALLOWED_IN_PAGEHANDLER),
		new Integer(DICTIONARY_FUNCTION_USED_WITHOUT_DICTIONARY),
		new Integer(DICTIONARY_OR_ARRAY_FUNCTION_USED_WITHOUT_DICTIONARY_OR_ARRAY),
		new Integer(COND_INVALID_ESCAPE_CHARACTER),
		new Integer(COND_OPERAND_MUST_BE_STRING),
		new Integer(COND_OPERAND_CANNOT_BE_HEX_OR_DBCHAR),
		new Integer(SYSTEM_FUNCTION_ARG_CANNOT_BE_EXPRESSION),
		new Integer(TYPE_NOT_VALID_IN_EXPRESSION),
		new Integer(EXPRESSION_FUNCTION_INVOCATION_NOT_ALLOWED),
		new Integer(OPERANDS_NOT_VALID_WITH_OPERATOR),
		new Integer(EXPRESSIONS_INCOMPATIBLE),
		new Integer(ELEMENT_NOT_VALID_IN_EXPRESSION),
		new Integer(UNARY_EXPRESSION_INVALID_IN_STRING_CONCAT_EXPRESSION),
		new Integer(TYPE_NOT_VALID_IN_STRING_CONCAT_EXPRESSION),
		new Integer(TYPE_NOT_VALID_IN_NUMERIC_EXPRESSION),
		new Integer(TYPE_INCOMPATIBLE_ARITHMETIC_COMPARISON),
		new Integer(DISCOURAGED_ARITHMETIC_COMPARISON),
		new Integer(IN_CONDITIONAL_LEFT_OPERAND_INVALID),
		new Integer(IN_CONDITIONAL_RIGHT_OPERAND_INVALID),
		new Integer(COND_OPERAND_MUST_BE_RECORD),
		new Integer(COND_OPERAND_MUST_BE_TEXTFORM_FIELD),
		new Integer(INVALID_TYPE_BLANKS),
		new Integer(COND_OPERAND_INVALID_FOR_NULL),
		new Integer(COND_OPERAND_INVALID_MODIFIED),
		new Integer(ROUTINE_MUST_HAVE_X_OR_Y_ARGS),
		new Integer(ARG_MUST_BE_ITEM_INTEGER_OR_STRING),
		new Integer(INVALID_USAGE_LOCATION),
		new Integer(ARG_MUST_BE_ITEM_CONSTANT_OR_LITERAL),
		new Integer(ARG_MUST_BE_ITEM),
		new Integer(ARG_MUST_BE_CHAR_ITEM),
		new Integer(ROUTINE_MUST_HAVE_ATLEAST_X_ARGS),
		new Integer(ROUTINE_MUST_HAVE_ONE_OR_TWO_ITEM_ARGUMENTS),
		new Integer(INVALID_ROUND_ARG_1),
		new Integer(INVALID_SUBSCRIPT),
		new Integer(ROUTINE_MUST_HAVE_X_ARGS),
		new Integer(ARG_MUST_BE_INTEGER_ITEM_CONSTANT_OR_LITERAL),
		new Integer(ARG_MUST_BE_ITEM_STRING_CONSTANT_OR_LITERAL),
		new Integer(ARG_MUST_BE_GREATER_THAN_ZERO),
		new Integer(INVALID_SETSUBSTR_ARGUMENT_4),
		new Integer(ARG_MUST_NOT_BE_INTEGER),
		new Integer(ROUTINE_MUST_HAVE_ONE_ITEM_ARG),
		new Integer(NOT_VAGCOMP_FOR_CONNECTION_SERVICES),
		new Integer(INVALID_ARG_1_TO_5_FOR_CONNECTION_SERVICES),
		new Integer(ARG6_MUST_CERTAIN_VALUES_FOR_CONNECTION_SERVICES),
		new Integer(INVALID_NUM_ARGS_FOR_CONVERT),
		new Integer(INVALID_FIRST_ARG_FOR_CONVERT),
		new Integer(INVALID_SECOND_ARG_FOR_CONVERT),
		new Integer(INVALID_THIRD_ARG_FOR_CONVERT),
		new Integer(INVALID_NUM_ARGS_FOR_PURGE),
		new Integer(INVALID_ARG_FOR_PURGE),
		new Integer(NOT_VAGCOMP_FOR_SYSTEM_WORD),
		new Integer(INVALID_NUM_ARGS_FOR_SET_ERROR_OR_LOCALE),
		new Integer(INVALID_LITERAL_ARG1_2_LENGTH_FOR_SETLOCALE),
		new Integer(INVALID_FIRST_ARG_FOR_STARTTRANS),
		new Integer(SHOULD_NOT_BE_SUBSCRIPTED),
		new Integer(TOO_MANY_QUALIFIERS),
		new Integer(INVALID_SYSTEM_VARIABLE_VALUE_0_OR_1),
		new Integer(INVALID_SYSTEM_VARIABLE_VALUE_0_1_OR_2),
		new Integer(INVALID_NUM_SYSTEM_VARIABLE_ASSIGNMENT),
		new Integer(INVALID_TYPE_ISNUMERIC),
		new Integer(INVALID_RETURN_CODE_ASSIGN_VALUE),
		new Integer(INVALID_MQCONDITIONCODE_ASSIGN),
		new Integer(SUBSCRITPT_REQUIRED),
		new Integer(INVALID_DATA_WORD_SUBSCRIPT_VALUE),
		new Integer(INVALID_ARRAY_INDEX_ASSIGN_VALUE),
		new Integer(INVALID_VALIDATIONMSGNUM_ASSIGN_VALUE),
		new Integer(ARG_MUST_BE_BIN_ITEM_LESS_THAN_5_DIGITS),
		new Integer(INVALID_ARG_LIST),
		new Integer(INVALID_EVENT_KEY_VALUE),
		new Integer(INVALID_SYSTEM_TYPE_VALUE),
		new Integer(INVALID_EVENT_KEY_USE),
		new Integer(INVALID_SYSTEM_TYPE_USE),
		new Integer(INVALID_ARG_7_FOR_CONNECT),
		new Integer(INVALID_MOVE_TO_NUM_SYSTEM_VARIABLE),
		new Integer(INVALID_ARG_MUST_BE_INT),
		new Integer(FORWARD_ARG_MUST_BE_UI_RECORD),
		new Integer(FORWARD_ARG_MUST_BE_ITEM_RECORD_OR_DYNAMIC_ARRAY),
		new Integer(FORWARD_UI_RECORD_DEFINITION_MUST_MATCH_INPUT_PAGE_RECORD),
		new Integer(FORWARD_RETURN_TO_MUST_BE_ACTION_PROGRAM),
		new Integer(MOVE_FOR_COUNT_NOT_INTEGER),
		new Integer(ARG_MUST_BE_ITEM_OR_RECORD),
		new Integer(ARG_MUST_BE_SQL_REC_ITEM_OR_STRING_LITERAL),
		new Integer(INVALID_PAGEHANDLER_SYSTEM_FUNCTION_USAGE),
		new Integer(PROPERTY_DOESNT_RESOLVE),
		new Integer(PROPERTY_AMBIGUOUS),
		new Integer(PROPERTY_MUST_BE_BASIC_RECORD),
		new Integer(RECORD_CANT_BE_SYSTEM_FUNCTION_RESULT),
		new Integer(RESULTSETID_NOT_FOUND),
		new Integer(HOST_VARIABLE_NOT_FOUND),
		new Integer(HOST_VARIABLE_AMBIGUOUS),
		new Integer(DUPLICATE_RESULTSETID),
		new Integer(CONFLICTING_RESULTSET_ID),
		new Integer(PREPARED_STATEMENT_ID_NOT_FOUND),
		new Integer(ITEM_OR_CONSTANT_NOT_CHARACTER_TYPE),
		new Integer(PASSING_RECORD_NOT_RECORD),
		new Integer(STATEMENT_CANNOT_BE_IN_ACTION_PROGRAM),
		new Integer(STATEMENT_CANNOT_BE_IN_ACTION_OR_BASIC_PROGRAM),
		new Integer(STATEMENT_CANNOT_BE_IN_LIBRARY),
		new Integer(STATEMENT_CANNOT_BE_IN_PAGE_HANDLER),
		new Integer(STATEMENT_CANNOT_BE_IN_BASIC_PROGRAM),
		new Integer(STATEMENT_CANNOT_BE_IN_BASIC_OR_TEXTUI_PROGRAM),
		new Integer(STATEMENT_CANNOT_BE_IN_CALLED_BASIC_OR_CALLED_TEXTUI_PROGRAM),
		new Integer(STATEMENT_CANNOT_BE_IN_CALLED_TEXT_UI_PROGRAM),
		new Integer(STATEMENT_CAN_ONLY_BE_IN_PAGE_HANDLER),
		new Integer(NOT_ENOUGH_SUBSCRIPTS),
		new Integer(TOO_MANY_SUBSCRIPTS),
		new Integer(SUBSCRIPT_OUT_OF_RANGE),
		new Integer(DOT_ACCESS_USED_AFTER_DYNAMIC),
		new Integer(EXPRESSION_AS_SUBSCRIPT),
		new Integer(SQL_TABLE_NAME_VAR_MUST_BE_ITEM),
		new Integer(VARIABLE_RESOLVED_TO_CONTAINER_MIGHT_BE_ITEM_IN_VAGEN),
		new Integer(PRINT_TARGET_MUST_BE_PRINT_FORM),
		new Integer(DUPLICATE_LABEL),
		new Integer(DELETE_FROM_CLAUSE_WITH_NON_SQL_RECORD),
		new Integer(EXIT_PROGRAM_ITEM_NOT_INTEGER),
		new Integer(EXIT_STACK_LABEL_NOT_IN_MAIN),
		new Integer(DUPLICATE_PREPARED_STATEMENT_ID),
		new Integer(CONFLICTING_PREPARED_STATEMENT_ID),
		new Integer(STATEMENT_TARGET_NOT_SQL_RECORD),
		new Integer(PREPARE_STATEMENT_FROM_ARGUMENT_NOT_STRING_EXPRESSION),
		new Integer(NO_RESULT_SET_ID_FOR_MULTIPLE_CURSOR_OPEN),
		new Integer(SQL_CLAUSES_OR_OPTIONS_ON_REPLACE_WITH_NON_SQL_REC),
		new Integer(REPLACE_STATEMENT_TARGET_NOT_RECORD),
		new Integer(OPEN_FOR_TARGET_NOT_SQL_RECORD),
		new Integer(ADD_STATEMENT_TARGET_NOT_RECORD),
		new Integer(ADD_STATEMENT_WITH_USED_WITHOUT_SQL_RECORD),
		new Integer(INVALID_CLAUSE_FOR_NON_SQL_TARGET),
		new Integer(GET_BY_POSITION_DIRECTIVE_OTHER_THAN_NEXT),
		new Integer(GET_BY_POSITION_DIRECTIVE_OTHER_THAN_NEXT_OR_PREVIOUS),
		new Integer(GET_BY_POSITION_STATEMENT_TARGET_NOT_RECORD),
		new Integer(GET_BY_KEY_STATEMENT_TARGET_NOT_RECORD),
		new Integer(STRINGCONCAT_EXPRESSION_INVALID_EXPR),
		new Integer(ASSIGNMENT_STATEMENT_INCOMPATIBLE_OPERANDS),
		new Integer(ASSIGNMENT_STATEMENT_RECORD_TARGET_SOURCE_CANNOT_BE),
		new Integer(ASSIGNMENT_STATEMENT_RECORD_SOURCE_TARGET_MUST_BE),
		new Integer(ASSIGNMENT_STATEMENT_MUST_BE_RECORD_OR_ITEM),
		new Integer(FUNCTION_MUST_RETURN_TYPE),
		new Integer(ASSIGNMENT_STATEMENT_VARIABLE_NOT_DEFINED),
		new Integer(SHOW_RETURNING_TO_NOT_PROGRAM_OR_CHARACTER_ITEM),
		new Integer(SHOW_PASSING_TARGET_NOT_RECORD),
		new Integer(INVALID_CLOSE_TARGET),
		new Integer(MOVE_STATEMENT_INVALID_TARGET_TYPE),
		new Integer(INVALID_CONVERSE_TARGET_FOR_ACTION_PROGRAM),
		new Integer(INVALID_CONVERSE_TARGET_FOR_TEXTUI_PROGRAM),
		new Integer(INVALID_CONVERSE_TARGET_FOR_UI_PROGRAM),
		new Integer(PROGRAM_MUST_BE_UIPROGRAM),
		new Integer(INVALID_PASSING_RECORD_RUI_PROGRAM),
		new Integer(NO_SEGMENTED_CONVERSE_IN_CALLED_PROGRAM),
		new Integer(MOVE_STATEMENT_INVALID_SOURCE_TYPE),
		new Integer(MOVE_STATEMENT_LITERAL_CANT_MOVE_BY_NAME_OR_BY_POSITION),
		new Integer(MOVE_STATEMENT_BYNAME_BYPOSITION_ITEM_HAS_NO_SUBSTRUCTURE),
		new Integer(MOVE_STATEMENT_NONUNIQUE_BYNAME_SOURCE),
		new Integer(MOVE_STATEMENT_NONUNIQUE_BYNAME_TARGET),
		new Integer(MOVE_STATEMENT_MULTIDIMENSIONAL_BYNAME_OR_BYPOSITION_SOURCE),
		new Integer(MOVE_STATEMENT_MULTIDIMENSIONAL_BYNAME_OR_BYPOSITION_TARGET),
		new Integer(MOVE_STATEMENT_INCOMPATIBLE_TYPES),
		new Integer(MOVE_STATEMENT_TARGET_WRONG_TYPE_FOR_SCALAR_SOURCE),
		new Integer(MOVE_STATEMENT_TARGET_WRONG_TYPE_FOR_CONTAINER_SOURCE),
		new Integer(MOVE_STATEMENT_TARGET_WRONG_TYPE_FOR_ARRAY_SOURCE),
		new Integer(FUNCTION_REFERENCE_CANNOT_BE_RESOLVED_CONTEXT),
		new Integer(FUNCTION_REFERENCE_AMBIGUOUS_CONTEXT),
		new Integer(FUNCTION_ARGUMENTS_DONT_MATCH),
		new Integer(NULLABLE_ARGUMENT_NOT_SQL_ITEM),
		new Integer(FIELD_ARGUMENT_NOT_FORM_FIELD),
		new Integer(STATIC_ARRAY_CANT_BE_ARGUMENT),
		new Integer(DYNAMIC_ARRAY_CANT_BE_ARGUMENT),
		new Integer(INVALID_ARGUMENT_TYPE),
		new Integer(ARRAY_FUNCTION_USED_WITHOUT_DYNAMIC_ARRAY),
		new Integer(ARRAY_ELEMENT_ARGUMENT_INCORRECT_TYPE),
		new Integer(MAXIMUMSIZE_ARGUMENT_INCORRECT),
		new Integer(INVALID_SET_STATEMENT_DATA_REFERENCE),
		new Integer(INVALID_SET_STATE_FOR_ITEM),
		new Integer(INVALID_SET_STATE_FOR_SQL_ITEM),
		new Integer(INVALID_SET_STATE_FOR_RECORD),
		new Integer(INVALID_SET_STATE_FOR_TEXT_FORM),
		new Integer(INVALID_SET_STATE_FOR_PRINT_FORM),
		new Integer(INVALID_SET_STATE_FOR_TEXT_FIELD),
		new Integer(MOVE_BY_POSITION_INCOMPATIBLE_TYPES),
		new Integer(SYSTEM_ARGS_ONLY_ITEMS_LITERALS),
		new Integer(CALL_ARGUMENT_REQUIRES_PROGRAM),
		new Integer(PROGRAM_ARGS_DONT_MATCH_PARAMS),
		new Integer(PAGEHANDLER_ARGS_DONT_MATCH_PARAMS),
		new Integer(FLEXIBLE_RECORD_PASSED_TO_NON_EGL_PROGRAM),
		new Integer(USED_LIBRARY_RECORD_USED_FOR_IO),
		new Integer(PROGRAM_INPUT_RECORD_DOESNT_MATCH_PARAM),
		new Integer(FORWARD_TARGET_DOESNT_HAVE_ONPAGELOAD_FUNCTION),
		new Integer(FUNCTION_ARG_CANNOT_BE_NULL),
		new Integer(OCCURED_ITEM_MOVE_OPERAND_NOT_SUBSCRIPTED),
		new Integer(NON_CONTAINER_MOVE_OPERAND_MOVED_BY_NAME_OR_POSITION),
		new Integer(INVALID_APPENDALL_ARG),
		new Integer(FLEXIBLE_RECORD_ARRAYS_MOVED_BYNAME_OR_BYPOSITION),
		new Integer(PROGRAM_INPUT_UIRECORD_DOESNT_MATCH_PARAM),
		new Integer(STATEMENT_TARGET_MUST_BE_TEXT_FORM),
		new Integer(STATEMENT_TARGET_MUST_BE_FORM),
		new Integer(STATEMENT_TARGET_MUST_BE_PRINT_FORM),
		new Integer(FORWARD_STATEMENT_CANNOT_CONTAIN_RETURNING_TO),
		new Integer(TRANSFER_TO_TRANSACTION_NOT_ALLOWED),
		new Integer(SHOW_STATEMENT_TARGET_WRONG_TYPE),
		new Integer(SHOW_STATEMENT_TARGET_MUST_BE_UIRECORD),
		new Integer(ARG_MUST_BE_MATH_NUMERIC_ITEM),
		new Integer(ARG_MUST_BE_MATH_INTEGER_ITEM),
		new Integer(ARG_MUST_BE_NUMERIC_ITEM_CONSTANT_OR_LITERAL),
		new Integer(ARG_MUST_BE_STRING_ITEM_CONSTANT_OR_LITERAL),
		new Integer(ARG_MUST_BE_STRING_CONSTANT_OR_LITERAL),
		new Integer(ARG_MUST_BE_NUMERIC_EXPRESSION),
		new Integer(ARG_MUST_BE_STRING_EXPRESSION),
		new Integer(ARG_MUST_BE_INTEGER_EXPRESSION),
		new Integer(ARG_MUST_HAVE_NO_DECIMALS),
		new Integer(DATETIME_LITERAL_CANNOT_START_OR_END_WITH_DELIM),
		new Integer(TIMESTAMP_LITERAL_YEAR_FIELD_TOO_LONG),
		new Integer(TIMESTAMP_LITERAL_MONTH_FIELD_TOO_LONG),
		new Integer(TIMESTAMP_LITERAL_DAY_FIELD_TOO_LONG),
		new Integer(TIMESTAMP_LITERAL_HOUR_FIELD_TOO_LONG),
		new Integer(TIMESTAMP_LITERAL_MINUTES_FIELD_TOO_LONG),
		new Integer(TIMESTAMP_LITERAL_SECONDS_FIELD_TOO_LONG),
		new Integer(TIMESTAMP_LITERAL_SECOND_FRACTIONS_FIELD_TOO_LONG),
		new Integer(DATETIME_PATTERN_HAS_INVALID_CHARACTER),
		new Integer(DATETIME_PATTERN_OUT_OF_ORDER),
		new Integer(DATETIME_PATTERN_YEAR_FIELD_TOO_LONG),
		new Integer(DATETIME_PATTERN_MONTH_FIELD_TOO_LONG),
		new Integer(DATETIME_PATTERN_DAY_FIELD_TOO_LONG),
		new Integer(DATETIME_PATTERN_HOUR_FIELD_TOO_LONG),
		new Integer(DATETIME_PATTERN_MINUTES_FIELD_TOO_LONG),
		new Integer(DATETIME_PATTERN_SECONDS_FIELD_TOO_LONG),
		new Integer(DATETIME_PATTERN_SECOND_FRACTIONS_FIELD_TOO_LONG),
		new Integer(DATETIME_PATTERN_MISSING_INTERMEDIATE_FIELD),
		new Integer(DATETIME_PATTERN_EMPTY),
		new Integer(DATETIME_PATTERN_INVALID_INTERVAL_SPAN),
		new Integer(TIMESTAMP_LITERAL_MUST_BE_PATTERN_LENGTH),
		new Integer(TIMESTAMP_LITERAL_MUST_HAVE_AS_MANY_FIELDS_AS_PATTERN),
		new Integer(TIMESTAMP_LITERAL_YEAR_FIELD_DOESNT_MATCH_PATTERN),
		new Integer(TIMESTAMP_LITERAL_MONTH_FIELD_DOESNT_MATCH_PATTERN),
		new Integer(TIMESTAMP_LITERAL_DAY_FIELD_DOESNT_MATCH_PATTERN),
		new Integer(TIMESTAMP_LITERAL_HOUR_FIELD_DOESNT_MATCH_PATTERN),
		new Integer(TIMESTAMP_LITERAL_MINUTES_FIELD_DOESNT_MATCH_PATTERN),
		new Integer(TIMESTAMP_LITERAL_SECONDS_FIELD_DOESNT_MATCH_PATTERN),
		new Integer(TIMESTAMP_LITERAL_SECOND_FRACTIONS_FIELD_DOESNT_MATCH_PATTERN),
		new Integer(DATETIME_LITERAL_MONTH_OUT_OF_RANGE),
		new Integer(DATETIME_LITERAL_DAY_OUT_OF_RANGE),
		new Integer(DATETIME_LITERAL_HOUR_OUT_OF_RANGE),
		new Integer(DATETIME_LITERAL_MINUTE_OUT_OF_RANGE),
		new Integer(DATETIME_LITERAL_SECOND_OUT_OF_RANGE),
		new Integer(EXTEND_TIMESTAMP_VALUE_ARGUMENT_WRONG_TYPE),
		new Integer(FORMAT_DATE_ARGUMENT1_WRONG_TYPE),
		new Integer(FORMAT_TIME_ARGUMENT1_WRONG_TYPE),
		new Integer(FORMAT_TIMESTAMP_ARGUMENT1_WRONG_TYPE),
		new Integer(TYPE_NOT_VALID_IN_BOOLEAN_EXPRESSION),
		new Integer(READONLY_FIELD_CANNOT_BE_ASSIGNED_TO),
		new Integer(READONLY_FIELD_CANNOT_BE_PASSED_TO_OUT_PARM),
		new Integer(INVALID_INTO_ITEM_FOR_GET_SQL_RECORD_ARRAY),
		new Integer(CLOSE_PRINTFORM_NOT_ALLOWED_IN_WEB_TRANSACTION),
		new Integer(VGWEBTRANSACTION_NOT_VALID_TRANSER_TO_TRANSACTION_TARGET),
		new Integer(BAD_TYPE_FOR_TRANSFER_TO_PROGRAM_IN_VGWEBTRANSACTION),
		new Integer(FORWARD_TO_URL_TARGET_MUST_BE_CHARACTER),
		new Integer(SHOW_UIRECORD_ONLY_VALID_IN_VGWEBTRANSACTION),
		new Integer(FORUPDATE_NOT_ALLOWED_WITH_ARRAY_TARGET),
		new Integer(SUBSTRUCTURED_ITEM_CANNOT_BE_ARGUMENT_TO_NATIVE_LIBRARY_FUNCTION),
		new Integer(FUNCTION_PARAMETER_REQUIRES_LENGTH),
		new Integer(ROUTINE_MUST_HAVE_EVEN_NUM_OF_ARGS),
		new Integer(ROUTINE_CANT_HAVE_MORE_THAN_ARGS),
		new Integer(HOST_VARIABLE_MUST_BE_ITEM),
		new Integer(SYSVAR_NOT_HOST_VARIABLE),
		new Integer(VARIABLE_NOT_FOUND_AS_ITEM),
		new Integer(RETURN_TARGET_NOT_ITEM),
		new Integer(EXIT_MODIFIER_ONLY_ALLOWED_IN_PROGRAM),
		new Integer(EXIT_MODIFIER_NOT_ALLOWED_IN_SERVICE),
		new Integer(CONVERT_TARGET_INVALID),
		new Integer(CONVERTBIDI_TARGET_INVALID),
		new Integer(CONVERTBIDI_CONVTABLE_INVALID),
		new Integer(MOVE_MUST_BE_REFERENCE),
		new Integer(MOVE_EXTERNALTYPE),
		new Integer(CANNOT_ASSIGN_TO_ARRAY_DICTIONARY_ELEMENTS),
		new Integer(CONTEXT_SPECIFIC_COMPILATION_EXCEPTION),
		new Integer(USINGKEYS_ITEM_IN_SQL_RECORD_ARRAY_IO_TARGET),
		new Integer(FUNCTION_INVOCATION_TARGET_NOT_FUNCTION_OR_DELEGATE),
		new Integer(DELETE_STATEMENT_RECORD_IS_INVALID_TYPE),
		new Integer(SYSTEM_FUNCTION_CANNOT_BE_DELEGATED),
		new Integer(FUNCTION_WITH_CONVERSE_CANNOT_BE_DELEGATED),
		new Integer(MAIN_FUNCTION_CANNOT_BE_ASSIGNED_TO_DELEGATE),
		new Integer(EXCEPTION_FILTER_NOT_VALID_WITH_V60EXCEPTIONCOMPATIBILITY),
		new Integer(EXCEPTION_FILTER_REQUIRED),
		new Integer(THROW_TARGET_MUST_BE_EXCEPTION),
		new Integer(THROW_NOT_VALID_WITH_V60EXCEPTIONCOMPATIBILITY),
		new Integer(SIZEINBYTES_ARGUMENT_INVALID),
		new Integer(CONVERT_ARGUMENT_INVALID),
		new Integer(SIZEOF_ARGUMENT_INVALID),
		new Integer(VARIABLE_NOT_FOUND_AS_ITEM_OR_CONTAINER),
		new Integer(TYPE_IN_CATCH_BLOCK_NOT_EXCEPTION),
		new Integer(DUPLICATE_ONEXCEPTION_EXCEPTION),
		new Integer(CANNOT_WRITE_TO_EXTERNALTYPE_FIELD_WITH_NO_SETTER),
		new Integer(CANNOT_READ_FROM_EXTERNALTYPE_FIELD_WITH_NO_GETTER),
		new Integer(VARIBLE_NEEDS_SYSTEM_LIBRARY_QUALIFIER),
		new Integer(DYNAMIC_ACCESS_NOT_ALLOWED_IN_INTO_CLAUSE),
		new Integer(PROGRAM_PARAMETER_HAS_INCORRECT_TYPE),
		new Integer(DATETIME_PATTERN_FIRST_INTERVAL_FIELD_TOO_LONG),
		new Integer(SYSTEM_FUNCTION_NOT_ALLOWED_IN_SERVICE),
		new Integer(TYPE_NOT_VALID_IN_BITWISE_EXPRESSION),
		new Integer(MULTIPLE_OVERLOADED_FUNCTIONS_MATCH_ARGUMENTS),
		new Integer(NO_FUNCTIONS_MATCH_ARGUMENTS),
		new Integer(FIXED_RECORDS_NOT_ALLOWED_IN_COMPARISONS),
		new Integer(COMPARING_TEXT_AND_NUMERIC),
		new Integer(TYPE_INVALID_CONSOLE_FIELD_TYPE_COMPARISON),
		new Integer(TRUNC_OPERAND_INVALID_MODIFIED),
		new Integer(INCORRECT_UNICODE_LENGTH_IN_UNICODE_CONVERSION_FUNCTION),
		new Integer(ITEM_RESOLVED_TO_CONTAINER_WITH_SAME_NAME_AS_FIELD),
		new Integer(ITEM_RESOLVED_TO_FIELD_WITH_SAME_NAME_AS_CONTAINER),
		new Integer(ITEM_RESOLVED_TO_FIELD_WITH_SAME_NAME_AS_FIELD),
		new Integer(STATEMENT_CANNOT_BE_IN_SERVICE),
		new Integer(INVOCATION_TARGET_INVALID),
		new Integer(INVOCATION_TARGET_FOR_CALL_INVALID),
		new Integer(IN_FROM_EXPRESSION_NOT_INTEGER),
		new Integer(NOCURSOR_REQUIRES_KEY_ITEM),
		new Integer(REDEFINER_AND_REDEFINED_MUST_BE_DECLARED_IN_SAME_PART),
		new Integer(SET_POSITION_STATEMENT_WITH_INVALID_DATAREF),
	}));
	
	/**
	 * To handle the class of validation error messages that begin with "{0} -"
	 * and end with "At line {x} in file{y}." If problemKind indicates one of
	 * these messages, an array of size three greater than inserts.length is returned,
	 * with null values for the first and last two elements.
	 */
	public static String[] shiftInsertsIfNeccesary(int problemKind, String[] inserts) {
		if(inserts == null) return null;
		if(messagesWithLineNumberInserts.contains(new Integer(problemKind))) {
			String[] newInserts = new String[inserts.length+3];
			System.arraycopy(inserts, 0, newInserts, 1, inserts.length);
			return newInserts;
		}
		return inserts;
	}
	
	public boolean hasError() {
		return hasError;
	}
	
	public void setHasError(boolean err) {
		hasError = err;
	}
}
