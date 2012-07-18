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
package org.eclipse.edt.compiler.internal.core.validation.part;

import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.core.ast.DisplayStatement;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.core.ast.TransferStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;


public class StatementValidInContainerInfoFactory {
	private static class StatementValidInContainerInfo implements IStatementValidInContainerInfo {

		private Class[] invalidStatements;
		private int problemKind;	
		
		public StatementValidInContainerInfo(Class[] invalidStatements, int problemKind) {
			this.invalidStatements = invalidStatements;
			this.problemKind = problemKind;		
		}

		public int getProblemKind() {
			return problemKind;
		}
		
		public boolean statementIsValidInContainer(Statement statement) {
			for(int i = 0; i < invalidStatements.length; i++) {
				if(invalidStatements[i].isInstance(statement)) {
					return false;
				}
			}
			return true;
		}
	}
	
	private static class DefaultStatementValidInContainerInfo implements IStatementValidInContainerInfo {

		public static DefaultStatementValidInContainerInfo INSTANCE = new DefaultStatementValidInContainerInfo();	
		
		private DefaultStatementValidInContainerInfo() {}

		public int getProblemKind() {
			return 0;
		}
		
		public boolean statementIsValidInContainer(Statement statement) {		
			return true;
		}
	}
	
	public static final StatementValidInContainerInfoFactory INSTANCE = new StatementValidInContainerInfoFactory();
	
	private StatementValidInContainerInfoFactory() {}
	
	private final Class[] INVALID_STATEMENTS_IN_SERVICE = new Class[] {
		TransferStatement.class
	};
	
	private final Class[] INVALID_STATEMENTS_IN_LIBRARY = new Class[] {
		DisplayStatement.class,
		TransferStatement.class
	};
	
	public IStatementValidInContainerInfo create(IPartBinding partBinding) {
		switch(partBinding.getKind()) {
			case IPartBinding.SERVICE_BINDING:
				return new StatementValidInContainerInfo(INVALID_STATEMENTS_IN_SERVICE, IProblemRequestor.STATEMENT_CANNOT_BE_IN_SERVICE);			
			case IPartBinding.LIBRARY_BINDING:
				return new StatementValidInContainerInfo(INVALID_STATEMENTS_IN_LIBRARY , IProblemRequestor.STATEMENT_CANNOT_BE_IN_LIBRARY);
		}
		return DefaultStatementValidInContainerInfo.INSTANCE;
	}
}
