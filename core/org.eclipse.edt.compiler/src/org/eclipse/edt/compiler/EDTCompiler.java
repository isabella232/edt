/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.lookup.BindingCreator;
import org.eclipse.edt.compiler.internal.core.validation.DefaultFunctionValidator;
import org.eclipse.edt.compiler.internal.core.validation.DefaultPartValidator;
import org.eclipse.edt.compiler.internal.core.validation.DefaultStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.DefaultTypeValidator;
import org.eclipse.edt.compiler.internal.egl2mof.DefaultElementGenerator;
import org.eclipse.edt.compiler.internal.egl2mof.ElementGenerator;

public class EDTCompiler extends BaseCompiler {
	
	@Override
	public String getSystemEnvironmentPath() {
		if (systemEnvironmentRootPath == null) {
			StringBuilder buf = new StringBuilder(100);
			buf.append(SystemLibraryUtil.getSystemLibraryPath(BindingCreator.class, "lib"));
			buf.append(File.pathSeparatorChar);
			buf.append(super.getSystemEnvironmentPath());
			systemEnvironmentRootPath = buf.toString();
		}
		return systemEnvironmentRootPath;
	}
		
	@Override
	public List<ASTValidator> getValidatorsFor(Node node) {
		List<ASTValidator> validators = super.getValidatorsFor(node);
		if (validators == null) {
			validators = new ArrayList<ASTValidator>(1);
		}
		
		// Extensions can contribute to, but not replace, part, type and function validation. Statement
		// validation, however, CAN be replaced completely. Default validators will always be run first.
		if (node instanceof Part) {
			validators.add(0, new DefaultPartValidator());
		}
		else if (node instanceof Type) {
			if (DefaultTypeValidator.isApplicableFor((Type)node)) {
				validators.add(0, new DefaultTypeValidator());
			}
		}
		else if (node instanceof NestedFunction || node instanceof Constructor) {
			validators.add(0, new DefaultFunctionValidator());
		}
		else if (validators.size() == 0 && node instanceof Statement) {
			validators.add(new DefaultStatementValidator());
		}
		
		return validators;
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
