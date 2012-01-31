/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.util;


import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.edt.compiler.internal.interfaces.IEGLMessageContributor;
import org.eclipse.edt.compiler.internal.interfaces.IEGLNestedMessageContributor;
import org.eclipse.edt.compiler.internal.interfaces.IEGLStatementNode;


/**
 * Insert the type's description here.
 * Creation date: (9/7/2001 9:06:42 AM)
 * @author: 
 */
public class EGLMessage extends Object  implements IGenerationResultsMessage{

	protected int severity = 0;
	private String id = null;
	private String[] params = null;
	private String groupName = null;
	private int startLineNumber = 0;
	private int endLineNumber = 0;
	private int endOffset = 0;
	private int startOffset = 0;

	private IEGLMessageContributor messageContributor;
	private int startColumnNumber = 0;
	private int endColumnNumber = 0;

	private String builtMessage;

	private static ResourceBundle messageBundle;
	private String partName;

	/**
	 * @return Returns the partName.
	 */
	public String getPartName() {
		return partName;
	}
	/**
	 * @param partName The partName to set.
	 */
	public void setPartName(String partName) {
		this.partName = partName;
	}
	/**
	 * An object that fills in the blanks in the error messages.
	 */
	private static final MessageFormat formatter = new MessageFormat(""); //$NON-NLS-1$

	//Severities:
	public static final int EGL_ERROR_MESSAGE = 1;
	public static final int EGL_WARNING_MESSAGE = 2;
	public static final int EGL_INFORMATIONAL_MESSAGE = 3;

	//Groups:
	public static final String EGLMESSAGE_GROUP_VALIDATION = "Validation"; //$NON-NLS-1$
	public static final String EGLMESSAGE_GROUP_DEPLOYMENT = "Deployment"; //$NON-NLS-1$
	public static final String EGLMESSAGE_GROUP_EDITOR = "Editor"; //$NON-NLS-1$
	public static final String EGLMESSAGE_GROUP_XML_VALIDATION = "XML Validation"; //$NON-NLS-1$
	public static final String EGLMESSAGE_GROUP_INPUT = "Batch Input"; //$NON-NLS-1$
	public static final String EGLMESSAGE_GROUP_STATEMENT_PARSER = "StatementParser"; //$NON-NLS-1$
	public static final String EGLMESSAGE_GROUP_DEBUGGER = "Debugger"; //$NON-NLS-1$
	public static final String EGLMESSAGE_VAG_MIGRATION = "VAGMigration"; //$NON-NLS-1$
	public static final String EGLMESSAGE_GROUP_SYNTAX = "Syntax"; //$NON-NLS-1$
	//	public static final String EGLMESSAGE_GROUP_WEB_SERVICES = "Web Services";

	//EGL Message ranges (ids):
	// 0000-0999: reserved (not used)
	// 1000-2999: Core/common
	// 3000-6999: Validation/Pre-processor
	// 7000-7999: Editor
	// 8000-8999: 
	// 9000-9999: 
 	public static final String EGLMESSAGE_INVALID_NAME_LENGTH = "3001"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_INVALID_CHARACTER_IN_NAME = "3002"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_EZE_NOT_ALLOWED = "3003"; //$NON-NLS-1$
//	public static final String EGLMESSAGE_NONNUMERIC_WITH_DECIMALS = "3005"; //$NON-NLS-1$
//	public static final String EGLMESSAGE_WORKING_STORAGE_NOT_IN_LIST = "3006"; //$NON-NLS-1$
//	public static final String EGLMESSAGE_INVALID_KEEP_AFTER_USE = "3007"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_DUPLICATE_PART_NAME = "3009"; //$NON-NLS-1$
//	public static final String EGLMESSAGE_DUPLICATE_VARIABLE_NAME = "3010"; //$NON-NLS-1$
//	public static final String EGLMESSAGE_DUPLICATE_PARAMETER_NAME = "3011"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_INVALID_IMPORT_STATEMENT = "3013"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_DUPLICATE_IMPORT_STATEMENT = "3014"; //$NON-NLS-1$
//	public static final String EGLMESSAGE_TOO_MANY_PARAMETERS = "3017"; //$NON-NLS-1$
//	public static final String EGLMESSAGE_INVALID_SQL_ITEM_PARM_TYPE = "3018"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_RESERVED_WORD_NOT_ALLOWED = "3019"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_IMPORT_FILE_NOT_FOUND = "3020"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_IMPORT_FOLDER_NOT_FOUND = "3021"; //$NON-NLS-1$
 
 	public static final String EGLMESSAGE_FILE_NAME_REQUIRED = "3023"; //$NON-NLS-1$
	public static final String EGLMESSAGE_PROGRAM_NAME_REQUIRED = "3024"; //$NON-NLS-1$
	public static final String EGLMESSAGE_SYMPARM_NAME_REQUIRED = "3025"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_INVALID_REMOTECOMTYPE_PARMFORM = "3026"; //$NON-NLS-1$
//	public static final String EGLMESSAGE_REDEFINES_NAME_REQUIRED = "3027"; //$NON-NLS-1$
//	public static final String EGLMESSAGE_KEY_NAME_REQUIRED = "3028"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_DATABASE_NAME_REQUIRED = "3029"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_SERVER_NAME_REQUIRED = "3030"; //$NON-NLS-1$

 	public static final String EGLMESSAGE_REMOTEPGMTYPE_NOTALLOWED = "3380"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_REMOTEPGMTYPE_REQUIRES_REMOTECOMTYPE = "3381"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_REMOTEPGMTYPE_REQUIRES_WEB_PROJ = "3382"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_TOO_MANY_PARMS = "3383"; //$NON-NLS-1$
 	
	public static final String EGLMESSAGE_INPUT_INVALID_EXTENSION = "3896"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_INPUT_INVALID_ARGUMENT_COMBINATION = "3897"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_PROPERTY_FILE_LOAD_FAILED = "3898"; //$NON-NLS-1$
	public static final String EGLMESSAGE_ACTION_FACTORY_LOAD_FAILED = "3899"; //$NON-NLS-1$

 	public static final String EGLMESSAGE_INPUT_UNKNOWN_OPTION = "3990"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_INPUT_FILE_WRONG_EXTENSION = "3991"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_ERROR_RETRIEVING_FILE_CONTENTS = "3992"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_INPUT_USAGE = "3993"; //$NON-NLS-1$
//	public static final String EGLMESSAGE_INPUT_BUILD_DESCRIPTOR_FILE_NOT_FOUND = "3994"; //$NON-NLS-1$
	public static final String EGLMESSAGE_INPUT_DUPLICATE_ARGUMENT = "3995"; //$NON-NLS-1$
	public static final String EGLMESSAGE_INPUT_NO_FILE_SPECIFIED = "3996"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_INPUT_FILE_NOT_FOUND = "3997"; //$NON-NLS-1$
	public static final String EGLMESSAGE_XML_VALIDATION_ERROR = "3998"; //$NON-NLS-1$
	public static final String EGLMESSAGE_XML_VALIDATION_ERROR_IN_FILE = "3999"; //$NON-NLS-1$

//	//4001 : Used for data structure editor messages (may change)!

 	public static final String EGLMESSAGE_EZEDEST_NOT_SUPPORTED_FOR_NON_IO_LOGICALFILE = "4103"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_VARIABLE_LENGTH_RECORD_NOT_SUPPORTED = "4104"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_FILE_NAME_INVALID = "4105"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_RECORDS_HAVE_DIFFERENT_TYPES = "4106"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_RECORDS_HAVE_DIFFERENT_KEYS = "4107"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_TEXT_AND_FLOAT_NOT_COMPAT = "4108"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_TRANSFER_TO_TRANSACTION_NOT_SUPPORTED_FOR_BATCH = "4109"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_FILE_RECORD_HAS_INVALID_LENGTH = "4110"; //$NON-NLS-1$

 	public static final String EGLMESSAGE_TRANSFER_TO_EXTERNAL_PROGRAM_NOT_SUPPORTED = "4111"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_TRANSFER_TO_TRANSACTION_NOT_SUPPORTED = "4112"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_ANNOTATION_NOT_SUPPORTED = "4113"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_EZECONCT_CONNECT_RESET_NOT_SUPPORTED = "4114"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_EZECONCT_UOW_NOT_SUPPORTED = "4115"; //$NON-NLS-1$

 	public static final String EGLMESSAGE_EZEDEST_NOT_SUPPORTED = "4116"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_EZEDESTP_NOT_SUPPORTED = "4117"; //$NON-NLS-1$

 	public static final String EGLMESSAGE_EXPRESSION_AS_RETURN_NOT_SUPPORTED = "4119"; //$NON-NLS-1$

 	public static final String EGLMESSAGE_SUBSTRING_TARGET_NOT_SUPPORTED = "4120"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_SUBSTRING_ARG_NOT_SUPPORTED = "4121"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_EXPRESSION_ARG_NOT_SUPPORTED = "4122"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_HEX_CONCATENATION_NOT_SUPPORTED = "4124"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_FUNCTION_IN_ARITHEMETIC_NOT_SUPPORTED = "4125"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_TIMESTAMP_FORMATS_MUST_MATCH = "4126"; //$NON-NLS-1$
 	
 	public static final String EGLMESSAGE_SECOND_ARG_MUST_BE_LITERAL= "4127"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_TYPE_NOT_SUPPORTED_FOR_WEBSERVICE= "4128"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_WEBSERVICE_PROTOCOL_REQUIRED= "4129"; //$NON-NLS-1$
	public static final String EGLMESSAGE_CONVERT_ARGUMENT_NOT_VALID= "4130"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_IN_OPERATOR_NOT_VALID= "4131"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_EGLSERVICE_PROGRAMCONTROLLED_NOT_ALLOWED= "4132"; //$NON-NLS-1$

 	public static final String EGLMESSAGE_EGLSERVICE_INVOCATION_NOT_ALLOWED= "4133"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_LITERAL_NOT_SUPPORTED= "4134"; //$NON-NLS-1$
 
 	public static final String EGLMESSAGE_DD_STATEFUL_JAVA400J2C_NOT_SUPPORTED= "4135"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_CALL_STATEFUL_JAVA400J2C_NOT_SUPPORTED= "4136"; //$NON-NLS-1$


 	public static final String EGLMESSAGE_VARIABLE_LENGTH_SERIAL_RECORD_NOT_SUPPORTED = "4137"; //$NON-NLS-1$

 	public static final String EGLMESSAGE_TYPE_NOT_SUPPORTED_FOR_RESTSERVICE = "4142"; //$NON-NLS-1$

//	//4200-4299: Used for Statments
 	public static final String EGLMESSAGE_SET_STATEMENT_OPTION_NOT_VALID_FOR_TARGET = "4206"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_SET_NULL_NOT_ALLOWED = "4207"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_SET_STATEMENT_EXTENDEDHIGHLIGHTS_MUTUALLY_EXCLUSIVE = "4208"; //$NON-NLS-1$

