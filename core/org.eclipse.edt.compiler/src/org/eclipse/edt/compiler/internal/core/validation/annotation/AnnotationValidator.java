/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.validation.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.edt.compiler.binding.AnnotationBinding;
import org.eclipse.edt.compiler.binding.AnnotationFieldBinding;
import org.eclipse.edt.compiler.binding.AnnotationValidationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.DataItemBinding;
import org.eclipse.edt.compiler.binding.DataItemPropertiesProblemsProblemRequestor;
import org.eclipse.edt.compiler.binding.FieldContentValidationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.FixedRecordBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PartContentValidationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.binding.UsedTypeBinding;
import org.eclipse.edt.compiler.binding.ValueValidationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.annotationType.BasicFormGroupAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.annotationType.EGLNotInCurrentReleaseAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.annotationType.StereotypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.AnnotationExpression;
import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.ConstantFormField;
import org.eclipse.edt.compiler.core.ast.DataItem;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.compiler.core.ast.FormGroup;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedForm;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.core.ast.TopLevelForm;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.compiler.core.ast.VariableFormField;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;
import org.eclipse.edt.compiler.internal.core.validation.statement.LValueValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.RValueValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;


/**
 * @author svihovec
 *
 */
public class AnnotationValidator {

	private IProblemRequestor problemRequestor;	
	private ICompilerOptions compilerOptions;
	
	private IProblemRequestor storedProblemRequestor;
	
