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
package org.eclipse.edt.gen.java.annotation.templates;


import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.Constants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.AssignmentStatement;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.PartName;
import org.eclipse.edt.mof.egl.QualifiedFunctionInvocation;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.egl.StringLiteral;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class ResourceTemplate extends JavaTemplate {

	public void genAnnotation(AnnotationType aType, Context ctx, TabbedWriter out, Annotation annot, Field field) {
		Object processed = ctx.getAttribute(field, Constants.SubKey_fieldsProcessed4Resource);
		if(processed == null){
			//add an initialzer to the function 
			AssignmentStatement assignmentStatement = ctx.getFactory().createAssignmentStatement();
			if (field.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				assignmentStatement.addAnnotation(field.getAnnotation(IEGLConstants.EGL_LOCATION));
			assignmentStatement.setContainer(field.getContainer());
			Assignment assignment = factory.createAssignment();
			if (field.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				assignment.addAnnotation(field.getAnnotation(IEGLConstants.EGL_LOCATION));
			assignmentStatement.setAssignment(assignment);
			MemberName nameExpression = factory.createMemberName();
			if (field.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				nameExpression.addAnnotation(field.getAnnotation(IEGLConstants.EGL_LOCATION));
			nameExpression.setMember(field);
			nameExpression.setId(field.getName());
			assignment.setLHS(nameExpression);
			assignment.setRHS(getLibraryInvocation(annot, field));
			// add the assignment to the declaration statement block
			StatementBlock declarationBlock = field.getInitializerStatements();
			if(declarationBlock == null){
				declarationBlock = factory.createStatementBlock();
				declarationBlock.setContainer(field.getContainer());
				field.setInitializerStatements(declarationBlock);
			}
			declarationBlock.getStatements().add(0, assignmentStatement);
			// add the declaration statement block to the field
			ctx.putAttribute(field, Constants.SubKey_fieldsProcessed4Resource, Boolean.TRUE);
		}
	}
	private QualifiedFunctionInvocation getLibraryInvocation( Annotation annot, Field field) {
		ExternalType serviceLib = (ExternalType)TypeUtils.getType(TypeUtils.EGL_KeyScheme + Constants.LibrarySys).clone();
		QualifiedFunctionInvocation invocation = factory.createQualifiedFunctionInvocation();
		PartName partName  = factory.createPartName();
		partName.setType(serviceLib);
		invocation.setId("getResource");
		invocation.setQualifier(partName);
		StringLiteral bindingKey = factory.createStringLiteral();
		invocation.getArguments().add(bindingKey);
		if(annot.getValue("bindingkey") instanceof String && !((String)annot.getValue("bindingkey")).isEmpty()){
			bindingKey.setValue((String)annot.getValue("bindingkey"));
		}
		else{
			bindingKey.setValue(field.getName());
		}
		if(annot.getValue("propertyFileName") != null){
			StringLiteral deploymentDescriptorName = factory.createStringLiteral();
			invocation.getArguments().add(deploymentDescriptorName);
			deploymentDescriptorName.setValue((String)annot.getValue("propertyFileName"));
		}
		return invocation;
	}
}
