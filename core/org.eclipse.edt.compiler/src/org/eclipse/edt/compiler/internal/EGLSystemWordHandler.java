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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author jshavor
 * 
 * This is the public interface to for all EGL system words
 */
public class EGLSystemWordHandler {

	static TreeMap systemWords;
	static TreeMap mathLibWords;
	static TreeMap strLibWords;
	static TreeMap sysLibWords;
	static TreeMap sysVarWords;
	static TreeMap consoleLibWords;
	static TreeMap reportLibWords;
	static TreeMap lobLibWords;
	static TreeMap dynArrayWords;
	static TreeMap dictionaryWords;
	static TreeMap recordWords;
	static TreeMap reportHandlerWords;
	static TreeMap birtHandlerWords;
	static TreeMap vgLibWords;
	static TreeMap vgVarWords;
	static TreeMap converseVarWords;
	static TreeMap javaLibWords;
	static TreeMap dateTimeLibWords;		
	static TreeMap j2eeLibWords;
	static TreeMap converseLibWords;
	static TreeMap dliVarWords;
	static TreeMap dliLibWords;
	static TreeMap serviceLibWords;

	//Types common to both variables and functions
	public static final int SYSTEM_WORD_NONE = 0;

	//Types of variables
	public static final int SYSTEM_WORD_CHARACTER = 1 << 2;
	public static final int SYSTEM_WORD_NUMERIC = 1 << 3;
	public static final int SYSTEM_WORD_DICTIONARY = 1 << 4;
	public static final int SYSTEM_WORD_NAMEDTYPE = 1 << 5;
	public static final int SYSTEM_WORD_REF = 1 << 6;
	public static final int SYSTEM_WORD_READ_ONLY = 1 << 7;

	//Types of functions
	public static final int SYSTEM_WORD_RETURNS = 1 << 8;
	public static final int SYSTEM_WORD_NO_RETURNS = 1 << 9;
	public static final int SYSTEM_WORD_PAGEHANDLER = 1 << 10;
	public static final int SYSTEM_WORD_REPORTHANDLER = 1 << 11;
	public static final int SYSTEM_WORD_RETURN_TYPE_IS_NULLABLE = 1 << 12;
	public static final int SYSTEM_WORD_BIRTHANDLER = 1 << 13;
	
	// Special rules for "validArgumentCounts" parameter to EGLSystemFunctionWord. These should all
	// be negative numbers.
	public static final int ARG_COUNT_N_OR_MORE = -1;

	//  Library Names
	public static final String mathLibrary = IEGLConstants.KEYWORD_MATHLIB;
	public static final String stringLibrary = IEGLConstants.KEYWORD_STRLIB;
	public static final String systemLibrary = IEGLConstants.KEYWORD_SYSLIB;	
	public static final String systemVariablesLibrary = IEGLConstants.KEYWORD_SYSVAR;
	public static final String converseVariablesLibrary = IEGLConstants.KEYWORD_CONVERSEVAR;  
	public static final String dateTimeLibrary = IEGLConstants.KEYWORD_DATETIMELIB;  
	public static final String serviceLibrary = IEGLConstants.KEYWORD_SERVICELIB;
	
	
	// to be used as library value to denote that an EGLSystemVariable or EGLSystemFunction
	//		does not belong to one of the 4 system libraries
	//	Instead, they must be qualified by the target object
	//			dynamicArray - must be qualified by the target array
	public static final String dynamicArraySystemWord = "dynamicArray";		//$NON-NLS-1$
	public static final String dictionarySystemWord = "dictionary";		//$NON-NLS-1$
	//			record - must be qualified by the target record
	public static final String recordSystemFunction = "record"; //$NON-NLS-1$
	public static final String arrayElement = "arrayElement"; //$NON-NLS-1$
	public static final String textField = "textField"; //$NON-NLS-1$	
	public static final String identifier = "identifier";  // must be a fixed length item (standalone, or in a record, table, or form) or a fixed record, to be used for an output or input/output parameter //$NON-NLS-1$	
	public static final String reportHandlerSystemFunction = "reportHandler"; //$NON-NLS-1$
	public static final String mnemonic = "mnemonic"; //$NON-NLS-1$	
	public static final String vagText = "vagText"; //$NON-NLS-1$	
	public static final String vagTextOrNumeric = "vagTextOrNumeric"; //$NON-NLS-1$
	public static final String anyEglPrimitive = "anyEglPrimitive"; //$NON-NLS-1$	
	public static final String attribute = "attribute"; //$NON-NLS-1$
	public static final String itemOrRecord = "itemOrRecord"; // must be a fixed length item (standalone, or in a record, table, or form) or a fixed record, to be used for an input only parameter //$NON-NLS-1$
	public static final String fixedOrFlexibleRecord = "fixedOrFlexibleRecord"; //$NON-NLS-1$
	public static final String serviceOrInterface = "serviceOrInterface"; // must be a service or interface part

	//	largeObject - must be qualified by the target largeObject
	public static final String largeObjectSystemFunction = "largeObject";
	
	public static int INOUT = 0;
	public static int IN = 1;
	public static int OUT = 2;
	
