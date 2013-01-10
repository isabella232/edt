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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author svihovec
 *
 */
public class QualifiedNameDependentEntry extends DependentEntry {

	private Set qualifiedNames = Collections.EMPTY_SET;
	
	public void addPart(Part part){
		if(qualifiedNames == Collections.EMPTY_SET){
			qualifiedNames = new HashSet();
		}
		qualifiedNames.add(part);
	}

	public Set getParts() {
		return qualifiedNames;
	}

	public void removePart(Part part) {
		qualifiedNames.remove(part);
	}
	
	public boolean isEmpty(){
		return qualifiedNames.size() == 0;
	}
	
	public void serialize(DataOutputStream outputStream) throws IOException{
		outputStream.writeInt(qualifiedNames.size());
		
		for (Iterator iter = qualifiedNames.iterator(); iter.hasNext();) {
			Part part = (Part) iter.next();
			
			part.serialize(outputStream);
		}
	}
	
	public void deserialize(DataInputStream inputStream) throws IOException{
		int numQualifiedNames = inputStream.readInt();
		
		for(int i=0; i < numQualifiedNames; i++){
			Part part = new Part();
			
			part.deserialize(inputStream);
		
			addPart(part);
		}
	}

	public int getKind() {
		return QUALIFIED_NAME_DEPENDENT_ENTRY;
	}	
}
