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
package org.eclipse.edt.compiler.internal.core.lookup;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.edt.compiler.binding.FixedRecordBinding;
import org.eclipse.edt.compiler.binding.FormBinding;
import org.eclipse.edt.compiler.binding.FormFieldBinding;
import org.eclipse.edt.compiler.binding.FormGroupBinding;
import org.eclipse.edt.compiler.binding.FunctionContainerBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.NestedFunctionBinding;
import org.eclipse.edt.compiler.binding.OverloadedFunctionSet;
import org.eclipse.edt.compiler.binding.SettingsBlockAnnotationBindingsCompletor;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.binding.UsedTypeBinding;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.utils.ExpressionParser;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author winghong
 */

public abstract class FunctionContainerBinder extends DefaultBinder {

    private FunctionContainerBinding functionContainerBinding;

    public FunctionContainerBinder(FunctionContainerBinding functionContainerBinding, Scope scope,
            IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(scope, functionContainerBinding, dependencyRequestor, problemRequestor, compilerOptions);
        this.functionContainerBinding = functionContainerBinding;
    }

    /**
     * Subclasses should invoke this message after the currentScope field is set
     * to an instance of FunctionContainerScope, but before returning from the
     * visit() method for the function container AST.
     */
    protected void preprocessPart(Part part) {

        dependencyRequestor.recordFunctionContainerScope((FunctionContainerScope) currentScope);
        
        final Set usedForms = new HashSet();
        for (Iterator iter = part.getContents().iterator(); iter.hasNext();) {
            ((Node) iter.next()).accept(new DefaultASTVisitor() {
                //Bind the use statements of the function container first, as
                // their
                //targets affect resolution within functions.
                public boolean visit(UseStatement useStatement) {
                    for (Iterator iter = useStatement.getNames().iterator(); iter.hasNext();) {
                        Name nextName = (Name) iter.next();
                        ITypeBinding typeBinding = (ITypeBinding) nextName.resolveBinding();
                        if (typeBinding == IBinding.NOT_FOUND_BINDING || typeBinding == null) {
                            continue;
                        }
                        UsedTypeBinding usedType = useStatement.getUsedTypeBinding();
                        if (useStatement.getUsedTypeBinding() == null) {
                            usedType = new UsedTypeBinding(typeBinding);
                            useStatement.setUsedTypeBinding(usedType);
                            if (useStatement.hasSettingsBlock()) {
                                AnnotationLeftHandScope scope = new AnnotationLeftHandScope(currentScope, usedType, null, usedType,
                                        -1, functionContainerBinding);
                                SettingsBlockAnnotationBindingsCompletor blockCompletor = new SettingsBlockAnnotationBindingsCompletor(
                                        currentScope, functionContainerBinding, scope, dependencyRequestor, problemRequestor, compilerOptions);
                                useStatement.getSettingsBlock().accept(blockCompletor);
                            }
                        }
                        
                        if (typeBinding.getKind() == ITypeBinding.LIBRARY_BINDING) {
                            ((FunctionContainerScope) currentScope).addUsedLibrary(typeBinding);
                        } else if (typeBinding.getKind() == ITypeBinding.DATATABLE_BINDING) {
                            ((FunctionContainerScope) currentScope).addUsedDataTable(typeBinding);
                        } else if (typeBinding.getKind() == ITypeBinding.ENUMERATION_BINDING) {
                        	((FunctionContainerScope) currentScope).addUsedEnumeration(typeBinding);
                        } else if (typeBinding.getKind() == ITypeBinding.FORMGROUP_BINDING) {
                            if (!usedType.isHelpGroup()) {                                
                                FormGroupBinding formGroupBinding = (FormGroupBinding) typeBinding;
                                usedForms.addAll(formGroupBinding.getForms());
                                ((FunctionContainerScope) currentScope).setMainFormGroup(usedForms);
                                
                                for(Iterator usedFormsIter = formGroupBinding.getForms().iterator(); usedFormsIter.hasNext();) {
                                	FormBinding nextForm = (FormBinding) usedFormsIter.next();
                                	for(Iterator fieldsIter = nextForm.getFields().iterator(); fieldsIter.hasNext();) {
                                		FormFieldBinding nextField = (FormFieldBinding) fieldsIter.next();
                                		IAnnotationBinding aBinding = nextField.getAnnotation(new String[] {"egl", "ui"}, "ValidatorFunction");
                                		if(aBinding != null && aBinding.getValue() != null) {
                                			if (aBinding.getValue() instanceof String) {
                                				resolveValidatorFunctionRef(nextName, (String) aBinding.getValue());
                                			}
                                			else {
                                    			IFunctionBinding valFunctionBinding = (IFunctionBinding) aBinding.getValue();
                                    			if(valFunctionBinding != IBinding.NOT_FOUND_BINDING && !valFunctionBinding.isSystemFunction()) {
                                    				dependencyRequestor.recordTopLevelFunctionBinding(valFunctionBinding);                                				
                                    			}
                                			}
                                		}
                                	}
                                }
                            }
                        } else if (typeBinding.getKind() == ITypeBinding.FORM_BINDING) {
                        	usedForms.add(typeBinding);
                        	if(nextName.isQualifiedName()) {
                        		IBinding qualifierBinding = ((QualifiedName) nextName).getQualifier().resolveBinding();
                        		if(qualifierBinding.isTypeBinding() && ITypeBinding.FORMGROUP_BINDING == ((ITypeBinding) qualifierBinding).getKind()) {
                        			((FunctionContainerScope) currentScope).setMainFormGroup(usedForms);	                        			
                        		}
                        	}
                        	FormBinding nextForm = (FormBinding) typeBinding;
                        	for(Iterator fieldsIter = nextForm.getFields().iterator(); fieldsIter.hasNext();) {
                        		FormFieldBinding nextField = (FormFieldBinding) fieldsIter.next();
                        		IAnnotationBinding aBinding = nextField.getAnnotation(new String[] {"egl", "ui"}, "ValidatorFunction");
                        		if(aBinding != null) {
                        			
                        			if (aBinding.getValue() instanceof String) {
                        				resolveValidatorFunctionRef(nextName, (String) aBinding.getValue());
                        			}
                        			else {
                        				if (aBinding.getValue() instanceof IFunctionBinding) {
                                   			IFunctionBinding valFunctionBinding = (IFunctionBinding) aBinding.getValue();
                                			if(valFunctionBinding != IBinding.NOT_FOUND_BINDING && !valFunctionBinding.isSystemFunction()) {
                                				dependencyRequestor.recordTopLevelFunctionBinding(valFunctionBinding);                                				
                                			}
                        				}
                        			}
                        		}
                        	}
                        }
                    }
                    return false;
                }
            });
        }
    }

