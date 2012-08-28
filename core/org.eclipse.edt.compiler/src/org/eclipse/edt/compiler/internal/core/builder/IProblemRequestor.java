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
	public static final int ANNOTATION_NOT_FOUND = 2059; 
	public static final int ANNOTATION_VALUE_MUST_BE_NAME = 2060; 
	public static final int ANNOTATION_VALUE_MUST_BE_SIMPLENAME = 2061; 
	public static final int TABLE_NAME_VARIABLE_MUST_BE_NAME_EXPR = 2062; 
	public static final int SELECTEDINDEXITEM_MUST_BE_IN_RECORD  = 2063;
	public static final int ANNOTATION_VALUE_MUST_BE_NAME_ARRAY = 2064;
	public static final int NOT_AN_ANNOTATION = 2065;
	public static final int ONLY_STRING_FIELDS_ALLOWED = 2066;
	
	
		
//Also see EGLMessage for messages 3000 - 3030			
	public static final int COMPILATION_EXCEPTION = 3000;
	public static final int INVALID_NAME_LENGTH = 3001;
	public static final int INVALID_CHARACTER_IN_NAME = 3002;
	public static final int EZE_NOT_ALLOWED = 3003;
	public static final int CONTEXT_SPECIFIC_COMPILATION_EXCEPTION = 3004;
	public static final int NONNUMERIC_WITH_DECIMALS = 3005;
//	public static final int WORKING_STORAGE_NOT_IN_LIST = 3006;
		public static final int INVALID_SUBSCRIPT_SPECIFIED = 3007;
//	public static final int DUPLICATE_PART_NAME = 3009;
	public static final int DUPLICATE_VARIABLE_NAME = 3010;
	public static final int DUPLICATE_PARAMETER_NAME = 3011;
	public static final int DUPLICATE_NAME_ACROSS_LISTS = 3012;
//	public static final int INVALID_IMPORT_STATEMENT = 3013;
//	public static final int DUPLICATE_IMPORT_STATEMENT = 3014;
	public static final int INVALID_NUMBER_OF_PARAMETERS = 3015;
	public static final int PARAMETER_HAS_WRONG_TYPE = 3016;

//	public static final int INVALID_SQL_ITEM_PARM_TYPE = 3018;
	public static final int RESERVED_WORD_NOT_ALLOWED = 3019;
//	public static final int IMPORT_FILE_NOT_FOUND = 3020;
//	public static final int IMPORT_FOLDER_NOT_FOUND = 3021;
	//more import error messages at 3325
	
	public static final int DUPLICATE_NAME_IN_FILE = 3022;
	public static final int ANNOTATION_REQIRED_WITH_ANNOTATION = 3027;
	
	public static final int FUNCTION_TOO_MANY_PARMS = 3028;
		
