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
package org.eclipse.edt.ide.core.internal.dependency;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author svihovec
 *
 */
/*package*/ class Function implements IDependencyGraphValue{

    private String projectName;
	private QualifiedName packageName;
	private SimpleName partName;
	
	public Function(String projectName, String packageName, String partName) {
	    this.projectName = projectName;
		this.packageName = new QualifiedName(packageName);
		this.partName = new SimpleName(partName);
	}
	
	public Function() {}
	
	public String getProjectName(){
	    return projectName;
	}
	
	public String getPackageName(){
		return packageName.getQualifiedName();
	}
	
	public String getPartName(){
		return partName.getSimpleName();
	}
	
	public String toString(){
		return projectName + "." + packageName + "." + partName; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public boolean equals(Object obj) {
		if(obj == this){
			return true;
		}
		if(obj instanceof Function){
			return ((Function)obj).projectName.equals(projectName) && ((Function)obj).packageName.equals(packageName) && ((Function)obj).partName.equals(partName);
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
		outputStream.writeUTF(projectName);
		packageName.serialize(outputStream);
		partName.serialize(outputStream);
	}
	
	public void deserialize(DataInputStream inputStream) throws IOException{
		projectName = inputStream.readUTF();
		
		packageName = new QualifiedName();
		packageName.deserialize(inputStream);
		
		partName = new SimpleName();
		partName.deserialize(inputStream);
	}
	
	public int getKind() {
		return IDependencyGraphValue.FUNCTION;
	}
}
