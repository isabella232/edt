package org.eclipse.edt.ide.deployment.services.operation;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.compiler.internal.util.EGLMessage;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.deployment.core.model.RUIApplication;
import org.eclipse.edt.ide.deployment.core.model.Restservice;
import org.eclipse.edt.ide.deployment.operation.AbstractDeploymentOperation;
import org.eclipse.edt.ide.deployment.results.DeploymentResultMessageRequestor;
import org.eclipse.edt.ide.deployment.results.IDeploymentResultsCollector;
import org.eclipse.edt.ide.deployment.solution.DeploymentContext;
import org.eclipse.edt.ide.deployment.utilities.DeploymentUtilities;
import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.util.JavaAliaser;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartNotFoundException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * Configures rununit.properties in the target project. New values for keys get appended to the file.
 */
public class ConfigureRuntimePropertiesOperation extends AbstractDeploymentOperation {
	
	private static final String RUNUNIT_PROPERTIES = "rununit.properties"; //$NON-NLS-1$

	@Override
	public void execute(DeploymentContext context, IDeploymentResultsCollector resultsCollector, IProgressMonitor monitor) throws CoreException {
		DeploymentResultMessageRequestor requestor = new DeploymentResultMessageRequestor(resultsCollector);
		
		Set<Part> restServices = findRESTServices(context, requestor);
		
		boolean deployingRUIHandlers;
		RUIApplication ruiApp = context.getDeploymentDesc().getRUIApplication();
		if (ruiApp.deployAllHandlers()) {
			if (context.getSourceProject() != null) {
				deployingRUIHandlers = DeploymentUtilities.getAllRUIHandlersInProject(EGLCore.create(context.getSourceProject())).size() > 0;
			}
			else {
				deployingRUIHandlers = false;
			}
		}
		else {
			deployingRUIHandlers = ruiApp.getRUIHandlers().size() > 0;
		}
		
		if (deployingRUIHandlers || restServices.size() > 0) {
			genProperties(context, restServices, deployingRUIHandlers, requestor);
		}
	}
	
	private void genProperties(DeploymentContext context, Set<Part> services, boolean setGlobalProperty, DeploymentResultMessageRequestor requestor) {
		IFile file = null;
		try {
			if (context.getTargetProject().hasNature(JavaCore.NATURE_ID)) {
				IJavaProject javaProject = JavaCore.create(context.getTargetProject());
				IPath srcFolder = null;
				for (IClasspathEntry entry : javaProject.getRawClasspath()) {
					if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
						srcFolder = entry.getPath();
						break;
					}
				}
				
				if (srcFolder != null) {
					StringBuilder contents = new StringBuilder(100);
					Properties props = new Properties();
					file = ResourcesPlugin.getWorkspace().getRoot().getFile(srcFolder.append(RUNUNIT_PROPERTIES));
					if (file.exists()) {
						// Read in the previous entries.
						BufferedInputStream bis = null;
						try {
							bis = new BufferedInputStream(file.getContents(true));
							props.load(bis);
							
							// Also load the previous file contents so we can append to it.
							contents.append(Util.getFileContents(file));
							if (contents.charAt(contents.length() - 1) != '\n') {
								contents.append('\n');
							}
						}
						finally {
							if (bis != null) {
								try {
									bis.close();
								}
								catch (IOException ioe) {
								}
							}
						}
						
					}
					
					String ddName = context.getDeploymentDesc().getEGLDDFileName().toLowerCase();
					boolean changed = false;
					
					if (setGlobalProperty && appendPropertyIfNecessary(Constants.APPLICATION_PROPERTY_FILE_NAME_KEY, ddName, props, contents)) {
						changed = true;
					}
					
					for (Part part : services) {
						String generatedName;
						String id = part.getName();
						String pkg = part.getPackageName();
						
						if (pkg == null || pkg.length() == 0) {
							generatedName = JavaAliaser.getAlias(id);
						}
						else {
							generatedName = JavaAliaser.packageNameAlias(pkg) + '.' + JavaAliaser.getAlias(id);
						}
						
						String key = Constants.APPLICATION_PROPERTY_FILE_NAME_KEY + '.' + generatedName;
						if (appendPropertyIfNecessary(key, ddName, props, contents)) {
							changed = true;
						}
					}
					
					if (changed) {
						ByteArrayInputStream bais = new ByteArrayInputStream(contents.toString().getBytes());
						if (file.exists()) {
							file.setContents(bais, true, false, null);
						}
						else {
							file.create(bais, true, null);
						}
						
						requestor.addMessage(DeploymentUtilities.createEGLDeploymentInformationalMessage(
								EGLMessage.EGL_DEPLOYMENT_DEPLOYED_RT_PROPERTY_FILE,
								null,
								new String[] { file.getProjectRelativePath().toPortableString() }));
					}
				}
			}
		}
		catch (Exception e) {
			requestor.addMessage(DeploymentUtilities.createEGLDeploymentErrorMessage(
					EGLMessage.EGL_DEPLOYMENT_FAILED_DEPLOY_RT_PROPERTY_FILE,
					null,
					new String[] { file == null ? RUNUNIT_PROPERTIES : file.getProjectRelativePath().toPortableString() }));
			requestor.addMessage(DeploymentUtilities.createEGLDeploymentErrorMessage(
					EGLMessage.EGL_DEPLOYMENT_EXCEPTION,
					null,
					new String[] { DeploymentUtilities.createExceptionMessage(e) }));
		}
	}
	
	private boolean appendPropertyIfNecessary(String key, String value, Properties prevProps, StringBuilder buf) {
		String prevValue = prevProps.getProperty(key);
		if (prevValue == null || !value.equals(prevValue.trim())) {
			buf.append(key);
			buf.append('=');
			buf.append(value);
			buf.append('\n');
			return true;
		}
		return false;
	}
	
	private Set<Part> findRESTServices(DeploymentContext context, DeploymentResultMessageRequestor requestor) {
		Set<Part> services = new HashSet<Part>();
		
		List<Restservice> restServices = context.getDeploymentDesc().getRestservices();
		for (Restservice rest : restServices) {
			if (rest.isEnableGeneration()) {
				try {
					Part part = context.findPart(rest.getImplementation());
					services.add(part);
				}
				catch (PartNotFoundException e) {
					requestor.addMessage(DeploymentUtilities.createEGLDeploymentErrorMessage(
							EGLMessage.EGL_DEPLOYMENT_FAILED,
							null,
							new String[] { DeploymentUtilities.createExceptionMessage(e) }));
				}
			}
		}
		
		return services;
	}
}