//Also see EGLMessage for messages 3000 - 3030		
	public static final int DUPLICATE_USE_NAME = 3031;
	public static final int DUPLICATE_CONSTANT_NAME = 3032;
	public static final int SUBSCRIPT_CLOSED_AT_INVALID_LOCATION = 3033;
	public static final int SUBSCRIPT_NOT_CLOSED = 3034;
	public static final int INVALID_SUBSCRIPT_NESTING = 3035;
	public static final int TOO_MANY_ITEMS_IN_SUBSCRIPT = 3036;
	public static final int TYPE_CANNOT_BE_SUBSCRIPTED = 3037;

	//continued at 3042
	
	public static final int DUPLICATE_PART_NAME_IN_FILE = 3038;
	public static final int DUPLICATE_NAME_IN_NAMESPACE = 3039;
	public static final int GENERATABLE_PART_NAME_MUST_MATCH_FILE_NAME = 3040;
	public static final int ONLY_ONE_GENERATABLE_PART_PER_FILE = 3041;
	
	//continued from 3037
	public static final int TYPE_CANNOT_BE_QUALIFIED = 3042;
	public static final int INVALID_EMPTY_SUBSCRIPT= 3043;
	//additional CANNOT_BE_QUALIFIED messagse are continued at 3350
	
	
	//MORE EGLFILE ERROR MESSAGES
	public static final int MULTIPLE_PAGEHANDLERS_IN_FILE = 3045;
	public static final int PAGEHANDLER_AND_PROGRAM_IN_FILE = 3046;
	public static final int PAGEHANDLER_AND_FORMGROUP_IN_FILE = 3047;
	public static final int PAGEHANDLER_NAME_INVALID_WHEN_COMPARTED_TO_FILE_NAME = 3048;
	public static final int PACKAGE_NAME_DOESNT_MATCH_DIRECTORY_STRUCTURE = 3049;
	//continued at 3065

	public static final int INVALID_NESTING_OF_PROPERTIES = 3050;
	public static final int INVALID_NESTING_TOO_DEEP = 3051;
	public static final int UNSUPPORTED_PROPERTY = 3052;
	public static final int PROPERTY_REQUIRES_VALUE = 3053;		
	public static final int PROPERTY_REQUIRES_SPECIFIC_VALUE = 3054;				
	public static final int PROPERTY_REQUIRES_NUMERIC_ARRAY_VALUE = 3055;						
	public static final int PROPERTY_REQUIRES_STRING_ARRAY_VALUE = 3056;
	public static final int PROPERTY_REQUIRES_STRING_ARRAY_ARRAY_VALUE = 3057;
	public static final int PROPERTY_REQUIRES_NUMERIC_ARRAY_ARRAY_VALUE = 3058;
	public static final int PROPERTY_REQUIRES_NUMERIC_VALUE = 3059;										
	public static final int PROPERTY_REQUIRES_STRING_VALUE = 3060;
	public static final int PROPERTY_CANT_HANDLE_SQL_STRING_VALUE = 3061;
	public static final int PROPERTY_REQUIRED_WITH_THIS_PART_TYPE = 3062;
	public static final int PROPERTY_NOT_ALLOWED_WITH_THIS_RECORD_TYPE = 3063;
	public static final int PACKAGE_NAME_NOT_PROVIDED = 3064;
	
	//continued from 3049
	public static final int DEFAULT_PACKAGE_NAME_USED_IMPROPERLY = 3065;
	public static final int WHITESPACE_NOT_ALLOWED = 3066;
	public static final int STATIC_ARRAY_PARAMETER_DEFINITION = 3067;
	public static final int PROPERTIES_NOT_ALLOWED_IN_LIST_USE_DECLARATION = 3068;
	public static final int STATIC_ARRAY_PGM_PARAMETER_DEFINITION = 3069;

	public static final int PROPERTIES_MUTUALLY_EXCLUSIVE = 3070;				
	public static final int PROPERTY_REQUIRES_SPECIFIC_VALUE_OR_LIST = 3071;				
	public static final int PROPERTY_REQUIRES_SPECIFIC_VALUE_OR_ARRAY = 3072;						
	public static final int PROPERTY_REQUIRES_SPECIFIC_VALUE_OR_NAME = 3073;						
	public static final int PROPERTY_REQUIRES_SPECIFIC_VALUE_OR_INTEGER = 3074;
	public static final int PROPERTY_REQUIRES_SPECIFIC_VALUE_OR_QUOTED_STRING = 3075;										
	public static final int PROPERTY_REQUIRES_SPECIFIC_VALUE_OR_LITERAL = 3076;
	public static final int PROPERTY_VALUE_INVALID = 3077;
	public static final int PROPERTY_REQUIRES_LITERAL_ARRAY_VALUE = 3078;
	public static final int PROPERTY_REQUIRES_LIST_VALUE = 3079;
	public static final int PROPERTY_REQUIRES_SQL_STRING_VALUE = 3080;
	public static final int PROPERTY_ONLY_ALLOWED_ON_FIRST_ELEMENT_OF_ARRAY = 3081;
    public static final int PROPERTY_IS_REQUIRED_IN_NATIVE_LIBRARY = 3082;
    public static final int PROPERTY_REQUIRES_INTEGER_GREATER_THAN_ZERO = 3083;
    public static final int PROPERTY_REQUIRES_LITERAL_STRING_ARRAY_VALUE = 3084;
    public static final int PROPERTY_REQUIRES_INTEGER = 3085;
    public static final int PROPERTY_REQUIRES_LITERAL_INTEGER_ARRAY_VALUE = 3086;
    public static final int PROPERTY_REQUIRES_LITERAL_INTEGER_ARRAY_ARRAY_VALUE = 3087;
    public static final int PROPERTY_REQUIRES_LENGTH = 3089;
    
    public static final int PROPERTY_REQUIRES_ARRAY_OF_SPECIFIC_VALUE = 30543088;
	
	public static final int PACKAGE_NAME_CANNOT_START_OR_END_WITH_DOTS_OR_WHITESPACE = 3090;
	public static final int PACKAGE_NAME_CANNOT_BE_NULL = 3091;
	public static final int PACKAGE_NAME_CANNOT_BE_EMPTY = 3092;
	public static final int PACKAGE_NAME_CANNOT_CONTAIN_CONSECUTIVE_DOTS = 3093;
	public static final int INVALID_PROP_ON_FILLER_OR_EMBED = 3094;	
	public static final int PROPERTY_CANNOT_BE_RESOLVED = 3095;
	public static final int PROPERTY_IS_AMBIGUOUS = 3096;
	
	//Program Properties Messages
	public static final int PROPERTY_MSGTABLEPREFIX_INVALID_LENGTH = 3097;		
	public static final int PROPERTY_INVALID_PRIMITIVE_LENGTH_USED_WITH_ISNULLABLE_PROPERTY = 3098;
	public static final int PROPERTY_INVALID_SQLDATACODE_AND_PRIMITIVE_COMBINATION = 3099;
	public static final int PROPERTY_INVALID_PRIMITIVE_USED_WITH_SQLDATACODE_PROPERTY = 3100;
	public static final int PROPERTY_EXCEEDS_ALLOWED_LENGTH = 3101;
	public static final int PROPERTY_INVALID_PRIMITIVE_USED_WITH_ISNULLABLE_PROPERTY = 3102;
	public static final int PROPERTY_MINIMUM_INPUT_MUST_BE_LESS_THAN_PRIMITIVE_LENGTH = 3103;
	public static final int PROPERTY_MUST_RESOLVE_TO_FORM = 3104;
	public static final int PROPERTY_MUST_RESOLVE_TO_FORM_BUT_IS_AMBIGUOUS = 3105;
	public static final int PROPERTY_MUST_RESOLVE_TO_DATA_DECLARATION_OF_UI_RECORD = 3106;
	public static final int PROPERTY_MUST_RESOLVE_TO_DATA_DECLARATION_OF_RECORD = 3107;
	public static final int PROPERTY_MUST_RESOLVE_TO_DATA_DECLARATION = 3108;
	public static final int PROPERTY_MUST_RESOLVE_TO_DATA_DECLARATION_BUT_IS_AMBIGUOUS = 3109;
	public static final int PROPERTY_INPUT_PAGE_RECORD_COMBO = 3110;
	public static final int PROPERTY_CALLED_PROGRAM_INPUT_RECORD = 3111;

	public static final int PROPERTY_DUPLICATE_PROPERTIES_FOUND = 3112;
	//more at 3225
	
	public static final int ACTUAL_REQUIRED_LENGTH_TOO_LARGE = 3113;
	public static final int REDEFINES_MUST_BE_DECLARATION = 3114;
	public static final int REDEFINES_CANNOT_BE_REDEFINES = 3115;
	public static final int REDEFINES_SIZE_MISMATCH = 3116;
	public static final int REDEFINES_INITIALIZED_INCOMPATIBLE = 3117;
	public static final int RECORD_PARAMETER_WITH_NO_CONTENTS = 3118;
	public static final int PROGRAM_PARAMETER_OF_TYPE_ANY = 3119;
    public static final int INVALID_TYPE_USED_FOR_THIS_PROPERTY = 3120;
    public static final int PROPERTY_ONLY_VALID_FOR_PRIMITIVE_LIST = 3121;
	public static final int REDEFINING_MUST_BE_FIXED_RECORD = 3122;
	public static final int PROPERTY_REQUIRES_NONDECIMAL_DIGITS = 3123;
	public static final int REDEFINES_MUST_FOLLOW = 3124;
	public static final int REDEFINES_TARGET_IS_ARRAY = 3125;
	public static final int REDEFINER_AND_REDEFINED_MUST_BE_DECLARED_IN_SAME_PART = 3126;
	public static final int EXPRESSION_NOT_VALID_FOR_PROPERTY = 3127;
	public static final int CANNOT_SET_VALUE_OF_MULTI_VALUE_PROPERTY = 3128;
	
	public static final int PROPERTY_MUST_RESOLVE_TO_DATA_DECLARATION_OF_FLEX_RECORD = 3129;

	
	public static final int SELECTFROMLIST_MUST_BE_ARRAY  = 3130;
	
	public static final int SELECTTYPE_ITEM_MUST_MATCH_ARRAY = 3132;
	public static final int SELECTTYPE_REQUIRES_SELECTFROMLIST = 3133;	
	public static final int PROP_REQUIRES_DISPLAYUSE_BUTTON_OR_HYPERLINK = 3134;
	public static final int NEWWINDOW_REQUIRES_ACTION = 3135;
		public static final int NUMELEMENTSITEM_INVALID_FOR_DYNAMIC_ARRAY = 3136;
	public static final int NUMELEMENTSITEM_MUST_BE_IN_RECORD  = 3137;
	public static final int NUMELEMENTSITEM_MUST_BE_NUMERIC = 3138;
	public static final int NUMELEMENTSITEM_ITEM_MUST_BE_ARRAY = 3139;
	public static final int DUPLICATE_VALIDATION_ORDER_VALUES_FOUND = 3140;
	public static final int INVALID_PROPERTY_VALUE_FOR_ITEM_TYPE = 3141;
	public static final int SELECTTYPE_TARGET_MUST_BE_INT = 3142;	

	public static final int PROPERTY_MUST_NOT_RESOLVE_TO_LIBRARY_FUNCTION= 3169;
	public static final int PROPERTY_CERTAIN_PRIMITIVE_REQUIRED = 3170;
	public static final int PROPERTY_INVALID_CHARACTER_IN_DATEFORMAT = 3171;
	public static final int PROPERTY_DATEFORMAT_INVALID_PRIMITIVE_LENGTH_DATEFORMAT_MASK = 3172;
	public static final int PROPERTY_DATEFORMAT_INVALID_PRIMITIVE_LENGTH = 3173;
	public static final int PROPERTY_DATEFORMAT_INVALID_PRIMITIVE_TYPE = 3174;
	public static final int PROPERTY_DATEFORMAT_INVALID_DECIMALS = 3175;
	public static final int PROPERTY_DATEFORMAT_INVALID_INCOMPATIBLE_PROPERTIES = 3176;
	public static final int PROPERTY_INVALID_VALUE_DATEFORMAT_GREGORIAN_AND_JULIAN = 3177;
	public static final int PROPERTY_INVALID_VALUE_DATEFORMAT_MUST_MATCH_FIELDLEN = 3178;
	public static final int PROPERTY_INVALID_VALUE_DATEFORMAT = 3179;
	public static final int PROPERTY_ONLY_VALID_WHEN_IN_VAGCOMPATABILITY_MODE= 3180;
	public static final int PROPERTY_MINIMUM_INPUT_MUST_BE_GREATER_THAN_ZERO= 3181;
	public static final int PROPERTY_LENGTH_EXCEEDS_DEFINED_LENGTH = 3182;	
	public static final int PROPERTY_MUST_RESOLVE_TO_FUNCTION= 3183;
	public static final int PROPERTY_VALUES_FOR_OUTLINE_PROPERTY_INVALID = 3184;
	public static final int PROPERTY_ONLY_DBCHARS_ALLOWED = 3185;
	public static final int PROPERTY_NUMERIC_PRIMITIVE_USED_WITH_NONNUMERIC_VALUE = 3186;
	public static final int PROPERTY_INVALID_VALIDATORTABLE_TABLE_TYPE = 3187;

	public static final int PROPERTY_INVALID_VALUE_FOR_HIGH_VALUE_RANGE_PROPERTY = 3188;
	public static final int PROPERTY_NUMERIC_PROPERTY_WITH_NONNUMERIC_VALUE = 3189;
	public static final int PROPERTY_INVALID_VALUE_FOR_LOW_VALUE_RANGE_PROPERTY = 3190;
	public static final int PROPERTY_RANGE_LOW_GREATHER_THAN_HIGH_VALUE = 3191;
	public static final int PROPERTY_RANGE_LOW_OR_HIGH_NOT_NUMERIC_VALUE = 3192;
	public static final int PROPERTY_STRING_PRIMITIVE_REQUIRED = 3193;
	public static final int PROPERTY_NUMERIC_PRIMITIVE_REQUIRED = 3194;
	public static final int PROPERPTY_MUST_RESOLVE_TO_DATATABLE = 3195;
	public static final int PROPERTY_ONLY_MBCHARS_ALLOWED = 3196;
	public static final int PROPERTY_MUST_RESOLVE_TO_FUNCTION_BUT_IS_AMBIGUOUS = 3197;
	public static final int PROPERTY_MUST_RESOLVE_TO_DATATABLE_BUT_IS_AMBIGUOUS = 3198;
	public static final int PROPERTY_MANTISSA_EXCEEDS_DEFINED_LENGTH = 3199;
	public static final int PROPERTY_DECIMALS_EXCEEDS_DEFINED_LENGTH = 3200;
	public static final int PROPERTY_CANT_HAVE_DECIMALS = 3201;	

	public static final int PROPERTY_KEY_ITEM_MUST_BE_IN_INDEXED_RECORD = 3202;	
    public static final int PROPERTY_INVALID_TYPE_FOR_KEY_ITEM = 3203;
	public static final int KEY_ITEM_VALUE_TOO_LONG= 3204;
	public static final int KEY_ITEM_VALUE_HAS_DECIMALS = 3205;					
	public static final int PROPERTY_KEY_ITEM_MUST_BE_IN_SQL_RECORD = 3206;
	public static final int PROPERTY_INVALID_TYPE_FOR_NUM_ELEMENTS = 3207;
	public static final int NUM_ELEMENTS_ITEM_VALUE_TOO_LONG= 3208;
	public static final int NUM_ELEMENTS_ITEM_VALUE_HAS_DECIMALS = 3209;			
	public static final int NUM_ELEMENTS_HAS_AN_INVALID_NUMBER_OF_OCCURS = 3210;		
	public static final int NUM_ELEMENTS_MUST_BE_A_SINGLE_DIMENSIONAL_ARRAY = 3211;		
	public static final int NUM_ELEMENTS_ITEM_CANNOT_HAVE_SAME_NAME_AS_LAST_ITEM = 3212;
	public static final int NUM_ELEMENTS_ITEM_NAME_CANNOT_BE_IN_LAST_ARRAY = 3213;
    public static final int ITEM_REFERENCED_BY_NUM_ELEMENTS_MUST_BE_IN_RECORD = 3214;		
	public static final int PROPERTY_SELECTED_ELEMENT_MUST_BE_IN_RECORD = 3215;
	public static final int ITEM_REFERENCED_BY_COMMAND_VALUE_ITEM_MUST_BE_IN_RECORD = 3216;
	public static final int NUM_ELEMENTS_REQUIRES_AN_ARRAY = 3217;
	public static final int PROPERTY_INVALID_TYPE_FOR_RECORD_ITEM_PROPERTY = 3218; 
	public static final int RECORD_ITEM_PROPERTY_VALUE_TOO_LONG = 3219;
	public static final int RECORD_ITEM_PROPERTY_VALUE_HAS_DECIMALS = 3220;
	public static final int RECORD_ITEM_PROPERTY_VALUE_MUST_BE_DEFINED = 3221;
	public static final int RECORD_ITEM_PROPERTY_VALUE_MUST_BE_UNIQUE = 3222;
	public static final int PROPERTY_INVALID_UNKNOWN_TYPE_FOR_RECORD_ITEM_PROPERTY = 3223; 
			
	public static final int INTEGER_TOO_LARGE = 3224;		
			
	//continued from 3112
		public static final int PROPERTY_DUPLICATE_PROPERTIES_ON_STRUC_ITEM_FOUND = 3225;		
	public static final int PROPERTY_DUPLICATE_PROPERTIES_TOP_LEVEL_ITEM_FOUND = 3226;						
	public static final int PROPERTY_NO_FORMGROUPS_IN_PROGRAM_WITH_INPUTFORM = 3227;	
	public static final int PROPERTY_NO_FORM_FOUND_MATCHING_INPUTFORM = 3228;	
	public static final int PROPERTY_INVALID_FIELDLEN_VALUE = 3229;	
   
    public static final int ARRAY_DICTIONARY_HAS_INVALID_COLUMNS = 3231;
    
    public static final int BIRT_FUNCTION_NEEDS_RETURN = 3232;
    public static final int BIRT_FUNCTION_HAS_RETURN = 3233;
    public static final int BIRT_FUNCTION_WRONG_NUMBER_PARMS = 3234;
    public static final int BIRT_FUNCTION_PARM_MUST_BE = 3235;
    public static final int BIRT_FUNCTION_PARM_MUST_BE_ONE_OF = 3236;
    public static final int BIRT_FUNCTION_ANN_REQUIRED = 3237;
    public static final int BIRT_FUNCTION_VALUE_MUST_BE_GT_0 = 3238;
    public static final int NAME_MUST_BE_VALID_JAVA_IDENTIFIER = 3239;
    public static final int FIELD_NAME_MUST_BE_VALID_JAVA_IDENTIFIER = 3240;

    public static final int FORWARD_NOT_ALLOWED = 3245;

	public static final int ARRAY_DIMENSION_NOT_ALLOWED = 3248;
	public static final int SETTINGS_BLOCK_NOT_ALLOWED = 3249;
	public static final int INTEGER_LITERAL_OUT_OF_RANGE = 3250;
	public static final int DECIMAL_LITERAL_OUT_OF_RANGE = 3251;
	public static final int FLOATING_POINT_LITERAL_OUT_OF_RANGE = 3252;
	public static final int OCCURS_SIZE_NOT_POSITIVE_INTEGER = 3253;
	public static final int MAXSIZE_NOT_POSITIVE = 3254;
	public static final int ARRAY_DIMENSION_SPECIFIED_AFTER_DYNAMIC_DIMENSION = 3255;
	public static final int ARRAY_LITERAL_SIZE_TOO_LARGE = 3256;
	public static final int ARRAY_DIMENSION_SPECIFIED_WITH_TARGET_OF_REF = 3257;
	public static final int ARRAY_PASSED_TO_NON_EGL_PROGRAM = 3258;
	
	public static final int INVALID_NEW_OPERATION_ON_TYPE = 3259;
    public static final int TYPE_CANNOT_BE_RESOLVED = 3260;
	public static final int ISA_TARGET_NOT_ITEM_OR_PART = 3261;
	public static final int TYPE_IS_AMBIGUOUS = 3262;
	public static final int ARRAY_SIZE_LESS_THAN_ZERO = 3263;
	public static final int ARRAY_SIZE_NOT_ALLOWED_IN_ISA_OR_AS = 3264;
	public static final int HEX_LITERAL_LENGTH_MUST_BE_EVEN = 3265;
	public static final int HEX_LITERAL_LENGTH_MUST_BE_MULTIPLE_OF_FOUR = 3266;
	public static final int USER_FIELD_NAME_CONFLICTS_WITH_IMPLICIT_FIELD_NAME = 3267;
	public static final int VALIDATOR_FUNCTION_HAS_PARAMETERS = 3268;
	public static final int TYPEAHEAD_FUNCTION_BAD_SIGNATURE = 3269;
	public static final int TYPEAHEAD_REQUIRES_PROPERTIES = 3270;
	public static final int VALIDVALUES_RANGE_WITH_TYPEAHEAD = 3271;
	public static final int VALDATATABLE_TYPE_INVALID_FOR_TYPEAHEAD = 3272;
	public static final int TYPE_INVALID_FOR_TYPEAHEAD = 3273;
	public static final int TYPE_VALIDATOR_FUNCTION_NOT_VALID_FOR_VGUIRECORD = 3274;
	public static final int NULLABLE_INVALID_IN_ISA_AS_OR_NEW = 3275;
	public static final int VALIDATION_PROPERTIES_LIBRARY_WRONG_TYPE = 3276;
	public static final int BYTES_LITERAL_LENGTH_MUST_BE_EVEN = 3277;
	public static final int BIGINT_LITERAL_OUT_OF_RANGE = 3278;
	public static final int SMALLINT_LITERAL_OUT_OF_RANGE = 3279;
	public static final int SMALLFLOAT_LITERAL_OUT_OF_RANGE = 3280;
	public static final int TYPE_IS_NOT_PARAMETERIZABLE = 3281;
	public static final int TYPE_ARG_NOT_VALID = 3282;
	public static final int TYPE_ARGS_INVALID_SIZE = 3283;
	
	public static final int PUBLISHHELPER_FUNCTION_INVALID = 3320;	
	public static final int RETRIEVEVEVIEWHELPER_FUNCTION_INVALID = 3321;	
	public static final int RUI_ONCONSTRUCTOR_FUNCTION_INVALID = 3322;	
	public static final int PUBLISHMESSAGEHELPER_FUNCTION_INVALID = 3323;	
	public static final int RETRIEVEVALIDSTATEHELPER_FUNCTION_INVALID = 3324;	
	
	public static final int IMPORT_STATEMENT_PACKAGE_NAME_COULD_NOT_BE_RESOLVED = 3325;	
	public static final int IMPORT_STATEMENT_PART_NAME_IS_AMBIGUOUS = 3326;	
	public static final int IMPORT_STATEMENT_PART_NAME_COULD_NOT_BE_LOCATED = 3327;	
	public static final int IMPORT_COLLISION = 3328;
				
	//continued from 3042
	public static final int SUBSTRING_CANT_BE_EXPR = 3342; 
	public static final int SUBSCRIPT_CANT_BE_EXPR = 3343; 
	public static final int PART_CANNOT_HAVE_DASH = 3344; 
	public static final int INVALID_SUBSCRIPT_OR_SUBSTRING = 3345; 
	public static final int INVALID_REAL_OR_FLOAT_IN_NAME = 3346; 
	
	public static final int INVALID_USE_OF_THIS_QUALIFIER = 3347;
	public static final int REFERENCE_CANNOT_CONTAIN_SUBSTRING = 3348;
	public static final int INVALID_NAME_TOKEN_SEQUENCE = 3349;
	public static final int FUNCTION_REFERENCE_CANNOT_BE_SUBSCRIPTED = 3350;
	public static final int DATAITEM_REFERENCE_CANNOT_BE_SUBSCRIPTED = 3351;
	public static final int TYPEDEF_CANNOT_BE_SUBSCRIPTED = 3352;
	public static final int ALIAS_CANNOT_BE_SUBSCRIPTED = 3353;
	public static final int MSGTABLEPREFIX_CANNOT_BE_SUBSCRIPTED = 3354;
	public static final int IDENTIFIER_CANNOT_BE_SUBSCRIPTED = 3355;
	public static final int PARTREFERENCE_CANNOT_BE_SUBSCRIPTED = 3356;
	public static final int RECORD_FILENAME_CANNOT_BE_SUBSCRIPTED = 3357;
	public static final int PART_CANNOT_BE_SUBSCRIPTED = 3358;
	public static final int PART_CANNOT_BE_QUALIFIED = 3359;
	public static final int KEYITEM_CANNOT_BE_SUBSCRIPTED = 3360;	
	public static final int LENGTHITEM_CANNOT_BE_SUBSCRIPTED = 3361;
	public static final int SUBSTRING_TARGET_NOT_STRING = 3362;
	public static final int SUBSTRING_INDEX_NOT_INTEGER = 3363;
	public static final int SUBSTRING_EXPRESSION_IN_BAD_LOCATION = 3364;		

	public static final int ALIAS_CANNOT_BE_QUALIFIED = 3365;
	public static final int IDENTIFIER_CANNOT_BE_QUALIFIED = 3366;
	public static final int MSGTABLEPREFIX_CANNOT_BE_QUALIFIED = 3367;
	
	public static final int SCROLL_NOT_ALLOWED_FOR_DB2_TARGET = 3368;
	
	public static final int ALIAS_CANNOT_BE_EMPTY = 3369;
	
	public static final int ARRAYPROPERTY_OUT_OF_RANGE = 3370;
	public static final int ARRAYPROPERTY_NOT_ARRAY = 3371;
	public static final int ARRAYPROPERTY_DYNAMIC_ARRAY = 3372;
	public static final int ARRAYPROPERTY_NOT_INTEGER = 3373;
	public static final int ARRAYPROPERTY_MULTI_DIMENSION = 3374;
	public static final int DIMENSION_MUST_BE_INTEGER = 3375;		
    public static final int CANNOT_OVERRIDE_FLEX_RECORD_PROPERTIES = 3376;
    public static final int FUNCTION_INVOCOATION_NOT_ALLOWED_IN_ARRAY_SUBSCRIPT_IN_INTO_CLAUSE = 3377;
    public static final int DYNAMIC_ACCESS_NOT_ALLOWED_IN_INTO_CLAUSE = 3378;
    
    public static final int PROPERTIESFILE_NAME_CANNOT_CONTAIN_DASH = 3384;
    public static final int FUNCTION_CANT_HAVE_PARMS = 3385;
    public static final int FUNCTION_REQUIRES_RETURN_TYPE = 3386;
    public static final int FUNCTION_MUST_HAVE_ONE_PARM = 3387;
    public static final int FUNCTION_PARM_MUST_BE_IN = 3388;
    public static final int FUNCTION_CANT_HAVE_RETURN_TYPE = 3389;
    
	
	public static final int INTERFACE_FUNCTION_MISSING = 3400;
	public static final int TYPE_NOT_VALID_FOR_DECLARATION_IN_STEREOTYPE = 3410;

	public static final int SYSTEM_FUNCTION_CANNOT_BE_DELEGATED = 3416;
	public static final int FUNCTION_WITH_CONVERSE_CANNOT_BE_DELEGATED = 3417;
	public static final int MAIN_FUNCTION_CANNOT_BE_ASSIGNED_TO_DELEGATE = 3418;
	
	public static final int PART_DEFINITION_REQUIRES_TYPE_CLAUSE = 3420;
	public static final int EXTERNALTYPE_MUST_EXTEND_EXTERNALTYPE = 3421;
	public static final int INITIALIZER_NOT_ALLOWED_FOR_EXTERNALTYPE_FIELD = 3422;
	public static final int CANNOT_WRITE_TO_EXTERNALTYPE_FIELD_WITH_NO_SETTER = 3423;
	public static final int CANNOT_READ_FROM_EXTERNALTYPE_FIELD_WITH_NO_GETTER = 3424;
	public static final int TYPE_INVALID_IN_EXTERNALTYPE = 3425;
	public static final int TYPE_INVALID_IN_EXTERNALTYPE_UNLESS_PROPERTY_SPECIFIED = 3426;
	public static final int INTERFACE_MUST_EXTEND_INTERFACE = 3427;
	public static final int INVOCATION_MUST_BE_IN_TRY = 3428;
	public static final int NO_DEFAULT_CONSTRUCTOR = 3429;
	public static final int NULLABLE_TYPE_NOT_ALLOWED_IN_PART = 3430;
	public static final int NULLABLE_TYPE_NOT_ALLOWED_IN_PROGRAM_PARAMETER = 3431;
	public static final int NULLABLE_TYPE_BASE_MUST_BE_VALUE_TYPE = 3432;
	public static final int TYPE_NOT_INSTANTIABLE = 3433;
	public static final int PRIVATE_CONSTRUCTOR = 3434;
	public static final int TYPE_NOT_INSTANTIABLE_2 = 3435;

	public static final int SETTING_NOT_ALLOWED = 3436;
	public static final int SETTING_NOT_ALLOWED_NULL = 3437;
	public static final int PROPERTY_OVERRIDES_NOT_SUPPORTED = 3438;
	public static final int POSITIONAL_PROPERTY_NOT_ALLOWED_WITH_INITIAL_SIZE = 3439;

	public static final int TYPE_IN_CATCH_BLOCK_NOT_EXCEPTION = 3440;
	public static final int DUPLICATE_ONEXCEPTION_EXCEPTION = 3441;
	public static final int EXCEPTION_FILTER_NOT_VALID_WITH_V60EXCEPTIONCOMPATIBILITY = 3442;
	public static final int EXCEPTION_FILTER_REQUIRED = 3443;
	public static final int THROW_TARGET_MUST_BE_EXCEPTION = 3444;
	public static final int THROW_NOT_VALID_WITH_V60EXCEPTIONCOMPATIBILITY = 3445;
	
	public static final int EXTERNAL_TYPE_SUPER_SUBTYPE_MISMATCH = 3446;
	
	public static final int SIZEINBYTES_ARGUMENT_INVALID = 3450;
	public static final int CONVERT_ARGUMENT_INVALID = 3451;
	public static final int SIZEOF_ARGUMENT_INVALID = 3452;

	public static final int VARIABLE_NOT_FOUND_AS_ITEM_OR_CONTAINER = 3454;	
	public static final int VARIBLE_NEEDS_SYSTEM_LIBRARY_QUALIFIER = 3455;
	public static final int ONPAGELOADFUNCTION_PARAMETER_HAS_INVALID_TYPE = 3456;
	public static final int MUTUALLY_EXLCLUSIVE_CLAUSE_IN_STATEMENT = 3457;
	public static final int PART_OR_STATEMENT_NOT_SUPPORTED = 3458;
	public static final int SYSTEM_PART_NOT_SUPPORTED = 3459;
	public static final int SYSTEM_LIBRARY_NOT_SUPPORTED = 3460;
	public static final int LIBRARY_FUNCTION_NOT_ALLOWED_FOR_PROPERTY = 3461;
	public static final int PROPERTY_NOT_VALID_FOR_TYPES = 3462;

	public static final int STATEMENT_NOT_SUPPORTED = 3463;
	public static final int STATEMENT_NOT_EXTENDED = 3464;
	
	public static final int LENGTH_OF_NONDECIMAL_DIGITS_FOR_CONSTANT_TOO_LONG = 3465;
	public static final int DECIMALS_OF_VALUE_FOR_CONSTANT_TOO_LONG = 3466;
	public static final int SEGMENTS_OR_POSITION_REQUIRED_FOR_CONSOLE_FIELDS = 3467;
	public static final int MULTIPLE_OVERLOADED_FUNCTIONS_MATCH_ARGUMENTS = 3468;
	public static final int NO_FUNCTIONS_MATCH_ARGUMENTS = 3469;

	public static final int ENUMERATION_CONSTANT_INVALID = 3470;
	public static final int ENUMERATION_CONSTANT_DUPLICATE= 3471;
	public static final int MULTI_INDICES_NOT_SUPPORTED= 3472;
	public static final int ENUMERATION_NO_FIELDS= 3473;
	
	public static final int CLASS_MUST_EXTEND_CLASS = 3474;
	public static final int PART_CANNOT_EXTEND_ITSELF = 3475;
	
	public static final int THROWS_NOT_VALID_HERE= 3480;


//3896-3999 ARE IN EGLMESSAGES 					

 	public static final int ERROR_RETRIEVING_FILE_CONTENTS = 3992;
 	public static final int XML_VALIDATION_ERROR_IN_FILE = 3999;

//	//4001 : Used for data structure editor messages (may change)!
//	public static final int NO_SQL_TABLE_DEFINED = 4001;
//	public static final int MISSING_SQL_TABLE = 4002;
//	public static final int DUPLICATE_SQL_TABLE_LABEL = 4003;
	public static final int INVALID_TYPE_IN_FIXED_RECORD = 4004;
	public static final int INVALID_OCCURS_VALUE = 4005;
//	public static final int MISSING_RECORD_ORG_PROPERTIES = 4006;
	public static final int REF_USED_WITH_ANY = 4007;
	public static final int REF_USED_IN_FIXED_RECORD = 4008;
