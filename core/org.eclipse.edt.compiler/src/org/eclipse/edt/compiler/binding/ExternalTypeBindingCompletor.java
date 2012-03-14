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
package org.eclipse.edt.compiler.binding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.AnnotationLeftHandScope;
import org.eclipse.edt.compiler.internal.core.lookup.ExternalTypeScope;
import org.eclipse.edt.compiler.internal.core.lookup.FunctionScope;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.ResolutionException;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;


/**
 * @author winghong
 */
public class ExternalTypeBindingCompletor extends AbstractBinder {

    private ExternalTypeBinding externalTypeBinding;
	private IProblemRequestor problemRequestor;
	protected PartSubTypeAndAnnotationCollector partSubTypeAndAnnotationCollector;
	
	protected Set definedDataNames = new HashSet();
	protected Set definedFunctionNames = new HashSet();
	private List dataDeclarations = new ArrayList();

    public ExternalTypeBindingCompletor(Scope currentScope, ExternalTypeBinding externalTypeBinding, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(currentScope, externalTypeBinding, dependencyRequestor, compilerOptions);
        this.problemRequestor = problemRequestor;
        this.externalTypeBinding = externalTypeBinding;
    }
    
    public PartSubTypeAndAnnotationCollector getPartSubTypeAndAnnotationCollector() {
        if (partSubTypeAndAnnotationCollector == null) {
            partSubTypeAndAnnotationCollector = new PartSubTypeAndAnnotationCollector(externalTypeBinding, this, currentScope, problemRequestor);
        }
        return partSubTypeAndAnnotationCollector;
    }
    
    public boolean visit(ExternalType externalType) {
    	externalType.getName().setBinding(externalTypeBinding);
    	externalType.accept(getPartSubTypeAndAnnotationCollector());
        
        externalTypeBinding.setPrivate(externalType.isPrivate());
        
        processExtends(externalType);
    	
        return true;
    }
    
    private void processExtends(ExternalType externalType) {
        if(externalType.hasExtendedType()) {
    		for(Iterator iter = externalType.getExtendedTypes().iterator(); iter.hasNext();) {
            	try {
            		
            		Name name = (Name) iter.next();
        			ITypeBinding typeBinding = bindTypeName(name);
        			
        			if (Binding.isValidBinding(typeBinding) && typeBinding.getKind() == ITypeBinding.EXTERNALTYPE_BINDING) {
        				
        				ExternalTypeBinding extendedET = (ExternalTypeBinding) typeBinding;
        				if (extendedET.containsExtendsFor(externalTypeBinding)) {
                			problemRequestor.acceptProblem(
                    				name,
                    				IProblemRequestor.RECURSIVE_LOOP_IN_EXTENDS,
                    				new String[] {externalTypeBinding.getCaseSensitiveName(), name.toString()});
        					name.setBinding(Binding.NOT_FOUND_BINDING);
        				}
        				else {
        					externalTypeBinding.addExtendedType(typeBinding);
        				}
        			}
            	}
        		catch (ResolutionException e) {
        			problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
        		}
    		}
        }
    }
    
    
	public void endVisit(ExternalType externalType) {
		processSettingsBlocks();
		externalTypeBinding.setValid(true);
	}
    
