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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.edt.compiler.binding.AnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.FixedStructureBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.BinaryExpression;
import org.eclipse.edt.compiler.core.ast.BooleanLiteral;
import org.eclipse.edt.compiler.core.ast.CharLiteral;
import org.eclipse.edt.compiler.core.ast.DBCharLiteral;
import org.eclipse.edt.compiler.core.ast.DataTable;
import org.eclipse.edt.compiler.core.ast.DecimalLiteral;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FloatLiteral;
import org.eclipse.edt.compiler.core.ast.HexLiteral;
import org.eclipse.edt.compiler.core.ast.IntegerLiteral;
import org.eclipse.edt.compiler.core.ast.MBCharLiteral;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.StringLiteral;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.core.ast.UnaryExpression;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Dave Murray
 */
public class DataTableValidator extends FixedStructureValidator {
	
	private class DefaultDataTableItemValidator extends DefaultStructureItemValidator {
		public DefaultDataTableItemValidator() {
			super();
		}
		
		public void validate(StructureItemInfo sItemInfo) {
			super.validate(sItemInfo);
			
			if(sItemInfo.binding.hasOccurs()) {
				problemRequestor.acceptProblem(
					sItemInfo.nodeForErrors,
					IProblemRequestor.TABLE_ITEM_HAS_OCCURS,
					new String[] {
						sItemInfo.canonicalName,
						canonicalStructureName
					});
			}
			
			if(sItemInfo.structureItemNode.hasInitializer()) {
				problemRequestor.acceptProblem(
					sItemInfo.structureItemNode.getInitializer(),
					IProblemRequestor.INITIALIZERS_NOT_ALLOWED_IN_DATA_TABLE);
			}
		}
	}
	
	private class DefaultDataTableItemValidatorFactory extends StructureItemValidatorFactory {
		StructureItemValidator createStructureItemValidator() {
			return new DefaultDataTableItemValidator();
		}
	}
	
	private abstract class ContentToPrimitiveTypeChecker {
		abstract boolean checkStringLiteralContent(String content, PrimitiveTypeBinding primTypeBinding, Node nodeForErrors);
		abstract boolean checkBooleanLiteralContent(String content, PrimitiveTypeBinding primTypeBinding, Node nodeForErrors);
		abstract boolean checkNumericLiteralContent(String content, PrimitiveTypeBinding primTypeBinding, Node nodeForErrors);
	}
	
	private abstract class ContentToNumericTypeChecker extends ContentToPrimitiveTypeChecker {
		boolean checkStringLiteralContent(String content, PrimitiveTypeBinding primTypeBinding, Node nodeForErrors) {
			int firstChar = 0;			
			char[] chars = content.toCharArray();
			boolean foundDecimal = false;
			boolean hasMultipleDecimals = false;
			Character invalidChar = null;
			
			if(content.startsWith("-")) {
				firstChar = 1;
			}
			
			for(int i = firstChar; i < chars.length; i++) {
				char ch = chars[i];
				if('.' == ch) {
					if(foundDecimal) {
						hasMultipleDecimals = true;
					}
					else {
						foundDecimal = true;
					}
				}
				else if(invalidChar == null && !Character.isDigit(ch)) {
					invalidChar = new Character(ch);
				}
			}
			
			if(hasMultipleDecimals) {
				problemRequestor.acceptProblem(
					nodeForErrors,
					IProblemRequestor.DATATABLE_CONTENT_TOO_MANY_DECIMALS,
					new String[] {
						content,
						canonicalStructureName
					});
				return false;
			}
			else {
				if(invalidChar != null) {
					problemRequestor.acceptProblem(
						nodeForErrors,
						IProblemRequestor.DATATABLE_CONTENT_HAS_INVALID_DIGITS,
						new String[] {
							content,
							canonicalStructureName,
							primTypeBinding.getPrimitive().getName(),
							invalidChar.toString()
						});
					return false;
				}
				else {
					return checkNumericLiteralContent(content, primTypeBinding, nodeForErrors);
				}
			}			
		}
		
		boolean checkBooleanLiteralContent(String content, PrimitiveTypeBinding primTypeBinding, Node nodeForErrors) {
			problemRequestor.acceptProblem(
				nodeForErrors,
				IProblemRequestor.DATATABLE_CONTENT_MUST_BE_LITERAL,
				new String[] {
					content,
					canonicalStructureName
				});
			return false;
		}
	}
	
