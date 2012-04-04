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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.edt.compiler.binding.AmbiguousDataBinding;
import org.eclipse.edt.compiler.binding.AnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.EnumerationDataBinding;
import org.eclipse.edt.compiler.binding.FixedStructureBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.EGLSQLKeywordHandler;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.BinaryExpression;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.LiteralExpression;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.core.ast.UnaryExpression;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;

import com.ibm.icu.util.StringTokenizer;

/**
 * @author Dave Murray
 */
public class FixedRecordValidator extends FixedStructureValidator {
	
	private static final Set PRIMITIVES_NOT_ALLOWED_IN_SQL_RECORDS = new HashSet(Arrays.asList(new Primitive[] {
		Primitive.MBCHAR
	}));
	
	private static final Set PRIMITIVES_NOT_ALLOWED_IN_VGUI_RECORDS = new HashSet(Arrays.asList(new Primitive[] {
		Primitive.DATE,		Primitive.TIME,		Primitive.TIMESTAMP,			Primitive.MONTHSPAN_INTERVAL,
		Primitive.FLOAT,	Primitive.MONEY,	Primitive.SECONDSPAN_INTERVAL
	}));
	
	private abstract class RecordNameValidator {
		abstract void validate(Name recordName);
	}
	
	private class DefaultRecordNameValidator extends RecordNameValidator {
		void validate(Name recordName) {
			
		}
	}
	
	private class SQLRecordNameValidator extends DefaultRecordNameValidator {
		void validate(Name recordName) {
			super.validate(recordName);
			
			if(getField(structureBinding.getAnnotation(new String[] {"egl", "io", "sql"}, "SQLRecord"), IEGLConstants.PROPERTY_TABLENAMES) == null) {
				if (EGLSQLKeywordHandler.getSQLKeywordNamesToLowerCaseAsSet().contains(recordName.getCanonicalName().toLowerCase())) {
					problemRequestor.acceptProblem(
						recordNode.getName(),
						IProblemRequestor.RECORD_NAME_CANNOT_DUPE_SQL_CLAUSE_KEYWORD,
						new String[] {
							recordName.getCanonicalName(),
							EGLSQLKeywordHandler.getSQLClauseKeywordNamesCommaSeparatedString()});
				}
			}
		}
	}
	
	private class DLISegmentRecordNameValidator extends DefaultRecordNameValidator {
		void validate(Name recordName) {
			super.validate(recordName);
			if(structureBinding.getAnnotation(structureBinding.getSubType()).findData(InternUtil.intern(IEGLConstants.PROPERTY_SEGMENTNAME)) == IBinding.NOT_FOUND_BINDING) {
				if(!isValidDLIName(recordName.getCanonicalName())) {
					problemRequestor.acceptProblem(
						recordNode.getName(),
						IProblemRequestor.SEGMENTRECORD_NOT_VALID_DLINAME_SO_MUST_DEFINE_SEGMENTNAME,
						new String[] {recordName.getCanonicalName()});
				}
			}
		}
	}
	
	private class DefaultFixedRecordItemValidator extends DefaultStructureItemValidator {
		public DefaultFixedRecordItemValidator() {
			super();
		}
		
		public void validate(StructureItemInfo sItemInfo) {
			super.validate(sItemInfo);
			
			boolean itemIsLeaf = sItemInfo.binding.getChildren().isEmpty();
			boolean hasInitialzer = sItemInfo.structureItemNode.hasInitializer(); 
			
			if(itemIsLeaf) {
				if(hasInitialzer) {
					Expression initializer = sItemInfo.structureItemNode.getInitializer();
					if(checkInitializerIsLiteral(initializer)) {
						List occursDimensions = new ArrayList();
						occursDimensions.addAll(sItemInfo.binding.getOccursDimensions());
						checkInitializer(sItemInfo, occursDimensions, initializer);
					}
				}
			}
			else {
				if(sItemInfo.structureItemNode.hasInitializer()) {
					problemRequestor.acceptProblem(
						sItemInfo.structureItemNode.getInitializer(),
						IProblemRequestor.INITIALIZERS_ONLY_ALLOWED_ON_LEAF_ITEMS);
				}
			}
			
			if(!isNullableIsAllowed()) {
				IAnnotationBinding aBinding = sItemInfo.binding.getAnnotation(new String[] {"egl", "io", "sql"}, "IsSqlNullable");
				if(aBinding != null && Boolean.YES == aBinding.getValue()) {
					problemRequestor.acceptProblem(
						sItemInfo.nodeForErrors,
						IProblemRequestor.NON_SQL_RECORDS_CANNOT_CONTAIN_NULLABLE,
						new String[] {
							sItemInfo.canonicalName,
							canonicalStructureName
						});
				}
			}
		}
		
