/*******************************************************************************
 * Copyright © 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.lookup;

import java.util.Set;


/**
 * 
 * An Info is used to quickly retrieve information about an EGL Element.
 * 
 * @author svihovec
 *
 */
public interface IFileInfo {
	
	public Set getPartNames();

	public String getCaseSensitivePackageName();
	
	public ISourceRange getPartRange(String partName);
	
	public int getPartType(String partName);
	
	public String getCaseSensitivePartName(String partName);
	
	public byte[] getMD5Key(String partName);
	
	public int getLineNumberForOffset(int offset);
	
	public int getNumberOfLines();
	
	public int getOffsetForLine(int line);
}
