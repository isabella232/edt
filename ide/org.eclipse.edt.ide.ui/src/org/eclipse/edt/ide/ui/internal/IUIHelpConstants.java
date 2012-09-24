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
package org.eclipse.edt.ide.ui.internal;

import org.eclipse.edt.compiler.internal.EGLBasePlugin;

public interface IUIHelpConstants {

	public static final String PREFIX = EGLBasePlugin.getHelpIDPrefix() + "."; //$NON-NLS-1$

	//Properties pages
	public static final String BUILD_PATH_CONTEXT = PREFIX + "bldp0001"; //$NON-NLS-1$
	public static final String DEFAULT_BUILD_DESCRIPTOR_CONTEXT = PREFIX + "dbdc0001"; //$NON-NLS-1$
	public static final String DEFAULT_DEPLOYMENT_DESCRIPTOR_CONTEXT = PREFIX + "dbdc0001"; //$NON-NLS-1$
	public static final String EGLPROJECT_FEATURE_CONTEXT = PREFIX + "prfe0001";	//$NON-NLS-1$
	public static final String RUNTIME_DATA_SOURCE_DESCRIPTOR_CONTEXT = PREFIX + "prds0001"; //$NON-NLS-1$

	//Preferences pages
	public static final String EGL_BASE_PREFERENCES_CONTEXT = PREFIX + "base0001"; //$NON-NLS-1$
	public static final String INTERPRETIVE_DEBUGGER_PREFERENCES_CONTEXT = PREFIX + "idbg0001"; //$NON-NLS-1$
	public static final String BIDI_PREFERENCES_CONTEXT = PREFIX + "bidi0001"; //$NON-NLS-1$
	public static final String DEBUG_BEHAVIOR_MAPPING_PREFERENCES_CONTEXT = PREFIX + "idbg0002";  //$NON-NLS-1$
	public static final String DEBUG_IMS_DLI_PREFERENCES_CONTEXT = 	PREFIX + "idbg0003";		//$NON-NLS-1$
	public static final String DEFAULT_BUILD_DESCRIPTOR_PREFERENCES_CONTEXT = PREFIX + "idbd0001"; //$NON-NLS-1$
	public static final String EDITOR_PREFERENCE_CONTEXT = PREFIX + "edtp0001"; //$NON-NLS-1$
	public static final String SOURCE_STYLES_PREFERENCE_CONTEXT = PREFIX + "srcp0001"; //$NON-NLS-1$
	public static final String EDITOR_FOLDING_PREFERENCE_CONTEXT = PREFIX + "fold0001"; //$NON-NLS-1$
	public static final String ORGANIZE_IMPORTS_PREFERENCE_PAGE = PREFIX + "orgimp0001"; //$NON-NLS-1$
	public static final String FORMATTER_PREFERENCE_PAGE = PREFIX + "formatter0001"; //$NON-NLS-1$
	public static final String FORMATTER_MODIFY_PROFILE_PAGE = PREFIX + "formatter0002"; //$NON-NLS-1$
	public static final String EGL_TEMPLATE_PREFERENCES_CONTEXT = PREFIX + "temp0001"; //$NON-NLS-1$
	public static final String EGL_EDIT_TEMPLATE_CONTEXT = PREFIX + "temp0002"; //$NON-NLS-1$
	public static final String SQL_DATABASE_CONNECTIONS_PREFERENCES_CONTEXT = PREFIX + "sqlp0002"; //$NON-NLS-1$
	public static final String SQL_DATABASE_CONNECTIONS_PREFERENCES_NEWCONN_CONTEXT = PREFIX + "sqlp0003"; //$NON-NLS-1$
	public static final String SQL_DATABASE_CONNECTIONS_PREFERENCES_EDITCONN_CONTEXT = PREFIX + "sqlp0004"; //$NON-NLS-1$
	public static final String SQL_RETRIEVE_PREFERENCES_CONTEXT = PREFIX + "sqlp0001"; //$NON-NLS-1$
	public static final String GENERATION_PREFERENCES_CONTEXT = PREFIX + "genp0001"; //$NON-NLS-1$
	public static final String MIGRATION_PREFERENCES_CONTEXT = PREFIX + "migp0001";  //$NON-NLS-1$
	public static final String PAGE_DESIGNER_PREFERENCES_CONTEXT = PREFIX + "jsfp0001";  //$NON-NLS-1$
	public static final String SERVICE_PREFERENCES_CONTEXT = PREFIX + "serp0001";  //$NON-NLS-1$

