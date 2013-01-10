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
package org.eclipse.edt.compiler.tools;

import org.eclipse.edt.compiler.EDTCompiler;
import org.eclipse.edt.compiler.EGL2IRArgumentProcessor;
import org.eclipse.edt.compiler.EGL2IREnvironment;
import org.eclipse.edt.compiler.ICompiler;
import org.eclipse.edt.compiler.ICompilerExtension;
import org.eclipse.edt.compiler.internal.sdk.compile.ISDKProblemRequestorFactory;
import org.eclipse.edt.compiler.internal.sdk.compile.SourcePathEntry;
import org.eclipse.edt.compiler.internal.sdk.compile.SourcePathInfo;



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
public class EGL2IR {
	public static final String EGLBIN = ".eglbin";
	public static final String EGLXML = ".eglxml";
	
	public static EGL2IREnvironment eglcEnv;
	public static String SystemLibFolderPath;
	public static void main(String[] args) {
		main(args, (ICompiler)null);
	}
	
	public static void main(String[] args, ICompiler compiler) {
		EGL2IRArgumentProcessor.EGL2IRArguments processedArguments = new EGL2IRArgumentProcessor().processArguments(args);

		if(processedArguments != null){
		    compile(processedArguments, null,compiler);
		}
	}

	public static void main(String[] args,ISDKProblemRequestorFactory problemRequestorFactory) {
		main(args, problemRequestorFactory, null);
	}
	
	public static void main(String[] args,ISDKProblemRequestorFactory problemRequestorFactory, ICompiler compiler) {
		EGL2IRArgumentProcessor.EGL2IRArguments processedArguments = new EGL2IRArgumentProcessor().processArguments(args);
		
		SourcePathInfo.getInstance().reset();
		SourcePathEntry.getInstance().reset();

		if(processedArguments != null){
		    compile(processedArguments,problemRequestorFactory, compiler);
		}
	}
	
	public static void compile(final EGL2IRArgumentProcessor.EGL2IRArguments processedArgs,ISDKProblemRequestorFactory problemRequestorFactory, ICompiler compiler){
		if (compiler == null){
			compiler = new EDTCompiler();
		}
		
		// Process any extensions.
		String[] extensions = processedArgs.getExtensions();
		if (extensions != null && extensions.length > 0) {
			for (int i = 0; i < extensions.length; i++) {
				if (extensions[i].trim().length() > 0) {
					try {
						Class<?> clazz = Class.forName(extensions[i].trim(), true, EGL2IR.class.getClassLoader());
						Object o = clazz.newInstance();
						if (o instanceof ICompilerExtension) {
							((ICompilerExtension)o).setCompiler(compiler);
							compiler.addExtension((ICompilerExtension)o);
						}
						else {
							throw new RuntimeException("Extension " + extensions[i].trim() + " is not an instance of " + ICompilerExtension.class.getCanonicalName() + " - aborting");
						}
					}
					catch (Exception e) {
						System.err.println("Unable to load extension: " + extensions[i].trim() + ". Compilation aborted.");
						throw new RuntimeException(e);
					}
				}
			}
		}
		
		EGLC.compile(processedArgs, compiler, problemRequestorFactory);
	}

}
