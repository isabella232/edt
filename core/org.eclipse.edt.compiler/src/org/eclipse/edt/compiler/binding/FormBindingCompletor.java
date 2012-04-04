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

import java.util.Iterator;

import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedForm;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.TopLevelForm;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.AnnotationLeftHandScope;
import org.eclipse.edt.compiler.internal.core.lookup.FormScope;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;


/**
 * @author winghong
 */
public class FormBindingCompletor extends AbstractBinder {

    private FormBinding formBinding;
    private IAnnotationBinding partSubTypeAnnotationBinding;
    private IProblemRequestor problemRequestor;

    public FormBindingCompletor(Scope currentScope, FormBinding formBinding, IDependencyRequestor dependencyRequestor,
            IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(currentScope, formBinding, dependencyRequestor, compilerOptions);
        this.formBinding = formBinding;
        this.problemRequestor = problemRequestor;
    }

    public boolean visit(TopLevelForm form) {

        //First, we need to look for form subType on the AST or in the
        // settings block
        PartSubTypeAndAnnotationCollector collector = new PartSubTypeAndAnnotationCollector(formBinding, this, currentScope, problemRequestor);
        form.accept(collector);
        processSubType(collector, form.getName());

        //Next, we need to complete the fields in the record
        form.accept(new FormBindingFieldsCompletor(currentScope, formBinding, dependencyRequestor, problemRequestor, compilerOptions));

        //now we will need to process the SettingsBlock for the
        // form...the collector has already gathered those for us.

        processSettingsBlocksFromCollector(collector);
        setDefaultAnnotationsOnFields();
        
        formBinding.setPrivate(form.isPrivate());

        form.getName().setBinding(formBinding);

        return false;
    }
    
	public void endVisit(TopLevelForm topLevelForm) {
		formBinding.setValid(true);
	}
    
    private void setDefaultAnnotationsOnFields() {
        Iterator i = formBinding.getFields().iterator();
        while (i.hasNext()) {
            FormFieldBinding field =  (FormFieldBinding) i.next();
            if (field.isVariable()) {
                setDefaultAnntoations((VariableFormFieldBinding) field);
            }
            else {
                setDefaultAnntoations((ConstantFormFieldBinding) field);
            }
        }        
    }
    
    private void setDefaultAnntoations(VariableFormFieldBinding field) {
        new FieldLenUtility(field, compilerOptions, currentScope).checkFieldLen();
        new PositionUtility(field, field.getOccurs(), compilerOptions, currentScope).checkPosition();
    }
    
    private void setDefaultAnntoations(ConstantFormFieldBinding field) {
        new PositionUtility(field, 0, compilerOptions, currentScope).checkPosition();
    }
    
    private void processSettingsBlocksFromCollector(PartSubTypeAndAnnotationCollector collector) {

        if (collector.getSettingsBlocks().size() > 0) {
            AnnotationLeftHandScope scope = new AnnotationLeftHandScope(new FormScope(currentScope, formBinding), formBinding, formBinding, formBinding, -1, formBinding);
            if (!collector.isFoundSubTypeInSettingsBlock() && partSubTypeAnnotationBinding != null) {
                scope = new AnnotationLeftHandScope(scope, partSubTypeAnnotationBinding, partSubTypeAnnotationBinding.getType(), partSubTypeAnnotationBinding, -1, formBinding);
            }
            SettingsBlockAnnotationBindingsCompletor blockCompletor = new SettingsBlockAnnotationBindingsCompletor(currentScope, formBinding, scope, dependencyRequestor, problemRequestor, compilerOptions);
            Iterator i = collector.getSettingsBlocks().iterator();
            while (i.hasNext()) {
                SettingsBlock block = (SettingsBlock)i.next();
                block.accept(blockCompletor);
            }
        }
    }

    public boolean visit(NestedForm form) {
        //First, we need to look for form subType on the AST or in the
        // settings block
        PartSubTypeAndAnnotationCollector collector = new PartSubTypeAndAnnotationCollector(formBinding, this, currentScope, problemRequestor);
        form.accept(collector);
        processSubType(collector, form.getName());
        //Next, we need to complete the fields in the record
        form.accept(new FormBindingFieldsCompletor(currentScope, formBinding, dependencyRequestor,
                problemRequestor, compilerOptions));

        //now we will need to process the SettingsBlock for the
        // form...the collector has already gathered those for us.

        processSettingsBlocksFromCollector(collector);
        setDefaultAnnotationsOnFields();

        form.getName().setBinding(formBinding);
        
        formBinding.setPrivate(form.isPrivate());

        return false;
    }
    
	public void endVisit(NestedForm nestedForm) {
		formBinding.setValid(true);
	}

    protected void processSubType(PartSubTypeAndAnnotationCollector collector, Name formName) {
        if (collector.getSubTypeAnnotationBinding() == null) {
            problemRequestor.acceptProblem(formName, IProblemRequestor.INVALID_FORM_TYPE_DEFINATION, new String[] { formName.getCanonicalName() });
        }
        
        if (collector.isFoundSubTypeInSettingsBlock()) {
            partSubTypeAnnotationBinding = collector.getSubTypeAnnotationBinding();
        }
    }
}
