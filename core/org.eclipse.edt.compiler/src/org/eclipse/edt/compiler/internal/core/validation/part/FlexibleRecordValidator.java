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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.edt.compiler.binding.EnumerationDataBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordFieldBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.SystemEnumerationDataBinding;
import org.eclipse.edt.compiler.core.EGLSQLKeywordHandler;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.ParenthesizedExpression;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.StringLiteral;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.PCBKind;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.FieldValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Dave Murray
 */
public class FlexibleRecordValidator extends AbstractASTVisitor {
	
	private abstract class RecordNameValidator {
		abstract void validate(Name recordName);
	}
	
	private class DefaultRecordNameValidator extends RecordNameValidator {
		void validate(Name recordName) {
			EGLNameValidator.validate(recordName, EGLNameValidator.RECORD, problemRequestor, compilerOptions);
		}
	}
	
	private class SQLRecordNameValidator extends DefaultRecordNameValidator {
		void validate(Name recordName) {
			super.validate(recordName);
			
			if(getField(recordBinding.getAnnotation(new String[] {"egl", "io", "sql"}, "SQLRecord"), IEGLConstants.PROPERTY_TABLENAMES) == null) {
				if (EGLSQLKeywordHandler.getSQLKeywordNamesToLowerCaseAsSet().contains(recordName.getCanonicalName().toLowerCase())) {
					problemRequestor.acceptProblem(
						recordNameNode,
						IProblemRequestor.RECORD_NAME_CANNOT_DUPE_SQL_CLAUSE_KEYWORD,
						new String[] {
							recordName.getCanonicalName(),
							EGLSQLKeywordHandler.getSQLClauseKeywordNamesCommaSeparatedString()});
				}
			}
		}
	}
	
	private abstract class ItemInfo {
		Node nodeForErrors;
		String canonicalName;
		StructureItem structureItemNode;
		FlexibleRecordFieldBinding binding;
		
		public abstract ItemValidator getValidator();
	}
	
	private class DefaultItemInfo extends ItemInfo {
		public ItemValidator getValidator() {
			return new DefaultItemValidator(this);
		}
	}
	
	private abstract class PSBRecordItemInfo extends ItemInfo {
		private SystemEnumerationDataBinding pcbKind;
		private Set disallowedAnnotations;
		private int errorKindForInvalidAnnotation;
		private IAnnotationBinding pcbAnnotation;
		
		PSBRecordItemInfo(IAnnotationBinding pcbAnnotation, SystemEnumerationDataBinding pcbKind, Set disallowedAnnotations, int errorKindForInvalidAnnotation) {
			this.pcbKind = pcbKind;
			this.disallowedAnnotations = disallowedAnnotations;
			this.errorKindForInvalidAnnotation = errorKindForInvalidAnnotation;
			this.pcbAnnotation = pcbAnnotation;
		}
		
		public ItemValidator getValidator() {
			return new PSBRecordItemValidator(this);
		}
		
		IAnnotationBinding getPCBAnnotation() {
			return pcbAnnotation;
		}
		
		boolean hasPCBAnnotation() {
			return true;
		}
		
		boolean allowsAnnotation(String fieldName) {
			return !disallowedAnnotations.contains(InternUtil.intern(fieldName));
		}
		
		void reportErrorForInvalidAnnotation(String fieldName) {
			problemRequestor.acceptProblem(
				getAnnotationNode(structureItemNode, fieldName),
				errorKindForInvalidAnnotation,
				new String[] {
					fieldName,
					pcbKind.getCaseSensitiveName()
				});
		}
		public SystemEnumerationDataBinding getPCBKind() {
			return pcbKind;
		}
	}
	
	private class TP_PSBRecordItemInfo extends PSBRecordItemInfo {
		public TP_PSBRecordItemInfo(IAnnotationBinding pcbAnnotation) {
			super(pcbAnnotation,
				PCBKind.TP,
				new HashSet(Arrays.asList(new String[] {
					InternUtil.intern(IEGLConstants.PROPERTY_SECONDARYINDEX),
					InternUtil.intern(IEGLConstants.PROPERTY_SECONDARYINDEXITEM),
					InternUtil.intern(IEGLConstants.PROPERTY_HIERARCHY),
				})),
				IProblemRequestor.FIELD_NOT_ALLOWED_FOR_PCBTYPE);
		}
	}
	
	private class DB_PSBRecordItemInfo extends PSBRecordItemInfo {
		public DB_PSBRecordItemInfo(IAnnotationBinding pcbAnnotation) {
			this(pcbAnnotation, Collections.EMPTY_SET, -1);
		}
		
