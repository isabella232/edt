/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Status;
import org.eclipse.text.edits.TextEdit;

import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.ide.core.ast.rewrite.ASTRewrite;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.core.model.IField;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.rui.internal.Activator;

public class EGLWidgetDefinitionDeleteStragegy {
	private static final String CHILDREN_PROPERTY_NAME = InternUtil.intern("children");
	private static final String MVC_PROPERTY_NAME = InternUtil.intern("mvc");
	private static final String VIEW_PROPERTY_NAME = InternUtil.intern("view");
	private static final String CONTROLLER_PROPERTY_NAME = InternUtil.intern("controller");
	private static final String NAME_LABEL_PROPERTY_NAME = InternUtil.intern("nameLabel");
	private static final String ERROR_LABEL_PROPERTY_NAME = InternUtil.intern("errorLabel");
	private static final String ENTRIES_PROPERTY_NAME = InternUtil.intern("entries");
	
	private static final String CONTROLLER_TYPE_NAME = InternUtil.intern("Controller");
	private static final String FORM_FIELD_TYPE_NAME = InternUtil.intern("FormField");
	private static final String FORM_FIELD_TYPE_FULL_NAME = InternUtil.intern("org.eclipse.edt.rui.mvc.FormField");
	private static final String FORM_MANAGER_TYPE_NAME = InternUtil.intern("FormManager");
	
	private IEGLDocument currentDocument;
	private IPart modelPart;
	private ASTRewrite rewrite;
	private ChildrenRecursiveVisitor childrenVisitor;
	private ControllerVisitor controllerVisitor;
	private FormFieldVisitor formFieldVisitor;
	private FormManagerVisitor formManagerVisitor;
	
	//share delete use
	private Set<String> deleteFormFieldNames;
	private Set<String> oldFormFieldNames;
	private Set<Node> deletedFormFieldNodes;
	private Assignment currentFormManagerEntitiesAssignment;
	private Assignment formManagerEntitiesAssignment;
	
	
	private TextEdit textEdit;

	public EGLWidgetDefinitionDeleteStragegy(IEGLDocument currentDocument, IPart modelPart, ASTRewrite rewrite) {
		this.currentDocument = currentDocument;
		this.modelPart = modelPart;
		this.rewrite = rewrite;
		
		this.deleteFormFieldNames = new HashSet<String>();
		this.oldFormFieldNames = new HashSet<String>();
		this.deletedFormFieldNodes = new HashSet<Node>();
	}

	public void deleteWidgetDefinition(final String widgetName) throws Exception {
		childrenVisitor = new ChildrenRecursiveVisitor(modelPart, rewrite);
		controllerVisitor = new ControllerVisitor(rewrite);
		formFieldVisitor = new FormFieldVisitor();
		formManagerVisitor = new FormManagerVisitor();

		IField[] fields = modelPart.getFields();
		for (int j = 0; j < fields.length; j++) {
			IField field = fields[j];
			if (field.getElementName().equalsIgnoreCase(widgetName)) {
				Node widgetNameNode = currentDocument.getNewModelNodeAtOffset(field.getNameRange().getOffset(), field.getNameRange().getLength());
				Node widgetDefinition = widgetNameNode.getParent();
				//process child widget
				widgetDefinition.accept(childrenVisitor);
				//process related controller
				controllerVisitor.setWidgetName(widgetName);
				widgetDefinition.getParent().accept(controllerVisitor);
				//process related formField
				formFieldVisitor.setDeleteLabelPropertyName(widgetName);
				formFieldVisitor.setDeleteControllerPropertyName(controllerVisitor.getContorllerName());
				widgetDefinition.getParent().accept(formFieldVisitor);
				//process related formManager
				widgetDefinition.getParent().accept(formManagerVisitor);
				//delete widget definition
				rewrite.removeNode(widgetDefinition);
				textEdit = rewrite.rewriteAST(currentDocument);
				break;
			}
		}
		deleteFormFields();
	}
	
