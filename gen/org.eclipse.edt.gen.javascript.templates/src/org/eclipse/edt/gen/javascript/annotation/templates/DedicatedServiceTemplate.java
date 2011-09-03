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
package org.eclipse.edt.gen.javascript.annotation.templates;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.AssignmentStatement;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.LHSExpr;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.NewExpression;
import org.eclipse.edt.mof.egl.PartName;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.egl.StringLiteral;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;

public class DedicatedServiceTemplate extends JavaScriptTemplate {

	public void genDefaultValue(AnnotationType type, Context ctx, TabbedWriter out, Annotation annot, Field field) throws MofObjectNotFoundException, DeserializationException {
		String serviceName = (String)annot.getValue("serviceName");
		if(serviceName == null){
			serviceName = field.getType().getTypeSignature();
		}
		Annotation eglLocation = field.getAnnotation(IEGLConstants.EGL_LOCATION);
		Container container = field.getContainer();

		Record httpRecordType = (Record)TypeUtils.getType(TypeUtils.EGL_KeyScheme + Constants.PartHttp).clone();
		
		NewExpression newExpr = ctx.getFactory().createNewExpression();
		if (eglLocation != null)
			newExpr.addAnnotation(eglLocation);
		newExpr.setId(Constants.PartHttp);
		ctx.invoke(genNewExpression, newExpr, ctx, out);
		out.println(";");
		
		StatementBlock stmtBlock = ctx.getFactory().createStatementBlock();
		if (eglLocation != null)
			stmtBlock.addAnnotation(eglLocation);
		stmtBlock.setContainer(field.getContainer());

		// we need to create the member access
		MemberName httpRecordMemberName = factory.createMemberName();
		if (eglLocation != null)
			httpRecordMemberName.addAnnotation(eglLocation);
		httpRecordMemberName.setMember(field);
		httpRecordMemberName.setId(field.getId());

		//<field>.invocationType = ServiceType.EglDedicated;
		Field httpRecordField = getField(httpRecordType, "invocationType", eglLocation);
		stmtBlock.getStatements().add(createAssignment(container, 
				createFieldMemberAccess( httpRecordMemberName, httpRecordField, eglLocation),
				createEnumerationEntry(httpRecordField.getType(), "egldedicated"), 
				eglLocation));		

		//<field>.request.uri = "services.HelloWorld";
		Field httpRecordRequestField = getField(httpRecordType, "request", eglLocation);
		MemberAccess httpRecordRequestFieldMemberAccess = createFieldMemberAccess(httpRecordMemberName, httpRecordRequestField, eglLocation);
		
		Field httpRecordRequestUriField = getField((Record)httpRecordRequestField.getType(), "uri", eglLocation);
		StringLiteral stringLiteral = ctx.getFactory().createStringLiteral();
		stringLiteral.setValue(serviceName);
		stmtBlock.getStatements().add(createAssignment(container, 
				createFieldMemberAccess(httpRecordRequestFieldMemberAccess, httpRecordRequestUriField, eglLocation),
				stringLiteral, 
				eglLocation));		

		ctx.invoke(genStatementBodyNoBraces, stmtBlock, ctx, out);
	}

	private Field getField(Record record, String fieldName, Annotation eglLocation){
		Field field = (Field)record.getField(fieldName);
		if (eglLocation != null)
			field.addAnnotation(eglLocation);
		return field;
	}
	private MemberAccess createEnumerationEntry(Type enumerationType, String id) {
		PartName partName  = factory.createPartName();
		partName.setType(enumerationType);
		MemberAccess enumEntry = factory.createMemberAccess();
		enumEntry.setId(id);
		enumEntry.setQualifier(partName);
		return enumEntry;
	}

	private MemberAccess createFieldMemberAccess( Expression qualifier, Field targetField, Annotation eglLocation) {
		MemberAccess memberAccess = factory.createMemberAccess();
		if (eglLocation != null)
			memberAccess.addAnnotation(eglLocation);
		memberAccess.setQualifier(qualifier);
		memberAccess.setId(targetField.getName());
		memberAccess.setMember(targetField);
		return memberAccess;
	}

	private AssignmentStatement createAssignment(Container container, LHSExpr lhsExpr, Expression rhsExpr, Annotation eglLocation) {
		AssignmentStatement assignmentStatement = factory.createAssignmentStatement();
		if (eglLocation != null)
			assignmentStatement.addAnnotation(eglLocation);
		assignmentStatement.setContainer(container);
		Assignment assignment = factory.createAssignment();
		if (eglLocation != null)
			assignment.addAnnotation(eglLocation);
		assignmentStatement.setAssignment(assignment);
		assignment.setLHS(lhsExpr);
		assignment.setRHS(rhsExpr);
		return assignmentStatement;
	}
}
