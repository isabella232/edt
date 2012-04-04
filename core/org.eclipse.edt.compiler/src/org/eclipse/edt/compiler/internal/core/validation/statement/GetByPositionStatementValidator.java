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
package org.eclipse.edt.compiler.internal.core.validation.statement;

import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ForUpdateClause;
import org.eclipse.edt.compiler.core.ast.GetByPositionStatement;
import org.eclipse.edt.compiler.core.ast.IntoClause;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.UsingPCBClause;
import org.eclipse.edt.compiler.core.ast.WithInlineDLIClause;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;


/**
 * @author Craig Duval
 */
public class GetByPositionStatementValidator extends DefaultASTVisitor implements IOStatementValidatorConstants {

	private IProblemRequestor problemRequestor;

	private ICompilerOptions compilerOptions;

	public GetByPositionStatementValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
	}

	//TODO validate iorecord properties keyitem and length
	public boolean visit(final GetByPositionStatement agetByPositionStatement) {
		StatementValidator.validateIOTargetsContainer(agetByPositionStatement.getIOObjects(), problemRequestor);
		validatePosition(agetByPositionStatement);
		validateTarget(agetByPositionStatement);
		validateMultipleTargets(agetByPositionStatement);

//		String resultSetID = agetByPositionStatement.getFromResultSetID();
//		if (resultSetID != null) {
//			EGLNameValidator.validate(resultSetID, EGLNameValidator.RESULT_SET_ID, problemRequestor,
//					agetByPositionStatement, compilerOptions);
//		}

		agetByPositionStatement.accept(new AbstractASTVisitor() {
			WithInlineDLIClause inlinedli = null;
			
			UsingPCBClause pcbClause = null;

			ForUpdateClause updateClause = null;

			public boolean visit(ForUpdateClause forUpdateClause) {
				updateClause = forUpdateClause;
				return false;
			}

			public boolean visit(UsingPCBClause usingPCBClause) {
				pcbClause = usingPCBClause;
				return false;
			}
			
			public boolean visit(WithInlineDLIClause withInlineDLIClause) {
				if (inlinedli != null){
					problemRequestor.acceptProblem(withInlineDLIClause,
							IProblemRequestor.DUPE_INLINE_DLI,
							new String[] { IEGLConstants.KEYWORD_GET.toUpperCase()});
				}else{
					inlinedli = withInlineDLIClause;
				}					
				return false;
			}

			public void endVisit(GetByPositionStatement getByPositionStatement) {
				if (getByPositionStatement.getDliInfo() != null) {
					DLIStatementValidator validator = new DLIStatementValidator(getByPositionStatement,
							problemRequestor, 3);
					validator.validateGetByPosDLI(pcbClause, updateClause != null, getByPositionStatement
							.isGetInParent());
				}
			}
		});

		return true;
	}

	private void validateMultipleTargets(GetByPositionStatement getByPositionStatement) {
		int count = 0;
		boolean isdli = false;
		if (getByPositionStatement.hasTargetRecords()) {
			List targetList = getByPositionStatement.getTargetRecords();
			Iterator iter = targetList.iterator();
			while (iter.hasNext()) {
				Expression expr = (Expression) iter.next();
				ITypeBinding typeBinding = expr.resolveTypeBinding();
				if (StatementValidator.isValidBinding(typeBinding)) {
					if (++count > 1) {
						if (!isdli || typeBinding.getAnnotation(EGLIODLI, "DLISegment") == null) {
							problemRequestor.acceptProblem(expr,
									IProblemRequestor.MULTIPLE_TARGETS_MUST_ALL_BE_DLISEGMENT_SCALARS,
									new String[] { IEGLConstants.KEYWORD_GET });
						}
					} else {
						isdli = typeBinding.getAnnotation(EGLIODLI, "DLISegment") != null;
					}

				}
			}
		}
	}

	protected String getDirectiveString(GetByPositionStatement getByPositionStatement) {
		String retVal = "";
		if (getByPositionStatement.isAbsoluteDirection()) {
			retVal = IEGLConstants.KEYWORD_ABSOLUTE;
		} else if (getByPositionStatement.isCurrentDirection()) {
			retVal = IEGLConstants.KEYWORD_CURRENT;
		} else if (getByPositionStatement.isFirstDirection()) {
			retVal = IEGLConstants.KEYWORD_FIRST;
		} else if (getByPositionStatement.isLastDirection()) {
			retVal = IEGLConstants.KEYWORD_LAST;
		} else if (getByPositionStatement.isNextDirection()) {
			retVal = IEGLConstants.KEYWORD_NEXT;
		} else if (getByPositionStatement.isPreviousDirection()) {
			retVal = IEGLConstants.KEYWORD_PREVIOUS;
		} else if (getByPositionStatement.isRelativeDirection()) {
			retVal = IEGLConstants.KEYWORD_RELATIVE;
		}

		if (getByPositionStatement.hasPosition()) {
			retVal = retVal + " (" + getByPositionStatement.getPosition().getCanonicalString() + ")";
		}

		return retVal;
	}

	protected void addINVALID_CLAUSE_FOR_NON_SQL_TARGETError(Node node, String clause) {
//		problemRequestor.acceptProblem(node, IProblemRequestor.INVALID_CLAUSE_FOR_NON_SQL_TARGET,
//				new String[] { clause });
	}

	protected void validateRecordOptions(final GetByPositionStatement getByPositionStatement,
			final ITypeBinding typeBinding) {

//		if (getByPositionStatement.hasFromResultSetID()) {
//			if (typeBinding != null && typeBinding.getAnnotation(EGLIOSQL, "SQLRecord") == null) {
//				addINVALID_CLAUSE_FOR_NON_SQL_TARGETError(getByPositionStatement, IEGLConstants.KEYWORD_FROM);
//			}
//		}

		getByPositionStatement.accept(new AbstractASTVisitor() {

			public boolean visit(IntoClause intoClause) {
				if (typeBinding != null
						&& typeBinding.getAnnotation(EGLIOSQL, "SQLRecord") == null) {
					addINVALID_CLAUSE_FOR_NON_SQL_TARGETError(intoClause, IEGLConstants.KEYWORD_INTO);
				}
				intoClause.accept(new AbstractASTExpressionVisitor() {
					public boolean visitExpression(Expression expression) {
						StatementValidator.validateItemInIntoClause(expression, problemRequestor);
						return false;
					}

				});
				return false;
			}

			public boolean visit(ForUpdateClause forUpdateClause) {
				if (getByPositionStatement.hasTargetRecords()) {
					Iterator iter = getByPositionStatement.getTargetRecords().iterator();
					while (iter.hasNext()) {
						Expression expr = (Expression) iter.next();
						ITypeBinding arytypeBinding = expr.resolveTypeBinding();
						if (StatementValidator.isValidBinding(arytypeBinding)) {
							if (arytypeBinding.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
								if (arytypeBinding.getBaseType() == typeBinding)
									problemRequestor.acceptProblem(expr,
											IProblemRequestor.FORUPDATE_NOT_ALLOWED_WITH_ARRAY_TARGET);
							}
						}
					}
				}
				return false;
			}
		});
	}

	protected void validateDirectiveForType(GetByPositionStatement agetByPositionStatement, ITypeBinding typeBinding) {
		if (typeBinding.getAnnotation(EGLIODLI, "DLISegment") != null) {
			if (!agetByPositionStatement.isNextDirection()) {
				problemRequestor.acceptProblem(agetByPositionStatement,
						IProblemRequestor.ONLY_NEXT_DIRECTIVE_ALLOWED_WITH_DLISEGMENT);
			}
		}

		if (typeBinding.getAnnotation(EGLIOFILE, "RelativeRecord") != null
				|| typeBinding.getAnnotation(EGLIOFILE, "SerialRecord") != null
				|| typeBinding.getAnnotation(EGLIOMQ, "MQRecord") != null) {
			if (!agetByPositionStatement.isNextDirection()) {
				problemRequestor.acceptProblem(agetByPositionStatement,
						IProblemRequestor.GET_BY_POSITION_DIRECTIVE_OTHER_THAN_NEXT,
						new String[] { getDirectiveString(agetByPositionStatement) });
			}
		}

		if (typeBinding.getAnnotation(EGLIOFILE, "IndexedRecord") != null) {
			if (!agetByPositionStatement.isNextDirection() && !agetByPositionStatement.isPreviousDirection()) {
				problemRequestor.acceptProblem(agetByPositionStatement,
						IProblemRequestor.GET_BY_POSITION_DIRECTIVE_OTHER_THAN_NEXT_OR_PREVIOUS,
						new String[] { getDirectiveString(agetByPositionStatement) });
			}
		}

		if (typeBinding.getAnnotation(EGLIOFILE, "CSVRecord") != null) {
			if (!agetByPositionStatement.isNextDirection()) {
				problemRequestor.acceptProblem(agetByPositionStatement,
						IProblemRequestor.GET_BY_POSITION_DIRECTIVE_OTHER_THAN_NEXT,
						new String[] { getDirectiveString(agetByPositionStatement) });
			}
		}
		
		if (typeBinding.getAnnotation(EGLIODLI, "DLISegment") == null) {
			if (agetByPositionStatement.isGetInParent()) {
				problemRequestor.acceptProblem(agetByPositionStatement,
						IProblemRequestor.IO_CLAUSE_REQUIRES_DLISEGMENT_TARGET,
						new String[] { IEGLConstants.KEYWORD_INPARENT });
			}

			agetByPositionStatement.accept(new DefaultASTVisitor() {
				public boolean visit(GetByPositionStatement getByPositionStatement) {
					return true;
				}

				public boolean visit(final ForUpdateClause forUpdateClause) {
					problemRequestor.acceptProblem(forUpdateClause,
							IProblemRequestor.INVALID_CLAUSE_FOR_NON_DLI_TARGET,
							new String[] { IEGLConstants.KEYWORD_FORUPDATE });
					return false;
				}
			});
		}

		validateRecordOptions(agetByPositionStatement, typeBinding);
	}

	protected void validateTarget(GetByPositionStatement getByPositionStatement) {
		if (getByPositionStatement.hasTargetRecords()) {
			Iterator iter = getByPositionStatement.getTargetRecords().iterator();
			while (iter.hasNext()) {
				Expression expr = (Expression) iter.next();
				ITypeBinding typeBinding = expr.resolveTypeBinding();
				if (StatementValidator.isValidBinding(typeBinding)) {
					if (typeBinding.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
						ITypeBinding baseBinding = typeBinding.getBaseType();
						if (StatementValidator.isValidBinding(baseBinding)) {
							validateDirectiveForType(getByPositionStatement, baseBinding);
							if (baseBinding.getAnnotation(EGLIODLI, "DLISegment") == null) {
								problemRequestor.acceptProblem(expr,
										IProblemRequestor.GET_BY_POSITION_STATEMENT_TARGET_NOT_RECORD,
										new String[] { expr.getCanonicalString() });
							}
						}
					} else if (typeBinding.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING
							|| typeBinding.getKind() == ITypeBinding.FIXED_RECORD_BINDING) {
						validateDirectiveForType(getByPositionStatement, typeBinding);
					} else {
						problemRequestor.acceptProblem(expr,
								IProblemRequestor.GET_BY_POSITION_STATEMENT_TARGET_NOT_RECORD, new String[] { expr
										.getCanonicalString() });
					}
				}
			}
		} else {
			validateRecordOptions(getByPositionStatement, null);
		}
	}

	protected void validatePosition(GetByPositionStatement getByPositionStatement) {
		if (getByPositionStatement.hasPosition()) {
			Expression expr = getByPositionStatement.getPosition();
			ITypeBinding typeBinding = expr.resolveTypeBinding();
			if (!StatementValidator.isIntegerCompatible(typeBinding)) {
				problemRequestor.acceptProblem(expr, IProblemRequestor.GETBYPOSITION_POSITION_BAUE_MUST_BE_INTEGER);
			}
		}
	}
}
