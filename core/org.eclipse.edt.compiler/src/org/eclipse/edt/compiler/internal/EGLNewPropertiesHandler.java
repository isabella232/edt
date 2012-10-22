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
package org.eclipse.edt.compiler.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.ElementKind;
import org.eclipse.edt.mof.egl.utils.InternUtil;


public class EGLNewPropertiesHandler {
//	
//	private static Set REPLACEME = Collections.EMPTY_SET;
//		
//	private static boolean needRecalculateForNewAnno(Set annoSet){
//		return(annoSet == null);
//	}
//	
//    // types expected for the property
//    public static final int nameValue = 0;
//    public static final int quotedValue = 1;
//    public static final int specificValue = 2;
//    public static final int integerValue = 3;
//    public static final int literalValue = 4;
//    public static final int listValue = 5;
//    public static final int literalArray = 6;
//    public static final int nestedValue = 7;
//    public static final int sqlValue = 8;
//    public static final int arrayOfArrays = 9;
//    public static final int arrayOf = 10;
//    public static final int complexPropertyValue = 11;
//    
//    // locations for properties
//    // locations that cannot contain nested properties
//    public static final int locationDataItem = 1;
//    public static final int locationScreenFloatArea = 2;
//    public static final int locationPrintFloatArea = 3;
//    public static final int locationTextConstantFormField = 4;
//    public static final int locationPrintConstantFormField = 5;
//    public static final int locationTextVariableFormField = 6;
//    public static final int locationPrintVariableFormField = 7;
//    public static final int locationPageHandlerDeclaration = 8;
//    public static final int locationDataTable = 9;
//    public static final int locationFunction = 10;
//    public static final int locationProgram = 11;
//    public static final int locationLibrary = 12;
//    public static final int locationUseDeclaration = 13;
//    public static final int locationFormGroupUseDeclaration = 14;
//    public static final int locationDataTableUseDeclaration = 15;
//    public static final int locationFormUseDeclaration = 16;
//    public static final int locationLibraryUseDeclaration = 17;
//    public static final int locationStaticItemDataDeclaration = 18;
//    public static final int locationDynamicItemDataDeclaration = 19;
//    public static final int locationStaticVGUIRecordDataDeclaration = 20;
//    public static final int locationDynamicVGUIRecordDataDeclaration = 21;
//    public static final int locationStaticPageItemDataDeclaration = 22;
//    public static final int locationDynamicPageItemDataDeclaration = 23;
//    public static final int locationVGWebTransaction = 24;
//    public static final int locationBasicProgram = 25;
//    public static final int locationCalledBasicProgram = 26;
//    public static final int locationTextUIProgram = 27;
//    public static final int locationCalledTextUIProgram = 28;   
//    public static final int locationNativeLibrary=29;
//    public static final int locationNativeLibraryFunction=30;
//    public static final int locationServiceBindingLibrary=31;
//  
//    public static final int maxLocationNoNesting = locationCalledTextUIProgram;
//
//    // locations that can have only 1 level of nesting
//    public static final int locationFormGroup = 35;
//    public static final int locationTextFormDeclaration = 36;
//    public static final int locationPrintFormDeclaration = 37;
//
//    public static final int maxLocationOneLevelNesting = locationPrintFormDeclaration;
//
//    // locations where properties can be nested any number of levels
//    public static final int locationStructureItem = 40;
//    public static final int locationBasicRecord = 41;
//    public static final int locationIndexedRecord = 42;
//    public static final int locationRelativeRecord = 43;
//    public static final int locationSerialRecord = 44;
//    public static final int locationMQRecord = 45;
//    public static final int locationSQLRecord = 46;
//    public static final int locationVGUIRecord = 47; 
//    public static final int locationCSVRecord = 48;    
//    public static final int locationAnyRecord = 49;
//    public static final int locationFillerStructureItem = 50;
//
//    //locations 49 and 50 are unused
//    public static final int locationStaticBasicRecordDataDeclaration = 51;
//    public static final int locationStaticIndexedRecordDataDeclaration = 52;
//    public static final int locationStaticRelativeRecordDataDeclaration = 53;
//    public static final int locationStaticSerialRecordDataDeclaration = 54;
//    public static final int locationStaticMQRecordDataDeclaration = 55;
//    public static final int locationStaticSQLRecordDataDeclaration = 56;
//    public static final int locationStaticAnyRecordDataDeclaration = 57;
//    //locations 58-60 are unused
//    public static final int locationDynamicBasicRecordDataDeclaration = 61;
//    public static final int locationDynamicIndexedRecordDataDeclaration = 62;
//    public static final int locationDynamicRelativeRecordDataDeclaration = 63;
//    public static final int locationDynamicSerialRecordDataDeclaration = 64;
//    public static final int locationDynamicMQRecordDataDeclaration = 65;
//    public static final int locationDynamicSQLRecordDataDeclaration = 66;
//    public static final int locationDynamicAnyRecordDataDeclaration = 67;
//
//    public static final int locationFormatting = 68;
//    public static final int locationSqlItem = 69;
//    public static final int locationPageItem = 70;
//    public static final int locationUIItem = 71;
//    public static final int locationValidation = 72;
//    public static final int locationFieldPresentation = 73;
//    public static final int locationDoubleByteDevicePresentation = 74;
//    public static final int locationVariableField = 75;
//    public static final int locationItemFormField = 76;
//    
//    public static final int locationDictionary = 77;
//    public static final int locationConsoleForm = 78;
//    public static final int locationConsoleField = 79;
//    public static final int locationConsoleArrayField = 80;
//    public static final int locationWindow = 81;
//    public static final int locationPresentationAttributes = 83;
//    public static final int locationMenu = 84;
//    public static final int locationMenuItem = 85;
//    public static final int locationPrompt = 86;
//    public static final int locationOpenUI = 87;
//    public static final int locationReport = 88;
//    public static final int locationReportData = 89;
//    public static final int locationCommonVariableFormField = 90;
//    public static final int locationFormField = 91;
//    
//    // never used by validation, only for TUI editor  
//    public static final int locationTuiTextVariableFormField = 92;
//    public static final int locationTuiPrintVariableFormField = 93;
//    public static final int locationTuiFieldPresentation = 94;
//    public static final int locationTuiArrayElementFormField = 95;
//    
//    public static final int locationPSBRecord = 96;
//    public static final int locationDLISegment = 97;
//    
//    public static final int locationService = 98;
//    public static final int locationServiceDeclaration = 99;
//    public static final int locationServiceFunction = 100;
//    public static final int locationBasicInterface = 101;
//    public static final int locationJavaObject = 102;
//    public static final int locationInterfaceDeclaration = 103;
//    public static final int locationBasicAbstractFunction = 104;
//    public static final int locationJavaOnlyAbstractFunction = 105;
//    
//    //annotations
//    public static final int locationProgramLinkData = 106;
//    public static final int locationLinkParameter = 107;
//    public static final int locationDLI = 110;
//    public static final int locationPCB = 111;
//    public static final int locationRelationship = 112;
//    public static final int locationEGLBinding = 113;
//    public static final int locationWebBinding = 114;
//
//    public static final int locationLinkParms = 115;
//    public static final int locationPcbParms = 116;
//    
//    public static final int locationPsbRecordItem = 117;
//    
//    public static final int locationSAUIItem = 118;        //never used by validation, only for source assistant editor
//    public static final int locationSATUIItem = 119;        //never used by validation, only for source assistant editor
//
//    public static final int locationDL1Item = 120;
//    
//    public static final int locationHandler = 121;
//    
//    public static final int locationServiceClassDeclaration = 122;
//    public static final int locationNewExpression = 123;
//    public static final int locationExternalTypeClassDeclaration = 124;
//    public static final int locationExternalTypeArrayHandlerClassDeclaration = 125;
//    public static final int locationExternalTypeFunction = 126;
//    
//    public static final int allNonSubtype = 127;
//    
//    public static final int locationCall = 128;
//    public static final int locationTransfer = 129;
//    public static final int locationShow = 130;
//    
//    public static final int locationConsoleButton = 131;
//    public static final int locationConsoleRadiogroup = 132;
//    public static final int locationConsoleCheckbox = 133;
//    public static final int locationConsoleList = 134;
//    public static final int locationConsoleCombo = 135;
//    
//    public static final int locationDynamicConsoleForm = 136;
//    public static final int locationDynamicPSBRecord = 137;
//    public static final int locationDynamicDLISegment = 138;
//    
//    public static final int locationJavaScriptObject = 139;
//    public static final int locationHostProgram = 140;
//
//    public static final int locationExit = 141;
//
//    public static Collection getPropertyRules(int location) {
//		switch (location) {
//		case locationDataItem:
//			return getDataItemPropertyRules();
//		case locationStructureItem:
//			return getStructureItemPropertyRules();
//		case locationFillerStructureItem:
//			return getFillerStructureItemPropertyRules();
//		case locationBasicRecord:
//			return getBasicRecordPropertyRules();
//		case locationIndexedRecord:
//			return getIndexedRecordPropertyRules();
//		case locationRelativeRecord:
//			return getRelativeRecordPropertyRules();
//		case locationSerialRecord:
//			return getSerialRecordPropertyRules();
//		case locationMQRecord:
//			return getMQRecordPropertyRules();
//		case locationSQLRecord:
//			return getSQLRecordPropertyRules();
//		case locationCSVRecord:
//			return getCSVRecordPropertyRules();
//		case locationVGUIRecord:
//			return getVGUIRecordPropertyRules();
//		case locationAnyRecord:
//			return getAnyRecordPropertyRules();
//		case locationFormGroup:
//			return getFormGroupPropertyRules();
//		case locationScreenFloatArea:
//			return getScreenFloatingAreaPropertyRules();
//		case locationPrintFloatArea:
//			return getPrintFloatingAreaPropertyRules();
//		case locationTextConstantFormField:
//			return getTextConstantFormFieldPropertyRules();
//		case locationPrintConstantFormField:
//			return getPrintConstantFormFieldPropertyRules();
//		case locationTextVariableFormField:
//			return getTextVariableFormFieldPropertyRules();
//		case locationTuiTextVariableFormField:
//			return getTuiTextVariableFormFieldPropertyRules();
//		case locationTuiPrintVariableFormField:
//			return getTuiPrintVariableFormFieldPropertyRules();
//		case locationPrintVariableFormField:
//			return getPrintVariableFormFieldPropertyRules();
//		case locationTextFormDeclaration:
//			return getTextFormPropertyRules();
//		case locationPrintFormDeclaration:
//			return getPrintFormPropertyRules();
//		case locationPageHandlerDeclaration:
//			return getPageHandlerPropertyRules();
//		case locationDataTable:
//			return getDataTablePropertyRules();
//		case locationFunction:
//			return getFunctionPropertyRules();
//		case locationNativeLibraryFunction:
//			return getNativeLibraryFunctionPropertyRules();
//		case locationProgram:
//			return getAllProgramPropertyRules();
//		case locationVGWebTransaction:
//			return getVGWebTransactionPropertyRules();
//		case locationBasicProgram:
//			return getBasicProgramPropertyRules();
//		case locationCalledBasicProgram:
//			return getCalledBasicProgramPropertyRules();
//		case locationTextUIProgram:
//			return getTextUIProgramPropertyRules();
//		case locationCalledTextUIProgram:
//			return getCalledTextUIProgramPropertyRules();
//		case locationLibrary:
//			return getlibraryPropertyRules();
//		case locationNativeLibrary:
//			return getNativeLibraryPropertyRules();
//		case locationStaticBasicRecordDataDeclaration:
//			return getStaticBasicRecordDataDeclarationPropertyRules();
//		case locationStaticIndexedRecordDataDeclaration:
//			return getStaticIndexedRecordDataDeclarationPropertyRules();
//		case locationStaticRelativeRecordDataDeclaration:
//			return getStaticRelativeRecordDataDeclarationPropertyRules();
//		case locationStaticSerialRecordDataDeclaration:
//			return getStaticSerialRecordDataDeclarationPropertyRules();
//		case locationStaticMQRecordDataDeclaration:
//			return getStaticMQRecordDataDeclarationPropertyRules();
//		case locationStaticSQLRecordDataDeclaration:
//			return getStaticSQLRecordDataDeclarationPropertyRules();
//		case locationStaticVGUIRecordDataDeclaration:
//			return getStaticVGUIRecordDataDeclarationPropertyRules();
//		case locationStaticAnyRecordDataDeclaration:
//			return getStaticAnyRecordDataDeclarationPropertyRules();
//		case locationDynamicBasicRecordDataDeclaration:
//			return getDynamicBasicRecordDataDeclarationPropertyRules();
//		case locationDynamicIndexedRecordDataDeclaration:
//			return getDynamicIndexedRecordDataDeclarationPropertyRules();
//		case locationDynamicRelativeRecordDataDeclaration:
//			return getDynamicRelativeRecordDataDeclarationPropertyRules();
//		case locationDynamicSerialRecordDataDeclaration:
//			return getDynamicSerialRecordDataDeclarationPropertyRules();
//		case locationDynamicMQRecordDataDeclaration:
//			return getDynamicMQRecordDataDeclarationPropertyRules();
//		case locationDynamicSQLRecordDataDeclaration:
//			return getDynamicSQLRecordDataDeclarationPropertyRules();
//		case locationDynamicVGUIRecordDataDeclaration:
//			return getDynamicVGUIRecordDataDeclarationPropertyRules();
//		case locationDynamicAnyRecordDataDeclaration:
//			return getDynamicAnyRecordDataDeclarationPropertyRules();
//		case locationStaticItemDataDeclaration:
//			return getStaticItemDataDeclarationPropertyRules();
//		case locationDynamicItemDataDeclaration:
//			return getDynamicItemDataDeclarationPropertyRules();
//		case locationLibraryUseDeclaration:
//			return getLibraryUseDeclarationPropertyRules();
//
//		case locationSqlItem:
//			return getDataItemSQLItemPropertyRules();
//		case locationPageItem:
//			return getDataItemPageItemPropertyRules();
//		case locationDictionary:
//			return getDictionaryPropertyRules();
//		case locationConsoleField:
//			return getConsoleFieldPropertyRules();
//		case locationConsoleArrayField:
//			return getConsoleArrayFieldPropertyRules();
//		case locationWindow:
//			return getWindowPropertyRules();
//		case locationPresentationAttributes:
//			return getPresentationAttributesPropertyRules();
//		case locationMenu:
//			return getMenuPropertyRules();
//		case locationMenuItem:
//			return getMenuItemPropertyRules();
//		case locationPrompt:
//			return getPromptPropertyRules();
//		case locationService:
//			return getServicePropertyRules();
//		case locationServiceDeclaration:
//			return getServiceDeclarationPropertyRules();
//		case locationServiceFunction:
//			return getServiceFunctionPropertyRules();
//		case locationBasicInterface:
//			return getBasicInterfacePropertyRules();
//		case locationJavaObject:
//			return getJavaObjectPropertyRules();
//		case locationJavaScriptObject:
//			return getJavaScriptObjectPropertyRules();
//		case locationHostProgram:
//			return getHostProgramPropertyRules();
//		case locationInterfaceDeclaration:
//			return getInterfaceDeclarationPropertyRules();
//		case locationBasicAbstractFunction:
//			return getBasicAbstractFunctionPropertyRules();
//		case locationJavaOnlyAbstractFunction:
//			return getJavaOnlyAbstractFunctionPropertyRules();
//		case locationLinkParms:
//			return getLinkParmsPropertyRules();
//		case locationProgramLinkData:
//			return createRulesForFields(IEGLConstants.PROPERTY_PROGRAMLINKDATA);
//		case locationLinkParameter:
//			return createRulesForFields(IEGLConstants.PROPERTY_LINKPARAMETER);
//		case locationRelationship:
//			return createRulesForFields(IEGLConstants.PROPERTY_RELATIONSHIP);
//		case locationEGLBinding:
//			return createRulesForFields(IEGLConstants.PROPERTY_EGLBINDING);
//		case locationWebBinding:
//			return createRulesForFields(IEGLConstants.PROPERTY_WEBBINDING);
//		case locationHandler:
//			return getAllHandlerPropertyRules();
//		case locationServiceClassDeclaration:
//			return getServiceStaticItemDataDeclarationPropertyRules();
//		case locationNewExpression:
//			return getNewExpressionPropertyRules();
//		case locationExternalTypeClassDeclaration:
//			return getExternalTypeClassDeclartionPropertyRules();
//		case locationExternalTypeArrayHandlerClassDeclaration:
//			return getExternalTypeArrayHandlerClassDeclartionPropertyRules();
//		case locationExternalTypeFunction:
//			return getExternalTypeFunctionPropertyRules();
//		case allNonSubtype:
//			return createRulesFor(new AnnotationRecordFilter() {
//				public boolean passes(AnnotationType aTypeBinding) {
//					return true;
//				}
//			}, false);
//		default:
//			return null;
//		}
//	}
//	
//	private static Set AllHandlerPropertyRules;
//	private static Set getAllHandlerPropertyRules() {
//		if ( needRecalculateForNewAnno(AllHandlerPropertyRules) ) {
//			AllHandlerPropertyRules = new TreeSet();
//			AllHandlerPropertyRules.addAll(createRulesForElementKinds(ElementKind.HandlerPart));
//		}
//		return AllHandlerPropertyRules;
//
//	}
//
//
//	private static Set LinkParmsPropertyRules;
//	private static Set getLinkParmsPropertyRules() {
//		if ( needRecalculateForNewAnno(LinkParmsPropertyRules) ) {
//			LinkParmsPropertyRules = new TreeSet();
//			LinkParmsPropertyRules.addAll(createRulesForField(IEGLConstants.PROPERTY_PROGRAMLINKDATA, IEGLConstants.PROPERTY_LINKPARMS));
//		}
//		return LinkParmsPropertyRules;
//
//	}
//
//	private static Set JavaOnlyAbstractFunctionPropertyRules;
//	private static Set getJavaOnlyAbstractFunctionPropertyRules() {
//		if ( needRecalculateForNewAnno(JavaOnlyAbstractFunctionPropertyRules) ) {
//			JavaOnlyAbstractFunctionPropertyRules = new TreeSet();
//			JavaOnlyAbstractFunctionPropertyRules.addAll(REPLACEME);
//		}
//		return JavaOnlyAbstractFunctionPropertyRules;
//
//	}
//
//	private static Set ExternalTypeFunctionPropertyRules;
//	private static Set getExternalTypeFunctionPropertyRules() {
//		if ( needRecalculateForNewAnno(ExternalTypeFunctionPropertyRules) ) {
//			ExternalTypeFunctionPropertyRules = new TreeSet();
//			ExternalTypeFunctionPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_JAVANAME));
//			ExternalTypeFunctionPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_THROWSEXCEPTIONS));
//		}
//		return ExternalTypeFunctionPropertyRules;
//
//	}
//
//	private static Set BasicAbstractFunctionPropertyRules;
//	private static Set getBasicAbstractFunctionPropertyRules() {
//		if ( needRecalculateForNewAnno(BasicAbstractFunctionPropertyRules) ) {
//			BasicAbstractFunctionPropertyRules = new TreeSet();
//			BasicAbstractFunctionPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_XML));
//		}
//		return BasicAbstractFunctionPropertyRules;
//
//	}  
//
//	private static Set InterfaceDeclarationPropertyRules;
//	private static Set getInterfaceDeclarationPropertyRules() {
//		if ( needRecalculateForNewAnno(InterfaceDeclarationPropertyRules) ) {
//			InterfaceDeclarationPropertyRules = new TreeSet();
//			InterfaceDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_BINDSERVICE));
//			InterfaceDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_RESTBINDING));
//			InterfaceDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_WEBBINDING));
//			InterfaceDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_RESOURCE));
//			InterfaceDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_DEDICATEDSERVICE));
//		}
//		return InterfaceDeclarationPropertyRules;
//
//	}
//
//	private static Set JavaObjectPropertyRules;
//	private static Set getJavaObjectPropertyRules() {
//		if ( needRecalculateForNewAnno(JavaObjectPropertyRules) ) {
//			JavaObjectPropertyRules = new TreeSet();
//			JavaObjectPropertyRules.addAll(createRulesForElementKinds(ElementKind.ExternalTypePart));
//			JavaObjectPropertyRules.addAll(createRulesForFields(IEGLConstants.EXTERNALTYPE_SUBTYPE_JAVAOBJECT));
//		}
//		return JavaObjectPropertyRules;
//
//	}
//
//	private static Set JavaScriptObjectPropertyRules;
//	private static Set getJavaScriptObjectPropertyRules() {
//		if ( needRecalculateForNewAnno(JavaScriptObjectPropertyRules) ) {
//			JavaScriptObjectPropertyRules = new TreeSet();
//			JavaScriptObjectPropertyRules.addAll(createRulesForElementKinds(ElementKind.ExternalTypePart));
//			JavaScriptObjectPropertyRules.addAll(createRulesForFields(IEGLConstants.EXTERNALTYPE_SUBTYPE_JAVASCRIPTOBJECT));
//		}
//		return JavaScriptObjectPropertyRules;
//
//	}
//
//	private static Set HostProgramPropertyRules;
//	private static Set getHostProgramPropertyRules() {
//		if ( needRecalculateForNewAnno(HostProgramPropertyRules) ) {
//			HostProgramPropertyRules = new TreeSet();
//			HostProgramPropertyRules.addAll(createRulesForElementKinds(ElementKind.ExternalTypePart));
//			HostProgramPropertyRules.addAll(createRulesForFields(IEGLConstants.EXTERNALTYPE_SUBTYPE_HOSTPROGRAM));
//		}
//		return HostProgramPropertyRules;
//
//	}
//
//	private static Set BasicInterfacePropertyRules;
//	private static Set getBasicInterfacePropertyRules() {
//		if ( needRecalculateForNewAnno(BasicInterfacePropertyRules) ) {
//			BasicInterfacePropertyRules = new TreeSet();
//			BasicInterfacePropertyRules.addAll(createRulesForElementKinds(ElementKind.InterfacePart));
//		}
//		return BasicInterfacePropertyRules;
//
//	}
//
//	private static Set ServiceFunctionPropertyRules;
//	private static Set getServiceFunctionPropertyRules() {
//		if ( needRecalculateForNewAnno(ServiceFunctionPropertyRules) ) {
//			ServiceFunctionPropertyRules = new TreeSet();
//			ServiceFunctionPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_XML));
//		}
//		return ServiceFunctionPropertyRules;
//
//	}
//
//	private static Set ServiceDeclarationPropertyRules;
//	private static Set getServiceDeclarationPropertyRules() {
//		if ( needRecalculateForNewAnno(ServiceDeclarationPropertyRules) ) {
//			ServiceDeclarationPropertyRules = new TreeSet();
//			ServiceDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_BINDSERVICE));
//			ServiceDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_DEDICATEDSERVICE));
//			ServiceDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_RESOURCE));
//			ServiceDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_RESTBINDING));
//			ServiceDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_WEBBINDING));
//		}
//		return ServiceDeclarationPropertyRules;
//
//	}
//
//	private static Set ServicePropertyRules;
//	private static Set getServicePropertyRules() {
//		if ( needRecalculateForNewAnno(ServicePropertyRules) ) {
//			ServicePropertyRules = new TreeSet();
//			ServicePropertyRules.addAll(createRulesForElementKinds(ElementKind.ServicePart));
//		}
//		return ServicePropertyRules;
//
//	}
//
//	private static Set PromptPropertyRules;
//	private static Set getPromptPropertyRules() {
//		if ( needRecalculateForNewAnno(PromptPropertyRules) ) {
//			PromptPropertyRules = new TreeSet();
//			PromptPropertyRules.addAll(REPLACEME);
//		}
//		return PromptPropertyRules;
//
//	}
//
//	private static Set MenuItemPropertyRules;
//	private static Set getMenuItemPropertyRules() {
//		if ( needRecalculateForNewAnno(MenuItemPropertyRules) ) {
//			MenuItemPropertyRules = new TreeSet();
//			MenuItemPropertyRules.addAll(REPLACEME);
//		}
//		return MenuItemPropertyRules;
//
//	}
//
//	private static Set MenuPropertyRules;
//	private static Set getMenuPropertyRules() {
//		if ( needRecalculateForNewAnno(MenuPropertyRules) ) {
//			MenuPropertyRules = new TreeSet();
//			MenuPropertyRules.addAll(REPLACEME);
//		}
//		return MenuPropertyRules;
//
//	}
//
//	private static Set PresentationAttributesPropertyRules;
//	private static Set getPresentationAttributesPropertyRules() {
//		if ( needRecalculateForNewAnno(PresentationAttributesPropertyRules) ) {
//			PresentationAttributesPropertyRules = new TreeSet();
//			PresentationAttributesPropertyRules.addAll(REPLACEME);
//		}
//		return PresentationAttributesPropertyRules;
//
//	}
//
//	private static Set WindowPropertyRules;
//	private static Set getWindowPropertyRules() {
//		if ( needRecalculateForNewAnno(WindowPropertyRules) ) {
//			WindowPropertyRules = new TreeSet();
//			WindowPropertyRules.addAll(REPLACEME);
//		}
//		return WindowPropertyRules;
//
//	}
//
//	private static Set ConsoleArrayFieldPropertyRules;
//	private static Set getConsoleArrayFieldPropertyRules() {
//		if ( needRecalculateForNewAnno(ConsoleArrayFieldPropertyRules) ) {
//			ConsoleArrayFieldPropertyRules = new TreeSet();
//			ConsoleArrayFieldPropertyRules.addAll(createRulesForMemberAnnotations(IEGLConstants.RECORD_SUBTYPE_CONSOLE_FORM));
//			ConsoleArrayFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_COLUMNS));
//			ConsoleArrayFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_LINESBETWEENROWS));
//			ConsoleArrayFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_SPACESBETWEENCOLUMNS));
//			ConsoleArrayFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_ORIENTINDEXACROSS));
//		}
//		return ConsoleArrayFieldPropertyRules;
//
//	}
//
//	private static Set ConsoleFieldPropertyRules;
//	private static Set getConsoleFieldPropertyRules() {
//		if ( needRecalculateForNewAnno(ConsoleFieldPropertyRules) ) {
//			ConsoleFieldPropertyRules = new TreeSet();
//			ConsoleFieldPropertyRules.addAll(createRulesForMemberAnnotations(IEGLConstants.RECORD_SUBTYPE_CONSOLE_FORM));
//		}
//		return ConsoleFieldPropertyRules;
//
//	}
//
//	private static Set DictionaryPropertyRules;
//	private static Set getDictionaryPropertyRules() {
//		if ( needRecalculateForNewAnno(DictionaryPropertyRules) ) {
//			DictionaryPropertyRules = new TreeSet();
//			DictionaryPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_CASESENSITIVE));
//			DictionaryPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_ORDERING));
//		}
//		return DictionaryPropertyRules;
//
//	}
//
//
//	private static Set DataItemPageItemPropertyRules;
//	private static Set getDataItemPageItemPropertyRules() {
//		if ( needRecalculateForNewAnno(DataItemPageItemPropertyRules) ) {
//			DataItemPageItemPropertyRules = new TreeSet();
//			DataItemPageItemPropertyRules.addAll(createRulesForMemberAnnotations(IEGLConstants.HANDLER_SUBTYPE_JSF));
//		}
//		return DataItemPageItemPropertyRules;
//
//	}
//
//	private static Set DataItemSQLItemPropertyRules;
//	private static Set getDataItemSQLItemPropertyRules() {
//		if ( needRecalculateForNewAnno(DataItemSQLItemPropertyRules) ) {
//			DataItemSQLItemPropertyRules = new TreeSet();
//			DataItemSQLItemPropertyRules.addAll(createRulesForMemberAnnotations(IEGLConstants.RECORD_SUBTYPE_SQl));
//		}
//		return DataItemSQLItemPropertyRules;
//
//	}
//
//	
//	private static Set LibraryUseDeclarationPropertyRules;
//	private static Set getLibraryUseDeclarationPropertyRules() {
//		if ( needRecalculateForNewAnno(LibraryUseDeclarationPropertyRules) ) {
//			LibraryUseDeclarationPropertyRules = new TreeSet();
//			LibraryUseDeclarationPropertyRules.addAll(REPLACEME);
//		}
//		return LibraryUseDeclarationPropertyRules;
//
//	}
//
//	private static Set DynamicItemDataDeclarationPropertyRules;
//	private static Set getDynamicItemDataDeclarationPropertyRules() {
//		if ( needRecalculateForNewAnno(DynamicItemDataDeclarationPropertyRules) ) {
//			DynamicItemDataDeclarationPropertyRules = new TreeSet();
//			DynamicItemDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_MAXSIZE));
//			DynamicItemDataDeclarationPropertyRules.addAll(createRulesForMemberAnnotations(IEGLConstants.RECORD_SUBTYPE_SQl));
//			DynamicItemDataDeclarationPropertyRules.addAll(createRulesForElementKinds(ElementKind.FieldMbr));
//		}
//		return DynamicItemDataDeclarationPropertyRules;
//
//	}
//
//	private static Set StaticItemDataDeclarationPropertyRules;
//	private static Set getStaticItemDataDeclarationPropertyRules() {
//		if ( needRecalculateForNewAnno(StaticItemDataDeclarationPropertyRules )) {
//			StaticItemDataDeclarationPropertyRules = new TreeSet();
//			StaticItemDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_INITIALIZED));
//			StaticItemDataDeclarationPropertyRules.addAll(createRulesForMemberAnnotations(IEGLConstants.RECORD_SUBTYPE_SQl));
//			StaticItemDataDeclarationPropertyRules.addAll(createRulesForElementKinds(ElementKind.FieldMbr));
//		}
//		return StaticItemDataDeclarationPropertyRules;
//	}
//
//	private static Set StaticServiceItemDataDeclarationPropertyRules;
//	private static Set getServiceStaticItemDataDeclarationPropertyRules() {
//		if ( needRecalculateForNewAnno(StaticServiceItemDataDeclarationPropertyRules) ) {
//			StaticServiceItemDataDeclarationPropertyRules = new TreeSet();
//			StaticServiceItemDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_BINDSERVICE));
//			StaticServiceItemDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_INITIALIZED));
//			StaticServiceItemDataDeclarationPropertyRules.addAll(createRulesForMemberAnnotations(IEGLConstants.HANDLER_SUBTYPE_JSF));
//			StaticServiceItemDataDeclarationPropertyRules.addAll(createRulesForMemberAnnotations(IEGLConstants.RECORD_SUBTYPE_SQl));	    	
//		}
//		return StaticServiceItemDataDeclarationPropertyRules;
//	}
//
//	private static Set ExternalTypeClassDeclarationPropertyRules;
//	private static Set getExternalTypeClassDeclartionPropertyRules() {
//		if ( needRecalculateForNewAnno(ExternalTypeClassDeclarationPropertyRules) ) {
//			ExternalTypeClassDeclarationPropertyRules = new TreeSet();
//			ExternalTypeClassDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_JAVAPROPERTY));
//			ExternalTypeClassDeclarationPropertyRules.addAll(createRulesForElementKinds(ElementKind.FieldMbr));
//		}
//		return ExternalTypeClassDeclarationPropertyRules;
//	}
//
//	private static Set ExternalTypeArrayHandlerClassDeclarationPropertyRules;
//	private static Set getExternalTypeArrayHandlerClassDeclartionPropertyRules() {
//		if ( needRecalculateForNewAnno(ExternalTypeArrayHandlerClassDeclarationPropertyRules )) {
//			ExternalTypeArrayHandlerClassDeclarationPropertyRules = new TreeSet();
//			ExternalTypeArrayHandlerClassDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_JAVAPROPERTY));
//			ExternalTypeArrayHandlerClassDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_EVENTLISTENER));
//			ExternalTypeArrayHandlerClassDeclarationPropertyRules.addAll(createRulesForElementKinds(ElementKind.FieldMbr));
//		}
//		return ExternalTypeArrayHandlerClassDeclarationPropertyRules;
//	}
//
//	private static Set NewExpressionPropertyRules;
//	private static Set getNewExpressionPropertyRules() {
//		if (NewExpressionPropertyRules == null) {
//			NewExpressionPropertyRules = new TreeSet();
//		}
//		return NewExpressionPropertyRules;
//	}
//
//	private static Set DynamicAnyRecordDataDeclarationPropertyRules;
//	private static Set getDynamicAnyRecordDataDeclarationPropertyRules() {
//		if ( needRecalculateForNewAnno(DynamicAnyRecordDataDeclarationPropertyRules) ) {
//			DynamicAnyRecordDataDeclarationPropertyRules = new TreeSet();
//			DynamicAnyRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_MAXSIZE));
//			DynamicAnyRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
//			DynamicAnyRecordDataDeclarationPropertyRules.addAll(getVGUIRecordPropertyRules());
//			DynamicAnyRecordDataDeclarationPropertyRules.addAll(getSQLRecordPropertyRules());
//			DynamicAnyRecordDataDeclarationPropertyRules.addAll(getMQRecordPropertyRules());
//			DynamicAnyRecordDataDeclarationPropertyRules.addAll(getSerialRecordPropertyRules());
//			DynamicAnyRecordDataDeclarationPropertyRules.addAll(getRelativeRecordPropertyRules());
//			DynamicAnyRecordDataDeclarationPropertyRules.addAll(getIndexedRecordPropertyRules());
//			DynamicAnyRecordDataDeclarationPropertyRules.addAll(getBasicRecordPropertyRules());
//		}
//		return DynamicAnyRecordDataDeclarationPropertyRules;
//
//	}
//
//	private static Set DynamicVGUIRecordDataDeclarationPropertyRules;
//	private static Set getDynamicVGUIRecordDataDeclarationPropertyRules() {
//		if ( needRecalculateForNewAnno(DynamicVGUIRecordDataDeclarationPropertyRules) ) {
//			DynamicVGUIRecordDataDeclarationPropertyRules = new TreeSet();
//			DynamicVGUIRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_MAXSIZE));
//			DynamicVGUIRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
//			DynamicVGUIRecordDataDeclarationPropertyRules.addAll(getVGUIRecordPropertyRules());
//		}
//		return DynamicVGUIRecordDataDeclarationPropertyRules;
//
//	}
//
//	private static Set DynamicSQLRecordDataDeclarationPropertyRules;
//	private static Set getDynamicSQLRecordDataDeclarationPropertyRules() {
//		if ( needRecalculateForNewAnno(DynamicSQLRecordDataDeclarationPropertyRules) ) {
//			DynamicSQLRecordDataDeclarationPropertyRules = new TreeSet();
//			DynamicSQLRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_MAXSIZE));
//			DynamicSQLRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
//			DynamicSQLRecordDataDeclarationPropertyRules.addAll(getSQLRecordPropertyRules());
//		}
//		return DynamicSQLRecordDataDeclarationPropertyRules;
//
//	}
//
//	private static Set DynamicMQRecordDataDeclarationPropertyRules;
//	private static Set getDynamicMQRecordDataDeclarationPropertyRules() {
//		if ( needRecalculateForNewAnno(DynamicMQRecordDataDeclarationPropertyRules) ) {
//			DynamicMQRecordDataDeclarationPropertyRules = new TreeSet();
//			DynamicMQRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_MAXSIZE));
//			DynamicMQRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
//			DynamicMQRecordDataDeclarationPropertyRules.addAll(getMQRecordPropertyRules());
//		}
//		return DynamicMQRecordDataDeclarationPropertyRules;
//
//	}
//
//	private static Set DynamicSerialRecordDataDeclarationPropertyRules;
//	private static Set getDynamicSerialRecordDataDeclarationPropertyRules() {
//		if ( needRecalculateForNewAnno(DynamicSerialRecordDataDeclarationPropertyRules) ) {
//			DynamicSerialRecordDataDeclarationPropertyRules = new TreeSet();
//			DynamicSerialRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_MAXSIZE));
//			DynamicSerialRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
//			DynamicSerialRecordDataDeclarationPropertyRules.addAll(getSerialRecordPropertyRules());
//		}
//		return DynamicSerialRecordDataDeclarationPropertyRules;
//
//	}
//
//	private static Set DynamicRelativeRecordDataDeclarationPropertyRules;
//	private static Set getDynamicRelativeRecordDataDeclarationPropertyRules() {
//		if ( needRecalculateForNewAnno(DynamicRelativeRecordDataDeclarationPropertyRules) ) {
//			DynamicRelativeRecordDataDeclarationPropertyRules = new TreeSet();
//			DynamicRelativeRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_MAXSIZE));
//			DynamicRelativeRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
//			DynamicRelativeRecordDataDeclarationPropertyRules.addAll(getRelativeRecordPropertyRules());
//		}
//		return DynamicRelativeRecordDataDeclarationPropertyRules;
//
//	}
//
//	private static Set DynamicIndexedRecordDataDeclarationPropertyRules;
//	private static Set getDynamicIndexedRecordDataDeclarationPropertyRules() {
//		if ( needRecalculateForNewAnno(DynamicIndexedRecordDataDeclarationPropertyRules) ) {
//			DynamicIndexedRecordDataDeclarationPropertyRules = new TreeSet();
//			DynamicIndexedRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_MAXSIZE));
//			DynamicIndexedRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
//			DynamicIndexedRecordDataDeclarationPropertyRules.addAll(getIndexedRecordPropertyRules());
//		}
//		return DynamicIndexedRecordDataDeclarationPropertyRules;
//
//	}
//
//	private static Set DynamicBasicRecordDataDeclarationPropertyRules;
//	private static Set getDynamicBasicRecordDataDeclarationPropertyRules() {
//		if ( needRecalculateForNewAnno(DynamicBasicRecordDataDeclarationPropertyRules) ) {
//			DynamicBasicRecordDataDeclarationPropertyRules = new TreeSet();
//			DynamicBasicRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_MAXSIZE));
//			DynamicBasicRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
//			DynamicBasicRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_SELECTEDROWITEM));
//			DynamicBasicRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_SELECTEDVALUEITEM));
//			DynamicBasicRecordDataDeclarationPropertyRules.addAll(getBasicRecordPropertyRules());
//		}
//		return DynamicBasicRecordDataDeclarationPropertyRules;
//
//	}
//
//	private static Set StaticAnyRecordDataDeclarationPropertyRules;
//	private static Set getStaticAnyRecordDataDeclarationPropertyRules() {
//		if ( needRecalculateForNewAnno(StaticAnyRecordDataDeclarationPropertyRules) ) {
//			StaticAnyRecordDataDeclarationPropertyRules = new TreeSet();
//			StaticAnyRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_INITIALIZED));
//			StaticAnyRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
//			StaticAnyRecordDataDeclarationPropertyRules.addAll(getVGUIRecordPropertyRules());
//			StaticAnyRecordDataDeclarationPropertyRules.addAll(getSQLRecordPropertyRules());
//			StaticAnyRecordDataDeclarationPropertyRules.addAll(getMQRecordPropertyRules());
//			StaticAnyRecordDataDeclarationPropertyRules.addAll(getSerialRecordPropertyRules());
//			StaticAnyRecordDataDeclarationPropertyRules.addAll(getRelativeRecordPropertyRules());
//			StaticAnyRecordDataDeclarationPropertyRules.addAll(getIndexedRecordPropertyRules());
//			StaticAnyRecordDataDeclarationPropertyRules.addAll(getBasicRecordPropertyRules());
//		}
//		return StaticAnyRecordDataDeclarationPropertyRules;
//
//	}
//
//	private static Set StaticVGUIRecordDataDeclarationPropertyRules;
//	private static Set getStaticVGUIRecordDataDeclarationPropertyRules() {
//		if ( needRecalculateForNewAnno(StaticVGUIRecordDataDeclarationPropertyRules) ) {
//			StaticVGUIRecordDataDeclarationPropertyRules = new TreeSet();
//			StaticVGUIRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_INITIALIZED));
//			StaticVGUIRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
//			StaticVGUIRecordDataDeclarationPropertyRules.addAll(getVGUIRecordPropertyRules());
//		}
//		return StaticVGUIRecordDataDeclarationPropertyRules;
//
//	}
//
//	private static Set StaticSQLRecordDataDeclarationPropertyRules;
//	private static Set getStaticSQLRecordDataDeclarationPropertyRules() {
//		if ( needRecalculateForNewAnno(StaticSQLRecordDataDeclarationPropertyRules) ) {
//			StaticSQLRecordDataDeclarationPropertyRules = new TreeSet();
//			StaticSQLRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_INITIALIZED));
//			StaticSQLRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
//			StaticSQLRecordDataDeclarationPropertyRules.addAll(getSQLRecordPropertyRules());
//		}
//		return StaticSQLRecordDataDeclarationPropertyRules;
//
//	}
//
//	private static Set StaticMQRecordDataDeclarationPropertyRules;
//	private static Set getStaticMQRecordDataDeclarationPropertyRules() {
//		if ( needRecalculateForNewAnno(StaticMQRecordDataDeclarationPropertyRules) ) {
//			StaticMQRecordDataDeclarationPropertyRules = new TreeSet();
//			StaticMQRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_INITIALIZED));
//			StaticMQRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
//			StaticMQRecordDataDeclarationPropertyRules.addAll(getMQRecordPropertyRules());
//		}
//		return StaticMQRecordDataDeclarationPropertyRules;
//
//	}
//
//	private static Set StaticSerialRecordDataDeclarationPropertyRules;
//	private static Set getStaticSerialRecordDataDeclarationPropertyRules() {
//		if ( needRecalculateForNewAnno(StaticSerialRecordDataDeclarationPropertyRules) ) {
//			StaticSerialRecordDataDeclarationPropertyRules = new TreeSet();
//			StaticSerialRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_INITIALIZED));
//			StaticSerialRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
//			StaticSerialRecordDataDeclarationPropertyRules.addAll(getSerialRecordPropertyRules());
//		}
//		return StaticSerialRecordDataDeclarationPropertyRules;
//
//	}
//
//	private static Set StaticRelativeRecordDataDeclarationPropertyRules;
//	private static Set getStaticRelativeRecordDataDeclarationPropertyRules() {
//		if ( needRecalculateForNewAnno(StaticRelativeRecordDataDeclarationPropertyRules) ) {
//			StaticRelativeRecordDataDeclarationPropertyRules = new TreeSet();
//			StaticRelativeRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_INITIALIZED));
//			StaticRelativeRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
//			StaticRelativeRecordDataDeclarationPropertyRules.addAll(getRelativeRecordPropertyRules());
//		}
//		return StaticRelativeRecordDataDeclarationPropertyRules;
//
//	}
//
//	private static Set StaticIndexedRecordDataDeclarationPropertyRules;
//	private static Set getStaticIndexedRecordDataDeclarationPropertyRules() {
//		if ( needRecalculateForNewAnno(StaticIndexedRecordDataDeclarationPropertyRules) ) {
//			StaticIndexedRecordDataDeclarationPropertyRules = new TreeSet();
//			StaticIndexedRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_INITIALIZED));
//			StaticIndexedRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
//			StaticIndexedRecordDataDeclarationPropertyRules.addAll(getIndexedRecordPropertyRules());
//		}
//		return StaticIndexedRecordDataDeclarationPropertyRules;
//
//	}
//
//	private static Set StaticBasicRecordDataDeclarationPropertyRules;
//	private static Set getStaticBasicRecordDataDeclarationPropertyRules() {
//		if ( needRecalculateForNewAnno(StaticBasicRecordDataDeclarationPropertyRules) ) {
//			StaticBasicRecordDataDeclarationPropertyRules = new TreeSet();
//			StaticBasicRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_INITIALIZED));
//			StaticBasicRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
//			StaticBasicRecordDataDeclarationPropertyRules.addAll(getBasicRecordPropertyRules());
//		}
//		return StaticBasicRecordDataDeclarationPropertyRules;
//
//	}
//
//	private static Set NativeLibraryPropertyRules;
//	private static Set getNativeLibraryPropertyRules() {
//		if ( needRecalculateForNewAnno(NativeLibraryPropertyRules) ) {
//			NativeLibraryPropertyRules = new TreeSet();
//			NativeLibraryPropertyRules.addAll(createRulesFor(new TargetTypesHasAnnotationRecordFilter(ElementKind.LibraryPart)));
//			NativeLibraryPropertyRules.addAll(createRulesForFields(IEGLConstants.LIBRARY_SUBTYPE_NATIVE));
//		}
//		return NativeLibraryPropertyRules;
//
//	}
//
//	private static Set libraryPropertyRules;
//	private static Set getlibraryPropertyRules() {
//		if ( needRecalculateForNewAnno(libraryPropertyRules) ) {
//			libraryPropertyRules = new TreeSet();
//			libraryPropertyRules.addAll(createRulesFor(new TargetTypesHasAnnotationRecordFilter(ElementKind.LibraryPart)));
//			libraryPropertyRules.addAll(createRulesForFields(IEGLConstants.LIBRARY_SUBTYPE_BASIC));
//		}
//		return libraryPropertyRules;
//
//	}
//
//	private static Set CalledTextUIProgramPropertyRules;
//	private static Set getCalledTextUIProgramPropertyRules() {
//		if ( needRecalculateForNewAnno(CalledTextUIProgramPropertyRules) ) {
//			CalledTextUIProgramPropertyRules = new TreeSet();
//			CalledTextUIProgramPropertyRules.addAll(createRulesForElementKinds(ElementKind.ProgramPart));
//			CalledTextUIProgramPropertyRules.addAll(createRulesForFields(IEGLConstants.PROGRAM_SUBTYPE_TEXT_UI));
//		}
//		return CalledTextUIProgramPropertyRules;
//
//	}
//
//	private static Set TextUIProgramPropertyRules;
//	private static Set getTextUIProgramPropertyRules() {
//		if ( needRecalculateForNewAnno(TextUIProgramPropertyRules) ) {
//			TextUIProgramPropertyRules = new TreeSet();
//			TextUIProgramPropertyRules.addAll(createRulesForElementKinds(ElementKind.ProgramPart));
//			TextUIProgramPropertyRules.addAll(createRulesForFields(IEGLConstants.PROGRAM_SUBTYPE_TEXT_UI));
//		}
//		return TextUIProgramPropertyRules;
//
//	}
//
//	private static Set CalledBasicProgramPropertyRules;
//	private static Set getCalledBasicProgramPropertyRules() {
//		if ( needRecalculateForNewAnno(CalledBasicProgramPropertyRules) ) {
//			CalledBasicProgramPropertyRules = new TreeSet();
//			CalledBasicProgramPropertyRules.addAll(createRulesForElementKinds(ElementKind.ProgramPart));
//			CalledBasicProgramPropertyRules.addAll(createRulesForFields(IEGLConstants.PROGRAM_SUBTYPE_BASIC));
//		}
//		return CalledBasicProgramPropertyRules;
//
//	}
//
//	private static Set BasicProgramPropertyRules;
//	private static Set getBasicProgramPropertyRules() {
//		if ( needRecalculateForNewAnno(BasicProgramPropertyRules) ) {
//			BasicProgramPropertyRules = new TreeSet();
//			BasicProgramPropertyRules.addAll(createRulesForElementKinds(ElementKind.ProgramPart));
//			BasicProgramPropertyRules.addAll(createRulesForFields(IEGLConstants.PROGRAM_SUBTYPE_BASIC));
//		}
//		return BasicProgramPropertyRules;
//
//	}
//
//	private static Set VGWebTransactionPropertyRules;
//	private static Set getVGWebTransactionPropertyRules() {
//		if ( needRecalculateForNewAnno(VGWebTransactionPropertyRules) ) {
//			VGWebTransactionPropertyRules = new TreeSet();
//			VGWebTransactionPropertyRules.addAll(createRulesForElementKinds(ElementKind.ProgramPart));
//			VGWebTransactionPropertyRules.addAll(createRulesForFields(IEGLConstants.PROGRAM_SUBTYPE_VG_WEB_TRANSACTION));
//		}
//		return VGWebTransactionPropertyRules;
//
//	}
//
//	private static Set AllProgramPropertyRules;
//	private static Set getAllProgramPropertyRules() {
//		if ( needRecalculateForNewAnno(AllProgramPropertyRules) ) {
//			AllProgramPropertyRules = new TreeSet();
//			AllProgramPropertyRules.addAll(createRulesFor(new TargetTypesHasAnnotationRecordFilter(ElementKind.ProgramPart)));
//			AllProgramPropertyRules.addAll(createRulesForFields(IEGLConstants.PROGRAM_SUBTYPE_BASIC));
//		}
//		return AllProgramPropertyRules;
//
//	}
//
//	private static Set NativeLibraryFunctionPropertyRules;
//	private static Set getNativeLibraryFunctionPropertyRules() {
//		if ( needRecalculateForNewAnno(NativeLibraryFunctionPropertyRules) ) {
//			NativeLibraryFunctionPropertyRules = new TreeSet();
//			NativeLibraryFunctionPropertyRules.addAll(createRulesFor(new TargetTypesHasAnnotationRecordFilter(ElementKind.FunctionMbr)));
//		}
//		return NativeLibraryFunctionPropertyRules;
//	}
//
//	private static Set FunctionPropertyRules;
//	private static Set getFunctionPropertyRules() {
//		if ( needRecalculateForNewAnno(FunctionPropertyRules) ) {
//			FunctionPropertyRules = new TreeSet();
//			FunctionPropertyRules.addAll(createRulesFor(new TargetTypesHasAnnotationRecordFilter(ElementKind.FunctionPart)));
//		}
//		return FunctionPropertyRules;
//
//	}
//
//
//	private static Set AnyRecordPropertyRules;
//	private static Set getAnyRecordPropertyRules() {
//		if ( needRecalculateForNewAnno(AnyRecordPropertyRules) ) {
//			AnyRecordPropertyRules = new TreeSet();
//			AnyRecordPropertyRules.addAll(createRulesForElementKinds(new EnumerationDataBinding[] {ElementKind.RECORDPART, ElementKind.STRUCTUREDRECORDPART}));
//			AnyRecordPropertyRules.addAll(createRulesForFields(IEGLConstants.RECORD_SUBTYPE_BASIC));
//			AnyRecordPropertyRules.addAll(createRulesForFields(IEGLConstants.RECORD_SUBTYPE_EXCEPTION));
//		}
//		return AnyRecordPropertyRules;
//
//	}
//
//
//	private static Set SQLRecordPropertyRules;
//	private static Set getSQLRecordPropertyRules() {
//		if ( needRecalculateForNewAnno(SQLRecordPropertyRules) ) {
//			SQLRecordPropertyRules = new TreeSet();
//			SQLRecordPropertyRules.addAll(createRulesForElementKinds(new EnumerationDataBinding[] {ElementKind.RECORDPART, ElementKind.STRUCTUREDRECORDPART}));
//			SQLRecordPropertyRules.addAll(createRulesForFields(IEGLConstants.RECORD_SUBTYPE_SQl));
//		}
//		return SQLRecordPropertyRules;
//
//	}
//
//
//	
//	private static Set BasicRecordPropertyRules;
//	private static Set getBasicRecordPropertyRules() {
//		if ( needRecalculateForNewAnno(BasicRecordPropertyRules) ) {
//			BasicRecordPropertyRules = new TreeSet();
//			BasicRecordPropertyRules.addAll(createRulesForElementKinds(ElementKind.RecordPart));
//			BasicRecordPropertyRules.addAll(createRulesForFields(IEGLConstants.RECORD_SUBTYPE_BASIC));
//		}
//		return BasicRecordPropertyRules;
//
//	}
//
//	private static Set FillerStructureItemPropertyRules;
//	private static Set getFillerStructureItemPropertyRules() {
//		if ( needRecalculateForNewAnno(FillerStructureItemPropertyRules) ) {
//			FillerStructureItemPropertyRules = new TreeSet();
//			FillerStructureItemPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_UITYPE));
//		}
//		return FillerStructureItemPropertyRules;
//
//	}
//
//	private static Set StructureItemPropertyRules;
//	private static Set getStructureItemPropertyRules() {
//		if ( needRecalculateForNewAnno(StructureItemPropertyRules) ) {
//			StructureItemPropertyRules = new TreeSet();
//			StructureItemPropertyRules.addAll(createRulesForMemberAnnotations(IEGLConstants.RECORD_SUBTYPE_SQl));
//			StructureItemPropertyRules.addAll(createRulesForGroup("formatting"));
//			StructureItemPropertyRules.addAll(createRulesForGroup("validation"));
//			StructureItemPropertyRules.addAll(createRulesForGroup("ui"));
//			StructureItemPropertyRules.addAll(createRulesForMemberAnnotations(IEGLConstants.RECORD_SUBTYPE_DLI_SEGMENT));
//			StructureItemPropertyRules.addAll(createRulesForElementKinds(new EnumerationDataBinding[] {ElementKind.FIELDMBR}));
//		}
//		return StructureItemPropertyRules;
//
//	}
//	
//	private static Set dataItemPropertyRules;
//	private static Set getDataItemPropertyRules() {
//		if( needRecalculateForNewAnno(dataItemPropertyRules) ) {
//			dataItemPropertyRules = new TreeSet();
//			dataItemPropertyRules.addAll(createRulesFor(new TargetTypesHasAnnotationRecordFilter(ElementKind.DATAITEMPART)));
//		}
//		return dataItemPropertyRules;
//	}
//
//    public static Collection getPropertyRules( int location,
//			ExternalTypeBinding externalTypeBinding )
//	{
//		switch ( location )
//		{
//			case locationConsoleButton:
//				return getConsoleButtonPropertyRules( externalTypeBinding );
//			case locationConsoleCheckbox:
//				return getConsoleCheckboxPropertyRules( externalTypeBinding );
//			case locationConsoleRadiogroup:
//				return getConsoleRadiogroupPropertyRules( externalTypeBinding );
//			case locationConsoleCombo:
//				return getConsoleComboPropertyRules( externalTypeBinding );
//			case locationConsoleList:
//				return getConsoleListPropertyRules( externalTypeBinding );
//			default:
//				return null;
//		}
//	}
//
//	private static Set ConsoleWidgetPropertyRules = new TreeSet();
//
//	private static Set getConsoleWidgetPropertyRules(
//			ExternalTypeBinding externalTypeBinding )
//	{
//		ConsoleWidgetPropertyRules.clear();
//		if ( externalTypeBinding != null )
//		{
//			ConsoleWidgetPropertyRules
//					.addAll( createRulesFor( IEGLConstants.PROPERTY_BOUNDS ) );
//			for ( Iterator iter = externalTypeBinding.getDeclaredAndInheritedData()
//					.iterator(); iter.hasNext(); )
//			{
//				ClassFieldBinding classFieldBinding = (ClassFieldBinding)iter.next();
//				if ( classFieldBinding.isReadOnly() )
//				{
//					continue;
//				}
//				else
//				{
//					ConsoleWidgetPropertyRules.add( new EGLPropertyRule(
//							classFieldBinding ) );
//				}
//			}
//		}
//		return ConsoleWidgetPropertyRules;
//	}
//
//	private static Set ConsoleButtonPropertyRules = new TreeSet();
//
//	private static Set getConsoleButtonPropertyRules(
//			ExternalTypeBinding externalTypeBinding )
//	{
//		if ( ConsoleButtonPropertyRules.size() == 0 )
//		{
//			ConsoleButtonPropertyRules
//					.addAll( getConsoleWidgetPropertyRules( externalTypeBinding ) );
//		}
//		return ConsoleButtonPropertyRules;
//	}
//
//	private static Set ConsoleCheckboxPropertyRules = new TreeSet();
//
//	private static Set getConsoleCheckboxPropertyRules(
//			ExternalTypeBinding externalTypeBinding )
//	{
//		if ( ConsoleCheckboxPropertyRules.size() == 0 )
//		{
//			ConsoleCheckboxPropertyRules
//					.addAll( getConsoleWidgetPropertyRules( externalTypeBinding ) );
//		}
//		return ConsoleCheckboxPropertyRules;
//	}
//
//	private static Set ConsoleRadiogroupPropertyRules = new TreeSet();
//
//	private static Set getConsoleRadiogroupPropertyRules(
//			ExternalTypeBinding externalTypeBinding )
//	{
//		if ( ConsoleRadiogroupPropertyRules.size() == 0 )
//		{
//			ConsoleRadiogroupPropertyRules
//					.addAll( getConsoleWidgetPropertyRules( externalTypeBinding ) );
//		}
//		return ConsoleRadiogroupPropertyRules;
//	}
//
//	private static Set ConsoleComboPropertyRules = new TreeSet();
//
//	private static Set getConsoleComboPropertyRules(
//			ExternalTypeBinding externalTypeBinding )
//	{
//		if ( ConsoleComboPropertyRules.size() == 0 )
//		{
//			ConsoleComboPropertyRules
//					.addAll( getConsoleWidgetPropertyRules( externalTypeBinding ) );
//		}
//		return ConsoleComboPropertyRules;
//	}
//
//	private static Set ConsoleListPropertyRules = new TreeSet();
//
//	private static Set getConsoleListPropertyRules(
//			ExternalTypeBinding externalTypeBinding )
//	{
//		if ( ConsoleListPropertyRules.size() == 0 )
//		{
//			ConsoleListPropertyRules
//					.addAll( getConsoleWidgetPropertyRules( externalTypeBinding ) );
//		}
//		return ConsoleListPropertyRules;
//	}	
//	
//	private static interface AnnotationRecordFilter {
//		boolean passes(AnnotationType aTypeBinding); 
//	}
//	
//	private static class TargetTypesHasAnnotationRecordFilter implements AnnotationRecordFilter {
//		private List<ElementKind> allowedTargets;
//
//		public TargetTypesHasAnnotationRecordFilter(ElementKind allowedTarget) {
//			allowedTargets = new ArrayList<ElementKind>();
//			allowedTargets.add(allowedTarget);
//		}
//		
//		public TargetTypesHasAnnotationRecordFilter(List<ElementKind> allowedTargets) {
//			this.allowedTargets = allowedTargets;
//		}
//		
//		public boolean passes(AnnotationType annType) {
//			
//			for (ElementKind allowedTarget : allowedTargets) {
//				if (BindingUtil.isApplicableFor(allowedTarget, annType.getTargets(), false)) {
//					return true;
//				}
//			}
//			
//			return false;
//		}
//	}
//	
//	private static Collection createRulesFor(AnnotationRecordFilter filter) {
//		return createRulesFor(filter, false);
//	}
//	
//	private static Collection createRulesFor(AnnotationRecordFilter filter, boolean subtypes) {
//		Set result = new TreeSet(new EGLCaseInsensitiveComparator());
//		if(null == annoTypeMgr){ 
//			return result;
//		}
//		
//		for(Iterator iter = annoTypeMgr.getSystemPackageAnnotations().values().iterator(); iter.hasNext();) {
//			FlexibleRecordBinding rec = (FlexibleRecordBinding) iter.next();
//			if(rec.getAnnotation(AnnotationAnnotationTypeBinding.getInstance()) != null) {
//				AnnotationTypeBindingImpl aTypeBindingImpl = new AnnotationTypeBindingImpl(rec, null);
//				if(AnnotationAnnotationTypeBinding.getInstance() == aTypeBindingImpl.getAnnotationRecord().getSubType()) {
//					if(subtypes && aTypeBindingImpl.isPartSubType() ||
//					   !subtypes && !aTypeBindingImpl.isPartSubType()) {
//						if(filter.passes(aTypeBindingImpl)) {
//							result.add(new EGLPropertyRule(aTypeBindingImpl));
//						}
//					}
//				}
//			}
//		}
//		return result;
//	}
//	
//	private static Collection createRulesForFields(String complexPropertyName) {
//		return createRulesForField(complexPropertyName, null);
//	}
//	
//	private static Collection createRulesForField(String complexPropertyName, String fieldName) {
//		Set result = new TreeSet(new EGLCaseInsensitiveComparator());
//		
//		if(null == annoTypeMgr){ 
//			return result;
//		}
//		FlexibleRecordBinding rec = (FlexibleRecordBinding) annoTypeMgr.getSystemPackageAnnotations().get(InternUtil.intern(complexPropertyName));
//		if(rec != null) {
//			IDataBinding[] fields = rec.getFields();
//			for(int i = 0; i < fields.length; i++) {
//				if(fieldName == null || fields[i].getName().equalsIgnoreCase(fieldName)) {
//					EGLPropertyRule propertyRule = new EGLPropertyRule(fields[i]);
//					propertyRule.setAnnotationField(true);
//					result.add(propertyRule);
//				}
//			}
//		}
//
//		return result;
//	}
//	
//	public static Collection createRulesFor(String propertyName) {
//
//		if(null == annoTypeMgr){
//			return(Collections.EMPTY_LIST);
//		}
//		FlexibleRecordBinding rec = (FlexibleRecordBinding)annoTypeMgr.getSystemPackageAnnotations().get(InternUtil.intern(propertyName));
//		if(rec != null) {
//			AnnotationTypeBindingImpl aTypeBindingImpl = new AnnotationTypeBindingImpl(rec, null);
//			return Arrays.asList(new EGLPropertyRule[] {new EGLPropertyRule(aTypeBindingImpl)});
//		}
//		return Collections.EMPTY_LIST;
//	}
//	
//	public static Collection createRulesForMemberAnnotations(String stereotypeName) {
//		Set result = new TreeSet();
//		if(null == annoTypeMgr){
//			return(result);
//		}
//		FlexibleRecordBinding rec = (FlexibleRecordBinding) annoTypeMgr.getSystemPackageAnnotations().get(InternUtil.intern(stereotypeName));
//		if(rec != null) {
//			IAnnotationBinding aBinding = rec.getAnnotation(StereotypeAnnotationTypeBinding.getInstance());
//			if(aBinding != null) {
//				aBinding = (IAnnotationBinding) aBinding.findData("memberAnnotations");
//				if(IBinding.NOT_FOUND_BINDING != aBinding) {
//					Object[] recs = (Object[]) aBinding.getValue();
//					for(int i = 0; i < recs.length; i++) {						
//						result.addAll(createRulesFor(((IBinding) recs[i]).getName()));
//					}
//				}
//			}
//		}
//		return result;
//	}
//		
//	public static Collection createRulesForElementKinds(ElementKind elementKind) {
//		return createRulesFor(new TargetTypesHasAnnotationRecordFilter(elementKind));
//	}
//	
//	public static Collection createRulesForElementKinds(List<ElementKind> elementKinds) {
//		return createRulesFor(new TargetTypesHasAnnotationRecordFilter(elementKinds));
//	}
//	
//	public static Collection createRulesForSubtypes(ElementKind elementKind) {
//		return createRulesFor(new TargetTypesHasAnnotationRecordFilter(elementKind), true);
//	}
//	
//	public static Collection createRulesForSubtypes(List<ElementKind> elementKinds) {
//		return createRulesFor(new TargetTypesHasAnnotationRecordFilter(elementKinds), true);
//	}
}
