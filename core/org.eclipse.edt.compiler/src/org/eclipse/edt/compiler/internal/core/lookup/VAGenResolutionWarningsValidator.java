/*******************************************************************************
 * Copyright © 2006, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.lookup;

import java.util.Map;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.DataBindingWithImplicitQualifier;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;


/**
 * @author demurray
 */
public class VAGenResolutionWarningsValidator {
	
	private IProblemRequestor pRequestor;
	private ICompilerOptions compilerOptions;

	public VAGenResolutionWarningsValidator(IProblemRequestor pRequestor, ICompilerOptions compilerOptions) {
		this.pRequestor = pRequestor;
		this.compilerOptions = compilerOptions;
	}
	
	public void checkOperands(ITypeBinding functionContainerBinding, final Expression operand1, final Expression operand2, final Node nodeForErrors) {
		IAnnotationBinding aBinding = functionContainerBinding == null ? null : functionContainerBinding.getAnnotation(new String[] {"egl", "core"}, IEGLConstants.PROPERTY_ALLOWUNQUALIFIEDITEMREFERENCES); 
		if(compilerOptions.isVAGCompatible() &&
		   aBinding != null &&
		   Boolean.YES == aBinding.getValue()) {
			final ITypeBinding operand1Type = operand1.resolveTypeBinding();
			final ITypeBinding operand2Type = operand2.resolveTypeBinding();
			
			if(StatementValidator.isValidBinding(operand1Type) && StatementValidator.isValidBinding(operand2Type)) {
				operand1.accept(new DefaultASTVisitor() {
					public boolean visit(SimpleName simpleName) {
						boolean firstOperandContainer = isContainer(operand1Type);
						if(firstOperandContainer) {
							boolean secondOperandItem = isItem(operand2Type);
							if(firstOperandContainer && secondOperandItem) {
								pRequestor.acceptProblem(
									nodeForErrors,
									IProblemRequestor.VARIABLE_RESOLVED_TO_CONTAINER_MIGHT_BE_ITEM_IN_VAGEN,
									IMarker.SEVERITY_WARNING,
									new String[] {
										operand1.getCanonicalString(),
										operand2.getCanonicalString()
									});
							}
						}
						return false;
					}
				});
				operand2.accept(new DefaultASTVisitor() {
					public boolean visit(SimpleName simpleName) {
						boolean secondOperandContainer = isContainer(operand2Type);
						if(secondOperandContainer) {
							boolean firstOperandItem = isItem(operand1Type);
							if(firstOperandItem && secondOperandContainer) {
								pRequestor.acceptProblem(
									nodeForErrors,
									IProblemRequestor.VARIABLE_RESOLVED_TO_CONTAINER_MIGHT_BE_ITEM_IN_VAGEN,
									IMarker.SEVERITY_WARNING,
									new String[] {
										operand2.getCanonicalString(),
										operand1.getCanonicalString()
									});
							}
						}
						return false;
					}
				});
			}
		}
	}
	
	public void checkItemRecordNameOverlap(final Scope scope, final Expression expression) {
		if(compilerOptions.isVAGCompatible()) {
			final IDataBinding dBinding = expression.resolveDataBinding();
			if(IBinding.NOT_FOUND_BINDING != dBinding && dBinding != null) {
				expression.accept(new DefaultASTVisitor() {
					public boolean visit(SimpleName simpleName) {						
						FunctionContainerScope fContainerScope = getFunctionContainerScope(scope);
						if(fContainerScope != null) {
							Map itemsWhoseNamesCanBeUnqualified = fContainerScope.getItemsWhoseNamesCanBeUnqualified();
							Map recordsFormsAndDataTables = fContainerScope.getRecordsFormsAndDataTables();
							IDataBinding otherDBinding;
							
							switch(dBinding.getKind()) {								
								case IDataBinding.STRUCTURE_ITEM_BINDING:
								case IDataBinding.FORM_FIELD:
									otherDBinding = (IDataBinding) recordsFormsAndDataTables.get(dBinding.getName());
									if(otherDBinding != null) {										
										if(otherDBinding.isDataBindingWithImplicitQualifier()) {
											otherDBinding = ((DataBindingWithImplicitQualifier) otherDBinding).getWrappedDataBinding();
										}
										
										switch(otherDBinding.getKind()) {
											case IDataBinding.STRUCTURE_ITEM_BINDING:
											case IDataBinding.FORM_FIELD:
												pRequestor.acceptProblem(
													expression,
													IProblemRequestor.ITEM_RESOLVED_TO_FIELD_WITH_SAME_NAME_AS_FIELD,
													IMarker.SEVERITY_WARNING,
													new String[] {
														dBinding.getName(),
														dBinding.getDeclaringPart().getName(),
														otherDBinding.getDeclaringPart().getName()
													});
												break;
											default:
												pRequestor.acceptProblem(
													expression,
													IProblemRequestor.ITEM_RESOLVED_TO_FIELD_WITH_SAME_NAME_AS_CONTAINER,
													IMarker.SEVERITY_WARNING,
													new String[] {
														dBinding.getName(),
														dBinding.getDeclaringPart().getName()
													});
										}
									}
									break;
								default:
									ITypeBinding tBinding = dBinding.getType();
									if(Binding.isValidBinding(tBinding)) {
										switch(tBinding.getKind()) {
											case ITypeBinding.FIXED_RECORD_BINDING:
											case ITypeBinding.DATATABLE_BINDING:
											case ITypeBinding.FORM_BINDING:
												
												otherDBinding = (IDataBinding) itemsWhoseNamesCanBeUnqualified.get(dBinding.getName());									
												if(otherDBinding != null) {
													if(otherDBinding.isDataBindingWithImplicitQualifier()) {
														otherDBinding = ((DataBindingWithImplicitQualifier) otherDBinding).getWrappedDataBinding();
													}
													
													pRequestor.acceptProblem(
														expression,
														IProblemRequestor.ITEM_RESOLVED_TO_CONTAINER_WITH_SAME_NAME_AS_FIELD,
														IMarker.SEVERITY_WARNING,
														new String[] {
															dBinding.getName(),
															otherDBinding.getDeclaringPart().getName()
														});
												}
												break;
										}
									}
									break;
							}
						}
						return false;
					}
				});
			}
		}
	}

	private FunctionContainerScope getFunctionContainerScope(Scope scope) {
		while(scope != null && !(scope instanceof FunctionContainerScope)) {
			scope = scope.getParentScope();
		}
		return (FunctionContainerScope) scope;
	}

	/**
	 * @param operand2Type
	 * @return
	 */
	private boolean isItem(ITypeBinding type) {
		return ITypeBinding.PRIMITIVE_TYPE_BINDING == type.getKind() ||
		       ITypeBinding.MULTIPLY_OCCURING_ITEM == type.getKind();
	}

	/**
	 * @param operand1Type
	 * @return
	 */
	private boolean isContainer(ITypeBinding type) {
		return ITypeBinding.FIXED_RECORD_BINDING == type.getKind() ||
			   ITypeBinding.FORM_BINDING == type.getKind() ||
			   ITypeBinding.DATATABLE_BINDING == type.getKind();
	}
}
