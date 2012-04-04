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
package org.eclipse.edt.mof.egl;

import org.eclipse.edt.mof.egl.utils.InternUtil;

public interface MofConversion {
	
	String Mof_package_name = "org.eclipse.edt.mof";
	String EGL_reflect_package = "org.eclipse.edt.mof.egl";
	String EGL_lang_package = "egl.lang";
	String EGL_lang_reflect_package = "egl.lang.reflect";
	String EGL_lang_reflect_refTypes_package = "egl.lang.reflect.reftypes";
	String EGLX_lang_package = "eglx.lang";
	String EGL_KeyScheme = Type.EGL_KeyScheme + Type.KeySchemeDelimiter;
	String EGLX_SerializationKeyPrefix = EGL_KeyScheme + EGLX_lang_package;
	
	String Type_JavaObject = Mof_package_name+".JavaObject";
	String Type_EObject = Mof_package_name+".EObject";
	String Type_EDataType = Mof_package_name+".EDataType";
	String Type_EString = Mof_package_name+".EString";
	String Type_EBoolean = Mof_package_name+".EBoolean";
	String Type_EInteger = Mof_package_name+".EInt32";
	String Type_EFloat = Mof_package_name+".EFloat";
	String Type_EDecimal = Mof_package_name+".EDecimal";
	String Type_EList = Mof_package_name+".EList";
	String Type_EClass = Mof_package_name+".EClass";
	String Type_EParameter = Mof_package_name+".EParameter";
	String Type_EModelElement = Mof_package_name+".EModelElement";
	String Type_EClassifier = Mof_package_name+".EClassifier";
	String Type_EType = Mof_package_name+".EType";
	String Type_EFunction = Mof_package_name+".EFunction";
	String Type_EMetadataType = Mof_package_name+".EMetadataType";
	String Type_EStereotype = Mof_package_name+".EStereotype";
	String Type_EEnum = Mof_package_name+".EEnum";
	String Type_EEnumLiteral = Mof_package_name+".EEnumLiteral";
	String Type_EMetadataObject = Mof_package_name+".EMetadataObject";
	String Type_EField = Mof_package_name+".EField";
	    
	
	// EGL Base Types without keyScheme prefix
	String Type_Any = EGLX_lang_package+".EAny";
	String Type_Int = EGLX_lang_package+".EInt";
	String Type_Smallint = EGLX_lang_package+".ESmallint";
	String Type_Bigint = EGLX_lang_package+".EBigint";
	String Type_Float = EGLX_lang_package+".EFloat";
	String Type_Smallfloat = EGLX_lang_package+".ESmallfloat";
	String Type_Decimal = EGLX_lang_package+".EDecimal";
	String Type_Number = EGLX_lang_package+".ENumber";
	String Type_Boolean = EGLX_lang_package+".EBoolean";
	String Type_String = EGLX_lang_package+".EString";
	String Type_NULL = EGLX_lang_package+".NullType";
	String Type_Date = EGLX_lang_package+".EDate";
	String Type_Time = EGLX_lang_package+".ETime";
	String Type_Timestamp = EGLX_lang_package+".ETimestamp";
	String Type_List = EGLX_lang_package+".EList";
	String Type_Dictionary = EGLX_lang_package+".EDictionary";

	// Below here are types not supported in EDT 0.7.

	String Type_Num = EGLX_lang_package+".AnyNum";
	String Type_Bin = EGLX_lang_package+".AnyBin";
	String Type_Numc = EGLX_lang_package+".AnyNumc";
	String Type_Pacf = EGLX_lang_package+".AnyPacf";
	String Type_Char = EGLX_lang_package+".AnyChar";
	String Type_MBChar = EGLX_lang_package+".AnyMBChar";
	String Type_DBChar = EGLX_lang_package+".AnyDBChar";
	String Type_Hex = EGLX_lang_package+".AnyHex";
	String Type_Unicode = EGLX_lang_package+".AnyUnicode";
	String Type_Blob = EGLX_lang_package+".AnyBlob";
	String Type_Clob = EGLX_lang_package+".AnyClob";
	String Type_Interval = EGLX_lang_package+".AnyInterval";
	String Type_MonthInterval = EGLX_lang_package+".AnyMonthsInterval";
	String Type_SecondsInterval = EGLX_lang_package+".AnySecondsInterval";
	String Type_UBin = EGLX_lang_package+".AnyUBin";
	String Type_UnicodeNum = EGLX_lang_package+".AnyUnicodeNum";
	String Type_ArrayDictionary = EGLX_lang_package+".EArrayDictionary";
	
