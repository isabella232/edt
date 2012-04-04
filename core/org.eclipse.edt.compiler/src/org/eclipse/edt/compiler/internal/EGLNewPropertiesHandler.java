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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.edt.compiler.binding.AnnotationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.ClassFieldBinding;
import org.eclipse.edt.compiler.binding.EnumerationDataBinding;
import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.annotationType.AnnotationTypeBindingImpl;
import org.eclipse.edt.compiler.binding.annotationType.AnnotationTypeManager;
import org.eclipse.edt.compiler.binding.annotationType.EGLAnnotationGroupAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.annotationType.StereotypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.ElementKind;
import org.eclipse.edt.mof.egl.utils.InternUtil;


public class EGLNewPropertiesHandler {
	
	private static Set REPLACEME = Collections.EMPTY_SET;
	
	/*This is not thread safe
	 *You need to pass a AnnotationTypeManager into the class when you want to use 
	 *getPropertyRules, 
	 *createRulesForSubtypes,
	 *createRulesFor,
	 *createRulesForElementKinds functions of this class*/
	/*Different project may have different enviroment thus different AnnotationTypeManager.
	 * If new AnnotationTypeManager provided everything need to be calculate again.
	 */
	private static AnnotationTypeManager oldAnnoTypeMgr = null;
	private static AnnotationTypeManager annoTypeMgr = null;
	public static void setAnnoTypeMgr(AnnotationTypeManager aAnnoTypeMgr){
		oldAnnoTypeMgr = annoTypeMgr;
		annoTypeMgr = aAnnoTypeMgr;
	}
	
	private static boolean needRecalculateForNewAnno(Set annoSet){
		return(oldAnnoTypeMgr != annoTypeMgr || annoSet == null);
	}
	
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
    public static final int bidiEnabled = 12;
    
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
    public static final int locationCSVRecord = 48;    
    public static final int locationAnyRecord = 49;
    public static final int locationFillerStructureItem = 50;

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
    public static final int locationJavaObject = 102;
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
    
    public static final int locationServiceClassDeclaration = 122;
    public static final int locationNewExpression = 123;
    public static final int locationExternalTypeClassDeclaration = 124;
    public static final int locationExternalTypeArrayHandlerClassDeclaration = 125;
    public static final int locationExternalTypeFunction = 126;
    
    public static final int allNonSubtype = 127;
    
    public static final int locationCall = 128;
    public static final int locationTransfer = 129;
    public static final int locationShow = 130;
    
    public static final int locationConsoleButton = 131;
    public static final int locationConsoleRadiogroup = 132;
    public static final int locationConsoleCheckbox = 133;
    public static final int locationConsoleList = 134;
    public static final int locationConsoleCombo = 135;
    
    public static final int locationDynamicConsoleForm = 136;
    public static final int locationDynamicPSBRecord = 137;
    public static final int locationDynamicDLISegment = 138;
    
    public static final int locationJavaScriptObject = 139;
    public static final int locationHostProgram = 140;

    public static final int locationExit = 141;

    public static Collection getPropertyRules(int location) {
		switch (location) {
		case locationDataItem:
			return getDataItemPropertyRules();
		case locationStructureItem:
			return getStructureItemPropertyRules();
		case locationFillerStructureItem:
			return getFillerStructureItemPropertyRules();
		case locationBasicRecord:
			return getBasicRecordPropertyRules();
		case locationIndexedRecord:
			return getIndexedRecordPropertyRules();
		case locationRelativeRecord:
			return getRelativeRecordPropertyRules();
		case locationSerialRecord:
			return getSerialRecordPropertyRules();
		case locationMQRecord:
			return getMQRecordPropertyRules();
		case locationSQLRecord:
			return getSQLRecordPropertyRules();
		case locationCSVRecord:
			return getCSVRecordPropertyRules();
		case locationVGUIRecord:
			return getVGUIRecordPropertyRules();
		case locationAnyRecord:
			return getAnyRecordPropertyRules();
		case locationFormGroup:
			return getFormGroupPropertyRules();
		case locationScreenFloatArea:
			return getScreenFloatingAreaPropertyRules();
		case locationPrintFloatArea:
			return getPrintFloatingAreaPropertyRules();
		case locationTextConstantFormField:
			return getTextConstantFormFieldPropertyRules();
		case locationPrintConstantFormField:
			return getPrintConstantFormFieldPropertyRules();
		case locationTextVariableFormField:
			return getTextVariableFormFieldPropertyRules();
		case locationTuiTextVariableFormField:
			return getTuiTextVariableFormFieldPropertyRules();
		case locationTuiPrintVariableFormField:
			return getTuiPrintVariableFormFieldPropertyRules();
		case locationPrintVariableFormField:
			return getPrintVariableFormFieldPropertyRules();
		case locationTextFormDeclaration:
			return getTextFormPropertyRules();
		case locationPrintFormDeclaration:
			return getPrintFormPropertyRules();
		case locationPageHandlerDeclaration:
			return getPageHandlerPropertyRules();
		case locationDataTable:
			return getDataTablePropertyRules();
		case locationFunction:
			return getFunctionPropertyRules();
		case locationNativeLibraryFunction:
			return getNativeLibraryFunctionPropertyRules();
		case locationProgram:
			return getAllProgramPropertyRules();
		case locationVGWebTransaction:
			return getVGWebTransactionPropertyRules();
		case locationBasicProgram:
			return getBasicProgramPropertyRules();
		case locationCalledBasicProgram:
			return getCalledBasicProgramPropertyRules();
		case locationTextUIProgram:
			return getTextUIProgramPropertyRules();
		case locationCalledTextUIProgram:
			return getCalledTextUIProgramPropertyRules();
		case locationLibrary:
			return getlibraryPropertyRules();
		case locationNativeLibrary:
			return getNativeLibraryPropertyRules();
		case locationStaticBasicRecordDataDeclaration:
			return getStaticBasicRecordDataDeclarationPropertyRules();
		case locationStaticIndexedRecordDataDeclaration:
			return getStaticIndexedRecordDataDeclarationPropertyRules();
		case locationStaticRelativeRecordDataDeclaration:
			return getStaticRelativeRecordDataDeclarationPropertyRules();
		case locationStaticSerialRecordDataDeclaration:
			return getStaticSerialRecordDataDeclarationPropertyRules();
		case locationStaticMQRecordDataDeclaration:
			return getStaticMQRecordDataDeclarationPropertyRules();
		case locationStaticSQLRecordDataDeclaration:
			return getStaticSQLRecordDataDeclarationPropertyRules();
		case locationStaticVGUIRecordDataDeclaration:
			return getStaticVGUIRecordDataDeclarationPropertyRules();
		case locationStaticAnyRecordDataDeclaration:
			return getStaticAnyRecordDataDeclarationPropertyRules();
		case locationDynamicBasicRecordDataDeclaration:
			return getDynamicBasicRecordDataDeclarationPropertyRules();
		case locationDynamicIndexedRecordDataDeclaration:
			return getDynamicIndexedRecordDataDeclarationPropertyRules();
		case locationDynamicRelativeRecordDataDeclaration:
			return getDynamicRelativeRecordDataDeclarationPropertyRules();
		case locationDynamicSerialRecordDataDeclaration:
			return getDynamicSerialRecordDataDeclarationPropertyRules();
		case locationDynamicMQRecordDataDeclaration:
			return getDynamicMQRecordDataDeclarationPropertyRules();
		case locationDynamicSQLRecordDataDeclaration:
			return getDynamicSQLRecordDataDeclarationPropertyRules();
		case locationDynamicVGUIRecordDataDeclaration:
			return getDynamicVGUIRecordDataDeclarationPropertyRules();
		case locationDynamicAnyRecordDataDeclaration:
			return getDynamicAnyRecordDataDeclarationPropertyRules();
		case locationStaticItemDataDeclaration:
			return getStaticItemDataDeclarationPropertyRules();
		case locationDynamicItemDataDeclaration:
			return getDynamicItemDataDeclarationPropertyRules();
		case locationUseDeclaration:
			return getUseDeclarationPropertyRules();
		case locationDataTableUseDeclaration:
			return getDataTableUseDeclarationPropertyRules();
		case locationFormGroupUseDeclaration:
			return getFormGroupUseDeclarationPropertyRules();
		case locationLibraryUseDeclaration:
			return getLibraryUseDeclarationPropertyRules();
		case locationFormUseDeclaration:
			return getFormUseDeclarationPropertyRules();
		case locationStaticPageItemDataDeclaration:
			return getStaticPageItemDataDeclarationPropertyRules();
		case locationDynamicPageItemDataDeclaration:
			return getDynamicPageItemDataDeclarationPropertyRules();

		case locationFormatting:
			return getDataItemFormattingPropertyRules();
		case locationSqlItem:
			return getDataItemSQLItemPropertyRules();
		case locationPageItem:
			return getDataItemPageItemPropertyRules();
		case locationUIItem:
			return getDataItemUIItemPropertyRules();
		case locationSAUIItem:
			return getDataItemUIItemSAPropertyRules();
		case locationSATUIItem:
			return getDataItemTUISAPropertyRules();
		case locationDL1Item:
			return getDataItemDL1ItemPropertyRules();
		case locationPsbRecordItem:
			return getPsbRecordItemPropertyRules();
		case locationValidation:
			return getDataItemValidationPropertyRules();
		case locationFieldPresentation:
			return getDataItemFieldPresentationPropertyRules();
		case locationTuiFieldPresentation:
			return getTuiFieldPresentationPropertyRules();
		case locationDoubleByteDevicePresentation:
			return getDataItemDoubleByteDevicePresentationPropertyRules();
		case locationVariableField:
			return getDataItemVariableFieldPropertyRules();
		case locationItemFormField:
			return getDataItemItemFormFieldPropertyRules();
		case locationDictionary:
			return getDictionaryPropertyRules();
		case locationConsoleForm:
			return getConsoleFormPropertyRules();
		case locationDynamicConsoleForm:
			return getDynamicConsoleFormPropertyRules();
		case locationConsoleField:
			return getConsoleFieldPropertyRules();
		case locationConsoleArrayField:
			return getConsoleArrayFieldPropertyRules();
		case locationWindow:
			return getWindowPropertyRules();
		case locationPresentationAttributes:
			return getPresentationAttributesPropertyRules();
		case locationMenu:
			return getMenuPropertyRules();
		case locationMenuItem:
			return getMenuItemPropertyRules();
		case locationPrompt:
			return getPromptPropertyRules();
		case locationOpenUI:
			return getOpenUIPropertyRules();
		case locationCall:
			return getCallPropertyRules();
		case locationExit:
			return getExitPropertyRules();
		case locationTransfer:
			return getTransferPropertyRules();
		case locationShow:
			return getShowPropertyRules();
		case locationCommonVariableFormField:
			return getCommonVariableFormFieldPropertyRules();
		case locationFormField:
			return getFormFieldPropertyRules();
		case locationTuiArrayElementFormField:
			return getTuiArrayElementFormFieldPropertyRules();
		case locationPSBRecord:
			return getPSBRecordPropertyRules();
		case locationDynamicPSBRecord:
			return getDynamicPSBRecordPropertyRules();
		case locationDLISegment:
			return getDLISegmentPropertyRules();
		case locationDynamicDLISegment:
			return getDynamicDLISegmentPropertyRules();
		case locationService:
			return getServicePropertyRules();
		case locationServiceDeclaration:
			return getServiceDeclarationPropertyRules();
		case locationServiceFunction:
			return getServiceFunctionPropertyRules();
		case locationBasicInterface:
			return getBasicInterfacePropertyRules();
		case locationJavaObject:
			return getJavaObjectPropertyRules();
		case locationJavaScriptObject:
			return getJavaScriptObjectPropertyRules();
		case locationHostProgram:
			return getHostProgramPropertyRules();
		case locationInterfaceDeclaration:
			return getInterfaceDeclarationPropertyRules();
		case locationBasicAbstractFunction:
			return getBasicAbstractFunctionPropertyRules();
		case locationJavaOnlyAbstractFunction:
			return getJavaOnlyAbstractFunctionPropertyRules();
		case locationLinkParms:
			return getLinkParmsPropertyRules();
		case locationPcbParms:
			return getPcbParmsPropertyRules();
		case locationProgramLinkData:
			return createRulesForFields(IEGLConstants.PROPERTY_PROGRAMLINKDATA);
		case locationLinkParameter:
			return createRulesForFields(IEGLConstants.PROPERTY_LINKPARAMETER);
		case locationDLI:
			return createRulesForFields(IEGLConstants.PROPERTY_DLI);
		case locationPCB:
			return createRulesForFields(IEGLConstants.PROPERTY_PCB);
		case locationRelationship:
			return createRulesForFields(IEGLConstants.PROPERTY_RELATIONSHIP);
		case locationEGLBinding:
			return createRulesForFields(IEGLConstants.PROPERTY_EGLBINDING);
		case locationWebBinding:
			return createRulesForFields(IEGLConstants.PROPERTY_WEBBINDING);
		case locationHandler:
			return getAllHandlerPropertyRules();
		case locationServiceClassDeclaration:
			return getServiceStaticItemDataDeclarationPropertyRules();
		case locationNewExpression:
			return getNewExpressionPropertyRules();
		case locationExternalTypeClassDeclaration:
			return getExternalTypeClassDeclartionPropertyRules();
		case locationExternalTypeArrayHandlerClassDeclaration:
			return getExternalTypeArrayHandlerClassDeclartionPropertyRules();
		case locationExternalTypeFunction:
			return getExternalTypeFunctionPropertyRules();
		case locationConsoleButton:
			return getConsoleButtonPropertyRules( null );
		case locationConsoleCheckbox:
			return getConsoleCheckboxPropertyRules( null );
		case locationConsoleRadiogroup:
			return getConsoleRadiogroupPropertyRules( null );
		case locationConsoleCombo:
			return getConsoleComboPropertyRules( null );
		case locationConsoleList:
			return getConsoleListPropertyRules( null );			
		case allNonSubtype:
			return createRulesFor(new AnnotationRecordFilter() {
				public boolean passes(AnnotationTypeBindingImpl aTypeBinding) {
					return true;
				}
			}, false);
		default:
			return null;
		}
	}
	
