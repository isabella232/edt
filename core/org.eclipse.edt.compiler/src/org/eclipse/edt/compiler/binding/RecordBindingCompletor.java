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

import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AnnotationLeftHandScope;
import org.eclipse.edt.compiler.internal.core.lookup.DefaultBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.RecordScope;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.Stereotype;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.utils.NameUtile;


public class RecordBindingCompletor extends DefaultBinder {

    private org.eclipse.edt.mof.egl.Record recordBinding;
	private IRPartBinding irBinding;
    
    private Stereotype partStereotype;

	private RecordBindingFieldsCompletor flexibleRecordBindingFieldsCompletor;

    public RecordBindingCompletor(Scope currentScope, IRPartBinding irBinding, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(currentScope, (org.eclipse.edt.mof.egl.Record)irBinding.getIrPart(), dependencyRequestor, problemRequestor, compilerOptions);
        this.recordBinding = (org.eclipse.edt.mof.egl.Record)irBinding.getIrPart();
        this.irBinding = irBinding;
    }

    public boolean visit(Record record) {
        //First, we need to look for record subType on the AST or in the
        // settings block
        PartSubTypeAndAnnotationCollector collector = new PartSubTypeAndAnnotationCollector(recordBinding, this, problemRequestor);
        record.accept(collector);
        processSubType(collector);
        flexibleRecordBindingFieldsCompletor = new RecordBindingFieldsCompletor(currentScope, recordBinding, record.getName().getCanonicalName(), dependencyRequestor, problemRequestor, compilerOptions);
		//Next, we need to complete the fields in the record
        record.accept(flexibleRecordBindingFieldsCompletor);
        
        //now we will need to process the SettingsBlock for the
        // record...the collector has already gathered those for us.

        //TODO blows up right now
//        processSettingsBlocksFromCollector(collector);
        
        if (record.isPrivate()) {
        	recordBinding.setAccessKind(AccessKind.ACC_PRIVATE);
        }
        record.getName().setType(recordBinding);
        
        return false;
    }

	public void endVisit(Record record) {
    	irBinding.setValid(true);
    	setDefaultSuperType();

        currentScope = new RecordScope(currentScope, recordBinding);

        currentScope = currentScope.getParentScope();
        super.endVisit(record);
    }
	
	private void setDefaultSuperType() {
		StructPart anyRec = (StructPart)BindingUtil.findPart(NameUtile.getAsName(MofConversion.EGLX_lang_package), NameUtile.getAsName("AnyRecord"));
		BindingUtil.setDefaultSupertype(recordBinding, anyRec);
	}
    

    protected void processSubType(PartSubTypeAndAnnotationCollector collector) {
    	partStereotype = collector.getStereotype();
    }

    private void processSettingsBlocksFromCollector(PartSubTypeAndAnnotationCollector collector) {

        if (collector.getSettingsBlocks().size() > 0) {
            AnnotationLeftHandScope scope = new AnnotationLeftHandScope(new RecordScope(currentScope, recordBinding), recordBinding, recordBinding, recordBinding);
            if (!collector.isFoundSubTypeInSettingsBlock() && partStereotype != null) {
                scope = new AnnotationLeftHandScope(scope, partStereotype, (StereotypeType)partStereotype.getEClass(), partStereotype);
            }
            SettingsBlockAnnotationBindingsCompletor blockCompletor = new SettingsBlockAnnotationBindingsCompletor(currentScope, recordBinding, scope,
                    dependencyRequestor, problemRequestor, compilerOptions);
            
            for (SettingsBlock block : collector.getSettingsBlocks()) {
            	block.accept(blockCompletor);
            }
        }
    }

    public boolean visit(StructureItem structureItem) {
        return false;
    }

    public boolean visit(SettingsBlock settingsBlock) {
        return false;
    }

}