 	public static final String EGLMESSAGE_NULL_NOT_ALLOWED = "4209"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_LABEL_NOT_FOUND = "4210"; //$NON-NLS-1$

	public static final String EGLMESSAGE_GET_STATEMENT_DIRECTIVE_FILETYPE_RECORDTYPE_NOT_SUPPORTED_FOR_TARGETSYSTEM = "4215"; //$NON-NLS-1$
	public static final String EGLMESSAGE_GET_STATEMENT_REQUIRES_INTO = "4216"; //$NON-NLS-1$

	
	
	public static final String EGLMESSAGE_CONVERSE_IN_RECURSIVE_NOT_SUPPORTED = "4219"; //$NON-NLS-1$
	public static final String EGLMESSAGE_NO_INTO_ITEMS = "4220"; //$NON-NLS-1$
	public static final String EGLMESSAGE_FORUPDATE_INVALID_WITH_TERADATA = "4221"; //$NON-NLS-1$
	public static final String EGLMESSAGE_TOO_MANY_ARGS = "4222"; //$NON-NLS-1$
	public static final String EGLMESSAGE_STMT_NOT_VALID_IN_TYPEAHEADFUNCTION = "4223"; //$NON-NLS-1$
	public static final String EGLMESSAGE_DUPLICATE_RESULT_SET_ID_NOT_SUPPORTED_FOR_OPEN = "4224"; //$NON-NLS-1$	
	public static final String EGLMESSAGE_INVALID_SCREENSIZE = "4225"; //$NON-NLS-1$
	public static final String EGLMESSAGE_ARG_MUST_BE_FIXED_LEN = "4226"; //$NON-NLS-1$
	public static final String EGLMESSAGE_STMT_NOT_VALID_IN_VALIDATOR = "4227"; //$NON-NLS-1$
	public static final String EGLMESSAGE_INVALID_HELPFORM_NAME = "4228"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_DLIIO_COMMANDCODE_NOT_SUPPORTED = "4229"; //$NON-NLS-1$	
 	
	public static final String EGLMESSAGE_PASSING_RECORD_CANNOT_BE_FLEXIBLE = "4231"; //$NON-NLS-1$
	public static final String EGLMESSAGE_SYSTEM_DATA_NOT_VALID_AS_ARG = "4232"; //$NON-NLS-1$
	public static final String EGLMESSAGE_SUBSCRIPT_MUST_BE_DATAREF_OR_LITERAL = "4233"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_REMOTE_PROGRAM_NOT_SUPPORTED = "4234"; //$NON-NLS-1$
	public static final String EGLMESSAGE_SYSTEM_FUNCTION_NOT_VALID_AS_ARG = "4235"; //$NON-NLS-1$
	public static final String EGLMESSAGE_MSG_TABLE_REFERENCE_NOT_SUPPORTED_ON_TARGET = "4236"; //$NON-NLS-1$
	public static final String EGLMESSAGE_SPECIAL_FUNCTION_NOT_SUPPORTED_ON_TARGET = "4237"; //$NON-NLS-1$

	public static final String EGLMESSAGE_GET_STATEMENT_DIRECTIVE_NOT_SUPPORTED_FOR_TARGETSYSTEM = "4238"; //$NON-NLS-1$
	
	public static final String EGLMESSAGE_CALL_STATEMENT_RECURSIVE_CALL = "4241"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_CALL_STATEMENT_INVALID_REMOTE_VARIABLE_LENGTH_RECORD_ARG = "4242"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_CALL_STATEMENT_PARMFORM_MUST_BE_COMMPTR = "4243"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_CALL_STATEMENT_INVALID_REMOTE_ARG_SIZE = "4244"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_CALL_STATEMENT_INVALID_COMMDATA_ARG_SIZE = "4245"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_CALL_STATEMENT_REMOTE_DUPLICATE_ARG = "4246"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_CALL_STATEMENT_COMMDATA_DUPLICATE_ARG = "4247"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_CALL_STATEMENT_REMOTE_DUPLICATE_REDEFINED_ARG = "4248"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_CALL_STATEMENT_COMMDATA_DUPLICATE_REDEFINED_ARG = "4249"; //$NON-NLS-1$
	
	public static final String EGLMESSAGE_DUPLICATE_RESULT_SET_ID_NOT_SUPPORTED = "4250"; //$NON-NLS-1$	
	public static final String EGLMESSAGE_DUPLICATE_PREPARE_STATEMENT_ID_NOT_SUPPORTED = "4251"; //$NON-NLS-1$	

	public static final String EGLMESSAGE_AUDIT_ARG_MUST_BE_ITEM = "4252"; //$NON-NLS-1$
	public static final String EGLMESSAGE_OPENUI_NOT_SUPPORTED = "4253"; //$NON-NLS-1$
	public static final String EGLMESSAGE_DLIIO_NOT_SUPPORTED = "4254"; //$NON-NLS-1$
	public static final String EGLMESSAGE_PSB_INVALID = "4255"; //$NON-NLS-1$
	public static final String EGLMESSAGE_AUDIT_REQUIRES_PSB = "4256"; //$NON-NLS-1$
	public static final String EGLMESSAGE_PASSING_RECORD_TOO_BIG = "4257"; //$NON-NLS-1$
	public static final String EGLMESSAGE_SHOW_FORM_XFER_EXTERNAL_NOT_SUPPORTED = "4258"; //$NON-NLS-1$
	public static final String EGLMESSAGE_SHOW_FORM_PASSING_REC_NOT_SUPPORTED = "4259"; //$NON-NLS-1$
	public static final String EGLMESSAGE_TABLE_TOO_BIG = "4260"; //$NON-NLS-1$
	public static final String EGLMESSAGE_DLI_ANNOTATION_REQUIRED = "4261"; //$NON-NLS-1$
	public static final String EGLMESSAGE_INPUTFORM_NOT_SUPPORTED = "4262"; //$NON-NLS-1$
	public static final String EGLMESSAGE_VGWEBTRANSPROGRAM_NOT_SUPPORTED = "4263"; //$NON-NLS-1$
	public static final String EGLMESSAGE_INVALID_DATA_IN_ROW1_COL1 = "4264"; //$NON-NLS-1$
	public static final String EGLMESSAGE_VAR_FIELD_IN_ROW1_COL1 = "4265"; //$NON-NLS-1$
	public static final String EGLMESSAGE_NO_SPACE_FOR_REQUIRED_FIELDS = "4266"; //$NON-NLS-1$
	public static final String EGLMESSAGE_STARTTRANS_ARG_TOO_BIG = "4267"; //$NON-NLS-1$
	public static final String EGLMESSAGE_CONVERSE_FLOAT_FORM_NOT_SUPPORTED = "4268"; //$NON-NLS-1$
	public static final String EGLMESSAGE_DISPLAY_TEXT_FORM_NOT_SUPPORTED = "4269"; //$NON-NLS-1$
	public static final String EGLMESSAGE_PSB_OR_PCB_PARM_REQUIRED = "4270"; //$NON-NLS-1$
	public static final String EGLMESSAGE_RECORD_TYPE_NOT_SUPPORTED = "4271"; //$NON-NLS-1$
	
	public static final String EGLMESSAGE_CALLED_TEXT_NOT_SUPPORTED = "4273"; //$NON-NLS-1$
	public static final String EGLMESSAGE_GET_NEXT_SERIAL_NOT_SUPPORTED = "4274"; //$NON-NLS-1$
	public static final String EGLMESSAGE_IO_VGUIREC_NOT_SUPPORTED = "4275"; //$NON-NLS-1$
	public static final String EGLMESSAGE_NUM_LEN_TOO_BIG = "4276"; //$NON-NLS-1$
	public static final String EGLMESSAGE_ARG_CANNOT_BE_NIUM_FOR_STRING_FUNCTION = "4277"; //$NON-NLS-1$
	public static final String EGLMESSAGE_NON_SEGMENTED_NOT_SUPPORTED = "4278"; //$NON-NLS-1$
	public static final String EGLMESSAGE_PARMS_NOT_SUPPORTED = "4279"; //$NON-NLS-1$
	public static final String EGLMESSAGE_LOCAL_STORAGE_NOT_SUPPORTED = "4280"; //$NON-NLS-1$
	public static final String EGLMESSAGE_NULLABLE_TYPE_NOT_SUPPORTED = "4281"; //$NON-NLS-1$
	public static final String EGLMESSAGE_STMT_NOT_SUPPORTED = "4282"; //$NON-NLS-1$
	public static final String EGLMESSAGE_NUMERIC_LEN_TOO_BIG = "4283"; //$NON-NLS-1$
	public static final String EGLMESSAGE_OPERATOR_NOT_SUPPORTED = "4284"; //$NON-NLS-1$
	public static final String EGLMESSAGE_DYNAMICACCESS_NOT_SUPPORTED = "4285"; //$NON-NLS-1$

	public static final String EGLMESSAGE_ELAWORK_PCB_REQUIRED = "4286"; //$NON-NLS-1$
	public static final String EGLMESSAGE_CANNOT_REFERENCE_TP_PCB = "4287"; //$NON-NLS-1$
	
	public static final String EGLMESSAGE_FOREACH_NOT_SUPPORTED = "4288"; //$NON-NLS-1$
	public static final String EGLMESSAGE_CALL_NOT_SUPPORTED = "4289"; //$NON-NLS-1$
	public static final String EGLMESSAGE_IS_NOT_INVALID = "4290"; //$NON-NLS-1$	
	public static final String EGLMESSAGE_GSAM_PCB_REQUIRED = "4291"; //$NON-NLS-1$

	public static final String EGLMESSAGE_GET_PREVIOUS_INVALUD_FOR_SEQ_VASAM = "4292"; //$NON-NLS-1$

	
	//4300-4399: Used for Error Object messages (may change)!
	public static final String EGLMESSAGE_UNDEFINED_PART = "4300"; //$NON-NLS-1$
	public static final String EGLMESSAGE_UNDEFINED_PART_FOR_TYPEDEF = "4301"; //$NON-NLS-1$

	//4400-4499: Used for item validation messages (may change)!

