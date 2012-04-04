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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.IASTVisitor;
import org.eclipse.edt.compiler.core.ast.NameType;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.NullProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.AnnotationLeftHandScope;
import org.eclipse.edt.compiler.internal.core.lookup.DataBindingScope;
import org.eclipse.edt.compiler.internal.core.lookup.DefaultBinder;
import org.eclipse.edt.compiler.internal.core.lookup.FlexibleRecordScope;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.ResolutionException;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author winghong
 */
public class FlexibleRecordBindingFieldsCompletor extends AbstractBinder {

    public class FieldCompletor extends DefaultBinder {

        private FlexibleRecordFieldBinding fieldBinding;

        public FieldCompletor(Scope currentScope, FlexibleRecordFieldBinding fieldBinding, IDependencyRequestor dependencyRequestor,
                IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
            super(currentScope, fieldBinding.getDeclaringPart(), dependencyRequestor, problemRequestor, compilerOptions);
            this.fieldBinding = fieldBinding;
        }

        public boolean visit(StructureItem structureItem) {
            if(structureItem.hasInitializer()) {
             	fieldBinding.setInitialValue(getConstantValue(structureItem.getInitializer(), NullProblemRequestor.getInstance(), true));
            }
            return false;
        }
 
        public void endVisit(StructureItem structureItem) {
        	// don't let defaultbinder run twice
        }
    }

    private FlexibleRecordBinding recordBinding;
    private String canonicalRecordName;

    private IProblemRequestor problemRequestor;
    private boolean visitingFirstItem = true;
    
    private Set definedNames = new HashSet();
    
    private Map fieldNamesToNodes = new HashMap();

    public FlexibleRecordBindingFieldsCompletor(Scope currentScope, FlexibleRecordBinding recordBinding,
    		String canonicalRecordName, IDependencyRequestor dependencyRequestor,
			IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(currentScope, recordBinding, dependencyRequestor, compilerOptions);
        this.recordBinding = recordBinding;
        this.canonicalRecordName = canonicalRecordName;
        this.problemRequestor = problemRequestor;
    }

    public boolean visit(Record record) {
        return true;
    }
    
    @Override
    public void endVisit(Record record) {
    	IASTVisitor visitor = new AbstractASTVisitor() {
    		public boolean visit(StructureItem structureItem) {
    			if (structureItem.hasSettingsBlock()) {
    				IDataBinding fb = null;
    				if (structureItem.isFiller()) {
    					fb =  (IDataBinding)structureItem.resolveBinding();
    				}
    				else {
    					if (structureItem.getName() != null) {
    						fb = structureItem.getName().resolveDataBinding();
    					}
    				}
    				
    				if (Binding.isValidBinding(fb)) {
    					final IDataBinding fieldBinding = fb;
    					IASTVisitor sbVisitor = new DefaultASTVisitor() {
    				        public boolean visit(SettingsBlock settingsBlock) {
    				        	Scope fieldScope = new DataBindingScope(currentScope, fieldBinding);
    				            AnnotationLeftHandScope annotationScope = new AnnotationLeftHandScope(fieldScope, fieldBinding, fieldBinding.getType(),
    				                    fieldBinding, -1, fieldBinding.getDeclaringPart());
    				            settingsBlock.accept(new SettingsBlockAnnotationBindingsCompletor(new FlexibleRecordScope(currentScope, recordBinding), fieldBinding.getDeclaringPart(), annotationScope, dependencyRequestor,
    				                    problemRequestor, compilerOptions));
    				            return false;
    				        }
						};
						structureItem.getSettingsBlock().accept(sbVisitor);
    				}
    				
    			}
    			
    			return false;
    		}
		};
		
		record.accept(visitor);
    }

    public boolean visit(SettingsBlock settingsBlock) {
        return false;
    }

