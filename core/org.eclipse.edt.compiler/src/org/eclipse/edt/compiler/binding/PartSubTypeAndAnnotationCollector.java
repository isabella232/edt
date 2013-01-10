/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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
import java.util.List;

import org.eclipse.edt.compiler.core.ast.AnnotationExpression;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Class;
import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ResolutionException;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Stereotype;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.egl.Type;


public class PartSubTypeAndAnnotationCollector extends DefaultASTVisitor {

    private IProblemRequestor problemRequestor;

    private List<SettingsBlock> settingsBlocks = new ArrayList<SettingsBlock>();

    private Stereotype stereotype;
    
    private Part partBinding;

    private boolean foundSubTypeInSettingsBlock = false;

	private AbstractBinder abstractBinder;

    public PartSubTypeAndAnnotationCollector(Part partBinding, AbstractBinder binder, IProblemRequestor problemRequestor) {
        super();
        this.partBinding = partBinding;
        this.abstractBinder = binder;
        this.problemRequestor = problemRequestor;
    }

    public boolean visit(SettingsBlock settingsBlock) {
        settingsBlocks.add(settingsBlock);
        settingsBlock.accept(new DefaultASTVisitor() {
            //Search the settings block for a parttype annotation
            public boolean visit(SetValuesExpression setValuesExpression) {
                return true;
            }
            public boolean visit(AnnotationExpression annotationExpression) {
            	            	
                Type resolvedType = null;
                try {
                	resolvedType = abstractBinder.bindTypeName(annotationExpression.getName());
                }
                catch(ResolutionException e) {
                	resolvedType = null;        	
                }
                
            	annotationExpression.getName().setElement(null); //reset the binding info so that resolution will work later
            	annotationExpression.getName().setType(null); 
            	annotationExpression.getName().setBindAttempted(false);

            	StereotypeType stereotypeType = null;
                if(resolvedType != null) {
                	AnnotationType annType = BindingUtil.getAnnotationType(resolvedType);
                	if (annType instanceof StereotypeType) {
                		stereotypeType = (StereotypeType)annType;
                	}
                }
            	
                if (stereotypeType != null) {
                    if (BindingUtil.isApplicableFor(stereotypeType, partBinding)) {

                        if (stereotype == null) {
                            stereotype = (Stereotype)stereotypeType.newInstance();
                            foundSubTypeInSettingsBlock = true;
                            annotationExpression.getName().setElement(stereotype);
                            annotationExpression.getName().setType(stereotypeType);
                            annotationExpression.setType(stereotypeType);
                        } else {
                            problemRequestor.acceptProblem(annotationExpression.getName(), IProblemRequestor.DUPLICATE_PART_SUBTYPE);
                        }
                    }
                    else {
                        problemRequestor.acceptProblem(
                        		annotationExpression.getName(),
            	            	IProblemRequestor.INVALID_PART_SUBTYPE,
            	            	new String[] {annotationExpression.getName().getCanonicalName(), partBinding.getCaseSensitiveName()});
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

    public boolean visit(Class eglClass) {
        if (eglClass.hasSubType()) {
            checkSubType(eglClass.getSubType());
        }
        return true;
    }

	private void checkSubType(Name name) {

        StereotypeType stereotypeType = null;        

        Type resolvedType = null;
        boolean typeResolved = false;
        try {
        	resolvedType = abstractBinder.bindTypeName(name);
        	typeResolved = true;
        }
        catch(ResolutionException e) {
        	resolvedType = null;        	
        }
        
        if(resolvedType != null) {
        	AnnotationType annType = BindingUtil.getAnnotationType(resolvedType);
        	if (annType instanceof StereotypeType) {
        		stereotypeType = (StereotypeType)annType;
        	}
        }
        	                
        if (stereotypeType == null || !BindingUtil.isApplicableFor(stereotypeType, partBinding)) {
        	if(typeResolved) {
	            problemRequestor.acceptProblem(
	            	name,
	            	IProblemRequestor.INVALID_PART_SUBTYPE,
	            	new String[] {name.getCanonicalName(), partBinding.getCaseSensitiveName()});
        	}
        	else {
        		problemRequestor.acceptProblem(
	            	name,
	            	IProblemRequestor.TYPE_CANNOT_BE_RESOLVED,
	            	new String[] {name.getCanonicalName()});
        	}
            name.setBindAttempted(true);
        }
        else {
        	stereotype = (Stereotype)stereotypeType.newInstance();
            partBinding.addAnnotation(stereotype);
            name.setType(stereotypeType);
            name.setElement(stereotype);
        }

    }

	public Stereotype getStereotype() {
        return stereotype;
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
    public List<SettingsBlock> getSettingsBlocks() {
        return settingsBlocks;
    }
}