		private boolean checkInitializerIsLiteral(final Expression initializerExpr) {
			final boolean[] result = new boolean[] {true};
			initializerExpr.accept(new AbstractASTExpressionVisitor() {
				public boolean visit(UnaryExpression unaryExpression) {
					return true;
				}
				public void endVisitLiteral(LiteralExpression literal) {}
				
				public void endVisit(BinaryExpression binaryExpression) {
					if(!new AnnotationTypeBinding.IsStringLiteralChecker().isStringLiteral(binaryExpression)) {
						endVisitExpression(binaryExpression);
					}
				}
				public void endVisit(UnaryExpression unaryExpression) {
				}
				public void endVisitExpression(Expression expression) {
					problemRequestor.acceptProblem(
						initializerExpr,
						IProblemRequestor.FIXED_RECORD_ITEM_INITIALIZERS_MUST_BE_LITERAL_VALUES);
					result[0] = false;						
				}
			});
			return result[0];
		}
		
		private boolean checkInitializer(StructureItemInfo sItemInfo, final List occursDimensions, Expression initialzerExpression) {
			return checkInitializer(sItemInfo, occursDimensions, initialzerExpression, sItemInfo.canonicalName);
		}
		
		private boolean checkInitializer(final StructureItemInfo sItemInfo, final List occursDimensions, final Expression initialzerExpression, final String canonicalItemName) {
			final boolean[] result = new boolean[] {true};
			if(occursDimensions.isEmpty()) {
				initialzerExpression.accept(new AbstractASTExpressionVisitor() {
					public void endVisit(ArrayLiteral arrayLiteral) {
						problemRequestor.acceptProblem(
							arrayLiteral,
							IProblemRequestor.NON_MULTIPLY_OCCURING_ITEM_CANNOT_BE_INITIALIZED_WITH_ARRAY,
							new String[] {canonicalItemName});
						result[0] = false;
					}
					public void endVisitExpression(Expression expression) {
						ITypeBinding expressionTBinding = expression.resolveTypeBinding();
						if(expressionTBinding != null) {
							if(!TypeCompatibilityUtil.isMoveCompatible(sItemInfo.binding.getType(), expressionTBinding, expression, compilerOptions)) {
								problemRequestor.acceptProblem(
									expression,
									IProblemRequestor.ASSIGNMENT_STATEMENT_TYPE_MISMATCH,
									new String[] {
										getShortName(sItemInfo.binding.getType()),
										getShortName(expressionTBinding),										
										canonicalItemName + "=" + expression.getCanonicalString()
									});
								result[0] = false;
							}
						}
					}
				});
			}
			else {
				final int currentOccurs = ((Integer) occursDimensions.remove(0)).intValue();
				initialzerExpression.accept(new AbstractASTExpressionVisitor() {
					public void endVisit(ArrayLiteral arrayLiteral) {
						List expressions = arrayLiteral.getExpressions();
						if(expressions.size() > currentOccurs) {
							problemRequestor.acceptProblem(
								arrayLiteral,
								IProblemRequestor.TOO_MANY_ELEMENTS_IN_STRUCTURE_ITEM_ARRAY_INITIALIZER,
								new String[] {
									canonicalItemName,									
									Integer.toString(expressions.size()),
									Integer.toString(currentOccurs)
								});
							result[0] = false;
						}
						
						int itemIndex = 1;
						for(Iterator iter = expressions.iterator(); iter.hasNext();) {
							Expression nextExpr = (Expression) iter.next();
							List dimensionsLeft = new ArrayList();
							dimensionsLeft.addAll(occursDimensions);
							
							result[0] = result[0] & checkInitializer(sItemInfo, dimensionsLeft, nextExpr, canonicalItemName + "[" + itemIndex++ + "]");
						}
					}
					
					public void endVisitExpression(Expression expression) {
						problemRequestor.acceptProblem(
							expression,
							IProblemRequestor.MULTI_DIMENSIONAL_OCCURING_ITEM_HAS_NON_ARRAY_INITIALIZER,
							new String[] {
									canonicalItemName,
								Integer.toString(currentOccurs)
							});
						result[0] = false;
					}
				});
			}
			return result[0];
		}
		
