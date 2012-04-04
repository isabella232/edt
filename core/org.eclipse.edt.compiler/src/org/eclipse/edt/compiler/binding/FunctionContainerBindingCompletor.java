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
package org.eclipse.edt.compiler.binding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.AnnotationLeftHandScope;
import org.eclipse.edt.compiler.internal.core.lookup.FunctionContainerScope;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.ResolutionException;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.ParameterModifierKind;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author winghong
 */
public abstract class FunctionContainerBindingCompletor extends AbstractBinder {

    private FunctionContainerBinding functionContainerBinding;

    protected IProblemRequestor problemRequestor;

    protected Set definedDataNames = new HashSet();

    protected Set definedFunctionNames = new HashSet();

    protected PartSubTypeAndAnnotationCollector partSubTypeAndAnnotationCollector;

    protected FormGroupBinding mainFormGroup = null;
    protected Set usedForms = new HashSet();

    private FormGroupBinding helpFormGroup = null;

    private Set usedTypes = new HashSet();

    private Name helpFormGroupName;

    private List dataDeclarations = new ArrayList();

	private FunctionContainerScope functionContainerScope;

    public FunctionContainerBindingCompletor(FunctionContainerBinding functionContainerBinding, Scope currentScope,
            IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(currentScope, functionContainerBinding, dependencyRequestor, compilerOptions);
        this.problemRequestor = problemRequestor;
        this.functionContainerBinding = functionContainerBinding;
    }

    public PartSubTypeAndAnnotationCollector getPartSubTypeAndAnnotationCollector() {
        if (partSubTypeAndAnnotationCollector == null) {
            partSubTypeAndAnnotationCollector = new PartSubTypeAndAnnotationCollector(functionContainerBinding, this, currentScope, problemRequestor);
        }
        return partSubTypeAndAnnotationCollector;
    }

    void endVisitFunctionContainer(Part part) {
    	functionContainerBinding.setValid(true);
        if (helpFormGroup != null && mainFormGroup == null) {
            problemRequestor.acceptProblem(helpFormGroupName, IProblemRequestor.PROGRAM_USE_STATEMENT_HELP_GROUP_WITH_NO_MAIN_GROUP);
        }
        convertItemsToNullableIfNeccesary();
    }

    public boolean visit(ClassDataDeclaration classDataDeclaration) {
    	boolean varIsReadOnly = false;
        ITypeBinding typeBinding = null;
        try {
            typeBinding = bindType(classDataDeclaration.getType());
        } catch (ResolutionException e) {
            problemRequestor
                    .acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
            if(classDataDeclaration.hasSettingsBlock()) {
            	bindNamesToNotFound(classDataDeclaration.getSettingsBlockOpt());
            }
            return false; // Do not create the class field bindings if the type
            // cannot be resolved
        }

        dataDeclarations.add(classDataDeclaration);
        boolean isConstantDeclaration = classDataDeclaration.isConstant();
        Object constantValue = null;
        if (isConstantDeclaration) {
            constantValue = getConstantValue(classDataDeclaration.getInitializer(), typeBinding, ((Name) classDataDeclaration.getNames().get(0)).getCanonicalName(), problemRequestor);
        }
        Iterator i = classDataDeclaration.getNames().iterator();
        while (i.hasNext()) {
            Name name = (Name) i.next();
            String dataName = name.getIdentifier();

            ClassFieldBinding fieldBinding = isConstantDeclaration ? new ClassConstantBinding(name.getCaseSensitiveIdentifier(), functionContainerBinding,
                    typeBinding, constantValue) : new ClassFieldBinding(name.getCaseSensitiveIdentifier(), functionContainerBinding, typeBinding);
            
            fieldBinding.setIsPrivate(classDataDeclaration.isPrivate());
            fieldBinding.setIsReadOnly(varIsReadOnly);

            if (definedDataNames.contains(dataName) || definedFunctionNames.contains(dataName)) {
                problemRequestor.acceptProblem(name, IProblemRequestor.DUPLICATE_NAME_ACROSS_LISTS, new String[] { name.getCanonicalName(),
                        functionContainerBinding.getName() });
            } else {
                functionContainerBinding.addClassField(fieldBinding);
                definedDataNames.add(dataName);
            }
            name.setBinding(fieldBinding);
        }

        return false;
    }

    
    private void processDataDeclarationsSettingsBlocks() {
        Iterator i = dataDeclarations.iterator();
        while (i.hasNext()) {
            processSettingsBlock((ClassDataDeclaration)i.next(), functionContainerBinding, getFunctionContainerScope(), problemRequestor);
        }
    }
        