		public DB_PSBRecordItemInfo(IAnnotationBinding pcbAnnotation, Set disallowedAnnotations, int errorKindForInvalidAnnotation) {
			super(pcbAnnotation, PCBKind.DB, disallowedAnnotations, errorKindForInvalidAnnotation);
		}
	}
	
	private class ELAWORK_DB_PSBRecordItemInfo extends DB_PSBRecordItemInfo {
		public ELAWORK_DB_PSBRecordItemInfo(IAnnotationBinding pcbAnnotation) {
			super(pcbAnnotation,
				new HashSet(Arrays.asList(new String[] {
						InternUtil.intern(IEGLConstants.PROPERTY_SECONDARYINDEX),
						InternUtil.intern(IEGLConstants.PROPERTY_SECONDARYINDEXITEM),
						InternUtil.intern(IEGLConstants.PROPERTY_HIERARCHY),
					})),
				IProblemRequestor.FIELD_NOT_ALLOWED_FOR_PCBTYPE_WHEN_ITEM_IS_ELAWORK_OR_ELAMSG);
		}
	}
	
	private class ELAMSG_DB_PSBRecordItemInfo extends DB_PSBRecordItemInfo {
		public ELAMSG_DB_PSBRecordItemInfo(IAnnotationBinding pcbAnnotation) {
			super(pcbAnnotation,
				new HashSet(Arrays.asList(new String[] {
						InternUtil.intern(IEGLConstants.PROPERTY_SECONDARYINDEX),
						InternUtil.intern(IEGLConstants.PROPERTY_SECONDARYINDEXITEM),
						InternUtil.intern(IEGLConstants.PROPERTY_HIERARCHY),
					})),
				IProblemRequestor.FIELD_NOT_ALLOWED_FOR_PCBTYPE_WHEN_ITEM_IS_ELAWORK_OR_ELAMSG);
		}
	}
	
	private class GSAM_PSBRecordItemInfo extends PSBRecordItemInfo {
		public GSAM_PSBRecordItemInfo(IAnnotationBinding pcbAnnotation) {
			super(pcbAnnotation,
				PCBKind.GSAM,
				new HashSet(Arrays.asList(new String[] {
					InternUtil.intern(IEGLConstants.PROPERTY_SECONDARYINDEX),
					InternUtil.intern(IEGLConstants.PROPERTY_SECONDARYINDEXITEM),
					InternUtil.intern(IEGLConstants.PROPERTY_HIERARCHY),
				})),
				IProblemRequestor.FIELD_NOT_ALLOWED_FOR_PCBTYPE);
		}
	}
	
	private class DefaultPSBRecordItemInfo extends PSBRecordItemInfo {
		public DefaultPSBRecordItemInfo(IAnnotationBinding pcbAnnotation) {
			super(pcbAnnotation, null, Collections.EMPTY_SET, -1);
		}
	}
	
	private class NoPCBAnnotationPSBRecordItemInfo extends PSBRecordItemInfo {
		public NoPCBAnnotationPSBRecordItemInfo() {
			super(null, null, Collections.EMPTY_SET, -1);
		}
		
		boolean hasPCBAnnotation() {
			return false;
		}
	}
	
	private class ConsoleFormItemInfo extends ItemInfo {
		public ItemValidator getValidator() {
			return new ConsoleFormItemValidator(this);
		}
	}
	
	private class SQLRecordItemInfo extends ItemInfo {
		public ItemValidator getValidator() {
			return new SQLRecordItemValidator(this);
		}
	}
	
	private class CSVRecordItemInfo extends ItemInfo {
		public ItemValidator getValidator() {
			return new CSVRecordItemValidator(this);
		}
	}
	
	private abstract class ItemValidator {
		protected ItemInfo itemInfo;
		
		public ItemValidator(ItemInfo itemInfo) {
			this.itemInfo = itemInfo;
		}
		
		public abstract void validate();
	}
	
	private class DefaultItemValidator extends ItemValidator {		
		public DefaultItemValidator(ItemInfo itemInfo) {
			super(itemInfo);
		}
		
