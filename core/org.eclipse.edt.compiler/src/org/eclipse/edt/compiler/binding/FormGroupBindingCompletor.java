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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.edt.compiler.core.ast.FormGroup;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedForm;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.AnnotationLeftHandScope;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.ResolutionException;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;


/**
 * @author winghong
 */
public class FormGroupBindingCompletor extends AbstractBinder {

    private FormGroupBinding formGroupBinding;
    private IProblemRequestor problemRequestor;
    
    private Set definedFormNames = new HashSet();

    public FormGroupBindingCompletor(Scope currentScope, FormGroupBinding formGroupBinding, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(currentScope, formGroupBinding, dependencyRequestor, compilerOptions);
        this.formGroupBinding = formGroupBinding;
        this.problemRequestor = problemRequestor;
    }
    
    public boolean visit(FormGroup formGroup) {
    	formGroup.getName().setBinding(formGroupBinding);
    	
    	formGroupBinding.setPrivate(formGroup.isPrivate());
    	
        return true;
    }
    
	public void endVisit(FormGroup formGroup) {
		formGroupBinding.setValid(true);
	}
    
    public boolean visit(SettingsBlock settingsBlock) {
        AnnotationLeftHandScope lhScope = new AnnotationLeftHandScope(currentScope, formGroupBinding, formGroupBinding, formGroupBinding, -1, formGroupBinding);
        SettingsBlockAnnotationBindingsCompletor completor = new SettingsBlockAnnotationBindingsCompletor(currentScope, formGroupBinding, lhScope, dependencyRequestor, problemRequestor, compilerOptions);
        settingsBlock.accept(completor);
        return false;
    }
    
    public boolean visit(NestedForm form) {
    	String formName = form.getName().getIdentifier(); 
    	FormBinding formBinding = new FormBinding(formGroupBinding.getPackageName(), form.getName().getCaseSensitiveIdentifier(), formGroupBinding);
    	formBinding.setEnvironment(formGroupBinding.getEnvironment());

    	FormBindingCompletor formBinder = new FormBindingCompletor(currentScope, formBinding, dependencyRequestor, problemRequestor, compilerOptions);
        form.accept(formBinder);
        
        if(definedFormNames.contains(formName)) {
        	problemRequestor.acceptProblem(
        		form.getName(),
				IProblemRequestor.INVALID_FORMGROUP_MULTIPLE_DECLARATION,
				new String[] {formName, formGroupBinding.getCaseSensitiveName()});
        }
        else {        
        	formGroupBinding.addNestedForm(formBinding);
        	definedFormNames.add(formName);
        }
        
        formBinding.setEnvironment(formGroupBinding.getEnvironment());
        
        return false;
    }
    
	public boolean visit(UseStatement useStatement) {	    
        for( Iterator iter = useStatement.getNames().iterator(); iter.hasNext(); ) {
        	Name nextName = (Name) iter.next();
        	String nextFormName = nextName.getIdentifier();
			ITypeBinding typeBinding = null;
	        try {
	            typeBinding = bindTypeName(nextName);
	        } catch (ResolutionException e) {
	        	problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
	            continue; // Do not create the form binding if its type cannot be resolved
	        }
	        
	        if(typeBinding.getKind() == ITypeBinding.FORM_BINDING) {
	        	if(definedFormNames.contains(nextFormName)) {
	            	problemRequestor.acceptProblem(
	            		nextName,
	    				IProblemRequestor.INVALID_FORMGROUP_MULTIPLE_DECLARATION,
	    				new String[] {nextName.getCanonicalName(), formGroupBinding.getCaseSensitiveName()});
	            }
	            else {        
	            	formGroupBinding.addUsedForm(typeBinding);
	            	definedFormNames.add(nextFormName);
	            }
	        }
	        else {
	        	problemRequestor.acceptProblem(
	        		nextName,
					IProblemRequestor.INVALID_FORMGROUP_USEDECLARATION_VALUE,
					new String[] {nextName.getCanonicalName(), formGroupBinding.getCaseSensitiveName()});
	        }
        }
		return false;
	}
}
