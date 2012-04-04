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
/**
 * 
 */
package org.eclipse.edt.compiler.generationServer.parts;

/**
 * This interface provides methods to return EGL Part information.
 * 
 * IPartInfo objects can be obtained the PartInfoFactory class.
 * 
 * @see com.ibm.etools.egl.ui.parts.PartInfoFactory
 *
 */
public interface IPartInfo extends IElementInfo{

	
	/**
	 * @return Returns an array of all the parts that are referenced within this part's scope. 
	 * 
	 *  
	 */
	IPartInfo[] getAllReferencedParts();
	
	/**
	 * @param Adds a referenced part
	 */
	void addReferencedPart(IPartInfo partInfo);
	
	void addReferencedElement(IElementInfo elementInfo);
	
	/**
	 * @return Returns the packagename of the part. The name will be a '.' delimited String of packages
	 */
	String getPackageName();
	
	/**
	 * @return Returns the full pathname of the file that the part is defined in.
	 */
	String getResourceName();
	
	/**
	 * @return Returns the EGL source, starting at the given offset, for the given length.
	 */
	String getSource(int offset, int len);
	
	/**
	 * @return Returns an array of parts that are directly referenced by this part 
	 */
	IPartInfo[] getReferencedParts();

	/**
	 * @return Returns an array of elements that are directly referenced by this part 
	 */
	IElementInfo[] getReferencedElements();
	
	
	/**
	 * @return Returns the subType name of the part (or null if it does not have a subType) 
	 */
	String getSubType();


}