		public void validate() {
			if(itemInfo.structureItemNode.hasInitializer()) {
				Expression expression = itemInfo.structureItemNode.getInitializer();
				ITypeBinding expressionTBinding = expression.resolveTypeBinding();
				if(expressionTBinding != null && expressionTBinding != IBinding.NOT_FOUND_BINDING) {
					if(!TypeCompatibilityUtil.isMoveCompatible(itemInfo.binding.getType(), expressionTBinding, expression, compilerOptions) &&
					   !expressionTBinding.isDynamic() &&
					   !TypeCompatibilityUtil.areCompatibleExceptions(expressionTBinding, itemInfo.binding.getType(), compilerOptions)) {
						problemRequestor.acceptProblem(
							expression,
							IProblemRequestor.ASSIGNMENT_STATEMENT_TYPE_MISMATCH,
							new String[] {
								StatementValidator.getShortTypeString(itemInfo.binding.getType()),
								StatementValidator.getShortTypeString(expressionTBinding),										
								itemInfo.canonicalName + "=" + expression.getCanonicalString()
							});
					}
				}
			}
			
			
			Type type = itemInfo.structureItemNode.getType();
			if(type != null) {
				StatementValidator.validateRequiredFieldsInCUIDeclaration(type, itemInfo.structureItemNode.getSettingsBlock(), itemInfo.canonicalName.equals("*"), problemRequestor);
			}
			
			IBinding itemBinding = itemInfo.structureItemNode.resolveBinding();
			if(itemBinding != null && IBinding.NOT_FOUND_BINDING != itemBinding && itemBinding.isDataBinding()) {
				StatementValidator.validateDeclarationForStereotypeContext((IDataBinding) itemBinding, problemRequestor, itemInfo.nodeForErrors);
			}
		}
	}
	
	private static Map namesToRequiredPCBTypes = new HashMap();
	static {
		namesToRequiredPCBTypes.put(InternUtil.intern("ELAEXP"), PCBKind.TP);
		namesToRequiredPCBTypes.put(InternUtil.intern("ELAALT"), PCBKind.TP);
		namesToRequiredPCBTypes.put(InternUtil.intern("IOPCB"), PCBKind.TP);
		namesToRequiredPCBTypes.put(InternUtil.intern("ELAMSG"), PCBKind.DB);
		namesToRequiredPCBTypes.put(InternUtil.intern("ELAWORK"), PCBKind.DB);
	}
	private SystemEnumerationDataBinding lastPCBKindDeclared;
	private boolean pcbsDeclaredOutOfOrder = false;
	
	private class PSBRecordItemValidator extends DefaultItemValidator {
		PSBRecordItemInfo itemInfo;
		public PSBRecordItemValidator(PSBRecordItemInfo itemInfo) {
			super(itemInfo);
			this.itemInfo = itemInfo;
		}
		
