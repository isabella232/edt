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
package org.eclipse.edt.compiler.internal.core.lookup.System;

/**
 * @author Harmon
 */
public interface ISystemLibrary {

	// ///////
	// Special Function integer constants: Math Functions
	// ///////
	public static final int Abs_func = 1;
	public static final int Acos_func = 2;
	public static final int Asin_func = 4;
	public static final int Atan_func = 5;
	public static final int Atan2_func = 6;
	public static final int Ceiling_func = 7;
	public static final int CompareNum_func = 8;
	public static final int Cos_func = 9;
	public static final int Cosh_func = 10;
	public static final int Exp_func = 11;
	public static final int FloatingDifference_func = 13;
	public static final int FloatingMod_func = 14;
	public static final int FloatingProduct_func = 15;
	public static final int FloatingQuotient_func = 16;
	public static final int FloatingSum_func = 17;
	public static final int Floor_func = 18;
	public static final int Frexp_func = 19;
	public static final int Ldexp_func = 20;
	public static final int Log_func = 21;
	public static final int Log10_func = 22;
	public static final int Maximum_func = 23;
	public static final int Minimum_func = 24;
	public static final int Modf_func = 25;
	public static final int Pow_func = 26;
	public static final int Precision_func = 27;
	public static final int Round_func = 28;
	public static final int Sin_func = 29;
	public static final int Sinh_func = 30;
	public static final int Sqrt_func = 31;
	public static final int Tan_func = 32;
	public static final int Tanh_func = 33;
	public static final int Decimals_func = 37;
	public static final int Assign_func = 38;
	public static final int StringAsFloat_func = 39;
	public static final int StringAsInt_func = 40;
	public static final int StringAsDecimal_func = 41;

	public static final int math_functions_upper_bounds = 45;

	// ///////
	// Special Function integer constants: String Functions
	// ///////
	public static final int CompareStr_func = 50;
	public static final int Concatenate_func = 51;
	public static final int ConcatenateWithSeparator_func = 52;
	public static final int CopyStr_func = 53;
	public static final int FindStr_func = 54;
	public static final int GetNextToken_func = 55;
	public static final int SetBlankTerminator_func = 56;
	public static final int SetNullTerminator_func = 57;
	public static final int SetSubStr_func = 58;
	public static final int ByteLen_func = 59;
	public static final int CharacterLen_func = 60;
	public static final int GetTokenCount_func = 61;
	public static final int IndexOf_func = 62;
	public static final int string_functions_upper_bounds = 70;

	// ///////
	// Special Function integer constants: Special Functions
	// ///////
	public static final int ExitProgram_func = 72;
	public static final int Commit_func = 73;
	public static final int Connect_func = 74;
	public static final int ConnectionService_func = 75;
	public static final int VerifyChkDigitMod10_func = 77;
	public static final int VerifyChkDigitMod11_func = 78;
	public static final int ExitStack_func = 81;
	public static final int CalculateChkDigitMod10_func = 82;
	public static final int Purge_func = 95;
	public static final int Rollback_func = 96;
	public static final int Return_func = 97;
	public static final int Dimension_func = 98;