	private class ContentToFixedLengthNumericTypeChecker extends ContentToNumericTypeChecker {
		int maxDigits;
		public ContentToFixedLengthNumericTypeChecker(int maxDigits) {
			this.maxDigits = maxDigits;
		}
		
		boolean checkNumericLiteralContent(String content, PrimitiveTypeBinding primTypeBinding, Node nodeForErrors) {
			int contentLength = content.length();
			
			if(content.startsWith("-")) {
				contentLength -= 1;
			}			
			int decimalLoc = content.indexOf('.');
			if(decimalLoc != -1) {
				contentLength -= content.length()-decimalLoc;
			}
			
			if(contentLength > maxDigits) {
				problemRequestor.acceptProblem(
					nodeForErrors,
					IProblemRequestor.DATATABLE_CONTENT_HAS_INVALID_LENGTH,
					new String[] {
						content,
						canonicalStructureName,
						primTypeBinding.getPrimitive().getName(),
						Integer.toString(maxDigits),
						Integer.toString(contentLength)						
					});
				return false;
			}
			return true;
		}
	}
	
	private class ContentToUserDefinedLengthAndDecimalTypeChecker extends ContentToNumericTypeChecker {
		boolean checkNumericLiteralContent(String content, PrimitiveTypeBinding primTypeBinding, Node nodeForErrors) {
			int totalContentLength = 0;
			int contentLengthBeforeDecimalPoint = 0;
			int contentLengthAfterDecimalPoint = 0;
			
			StringTokenizer st = new StringTokenizer(content, "-.", true);
			if(st.hasMoreTokens()) {
				String firstToken = st.nextToken();
				if("-".equals(firstToken)) {
					firstToken = st.nextToken();
				}
				if(".".equals(firstToken)) {
					contentLengthBeforeDecimalPoint = 0;
					totalContentLength = contentLengthAfterDecimalPoint = trimZerosFromRight(st.nextToken()).length();
				}
				else {
					contentLengthBeforeDecimalPoint = firstToken.length();
					if(st.hasMoreTokens()) {
						String nextToken = st.nextToken();
						if(".".equals(nextToken)) {
							nextToken = st.nextToken();
						}
						contentLengthAfterDecimalPoint = trimZerosFromRight(nextToken).length();
					}
					totalContentLength = contentLengthBeforeDecimalPoint + contentLengthAfterDecimalPoint;
				}
			}
			
			if(contentLengthAfterDecimalPoint > 32) {
				problemRequestor.acceptProblem(
					nodeForErrors,
					IProblemRequestor.DATATABLE_CONTENT_DECIMALS_EXCEED_MAX_LENGTH,
					new String[] {
						content,
						canonicalStructureName,
						Integer.toString(contentLengthAfterDecimalPoint)
					});
				return false;
			}
			else if(contentLengthAfterDecimalPoint > primTypeBinding.getDecimals()) {
				problemRequestor.acceptProblem(
					nodeForErrors,
					IProblemRequestor.DATATABLE_CONTENT_DECIMALS_TOO_LONG,
					new String[] {
						content,
						canonicalStructureName,
						Integer.toString(contentLengthAfterDecimalPoint)
					});
				return false;
			} else {
				int typeBindingLength = primTypeBinding.getLength();
				if(totalContentLength > typeBindingLength) {
					problemRequestor.acceptProblem(
						nodeForErrors,
						IProblemRequestor.DATATABLE_CONTENT_HAS_INVALID_LENGTH,
						new String[] {
							content,
							canonicalStructureName,
							primTypeBinding.getPrimitive().getName(),
							Integer.toString(typeBindingLength),
							Integer.toString(totalContentLength)						
						});
					return false;
				}
			}
			return true;
		}
		
		private String trimZerosFromRight(String str) {
			return str.replaceFirst("0+$", "");
		}
	}
	
	private class ContentToCharacterTypeChecker extends ContentToPrimitiveTypeChecker {
		boolean checkNumericLiteralContent(String content, PrimitiveTypeBinding primTypeBinding, Node nodeForErrors) {
			return checkStringLiteralContent(content, primTypeBinding, nodeForErrors);
		}
		
