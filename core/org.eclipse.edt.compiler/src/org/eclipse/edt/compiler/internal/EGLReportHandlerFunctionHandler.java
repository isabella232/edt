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

import java.util.TreeMap;

/**
 * @author jshavor
 */
public class EGLReportHandlerFunctionHandler {
	public static final String FUNCTION_BEFOREREPORTINIT = "beforeReportInit";  //$NON-NLS-1$
	public static final String FUNCTION_AFTERREPORTINIT = "afterReportInit";  //$NON-NLS-1$
	public static final String FUNCTION_BEFOREPAGEINIT = "beforePageInit";  //$NON-NLS-1$
	public static final String FUNCTION_AFTERPAGEINIT = "afterPageInit";  //$NON-NLS-1$
	public static final String FUNCTION_BEFORECOLUMNINIT = "beforeColumnInit";  //$NON-NLS-1$
	public static final String FUNCTION_AFTERCOLUMNINIT = "afterColumnInit";  //$NON-NLS-1$
	public static final String FUNCTION_BEFOREGROUPINIT = "beforeGroupInit";  //$NON-NLS-1$
	public static final String FUNCTION_AFTERGROUPINIT = "afterGroupInit";  //$NON-NLS-1$
	public static final String FUNCTION_BEFOREDETAILEVAL = "beforeDetailEval";  //$NON-NLS-1$
	public static final String FUNCTION_AFTERDETAILEVAL = "afterDetailEval";  //$NON-NLS-1$

	public static final String STRINGVARIABLE = "stringVariable";  //$NON-NLS-1$

	static TreeMap callBackFunctions;
	
	static {
		callBackFunctions = new TreeMap(new EGLCaseInsensitiveComparator());
		callBackFunctions.put(FUNCTION_BEFOREREPORTINIT,
			new EGLSystemFunctionWord(
				FUNCTION_BEFOREREPORTINIT,
				EGLSystemWordHandler.SYSTEM_WORD_NONE,
				0, "", null, 0, new String[0], new String[0], new int[0], new int[] { 0 }));		//$NON-NLS-1$
		callBackFunctions.put(FUNCTION_AFTERREPORTINIT,
			new EGLSystemFunctionWord(
				FUNCTION_AFTERREPORTINIT, 
				EGLSystemWordHandler.SYSTEM_WORD_NONE,
				0, "", null, 0, new String[0], new String[0], new int[0], new int[] { 0 }));		//$NON-NLS-1$
		callBackFunctions.put(FUNCTION_BEFOREPAGEINIT,
			new EGLSystemFunctionWord(
				FUNCTION_BEFOREPAGEINIT,
				EGLSystemWordHandler.SYSTEM_WORD_NONE,
				0, "", null, 0, new String[0], new String[0], new int[0], new int[] { 0 }));		//$NON-NLS-1$
		callBackFunctions.put(FUNCTION_AFTERPAGEINIT,
			new EGLSystemFunctionWord(
				FUNCTION_AFTERPAGEINIT, 
				EGLSystemWordHandler.SYSTEM_WORD_NONE,
				0, "", null, 0, new String[0], new String[0], new int[0], new int[] { 0 }));		//$NON-NLS-1$
		callBackFunctions.put(FUNCTION_BEFORECOLUMNINIT,
			new EGLSystemFunctionWord(
				FUNCTION_BEFORECOLUMNINIT, 
				EGLSystemWordHandler.SYSTEM_WORD_NONE,
				0, "", null, 0, new String[0], new String[0], new int[0], new int[] { 0 }));		//$NON-NLS-1$
		callBackFunctions.put(FUNCTION_AFTERCOLUMNINIT,
			new EGLSystemFunctionWord(
				FUNCTION_AFTERCOLUMNINIT, 
				EGLSystemWordHandler.SYSTEM_WORD_NONE,
				0, "", null, 0, new String[0], new String[0], new int[0], new int[] { 0 }));		//$NON-NLS-1$
		callBackFunctions.put(FUNCTION_BEFOREGROUPINIT,
			new EGLSystemFunctionWord(
				FUNCTION_BEFOREGROUPINIT, 
				EGLSystemWordHandler.SYSTEM_WORD_NONE,
				0, "", null, 0, new String[] {STRINGVARIABLE}, new String[] {IEGLConstants.STRING_STRING}, new int[] { EGLSystemWordHandler.IN }, new int[] { 1 }));		//$NON-NLS-1$
		callBackFunctions.put(FUNCTION_AFTERGROUPINIT,
			new EGLSystemFunctionWord(
				FUNCTION_AFTERGROUPINIT, 
				EGLSystemWordHandler.SYSTEM_WORD_NONE,
				0, "", null, 0, new String[] {STRINGVARIABLE}, new String[] {IEGLConstants.STRING_STRING}, new int[] { EGLSystemWordHandler.IN }, new int[] { 1 }));		//$NON-NLS-1$
		callBackFunctions.put(FUNCTION_BEFOREDETAILEVAL,
			new EGLSystemFunctionWord(
				FUNCTION_BEFOREDETAILEVAL, 
				EGLSystemWordHandler.SYSTEM_WORD_NONE,
				0, "", null, 0, new String[0], new String[0], new int[0], new int[] { 0 }));		//$NON-NLS-1$
		callBackFunctions.put(FUNCTION_AFTERDETAILEVAL,
			new EGLSystemFunctionWord(
				FUNCTION_AFTERDETAILEVAL, 
				EGLSystemWordHandler.SYSTEM_WORD_NONE,
				0, "", null, 0, new String[0], new String[0], new int[0], new int[] { 0 }));		//$NON-NLS-1$
	}
	
	/**
	 * @return Returns the callBackFunctions.
	 */
	public static TreeMap getCallBackFunctions() {
		return callBackFunctions;
	}
}
