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

import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class UINlsStrings extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.edt.ide.ui.internal.UIMessages"; //$NON-NLS-1$
	private static final String BUNDLE_FOR_CONSTRUCTED_KEYS= "org.eclipse.edt.ide.ui.internal.ConstructedUIMessages"; //$NON-NLS-1$
	private static ResourceBundle bundleForConstructedKeys= ResourceBundle.getBundle(BUNDLE_FOR_CONSTRUCTED_KEYS);

	private UINlsStrings() {
		// Do not instantiate
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, UINlsStrings.class);
	}

	public static ResourceBundle getResourceBundleForConstructedKeys() {
		return bundleForConstructedKeys;
	}
	
	public static String CompilerPreferencePage_title;
	public static String CompilerPreferencePage_description;
	public static String CompilerPreferencePage_compilerNoLongerExistsError;
	public static String CompilerPreferencePage_duplicateCompilerIds;
	public static String CompilerPreferencePage_duplicateCompilerNames;
	public static String CompilerPreferencePage_duplicateGeneratorIds;
	public static String CompilerPreferencePage_duplicateGeneratorNames;
	public static String CompilerPreferencePage_compilerNoLongerAvailable;
	public static String CompilerPreferencePage_compilerNoLongerAvailable_title;
	public static String CompilerPreferencePage_cannotDisplayCompiler;
	public static String CompilerPreferencePage_selectedCompilerLabel;
	public static String CompilerPreferencePage_defaultCompilerLabel;
	public static String CompilerPreferencePage_selectedGeneratorsLabel;
	public static String CompilerPreferencePage_defaultGeneratorsLabel;
	public static String CompilerPreferencePage_nameLabel;
	public static String CompilerPreferencePage_languageLabel;
	public static String CompilerPreferencePage_versionLabel;
	public static String CompilerPreferencePage_ProviderLabel;
	public static String CompilerPropertyPage_errorCleaningUpPrefStore;
	public static String CompilerPropertyPage_overrideWorkspaceGenPrefs;
	public static String CompilerPropertyPage_overrideResourceGenPrefs;
	
	public static String BasePreferencePage_title;
	public static String BasePreferencePage_description;
	public static String BasePreferencePage_EGLFolderGroup_label;
	public static String BasePreferencePage_EGLSourceFolder_label;
	public static String BasePreferencePage_EGLOutputFolder_label;
	public static String BasePreferencePage_missingSourceFolderName;
	public static String BasePreferencePage_missingOutputFolderName;
	public static String BasePreferencePage_invalidSourceFolderName;
	public static String BasePreferencePage_invalidOutputFolderName;
	public static String BasePreferencePage_invalidBuildPath;
	
	public static String EGLBasePreferencePage_BasePreferencePage_ProjFeatureOptLabel;
	public static String EGLCopyAction_CopyToClipboardProblemDialog_message;
	public static String EGLCopyAction_CopyToClipboardProblemDialog_title;
	public static String EGLCopyAction_Label;
	public static String EGLDebuggerPrefPage_BtnAdd;
	public static String EGLDebuggerPrefPage_BtnDown;
	public static String EGLDebuggerPrefPage_BtnRemove;
	public static String EGLDebuggerPrefPage_BtnUp;
	public static String EGLDebuggerPrefPage_MappingMode;
	public static String EGLDebuggerPrefPage_MappingMode_ComboLabel;
	public static String EGLDebuggerPrefPage_CallTarget;
	public static String EGLDebuggerPrefPage_CompName;
	public static String EGLDebuggerPrefPage_initValue;
	public static String EGLDebuggerPrefPage_PartMapping;
	public static String EGLDebuggerPrefPage_ServiceRefName;
	public static String EGLDebuggerPrefPage_Tab_CalledPgm;
	public static String EGLDebuggerPrefPage_Tab_ServiceReference;
	public static String EGLDebuggerPrefPage_WildCardValidationMsg;
	public static String EGLDebuggerPrefPage_Multiple_Selection;
	public static String EGLDebuggerPrefPage_IMS_StoredProcedure;
	public static String EGLDebuggerPrefPage_IMS_HostPort;
	public static String EGLDebuggerPrefPage_IMS_ConversionTable;
	public static String EGLDebuggerPrefPage_IMS_ProxyTimeout;
	public static String EGLDebuggerPrefPage_IMS_PSBName;
	public static String EGLDebuggerPrefPage_IMS_BtnPrompt;
	public static String EGLDebuggerPrefPage_IMS_BtnPSBData;
	public static String EGLDebuggerPrefPage_IMS_BtnProgram;
	public static String EGLDeleteAction_Label;
	public static String EGLDeleteDialogAction;
	public static String EGLDeleteDialogElementTypeEGLFile;
	public static String EGLDeleteDialogElementTypeEGLPackage;
	public static String EGLDeleteDialogElementTypeEGLSourceFolder;
	public static String EGLDeleteDialogElementTypeResource;
	public static String EGLDeleteDialogMessagePlural;
	public static String EGLDeleteDialogMessageSingular;
	public static String EGLFormatting ;
	public static String EGLOpenActionProvider_OpenWith;
	public static String EGLPasteAction_Label;
	public static String EGLProjectFeatureGroup_JASPER;
	public static String EGLProjectFeatureGroup_SOA;
	public static String EGLProjectFeatureGroup_DD;
	public static String EGLProjectFeatureGroup_BIRT;
	public static String EGLProjectFeatureGroup_MQ;
	public static String EGLProjectFeatureGroup_LDAP;
	public static String EGLProjectfeatureGroup_iSeries;
	public static String FormatComplete ;
	public static String FormattingProgress ;
	public static String FormatTitle ;
	// Common (possibly shared across plug-ins?
	public static String SelectAllLabel;
	public static String DeselectAllLabel;
	public static String CopyLabel;

	// Property Page
	public static String PropertyProperty;
	public static String PropertyValue;
	public static String PropertyNoValueSet;
	public static String PropertyMultipleValues;
	public static String PropertyNoSelection;

	// EGL Default Build Descriptor properties page
	public static String DefaultDDPropertiesPageLabelText;
	public static String DefaultDDPropertiesPageResourceLabelText;
	public static String DefaultBDPropertiesPageDebugLabelText;
	public static String DefaultDDPropertiesPageRuntimeLabelText;
	public static String DefaultBDPropertiesPageNoValueSetText;
	public static String DefaultDDPropertiesPageJavaLabel;
	public static String DefaultBDPropertiesPageJavaScriptLabel;
	public static String DefaultBDPropertiesPageCobolLabel;
	public static String DefaultBDPropertiesPageNonJavaScriptDebugLabel;
	public static String DefaultDDPropertiesPageBrowseButtonLabel;
	public static String DefaultBDPropertiesPageBrowseDialogTitle;
	public static String DefaultBDPropertiesPageBrowseDialogDebugInterpretiveDesc;
	public static String DefaultBDPropertiesPageBrowseDialogDebugJavaScriptDesc;
	public static String DefaultBDPropertiesPageBrowseDialogRuntimeJavaDesc;
	public static String DefaultDDPropertiesPageBrowseDialogRuntimeJavaScriptDesc;
	public static String DefaultBDPropertiesPageBrowseDialogRuntimeCobolDesc;
	public static String DefaultDDPropertiesPageClearButtonLabel;
	public static String DefaultBDPropertiesPageNoTargetBuildDescriptorErrorMessage;
	public static String DefaultBDPropertiesPageNoDebugBuildDescriptorErrorMessage;
	public static String DefaultDDPropertiesPageSpecifyValuesLabel;
	public static String DefaultBDPropertiesPageInheritedValuesLabel;
	public static String DefualtBDPropertiesPageInheritedFromPreferenceText;

	// EGL Jasper Report Compiler properties page
	public static String EnableJasperReportCompilerCheckbox;
	
	// EGL Runtime Data Source properties page
	public static String RuntimeDataSourcePropertiesPageDescriptionText;
	public static String RuntimeDataSourcePropertiesNoneLabel;
	public static String RuntimeDataSourcePropertiesPageLoadFromConnectionLabel;
	public static String RuntimeDataSourcePropertiesPageModifyValuesManuallyLabel;
	public static String RuntimeDataSourcePropertiesPageErrorDialogTitle;
	
	// EGL Target Runtime Platforms properties page