	private void deleteFormFields(){
		try{
			IField[] fields = modelPart.getFields();
			for (int j = 0; j < fields.length; j++) {
				IField field = fields[j];
				if(deleteFormFieldNames.contains(field.getElementName())){
					Node formFieldNameNode = currentDocument.getNewModelNodeAtOffset(field.getNameRange().getOffset(),field.getNameRange().getLength());
					Node formFieldNode = formFieldNameNode.getParent();
					
					if(!deletedFormFieldNodes.contains(formFieldNode)){
						rewrite.removeNode(formFieldNode);
						textEdit = rewrite.rewriteAST(currentDocument);
						deletedFormFieldNodes.add(formFieldNode);
					}
				}
			}
		}catch (Exception e) {
			Activator.getDefault().getLog().log(new Status(Status.ERROR,Activator.PLUGIN_ID,"Delete Form Field Definition: Error deleting definition",e));
		}
	}
	
	public void modifyFormManagerEntities(){
		if(formManagerEntitiesAssignment != null){
			List<String> newFormFieldNames = new ArrayList<String>();
			for(String oldFormFieldName : oldFormFieldNames){
				if(!deleteFormFieldNames.contains(oldFormFieldName)){
					newFormFieldNames.add(oldFormFieldName);
				}
			}
			
			StringBuffer sbNewFormFields = new StringBuffer("entries = [ ");
			for(int i=0; i<newFormFieldNames.size(); i++){
				sbNewFormFields.append(newFormFieldNames.get(i));
				if(i != newFormFieldNames.size() - 1){
					sbNewFormFields.append(", ");
				}
			}
			String sNewFormFields = sbNewFormFields.append(" ]").toString();
			rewrite.setText(formManagerEntitiesAssignment, sNewFormFields);
			textEdit = rewrite.rewriteAST(currentDocument);
		}
	}

	public TextEdit getTextEdit() {
		return textEdit;
	}

	private class ControllerVisitor extends DefaultASTVisitor {
		private final ASTRewrite rewrite;
		private String widgetName;
		private String contorllerName;

		public ControllerVisitor(ASTRewrite rewrite) {
			this.rewrite = rewrite;
		}
		
		public void setWidgetName(String widgetName) {
			this.widgetName = widgetName;
		}
		
		public String getContorllerName(){
			return contorllerName;
		}
		
		public boolean visit(Handler handler) {
			return true;
		}

		public boolean visit(ClassDataDeclaration classDataDeclaration) {
			if(classDataDeclaration.getType().getCanonicalName().equalsIgnoreCase(CONTROLLER_TYPE_NAME) && classDataDeclaration.hasSettingsBlock()){
				SettingsBlock settingsBlockOpt = classDataDeclaration.getSettingsBlockOpt();
				List settings = settingsBlockOpt.getSettings();
				for(int i=0; i<settings.size(); i++){
					Object oSetting = settings.get(i);
					if(oSetting instanceof SetValuesExpression){
						SetValuesExpression setValuesExpression = (SetValuesExpression)oSetting;
						if(setValuesExpression.getExpression().getCanonicalString().equalsIgnoreCase(MVC_PROPERTY_NAME)){
							SettingsBlock settingsBlock = setValuesExpression.getSettingsBlock();
							AssignmentLocator assignmentLocator = new AssignmentLocator(VIEW_PROPERTY_NAME);
							settingsBlock.accept(assignmentLocator);
							Assignment assignment = assignmentLocator.getAssignment();
							if (assignment != null) {
								String rightHandSide = assignment.getRightHandSide().getCanonicalString().trim();
								String rightHandSideFieldName = rightHandSide.split(" ")[0];
								if(rightHandSideFieldName.equalsIgnoreCase(widgetName)){
									contorllerName = ((SimpleName)classDataDeclaration.getNames().get(0)).getCanonicalName();
									rewrite.removeNode(classDataDeclaration);
									textEdit = rewrite.rewriteAST(currentDocument);
								}
							}
						};
					}
				}
			}
			return false;
		}
	}
	
