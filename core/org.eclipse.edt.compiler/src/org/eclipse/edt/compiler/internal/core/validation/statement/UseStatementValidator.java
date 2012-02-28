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
	package org.eclipse.edt.compiler.internal.core.validation.statement;
	
	import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.DataItemBinding;
import org.eclipse.edt.compiler.binding.DataTableBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.MutuallyExclusiveAnnotationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.binding.UsedTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTPartVisitor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.SelectedValueItemAnnotationValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;

	
	/**
	 * @author Craig Duval
	 */
	public class UseStatementValidator extends DefaultASTVisitor implements IOStatementValidatorConstants {
		
		private IPartBinding parent;
		private String canonicalParentName;
		private IProblemRequestor problemRequestor;
        private ICompilerOptions compilerOptions;
		
		public UseStatementValidator(IPartBinding binding,String canonicalParentName, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
			parent = binding;
			this.problemRequestor = problemRequestor;
			this.canonicalParentName = canonicalParentName;
			this.compilerOptions = compilerOptions;
		}
		
		public boolean visit(final UseStatement useStatement) {
			if (!StatementValidator.isValidBinding(parent)){
				return false;
			}
			
			if (!StatementValidator.isValidBinding(useStatement.getUsedTypeBinding().getType())){
				return false;
			}
			
			if (useStatement.getNames().size() == 0){
				return false;
			}
			
			new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(useStatement);
			checkProperties(useStatement);
			
			useStatement.getParent().accept(new AbstractASTPartVisitor(){
				public boolean visit(Service service) {
					validateMsgTable();
					if (useStatement.getUsedTypeBinding().getType().getKind() == ITypeBinding.SERVICE_BINDING){
						problemRequestor.acceptProblem((Node)useStatement.getNames().get(0),
								IProblemRequestor.SERVICE_CANNOT_BE_USED_BY_SERVICE);
					}else if (useStatement.getUsedTypeBinding().getType().getKind() == ITypeBinding.FORMGROUP_BINDING){
						problemRequestor.acceptProblem((Node)useStatement.getNames().get(0),
								IProblemRequestor.SERVICE_CANNOT_USE_FORM_GROUP);
					}
					return false;
				}
				
				public boolean visit(Handler pageHandler){
					validateHandlers(IEGLConstants.KEYWORD_HANDLER);
					validateMsgTable();
					return false;
				}
				
				public boolean visit (Program program){
					validateProgLibTarget(IEGLConstants.KEYWORD_PROGRAM);
					validateMsgTable();
					if (parent.getAnnotation(EGLUIWEBTRANSACTION, "VGWebTransaction") != null){
						if (useStatement.getUsedTypeBinding().getType().getKind() == ITypeBinding.FORMGROUP_BINDING){
							problemRequestor.acceptProblem((Node)useStatement.getNames().get(0),
									IProblemRequestor.USE_FORMGROUP_NOT_VALID_IN_WEBPROGRAM);
						}
					}
					
					if (useStatement.getUsedTypeBinding().getAnnotation(EGLCORE, "DeleteAfterUse") != null){
						problemRequestor.acceptProblem((Node)useStatement.getNames().get(0),
								IProblemRequestor.USE_STATEMENT_VAGCOMPATIBILITY,
								new String[] {IEGLConstants.PROPERTY_DELETEAFTERUSE});
					}
					return false;
				}
				
				public boolean visit (Library library){
					validateProgLibTarget(IEGLConstants.KEYWORD_LIBRARY);
					validateMsgTable();
					return false;
				}
				
				
				protected void validateMsgTable(){
					if (parent.getAnnotation(EGLUIWEBTRANSACTION, "VGWebTransaction") == null &&
						useStatement.getUsedTypeBinding().getType().getAnnotation(EGLCORE, "MsgTable") != null){
						//check first column make sure is num
						DataTableBinding dt = (DataTableBinding)useStatement.getUsedTypeBinding().getType();
						if (dt.getStructureItems().size() > 0){
							StructureItemBinding binding = (StructureItemBinding)dt.getStructureItems().get(0);
							if (StatementValidator.isValidBinding(binding)){
								if (StatementValidator.isValidBinding(binding.getType())){
									ITypeBinding typeBinding = binding.getType();
									boolean error = false;
									if (typeBinding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING && 
										((PrimitiveTypeBinding)typeBinding).getPrimitive() != Primitive.NUM){
										error = true;
									}else if (typeBinding.getKind() == ITypeBinding.DATAITEM_BINDING &&
											((DataItemBinding)typeBinding).getPrimitiveTypeBinding().getPrimitive() != Primitive.NUM){
										error = true;
									}else if (typeBinding.getKind() != ITypeBinding.PRIMITIVE_TYPE_BINDING &&
											typeBinding.getKind() != ITypeBinding.DATAITEM_BINDING){
										error= true;
									}
									
									if (error){
										problemRequestor.acceptProblem((Node)useStatement.getNames().get(0),
												IProblemRequestor.MESSAGE_TABLE_MUST_HAVE_FIRST_COLUMN_NUM_UNLESS_USED_BY_WEBTRANSACTION,
												new String[] {((Name)useStatement.getNames().get(0)).getCanonicalName()});
									}
								}
							}
						}
							
					}
				}
				
				protected void validateProgLibTarget(String type){
					if (useStatement.getUsedTypeBinding().getType().getKind() != ITypeBinding.FORMGROUP_BINDING &&
							useStatement.getUsedTypeBinding().getType().getKind() != ITypeBinding.DATATABLE_BINDING &&
							useStatement.getUsedTypeBinding().getType().getKind() != ITypeBinding.LIBRARY_BINDING &&
							useStatement.getUsedTypeBinding().getType().getKind() != ITypeBinding.ENUMERATION_BINDING){
						problemRequestor.acceptProblem((Node)useStatement.getNames().get(0),
								IProblemRequestor.PROGRAM_OR_LIBRARY_USE_STATEMENT_RESOLVES_TO_INVALID_TYPE,
								new String[] {((Name)useStatement.getNames().get(0)).getCanonicalName(),
								type,
								canonicalParentName});
					}
				}
				
				protected void validateHandlers(String handlertypename){
					if (useStatement.getUsedTypeBinding().getType().getKind() == ITypeBinding.FORMGROUP_BINDING){
						problemRequestor.acceptProblem((Node)useStatement.getNames().get(0),
								IProblemRequestor.INVALID_HANDLER_USEDECLARATION_VALUE,
								new String[] {((Name)useStatement.getNames().get(0)).getCanonicalName(),
								handlertypename,
								canonicalParentName});
					}
				}
				
				public void visitPart(Part part){
					
				}
			});

			return false;
		}
		
		private final MutuallyExclusiveAnnotationAnnotationTypeBinding[] MUTUALEXCLUSIONCHECKS = new MutuallyExclusiveAnnotationAnnotationTypeBinding[] {
			new MutuallyExclusiveAnnotationAnnotationTypeBinding(InternUtil.internCaseSensitive(IEGLConstants.PROPERTY_SELECTEDROWITEM),
				new String[] {
					InternUtil.internCaseSensitive(IEGLConstants.PROPERTY_SELECTEDVALUEITEM)
				}
			)
		};

		private void checkProperties(UseStatement useStatement) {
			UsedTypeBinding usedTypeBinding = useStatement.getUsedTypeBinding();
			if(usedTypeBinding != null) {
				List annotations = usedTypeBinding.getAnnotations();
				Map allAnnotations = new HashMap();
				for(Iterator iter = annotations.iterator(); iter.hasNext();) {
					IAnnotationBinding aBinding = (IAnnotationBinding) iter.next();
					allAnnotations.put(aBinding.getName(), aBinding);
				}
				
				Name firstName = (Name) useStatement.getNames().get(0);
				for(int i = 0; i < MUTUALEXCLUSIONCHECKS.length; i++) {					
					MUTUALEXCLUSIONCHECKS[i].validate(firstName, firstName, null, usedTypeBinding.getCaseSensitiveName(), allAnnotations, problemRequestor, compilerOptions);
				}
				
				new SelectedValueItemAnnotationValidator().doValidate(firstName, firstName, usedTypeBinding, usedTypeBinding.getType(), firstName.getCanonicalName(), allAnnotations, problemRequestor, compilerOptions);
			}
		}
	}
	
	