	public static final int SetError_func = 100;
	public static final int SetLocale_func = 101;
	public static final int Wait_func = 102;
	public static final int Audit_func = 103;
	public static final int StartTransaction_func = 104;
	public static final int CallChartUtility_func = 105;
	public static final int SetRemoteUser_func = 106;
	public static final int ClearScreen_func = 107;
	public static final int DisplayMsgNum_func = 108;
	public static final int FieldInputLength_func = 109;
	public static final int ValidationFailed_func = 110;
	public static final int SetRequestAttr_func = 111;
	public static final int GetRequestAttr_func = 112;
	public static final int ClearRequestAttr_func = 113;
	public static final int SetSessionAttr_func = 114;
	public static final int GetSessionAttr_func = 115;
	public static final int ClearSessionAttr_func = 116;
	public static final int Disconnect_func = 117;
	public static final int DisconnectAll_func = 118;
	public static final int QueryCurrentDatabase_func = 119;
	public static final int SetCurrentDatabase_func = 120;
	public static final int CalculateChkDigitMod11_func = 121; // used to be 82
	public static final int PageEject_func = 122;
	public static final int MaximumSize_func = 124;
	public static final int BeginDatabaseTransaction_func = 125;
	public static final int DefineDatabaseAlias_func = 126;
	public static final int GetVAGSysType_func = 127;
	public static final int GetMessage_func = 128;
	public static final int ConditionAsInt_func = 129;
	public static final int VGTDLI_func = 130;
	public static final int ConvertBIDI_func = 131;
	public static final int BooleanAsString_func = 132;
	public static final int ClearEGLSessionAttrs_func = 133;
	public static final int GetApplicationAttr_func = 134;
	public static final int SetApplicationAttr_func = 135;
	public static final int StartProgram_func = 136;
	public static final int Convert_func = 137;
	public static final int Size_func = 138;
	public static final int Bytes_func = 139;
	public static final int ClearApplicationAttr_func = 140;
	public static final int ConstructQuery_func = 141;
	public static final int SetErrorForComponentId_func = 142;
	public static final int GetQueryParameter_func = 143;
	public static final int ConvertFromJSON_func = 144;
	public static final int ConvertFromXML_func = 145;
	public static final int ConvertToXML_func = 146;
	public static final int ConvertToJSON_func = 147;
	public static final int ConvertFromURLEncoded_func = 148;
	public static final int ConvertToURLEncoded_func = 149;

	// ///////
	// Special Function integer constants: Special Java Functions
	//
	// This category was broken out of special functions on 6/10/03
	// to support the FunctionImplementation.isJavaFunction() method.
	//
	// ///////
	public static final int Invoke_func = 150;
	public static final int Store_func = 151;
	public static final int StoreNew_func = 152;
	public static final int GetField_func = 153;
	public static final int SetField_func = 154;
	public static final int StoreField_func = 155;
	public static final int QualifiedTypeName_func = 156;
	public static final int StoreCopy_func = 157;
	public static final int Remove_func = 158;
	public static final int JavaRemoveAll_func = 159;
	public static final int IsNull_func = 160;
	public static final int IsObjID_func = 161;
	public static final int java_functions_upper_bound_func = 169;

	// Dyanmic Array Functions
	public static final int AppendElement_func = 170;
	public static final int AppendAll_func = 171;
	public static final int InsertElement_func = 172;
	public static final int RemoveElement_func = 173;
	public static final int RemoveAll_func = 174;
	public static final int Resize_func = 175;
	public static final int ResizeAll_func = 176;
	public static final int SetMaxSize_func = 177;
	public static final int SetMaxSizes_func = 178;
	public static final int GetSize_func = 179;
	public static final int GetMaxSize_func = 180;
	public static final int SetElementsEmpty_func = 181;
	public static final int array_functions_upper_bounds = 185;

	public static final int ConvertNumberToUnsignedUnicodeNum_func = 192;
	public static final int ConvertUnsignedUnicodeNumToNumber_func = 193;
	public static final int ConvertNumberToUnicodeNum_func = 194;
	public static final int ConvertUnicodeNumToNumber_func = 195;
	public static final int IntAsUnicode_func = 196;
	public static final int UnicodeAsInt_func = 197;
	public static final int WriteStdout_func = 198;
	public static final int WriteStderr_func = 199;
	public static final int Clip_func = 200;
	public static final int Column_func = 201;
	public static final int Format_func = 202;
	public static final int FormatNumber_func = 203;
	public static final int IntegerAsString_func = 204;
	public static final int LowerCase_func = 205;
	public static final int Spaces_func = 206;
	public static final int UpperCase_func = 207;
	public static final int CallCmd_func = 208;
	public static final int CharacterAsInt_func = 209;
	public static final int GetCmdLineArgCount_func = 210;
	public static final int GetCmdLineArg_func = 211;
	public static final int StartCmd_func = 212;
	public static final int LoadTable_func = 213;
	public static final int UnloadTable_func = 214;
	public static final int SetCurrentWindow_func = 215;
	public static final int GetProperty_func = 216;