	private class FormFieldVisitor extends DefaultASTVisitor {
		private String controllerName;
		private String nameLabelName;
		private String errorLabelName;
		
		private String deleteControllerPropertyName;
		private String deleteLabelPropertyName;
		
		public void setDeleteLabelPropertyName(String deleteLabelPropertyName){
			this.deleteLabelPropertyName = deleteLabelPropertyName; 
		}
		
		public void setDeleteControllerPropertyName(String deleteControllerPropertyName){
			this.deleteControllerPropertyName = deleteControllerPropertyName; 
		}

		public boolean visit(Handler handler) {
			return true;
		}
		
		public boolean visit(ClassDataDeclaration classDataDeclaration) {
			String theFormFieldName = classDataDeclaration.getType().getCanonicalName();
			if((theFormFieldName.equalsIgnoreCase(FORM_FIELD_TYPE_NAME) || theFormFieldName.equalsIgnoreCase(FORM_FIELD_TYPE_FULL_NAME)) && classDataDeclaration.hasSettingsBlock()){
				SettingsBlock settingsBlockOpt = classDataDeclaration.getSettingsBlockOpt();
				// get controller property
				AssignmentLocator controllerAssignmentLocator = new AssignmentLocator(CONTROLLER_PROPERTY_NAME);
				settingsBlockOpt.accept(controllerAssignmentLocator);
				Assignment controllerAssignment = controllerAssignmentLocator.getAssignment();
				if (controllerAssignment != null) {
					controllerName = controllerAssignment.getRightHandSide().getCanonicalString();
				}
				
				// get nameLabel property
				AssignmentLocator nameLabelAssignmentLocator = new AssignmentLocator(NAME_LABEL_PROPERTY_NAME);
				settingsBlockOpt.accept(nameLabelAssignmentLocator);
				Assignment nameLabelAssignment = nameLabelAssignmentLocator.getAssignment();
				if (nameLabelAssignment != null) {
					nameLabelName = nameLabelAssignment.getRightHandSide().getCanonicalString();
				}
				
				// get errorLabel property
				AssignmentLocator errorLabelAssignmentLocator = new AssignmentLocator(ERROR_LABEL_PROPERTY_NAME);
				settingsBlockOpt.accept(errorLabelAssignmentLocator);
				Assignment errorLabelAssignment = errorLabelAssignmentLocator.getAssignment();
				if (errorLabelAssignment != null) {
					errorLabelName = errorLabelAssignment.getRightHandSide().getCanonicalString();
				}
				
				String formFieldName = ((SimpleName)classDataDeclaration.getNames().get(0)).getCanonicalName();
				
				if(((controllerName != null && controllerName.equalsIgnoreCase(deleteControllerPropertyName)) ||
						(nameLabelName != null && nameLabelName.equalsIgnoreCase(deleteLabelPropertyName)) ||
						(errorLabelName != null && errorLabelName.equalsIgnoreCase(deleteLabelPropertyName)))){
					deleteFormFieldNames.add(formFieldName);
				}				
			}
			
			return false;
		}
	}
	
	
	private class FormManagerVisitor extends DefaultASTVisitor {
		public boolean visit(Handler handler) {
			return true;
		}
		
