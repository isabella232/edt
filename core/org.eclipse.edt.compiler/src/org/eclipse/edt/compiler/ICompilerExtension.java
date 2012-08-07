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
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Type;

//TODO need to be able to contribute error message resource bundles & IDs
public interface ICompilerExtension {
	/**
	 * @return the paths of eglars & mofars containing system parts
	 */
	String[] getSystemEnvironmentPaths();
	
	/**
	 * @return the AST types this compiler extension can extend, for example ForEachStatement.class.
	 */
	Class[] getExtendedTypes();
	
	/**
	 * Returns a new MOF element if the given AST node should be extended. For example a particular annotation
	 * on a NestedFunction might mean we want to return a new subclass of Function. The type returned must be a
	 * subclass of the default IR type. Only AST types included in {@link #getExtendedTypes()} will be passed into
	 * this method.
	 * 
	 * @param node The original bound element.
	 * @return a new MOF element, or null.
	 */
	Element createMofFor(Node node);
	
	/**
	 * Returns a validator for the given statement, or null if it's not a type being extended. Only AST
	 * types included in {@link #getExtendedTypes()} will be passed into this method.
	 * 
	 * @param stmt The statement to validate.
	 * @return a validator for the given statement, or null if it's not a type being extended.
	 */
	StatementValidator getValidatorFor(Statement stmt);
	
	/**
	 * Returns a validator for the given type, or null if it's not a type being extended.
	 * 
	 * @param type The type to validate.
	 * @return a validator for the given type, or null if it's not a type being extended.
	 */
	TypeValidator getValidatorFor(Type type);
}