		public void validate() {
			super.validate();
			
			if(!itemInfo.hasPCBAnnotation() &&
			   itemInfo.binding.getAnnotation(new String[] {"egl", "core"}, "Redefines") == null) {
				problemRequestor.acceptProblem(
					itemInfo.nodeForErrors,
					IProblemRequestor.PCB_PROPERTY_REQUIRED_FOR_PSB_ITEMS);
			}
						
			if(ITypeBinding.ARRAY_TYPE_BINDING == itemInfo.binding.getType().getKind()) {
				problemRequestor.acceptProblem(
					itemInfo.nodeForErrors,
					IProblemRequestor.ARRAYS_NOT_VALID_IN_PSB);
			}
			
			if(ITypeBinding.FIXED_RECORD_BINDING != itemInfo.binding.getType().getBaseType().getKind()) {
				problemRequestor.acceptProblem(
					itemInfo.nodeForErrors,
					IProblemRequestor.DLI_ONLY_FIXED_RECORDS_ALLOWED_IN_PSBRECORD);
			}
			
			if(itemInfo.hasPCBAnnotation()) {
				IAnnotationBinding pcbAnnotation = itemInfo.getPCBAnnotation();
				checkAnnotation(pcbAnnotation, InternUtil.intern(IEGLConstants.PROPERTY_SECONDARYINDEX));
				checkAnnotation(pcbAnnotation, InternUtil.intern(IEGLConstants.PROPERTY_SECONDARYINDEXITEM));
				checkAnnotation(pcbAnnotation, InternUtil.intern(IEGLConstants.PROPERTY_HIERARCHY));
				
				itemInfo.structureItemNode.accept(new DefaultASTVisitor() {
					public boolean visit(StructureItem structureItem) {
						return true;
					}
					public boolean visit(SettingsBlock settingsBlock) {
						return true;
					}
					public boolean visit(SetValuesExpression setValuesExpression) {
						ITypeBinding tBinding = setValuesExpression.resolveTypeBinding();
						if(AbstractBinder.annotationIs(tBinding, new String[] {"egl", "io", "dli"}, "PCB")) {
							return true;
						}
						return false;
					}
					
					public boolean visit(Assignment assignment) {
						IDataBinding dBinding = assignment.getLeftHandSide().resolveDataBinding();
						if(dBinding != null && IBinding.NOT_FOUND_BINDING != dBinding && IDataBinding.ANNOTATION_BINDING == dBinding.getKind()) {
							if(InternUtil.intern(IEGLConstants.PROPERTY_HIERARCHY) == dBinding.getName()) {						
								IAnnotationBinding hierarchyAnnotation = (IAnnotationBinding) dBinding;
								if(hierarchyAnnotation.getValue() != null) {
									ArrayLiteral annotationValueNode = getArrayLiteral(assignment.getRightHandSide());
									if(annotationValueNode != null) {
										validateHierarchy(hierarchyAnnotation, annotationValueNode);
									}
								}
							}
						}
						return false;
					}
					
					private ArrayLiteral getArrayLiteral(Expression expr) {
						final ArrayLiteral[] result = new ArrayLiteral[] {null};
						expr.accept(new DefaultASTVisitor() {
							public boolean visit(ParenthesizedExpression parenthesizedExpression) {
								return true;
							}
							public boolean visit(ArrayLiteral arrayLiteral) {
								result[0] = arrayLiteral;
								return false;
							}
						});
						return result[0];
					}
				});				
			}
			
			Object requiredPCBType = namesToRequiredPCBTypes.get(itemInfo.binding.getName());
			if(requiredPCBType != null && itemInfo.getPCBKind() != null &&
			   requiredPCBType != itemInfo.getPCBKind()) {
				problemRequestor.acceptProblem(
					itemInfo.nodeForErrors,
					IProblemRequestor.DLI_SPECIAL_PSB_NAME_REQUIRES_SPECIFIC_PCBTYPE,
					new String[] {
						itemInfo.canonicalName,
						((IDataBinding) requiredPCBType).getCaseSensitiveName()	
					});
			}
			
			if(!pcbsDeclaredOutOfOrder) {			
				SystemEnumerationDataBinding pcbKind = itemInfo.getPCBKind();
				if(pcbKind != null) {
					if(lastPCBKindDeclared != null) {
						if(lastPCBKindDeclared != pcbKind) {
							if(lastPCBKindDeclared == PCBKind.GSAM) {
								problemRequestor.acceptProblem(
									recordNameNode,
									IProblemRequestor.PCBS_DECLARED_OUT_OF_ORDER_X_AFTER_Y,
									new String[] {
										pcbKind.getCaseSensitiveName(), PCBKind.GSAM.getCaseSensitiveName()
									});
								pcbsDeclaredOutOfOrder = true;
							}
							else if(lastPCBKindDeclared == PCBKind.DB) {
								if(pcbKind != PCBKind.GSAM) {
									problemRequestor.acceptProblem(
										recordNameNode,
										IProblemRequestor.PCBS_DECLARED_OUT_OF_ORDER_X_AFTER_Y,
										new String[] {
											pcbKind.getCaseSensitiveName(), PCBKind.DB.getCaseSensitiveName()
										});
									pcbsDeclaredOutOfOrder = true;
								}
							}
						}					
					}
					lastPCBKindDeclared = pcbKind;
				}				
			}
		}
		
		private boolean checkAnnotation(IAnnotationBinding pcbAnnotation, String fieldName) {
			if(pcbAnnotation.findData(fieldName) != IBinding.NOT_FOUND_BINDING) {
				if(!itemInfo.allowsAnnotation(fieldName)) {
					itemInfo.reportErrorForInvalidAnnotation(fieldName);
					return false;
				}
			}
			return true;
		}
		
		private class RelationshipNode {
			ITypeBinding segmentRecord;
			ITypeBinding parentRecord;
			RelationshipNode parent;
			SetValuesExpression ast;
			
			RelationshipNode(ITypeBinding segmentRecord, ITypeBinding parentRecord, SetValuesExpression ast) {
				this.segmentRecord = segmentRecord;
				this.parentRecord = parentRecord;
				this.ast = ast;
			}
			
			boolean hasParent( RelationshipNode parentRelationship ) {
				if( parent == null ) {
					return false;
				}
				if(parentRecord == parentRelationship.segmentRecord) {
					return true;
				}
				return parent.hasParent( parentRelationship );
			}
			
			int depth() {
				return parent == null ? 1 : parent.depth() + 1;
			}
		}
		
		private boolean containsSegment(List list, RelationshipNode relNode) {
		    return getRelationshipNode(list, relNode.segmentRecord) != null;
		}
		