	private static Set AllHandlerPropertyRules;
	private static Set getAllHandlerPropertyRules() {
		if ( needRecalculateForNewAnno(AllHandlerPropertyRules) ) {
			AllHandlerPropertyRules = new TreeSet();
			AllHandlerPropertyRules.addAll(createRulesForElementKinds(ElementKind.HANDLERPART));
		}
		return AllHandlerPropertyRules;

	}

	private static Set PcbParmsPropertyRules;
	private static Set getPcbParmsPropertyRules() {
		if ( needRecalculateForNewAnno(PcbParmsPropertyRules) ) {
			PcbParmsPropertyRules = new TreeSet();
			PcbParmsPropertyRules.addAll(createRulesForField(IEGLConstants.PROPERTY_DLI, IEGLConstants.PROPERTY_PCBPARMS));
		}
		return PcbParmsPropertyRules;

	}

	private static Set LinkParmsPropertyRules;
	private static Set getLinkParmsPropertyRules() {
		if ( needRecalculateForNewAnno(LinkParmsPropertyRules) ) {
			LinkParmsPropertyRules = new TreeSet();
			LinkParmsPropertyRules.addAll(createRulesForField(IEGLConstants.PROPERTY_PROGRAMLINKDATA, IEGLConstants.PROPERTY_LINKPARMS));
		}
		return LinkParmsPropertyRules;

	}

	private static Set JavaOnlyAbstractFunctionPropertyRules;
	private static Set getJavaOnlyAbstractFunctionPropertyRules() {
		if ( needRecalculateForNewAnno(JavaOnlyAbstractFunctionPropertyRules) ) {
			JavaOnlyAbstractFunctionPropertyRules = new TreeSet();
			JavaOnlyAbstractFunctionPropertyRules.addAll(REPLACEME);
		}
		return JavaOnlyAbstractFunctionPropertyRules;

	}