	//Views
	public static final String INTERP_DEBUG_VALIDATION_ERRORS = PREFIX + "idve0001"; //$NON-NLS-1$
	public static final String SQL_RESULTS = PREFIX + "sqlr0001"; //$NON-NLS-1$
    public static final String DLI_RESULTS = PREFIX + "dlir0001"; //$NON-NLS-1$
	public static final String GENERATION_RESULTS = PREFIX + "genr0001"; //$NON-NLS-1$
	public static final String PARTS_REFERENCE = PREFIX + "pref001"; //$NON-NLS-1$
	public static final String PARTS_LIST = PREFIX + "plist001"; //$NON-NLS-1$
	public static final String PARTS_LIST_FILTER = PREFIX + "pfltr001"; //$NON-NLS-1$


	//Generation UI Wizard
	public static final String GEN_WIZ_PAGE1 = PREFIX + "genw0001"; //$NON-NLS-1$
	public static final String GEN_WIZ_PAGE2 = PREFIX + "genw0002"; //$NON-NLS-1$
	public static final String GEN_WIZ_PAGE3 = PREFIX + "genw0003"; //$NON-NLS-1$
	public static final String GEN_WIZ_PAGE4 = PREFIX + "genw0004"; //$NON-NLS-1$

	//Dialogs
	public static final String SQL_PREPARE_STATEMENT = PREFIX + "sqld0001"; //$NON-NLS-1$
	public static final String SQL_DEFAULT_SELECT_STATEMENT = PREFIX + "sqld0002"; //$NON-NLS-1$
	public static final String SQL_VIEW_STATEMENT = PREFIX + "sqld0003"; //$NON-NLS-1$
    public static final String DLI_VIEW_STATEMENT = PREFIX + "dlid0001"; //$NON-NLS-1$
	public static final String EGL_OPEN_TYPE_DIALOG = PREFIX + "opnd001"; //$NON-NLS-1$
	public static final String EGL_MOVE_DIALOG = PREFIX + "mved001"; //$NON-NLS-1$
	public static final String EGL_RENAME_DIALOG = PREFIX + "rnmd001"; //$NON-NLS-1$
	public static final String EGL_DELETE_DIALOG = PREFIX + "dltd001"; //$NON-NLS-1$
	public static final String EGL_SEARCH_DIALOG = PREFIX + "psch001"; //$NON-NLS-1$
	public static final String PARTS_REFERENCE_FIND = PREFIX + "pref002"; //$NON-NLS-1$
	public static final String PARTS_REFERENCE_SEARCH = PREFIX + "pref003"; //$NON-NLS-1$	
	public static final String PARTS_INTERFACE_SELECTION_DIALOG = PREFIX + "insd001"; 	//$NON-NLS-1$
	public static final String IMPORT_ORGANIZE_INPUT_DIALOG = PREFIX + "orgimpdlg001";	//$NON-NLS-1$
	public static final String MIGRATION_REMOVE_FILES_DIALOG = PREFIX + "migr001";  	//$NON-NLS-1$
	
