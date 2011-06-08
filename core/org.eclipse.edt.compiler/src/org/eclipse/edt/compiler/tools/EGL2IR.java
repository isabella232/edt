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
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.EDTCompiler;
import org.eclipse.edt.compiler.EGL2IRArgumentProcessor;
import org.eclipse.edt.compiler.EGL2IREnvironment;
import org.eclipse.edt.compiler.ISystemEnvironment;
import org.eclipse.edt.compiler.Processor;
import org.eclipse.edt.compiler.SystemEnvironment;
import org.eclipse.edt.compiler.SystemEnvironmentUtil;
import org.eclipse.edt.compiler.SystemPackageBuildPathEntryFactory;
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
import org.eclipse.edt.compiler.internal.core.lookup.BindingCreator;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.mof2binding.Mof2Binding;
import org.eclipse.edt.compiler.internal.sdk.IPartRequestor;
import org.eclipse.edt.compiler.internal.sdk.compile.ASTManager;
import org.eclipse.edt.compiler.internal.sdk.compile.ISDKProblemRequestorFactory;
import org.eclipse.edt.compiler.internal.sdk.compile.PartPathEntry;
import org.eclipse.edt.compiler.internal.sdk.compile.SourcePathEntry;
import org.eclipse.edt.compiler.internal.sdk.compile.SourcePathInfo;
import org.eclipse.edt.compiler.internal.util.NameUtil;
import org.eclipse.edt.compiler.sdk.compile.BuildPathException;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.impl.ProgramImpl;
import org.eclipse.edt.mof.egl.lookup.EglLookupDelegate;
import org.eclipse.edt.mof.egl.lookup.PartEnvironment;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.FileSystemObjectStore;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.serialization.ObjectStore;



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
	public static ISystemEnvironment systemEnvironment;
	public static void main(String[] args) {

		EGL2IRArgumentProcessor.EGL2IRArguments processedArguments = new EGL2IRArgumentProcessor().processArguments(args);

		if(processedArguments != null){
		    compile(processedArguments, null,null);
		}
	}

	public static void main(String[] args, IPartRequestor partRequestor, ISDKProblemRequestorFactory problemRequestorFactory) {

		EGL2IRArgumentProcessor.EGL2IRArguments processedArguments = new EGL2IRArgumentProcessor().processArguments(args);

		if(processedArguments != null){
		    compile(processedArguments, problemRequestorFactory, partRequestor);
		}
	}

	public static void main(String[] args,ISDKProblemRequestorFactory problemRequestorFactory) {

		EGL2IRArgumentProcessor.EGL2IRArguments processedArguments = new EGL2IRArgumentProcessor().processArguments(args);
		
		SourcePathInfo.getInstance().reset();
		SourcePathEntry.getInstance().reset();

		if(processedArguments != null){
		    compile(processedArguments,problemRequestorFactory,null);
		}
	}
	
	public static void compile(final EGL2IRArgumentProcessor.EGL2IRArguments processedArgs,ISDKProblemRequestorFactory problemRequestorFactory,IPartRequestor partRequestor){
		EGLG.compile(processedArgs, new EDTCompiler(), problemRequestorFactory, partRequestor);
	}

}