	public static final String EGLMESSAGE_SQLDATACODE_NOT_SUPPORTED = "4412"; //$NON-NLS-1$
	public static final String EGLMESSAGE_DEPLOYMENT_DESCRIPTOR_NOT_FOUND = "4413"; //$NON-NLS-1$
	public static final String EGLMESSAGE_DEPLOYMENT_DESCRIPTOR_REQUIRED = "4414"; //$NON-NLS-1$
	public static final String EGLMESSAGE_INVALID_MAXARRAYSIZE = "4415"; //$NON-NLS-1$
	public static final String EGLMESSAGE_FLEXIBLE_RECORD_NOT_SUPPORTED = "4416"; //$NON-NLS-1$
	public static final String EGLMESSAGE_SET_VALUE_BLOCKS_NOT_SUPPORTED = "4417"; //$NON-NLS-1$
	public static final String EGLMESSAGE_TYPE_NOT_SUPPORTED = "4418"; //$NON-NLS-1$
	public static final String EGLMESSAGE_MULTI_DIMENSION_ARRAY_NOT_SUPPORTED = "4419"; //$NON-NLS-1$
	public static final String EGLMESSAGE_NON_LOCAL_SQL_SCOPE_NOT_SUPPORTED = "4420"; //$NON-NLS-1$
	public static final String EGLMESSAGE_FORM_FIELD_TYPE_NOT_SUPPORTED = "4421"; //$NON-NLS-1$
	public static final String EGLMESSAGE_NATIVELIBRARY_NOT_SUPPORTED = "4422"; //$NON-NLS-1$
	public static final String EGLMESSAGE_ANY_COMPARISON_NOT_SUPPORTED = "4423"; //$NON-NLS-1$
	
	public static final String EGLMESSAGE_LITERAL_LEN_TOO_BIG = "4424"; //$NON-NLS-1$
	public static final String EGLMESSAGE_FUNCTION_PARM_CANNOT_DO_IO = "4425"; //$NON-NLS-1$
	

	public static final String EGLMESSAGE_FUNCTION_RETURN_TYPE_NOT_SUPPORTED = "4425"; //$NON-NLS-1$
	public static final String EGLMESSAGE_PARENS_FOR_SIGN_NOT_SUPPORTED = "4426"; //$NON-NLS-1$
	public static final String EGLMESSAGE_FORMATTING_PROPERTIES_FOR_CURRENCY_NOT_SUPPORTED = "4427"; //$NON-NLS-1$
	
	public static final String EGLMESSAGE_FUNCTION_OVERLOADING_NOT_SUPPORTED = "4429"; //$NON-NLS-1$

	public static final String EGLMESSAGE_NO_LABELS_IN_BLOCK = "4430"; //$NON-NLS-1$
	public static final String EGLMESSAGE_TOO_MANY_BYPASS_KEYS = "4431"; //$NON-NLS-1$
	
	public static final String EGLMESSAGE_FUNCTION_OVERLOADING_NOT_SUPPORTED_IN_WEB_SERVICE = "4432"; //$NON-NLS-1$
	

//	//4500-4579: Used for SQL statement validation messages (may change)!

 	public static final String EGLMESSAGE_STATEMENT_VALIDATION_FAILED = "4513"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_CLASS_LOCATION_REQUIRED = "4514"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_COULD_NOT_BUILD_DEFAULT_STATEMENT = "4515"; //$NON-NLS-1$

	public static final String EGLMESSAGE_CONNECT_TO_DATABASE_FAILED = "4526"; //$NON-NLS-1$

	public static final String EGLMESSAGE_CANNOT_DERIVE_INTO = "4527"; //$NON-NLS-1$
	public static final String EGLMESSAGE_COLUMN_NAME_NOT_FOUND = "4528"; //$NON-NLS-1$
	public static final String EGLMESSAGE_DUPLICATE_COLUMN_NAME_FOUND = "4529"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_IO_OBJECT_CONTAINS_NO_STRUCTURE_ITEMS = "4530"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_IO_OBJECT_CONTAINS_NO_READ_WRITE_COLUMNS = "4531"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_IO_OBJECT_IS_SQL_JOIN = "4532"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_IO_OBJECT_CONTAINS_ONLY_KEY_OR_READONLY_COLUMNS = "4533"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_IO_OBJECT_NOT_SQL_RECORD = "4534"; //$NON-NLS-1$
//	public static final String EGLMESSAGE_IO_OBJECT_UNDEFINED_OR_NOT_SQL_RECORD_FOR_UPDATE_OR_SETUPD = "4535"; //$NON-NLS-1$
//	//	public static final String THIS_IS_AVAILABLE_FOR_REUSE = "4536";	
 	public static final String EGLMESSAGE_CONNECTION_ERROR_ON_VALIDATE = "4537"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_IO_OBJECT_REQUIRED_FOR_SQLEXEC = "4538"; //$NON-NLS-1$
//	public static final String EGLMESSAGE_INVALID_RECORD_TYPE_FOR_STATEMENTID = "4539"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_COULD_NOT_BUILD_DEFAULT_STATEMENT_FOR_SQL_RECORD = "4540"; //$NON-NLS-1$
  	//	public static final String THIS_IS_AVAILABLE_FOR_REUSE = "4541";
 	public static final String EGLMESSAGE_SQL_RECORD_CONTAINS_NO_STRUCTURE_ITEMS = "4542"; //$NON-NLS-1$
	
	// 4580-4599: Used for SQL retrieve messages.  In 5.1, these messages are only shown in the editor but in 6.0,
	// these messages are also used when creating an SQL record for generation.
	public static final String EGLMESSAGE_ERROR_CLASS_LOCATION_REQUIRED = "4580"; //$NON-NLS-1$
	public static final String EGLMESSAGE_ERROR_CONNECT_ERROR_ON_RETRIEVE = "4581"; //$NON-NLS-1$
	public static final String EGLMESSAGE_ERROR_TABLE_NOT_FOUND_ON_RETRIEVE = "4582"; //$NON-NLS-1$
	public static final String EGLMESSAGE_ERROR_TABLES_NOT_FOUND_ON_RETRIEVE = "4583";	//$NON-NLS-1$
 	public static final String EGLMESSAGE_INFO_UNSUPPORTED_SQL_TYPE_ON_RETRIEVE = "4584"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_INFO_DECIMAL_LENGTH_SHORTENED_FROM_ON_RETRIEVE = "4585"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_INFO_DECIMAL_DECIMALS_SHORTENED_FROM_ON_RETRIEVE = "4586"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_INFO_HEX_LENGTH_SHORTENED_FROM_ON_RETRIEVE = "4587"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_INFO_HEX_LENGTH_SHORTENED_ON_RETRIEVE = "4588"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_INFO_INVALID_LENGTH_SET_TO_ZERO_ON_RETRIEVE = "4589";	//$NON-NLS-1$
 	public static final String EGLMESSAGE_INFO_CHAR_LENGTH_SHORTENED_FROM_ON_RETRIEVE = "4590"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_INFO_DBCHAR_LENGTH_SHORTENED_FROM_ON_RETRIEVE = "4591"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_INFO_UNICODE_LENGTH_SHORTENED_FROM_ON_RETRIEVE = "4592"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_INFO_INVALID_DECIMALS_SET_TO_ZERO_ON_RETRIEVE = "4593";	//$NON-NLS-1$
 	public static final String EGLMESSAGE_ERROR_NO_CONNECTION_OBJECT_SELECTED = "4594";	//$NON-NLS-1$
 	public static final String EGLMESSAGE_INFO_UNSUPPORTED_TYPE_FOR_COBOL_ON_RETRIEVE = "4595";	//$NON-NLS-1$
 	
 	//4600-4699: Used for Build Descriptor validation messages (may change)!
 	public static final String EGLMESSAGE_BD_PROJECTID_TOO_LONG = "4600"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_BD_INVALID_TWAOFFSET = "4601"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_BD_INVALID_PACKAGE = "4602"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_BD_DUPLICATE_SYMPARM = "4603"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_BD_INVALID_DESTPORT = "4604"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_BD_SELF_REFERENCING = "4605"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_BD_INTEGER_VALUE_INVALID = "4606"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_BD_DECIMAL_SYMBOL_TOO_BIG = "4607"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_BD_GENDIR_MUST_BE_ABSOLUTE = "4608"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_BD_DBMS_NOT_VALID = "4609"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_BD_REQUIRED_VALUE_MISSING = "4610"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_BD_RESERVEDWORD_FILE_ERROR = "4611"; //$NON-NLS-1$
 
 	public static final String EGLMESSAGE_SYMPARM_KEY_MISSING = "4612"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_BD_GENPROJECT_NOT_ALLOWED = "4613"; //$NON-NLS-1$
	public static final String EGLMESSAGE_BD_NOT_FOUND = "4614"; //$NON-NLS-1$
	public static final String EGLMESSAGE_PROJECT_NOT_FOUND = "4615"; //$NON-NLS-1$
	public static final String EGLMESSAGE_TERADATA_NOT_ACTIVE = "4616"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_BD_GENPROJECT_INVALID = "4617"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_BD_SYSTEM_NOT_ENABLED = "4618"; //$NON-NLS-1$
 
	public static final String EGLMESSAGE_BD_GENDIRECTORY_OR_GENPROJECT_NEEDED = "4619"; //$NON-NLS-1$
	public static final String EGLMESSAGE_BD_BOTH_GENDIRECTORY_AND_GENPROJECT_NOT_ALLOWED = "4620"; //$NON-NLS-1$

	public static final String EGLMESSAGE_BD_LONG_GREGORIAN_MASK_INVALID = "4621"; //$NON-NLS-1$
	public static final String EGLMESSAGE_BD_LONG_JULIAN_MASK_INVALID = "4622"; //$NON-NLS-1$
	public static final String EGLMESSAGE_BD_SHORT_GREGORIAN_MASK_INVALID = "4623"; //$NON-NLS-1$
	public static final String EGLMESSAGE_BD_SHORT_JULIAN_MASK_INVALID = "4624"; //$NON-NLS-1$
	public static final String EGLMESSAGE_BD_GEN_PROJECT_MUST_BE_EGLWEB = "4625"; //$NON-NLS-1$
	public static final String EGLMESSAGE_BD_GEN_PROJECT_MUST_BE_JAVA = "4626"; //$NON-NLS-1$
	public static final String EGLMESSAGE_GEN_PROJECT_OVERRIDDEN = "4627"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_WORKSPACE_REQUIRED = "4628"; //$NON-NLS-1$
	public static final String EGLMESSAGE_GEN_PROJECT_OVERRIDDEN_FOR_TARGETSYSETM = "4629"; //$NON-NLS-1$

	//4700-4799: Used for Resource Associations validation messages (may change)!
 	public static final String EGLMESSAGE_RA_DUPLICATE_SYSTEM = "4700"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_RA_DUPLICATE_ASSOCIATION = "4701"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_FILE_TYPE_NOT_SUPPORTED = "4702"; //$NON-NLS-1$
//	public static final String EGLMESSAGE_CONFLICTING_FILE_ATTRIBUTES_FOR_LOGICAL_FILE = "4703"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_INCORRECT_FILE_TYPE_FOR_RECORD_ORGANIZATION = "4704"; //$NON-NLS-1$
 	
 	public static final String EGLMESSAGE_INVALID_BLOCKSIZE = "4705"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_INVALID_BLOCKSIZE_OPTION = "4706"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_INVALID_BLOCKSIZE_INT = "4707"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_INVALID_BLOCKSIZE_INT1_GT_INT2 = "4708"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_INVALID_SYSTEMNUMBER = "4709"; //$NON-NLS-1$
 
