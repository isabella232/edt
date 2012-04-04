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
import java.util.Set;

import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.ConstantFormField;
import org.eclipse.edt.compiler.core.ast.NestedForm;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.TopLevelForm;
import org.eclipse.edt.compiler.core.ast.VariableFormField;
import org.eclipse.edt.compiler.internal.core.builder.DefaultProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.NullProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.AnnotationLeftHandScope;
import org.eclipse.edt.compiler.internal.core.lookup.DataBindingScope;
import org.eclipse.edt.compiler.internal.core.lookup.DefaultBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.ResolutionException;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;


/**
 * @author winghong
 */
public class FormBindingFieldsCompletor extends AbstractBinder {
    
    public class FieldCompletor extends DefaultBinder {

        private FormFieldBinding fieldBinding;

        public FieldCompletor(Scope currentScope, FormFieldBinding fieldBinding, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
            super(currentScope, fieldBinding.getDeclaringPart(), dependencyRequestor, problemRequestor, compilerOptions);
            this.fieldBinding = fieldBinding;
        }
        
    	public boolean visit(VariableFormField variableFormField) {
    	    return true;
    	}
    	
        public void endVisit(VariableFormField variableFormField) {
            // TODO Auto-generated method stub
            super.endVisit(variableFormField);
        }
    	
        public boolean visit(ConstantFormField constantFormField) {
            return true;
        }
        
        public boolean visit(SettingsBlock settingsBlock) {
            
            Scope fieldScope = new DataBindingScope(currentScope, fieldBinding);
            AnnotationLeftHandScope annotationScope = new AnnotationLeftHandScope(fieldScope, fieldBinding, fieldBinding.getType(), fieldBinding, -1, fieldBinding.getDeclaringPart());        
            settingsBlock.accept(new SettingsBlockAnnotationBindingsCompletor(currentScope, fieldBinding.getDeclaringPart(), annotationScope, dependencyRequestor, problemRequestor, compilerOptions));
            return false;
        }
    }

    private Set definedFields = new HashSet();

    private FormBinding formBinding;

    private IProblemRequestor problemRequestor;

    public FormBindingFieldsCompletor(Scope currentScope, FormBinding formBinding,
            IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(currentScope, formBinding, dependencyRequestor, compilerOptions);
        this.formBinding = formBinding;
        this.problemRequestor = problemRequestor;
    }

    public boolean visit(TopLevelForm form) {
        return true;
    }

    public boolean visit(NestedForm form) {
        return true;
    }

    public boolean visit(SettingsBlock settingsBlock) {
        return false;
    }