	// Date/Time Functions (6.0)
	public static final int FormatDate_func = 218;
	public static final int FormatTime_func = 219;
	public static final int FormatTimeStamp_func = 220;
	public static final int date_time_format_upper_bounds = 220;

	public static final int CurrentGregorianDate_var = 221;
	public static final int CurrentFormattedGregorianDate_var = 222;
	public static final int CurrentJulianDate_var = 223;
	public static final int CurrentFormattedJulianDate_var = 224;
	public static final int CurrentShortGregorianDate_var = 225;
	public static final int CurrentShortJulianDate_var = 226;
	public static final int CurrentTime_func = 227;
	public static final int CurrentFormattedTime_var = 228;
	public static final int CurrentDate_func = 229;
	public static final int DateValue_func = 230;
	public static final int DateValueFromGregorian_func = 231;
	public static final int DateValueFromJulian_func = 232;
	public static final int TimeValue_func = 233;
	public static final int IntervalValue_func = 234;
	public static final int IntervalValueWithPattern_func = 235;
	public static final int TimeStampValue_func = 236;
	public static final int TimeStampValueWithPattern_func = 237;
	public static final int TimeStampFrom_func = 238;
	public static final int DayOf_func = 239;
	public static final int YearOf_func = 240;
	public static final int MonthOf_func = 241;
	public static final int WeekdayOf_func = 242;
	public static final int Mdy_func = 243;
	public static final int DateOf_func = 244;
	public static final int TimeOf_func = 245;
	public static final int Extend_func = 246;
	public static final int DateFromInt_func = 247;

	// Dictionary functions (6.0.0.1)
	public static final int ContainsKey_func = 250;
	public static final int GetKeys_func = 251;
	public static final int GetValues_func = 252;
	public static final int InsertAll_func = 253;
	public static final int Dictionary_RemoveElement_func = 254;
	public static final int Dictionary_RemoveAll_func = 255;
	public static final int Dictionary_Size_func = 256;
	public static final int dictionary_functions_upper_bounds = 259;

	// Blob/Clob functions (6.0.0.1)
	public static final int AttachBlobToFile_func = 260;
	public static final int GetBlobLen_func = 261;
	public static final int TruncateBlob_func = 262;
	public static final int LoadBlobFromFile_func = 263;
	public static final int UpdateBlobToFile_func = 264;
	public static final int FreeBlob_func = 265;
	public static final int AttachClobToFile_func = 266;
	public static final int GetClobLen_func = 267;
	public static final int GetSubStrFromClob_func = 268;
	public static final int GetStrFromClob_func = 269;
	public static final int SetClobFromStringAtPosition_func = 270;
	public static final int SetClobFromString_func = 271;
	public static final int TruncateClob_func = 272;
	public static final int LoadClobFromFile_func = 273;
	public static final int UpdateClobToFile_func = 274;
	public static final int FreeClob_func = 275;
	public static final int AttachBlobToTempFile_func = 276;
	public static final int AttachClobToTempFile_func = 277;
	public static final int large_object_functions_upper_bounds = 280;

	// ReportHandler functions
	public static final int GetReportParameter_func = 300;
	public static final int SetReportVariableValue_func = 301;
	public static final int GetReportVariableValue_func = 302;
	public static final int GetFieldValue_func = 303;
	public static final int AddReportData_func = 304;
	public static final int GetReportData_func = 305;
	public static final int SetFieldValue_func = 306;
	public static final int report_handler_functions_upper_bounds = 310;

	public static final int GetDataColumnBinding_func = 330;

