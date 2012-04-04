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
package org.eclipse.edt.compiler.core.ast;

/**
 * @author Dave Murray
 */
public interface IOStatementClauseInfo {
	static final int LIST_VALUE = 0;
	static final int INLINE_STMT_VALUE = 1;	
	static final int IDENTIFIER_VALUE = 2;
	static final int NO_VALUE = 3;
	
	String getClauseKeyword();
	String getContentPrefix();
	String getContentSuffix();
	int getContentType();
}