//	public static final int ITEM_NAME_REQUIRED_IF_NOT_TYPEDEF = 4009;
//	public static final int BLANK_ITEMS_CANNOT_HAVE_DATA_CHARACTERISTICS = 4010;
//	public static final int ITEMS_WITH_TYPEDEF_TO_STRUCTURE_MUST_HAVE_BLANK_NAMES_IN_SQL = 4011;
	public static final int FILLER_NOT_ALLOWED_IN_SQL = 4012;
	public static final int SQL_ITEM_MUST_BE_READ_ONLY_IF_JOIN_OR_EXP = 4014;
//	public static final int SQL_ITEM_DATA_CODE_IS_INVALID = 4015;
//	public static final int TOO_MANY_RECORD_ORG_PROPERTIES = 4016;
//	public static final int INVALID_REDEFINES_NAME = 4017;
//	public static final int INVALID_GET_OPTIONS_NAME = 4018;
//	public static final int INVALID_OPEN_OPTIONS_NAME = 4019;
//	public static final int INVALID_PUT_OPTIONS_NAME = 4020;
//	public static final int INVALID_MESSAGE_DESCRIPTOR_NAME = 4021;
//	public static final int INVALID_QUEUE_DESCRIPTOR_NAME = 4022;
//	public static final int SQL_TABLE_LABEL_ENTRY_LENGTH_INVALID = 4023;
	public static final int DEDICATEDSERIVE_VALID_ONLY_FOR_SERVICE = 4024;
	public static final int GETBYPOSITION_POSITION_BAUE_MUST_BE_INTEGER = 4025;
	public static final int FIXED_RECORD_EMBEDDED_IN_FLEXIBLE = 4026;		
	public static final int DYNAMIC_ARRAY_USED_IN_FIXED_RECORD = 4027;
	public static final int BYPOSITION_USED_WITH_FLEXIBLE_RECORD_AND_FORM = 4031;
	public static final int REF_ARRAY_POINTS_TO_NONDYNAMIC_ARRAY = 4032;
	public static final int REF_USED_WITH_PRIMITIVE = 4033;
	public static final int REF_USED_WITH_FIXED_RECORD = 4034;
	public static final int FLEXIBLE_RECORD_FIELD_MISSING_TYPE = 4035;
	public static final int PRIMITIVE_TYPE_NOT_ALLOWED_IN_PART_OF_SUBTYPE = 4036;
	public static final int TYPEAHEAD_MUST_BE_ON_LEAF_ITEM = 4037;
	public static final int OVERRIDING_IMPLICIT_FUNCTION = 4038;
//
	
//	public static final int RECORD_HAS_UNDEFINED_NUMBER_OF_OCCURRENCES_ITEM = 4100;
//	public static final int RECORD_HAS_NUMBER_OF_OCCURRENCES_ITEM_UNDER_VAR_OCCURRING_ITEM = 4102;
//	public static final int RECORD_HAS_FILLER_FOR_VARIABLY_OCCURING_ITEM = 4104;
//	public static final int RECORD_HAS_UNDEFINED_KEY_ITEM = 4106;
//	public static final int REDEFINED_RECORD_HAS_UNDEFINED_REDEFINED_RECORD_ITEM = 4107;
//	public static final int REDEFINED_RECORD_IS_REDEFINING_A_REDEFINED_RECORD = 4108;
	public static final int TRANSFER_TO_TRANSACTION_NOT_SUPPORTED_FOR_BATCH = 4109;
//	public static final int FILE_RECORD_HAS_INVALID_LENGTH = 4110;
//	public static final int STRUCTURE_HAS_TOO_MANY_LEVELS = 4111;
//	public static final int STRUCTURE_HAS_TOO_MANY_OCCURS_WITHIN_OCCURS = 4112;
//	public static final int STRUCTURE_ITEM_IS_FILLER_WITH_DECIMALS = 4113;

//	public static final int STRUCTURE_ITEM_WITH_PRIM_CHAR_AND_TYPDEF_TO_ITEM = 4117;
		public static final int STRUCTURE_LEAF_ITEM_WITH_NO_PRIM_CHAR = 4118;
//	public static final int STRUCTURE_ITEM_HAS_INVALID_LENGTH = 4119;
//	public static final int STRUCTURE_ITEM_FOR_SQL_RECORD_HAS_INVALID_OCCURS_VALUE = 4120;
//	public static final int STRUCTURE_ITEM_FOR_SQL_RECORD_HAS_DECIMALS_AND_IS_NOT_PACK = 4121;
//	public static final int STRUCTURE_ITEM_FOR_SQL_RECORD_IS_A_FILLER = 4122;
	public static final int STRUCTURE_ITEM_FOR_SQL_RECORD_HAS_INVALID_TYPE = 4123;
//	public static final int STRUCTURE_ITEM_FOR_SQL_RECORD_HAS_SUBSTRUCTURE = 4124;
//	public static final int STRUCTURE_ITEM_FOR_SQL_RECORD_IS_BIN_AND_HAS_INVALID_LENGTH = 4125;
//	public static final int UNDEFINED_VARIABLE_LENGTH_ITEM = 4126;

//
//	public static final int REDEFINED_RECORD_LENGTH_CANNOT_EXCEED_PRIMARY_RECORD_LENGTH = 4128;
//	public static final int REDEFINED_RECORD_IS_REDEFINING_A_PROGRAM_PARAMETER = 4129;
//	public static final int VARIABLE_LENGTH_RECORD_HAS_VARIABLE_LENGTH_ITEM_WITH_INVALID_ATTRIBUTES = 4130;
//	public static final int RELATIVERECORD_HAS_KEY_ITEM_WITH_INVALID_ATTRIBUTES = 4131;
//	public static final int VARIABLE_LENGTH_RECORD_HAS_NUMBER_OF_OCCURS_ITEM_WITH_INVALID_ATTRIBUTES = 4132;
//	public static final int IO_RECORD_HAS_NO_ITEMS = 4133;
//	public static final int SUBITEM_TYPE_INCOMPATIBLE_WITH_PARENT = 4134;
//	public static final int STRUCTURE_ITEM_TYPEDEFS_STRUCTURE_AND_HAS_STRUCTUREITEM_DECLARATIONS = 4135;
//	public static final int IO_RECORD_ORGANIZATION_NOT_SUPPORTED = 4136;
//	public static final int VARIABLE_LENGTH_SERIAL_RECORD_NOT_SUPPORTED = 4137;

	public static final int BIN_STRUCTURE_ITEM_FOR_SQL_RECORD_IS_NOT_INTEGER = 4138;
	public static final int SQL_RECORD_HAS_OCCURS = 4139;
	public static final int INVALID_TYPE_FOR_CSVRECORD = 4140;
	public static final int ARRAY_INVALID_FOR_CSVRECORD = 4141;
    
//	public static final int WEB_ITEM_MUST_HAVE_OCCURS = 4150;
//	public static final int WEB_ITEM_OCCURSFOR_NOT_FOUND = 4151;
//	public static final int WEB_ITEM_OCCURSFOR_MUST_HAVE_SINGLE_OCCURRANCE = 4152;
//	public static final int WEB_ITEM_OCCURSFOR_MUST_NOT_HAVE_DECIMALS = 4153;
//	public static final int WEB_ITEM_OCCURSFOR_MUST_BE_NUMERIC = 4154;
//	public static final int EDIT_ITEM_EDITTYPE_REQUIRES_NUMERIC_OR_CHAR_ITEM = 4155;
//	public static final int EDIT_ITEM_FOLD_PROHIBITS_NUMERIC_OR_DBCS_ITEM = 4156;
//	public static final int EDIT_ITEM_PROPERTIES_INVALID_FOR_NON_LEAF_ITEMS = 4157;
//	public static final int WEB_ITEM_PROPERTIES_INVALID_FOR_NON_LEAF_ITEMS = 4158;
	public static final int EDIT_ITEM_FILL_CHARACTER_INVALID_FOR_DBCS = 4159;
//	public static final int EDIT_ITEM_SOSI_INVALID_FOR_NON_MIXED = 4160;
//	public static final int EDIT_ITEM_CURRENCY_REQUIRES_NUMERIC = 4161;
//	public static final int EDIT_ITEM_NUMERICSEPARATOR_REQUIRES_NUMERIC = 4162;
//	public static final int EDIT_ITEM_SIGN_REQUIRES_NUMERIC = 4163;
//	public static final int EDIT_ITEM_ZEROEDIT_REQUIRES_NUMERIC = 4164;
//	public static final int EDIT_ITEM_EDIT_TABLE_INVALID_EDIT_USE = 4165;
//	public static final int EDIT_ITEM_NOT_COMPATIBLE_WITH_TABLE_COLUMN = 4166;
//	public static final int EDIT_ITEM_MINIMUM_INPUT_TOO_LARGE = 4167;
//	public static final int EDIT_ITEM_RANGE_MUST_CONTAIN_TWO_VALUES = 4168;
//	public static final int EDIT_ITEM_RANGE_LOW_VALUE_INVALID = 4169;
//	public static final int EDIT_ITEM_RANGE_HIGH_VALUE_INVALID = 4170;
//	public static final int EDIT_ITEM_RANGE_LOW_MUST_BE_LESSTHAN_HIGH = 4171;
//	public static final int EDIT_ITEM_RANGE_LOW_VALUE_TOO_LARGE = 4172;
//	public static final int EDIT_ITEM_RANGE_HIGH_VALUE_TOO_SMALL = 4173;
//	public static final int SUBMIT_VALUE_RECEIVER_ITEM_WRONG_TYPE = 4174;
	public static final int TABLE_MATCHINVALID_MUST_CONTAIN_COLUMN = 4175;
	public static final int TABLE_MATCHVALID_MUST_CONTAIN_COLUMN = 4176;
	public static final int TABLE_RANGECHECK_MUST_CONTAIN_TWO_COLUMNS = 4177;
//	public static final int WEB_ITEM_WRONG_TYPE_FOR_SUBMIT = 4178;
//	public static final int EDIT_ITEM_TOO_MANY_DIMENSIONS = 4179;
	public static final int TABLE_ITEM_HAS_OCCURS = 4180;
	public static final int TABLE_MESSAGE_MUST_CONTAIN_TWO_COLUMNS = 4181;
	public static final int MESSAGE_TABLE_MUST_HAVE_FIRST_COLUMN_NUM_UNLESS_USED_BY_WEBTRANSACTION = 4182;
//
//	//4200-4299: Used for Statments
//	public static final int SET_EMPTY_STATEMENT_WITH_INVALID_DATAREF = 4200;
//	public static final int AMBIGUOUS_DATAREF = 4201;
	public static final int SET_POSITION_STATEMENT_WITH_INVALID_DATAREF = 4202;
//	public static final int UNDECLARED_DATAREF = 4203;
//	public static final int SET_NULL_STATEMENT_WITH_INVALID_DATAREF = 4204;
//	public static final int SET_STATEMENT_OPTION_NOT_VALID_FOR_TARGET = 4206;
//	public static final int SET_STATEMENT_DATAREF_IN_CALL_PARM_LIST = 4207;
//
//	public static final int IO_STATEMENT_WITH_UNDECLARED_DATAREF = 4209;
//	public static final int SCANBACK_STATEMENT_WITH_VSAM_FILE = 4210;
//	public static final int SCAN_AND_ADD_ON_THE_SAME_SERIAL_FILE = 4211;
//	public static final int ASSIGNMENT_FUNCTION_DOES_NOT_RETURN_VALUE = 4212;
//	public static final int ASSIGNMENT_FUNCTION_RETURN_VALUE_TO_DATA_STRUCTURE = 4213;
//	public static final int ASSIGNMENT_FUNCTION_RETURN_VALUE_INCOMPATIBLE_WITH_TARGET = 4214;
//	public static final int ASSIGNMENT_DATA_TYPES_INCOMPATIBLE = 4215;
//	public static final int ASSIGNMENT_DATA_ITEM_TYPES_INCOMPATIBLE = 4216;
//	public static final int ASSIGNMENT_LITERAL_TO_DATA_STRUCTURE = 4217;
//	public static final int ASSIGNMENT_LITERAL_AND_ITEM_INCOMPATIBLE = 4218;
//	public static final int ASSIGNMENT_HEX_DATA_INVALID = 4219;
//	public static final int ARITHMETIC_ASSIGNMENT_TARGET_DATA_STRUCTURE = 4220;
//	public static final int ARITHMETIC_ASSIGNMENT_TARGET_INVALID_TYPE = 4221;
//	public static final int ARITHMETIC_ASSIGNMENT_OPERAND_DATA_STRUCTURE = 4222;
//	public static final int ARITHMETIC_ASSIGNMENT_OPERAND_INVALID_TYPE = 4223;
//
//	public static final int SUBSCRIPT_NOT_ALLOWED_FOR_DATA_STRUCTURE = 4224;
//	public static final int SUBSCRIPT_SPECIFIED_FOR_SINGLE_OCCURRING_ITEM = 4225;
//	public static final int SUBSCRIPT_NUMBER_OF_DIMENSIONS_SPECIFIED_INCORRECT = 4226;
//	public static final int SUBSCRIPT_LITERAL_VALUE_EXCEEDS_DIMENSION_SIZE = 4227;
//	public static final int SUBSCRIPT_DATA_STRUCTURE_SPECIFIED_FOR_SUBSCRIPT = 4228;
//	public static final int SUBSCRIPT_INVALID_ITEM_SPECIFIED_FOR_SUBSCRIPT = 4229;
//
//	public static final int FUNCTION_INVOCATION_INCORRECT_NUM_OF_ARGS = 4230;
//	public static final int FUNCTION_INVOCATION_INCORRECT_DATAREF_ARGUMENT_TYPE = 4231;
//	public static final int FUNCTION_INVOCATION_INCORRECT_LITERAL_ARGUMENT_TYPE = 4232;
//	public static final int FUNCTION_INVOCATION_INCORRECT_DATAREF_ARGUMENT_LENGTH = 4233;
//	public static final int FUNCTION_INVOCATION_INCORRECT_LITERAL_ARGUMENT_LENGTH = 4234;
//	public static final int FUNCTION_INVOCATION_INCORRECT_DATAREF_ARGUMENT_DECIMALS = 4235;
//	public static final int FUNCTION_INVOCATION_INCORRECT_LITERAL_ARGUMENT_DECIMALS = 4236;
//
//	public static final int EZE_FUNCTION_INVOCATION_NOT_SUPPORTED_ON_TARGET = 4237;
//	public static final int EZERTN_CANNOT_SPECIFY_A_VALUE = 4239;
//	public static final int EZERTN_MUST_SPECIFY_A_VALUE = 4240;
//

//4241- 4249 ARE IN EGLMESSAGES
	
	public static final int CANNOT_PASS_REFERNECE_TYPE_ON_CALL = 4230; 

//	public static final int OPERAND_FOR_IOERROR_CONDITION_MUST_BE_IO_RECORD = 4250;
//	public static final int OPERAND_FOR_KEYWORD_CONDITION_MUST_BE_ITEM = 4251;
//	public static final int OPERAND_FOR_KEYWORD_CONDITION_MUST_BE_SQLITEM = 4252;
//	public static final int COMPARE_DATA_STRUCTURE_INVALID = 4253;
//	public static final int COMPARE_LITERAL_INCOMPATIBLE_WITH_ARITHMETIC_EXPRESSION = 4254;
//	public static final int COMPARE_ITEM_INCOMPATIBLE_WITH_ARITHMETIC_EXPRESSION = 4255;
//	public static final int COMPARE_ITEM_INCOMPATIBLE_WITH_ITEM = 4256;
//	public static final int COMPARE_ITEM_INCOMPATIBLE_WITH_LITERAL = 4257;
//	public static final int COMPARE_LITERAL_INCOMPATIBLE_WITH_LITERAL = 4258;
//	public static final int OPERAND_FOR_KEYWORD_CONDITION_WRONG_ITEM_TYPE = 4259;
//
//	public static final int ASSIGNMENT_EZEJAVA_FUNCTION_RETURN_VALUE_INCOMPATIBLE_WITH_TARGET = 4260;
//	public static final int CAST_INVALID_FOR_DATASTRUCTURE = 4261;
//	public static final int CAST_INCOMPATIBLE_WITH_LITERAL_TYPE = 4262;
//	public static final int CAST_INCOMPATIBLE_WITH_ITEM_TYPE = 4263;
//	public static final int CAST_INVALID_FOR_ARGUMENT_TYPE = 4264;
//	public static final int ARGUMENT_MUST_BE_CAST_TO_OBJID = 4265;
//
//	public static final int OPERAND_FOR_MODIFIED_CONDITION_MUST_BE_FORM_OR_FORM_ITEM = 4266;
//
//	public static final int ADD_STATEMENT_RECORD_IS_INVALID_TYPE = 4270;
//	public static final int CLOSE_STATEMENT_RECORD_IS_INVALID_TYPE = 4271;
	public static final int DELETE_STATEMENT_RECORD_IS_INVALID_TYPE = 4272;
//	public static final int INQUIRY_STATEMENT_RECORD_IS_INVALID_TYPE = 4273;
//	public static final int REPLACE_STATEMENT_RECORD_IS_INVALID_TYPE = 4274;
//	public static final int SCANBACK_STATEMENT_RECORD_IS_INVALID_TYPE = 4275;
//	public static final int SCAN_STATEMENT_RECORD_IS_INVALID_TYPE = 4276;
//	public static final int SETINQ_STATEMENT_RECORD_IS_INVALID_TYPE = 4277;
//	public static final int SETUPD_STATEMENT_RECORD_IS_INVALID_TYPE = 4278;
//	public static final int SQLEXEC_STATEMENT_RECORD_IS_INVALID_TYPE = 4279;
//	public static final int UPDATE_STATEMENT_RECORD_IS_INVALID_TYPE = 4280;
//
//	public static final int PROGRAM_IO_STATEMENT_WITH_RECORD_IN_PROGRAM_PARM_LIST = 4281;
//	public static final int FUNCTION_IO_STATEMENT_WITH_RECORD_IN_PROGRAM_PARM_LIST = 4282;
//
//	public static final int FORWARD_STATEMENT_LABEL_MUST_BE_ITEM = 4290;
//	public static final int FORWARD_STATEMENT_LABEL_ITEM_MUST_BE_CHA = 4291;
//	public static final int FORWARD_ONLY_VALID_IN_ACTION_PROGRAMS = 4292;
//	public static final int EZEUI_WORD_ONLY_VALID_IN_ACTION_PROGRAMS = 4293;
//	public static final int FORWARD_STATEMENT_FORM_NOT_DEFINED = 4294;
//
	public static final int JAVA_FUNCTION_NUMERIC_EXPRESSION_AS_ARG = 4293;
	public static final int JAVA_CAST_NO_CAST_ALLOWED = 4294;
	public static final int JAVA_CAST_NO_CAST_OR_OBJID_REQUIRED = 4295;
	public static final int JAVA_CAST_OBJID_REQUIRED = 4296;
	public static final int JAVA_CAST_ARGUMENT_REQUIRED = 4297;
	public static final int JAVA_CAST_CHARACTER_TYPE_NEEDED = 4298;
	public static final int JAVA_CAST_NUMERIC_TYPE_NEEDED = 4299;
	
