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

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.AnnotationLeftHandScope;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.ResolutionException;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;


/**
 * @author winghong
 */
public class InterfaceBindingCompletor extends AbstractBinder {

    private InterfaceBinding interfaceBinding;
	private IProblemRequestor problemRequestor;
	protected Set definedFunctionNames = new HashSet();
	protected PartSubTypeAndAnnotationCollector partSubTypeAndAnnotationCollector;

    public InterfaceBindingCompletor(Scope currentScope, InterfaceBinding interfaceBinding, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(currentScope, interfaceBinding, dependencyRequestor, compilerOptions);
        this.problemRequestor = problemRequestor;
        this.interfaceBinding = interfaceBinding;
    }
    
    public PartSubTypeAndAnnotationCollector getPartSubTypeAndAnnotationCollector() {
        if (partSubTypeAndAnnotationCollector == null) {
            partSubTypeAndAnnotationCollector = new PartSubTypeAndAnnotationCollector(interfaceBinding, this, currentScope, problemRequestor);
        }
        return partSubTypeAndAnnotationCollector;
    }
    
    public boolean visit(Interface interfaceAST) {
    	interfaceAST.getName().setBinding(interfaceBinding);
    	interfaceAST.accept(getPartSubTypeAndAnnotationCollector());
        processSettingsBlocks();
        
        interfaceBinding.setPrivate(interfaceAST.isPrivate());

        processExtends(interfaceAST);
        
    	return true;
    }
    
    private void processExtends(Interface interfaceAST) {
        if(interfaceAST.hasExtendedType()) {
    		for(Iterator iter = interfaceAST.getExtendedTypes().iterator(); iter.hasNext();) {
            	try {
            		
            		Name name = (Name) iter.next();
        			ITypeBinding typeBinding = bindTypeName(name);
        			
        			if (Binding.isValidBinding(typeBinding) && typeBinding.getKind() == ITypeBinding.INTERFACE_BINDING) {
        				
        				InterfaceBinding extendedInterface = (InterfaceBinding) typeBinding;
        				if (extendedInterface.containsExtendsFor(interfaceBinding)) {
                			problemRequestor.acceptProblem(
                    				name,
                    				IProblemRequestor.RECURSIVE_LOOP_IN_EXTENDS,
                    				new String[] {interfaceBinding.getCaseSensitiveName(), name.toString()});
        					name.setBinding(Binding.NOT_FOUND_BINDING);
        				}
        				else {
        					interfaceBinding.addExtendedType(typeBinding);
        				}
        			}
            	}
        		catch (ResolutionException e) {
        			problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
        		}
    		}
        }
    }
    
	public void endVisit(Interface interfaceNode) {
		interfaceBinding.setValid(true);
	}
    
    public boolean visit(NestedFunction nestedFunction) {
    	String name = nestedFunction.getName().getIdentifier();
    	
    	FunctionBinding functionBinding = new FunctionBinding(nestedFunction.getName().getCaseSensitiveIdentifier(), interfaceBinding);
        FunctionBindingCompletor functionBindingCompletor = new FunctionBindingCompletor(interfaceBinding, currentScope, functionBinding, dependencyRequestor, problemRequestor, compilerOptions);
        nestedFunction.accept(functionBindingCompletor);
    	
        NestedFunctionBinding nestedFunctionBinding = new NestedFunctionBinding(functionBinding.getCaseSensitiveName(), interfaceBinding, functionBinding);
    	if(definedFunctionNames.contains(name)) {
        	for(Iterator iter = interfaceBinding.getDeclaredFunctions().iterator(); iter.hasNext();) {
        		IFunctionBinding fBinding = (IFunctionBinding) ((IDataBinding) iter.next()).getType();
        		if(TypeCompatibilityUtil.functionSignituresAreIdentical(fBinding, functionBinding, compilerOptions, false, false)) {
        			problemRequestor.acceptProblem(
        				nestedFunction.getName(),
        				IProblemRequestor.DUPLICATE_NAME_IN_FILE,
        				new String[] {
        					IEGLConstants.KEYWORD_FUNCTION,
        					nestedFunction.getName().getCanonicalName()
        				});
        			nestedFunction.getName().setBinding(nestedFunctionBinding);
        			return false;
        		}
        	}
        } else {
            definedFunctionNames.add(name);
        }
		interfaceBinding.addDeclaredFunction(nestedFunctionBinding);
		nestedFunction.getName().setBinding(nestedFunctionBinding);
        return false;
    }
    
    protected void processSettingsBlocks() {
        if (getPartSubTypeAndAnnotationCollector().getSettingsBlocks().size() > 0) {
            AnnotationLeftHandScope scope = new AnnotationLeftHandScope(currentScope, interfaceBinding, interfaceBinding, interfaceBinding, -1, interfaceBinding);            
            SettingsBlockAnnotationBindingsCompletor blockCompletor = new SettingsBlockAnnotationBindingsCompletor(currentScope, interfaceBinding, scope, dependencyRequestor, problemRequestor, compilerOptions);
	        Iterator i = getPartSubTypeAndAnnotationCollector().getSettingsBlocks().iterator();
	        while (i.hasNext()) {
	            SettingsBlock block = (SettingsBlock)i.next();
	            block.accept(blockCompletor);
	        }
        }
    }
}
