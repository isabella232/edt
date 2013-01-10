/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.search;

import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.ide.core.internal.model.Util;
import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;

/**
 * A <tt>IFilePartInfo</tt> represents a type in a class or java file.
 */
public class PartDeclarationInfo extends IFilePartInfo {
	
	public PartDeclarationInfo(String pkg, String name, char[][] enclosingTypes, String project, String sourceFolder, String file, String ext, char partType) {
		super(pkg, name, enclosingTypes, project, sourceFolder, file, ext, partType);
	}
	
	public PartDeclarationInfo(String pkg, String name, char[][] enclosingTypes, String project, String containerLocation, String sourceFolder, String file, String ext, char partType, boolean isExternal){
		super(pkg, name, enclosingTypes, project, containerLocation, sourceFolder, file, ext, partType, isExternal);
	}
	
	public int getElementType() {
		return PartInfo.PART_DECL_INFO;
	}
	
	protected IEGLElement getEGLElement(IEGLSearchScope scope) {
		Object file = super.getEGLElement(scope);
		
		if(file instanceof IEGLFile)
		{
			if(fEnclosingNames.length > 0)
			{
				// get the first entry from the file
				IPart part = ((IEGLFile)file).getPart(Util.toString(fEnclosingNames[0]));
				
				// get all other enclosing parts from that part
				for (int i = 1; i < fEnclosingNames.length; i++) {
					part = part.getPart(Util.toString(fEnclosingNames[i]));
				}
				
				// get the part we are looking for from the innermost enclosing part
				part = part.getPart(fName);
				return part; 
			}
			else
			{
				return ((IEGLFile)file).getPart(fName);
			}
		} else if(file instanceof IClassFile) {
			if(fEnclosingNames.length > 0)
			{
				// get the first entry from the file
				IPart part = ((IClassFile)file).getPart(Util.toString(fEnclosingNames[0]));
				
				// get all other enclosing parts from that part
				for (int i = 1; i < fEnclosingNames.length; i++) {
					part = part.getPart(Util.toString(fEnclosingNames[i]));
				}
				
				// get the part we are looking for from the innermost enclosing part
				part = part.getPart(fName);
				return part; 
			}
			else{
				return ((IClassFile) file).getPart();
			}
		}
		
		return null;
	}
	
	public boolean isBinary(){
		if(getExtension().endsWith(IEGLConstants.EGL)){
			return false;
		} else if(getExtension().endsWith("eglar") || getExtension().endsWith("eglxml")){
			return true;
		}
		return false;
	}
}