//4300-4301 ARE IN EGLMESSAGES	

	//4350-4499: Used for item validation messages (may change)!
	public static final int MISSING_LENGTH_FOR_PRIMITIVE_TYPE = 4350;
	
	public static final int LENGTH_NOT_ALLOWED = 4400;
	public static final int INVALID_LENGTH_FOR_PARAMETERIZED_TYPE = 4401;
	public static final int INVALID_DECIMALS = 4402;
	public static final int DECIMALS_GREATER_THAN_LENGTH = 4403;
	public static final int INVALID_CALCULATED_LENGTH_FOR_PRIMITIVE_TYPE_WITH_LOGICAL_CHILDREN = 4404;
	public static final int PARAMETERIZED_TYPE_REQUIRES_DATETIME_PATTERN = 4405;
//	public static final int TYPEDEF_ITEM_CAUSES_CIRCULAR_TYPEDEFS = 4405;
//	public static final int NAMELESS_TYPEDEF_ITEM_CAUSES_CIRCULAR_TYPEDEFS = 4406;
	public static final int DUPLICATE_ITEM_NAME = 4407;
//	public static final int INVALID_PRIMITIVETYPE = 4408;
	public static final int INVALID_LENGTH_FOR_PRIMITIVE_TYPE_WITH_FIXED_LENGTH = 4409;
	public static final int INVALID_CALCULATED_LENGTH_FOR_PRIMITIVE_TYPE_WITH_FIXED_LENGTH_WITH_LOGICAL_CHILDREN = 4410;
	public static final int DUPLICATE_ITEM_NAME_DUE_TO_EMBED = 4411;
	public static final int NEGATIVE_LENGTH_INVALID = 4412;

//
//	//4500-4599: Used for SQL validation messages (may change)!
//	public static final int DUPLICATE_STATEMENT_ID = 4500;
//	public static final int INVALID_IO_OPTION_FOR_SQL_STATEMENT = 4501;
//	public static final int EXCEPTION_REMOVING_SQL_COMMENTS = 4502;
//	//		public static final int THIS_IS_AVAILABLE_FOR_REUSE = 4503;
//	//		public static final int THIS_IS_AVAILABLE_FOR_REUSE = 4504;
//	//		public static final int THIS_IS_AVAILABLE_FOR_REUSE = 4505;
//	//		public static final int THIS_IS_AVAILABLE_FOR_REUSE = 4506;
//	public static final int INVALID_INTO_CLAUSE = 4507;
//	public static final int INTO_CLAUSE_ENDS_WITH_COMMA = 4508;
//	public static final int SINGLE_ROW_SELECT_INVALID_WITH_TNHV = 4509;
//	public static final int SINGLE_ROW_SELECT_INVALID_WITH_ORDER_BY_CLAUSE = 4510;
//	public static final int INVALID_EMPTY_CLAUSE = 4511;
//	public static final int INVALID_LITERAL = 4512;
//	public static final int STATEMENT_VALIDATION_FAILED = 4513;
//	public static final int CLASS_LOCATION_REQUIRED = 4514;
//	public static final int COULD_NOT_BUILD_DEFAULT_STATEMENT = 4515;
//	public static final int DATA_ITEM_NOT_FOUND_FOR_COLUMN_IDENTIFIER = 4516;
//
//	//      public static final int THIS_IS_AVAILABLE_FOR_REUSE = 4516;
//	public static final int Single_Row_Select_And_ESTB_Mutually_Exclusive = 4517;
//	public static final int SQL_INFO_WITH_NON_SQL_RECORD = 4518;
//	public static final int NO_READ_WRITE_ITEMS = 4519;
//	public static final int JOIN_NOT_ALLOWED = 4520;
//	public static final int CONTAINS_ONLY_KEY_OR_READONLY_COLUMNS = 4521;
//	public static final int UPDATE_ID_REQUIRED = 4522;
//	public static final int UPDATE_ID_MUST_REFERENCE_UPDATE_OR_SETUPD_STMT = 4523;
//	public static final int SQL_IO_OBJECTS_MUST_MATCH = 4524;
//	//      public static final int THIS_IS_AVAILABLE_FOR_REUSE = 4525;
//	public static final int CONNECT_TO_DATABASE_FAILED = 4526;
//
//4527-4542 ARE IN EGLMESSAGES
	public static final int STATEMENT_VALIDATION_FAILED = 4513; //$NON-NLS-1$
 	public static final int CLASS_LOCATION_REQUIRED = 4514; //$NON-NLS-1$
 	public static final int COULD_NOT_BUILD_DEFAULT_STATEMENT = 4515; //$NON-NLS-1$

	public static final int CONNECT_TO_DATABASE_FAILED = 4526; //$NON-NLS-1$

	public static final int CANNOT_DERIVE_INTO = 4527; //$NON-NLS-1$
	public static final int COLUMN_NAME_NOT_FOUND = 4528; //$NON-NLS-1$
	public static final int DUPLICATE_COLUMN_NAME_FOUND = 4529; //$NON-NLS-1$
	public static final int IO_OBJECT_CONTAINS_NO_STRUCTURE_ITEMS = 4530; //$NON-NLS-1$
 	public static final int IO_OBJECT_CONTAINS_NO_READ_WRITE_COLUMNS = 4531; //$NON-NLS-1$
 	public static final int IO_OBJECT_IS_SQL_JOIN = 4532; //$NON-NLS-1$
 	public static final int IO_OBJECT_CONTAINS_ONLY_KEY_OR_READONLY_COLUMNS = 4533; //$NON-NLS-1$
 	public static final int IO_OBJECT_NOT_SQL_RECORD = 4534; //$NON-NLS-1$
//	public static final int IO_OBJECT_UNDEFINED_OR_NOT_SQL_RECORD_FOR_UPDATE_OR_SETUPD = 4535; //$NON-NLS-1$
//	//	public static final String THIS_IS_AVAILABLE_FOR_REUSE = 4536;	
 	public static final int CONNECTION_ERROR_ON_VALIDATE = 4537; //$NON-NLS-1$
 	public static final int IO_OBJECT_REQUIRED_FOR_SQLEXEC = 4538; //$NON-NLS-1$
//	public static final int INVALID_RECORD_TYPE_FOR_STATEMENTID = 4539; //$NON-NLS-1$
 	public static final int COULD_NOT_BUILD_DEFAULT_STATEMENT_FOR_SQL_RECORD = 4540; //$NON-NLS-1$
  	//	public static final String THIS_IS_AVAILABLE_FOR_REUSE = 4541;
 	public static final int SQL_RECORD_CONTAINS_NO_STRUCTURE_ITEMS = 4542; //$NON-NLS-1$

	public static final int P_INVALID_ORDER_BY = 4551;
	public static final int P_INVALID_GROUP_BY = 4552;
	public static final int P_INVALID_INSERT_INTO = 4553;
	public static final int P_INVALID_FOR_UPDATE_OF = 4554;
	public static final int P_BY_WO_ORDER_OR_GROUP = 4555;
	public static final int P_OF_WO_FOR_UPDATE = 4556;
	public static final int P_UNMATCHED_PARENS = 4557;		
// 		public static final int P_INVALID_WHERE_CURRENT_OF = 4558;

//4580 -4808 ARE IN EGLMESSAGES   


//	//4900-4999: Used for Logic/Program/Function messages
	public static final int INVALID_FORM_TYPEDEF = 4901;
	public static final int FUNCTION_PARAMETER_HAS_INCORRECT_TYPE = 4902;
//	public static final int PROGRAM_DECLARATION_HAS_INCORRECT_TYPE = 4903;
	public static final int PROGRAM_PARAMETER_HAS_INCORRECT_TYPE = 4904;
//	public static final int PROGRAM_WORKING_STORAGE_RECORD_HAS_INCORRECT_TYPE = 4905;
//	public static final int PROGRAM_NAME_RESERVED = 4906;
//	public static final int CALLED_REMOTE_PROGRAM_PARM_LENGTH_IS_TOO_LONG = 4909;
//	public static final int CALLED_PROGRAM_PARM_LENGTH_IS_TOO_LONG_FOR_COMMDATA = 4910;
//	public static final int CALLED_REMOTE_PROGRAM_IS_NOT_BATCH = 4911;
//	public static final int ACTION_PROGRAM_INPUT_FORM_MUST_BE_WEB_FORM = 4912;
//	public static final int ACTION_PROGRAM_INPUT_FORM_NOT_FOUND = 4913;
	
	public static final int SYSTEM_FUNCTION_NOT_ALLOWED_IN_PAGEHANDLER = 4912;
	public static final int SYSTEM_FUNCTION_NOT_ALLOWED_IN_SERVICE = 4913;

	public static final int FUNCTION_RETURN_HAS_INCORRECT_TYPE = 4914;
	public static final int RETURN_DOES_NOT_RESOLVE = 4915;
	public static final int FUNCTION_RETURN_IS_AMBIGUOUS = 4916;
	public static final int MAIN_FUNCTION_HAS_RETURN = 4917;		

	public static final int PROGRAM_PARAMTER_DOES_NOT_RESOLVE = 4920;
	public static final int PROGRAM_PARAMTER_IS_AMBIGUOUS = 4921;
	public static final int PROGRAM_MAIN_FUNCTION_REQUIRED = 4922;
	public static final int PROGRAM_ONLY_ONE_MAIN_FUNCTION_ALLOWED = 4923;
	public static final int MAIN_FUNCTION_HAS_PARAMETERS = 4924;
	public static final int DATA_DECLARATION_DOES_NOT_RESOLVE = 4925;
	public static final int DATA_DECLARATION_HAS_INCORRECT_TYPE = 4926;
	public static final int DATA_DECLARATION_IS_AMBIGUOUS = 4927;
	public static final int PROGRAM_OR_LIBRARY_USE_STATEMENT_IS_AMBIGUOUS = 4928;
	public static final int PROGRAM_OR_LIBRARY_USE_STATEMENT_DOES_NOT_RESOLVE = 4929;
	public static final int USE_STATEMENT_RESOLVES_TO_INVALID_TYPE = 4930;
	public static final int PROGRAM_USE_STATEMENT_TOO_MANY_FORMGROUPS = 4931;
	public static final int PROGRAM_USE_STATEMENT_TOO_MANY_HELP_GROUP_PROPERTIES = 4932;
	public static final int PROGRAM_PARAMETER_FORM_MUST_BE_IN_MAIN_FORMGROUP = 4933;
	public static final int PROGRAM_OR_LIBRARY_USE_STATEMENT_INAPPLICABLE_PROPERTY_1 = 4934;
	public static final int PROGRAM_OR_LIBRARY_USE_STATEMENT_INAPPLICABLE_PROPERTY_2 = 4935;
	public static final int PROGRAM_USE_STATEMENT_HELP_GROUP_WITH_NO_MAIN_GROUP = 4936;
	public static final int USE_STATEMENT_VAGCOMPATIBILITY = 4937;
	public static final int FUNCTION_PARAMETER_REQUIRES_LENGTH = 4938;
	public static final int PROGRAM_PARAMTER_NO_FORM_ARRAY = 4939;

	public static final int FUNCTION_PARAMTER_DOES_NOT_RESOLVE = 4940;
	public static final int FUNCTION_PARAMTER_IS_AMBIGUOUS = 4941;
	public static final int FUNCTION_PARAMETER_TYPE_CANNOT_BE_NULLABLE = 4942;
	public static final int FUNCTION_PARAMETER_BIN_CANNOT_BE_NULLABLE = 4943;		
	public static final int FUNCTION_NO_MAIN_FUNCTION_ALLOWED = 4944;
	public static final int DUPLICATE_FUNCTION_NAMES = 4945;
	public static final int FUNCTION_PARAMETER_TYPE_CANNOT_BE_FIELD = 4946;
	public static final int FUNCTION_PARAMETER_TYPE_CANNOT_BE_IN = 4947;
	public static final int FUNCTION_PARAMETER_OUT_NOT_ALLOWED_WITH_FIELD = 4948;
	public static final int IN_OR_OUT_MODIFIER_NOT_ALLOWED_WITH_ANY_TYPE = 4949;
	public static final int FUNCTION_PARAMETER_TYPE_CANNOT_BE_OUT = 4950;
    public static final int DICTIONARY_FUNCTION_USED_WITHOUT_DICTIONARY = 4951;
    public static final int DICTIONARY_OR_ARRAY_FUNCTION_USED_WITHOUT_DICTIONARY_OR_ARRAY = 4952;
    public static final int FUNCTION_PARAMETER_TYPE_CANNOT_BE_OUT_OR_INOUT = 4953;
    public static final int CONSTANT_DECL_MUST_BE_PRIMITIVE = 4954;
    public static final int CONSTANT_VALUE_MUST_BE_LITERAL = 4955;
	public static final int FUNCTION_PARAMETER_MODIFIER_NOT_ALLOWED_WITH_LOOSE_TYPE = 4956;
	public static final int IN_MODIFIER_REQUIRED_FOR_EXTERNALTYPE = 4957;
	public static final int CONST_MODIFIER_NOT_ALLOWED_WITH_IN_MODIFIER = 4958;
    public static final int FUNCTION_PARM_CONST_AND_FIELD_MUTEX = 4960;
    public static final int EXTERNALTYPE_PARM_CANNOT_BE_CONST = 4961;
    public static final int SERVICE_PARM_CANNOT_BE_CONST = 4962;
    public static final int ONEWAY_FUNCTION_PARM_MUST_BE_IN = 4963;
    public static final int FORM_MUST_BE_QUALIFIED_BY_FORMGROUP = 4965;
    public static final int CONSTANT_VALUE_MIXED_TYPE_ARRAY = 4966;

	

//	//5000-5099: Used for Record/Table messages
//	public static final int TABLE_NAME_RESERVED = 5000;
		public static final int TABLE_ROW_MUST_BE_LIST = 5002;
		public static final int TABLE_HAS_NO_CONTENTS = 5003;
	public static final int DATATABLE_NUMBER_COLUMNS_DO_NOT_MATCH = 5004;
//	public static final int MQRECORD_GET_OPTIONS_UNKNOWN = 5005;
//	public static final int MQRECORD_OPEN_OPTIONS_UNKNOWN = 5006;
	
	
	public static final int SHARED_MUST_BE_YES = 5007;
	public static final int RESIDENT_MUST_BE_NO = 5008;
	
	public static final int IS_NOT_UNSUPPORTED = 5009;
	
//
//	public static final int MQRECORD_GET_OPTIONS_WRONG_TYPE = 5010;
//	public static final int MQRECORD_OPEN_OPTIONS_WRONG_TYPE = 5011;
//	public static final int MQRECORD_PUT_OPTIONS_WRONG_TYPE = 5012;
//	public static final int MQRECORD_MESSAGE_DESCRIPTOR_OPTIONS_WRONG_TYPE = 5013;
//	public static final int MQRECORD_QUEUE_DESCRIPTOR_OPTIONS_WRONG_TYPE = 5014;

	public static final int DATATABLE_CONTENT_HAS_INVALID_CHARACTERS = 5019;
	public static final int DATATABLE_CONTENT_HAS_INVALID_DIGITS = 5020;

	public static final int DATATABLE_CONTENT_INVALID_HEX_CONTENT = 5022;
	public static final int DATATABLE_CONTENT_HAS_INVALID_LENGTH = 5023;
	public static final int DATATABLE_CONTENT_DECIMALS_EXCEED_MAX_LENGTH = 5024;
	public static final int DATATABLE_CONTENT_DECIMALS_TOO_LONG = 5025;
    public static final int DATATABLE_CONTENT_MUST_BE_LITERAL = 5026;
    public static final int DATATABLE_CONTENT_TOO_MANY_DECIMALS = 5027;
    public static final int DATATABLE_CONTENT_MUST_BE_BOOLEAN_LITERAL = 5028;
    
	public static final int TOO_MANY_DIMENSIONS_FOR_ARRAY = 5033;
	public static final int TOO_MANY_DIMENTIONS_FOR_RECORD_ARRAY = 5034;
	//5035-5050: Used for For statement messages
	public static final int FOR_STATEMENT_COUNTER_MUST_BE_INT = 5035;
	public static final int FOR_STATEMENT_EXPR_MUST_BE_INT = 5036;
	public static final int FOREACH_ARRAY_MUST_DECLARE_VARIABLE = 5037;
	public static final int FOREACH_SOURCE_MUST_BE_ARRAY = 5038;

	public static final int COND_INVALID_ESCAPE_CHARACTER = 5040;
	public static final int COND_OPERAND_MUST_BE_STRING = 5041;
	public static final int COND_OPERAND_CANNOT_BE_HEX_OR_DBCHAR = 5042;
	

