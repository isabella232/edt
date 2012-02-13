/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.validation.part;

import java.util.Iterator;

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.InterfaceBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Dave Murray
 */
public class InterfaceValidator extends AbstractASTVisitor {
	
	protected IProblemRequestor problemRequestor;
	private InterfaceBinding partBinding;
    private ICompilerOptions compilerOptions;
    protected Interface iFaceNode = null;
	
	public InterfaceValidator(IProblemRequestor problemRequestor, InterfaceBinding partBinding, ICompilerOptions compilerOptions) {
		this.problemRequestor = problemRequestor;
		this.partBinding = partBinding;
		this.compilerOptions = compilerOptions;
	}
	
	public boolean visit(Interface interfaceNode) {
		iFaceNode = interfaceNode;
		EGLNameValidator.validate(interfaceNode.getName(), EGLNameValidator.HANDLER, problemRequestor, compilerOptions);
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(interfaceNode);
		checkExtendedTypes(interfaceNode);
		return true;
	}

	public boolean visit(NestedFunction nestedFunction) {
		nestedFunction.accept(new FunctionValidator(problemRequestor, partBinding, compilerOptions));
		if (nestedFunction.isPrivate()){
			problemRequestor.acceptProblem(nestedFunction.getName(),
					IProblemRequestor.INTERFACE_FUNCTION_CANNOT_BE_PRIVATE);
		}

		ServiceInterfaceValidatorUtil.validateParametersAndReturn(nestedFunction,false,problemRequestor);
		
		if (InternUtil.intern(nestedFunction.getName().getCanonicalName()) == InternUtil.intern(IEGLConstants.MNEMONIC_MAIN)){
			problemRequestor.acceptProblem(iFaceNode.getName(),
					IProblemRequestor.LIBRARY_NO_MAIN_FUNCTION_ALLOWED,
					new String[] {partBinding.getCaseSensitiveName()});
		}
		
		return false;
	}
	
	public boolean visit(Constructor constructor) {
		constructor.accept(new FunctionValidator(problemRequestor, partBinding, compilerOptions));
		return false;
	}

	
	private void checkExtendedTypes(Interface iface) {
		for(Iterator iter = iface.getExtendedTypes().iterator(); iter.hasNext();) {
			Name nameAST = (Name) iter.next();
			ITypeBinding extendedType = (ITypeBinding) (nameAST).resolveBinding();
			if(extendedType != null && IBinding.NOT_FOUND_BINDING != extendedType) {
				boolean typeIsValid = false;
				
				if(ITypeBinding.INTERFACE_BINDING == extendedType.getKind()) {
					typeIsValid = true;
				}
				
				if(!typeIsValid) {
					problemRequestor.acceptProblem(
							nameAST,
						IProblemRequestor.INTERFACE_MUST_EXTEND_INTERFACE,
						new String[] {
							extendedType.getCaseSensitiveName()
						});
				}
			}
		}
	}
	
	public boolean visit(SettingsBlock settingsBlock) {
		return false;
	}
}
