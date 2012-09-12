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

import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.mof.utils.NameUtile;

/**
 * @author svihovec
 *
 */
/*package*/ class QualifiedName implements IDependencyGraphValue{

	private String qualifiedName;
	
	public QualifiedName(String qualifiedName) {
		this.qualifiedName = qualifiedName;
	}

	public QualifiedName() {}
	
	public String getQualifiedName(){
		return qualifiedName;
	}
	
	public String toString(){
		return qualifiedName;
	}
	
	
	public boolean equals(Object obj) {
	    if(this == obj){
			return true;
		}
		if(obj instanceof QualifiedName){
		    return NameUtile.equals(qualifiedName, ((QualifiedName)obj).qualifiedName);
		}
		return false;
	}
	
	public int hashCode() {
		return qualifiedName.hashCode();
	}
	
	public int getNormalizedHashCode() {
		return qualifiedName.toUpperCase().toLowerCase().hashCode();
	}

	public void serialize(DataOutputStream outputStream) throws IOException {
		// Write out in the old format so as to not break existing workspaces.
		String[] segments = Util.qualifiedNameToStringArray(qualifiedName);
		outputStream.writeInt(segments.length);
		
		for (int i = 0; i < segments.length; i++) {
			outputStream.writeUTF(segments[i]);
		}		
	}

	public void deserialize(DataInputStream inputStream) throws IOException {
		// Read in the old format so as to not break existing workspaces.
		int segments = inputStream.readInt();
		StringBuilder buf = new StringBuilder();
	
		for (int i=0; i < segments; i++) {
			if (i > 0) {
				buf.append('.');
			}
			buf.append(inputStream.readUTF());
		}		
		
		qualifiedName = NameUtile.getAsName(buf.toString());
	}
	
	public int getKind() {
		return IDependencyGraphValue.QUALIFIED_NAME;
	}
}