	//New Wizards
	public static final String EGL_PROGRAM_DEFINITION = PREFIX + "prgm0001"; //$NON-NLS-1$
	public static final String EGL_EMPTY_FILE_DEFINITION = PREFIX + "file0001"; //$NON-NLS-1$
	public static final String EGL_DATA_TABLE_DEFINITION = PREFIX + "dtbl0001"; //$NON-NLS-1$
	public static final String EGL_LIBRARY_DEFINITION = PREFIX + "libr0001"; //$NON-NLS-1$
	public static final String EGL_HANDLER_DEFINITION = PREFIX + "hdlr0001"; //$NON-NLS-1$
	public static final String EGL_RUI_HANDLER_DEFINITION = PREFIX + "ruihdlr0001"; //$NON-NLS-1$
	public static final String EGL_RUI_WIDGET_DEFINITION = PREFIX + "ruihdlr0002"; //$NON-NLS-1$
	public static final String EGL_FORM_GROUP_DEFINITION = PREFIX + "frmg0001"; //$NON-NLS-1$
	public static final String EGL_UI_RECORD_DEFINITION = PREFIX + "uirc0001"; //$NON-NLS-1$
	public static final String EGL_PAGE_HANDLER_DEFINITION = PREFIX + "phdl0001"; //$NON-NLS-1$
	public static final String EGL_PACKAGE_DEFINITION = PREFIX + "epkg0001"; //$NON-NLS-1$
	public static final String EGL_SOURCE_FOLDER_DEFINITION = PREFIX + "esrc0001"; //$NON-NLS-1$
	public static final String EGL_PROJECT_DEFINITION = PREFIX + "eprj0001"; //$NON-NLS-1$
	public static final String EGL_PROJECT_REQUIRED_PROJECTS = PREFIX + "eprj002"; //$NON-NLS-1$
	public static final String EGL_PROJECT_JAVA_BUILD_OPTIONS_DEFINTIION = PREFIX + "eprj003"; //$NON-NLS-1$
	public static final String EGL_PROJECT_NONJAVA_BUILD_OPTIONS_DEFINTIION = PREFIX + "eprj005"; //$NON-NLS-1$
	public static final String EGL_PROJECT_ORDER_EXPORT = PREFIX + "eprj004"; //$NON-NLS-1$
	public static final String EGL_WEB_PROJECT_DEFINITION = PREFIX + "eprj0011"; //$NON-NLS-1$ 
	public static final String EGL_NEW_PART = PREFIX + "pref004"; //$NON-NLS-1$
	public static final String EGL_SERVICE_DEFINITION = PREFIX + "service0001"; //$NON-NLS-1$
	public static final String EGL_INTERFACE_DEFINITION = PREFIX + "interface0001"; //$NON-NLS-1$
	public static final String WSDL2EGL = PREFIX + "wsdl2egl0001"; //$NON-NLS-1$
	public static final String WSDL2EGL_INTERFACE = PREFIX + "wsdl2egl0002"; //$NON-NLS-1$
	public static final String WSDL2EGL_BINDING = PREFIX + "wsdl2egl0003"; //$NON-NLS-1$
	public static final String EGL_EXTRACT_INTERFACE = PREFIX + "extractint0001"; //$NON-NLS-1$
	public static final String EGL_EXTRACT_INTERFACE_FR_ET = PREFIX + "extractint0002"; //$NON-NLS-1$
	public static final String MODULE_ADDIMPORTWSDL = PREFIX + "modwiz0001";
	public static final String MODULE_ADDCOMPONENT = PREFIX + "modwiz0002";
	public static final String EGL_ENTRYPOINT = PREFIX +"modwiz0003";//$NON-NLS-1$	
	public static final String MODULE_ENTRYPOINT_WS1 = PREFIX + "modwiz0004";
	public static final String MODULE_ENTRYPOINT_WS2 = PREFIX +"modwiz0005";
	public static final String MODULE_ENTRYPOINT_TCPIP1 = PREFIX + "modwiz0006";
	public static final String MODULE_ENTRYPOINT_TCPIP2 = PREFIX + "modwiz0007";
	public static final String EGL_EXTERNALSERVICE = PREFIX + "modwiz0008";//$NON-NLS-1$
	public static final String MODULE_EXTERNALSERVICE_WS1 = PREFIX + "modwiz0009";
	public static final String MODULE_EXTERNALSERVICE_WS2 = PREFIX + "modwiz0010";
	public static final String MODULE_EXTERNALSERVICE_WS3 = PREFIX + "modwiz0011";
	public static final String MODULE_EXTERNALSERVICE_TCPIP1 = PREFIX + "modwiz0012";
	public static final String MODULE_ADDREFERENCE = PREFIX + "modwiz0013";
	public static final String MODULE_ADDPROPERTY = PREFIX + "modwiz0014";
	public static final String MODULE_RESTBINDING = PREFIX + "modwiz0015"; 
	public static final String MODULE_SQLDATABASEBINDING = PREFIX + "modwiz0016";
	public static final String MODULE_DEDICATEDBINDING = PREFIX + "modwiz0017";
	public static final String IBMI2EGL_EXTERNALTYPE = PREFIX + "ibmi2egl0001"; //$NON-NLS-1$
	public static final String IBMI2EGL_ENTRYPOINTS = PREFIX + "ibmi2egl0002"; //$NON-NLS-1$
	public static final String IBMI2EGL_REST = PREFIX + "ibmi2egl0003"; //$NON-NLS-1$
	public static final String IBMI2EGL_SOAP = PREFIX + "ibmi2egl0004"; //$NON-NLS-1$
	public static final String IBMI2EGL_DD = PREFIX + "ibmi2egl0005"; //$NON-NLS-1$
	public static final String EGL_NEW_RECORD_WIZARD_PAGE = PREFIX + "recwiz0001"; //$NON-NLS-1$
	public static final String EGL_NEW_RECORD_TEMPLATE_SELECTION_PAGE = PREFIX + "recwiz0002"; //$NON-NLS-1$
	public static final String EGL_NEW_RECORD_FROM_JSON_PAGE = PREFIX + "recwiz0003"; //$NON-NLS-1$
	public static final String EGL_NEW_RECORD_FROM_XML_PAGE = PREFIX + "recwiz0004"; //$NON-NLS-1$
	public static final String EGL_NEW_RECORD_FROM_SCHEMA_PAGE = PREFIX + "recwiz0005"; //$NON-NLS-1$
	public static final String EGL_NEW_RECORD_SUMMARY_PAGE = PREFIX + "recwiz0006"; //$NON-NLS-1$
	public static final String NEW_PROJECT_WIZARD_TYPE_PAGE = PREFIX + "eprj0021"; //$NON-NLS-1$
	