    public boolean visit(NestedFunction nestedFunction) {
    	String name = nestedFunction.getName().getIdentifier();
    	
    	FunctionBinding functionBinding = new FunctionBinding(nestedFunction.getName().getCaseSensitiveIdentifier(), externalTypeBinding);
        FunctionBindingCompletor functionBindingCompletor = new FunctionBindingCompletor(externalTypeBinding, currentScope, functionBinding, dependencyRequestor, problemRequestor, compilerOptions);
        nestedFunction.accept(functionBindingCompletor);
    	
    	NestedFunctionBinding nestedFunctionBinding = new NestedFunctionBinding(functionBinding.getCaseSensitiveName(), externalTypeBinding, functionBinding);
    	if (definedDataNames.contains(name)) {
        	problemRequestor.acceptProblem(nestedFunction.getName(), IProblemRequestor.DUPLICATE_NAME_ACROSS_LISTS, new String[] { nestedFunction.getName().getCanonicalName(), externalTypeBinding.getCaseSensitiveName() });
        } else if(definedFunctionNames.contains(name)) {
        	for(Iterator iter = externalTypeBinding.getDeclaredFunctions().iterator(); iter.hasNext();) {
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
		externalTypeBinding.addDeclaredFunction(nestedFunctionBinding);
		nestedFunction.getName().setBinding(nestedFunctionBinding);

        return false;
    }
    
    protected void processSettingsBlocks() {
    	processDataDeclarationsSettingsBlocks();
        if (getPartSubTypeAndAnnotationCollector().getSettingsBlocks().size() > 0) {
            AnnotationLeftHandScope scope = new AnnotationLeftHandScope(currentScope, externalTypeBinding, externalTypeBinding, externalTypeBinding, -1, externalTypeBinding);
            //If the part type is specified on the part (as opposed to with an annotation),
            //then create an annotation scope to handle resolution of subtype dependent properties
            if (!getPartSubTypeAndAnnotationCollector().isFoundSubTypeInSettingsBlock()) {
                if (getPartSubTypeAndAnnotationCollector().getSubTypeAnnotationBinding() != null) {                	
                	scope = new AnnotationLeftHandScope(scope, getPartSubTypeAndAnnotationCollector().getSubTypeAnnotationBinding(), getPartSubTypeAndAnnotationCollector().getSubTypeAnnotationBinding().getType(), getPartSubTypeAndAnnotationCollector().getSubTypeAnnotationBinding(), -1, externalTypeBinding);
                }
            }
	        SettingsBlockAnnotationBindingsCompletor blockCompletor = new SettingsBlockAnnotationBindingsCompletor(new ExternalTypeScope(currentScope, externalTypeBinding), externalTypeBinding, scope, dependencyRequestor, problemRequestor, compilerOptions);
            Iterator i = getPartSubTypeAndAnnotationCollector().getSettingsBlocks().iterator();
	        while (i.hasNext()) {
	            SettingsBlock block = (SettingsBlock)i.next();
	            block.accept(blockCompletor);
	        }
        }
    }
    
    public boolean visit(ClassDataDeclaration classDataDeclaration) {
		ITypeBinding typeBinding = null;
        try {
            typeBinding = bindType(classDataDeclaration.getType());
        } catch (ResolutionException e) {
            problemRequestor
                    .acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
            if(classDataDeclaration.hasSettingsBlock()) {
            	bindNamesToNotFound(classDataDeclaration.getSettingsBlockOpt());
            }
            return false; // Do not create the class field bindings if the type
            // cannot be resolved
        }

        dataDeclarations.add(classDataDeclaration);
        
        Iterator i = classDataDeclaration.getNames().iterator();
        while (i.hasNext()) {
            Name name = (Name) i.next();
            String dataName = name.getIdentifier();

            ClassFieldBinding fieldBinding = new ClassFieldBinding(name.getCaseSensitiveIdentifier(), externalTypeBinding, typeBinding);
            
            fieldBinding.setIsStatic(classDataDeclaration.isStatic());
            fieldBinding.setIsPrivate(classDataDeclaration.isPrivate());

            if (definedDataNames.contains(dataName)) {
                problemRequestor.acceptProblem(
                	name,
                	IProblemRequestor.DUPLICATE_NAME_ACROSS_LISTS,
                	new String[] { dataName, externalTypeBinding.getCaseSensitiveName() });
            } else {
            	externalTypeBinding.addClassField(fieldBinding);
                definedDataNames.add(dataName);
            }
            name.setBinding(fieldBinding);
        }

        return false;
    }
    
    public boolean visit(Constructor constructor) {
    	final ConstructorBinding constructorBinding = new ConstructorBinding(externalTypeBinding);
    	final Set definedParameters = new HashSet();
    	
    	constructor.setBinding(constructorBinding);
    	
    	constructorBinding.setPrivate(constructor.isPrivate());
    	
    	constructor.accept(new AbstractASTVisitor() {
    		public boolean visit(FunctionParameter functionParameter) {
    			String parmName = functionParameter.getName().getIdentifier();
    	        Type parmType = functionParameter.getType();        
    	        ITypeBinding typeBinding = null;
    	        try {
    	            typeBinding = bindType(parmType);
    	        } catch (ResolutionException e) {
    	        	functionParameter.getName().setBinding(new FunctionParameterBinding(functionParameter.getName().getCaseSensitiveIdentifier(), externalTypeBinding, IBinding.NOT_FOUND_BINDING, (IFunctionBinding) constructorBinding.getType()));
    	            problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
    	            return false;
    	        }
    	        
    	        FunctionParameterBinding funcParmBinding = new FunctionParameterBinding(functionParameter.getName().getCaseSensitiveIdentifier(), externalTypeBinding, typeBinding, (IFunctionBinding) constructorBinding.getType());
    	        functionParameter.getName().setBinding(funcParmBinding);
    	        
    	        if(!BindingUtilities.isValidDeclarationType(typeBinding)) {
    	        	problemRequestor.acceptProblem(
    	        		parmType,
    	        		IProblemRequestor.FUNCTION_PARAMETER_HAS_INCORRECT_TYPE,
    					new String[] {functionParameter.getName().getCanonicalName(), IEGLConstants.KEYWORD_CONSTRUCTOR});
    	        	return false;				
    	        }
    	        
    	        FunctionParameter.AttrType attrType = functionParameter.getAttrType();
    	        if (attrType == FunctionParameter.AttrType.FIELD) {
    	            funcParmBinding.setField(true);
    	        } else if (attrType == FunctionParameter.AttrType.SQLNULLABLE) {
    	            funcParmBinding.setSqlNullable(true);
    	        }
    	        FunctionParameter.UseType useType = functionParameter.getUseType();
    	        if (useType == FunctionParameter.UseType.IN) {
    	            funcParmBinding.setInput(true);
    	        } else if (useType == FunctionParameter.UseType.OUT) {
    	            funcParmBinding.setOutput(true);
    	        } else if (useType == null && Binding.isValidBinding(typeBinding) && typeBinding.isReference()) {
    	            funcParmBinding.setInput(true);
    	        }

    	        if (definedParameters.contains(parmName)) {
    	            problemRequestor.acceptProblem(functionParameter, IProblemRequestor.DUPLICATE_NAME_ACROSS_LISTS, new String[] { functionParameter.getName().getCanonicalName(), IEGLConstants.KEYWORD_CONSTRUCTOR });
    	        } else {
    	            constructorBinding.addParameter(funcParmBinding);
    	            definedParameters.add(parmName);
    	        }
    	        
    	        return false;
    		}
    	});
    	
    	externalTypeBinding.addConstructor(constructorBinding);
    	
    	if (constructor.hasSettingsBlock()) {
            FunctionScope functionScope = new FunctionScope(currentScope, (FunctionBinding)constructorBinding.getType());
            AnnotationLeftHandScope scope = new AnnotationLeftHandScope(functionScope, constructorBinding, null, constructorBinding, -1, externalTypeBinding);
            SettingsBlockAnnotationBindingsCompletor blockCompletor = new SettingsBlockAnnotationBindingsCompletor(functionScope, externalTypeBinding, scope,
                    dependencyRequestor, problemRequestor, compilerOptions);
            constructor.getSettingsBlock().accept(blockCompletor);
    		
    	}
    	
    	return false;
    }
    
    private void processDataDeclarationsSettingsBlocks() {
        Iterator i = dataDeclarations.iterator();
        while (i.hasNext()) {
            processSettingsBlock((ClassDataDeclaration)i.next(), externalTypeBinding, currentScope, problemRequestor);
        }
    }
}
