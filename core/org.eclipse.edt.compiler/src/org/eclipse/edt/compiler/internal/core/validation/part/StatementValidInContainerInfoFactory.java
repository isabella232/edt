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
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.ConverseStatement;
import org.eclipse.edt.compiler.core.ast.DisplayStatement;
import org.eclipse.edt.compiler.core.ast.PrintStatement;
import org.eclipse.edt.compiler.core.ast.ShowStatement;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.core.ast.TransferStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.validation.statement.IOStatementValidatorConstants;


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
	
	private final Class[] INVALID_STATEMENTS_IN_VGWEBTRANSACTION = new Class[] {
		PrintStatement.class,
	};
	
	private final Class[] INVALID_STATEMENTS_IN_LIBRARY = new Class[] {
		DisplayStatement.class,
		TransferStatement.class
	};
	
	private final Class[] INVALID_STATEMENTS_IN_JSFHANDLER = new Class[] {
		ConverseStatement.class,
		DisplayStatement.class,
		PrintStatement.class,
		ShowStatement.class,
		TransferStatement.class
	};

	public IStatementValidInContainerInfo create(IPartBinding partBinding) {
		switch(partBinding.getKind()) {
			case IPartBinding.SERVICE_BINDING:
				return new StatementValidInContainerInfo(INVALID_STATEMENTS_IN_SERVICE, IProblemRequestor.STATEMENT_CANNOT_BE_IN_SERVICE);			
			case IPartBinding.PROGRAM_BINDING:
				if (AbstractBinder.annotationIs(partBinding.getSubType(), IOStatementValidatorConstants.EGLUIWEBTRANSACTION, IEGLConstants.PROGRAM_SUBTYPE_VG_WEB_TRANSACTION)) {
					return new StatementValidInContainerInfo(INVALID_STATEMENTS_IN_VGWEBTRANSACTION, IProblemRequestor.STATEMENT_CANNOT_BE_IN_ACTION_PROGRAM);
				}
				break;
			case IPartBinding.LIBRARY_BINDING:
				return new StatementValidInContainerInfo(INVALID_STATEMENTS_IN_LIBRARY , IProblemRequestor.STATEMENT_CANNOT_BE_IN_LIBRARY);
			case IPartBinding.HANDLER_BINDING:
				if(AbstractBinder.annotationIs(partBinding.getSubType(), IOStatementValidatorConstants.EGLUIJSF, IEGLConstants.PROPERTY_JSFHANDLER)) {
					return new StatementValidInContainerInfo(INVALID_STATEMENTS_IN_JSFHANDLER , IProblemRequestor.STATEMENT_CANNOT_BE_IN_PAGE_HANDLER);
				}
				break;
		}
		return DefaultStatementValidInContainerInfo.INSTANCE;
	}
}
