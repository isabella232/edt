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
import java.util.Map;
import java.util.Set;

import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.IASTVisitor;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.AnnotationLeftHandScope;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.MemberScope;
import org.eclipse.edt.compiler.internal.core.lookup.RecordScope;
import org.eclipse.edt.compiler.internal.core.lookup.ResolutionException;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.Type;


public class RecordBindingFieldsCompletor extends AbstractBinder {

    private org.eclipse.edt.mof.egl.Record recordBinding;
    private String canonicalRecordName;

    private IProblemRequestor problemRequestor;
    
    private Set<String> definedNames = new HashSet<String>();
    
    private Map<String, Name> fieldNamesToNodes = new HashMap<String, Name>();

    public RecordBindingFieldsCompletor(Scope currentScope, org.eclipse.edt.mof.egl.Record recordBinding,
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
    				Field field = null;
    				if (structureItem.isFiller()) {
    					field =  (Field)structureItem.resolveMember();
    				}
    				else {
    					if (structureItem.getName() != null) {
    						field = (Field)structureItem.getName().resolveMember();
    					}
    				}
    				
    				if (field != null) {
    					final Field fld = field;
     					IASTVisitor sbVisitor = new DefaultASTVisitor() {
    				        public boolean visit(SettingsBlock settingsBlock) {
    				        	Scope fieldScope = new MemberScope(currentScope, fld);
    				            AnnotationLeftHandScope annotationScope = new AnnotationLeftHandScope(fieldScope, fld, fld.getType(), fld);
    				            settingsBlock.accept(new SettingsBlockAnnotationBindingsCompletor(new RecordScope(currentScope, recordBinding), recordBinding, annotationScope, dependencyRequestor,
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
        Type type = null;
        try {
            if (structureItem.hasType()) {
                type = bindType(structureItem.getType());
            }
        } catch (ResolutionException e) {
            problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
            if(structureItem.hasSettingsBlock()) {
            	setBindAttemptedForNames(structureItem.getSettingsBlock());
            }
            return false; // Do not create the field binding if its type cannot be resolved
        }
        
        if (structureItem.isFiller()) {
            Field fieldBinding = createField(structureItem, type);
            recordBinding.getFields().add(fieldBinding);
            structureItem.setMember(fieldBinding);
            return false;
        }

        if (structureItem.isEmbedded()) {
        	//TODO handle embedded records if we support them
        }
        
        Field fieldBinding = createField(structureItem, type);
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
    		recordBinding.getFields().add(fieldBinding);
    	}
        
        return false;
    }

    public Field createField(StructureItem structureItem, Type type) {

        String fieldName = structureItem.isFiller() ? "*" : structureItem.getName().getCaseSensitiveIdentifier();

        Field field = IrFactory.INSTANCE.createField();
        field.setName(fieldName);
        field.setContainer(recordBinding);
        field.setType(type);
        field.setIsNullable(structureItem.isNullable());
        
        if (!structureItem.isFiller()) {
            structureItem.getName().setMember(field);
            structureItem.getName().setType(type);
            fieldNamesToNodes.put(fieldName, structureItem.getName());
        }
        return field;
    }
    
    
    public Node getNode(String fieldName) {
    	return (Node) fieldNamesToNodes.get(fieldName);
    }
}
