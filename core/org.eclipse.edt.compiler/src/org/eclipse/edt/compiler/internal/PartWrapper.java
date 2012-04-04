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
package org.eclipse.edt.compiler.internal;

import org.eclipse.core.resources.IFile;

import com.ibm.icu.text.Collator;

public class PartWrapper implements Comparable,IEGLPartWrapper {
	public static final String NO_VALUE_SET = ""; //$NON-NLS-1$
	
	private String partName = NO_VALUE_SET;
	private String partPath = NO_VALUE_SET;
	
	public PartWrapper() {
		super();
	}
	
	public PartWrapper(String partName, IFile partPath) {
		super();
		
		setPartName(partName);
		setPartPath(partPath);
	}

	/**
	 * Returns the partName.
	 * @return String
	 */
	public String getPartName() {
		return partName;
	}

	/**
	 * Returns the partPath.
	 * @return String
	 */
	public String getPartPath() {
		return partPath;
	}

	/**
	 * Sets the partName.
	 * @param partName The partName to set
	 */
	public void setPartName(String partName) {
		
		if(partName == null)
		{
			this.partName = NO_VALUE_SET;
		}
		else
		{
			this.partName = partName;
		}
	}

	/**
	 * Sets the partPath.
	 * @param partPath The partPath to set
	 */
	public void setPartPath(String partPath) {
		
		if(partPath == null)
		{
			this.partPath = NO_VALUE_SET;
		}
		else
		{
			this.partPath = partPath;
		}
	}
	
	/**
	 * Set the partPath with a file.
	 */
	public void setPartPath(IFile partPath)
	{
		if(partPath == null)
		{
			this.partPath = NO_VALUE_SET;
		}
		else
		{
			this.partPath = partPath.getFullPath().makeRelative().toString();
		}
	}
	
	/**
	 * Compare the names ignoring case.  If the names are the same,
	 * compare the paths using case.
	 * @see java.lang.Comparable#compareTo(Object)
	 */
	public int compareTo(Object o) {

		int result = -1;
		
		if(o instanceof PartWrapper)
		{
			Collator collator = Collator.getInstance();
			
			// we want to ignore case on this comparison, so set the weight
			// on the collator to Secondary
			collator.setStrength(Collator.SECONDARY);
			result = collator.compare(partName, ((PartWrapper)o).getPartName());
			
			if(result == 0)
			{
				//compare the paths
				result = partPath.compareTo(((PartWrapper)o).getPartPath());
			}
		}
		
		return result;
	}

}
