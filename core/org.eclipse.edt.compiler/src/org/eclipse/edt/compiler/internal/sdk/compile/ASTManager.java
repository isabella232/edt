/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.sdk.compile;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.core.ast.ErrorCorrectingParser;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Lexer;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.utils.SoftLRUCache;
import org.eclipse.edt.compiler.internal.sdk.utils.Util;
import org.eclipse.edt.mof.utils.NameUtile;


/**
 * @author winghong
 */
public class ASTManager {

    private SoftLRUCache fileLRUCache = new SoftLRUCache(MAX_NUM_FILES);
	private static final int MAX_NUM_FILES = 10;
    
	private static final ASTManager INSTANCE = new ASTManager();
	
	private ASTManager(){}
	
	public static ASTManager getInstance(){
		return INSTANCE;
	}
		
    public File getFileAST(java.io.File file) {
    	
    	File cachedFile = (File)fileLRUCache.get(file);
    	
    	if(cachedFile == null){
	        try {
	        	ErrorCorrectingParser parser;
	           	parser = new ErrorCorrectingParser(new Lexer(new BufferedInputStream(new FileInputStream(file))));
	           	
	        	cachedFile = (File)parser.parse().value;
	           	fileLRUCache.put(file, cachedFile);
	        } catch (Exception e) {
	           throw new BuildException(e);
	        	
	        }
    	}
    	return cachedFile;
    }
    
    public Part getPartAST(java.io.File declaringFile, String partName) {
        File file = getFileAST(declaringFile);
        List parts = file.getParts();
        
        for(Iterator iter = parts.iterator(); iter.hasNext();) {
            Part part = (Part) iter.next();
            if(NameUtile.equals(part.getIdentifier(), partName)) {
            	return part.clonePart();
            }
        }
    	throw new RuntimeException("Part must exist before calling this method");
    }
    
    public Node getAST(java.io.File declaringFile, String partName){
    	 if(NameUtile.equals(Util.getFilePartName(declaringFile), partName)){
    		return getFilePartAST(declaringFile);
    	}else{
    		return getPartAST(declaringFile, partName);
    	}
    }

	private Node getFilePartAST(java.io.File declaringFile) {
		return getFileAST(declaringFile).cloneFilePart();
	}  
	
	public void reset() {
		fileLRUCache = new SoftLRUCache(MAX_NUM_FILES);
	}
 }