 	//4800-4900: Used for Linkage Options validation messages (may change)!
 	public static final String EGLMESSAGE_LO_DUPLICATE_CALLLINK = "4800"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_LO_DUPLICATE_FILELINK = "4801"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_LO_INVALID_PACKAGE = "4802"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_LO_INVALID_PROVIDERURL = "4803"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_LO_REDUNDANT_SERVERID = "4804"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_LO_CALLLINK_MUST_BE_REMOTE_OR_EJB = "4805"; //$NON-NLS-1$
	public static final String EGLMESSAGE_LO_INVALID_PARMFORM = "4806"; //$NON-NLS-1$
	public static final String EGLMESSAGE_LO_INVALID_LINKTYPE = "4807"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_LO_DUPLICATE_ASYNCHLINK = "4808"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_LO_INVALID_WRAPPERPACKAGENAME = "4809"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_PARMFORM_INVALID_FOR_PROCEDURE = "4810"; //$NON-NLS-1$
	public static final String EGLMESSAGE_LO_INVALID_REMOTECOMTYPE = "4811"; //$NON-NLS-1$
//
//	//4900-4999: Used for Logic/Program/Function messages
	public static final String EGLMESSAGE_PART_GENERATION_NOT_SUPPORTED = "4900"; //$NON-NLS-1$
//	public static final String EGLMESSAGE_FUNCTION_DECLARATION_HAS_INCORRECT_TYPE = "4901"; //$NON-NLS-1$
//	public static final String EGLMESSAGE_FUNCTION_PARAMETER_HAS_INCORRECT_TYPE = "4902"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_PAGEHANDLERS_NOT_SUPPORTED = "4903"; //$NON-NLS-1$
//	public static final String EGLMESSAGE_PROGRAM_PARAMETER_HAS_INCORRECT_TYPE = "4904"; //$NON-NLS-1$
	public static final String EGLMESSAGE_J2EELEVEL_NOT_VALID = "4905"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_PART_NAME_RESERVED = "4906"; //$NON-NLS-1$
	public static final String EGLMESSAGE_MSGTABLE_NOT_FOUND = "4907"; //$NON-NLS-1$
	public static final String EGLMESSAGE_LIBRARIES_NOT_SUPPORTED = "4908"; //$NON-NLS-1$
	public static final String EGLMESSAGE_TEXTUIPROGRAM_NOT_SUPPORTED = "4909"; //$NON-NLS-1$
	public static final String EGLMESSAGE_HANDLERS_NOT_SUPPORTED = "4910"; //$NON-NLS-1$
	public static final String EGLMESSAGE_PART_NOT_SUPPORTED = "4911"; //$NON-NLS-1$

	public static final String EGLMESSAGE_ACTION_PART_NAME_NOT_VALID = "4918"; //$NON-NLS-1$
	public static final String EGLMESSAGE_ACTION_PART_ALIAS_NOT_VALID = "4919"; //$NON-NLS-1$

	public static final String EGLMESSAGE_WEBTRANS_MSGTABLE_INVALID = "5005"; //$NON-NLS-1$
	public static final String EGLMESSAGE_TUI_MSGTABLE_INVALID = "5006"; //$NON-NLS-1$
	
	
	//5200-5399: Used for EGL Form and Form Group Validation
 	public static final String EGLMESSAGE_VARIABLE_FIELD_ATTRIB_NOT_SUPPORTED = "5250"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_CONSTANT_FIELD_ATTRIB_NOT_SUPPORTED = "5251"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_VALID_VALUES_NOT_SUPPORTED_FOR_NO_NUMERIC = "5253"; //$NON-NLS-1$
 
 	public static final String EGLMESSAGE_FORM_FORMAT_MODULE_OVERFLOW = "5257"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_FORMGROUP_FORMAT_MODULE_OVERFLOW = "5258"; //$NON-NLS-1$
 	
	public static final String EGLMESSAGE_FORM_NAME_IS_DUPLICATE = "5550"; //$NON-NLS-1$
	
	public static final String EGLMESSAGE_BIN18_NOT_SUPPORTED = "5555"; //$NON-NLS-1$
	
	public static final String EGLMESSAGE_INDEX_TOO_SMALL = "5800"; //$NON-NLS-1$
	public static final String EGLMESSAGE_INDEX_TOO_LARGE = "5801"; //$NON-NLS-1$
	public static final String EGLMESSAGE_START_MUST_BE_SMALLER_THAN_END = "5802"; //$NON-NLS-1$
	

	//6000-6020: Used for EGL Web Services Validation
	public static final String EGL_WEB_SERVICES_INVALID_OPERATION_NAME = "6000"; //$NON-NLS-1$
 	public static final String EGL_WEB_SERVICES_INVALID_PROGRAM_PATH = "6001"; //$NON-NLS-1$
 	public static final String EGL_WEB_SERVICES_DUPLICATE_OPERATION_NAME = "6002"; //$NON-NLS-1$
 	public static final String EGL_WEB_SERVICES_NO_OPERATION = "6003"; //$NON-NLS-1$
 	public static final String EGL_WEB_SERVICES_INVALID_WEB_SERVICE_NAME = "6004"; //$NON-NLS-1$
 	public static final String EGL_WEB_SERVICES_NAME_MUST_EQUAL_FILE_NAME = "6005"; //$NON-NLS-1$
 	public static final String EGL_WEB_SERVICES_OPERATION_AND_PROGRAM_PATH_REQUIRED = "6006"; //$NON-NLS-1$
 	public static final String EGL_WEB_SERVICES_OPERATION_NAME_MUST_NOT_EQUAL_FILE_NAME = "6007"; //$NON-NLS-1$
 	public static final String EGL_WEB_SERVICES_NAME_CANNOT_BE_JAVA_KEYWORD = "6008"; //$NON-NLS-1$
 	public static final String EGL_WEB_SERVICES_OPERATION_NAME_CANNOT_BE_JAVA_KEYWORD = "6009"; //$NON-NLS-1$
 	public static final String EGL_WEB_SERVICES_PARAMTER_MISSING_FROM_PROGRAM = "6010"; //$NON-NLS-1$
	public static final String EGL_WEB_SERVICES_PARAMTER_MISSING_FROM_OPERATION = "6011"; //$NON-NLS-1$
	public static final String EGL_WEB_SERVICES_PROGRAM_MUST_BE_CALLED_BATCH = "6012"; //$NON-NLS-1$
	public static final String EGL_WEB_SERVICES_PARAMTER_NAME_MISMATCH = "6013"; //$NON-NLS-1$
	public static final String EGL_WEB_SERVICES_OPERATION_NAME_MISSING_FROM_WSDL = "6014"; //$NON-NLS-1$
	public static final String EGL_WEB_SERVICES_PORT_NAME_MISSING_FROM_WSDL = "6015"; //$NON-NLS-1$
	public static final String EGL_WEB_SERVICES_PARAMETER_TYPE_MISMATCH = "6016"; //$NON-NLS-1$
	public static final String EGL_WEB_SERVICES_WSDL_EGL_RECORD_MISMATCH = "6017"; //$NON-NLS-1$
	
	// 7700 - 7800: Used for ConsoleUI Validation
	public static final String OPENUI_TARGETTYPE = "7700"; //$NON-NLS-1$
 	public static final String OPENUI_MUST_BE_CONSOLEFIELD = "7701"; //$NON-NLS-1$
 	public static final String OPENUI_BIND_NOT_ALLOWED = "7702"; //$NON-NLS-1$
 	public static final String OPENUI_BIND_EXACTLY_ONE = "7703"; //$NON-NLS-1$
 	public static final String OPENUI_BIND_TOO_MANY = "7704"; //$NON-NLS-1$
 	public static final String OPENUI_BIND_MUST_BE_TEXT = "7705"; //$NON-NLS-1$
 	public static final String OPENUI_BIND_READ_ONLY = "7706"; //$NON-NLS-1$
 	public static final String OPENUI_EVENTTYPE_INVALID = "7707"; //$NON-NLS-1$
 	public static final String OPENUI_EVENTARG_NOT_ALLOWED = "7708"; //$NON-NLS-1$
 	public static final String OPENUI_EVENTARG_REQUIRED = "7709"; //$NON-NLS-1$
 	
 	public static final String PARMS_NOT_VALID_FOR_WEBTRANS = "7771"; //$NON-NLS-1$

 	//8000-8149: Used for Generation messages
 	public static final String EGLMESSAGE_BIDI_CONVERSION_TABLE_ERROR = "8000"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_OUTPUT_DIRECTORY_ERROR = "8001"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_WRAPPER_NAME_CONFLICT_ERROR = "8002"; //$NON-NLS-1$
	public static final String EGLMESSAGE_RESOURCE_UPDATE_ERROR = "8003"; //$NON-NLS-1$
	public static final String EGLMESSAGE_FILE_NAME_CONFLICT_ERROR = "8004"; //$NON-NLS-1$
	public static final String EGLMESSAGE_UNSUPPORTED_ELEMENT = "8005"; //$NON-NLS-1$
	public static final String EGLMESSAGE_UNSUPPORTED_DATA_TYPE = "8006"; //$NON-NLS-1$
	public static final String EGLMESSAGE_UNSUPPORTED_SERVICE_BINDING_TYPE = "8007"; //$NON-NLS-1$
	public static final String EGLMESSAGE_UNSUPPORTED_DEPLOYMENT_DESCRIPTOR_TARGET = "8008"; //$NON-NLS-1$

	public static final String EGLMESSAGE_TEMPLATE_DIRECTORY_NOT_SPECIFIED = "8010"; //$NON-NLS-1$
	public static final String EGLMESSAGE_TEMPLATE_DIRECTORY_NOT_FOUND = "8011"; //$NON-NLS-1$

	//8150-    : Used for build plan and prep messages
	public static final String EGLMESSAGE_BUILD_SUCCEEDED = "8150"; //$NON-NLS-1$
	public static final String EGLMESSAGE_BUILDPLAN_CREATED = "8151"; //$NON-NLS-1$

