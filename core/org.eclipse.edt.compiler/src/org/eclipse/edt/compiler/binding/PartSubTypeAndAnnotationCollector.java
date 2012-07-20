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
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.annotationType.AnnotationTypeBindingImpl;
import org.eclipse.edt.compiler.binding.annotationType.AnnotationTypeManager;
import org.eclipse.edt.compiler.binding.annotationType.EGLNotInCurrentReleaseAnnotationTypeBinding;
import org.eclipse.edt.compiler.core.ast.AnnotationExpression;
import org.eclipse.edt.compiler.core.ast.DataTable;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.EGLClass;
import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedForm;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.TopLevelForm;
import org.eclipse.edt.compiler.core.ast.TopLevelFunction;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ResolutionException;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.mof.egl.utils.InternUtil;


public class PartSubTypeAndAnnotationCollector extends DefaultASTVisitor {

    private IProblemRequestor problemRequestor;

    private List settingsBlocks = new ArrayList();

    private AnnotationBinding subTypeAnnotationBinding;
    
    private IPartBinding partBinding;

    private boolean foundSubTypeInSettingsBlock = false;

	private Scope currentScope;

	private AbstractBinder abstractBinder;

    public PartSubTypeAndAnnotationCollector(IPartBinding partBinding, AbstractBinder binder, Scope currentScope, IProblemRequestor problemRequestor) {
        super();
        this.partBinding = partBinding;
        this.abstractBinder = binder;
        this.problemRequestor = problemRequestor;
        this.currentScope = currentScope;
    }

    public boolean visit(SettingsBlock settingsBlock) {
        settingsBlocks.add(settingsBlock);
        settingsBlock.accept(new DefaultASTVisitor() {
            //Search the settings block for a parttype annotation
            public boolean visit(SetValuesExpression setValuesExpression) {
                return true;
            }
            public boolean visit(AnnotationExpression annotationExpression) {
            	
            	IAnnotationTypeBinding typeBinding = null;
            	
                ITypeBinding resolvedType = null;
                try {
                	resolvedType = abstractBinder.bindTypeName(annotationExpression.getName());
                }
                catch(ResolutionException e) {
                	resolvedType = IBinding.NOT_FOUND_BINDING;        	
                }
                
            	annotationExpression.getName().setBinding(null); //reset the type so that resolution will work later

            	if(IBinding.NOT_FOUND_BINDING != resolvedType) {
                	if(resolvedType.isPartBinding() && !resolvedType.isValid() && resolvedType != partBinding) {
                		resolvedType = ((IPartBinding) resolvedType).realize();
                	}
                	if(hasAnnotation(resolvedType, AnnotationAnnotationTypeBinding.getInstance())) {
                		IAnnotationTypeBinding aTypeBinding = new AnnotationTypeBindingImpl((FlexibleRecordBinding) resolvedType, partBinding);
                		if(aTypeBinding.isPartSubType()) {        			
                			typeBinding = aTypeBinding;       			
                		}
                	}
                }

            	if (typeBinding == null) {
                    typeBinding = AnnotationTypeManager.getAnnotationType(
                            InternUtil.intern(annotationExpression.getName().getCanonicalName()));
            	}          	
            	
                if (typeBinding != null && typeBinding.isPartSubType()) {
                    if (typeBinding.isApplicableFor(partBinding)) {

                        if (subTypeAnnotationBinding == null) {
                            subTypeAnnotationBinding = new AnnotationBinding(typeBinding.getCaseSensitiveName(), partBinding, typeBinding);
                            foundSubTypeInSettingsBlock = true;
                            annotationExpression.getName().setBinding(subTypeAnnotationBinding);
                            annotationExpression.getName().setTypeBinding(typeBinding);
                            annotationExpression.setTypeBinding(typeBinding);
                        } else {
                            problemRequestor.acceptProblem(annotationExpression.getName(), IProblemRequestor.DUPLICATE_PART_SUBTYPE);
                        }
                    }
                    else {
                        problemRequestor.acceptProblem(annotationExpression.getName(), IProblemRequestor.INVALID_PART_SUBTYPE);
                    }
                }

                return false;
            }
            public boolean visit(SettingsBlock settingsBlock) {
                return true;
            }
            
        });
        return false;
    }

    public boolean visit(Record record) {
        if (record.hasSubType()) {
            checkSubType(record.getSubType());
        }
        return true;
    }
    
	public boolean visit(DataTable dataTable) {
		if (dataTable.hasSubType()) {
			checkSubType(dataTable.getSubType());
		}
		return true;
	}

    public boolean visit(Program program) {
        if (program.hasSubType()) {
            checkSubType(program.getSubType());
        }
        return true;
    }

