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

import org.eclipse.edt.compiler.core.ast.AnnotationExpression;
import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.NameType;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AnnotationLeftHandScope;
import org.eclipse.edt.compiler.internal.core.lookup.AnnotationRightHandScope;
import org.eclipse.edt.compiler.internal.core.lookup.DefaultBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.NullScope;
import org.eclipse.edt.compiler.internal.core.lookup.ResolutionException;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.EClassifier;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EGenericType;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.EType;
import org.eclipse.edt.mof.MofFactory;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.EClassProxy;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.utils.NameUtile;


public class AnnotationTypeCompletor extends DefaultBinder {

    private org.eclipse.edt.mof.egl.AnnotationType annotationType;
	private IRPartBinding irBinding;
    
    public AnnotationTypeCompletor(Scope currentScope, IRPartBinding irBinding, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(currentScope, (org.eclipse.edt.mof.egl.AnnotationType)irBinding.getIrPart(), dependencyRequestor, problemRequestor, compilerOptions);
        this.annotationType = (org.eclipse.edt.mof.egl.AnnotationType)irBinding.getIrPart();
        this.irBinding = irBinding;
    }

    public boolean visit(Record record) {
    	
    	//Set bindAttempted on the subType so we dont try to resolve to it
    	record.getSubType().setBindAttempted(true);
    	        
        if (record.isPrivate()) {
        	annotationType.setAccessKind(AccessKind.ACC_PRIVATE);
        }
        record.getName().setType(annotationType);
        
        processFields(record);
        processSettingsBlocks(record);
        
        return false;
    }
    
    private void processSettingsBlocks(Record record) {
    	
    	record.accept(new DefaultASTVisitor() {
    		public boolean visit(Record record) {
    			return true;
    		}
    		
    		public boolean visit(SettingsBlock settingsBlock) {
    			return true;
    		}
    		
    		public boolean visit(org.eclipse.edt.compiler.core.ast.Assignment assignment) {
    			if (assignment.getLeftHandSide() instanceof SimpleName) {
    				SimpleName simpName = (SimpleName)assignment.getLeftHandSide();
    				EField field = annotationType.getEClass().getEField(simpName.getIdentifier());
    				if (field != null) {
    					
        				AnnotationRightHandScope rhScope = new AnnotationRightHandScope(currentScope, field);
        				Object obj =  new AnnotationTypeValueGatherer(assignment.getRightHandSide(), field, rhScope, annotationType, dependencyRequestor, problemRequestor, compilerOptions).getValue();
        				AnnotationValueValidator validator =  new AnnotationValueValidator(problemRequestor);
        				Object result = validator.validateValue(obj, assignment.getRightHandSide(), field, field.getEType(), field.isNullable());
        				if (result != null) {
        					annotationType.eSet(field, result);
        				}

        				simpName.setBindAttempted(true);
    				}
    			}
    			return false;
    		}
    		public void endVisit(SettingsBlock settingsBlock) {
    	    	//Now that we are done processing the field settings for the annotationType, process any annotations on the 
    	    	//annotationType
    	        AnnotationLeftHandScope scope = new AnnotationLeftHandScope(NullScope.INSTANCE, annotationType, annotationType, annotationType);
    	        SettingsBlockAnnotationBindingsCompletor blockCompletor = new SettingsBlockAnnotationBindingsCompletor(currentScope, annotationType, scope,
    	                dependencyRequestor, problemRequestor, compilerOptions);
    	        settingsBlock.accept(blockCompletor);
    		}
    		
		});
 
    }
    
    private void processFields(Record record) {
    	
    	final Set<String> definedNames = new HashSet<String>();
    	
    	record.accept(new DefaultASTVisitor() {
    		public boolean visit(Record record) {
    			return true;
    		}
    		public boolean visit(StructureItem structureItem) {
    			
    			EType eType = null;
    			try {
	    			eType = bindEType(structureItem.getType());
    	        } catch (ResolutionException e) {
    	            problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
    	            if(structureItem.hasSettingsBlock()) {
    	            	setBindAttemptedForNames(structureItem.getSettingsBlock());
    	            }
    	            return false; // Do not create the field binding if its type cannot be resolved
    	        }
    			
    			final EField field = createEField(structureItem, eType);
    			if (definedNames.contains(NameUtile.getAsName(field.getName()))) {
    	    		problemRequestor.acceptProblem(
    	        			structureItem.getName(),
    	    				IProblemRequestor.DUPLICATE_VARIABLE_NAME,
    	    				new String[] {
    	        				structureItem.getName().getCanonicalName(),
    	    					annotationType.getCaseSensitiveName()
    	        			});
    			}
    			else {
    				definedNames.add(NameUtile.getAsName(field.getName()));
    				annotationType.getEFields().add(field);
    				structureItem.getName().setElement(field);
    			}
    			
    			if (structureItem.hasSettingsBlock()) {
    				
    				AnnotationLeftHandScope lhScope =  new AnnotationLeftHandScope(NullScope.INSTANCE, null, null, null);
    	            SettingsBlockAnnotationBindingsCompletor blockCompletor = new SettingsBlockAnnotationBindingsCompletor(currentScope, annotationType, lhScope,
    	                    dependencyRequestor, problemRequestor, compilerOptions);
    	            structureItem.getSettingsBlock().accept(blockCompletor);
    	            //copy put the annotations from the settingsBlock into the metatData for the EField
    	            structureItem.getSettingsBlock().accept(new DefaultASTVisitor() {
    	            	public boolean visit(SettingsBlock settingsBlock) {
    	            		return true;
    	            	}
						public boolean visit(AnnotationExpression annExpr) {
							if (annExpr.resolveAnnotation() != null) {
								field.getMetadataList().add(annExpr.resolveAnnotation());
							}
							return false;
						}

					});
    			}

    			    			    			
    			if (structureItem.hasInitializer()) {
    				AnnotationRightHandScope rhScope = new AnnotationRightHandScope(currentScope, field);
    				Object obj =  new AnnotationValueGatherer(structureItem.getInitializer(), rhScope, annotationType, dependencyRequestor, problemRequestor, compilerOptions).getValue();
    				AnnotationValueValidator validator =  new AnnotationValueValidator(problemRequestor);
    				Object result = validator.validateValue(obj, structureItem.getInitializer(), field, field.getEType(), field.isNullable());
    				if (result != null) {
    					field.setInitialValue(result);
    				}
    				
    			}
    			
    			return false;
    		}
    		
    	    public EField createEField(StructureItem structureItem, EType etype) {

    	        String fieldName = structureItem.getName().getCaseSensitiveIdentifier();

    	        EField field = MofFactory.INSTANCE.createEField(true);
    	        field.setName(fieldName);
    	        field.setDeclarer(annotationType);
    	        field.setEType(etype);
    	        field.setIsNullable(structureItem.isNullable());
    	        
    	        structureItem.getName().setBindAttempted(true);
    	        return field;
    	    }
    		
		});
    }