		private RelationshipNode getRelationshipNode(List nodes, ITypeBinding segmentRecord) {
			for( Iterator iter = nodes.iterator(); iter.hasNext(); ) {
				RelationshipNode next = (RelationshipNode) iter.next();
				if(next.segmentRecord == segmentRecord) {
					return next;
				}
			}
			return null;
		}
		
		private boolean isLoop(RelationshipNode node, List list) {
		    RelationshipNode curNode = getRelationshipNode(list, node.parentRecord);
		    while (curNode != null) {
			    if (curNode == node) {
			        return true;
			    }
			    curNode = getRelationshipNode(list, curNode.parentRecord);
		    }
		    return false;
		}
		
		private void validateHierarchy(IAnnotationBinding hierarchyAnnotation, ArrayLiteral hierarchyValueExpression) {
			Object[] hierarchyAnnotationValue = (Object[]) hierarchyAnnotation.getValue();
			IAnnotationBinding[] relationships = null;
			if(hierarchyAnnotationValue.length != 0 && hierarchyAnnotationValue instanceof IAnnotationBinding[]) {
				relationships = (IAnnotationBinding[]) hierarchyAnnotationValue; 
			}
			boolean relationshipsAreValid = true;
			List unparentedNodes = new ArrayList();
			List parentedNodes = new ArrayList();
						
			if( relationships != null ) {
				Iterator exprIterator = hierarchyValueExpression.getExpressions().iterator();
				for( int i = 0; i < relationships.length; i++ ) {
					IAnnotationBinding currentRelationship = relationships[i];
					SetValuesExpression currentRelationshipAnnotation = (SetValuesExpression) exprIterator.next();
					
					IAnnotationBinding aBinding = getField(currentRelationship, IEGLConstants.PROPERTY_SEGMENTRECORD);
					ITypeBinding segmentRecord = null;
					if(aBinding != null && aBinding.getValue() != null) {
						if(IBinding.NOT_FOUND_BINDING != aBinding.getValue()) {
							segmentRecord = (ITypeBinding) aBinding.getValue(); 
						}
					}
					
					if( segmentRecord == null ) {
						relationshipsAreValid = false;
					}
					
					aBinding = getField(currentRelationship, IEGLConstants.PROPERTY_PARENTRECORD);
					ITypeBinding parentRecord = null;
					if(aBinding != null && aBinding.getValue() != null) {
						if(IBinding.NOT_FOUND_BINDING != aBinding.getValue()) {
							parentRecord = (ITypeBinding) aBinding.getValue(); 
						}
					}
					
					if( i == 0 ) {
						if( parentRecord != null ) {
							relationshipsAreValid = false;
						}
						else {		
							parentedNodes.add( new RelationshipNode( segmentRecord, null, currentRelationshipAnnotation ) );
						}
					}
					else {
						if( parentRecord == null ) {							
							relationshipsAreValid = false;
							if(currentRelationshipAnnotation.getSettingsBlock().getSetting(InternUtil.intern(IEGLConstants.PROPERTY_PARENTRECORD)) == null) {
								problemRequestor.acceptProblem(
									currentRelationshipAnnotation.getExpression(),
									IProblemRequestor.NOT_FIRST_PCB_HIERARCHY_ENTRY_MISSING_PARENTRECORD);
							}
						}
						else {							
							if(segmentRecord == parentRecord) {
								relationshipsAreValid = false;
								problemRequestor.acceptProblem(
									currentRelationshipAnnotation.getExpression(),
									IProblemRequestor.SEGMENTRECORD_AND_PARENTRECORD_CANNOT_BE_SAME);
							}
							else {			
							    RelationshipNode relNode =  new RelationshipNode( segmentRecord, parentRecord, currentRelationshipAnnotation );
							    if (containsSegment(parentedNodes, relNode) || containsSegment(unparentedNodes, relNode)) {
							    	problemRequestor.acceptProblem(
							    		currentRelationshipAnnotation.getSettingsBlock().getSetting(InternUtil.intern(IEGLConstants.PROPERTY_SEGMENTRECORD)),
										IProblemRequestor.DUPLICATE_RELATIONSHIP_IN_HIERARCHY,
										new String[] {segmentRecord.getCaseSensitiveName()});
							    }
								unparentedNodes.add(relNode);
							}
						}
					}
				}
			}
			else {
				relationshipsAreValid = false;
			}
			
			if( relationshipsAreValid ) {
				boolean removedSomethingFromUnparentedList = true;
				
				while( !unparentedNodes.isEmpty() && removedSomethingFromUnparentedList ) {
					List nodesToRemove = new ArrayList();
					
					for( Iterator iter = unparentedNodes.iterator(); iter.hasNext(); ) {
						RelationshipNode nextUnparented = (RelationshipNode) iter.next();
						RelationshipNode parented = getRelationshipNode( parentedNodes, nextUnparented.parentRecord );
						
						if( parented != null ) {
							nodesToRemove.add( nextUnparented );
							if( parented.hasParent( nextUnparented ) ) {
								problemRequestor.acceptProblem(
									nextUnparented.ast.getSettingsBlock().getSetting(InternUtil.intern(IEGLConstants.PROPERTY_PARENTRECORD)).getLeftHandSide(),
									IProblemRequestor.LOOP_IN_DLI_SEGMENT_RECORD_HIERARCHY,
									new String[] { nextUnparented.segmentRecord.getCaseSensitiveName() } );
							}
							else {							
								nextUnparented.parent = parented;							
								parentedNodes.add( nextUnparented );
							}
						}
					}
					
					removedSomethingFromUnparentedList = !nodesToRemove.isEmpty();
					unparentedNodes.removeAll( nodesToRemove );				
				}
				if( !unparentedNodes.isEmpty() ) {
				    List nodesToRemove = new ArrayList();
					for( Iterator iter = unparentedNodes.iterator(); iter.hasNext(); ) {
						RelationshipNode next = (RelationshipNode) iter.next();
						if( getRelationshipNode( unparentedNodes, next.parentRecord ) == null ) {
						    nodesToRemove.add(next);
							problemRequestor.acceptProblem(
								next.ast.getSettingsBlock().getSetting(InternUtil.intern(IEGLConstants.PROPERTY_PARENTRECORD)),
								IProblemRequestor.PARENTRECORD_MISSING_FROM_HIERARCHICAL_PATH_OF_PCB,
								new String[] { next.parentRecord.getCaseSensitiveName() } );
						}
					}
					unparentedNodes.removeAll(nodesToRemove);
					//Check everything left in unparentedNodes for an orphaned loop
					for( Iterator iter = unparentedNodes.iterator(); iter.hasNext(); ) {
						RelationshipNode next = (RelationshipNode) iter.next();
						if (isLoop(next, unparentedNodes)) {
							problemRequestor.acceptProblem(
								next.ast.getSettingsBlock().getSetting(InternUtil.intern(IEGLConstants.PROPERTY_SEGMENTRECORD)),
								IProblemRequestor.LOOP_IN_DLI_SEGMENT_RECORD_HIERARCHY,
								new String[] { next.segmentRecord.getCaseSensitiveName() } );
						}
					}
				}
				
				for( Iterator iter = parentedNodes.iterator(); iter.hasNext(); ) {
					RelationshipNode next = (RelationshipNode) iter.next();
					int depth = next.depth();
					if( depth > 15 ) {
						problemRequestor.acceptProblem(
							next.ast.getExpression(),
							IProblemRequestor.MORE_THAN_FIFTEEN_LEVELS_IN_SEGMENT_RECORD_HIERARCHY,
							new String[] { Integer.toString( depth ) } );
					}
				}
			}			
		}
	}
	