//	public static String TargetRuntimePlatformPageDescriptionText;

	// Generation Results View
	public static String GRVDefaultResultsText;
	public static String GRVTitle;
	public static String GRVDisplayResultsJobName;
	public static String GRVWaitingForWorkspaceSubtaskName;
	public static String GRVCreatingMarkersSubtaskName;
	public static String GRVQueueingUpdatesSubtaskName;
	public static String GRVRefreshResultsJobName;
	public static String GRVRemoveTabText;
	public static String GRVRemoveAllTabsText;
	public static String GRVRemoveTabsWithNoErrorsText;
	public static String GRVRemoveEntryText;
	public static String GRVRemoveAllEntryText;
	public static String GRVRemoveEntryWithNoErrorsText;
	public static String GRVNOResultsText;
	public static String GRVUpdateResultsJobName;
	public static String GRVUnrescheduledUpdateResultsJobName;

	// Interpretive Debug Validation Errors View
	public static String InterpretiveDebugValidationErrorsDefault;
	public static String InterpretiveDebugValidationErrorsTitle;

	// Generation Wizard 
	public static String GenerationWizardTitle;

	// Generation Wizard Duplicate Resource Message Box
	public static String GenerationWizardDuplicateResourceMessageBoxMessage;

	// Generation Wizard Create Path Messag Box
	public static String GenerationWizardCreatePathMessageBoxMessage;

	// Generation Wizard Default Build Descriptor Type Page
	public static String GenerationWizardDBDPageName;
	public static String GenerationWizardDBDPageDBDDescription;
	public static String GenerationWizardDBDPageTitle;
	public static String GenerationWizardDBDPageDebugButtonText;
	public static String GenerationWizardDBDPageTargetSystemButtonText;
	public static String GenerationWizardDBDPageJavaWrapperButtonText;

	// Generation Wizard Part Selection Page
	public static String GenerationWizardPartSelectionPageTitle;
	public static String GenerationWizardPartSelectionPageDescription;
	public static String GenerationWizardPartSelectionPageName;
	public static String GenerationWizardPartSelectionPageDeselectAllButtonText;
	public static String GenerationWizardPartSelectionPageSelectAllButtonText;
	public static String GenerationWizardPartSelectionPageErrorMessageText;
	public static String GenerationWizardPartSelectionPageNoPartsMessageText;

	// Generation Wizard Build Descriptor Page
	public static String GenerationWizardBDPageName;
	public static String GenerationWizardBDPageTitle;
	public static String GenerationWizardBDPageManyBDButtonText;
	public static String GenerationWizardBDPageSingleBDButtonText;
	public static String GenerationWizardBDPageRuntimeBDDescription;
	public static String GenerationWizardBDPageRuntimeBDOneForAllErrorMessageText;
	public static String GenerationWizardBDPageRuntimeBDOneForEachErrorMessageText;
