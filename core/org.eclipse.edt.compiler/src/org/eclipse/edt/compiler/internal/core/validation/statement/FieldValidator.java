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

import org.eclipse.core.resources.IMarker;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AnnotationExpression;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.type.TypeValidator;
import org.eclipse.edt.mof.egl.Annotation;


public class FieldValidator extends DefaultASTVisitor {
	
	private IProblemRequestor problemRequestor;
    private ICompilerOptions compilerOptions;
    private IPartBinding declaringPart;
	
	public FieldValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions, IPartBinding declaringPart) {
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
		this.declaringPart = declaringPart;
	}
	
	@Override
	public boolean visit(final ClassDataDeclaration classDataDeclaration) {
		Type type = classDataDeclaration.getType();
		TypeValidator.validate(classDataDeclaration.getType(), declaringPart, problemRequestor, compilerOptions);
		
		if (classDataDeclaration.hasInitializer()) {
			validateInitializer(classDataDeclaration.getNames().get(0), classDataDeclaration.getInitializer());
			
			if (classDataDeclaration.hasSettingsBlock()) {
				issueErrorForInitialization(classDataDeclaration.getSettingsBlockOpt(), ((Name)classDataDeclaration.getNames().get(0)).getCanonicalName(), IProblemRequestor.SETTING_NOT_ALLOWED);
			}
		}
		else {
			TypeValidator.validateInstantiatable(type, declaringPart, classDataDeclaration.isNullable(), problemRequestor);
			
			//nullable types cannot specify a settings block that contains settings for data in the field
			if (classDataDeclaration.hasSettingsBlock() && classDataDeclaration.isNullable()) {
				issueErrorForInitialization(classDataDeclaration.getSettingsBlockOpt(), ((Name)classDataDeclaration.getNames().get(0)).getCanonicalName(), IProblemRequestor.SETTING_NOT_ALLOWED_NULL);
			}
		}
		
		//TODO - currently the bindings don't contain property overrides
//		if (classDataDeclaration.hasSettingsBlock()) {
//			IDataBinding dbinding = ((Expression)classDataDeclaration.getNames().get(0)).resolveDataBinding();
//			if (Binding.isValidBinding(dbinding)) {
//				issueErrorForPropertyOverrides(dbinding, classDataDeclaration.getSettingsBlockOpt());
//			}
//		}
		return false;
	}
	
	@Override
	public boolean visit(FunctionDataDeclaration functionDataDeclaration) {
		Type type = functionDataDeclaration.getType();
		if (type != null) {
			TypeValidator.validate(type, declaringPart, problemRequestor, compilerOptions);
			
			if (functionDataDeclaration.hasInitializer()) {
				validateInitializer(functionDataDeclaration.getNames().get(0), functionDataDeclaration.getInitializer());
				
				if (functionDataDeclaration.hasSettingsBlock()) {
					issueErrorForInitialization(functionDataDeclaration.getSettingsBlockOpt(), ((Name)functionDataDeclaration.getNames().get(0)).getCanonicalName(), IProblemRequestor.SETTING_NOT_ALLOWED);
				}
			}
			else {
				TypeValidator.validateInstantiatable(type, declaringPart, functionDataDeclaration.isNullable(), problemRequestor);
				
				//nullable types cannot specify a settings block that contains settings for data in the field
				if (functionDataDeclaration.hasSettingsBlock() && functionDataDeclaration.isNullable()) {
					issueErrorForInitialization(functionDataDeclaration.getSettingsBlockOpt(), ((Name)functionDataDeclaration.getNames().get(0)).getCanonicalName(), IProblemRequestor.SETTING_NOT_ALLOWED_NULL);
				}
			}
		}
		
		//TODO - currently the bindings don't contain property overrides
//		if (functionDataDeclaration.hasSettingsBlock()) {
//			IDataBinding dbinding = ((Expression)functionDataDeclaration.getNames().get(0)).resolveDataBinding();
//			if (Binding.isValidBinding(dbinding)) {
//				issueErrorForPropertyOverrides(dbinding, functionDataDeclaration.getSettingsBlockOpt());
//			}
//		}
		
		return false;
	}
	
	@Override
	public boolean visit(StructureItem structureItem) {
		Type type = structureItem.getType();
		if (type != null) {
			TypeValidator.validate(type, declaringPart, problemRequestor, compilerOptions);
			
			if (structureItem.hasInitializer()) {
				validateInitializer(structureItem.getName(), structureItem.getInitializer());
				
				if (structureItem.hasSettingsBlock()) {
					if (structureItem.getName() != null) {
						issueErrorForInitialization(structureItem.getSettingsBlock(), structureItem.getName().getCanonicalName(), IProblemRequestor.SETTING_NOT_ALLOWED);
					}
				}
			}
			if (!structureItem.hasInitializer()) {
				TypeValidator.validateInstantiatable(type, declaringPart, structureItem.isNullable(), problemRequestor);
				
				//nullable types cannot specify a settings block that contains settings for data in the field
				if (structureItem.hasSettingsBlock() && structureItem.isNullable() && structureItem.getName() != null) {
					issueErrorForInitialization(structureItem.getSettingsBlock(), structureItem.getName().getCanonicalName(), IProblemRequestor.SETTING_NOT_ALLOWED_NULL);
				}
			}
		}
		
		//TODO - currently the bindings don't contain property overrides
//		if (structureItem.hasSettingsBlock() && structureItem.getName() != null) {
//			IDataBinding dbinding = structureItem.getName().resolveDataBinding();
//			if (Binding.isValidBinding(dbinding)) {
//				issueErrorForPropertyOverrides(dbinding, structureItem.getSettingsBlock());
//			}
//		}
		
		return false;
	}
	
	private void validateInitializer(Name name, Expression initializer) {
		if (initializer != null && name != null) {
			new AssignmentStatementValidator(problemRequestor, compilerOptions, declaringPart).validateAssignment(
					Assignment.Operator.ASSIGN, name, initializer, name.resolveType(), initializer.resolveType(), name.resolveMember(), initializer.resolveMember(),
					new LValueValidator.DefaultLValueValidationRules() {
						@Override
						public boolean canAssignToConstantVariables() {
							return true;
						}
					});
		}
	}
	
	private void issueErrorForInitialization(SettingsBlock settings, final String fieldName, int errorNo) {
		final Node[] errorNode = new Node[1];
    	settings.accept(new AbstractASTExpressionVisitor() {
    		@Override
    		public boolean visit(Assignment assignment) {
    			if (errorNode[0] != null) {
    				return false;
    			}
    			
    			//check if it was an annotation type assignment
    			if (assignment.resolveBinding() == null) {
    				Object dBinding = assignment.getLeftHandSide().resolveElement();
    				if (dBinding != null && !(dBinding instanceof Annotation)) {
    	        		errorNode[0] = assignment;
    				}
    			}
    			return false;
    		}
    		
    		@Override
    		public boolean visit(AnnotationExpression annotationExpression) {
    			return false;
    		}
    		
    		@Override
    		public boolean visit(SetValuesExpression setValuesExpression) {
    			if (errorNode[0] != null) {
    				return false;
    			}
    			Object dBinding = setValuesExpression.getExpression().resolveElement();
				if (dBinding != null && !(dBinding instanceof Annotation)) {
					setValuesExpression.getSettingsBlock().accept(this);
				}
				return false; 			
    		}
    		
    		@Override
    		public boolean visitExpression(Expression expression) {
    			if (errorNode[0] != null) {
    				return false;
    			}
    			errorNode[0] = expression;
    			return false;
    		}
    		
    	});
    	if (errorNode[0] != null) {
    		problemRequestor.acceptProblem(errorNode[0], errorNo, IMarker.SEVERITY_ERROR, new String[] {fieldName});
    	}
    }
	