	static {
		systemWords = new TreeMap(new EGLCaseInsensitiveComparator());
		mathLibWords = new TreeMap(new EGLCaseInsensitiveComparator());
		strLibWords = new TreeMap(new EGLCaseInsensitiveComparator());
		sysLibWords = new TreeMap(new EGLCaseInsensitiveComparator());
		sysVarWords = new TreeMap(new EGLCaseInsensitiveComparator());
		consoleLibWords = new TreeMap(new EGLCaseInsensitiveComparator());
		reportLibWords = new TreeMap(new EGLCaseInsensitiveComparator());
		lobLibWords = new TreeMap(new EGLCaseInsensitiveComparator());
		vgLibWords = new TreeMap(new EGLCaseInsensitiveComparator());
		vgVarWords = new TreeMap(new EGLCaseInsensitiveComparator());
		converseVarWords = new TreeMap(new EGLCaseInsensitiveComparator());
		javaLibWords = new TreeMap(new EGLCaseInsensitiveComparator());
		dateTimeLibWords = new TreeMap(new EGLCaseInsensitiveComparator());		
		j2eeLibWords = new TreeMap(new EGLCaseInsensitiveComparator());
		converseLibWords = new TreeMap(new EGLCaseInsensitiveComparator());		
		dynArrayWords = new TreeMap(new EGLCaseInsensitiveComparator());
		dictionaryWords = new TreeMap(new EGLCaseInsensitiveComparator());
		recordWords = new TreeMap(new EGLCaseInsensitiveComparator());
		reportHandlerWords = new TreeMap(new EGLCaseInsensitiveComparator());
		dliVarWords = new TreeMap(new EGLCaseInsensitiveComparator());
		dliLibWords = new TreeMap(new EGLCaseInsensitiveComparator());		
		serviceLibWords = new TreeMap(new EGLCaseInsensitiveComparator());
		
		systemWords.put(IEGLConstants.KEYWORD_MATHLIB,mathLibWords);
		systemWords.put(IEGLConstants.KEYWORD_STRLIB,strLibWords);
		systemWords.put(IEGLConstants.KEYWORD_SYSLIB,sysLibWords);
		systemWords.put(IEGLConstants.KEYWORD_SYSVAR,sysVarWords);
		systemWords.put(IEGLConstants.KEYWORD_DATETIMELIB,dateTimeLibWords);
		systemWords.put(IEGLConstants.KEYWORD_CONVERSEVAR,converseVarWords);
		systemWords.put(dynamicArraySystemWord,dynArrayWords);
		systemWords.put(dictionarySystemWord,dictionaryWords);
		systemWords.put(recordSystemFunction,recordWords);
		systemWords.put(reportHandlerSystemFunction,reportHandlerWords);
		systemWords.put(IEGLConstants.KEYWORD_SERVICELIB, serviceLibWords);
		
		//parms:
		//	- special function name
		//	- additional information about the special function (used by content assist)
		//  - primitive return type
		//  - return length
		//	- number of parameters (used by content assist)
		// ***IF YOU MODIFY THIS LIST YOU NEED TO REGEN THE CONSTANTS IN IEGLConstants USING***
		// org.eclipse.edt.compiler.internal.dev.tools.EGLSystemWordTool

		//math functions
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_ABS, new EGLSystemFunctionWord("abs", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Abs, mathLibrary, IEGLConstants.NUMBER_STRING, 9, new String[] {"numericItem"}, new String[] { IEGLConstants.NUMBER_STRING }, new int[] { INOUT }, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_ACOS, new EGLSystemFunctionWord("acos", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Acos, mathLibrary, IEGLConstants.NUMBER_STRING, 9, new String[] {"numericItem"}, new String[] { IEGLConstants.NUMBER_STRING }, new int[] { INOUT }, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_ASIN, new EGLSystemFunctionWord("asin", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Asin, mathLibrary, IEGLConstants.NUMBER_STRING, 9, new String[] {"numericItem"}, new String[] { IEGLConstants.NUMBER_STRING }, new int[] { INOUT }, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_ATAN, new EGLSystemFunctionWord("atan", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Atan, mathLibrary, IEGLConstants.NUMBER_STRING, 9, new String[] {"numericItem"}, new String[] { IEGLConstants.NUMBER_STRING }, new int[] { INOUT }, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_ATAN2, new EGLSystemFunctionWord("atan2", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Atan2, mathLibrary, IEGLConstants.NUMBER_STRING, 9, new String[] {"numericItem1", "numericItem2"}, new String[] {IEGLConstants.NUMBER_STRING, IEGLConstants.NUMBER_STRING}, new int[] { INOUT, INOUT }, new int[] { 2 })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_CEILING, new EGLSystemFunctionWord("ceiling", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Ceil, mathLibrary, IEGLConstants.NUMBER_STRING, 9, new String[] {"numericItem"}, new String[] { IEGLConstants.NUMBER_STRING }, new int[] { INOUT }, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_COMPARENUM, new EGLSystemFunctionWord("compareNum", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_CompareNumbers, mathLibrary, IEGLConstants.INT_STRING, 9 , new String[] {"numericItem1", "numericItem2"}, new String[] {IEGLConstants.NUMBER_STRING, IEGLConstants.NUMBER_STRING}, new int[] { INOUT, INOUT }, new int[] { 2 })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_COS, new EGLSystemFunctionWord("cos", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Cos, mathLibrary, IEGLConstants.NUMBER_STRING, 9, new String[] {"numericItem"}, new String[] { IEGLConstants.NUMBER_STRING }, new int[] { INOUT }, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_COSH, new EGLSystemFunctionWord("cosh", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Cosh, mathLibrary, IEGLConstants.NUMBER_STRING, 9, new String[] {"numericItem"}, new String[] { IEGLConstants.NUMBER_STRING }, new int[] { INOUT }, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_EXP, new EGLSystemFunctionWord("exp", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Exp, mathLibrary, IEGLConstants.NUMBER_STRING, 9, new String[] {"numericItem"}, new String[] { IEGLConstants.NUMBER_STRING }, new int[] { INOUT }, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_FLOATINGASSIGN, new EGLSystemFunctionWord("floatingAssign", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_FloatingAssign, mathLibrary, IEGLConstants.NUMBER_STRING, 9, new String[] {"numericItem"}, new String[] { IEGLConstants.NUMBER_STRING }, new int[] { INOUT }, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_FLOATINGDIFFERENCE, new EGLSystemFunctionWord("floatingDifference", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_FloatingDifference, mathLibrary, IEGLConstants.NUMBER_STRING, 9, new String[] {"numericItem1", "numericItem2"}, new String[] {IEGLConstants.NUMBER_STRING, IEGLConstants.NUMBER_STRING}, new int[] { INOUT, INOUT }, new int[] { 2 })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_FLOATINGMOD, new EGLSystemFunctionWord("floatingMod", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_FloatingMod, mathLibrary, IEGLConstants.NUMBER_STRING, 9, new String[] {"numericItem1", "numericItem2"}, new String[] {IEGLConstants.NUMBER_STRING, IEGLConstants.NUMBER_STRING}, new int[] { INOUT, INOUT }, new int[] { 2 })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_FLOATINGPRODUCT, new EGLSystemFunctionWord("floatingProduct", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_FloatingProduct, mathLibrary, IEGLConstants.NUMBER_STRING, 9, new String[] {"numericItem1", "numericItem2"}, new String[] {IEGLConstants.NUMBER_STRING, IEGLConstants.NUMBER_STRING}, new int[] { INOUT, INOUT }, new int[] { 2 })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_FLOATINGQUOTIENT, new EGLSystemFunctionWord("floatingQuotient", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_FloatingQuotent, mathLibrary, IEGLConstants.NUMBER_STRING, 9, new String[] {"numericItem1", "numericItem2"}, new String[] {IEGLConstants.NUMBER_STRING, IEGLConstants.NUMBER_STRING}, new int[] { INOUT, INOUT }, new int[] { 2 })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_FLOATINGSUM, new EGLSystemFunctionWord("floatingSum", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_FloatingSum, mathLibrary, IEGLConstants.NUMBER_STRING, 9, new String[] {"numericItem1", "numericItem2"}, new String[] {IEGLConstants.NUMBER_STRING, IEGLConstants.NUMBER_STRING}, new int[] { INOUT, INOUT }, new int[] { 2 })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_FLOOR, new EGLSystemFunctionWord("floor", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Floor, mathLibrary, IEGLConstants.NUMBER_STRING, 9, new String[] {"numericItem"}, new String[] { IEGLConstants.NUMBER_STRING }, new int[] { INOUT }, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_FREXP, new EGLSystemFunctionWord("frexp", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Frexp, mathLibrary, IEGLConstants.NUMBER_STRING, 9, new String[] {"numericItem", "integerExponent"}, new String[] {IEGLConstants.NUMBER_STRING, IEGLConstants.INT_STRING}, new int[] { INOUT, IN }, new int[] { 2 })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_LDEXP, new EGLSystemFunctionWord("ldexp", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Ldexp, mathLibrary, IEGLConstants.NUMBER_STRING, 9, new String[] {"numericItem", "integer"}, new String[] {IEGLConstants.NUMBER_STRING, IEGLConstants.INT_STRING}, new int[] { INOUT, IN }, new int[] { 2 })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_LOG, new EGLSystemFunctionWord("log", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Log, mathLibrary, IEGLConstants.NUMBER_STRING, 9, new String[] {"numericItem"}, new String[] { IEGLConstants.NUMBER_STRING }, new int[] { INOUT }, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_LOG10, new EGLSystemFunctionWord("log10", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Log10, mathLibrary, IEGLConstants.NUMBER_STRING, 9, new String[] {"numericItem"}, new String[] { IEGLConstants.NUMBER_STRING }, new int[] { INOUT }, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_MAXIMUM, new EGLSystemFunctionWord("maximum", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Max, mathLibrary, IEGLConstants.NUMBER_STRING, 9, new String[] {"numericItem1", "numericItem2"}, new String[] {IEGLConstants.NUMBER_STRING, IEGLConstants.NUMBER_STRING}, new int[] { INOUT, INOUT }, new int[] { 2 })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_MINIMUM, new EGLSystemFunctionWord("minimum", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Min, mathLibrary, IEGLConstants.NUMBER_STRING, 9, new String[] {"numericItem1", "numericItem2"}, new String[] {IEGLConstants.NUMBER_STRING, IEGLConstants.NUMBER_STRING}, new int[] { INOUT, INOUT }, new int[] { 2 })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_MODF, new EGLSystemFunctionWord("modf", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Modf, mathLibrary, IEGLConstants.NUMBER_STRING,9, new String[] {"numericItem", "integerNumericItem"}, new String[] {IEGLConstants.NUMBER_STRING, IEGLConstants.NUMBER_STRING}, new int[] { INOUT, INOUT }, new int[] { 2 })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_POW, new EGLSystemFunctionWord("pow", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Pow, mathLibrary, IEGLConstants.NUMBER_STRING, 9, new String[] {"numericItem1", "numericItem2"}, new String[] {IEGLConstants.NUMBER_STRING, IEGLConstants.NUMBER_STRING}, new int[] { INOUT, INOUT }, new int[] { 2 })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_PRECISION, new EGLSystemFunctionWord("precision", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Precision, mathLibrary, IEGLConstants.BIN_STRING, 9, new String[] {"numericItem"}, new String[] { IEGLConstants.NUMBER_STRING }, new int[] { INOUT }, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_ROUND, new EGLSystemFunctionWord("round", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Round, mathLibrary, IEGLConstants.NUMBER_STRING, 9 , new String[] {"numericExpression", "power"}, new String[] {IEGLConstants.NUMBER_STRING, IEGLConstants.INT_STRING}, new int[] { IN, INOUT }, new int[] { 1, 2 })); //$NON-NLS-1$ //$NON-NLS-2$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_SIN, new EGLSystemFunctionWord("sin", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Sin, mathLibrary, IEGLConstants.NUMBER_STRING, 9, new String[] {"numericItem"}, new String[] { IEGLConstants.NUMBER_STRING }, new int[] { INOUT }, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_SINH, new EGLSystemFunctionWord("sinh", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Sinh, mathLibrary, IEGLConstants.NUMBER_STRING, 9, new String[] {"numericItem"}, new String[] { IEGLConstants.NUMBER_STRING }, new int[] { INOUT }, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_SQRT, new EGLSystemFunctionWord("sqrt", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Sqrt, mathLibrary, IEGLConstants.NUMBER_STRING, 9, new String[] {"numericItem"}, new String[] { IEGLConstants.NUMBER_STRING }, new int[] { INOUT }, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_TAN, new EGLSystemFunctionWord("tan", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Tan, mathLibrary, IEGLConstants.NUMBER_STRING, 9, new String[] {"numericItem"}, new String[] { IEGLConstants.NUMBER_STRING }, new int[] { INOUT }, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_TANH, new EGLSystemFunctionWord("tanh", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Tanh, mathLibrary, IEGLConstants.NUMBER_STRING, 9, new String[] {"numericItem"}, new String[] { IEGLConstants.NUMBER_STRING }, new int[] { INOUT }, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_STRINGASINT, new EGLSystemFunctionWord("stringAsInt", SYSTEM_WORD_RETURNS | SYSTEM_WORD_RETURN_TYPE_IS_NULLABLE, IEGLConstants.Special_Function_StringAsInt, mathLibrary, IEGLConstants.BIGINT_STRING, 18, new String[] {"numberAsText"}, new String[] { IEGLConstants.STRING_STRING }, new int[] { IN }, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_STRINGASDECIMAL, new EGLSystemFunctionWord("stringAsDecimal", SYSTEM_WORD_RETURNS | SYSTEM_WORD_RETURN_TYPE_IS_NULLABLE, IEGLConstants.Special_Function_StringAsDecimal, mathLibrary, IEGLConstants.NUMBER_STRING, 0, new String[] {"numberAsText"}, new String[] { IEGLConstants.STRING_STRING }, new int[] { IN }, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$
		mathLibWords.put(IEGLConstants.SYSTEM_WORD_STRINGASFLOAT, new EGLSystemFunctionWord("stringAsFloat", SYSTEM_WORD_RETURNS | SYSTEM_WORD_RETURN_TYPE_IS_NULLABLE, IEGLConstants.Special_Function_StringAsFloat, mathLibrary, IEGLConstants.FLOAT_STRING, 8, new String[] {"numberAsText"}, new String[] { IEGLConstants.STRING_STRING }, new int[] { IN }, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$

		//string functions
		strLibWords.put(IEGLConstants.SYSTEM_WORD_COMPARESTR, new EGLSystemFunctionWord("compareStr", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_CompareStrings, stringLibrary, IEGLConstants.BIN_STRING, 9, new String[] {"targetString", "targetIndex", "targetLength", "sourceString", "sourceIndex", "sourceLength"}, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
		                                                                                                                                                                                                              new String[] {vagText, IEGLConstants.INT_STRING, IEGLConstants.INT_STRING, vagText, IEGLConstants.INT_STRING, IEGLConstants.INT_STRING},
																																																					  new int[] {IN, IN, IN, IN, IN, IN}, new int[] { 6 }));
		strLibWords.put(IEGLConstants.SYSTEM_WORD_CONCATENATE, new EGLSystemFunctionWord("concatenate", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Concatenate, stringLibrary, IEGLConstants.BIN_STRING, 9, new String[] {"targetString", "sourceString"}, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		                                                                                                                                                                                                             new String[] {vagText, vagText},
																																																					 new int[] {INOUT, IN}, new int[] { 2 }));
		strLibWords.put(IEGLConstants.SYSTEM_WORD_CONCATENATEWITHSEPARATOR, new EGLSystemFunctionWord("concatenateWithSeparator", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_ConcatenateWithSeparator, stringLibrary, IEGLConstants.BIN_STRING, 9, new String[] {"targetString", "sourceString", "separatorString"}, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		                                                                                                                                                                                                                                                    new String[] { vagText, vagText, vagText},
																																																															new int[] {INOUT, IN, IN}, new int[] { 3 }));
		strLibWords.put(IEGLConstants.SYSTEM_WORD_COPYSTR, new EGLSystemFunctionWord("copyStr", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_CopyString, stringLibrary, null, 0, new String[] {"targetString", "targetIndex", "targetLength", "sourceString", "sourceIndex", "sourceLength"}, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
		                                                                                                                                                                             new String[] {vagText, IEGLConstants.INT_STRING, IEGLConstants.INT_STRING, vagText, IEGLConstants.INT_STRING, IEGLConstants.INT_STRING},
																																													 new int[] {INOUT, IN, IN, IN, IN, IN}, new int[] { 6 }));
		strLibWords.put(IEGLConstants.SYSTEM_WORD_FINDSTR, new EGLSystemFunctionWord("findStr", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_FindString, stringLibrary, IEGLConstants.BIN_STRING, 9, new String[] {"sourceString", "sourceIndex", "sourceLength", "searchString"}, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		                                                                                                                                                                                                    new String[] {vagText, IEGLConstants.INT_STRING, IEGLConstants.INT_STRING, vagText},
																																																			new int[] {IN, INOUT, IN, IN}, new int[] { 4 }));
		strLibWords.put(IEGLConstants.SYSTEM_WORD_GETNEXTTOKEN, new EGLSystemFunctionWord("getNextToken", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_GetNextToken, stringLibrary, IEGLConstants.BIN_STRING, 9, new String[] {"targetString", "sourceString", "sourceIndex", "sourceLength", "characterDelimiters"}, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		                                                                                                                                                                                                                new String[] {vagText, vagText, IEGLConstants.INT_STRING, IEGLConstants.INT_STRING, vagText},
																																																						new int[] {INOUT, IN, INOUT, INOUT, IN}, new int[] { 5 }));
		strLibWords.put(IEGLConstants.SYSTEM_WORD_SETBLANKTERMINATOR, new EGLSystemFunctionWord("setBlankTerminator", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_SetBlankTerminator, stringLibrary, null, 0, new String[] {"targetString"}, new String[] { vagText }, new int[] {INOUT}, new int[] { 1 } )); //$NON-NLS-1$ //$NON-NLS-2$
		strLibWords.put(IEGLConstants.SYSTEM_WORD_SETNULLTERMINATOR, new EGLSystemFunctionWord("setNullTerminator", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_SetNullTerminator, stringLibrary, null, 0, new String[] {"targetString"}, new String[] { vagText }, new int[] {INOUT}, new int[] { 1 } )); //$NON-NLS-1$ //$NON-NLS-2$
		strLibWords.put(IEGLConstants.SYSTEM_WORD_SETSUBSTR, new EGLSystemFunctionWord("setSubStr", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_SetSubString, stringLibrary, null, 0, new String[] {"targetString", "targetIndex", "targetLength", "sourceCharacter"}, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		                                                                                                                                                                                   new String[] {vagText, IEGLConstants.INT_STRING, IEGLConstants.INT_STRING, vagText},
																																														   new int[] {INOUT, IN, IN, IN}, new int[] { 4 }));
		strLibWords.put(IEGLConstants.SYSTEM_WORD_STRLEN, new EGLSystemFunctionWord("strLen", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_StringLength, stringLibrary, IEGLConstants.BIN_STRING, 9, new String[] {"sourceString"}, new String[] {vagText}, new int[] {IN}, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$
		strLibWords.put(IEGLConstants.SYSTEM_WORD_TEXTLEN, new EGLSystemFunctionWord("textLen", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_TextLen, stringLibrary, IEGLConstants.INT_STRING, 0, new String[] {"aString"}, new String[] {IEGLConstants.STRING_STRING }, new int[] {IN}, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$
		strLibWords.put(IEGLConstants.SYSTEM_WORD_CHARACTERASINT, new EGLSystemFunctionWord("characterAsInt", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_CharacterAsInteger, stringLibrary, IEGLConstants.INT_STRING, 0, new String[] {"characterExpression"}, new String[] {IEGLConstants.STRING_STRING}, new int[] {IN}, new int[] { 1 } ));	//$NON-NLS-1$ //$NON-NLS-2$	
		strLibWords.put(IEGLConstants.SYSTEM_WORD_CLIP, new EGLSystemFunctionWord("clip", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Clip, stringLibrary, IEGLConstants.STRING_STRING, 0, new String[] {"text"}, new String[] {IEGLConstants.STRING_STRING }, new int[] {IN}, new int[] { 1 }));		
		strLibWords.put(IEGLConstants.SYSTEM_WORD_FORMATNUMBER, new EGLSystemFunctionWord("formatNumber", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_FormatNum, stringLibrary, IEGLConstants.STRING_STRING, 0, new String[] {"numericExpression","numericFormat"}, new String[] {IEGLConstants.NUMBER_STRING, IEGLConstants.STRING_STRING }, new int[] {IN, IN}, new int[] { 1, 2 }));
		strLibWords.put(IEGLConstants.SYSTEM_WORD_INTEGERASCHAR, new EGLSystemFunctionWord("integerAsChar", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_IntegerAsCharacter, stringLibrary, IEGLConstants.STRING_STRING, 0, new String[] {"integerExpression"}, new String[] {IEGLConstants.INT_STRING }, new int[] {IN}, new int[] { 1 }));
		strLibWords.put(IEGLConstants.SYSTEM_WORD_LOWERCASE, new EGLSystemFunctionWord("lowerCase", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_LowerCase, stringLibrary, IEGLConstants.STRING_STRING, 0, new String[] {"characterItem"}, new String[] {IEGLConstants.STRING_STRING }, new int[] {IN}, new int[] { 1 }));
		strLibWords.put(IEGLConstants.SYSTEM_WORD_SPACES, new EGLSystemFunctionWord("spaces", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Spaces, stringLibrary, IEGLConstants.STRING_STRING, 0, new String[] {"characterCount"}, new String[] {IEGLConstants.INT_STRING }, new int[] {IN}, new int[] { 1 }));
		strLibWords.put(IEGLConstants.SYSTEM_WORD_UPPERCASE, new EGLSystemFunctionWord("upperCase", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_UpperCase, stringLibrary, IEGLConstants.STRING_STRING, 0, new String[] {"characterItem"}, new String[] { IEGLConstants.STRING_STRING }, new int[] {IN}, new int[] { 1 }));		
		strLibWords.put(IEGLConstants.SYSTEM_WORD_FORMATDATE, new EGLSystemFunctionWord("formatDate", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_FormatDate, stringLibrary, IEGLConstants.STRING_STRING, 0, new String[] {"inputItem", "characterDateFormat"}, new String[] {IEGLConstants.DATE_STRING, IEGLConstants.STRING_STRING}, new int[] {IN, IN}, new int[] { 1, 2 }));
		strLibWords.put(IEGLConstants.SYSTEM_WORD_FORMATTIME, new EGLSystemFunctionWord("formatTime", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_FormatTime, stringLibrary, IEGLConstants.STRING_STRING, 0, new String[] {"inputItem", "characterTimeFormat"}, new String[] {IEGLConstants.TIME_STRING, IEGLConstants.STRING_STRING}, new int[]  {IN, IN}, new int[] { 1, 2 }));
		strLibWords.put(IEGLConstants.SYSTEM_WORD_FORMATTIMESTAMP, new EGLSystemFunctionWord("formatTimeStamp", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_FormatTimeStamp, stringLibrary, IEGLConstants.STRING_STRING, 0, new String[] {"inputItem", "timeStampFormat"}, new String[] {IEGLConstants.TIMESTAMP_F6_STRING, IEGLConstants.STRING_STRING}, new int[] {IN, IN}, new int[] { 1, 2 }));
		//Date and Time format variables
		strLibWords.put(IEGLConstants.SYSTEM_WORD_ISODATEFORMAT, new EGLSystemVariable("isoDateFormat", SYSTEM_WORD_CHARACTER | SYSTEM_WORD_READ_ONLY, IEGLConstants.Special_Function_ISODATEFORMAT, IEGLConstants.CHAR_STRING, 10, stringLibrary)); //$NON-NLS-1$
		strLibWords.put(IEGLConstants.SYSTEM_WORD_USADATEFORMAT, new EGLSystemVariable("usaDateFormat", SYSTEM_WORD_CHARACTER | SYSTEM_WORD_READ_ONLY, IEGLConstants.Special_Function_USADATEFORMAT, IEGLConstants.CHAR_STRING, 10, stringLibrary)); //$NON-NLS-1$
		strLibWords.put(IEGLConstants.SYSTEM_WORD_EURDATEFORMAT, new EGLSystemVariable("eurDateFormat", SYSTEM_WORD_CHARACTER | SYSTEM_WORD_READ_ONLY, IEGLConstants.Special_Function_EURDATEFORMAT, IEGLConstants.CHAR_STRING, 10, stringLibrary)); //$NON-NLS-1$
		strLibWords.put(IEGLConstants.SYSTEM_WORD_JISDATEFORMAT, new EGLSystemVariable("jisDateFormat", SYSTEM_WORD_CHARACTER | SYSTEM_WORD_READ_ONLY, IEGLConstants.Special_Function_JISDATEFORMAT, IEGLConstants.CHAR_STRING, 10, stringLibrary)); //$NON-NLS-1$
		strLibWords.put(IEGLConstants.SYSTEM_WORD_ISOTIMEFORMAT, new EGLSystemVariable("isoTimeFormat", SYSTEM_WORD_CHARACTER | SYSTEM_WORD_READ_ONLY, IEGLConstants.Special_Function_ISOTIMEFORMAT, IEGLConstants.CHAR_STRING, 8, stringLibrary)); //$NON-NLS-1$
		strLibWords.put(IEGLConstants.SYSTEM_WORD_USATIMEFORMAT, new EGLSystemVariable("usaTimeFormat", SYSTEM_WORD_CHARACTER | SYSTEM_WORD_READ_ONLY, IEGLConstants.Special_Function_USATIMEFORMAT, IEGLConstants.CHAR_STRING, 8, stringLibrary)); //$NON-NLS-1$
		strLibWords.put(IEGLConstants.SYSTEM_WORD_EURTIMEFORMAT, new EGLSystemVariable("eurTimeFormat", SYSTEM_WORD_CHARACTER | SYSTEM_WORD_READ_ONLY, IEGLConstants.Special_Function_EURTIMEFORMAT, IEGLConstants.CHAR_STRING, 8, stringLibrary)); //$NON-NLS-1$
		strLibWords.put(IEGLConstants.SYSTEM_WORD_JISTIMEFORMAT, new EGLSystemVariable("jisTimeFormat", SYSTEM_WORD_CHARACTER | SYSTEM_WORD_READ_ONLY, IEGLConstants.Special_Function_JISTIMEFORMAT, IEGLConstants.CHAR_STRING, 8, stringLibrary)); //$NON-NLS-1$
		strLibWords.put(IEGLConstants.SYSTEM_WORD_DB2TIMESTAMPFORMAT, new EGLSystemVariable("db2TimeStampFormat", SYSTEM_WORD_CHARACTER | SYSTEM_WORD_READ_ONLY, IEGLConstants.Special_Function_DB2TIMESTAMPFORMAT, IEGLConstants.CHAR_STRING, 26, stringLibrary)); //$NON-NLS-1$
		strLibWords.put(IEGLConstants.SYSTEM_WORD_ODBCTIMESTAMPFORMAT, new EGLSystemVariable("odbcTimeStampFormat", SYSTEM_WORD_CHARACTER | SYSTEM_WORD_READ_ONLY, IEGLConstants.Special_Function_ODBCTIMESTAMPFORMAT, IEGLConstants.CHAR_STRING, 26, stringLibrary)); //$NON-NLS-1$

		strLibWords.put(IEGLConstants.SYSTEM_WORD_DEFAULTDATEFORMAT, new EGLSystemVariable("defaultDateFormat", SYSTEM_WORD_CHARACTER, IEGLConstants.Special_Function_DefaultDateFormat, IEGLConstants.STRING_STRING, 0, stringLibrary)); //$NON-NLS-1$
		strLibWords.put(IEGLConstants.SYSTEM_WORD_DEFAULTTIMEFORMAT, new EGLSystemVariable("defaultTimeFormat", SYSTEM_WORD_CHARACTER, IEGLConstants.Special_Function_DefaultTimeFormat, IEGLConstants.STRING_STRING, 0, stringLibrary)); //$NON-NLS-1$
		strLibWords.put(IEGLConstants.SYSTEM_WORD_DEFAULTTIMESTAMPFORMAT, new EGLSystemVariable("defaultTimeStampFormat", SYSTEM_WORD_CHARACTER, IEGLConstants.Special_Function_DefaultTimeStampFormat, IEGLConstants.STRING_STRING, 0, stringLibrary)); //$NON-NLS-1$
		strLibWords.put(IEGLConstants.SYSTEM_WORD_DEFAULTMONEYFORMAT, new EGLSystemVariable("defaultMoneyFormat", SYSTEM_WORD_CHARACTER, IEGLConstants.Special_Function_DefaultMoneyFormat, IEGLConstants.STRING_STRING, 0, stringLibrary)); //$NON-NLS-1$
		strLibWords.put(IEGLConstants.SYSTEM_WORD_DEFAULTNUMERICFORMAT, new EGLSystemVariable("defaultNumericFormat", SYSTEM_WORD_CHARACTER, IEGLConstants.Special_Function_DefaultNumericFormat, IEGLConstants.STRING_STRING, 0, stringLibrary)); //$NON-NLS-1$
		
		//Dynamic Array Functions & Variables
		//these can not be standalone.  must be after an array.
		dynArrayWords.put(IEGLConstants.SYSTEM_WORD_APPENDELEMENT, new EGLSystemFunctionWord("appendElement", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_AppendElement, dynamicArraySystemWord, null, 0, new String[] {"appendElement"}, new String[] {arrayElement}, new int[] {IN}, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		dynArrayWords.put(IEGLConstants.SYSTEM_WORD_APPENDALL, new EGLSystemFunctionWord("appendAll", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_AppendAll, dynamicArraySystemWord, null, 0, new String[] {"array"}, new String[] {arrayElement+"[]"}, new int[] {IN}, new int[] { 1 } )); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		dynArrayWords.put(IEGLConstants.SYSTEM_WORD_INSERTELEMENT, new EGLSystemFunctionWord("insertElement", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_InsertElement, dynamicArraySystemWord, null, 0, new String[] {"insertElement", "arrayIndex"}, new String[] {arrayElement, IEGLConstants.INT_STRING}, new int[] {IN, IN}, new int[] { 2 })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		dynArrayWords.put(IEGLConstants.SYSTEM_WORD_REMOVEELEMENT, new EGLSystemFunctionWord("removeElement", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_RemoveElement, dynamicArraySystemWord, null, 0, new String[] {"arrayIndex"}, new String[] {IEGLConstants.INT_STRING}, new int[] {IN}, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$
		dynArrayWords.put(IEGLConstants.SYSTEM_WORD_REMOVEALL, new EGLSystemFunctionWord("removeAll", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_RemoveAll, dynamicArraySystemWord, null, 0, new String[0], new String[0], new int[0], new int[] { 0 })); //$NON-NLS-1$
		dynArrayWords.put(IEGLConstants.SYSTEM_WORD_RESIZE, new EGLSystemFunctionWord("resize", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_Resize, dynamicArraySystemWord, null, 0, new String[] {"newDimension"}, new String[] {IEGLConstants.INT_STRING}, new int[] {IN}, new int[] { 1 })); //$NON-NLS-1$
		dynArrayWords.put(IEGLConstants.SYSTEM_WORD_RESIZEALL, new EGLSystemFunctionWord("resizeAll", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_ResizeAll, dynamicArraySystemWord, null, 0, new String[] {"newDimensions"}, new String[] {IEGLConstants.INT_STRING + "[]"}, new int[] {IN}, new int[] { 1 })); //$NON-NLS-1$

		dynArrayWords.put(IEGLConstants.SYSTEM_WORD_SETMAXSIZE, new EGLSystemFunctionWord("setMaxSize", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_SetMaxSize, dynamicArraySystemWord, null, 0, new String[] {"maxSize"}, new String[] {IEGLConstants.INT_STRING}, new int[] {IN}, new int[] { 1 })); //$NON-NLS-1$
		dynArrayWords.put(IEGLConstants.SYSTEM_WORD_SETMAXSIZES, new EGLSystemFunctionWord("setMaxSizes", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_SetMaxSizes, dynamicArraySystemWord, null, 0, new String[] {"maxSizes"}, new String[] {IEGLConstants.INT_STRING + "[]"}, new int[] {IN}, new int[] { 1 })); //$NON-NLS-1$
		dynArrayWords.put(IEGLConstants.SYSTEM_WORD_GETSIZE, new EGLSystemFunctionWord("getSize", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_GetSize, dynamicArraySystemWord, IEGLConstants.BIN_STRING, 9, new String[0], new String[0], new int[0], new int[] { 0 })); //$NON-NLS-1$
		dynArrayWords.put(IEGLConstants.SYSTEM_WORD_GETMAXSIZE, new EGLSystemFunctionWord("getMaxSize", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_GetMaxSize, dynamicArraySystemWord, IEGLConstants.BIN_STRING, 9, new String[0], new String[0], new int[0], new int[] { 0 })); //$NON-NLS-1$
		dynArrayWords.put(IEGLConstants.SYSTEM_WORD_SETELEMENTSEMPTY, new EGLSystemFunctionWord("setElementsEmpty", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_SetElementsEmpty, dynamicArraySystemWord, null, 0, new String[0], new String[0], new int[0], new int[] { 0 })); //$NON-NLS-1$

		
		//Dictionary Functions & Variables
		//these can not be standalone.  must be after a dictionary.
		dictionaryWords.put(IEGLConstants.SYSTEM_WORD_CONTAINSKEY, new EGLSystemFunctionWord("containsKey", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_ContainsKey, dictionarySystemWord, IEGLConstants.BOOLEAN_STRING, 0, new String[] {"key"}, new String[] {IEGLConstants.STRING_STRING}, new int[] {IN}, new int[] {1} )); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		dictionaryWords.put(IEGLConstants.SYSTEM_WORD_GETKEYS, new EGLSystemFunctionWord("getKeys", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_GetKeys, dictionarySystemWord, IEGLConstants.STRING_STRING + "[]", 0, new String[0], new String[0], new int[0], new int[] {0})); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		dictionaryWords.put(IEGLConstants.SYSTEM_WORD_GETVALUES, new EGLSystemFunctionWord("getValues", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_GetValues, dictionarySystemWord, IEGLConstants.ANY_STRING + "[]", 0, new String[0], new String[0], new int[0], new int[] {0})); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		dictionaryWords.put(IEGLConstants.SYSTEM_WORD_INSERTALL, new EGLSystemFunctionWord("insertAll", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_InsertAll, dictionarySystemWord, null, 0, new String[] {"dictionary"}, new String[] {IEGLConstants.DICTIONARY_STRING}, new int[] {IN}, new int[] {1})); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		dictionaryWords.put(IEGLConstants.SYSTEM_WORD_REMOVEELEMENT, new EGLSystemFunctionWord("removeElement", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_Dictionary_RemoveElement, dictionarySystemWord, null, 0, new String[] {"key"}, new String[] {IEGLConstants.STRING_STRING}, new int[] {IN}, new int[] {1})); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		dictionaryWords.put(IEGLConstants.SYSTEM_WORD_REMOVEALL, new EGLSystemFunctionWord("removeAll", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_Dictionary_RemoveAll, dictionarySystemWord, null, 0, new String[0], new String[0], new int[0], new int[] {0})); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		dictionaryWords.put(IEGLConstants.SYSTEM_WORD_SIZE, new EGLSystemFunctionWord("size", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Dictionary_Size, dictionarySystemWord, IEGLConstants.BIN_STRING, 9, new String[0], new String[0], new int[0], new int[] {0})); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
 		
		//sql functions
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_COMMIT, new EGLSystemFunctionWord("commit", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_Commit, systemLibrary, null, 0, new String[0], new String[0], new int[0], new int[] { 0 })); //$NON-NLS-1$
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_CONNECT, new EGLSystemFunctionWord("connect", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_Connect, systemLibrary, null, 0, new String[] {"database", "userid", "password", "commitScope", "disconnectOption", "isolationLevel", "commitControl"}, new String[] {IEGLConstants.STRING_STRING, IEGLConstants.STRING_STRING, IEGLConstants.STRING_STRING, mnemonic, mnemonic, mnemonic, mnemonic}, new int[] {IN, IN, IN, IN, IN, IN, IN}, new int[] {3, 4, 5, 6, 7})); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_DISCONNECT, new EGLSystemFunctionWord("disconnect", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_Disconnect, systemLibrary, null, 0, new String[] {"database"}, new String[] {IEGLConstants.STRING_STRING}, new int[] {IN}, new int[] { 0, 1 })); //$NON-NLS-1$ //$NON-NLS-2$
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_DISCONNECTALL, new EGLSystemFunctionWord("disconnectAll", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_DisconnectAll, systemLibrary, null, 0, new String[0], new String[0], new int[0], new int[] { 0 })); //$NON-NLS-1$
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_ROLLBACK, new EGLSystemFunctionWord("rollback", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_Rollback, systemLibrary, null, 0, new String[0], new String[0], new int[0], new int[] { 0 })); //$NON-NLS-1$
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_QUERYCURRENTDATABASE, new EGLSystemFunctionWord("queryCurrentDatabase", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_QueryCurrentDatabase, systemLibrary, null, 0, new String[] {"product", "release"}, new String[] {IEGLConstants.CHAR8_STRING, IEGLConstants.CHAR8_STRING}, new int[] {IN, IN}, new int[] { 2 })); //$NON-NLS-1$ //$NON-NLS-2$
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_SETCURRENTDATABASE, new EGLSystemFunctionWord("setCurrentDatabase", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_SetCurrentDatabase, systemLibrary, null, 0, new String[] {"database"}, new String[] {IEGLConstants.STRING_STRING}, new int[] {IN}, new int[] { 1 })); //$NON-NLS-1$ 
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_BEGINDATABASETRANSACTION, new EGLSystemFunctionWord("beginDatabaseTransaction", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_BeginDatabaseTransaction, systemLibrary, null, 0, new String[] {"databaseName"}, new String[] {IEGLConstants.STRING_STRING}, new int[] {IN}, new int[] {0, 1} )); //$NON-NLS-1$
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_DEFINEDATABASEALIAS, new EGLSystemFunctionWord("defineDatabaseAlias", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_DefineDatabaseAlias, systemLibrary, null, 0, new String[] {"alias", "databaseName"}, new String[] {IEGLConstants.STRING_STRING,IEGLConstants.STRING_STRING}, new int[] {IN, IN}, new int[] { 2 } )); //$NON-NLS-1$

		//miscellaneous functions
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_AUDIT, new EGLSystemFunctionWord("audit", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_Audit, systemLibrary, null, 0, new String[] {"record", "jid"}, new String[] {"record", IEGLConstants.SMALLINT_STRING}, new int[] {IN, IN}, new int[] { 1, 2 } )); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_BYTES, new EGLSystemFunctionWord("bytes", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Bytes, systemLibrary, IEGLConstants.BIN_STRING, 9, new String[] {"itemOrRecord"}, new String[] {itemOrRecord}, new int[] {IN}, new int[] { 1 } )); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_CALCULATECHKDIGITMOD10, new EGLSystemFunctionWord("calculateChkDigitMod10", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_CalculateChkDigitMod10, systemLibrary, null, 0, new String[] {"input", "length", "result"}, new String[] {IEGLConstants.CHAR_STRING, IEGLConstants.INT_STRING, IEGLConstants.SMALLINT_STRING}, new int[] {INOUT, IN, INOUT}, new int[] { 3 })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_CALCULATECHKDIGITMOD11, new EGLSystemFunctionWord("calculateChkDigitMod11", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_CalculateChkDigitMod11, systemLibrary, null, 0, new String[] {"input", "length", "result"}, new String[] {IEGLConstants.CHAR_STRING, IEGLConstants.INT_STRING, IEGLConstants.SMALLINT_STRING}, new int[] {INOUT, IN, INOUT}, new int[] { 3 })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_CONVERT, new EGLSystemFunctionWord("convert", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_Convert, systemLibrary, null, 0, new String[] {"target", "direction", "conversionTable"}, new String[] {identifier, IEGLConstants.STRING_STRING, IEGLConstants.CHAR_STRING}, new int[] {INOUT, IN, IN}, new int[] { 1, 2, 3 })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_PURGE, new EGLSystemFunctionWord("purge", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_Purge, systemLibrary, null, 0, new String[] {"queueName"}, new String[] {IEGLConstants.CHAR_STRING}, new int[] {IN}, new int[] { 1 })); //$NON-NLS-1$
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_SETERROR, new EGLSystemFunctionWord("setError", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_SetError, systemLibrary, null, 0, new String[] {"messageText", "messageKey","inserts"}, new String[] {IEGLConstants.CHAR_STRING, IEGLConstants.STRING_STRING, IEGLConstants.STRING_STRING}, new int[] {IN, IN, IN}, new int[] { ARG_COUNT_N_OR_MORE, 1 }));	//has 2 formats //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_SETLOCALE, new EGLSystemFunctionWord("setLocale", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_SetLocale, systemLibrary, null, 0, new String[] {"languageCode", "countryCode","variant"}, new String[] {IEGLConstants.CHAR2_STRING, IEGLConstants.CHAR2_STRING, IEGLConstants.CHAR2_STRING}, new int[] {IN, IN, IN}, new int[] { 2, 3 })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_SETREMOTEUSER, new EGLSystemFunctionWord("setRemoteUser", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_SetRemoteUser, systemLibrary, null, 0, new String[] {"userid", "password"}, new String[] {IEGLConstants.STRING_STRING, IEGLConstants.STRING_STRING}, new int[] {IN, IN}, new int[] { 2 })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_STARTTRANSACTION, new EGLSystemFunctionWord("startTransaction", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_StartTransaction, systemLibrary, null, 0, new String[] {"request", "prid", "termid"}, new String[] {"record",anyEglPrimitive, IEGLConstants.CHAR_STRING}, new int[] {IN, IN, IN}, new int[] { 1, 2, 3 })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_VERIFYCHKDIGITMOD10, new EGLSystemFunctionWord("verifyChkDigitMod10", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_VerifyChkDigitMod10, systemLibrary, null, 0, new String[] {"input", "length", "result"}, new String[] {IEGLConstants.CHAR_STRING, IEGLConstants.INT_STRING, IEGLConstants.SMALLINT_STRING}, new int[] {INOUT, IN, INOUT}, new int[] { 3 })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_VERIFYCHKDIGITMOD11, new EGLSystemFunctionWord("verifyChkDigitMod11", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_VerifyChkDigitMod11, systemLibrary, null, 0, new String[] {"input", "length", "result"}, new String[] {IEGLConstants.CHAR_STRING, IEGLConstants.INT_STRING, IEGLConstants.SMALLINT_STRING}, new int[] {INOUT, IN, INOUT}, new int[] { 3 })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_WAIT, new EGLSystemFunctionWord("wait", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_Wait, systemLibrary, null, 0, new String[] {"seconds"}, new String[] {IEGLConstants.BIN9_2_STRING}, new int[] {IN}, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$

		sysLibWords.put(IEGLConstants.SYSTEM_WORD_MAXIMUMSIZE, new EGLSystemFunctionWord("maximumSize", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_MaximumSize, systemLibrary, IEGLConstants.BIN_STRING, 9, new String[] {"dynamicArray"}, new String[] {IEGLConstants.DYNAMIC_ARRAY}, new int[] {IN}, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_SIZE, new EGLSystemFunctionWord("size", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Size, systemLibrary, IEGLConstants.BIN_STRING, 9, new String[] {"arrayOrTable"}, new String[] {IEGLConstants.ARRAY_OR_TABLE}, new int[] {IN}, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$

		sysLibWords.put(IEGLConstants.SYSTEM_WORD_CALLCMD, new EGLSystemFunctionWord("callCmd", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_CallCmd, systemLibrary, null, 0, new String[] {"commandString", "modeString"}, new String[] { IEGLConstants.STRING_STRING, IEGLConstants.STRING_STRING }, new int[] {IN, IN}, new int[] { 1, 2 }));
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_GETCMDLINEARGCOUNT, new EGLSystemFunctionWord("getCmdLineArgCount", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_GetCmdLineArgCount, systemLibrary, IEGLConstants.INT_STRING, 0, new String[] {}, new String[] { }, new int[0], new int[] { 0 }));
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_GETCMDLINEARG, new EGLSystemFunctionWord("getCmdLineArg", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_GetCmdLineArg, systemLibrary, IEGLConstants.STRING_STRING, 0, new String[] {"index"}, new String[] { IEGLConstants.INT_STRING }, new int[] {IN}, new int[] { 1 }));
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_STARTCMD, new EGLSystemFunctionWord("startCmd", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_StartCmd, systemLibrary, null, 0, new String[] {"commandString", "modeString"}, new String[] {IEGLConstants.STRING_STRING, IEGLConstants.STRING_STRING }, new int[] {IN, IN}, new int[] { 1, 2 }));
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_LOADTABLE, new EGLSystemFunctionWord("loadTable", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_LoadTable, systemLibrary, null, 0, new String[] {"filename","insertIntoClause","delimiter"}, new String[] { IEGLConstants.STRING_STRING, IEGLConstants.STRING_STRING, IEGLConstants.STRING_STRING}, new int[] {IN, IN, IN}, new int[] { 2, 3 }));
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_UNLOADTABLE, new EGLSystemFunctionWord("unloadTable", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_UnloadTable, systemLibrary, null, 0, new String[] {"filename","selectStatement","delimiter"}, new String[] { IEGLConstants.STRING_STRING, IEGLConstants.STRING_STRING, IEGLConstants.STRING_STRING}, new int[] {IN, IN, IN}, new int[] { 2, 3 }));

		sysLibWords.put(IEGLConstants.SYSTEM_WORD_GETPROPERTY, new EGLSystemFunctionWord("getProperty", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_GetProperty, systemLibrary, IEGLConstants.STRING_STRING, 0, new String[] {"textVariableOrLiteral "}, new String[] {IEGLConstants.STRING_STRING}, new int[] {IN}, new int[] { 1 }));
		
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_WRITESTDOUT, new EGLSystemFunctionWord("writeStdout", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_WriteStdout, systemLibrary, null, 0, new String[] {"text"}, new String[] {IEGLConstants.STRING_STRING}, new int[] {IN}, new int[] { 1 }));
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_WRITESTDERR, new EGLSystemFunctionWord("writeStderr", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_WriteStderr, systemLibrary, null, 0, new String[] {"text"}, new String[] {IEGLConstants.STRING_STRING}, new int[] {IN}, new int[] { 1 }));

		// Add currentException variable to sysLib
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_CURRENTEXCEPTION, new EGLSystemVariable("currentException", SYSTEM_WORD_DICTIONARY, IEGLConstants.Special_Function_CurrentException, IEGLConstants.DICTIONARY_STRING, 0, IEGLConstants.KEYWORD_SYSLIB.toLowerCase())); //$NON-NLS-1$

		sysLibWords.put(IEGLConstants.SYSTEM_WORD_GETMESSAGE, new EGLSystemFunctionWord("getMessage", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_GetMessage, systemLibrary, IEGLConstants.STRING_STRING, 0, new String[] {"key", "inserts"}, new String[] { IEGLConstants.STRING_STRING, IEGLConstants.STRING_STRING + "[]" }, new int[] { IN, IN }, new int[] {1, 2} ));

		sysLibWords.put(IEGLConstants.SYSTEM_WORD_STARTLOG, new EGLSystemFunctionWord("startLog", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_StartLog, systemLibrary, null, 0, new String[] {"filename"}, new String[] {IEGLConstants.STRING_STRING}, new int[] {IN}, new int[] { 1 }));
		sysLibWords.put(IEGLConstants.SYSTEM_WORD_ERRORLOG, new EGLSystemFunctionWord("errorLog", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_ErrorLog, systemLibrary, null, 0, new String[] {"errorMessage"}, new String[] {IEGLConstants.STRING_STRING}, new int[] {IN}, new int[] { 1 }));

		sysLibWords.put(IEGLConstants.SYSTEM_WORD_CONDITIONASINT, new EGLSystemFunctionWord("conditionAsInt", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_ConditionAsInt, systemLibrary, IEGLConstants.SMALLINT_STRING, 4, new String[] {"errorMessage"}, new String[] {IEGLConstants.BOOLEAN_STRING}, new int[] {IN}, new int[] { 1 }));
		
		// System Variables
		sysVarWords.put(IEGLConstants.SYSTEM_WORD_ARRAYINDEX, new EGLSystemVariable("arrayIndex", SYSTEM_WORD_NUMERIC, IEGLConstants.Special_Function_ArrayIndex, IEGLConstants.BIN_STRING, 9)); //$NON-NLS-1$
		
		sysVarWords.put(IEGLConstants.SYSTEM_WORD_CALLCONVERSIONTABLE, new EGLSystemVariable("callConversionTable", SYSTEM_WORD_CHARACTER, IEGLConstants.Special_Function_CallConversionTable, IEGLConstants.CHAR_STRING, 8)); //$NON-NLS-1$
	
		sysVarWords.put(IEGLConstants.SYSTEM_WORD_SESSIONID, new EGLSystemVariable("sessionId", SYSTEM_WORD_CHARACTER | SYSTEM_WORD_READ_ONLY, IEGLConstants.Special_Function_ConnectionID, IEGLConstants.CHAR_STRING, 8)); //$NON-NLS-1$
//		???			systemWords.put(IEGLConstants.SYSTEM_WORD_CONVERSIONTABLE, new EGLSystemVariable("conversionTable", SYSTEM_WORD_CHARACTER, IEGLConstants.Special_Function_ConversionTable, IEGLConstants.CHAR_STRING, 8));
				
		sysVarWords.put(IEGLConstants.SYSTEM_WORD_ERRORCODE, new EGLSystemVariable("errorCode", SYSTEM_WORD_CHARACTER, IEGLConstants.Special_Function_ErrorCode, IEGLConstants.CHAR_STRING, 8)); //$NON-NLS-1$
	
		// Jeff 11-12
		// TODO Make sure these three have all the right settings (Not so sure about ExceptionMsg because it's an array, and ExceptionMsgCount because it's an int -> 32 bit int -> 4 bytes for length
//		systemWords.put(IEGLConstants.SYSTEM_WORD_EXCEPTIONCODE, new EGLSystemVariable("exceptionCode", SYSTEM_WORD_CHARACTER, IEGLConstants.Special_Function_ExceptionCode, IEGLConstants.CHAR_STRING, 8)); //$NON-NLS-1$
//		systemWords.put(IEGLConstants.SYSTEM_WORD_EXCEPTIONMSG, new EGLSystemVariable("exceptionMsg", SYSTEM_WORD_CHARACTER, IEGLConstants.Special_Function_ExceptionMsg, IEGLConstants.MBCHAR_STRING, 255)); //$NON-NLS-1$
//		systemWords.put(IEGLConstants.SYSTEM_WORD_EXCEPTIONMSGCOUNT, new EGLSystemVariable("exceptionMsgCount", SYSTEM_WORD_NUMERIC, IEGLConstants.Special_Function_ExceptionMsgCount, IEGLConstants.INT_STRING, 4)); //$NON-NLS-1$
		
		sysVarWords.put(IEGLConstants.SYSTEM_WORD_FORMCONVERSIONTABLE, new EGLSystemVariable("formConversionTable", SYSTEM_WORD_CHARACTER, IEGLConstants.Special_Function_FormConversionTable, IEGLConstants.CHAR_STRING, 8)); //$NON-NLS-1$
 
		recordWords.put(IEGLConstants.SYSTEM_WORD_RESOURCEASSOCIATION, new EGLSystemVariable("resourceAssociation", SYSTEM_WORD_CHARACTER, IEGLConstants.Special_Function_SystemName, IEGLConstants.CHAR_STRING, 65, recordSystemFunction)); //$NON-NLS-1$
		
		// EGLTODO Does this have a primitive type and length?
//		sysVarWords.put(IEGLConstants.SYSTEM_WORD_HANDLEHARDDLIERRORS, new EGLSystemVariable("handleHardDliErrors", SYSTEM_WORD_NUMERIC, IEGLConstants.Special_Function_HandleHardDliErrors, IEGLConstants.BIN_STRING, 9)); //$NON-NLS-1$
	
		sysVarWords.put(IEGLConstants.SYSTEM_WORD_OVERFLOWINDICATOR, new EGLSystemVariable("overflowIndicator", SYSTEM_WORD_NUMERIC, IEGLConstants.Special_Function_OverflowIndicator, IEGLConstants.NUM_STRING, 1)); //$NON-NLS-1$
		
		sysVarWords.put(IEGLConstants.SYSTEM_WORD_REMOTESYSTEMID, new EGLSystemVariable("remoteSystemId", SYSTEM_WORD_CHARACTER, IEGLConstants.Special_Function_RemoteSystemID, IEGLConstants.CHAR_STRING, 8)); //$NON-NLS-1$
		sysVarWords.put(IEGLConstants.SYSTEM_WORD_RETURNCODE, new EGLSystemVariable("returnCode", SYSTEM_WORD_NUMERIC, IEGLConstants.Special_Function_ReturnCode, IEGLConstants.BIN_STRING, 9)); //$NON-NLS-1$
		
		sysVarWords.put(IEGLConstants.SYSTEM_WORD_CONVERSATIONID, new EGLSystemVariable("conversationId", SYSTEM_WORD_CHARACTER | SYSTEM_WORD_READ_ONLY,IEGLConstants.Special_Function_SessionID, IEGLConstants.CHAR_STRING, 8)); //$NON-NLS-1$
		
		// EGLTODO Does this have a primitive type and length?
		sysVarWords.put(IEGLConstants.SYSTEM_WORD_SQLCA, new EGLSystemVariable("sqlca", SYSTEM_WORD_NONE, IEGLConstants.Special_Function_SqlCa, IEGLConstants.HEX_STRING, 136)); //$NON-NLS-1$
		sysVarWords.put(IEGLConstants.SYSTEM_WORD_SQLCODE, new EGLSystemVariable("sqlcode", SYSTEM_WORD_NUMERIC, IEGLConstants.Special_Function_SqlCode, IEGLConstants.BIN_STRING, 9)); //$NON-NLS-1$
		sysVarWords.put(IEGLConstants.SYSTEM_WORD_SQLSTATE, new EGLSystemVariable("sqlState", SYSTEM_WORD_CHARACTER, IEGLConstants.Special_Function_SqlState, IEGLConstants.CHAR_STRING, 5)); //$NON-NLS-1$
		sysVarWords.put(IEGLConstants.SYSTEM_WORD_SYSTEMTYPE, new EGLSystemVariable("systemType", SYSTEM_WORD_CHARACTER | SYSTEM_WORD_READ_ONLY, IEGLConstants.Special_Function_SystemType, IEGLConstants.CHAR_STRING, 8)); //$NON-NLS-1$
		sysVarWords.put(IEGLConstants.SYSTEM_WORD_TERMINALID, new EGLSystemVariable("terminalId", SYSTEM_WORD_CHARACTER | SYSTEM_WORD_READ_ONLY, IEGLConstants.Special_Function_TerminalID, IEGLConstants.CHAR_STRING, 8)); //$NON-NLS-1$
		
		// EGLTODO Does this have a primitive type and length?
		sysVarWords.put(IEGLConstants.SYSTEM_WORD_TRANSACTIONID, new EGLSystemVariable("transactionId", SYSTEM_WORD_CHARACTER, IEGLConstants.Special_Function_TransactionID, IEGLConstants.CHAR_STRING, 50)); //$NON-NLS-1$
		
		sysVarWords.put(IEGLConstants.SYSTEM_WORD_TRANSFERNAME, new EGLSystemVariable("transferName", SYSTEM_WORD_CHARACTER, IEGLConstants.Special_Function_TransferName, IEGLConstants.CHAR_STRING, 8)); //$NON-NLS-1$
		sysVarWords.put(IEGLConstants.SYSTEM_WORD_USERID, new EGLSystemVariable("userId", SYSTEM_WORD_CHARACTER | SYSTEM_WORD_READ_ONLY, IEGLConstants.Special_Function_UserID, IEGLConstants.CHAR_STRING, 8)); //$NON-NLS-1$
		
		converseVarWords.put(IEGLConstants.SYSTEM_WORD_EVENTKEY, new EGLSystemVariable("eventKey", SYSTEM_WORD_CHARACTER | SYSTEM_WORD_READ_ONLY, IEGLConstants.Special_Function_EventKey, IEGLConstants.CHAR_STRING, 8, converseVariablesLibrary)); //$NON-NLS-1$
		// EGLTODO This is the old EZEDESTP, need to figure out what that is (prim type).
		converseVarWords.put(IEGLConstants.SYSTEM_WORD_PRINTERASSOCIATION, new EGLSystemVariable("printerAssociation", SYSTEM_WORD_CHARACTER, IEGLConstants.Special_Function_PrinterAssociation, IEGLConstants.CHAR_STRING, 50, converseVariablesLibrary)); //$NON-NLS-1$
		// EGLTODO Does this have a primitive type and length?
		converseVarWords.put(IEGLConstants.SYSTEM_WORD_SEGMENTEDMODE, new EGLSystemVariable("segmentedMode", SYSTEM_WORD_NUMERIC, IEGLConstants.Special_Function_SegmentedMode, IEGLConstants.BIN_STRING, 9, converseVariablesLibrary)); //$NON-NLS-1$
		// EGLTODO Does the next two have a primitive type and lengths?
		converseVarWords.put(IEGLConstants.SYSTEM_WORD_VALIDATIONMSGNUM, new EGLSystemVariable("validationMsgNum", SYSTEM_WORD_NUMERIC, IEGLConstants.Special_Function_ValidationMsgNum, IEGLConstants.BIN_STRING, 9, converseVariablesLibrary)); //$NON-NLS-1$
		// EGLTODO Does this have a prim type and length?
		converseVarWords.put(IEGLConstants.SYSTEM_WORD_COMMITONCONVERSE, new EGLSystemVariable("commitOnConverse", SYSTEM_WORD_NUMERIC, IEGLConstants.Special_Function_CommitOnConverse, IEGLConstants.NUM_STRING, 1, converseVariablesLibrary)); //$NON-NLS-1$
					
		//Date/Time Functions
		dateTimeLibWords.put(IEGLConstants.SYSTEM_WORD_CURRENTDATE, new EGLSystemFunctionWord("currentDate", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_CurrentDate, dateTimeLibrary, IEGLConstants.DATE_STRING, 8, new String[0], new String[0], new int[0], new int[] { 0 } ));
		dateTimeLibWords.put(IEGLConstants.SYSTEM_WORD_CURRENTTIME, new EGLSystemFunctionWord("currentTime", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_CurrentTime, dateTimeLibrary, IEGLConstants.TIME_STRING, 6, new String[0], new String[0], new int[0], new int[] { 0 }));
		dateTimeLibWords.put(IEGLConstants.SYSTEM_WORD_CURRENTTIMESTAMP, new EGLSystemFunctionWord("currentTimeStamp", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_CurrentTimeStamp, dateTimeLibrary, IEGLConstants.TIMESTAMP_F6_STRING, 20, new String[0], new String[0], new int[0], new int[] { 0 }));		
		dateTimeLibWords.put(IEGLConstants.SYSTEM_WORD_DATEVALUE, new EGLSystemFunctionWord("dateValue", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_DateValue, dateTimeLibrary, IEGLConstants.DATE_STRING, 8, new String[] {"characterExpression"}, new String[] {IEGLConstants.STRING_STRING}, new int[] {IN}, new int[] { 1 }));
		dateTimeLibWords.put(IEGLConstants.SYSTEM_WORD_DATEVALUEFROMGREGORIAN, new EGLSystemFunctionWord("dateValueFromGregorian", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_DateValueFromGregorian, dateTimeLibrary, IEGLConstants.DATE_STRING, 8, new String[] {"gregorianDateAsInteger"}, new String[] {IEGLConstants.INT_STRING}, new int[] {IN}, new int[] { 1 }));
		dateTimeLibWords.put(IEGLConstants.SYSTEM_WORD_DATEVALUEFROMJULIAN, new EGLSystemFunctionWord("dateValueFromJulian", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_DateValueFromJulian, dateTimeLibrary, IEGLConstants.DATE_STRING, 8, new String[] {"julianDateAsInteger"}, new String[] {IEGLConstants.INT_STRING}, new int[] {IN}, new int[] { 1 }));
		dateTimeLibWords.put(IEGLConstants.SYSTEM_WORD_TIMEVALUE, new EGLSystemFunctionWord("timeValue", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_TimeValue, dateTimeLibrary, IEGLConstants.TIME_STRING, 6, new String[] {"characterExpression"}, new String[] {IEGLConstants.STRING_STRING}, new int[] {IN}, new int[] { 1 }));
		dateTimeLibWords.put(IEGLConstants.SYSTEM_WORD_INTERVALVALUE, new EGLSystemFunctionWord("intervalValue", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_IntervalValue, dateTimeLibrary, IEGLConstants.INTERVAL_STRING, 28, new String[] {"characterExpression"}, new String[] {IEGLConstants.STRING_STRING}, new int[] {IN}, new int[] { 1 }));
		dateTimeLibWords.put(IEGLConstants.SYSTEM_WORD_INTERVALVALUEWITHPATTERN, new EGLSystemFunctionWord("intervalValueWithPattern", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_IntervalValueWithPattern, dateTimeLibrary, IEGLConstants.INTERVAL_STRING, 28, new String[] {"characterExpression", "intervalPattern"}, new String[] {IEGLConstants.STRING_STRING, IEGLConstants.STRING_STRING}, new int[] {IN, IN}, new int[] { 1, 2 }));
		dateTimeLibWords.put(IEGLConstants.SYSTEM_WORD_TIMESTAMPVALUE, new EGLSystemFunctionWord("timeStampValue", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_TimeStampValue, dateTimeLibrary, IEGLConstants.TIMESTAMP_F6_STRING, 20, new String[] {"characterExpression"}, new String[] {IEGLConstants.STRING_STRING}, new int[] {IN}, new int[] { 1 }));
		dateTimeLibWords.put(IEGLConstants.SYSTEM_WORD_TIMESTAMPVALUEWITHPATTERN, new EGLSystemFunctionWord("timeStampValueWithPattern", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_TimeStampValueWithPattern, dateTimeLibrary, IEGLConstants.TIMESTAMP_STRING, 20, new String[] {"characterExpression", "timeSpanPattern"}, new String[] {IEGLConstants.STRING_STRING, IEGLConstants.STRING_STRING}, new int[] {IN, IN}, new int[] { 1, 2 }));
		dateTimeLibWords.put(IEGLConstants.SYSTEM_WORD_TIMESTAMPFROM, new EGLSystemFunctionWord("timestampFrom", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_TimeStampFrom, dateTimeLibrary, IEGLConstants.TIMESTAMP_F6_STRING, 20, new String[] {"dateValue", "timeValue"}, new String[] {IEGLConstants.DATE_STRING, IEGLConstants.TIME_STRING}, new int[] {IN, IN}, new int[] { 2 }));
		dateTimeLibWords.put(IEGLConstants.SYSTEM_WORD_DAYOF, new EGLSystemFunctionWord("dayOf", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_DayOf, dateTimeLibrary, IEGLConstants.INT_STRING, 9, new String[] {"timestampValue"}, new String[] {IEGLConstants.TIMESTAMP_STRING}, new int[] {IN}, new int[] { 1 }));
		dateTimeLibWords.put(IEGLConstants.SYSTEM_WORD_MONTHOF, new EGLSystemFunctionWord("monthOf", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_MonthOf, dateTimeLibrary, IEGLConstants.INT_STRING, 9, new String[] {"timestampValue"}, new String[] {IEGLConstants.TIMESTAMP_STRING}, new int[] {IN}, new int[] { 1 }));
		dateTimeLibWords.put(IEGLConstants.SYSTEM_WORD_YEAROF, new EGLSystemFunctionWord("yearOf", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_YearOf, dateTimeLibrary, IEGLConstants.INT_STRING, 9, new String[] {"timestampValue"}, new String[] {IEGLConstants.TIMESTAMP_STRING}, new int[] {IN}, new int[] { 1 }));
		dateTimeLibWords.put(IEGLConstants.SYSTEM_WORD_WEEKDAYOF, new EGLSystemFunctionWord("weekdayOf", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_WeekdayOf, dateTimeLibrary, IEGLConstants.INT_STRING, 9, new String[] {"timestampValue"}, new String[] {IEGLConstants.TIMESTAMP_STRING}, new int[] {IN}, new int[] { 1 }));
		dateTimeLibWords.put(IEGLConstants.SYSTEM_WORD_MDY, new EGLSystemFunctionWord("mdy", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Mdy, dateTimeLibrary, IEGLConstants.DATE_STRING, 8, new String[] {"monthValue", "dayValue", "yearValue"}, new String[] {IEGLConstants.INT_STRING, IEGLConstants.INT_STRING, IEGLConstants.INT_STRING}, new int[] {IN, IN, IN}, new int[] { 3 }));
		dateTimeLibWords.put(IEGLConstants.SYSTEM_WORD_DATEOF, new EGLSystemFunctionWord("dateOf", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_DateOf, dateTimeLibrary, IEGLConstants.DATE_STRING, 8, new String[] {"timestampValue"}, new String[] {IEGLConstants.TIMESTAMP_STRING}, new int[] {IN}, new int[] { 1 }));
		dateTimeLibWords.put(IEGLConstants.SYSTEM_WORD_TIMEOF, new EGLSystemFunctionWord("timeOf", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_TimeOf, dateTimeLibrary, IEGLConstants.TIME_STRING, 6, new String[] {"timestampValue"}, new String[] {IEGLConstants.TIMESTAMP_F6_STRING}, new int[] {IN}, new int[] { 0, 1 }));
		dateTimeLibWords.put(IEGLConstants.SYSTEM_WORD_EXTEND, new EGLSystemFunctionWord("extend", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_Extend, dateTimeLibrary, IEGLConstants.TIMESTAMP_STRING, 20, new String[] {"extensionItem", "timeSpanPattern"}, new String[] {IEGLConstants.TIMESTAMP_F6_STRING, IEGLConstants.STRING_STRING}, new int[] {IN, IN}, new int[] { 1, 2 }));
		
		reportHandlerWords.put( IEGLConstants.SYSTEM_WORD_GETREPORTPARAMETER, new EGLSystemFunctionWord("getReportParameter", SYSTEM_WORD_REPORTHANDLER | SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_GetReportParameter, "", IEGLConstants.ANY_STRING, 0, new String[] {"parameter"}, new String[] {IEGLConstants.STRING_STRING}, new int[] {IN}, new int[] {1} ) ); //$NON-NLS-1$ //$NON-NLS-2$
		reportHandlerWords.put( IEGLConstants.SYSTEM_WORD_SETREPORTVARIABLEVALUE, new EGLSystemFunctionWord("setReportVariableValue", SYSTEM_WORD_REPORTHANDLER, IEGLConstants.Special_Function_SetReportVariableValue, "", null, 0, new String[] {"variable", "value"}, new String[] {IEGLConstants.STRING_STRING, IEGLConstants.ANY_STRING}, new int[] {IN, IN}, new int[] {2} ) ); //$NON-NLS-1$ //$NON-NLS-2$
		reportHandlerWords.put( IEGLConstants.SYSTEM_WORD_GETREPORTVARIABLEVALUE, new EGLSystemFunctionWord("getReportVariableValue", SYSTEM_WORD_REPORTHANDLER | SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_GetReportVariableValue, "", IEGLConstants.ANY_STRING, 0, new String[] {"variable"}, new String[] {IEGLConstants.STRING_STRING}, new int[] {IN}, new int[] {1} ) ); //$NON-NLS-1$ //$NON-NLS-2$
		reportHandlerWords.put( IEGLConstants.SYSTEM_WORD_GETFIELDVALUE, new EGLSystemFunctionWord("getFieldValue", SYSTEM_WORD_REPORTHANDLER | SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_GetFieldValue, "", IEGLConstants.ANY_STRING, 0, new String[] {"fieldName"}, new String[] {IEGLConstants.STRING_STRING}, new int[] {IN}, new int[] {1} ) ); //$NON-NLS-1$ //$NON-NLS-2$
		reportHandlerWords.put( IEGLConstants.SYSTEM_WORD_ADDREPORTDATA, new EGLSystemFunctionWord("addReportData", SYSTEM_WORD_REPORTHANDLER, IEGLConstants.Special_Function_AddReportData, "", null, 0, new String[] {"rd", "dataSetName"}, new String[] {IEGLConstants.REPORT_DATA_STRING, IEGLConstants.STRING_STRING}, new int[] {IN, IN}, new int[] {2} ) ); //$NON-NLS-1$ //$NON-NLS-2$
		reportHandlerWords.put( IEGLConstants.SYSTEM_WORD_GETREPORTDATA, new EGLSystemFunctionWord("getReportData", SYSTEM_WORD_REPORTHANDLER | SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_GetReportData, "", IEGLConstants.REPORT_DATA_STRING, 0, new String[] {"dataSetName"}, new String[] {IEGLConstants.STRING_STRING}, new int[] {IN}, new int[] {1} ) ); //$NON-NLS-1$ //$NON-NLS-2$
		
		serviceLibWords.put(IEGLConstants.SYSTEM_WORD_SETWEBENDPOINT, new EGLSystemFunctionWord("setWebEndpoint", SYSTEM_WORD_NO_RETURNS, IEGLConstants.Special_Function_SetWebEndpoint, serviceLibrary, null, 0, new String[] {"service", "endpoint"}, new String[] {serviceOrInterface,IEGLConstants.STRING_STRING}, new int[]{IN, IN}, new int[] { 2 })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
		serviceLibWords.put(IEGLConstants.SYSTEM_WORD_GETWEBENDPOINT, new EGLSystemFunctionWord("getWebEndpoint", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_GetWebEndpoint, serviceLibrary, IEGLConstants.STRING_STRING, 0, new String[] {"service"}, new String[] {serviceOrInterface}, new int[]{IN}, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$ 
		serviceLibWords.put(IEGLConstants.SYSTEM_WORD_SETTCPIPLOCATION, new EGLSystemFunctionWord("setTCPIPLocation", SYSTEM_WORD_NO_RETURNS, IEGLConstants.Special_Function_SetTCPIPLocation, serviceLibrary, null, 0, new String[] {"service", "location"}, new String[] {serviceOrInterface,IEGLConstants.STRING_STRING}, new int[]{IN, IN}, new int[] { 2 })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
		serviceLibWords.put(IEGLConstants.SYSTEM_WORD_GETTCPIPLOCATION, new EGLSystemFunctionWord("getTCPIPLocation", SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_GetTCPIPLocation, serviceLibrary, IEGLConstants.STRING_STRING, 0, new String[] {"service"}, new String[] {serviceOrInterface}, new int[]{IN}, new int[] { 1 })); //$NON-NLS-1$ //$NON-NLS-2$
		
		birtHandlerWords.put(IEGLConstants.SYSTEM_WORD_GETDATACOLUMNBINDING, new EGLSystemFunctionWord("getDataColumnBinding", SYSTEM_WORD_BIRTHANDLER | SYSTEM_WORD_RETURNS, IEGLConstants.Special_Function_GetDataColumnBinding, "", IEGLConstants.ANY_STRING, 0, new String[] {"fieldName"}, new String[] {IEGLConstants.STRING_STRING}, new int[] {IN}, new int[] {1} ) ); //$NON-NLS-1$ //$NON-NLS-2$
	} 
	
//	***********************
//  MATHLIB
	/**
	 * return the list of array mathLib EGLSystemWord objects
	 */
	public static List getArrayMathLibWords() {
		ArrayList systemWordsList = new ArrayList();
		Collection systemWordsValues = mathLibWords.values();
		Iterator iter = systemWordsValues.iterator();
		for (int i = 0; i < mathLibWords.size(); i++) {
			EGLSystemWord systemWord = (EGLSystemWord) iter.next();
			if (systemWord.isArrayWord())
				systemWordsList.add(systemWord);
		}
		return systemWordsList;
	}

	/**
	 * return the list of mathLib EGLSystemWord objects
	 */
	public static List getMathLibWords() {
		ArrayList systemWordsList = new ArrayList(mathLibWords.size());
		Collection systemWordsValues = mathLibWords.values();
		Iterator iter = systemWordsValues.iterator();
		for (int i = 0; i < mathLibWords.size(); i++) {
			EGLSystemWord systemWord = (EGLSystemWord) iter.next();
			systemWordsList.add(systemWord);
		}
		return systemWordsList;
	}
	
	public static Map getMathLibWordsAsMap()
	{
		return mathLibWords;
	}

	/**
	 * return the List of mathLib EGLSystemWord names
	 */
	public static List getMathLibWordNames() {
		ArrayList systemWordNames = new ArrayList(mathLibWords.size());
		Collection systemWordsValues = mathLibWords.values();
		Iterator iter = systemWordsValues.iterator();
		for (int i = 0; i < mathLibWords.size(); i++) {
			EGLSystemWord systemWord = (EGLSystemWord) iter.next();
			systemWordNames.add(systemWord.getName());
		}
		return systemWordNames;
	}

	/**
	 * return the List of mathLib EGLSystemWord names in lowercase
	 */
	public static List getMathLibWordNamesToLowerCase() {
		ArrayList systemWordNames = new ArrayList(mathLibWords.size());
		Collection systemWordsValues = mathLibWords.values();
		Iterator iter = systemWordsValues.iterator();
		for (int i = 0; i < mathLibWords.size(); i++) {
			EGLSystemWord systemWord = (EGLSystemWord) iter.next();
			systemWordNames.add(systemWord.getName().toLowerCase());
		}
		return systemWordNames;
	}

	/**
	 * return the List of mathLib function EGLSystemWord names with a return value
	 */
	public static List getmathLibFunctionWordNamesWithReturnValue() {
		ArrayList systemWordNames = new ArrayList();
		Collection systemWordsValues = mathLibWords.values();
		Iterator iter = systemWordsValues.iterator();
		for (int i = 0; i < mathLibWords.size(); i++) {
			EGLSystemWord systemWord = (EGLSystemWord) iter.next();
			if (systemWord instanceof EGLSystemFunctionWord) {
				EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
				if (functionWord.hasReturnCode())
					systemWordNames.add(systemWord.getName());
			}
		}
		return systemWordNames;
	}

	/**
	 * return the List of mathLib function EGLSystemWord with a return value
	 */
	public static List getMathLibFunctionWordsWithReturnValue() {
		ArrayList systemWordNames = new ArrayList();
		Collection systemWordsValues = mathLibWords.values();
		Iterator iter = systemWordsValues.iterator();
		for (int i = 0; i < mathLibWords.size(); i++) {
			EGLSystemWord systemWord = (EGLSystemWord) iter.next();
			if (systemWord instanceof EGLSystemFunctionWord) {
				EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
				if (functionWord.hasReturnCode())
					systemWordNames.add(systemWord);
			}
		}
		return systemWordNames;
	}

	/**
	 * return the List of MathLib function EGLSystemWord names
	 */
	public static List getMathLibFunctionWordNames() {
		ArrayList systemWordNames = new ArrayList();
		Collection systemWordsValues = mathLibWords.values();
		Iterator iter = systemWordsValues.iterator();
		for (int i = 0; i < mathLibWords.size(); i++) {
			EGLSystemWord systemWord = (EGLSystemWord) iter.next();
			if (systemWord instanceof EGLSystemFunctionWord) {
				systemWordNames.add(systemWord.getName());
			}
		}
		return systemWordNames;
	}

	/**
	 * return the List of mathLib function EGLSystemWord names with no return value
	 */
	public static List getMathLibFunctionWordNamesWithoutReturnValue() {
		ArrayList systemWordNames = new ArrayList();
		Collection systemWordsValues = mathLibWords.values();
		Iterator iter = systemWordsValues.iterator();
		for (int i = 0; i < mathLibWords.size(); i++) {
			EGLSystemWord systemWord = (EGLSystemWord) iter.next();
			if (systemWord instanceof EGLSystemFunctionWord) {
				EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
				if (!functionWord.hasReturnCode())
					systemWordNames.add(systemWord.getName());
			}
		}
		return systemWordNames;
	}

	/**
	 * return the List of mathLib function EGLSystemWord names with no return value
	 */
	public static List getMathLibFunctionWordsWithoutReturnValue() {
		ArrayList systemWordNames = new ArrayList();
		Collection systemWordsValues = mathLibWords.values();
		Iterator iter = systemWordsValues.iterator();
		for (int i = 0; i < mathLibWords.size(); i++) {
			EGLSystemWord systemWord = (EGLSystemWord) iter.next();
			if (systemWord instanceof EGLSystemFunctionWord) {
				EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
				if (!functionWord.hasReturnCode())
					systemWordNames.add(systemWord);
			}
		}
		return systemWordNames;
	}

	/**
	 * return the List of mathLib library EGLSystemWords
	 */
	public static List getLibraryMathLibWords(String library) {
		ArrayList systemWordNames = new ArrayList();
		Collection systemWordsValues = mathLibWords.values();
		Iterator iter = systemWordsValues.iterator();
		for (int i = 0; i < mathLibWords.size(); i++) {
			EGLSystemWord systemWord = (EGLSystemWord) iter.next();
			if (systemWord.getLibrary().equals(library))
				systemWordNames.add(systemWord);
		}
		return systemWordNames;
	}
	
	/**
	 * Return the list of mathLib system function names that are only valid for use within a page handler.
	 */
	public static List getPageHandlerMathLibFunctionWordNames() {
		ArrayList systemWordNames = new ArrayList();
		Iterator iter = mathLibWords.keySet().iterator();
		while( iter.hasNext() ) {
			EGLSystemWord systemWord = (EGLSystemWord) mathLibWords.get( iter.next() );
			if( systemWord.isPageHandlerSystemFunction() ) {
				systemWordNames.add(systemWord.getName());
			}
		}
		return systemWordNames;
	}

	/**
	 * return the List of mathLib function EGLSystemWord names with no return value
	 */
	public static EGLSystemWord getEGLMathLibWord(String key) {
		return (EGLSystemWord) mathLibWords.get(key);
	}

	/**
	 * return the List of mathLib variable EGLSystemWord names
	 */
	public static List getMathLibVariableNames() {
		ArrayList systemWordNames = new ArrayList();
		Collection systemWordsValues = mathLibWords.values();
		Iterator iter = systemWordsValues.iterator();
		for (int i = 0; i < mathLibWords.size(); i++) {
			EGLSystemWord systemWord = (EGLSystemWord) iter.next();
			if (systemWord instanceof EGLSystemVariable)
				systemWordNames.add(systemWord.getName());
		}
		return systemWordNames;
	}

	/**
	 * return the List of mathLib variable EGLSystemWords
	 */
	public static List getMathLibVariables() {
		ArrayList systemWordNames = new ArrayList();
		Collection systemWordsValues = mathLibWords.values();
		Iterator iter = systemWordsValues.iterator();
		for (int i = 0; i < mathLibWords.size(); i++) {
			EGLSystemWord systemWord = (EGLSystemWord) iter.next();
			if (systemWord instanceof EGLSystemVariable)
				systemWordNames.add(systemWord);
		}
		return systemWordNames;
	}

	/**
	 * return the List of mathLib numeric variable EGLSystemWord names
	 */
	public static List getMathLibNumericVariableNames() {
		ArrayList systemWordNames = new ArrayList();
		Collection systemWordsValues = mathLibWords.values();
		Iterator iter = systemWordsValues.iterator();
		for (int i = 0; i < mathLibWords.size(); i++) {
			EGLSystemWord systemWord = (EGLSystemWord) iter.next();
			if (systemWord instanceof EGLSystemVariable) {
				EGLSystemVariable systemVariable = (EGLSystemVariable) systemWord;
				if (systemVariable.isNumericVariable())
					systemWordNames.add(systemVariable.getName());
			}
		}
		return systemWordNames;
	}
	
//***********************
//   STRLIB
	/**
	 * return the list of array strLib EGLSystemWord objects
	 */
	public static List getArrayStrLibWords() {
		ArrayList systemWordsList = new ArrayList();
		Collection systemWordsValues = strLibWords.values();
		Iterator iter = systemWordsValues.iterator();
		for (int i = 0; i < strLibWords.size(); i++) {
			EGLSystemWord systemWord = (EGLSystemWord) iter.next();
			if (systemWord.isArrayWord())
				systemWordsList.add(systemWord);
		}
		return systemWordsList;
	}

	/**
	 * return the list of strlib EGLSystemWord objects
	 */
	public static List getStrLibWords() {
		ArrayList systemWordsList = new ArrayList(strLibWords.size());
		Collection systemWordsValues = strLibWords.values();
		Iterator iter = systemWordsValues.iterator();
		for (int i = 0; i < strLibWords.size(); i++) {
			EGLSystemWord systemWord = (EGLSystemWord) iter.next();
			systemWordsList.add(systemWord);
		}
		return systemWordsList;
	}
	
	public static Map getStrLibWordsAsMap()
	{
		return strLibWords;
	}

	/**
	 * return the List of strLib EGLSystemWord names
	 */
	public static List getStrLibWordNames() {
		ArrayList systemWordNames = new ArrayList(strLibWords.size());
		Collection systemWordsValues = strLibWords.values();
		Iterator iter = systemWordsValues.iterator();
		for (int i = 0; i < strLibWords.size(); i++) {
			EGLSystemWord systemWord = (EGLSystemWord) iter.next();
			systemWordNames.add(systemWord.getName());
		}
		return systemWordNames;
	}

	/**
	 * return the List of strLib EGLSystemWord names in lowercase
	 */
	public static List getStrLibWordNamesToLowerCase() {
		ArrayList systemWordNames = new ArrayList(strLibWords.size());
		Collection systemWordsValues = strLibWords.values();
		Iterator iter = systemWordsValues.iterator();
		for (int i = 0; i < strLibWords.size(); i++) {
			EGLSystemWord systemWord = (EGLSystemWord) iter.next();
			systemWordNames.add(systemWord.getName().toLowerCase());
		}
		return systemWordNames;
	}

	/**
	 * return the List of strLib function EGLSystemWord names with a return value
	 */
	public static List getStrLibFunctionWordNamesWithReturnValue() {
		ArrayList systemWordNames = new ArrayList();
		Collection systemWordsValues = strLibWords.values();
		Iterator iter = systemWordsValues.iterator();
		for (int i = 0; i < strLibWords.size(); i++) {
			EGLSystemWord systemWord = (EGLSystemWord) iter.next();
			if (systemWord instanceof EGLSystemFunctionWord) {
				EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
				if (functionWord.hasReturnCode())
					systemWordNames.add(systemWord.getName());
			}
		}
		return systemWordNames;
	}

	/**
	 * return the List of strLib function EGLSystemWord with a return value
	 */
	public static List getStrLibFunctionWordsWithReturnValue() {
		ArrayList systemWordNames = new ArrayList();
		Collection systemWordsValues = strLibWords.values();
		Iterator iter = systemWordsValues.iterator();
		for (int i = 0; i < strLibWords.size(); i++) {
			EGLSystemWord systemWord = (EGLSystemWord) iter.next();
			if (systemWord instanceof EGLSystemFunctionWord) {
				EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
				if (functionWord.hasReturnCode())
					systemWordNames.add(systemWord);
			}
		}
		return systemWordNames;
	}

	/**
	 * return the List of strLib function EGLSystemWord names
	 */
	public static List getStrLibFunctionWordNames() {
		ArrayList systemWordNames = new ArrayList();
		Collection systemWordsValues = strLibWords.values();
		Iterator iter = systemWordsValues.iterator();
		for (int i = 0; i < strLibWords.size(); i++) {
			EGLSystemWord systemWord = (EGLSystemWord) iter.next();
			if (systemWord instanceof EGLSystemFunctionWord) {
				systemWordNames.add(systemWord.getName());
			}
		}
		return systemWordNames;
	}

	/**
	 * return the List of strLib function EGLSystemWord names with no return value
	 */
	public static List getStrLibFunctionWordNamesWithoutReturnValue() {
		ArrayList systemWordNames = new ArrayList();
		Collection systemWordsValues = strLibWords.values();
		Iterator iter = systemWordsValues.iterator();
		for (int i = 0; i < strLibWords.size(); i++) {
			EGLSystemWord systemWord = (EGLSystemWord) iter.next();
			if (systemWord instanceof EGLSystemFunctionWord) {
				EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
				if (!functionWord.hasReturnCode())
					systemWordNames.add(systemWord.getName());
			}
		}
		return systemWordNames;
	}

	/**
	 * return the List of strLib function EGLSystemWord names with no return value
	 */
	public static List getStrLibFunctionWordsWithoutReturnValue() {
		ArrayList systemWordNames = new ArrayList();
		Collection systemWordsValues = strLibWords.values();
		Iterator iter = systemWordsValues.iterator();
		for (int i = 0; i < strLibWords.size(); i++) {
			EGLSystemWord systemWord = (EGLSystemWord) iter.next();
			if (systemWord instanceof EGLSystemFunctionWord) {
				EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
				if (!functionWord.hasReturnCode())
					systemWordNames.add(systemWord);
			}
		}
		return systemWordNames;
	}

	/**
	 * return the List of strLib library EGLSystemWords
	 */
	public static List getLibraryStrLibWords(String library) {
		ArrayList systemWordNames = new ArrayList();
		Collection systemWordsValues = strLibWords.values();
		Iterator iter = systemWordsValues.iterator();
		for (int i = 0; i < strLibWords.size(); i++) {
			EGLSystemWord systemWord = (EGLSystemWord) iter.next();
			if (systemWord.getLibrary().equals(library))
				systemWordNames.add(systemWord);
		}
		return systemWordNames;
	}
	
	/**
	 * Return the list of strLib system function names that are only valid for use within a page handler.
	 */
	public static List getPageHandlerStrLibFunctionWordNames() {
		ArrayList systemWordNames = new ArrayList();
		Iterator iter = strLibWords.keySet().iterator();
		while( iter.hasNext() ) {
			EGLSystemWord systemWord = (EGLSystemWord) strLibWords.get( iter.next() );
			if( systemWord.isPageHandlerSystemFunction() ) {
				systemWordNames.add(systemWord.getName());
			}
		}
		return systemWordNames;
	}

	/**
	 * return the List of function EGLSystemWord names with no return value
	 */
	public static EGLSystemWord getEGLStrLibWord(String key) {
		return (EGLSystemWord) strLibWords.get(key);
	}

	/**
	 * return the List of strLib variable EGLSystemWord names
	 */
	public static List getStrLibVariableNames() {
		ArrayList systemWordNames = new ArrayList();
		Collection systemWordsValues = strLibWords.values();
		Iterator iter = systemWordsValues.iterator();
		for (int i = 0; i < strLibWords.size(); i++) {
			EGLSystemWord systemWord = (EGLSystemWord) iter.next();
			if (systemWord instanceof EGLSystemVariable)
				systemWordNames.add(systemWord.getName());
		}
		return systemWordNames;
	}

	/**
	 * return the List of strLib variable EGLSystemWords
	 */
	public static List getStrLibVariables() {
		ArrayList systemWordNames = new ArrayList();
		Collection systemWordsValues = strLibWords.values();
		Iterator iter = systemWordsValues.iterator();
		for (int i = 0; i < strLibWords.size(); i++) {
			EGLSystemWord systemWord = (EGLSystemWord) iter.next();
			if (systemWord instanceof EGLSystemVariable)
				systemWordNames.add(systemWord);
		}
		return systemWordNames;
	}

	/**
	 * return the List of strLIb numeric variable EGLSystemWord names
	 */
	public static List getStrLibNumericVariableNames() {
		ArrayList systemWordNames = new ArrayList();
		Collection systemWordsValues = strLibWords.values();
		Iterator iter = systemWordsValues.iterator();
		for (int i = 0; i < strLibWords.size(); i++) {
			EGLSystemWord systemWord = (EGLSystemWord) iter.next();
			if (systemWord instanceof EGLSystemVariable) {
				EGLSystemVariable systemVariable = (EGLSystemVariable) systemWord;
				if (systemVariable.isNumericVariable())
					systemWordNames.add(systemVariable.getName());
			}
		}
		return systemWordNames;
	}
	
	
//	***********************
//	   SYSLIB
		/**
		 * return the list of array sysLib EGLSystemWord objects
		 */
		public static List getArraySysLibWords() {
			ArrayList systemWordsList = new ArrayList();
			Collection systemWordsValues = sysLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < sysLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord.isArrayWord())
					systemWordsList.add(systemWord);
			}
			return systemWordsList;
		}

		/**
		 * return the list of sysLib EGLSystemWord objects
		 */
		public static List getSysLibWords() {
			ArrayList systemWordsList = new ArrayList(sysLibWords.size());
			Collection systemWordsValues = sysLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < sysLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordsList.add(systemWord);
			}
			return systemWordsList;
		}
		
		public static Map getSysLibWordsAsMap()
		{
			return sysLibWords;
		}

		/**
		 * return the List of sysLib EGLSystemWord names
		 */
		public static List getSysLibWordNames() {
			ArrayList systemWordNames = new ArrayList(sysLibWords.size());
			Collection systemWordsValues = sysLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < sysLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordNames.add(systemWord.getName());
			}
			return systemWordNames;
		}

		/**
		 * return the List of sysLib EGLSystemWord names in lowercase
		 */
		public static List getSysLibWordNamesToLowerCase() {
			ArrayList systemWordNames = new ArrayList(sysLibWords.size());
			Collection systemWordsValues = sysLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < sysLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordNames.add(systemWord.getName().toLowerCase());
			}
			return systemWordNames;
		}

		/**
		 * return the List of sysLib function EGLSystemWord names with a return value
		 */
		public static List getSysLibFunctionWordNamesWithReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = sysLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < sysLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (functionWord.hasReturnCode())
						systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}

		/**
		 * return the List of sysLib function EGLSystemWord with a return value
		 */
		public static List getSysLibFunctionWordsWithReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = sysLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < sysLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (functionWord.hasReturnCode())
						systemWordNames.add(systemWord);
				}
			}
			return systemWordNames;
		}