		protected boolean isNullableIsAllowed() {
			return false;
		}
		
		private String getShortName(ITypeBinding tBinding) {
			if(ITypeBinding.PRIMITIVE_TYPE_BINDING == tBinding.getKind()) {
				return ((PrimitiveTypeBinding) tBinding).getPrimitive().getName();
			}
			return tBinding.getName();
		}
	}
	
	private class SQLRecordItemValidator extends DefaultFixedRecordItemValidator {
		public SQLRecordItemValidator() {
			super();
		}
		
		public void validate(StructureItemInfo sItemInfo) {
			super.validate(sItemInfo);
			
			if(sItemInfo.binding.hasOccurs()) {
				problemRequestor.acceptProblem(
					sItemInfo.nodeForErrors,
					IProblemRequestor.SQL_RECORD_HAS_OCCURS,
					new String[] {
						sItemInfo.canonicalName,
						canonicalStructureName
					});
			}
			
			if(sItemInfo.structureItemNode.isFiller()) {
				problemRequestor.acceptProblem(
					sItemInfo.nodeForErrors,
					IProblemRequestor.FILLER_NOT_ALLOWED_IN_SQL,
					new String[]{canonicalStructureName});
			}
			
			if(!sItemInfo.binding.getChildren().isEmpty()) {
				problemRequestor.acceptProblem(
					sItemInfo.nodeForErrors,
					IProblemRequestor.SQL_FLAT_LEVEL_NUMBERING,
					new String[] {
						canonicalStructureName,
						sItemInfo.canonicalName
					});
			}
			
			if(ITypeBinding.PRIMITIVE_TYPE_BINDING == sItemInfo.binding.getType().getKind()) {
				PrimitiveTypeBinding primTypeBinding = (PrimitiveTypeBinding) sItemInfo.binding.getType();
				Primitive prim = primTypeBinding.getPrimitive();
				
				if(PRIMITIVES_NOT_ALLOWED_IN_SQL_RECORDS.contains(prim)) {
					problemRequestor.acceptProblem(
						sItemInfo.structureItemNode.getType(),
						IProblemRequestor.STRUCTURE_ITEM_FOR_SQL_RECORD_HAS_INVALID_TYPE,
						new String[] {
							sItemInfo.canonicalName,
							canonicalStructureName,
							prim.getName()
						});
				}
				
				if(prim == Primitive.BIN && primTypeBinding.getDecimals() != 0) {
					problemRequestor.acceptProblem(
						sItemInfo.structureItemNode.getType(),
						IProblemRequestor.BIN_STRUCTURE_ITEM_FOR_SQL_RECORD_IS_NOT_INTEGER,
						new String[] {
							Primitive.BIN.getName(),
							sItemInfo.canonicalName,
							canonicalStructureName
						});
				}
			}
			
			IAnnotationBinding aBinding = sItemInfo.binding.getAnnotation(new String[] {"egl", "io", "sql"}, "IsReadOnly");
			if(aBinding != null && Boolean.NO == aBinding.getValue()) {
				boolean mustBeReadOnly = getRecordIsSQLJoinRecord() || hasExpressionForColumn(sItemInfo);
				
				if(mustBeReadOnly) {
					problemRequestor.acceptProblem(
						sItemInfo.nodeForErrors,
						IProblemRequestor.SQL_ITEM_MUST_BE_READ_ONLY_IF_JOIN_OR_EXP,
						new String[] {sItemInfo.canonicalName});
				}
			}
		}
		
		public void validateEmbeddedItem(StructureItem embeddedItem) {
			ITypeBinding binding = embeddedItem.getType().resolveTypeBinding();
			if(binding != null && binding != IBinding.NOT_FOUND_BINDING && ITypeBinding.FIXED_RECORD_BINDING == binding.getKind()) {
				if(binding.getAnnotation(new String[] {"egl", "io", "sql"}, "SQLRecord") == null) {
					problemRequestor.acceptProblem(
						embeddedItem.getType(),
						IProblemRequestor.EMBEDDED_RECORD_MUST_BE_SQL_RECORD,
						new String[] {
							embeddedItem.getType().getCanonicalName()
						});
				}
			}
		}
		