	public AnnotationValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
	}
	
	private IProblemRequestor restoreProblemRequestor() {
		IProblemRequestor result = storedProblemRequestor;
		storedProblemRequestor = null;
		return result;
	}

	private IProblemRequestor specializeProblemRequestor(IProblemRequestor problemRequestor, Node target, IAnnotationBinding annotation) {
		return specializeProblemRequestor(problemRequestor, target, annotation.getAnnotationType());
	}
	
	private IProblemRequestor specializeProblemRequestor(IProblemRequestor problemRequestor, Node target, IAnnotationTypeBinding annotationType) {
		storedProblemRequestor = problemRequestor;
		
		IProblemRequestor result = problemRequestor;
		
		if(target instanceof DataItem) {
			result = new DataItemPropertiesProblemsProblemRequestor(problemRequestor, ((DataItem) target).getName().resolveBinding(), annotationType);
		}
		
		return result;
	}
	
	public void validateAnnotationTarget(Node target){
		
		target.accept(new DefaultASTVisitor(){
			
			public boolean visit(Record record) {
				IAnnotationTypeBinding partSubTypeBinding = null;
				IBinding bindingWithAnnotations = null;
				
				IBinding binding = record.getName().resolveBinding();
				if(binding.isTypeBinding() && ((ITypeBinding) binding).isPartBinding()) {
					partSubTypeBinding = ((IPartBinding) binding).getSubType();
					bindingWithAnnotations = binding.getAnnotation(partSubTypeBinding);
					if(partSubTypeBinding != null) {
						partSubTypeBinding = partSubTypeBinding.getValidationProxy();
					}
				}
				
				if(partSubTypeBinding == null) {
					return false;
				}
				
				if(partSubTypeBinding != null && bindingWithAnnotations != null) {					
					// process annotations on part subtype
					processPartSubTypeSubAnnotations(record.getName(), record, bindingWithAnnotations, partSubTypeBinding.getAnnotations());
					
					// get annotations for fields, run validation annotations on fields
					processPartSubTypeFields(record, record.getContents(), partSubTypeBinding);
					
					// run validation rules from subtype
					processPartSubType(record, partSubTypeBinding);
				}
			
				processOverriddenAnnotationsAndSetValues(record);
				
				return false;
			}
			
			public boolean visit(TopLevelForm form) {				
				Name subType = form.getSubType();
				
				if(subType != null){
					IDataBinding subTypeBinding = subType.resolveDataBinding();
				
					if(IBinding.NOT_FOUND_BINDING != subTypeBinding) {
						// process annotations on part subtype
						processPartSubTypeSubAnnotations(form.getName(), form, subTypeBinding, ((IAnnotationTypeBinding) subTypeBinding.getType()).getValidationProxy().getAnnotations());
						
						// get annotations for fields, run validation annotations on fields
						processPartSubTypeFields(form, form.getContents(), ((IAnnotationTypeBinding)subTypeBinding.getType()).getValidationProxy());
						
						// run validation rules from subtype
						processPartSubType((Part)form, ((IAnnotationTypeBinding)subTypeBinding.getType()).getValidationProxy());
					}
				}
				
				processOverriddenAnnotationsAndSetValues(form);
				
				return false;
			}
			
			public boolean visit(NestedForm form) {				
				Name subType = form.getSubType();
				
				if(subType != null){
					IDataBinding subTypeBinding = subType.resolveDataBinding();
					
				
					if(IBinding.NOT_FOUND_BINDING != subTypeBinding) {
						// process annotations on part subtype
						processPartSubTypeSubAnnotations(form.getName(), form, subTypeBinding, ((IAnnotationTypeBinding)subTypeBinding.getType()).getValidationProxy().getAnnotations());
						
						// get annotations for fields, run validation annotations on fields
						processPartSubTypeFields(form, form.getContents(), ((IAnnotationTypeBinding)subTypeBinding.getType()).getValidationProxy());
						
						// run validation rules from subtype
						processPartSubType(form.getName(), form, form.getContents(), ((IAnnotationTypeBinding)subTypeBinding.getType()).getValidationProxy());
					}
				}
				
				processOverriddenAnnotationsAndSetValues(form);
				
				return false;
			}
			
			public boolean visitFunctionContainer(Part functionContainerPart, Name subType) {
				IAnnotationTypeBinding partSubTypeBinding = null;
				IBinding bindingWithAnnotations = null;
				
				IBinding binding = functionContainerPart.getName().resolveBinding();
				if(binding.isTypeBinding() && ((ITypeBinding) binding).isPartBinding()) {
					partSubTypeBinding = ((IPartBinding) binding).getSubType();
					bindingWithAnnotations = binding.getAnnotation(partSubTypeBinding);
					if(partSubTypeBinding != null) {
						partSubTypeBinding = partSubTypeBinding.getValidationProxy();
					}
				}
				
				if(partSubTypeBinding == null) {
					return false;
				}
				
				// process annotations on part subtype
				processPartSubTypeSubAnnotations(functionContainerPart.getName(), functionContainerPart, bindingWithAnnotations, partSubTypeBinding.getAnnotations());
				
				// get annotations for fields, run validation annotations on fields
				processPartSubTypeFields(functionContainerPart, functionContainerPart.getContents(), partSubTypeBinding);
				
				// run validation rules from subtype
				processPartSubType(functionContainerPart, partSubTypeBinding);
				
				processOverriddenAnnotationsAndSetValues(functionContainerPart);
				
				return false;
			}
			
			public boolean visit(Program program) {
				visitFunctionContainer(program, program.getSubType());
				return false;
			}
			
			public boolean visit(Library library) {
				visitFunctionContainer(library, library.getSubType());
				return false;
			}
			
			public boolean visit(Handler handler) {
				visitFunctionContainer(handler, handler.getSubType());
				return false;
			}
			
			public boolean visit(Interface interfaceNode) {
				visitFunctionContainer(interfaceNode, interfaceNode.getSubType());
				return false;
			}

			public boolean visit(Service serviceNode) {
				visitFunctionContainer(serviceNode, serviceNode.getSubType());
				return false;
			}
			
			public boolean visit(NestedFunction function) {
				
				IDataBinding db = function.getName().resolveDataBinding();
				if (Binding.isValidBinding(db)) {
					processAnnotations(function, db.getType(), db);
				}
				return false;
			}
			
			public boolean visit(ExternalType externalType) {
				visitFunctionContainer(externalType, externalType.getSubType());
				return false;
			}
			
			public boolean visit(final DataItem dataItem) {
				DataItemBinding binding = (DataItemBinding) dataItem.getName().resolveBinding();
				processAnnotations(dataItem, binding == null ? null : binding.getPrimitiveTypeBinding(), null);
				processOverriddenAnnotationsAndSetValues(dataItem);				
				return false;
			}
			
			public boolean visit(FormGroup formGroup) {
//				 process annotations on part subtype
				processPartSubTypeSubAnnotations(formGroup.getName(), formGroup, formGroup.getName().resolveBinding(), null);
				
				// get annotations for fields, run validation annotations on fields
				processPartSubTypeFields(formGroup, formGroup.getContents(), BasicFormGroupAnnotationTypeBinding.getInstance());
				
				// run validation rules from subtype
				processPartSubType(formGroup, null);
				
				processOverriddenAnnotationsAndSetValues(formGroup);
				return false;
			}
			
			public boolean visit(StructureItem structureItem) {
				processAnnotations(structureItem, (IDataBinding) structureItem.resolveBinding());
				
				if(structureItem.hasSettingsBlock()) {
					processOverriddenAnnotationsAndSetValues(structureItem.getSettingsBlock());
				}
				
				return false;
			}
			
			public boolean visit(ClassDataDeclaration classDataDeclaration) {			
				processAnnotations(classDataDeclaration, getType(classDataDeclaration.getType().resolveTypeBinding()), null);
				
				if(classDataDeclaration.hasSettingsBlock()) {
					processOverriddenAnnotationsAndSetValues(classDataDeclaration.getSettingsBlockOpt());
				}
				
				return false;
			}
			
			public boolean visit(UseStatement useStatement) {			
				UsedTypeBinding usedTypeBinding = useStatement.getUsedTypeBinding();
				if(usedTypeBinding != null) {
					processAnnotations(useStatement, getType(usedTypeBinding.getType()), null);
				}
				
				return false;
			}
			
			private ITypeBinding getType(ITypeBinding binding) {
				if(binding != null && IBinding.NOT_FOUND_BINDING != binding) {
					switch(binding.getKind()) {
					case ITypeBinding.DATAITEM_BINDING:
						return binding = ((DataItemBinding) binding).getPrimitiveTypeBinding();
					case ITypeBinding.ARRAY_TYPE_BINDING:
						return ArrayTypeBinding.getInstance(getType(((ArrayTypeBinding) binding).getElementType()));
					}
				}
				return binding;
			}

			public boolean visit(FunctionDataDeclaration functionDataDeclaration) {
				processAnnotations(functionDataDeclaration, getType(functionDataDeclaration.getType().resolveTypeBinding()), null);
				
				if(functionDataDeclaration.hasSettingsBlock()) {
					processOverriddenAnnotationsAndSetValues(functionDataDeclaration.getSettingsBlockOpt());
				}
				
				return false;
			}
			
			public boolean visit(VariableFormField variableFormField) {
				processAnnotations(variableFormField, variableFormField.getName().resolveDataBinding());
				if(variableFormField.hasSettingsBlock()) {
					processOverriddenAnnotationsAndSetValues(variableFormField.getSettingsBlock());
				}
				return false;
			}
			
			public boolean visit(ConstantFormField constantFormField) {
				processAnnotations(constantFormField, constantFormField.resolveBinding());
				return false;
			}
		});
	}
	
	private void processAnnotations(final Node target, final IDataBinding targetBinding){
		processAnnotations(target, targetBinding == null || IBinding.NOT_FOUND_BINDING == targetBinding ? null : targetBinding.getType(), null);
	}
	
	/**
	 * Process annotations that appear on a DataItem or Field target (structure item, page item, form field, program variable)
	 * @param targetDataBinding TODO
	 */
	private void processAnnotations(final Node target, final ITypeBinding targetTypeBinding, IDataBinding targetDataBinding){
		target.accept(new AbstractASTExpressionVisitor(){			
			public boolean visit(ClassDataDeclaration classDataDeclaration) {
				if(classDataDeclaration.hasSettingsBlock()) {
					classDataDeclaration.getSettingsBlockOpt().accept(this);
				}
				return false;
			}
			
			public boolean visit(FunctionDataDeclaration functionDataDeclaration) {
				if(functionDataDeclaration.hasSettingsBlock()) {
					functionDataDeclaration.getSettingsBlockOpt().accept(this);
				}
				return false;
			}
			
			public boolean visit(NestedFunction nestedFunction) {
				
				final AbstractASTExpressionVisitor superVisitor = this;
				
				DefaultASTVisitor visitor = new DefaultASTVisitor() {
					public boolean visit(NestedFunction nestedFunction) {
						return true;
					}
					public boolean visit(SettingsBlock settingsBlock) {
						settingsBlock.accept(superVisitor);
						return false;
					}
				};
				nestedFunction.accept(visitor);
				return false;
			}
			
			public boolean visit(UseStatement useStatement) {
				if(useStatement.hasSettingsBlock()) {
					useStatement.getSettingsBlock().accept(this);
				}
				return false;
			}
			
			public boolean visit(StructureItem structureItem) {
				if(structureItem.hasSettingsBlock()) {
					structureItem.getSettingsBlock().accept(this);
				}
				return false;
			}
			
			public boolean visit(VariableFormField variableFormField) {
				if(variableFormField.hasSettingsBlock()) {
					variableFormField.getSettingsBlock().accept(this);
				}
				return false;
			}
			
			public boolean visit(ConstantFormField constantFormField) {
				if(constantFormField.hasSettingsBlock()) {
					constantFormField.getSettingsBlock().accept(this);
				}
				return false;
			}
			
			public boolean visit(AnnotationExpression annotationExpression) {
				IDataBinding annotationExpressionDBinding = annotationExpression.resolveDataBinding(); 
				if(IBinding.NOT_FOUND_BINDING != annotationExpressionDBinding &&
                   annotationExpressionDBinding.getKind() == IDataBinding.ANNOTATION_BINDING){
				
					processSubAnnotations(annotationExpression, target, targetTypeBinding, null, (IAnnotationBinding)annotationExpression.resolveDataBinding(), ((IAnnotationTypeBinding) annotationExpression.resolveTypeBinding()).getValidationProxy().getAnnotations());
				}
				return false;
			}
			
			public boolean visit(SetValuesExpression setValuesExpression) {				
				Expression annotationExpression = setValuesExpression.getExpression();
				
				IDataBinding annotationExpressionDBinding = annotationExpression.resolveDataBinding(); 
				if(Binding.isValidBinding(annotationExpressionDBinding) &&
                   annotationExpressionDBinding.getKind() == IDataBinding.ANNOTATION_BINDING){
					SettingsBlock settingsBlock = setValuesExpression.getSettingsBlock();
					
					ITypeBinding annotationExpressionTBinding =  annotationExpressionDBinding.getType();
					if(annotationExpressionTBinding != null) {					   
						processSubAnnotations(annotationExpression, target, targetTypeBinding, null, (IAnnotationBinding)annotationExpressionDBinding, ((IAnnotationTypeBinding) annotationExpressionTBinding).getValidationProxy().getAnnotations());
						processComplexAnnotationFields(target, settingsBlock, (IAnnotationTypeBinding) annotationExpressionTBinding);
						
//						for(Iterator iter = setValuesExpression.getSettingsBlock().getSettings().iterator(); iter.hasNext();) {
//							((Node) iter.next()).accept(this);
//						}
					}
				}
				return false;
			}
			
			public boolean visit(Assignment assignment){
				IAnnotationBinding aBinding = assignment.resolveBinding();
				if(aBinding != null) {
					IAnnotationTypeBinding validationProxy = aBinding.getAnnotationType().getValidationProxy();
					processSubAnnotations(assignment, target, targetTypeBinding, null, aBinding, validationProxy.getAnnotations());
					processSimpleAnnotationFields(target, assignment, validationProxy);	
					
					if (aBinding.isAnnotationField()) {
						AnnotationFieldBinding annField = (AnnotationFieldBinding) aBinding;
						if (annField.getEnclosingAnnotationType() != null && annField.getEnclosingAnnotationType().getValidationProxy() != null) {
							processComplexAnnotationFields(target, assignment, annField.getEnclosingAnnotationType().getValidationProxy());
						}
					}
				}
				return false;
			}
			
			public boolean visitExpression(final Expression expression) {
				target.accept(new DefaultASTVisitor() {
					public boolean visit(ClassDataDeclaration classDataDeclaration) {
						classDataDeclaration.getType().accept(this);
						return false;					
					}
					
					public boolean visit(StructureItem structureItem) {
						structureItem.getType().accept(this);
						return false;
					}
					
					public boolean visit(ArrayType arrayType) {
						if(arrayType.hasInitialSize()) {
							if(targetTypeBinding != null && ITypeBinding.ARRAY_TYPE_BINDING == targetTypeBinding.getKind()) {
								ITypeBinding exprType = expression.resolveTypeBinding();
								if(exprType != null) {
									if(!TypeCompatibilityUtil.isMoveCompatible(
										((ArrayTypeBinding) targetTypeBinding).getElementType(),
										exprType,
										null,
										compilerOptions)) {
										problemRequestor.acceptProblem(
											expression,
											IProblemRequestor.ASSIGNMENT_STATEMENT_TYPE_MISMATCH,
											new String[] {
												exprType.getCaseSensitiveName(),
												((ArrayTypeBinding) targetTypeBinding).getElementType().getCaseSensitiveName(),
												expression.getCanonicalString()
											});
									}
								}
							}		
						}
						else {
//							problemRequestor.acceptProblem(
//								expression,
//								IProblemRequestor.POSITIONAL_PROPERTY_NOT_VALID_FOR,
//								new String[] {
//									arrayType.getCanonicalName()	
//								});
						}
						return false;
					}
				});				
				return false;
			}
		});
	}
	
	private void processOverriddenAnnotationsAndSetValues(final Node target) {
		target.accept(new DefaultASTVisitor() {
			public boolean visit(Record record) {
				return true;
			}
			
			public boolean visit(NestedForm nestedForm) {
				return true;
			}
			
			public boolean visit(TopLevelForm topLevelForm) {
				return true;
			}
			
			public boolean visit(Program program) {
				return true;
			}
			
			public boolean visit(Library library) {
				return true;
			}
			
			public boolean visit(Handler handler) {
				return true;
			}
			
			public boolean visit(DataItem dataItem) {
				return true;
			}
			
			public boolean visit(FormGroup formGroup) {
				return true;
			}
			
			public boolean visit(SettingsBlock settingsBlock) {
				return true;
			}
			
			public boolean visit(SetValuesExpression setValuesExpression) {
				final Expression annotationExpression = setValuesExpression.getExpression();
				
				final IDataBinding annotationExpressionDBinding = annotationExpression.resolveDataBinding(); 
				if(IBinding.NOT_FOUND_BINDING != annotationExpressionDBinding && annotationExpressionDBinding != null) {
					if(IDataBinding.STRUCTURE_ITEM_BINDING == annotationExpressionDBinding.getKind() ||
					   IDataBinding.FORM_FIELD == annotationExpressionDBinding.getKind()) {
						setValuesExpression.getSettingsBlock().accept(new DefaultASTVisitor() {
							public boolean visit(SettingsBlock settingsBlock) {
								return true;
							}
							
							public boolean visit(Assignment assignment) {
								IAnnotationBinding aBinding = assignment.resolveBinding();
								if(aBinding != null) {
									List ruleAnnotations = ((IAnnotationTypeBinding)aBinding.getType()).getValidationProxy().getAnnotations();
									processSubAnnotations(assignment, annotationExpression, annotationExpressionDBinding.getType(), null, aBinding, ruleAnnotations);
								}
								return false;
							}
						});
					}
					processOverriddenAnnotationsAndSetValues(setValuesExpression.getSettingsBlock());
					
					if(annotationExpressionDBinding.getAnnotation(EGLNotInCurrentReleaseAnnotationTypeBinding.getInstance()) != null) {
						problemRequestor.acceptProblem(
							annotationExpression instanceof AnnotationExpression ? ((AnnotationExpression) annotationExpression).getName() : annotationExpression,
							IProblemRequestor.SYSTEM_PART_NOT_SUPPORTED,
							new String[] {
								annotationExpressionDBinding.getType().getCaseSensitiveName()
							});
					}
				}
				return false;
			}
			
			public boolean visit(final Assignment assignment){
				IAnnotationBinding aBinding = assignment.resolveBinding();
				if(aBinding == null) {
					ITypeBinding lhType = assignment.getLeftHandSide().resolveTypeBinding();
					ITypeBinding rhType = assignment.getRightHandSide().resolveTypeBinding();
					if(StatementValidator.isValidBinding(lhType) &&
					   StatementValidator.isValidBinding(rhType) &&
					   ITypeBinding.ANNOTATION_BINDING != lhType.getKind()) {
						
						if((Assignment.Operator.CONCAT == assignment.getOperator() || Assignment.Operator.NULLCONCAT == assignment.getOperator()) && ITypeBinding.ARRAY_TYPE_BINDING == lhType.getKind()) {
							lhType = ((ArrayTypeBinding) lhType).getElementType();
						}
						
						if(!TypeCompatibilityUtil.isMoveCompatible(lhType, rhType, assignment.getRightHandSide(), compilerOptions) &&
						   !rhType.isDynamic() &&
						   !TypeCompatibilityUtil.areCompatibleExceptions(rhType, lhType, compilerOptions)) {
							problemRequestor.acceptProblem(
								assignment.getRightHandSide(),
								IProblemRequestor.ASSIGNMENT_STATEMENT_TYPE_MISMATCH,
								new String[] {
									StatementValidator.getShortTypeString(lhType),
									StatementValidator.getShortTypeString(rhType),
									assignment.getLeftHandSide().getCanonicalString() + "=" + assignment.getRightHandSide().getCanonicalString()
								});
						}
						
						target.accept(new DefaultASTVisitor() {
							public boolean visit(Record record) {
								IDataBinding lhDBinding = assignment.getLeftHandSide().resolveDataBinding();
								if(StatementValidator.isValidBinding(lhDBinding) &&
								   IDataBinding.STRUCTURE_ITEM_BINDING == lhDBinding.getKind() &&					   
								   !((StructureItemBinding) lhDBinding).getChildren().isEmpty()) {
									problemRequestor.acceptProblem(
										assignment.getLeftHandSide(),
										IProblemRequestor.INITIALIZERS_ONLY_ALLOWED_ON_LEAF_ITEMS);
								}
								return false;
							}
						});
						
						IDataBinding lhDBinding = assignment.getLeftHandSide().resolveDataBinding();
						if(StatementValidator.isValidBinding(lhDBinding)) {
							new LValueValidator(problemRequestor, compilerOptions, lhDBinding, assignment.getLeftHandSide(), new LValueValidator.DefaultLValueValidationRules() {
								public boolean canAssignToReadOnlyVariables() {
									return true;
								}
							}).validate();
						}
						
						IDataBinding rhDBinding = assignment.getRightHandSide().resolveDataBinding();
						if(StatementValidator.isValidBinding(rhDBinding)) {
							new RValueValidator(problemRequestor, compilerOptions, rhDBinding, assignment.getRightHandSide()).validate();
						}
					}
				}
				else {
					IDataBinding lhDBinding = assignment.getLeftHandSide().resolveDataBinding();
					if(StatementValidator.isValidBinding(lhDBinding)) {
						if(lhDBinding.getAnnotation(EGLNotInCurrentReleaseAnnotationTypeBinding.getInstance()) != null) {
							problemRequestor.acceptProblem(
								assignment.getLeftHandSide(),
								IProblemRequestor.SYSTEM_PART_NOT_SUPPORTED,
								new String[] {
									lhDBinding.getCaseSensitiveName()
								});
						}
					}
				}
				return false;
			}
		});
	}
	
	/**
	 * Process annotations found on fields of this complex annotation.
	 */
	private void processComplexAnnotationFields(final Node target, final SettingsBlock settingsBlock, final IAnnotationTypeBinding complexType) {
		
		settingsBlock.accept(new AbstractASTVisitor(){
			
			public boolean visit(SetValuesExpression setValuesExpression) {
				// we have found a nested annotation, start the process over
				processAnnotations(setValuesExpression, (ITypeBinding) null, null);
				return false;
			}
			
			public boolean visit(AnnotationExpression annotationExpression) {
				// we have found a nested annotation, start the process over
				processAnnotations(annotationExpression, (ITypeBinding) null, null);
				return false;
			}
			
			public boolean visit(Assignment assignment){
				processComplexAnnotationFields(target, assignment, complexType);
				return true;
			}
		});	
	}
	
	private void processComplexAnnotationFields(final Node target, final Assignment assignment, final IAnnotationTypeBinding complexType) {
		IAnnotationBinding aBinding = assignment.resolveBinding();
		
		if(aBinding != null && complexType != null) {				
			Object value = aBinding.getValue();
			if(value != null && IBinding.NOT_FOUND_BINDING != value) {
				List annotations = complexType.getValidationProxy().getFieldAnnotations(aBinding.getName());
				
				if(annotations != null){
					for (Iterator iter = annotations.iterator(); iter.hasNext();) {
						ValueValidationAnnotationTypeBinding nextRule = (ValueValidationAnnotationTypeBinding) iter.next();
						
						nextRule.validate(assignment.getRightHandSide(), target, aBinding, problemRequestor, compilerOptions);
					}
				}
			}
		}
	}

		
	/**
	 * Process annotations found on the value field of this simple annotation.
	 */
	private void processSimpleAnnotationFields(Node target, Assignment assignment, IAnnotationTypeBinding simpleType) {
		
		// process annotations specifically for this annotations value
		List valueAnnotations = simpleType.getValueAnnotations();
		for (Iterator iterator = valueAnnotations.iterator(); iterator.hasNext();) {
			ValueValidationAnnotationTypeBinding nextRule = (ValueValidationAnnotationTypeBinding) iterator.next();
			
			IAnnotationBinding resolveBinding = assignment.resolveBinding();
			Object value = resolveBinding.getValue();
			if(value != null && IBinding.NOT_FOUND_BINDING != value) {
				problemRequestor = specializeProblemRequestor(problemRequestor, target, simpleType);
				nextRule.validate(assignment.getRightHandSide(), target, resolveBinding, problemRequestor, compilerOptions);
				problemRequestor = restoreProblemRequestor();
			}
		}
	}
	
	/**
	 * process annotations specified for all fields of this annotation
	 * @param targetDataBinding TODO
	 */
	private void processSubAnnotations(Node errorNode, Node target, ITypeBinding targetTypeBinding, IDataBinding targetDataBinding, IAnnotationBinding annotation, List rules) {
		
		HashMap allAnnotationsMap = new HashMap();
		
		allAnnotationsMap.put(annotation.getName(), annotation);

		
		if(((IAnnotationTypeBinding)annotation.getType()).isComplex()){
			List fields = annotation.getAnnotations();
			for (Iterator iterator = fields.iterator(); iterator.hasNext();) {		
				AnnotationBinding annotationBinding = (AnnotationBinding) iterator.next();
				allAnnotationsMap.put(annotationBinding.getName(), annotationBinding);
			}
			
			fields = annotation.getAnnotationFields();
			for (Iterator iterator = fields.iterator(); iterator.hasNext();) {		
				IDataBinding annotationBinding = (IDataBinding) iterator.next();
				allAnnotationsMap.put(annotationBinding.getName(), annotationBinding);
			}
		}
		
		// Apply all valid rules to this parts annotations
		if(rules != null){
			for (Iterator subIter = rules.iterator(); subIter.hasNext();) {
				AnnotationValidationAnnotationTypeBinding nextRule = (AnnotationValidationAnnotationTypeBinding)subIter.next();
				
				problemRequestor = specializeProblemRequestor(problemRequestor, target, annotation);
				nextRule.validate(errorNode, target, targetTypeBinding, allAnnotationsMap, problemRequestor, compilerOptions);
				problemRequestor = restoreProblemRequestor();
			}
		}
	}
	
	private void processPartSubTypeSubAnnotations(Node errorNode, Node target, IBinding subTypeBinding, List rules) {
		
		List fields = new ArrayList();
		fields.addAll(subTypeBinding.getAnnotations());
		if(subTypeBinding.isAnnotationBinding()) {
			fields.addAll(((IAnnotationBinding) subTypeBinding).getAnnotationFields());
		}
		
		HashMap allAnnotationsMap = new HashMap();
		for (Iterator iterator = fields.iterator(); iterator.hasNext();) {		
			AnnotationBinding annotationBinding = (AnnotationBinding) iterator.next();
			allAnnotationsMap.put(annotationBinding.getName(), annotationBinding);
		}
		
		// Apply all valid rules to this parts annotations
		if(rules != null){
			for (Iterator subIter = rules.iterator(); subIter.hasNext();) {
				Object next = subIter.next();
				if(next instanceof AnnotationValidationAnnotationTypeBinding) {
					AnnotationValidationAnnotationTypeBinding nextRule = (AnnotationValidationAnnotationTypeBinding)next;
					
					nextRule.validate(errorNode, target, null, allAnnotationsMap, problemRequestor, compilerOptions);
				}
			}
		}
	}

	/**
	 * This method is very similar to the processComplexAnnotationFields method. We have two methods due to the fact that a part subtype does not contain
	 * an AnnotationExpression (@name), which means that we have to drill down from the part directly to the assignment statements inside the settings block.
	 */
	private void processPartSubTypeFields(final Part part, final IAnnotationTypeBinding partSubType){
		part.accept(new DefaultASTVisitor(){
			
			public boolean visit(Record record) {
				return true;
			}
			
			public boolean visit(SettingsBlock settingsBlock) {
				return true;
			}
			
			public boolean visit(SetValuesExpression setValuesExpression) {
				// we have found a nested annotation, start the process over
				processAnnotations(setValuesExpression, (ITypeBinding) null, null);
				return false;
			}
			
			public boolean visit(Assignment assignment){
			
				List annotations = partSubType.getFieldAnnotations(((IAnnotationTypeBinding)assignment.resolveBinding().getType()).getName());
				
				if(annotations != null){
					for (Iterator iter = annotations.iterator(); iter.hasNext();) {
						ValueValidationAnnotationTypeBinding rule = (ValueValidationAnnotationTypeBinding) iter.next();
						
						rule.validate(assignment, part, assignment.resolveBinding(), problemRequestor, compilerOptions);
					}
				}
				
				return false;
			}
		});
	}
		
	
	private void processPartSubTypeFields(final Node target, List partContents, final IAnnotationTypeBinding partSubType){
		
		for (Iterator iter = partContents.iterator(); iter.hasNext();) {
			Node content = (Node) iter.next();
			
			content.accept(new DefaultASTVisitor(){
			
				public boolean visit(SettingsBlock settingsBlock) {
					
					processComplexAnnotationFields(target, settingsBlock, partSubType);
					return false;
				}
			});
		}
	}
	
	/**
	 * Process annotations for this parts contents.
	 */
	private void processPartSubType(Part part, final IAnnotationTypeBinding subTypeAnnotation) {
		processPartSubType(part.getName(), part, part.getContents(), subTypeAnnotation);
	}
	
	private void processPartSubType(Name partName, Node partNode, List partContents, final IAnnotationTypeBinding subTypeAnnotation) {
		
		final HashMap allPartAnnotations = new HashMap();
		
		for (Iterator iter = partContents.iterator(); iter.hasNext();) {
			
			Node node = (Node)iter.next();
			
			node.accept(new DefaultASTVisitor(){
				public boolean visit(StructureItem structureItem) {
//					if(structureItem.isEmbedded()) {
//						System.out.println();
//					}
//					else {
						IDataBinding structureItemBinding = (IDataBinding) structureItem.resolveBinding();
						if(structureItemBinding != null && IBinding.NOT_FOUND_BINDING != structureItemBinding) {
							List annotations = structureItemBinding.getAnnotations();
							Node nodeForErrors;
							String canonicalItemName;
							if(structureItem.isFiller()) {
								nodeForErrors = new Node(structureItem.getOffset(), structureItem.getOffset()+1);
								canonicalItemName = "*";
							}
							else if(structureItem.isEmbedded()) {
								nodeForErrors = structureItem.getType();
								canonicalItemName = structureItem.getType().getCanonicalName();
							}
							else {
								nodeForErrors = structureItem.getName();
								canonicalItemName = structureItem.getName().getCanonicalName();
							}
							processContentAnnotations(structureItem, nodeForErrors, structureItemBinding, canonicalItemName, annotations);
						}
//					}
					return false;
				}
				
				public boolean visit(VariableFormField variableFormField) {
					IDataBinding fieldBinding = (IDataBinding) variableFormField.getName().resolveBinding();
					if(fieldBinding != null) {
						List annotations = fieldBinding.getAnnotations();
						
						processContentAnnotations(variableFormField, variableFormField.getName(), fieldBinding, variableFormField.getName().getCanonicalName(), annotations);
					}
					return false;
				}
				
				public boolean visit(ConstantFormField constantFormField) {
					IDataBinding fieldBinding = constantFormField.resolveBinding();
					if(fieldBinding != null) {
						List annotations = fieldBinding.getAnnotations();
						
						processContentAnnotations(constantFormField, constantFormField, fieldBinding, "*", annotations);
					}
					return false;
				}
				
				public boolean visit(ClassDataDeclaration classDataDeclaration) {
					List names = classDataDeclaration.getNames();
					
					for (Iterator iterator = names.iterator(); iterator.hasNext();) {
						Name name = (Name) iterator.next();
						
						IDataBinding classBinding = name.resolveDataBinding();
						// IDataBinding structureItemBinding = .resolveDataBinding();
						if(IBinding.NOT_FOUND_BINDING != classBinding && classBinding != null) {
							List annotations = classBinding.getAnnotations();
							processContentAnnotations(classDataDeclaration, name, classBinding, name.getCanonicalName(), annotations);
						}
					}
					
					return false;
				}
								
				public boolean visit(org.eclipse.edt.compiler.core.ast.Constructor constructor) {
					
					for (Iterator iter = constructor.getParameters().iterator(); iter.hasNext();) {
						FunctionParameter param = (FunctionParameter) iter.next();
						IDataBinding paramBinding = param.getName().resolveDataBinding();
						
						if(Binding.isValidBinding(paramBinding)) {
							for(Iterator annIter = subTypeAnnotation.getPartSubTypeAnnotations().iterator(); annIter.hasNext();) {
								((FieldContentValidationAnnotationTypeBinding) annIter.next()).validateFunctionParameter(param, paramBinding, problemRequestor, compilerOptions);
							}
						}
					}

					return false;
				}
				
				public boolean visit(NestedFunction nestedFunction) {
					for (Iterator iter = nestedFunction.getFunctionParameters().iterator(); iter.hasNext();) {
						FunctionParameter param = (FunctionParameter) iter.next();
						IDataBinding paramBinding = param.getName().resolveDataBinding();
						
						if(Binding.isValidBinding(paramBinding)) {
							for(Iterator annIter = subTypeAnnotation.getPartSubTypeAnnotations().iterator(); annIter.hasNext();) {
								((FieldContentValidationAnnotationTypeBinding) annIter.next()).validateFunctionParameter(param, paramBinding, problemRequestor, compilerOptions);
							}
						}
					}
					
					if(nestedFunction.hasReturnType()) {
						Type returnType = nestedFunction.getReturnType();
						ITypeBinding returnTypeBinding = returnType.resolveTypeBinding();
						
						if(Binding.isValidBinding(returnTypeBinding)) {
							for(Iterator annIter = subTypeAnnotation.getPartSubTypeAnnotations().iterator(); annIter.hasNext();) {
								((FieldContentValidationAnnotationTypeBinding) annIter.next()).validateFunctionReturnType(returnType, returnTypeBinding, (IPartBinding) ((Part) nestedFunction.getParent()).getName().resolveBinding(), problemRequestor, compilerOptions);
							}
						}
					}
					
					IDataBinding nestedFunctionBinding = nestedFunction.getName().resolveDataBinding();
					if(Binding.isValidBinding(nestedFunctionBinding)) {
						processContentAnnotations(nestedFunction, nestedFunction.getName(), nestedFunctionBinding, nestedFunction.getName().getCanonicalName(), nestedFunctionBinding.getAnnotations());
					}
					
					return false;
				}
				
				private void processContentAnnotations(Node field, Node nodeForErrors, IDataBinding containerBinding, String canonicalContainerName, List annotations){
					Map allAnnotationsMap = getAllAnnotationsMap(annotations, allPartAnnotations, nodeForErrors, containerBinding);
					// Apply all valid rules to this items annotations
					for (Iterator subIter = subTypeAnnotation.getPartSubTypeAnnotations().iterator(); subIter.hasNext();) {
						FieldContentValidationAnnotationTypeBinding rule = (FieldContentValidationAnnotationTypeBinding) subIter.next();
						runFieldContentRuleOnBindingAndChildren(rule, containerBinding, containerBinding, nodeForErrors, field, canonicalContainerName, allAnnotationsMap, allPartAnnotations, problemRequestor, false);					
					}
					
					IAnnotationTypeBinding enclosingSubtype = getEnclosingSubtype(containerBinding);
					if(Binding.isValidBinding(enclosingSubtype)) {
						IAnnotationBinding memberAnnotationsABinding = getMemberAnnotationsForSubtype(enclosingSubtype);
						if(Binding.isValidBinding(memberAnnotationsABinding)) {
							FieldContentValidationAnnotationTypeBinding rule = new DataItemPropertiesFieldContentRule(enclosingSubtype, (Object[]) memberAnnotationsABinding.getValue());
							runFieldContentRuleOnBindingAndChildren(rule, containerBinding, containerBinding, nodeForErrors, field, canonicalContainerName, allAnnotationsMap, allPartAnnotations, problemRequestor, true);						
						}
					}
				}
				
				private IAnnotationTypeBinding getEnclosingSubtype(IDataBinding containerBinding) {
					IPartBinding declaringPart = containerBinding.getDeclaringPart();
					IAnnotationBinding subTypeAnnotationBinding = declaringPart.getSubTypeAnnotationBinding();
					if(Binding.isValidBinding(subTypeAnnotationBinding)) {
						return subTypeAnnotationBinding.getAnnotationType();
					}
					return null;
				}

				private IAnnotationBinding getMemberAnnotationsForSubtype(IAnnotationTypeBinding annotationType) {					
					IAnnotationBinding annotation = annotationType.getAnnotation(StereotypeAnnotationTypeBinding.getInstance());
					if(Binding.isValidBinding(annotation)) {
						return (IAnnotationBinding) annotation.findData("memberAnnotations");
					}
					return null;
				}				
			});
		}
		
		// process part annotation rules from sub type
		if(subTypeAnnotation != null) {
			for (Iterator subIter = subTypeAnnotation.getPartTypeAnnotations().iterator(); subIter.hasNext();) {
				PartContentValidationAnnotationTypeBinding rule = (PartContentValidationAnnotationTypeBinding) subIter.next();
		
				rule.validate(partName, partNode, allPartAnnotations, problemRequestor);			
			}
		}
	}
	
	private Map getAllAnnotationsMap(List annotations, HashMap allPartAnnotations, Node field, IDataBinding targetDBinding) {
		HashMap allAnnotationsMap = new HashMap();
		for (Iterator iterator = annotations.iterator(); iterator.hasNext();) {		
			IAnnotationBinding annotationBinding = (AnnotationBinding) iterator.next();
			allAnnotationsMap.put(annotationBinding.getName(), annotationBinding);
			
			LinkedHashMap allAnnotationsForName = (LinkedHashMap)allPartAnnotations.get(annotationBinding.getName());
			
			if(allAnnotationsForName == null){
				allAnnotationsForName = new LinkedHashMap();
				allPartAnnotations.put(annotationBinding.getName(), allAnnotationsForName);
			}
			
			allAnnotationsForName.put(annotationBinding, new Object[] {field, targetDBinding});
		}
		return allAnnotationsMap;
	}
	
	private void runFieldContentRuleOnBindingAndChildren(FieldContentValidationAnnotationTypeBinding rule, IDataBinding topLevelDBinding, IDataBinding dBinding, Node nodeForErrors, Node target, String canonicalContainerName, Map allAnnotationsMap, HashMap allPartAnnotations, IProblemRequestor problemRequestor, boolean recurseIntoStructureItems) {
		runFieldContentRuleOnBindingAndChildren(rule, topLevelDBinding, new Stack(), topLevelDBinding, nodeForErrors, target, canonicalContainerName, allAnnotationsMap, allPartAnnotations, problemRequestor, recurseIntoStructureItems);
	}
	
	private void runFieldContentRuleOnBindingAndChildren(FieldContentValidationAnnotationTypeBinding rule, IDataBinding topLevelDBinding, Stack pathFromTopLevelDBinding, IDataBinding dBinding, Node nodeForErrors, Node target, String canonicalContainerName, Map allAnnotationsMap, HashMap allPartAnnotations, IProblemRequestor problemRequestor, boolean recurseIntoStructureItems) {
		rule.validate(nodeForErrors, target, dBinding, canonicalContainerName, allAnnotationsMap, problemRequestor, compilerOptions);
		ITypeBinding tBinding = dBinding.getType();
		if(tBinding != null) {
			runFieldContentRuleOnBindingAndChildren(rule, tBinding, topLevelDBinding, pathFromTopLevelDBinding, nodeForErrors, target, allPartAnnotations, problemRequestor);
		}
		
		if(recurseIntoStructureItems && IDataBinding.STRUCTURE_ITEM_BINDING == dBinding.getKind()) {
			for(Iterator iter = ((StructureItemBinding) dBinding).getChildren().iterator(); iter.hasNext();) {
				IDataBinding nextChild = (IDataBinding) iter.next();
				pathFromTopLevelDBinding.push(nextChild);
				allAnnotationsMap = getAllAnnotationsMap(topLevelDBinding.getAnnotationsFor((IDataBinding[])pathFromTopLevelDBinding.toArray(new IDataBinding[0])), allPartAnnotations, nodeForErrors, nextChild);
				pathFromTopLevelDBinding.pop();
				runFieldContentRuleOnBindingAndChildren(rule, topLevelDBinding, pathFromTopLevelDBinding, nextChild, nodeForErrors, target, nextChild.getName(), allAnnotationsMap, allPartAnnotations, problemRequestor, true);				
			}
		}
	}
	
	private void runFieldContentRuleOnBindingAndChildren(FieldContentValidationAnnotationTypeBinding rule, ITypeBinding tBinding, IDataBinding topLevelDBinding, Stack pathFromTopLevelDBinding, Node nodeForErrors, Node target, HashMap allPartAnnotations, IProblemRequestor problemRequestor) {
		if(ITypeBinding.FIXED_RECORD_BINDING == tBinding.getKind()) {
			for(Iterator iter = ((FixedRecordBinding) tBinding).getStructureItems().iterator(); iter.hasNext();) {
				IDataBinding nextChild = (IDataBinding) iter.next();
				pathFromTopLevelDBinding.push(nextChild);
				Map allAnnotationsMap = getAllAnnotationsMap(topLevelDBinding.getAnnotationsFor((IDataBinding[])pathFromTopLevelDBinding.toArray(new IDataBinding[0])), allPartAnnotations, nodeForErrors, nextChild);
				pathFromTopLevelDBinding.pop();
				runFieldContentRuleOnBindingAndChildren(rule, topLevelDBinding, pathFromTopLevelDBinding, nextChild, nodeForErrors, target, nextChild.getName(), allAnnotationsMap, allPartAnnotations, problemRequestor, true);
				
			}
		}
		else if(ITypeBinding.FLEXIBLE_RECORD_BINDING == tBinding.getKind()) {
			for(Iterator iter = ((FlexibleRecordBinding) tBinding).getDeclaredFields().iterator(); iter.hasNext();) {
				IDataBinding nextChild = (IDataBinding) iter.next();
				pathFromTopLevelDBinding.push(nextChild);
				Map allAnnotationsMap = getAllAnnotationsMap(topLevelDBinding.getAnnotationsFor((IDataBinding[])pathFromTopLevelDBinding.toArray(new IDataBinding[0])), allPartAnnotations, nodeForErrors, nextChild);
				runFieldContentRuleOnBindingAndChildren(rule, topLevelDBinding, pathFromTopLevelDBinding, nextChild, nodeForErrors, target, nextChild.getName(), allAnnotationsMap, allPartAnnotations, problemRequestor, true);
				pathFromTopLevelDBinding.pop();
			}
		}
	}
}