    public boolean visit(NestedFunction nestedFunction) {
        String name = nestedFunction.getName().getIdentifier();
        
        FunctionBinding functionBinding = new FunctionBinding(nestedFunction.getName().getCaseSensitiveIdentifier(), functionContainerBinding);
        FunctionBindingCompletor functionBindingCompletor = new FunctionBindingCompletor(functionContainerBinding, getFunctionContainerScope(),
                functionBinding, dependencyRequestor, problemRequestor, compilerOptions);
        nestedFunction.accept(functionBindingCompletor);
        NestedFunctionBinding nestedFunctionBinding = new NestedFunctionBinding(functionBinding.getCaseSensitiveName(), functionContainerBinding, functionBinding);
        nestedFunction.getName().setBinding(nestedFunctionBinding);
        List removeThese = new ArrayList();
        
        if (definedDataNames.contains(name)) {
        	problemRequestor.acceptProblem(nestedFunction.getName(), IProblemRequestor.DUPLICATE_NAME_ACROSS_LISTS, new String[] { nestedFunction.getName().getCanonicalName(), functionContainerBinding.getCaseSensitiveName() });
        } else if(definedFunctionNames.contains(name)) {
        	for(Iterator iter = functionContainerBinding.getDeclaredFunctions().iterator(); iter.hasNext();) {
        		IDataBinding fDataBinding = (IDataBinding) iter.next();
        		IFunctionBinding fBinding = (IFunctionBinding) fDataBinding.getType();
        		if(TypeCompatibilityUtil.functionSignituresAreIdentical(fBinding, functionBinding, compilerOptions, false, false)) {
        			if (fBinding.isImplicit()) {
        				//remove the implicit and add the new function
        				removeThese.add(fDataBinding);
        				
        				if (!isOverride(nestedFunction)) {
    	        			problemRequestor.acceptProblem(
    		        				nestedFunction.getName(),
    		        				IProblemRequestor.OVERRIDING_IMPLICIT_FUNCTION,
    		        				IMarker.SEVERITY_WARNING,
    		        				new String[] {
    		        					nestedFunction.getName().getCanonicalName()
    		        				});
        				}
        			}
        			else {
	        			problemRequestor.acceptProblem(
	        				nestedFunction.getName(),
	        				IProblemRequestor.DUPLICATE_NAME_IN_FILE,
	        				new String[] {
	        					IEGLConstants.KEYWORD_FUNCTION,
	        					nestedFunction.getName().getCanonicalName()
	        				});
	        			return false;
        			}
        		}
        	}
        } else {
            definedFunctionNames.add(name);
        }        
        
        functionContainerBinding.addDeclaredFunction(nestedFunctionBinding);
        functionContainerBinding.getDeclaredFunctions().removeAll(removeThese);
        
        return false;
    }
    
    boolean isOverride(NestedFunction node) {
    	final boolean[] override = new boolean[1];
    	DefaultASTVisitor visitor =  new DefaultASTVisitor() {
    		public boolean visit(NestedFunction nestedFunction) {
    			return true;
    		}
    		public boolean visit(SettingsBlock settingsBlock) {
    			return true;
    		}
    		
    		public boolean visit(org.eclipse.edt.compiler.core.ast.SetValuesExpression setValuesExpression) {
    			setValuesExpression.getExpression().accept(this);
    			return false;
    		}
    		
    		public boolean visit(org.eclipse.edt.compiler.core.ast.AnnotationExpression annotationExpression) {
    			String name = annotationExpression.getCanonicalString();
    			if (InternUtil.intern(name) == InternUtil.intern("override")) {
    				override[0] = true;
    			}
    			return false;
    		}
    	};
    	node.accept(visitor);
    	
    	return override[0];
    }

