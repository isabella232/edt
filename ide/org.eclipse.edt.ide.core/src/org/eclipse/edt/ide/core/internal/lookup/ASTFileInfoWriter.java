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

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;

/**
 * Write the specified ASTileInfo to the disk.
 * 
 * The format of the output file is:
 * 
 * Number of Lines
 * Offsets of Lines
 * Number of Parts
 * partName, caseSensitivePartName, partType, partOffset, partLength, MD5key
 * partName, caseSensitivePartName, partType, partOffset, partLength, MD5key
 * ...
 * 
 * @author svihovec
 *
 */
 public class ASTFileInfoWriter {

	public static void writeFileInfo(IFileInfo fileInfo, IPath fileInfoPath) {
		
		try {
			fileInfoPath.removeLastSegments(1).toFile().mkdirs();
			
			DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileInfoPath.toFile())));
			
			try {
				int numLines = fileInfo.getNumberOfLines();
				dataOutputStream.writeInt(numLines);
				
				for(int i=0; i<numLines; i++){
					dataOutputStream.writeInt(fileInfo.getOffsetForLine(i));
				}
				
				dataOutputStream.writeUTF(fileInfo.getCaseSensitivePackageName());
				
				Set partNames = fileInfo.getPartNames();
				dataOutputStream.writeInt(partNames.size());
				
				// write part info
				for (Iterator iter = partNames.iterator(); iter.hasNext();) {
					String partName = (String)iter.next();
					dataOutputStream.writeUTF(partName);
					dataOutputStream.writeUTF(fileInfo.getCaseSensitivePartName(partName));
					
					dataOutputStream.writeInt(fileInfo.getPartType(partName));
					
					ISourceRange partRange = fileInfo.getPartRange(partName);
					dataOutputStream.writeInt(partRange.getOffset());
					dataOutputStream.writeInt(partRange.getLength());

					byte[] md5Key = fileInfo.getMD5Key(partName);
					
					dataOutputStream.writeInt(md5Key.length);
					dataOutputStream.write(md5Key);		
				}
			}finally{
				dataOutputStream.close();
			}
		}catch (IOException e) {
			throw new BuildException("Error writing FileInfo", e); //$NON-NLS-1$
		}	
	}
}
