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
package org.eclipse.edt.ide.core.internal.builder;

import java.io.IOException;
import java.io.ObjectInputStream;

import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author svihovec
 *
 */
public class BuildManagerPartChange extends BuildManagerChange {

	private String[] packageName;
	private String partName;
	private int partType;
	
	public BuildManagerPartChange(String[] packageName, String partName, int partType){
		this.packageName = packageName;
		this.partName = partName;
		this.partType = partType;
	}
	
	public boolean isPart() {
		return true;
	}
	
	public String[] getPackageName(){
		return packageName;
	}
	
	public String getPartName(){
		return partName;
	}
	
	public int hashCode() {
		return partName.hashCode();
	}
	
	public boolean equals(Object obj) {
		if(obj == this){
			return true;
		}
		
		if(obj instanceof BuildManagerPartChange){
			return ((BuildManagerPartChange)obj).packageName == packageName && ((BuildManagerPartChange)obj).partName == partName && ((BuildManagerPartChange)obj).partType == partType;
		}
		return false;
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		packageName = InternUtil.intern(packageName);
		partName = InternUtil.intern(partName);
	}

	public int getPartType() {
		return partType;
	}
}
