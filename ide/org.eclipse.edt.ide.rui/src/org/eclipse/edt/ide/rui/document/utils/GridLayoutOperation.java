/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.document.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.ide.core.ast.rewrite.ASTRewrite;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IField;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.rui.internal.Activator;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.mof.utils.NameUtile;
import org.eclipse.text.edits.TextEdit;

public class GridLayoutOperation {
	
	public static final int ACTION_INSERT_ROW_AHEAD = 1;
	public static final int ACTION_INSERT_ROW_AFTER = 2;
	public static final int ACTION_INSERT_COLUMN_AHEAD = 3;
	public static final int ACTION_INSERT_COLUMN_AFTER = 4;
	public static final int ACTION_DELETE_ROW = 5;
	public static final int ACTION_DELETE_COLUMN = 6;
	
	private static final String GRID_LAYOUT_TYPE_NAME = NameUtile.getAsName("GridLayout");
	private static final String CHILDREN_PROPERTY_NAME = NameUtile.getAsName("children");
	private static final String LAYOUT_DATA_PROPERTY_NAME = NameUtile.getAsName("layoutData");
	private static final String ROW_PROPERTY_NAME = NameUtile.getAsName("row");
	private static final String ROWS_PROPERTY_NAME = NameUtile.getAsName("rows");
	private static final String COLUMN_PROPERTY_NAME = NameUtile.getAsName("column");
	private static final String COLUMNS_PROPERTY_NAME = NameUtile.getAsName("columns");
	private static final String HORIZONTAL_SPAN_PROPERTY_NAME = NameUtile.getAsName("horizontalSpan");
	private static final String VERTICAL_SPAN_PROPERTY_NAME = NameUtile.getAsName("verticalSpan");

	private IEGLDocument currentDocument;
	private IPart modelPart;
	private IFile currentFile;
	private TextEdit textEdit;
	private List<String> newChildren = new ArrayList<String>();
	
	private static final int ADD = 1;
	private static final int SUBSTRACT = 2;
	
	EGLWidgetDefinitionDeleteStragegy deleteStrategy;
	private boolean isLastChild;
	
	public GridLayoutOperation(IEGLDocument currentDocument, IFile currentFile){
		this.currentDocument = currentDocument;		
		this.currentFile = currentFile;
	}
		