	public void endVisit(Record record) {
    	irBinding.setValid(true);
        super.endVisit(record);
    }
	
    public EType bindEType(org.eclipse.edt.compiler.core.ast.Type type) throws ResolutionException {
        switch(type.getKind()) {
            case org.eclipse.edt.compiler.core.ast.Type.ARRAYTYPE:
                ArrayType arrayType = (ArrayType) type;
                
                if (arrayType.hasInitialSize()) {
    	    		problemRequestor.acceptProblem(
    	    				arrayType.getInitialSize(),
    	    				IProblemRequestor.ARRAY_DIMENSION_NOT_ALLOWED,
    	    				new String[] {});
                }
                
                EType elementType = bindEType(arrayType.getElementType());
                EGenericType genType = MofFactory.INSTANCE.createEGenericType(true);
				try {
					genType.setEClassifier((EClassifier)Environment.getCurrentEnv().findType(MofConversion.Type_EList));
				} catch (Exception e) {
				}
				genType.getETypeArguments().add(elementType);
                
                return genType;

            case org.eclipse.edt.compiler.core.ast.Type.NAMETYPE:
            	
                NameType nameType = (NameType) type;
                org.eclipse.edt.mof.egl.Type result =  bindTypeName(nameType.getName());  
                if (nameType.getArguments() != null && nameType.getArguments().size() > 0) {
     	    		problemRequestor.acceptProblem(
    	    				type,
    	    				IProblemRequestor.STEREOTYPE_NO_PARMS,
    	    				new String[] {});
                }
                return translateToEType(result, nameType);
                
            default: throw new RuntimeException("Shouldn't come into here");            
        }
    }
    
    private EType translateToEType(Type type, NameType nameType) throws ResolutionException{
    	if (type == null || type.getClassifier() == null) {
    		return null;
    	}
    	try {
    		Classifier classifier = type.getClassifier();
			//handle any, string, boolean, int, float, decimal
			if (classifier.equals(TypeUtils.Type_ANY)) {
				return (EType)Environment.getCurrentEnv().find(MofConversion.Type_JavaObject);
			}
			if (classifier.equals(TypeUtils.Type_BOOLEAN)) {
				return (EType)Environment.getCurrentEnv().find(MofConversion.Type_EBoolean);
			}
			if (classifier.equals(TypeUtils.Type_STRING)) {
				return (EType)Environment.getCurrentEnv().find(MofConversion.Type_EString);
			}
			if (classifier.equals(TypeUtils.Type_INT) || type.equals(TypeUtils.Type_SMALLINT) || type.equals(TypeUtils.Type_BIGINT)) {
				return (EType)Environment.getCurrentEnv().find(MofConversion.Type_EInteger);
			}
			if (classifier.equals(TypeUtils.Type_FLOAT) || type.equals(TypeUtils.Type_SMALLFLOAT)) {
				return (EType)Environment.getCurrentEnv().find(MofConversion.Type_EFloat);
			}
			if (classifier.equals(TypeUtils.Type_DECIMAL)) {
				return (EType)Environment.getCurrentEnv().find(MofConversion.Type_EDecimal);
			}
			
			//handle proxy types
			EType etype = BindingUtil.getETypeFromProxy(type);
			if (etype != null) {
				return etype;
			}
			
			//If the type is an annotationType or an Enumeration, it is already an EType
			if (type instanceof EType) {
				return ((EType) type);
			}
			
			if (type instanceof Classifier) {
				EObject proxiedType = BindingUtil.getMofClassProxyFor((Classifier)type);
				if (proxiedType instanceof EType) {
					return (EType)proxiedType;
				}
			}
			
		} catch (Exception e) {
		}
    	
    	//IF we get here, the type is not valid for use on an annotation field
    	throw new ResolutionException(nameType.getName().getOffset(), nameType.getName().getOffset() + nameType.getName().getLength(), IProblemRequestor.STEREOTYPE_BAD_TYPE, new String[] {nameType.getName().getCanonicalName(), annotationType.getCaseSensitiveName()});    	
    }

	    

}