	public static final String EGL_NEW_EXTERNALTYPE_WIZARD_PAGE = PREFIX + "extwiz0001"; //$NON-NLS-1$
	
	//EGL Editor
	public static final String EGL_EDITOR = PREFIX + "edit0001"; //$NON-NLS-1$
	public static final String EGL_EDITOR_PACKAGE_DECLARATION = PREFIX + "kwd_package"; //$NON-NLS-1$
	public static final String EGL_EDITOR_IMPORT_DECLARATION = PREFIX + "kwd_import"; //$NON-NLS-1$
	public static final String EGL_EDITOR_CLASS_DATA_DECLARATION = PREFIX + "stmnt_variable"; //$NON-NLS-1$
	public static final String EGL_EDITOR_FUNCTION_DATA_DECLARATION = PREFIX + "stmnt_variable"; //$NON-NLS-1$
	
	public static final String EGL_EDITOR_DATA_ITEM = PREFIX + "part_dataitem"; //$NON-NLS-1$
	public static final String EGL_EDITOR_DATA_TABLE = PREFIX + "part_datatable"; //$NON-NLS-1$
	public static final String EGL_EDITOR_DELEGATE = PREFIX + "part_delegate"; //$NON-NLS-1$
	public static final String EGL_EDITOR_EXTERNAL_TYPE = PREFIX + "part_externaltype"; //$NON-NLS-1$
	public static final String EGL_EDITOR_FORM = PREFIX + "part_form"; //$NON-NLS-1$
	public static final String EGL_EDITOR_FORM_GROUP = PREFIX + "part_formgroup"; //$NON-NLS-1$
	public static final String EGL_EDITOR_FUNCTION = PREFIX + "part_function"; //$NON-NLS-1$
	public static final String EGL_EDITOR_HANDLER = PREFIX + "part_handler"; //$NON-NLS-1$
	public static final String EGL_EDITOR_INTERFACE = PREFIX + "part_interface"; //$NON-NLS-1$
	public static final String EGL_EDITOR_LIBRARY = PREFIX + "part_library"; //$NON-NLS-1$
	public static final String EGL_EDITOR_PROGRAM = PREFIX + "part_program"; //$NON-NLS-1$
	public static final String EGL_EDITOR_RECORD = PREFIX + "part_record"; //$NON-NLS-1$
	public static final String EGL_EDITOR_SERVICE = PREFIX + "part_service"; //$NON-NLS-1$

	public static final String EGL_EDITOR_HANDLER_JSF = PREFIX + "part_handler"; //$NON-NLS-1$
	public static final String EGL_EDITOR_HANDLER_REPORT = PREFIX + "part_handler"; //$NON-NLS-1$
	
