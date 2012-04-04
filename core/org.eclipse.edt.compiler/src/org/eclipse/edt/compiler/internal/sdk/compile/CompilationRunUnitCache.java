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
package org.eclipse.edt.compiler.internal.sdk.compile;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.edt.compiler.internal.util.ICompilationListener;
  

public class CompilationRunUnitCache implements ICompilationListener {
	
	Set level03Compiles = new HashSet();

	public void acceptLevel01Compile(File file, String[] pkg, String partName) {
	}

	public void acceptLevel02Compile(File file, String[] pkg, String partName) {
	}

	public void acceptLevel03Compile(File file, String[] pkg, String partName) {
		level03Compiles.add(getKey(file, partName));
	}
	
	public boolean isLevel03Compiled(File file, String[] pkg, String partName) {
		return level03Compiles.contains(getKey(file, partName));
	}
	
	private String getKey(File file, String partName) {
		
		try {
			return file.getAbsoluteFile().getCanonicalPath() + " " + partName.toLowerCase();
		} catch (IOException e) {
			return file.getAbsolutePath().toLowerCase() + " " + partName.toLowerCase();
		}
		
	}

	public boolean acceptsLevel01Compiles() {
		return false;
	}

	public boolean acceptsLevel02Compiles() {
		return false;
	}

	public boolean acceptsLevel03Compiles() {
		return true;
	}

	public void initialize() {
		level03Compiles = new HashSet();
	}

}