	protected void doneVisitingPart() {
        currentScope.startReturningTopLevelFunctions();
        
        if(functionContainerBinding.getAnnotation(new String[] {"egl", "ui", "webtransaction"}, "VGWebTransaction") != null) {        
	        for(Iterator iter = functionContainerBinding.getDeclaredData().iterator(); iter.hasNext();) {
	        	IDataBinding dBinding = (IDataBinding) iter.next();
	        	ITypeBinding tBinding = dBinding.getType().getBaseType();
	        	if(tBinding.getAnnotation(new String[] {"egl", "ui", "webtransaction"}, "VGUIRecord") != null) {
	        		Set validatorFunctions = new HashSet();
	        		
	        		IAnnotationBinding runValidatorAnnotation = (IAnnotationBinding) tBinding.getAnnotation(new String[] {"egl", "ui", "webtransaction"}, "VGUIRecord").findData(InternUtil.intern(IEGLConstants.PROPERTY_RUNVALIDATORFROMPROGRAM));
	        		if(runValidatorAnnotation == IBinding.NOT_FOUND_BINDING || Boolean.YES == runValidatorAnnotation.getValue()) {
	        			IAnnotationBinding validatorFunctionAnnotation = (IAnnotationBinding) tBinding.getAnnotation(new String[] {"egl", "ui", "webtransaction"}, "VGUIRecord").findData(InternUtil.intern(IEGLConstants.PROPERTY_VALIDATORFUNCTION));
	        			if(validatorFunctionAnnotation != IBinding.NOT_FOUND_BINDING) {
	        				IFunctionBinding validatorFunctionBinding = (IFunctionBinding) validatorFunctionAnnotation.getValue();
	        				if(validatorFunctionBinding != IBinding.NOT_FOUND_BINDING) {
	        					validatorFunctions.add(validatorFunctionBinding);
	        				}
	        			}
	        		}
	        		
	        		for(Iterator iter2 = ((FixedRecordBinding) tBinding).getStructureItems().iterator(); iter2.hasNext();) {
	        			addValidatorFunctions(validatorFunctions, (StructureItemBinding) iter2.next());
	        		}
	        		
	        		for(Iterator iter2 = validatorFunctions.iterator(); iter2.hasNext();) {
	    				dependencyRequestor.recordTopLevelFunctionBinding((IFunctionBinding) iter2.next());
	    			}
	        	}
	        }
        }
    }
    
