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
package org.eclipse.edt.ide.core;

import org.eclipse.core.resources.IProject;

/**
 * Enables clients to run a generation request for a particular generator. The implementation
 * should take care of any setup required for the generator to run. It should also be stateless, in that
 * the same instance can be used to run any number of generations, possibly at the same time.
 */
public interface IGenerator extends org.eclipse.edt.compiler.IGenerator {
				
	/**
	 * @return true if this generator supports the given project.
	 */
	public boolean supportsProject(IProject project);
	
	/**
	 * @return an array of runtime containers provided by this generator; this may return null.
	 */
	public EDTRuntimeContainer[] getRuntimeContainers();
			
}