//	public static final int WEBFORM_NOT_SUPPORTED_ON_TARGET_SYSTEM = 5051;
//	public static final int WEBFORM_SUBMITVALUE_RECEIVER_ITEM_NOT_FOUND = 5052;
	
	public static final int RECURSIVE_LOOP_IN_EXTENDS = 5045;   		

	public static final int SYSTEM_FUNCTION_ARG_CANNOT_BE_EXPRESSION = 5046;

    public static final int TYPE_INVALID_REF_TYPE_COMPARISON = 5048;
    public static final int TYPE_NOT_VALID_IN_EXPRESSION = 5049;
	public static final int EXPRESSION_FUNCTION_INVOCATION_NOT_ALLOWED = 5050;
	public static final int TYPE_INVALID_CONSOLE_FIELD_TYPE_COMPARISON = 5051;
	public static final int FIXED_RECORDS_NOT_ALLOWED_IN_COMPARISONS = 5052;
	public static final int COMPARING_TEXT_AND_NUMERIC = 5053;
	
//  
	public static final int CONTINUE_STATEMENT_LOCATION = 5054;
	public static final int INVALID_CONTINUE_EXIT_MODIFIER = 5055;
	public static final int INVALID_CONTINUE_EXIT_LABEL = 5056;	
	public static final int RECURSIVE_LOOP_STARTED_WITHIN_FLEXIBLE_RECORD_BY_TYPEDEF = 5057;   		
	public static final int EMBEDDED_RECORD_MUST_BE_SQL_RECORD = 5058;
    public static final int ITEM_IS_AMBIGUOUS = 5059;
	public static final int EMBEDED_ITEM_DOES_NOT_RESOLVE = 5060;
	public static final int PART_DOES_NOT_RESOLVE_TO_DATAITEM_OR_RECORD = 5061;
	public static final int EMBED_RERERS_TO_EMPTY_RECORD  = 5062;
	public static final int INCONSISTENT_LEVEL_NUMBERING = 5063;
	public static final int INVALID_STRUCTURE_LEVEL_NUMBERS = 5064;
	public static final int INVALID_RECORD_REFERENCED_WITHIN_ANOTHER_RECORD = 5065;
	public static final int INVALID_AMOUNT_OF_NESTING = 5066;
	public static final int SQL_RECORDS_MUST_ONLY_CONTAIN_DATAITEMS = 5067;
	public static final int SQL_FLAT_LEVEL_NUMBERING = 5068;
	public static final int INVALID_SUMMED_RECORD_LENGTH = 5069;
	public static final int ITEM_DOES_NOT_RESOLVE = 5070;
	public static final int NUMBER_USED_INCORRECTLY = 5071;
	public static final int RECURSIVE_LOOP_STARTED_WITHIN_RECORD = 5072;
	public static final int FILLER_STRUCTURE_ITEM_DOES_NOT_RESOLVE_TO_DATAITEM_OR_RECORD = 5073;
	public static final int FILLER_STRUCTURE_ITEM_IS_AMBIGUOUS = 5074;
	public static final int RECURSIVE_LOOP_STARTED_WITHIN_RECORD_BY_EMBEDS = 5075;
	public static final int NON_SQL_RECORDS_CANNOT_CONTAIN_NULLABLE = 5076;
	
	// 5077-?? : Case statement validation
	public static final int CASE_WHEN_MUST_NOT_BE_BOOLEAN_EXPRESSION = 5077;
	public static final int CASE_WHEN_MUST_BE_BOOLEAN_EXPRESSION = 5078; 

	
	//5079-?? : Misc expression validation
	public static final int OPERANDS_NOT_VALID_WITH_OPERATOR = 5079;		
	public static final int LITERAL_INVALID_IN_NUMERIC_EXPRESSION = 5080;		
	public static final int NUMERIC_EXPRESSION_TYPE_NOT_VALID = 5081;
	public static final int STRING_CONCAT_EXPRESSION_TYPE_NOT_VALID = 5082;
	public static final int LITERAL_INVALID_IN_STRING_CONCAT_EXPRESSION = 5083;
	public static final int EXPRESSIONS_INCOMPATIBLE = 5084;
	public static final int ELEMENT_NOT_VALID_IN_EXPRESSION = 5085;
	public static final int UNARY_EXPRESSION_INVALID_IN_STRING_CONCAT_EXPRESSION = 5086;
	public static final int TYPE_NOT_VALID_IN_STRING_CONCAT_EXPRESSION = 5087;
	public static final int TYPE_NOT_VALID_IN_NUMERIC_EXPRESSION = 5088;
	public static final int TYPE_INCOMPATIBLE_ARITHMETIC_COMPARISON = 5089;
	public static final int IN_CONDITIONAL_LEFT_OPERAND_INVALID = 5090;
	public static final int IN_CONDITIONAL_RIGHT_OPERAND_INVALID = 5091;
	public static final int COND_OPERAND_MUST_BE_RECORD = 5092;
	public static final int COND_OPERAND_MUST_BE_TEXTFORM_FIELD = 5093;
	public static final int INVALID_TYPE_BLANKS = 5094;
	public static final int COND_OPERAND_INVALID_FOR_NULL = 5095;
	public static final int COND_OPERAND_INVALID_MODIFIED = 5096;


		

	// 5097-5150:  System Word Validation Errors (Jeff)
	public static final int INVALID_USAGE_LOCATION_NO_FUNCTION = 5097;
	public static final int ROUTINE_MUST_HAVE_X_OR_Y_ARGS = 5098;		
	public static final int ARG_MUST_BE_ITEM_INTEGER_OR_STRING = 5099;		
	public static final int WORD_NOT_IN_LIBRARY = 5100;
	public static final int INVALID_USAGE_LOCATION = 5101;
	public static final int ARG_MUST_BE_ITEM_CONSTANT_OR_LITERAL = 5102;
	public static final int ARG_MUST_BE_ITEM = 5103;
	public static final int ARG_MUST_BE_CHAR_ITEM = 5104;		
	public static final int ROUTINE_MUST_HAVE_ATLEAST_X_ARGS = 5105;
	public static final int ROUTINE_MUST_HAVE_ONE_OR_TWO_ITEM_ARGUMENTS = 5106;
	public static final int INVALID_ROUND_ARG_1 = 5107;
	public static final int INVALID_SUBSCRIPT = 5108;
	public static final int ROUTINE_MUST_HAVE_X_ARGS = 5109;
	public static final int ARG_MUST_BE_INTEGER_ITEM_CONSTANT_OR_LITERAL = 5110;
	public static final int ARG_MUST_BE_ITEM_STRING_CONSTANT_OR_LITERAL = 5111;
	public static final int ARG_MUST_BE_GREATER_THAN_ZERO = 5112;
	public static final int INVALID_SETSUBSTR_ARGUMENT_4 = 5113;
	public static final int ARG_MUST_NOT_BE_INTEGER = 5114;
	public static final int ROUTINE_MUST_HAVE_ONE_ITEM_ARG = 5115;
	public static final int NOT_VAGCOMP_FOR_CONNECTION_SERVICES = 5123;
	public static final int INVALID_ARG_1_TO_5_FOR_CONNECTION_SERVICES = 5124;
	public static final int ARG6_MUST_CERTAIN_VALUES_FOR_CONNECTION_SERVICES = 5125;
	public static final int INVALID_NUM_ARGS_FOR_CONVERT = 5126;
	public static final int INVALID_FIRST_ARG_FOR_CONVERT = 5127;
	public static final int INVALID_SECOND_ARG_FOR_CONVERT = 5128;
	public static final int INVALID_THIRD_ARG_FOR_CONVERT = 5129;
	public static final int INVALID_NUM_ARGS_FOR_PURGE = 5130;
	public static final int INVALID_ARG_FOR_PURGE = 5131;
	public static final int NOT_VAGCOMP_FOR_SYSTEM_WORD = 5132;
	public static final int INVALID_NUM_ARGS_FOR_SET_ERROR_OR_LOCALE = 5133;
	public static final int INVALID_LITERAL_ARG1_2_LENGTH_FOR_SETLOCALE = 5134;
	public static final int INVALID__ARG2_FOR_AUDIT = 5135;
	public static final int INVALID_FIRST_ARG_FOR_STARTTRANS = 5136;
	public static final int SHOULD_NOT_BE_SUBSCRIPTED = 5137;
	public static final int TOO_MANY_QUALIFIERS = 5138;
	public static final int INVALID_SYSTEM_VARIABLE_VALUE_0_OR_1 = 5139; 
	public static final int INVALID_SYSTEM_VARIABLE_VALUE_0_1_OR_2 = 5140;
	public static final int INVALID_NUM_SYSTEM_VARIABLE_ASSIGNMENT = 5141;
	public static final int INVALID_STR_SYSTEM_VARIABLE_ASSIGNMENT = 5142;
	public static final int INVALID_TYPE_ISNUMERIC = 5143;
	public static final int INVALID_RETURN_CODE_ASSIGN_VALUE = 5144;
	public static final int INVALID_MQCONDITIONCODE_ASSIGN = 5145;
	public static final int SUBSCRITPT_REQUIRED = 5146;
	public static final int INVALID_DATA_WORD_SUBSCRIPT_VALUE = 5147;
	public static final int INVALID_ARRAY_INDEX_ASSIGN_VALUE = 5148;
	public static final int INVALID_VALIDATIONMSGNUM_ASSIGN_VALUE = 5149;
	public static final int FUNCTION_MUST_HAVE_ARG_LIST = 5150;
	public static final int INVALID_FUNC_ARG = 5151;
	public static final int INVALID_ROUND_FUNC_ARG = 5152;
	public static final int ARG_MUST_BE_BIN_ITEM_LESS_THAN_5_DIGITS = 5153;
	public static final int INVALID_ARG_LIST = 5154;
	public static final int INVALID_ARITHMETIC_CONDITION = 5155;
	public static final int INVALID_NON_ARITHMETIC_CONDITION = 5156;
	public static final int INVALID_EXPRESSION_DATA_ACCESS_OR_LITERAL = 5157;
	public static final int INVALID_EXPRESSION_DATA_ACCESS = 5158;
	public static final int INVALID_NUMERIC_EXPRESSION = 5159;
	public static final int INVALID_FUNC_INVOC_RESULT = 5160;
	public static final int INVALID_ARITHMETIC_CONDITION_STRING_TO_NUMERIC = 5161;
	public static final int INVALID_ARITHMETIC_CONDITION_NUMERIC_TO_STRING = 5162; 
	public static final int INVALID_CONDITIONAL_EXPRESSION = 5163; 
	public static final int INVALID_EVENT_KEY_VALUE = 5164; 
	public static final int INVALID_SYSTEM_TYPE_VALUE = 5165; 
	public static final int INVALID_PA_KEY_VALUE = 5166; 
	public static final int INVALID_PF_KEY_VALUE = 5167; 
	public static final int INVALID_NONARITHMETIC_COND_RIGHT_SIDE = 5168; 
	public static final int INVALID_EVENT_KEY_USE = 5169;
	public static final int INVALID_SYSTEM_TYPE_USE = 5170; 
	public static final int INVALID_STRING_CONCAT_EXPR = 5172; 
	public static final int INVALID_NUMERIC_CASE_TO_STRING_WHEN = 5173; 
	public static final int INVALID_STRING_CASE_TO_NUMERIC_WHEN = 5174;
	public static final int INVALID_ARG_7_FOR_CONNECT = 5175;		
	public static final int TYPE_NOT_VALID_IN_BITWISE_EXPRESSION = 5176;
	public static final int TRUNC_OPERAND_INVALID_MODIFIED = 5177;
//	public static final int INVALID_IF_EXIT_MODIFIER = ????; 
//	public static final int INVALID_WHILE_EXIT_MODIFIER = 5176; 
//	public static final int INVALID_CASE_EXIT_MODIFIER = 5177; 
	public static final int INVALID_FORWARD_UIRECORD_NUM_ARGS = 5178; 
	public static final int INVALID_EXPRESSION_DATA_ACCESS_OR_NUMERIC_LITERAL = 5179;
	public static final int INVALID_MOVE_TO_ARRAY_MODIFIER = 5180;
	public static final int INVALID_SHOW_EXTERNAL = 5181;
	public static final int INVALID_FORWARD_STMT_PASSING_WITH_OUT_RETURNING_OPTION = 5182;
	public static final int INVALID_SHOW_STMT_PASSING_WITH_OUT_RETURNING_OPTION = 5183;
	public static final int INVALID_TRANSFER_EXTERNAL = 5184;
	public static final int INVALID_MOVE_TO_NUM_SYSTEM_VARIABLE = 5185;
	public static final int INVALID_MOVE_TO_STR_SYSTEM_VARIABLE = 5186;
	public static final int INVALID_DATA_ACCESS_OR_POS_INT_LITERAL = 5187;
	public static final int INVALID_EXPRESSION_DATA_ACCESS_OR_STRING_LITERAL = 5188;
	public static final int INVALID_ARG_MUST_BE_INT = 5189;
	public static final int INVALID_FORWARD_TARGET = 5190;
	public static final int FORWARD_ARG_MUST_BE_UI_RECORD = 5191;
	public static final int FORWARD_ARG_MUST_BE_ITEM_RECORD_OR_DYNAMIC_ARRAY = 5192;
	public static final int IN_FROM_EXPRESSION_NOT_INTEGER = 5193;
	public static final int FORWARD_UI_RECORD_DEFINITION_MUST_MATCH_INPUT_PAGE_RECORD = 5194;
	public static final int FORWARD_RETURN_TO_MUST_BE_ACTION_PROGRAM = 5195;
	public static final int MOVE_FOR_COUNT_NOT_INTEGER = 5196;
	public static final int PREPARE_STATEMENT_NO_FROM_CLAUSE = 5197;
	public static final int SHOW_STATEMENT_NO_RETURNING_TO_CLAUSE = 5198;
	public static final int ARG_MUST_NOT_BE_CONSTANT = 5199;		
	public static final int ARG_MUST_BE_ITEM_OR_RECORD = 5200;		
			
	// 01-09 Jeff - New Message for EGLNameValidator
	// 	$NON_NLS-1$
	
	// 5200-5299:  SQL parse errors
	public static final int L_INVALID_ESCAPE_SEQUENCE = 5201;
	public static final int L_STRING_NOT_CLOSED = 5202;
	public static final int L_TOO_MANY_DIGITS = 5203;
	public static final int L_NAME_CANT_HAVE_SUBSTRING = 5204;
	public static final int P_FOUND_RBRACKET_WRONG = 5205;		
	public static final int P_MISSING_FROM_SUBSTRING = 5206;
	public static final int P_TOO_MANY_SUBSTRINGS = 5207;				
	public static final int P_UNRECOGNIZED_TOKEN = 5208;						
	public static final int P_SUBSCRIPT_OR_SUBSTRING_NOT_CLOSED = 5209;
	public static final int P_SUBSTRING_NOT_LAST = 5210;		
	public static final int P_FOUND_EMPTY_BRACKETS = 5211;
	
	public static final int DISCOURAGED_ARITHMETIC_COMPARISON = 5213;
	
	public static final int MISSING_OPERATION_FOR_EXPRESSION = 5214;

	
	public static final int ROUTINE_MUST_HAVE_EVEN_NUM_OF_ARGS = 5254;
	public static final int ARG_MUST_BE_SQL_REC_ITEM_OR_STRING_LITERAL = 5255;
	public static final int ROUTINE_CANT_HAVE_MORE_THAN_ARGS = 5256;	

