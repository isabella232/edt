package org.eclipse.edt.mof.egl.plugin;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.edt.compiler.SystemEnvironment;
import org.eclipse.edt.compiler.internal.core.builder.NullBuildNotifier;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.sdk.compile.SourcePathEntry;
import org.eclipse.edt.mof.egl.compiler.EGL2IREnvironment;
import org.eclipse.edt.mof.egl.compiler.Processor;
import org.eclipse.edt.mof.egl.compiler.SystemPackageBuildPathEntryFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class MofEglPlugin extends Plugin{

	public void start(BundleContext context) throws Exception {
		super.start(context);
		initializeSystemPackages();
	}
	
	public static void fred(){
		
	}
	public static void initializeSystemPackages(){
		Bundle bundle = Platform.getBundle("org.eclipse.edt.compiler"); //$NON-NLS-1$
		try {
			String file = FileLocator.resolve( bundle.getEntry( "/" ) ).getFile(); //$NON-NLS-1$
			// Replace Eclipse's slashes with the system's file separator.
			file = file.replace( '/', File.separatorChar );
			file = file + SystemEnvironment.EDT_LIB_DIRECTORY;
			File libfile = new File(file);
			if (libfile.exists()){
				
			    Processor processor = new Processor(NullBuildNotifier.getInstance(), new ICompilerOptions(){		            
			        public boolean isVAGCompatible() {return true;}
					public boolean isAliasJSFNames() {return false;}}
					,null);
			    
				EGL2IREnvironment eglcEnv = new EGL2IREnvironment();
			    processor.setEnvironment(eglcEnv);			    
	            SourcePathEntry.getInstance().setDeclaringEnvironment(eglcEnv);
	            SourcePathEntry.getInstance().setProcessor(processor);
				SystemEnvironment.getInstance().initializeSystemPackages(file, new SystemPackageBuildPathEntryFactory(eglcEnv, eglcEnv.getConverter()));
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

}