	public void doOpeation(int iStatementOffset, int iStatementLength, final int row, final int column, final int operationType) {
		try{
			IEGLFile modelFile = (IEGLFile)EGLCore.create(currentFile);
			IEGLFile sharedWorkingCopy = (IEGLFile)modelFile.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null);
			String partName = new Path(currentFile.getName()).removeFileExtension().toString();
			modelPart = sharedWorkingCopy.getPart(partName);
			sharedWorkingCopy.open(null);
			sharedWorkingCopy.reconcile(false, null);
			
			try{
				final org.eclipse.edt.compiler.core.ast.File fileAST = currentDocument.getNewModelEGLFile();
				final ASTRewrite rewrite = ASTRewrite.create(fileAST);
				this.deleteStrategy = new EGLWidgetDefinitionDeleteStragegy( currentDocument, modelPart, rewrite );
				Node gridLayoutWidgetName = DocumentUtil.getWidgetNode( currentDocument, iStatementOffset, iStatementLength );
				if(gridLayoutWidgetName != null){
					gridLayoutWidgetName.getParent().accept(new GridLayoutVisitor(row, column, operationType, rewrite));
				}
				textEdit.apply(currentDocument);
			}catch(Exception e){
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "GridLayout Operation: Error doing GridLayout operation", e));
			}finally{
				sharedWorkingCopy.destroy();					
			}
		}catch(EGLModelException e){
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "GridLayout Operation: Error creating working copy", e));
		}
	}
	
	private void modifyProperty( SettingsBlock fieldSettingsBlock, ASTRewrite rewrite, String property, int operationType, int number ) {
		AssignmentLocator assignmentLocator = new AssignmentLocator(property);
		fieldSettingsBlock.accept(assignmentLocator);
		Assignment assignment = assignmentLocator.getAssignment();
		int value = convertToInt( assignment.getRightHandSide().getCanonicalString() );
		
		if ( value < number ) {
			return;
		}
		
		switch ( operationType ) {
			case ADD:
				value ++;
				break;
			case SUBSTRACT:
				value --;
				break;
		}
		rewrite.setText(assignment, property + " = " + value);
		textEdit =  rewrite.rewriteAST(currentDocument);

	}
	
	private void modifyHorizontalSpanProperty( SettingsBlock fieldSettingsBlock, ASTRewrite rewrite, int number ) {
		AssignmentLocator horizontalSpanAssignmentLocator = new AssignmentLocator(HORIZONTAL_SPAN_PROPERTY_NAME);
		fieldSettingsBlock.accept(horizontalSpanAssignmentLocator);
		Assignment horizontalSpanAssignment = horizontalSpanAssignmentLocator.getAssignment();
		if(horizontalSpanAssignment == null){
			return;
		}
		
		int horizontalSpan = convertToInt( horizontalSpanAssignment.getRightHandSide().getCanonicalString() );
		if(horizontalSpan == 1){
			return;
		}
		
		AssignmentLocator columnAssignmentLocator = new AssignmentLocator(COLUMN_PROPERTY_NAME);
		fieldSettingsBlock.accept(columnAssignmentLocator);
		Assignment columnAssignment = columnAssignmentLocator.getAssignment();
		
		int column = convertToInt( columnAssignment.getRightHandSide().getCanonicalString() );
		
		if( column < number && number < column + horizontalSpan){
			horizontalSpan --;
		}
		
		rewrite.setText(horizontalSpanAssignment, HORIZONTAL_SPAN_PROPERTY_NAME + " = " + horizontalSpan);
		textEdit =  rewrite.rewriteAST(currentDocument);
	}
	
	private void modifyVerticalSpanProperty( SettingsBlock fieldSettingsBlock, ASTRewrite rewrite, int number ) {
		AssignmentLocator verticalSpanAssignmentLocator = new AssignmentLocator(VERTICAL_SPAN_PROPERTY_NAME);
		fieldSettingsBlock.accept(verticalSpanAssignmentLocator);
		Assignment verticalSpanAssignment = verticalSpanAssignmentLocator.getAssignment();
		if(verticalSpanAssignment == null){
			return;
		}
		
		int verticalSpan = convertToInt( verticalSpanAssignment.getRightHandSide().getCanonicalString() );
		if(verticalSpan == 1){
			return;
		}
		
		AssignmentLocator rowAssignmentLocator = new AssignmentLocator(ROW_PROPERTY_NAME);
		fieldSettingsBlock.accept(rowAssignmentLocator);
		Assignment rowAssignment = rowAssignmentLocator.getAssignment();
		
		int row = convertToInt( rowAssignment.getRightHandSide().getCanonicalString() );
		
		if( row < number && number < row + verticalSpan){
			verticalSpan --;
		}
		
		rewrite.setText(verticalSpanAssignment, VERTICAL_SPAN_PROPERTY_NAME + " = " + verticalSpan);
		textEdit =  rewrite.rewriteAST(currentDocument);
	}
	
	private void deleteOperation( SettingsBlock fieldSettingsBlock, ASTRewrite rewrite, String property, String widgetName, int number) {
		AssignmentLocator assignmentLocator = new AssignmentLocator(property);
		fieldSettingsBlock.accept(assignmentLocator);
		Assignment assignment = assignmentLocator.getAssignment();
		int value = convertToInt( assignment.getRightHandSide().getCanonicalString() );
		
		if(value == number){
			try {
				deleteStrategy.deleteWidgetDefinition(widgetName);
			} catch (Exception e) {
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "GridLayoutOperation: error when delete", e));
			}
			textEdit = deleteStrategy.getTextEdit();
			newChildren.remove( widgetName );
		}else if(value > number){
			modifyProperty(fieldSettingsBlock, rewrite, property, SUBSTRACT, -1);
		}
	}
	
	private class GridLayoutVisitor extends DefaultASTVisitor{
		
		private final int row, column, operationType;
		private final ASTRewrite rewrite;
		
		public GridLayoutVisitor(int row, int column, final int operationType, ASTRewrite rewrite){
			this.row = row;
			this.column = column;
			this.operationType = operationType;
			this.rewrite = rewrite;
		}
		
		public boolean visit(Handler handler) {
			return true;
		}
		
		public boolean visit(ClassDataDeclaration classDataDeclaration){
			if (classDataDeclaration.getType().getCanonicalName().equalsIgnoreCase(GRID_LAYOUT_TYPE_NAME) && classDataDeclaration.hasSettingsBlock()) {
				SettingsBlock fieldSettingsBlock = classDataDeclaration.getSettingsBlockOpt();

				if(fieldSettingsBlock != null){
					AssignmentLocator assignmentLocator = new AssignmentLocator(CHILDREN_PROPERTY_NAME);
					fieldSettingsBlock.accept(assignmentLocator);
					final Assignment childrenAssignment = assignmentLocator.getAssignment();
					if(childrenAssignment != null){
						childrenAssignment.accept( new ChildrenArrayVisitor( row+1, column+1, operationType, rewrite ) );
					}
					
					switch ( operationType ) {
						case ACTION_INSERT_ROW_AHEAD:
						case ACTION_INSERT_ROW_AFTER:
							modifyProperty(fieldSettingsBlock, rewrite, ROWS_PROPERTY_NAME, ADD, -1 );
							break;
						case ACTION_INSERT_COLUMN_AHEAD:
						case ACTION_INSERT_COLUMN_AFTER:
							modifyProperty(fieldSettingsBlock, rewrite, COLUMNS_PROPERTY_NAME, ADD, -1 );
							break;
						case ACTION_DELETE_ROW:
							modifyProperty(fieldSettingsBlock, rewrite, ROWS_PROPERTY_NAME, SUBSTRACT, -1  );
							break;
						case ACTION_DELETE_COLUMN:
							modifyProperty(fieldSettingsBlock, rewrite, COLUMNS_PROPERTY_NAME, SUBSTRACT, -1  );
							break;
					}
					
					StringBuffer sbChildren = new StringBuffer("children = [ ");
					for(int i=0; i<newChildren.size(); i++){
						sbChildren.append(newChildren.get(i));
						if(i < newChildren.size() - 1){
							sbChildren.append(", ");
						}
					}
					sbChildren.append("]");
					rewrite.setText(childrenAssignment, sbChildren.toString());
					textEdit =  rewrite.rewriteAST(currentDocument);
				}
			}
			return false;
		}
	}
	
	private class ChildrenArrayVisitor extends DefaultASTVisitor{

		private final int row, column, operationType;
		private final ASTRewrite rewrite;
		
		public ChildrenArrayVisitor(int row, int column, final int operationType, ASTRewrite rewrite){
			this.row = row;
			this.column = column;
			this.operationType = operationType;
			this.rewrite = rewrite;
		}
		
		public boolean visit(final Assignment assignment){
			assignment.getRightHandSide().accept(new DefaultASTVisitor(){
				public boolean visit(ArrayLiteral arrayLiteral){
					List expressions = arrayLiteral.getExpressions();
					for ( int i = 0; i < expressions.size(); i ++ ) {
						if(i == expressions.size() - 1){
							isLastChild = true;
						}
						if ( expressions.get(i) instanceof SimpleName ) {
							SimpleName simpleName = (SimpleName)expressions.get(i);
							newChildren.add(simpleName.getIdentifier());
							try{
								IField[] fields = modelPart.getFields();
								for (int j = 0; j < fields.length; j++) {
									final IField field = fields[j];
									if( field.getElementName().equalsIgnoreCase( simpleName.getIdentifier()) ) {
										Node childWidgetName = currentDocument.getNewModelNodeAtOffset(field.getNameRange().getOffset(), field.getNameRange().getLength());
										if ( childWidgetName != null ) {
											childWidgetName.getParent().accept( new DefaultASTVisitor(){
												public boolean visit(ClassDataDeclaration classDataDeclaration){
													if(classDataDeclaration.getSettingsBlockOpt() != null){
														SettingsBlock fieldSettingsBlock = classDataDeclaration.getSettingsBlockOpt();
														if(fieldSettingsBlock != null){
															AssignmentLocator assignmentLocator = new AssignmentLocator(LAYOUT_DATA_PROPERTY_NAME);
															fieldSettingsBlock.accept(assignmentLocator);
															Assignment layoutDataAssignment = assignmentLocator.getAssignment();
															layoutDataAssignment.getRightHandSide().accept( new DefaultASTVisitor(){
																public boolean visit(final NewExpression newExpression){
																	SettingsBlock fieldSettingsBlock = newExpression.getSettingsBlock();
																	switch ( operationType ) {
																		case ACTION_INSERT_ROW_AHEAD:
																			modifyProperty(fieldSettingsBlock, rewrite, ROW_PROPERTY_NAME, ADD, row );
																			break;
																		case ACTION_INSERT_ROW_AFTER:
																			modifyProperty(fieldSettingsBlock, rewrite, ROW_PROPERTY_NAME, ADD, row + 1 );
																			break;
																		case ACTION_INSERT_COLUMN_AHEAD:
																			modifyProperty(fieldSettingsBlock, rewrite, COLUMN_PROPERTY_NAME, ADD, column );
																			break;
																		case ACTION_INSERT_COLUMN_AFTER:
																			modifyProperty(fieldSettingsBlock, rewrite, COLUMN_PROPERTY_NAME, ADD, column + 1);
																			break;
																		case ACTION_DELETE_ROW:
																			deleteOperation( fieldSettingsBlock, rewrite, ROW_PROPERTY_NAME, field.getElementName(), row );
																			modifyVerticalSpanProperty(fieldSettingsBlock, rewrite, row);
																			if(isLastChild){
																				deleteStrategy.modifyFormManagerEntities();
																				isLastChild = false;
																			}
																			break;
																		case ACTION_DELETE_COLUMN:
																			deleteOperation( fieldSettingsBlock, rewrite, COLUMN_PROPERTY_NAME, field.getElementName(), column );
																			modifyHorizontalSpanProperty(fieldSettingsBlock, rewrite, column);
																			if(isLastChild){
																				deleteStrategy.modifyFormManagerEntities();
																				isLastChild = false;
																			}
																			break;
																	}
																	return false;
																}
															});
														}
													}	
													return false;
												}
											});					
										}
									}
								}
							}catch(Exception e){
								Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Get Event Handler: Error finding functions", e));
							}
						}
					}
					return false;
				}
			});
		
			return false;
		}
	}
	
	private int convertToInt( String str ) {
		try {
			return Integer.parseInt( str );
		} catch ( Exception e ) {
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "GridLayoutOperation: Error convert to int", e));
		}
		return 0;
	}

}
