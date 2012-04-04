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

import org.eclipse.edt.compiler.core.ast.DataItem;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.AnnotationLeftHandScope;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.ResolutionException;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValidator;


/**
 * @author winghong
 */
public class DataItemBindingCompletor extends AbstractBinder {

    private DataItemBinding dataItemBinding;
    private IProblemRequestor problemRequestor;

    public DataItemBindingCompletor(Scope currentScope, DataItemBinding dataItemBinding, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(currentScope, dataItemBinding, dependencyRequestor, compilerOptions);
        this.dataItemBinding = dataItemBinding;
        this.problemRequestor = problemRequestor;
    }
    
    public boolean visit(DataItem dataItem) {
    	dataItem.getName().setBinding(dataItemBinding);
    	dataItemBinding.setPrivate(dataItem.isPrivate());
    	
    	ITypeBinding typeBinding = null;
        try {
            typeBinding = bindType(dataItem.getType());
        } catch (ResolutionException e) {
            problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
            return false;   // Do not create the field binding if its type cannot be resolved
        }
        
        if(typeBinding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING) {        	
        	if(typeBinding == PrimitiveTypeBinding.getInstance(Primitive.ANY)) {
	        	problemRequestor.acceptProblem(dataItem.getType(), IProblemRequestor.DATA_ITEM_TYPE_NOT_PRIMITIVE);	
	        }
	        else {
	        	dataItemBinding.setPrimitiveTypeBinding((PrimitiveTypeBinding) typeBinding);
	        }        	
        }
        else {
        	problemRequestor.acceptProblem(dataItem.getType(), IProblemRequestor.DATA_ITEM_TYPE_NOT_PRIMITIVE);
        }
        
        return true;
    }
    
	public void endVisit(DataItem dataItem) {
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(dataItem);
		
		dataItemBinding.setValid(true);
	}
    
    public boolean visit(SettingsBlock settingsBlock) {
        AnnotationLeftHandScope scope = new AnnotationLeftHandScope(currentScope, dataItemBinding, dataItemBinding, dataItemBinding, -1, dataItemBinding);
        SettingsBlockAnnotationBindingsCompletor blockCompletor = new SettingsBlockAnnotationBindingsCompletor(currentScope, dataItemBinding, scope, dependencyRequestor, problemRequestor, compilerOptions);
        settingsBlock.accept(blockCompletor);
        return false;
    }
}