	Map consoleFieldNamesToNodes = new HashMap();
	
	private class ConsoleFormItemValidator extends DefaultItemValidator {
		public ConsoleFormItemValidator(ItemInfo itemInfo) {
			super(itemInfo);
			this.itemInfo = itemInfo;
		}
		
		public void validate() {
			super.validate();
			if(AbstractBinder.typeIs(itemInfo.binding.getType(), new String[] {"egl", "ui", "console"}, "ConsoleField")) {
				if(itemInfo.structureItemNode.hasSettingsBlock()) {
					itemInfo.structureItemNode.getSettingsBlock().accept(new DefaultASTVisitor() {
						public boolean visit(SettingsBlock settingsBlock) {
							return true;
						}
						public boolean visit(final Assignment assignment) {
							assignment.getLeftHandSide().accept(new DefaultASTVisitor() {
								public boolean visit(SimpleName simpleName) {
									if(InternUtil.intern(IEGLConstants.PROPERTY_NAME) == simpleName.getIdentifier()) {
										assignment.getRightHandSide().accept(new DefaultASTVisitor() {
											public boolean visit(StringLiteral stringLiteral) {
												String name = InternUtil.intern(stringLiteral.getCanonicalString());
												List nodesWithName = (List) consoleFieldNamesToNodes.get(name);
												if(nodesWithName == null) {
													nodesWithName = new ArrayList();
													consoleFieldNamesToNodes.put(name, nodesWithName);
												}
												nodesWithName.add(stringLiteral);
												return false;
											}
										});
									}
									return false;
								}
							});
							return false;
						}
					});
				}
			}
		}
	}
	
