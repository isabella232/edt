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

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 
 * A FileInfoDifferencer is used to compare two different FileInfo objects and report the changes between the two
 * 
 * @author svihovec
 *
 */
public class FileInfoDifferencer {
	
	private IFileInfoDifferenceNotificationRequestor requestor;
	
	public FileInfoDifferencer(IFileInfoDifferenceNotificationRequestor requestor){
		this.requestor = requestor;
	}
	
	/**
	 * Find differences between the previous FileInfo and the current FileInfo.
	 * 
	 * @param previousVersion
	 * @param currentVersion
	 */
	synchronized public void findDifferences(IFileInfo previousVersion, IFileInfo currentVersion){
		Set previousVersionPartNames = new LinkedHashSet(previousVersion.getPartNames());
		Set currentVersionPartNames = currentVersion.getPartNames();
		
		for (Iterator iter = currentVersionPartNames.iterator(); iter.hasNext();) {
			String	partName = (String) iter.next();
			
			if(previousVersionPartNames.contains(partName)){
				previousVersionPartNames.remove(partName);
				if((previousVersion.getPartRange(partName).getLength() != currentVersion.getPartRange(partName).getLength()) ||
					(previousVersion.getPartRange(partName).getOffset() != currentVersion.getPartRange(partName).getOffset())){
					requestor.partChanged(partName);
				}else if(!Arrays.equals(previousVersion.getMD5Key(partName), currentVersion.getMD5Key(partName))){
					requestor.partChanged(partName);
				}
			}else{
				requestor.partAdded(partName);
			}
		}
		
		for (Iterator iter = previousVersionPartNames.iterator(); iter.hasNext();) {
			String partName = (String) iter.next();
			requestor.partRemoved(partName);
		}
	}
}
