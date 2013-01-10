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
public class FunctionEntry implements IDependencyGraphEntry {

    Set functions = Collections.EMPTY_SET;
	
    public void addFunction(Function function){
		if(functions == Collections.EMPTY_SET){
			functions = new HashSet();
		}
		functions.add(function);
	}

    public void removeFunction(Function function) {
		functions.remove(function);		
	}

	public Set getFunctions() {
		return functions;
	}
	
	public boolean isEmpty(){
		return functions.size() == 0;
	}
	
	public void serialize(DataOutputStream outputStream) throws IOException{
		outputStream.writeInt(functions.size());
		
		for (Iterator iter = functions.iterator(); iter.hasNext();) {
			Function function = (Function) iter.next();
			
			function.serialize(outputStream);
		}
	}
	
	public void deserialize(DataInputStream inputStream) throws IOException{
		int numFunctions = inputStream.readInt();
		
		for(int i=0; i < numFunctions; i++){
			
			Function function = new Function();
			
			function.deserialize(inputStream);
			
			addFunction(function);
		}	
	}

	public int getKind() {
		return FUNCTION_ENTRY;
	}
}