//5250-5251 ARE IN EGLMESSAGES
	
	public static final int INVALID_PAGEHANDLER_SYSTEM_FUNCTION_USAGE = 5252;

	// 5300-5999: Used for PageHandler, Form and Form Group Validation
	public static final int INVALID_FORMGROUP_CONTENT = 5300;
	public static final int INVALID_FORM_CONTENT = 5301;
	public static final int INVALID_PAGEHANDLER_CONTENT = 5302;
	public static final int INVALID_FORMGROUP_PROPERTY = 5303;
	public static final int INVALID_FORM_PROPERTY = 5304;
	public static final int INVALID_PAGEHANDLER_PROPERTY = 5305;
	public static final int INVALID_VALIDATIONBYPASSKEY_PROPERTY_VALUE = 5306;
	public static final int INVALID_HELPKEY_PROPERTY_VALUE = 5307;
	public static final int INVALID_PROPERTY_VALUE_YES_OR_NO = 5308;
	public static final int INVALID_SIMPLE_NAME_PROPERTY_VALUE = 5309;
	public static final int FIELDLEN_LESS_THAN_VALUE_LENGTH = 5310;
    
	public static final int INVALID_SIZE_PROPERTY_VALUE = 5311;
	public static final int INVALID_POSITIVE_INTEGER_PROPERTY_VALUE = 5312;
	public static final int INVALID_FORM_SIZE_PROPERTY_VALUE = 5313;
	public static final int INVALID_FORM_POSITION_PROPERTY_VALUE = 5314;
	public static final int INVALID_FORM_HELPFORM_PROPERTY_VALUE = 5315;
	public static final int INVALID_FORM_MSGFIELD_PROPERTY_VALUE = 5316;
	public static final int INVALID_FORM_HELPFORM_VARIABLE_FIELD = 5317;

	public static final int INVALID_FORMGROUP_ALIAS_PROPERTY_DUPLICATE = 5325;
	public static final int INVALID_FORMGROUP_ALIAS_PROPERTY_FORM_NAME = 5326;
	public static final int INVALID_FORMGROUP_SCREENFLOATINGAREA_PROPERTY = 5327;
	public static final int INVALID_FORMGROUP_PRINTFLOATINGAREA_PROPERTY = 5328;
	public static final int INVALID_FORMGROUP_FLOATINGAREA_DUPLICATION = 5329;
	public static final int INVALID_FORMGROUP_USEDECLARATION_CANNOT_RESOLVE = 5330;
	public static final int INVALID_FORMGROUP_MULTIPLE_DECLARATION = 5331;
	public static final int INVALID_TEXTFORM_PROPERTY_NOT_VALID_FOR_FORMTYPE = 5332;
	public static final int INVALID_FORM_SIZE_PROPERTY_NOT_SPECIFIED = 5333;
	public static final int INVALID_FORM_FIELD_IDENTIFIER_DUPLICATION = 5334;
	public static final int INVALID_FORM_VALIDATIONORDER_PROPERTY_SPECIFICATION = 5335;
	public static final int INVALID_FORM_CURSOR_PROPERTY_SPECIFICATION = 5336;
	public static final int INVALID_FORM_FIELD_OVERLAPPING = 5337;
	public static final int INVALID_FORM_FIELD_OVERFLOWING = 5338;
	public static final int INVALID_FORM_FIELD_TYPE = 5339;

	public static final int INVALID_FORM_FIELD_POSITION_PROPERTY_VALUE = 5340;
	public static final int INVALID_FORM_FIELD_VALUE_PROPERTY_VALUE = 5341;
	public static final int INVALID_FORM_FIELD_FIELDLEN_PROPERTY_VALUE = 5342;
	public static final int INVALID_FORM_FIELD_PROPERTY_VALUE_MUST_BE_ONE_OF = 5343;
	public static final int INVALID_FORM_FIELD_OUTLINE_PROPERTY_VALUE = 5344;
	public static final int INVALID_FORM_FIELD_VALIDVALUES_PROPERTY_VALUE = 5345;
	public static final int INVALID_FORM_FIELD_STRING_NUMERIC_LITERAL_PROPERTY_VALUE = 5346;
	public static final int INVALID_FORM_FIELD_VALIDATOR_PROPERTY_VALUE = 5347;
	public static final int INVALID_FORM_FIELD_VALIDATORTABLE_PROPERTY_VALUE = 5348;	
	public static final int INVALID_NUMERIC_FORM_FIELD_FIELDLEN_PROPERTY_VALUE = 5349;	
	public static final int INVALID_FORM_FIELD_GREATER_ZERO_PROPERTY_VALUE = 5350;
	public static final int INVALID_FORM_FIELD_POSITIVE_INTEGER_PROPERTY_VALUE = 5351;
	public static final int INVALID_FORM_FIELD_CURRENCY_SYMBOL_PROPERTY_VALUE = 5352;
	public static final int INVALID_FORM_FIELD_FILLCHARACTER_PROPERTY_VALUE = 5353;
	public static final int INVALID_FORM_FIELD_DATEFORMAT_PROPERTY_VALUE = 5354;
	public static final int INVALID_FORM_FIELD_TIMEFORMAT_PROPERTY_VALUE = 5355;
	public static final int INVALID_FORM_FIELD_PROPERTY = 5356;
	public static final int INVALID_FORM_FIELD_PROPERTY_OVERRIDE = 5357;
	public static final int INVALID_PRINTFORM_FIELD_PROPERTY = 5358;
	public static final int INVALID_PRINTFORM_FIELD_HIGHLIGHT_PROPERTY_VALUE = 5359;
	public static final int INVALID_FORM_FIELD_OCCURS_VALUE = 5360;
	public static final int INVALID_PROPERTY_DUPLICATION = 5361;
	public static final int INVALID_FORM_FIELD_PROPERTY_DUPLICATION = 5362;
	public static final int INVALID_PAGEHANDLER_ALIAS_PROPERTY_VALUE = 5363;
	public static final int INVALID_STRING_LITERAL_PROPERTY_VALUE = 5364;
	public static final int INVALID_FUNCTION_NAME_PROPERTY_VALUE = 5365;
	public static final int INVALID_PAGEHANDLER_EVENTVALUEITEM_PROPERTY_VALUE = 5366;
	public static final int INVALID_PAGEHANDLER_VALIDATIONBYPASSFUNCTIONS_PROPERTY_VALUE = 5367;
	public static final int INVALID_PAGEHANDLER_MSGRESOURCE_PROPERTY_VALUE = 5368;
	public static final int INVALID_PAGEHANDLER_USEDECLARATION_CANNOT_RESOLVE = 5369;
	public static final int INVALID_PAGEHANDLER_USEDECLARATION_IS_AMBIGUOUS = 5370;
	public static final int INVALID_DATADECLARATION_REDEFINES_PROPERTY_VALUE = 5371;
	public static final int INVALID_FORM_DATAITEMDEFINATIONIDENTIFIER_CANNOT_RESOLVE = 5372;
	public static final int INVALID_FORM_DATAITEMDEFINATIONIDENTIFIER_IS_AMBIGUOUS = 5373;
	public static final int INVALID_FORMGROUP_USEDECLARATION_IS_AMBIGUOUS = 5374;
	public static final int INVALID_FORM_HELPFORM_PROPERTY_VALUE_CANNOT_RESOLVE = 5375;
	public static final int INVALID_FORM_HELPFORM_PROPERTY_VALUE_AMBIGUOUS = 5376;
	public static final int INVALID_FORM_MSGFIELD_PROPERTY_VALUE_CANNOT_RESOLVE = 5377;
	public static final int INVALID_FORMGROUP_USEDECLARATION_VALUE = 5378;
	public static final int INVALID_HANDLER_USEDECLARATION_VALUE = 5379;
	public static final int INVALID_FORM_FIELD_CHARACTER_PROPERTY_APPLICATION = 5380;
	public static final int INVALID_FORM_FIELD_NUMERIC_PROPERTY_APPLICATION = 5381;
	public static final int INVALID_FORM_DATAITEMDEFINATIONIDENTIFIER_IS_INVALID = 5382;
	public static final int INVALID_PAGEHANDLER_PROPERTY_VALUE_CANNOT_RESOLVE = 5383;
	public static final int INVALID_PAGEHANDLER_PROPERTY_VALUE_IS_AMBIGUOUS = 5384;
	public static final int INVALID_FORM_SCREENSIZES_PROPERTY_VALUE = 5385;
	public static final int INVALID_PRINTFORM_PROPERTY_SCREENSIZES = 5386;
	public static final int INVALID_FORM_FIELD_WRAPPING = 5387;
	public static final int INVALID_FORMGROUP_SCREENFLOATINGAREA_PROPERTY_VALUE = 5388;
	public static final int INVALID_FORMGROUP_PRINTFLOATINGAREA_PROPERTY_VALUE = 5389;
	public static final int INVALID_FORM_TYPE_DEFINATION = 5390;
	public static final int INVALID_FORMGROUP_DEVICETYPE_PROPERTY_VALUE = 5391;
	public static final int INVALID_FORM_FIELD_PROPERTY_CANNOT_RESOLVE = 5392;
	public static final int INVALID_FORM_FIELD_PROPERTY_IS_AMBIGUOUS = 5393;
	public static final int INVALID_PAGEHANDLER_FIELD_PROPERTY_CANNOT_RESOLVE = 5394;
	public static final int INVALID_DATADECLARATION_PROPERTY_MAXSIZE = 5395;			
	public static final int INVALID_FORM_FIELD_MSG_KEY_INTEGER_PROPERTY_VALUE = 5396; 			
	public static final int INVALID_MARGIN_VALUE = 5397;	
	public static final int INVALID_MARGINS_VERSES_HEIGHT = 5398;	
	public static final int INVALID_MARGINS_VERSES_WIDTH = 5399;	

	public static final int NULL_PAGEHANDLER_FILL_CHAR = 5400;			
	public static final int NONBLANK_PAGEHANDLER_FILL_CHAR = 5401;			
	public static final int NEWWINDOW_WITHOUT_ACTIONPROGRAM = 5402;
	public static final int NON_NUMERIC_SELECTTYPE = 5403;
	public static final int DISPLAYUSE_BUTTON = 5404;
	public static final int BYPASSVALIDATION_IN_PAGEHANDLER = 5405;
	public static final int INVALID_PAGEHANDLER_SELECTFROMLIST_PROPERTY_VALUE = 5406;
	public static final int INVALID_FORM_FIELD_DATETIME_PROPERTY_APPLICATION = 5407;
	public static final int INVALID_FORM_FIELD_NOT_SUPPORTED_FOR_DATETIME = 5408;
	public static final int INVALID_FORM_FIELD_TIMESTAMPFORMAT_PROPERTY_VALUE = 5409;
	public static final int INVALID_PAGEHANDLER_SCOPE_PROPERTY_VALUE = 5410;
	public static final int PART_IN_PROPERTY_UNRESOLVED = 5411;
	public static final int PART_IN_PROPERTY_AMBIGUOUS = 5412;
	public static final int PART_IN_PROPERTY_WRONG_RECORD_TYPE = 5413;
	public static final int PROPERTY_ONLY_VALID_FOR_PRIMITIVE_TYPE = 5414;
	public static final int PROPERTY_MUST_BE_SERVICE = 5415;
	public static final int LENGTH_TOO_SHORT_FOR_DATEFORMAT_MASK = 5416;
	public static final int LENGTH_OF_CHARACTER_ITEM_TOO_SHORT_FOR_DATETIMEFORMAT = 5417;
	public static final int LENGTH_OF_NUMERIC_ITEM_TOO_SHORT_FOR_DATETIMEFORMAT = 5418;
	public static final int PART_IN_PROPERTY_WRONG_LIBRARY_TYPE = 5419;
//5550-5555 IS IN EGLMESSAGES	
		public static final int PROPERTY_DOESNT_RESOLVE = 5556;
		public static final int PROPERTY_AMBIGUOUS = 5557;
		public static final int PROPERTY_MUST_BE_BASIC_RECORD = 5558;
		public static final int RECORD_CANT_BE_SYSTEM_FUNCTION_RESULT = 5559;
	
	//  5600-5699: (for now) Used for Library errors
	public static final int LIBRARY_QUALIFIED_NAME_NOT_ALLOWED_AS_DECLARATION= 5601;
	public static final int LIBRARY_PARAMETER_TYPES_MUST_SPECIFY_LENGTH = 5602;
	public static final int LIBRARY_PARAMETER_TYPES_NUMBER_IS_INVALID = 5603;
	public static final int FUNCTION_PARAMETERS_DO_NOT_SUPPORT_NULLABLE_AND_FIELD = 5604;
    public static final int NATIVE_LIBRARY_FUNCTION_PARAMETERS_DO_NOT_SUPPORT_REF = 5605;
    public static final int NATIVE_LIBRARY_FUNCTION_PARAMETERS_DO_NOT_SUPPORT_ARRAYS = 5606;
    public static final int NATIVE_LIBRARY_FUNCTION_PARAMETERS_DO_NOT_SUPPORT_FORMS = 5607;
    public static final int NATIVE_LIBRARY_FUNCTION_PARAMETERS_DO_NOT_SUPPORT_RECORDS = 5608;
    public static final int NATIVE_LIBRARY_FUNCTION_PARAMETER_HAS_INCORRECT_TYPE = 5609;
    public static final int NATIVE_LIBRARY_FUNCTION_PARAMTER_DOES_NOT_RESOLVE = 5610;
    public static final int NATIVE_LIBRARY_FUNCTIONS_DO_NOT_SUPPORT_CONSTANT_DECLARATIONS = 5611;
    public static final int NATIVE_LIBRARY_FUNCTIONS_DO_NOT_SUPPORT_STATEMENTS = 5612;
    public static final int NATIVE_LIBRARY_FUNCTIONS_DO_NOT_SUPPORT_DECLARATIONS = 5613;
    public static final int NATIVE_LIBRARYS_DO_NOT_SUPPORT_CONSTANT_DECLARATIONS = 5614;
    public static final int NATIVE_LIBRARYS_DO_NOT_SUPPORT_DECLARATIONS = 5615;
    public static final int NATIVE_LIBRARYS_DO_NOT_SUPPORT_USE_STATEMENTS = 5616;
    public static final int LIBRARY_HAS_INVALID_SUBTYPE = 5617;
    public static final int SUBSTRUCTURED_ITEM_CANNOT_BE_ARGUMENT_TO_NATIVE_LIBRARY_FUNCTION = 5618;
    public static final int CANNOT_PASS_NULL = 5619;
    
    public static final int PROXY_FUNCTIONS_CANNOT_HAVE_STMTS = 5704;
//6000-6050 ARE IN EGLMESSAGES

	public static final int EMPTY_SQL_STRING = 6500;
	public static final int REQUIRED_SQL_CLAUSE_MISSING = 6501;
	public static final int REQUIRED_SQL_COLUMNS_CLAUSE_MISSING = 6502;		 
	public static final int SQL_CLAUSES_OUT_OF_ORDER = 6503;				
	public static final int SQL_COLUMNS_CLAUSES_OUT_OF_ORDER = 6504;
	public static final int SQL_COLUMNS_CLAUSES_OUT_OF_ORDER_2 = 6505;
	public static final int SQL_CLAUSES_DUPLICATED = 6506;
	public static final int SQL_CLAUSE_UNSUPPORTED = 6507;
	public static final int CANT_HAVE_BOTH_FORUPDATE_SINGLEROW = 6508;
	public static final int CANT_HAVE_BOTH_INLINE_SQL_PREPARED_STMT_REF = 6509;
	public static final int DUPE_OPTION = 6510;
	public static final int DUPE_CLAUSE = 6511;	
	public static final int DUPE_INLINE_SQL = 6512;
	public static final int DUPE_PREPARED_STMT_REFERENCE = 6513;
	public static final int CANT_HAVE_USING_WITHOUT_PREPARED_STMT_REF = 6514;
	public static final int CANT_HAVE_FORUPDATE_WITHOUT_SQLRECORD = 6515;
	public static final int MUST_HAVE_INTO_AND_SQL_INFO = 6516;
	public static final int FOR_UPDATE_MUST_BE_LAST = 6517;
	public static final int CANT_HAVE_BOTH_RECORD_FROM_RESULT_SET = 6518;
	public static final int CANT_HAVE_BOTH_PREVIOUS_FROM_RESULT_SET = 6519;	
	public static final int CANT_HAVE_BOTH_INTO_CLAUSE_FROM_RESULT_SET = 6520;	
	public static final int CANT_HAVE_BOTH_INTO_CLAUSE_PREVIOUS = 6521;
	public static final int CANT_SUBSCRIPT_IO_OBJECT = 6522;	
	public static final int INVALID_QUALIFIER_FOR_IO_OBJECT = 6523;	
	public static final int MUST_HAVE_SQLSTMT_OR_FOR_CLAUSE = 6524;
	public static final int CANT_BE_USED_WITH_CALL = 6525;
	public static final int CANT_HAVE_INTO_WITH_CALL = 6526;	
	public static final int MISSING_HOST_VAR_NAME = 6527;	
	public static final int HOST_VARIABLE_MUST_BE_ITEM = 6528;		
	public static final int CANT_HAVE_BOTH_USINGKEYS_FORUPDATE = 6529;	
	public static final int CANT_HAVE_USINGKEYS_WITHOUT_SQLRECORD = 6530;		
	public static final int CLAUSE_CANT_BE_EMPTY = 6531;		
	public static final int RESULTSETID_NOT_FOUND = 6532;
	public static final int HOST_VARIABLE_NOT_FOUND = 6533;
	public static final int HOST_VARIABLE_AMBIGUOUS = 6534;
	public static final int DUPLICATE_RESULTSETID = 6535;
	public static final int CONFLICTING_RESULTSET_ID = 6536;
	public static final int PREPARED_STATEMENT_ID_NOT_FOUND = 6537;
	public static final int SYSVAR_NOT_HOST_VARIABLE = 6538;
	public static final int CANT_HAVE_BOTH_SINGLEROW_PREPARED_STMT_ID = 6539;		
	public static final int ITEM_OR_CONSTANT_NOT_CHARACTER_TYPE = 6540;
	public static final int PASSING_RECORD_NOT_RECORD = 6541;
	public static final int USINGKEYS_ITEM_IN_SQL_RECORD_ARRAY_IO_TARGET = 6542;
	public static final int INVOCATION_TARGET_INVALID = 6543;
	public static final int INVOCATION_TARGET_FOR_CALL_INVALID = 6544;
	public static final int HOST_VARIABLE_CANT_BE_WHERE_CLAUSE = 6545;
	
	public static final int STATEMENT_CANNOT_BE_IN_ACTION_PROGRAM = 6550;
	public static final int STATEMENT_CANNOT_BE_IN_ACTION_OR_BASIC_PROGRAM = 6551;
	public static final int STATEMENT_CANNOT_BE_IN_LIBRARY = 6552;
	public static final int STATEMENT_CANNOT_BE_IN_PAGE_HANDLER = 6553;
	public static final int STATEMENT_CANNOT_BE_IN_BASIC_PROGRAM = 6554;
	public static final int STATEMENT_CANNOT_BE_IN_BASIC_OR_TEXTUI_PROGRAM = 6555;
	public static final int STATEMENT_CANNOT_BE_IN_CALLED_BASIC_OR_CALLED_TEXTUI_PROGRAM = 6556;
	public static final int STATEMENT_CANNOT_BE_IN_CALLED_TEXT_UI_PROGRAM = 6557;
	public static final int STATEMENT_CAN_ONLY_BE_IN_PAGE_HANDLER = 6558;
	public static final int STATEMENT_CANNOT_BE_IN_SERVICE = 6559;
	
	public static final int ITEM_RESOLVED_TO_CONTAINER_WITH_SAME_NAME_AS_FIELD = 6570;
	public static final int ITEM_RESOLVED_TO_FIELD_WITH_SAME_NAME_AS_CONTAINER = 6571;
	public static final int ITEM_RESOLVED_TO_FIELD_WITH_SAME_NAME_AS_FIELD = 6572;
	public static final int PROGRAM_MUST_BE_UIPROGRAM = 6573;
	
	//6580 - ????: Messages for subscript validation
	public static final int NOT_ENOUGH_SUBSCRIPTS = 6580;
	public static final int TOO_MANY_SUBSCRIPTS = 6581;
	public static final int SUBSCRIPT_OUT_OF_RANGE = 6582;
	public static final int SUBSCRIPT_MUST_BE_INTEGER_ITEM = 6583;
	public static final int DOT_ACCESS_USED_AFTER_DYNAMIC = 6584;
	public static final int ARRAY_ACCESS_NOT_SUBSCRIPTED = 6585;
	public static final int NON_ARRAY_ACCESS_SUBSCRIPTED = 6586;
	public static final int EXPRESSION_AS_SUBSCRIPT = 6587;
	public static final int NON_DYNAMIC_ACCESS_ACCESSED_DYNAMICALLY = 6588;
	public static final int PROPERTY_VALUE_MUST_BE_AN_ARRAY = 6589;		
	public static final int PROPERTY_VALUE_MUST_BE_A_STRING_ARRAY_ARRAY = 6590;
    public static final int SQL_TABLE_NAME_MUST_BE_ARRAY_OF_ARRAYS = 6591;
	public static final int SQL_TABLE_NAME_VAR_MUST_BE_ARRAY_OF_ARRAYS = 6592;
	public static final int SQL_TABLE_NAME_VAR_MUST_BE_ITEM = 6593;
	public static final int INVALID_SQL_TABLE_NAME_VARIABLE = 6594;
	public static final int INVALID_SQL_TABLE_NAME_OR_LABEL = 6596;
	public static final int SQL_TABLE_NAME_LABEL_VARIABLE_DUPLICATES_CLAUSE = 6597;
	public static final int RECORD_NAME_CANNOT_DUPE_SQL_CLAUSE_KEYWORD = 6598;
	
	//6599 - Message for data item part validation. Doesn't seem to fit anywhere else 
	public static final int DATA_ITEM_TYPE_NOT_PRIMITIVE = 6599;
			
	// 6600 - 6800: Messages for statement validation
	public static final int INVALID_PASSING_RECORD_RUI_PROGRAM = 6600;
	public static final int INVALID_CONVERSE_TARGET_FOR_UI_PROGRAM = 6601;
	public static final int LABEL_DECLARATION_CANT_BE_IN_ONEVENT_BLOCK = 6602;


	
	// messages for call statement 
	public static final int DUPE_CALL_OPTION = 6603;
	public static final int TOO_MANY_ARGS_ON_CALL = 6604;