	//8300-: Used for EGL deployment
	public static final String EGL_DEPLOYMENT_FAILED_DOT_LOCATE_EXCEPTION = "8300"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_DEPLOYING_WITH_BUILDDESCRIPTOR = "8302"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_DEPLOYING_RUIHANDLER = "8303"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_COMPLETE = "8304"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_FAILED = "8305"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_EXCEPTION = "8306"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_FAILED_CREATE_NLS_FILE = "8307"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_FAILED_LOCATE_NLS_FILE = "8308"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_FAILED_CREATE_BIND_FILE = "8309"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_FAILED_LOCATE_EGLDD_FILE = "8310"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_FAILED_LOCATE_CHILD_DOT_DEPLOY_FILES = "8311"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_FAILED_CREATE_HTML_FILE = "8312"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_FAILED_CREATE_RT_PROPS_FILE = "8313"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_DEPLOYED_PROPERTY_FILE = "8314"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_FAILED_DEPLOY_PROPERTY_FILE = "8315"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_DEPLOYED_BIND_FILE = "8316"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_FAILED_DEPLOY_BIND_FILE = "8317"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_FAILED_DEPLOY_RT_PROPERTY_FILE = "8318"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_DEPLOYED_RT_PROPERTY_FILE = "8319"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_DEPLOYED_HTML_FILE = "8320"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_FAILED_DEPLOY_HTML_FILE = "8321"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_DEPLOYED_RT_MSG_BUNDLE = "8322"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_FAILED_DEPLOY_RT_MSG_BUNDLE = "8323"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_FAILED_CREATE_RT_MSG_BUNDLE_FOLDER = "8324"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_FAILED_CREATE_PROPERTIES_FOLDER = "8325"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_LOCALE_PROCESSING_FAILED = "8326"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_MISSING_IMPORT_EXCEPTION = "8327"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_FAILED_FIND_NLS_FILES = "8328"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_CREATED_RESOURCE_REFS = "8329"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_FAILED_DEFINE_DATASOURCE = "8330"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_FAILED_WRITE_CONTEXTDOTXML = "8331"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_DEFINED_DATASOURCES = "8332"; //$NON-NLS-1$
	public static final String EGL_DEPLOYMENT_SERVER_NOT_TOMCAT = "8333"; //$NON-NLS-1$
	
	//9000-    : Used for misc validation
	public static final String EGLMESSAGE_GENERATABLE_NOT_FOUND = "9000"; //$NON-NLS-1$
	public static final String EGLMESSAGE_PARTNOTFOUND = "9001"; //$NON-NLS-1$
	public static final String EGLMESSAGE_INVALIDPARTTYPE = "9002"; //$NON-NLS-1$
	public static final String EGLMESSAGE_NOSUCHMEMBER = "9003"; //$NON-NLS-1$

	
	//9980 - : Used for persistence of IRs
	public static final String EGLMESSAGE_CONSTANT_POOL_TOO_BIG = "9980"; //$NON-NLS-1$
	public static final String EGLMESSAGE_NOT_AN_IR = "9981"; //$NON-NLS-1$
	public static final String EGLMESSAGE_INCOMPATIBLE_VERSION = "9982"; //$NON-NLS-1$
	public static final String EGLMESSAGE_UNKNOWN_OBJECT = "9983"; //$NON-NLS-1$
	public static final String EGLMESSAGE_UNKNOWN_OBJECT_IN_POOL = "9984"; //$NON-NLS-1$
	public static final String EGLMESSAGE_CONSTANT_POOL_INDEX_OUT_OF_RANGE = "9985"; //$NON-NLS-1$
	

 	//9988-    : Used for Validation complete
 	public static final String EGLMESSAGE_SYSTEM_PART_NOT_GENABLE = "9987"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_GENERATION_CANCELED = "9988"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_EMPTY_MESSAGE = "9989"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_VALIDATION_COMPLETE = "9990"; //$NON-NLS-1$

	//9991-9997: Used for Generation failed/complete messages
	public static final String EGLMESSAGE_COMPILE_ERRORS = "9991"; //$NON-NLS-1$
	public static final String EGLMESSAGE_COMPILE_ERRORS_IN_SUBPART = "9992"; //$NON-NLS-1$
	public static final String EGLMESSAGE_GENERATION_ALREADY_OCCURRED = "9993"; //$NON-NLS-1$
	public static final String EGLMESSAGE_GENERATION_PROGRAM_RESULTS_VIEW_MESSAGE = "9994"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_BUILD_ERROR = "9995"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_GENERATION_COMPLETE = "9996"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_GENERATION_FAILED = "9997"; //$NON-NLS-1$

	//9998-9999: Used for unexpected exception
	public static final String EGLMESSAGE_EXCEPTION_MESSAGE = "9998"; //$NON-NLS-1$
 	public static final String EGLMESSAGE_EXCEPTION_STACKTRACE = "9999"; //$NON-NLS-1$

	/**
	 * EGLMessageconstructor comment.
	 * @param aBundleName java.lang.String
	 * @param aSeverity int (expecting EGL_ERROR_MESSAGE, EGL_INFORMATIONAL_MESSAGE, EGL_WARNING_MESSAGE)
	 * @param anId java.lang.String (error message number == key of message in resource bundle)
	 * @param groupName java.lang.String (see EGLMESSAGE_GROUP_CORE for an example)
	 */
	public EGLMessage(
		String aBundleName,
		int aSeverity,
		String anId,
		String groupName,
		Object messageContributor,
		java.lang.String[] aParams,
		int aStartOffset,
		int anEndOffset) {

		//  read the resource bundle and pass along to the real EGLMessage constructor

		this(
			ResourceBundle.getBundle(aBundleName, Locale.getDefault()),
			aSeverity,
			anId,
			groupName,
			messageContributor,
			aParams,
			aStartOffset,
			anEndOffset);

	}

	/**
	 * EGLMessageconstructor comment.
	 * @param aBundleName java.lang.String
	 * @param aSeverity int (expecting EGL_ERROR_MESSAGE, EGL_INFORMATIONAL_MESSAGE, EGL_WARNING_MESSAGE)
	 * @param anId java.lang.String (error message number == key of message in resource bundle)
	 * @param groupName java.lang.String (see EGLMESSAGE_GROUP_CORE for an example)
	 */
	public EGLMessage(
		String aBundleName,
		int aSeverity,
		String anId,
		String groupName,
		Object messageContributor,
		java.lang.String[] aParams,
		int aStartLine,
		int aStartColumn,
		int anEndLine,
		int anEndColumn) {

		//  read the resource bundle and pass along to the real EGLMessage constructor

		this(
			ResourceBundle.getBundle(aBundleName, Locale.getDefault()),
			aSeverity,
			anId,
			groupName,
			messageContributor,
			aParams,
			aStartLine,
			aStartColumn,
			anEndLine,
			anEndColumn);

	}
	
	public EGLMessage() {
		super();
	}
	/**
	 * EGLMessage constructor comment.
	 * @param bundle java.util.ResourceBundle
	 * @param aSeverity int (expecting EGL_ERROR_MESSAGE, EGL_INFORMATIONAL_MESSAGE, EGL_WARNING_MESSAGE)
	 * @param anId java.lang.String (error message number == key of message in resource bundle)
	 * @param groupName java.lang.String (see EGLMESSAGE_GROUP_CORE for an example)
	 */
	public EGLMessage(ResourceBundle bundle, int aSeverity, String anId, String groupName, java.lang.String[] aParams) {

		severity = aSeverity;
		id = anId;
		params = aParams;
		setGroupName(groupName);
		messageBundle = bundle;

		builtMessage = buildMessageText(anId, aParams);
	}
	/**
	 * EGLMessage constructor comment.
	 * @param aBundle       The resource bundle that contains the message text.
	 * @param aSeverity     expecting EGL_ERROR_MESSAGE, EGL_INFORMATIONAL_MESSAGE, EGL_WARNING_MESSAGE
	 * @param anId          error message number == key of message in resource bundle
	 * @param groupName     see EGLMESSAGE_GROUP_CORE for an example
	 * @param messageContributor The holder of the part in error.
	 * @param aParams       The inserts for the message
	 * @param aStartOffset  The offset where the error starts.
	 * @param anEndOffset   The offset where the error ends.
	 */
	public EGLMessage(
		ResourceBundle aBundle,
		int aSeverity,
		String anId,
		String groupName,
		Object aMessageContributor,
		java.lang.String[] aParams,
		int aStartOffset,
		int anEndOffset) {
						
		messageBundle = aBundle;
		severity = aSeverity;
		id = anId;
		params = aParams;
		setGroupName(groupName);
		setStartOffset(aStartOffset);
		setEndOffset(anEndOffset);
		//	messageBundle = ResourceBundle.getBundle(aBundleName, Locale.getDefault()); //$NON-NLS-1$

		// Now read the message text from the resource bundle
		// and format with the inserts 	

		builtMessage = buildMessageText(anId, aParams);

		if (aMessageContributor != null) {
			if (aMessageContributor instanceof IEGLMessageContributor) {
				IEGLMessageContributor mc = (IEGLMessageContributor) aMessageContributor;
				setMessageContributor(mc);
				if (aStartOffset == -1) {
					//no startline given, see if we can find it from the IMessageContributor
					if (mc.getStart() != null) {
						setStartLine(mc.getStart().getLine());
						setStartColumn(mc.getStart().getColumn());
						setStartOffset(mc.getStart().getOffset());
					}
				}
				if (anEndOffset == -1) {
					// no endline given, see if we can find it from the IMessageContributor 
					if (mc.getEnd() != null) {
						setEndLine(mc.getEnd().getLine());
						setEndColumn(mc.getEnd().getColumn());
						setEndOffset(mc.getEnd().getOffset() + 1);
					}
				}
			}
		}

	}
	/**
	 * EGLMessage constructor comment.
	 * @param aBundle       The resource bundle that contains the message text.
	 * @param aSeverity     expecting EGL_ERROR_MESSAGE, EGL_INFORMATIONAL_MESSAGE, EGL_WARNING_MESSAGE
	 * @param anId          error message number == key of message in resource bundle
	 * @param groupName     see EGLMESSAGE_GROUP_CORE for an example
	 * @param messageContributor The holder of the part in error.
	 * @param aParams       The inserts for the message
	 * @param aStartLine    The line where the error starts.
	 * @param aStartColum   The column where the error starts.
	 * @param anEndLine     The line where the error ends.
	 * @param anEndColum    The column where the error ends.
	 */
	public EGLMessage(
		ResourceBundle aBundle,
		int aSeverity,
		String anId,
		String groupName,
		Object aMessageContributor,
		java.lang.String[] aParams,
		int aStartLine,
		int aStartColumn,
		int anEndLine,
		int anEndColumn) {

		int aStartOffset = 0;
		int anEndOffset = 0;
						
		messageBundle = aBundle;
		severity = aSeverity;
		id = anId;
		params = aParams;
		setGroupName(groupName);

		//	messageBundle = ResourceBundle.getBundle(aBundleName, Locale.getDefault()); //$NON-NLS-1$

		// Now read the message text from the resource bundle
		// and format with the inserts 	

		builtMessage = buildMessageText(anId, aParams);

		if (aMessageContributor != null) {
			if (aMessageContributor instanceof IEGLMessageContributor) {
				IEGLMessageContributor mc = (IEGLMessageContributor) aMessageContributor;
				setMessageContributor(mc);
				if (aStartLine == -1) {
					//no startline given, see if we can find it from the IMessageContributor
					if (mc.getStart() != null) {
						aStartLine = mc.getStart().getLine();
						aStartColumn = mc.getStart().getColumn();
						aStartOffset = mc.getStart().getOffset();
					}
				}
				if (anEndLine == -1) {
					// no endline given, see if we can find it from the IMessageContributor
					if (mc.getEnd() != null) {
						anEndLine = mc.getEnd().getLine();
						anEndColumn = mc.getEnd().getColumn();
						anEndOffset = mc.getEnd().getOffset() + 1;
					}
				}
			}
		}

		setStartLine(aStartLine);
		setStartColumn(aStartColumn);
		setEndLine(anEndLine);
		setEndColumn(anEndColumn);
		setStartOffset(aStartOffset);
		setEndOffset(anEndOffset);
	}
	/**
	 * EGLMessage constructor comment.
	 * @param aBundle       The resource bundle that contains the message text.
	 * @param aSeverity     expecting EGL_ERROR_MESSAGE, EGL_INFORMATIONAL_MESSAGE, EGL_WARNING_MESSAGE
	 * @param anId          error message number == key of message in resource bundle
	 * @param groupName     see EGLMESSAGE_GROUP_CORE for an example
	 * @param messageContributor The holder of the part in error.
	 * @param aParams       The inserts for the message
	 * @param aStartLine    The line where the error starts.
	 * @param aStartColum   The column where the error starts.
	 * @param anEndLine     The line where the error ends.
	 * @param anEndColum    The column where the error ends.
	 */
	public EGLMessage(
		String aBundle,
		int aSeverity,
		String anId,
		String groupName,
		Object aMessageContributor,
		java.lang.String[] aParams,
		int aStartLine,
		int aStartColumn,
		int aStartOffset,
		int anEndLine,
		int anEndColumn,
		int anEndOffset) {

		messageBundle = ResourceBundle.getBundle(aBundle, Locale.getDefault());
		severity = aSeverity;
		id = anId;
		params = aParams;
		setGroupName(groupName);

		// Now read the message text from the resource bundle
		// and format with the inserts 	

		builtMessage = buildMessageText(anId, aParams);

		if (aMessageContributor != null) {
			if (aMessageContributor instanceof IEGLMessageContributor) {
				IEGLMessageContributor mc = (IEGLMessageContributor) aMessageContributor;
				setMessageContributor(mc);
				if (aStartLine == -1) {
					//no startline given, see if we can find it from the IMessageContributor
					if (mc.getStart() != null) {
						aStartLine = mc.getStart().getLine();
						aStartColumn = mc.getStart().getColumn();
						aStartOffset = mc.getStart().getOffset();
					}
				}
				if (anEndLine == -1) {
					// no endline given, see if we can find it from the IMessageContributor
					if (mc.getEnd() != null) {
						anEndLine = mc.getEnd().getLine();
						anEndColumn = mc.getEnd().getColumn();
						anEndOffset = mc.getEnd().getOffset() + 1;
					}
				}
			}
		}

		setStartLine(aStartLine);
		setStartColumn(aStartColumn);
		setEndLine(anEndLine);
		setEndColumn(anEndColumn);
		setStartOffset(aStartOffset);
		setEndOffset(anEndOffset);
	}
	/**
	 * Returns the localized version of the message that corresponds to the key.
	 *
	 * @param key      the message key, one of the constants defined in this class.
	 * @param inserts  the message inserts.
	 * @return the localized version of the message that corresponds to the key.
	 */
	public static String buildMessageText(String key, Object[] inserts) {
		try {
			String message = messageBundle.getString(key);
			if (message == null || inserts == null || inserts.length == 0) {
				return message;
			}

			formatter.applyPattern(message);
			return formatter.format(insertsWithoutNulls(inserts));
		} catch (MissingResourceException mrx) {
			return key;
		}
	}

