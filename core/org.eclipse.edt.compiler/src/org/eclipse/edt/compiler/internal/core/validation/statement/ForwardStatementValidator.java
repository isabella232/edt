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

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.edt.compiler.binding.HandlerBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTPartVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ForwardStatement;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.ParenthesizedExpression;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.PassingClause;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.ReturningToNameClause;
import org.eclipse.edt.compiler.core.ast.StringLiteral;
import org.eclipse.edt.compiler.core.ast.SubstringAccess;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Craig Duval
 */
public class ForwardStatementValidator extends DefaultASTVisitor implements IOStatementValidatorConstants {

	private IProblemRequestor problemRequestor;

	private ICompilerOptions compilerOptions;

	public ForwardStatementValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
	}

	public boolean visit(final ForwardStatement forwardStatement) {
		StatementValidator.validateIOTargetsContainer(forwardStatement.getIOObjects(), problemRequestor);
		if (forwardStatement.getArguments().size() > 0 && forwardStatement.isForwardToURL()) {
			problemRequestor.acceptProblem((Expression) forwardStatement.getArguments().get(0),
					IProblemRequestor.ARGUMENTS_NOT_ALLOWED_ON_FORWARD_TO_URL);
		}
		checkPageHandlerArguments(forwardStatement);
		validatePassing(forwardStatement);
		validateContainer(forwardStatement);

		if (forwardStatement.isForwardToURL()) {
			Expression aexpr = forwardStatement.getForwardTarget();
			aexpr.accept(new AbstractASTExpressionVisitor() {
				public boolean visitExpression(Expression expr) {
					ITypeBinding typeBinding = expr.resolveTypeBinding();
					if (!TypeCompatibilityUtil.isMoveCompatible(PrimitiveTypeBinding.getInstance(Primitive.CHAR), typeBinding, expr,
							compilerOptions)) {
						problemRequestor.acceptProblem(expr, IProblemRequestor.FORWARD_TO_URL_TARGET_MUST_BE_CHARACTER);

					}
					return false;
				}

				public boolean visit(StringLiteral stringLiteral) {
					return false;
				}
			});

		}

		if (hasReturningToNameClause(forwardStatement) || !forwardStatement.hasForwardTarget()) {
			if (forwardStatement.getArguments().size() != 1) {
				problemRequestor.acceptProblem(forwardStatement, IProblemRequestor.INVALID_FORWARD_UIRECORD_NUM_ARGS);
			}
		}
		return false;
	}

	private void validateContainer(final ForwardStatement forwardStatement) {
		Node parent = forwardStatement.getParent();
		Node child = forwardStatement;
		while (parent.getParent() != null) {
			child = parent;
			parent = parent.getParent();
		}

		child.accept(new AbstractASTPartVisitor() {
			public boolean visit(Program program) {
				addError();
				return false;
			}

			public boolean visit(Library lib) {
				addError();
				return false;
			}

			public void visitPart(Part part) {
			}

			protected void addError() {
				problemRequestor.acceptProblem(forwardStatement, IProblemRequestor.STATEMENT_CAN_ONLY_BE_IN_PAGE_HANDLER,
						new String[] { IEGLConstants.KEYWORD_FORWARD });
			}
		});
	}

	private void validatePassing(final ForwardStatement forwardStatement) {
		forwardStatement.accept(new AbstractASTVisitor() {
			public boolean visit(PassingClause passingClause) {
				Expression expr = passingClause.getExpression();
				ITypeBinding typeBinding = expr.resolveTypeBinding();
				if (StatementValidator.isValidBinding(typeBinding)) {
					if (typeBinding.getKind() != ITypeBinding.FIXED_RECORD_BINDING
							&& typeBinding.getKind() != ITypeBinding.FLEXIBLE_RECORD_BINDING) {
						problemRequestor.acceptProblem(expr, IProblemRequestor.PASSING_RECORD_NOT_RECORD, new String[] { expr
								.getCanonicalString() });
					} else if (typeBinding.getAnnotation(EGLIODLI, "PSBRecord") != null) {
						problemRequestor.acceptProblem(expr, IProblemRequestor.DLI_PSBRECORD_NOT_VALID_AS_PASSING_ITEM, new String[] { expr
								.getCanonicalString() });
					}
				}

				if (!hasReturningToNameClause(forwardStatement)) {
					problemRequestor.acceptProblem(passingClause, IProblemRequestor.INVALID_FORWARD_STMT_PASSING_WITH_OUT_RETURNING_OPTION);
				}

				return false;
			}
		});
	}

	private void checkPageHandlerArguments(ForwardStatement forwardStatement) {
		if (forwardStatement.getArguments().size() == 0) {
			return;
		}

		if (isForwardUIRecord(forwardStatement)) {
			// Validate that the arguement is a UI record
			Expression expr = (Expression) forwardStatement.getArguments().get(0);
			ITypeBinding typeBinding = expr.resolveTypeBinding();
			if (StatementValidator.isValidBinding(typeBinding)) {
				if (typeBinding.getAnnotation(EGLUIWEBTRANSACTION, "VGUIRecord") == null) {
					problemRequestor.acceptProblem(expr, IProblemRequestor.FORWARD_ARG_MUST_BE_UI_RECORD, new String[] { expr
							.getCanonicalString() });
				} else {
					validateReturningTo(forwardStatement, typeBinding);
				}
			}

			return;
		}

		ArrayList arglist = new ArrayList();
		boolean argsValid = getForwardStatementArgTypes(forwardStatement, arglist);

		if (argsValid) {
			if (forwardStatement.isForwardToLabel()) {

				ITypeBinding[] forwardArgs = (ITypeBinding[]) arglist.toArray(new ITypeBinding[arglist.size()]);
				HandlerBinding pageHandler = getTargetPageHandler(forwardStatement);
				if (pageHandler == null) {
					return;
				}

				IDataBinding onConstructionFunctionDBinding = pageHandler.getAnnotation(EGLUIJSF, "JSFHandler").findData(
						InternUtil.intern(IEGLConstants.PROPERTY_ONCONSTRUCTIONFUNCTION));
				if (IBinding.NOT_FOUND_BINDING != onConstructionFunctionDBinding) {
					validateArguments(forwardStatement, forwardArgs, onConstructionFunctionDBinding);

				} else {
					problemRequestor.acceptProblem(forwardStatement.getForwardTarget(),
							IProblemRequestor.FORWARD_TARGET_DOESNT_HAVE_ONPAGELOAD_FUNCTION);
				}
				
				IDataBinding onPreRenderFunctionDBinding = pageHandler.getAnnotation(EGLUIJSF, "JSFHandler").findData(
						InternUtil.intern(IEGLConstants.PROPERTY_ONPRERENDERFUNCTION));
				if (IBinding.NOT_FOUND_BINDING != onConstructionFunctionDBinding) {
					validateArguments(forwardStatement, forwardArgs, onPreRenderFunctionDBinding);
				}
			}

			for (Iterator iter = forwardStatement.getArguments().iterator(); iter.hasNext();) {
				((Expression) iter.next()).accept(new DefaultASTVisitor() {
					public boolean visit(ParenthesizedExpression parenthesizedExpression) {
						return true;
					}

					public boolean visit(final SubstringAccess substringAccess) {
						problemRequestor.acceptProblem(substringAccess, IProblemRequestor.SUBSTRING_EXPRESSION_IN_BAD_LOCATION);
						return false;
					}
				});
			}
		}

	}

	private void validateArguments(ForwardStatement forwardStatement, ITypeBinding[] forwardArgs, IDataBinding functionDBinding) {
		if(functionDBinding != null && IBinding.NOT_FOUND_BINDING != functionDBinding) {
			Object value = ((IAnnotationBinding) functionDBinding).getValue();
			if (value != null && value != IBinding.NOT_FOUND_BINDING) {
				IFunctionBinding funcBinding = (IFunctionBinding) value;
				String funcName = funcBinding.getName();
				if (StatementValidator.isValidBinding(funcBinding)) {
					ITypeBinding[] paramtypes = getFunctionParamTypes(funcBinding);
					if (!StatementValidator.checkArguments(paramtypes, forwardArgs, compilerOptions)) {
						problemRequestor.acceptProblem(forwardStatement.getForwardTarget(),
								IProblemRequestor.PAGEHANDLER_ARGS_DONT_MATCH_PARAMS, new String[] { funcName,
										StatementValidator.getParmListString(paramtypes),
										StatementValidator.getParmListString(forwardArgs) });
					}
				}
			}
		}
	}

	private HandlerBinding getTargetPageHandler(ForwardStatement forwardStatement) {
		HandlerBinding pageHandler = null;
		ITypeBinding binding = forwardStatement.getForwardTarget().resolveTypeBinding();

		if (StatementValidator.isValidBinding(binding)) {
			if (binding.getAnnotation(EGLUIJSF, "JSFHandler") != null) {
				pageHandler = (HandlerBinding) binding;
			}

		}

		return pageHandler;
	}

	private boolean getForwardStatementArgTypes(ForwardStatement forwardStatement, ArrayList arglist) {
		boolean argsValid = true;
		Iterator iter = forwardStatement.getArguments().iterator();
		while (iter.hasNext()) {
			Expression expr = (Expression) iter.next();
			ITypeBinding typeBinding = expr.resolveTypeBinding();
			if (StatementValidator.isValidBinding(typeBinding)) {
				if (!isValidArg(typeBinding)) {
					argsValid = false;
					problemRequestor.acceptProblem(expr, IProblemRequestor.FORWARD_ARG_MUST_BE_ITEM_RECORD_OR_DYNAMIC_ARRAY,
							new String[] { expr.getCanonicalString() });
				}
			} else {
				argsValid = false;
			}

			arglist.add(typeBinding);
		}

		return argsValid;
	}

	private ITypeBinding[] getFunctionParamTypes(IFunctionBinding funcBinding) {
		ArrayList list = new ArrayList();
		Iterator iter = funcBinding.getParameters().iterator();
		while (iter.hasNext()) {
			IDataBinding binding = (IDataBinding) iter.next();
			list.add(binding.getType());
		}

		return (ITypeBinding[]) list.toArray(new ITypeBinding[list.size()]);

	}

	private boolean hasReturningToNameClause(ForwardStatement forwardStatement) {
		ReturnBooleanASTVisitor visitor = new ReturnBooleanASTVisitor() {
			public boolean visit(ReturningToNameClause returningToNameClause) {
				bool = true;
				return true;
			}
		};

		forwardStatement.accept(visitor);
		return visitor.hasBool();
	}

	private boolean isForwardUIRecord(ForwardStatement stmt) {

		if (stmt.getArguments().size() != 1) {
			return false;
		}

		if (stmt.hasForwardTarget()) {
			return false;
		}

		if (hasReturningToNameClause(stmt)) {
			return true;
		}

		return true;
	}

	private void validateReturningTo(ForwardStatement forwardStatement, final ITypeBinding uirecBinding) {
		forwardStatement.accept(new AbstractASTVisitor() {
			public boolean visit(ReturningToNameClause returningToNameClause) {
				String pgmName = returningToNameClause.getName().getCanonicalName();
				if (StatementValidator.isValidBinding(returningToNameClause.getName().resolveBinding())
						&& returningToNameClause.getName().resolveBinding().isTypeBinding()) {
					ITypeBinding typeBinding = (ITypeBinding) returningToNameClause.getName().resolveBinding();
					if (typeBinding.getKind() == ITypeBinding.PROGRAM_BINDING) {
						if (typeBinding.getAnnotation(EGLUIWEBTRANSACTION, "VGWebTransaction") != null) {
							IDataBinding inputUIRecordDBinding = typeBinding.getAnnotation(EGLUIWEBTRANSACTION, "VGWebTransaction")
									.findData(InternUtil.intern(IEGLConstants.PROPERTY_INPUTUIRECORD));
							if (IBinding.NOT_FOUND_BINDING != inputUIRecordDBinding) {
								IDataBinding dataBinding = (IDataBinding) ((IAnnotationBinding) inputUIRecordDBinding).getValue();
								if (StatementValidator.isValidBinding(dataBinding)) {
									if (StatementValidator.isValidBinding(dataBinding.getType()) && dataBinding.getType() == uirecBinding) {
										return false;
									}
								}
							}
							problemRequestor.acceptProblem(returningToNameClause.getName(),
									IProblemRequestor.FORWARD_UI_RECORD_DEFINITION_MUST_MATCH_INPUT_PAGE_RECORD, new String[] { pgmName });
						} else {
							problemRequestor.acceptProblem(returningToNameClause.getName(),
									IProblemRequestor.FORWARD_RETURN_TO_MUST_BE_ACTION_PROGRAM, new String[] { pgmName });
						}

					}

				}
				return false;
			}
		});

	}

	private boolean isValidArg(ITypeBinding type) {
		if (type.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING && ((PrimitiveTypeBinding) type).getPrimitive() != Primitive.ANY) {
			return true;
		}
		if (type.getKind() == ITypeBinding.FIXED_RECORD_BINDING || type.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING) {
			return true;
		}
		if (type.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
			return true;
		}
		return false;
	}

	private class ReturnBooleanASTVisitor extends AbstractASTVisitor {
		boolean bool = false;

		public boolean hasBool() {
			return bool;
		}
	}

}