	String Type_EGLNullType = EGL_KeyScheme+Type_NULL;
	String Type_EGLAny = EGL_KeyScheme+Type_Any;
	String Type_EGLInt = EGL_KeyScheme+Type_Int;
	String Type_EGLSmallint = EGL_KeyScheme+Type_Smallint;
	String Type_EGLBigint = EGL_KeyScheme+Type_Bigint;
	String Type_EGLFloat = EGL_KeyScheme+Type_Float;
	String Type_EGLSmallfloat = EGL_KeyScheme+Type_Smallfloat;
	String Type_EGLDecimal = EGL_KeyScheme+Type_Decimal;
	String Type_EGLNum = EGL_KeyScheme+Type_Num;
	String Type_EGLBin = EGL_KeyScheme+Type_Bin;
	String Type_EGLNumber = EGL_KeyScheme+Type_Number;
	String Type_EGLNumc = EGL_KeyScheme+Type_Numc;
	String Type_EGLPacf = EGL_KeyScheme+Type_Pacf;
	String Type_EGLBoolean = EGL_KeyScheme+Type_Boolean;
	String Type_EGLChar = EGL_KeyScheme+Type_Char;
	String Type_EGLMBChar = EGL_KeyScheme+Type_MBChar;
	String Type_EGLDBChar = EGL_KeyScheme+Type_DBChar;
	String Type_EGLHex = EGL_KeyScheme+Type_Hex;
	String Type_EGLString = EGL_KeyScheme+Type_String;
	String Type_EGLUnicode = EGL_KeyScheme+Type_Unicode;
	String Type_EGLBlob = EGL_KeyScheme+Type_Blob;
	String Type_EGLClob = EGL_KeyScheme+Type_Clob;
	String Type_EGLDate = EGL_KeyScheme+Type_Date;
	String Type_EGLTime = EGL_KeyScheme+Type_Time;
	String Type_EGLTimestamp = EGL_KeyScheme+Type_Timestamp;
	String Type_EGLInterval = EGL_KeyScheme+Type_Interval;
	String Type_EGLMonthInterval = EGL_KeyScheme+Type_MonthInterval;
	String Type_EGLSecondsInterval = EGL_KeyScheme+Type_SecondsInterval;
	String Type_EGLUBin = EGL_KeyScheme+Type_UBin;
	String Type_EGLUnicodeNum = EGL_KeyScheme+Type_UnicodeNum;
	String Type_EGLList = EGL_KeyScheme+Type_List;
	String Type_EGLDictionary = EGL_KeyScheme+Type_Dictionary;
	String Type_EGLArrayDictionary = EGL_KeyScheme+Type_ArrayDictionary;
	
	
	// EGL Part Types
	String Type_EGLRecord = EGL_reflect_package+".EGLRecord";
	String Type_EGLStructuredRecord = EGL_reflect_package+".StructuredRecord";
	String Type_EGLExternalType = EGL_reflect_package+".EGLExternalType";
	String Type_EGLEnumeration = EGL_reflect_package+".EGLEnumeration";
	String Type_EGLDataTable = EGL_reflect_package+".EGLDataTable";
	String Type_EGLProgram = EGL_reflect_package+".EGLProgram";
	String Type_EGLDelegate = EGL_reflect_package+".EGLDelegate";
	String Type_EGLForm = EGL_reflect_package+".EGLForm";
	String Type_EGLFormGroup = EGL_reflect_package+".EGLFormGroup";
	String Type_EGLInterface = EGL_reflect_package+".EGLEnterface";
	String Type_EGLDataItem = EGL_reflect_package+".EGLDataItem";
	String Type_EGLLibrary = EGL_reflect_package+".EGLLibrary";
	String Type_EGLHandler = EGL_reflect_package+".EGLHandler";
	String Type_EGLFunctionPart = EGL_reflect_package+".EGLFunctionPart";
	String Type_EGLService = EGL_reflect_package+".EGLService";
	String Type_EGLAnnotationType = EGL_reflect_package+".AnnotationType";
	String Type_EGLStereotypeType = EGL_reflect_package+".StereotypeType";
	
