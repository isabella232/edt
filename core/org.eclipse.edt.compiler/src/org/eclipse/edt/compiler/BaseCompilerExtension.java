/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
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

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.egl2mof.ElementGenerator;

public class BaseCompilerExtension implements ICompilerExtension {
	
	protected ICompiler compiler;
	
	@Override
	public String[] getSystemEnvironmentPaths() {
		return null;
	}
	
	@Override
	public Class[] getExtendedTypes() {
		return null;
	}
	
	@Override
	public ElementGenerator getElementGeneratorFor(Node node) {
		return null;
	}
	
	@Override
	public ASTValidator getValidatorFor(Node node) {
		return null;
	}
	
	@Override
	public void setCompiler(ICompiler compiler) {
		this.compiler = compiler;
	}
	
	@Override
	public ICompiler getCompiler() {
		return compiler;
	}
}
