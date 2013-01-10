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
package org.eclipse.edt.compiler.core.ast;


/**
 * ExternallyDefinedClause AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 * 
 * @deprecated Specified in settings block now. This class will be deleted
 */
public class ExternallyDefinedClause extends Node {
	
	public static IOStatementClauseInfo INFO = new IOStatementClauseInfo() {
		public String getClauseKeyword() {
			return "ExternallyDefined";//IEGLConstants.KEYWORD_EXTERNALLYDEFINED;
		}

		public String getContentPrefix() {
			return null;
		}

		public String getContentSuffix() {
			return null;
		}

		public int getContentType() {
			return IOStatementClauseInfo.NO_VALUE;
		}		
	};

	public ExternallyDefinedClause(int startOffset, int endOffset) {
		super(startOffset, endOffset);		
	}
	
	public void accept(IASTVisitor visitor) {
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new ExternallyDefinedClause(getOffset(), getOffset() + getLength());
	}
}