		boolean checkStringLiteralContent(String content, PrimitiveTypeBinding primTypeBinding, Node nodeForErrors) {
			if(content.length() > primTypeBinding.getLength()) {
				problemRequestor.acceptProblem(
					nodeForErrors,
					IProblemRequestor.DATATABLE_CONTENT_HAS_INVALID_LENGTH,
					new String[] {
						content,
						canonicalStructureName,
						primTypeBinding.getPrimitive().getName(),
						Integer.toString(primTypeBinding.getLength()),
						Integer.toString(content.length())												
					});
				return false;
			}
			return true;
		}
		
		boolean checkBooleanLiteralContent(String content, PrimitiveTypeBinding primTypeBinding, Node nodeForErrors) {
			problemRequestor.acceptProblem(
				nodeForErrors,
				IProblemRequestor.DATATABLE_CONTENT_MUST_BE_LITERAL,
				new String[] {
					content,
					canonicalStructureName
				});
			return false;
		}
	}

	private class ContentToDBCharTypeChecker extends ContentToCharacterTypeChecker {
		boolean checkStringLiteralContent(String content, PrimitiveTypeBinding primTypeBinding, Node nodeForErrors) {
			if(!super.checkStringLiteralContent(content, primTypeBinding, nodeForErrors)) {
				return false;
			}
			
			char[] chars = content.toCharArray();
			
			for (int i = 0; i < chars.length; i++) {
				// This validation check seems bogus...why would table contents have to be valid as an identifier??
				// In order to validate if a unicode character was actually valid, we would need to know the codepage
				// that this table was going to be used for. Then we would need to check to see if there was a corresponding
				// character in the target codepage. Currently we do not have the information needed to do this. 
//				if (!Character.isUnicodeIdentifierPart(chars[i]) && !Character.isSpaceChar(chars[i])) {
//					problemRequestor.acceptProblem(
//						nodeForErrors,
//						IProblemRequestor.DATATABLE_CONTENT_ONLY_UNICODE_ALLOWED,
//						new String[] {
//							content,
//							canonicalStructureName,
//							primTypeBinding.getPrimitive().getName(),
//							String.valueOf(chars[i])
//						});
//					return false;
//				}

				//Converting characters for tighteter value validation
				char[] originalChar = new char[] { chars[i] };
				String originalContents = new String(originalChar);
				byte[] contentBytes = originalContents.getBytes();
				String newContents = new String(contentBytes);

				if (!originalContents.equals(newContents)) {
					problemRequestor.acceptProblem(
						nodeForErrors,
						IProblemRequestor.DATATABLE_CONTENT_HAS_INVALID_CHARACTERS,
						new String[] {
							content,
							canonicalStructureName
						});
					return false;
				}
			}
			return true;
		}
	}
	
	private static Set validCharactersForHexContent = new HashSet(Arrays.asList(new Character[] {
		new Character('0'),	new Character('a'),	new Character('A'),
		new Character('1'), new Character('b'), new Character('B'),
		new Character('2'),	new Character('c'),	new Character('C'),
		new Character('3'),	new Character('d'),	new Character('D'),
		new Character('4'),	new Character('e'),	new Character('E'),
		new Character('5'),	new Character('f'),	new Character('F'),
		new Character('6'),	
		new Character('7'),
		new Character('8'),
		new Character('9')
	}));
	
	private class ContentToHexTypeChecker extends ContentToCharacterTypeChecker {
		boolean checkStringLiteralContent(String content, PrimitiveTypeBinding primTypeBinding, Node nodeForErrors) {
			if(!super.checkStringLiteralContent(content, primTypeBinding, nodeForErrors)) {
				return false;
			}
			
			char[] chars = content.toCharArray();
			for (int i = 0; i < chars.length; i++) {
				if(!validCharactersForHexContent.contains(new Character(chars[i]))) {
					problemRequestor.acceptProblem(
						nodeForErrors,
						IProblemRequestor.DATATABLE_CONTENT_INVALID_HEX_CONTENT,
						new String[] {
							content,
							canonicalStructureName,
							String.valueOf(chars[i])}
						);
						return false;
				}
			}
			return true;
		}
	}
	
	private class ContentToBooleanTypeChecker extends ContentToPrimitiveTypeChecker {