//	public static final int CANT_HAVE_FUNC_INVOC_ARG_ON_CALL = 6605;	
//	public static final int CANT_HAVE_EXPR_ARG_ON_CALL = 6606;		
//	public static final int CANT_HAVE_NUMERIC_ARG_ON_CALL = 6607;
	
	// messages for return statement				
	public static final int RETURN_VALUE_WO_RETURN_DEF = 6608;		
	
	public static final int INVALID_LITERAL_IN_USING_CLAUSE = 6609;

	// messages for set statement				
	public static final int DUPE_STATE_ON_SET = 6610;		
	public static final int MULTIPLE_COLORS_ON_SET = 6611;		
	public static final int MULTIPLE_INTENSITY_ON_SET = 6612;	
	public static final int MULTIPLE_PROTECTION_ON_SET = 6613;		
	public static final int MULTIPLE_TEXT_FIELD_STATE_ON_SET = 6614;	
	public static final int EMPTY_AND_INITIAL_ON_SET = 6615;			
	public static final int MULTIPLE_HIGHLIGHT_STATE_ON_SET = 6616;	
	public static final int UNSUPPORTED_STATE_ON_SET = 6617;
	public static final int TEXT_FIELD_STATES_WITH_OTHERS_ON_SET = 6618;

	// general messages related to data access resolution
	public static final int VARIABLE_NOT_FOUND = 6619;
	public static final int VARIABLE_ACCESS_AMBIGUOUS = 6620;
	public static final int VARIABLE_RESOLVED_TO_CONTAINER_MIGHT_BE_ITEM_IN_VAGEN = 6621;
	public static final int VARIABLE_NOT_FOUND_AS_ITEM = 6622;
	
	// messages for print statement
	public static final int PRINT_TARGET_MUST_BE_PRINT_FORM = 6623;
	
	// messages for goto statement
	public static final int GOTO_LABEL_IS_UNDEFINED = 6624;
	
	// messages for label statement
	public static final int DUPLICATE_LABEL = 6625;
	
	// messages for delete statement
	public static final int DELETE_FROM_CLAUSE_WITH_NON_SQL_RECORD = 6626;
	
	// messages for exit statement
	public static final int EXIT_PROGRAM_LITERAL_NOT_INTEGER = 6627;
	public static final int EXIT_PROGRAM_ITEM_NOT_INTEGER = 6628;
	public static final int EXIT_STACK_LABEL_NOT_IN_MAIN = 6629;
	
	// messages for prepare statement
	public static final int DUPLICATE_PREPARED_STATEMENT_ID = 6630;
	public static final int CONFLICTING_PREPARED_STATEMENT_ID = 6631;
	public static final int STATEMENT_TARGET_NOT_SQL_RECORD = 6632;
	public static final int PREPARE_STATEMENT_FROM_ARGUMENT_NOT_STRING_EXPRESSION = 6633;
	
	// messages for replace statement
	public static final int NO_RESULT_SET_ID_FOR_MULTIPLE_CURSOR_OPEN = 6634;
	public static final int SQL_CLAUSES_OR_OPTIONS_ON_REPLACE_WITH_NON_SQL_REC = 6635;
	public static final int REPLACE_STATEMENT_TARGET_NOT_RECORD = 6636;
	
	// messages for open statement
	public static final int OPEN_FORUPDATE_USED_WITH_SQL_CALL = 6637;		
	public static final int OPEN_FOR_TARGET_NOT_SQL_RECORD = 6638;
	
	// messages for add/add array statements
	public static final int ADD_STATEMENT_TARGET_NOT_RECORD = 6639;
	public static final int ADD_STATEMENT_WITH_USED_WITHOUT_SQL_RECORD = 6640;
	
	// messages for get by position statement
	public static final int INVALID_CLAUSE_FOR_NON_SQL_TARGET = 6641;
	public static final int GET_BY_POSITION_DIRECTIVE_OTHER_THAN_NEXT = 6642;
	public static final int GET_BY_POSITION_DIRECTIVE_OTHER_THAN_NEXT_OR_PREVIOUS = 6643;
	public static final int GET_BY_POSITION_STATEMENT_TARGET_NOT_RECORD = 6644;

	// messages for get by key statement
	public static final int GET_BY_KEY_STATEMENT_TARGET_NOT_RECORD = 6645;

	// messages for assignment statement
	public static final int CANNOT_MODIFY_CONSTANT = 6646;
	public static final int STRINGCONCAT_EXPRESSION_INVALID_EXPR = 6647;
	public static final int ASSIGNMENT_STATEMENT_INCOMPATIBLE_OPERANDS = 6648;
	public static final int SUBSTRING_IMMUTABLE = 6649;
	public static final int ASSIGNMENT_STATEMENT_RECORD_TARGET_SOURCE_CANNOT_BE = 6650;
	public static final int ASSIGNMENT_STATEMENT_RECORD_SOURCE_TARGET_MUST_BE = 6651;
	public static final int ASSIGNMENT_STATEMENT_MUST_BE_RECORD_OR_ITEM = 6652;
	public static final int ASSIGNMENT_STATEMENT_TYPE_MISMATCH = 6653;
	public static final int FUNCTION_MUST_RETURN_TYPE = 6654;
	public static final int ASSIGNMENT_STATEMENT_VARIABLE_NOT_DEFINED = 6655;
	
	// messages for show statement
	public static final int INVALID_PACKAGE_NAME = 6656;
	public static final int SHOW_RETURNING_TO_NOT_PROGRAM_OR_CHARACTER_ITEM = 6657;
	public static final int SHOW_PASSING_TARGET_NOT_RECORD = 6658;
	
	// messages for close statement
	public static final int INVALID_CLOSE_TARGET = 6659; 
	
	public static final int MOVE_STATEMENT_INVALID_TARGET_TYPE = 6660;
	
	// messages for converse statement
	public static final int INVALID_CONVERSE_TARGET_FOR_ACTION_PROGRAM = 6661;
	public static final int INVALID_CONVERSE_TARGET_FOR_TEXTUI_PROGRAM = 6662;
	public static final int NO_SEGMENTED_CONVERSE_IN_CALLED_PROGRAM = 6663;
	
	// messages for move statement
	public static final int MOVE_STATEMENT_MAY_ONLY_HAVE_ONE_MODIFIER = 6664;
	public static final int MOVE_STATEMENT_INVALID_SOURCE_TYPE = 6665;		
	public static final int MOVE_STATEMENT_LITERAL_CANT_MOVE_BY_NAME_OR_BY_POSITION = 6666;
	public static final int MOVE_STATEMENT_BYNAME_BYPOSITION_ITEM_HAS_NO_SUBSTRUCTURE = 6667;
	public static final int MOVE_STATEMENT_NONUNIQUE_BYNAME_SOURCE = 6668;
	public static final int MOVE_STATEMENT_NONUNIQUE_BYNAME_TARGET = 6669;
	public static final int MOVE_STATEMENT_MULTIDIMENSIONAL_BYNAME_OR_BYPOSITION_SOURCE = 6670;
	public static final int MOVE_STATEMENT_MULTIDIMENSIONAL_BYNAME_OR_BYPOSITION_TARGET = 6671;
	public static final int MOVE_STATEMENT_INCOMPATIBLE_TYPES = 6672;
	public static final int FUNCTION_ARG_LITERAL_NOT_VALID_WITH_INOUT_DATETIME_PARAMETER = 6673;		
	public static final int FUNCTION_ARG_REQUIRES_IN_PARAMETER = 6674;
	public static final int FUNCTION_ARG_LITERAL_NOT_VALID_WITH_OUT_PARAMETER = 6675;
	
	public static final int MOVE_STATEMENT_TARGET_WRONG_TYPE_FOR_SCALAR_SOURCE = 6676;
	public static final int MOVE_STATEMENT_TARGET_WRONG_TYPE_FOR_CONTAINER_SOURCE = 6677;
	public static final int MOVE_STATEMENT_TARGET_WRONG_TYPE_FOR_ARRAY_SOURCE = 6678;
	
	// messages for function invocations
	public static final int FUNCTION_REFERENCE_CANNOT_BE_RESOLVED_CONTEXT = 6679;
	public static final int FUNCTION_REFERENCE_AMBIGUOUS_CONTEXT = 6680;
	public static final int FUNCTION_ARGUMENTS_DONT_MATCH = 6681;
	public static final int NULLABLE_ARGUMENT_NOT_SQL_ITEM = 6682;
	public static final int FIELD_ARGUMENT_NOT_FORM_FIELD = 6683;
	public static final int STATIC_ARRAY_CANT_BE_ARGUMENT = 6684;
	public static final int DYNAMIC_ARRAY_CANT_BE_ARGUMENT = 6685;
	public static final int INVALID_ARGUMENT_TYPE = 6686;
	public static final int ARRAY_FUNCTION_USED_WITHOUT_DYNAMIC_ARRAY = 6687;
	public static final int ARRAY_ELEMENT_ARGUMENT_INCORRECT_TYPE = 6688;
	public static final int MAXIMUMSIZE_ARGUMENT_INCORRECT = 6689;
	
	public static final int NOCURSOR_REQUIRES_KEY_ITEM = 6690;	
	
	// messages for return statement
	public static final int RETURN_STATEMENT_TYPE_INCOMPATIBLE = 6691;
	public static final int RETURN_EXPRESSION_INVALID = 6692;
	public static final int RETURN_TARGET_NOT_ITEM = 6693;
	
	// messages for set statement
	public static final int INVALID_SET_STATEMENT_DATA_REFERENCE = 6694;
	public static final int INVALID_SET_STATE_FOR_ITEM = 6695;
	public static final int INVALID_SET_STATE_FOR_SQL_ITEM = 6696;
	public static final int INVALID_SET_STATE_FOR_RECORD = 6697;
	public static final int INVALID_SET_STATE_FOR_TEXT_FORM = 6699;
	public static final int INVALID_SET_STATE_FOR_PRINT_FORM = 6700;
	public static final int INVALID_SET_STATE_FOR_TEXT_FIELD = 6701;
	public static final int MOVE_BY_POSITION_INCOMPATIBLE_TYPES = 6702;		
	public static final int SYSTEM_ARGS_ONLY_ITEMS_LITERALS = 6703;
	public static final int FUNCTION_ARG_CANNOT_BE_NULL = 6704;
	
	public static final int CALL_ARGUMENT_REQUIRES_PROGRAM = 6706;
	public static final int PROGRAM_ARGS_DONT_MATCH_PARAMS = 6707;
	public static final int PAGEHANDLER_ARGS_DONT_MATCH_PARAMS = 6708;
	public static final int FLEXIBLE_RECORD_PASSED_TO_NON_EGL_PROGRAM = 6709;
	
	public static final int USED_LIBRARY_RECORD_USED_FOR_IO = 6710;
	
	public static final int PROGRAM_INPUT_RECORD_DOESNT_MATCH_PARAM = 6711;
	public static final int FORWARD_TARGET_DOESNT_HAVE_ONPAGELOAD_FUNCTION = 6712;
	
	public static final int FUNCTION_ARG_NOT_REFERENCE_COMPATIBLE_WITH_PARM = 6716;
	
	public static final int OCCURED_ITEM_MOVE_OPERAND_NOT_SUBSCRIPTED = 6717;
	public static final int NON_CONTAINER_MOVE_OPERAND_MOVED_BY_NAME_OR_POSITION = 6718;
	
	public static final int INVALID_APPENDALL_ARG = 6719;
	
	public static final int FUNCTION_INVOCATION_USED_IN_ISNOT_WITHOUT_NULL = 6720;
	public static final int INVALID_EXPRESSION_IN_ISNOT = 6721;
	
	public static final int FLEXIBLE_RECORD_ARRAYS_MOVED_BYNAME_OR_BYPOSITION = 6722;
	public static final int PROGRAM_INPUT_UIRECORD_DOESNT_MATCH_PARAM = 6723;
	
	public static final int SUBSTRING_USED_IN_ISNOT_WITH_NULL_OR_TRUNC = 6725;
	
	public static final int EXIT_MODIFIER_ONLY_ALLOWED_IN_PROGRAM = 6726;
	public static final int EXIT_MODIFIER_NOT_ALLOWED_IN_SERVICE = 6727;
	
	public static final int GOTO_LABEL_NOT_ACCESSIBLE = 6728;	
	//public static final int GOTO_LABEL_OUTSIDE_ONEVENT_BLOCK = 6728;

	public static final int FUNCTION_ARG_NOT_COMPATIBLE_WITH_SPECIAL_PARM_RECORD_OR_DICTIONARY = 6729;
	
	public static final int FUNCTION_ARG_NOT_ASSIGNMENT_COMPATIBLE_WITH_PARM = 6731;
	public static final int FUNCTION_ARG_NOT_COMPATIBLE_WITH_SPECIAL_PARM_ANYEGL = 6732;
	public static final int FUNCTION_ARG_NOT_COMPATIBLE_WITH_SPECIAL_PARM_ARRAYORTABLE = 6733;
	public static final int FUNCTION_ARG_NOT_COMPATIBLE_WITH_SPECIAL_PARM_ATTRIBUTE = 6734;
	public static final int FUNCTION_ARG_NOT_COMPATIBLE_WITH_SPECIAL_PARM_CONSOLEFORM = 6735;
	public static final int CONVERT_TARGET_INVALID = 6736;
	public static final int FUNCTION_ARG_NOT_COMPATIBLE_WITH_SPECIAL_PARM_ITEMORRECORD = 6737;
	public static final int FUNCTION_ARG_NOT_COMPATIBLE_WITH_SPECIAL_PARM_RECORD = 6738;
	public static final int FUNCTION_ARG_NOT_COMPATIBLE_WITH_SPECIAL_PARM_SERVICEORINTERFACE = 6739;
	public static final int FUNCTION_ARG_NOT_COMPATIBLE_WITH_SPECIAL_PARM_TEXTFIELD = 6740;
	public static final int FUNCTION_ARG_NOT_COMPATIBLE_WITH_SPECIAL_PARM_VAGTEXT = 6741;
	public static final int FUNCTION_ARG_NOT_COMPATIBLE_WITH_SPECIAL_PARM_VAGTEXTORNUMERIC = 6742;
	public static final int FUNCTION_ARG_NOT_COMPATIBLE_WITH_LOOSE_PARM = 6743;
	public static final int FUNCTION_ARG_NOT_COMPATIBLE_WITH_LOOSE_NUMERIC_PARM= 6744;
	public static final int FUNCTION_ARG_NOT_COMPATIBLE_WITH_IO_RECORD_PARM = 6745;
	
	public static final int FUNCTION_REFERENCE_CANNOT_BE_RESOLVED = 6746;
	public static final int FUNCTION_REFERENCE_AMBIGUOUS = 6747;
	
	public static final int DATAITEM_CONTEXT_SPECIFIC_PROBLEM = 6748;
	
	public static final int MIXED_LITERAL_TYPES_IN_ARRAY_LITERAL = 6750;
	public static final int FUNCTION_INVOCATION_TARGET_NOT_FUNCTION_OR_DELEGATE = 6751;

	public static final int CONVERTBIDI_TARGET_INVALID = 6752;
	public static final int CONVERTBIDI_CONVTABLE_INVALID = 6753;
	
	public static final int FUNCTION_ARG_CANNOT_BE_THIS = 6754;
	public static final int CONSTRUCTOR_CALL_WRONG_PLACE = 6756;

	public static final int CANNOT_ASSIGN_NULL = 6757;
	public static final int FUNCTION_ARG_CANNOT_BE_SUPER = 6758;

	public static final int MOVE_MODIFIER_INVALID = 6760;
	public static final int MOVE_MUST_BE_REFERENCE = 6761;
	public static final int MOVE_EXTERNALTYPE = 6762;
	

	//7500-7599: Used for VAGCompatability messages		
	public static final int CALL_OPTIONS_ONLY_VALID_IF_VAGCOMPATIBLE = 7501;
	
	// messages for display statement
	public static final int STATEMENT_TARGET_MUST_BE_TEXT_FORM = 7502;		
	public static final int STATEMENT_TARGET_MUST_BE_FORM = 7503;		
	public static final int STATEMENT_TARGET_MUST_BE_PRINT_FORM = 7504;		
	
	// messages for forwad statement
	public static final int FORWARD_STATEMENT_CANNOT_CONTAIN_RETURNING_TO = 7505;		 
	 
	// messages for transfer statement
	public static final int TRANSFER_TO_TRANSACTION_NOT_ALLOWED = 7506;
	
	public static final int SHOW_STATEMENT_TARGET_WRONG_TYPE = 7507;
	public static final int SHOW_STATEMENT_TARGET_MUST_BE_UIRECORD = 7508;
	
	// messages for set values block
	public static final int POSITIONAL_PROPERTY_NOT_VALID_FOR = 7510;
	public static final int SET_VALUES_BLOCK_NOT_VALID_AS_FUNC_ARG = 7511;
	public static final int SET_VALUES_BLOCK_OR_CONSTRUCTOR_NOT_VALID_AS_CONDITIONAL_OPERAND = 7512;
	public static final int SET_VALUES_BLOCK_NOT_VALID_AS_CASE_CRITERION = 7513;
	public static final int SET_VALUES_BLOCK_NOT_VALID_AS_WHEN_CLAUSE = 7514;
	public static final int SET_VALUES_BLOCK_NOT_VALID_AS_CALL_ARG = 7515;
	public static final int SET_VALUES_BLOCK_NOT_VALID_AS_RETURN_ARG = 7516;
	public static final int SET_VALUES_BLOCK_NOT_VALID_HERE = 7517;
	public static final int THISARRAY_ONLY_IN_SET_VALUES_BLOOCK = 7518;
	public static final int SERVICE_AND_INTERFACE_EMPTY_BLOCK = 7519;

	public static final int ARG_MUST_BE_MATH_NUMERIC_ITEM = 7550;		 
	public static final int ARG_MUST_BE_MATH_INTEGER_ITEM = 7551; //$N
	public static final int ARG_MUST_BE_NUMERIC_ITEM_CONSTANT_OR_LITERAL = 7552;
	public static final int ARG_MUST_BE_STRING_ITEM_CONSTANT_OR_LITERAL = 7553;
	public static final int ARG_MUST_BE_STRING_CONSTANT_OR_LITERAL = 7554;
	public static final int ARG_MUST_BE_NUMERIC_EXPRESSION = 7555;
	public static final int ARG_MUST_BE_STRING_EXPRESSION = 7556;
	public static final int ARG_MUST_BE_INTEGER_EXPRESSION = 7557;
	public static final int ARG_MUST_HAVE_NO_DECIMALS = 7558;
	
	public static final int DATETIME_LITERAL_CANNOT_START_OR_END_WITH_DELIM = 7600;
	public static final int TIMESTAMP_LITERAL_YEAR_FIELD_TOO_LONG = 7615;
	public static final int TIMESTAMP_LITERAL_MONTH_FIELD_TOO_LONG = 7616;
	public static final int TIMESTAMP_LITERAL_DAY_FIELD_TOO_LONG = 7617;
	public static final int TIMESTAMP_LITERAL_HOUR_FIELD_TOO_LONG = 7618;
	public static final int TIMESTAMP_LITERAL_MINUTES_FIELD_TOO_LONG = 7619;
	public static final int TIMESTAMP_LITERAL_SECONDS_FIELD_TOO_LONG = 7620;
	public static final int TIMESTAMP_LITERAL_SECOND_FRACTIONS_FIELD_TOO_LONG = 7621;
	
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
	public static final int TIMESTAMP_LITERAL_MUST_BE_PATTERN_LENGTH = 7637;
	public static final int TIMESTAMP_LITERAL_MUST_HAVE_AS_MANY_FIELDS_AS_PATTERN = 7638;
	public static final int TIMESTAMP_LITERAL_YEAR_FIELD_DOESNT_MATCH_PATTERN = 7639;
	public static final int TIMESTAMP_LITERAL_MONTH_FIELD_DOESNT_MATCH_PATTERN = 7640;
	public static final int TIMESTAMP_LITERAL_DAY_FIELD_DOESNT_MATCH_PATTERN = 7641;
	public static final int TIMESTAMP_LITERAL_HOUR_FIELD_DOESNT_MATCH_PATTERN = 7642;
	public static final int TIMESTAMP_LITERAL_MINUTES_FIELD_DOESNT_MATCH_PATTERN = 7643;
	public static final int TIMESTAMP_LITERAL_SECONDS_FIELD_DOESNT_MATCH_PATTERN = 7644;
	public static final int TIMESTAMP_LITERAL_SECOND_FRACTIONS_FIELD_DOESNT_MATCH_PATTERN = 7645;
	public static final int DATETIME_LITERAL_MONTH_OUT_OF_RANGE = 7646;
	public static final int DATETIME_LITERAL_DAY_OUT_OF_RANGE = 7647;
	public static final int DATETIME_LITERAL_HOUR_OUT_OF_RANGE = 7648;
	public static final int DATETIME_LITERAL_MINUTE_OUT_OF_RANGE = 7649;
	public static final int DATETIME_LITERAL_SECOND_OUT_OF_RANGE = 7650;
	public static final int EXTEND_TIMESTAMP_VALUE_ARGUMENT_WRONG_TYPE = 7651;
	public static final int FORMAT_DATE_ARGUMENT1_WRONG_TYPE = 7652;
	public static final int FORMAT_TIME_ARGUMENT1_WRONG_TYPE = 7653;
	public static final int FORMAT_TIMESTAMP_ARGUMENT1_WRONG_TYPE = 7654;
	public static final int INCORRECT_UNICODE_LENGTH_IN_UNICODE_CONVERSION_FUNCTION = 7655;
	
	public static final int DUPLICATE_PROPERTY_VALUES_FOR_OUTLINE_PROPERTY = 7660;
	public static final int INVALID_CONSTANT_PROPERTY_VALUES_FOR_OUTLINE_PROPERTY = 7661;
	
	public static final int PROPERTY_KEY_ITEMS_MUST_BE_STRING = 7662;
	
	public static final int PROPERTY_VALIDVALUES_INVALID_FORMAT = 7670;
	public static final int PROPERTY_VALIDVALUES_INVALID_RANGE_VALUE = 7671;
	public static final int PROPERTY_VALIDVALUES_INVALID_VALUE_TYPE = 7672;
	
	public static final int VALUEITEM_OR_LABELITEM_CANNOT_BE_ARRAY = 7680;
	public static final int SELECTEDROWITEM_TYPE_INVALID_FOR_RECORD_ARRAY_OR_TABLE = 7681;
	public static final int SELECTEDVALUEITEM_VALUEITEM_TYPE_MISMATCH = 7682;
	public static final int VALUEITEM_PRIMITIVEARRAY_OR_COLUMN_TARGET_TYPE_MISMATCH = 7683;
	public static final int PROPERTY_INVALID_FOR_ARRAYS = 7684;
	public static final int ONPRERENDERFUNCTION_ONCONSTRUCTION_FUNCTION_PARAMETER_MISMATCH_NUMBER = 7685;
	public static final int ONPRERENDERFUNCTION_ONCONSTRUCTION_FUNCTION_PARAMETER_MISMATCH_TYPE = 7686;
	public static final int LABELITEM_MUST_BE_NON_HEX_OR_BLOB_PRIMITIVE = 7687;
	public static final int SELECTEDROWITEM_TYPE_INVALID_FOR_ITEM_ARRAY = 7688;
	public static final int SELECTION_ITEM_MUST_BE_TOP_LEVEL_AND_LEAF = 7689;	
	public static final int TYPE_NOT_VALID_IN_BOOLEAN_EXPRESSION = 7690;
	public static final int ONVALUECHANGEFUNCTION_NOT_ONE_PARAMETER = 7691;
	public static final int ONVALUECHANGEFUNCTION_PARAMETER_TYPE_INVALID = 7692;
	public static final int PROPERTY_ONLY_VALID_FOR_ARRAYS = 7693;
	public static final int BLOB_OR_HEX_USED_WITH_SELECTION_PROPERTY = 7694;
	public static final int SELECTION_ITEM_MAY_NOT_BE_LIBRARY_FIELD = 7695;
	public static final int VALIDATOR_DATATABLE_MATCH_VALID_COLUMN_TYPE_MISMATCH = 7696;
	public static final int VALIDATOR_DATATABLE_RANGECHK_COLUMN_TYPE_MISMATCH = 7697;
	