	// Console UI Functions 6.0.0.1
	public static final int DisplayLineMode_func = 353;
	public static final int PromptLineMode_func = 354;
	public static final int ActivateWindow_func = 355;
	public static final int ActivateWindowByName_func = 356;
	public static final int ClearActiveWindow_func = 357;
	public static final int ClearWindow_func = 358;
	public static final int ClearWindowByName_func = 359;
	public static final int CloseActiveWindow_func = 360;
	public static final int CloseWindow_func = 361;
	public static final int CloseWindowByName_func = 362;
	public static final int DrawBox_func = 363;
	public static final int DrawBoxWithColor_func = 364;
	public static final int DisplayAtLine_func = 365;
	public static final int DisplayAtPosition_func = 366;
	public static final int DisplayMessage_func = 367;
	public static final int DisplayError_func = 368;
	public static final int HideErrorWindow_func = 369;
	public static final int OpenWindow_func = 370;
	public static final int OpenWindowByName_func = 371;
	public static final int ClearActiveForm_func = 372;
	public static final int ClearForm_func = 373;
	public static final int DisplayForm_func = 375;
	public static final int DisplayFormByName_func = 376;
	public static final int DisplayFields_func = 377;
	public static final int GotoField_func = 378;
	public static final int GotoFieldByName_func = 379;
	public static final int NextField_func = 380;
	public static final int PreviousField_func = 381;
	public static final int IsCurrentField_func = 382;
	public static final int IsCurrentFieldByName_func = 383;
	public static final int IsFieldModified_func = 384;
	public static final int IsFieldModifiedByName_func = 385;
	public static final int GetKey_func = 386;
	public static final int GetKeyCode_func = 387;
	public static final int LastKeyTyped_func = 388;
	public static final int CurrentArrayScreenLine_func = 389;
	public static final int CurrentArrayDataLine_func = 390;
	public static final int ScrollDownPage_func = 391;
	public static final int ScrollDownLines_func = 392;
	public static final int ScrollUpPage_func = 393;
	public static final int ScrollUpLines_func = 394;
	public static final int SetArrayLine_func = 395;
	public static final int ShowHelp_func = 396;
	public static final int GetFieldBuf_func = 397;
	public static final int OpenWindowWithForm_func = 398;
	public static final int OpenWindowWithFormByName_func = 399;
	public static final int ClearFields_func = 400;
	public static final int ClearFieldsByName_func = 401;
	public static final int DisplayFieldsByName_func = 402;
	public static final int GetKeyName_func = 403;
	public static final int CancelArrayDelete_func = 404;
	public static final int CancelArrayInsert_func = 405;
	public static final int SetCurrentArrayCount_func = 406;
	public static final int GotoMenuItem_func = 407;
	public static final int GotoMenuItemByName_func = 408;
	public static final int HideMenuItem_func = 409;
	public static final int HideMenuItemByName_func = 410;
	public static final int ShowMenuItem_func = 411;
	public static final int ShowMenuItemByName_func = 412;
	public static final int CurrentArrayCount_func = 415;
	public static final int ShowAllMenuItems_func = 416;
	public static final int HideAllMenuItems_func = 417;
	public static final int UpdateWindowAttributes_func = 418;
	public static final int RegisterConsoleForm_func = 419;
	public static final int OpenFileDialog_func = 420;
	public static final int OpenDirectoryDialog_func = 421;
	public static final int console_functions_upper_bounds = 429;

	// ReportLibrary functions
	public static final int FillReport_func = 430;
	public static final int ExportReport_func = 431;
	public static final int AddReportParameter_func = 432;
	public static final int ResetReportParameters_func = 433;
	public static final int reportLibrary_functions_upper_bounds = 439;

	// Log functions
	public static final int StartLog_func = 440;
	public static final int ErrorLog_func = 441;

