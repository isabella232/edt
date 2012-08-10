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
package org.eclipse.edt.compiler.internal.core.validation.part;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.compiler.ASTValidator;
import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Dave Murray
 */
public class InterfaceValidator extends FunctionContainerValidator {
	
	IRPartBinding irBinding;
	private org.eclipse.edt.mof.egl.Interface interfaceBinding;
    protected Interface iFaceNode;
	
	public InterfaceValidator(IProblemRequestor problemRequestor, IRPartBinding irBinding, ICompilerOptions compilerOptions) {
		super(problemRequestor, irBinding, compilerOptions);
		this.irBinding = irBinding;
		interfaceBinding = (org.eclipse.edt.mof.egl.Interface)irBinding.getIrPart();
	}
	
	@Override
	public boolean visit(Interface interfaceNode) {
		iFaceNode = interfaceNode;
		EGLNameValidator.validate(interfaceNode.getName(), EGLNameValidator.HANDLER, problemRequestor, compilerOptions);
//		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(interfaceNode); TODO
		
		if (iFaceNode.hasExtendedType()) {
			checkExtendedTypes();
			checkCycles();
		}
		return true;
	}
	
	@Override
	public boolean visit(NestedFunction nestedFunction) {
		List<ASTValidator> validators = partBinding.getEnvironment().getCompiler().getValidatorsFor(nestedFunction);
    	if (validators != null && validators.size() > 0) {
    		for (ASTValidator validator : validators) {
    			validator.validate(nestedFunction, (IRPartBinding)partBinding, problemRequestor, compilerOptions);
    		}
    	}
		
		if (nestedFunction.isPrivate()){
			problemRequestor.acceptProblem(nestedFunction.getName(),
					IProblemRequestor.INTERFACE_FUNCTION_CANNOT_BE_PRIVATE);
		}
		
//		ServiceInterfaceValidatorUtil.validateParametersAndReturn(nestedFunction,false,problemRequestor); TODO
		
		if (InternUtil.intern(nestedFunction.getName().getCanonicalName()) == InternUtil.intern(IEGLConstants.MNEMONIC_MAIN)){
			problemRequestor.acceptProblem(iFaceNode.getName(),
					IProblemRequestor.LIBRARY_NO_MAIN_FUNCTION_ALLOWED,
					new String[] {partBinding.getCaseSensitiveName()});
		}
		
		return false;
	}
	
	private void checkExtendedTypes() {
		for (Iterator iter = iFaceNode.getExtendedTypes().iterator(); iter.hasNext();) {
			Name nameAST = (Name) iter.next();
			Type extendedType = nameAST.resolveType();
			if (extendedType != null && !(extendedType instanceof org.eclipse.edt.mof.egl.Interface)) {
				problemRequestor.acceptProblem(
						nameAST,
						IProblemRequestor.INTERFACE_MUST_EXTEND_INTERFACE,
						new String[] {
								extendedType.getTypeSignature()
						});
			}
		}
	}
	
	private void checkCycles() {
		for (Name name : iFaceNode.getExtendedTypes()) {
			Type type = name.resolveType();
			if (type instanceof org.eclipse.edt.mof.egl.Interface
					&& checkCycles((org.eclipse.edt.mof.egl.Interface)type, new HashSet<org.eclipse.edt.mof.egl.Interface>())) {
				problemRequestor.acceptProblem(
	    				name,
	    				IProblemRequestor.RECURSIVE_LOOP_IN_EXTENDS,
	    				new String[] {interfaceBinding.getCaseSensitiveName(), name.toString()});
			}
		}
	}
	
	private boolean checkCycles(org.eclipse.edt.mof.egl.Interface iface, Set<org.eclipse.edt.mof.egl.Interface> seen) {
		if (seen.contains(iface)) {
			return false;
		}
		
		if (interfaceBinding.equals(iface)) {
			return true;
		}
		
		//TODO sometimes the super interface binding is not completed...why?
		seen.add(iface);
		for (org.eclipse.edt.mof.egl.Interface superInterface : iface.getInterfaces()) {
			if (checkCycles(superInterface, seen)) {
				return true;
			}
		}
		return false;
	}
}