    public boolean visit(StructureItem structureItem) {
        ITypeBinding typeBinding = null;
        try {
            if (structureItem.hasType()) {
                typeBinding = bindType(structureItem.getType());
                if (typeBinding.getBaseType().getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING) {
                    FlexibleRecordBinding flexBinding = (FlexibleRecordBinding) typeBinding.getBaseType();

                    recordBinding.addReferencedRecord(flexBinding);
                    if (loopExists(flexBinding)) {
                        problemRequestor.acceptProblem(structureItem.getType(),
                                IProblemRequestor.RECURSIVE_LOOP_STARTED_WITHIN_FLEXIBLE_RECORD_BY_TYPEDEF, new String[] { typeBinding
                                        .getBaseType().getCaseSensitiveName() });
                        if (structureItem.getName() != null) {
                            structureItem.getName().setBinding(IBinding.NOT_FOUND_BINDING);
                        }
                        structureItem.getType().accept(new DefaultASTVisitor() {
                            public boolean visit(NameType nameType) {
                                nameType.getName().setBinding(IBinding.NOT_FOUND_BINDING);
                                return false;
                            }

                            public boolean visit(ArrayType arrayType) {
                                arrayType.setTypeBinding(null);
                                return false;
                            }
                        });
                        return false;
                    }

                }
            }
        } catch (ResolutionException e) {
            problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
            if(structureItem.hasSettingsBlock()) {
            	bindNamesToNotFound(structureItem.getSettingsBlock());
            }
            return false; // Do not create the field binding if its type cannot be resolved
        }
        
        if(structureItem.hasLevel()) {
        	if(visitingFirstItem) {
	        	/*
	        	 * We would not be building a flexible record binding unless Record::isFlexible()
	        	 * returned true. If we're visiting the first item and it has a level number, then
	        	 * the record subtype must be one that forces the record to be flexible.
	        	 */
	        	problemRequestor.acceptProblem(
	        		structureItem,
					IProblemRequestor.LEVEL_NUMBERS_NOT_ALLOWED_IN_RECORD,
					new String[] {recordBinding.getSubType().getName()});	        	
        	}
        	else {
        		problemRequestor.acceptProblem(
        			structureItem,
					IProblemRequestor.INCONSISTENT_LEVEL_NUMBERING,
					new String[] {getCanonicalName(structureItem)});
        	}
        }
        else {
        	visitingFirstItem = false;
        }
        
        if (structureItem.isFiller()) {
            FlexibleRecordFieldBinding fieldBinding = createField(structureItem, typeBinding);
            recordBinding.addField(fieldBinding);
            structureItem.accept(new FieldCompletor(currentScope, fieldBinding, dependencyRequestor, problemRequestor, compilerOptions));
            structureItem.setBinding(fieldBinding);
            return false;
        }

        if (structureItem.isEmbedded()) {
            if (typeBinding.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING) {
                //TODO What do we do with the set values block association with
                // the embed???

                FlexibleRecordBinding flexBinding = (FlexibleRecordBinding) typeBinding;
                
                if (!flexBinding.isValid()) {
                	flexBinding = (FlexibleRecordBinding)flexBinding.realize();
                }
                
                for (Iterator iter = flexBinding.getDeclaredFields().iterator(); iter.hasNext();) {
                	FlexibleRecordFieldBinding nextBinding = (FlexibleRecordFieldBinding) iter.next();
                	if(definedNames.contains(nextBinding.getName())) {
                		//TODO: report error
                	}
                	else {
                		definedNames.add(nextBinding.getName());
                		recordBinding.addField(nextBinding);
                	}
                }
                return false;
            } else if (typeBinding.getKind() == ITypeBinding.FIXED_RECORD_BINDING) {
                problemRequestor.acceptProblem(structureItem.getType(), IProblemRequestor.FIXED_RECORD_EMBEDDED_IN_FLEXIBLE);
                return false;
            } else {
                problemRequestor.acceptProblem(structureItem.getType(), IProblemRequestor.EMBEDED_ITEM_DOES_NOT_RESOLVE,
                        new String[] { structureItem.getType().getCanonicalName() });
                return false;
            }
        }
        
        if(!structureItem.hasType()) {
        	problemRequestor.acceptProblem(
        		structureItem.getName(),
				IProblemRequestor.FLEXIBLE_RECORD_FIELD_MISSING_TYPE,
				new String[] {structureItem.getName().getCanonicalName()});
        	typeBinding = PrimitiveTypeBinding.getInstance(Primitive.CHAR, 0);
        }

        FlexibleRecordFieldBinding fieldBinding = createField(structureItem, typeBinding);
        if (fieldBinding != null) {
        	if(definedNames.contains(fieldBinding.getName())) {
        		problemRequestor.acceptProblem(
        			structureItem.getName(),
					IProblemRequestor.DUPLICATE_VARIABLE_NAME,
					new String[] {
        				structureItem.getName().getCanonicalName(),
						canonicalRecordName
        			});
        	}
        	else {
        		definedNames.add(fieldBinding.getName());
        		recordBinding.addField(fieldBinding);
        	}
            
            structureItem.accept(new FieldCompletor(currentScope, fieldBinding, dependencyRequestor, problemRequestor, compilerOptions));
        }
        
        return false;
    }

    public boolean loopExists(FlexibleRecordBinding recType) {

        if (recType.containsReferenceTo(recordBinding)) {
            return true;
        }
        if (!recType.isValid) {
            recType = (FlexibleRecordBinding)recType.realize();
            if (recType.containsReferenceTo(recordBinding)) {
                return true;
            }
        }
        return false;
    }

    public FlexibleRecordFieldBinding createField(StructureItem structureItem, ITypeBinding typeBinding) {

        String fieldName = structureItem.isFiller() ? InternUtil.internCaseSensitive("*") : structureItem.getName().getCaseSensitiveIdentifier();

        FlexibleRecordFieldBinding field = new FlexibleRecordFieldBinding(fieldName, recordBinding, typeBinding);
        if (!structureItem.isFiller()) {
            structureItem.getName().setBinding(field);
            fieldNamesToNodes.put(fieldName, structureItem.getName());
        }
        return field;
    }
    
    private String getCanonicalName(StructureItem sItem) {
    	if(sItem.isEmbedded()) {
    		return sItem.getType().getCanonicalName();
    	}
    	else if(sItem.isFiller()) {
    		return "*";
    	}
    	else {
    		return sItem.getName().getCanonicalName();
    	}
    }
    
    public Node getNode(String fieldName) {
    	return (Node) fieldNamesToNodes.get(fieldName);
    }
}
