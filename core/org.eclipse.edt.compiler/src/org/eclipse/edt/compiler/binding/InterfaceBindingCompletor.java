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

import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.ResolutionException;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.egl.Type;


/**
 * @author winghong
 */
public class InterfaceBindingCompletor extends FunctionContainerBindingCompletor {

    private org.eclipse.edt.mof.egl.Interface interfaceBinding;
	protected Set definedFunctionNames = new HashSet();

    public InterfaceBindingCompletor(Scope currentScope, IRPartBinding irBinding, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(irBinding, currentScope, dependencyRequestor, problemRequestor, compilerOptions);
        this.interfaceBinding = (org.eclipse.edt.mof.egl.Interface)irBinding.getIrPart();
    }
    
    public boolean visit(Interface interfaceAST) {
    	interfaceAST.getName().setType(interfaceBinding);
    	interfaceAST.accept(getPartSubTypeAndAnnotationCollector());
        
        if (interfaceAST.isPrivate()) {
        	interfaceBinding.setAccessKind(AccessKind.ACC_PRIVATE);
    	}

        processExtends(interfaceAST);
        
    	return true;
    }
    
    private void processExtends(Interface interfaceAST) {
        if(interfaceAST.hasExtendedType()) {
    		for(Iterator iter = interfaceAST.getExtendedTypes().iterator(); iter.hasNext();) {
            	try {
            		
            		Name name = (Name) iter.next();
        			Type typeBinding = bindTypeName(name);
        			if (typeBinding instanceof org.eclipse.edt.mof.egl.Interface) {
        				//TODO check if typeBinding extends interfaceBinding (see old code below)
        				interfaceBinding.getSuperTypes().add((org.eclipse.edt.mof.egl.Interface)typeBinding);
        			}
        			
        			
//        			if (Binding.isValidBinding(typeBinding) && typeBinding.getKind() == ITypeBinding.INTERFACE_BINDING) {
//        				
//        				InterfaceBinding extendedInterface = (InterfaceBinding) typeBinding;
//        				if (extendedInterface.containsExtendsFor(interfaceBinding)) {
//                			problemRequestor.acceptProblem(
//                    				name,
//                    				IProblemRequestor.RECURSIVE_LOOP_IN_EXTENDS,
//                    				new String[] {interfaceBinding.getCaseSensitiveName(), name.toString()});
//        					name.setBinding(Binding.NOT_FOUND_BINDING);
//        				}
//        				else {
//        					interfaceBinding.addExtendedType(typeBinding);
//        				}
//        			}
            	}
        		catch (ResolutionException e) {
        			problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
        		}
    		}
        }
    }
    
	public void endVisit(Interface interfaceNode) {
		processSettingsBlocks();
		endVisitFunctionContainer(interfaceNode);
	}
    
    @Override
	protected StereotypeType getDefaultStereotypeType() {
		return null;
	}
}
