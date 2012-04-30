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
package org.eclipse.edt.compiler.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.EGL2IRArgumentProcessor;
import org.eclipse.edt.compiler.EGL2IREnvironment;
import org.eclipse.edt.compiler.ICompiler;
import org.eclipse.edt.compiler.ISystemEnvironment;
import org.eclipse.edt.compiler.PartEnvironmentStack;
import org.eclipse.edt.compiler.Processor;
import org.eclipse.edt.compiler.Util;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AnnotationExpression;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.StringLiteral;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.builder.NullBuildNotifier;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.sdk.compile.ASTManager;
import org.eclipse.edt.compiler.internal.sdk.compile.ISDKProblemRequestorFactory;
import org.eclipse.edt.compiler.internal.sdk.compile.SourcePathEntry;
import org.eclipse.edt.compiler.internal.sdk.compile.SourcePathInfo;
import org.eclipse.edt.compiler.internal.util.NameUtil;
import org.eclipse.edt.compiler.sdk.compile.BuildPathException;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.serialization.FileSystemObjectStore;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.serialization.ObjectStore;
import org.eclipse.edt.mof.utils.NameUtile;



/**
 * @author svihovec
 *
 * TODO Issues
 * 	- when a source part falls out of the cache, it will be re-compiled if it is needed again
 * 		- should we even be caching them then?
 *  - we compile a part found on the source path, even if the source path contains a class file that is newer than the source file
 * 	- when compiling a part, we compile the part and its file part.  If two parts from the same part are compiled, the file part will be compiled twice
 * 		- see first issue, which comes into play here as well
 * 	- buffering of reading and writing
 *  - a top level function will be compiled generically once for each time it is called
 * 	- error handling for source and classpath entries
 */
public class EGLC {
	public static final String EGLBIN = ".eglbin";
	public static final String EGLXML = ".eglxml";
	
	public static EGL2IREnvironment eglcEnv;
	public static ISystemEnvironment systemEnvironment;

	
	public static void compile(final EGL2IRArgumentProcessor.EGL2IRArguments processedArgs, ICompiler compiler, ISDKProblemRequestorFactory problemRequestorFactory){
		try {
	        File[] files = processedArgs.getPartFiles();
	        
	        if(files.length > 0){
	        	
	        	if (eglcEnv == null) {
	        		eglcEnv = new EGL2IREnvironment();
	        	}
	        	
    			initializeOutputPath(processedArgs);
    			initializeSystemRoot(processedArgs, compiler);
    			initializeEGLPath(processedArgs);
    				            
	            ISystemEnvironment sysEnv = compiler.getSystemEnvironment(null);
            	eglcEnv.setCompiler(compiler);

			    
			    Processor processor = new Processor(NullBuildNotifier.getInstance(), new ICompilerOptions(){

			    },problemRequestorFactory, sysEnv);
			    
			    PartEnvironmentStack.pushEnv(eglcEnv);
			    
			    processor.setEnvironment(eglcEnv);
			    
	        	SourcePathInfo.getInstance().setSourceLocations(resolveSourcePathLocations(processedArgs.getSourcePathEntries(),processedArgs.getIROutputPath()));
	            SourcePathEntry.getInstance().setDeclaringEnvironment(eglcEnv);
	            SourcePathEntry.getInstance().setProcessor(processor);
	            
	            
    				            
			    for (int j = 0; j < files.length;j++){
			    	File file = files[j];
				    org.eclipse.edt.compiler.core.ast.File fileAST = ASTManager.getInstance().getFileAST(file);
		        	String packageName = createPackageName(fileAST);
				    processor.addPart(packageName, Util.getCaseSensitiveFilePartName(file));
				    SourcePathInfo.getInstance().addPart(packageName, Util.getFilePartName(file), ITypeBinding.FILE_BINDING, file, Util.getCaseSensitiveFilePartName(file));
				    
		        	for (Iterator iter = fileAST.getParts().iterator(); iter.hasNext();) {
						Part part = (Part) iter.next();
			            processor.addPart(packageName, part.getName().getCaseSensitiveIdentifier());
			            SourcePathInfo.getInstance().addPart(packageName, part.getName().getIdentifier(), Util.getPartType(part), file, part.getName().getCaseSensitiveIdentifier());
		        	}
			    }
				    
		
		        processor.process();
	    		
		      }else{
		            throw new RuntimeException("cannot find target file");
		      }
	        
	    } catch (BuildException e) {
           throw e;
        } catch (BuildPathException e) {
           throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
         }
        finally {
        	PartEnvironmentStack.popEnv();
        }
	}