    public boolean visit(Library library) {
        if (library.hasSubType()) {
            checkSubType(library.getSubType());
        }
        return true;
    }

    public boolean visit(Service service) {
    	if (service.hasSubType()) {
            checkSubType(service.getSubType());
        }
        return true;
    }
    
    public boolean visit(Interface intrface) {
        if (intrface.hasSubType()) {
            checkSubType(intrface.getSubType());
        }
        return true;
    }
    
    public boolean visit(ExternalType externalType) {
        if (externalType.hasSubType()) {
            checkSubType(externalType.getSubType());
        }
        return true;
    }
    
    public boolean visit(Handler handler) {
        if (handler.hasSubType()) {
            checkSubType(handler.getSubType());
        }
        return true;
    }

    public boolean visit(EGLClass eglClass) {
        if (eglClass.hasSubType()) {
            checkSubType(eglClass.getSubType());
        }
        return true;
    }

    public boolean visit(NestedForm nestedForm) {
        if (nestedForm.hasSubType()) {
            checkSubType(nestedForm.getSubType());
        }
        return true;
    }
    
    public boolean visit(TopLevelForm topLevelForm) {
        if (topLevelForm.hasSubType()) {
            checkSubType(topLevelForm.getSubType());
        }
        return true;
    }
    
	public boolean visit(TopLevelFunction topLevelFunction) {
		return true;
	}
	
	private void checkSubType(Name name) {

        IPartSubTypeAnnotationTypeBinding subTypeAnnotationTypeBinding = null;        
        IAnnotationTypeBinding typeBinding = null;
        
        ITypeBinding resolvedType = null;
        boolean typeResolved = false;
        try {
        	resolvedType = abstractBinder.bindTypeName(name);
        	typeResolved = true;
        }
        catch(ResolutionException e) {
        	resolvedType = IBinding.NOT_FOUND_BINDING;        	
        }
        
        if(IBinding.NOT_FOUND_BINDING != resolvedType) {
        	if(resolvedType.isPartBinding() && !resolvedType.isValid() && resolvedType != partBinding) {
        		resolvedType = ((IPartBinding) resolvedType).realize();
        	}
        	if(hasAnnotation(resolvedType, AnnotationAnnotationTypeBinding.getInstance())) {
        		IAnnotationTypeBinding aTypeBinding = new AnnotationTypeBindingImpl((FlexibleRecordBinding) resolvedType, partBinding);
        		if(aTypeBinding.isPartSubType()) {        			
        			typeBinding = aTypeBinding;       			
        		}
        	}
        }
        	
        if(typeBinding == null) {
        	typeBinding = AnnotationTypeManager.getAnnotationType(name);
        }
        
        if (typeBinding != null && typeBinding.isPartSubType()) {
            subTypeAnnotationTypeBinding = (IPartSubTypeAnnotationTypeBinding) typeBinding;
            resolvedType = subTypeAnnotationTypeBinding;
            typeResolved = true;
        }
        
        if (subTypeAnnotationTypeBinding == null || !subTypeAnnotationTypeBinding.isApplicableFor(partBinding)) {
        	if(typeResolved) {
	            problemRequestor.acceptProblem(
	            	name,
	            	IProblemRequestor.INVALID_PART_SUBTYPE,
	            	new String[] {resolvedType.getPackageQualifiedName(), partBinding.getCaseSensitiveName()});
        	}
        	else {
        		problemRequestor.acceptProblem(
	            	name,
	            	IProblemRequestor.TYPE_CANNOT_BE_RESOLVED,
	            	new String[] {name.getCanonicalName()});
        	}
            name.setBinding(IBinding.NOT_FOUND_BINDING);
        }
        else {
            subTypeAnnotationBinding = new AnnotationBinding(subTypeAnnotationTypeBinding.getCaseSensitiveName(), partBinding, subTypeAnnotationTypeBinding);
            partBinding.addAnnotation(subTypeAnnotationBinding);
            name.setBinding(subTypeAnnotationBinding);
        }

    }

    private boolean hasAnnotation(ITypeBinding resolvedType, IAnnotationTypeBinding annotationType) {
		for(Iterator iter = resolvedType.getAnnotations().iterator(); iter.hasNext();) {
			if(((IAnnotationBinding) iter.next()).getType() == annotationType) {
				return true;
			}
		}
		return false;
	}

	public AnnotationBinding getSubTypeAnnotationBinding() {
        return subTypeAnnotationBinding;
    }

    /**
     * @return Returns the foundSubTypeInSettingsBlock.
     */
    public boolean isFoundSubTypeInSettingsBlock() {
        return foundSubTypeInSettingsBlock;
    }

    /**
     * @return Returns the settingsBlocks.
     */
    public List getSettingsBlocks() {
        return settingsBlocks;
    }
}