		boolean checkBooleanLiteralContent(String content, PrimitiveTypeBinding primTypeBinding, Node nodeForErrors) {
			return true;
		}
		
		boolean checkStringLiteralContent(String content, PrimitiveTypeBinding primTypeBinding, Node nodeForErrors) {
			problemRequestor.acceptProblem(
				nodeForErrors,
				IProblemRequestor.DATATABLE_CONTENT_MUST_BE_BOOLEAN_LITERAL,
				new String[] {
					content,
					canonicalStructureName
				});
			return false;
		}

		boolean checkNumericLiteralContent(String content, PrimitiveTypeBinding primTypeBinding, Node nodeForErrors) {
			problemRequestor.acceptProblem(
				nodeForErrors,
				IProblemRequestor.DATATABLE_CONTENT_MUST_BE_BOOLEAN_LITERAL,
				new String[] {
					content,
					canonicalStructureName
				});
			return false;
		}
		
	}
	
	private Map primitivesToContentCheckers = null;
	
	private ContentToPrimitiveTypeChecker getChecker(Primitive prim) {
		if(primitivesToContentCheckers == null) {
			primitivesToContentCheckers = new HashMap();
			primitivesToContentCheckers.put(Primitive.BIGINT, new ContentToFixedLengthNumericTypeChecker(Primitive.BIGINT.getDefaultLength()));
			primitivesToContentCheckers.put(Primitive.SMALLINT, new ContentToFixedLengthNumericTypeChecker(Primitive.SMALLINT.getDefaultLength()));
			primitivesToContentCheckers.put(Primitive.INT, new ContentToFixedLengthNumericTypeChecker(Primitive.INT.getDefaultLength()));
			
			primitivesToContentCheckers.put(Primitive.BIN, new ContentToUserDefinedLengthAndDecimalTypeChecker());
			primitivesToContentCheckers.put(Primitive.NUM, new ContentToUserDefinedLengthAndDecimalTypeChecker());
			primitivesToContentCheckers.put(Primitive.NUMC, new ContentToUserDefinedLengthAndDecimalTypeChecker());
			primitivesToContentCheckers.put(Primitive.PACF, new ContentToUserDefinedLengthAndDecimalTypeChecker());
			primitivesToContentCheckers.put(Primitive.DECIMAL, new ContentToUserDefinedLengthAndDecimalTypeChecker());
			
			primitivesToContentCheckers.put(Primitive.CHAR, new ContentToCharacterTypeChecker());
			primitivesToContentCheckers.put(Primitive.UNICODE, new ContentToCharacterTypeChecker());
			
			primitivesToContentCheckers.put(Primitive.DBCHAR, new ContentToDBCharTypeChecker());
			
			primitivesToContentCheckers.put(Primitive.HEX, new ContentToHexTypeChecker());
			
			primitivesToContentCheckers.put(Primitive.BOOLEAN, new ContentToBooleanTypeChecker());
		}
		return (ContentToPrimitiveTypeChecker) primitivesToContentCheckers.get(prim);
	}
	
	private class ContentsAnnotationChecker extends DefaultASTVisitor {
		boolean foundContents = false;
		boolean foundSharedNo = false;
		boolean foundResidentYes = false;
		Column[] columns;
		
		public ContentsAnnotationChecker(Column[] columns) {
			this.columns = columns;
		}

		public boolean visit(DataTable dataTable) {
			return true;
		}

		public boolean visit(SettingsBlock settingsBlock) {
			return true;
		}
		