		private boolean hasExpressionForColumn(StructureItemInfo sItemInfo) {
			IAnnotationBinding aBinding = sItemInfo.binding.getAnnotation(new String[] {"egl", "io", "sql"}, "column");
			if(aBinding != null && aBinding.getValue() != null) {
				return new StringTokenizer((String) aBinding.getValue(), "+-/*\t\n\r\f").countTokens() > 1;
			}
			return false;
		}
		
		protected boolean isNullableIsAllowed() {
			return true;
		}
	}
	
	private List validationOrderValues = new ArrayList();
	
	protected class VGUIRecordItemValidator extends DefaultFixedRecordItemValidator {		
		public VGUIRecordItemValidator() {
			super();
		}
		
		public void validate(StructureItemInfo sItemInfo) {
			super.validate(sItemInfo);
			
			IAnnotationBinding aBinding = sItemInfo.binding.getAnnotation(new String[] {"egl", "ui"}, "ValidationOrder");
			if(aBinding != null) {
				IAnnotationBinding uiTypeABinding = sItemInfo.binding.getAnnotation(new String[] {"egl", "ui", "webTransaction"}, "UIType");
				if(uiTypeABinding != null) {
					EnumerationDataBinding uiType = (EnumerationDataBinding) uiTypeABinding.getValue();
					if(InternUtil.intern("INPUT") == uiType.getName() ||
					   InternUtil.intern("INPUTOUTPUT") == uiType.getName()) {
						validationOrderValues.add(aBinding.getValue());
					}
				}
			}
			
			if(ITypeBinding.PRIMITIVE_TYPE_BINDING == sItemInfo.binding.getType().getKind()) {
				PrimitiveTypeBinding primTypeBinding = (PrimitiveTypeBinding) sItemInfo.binding.getType();
				Primitive prim = primTypeBinding.getPrimitive();
				
				if(PRIMITIVES_NOT_ALLOWED_IN_VGUI_RECORDS.contains(prim)) {
					problemRequestor.acceptProblem(
						sItemInfo.structureItemNode.getType(),
						IProblemRequestor.INVALID_TYPE_IN_VGUIRECORD,
						new String[] {prim.getName()});
				}
			}
		}
		
		public void validatePart(FixedStructureBinding fixedStructureBinding) {
			 
			Map simpleNamesToDBindings = fixedStructureBinding.getSimpleNamesToDataBindingsMap();
			for(Iterator iter = simpleNamesToDBindings.keySet().iterator(); iter.hasNext();) {
				String nextName = (String) iter.next();
				IDataBinding nextDBinding = (IDataBinding) simpleNamesToDBindings.get(nextName);
				if(nextDBinding == AmbiguousDataBinding.getInstance()) {
					if(!nextName.equals("*")) {
						problemRequestor.acceptProblem(
							recordNode.getName(),
							IProblemRequestor.ITEM_NAMES_MUST_BE_UNIQUE_IN_VGUIRECORD,
							new String[] {nextName});
					}
				}
				else if(((StructureItemBinding) nextDBinding).numOccursDimensions() > 1) {
					problemRequestor.acceptProblem(
						recordNode.getName(),
						IProblemRequestor.MULTI_DIMENSIONAL_MULTI_OCCURING_ITEMS_NOT_ALLOWED_IN_VGUIRECORD,
						new String[] {((StructureItemBinding) nextDBinding).getParentQualifiedName()});
				}
			}
		}
		
	}
	
	private class DefaultFixedRecordItemValidatorFactory extends StructureItemValidatorFactory {
		StructureItemValidator createStructureItemValidator() {
			return new DefaultFixedRecordItemValidator();
		}
	}
	
	private class SQLStructureItemValidatorFactory extends StructureItemValidatorFactory {
		StructureItemValidator createStructureItemValidator() {
			return new SQLRecordItemValidator();
		}
	}
		
	private class VGUIStructureItemValidatorFactory extends StructureItemValidatorFactory {
		StructureItemValidator createStructureItemValidator() {
			return new VGUIRecordItemValidator();
		}
	}
	
	private Record recordNode;
	private java.lang.Boolean recordIsSQLJoinRecord;
	
