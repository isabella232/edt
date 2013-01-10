/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.generation;

import org.eclipse.edt.compiler.internal.util.IGenerationResultsMessage;
import org.eclipse.edt.ide.core.internal.generation.PartWrapper;

public interface IGenerationResult {
	public static int DEBUG = 0;
	public static int TARGET = 1;
	
	public PartWrapper getPart();
  
	public IGenerationResultsMessage[] getMessages();
	
	public boolean hasErrors();
	public boolean hasWarnings();
	public int getType();
}