	private class SQLRecordItemValidator extends DefaultItemValidator {
		public SQLRecordItemValidator(ItemInfo itemInfo) {
			super(itemInfo);
			this.itemInfo = itemInfo;
		}
		
		public void validate() {
			super.validate();
		}
	}
	
	private class CSVRecordItemValidator extends DefaultItemValidator {
		public CSVRecordItemValidator(ItemInfo itemInfo) {
			super(itemInfo);
			this.itemInfo = itemInfo;
		}
		
		public void validate() {
			IBinding itemBinding = itemInfo.structureItemNode.resolveBinding();
			if(itemBinding != null && IBinding.NOT_FOUND_BINDING != itemBinding && itemBinding.isDataBinding()) {
				ITypeBinding typeBinding = ((IDataBinding)itemBinding).getType();
				if(ITypeBinding.ARRAY_TYPE_BINDING == typeBinding.getKind()) {
					problemRequestor.acceptProblem(
							itemInfo.structureItemNode.getType(),
							IProblemRequestor.ARRAY_INVALID_FOR_CSVRECORD);
				}
				else if(ITypeBinding.PRIMITIVE_TYPE_BINDING == typeBinding.getKind()) {
					PrimitiveTypeBinding primTypeBinding = (PrimitiveTypeBinding) typeBinding;
					Primitive prim = primTypeBinding.getPrimitive();
					
					switch(prim.getType()) {
						case Primitive.BLOB_PRIMITIVE:
						case Primitive.CLOB_PRIMITIVE:
						case Primitive.ANY_PRIMITIVE:
							problemRequestor.acceptProblem(
								itemInfo.structureItemNode.getType(),
								IProblemRequestor.INVALID_TYPE_FOR_CSVRECORD,
								new String[] {prim.getName()});
							break;						
					}
				}
				else if (typeBinding.getKind() != ITypeBinding.PRIMITIVE_TYPE_BINDING) {
		            problemRequestor.acceptProblem(itemInfo.structureItemNode.getType(), IProblemRequestor.INVALID_TYPE_FOR_CSVRECORD,
		                    new String[] { typeBinding.getCaseSensitiveName() });
	        	}
			}
			super.validate();
		}
	}
	
	protected IProblemRequestor problemRequestor;
	private FlexibleRecordBinding recordBinding;
	private Name recordNameNode;
	private String canonicalRecordName;
    private ICompilerOptions compilerOptions;
	