//	public static String GenerationWizardBDPageWrapperBDErrorMessageText;
	public static String GenerationWizardBDPageNoBuildDescriptorSelected;
	public static String GenerationWizardBDPagePartsListLabel;
	public static String GenerationWizardBDPageNoBuildDescriptorForPartLabel;
	public static String GenerationWizardBDPageBuildDescriptorListLabel;
	public static String GenerationWizardBDPageBuildDescriptorListAddButtonLabel;
	public static String GenerationWizardBDPageBuildDescriptorListRemoveButtonLabel;
	public static String GenerationWizardBDPageBuildDescriptorSelectionDialogTitle;
	public static String GenerationWizardBDPageBuildDescriptorSelectionDialogDescription;

	// Generation Parameters Page
	public static String GenerationWizardOptionOverridePageTitle;
	public static String GenerationWizardOptionOverridePageDescription;
	public static String GenerationWizardOptionOverridePageName;
	public static String GenerationWizardOptionOverrideErrorTitle;
	public static String GenerationWizardOptionOverrideGeneralOptionsTabName;
	public static String GenerationWizardOptionOverrideSQLOptionsTabName;
	public static String GenerationWizardOptionOverrideJavaOptionsTabName;
	public static String GenerationWizardOptionOverrideCOBOLOptionsTabName;
	public static String GenerationWizardOptionOverrideSystemText;
	public static String GenerationWizardOptionOverrideDestHostText;
	public static String GenerationWizardOptionOverrideDestPortText;
	public static String GenerationWizardOptionOverrideDestIDText;
	public static String GenerationWizardOptionOverrideDestPasswordText;
	public static String GenerationWizardOptionOverrideGenDirectoryText;
	public static String GenerationWizardOptionOverrideSQLIDText;
	public static String GenerationWizardOptionOverrideSQLPasswordText;
	public static String GenerationWizardOptionOverrideSQLDBText;
	public static String GenerationWizardOptionOverrideSQLJNDINameText;
	public static String GenerationWizardOptionOverrideDestDirectoryText;
	public static String GenerationWizardOptionOverrideGenProjectText;
	public static String GenerationWizardOptionOverrideTempDirectoryText;
	public static String GenerationWizardOptionOverrideTemplateDirText;
	public static String GenerationWizardOptionOverrideReservedWordText;
	public static String GenerationWizardOptionOverrideProjectIDText;
	public static String GenerationWizardOptionOverrideDestLibraryText;

	// Generation Wizard Command File Page
	public static String GenerationWizardCommandFilePageTitle;
	public static String GenerationWizardCommandFilePageDescription;
	public static String GenerationWizardCommandFilePageName;
	public static String GenerationWizardCommandFilePageCreateCmdFileButtonText;
	public static String GenerationWizardCommandFilePageCreateCmdFileOnlyButtonText;
	public static String GenerationWizardCommandFilePageCreateCmdFileAndGenerateButtonText;
	public static String GenerationWizardCommandFilePageCmdFilePathText;
	public static String GenerationWizardCommandFilePageBrowseButtonText;
	public static String GenerationWizardCommandFilePageInvalidFilePathText;
	public static String GenerationWizardCommandFilePageInvalidFileExtensionText;
	public static String GenerationWizardCommandFilePageCreateCmdFileWithEglPathButtonText;

	// Generation Results Dialog
	public static String GenerationWizardResultsDialogTitle;

	// Generation Wizard Build Descriptor table
	public static String GenerationWizardBDTablePartText;
	public static String GenerationWizardBDTableBDText;
	public static String GenerationWizardBDTableNoValueSetText;

	// EGL Generation Order properties page
	public static String ProjectPathPropertiesPageLabelText;
	public static String ProjectPathPropertiesPageUpButtonLabelText;
	public static String ProjectPathPropertiesPageDownButtonLabelText;
	public static String ProjectPathPropertiesPageClosedProjectLabelText;
	
	// Generation strings
	public static String GenerateFailedMessageTitle;
	public static String GenerateNoBuildDescriptor;
	
	// RCP strings
	public static String RCPFailedToLaunchTitle;
	public static String RCPFailureMessage;
	
	// EGL Enable/Disable debug on server
	public static String ServerEnableDisableDebugDialogTitle;
	public static String ServerEnableDebugDialogText;
	public static String ServerDisableDebugDialogText;
	public static String ServerEnableDisableDebugErrorDialogText;

	// SQL Views/Dialogs    
	public static String SQLErrorsViewTitle;
	public static String SQLErrorsViewDefaultLabel;
	public static String SQLRetrieveMessageViewTitle;
	public static String SyntaxError ;
	public static String UndoNotSupportedMsg ;
	public static String ValidateSQLStatementMessageDialogTitle;
	public static String ValidateSQLStatementSuccessfulMessageText;
	public static String SQLRecordPartDialogNoPartsMessage;
	public static String SQLRecordPartDialogMessagePart1;
	public static String SQLRecordPartDialogMessagePart2;
	public static String SQLRecordPartDialogTitle;
	public static String SQLRecordPartDialogErrorMessage;
	public static String SQLRecordPartDialogErrorTitle;
	public static String AddSQLStatementMessageDialogTitle;
	public static String AddWithIntoSQLStatementMessageDialogTitle;
	public static String RemoveSQLStatementMessageDialogTitle;
	public static String ResetSQLStatementMessageDialogTitle;
	public static String ViewOrgImportStatus;
	public static String ViewSQLStatementMessageDialogTitle;
	public static String SQLCreateDataItemMessageTitle;
	public static String SQLSaveDirtyEditorsDialogTitle;
	public static String SQLSaveDirtyEditorsDialogMessage;
    
    //SQL Statement View Dialog
	public static String ViewSQLStatementDialogTitle;
	public static String ViewDefaultSelectStatementDialogTitle;
	public static String AddSQLStatementButtonLabel;
	public static String AddWithIntoButtonLabel;
	public static String ResetSQLStatementButtonLabel;
	public static String ValidateSQLButtonLabel;
    
    //DLI Statement Views and Dialogs
	public static String ViewDLIStatementDialogTitle;
	public static String AddDLIStatementButtonLabel;
	public static String ResetDLIStatementButtonLabel;
	public static String DLIErrorsViewTitle;
	public static String DLIErrorsViewDefaultLabel;
	public static String AddDLIStatementMessageDialogTitle;
	public static String RemoveDLIStatementMessageDialogTitle;
	public static String ResetDLIStatementMessageDialogTitle;
	public static String ViewDLIStatementMessageDialogTitle;
	public static String DLIRecordPartDialogMessagePart1;
	public static String DLIRecordPartDialogMessagePart2;
	public static String DLIRecordPartDialogTitle;
    
	//SQL Prepare Statement Dialog
	public static String CreatePreparedStatementDialogTitle;
	public static String SQLPrepareStatementMessageTitle;
	public static String PreparedStatementIDLabel;
	public static String SQLRecordNameLabel;
	public static String ResultSetIDFieldLabel;
	public static String ExecutionTypeLabel;
	public static String ComboItemDelete;
	public static String ComboItemInsert;
	public static String ComboItemUpdate;
	public static String ComboItemGetByKey;
	public static String ComboItemGetByKeyForUpdate;
	public static String ComboItemOpenForUpdate;
	public static String SQLRecordPartNameLabel;
	public static String StatusFieldErrorMessageOne;
	public static String StatusFieldErrorMessageTwo;
	public static String StatusFieldErrorMessageThree;
	
	// EGL Base Preference
	public static String PreferenceManager_0;
	public static String BasePreferencePage_Encoding;
	public static String BasePreferencePage_BaseGroup_label;
	public static String BasePreferencePage_Base_VAGCompatibility_label;
	public static String BasePreferencePage_EncodingGroup_label;
	public static String BasePreferencePage_Encoding_Description_label;
	public static String BasePreferencePage_DestinationGroup_label;
	public static String BasePreferencePage_Destination_Userid_label;
	public static String BasePreferencePage_Destination_Password_label;
	public static String BasePreferencePage_DestinationGroup_Description;
	public static String BasePreferencePage_WebFeatureGroup_label;
	public static String BasePreferencePage_WebFeature_EGLJSF_label;
	public static String BasePreferencePage_WebFeature_WebTrans_label;
	public static String BasePreferencePage_WebFeature_JavaObjInterface_label;
	public static String BasePreferencePage_WebFeature_Error_Message;
	public static String BasePreferencePage_BuildPathProblemsGroup_label;
	public static String BasePreferencePage_IncompleteBuildPath_label;
	public static String BasePreferencePage_ComboItemError;
	public static String BasePreferencePage_ComboItemWarning;

	//preference page to link to project preference page
	public static String PropertyAndPreferencePage_useworkspacesettings_change;
	public static String PropertyAndPreferencePage_showprojectspecificsettings_label;
	public static String PropertyAndPreferencePage_useprojectsettings_label;

	// EGL Base Preference Page rebuild all dialog and status monitor strings
	public static String BasePreferencePage_Rebuild_All_dialog_title;
	public static String BasePreferencePage_Rebuild_All_dialog_message;
	public static String BasePreferencePage_BuildPathComboChanged_Rebuild_All_title;
	public static String BasePreferencePage_BuildPathComboChanged_Rebuild_All_message;
	public static String BasePreferencePage_Rebuild_All_monitor_string;
	
	// EGL Organize Import Preferences
	public static String ImportOrganizePreferencePage_description;
	public static String ImportOrganizePreferencePage_title;

	public static String ImportOrganizeConfigurationBlock_order_label;
	public static String ImportOrganizeConfigurationBlock_other_normal;
	public static String ImportOrganizeConfigurationBlock_other_normal_label;
	public static String ImportOrganizeConfigurationBlock_order_add_button;
	public static String ImportOrganizeConfigurationBlock_order_edit_button;
	public static String ImportOrganizeConfigurationBlock_order_up_button;
	public static String ImportOrganizeConfigurationBlock_order_down_button;
	public static String ImportOrganizeConfigurationBlock_order_remove_button;
	public static String ImportOrganizeConfigurationBlock_order_load_button;
	public static String ImportOrganizeConfigurationBlock_order_save_button;
	public static String ImportOrganizeConfigurationBlock_other_description;
	public static String ImportOrganizeConfigurationBlock_threshold_label;
	public static String ImportOrganizeConfigurationBlock_error_invalidthreshold;
	public static String ImportOrganizeConfigurationBlock_loadDialog_title;
	public static String ImportOrganizeConfigurationBlock_loadDialog_error_title;
	public static String ImportOrganizeConfigurationBlock_loadDialog_error_message;
	public static String ImportOrganizeConfigurationBlock_saveDialog_title;
	public static String ImportOrganizeConfigurationBlock_saveDialog_error_title;
	public static String ImportOrganizeConfigurationBlock_staticthreshold_label;
	public static String ImportOrganizeConfigurationBlock_saveDialog_error_message;
	public static String ImportOrganizeInputDialog_title;
	public static String ImportOrganizeInputDialog_browse_packages_button;
	public static String ImportOrganizeInputDialog_browse_types_label;
	public static String ImportOrganizeInputDialog_ChoosePackageDialog_title;
	public static String ImportOrganizeInputDialog_ChoosePackageDialog_description;
	public static String ImportOrganizeInputDialog_ChoosePackageDialog_empty;
	public static String ImportOrganizeInputDialog_ChooseTypeDialog_title;
	public static String ImportOrganizeInputDialog_ChooseTypeDialog_description;
	public static String ImportOrganizeInputDialog_ChooseTypeDialog_error_message;

	public static String ImportOrganizeInputDialog_error_enterName;
	public static String ImportOrganizeInputDialog_error_invalidName;
	public static String ImportOrganizeInputDialog_error_entryExists;
	public static String ImportOrganizeInputDialog_name_group_label;	
	
	// EGL Editor Preference	
	public static String EGLEditorName;
	public static String TextEditorLink;
	public static String TextEditorTooltip;
	public static String EditorPreferencePageDescription;
	public static String EditorFoldingPreferencePageDescription;
	public static String EditorFoldingPreferencePageEnable;
	public static String EditorFoldingPreferencePageCombo_caption;
	public static String EditorFoldingPreferencePageInfo_no_preferences;
	public static String EditorFoldingPreferenceError_not_exist;
	
	public static String ShowLineNumbersLabel;
	public static String AnnotateErrorsLabel;
	public static String AnnotateErrorsKey;
	public static String AnnotateErrorsInOverviewLabel;
	public static String AnnotateErrorsInRulerKey;
	public static String AnnotateErrorsAsYouTypeLabel;
	public static String ContentAssistLabel;
	public static String AutoActivationLabel;
	
	//EGL Template Preference
	public static String Preference_template_name_label;
	public static String Preference_template_description_label;
	
	//EGL Source Styles Preference
	public static String SourceStyleDialogTitle;
	public static String SourceStyleDefault;
	public static String SourceStyleKeywords;
	public static String SourceStyleLiterals;
	public static String SourceStyleSingleComment;
	public static String SourceStyleMultiComment;
	public static String SourceStyleSystemWords;
	public static String SourceStyleBackgroundColor;
	public static String SourceStyleSystemDefault;
	public static String SourceStyleCustom;
	public static String SourceStyleForeground;
	public static String SourceStyleColor;
	public static String SourceStyleBold;
	public static String SourceStylePreview;

	//EGL editor Actions
	public static String AddBreakpointLabel;
	public static String EnableBreakpointString;
	public static String DisableBreakpointString;
	public static String BreakpointPropertiesLabel;

	public static String ExpandAllActionLabel;
	public static String CollapseAllActionLabel;
	public static String GenerateActionLabel;
	public static String GenerateWithWizardActionLabel;
	public static String RemoveActionLabel;
	public static String SortMenuLabel;
	public static String SortByNameActionLabel;
	public static String SortByOrderActionLabel;
	public static String SortByTypeActionLabel;
	
	//EGL Search actions
	public static String FindReferencesLabel;
	public static String FindDeclarationLabel;
	
	// SQL actions
	public static String SQLStatementActionLabel;
	public static String SQLRecordActionLabel;

    // DLI actions
	public static String DLIStatementActionLabel;
    
	public static String InitializableRefactoring_argument_not_exist;
	public static String InitializableRefactoring_illegal_argument;
	public static String InitializableRefactoring_inacceptable_arguments;
	public static String InitializableRefactoring_input_not_exists;
	public static String InitializableRefactoring_inputs_do_not_exist;
	
	public static String RefactoringProcessor_precondition_checking;
	public static String RefactoringProcessor_creating_changes;
	public static String RefactoringProcessor_searching_references;
	
	public static String EGLMergeViewer_title;

	public static String Refactor;
	public static String Refactor_unexpected_exception;
	
	public static String Checks_eglfile_name_used;
	
	public static String MoveRefactoring_reorganize_elements;
	public static String MoveRefactoring_updatePackage;
	public static String MoveRefactoring_updateImports;
	
	public static String CopyRefactoring_cu_copyOf1;
	public static String CopyRefactoring_cu_copyOfMore;
	public static String CopyRefactoring_resource_copyOf1;
	public static String CopyRefactoring_resource_copyOfMore;
	public static String CopyRefactoring_package_copyOf1;
	public static String CopyRefactoring_package_copyOfMore;
	
	public static String ReorgCopyWizard_1;
	public static String ReorgCopyWizard_2;
	
	public static String CopyCompilationUnitChange_copy;
	public static String CopyPackageChange_copy;
	public static String CopyPackageFragmentRootChange_copy;
	
	public static String RefactoringStarter_save_all_resources;
	public static String RefactoringStarter_must_save;
	public static String RefactoringStarter_unexpected_exception;
	
	public static String Move;
	public static String MoveEGLFileChange_name;
	public static String MoveAction_update_references;
	public static String MoveSupport_dialog_title;
	
	public static String Rename;
	public static String RenameAction_unavailable;
	public static String RenameSupport_not_available;
	public static String RenameSupport_dialog_title;
	public static String RenamePartRefactoring_name;	
	public static String RenamePartProcessor_changeCategory_type;
	public static String RenamePartProcessor_change_name;
	public static String RenamePartProcessor_changeCategory_type_description;
	public static String RenamePartRefactoring_exists;
	public static String RenamePartRefactoring_rename_main_function;
	public static String RenamePartRefactoring_update;
	public static String RenamePartRefactoring_update_reference;
	public static String RenamePartRefactoring_creating_change;
	public static String RenameEGLFileRefactoring_name;
	public static String RenameInputWizardPage_update_references;
	public static String RenameInputWizardPage_new_name;
	public static String RenameFileWizard_defaultPageTitle;
	public static String RenamePartWizard_defaultPageTitle;
	public static String RenamePartWizardInputPage_description;
	public static String RenameResourceChange_does_not_exist;
	public static String RenameResourceChange_name;
	public static String RenameResourceChange_rename_resource;
	
	public static String OverwriteHelper_0;
	public static String OverwriteHelper_1;
	public static String OverwriteHelper_2;
	public static String OverwriteHelper_3;
	
	public static String ReadOnlyResourceFinder_0;
	public static String ReadOnlyResourceFinder_1;
	public static String ReadOnlyResourceFinder_2;
	public static String ReadOnlyResourceFinder_3;
	
	public static String ReorgQueries_skip_all;
	
	public static String ReorgPolicy_move_source_folder;
	public static String ReorgPolicy_move_package;
	public static String ReorgPolicy_move;
	public static String ReorgPolicy_move_members;
	public static String ReorgPolicy_copy;
	public static String CopyResourceString_copy;
	public static String ReorgPolicyFactory_doesnotexist0;
	public static String ReorgPolicyFactory_readonly;
	public static String ReorgPolicyFactory_structure;
	public static String ReorgPolicyFactory_inconsistent;
	public static String ReorgPolicyFactory_archive;
	public static String ReorgPolicyFactory_external;
	public static String ReorgPolicyFactory_phantom;
	public static String ReorgPolicyFactory_inaccessible;
	public static String ReorgPolicyFactory_not_this_resource;
	public static String ReorgPolicyFactory_linked;
	public static String ReorgPolicyFactory_no_resource;
	public static String ReorgPolicyFactory_doesnotexist1;
	public static String ReorgPolicyFactory_cannot_modify;
	public static String ReorgPolicyFactory_cannot;
	public static String ReorgPolicyFactory_package_decl;
	public static String ReorgPolicyFactory_src2proj;
	public static String ReorgPolicyFactory_jmodel;
	public static String ReorgPolicyFactory_src2writable;
	public static String ReorgPolicyFactory_src2nosrc;
	public static String ReorgPolicyFactory_packages;
	public static String ReorgPolicyFactory_cannot1;
	public static String ReorgPolicyFactory_noCopying;
	public static String ReorgPolicyFactory_element2parent;
	public static String ReorgPolicyFactory_package2parent;
	public static String ReorgPolicyFactory_parent;
	public static String ReorgPolicyFactory_noMoving;
	
	public static String ReorgMoveAction_3;
	public static String ReorgMoveAction_4;
	public static String ReorgMoveWizard_3;
	public static String ReorgMoveWizard_4;
	public static String ReorgMoveWizard_textual_move;
	public static String ReorgMoveWizard_newPackage;
	public static String ReorgMoveWizard_newEGLFile;
	
	public static String EGLMoveProcessor_change_name;
	public static String MoveRefactoring_0;
	
	public static String EGLCopyProcessor_change_name;
	public static String CopyRefactoring_0;
	
	public static String Change_does_not_exist;
	public static String Change_has_modifications;
	public static String Change_is_read_only;
	public static String Change_is_unsaved;
	public static String Change_same_read_only;
	
	public static String DynamicValidationStateChange_workspace_changed;
	
	public static String createFile_creating_resource;
	public static String createFile_Create_file;
	public static String deleteFile_deleting_resource;
	public static String deleteFile_Delete_File;

	public static String CreateFileChange_error_exists;
	public static String CreateFileChange_error_unknownLocation;
	
	public static String OpenRefactoringWizardAction_refactoring;
	
	public static String ReorgQueries_nameConflictMessage;
	public static String ReorgQueries_resourceWithThisNameAlreadyExists;
	public static String ReorgQueries_invalidNameMessage;
	public static String ReorgQueries_packagewithThatNameexistsMassage;
	public static String ReorgQueries_Confirm_Overwritting;
	public static String ReorgQueries_resourceExistsWithDifferentCaseMassage;
	
	public static String Rename_BuildFileAssociations;
	public static String Rename_GenProject;
	public static String Rename_GenProject_In_BLD_File;
	public static String Rename_Project_In_Store;

	public static String RenameResourceChange_JSP_Updates;
	public static String RenameResourceChange_JSFHandler_Updates;
	public static String RenameResourceChange_JSFHandler_CodeBehind;
	public static String RenameResourceChange_JSFHandler_CodeBehind_PageCode;
	public static String RenameResourceChange_JSFHandler_References_PageCode;

	public static String DeleteJsfBeanRefactoring_name;	
	public static String DeleteFacesConfigEntriesRefactoring_name;	
	public static String DeleteRefactoring_name;

	public static String UpdateFacesConfigEntriesRefactoring_name;	

	public static String Resources_outOfSyncResources;
	public static String Resources_outOfSync;
	public static String Resources_modifiedResources;
	public static String Resources_fileModified;
	
	public static String ReorgUserInputPage_choose_destination_single;
	public static String ReorgUserInputPage_choose_destination_multi;

	public static String EGLAnnotationHoverMultipleMarkersAtThisLine;

	public static String MemberFilterActionGroup_hide_nonpublic_label;
	public static String MemberFilterActionGroup_hide_nonpublic_tooltip;
	public static String MemberFilterActionGroup_hide_nonpublic_description;
    
	public static String TypeSelectionDialog_errorMessage;
    
	public static String EGLImageLabelprovider_assert_wrongImage;
	
	// Default Build Descriptor Preference page		
	public static String DefaultBuildDescriptorPreferencePageDataSourceChangeOption;
	public static String DefaultBuildDescriptorPreferencePageDataSourceChangePrompt;
	public static String DefaultBuildDescriptorPreferencePageDataSourceChangeAlways;
	public static String DefaultBuildDescriptorPreferencePageDataSourceChangeNever;
    
    // Interpretive Debug Preference page and dialog
	public static String InterpretiveDebugStopLabel;
	public static String InterpretiveDebugStopInCalledProgramLabel;
	public static String InterpretiveDebugStopInTransferredToProgramLabel;
	public static String InterpretiveDebugHotswapLabel;
	public static String InterpretiveDebugWarnNoSrcLabel;
	public static String InterpretiveDebugWarnFormatErrorLabel;
	public static String InterpretiveDebugCallByAliasLabel;
	public static String InterpretiveDebugPromptLabel;
	public static String InterpretiveDebugSetSystemPromptLabel;
	public static String InterpretiveDebugAddProjectLabel;
	public static String InterpretiveDebugAddProjectTitle;
	public static String InterpretiveDebugAddJarsLabel;
	public static String InterpretiveDebugAddJarsTitle;
	public static String InterpretiveDebugAddDirectoryLabel;
	public static String InterpretiveDebugAddDirectoryTitle;
	public static String InterpretiveDebugClassPathRemoveLabel;
	public static String InterpretiveDebugClassPathMoveUpLabel;
	public static String InterpretiveDebugClassPathMoveDownLabel;
	public static String InterpretiveDebugClasspathOrderLabel;
	public static String InterpretiveDebugNoProjectsMessage;
	public static String InterpretiveDebugDuplicateMessage;
	public static String InterpretiveDebugAddProjectdialogtitle;
	public static String InterpretiveDebugProjectsdialogLabel;
	public static String InterpretiveDebugPortLabel;
	public static String InterpretiveDebugPortInvalid;
	public static String InterpretiveDebugCharacterEncodingLabel;
	public static String InterpretiveDebugEbcdicCodepage;
	public static String InterpretiveDebugDefaultEncoding;
	public static String InterpretiveDebugRemoteUserLabel;
	public static String InterpretiveDebugRemotePasswordLabel;
	public static String InterpretiveDebugVSEVsamLabel;
	public static String InterpretiveDebugCharacterEncodingCp037;
	public static String InterpretiveDebugCharacterEncodingCp273;
	public static String InterpretiveDebugCharacterEncodingCp277;
	public static String InterpretiveDebugCharacterEncodingCp278;
	public static String InterpretiveDebugCharacterEncodingCp280;
	public static String InterpretiveDebugCharacterEncodingCp284;
	public static String InterpretiveDebugCharacterEncodingCp285;
	public static String InterpretiveDebugCharacterEncodingCp297;
	public static String InterpretiveDebugCharacterEncodingCp500;
	public static String InterpretiveDebugCharacterEncodingCp870;
	public static String InterpretiveDebugCharacterEncodingCp875;
	public static String InterpretiveDebugCharacterEncodingCp1025;
	public static String InterpretiveDebugCharacterEncodingCp1026;
	public static String InterpretiveDebugCharacterEncodingCp1364;
	public static String InterpretiveDebugCharacterEncodingCp1371;
	public static String InterpretiveDebugCharacterEncodingCp1388;
	public static String InterpretiveDebugCharacterEncodingCp1390;
	public static String InterpretiveDebugCharacterEncodingCp1399;
	public static String InterpretiveDebugSuspendLabel;
	public static String InterpretiveDebugGeneralLabel;
	public static String InterpretiveDebugDialogsLabel;
	public static String InterpretiveDebugClearAllHiddenDialogsLabel;
	public static String InterpretiveDebugClearAllHiddenDialogsButton;
	public static String InterpretiveDebugClearAllSavedSqlPromptDialogsLabel;
	public static String InterpretiveDebugClearAllSavedSqlPromptDialogsButton;
	public static String InterpretiveDebugClearAllSavedRemoteCallPromptDialogsLabel;
	public static String InterpretiveDebugClearAllSavedRemoteCallPromptDialogsButton;	
	public static String InterpretiveDebugSeeRunDebugLabel;
	public static String InterpretiveDebugReleaseResourcesLabel;
    
    // Generation Preference
	public static String GeneratationBuildBeforeGenerate;
	public static String GeneratationAutoGenerateParts;
	public static String GeneratationAutoGeneratePartsDescription1;
	public static String GeneratationAutoGeneratePartsDescription2;
	public static String GeneratationAutoGeneratePartsJava;
	public static String GeneratationAutoGeneratePartsJavaScript;
	public static String GeneratationAutoGeneratePartsCobol;
	public static String GeneratationAutoGenerateDeploymentDescriptor;
	
	// Jeff 0910 - Template Variable Messages
	public static String EGLTemplateVariableNameDescription;
	public static String EGLTemplateVariablesSpacingError;
	
	// Jeff 0916 - EGL Outline View Filters
	public static String FilterDetailsLabel;
	public static String FilterDetailsDescription;
	public static String FilterDetailsTooltip;
	
	// Outline view	
	public static String OutlineViewImportGroup;
	
	// Outline view actions	
	public static String OutlineViewSyncWithEditorLabel;
	public static String OutlineViewSyncWithEditorTooltip;
	public static String OutlineViewSyncWithEditorDescription;

	// Craig  - EGL Parts Reference View Filters
	public static String PartsReferenceFilterDataLabel;
	public static String PartsReferenceFilterDataDescription;
	public static String PartsReferenceFilterDataTooltip;
	public static String PartsReferenceRefreshLabel;
	public static String PartsReferenceRefreshDescription;
	public static String PartsReferenceRefreshTooltip;
	public static String PartsReferenceNoPartAvailable;
	public static String PartsReferenceParamContainer;
	public static String PartsReferenceDataContainer;
	public static String PartsReferenceServiceReferences;
	public static String PartsReferenceUseContainer;
	public static String PartsReferenceImplementsContainer;
	public static String PartsReferenceOpenPartLabel;
	public static String PartsReferenceOpenPartTitle;
	public static String PartsReferenceOpenPartDescription;
	public static String PartsReferenceOpenPartTooltip;
	public static String PartsReferenceLinkEditorLabel;
	public static String PartsReferenceLinkEditorDescription;
	public static String PartsReferenceLinkEditorTooltip;
	public static String PartsReferenceGoIntoLabel;
	public static String PartsReferenceSearchLabel;
	public static String PartsReferenceFindLabel;
	public static String PartsReferenceBrowsePartsLabel;
	public static String PartsReferenceExpandBranchLabel;
	public static String PartsReferenceExpandAllLabel;
	public static String PartsReferencePrevLabel;
	public static String PartsReferencePrevDescription;
	public static String PartsReferencePrevTooltip;
	public static String PartsReferenceNextLabel;
	public static String PartsReferenceNextDescription;
	public static String PartsReferenceNextTooltip;
	public static String PartsReferenceHistoryLabel;
	public static String PartsReferenceHistoryDescription;
	public static String PartsReferenceHistoryTooltip;
	public static String PartsReferenceNodeOpenPartLabel;
	public static String PartsReferenceRemoveAllLabel;
	public static String PartsReferenceNoContextText;
	public static String PartReferenceNoRootPart;
	public static String PartReferencePartNotDefined;
	public static String PartsReferenceFlatLabel;
	public static String PartsReferenceHierLabel;
	public static String PartsReferenceFlatPartHeader;
	public static String PartsReferenceFlatProjHeader;
	public static String PartsReferenceFlatPackHeader;
	public static String PartsReferenceFlatFileHeader;

	//PRV API
	public static String PartsReferenceAPIJobName; 

	//Parts Browser
	public static String PartsBrowserNoContextText;
	public static String PartsBrowserNodeOpenPartLabel;
	public static String PartBrowserNoRootPart;
	public static String PartsBrowserFlatPartHeader;
	public static String PartsBrowserFlatProjHeader;
	public static String PartsBrowserFlatPackHeader;
	public static String PartsBrowserFlatFileHeader;
	public static String PartsBrowserFilterActionLabel;
	public static String PartsBrowserFilterPageLabel;
	public static String PartsBrowserFilterPageResourceLabel;
	public static String PartsBrowserFilterPageTypeLabel;
	public static String PartsBrowserFilterPageExpressionLabel;
	public static String PartsBrowserFilterPageSelectallLabel;
	public static String PartsBrowserFilterPageDeselectallLabel;
	public static String PartBrowserFlatLabelFlatType;
	public static String PartBrowserFlatParttypeProgramLabel;
	public static String PartBrowserFlatParttypeDelegateLabel;
	public static String PartBrowserFlatParttypeExternalTypeLabel;
	public static String PartBrowserFlatParttypeEnumerationLabel;
	public static String PartBrowserFlatParttypeFunctionLabel;
	public static String PartBrowserFlatParttypePagehandlerLabel;
	public static String PartBrowserFlatParttypeReporthandlerLabel;
	public static String PartBrowserFlatParttypeServiceLabel;
	public static String PartBrowserFlatParttypeInterfaceLabel;
	public static String PartBrowserFlatParttypeLibraryLabel;
	public static String PartBrowserFlatParttypeDatatableLabel;
	public static String PartBrowserFlatParttypeRecordLabel;
	public static String PartBrowserFlatParttypeDataitemLabel;
	public static String PartBrowserFlatParttypeFormgroupLabel;
	public static String PartBrowserFlatParttypeFormLabel;
	public static String PartBrowserDefaultFilterName;
	public static String PartsBrowserRemoveAllLabel;
	public static String PartsBrowserHistoryLabel;
	public static String PartsBrowserHistoryDescription;
	public static String PartsBrowserHistoryTooltip;
	public static String PartsBrowserStatusLabel;
	public static String PartsBrowserRefreshLabel;
	public static String PartsBrowserRefreshDescription;
	public static String PartsBrowserRefreshTooltip;
	public static String PartsBrowserOpenPartLabel;
	public static String PartsBrowserOpenPartinPartsRefLabel;
	public static String PartsBrowserSelectAllActionLabel;
	public static String PartsBrowserShowInProjectExplorerActionLabel;
	//Outline and Parts Reference search actions
	public static String EGLSearchActionRreference;
	public static String EGLSearchActionDeclaration;
	public static String EGLSearchActionWorkspace;
	public static String EGLSearchActionProject;
	public static String EGLSearchActionWorkingset;
	
	// Jeff 0926 - EGL Open Part Action Dialog
	public static String OpenPartDescription;
	public static String OpenPartTooltip;
	public static String OpenPartLabel;
	public static String OpenPartErrorMessage;
	public static String OpenPartErrorTitle;
	
	public static String OpenPartDialogTitle;
	public static String OpenPartDialogMessage;
	public static String OpenPartDialog_UpperLabel;
	public static String OpenPartDialog_LowerLabel;
	
	public static String OpenPartDialog_NoParts_Message;
	public static String OpenPartDialog_NoParts_Title;
	public static String OpenPartDialog_ErrorMessage;
	public static String OpenPartDialog_DefaultPackage;
	
	// Jeff 1001 - EGL GoToPart Action
	public static String GotoPartActionLabel;
	public static String GotoPartActionDescription;
	public static String GotoPartDialogMessage;
	public static String GotoPartDialogTitle;
	public static String GotoPartErrorMessage;
	
	public static String DefaultDialogMessage;
	
	// Jeff 1008 - EGLOpenOnSelection
	public static String OpenOnSelection_NoMatchingPart;
	public static String OpenOnSelection_JSP_Bad_Project;
	public static String OpenOnSelection_JSP_FolderDNE;
	public static String OpenOnSelection_error_messageArgs;
	public static String OpenOnSelection_error_messageProblems;
	
	// SQL message inserts
	public static String AddSQLStatementActionMessageInsert;
	public static String AddWithIntoSQLStatementActionMessageInsert;
	public static String ValidateSQLStatementActionMessageInsert;
	public static String RemoveSQLStatementActionMessageInsert;
	public static String ResetSQLStatementActionMessageInsert;
	public static String ViewSQLStatementActionMessageInsert;
	public static String RetrieveSQLActionMessageInsert;
	public static String PrepareSQLStatementActionMessageInsert;
	public static String CreateDataItemMessageInsert;
    
    // DLI message inserts
	public static String AddDLIStatementActionMessageInsert;
	public static String RemoveDLIStatementActionMessageInsert;
	public static String ResetDLIStatementActionMessageInsert;
	public static String ViewDLIStatementActionMessageInsert;
    
	// Content assist additional information
	public static String CAProposal_Array;
	public static String CAProposal_ArrayDictionary;
	public static String CAProposal_ArrayFunctionSystemWord;
	public static String CAProposal_ArrayVariableSystemWord;
	public static String CAProposal_BooleanOperator;
	public static String CAProposal_BytesLibrary;
	public static String CAProposal_ColorState;
	public static String CAProposal_ConsoleField;
	public static String CAProposal_ConsoleFieldFields;
	public static String CAProposal_ConsoleLibrary;
	public static String CAProposal_ConstantDeclaration;
	public static String CAProposal_ConstantDeclarationIn;
	public static String CAProposal_DateLibrary;
	public static String CAProposal_DictionaryKey;
	public static String CAProposal_DLISegmentRecordState;
	public static String CAProposal_EGLKeyword;
	public static String CAProposal_EGLSystemLibrary;
	public static String CAProposal_EnumerationName;
	public static String CAProposal_EnumerationValue;
	public static String CAProposal_EventKind;
	public static String CAProposal_Exception;
	public static String CAProposal_ExitProgramStatement;
	public static String CAProposal_ExitStatement;
	public static String CAProposal_ExternalTypeType;
	public static String CAProposal_FieldName;
	public static String CAProposal_FormType;
	public static String CAProposal_FunctionSystemWord;
	public static String CAProposal_HandlerFunction;
	public static String CAProposal_HandlerType;
	public static String CAProposal_HighlightingState;
	public static String CAProposal_IndexedState;
	public static String CAProposal_IntensityState;
	public static String CAProposal_InterfaceFunction;
	public static String CAProposal_InterfaceType;
	public static String CAProposal_IOErrorState;
	public static String CAProposal_ItemName;
	public static String CAProposal_ItemState;
	public static String CAProposal_JasperReportHandlerFunction;
	public static String CAProposal_JSPFile;
	public static String CAProposal_KeyValue;
	public static String CAProposal_LibraryFunction;
	public static String CAProposal_LibraryType;
	public static String CAProposal_LibraryVariable;
	public static String CAProposal_LOBLibrary;
	public static String CAProposal_LoosePrimitiveType;
	public static String CAProposal_MathLibrary;
	public static String CAProposal_MenuFields;
	public static String CAProposal_MenuItemFields;
	public static String CAProposal_ModifiedState;
	public static String CAProposal_NestedFunction;
	public static String CAProposal_NumericConstant;
	public static String CAProposal_Operator;
	public static String CAProposal_ParameterDeclaration;
	public static String CAProposal_ParameterDeclarationIn;
	public static String CAProposal_PredefinedDataType;
	public static String CAProposal_PresentationAttributesFields;
	public static String CAProposal_Primitive;
	public static String CAProposal_PrimitiveType;
	public static String CAProposal_PrintFormState;
	public static String CAProposal_ProgramType;
	public static String CAProposal_PromptFields;
	public static String CAProposal_PropertyName;
	public static String CAProposal_PropertyNameDataTables;
	public static String CAProposal_PropertyNameFormGroups;
	public static String CAProposal_PropertyValue;
	public static String CAProposal_ProtectionState;
	public static String CAProposal_RecordArrayState;
	public static String CAProposal_RecordItemState;
	public static String CAProposal_RecordState;
	public static String CAProposal_RecordType;
	public static String CAProposal_ReferenceToDynamicArray;
	public static String CAProposal_ReferenceToRecord;
	public static String CAProposal_ReportDataFields;
	public static String CAProposal_ReportFields;
	public static String CAProposal_ReportLibrary;
	public static String CAProposal_ServiceFunction;
	public static String CAProposal_ServiceVariable;
	public static String CAProposal_ServiceReferenceDeclarationIn;
	public static String CAProposal_SetState;
	public static String CAProposal_SQLItemState;
	public static String CAProposal_SQLLibrary;
	public static String CAProposal_StringConstant;
	public static String CAProposal_StringLibrary;
	public static String CAProposal_StringOperator;
	public static String CAProposal_SystemLibrary;
	public static String CAProposal_SystemVarLibrary;
	public static String CAProposal_SystemValue;
	public static String CAProposal_SystemVariable;
	public static String CAProposal_TableType;
	public static String CAProposal_TextFieldState;
	public static String CAProposal_TextFormState;
	public static String CAProposal_TimeLibrary;
	public static String CAProposal_TimeStampLibrary;
	public static String CAProposal_TypeSpecification;
	public static String CAProposal_UseDeclarationIn;
	public static String CAProposal_Variable;
	public static String CAProposal_VariableDeclaration;
	public static String CAProposal_VariableDeclarationIn;
	public static String CAProposal_VariableSystemWord;
	public static String CAProposal_WebItemState;
	public static String CAProposal_WindowFields;

	public static String CAProposal_ConverseLibrary;
	public static String CAProposal_ConverseVarLibrary;
	public static String CAProposal_DataTimeLibrary;
	public static String CAProposal_J2EELibrary;
	public static String CAProposal_JavaLibrary;
	public static String CAProposal_VGLibrary;
	public static String CAProposal_VGVarLibrary;
	public static String CAProposal_DLILibrary;
	public static String CAProposal_DLIVarLibrary;
	public static String CAProposal_ServiceLibrary;
	public static String CAProposal_HttpLibrary;
	public static String CAProposal_RuiLibrary;
	public static String CAProposal_JsonLibrary;
	public static String CAProposal_XmlLibrary;
	
	public static String Validate_label;
	public static String Revalidate_label;
	public static String Valid_label;
	
	public static String MoveDialog_validateMsg_NoSelection;
	public static String MoveDialog_validateMsg_MultiDestination;
	public static String MoveDialog_validateMsg_SameParent;

	public static String OptionalMessageDialog_dontShowAgain;
	
	// Linked mode
	public static String ProjectSelectionDialog_title;
	public static String ProjectSelectionDialog_desciption;
	public static String ProjectSelectionDialog_filter;
	
	public static String OrganizeUsedFormsAction_op_description;
	public static String OrganizeAction_error_parse;
	public static String OrganizeAction_error_unexpected;
	public static String OrganizeUsedFormsAction_status_title;
	public static String OrganizeUsedFormsAction_status_description;
	public static String OrganizeUsedFormsAction_status_noUseFG;
	public static String OrganizeAction_error_syntax;
	public static String OrganizeUsedFormsAction_error_multiformgroup;
	public static String OrganizeUsedFormsAction_error_resolution;
	public static String OrganizeForms;

	public static String OrganizeImportsAction_op_description;
	public static String OrganizeImportsAction_status_title;
	public static String OrganizeImportsAction_status_description;
	
	public static String OrganizeImportsAction_selectiondialog_title;
	public static String OrganizeImportsAction_selectiondialog_message;
	public static String MultiElementListSelectionDialog_pageInfoMessage;
	
	public static String OrganizeImportsAction_summary;
	public static String OrganizeImportsAction_multi_error_unresolvable;
	public static String OrganizeImportsAction_multi_error_notoncp;	
	public static String OrganizeImportsOperation_description;
	public static String AddImportsOperation_description;
	public static String AddImportsOperation_error_notresolved_message;
	public static String AddImportsOperation_error_importclash;
	public static String FoldingMenuName;
	
	public static String CodeBehindPreferencePage_GroupBoxPageHandlerTitle;
	public static String CodeBehindPreferencePage_PackagePrefix;
	public static String CodeBehindPreferencePage_SyncFacesDeletes;
	public static String CodeBehindPreferencePage_SyncPageHandlerDeletes;
	public static String CodeBehindPreferencePage_CleanupGeneratedArtifacts;
	public static String CodeBehindPreferencePage_GroupBoxBindingsTitle;
	public static String CodeBehindPreferencePage_GenerateEGLBindings;
	public static String CodeBehindPreferencePage_LoadbundleVariable;
	
	// Page Designer Loadbundle variable preference messages
	public static String CodeBehindPreferencePage_LoadbundleVarNullName;
	public static String CodeBehindPreferencePage_LoadbundleVarEmptyName;
	public static String CodeBehindPreferencePage_LoadbundleVarDotName;
	public static String CodeBehindPreferencePage_LoadbundleVarWithBlanks;
	public static String CodeBehindPreferencePage_LoadbundleVarConsecutiveDotsName;
	public static String CodeBehindPreferencePage_LoadbundleVarUppercaseName;
	
	// Page Designer Preference to alias names in JSF handler pages
	public static String CodeBehindPreferencePage_AliasJSFNames;
	public static String CodeBehindPreferencePage_AliasJSFNames_dialog_title;
	public static String CodeBehindPreferencePage_AliasJSFNames_dialog_message;
	public static String CodeBehindPreferencePage_AliasJSFNames_monitor_string;
	
	// Migrator to handle alias/unalias names in JSPs
	public static String MigrationOperationJobName;
	public static String SubTaskProgresMonitorHeader;
	
	//EGL Bidi Preferences
	public static String EGL_Preferences_Configure_Bidi;
	public static String EGL_Preferences_Bidi_Enabled;
	public static String EGL_Preferences_Group_Source_Assistant;
	public static String EGL_Preferences_Bidi_Visual_Source_Assistant;
	public static String EGL_Preferences_Bidi_RTL_Source_Assistant;
	public static String EGL_Preferences_Bidi_Visual_Editing;
	
	public static String EGL_ValidateSQLJob;
	public static String EGL_ValidateDefaultSelectJob;
	
	// Web Project Wizard
	public static String WebProjectWizard_error_target_runtime_is_unsupported;
	
	// project generation settings
	public static String genSettingsGroupLabel;
	public static String genDirSelectionTitle;
	public static String genDirSelectionMsg;
	public static String genInsideWorkbench;
	public static String genOutsideWorkbench;
	public static String genDefaultGenDir;
	public static String genSettingsSaveError;
	public static String genSettingsValidationBlank;
	public static String genSettingsValidationInvalid;
	public static String genSettingsValidationProject;
	public static String genSettingsValidationDefaultBlank;
	public static String genSettingsValidationDefaultRetlative;
	public static String genSettingsValidationDefaultInvalid;
	public static String genArguments;
	public static String genContributorsLabel;
	public static String genContributorId;
	public static String genContributorClass;
	public static String genContributorProvider;
	public static String genContributorRequires;
}