		/**
		 * return the List of sysLib function EGLSystemWord names
		 */
		public static List getSysLibFunctionWordNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = sysLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < sysLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}

		/**
		 * return the List of sysLib function EGLSystemWord names with no return value
		 */
		public static List getSysLibFunctionWordNamesWithoutReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = sysLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < sysLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (!functionWord.hasReturnCode())
						systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}

		/**
		 * return the List of sysLib function EGLSystemWord names with no return value
		 */
		public static List getSysLibFunctionWordsWithoutReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = sysLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < sysLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (!functionWord.hasReturnCode())
						systemWordNames.add(systemWord);
				}
			}
			return systemWordNames;
		}

		/**
		 * return the List of library sysLib EGLSystemWords
		 */
		public static List getLibrarySysLibWords(String library) {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = sysLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < sysLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord.getLibrary().equals(library))
					systemWordNames.add(systemWord);
			}
			return systemWordNames;
		}

		/**
		 * Return the list of sysLib system function names that are only valid for use within a page handler.
		 */
		public static List getPageHandlerSysLibFunctionWordNames() {
			ArrayList systemWordNames = new ArrayList();
			Iterator iter = sysLibWords.keySet().iterator();
			while( iter.hasNext() ) {
				EGLSystemWord systemWord = (EGLSystemWord) sysLibWords.get( iter.next() );
				if( systemWord.isPageHandlerSystemFunction() ) {
					systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}

		/**
		 * return the List of sysLib function EGLSystemWord names with no return value
		 */
		public static EGLSystemWord getEGLSysLibWord(String key) {
			return (EGLSystemWord) sysLibWords.get(key);
		}

		/**
		 * return the List of sysLib variable EGLSystemWord names
		 */
		public static List getSysLibVariableNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = sysLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < sysLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable)
					systemWordNames.add(systemWord.getName());
			}
			return systemWordNames;
		}

		/**
		 * return the List of sysLib variable EGLSystemWords
		 */
		public static List getSysLibVariables() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = sysLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < sysLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable)
					systemWordNames.add(systemWord);
			}
			return systemWordNames;
		}

		/**
		 * return the List of sysLib numeric variable EGLSystemWord names
		 */
		public static List getSysLibNumericVariableNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = sysLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < sysLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable) {
					EGLSystemVariable systemVariable = (EGLSystemVariable) systemWord;
					if (systemVariable.isNumericVariable())
						systemWordNames.add(systemVariable.getName());
				}
			}
			return systemWordNames;
		}
		
		
