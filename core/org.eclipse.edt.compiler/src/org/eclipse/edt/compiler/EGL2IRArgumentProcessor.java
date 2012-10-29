/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler;

import java.io.File;

import org.eclipse.edt.compiler.sdk.compile.EGLCArgumentProcessor;


public class EGL2IRArgumentProcessor extends EGLCArgumentProcessor {
	public class EGL2IRArguments extends EGLCArgumentProcessor.EGLCArguments {
		File systemRoot = null;
		public boolean xmlOut = false;
		String[] extensions;
		
		public File getSystemRoot() {
			return systemRoot;
		}
		protected void setSystemRoot(File root) {
			systemRoot = root;
		}
		
		public boolean getXMLOut() {
			return xmlOut;
		}
		public void setXMLOut(boolean value) {
			xmlOut = value;
		}
		public void setExtensions(String extensions) {
			this.extensions = extensions.split(",");
		}
		public String[] getExtensions() {
			return extensions;
		}
	}
	
    private static final String SYSTEMROOT = "-systemRoot";
    private static final String XML_OUT = "-xmlOut";
    private static final String EXTENSIONS = "-extensions";

    public EGL2IRArguments createDefaultArguments(){
    	return new EGL2IRArguments();
    }

     public EGL2IRArguments processArguments(String[] args){
	    
	    EGL2IRArguments arguments = new EGL2IRArguments();
	    
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
     
     private void processError() {
         System.out.println("Usage: eglc -systemRoot <path> -eglPath <path> -clean -irDestination <path> -xmlOut <partFiles> -extensions <comma-delimited-extensions>");
     }

   public int processArgument(EGL2IRArguments arguments,String[] args,int index){
    	int newindex = index;
    	if(args[newindex].charAt(0) == '-'){
	        if(args[newindex].equalsIgnoreCase(SYSTEMROOT)){
	            arguments.setSystemRoot(new File(args[newindex + 1]));
	            newindex += 2;
	            return newindex;
	        }
	        if(args[newindex].equalsIgnoreCase(XML_OUT)){
	            arguments.setXMLOut(true);
	            newindex += 1;
	            return newindex;
	        }
	        if(args[newindex].equalsIgnoreCase(EXTENSIONS)){
	        	arguments.setExtensions(args[newindex + 1]);
	        	newindex += 2;
	        	return newindex;
	        }

	    }
    	newindex = super.processArgument(arguments, args, newindex);
    	return newindex;
    }

}