		public boolean visit(Assignment assignment) {
			if(assignment.getLeftHandSide().isName()) {
				IBinding binding = ((Name) assignment.getLeftHandSide()).resolveBinding();
				if (binding != null && IBinding.NOT_FOUND_BINDING != binding && binding.isAnnotationBinding()) {
					IAnnotationBinding ann = (IAnnotationBinding) binding;
					if(InternUtil.intern(IEGLConstants.PROPERTY_CONTENTS) == binding.getName()) {								
						foundContents = true;
						if(ann.getValue() != null) {
							if(assignment.getRightHandSide() instanceof ArrayLiteral) {
								assignment.getRightHandSide().accept(this);
							}
							else {
								problemRequestor.acceptProblem(
									assignment.getRightHandSide(),
									IProblemRequestor.TABLE_ROW_MUST_BE_LIST);
							}
						}
					}
					else {
						if(InternUtil.intern(IEGLConstants.PROPERTY_SHARED) == binding.getName()) {
							if (ann.getValue() == Boolean.NO) {
								foundSharedNo = true;
								if (foundResidentYes) {
									problemRequestor.acceptProblem(
											assignment.getRightHandSide(),
											IProblemRequestor.SHARED_MUST_BE_YES);
								}
							}
						}
						else {
							if(InternUtil.intern(IEGLConstants.PROPERTY_RESIDENT) == binding.getName()) {
								if (ann.getValue() == Boolean.YES) {
									foundResidentYes = true;
									if (foundSharedNo) {
										problemRequestor.acceptProblem(
												assignment.getRightHandSide(),
												IProblemRequestor.RESIDENT_MUST_BE_NO);
									}
								}
							}
						}
					}
					
				}
			}
			return false;
		}
		public boolean visit(final ArrayLiteral arrayLiteral) {
			final boolean[] errorEncountered = new boolean[] {false};
			for(Iterator iter = arrayLiteral.getExpressions().iterator(); iter.hasNext() && !errorEncountered[0];) {
				((Node) iter.next()).accept(new AbstractASTExpressionVisitor() {
					public void endVisit(ArrayLiteral arrayLiteral) {
						arrayLiteral.accept(new ContentsArrayChecker(columns));
					}
					
					public void endVisitExpression(Expression expression) {
						problemRequestor.acceptProblem(
							arrayLiteral,
							IProblemRequestor.TABLE_ROW_MUST_BE_LIST);
						errorEncountered[0] = true;
					}
				});
			}
			return false;
		}
	}
	
	private class ContentsArrayChecker extends AbstractASTExpressionVisitor {
		boolean visitingTopLevel = true;
		boolean isNegative;
		Column[] columns;
		int elementsVisited = 0;
		
		public ContentsArrayChecker(Column[] columns) {
			this.columns = columns;
		}
		
		public boolean visit(ArrayLiteral arrayLiteral) {
			if(visitingTopLevel) {
				visitingTopLevel = false;
				List expressions = arrayLiteral.getExpressions();
				
				if(expressions.size() != columns.length) {
					problemRequestor.acceptProblem(
						arrayLiteral,
						IProblemRequestor.DATATABLE_NUMBER_COLUMNS_DO_NOT_MATCH,
						new String[] {
							canonicalStructureName,
							Integer.toString(columns.length),
							Integer.toString(expressions.size())
						});
				}
				
				for(Iterator iter = expressions.iterator(); iter.hasNext();) {
					isNegative = false;
					((Expression) iter.next()).accept(this);
					elementsVisited += 1;
				}
			}
			else {
				visitExpression(arrayLiteral);
			}
			return false;
		}
		
		private void checkStringLiteralAgainstColumn(String contentString, Node contentNode) {
			if(elementsVisited < columns.length) {
				ContentToPrimitiveTypeChecker checker = getChecker(columns[elementsVisited].columnType.getPrimitive());
				if(checker != null) {
					if(checker.checkStringLiteralContent(contentString, columns[elementsVisited].columnType, contentNode)) {
						checkTypeCompatibility(contentNode);
					}
				}
			}
		}
		
		private void checkBooleanLiteralAgainstColumn(String contentString, Node contentNode) {
			if(elementsVisited < columns.length) {
				ContentToPrimitiveTypeChecker checker = getChecker(columns[elementsVisited].columnType.getPrimitive());
				if(checker != null) {
					if(checker.checkBooleanLiteralContent(contentString, columns[elementsVisited].columnType, contentNode)) {
						checkTypeCompatibility(contentNode);
					}
				}
			}
		}
		
		private void checkNumericLiteralAgainstColumn(String contentString, Node contentNode) {
			if(elementsVisited < columns.length) {
				ContentToPrimitiveTypeChecker checker = getChecker(columns[elementsVisited].columnType.getPrimitive());
				if(checker != null) {
					if(checker.checkNumericLiteralContent(contentString, columns[elementsVisited].columnType, contentNode)) {
						checkTypeCompatibility(contentNode);
					}
				}
			}
		}
		