//***********************
//	  SYSVAR
		/**
		 * return the list of array sysVar EGLSystemWord objects
		 */
		public static List getArraySysVarWords() {
			ArrayList systemWordsList = new ArrayList();
			Collection systemWordsValues = sysVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < sysVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord.isArrayWord())
					systemWordsList.add(systemWord);
			}
			return systemWordsList;
		}

		/**
		 * return the list of sysVar EGLSystemWord objects
		 */
		public static List getSysVarWords() {
			ArrayList systemWordsList = new ArrayList(sysVarWords.size());
			Collection systemWordsValues = sysVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < sysVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordsList.add(systemWord);
			}
			return systemWordsList;
		}
		
		public static Map getSysVarWordsAsMap()
		{
			return sysVarWords;
		}

		/**
		 * return the List of sysVar EGLSystemWord names
		 */
		public static List getSysVarWordNames() {
			ArrayList systemWordNames = new ArrayList(sysVarWords.size());
			Collection systemWordsValues = sysVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < sysVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordNames.add(systemWord.getName());
			}
			return systemWordNames;
		}

		/**
		 * return the List of sysVar EGLSystemWord names in lowercase
		 */
		public static List getSysVarWordNamesToLowerCase() {
			ArrayList systemWordNames = new ArrayList(sysVarWords.size());
			Collection systemWordsValues = sysVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < sysVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordNames.add(systemWord.getName().toLowerCase());
			}
			return systemWordNames;
		}

		/**
		 * return the List of sysVar function EGLSystemWord names with a return value
		 */
		public static List getSysVarFunctionWordNamesWithReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = sysVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < sysVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (functionWord.hasReturnCode())
						systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}

		/**
		 * return the List of sysVar function EGLSystemWord with a return value
		 */
		public static List getSysVarFunctionWordsWithReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = sysVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < sysVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (functionWord.hasReturnCode())
						systemWordNames.add(systemWord);
				}
			}
			return systemWordNames;
		}

		/**
		 * return the List of sysVar function EGLSystemWord names
		 */
		public static List getSysVarFunctionWordNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = sysVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < sysVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}

		/**
		 * return the List of sysVar function EGLSystemWord names with no return value
		 */
		public static List getSysVarFunctionWordNamesWithoutReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = sysVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < sysVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (!functionWord.hasReturnCode())
						systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}

		/**
		 * return the List of sysVar function EGLSystemWord names with no return value
		 */
		public static List getSysVarFunctionWordsWithoutReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = sysVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < sysVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (!functionWord.hasReturnCode())
						systemWordNames.add(systemWord);
				}
			}
			return systemWordNames;
		}

		/**
		 * return the List of library sysVar EGLSystemWords
		 */
		public static List getLibrarySysVarWords(String library) {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = sysVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < sysVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord.getLibrary().equals(library))
					systemWordNames.add(systemWord);
			}
			return systemWordNames;
		}

		/**
		 * Return the list of sysVar system function names that are only valid for use within a page handler.
		 */
		public static List getPageHandlerSysVarFunctionWordNames() {
			ArrayList systemWordNames = new ArrayList();
			Iterator iter = sysVarWords.keySet().iterator();
			while( iter.hasNext() ) {
				EGLSystemWord systemWord = (EGLSystemWord) sysVarWords.get( iter.next() );
				if( systemWord.isPageHandlerSystemFunction() ) {
					systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}

		/**
		 * return the List of sysVar function EGLSystemWord names with no return value
		 */
		public static EGLSystemWord getEGLSysVarWord(String key) {
			return (EGLSystemWord) sysVarWords.get(key);
		}

		/**
		 * return the List of sysVar variable EGLSystemWord names
		 */
		public static List getSysVarVariableNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = sysVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < sysVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable)
					systemWordNames.add(systemWord.getName());
			}
			return systemWordNames;
		}

		/**
		 * return the List of sysVar variable EGLSystemWords
		 */
		public static List getSysVarVariables() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = sysVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < sysVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable)
					systemWordNames.add(systemWord);
			}
			return systemWordNames;
		}

		/**
		 * return the List of sysVar numeric variable EGLSystemWord names
		 */
		public static List getSysVarNumericVariableNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = sysVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < sysVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable) {
					EGLSystemVariable systemVariable = (EGLSystemVariable) systemWord;
					if (systemVariable.isNumericVariable())
						systemWordNames.add(systemVariable.getName());
				}
			}
			return systemWordNames;
		}
			
	