	String Type_Part = EGL_reflect_package+".Part";
	String Type_Record = EGL_reflect_package+".Record";
	String Type_StructuredRecord = EGL_reflect_package+".StructuredRecord";
	String Type_ExternalType = EGL_reflect_package+".ExternalType";
	String Type_Enumeration = EGL_reflect_package+".Enumeration";
	String Type_DataTable = EGL_reflect_package+".DataTable";
	String Type_Program = EGL_reflect_package+".Program";
	String Type_Delegate = EGL_reflect_package+".Delegate";
	String Type_Form = EGL_reflect_package+".Form";
	String Type_FormGroup = EGL_reflect_package+".FormGroup";
	String Type_Interface = EGL_reflect_package+".Interface";
	String Type_DataItem = EGL_reflect_package+".DataItem";
	String Type_Library = EGL_reflect_package+".Library";
	String Type_Handler = EGL_reflect_package+".Handler";
	String Type_FunctionPart = EGL_reflect_package+".FunctionPart";
	String Type_Service = EGL_reflect_package+".Service";
	String Type_Annotation = EGL_reflect_package+".Annotation";
	String Type_Stereotype = EGL_reflect_package+".Stereotype";
	String Type_AnnotationType = EGL_reflect_package+".AnnotationType";
	String Type_StereotypeType = EGL_reflect_package+".StereotypeType";
	String Type_ElementKind = EGL_reflect_package+".ElementKind";
	
	// EGL Model Types
	String Type_EGLEnumerationEntry = EGL_reflect_package+".EnumerationEntry";
	String Type_EGLField = EGL_reflect_package+".Field";
	String Type_EGLStructuredField = EGL_reflect_package+".StructuredField";
	String Type_EGLFunction = EGL_reflect_package+".EGLFunction";
	String Type_EGLOperation = EGL_reflect_package+".EGLOperation";
	String Type_EGLFunctionParameter = EGL_reflect_package+".FunctionParameter";
	String Type_EGLConstructor = EGL_reflect_package+".EGLConstructor";
	String Type_EGLBuiltInOperation = EGL_reflect_package+".BuiltInOperation";
	String Type_EGLConversionOperation = EGL_reflect_package+".ConversionOperation";
	String Type_EGLModelElement = EGL_reflect_package+".ModelElement";
	String Type_EGLClassifier = EGL_reflect_package+".Classifier";
	String Type_EGLClass = EGL_reflect_package+".EGLClass";
	String Type_EGLDataTypeType = EGL_reflect_package+".DataType";
	String Type_EGLPrimitive = EGL_reflect_package+".Primitive";

	String Type_Element = EGL_reflect_package+".Element";
	String Type_EnumerationEntry = EGL_reflect_package+".EnumerationEntry";
	String Type_ConstantField = EGL_reflect_package+".ConstantField";
	String Type_Field = EGL_reflect_package+".Field";
	String Type_StructuredField = EGL_reflect_package+".StructuredField";
	String Type_Type = EGL_reflect_package+".Type";
	String Type_Function = EGL_reflect_package+".Function";
	String Type_Operation = EGL_reflect_package+".Operation";
	String Type_FunctionParameter = EGL_reflect_package+".FunctionParameter";
	String Type_Constructor = EGL_reflect_package+".Constructor";
	String Type_BuiltInOperation = EGL_reflect_package+".BuiltInOperation";
	String Type_ConversionOperation = EGL_reflect_package+".ConversionOperation";
	String Type_ModelElement = EGL_reflect_package+".ModelElement";
	String Type_Classifier = EGL_reflect_package+".Classifier";
	String Type_DataTypeType = EGL_reflect_package+".DataType";
	String Type_Primitive = EGL_reflect_package+".Primitive";
	String Type_SequenceType = EGL_reflect_package+".SequenceType";
	String Type_FixedPrecisionType = EGL_reflect_package+".FixedPrecisionType";
	String Type_IntervalType = EGL_reflect_package+".IntervalType";
	String Type_TimestampType = EGL_reflect_package+".TimestampType";
	
