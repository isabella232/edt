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

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.internal.core.lookup.BindingCreator;
import org.eclipse.edt.compiler.internal.core.validation.DefaultPartValidator;
import org.eclipse.edt.compiler.internal.core.validation.DefaultStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.DefaultTypeValidator;
import org.eclipse.edt.compiler.internal.egl2mof.DefaultElementGenerator;
import org.eclipse.edt.compiler.internal.egl2mof.ElementGenerator;
import org.eclipse.edt.mof.egl.Type;

public class EDTCompiler extends BaseCompiler {
	
	@Override
	public String getSystemEnvironmentPath() {
		if (systemEnvironmentRootPath == null) {
			StringBuilder buf = new StringBuilder(100);
			buf.append(SystemEnvironmentUtil.getSystemLibraryPath(BindingCreator.class, "lib"));
			buf.append(File.pathSeparatorChar);
			buf.append(super.getSystemEnvironmentPath());
			systemEnvironmentRootPath = buf.toString();
		}
		return systemEnvironmentRootPath;
	}
	
	@Override
	public List<String> getImplicitlyUsedEnumerations() {
		
		List<String> implicitlyUsedEnumerations = new ArrayList<String>();
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
		StatementValidator validator = super.getValidatorFor(stmt);
		if (validator != null) {
			return validator;
		}
		return new DefaultStatementValidator();
	}
	
	@Override
	public List<PartValidator> getValidatorsFor(org.eclipse.edt.compiler.core.ast.Part part) {
		List<PartValidator> validators = super.getValidatorsFor(part);
		if (validators == null) {
			validators = new ArrayList<PartValidator>(1);
		}
		
		// default gets run first.
		validators.add(0, new DefaultPartValidator());
		return validators;
	}
	
	@Override
	public TypeValidator getValidatorFor(Type type) {
		TypeValidator validator = super.getValidatorFor( type );
		if (validator != null) {
			return validator;
		}
		
		return new DefaultTypeValidator();
	}
	
	@Override
	public ElementGenerator getElementGeneratorFor(Node node) {
		ElementGenerator generator = super.getElementGeneratorFor( node );
		if (generator != null) {
			return generator;
		}
		
		return new DefaultElementGenerator();
	}
}