//***********************
//	  Dictionary
		/**
		 * return the list of array Dictionary EGLSystemWord objects
		 */
		public static List getArrayDictionaryWords() {
			ArrayList systemWordsList = new ArrayList();
			Collection systemWordsValues = dictionaryWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < dictionaryWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord.isArrayWord())
					systemWordsList.add(systemWord);
			}
			return systemWordsList;
		}

		/**
		 * return the list of Dictionary EGLSystemWord objects
		 */
		public static List getDictionaryWords() {
			ArrayList systemWordsList = new ArrayList(dictionaryWords.size());
			Collection systemWordsValues = dictionaryWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < dictionaryWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordsList.add(systemWord);
			}
			return systemWordsList;
		}
		
		public static Map getDictionaryWordsAsMap()
		{
			return dictionaryWords;
		}

		/**
		 * return the List of Dictionary EGLSystemWord names
		 */
		public static List getDictionaryWordNames() {
			ArrayList systemWordNames = new ArrayList(dictionaryWords.size());
			Collection systemWordsValues = dictionaryWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < dictionaryWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordNames.add(systemWord.getName());
			}
			return systemWordNames;
		}

		/**
		 * return the List of Dictionary EGLSystemWord names in lowercase
		 */
		public static List getDictionaryWordNamesToLowerCase() {
			ArrayList systemWordNames = new ArrayList(dictionaryWords.size());
			Collection systemWordsValues = dictionaryWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < dictionaryWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordNames.add(systemWord.getName().toLowerCase());
			}
			return systemWordNames;
		}

		/**
		 * return the List of Dictionary function EGLSystemWord names with a return value
		 */
		public static List getDictionaryFunctionWordNamesWithReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = dictionaryWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < dictionaryWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (functionWord.hasReturnCode())
						systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}

		/**
		 * return the List of Dictionary function EGLSystemWord with a return value
		 */
		public static List getDictionaryFunctionWordsWithReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = dictionaryWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < dictionaryWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (functionWord.hasReturnCode())
						systemWordNames.add(systemWord);
				}
			}
			return systemWordNames;
		}

		/**
		 * return the List of Dictionary function EGLSystemWord names
		 */
		public static List getDictionaryFunctionWordNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = dictionaryWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < dictionaryWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}

		/**
		 * return the List of Dictionary function EGLSystemWord names with no return value
		 */
		public static List getDictionaryFunctionWordNamesWithoutReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = dictionaryWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < dictionaryWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (!functionWord.hasReturnCode())
						systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}

		/**
		 * return the List of Dictionary function EGLSystemWord names with no return value
		 */
		public static List getDictionaryFunctionWordsWithoutReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = dictionaryWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < dictionaryWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (!functionWord.hasReturnCode())
						systemWordNames.add(systemWord);
				}
			}
			return systemWordNames;
		}

		/**
		 * return the List of library Dictionary EGLSystemWords
		 */
		public static List getLibraryDictionaryWords(String library) {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = dictionaryWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < dictionaryWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord.getLibrary().equals(library))
					systemWordNames.add(systemWord);
			}
			return systemWordNames;
		}

		/**
		 * Return the list of Dictionary system function names that are only valid for use within a page handler.
		 */
		public static List getPageHandlerDictionaryFunctionWordNames() {
			ArrayList systemWordNames = new ArrayList();
			Iterator iter = dictionaryWords.keySet().iterator();
			while( iter.hasNext() ) {
				EGLSystemWord systemWord = (EGLSystemWord) dictionaryWords.get( iter.next() );
				if( systemWord.isPageHandlerSystemFunction() ) {
					systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}

		/**
		 * return the List of Dictionary function EGLSystemWord names with no return value
		 */
		public static EGLSystemWord getEGLDictionaryWord(String key) {
			return (EGLSystemWord) dictionaryWords.get(key);
		}

		/**
		 * return the List of Dictionary variable EGLSystemWord names
		 */
		public static List getDictionaryVariableNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = dictionaryWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < dictionaryWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable)
					systemWordNames.add(systemWord.getName());
			}
			return systemWordNames;
		}

		/**
		 * return the List of Dictionary variable EGLSystemWords
		 */
		public static List getDictionaryVariables() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = dictionaryWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < dictionaryWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable)
					systemWordNames.add(systemWord);
			}
			return systemWordNames;
		}

		/**
		 * return the List of Dictionary numeric variable EGLSystemWord names
		 */
		public static List getDictionaryNumericVariableNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = dictionaryWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < dictionaryWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable) {
					EGLSystemVariable systemVariable = (EGLSystemVariable) systemWord;
					if (systemVariable.isNumericVariable())
						systemWordNames.add(systemVariable.getName());
				}
			}
			return systemWordNames;
		}
		

				
//***********************
//  Console UI
		/**
		 * return the list of array ConsoleLib EGLSystemWord objects
		 */
		public static List getArrayConsoleLibWords() {
			ArrayList systemWordsList = new ArrayList();
			Collection systemWordsValues = consoleLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < consoleLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord.isArrayWord())
					systemWordsList.add(systemWord);
			}
			return systemWordsList;
		}

		/**
		 * return the list of consoleLib EGLSystemWord objects
		 */
		public static List getConsoleLibWords() {
			ArrayList systemWordsList = new ArrayList(consoleLibWords.size());
			Collection systemWordsValues = consoleLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < consoleLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordsList.add(systemWord);
			}
			return systemWordsList;
		}
		
		public static Map getConsoleLibWordsAsMap()
		{
			return consoleLibWords;
		}
		
		/**
		 * return the List of ConsoleLib EGLSystemWord names
		 */
		public static List getConsoleLibWordNames() {
			ArrayList systemWordNames = new ArrayList(consoleLibWords.size());
			Collection systemWordsValues = consoleLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < consoleLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordNames.add(systemWord.getName());
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConsoleLib EGLSystemWord names in lowercase
		 */
		public static List getConsoleLibWordNamesToLowerCase() {
			ArrayList systemWordNames = new ArrayList(consoleLibWords.size());
			Collection systemWordsValues = consoleLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < consoleLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordNames.add(systemWord.getName().toLowerCase());
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConsoleLib function EGLSystemWord names with a return value
		 */
		public static List getConsoleLibFunctionWordNamesWithReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = consoleLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < consoleLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (functionWord.hasReturnCode())
						systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConsoleLib function EGLSystemWord with a return value
		 */
		public static List getConsoleLibFunctionWordsWithReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = consoleLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < consoleLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (functionWord.hasReturnCode())
						systemWordNames.add(systemWord);
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConsoleLib function EGLSystemWord names
		 */
		public static List getConsoleLibFunctionWordNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = consoleLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < consoleLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConsoleLib function EGLSystemWord names with no return value
		 */
		public static List getConsoleLibFunctionWordNamesWithoutReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = consoleLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < consoleLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (!functionWord.hasReturnCode())
						systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConsoleLib function EGLSystemWord names with no return value
		 */
		public static List getConsoleLibFunctionWordsWithoutReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = consoleLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < consoleLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (!functionWord.hasReturnCode())
						systemWordNames.add(systemWord);
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConsoleLib Dictionary EGLSystemWords
		 */
		public static List getLibraryConsoleLibWords(String library) {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = consoleLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < consoleLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord.getLibrary().equals(library))
					systemWordNames.add(systemWord);
			}
			return systemWordNames;
		}
		
		/**
		 * Return the list of ConsoleLib system function names that are only valid for use within a page handler.
		 */
		public static List getPageHandlerConsoleLibFunctionWordNames() {
			ArrayList systemWordNames = new ArrayList();
			Iterator iter = consoleLibWords.keySet().iterator();
			while( iter.hasNext() ) {
				EGLSystemWord systemWord = (EGLSystemWord) consoleLibWords.get( iter.next() );
				if( systemWord.isPageHandlerSystemFunction() ) {
					systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConsoleLib function EGLSystemWord names with no return value
		 */
		public static EGLSystemWord getEGLConsoleLibWord(String key) {
			return (EGLSystemWord) consoleLibWords.get(key);
		}
		
		/**
		 * return the List of ConsoleLib variable EGLSystemWord names
		 */
		public static List getConsoleLibVariableNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = consoleLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < consoleLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable)
					systemWordNames.add(systemWord.getName());
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConsoleLib variable EGLSystemWords
		 */
		public static List getConsoleLibVariables() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = consoleLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < consoleLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable)
					systemWordNames.add(systemWord);
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConsoleLib numeric variable EGLSystemWord names
		 */
		public static List getConsoleLibNumericVariableNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = consoleLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < consoleLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable) {
					EGLSystemVariable systemVariable = (EGLSystemVariable) systemWord;
					if (systemVariable.isNumericVariable())
						systemWordNames.add(systemVariable.getName());
				}
			}
			return systemWordNames;
		}
		
		
		
		
		