	private static void deleteIRs(File path){
		File[] files = path.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()){
				deleteIRs(files[i]);
			}else if(files[i].getName().indexOf(".ir") > 0){
				files[i].delete();
			}
		}	
	}
		
	private static void initializeEGLPath (final EGL2IRArgumentProcessor.EGL2IRArguments processedArgs){
		File[] paths = processedArgs.getSourcePathEntries();
		for (File path : paths) {
			if (path.exists()) {
				String outType = processedArgs.xmlOut ? ObjectStore.XML : ObjectStore.BINARY;
				String fileext = processedArgs.xmlOut ? EGLXML : EGLBIN;
				ObjectStore store = new FileSystemObjectStore(path, eglcEnv, outType, fileext);
				eglcEnv.registerObjectStore(Type.EGL_KeyScheme, store);
				eglcEnv.addRoot(path);
			}
		}
	}
	
	private static void initializeOutputPath (final EGL2IRArgumentProcessor.EGL2IRArguments processedArgs){
		File path = processedArgs.getIROutputPath();
		if (path != null){
			if (!path.exists()){
				path.mkdirs();
			}else if (processedArgs.isClean()){
				deleteIRs(path);
			}
		}else if (processedArgs.isClean()){
			File[] sourcelocs = processedArgs.getSourcePathEntries();
			for (int i = 0; i < sourcelocs.length; i++){
				deleteIRs(sourcelocs[i]);
			}
		}
		String outType = processedArgs.getXMLOut() ? ObjectStore.XML : ObjectStore.BINARY;
		String fileext = processedArgs.xmlOut ? EGLXML : EGLBIN;
		
		// Default file extension for looking up EGL Model types which are instances of the MOF model
		ObjectStore store = new FileSystemObjectStore(path, eglcEnv, outType);
		eglcEnv.registerObjectStore(IEnvironment.DefaultScheme, store);
		eglcEnv.setDefaultSerializeStore(IEnvironment.DefaultScheme, store);
		
		// EGL file extension for looking up EGL parts which are instances of the EGL model
		store = new FileSystemObjectStore(path, eglcEnv, outType, fileext);
		eglcEnv.registerObjectStore(Type.EGL_KeyScheme, store);	
		eglcEnv.setDefaultSerializeStore(Type.EGL_KeyScheme, store);
		
		eglcEnv.addRoot(path);
	}
	
	private static void initializeSystemRoot (final EGL2IRArgumentProcessor.EGL2IRArguments processedArgs, ICompiler compiler){
		
		String outType = processedArgs.getXMLOut() ? ObjectStore.XML : ObjectStore.BINARY;

		File path = processedArgs.getSystemRoot();
		if (path != null && path.exists()) {
			ObjectStore store = new FileSystemObjectStore(path, eglcEnv, outType);
			eglcEnv.registerObjectStore(IEnvironment.DefaultScheme, store);
			
			String fileext = processedArgs.xmlOut ? EGLXML : EGLBIN;
	//		store = new FileSystemObjectStore(path, PartEnvironment.INSTANCE,outType);
			store = new FileSystemObjectStore(path, eglcEnv,outType, fileext);
			eglcEnv.registerObjectStore(Type.EGL_KeyScheme, store);
			eglcEnv.addRoot(path);
		}
		else if (compiler != null) {
			ISystemEnvironment env = compiler.getSystemEnvironment(null);
			Map<String, List<ObjectStore>> systemMap = env.getStores();
			for (Map.Entry<String, List<ObjectStore>> entry : systemMap.entrySet()) {
				String scheme = entry.getKey();
				List<ObjectStore> stores = entry.getValue();
				
				for (ObjectStore store : stores) {
					eglcEnv.registerObjectStore(scheme, store);
				}
			}
		}
	}

	
	private static String createPackageName(org.eclipse.edt.compiler.core.ast.File fileAST) {

	    if(fileAST.hasPackageDeclaration()){
	        return fileAST.getPackageDeclaration().getName().getNameComponents();
	    }
	    
	    return NameUtile.getAsName("");
    }

	 private static File resolveSourcePathLocation(File sourcePathEntryLocation) {
		 try{
             return sourcePathEntryLocation.getCanonicalFile();
         }catch(IOException e){
             System.out.println("Could not find source path location: " + sourcePathEntryLocation);
             throw new RuntimeException(e);
         }
	 }
	 
    private static File[] resolveSourcePathLocations(File[] sourcePathEntryLocations,File outpath) throws Exception{
		
        File[] canonicalLocations = new File[sourcePathEntryLocations.length];
        
        for (int i = 0; i < sourcePathEntryLocations.length; i++) {
            canonicalLocations[i] = resolveSourcePathLocation(sourcePathEntryLocations[i]);
		}	
		
		return canonicalLocations;
	}
    	
}