	private static Set ExternalTypeFunctionPropertyRules;
	private static Set getExternalTypeFunctionPropertyRules() {
		if ( needRecalculateForNewAnno(ExternalTypeFunctionPropertyRules) ) {
			ExternalTypeFunctionPropertyRules = new TreeSet();
			ExternalTypeFunctionPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_JAVANAME));
			ExternalTypeFunctionPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_THROWSEXCEPTIONS));
		}
		return ExternalTypeFunctionPropertyRules;

	}

	private static Set BasicAbstractFunctionPropertyRules;
	private static Set getBasicAbstractFunctionPropertyRules() {
		if ( needRecalculateForNewAnno(BasicAbstractFunctionPropertyRules) ) {
			BasicAbstractFunctionPropertyRules = new TreeSet();
			BasicAbstractFunctionPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_XML));
		}
		return BasicAbstractFunctionPropertyRules;

	}  

	private static Set InterfaceDeclarationPropertyRules;
	private static Set getInterfaceDeclarationPropertyRules() {
		if ( needRecalculateForNewAnno(InterfaceDeclarationPropertyRules) ) {
			InterfaceDeclarationPropertyRules = new TreeSet();
			InterfaceDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_BINDSERVICE));
			InterfaceDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_RESTBINDING));
			InterfaceDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_WEBBINDING));
			InterfaceDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_RESOURCE));
			InterfaceDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_DEDICATEDSERVICE));
		}
		return InterfaceDeclarationPropertyRules;

	}

	private static Set JavaObjectPropertyRules;
	private static Set getJavaObjectPropertyRules() {
		if ( needRecalculateForNewAnno(JavaObjectPropertyRules) ) {
			JavaObjectPropertyRules = new TreeSet();
			JavaObjectPropertyRules.addAll(createRulesForElementKinds(ElementKind.EXTERNALTYPEPART));
			JavaObjectPropertyRules.addAll(createRulesForFields(IEGLConstants.EXTERNALTYPE_SUBTYPE_JAVAOBJECT));
		}
		return JavaObjectPropertyRules;

	}

	private static Set JavaScriptObjectPropertyRules;
	private static Set getJavaScriptObjectPropertyRules() {
		if ( needRecalculateForNewAnno(JavaScriptObjectPropertyRules) ) {
			JavaScriptObjectPropertyRules = new TreeSet();
			JavaScriptObjectPropertyRules.addAll(createRulesForElementKinds(ElementKind.EXTERNALTYPEPART));
			JavaScriptObjectPropertyRules.addAll(createRulesForFields(IEGLConstants.EXTERNALTYPE_SUBTYPE_JAVASCRIPTOBJECT));
		}
		return JavaScriptObjectPropertyRules;

	}

	private static Set HostProgramPropertyRules;
	private static Set getHostProgramPropertyRules() {
		if ( needRecalculateForNewAnno(HostProgramPropertyRules) ) {
			HostProgramPropertyRules = new TreeSet();
			HostProgramPropertyRules.addAll(createRulesForElementKinds(ElementKind.EXTERNALTYPEPART));
			HostProgramPropertyRules.addAll(createRulesForFields(IEGLConstants.EXTERNALTYPE_SUBTYPE_HOSTPROGRAM));
		}
		return HostProgramPropertyRules;

	}

	private static Set BasicInterfacePropertyRules;
	private static Set getBasicInterfacePropertyRules() {
		if ( needRecalculateForNewAnno(BasicInterfacePropertyRules) ) {
			BasicInterfacePropertyRules = new TreeSet();
			BasicInterfacePropertyRules.addAll(createRulesForElementKinds(ElementKind.INTERFACEPART));
		}
		return BasicInterfacePropertyRules;

	}

	private static Set ServiceFunctionPropertyRules;
	private static Set getServiceFunctionPropertyRules() {
		if ( needRecalculateForNewAnno(ServiceFunctionPropertyRules) ) {
			ServiceFunctionPropertyRules = new TreeSet();
			ServiceFunctionPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_XML));
		}
		return ServiceFunctionPropertyRules;

	}

	private static Set ServiceDeclarationPropertyRules;
	private static Set getServiceDeclarationPropertyRules() {
		if ( needRecalculateForNewAnno(ServiceDeclarationPropertyRules) ) {
			ServiceDeclarationPropertyRules = new TreeSet();
			ServiceDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_BINDSERVICE));
			ServiceDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_DEDICATEDSERVICE));
			ServiceDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_RESOURCE));
			ServiceDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_RESTBINDING));
			ServiceDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_WEBBINDING));
		}
		return ServiceDeclarationPropertyRules;

	}

	private static Set ServicePropertyRules;
	private static Set getServicePropertyRules() {
		if ( needRecalculateForNewAnno(ServicePropertyRules) ) {
			ServicePropertyRules = new TreeSet();
			ServicePropertyRules.addAll(createRulesForElementKinds(ElementKind.SERVICEPART));
		}
		return ServicePropertyRules;

	}

	private static Set DLISegmentPropertyRules;
	private static Set getDLISegmentPropertyRules() {
		if ( needRecalculateForNewAnno(DLISegmentPropertyRules) ) {
			DLISegmentPropertyRules = new TreeSet();
			DLISegmentPropertyRules.addAll(createRulesForElementKinds(ElementKind.RECORDPART));
			DLISegmentPropertyRules.addAll(createRulesForFields(IEGLConstants.RECORD_SUBTYPE_DLI_SEGMENT));
		}
		return DLISegmentPropertyRules;

	}
	
	private static Set DynamicDLISegmentPropertyRules;
	private static Set getDynamicDLISegmentPropertyRules() {
		if ( needRecalculateForNewAnno(DynamicDLISegmentPropertyRules) ) {
			DynamicDLISegmentPropertyRules = new TreeSet();
			DynamicDLISegmentPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_MAXSIZE));
			DynamicDLISegmentPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
			DynamicDLISegmentPropertyRules.addAll(createRulesForElementKinds(ElementKind.RECORDPART));
			DynamicDLISegmentPropertyRules.addAll(createRulesForFields(IEGLConstants.RECORD_SUBTYPE_DLI_SEGMENT));
		}
		return DynamicDLISegmentPropertyRules;

	}

	private static Set PSBRecordPropertyRules;
	private static Set getPSBRecordPropertyRules() {
		if ( needRecalculateForNewAnno(PSBRecordPropertyRules) ) {
			PSBRecordPropertyRules = new TreeSet();
			PSBRecordPropertyRules.addAll(createRulesForElementKinds(ElementKind.RECORDPART));
			PSBRecordPropertyRules.addAll(createRulesForFields(IEGLConstants.RECORD_SUBTYPE_PSB_RECORD));
		}
		return PSBRecordPropertyRules;

	}
	
	private static Set DynamicPSBRecordPropertyRules;
	private static Set getDynamicPSBRecordPropertyRules() {
		if ( needRecalculateForNewAnno(DynamicPSBRecordPropertyRules) ) {
			DynamicPSBRecordPropertyRules = new TreeSet();
			DynamicPSBRecordPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_MAXSIZE));
			DynamicPSBRecordPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
			DynamicPSBRecordPropertyRules.addAll(createRulesForElementKinds(ElementKind.RECORDPART));
			DynamicPSBRecordPropertyRules.addAll(createRulesForFields(IEGLConstants.RECORD_SUBTYPE_PSB_RECORD));
		}
		return DynamicPSBRecordPropertyRules;

	}

	private static Set TuiArrayElementFormFieldPropertyRules;
	private static Set getTuiArrayElementFormFieldPropertyRules() {
		if ( needRecalculateForNewAnno(TuiArrayElementFormFieldPropertyRules) ) {
			TuiArrayElementFormFieldPropertyRules = new TreeSet();
			TuiArrayElementFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_VALUE));
			TuiArrayElementFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_CURSOR));
			TuiArrayElementFormFieldPropertyRules.addAll(createRulesForGroup("fieldPresentation"));
			TuiArrayElementFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_OUTLINE));
			TuiArrayElementFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_MODIFIED));
			TuiArrayElementFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_PROTECT));
		}
		return TuiArrayElementFormFieldPropertyRules;

	}

	private static Set FormFieldPropertyRules;
	private static Set getFormFieldPropertyRules() {
		if ( needRecalculateForNewAnno(FormFieldPropertyRules) ) {
			FormFieldPropertyRules = new TreeSet();
			FormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_FIELDLEN));
			FormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_POSITION));
			FormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_VALUE));
		}
		return FormFieldPropertyRules;

	}

	private static Set CommonVariableFormFieldPropertyRules;
	private static Set getCommonVariableFormFieldPropertyRules() {
		if ( needRecalculateForNewAnno(CommonVariableFormFieldPropertyRules) ) {
			CommonVariableFormFieldPropertyRules = new TreeSet();
			CommonVariableFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_COLUMNS));
			CommonVariableFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_LINESBETWEENROWS));
			CommonVariableFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_SPACESBETWEENCOLUMNS));
			CommonVariableFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_INDEXORIENTATION));
		}
		return CommonVariableFormFieldPropertyRules;

	}

	private static Set OpenUIPropertyRules;
	private static Set getOpenUIPropertyRules() {
		if ( needRecalculateForNewAnno(OpenUIPropertyRules) ) {
			OpenUIPropertyRules = new TreeSet();
			OpenUIPropertyRules.addAll(createRulesForElementKinds(ElementKind.OPENUISTATEMENT));
		}
		return OpenUIPropertyRules;

	}
	
	private static Set CallPropertyRules;
	private static Set getCallPropertyRules() {
		if ( needRecalculateForNewAnno(CallPropertyRules) ) {
			CallPropertyRules = new TreeSet();
			CallPropertyRules.addAll(createRulesForElementKinds(ElementKind.CALLSTATEMENT));
		}
		return CallPropertyRules;

	}

	private static Set ExitPropertyRules;
	private static Set getExitPropertyRules() {
		if ( needRecalculateForNewAnno(ExitPropertyRules) ) {
			ExitPropertyRules = new TreeSet();
			ExitPropertyRules.addAll(createRulesForElementKinds(ElementKind.EXITSTATEMENT));
		}
		return ExitPropertyRules;

	}

	
	private static Set TransferPropertyRules;
	private static Set getTransferPropertyRules() {
		if ( needRecalculateForNewAnno(TransferPropertyRules) ) {
			TransferPropertyRules = new TreeSet();
			TransferPropertyRules.addAll(createRulesForElementKinds(ElementKind.TRANSFERSTATEMENT));
		}
		return TransferPropertyRules;

	}
	
	private static Set ShowPropertyRules;
	private static Set getShowPropertyRules() {
		if ( needRecalculateForNewAnno(ShowPropertyRules) ) {
			ShowPropertyRules = new TreeSet();
			ShowPropertyRules.addAll(createRulesForElementKinds(ElementKind.SHOWSTATEMENT));
		}
		return ShowPropertyRules;

	}

	private static Set PromptPropertyRules;
	private static Set getPromptPropertyRules() {
		if ( needRecalculateForNewAnno(PromptPropertyRules) ) {
			PromptPropertyRules = new TreeSet();
			PromptPropertyRules.addAll(REPLACEME);
		}
		return PromptPropertyRules;

	}

	private static Set MenuItemPropertyRules;
	private static Set getMenuItemPropertyRules() {
		if ( needRecalculateForNewAnno(MenuItemPropertyRules) ) {
			MenuItemPropertyRules = new TreeSet();
			MenuItemPropertyRules.addAll(REPLACEME);
		}
		return MenuItemPropertyRules;

	}

	private static Set MenuPropertyRules;
	private static Set getMenuPropertyRules() {
		if ( needRecalculateForNewAnno(MenuPropertyRules) ) {
			MenuPropertyRules = new TreeSet();
			MenuPropertyRules.addAll(REPLACEME);
		}
		return MenuPropertyRules;

	}

	private static Set PresentationAttributesPropertyRules;
	private static Set getPresentationAttributesPropertyRules() {
		if ( needRecalculateForNewAnno(PresentationAttributesPropertyRules) ) {
			PresentationAttributesPropertyRules = new TreeSet();
			PresentationAttributesPropertyRules.addAll(REPLACEME);
		}
		return PresentationAttributesPropertyRules;

	}

	private static Set WindowPropertyRules;
	private static Set getWindowPropertyRules() {
		if ( needRecalculateForNewAnno(WindowPropertyRules) ) {
			WindowPropertyRules = new TreeSet();
			WindowPropertyRules.addAll(REPLACEME);
		}
		return WindowPropertyRules;

	}

	private static Set ConsoleArrayFieldPropertyRules;
	private static Set getConsoleArrayFieldPropertyRules() {
		if ( needRecalculateForNewAnno(ConsoleArrayFieldPropertyRules) ) {
			ConsoleArrayFieldPropertyRules = new TreeSet();
			ConsoleArrayFieldPropertyRules.addAll(createRulesForMemberAnnotations(IEGLConstants.RECORD_SUBTYPE_CONSOLE_FORM));
			ConsoleArrayFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_COLUMNS));
			ConsoleArrayFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_LINESBETWEENROWS));
			ConsoleArrayFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_SPACESBETWEENCOLUMNS));
			ConsoleArrayFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_ORIENTINDEXACROSS));
		}
		return ConsoleArrayFieldPropertyRules;

	}

	private static Set ConsoleFieldPropertyRules;
	private static Set getConsoleFieldPropertyRules() {
		if ( needRecalculateForNewAnno(ConsoleFieldPropertyRules) ) {
			ConsoleFieldPropertyRules = new TreeSet();
			ConsoleFieldPropertyRules.addAll(createRulesForMemberAnnotations(IEGLConstants.RECORD_SUBTYPE_CONSOLE_FORM));
		}
		return ConsoleFieldPropertyRules;

	}

	private static Set ConsoleFormPropertyRules;
	private static Set getConsoleFormPropertyRules() {
		if ( needRecalculateForNewAnno(ConsoleFormPropertyRules) ) {
			ConsoleFormPropertyRules = new TreeSet();
			ConsoleFormPropertyRules.addAll(createRulesForElementKinds(ElementKind.RECORDPART));
			ConsoleFormPropertyRules.addAll(createRulesForFields(IEGLConstants.RECORD_SUBTYPE_CONSOLE_FORM));
			ConsoleFormPropertyRules.add(new EGLPropertyRule(IEGLConstants.PROPERTY_NAME, new int[] {quotedValue}));
		}
		return ConsoleFormPropertyRules;

	}
	
	private static Set DynamicConsoleFormPropertyRules;
	private static Set getDynamicConsoleFormPropertyRules() {
		if ( needRecalculateForNewAnno(DynamicConsoleFormPropertyRules) ) {
			DynamicConsoleFormPropertyRules = new TreeSet();
			DynamicConsoleFormPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_MAXSIZE));
			DynamicConsoleFormPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
			DynamicConsoleFormPropertyRules.addAll(createRulesForElementKinds(ElementKind.RECORDPART));
			DynamicConsoleFormPropertyRules.addAll(createRulesForFields(IEGLConstants.RECORD_SUBTYPE_CONSOLE_FORM));
			DynamicConsoleFormPropertyRules.add(new EGLPropertyRule(IEGLConstants.PROPERTY_NAME, new int[] {quotedValue}));
		}
		return ConsoleFormPropertyRules;

	}

	private static Set DictionaryPropertyRules;
	private static Set getDictionaryPropertyRules() {
		if ( needRecalculateForNewAnno(DictionaryPropertyRules) ) {
			DictionaryPropertyRules = new TreeSet();
			DictionaryPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_CASESENSITIVE));
			DictionaryPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_ORDERING));
		}
		return DictionaryPropertyRules;

	}

	private static Set DataItemItemFormFieldPropertyRules;
	private static Set getDataItemItemFormFieldPropertyRules() {
		if ( needRecalculateForNewAnno(DataItemItemFormFieldPropertyRules) ) {
			DataItemItemFormFieldPropertyRules = new TreeSet();
			DataItemItemFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_FIELDLEN));
		}
		return DataItemItemFormFieldPropertyRules;

	}

	private static Set DataItemVariableFieldPropertyRules;
	private static Set getDataItemVariableFieldPropertyRules() {
		if ( needRecalculateForNewAnno(DataItemVariableFieldPropertyRules) ) {
			DataItemVariableFieldPropertyRules = new TreeSet();
			DataItemVariableFieldPropertyRules.addAll(createRulesForGroup("variableField"));
		}
		return DataItemVariableFieldPropertyRules;

	}

	private static Set DataItemDoubleByteDevicePresentationPropertyRules;
	private static Set getDataItemDoubleByteDevicePresentationPropertyRules() {
		if ( needRecalculateForNewAnno(DataItemDoubleByteDevicePresentationPropertyRules) ) {
			DataItemDoubleByteDevicePresentationPropertyRules = new TreeSet();
			DataItemDoubleByteDevicePresentationPropertyRules.addAll(createRulesForGroup("doubleByteDevicePresentation"));
		}
		return DataItemDoubleByteDevicePresentationPropertyRules;

	}

	private static Set TuiFieldPresentationPropertyRules;
	private static Set getTuiFieldPresentationPropertyRules() {
		if ( needRecalculateForNewAnno(TuiFieldPresentationPropertyRules) ) {
			TuiFieldPresentationPropertyRules = new TreeSet();
			TuiFieldPresentationPropertyRules.addAll(createRulesForGroup("fieldPresentation"));
			TuiFieldPresentationPropertyRules.addAll(createRulesForGroup("doubleByteDevicePresentation"));
		}
		return TuiFieldPresentationPropertyRules;

	}

	private static Set DataItemFieldPresentationPropertyRules;
	private static Set getDataItemFieldPresentationPropertyRules() {
		if ( needRecalculateForNewAnno(DataItemFieldPresentationPropertyRules) ) {
			DataItemFieldPresentationPropertyRules = new TreeSet();
			DataItemFieldPresentationPropertyRules.addAll(createRulesForGroup("fieldPresentation"));
			DataItemFieldPresentationPropertyRules.addAll(createRulesForGroup("doubleByteDevicePresentation"));
			DataItemFieldPresentationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_FIELDLEN));
		}
		return DataItemFieldPresentationPropertyRules;

	}

	private static Set DataItemValidationPropertyRules;
	private static Set getDataItemValidationPropertyRules() {
		if ( needRecalculateForNewAnno(DataItemValidationPropertyRules) ) {
			DataItemValidationPropertyRules = new TreeSet();
			DataItemValidationPropertyRules.addAll(createRulesForGroup("validation"));
		}
		return DataItemValidationPropertyRules;

	}

	private static Set PsbRecordItemPropertyRules;
	private static Set getPsbRecordItemPropertyRules() {
		if ( needRecalculateForNewAnno(PsbRecordItemPropertyRules)) {
			PsbRecordItemPropertyRules = new TreeSet();
			PsbRecordItemPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_INITIALIZED));
			PsbRecordItemPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
			PsbRecordItemPropertyRules.addAll(createRulesForMemberAnnotations(IEGLConstants.RECORD_SUBTYPE_PSB_RECORD));
		}
		return PsbRecordItemPropertyRules;

	}

	private static Set DataItemDL1ItemPropertyRules;
	private static Set getDataItemDL1ItemPropertyRules() {
		if ( needRecalculateForNewAnno(DataItemDL1ItemPropertyRules) ) {
			DataItemDL1ItemPropertyRules = new TreeSet();
			DataItemDL1ItemPropertyRules.addAll(createRulesForMemberAnnotations(IEGLConstants.RECORD_SUBTYPE_DLI_SEGMENT));
		}
		return DataItemDL1ItemPropertyRules;

	}

	private static Set DataItemTUISAPropertyRules;
	private static Set getDataItemTUISAPropertyRules() {
		if ( needRecalculateForNewAnno(DataItemTUISAPropertyRules) ) {
			DataItemTUISAPropertyRules = new TreeSet();
			DataItemTUISAPropertyRules.addAll(createRulesForGroup("variableField"));
			DataItemTUISAPropertyRules.addAll(createRulesForGroup("fieldPresentation"));
			DataItemTUISAPropertyRules.addAll(createRulesForGroup("doubleByteDevicePresentation"));
			DataItemTUISAPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_FIELDLEN));
		}
		return DataItemTUISAPropertyRules;

	}

	private static Set DataItemUIItemSAPropertyRules;
	private static Set getDataItemUIItemSAPropertyRules() {
		if ( needRecalculateForNewAnno(DataItemUIItemSAPropertyRules) ) {
			DataItemUIItemSAPropertyRules = new TreeSet();
			DataItemUIItemSAPropertyRules.addAll(createRulesForGroup("ui"));
		}
		return DataItemUIItemSAPropertyRules;

	}

	private static Set DataItemUIItemPropertyRules;
	private static Set getDataItemUIItemPropertyRules() {
		if ( needRecalculateForNewAnno(DataItemUIItemPropertyRules) ) {
			DataItemUIItemPropertyRules = new TreeSet();
			DataItemUIItemPropertyRules.addAll(createRulesForGroup("formatting"));
			DataItemUIItemPropertyRules.addAll(createRulesForGroup("validation"));
			DataItemUIItemPropertyRules.addAll(createRulesForGroup("ui"));
		}
		return DataItemUIItemPropertyRules;

	}

	private static Set DataItemPageItemPropertyRules;
	private static Set getDataItemPageItemPropertyRules() {
		if ( needRecalculateForNewAnno(DataItemPageItemPropertyRules) ) {
			DataItemPageItemPropertyRules = new TreeSet();
			DataItemPageItemPropertyRules.addAll(createRulesForMemberAnnotations(IEGLConstants.HANDLER_SUBTYPE_JSF));
		}
		return DataItemPageItemPropertyRules;

	}

	private static Set DataItemSQLItemPropertyRules;
	private static Set getDataItemSQLItemPropertyRules() {
		if ( needRecalculateForNewAnno(DataItemSQLItemPropertyRules) ) {
			DataItemSQLItemPropertyRules = new TreeSet();
			DataItemSQLItemPropertyRules.addAll(createRulesForMemberAnnotations(IEGLConstants.RECORD_SUBTYPE_SQl));
		}
		return DataItemSQLItemPropertyRules;

	}

	private static Set DataItemFormattingPropertyRules;
	private static Set getDataItemFormattingPropertyRules() {
		if ( needRecalculateForNewAnno(DataItemFormattingPropertyRules) ) {
			DataItemFormattingPropertyRules = new TreeSet();
			DataItemFormattingPropertyRules.addAll(createRulesForGroup("formatting"));
		}
		return DataItemFormattingPropertyRules;

	}

	private static Set DynamicPageItemDataDeclarationPropertyRules;
	private static Set getDynamicPageItemDataDeclarationPropertyRules() {
		if ( needRecalculateForNewAnno(DynamicPageItemDataDeclarationPropertyRules) ) {
			DynamicPageItemDataDeclarationPropertyRules = new TreeSet();
			DynamicPageItemDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_MAXSIZE));
			DynamicPageItemDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_SELECTEDROWITEM));
			DynamicPageItemDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_SELECTEDVALUEITEM));
			DynamicPageItemDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_SELECTIONLIST));
			DynamicPageItemDataDeclarationPropertyRules.addAll(createRulesForMemberAnnotations(IEGLConstants.HANDLER_SUBTYPE_JSF));
			DynamicPageItemDataDeclarationPropertyRules.addAll(createRulesForMemberAnnotations(IEGLConstants.RECORD_SUBTYPE_SQl));
			DynamicPageItemDataDeclarationPropertyRules.addAll(createRulesForGroup("validation")); //$NON-NLS-1$
			DynamicPageItemDataDeclarationPropertyRules.addAll(createRulesForGroup("formatting")); //$NON-NLS-1$
		}
		return DynamicPageItemDataDeclarationPropertyRules;

	}

	private static Set StaticPageItemDataDeclarationPropertyRules;
	private static Set getStaticPageItemDataDeclarationPropertyRules() {
		if ( needRecalculateForNewAnno(StaticPageItemDataDeclarationPropertyRules) ) {
			StaticPageItemDataDeclarationPropertyRules = new TreeSet();
			StaticPageItemDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_INITIALIZED));
			StaticPageItemDataDeclarationPropertyRules.addAll(createRulesForMemberAnnotations(IEGLConstants.HANDLER_SUBTYPE_JSF));
			StaticPageItemDataDeclarationPropertyRules.addAll(createRulesForMemberAnnotations(IEGLConstants.RECORD_SUBTYPE_SQl));
			StaticPageItemDataDeclarationPropertyRules.addAll(createRulesForGroup("validation"));
			StaticPageItemDataDeclarationPropertyRules.addAll(createRulesForGroup("formatting"));
		}
		return StaticPageItemDataDeclarationPropertyRules;

	}

	private static Set FormUseDeclarationPropertyRules;
	private static Set getFormUseDeclarationPropertyRules() {
		if ( needRecalculateForNewAnno(FormUseDeclarationPropertyRules) ) {
			FormUseDeclarationPropertyRules = new TreeSet();
			FormUseDeclarationPropertyRules.addAll(createRulesFor(new TargetTypesHasAnnotationRecordFilter(ElementKind.FORMUSE)));
		}
		return FormUseDeclarationPropertyRules;
	}
	
	private static Set LibraryUseDeclarationPropertyRules;
	private static Set getLibraryUseDeclarationPropertyRules() {
		if ( needRecalculateForNewAnno(LibraryUseDeclarationPropertyRules) ) {
			LibraryUseDeclarationPropertyRules = new TreeSet();
			LibraryUseDeclarationPropertyRules.addAll(REPLACEME);
		}
		return LibraryUseDeclarationPropertyRules;

	}

	private static Set FormGroupUseDeclarationPropertyRules;
	private static Set getFormGroupUseDeclarationPropertyRules() {
		if ( needRecalculateForNewAnno(FormGroupUseDeclarationPropertyRules)  ) {
			FormGroupUseDeclarationPropertyRules = new TreeSet();
			FormGroupUseDeclarationPropertyRules.addAll(createRulesFor(new TargetTypesHasAnnotationRecordFilter(ElementKind.FORMGROUPUSE)));
		}
		return FormGroupUseDeclarationPropertyRules;

	}

	private static Set DataTableUseDeclarationPropertyRules;
	private static Set getDataTableUseDeclarationPropertyRules() {
		if ( needRecalculateForNewAnno(DataTableUseDeclarationPropertyRules) ) {
			DataTableUseDeclarationPropertyRules = new TreeSet();
			DataTableUseDeclarationPropertyRules.addAll(createRulesFor(new TargetTypesHasAnnotationRecordFilter(ElementKind.DATATABLEUSE)));
		}
		return DataTableUseDeclarationPropertyRules;

	}

	private static Set UseDeclarationPropertyRules;
	private static Set getUseDeclarationPropertyRules() {
		if ( needRecalculateForNewAnno(UseDeclarationPropertyRules) ) {
			UseDeclarationPropertyRules = new TreeSet();
			UseDeclarationPropertyRules.addAll(getDataTableUseDeclarationPropertyRules());
			UseDeclarationPropertyRules.addAll(getFormGroupUseDeclarationPropertyRules());
		}
		return UseDeclarationPropertyRules;

	}

	private static Set DynamicItemDataDeclarationPropertyRules;
	private static Set getDynamicItemDataDeclarationPropertyRules() {
		if ( needRecalculateForNewAnno(DynamicItemDataDeclarationPropertyRules) ) {
			DynamicItemDataDeclarationPropertyRules = new TreeSet();
			DynamicItemDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_MAXSIZE));
			DynamicItemDataDeclarationPropertyRules.addAll(createRulesForMemberAnnotations(IEGLConstants.RECORD_SUBTYPE_SQl));
			DynamicItemDataDeclarationPropertyRules.addAll(createRulesForElementKinds(new EnumerationDataBinding[]{ElementKind.FIELDMBR}));
		}
		return DynamicItemDataDeclarationPropertyRules;

	}

	private static Set StaticItemDataDeclarationPropertyRules;
	private static Set getStaticItemDataDeclarationPropertyRules() {
		if ( needRecalculateForNewAnno(StaticItemDataDeclarationPropertyRules )) {
			StaticItemDataDeclarationPropertyRules = new TreeSet();
			StaticItemDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_INITIALIZED));
			StaticItemDataDeclarationPropertyRules.addAll(createRulesForMemberAnnotations(IEGLConstants.RECORD_SUBTYPE_SQl));
			StaticItemDataDeclarationPropertyRules.addAll(createRulesForElementKinds(new EnumerationDataBinding[]{ElementKind.FIELDMBR}));
		}
		return StaticItemDataDeclarationPropertyRules;
	}

	private static Set StaticServiceItemDataDeclarationPropertyRules;
	private static Set getServiceStaticItemDataDeclarationPropertyRules() {
		if ( needRecalculateForNewAnno(StaticServiceItemDataDeclarationPropertyRules) ) {
			StaticServiceItemDataDeclarationPropertyRules = new TreeSet();
			StaticServiceItemDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_BINDSERVICE));
			StaticServiceItemDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_INITIALIZED));
			StaticServiceItemDataDeclarationPropertyRules.addAll(createRulesForMemberAnnotations(IEGLConstants.HANDLER_SUBTYPE_JSF));
			StaticServiceItemDataDeclarationPropertyRules.addAll(createRulesForMemberAnnotations(IEGLConstants.RECORD_SUBTYPE_SQl));	    	
		}
		return StaticServiceItemDataDeclarationPropertyRules;
	}

	private static Set ExternalTypeClassDeclarationPropertyRules;
	private static Set getExternalTypeClassDeclartionPropertyRules() {
		if ( needRecalculateForNewAnno(ExternalTypeClassDeclarationPropertyRules) ) {
			ExternalTypeClassDeclarationPropertyRules = new TreeSet();
			ExternalTypeClassDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_JAVAPROPERTY));
			ExternalTypeClassDeclarationPropertyRules.addAll(createRulesForElementKinds(new EnumerationDataBinding[] {ElementKind.FIELDMBR}));
		}
		return ExternalTypeClassDeclarationPropertyRules;
	}

	private static Set ExternalTypeArrayHandlerClassDeclarationPropertyRules;
	private static Set getExternalTypeArrayHandlerClassDeclartionPropertyRules() {
		if ( needRecalculateForNewAnno(ExternalTypeArrayHandlerClassDeclarationPropertyRules )) {
			ExternalTypeArrayHandlerClassDeclarationPropertyRules = new TreeSet();
			ExternalTypeArrayHandlerClassDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_JAVAPROPERTY));
			ExternalTypeArrayHandlerClassDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_EVENTLISTENER));
			ExternalTypeArrayHandlerClassDeclarationPropertyRules.addAll(createRulesForElementKinds(new EnumerationDataBinding[] {ElementKind.FIELDMBR}));
		}
		return ExternalTypeArrayHandlerClassDeclarationPropertyRules;
	}

	private static Set NewExpressionPropertyRules;
	private static Set getNewExpressionPropertyRules() {
		if (NewExpressionPropertyRules == null) {
			NewExpressionPropertyRules = new TreeSet();
		}
		return NewExpressionPropertyRules;
	}

	private static Set DynamicAnyRecordDataDeclarationPropertyRules;
	private static Set getDynamicAnyRecordDataDeclarationPropertyRules() {
		if ( needRecalculateForNewAnno(DynamicAnyRecordDataDeclarationPropertyRules) ) {
			DynamicAnyRecordDataDeclarationPropertyRules = new TreeSet();
			DynamicAnyRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_MAXSIZE));
			DynamicAnyRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
			DynamicAnyRecordDataDeclarationPropertyRules.addAll(getVGUIRecordPropertyRules());
			DynamicAnyRecordDataDeclarationPropertyRules.addAll(getSQLRecordPropertyRules());
			DynamicAnyRecordDataDeclarationPropertyRules.addAll(getMQRecordPropertyRules());
			DynamicAnyRecordDataDeclarationPropertyRules.addAll(getSerialRecordPropertyRules());
			DynamicAnyRecordDataDeclarationPropertyRules.addAll(getRelativeRecordPropertyRules());
			DynamicAnyRecordDataDeclarationPropertyRules.addAll(getIndexedRecordPropertyRules());
			DynamicAnyRecordDataDeclarationPropertyRules.addAll(getBasicRecordPropertyRules());
		}
		return DynamicAnyRecordDataDeclarationPropertyRules;

	}

	private static Set DynamicVGUIRecordDataDeclarationPropertyRules;
	private static Set getDynamicVGUIRecordDataDeclarationPropertyRules() {
		if ( needRecalculateForNewAnno(DynamicVGUIRecordDataDeclarationPropertyRules) ) {
			DynamicVGUIRecordDataDeclarationPropertyRules = new TreeSet();
			DynamicVGUIRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_MAXSIZE));
			DynamicVGUIRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
			DynamicVGUIRecordDataDeclarationPropertyRules.addAll(getVGUIRecordPropertyRules());
		}
		return DynamicVGUIRecordDataDeclarationPropertyRules;

	}

	private static Set DynamicSQLRecordDataDeclarationPropertyRules;
	private static Set getDynamicSQLRecordDataDeclarationPropertyRules() {
		if ( needRecalculateForNewAnno(DynamicSQLRecordDataDeclarationPropertyRules) ) {
			DynamicSQLRecordDataDeclarationPropertyRules = new TreeSet();
			DynamicSQLRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_MAXSIZE));
			DynamicSQLRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
			DynamicSQLRecordDataDeclarationPropertyRules.addAll(getSQLRecordPropertyRules());
		}
		return DynamicSQLRecordDataDeclarationPropertyRules;

	}

	private static Set DynamicMQRecordDataDeclarationPropertyRules;
	private static Set getDynamicMQRecordDataDeclarationPropertyRules() {
		if ( needRecalculateForNewAnno(DynamicMQRecordDataDeclarationPropertyRules) ) {
			DynamicMQRecordDataDeclarationPropertyRules = new TreeSet();
			DynamicMQRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_MAXSIZE));
			DynamicMQRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
			DynamicMQRecordDataDeclarationPropertyRules.addAll(getMQRecordPropertyRules());
		}
		return DynamicMQRecordDataDeclarationPropertyRules;

	}

	private static Set DynamicSerialRecordDataDeclarationPropertyRules;
	private static Set getDynamicSerialRecordDataDeclarationPropertyRules() {
		if ( needRecalculateForNewAnno(DynamicSerialRecordDataDeclarationPropertyRules) ) {
			DynamicSerialRecordDataDeclarationPropertyRules = new TreeSet();
			DynamicSerialRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_MAXSIZE));
			DynamicSerialRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
			DynamicSerialRecordDataDeclarationPropertyRules.addAll(getSerialRecordPropertyRules());
		}
		return DynamicSerialRecordDataDeclarationPropertyRules;

	}

	private static Set DynamicRelativeRecordDataDeclarationPropertyRules;
	private static Set getDynamicRelativeRecordDataDeclarationPropertyRules() {
		if ( needRecalculateForNewAnno(DynamicRelativeRecordDataDeclarationPropertyRules) ) {
			DynamicRelativeRecordDataDeclarationPropertyRules = new TreeSet();
			DynamicRelativeRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_MAXSIZE));
			DynamicRelativeRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
			DynamicRelativeRecordDataDeclarationPropertyRules.addAll(getRelativeRecordPropertyRules());
		}
		return DynamicRelativeRecordDataDeclarationPropertyRules;

	}

	private static Set DynamicIndexedRecordDataDeclarationPropertyRules;
	private static Set getDynamicIndexedRecordDataDeclarationPropertyRules() {
		if ( needRecalculateForNewAnno(DynamicIndexedRecordDataDeclarationPropertyRules) ) {
			DynamicIndexedRecordDataDeclarationPropertyRules = new TreeSet();
			DynamicIndexedRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_MAXSIZE));
			DynamicIndexedRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
			DynamicIndexedRecordDataDeclarationPropertyRules.addAll(getIndexedRecordPropertyRules());
		}
		return DynamicIndexedRecordDataDeclarationPropertyRules;

	}

	private static Set DynamicBasicRecordDataDeclarationPropertyRules;
	private static Set getDynamicBasicRecordDataDeclarationPropertyRules() {
		if ( needRecalculateForNewAnno(DynamicBasicRecordDataDeclarationPropertyRules) ) {
			DynamicBasicRecordDataDeclarationPropertyRules = new TreeSet();
			DynamicBasicRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_MAXSIZE));
			DynamicBasicRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
			DynamicBasicRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_SELECTEDROWITEM));
			DynamicBasicRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_SELECTEDVALUEITEM));
			DynamicBasicRecordDataDeclarationPropertyRules.addAll(getBasicRecordPropertyRules());
		}
		return DynamicBasicRecordDataDeclarationPropertyRules;

	}

	private static Set StaticAnyRecordDataDeclarationPropertyRules;
	private static Set getStaticAnyRecordDataDeclarationPropertyRules() {
		if ( needRecalculateForNewAnno(StaticAnyRecordDataDeclarationPropertyRules) ) {
			StaticAnyRecordDataDeclarationPropertyRules = new TreeSet();
			StaticAnyRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_INITIALIZED));
			StaticAnyRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
			StaticAnyRecordDataDeclarationPropertyRules.addAll(getVGUIRecordPropertyRules());
			StaticAnyRecordDataDeclarationPropertyRules.addAll(getSQLRecordPropertyRules());
			StaticAnyRecordDataDeclarationPropertyRules.addAll(getMQRecordPropertyRules());
			StaticAnyRecordDataDeclarationPropertyRules.addAll(getSerialRecordPropertyRules());
			StaticAnyRecordDataDeclarationPropertyRules.addAll(getRelativeRecordPropertyRules());
			StaticAnyRecordDataDeclarationPropertyRules.addAll(getIndexedRecordPropertyRules());
			StaticAnyRecordDataDeclarationPropertyRules.addAll(getBasicRecordPropertyRules());
		}
		return StaticAnyRecordDataDeclarationPropertyRules;

	}

	private static Set StaticVGUIRecordDataDeclarationPropertyRules;
	private static Set getStaticVGUIRecordDataDeclarationPropertyRules() {
		if ( needRecalculateForNewAnno(StaticVGUIRecordDataDeclarationPropertyRules) ) {
			StaticVGUIRecordDataDeclarationPropertyRules = new TreeSet();
			StaticVGUIRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_INITIALIZED));
			StaticVGUIRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
			StaticVGUIRecordDataDeclarationPropertyRules.addAll(getVGUIRecordPropertyRules());
		}
		return StaticVGUIRecordDataDeclarationPropertyRules;

	}

	private static Set StaticSQLRecordDataDeclarationPropertyRules;
	private static Set getStaticSQLRecordDataDeclarationPropertyRules() {
		if ( needRecalculateForNewAnno(StaticSQLRecordDataDeclarationPropertyRules) ) {
			StaticSQLRecordDataDeclarationPropertyRules = new TreeSet();
			StaticSQLRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_INITIALIZED));
			StaticSQLRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
			StaticSQLRecordDataDeclarationPropertyRules.addAll(getSQLRecordPropertyRules());
		}
		return StaticSQLRecordDataDeclarationPropertyRules;

	}

	private static Set StaticMQRecordDataDeclarationPropertyRules;
	private static Set getStaticMQRecordDataDeclarationPropertyRules() {
		if ( needRecalculateForNewAnno(StaticMQRecordDataDeclarationPropertyRules) ) {
			StaticMQRecordDataDeclarationPropertyRules = new TreeSet();
			StaticMQRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_INITIALIZED));
			StaticMQRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
			StaticMQRecordDataDeclarationPropertyRules.addAll(getMQRecordPropertyRules());
		}
		return StaticMQRecordDataDeclarationPropertyRules;

	}

	private static Set StaticSerialRecordDataDeclarationPropertyRules;
	private static Set getStaticSerialRecordDataDeclarationPropertyRules() {
		if ( needRecalculateForNewAnno(StaticSerialRecordDataDeclarationPropertyRules) ) {
			StaticSerialRecordDataDeclarationPropertyRules = new TreeSet();
			StaticSerialRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_INITIALIZED));
			StaticSerialRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
			StaticSerialRecordDataDeclarationPropertyRules.addAll(getSerialRecordPropertyRules());
		}
		return StaticSerialRecordDataDeclarationPropertyRules;

	}

	private static Set StaticRelativeRecordDataDeclarationPropertyRules;
	private static Set getStaticRelativeRecordDataDeclarationPropertyRules() {
		if ( needRecalculateForNewAnno(StaticRelativeRecordDataDeclarationPropertyRules) ) {
			StaticRelativeRecordDataDeclarationPropertyRules = new TreeSet();
			StaticRelativeRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_INITIALIZED));
			StaticRelativeRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
			StaticRelativeRecordDataDeclarationPropertyRules.addAll(getRelativeRecordPropertyRules());
		}
		return StaticRelativeRecordDataDeclarationPropertyRules;

	}

	private static Set StaticIndexedRecordDataDeclarationPropertyRules;
	private static Set getStaticIndexedRecordDataDeclarationPropertyRules() {
		if ( needRecalculateForNewAnno(StaticIndexedRecordDataDeclarationPropertyRules) ) {
			StaticIndexedRecordDataDeclarationPropertyRules = new TreeSet();
			StaticIndexedRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_INITIALIZED));
			StaticIndexedRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
			StaticIndexedRecordDataDeclarationPropertyRules.addAll(getIndexedRecordPropertyRules());
		}
		return StaticIndexedRecordDataDeclarationPropertyRules;

	}

	private static Set StaticBasicRecordDataDeclarationPropertyRules;
	private static Set getStaticBasicRecordDataDeclarationPropertyRules() {
		if ( needRecalculateForNewAnno(StaticBasicRecordDataDeclarationPropertyRules) ) {
			StaticBasicRecordDataDeclarationPropertyRules = new TreeSet();
			StaticBasicRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_INITIALIZED));
			StaticBasicRecordDataDeclarationPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_REDEFINES));
			StaticBasicRecordDataDeclarationPropertyRules.addAll(getBasicRecordPropertyRules());
		}
		return StaticBasicRecordDataDeclarationPropertyRules;

	}

	private static Set NativeLibraryPropertyRules;
	private static Set getNativeLibraryPropertyRules() {
		if ( needRecalculateForNewAnno(NativeLibraryPropertyRules) ) {
			NativeLibraryPropertyRules = new TreeSet();
			NativeLibraryPropertyRules.addAll(createRulesFor(new TargetTypesHasAnnotationRecordFilter(ElementKind.LIBRARYPART)));
			NativeLibraryPropertyRules.addAll(createRulesForFields(IEGLConstants.LIBRARY_SUBTYPE_NATIVE));
		}
		return NativeLibraryPropertyRules;

	}

	private static Set libraryPropertyRules;
	private static Set getlibraryPropertyRules() {
		if ( needRecalculateForNewAnno(libraryPropertyRules) ) {
			libraryPropertyRules = new TreeSet();
			libraryPropertyRules.addAll(createRulesFor(new TargetTypesHasAnnotationRecordFilter(ElementKind.LIBRARYPART)));
			libraryPropertyRules.addAll(createRulesForFields(IEGLConstants.LIBRARY_SUBTYPE_BASIC));
		}
		return libraryPropertyRules;

	}

	private static Set CalledTextUIProgramPropertyRules;
	private static Set getCalledTextUIProgramPropertyRules() {
		if ( needRecalculateForNewAnno(CalledTextUIProgramPropertyRules) ) {
			CalledTextUIProgramPropertyRules = new TreeSet();
			CalledTextUIProgramPropertyRules.addAll(createRulesForElementKinds(ElementKind.PROGRAMPART));
			CalledTextUIProgramPropertyRules.addAll(createRulesForFields(IEGLConstants.PROGRAM_SUBTYPE_TEXT_UI));
		}
		return CalledTextUIProgramPropertyRules;

	}

	private static Set TextUIProgramPropertyRules;
	private static Set getTextUIProgramPropertyRules() {
		if ( needRecalculateForNewAnno(TextUIProgramPropertyRules) ) {
			TextUIProgramPropertyRules = new TreeSet();
			TextUIProgramPropertyRules.addAll(createRulesForElementKinds(ElementKind.PROGRAMPART));
			TextUIProgramPropertyRules.addAll(createRulesForFields(IEGLConstants.PROGRAM_SUBTYPE_TEXT_UI));
		}
		return TextUIProgramPropertyRules;

	}

	private static Set CalledBasicProgramPropertyRules;
	private static Set getCalledBasicProgramPropertyRules() {
		if ( needRecalculateForNewAnno(CalledBasicProgramPropertyRules) ) {
			CalledBasicProgramPropertyRules = new TreeSet();
			CalledBasicProgramPropertyRules.addAll(createRulesForElementKinds(ElementKind.PROGRAMPART));
			CalledBasicProgramPropertyRules.addAll(createRulesForFields(IEGLConstants.PROGRAM_SUBTYPE_BASIC));
		}
		return CalledBasicProgramPropertyRules;

	}

	private static Set BasicProgramPropertyRules;
	private static Set getBasicProgramPropertyRules() {
		if ( needRecalculateForNewAnno(BasicProgramPropertyRules) ) {
			BasicProgramPropertyRules = new TreeSet();
			BasicProgramPropertyRules.addAll(createRulesForElementKinds(ElementKind.PROGRAMPART));
			BasicProgramPropertyRules.addAll(createRulesForFields(IEGLConstants.PROGRAM_SUBTYPE_BASIC));
		}
		return BasicProgramPropertyRules;

	}

	private static Set VGWebTransactionPropertyRules;
	private static Set getVGWebTransactionPropertyRules() {
		if ( needRecalculateForNewAnno(VGWebTransactionPropertyRules) ) {
			VGWebTransactionPropertyRules = new TreeSet();
			VGWebTransactionPropertyRules.addAll(createRulesForElementKinds(ElementKind.PROGRAMPART));
			VGWebTransactionPropertyRules.addAll(createRulesForFields(IEGLConstants.PROGRAM_SUBTYPE_VG_WEB_TRANSACTION));
		}
		return VGWebTransactionPropertyRules;

	}

	private static Set AllProgramPropertyRules;
	private static Set getAllProgramPropertyRules() {
		if ( needRecalculateForNewAnno(AllProgramPropertyRules) ) {
			AllProgramPropertyRules = new TreeSet();
			AllProgramPropertyRules.addAll(createRulesFor(new TargetTypesHasAnnotationRecordFilter(ElementKind.PROGRAMPART)));
			AllProgramPropertyRules.addAll(createRulesForFields(IEGLConstants.PROGRAM_SUBTYPE_BASIC));
		}
		return AllProgramPropertyRules;

	}

	private static Set NativeLibraryFunctionPropertyRules;
	private static Set getNativeLibraryFunctionPropertyRules() {
		if ( needRecalculateForNewAnno(NativeLibraryFunctionPropertyRules) ) {
			NativeLibraryFunctionPropertyRules = new TreeSet();
			NativeLibraryFunctionPropertyRules.addAll(createRulesFor(new TargetTypesHasAnnotationRecordFilter(ElementKind.FUNCTIONMBR)));
		}
		return NativeLibraryFunctionPropertyRules;
	}

	private static Set FunctionPropertyRules;
	private static Set getFunctionPropertyRules() {
		if ( needRecalculateForNewAnno(FunctionPropertyRules) ) {
			FunctionPropertyRules = new TreeSet();
			FunctionPropertyRules.addAll(createRulesFor(new TargetTypesHasAnnotationRecordFilter(ElementKind.FUNCTIONPART)));
		}
		return FunctionPropertyRules;

	}

	private static Set DataTablePropertyRules;
	private static Set getDataTablePropertyRules() {
		if ( needRecalculateForNewAnno(DataTablePropertyRules) ) {
			DataTablePropertyRules = new TreeSet();
			DataTablePropertyRules.addAll(createRulesFor(new TargetTypesHasAnnotationRecordFilter(ElementKind.DATATABLEPART)));
			DataTablePropertyRules.addAll(createRulesForFields(IEGLConstants.DATATABLE_SUBTYPE_BASIC));
		}
		return DataTablePropertyRules;

	}

	private static Set PageHandlerPropertyRules;
	private static Set getPageHandlerPropertyRules() {
		if ( needRecalculateForNewAnno(PageHandlerPropertyRules) ) {
			PageHandlerPropertyRules = new TreeSet();
			PageHandlerPropertyRules.addAll(createRulesFor(new TargetTypesHasAnnotationRecordFilter(ElementKind.HANDLERPART)));
			PageHandlerPropertyRules.addAll(createRulesForFields(IEGLConstants.HANDLER_SUBTYPE_JSF));
		}
		return PageHandlerPropertyRules;

	}

	private static Set PrintFormPropertyRules;
	private static Set getPrintFormPropertyRules() {
		if ( needRecalculateForNewAnno(PrintFormPropertyRules) ) {
			PrintFormPropertyRules = new TreeSet();
			PrintFormPropertyRules.addAll(createRulesForElementKinds(ElementKind.FORMPART));
			PrintFormPropertyRules.addAll(createRulesForFields(IEGLConstants.FORM_SUBTYPE_PRINT));
		}
		return PrintFormPropertyRules;

	}

	private static Set TextFormPropertyRules;
	private static Set getTextFormPropertyRules() {
		if ( needRecalculateForNewAnno(TextFormPropertyRules) ) {
			TextFormPropertyRules = new TreeSet();
			TextFormPropertyRules.addAll(createRulesForElementKinds(ElementKind.FORMPART));
			TextFormPropertyRules.addAll(createRulesForFields(IEGLConstants.FORM_SUBTYPE_TEXT));
		}
		return TextFormPropertyRules;

	}

	private static Set PrintVariableFormFieldPropertyRules;
	private static Set getPrintVariableFormFieldPropertyRules() {
		if ( needRecalculateForNewAnno(PrintVariableFormFieldPropertyRules) ) {
			PrintVariableFormFieldPropertyRules = new TreeSet();
			PrintVariableFormFieldPropertyRules.addAll(createRulesForMemberAnnotations(IEGLConstants.FORM_SUBTYPE_PRINT));
		}
		return PrintVariableFormFieldPropertyRules;

	}

	private static Set TuiPrintVariableFormFieldPropertyRules;
	private static Set getTuiPrintVariableFormFieldPropertyRules() {
		if ( needRecalculateForNewAnno(TuiPrintVariableFormFieldPropertyRules) ) {
			TuiPrintVariableFormFieldPropertyRules = new TreeSet();
			TuiPrintVariableFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_HIGHLIGHT));
		}
		return TuiPrintVariableFormFieldPropertyRules;

	}

	private static Set TuiTextVariableFormFieldPropertyRules;
	private static Set getTuiTextVariableFormFieldPropertyRules() {
		if ( needRecalculateForNewAnno(TuiTextVariableFormFieldPropertyRules) ) {
			TuiTextVariableFormFieldPropertyRules = new TreeSet();
			TuiTextVariableFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_CURSOR));
			TuiTextVariableFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_DETECTABLE));
			TuiTextVariableFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_MODIFIED));
			TuiTextVariableFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_PROTECT));
			TuiTextVariableFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_VALIDATIONORDER));
		}
		return TuiTextVariableFormFieldPropertyRules;

	}

	private static Set TextVariableFormFieldPropertyRules;
	private static Set getTextVariableFormFieldPropertyRules() {
		if ( needRecalculateForNewAnno(TextVariableFormFieldPropertyRules) ) {
			TextVariableFormFieldPropertyRules = new TreeSet();
			TextVariableFormFieldPropertyRules.addAll(createRulesForMemberAnnotations(IEGLConstants.FORM_SUBTYPE_TEXT));
		}
		return TextVariableFormFieldPropertyRules;

	}

	private static Set PrintConstantFormFieldPropertyRules;
	private static Set getPrintConstantFormFieldPropertyRules() {
		if ( needRecalculateForNewAnno(PrintConstantFormFieldPropertyRules) ) {
			PrintConstantFormFieldPropertyRules = new TreeSet();
			PrintConstantFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_COLOR));
			PrintConstantFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_CURSOR));
			PrintConstantFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_DETECTABLE));
			PrintConstantFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_FIELDLEN));
			PrintConstantFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_HIGHLIGHT));
			PrintConstantFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_INTENSITY));
			PrintConstantFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_OUTLINE));
			PrintConstantFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_POSITION));
			PrintConstantFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_PROTECT));
			PrintConstantFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_VALUE));
		}
		return PrintConstantFormFieldPropertyRules;

	}

	private static Set TextConstantFormFieldPropertyRules;
	private static Set getTextConstantFormFieldPropertyRules() {
		if ( needRecalculateForNewAnno(TextConstantFormFieldPropertyRules) ) {
			TextConstantFormFieldPropertyRules = new TreeSet();
			TextConstantFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_COLOR));
			TextConstantFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_CURSOR));
			TextConstantFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_DETECTABLE));
			TextConstantFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_FIELDLEN));
			TextConstantFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_HIGHLIGHT));
			TextConstantFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_INTENSITY));
			TextConstantFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_OUTLINE));
			TextConstantFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_POSITION));
			TextConstantFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_PROTECT));
			TextConstantFormFieldPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_VALUE));
		}
		return TextConstantFormFieldPropertyRules;
	}

	private static Set PrintFloatingAreaPropertyRules;
	private static Set getPrintFloatingAreaPropertyRules() {
		if ( needRecalculateForNewAnno(PrintFloatingAreaPropertyRules) ) {
			PrintFloatingAreaPropertyRules = new TreeSet();
			PrintFloatingAreaPropertyRules.addAll(createRulesForFields(IEGLConstants.PROPERTY_PRINTFLOATINGAREA));
		}
		return PrintFloatingAreaPropertyRules;

	}

	private static Set ScreenFloatingAreaPropertyRules;
	private static Set getScreenFloatingAreaPropertyRules() {
		if ( needRecalculateForNewAnno(ScreenFloatingAreaPropertyRules) ) {
			ScreenFloatingAreaPropertyRules = new TreeSet();
			ScreenFloatingAreaPropertyRules.addAll(createRulesForFields(IEGLConstants.PROPERTY_SCREENFLOATINGAREA));
		}
		return ScreenFloatingAreaPropertyRules;

	}

	private static Set FormGroupPropertyRules;
	private static Set getFormGroupPropertyRules() {
		if ( needRecalculateForNewAnno(FormGroupPropertyRules) ) {
			FormGroupPropertyRules = new TreeSet();
			FormGroupPropertyRules.addAll(createRulesForElementKinds(ElementKind.FORMGROUPPART));
		}
		return FormGroupPropertyRules;

	}

	private static Set AnyRecordPropertyRules;
	private static Set getAnyRecordPropertyRules() {
		if ( needRecalculateForNewAnno(AnyRecordPropertyRules) ) {
			AnyRecordPropertyRules = new TreeSet();
			AnyRecordPropertyRules.addAll(createRulesForElementKinds(new EnumerationDataBinding[] {ElementKind.RECORDPART, ElementKind.STRUCTUREDRECORDPART}));
			AnyRecordPropertyRules.addAll(createRulesForFields(IEGLConstants.RECORD_SUBTYPE_BASIC));
			AnyRecordPropertyRules.addAll(createRulesForFields(IEGLConstants.RECORD_SUBTYPE_CONSOLE_FORM));
			AnyRecordPropertyRules.addAll(createRulesForFields(IEGLConstants.RECORD_SUBTYPE_DLI_SEGMENT));
			AnyRecordPropertyRules.addAll(createRulesForFields(IEGLConstants.RECORD_SUBTYPE_EXCEPTION));
			AnyRecordPropertyRules.addAll(createRulesForFields(IEGLConstants.RECORD_SUBTYPE_INDEXED));
			AnyRecordPropertyRules.addAll(createRulesForFields(IEGLConstants.RECORD_SUBTYPE_MQ));
			AnyRecordPropertyRules.addAll(createRulesForFields(IEGLConstants.RECORD_SUBTYPE_PSB_RECORD));
			AnyRecordPropertyRules.addAll(createRulesForFields(IEGLConstants.RECORD_SUBTYPE_RELATIVE));
			AnyRecordPropertyRules.addAll(createRulesForFields(IEGLConstants.RECORD_SUBTYPE_SERIAL));
			AnyRecordPropertyRules.addAll(createRulesForFields(IEGLConstants.RECORD_SUBTYPE_SQl));
			AnyRecordPropertyRules.addAll(createRulesForFields(IEGLConstants.RECORD_SUBTYPE_VGUI));
		}
		return AnyRecordPropertyRules;

	}

	private static Set VGUIRecordPropertyRules;
	private static Set getVGUIRecordPropertyRules() {
		if ( needRecalculateForNewAnno(VGUIRecordPropertyRules) ) {
			VGUIRecordPropertyRules = new TreeSet();
			VGUIRecordPropertyRules.addAll(createRulesForElementKinds(new EnumerationDataBinding[] {ElementKind.VGUIRECORDPART, ElementKind.STRUCTUREDRECORDPART}));
			VGUIRecordPropertyRules.addAll(createRulesForFields(IEGLConstants.RECORD_SUBTYPE_VGUI));
		}
		return VGUIRecordPropertyRules;

	}

	private static Set SQLRecordPropertyRules;
	private static Set getSQLRecordPropertyRules() {
		if ( needRecalculateForNewAnno(SQLRecordPropertyRules) ) {
			SQLRecordPropertyRules = new TreeSet();
			SQLRecordPropertyRules.addAll(createRulesForElementKinds(new EnumerationDataBinding[] {ElementKind.RECORDPART, ElementKind.STRUCTUREDRECORDPART}));
			SQLRecordPropertyRules.addAll(createRulesForFields(IEGLConstants.RECORD_SUBTYPE_SQl));
		}
		return SQLRecordPropertyRules;

	}

	private static Set MQRecordPropertyRules;
	private static Set getMQRecordPropertyRules() {
		if ( needRecalculateForNewAnno(MQRecordPropertyRules) ) {
			MQRecordPropertyRules = new TreeSet();
			MQRecordPropertyRules.addAll(createRulesForElementKinds(ElementKind.STRUCTUREDRECORDPART));
			MQRecordPropertyRules.addAll(createRulesForFields(IEGLConstants.RECORD_SUBTYPE_MQ));
		}
		return MQRecordPropertyRules;

	}

	private static Set SerialRecordPropertyRules;
	private static Set getSerialRecordPropertyRules() {
		if ( needRecalculateForNewAnno(SerialRecordPropertyRules) ) {
			SerialRecordPropertyRules = new TreeSet();
			SerialRecordPropertyRules.addAll(createRulesForElementKinds(ElementKind.STRUCTUREDRECORDPART));
			SerialRecordPropertyRules.addAll(createRulesForFields(IEGLConstants.RECORD_SUBTYPE_SERIAL));
		}
		return SerialRecordPropertyRules;

	}

	private static Set RelativeRecordPropertyRules;
	private static Set getRelativeRecordPropertyRules() {
		if ( needRecalculateForNewAnno(RelativeRecordPropertyRules) ) {
			RelativeRecordPropertyRules = new TreeSet();
			RelativeRecordPropertyRules.addAll(createRulesForElementKinds(ElementKind.STRUCTUREDRECORDPART));
			RelativeRecordPropertyRules.addAll(createRulesForFields(IEGLConstants.RECORD_SUBTYPE_RELATIVE));
		}
		return RelativeRecordPropertyRules;

	}

	private static Set IndexedRecordPropertyRules;
	private static Set getIndexedRecordPropertyRules() {
		if ( needRecalculateForNewAnno(IndexedRecordPropertyRules) ) {
			IndexedRecordPropertyRules = new TreeSet();
			IndexedRecordPropertyRules.addAll(createRulesForElementKinds(ElementKind.STRUCTUREDRECORDPART));
			IndexedRecordPropertyRules.addAll(createRulesForFields(IEGLConstants.RECORD_SUBTYPE_INDEXED));
		}
		return IndexedRecordPropertyRules;

	}

	private static Set CSVRecordPropertyRules;
	private static Set getCSVRecordPropertyRules() {
		if ( needRecalculateForNewAnno(CSVRecordPropertyRules) ) {
			CSVRecordPropertyRules = new TreeSet();
			CSVRecordPropertyRules.addAll(createRulesForElementKinds(ElementKind.RECORDPART));
			CSVRecordPropertyRules.addAll(createRulesForFields(IEGLConstants.RECORD_SUBTYPE_CSV));
		}
		return CSVRecordPropertyRules;

	}
	
	private static Set BasicRecordPropertyRules;
	private static Set getBasicRecordPropertyRules() {
		if ( needRecalculateForNewAnno(BasicRecordPropertyRules) ) {
			BasicRecordPropertyRules = new TreeSet();
			BasicRecordPropertyRules.addAll(createRulesForElementKinds(new EnumerationDataBinding[] {ElementKind.RECORDPART, ElementKind.STRUCTUREDRECORDPART}));
			BasicRecordPropertyRules.addAll(createRulesForFields(IEGLConstants.RECORD_SUBTYPE_BASIC));
		}
		return BasicRecordPropertyRules;

	}

	private static Set FillerStructureItemPropertyRules;
	private static Set getFillerStructureItemPropertyRules() {
		if ( needRecalculateForNewAnno(FillerStructureItemPropertyRules) ) {
			FillerStructureItemPropertyRules = new TreeSet();
			FillerStructureItemPropertyRules.addAll(createRulesFor(IEGLConstants.PROPERTY_UITYPE));
		}
		return FillerStructureItemPropertyRules;

	}

	private static Set StructureItemPropertyRules;
	private static Set getStructureItemPropertyRules() {
		if ( needRecalculateForNewAnno(StructureItemPropertyRules) ) {
			StructureItemPropertyRules = new TreeSet();
			StructureItemPropertyRules.addAll(createRulesForMemberAnnotations(IEGLConstants.RECORD_SUBTYPE_SQl));
			StructureItemPropertyRules.addAll(createRulesForGroup("formatting"));
			StructureItemPropertyRules.addAll(createRulesForGroup("validation"));
			StructureItemPropertyRules.addAll(createRulesForGroup("ui"));
			StructureItemPropertyRules.addAll(createRulesForMemberAnnotations(IEGLConstants.RECORD_SUBTYPE_DLI_SEGMENT));
			StructureItemPropertyRules.addAll(createRulesForElementKinds(new EnumerationDataBinding[] {ElementKind.FIELDMBR}));
		}
		return StructureItemPropertyRules;

	}
	
	private static Set dataItemPropertyRules;
	private static Set getDataItemPropertyRules() {
		if( needRecalculateForNewAnno(dataItemPropertyRules) ) {
			dataItemPropertyRules = new TreeSet();
			dataItemPropertyRules.addAll(createRulesFor(new TargetTypesHasAnnotationRecordFilter(ElementKind.DATAITEMPART)));
		}
		return dataItemPropertyRules;
	}

    public static Collection getPropertyRules( int location,
			ExternalTypeBinding externalTypeBinding )
	{
		switch ( location )
		{
			case locationConsoleButton:
				return getConsoleButtonPropertyRules( externalTypeBinding );
			case locationConsoleCheckbox:
				return getConsoleCheckboxPropertyRules( externalTypeBinding );
			case locationConsoleRadiogroup:
				return getConsoleRadiogroupPropertyRules( externalTypeBinding );
			case locationConsoleCombo:
				return getConsoleComboPropertyRules( externalTypeBinding );
			case locationConsoleList:
				return getConsoleListPropertyRules( externalTypeBinding );
			default:
				return null;
		}
	}

	private static Set ConsoleWidgetPropertyRules = new TreeSet();

	private static Set getConsoleWidgetPropertyRules(
			ExternalTypeBinding externalTypeBinding )
	{
		ConsoleWidgetPropertyRules.clear();
		if ( externalTypeBinding != null )
		{
			ConsoleWidgetPropertyRules
					.addAll( createRulesFor( IEGLConstants.PROPERTY_BOUNDS ) );
			for ( Iterator iter = externalTypeBinding.getDeclaredAndInheritedData()
					.iterator(); iter.hasNext(); )
			{
				ClassFieldBinding classFieldBinding = (ClassFieldBinding)iter.next();
				if ( classFieldBinding.isReadOnly() )
				{
					continue;
				}
				else
				{
					ConsoleWidgetPropertyRules.add( new EGLPropertyRule(
							classFieldBinding ) );
				}
			}
		}
		return ConsoleWidgetPropertyRules;
	}

	private static Set ConsoleButtonPropertyRules = new TreeSet();

	private static Set getConsoleButtonPropertyRules(
			ExternalTypeBinding externalTypeBinding )
	{
		if ( ConsoleButtonPropertyRules.size() == 0 )
		{
			ConsoleButtonPropertyRules
					.addAll( getConsoleWidgetPropertyRules( externalTypeBinding ) );
		}
		return ConsoleButtonPropertyRules;
	}

	private static Set ConsoleCheckboxPropertyRules = new TreeSet();

	private static Set getConsoleCheckboxPropertyRules(
			ExternalTypeBinding externalTypeBinding )
	{
		if ( ConsoleCheckboxPropertyRules.size() == 0 )
		{
			ConsoleCheckboxPropertyRules
					.addAll( getConsoleWidgetPropertyRules( externalTypeBinding ) );
		}
		return ConsoleCheckboxPropertyRules;
	}

	private static Set ConsoleRadiogroupPropertyRules = new TreeSet();

	private static Set getConsoleRadiogroupPropertyRules(
			ExternalTypeBinding externalTypeBinding )
	{
		if ( ConsoleRadiogroupPropertyRules.size() == 0 )
		{
			ConsoleRadiogroupPropertyRules
					.addAll( getConsoleWidgetPropertyRules( externalTypeBinding ) );
		}
		return ConsoleRadiogroupPropertyRules;
	}

	private static Set ConsoleComboPropertyRules = new TreeSet();

	private static Set getConsoleComboPropertyRules(
			ExternalTypeBinding externalTypeBinding )
	{
		if ( ConsoleComboPropertyRules.size() == 0 )
		{
			ConsoleComboPropertyRules
					.addAll( getConsoleWidgetPropertyRules( externalTypeBinding ) );
		}
		return ConsoleComboPropertyRules;
	}

	private static Set ConsoleListPropertyRules = new TreeSet();

	private static Set getConsoleListPropertyRules(
			ExternalTypeBinding externalTypeBinding )
	{
		if ( ConsoleListPropertyRules.size() == 0 )
		{
			ConsoleListPropertyRules
					.addAll( getConsoleWidgetPropertyRules( externalTypeBinding ) );
		}
		return ConsoleListPropertyRules;
	}	
	
	private static interface AnnotationRecordFilter {
		boolean passes(AnnotationTypeBindingImpl aTypeBinding); 
	}
	
	private static class TargetTypesHasAnnotationRecordFilter implements AnnotationRecordFilter {
		private EnumerationDataBinding[] allowedTargets;

		public TargetTypesHasAnnotationRecordFilter(EnumerationDataBinding allowedTarget) {
			this(new EnumerationDataBinding[] {allowedTarget});
		}
		
		public TargetTypesHasAnnotationRecordFilter(EnumerationDataBinding[] allowedTargets) {
			this.allowedTargets = allowedTargets;
		}
		
		public boolean passes(AnnotationTypeBindingImpl aTypeBinding) {
			IAnnotationBinding targets = (IAnnotationBinding) aTypeBinding.getAnnotation(AnnotationAnnotationTypeBinding.getInstance()).findData(InternUtil.intern("targets"));
			if(targets != IBinding.NOT_FOUND_BINDING) {
				Object[] value = (Object[]) targets.getValue();
				for(int i = 0; i < value.length; i++) {
					for(int j = 0; j < allowedTargets.length; j++) {
						if(allowedTargets[j] == value[i]) {
							return true;
						}
					}
				}
			}
			return false;
		}
	}
	
	private static class TargetInGroupFilter implements AnnotationRecordFilter {		
		private String groupName;

		public TargetInGroupFilter(String groupName) {
			this.groupName = groupName;
		}
		
		public boolean passes(AnnotationTypeBindingImpl aTypeBinding) {
			IAnnotationBinding group = aTypeBinding.getAnnotationRecord().getAnnotation(EGLAnnotationGroupAnnotationTypeBinding.getInstance());
			if(group != null) {
				return ((String) group.getValue()).equalsIgnoreCase(groupName);
			}
			return false;
		}
	}
	
	private static Collection createRulesFor(AnnotationRecordFilter filter) {
		return createRulesFor(filter, false);
	}
	
	private static Collection createRulesFor(AnnotationRecordFilter filter, boolean subtypes) {
		Set result = new TreeSet(new EGLCaseInsensitiveComparator());
		if(null == annoTypeMgr){ 
			return result;
		}
		
		for(Iterator iter = annoTypeMgr.getSystemPackageAnnotations().values().iterator(); iter.hasNext();) {
			FlexibleRecordBinding rec = (FlexibleRecordBinding) iter.next();
			if(rec.getAnnotation(AnnotationAnnotationTypeBinding.getInstance()) != null) {
				AnnotationTypeBindingImpl aTypeBindingImpl = new AnnotationTypeBindingImpl(rec, null);
				if(AnnotationAnnotationTypeBinding.getInstance() == aTypeBindingImpl.getAnnotationRecord().getSubType()) {
					if(subtypes && aTypeBindingImpl.isPartSubType() ||
					   !subtypes && !aTypeBindingImpl.isPartSubType()) {
						if(filter.passes(aTypeBindingImpl)) {
							result.add(new EGLPropertyRule(aTypeBindingImpl));
						}
					}
				}
			}
		}
		return result;
	}
	
	private static Collection createRulesForFields(String complexPropertyName) {
		return createRulesForField(complexPropertyName, null);
	}
	
	private static Collection createRulesForField(String complexPropertyName, String fieldName) {
		Set result = new TreeSet(new EGLCaseInsensitiveComparator());
		
		if(null == annoTypeMgr){ 
			return result;
		}
		FlexibleRecordBinding rec = (FlexibleRecordBinding) annoTypeMgr.getSystemPackageAnnotations().get(InternUtil.intern(complexPropertyName));
		if(rec != null) {
			IDataBinding[] fields = rec.getFields();
			for(int i = 0; i < fields.length; i++) {
				if(fieldName == null || fields[i].getName().equalsIgnoreCase(fieldName)) {
					EGLPropertyRule propertyRule = new EGLPropertyRule(fields[i]);
					propertyRule.setAnnotationField(true);
					result.add(propertyRule);
				}
			}
		}

		return result;
	}
	
	public static Collection createRulesFor(String propertyName) {

		if(null == annoTypeMgr){
			return(Collections.EMPTY_LIST);
		}
		FlexibleRecordBinding rec = (FlexibleRecordBinding)annoTypeMgr.getSystemPackageAnnotations().get(InternUtil.intern(propertyName));
		if(rec != null) {
			AnnotationTypeBindingImpl aTypeBindingImpl = new AnnotationTypeBindingImpl(rec, null);
			return Arrays.asList(new EGLPropertyRule[] {new EGLPropertyRule(aTypeBindingImpl)});
		}
		return Collections.EMPTY_LIST;
	}
	
	public static Collection createRulesForMemberAnnotations(String stereotypeName) {
		Set result = new TreeSet();
		if(null == annoTypeMgr){
			return(result);
		}
		FlexibleRecordBinding rec = (FlexibleRecordBinding) annoTypeMgr.getSystemPackageAnnotations().get(InternUtil.intern(stereotypeName));
		if(rec != null) {
			IAnnotationBinding aBinding = rec.getAnnotation(StereotypeAnnotationTypeBinding.getInstance());
			if(aBinding != null) {
				aBinding = (IAnnotationBinding) aBinding.findData("memberAnnotations");
				if(IBinding.NOT_FOUND_BINDING != aBinding) {
					Object[] recs = (Object[]) aBinding.getValue();
					for(int i = 0; i < recs.length; i++) {						
						result.addAll(createRulesFor(((IBinding) recs[i]).getName()));
					}
				}
			}
		}
		return result;
	}
	
	private static Collection createRulesForGroup(String groupName) {
		return createRulesFor(new TargetInGroupFilter(groupName));
	}
	
	public static Collection createRulesForElementKinds(EnumerationDataBinding elementKind) {
		return createRulesFor(new TargetTypesHasAnnotationRecordFilter(elementKind));
	}
	
	public static Collection createRulesForElementKinds(EnumerationDataBinding[] elementKinds) {
		return createRulesFor(new TargetTypesHasAnnotationRecordFilter(elementKinds));
	}
	
	public static Collection createRulesForSubtypes(EnumerationDataBinding elementKind) {
		return createRulesFor(new TargetTypesHasAnnotationRecordFilter(elementKind), true);
	}
	
	public static Collection createRulesForSubtypes(EnumerationDataBinding[] elementKinds) {
		return createRulesFor(new TargetTypesHasAnnotationRecordFilter(elementKinds), true);
	}
}