//		***********************
//		  Dynamic Array
			/**
			 * return the list of array dynArray EGLSystemWord objects
			 */
			public static List getArrayDynArrayWords() {
				ArrayList systemWordsList = new ArrayList();
				Collection systemWordsValues = dynArrayWords.values();
				Iterator iter = systemWordsValues.iterator();
				for (int i = 0; i < dynArrayWords.size(); i++) {
					EGLSystemWord systemWord = (EGLSystemWord) iter.next();
					if (systemWord.isArrayWord())
						systemWordsList.add(systemWord);
				}
				return systemWordsList;
			}

			/**
			 * return the list of dynArray EGLSystemWord objects
			 */
			public static List getDynArrayWords() {
				ArrayList systemWordsList = new ArrayList(dynArrayWords.size());
				Collection systemWordsValues = dynArrayWords.values();
				Iterator iter = systemWordsValues.iterator();
				for (int i = 0; i < dynArrayWords.size(); i++) {
					EGLSystemWord systemWord = (EGLSystemWord) iter.next();
					systemWordsList.add(systemWord);
				}
				return systemWordsList;
			}
			
			public static Map getDynArrayWordsAsMap()
			{
				return dynArrayWords;
			}

			/**
			 * return the List of dynArray EGLSystemWord names
			 */
			public static List getDynArrayWordNames() {
				ArrayList systemWordNames = new ArrayList(dynArrayWords.size());
				Collection systemWordsValues = dynArrayWords.values();
				Iterator iter = systemWordsValues.iterator();
				for (int i = 0; i < dynArrayWords.size(); i++) {
					EGLSystemWord systemWord = (EGLSystemWord) iter.next();
					systemWordNames.add(systemWord.getName());
				}
				return systemWordNames;
			}

			/**
			 * return the List of dynArray EGLSystemWord names in lowercase
			 */
			public static List getDynArrayWordNamesToLowerCase() {
				ArrayList systemWordNames = new ArrayList(dynArrayWords.size());
				Collection systemWordsValues = dynArrayWords.values();
				Iterator iter = systemWordsValues.iterator();
				for (int i = 0; i < dynArrayWords.size(); i++) {
					EGLSystemWord systemWord = (EGLSystemWord) iter.next();
					systemWordNames.add(systemWord.getName().toLowerCase());
				}
				return systemWordNames;
			}

			/**
			 * return the List of dynArray function EGLSystemWord names with a return value
			 */
			public static List getDynArrayFunctionWordNamesWithReturnValue() {
				ArrayList systemWordNames = new ArrayList();
				Collection systemWordsValues = dynArrayWords.values();
				Iterator iter = systemWordsValues.iterator();
				for (int i = 0; i < dynArrayWords.size(); i++) {
					EGLSystemWord systemWord = (EGLSystemWord) iter.next();
					if (systemWord instanceof EGLSystemFunctionWord) {
						EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
						if (functionWord.hasReturnCode())
							systemWordNames.add(systemWord.getName());
					}
				}
				return systemWordNames;
			}

			/**
			 * return the List of dynArray function EGLSystemWord with a return value
			 */
			public static List getDynArrayFunctionWordsWithReturnValue() {
				ArrayList systemWordNames = new ArrayList();
				Collection systemWordsValues = dynArrayWords.values();
				Iterator iter = systemWordsValues.iterator();
				for (int i = 0; i < dynArrayWords.size(); i++) {
					EGLSystemWord systemWord = (EGLSystemWord) iter.next();
					if (systemWord instanceof EGLSystemFunctionWord) {
						EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
						if (functionWord.hasReturnCode())
							systemWordNames.add(systemWord);
					}
				}
				return systemWordNames;
			}

			/**
			 * return the List of dynArray function EGLSystemWord names
			 */
			public static List getDynArrayFunctionWordNames() {
				ArrayList systemWordNames = new ArrayList();
				Collection systemWordsValues = dynArrayWords.values();
				Iterator iter = systemWordsValues.iterator();
				for (int i = 0; i < dynArrayWords.size(); i++) {
					EGLSystemWord systemWord = (EGLSystemWord) iter.next();
					if (systemWord instanceof EGLSystemFunctionWord) {
						systemWordNames.add(systemWord.getName());
					}
				}
				return systemWordNames;
			}

			/**
			 * return the List of dynArray function EGLSystemWord names with no return value
			 */
			public static List getDynArrayFunctionWordNamesWithoutReturnValue() {
				ArrayList systemWordNames = new ArrayList();
				Collection systemWordsValues = dynArrayWords.values();
				Iterator iter = systemWordsValues.iterator();
				for (int i = 0; i < dynArrayWords.size(); i++) {
					EGLSystemWord systemWord = (EGLSystemWord) iter.next();
					if (systemWord instanceof EGLSystemFunctionWord) {
						EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
						if (!functionWord.hasReturnCode())
							systemWordNames.add(systemWord.getName());
					}
				}
				return systemWordNames;
			}

			/**
			 * return the List of dynArray function EGLSystemWord names with no return value
			 */
			public static List getDynArrayFunctionWordsWithoutReturnValue() {
				ArrayList systemWordNames = new ArrayList();
				Collection systemWordsValues = dynArrayWords.values();
				Iterator iter = systemWordsValues.iterator();
				for (int i = 0; i < dynArrayWords.size(); i++) {
					EGLSystemWord systemWord = (EGLSystemWord) iter.next();
					if (systemWord instanceof EGLSystemFunctionWord) {
						EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
						if (!functionWord.hasReturnCode())
							systemWordNames.add(systemWord);
					}
				}
				return systemWordNames;
			}

			/**
			 * return the List of library dynArray EGLSystemWords
			 */
			public static List getLibraryDynArrayWords(String library) {
				ArrayList systemWordNames = new ArrayList();
				Collection systemWordsValues = dynArrayWords.values();
				Iterator iter = systemWordsValues.iterator();
				for (int i = 0; i < dynArrayWords.size(); i++) {
					EGLSystemWord systemWord = (EGLSystemWord) iter.next();
					if (systemWord.getLibrary().equals(library))
						systemWordNames.add(systemWord);
				}
				return systemWordNames;
			}

			/**
			 * Return the list of dynArray system function names that are only valid for use within a page handler.
			 */
			public static List getPageHandlerDynArrayFunctionWordNames() {
				ArrayList systemWordNames = new ArrayList();
				Iterator iter = dynArrayWords.keySet().iterator();
				while( iter.hasNext() ) {
					EGLSystemWord systemWord = (EGLSystemWord) dynArrayWords.get( iter.next() );
					if( systemWord.isPageHandlerSystemFunction() ) {
						systemWordNames.add(systemWord.getName());
					}
				}
				return systemWordNames;
			}

			/**
			 * return the List of dynArray function EGLSystemWord names with no return value
			 */
			public static EGLSystemWord getEGLDynArrayWord(String key) {
				return (EGLSystemWord) dynArrayWords.get(key);
			}

			/**
			 * return the List of dynArray variable EGLSystemWord names
			 */
			public static List getDynArrayVariableNames() {
				ArrayList systemWordNames = new ArrayList();
				Collection systemWordsValues = dynArrayWords.values();
				Iterator iter = systemWordsValues.iterator();
				for (int i = 0; i < dynArrayWords.size(); i++) {
					EGLSystemWord systemWord = (EGLSystemWord) iter.next();
					if (systemWord instanceof EGLSystemVariable)
						systemWordNames.add(systemWord.getName());
				}
				return systemWordNames;
			}

			/**
			 * return the List of dynArray variable EGLSystemWords
			 */
			public static List getDynArrayVariables() {
				ArrayList systemWordNames = new ArrayList();
				Collection systemWordsValues = dynArrayWords.values();
				Iterator iter = systemWordsValues.iterator();
				for (int i = 0; i < dynArrayWords.size(); i++) {
					EGLSystemWord systemWord = (EGLSystemWord) iter.next();
					if (systemWord instanceof EGLSystemVariable)
						systemWordNames.add(systemWord);
				}
				return systemWordNames;
			}

			/**
			 * return the List of dynArray numeric variable EGLSystemWord names
			 */
			public static List getDynArrayNumericVariableNames() {
				ArrayList systemWordNames = new ArrayList();
				Collection systemWordsValues = dynArrayWords.values();
				Iterator iter = systemWordsValues.iterator();
				for (int i = 0; i < dynArrayWords.size(); i++) {
					EGLSystemWord systemWord = (EGLSystemWord) iter.next();
					if (systemWord instanceof EGLSystemVariable) {
						EGLSystemVariable systemVariable = (EGLSystemVariable) systemWord;
						if (systemVariable.isNumericVariable())
							systemWordNames.add(systemVariable.getName());
					}
				}
				return systemWordNames;
			}
				



//***********************
//	  record words
		/**
		 * return the list of array record EGLSystemWord objects
		 */
		public static List getArrayRecordWords() {
			ArrayList systemWordsList = new ArrayList();
			Collection systemWordsValues = recordWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < recordWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord.isArrayWord())
					systemWordsList.add(systemWord);
			}
			return systemWordsList;
		}

		/**
		 * return the list of record EGLSystemWord objects
		 */
		public static List getRecordWords() {
			ArrayList systemWordsList = new ArrayList(recordWords.size());
			Collection systemWordsValues = recordWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < recordWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordsList.add(systemWord);
			}
			return systemWordsList;
		}
		
		public static Map getRecordWordsAsMap()
		{
			return recordWords;
		}

		/**
		 * return the List of record EGLSystemWord names
		 */
		public static List getRecordWordNames() {
			ArrayList systemWordNames = new ArrayList(recordWords.size());
			Collection systemWordsValues = recordWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < recordWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordNames.add(systemWord.getName());
			}
			return systemWordNames;
		}

		/**
		 * return the List of record EGLSystemWord names in lowercase
		 */
		public static List getRecordWordNamesToLowerCase() {
			ArrayList systemWordNames = new ArrayList(recordWords.size());
			Collection systemWordsValues = recordWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < recordWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordNames.add(systemWord.getName().toLowerCase());
			}
			return systemWordNames;
		}

		/**
		 * return the List of record function EGLSystemWord names with a return value
		 */
		public static List getRecordFunctionWordNamesWithReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = recordWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < recordWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (functionWord.hasReturnCode())
						systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}

		/**
		 * return the List of record function EGLSystemWord with a return value
		 */
		public static List getRecordFunctionWordsWithReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = recordWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < recordWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (functionWord.hasReturnCode())
						systemWordNames.add(systemWord);
				}
			}
			return systemWordNames;
		}

		/**
		 * return the List of record function EGLSystemWord names
		 */
		public static List getRecordFunctionWordNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = recordWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < recordWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}

		/**
		 * return the List of record function EGLSystemWord names with no return value
		 */
		public static List getRecordFunctionWordNamesWithoutReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = recordWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < recordWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (!functionWord.hasReturnCode())
						systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}

		/**
		 * return the List of record function EGLSystemWord names with no return value
		 */
		public static List getRecordFunctionWordsWithoutReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = recordWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < recordWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (!functionWord.hasReturnCode())
						systemWordNames.add(systemWord);
				}
			}
			return systemWordNames;
		}

		/**
		 * return the List of library record EGLSystemWords
		 */
		public static List getLibraryRecordWords(String library) {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = recordWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < recordWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord.getLibrary().equals(library))
					systemWordNames.add(systemWord);
			}
			return systemWordNames;
		}

		/**
		 * Return the list of record system function names that are only valid for use within a page handler.
		 */
		public static List getPageHandlerRecordFunctionWordNames() {
			ArrayList systemWordNames = new ArrayList();
			Iterator iter = recordWords.keySet().iterator();
			while( iter.hasNext() ) {
				EGLSystemWord systemWord = (EGLSystemWord) recordWords.get( iter.next() );
				if( systemWord.isPageHandlerSystemFunction() ) {
					systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}

		/**
		 * return the List of record function EGLSystemWord names with no return value
		 */
		public static EGLSystemWord getEGLRecordWord(String key) {
			return (EGLSystemWord) recordWords.get(key);
		}

		/**
		 * return the List of record variable EGLSystemWord names
		 */
		public static List getRecordVariableNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = recordWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < recordWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable)
					systemWordNames.add(systemWord.getName());
			}
			return systemWordNames;
		}

		/**
		 * return the List of record variable EGLSystemWords
		 */
		public static List getRecordVariables() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = recordWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < recordWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable)
					systemWordNames.add(systemWord);
			}
			return systemWordNames;
		}

		/**
		 * return the List of record numeric variable EGLSystemWord names
		 */
		public static List getRecordNumericVariableNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = recordWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < recordWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable) {
					EGLSystemVariable systemVariable = (EGLSystemVariable) systemWord;
					if (systemVariable.isNumericVariable())
						systemWordNames.add(systemVariable.getName());
				}
			}
			return systemWordNames;
		}
	
//***********************
//  Report Lib
			/**
			 * return the list of array ReportLib EGLSystemWord objects
			 */
			public static List getArrayReportLibWords() {
				ArrayList systemWordsList = new ArrayList();
				Collection systemWordsValues = reportLibWords.values();
				Iterator iter = systemWordsValues.iterator();
				for (int i = 0; i < reportLibWords.size(); i++) {
					EGLSystemWord systemWord = (EGLSystemWord) iter.next();
					if (systemWord.isArrayWord())
						systemWordsList.add(systemWord);
				}
				return systemWordsList;
			}

			/**
			 * return the list of ReportLib EGLSystemWord objects
			 */
			public static List getReportLibWords() {
				ArrayList systemWordsList = new ArrayList(reportLibWords.size());
				Collection systemWordsValues = reportLibWords.values();
				Iterator iter = systemWordsValues.iterator();
				for (int i = 0; i < reportLibWords.size(); i++) {
					EGLSystemWord systemWord = (EGLSystemWord) iter.next();
					systemWordsList.add(systemWord);
				}
				return systemWordsList;
			}

			/**
			 * return the list of reportHandlerWords
			 */
			public static List getReportHandlerLibWords() {
				ArrayList systemWordsList = new ArrayList(reportHandlerWords.size());
				Collection systemWordsValues = reportHandlerWords.values();
				Iterator iter = systemWordsValues.iterator();
				for (int i = 0; i < reportHandlerWords.size(); i++) {
					EGLSystemWord systemWord = (EGLSystemWord) iter.next();
					systemWordsList.add(systemWord);
				}
				return systemWordsList;
			}

	
			public static Map getReportLibWordsAsMap()
			{
				return reportLibWords;
			}

			/**
			 * return the List of ReportLib EGLSystemWord names
			 */
			public static List getReportLibWordNames() {
				ArrayList systemWordNames = new ArrayList(reportLibWords.size());
				Collection systemWordsValues = reportLibWords.values();
				Iterator iter = systemWordsValues.iterator();
				for (int i = 0; i < reportLibWords.size(); i++) {
					EGLSystemWord systemWord = (EGLSystemWord) iter.next();
					systemWordNames.add(systemWord.getName());
				}
				return systemWordNames;
			}

			/**
			 * return the List of ReportLib EGLSystemWord names in lowercase
			 */
			public static List getReportLibWordNamesToLowerCase() {
				ArrayList systemWordNames = new ArrayList(reportLibWords.size());
				Collection systemWordsValues = reportLibWords.values();
				Iterator iter = systemWordsValues.iterator();
				for (int i = 0; i < reportLibWords.size(); i++) {
					EGLSystemWord systemWord = (EGLSystemWord) iter.next();
					systemWordNames.add(systemWord.getName().toLowerCase());
				}
				return systemWordNames;
			}

			/**
			 * return the List of ReportLib function EGLSystemWord names with a return value
			 */
			public static List getReportLibFunctionWordNamesWithReturnValue() {
				ArrayList systemWordNames = new ArrayList();
				Collection systemWordsValues = reportLibWords.values();
				Iterator iter = systemWordsValues.iterator();
				for (int i = 0; i < reportLibWords.size(); i++) {
					EGLSystemWord systemWord = (EGLSystemWord) iter.next();
					if (systemWord instanceof EGLSystemFunctionWord) {
						EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
						if (functionWord.hasReturnCode())
							systemWordNames.add(systemWord.getName());
					}
				}
				return systemWordNames;
			}

			/**
			 * return the List of ReportLib function EGLSystemWord with a return value
			 */
			public static List getReportLibFunctionWordsWithReturnValue() {
				ArrayList systemWordNames = new ArrayList();
				Collection systemWordsValues = reportLibWords.values();
				Iterator iter = systemWordsValues.iterator();
				for (int i = 0; i < reportLibWords.size(); i++) {
					EGLSystemWord systemWord = (EGLSystemWord) iter.next();
					if (systemWord instanceof EGLSystemFunctionWord) {
						EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
						if (functionWord.hasReturnCode())
							systemWordNames.add(systemWord);
					}
				}
				return systemWordNames;
			}

			/**
			 * return the List of ReportLib function EGLSystemWord names
			 */
			public static List getReportLibFunctionWordNames() {
				ArrayList systemWordNames = new ArrayList();
				Collection systemWordsValues = reportLibWords.values();
				Iterator iter = systemWordsValues.iterator();
				for (int i = 0; i < reportLibWords.size(); i++) {
					EGLSystemWord systemWord = (EGLSystemWord) iter.next();
					if (systemWord instanceof EGLSystemFunctionWord) {
						systemWordNames.add(systemWord.getName());
					}
				}
				return systemWordNames;
			}

			/**
			 * return the List of ReportLib function EGLSystemWord names with no return value
			 */
			public static List getReportLibFunctionWordNamesWithoutReturnValue() {
				ArrayList systemWordNames = new ArrayList();
				Collection systemWordsValues = reportLibWords.values();
				Iterator iter = systemWordsValues.iterator();
				for (int i = 0; i < reportLibWords.size(); i++) {
					EGLSystemWord systemWord = (EGLSystemWord) iter.next();
					if (systemWord instanceof EGLSystemFunctionWord) {
						EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
						if (!functionWord.hasReturnCode())
							systemWordNames.add(systemWord.getName());
					}
				}
				return systemWordNames;
			}

			/**
			 * return the List of ReportLib function EGLSystemWord names with no return value
			 */
			public static List getReportLibFunctionWordsWithoutReturnValue() {
				ArrayList systemWordNames = new ArrayList();
				Collection systemWordsValues = reportLibWords.values();
				Iterator iter = systemWordsValues.iterator();
				for (int i = 0; i < reportLibWords.size(); i++) {
					EGLSystemWord systemWord = (EGLSystemWord) iter.next();
					if (systemWord instanceof EGLSystemFunctionWord) {
						EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
						if (!functionWord.hasReturnCode())
							systemWordNames.add(systemWord);
					}
				}
				return systemWordNames;
			}

			/**
			 * return the List of ReportLib Dictionary EGLSystemWords
			 */
			public static List getLibraryReportLibWords(String library) {
				ArrayList systemWordNames = new ArrayList();
				Collection systemWordsValues = reportLibWords.values();
				Iterator iter = systemWordsValues.iterator();
				for (int i = 0; i < reportLibWords.size(); i++) {
					EGLSystemWord systemWord = (EGLSystemWord) iter.next();
					if (systemWord.getLibrary().equals(library))
						systemWordNames.add(systemWord);
				}
				return systemWordNames;
			}

			/**
			 * Return the list of ReportLib system function names that are only valid for use within a page handler.
			 */
			public static List getPageHandlerReportLibFunctionWordNames() {
				ArrayList systemWordNames = new ArrayList();
				Iterator iter = reportLibWords.keySet().iterator();
				while( iter.hasNext() ) {
					EGLSystemWord systemWord = (EGLSystemWord) reportLibWords.get( iter.next() );
					if( systemWord.isPageHandlerSystemFunction() ) {
						systemWordNames.add(systemWord.getName());
					}
				}
				return systemWordNames;
			}

			/**
			 * return the List of ReportLib function EGLSystemWord names with no return value
			 */
			public static EGLSystemWord getEGLReportLibWord(String key) {
				return (EGLSystemWord) reportLibWords.get(key);
			}

			/**
			 * return the List of ReportLib variable EGLSystemWord names
			 */
			public static List getReportLibVariableNames() {
				ArrayList systemWordNames = new ArrayList();
				Collection systemWordsValues = reportLibWords.values();
				Iterator iter = systemWordsValues.iterator();
				for (int i = 0; i < reportLibWords.size(); i++) {
					EGLSystemWord systemWord = (EGLSystemWord) iter.next();
					if (systemWord instanceof EGLSystemVariable)
						systemWordNames.add(systemWord.getName());
				}
				return systemWordNames;
			}

			/**
			 * return the List of ReportLib variable EGLSystemWords
			 */
			public static List getReportLibVariables() {
				ArrayList systemWordNames = new ArrayList();
				Collection systemWordsValues = reportLibWords.values();
				Iterator iter = systemWordsValues.iterator();
				for (int i = 0; i < reportLibWords.size(); i++) {
					EGLSystemWord systemWord = (EGLSystemWord) iter.next();
					if (systemWord instanceof EGLSystemVariable)
						systemWordNames.add(systemWord);
				}
				return systemWordNames;
			}

			/**
			 * return the List of ReportLib numeric variable EGLSystemWord names
			 */
			public static List getReportLibNumericVariableNames() {
				ArrayList systemWordNames = new ArrayList();
				Collection systemWordsValues = reportLibWords.values();
				Iterator iter = systemWordsValues.iterator();
				for (int i = 0; i < reportLibWords.size(); i++) {
					EGLSystemWord systemWord = (EGLSystemWord) iter.next();
					if (systemWord instanceof EGLSystemVariable) {
						EGLSystemVariable systemVariable = (EGLSystemVariable) systemWord;
						if (systemVariable.isNumericVariable())
							systemWordNames.add(systemVariable.getName());
					}
				}
				return systemWordNames;
			}