	public static Object[] insertsWithoutNulls(Object[] originalInserts) {

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
	/**
	 *
	 */
	public static EGLMessage createEGLEditorErrorMessage(ResourceBundle resource, String messageID) {

		return new EGLMessage(resource, EGLMessage.EGL_ERROR_MESSAGE, messageID, EGLMessage.EGLMESSAGE_GROUP_EDITOR, new String[] { null });
	}
	/**
	 *
	 */
	public static EGLMessage createEGLEditorErrorMessage(ResourceBundle resource, String messageID, String[] inserts) {

		return new EGLMessage(resource, EGLMessage.EGL_ERROR_MESSAGE, messageID, EGLMessage.EGLMESSAGE_GROUP_EDITOR, inserts);
	}
	/**
	 *
	 */
	public static EGLMessage createEGLEditorErrorMessage(ResourceBundle resource, String messageID, String insert) {

		return new EGLMessage(
			resource,
			EGLMessage.EGL_ERROR_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_EDITOR,
			new String[] { insert });
	}
	/**
	 *
	 */
	public static EGLMessage createEGLEditorInformationalMessage(ResourceBundle resource, String messageID) {

		return new EGLMessage(
			resource,
			EGLMessage.EGL_INFORMATIONAL_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_EDITOR,
			new String[] { null });
	}
	/**
	 *
	 */
	public static EGLMessage createEGLEditorInformationalMessage(ResourceBundle resource, String messageID, String insert) {

		return new EGLMessage(
			resource,
			EGLMessage.EGL_INFORMATIONAL_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_EDITOR,
			new String[] { insert });
	}
	/**
	 *
	 */
	public static EGLMessage createEGLEditorInformationalMessage(ResourceBundle resource, String messageID, String[] inserts) {

		return new EGLMessage(resource, EGLMessage.EGL_INFORMATIONAL_MESSAGE, messageID, EGLMessage.EGLMESSAGE_GROUP_EDITOR, inserts);
	}
	/**
	 *
	 */
	public static EGLMessage createEGLEditorWarningMessage(ResourceBundle resource, String messageID) {

		return new EGLMessage(
			resource,
			EGLMessage.EGL_WARNING_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_EDITOR,
			new String[] { null });
	}
	/**
	 *
	 */
	public static EGLMessage createEGLEditorWarningMessage(ResourceBundle resource, String messageID, String[] inserts) {

		return new EGLMessage(resource, EGLMessage.EGL_WARNING_MESSAGE, messageID, EGLMessage.EGLMESSAGE_GROUP_EDITOR, inserts);
	}
	/**
	 *
	 */
	public static EGLMessage createEGLInputErrorMessage(String messageID, String[] inserts) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_ERROR_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_INPUT,
			null,
			inserts,
			-1,
			-1,
			-1,
			-1);

	}
	/**
	 *
	 */
	public static EGLMessage createEGLInputErrorMessage(String messageID, String insert) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_ERROR_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_INPUT,
			null,
			new String[] { insert },
			-1,
			-1,
			-1,
			-1);
	}

	/**
	 *
	 */
	public static EGLMessage createEGLDebuggerErrorMessage(ResourceBundle resource, String messageID) {

		return new EGLMessage(
			resource,
			EGLMessage.EGL_ERROR_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_DEBUGGER,
			new String[] { null });
	}
	/**
	 *
	 */
	public static EGLMessage createEGLDebuggerErrorMessage(ResourceBundle resource, String messageID, String[] inserts) {

		return new EGLMessage(resource, EGLMessage.EGL_ERROR_MESSAGE, messageID, EGLMessage.EGLMESSAGE_GROUP_DEBUGGER, inserts);
	}
	/**
	 *
	 */
	public static EGLMessage createEGLDebuggerErrorMessage(ResourceBundle resource, String messageID, String insert) {

		return new EGLMessage(
			resource,
			EGLMessage.EGL_ERROR_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_DEBUGGER,
			new String[] { insert });
	}
	/**
	 *
	 */
	public static EGLMessage createEGLDebuggerWarningMessage(ResourceBundle resource, String messageID) {

		return new EGLMessage(
			resource,
			EGLMessage.EGL_WARNING_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_DEBUGGER,
			new String[] { null });
	}
	/**
	 *
	 */
	public static EGLMessage createEGLDebuggerWarningMessage(ResourceBundle resource, String messageID, String[] inserts) {

		return new EGLMessage(resource, EGLMessage.EGL_WARNING_MESSAGE, messageID, EGLMessage.EGLMESSAGE_GROUP_DEBUGGER, inserts);
	}
	/**
	 *
	 */
	public static EGLMessage createEGLDebuggerWarningMessage(ResourceBundle resource, String messageID, String insert) {

		return new EGLMessage(
			resource,
			EGLMessage.EGL_WARNING_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_DEBUGGER,
			new String[] { insert });
	}
	/**
	 *
	 */
	public static EGLMessage createEGLValidationErrorMessage(String messageID, Object messageContributor) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_ERROR_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			new String[] { null },
			-1,
			-1,
			-1,
			-1);
	}
	/**
	 *
	 */
	public static EGLMessage createEGLValidationErrorMessage(String messageID, Object messageContributor, String[] inserts) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_ERROR_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			inserts,
			-1,
			-1,
			-1,
			-1);
	}
	public static EGLMessage createEGLDeploymentErrorMessage(String messageID, Object messageContributor, String[] inserts) {

		return new EGLMessage(
				getValidationResourceBundleName(),
				EGLMessage.EGL_ERROR_MESSAGE,
				messageID,
				EGLMessage.EGLMESSAGE_GROUP_DEPLOYMENT,
				messageContributor,
				inserts,
				-1,
				-1,
				-1,
				-1);
	}
	public static EGLMessage createEGLDeploymentInformationalMessage(String messageID, Object messageContributor, String[] inserts) {

		return new EGLMessage(
				getValidationResourceBundleName(),
				EGLMessage.EGL_INFORMATIONAL_MESSAGE,
				messageID,
				EGLMessage.EGLMESSAGE_GROUP_DEPLOYMENT,
				messageContributor,
				inserts,
				-1,
				-1,
				-1,
				-1);
	}
	/**
	 *
	 */
	public static EGLMessage createEGLValidationErrorMessage(
		String messageID,
		Object messageContributor,
		String[] inserts,
		int aStartLine,
		int aStartColumn) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_ERROR_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			inserts,
			aStartLine,
			aStartColumn,
			-1,
			-1);
	}
	/**
	 *
	 */
	public static EGLMessage createEGLValidationErrorMessage(
		String messageID,
		Object messageContributor,
		String[] inserts,
		int aStartLine,
		int aStartColumn,
		int anEndLine,
		int anEndColumn) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_ERROR_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			inserts,
			aStartLine,
			aStartColumn,
			anEndLine,
			anEndColumn);
	}
	/**
	 *
	 */
	public static EGLMessage createEGLValidationPartErrorMessage(
		ResourceBundle resource,
		String messageID,
		Object messageContributor,
		String[] inserts,
		int aStartLine,
		int aStartColumn,
		int anEndLine,
		int anEndColumn) {

		return new EGLMessage(
			resource,
			EGLMessage.EGL_ERROR_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			inserts,
			aStartLine,
			aStartColumn,
			anEndLine,
			anEndColumn);
	}	

	/**
	 *
	 */
	public static EGLMessage createEGLValidationErrorMessage(
		String messageID,
		Object messageContributor,
		String[] inserts,
		IEGLStatementNode node) {

		EGLMessage message =
			new EGLMessage(
				getValidationResourceBundleName(),
				EGLMessage.EGL_ERROR_MESSAGE,
				messageID,
				EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
				messageContributor,
				inserts,
				node.getStartLine(),
				node.getStartColumn(),
				node.getEndLine(),
				node.getEndColumn());
		message.setStartOffset(node.getStartOffset());
		message.setEndOffset(node.getEndOffset() + 1);
		return message;
	}
	/**
	 *
	 */
	public static EGLMessage createEGLValidationErrorMessage(String messageID, Object messageContributor, String insert) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_ERROR_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			new String[] { insert },
			-1,
			-1,
			-1,
			-1);
	}
	/**
	 *
	 */
	public static EGLMessage createEGLValidationErrorMessage(
		String messageID,
		Object messageContributor,
		String insert,
		int aStartOffset,
		int anEndOffset) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_ERROR_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			new String[] { insert },
			aStartOffset,
			anEndOffset);
		}
	/**
	 *
	 */
	public static EGLMessage createEGLValidationErrorMessage(
		String bundleName,
		String messageID,
		String[] inserts,
		int aStartOffset,
		int anEndOffset) {
	
		Object messageContributor = null;

		return new EGLMessage(
			bundleName,
			EGLMessage.EGL_ERROR_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			inserts,
			aStartOffset,
			anEndOffset);
		}		
	/**
	 *
	 */
	public static EGLMessage createEGLValidationErrorMessage(
		String messageID,
		Object messageContributor,
		String insert,
		int aStartLine,
		int aStartColumn,
		int anEndLine,
		int anEndColumn) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_ERROR_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			new String[] { insert },
			aStartLine,
			aStartColumn,
			anEndLine,
			anEndColumn);
	}
	/**
	 *
	 */
	public static EGLMessage createEGLValidationInformationalMessage(String messageID, Object messageContributor) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_INFORMATIONAL_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			new String[] { null },
			1,
			1,
			-1,
			-1);
	}
	/**
	 *
	 */
	public static EGLMessage createEGLValidationInformationalMessage(String messageID, Object messageContributor, String[] inserts) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_INFORMATIONAL_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			inserts,
			1,
			1,
			-1,
			-1);
	}
	/**
	 *
	 */
	public static EGLMessage createEGLValidationInformationalMessage(String messageID, Object messageContributor, String insert) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_INFORMATIONAL_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			new String[] { insert },
			1,
			1,
			-1,
			-1);
	}
	
	public static EGLMessage createEGLValidationWarningMessage(
		String bundleName,
		String messageID,
		String[] inserts,
		int aStartOffset,
		int anEndOffset) {
	
		Object messageContributor = null;

		return new EGLMessage(
			bundleName,
			EGLMessage.EGL_WARNING_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			inserts,
			aStartOffset,
			anEndOffset);
		}		
	
	/**
	 *
	 */
	public static EGLMessage createEGLValidationWarningMessage(String messageID, Object messageContributor) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_WARNING_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			new String[] { null },
			-1,
			-1,
			-1,
			-1);
	}
	/**
	 *
	 */
	public static EGLMessage createEGLValidationWarningMessage(String messageID, Object messageContributor, String[] inserts) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_WARNING_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			inserts,
			-1,
			-1,
			-1,
			-1);
	}
	/**
	 *
	 */
	public static EGLMessage createEGLValidationWarningMessage(String messageID, Object messageContributor, String insert) {

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_WARNING_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_VALIDATION,
			messageContributor,
			new String[] { insert },
			-1,
			-1,
			-1,
			-1);
	}
	
	public static EGLMessage createEGLSytaxErrorMessage(
		String bundleName,
		String messageID,
		String[] inserts,
		int aStartOffset,
		int anEndOffset) {
	
		Object messageContributor = null;

		return new EGLMessage(
			bundleName,
			EGLMessage.EGL_ERROR_MESSAGE,
			messageID,
			EGLMessage.EGLMESSAGE_GROUP_SYNTAX,
			messageContributor,
			inserts,
			aStartOffset,
			anEndOffset);
	}
	
	public static EGLMessage createEGLSyntaxWarningMessage(
			String bundleName,
			String messageID,
			String[] inserts,
			int aStartOffset,
			int anEndOffset) {
		
			Object messageContributor = null;

			return new EGLMessage(
				bundleName,
				EGLMessage.EGL_WARNING_MESSAGE,
				messageID,
				EGLMessage.EGLMESSAGE_GROUP_SYNTAX,
				messageContributor,
				inserts,
				aStartOffset,
				anEndOffset);
	}
	///**
	// *
	// */
	//public static EGLMessage createEGLWebServicesErrorMessage(ResourceBundle resource, String messageID) {
	//
	//	return new EGLMessage(
	//			resource,
	//			EGLMessage.EGL_ERROR_MESSAGE,
	//			messageID,
	//			EGLMessage.EGLMESSAGE_GROUP_WEB_SERVICES,
	//			new String[] {null});
	//}
	///**
	// *
	// */
	//public static EGLMessage createEGLWebServicesErrorMessage(ResourceBundle resource, String messageID, String[] inserts) {
	//
	//	return new EGLMessage(
	//			resource,
	//			EGLMessage.EGL_ERROR_MESSAGE,
	//			messageID,
	//			EGLMessage.EGLMESSAGE_GROUP_WEB_SERVICES,
	//			inserts);
	//}
	///**
	// *
	// */
	//public static EGLMessage createEGLWebServicesErrorMessage(ResourceBundle resource, String messageID, String insert) {
	//
	//	return new EGLMessage(
	//			resource,
	//			EGLMessage.EGL_ERROR_MESSAGE,
	//			messageID,
	//			EGLMessage.EGLMESSAGE_GROUP_WEB_SERVICES,
	//			new String[] {insert});
	//}
	///**
	// *
	// */
	//public static EGLMessage createEGLWebServicesInformationalMessage(ResourceBundle resource, String messageID) {
	//
	//	return new EGLMessage(
	//			resource,
	//			EGLMessage.EGL_INFORMATIONAL_MESSAGE,
	//			messageID,
	//			EGLMessage.EGLMESSAGE_GROUP_WEB_SERVICES,
	//			new String[] {null});
	//}
	///**
	// *
	// */
	//public static EGLMessage createEGLWebServicesInformationalMessage(ResourceBundle resource, String messageID, String insert) {
	//
	//	return new EGLMessage(
	//			resource,
	//			EGLMessage.EGL_INFORMATIONAL_MESSAGE,
	//			messageID,
	//			EGLMessage.EGLMESSAGE_GROUP_WEB_SERVICES,
	//			new String[] {insert});
	//}
	///**
	// *
	// */
	//public static EGLMessage createEGLWebServicesInformationalMessage(ResourceBundle resource, String messageID, String[] inserts) {
	//
	//	return new EGLMessage(
	//			resource,
	//			EGLMessage.EGL_INFORMATIONAL_MESSAGE,
	//			messageID,
	//			EGLMessage.EGLMESSAGE_GROUP_WEB_SERVICES,
	//			inserts);
	//}
	///**
	// *
	// */
	//public static EGLMessage createEGLWebServicesWarningMessage(ResourceBundle resource, String messageID) {
	//
	//	return new EGLMessage(
	//			resource,
	//			EGLMessage.EGL_WARNING_MESSAGE,
	//			messageID,
	//			EGLMessage.EGLMESSAGE_GROUP_WEB_SERVICES,
	//			new String[] {null});
	//}
	///**
	// *
	// */
	//public static EGLMessage createEGLWebServicesWarningMessage(ResourceBundle resource, String messageID, String[] inserts) {
	//
	//	return new EGLMessage(
	//			resource,
	//			EGLMessage.EGL_WARNING_MESSAGE,
	//			messageID,
	//			EGLMessage.EGLMESSAGE_GROUP_WEB_SERVICES,
	//			inserts);
	//}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/9/2001 2:18:31 PM)
	 * @return org.eclipse.edt.compiler.internal.compiler.utils.EGLMessage
	 * @param String messageString
	 * @param String fileName
	 * @param lineNumber int
	 * @param columnNumber int
	 * @param messageContributor object
	 */
	public static EGLMessage createEGLXMLValidationErrorMessage(
		String messageString,
		String fileName,
		int lineNumber,
		int columnNumber,
		Object messageContributor) {

		String messageNumber;
		String[] messageInserts;

		if (fileName == null) {
			messageNumber = EGLMESSAGE_XML_VALIDATION_ERROR;
			messageInserts = new String[] { messageString };
		} else {
			messageNumber = EGLMESSAGE_XML_VALIDATION_ERROR_IN_FILE;
			messageInserts = new String[] { messageString, fileName };
		}

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_ERROR_MESSAGE,
			messageNumber,
			EGLMessage.EGLMESSAGE_GROUP_XML_VALIDATION,
			messageContributor,
			messageInserts,
			lineNumber,
			columnNumber,
			lineNumber,
			columnNumber);

	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/9/2001 2:18:31 PM)
	 * @return org.eclipse.edt.compiler.internal.compiler.utils.EGLMessage
	 * @param String messageString
	 * @param String fileName
	 * @param lineNumber int
	 * @param columnNumber int
	 * @param messageContributor object
	 */
	public static EGLMessage createEGLXMLValidationWarningMessage(
		String messageString,
		String fileName,
		int lineNumber,
		int columnNumber,
		Object messageContributor) {

		String messageNumber;
		String[] messageInserts;

		if (fileName == null) {
			messageNumber = EGLMESSAGE_XML_VALIDATION_ERROR;
			messageInserts = new String[] { messageString };
		} else {
			messageNumber = EGLMESSAGE_XML_VALIDATION_ERROR_IN_FILE;
			messageInserts = new String[] { messageString, fileName };
		}

		return new EGLMessage(
			getValidationResourceBundleName(),
			EGLMessage.EGL_WARNING_MESSAGE,
			messageNumber,
			EGLMessage.EGLMESSAGE_GROUP_XML_VALIDATION,
			messageContributor,
			messageInserts,
			lineNumber,
			columnNumber,
			lineNumber,
			columnNumber);

	}
	/**
	 * Returns a text representation of this message formatted in the default Locale,
	 * with inserts already applied.
	 */
	public String getBuiltMessage() {

		String flag = " "; //$NON-NLS-1$
		switch (getSeverity()) {
			case EGL_ERROR_MESSAGE :
				flag = "e"; //$NON-NLS-1$
				break;
			case EGL_WARNING_MESSAGE :
				flag = "w"; //$NON-NLS-1$
				break;
			case EGL_INFORMATIONAL_MESSAGE :
				flag = "i"; //$NON-NLS-1$
		}

		return "IWN." + getMessagePrefix() + "." + id + "." + flag + " " + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		startLineNumber + "/" + startColumnNumber + " " + builtMessage; //$NON-NLS-1$ //$NON-NLS-2$
	}
	/**
	 * Returns a text representation of this message formatted in the default Locale,
	 * with inserts already applied, and no lone or column information.
	 */
	public String getBuiltMessageWithoutLineAndColumn() {

		String flag = " "; //$NON-NLS-1$
		switch (getSeverity()) {
			case EGL_ERROR_MESSAGE :
				flag = "e"; //$NON-NLS-1$
				break;
			case EGL_WARNING_MESSAGE :
				flag = "w"; //$NON-NLS-1$
				break;
			case EGL_INFORMATIONAL_MESSAGE :
				flag = "i"; //$NON-NLS-1$
		}

		return "IWN." + getMessagePrefix() + "." + id + "." + flag + " " + builtMessage; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
	/**
	 * Returns a text representation of this message formatted in the default Locale,
	 * with inserts already applied.
	 */
	public String getBuiltMessageWithLineAndColumn() {

		String flag = " "; //$NON-NLS-1$
		switch (getSeverity()) {
			case EGL_ERROR_MESSAGE :
				flag = "e"; //$NON-NLS-1$
				break;
			case EGL_WARNING_MESSAGE :
				flag = "w"; //$NON-NLS-1$
				break;
			case EGL_INFORMATIONAL_MESSAGE :
				flag = "i"; //$NON-NLS-1$
		}

		if (messageContributor instanceof IEGLMessageContributor && ((IEGLMessageContributor) messageContributor).getResourceName() != null) { // have project/file information
			return "IWN." + getMessagePrefix() + "." + id + "." + flag //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			+" - " + ((IEGLMessageContributor) messageContributor).getResourceName() //$NON-NLS-1$
			+" - " + startLineNumber + "/" + startColumnNumber //$NON-NLS-1$ //$NON-NLS-2$
			+" - " + builtMessage; //$NON-NLS-1$
		} else {
			return "IWN." + getMessagePrefix() + "." + id + "." + flag //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			+" - " + startLineNumber + "/" + startColumnNumber //$NON-NLS-1$ //$NON-NLS-2$
			+" - " + builtMessage; //$NON-NLS-1$

		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/9/2001 11:33:50 AM)
	 * @return int
	 */
	public int getEndColumn() {
		return endColumnNumber;
	}
	/**
	 * If there is a target object associated with this message, it will have a
	 * line number associated with it. If the line number was not set, "0" will
	 * be returned.
	 */
	public int getEndLine() {
		return endLineNumber;
	}
	/**
	 * Returns the id of the message.  Message ids are used as the constants in property bundles which
	 * localize the description of the message in a locale-independent fashion.
	 * The id may not be null or the empty string.
	 * @return java.lang.String
	 */
	public String getId() {
		return id;
	}
	/**
	 * Returns whether the message is a syntax message or not
	 */
	public boolean isSyntaxMessage() {

		if (groupName.equals(EGLMessage.EGLMESSAGE_GROUP_SYNTAX )) {
			return true;
		} else
			return false;	
	}	
	/**
	 * Returns a message prefix for the groupName 
	 */
	public String getMessagePrefix() {

		if (groupName.equals(EGLMessage.EGLMESSAGE_GROUP_VALIDATION)) {
			return ("VAL"); //$NON-NLS-1$
		} else if (groupName.equals(EGLMessage.EGLMESSAGE_GROUP_STATEMENT_PARSER)) {
			return ("EGL"); //$NON-NLS-1$
		} else if (groupName.equals(EGLMessage.EGLMESSAGE_GROUP_XML_VALIDATION)) {
			return ("XML"); //$NON-NLS-1$
		} else if (groupName.equals(EGLMessage.EGLMESSAGE_GROUP_EDITOR)) {
			return ("EDT"); //$NON-NLS-1$
		} else if (groupName.equals(EGLMessage.EGLMESSAGE_GROUP_INPUT)) {
			return ("INP"); //$NON-NLS-1$
		} else if (groupName.equals(EGLMessage.EGLMESSAGE_GROUP_DEBUGGER)) {
			return ("DBG"); //$NON-NLS-1$
		} else if (groupName.equals(EGLMessage.EGLMESSAGE_VAG_MIGRATION)) {
			return ("MIG"); //$NON-NLS-1$
		} else if( groupName.equals(EGLMessage.EGLMESSAGE_GROUP_SYNTAX )) {
			return ("SYN"); //$NON-NLS-1$
		} else if( groupName.equals(EGLMessage.EGLMESSAGE_GROUP_DEPLOYMENT )) {
			return ("DEP"); //$NON-NLS-1$
		} else
			return ("XXX"); //$NON-NLS-1$	
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/3/2001 4:46:02 PM)
	 * @return java.lang.Object
	 */
	public java.lang.Object getMessageContributor() {
		return messageContributor;
	}
	public static ResourceBundle getResourceBundle() {

		return ResourceBundle.getBundle(getResourceBundleName());
	}
	/**
	 *
	 */
	public static String getResourceBundleName() {

		return org.eclipse.edt.compiler.internal.IEGLBaseConstants.EGL_VALIDATION_RESOURCE_BUNDLE_NAME;
	}
	// ---------- resource bundle -------------

	/**
	 * We don't want to crash because of a missing String.
	 * Returns the key if not found.
	 */
	public static String getResourceString(String key) {

		try {
			return getResourceBundle().getString(key);
		} catch (MissingResourceException x) {
			return key;
		} catch (NullPointerException x) {
			return "!" + key + "!"; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	/**
	 * Returns the severity level of the message.  One of SeverityEnum.XXX constants.
	 * @return int
	 */
	public int getSeverity() {
		return severity;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/9/2001 11:33:50 AM)
	 * @return int
	 */
	public int getStartColumn() {
		return startColumnNumber;
	}
	/**
	 * If there is a target object associated with this message, it will have a
	 * starting offset associated with it. If the offset was not set, "0" will
	 * be returned.
	 */
	public int getStartOffset() {
		return startOffset;
	}
	/**
	 * If there is a target object associated with this message, it will have a
	 * ending offset associated with it. If the offset was not set, "0" will
	 * be returned.
	 */
	public int getEndOffset() {
		return endOffset;
	}
	/**
	 * If there is a target object associated with this message, it will have a
	 * line number associated with it. If the line number was not set, "0" will
	 * be returned.
	 */
	public int getStartLine() {
		return startLineNumber;
	}
	/**
	 *
	 */
	public static String getValidationResourceBundleName() {

		return org.eclipse.edt.compiler.internal.IEGLBaseConstants.EGL_VALIDATION_RESOURCE_BUNDLE_NAME;
	}

	//public static String getWebServicesResourceBundleName() {
	//
	//	return "org.eclipse.edt.compiler.internal.webservices.parteditor.EGLWebServicesPartEditorResources";
	//}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/14/2001 10:33:09 PM)
	 * @return boolean
	 */
	public boolean isError() {
		return getSeverity() == EGLMessage.EGL_ERROR_MESSAGE;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/5/2001 11:33:50 AM)
	 * @return int
	 */
	public boolean isInformational() {
		if ((getSeverity()) == EGL_INFORMATIONAL_MESSAGE) {
			return true;
		} else
			return false;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/5/2001 11:33:50 AM)
	 * @return int
	 */
	public boolean isWarning() {
		if ((getSeverity()) == EGL_WARNING_MESSAGE) {
			return true;
		} else
			return false;
	}
	/**
	 * Returns the raw message text
	 */
	public String primGetBuiltMessage() {

		return builtMessage;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/9/2001 11:33:50 AM)
	 * @param newUserData java.lang.Object
	 */
	public void setBuiltMessage(String msgText) {
		builtMessage = msgText;
	}
	/**
	 * If there is a target object associated with this IMessage, then an optional
	 * ending column number may be set. To indicate no column number or use of endPosition,
	 * use "0".
	 * @param newColumnNumber int
	 */
	public void setEndColumn(int newColumnNumber) {
		if (newColumnNumber < 0) {
			endColumnNumber = 0;
		} else {
			endColumnNumber = newColumnNumber;
		}
	}
	/**
	 * If there is a target object associated with this IMessage, then an optional
	 * ending line number may be set. To indicate no line number or use of endPosition,
	 * use "0".
	 */
	public void setEndLine(int lineNumber) {
		if (lineNumber < 0) {
			this.endLineNumber = 0;
		} else {
			this.endLineNumber = lineNumber;
		}
	}
	/**
	 * To support removal of a subset of validation messages, an IValidator
	 * may assign group names to IMessages. An IMessage subset will be identified
	 * by the name of its group. Default (null) means no group.
	 */
	public void setGroupName(String name) {
		groupName = name;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/3/2001 4:46:02 PM)
	 * @param newPart java.lang.Object
	 */
	public void setMessageContributor(IEGLMessageContributor newPart) {
		messageContributor = newPart.getMessageContributor();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/3/2001 4:46:02 PM)
	 * @param newPart java.lang.Object
	 */
	public void setMessageContributor(IEGLNestedMessageContributor newPart) {
		messageContributor = newPart.getMessageContributor();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/9/2001 11:33:50 AM)
	 * @param newColumnNumber int
	 */
	public void setStartColumn(int newColumnNumber) {
		if (newColumnNumber < 0) {
			startColumnNumber = 0;
		} else {
			startColumnNumber = newColumnNumber;
		}
	}
	/**
	 * If there is a target object associated with this IMessage, then an optional
	 * line number which may be set. To indicate no line number, use "0".
	 */
	public void setStartLine(int lineNumber) {
		if (lineNumber < 0) {
			this.startLineNumber = 0;
		} else {
			this.startLineNumber = lineNumber;
		}
	}
	/**
	 * If there is a target object associated with this IMessage, then an optional
	 * starting offset which may be set. To indicate no offset, use "0".
	 */
	public void setStartOffset(int offset) {
		if (offset < 0) {
			this.startOffset = 0;
		} else {
			this.startOffset = offset;
		}
	}
	/**
	 * If there is a target object associated with this IMessage, then an optional
	 * starting offset which may be set. To indicate no offset, use "0".
	 */
	public void setEndOffset(int offset) {
		if (offset < 0) {
			this.endOffset = 0;
		} else {
			this.endOffset = offset;
		}
	}
	/**
	 * Returns a text representation of this message formatted in the default Locale,
	 * with the bundle loaded by the default ClassLoader.
	 */
	public String toString() {

		return getId() + ": " + builtMessage; //$NON-NLS-1$
	}
	/**
	 * @return
	 */
	public String[] getParams() {
		return params;
	}
	
	public String getResourceName() {
		if (getMessageContributor() instanceof IEGLMessageContributor) {
			return ((IEGLMessageContributor)getMessageContributor()).getResourceName();
		}
		return null;
	}

}