	public static final String EGL_EDITOR_RECORD_BASICRECORD = PREFIX + "part_record"; //$NON-NLS-1$
	public static final String EGL_EDITOR_RECORD_CONSOLEFORM = PREFIX + "part_record"; //$NON-NLS-1$
	public static final String EGL_EDITOR_RECORD_DLISEGMENT = PREFIX + "part_record"; //$NON-NLS-1$
	public static final String EGL_EDITOR_RECORD_EXCEPTION = PREFIX + "part_record"; //$NON-NLS-1$
	public static final String EGL_EDITOR_RECORD_INDEXRECORD = PREFIX + "part_record"; //$NON-NLS-1$
	public static final String EGL_EDITOR_RECORD_MQRECORD = PREFIX + "part_record"; //$NON-NLS-1$
	public static final String EGL_EDITOR_RECORD_PSBRECORD = PREFIX + "part_record"; //$NON-NLS-1$
	public static final String EGL_EDITOR_RECORD_RELATIVERECORD = PREFIX + "part_record"; //$NON-NLS-1$
	public static final String EGL_EDITOR_RECORD_SERIALRECORD = PREFIX + "part_record"; //$NON-NLS-1$
	public static final String EGL_EDITOR_RECORD_SQLRECORD = PREFIX + "part_record"; //$NON-NLS-1$
	public static final String EGL_EDITOR_RECORD_VGUIRECORD = PREFIX + "part_record"; //$NON-NLS-1$

	public static final String EGL_EDITOR_TABLE_BASICTABLE = PREFIX + "part_datatable"; //$NON-NLS-1$
	public static final String EGL_EDITOR_TABLE_MATCHINVALIDTABLE = PREFIX + "part_datatable"; //$NON-NLS-1$
	public static final String EGL_EDITOR_TABLE_MATCHVALIDTABLE = PREFIX + "part_datatable"; //$NON-NLS-1$
	public static final String EGL_EDITOR_TABLE_MSGTABLE = PREFIX + "part_datatable"; //$NON-NLS-1$
	public static final String EGL_EDITOR_TABLE_RANGECHKTABLE = PREFIX + "part_datatable"; //$NON-NLS-1$
	
	public static final String EGL_EDITOR_FORM_PRINTFORM = PREFIX + "part_form"; //$NON-NLS-1$
	public static final String EGL_EDITOR_FORM_TEXTFORM = PREFIX + "part_form"; //$NON-NLS-1$
	
	public static final String EGL_EDITOR_PROGRAM_BASICPROGRAM = PREFIX + "part_program"; //$NON-NLS-1$
	public static final String EGL_EDITOR_PROGRAM_TEXTUI = PREFIX + "part_program"; //$NON-NLS-1$
	public static final String EGL_EDITOR_PROGRAM_VGWEBTRANSACTION = PREFIX + "part_program"; //$NON-NLS-1$
	
	public static final String EGL_EDITOR_LIBRARY_BASICLIBRARY = PREFIX + "part_library"; //$NON-NLS-1$
	public static final String EGL_EDITOR_LIBRARY_NATIVELIBRARY = PREFIX + "part_library"; //$NON-NLS-1$
	
	public static final String EGL_EDITOR_NESTED_FUNCTION = PREFIX + "part_function"; //$NON-NLS-1$
	public static final String EGL_EDITOR_NESTED_FORM = PREFIX + "part_form"; //$NON-NLS-1$
	