//***********************
//  Converse Lib
		/**
		 * return the list of array ConverseLib EGLSystemWord objects
		 */
		public static List getArrayConverseLibWords() {
			ArrayList systemWordsList = new ArrayList();
			Collection systemWordsValues = converseLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < converseLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord.isArrayWord())
					systemWordsList.add(systemWord);
			}
			return systemWordsList;
		}
		
		/**
		 * return the list of converseLib EGLSystemWord objects
		 */
		public static List getConverseLibWords() {
			ArrayList systemWordsList = new ArrayList(converseLibWords.size());
			Collection systemWordsValues = converseLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < converseLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordsList.add(systemWord);
			}
			return systemWordsList;
		}		
		
		public static Map getConverseLibWordsAsMap()
		{
			return converseLibWords;
		}
		
		/**
		 * return the List of ConverseLib EGLSystemWord names
		 */
		public static List getConverseLibWordNames() {
			ArrayList systemWordNames = new ArrayList(converseLibWords.size());
			Collection systemWordsValues = converseLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < converseLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordNames.add(systemWord.getName());
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConverseLib EGLSystemWord names in lowercase
		 */
		public static List getConverseLibWordNamesToLowerCase() {
			ArrayList systemWordNames = new ArrayList(converseLibWords.size());
			Collection systemWordsValues = converseLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < converseLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordNames.add(systemWord.getName().toLowerCase());
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConverseLib function EGLSystemWord names with a return value
		 */
		public static List getConverseLibFunctionWordNamesWithReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = converseLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < converseLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (functionWord.hasReturnCode())
						systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConverseLib function EGLSystemWord with a return value
		 */
		public static List getConverseLibFunctionWordsWithReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = converseLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < converseLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (functionWord.hasReturnCode())
						systemWordNames.add(systemWord);
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConverseLib function EGLSystemWord names
		 */
		public static List getConverseLibFunctionWordNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = converseLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < converseLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConverseLib function EGLSystemWord names with no return value
		 */
		public static List getConverseLibFunctionWordNamesWithoutReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = converseLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < converseLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (!functionWord.hasReturnCode())
						systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConverseLib function EGLSystemWord names with no return value
		 */
		public static List getConverseLibFunctionWordsWithoutReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = converseLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < converseLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (!functionWord.hasReturnCode())
						systemWordNames.add(systemWord);
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConverseLib Dictionary EGLSystemWords
		 */
		public static List getLibraryConverseLibWords(String library) {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = converseLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < converseLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord.getLibrary().equals(library))
					systemWordNames.add(systemWord);
			}
			return systemWordNames;
		}
		
		/**
		 * Return the list of ConverseLib system function names that are only valid for use within a page handler.
		 */
		public static List getPageHandlerConverseLibFunctionWordNames() {
			ArrayList systemWordNames = new ArrayList();
			Iterator iter = converseLibWords.keySet().iterator();
			while( iter.hasNext() ) {
				EGLSystemWord systemWord = (EGLSystemWord) converseLibWords.get( iter.next() );
				if( systemWord.isPageHandlerSystemFunction() ) {
					systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConverseLib function EGLSystemWord names with no return value
		 */
		public static EGLSystemWord getEGLConverseLibWord(String key) {
			return (EGLSystemWord) converseLibWords.get(key);
		}
		
		/**
		 * return the List of ConverseLib variable EGLSystemWord names
		 */
		public static List getConverseLibVariableNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = converseLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < converseLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable)
					systemWordNames.add(systemWord.getName());
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConverseLib variable EGLSystemWords
		 */
		public static List getConverseLibVariables() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = converseLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < converseLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable)
					systemWordNames.add(systemWord);
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConverseLib numeric variable EGLSystemWord names
		 */
		public static List getConverseLibNumericVariableNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = converseLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < converseLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable) {
					EGLSystemVariable systemVariable = (EGLSystemVariable) systemWord;
					if (systemVariable.isNumericVariable())
						systemWordNames.add(systemVariable.getName());
				}
			}
			return systemWordNames;
		}	
		
//***********************
//  Lob Lib
		/**
		 * return the list of array LobLib EGLSystemWord objects
		 */
		public static List getArrayLobLibWords() {
			ArrayList systemWordsList = new ArrayList();
			Collection systemWordsValues = lobLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < lobLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord.isArrayWord())
					systemWordsList.add(systemWord);
			}
			return systemWordsList;
		}
		
		/**
		 * return the list of LobLib EGLSystemWord objects
		 */
		public static List getLobLibWords() {
			ArrayList systemWordsList = new ArrayList(lobLibWords.size());
			Collection systemWordsValues = lobLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < lobLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordsList.add(systemWord);
			}
			return systemWordsList;
		}		
		
		public static Map getLobLibWordsAsMap()
		{
			return lobLibWords;
		}
		
		/**
		 * return the List of LobLib EGLSystemWord names
		 */
		public static List getLobLibWordNames() {
			ArrayList systemWordNames = new ArrayList(lobLibWords.size());
			Collection systemWordsValues = lobLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < lobLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordNames.add(systemWord.getName());
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of LobLib EGLSystemWord names in lowercase
		 */
		public static List getLobLibWordNamesToLowerCase() {
			ArrayList systemWordNames = new ArrayList(lobLibWords.size());
			Collection systemWordsValues = lobLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < lobLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordNames.add(systemWord.getName().toLowerCase());
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of LobLib function EGLSystemWord names with a return value
		 */
		public static List getLobLibFunctionWordNamesWithReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = lobLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < lobLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (functionWord.hasReturnCode())
						systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of LobLib function EGLSystemWord with a return value
		 */
		public static List getLobLibFunctionWordsWithReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = lobLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < lobLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (functionWord.hasReturnCode())
						systemWordNames.add(systemWord);
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of LobLib function EGLSystemWord names
		 */
		public static List getLobLibFunctionWordNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = lobLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < lobLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of LobLib function EGLSystemWord names with no return value
		 */
		public static List getLobLibFunctionWordNamesWithoutReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = lobLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < lobLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (!functionWord.hasReturnCode())
						systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of LobLib function EGLSystemWord names with no return value
		 */
		public static List getLobLibFunctionWordsWithoutReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = lobLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < lobLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (!functionWord.hasReturnCode())
						systemWordNames.add(systemWord);
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of LobLib Dictionary EGLSystemWords
		 */
		public static List getLibraryLobLibWords(String library) {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = lobLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < lobLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord.getLibrary().equals(library))
					systemWordNames.add(systemWord);
			}
			return systemWordNames;
		}
		
		/**
		 * Return the list of LobLib system function names that are only valid for use within a page handler.
		 */
		public static List getPageHandlerLobLibFunctionWordNames() {
			ArrayList systemWordNames = new ArrayList();
			Iterator iter = lobLibWords.keySet().iterator();
			while( iter.hasNext() ) {
				EGLSystemWord systemWord = (EGLSystemWord) lobLibWords.get( iter.next() );
				if( systemWord.isPageHandlerSystemFunction() ) {
					systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of LobLib function EGLSystemWord names with no return value
		 */
		public static EGLSystemWord getEGLLobLibWord(String key) {
			return (EGLSystemWord) lobLibWords.get(key);
		}
		
		/**
		 * return the List of LobLib variable EGLSystemWord names
		 */
		public static List getLobLibVariableNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = lobLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < lobLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable)
					systemWordNames.add(systemWord.getName());
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of LobLib variable EGLSystemWords
		 */
		public static List getLobLibVariables() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = lobLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < lobLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable)
					systemWordNames.add(systemWord);
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of LobLib numeric variable EGLSystemWord names
		 */
		public static List getLobLibNumericVariableNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = lobLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < lobLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable) {
					EGLSystemVariable systemVariable = (EGLSystemVariable) systemWord;
					if (systemVariable.isNumericVariable())
						systemWordNames.add(systemVariable.getName());
				}
			}
			return systemWordNames;
		}	

//***********************
//	  VG Lib
		/**
		 * return the list of array VGLib EGLSystemWord objects
		 */
		public static List getArrayVGLibWords() {
			ArrayList systemWordsList = new ArrayList();
			Collection systemWordsValues = vgLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < vgLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord.isArrayWord())
					systemWordsList.add(systemWord);
			}
			return systemWordsList;
		}

		/**
		 * return the list of VGLib EGLSystemWord objects
		 */
		public static List getVGLibWords() {
			ArrayList systemWordsList = new ArrayList(vgLibWords.size());
			Collection systemWordsValues = vgLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < vgLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordsList.add(systemWord);
			}
			return systemWordsList;
		}		
		
		public static Map getVGLibWordsAsMap()
		{
			return vgLibWords;
		}
		
		/**
		 * return the List of VGLib EGLSystemWord names
		 */
		public static List getVGLibWordNames() {
			ArrayList systemWordNames = new ArrayList(vgLibWords.size());
			Collection systemWordsValues = vgLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < vgLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordNames.add(systemWord.getName());
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of VGLib EGLSystemWord names in lowercase
		 */
		public static List getVGLibWordNamesToLowerCase() {
			ArrayList systemWordNames = new ArrayList(vgLibWords.size());
			Collection systemWordsValues = vgLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < vgLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordNames.add(systemWord.getName().toLowerCase());
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of VGLib function EGLSystemWord names with a return value
		 */
		public static List getVGLibFunctionWordNamesWithReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = vgLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < vgLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (functionWord.hasReturnCode())
						systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of VGLib function EGLSystemWord with a return value
		 */
		public static List getVGLibFunctionWordsWithReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = vgLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < vgLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (functionWord.hasReturnCode())
						systemWordNames.add(systemWord);
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of VGLib function EGLSystemWord names
		 */
		public static List getVGLibFunctionWordNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = vgLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < vgLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of VGLib function EGLSystemWord names with no return value
		 */
		public static List getVGLibFunctionWordNamesWithoutReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = vgLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < vgLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (!functionWord.hasReturnCode())
						systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of VGLib function EGLSystemWord names with no return value
		 */
		public static List getVGLibFunctionWordsWithoutReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = vgLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < vgLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (!functionWord.hasReturnCode())
						systemWordNames.add(systemWord);
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of VGLib Dictionary EGLSystemWords
		 */
		public static List getLibraryVGLibWords(String library) {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = vgLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < vgLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord.getLibrary().equals(library))
					systemWordNames.add(systemWord);
			}
			return systemWordNames;
		}
		
		/**
		 * Return the list of VGLib system function names that are only valid for use within a page handler.
		 */
		public static List getPageHandlerVGLibFunctionWordNames() {
			ArrayList systemWordNames = new ArrayList();
			Iterator iter = vgLibWords.keySet().iterator();
			while( iter.hasNext() ) {
				EGLSystemWord systemWord = (EGLSystemWord) vgLibWords.get( iter.next() );
				if( systemWord.isPageHandlerSystemFunction() ) {
					systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of VGLib function EGLSystemWord names with no return value
		 */
		public static EGLSystemWord getEGLVGLibWord(String key) {
			return (EGLSystemWord) vgLibWords.get(key);
		}
		
		/**
		 * return the List of VGLib variable EGLSystemWord names
		 */
		public static List getVGLibVariableNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = vgLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < vgLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable)
					systemWordNames.add(systemWord.getName());
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of VGLib variable EGLSystemWords
		 */
		public static List getVGLibVariables() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = vgLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < vgLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable)
					systemWordNames.add(systemWord);
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of VGLib numeric variable EGLSystemWord names
		 */
		public static List getVGLibNumericVariableNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = vgLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < vgLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable) {
					EGLSystemVariable systemVariable = (EGLSystemVariable) systemWord;
					if (systemVariable.isNumericVariable())
						systemWordNames.add(systemVariable.getName());
				}
			}
			return systemWordNames;
		}	

//***********************
//	Java Lib
		/**
		 * return the list of array JavaLib EGLSystemWord objects
		 */
		public static List getArrayJavaLibWords() {
			ArrayList systemWordsList = new ArrayList();
			Collection systemWordsValues = javaLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < javaLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord.isArrayWord())
					systemWordsList.add(systemWord);
			}
			return systemWordsList;
		}
		
		/**
		 * return the list of JavaLib EGLSystemWord objects
		 */
		public static List getJavaLibWords() {
			ArrayList systemWordsList = new ArrayList(javaLibWords.size());
			Collection systemWordsValues = javaLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < javaLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordsList.add(systemWord);
			}
			return systemWordsList;
		}		
		
		public static Map getJavaLibWordsAsMap()
		{
			return javaLibWords;
		}
		
		/**
		 * return the List of JavaLib EGLSystemWord names
		 */
		public static List getJavaLibWordNames() {
			ArrayList systemWordNames = new ArrayList(javaLibWords.size());
			Collection systemWordsValues = javaLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < javaLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordNames.add(systemWord.getName());
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of JavaLib EGLSystemWord names in lowercase
		 */
		public static List getJavaLibWordNamesToLowerCase() {
			ArrayList systemWordNames = new ArrayList(javaLibWords.size());
			Collection systemWordsValues = javaLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < javaLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordNames.add(systemWord.getName().toLowerCase());
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of JavaLib function EGLSystemWord names with a return value
		 */
		public static List getJavaLibFunctionWordNamesWithReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = javaLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < javaLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (functionWord.hasReturnCode())
						systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of JavaLib function EGLSystemWord with a return value
		 */
		public static List getJavaLibFunctionWordsWithReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = javaLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < javaLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (functionWord.hasReturnCode())
						systemWordNames.add(systemWord);
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of JavaLib function EGLSystemWord names
		 */
		public static List getJavaLibFunctionWordNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = javaLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < javaLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of JavaLib function EGLSystemWord names with no return value
		 */
		public static List getJavaLibFunctionWordNamesWithoutReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = javaLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < javaLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (!functionWord.hasReturnCode())
						systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of JavaLib function EGLSystemWord names with no return value
		 */
		public static List getJavaLibFunctionWordsWithoutReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = javaLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < javaLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (!functionWord.hasReturnCode())
						systemWordNames.add(systemWord);
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of JavaLib Dictionary EGLSystemWords
		 */
		public static List getLibraryJavaLibWords(String library) {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = javaLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < javaLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord.getLibrary().equals(library))
					systemWordNames.add(systemWord);
			}
			return systemWordNames;
		}
		
		/**
		 * Return the list of JavaLib system function names that are only valid for use within a page handler.
		 */
		public static List getPageHandlerJavaLibFunctionWordNames() {
			ArrayList systemWordNames = new ArrayList();
			Iterator iter = javaLibWords.keySet().iterator();
			while( iter.hasNext() ) {
				EGLSystemWord systemWord = (EGLSystemWord) javaLibWords.get( iter.next() );
				if( systemWord.isPageHandlerSystemFunction() ) {
					systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of JavaLib function EGLSystemWord names with no return value
		 */
		public static EGLSystemWord getEGLJavaLibWord(String key) {
			return (EGLSystemWord) javaLibWords.get(key);
		}
		
		/**
		 * return the List of JavaLib variable EGLSystemWord names
		 */
		public static List getJavaLibVariableNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = javaLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < javaLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable)
					systemWordNames.add(systemWord.getName());
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of JavaLib variable EGLSystemWords
		 */
		public static List getJavaLibVariables() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = javaLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < javaLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable)
					systemWordNames.add(systemWord);
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of JavaLib numeric variable EGLSystemWord names
		 */
		public static List getJavaLibNumericVariableNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = javaLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < javaLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable) {
					EGLSystemVariable systemVariable = (EGLSystemVariable) systemWord;
					if (systemVariable.isNumericVariable())
						systemWordNames.add(systemVariable.getName());
				}
			}
			return systemWordNames;
		}	
		
//***********************
//	  DateTime Lib
		/**
		 * return the list of array DateTimeLib EGLSystemWord objects
		 */
		public static List getArrayDateTimeLibWords() {
			ArrayList systemWordsList = new ArrayList();
			Collection systemWordsValues = dateTimeLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < dateTimeLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord.isArrayWord())
					systemWordsList.add(systemWord);
			}
			return systemWordsList;
		}

		/**
		 * return the list of DateTimeLib EGLSystemWord objects
		 */
		public static List getDateTimeLibWords() {
			ArrayList systemWordsList = new ArrayList(dateTimeLibWords.size());
			Collection systemWordsValues = dateTimeLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < dateTimeLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordsList.add(systemWord);
			}
			return systemWordsList;
		}
		
		
		public static Map getDateTimeLibWordsAsMap()
		{
			return dateTimeLibWords;
		}
		
		/**
		 * return the List of DateTimeLib EGLSystemWord names
		 */
		public static List getDateTimeLibWordNames() {
			ArrayList systemWordNames = new ArrayList(dateTimeLibWords.size());
			Collection systemWordsValues = dateTimeLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < dateTimeLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordNames.add(systemWord.getName());
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of DateTimeLib EGLSystemWord names in lowercase
		 */
		public static List getDateTimeLibWordNamesToLowerCase() {
			ArrayList systemWordNames = new ArrayList(dateTimeLibWords.size());
			Collection systemWordsValues = dateTimeLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < dateTimeLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordNames.add(systemWord.getName().toLowerCase());
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of DateTimeLib function EGLSystemWord names with a return value
		 */
		public static List getDateTimeLibFunctionWordNamesWithReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = dateTimeLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < dateTimeLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (functionWord.hasReturnCode())
						systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of DateTimeLib function EGLSystemWord with a return value
		 */
		public static List getDateTimeLibFunctionWordsWithReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = dateTimeLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < dateTimeLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (functionWord.hasReturnCode())
						systemWordNames.add(systemWord);
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of DateTimeLib function EGLSystemWord names
		 */
		public static List getDateTimeLibFunctionWordNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = dateTimeLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < dateTimeLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of DateTimeLib function EGLSystemWord names with no return value
		 */
		public static List getDateTimeLibFunctionWordNamesWithoutReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = dateTimeLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < dateTimeLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (!functionWord.hasReturnCode())
						systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of DateTimeLib function EGLSystemWord names with no return value
		 */
		public static List getDateTimeLibFunctionWordsWithoutReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = dateTimeLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < dateTimeLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (!functionWord.hasReturnCode())
						systemWordNames.add(systemWord);
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of DateTimeLib Dictionary EGLSystemWords
		 */
		public static List getLibraryDateTimeLibWords(String library) {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = dateTimeLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < dateTimeLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord.getLibrary().equals(library))
					systemWordNames.add(systemWord);
			}
			return systemWordNames;
		}
		
		/**
		 * Return the list of DateTimeLib system function names that are only valid for use within a page handler.
		 */
		public static List getPageHandlerDateTimeLibFunctionWordNames() {
			ArrayList systemWordNames = new ArrayList();
			Iterator iter = dateTimeLibWords.keySet().iterator();
			while( iter.hasNext() ) {
				EGLSystemWord systemWord = (EGLSystemWord) dateTimeLibWords.get( iter.next() );
				if( systemWord.isPageHandlerSystemFunction() ) {
					systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of DateTimeLib function EGLSystemWord names with no return value
		 */
		public static EGLSystemWord getEGLDateTimeLibWord(String key) {
			return (EGLSystemWord) dateTimeLibWords.get(key);
		}
		
		/**
		 * return the List of DateTimeLib variable EGLSystemWord names
		 */
		public static List getDateTimeLibVariableNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = dateTimeLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < dateTimeLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable)
					systemWordNames.add(systemWord.getName());
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of DateTimeLib variable EGLSystemWords
		 */
		public static List getDateTimeLibVariables() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = dateTimeLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < dateTimeLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable)
					systemWordNames.add(systemWord);
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of DateTimeLib numeric variable EGLSystemWord names
		 */
		public static List getDateTimeLibNumericVariableNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = dateTimeLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < dateTimeLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable) {
					EGLSystemVariable systemVariable = (EGLSystemVariable) systemWord;
					if (systemVariable.isNumericVariable())
						systemWordNames.add(systemVariable.getName());
				}
			}
			return systemWordNames;
		}	
		
//		***********************
//		  Service Lib
		
		/**
		 * return the list of serviceLib EGLSystemWord words
		 */
		public static List getServiceLibWords() {
			ArrayList systemWordsList = new ArrayList(serviceLibWords.size());
			Collection systemWordsValues = serviceLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < serviceLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordsList.add(systemWord);
			}
			return systemWordsList;
		}		
		
//***********************
//  J2EE Lib
		/**
		 * return the list of array J2EELib EGLSystemWord objects
		 */
		public static List getArrayJ2EELibWords() {
			ArrayList systemWordsList = new ArrayList();
			Collection systemWordsValues = j2eeLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < j2eeLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord.isArrayWord())
					systemWordsList.add(systemWord);
			}
			return systemWordsList;
		}
		
		/**
		 * return the list of J2EELib EGLSystemWord objects
		 */
		public static List getJ2EELibWords() {
			ArrayList systemWordsList = new ArrayList(j2eeLibWords.size());
			Collection systemWordsValues = j2eeLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < j2eeLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordsList.add(systemWord);
			}
			return systemWordsList;
		}		
		
		
		public static Map getJ2EELibWordsAsMap()
		{
			return j2eeLibWords;
		}
		
		/**
		 * return the List of J2EELib EGLSystemWord names
		 */
		public static List getJ2EELibWordNames() {
			ArrayList systemWordNames = new ArrayList(j2eeLibWords.size());
			Collection systemWordsValues = j2eeLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < j2eeLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordNames.add(systemWord.getName());
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of J2EELib EGLSystemWord names in lowercase
		 */
		public static List getJ2EELibWordNamesToLowerCase() {
			ArrayList systemWordNames = new ArrayList(j2eeLibWords.size());
			Collection systemWordsValues = j2eeLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < j2eeLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordNames.add(systemWord.getName().toLowerCase());
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of J2EELib function EGLSystemWord names with a return value
		 */
		public static List getJ2EELibFunctionWordNamesWithReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = j2eeLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < j2eeLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (functionWord.hasReturnCode())
						systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of J2EELib function EGLSystemWord with a return value
		 */
		public static List getJ2EELibFunctionWordsWithReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = j2eeLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < j2eeLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (functionWord.hasReturnCode())
						systemWordNames.add(systemWord);
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of J2EELib function EGLSystemWord names
		 */
		public static List getJ2EELibFunctionWordNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = j2eeLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < j2eeLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of J2EELib function EGLSystemWord names with no return value
		 */
		public static List getJ2EELibFunctionWordNamesWithoutReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = j2eeLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < j2eeLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (!functionWord.hasReturnCode())
						systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of J2EELib function EGLSystemWord names with no return value
		 */
		public static List getJ2EELibFunctionWordsWithoutReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = j2eeLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < j2eeLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (!functionWord.hasReturnCode())
						systemWordNames.add(systemWord);
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of J2EELib Dictionary EGLSystemWords
		 */
		public static List getLibraryJ2EELibWords(String library) {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = j2eeLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < j2eeLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord.getLibrary().equals(library))
					systemWordNames.add(systemWord);
			}
			return systemWordNames;
		}
		
		/**
		 * Return the list of J2EELib system function names that are only valid for use within a page handler.
		 */
		public static List getPageHandlerJ2EELibFunctionWordNames() {
			ArrayList systemWordNames = new ArrayList();
			Iterator iter = j2eeLibWords.keySet().iterator();
			while( iter.hasNext() ) {
				EGLSystemWord systemWord = (EGLSystemWord) j2eeLibWords.get( iter.next() );
				if( systemWord.isPageHandlerSystemFunction() ) {
					systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of J2EELib function EGLSystemWord names with no return value
		 */
		public static EGLSystemWord getEGLJ2EELibWord(String key) {
			return (EGLSystemWord) j2eeLibWords.get(key);
		}
		
		/**
		 * return the List of J2EELib variable EGLSystemWord names
		 */
		public static List getJ2EELibVariableNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = j2eeLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < j2eeLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable)
					systemWordNames.add(systemWord.getName());
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of J2EELib variable EGLSystemWords
		 */
		public static List getJ2EELibVariables() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = j2eeLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < j2eeLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable)
					systemWordNames.add(systemWord);
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of J2EELib numeric variable EGLSystemWord names
		 */
		public static List getJ2EELibNumericVariableNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = j2eeLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < j2eeLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable) {
					EGLSystemVariable systemVariable = (EGLSystemVariable) systemWord;
					if (systemVariable.isNumericVariable())
						systemWordNames.add(systemVariable.getName());
				}
			}
			return systemWordNames;
		}	
	
