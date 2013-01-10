/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.eclipse.edt.compiler.internal.enumerations.EGLCommTypeKindEnumeration;
import org.eclipse.edt.compiler.internal.enumerations.EGLUITypeKindEnumeration;


/**
 * @author dollar
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class EGLPropertiesHandler {

        // ***IF YOU MODIFY THIS LIST YOU NEED TO REGEN THE CONSTANTS IN IEGLConstants USING***
        // org.eclipse.edt.compiler.internal.dev.tools.EGLPropertiesTool

        private static String[] propertyNames =
                {
        				 "action",		             	  //$NON-NLS-1$
                         "addSpaceForSOSI",               //$NON-NLS-1$
                         "alias",                         //$NON-NLS-1$
                         "align",                         //$NON-NLS-1$
                         "allowAppend",					  //$NON-NLS-1$
                         "allowDelete",			          //$NON-NLS-1$
                         "allowInsert",					  //$NON-NLS-1$
                         "allowUnqualifiedItemReferences",//$NON-NLS-1$
						 "base",						  //$NON-NLS-1$
						 "binding",						  //$NON-NLS-1$
						 "bindingByName",				  //$NON-NLS-1$
                         "bottomMargin",                  //$NON-NLS-1$
                         "bypassValidation",              //$NON-NLS-1$
						 "callingConvention",			  //$NON-NLS-1$
						 "callInterface",    			  //$NON-NLS-1$						 
						 "cancelOnPageTransition",		  //$NON-NLS-1$	
						 "caseSensitive",                 //$NON-NLS-1$
                         "color",                         //$NON-NLS-1$
                         "column",                        //$NON-NLS-1$
                         "columns",                       //$NON-NLS-1$
                         "commandValueItem",              //$NON-NLS-1$
                         "commType",                      //$NON-NLS-1$
                         "connectionName",		          //$NON-NLS-1$
                         "containerContextDependent",     //$NON-NLS-1$
                         "contents",                      //$NON-NLS-1$
                         "currency",                      //$NON-NLS-1$
                         "currencySymbol",                //$NON-NLS-1$
                         "currentArrayCount",	          //$NON-NLS-1$
                         "cursor",                        //$NON-NLS-1$
                         "data",   		        	      //$NON-NLS-1$
                         "dataType", 	        	      //$NON-NLS-1$
                         "dateFormat",                    //$NON-NLS-1$
						 "debugImpl",                     //$NON-NLS-1$
						 "defaultPSBName",                //$NON-NLS-1$
                         "defaultSelectCondition",        //$NON-NLS-1$
                         "deleteAfterUse",                //$NON-NLS-1$
                         "delimiters",	                  //$NON-NLS-1$
                         "detectable",                    //$NON-NLS-1$
                         "deviceType",                    //$NON-NLS-1$
                         "displayName",                   //$NON-NLS-1$
                         "displayOnly",                   //$NON-NLS-1$
                         "displayUse",                    //$NON-NLS-1$
                         "dli",             			  //$NON-NLS-1$
						 "dliFieldName",				  //$NON-NLS-1$
						 "dllName",						  //$NON-NLS-1$	
						 "eglBinding",                    //$NON-NLS-1$
						 "elementName",                   //$NON-NLS-1$
						 "endpoint",                      //$NON-NLS-1$						 
						 "enumeration",                   //$NON-NLS-1$						 
						 "eventValueItem",                //$NON-NLS-1$
                         "fieldLen",                      //$NON-NLS-1$
                         "fileName",                      //$NON-NLS-1$
                         "fill",                          //$NON-NLS-1$
                         "fillCharacter",                 //$NON-NLS-1$
						 "formSize",                      //$NON-NLS-1$
						 "getInParent",                   //$NON-NLS-1$
                         "getOptionsRecord",              //$NON-NLS-1$
						 "handleHardDLIErrors",			  //$NON-NLS-1$
						 "handleHardIOErrors",            //$NON-NLS-1$
                         "hasComment",                    //$NON-NLS-1$
                         "help",                          //$NON-NLS-1$
                         "helpForm",                      //$NON-NLS-1$
                         "helpGroup",                     //$NON-NLS-1$
                         "helpKey",                       //$NON-NLS-1$
                         "helpMsgKey",                    //$NON-NLS-1$
                         "hierarchy",	                  //$NON-NLS-1$
                         "highlight",                     //$NON-NLS-1$
                         "hostVarQualifier",              //$NON-NLS-1$
                         "includeMsgInTransaction",       //$NON-NLS-1$
                         "includeReferencedFunctions",    //$NON-NLS-1$
                         "indexOrientation",              //$NON-NLS-1$
                         "initialized",                   //$NON-NLS-1$
                         "inputForm",                     //$NON-NLS-1$
                         "inputUIRecord",                 //$NON-NLS-1$
                         "inputRecord",                   //$NON-NLS-1$
                         "inputRequired",                 //$NON-NLS-1$
                         "inputRequiredMsgKey",           //$NON-NLS-1$
                         "intensity",                     //$NON-NLS-1$
						 "isBoolean",                     //$NON-NLS-1$
                         "isConstruct",		              //$NON-NLS-1$
                         "isDecimalDigit",                //$NON-NLS-1$
                         "isHexDigit",                    //$NON-NLS-1$
                         "isLastParamReturnValue",        //$NON-NLS-1$
                         "isReadOnly",                    //$NON-NLS-1$
                         "isSqlNullable",                 //$NON-NLS-1$
                         "javaName",            	      //$NON-NLS-1$
                         "keyItem",                       //$NON-NLS-1$
                         "keyItems",                      //$NON-NLS-1$
                         "labelAndHelpResource",          //$NON-NLS-1$
                         "leftMargin",                    //$NON-NLS-1$
                         "lengthItem",                    //$NON-NLS-1$
                         "linesBetweenRows",              //$NON-NLS-1$
                         "lineWrap", 					  //$NON-NLS-1$
                         "linkParms", 					  //$NON-NLS-1$
                         "linkParameter", 				  //$NON-NLS-1$
                         "localSQLScope",                 //$NON-NLS-1$
                         "lowerCase",                     //$NON-NLS-1$
                         "masked", 	                      //$NON-NLS-1$
                         "maxArrayCount",                 //$NON-NLS-1$
						 "maxInclusive",				  //$NON-NLS-1$
						 "maxExclusive",				  //$NON-NLS-1$
						 "maxLength",					  //$NON-NLS-1$
                         "maxLen", 	                      //$NON-NLS-1$
                         "maxSize",                       //$NON-NLS-1$
						 "minExclusive",                  //$NON-NLS-1$
						 "minInclusive",                  //$NON-NLS-1$
						 "minLength",                     //$NON-NLS-1$
                         "minimumInput",                  //$NON-NLS-1$
                         "minimumInputMsgKey",            //$NON-NLS-1$
                         "modified",                      //$NON-NLS-1$
                         "msgDescriptorRecord",           //$NON-NLS-1$
                         "msgField",                      //$NON-NLS-1$
                         "msgResource",                   //$NON-NLS-1$
                         "msgTablePrefix",                //$NON-NLS-1$
                         "name",	                      //$NON-NLS-1$
                         "namespace",	                  //$NON-NLS-1$
                         "needsSOSI",                     //$NON-NLS-1$
                         "newWindow",                     //$NON-NLS-1$
                         "numElementsItem",               //$NON-NLS-1$
                         "numericSeparator",              //$NON-NLS-1$
                         "onPageLoadFunction",            //$NON-NLS-1$
                         "openOptionsRecord",             //$NON-NLS-1$
                         "openQueueExclusive",            //$NON-NLS-1$
                         "ordering",                      //$NON-NLS-1$
						 "orientIndexAcross",             //$NON-NLS-1$
						 "outline",                       //$NON-NLS-1$
                         "packageName",                   //$NON-NLS-1$
                         "pageSize",                      //$NON-NLS-1$
                         "parentRecord",                  //$NON-NLS-1$
                         "pattern",          	          //$NON-NLS-1$
						 "pcb",    				          //$NON-NLS-1$
						 "pcbName",				          //$NON-NLS-1$
						 "pcbType",				          //$NON-NLS-1$
						 "pcbParms",			          //$NON-NLS-1$
						 "persistent",					  //$NON-NLS-1$
                         "pfKeyEquate",                   //$NON-NLS-1$
						 "position",                      //$NON-NLS-1$
                         "printFloatingArea",             //$NON-NLS-1$
                         "programLinkData",               //$NON-NLS-1$
						 "programName",                   //$NON-NLS-1$
						 "prompt",                        //$NON-NLS-1$
                         "protect",                       //$NON-NLS-1$
						 "psb",	                		  //$NON-NLS-1$						 
						 "psbParm",               		  //$NON-NLS-1$
						 "putOptionsRecord",              //$NON-NLS-1$
                         "queueDescriptorRecord",         //$NON-NLS-1$
                         "queueName",                     //$NON-NLS-1$
                         "redefines",                     //$NON-NLS-1$
                         "relationship",                  //$NON-NLS-1$
                         "reportDesignFile",              //$NON-NLS-1$
                         "reportDestinationFile",         //$NON-NLS-1$
                         "reportExportFile",              //$NON-NLS-1$
                         "reportData", 		              //$NON-NLS-1$
                         "resident",                      //$NON-NLS-1$
                         "rightMargin",                   //$NON-NLS-1$
                         "runValidatorFromProgram",       //$NON-NLS-1$
						 "scope",                         //$NON-NLS-1$
                         "screenFloatingArea",            //$NON-NLS-1$
                         "screenSize",                    //$NON-NLS-1$
                         "screenSizes",                   //$NON-NLS-1$
                         "secondaryIndex",                //$NON-NLS-1$
                         "secondaryIndexItem",            //$NON-NLS-1$
                         "segmented",                     //$NON-NLS-1$
						 "segmentName",                   //$NON-NLS-1$
						 "segmentRecord",                 //$NON-NLS-1$
                         "segments",                      //$NON-NLS-1$
                         "selectedIndexItem",             //$NON-NLS-1$
                         "selectFromListItem",            //$NON-NLS-1$
                         "selectType",                    //$NON-NLS-1$
                         "serviceName",                   //$NON-NLS-1$
                         "servicePackage",                //$NON-NLS-1$
                         "serviceAlias",                  //$NON-NLS-1$
                         "setInitial",                    //$NON-NLS-1$
                         "shared",                        //$NON-NLS-1$
                         "showBrackets",                  //$NON-NLS-1$
                         "sign",                          //$NON-NLS-1$
                         "SOSITakePosition",              //$NON-NLS-1$
                         "spacesBetweenColumns",          //$NON-NLS-1$
                         "sqlDataCode",                   //$NON-NLS-1$
                         "sqlStatement",                  //$NON-NLS-1$
						 "sqlVariableLen",    		      //$NON-NLS-1$
                         "tableNameVariables",            //$NON-NLS-1$
                         "tableNames",                    //$NON-NLS-1$
                         "tcpipLocation",                 //$NON-NLS-1$
                         "throwNrfEofExceptions",    	  //$NON-NLS-1$
                         "timeFormat",                    //$NON-NLS-1$
                         "timeStampFormat", 			  //$NON-NLS-1$
                         "title",                         //$NON-NLS-1$
                         "topMargin",                     //$NON-NLS-1$
                         "typeChkMsgKey",                 //$NON-NLS-1$
                         "uiRecordName",	              //$NON-NLS-1$
						 "uiType",    		              //$NON-NLS-1$
                         "upperCase",                     //$NON-NLS-1$
                         "v60ExceptionCompatibility",	  //$NON-NLS-1$
                         "validationBypassFunctions",     //$NON-NLS-1$
                         "validationBypassKeys",          //$NON-NLS-1$
                         "validationOrder",               //$NON-NLS-1$
                         "validatorDataTable",            //$NON-NLS-1$
                         "validatorDataTableMsgKey",      //$NON-NLS-1$
                         "validatorFunction",             //$NON-NLS-1$
                         "validatorFunctionMsgKey",       //$NON-NLS-1$
                         "validValues",                   //$NON-NLS-1$
                         "validValuesMsgKey",             //$NON-NLS-1$
                         "value",                         //$NON-NLS-1$
                         "valueRef",                      //$NON-NLS-1$
                         "verify",                        //$NON-NLS-1$
                         "view",                          //$NON-NLS-1$
                         "viewRootVar",                   //$NON-NLS-1$
						 "webBinding",                    //$NON-NLS-1$
						 "whitespace",                    //$NON-NLS-1$
						 "wsdlFile",                  	  //$NON-NLS-1$
						 "wsdlPort",                   	  //$NON-NLS-1$
						 "wsdlService",                	  //$NON-NLS-1$
						 "zeroFormat"                     //$NON-NLS-1$
                        };

        static TreeMap sqlItemProperties;
        static TreeMap pageItemProperties;
        static TreeMap dliItemProperties;
        static TreeMap formattingProperties;
        static TreeMap validationProperties;
        static TreeMap uiItemProperties;
        static TreeMap uiItemSAProperties;		//for source assistance editor only
        static TreeMap fieldPresentationProperties;
        static TreeMap doubleByteDevicePresentationProperties;
        static TreeMap staticRecordDataDeclarationProperties;
        static TreeMap dynamicRecordDataDeclarationProperties;
        static TreeMap staticItemDataDeclarationProperties;
        static TreeMap dynamicItemDataDeclarationProperties;
        static TreeMap formGroupUseProperties;
        static TreeMap dataTableUseProperties;
        static TreeMap commonFormProperties;
        static TreeMap basicRecordProperties;
        static TreeMap indexedRecordProperties;
        static TreeMap relativeRecordProperties;
        static TreeMap serialRecordProperties;
        static TreeMap MQRecordProperties;
        static TreeMap SQLRecordProperties;
        static TreeMap vgUIRecordProperties;
        static TreeMap formGroupProperties;
        static TreeMap screenFloatingAreaProperties;
        static TreeMap printFloatingAreaProperties;
        static TreeMap textConstantFormFieldProperties;
        static TreeMap formFieldProperties;
        static TreeMap itemFormFieldProperties;
        static TreeMap printFormFieldProperties;
        static TreeMap variableFieldProperties;
        static TreeMap textVariableFormFieldProperties;
        static TreeMap tuiTextVariableFormFieldProperties; //for tui editor only
        static TreeMap tuiPrintVariableFormFieldProperties; //for tui editor only
        static TreeMap commonVariableFormFieldProperties;
        static TreeMap textFormProperties;
        static TreeMap printFormProperties;
        static TreeMap pageHandlerProperties;
        static TreeMap dataTableProperties;
        static TreeMap functionProperties;
        static TreeMap programProperties;
        static TreeMap basicProgramProperties;
        static TreeMap calledProgramProperties;
        static TreeMap textUIProgramProperties;
        static TreeMap vgWebTransactionProperties;
        static TreeMap libraryProperties;
        static TreeMap handlerProperties;
        static TreeMap serviceBindingLibraryProperteis;
        static TreeMap nativeLibraryProperties;
        static TreeMap nativeLibraryFunctionProperties;
        static TreeMap dictionaryProperties;
        static TreeMap consoleFormProperties;
        static TreeMap consoleFieldProperties;
        static TreeMap consoleArrayFieldProperties;
        static TreeMap windowProperties;
        static TreeMap presentationAttributesProperties;
        static TreeMap menuProperties;
        static TreeMap menuItemProperties;
        static TreeMap promptProperties;
        static TreeMap formArrayElementProperties; // AJP
        static TreeMap psbRecordProperties;
        static TreeMap dliSegmentProperties;
        static TreeMap serviceProperties;
        static TreeMap serviceDeclarationProperties;
        static TreeMap serviceFunctionProperties;
        static TreeMap basicInterfaceProperties;
        static TreeMap javaObjectInterfaceProperties;
        static TreeMap interfaceDeclarationProperties;
        static TreeMap basicAbstractFunctionProperties;
        static TreeMap javaOnlyAbstractFunctionProperties;
        static TreeMap fillerStructureItemProperties;
        
        static TreeMap complexRecordProperties;
        static TreeMap complexDataItemProperties;
        static TreeMap complexItemDeclarationProperties;
        static TreeMap complexStructureItemProperties;
        static TreeMap complexProgramProperties;
        static TreeMap complexFunctionProperties;
        static TreeMap complexServiceProperties;
        static TreeMap complexInterfaceProperties;
        static TreeMap complexAbstractFunctionProperties;
        
        static TreeMap linkParameterProperties;
        static TreeMap linkParmsProperties;
        static TreeMap pcbParmsProperties;
        static TreeMap psbRecordItemProperties;
        static TreeMap hierarchyProperties;
        static TreeMap relationshipProperties;
        

        // types expected for the property
        public static final int nameValue = 0;
        public static final int quotedValue = 1;
        public static final int specificValue = 2;
        public static final int integerValue = 3;
        public static final int literalValue = 4;
        public static final int listValue = 5;
        public static final int literalArray = 6;
        public static final int nestedValue = 7;
        public static final int sqlValue = 8;
        public static final int arrayOfArrays = 9;
        public static final int arrayOf = 10;
        public static final int complexPropertyValue = 11;

        // locations for properties
        // locations that cannot contain nested properties
        public static final int locationDataItem = 1;
        public static final int locationScreenFloatArea = 2;
        public static final int locationPrintFloatArea = 3;
        public static final int locationTextConstantFormField = 4;
        public static final int locationPrintConstantFormField = 5;
        public static final int locationTextVariableFormField = 6;
        public static final int locationPrintVariableFormField = 7;
        public static final int locationPageHandlerDeclaration = 8;
        public static final int locationDataTable = 9;
        public static final int locationFunction = 10;
        public static final int locationProgram = 11;
        public static final int locationLibrary = 12;
        public static final int locationUseDeclaration = 13;
        public static final int locationFormGroupUseDeclaration = 14;
        public static final int locationDataTableUseDeclaration = 15;
        public static final int locationFormUseDeclaration = 16;
        public static final int locationLibraryUseDeclaration = 17;
        public static final int locationStaticItemDataDeclaration = 18;
        public static final int locationDynamicItemDataDeclaration = 19;
        public static final int locationStaticVGUIRecordDataDeclaration = 20;
        public static final int locationDynamicVGUIRecordDataDeclaration = 21;
        public static final int locationStaticPageItemDataDeclaration = 22;
        public static final int locationDynamicPageItemDataDeclaration = 23;
        public static final int locationVGWebTransaction = 24;
        public static final int locationBasicProgram = 25;
        public static final int locationCalledBasicProgram = 26;
        public static final int locationTextUIProgram = 27;
        public static final int locationCalledTextUIProgram = 28;   
        public static final int locationNativeLibrary=29;
        public static final int locationNativeLibraryFunction=30;
        public static final int locationServiceBindingLibrary=31;
      
        public static final int maxLocationNoNesting = locationCalledTextUIProgram;

        // locations that can have only 1 level of nesting
        public static final int locationFormGroup = 35;
        public static final int locationTextFormDeclaration = 36;
        public static final int locationPrintFormDeclaration = 37;

        public static final int maxLocationOneLevelNesting = locationPrintFormDeclaration;

        // locations where properties can be nested any number of levels
        public static final int locationStructureItem = 40;
        public static final int locationBasicRecord = 41;
        public static final int locationIndexedRecord = 42;
        public static final int locationRelativeRecord = 43;
        public static final int locationSerialRecord = 44;
        public static final int locationMQRecord = 45;
        public static final int locationSQLRecord = 46;
        public static final int locationVGUIRecord = 47;        
        public static final int locationAnyRecord = 48;
        public static final int locationFillerStructureItem = 49;

        //locations 49 and 50 are unused
        public static final int locationStaticBasicRecordDataDeclaration = 51;
        public static final int locationStaticIndexedRecordDataDeclaration = 52;
        public static final int locationStaticRelativeRecordDataDeclaration = 53;
        public static final int locationStaticSerialRecordDataDeclaration = 54;
        public static final int locationStaticMQRecordDataDeclaration = 55;
        public static final int locationStaticSQLRecordDataDeclaration = 56;
        public static final int locationStaticAnyRecordDataDeclaration = 57;
        //locations 58-60 are unused
        public static final int locationDynamicBasicRecordDataDeclaration = 61;
        public static final int locationDynamicIndexedRecordDataDeclaration = 62;
        public static final int locationDynamicRelativeRecordDataDeclaration = 63;
        public static final int locationDynamicSerialRecordDataDeclaration = 64;
        public static final int locationDynamicMQRecordDataDeclaration = 65;
        public static final int locationDynamicSQLRecordDataDeclaration = 66;
        public static final int locationDynamicAnyRecordDataDeclaration = 67;

        public static final int locationFormatting = 68;
        public static final int locationSqlItem = 69;
        public static final int locationPageItem = 70;
        public static final int locationUIItem = 71;
        public static final int locationValidation = 72;
        public static final int locationFieldPresentation = 73;
        public static final int locationDoubleByteDevicePresentation = 74;
        public static final int locationVariableField = 75;
        public static final int locationItemFormField = 76;
        
        public static final int locationDictionary = 77;
        public static final int locationConsoleForm = 78;
        public static final int locationConsoleField = 79;
        public static final int locationConsoleArrayField = 80;
        public static final int locationWindow = 81;
        public static final int locationPresentationAttributes = 83;
        public static final int locationMenu = 84;
        public static final int locationMenuItem = 85;
        public static final int locationPrompt = 86;
        public static final int locationOpenUI = 87;
        public static final int locationReport = 88;
        public static final int locationReportData = 89;
        public static final int locationCommonVariableFormField = 90;
        public static final int locationFormField = 91;
        
        // never used by validation, only for TUI editor  
        public static final int locationTuiTextVariableFormField = 92;
        public static final int locationTuiPrintVariableFormField = 93;
        public static final int locationTuiFieldPresentation = 94;
        public static final int locationTuiArrayElementFormField = 95;
        
        public static final int locationPSBRecord = 96;
        public static final int locationDLISegment = 97;
        
        public static final int locationService = 98;
        public static final int locationServiceDeclaration = 99;
        public static final int locationServiceFunction = 100;
        public static final int locationBasicInterface = 101;
        public static final int locationJavaObjectInterface = 102;
        public static final int locationInterfaceDeclaration = 103;
        public static final int locationBasicAbstractFunction = 104;
        public static final int locationJavaOnlyAbstractFunction = 105;
        
        //annotations
        public static final int locationProgramLinkData = 106;
        public static final int locationLinkParameter = 107;

        public static final int locationDLI = 110;
        public static final int locationPCB = 111;
        public static final int locationRelationship = 112;
        public static final int locationEGLBinding = 113;
        public static final int locationWebBinding = 114;

        public static final int locationLinkParms = 115;
        public static final int locationPcbParms = 116;
        
        public static final int locationPsbRecordItem = 117;
        
        public static final int locationSAUIItem = 118;        //never used by validation, only for source assistant editor
        public static final int locationSATUIItem = 119;        //never used by validation, only for source assistant editor

        public static final int locationDL1Item = 120;
        
        public static final int locationHandler = 121;
        
        static {
                sqlItemProperties = new TreeMap();
                pageItemProperties = new TreeMap();
                dliItemProperties = new TreeMap();
                uiItemProperties = new TreeMap();
                uiItemSAProperties = new TreeMap();
                formattingProperties = new TreeMap();
                validationProperties = new TreeMap();
                fieldPresentationProperties = new TreeMap();
                doubleByteDevicePresentationProperties = new TreeMap();
                staticItemDataDeclarationProperties = new TreeMap();
                dynamicItemDataDeclarationProperties = new TreeMap();
                staticRecordDataDeclarationProperties = new TreeMap();
                dynamicRecordDataDeclarationProperties = new TreeMap();
                formGroupUseProperties = new TreeMap();
                dataTableUseProperties = new TreeMap();
                commonFormProperties = new TreeMap();
                basicRecordProperties = new TreeMap();
                indexedRecordProperties = new TreeMap();
                relativeRecordProperties = new TreeMap();
                serialRecordProperties = new TreeMap();
                MQRecordProperties = new TreeMap();
                SQLRecordProperties = new TreeMap();
                vgUIRecordProperties = new TreeMap();
                formGroupProperties = new TreeMap();
                screenFloatingAreaProperties = new TreeMap();
                printFloatingAreaProperties = new TreeMap();
                textConstantFormFieldProperties = new TreeMap();
                formFieldProperties = new TreeMap();
                itemFormFieldProperties = new TreeMap();
                printFormFieldProperties = new TreeMap();
                variableFieldProperties = new TreeMap();
                textVariableFormFieldProperties = new TreeMap();
                tuiTextVariableFormFieldProperties = new TreeMap(); // only for TUI editor
                tuiPrintVariableFormFieldProperties = new TreeMap(); // only for TUI editor
                commonVariableFormFieldProperties = new TreeMap();
                textFormProperties = new TreeMap();
                printFormProperties = new TreeMap();
                pageHandlerProperties = new TreeMap();
                dataTableProperties = new TreeMap();
                functionProperties = new TreeMap();
                programProperties = new TreeMap();
                calledProgramProperties = new TreeMap();
                basicProgramProperties = new TreeMap();
                textUIProgramProperties = new TreeMap();
                vgWebTransactionProperties = new TreeMap();
                libraryProperties = new TreeMap();
                handlerProperties = new TreeMap();
                serviceBindingLibraryProperteis = new TreeMap();
                nativeLibraryProperties = new TreeMap();
                nativeLibraryFunctionProperties = new TreeMap();
                dictionaryProperties = new TreeMap();
                consoleFormProperties = new TreeMap();
                consoleFieldProperties = new TreeMap();
                consoleArrayFieldProperties = new TreeMap();
                windowProperties = new TreeMap();
                presentationAttributesProperties = new TreeMap();
                menuProperties = new TreeMap();
                menuItemProperties = new TreeMap();
                promptProperties = new TreeMap();
                formArrayElementProperties = new TreeMap();
                psbRecordProperties = new TreeMap();
                dliSegmentProperties = new TreeMap();
                serviceProperties = new TreeMap();
                serviceDeclarationProperties = new TreeMap();
                serviceFunctionProperties = new TreeMap();
                basicInterfaceProperties = new TreeMap();
                javaObjectInterfaceProperties = new TreeMap();
                interfaceDeclarationProperties = new TreeMap();
                basicAbstractFunctionProperties = new TreeMap();
                javaOnlyAbstractFunctionProperties = new TreeMap();
                fillerStructureItemProperties = new TreeMap();
                
                complexRecordProperties = new TreeMap();
                complexDataItemProperties = new TreeMap();
                complexItemDeclarationProperties = new TreeMap();
                complexStructureItemProperties = new TreeMap();
                complexProgramProperties = new TreeMap();
                complexFunctionProperties = new TreeMap();
                complexAbstractFunctionProperties = new TreeMap();
                complexServiceProperties = new TreeMap();
                complexInterfaceProperties = new TreeMap();
                
                linkParameterProperties = new TreeMap();
                linkParmsProperties = new TreeMap();
                pcbParmsProperties = new TreeMap();
                psbRecordItemProperties = new TreeMap();
                hierarchyProperties = new TreeMap();
                relationshipProperties = new TreeMap();

        String yesOrNo[] = new String [] {IEGLConstants.MNEMONIC_YES, IEGLConstants.MNEMONIC_NO };
        
        int nameValueValid[] = new int [] {nameValue};			//1+1 - helpKey, outline
        int quotedValueValid[] = new int [] {quotedValue};		//90+4
        int specificValueValid[] = new int [] {specificValue};	//71+4
        int integerValueValid[] = new int [] {integerValue};	//15
        int literalValueValid[] = new int [] {literalValue};	//1 - valuePageItem
        int listValueValid[] = new int [] {listValue};			//11
        int literalArrayValid[] = new int [] {literalArray};	//1 - contents
        int nestedValueValid[] = new int [] {nestedValue};		//2 - printFloatingArea, screenFloatingArea
        int sqlValueValid[] = new int [] {sqlValue};			//1 - defaultSelectCondition
        int arrayOfArraysValid[] = new int [] {arrayOfArrays};	//2 - tableNameVariables, tableNames
        int arrayOfValid[] = new int [] {arrayOf};				//2 - hierarchy, linkParms
        int complexPropertyValueValid[] = new int [] {complexPropertyValue};	//2 - hierarchy, linkParms

		EGLPropertyRule elementName = new EGLPropertyRule( IEGLConstants.PROPERTY_ELEMENTNAME, quotedValueValid);
		EGLPropertyRule namespace = new EGLPropertyRule( IEGLConstants.PROPERTY_NAMESPACE, quotedValueValid);
        EGLPropertyRule base = new EGLPropertyRule( IEGLConstants.PROPERTY_BASE, quotedValueValid);
		EGLPropertyRule minLength = new EGLPropertyRule( IEGLConstants.PROPERTY_MINLENGTH, quotedValueValid);
		EGLPropertyRule maxLength = new EGLPropertyRule( IEGLConstants.PROPERTY_MAXLENGTH, quotedValueValid);
		EGLPropertyRule enumeration = new EGLPropertyRule( IEGLConstants.PROPERTY_ENUMERATION, listValueValid);
		EGLPropertyRule whitespace = new EGLPropertyRule( IEGLConstants.PROPERTY_WHITESPACE, quotedValueValid);
		EGLPropertyRule maxInclusive = new EGLPropertyRule( IEGLConstants.PROPERTY_MAXINCLUSIVE, quotedValueValid);
		EGLPropertyRule minInclusive = new EGLPropertyRule( IEGLConstants.PROPERTY_MININCLUSIVE, quotedValueValid);
		EGLPropertyRule maxExclusive = new EGLPropertyRule( IEGLConstants.PROPERTY_MAXEXCLUSIVE, quotedValueValid);
		EGLPropertyRule minExclusive = new EGLPropertyRule( IEGLConstants.PROPERTY_MINEXCLUSIVE, quotedValueValid);
        
		EGLPropertyRule action = new EGLPropertyRule(IEGLConstants.PROPERTY_ACTION, quotedValueValid);
        EGLPropertyRule addSpaceForSOSI = new EGLPropertyRule(IEGLConstants.PROPERTY_ADDSPACEFORSOSI, specificValueValid, yesOrNo );
        EGLPropertyRule alias = new EGLPropertyRule(IEGLConstants.PROPERTY_ALIAS, quotedValueValid );
        EGLPropertyRule align = new EGLPropertyRule(IEGLConstants.PROPERTY_ALIGN, specificValueValid, new String [] { IEGLConstants.MNEMONIC_LEFT, IEGLConstants.MNEMONIC_RIGHT, IEGLConstants.MNEMONIC_NONE, IEGLConstants.MNEMONIC_CENTER }, true );
        EGLPropertyRule allowAppend = new EGLPropertyRule(IEGLConstants.PROPERTY_ALLOWAPPEND, specificValueValid, yesOrNo);
        EGLPropertyRule allowDelete = new EGLPropertyRule(IEGLConstants.PROPERTY_ALLOWDELETE, specificValueValid, yesOrNo);
        EGLPropertyRule allowInsert = new EGLPropertyRule(IEGLConstants.PROPERTY_ALLOWINSERT, specificValueValid, yesOrNo);
        EGLPropertyRule binding = new EGLPropertyRule(IEGLConstants.PROPERTY_BINDING, quotedValueValid);
        EGLPropertyRule bindingByName = new EGLPropertyRule(IEGLConstants.PROPERTY_BINDINGBYNAME, specificValueValid, yesOrNo);
        EGLPropertyRule isBoolean = new EGLPropertyRule(IEGLConstants.PROPERTY_ISBOOLEAN, specificValueValid, yesOrNo, true );
        EGLPropertyRule bottomMargin = new EGLPropertyRule(IEGLConstants.PROPERTY_BOTTOMMARGIN, integerValueValid );
        EGLPropertyRule bypassValidation = new EGLPropertyRule(IEGLConstants.PROPERTY_BYPASSVALIDATION, specificValueValid, yesOrNo );
        EGLPropertyRule callingConvention = new EGLPropertyRule(IEGLConstants.PROPERTY_CALLINGCONVENTION, specificValueValid, new String[] { IEGLConstants.MNEMONIC_I4GL, "lib" } ); //$NON-NLS-1$
        EGLPropertyRule caseSensitive = new EGLPropertyRule(IEGLConstants.PROPERTY_CASESENSITIVE, specificValueValid, yesOrNo );
        EGLPropertyRule color = new EGLPropertyRule(IEGLConstants.PROPERTY_COLOR, specificValueValid, new String [] { IEGLConstants.MNEMONIC_DEFAULTCOLOR, IEGLConstants.MNEMONIC_BLUE, IEGLConstants.MNEMONIC_GREEN, IEGLConstants.MNEMONIC_MAGENTA, IEGLConstants.MNEMONIC_RED, IEGLConstants.MNEMONIC_CYAN, IEGLConstants.MNEMONIC_YELLOW, IEGLConstants.MNEMONIC_WHITE, IEGLConstants.MNEMONIC_BLACK } );
        EGLPropertyRule column = new EGLPropertyRule(IEGLConstants.PROPERTY_COLUMN, quotedValueValid);
        EGLPropertyRule columns = new EGLPropertyRule(IEGLConstants.PROPERTY_COLUMNS, integerValueValid );
        EGLPropertyRule commandValueItem = new EGLPropertyRule(IEGLConstants.PROPERTY_COMMANDVALUEITEM, quotedValueValid);
        EGLPropertyRule commType = new EGLPropertyRule(IEGLConstants.PROPERTY_COMMTYPE, specificValueValid, new String [] { IEGLConstants.MNEMONIC_LOCAL, IEGLConstants.MNEMONIC_TCPIP });
        commType.setEnumeration(EGLCommTypeKindEnumeration.getInstance());
        EGLPropertyRule connectionName = new EGLPropertyRule(IEGLConstants.PROPERTY_CONNECTIONNAME, quotedValueValid );
        EGLPropertyRule containerContextDependent = new EGLPropertyRule(IEGLConstants.PROPERTY_CONTAINERCONTEXTDEPENDENT, specificValueValid, yesOrNo );
        EGLPropertyRule contents = new EGLPropertyRule(IEGLConstants.PROPERTY_CONTENTS, literalArrayValid );
        EGLPropertyRule currency = new EGLPropertyRule(IEGLConstants.PROPERTY_CURRENCY, specificValueValid, yesOrNo, true );
        EGLPropertyRule currencySymbol = new EGLPropertyRule(IEGLConstants.PROPERTY_CURRENCYSYMBOL, quotedValueValid, null, true );
        EGLPropertyRule cursor =  new EGLPropertyRule(IEGLConstants.PROPERTY_CURSOR, specificValueValid, yesOrNo );
        EGLPropertyRule data = new EGLPropertyRule(IEGLConstants.PROPERTY_DATA, quotedValueValid);
        EGLPropertyRule dataType = new EGLPropertyRule(IEGLConstants.PROPERTY_DATATYPE, quotedValueValid);
        EGLPropertyRule dateFormat = new EGLPropertyRule(IEGLConstants.PROPERTY_DATEFORMAT, new int [] {specificValue, quotedValue}, new String [] {IEGLConstants.MNEMONIC_ISODATEFORMAT, IEGLConstants.MNEMONIC_USADATEFORMAT, IEGLConstants.MNEMONIC_EURDATEFORMAT, IEGLConstants.MNEMONIC_JISDATEFORMAT, IEGLConstants.MNEMONIC_DEFAULTDATEFORMAT, IEGLConstants.MNEMONIC_SYSTEMGREGORIANDATEFORMAT, IEGLConstants.MNEMONIC_SYSTEMJULIANDATEFORMAT }, true );
        EGLPropertyRule debugImpl = new EGLPropertyRule(IEGLConstants.PROPERTY_DEBUGIMPL, quotedValueValid);
        EGLPropertyRule defaultSelectCondition = new EGLPropertyRule(IEGLConstants.PROPERTY_DEFAULTSELECTCONDITION, sqlValueValid );
        EGLPropertyRule deleteAfterUse = new EGLPropertyRule(IEGLConstants.PROPERTY_DELETEAFTERUSE, specificValueValid, yesOrNo );
        EGLPropertyRule delimiters = new EGLPropertyRule(IEGLConstants.PROPERTY_DELIMITERS, quotedValueValid);
        EGLPropertyRule detectable =  new EGLPropertyRule(IEGLConstants.PROPERTY_DETECTABLE, specificValueValid, yesOrNo );
        EGLPropertyRule deviceType = new EGLPropertyRule(IEGLConstants.PROPERTY_DEVICETYPE, specificValueValid, new String [] {IEGLConstants.MNEMONIC_SINGLEBYTE, IEGLConstants.MNEMONIC_DOUBLEBYTE } );
        EGLPropertyRule displayName = new EGLPropertyRule(IEGLConstants.PROPERTY_DISPLAYNAME, quotedValueValid);
        EGLPropertyRule displayOnly = new EGLPropertyRule(IEGLConstants.PROPERTY_DISPLAYONLY, specificValueValid, yesOrNo);
        EGLPropertyRule displayUse =new EGLPropertyRule(IEGLConstants.PROPERTY_DISPLAYUSE, specificValueValid, new String [] { IEGLConstants.MNEMONIC_INPUT, IEGLConstants.MNEMONIC_OUTPUT, IEGLConstants.MNEMONIC_SECRET, IEGLConstants.MNEMONIC_BUTTON, IEGLConstants.MNEMONIC_HYPERLINK, IEGLConstants.MNEMONIC_TABLE } );
        EGLPropertyRule dllName = new EGLPropertyRule(IEGLConstants.PROPERTY_DLLNAME, quotedValueValid);
        EGLPropertyRule endpoint = new EGLPropertyRule(IEGLConstants.PROPERTY_ENDPOINT, quotedValueValid);
        EGLPropertyRule fieldLen = new EGLPropertyRule(IEGLConstants.PROPERTY_FIELDLEN, integerValueValid );
        EGLPropertyRule fileName = new EGLPropertyRule(IEGLConstants.PROPERTY_FILENAME, quotedValueValid );
        EGLPropertyRule fill = new EGLPropertyRule(IEGLConstants.PROPERTY_FILL, specificValueValid, yesOrNo, true );
        EGLPropertyRule fillCharacter = new EGLPropertyRule(IEGLConstants.PROPERTY_FILLCHARACTER, new int [] {specificValue, quotedValue}, new String [] {IEGLConstants.MNEMONIC_NULLFILL}, true );
        EGLPropertyRule formSize = new EGLPropertyRule(IEGLConstants.PROPERTY_FORMSIZE, listValueValid );
        EGLPropertyRule getOptionsRecord = new EGLPropertyRule(IEGLConstants.PROPERTY_GETOPTIONSRECORD, quotedValueValid );
        EGLPropertyRule handleHardIOErrors = new EGLPropertyRule(IEGLConstants.PROPERTY_HANDLEHARDIOERRORS, specificValueValid, yesOrNo );
        EGLPropertyRule helpForm = new EGLPropertyRule(IEGLConstants.PROPERTY_HELPFORM, quotedValueValid );
        EGLPropertyRule helpGroup = new EGLPropertyRule(IEGLConstants.PROPERTY_HELPGROUP, specificValueValid, yesOrNo );
        EGLPropertyRule helpKey = new EGLPropertyRule(IEGLConstants.PROPERTY_HELPKEY, nameValueValid );
        EGLPropertyRule hierarchy = new EGLPropertyRule(IEGLConstants.PROPERTY_HIERARCHY, arrayOfValid, complexPropertyValueValid, null );        
        EGLPropertyRule highlight = new EGLPropertyRule(IEGLConstants.PROPERTY_HIGHLIGHT, specificValueValid, new String [] { IEGLConstants.MNEMONIC_DEFAULTHIGHLIGHT, IEGLConstants.MNEMONIC_NOHIGHLIGHT, IEGLConstants.MNEMONIC_BLINK, IEGLConstants.MNEMONIC_REVERSE, IEGLConstants.MNEMONIC_UNDERLINE } );
        EGLPropertyRule includeFunctions = new EGLPropertyRule(IEGLConstants.PROPERTY_INCLUDEREFERENCEDFUNCTIONS, specificValueValid, yesOrNo );
        EGLPropertyRule includeMsgInTransaction = new EGLPropertyRule(IEGLConstants.PROPERTY_INCLUDEMSGINTRANSACTION, specificValueValid, yesOrNo );
        EGLPropertyRule indexOrientation = new EGLPropertyRule(IEGLConstants.PROPERTY_INDEXORIENTATION, specificValueValid, new String [] {IEGLConstants.MNEMONIC_ACROSS, IEGLConstants.MNEMONIC_DOWN } );
        EGLPropertyRule initialized = new EGLPropertyRule(IEGLConstants.PROPERTY_INITIALIZED, specificValueValid, yesOrNo );
        EGLPropertyRule inputForm = new EGLPropertyRule(IEGLConstants.PROPERTY_INPUTFORM, quotedValueValid );
        EGLPropertyRule inputUIRecord = new EGLPropertyRule(IEGLConstants.PROPERTY_INPUTUIRECORD, quotedValueValid );
        EGLPropertyRule inputRecord = new EGLPropertyRule(IEGLConstants.PROPERTY_INPUTRECORD, quotedValueValid );
        EGLPropertyRule inputRequired = new EGLPropertyRule(IEGLConstants.PROPERTY_INPUTREQUIRED, specificValueValid, yesOrNo, true );
        EGLPropertyRule inputRequiredMsgKey = new EGLPropertyRule(IEGLConstants.PROPERTY_INPUTREQUIREDMSGKEY, quotedValueValid, null, true );
        EGLPropertyRule intensity = new EGLPropertyRule(IEGLConstants.PROPERTY_INTENSITY, specificValueValid, new String [] { IEGLConstants.MNEMONIC_NORMALINTENSITY, IEGLConstants.MNEMONIC_BOLD, IEGLConstants.MNEMONIC_INVISIBLE, IEGLConstants.MNEMONIC_DIM, IEGLConstants.MNEMONIC_DEFAULTINTENSITY} );
        EGLPropertyRule isConstruct = new EGLPropertyRule(IEGLConstants.PROPERTY_ISCONSTRUCT, specificValueValid, yesOrNo);
        EGLPropertyRule isDecimalDigit = new EGLPropertyRule(IEGLConstants.PROPERTY_ISDECIMALDIGIT, specificValueValid, yesOrNo, true );
        EGLPropertyRule isHexDigit = new EGLPropertyRule(IEGLConstants.PROPERTY_ISHEXDIGIT, specificValueValid, yesOrNo, true );
        EGLPropertyRule isLastParamReturnValue = new EGLPropertyRule(IEGLConstants.PROPERTY_ISLASTPARAMRETURNVALUE, specificValueValid, yesOrNo );
        EGLPropertyRule isReadOnly = new EGLPropertyRule(IEGLConstants.PROPERTY_ISREADONLY, specificValueValid, yesOrNo );
        EGLPropertyRule javaName = new EGLPropertyRule(IEGLConstants.PROPERTY_JAVANAME, quotedValueValid );
        EGLPropertyRule keyItem = new EGLPropertyRule(IEGLConstants.PROPERTY_KEYITEM, quotedValueValid );
        EGLPropertyRule keyItems = new EGLPropertyRule(IEGLConstants.PROPERTY_KEYITEMS, listValueValid );
        EGLPropertyRule leftMargin = new EGLPropertyRule(IEGLConstants.PROPERTY_LEFTMARGIN, integerValueValid );
        EGLPropertyRule lengthItem = new EGLPropertyRule(IEGLConstants.PROPERTY_LENGTHITEM, quotedValueValid );
        EGLPropertyRule linesBetweenRows = new EGLPropertyRule(IEGLConstants.PROPERTY_LINESBETWEENROWS, integerValueValid );        
        EGLPropertyRule linkParms = new EGLPropertyRule(IEGLConstants.PROPERTY_LINKPARMS, arrayOfValid, complexPropertyValueValid, null );        
        EGLPropertyRule localSQLScope = new EGLPropertyRule(IEGLConstants.PROPERTY_LOCALSQLSCOPE, specificValueValid, yesOrNo);
        EGLPropertyRule lowercase = new EGLPropertyRule(IEGLConstants.PROPERTY_LOWERCASE, specificValueValid, yesOrNo );
        EGLPropertyRule masked = new EGLPropertyRule(IEGLConstants.PROPERTY_MASKED, specificValueValid, yesOrNo );
        EGLPropertyRule maxLen = new EGLPropertyRule(IEGLConstants.PROPERTY_MAXLEN, integerValueValid );
        EGLPropertyRule maxSize = new EGLPropertyRule(IEGLConstants.PROPERTY_MAXSIZE, integerValueValid );
        EGLPropertyRule minimumInput = new EGLPropertyRule(IEGLConstants.PROPERTY_MINIMUMINPUT, integerValueValid, null, true );
        EGLPropertyRule minimumInputMsgKey = new EGLPropertyRule(IEGLConstants.PROPERTY_MINIMUMINPUTMSGKEY, quotedValueValid, null, true );
        EGLPropertyRule modified = new EGLPropertyRule(IEGLConstants.PROPERTY_MODIFIED, specificValueValid, yesOrNo );
        EGLPropertyRule msgDescriptorRecord = new EGLPropertyRule(IEGLConstants.PROPERTY_MSGDESCRIPTORRECORD, quotedValueValid );
        EGLPropertyRule msgField = new EGLPropertyRule(IEGLConstants.PROPERTY_MSGFIELD, quotedValueValid );
        EGLPropertyRule msgResource = new EGLPropertyRule(IEGLConstants.PROPERTY_MSGRESOURCE, quotedValueValid );
        EGLPropertyRule msgTablePrefix = new EGLPropertyRule(IEGLConstants.PROPERTY_MSGTABLEPREFIX, quotedValueValid );
        EGLPropertyRule name = new EGLPropertyRule(IEGLConstants.PROPERTY_NAME, quotedValueValid );
        EGLPropertyRule needsSOSI = new EGLPropertyRule(IEGLConstants.PROPERTY_NEEDSSOSI, specificValueValid, yesOrNo, true );
        EGLPropertyRule newWindow = new EGLPropertyRule(IEGLConstants.PROPERTY_NEWWINDOW, specificValueValid, yesOrNo );
        EGLPropertyRule numElementsItem = new EGLPropertyRule(IEGLConstants.PROPERTY_NUMELEMENTSITEM, quotedValueValid );
        EGLPropertyRule numericSeparator = new EGLPropertyRule(IEGLConstants.PROPERTY_NUMERICSEPARATOR, specificValueValid, yesOrNo, true );
        EGLPropertyRule openOptionsRecord = new EGLPropertyRule(IEGLConstants.PROPERTY_OPENOPTIONSRECORD, quotedValueValid );
        EGLPropertyRule openQueueExclusive = new EGLPropertyRule(IEGLConstants.PROPERTY_OPENQUEUEEXCLUSIVE, specificValueValid, yesOrNo );
        EGLPropertyRule ordering = new EGLPropertyRule(IEGLConstants.PROPERTY_ORDERING, specificValueValid, new String [] { IEGLConstants.MNEMONIC_NONE, IEGLConstants.MNEMONIC_BYINSERTION, IEGLConstants.MNEMONIC_BYKEY } );
        EGLPropertyRule orientIndexAcross = new EGLPropertyRule(IEGLConstants.PROPERTY_ORIENTINDEXACROSS, specificValueValid, yesOrNo);
        EGLPropertyRule outline = new EGLPropertyRule(IEGLConstants.PROPERTY_OUTLINE, new int [] {listValue}, new String [] { IEGLConstants.MNEMONIC_RIGHT, IEGLConstants.MNEMONIC_LEFT, IEGLConstants.MNEMONIC_TOP, IEGLConstants.MNEMONIC_BOTTOM, IEGLConstants.MNEMONIC_BOX, IEGLConstants.MNEMONIC_NOOUTLINE} );
        EGLPropertyRule packageName = new EGLPropertyRule(IEGLConstants.PROPERTY_PACKAGENAME, quotedValueValid );
        EGLPropertyRule pageSize = new EGLPropertyRule(IEGLConstants.PROPERTY_PAGESIZE, listValueValid );
        EGLPropertyRule parentRecord = new EGLPropertyRule(IEGLConstants.PROPERTY_PARENTRECORD, quotedValueValid );
        EGLPropertyRule pattern = new EGLPropertyRule(IEGLConstants.PROPERTY_PATTERN, quotedValueValid );
        EGLPropertyRule persistent = new EGLPropertyRule(IEGLConstants.PROPERTY_PERSISTENT, specificValueValid, yesOrNo );
        EGLPropertyRule pfEquate = new EGLPropertyRule(IEGLConstants.PROPERTY_PFKEYEQUATE, specificValueValid, yesOrNo );
        EGLPropertyRule position = new EGLPropertyRule(IEGLConstants.PROPERTY_POSITION, listValueValid );
        EGLPropertyRule printFloatingArea = new EGLPropertyRule(IEGLConstants.PROPERTY_PRINTFLOATINGAREA, nestedValueValid );
		EGLPropertyRule printFormHighlight = new EGLPropertyRule(IEGLConstants.PROPERTY_HIGHLIGHT, specificValueValid, new String [] {IEGLConstants.MNEMONIC_UNDERLINE } );
		EGLPropertyRule programName = new EGLPropertyRule(IEGLConstants.PROPERTY_PROGRAMNAME, quotedValueValid );
        EGLPropertyRule protect =  new EGLPropertyRule(IEGLConstants.PROPERTY_PROTECT, specificValueValid, new String [] {"protect", "noProtect", "skipProtect" } ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        EGLPropertyRule protectSkip = new EGLPropertyRule(IEGLConstants.PROPERTY_PROTECT, specificValueValid, new String [] { "protect", "noProtect", "skipProtect" } );   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        EGLPropertyRule putOptionsRecord = new EGLPropertyRule(IEGLConstants.PROPERTY_PUTOPTIONSRECORD, quotedValueValid );
        EGLPropertyRule queueDescriptorRecord = new EGLPropertyRule(IEGLConstants.PROPERTY_QUEUEDESCRIPTORRECORD, quotedValueValid );
        EGLPropertyRule queueName = new EGLPropertyRule(IEGLConstants.PROPERTY_QUEUENAME, quotedValueValid );
        EGLPropertyRule redefines = new EGLPropertyRule(IEGLConstants.PROPERTY_REDEFINES, quotedValueValid );
        EGLPropertyRule reportDesignFile = new EGLPropertyRule(IEGLConstants.PROPERTY_REPORTDESIGNFILE, quotedValueValid );
        EGLPropertyRule reportDestinationFile = new EGLPropertyRule(IEGLConstants.PROPERTY_REPORTDESTINATIONFILE, quotedValueValid );
        EGLPropertyRule reportExportFile = new EGLPropertyRule(IEGLConstants.PROPERTY_REPORTEXPORTFILE, quotedValueValid );
        EGLPropertyRule reportData = new EGLPropertyRule(IEGLConstants.PROPERTY_REPORTDATA, quotedValueValid );
        EGLPropertyRule resident = new EGLPropertyRule(IEGLConstants.PROPERTY_RESIDENT, specificValueValid, yesOrNo );
        EGLPropertyRule rightMargin = new EGLPropertyRule(IEGLConstants.PROPERTY_RIGHTMARGIN, integerValueValid );
        EGLPropertyRule runValidatorFromProgram = new EGLPropertyRule(IEGLConstants.PROPERTY_RUNVALIDATORFROMPROGRAM, specificValueValid, yesOrNo );
        EGLPropertyRule scope = new EGLPropertyRule(IEGLConstants.PROPERTY_SCOPE, specificValueValid, new String [] {IEGLConstants.MNEMONIC_REQUEST, IEGLConstants.MNEMONIC_SESSION, "pageSession" } ); //$NON-NLS-1$
        EGLPropertyRule screenFloatingArea = new EGLPropertyRule(IEGLConstants.PROPERTY_SCREENFLOATINGAREA, nestedValueValid );
        EGLPropertyRule screenSize = new EGLPropertyRule(IEGLConstants.PROPERTY_SCREENSIZE, listValueValid );
        EGLPropertyRule screenSizes = new EGLPropertyRule(IEGLConstants.PROPERTY_SCREENSIZES, arrayOfArraysValid );
        EGLPropertyRule secondaryIndexItem = new EGLPropertyRule(IEGLConstants.PROPERTY_SECONDARYINDEXITEM, quotedValueValid );        
        EGLPropertyRule segmented = new EGLPropertyRule(IEGLConstants.PROPERTY_SEGMENTED, specificValueValid, yesOrNo );
        EGLPropertyRule segmentRecord = new EGLPropertyRule(IEGLConstants.PROPERTY_SEGMENTRECORD, quotedValueValid );
        EGLPropertyRule segments = new EGLPropertyRule(IEGLConstants.PROPERTY_SEGMENTS, arrayOfArraysValid );
        EGLPropertyRule selectedIndexItem =new EGLPropertyRule(IEGLConstants.PROPERTY_SELECTEDINDEXITEM, quotedValueValid);
        EGLPropertyRule selectFromListItem =new EGLPropertyRule(IEGLConstants.PROPERTY_SELECTFROMLISTITEM, quotedValueValid);
        EGLPropertyRule selectType =new EGLPropertyRule(IEGLConstants.PROPERTY_SELECTTYPE, specificValueValid, new String [] { IEGLConstants.MNEMONIC_INDEX, IEGLConstants.MNEMONIC_VALUE } );
        EGLPropertyRule setInitial = new EGLPropertyRule(IEGLConstants.PROPERTY_SETINITIAL, specificValueValid, yesOrNo);
        EGLPropertyRule serviceAlias = new EGLPropertyRule(IEGLConstants.PROPERTY_SERVICEALIAS, quotedValueValid);
        EGLPropertyRule serviceName = new EGLPropertyRule(IEGLConstants.PROPERTY_SERVICENAME, quotedValueValid);
        EGLPropertyRule servicePackage = new EGLPropertyRule(IEGLConstants.PROPERTY_SERVICEPACKAGE, quotedValueValid);
        EGLPropertyRule shared = new EGLPropertyRule(IEGLConstants.PROPERTY_SHARED, specificValueValid, yesOrNo );
        EGLPropertyRule showBrackets = new EGLPropertyRule(IEGLConstants.PROPERTY_SHOWBRACKETS, specificValueValid, yesOrNo );
        EGLPropertyRule sign = new EGLPropertyRule(IEGLConstants.PROPERTY_SIGN, specificValueValid, new String [] { IEGLConstants.MNEMONIC_NONE, IEGLConstants.MNEMONIC_LEADING, IEGLConstants.MNEMONIC_TRAILING, IEGLConstants.MNEMONIC_PARENS }, true );
        EGLPropertyRule spacesBetweenColumns = new EGLPropertyRule(IEGLConstants.PROPERTY_SPACESBETWEENCOLUMNS, integerValueValid );
        EGLPropertyRule sqlStatement = new EGLPropertyRule(IEGLConstants.PROPERTY_SQLSTATEMENT, quotedValueValid);
        EGLPropertyRule sqlDataCode = new EGLPropertyRule(IEGLConstants.PROPERTY_SQLDATACODE, integerValueValid);
		EGLPropertyRule sqlVariableLen = new EGLPropertyRule(IEGLConstants.PROPERTY_SQLVARIABLELEN, specificValueValid, yesOrNo );
        EGLPropertyRule tableNameVariables = new EGLPropertyRule(IEGLConstants.PROPERTY_TABLENAMEVARIABLES, arrayOfArraysValid );
        EGLPropertyRule tableNames = new EGLPropertyRule(IEGLConstants.PROPERTY_TABLENAMES, arrayOfArraysValid );
        EGLPropertyRule tcpipLocation = new EGLPropertyRule(IEGLConstants.PROPERTY_TCPIPLOCATION, quotedValueValid);
        EGLPropertyRule throwNrfEofExceptions = new EGLPropertyRule(IEGLConstants.PROPERTY_THROWNRFEOFEXCEPTIONS, specificValueValid, yesOrNo );
        EGLPropertyRule timeFormat = new EGLPropertyRule(IEGLConstants.PROPERTY_TIMEFORMAT, new int [] {specificValue, quotedValue}, new String [] {IEGLConstants.MNEMONIC_ISOTIMEFORMAT, IEGLConstants.MNEMONIC_USATIMEFORMAT, IEGLConstants.MNEMONIC_EURTIMEFORMAT, IEGLConstants.MNEMONIC_JISTIMEFORMAT, IEGLConstants.MNEMONIC_DEFAULTTIMEFORMAT }, true );
        EGLPropertyRule timeStampFormat = new EGLPropertyRule(IEGLConstants.PROPERTY_TIMESTAMPFORMAT, new int [] {specificValue, quotedValue}, new String[] {IEGLConstants.MNEMONIC_DB2TIMESTAMPFORMAT, IEGLConstants.MNEMONIC_ODBCTIMESTAMPFORMAT, IEGLConstants.MNEMONIC_DEFAULTTIMESTAMPFORMAT });
        EGLPropertyRule title = new EGLPropertyRule(IEGLConstants.PROPERTY_TITLE, quotedValueValid );
        EGLPropertyRule topMargin = new EGLPropertyRule(IEGLConstants.PROPERTY_TOPMARGIN, integerValueValid );
        EGLPropertyRule typeChkMsgKey = new EGLPropertyRule(IEGLConstants.PROPERTY_TYPECHKMSGKEY, quotedValueValid, null, true );
        EGLPropertyRule unqualifiedItems = new EGLPropertyRule(IEGLConstants.PROPERTY_ALLOWUNQUALIFIEDITEMREFERENCES, specificValueValid, yesOrNo );
        EGLPropertyRule uiRecordName = new EGLPropertyRule(IEGLConstants.PROPERTY_UIRECORDNAME, quotedValueValid );
        EGLPropertyRule uiType = new EGLPropertyRule(IEGLConstants.PROPERTY_UITYPE, specificValueValid, new String [] { IEGLConstants.MNEMONIC_UIFORM, IEGLConstants.MNEMONIC_HIDDEN, IEGLConstants.MNEMONIC_INPUT, IEGLConstants.MNEMONIC_INPUTOUTPUT, IEGLConstants.MNEMONIC_NONE, IEGLConstants.MNEMONIC_OUTPUT, IEGLConstants.MNEMONIC_PROGRAMLINK, IEGLConstants.MNEMONIC_SUBMIT, IEGLConstants.MNEMONIC_SUBMITBYPASS } );
        uiType.setEnumeration( EGLUITypeKindEnumeration.getInstance() );
        EGLPropertyRule upperCase = new EGLPropertyRule(IEGLConstants.PROPERTY_UPPERCASE, specificValueValid, yesOrNo, true );
        EGLPropertyRule v60ExceptionCompatibility = new EGLPropertyRule(IEGLConstants.PROPERTY_V60EXCEPTIONCOMPATIBILITY, specificValueValid, yesOrNo );
        EGLPropertyRule validationBypassFunctions = new EGLPropertyRule(IEGLConstants.PROPERTY_VALIDATIONBYPASSFUNCTIONS, listValueValid );
        EGLPropertyRule validationBypassKeys = new EGLPropertyRule(IEGLConstants.PROPERTY_VALIDATIONBYPASSKEYS, listValueValid );
        EGLPropertyRule validationOrder = new EGLPropertyRule(IEGLConstants.PROPERTY_VALIDATIONORDER, integerValueValid );
        EGLPropertyRule validatorDataTable = new EGLPropertyRule(IEGLConstants.PROPERTY_VALIDATORDATATABLE, quotedValueValid, null, true);
        EGLPropertyRule validatorDataTableMsgKey = new EGLPropertyRule(IEGLConstants.PROPERTY_VALIDATORDATATABLEMSGKEY, quotedValueValid, null, true );
        EGLPropertyRule validatorFunction = new EGLPropertyRule(IEGLConstants.PROPERTY_VALIDATORFUNCTION, quotedValueValid, null, true );
		EGLPropertyRule validatorFunctionMsgKey = new EGLPropertyRule(IEGLConstants.PROPERTY_VALIDATORFUNCTIONMSGKEY, quotedValueValid, null, true );
        EGLPropertyRule validValues = new EGLPropertyRule(IEGLConstants.PROPERTY_VALIDVALUES, listValueValid, null, true );
        EGLPropertyRule validValuesMsgKey = new EGLPropertyRule(IEGLConstants.PROPERTY_VALIDVALUESMSGKEY, quotedValueValid, null, true );
        EGLPropertyRule value = new EGLPropertyRule(IEGLConstants.PROPERTY_VALUE, quotedValueValid);
        EGLPropertyRule valuePageItem = new EGLPropertyRule(IEGLConstants.PROPERTY_VALUE, literalValueValid);
        EGLPropertyRule valueRef = new EGLPropertyRule(IEGLConstants.PROPERTY_VALUEREF, quotedValueValid);
        EGLPropertyRule valueQuoted = new EGLPropertyRule(IEGLConstants.PROPERTY_VALUE, quotedValueValid );
        EGLPropertyRule verify = new EGLPropertyRule(IEGLConstants.PROPERTY_VERIFY, specificValueValid, yesOrNo);
        EGLPropertyRule view = new EGLPropertyRule(IEGLConstants.PROPERTY_VIEW, quotedValueValid );
        EGLPropertyRule viewRootVar = new EGLPropertyRule(IEGLConstants.PROPERTY_VIEWROOTVAR, quotedValueValid );
        EGLPropertyRule wsdlFile = new EGLPropertyRule(IEGLConstants.PROPERTY_WSDLFILE, quotedValueValid );
        EGLPropertyRule wsdlPort = new EGLPropertyRule(IEGLConstants.PROPERTY_WSDLPORT, quotedValueValid );
        EGLPropertyRule wsdlService = new EGLPropertyRule(IEGLConstants.PROPERTY_WSDLSERVICE, quotedValueValid );
        EGLPropertyRule lineWrap = new EGLPropertyRule(IEGLConstants.PROPERTY_LINEWRAP, specificValueValid, new String[] {IEGLConstants.MNEMONIC_CHARACTER, IEGLConstants.MNEMONIC_WORD, IEGLConstants.MNEMONIC_COMPRESS} );
        EGLPropertyRule zeroFormat = new EGLPropertyRule(IEGLConstants.PROPERTY_ZEROFORMAT, specificValueValid, yesOrNo, true );
        
        EGLPropertyRule programLinkData = new EGLPropertyRule(
        	IEGLConstants.PROPERTY_PROGRAMLINKDATA,
			new EGLPropertyRule[] {
        		programName, newWindow, uiRecordName, linkParms
        	} );            
        
        EGLPropertyRule linkParameter = new EGLPropertyRule(
        	IEGLConstants.PROPERTY_LINKPARAMETER,
			new EGLPropertyRule[] {
        		name, value, valueRef
        	} );
        
        linkParms.elementAnnotationTypes = new EGLPropertyRule[] { linkParameter };
        
        
        EGLPropertyRule relationship = new EGLPropertyRule(
        	IEGLConstants.PROPERTY_RELATIONSHIP,
			new EGLPropertyRule[] {
       			segmentRecord, parentRecord
        	} );
        
        hierarchy.elementAnnotationTypes = new EGLPropertyRule[] { relationship };
       
        EGLPropertyRule eglBinding = new EGLPropertyRule(
        	IEGLConstants.PROPERTY_EGLBINDING,
			new EGLPropertyRule[] {
       			commType, serviceName, servicePackage, serviceAlias, tcpipLocation, debugImpl
        	} );
        
        EGLPropertyRule webBinding = new EGLPropertyRule(
        	IEGLConstants.PROPERTY_WEBBINDING,
			new EGLPropertyRule[] {
        			wsdlFile, wsdlService, wsdlPort, endpoint, debugImpl
        	} );
        
                //parms:
                //      - property name
                //      - integer array containing the types of values that are valid
                //  - if one of the types is specificValue, a string array containing the values
                //                      if yes and no are valid, put yes first.  Code in EGLPropertiesValidator
                //                      depends on yes being first.


// locationDataItem = sqlItemProperties
//                  + pageItemProperties
//                  + uiItemProperties
//                  + formattingProperties
//                  + validationProperties
//                  + fieldPresentationProperties (+ doubleByteDevicePresentationProperties)
//                  + variableFieldProperties
//					+ itemFormFieldProperties
		itemFormFieldProperties.put(IEGLConstants.PROPERTY_FIELDLEN, fieldLen );

// locationStructureItem = sqlItemProperties
//                          + pageItemProperties
//                          + uiItemProperties
//                          + formattingProperties
//                          + validationProperties

        // sql item properties
        sqlItemProperties.put(IEGLConstants.PROPERTY_COLUMN, column);
        sqlItemProperties.put(IEGLConstants.PROPERTY_ISREADONLY, isReadOnly);
        sqlItemProperties.put(IEGLConstants.PROPERTY_SQLDATACODE, sqlDataCode );
 		sqlItemProperties.put(IEGLConstants.PROPERTY_SQLVARIABLELEN, sqlVariableLen );
 		sqlItemProperties.put(IEGLConstants.PROPERTY_PERSISTENT, persistent );
        sqlItemProperties.put(IEGLConstants.PROPERTY_MAXLEN, maxLen );

        // page item properties
        pageItemProperties.put(IEGLConstants.PROPERTY_DISPLAYNAME, displayName);
        pageItemProperties.put(IEGLConstants.PROPERTY_VALUE, valuePageItem);
        pageItemProperties.put(IEGLConstants.PROPERTY_VALIDATIONORDER, validationOrder);
        pageItemProperties.put(IEGLConstants.PROPERTY_NUMELEMENTSITEM, numElementsItem);
        pageItemProperties.put(IEGLConstants.PROPERTY_DISPLAYUSE, displayUse );
        pageItemProperties.put(IEGLConstants.PROPERTY_BYPASSVALIDATION, bypassValidation );
        pageItemProperties.put(IEGLConstants.PROPERTY_ACTION, action );
        pageItemProperties.put(IEGLConstants.PROPERTY_SELECTFROMLISTITEM, selectFromListItem );
        pageItemProperties.put(IEGLConstants.PROPERTY_NEWWINDOW, newWindow);
        pageItemProperties.put(IEGLConstants.PROPERTY_SELECTTYPE, selectType );
        
        fillerStructureItemProperties.put(IEGLConstants.PROPERTY_UITYPE, uiType );

        // ui Item properties
        uiItemProperties.put(IEGLConstants.PROPERTY_ALIAS, alias);
        uiItemProperties.put(IEGLConstants.PROPERTY_CURRENCY, currency);
        uiItemProperties.put(IEGLConstants.PROPERTY_CURRENCYSYMBOL, currencySymbol);
        uiItemProperties.put(IEGLConstants.PROPERTY_DATEFORMAT, dateFormat);
        uiItemProperties.put(IEGLConstants.PROPERTY_DISPLAYNAME, displayName);
        uiItemProperties.put(IEGLConstants.PROPERTY_FILLCHARACTER, fillCharacter );
        uiItemProperties.put(IEGLConstants.PROPERTY_INPUTREQUIRED, inputRequired);
        uiItemProperties.put(IEGLConstants.PROPERTY_INPUTREQUIREDMSGKEY, inputRequiredMsgKey );
        uiItemProperties.put(IEGLConstants.PROPERTY_ISBOOLEAN, isBoolean);
        uiItemProperties.put(IEGLConstants.PROPERTY_MINIMUMINPUT, minimumInput);
        uiItemProperties.put(IEGLConstants.PROPERTY_MINIMUMINPUTMSGKEY, minimumInputMsgKey);
        uiItemProperties.put(IEGLConstants.PROPERTY_NEEDSSOSI, needsSOSI);
        uiItemProperties.put(IEGLConstants.PROPERTY_NUMELEMENTSITEM, numElementsItem);
        uiItemProperties.put(IEGLConstants.PROPERTY_NUMERICSEPARATOR, numericSeparator);
        uiItemProperties.put(IEGLConstants.PROPERTY_PROGRAMLINKDATA, programLinkData);
        uiItemProperties.put(IEGLConstants.PROPERTY_RUNVALIDATORFROMPROGRAM, runValidatorFromProgram);
        uiItemProperties.put(IEGLConstants.PROPERTY_SELECTEDINDEXITEM, selectedIndexItem);
        uiItemProperties.put(IEGLConstants.PROPERTY_SIGN, sign);
        uiItemProperties.put(IEGLConstants.PROPERTY_TIMEFORMAT, timeFormat);
        uiItemProperties.put(IEGLConstants.PROPERTY_TYPECHKMSGKEY, typeChkMsgKey );
        uiItemProperties.put(IEGLConstants.PROPERTY_UITYPE, uiType);
        uiItemProperties.put(IEGLConstants.PROPERTY_UPPERCASE, upperCase);
        uiItemProperties.put(IEGLConstants.PROPERTY_VALIDATIONORDER, validationOrder);
        uiItemProperties.put(IEGLConstants.PROPERTY_VALIDATORDATATABLE, validatorDataTable );
        uiItemProperties.put(IEGLConstants.PROPERTY_VALIDATORDATATABLEMSGKEY, validatorDataTableMsgKey);
        uiItemProperties.put(IEGLConstants.PROPERTY_VALIDATORFUNCTION, validatorFunction );
        uiItemProperties.put(IEGLConstants.PROPERTY_VALIDATORFUNCTIONMSGKEY, validatorFunctionMsgKey);
        uiItemProperties.put(IEGLConstants.PROPERTY_VALIDVALUES, validValues);
        uiItemProperties.put(IEGLConstants.PROPERTY_VALIDVALUESMSGKEY, validValuesMsgKey);
        uiItemProperties.put(IEGLConstants.PROPERTY_ZEROFORMAT, zeroFormat);
        
        // ui Item for Source Assistant editor properties
        uiItemSAProperties.put(IEGLConstants.PROPERTY_ALIAS, alias);
        uiItemSAProperties.put(IEGLConstants.PROPERTY_DISPLAYNAME, displayName);
        uiItemSAProperties.put(IEGLConstants.PROPERTY_NUMELEMENTSITEM, numElementsItem);
        uiItemSAProperties.put(IEGLConstants.PROPERTY_RUNVALIDATORFROMPROGRAM, runValidatorFromProgram);
        uiItemSAProperties.put(IEGLConstants.PROPERTY_SELECTEDINDEXITEM, selectedIndexItem);
        uiItemSAProperties.put(IEGLConstants.PROPERTY_UITYPE, uiType);
        uiItemSAProperties.put(IEGLConstants.PROPERTY_VALIDATIONORDER, validationOrder);        

        // formatting properties
        formattingProperties.put(IEGLConstants.PROPERTY_CURRENCY, currency );
        formattingProperties.put(IEGLConstants.PROPERTY_CURRENCYSYMBOL, currencySymbol );
        formattingProperties.put(IEGLConstants.PROPERTY_ISBOOLEAN, isBoolean);
        formattingProperties.put(IEGLConstants.PROPERTY_ALIGN, align );
        formattingProperties.put(IEGLConstants.PROPERTY_FILLCHARACTER, fillCharacter);
        formattingProperties.put(IEGLConstants.PROPERTY_DATEFORMAT,dateFormat);
        formattingProperties.put(IEGLConstants.PROPERTY_TIMEFORMAT, timeFormat);
        formattingProperties.put(IEGLConstants.PROPERTY_PATTERN, pattern);
        formattingProperties.put(IEGLConstants.PROPERTY_MASKED, masked);
        formattingProperties.put(IEGLConstants.PROPERTY_UPPERCASE, upperCase );
        formattingProperties.put(IEGLConstants.PROPERTY_LOWERCASE, lowercase );
        formattingProperties.put(IEGLConstants.PROPERTY_NUMERICSEPARATOR, numericSeparator);
        formattingProperties.put(IEGLConstants.PROPERTY_ZEROFORMAT, zeroFormat);
        formattingProperties.put(IEGLConstants.PROPERTY_SIGN, sign);
        formattingProperties.put(IEGLConstants.PROPERTY_LINEWRAP, lineWrap);
        formattingProperties.put(IEGLConstants.PROPERTY_TIMESTAMPFORMAT, timeStampFormat);

        // validation properties
        validationProperties.put(IEGLConstants.PROPERTY_ISDECIMALDIGIT, isDecimalDigit );
        validationProperties.put(IEGLConstants.PROPERTY_ISHEXDIGIT, isHexDigit );
        validationProperties.put(IEGLConstants.PROPERTY_NEEDSSOSI, needsSOSI);
        validationProperties.put(IEGLConstants.PROPERTY_VALIDVALUES, validValues);
        validationProperties.put(IEGLConstants.PROPERTY_VALIDVALUESMSGKEY, validValuesMsgKey);
        validationProperties.put(IEGLConstants.PROPERTY_VALIDATORFUNCTION, validatorFunction );
        validationProperties.put(IEGLConstants.PROPERTY_VALIDATORDATATABLE, validatorDataTable );
        validationProperties.put(IEGLConstants.PROPERTY_FILL, fill );
        validationProperties.put(IEGLConstants.PROPERTY_INPUTREQUIRED, inputRequired);
        validationProperties.put(IEGLConstants.PROPERTY_MINIMUMINPUT, minimumInput);
        validationProperties.put(IEGLConstants.PROPERTY_MINIMUMINPUTMSGKEY, minimumInputMsgKey);
        validationProperties.put(IEGLConstants.PROPERTY_INPUTREQUIREDMSGKEY, inputRequiredMsgKey );
        validationProperties.put(IEGLConstants.PROPERTY_TYPECHKMSGKEY, typeChkMsgKey );
        validationProperties.put(IEGLConstants.PROPERTY_VALIDATORFUNCTIONMSGKEY, validatorFunctionMsgKey);
        validationProperties.put(IEGLConstants.PROPERTY_VALIDATORDATATABLEMSGKEY, validatorDataTableMsgKey);

        // field presentation properties
        fieldPresentationProperties.put(IEGLConstants.PROPERTY_COLOR, color);
        fieldPresentationProperties.put(IEGLConstants.PROPERTY_HIGHLIGHT, highlight);
        fieldPresentationProperties.put(IEGLConstants.PROPERTY_INTENSITY, intensity);

        doubleByteDevicePresentationProperties.put(IEGLConstants.PROPERTY_OUTLINE, outline);

        // variable field properties
        variableFieldProperties.put(IEGLConstants.PROPERTY_DETECTABLE, detectable );
        variableFieldProperties.put(IEGLConstants.PROPERTY_MODIFIED, modified );
        variableFieldProperties.put(IEGLConstants.PROPERTY_PROTECT, protectSkip);

//		locationStatic/DynamicBasicRecordDataDeclaration = static/dynamicBasicRecordDataDeclarationProperties
//                                                      + basicRecordProperties

//      locationStatic/DynamicIndexedRecordDataDeclaration = static/dynamicIndexedRecordDataDeclarationProperties
//                                                      + indexedRecordProperties

//      locationStatic/DynamicRelativeRecordDataDeclaration = static/dynamicRelativeRecordDataDeclarationProperties
//                                                      + relativeRecordProperties

//      locationStatic/DynamicSerialRecordDataDeclaration = static/dynamicSerialRecordDataDeclarationProperties
//                                                      + serialRecordProperties

//      locationStatic/DynamicMQRecordDataDeclaration = static/dynamicMQRecordDataDeclarationProperties
//                                                      + MQRecordProperties

//      locationStatic/DynamicSQLRecordDataDeclaration = static/dynamicSQLRecordDataDeclarationProperties
//                                                      + SQLRecordProperties

//      locationStatic/DynamicUIRecordDataDeclaration = static/dynamicUIRecordDataDeclarationProperties
//                       							    + vgUIRecordProperties

//      locationStaticRecordDataDeclaration = initialized and redefinesRecord
        staticRecordDataDeclarationProperties.put(IEGLConstants.PROPERTY_INITIALIZED, initialized);
        staticRecordDataDeclarationProperties.put(IEGLConstants.PROPERTY_REDEFINES, redefines);

//      locationDynamicRecordDataDeclaration = maxsize and redefinesRecord
        dynamicRecordDataDeclarationProperties.put(IEGLConstants.PROPERTY_MAXSIZE, maxSize);
        dynamicRecordDataDeclarationProperties.put(IEGLConstants.PROPERTY_REDEFINES, redefines);

//  locationStaticItemDataDeclaration = Initialized
//										+ pageItemProperties
//                                      + sqlItemProperties 
        staticItemDataDeclarationProperties.put(IEGLConstants.PROPERTY_INITIALIZED, initialized);

//      locationDynamicItemDataDeclaration = maxsize
//										+ pageItemProperties
//										+ sqlItemProperties 
        dynamicItemDataDeclarationProperties.put(IEGLConstants.PROPERTY_MAXSIZE, maxSize);


//  locationUseDeclaration = formGroupUseProperties
//                                   + commonFormProperties
//                                   + datatableUseProperties
        formGroupUseProperties.put(IEGLConstants.PROPERTY_HELPGROUP, helpGroup);

        dataTableUseProperties.put(IEGLConstants.PROPERTY_DELETEAFTERUSE, deleteAfterUse);

        commonFormProperties.put(IEGLConstants.PROPERTY_VALIDATIONBYPASSKEYS, validationBypassKeys);
        commonFormProperties.put(IEGLConstants.PROPERTY_HELPKEY, helpKey);
        commonFormProperties.put(IEGLConstants.PROPERTY_PFKEYEQUATE, pfEquate);


//  locationBasicRecord = basicRecordProperties
//      locationIndexedRecord =  indexedRecordProperties
//      locationRelativeRecord =  relativeRecordProperties
//      locationSerialRecord = serialRecordProperties
//      locationMQRecord =  MQRecordProperties
//      locationSQLRecord =  SQLRecordProperties
// 		locationUIRecord = vgUIRecordProperties l
//      locationAnyRecord = basicRecordProperties
//                          + indexedRecordProperties
//                          + relativeRecordProperties
//                          + serialRecordProperties
//                          + MQRecordProperties
//                          + SQLRecordProperties
//                 		    + vgUIRecordProperties


        basicRecordProperties.put(IEGLConstants.PROPERTY_CONTAINERCONTEXTDEPENDENT, containerContextDependent);

        indexedRecordProperties.put(IEGLConstants.PROPERTY_CONTAINERCONTEXTDEPENDENT, containerContextDependent);
        indexedRecordProperties.put(IEGLConstants.PROPERTY_FILENAME, fileName);
        indexedRecordProperties.put(IEGLConstants.PROPERTY_KEYITEM, keyItem);
        indexedRecordProperties.put(IEGLConstants.PROPERTY_LENGTHITEM, lengthItem);
        indexedRecordProperties.put(IEGLConstants.PROPERTY_NUMELEMENTSITEM, numElementsItem);

        relativeRecordProperties.put(IEGLConstants.PROPERTY_CONTAINERCONTEXTDEPENDENT, containerContextDependent);
        relativeRecordProperties.put(IEGLConstants.PROPERTY_FILENAME, fileName);
        relativeRecordProperties.put(IEGLConstants.PROPERTY_KEYITEM, keyItem);

        serialRecordProperties.put(IEGLConstants.PROPERTY_CONTAINERCONTEXTDEPENDENT, containerContextDependent);
        serialRecordProperties.put(IEGLConstants.PROPERTY_FILENAME, fileName);
        serialRecordProperties.put(IEGLConstants.PROPERTY_LENGTHITEM, lengthItem);
        serialRecordProperties.put(IEGLConstants.PROPERTY_NUMELEMENTSITEM, numElementsItem);

        MQRecordProperties.put(IEGLConstants.PROPERTY_CONTAINERCONTEXTDEPENDENT, containerContextDependent);
        MQRecordProperties.put(IEGLConstants.PROPERTY_GETOPTIONSRECORD, getOptionsRecord);
        MQRecordProperties.put(IEGLConstants.PROPERTY_INCLUDEMSGINTRANSACTION, includeMsgInTransaction);
        MQRecordProperties.put(IEGLConstants.PROPERTY_LENGTHITEM, lengthItem);
        MQRecordProperties.put(IEGLConstants.PROPERTY_MSGDESCRIPTORRECORD, msgDescriptorRecord);
        MQRecordProperties.put(IEGLConstants.PROPERTY_NUMELEMENTSITEM, numElementsItem);
        MQRecordProperties.put(IEGLConstants.PROPERTY_OPENOPTIONSRECORD, openOptionsRecord);
        MQRecordProperties.put(IEGLConstants.PROPERTY_OPENQUEUEEXCLUSIVE, openQueueExclusive);
        MQRecordProperties.put(IEGLConstants.PROPERTY_PUTOPTIONSRECORD, putOptionsRecord);
        MQRecordProperties.put(IEGLConstants.PROPERTY_QUEUEDESCRIPTORRECORD, queueDescriptorRecord);
        MQRecordProperties.put(IEGLConstants.PROPERTY_QUEUENAME, queueName);

        SQLRecordProperties.put(IEGLConstants.PROPERTY_CONTAINERCONTEXTDEPENDENT, containerContextDependent);
        SQLRecordProperties.put(IEGLConstants.PROPERTY_DEFAULTSELECTCONDITION, defaultSelectCondition );
        SQLRecordProperties.put(IEGLConstants.PROPERTY_KEYITEMS, keyItems);
        SQLRecordProperties.put(IEGLConstants.PROPERTY_TABLENAMES, tableNames);
        SQLRecordProperties.put(IEGLConstants.PROPERTY_TABLENAMEVARIABLES, tableNameVariables );
        
        vgUIRecordProperties.put(IEGLConstants.PROPERTY_ALIAS, alias);
        vgUIRecordProperties.put(IEGLConstants.PROPERTY_TITLE, title);
        vgUIRecordProperties.put(IEGLConstants.PROPERTY_VALIDATORFUNCTION, validatorFunction);
        vgUIRecordProperties.put(IEGLConstants.PROPERTY_RUNVALIDATORFROMPROGRAM, runValidatorFromProgram);
        vgUIRecordProperties.put(IEGLConstants.PROPERTY_COMMANDVALUEITEM, commandValueItem);

        dliSegmentProperties.put(IEGLConstants.PROPERTY_KEYITEM, keyItem);
        dliSegmentProperties.put(IEGLConstants.PROPERTY_LENGTHITEM, lengthItem);

//      locationFormGroup = formGroupProperties
//                                + commonFormProperties

        formGroupProperties.put(IEGLConstants.PROPERTY_ALIAS, alias);
        formGroupProperties.put(IEGLConstants.PROPERTY_SCREENFLOATINGAREA, screenFloatingArea);
        formGroupProperties.put(IEGLConstants.PROPERTY_PRINTFLOATINGAREA, printFloatingArea);


//      locationScreenFloatArea = screenFloatingAreaProperties
//      locationPrintFloatArea = printFloatingAreaProperties

        screenFloatingAreaProperties.put(IEGLConstants.PROPERTY_SCREENSIZE, screenSize);
        screenFloatingAreaProperties.put(IEGLConstants.PROPERTY_TOPMARGIN, topMargin);
        screenFloatingAreaProperties.put(IEGLConstants.PROPERTY_BOTTOMMARGIN, bottomMargin);
        screenFloatingAreaProperties.put(IEGLConstants.PROPERTY_LEFTMARGIN, leftMargin);
        screenFloatingAreaProperties.put(IEGLConstants.PROPERTY_RIGHTMARGIN, rightMargin);

        printFloatingAreaProperties.put(IEGLConstants.PROPERTY_DEVICETYPE, deviceType);
        printFloatingAreaProperties.put(IEGLConstants.PROPERTY_PAGESIZE, pageSize);
        printFloatingAreaProperties.put(IEGLConstants.PROPERTY_TOPMARGIN, topMargin);
        printFloatingAreaProperties.put(IEGLConstants.PROPERTY_BOTTOMMARGIN, bottomMargin);
        printFloatingAreaProperties.put(IEGLConstants.PROPERTY_LEFTMARGIN, leftMargin);
        printFloatingAreaProperties.put(IEGLConstants.PROPERTY_RIGHTMARGIN, rightMargin);


//  locationTextConstantFormField = textConstantFormFieldProperties
//                                        + formFieldProperties
//                                        + fieldPresentationProperties
//                                        + doubleByteDevicePresentationProperties

        textConstantFormFieldProperties.put(IEGLConstants.PROPERTY_CURSOR, cursor);
        textConstantFormFieldProperties.put(IEGLConstants.PROPERTY_DETECTABLE, detectable);
        textConstantFormFieldProperties.put(IEGLConstants.PROPERTY_PROTECT, protect);

        formFieldProperties.put(IEGLConstants.PROPERTY_POSITION, position);
        formFieldProperties.put(IEGLConstants.PROPERTY_VALUE, valueQuoted );
        formFieldProperties.put(IEGLConstants.PROPERTY_FIELDLEN, fieldLen );

//      locationPrintConstantFormField = printFormFieldProperties
//                                       + formFieldProperties
//                                       + doubleByteDevicePresentationProperties
        printFormFieldProperties.put(IEGLConstants.PROPERTY_HIGHLIGHT, printFormHighlight );


//      locationPrintVariableFormField = commonVariableFormFieldProperties
//                                       + formFieldProperties
//                                       + formattingProperties
//                                       + doubleByteDevicePresentationProperties

//      locationTextVariableFormField = textVariableFormFieldProperties
//                                       + variableFieldProperties
//                                       + commonVariableFormFieldProperties
//                                       + formFieldProperties
//                                       + formattingProperties
//                                       + validationProperties
//                                       + fieldPresentationProperties
//                                       + doubleByteDevicePresentationProperties
        textVariableFormFieldProperties.put(IEGLConstants.PROPERTY_CURSOR, cursor);
        textVariableFormFieldProperties.put(IEGLConstants.PROPERTY_VALIDATIONORDER, validationOrder);

// 		locationTuiTextVariableFormField - for TUI editor only
        tuiTextVariableFormFieldProperties.put(IEGLConstants.PROPERTY_DETECTABLE, detectable );
        tuiTextVariableFormFieldProperties.put(IEGLConstants.PROPERTY_MODIFIED, modified );
        tuiTextVariableFormFieldProperties.put(IEGLConstants.PROPERTY_PROTECT, protectSkip);
        tuiTextVariableFormFieldProperties.put(IEGLConstants.PROPERTY_CURSOR, cursor);
        tuiTextVariableFormFieldProperties.put(IEGLConstants.PROPERTY_VALIDATIONORDER, validationOrder);

// 		locationTuiPrintVariableFormField - for TUI editor only
        tuiPrintVariableFormFieldProperties.put(IEGLConstants.PROPERTY_HIGHLIGHT, highlight );
        
        commonVariableFormFieldProperties.put(IEGLConstants.PROPERTY_COLUMNS, columns);
        commonVariableFormFieldProperties.put(IEGLConstants.PROPERTY_LINESBETWEENROWS, linesBetweenRows);
        commonVariableFormFieldProperties.put(IEGLConstants.PROPERTY_SPACESBETWEENCOLUMNS, spacesBetweenColumns);
        commonVariableFormFieldProperties.put(IEGLConstants.PROPERTY_INDEXORIENTATION, indexOrientation );


//      locationTextFormDeclaration = textFormProperties
        textFormProperties.put(IEGLConstants.PROPERTY_ALIAS, alias);
        textFormProperties.put(IEGLConstants.PROPERTY_FORMSIZE, formSize);
        textFormProperties.put(IEGLConstants.PROPERTY_SCREENSIZES, screenSizes);
        textFormProperties.put(IEGLConstants.PROPERTY_POSITION, position);
        textFormProperties.put(IEGLConstants.PROPERTY_VALIDATIONBYPASSKEYS, validationBypassKeys);
        textFormProperties.put(IEGLConstants.PROPERTY_HELPKEY, helpKey);
        textFormProperties.put(IEGLConstants.PROPERTY_HELPFORM, helpForm);
        textFormProperties.put(IEGLConstants.PROPERTY_MSGFIELD, msgField );

//      locationPrintFormDeclaration = printFormProperties
        printFormProperties.put(IEGLConstants.PROPERTY_ALIAS, alias);
        printFormProperties.put(IEGLConstants.PROPERTY_FORMSIZE, formSize);
        printFormProperties.put(IEGLConstants.PROPERTY_ADDSPACEFORSOSI, addSpaceForSOSI);
        printFormProperties.put(IEGLConstants.PROPERTY_POSITION, position);
        printFormProperties.put(IEGLConstants.PROPERTY_MSGFIELD, msgField);


//      locationPageHandlerDeclaration = pageHandlerProperties
        pageHandlerProperties.put(IEGLConstants.PROPERTY_VALIDATIONBYPASSFUNCTIONS, validationBypassFunctions);
        pageHandlerProperties.put(IEGLConstants.PROPERTY_VALIDATORFUNCTION, validatorFunction);
        pageHandlerProperties.put(IEGLConstants.PROPERTY_MSGRESOURCE, msgResource);
        pageHandlerProperties.put(IEGLConstants.PROPERTY_ALLOWUNQUALIFIEDITEMREFERENCES, unqualifiedItems );
        pageHandlerProperties.put(IEGLConstants.PROPERTY_INCLUDEREFERENCEDFUNCTIONS, includeFunctions );
        pageHandlerProperties.put(IEGLConstants.PROPERTY_VIEW, view);
        pageHandlerProperties.put(IEGLConstants.PROPERTY_TITLE, title);
        pageHandlerProperties.put(IEGLConstants.PROPERTY_LOCALSQLSCOPE, localSQLScope);
        pageHandlerProperties.put(IEGLConstants.PROPERTY_HANDLEHARDIOERRORS, handleHardIOErrors);
        pageHandlerProperties.put(IEGLConstants.PROPERTY_THROWNRFEOFEXCEPTIONS, throwNrfEofExceptions);
        pageHandlerProperties.put(IEGLConstants.PROPERTY_SCOPE, scope );
        pageHandlerProperties.put(IEGLConstants.PROPERTY_V60EXCEPTIONCOMPATIBILITY, v60ExceptionCompatibility);
        pageHandlerProperties.put(IEGLConstants.PROPERTY_VIEWROOTVAR, viewRootVar );

        
//      locationDataTable = dataTableProperties
        dataTableProperties.put(IEGLConstants.PROPERTY_SHARED, shared);
        dataTableProperties.put(IEGLConstants.PROPERTY_RESIDENT, resident);
        dataTableProperties.put(IEGLConstants.PROPERTY_ALIAS, alias);
        dataTableProperties.put(IEGLConstants.PROPERTY_CONTENTS, contents);

//      locationProgram,
//          basic = basicProgramProperties
//                   + programProperties
//          textUI = textUIProgramProperties
//                   + basicProgramProperties
//                   + programProperties
//			calledbasic = basicProgramProperties
//					 + programProperties
//                   + calledProgramProperties 
//			calledtextUI = textUIProgramProperties
//					 + basicProgramProperties
//					 + programProperties
//                   + calledProgramProperties
//          action = vgWebTransactionProperties
//                   + programProperties
		basicProgramProperties.put(IEGLConstants.PROPERTY_INPUTRECORD, inputRecord);
        basicProgramProperties.put(IEGLConstants.PROPERTY_MSGTABLEPREFIX, msgTablePrefix);

        calledProgramProperties.put(IEGLConstants.PROPERTY_MSGTABLEPREFIX, msgTablePrefix);

        textUIProgramProperties.put(IEGLConstants.PROPERTY_INPUTFORM, inputForm);
        textUIProgramProperties.put(IEGLConstants.PROPERTY_SEGMENTED, segmented);

        vgWebTransactionProperties.put(IEGLConstants.PROPERTY_INPUTUIRECORD, inputUIRecord);
        vgWebTransactionProperties.put(IEGLConstants.PROPERTY_INPUTRECORD, inputRecord);
        vgWebTransactionProperties.put(IEGLConstants.PROPERTY_MSGTABLEPREFIX, msgTablePrefix);

        programProperties.put(IEGLConstants.PROPERTY_ALIAS, alias);
        programProperties.put(IEGLConstants.PROPERTY_ALLOWUNQUALIFIEDITEMREFERENCES, unqualifiedItems );
        programProperties.put(IEGLConstants.PROPERTY_INCLUDEREFERENCEDFUNCTIONS, includeFunctions );
        programProperties.put(IEGLConstants.PROPERTY_LOCALSQLSCOPE, localSQLScope);
        programProperties.put(IEGLConstants.PROPERTY_HANDLEHARDIOERRORS, handleHardIOErrors);
        programProperties.put(IEGLConstants.PROPERTY_THROWNRFEOFEXCEPTIONS, throwNrfEofExceptions);
        programProperties.put(IEGLConstants.PROPERTY_V60EXCEPTIONCOMPATIBILITY, v60ExceptionCompatibility);
       
        serviceProperties.put(IEGLConstants.PROPERTY_ALIAS, alias);
        serviceProperties.put(IEGLConstants.PROPERTY_LOCALSQLSCOPE, localSQLScope);
        serviceProperties.put(IEGLConstants.PROPERTY_HANDLEHARDIOERRORS, handleHardIOErrors);
        serviceProperties.put(IEGLConstants.PROPERTY_THROWNRFEOFEXCEPTIONS, throwNrfEofExceptions);
        serviceProperties.put(IEGLConstants.PROPERTY_V60EXCEPTIONCOMPATIBILITY, v60ExceptionCompatibility);
//        serviceProperties.put(IEGLConstants.PROPERTY_WSDL, wsdl);

        serviceDeclarationProperties.put(IEGLConstants.PROPERTY_EGLBINDING, eglBinding);
        serviceDeclarationProperties.put(IEGLConstants.PROPERTY_WEBBINDING, webBinding);

        interfaceDeclarationProperties.put(IEGLConstants.PROPERTY_EGLBINDING, eglBinding);
        interfaceDeclarationProperties.put(IEGLConstants.PROPERTY_WEBBINDING, webBinding);

//        serviceFunctionProperties.put(IEGLConstants.PROPERTY_WSDL, wsdl);

//        basicInterfaceProperties.put(IEGLConstants.PROPERTY_WSDL, wsdl);

//        javaObjectInterfaceProperties.put(IEGLConstants.PROPERTY_WSDL, wsdl);
        javaObjectInterfaceProperties.put(IEGLConstants.PROPERTY_JAVANAME, javaName);
        javaObjectInterfaceProperties.put(IEGLConstants.PROPERTY_PACKAGENAME, packageName);

//        basicAbstractFunctionProperties.put(IEGLConstants.PROPERTY_WSDL, wsdl);

//        javaOnlyAbstractFunctionProperties.put(IEGLConstants.PROPERTY_WSDL, wsdl);
        javaOnlyAbstractFunctionProperties.put(IEGLConstants.PROPERTY_JAVANAME, javaName);

//  	locationLibrary = libraryProperties
        libraryProperties.put(IEGLConstants.PROPERTY_ALIAS, alias);
        libraryProperties.put(IEGLConstants.PROPERTY_ALLOWUNQUALIFIEDITEMREFERENCES , unqualifiedItems);
        libraryProperties.put(IEGLConstants.PROPERTY_INCLUDEREFERENCEDFUNCTIONS , includeFunctions);
        libraryProperties.put(IEGLConstants.PROPERTY_MSGTABLEPREFIX, msgTablePrefix);
        libraryProperties.put(IEGLConstants.PROPERTY_LOCALSQLSCOPE, localSQLScope);
        libraryProperties.put(IEGLConstants.PROPERTY_HANDLEHARDIOERRORS, handleHardIOErrors);
        libraryProperties.put(IEGLConstants.PROPERTY_THROWNRFEOFEXCEPTIONS, throwNrfEofExceptions);
        libraryProperties.put(IEGLConstants.PROPERTY_V60EXCEPTIONCOMPATIBILITY, v60ExceptionCompatibility);

//		locationServiceBindignLibrary
        serviceBindingLibraryProperteis.put(IEGLConstants.PROPERTY_ALIAS, alias );
        
//		locationNativeLibrary
        nativeLibraryProperties.put(IEGLConstants.PROPERTY_ALIAS, alias);
        nativeLibraryProperties.put(IEGLConstants.PROPERTY_ALLOWUNQUALIFIEDITEMREFERENCES , unqualifiedItems);
        nativeLibraryProperties.put(IEGLConstants.PROPERTY_INCLUDEREFERENCEDFUNCTIONS , includeFunctions);
        nativeLibraryProperties.put(IEGLConstants.PROPERTY_MSGTABLEPREFIX, msgTablePrefix);
        nativeLibraryProperties.put(IEGLConstants.PROPERTY_CALLINGCONVENTION, callingConvention);
        nativeLibraryProperties.put(IEGLConstants.PROPERTY_DLLNAME, dllName);
        
        handlerProperties.put(IEGLConstants.PROPERTY_LOCALSQLSCOPE, localSQLScope);
        handlerProperties.put(IEGLConstants.PROPERTY_HANDLEHARDIOERRORS, handleHardIOErrors);
        handlerProperties.put(IEGLConstants.PROPERTY_THROWNRFEOFEXCEPTIONS, throwNrfEofExceptions);
        handlerProperties.put(IEGLConstants.PROPERTY_V60EXCEPTIONCOMPATIBILITY, v60ExceptionCompatibility);

//      locationFunction = functionProperties
        functionProperties.put(IEGLConstants.PROPERTY_CONTAINERCONTEXTDEPENDENT, containerContextDependent);

//		locationNativeLibraryFunction = nativeLibraryFunctionProperties
//									+ functionProperties
        nativeLibraryFunctionProperties.put(IEGLConstants.PROPERTY_ALIAS, alias);
        
        dictionaryProperties.put(IEGLConstants.PROPERTY_CASESENSITIVE, caseSensitive);
        dictionaryProperties.put(IEGLConstants.PROPERTY_ORDERING, ordering);
        
        consoleFormProperties.put(IEGLConstants.PROPERTY_NAME, name);
        consoleFormProperties.put(IEGLConstants.PROPERTY_FORMSIZE, formSize);
        consoleFormProperties.put(IEGLConstants.PROPERTY_SHOWBRACKETS, showBrackets);
        consoleFormProperties.put(IEGLConstants.PROPERTY_DELIMITERS, delimiters);
        
        consoleFieldProperties.put(IEGLConstants.PROPERTY_BINDING, binding);
        consoleFieldProperties.put(IEGLConstants.PROPERTY_DATATYPE, dataType);
        consoleFieldProperties.put(IEGLConstants.PROPERTY_FIELDLEN, fieldLen);
        consoleFieldProperties.put(IEGLConstants.PROPERTY_POSITION, position);
        consoleFieldProperties.put(IEGLConstants.PROPERTY_SEGMENTS, segments);
        consoleFieldProperties.put(IEGLConstants.PROPERTY_VALIDVALUES, validValues);
        
        consoleArrayFieldProperties.put(IEGLConstants.PROPERTY_COLUMNS, columns);
        consoleArrayFieldProperties.put(IEGLConstants.PROPERTY_LINESBETWEENROWS, linesBetweenRows);
        consoleArrayFieldProperties.put(IEGLConstants.PROPERTY_SPACESBETWEENCOLUMNS, spacesBetweenColumns);
        consoleArrayFieldProperties.put(IEGLConstants.PROPERTY_ORIENTINDEXACROSS, orientIndexAcross);
               
        complexDataItemProperties.put(IEGLConstants.PROPERTY_PROGRAMLINKDATA, programLinkData );
        
        complexItemDeclarationProperties.put(IEGLConstants.PROPERTY_PROGRAMLINKDATA, programLinkData );
        
        complexProgramProperties.put(IEGLConstants.PROPERTY_RELATIONSHIP, relationship );
        
//        complexFunctionProperties.put(IEGLConstants.PROPERTY_WSDL, wsdl );
        
//        complexServiceProperties.put(IEGLConstants.PROPERTY_WSDL, wsdl );
        
//        complexInterfaceProperties.put(IEGLConstants.PROPERTY_WSDL, wsdl );
        
//        complexAbstractFunctionProperties.put(IEGLConstants.PROPERTY_WSDL, wsdl );

        linkParameterProperties.put(IEGLConstants.PROPERTY_LINKPARAMETER, linkParameter);
        linkParmsProperties.put(IEGLConstants.PROPERTY_LINKPARMS, linkParms);

        hierarchyProperties.put(IEGLConstants.PROPERTY_HIERARCHY, hierarchy);
        relationshipProperties.put(IEGLConstants.PROPERTY_RELATIONSHIP, relationship);
       }

  /**
   * @return
   */
  public static String[] getAllPropertyNames() {
          return propertyNames;
  }

  /**
   * return the List of all EGL property names in lowercase
   */
  public static String[] getAllPropertyNamesToLowerCase() {
          String[] lowercasePropertyNames = new String[propertyNames.length];
          for (int i = 0; i < propertyNames.length; i++) {
                  lowercasePropertyNames[i] = propertyNames[i].toLowerCase();
          }
          return lowercasePropertyNames;
  }

  /**
   * return the List of all EGL property names in lowercase
   */
  public static ArrayList getAllPropertyNamesToLowerCaseAsArrayList() {
          ArrayList lowercasePropertyNames = new ArrayList();
          for (int i = 0; i < propertyNames.length; i++) {
                  lowercasePropertyNames.add(propertyNames[i].toLowerCase());
          }
          return lowercasePropertyNames;
  }

  public static List getNamesFromValues(ArrayList propertyValues){
          ArrayList propertyNames = new ArrayList(propertyValues.size() );

          Iterator iter = propertyValues.iterator();
          for (int i = 0; i < propertyValues.size(); i++) {
                  EGLPropertyRule property = (EGLPropertyRule) iter.next();
                  propertyNames.add(property.getName());
          }
          return propertyNames;
  }

  public static List getNamesFromValuesToLowerCase(ArrayList propertyValues){
          ArrayList propertyNames = new ArrayList(propertyValues.size() );

          Iterator iter = propertyValues.iterator();
          for (int i = 0; i < propertyValues.size(); i++) {
                  EGLPropertyRule property = (EGLPropertyRule) iter.next();
                  propertyNames.add(property.getName().toLowerCase());
          }
          return propertyNames;
  }


  public static String getLocation(int location) {
            switch (location) {
                    //FRIEDA get rid of hard coded strings
                    case locationDataItem :
                                    return IEGLConstants.KEYWORD_DATAITEM;
                    case locationStructureItem :
                                    return "structureItem";
                    case locationFillerStructureItem :
                        			return "filler structureItem";
                    case locationPsbRecordItem :
                    				return "PSBRecord item";
                    case locationBasicRecord :
                                    return IEGLConstants.RECORD_SUBTYPE_BASIC;
                    case locationIndexedRecord :
                                    return IEGLConstants.RECORD_SUBTYPE_INDEXED;
                    case locationRelativeRecord :
                                    return IEGLConstants.RECORD_SUBTYPE_RELATIVE;
                    case locationSerialRecord :
                                    return IEGLConstants.RECORD_SUBTYPE_SERIAL;
                    case locationMQRecord :
                                    return IEGLConstants.RECORD_SUBTYPE_MQ;
                    case locationSQLRecord :
                                    return IEGLConstants.RECORD_SUBTYPE_SQl;
                    case locationAnyRecord :
                                    return IEGLConstants.KEYWORD_RECORD;
                    case locationFormGroup :
                                    return IEGLConstants.KEYWORD_FORMGROUP;
                    case locationScreenFloatArea :
                                    return "screenFloatingArea";
                    case locationPrintFloatArea :
                                    return "printFloatingArea";
                    case locationTextConstantFormField :
                                    return "textForm constant field";
                    case locationPrintConstantFormField :
                                    return "printForm constant field";
                    case locationTextVariableFormField :
                                    return "textForm variable field";
                    case locationTuiTextVariableFormField :
                        return "TUI text variable field";
                    case locationTuiPrintVariableFormField :
                        return "TUI print variable field";
                    case locationPrintVariableFormField :
                                    return "printForm variable field";
                    case locationTextFormDeclaration :
                                    return IEGLConstants.FORM_SUBTYPE_TEXT;
                    case locationPrintFormDeclaration :
                                    return IEGLConstants.FORM_SUBTYPE_PRINT;
                    case locationDataTable :
                                    return IEGLConstants.KEYWORD_DATATABLE;
                    case locationFunction :
                                    return IEGLConstants.KEYWORD_FUNCTION;
                    case locationNativeLibraryFunction :
                    				return "nativeLibrary function";
                    case locationProgram :
                                    return IEGLConstants.KEYWORD_PROGRAM;
					case locationVGWebTransaction :
									return IEGLConstants.PROGRAM_SUBTYPE_VG_WEB_TRANSACTION;                                                
					case locationBasicProgram :
									return IEGLConstants.PROGRAM_SUBTYPE_BASIC;                                                
					case locationCalledBasicProgram :
									return "called basic program";                                                
					case locationTextUIProgram :
									return IEGLConstants.PROGRAM_SUBTYPE_TEXT_UI;                                                
					case locationCalledTextUIProgram :
									return "called text UI program";                                                
                    case locationLibrary :
                                    return "basic library";
                    case locationServiceBindingLibrary :
                    				return "service binding library";
                    case locationNativeLibrary :
                    				return "native library";
                    case locationStaticBasicRecordDataDeclaration :
                                    return "static basic record data declaration";
                    case locationStaticIndexedRecordDataDeclaration :
                                    return "static indexed record data declaration";
                    case locationStaticRelativeRecordDataDeclaration :
                                    return "static relative record data declaration";
                    case locationStaticSerialRecordDataDeclaration :
                                    return "static serial record data declaration";
                    case locationStaticMQRecordDataDeclaration :
                                    return "static MQ record data declaration";
                    case locationStaticSQLRecordDataDeclaration :
                                    return "static SQL record data declaration";
                    case locationStaticVGUIRecordDataDeclaration :
                                    return "static UI record data declaration";
                    case locationStaticAnyRecordDataDeclaration :
                                   return "static record data declaration";
                    case locationDynamicBasicRecordDataDeclaration :
                                   return "dynamic basic record data declaration";
                    case locationDynamicIndexedRecordDataDeclaration :
                                   return "dynamic indexed record data declaration";
                    case locationDynamicRelativeRecordDataDeclaration :
                                    return "dynamic relative record data declaration";
                    case locationDynamicSerialRecordDataDeclaration :
                                    return "dynamic serial record data declaration";
                    case locationDynamicMQRecordDataDeclaration :
                                    return "dynamic MQ record data declaration";
                    case locationDynamicSQLRecordDataDeclaration :
                                    return "dynamic SQL record data declaration";
                    case locationDynamicVGUIRecordDataDeclaration :
                                    return "dynamic UI record data declaration";
                    case locationDynamicAnyRecordDataDeclaration :
                                    return "dynamic record data declaration";
                    case locationStaticItemDataDeclaration :
                                    return "static item data declaration";
                    case locationDynamicItemDataDeclaration :
                          			return "dynamic item data declaration";
                    case locationUseDeclaration :
                                    return "use declaration";
                    case locationDataTableUseDeclaration :
                                    return "data table use declaration";
                    case locationFormGroupUseDeclaration :
                                    return "form group use declaration";
                    case locationLibraryUseDeclaration :
                                    return "library use declaration";
                    case locationFormUseDeclaration :
                                    return "form use declaration";
					case locationStaticPageItemDataDeclaration :
									return "static page item data declaration";                                               
					case locationDynamicPageItemDataDeclaration :
									return "dynamic page item data declaration";                                      
					case locationDictionary :
									return "dictionary";                                                
					case locationConsoleForm :
									return "console form";                                                
					case locationConsoleField :
									return "console field";                                                
					case locationConsoleArrayField :
									return "console array field";                                                
					case locationWindow :
									return "window";                                                
					case locationPresentationAttributes :
									return "presentation attributes";                                                
					case locationMenu :
									return "menu";                                                
					case locationMenuItem :
									return "menu item";                                                
					case locationPrompt :
									return "prompt";                                                
					case locationOpenUI :
									return "openUI";
					case locationTuiArrayElementFormField:
									return "array element form field properties";
                    case locationBasicAbstractFunction :
                    case locationJavaOnlyAbstractFunction :
                    				return "abstract function";
                    case locationService :
        							return IEGLConstants.KEYWORD_SERVICE.toLowerCase();
                    case locationBasicInterface :
                    case locationJavaObjectInterface :
                    				return IEGLConstants.KEYWORD_INTERFACE.toLowerCase();
                    case locationServiceDeclaration :
                    				return "service declaration";
                    case locationInterfaceDeclaration :
        							return "interface declaration";
        			case locationHandler :
        							return IEGLConstants.KEYWORD_HANDLER;
                    default :
                                    return null;
                }
  }

  public static List getPropertyNames(int location) {
            switch (location) {
                    case locationDataItem :
                                    return getDataItemPropertyNames();
                    case locationStructureItem :
                                    return getStructureItemPropertyNames();
                    case locationBasicRecord :
                                    return getBasicRecordPropertyNames();
                    case locationIndexedRecord :
                                    return getIndexedRecordPropertyNames();
                    case locationRelativeRecord :
                                    return getRelativeRecordPropertyNames();
                    case locationSerialRecord :
                                    return getSerialRecordPropertyNames();
                    case locationMQRecord :
                                    return getMQRecordPropertyNames();
                    case locationSQLRecord :
                                    return getSQLRecordPropertyNames();
                    case locationVGUIRecord :
                                    return getVGUIRecordPropertyNames();
                    case locationAnyRecord :
                                    return getAnyRecordPropertyNames();
                    case locationFormGroup :
                                    return getFormGroupPropertyNames();
                    case locationScreenFloatArea :
                                    return getScreenFloatingAreaPropertyNames();
                    case locationPrintFloatArea :
                                    return getPrintFloatingAreaPropertyNames();
                    case locationTextConstantFormField :
                                    return getTextConstantFormFieldPropertyNames();
                    case locationPrintConstantFormField :
                                    return getPrintConstantFormFieldPropertyNames();
                    case locationTextVariableFormField :
                                    return getTextVariableFormFieldPropertyNames();
                    case locationTuiTextVariableFormField :
                    				return getTuiTextVariableFormFieldPropertyNames();
                    case locationTuiPrintVariableFormField :
                    			return getTuiPrintVariableFormFieldPropertyNames();
                    case locationPrintVariableFormField :
                                    return getPrintVariableFormFieldPropertyNames();
                    case locationTextFormDeclaration :
                                    return getTextFormPropertyNames();
                    case locationPrintFormDeclaration :
                                    return getPrintFormPropertyNames();
                    case locationPageHandlerDeclaration :
                                    return getPageHandlerPropertyNames();
                    case locationDataTable :
                                    return getDataTablePropertyNames();
                    case locationFunction :
                                    return getFunctionPropertyNames();
                    case locationNativeLibraryFunction :
                    				return getNativeLibraryFunctionPropertyNames();
                    case locationProgram :
                                    return getAllProgramPropertyNames();
					case locationVGWebTransaction :
									return getVGWebTransactionPropertyNames();                                                
					case locationBasicProgram :
									return getBasicProgramPropertyNames();                                               
					case locationCalledBasicProgram :
									return getCalledBasicProgramPropertyNames();                                                
					case locationTextUIProgram :
									return getTextUIProgramPropertyNames();                                                
					case locationCalledTextUIProgram :
									return getCalledTextUIProgramPropertyNames();                                                
                    case locationLibrary :
                                    return getlibraryPropertyNames();
                    case locationServiceBindingLibrary :
                        			return getServiceBindingLibraryPropertyNames();                                  
                    case locationNativeLibrary :
                    				return getNativeLibraryPropertyNames();
                    case locationStaticBasicRecordDataDeclaration :
                                    return getStaticBasicRecordDataDeclarationPropertyNames();
                    case locationStaticIndexedRecordDataDeclaration :
                                    return getStaticIndexedRecordDataDeclarationPropertyNames();
                    case locationStaticRelativeRecordDataDeclaration :
                                    return getStaticRelativeRecordDataDeclarationPropertyNames();
                    case locationStaticSerialRecordDataDeclaration :
                                    return getStaticSerialRecordDataDeclarationPropertyNames();
                    case locationStaticMQRecordDataDeclaration :
                                    return getStaticMQRecordDataDeclarationPropertyNames();
                    case locationStaticSQLRecordDataDeclaration :
                                    return getStaticSQLRecordDataDeclarationPropertyNames();
                    case locationStaticVGUIRecordDataDeclaration :
                                    return getStaticVGUIRecordDataDeclarationPropertyNames();
                    case locationStaticAnyRecordDataDeclaration :
                           			return getStaticAnyRecordDataDeclarationPropertyNames();
                    case locationDynamicBasicRecordDataDeclaration :
                                    return getDynamicBasicRecordDataDeclarationPropertyNames();
                    case locationDynamicIndexedRecordDataDeclaration :
                                    return getDynamicIndexedRecordDataDeclarationPropertyNames();
                    case locationDynamicRelativeRecordDataDeclaration :
                                    return getDynamicRelativeRecordDataDeclarationPropertyNames();
                    case locationDynamicSerialRecordDataDeclaration :
                                    return getDynamicSerialRecordDataDeclarationPropertyNames();
                    case locationDynamicMQRecordDataDeclaration :
                                    return getDynamicMQRecordDataDeclarationPropertyNames();
                    case locationDynamicSQLRecordDataDeclaration :
                                    return getDynamicSQLRecordDataDeclarationPropertyNames();
                    case locationDynamicVGUIRecordDataDeclaration :
                                    return getDynamicVGUIRecordDataDeclarationPropertyNames();
                    case locationDynamicAnyRecordDataDeclaration :
                       			    return getDynamicAnyRecordDataDeclarationPropertyNames();
                    case locationStaticItemDataDeclaration :
                           			return getStaticItemDataDeclarationPropertyNames();
                    case locationDynamicItemDataDeclaration :
                         			return getDynamicItemDataDeclarationPropertyNames();
                    case locationUseDeclaration :
                                    return getUseDeclarationPropertyNames();
                    case locationDataTableUseDeclaration :
                                    return getDataTableUseDeclarationPropertyNames();
                    case locationFormGroupUseDeclaration :
                                    return getFormGroupUseDeclarationPropertyNames();
                    case locationLibraryUseDeclaration :
                                    return getLibraryUseDeclarationPropertyNames();
                    case locationFormUseDeclaration :
                                    return getFormUseDeclarationPropertyNames();
					case locationStaticPageItemDataDeclaration :
									return getStaticPageItemDataDeclarationPropertyNames();
					case locationDynamicPageItemDataDeclaration :
									return getDynamicPageItemDataDeclarationPropertyNames();									                                                
					case locationDictionary :
									return getDictionaryPropertyNames();									                                                
					case locationConsoleForm :
									return getConsoleFormPropertyNames();									                                                
					case locationConsoleField :
									return getConsoleFieldPropertyNames();									                                                
					case locationConsoleArrayField :
									return getConsoleArrayFieldPropertyNames();									                                                
					case locationWindow :
									return getWindowPropertyNames();                                                
					case locationPresentationAttributes :
									return getPresentationAttributesPropertyNames();                                                
					case locationMenu :
									return getMenuPropertyNames();                                                
					case locationMenuItem :
									return getMenuItemPropertyNames();                                                
					case locationPrompt :
									return getPromptPropertyNames();                                                
					case locationTuiArrayElementFormField:
									return getTuiArrayElementFormFieldPropertyNames();
					case locationPSBRecord :
									return getPSBRecordPropertyNames();
					case locationDLISegment :
									return getDLISegmentPropertyNames();
                   default :
                                    return null;
                }
  }

  public static List getPropertyNamesToLowerCase(int location) {
            switch (location) {
                    case locationDataItem :
                                    return getDataItemPropertyNamesToLowerCase();
                    case locationStructureItem :
                                    return getStructureItemPropertyNamesToLowerCase();
                    case locationBasicRecord :
                                    return getBasicRecordPropertyNamesToLowerCase();
                    case locationIndexedRecord :
                                    return getIndexedRecordPropertyNamesToLowerCase();
                    case locationRelativeRecord :
                                    return getRelativeRecordPropertyNamesToLowerCase();
                    case locationSerialRecord :
                                    return getSerialRecordPropertyNamesToLowerCase();
                    case locationMQRecord :
                                    return getMQRecordPropertyNamesToLowerCase();
                    case locationSQLRecord :
                                    return getSQLRecordPropertyNamesToLowerCase();
					case locationVGUIRecord :
									return getVGUIRecordPropertyNamesToLowerCase();
					case locationAnyRecord :
									return getAnyRecordPropertyNamesToLowerCase();									                                                
                    case locationFormGroup :
                                    return getFormGroupPropertyNamesToLowerCase();
                    case locationScreenFloatArea :
                                    return getScreenFloatingAreaPropertyNamesToLowerCase();
                    case locationPrintFloatArea :
                                    return getPrintFloatingAreaPropertyNamesToLowerCase();
                    case locationTextConstantFormField :
                                    return getTextConstantFormFieldPropertyNamesToLowerCase();
                    case locationPrintConstantFormField :
                                    return getPrintConstantFormFieldPropertyNamesToLowerCase();
                    case locationTextVariableFormField :
                                    return getTextVariableFormFieldPropertyNamesToLowerCase();
                    case locationTuiTextVariableFormField :
                    				return getTuiTextVariableFormFieldPropertyNamesToLowerCase();
                    case locationTuiPrintVariableFormField :
                    				return getTuiPrintVariableFormFieldPropertyNamesToLowerCase();
                    case locationPrintVariableFormField :
                                    return getPrintVariableFormFieldPropertyNamesToLowerCase();
                    case locationTextFormDeclaration :
                                    return getTextFormPropertyNamesToLowerCase();
                    case locationPrintFormDeclaration :
                                    return getPrintFormPropertyNamesToLowerCase();
                    case locationPageHandlerDeclaration :
                                    return getPageHandlerPropertyNamesToLowerCase();
                    case locationDataTable :
                                    return getDataTablePropertyNamesToLowerCase();
                    case locationFunction :
                                    return getFunctionPropertyNamesToLowerCase();
                    case locationNativeLibraryFunction :
        							return getNativeLibraryFunctionPropertyNamesToLowerCase();
                    case locationProgram :
                                    return getAllProgramPropertyNamesToLowerCase();
					case locationVGWebTransaction :
									return getVGWebTransactionPropertyNamesToLowerCase();                                               
					case locationBasicProgram :
									return getBasicProgramPropertyNamesToLowerCase();                                                
					case locationCalledBasicProgram :
									return getCalledBasicProgramPropertyNamesToLowerCase();                                                
					case locationTextUIProgram :
									return getTextUIProgramPropertyNamesToLowerCase();                                                
					case locationCalledTextUIProgram :
									return getCalledTextUIProgramPropertyNamesToLowerCase();                                                
                    case locationLibrary :
                                    return getlibraryPropertyNamesToLowerCase();
                    case locationNativeLibrary :
        							return getNativeLibraryPropertyNamesToLowerCase();
                    case locationStaticBasicRecordDataDeclaration :
                                    return getStaticBasicRecordDataDeclarationPropertyNamesToLowerCase();
                    case locationStaticIndexedRecordDataDeclaration :
                                    return getStaticIndexedRecordDataDeclarationPropertyNamesToLowerCase();
                    case locationStaticRelativeRecordDataDeclaration :
                                    return getStaticRelativeRecordDataDeclarationPropertyNamesToLowerCase();
                    case locationStaticSerialRecordDataDeclaration :
                                    return getStaticSerialRecordDataDeclarationPropertyNamesToLowerCase();
                    case locationStaticMQRecordDataDeclaration :
                                    return getStaticMQRecordDataDeclarationPropertyNamesToLowerCase();
                    case locationStaticSQLRecordDataDeclaration :
                                    return getStaticSQLRecordDataDeclarationPropertyNamesToLowerCase();
                    case locationStaticVGUIRecordDataDeclaration :
                                    return getStaticVGUIRecordDataDeclarationPropertyNamesToLowerCase();
                    case locationStaticAnyRecordDataDeclaration :
                                    return getStaticAnyRecordDataDeclarationPropertyNamesToLowerCase();
                    case locationDynamicBasicRecordDataDeclaration :
                                    return getDynamicBasicRecordDataDeclarationPropertyNamesToLowerCase();
                    case locationDynamicIndexedRecordDataDeclaration :
                                    return getDynamicIndexedRecordDataDeclarationPropertyNamesToLowerCase();
                    case locationDynamicRelativeRecordDataDeclaration :
                                    return getDynamicRelativeRecordDataDeclarationPropertyNamesToLowerCase();
                    case locationDynamicSerialRecordDataDeclaration :
                                    return getDynamicSerialRecordDataDeclarationPropertyNamesToLowerCase();
                    case locationDynamicMQRecordDataDeclaration :
                                    return getDynamicMQRecordDataDeclarationPropertyNamesToLowerCase();
                    case locationDynamicSQLRecordDataDeclaration :
                                    return getDynamicSQLRecordDataDeclarationPropertyNamesToLowerCase();
                    case locationDynamicVGUIRecordDataDeclaration :
                                    return getDynamicVGUIRecordDataDeclarationPropertyNamesToLowerCase();
                    case locationDynamicAnyRecordDataDeclaration :
                                    return getDynamicAnyRecordDataDeclarationPropertyNamesToLowerCase();
                    case locationStaticItemDataDeclaration :
                                    return getStaticItemDataDeclarationPropertyNamesToLowerCase();
                    case locationDynamicItemDataDeclaration :
                                    return getDynamicItemDataDeclarationPropertyNamesToLowerCase();
                    case locationUseDeclaration :
                                    return getUseDeclarationPropertyNamesToLowerCase();
                    case locationDataTableUseDeclaration :
                                    return getDataTableUseDeclarationPropertyNamesToLowerCase();
                    case locationFormGroupUseDeclaration :
                                    return getFormGroupUseDeclarationPropertyNamesToLowerCase();
                    case locationLibraryUseDeclaration :
                                    return getLibraryUseDeclarationPropertyNamesToLowerCase();
                    case locationFormUseDeclaration :
                                    return getFormUseDeclarationPropertyNamesToLowerCase();
					case locationStaticPageItemDataDeclaration :
									return getStaticPageItemDataDeclarationPropertyNamesToLowerCase();
					case locationDynamicPageItemDataDeclaration :
									return getDynamicPageItemDataDeclarationPropertyNamesToLowerCase();									                                                
					case locationDictionary :
									return getDictionaryPropertyNamesToLowerCase();									                                                
					case locationConsoleForm :
									return getConsoleFormPropertyNamesToLowerCase();									                                                
					case locationConsoleField :
									return getConsoleFieldPropertyNamesToLowerCase();									                                                
					case locationConsoleArrayField :
									return getConsoleArrayFieldPropertyNamesToLowerCase();									                                                
					case locationWindow :
									return getWindowPropertyNamesToLowerCase();                                                
					case locationPresentationAttributes :
									return getPresentationAttributesPropertyNamesToLowerCase();                                                
					case locationMenu :
									return getMenuPropertyNamesToLowerCase();                                                
					case locationMenuItem :
									return getMenuItemPropertyNamesToLowerCase();                                                
					case locationPrompt :
									return getPromptPropertyNamesToLowerCase();                                                
					case locationTuiArrayElementFormField:
									return getTuiArrayElementFormFieldPropertyNamesToLowerCase();
					case locationPSBRecord :
									return getPSBRecordPropertyNamesToLowerCase();
					case locationDLISegment :
									return getDLISegmentPropertyNamesToLowerCase();
                    default :
                                    return null;
                }
  }
  
  
  /**
   * For the 6.0.1 implementation of "Annotations", which are identifiers preceded by an '@'
   * symbol and include a property block containing more properties or annotations.
   */
 public static ArrayList getComplexPropertyRules(int location) {
 	switch (location) {
 		case locationBasicRecord :
        case locationIndexedRecord :
        case locationRelativeRecord :
        case locationSerialRecord :
        case locationMQRecord :
        case locationSQLRecord :
        case locationVGUIRecord :
        case locationAnyRecord :
			return getRecordComplexPropertyPropertyRules(); 
			
		case locationPsbRecordItem :
			return getPsbRecordItemPropertyRules();
			
 		case locationStructureItem :
 		case locationStaticPageItemDataDeclaration :
 		case locationStaticBasicRecordDataDeclaration :
 		case locationStaticAnyRecordDataDeclaration :
 		case locationStaticSQLRecordDataDeclaration :
 		case locationStaticIndexedRecordDataDeclaration :
 		case locationStaticMQRecordDataDeclaration :
 		case locationStaticRelativeRecordDataDeclaration :
 		case locationStaticSerialRecordDataDeclaration :
 		case locationStaticVGUIRecordDataDeclaration :
 		case locationDynamicItemDataDeclaration :
 		case locationDynamicPageItemDataDeclaration :
 		case locationDynamicBasicRecordDataDeclaration :
 		case locationDynamicAnyRecordDataDeclaration :
 		case locationDynamicSQLRecordDataDeclaration :
 		case locationDynamicIndexedRecordDataDeclaration :
 		case locationDynamicMQRecordDataDeclaration :
 		case locationDynamicRelativeRecordDataDeclaration :
 		case locationDynamicSerialRecordDataDeclaration :
 		case locationDynamicVGUIRecordDataDeclaration :
 			return getItemDeclarationComplexPropertyPropertyRules();
		
 		case locationDataItem :
 			return getDataItemComplexPropertyPropertyRules();

 		case locationProgram :
 		case locationVGWebTransaction :
 		case locationBasicProgram :
 		case locationCalledBasicProgram :
 		case locationTextUIProgram :
 		case locationCalledTextUIProgram :
 			return getProgramComplexPropertyRules();
 			
 		case locationFunction :
 			return getFunctionComplexPropertyRules();
 			
 		case locationBasicAbstractFunction :
 			return getBasicAbstractFunctionComplexPropertyRules();
 
 		case locationJavaOnlyAbstractFunction :
 			return getJavaOnlyAbstractFunctionComplexPropertyRules();
 			
 		case locationService :
 			return getServiceComplexPropertyRules();
 			
 		case locationBasicInterface :
 			return getBasicInterfaceComplexPropertyRules();

 		case locationJavaObjectInterface :
 			return getJavaObjectInterfaceComplexPropertyRules();
 			
 		case locationServiceDeclaration :
 			return getServiceDeclarationPropertyRules();
 			
 		case locationInterfaceDeclaration :
 			return getInterfaceDeclarationPropertyRules();
}
 	return new ArrayList();
 }

  public static ArrayList getPropertyRules(int location) {
            switch (location) {
                    case locationDataItem :
                                    return getDataItemPropertyRules();
                    case locationStructureItem :
                                    return getStructureItemPropertyRules();                                    
                    case locationFillerStructureItem :
                        			return getFillerStructureItemPropertyRules();                        
                    case locationBasicRecord :
                                    return getBasicRecordPropertyRules();
                    case locationIndexedRecord :
                                    return getIndexedRecordPropertyRules();
                    case locationRelativeRecord :
                                    return getRelativeRecordPropertyRules();
                    case locationSerialRecord :
                                    return getSerialRecordPropertyRules();
                    case locationMQRecord :
                                    return getMQRecordPropertyRules();
                    case locationSQLRecord :
                                    return getSQLRecordPropertyRules();
                    case locationVGUIRecord :
                       			    return getVGUIRecordPropertyRules();
					case locationAnyRecord :
									return getAnyRecordPropertyRules();                                      
                    case locationFormGroup :
                                    return getFormGroupPropertyRules();
                    case locationScreenFloatArea :
                                    return getScreenFloatingAreaPropertyRules();
                    case locationPrintFloatArea :
                                    return getPrintFloatingAreaPropertyRules();
                    case locationTextConstantFormField :
                                    return getTextConstantFormFieldPropertyRules();
                    case locationPrintConstantFormField :
                                    return getPrintConstantFormFieldPropertyRules();
                    case locationTextVariableFormField :
                                    return getTextVariableFormFieldPropertyRules();
                    case locationTuiTextVariableFormField :
                    				return getTuiTextVariableFormFieldPropertyRules();
                    case locationTuiPrintVariableFormField :
                    				return getTuiPrintVariableFormFieldPropertyRules();
                    case locationPrintVariableFormField :
                                    return getPrintVariableFormFieldPropertyRules();
                    case locationTextFormDeclaration :
                                    return getTextFormPropertyRules();
                    case locationPrintFormDeclaration :
                                    return getPrintFormPropertyRules();
                    case locationPageHandlerDeclaration :
                                    return getPageHandlerPropertyRules();
                    case locationDataTable :
                                    return getDataTablePropertyRules();
                    case locationFunction :
                                    return getFunctionPropertyRules();
                    case locationNativeLibraryFunction :
        							return getNativeLibraryFunctionPropertyRules();
                    case locationProgram :
                                    return getAllProgramPropertyRules();
					case locationVGWebTransaction :
									return getVGWebTransactionPropertyRules();                                                
					case locationBasicProgram :
									return getBasicProgramPropertyRules();                                                
					case locationCalledBasicProgram :
									return getCalledBasicProgramPropertyRules();                                                
					case locationTextUIProgram :
									return getTextUIProgramPropertyRules();                                                
					case locationCalledTextUIProgram :
									return getCalledTextUIProgramPropertyRules();                                                
                    case locationLibrary :
                                    return getlibraryPropertyRules();
//                    case locationServiceBindingLibrary :
//                        			return getServiceBindingLibraryPropertyRules();
                    case locationNativeLibrary :
        							return getNativeLibraryPropertyRules();
                    case locationStaticBasicRecordDataDeclaration :
                                    return getStaticBasicRecordDataDeclarationPropertyRules();
                    case locationStaticIndexedRecordDataDeclaration :
                                    return getStaticIndexedRecordDataDeclarationPropertyRules();
                    case locationStaticRelativeRecordDataDeclaration :
                                    return getStaticRelativeRecordDataDeclarationPropertyRules();
                    case locationStaticSerialRecordDataDeclaration :
                                    return getStaticSerialRecordDataDeclarationPropertyRules();
                    case locationStaticMQRecordDataDeclaration :
                                    return getStaticMQRecordDataDeclarationPropertyRules();
                    case locationStaticSQLRecordDataDeclaration :
                                    return getStaticSQLRecordDataDeclarationPropertyRules();
                    case locationStaticVGUIRecordDataDeclaration :
                                    return getStaticVGUIRecordDataDeclarationPropertyRules();
                    case locationStaticAnyRecordDataDeclaration :
                                    return getStaticAnyRecordDataDeclarationPropertyRules();
                    case locationDynamicBasicRecordDataDeclaration :
                                    return getDynamicBasicRecordDataDeclarationPropertyRules();
                    case locationDynamicIndexedRecordDataDeclaration :
                                    return getDynamicIndexedRecordDataDeclarationPropertyRules();
                    case locationDynamicRelativeRecordDataDeclaration :
                                    return getDynamicRelativeRecordDataDeclarationPropertyRules();
                    case locationDynamicSerialRecordDataDeclaration :
                                    return getDynamicSerialRecordDataDeclarationPropertyRules();
                    case locationDynamicMQRecordDataDeclaration :
                                    return getDynamicMQRecordDataDeclarationPropertyRules();
                    case locationDynamicSQLRecordDataDeclaration :
                                    return getDynamicSQLRecordDataDeclarationPropertyRules();
                    case locationDynamicVGUIRecordDataDeclaration :
                                    return getDynamicVGUIRecordDataDeclarationPropertyRules();
                    case locationDynamicAnyRecordDataDeclaration :
                                    return getDynamicAnyRecordDataDeclarationPropertyRules();
                    case locationStaticItemDataDeclaration :
                                    return getStaticItemDataDeclarationPropertyRules();
                    case locationDynamicItemDataDeclaration :
                                    return getDynamicItemDataDeclarationPropertyRules();
                    case locationUseDeclaration :
                                    return getUseDeclarationPropertyRules();
                    case locationDataTableUseDeclaration :
                                    return getDataTableUseDeclarationPropertyRules();
                    case locationFormGroupUseDeclaration :
                                    return getFormGroupUseDeclarationPropertyRules();
                    case locationLibraryUseDeclaration :
                                    return getLibraryUseDeclarationPropertyRules();
                    case locationFormUseDeclaration :
                                    return getFormUseDeclarationPropertyRules();
					case locationStaticPageItemDataDeclaration :
									return getStaticPageItemDataDeclarationPropertyRules();
					case locationDynamicPageItemDataDeclaration :
									return getDynamicPageItemDataDeclarationPropertyRules();	
						
					case locationFormatting :
						return getDataItemFormattingPropertyRules();
					case locationSqlItem :
						return getDataItemSQLItemPropertyRules();
					case locationPageItem :
						return getDataItemPageItemPropertyRules();
					case locationUIItem :
						return getDataItemUIItemPropertyRules();
					case locationSAUIItem:
					    return getDataItemUIItemSAPropertyRules();
					case locationSATUIItem:
					    return getDataItemTUISAPropertyRules();
					case locationDL1Item:
					    return getDataItemDL1ItemPropertyRules();
					case locationPsbRecordItem :
						return getPsbRecordItemPropertyRules();
					case locationValidation :
						return getDataItemValidationPropertyRules();
					case locationFieldPresentation :
						return getDataItemFieldPresentationPropertyRules();
					case locationTuiFieldPresentation :
						return getTuiFieldPresentationPropertyRules();
					case locationDoubleByteDevicePresentation :
						return getDataItemDoubleByteDevicePresentationPropertyRules();
					case locationVariableField :
						return getDataItemVariableFieldPropertyRules();
					case locationItemFormField :
						return getDataItemItemFormFieldPropertyRules();
					case locationDictionary :
						return getDictionaryPropertyRules();
					case locationConsoleForm :
						return getConsoleFormPropertyRules();
					case locationConsoleField :
						return getConsoleFieldPropertyRules();
					case locationConsoleArrayField :
						return getConsoleArrayFieldPropertyRules();
					case locationWindow :
						return getWindowPropertyRules();                                                
					case locationPresentationAttributes :
						return getPresentationAttributesPropertyRules();                                                
					case locationMenu :
						return getMenuPropertyRules();                                                
					case locationMenuItem :
						return getMenuItemPropertyRules();                                                
					case locationPrompt :
						return getPromptPropertyRules();                                                
					case locationCommonVariableFormField :
						return getCommonVariableFormFieldPropertyRules();
					case locationFormField :
						return getFormFieldPropertyRules();
					case locationTuiArrayElementFormField:
						return getTuiArrayElementFormFieldPropertyRules();
					case locationPSBRecord :
						return getPSBRecordPropertyRules();
					case locationDLISegment :
						return getDLISegmentPropertyRules();					
					case locationService :
						return getServicePropertyRules();
					case locationServiceDeclaration :
						return getServiceDeclarationPropertyRules();
					case locationServiceFunction :
						return getServiceFunctionPropertyRules();
					case locationBasicInterface :
						return getBasicInterfacePropertyRules();
					case locationJavaObjectInterface :
						return getJavaObjectInterfacePropertyRules();
					case locationInterfaceDeclaration :
						return getInterfaceDeclarationPropertyRules();
					case locationBasicAbstractFunction :
						return getBasicAbstractFunctionPropertyRules();
					case locationJavaOnlyAbstractFunction :
						return getJavaOnlyAbstractFunctionPropertyRules();
					case locationLinkParms :
						return getLinkParmsPropertyRules();
					case locationPcbParms :
						return getPcbParmsPropertyRules();
					case locationProgramLinkData :
						return getComplexAnnotationPropertyRules(uiItemProperties, IEGLConstants.PROPERTY_PROGRAMLINKDATA);
					case locationLinkParameter :
						return getComplexAnnotationPropertyRules(linkParameterProperties, IEGLConstants.PROPERTY_LINKPARAMETER);
					case locationRelationship :
						return getComplexAnnotationPropertyRules(complexProgramProperties, IEGLConstants.PROPERTY_RELATIONSHIP);
					case locationEGLBinding :
						return getComplexAnnotationPropertyRules(serviceDeclarationProperties, IEGLConstants.PROPERTY_EGLBINDING);
					case locationWebBinding :
						return getComplexAnnotationPropertyRules(serviceDeclarationProperties, IEGLConstants.PROPERTY_WEBBINDING);
					case locationHandler :
						return getAllHandlerPropertyRules();
                    default :
                       return null;
                }
  }