		private void checkTypeCompatibility(Node contentNode) {
			if(contentNode instanceof Expression) {
				Expression expression = (Expression) contentNode;
				ITypeBinding contentTypeBinding = expression.resolveTypeBinding();
				if(Binding.isValidBinding(contentTypeBinding)) {
					if(!TypeCompatibilityUtil.isMoveCompatible(columns[elementsVisited].columnType, contentTypeBinding, null, compilerOptions)) {
						problemRequestor.acceptProblem(
							contentNode,
							IProblemRequestor.ASSIGNMENT_STATEMENT_TYPE_MISMATCH,
							new String[] {
								StatementValidator.getTypeString(contentTypeBinding),
								StatementValidator.getTypeString(columns[elementsVisited].columnType),
								columns[elementsVisited].columnName + " = " + expression.getCanonicalString()
							});
					}
				}
			}
		}

		private String getNumericString(String str) {
			return isNegative ? "-" + str : str;
		}
		
		public boolean visit(UnaryExpression unaryExpression) {
            if (unaryExpression.getOperator() == UnaryExpression.Operator.MINUS) {
                isNegative = !isNegative;
            }
            return true;
        }
		
		public boolean visit(IntegerLiteral integerLiteral) {
			checkNumericLiteralAgainstColumn(getNumericString(integerLiteral.getValue()), integerLiteral);
			return false;
		}
		
		public boolean visit(FloatLiteral floatLiteral) {
			checkNumericLiteralAgainstColumn(getNumericString(floatLiteral.getValue()), floatLiteral);
			return false;
		}
		
		public boolean visit(DecimalLiteral decimalLiteral) {
			checkNumericLiteralAgainstColumn(getNumericString(decimalLiteral.getValue()), decimalLiteral);
			return false;
		}
		
		public boolean visit(StringLiteral stringLiteral) {
			checkStringLiteralAgainstColumn(stringLiteral.getValue(), stringLiteral);
			return false;
		}
		
		public boolean visit(CharLiteral stringLiteral) {
			checkStringLiteralAgainstColumn(stringLiteral.getValue(), stringLiteral);
			return false;
		}
		
		public boolean visit(DBCharLiteral stringLiteral) {
			checkStringLiteralAgainstColumn(stringLiteral.getValue(), stringLiteral);
			return false;
		}
		
		public boolean visit(MBCharLiteral stringLiteral) {
			checkStringLiteralAgainstColumn(stringLiteral.getValue(), stringLiteral);
			return false;
		}
		
		public boolean visit(HexLiteral stringLiteral) {
			checkStringLiteralAgainstColumn(stringLiteral.getValue(), stringLiteral);
			return false;
		}
		
		public boolean visit(BinaryExpression binaryExpression) {
			if(new AnnotationTypeBinding.IsStringLiteralChecker().isStringLiteral(binaryExpression)) {
				checkStringLiteralAgainstColumn(getString(binaryExpression), binaryExpression);				
			}
			return false;
		}
		
		public boolean visit(BooleanLiteral booleanLiteral) {
			checkBooleanLiteralAgainstColumn(booleanLiteral.getCanonicalString(), booleanLiteral);
			return false;
		}
		
		private String getString(Expression expr) {
			final String[] result = new String[] {null};
			expr.accept(new DefaultASTVisitor() {
				public boolean visit(StringLiteral stringLiteral) {
					result[0] = stringLiteral.getValue();
					return false;
				}
				public boolean visit(CharLiteral stringLiteral) {
					result[0] = stringLiteral.getValue();
					return false;
				}
				public boolean visit(DBCharLiteral stringLiteral) {
					result[0] = stringLiteral.getValue();
					return false;
				}
				public boolean visit(MBCharLiteral stringLiteral) {
					result[0] = stringLiteral.getValue();
					return false;
				}
				public boolean visit(HexLiteral stringLiteral) {
					result[0] = stringLiteral.getValue();
					return false;
				}
				public boolean visit(BinaryExpression binaryExpression) {
					result[0] = getString(binaryExpression.getFirstExpression()) +
					            getString(binaryExpression.getSecondExpression());
					return false;
				}
			});
			return result[0];
		}
		