	// Service functions
	/*
	 * These functions are now in ServiceLib but the constants are defined in
	 * Syslib public static final int ConvertFromJSON_func = 144; public static
	 * final int ConvertToJSON_func = 147; public static final int
	 * ConvertFromURLEncoded_func = 148; public static final int
	 * ConvertToURLEncoded_func = 149;
	 */
	public static final int SetWebServiceLocation_func = 450;
	public static final int GetWebServiceLocation_func = 451;
	public static final int SetTCPIPLocation_func = 452;
	public static final int GetTCPIPLocation_func = 453;
	public static final int BindService_func = 454;
	public static final int DoInvoke_func = 455;
	public static final int DoInvokeAsync_func = 456;
	public static final int SetHTTPBasicAuthentication_func = 460;
	public static final int SetRestRequestHeaders = 461;
	public static final int GetRestRequestHeaders = 462;
	public static final int SetRestServiceLocation_func = 463;
	public static final int GetRestServiceLocation_func = 464;
	public static final int GetCurrentCallbackResponse = 457;
	public static final int GetOriginalRequest = 458;
	public static final int EndStatefulServiceSession_func = 459;
	public static final int SetProxyBasicAuthentication_func = 465;
	public static final int serviceExceptionHandler_var = 466;
	public static final int SetSOAPRequestHeaders = 467;
	public static final int GetSOAPRequestHeaders = 468;
	public static final int SetCCSID_func = 469;

	// J2EELib Security Functions
	public static final int GetRemoteUser_func = 470;
	public static final int IsUserInRole_func = 471;
	public static final int GetAuthenticationType_func = 472;
	public static final int getContext_var = 473;

	// PortalLib functions
	public static final int GetPortletSessionAttr_func = 510;
	public static final int SetPortletSessionAttr_func = 511;
	public static final int SetPortletMode_func = 512;
	public static final int setWindowState_func = 513;
	public static final int isPreferenceReadOnly_func = 514;
	public static final int getPreferenceValue_func = 515;
	public static final int getPreferenceValues_func = 516;
	public static final int resetPreference_func = 517;
	public static final int setPreferenceValue_func = 518;
	public static final int setPreferenceValues_func = 519;
	public static final int savePreferences_func = 520;
	public static final int getWindowState_func = 521;
	public static final int getPortletMode_func = 522;
	public static final int createVaultSlot_func = 523;
	public static final int getCredential_func = 524;
	public static final int deleteVaultSlot_func = 525;
	public static final int setCredential_func = 526;
	public static final int clearPortletSessionAttr_func = 527;

	public static final int ConvertEncodedTextToString_func = 530;
	public static final int ConvertStringToEncodedText_func = 531;

	// XMLLib Functions
	/*
	 * These functions are now in XMLLib but the constants are defined in Syslib
	 * public static final int ConvertFromXML_func = 145; public static final
	 * int ConvertToXML_func = 146;
	 */
	public static final int NamespaceAware_func = 540;
	public static final int PrettyPrint_func = 541;
	public static final int SetXMLRootElement_func = 542;

	// //////////////
	// System Variable Words
	// /////////////

	public static final int TransferName_var = 715;
	public static final int CommitOnConverse_var = 716;
	public static final int ConversionTable_var = 717;
	public static final int HandleHardIOErrors_var = 718;
	public static final int RemoteSystemID_var = 719;
	public static final int SessionID_var = 720;
	public static final int TerminalID_var = 721;
	public static final int TextMessageNumber_var = 722;
	public static final int TextMessage_var = 723;
	public static final int HandleOverflow_var = 724;
	public static final int OverflowIndicator_var = 725;
	public static final int ReturnCode_var = 726;
	public static final int HandleCallErrors_var = 727;
	public static final int MQConditionCode_var = 728;
	public static final int ErrorCode_var = 729;
	public static final int SimulateSegmentation_var = 730;
	public static final int TransactionID_var = 731;
	public static final int SqlCode_var = 732;
	public static final int SqlCa_var = 733;
	public static final int SqlErrd_var = 734;
	public static final int SqlErrmc_var = 735;
	public static final int SqlWarn_var = 736;
	public static final int SystemType_var = 738;
	public static final int ConnectionID_var = 739;
	public static final int UserID_var = 740;
	public static final int ArrayIndex_var = 741;
	public static final int EventKey_var = 742;
	public static final int Destination_var = 743;
	public static final int PrinterAssociation_var = 744;
	public static final int ExceptionCode_var = 745;
	public static final int ExceptionMsgCount_var = 746;
	public static final int ExceptionMsg_var = 747;
	public static final int SqlState_var = 748;
	public static final int HandleSysLibraryErrors_var = 749;
	public static final int ValidationMsgNum_var = 750;
	public static final int SegmentedMode_var = 751;
	public static final int ResourceAssociation_var = 752;
	public static final int SQLLib_currentSchema_var = 753;
	public static final int SqlIsolationLevel_var = 754;
	public static final int ConversationID_var = 755;
	public static final int SQLLib_sqlData_var = 756;
	public static final int SysVar_sqlData_var = 757;