//***********************
//  Converse Var
		/**
		 * return the list of array ConverseVar EGLSystemWord objects
		 */
		public static List getArrayConverseVarWords() {
			ArrayList systemWordsList = new ArrayList();
			Collection systemWordsValues = converseVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < converseVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord.isArrayWord())
					systemWordsList.add(systemWord);
			}
			return systemWordsList;
		}
		
		/**
		 * return the list of ConverseVar EGLSystemWord objects
		 */
		public static List getConverseVarWords() {
			ArrayList systemWordsList = new ArrayList(converseVarWords.size());
			Collection systemWordsValues = converseVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < converseVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordsList.add(systemWord);
			}
			return systemWordsList;
		}		
		
		
		public static Map getConverseVarWordsAsMap()
		{
			return converseVarWords;
		}
		
		/**
		 * return the List of ConsoleVar EGLSystemWord names
		 */
		public static List getConverseVarWordNames() {
			ArrayList systemWordNames = new ArrayList(converseVarWords.size());
			Collection systemWordsValues = converseVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < converseVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordNames.add(systemWord.getName());
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConverseVar EGLSystemWord names in lowercase
		 */
		public static List getConverseVarWordNamesToLowerCase() {
			ArrayList systemWordNames = new ArrayList(converseVarWords.size());
			Collection systemWordsValues = converseVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < converseVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordNames.add(systemWord.getName().toLowerCase());
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConverseVar function EGLSystemWord names with a return value
		 */
		public static List getConverseVarFunctionWordNamesWithReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = converseVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < converseVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (functionWord.hasReturnCode())
						systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConverseVar function EGLSystemWord with a return value
		 */
		public static List getConverseVarFunctionWordsWithReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = converseVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < converseVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (functionWord.hasReturnCode())
						systemWordNames.add(systemWord);
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConverseVar function EGLSystemWord names
		 */
		public static List getConverseVarFunctionWordNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = converseVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < converseVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConverseVar function EGLSystemWord names with no return value
		 */
		public static List getConverseVarFunctionWordNamesWithoutReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = converseVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < converseVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (!functionWord.hasReturnCode())
						systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConverseVar function EGLSystemWord names with no return value
		 */
		public static List getConverseVarFunctionWordsWithoutReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = converseVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < converseVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (!functionWord.hasReturnCode())
						systemWordNames.add(systemWord);
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConverseVar Dictionary EGLSystemWords
		 */
		public static List getLibraryConverseVarWords(String library) {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = converseVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < converseVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord.getLibrary().equals(library))
					systemWordNames.add(systemWord);
			}
			return systemWordNames;
		}
		
		/**
		 * Return the list of ConverseVar system function names that are only valid for use within a page handler.
		 */
		public static List getPageHandlerConsoleConverseVarFunctionWordNames() {
			ArrayList systemWordNames = new ArrayList();
			Iterator iter = converseVarWords.keySet().iterator();
			while( iter.hasNext() ) {
				EGLSystemWord systemWord = (EGLSystemWord) converseVarWords.get( iter.next() );
				if( systemWord.isPageHandlerSystemFunction() ) {
					systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConverseVar function EGLSystemWord names with no return value
		 */
		public static EGLSystemWord getEGLConverseVarWord(String key) {
			return (EGLSystemWord) converseVarWords.get(key);
		}
		
		/**
		 * return the List of ConverseVar variable EGLSystemWord names
		 */
		public static List getConverseVarVariableNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = converseVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < converseVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable)
					systemWordNames.add(systemWord.getName());
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConverseVar variable EGLSystemWords
		 */
		public static List getConverseVarVariables() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = converseVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < converseVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable)
					systemWordNames.add(systemWord);
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of ConverseVar numeric variable EGLSystemWord names
		 */
		public static List getConverseVarNumericVariableNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = converseVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < converseVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable) {
					EGLSystemVariable systemVariable = (EGLSystemVariable) systemWord;
					if (systemVariable.isNumericVariable())
						systemWordNames.add(systemVariable.getName());
				}
			}
			return systemWordNames;
		}
		
//***********************
//  VG Var
		/**
		 * return the list of array VGVar EGLSystemWord objects
		 */
		public static List getArrayVGVarWordsWords() {
			ArrayList systemWordsList = new ArrayList();
			Collection systemWordsValues = vgVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < vgVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord.isArrayWord())
					systemWordsList.add(systemWord);
			}
			return systemWordsList;
		}
		
		/**
		 * return the list of VGVar EGLSystemWord objects
		 */
		public static List getVGVarWords() {
			ArrayList systemWordsList = new ArrayList(vgVarWords.size());
			Collection systemWordsValues = vgVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < vgVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordsList.add(systemWord);
			}
			return systemWordsList;
		}		
		
		
		public static Map getVGVarWordsWordsAsMap()
		{
			return vgVarWords;
		}
		
		/**
		 * return the List of VGVar EGLSystemWord names
		 */
		public static List getVGVarWordsWordNames() {
			ArrayList systemWordNames = new ArrayList(vgVarWords.size());
			Collection systemWordsValues = vgVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < vgVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordNames.add(systemWord.getName());
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of VGVar EGLSystemWord names in lowercase
		 */
		public static List getVGVarWordNamesToLowerCase() {
			ArrayList systemWordNames = new ArrayList(vgVarWords.size());
			Collection systemWordsValues = vgVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < vgVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordNames.add(systemWord.getName().toLowerCase());
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of VGVar function EGLSystemWord names with a return value
		 */
		public static List getVGVarFunctionWordNamesWithReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = vgVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < vgVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (functionWord.hasReturnCode())
						systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of VGVar function EGLSystemWord with a return value
		 */
		public static List getVGVarFunctionWordsWithReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = vgVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < vgVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (functionWord.hasReturnCode())
						systemWordNames.add(systemWord);
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of VGVar function EGLSystemWord names
		 */
		public static List getVGVarFunctionWordNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = vgVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < vgVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of VGVar function EGLSystemWord names with no return value
		 */
		public static List getVGVarFunctionWordNamesWithoutReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = vgVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < vgVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (!functionWord.hasReturnCode())
						systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of VGVar function EGLSystemWord names with no return value
		 */
		public static List getVGVarFunctionWordsWithoutReturnValue() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = vgVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < vgVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (!functionWord.hasReturnCode())
						systemWordNames.add(systemWord);
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of VGVar Dictionary EGLSystemWords
		 */
		public static List getLibraryVGVarWords(String library) {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = vgVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < vgVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord.getLibrary().equals(library))
					systemWordNames.add(systemWord);
			}
			return systemWordNames;
		}
		
		/**
		 * Return the list of VGVar system function names that are only valid for use within a page handler.
		 */
		public static List getPageHandlerVGVarFunctionWordNames() {
			ArrayList systemWordNames = new ArrayList();
			Iterator iter = vgVarWords.keySet().iterator();
			while( iter.hasNext() ) {
				EGLSystemWord systemWord = (EGLSystemWord) vgVarWords.get( iter.next() );
				if( systemWord.isPageHandlerSystemFunction() ) {
					systemWordNames.add(systemWord.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of VGVar function EGLSystemWord names with no return value
		 */
		public static EGLSystemWord getEGLVGVarWord(String key) {
			return (EGLSystemWord) vgVarWords.get(key);
		}
		
		/**
		 * return the List of VGVar variable EGLSystemWord names
		 */
		public static List getVGVarVariableNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = vgVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < vgVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable)
					systemWordNames.add(systemWord.getName());
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of VGVar variable EGLSystemWords
		 */
		public static List getVGVarVariables() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = vgVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < vgVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable)
					systemWordNames.add(systemWord);
			}
			return systemWordNames;
		}
		
		/**
		 * return the List of VGVar numeric variable EGLSystemWord names
		 */
		public static List getVGVarNumericVariableNames() {
			ArrayList systemWordNames = new ArrayList();
			Collection systemWordsValues = vgVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < vgVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable) {
					EGLSystemVariable systemVariable = (EGLSystemVariable) systemWord;
					if (systemVariable.isNumericVariable())
						systemWordNames.add(systemVariable.getName());
				}
			}
			return systemWordNames;
		}
		
		/**
		 * return the list of VGLib EGLSystemWord objects
		 */
		public static List getDLILibWords() {
			ArrayList systemWordsList = new ArrayList(dliLibWords.size());
			Collection systemWordsValues = dliLibWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < dliLibWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordsList.add(systemWord);
			}
			return systemWordsList;
		}		
		
		/**
		 * return the list of VGVar EGLSystemWord objects
		 */
		public static List getDLIVarWords() {
			ArrayList systemWordsList = new ArrayList(dliVarWords.size());
			Collection systemWordsValues = dliVarWords.values();
			Iterator iter = systemWordsValues.iterator();
			for (int i = 0; i < dliVarWords.size(); i++) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordsList.add(systemWord);
			}
			return systemWordsList;
		}		
	
//****************
//    system words

	/**
	 * return the list of array EGLSystemWord objects
	 */
	public static List getArraySystemWords() {
		ArrayList systemWordsList = new ArrayList();
		Collection wordTreeMaps = systemWords.values();
		Iterator mapsIter = wordTreeMaps.iterator();
		for (int jj = 0; jj < wordTreeMaps.size(); jj++) {
			Collection systemWordsValues = ((TreeMap)mapsIter.next()).values();
			Iterator iter = systemWordsValues.iterator();
			while (iter.hasNext()) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord.isArrayWord())
					systemWordsList.add(systemWord);
			}
		}

		return systemWordsList;
	}

	/**
	 * return the list of EGLSystemWord objects
	 */
	public static List getSystemWords() {
		ArrayList systemWordsList = new ArrayList();
		Collection wordTreeMaps = systemWords.values();
		Iterator mapsIter = wordTreeMaps.iterator();
		for (int jj = 0; jj < wordTreeMaps.size(); jj++) {
			Collection systemWordsValues = ((TreeMap)mapsIter.next()).values();
			Iterator iter = systemWordsValues.iterator();
			while (iter.hasNext()) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordsList.add(systemWord);
			}
		}

		return systemWordsList;
	}	
//?????	
	public static Map getSystemWordsAsMap()
	{
		return systemWords;
	}

	/**
	 * return the List of EGLSystemWord names
	 */
	public static List getSystemWordNames() {
		ArrayList systemWordNames = new ArrayList();
		Collection wordTreeMaps = systemWords.values();
		Iterator mapsIter = wordTreeMaps.iterator();
		for (int jj = 0; jj < wordTreeMaps.size(); jj++) {
			Collection systemWordsValues = ((TreeMap)mapsIter.next()).values();
			Iterator iter = systemWordsValues.iterator();
			while (iter.hasNext()) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordNames.add(systemWord.getName());
			}
		}

		return systemWordNames;
	}	

	/**
	 * return the List of EGLSystemWord names in lowercase
	 */
	public static List getSystemWordNamesToLowerCase() {
		ArrayList systemWordNames = new ArrayList();
		Collection wordTreeMaps = systemWords.values();
		Iterator mapsIter = wordTreeMaps.iterator();
		for (int jj = 0; jj < wordTreeMaps.size(); jj++) {
			Collection systemWordsValues = ((TreeMap)mapsIter.next()).values();
			Iterator iter = systemWordsValues.iterator();
			while (iter.hasNext()) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				systemWordNames.add(systemWord.getName().toLowerCase());
			}
		}

		return systemWordNames;
	}	


	/**
	 * return the List of function EGLSystemWord names with a return value
	 */
	public static List getSystemFunctionWordNamesWithReturnValue() {
		ArrayList systemWordNames = new ArrayList();
		Collection wordTreeMaps = systemWords.values();
		Iterator mapsIter = wordTreeMaps.iterator();
		for (int jj = 0; jj < wordTreeMaps.size(); jj++) {
			Collection systemWordsValues = ((TreeMap)mapsIter.next()).values();
			Iterator iter = systemWordsValues.iterator();
			while (iter.hasNext()) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (functionWord.hasReturnCode())
						systemWordNames.add(systemWord.getName());
				}
			}
		}

		return systemWordNames;
	}	
		
	/**
	 * return the List of function EGLSystemWord with a return value
	 */
	public static List getSystemFunctionWordsWithReturnValue() {
		ArrayList systemWordNames = new ArrayList();
		Collection wordTreeMaps = systemWords.values();
		Iterator mapsIter = wordTreeMaps.iterator();
		for (int jj = 0; jj < wordTreeMaps.size(); jj++) {
			Collection systemWordsValues = ((TreeMap)mapsIter.next()).values();
			Iterator iter = systemWordsValues.iterator();
			while (iter.hasNext()) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (functionWord.hasReturnCode())
						systemWordNames.add(systemWord);
				}
			}
		}

		return systemWordNames;
	}		
		

	/**
	 * return the List of function EGLSystemWord names
	 */
	public static List getSystemFunctionWordNames() {
		ArrayList systemWordNames = new ArrayList();
		Collection wordTreeMaps = systemWords.values();
		Iterator mapsIter = wordTreeMaps.iterator();
		for (int jj = 0; jj < wordTreeMaps.size(); jj++) {
			Collection systemWordsValues = ((TreeMap)mapsIter.next()).values();
			Iterator iter = systemWordsValues.iterator();
			while (iter.hasNext()) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					systemWordNames.add(systemWord.getName());
				}
			}
		}

		return systemWordNames;
	}		

	/**
	 * return the List of function EGLSystemWord names with no return value
	 */
	public static List getSystemFunctionWordNamesWithoutReturnValue() {
		ArrayList systemWordNames = new ArrayList();
		Collection wordTreeMaps = systemWords.values();
		Iterator mapsIter = wordTreeMaps.iterator();
		for (int jj = 0; jj < wordTreeMaps.size(); jj++) {
			Collection systemWordsValues = ((TreeMap)mapsIter.next()).values();
			Iterator iter = systemWordsValues.iterator();
			while (iter.hasNext()) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (!functionWord.hasReturnCode())
						systemWordNames.add(systemWord.getName());
				}
			}
		}

		return systemWordNames;
	}

	/**
	 * return the List of function EGLSystemWord names with no return value
	 */
	public static List getSystemFunctionWordsWithoutReturnValue() {
		ArrayList systemWordNames = new ArrayList();
		Collection wordTreeMaps = systemWords.values();
		Iterator mapsIter = wordTreeMaps.iterator();
		for (int jj = 0; jj < wordTreeMaps.size(); jj++) {
			Collection systemWordsValues = ((TreeMap)mapsIter.next()).values();
			Iterator iter = systemWordsValues.iterator();
			while (iter.hasNext()) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord functionWord = (EGLSystemFunctionWord) systemWord;
					if (!functionWord.hasReturnCode())
						systemWordNames.add(systemWord);
				}
			}
		}

		return systemWordNames;
	}
	
	/**
	 * return the List of library EGLSystemWords
	 */
	public static List getLibrarySystemWords(String library) {
		//deferred70
		if (library.equalsIgnoreCase(IEGLConstants.KEYWORD_MATHLIB))
				return getMathLibWords();
		else if (library.equalsIgnoreCase(IEGLConstants.KEYWORD_STRLIB))
				return getStrLibWords();
		else if (library.equalsIgnoreCase(IEGLConstants.KEYWORD_SYSLIB))
				return getSysLibWords();
		else if (library.equalsIgnoreCase(IEGLConstants.KEYWORD_SYSVAR))
				return getSysVarWords();	
		else if (library.equalsIgnoreCase(IEGLConstants.KEYWORD_DATETIMELIB))
				return getDateTimeLibWords();	
		else if (library.equalsIgnoreCase(IEGLConstants.KEYWORD_SERVICELIB))
			return getServiceLibWords();			
		else if (library.equalsIgnoreCase(dynamicArraySystemWord))
				return getDynArrayWords();
		else if (library.equalsIgnoreCase(dictionarySystemWord))
				return getDictionaryWords();		
		else if (library.equalsIgnoreCase(recordSystemFunction))
			return getRecordWords();
		else { // this is slower but it guarantees we don't miss anything
			ArrayList systemWordNames = new ArrayList();
			Collection wordTreeMaps = systemWords.values();
			Iterator mapsIter = wordTreeMaps.iterator();
			for (int jj = 0; jj < wordTreeMaps.size(); jj++) {
				Collection systemWordsValues = ((TreeMap)mapsIter.next()).values();
				Iterator iter = systemWordsValues.iterator();
				while (iter.hasNext()) {
					EGLSystemWord systemWord = (EGLSystemWord) iter.next();
					if (systemWord.getLibrary().equals(library))
						systemWordNames.add(systemWord);
				}
			}

			return systemWordNames;
		}	
	}
	
	/**
	 * Return the list of system function names that are only valid for use within a page handler.
	 */
	public static List getPageHandlerSystemFunctionWordNames() {
		ArrayList systemWordNames = new ArrayList();
		Collection wordTreeMaps = systemWords.values();
		Iterator mapsIter = wordTreeMaps.iterator();
		for (int jj = 0; jj < wordTreeMaps.size(); jj++) {
			Collection systemWordsValues = ((TreeMap)mapsIter.next()).values();
			Iterator iter = systemWordsValues.iterator();
			while (iter.hasNext()) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if( systemWord.isPageHandlerSystemFunction() ) {
					systemWordNames.add(systemWord.getName());
				}
			}
		}

		return systemWordNames;
	}
	
	/**
	 * return a particular EGLSystemWord 
	 */
	public static EGLSystemWord getEGLSystemWord(String key) {
		Collection wordTreeMaps = systemWords.values();
		Iterator mapsIter = wordTreeMaps.iterator();
		for (int jj = 0; jj < wordTreeMaps.size(); jj++) {
			Collection systemWordsValues = ((TreeMap)mapsIter.next()).values();
			for( Iterator iter = systemWordsValues.iterator(); iter.hasNext(); ) {
				EGLSystemWord next = (EGLSystemWord) iter.next();
				if( next.getName().equalsIgnoreCase( key ) ) {
					return next;					
				}
			}
		}

		return null; // if haven't already returned then didn't find it.
	}
	
	public static EGLSystemWord getEGLSystemWord( String key, String libName ) {
		Map map = (Map) systemWords.get( libName );
		Collection systemWordsValues = map == null ? new ArrayList() : map.values();
		for( Iterator iter = systemWordsValues.iterator(); iter.hasNext(); ) {
			EGLSystemWord next = (EGLSystemWord) iter.next();
			if( next.getName().equalsIgnoreCase( key ) ) {
				return next;					
			}
		}	

		return null; // if haven't already returned then didn't find it.
	}

	/**
	 * return the List of variable EGLSystemWord names
	 */
	public static List getSystemVariableNames() {
		ArrayList systemWordNames = new ArrayList();
		Collection wordTreeMaps = systemWords.values();
		Iterator mapsIter = wordTreeMaps.iterator();
		for (int jj = 0; jj < wordTreeMaps.size(); jj++) {
			Collection systemWordsValues = ((TreeMap)mapsIter.next()).values();
			Iterator iter = systemWordsValues.iterator();
			while (iter.hasNext()) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable)
					systemWordNames.add(systemWord.getName());
			}
		}

		return systemWordNames;
	}

	/**
	 * return the List of variable EGLSystemWords
	 */
	public static List getSystemVariables() {
		ArrayList systemWordNames = new ArrayList();
		Collection wordTreeMaps = systemWords.values();
		Iterator mapsIter = wordTreeMaps.iterator();
		for (int jj = 0; jj < wordTreeMaps.size(); jj++) {
			Collection systemWordsValues = ((TreeMap)mapsIter.next()).values();
			Iterator iter = systemWordsValues.iterator();
			while (iter.hasNext()) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable)
					systemWordNames.add(systemWord);
			}
		}

		return systemWordNames;
	}

	/**
	 * return the List of numeric variable EGLSystemWord names
	 */
	public static List getSystemNumericVariableNames() {
		ArrayList systemWordNames = new ArrayList();
		Collection wordTreeMaps = systemWords.values();
		Iterator mapsIter = wordTreeMaps.iterator();
		for (int jj = 0; jj < wordTreeMaps.size(); jj++) {
			Collection systemWordsValues = ((TreeMap)mapsIter.next()).values();
			Iterator iter = systemWordsValues.iterator();
			while (iter.hasNext()) {
				EGLSystemWord systemWord = (EGLSystemWord) iter.next();
				if (systemWord instanceof EGLSystemVariable) {
					EGLSystemVariable systemVariable = (EGLSystemVariable) systemWord;
					if (systemVariable.isNumericVariable())
						systemWordNames.add(systemVariable.getName());
				}
			}
		}

		return systemWordNames;
	}

}