	public static final String EGL_EDITOR_ADD_STATEMENT = PREFIX + "kwd_add"; //$NON-NLS-1$
	public static final String EGL_EDITOR_ASSIGNMENT_STATEMENT = PREFIX + "stmnt_assignment"; //$NON-NLS-1$
	public static final String EGL_EDITOR_CALL_STATEMENT = PREFIX + "kwd_call"; //$NON-NLS-1$
	public static final String EGL_EDITOR_CASE_STATEMENT = PREFIX + "kwd_case"; //$NON-NLS-1$
	public static final String EGL_EDITOR_CLOSE_STATEMENT = PREFIX + "kwd_close"; //$NON-NLS-1$
	public static final String EGL_EDITOR_CONTINUE_STATEMENT = PREFIX + "kwd_continue"; //$NON-NLS-1$
	public static final String EGL_EDITOR_DELETE_STATEMENT = PREFIX + "kwd_delete"; //$NON-NLS-1$
	public static final String EGL_EDITOR_ELSE_BLOCK = PREFIX + "kwd_else"; //$NON-NLS-1$
	public static final String EGL_EDITOR_EXECUTE_STATEMENT = PREFIX + "kwd_execute"; //$NON-NLS-1$
	public static final String EGL_EDITOR_EXIT_STATEMENT = PREFIX + "kwd_exit"; //$NON-NLS-1$
	public static final String EGL_EDITOR_FOR_STATEMENT = PREFIX + "kwd_for"; //$NON-NLS-1$
	public static final String EGL_EDITOR_FOREACH_STATEMENT = PREFIX + "kwd_foreach"; //$NON-NLS-1$
	public static final String EGL_EDITOR_FORWARD_STATEMENT = PREFIX + "kwd_forward"; //$NON-NLS-1$
	public static final String EGL_EDITOR_FUNCTION_INVOCATION_STATEMENT = PREFIX + "stmnt_function"; //$NON-NLS-1$
	public static final String EGL_EDITOR_GET_STATEMENT_BY_KEY = PREFIX + "kwd_get"; //$NON-NLS-1$
	public static final String EGL_EDITOR_GET_STATEMENT_BY_POSITION = PREFIX + "kwd_get"; //$NON-NLS-1$
	public static final String EGL_EDITOR_GOTO_STATEMENT = PREFIX + "kwd_goto"; //$NON-NLS-1$
	public static final String EGL_EDITOR_IF_STATEMENT = PREFIX + "kwd_if"; //$NON-NLS-1$
	public static final String EGL_EDITOR_MOVE_STATEMENT = PREFIX + "kwd_move"; //$NON-NLS-1$
	public static final String EGL_EDITOR_OPEN_STATEMENT = PREFIX + "kwd_open"; //$NON-NLS-1$
	public static final String EGL_EDITOR_PREPARE_STATEMENT = PREFIX + "kwd_prepare"; //$NON-NLS-1$
	public static final String EGL_EDITOR_REPLACE_STATEMENT = PREFIX + "kwd_replace"; //$NON-NLS-1$
	public static final String EGL_EDITOR_RETURN_STATEMENT = PREFIX + "kwd_return"; //$NON-NLS-1$
	public static final String EGL_EDITOR_SET_STATEMENT = PREFIX + "kwd_set"; //$NON-NLS-1$
	public static final String EGL_EDITOR_TRY_STATEMENT = PREFIX + "kwd_try"; //$NON-NLS-1$
	public static final String EGL_EDITOR_WHILE_STATEMENT = PREFIX + "kwd_while"; //$NON-NLS-1$
	
	//Seoul
	public static final String EGL_QEV_EDITOR = PREFIX + "qeve0001"; //$NON-NLS-1$
	public static final String PAGEDESIGNER_PRIMITIVE_DIALOG = PREFIX + "webt0001"; //$NON-NLS-1$
	public static final String PAGEDESIGNER_DATAITEM_DIALOG  = PREFIX + "webt0002"; //$NON-NLS-1$
	public static final String PAGEDESIGNER_RECORD_DIALOG    = PREFIX + "webt0003"; //$NON-NLS-1$
	public static final String PAGEDESIGNER_UIRECORD_DIALOG  = PREFIX + "webt0004"; //$NON-NLS-1$
	public static final String PAGEDESIGNER_NEW_FIELD_DIALOG = PREFIX + "webt0005"; //$NON-NLS-1$
	public static final String PAGEDESIGNER_NEW_DATATABLE_DIALOG = PREFIX + "webt0005"; //$NON-NLS-1$
	public static final String PAGEDESIGNER_JSFFORM_PAGE = PREFIX + "webt0006"; //$NON-NLS-1$
	public static final String PAGEDESIGNER_DD_EDITOR_OVERVIEW_PAGE = PREFIX + "webt0007"; //$NON-NLS-1$
	public static final String PAGEDESIGNER_EGL_INSERT_DATA_WIZARD = PREFIX + "webt0008"; //$NON-NLS-1$
	
