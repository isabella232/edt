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
package org.eclipse.edt.compiler.internal.sdk.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.DataItem;
import org.eclipse.edt.compiler.core.ast.DataTable;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Delegate;
import org.eclipse.edt.compiler.core.ast.Enumeration;
import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.compiler.core.ast.FormGroup;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.core.ast.TopLevelForm;
import org.eclipse.edt.compiler.core.ast.TopLevelFunction;
import org.eclipse.edt.compiler.internal.core.utils.CharOperation;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author svihovec
 */
public class Util {

    public final static char[] SUFFIX_egl = ".egl".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_EGL = ".EGL".toCharArray(); //$NON-NLS-1$
	
	public static String getFilePartName(File file){
		return InternUtil.intern(file.getAbsolutePath().toString());
	}
	
	public static String getCaseSensitiveFilePartName(File file){
		return InternUtil.internCaseSensitive(file.getAbsolutePath().toString());
	}
	
	/**
	 * Returns true if str.toLowerCase().endsWith(".egl")
	 * implementation is not creating extra strings.
	 */
	public static final boolean isEGLFileName(String name) {
		int nameLength = name == null ? 0 : name.length();
		int suffixLength = SUFFIX_EGL.length;
		if (nameLength < suffixLength) return false;

		for (int i = 0, offset = nameLength - suffixLength; i < suffixLength; i++) {
			char c = name.charAt(offset + i);
			if (c != SUFFIX_egl[i] && c != SUFFIX_EGL[i]) return false;
		}
		return true;		
	}
	
	public static int getPartType(Node part){
    	
    	final Integer[] value = new Integer[1];
    	
    	part.accept(new DefaultASTVisitor(){
	    	public boolean visit(Handler handler){
	    		value[0] = new Integer(ITypeBinding.HANDLER_BINDING);
	    		return false;
			}

	    	public boolean visit(ExternalType extType){
	    		value[0] = new Integer(ITypeBinding.EXTERNALTYPE_BINDING);
	    		return false;
			}
	    	public boolean visit(Enumeration enumeration){
	    		value[0] = new Integer(ITypeBinding.ENUMERATION_BINDING);
	    		return false;
			}

			public boolean visit(DataItem dataItem) {
				value[0] = new Integer(ITypeBinding.DATAITEM_BINDING);
				return false;
			}
	
			public boolean visit(DataTable dataTable) {
				value[0] = new Integer(ITypeBinding.DATATABLE_BINDING);
				return false;
			}
	
			public boolean visit(FormGroup formGroup) {
				value[0] = new Integer(ITypeBinding.FORMGROUP_BINDING);
				return false;
			}
	
			public boolean visit(Interface interfaceNode) {
				value[0] = new Integer(ITypeBinding.INTERFACE_BINDING);
				return false;
			}
	
			public boolean visit(Library library) {
				value[0] = new Integer(ITypeBinding.LIBRARY_BINDING);
				return false;
			}
	
			public boolean visit(Program program) {
				value[0] = new Integer(ITypeBinding.PROGRAM_BINDING);
				return false;
			}

			public boolean visit(Delegate delegate) {
				value[0] = new Integer(ITypeBinding.DELEGATE_BINDING);
				return false;
			}

			public boolean visit(Record record) {
				if(record.isFlexible()){
					value[0] = new Integer(ITypeBinding.FLEXIBLE_RECORD_BINDING);
				}else{
					value[0] = new Integer(ITypeBinding.FIXED_RECORD_BINDING);
				}
				return false;
			}
	
			public boolean visit(Service service) {
				value[0] = new Integer(ITypeBinding.SERVICE_BINDING);
				return false;
			}
	
			public boolean visit(TopLevelForm topLevelForm) {
				value[0] = new Integer(ITypeBinding.FORM_BINDING);
				return false;
			}
	
			public boolean visit(TopLevelFunction topLevelFunction) {
				value[0] = new Integer(ITypeBinding.FUNCTION_BINDING);
				return false;
			}
			
			public boolean visit(org.eclipse.edt.compiler.core.ast.File file){
			    value[0] = new Integer(ITypeBinding.FILE_BINDING);
			    return false;
			}
    	});
    	
    	return value[0].intValue();
    } 
	
	public static void log(String message, Exception e){
	    System.out.println(message);
	    e.printStackTrace();
	}
		
    public static List getPartFiles(String pattern,String extension) {
    	ArrayList list = new ArrayList();
    	if(pattern.indexOf('*')> 0 || pattern.indexOf('?')> 0){
    		int index = pattern.lastIndexOf(File.separatorChar);
    		char[] patternChars = index != -1 ? pattern.substring(index + 1).toCharArray():pattern.toCharArray();
    		File userDir = new File(pattern).getParentFile();//new File(System.getProperty("user.dir"));
    		if(userDir.isDirectory() && userDir.exists()){
    			File[] files = userDir.listFiles();
    			for(int i = 0; i < files.length; i++){
    				File thisFile = files[i];
    				if(thisFile.getName().toLowerCase().endsWith(extension.toLowerCase())){
        				if(CharOperation.pathMatch(patternChars,thisFile.getName().toCharArray(),true,File.separatorChar)){
        					try {
								list.add(thisFile.getCanonicalFile());
							} catch (IOException e) {
								//e.printStackTrace();
							}
        				}
    				}
    			}
    		}
    	}else{
    		if (!compileAll(list, pattern, extension)) {
    			list.add(new File(pattern));
    		}
    	}
   	         
    	return list;
    }
    
    private static boolean compileAll(List list, String pattern,String extension) {
    	File dir = new File(pattern);
    	if (dir.exists() && dir.isDirectory()) {
    		addAllFilesToList(list, dir, extension);
    		return true;
    	}
    	return false;
    }
    
    private static void addAllFilesToList(List list, File dir, String extension) {
    	File[] files = dir.listFiles();
    	for (int i = 0; i < files.length; i++) {
    		
    		if (files[i].isDirectory()) {
    			addAllFilesToList(list, files[i], extension);
    		}
    		else {
    			if(files[i].getName().toLowerCase().endsWith(extension.toLowerCase())){
    				try {
						list.add(files[i].getCanonicalFile());
					} catch (IOException e) {}
    			}
    		}
    				
		}
    }
}
