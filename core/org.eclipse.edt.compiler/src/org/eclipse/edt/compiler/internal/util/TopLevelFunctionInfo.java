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
package org.eclipse.edt.compiler.internal.util;

import org.eclipse.edt.compiler.core.ast.TopLevelFunction;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;


public class TopLevelFunctionInfo {
	
	private String fileName;
	private TopLevelFunction boundAst;
	private IProblemRequestor problemRequestor;
	private Object sourceResolver;

	
	public TopLevelFunctionInfo(TopLevelFunction ast, String name, IProblemRequestor problemRequestor) {
		this(ast, name, problemRequestor, null);
	}

	public TopLevelFunctionInfo(TopLevelFunction ast, String name, IProblemRequestor problemRequestor, Object sourceResolver) {
		super();
		boundAst = ast;
		fileName = name;
		this.problemRequestor = problemRequestor;
		this.sourceResolver = sourceResolver;
	}
	
	
	public TopLevelFunction getBoundAst() {
		return boundAst;
	}
	public void setBoundAst(TopLevelFunction boundAst) {
		this.boundAst = boundAst;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public IProblemRequestor getProblemRequestor() {
		return problemRequestor;
	}
	public void setProblemRequestor(IProblemRequestor problemRequestor) {
		this.problemRequestor = problemRequestor;
	}

	public Object getSourceResolver() {
		return sourceResolver;
	}
	

}