    protected void processSettingsBlocks() {
        processDataDeclarationsSettingsBlocks();
        if (getPartSubTypeAndAnnotationCollector().getSettingsBlocks().size() > 0) {
            AnnotationLeftHandScope scope = new AnnotationLeftHandScope(currentScope, functionContainerBinding,
                    functionContainerBinding, functionContainerBinding, -1, functionContainerBinding);
            //If the part type is specified on the part (as opposed to with an
            // annotation),
            //then create an annotation scope to handle resolution of subtype
            // dependent properties
            if (!getPartSubTypeAndAnnotationCollector().isFoundSubTypeInSettingsBlock()) {
                if (getPartSubTypeAndAnnotationCollector().getSubTypeAnnotationBinding() == null) {
                    if (getDefaultSubType() != null) {
                        AnnotationBinding annotation = new AnnotationBinding(getDefaultSubType().getCaseSensitiveName(), functionContainerBinding,
                                getDefaultSubType());
                        functionContainerBinding.addAnnotation(annotation);
                        scope = new AnnotationLeftHandScope(scope, annotation, annotation.getType(), annotation, -1, functionContainerBinding);
                    }
                } else {
                    scope = new AnnotationLeftHandScope(scope, getPartSubTypeAndAnnotationCollector().getSubTypeAnnotationBinding(), getPartSubTypeAndAnnotationCollector().getSubTypeAnnotationBinding().getType(),
                            getPartSubTypeAndAnnotationCollector().getSubTypeAnnotationBinding(), -1, functionContainerBinding);
                }
            }
            Scope fcScope = getFunctionContainerScope();
            fcScope.startReturningTopLevelFunctions();
            SettingsBlockAnnotationBindingsCompletor blockCompletor = new SettingsBlockAnnotationBindingsCompletor(fcScope, functionContainerBinding, scope,
                    dependencyRequestor, problemRequestor, compilerOptions);
            Iterator i = getPartSubTypeAndAnnotationCollector().getSettingsBlocks().iterator();
            while (i.hasNext()) {
                SettingsBlock block = (SettingsBlock) i.next();
                block.accept(blockCompletor);
            }
        } else {
            if (getPartSubTypeAndAnnotationCollector().getSubTypeAnnotationBinding() == null) {
                if (getDefaultSubType() != null) {
                    AnnotationBinding annotation = new AnnotationBinding(getDefaultSubType().getCaseSensitiveName(), functionContainerBinding,
                            getDefaultSubType());
                    functionContainerBinding.addAnnotation(annotation);
                }
            }
        }
    }

    protected abstract IPartSubTypeAnnotationTypeBinding getDefaultSubType();

