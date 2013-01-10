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

import org.eclipse.edt.mof.utils.NameUtile;

/**
 * @author svihovec
 *
 */
/*package*/ class SimpleName implements IDependencyGraphValue{

	private String simpleName;
	
	public SimpleName(String simpleName) {	
		this.simpleName = simpleName;
	}

	public SimpleName() {}

	public String getSimpleName(){
		return simpleName;
	}
	
	public String toString(){
		return simpleName;
	}
	
	public boolean equals(Object obj) {
	    if(this == obj){
			return true;
		}
		if(obj instanceof SimpleName){
		    return NameUtile.equals(simpleName, ((SimpleName)obj).simpleName);
		}
		return false;
	}
	
	public int hashCode() {
		return simpleName.hashCode();
	}
	
	public int getNormalizedHashCode() {
		return simpleName.toUpperCase().toLowerCase().hashCode();
	}

	public void serialize(DataOutputStream outputStream) throws IOException {
		outputStream.writeUTF(simpleName);		
	}

	public void deserialize(DataInputStream inputStream) throws IOException {
		simpleName = NameUtile.getAsName(inputStream.readUTF());		
	}

	public int getKind() {
		return IDependencyGraphValue.SIMPLE_NAME;
	}	
}
