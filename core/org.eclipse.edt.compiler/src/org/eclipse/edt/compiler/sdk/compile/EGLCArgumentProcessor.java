/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.sdk.compile;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.edt.compiler.internal.sdk.utils.Util;

import com.ibm.icu.util.StringTokenizer;

/**
 * @author svihovec
 *
 */
public class EGLCArgumentProcessor {

    public class EGLCArguments {
        private File[] sourcePath = null;
        private File[] partPath = null;
        private File irOutputPath = null;
        private File[] partFiles = null;
        private boolean bClean = false;
        private ArrayList list = new ArrayList();
        private String nlsCode = null;
        private String msgTablePrefix = null;
        
        public boolean isClean() {
			return bClean;
		}
        
        public String getNlsCode() {
        	return nlsCode;
        }

        public void setNlsCode(String str) {
        	nlsCode = str;
        }
        
        public String getMsgTablePrefix() {
        	return msgTablePrefix;
        }

        public void setMsgTablePrefix(String str) {
        	msgTablePrefix = str;
        }
        
		public void setClean(boolean clean) {
			bClean = clean;
		}

		public File[] getSourcePathEntries(){
            if(sourcePath == null){
                return getPartPathEntries();
            }
            return sourcePath;
        }
        
        public File[] getPartPathEntries(){
            if(partPath == null){
                return new File[]{new File(System.getProperty("user.dir"))};
            }
            return partPath;
        }
        
        public File getIROutputPath(){
        	return    irOutputPath;     	
        }
        
        public File[] getPartFiles(){
        	if (partFiles == null){
        		partFiles = (File[])list.toArray(new File[list.size()]);
        	}
            return partFiles;
        }
        
        protected void initPartFiles(String pattern) {
        	list.addAll(Util.getPartFiles(pattern,".egl"));
          
        }
        
        protected void setPartPath(File[] partPath) {
            this.partPath = partPath;
        }
        protected void setSourcePath(File[] sourcePath) {
            this.sourcePath = sourcePath;
        }
        
        protected void setIROutputPath(File file){
        	irOutputPath = file;
        }
    }
    
	private static final String SOURCE_PATH = "-eglPath";
	private static final String IR_OUTPUT_PATH = "-irDestination";
    private static final String CLEAN = "-clean";
    private static final String NLSCODE = "-nlsCode";
    private static final String MSGTABLEPREFIX = "-msgTablePrefix";
    
    public EGLCArguments createDefaultArguments(){
    	return new EGLCArguments();
    }
    
     public EGLCArguments processArguments(String[] args){
	    
	    EGLCArguments arguments = new EGLCArguments();
	    
	    try{
	        int i=0;
	        while(i < args.length){
	        	int newindex = processArgument(arguments,args,i);
	        	if (newindex == -1){
		            System.out.println("Invalid option: " + args[i]);
		            processError();
		            return null;
		        }
	        	i = newindex;
	        }
	    }catch(ArrayIndexOutOfBoundsException e){
	        processError();
	        return null;
	     }
	    
	    return arguments;
	}
    
     public int processArgument(EGLCArguments arguments,String[] args,int index){
 	    int newindex = index;
         if(args[newindex].charAt(0) == '-'){
	        if(args[newindex].equalsIgnoreCase(SOURCE_PATH)){
	            arguments.setSourcePath(processPath(args[newindex + 1]));
	            arguments.setPartPath(processPath(args[newindex + 1]));
	             newindex += 2;
	        }else if(args[newindex].equalsIgnoreCase(CLEAN)){
	            arguments.setClean(true);
	            newindex += 1;
	        }else if(args[newindex].equalsIgnoreCase(NLSCODE)){
	            arguments.setNlsCode(args[newindex + 1]);
	            newindex += 2;
	        }else if(args[newindex].equalsIgnoreCase(MSGTABLEPREFIX)){
	            arguments.setMsgTablePrefix(args[newindex + 1]);
	            newindex += 2;
	        }else if(args[newindex].equalsIgnoreCase(IR_OUTPUT_PATH)){
	            arguments.setIROutputPath(new File(args[newindex + 1]));
	            newindex += 2;
	        }else{
	            return -1;
	        }
        }else{
            arguments.initPartFiles(args[newindex]);
            newindex += 1;
        }

    
 	    return newindex;
 	}
      
    private void processError() {
        System.out.println("Usage: eglc -eglPath <path> -clean -irDestination <path> <partFiles>");
    }

    private static File[] processPath(String partPath) {
		ArrayList partPathLocations = new ArrayList();
		
		StringTokenizer partPathTokenizer = new StringTokenizer(partPath, File.pathSeparator);
		
		while(partPathTokenizer.hasMoreTokens()){
			partPathLocations.add(new File(partPathTokenizer.nextToken()));
		}
		
		return (File[])partPathLocations.toArray(new File[partPathLocations.size()]);
	}
    
   
}
