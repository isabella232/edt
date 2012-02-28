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
	
	import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.FormFieldBinding;
import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ParenthesizedExpression;
import org.eclipse.edt.compiler.core.ast.SetStatement;
import org.eclipse.edt.compiler.core.ast.SubstringAccess;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;

	
	/**
	 * @author Craig Duval
	 */
	public class SetStatementValidator extends DefaultASTVisitor implements IOStatementValidatorConstants{
		
		private static final String POSITION_INTERN				= InternUtil.intern(IEGLConstants.MNEMONIC_POSITION);
		private static final String ALARM_INTERN				= InternUtil.intern(IEGLConstants.MNEMONIC_ALARM);
		private static final String CURSOR_INTERN				= InternUtil.intern(IEGLConstants.MNEMONIC_CURSOR);
		private static final String FULL_INTERN					= InternUtil.intern(IEGLConstants.MNEMONIC_FULL);
		private static final String NOHIGHLIGHT_INTERN			= InternUtil.intern(IEGLConstants.MNEMONIC_NOHIGHLIGHT);
		private static final String BLINK_INTERN				= InternUtil.intern(IEGLConstants.MNEMONIC_BLINK);
		private static final String REVERSE_INTERN				= InternUtil.intern(IEGLConstants.MNEMONIC_REVERSE);
		private static final String UNDERLINE_INTERN			= InternUtil.intern(IEGLConstants.MNEMONIC_UNDERLINE);
		private static final String MODIFIED_INTERN				= InternUtil.intern(IEGLConstants.MNEMONIC_MODIFIED);
		private static final String DEFAULTCOLOR_INTERN			= InternUtil.intern(IEGLConstants.MNEMONIC_DEFAULTCOLOR);
		private static final String BLACK_INTERN				= InternUtil.intern(IEGLConstants.MNEMONIC_BLACK);
		private static final String BLUE_INTERN					= InternUtil.intern(IEGLConstants.MNEMONIC_BLUE);
		private static final String GREEN_INTERN				= InternUtil.intern(IEGLConstants.MNEMONIC_GREEN);
		private static final String MAGENTA_INTERN				= InternUtil.intern(IEGLConstants.MNEMONIC_MAGENTA);
		private static final String RED_INTERN					= InternUtil.intern(IEGLConstants.MNEMONIC_RED);
		private static final String CYAN_INTERN					= InternUtil.intern(IEGLConstants.MNEMONIC_CYAN);
		private static final String YELLOW_INTERN				= InternUtil.intern(IEGLConstants.MNEMONIC_YELLOW);
		private static final String WHITE_INTERN				= InternUtil.intern(IEGLConstants.MNEMONIC_WHITE);
		private static final String BOLD_INTERN					= InternUtil.intern(IEGLConstants.MNEMONIC_BOLD);
		private static final String DIM_INTERN					= InternUtil.intern(IEGLConstants.MNEMONIC_DIM);
		private static final String MASKED_INTERN				= InternUtil.intern(IEGLConstants.MNEMONIC_MASKED);
		private static final String INVISIBLE_INTERN			= InternUtil.intern(IEGLConstants.MNEMONIC_INVISIBLE);
		private static final String NORMALINTENSITY_INTERN		= InternUtil.intern(IEGLConstants.MNEMONIC_NORMALINTENSITY);
		private static final String PROTECT_INTERN				= InternUtil.intern(IEGLConstants.MNEMONIC_PROTECT);
		private static final String SKIP_INTERN					= InternUtil.intern(IEGLConstants.MNEMONIC_SKIP);
		private static final String UNPROTECT_INTERN			= InternUtil.intern(IEGLConstants.MNEMONIC_UNPROTECT);
		private static final String NORMAL_INTERN				= InternUtil.intern(IEGLConstants.MNEMONIC_NORMAL);
		private static final String INITIALATTRIBUTES_INTERN	= InternUtil.intern(IEGLConstants.MNEMONIC_INITIALATTRIBUTES);
		private static final String INITIAL_INTERN				= InternUtil.intern(IEGLConstants.MNEMONIC_INITIAL);
		private static final String EMPTY_INTERN				= InternUtil.intern(IEGLConstants.MNEMONIC_EMPTY);
		
		protected static final String[] VALID_FOR_PRINTFORM = InternUtil.intern(new String[] {
			INITIAL_INTERN,
			INITIALATTRIBUTES_INTERN,
			EMPTY_INTERN });
		
		protected static final String[] VALID_FOR_TEXTFORM = InternUtil.intern(new String[] {
			ALARM_INTERN,
			INITIAL_INTERN,
			INITIALATTRIBUTES_INTERN,
			EMPTY_INTERN });
		
		protected static final String[] VALID_FOR_FORMFIELD = InternUtil.intern(new String[] {
			CURSOR_INTERN,
			FULL_INTERN,
			EMPTY_INTERN,
			NORMAL_INTERN,
			INITIALATTRIBUTES_INTERN,
			INITIAL_INTERN,
			MODIFIED_INTERN,
			DEFAULTCOLOR_INTERN,
			BLACK_INTERN,
			BLUE_INTERN,
			GREEN_INTERN,
			MAGENTA_INTERN,
			RED_INTERN,
			CYAN_INTERN,
			YELLOW_INTERN,
			WHITE_INTERN,
			BLINK_INTERN,
			REVERSE_INTERN,
			UNDERLINE_INTERN,
			NOHIGHLIGHT_INTERN,
			BOLD_INTERN,
			DIM_INTERN,
			MASKED_INTERN,
			INVISIBLE_INTERN,
			NORMALINTENSITY_INTERN,
			PROTECT_INTERN,
			SKIP_INTERN,
			UNPROTECT_INTERN });
		
		private String sColor;// defaultColor, black, blue, green, magenta, red, cyan, yellow, white
		private String sIntensity; // bold, invisible, normalIntensity, dim, masked 
		private String sProtection; // protect, skip, unprotect
		// note that initial is in both of these categories
		private String sDefaultTextFieldState; // normal, initial, initialAttributes
		private String sEmptyOrInitial; // empty, initial

		private String sNoHighlightState; //nohighlight
		//can't have nohighlight with any of the following three
		private String sBlinkState; // blink
		private String sReverseState; // reverse
		private String sUnderlineState; // underline  

		// can't have any defaultTextFieldState with 
		// any of: modified, color, intensity, protection, nohighlight, blink, reverse, underline 
		private String sModifiedState; //modified

		// no rules on these except can't duplicate 
		private boolean positionState; // position
		private boolean alarmState; // alarm
		private boolean cursorState; // cursor
		private boolean fullState; // full
		
		private IProblemRequestor problemRequestor;
		
		private boolean enclosingPartIsPageHandler = false;
        private ICompilerOptions compilerOptions;
		
		public SetStatementValidator(IProblemRequestor problemRequestor, IPartBinding enclosingPart, ICompilerOptions compilerOptions) {
			this.problemRequestor = problemRequestor;
			this.compilerOptions = compilerOptions;
			if(enclosingPart != null) {
				enclosingPartIsPageHandler = enclosingPart.getAnnotation(EGLUIJSF, "JSFHandler") != null;
			}
		}
		
		public boolean visit(final SetStatement setStatement) {
			checkForDuplicateStates(setStatement);
			checkForConflictingStates(setStatement);
			if(!checkNoSubstringAsTarget(setStatement)) {
				return false;
			}
			validateReferenceAndStates(setStatement);			
			return false;
		}
	
		protected void checkForDuplicateStates(SetStatement setStmt){
			Iterator iterator = setStmt.getStates().iterator();
			
			while (iterator.hasNext()) {
				String result = InternUtil.intern((String) iterator.next());

				if (result == POSITION_INTERN) {
					if (!positionState) {
						positionState = true;
					} else { //duplicate
						problemRequestor.acceptProblem(setStmt,
								IProblemRequestor.DUPE_STATE_ON_SET,
								new String[] { IEGLConstants.MNEMONIC_POSITION.toUpperCase()});						
					}
				} else if (result == ALARM_INTERN) {
					if (!alarmState) {
						alarmState = true;
					} else { //duplicate
						problemRequestor.acceptProblem(setStmt,
								IProblemRequestor.DUPE_STATE_ON_SET,
								new String[] { IEGLConstants.MNEMONIC_ALARM.toUpperCase()});			
					}
				} else if (result == CURSOR_INTERN) {
					if (!cursorState ) {
						cursorState = true;
					} else { //duplicate
						problemRequestor.acceptProblem(setStmt,
								IProblemRequestor.DUPE_STATE_ON_SET,
								new String[] { IEGLConstants.MNEMONIC_CURSOR.toUpperCase()});
					}
				} else if (result == FULL_INTERN) {
					if (!fullState) {
						fullState = true;
					} else { //duplicate
						problemRequestor.acceptProblem(setStmt,
								IProblemRequestor.DUPE_STATE_ON_SET,
								new String[] { IEGLConstants.MNEMONIC_FULL.toUpperCase()});
					}
				} else if (result == NOHIGHLIGHT_INTERN) {
					if (sNoHighlightState == null) {
						sNoHighlightState = result.toUpperCase();
					} else { //duplicate
						problemRequestor.acceptProblem(setStmt,
								IProblemRequestor.DUPE_STATE_ON_SET,
								new String[] { IEGLConstants.MNEMONIC_NOHIGHLIGHT.toUpperCase()});
					}
				} else if (result == BLINK_INTERN) {
					if (sBlinkState == null) {
						sBlinkState = result.toUpperCase();
					} else { //duplicate
						problemRequestor.acceptProblem(setStmt,
								IProblemRequestor.DUPE_STATE_ON_SET,
								new String[] { IEGLConstants.MNEMONIC_BLINK.toUpperCase()});						
					}
				} else if (result == REVERSE_INTERN) {
					if (sReverseState == null) {
						sReverseState = result.toUpperCase();
					} else { //duplicate
						problemRequestor.acceptProblem(setStmt,
								IProblemRequestor.DUPE_STATE_ON_SET,
								new String[] { IEGLConstants.MNEMONIC_REVERSE.toUpperCase()});		
					}
				} else if (result == UNDERLINE_INTERN) {
					if (sUnderlineState == null) {
						sUnderlineState = result.toUpperCase();
					} else { //duplicate
						problemRequestor.acceptProblem(setStmt,
								IProblemRequestor.DUPE_STATE_ON_SET,
								new String[] { IEGLConstants.MNEMONIC_UNDERLINE.toUpperCase()});
					}
				} else if (result == MODIFIED_INTERN) {
					if (sModifiedState == null) {
						sModifiedState = result.toUpperCase();
					} else { //duplicate
						problemRequestor.acceptProblem(setStmt,
								IProblemRequestor.DUPE_STATE_ON_SET,
								new String[] { IEGLConstants.MNEMONIC_MODIFIED.toUpperCase()});
					}
				} else if (
					result == DEFAULTCOLOR_INTERN
						|| result == BLACK_INTERN
						|| result == BLUE_INTERN
						|| result == GREEN_INTERN
						|| result == MAGENTA_INTERN
						|| result == RED_INTERN
						|| result == CYAN_INTERN
						|| result == YELLOW_INTERN
						|| result == WHITE_INTERN) {
					if (sColor == null) {
						sColor = result.toUpperCase();
					} else { //multiple colors
						problemRequestor.acceptProblem(setStmt,
								IProblemRequestor.MULTIPLE_COLORS_ON_SET,
								new String[] { sColor,result.toUpperCase()});
					}
				} else if (
						result == BOLD_INTERN
						|| result == DIM_INTERN
						|| result == MASKED_INTERN
						|| result == INVISIBLE_INTERN
						|| result == NORMALINTENSITY_INTERN) {
					if (sIntensity == null) {
						sIntensity = result.toUpperCase();
					} else { //multiple intensity states
						problemRequestor.acceptProblem(setStmt,
								IProblemRequestor.MULTIPLE_INTENSITY_ON_SET,
								new String[] { sIntensity,result.toUpperCase()});
					}
				} else if (
						result == PROTECT_INTERN
						|| result == SKIP_INTERN
						|| result == UNPROTECT_INTERN) {
					if (sProtection == null) {
						sProtection = result.toUpperCase();
					} else { //multiple protection states
						problemRequestor.acceptProblem(setStmt,
								IProblemRequestor.MULTIPLE_PROTECTION_ON_SET,
								new String[] { sProtection,result.toUpperCase()});
					}
				} else if (
						result == NORMAL_INTERN
						|| result == INITIALATTRIBUTES_INTERN
						|| result == INITIAL_INTERN) {
					if (sDefaultTextFieldState == null) {
						sDefaultTextFieldState = result.toUpperCase();
					} else { //multiple text field states
						problemRequestor.acceptProblem(setStmt,
								IProblemRequestor.MULTIPLE_TEXT_FIELD_STATE_ON_SET,
								new String[] { sDefaultTextFieldState,result.toUpperCase()});
					}
					if (result == INITIAL_INTERN) {
						if (sEmptyOrInitial == null) {
							sEmptyOrInitial = result.toUpperCase();
						} else { //multiple empty or initial states
							problemRequestor.acceptProblem(setStmt,
									IProblemRequestor.EMPTY_AND_INITIAL_ON_SET,
									new String[] { sEmptyOrInitial,result.toUpperCase()});
						}
					}
				} else if (result == EMPTY_INTERN) {
					if (sEmptyOrInitial == null) {
						sEmptyOrInitial = result.toUpperCase();
					} else { //multiple empty or initial states
						problemRequestor.acceptProblem(setStmt,
								IProblemRequestor.EMPTY_AND_INITIAL_ON_SET,
								new String[] { sEmptyOrInitial,result.toUpperCase()});
					}
				} else {
					problemRequestor.acceptProblem(setStmt,
							IProblemRequestor.UNSUPPORTED_STATE_ON_SET,
							new String[] { result.toUpperCase()});

				}
			} // end while loop
		}
	

		protected void checkForConflictingStates(SetStatement setStmt ) {
			if (sNoHighlightState != null && sBlinkState != null) { //multiple highlighting states
				problemRequestor.acceptProblem(setStmt,
						IProblemRequestor.MULTIPLE_HIGHLIGHT_STATE_ON_SET,
						new String[] {sNoHighlightState, sBlinkState});
			}
			if (sNoHighlightState != null && sReverseState != null) { //multiple highlighting states
				problemRequestor.acceptProblem(setStmt,
						IProblemRequestor.MULTIPLE_HIGHLIGHT_STATE_ON_SET,
						new String[] {sNoHighlightState, sReverseState});
			}
			if (sNoHighlightState != null && sUnderlineState != null) { //multiple highlighting states
				problemRequestor.acceptProblem(setStmt,
						IProblemRequestor.MULTIPLE_HIGHLIGHT_STATE_ON_SET,
						new String[] {sNoHighlightState, sUnderlineState});
			}

			// can't have any defaultTextFieldState with 
			// any of: modified, color, intensity, protection, nohighlight, blink, reverse, underline 
			if (sDefaultTextFieldState != null) {
				if (sModifiedState != null) {
					problemRequestor.acceptProblem(setStmt,
							IProblemRequestor.TEXT_FIELD_STATES_WITH_OTHERS_ON_SET,
							new String[] {sDefaultTextFieldState, sModifiedState});
				}
	
				if (sColor != null) {
					problemRequestor.acceptProblem(setStmt,
							IProblemRequestor.TEXT_FIELD_STATES_WITH_OTHERS_ON_SET,
							new String[] {sDefaultTextFieldState, sColor});
				}
				if (sIntensity != null) {
					problemRequestor.acceptProblem(setStmt,
							IProblemRequestor.TEXT_FIELD_STATES_WITH_OTHERS_ON_SET,
							new String[] {sDefaultTextFieldState, sIntensity});
				}
				if (sProtection != null) {
					problemRequestor.acceptProblem(setStmt,
							IProblemRequestor.TEXT_FIELD_STATES_WITH_OTHERS_ON_SET,
							new String[] {sDefaultTextFieldState, sProtection});
				}
				if (sNoHighlightState != null) {
					problemRequestor.acceptProblem(setStmt,
							IProblemRequestor.TEXT_FIELD_STATES_WITH_OTHERS_ON_SET,
							new String[] {sDefaultTextFieldState, sNoHighlightState});
				}
				
				if (sBlinkState != null) {
					problemRequestor.acceptProblem(setStmt,
							IProblemRequestor.TEXT_FIELD_STATES_WITH_OTHERS_ON_SET,
							new String[] {sDefaultTextFieldState, sBlinkState});
				}
				if (sReverseState != null) {
					problemRequestor.acceptProblem(setStmt,
							IProblemRequestor.TEXT_FIELD_STATES_WITH_OTHERS_ON_SET,
							new String[] {sDefaultTextFieldState, sReverseState});
				}
				if (sUnderlineState != null) {
					problemRequestor.acceptProblem(setStmt,
							IProblemRequestor.TEXT_FIELD_STATES_WITH_OTHERS_ON_SET,
							new String[] {sDefaultTextFieldState, sUnderlineState});

				}
			}		
		}
		
		public void validateReferenceAndStates(final SetStatement setStmt) {

			setStmt.accept(new AbstractASTExpressionVisitor(){
				public boolean visitExpression(final Expression expression) {
					IDataBinding dataBinding = expression.resolveDataBinding();
					if (!StatementValidator.isValidBinding(dataBinding)){
						return false;
						
					}
					
					if (dataBinding.getKind() != IDataBinding.CLASS_FIELD_BINDING
							&& dataBinding.getKind() != IDataBinding.FUNCTION_PARAMETER_BINDING
							&& dataBinding.getKind() != IDataBinding.LOCAL_VARIABLE_BINDING
							&& dataBinding.getKind() != IDataBinding.PROGRAM_PARAMETER_BINDING
							&& dataBinding.getKind() != IDataBinding.FORM_BINDING
							&& dataBinding.getKind() != IDataBinding.STRUCTURE_ITEM_BINDING
							&& dataBinding.getKind() != IDataBinding.DATATABLE_BINDING// !setTarget.isTableColumn()
							&& dataBinding.getKind() != IDataBinding.FORM_FIELD) {
						//TODO complete
								//FRIEDA why isn't there a message here? saying invalid target for set statement
						return false;
					}else {
						ITypeBinding typeBinding = expression.resolveTypeBinding();
						if (!StatementValidator.isValidBinding(typeBinding)){
							return false;
						}
						
						if (typeBinding.isDynamic()){
							
						}else if ( StatementValidator.isRecordOrRecordArray(typeBinding) ) {
							if( typeBinding.getAnnotation(EGLIODLI, "PSBRecord") != null) {
								problemRequestor.acceptProblem(expression,
										IProblemRequestor.DLI_PSBRECORD_NOT_VALID_AS_STATEMENT_OPERAND,
										new String[] { IEGLConstants.KEYWORD_SET });
							}
							else {
								List validStates = new ArrayList();
								validStates.add( EMPTY_INTERN );
								validStates.add( INITIAL_INTERN );
								validStates.add( POSITION_INTERN );
								
								int  iErr = -1;
								final boolean[] isIndexedOrDLI = new boolean[] {false};
								if( typeBinding.getAnnotation(EGLIOFILE, "IndexedRecord") != null ||
									typeBinding.getAnnotation(EGLIODLI, "DLISegment") != null) {									
									iErr = IProblemRequestor.INVALID_SET_STATE_FOR_INDEXED_RECORD;
									isIndexedOrDLI[0] = true;
								}
								else {
									iErr = IProblemRequestor.INVALID_SET_STATE_FOR_RECORD;
								}
								
								String[] toArray = (String[]) validStates.toArray( new String[0] );
								validateStates( setStmt,expression, toArray, iErr, POSITION_INTERN, new IOnStateDoThis() {
									public void doIt() {
										if(!isIndexedOrDLI[0]) {
											problemRequestor.acceptProblem(expression, IProblemRequestor.SET_POSITION_STATEMENT_WITH_INVALID_DATAREF, new String[] {expression.getCanonicalString()});
										}
									}
								});
							}
						}else if (typeBinding.getKind() ==  ITypeBinding.FORM_BINDING) {
							if (typeBinding.getAnnotation(EGLUITEXT, "PrintForm") != null) {
								// print form - initial, initialAttributes, empty
								validateStates( setStmt,expression,
									VALID_FOR_PRINTFORM,
									IProblemRequestor.INVALID_SET_STATE_FOR_PRINT_FORM);
							} else {
								// text form - alarm, initial, initialAttributes, empty
								validateStates( setStmt,expression,
									VALID_FOR_TEXTFORM,
									IProblemRequestor.INVALID_SET_STATE_FOR_TEXT_FORM);
							}
						}else {
							if ( typeBinding.getKind() != ITypeBinding.ARRAY_TYPE_BINDING &&
									((dataBinding.getKind() == IDataBinding.FORM_FIELD && ((FormFieldBinding)dataBinding).isTextFormField())
									|| (dataBinding.getKind() == IDataBinding.FUNCTION_PARAMETER_BINDING) && ((FunctionParameterBinding )dataBinding).isField())) {
									// text form field - cursor, full, empty, normal, initialAtributes, initial,
									//                   modified, defaultcolor, blue, green, magenta, red, cyan,
									//                   yellow, white, blink, reverse, underline, nohighlight, bold,
									//                   invisible, normalIntensity, protect, skip, unprotect.
									validateStates( setStmt,expression,VALID_FOR_FORMFIELD,IProblemRequestor.INVALID_SET_STATE_FOR_TEXT_FIELD);

								} else if (isSQLItem(dataBinding) ||
											(dataBinding.getKind() == IDataBinding.FUNCTION_PARAMETER_BINDING && ((FunctionParameterBinding )dataBinding).isSQLNullable())) {
									// SQL item - null, plus empty and initial
									List validStates = new ArrayList();
										if( enclosingPartIsPageHandler ) {
										validStates.add( EMPTY_INTERN );
										validStates.add( INITIAL_INTERN );
									}
									
									validateStates(setStmt,expression,
										validStates.isEmpty() ? new String[] { null } : (String[]) validStates.toArray( new String[0] ),
										IProblemRequestor.INVALID_SET_STATE_FOR_SQL_ITEM);
								} else {
									if ( (dataBinding.getKind() == IDataBinding.LOCAL_VARIABLE_BINDING || dataBinding.getKind() == IDataBinding.CLASS_FIELD_BINDING ) &&
										 enclosingPartIsPageHandler ) {
										validateStates(setStmt,expression,
											new String[] { EMPTY_INTERN,
											               INITIAL_INTERN},
											IProblemRequestor.INVALID_SET_STATE_FOR_ITEM);
									}
									else 
									{
										if( dataBinding.getKind() == IDataBinding.STRUCTURE_ITEM_BINDING ) {
											// The target is not an SQLRecord item, or we wouldn't be here
											// Allow empty if item has substructure
											String[] validStates = ((StructureItemBinding)dataBinding).getChildren().size() == 0 ?
												new String[] {null} :
												new String[] {EMPTY_INTERN};
											validateStates( setStmt,expression,
												validStates,
												IProblemRequestor.INVALID_SET_STATE_FOR_ITEM);
										}
										else {
											validateStates( setStmt,expression,
												new String[0],
												IProblemRequestor.INVALID_SET_STATE_FOR_ITEM);
										}
									}
								}
							}
						}
					return false;
					
				}//end visit
			});
	
		}
		
		private boolean checkNoSubstringAsTarget(final SetStatement setStmt) {
			final boolean[] result = new boolean[] {true}; 
			for(Iterator iter = setStmt.getSetTargets().iterator(); iter.hasNext();) {
				((Expression) iter.next()).accept(new DefaultASTVisitor() {
					public boolean visit(ParenthesizedExpression parenthesizedExpression) {
						return true;
					}
					public boolean visit(final SubstringAccess substringAccess) {
						problemRequestor.acceptProblem(
							substringAccess,
							IProblemRequestor.SUBSTRING_EXPRESSION_IN_BAD_LOCATION);
						result[0] = false;						
						return false;
					}
				});
			}
			return result[0];
		}
		
		private static interface IOnStateDoThis {
			void doIt();
		}
		
		protected boolean validateStates(SetStatement setStmt,Expression expression,String[] internedValidStates, int iErr) {
			return validateStates(setStmt, expression, internedValidStates, iErr, null, null);
		}

		protected boolean validateStates(SetStatement setStmt,Expression expression,String[] internedValidStates, int iErr, String onState, IOnStateDoThis doThis) {
			boolean allValid = true;
			for (Iterator iter = setStmt.getStates().iterator(); iter.hasNext();) {
				String nextState = InternUtil.intern((String)iter.next());
				if (stateIsSupported(nextState) && !stringInArray(nextState, internedValidStates)) {
					allValid = false;
					problemRequestor.acceptProblem(expression,
						iErr,
						new String[]{nextState.toUpperCase()});
				}
				else if(nextState == onState) {
					doThis.doIt();
				}
			}
			return allValid;
		}
		
		private static boolean stringInArray(String string, String[] internedArray) {
			if (internedArray.length == 0 || internedArray[0] == null){
				return false;
			}
			for (int i = 0; i < internedArray.length; i++) {
				if (internedArray[i] == string) {				
					return true;
				}
			}
			return false;
		}
		
		private static boolean stateIsSupported(String state) {
			return state == POSITION_INTERN
				|| state == ALARM_INTERN
				|| state == CURSOR_INTERN
				|| state == FULL_INTERN
				|| state == NOHIGHLIGHT_INTERN
				|| state == BLINK_INTERN
				|| state == REVERSE_INTERN
				|| state == UNDERLINE_INTERN
				|| state == MODIFIED_INTERN
				|| state == DEFAULTCOLOR_INTERN
				|| state == BLACK_INTERN			
				|| state == BLUE_INTERN
				|| state == GREEN_INTERN
				|| state == MAGENTA_INTERN
				|| state == RED_INTERN
				|| state == CYAN_INTERN
				|| state == YELLOW_INTERN
				|| state == WHITE_INTERN
				|| state == BOLD_INTERN
				|| state == DIM_INTERN
				|| state == MASKED_INTERN
				|| state == INVISIBLE_INTERN
				|| state == NORMALINTENSITY_INTERN
				|| state == PROTECT_INTERN
				|| state == SKIP_INTERN
				|| state == UNPROTECT_INTERN
				|| state == NORMAL_INTERN
				|| state == INITIALATTRIBUTES_INTERN
				|| state == INITIAL_INTERN
				|| state == EMPTY_INTERN;
		}
		
		protected  boolean isSQLItem(IDataBinding dBinding) {
			IPartBinding pBinding = dBinding.getDeclaringPart();
			if (StatementValidator.isValidBinding(pBinding)) {
				if (pBinding.getAnnotation(EGLIOSQL, "SQLRecord") != null) {
					return true;
				}
			}
			return false;
		}		
	}
	
	


