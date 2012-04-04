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

import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.mof.egl.utils.InternUtil;

/**
 * @author svihovec
 *
 */
/*package*/ class QualifiedName implements IDependencyGraphValue{

	private String[] qualifiedName;
	
	public QualifiedName(String[] qualifiedName) {
		this.qualifiedName = qualifiedName;
	}

	public QualifiedName() {}
	
	public String[] getQualifiedName(){
		return qualifiedName;
	}
	
	public String toString(){
		return Util.stringArrayToPath(qualifiedName).toString().replace(IPath.SEPARATOR, '.');
	}
	
	
	public boolean equals(Object obj) {
	    if(this == obj){
			return true;
		}
		if(obj instanceof QualifiedName){
		    return qualifiedName == ((QualifiedName)obj).qualifiedName;
		}
		return false;
	}
	
	public int hashCode() {
		int result = 17;
		
		for (int i = 0; i < qualifiedName.length; i++) {
			result = 37 * result + qualifiedName[i].hashCode();
		}
		return result;
	}
	
	public int getNormalizedHashCode() {
		int result = 17;
		
		for (int i = 0; i < qualifiedName.length; i++) {
			result = 37 * result + qualifiedName[i].toUpperCase().toLowerCase().hashCode();
		}
		return result;
	}

	public void serialize(DataOutputStream outputStream) throws IOException {
		outputStream.writeInt(qualifiedName.length);
		
		for (int i = 0; i < qualifiedName.length; i++) {
			outputStream.writeUTF(qualifiedName[i]);
		}		
	}

	public void deserialize(DataInputStream inputStream) throws IOException {
		qualifiedName = new String[inputStream.readInt()];
	
		for(int i=0; i < qualifiedName.length; i++){
			qualifiedName[i] = inputStream.readUTF();
		}		
		
		qualifiedName = InternUtil.intern(qualifiedName);
	}
	
	public int getKind() {
		return IDependencyGraphValue.QUALIFIED_NAME;
	}
}