private static ArrayList getComplexAnnotationPropertyRules(TreeMap properties, String propertyName) {
	EGLPropertyRule rule = (EGLPropertyRule) properties.get(propertyName);
	EGLPropertyRule rules[] = rule.getElementAnnotationTypes();
	ArrayList result = new ArrayList(rules.length);
	for (int i = 0; i < rules.length; i++) {
		EGLPropertyRule rule2 = rules[i];
		result.add(rule2);
	}
	return result;
}

/**
   * return the List of EGL property names valid for a DataItem
   */
  public static List getDataItemPropertyNames() {
          ArrayList propertyValues = getDataItemPropertyRules();
          return getNamesFromValues(propertyValues);
  }
  /**
   * return the List of EGL property names valid for a DataItem
   */
  public static List getDataItemPropertyNamesToLowerCase() {
          ArrayList propertyValues = getDataItemPropertyRules();
          return getNamesFromValuesToLowerCase(propertyValues);
  }
  /**
   * return the ArrayList of EGL property rules valid for a DataItem
   */
  public static ArrayList getDataItemPropertyRules() {
		TreeMap map = new TreeMap(sqlItemProperties);
		map.putAll(pageItemProperties);
		map.putAll(uiItemProperties);
		map.putAll(formattingProperties);
		map.putAll(validationProperties);
		map.putAll(fieldPresentationProperties);
		map.putAll(doubleByteDevicePresentationProperties);
		map.putAll(variableFieldProperties);
		map.putAll(itemFormFieldProperties);
		map.putAll(dliItemProperties);
		map.putAll(complexDataItemProperties);
		
		return getUniquePropertyRules(map);
  }

  /**
   * return the ArrayList of EGL sql Item property rules valid for a DataItem
   */
  public static ArrayList getDataItemSQLItemPropertyRules() {
  	return getPropertyRules(sqlItemProperties);
  }
  /**
   * return the ArrayList of EGL page Item property rules valid for a DataItem
   */
  public static ArrayList getDataItemPageItemPropertyRules() {
  	return getPropertyRules(pageItemProperties);
  }
  
  /**
   * 
   * @return teh ArrayList of EGL DL1 Item Property rules valid for a DataItem
   */
  public static ArrayList getDataItemDL1ItemPropertyRules()
  {
      return getPropertyRules(dliItemProperties);
  }
  
  /**
   * return the ArrayList of PCB record Item property rules valid for a DataItem
   */
  public static ArrayList getPsbRecordItemPropertyRules() {
  	TreeMap map = new TreeMap(staticRecordDataDeclarationProperties);
  	map.putAll( psbRecordItemProperties );
  	return getPropertyRules( map );
}
  /**
   * return the ArrayList of EGL UI Item property rules valid for a DataItem
   */
  public static ArrayList getDataItemUIItemPropertyRules() {
  	return getPropertyRules(uiItemProperties);
}

  /**
   * return the ArrayList of EGL UI Item property rules valid in source assistance for a DataItem
   */  
  public static ArrayList getDataItemUIItemSAPropertyRules(){
      return getPropertyRules(uiItemSAProperties);
  }
  
  /**
   * return the ArrayList of EGL formatting property rules valid for a DataItem
   */
  public static ArrayList getDataItemFormattingPropertyRules() {
  	return getPropertyRules(formattingProperties);
  }
  /**
   * return the ArrayList of EGL validation property rules valid for a DataItem
   */
  public static ArrayList getDataItemValidationPropertyRules() {
  	return getPropertyRules(validationProperties);
  }
  /**
   * return the ArrayList of EGL field presentation property rules valid for a DataItem
   */
  public static ArrayList getDataItemFieldPresentationPropertyRules() {
	TreeMap map = new TreeMap(fieldPresentationProperties);
	map.putAll(doubleByteDevicePresentationProperties);
	map.putAll(itemFormFieldProperties);
	return getUniquePropertyRules(map);
  }
  
  /**
   * 
   * @return the ArrayList of EGL Text UI property rules valid for a DataItem
   * 		 which is the combined unique list of valid Variable Field properties and 
   * 		 field presentation properties for a DataItem
   */
  public static ArrayList getDataItemTUISAPropertyRules()
  {
      HashSet set = new HashSet(getDataItemVariableFieldPropertyRules());
      set.addAll(getDataItemFieldPresentationPropertyRules());

      return new ArrayList(set);      
  }
  
  /**
   * return the ArrayList of EGL field presentation property rules valid for a DataItem
   */
  public static ArrayList getTuiFieldPresentationPropertyRules() {
	TreeMap map = new TreeMap(fieldPresentationProperties);
	map.putAll(doubleByteDevicePresentationProperties);
	return getUniquePropertyRules(map);
  }  
  /**
   * return the ArrayList of EGL double byte device presentation property rules valid for a DataItem
   */
  public static ArrayList getDataItemDoubleByteDevicePresentationPropertyRules() {
  	return getPropertyRules(doubleByteDevicePresentationProperties);
  }
  /**
   * return the ArrayList of EGL variable field property rules valid for a DataItem
   */
  public static ArrayList getDataItemVariableFieldPropertyRules() {
  	return getPropertyRules(variableFieldProperties);
  }
  /**
   * return the ArrayList of EGL item form field property rules valid for a DataItem
   */
  public static ArrayList getDataItemItemFormFieldPropertyRules() {
  	return getPropertyRules(itemFormFieldProperties);
  }
  /**
   * return the List of EGL property names valid for a StructureItem
   */
  public static List getStructureItemPropertyNames() {
        ArrayList propertyValues = getStructureItemPropertyRules();
        return getNamesFromValues(propertyValues);
  }
  /**
   * return the List of EGL property names valid for a StructureItem
   */
  public static List getStructureItemPropertyNamesToLowerCase() {
        ArrayList propertyValues = getStructureItemPropertyRules();
        return getNamesFromValuesToLowerCase(propertyValues);
  }

  /**
   * return the ArrayList of EGL property rules valid for a StructureItem
   */
  public static ArrayList getStructureItemPropertyRules() {
		TreeMap map = new TreeMap(sqlItemProperties);
		map.putAll(pageItemProperties);
		map.putAll(formattingProperties);
		map.putAll(validationProperties);
		map.putAll(uiItemProperties);
		map.putAll(dliItemProperties);
		return getUniquePropertyRules(map);
  }
  
  /**
   * return the ArrayList of EGL property rules valid for a filler StructureItem
   */
  public static ArrayList getFillerStructureItemPropertyRules() {
  	return new ArrayList( fillerStructureItemProperties.values() );
  }
  
  public static ArrayList getRecordComplexPropertyPropertyRules() {
  	return new ArrayList( complexRecordProperties.values() );
  }
  
  public static ArrayList getDataItemComplexPropertyPropertyRules() {
  	return new ArrayList( complexDataItemProperties.values() );
  }
  
  public static ArrayList getItemDeclarationComplexPropertyPropertyRules() {
  	return new ArrayList( complexItemDeclarationProperties.values() );
  }
  
  public static ArrayList getStructureItemComplexPropertyRules() {  		
	return new ArrayList( complexStructureItemProperties.values() );
  }
  
  public static ArrayList getProgramComplexPropertyRules() {  		
	return new ArrayList( complexProgramProperties.values() );
  }
  
  public static ArrayList getFunctionComplexPropertyRules() {
  	return new ArrayList( complexFunctionProperties.values() );
  }
  
  public static ArrayList getBasicAbstractFunctionComplexPropertyRules() {
  	return new ArrayList( complexAbstractFunctionProperties.values() );
  }

  public static ArrayList getJavaOnlyAbstractFunctionComplexPropertyRules() {
    	return new ArrayList( complexAbstractFunctionProperties.values() );
    }

  public static ArrayList getServiceComplexPropertyRules() {
  	return new ArrayList( complexServiceProperties.values() );
  }
  
  public static ArrayList getBasicInterfaceComplexPropertyRules() {
  	return new ArrayList( complexInterfaceProperties.values() );
  }

  public static ArrayList getJavaObjectInterfaceComplexPropertyRules() {
    	return new ArrayList( complexInterfaceProperties.values() );
    }

  /**
   * return the List of EGL property names valid for a Data Declaration
   */
  public static List getStaticBasicRecordDataDeclarationPropertyNames() {
        ArrayList propertyValues = getStaticBasicRecordDataDeclarationPropertyRules();
        return getNamesFromValues(propertyValues);
  }
  /**
   * return the List of EGL property names valid for a Data Declaration
   */
  public static List getStaticBasicRecordDataDeclarationPropertyNamesToLowerCase() {
        ArrayList propertyValues = getStaticBasicRecordDataDeclarationPropertyRules();
        return getNamesFromValuesToLowerCase(propertyValues);
  }
  /**
   * return the Collection of EGL property rules valid for a Data Declaration
   */
  public static ArrayList getStaticBasicRecordDataDeclarationPropertyRules() {
        ArrayList recordProperties = getBasicRecordPropertyRules();
        return addStaticRecordDataDeclarationPropertyRules(recordProperties);
  }

  /**
   * return the List of EGL property names valid for a Data Declaration
   */
  public static List getStaticIndexedRecordDataDeclarationPropertyNames() {
        ArrayList propertyValues = getStaticIndexedRecordDataDeclarationPropertyRules();
        return getNamesFromValues(propertyValues);
  }
  /**
   * return the List of EGL property names valid for a Data Declaration
   */
  public static List getStaticIndexedRecordDataDeclarationPropertyNamesToLowerCase() {
        ArrayList propertyValues = getStaticIndexedRecordDataDeclarationPropertyRules();
        return getNamesFromValuesToLowerCase(propertyValues);
  }
  /**
   * return the Collection of EGL property rules valid for a Data Declaration
   */
  public static ArrayList getStaticIndexedRecordDataDeclarationPropertyRules() {
        ArrayList recordProperties = getIndexedRecordPropertyRules();
        return addStaticRecordDataDeclarationPropertyRules(recordProperties);
 }

  /**
   * return the List of EGL property names valid for a Data Declaration
   */
  public static List getStaticRelativeRecordDataDeclarationPropertyNames() {
        ArrayList propertyValues = getStaticRelativeRecordDataDeclarationPropertyRules();
        return getNamesFromValues(propertyValues);
  }
  /**
   * return the List of EGL property names valid for a Data Declaration
   */
  public static List getStaticRelativeRecordDataDeclarationPropertyNamesToLowerCase() {
        ArrayList propertyValues = getStaticRelativeRecordDataDeclarationPropertyRules();
        return getNamesFromValuesToLowerCase(propertyValues);
  }
  /**
   * return the Collection of EGL property rules valid for a Data Declaration
   */
  public static ArrayList getStaticRelativeRecordDataDeclarationPropertyRules() {
        ArrayList recordProperties = getRelativeRecordPropertyRules();
        return addStaticRecordDataDeclarationPropertyRules(recordProperties);
  }

  /**
   * return the List of EGL property names valid for a Data Declaration
   */
  public static List getStaticSerialRecordDataDeclarationPropertyNames() {
        ArrayList propertyValues = getStaticSerialRecordDataDeclarationPropertyRules();
        return getNamesFromValues(propertyValues);
  }
  /**
   * return the List of EGL property names valid for a Data Declaration
   */
  public static List getStaticSerialRecordDataDeclarationPropertyNamesToLowerCase() {
        ArrayList propertyValues = getStaticSerialRecordDataDeclarationPropertyRules();
        return getNamesFromValuesToLowerCase(propertyValues);
  }
  /**
   * return the Collection of EGL property rules valid for a Data Declaration
   */
  public static ArrayList getStaticSerialRecordDataDeclarationPropertyRules() {
        ArrayList recordProperties = getSerialRecordPropertyRules();
        return addStaticRecordDataDeclarationPropertyRules(recordProperties);
  }

  /**
   * return the List of EGL property names valid for a Data Declaration
   */
  public static List getStaticMQRecordDataDeclarationPropertyNames() {
        ArrayList propertyValues = getStaticMQRecordDataDeclarationPropertyRules();
        return getNamesFromValues(propertyValues);
  }
  /**
   * return the List of EGL property names valid for a Data Declaration
   */
  public static List getStaticMQRecordDataDeclarationPropertyNamesToLowerCase() {
        ArrayList propertyValues = getStaticMQRecordDataDeclarationPropertyRules();
        return getNamesFromValuesToLowerCase(propertyValues);
  }
  /**
   * return the Collection of EGL property rules valid for a Data Declaration
   */
    public static ArrayList getStaticMQRecordDataDeclarationPropertyRules() {
            ArrayList recordProperties = getMQRecordPropertyRules();
            return addStaticRecordDataDeclarationPropertyRules(recordProperties);
    }

    /**
     * return the List of EGL property names valid for a Data Declaration
     */
    public static List getStaticSQLRecordDataDeclarationPropertyNames() {
            ArrayList propertyValues = getStaticSQLRecordDataDeclarationPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a Data Declaration
     */
    public static List getStaticSQLRecordDataDeclarationPropertyNamesToLowerCase() {
            ArrayList propertyValues = getStaticSQLRecordDataDeclarationPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the Collection of EGL property rules valid for a Data Declaration
     */
    public static ArrayList getStaticSQLRecordDataDeclarationPropertyRules() {
            ArrayList recordProperties = getSQLRecordPropertyRules();
            return addStaticRecordDataDeclarationPropertyRules(recordProperties);
    }

    /**
     * return the List of EGL property names valid for a Data Declaration
     */
    public static List getStaticVGUIRecordDataDeclarationPropertyNames() {
            ArrayList propertyValues = getStaticVGUIRecordDataDeclarationPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a Data Declaration
     */
    public static List getStaticVGUIRecordDataDeclarationPropertyNamesToLowerCase() {
            ArrayList propertyValues = getStaticVGUIRecordDataDeclarationPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the Collection of EGL property rules valid for a Data Declaration
     */
    public static ArrayList getStaticVGUIRecordDataDeclarationPropertyRules() {
    	return getPropertyRules(staticRecordDataDeclarationProperties);
    }

    /*
    * return the Collection of EGL property rules valid for a Data Declaration
    */
   public static ArrayList addStaticRecordDataDeclarationPropertyRules(ArrayList recordProperties) {
           ArrayList rulesList = new ArrayList(staticRecordDataDeclarationProperties.size()
                                            + recordProperties.size());
           Collection propertyValues = staticRecordDataDeclarationProperties.values();
           Iterator iter = propertyValues.iterator();
           for (int i = 0; i < staticRecordDataDeclarationProperties.size(); i++) {
                   EGLPropertyRule rule = (EGLPropertyRule) iter.next();
                   rulesList.add(rule);
           }
           iter = recordProperties.iterator();
           for (int i = 0; i < recordProperties.size(); i++) {
                   EGLPropertyRule rule = (EGLPropertyRule) iter.next();
                   if (!(rulesList.contains(rule)) )
                           rulesList.add(rule);
           }

           return rulesList;
   }
   /**
    * return the List of EGL property names valid for a Data Declaration
    */
   public static List getDynamicBasicRecordDataDeclarationPropertyNames() {
           ArrayList propertyValues = getDynamicBasicRecordDataDeclarationPropertyRules();
           return getNamesFromValues(propertyValues);
   }
   /**
    * return the List of EGL property names valid for a Data Declaration
    */
   public static List getDynamicBasicRecordDataDeclarationPropertyNamesToLowerCase() {
           ArrayList propertyValues = getDynamicBasicRecordDataDeclarationPropertyRules();
           return getNamesFromValuesToLowerCase(propertyValues);
   }
   /**
    * return the Collection of EGL property rules valid for a Data Declaration
    */
   public static ArrayList getDynamicBasicRecordDataDeclarationPropertyRules() {
           ArrayList recordProperties = getBasicRecordPropertyRules();
           return addDynamicRecordDataDeclarationPropertyRules(recordProperties);
   }

   /**
    * return the List of EGL property names valid for a Data Declaration
    */
   public static List getDynamicIndexedRecordDataDeclarationPropertyNames() {
           ArrayList propertyValues = getDynamicIndexedRecordDataDeclarationPropertyRules();
           return getNamesFromValues(propertyValues);
   }
   /**
    * return the List of EGL property names valid for a Data Declaration
    */
   public static List getDynamicIndexedRecordDataDeclarationPropertyNamesToLowerCase() {
           ArrayList propertyValues = getDynamicIndexedRecordDataDeclarationPropertyRules();
           return getNamesFromValuesToLowerCase(propertyValues);

   }
   /**
    * return the Collection of EGL property rules valid for a Data Declaration
    */
   public static ArrayList getDynamicIndexedRecordDataDeclarationPropertyRules() {
           ArrayList recordProperties = getDynamicIndexedRecordPropertyRules();
           return addDynamicRecordDataDeclarationPropertyRules(recordProperties);
   }

   /**
    * return the List of EGL property names valid for a Data Declaration
    */
   public static List getDynamicRelativeRecordDataDeclarationPropertyNames() {
           ArrayList propertyValues = getDynamicRelativeRecordDataDeclarationPropertyRules();
           return getNamesFromValues(propertyValues);
   }
   /**
    * return the List of EGL property names valid for a Data Declaration
    */
   public static List getDynamicRelativeRecordDataDeclarationPropertyNamesToLowerCase() {
           ArrayList propertyValues = getDynamicRelativeRecordDataDeclarationPropertyRules();
           return getNamesFromValuesToLowerCase(propertyValues);
   }
   /**
    * return the Collection of EGL property rules valid for a Data Declaration
    */
   public static ArrayList getDynamicRelativeRecordDataDeclarationPropertyRules() {
           ArrayList recordProperties = getRelativeRecordPropertyRules();
           return addDynamicRecordDataDeclarationPropertyRules(recordProperties);
   }

   /**
    * return the List of EGL property names valid for a Data Declaration
    */
   public static List getDynamicSerialRecordDataDeclarationPropertyNames() {
           ArrayList propertyValues = getDynamicSerialRecordDataDeclarationPropertyRules();
           return getNamesFromValues(propertyValues);
   }
   /**
    * return the List of EGL property names valid for a Data Declaration
    */
   public static List getDynamicSerialRecordDataDeclarationPropertyNamesToLowerCase() {
           ArrayList propertyValues = getDynamicSerialRecordDataDeclarationPropertyRules();
           return getNamesFromValuesToLowerCase(propertyValues);
   }
   /**
    * return the Collection of EGL property rules valid for a Data Declaration
    */
   public static ArrayList getDynamicSerialRecordDataDeclarationPropertyRules() {
           ArrayList recordProperties = getDynamicSerialRecordPropertyRules();
           return addDynamicRecordDataDeclarationPropertyRules(recordProperties);
   }

   /**
    * return the List of EGL property names valid for a Data Declaration
    */
   public static List getDynamicMQRecordDataDeclarationPropertyNames() {
           ArrayList propertyValues = getDynamicMQRecordDataDeclarationPropertyRules();
           return getNamesFromValues(propertyValues);
   }
   /**
    * return the List of EGL property names valid for a Data Declaration
    */
   public static List getDynamicMQRecordDataDeclarationPropertyNamesToLowerCase() {
           ArrayList propertyValues = getDynamicMQRecordDataDeclarationPropertyRules();
           return getNamesFromValuesToLowerCase(propertyValues);
   }
   /**
    * return the Collection of EGL property rules valid for a Data Declaration
    */
   public static ArrayList getDynamicMQRecordDataDeclarationPropertyRules() {
           ArrayList recordProperties = getDynamicMQRecordPropertyRules();
           return addDynamicRecordDataDeclarationPropertyRules(recordProperties);
   }

   /**
    * return the List of EGL property names valid for a Data Declaration
    */
   public static List getDynamicSQLRecordDataDeclarationPropertyNames() {
           ArrayList propertyValues = getDynamicSQLRecordDataDeclarationPropertyRules();
           return getNamesFromValues(propertyValues);
   }
   /**
    * return the List of EGL property names valid for a Data Declaration
    */
   public static List getDynamicSQLRecordDataDeclarationPropertyNamesToLowerCase() {
           ArrayList propertyValues = getDynamicSQLRecordDataDeclarationPropertyRules();
           return getNamesFromValuesToLowerCase(propertyValues);
   }
   /**
    * return the Collection of EGL property rules valid for a Data Declaration
    */
   public static ArrayList getDynamicSQLRecordDataDeclarationPropertyRules() {
           ArrayList recordProperties = getSQLRecordPropertyRules();
           return addDynamicRecordDataDeclarationPropertyRules(recordProperties);
   }

   /**
    * return the List of EGL property names valid for a Data Declaration
    */
   public static List getDynamicVGUIRecordDataDeclarationPropertyNames() {
           ArrayList propertyValues = getDynamicVGUIRecordDataDeclarationPropertyRules();
           return getNamesFromValues(propertyValues);
   }
   /**
    * return the List of EGL property names valid for a Data Declaration
    */
   public static List getDynamicVGUIRecordDataDeclarationPropertyNamesToLowerCase() {
           ArrayList propertyValues = getDynamicVGUIRecordDataDeclarationPropertyRules();
           return getNamesFromValuesToLowerCase(propertyValues);
   }
   /**
    * return the Collection of EGL property rules valid for a Data Declaration
    */
   public static ArrayList getDynamicVGUIRecordDataDeclarationPropertyRules() {
        return getPropertyRules(dynamicRecordDataDeclarationProperties);
   }

   /*
   * return the Collection of EGL property rules valid for a Data Declaration
   */
  public static ArrayList addDynamicRecordDataDeclarationPropertyRules(ArrayList recordProperties) {
          ArrayList rulesList = new ArrayList(dynamicRecordDataDeclarationProperties.size()
                                    + recordProperties.size());
          Collection propertyValues = dynamicRecordDataDeclarationProperties.values();
          Iterator iter = propertyValues.iterator();
          for (int i = 0; i < dynamicRecordDataDeclarationProperties.size(); i++) {
                  EGLPropertyRule rule = (EGLPropertyRule) iter.next();
                  rulesList.add(rule);
          }
          iter = recordProperties.iterator();
          for (int i = 0; i < recordProperties.size(); i++) {
                  EGLPropertyRule rule = (EGLPropertyRule) iter.next();
                  if (!(rulesList.contains(rule)) )
                          rulesList.add(rule);
          }

          return rulesList;
  }

  /**
   * return the List of EGL property names valid for a Data Declaration
   */
  public static List getDynamicAnyRecordDataDeclarationPropertyNames() {
          ArrayList propertyValues = getDynamicAnyRecordDataDeclarationPropertyRules();
          return getNamesFromValues(propertyValues);
  }
  /**
   * return the List of EGL property names valid for a Data Declaration
   */
  public static List getDynamicAnyRecordDataDeclarationPropertyNamesToLowerCase() {
          ArrayList propertyValues = getDynamicAnyRecordDataDeclarationPropertyRules();
          return getNamesFromValuesToLowerCase(propertyValues);
  }
  /**
   * return the ArrayList of EGL property rules valid for all records
   */
  public static ArrayList getDynamicAnyRecordDataDeclarationPropertyRules() {
	TreeMap map = new TreeMap(dynamicRecordDataDeclarationProperties);
	map.putAll(basicRecordProperties);
	map.putAll(indexedRecordProperties);
	map.putAll(relativeRecordProperties);
	map.putAll(serialRecordProperties);
	map.putAll(MQRecordProperties);
	map.putAll(SQLRecordProperties);
	return getUniquePropertyRules(map, IEGLConstants.PROPERTY_NUMELEMENTSITEM);
  }
    /**
     * return the List of EGL property names valid for an Item Data Declaration
     */
    public static List getStaticItemDataDeclarationPropertyNames() {
            ArrayList propertyValues = getStaticItemDataDeclarationPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for an Item Data Declaration
     */
    public static List getStaticItemDataDeclarationPropertyNamesToLowerCase() {
            ArrayList propertyValues = getStaticItemDataDeclarationPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the Collection of EGL property rules valid for a Data Declaration
     */
    public static ArrayList getStaticItemDataDeclarationPropertyRules() {
    	TreeMap map = new TreeMap(staticItemDataDeclarationProperties);
    	map.putAll(pageItemProperties);
    	map.putAll(sqlItemProperties);
    	return getUniquePropertyRules(map);
    }
	/**
	  * return the List of EGL property names valid for a page Item Data Declaration
	  */
	 public static List getStaticPageItemDataDeclarationPropertyNames() {
			 ArrayList propertyValues = getStaticPageItemDataDeclarationPropertyRules();
			 return getNamesFromValues(propertyValues);
	 }
	 /**
	  * return the List of EGL property names valid for a page Item Data Declaration
	  */
	 public static List getStaticPageItemDataDeclarationPropertyNamesToLowerCase() {
			 ArrayList propertyValues = getStaticPageItemDataDeclarationPropertyRules();
			 return getNamesFromValuesToLowerCase(propertyValues);
	 }
	 /**
	  * return the Collection of EGL property rules valid for a page item Data Declaration
	  */
	 public static ArrayList getStaticPageItemDataDeclarationPropertyRules() {
    	TreeMap map = new TreeMap(staticItemDataDeclarationProperties);
    	map.putAll(pageItemProperties);
    	map.putAll(sqlItemProperties);
    	map.putAll(validationProperties);
    	map.putAll(formattingProperties);
    	return getUniquePropertyRules(map);
	 }        
    /**
     * return the List of EGL property names valid for a dynamic Item Data Declaration
     */
    public static List getDynamicItemDataDeclarationPropertyNames() {
            ArrayList propertyValues = getDynamicItemDataDeclarationPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a dynamic Item Data Declaration
     */
    public static List getDynamicItemDataDeclarationPropertyNamesToLowerCase() {
            ArrayList propertyValues = getDynamicItemDataDeclarationPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the Collection of EGL property rules valid for a dynamic item Data Declaration
     */
    public static ArrayList getDynamicItemDataDeclarationPropertyRules() {
    	TreeMap map = new TreeMap(dynamicItemDataDeclarationProperties);
    	map.putAll(pageItemProperties);
    	map.putAll(sqlItemProperties);
		return getUniquePropertyRules(map, IEGLConstants.PROPERTY_NUMELEMENTSITEM);
    }
	/**
	 * return the List of EGL property names valid for a dynamic page Item Data Declaration
	 */
	public static List getDynamicPageItemDataDeclarationPropertyNames() {
			ArrayList propertyValues = getDynamicPageItemDataDeclarationPropertyRules();
			return getNamesFromValues(propertyValues);
	}
	/**
	 * return the List of EGL property names valid for a dynamic page Item Data Declaration
	 */
	public static List getDynamicPageItemDataDeclarationPropertyNamesToLowerCase() {
			ArrayList propertyValues = getDynamicPageItemDataDeclarationPropertyRules();
			return getNamesFromValuesToLowerCase(propertyValues);
	}
	/**
	 * return the Collection of EGL property rules valid for a dynamic page item Data Declaration
	 */
	public static ArrayList getDynamicPageItemDataDeclarationPropertyRules() {
    	TreeMap map = new TreeMap(dynamicItemDataDeclarationProperties);
    	map.putAll(pageItemProperties);
    	map.putAll(sqlItemProperties);
    	map.putAll(validationProperties);
    	map.putAll(formattingProperties);
		return getUniquePropertyRules(map, IEGLConstants.PROPERTY_NUMELEMENTSITEM);
	}        
    /**
     * return the List of EGL property names valid for a Use Declaration
     */
    public static List getUseDeclarationPropertyNames() {
            ArrayList propertyValues = getUseDeclarationPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a Use Declaration
     */
    public static List getUseDeclarationPropertyNamesToLowerCase() {
            ArrayList propertyValues = getUseDeclarationPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a Use Declaration
     */
    public static ArrayList getUseDeclarationPropertyRules() {
    	TreeMap map = new TreeMap(formGroupUseProperties);
    	map.putAll(commonFormProperties);
    	map.putAll(dataTableUseProperties);
    	return getUniquePropertyRules(map);
	}

    /**
     * return the List of EGL property names valid for a Use Declaration
     */
    public static List getDataTableUseDeclarationPropertyNames() {
            ArrayList propertyValues = getDataTableUseDeclarationPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a Use Declaration
     */
    public static List getDataTableUseDeclarationPropertyNamesToLowerCase() {
            ArrayList propertyValues = getDataTableUseDeclarationPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a Use Declaration
     */
    public static ArrayList getDataTableUseDeclarationPropertyRules() {
    	return getPropertyRules(dataTableUseProperties);
	}

    /**
     * return the List of EGL property names valid for a Use Declaration
     */
    public static List getFormGroupUseDeclarationPropertyNames() {
            ArrayList propertyValues = getFormGroupUseDeclarationPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a Use Declaration
     */
    public static List getFormGroupUseDeclarationPropertyNamesToLowerCase() {
            ArrayList propertyValues = getFormGroupUseDeclarationPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a Use Declaration
     */
    public static ArrayList getFormGroupUseDeclarationPropertyRules() {
    	TreeMap map = new TreeMap(formGroupUseProperties);
    	map.putAll(commonFormProperties);
    	return getUniquePropertyRules(map);
	}

    /**
     * return the List of EGL property names valid for a Use Declaration
     */
    public static List getFormUseDeclarationPropertyNames() {
            ArrayList propertyValues = getFormUseDeclarationPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a Use Declaration
     */
    public static List getFormUseDeclarationPropertyNamesToLowerCase() {
            ArrayList propertyValues = getFormUseDeclarationPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a Use Declaration
     */
    public static ArrayList getFormUseDeclarationPropertyRules() {
            ArrayList rulesList = new ArrayList( );
            return rulesList;
    }
    /**
     * return the List of EGL property names valid for a Use Declaration
     */
    public static List getLibraryUseDeclarationPropertyNames() {
                    return getFormUseDeclarationPropertyNames();
    }
    /**
     * return the List of EGL property names valid for a Use Declaration
     */
    public static List getLibraryUseDeclarationPropertyNamesToLowerCase() {
            return getFormUseDeclarationPropertyNamesToLowerCase();
    }
    /**
     * return the ArrayList of EGL property rules valid for a Use Declaration
     */
    public static ArrayList getLibraryUseDeclarationPropertyRules() {
            return getFormUseDeclarationPropertyRules();
    }
    /**
     * return the List of EGL property names valid for a basic record
     */
    public static List getBasicRecordPropertyNames() {
            ArrayList propertyValues = getBasicRecordPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a basic record
     */
    public static List getBasicRecordPropertyNamesToLowerCase() {
            ArrayList propertyValues = getBasicRecordPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a basic record
     */
    public static ArrayList getBasicRecordPropertyRules() {
    	return getPropertyRules(basicRecordProperties);
    }

    /**
     * return the List of EGL property names valid for an Indexed record
     */
    public static List getIndexedRecordPropertyNames() {
            ArrayList propertyValues = getIndexedRecordPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for an Indexed record
     */
    public static List getIndexedRecordPropertyNamesToLowerCase() {
            ArrayList propertyValues = getIndexedRecordPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for an Indexed record
     */
    public static ArrayList getIndexedRecordPropertyRules() {
    	return getPropertyRules(indexedRecordProperties);
    }
	/**
	 * return the ArrayList of EGL property rules valid for a dynamic Indexed record
	 */
	public static ArrayList getDynamicIndexedRecordPropertyRules() {
		return getPropertyRules(indexedRecordProperties, IEGLConstants.PROPERTY_NUMELEMENTSITEM);
	}
    /**
     * return the List of EGL property names valid for a relative record
     */
    public static List getRelativeRecordPropertyNames() {
            ArrayList propertyValues = getRelativeRecordPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a relative record
     */
    public static List getRelativeRecordPropertyNamesToLowerCase() {
            ArrayList propertyValues = getRelativeRecordPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a relative Record
     */
    public static ArrayList getRelativeRecordPropertyRules() {
    	return getPropertyRules(relativeRecordProperties);
    }

    /**
     * return the List of EGL property names valid for a serial record
     */
    public static List getSerialRecordPropertyNames() {
            ArrayList propertyValues = getSerialRecordPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a serial record
     */
    public static List getSerialRecordPropertyNamesToLowerCase() {
            ArrayList propertyValues = getSerialRecordPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a static record
     */
    public static List getStaticRecordDataDeclarationPropertyNamesToLowerCase() {
            ArrayList propertyValues = getStaticRecordDataDeclarationPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a serial record
     */
    public static ArrayList getSerialRecordPropertyRules() {
    	return getPropertyRules(serialRecordProperties);
    }
    /**
     * return the ArrayList of EGL property rules valid for a serial record
     */
    public static ArrayList getStaticRecordDataDeclarationPropertyRules() {
    	return getPropertyRules(staticRecordDataDeclarationProperties);
    }
	/**
	 * return the ArrayList of EGL property rules valid for a dynamic serial record
	 */
	public static ArrayList getDynamicSerialRecordPropertyRules() {
		return getPropertyRules(serialRecordProperties, IEGLConstants.PROPERTY_NUMELEMENTSITEM);
	}

					
    /**
     * return the List of EGL property names valid for an MQ Record
     */
    public static List getMQRecordPropertyNames() {
            ArrayList propertyValues = getMQRecordPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for an MQ Record
     */
    public static List getMQRecordPropertyNamesToLowerCase() {
            ArrayList propertyValues = getMQRecordPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for an MQ Record
     */
    public static ArrayList getMQRecordPropertyRules() {
    	return getPropertyRules(MQRecordProperties);
    }

	/**
	 * return the ArrayList of EGL property rules valid for a dynamic MQ Record
	 */
	public static ArrayList getDynamicMQRecordPropertyRules() {
    	return getPropertyRules(MQRecordProperties, IEGLConstants.PROPERTY_NUMELEMENTSITEM);
	}

    /**
     * return the List of EGL property names valid for an SQL Record
     */
    public static List getSQLRecordPropertyNames() {
            ArrayList propertyValues = getSQLRecordPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for an SQL Record
     */
    public static List getSQLRecordPropertyNamesToLowerCase() {
            ArrayList propertyValues = getSQLRecordPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for an SQL Record
     */
    public static ArrayList getSQLRecordPropertyRules() {
    	return getPropertyRules(SQLRecordProperties);
    }

    /**
     * return the List of EGL property names valid for a PSB Record
     */
    public static List getPSBRecordPropertyNames() {
            ArrayList propertyValues = getPSBRecordPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a PSB Record
     */
    public static List getPSBRecordPropertyNamesToLowerCase() {
            ArrayList propertyValues = getPSBRecordPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a PSB Record
     */
    public static ArrayList getPSBRecordPropertyRules() {
    	return getPropertyRules(psbRecordProperties);
    }
    /**
     * return the ArrayList of EGL property rules valid for a DLI segment
     */
    public static ArrayList getDLISegmentPropertyRules() {
    	return getPropertyRules(dliSegmentProperties);
    }

    /**
     * return the List of EGL property names valid for a DLI segment
     */
    public static List getDLISegmentPropertyNames() {
            ArrayList propertyValues = getDLISegmentPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a DLI segment
     */
    public static List getDLISegmentPropertyNamesToLowerCase() {
            ArrayList propertyValues = getDLISegmentPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a VG UI Record
     */
    public static List getVGUIRecordPropertyNames() {
            ArrayList propertyValues = getVGUIRecordPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a VG UI Record
     */
    public static List getVGUIRecordPropertyNamesToLowerCase() {
            ArrayList propertyValues = getVGUIRecordPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a VG UI Record
     */
    public static ArrayList getVGUIRecordPropertyRules() {
    	return getPropertyRules(vgUIRecordProperties);
    }
    /**
     * return the List of EGL property names valid for an SQL Record
     */
    public static List getAnyRecordPropertyNames() {
            ArrayList propertyValues = getAnyRecordPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for an SQL Record
     */
    public static List getAnyRecordPropertyNamesToLowerCase() {
            ArrayList propertyValues = getAnyRecordPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for an SQL Record
     */
    public static ArrayList getAnyRecordPropertyRules() {
		TreeMap map = new TreeMap(basicRecordProperties);
		map.putAll(indexedRecordProperties);
		map.putAll(relativeRecordProperties);
		map.putAll(serialRecordProperties);
		map.putAll(MQRecordProperties);
		map.putAll(SQLRecordProperties);
		map.putAll(vgUIRecordProperties);
		return getUniquePropertyRules(map);
    }

    /**
     * return the List of EGL property names valid for all records
     */
    public static List getStaticAnyRecordDataDeclarationPropertyNames() {
            ArrayList propertyValues = getStaticAnyRecordDataDeclarationPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for all records
     */
    public static List getStaticAnyRecordDataDeclarationPropertyNamesToLowerCase() {
            ArrayList propertyValues = getStaticAnyRecordDataDeclarationPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for all records
     */
    public static ArrayList getStaticAnyRecordDataDeclarationPropertyRules() {
		TreeMap map = new TreeMap(staticRecordDataDeclarationProperties);
		map.putAll(basicRecordProperties);
		map.putAll(indexedRecordProperties);
		map.putAll(relativeRecordProperties);
		map.putAll(serialRecordProperties);
		map.putAll(MQRecordProperties);
		map.putAll(SQLRecordProperties);
		return getUniquePropertyRules(map);
    }

    /**
     * return the List of EGL property names valid for a Form Group
     */
    public static List getFormGroupPropertyNames() {
            ArrayList propertyValues = getFormGroupPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a Form Group
     */
    public static List getFormGroupPropertyNamesToLowerCase() {
            ArrayList propertyValues = getFormGroupPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a Form Group
     */
    public static ArrayList getFormGroupPropertyRules() {
		TreeMap map = new TreeMap(formGroupProperties);
		map.putAll(commonFormProperties);
		return getUniquePropertyRules(map);
    }

    /**
     * return the List of EGL property names valid for a screen floating area
     */
    public static List getScreenFloatingAreaPropertyNames() {
            ArrayList propertyValues = getScreenFloatingAreaPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a screen floating area
     */
    public static List getScreenFloatingAreaPropertyNamesToLowerCase() {
            ArrayList propertyValues = getScreenFloatingAreaPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a screen floating area
     */
    public static ArrayList getScreenFloatingAreaPropertyRules() {
    	return getPropertyRules(screenFloatingAreaProperties);
    }

    /**
     * return the List of EGL property names valid for a Print Floating Area
     */
    public static List getPrintFloatingAreaPropertyNames() {
            ArrayList propertyValues = getPrintFloatingAreaPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a Print Floating Area
     */
    public static List getPrintFloatingAreaPropertyNamesToLowerCase() {
            ArrayList propertyValues = getPrintFloatingAreaPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a Print Floating Area
     */
    public static ArrayList getPrintFloatingAreaPropertyRules() {
    	return getPropertyRules(printFloatingAreaProperties);
    }

    /**
     * return the List of EGL property names valid for a Constant field on a text form
     */
    public static List getTextConstantFormFieldPropertyNames() {
            ArrayList propertyValues = getTextConstantFormFieldPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a Constant field on a text form
     */
    public static List getTextConstantFormFieldPropertyNamesToLowerCase() {
            ArrayList propertyValues = getTextConstantFormFieldPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a Constant field on a text form
     */
    public static ArrayList getTextConstantFormFieldPropertyRules() {
		TreeMap map = new TreeMap(textConstantFormFieldProperties);
		map.putAll(formFieldProperties);
		map.putAll(fieldPresentationProperties);
		map.putAll(doubleByteDevicePresentationProperties);
		return getUniquePropertyRules(map);
    }

	/**
     * return the List of EGL property names valid for a constant field on a print form
     */
    public static List getPrintConstantFormFieldPropertyNames() {
            ArrayList propertyValues = getPrintConstantFormFieldPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a constant field on a print form
     */
    public static List getPrintConstantFormFieldPropertyNamesToLowerCase() {
            ArrayList propertyValues = getPrintConstantFormFieldPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a constant field on a print form
     */
    public static ArrayList getPrintConstantFormFieldPropertyRules() {
		TreeMap map = new TreeMap(printFormFieldProperties);
		map.putAll(formFieldProperties);
		map.putAll(doubleByteDevicePresentationProperties);
		return getUniquePropertyRules(map);
    }

    /**
     * return the List of EGL property names valid for a variable field on a print form
     */
    public static List getPrintVariableFormFieldPropertyNames() {
            ArrayList propertyValues = getPrintVariableFormFieldPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a variable field on a print form
     */
    public static List getPrintVariableFormFieldPropertyNamesToLowerCase() {
            ArrayList propertyValues = getPrintVariableFormFieldPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a variable field on a print form
     */
    public static ArrayList getPrintVariableFormFieldPropertyRules() {
		TreeMap map = new TreeMap(commonVariableFormFieldProperties);
		map.putAll(printFormFieldProperties);
		map.putAll(formFieldProperties);
		map.putAll(formattingProperties);
		map.putAll(doubleByteDevicePresentationProperties);
		return getUniquePropertyRules(map);
    }

    /**
     * return the List of EGL property names valid for a variable field on a text form
     */
    public static List getTextVariableFormFieldPropertyNames() {
            ArrayList propertyValues = getTextVariableFormFieldPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a variable field on a text form
     */
    public static List getTextVariableFormFieldPropertyNamesToLowerCase() {
            ArrayList propertyValues = getTextVariableFormFieldPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a variable field on a text form
     */
    public static ArrayList getTextVariableFormFieldPropertyRules() {
		TreeMap map = new TreeMap(textVariableFormFieldProperties);
		map.putAll(commonVariableFormFieldProperties);
		map.putAll(variableFieldProperties);
		map.putAll(formFieldProperties);
		map.putAll(formattingProperties);
		map.putAll(validationProperties);
		map.putAll(fieldPresentationProperties);
		map.putAll(doubleByteDevicePresentationProperties);
		return getUniquePropertyRules(map);
    }

    /**
     * return the List of EGL property names valid for a variable field on a text form
     */
    public static List getTuiTextVariableFormFieldPropertyNames() {
            ArrayList propertyValues = getTuiTextVariableFormFieldPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a variable field on a text form
     */
    public static List getTuiTextVariableFormFieldPropertyNamesToLowerCase() {
            ArrayList propertyValues = getTuiTextVariableFormFieldPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a variable field on a text form
     */
    public static ArrayList getTuiTextVariableFormFieldPropertyRules() {
		return getUniquePropertyRules(tuiTextVariableFormFieldProperties);
    }

    /**
     * return the List of EGL property names valid for a variable field on a print form
     */
    public static List getTuiPrintVariableFormFieldPropertyNames() {
            ArrayList propertyValues = getTuiPrintVariableFormFieldPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a variable field on a print form
     */
    public static List getTuiPrintVariableFormFieldPropertyNamesToLowerCase() {
            ArrayList propertyValues = getTuiPrintVariableFormFieldPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a variable field on a print form
     */
    public static ArrayList getTuiPrintVariableFormFieldPropertyRules() {
		return getUniquePropertyRules(tuiPrintVariableFormFieldProperties);
    }

    
    /**
     * return the List of EGL property names valid for a text Form
     */
    public static List getTextFormPropertyNames() {
            ArrayList propertyValues = getTextFormPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a text Form
     */
    public static List getTextFormPropertyNamesToLowerCase() {
            ArrayList propertyValues = getTextFormPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a text Form
     */
    public static ArrayList getTextFormPropertyRules() {
		return getPropertyRules(textFormProperties);
    }

    /**
     * return the List of EGL property names valid for a Print Form
     */
    public static List getPrintFormPropertyNames() {
            ArrayList propertyValues = getPrintFormPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a Print Form
     */
    public static List getPrintFormPropertyNamesToLowerCase() {
            ArrayList propertyValues = getPrintFormPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a Print Form
     */
    public static ArrayList getPrintFormPropertyRules() {
		return getPropertyRules(printFormProperties);
    }

    /**
     * return the List of EGL property names valid for a Page Handler
     */
    public static List getPageHandlerPropertyNames() {
            ArrayList propertyValues = getPageHandlerPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a Page Handler
     */
    public static List getPageHandlerPropertyNamesToLowerCase() {
            ArrayList propertyValues = getPageHandlerPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a Page Handler
     */
    public static ArrayList getPageHandlerPropertyRules() {
		return getPropertyRules(pageHandlerProperties);
    }

    /**
     * return the List of EGL property names valid for a DataTable
     */
    public static List getDataTablePropertyNames() {
            ArrayList propertyValues = getDataTablePropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a DataTable
     */
    public static List getDataTablePropertyNamesToLowerCase() {
            ArrayList propertyValues = getDataTablePropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a DataTable
     */
    public static ArrayList getDataTablePropertyRules() {
		return getPropertyRules(dataTableProperties);
    }

    /**
     * return the List of EGL property names valid for a basic program
     */
    public static List getBasicProgramPropertyNames() {
            ArrayList propertyValues = getBasicProgramPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a basic program
     */
    public static List getBasicProgramPropertyNamesToLowerCase() {
            ArrayList propertyValues = getBasicProgramPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a basic program
     */
    public static ArrayList getBasicProgramPropertyRules() {
		TreeMap map = new TreeMap(basicProgramProperties);
		map.putAll(programProperties);
		return getUniquePropertyRules(map);
    }

	/**
	 * return the List of EGL property names valid for a called basic program
	 */
	public static List getCalledBasicProgramPropertyNames() {
			ArrayList propertyValues = getCalledBasicProgramPropertyRules();
			return getNamesFromValues(propertyValues);
	}
	/**
	 * return the List of EGL property names valid for a called basic program
	 */
	public static List getCalledBasicProgramPropertyNamesToLowerCase() {
			ArrayList propertyValues = getCalledBasicProgramPropertyRules();
			return getNamesFromValuesToLowerCase(propertyValues);
	}
	/**
	 * return the ArrayList of EGL property rules valid for a called basic program
	 */
	public static ArrayList getCalledBasicProgramPropertyRules() {
		TreeMap map = new TreeMap(calledProgramProperties);
		map.putAll(programProperties);
		return getUniquePropertyRules(map);
	}

    /**
     * return the List of EGL property names valid for a text UI program
     */
    public static List getTextUIProgramPropertyNames() {
            ArrayList propertyValues = getTextUIProgramPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a text UI program
     */
    public static List getTextUIProgramPropertyNamesToLowerCase() {
            ArrayList propertyValues = getTextUIProgramPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a text UI program
     */
    public static ArrayList getTextUIProgramPropertyRules() {
		TreeMap map = new TreeMap(textUIProgramProperties);
		map.putAll(basicProgramProperties);
		map.putAll(programProperties);
		return getUniquePropertyRules(map);
    }

	/**
	 * return the List of EGL property names valid for a called text UI program
	 */
	public static List getCalledTextUIProgramPropertyNames() {
			ArrayList propertyValues = getCalledTextUIProgramPropertyRules();
			return getNamesFromValues(propertyValues);
	}
	/**
	 * return the List of EGL property names valid for a called text UI program
	 */
	public static List getCalledTextUIProgramPropertyNamesToLowerCase() {
			ArrayList propertyValues = getCalledTextUIProgramPropertyRules();
			return getNamesFromValuesToLowerCase(propertyValues);
	}
	/**
	 * return the ArrayList of EGL property rules valid for a called text UI program
	 */
	public static ArrayList getCalledTextUIProgramPropertyRules() {
		TreeMap map = new TreeMap(textUIProgramProperties);
		map.putAll(calledProgramProperties);
		map.putAll(programProperties);
		return getUniquePropertyRules(map);
	}

    /**
     * return the List of EGL property names valid for a action program
     */
    public static List getVGWebTransactionPropertyNames() {
            ArrayList propertyValues = getVGWebTransactionPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a action program
     */
    public static List getVGWebTransactionPropertyNamesToLowerCase() {
            ArrayList propertyValues = getVGWebTransactionPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a action program
     */
    public static ArrayList getVGWebTransactionPropertyRules() {
		TreeMap map = new TreeMap(vgWebTransactionProperties);
		map.putAll(programProperties);
		return getUniquePropertyRules(map);
    }
    /**
     * return the List of EGL property names valid for all records
     */
    public static List getAllProgramPropertyNames() {
            ArrayList propertyValues = getAllProgramPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for all records
     */
    public static List getAllProgramPropertyNamesToLowerCase() {
            ArrayList propertyValues = getAllProgramPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for all records
     */
    public static ArrayList getAllProgramPropertyRules() {
		TreeMap map = new TreeMap(/*vgWebTransactionProperties*/);
		map.putAll(calledProgramProperties);
		map.putAll(basicProgramProperties);
//		map.putAll(textUIProgramProperties);
		map.putAll(programProperties);
		return getUniquePropertyRules(map);
    }
    /**
     * return the List of EGL property names valid for all records
     */
    public static List getAllHandlerPropertyNames() {
            ArrayList propertyValues = getAllHandlerPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for all records
     */
    public static List getAllHandlerPropertyNamesToLowerCase() {
            ArrayList propertyValues = getAllHandlerPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for all records
     */
    public static ArrayList getAllHandlerPropertyRules() {
		TreeMap map = new TreeMap(handlerProperties);
		return getUniquePropertyRules(map);
    }
    /**
     * return the List of EGL property names valid for a Library
     */
    public static List getlibraryPropertyNames() {
            ArrayList propertyValues = getlibraryPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a Library
     */
    public static List getlibraryPropertyNamesToLowerCase() {
            ArrayList propertyValues = getlibraryPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a Library
     */
    public static ArrayList getlibraryPropertyRules() {
		return getPropertyRules(libraryProperties);
    }
    
    /**
     * return the List of EGL property names valid for a Library
     */
    public static List getServiceBindingLibraryPropertyNames() {
            ArrayList propertyValues = getServiceBindingLibraryPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a Library
     */
    public static List getServiceBindingLibraryPropertyNamesToLowerCase() {
            ArrayList propertyValues = getServiceBindingLibraryPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a Library
     */
    public static ArrayList getServiceBindingLibraryPropertyRules() {
		return getPropertyRules(serviceBindingLibraryProperteis);
    }
    
    /**
     * return the List of EGL property names valid for a Library
     */
    public static List getServicePropertyNames() {
            ArrayList propertyValues = getServicePropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a service
     */
    public static List getServicePropertyNamesToLowerCase() {
            ArrayList propertyValues = getServicePropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    
    /**
     * return the ArrayList of EGL property rules valid for a service
     */
    public static ArrayList getServicePropertyRules() {
		return getPropertyRules(serviceProperties);
    }
    
    /**
     * return the ArrayList of EGL property rules valid for a service declaration
     */
    public static ArrayList getServiceDeclarationPropertyRules() {
		return getPropertyRules(serviceDeclarationProperties);
    }
    
    /**
     * return the ArrayList of EGL property rules valid for a service function
     */
    public static ArrayList getServiceFunctionPropertyRules() {
		return getPropertyRules(serviceFunctionProperties);
    }
    
    /**
     * return the List of EGL property names valid for a NativeLibrary
     */
    public static List getNativeLibraryPropertyNames() {
            ArrayList propertyValues = getNativeLibraryPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a NativeLibrary
     */
    public static List getNativeLibraryPropertyNamesToLowerCase() {
            ArrayList propertyValues = getNativeLibraryPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a NativeLibrary
     */
    public static ArrayList getNativeLibraryPropertyRules() {
		return getPropertyRules(nativeLibraryProperties);
    }

    /**
     * return the List of EGL property names valid for a Function
     */
    public static List getFunctionPropertyNames() {
            ArrayList propertyValues = getFunctionPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a Function
     */
    public static List getFunctionPropertyNamesToLowerCase() {
            ArrayList propertyValues = getFunctionPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a Function
     */
    public static ArrayList getFunctionPropertyRules() {
		return getPropertyRules(functionProperties);
    }
    /**
     * return the List of EGL property names valid for a nativeLibrary Function
     */
    public static List getNativeLibraryFunctionPropertyNames() {
            ArrayList propertyValues = getNativeLibraryFunctionPropertyRules();
            return getNamesFromValues(propertyValues);
    }
    /**
     * return the List of EGL property names valid for a nativeLibrary Function
     */
    public static List getNativeLibraryFunctionPropertyNamesToLowerCase() {
            ArrayList propertyValues = getNativeLibraryFunctionPropertyRules();
            return getNamesFromValuesToLowerCase(propertyValues);
    }
    /**
     * return the ArrayList of EGL property rules valid for a nativeLibrary Function
     */
    public static ArrayList getNativeLibraryFunctionPropertyRules() {
		TreeMap map = new TreeMap(nativeLibraryFunctionProperties);
		map.putAll(functionProperties);
		return getUniquePropertyRules(map);
    }

    /**
	 * return the List of EGL property rules valid for a dictionary
	 */
	public static ArrayList getDictionaryPropertyRules() {
		return getPropertyRules(dictionaryProperties);
	}

	/**
	 * return the List of EGL property names valid for a dictionary
	 */
	public static List getDictionaryPropertyNames() {
		return getNamesFromValues(getDictionaryPropertyRules());
	}
	
	/**
	 * return the List of lowercase EGL property names valid for a dictionary
	 */
	public static List getDictionaryPropertyNamesToLowerCase() {
		return getNamesFromValuesToLowerCase(getDictionaryPropertyRules());
	}

    /**
	 * return the List of EGL property rules valid for a consoleForm
	 */
	public static ArrayList getConsoleFormPropertyRules() {
		return getPropertyRules(consoleFormProperties);
	}

	/**
	 * return the List of EGL property names valid for a consoleForm
	 */
	public static List getConsoleFormPropertyNames() {
		return getNamesFromValues(getConsoleFormPropertyRules());
	}
	
	/**
	 * return the List of lowercase EGL property names valid for a consoleForm
	 */
	public static List getConsoleFormPropertyNamesToLowerCase() {
		return getNamesFromValuesToLowerCase(getConsoleFormPropertyRules());
	}

    /**
	 * return the List of EGL property rules valid for a consoleField
	 */
	public static ArrayList getConsoleFieldPropertyRules() {
		return getPropertyRules(consoleFieldProperties);
	}

	/**
	 * return the List of EGL property names valid for a consoleField
	 */
	public static List getConsoleFieldPropertyNames() {
		return getNamesFromValues(getConsoleFieldPropertyRules());
	}
	
	/**
	 * return the List of lowercase EGL property names valid for a consoleField
	 */
	public static List getConsoleFieldPropertyNamesToLowerCase() {
		return getNamesFromValuesToLowerCase(getConsoleFieldPropertyRules());
	}

    /**
	 * return the List of EGL property rules valid for a console array field
	 */
	public static ArrayList getConsoleArrayFieldPropertyRules() {
		TreeMap map = new TreeMap(consoleFieldProperties);
		map.putAll(consoleArrayFieldProperties);
		return getUniquePropertyRules(map);
	}

	/**
	 * return the List of EGL property names valid for a console array field
	 */
	public static List getConsoleArrayFieldPropertyNames() {
		ArrayList propertyValues = getConsoleFieldPropertyRules();
		propertyValues.addAll(getConsoleArrayFieldPropertyRules());
		return getNamesFromValues(propertyValues);
	}
	
	/**
	 * return the List of lowercase EGL property names valid for a console array field
	 */
	public static List getConsoleArrayFieldPropertyNamesToLowerCase() {
		ArrayList propertyValues = getConsoleFieldPropertyRules();
		propertyValues.addAll(getConsoleArrayFieldPropertyRules());
		return getNamesFromValuesToLowerCase(propertyValues);
	}

    /**
	 * return the List of EGL property rules valid for a window
	 */
	public static ArrayList getWindowPropertyRules() {
		return getPropertyRules(windowProperties);
	}

	/**
	 * return the List of EGL property names valid for a window
	 */
	public static List getWindowPropertyNames() {
		return getNamesFromValues(getWindowPropertyRules());
	}
	
	/**
	 * return the List of lowercase EGL property names valid for a window
	 */
	public static List getWindowPropertyNamesToLowerCase() {
		return getNamesFromValuesToLowerCase(getWindowPropertyRules());
	}

    /**
	 * return the List of EGL property rules valid for a presentation attributes
	 */
	public static ArrayList getPresentationAttributesPropertyRules() {
		return getPropertyRules(presentationAttributesProperties);
	}

	/**
	 * return the List of EGL property names valid for a presentation attributes
	 */
	public static List getPresentationAttributesPropertyNames() {
		return getNamesFromValues(getPresentationAttributesPropertyRules());
	}
	
	/**
	 * return the List of lowercase EGL property names valid for a presentation attributes
	 */
	public static List getPresentationAttributesPropertyNamesToLowerCase() {
		return getNamesFromValuesToLowerCase(getPresentationAttributesPropertyRules());
	}

    /**
	 * return the List of EGL property rules valid for a menu
	 */
	public static ArrayList getMenuPropertyRules() {
		return getPropertyRules(menuProperties);
	}

	/**
	 * return the List of EGL property names valid for a menu
	 */
	public static List getMenuPropertyNames() {
		return getNamesFromValues(getMenuPropertyRules());
	}
	
	/**
	 * return the List of lowercase EGL property names valid for a menu
	 */
	public static List getMenuPropertyNamesToLowerCase() {
		return getNamesFromValuesToLowerCase(getMenuPropertyRules());
	}

    /**
	 * return the List of EGL property rules valid for a menu item
	 */
	public static ArrayList getMenuItemPropertyRules() {
		return getPropertyRules(menuItemProperties);
	}

	/**
	 * return the List of EGL property names valid for a menu item
	 */
	public static List getMenuItemPropertyNames() {
		return getNamesFromValues(getMenuItemPropertyRules());
	}
	
	/**
	 * return the List of lowercase EGL property names valid for a menu item
	 */
	public static List getMenuItemPropertyNamesToLowerCase() {
		return getNamesFromValuesToLowerCase(getMenuItemPropertyRules());
	}

    /**
	 * return the List of EGL property rules valid for a prompt
	 */
	public static ArrayList getPromptPropertyRules() {
		return getPropertyRules(promptProperties);
	}

	/**
	 * return the List of EGL property names valid for a prompt
	 */
	public static List getPromptPropertyNames() {
		return getNamesFromValues(getPromptPropertyRules());
	}
	
	/**
	 * return the List of lowercase EGL property names valid for a prompt
	 */
	public static List getPromptPropertyNamesToLowerCase() {
		return getNamesFromValuesToLowerCase(getPromptPropertyRules());
	}

    /**
	 * return the List of EGL property rules valid for a Basic Interface
	 */
	public static ArrayList getBasicInterfacePropertyRules() {
		return getPropertyRules(basicInterfaceProperties);
	}

	public static ArrayList getJavaObjectInterfacePropertyRules() {
		return getPropertyRules(javaObjectInterfaceProperties);
	}

    /**
	 * return the List of EGL property rules valid for an Interface declaration
	 */
	public static ArrayList getInterfaceDeclarationPropertyRules() {
		return getPropertyRules(interfaceDeclarationProperties);
	}

    /**
	 * return the List of EGL property rules valid for an abstract function
	 */
	public static ArrayList getBasicAbstractFunctionPropertyRules() {
		return getPropertyRules(basicAbstractFunctionProperties);
	}

    /**
	 * return the List of EGL property rules valid for an abstract function in a JavaOnly interface
	 */
	public static ArrayList getJavaOnlyAbstractFunctionPropertyRules() {
		return getPropertyRules(javaOnlyAbstractFunctionProperties);
	}

	/**
	 * return the List of EGL property names valid for a Basic Interface
	 */
	public static List getBasicInterfacePropertyNames() {
		return getNamesFromValues(getBasicInterfacePropertyRules());
	}

	/**
	 * return the List of EGL property names valid for a JavaObject Interface
	 */
	public static List getJavaObjectInterfacePropertyNames() {
		return getNamesFromValues(getJavaObjectInterfacePropertyRules());
	}

    /**
	 * return the List of EGL property rules valid for an Interface
	 */
	public static ArrayList getPcbParmsPropertyRules() {
		return getPropertyRules(pcbParmsProperties);
	}

	public static ArrayList getLinkParmsPropertyRules() {
		return getPropertyRules(linkParmsProperties);
	}

	public static ArrayList getHierarchyPropertyRules() {
		return getPropertyRules(hierarchyProperties);
	}

	public static ArrayList getCommonVariableFormFieldPropertyRules() {
		return getPropertyRules(commonVariableFormFieldProperties);
	}

	public static ArrayList getFormFieldPropertyRules() {
		return getPropertyRules(formFieldProperties);
	}

	public static EGLPropertyRule getPropertyRule(int location, String propertyName) {
		List rules = getPropertyRules(location);
		for (Iterator iter = rules.iterator(); iter.hasNext();) {
			EGLPropertyRule rule = (EGLPropertyRule) iter.next();
			if (rule.getName().equalsIgnoreCase(propertyName))
				return rule;
		}
		return null;
	}

	private static ArrayList getUniquePropertyRules(TreeMap properties) {
		return getUniquePropertyRules(properties, "");
	}

	private static ArrayList getUniquePropertyRules(TreeMap properties, String excludeProperty) {
		ArrayList rulesList = new ArrayList(properties.size());
        Iterator iter = properties.values().iterator();
        for (int i = 0; i < properties.size(); i++) {
            EGLPropertyRule rule = (EGLPropertyRule) iter.next();
            if (!(rulesList.contains(rule)) && !(rule.name.equalsIgnoreCase(excludeProperty)))
                    rulesList.add(rule);
        }
		return rulesList;
	}

    /**
	 * return the List of EGL property rules valid for a properties TreeMap
	 */
	public static ArrayList getPropertyRules(TreeMap properties) {
		return getPropertyRules(properties, "");
	}
	
	public static ArrayList getPropertyRules(TreeMap properties, String excludeProperty) {
		ArrayList rulesList = new ArrayList(properties.size());
		for (Iterator iter = properties.values().iterator(); iter.hasNext();) {
			EGLPropertyRule rule = (EGLPropertyRule) iter.next();
			if (!(rule.name.equalsIgnoreCase(excludeProperty)))				 
				rulesList.add(rule);
		}
		return rulesList;
	}
	
    /**
	 * return the List of EGL property rules valid for an array element on a form
	 */
	public static ArrayList getTuiArrayElementFormFieldPropertyRules() {
		TreeMap map = new TreeMap( formArrayElementProperties);
		map.putAll( fieldPresentationProperties);
		return getUniquePropertyRules(map);
	}

	/**
	 * return the List of EGL property names valid for  an array element on a form
	 */
	public static List getTuiArrayElementFormFieldPropertyNames() {
		return getNamesFromValues( getTuiArrayElementFormFieldPropertyRules());
	}
	
	/**
	 * return the List of lowercase EGL property names valid for  an array element on a form
	 */
	public static List getTuiArrayElementFormFieldPropertyNamesToLowerCase() {
		return getNamesFromValuesToLowerCase( getTuiArrayElementFormFieldPropertyRules());
	}
}