//	private void issueErrorForPropertyOverrides(IDataBinding fieldBinding, SettingsBlock settings) {
//    	if (!(fieldBinding instanceof DataBinding) || ((DataBinding)fieldBinding).getPropertyOverrides().isEmpty()) {
//    		return;
//    	}
//    	
//    	final Stack<IDataBinding> stack = new Stack();
//    	stack.push(fieldBinding);
//		final Node[] errorNode = new Node[1];
//   	
//    	settings.accept(new AbstractASTExpressionVisitor() {
//    		public boolean visit(Assignment assignment) {
//    			if (errorNode[0] != null) {
//    				return false;
//    			}
//    			
//    			//if this is an annotation assignment
//    			if (Binding.isValidBinding(assignment.resolveBinding())) {
//    				if (stack.size() > 1) {
//    					errorNode[0] = assignment;
//    				}
//    			}
//    			return false;
//    		}
//    		
//    		public boolean visit(AnnotationExpression annotationExpression) {
//    			if (errorNode[0] != null) {
//    				return false;
//    			}
//    			
//				if (stack.size() > 1) {
//					errorNode[0] = annotationExpression;
//				}
//				return false;
//    		}
//    		
//    		public boolean visit(SetValuesExpression setValuesExpression) {
//    			if (errorNode[0] != null) {
//    				return false;
//    			}
//    			
//    			IDataBinding dBinding = setValuesExpression.getExpression().resolveDataBinding();
//				if (Binding.isValidBinding(dBinding)) {					
//					if (dBinding.getKind() == IDataBinding.ANNOTATION_BINDING) {
//						if (stack.size() > 1) {
//							errorNode[0] = setValuesExpression.getExpression();
//						}
//					}
//					else {
//						stack.push(dBinding);
//						setValuesExpression.getSettingsBlock().accept(this);
//					}
//				}
//				return false; 			
//    		}
//    		
//    		public void endVisit(SetValuesExpression setValuesExpression) {
//    			IDataBinding dBinding = setValuesExpression.getExpression().resolveDataBinding();
//				if (Binding.isValidBinding(dBinding)) {					
//					if (dBinding.getKind() == IDataBinding.ANNOTATION_BINDING) {}
//					else {
//						stack.pop();
//					}
//				}
//    		}
//    		
//    		public boolean visitExpression(Expression expression) {
//    			return false;
//    		}
//    		
//    	});
//		problemRequestor.acceptProblem(errorNode[0], IProblemRequestor.PROPERTY_OVERRIDES_NOT_SUPPORTED, IMarker.SEVERITY_ERROR, new String[] {fieldBinding.getCaseSensitiveName()});
//    }
}