	// EGL Base type hierarchy
	String Type_AnyValue = EGLX_SerializationKeyPrefix+".AnyValue";
	String Type_AnyText = EGLX_SerializationKeyPrefix+".AnyText";
	String Type_AnyRecord = EGLX_SerializationKeyPrefix+".AnyRecord";
	String Type_AnyStruct = EGLX_SerializationKeyPrefix+".AnyStruct";
	String Type_AnyEnumeration = EGLX_SerializationKeyPrefix+".AnyEnumeration";
	String Type_AnyDelegate = EGLX_SerializationKeyPrefix+".AnyDelegate";
	
	// EGL Reflect Types	
	String Type_FieldRef = EGL_lang_reflect_refTypes_package+".EGLFieldRef";
	String Type_FieldInTargetRef = EGL_lang_reflect_refTypes_package+".EGLFieldInTargetRef";
	String Type_FunctionRef = EGL_lang_reflect_refTypes_package+".EGLFunctionRef";
	String Type_FunctionMemberRef = EGL_lang_reflect_refTypes_package+".EGLFunctionMemberRef";
	String Type_InternalRef = EGL_lang_reflect_refTypes_package+".EGLInternalRef";
	String Type_PartRef = EGL_lang_reflect_refTypes_package+".EGLPartRef";
	String Type_TypeRef = EGL_lang_reflect_refTypes_package+".EGLTypeRef";
	String Type_RecordRef = EGL_lang_reflect_refTypes_package+".EGLRecordRef";
	String Type_ServiceRef = EGL_lang_reflect_refTypes_package+".EGLServiceRef";
	String Type_SQLStringRef = EGL_lang_reflect_refTypes_package+".EGLSQLStringRef";

	// EGL System parts
	String Type_SqlRecord = "egl.io.sql.SQLRecord";
	String Type_DliRecord = "egl.io.sql.DLIRecord";
	
	String irExt = ".ir";
	String ArraySignatureChar = "[";
	String ListSignatureChar = "<";
	String NullableSignatureChar = "?";
	String OpenParameterizedTypeSignatureChar = "(";
	String CloseParameterizedTypeSignatureChar = ")";
	String EAnnotation_Source_StereotypedBy = "stereotypedBy";
	String Operator_Widen = "widen";
	String Operator_Narrow = "narrow";
	String KeySchemeDelimiter = ":";

	String ElementKind_RecordPart = InternUtil.intern("RecordPart");
	String ElementKind_StructuredRecordPart = InternUtil.intern("StructuredRecordPart");
	String ElementKind_ProgramPart = InternUtil.intern("ProgramPart");
	String ElementKind_LibraryPart = InternUtil.intern("LibraryPart");
	String ElementKind_HandlerPart = InternUtil.intern("HandlerPart");
	String ElementKind_InterfacePart = InternUtil.intern("InterfacePart");
	String ElementKind_ServicePart = InternUtil.intern("ServicePart");
	String ElementKind_ExternalTypePart = InternUtil.intern("ExternalTypePart");
	String ElementKind_DelegatePart = InternUtil.intern("DelegatePart");
	String ElementKind_FormGroupPart = InternUtil.intern("FormGroupPart");
	String ElementKind_FormPart = InternUtil.intern("FormPart");
	String ElementKind_DataTablePart = InternUtil.intern("DataTablePart");
	String ElementKind_DataItemPart = InternUtil.intern("DataItemPart");
	String ElementKind_Part = InternUtil.intern("Part");
	String ElementKind_FieldMbr = InternUtil.intern("FieldMbr");
	String ElementKind_StructuredFieldMbr = InternUtil.intern("StructuredFieldMbr");
	String ElementKind_FunctionMbr = InternUtil.intern("FunctionMbr");
	String ElementKind_ConstructorMbr = InternUtil.intern("ConstructorMbr");
	String ElementKind_CallStatement = InternUtil.intern("CallStatement");
	String ElementKind_ShowStatement = InternUtil.intern("ShowStatement");
	String ElementKind_TransferStatement = InternUtil.intern("TransferStatement");
	String ElementKind_OpenUIStatement = InternUtil.intern("OpenUIStatement");
	String ElementKind_LibraryUse = InternUtil.intern("LibraryUse");
	String ElementKind_FormUse = InternUtil.intern("FormUse");
	String ElementKind_FormGroupUse = InternUtil.intern("FormGroupUse");
	String ElementKind_DataTableUse = InternUtil.intern("DataTableUse");
	String ElementKind_AnnotationValue = InternUtil.intern("AnnotationValue");
	String ElementKind_AnnotationType = InternUtil.intern("AnnotationType");

}
