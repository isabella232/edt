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
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.internal.egl2mof.ElementGenerator;
import org.eclipse.edt.mof.egl.Type;

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
	 * Returns an ElementGenerator capable of creating alternate elements if the given AST node should be extended.
	 * For example a particular annotation on a NestedFunction might mean we want to create a new subclass of Function.
	 * The type created by the generator must be a subclass of the default IR type. Only AST types included in
	 * {@link #getExtendedTypes()} will be passed into this method.
	 * 
	 * @param node The original bound element.
	 * @see ElementGenerator
	 * @return an ElementGenerator, or null.
	 */
	ElementGenerator getElementGeneratorFor(Node node);
	
	/**
	 * Returns a validator for the given part, or null if doesn't require extra validation. Only AST types included
	 * in {@link #getExtendedTypes()} will be passed into this method. It is up to the compiler whether this validator
	 * is run before or after its own validation.
	 * 
	 * @param part The part to validate.
	 * @see PartValidator
	 * @return a validator for the given part, or null if no extra validation is required.
	 */
	PartValidator getValidatorFor(Part part);
	
	/**
	 * Returns a validator for the given statement, or null if it's not a type being extended. Only AST
	 * types included in {@link #getExtendedTypes()} will be passed into this method.
	 * 
	 * @param stmt The statement to validate.
	 * @see StatementValidator
	 * @return a validator for the given statement, or null if it's not a type being extended.
	 */
	StatementValidator getValidatorFor(Statement stmt);
	
	/**
	 * Returns a validator for the given type, or null if it's not a type being extended.
	 * 
	 * @param type The type to validate.
	 * @see TypeValidator
	 * @return a validator for the given type, or null if it's not a type being extended.
	 */
	TypeValidator getValidatorFor(Type type);
}
