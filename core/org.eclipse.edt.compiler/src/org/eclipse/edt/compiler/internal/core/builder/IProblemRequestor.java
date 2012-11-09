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

import java.util.ResourceBundle;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.mof.egl.Element;


/**
 * @author winghong
 */
public interface IProblemRequestor {

	// The following method should be overriden by all non-abstract subtypes
    void acceptProblem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts, ResourceBundle bundle);    
    
    // The following methods exist for convenience and are overriden in
    // DefaultProblemRequestor. Subtypes need not override them
    void acceptProblem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts);
    void acceptProblem(Node astNode, int problemKind);
    void acceptProblem(Node astNode, int problemKind, int severity);		
	void acceptProblem(Node astNode, int problemKind, String[] inserts);	
	void acceptProblem(Node astNode, int problemKind, int severity, String[] inserts);
	void acceptProblem(Node astNode, int problemKind, int severity, String[] inserts, ResourceBundle bundle);
	void acceptProblem(Element element, int problemKind);
	void acceptProblem(Element element, int problemKind, int severity);
	void acceptProblem(Element element, int problemKind, int severity, String[] inserts);
	void acceptProblem(Element element, int problemKind, int severity, String[] inserts, ResourceBundle bundle);
	void acceptProblem(int startOffset, int endOffset, int severity, int problemKind);
	void acceptProblem(int startOffset, int endOffset, int problemKind, String[] inserts);
	void acceptProblem(int startOffset, int endOffset, int problemKind, boolean isError, String[] inserts);
	void acceptProblem(int startOffset, int endOffset, int problemKind, boolean isError, String[] inserts, ResourceBundle bundle);
	boolean shouldReportProblem(int problemKind);
	
	boolean hasError();
	
	//EGL Message ranges (ids):
	// 0000-0999: reserved (not used)
	// 2000-2999: Parser
	// 3000-6999: Validation/Pre-processor
	// 7000-7999: Editor
	// 8000-8999:
	// 9000-9999:
	public static final int MISSING_SEMI = 2000;
	public static final int UNEXPECTED_EOF = 2001;
	public static final int UNEXPECTED_TOKEN = 2002;
	public static final int MISSING_END = 2003;
	public static final int UNMATCHED_BRACE = 2004;
	public static final int UNMATCHED_PAREN = 2005;
	public static final int UNMATCHED_BRACKET = 2006;
	public static final int UNCLOSED_STRING = 2050;
	public static final int UNCLOSED_BLOCK_COMMENT = 2051;
	public static final int UNCLOSED_SQL_STMT = 2052;
	public static final int INVALID_SQL_STMT = 2053;
	public static final int INVALID_SQL_CONDITION = 2054;
	public static final int INCOMPLETE_DATA_ACCESS = 2055;
	public static final int DUPLICATE_PART_SUBTYPE = 2056;
	public static final int INVALID_PART_SUBTYPE = 2057;
	public static final int ANNOTATION_NOT_APPLICABLE = 2058; 
	public static final int NOT_AN_ANNOTATION = 2065;
	public static final int COMPILATION_EXCEPTION = 3000;
	public static final int INVALID_NAME_LENGTH = 3001;
	public static final int INVALID_CHARACTER_IN_NAME = 3002;
	public static final int EZE_NOT_ALLOWED = 3003;
	public static final int DUPLICATE_VARIABLE_NAME = 3010;
	public static final int DUPLICATE_NAME_ACROSS_LISTS = 3012;
	public static final int RESERVED_WORD_NOT_ALLOWED = 3019;
	public static final int DUPLICATE_NAME_IN_FILE = 3022;
	public static final int FUNCTION_TOO_MANY_PARMS = 3028;
	public static final int DUPLICATE_USE_NAME = 3031;
	public static final int INVALID_SUBSCRIPT_NESTING = 3035;
	public static final int TYPE_CANNOT_BE_SUBSCRIPTED = 3037;
	public static final int DUPLICATE_NAME_IN_NAMESPACE = 3039;
	public static final int GENERATABLE_PART_NAME_MUST_MATCH_FILE_NAME = 3040;
	public static final int ONLY_ONE_GENERATABLE_PART_PER_FILE = 3041;
	public static final int TYPE_CANNOT_BE_QUALIFIED = 3042;
	public static final int PACKAGE_NAME_DOESNT_MATCH_DIRECTORY_STRUCTURE = 3049;
	public static final int PACKAGE_NAME_NOT_PROVIDED = 3064;
	public static final int WHITESPACE_NOT_ALLOWED = 3066;
	public static final int STATIC_ARRAY_PARAMETER_DEFINITION = 3067;
	public static final int RECORD_PARAMETER_WITH_NO_CONTENTS = 3118;
	public static final int EXPRESSION_NOT_VALID_FOR_PROPERTY = 3127;
	public static final int PROPERTY_STRING_PRIMITIVE_REQUIRED = 3193;
	public static final int PROPERTY_NUMERIC_PRIMITIVE_REQUIRED = 3194;
	public static final int ARRAY_DIMENSION_NOT_ALLOWED = 3248;
	public static final int SETTINGS_BLOCK_NOT_ALLOWED = 3249;
	public static final int INTEGER_LITERAL_OUT_OF_RANGE = 3250;
	public static final int DECIMAL_LITERAL_OUT_OF_RANGE = 3251;
	public static final int FLOATING_POINT_LITERAL_OUT_OF_RANGE = 3252;
    public static final int TYPE_CANNOT_BE_RESOLVED = 3260;
	public static final int TYPE_IS_AMBIGUOUS = 3262;
	public static final int ARRAY_SIZE_LESS_THAN_ZERO = 3263;
	public static final int ARRAY_SIZE_NOT_ALLOWED_IN_ISA_OR_AS = 3264;
	public static final int BYTES_LITERAL_LENGTH_MUST_BE_EVEN = 3277;
	public static final int BIGINT_LITERAL_OUT_OF_RANGE = 3278;
	public static final int SMALLINT_LITERAL_OUT_OF_RANGE = 3279;
	public static final int SMALLFLOAT_LITERAL_OUT_OF_RANGE = 3280;
	public static final int TYPE_IS_NOT_PARAMETERIZABLE = 3281;
	public static final int TYPE_ARG_NOT_VALID = 3282;
	public static final int TYPE_ARGS_INVALID_SIZE = 3283;
	public static final int ANNOTATION_CANNOT_BE_ARRAY = 3290;
	public static final int ANNOTATION_MUST_BE_ARRAY = 3291;
	public static final int ANNOTATION_CANNOT_BE_NULL= 3292;
	public static final int ANNOTATION_MUST_BE_STRING = 3293;
	public static final int ANNOTATION_MUST_BE_BOOL = 3294;
	public static final int ANNOTATION_MUST_BE_INT = 3295;
	public static final int ANNOTATION_MUST_BE_FLOAT = 3296;
	public static final int ANNOTATION_MUST_BE_DECIMAL = 3297;
	public static final int ANNOTATION_VALUE_NOT_COMPAT = 3298;
	public static final int IMPORT_COLLISION = 3328;
	public static final int PART_CANNOT_HAVE_DASH = 3344; 
	public static final int INVALID_SUBSCRIPT_OR_SUBSTRING = 3345; 
	public static final int INVALID_REAL_OR_FLOAT_IN_NAME = 3346; 
	public static final int REFERENCE_CANNOT_CONTAIN_SUBSTRING = 3348;
	public static final int INVALID_NAME_TOKEN_SEQUENCE = 3349;
	public static final int TYPEDEF_CANNOT_BE_SUBSCRIPTED = 3352;
	public static final int IDENTIFIER_CANNOT_BE_SUBSCRIPTED = 3355;
	public static final int PARTREFERENCE_CANNOT_BE_SUBSCRIPTED = 3356;
	public static final int RECORD_FILENAME_CANNOT_BE_SUBSCRIPTED = 3357;
	public static final int PART_CANNOT_BE_SUBSCRIPTED = 3358;
	public static final int PART_CANNOT_BE_QUALIFIED = 3359;
	public static final int SUBSTRING_INDEX_NOT_INTEGER = 3363;
	public static final int ALIAS_CANNOT_BE_QUALIFIED = 3365;
	public static final int IDENTIFIER_CANNOT_BE_QUALIFIED = 3366;
    public static final int FUNCTION_CANT_HAVE_PARMS = 3385;
    public static final int FUNCTION_REQUIRES_RETURN_TYPE = 3386;
    public static final int FUNCTION_MUST_HAVE_ONE_PARM = 3387;
    public static final int FUNCTION_PARM_MUST_BE_IN = 3388;
    public static final int FUNCTION_CANT_HAVE_RETURN_TYPE = 3389;
	public static final int INTERFACE_FUNCTION_MISSING = 3400;
	public static final int MAIN_FUNCTION_CANNOT_BE_ASSIGNED_TO_DELEGATE = 3418;
	public static final int PART_DEFINITION_REQUIRES_TYPE_CLAUSE = 3420;
	public static final int EXTERNALTYPE_MUST_EXTEND_EXTERNALTYPE = 3421;
	public static final int CANNOT_WRITE_TO_EXTERNALTYPE_FIELD_WITH_NO_SETTER = 3423;
	public static final int CANNOT_READ_FROM_EXTERNALTYPE_FIELD_WITH_NO_GETTER = 3424;
	public static final int INTERFACE_MUST_EXTEND_INTERFACE = 3427;
	public static final int INVOCATION_MUST_BE_IN_TRY = 3428;
	public static final int TYPE_NOT_INSTANTIABLE = 3433;
	public static final int PRIVATE_CONSTRUCTOR = 3434;
	public static final int SETTING_NOT_ALLOWED = 3436;
	public static final int SETTING_NOT_ALLOWED_NULL = 3437;
	public static final int POSITIONAL_PROPERTY_NOT_ALLOWED_WITH_INITIAL_SIZE = 3439;
	public static final int TYPE_IN_CATCH_BLOCK_NOT_EXCEPTION = 3440;
	public static final int DUPLICATE_ONEXCEPTION_EXCEPTION = 3441;
	public static final int THROW_TARGET_MUST_BE_EXCEPTION = 3444;
	public static final int EXTERNAL_TYPE_SUPER_SUBTYPE_MISMATCH = 3446;
	public static final int PART_OR_STATEMENT_NOT_SUPPORTED = 3458;
	public static final int EXTERNAL_FUNCTION_NOT_ALLOWED_FOR_PROPERTY = 3461;
	public static final int STATEMENT_NOT_SUPPORTED = 3463;
	public static final int STATEMENT_NOT_EXTENDED = 3464;
	public static final int MULTIPLE_OVERLOADED_FUNCTIONS_MATCH_ARGUMENTS = 3468;
	public static final int NO_FUNCTIONS_MATCH_ARGUMENTS = 3469;
	public static final int ENUMERATION_CONSTANT_INVALID = 3470;
	public static final int ENUMERATION_CONSTANT_DUPLICATE= 3471;
	public static final int MULTI_INDICES_NOT_SUPPORTED= 3472;
	public static final int ENUMERATION_NO_FIELDS= 3473;
	public static final int CLASS_MUST_EXTEND_CLASS = 3474;
	public static final int PART_CANNOT_EXTEND_ITSELF = 3475;
	public static final int THROWS_NOT_VALID_HERE= 3480;
	public static final int STEREOTYPE_TYPE_REQUIRED = 3512;
	public static final int STEREOTYPE_NO_PARMS = 3514;
	public static final int STEREOTYPE_BAD_TYPE = 3515;
	public static final int INVALID_LENGTH_FOR_PARAMETERIZED_TYPE = 4401;
	public static final int INVALID_DECIMALS = 4402;
	public static final int DECIMALS_GREATER_THAN_LENGTH = 4403;
	public static final int NEGATIVE_LENGTH_INVALID = 4412;
 	public static final int COULD_NOT_BUILD_DEFAULT_STATEMENT = 4515; //$NON-NLS-1$
	public static final int IO_OBJECT_CONTAINS_NO_STRUCTURE_ITEMS = 4530; //$NON-NLS-1$
 	public static final int IO_OBJECT_CONTAINS_NO_READ_WRITE_COLUMNS = 4531; //$NON-NLS-1$
 	public static final int IO_OBJECT_IS_SQL_JOIN = 4532; //$NON-NLS-1$
 	public static final int IO_OBJECT_CONTAINS_ONLY_KEY_OR_READONLY_COLUMNS = 4533; //$NON-NLS-1$
	public static final int PROGRAM_MAIN_FUNCTION_REQUIRED = 4922;
	public static final int MAIN_FUNCTION_HAS_PARAMETERS = 4924;
	public static final int DATA_DECLARATION_HAS_INCORRECT_TYPE = 4926;
	public static final int USE_STATEMENT_RESOLVES_TO_INVALID_TYPE = 4930;
	public static final int CONST_MODIFIER_NOT_ALLOWED_WITH_OUT_MODIFIER = 4958;
    public static final int EXTERNALTYPE_PARM_CANNOT_BE_CONST = 4961;
    public static final int SERVICE_PARM_CANNOT_BE_CONST = 4962;
	public static final int IS_NOT_UNSUPPORTED = 5009;
	public static final int FOR_STATEMENT_COUNTER_MUST_BE_INT = 5035;
	public static final int FOR_STATEMENT_EXPR_MUST_BE_INT = 5036;
	public static final int FOREACH_ARRAY_MUST_DECLARE_VARIABLE = 5037;
	public static final int FOREACH_SOURCE_MUST_BE_ARRAY = 5038;
	public static final int RECURSIVE_LOOP_IN_EXTENDS = 5045;   		
	public static final int CONTINUE_STATEMENT_LOCATION = 5054;
	public static final int INVALID_CONTINUE_EXIT_MODIFIER = 5055;
	public static final int INVALID_CONTINUE_EXIT_LABEL = 5056;
	public static final int RECURSIVE_LOOP_STARTED_WITHIN_FLEXIBLE_RECORD_BY_TYPEDEF = 5057;
	public static final int CASE_WHEN_MUST_BE_BOOLEAN_EXPRESSION = 5078; 
	public static final int TYPE_INCOMPATIBLE_ARITHMETIC_COMPARISON = 5089;
	public static final int ROUTINE_MUST_HAVE_X_ARGS = 5109;
	public static final int INVALID_EXPRESSION_DATA_ACCESS_OR_STRING_LITERAL = 5188;
	public static final int L_STRING_NOT_CLOSED = 5202;
	public static final int L_TOO_MANY_DIGITS = 5203;
	public static final int P_FOUND_RBRACKET_WRONG = 5205;		
	public static final int P_MISSING_FROM_SUBSTRING = 5206;
	public static final int P_TOO_MANY_SUBSTRINGS = 5207;				
	public static final int P_UNRECOGNIZED_TOKEN = 5208;						
	public static final int P_SUBSCRIPT_OR_SUBSTRING_NOT_CLOSED = 5209;
	public static final int P_SUBSTRING_NOT_LAST = 5210;		
	public static final int P_FOUND_EMPTY_BRACKETS = 5211;
	public static final int MISSING_OPERATION_FOR_BINARY_EXPRESSION = 5214;
	public static final int MISSING_OPERATION_FOR_SUBSTRING = 5215;
	public static final int MISSING_OPERATION_FOR_UNARY_EXPRESSION = 5216;
	public static final int PROPERTY_ONLY_VALID_FOR_PRIMITIVE_TYPE = 5414;
    public static final int PROXY_FUNCTIONS_CANNOT_HAVE_STMTS = 5704;
	public static final int DUPE_OPTION = 6510;
	public static final int SUBSCRIPT_MUST_BE_INTEGER_ITEM = 6583;
	public static final int DOT_ACCESS_USED_AFTER_DYNAMIC = 6584;
	public static final int ARRAY_ACCESS_NOT_SUBSCRIPTED = 6585;
	public static final int NON_ARRAY_ACCESS_SUBSCRIPTED = 6586;
	public static final int NON_DYNAMIC_ACCESS_ACCESSED_DYNAMICALLY = 6588;
	public static final int RETURN_VALUE_WO_RETURN_DEF = 6608;		
	public static final int VARIABLE_NOT_FOUND = 6619;
	public static final int VARIABLE_ACCESS_AMBIGUOUS = 6620;
	public static final int GOTO_LABEL_IS_UNDEFINED = 6624;
	public static final int DUPLICATE_LABEL = 6625;
	public static final int EXIT_PROGRAM_ITEM_NOT_INTEGER = 6628;
	public static final int CANNOT_MODIFY_CONSTANT = 6646;
	public static final int SUBSTRING_IMMUTABLE = 6649;
	public static final int ASSIGNMENT_STATEMENT_TYPE_MISMATCH = 6653;
	public static final int FUNCTION_MUST_RETURN_TYPE = 6654;
	public static final int FUNCTION_ARG_LITERAL_NOT_VALID_WITH_INOUT_PARAMETER = 6673;		
	public static final int FUNCTION_ARG_REQUIRES_IN_PARAMETER = 6674;
	public static final int FUNCTION_ARG_LITERAL_NOT_VALID_WITH_OUT_PARAMETER = 6675;
	public static final int RETURN_STATEMENT_TYPE_INCOMPATIBLE = 6691;
	public static final int FUNCTION_ARG_NOT_REFERENCE_COMPATIBLE_WITH_PARM = 6716;
	public static final int EXIT_MODIFIER_ONLY_ALLOWED_IN_PROGRAM = 6726;
	public static final int EXIT_MODIFIER_NOT_ALLOWED_IN_SERVICE = 6727;
	public static final int GOTO_LABEL_NOT_ACCESSIBLE = 6728;	
	public static final int FUNCTION_ARG_NOT_ASSIGNMENT_COMPATIBLE_WITH_PARM = 6731;
	public static final int FUNCTION_REFERENCE_CANNOT_BE_RESOLVED = 6746;
	public static final int FUNCTION_INVOCATION_TARGET_NOT_FUNCTION_OR_DELEGATE = 6751;
	public static final int FUNCTION_ARG_CANNOT_BE_THIS = 6754;
	public static final int CONSTRUCTOR_CALL_WRONG_PLACE = 6756;
	public static final int FUNCTION_ARG_CANNOT_BE_SUPER = 6758;
	public static final int POSITIONAL_PROPERTY_NOT_VALID_FOR = 7510;
	public static final int SET_VALUES_BLOCK_NOT_VALID_AS_FUNC_ARG = 7511;
	public static final int SET_VALUES_BLOCK_NOT_VALID_AS_CASE_CRITERION = 7513;
	public static final int SET_VALUES_BLOCK_NOT_VALID_AS_WHEN_CLAUSE = 7514;
	public static final int SET_VALUES_BLOCK_NOT_VALID_AS_RETURN_ARG = 7516;
	public static final int DATETIME_PATTERN_FIRST_INTERVAL_FIELD_TOO_LONG = 7624;
	public static final int DATETIME_PATTERN_HAS_INVALID_CHARACTER = 7625;
	public static final int DATETIME_PATTERN_OUT_OF_ORDER = 7626;
	public static final int DATETIME_PATTERN_YEAR_FIELD_TOO_LONG = 7627;
	public static final int DATETIME_PATTERN_MONTH_FIELD_TOO_LONG = 7628;
	public static final int DATETIME_PATTERN_DAY_FIELD_TOO_LONG = 7629;
	public static final int DATETIME_PATTERN_HOUR_FIELD_TOO_LONG = 7630;
	public static final int DATETIME_PATTERN_MINUTES_FIELD_TOO_LONG = 7631;
	public static final int DATETIME_PATTERN_SECONDS_FIELD_TOO_LONG = 7632;
	public static final int DATETIME_PATTERN_SECOND_FRACTIONS_FIELD_TOO_LONG = 7633;
	public static final int DATETIME_PATTERN_MISSING_INTERMEDIATE_FIELD = 7634;
	public static final int DATETIME_PATTERN_EMPTY = 7635;
	public static final int DATETIME_PATTERN_INVALID_INTERVAL_SPAN = 7636;
	public static final int UNREACHABLE_CODE = 7755;
	public static final int PART_MUST_IMPLEMENT_AN_INTERFACE = 7801;
	public static final int TYPE_NOT_ALLOWED_IN_SERVICE_OR_PROXY_FUNC_PARM = 7804;
	public static final int TYPE_NOT_ALLOWED_AS_SERVICE_OR_PROXY_FUNC_RETURN = 7805;
	public static final int FIELD_NOT_ALLOWED_IN_SERVICE_OR_PROXY_FUNC_PARM = 7806;
	public static final int INTERFACE_FUNCTION_CANNOT_BE_PRIVATE = 7810;		
	public static final int IN_MODIFIER_REQUIRED_FOR_JAVAOBJECT_FUNCTION_PARAMETERS = 7824;
	public static final int IN_MODIFIER_REQUIRED_FOR_JAVASCRIPTOBJECT_FUNCTION_PARAMETERS = 7826;
	public static final int MATCHING_CONSTRUCTOR_CANNOT_BE_FOUND = 7830;
    public static final int FUNCTION_NOT_VALID_AS_LVALUE = 7954;
}