    private void addValidatorFunctions(Set functionBindings, StructureItemBinding sItemBinding) {
    	IAnnotationBinding runValidatorAnnotation = sItemBinding.getAnnotation(new String[] {"egl", "ui"}, "RunValidatorFromProgram");
		if(runValidatorAnnotation == null || Boolean.YES == runValidatorAnnotation.getValue()) {
			IAnnotationBinding validatorFunctionAnnotation = sItemBinding.getAnnotation(new String[] {"egl", "ui"}, "ValidatorFunction");
			if(validatorFunctionAnnotation != null) {
				IFunctionBinding validatorFunctionBinding = (IFunctionBinding) validatorFunctionAnnotation.getValue();
				if(validatorFunctionBinding != IBinding.NOT_FOUND_BINDING) {
					functionBindings.add(validatorFunctionBinding);
				}
			}
		}
		
		for(Iterator iter = sItemBinding.getChildren().iterator(); iter.hasNext();) {
			addValidatorFunctions(functionBindings, (StructureItemBinding) iter.next());
		}
	}

    public boolean visit(ClassDataDeclaration classDataDeclaration) {
        // Because part of the field declaration (i.e. its type) has already
        // been processed, we take over the traversal of FieldDeclaration in
        // here
        if (classDataDeclaration.getSettingsBlockOpt() != null)
            classDataDeclaration.getSettingsBlockOpt().accept(this);
        if (classDataDeclaration.hasInitializer())
            classDataDeclaration.getInitializer().accept(this);
        processResolvableProperties(classDataDeclaration);
        return false;
    }

    private void processResolvableProperties(ClassDataDeclaration classDataDeclaration) {
        Iterator i = classDataDeclaration.getNames().iterator();
        while (i.hasNext()) {
            Name name = (Name) i.next();
            if (name.resolveBinding() == IBinding.NOT_FOUND_BINDING) {
                break;
            }

            if (classDataDeclaration.hasSettingsBlock()) {
                processResolvableProperties(classDataDeclaration.getSettingsBlockOpt(), name.resolveDataBinding(), null);
            }
            processResolvableProperties(name);
        }
    }

    public boolean visit(NestedFunction nestedFunction) {    	
    	IFunctionBinding functionBinding = (IFunctionBinding) ((NestedFunctionBinding) nestedFunction.getName().resolveBinding()).getType();
        if (functionBinding != null) {
            FunctionBinder functionBinder = new FunctionBinder(functionContainerBinding, functionBinding, currentScope,
                    dependencyRequestor, problemRequestor, compilerOptions);
            nestedFunction.accept(functionBinder);
        }
        return false;
    }

    
	private Object resolveValidatorFunctionRef(Node errorNode, String value) {


		Name name = new ExpressionParser(compilerOptions).parseAsName(value);
		if (name == null) {
			return null;
		}
		
		Object result;
		try {
			IDataBinding resultDBinding = bindExpressionName(name, true);
			if (IDataBinding.OVERLOADED_FUNCTION_SET_BINDING == resultDBinding.getKind()) {
				resultDBinding = (IDataBinding) ((OverloadedFunctionSet) resultDBinding).getNestedFunctionBindings().get(0);
			}
			if (resultDBinding.getType() != null && resultDBinding.getType().isFunctionBinding()) {
				if(ITypeBinding.LIBRARY_BINDING == resultDBinding.getDeclaringPart().getKind()) {					
					result = IBinding.NOT_FOUND_BINDING;
					problemRequestor.acceptProblem(errorNode, IProblemRequestor.PROPERTY_MUST_NOT_RESOLVE_TO_LIBRARY_FUNCTION, IMarker.SEVERITY_ERROR, new String[] {IEGLConstants.PROPERTY_VALIDATORFUNCTION, value});
				}
				else {
					result = (IFunctionBinding) resultDBinding.getType();
				}
			} else {
				result = IBinding.NOT_FOUND_BINDING;
				problemRequestor.acceptProblem(errorNode, IProblemRequestor.PROPERTY_MUST_RESOLVE_TO_FUNCTION, IMarker.SEVERITY_ERROR, new String[] {IEGLConstants.PROPERTY_VALIDATORFUNCTION, value});
			}
		} catch (ResolutionException e) {
			if (IProblemRequestor.VARIABLE_NOT_FOUND == e.getProblemKind()) {
				problemRequestor.acceptProblem(errorNode, IProblemRequestor.FUNCTION_REFERENCE_CANNOT_BE_RESOLVED, IMarker.SEVERITY_ERROR, e
						.getInserts());
				result = IBinding.NOT_FOUND_BINDING;
			} else if (IProblemRequestor.VARIABLE_ACCESS_AMBIGUOUS == e.getProblemKind()) {
				problemRequestor
						.acceptProblem(errorNode, IProblemRequestor.FUNCTION_REFERENCE_AMBIGUOUS, IMarker.SEVERITY_ERROR, e.getInserts());
				result = IBinding.NOT_FOUND_BINDING;
			} else {
				problemRequestor.acceptProblem(errorNode, e.getProblemKind(), IMarker.SEVERITY_ERROR, e.getInserts());
				result = IBinding.NOT_FOUND_BINDING;
			}
		}
		return result;
	}
    
}
