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
package org.eclipse.edt.ide.core.internal.lookup;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.edt.compiler.core.ast.ErrorCorrectingParser;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Lexer;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.utils.SoftLRUCache;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.mof.utils.NameUtile;

public abstract class AbstractASTManager {

	private SoftLRUCache fileLRUCache = new SoftLRUCache();
	
	/**
	 * Parse this file into an AST Tree if it does not already exist in the cache.
	 * 
	 * The file is read from the disk.
	 */
	public File getFileAST(IFile file) {
		File cachedFile = (File)fileLRUCache.get(file); 
		
		if(cachedFile == null){
			try{
				String fileContents = Util.getFileContents(file);
				cachedFile = getFileAST(file, fileContents);
			} catch (Exception e) {
	           throw new BuildException(e);
	        }
			
			fileLRUCache.put(file, cachedFile);
		}
		
		return cachedFile;
	}
	
	/**
	 * Parse this fileContents into an AST Tree.
	 * 
	 * The contents of the file are passed in and will not be read from the disk.
	 * 
	 * This method checks the cache for the key first. If the key is registered, then no parsing is done, the cached
	 * version is returned.
	 */
	public File getFileAST(Object key, String fileContents){

		File result = (File)fileLRUCache.get(key); 
		
		if(result == null){
			result = getFileAST(new BufferedReader(new StringReader(fileContents)));
			fileLRUCache.put(key, result);	
		}
		
		return result;
	}
	
	/**
	 * Parse this file into an AST Tree.
	 * 
	 * The contents of the file are passed in and will not be read from the disk.
	 * 
	 * This method assumes that the file contents being passed in are the "latest and greatest", and will replace any previously cached versions of the file.
	 */
	public File getFileAST(IFile file, String fileContents){
		File result = null;
		
		try{
			result = getFileAST(new BufferedReader(new StringReader(fileContents)));
		} catch (Exception e) {
           throw new BuildException(e);
        }
		
		fileLRUCache.put(file, result);		
		
		return result;
	}

		
	private File getFileAST(Reader reader) {
    	ErrorCorrectingParser parser = new ErrorCorrectingParser(new Lexer(reader));
       	return (File)parser.parse().value;
    }
    
    public Part getPartAST(IFile declaringFile, String partName) {
        File file = getFileAST(declaringFile);
        return getPartAST(file, declaringFile, partName);
    }
    
    public Part getPartAST(File file, IFile declaringFile, String partName) {
        List parts = file.getParts();
        
        for(Iterator iter = parts.iterator(); iter.hasNext();) {
            Part part = (Part) iter.next();
            if(NameUtile.equals(part.getIdentifier(), partName)){
            	Part result = part.clonePart();
            	
            	doGetPartAST(declaringFile, result);
            	
            	return result;
            }
        }
    	throw new BuildException("Part must exist before calling this method"); //$NON-NLS-1$
    }

    
    protected void doGetPartAST(IFile declaringFile, Part result) {
		// noop - required by Working copy ast manager
	}

	public Node getAST(IFile declaringFile, String partName){
    	 if(NameUtile.equals(Util.getFilePartName(declaringFile), partName)){
    		return getFilePartAST(declaringFile);
    	}else{
    		return getPartAST(declaringFile, partName);
    	}
    }

	private Node getFilePartAST(IFile declaringFile) {
		return getFileAST(declaringFile).cloneFilePart();
	}
	
	public void clear(){
		fileLRUCache = new SoftLRUCache();
	}
}