    public boolean visit(UseStatement useStatement) {
        for (Iterator iter = useStatement.getNames().iterator(); iter.hasNext();) {
            Name nextName = (Name) iter.next();

            ITypeBinding typeBinding = null;
            try {
                typeBinding = bindTypeName(nextName, true);
            } catch (ResolutionException e) {
                problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e
                        .getInserts());
                useStatement.setUsedTypeBinding(new UsedTypeBinding(IBinding.NOT_FOUND_BINDING));
                if (useStatement.hasSettingsBlock()) {
                	bindNamesToNotFound(useStatement.getSettingsBlock());
                }
                continue;
            }

            if (usedTypes.contains(typeBinding)) {
                problemRequestor.acceptProblem(nextName, IProblemRequestor.DUPLICATE_USE_NAME, new String[] {
                        nextName.getCanonicalString(), functionContainerBinding.getCaseSensitiveName() });

            } else {
                usedTypes.add(typeBinding);
            }
            
            if(ITypeBinding.LIBRARY_BINDING == typeBinding.getKind()) {
            	getFunctionContainerScope().addUsedLibrary(typeBinding);
            }
            else if (typeBinding.getKind() == ITypeBinding.FORMGROUP_BINDING || typeBinding.getKind() == ITypeBinding.FORM_BINDING) {
                FormGroupBinding formGroupBinding = null;
                if(ITypeBinding.FORMGROUP_BINDING == typeBinding.getKind()) {
                	formGroupBinding = (FormGroupBinding) typeBinding;                	
                }
                else {
                	if (nextName instanceof QualifiedName) {
                		IBinding qnBinding = ((QualifiedName) nextName).getQualifier().resolveBinding();
                		if (Binding.isValidBinding(qnBinding) && qnBinding.isTypeBinding() && ((ITypeBinding)qnBinding).getKind() == ITypeBinding.FORMGROUP_BINDING) {
                        	formGroupBinding = (FormGroupBinding) ((QualifiedName) nextName).getQualifier().resolveBinding();
                        	usedForms.add(typeBinding);
                        	getFunctionContainerScope().setMainFormGroup(usedForms);
                		}
                	}
                }
                if (formGroupBinding == null) {
                    problemRequestor.acceptProblem(nextName, IProblemRequestor.FORM_MUST_BE_QUALIFIED_BY_FORMGROUP, new String[] {
                            nextName.getCanonicalString()});
                    return false;
                }
                
                if (!formGroupBinding.isValid()) {
                    formGroupBinding = (FormGroupBinding) formGroupBinding.realize();
                }
                UsedTypeBinding usedType = new UsedTypeBinding(formGroupBinding);
                useStatement.setUsedTypeBinding(usedType);
                if (useStatement.hasSettingsBlock()) {
                    AnnotationLeftHandScope scope = new AnnotationLeftHandScope(currentScope, usedType, formGroupBinding, usedType, -1, functionContainerBinding);
                    SettingsBlockAnnotationBindingsCompletor blockCompletor = new SettingsBlockAnnotationBindingsCompletor(currentScope, functionContainerBinding, 
                            scope, dependencyRequestor, problemRequestor, compilerOptions);
                    useStatement.getSettingsBlock().accept(blockCompletor);
                }
                if (usedType.isHelpGroup()) {
                    if (helpFormGroup == null) {
                        helpFormGroup = formGroupBinding;
                        helpFormGroupName = nextName;
                    } else {
                        problemRequestor.acceptProblem(nextName, IProblemRequestor.PROGRAM_USE_STATEMENT_TOO_MANY_HELP_GROUP_PROPERTIES,
                                new String[] { nextName.getCanonicalString(), functionContainerBinding.getCaseSensitiveName() });
                    }
                } else {
                	if(ITypeBinding.FORMGROUP_BINDING == typeBinding.getKind()) {
                		usedForms.addAll(formGroupBinding.getForms());
                		getFunctionContainerScope().setMainFormGroup(usedForms);
                	}
                    if (mainFormGroup == null) {
                        mainFormGroup = formGroupBinding;
                    } else if(mainFormGroup != formGroupBinding) {                    	
                        problemRequestor.acceptProblem(nextName, IProblemRequestor.PROGRAM_USE_STATEMENT_TOO_MANY_FORMGROUPS, new String[] {
                                nextName.getCanonicalString(), functionContainerBinding.getCaseSensitiveName() });
                    }
                }

            }
        }
        return false;
    }
    
    protected FunctionContainerScope getFunctionContainerScope() {
    	if(functionContainerScope == null) {
    		functionContainerScope = new FunctionContainerScope(currentScope, functionContainerBinding);
    	}
    	return functionContainerScope;
    }
    
    protected void addImplicitFieldsFromAnnotations() {
		for(Iterator iter = functionContainerBinding.getAnnotations().iterator(); iter.hasNext();) {
			IAnnotationBinding annotationBinding = (IAnnotationBinding) iter.next();
			annotationBinding = annotationBinding.getAnnotation(AnnotationAnnotationTypeBinding.getInstance());
			if(annotationBinding != null) {
				IAnnotationBinding aBinding = (IAnnotationBinding) annotationBinding.findData("implicitFields");
				if(IBinding.NOT_FOUND_BINDING != aBinding) {
					IAnnotationBinding[] implicitFields = (IAnnotationBinding[]) aBinding.getValue();
					for(int i = 0; i < implicitFields.length; i++) {
						IAnnotationBinding implicitField = implicitFields[i];
						IAnnotationBinding fieldNameBinding = (IAnnotationBinding) implicitField.findData("FieldName");
						IAnnotationBinding fieldTypeBinding = (IAnnotationBinding) implicitField.findData("FieldType");
						IAnnotationBinding isPrivateBinding = (IAnnotationBinding) implicitField.findData("IsPrivate");
						if(IBinding.NOT_FOUND_BINDING != fieldNameBinding && IBinding.NOT_FOUND_BINDING != fieldTypeBinding) {
							ClassFieldBinding newField = new ClassFieldBinding(
								InternUtil.internCaseSensitive((String) fieldNameBinding.getValue()),
								functionContainerBinding,
								(ITypeBinding) fieldTypeBinding.getValue()
							);
							newField.setIsPrivate(IBinding.NOT_FOUND_BINDING != isPrivateBinding && Boolean.YES == isPrivateBinding.getValue());
							functionContainerBinding.addClassField(newField);
							newField.addAnnotations(implicitField.getAnnotations());
							newField.setImplicit(true);
							definedDataNames.add(newField);
						}
					}
				}
				
				aBinding = (IAnnotationBinding) annotationBinding.findData("implicitFunctions");
				if(IBinding.NOT_FOUND_BINDING != aBinding) {
					IAnnotationBinding[] implicitFunctions = (IAnnotationBinding[]) aBinding.getValue();
					for(int i = 0; i < implicitFunctions.length; i++) {
						IAnnotationBinding implicitFunction = implicitFunctions[i];
						IAnnotationBinding functionNameBinding = (IAnnotationBinding) implicitFunction.findData("FunctionName");
						IAnnotationBinding isStaticBinding = (IAnnotationBinding) implicitFunction.findData("IsStatic");
						IAnnotationBinding parameterTypesBinding = (IAnnotationBinding) implicitFunction.findData("ParameterTypes");
						IAnnotationBinding returnTypeBinding = (IAnnotationBinding) implicitFunction.findData("ReturnType");
						IAnnotationBinding isPrivateBinding = (IAnnotationBinding) implicitFunction.findData("IsPrivate");
						IAnnotationBinding modifiersBinding = (IAnnotationBinding) implicitFunction.findData("modifiers");
						
						if(IBinding.NOT_FOUND_BINDING != functionNameBinding) {
							FunctionBinding newFunction = new FunctionBinding(
								InternUtil.internCaseSensitive((String) functionNameBinding.getValue()),
								functionContainerBinding
							);
							newFunction.setImplicit(true);
							if(IBinding.NOT_FOUND_BINDING != isStaticBinding && Boolean.YES == isStaticBinding.getValue()) {
								newFunction.setStatic(true);
							}
							if(IBinding.NOT_FOUND_BINDING != isPrivateBinding && Boolean.YES == isPrivateBinding.getValue()) {
								newFunction.setPrivate(true);
							}
							
							if(IBinding.NOT_FOUND_BINDING != parameterTypesBinding) {
								Object[] parameterTypes = (Object[]) parameterTypesBinding.getValue();
								Object[] modifiers = null;
								if(IBinding.NOT_FOUND_BINDING != modifiersBinding) {
									modifiers =  (Object[])modifiersBinding.getValue();
								}
								for(int j = 0; j < parameterTypes.length; j++) {
									ITypeBinding typeBinding = (ITypeBinding) parameterTypes[j];
									FunctionParameterBinding functionParameterBinding = new FunctionParameterBinding(InternUtil.internCaseSensitive("arg" + Integer.toString(j+1)), functionContainerBinding, typeBinding, newFunction);
									
									if (modifiers != null && j < modifiers.length) {
										if (modifiers[j].toString().equalsIgnoreCase(ParameterModifierKind.INMODIFIER.getName())) {
											functionParameterBinding.setInput(true);
										}
										else {
											if (modifiers[j].toString().equalsIgnoreCase(ParameterModifierKind.OUTMODIFIER.getName())) {
												functionParameterBinding.setOutput(true);
											}
										}
									}
									newFunction.addParameter(functionParameterBinding);
								}
							}
							if(IBinding.NOT_FOUND_BINDING != returnTypeBinding) {
								newFunction.setReturnType((ITypeBinding) returnTypeBinding.getValue());
							}
							functionContainerBinding.addDeclaredFunction(new NestedFunctionBinding(newFunction.getCaseSensitiveName(), functionContainerBinding, newFunction));
							definedFunctionNames.add(newFunction.getName());
						}
					}
				}
			}
		}		
	}
    
    private void convertItemsToNullableIfNeccesary() {
		IAnnotationBinding aBinding = functionContainerBinding.getAnnotation(new String[] {"egl", "core"}, "I4GLItemsNullable");
		if(aBinding != null && Boolean.YES == aBinding.getValue()) {
			for(Iterator iter = functionContainerBinding.getClassFields().iterator(); iter.hasNext();) {
				DataBinding next = (DataBinding) iter.next();
				ITypeBinding type = next.getType();
				if(type != null && ITypeBinding.PRIMITIVE_TYPE_BINDING == type.getBaseType().getKind()) {
					next.setType(type.getNullableInstance());
				}
			}
			for(Iterator iter = functionContainerBinding.getDeclaredFunctions().iterator(); iter.hasNext();) {
				NestedFunctionBinding next = (NestedFunctionBinding) iter.next();
				FunctionBinding nextFunc = (FunctionBinding) next.getType();
				ITypeBinding returnType = nextFunc.getReturnType();
				if(returnType != null && ITypeBinding.PRIMITIVE_TYPE_BINDING == returnType.getBaseType().getKind()) {
					nextFunc.setReturnType(returnType.getNullableInstance());
				}
				for(Iterator pIter = nextFunc.getParameters().iterator(); pIter.hasNext();) {
					DataBinding dBinding = (DataBinding) pIter.next();
					ITypeBinding type = dBinding.getType();
					if(type != null && ITypeBinding.PRIMITIVE_TYPE_BINDING == type.getBaseType().getKind()) {
						dBinding.setType(type.getNullableInstance());
					}
				}
			}
		}
	}
}
