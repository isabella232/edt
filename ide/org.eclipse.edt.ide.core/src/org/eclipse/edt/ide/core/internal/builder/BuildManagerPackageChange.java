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

import org.eclipse.edt.mof.utils.NameUtile;


/**
 * @author svihovec
 *
 */
public class BuildManagerPackageChange extends BuildManagerChange {

	private static final long serialVersionUID = 1L;
	
	private String packageName;
	
	public BuildManagerPackageChange(String packageName){
		this.packageName = packageName;
	}
	
	public boolean isPackage() {
		return true;
	}
	
	public String getPackageName(){
		return packageName;
	}
	
	public int hashCode() {
		return packageName.hashCode();
	}
	
	public boolean equals(Object obj) {
		if(obj == this){
			return true;
		}
		
		if(obj instanceof BuildManagerPackageChange){
			return NameUtile.equals(((BuildManagerPackageChange)obj).packageName, packageName);
		}
		return false;
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		packageName = NameUtile.getAsName(packageName);
	}
}