	public static final int CurrentForm_var = 758;
	public static final int setCursorPosition_func = 759;
	public static final int getCursorLine_func = 760;
	public static final int getCursorColumn_func = 761;
	public static final int CurrentTimeStamp_func = 762;
	public static final int CallConversionTable_var = 763; // Jeff 12-17
	public static final int FormConversionTable_var = 764; // Jeff 12-17

	public static final int NULLFILL_var = 769; 
	public static final int ISODATEFORMAT_var = 770; // Jeff 12-17
	public static final int USADATEFORMAT_var = 771; // Jeff 12-17
	public static final int EURDATEFORMAT_var = 772; // Jeff 12-17
	public static final int JISDATEFORMAT_var = 773; // Jeff 12-17
	public static final int ISOTIMEFORMAT_var = 774; // Jeff 12-17
	public static final int USATIMEFORMAT_var = 775; // Jeff 12-17
	public static final int EURTIMEFORMAT_var = 776; // Jeff 12-17
	public static final int JISTIMEFORMAT_var = 777; // Jeff 12-17
	public static final int DB2TIMESTAMPFORMAT_var = 778; // Jeff 12-17
	public static final int ODBCTIMESTAMPFORMAT_var = 779; // Jeff 12-17
	public static final int LocaleDateFormat_var = 780;
	public static final int LocaleTimeFormat_var = 781;
	public static final int LocaleTimeStampFormat_var = 782;
	public static final int DefaultDateFormat_var = 783;
	public static final int DefaultTimeFormat_var = 784;
	public static final int DefaultTimeStampFormat_var = 785;
	public static final int DefaultMoneyFormat_var = 786;
	public static final int DefaultNumericFormat_var = 787;
	public static final int SystemGregorianDateFormat_var = 788;
	public static final int SystemJulianDateFormat_var = 789;

	public static final int CurrentException_var = 790;

	public static final int ArrayIndexOutOfBounds_var = 791;
	public static final int IncompatibleTypeException_var = 792;
	public static final int InvalidReference_var = 793;
	public static final int NoFieldFoundException_var = 794;
	public static final int InvocationException_var = 795;
	public static final int ServiceException_var = 796;
	public static final int FileIOException_var = 797;
	public static final int FileNotFoundException_var = 798;
	public static final int MQIOException_var = 799;
	public static final int SQLIOException_var = 800;
	public static final int LOBException_var = 801;

	public static final int NoOutline_var = 802;
	public static final int Box_var = 803;

	public static final int Key_Accept_var = 825;
	public static final int Key_DeleteLine_var = 826;
	public static final int Key_InsertLine_var = 827;
	public static final int Key_Pagedown_var = 828;
	public static final int Key_Pageup_var = 829;
	public static final int Key_Help_var = 830;
	public static final int Key_Interrupt_var = 831;
	public static final int Key_Quit_var = 832;
	public static final int ActiveForm_var = 833;
	public static final int ActiveWindow_var = 834;
	public static final int ErrorWindowVisible_var = 835;
	public static final int ErrorWindow_var = 836;
	public static final int ErrorLine_var = 837;
	public static final int Screen_var = 838;
	public static final int InterruptRequested_var = 839;
	public static final int QuitRequested_var = 840;
	public static final int MessageResource_var = 841;
	public static final int CursorWrap_var = 842;
	public static final int DefinedFieldOrder_var = 843;
	public static final int DeferInterrupt_var = 844;
	public static final int DeferQuit_var = 845;
	public static final int SQLInterrupt_var = 846;
	public static final int DefaultDisplayAttributes_var = 847;
	public static final int DefaultInputAttributes_var = 848;
	public static final int CurrentDisplayAttributes_var = 849;
	public static final int CommentLine_var = 850;
	public static final int FormLine_var = 851;
	public static final int MenuLine_var = 852;
	public static final int MessageLine_var = 853;
	public static final int PromptLine_var = 854;
	public static final int CurrentRowAttributes_var = 855;

