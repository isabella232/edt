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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author svihovec
 *
 */
public class DependencyEntry implements IDependencyGraphEntry {

	Set qualifiedNames = Collections.EMPTY_SET;
	Set simpleNames = Collections.EMPTY_SET;
	
	public void addSimpleName(SimpleName simpleName){
		if(simpleNames == Collections.EMPTY_SET){
			simpleNames = new HashSet();
		}
		simpleNames.add(simpleName);
	}
	
	public void addQualifiedName(QualifiedName qualifiedName){
		if(qualifiedNames == Collections.EMPTY_SET){
			qualifiedNames = new HashSet();
		}
		qualifiedNames.add(qualifiedName);
	}

	public Set getSimpleNames() {
		return simpleNames;
	}

	public void removeSimpleName(SimpleName simpleName) {
		simpleNames.remove(simpleName);		
	}

	public void removeQualifiedName(QualifiedName qualifiedName) {
		qualifiedNames.remove(qualifiedName);		
	}

	public Set getQualifiedNames() {
		return qualifiedNames;
	}
	
	public boolean isEmpty(){
		return qualifiedNames.size() == 0 && simpleNames.size() == 0;
	}
	
	public void serialize(DataOutputStream outputStream) throws IOException{
		outputStream.writeInt(qualifiedNames.size());
		
		for (Iterator iter = qualifiedNames.iterator(); iter.hasNext();) {
			QualifiedName qualifiedName = (QualifiedName) iter.next();
			
			qualifiedName.serialize(outputStream);
		}
		
		outputStream.writeInt(simpleNames.size());
		
		for (Iterator iter = simpleNames.iterator(); iter.hasNext();) {
			SimpleName simpleName = (SimpleName) iter.next();
			
			simpleName.serialize(outputStream);
		}		
	}
	
	public void deserialize(DataInputStream inputStream) throws IOException{
		int numQualifiedNames = inputStream.readInt();
		
		for(int i=0; i < numQualifiedNames; i++){
			QualifiedName qualifiedName = new QualifiedName();
			
			qualifiedName.deserialize(inputStream);	
			
			addQualifiedName(qualifiedName);
		}
		
		int numSimpleNames = inputStream.readInt();
		
		for(int i=0; i < numSimpleNames; i++){
			SimpleName simpleName = new SimpleName();
			
			simpleName.deserialize(inputStream);
			
			addSimpleName(simpleName);
		}
	}

	public int getKind() {
		return DEPENDENCY_ENTRY;
	}
}
