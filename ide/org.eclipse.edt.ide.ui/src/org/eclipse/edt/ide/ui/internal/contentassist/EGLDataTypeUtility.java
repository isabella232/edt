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

import org.eclipse.edt.compiler.internal.IEGLConstants;

public interface EGLDataTypeUtility {

		 
	public final static String[] ALIASED_TYPE_STRINGS = {
  		IEGLConstants.KEYWORD_ANY,
  		IEGLConstants.KEYWORD_BIGINT,
		IEGLConstants.KEYWORD_BOOLEAN,
		IEGLConstants.KEYWORD_BYTES,
		IEGLConstants.KEYWORD_DATE,
		IEGLConstants.KEYWORD_DECIMAL,
		IEGLConstants.MIXED_DICTIONARY_STRING,
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
