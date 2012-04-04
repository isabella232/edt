/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;

public class SerializationManager {
	
	private static final SerializationManager INSTANCE = new SerializationManager();
	
	private SerializationManager(){}
	
	public static SerializationManager getInstance(){
		return INSTANCE;
	}
	
	public void serialize(IPath outputPath, HashMap value) throws IOException{
		outputPath.removeLastSegments(1).toFile().mkdirs();
		
		DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outputPath.toFile())));
		
		try{
			outputStream.writeInt(value.size());
			
			for (Iterator iter = value.keySet().iterator(); iter.hasNext();) {
				IDependencyGraphValue entryName = (IDependencyGraphValue)iter.next();
				outputStream.writeInt(entryName.getKind());
				entryName.serialize(outputStream);
				
				
				IDependencyGraphEntry entry = (IDependencyGraphEntry)value.get(entryName);
				outputStream.writeInt(entry.getKind());
				entry.serialize(outputStream);
			}		
		}finally{
			outputStream.close();
		}
	}
	
	public HashMap deserialize(IPath inputPath) throws IOException{
		
		HashMap result = null;
		
		File file = inputPath.toFile();
		
		if(file.exists()){
			
			DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
			
			result = new HashMap();
			
			try{
				int numEntries = inputStream.readInt();
				
				for(int i=0; i < numEntries; i++){
					// Get the Entry Key
					int valueKind = inputStream.readInt();
					
					IDependencyGraphValue entryKey;
					switch(valueKind){
						case IDependencyGraphValue.FUNCTION:
							entryKey = new Function();
							break;
						case IDependencyGraphValue.PART:
							entryKey = new Part();
							break;
						case IDependencyGraphValue.QUALIFIED_NAME:
							entryKey = new QualifiedName();
							break;
						case IDependencyGraphValue.SIMPLE_NAME:
							entryKey = new SimpleName();
							break;
						default:
							throw new BuildException("Invalid Graph Value Kind");
					}
					
					entryKey.deserialize(inputStream);
					
					// Get the Entry
					int entryKind = inputStream.readInt();
					IDependencyGraphEntry entry;
					switch(entryKind){
						case IDependencyGraphEntry.DEPENDENCY_ENTRY:
							entry = new DependencyEntry();
							break;
						case IDependencyGraphEntry.QUALIFIED_NAME_DEPENDENT_ENTRY:
							entry = new QualifiedNameDependentEntry();
							break;
						case IDependencyGraphEntry.SIMPLE_NAME_DEPENDENT_ENTRY:
							entry = new SimpleNameDependentEntry();
							break;
						case IDependencyGraphEntry.FUNCTION_ENTRY:
							entry = new FunctionEntry();
							break;			
						default:
							throw new BuildException("Invalid Graph Entry Kind");
					}
					
					entry.deserialize(inputStream);
					
					result.put(entryKey, entry);
				}
			}finally{
				inputStream.close();
			}
		}
		
		return result;
	}
}
