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
package org.eclipse.edt.compiler.internal.util;

import java.io.File;
  
public interface ICompilationListener {
	void acceptLevel01Compile(File file, String[] pkg, String partName);
	void acceptLevel02Compile(File file, String[] pkg, String partName);
	void acceptLevel03Compile(File file, String[] pkg, String partName);
	
	boolean acceptsLevel01Compiles();
	boolean acceptsLevel02Compiles();
	boolean acceptsLevel03Compiles();
	
	boolean isLevel03Compiled(File file, String[] pkg, String partName);
	
	void initialize();
}