	public FlexibleRecordValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
	}
	
	public boolean visit(Record record) {
		recordNameNode = record.getName();
		canonicalRecordName = record.getName().getCanonicalName();
		recordBinding = (FlexibleRecordBinding) record.getName().resolveBinding();		
		getRecordNameValidator().validate(record.getName());
		
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(record);		
		
		return true;
	}
	
	public void endVisit(Record record) {
		for(Iterator iter = consoleFieldNamesToNodes.keySet().iterator(); iter.hasNext();) {
			String nextName = (String) iter.next();
			List nodesWithName = (List) consoleFieldNamesToNodes.get(nextName);
			if(nodesWithName.size() > 1) {
				for(Iterator iter2 = nodesWithName.iterator(); iter2.hasNext();) {
					problemRequestor.acceptProblem(
						(Node) iter2.next(),
						IProblemRequestor.DUPLICATE_NAME_IN_NAMESPACE,
						new String[] {nextName});
				}					
			}	
		}
	}
	
	public boolean visit(StructureItem structureItem) {
		if(structureItem.isEmbedded()) {
		}
		else {
			ItemInfo itemInfo = createItemInfo(structureItem);
			if(itemInfo != null) {
				itemInfo.getValidator().validate();
			}
		}
		
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(structureItem);
		structureItem.accept(new FieldValidator(problemRequestor, compilerOptions));
		
		if (structureItem.hasType()) {
			StatementValidator.validateDataDeclarationType(structureItem.getType(), problemRequestor, recordBinding);
		}
		
		return false;
	}
	
	public boolean visit(SettingsBlock settingsBlock) {
		return false;
	}
	
	private RecordNameValidator getRecordNameValidator() {
		if(recordBinding != null) {
			if(AbstractBinder.annotationIs(recordBinding.getSubType(), new String[] {"egl", "io", "sql"}, "SQLRecord")) {
				return new SQLRecordNameValidator();
			}
		}
		return new DefaultRecordNameValidator();
	}
	
	private ItemInfo createItemInfo(StructureItem sItem) {
		ItemInfo result = null;
		IBinding binding = sItem.resolveBinding();
		if(binding == null || binding == IBinding.NOT_FOUND_BINDING) {
			return null;
		}
		FlexibleRecordFieldBinding itemBinding = (FlexibleRecordFieldBinding) binding; 
		
		if(AbstractBinder.annotationIs(recordBinding.getSubType(), new String[] {"egl", "io", "dli"}, "PSBRecord")) {
			result = createPSBRecordItemInfo(itemBinding);
		}
		else if(AbstractBinder.annotationIs(recordBinding.getSubType(), new String[] {"egl", "ui", "console"}, "ConsoleForm")) {
			result = new ConsoleFormItemInfo();
		}
		else if(AbstractBinder.annotationIs(recordBinding.getSubType(), new String[] {"egl", "io", "sql"}, "SQLRecord")) {
			result = new SQLRecordItemInfo();
		}
		else if(AbstractBinder.annotationIs(recordBinding.getSubType(), new String[] {"egl", "io", "file"}, "CSVRecord")) {
			result = new CSVRecordItemInfo();
		}
		else {
			result = new DefaultItemInfo();
		}
		
		if(sItem.isFiller()) {
			result.nodeForErrors = sItem;
			result.canonicalName = "*";			
		}
		else {
			result.nodeForErrors = sItem.getName();
			result.canonicalName = sItem.getName().getCanonicalName();			
		}
		result.structureItemNode = sItem;
		result.binding = itemBinding;
		
		return result;		
	}
	
	private ItemInfo createPSBRecordItemInfo(IBinding itemBinding) {
		ItemInfo result = null;
		IAnnotationBinding pcbBinding = itemBinding.getAnnotation(new String[] {"egl", "io", "dli"}, "PCB");
		if(pcbBinding == null) {
			result = new NoPCBAnnotationPSBRecordItemInfo();
		}
		else {
			IAnnotationBinding aBinding = getField(pcbBinding, IEGLConstants.PROPERTY_PCBTYPE);
			if(aBinding == null) {
				result = new DefaultPSBRecordItemInfo(pcbBinding);
			}
			else {
				EnumerationDataBinding value = (EnumerationDataBinding) aBinding.getValue();
				if(AbstractBinder.enumerationIs(value, new String[] {"egl", "io", "dli"}, "PCBKind", "TP")) {
					result = new TP_PSBRecordItemInfo(pcbBinding);
				}
				else if(AbstractBinder.enumerationIs(value, new String[] {"egl", "io", "dli"}, "PCBKind", "DB")) {
					if(itemBinding.getName() == InternUtil.intern("ELAWORK")) {
						result = new ELAWORK_DB_PSBRecordItemInfo(pcbBinding);
					}
					else if(itemBinding.getName() == InternUtil.intern("ELAMSG")) {
						result = new ELAMSG_DB_PSBRecordItemInfo(pcbBinding);
					}
					else {
						result = new DB_PSBRecordItemInfo(pcbBinding);
					}
				}
				else if(AbstractBinder.enumerationIs(value, new String[] {"egl", "io", "dli"}, "PCBKind", "GSAM")) {
					result = new GSAM_PSBRecordItemInfo(pcbBinding);
				}
			}
		}
		return result;
	}
	
	private static Node getAnnotationNode(StructureItem sItemNode, final String fieldName) {
		final Node[] result = new Node[] {null};
		sItemNode.accept(new DefaultASTVisitor() {
			public boolean visit(StructureItem structureItem) {
				return true;
			}
			public boolean visit(SetValuesExpression setValuesExpression) {
				return true;
			}
			public boolean visit(SettingsBlock settingsBlock) {
				return true;
			}
			public boolean visit(Assignment assignment) {
				IBinding binding = assignment.getLeftHandSide().resolveDataBinding();
				if(binding != null && binding != IBinding.NOT_FOUND_BINDING && binding.isAnnotationBinding()) {
					if(fieldName == binding.getName()) {
						result[0] = assignment;
					}
				}
				return false;
			}
		});
		return result[0];
	}
	
	private IAnnotationBinding getField(IAnnotationBinding aBinding, String fieldName) {
		IDataBinding fieldBinding = aBinding.findData(fieldName);
		return IBinding.NOT_FOUND_BINDING == fieldBinding ? null : (IAnnotationBinding) fieldBinding;
	}
}
