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
package org.eclipse.edt.ide.ui.internal.contentassist;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.edt.compiler.internal.IEGLConstants;
/*
 * Comment unsupported language for edt 0.7, those will be uncommented later for edt 1.0
 */
public interface EGLDataTypeUtility {

	public final static String[] All_PREDEFINED_TYPE_STRINGS = {
	  		IEGLConstants.MIXED_ANY_STRING,
	  		IEGLConstants.MIXED_DICTIONARY_STRING,
	};
	

	public final static String[] PREDEFINED_SERVICE_FUNCTION_TYPE_STRINGS = {
  			IEGLConstants.MIXED_DICTIONARY_STRING,
	};
	 	 
	public final static String[] PREDEFINED_DATA_TYPE_STRINGS = {
  		IEGLConstants.MIXED_ANY_STRING,
  		IEGLConstants.MIXED_DICTIONARY_STRING,
	};
	
	public final static String[] PREDEFINED_NEWABLE_TYPE_STRINGS = {
			IEGLConstants.MIXED_DICTIONARY_STRING,
};
	 
	 
	public final static String[] PRIMITIVE_TYPE_STRINGS = {
  		IEGLConstants.KEYWORD_BIGINT,
		IEGLConstants.KEYWORD_BOOLEAN,
		IEGLConstants.KEYWORD_BYTES,
		IEGLConstants.KEYWORD_DATE,
		IEGLConstants.KEYWORD_DECIMAL,
		IEGLConstants.KEYWORD_FLOAT,
		IEGLConstants.KEYWORD_INT,
		IEGLConstants.KEYWORD_NUMBER,
		IEGLConstants.KEYWORD_SMALLFLOAT,
		IEGLConstants.KEYWORD_SMALLINT,
		IEGLConstants.KEYWORD_STRING,
		IEGLConstants.KEYWORD_TIME,
		IEGLConstants.KEYWORD_TIMESTAMP,
	};
	 	    	  		 		 
}
