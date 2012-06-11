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

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.FixedRecordBindingCompletor;
import org.eclipse.edt.compiler.binding.FunctionContainerBinding;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.NullProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.utils.ExpressionParser;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author winghong
 */

public class FixedRecordBinder extends DefaultBinder {

    private FixedRecordBinding recordBinding;
    private Scope scope;

    public FixedRecordBinder(FixedRecordBinding recordBinding, Scope scope, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(scope, recordBinding, dependencyRequestor, problemRequestor, compilerOptions);
        this.recordBinding = recordBinding;
        this.scope = scope;
    }

    public boolean visit(Record record) {
        // First we have to complete the record binding (as a side effect some of the AST nodes are bound)
        record.accept(new FixedRecordBindingCompletor(scope, recordBinding, dependencyRequestor, problemRequestor, compilerOptions));

        // The current scope only changes once the initial record binding is complete
        currentScope = new FixedStructureScope(currentScope, recordBinding);

        // We will bind the rest of the record now
        return true;
    }
    
	public void endVisit(Record record) {
		if(annotationIs(recordBinding.getSubType(), new String[] {"egl", "ui", "webTransaction"}, "VGUIRecord")) {
			dependencyRequestor.recordFunctionContainerScope(new FunctionContainerScope(scope, new UIRecordFunctionContainerBinding(recordBinding, compilerOptions, currentScope)));
			
			Set validatorFunctions = new HashSet();
			IAnnotationBinding aBinding = (IAnnotationBinding) recordBinding.getAnnotation(new String[] {"egl", "ui", "webTransaction"}, "VGUIRecord").findData(InternUtil.intern(IEGLConstants.PROPERTY_RUNVALIDATORFROMPROGRAM));
			if(aBinding != IBinding.NOT_FOUND_BINDING && Boolean.NO == aBinding.getValue()) {
				aBinding = (IAnnotationBinding) recordBinding.getAnnotation(new String[] {"egl", "ui", "webTransaction"}, "VGUIRecord").findData(InternUtil.intern(IEGLConstants.PROPERTY_VALIDATORFUNCTION));
				if(aBinding != IBinding.NOT_FOUND_BINDING && aBinding.getValue() != null && IBinding.NOT_FOUND_BINDING != aBinding.getValue()) {				
					validatorFunctions.add(aBinding.getValue());
				}
			}
			for(Iterator iter = recordBinding.getStructureItems().iterator(); iter.hasNext();) {
				addValidatorFunctions(validatorFunctions, (StructureItemBinding) iter.next());
			}			
			for(Iterator iter = validatorFunctions.iterator(); iter.hasNext();) {
				IFunctionBinding nextFunction = (IFunctionBinding) iter.next();
				if(!nextFunction.isSystemFunction()) {
					dependencyRequestor.recordTopLevelFunctionBinding(nextFunction);
				}
			}
		}
	}
	
	private void addValidatorFunctions(Set functionBindings, StructureItemBinding sItemBinding) {
		IAnnotationBinding aBinding = sItemBinding.getAnnotation(new String[] {"egl", "ui"}, "RunValidatorFromProgram");
		if(aBinding != null && Boolean.NO == aBinding.getValue()) {
			aBinding = sItemBinding.getAnnotation(new String[] {"egl", "ui"}, "ValidatorFunction");
			if(aBinding != null && aBinding.getValue() != null && IBinding.NOT_FOUND_BINDING != aBinding.getValue()) {
				functionBindings.add(aBinding.getValue());
			}
		}
		for(Iterator iter = sItemBinding.getChildren().iterator(); iter.hasNext();) {
			addValidatorFunctions(functionBindings, (StructureItemBinding) iter.next());
		}
	}

    public boolean visit (StructureItem item) {
        // Because part of the field declaration (i.e. its type) has already
        // been processed, we take over the traversal of FieldDeclaration in here
        if(item.hasSettingsBlock()) item.getSettingsBlock().accept(this);
        if(item.hasInitializer()) item.getInitializer().accept(this);
        resolveProgramAndRecordName(item);
        return false;
    }
    
   private void resolveProgramAndRecordName(StructureItem item) {
	   if (Binding.isValidBinding(item.resolveBinding())) {
			IAnnotationBinding ann = item.resolveBinding().getAnnotation(new String[] { "egl", "ui", "webTransaction" }, "programLinkData");
			if (Binding.isValidBinding(ann)) {
				IDataBinding field = ann.findData(InternUtil.intern(IEGLConstants.PROPERTY_PROGRAMNAME));
				if (Binding.isValidBinding(field)) {
					AnnotationFieldBinding fb = (AnnotationFieldBinding) field;
					fb.setValue(resolveProgramName(fb.getValue()), null, null, compilerOptions, false);
				}
				field = ann.findData(InternUtil.intern(IEGLConstants.PROPERTY_UIRECORDNAME));
				if (Binding.isValidBinding(field)) {
					AnnotationFieldBinding fb = (AnnotationFieldBinding) field;
					fb.setValue(resolveUIRecordName(fb.getValue()), null, null, compilerOptions, false);
				}
			}
		}
   }
   
	private Object resolveProgramName(Object obj) {
		
		if (!(obj instanceof String)) {
			return obj;
		}
		String nameString = (String)obj;
		
		Name name = new ExpressionParser(compilerOptions).parseAsName(nameString);
		if (name == null) {
			return nameString;
		}
		
		try {
			ITypeBinding type = bindTypeName(name);
			if (type.getKind() == ITypeBinding.PROGRAM_BINDING) {
				return type;
			}
		} catch (ResolutionException e) {}
		return nameString;
	}

	private Object resolveUIRecordName(Object obj) {
		
		if (!(obj instanceof String)) {
			return obj;
		}
		String nameString = (String)obj;
		
		Name name = new ExpressionParser(compilerOptions).parseAsName(nameString);
		if (name == null) {
			return nameString;
		}
		
		try {
			ITypeBinding type = bindTypeName(name);
			if (type.getKind() == ITypeBinding.FIXED_RECORD_BINDING) {
				return type;
			}
		} catch (ResolutionException e) {}
		return nameString;
	}

    
    public boolean visit(Assignment assignment) {
		Scope currentScopeParent = currentScope.getParentScope();		
		currentScope.setParentScope(NullScope.INSTANCE);
		assignment.getLeftHandSide().accept(this);
		currentScope.setParentScope(currentScopeParent);
		assignment.getRightHandSide().accept(this);
		return false;
	}
}
