package org.eclipse.edt.ide.core.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.osgi.framework.Bundle;

public class EGLSystemPathContaierInitializer extends
		EGLPathContainerInitializer {

	@Override
	public void initialize(IPath containerPath, IEGLProject project)
			throws CoreException {
		if(isValidEGLSystemPathContainerPath(containerPath)){
			List<IEGLPathEntry> pathEntries = new ArrayList<IEGLPathEntry>();
			IPath path = new Path(getPathToPluginDirectory("org.eclipse.edt.compiler", "lib", "edtCompiler.eglar"));
			pathEntries.add(EGLCore.newLibraryEntry(path, path, null));
			
			path = new Path(getPathToPluginDirectory("org.eclipse.edt.mof.egl", "lib", "egl.mofar"));
			pathEntries.add(EGLCore.newLibraryEntry(path, path, null));
			
			path = new Path(getPathToPluginDirectory("org.eclipse.edt.mof.eglx.persistence.sql", "egllib", "sql.mofar"));
			pathEntries.add(EGLCore.newLibraryEntry(path, path, null));
			
			path = new Path(getPathToPluginDirectory("org.eclipse.edt.mof.eglx.services", "egllib", "services.mofar"));
			pathEntries.add(EGLCore.newLibraryEntry(path, path, null));
			
			path = new Path(getPathToPluginDirectory("org.eclipse.edt.mof.eglx.jtopen", "egllib", "jtopen.eglar"));
			pathEntries.add(EGLCore.newLibraryEntry(path, path, null));
			
			path = new Path(getPathToPluginDirectory("org.eclipse.edt.mof.eglx.jtopen", "egllib", "externalTypes.eglar"));
			pathEntries.add(EGLCore.newLibraryEntry(path, path, null));
			
			path = new Path(getPathToPluginDirectory("org.eclipse.edt.mof.eglx.jtopen", "egllib", "jtopen.mofar"));
			pathEntries.add(EGLCore.newLibraryEntry(path, path, null));
			
			IEGLPathEntry[] entries = (IEGLPathEntry[])pathEntries.toArray(new IEGLPathEntry[pathEntries.size()]);
			EGLSystemRuntimePathContainer container = new EGLSystemRuntimePathContainer(containerPath, "EGLSystemRuntimePathContainer", entries);

			EGLCore.setEGLPathContainer(containerPath, new IEGLProject[]{project}, new EGLSystemRuntimePathContainer[]{container}, new NullProgressMonitor());
			project.getRawEGLPath();
		}
	}
	
	protected String getPathToPluginDirectory(String pluginID, String subDir, String fileName) {
		Bundle bundle = Platform.getBundle(pluginID);
		try {
			String file = FileLocator.resolve( bundle.getEntry( "/" ) ).getFile(); //$NON-NLS-1$
			
			// Replace Eclipse's slashes with the system's file separator.
			file = file.replace( '/', File.separatorChar );
			file = file + subDir + File.separatorChar + fileName;
			return file;
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return null;
	}

	private boolean isValidEGLSystemPathContainerPath(IPath path) {
		return path != null && path.segmentCount() == 1 && EDTCoreIDEPlugin.EDT_SYSTEM_RUNTIME_CONTAINER_ID.equals(path.segment(0));
	}
	
	
	private class EGLSystemRuntimePathContainer implements IEGLPathContainer{

		private IPath path;
		private String description;
		private IEGLPathEntry[] entries;
		
		
		
		public EGLSystemRuntimePathContainer(IPath path, String description,
				IEGLPathEntry[] entries) {
			super();
			this.path = path;
			this.description = description;
			this.entries = entries;
		}

		@Override
		public IEGLPathEntry[] getEGLPathEntries() {
			return entries;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public int getKind() {
			return K_SYSTEM;
		}

		@Override
		public IPath getPath() {
			return path;
		}
		
	}

}