	public FixedRecordValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		super(problemRequestor, compilerOptions);
	}
	
	public boolean visit(Record record) {
		recordNode = record;
		canonicalStructureName = record.getName().getCanonicalName();
		structureBinding = (FixedStructureBinding) record.getName().resolveBinding();
		getRecordNameValidator().validate(record.getName());
		sItemValidatorFactory = getItemValidatorFactory();
		
		EGLNameValidator.validate(record.getName(), EGLNameValidator.RECORD, problemRequestor, compilerOptions);

		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(record);		
				
		return true;
	}
	
	public void endVisit(Record record) {
		checkValidationOrderValues();
		
		if(structureBinding != null) {
			getItemValidatorFactory().createStructureItemValidator().validatePart(structureBinding);
		}
	}
	
	public boolean visit(SettingsBlock settingsBlock) {
		super.visit(settingsBlock);
		return false;
	}
	

	public boolean visit(StructureItem structureItem) {
		super.visit(structureItem);
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(structureItem);
		return false;
	}
	
	private RecordNameValidator getRecordNameValidator() {
		if(structureBinding != null) {
			if(AbstractBinder.annotationIs(structureBinding.getSubType(), new String[] {"egl", "io", "sql"}, "SQLRecord")) {
				return new SQLRecordNameValidator();
			}
			if(AbstractBinder.annotationIs(structureBinding.getSubType(), new String[] {"egl", "io", "dli"}, "DLISegment")) {
				return new DLISegmentRecordNameValidator();
			}			
		}
		return new DefaultRecordNameValidator();
	}
	
	private StructureItemValidatorFactory getItemValidatorFactory() {
		if(structureBinding != null) {
			if(AbstractBinder.annotationIs(structureBinding.getSubType(), new String[] {"egl", "io", "sql"}, "SQLRecord")) {
				return new SQLStructureItemValidatorFactory();
			}
			if(AbstractBinder.annotationIs(structureBinding.getSubType(), new String[] {"egl", "ui", "webTransaction"}, "VGUIRecord")) {
				return new VGUIStructureItemValidatorFactory();
			}
						
			
		}
		return new DefaultFixedRecordItemValidatorFactory();
	}
	
	private void checkValidationOrderValues() {
		if( !validationOrderValues.isEmpty() ) {
			Collections.sort( validationOrderValues );
			
			Iterator iter = validationOrderValues.iterator();
			int valOrder = ((Integer) iter.next()).intValue();
			
			if( valOrder < 1 ) {
				problemRequestor.acceptProblem(
					recordNode.getName(),
					IProblemRequestor.VALIDATION_ORDER_VALUES_INVALID,
					new String[] {} );
			}
			
			while( iter.hasNext() ) {
				int nextOrder = ((Integer) iter.next()).intValue();
				if( nextOrder <= valOrder) {
					problemRequestor.acceptProblem(
						recordNode.getName(),
						IProblemRequestor.VALIDATION_ORDER_VALUES_INVALID);
					break;
				}
				valOrder = nextOrder;
			}
		}
	}
	
	private boolean getRecordIsSQLJoinRecord() {
		if(recordIsSQLJoinRecord == null) {
			recordIsSQLJoinRecord = java.lang.Boolean.FALSE;
			if(structureBinding != null) {
				if(AbstractBinder.annotationIs(structureBinding.getSubType(), new String[] {"egl", "io", "sql"}, "SQLRecord")) {
					IAnnotationBinding aBinding = getField(structureBinding.getAnnotation(new String[] {"egl", "io", "sql"}, "SQLRecord"), IEGLConstants.PROPERTY_TABLENAMES);
					if(aBinding != null && aBinding.getValue() != null && ((Object[]) aBinding.getValue()).length > 1) {
						recordIsSQLJoinRecord = java.lang.Boolean.TRUE;
					}
				}
			}			
		}
		return recordIsSQLJoinRecord.booleanValue();
	}
	
	private static boolean isValidDLIName( String name ) {
		if( name.length() > 8 ) {
			return false;
		}
		if( name.length() != 0 ) {
			char ch = name.charAt( 0 );
			if( !Character.isLetter( ch ) && ch != '@' && ch != '$' && ch != '#' ) {
				return false;
			}
			if( name.length() != 1 ) {
				for( int i = 1; i < name.length(); i++ ) {
					ch = name.charAt( i );
					if( !Character.isLetter( ch ) && !Character.isDigit( ch ) && ch != '@' && ch != '$' && ch != '#' ) {
						return false;
					}					
				}
			}
		}
		return true;
	}
	
	private IAnnotationBinding getField(IAnnotationBinding aBinding, String fieldName) {
		IDataBinding fieldBinding = aBinding.findData(fieldName);
		return IBinding.NOT_FOUND_BINDING == fieldBinding ? null : (IAnnotationBinding) fieldBinding;
	}
}
