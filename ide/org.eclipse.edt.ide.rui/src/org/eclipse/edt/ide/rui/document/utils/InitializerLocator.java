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

import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.NewExpression;

public class InitializerLocator extends DefaultASTVisitor{
	private NewExpression initializer = null;
	
	public boolean visit(NewExpression newExpression) {
		initializer = newExpression;
		return false;
	}
	
	public NewExpression getInitializer(){
		return initializer;
	}	
}
