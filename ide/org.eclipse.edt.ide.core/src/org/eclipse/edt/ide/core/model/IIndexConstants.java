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
package org.eclipse.edt.ide.core.model;


public interface IIndexConstants {
	/* index encoding */
	char[] REF= "ref/".toCharArray(); //$NON-NLS-1$
	char[] FIELD_REF= "fieldRef/".toCharArray(); //$NON-NLS-1$
	char[] FUNCTION_REF= "functionRef/".toCharArray(); //$NON-NLS-1$
	char[] PART_REF= "partRef/".toCharArray(); //$NON-NLS-1$
	char[] PART_DECL = "partDecl/".toCharArray(); //$NON-NLS-1$
	int    PART_DECL_LENGTH = 9;
	char[] FUNCTION_DECL= "functionDecl/".toCharArray(); //$NON-NLS-1$
	char[] FIELD_DECL= "fieldDecl/".toCharArray(); //$NON-NLS-1$
	char[] USE_DECL="useDecl/".toCharArray(); //$NON-NLS-1$
	char[][] COUNTS= 
		new char[][] { new char[] {'0'}, new char[] {'1'}, new char[] {'2'}, new char[] {'3'}, new char[] {'4'}, new char[] {'5'}, new char[] {'6'}, new char[] {'7'}, new char[] {'8'}, new char[] {'9'}
	};
	char PART_SUFFIX = 0x7FFF; // All types ORed together
	char PROGRAM_SUFFIX = 0x0001;
	char ANNOTATION_SUFFIX = 0x0002;
	char TABLE_SUFFIX = 0x0004;
	char RECORD_SUFFIX = 0x0008;
	char ITEM_SUFFIX = 0x0010;
	char FORM_SUFFIX = 0x0020;
	char FORMGRP_SUFFIX = 0x0040;
	char FUNCTION_SUFFIX = 0x0080;
	char LIBRARY_SUFFIX = 0x0100;
	char HANDLER_SUFFIX = 0x0200;
	char SERVICE_SUFFIX = 0x0400;
	char INTERFACE_SUFFIX = 0x0800;
	char DELEGATE_SUFFIX = 0x1000;
	char EXTERNALTYPE_SUFFIX = 0x2000;
	char ENUMERATION_SUFFIX = 0x4000;
	char STEREOTYPE_SUFFIX = 0x8000;
	


	char SEPARATOR= '/';

	char[] ONE_STAR = new char[] {'*'};
	char[][] ONE_STAR_CHAR = new char[][] {ONE_STAR};

	// used as special marker for enclosing type name of local and anonymous classes
	char[] ONE_ZERO = new char[] {'0'}; 
	char[][] ONE_ZERO_CHAR = new char[][] {ONE_ZERO};
	
	/**
	 * The search pattern matches exactly the search result,
	 * that is, the source of the search result equals the search pattern.
	 */
	int EXACT_MATCH = 0;
	/**
	 * The search pattern is a prefix of the search result.
	 */
	int PREFIX_MATCH = 1;
	/**
	 * The search pattern contains one or more wild cards ('*') where a 
	 * wild-card can replace 0 or more characters in the search result.
	 */
	int PATTERN_MATCH = 2;
	
	/* Waiting policies */
	


}
