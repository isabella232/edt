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
package org.eclipse.edt.compiler.internal.core.validation.annotation;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.AnnotationValidationRule;
import org.eclipse.edt.compiler.binding.FieldContentValidationRule;
import org.eclipse.edt.compiler.binding.IValidationProxy;
import org.eclipse.edt.compiler.binding.InvocationValidationRule;
import org.eclipse.edt.compiler.binding.PartContentValidationRule;
import org.eclipse.edt.compiler.binding.ValueValidationRule;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.AnnotationExpression;
import org.eclipse.edt.compiler.core.ast.ArrayAccess;
import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.BinaryExpression;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.DataItem;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.FunctionInvocation;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.core.ast.UnaryExpression;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.statement.AssignmentStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.RValueValidator;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Stereotype;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.utils.NameUtile;


/**
 * @author svihovec
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

	private IProblemRequestor specializeProblemRequestor(IProblemRequestor problemRequestor, Node target, Annotation annotation) {
		return specializeProblemRequestor(problemRequestor, target, (AnnotationType)annotation.getEClass());
	}
	
	private IProblemRequestor specializeProblemRequestor(IProblemRequestor problemRequestor, Node target, AnnotationType annotationType) {
		storedProblemRequestor = problemRequestor;
		
		IProblemRequestor result = problemRequestor;
		
		//TODO uncomment and port when data items are supported
//		if(target instanceof DataItem) {
//			result = new DataItemPropertiesProblemsProblemRequestor(problemRequestor, ((DataItem) target).getName().resolveBinding(), annotationType);
//		}
		
		return result;
	}
	
	public static IValidationProxy getValidationProxy(Annotation annot) {
		if (annot != null) {
			EClass eclass = annot.getEClass();
			if (eclass instanceof AnnotationType) {
				String proxy = ((AnnotationType)eclass).getValidationProxy();
				if (proxy != null) {
					proxy = proxy.trim();
					if (proxy.length() > 0) {
						try {
							Class c = AnnotationValidator.class.getClassLoader().loadClass(proxy);
							IValidationProxy valProxy = (IValidationProxy)c.getMethod("getInstance", (Class[])null).invoke(null, (Object[])null);
							valProxy.setType((AnnotationType)eclass);
							return valProxy;
						}
						catch(ClassNotFoundException e) {
							throw new RuntimeException(e);
						}
						catch(IllegalAccessException e) {
							throw new RuntimeException(e);
						}
						catch(InvocationTargetException e) {
							throw new RuntimeException(e);
						}
						catch (IllegalArgumentException e) {
							throw new RuntimeException(e);
						}
						catch (SecurityException e) {
							throw new RuntimeException(e);
						}
						catch (NoSuchMethodException e) {
							throw new RuntimeException(e);
						}
					}
				}
			}
		}
		return null;
	}
	
	public void validateAnnotationTarget(Node target) {
		target.accept(new DefaultASTVisitor(){
			@Override
			public boolean visit(Record record) {
				IValidationProxy proxy = null;
				Stereotype subtype = null;
				
				org.eclipse.edt.mof.egl.Type type = record.getName().resolveType();
				if (type instanceof org.eclipse.edt.mof.egl.Part) {
					subtype = ((org.eclipse.edt.mof.egl.Part)type).getSubType();
					if (subtype != null) {
						proxy = getValidationProxy(subtype);
					}
				}
				
				validateExpressions(record);

				if (proxy == null) {
					return false;
				}
				
				// process annotations on part subtype
				processPartSubTypeSubAnnotations(record.getName(), record, subtype, proxy.getAnnotationValidators());
				
				// get annotations for fields, run validation annotations on fields
				processPartSubTypeFields(record, record.getContents(), proxy);
				
				// run validation rules from subtype
				processPartSubType(record, proxy);
			
				processOverriddenAnnotationsAndSetValues(record);
				
				return false;
			}
			
			public boolean visitFunctionContainer(Part functionContainerPart, Name subType) {
				IValidationProxy proxy = null;
				Stereotype subtype = null;
				
				org.eclipse.edt.mof.egl.Type type = functionContainerPart.getName().resolveType();
				if (type instanceof org.eclipse.edt.mof.egl.Part) {
					subtype = ((org.eclipse.edt.mof.egl.Part)type).getSubType();
					if (subtype != null) {
						proxy = getValidationProxy(subtype);
					}
				}
				
				validateExpressions(functionContainerPart);

				if (proxy == null) {
					return false;
				}
				
				// process annotations on part subtype
				processPartSubTypeSubAnnotations(functionContainerPart.getName(), functionContainerPart, subtype, proxy.getAnnotationValidators());
				
				// get annotations for fields, run validation annotations on fields
				processPartSubTypeFields(functionContainerPart, functionContainerPart.getContents(), proxy);
				
				// run validation rules from subtype
				processPartSubType(functionContainerPart, proxy);
				
				processOverriddenAnnotationsAndSetValues(functionContainerPart);
				
				return false;
			}
			
			@Override
			public boolean visit(Program program) {
				visitFunctionContainer(program, program.getSubType());
				return false;
			}
			
			@Override
			public boolean visit(Library library) {
				visitFunctionContainer(library, library.getSubType());
				return false;
			}
			
			@Override
			public boolean visit(Handler handler) {
				visitFunctionContainer(handler, handler.getSubType());
				return false;
			}
			
			@Override
			public boolean visit(Interface interfaceNode) {
				visitFunctionContainer(interfaceNode, interfaceNode.getSubType());
				return false;
			}
			
			@Override
			public boolean visit(Service serviceNode) {
				visitFunctionContainer(serviceNode, serviceNode.getSubType());
				return false;
			}
			
			@Override
			public boolean visit(NestedFunction function) {
				Member m = function.getName().resolveMember();
				if (m != null) {
					processAnnotations(function, m);
				}
				return false;
			}
			
			@Override
			public boolean visit(Constructor constructor) {
				org.eclipse.edt.mof.egl.Constructor c = constructor.getBinding();
				if (c != null) {
					processAnnotations(constructor, c);
				}
				return false;
			}
			
			@Override
			public boolean visit(ExternalType externalType) {
				visitFunctionContainer(externalType, externalType.getSubType());
				return false;
			}
			
			//TODO uncomment and port when data items are supported
//			@Override
//			public boolean visit(final DataItem dataItem) {
//				DataItemBinding binding = (DataItemBinding) dataItem.getName().resolveBinding();
//				processAnnotations(dataItem, binding == null ? null : binding.getPrimitiveTypeBinding(), null);
//				processOverriddenAnnotationsAndSetValues(dataItem);				
//				return false;
//			}
			
			@Override
			public boolean visit(StructureItem structureItem) {
				processAnnotations(structureItem, structureItem.resolveMember());
				
				if (structureItem.hasSettingsBlock()) {
					processOverriddenAnnotationsAndSetValues(structureItem.getSettingsBlock());
				}
				
				if (structureItem.resolveMember() != null && structureItem.hasType() && !structureItem.hasInitializer() && !structureItem.isNullable()) {					
					validateInvocation(structureItem, getDefaultConstructor(structureItem.getType().resolveType()), BindingUtil.getDeclaringPart(structureItem.resolveMember()));					
				}
				return false;
			}
			
			@Override
			public boolean visit(ClassDataDeclaration classDataDeclaration) {			
				processAnnotations(classDataDeclaration, classDataDeclaration.getNames().get(0).resolveMember());
				
				if (classDataDeclaration.hasSettingsBlock()) {
					processOverriddenAnnotationsAndSetValues(classDataDeclaration.getSettingsBlockOpt());
				}
				
				Member field = classDataDeclaration.getNames().get(0).resolveMember();
				if (field != null && !classDataDeclaration.hasInitializer() && !classDataDeclaration.isNullable()) {					
					validateInvocation(classDataDeclaration, getDefaultConstructor(classDataDeclaration.getType().resolveType()), BindingUtil.getDeclaringPart(field));					
				}
				return false;
			}
			
			private void validateExpressions(Part part) {
				if (part.getName().resolveType() == null) {
					return;
				}
				final org.eclipse.edt.mof.egl.Part partBinding = (org.eclipse.edt.mof.egl.Part)part.getName().resolveType();
				part.accept(new AbstractASTVisitor() {
					@Override
					public boolean visit(BinaryExpression binaryExpression) {
						if (binaryExpression.getFirstExpression() != null) {
							RValueValidator validator = new RValueValidator(problemRequestor, compilerOptions, binaryExpression.getFirstExpression().resolveMember(), binaryExpression.getFirstExpression());
							validator.validate();
						}
						if (binaryExpression.getSecondExpression() != null) {
							RValueValidator validator = new RValueValidator(problemRequestor, compilerOptions, binaryExpression.getSecondExpression().resolveMember(), binaryExpression.getSecondExpression());
							validator.validate();
						}
						return true;
					}
					
					@Override
					public boolean visit(UnaryExpression unaryExpression) {
						RValueValidator validator = new RValueValidator(problemRequestor, compilerOptions, unaryExpression.getExpression().resolveMember(), unaryExpression.getExpression());
						validator.validate();
						return true;
					}
					
					@Override
					public boolean visit(ArrayAccess arrayAccess) {
						Iterator i = arrayAccess.getIndices().iterator();
						while (i.hasNext()) {
							Expression expr = (Expression)i.next();
							RValueValidator validator = new RValueValidator(problemRequestor, compilerOptions, expr.resolveMember(), expr);
							validator.validate();
						}
						return true;
					}
					
					@Override
					public boolean visit(NewExpression newExpression) {
						if (newExpression.resolveConstructor() != null) {
							validateInvocation(newExpression, newExpression.resolveConstructor(), partBinding);
						}
						if (newExpression.getType().resolveType() != null && newExpression.getType().resolveType() instanceof org.eclipse.edt.mof.egl.ArrayType) {
							ArrayType arrType = (ArrayType)newExpression.getType();
							org.eclipse.edt.mof.egl.Type baseType = BindingUtil.getBaseType(newExpression.resolveType());
							if (arrType.hasInitialSize() && !BindingUtil.isZeroLiteral(arrType.getInitialSize()) && baseType != null) {
								validateInvocation(newExpression, getDefaultConstructor(baseType), partBinding);
							}
						}
						return true;
					}
					
					@Override
					public boolean visit(FunctionInvocation functionInvocation) {
						validateInvocation(functionInvocation, functionInvocation.getTarget().resolveMember(), partBinding);
						return true;
					}
				});					
			}
			
			private void validateInvocation(Node node, Element element, org.eclipse.edt.mof.egl.Part declaringPart) {
				if (element != null) {
					for (Annotation annot : element.getAnnotations()) {
						IValidationProxy proxy = getValidationProxy(annot);
						if (proxy != null) {
							for (InvocationValidationRule rule : proxy.getInvocationValidators()) {
								rule.validate(node, element, declaringPart, problemRequestor, compilerOptions);
							}
						}
					}
				}
			}
			
			@Override
			public boolean visit(UseStatement useStatement) {			
				List<Name> names = useStatement.getNames();
				if (names.size() > 0) {
					org.eclipse.edt.mof.egl.Type type = names.get(0).resolveType();
					if (type != null) {
						processAnnotations(useStatement, type);
					}
				}
				
				return false;
			}
			
			@Override
			public boolean visit(FunctionDataDeclaration functionDataDeclaration) {
				processAnnotations(functionDataDeclaration, functionDataDeclaration.getNames().get(0).resolveMember());
				
				if (functionDataDeclaration.hasSettingsBlock()) {
					processOverriddenAnnotationsAndSetValues(functionDataDeclaration.getSettingsBlockOpt());
				}
				
				Member field = functionDataDeclaration.getNames().get(0).resolveMember();
				if (field != null && !functionDataDeclaration.hasInitializer() && !functionDataDeclaration.isNullable()) {				
					validateInvocation(functionDataDeclaration, getDefaultConstructor(functionDataDeclaration.getType().resolveType()), BindingUtil.getDeclaringPart(field));
				}

				return false;
			}
		});
	}
	
	/**
	 * Process annotations that appear on a DataItem or Field target (structure item, global variable, local variable)
	 * @param targetElement
	 */
	private void processAnnotations(final Node target, final Element targetElement) {
		target.accept(new AbstractASTExpressionVisitor() {
			@Override
			public boolean visit(ClassDataDeclaration classDataDeclaration) {
				if(classDataDeclaration.hasSettingsBlock()) {
					classDataDeclaration.getSettingsBlockOpt().accept(this);
				}
				return false;
			}
			
			@Override
			public boolean visit(FunctionDataDeclaration functionDataDeclaration) {
				if(functionDataDeclaration.hasSettingsBlock()) {
					functionDataDeclaration.getSettingsBlockOpt().accept(this);
				}
				return false;
			}
			
			@Override
			public boolean visit(NestedFunction nestedFunction) {
				
				final AbstractASTExpressionVisitor superVisitor = this;
				
				DefaultASTVisitor visitor = new DefaultASTVisitor() {
					@Override
					public boolean visit(NestedFunction nestedFunction) {
						return true;
					}
					@Override
					public boolean visit(SettingsBlock settingsBlock) {
						settingsBlock.accept(superVisitor);
						return false;
					}
				};
				nestedFunction.accept(visitor);
				return false;
			}
			
			@Override
			public boolean visit(UseStatement useStatement) {
				if(useStatement.hasSettingsBlock()) {
					useStatement.getSettingsBlock().accept(this);
				}
				return false;
			}
			
			@Override
			public boolean visit(StructureItem structureItem) {
				if(structureItem.hasSettingsBlock()) {
					structureItem.getSettingsBlock().accept(this);
				}
				return false;
			}
			
			@Override
			public boolean visit(AnnotationExpression annotationExpression) {
				Annotation annot = annotationExpression.resolveAnnotation(); 
				if (annot != null) {
					IValidationProxy proxy = getValidationProxy(annot);
					if (proxy != null) {
						processSubAnnotations(annotationExpression, target, targetElement, annot, proxy.getAnnotationValidators());
					}
				}
				return false;
			}
			
			@Override
			public boolean visit(SetValuesExpression setValuesExpression) {				
				Expression annotationExpression = setValuesExpression.getExpression();
				
				Object annotationExpressionDBinding = annotationExpression.resolveElement();
				if (annotationExpressionDBinding instanceof Annotation) {
					SettingsBlock settingsBlock = setValuesExpression.getSettingsBlock();
					
					IValidationProxy proxy = getValidationProxy((Annotation)annotationExpressionDBinding);
					if (proxy != null) {
						processSubAnnotations(annotationExpression, target, targetElement, (Annotation)annotationExpressionDBinding, proxy.getAnnotationValidators());
						processComplexAnnotationFields(target, settingsBlock, proxy);
					}
				}
				return false;
			}
			
			@Override
			public boolean visit(Assignment assignment) {
				Annotation aBinding = assignment.resolveBinding();
				if (aBinding != null) {
					IValidationProxy validationProxy = getValidationProxy(aBinding);
					processSubAnnotations(assignment, target, targetElement, aBinding, validationProxy.getAnnotationValidators());
					processComplexAnnotationFields(target, assignment, validationProxy);
				}
				return false;
			}
			
			@Override
			public boolean visitExpression(final Expression expression) {
				target.accept(new DefaultASTVisitor() {
					@Override
					public boolean visit(ClassDataDeclaration classDataDeclaration) {
						classDataDeclaration.getType().accept(this);
						return false;					
					}
					
					@Override
					public boolean visit(StructureItem structureItem) {
						structureItem.getType().accept(this);
						return false;
					}
					
					@Override
					public boolean visit(ArrayType arrayType) {
						if (arrayType.hasInitialSize()) {
							org.eclipse.edt.mof.egl.Type targetTypeBinding = null;
							if (targetElement instanceof org.eclipse.edt.mof.egl.Type) {
								targetTypeBinding = (org.eclipse.edt.mof.egl.Type)targetElement;
							}
							
							if (targetTypeBinding instanceof org.eclipse.edt.mof.egl.ArrayType) {
								org.eclipse.edt.mof.egl.Type exprType = expression.resolveType();
								if (exprType != null) {
									if (!IRUtils.isMoveCompatible(((org.eclipse.edt.mof.egl.ArrayType)targetTypeBinding).getElementType(), exprType, null)) {
										problemRequestor.acceptProblem(
											expression,
											IProblemRequestor.ASSIGNMENT_STATEMENT_TYPE_MISMATCH,
											new String[] {
												exprType.getTypeSignature(),
												((org.eclipse.edt.mof.egl.ArrayType)targetTypeBinding).getElementType().getTypeSignature(),
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
			@Override
			public boolean visit(Record record) {
				return true;
			}
			
			@Override
			public boolean visit(Program program) {
				return true;
			}
			
			@Override
			public boolean visit(Library library) {
				return true;
			}
			
			@Override
			public boolean visit(Handler handler) {
				return true;
			}
			
			@Override
			public boolean visit(org.eclipse.edt.compiler.core.ast.EGLClass eglClass) {
				return true;
			};
			
			@Override
			public boolean visit(DataItem dataItem) {
				return true;
			}
			
			@Override
			public boolean visit(SettingsBlock settingsBlock) {
				return true;
			}
			
			@Override
			public boolean visit(SetValuesExpression setValuesExpression) {
				Expression annotationExpression = setValuesExpression.getExpression();
				
				Member annotationExpressionDBinding = annotationExpression.resolveMember(); 
				if (annotationExpressionDBinding != null) {
					processOverriddenAnnotationsAndSetValues(setValuesExpression.getSettingsBlock());
				}
				return false;
			}
			
			@Override
			public boolean visit(final Assignment assignment){
				Annotation aBinding = assignment.resolveBinding();
				if (aBinding == null) {
					org.eclipse.edt.mof.egl.Type lhType = assignment.getLeftHandSide().resolveType();
					org.eclipse.edt.mof.egl.Type rhType = assignment.getRightHandSide().resolveType();
					if (lhType != null && rhType != null && !(lhType instanceof AnnotationType)) {
						new AssignmentStatementValidator(problemRequestor, 	compilerOptions, null).validateAssignment(
								assignment.getOperator(),
								assignment.getLeftHandSide(),
								assignment.getRightHandSide(),
								assignment.getLeftHandSide().resolveType(),
								assignment.getRightHandSide().resolveType(),
								assignment.getLeftHandSide().resolveMember(),
								assignment.getRightHandSide().resolveMember());
					}
				}
				return false;
			}
		});
	}
	
	/**
	 * Process annotations found on fields of this complex annotation.
	 */
	private void processComplexAnnotationFields(final Node target, final SettingsBlock settingsBlock, final IValidationProxy proxy) {
		final boolean[] processed = {false};
		settingsBlock.accept(new AbstractASTExpressionVisitor() {
			@Override
			public boolean visit(SetValuesExpression setValuesExpression) {
				// we have found a nested annotation, start the process over
				processAnnotations(setValuesExpression, null);
				processed[0] = true;
				return false;
			}
			
			@Override
			public boolean visit(AnnotationExpression annotationExpression) {
				// we have found a nested annotation, start the process over
				processAnnotations(annotationExpression, null);
				processed[0] = true;
				return false;
			}
			
			@Override
			public boolean visit(Assignment assignment){
				processComplexAnnotationFields(target, assignment, proxy);
				processed[0] = true;
				return true;
			}
		});
		
		// When there's just 1 field users can omit the name: @MyAnnot{"abc"} instead of @MyAnnot{value = "abc"}
		if (!processed[0] && settingsBlock.getSettings().size() == 1 && settingsBlock.getParent() instanceof SetValuesExpression) {
			Object element = ((SetValuesExpression)settingsBlock.getParent()).getExpression().resolveElement();
			if (element instanceof Annotation) {
				Annotation annot = (Annotation)element;
				List<EField> fields = annot.getEClass().getEFields();
				if (fields.size() == 1) {
					String name = NameUtile.getAsName(fields.get(0).getName());
					Object value = annot.getValue(name);
					if (value != null) {
						List<ValueValidationRule> rules = proxy.getFieldValidators(name);
						if (rules != null && rules.size() > 0) {
							for (ValueValidationRule nextRule : rules) {
								nextRule.validate(settingsBlock.getParent(), target, annot, problemRequestor, compilerOptions);
							}
						}
					}
				}
			}
		}
	}
	
	private void processComplexAnnotationFields(Node target, Assignment assignment, IValidationProxy proxy) {
		Annotation aBinding = assignment.resolveBinding();
		
		if (aBinding != null && proxy != null) {
			String name = NameUtile.getAsName(assignment.getLeftHandSide().getCanonicalString());
			Object value = aBinding.getValue(name);
			if (value != null) {
				List<ValueValidationRule> annotations = proxy.getFieldValidators(name);
				if (annotations != null) {
					for (ValueValidationRule nextRule : annotations) {
						nextRule.validate(assignment.getRightHandSide(), target, aBinding, problemRequestor, compilerOptions);
					}
				}
			}
		}
	}
	
	/**
	 * process annotations specified for all fields of this annotation
	 */
	private void processSubAnnotations(Node errorNode, Node target, Element targetElement, Annotation annotation, List<AnnotationValidationRule> rules) {
		if (rules != null && rules.size() > 0) {
			// Collect all annotations and fields from the annotation, and also add the annotation itself. Users can retrieve them by name if they need the values.
			Map<String, Object> allAnnotationsAndFieldsMap = new HashMap();
			allAnnotationsAndFieldsMap.put(NameUtile.getAsName(annotation.getEClass().getName()), annotation);
			
			for (Annotation annot : annotation.getAnnotations()) {
				allAnnotationsAndFieldsMap.put(NameUtile.getAsName(annot.getEClass().getName()), annot);
			}
			
			for (EField efield : annotation.getEClass().getEFields()) {
				Object value = annotation.getValue(efield.getName());
				if (value != null) {
					allAnnotationsAndFieldsMap.put(NameUtile.getAsName(efield.getName()), value);
				}
			}
			
			// Apply all valid rules to this parts annotations
			for (AnnotationValidationRule rule : rules) {
				problemRequestor = specializeProblemRequestor(problemRequestor, target, annotation);
				rule.validate(errorNode, target, targetElement, allAnnotationsAndFieldsMap, problemRequestor, compilerOptions);
				problemRequestor = restoreProblemRequestor();
			}
		}
	}
	
	private void processPartSubTypeSubAnnotations(Node errorNode, Node target, Annotation subTypeBinding, List<AnnotationValidationRule> rules) {
		if (rules != null && rules.size() > 0) {
			// Collect all annotations and fields from the subtype, and also add the subtype itself. Users can retrieve them by name if they need the values.
			Map<String, Object> allAnnotationsAndFieldsMap = new HashMap();
			allAnnotationsAndFieldsMap.put(NameUtile.getAsName(subTypeBinding.getEClass().getName()), subTypeBinding);
			
			for (Annotation annot : subTypeBinding.getAnnotations()) {
				allAnnotationsAndFieldsMap.put(NameUtile.getAsName(annot.getEClass().getName()), annot);
			}
			
			for (EField efield : subTypeBinding.getEClass().getEFields()) {
				Object value = subTypeBinding.getValue(efield.getName());
				if (value != null) {
					allAnnotationsAndFieldsMap.put(NameUtile.getAsName(efield.getName()), value);
				}
			}
			
			// Apply all valid rules to this parts annotations
			for (AnnotationValidationRule rule : rules) {
				rule.validate(errorNode, target, null, allAnnotationsAndFieldsMap, problemRequestor, compilerOptions);
			}
		}
	}

	private void processPartSubTypeFields(final Node target, List<Node> partContents, final IValidationProxy proxy){
		for (Node node : partContents) {
			node.accept(new DefaultASTVisitor() {
				@Override
				public boolean visit(SettingsBlock settingsBlock) {
					processComplexAnnotationFields(target, settingsBlock, proxy);
					return false;
				}
			});
		}
	}
	
	/**
	 * Process annotations for this parts contents.
	 */
	private void processPartSubType(Part part, IValidationProxy proxy) {
		processPartSubType(part.getName(), part, part.getContents(), proxy);
	}
	
	private void processPartSubType(Name partName, Node partNode, List<Node> partContents, final IValidationProxy proxy) {
		
		final Map<String, Map<Annotation, Object[]>> allPartAnnotations = new HashMap();
		
		for (Node node : partContents) {
			node.accept(new DefaultASTVisitor() {
				@Override
				public boolean visit(StructureItem structureItem) {
//					if(structureItem.isEmbedded()) {
//						System.out.println();
//					}
//					else {
						Member structureItemBinding = structureItem.resolveMember();
						if (structureItemBinding != null) {
							List<Annotation> annotations = structureItemBinding.getAnnotations();
							Node nodeForErrors = structureItem.getName();
							String canonicalItemName = structureItem.getName().getCanonicalName();
							processContentAnnotations(structureItem, nodeForErrors, structureItemBinding, canonicalItemName, annotations);
						}
//					}
					return false;
				}
				
				@Override
				public boolean visit(ClassDataDeclaration classDataDeclaration) {
					List<Name> names = classDataDeclaration.getNames();
					for (Name name : names) {
						Member classBinding = name.resolveMember();
						if (classBinding != null) {
							List<Annotation> annotations = classBinding.getAnnotations();
							processContentAnnotations(classDataDeclaration, name, classBinding, name.getCanonicalName(), annotations);
						}
					}
					return false;
				}
				
				@Override
				public boolean visit(Constructor constructor) {
					for (Iterator iter = constructor.getParameters().iterator(); iter.hasNext();) {
						FunctionParameter param = (FunctionParameter) iter.next();
						Member paramBinding = param.getName().resolveMember();
						
						if (paramBinding != null) {
							for (FieldContentValidationRule rule : proxy.getPartSubTypeValidators()) {
								rule.validateFunctionParameter(param, paramBinding, problemRequestor, compilerOptions);
							}
						}
					}
					return false;
				}
				
				@Override
				public boolean visit(NestedFunction nestedFunction) {
					for (Iterator iter = nestedFunction.getFunctionParameters().iterator(); iter.hasNext();) {
						FunctionParameter param = (FunctionParameter) iter.next();
						Member paramBinding = param.getName().resolveMember();
						
						if (paramBinding != null) {
							for (FieldContentValidationRule rule : proxy.getPartSubTypeValidators()) {
								rule.validateFunctionParameter(param, paramBinding, problemRequestor, compilerOptions);
							}
						}
					}
					
					if (nestedFunction.hasReturnType()) {
						Type returnType = nestedFunction.getReturnType();
						org.eclipse.edt.mof.egl.Type returnTypeBinding = returnType.resolveType();
						
						if (returnTypeBinding != null) {
							for (FieldContentValidationRule rule : proxy.getPartSubTypeValidators()) {
								rule.validateFunctionReturnType(returnType, returnTypeBinding, ((Part)nestedFunction.getParent()).getName().resolveMember(), problemRequestor, compilerOptions);
							}
						}
					}
					
					Member nestedFunctionBinding = nestedFunction.getName().resolveMember();
					if (nestedFunctionBinding != null) {
						processContentAnnotations(nestedFunction, nestedFunction.getName(), nestedFunctionBinding, nestedFunction.getName().getCanonicalName(), nestedFunctionBinding.getAnnotations());
					}
					
					return false;
				}
				
				private void processContentAnnotations(Node field, Node nodeForErrors, Member containerBinding, String canonicalContainerName, List<Annotation> annotations) {
					Map<String, Annotation> allAnnotationsMap = getAllAnnotationsMap(annotations, allPartAnnotations, nodeForErrors, containerBinding);
					// Apply all valid rules to this items annotations
					for (FieldContentValidationRule rule : proxy.getPartSubTypeValidators()) {
						runFieldContentRuleOnBindingAndChildren(rule, containerBinding, containerBinding, nodeForErrors, field, canonicalContainerName, allAnnotationsMap, allPartAnnotations, problemRequestor);					
					}
					
					//TODO uncomment and port when data items are supported
//					AnnotationType enclosingSubtype = getEnclosingSubtype(containerBinding);
//					if (enclosingSubtype != null) {
//						List<AnnotationType> memberAnnotationsABinding = getMemberAnnotationsForSubtype(enclosingSubtype);
//						if (memberAnnotationsABinding != null && memberAnnotationsABinding.size() > 0) {
//							FieldContentValidationRule rule = new DataItemPropertiesFieldContentRule(enclosingSubtype, memberAnnotationsABinding.toArray());
//							runFieldContentRuleOnBindingAndChildren(rule, containerBinding, containerBinding, nodeForErrors, field, canonicalContainerName, allAnnotationsMap, allPartAnnotations, problemRequestor);						
//						}
//					}
				}
//				
//				private AnnotationType getEnclosingSubtype(Member containerBinding) {
//					IPartBinding declaringPart = containerBinding.getDeclaringPart();
//					Annotation subTypeAnnotationBinding = declaringPart.getSubTypeAnnotationBinding();
//					if (subTypeAnnotationBinding != null) {
//						return subTypeAnnotationBinding.getAnnotationType();
//					}
//					return null;
//				}
//
//				private List<AnnotationType> getMemberAnnotationsForSubtype(AnnotationType annotationType) {
//					if (annotationType instanceof StereotypeType) {
//						return ((StereotypeType)annotationType).getMemberAnnotations();
//					}
//					return null;
//				}				
			});
		}
		
		// process part annotation rules from sub type
		if (proxy != null) {
			for (PartContentValidationRule rule : proxy.getPartTypeValidators()) {
				rule.validate(partName, partNode, allPartAnnotations, problemRequestor);			
			}
		}
	}
	
	private Map<String, Annotation> getAllAnnotationsMap(List<Annotation> annotations, Map<String, Map<Annotation, Object[]>> allPartAnnotations,
			Node field, Member targetDBinding) {
		Map<String, Annotation> allAnnotationsMap = new HashMap();
		for (Annotation annotationBinding : annotations) {
			allAnnotationsMap.put(NameUtile.getAsName(annotationBinding.getEClass().getName()), annotationBinding);
			
			Map<Annotation, Object[]> allAnnotationsForName = (LinkedHashMap)allPartAnnotations.get(NameUtile.getAsName(annotationBinding.getEClass().getName()));
			
			if (allAnnotationsForName == null) {
				allAnnotationsForName = new LinkedHashMap();
				allPartAnnotations.put(NameUtile.getAsName(annotationBinding.getEClass().getName()), allAnnotationsForName);
			}
			
			allAnnotationsForName.put(annotationBinding, new Object[] {field, targetDBinding});
		}
		return allAnnotationsMap;
	}
	
	private void runFieldContentRuleOnBindingAndChildren(FieldContentValidationRule rule, Member topLevelDBinding, Member dBinding, Node nodeForErrors, Node target, String canonicalContainerName, Map<String, Annotation> allAnnotationsMap, Map<String, Map<Annotation, Object[]>> allPartAnnotations, IProblemRequestor problemRequestor) {
		rule.validate(nodeForErrors, target, dBinding, canonicalContainerName, allAnnotationsMap, problemRequestor, compilerOptions);
		org.eclipse.edt.mof.egl.Type tBinding = dBinding.getType();
		if (tBinding != null) {
			runFieldContentRuleOnBindingAndChildren(rule, tBinding, topLevelDBinding, nodeForErrors, target, allPartAnnotations, problemRequestor);
		}
	}
	
	private void runFieldContentRuleOnBindingAndChildren(FieldContentValidationRule rule, org.eclipse.edt.mof.egl.Type tBinding, Member topLevelDBinding, Node nodeForErrors, Node target, Map<String, Map<Annotation, Object[]>> allPartAnnotations, IProblemRequestor problemRequestor) {
		if (tBinding instanceof org.eclipse.edt.mof.egl.Record) {
			for (Field nextChild : ((org.eclipse.edt.mof.egl.Record)tBinding).getFields()) {
				//TODO property overrides need to be supported
				Map<String, Annotation> allAnnotationsMap = getAllAnnotationsMap(nextChild.getAnnotations(), allPartAnnotations, nodeForErrors, nextChild);
				runFieldContentRuleOnBindingAndChildren(rule, topLevelDBinding, nextChild, nodeForErrors, target, nextChild.getName(), allAnnotationsMap, allPartAnnotations, problemRequestor);
			}
		}
	}
	
	private org.eclipse.edt.mof.egl.Constructor getDefaultConstructor(org.eclipse.edt.mof.egl.Type type) {
		if (type.getClassifier() instanceof StructPart) {
			for (org.eclipse.edt.mof.egl.Constructor con : ((StructPart)type.getClassifier()).getConstructors()) {
				if (con.getParameters().size() == 0) {
					return con;
				}
			}
		}
		return null;
	}
}