		public boolean visitExpression(Expression expression) {
			problemRequestor.acceptProblem(
				expression,
				IProblemRequestor.DATATABLE_CONTENT_MUST_BE_LITERAL,
				new String[] {
					expression.getCanonicalString(),
					canonicalStructureName
				});
			return false;
		}
	}
	
	DataTable tableNode;
    
	public DataTableValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		super(problemRequestor, compilerOptions);
		sItemValidatorFactory = getItemValidatorFactory();
	}
	
	public boolean visit(DataTable dataTable) {
		tableNode = dataTable;
		canonicalStructureName = dataTable.getName().getCanonicalName();
		structureBinding = (FixedStructureBinding) dataTable.getName().resolveBinding();
		sItemValidatorFactory = getItemValidatorFactory();
		
		EGLNameValidator.validate(dataTable.getName(), EGLNameValidator.DATATABLE, problemRequestor, compilerOptions);
		
		return true;
	}
	
	public void endVisit(DataTable dataTable) {
		if(structureBinding != null) {
			Column[] columns = buildColumns(structureBinding);
			
			ContentsAnnotationChecker cAnnotationChecker = new ContentsAnnotationChecker(columns);
			dataTable.accept(cAnnotationChecker);
			if(!cAnnotationChecker.foundContents) {
				problemRequestor.acceptProblem(
					dataTable.getName(),
					IProblemRequestor.TABLE_HAS_NO_CONTENTS,
					new String[] {canonicalStructureName});
			}
			
			checkNumberOfColumnsCorrect(columns.length);
		}
	}
	
	private void checkNumberOfColumnsCorrect(int numColumns) {
		IAnnotationTypeBinding subType = structureBinding.getSubType();
		if(AbstractBinder.annotationIs(subType, new String[] {"egl", "core"}, "MatchValidTable")) {
			checkNumberOfColumnsCorrect(1, numColumns, IProblemRequestor.TABLE_MATCHVALID_MUST_CONTAIN_COLUMN);
		}
		else if(AbstractBinder.annotationIs(subType, new String[] {"egl", "core"}, "MatchInValidTable")) {
			checkNumberOfColumnsCorrect(1, numColumns, IProblemRequestor.TABLE_MATCHINVALID_MUST_CONTAIN_COLUMN);			
		}
		else if(AbstractBinder.annotationIs(subType, new String[] {"egl", "core"}, "MsgTable")) {
			checkNumberOfColumnsCorrect(2, numColumns, IProblemRequestor.TABLE_MESSAGE_MUST_CONTAIN_TWO_COLUMNS);			
		}
		else if(AbstractBinder.annotationIs(subType, new String[] {"egl", "core"}, "RangeChkTable")) {
			checkNumberOfColumnsCorrect(2, numColumns, IProblemRequestor.TABLE_RANGECHECK_MUST_CONTAIN_TWO_COLUMNS);			
		}
	}
	
	private void checkNumberOfColumnsCorrect(int minimumColumnsRequired, int numColumns, int problemKindIfIncorrect) {
		if(numColumns < minimumColumnsRequired) {
			problemRequestor.acceptProblem(
				tableNode.getName(),
				problemKindIfIncorrect,
				new String[] {canonicalStructureName});
		}
	}
	
	private static class Column {
		PrimitiveTypeBinding columnType;
		String columnName;
		
		Column(String columnName, PrimitiveTypeBinding columnType) {
			this.columnType = columnType;
			this.columnName = columnName;
		}
	}
	
	private Column[] buildColumns(FixedStructureBinding structureBinding) {
		List columns = new ArrayList();
		for(Iterator iter = structureBinding.getStructureItems().iterator(); iter.hasNext();) {
			StructureItemBinding nextItem = (StructureItemBinding) iter.next();
			columns.add(new Column(nextItem.getCaseSensitiveName(), (PrimitiveTypeBinding) nextItem.getType()));
		}
		return (Column[]) columns.toArray(new Column[0]);
	}

	public boolean visit(StructureItem structureItem) {
		super.visit(structureItem);
		return false;
	}
	
	public boolean visit(SettingsBlock settingsBlock) {
		super.visit(settingsBlock);
		return false;
	}
	
	private StructureItemValidatorFactory getItemValidatorFactory() {
		return new DefaultDataTableItemValidatorFactory();
	}
}