//	 7700 - 7800: Used for ConsoleUI Validation
	public static final int OPENUI_TARGETTYPE = 7700; 
 	public static final int OPENUI_MUST_BE_CONSOLEFIELD = 7701;
 	public static final int OPENUI_BIND_NOT_ALLOWED = 7702; 
 	public static final int OPENUI_BIND_EXACTLY_ONE = 7703; 
 	public static final int OPENUI_BIND_TOO_MANY = 7704; 
 	public static final int OPENUI_BIND_MUST_BE_TEXT = 7705; 
 	public static final int OPENUI_BIND_READ_ONLY = 7706; 
 	public static final int OPENUI_EVENTTYPE_INVALID = 7707; 
 	public static final int OPENUI_EVENTARG_NOT_ALLOWED = 7708; 
 	public static final int OPENUI_EVENTARG_REQUIRED = 7709; 
	
	public static final int CONSOLEFIELD_DECLARATIONS_ONLY_ALLOWED_IN_CONSOLEFORMS = 7710;
	public static final int ONLY_DATAITEMS_ALLOWED_AS_PARAMETER_OR_RETURN_IN_REPORTHANDLER = 7711;
	
	public static final int CONSOLEFIELD_DECLARATION_REQUIRES_FIELDLEN= 7722;
	
	public static final int MENU_DECLARATION_REQUIRES_LABELKEY_OR_LABELTEXT = 7724;
	public static final int INVALID_DATATYPE_PROPERTY = 7725;
	
	public static final int INVALID_PROPERTY_MUST_BE_TWO_POSITIVE_INTEGERS = 7728;
	
	public static final int READONLY_FIELD_CANNOT_BE_ASSIGNED_TO = 7740;
	public static final int READONLY_FIELD_CANNOT_BE_PASSED_TO_OUT_PARM = 7741;

	public static final int CANNOT_ASSIGN_TO_ARRAY_DICTIONARY_ELEMENTS = 7744;
	public static final int INVALID_INTO_ITEM_FOR_GET_SQL_RECORD_ARRAY=7745;		
	public static final int UNSUPPORTED_FIELD_IN_COMPLEX_ANNOTATION = 7746;
	public static final int INVALID_ELEMENT_FOR_ANNOTATION_ARRAY_PROPERTY = 7747;
	public static final int REQUIRED_FIELD_IN_COMPLEX_ANNOTATION = 7748;
	public static final int FIELD_X_REQUIRES_FIELD_Y_SPECIFICATION = 7749;
	
	public static final int ANNOTATION_REQUIRES_ONE_OF = 7751;
	public static final int INITIALIZERS_NOT_ALLOWED_IN_DATA_TABLE = 7752;
	public static final int INITIALIZERS_ONLY_ALLOWED_ON_LEAF_ITEMS = 7753;
	public static final int LOCAL_REFERENCE_VARIABLES_CANNOT_BE_USED_IN_ONEVENT_BLOCK = 7754;
	public static final int UNREACHABLE_CODE = 7755;
	public static final int NON_MULTIPLY_OCCURING_ITEM_CANNOT_BE_INITIALIZED_WITH_ARRAY = 7756;
	public static final int TOO_MANY_ELEMENTS_IN_STRUCTURE_ITEM_ARRAY_INITIALIZER = 7757;
	public static final int MULTI_DIMENSIONAL_OCCURING_ITEM_HAS_NON_ARRAY_INITIALIZER = 7758;	
	
	public static final int RUNTIME_NAME_OF_WEB_PROGRAM_EXCEEDS_8_CHARACTERS = 7761;
	
	public static final int INVALID_TYPE_IN_VGUIRECORD = 7770;
	public static final int USE_FORMGROUP_NOT_VALID_IN_WEBPROGRAM = 7772;
	public static final int ARRAY_OF_UIRECORDS_NOT_ALLOWED = 7773;
	public static final int CLOSE_PRINTFORM_NOT_ALLOWED_IN_WEB_TRANSACTION = 7774;
	public static final int VGWEBTRANSACTION_NOT_VALID_TRANSER_TO_TRANSACTION_TARGET = 7775;
	public static final int BAD_TYPE_FOR_TRANSFER_TO_PROGRAM_IN_VGWEBTRANSACTION = 7776;
	public static final int FORWARD_TO_URL_TARGET_MUST_BE_CHARACTER = 7777;
	public static final int ARGUMENTS_NOT_ALLOWED_ON_FORWARD_TO_URL = 7778;
	public static final int SHOW_UIRECORD_ONLY_VALID_IN_VGWEBTRANSACTION = 7779;
	public static final int FIXED_RECORD_ITEM_INITIALIZERS_MUST_BE_LITERAL_VALUES = 7780;
	public static final int MULTI_DIMENSIONAL_MULTI_OCCURING_ITEMS_NOT_ALLOWED_IN_VGUIRECORD = 7781;
	public static final int ITEM_NAMES_MUST_BE_UNIQUE_IN_VGUIRECORD = 7782;
	public static final int COMMAND_VALUE_ITEM_PROPERTY_VALUE_CANNOT_EQUAL_RECORD_NAME = 7783;
	public static final int COMMAND_VALUE_ITEM_ITEM_WRONG_TYPE = 7784;
	public static final int PROPERTY_NOT_ALLOWED_WITH_UITYPE_OF = 7785;
	public static final int CONFLICT_WITH_NUMELEMENTSITEM_COMMANDVALUEITEM_SELECTEDINDEXITEM_ITEMNAME = 7786;
	public static final int PROPERTY_ONLY_APPLICABLE_TO_MULTIPLY_OCCURING_ITEMS = 7787;
	public static final int PROPERTY_REFERENCE_MUST_BE_NUMERIC_WITH_NO_DECIMALS = 7788;
	public static final int PROPERTY_REFERENCE_CANNOT_BE_MUTLIPLY_OCCURING = 7789;
	public static final int SELECTEDINDEXITEM_REFERENCE_MUST_HAVE_SAME_NUMBER_OF_OCCURS = 7790;
	public static final int PROPERTY_REQUIRED_WHEN_UITYPE_IS_PROGRAMLINK_OR_UIFORM = 7791;
	public static final int FILLER_ITEMS_REQUIRE_UITYPE_NONE = 7792;
	public static final int PROPERTY_NOT_VALID_WITH_ANY_OF_PROPERTIES = 7793;
	public static final int PROPERTY_ONLY_VALID_FOR_CHARACTER_PRIMITIVE = 7794;
	public static final int UITYPE_OF_REQUIRES_PRIMITIVE_TYPE_OF = 7795;
	public static final int VALIDATION_ORDER_VALUES_INVALID = 7796;
	
	public static final int VALUEREF_VALUE_CANNOT_BE_QUALIFIED_OR_SUBSCRIPTED = 7798;
	public static final int VALUEREF_NOT_RESOLVED = 7799;
	
	public static final int INTERFACE_IS_AMBIGUOUS = 7800;
	public static final int PART_MUST_IMPLEMENT_AN_INTERFACE = 7801;
	public static final int SERVICE_CANNOT_BE_USED_BY_SERVICE = 7802;
	public static final int LOOSE_TYPES_NOT_ALLOWED_IN_SERVICE_OR_INTERFACE_FUNC_PARM = 7803;
	public static final int TYPE_NOT_ALLOWED_IN_SERVICE_OR_INTERFACE_FUNC_PARM = 7804;
	public static final int TYPE_NOT_ALLOWED_AS_SERVICE_OR_INTERFACE_FUNC_RETURN = 7805;
	public static final int FIELD_NOT_ALLOWED_IN_SERVICE_OR_INTERFACE_FUNC_PARM = 7806;
	public static final int STATIC_FUNCTIONS_ONLY_ALLOWED_IN_JAVAOBJECT_INTERFACE = 7807;
	public static final int EXTENDS_CLAUSE_ONLY_ALLOWED_IN_JAVAOBJECT_INTERFACE = 7808;
	public static final int INTERFACE_FUNCTION_CANNOT_BE_PRIVATE = 7810;		
	public static final int SERVICE_CANNOT_USE_FORM_GROUP = 7811;
	public static final int OVERRIDEN_FUNCTION_PARAMETERS_DONT_MATCH = 7812;
	public static final int OVERRIDEN_RETURN_TYPES_DONT_MATCH = 7813;
	public static final int FUNCTIONS_NOT_ALLOWED_IN_SERVICEBINDINGLIBRARY = 7814;
	public static final int TWO_PROPERTIES_ARE_MUTUALLY_EXCLUSIVE = 7815;
	public static final int PROPERTY_ONLY_ALLOWED_IN_SERVICEBINDINGLIBRARY = 7816;
	public static final int PROPERTY_XSD_BASE_NOT_COMPATIBLE_WITH_PRIMITIVE = 7817;
	public static final int PROPERTY_XSD_BASE_NEEDS_ARRAY_OF_PRIMITIVES = 7818;
	public static final int SERVICE_OR_INTERFACE_CANNOT_BE_CALL_PARM = 7819;
	public static final int SERVICE_OR_INTERFACE_ARRAYS_NOT_SUPPORTED = 7820;
	public static final int VIEWROOTVAR_NOT_FOUND = 7821;
	public static final int VIEWROOTVAR_NOT_JAVAOBJECT_INTERFACE = 7822;
	public static final int RECORDS_CANNOT_BE_PARAMETERS_IN_JAVAOBJECT_INTERFACE = 7823;
	public static final int IN_MODIFIER_REQUIRED_FOR_JAVAOBJECT_FUNCTION_PARAMETERS = 7824;
	public static final int NEW_NOT_SUPPORTED_FOR_SERVICE_OR_INTERFACE = 7825;
	public static final int IN_MODIFIER_REQUIRED_FOR_JAVASCRIPTOBJECT_FUNCTION_PARAMETERS = 7826;

	public static final int MATCHING_CONSTRUCTOR_CANNOT_BE_FOUND = 7830;
	
	public static final int BOTH_PROPERTIES_REQUIRED_IF_ONE_SPECIFIED = 7857;

	public static final int FORUPDATE_NOT_ALLOWED_WITH_ARRAY_TARGET = 7872;
	public static final int LEVEL_NUMBERS_NOT_ALLOWED_IN_RECORD = 7875;
    public static final int DUPLICATE_RELATIONSHIP_IN_HIERARCHY = 7883;
    
    public static final int FUNCTION_NOT_VALID_AS_LVALUE = 7954;
    
    public static final int VALIDATION_ERROR_COMPILING_BINARY_FUNCTION = 7999;    
}