    public VariableFormFieldBinding createVariableField(final VariableFormField variableFormField, ITypeBinding typeBinding, final IProblemRequestor problemRequestor) {

        String fieldName = variableFormField.getName().getIdentifier();
        
        if(isNullable(typeBinding)) {
        	problemRequestor.acceptProblem(variableFormField.getType(), IProblemRequestor.NULLABLE_TYPE_NOT_ALLOWED_IN_PART);
        	typeBinding = typeBinding.getBaseType();
        }

        VariableFormFieldBinding field = new VariableFormFieldBinding(variableFormField.getName().getCaseSensitiveIdentifier(), formBinding, typeBinding);
        
        ITypeBinding baseType = field.getType().getBaseType();
        if (baseType.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING) {
        	Primitive prim = ((PrimitiveTypeBinding) baseType).getPrimitive();
        	if(prim == Primitive.UNICODE ||
        	   prim == Primitive.ANY ||
        	   prim == Primitive.MONTHSPAN_INTERVAL ||
        	   prim == Primitive.SECONDSPAN_INTERVAL ||
        	   prim == Primitive.INTERVAL ||
			   prim == Primitive.STRING) {
        		problemRequestor.acceptProblem(
        			variableFormField.getType(),
					IProblemRequestor.INVALID_FORM_FIELD_TYPE,
					new String[] {
        				variableFormField.getName().getCanonicalName(),
						formBinding.getCaseSensitiveName(),
						prim.getName()
        			});
        	}
        }
        else {
        	if(baseType.getKind() != ITypeBinding.DATAITEM_BINDING) {
	            problemRequestor.acceptProblem(
	            	variableFormField.getType(),
					IProblemRequestor.INVALID_FORM_DATAITEMDEFINATIONIDENTIFIER_IS_INVALID,
					new String[] {baseType.getName(), fieldName, formBinding.getCaseSensitiveName()});
        	}
        }
        
        if(typeBinding.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
	        OccursValueFinder occursFinder = new OccursValueFinder(problemRequestor, variableFormField.getName().getCanonicalName()) {
	        	protected void reportSizeNotIntegerError(Node errorNode, String canonicalExprString) {
					problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.INVALID_FORM_FIELD_OCCURS_VALUE,
						new String[] {variableFormField.getName().getCanonicalName(), formBinding.getCaseSensitiveName()});
				}
	        };	        
	        ArrayType arrayType = (ArrayType) variableFormField.getType();
	        if(arrayType.hasInitialSize()) {
				arrayType.getInitialSize().accept(occursFinder);
				int occurs = occursFinder.getOccursValue();
				if(occurs > 0) {
					field.setOccurs(occurs);
					field.setType(field.getType().getBaseType());
				}
	        }
	        else {
	        	problemRequestor.acceptProblem(
	        		variableFormField.getName(),
	        		IProblemRequestor.INVALID_FORM_FIELD_OCCURS_VALUE,
					new String[] {variableFormField.getName().getCanonicalName(), formBinding.getCaseSensitiveName()});
	        }
        }
        
        variableFormField.getName().setBinding(field);

        return field;
    }
    
    
	public boolean visit(VariableFormField variableFormField) {
        ITypeBinding typeBinding = null;
        try {
            typeBinding = bindType(variableFormField.getType());
        } catch (ResolutionException e) {
       		problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
            return false;
        }

        final boolean[] fieldIsValid = new boolean[] {true}; 
        VariableFormFieldBinding fieldBinding = createVariableField(variableFormField, typeBinding, new DefaultProblemRequestor() {
			public void acceptProblem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts) {
				fieldIsValid[0] = false;
		 		if (severity == IMarker.SEVERITY_ERROR) {
		 			setHasError(true);
		 		}
				problemRequestor.acceptProblem(startOffset, endOffset, severity, problemKind, inserts);
			}
        });
        
        if (variableFormField.hasInitializer()) {
         	fieldBinding.setInitialValue(getConstantValue(variableFormField.getInitializer(), NullProblemRequestor.getInstance(), true));
        }
 
        if (fieldBinding != null) {
        	if(definedFields.contains(variableFormField.getName().getIdentifier())) {
        		problemRequestor.acceptProblem(
        			variableFormField.getName(),
        			IProblemRequestor.INVALID_FORM_FIELD_IDENTIFIER_DUPLICATION,
					new String[] {
        				variableFormField.getName().getCanonicalName(),
						formBinding.getCaseSensitiveName()
        			});
        	}
        	else {
        		definedFields.add(variableFormField.getName().getIdentifier());
        		if(fieldIsValid[0]) {
        			formBinding.addFormField(fieldBinding);
        		}
        	}
        	variableFormField.accept(new FieldCompletor(currentScope, fieldBinding, dependencyRequestor,
                    problemRequestor, compilerOptions));
        }

        return false;
    }
	
	public boolean visit(ConstantFormField constantFormField) {
		ConstantFormFieldBinding fieldBinding = new ConstantFormFieldBinding(formBinding);
        formBinding.addFormField(fieldBinding);
        constantFormField.setBinding(fieldBinding);
        constantFormField.accept(new FieldCompletor(currentScope, fieldBinding, dependencyRequestor,
                problemRequestor, compilerOptions));
        
		return false;
	}
	

}
