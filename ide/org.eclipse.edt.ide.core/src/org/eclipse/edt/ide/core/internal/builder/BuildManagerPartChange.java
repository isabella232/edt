/*******************************************************************************
 * Copyright © 2005, 2013 IBM Corporation and others.
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

import org.eclipse.edt.mof.utils.NameUtile;


/**
 * @author svihovec
 *
 */
public class BuildManagerPartChange extends BuildManagerChange {

	private static final long serialVersionUID = 1L;
	
	private String packageName;
	private String partName;
	private int partType;
	
	public BuildManagerPartChange(String packageName, String partName, int partType){
		this.packageName = packageName;
		this.partName = partName;
		this.partType = partType;
	}
	
	public boolean isPart() {
		return true;
	}
	
	public String getPackageName(){
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
			return NameUtile.equals(((BuildManagerPartChange)obj).packageName, packageName)
					&& NameUtile.equals(((BuildManagerPartChange)obj).partName, partName) && ((BuildManagerPartChange)obj).partType == partType;
		}
		return false;
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		packageName = NameUtile.getAsName(packageName);
		partName = NameUtile.getAsName(partName);
	}

	public int getPartType() {
		return partType;
	}
}
