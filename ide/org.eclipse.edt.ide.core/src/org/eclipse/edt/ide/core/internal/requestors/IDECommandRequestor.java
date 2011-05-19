/*******************************************************************************
 * Copyright © 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.requestors;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.internal.EGLBasePlugin;
import org.eclipse.edt.compiler.internal.EGLGenerationModeSetting;
import org.eclipse.edt.compiler.internal.EGLVAGCompatibilitySetting;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.utils.EGLProjectInfoUtility;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.core.ResolvedSourceType;
import org.eclipse.jdt.internal.core.search.matching.SuperTypeReferencePattern;
import org.eclipse.jst.j2ee.internal.project.J2EEProjectUtilities;
import org.eclipse.jst.j2ee.model.IModelProvider;
import org.eclipse.jst.j2ee.model.ModelProviderManager;
import org.eclipse.jst.j2ee.project.JavaEEProjectUtilities;
import org.eclipse.jst.javaee.web.WebAppVersionType;
import org.eclipse.pde.internal.core.natures.PDE;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.xml.sax.InputSource;

import com.ibm.icu.math.BigDecimal;
import com.ibm.icu.text.DateFormat;

//EDT removed all SQL, scope, and build descriptor code.
public class IDECommandRequestor extends AbstractCommandRequestor implements CommandRequestor {
	private org.xml.sax.EntityResolver entityResolver = null;

	public IDECommandRequestor() {
		super();
	}

	public java.lang.String[] findFiles(String fileName) {
		return null;
	}

	public IFile getFile(String fileName) {
		if (fileName == null) {
			return null;
		}

		org.eclipse.core.runtime.IPath path = new Path(fileName);
		try {
			return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		} catch (Exception e) {
			return null;
		}
	}

	public String getFileContents(String fileName) throws java.lang.Exception {
		IFile file = getFile(fileName);
		if (file == null) {
			return "";
		} else {
			InputStream stream = file.getContents(true);
			byte[] bytes = new byte[stream.available()];
			stream.read(bytes);
			stream.close();

			ByteArrayInputStream is = new ByteArrayInputStream(bytes);
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(is, file.getCharset()));

			return buildStringBuffer(inputReader).toString();
		}
	}

	private StringBuffer buildStringBuffer(BufferedReader inputReader) {
		StringBuffer s = new StringBuffer();
		try {
			char cbuf[] = new char[4096];
			int length = 0;
			while ((length = inputReader.read(cbuf)) >= 0) {
				s.append(cbuf, 0, length);
			}
		} catch (IOException e) {
		}
		return s;
	}

	public String getFullFileName(String fileName) {

		IFile file = null;
		try {
			file = getFile(fileName);
		} catch (Exception e) {
		}
		if (file == null) {
			return fileName;
		} else {
			return file.getFullPath().toOSString();
		}

	}
	
	public boolean isWorkbenchAvailable() {
		return true;
	}

	public String getDate(String fileName) {
		IFile iFile = getFile(fileName);
		if (iFile != null) {
			IPath path = iFile.getLocation();
			if (path != null) {
				File file = path.toFile();
				long mod = file.lastModified();
				if (mod != 0) {
					return DateFormat.getDateInstance().format(new Date(mod));
				}
			}
		}
		return null;
	}

	public String getTime(String fileName) {
		IFile iFile = getFile(fileName);
		if (iFile != null) {
			IPath path = iFile.getLocation();
			if (path != null) {
				File file = path.toFile();
				long mod = file.lastModified();
				if (mod != 0) {
					return DateFormat.getTimeInstance().format(new Date(mod));
				}
			}
		}
		return null;
	}

	public boolean folderExists(String folderName, String fileNameContext) {
		IFile fileContext = getFile(fileNameContext);
		if (fileContext == null) {
			return false;
		}
		if (folderExistsInProject(folderName, fileContext.getProject())) {
			return true;
		}

		IProject projects[];
		try {
			projects = fileContext.getProject().getReferencedProjects();
		} catch (CoreException e) {
			return false;
		}
		for (int i = 0; i < projects.length; i++) {
			if (folderExistsInProject(folderName, projects[i])) {
				return true;
			}
		}
		return false;

	}

	private boolean folderExistsInProject(String folderName, IProject project) {
		IResource resource = project.findMember(folderName);
		if ((resource != null) && resource.getType() == IResource.FOLDER) {
			return true;
		} else {
			return false;
		}
	}

	public InputSource getInputSource(String fileName) throws Exception {
		IFile file = getFile(fileName);
		if (file == null) {
			return null;
		} else {
			InputStream stream = file.getContents();
			InputSource is = new InputSource(new BufferedInputStream(stream));
			is.setSystemId(new String(fileName));
			return is;
		}
	}

	public String getProjectName(String fileName) {
		IFile file = getFile(fileName);
		if (file != null) {
			IProject project = file.getProject();
			if (project != null) {
				return project.getName();
			}
		}
		return null;
	}

	public String getDestUserId() {
		String value = EGLBasePlugin.getDestinationUserIDPreference();
		if (value == null || value.trim().length() == 0) {
			return null;
		}
		return value;
	}

	public String getDestPassword() {
		String value = EGLBasePlugin.getDestinationPasswordPreference();
		if (value == null || value.trim().length() == 0) {
			return null;
		}
		return value;
	}

	public boolean getVAGCompatiblity() {
		return EGLVAGCompatibilitySetting.isVAGCompatibility();
	}
	
	public int getGenerationMode() {
		return EGLGenerationModeSetting.DEPLOYMENT_GENERATION_MODE;
	}

	public IProject getProject(String projectName) {
		if (projectName == null || projectName.length() == 0) {
			return null;
		}
		return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
	}

	public static boolean isEGLProject(IProject project) {
		try {
			return project.isNatureEnabled(EGLCore.NATURE_ID);
		} catch (Exception e) {
			return false;
		}  
	}
	
	public static boolean isPluginProject(IProject project) {
		try {
			return project.isNatureEnabled(PDE.PLUGIN_NATURE);
		} catch (Exception e) {
			return false;
		}  
	}

	public boolean isValidProjectName(String name) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (workspace != null) {
			IStatus nameStatus = workspace.validateName(name, IResource.PROJECT);
			return nameStatus.isOK();
		}
		return true;
	}

	public Boolean isJavaProject(String name) {
		IProject project = getProject(name);
		if (project == null || !project.exists() || !project.isOpen()) {
			return null;
		}

		try {
			if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
		} catch (Exception e) {
			return Boolean.FALSE;
		}

	}

	public Boolean isWebProject(String name) {
		IProject project = getProject(name);
		if (project == null || !project.exists()  || !project.isOpen()) {
			return null;
		}

		try {

			if (JavaEEProjectUtilities.isDynamicWebProject(project)) {
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
		} catch (Exception e) {
			return Boolean.FALSE;
		}

	}

	public String getXmlFileContents(String fileName) throws Exception {
		return getFileContents(fileName);
	}

	public Boolean projectSupportsJaxWS(String name) {
		IProject project = getProject(name);
		if (project == null || !project.exists()  || !project.isOpen()) {
			return null;
		}
		
		return 	new Boolean(
				isWasJ2EE50(project) || 
				(isWas6(project) && hasJaxWSFacet(project)) ||
				(isTomcat(project) && isJava15(project)) ||
				(!JavaEEProjectUtilities.isDynamicWebProject(project) && isJavaProject(name)));
	}
	
	private boolean isWasJ2EE50(IProject proj) {
		
		return getJ2eeVersion(proj) >= 50 && isWebsphereRuntime(getRuntime(proj));
	}
	
	private boolean isWas6(IProject proj) {
		return isRuntimeWebsphere6((getRuntime(proj)));
	}

	private boolean hasJaxWSFacet(IProject proj) {
		IProjectFacetVersion vers = JavaEEProjectUtilities.getProjectFacetVersion( proj, "com.ibm.websphere.wsfp.web" );
		if (vers != null) {
			String verStr = vers.getVersionString();
			if (verStr != null) {
				 BigDecimal dec = new BigDecimal(verStr);
				 return (dec.floatValue() >= 1);
			}
		}
		return false;
	}
	
	private boolean isTomcat(IProject proj) {
		return isTomcatRuntime(getRuntime(proj));
	}

	private boolean isWebsphereRuntime(IProject proj) {
		return isWebsphereRuntime(getRuntime(proj));
	}

	private boolean isJava15(IProject proj) {
		IProjectFacetVersion vers = JavaEEProjectUtilities.getProjectFacetVersion( proj, "java" );
		if (vers != null) {
			String verStr = vers.getVersionString();
			if (verStr != null) {
				 BigDecimal dec = new BigDecimal(verStr);
				 return (dec.floatValue() >= 1.5);
			}
		}
		return false;
	}
	

	private  int getJ2eeVersion( IProject project )
	{
		String version = getJ2EEVersion( project );
		if( version == null )
		{
			return 0;
		}
		else if( version.equalsIgnoreCase( "3.0" ) )
		{
			return 60;
		}
		else if( version.equalsIgnoreCase( "2.5" ) )
		{
			return 50;
		}
		else if( version.equalsIgnoreCase( "2.4" ) )
		{
			return 14;
		}
		else if ( version.length() > 0)
		{
			return 13;
		}
		else
		{
			return 0;
		}
	}

	public static String getJ2EEVersion(IProject project)
	{
		String version = "2.2";
		final IModelProvider provider = ModelProviderManager.getModelProvider( project );		
		if( provider != null )
		{
			final Object webapp =  provider.getModelObject(); 
			
			if (webapp instanceof org.eclipse.jst.javaee.web.WebApp) 
			{
				WebAppVersionType versionType = ((org.eclipse.jst.javaee.web.WebApp)webapp).getVersion();
				if( versionType == null )
				{
					version = null;
				}
				else
				{
					version = versionType.toString();
				}
			}
			else if( webapp instanceof org.eclipse.jst.j2ee.webapplication.WebApp )
			{
				version = ((org.eclipse.jst.j2ee.webapplication.WebApp)webapp).getVersion();
			}
		}
		if( version == null )
		{
			version = "";
		}
		return version;
	}
	
	private boolean isWebsphereRuntime( org.eclipse.wst.server.core.IRuntime runtime )
	{
		return isTargetWebProject( runtime, "WEBSPHERE", null );
	}

	private boolean isTargetWebProject( org.eclipse.wst.server.core.IRuntime runtime, String id, String version )
	{
	    if( runtime != null && runtime.getRuntimeType() != null &&
	    		runtime.getRuntimeType().getName().toLowerCase().indexOf( id.toLowerCase() ) > -1 )
	    {
	    	if( version == null || version.length() == 0 )
	    	{
	    		return true;
	    	}
	    	else
	    	{
	    		return runtime.getRuntimeType().getVersion().toLowerCase().indexOf( version.toLowerCase() ) > -1;
	    	}
	    }

	    return false;
	}

	public static org.eclipse.wst.server.core.IRuntime getRuntime( IProject project )
	{
	    org.eclipse.wst.server.core.IRuntime runtime = null;
	    try
	    {
		    runtime = J2EEProjectUtilities.getServerRuntime( project );
	    }
	    catch (Exception e)
	    {
	    }

	    return runtime;
	}

	private boolean isRuntimeWebsphere6( org.eclipse.wst.server.core.IRuntime runtime)
	{
		return isTargetWebProject( runtime, "WEBSPHERE", "6." );
	}

	private boolean isTomcatRuntime( org.eclipse.wst.server.core.IRuntime runtime )
	{
		return isTargetWebProject( runtime, "Tomcat", null );
	}

	public Boolean projectContainsJaxRPC(String name) {
		IProject project = getProject(name);
		if (project == null || !project.exists() || !project.isOpen()) {
			return null;
		}
		
		if (isWebsphereRuntime(project)) {
			String fileName1 = "/" + project.getName() +"/WebContent/WEB-INF/ibm-webservices-bnd.xmi";
			String fileName2 = "/" + project.getName() +"/WebContent/WEB-INF/ibm-webservicesclient-bnd.xmi";
			return new Boolean(fileExits(fileName1) || fileExits(fileName2));
		}
		
		if (isTomcat(project)) {
			String fileName = "/" + project.getName() +"/WebContent/WEB-INF/lib/axis.jar";
			return new Boolean(fileExits(fileName));
		}
		
		return new Boolean(false);
	}
	
	private boolean fileExits(String path) {
		IFile file = getFile(path);
		return (file != null) && (file.exists());
	}

	public Boolean projectContainsJaxWS(String name, boolean hasServices,
			boolean hasWebBindings) {
		IProject project = getProject(name);
		if (project == null || !project.exists() || !project.isOpen()) {
			return null;
		}
		
		if (isWebsphereRuntime(project)) {
			//If there are services, much check for EGL generated JAX-WS (wrapper) file
			if (hasServices) {
				return new Boolean(hasEGLGeneratedWrapper(project));
				
			}
			
			//If there are web bindings, much check for EGL generated JAX-WS (proxy) file
			if (hasWebBindings) {
				return new Boolean(hasJavaOutputFile(project, "_wsProxy.java"));
			}
			
		}
		
		if (isTomcat(project)) {
			String fileName = "/" + project.getName() +"/WebContent/WEB-INF/lib/axis2-kernel-SNAPSHOT.jar";
			return new Boolean(fileExits(fileName));
		}
		
		return new Boolean(false);
	}
	
	private String getJavaOutputFolder(IProject project) {
			try {
				return EGLProjectInfoUtility.getGeneratedJavaFolder( project );
			} catch (Exception e) {
				return null;
			}
	}
	
	private boolean hasJavaOutputFile(IProject project, final String filenameSuffix) {
		String folder = getJavaOutputFolder(project);
		if (folder == null) {
			return false;
		}
		IResource res = project.findMember(folder);
		if (res == null || !(res.exists()) || !(res instanceof IFolder)) {
			return false;
		}
		
		final boolean[] found = new boolean[1];
		
		IResourceVisitor visitor = new IResourceVisitor() {

			public boolean visit(IResource resource) throws CoreException {
				if (found[0]) {
					return false;
				}
				
				if (resource instanceof IFile) {
					IFile file = (IFile) resource;
					if (file.getName().endsWith(filenameSuffix)) {
						found[0] = true;
					}
					return false;
				}
				return true;
			}
			
		};		
		try {
			res.accept(visitor);
		} catch (CoreException e) {
			return false;
		}
		return found[0];
	}
	
	
	private boolean hasEGLGeneratedWrapper(IProject project) {
				
	    final boolean[] found = new boolean[1];

	    try {
	    	
	    	IPackageFragmentRoot frag = getPackageFragmentRoot(project);
	    	if (frag == null) {
	    		return false;
	    	}
			SuperTypeReferencePattern pattern = new SuperTypeReferencePattern("javax.xml.ws".toCharArray(), "Provider".toCharArray(), IJavaSearchConstants.IMPLEMENTORS, SearchPattern.R_EXACT_MATCH);
			IJavaSearchScope scope = SearchEngine.createJavaSearchScope(new IJavaElement[] {frag});
		    SearchEngine searchEngine = new SearchEngine();
		    		    
		    SearchRequestor req = new SearchRequestor() {
	
				public void acceptSearchMatch(SearchMatch match)
						throws CoreException {
					if (found[0]) {
						return;
					}
					Object obj = match.getElement();
					if (obj instanceof ResolvedSourceType) {
						ResolvedSourceType src = (ResolvedSourceType)obj;
						if (src.getFullyQualifiedName().startsWith("ws.")) {
							found[0] = true;
						}
					}
				}	    	
		    };
			searchEngine.search(pattern, new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()}, scope, req, null);
		} catch (Exception e) {
			return false;
		}
		return found[0];

		
	}
	
	private IPackageFragmentRoot getPackageFragmentRoot(IProject project) {
		IJavaProject jProj = JavaCore.create(project);
		IResource res = project.findMember(getJavaOutputFolder(project));
		if (res == null || !res.exists()) {
			return null;
		}
		return jProj.getPackageFragmentRoot(res);		
	}
	

}
