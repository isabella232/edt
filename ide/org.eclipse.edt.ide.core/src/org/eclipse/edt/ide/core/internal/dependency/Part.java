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
package org.eclipse.edt.ide.core.internal.dependency;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author svihovec
 *
 */
/*package*/ class Part implements IDependencyGraphValue{

	private QualifiedName packageName;
	private SimpleName partName;
	
	public Part(String packageName, String partName) {
		this.packageName = new QualifiedName(packageName);
		this.partName = new SimpleName(partName);
		
	}
	
	public Part() {}

	public String getPackageName(){
		return packageName.getQualifiedName();
	}
	
	public String getPartName(){
		return partName.getSimpleName();
	}
	
	public String toString(){
		return packageName + "." + partName; //$NON-NLS-1$
	}
	
	public boolean equals(Object obj) {
		if(obj == this){
			return true;
		}
		if(obj instanceof Part){
			return ((Part)obj).packageName.equals(packageName) && ((Part)obj).partName.equals(partName);
		}
		return false;
	}
	
	public int hashCode() {
		return partName.hashCode();
	}
	
	public int getNormalizedHashCode(){
		return partName.getNormalizedHashCode();
	}

	public void serialize(DataOutputStream outputStream) throws IOException {
		packageName.serialize(outputStream);
		partName.serialize(outputStream);
	}

	public void deserialize(DataInputStream inputStream) throws IOException {
		packageName = new QualifiedName();
		packageName.deserialize(inputStream);
		
		partName = new SimpleName();
		partName.deserialize(inputStream);		
	}
	
	public int getKind() {
		return IDependencyGraphValue.PART;
	}
}
