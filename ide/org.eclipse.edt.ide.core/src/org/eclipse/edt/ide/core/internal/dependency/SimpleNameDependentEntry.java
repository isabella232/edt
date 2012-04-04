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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author svihovec
 *
 */
public class SimpleNameDependentEntry extends DependentEntry {

	private HashMap simpleNames = new HashMap();
	private HashSet files = new HashSet();
	
	public void addPart(Part filePart, SimpleName partName){
		
		HashSet dependentsByFilePart = (HashSet)simpleNames.get(filePart);
	
		if(dependentsByFilePart == null){
			dependentsByFilePart = new HashSet();
			simpleNames.put(filePart, dependentsByFilePart);
			files.add(filePart);
		}
		dependentsByFilePart.add(partName);		
	}

	public HashSet getFileParts() {
		return files;
	}

	public HashSet getParts(Part filePart) {
		return (HashSet)simpleNames.get(filePart);
	}

	public void removePart(Part filePart, SimpleName partName) {
		HashSet dependentsByFilePart = (HashSet)simpleNames.get(filePart);
		
		if(dependentsByFilePart != null){
			dependentsByFilePart.remove(partName);
			
			if(dependentsByFilePart.size() == 0){
				simpleNames.remove(filePart);
				files.remove(filePart);
			}
		}
	}
	
	public boolean isEmpty(){
		return simpleNames.size() == 0 && files.size() == 0;
	}
	
	public void serialize(DataOutputStream outputStream) throws IOException{
		outputStream.writeInt(files.size());
		
		for (Iterator iter = files.iterator(); iter.hasNext();) {
			Part filePart = (Part) iter.next();
			filePart.serialize(outputStream);
			
			HashSet dependentsByFilePart = (HashSet)simpleNames.get(filePart);
			outputStream.writeInt(dependentsByFilePart.size());
			for (Iterator iterator = dependentsByFilePart.iterator(); iterator.hasNext();) {
				SimpleName simpleName = (SimpleName) iterator.next();
				
				simpleName.serialize(outputStream);
			}
		}
	}
	
	public void deserialize(DataInputStream inputStream) throws IOException{
		int numFiles = inputStream.readInt();
		
		for(int i=0; i < numFiles; i++){
			Part filePart = new Part();
			filePart.deserialize(inputStream);
			
			int numPartNames = inputStream.readInt();
			
			for(int j=0; j < numPartNames; j++){
				SimpleName partName = new SimpleName();
				partName.deserialize(inputStream);
				
				addPart(filePart, partName);
			}		
		}
	}

	public int getKind() {
		return SIMPLE_NAME_DEPENDENT_ENTRY;
	}
}
