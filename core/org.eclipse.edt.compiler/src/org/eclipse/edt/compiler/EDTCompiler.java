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
package org.eclipse.edt.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.internal.core.lookup.BindingCreator;
import org.eclipse.edt.compiler.internal.core.validation.DefaultStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.CallStatementValidator;
import org.eclipse.edt.compiler.internal.egl2mof.eglx.jtopen.IBMiProgramCallStatementValidator;
import org.eclipse.edt.compiler.internal.egl2mof.eglx.persistence.sql.SQLActionStatementValidator;
import org.eclipse.edt.compiler.internal.egl2mof.eglx.services.ServicesActionStatementValidator;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.eglx.jtopen.IBMiCallStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlActionStatement;
import org.eclipse.edt.mof.eglx.services.ServicesCallStatement;

public class EDTCompiler extends BaseCompiler {
		
	
	static {
		StatementValidator.Registry.put(MofConversion.Type_SqlRecord, new DefaultStatementValidator());
		StatementValidator.Registry.put(MofConversion.EGL_lang_package, new DefaultStatementValidator());
		StatementValidator.Registry.put("eglx.persistence.sql", new SQLActionStatementValidator());
		StatementValidator.Registry.put("eglx.services", new ServicesActionStatementValidator());
		StatementValidator.Registry.put("eglx.jtopen", new IBMiProgramCallStatementValidator());
	}

	@Override
	public String getSystemEnvironmentPath() {
		
		if (systemEnvironmentRootPath == null) {

			String path = SystemEnvironmentUtil.getSystemLibraryPath(BindingCreator.class, "lib");
			path += File.pathSeparator;
			path += SystemEnvironmentUtil.getSystemLibraryPath(SqlActionStatement.class, "egllib");
			path += File.pathSeparator;
			path += SystemEnvironmentUtil.getSystemLibraryPath(ServicesCallStatement.class, "egllib");
			path += File.pathSeparator;
			path += SystemEnvironmentUtil.getSystemLibraryPath(IBMiCallStatement.class, "egllib");
			path += File.pathSeparator;
			systemEnvironmentRootPath = path + super.getSystemEnvironmentPath();
		}
		
		return systemEnvironmentRootPath;
	}
	
	@Override
	public List<String> getImplicitlyUsedEnumerations() {
		
		List<String> implicitlyUsedEnumerations = new ArrayList<String>();
        implicitlyUsedEnumerations.add(InternUtil.intern("AlignKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("CaseFormatKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("ColorKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("ConvertDirection"));
        implicitlyUsedEnumerations.add(InternUtil.intern("HighlightKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("IntensityKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("LineWrapKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("EventKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("WindowAttributeKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("DataSource"));
        implicitlyUsedEnumerations.add(InternUtil.intern("ExportFormat"));
        implicitlyUsedEnumerations.add(InternUtil.intern("CommitScopeKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("DisconnectKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("IsolationLevelKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("CommitControlKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("ConsoleEventKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("PortletModeKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("SessionScopeKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("WindowStateKind"));
        implicitlyUsedEnumerations.add(InternUtil.intern("SecretKind"));
		return implicitlyUsedEnumerations;
	}
	
	@Override
	public List<String> getAllImplicitlyUsedEnumerations() {
		List<String> all = new ArrayList<String>();
		all.addAll(super.getAllImplicitlyUsedEnumerations());
		all.addAll(getImplicitlyUsedEnumerations());
		return all;
	}
	
	@Override
	public StatementValidator getValidatorFor(Statement stmt) {
		// Lookup statement validator based on DataSource operand in FROM/TO clause
		// TODO this is not properly generalized yet
		final StatementValidator[] validator = new StatementValidator[1];
		stmt.accept(new AbstractASTExpressionVisitor() {
			public boolean visit(org.eclipse.edt.compiler.core.ast.ForEachStatement forEachStatement) {
				if (validator[0] == null) {
					if (forEachStatement.getVariableDeclarationName() != null) {
						validator[0] = StatementValidator.Registry.get(MofConversion.EGL_lang_package);
						return false;
					}
					return true;
				}
				return false;
			};
			public boolean visit(org.eclipse.edt.compiler.core.ast.FromOrToExpressionClause clause) {
				if (validator[0] == null && clause.getExpression() != null) {
					ITypeBinding type = clause.getExpression().resolveTypeBinding();
					if (type != null && type.getPackageName() != null) {
						String key = IRUtils.concatWithSeparator(type.getPackageName(), ".");
						validator[0] = StatementValidator.Registry.get(key);
					}
				}
				return false;
			}
		});
		
		if (validator[0] == null && !(stmt instanceof org.eclipse.edt.compiler.core.ast.CallStatement)) {
			return StatementValidator.Registry.get("eglx.persistence.sql");
		}
		else if (validator[0] == null && stmt instanceof org.eclipse.edt.compiler.core.ast.CallStatement) {
			CallStatement call = (CallStatement) stmt;
			if (CallStatementValidator.isFunctionCallStatement(call)) {
				if (CallStatementValidator.isLocalFunctionCallStatement(call)) {
					return StatementValidator.Registry.get("eglx.jtopen");
				}
				else {
					return StatementValidator.Registry.get("eglx.services");
				}
			}
			else {
				return StatementValidator.Registry.get(MofConversion.EGL_lang_package);
			}
		}
		else {
			return validator[0];
		}
	}
}