	//Source Assistant
	public static final String SourceAssistant_DataItem = PREFIX + "dtim0001"; //$NON-NLS-1$
	public static final String SourceAssistant_BasicRecordProperties = PREFIX + "rdbs0001"; //$NON-NLS-1$
	public static final String SourceAssistant_IndexedRecordProperties = PREFIX + "rcin0001"; //$NON-NLS-1$
	public static final String SourceAssistant_MQRecordProperties = PREFIX + "rcmq0001"; //$NON-NLS-1$
	public static final String SourceAssistant_RelativeRecordProperties = PREFIX + "rcrl0001"; //$NON-NLS-1$
	public static final String SourceAssistant_SerialRecordProperties = PREFIX + "rcsr0001"; //$NON-NLS-1$
	public static final String SourceAssistant_SQLRecordProperties = PREFIX + "rcsq0001"; //$NON-NLS-1$
	public static final String SourceAssistant_JsfComponentTree = PREFIX + "satc0001"; //$NON-NLS-1$
	
	//EGL Module editor
	public static final String MODULE_EDITOR_OVERVIEWPAGE = PREFIX + "moded0001";
	public static final String MODULE_EDITOR_OVERVIEWPAGE_IMPORT = PREFIX + "moded0002";
	public static final String MODULE_EDITOR_OVERVIEWPAGE_COMPONENTS = PREFIX + "moded0003";
	public static final String MODULE_EDITOR_OVERVIEWPAGE_ENTRYPOINTS = PREFIX + "moded0004";
	public static final String MODULE_EDITOR_OVERVIEWPAGE_EXTERNALSERVICES = PREFIX + "moded0005";
	public static final String MODULE_EDITOR_COMPONENTPAGE = PREFIX + "moded0101";
	public static final String MODULE_EDITOR_ENTRYPOINTPAGE = PREFIX + "moded0201";
	public static final String MODULE_EDITOR_EXTERNALSERVICEPAGE = PREFIX + "moded0301";
	
	//EGL Deployment descriptor
	public static final String EGLDD_EDITOR_OVERVIEWPAGE = PREFIX + "dded0001";
	public static final String EGLDD_EDITOR_BINDINGPAGE = PREFIX + "dded0002";
	public static final String EGLDD_EDITOR_WSPAGE = PREFIX + "dded0003";
	public static final String EGLDD_EDITOR_PROTOCOLPAGE = PREFIX + "dded0004";
	public static final String EGLDD_EDITOR_IMPORTPAGE = PREFIX + "dded0005";
	public static final String EGLDD_EDITOR_OVERVIEWPAGE_SERVICEBINDINGS = PREFIX + "dded0006";
	public static final String EGLDD_EDITOR_OVERVIEWPAGE_WS = PREFIX + "dded0007";
	public static final String EGLDD_EDITOR_OVERVIEWPAGE_PROTOCOLS = PREFIX + "dded0008";
	public static final String EGLDD_EDITOR_OVERVIEWPAGE_IMPORTS = PREFIX + "dded0009";
	public static final String EGLDD_EDITOR_RESOURCESPAGE = PREFIX + "dded0010";
	
	
	public static final String EGLDDWIZ_INCLUDEEGLDD = PREFIX + "ddwiz0001";
	public static final String EGLDDWIZ_COPYEGLDD = PREFIX + "ddwiz0002";
	public static final String EGLDDWIZ_ADDBINDING = PREFIX + "ddwiz0003";
	public static final String EGLDDWIZ_ADDBINDING_WSDL = PREFIX + "ddwiz0004";
	public static final String EGLDDWIZ_ADDBINDING_WSDL_BROWSE = PREFIX + "ddwiz0005";
	public static final String EGLDDWIZ_ADDBINDING_WSDL_INTERFACE_BROWSE = PREFIX + "ddwiz0006";
	public static final String EGLDDWIZ_ADDBINDING_WSDL_2 = PREFIX + "ddwiz0007";
	public static final String EGLDDWIZ_ADDBINDING_WSDL_3 = PREFIX + "ddwiz0008";
	public static final String EGLDDWIZ_ADDBINDING_EGL = PREFIX + "ddwiz0009";
	public static final String EGLDDWIZ_ADDPROTOCOL = PREFIX + "ddwiz0010";
	public static final String EGLDDWIZ_ADDWS = PREFIX + "ddwiz0011";
	
	public static final String EGLAR_EXPORT_WIZARD = PREFIX + "eglar_export_wizard";
	public static final String EGLAR_OPTIONS_WIZARD_PAGE  = PREFIX + "eglar_export_wizard_option";
	public static final String BINARY_PROJECT_EXPORT_WIZARD = PREFIX + "binary_project_export_wizard";
}
