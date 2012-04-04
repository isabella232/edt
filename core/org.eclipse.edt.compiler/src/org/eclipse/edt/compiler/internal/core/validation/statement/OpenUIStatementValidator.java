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
package org.eclipse.edt.compiler.internal.core.validation.statement;

import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.OpenUIStatementBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.VariableBinding;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.LiteralExpression;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.OnEventBlock;
import org.eclipse.edt.compiler.core.ast.OpenUIStatement;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Jason Peterson
 */
public class OpenUIStatementValidator extends DefaultASTVisitor implements IOStatementValidatorConstants{

	private IProblemRequestor problemRequestor;
	private ITypeBinding targetType;
	private OpenUIStatement openUIStatement;
	private OpenUIStatementBinding openUIStatementBinding;
	private boolean isNewExpression;
	private boolean isLiteralExpression;
	private OpenTargetValidator openTargetValidator = new OpenTargetValidator();
	private BindClauseValidator bindClauseValidator = new BindClauseValidator();
	private EventBlockValidator eventBlockValidator = new EventBlockValidator();
	private ICompilerOptions compilerOptions;

	
	public OpenUIStatementValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
	}
	
	public boolean visit(OpenUIStatement openUIStatement) {
		this.targetType = null;
		this.openUIStatement = openUIStatement;
		this.openUIStatementBinding = openUIStatement.getStatementBinding();
	
		openTargetValidator.validateOpenTargets();
		eventBlockValidator.validateEventBlocks();
		bindClauseValidator.validateBindClause();
		
		validateExpressionAttributes();

		return false;
	}
	
	 private class OpenTargetValidator {
		 
		 private void validateOpenTargets() {
			
			if (openUIStatement.getOpenableElements().size() == 1) {
				validateSingleOpenTarget();
			} else {
				validateMultipleOpenTargets();
			}
		}

		private void validateSingleOpenTarget() {
			Expression target = (Expression) openUIStatement.getOpenableElements().get(0);
			targetType = target.resolveTypeBinding();
			isNewExpression = false;
			
			target.accept(new DefaultASTVisitor() {
				
				public boolean visit(NewExpression newExpression) {
					isNewExpression = true;
					return false;
				}
			});

			if (StatementValidator.isValidBinding(targetType)) {

				if (targetType.isReference()) {
					// Now that we know targetType is a reference, proceed to
					// get its base type
					ITypeBinding elementType = targetType.getBaseType();
					
					if (AbstractBinder.typeIs(elementType, EGLUICONSOLE, "PROMPT")
							|| AbstractBinder.typeIs(elementType, EGLUICONSOLE, "WINDOW")
							|| AbstractBinder.typeIs(elementType, EGLUICONSOLE, "MENU")
							|| (AbstractBinder.typeIs(elementType, EGLUICONSOLE, "CONSOLEFIELD") && !isNewExpression)
							|| elementType.isDynamicallyAccessible()) {
						return;
					}
					
					if (targetType.getAnnotation(EGLUICONSOLE, "ConsoleForm") != null) {
						return;
					}
					
					if(extendsConsoleWidget(targetType.getBaseType())) {						
						return;
					}
				} else if (targetType.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
					ITypeBinding elementType = targetType.getBaseType();
					if (elementType.isReference()) {
						ITypeBinding baseType = elementType.getBaseType();
						if (AbstractBinder.typeIs(baseType, EGLUICONSOLE, "CONSOLEFIELD"))
							return;
					}
				} else if (targetType.isDynamicallyAccessible())
					return;

				// If it is not one of the above, output an error
				problemRequestor.acceptProblem(target.getOffset(), target.getOffset() + target.getLength(), 
						IMarker.SEVERITY_ERROR, 
						IProblemRequestor.OPENUI_TARGETTYPE,
						new String[] {target.getCanonicalString()});	
			}
		}

		private void validateMultipleOpenTargets() {

			for (Iterator iter = openUIStatement.getOpenableElements().iterator(); iter.hasNext();) {
				Expression target = (Expression) iter.next();
				ITypeBinding targetType = target.resolveTypeBinding();
				isNewExpression = false;
				
				target.accept(new DefaultASTVisitor() {
					
					public boolean visit(NewExpression newExpression) {
						isNewExpression = true;
						return false;
					}
				});

				if (StatementValidator.isValidBinding(targetType)) {
					if (targetType.isReference()
							&& AbstractBinder.typeIs(targetType.getBaseType(), EGLUICONSOLE, "CONSOLEFIELD")
							&& !isNewExpression)
						continue;

					if (targetType.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
						ITypeBinding elementType = targetType.getBaseType();
						if (elementType.isReference()) {
							ITypeBinding baseType = elementType.getBaseType();
							if (AbstractBinder.typeIs(baseType, EGLUICONSOLE, "CONSOLEFIELD"))
								continue;
						}
					}
					
					if (targetType.isDynamicallyAccessible())
						continue;
					
					if(extendsConsoleWidget(targetType.getBaseType())) {						
						continue;
					}
				}
				problemRequestor.acceptProblem(target.getOffset(), target.getOffset() + target.getLength(), 
						IMarker.SEVERITY_ERROR, 
						IProblemRequestor.OPENUI_MUST_BE_CONSOLEFIELD,
						new String[] {target.getCanonicalString()});	
			}
		}
	}
	
	 private class EventBlockValidator {
		    private void validateEventBlocks() {
		    	openUIStatement.accept(new AbstractASTVisitor(){
					  public boolean visit(OnEventBlock onEventBlock) {
						  validateEventBlock(onEventBlock);
						  return false;
					  }
				  });
		    }
		    
		    private void validateEventBlock(OnEventBlock eventBlock) {
		    	IDataBinding dBinding = eventBlock.getEventTypeExpr().resolveDataBinding();
		    	if(Binding.isValidBinding(dBinding)) {
		    		boolean isValid = false;
		    		switch(dBinding.getKind()) {
		    			case IDataBinding.ENUMERATION_BINDING:
		    				if(InternUtil.intern("ConsoleEventKind") == dBinding.getDeclaringPart().getName()) {
		    					isValid = true;		    				
			    				if(InternUtil.intern("AFTER_DELETE") == dBinding.getName()
			    						|| InternUtil.intern("AFTER_OPENUI") == dBinding.getName()
			    		        		|| InternUtil.intern("AFTER_INSERT") == dBinding.getName()
			    		        		|| InternUtil.intern("AFTER_ROW") == dBinding.getName()
			    		        		|| InternUtil.intern("BEFORE_DELETE") == dBinding.getName()
			    		        		|| InternUtil.intern("BEFORE_OPENUI") == dBinding.getName()
			    		        		|| InternUtil.intern("BEFORE_INSERT") == dBinding.getName()
			    		        		|| InternUtil.intern("BEFORE_ROW") == dBinding.getName()) {
			    		        	validateNoStringEventBlock(eventBlock);
			    		        } else if (InternUtil.intern("AFTER_FIELD") == dBinding.getName() ||
			    		        		   InternUtil.intern("BEFORE_FIELD") == dBinding.getName()) {
			    		        	validateFieldListEventBlock(eventBlock);
			    		        } else if (InternUtil.intern("ON_KEY") == dBinding.getName()) {
			    		        	validateKeyListEventBlock(eventBlock);
			    		        } else if (InternUtil.intern("MENU_ACTION") == dBinding.getName()) {
			    		        	validateMenuActionListEventBlock(eventBlock);
			    		        }
		    				}
		    				break;
		    			case IDataBinding.CLASS_FIELD_BINDING:
		    				isValid = PrimitiveTypeBinding.getInstance(Primitive.INT) == dBinding.getType() &&
		    				          extendsConsoleWidget(dBinding.getDeclaringPart());
		    				break; 
		    		}
		    		
		    		if(!isValid) {
	    				problemRequestor.acceptProblem(eventBlock.getOffset(), eventBlock.getOffset() + eventBlock.getLength(), 
							IMarker.SEVERITY_ERROR, 
							IProblemRequestor.OPENUI_EVENTTYPE_INVALID,
							new String[] {eventBlock.getEventTypeExpr().getCanonicalString()});
		    		}
		    	}
			}
		   
		    private void validateNoStringEventBlock(OnEventBlock eventBlock) {
				if(eventBlock.getStringList().size() > 0) {
					int size = eventBlock.getStringList().size();
					problemRequestor.acceptProblem(((Expression) eventBlock.getStringList().get(0)).getOffset(), 
							((Expression) eventBlock.getStringList().get(size - 1)).getOffset() +
							((Expression) eventBlock.getStringList().get(size - 1)).getLength(), 
							IMarker.SEVERITY_ERROR, 
							IProblemRequestor.OPENUI_EVENTARG_NOT_ALLOWED);
		        }
			}	
		    
			private void validateHasStringEventBlock(OnEventBlock eventBlock) {
				 if(eventBlock.getStringList().size() == 0) {
					 problemRequestor.acceptProblem(eventBlock.getOffset(), eventBlock.getOffset() + eventBlock.getLength(), 
								IMarker.SEVERITY_ERROR, 
								IProblemRequestor.OPENUI_EVENTARG_REQUIRED);
				 }		
			}
			
			private void validateFieldListEventBlock(OnEventBlock eventBlock) {
				validateHasStringEventBlock(eventBlock);
			}
		    
			private void validateKeyListEventBlock(OnEventBlock eventBlock) {
				validateHasStringEventBlock(eventBlock);
			}
			
			private void validateMenuActionListEventBlock(OnEventBlock eventBlock) {
				validateHasStringEventBlock(eventBlock);
			}
	    } 
	 
	    private class BindClauseValidator {

		private void validateBindClause() {
			validateBindClauseApplicable();
			validateBindTargetCount();
			validateBindClauseTargets();
		}

		private void validateBindClauseApplicable() {

			if (openUIStatement.getBindClauseVariables().size() == 0)
				return;

			if (StatementValidator.isValidBinding(targetType)) {
				if (targetType.isReference()) {
					ITypeBinding elementType = targetType.getBaseType();
					if (AbstractBinder.typeIs(elementType, EGLUICONSOLE, "PROMPT"))
						return;
					if (AbstractBinder.typeIs(elementType, EGLUICONSOLE, "CONSOLEFIELD"))
						return;
				}

				if (targetType.isDynamicallyAccessible())
					return;
				
				if(extendsConsoleWidget(targetType.getBaseType())) {						
					return;
				}

				if (targetType.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING
						|| targetType.getKind() == ITypeBinding.FIXED_RECORD_BINDING) {
					if (targetType.getAnnotation(EGLUICONSOLE, "ConsoleForm") != null)
						return;
				}
				int size = openUIStatement.getBindClauseVariables().size();
				problemRequestor.acceptProblem(((Expression) openUIStatement.getBindClauseVariables().get(0)).getOffset(), 
						((Expression) openUIStatement.getBindClauseVariables().get(size - 1)).getOffset() +
						((Expression) openUIStatement.getBindClauseVariables().get(size - 1)).getLength(), 
						IMarker.SEVERITY_ERROR, 
						IProblemRequestor.OPENUI_BIND_NOT_ALLOWED);
			}
		}

		private void validateBindTargetCount() {

			int bindTargetCount = openUIStatement.getBindClauseVariables().size();
			int openTargetCount = openUIStatement.getOpenableElements().size();
			List bindClauseVariables = openUIStatement.getBindClauseVariables();
			if (openTargetCount > 1) {
				if (openTargetCount < bindTargetCount) {
					problemRequestor.acceptProblem(
						((Expression) bindClauseVariables.get(0)).getOffset(), 
						((Expression) bindClauseVariables.get(bindTargetCount - 1)).getOffset() +
						((Expression) bindClauseVariables.get(bindTargetCount - 1)).getLength(), 
						IMarker.SEVERITY_ERROR, 
						IProblemRequestor.OPENUI_BIND_TOO_MANY);
				}
			}
			
			IAnnotationBinding aBinding = openUIStatementBinding.getAnnotation(EGLUICONSOLE, "IsConstruct");
			if(aBinding != null && Boolean.YES == aBinding.getValue()) {
				if (bindTargetCount != 1) {
					if(openUIStatement.hasBindClause()) {
						int startOffset = ((Expression) bindClauseVariables.get(0)).getOffset();
						int endOffset = ((Expression) bindClauseVariables.get(bindClauseVariables.size()-1)).getOffset() +
						                ((Expression) bindClauseVariables.get(bindClauseVariables.size()-1)).getLength();
						problemRequestor.acceptProblem(
								startOffset, endOffset,
								((Expression) bindClauseVariables.get(0)).getOffset() +
								
							IMarker.SEVERITY_ERROR,
							IProblemRequestor.OPENUI_BIND_EXACTLY_ONE);
					}
					else {
						problemRequestor.acceptProblem(
							openUIStatement.getOpenAttributes().getSetting(InternUtil.intern("IsConstruct")),
							IProblemRequestor.OPENUI_BIND_EXACTLY_ONE);
					}
				}
			}
							
		}

		private void validateBindClauseTargets() {

			for (Iterator iter = openUIStatement.getBindClauseVariables().iterator(); iter.hasNext();) {
				Expression target = (Expression) iter.next();
				ITypeBinding targetTypeBinding = target.resolveTypeBinding();
				IDataBinding targetDataBinding = target.resolveDataBinding();
				isLiteralExpression = false;
				
				if (StatementValidator.isValidBinding(targetDataBinding)) {
						
					IAnnotationBinding aBinding = openUIStatementBinding.getAnnotation(EGLUICONSOLE, "IsConstruct");
					if (aBinding != null && Boolean.YES == aBinding.getValue()) {
						if (!isWritableDataBinding(targetDataBinding)
								|| !isStringTypeBinding(targetDataBinding.getType())) {
							problemRequestor.acceptProblem(target.getOffset(), target.getOffset() + target.getLength(), 
									IMarker.SEVERITY_ERROR, 
									IProblemRequestor.OPENUI_BIND_MUST_BE_TEXT);	
						}
					} else {
						aBinding = openUIStatementBinding.getAnnotation(EGLUICONSOLE, "DisplayOnly");
						if (aBinding == null || Boolean.NO == aBinding.getValue()) {
							if (!isWritableDataBinding(targetDataBinding)) {
								problemRequestor.acceptProblem(target.getOffset(), target.getOffset() + target.getLength(), 
										IMarker.SEVERITY_ERROR, 
										IProblemRequestor.OPENUI_BIND_READ_ONLY);	
							}
						}
					}
				} else if (StatementValidator.isValidBinding(targetTypeBinding)){
					
					// Bind target is not writable, so error if isConstruct = yes, or isDisplayOnly = no
					IAnnotationBinding aBinding = openUIStatementBinding.getAnnotation(EGLUICONSOLE, "IsConstruct");
					if (aBinding != null && Boolean.YES == aBinding.getValue()) {
						problemRequestor.acceptProblem(target.getOffset(), target.getOffset() + target.getLength(), 
								IMarker.SEVERITY_ERROR, 
								IProblemRequestor.OPENUI_BIND_MUST_BE_TEXT);	
					} else {
						aBinding = openUIStatementBinding.getAnnotation(EGLUICONSOLE, "DisplayOnly");
						if (aBinding == null || Boolean.NO == aBinding.getValue()) {
							// Allow literals only in this case
							target.accept(new AbstractASTExpressionVisitor(){
								
								public boolean visitLiteral(LiteralExpression literal) {
							        isLiteralExpression = true;
									return false;
							    }
							});	
							if (!isLiteralExpression) {
								problemRequestor.acceptProblem(target.getOffset(), target.getOffset() + target.getLength(), 
										IMarker.SEVERITY_ERROR, 
										IProblemRequestor.OPENUI_BIND_READ_ONLY);	
							}
						}
					}
				}
				else {
					continue;
				}
			}
		}

		private boolean isStringTypeBinding(ITypeBinding typeBinding) {
			boolean isString = false;
			if (typeBinding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING) {
				Primitive primitiveType = ((PrimitiveTypeBinding) typeBinding).getPrimitive();
				
				switch (primitiveType.getType()) {
				case Primitive.CHAR_PRIMITIVE:
					isString = true;
					break;
				case Primitive.DBCHAR_PRIMITIVE:
					isString = true;
					break;
				case Primitive.HEX_PRIMITIVE:
					isString = true;
					break;
				case Primitive.MBCHAR_PRIMITIVE:
					isString = true;
					break;
				case Primitive.UNICODE_PRIMITIVE:
					isString = true;
					break;
				case Primitive.STRING_PRIMITIVE:
					isString = true;
					break;
				}	
			}

			return isString;
		}

		private boolean isWritableDataBinding(IDataBinding dataBinding) {
			if (dataBinding instanceof VariableBinding) {
				return !((VariableBinding) dataBinding).isConstant();
			}
			return true;
		}
	}
	    
	private void validateExpressionAttributes() {
		if(openUIStatement.hasOpenAttributes()) {
			openUIStatement.getOpenAttributes().accept(new DefaultASTVisitor() {
				public boolean visit(SettingsBlock settingsBlock) {
					return true;
				}
				
				public boolean visit(Assignment assignment) {
					IAnnotationBinding aBinding = assignment.resolveBinding();
					if(aBinding != null && IBinding.NOT_FOUND_BINDING != aBinding) {
						IAnnotationTypeBinding aTypeBinding = (IAnnotationTypeBinding) aBinding.getType();
						if(aTypeBinding.takesExpressionInOpenUIStatement()) {
							Expression valueExpr = (Expression) aBinding.getValue();
							ITypeBinding valueType = valueExpr.resolveTypeBinding();
							if(valueType != null) {
								if(!TypeCompatibilityUtil.isMoveCompatible(aTypeBinding.getSingleValueType(), valueType, valueExpr, compilerOptions)) {
									problemRequestor.acceptProblem(
										assignment.getRightHandSide(),
										IProblemRequestor.ASSIGNMENT_STATEMENT_TYPE_MISMATCH,
										new String[] {
											StatementValidator.getShortTypeString(aTypeBinding.getSingleValueType()),
											StatementValidator.getShortTypeString(valueType),											
											aTypeBinding.getCaseSensitiveName() + " = " + valueExpr.getCanonicalString()
										});
								}
							}
						}
					}
					return false;
				}
			});
		}
	}
	
	private boolean extendsConsoleWidget(ITypeBinding tBinding) {
		if(ITypeBinding.EXTERNALTYPE_BINDING == tBinding.getKind()) {
			for(Iterator iter = ((ExternalTypeBinding) tBinding).getExtendedTypes().iterator(); iter.hasNext();) {
				if(AbstractBinder.typeIs((ITypeBinding) iter.next(), EGLUICONSOLE, "CONSOLEWIDGET")) {
					return true;
				}
			}
		}
		return false;
	}
}