		public boolean visit(ClassDataDeclaration classDataDeclaration) {
			if (classDataDeclaration.getType().getCanonicalName().equalsIgnoreCase(FORM_MANAGER_TYPE_NAME) && classDataDeclaration.hasSettingsBlock()) {
				SettingsBlock settingsBlockOpt = classDataDeclaration.getSettingsBlockOpt();
				AssignmentLocator assignmentLocator = new AssignmentLocator(ENTRIES_PROPERTY_NAME);
				settingsBlockOpt.accept(assignmentLocator);

				
				currentFormManagerEntitiesAssignment = assignmentLocator.getAssignment();
				currentFormManagerEntitiesAssignment.getRightHandSide().accept(new DefaultASTVisitor(){
					public boolean visit(ArrayLiteral arrayLiteral) {
						List expressions = arrayLiteral.getExpressions();
						if (expressions.size() > 0) {
							for (int i = 0; i < expressions.size(); i++) {
								Node expression = (Node)expressions.get(i);
								if(expression instanceof SimpleName){
									SimpleName simpleName = (SimpleName)expression;
									if(deleteFormFieldNames.contains(simpleName.getCanonicalName())){
										formManagerEntitiesAssignment = currentFormManagerEntitiesAssignment;
									};
								}
							}
						}
						
						return false;
					}
				});		
				
				if (formManagerEntitiesAssignment != null) {
					formManagerEntitiesAssignment.getRightHandSide().accept(new DefaultASTVisitor(){
						public boolean visit(ArrayLiteral arrayLiteral) {
							List expressions = arrayLiteral.getExpressions();
							if (expressions.size() > 0) {
								for (int i = 0; i < expressions.size(); i++) {
									Node expression = (Node)expressions.get(i);
									if(expression instanceof SimpleName){
										SimpleName simpleName = (SimpleName)expression;
										oldFormFieldNames.add(simpleName.getCanonicalName());
									}
								}
							}
							
							return false;
						}
					});		
				}
			}
			return false;
		}
	}

	private class ChildrenRecursiveVisitor extends DefaultASTVisitor {
		private final IPart modelPart;
		private final ASTRewrite rewrite;

		public ChildrenRecursiveVisitor(IPart modelPart, ASTRewrite rewrite) {
			this.modelPart = modelPart;
			this.rewrite = rewrite;
		}

		public boolean visit(ClassDataDeclaration classDataDeclaration) {
			if (classDataDeclaration.hasSettingsBlock()) {
				SettingsBlock settingsBlockOpt = classDataDeclaration.getSettingsBlockOpt();
				AssignmentLocator assignmentLocator = new AssignmentLocator(CHILDREN_PROPERTY_NAME);
				settingsBlockOpt.accept(assignmentLocator);
				Assignment assignment = assignmentLocator.getAssignment();
				if (assignment != null) {
					assignment.getRightHandSide().accept(new DefaultASTVisitor() {
						public boolean visit(ArrayLiteral arrayLiteral) {
							try {
								List expressions = arrayLiteral.getExpressions();
								if (expressions.size() > 0) {
									for (int i = 0; i < expressions.size(); i++) {
										Object expression = expressions.get(i);
										if (expression instanceof SimpleName) {
											SimpleName simpleName = (SimpleName)expression;
											String childWidgetName = simpleName.getCanonicalName();
											IField[] fields = modelPart.getFields();
											for (int j = 0; j < fields.length; j++) {
												IField field = fields[j];
												if (childWidgetName.equalsIgnoreCase(field.getElementName())) {
													Node childWidgetNameNode = currentDocument.getNewModelNodeAtOffset(field.getNameRange().getOffset(),field.getNameRange().getLength());
													Node childWidgetDefinition = childWidgetNameNode.getParent();
													//process child widget
													childWidgetDefinition.accept(childrenVisitor);
													//process related controller
													controllerVisitor.setWidgetName(childWidgetName);
													childWidgetDefinition.getParent().accept(controllerVisitor);
													//process related formField
													formFieldVisitor.setDeleteLabelPropertyName(childWidgetName);
													formFieldVisitor.setDeleteControllerPropertyName(controllerVisitor.getContorllerName());
													childWidgetDefinition.getParent().accept(formFieldVisitor);
													//process related formManager
													childWidgetDefinition.getParent().accept(formManagerVisitor);
													//delete widget definition
													rewrite.removeNode(childWidgetDefinition);
													rewrite.rewriteAST(currentDocument);
													break;
												}
											}
										}
									}
								}
							} catch (Exception e) {
								Activator.getDefault().getLog().log(new Status(Status.ERROR,Activator.PLUGIN_ID,"Delete Widget Definition: Error deleting definition",e));
							}
							return false;
						}
					});
				}
			}
			return false;
		}
	}
}