	public static final int dlivar_variables_lower_bounds = 856;
	public static final int DBName_var = 856;
	public static final int SegmentLevel_var = 857;
	public static final int StatusCode_var = 858;
	public static final int ProcOptions_var = 859;
	public static final int SegmentName_var = 860;
	public static final int KeyAreaLen_var = 861;
	public static final int KeyArea_var = 862;
	public static final int NumSensitiveSegs_var = 863;
	public static final int CICSError_var = 864;
	public static final int CICSCondition_var = 865;
	public static final int CICSRestart_var = 866;
	public static final int HandleHardDLIErrors_var = 867;
	public static final int dlivar_variables_upper_bounds = 867;

	public static final int CopyBytes_func = 873;
	public static final int CompareBytes_func = 874;
	public static final int ConcatenateBytes_func = 875;

	public static final int KeyAreaHex_var = 878;

	// System Enumeration Constants
	public static final int ENUM_AlignKind = 100;
	public static final int ENUM_CaseFormatKind = 101;
	public static final int ENUM_ColorKind = 102;
	public static final int ENUM_HighlighKind = 103;
	public static final int ENUM_IntensityKind = 104;
	public static final int ENUM_LineWrapKind = 105;
	public static final int ENUM_DataSource = 106;
	public static final int ENUM_ExportFormat = 107;
	public static final int ENUM_EventKind = 108;
	public static final int ENUM_WindowAttributeKind = 109;
	public static final int ENUM_OutlineKind = 110;
	public static final int ENUM_ConsoleEventKind = 111;

	public static final int ENUM_CallingConventionKind = 115;
	public static final int ENUM_ConvertDirection = 116;
	public static final int ENUM_OrderingKind = 117;
	public static final int ENUM_ServiceKind = 118;
	public static final int ENUM_WhitespaceKind = 119;
	public static final int ENUM_DLICallInterfaceKind = 120;
	public static final int ENUM_PCBKind = 121;
	public static final int ENUM_CSVStyle = 122;
	public static final int ENUM_DisconnectKind = 123;
	public static final int ENUM_IsolationLevelKind = 124;
	public static final int ENUM_CommitControlKind = 125;
	public static final int ENUM_SessionScopeKind = 126;
	public static final int ENUM_PortletModeKind = 127;
	public static final int ENUM_WindowStateKind = 128;
	public static final int ENUM_SecretKind = 129;
	public static final int ENUM_RowTypeKind = 130;
	public static final int ENUM_EventTypeKind = 131;
	public static final int ENUM_DisplayUseKind = 132;
	public static final int ENUM_ScopeKind = 133;
	public static final int ENUM_SelectTypeKind = 134;
	public static final int ENUM_DeviceTypeKind = 135;
	public static final int ENUM_IndexOrientationKind = 136;
	public static final int ENUM_RESTFormatKind = 137;

	public static final int ENUM_ProtectKind = 138;
	public static final int ENUM_UITypeKind = 139;
	public static final int ENUM_PFKeyKind = 140;
	public static final int ENUM_SignKind = 141;
	public static final int ENUM_OrientationKind = 142;
	public static final int ENUM_BidiTypeKind = 143;
	public static final int ENUM_XMLFormKind = 144;
	public static final int ENUM_XMLStructureKind = 145;
	public static final int ENUM_ModifierKind = 146;
	public static final int ENUM_EncodingKind = 147;

}
