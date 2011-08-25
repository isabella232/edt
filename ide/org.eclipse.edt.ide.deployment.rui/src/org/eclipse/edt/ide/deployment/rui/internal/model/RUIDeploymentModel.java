/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.rui.internal.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.compiler.internal.util.EGLMessage;
import org.eclipse.edt.compiler.internal.util.IGenerationResultsMessage;
import org.eclipse.edt.ide.core.internal.search.PartInfo;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.deployment.core.IDeploymentConstants;
import org.eclipse.edt.ide.deployment.core.model.Parameter;
import org.eclipse.edt.ide.deployment.core.model.RUIApplication;
import org.eclipse.edt.ide.deployment.core.model.RUIHandler;
import org.eclipse.edt.ide.deployment.results.DeploymentResultMessageRequestor;
import org.eclipse.edt.ide.deployment.results.IDeploymentResultsCollector;
import org.eclipse.edt.ide.deployment.rui.Activator;
import org.eclipse.edt.ide.deployment.rui.internal.IConstants;
import org.eclipse.edt.ide.deployment.rui.internal.preferences.HandlerLocalesList;
import org.eclipse.edt.ide.deployment.rui.internal.util.DeployLocale;
import org.eclipse.edt.ide.deployment.rui.internal.util.GenerateHTMLFile;
import org.eclipse.edt.ide.deployment.rui.internal.util.Utils;
import org.eclipse.edt.ide.deployment.utilities.DeploymentUtilities;
import org.eclipse.edt.ide.rui.utils.IFileLocator;

/**
 *
 */
public class RUIDeploymentModel {
	
	private static class ValidatorMessageRequestor implements IGenerationMessageRequestor {
		private boolean hasError = false;
		private boolean hasCompileError = false;
		private boolean hasGenerationError = false;
		
		public void addMessage(IGenerationResultsMessage message) {
			if( !hasError && message.isError() )
			{
				hasError = true;
				
				if(EGLMessage.EGLMESSAGE_COMPILE_ERRORS.equals(message.getId())){
					hasCompileError = true;
				}else{
					hasGenerationError = true;										
				}
			}
		}

		public void addMessages(List list) {
			for( Iterator<EGLMessage> itr = list.iterator(); itr.hasNext();)
			{
				EGLMessage message = itr.next();
				addMessage(message);
			}
		}

		public void clear() {							
		}

		public List getMessages() {
			return null;
		}

		public boolean isError() {
			return hasError;
		}
		
		public boolean isCompileError() {
			return hasCompileError;
		}
		
		public boolean isGenerationError() {
			return hasGenerationError;
		}

		public void sendMessagesToGenerationResultsServer(boolean bool) {
			// TODO Auto-generated method stub
			
		}
	};
	public class DeployableFile{
		private byte[] file;
		private boolean deployed;
		public DeployableFile(byte[] file) {
			this.file = file;
			deployed = false;
		}
		public boolean isDeployed() {
			return deployed;
		}
		public void setDeployed(boolean generated) {
			this.deployed = generated;
		}
		public byte[] getFile() {
			return file;
		}
		
	}
	/**
	 * The selected source project. This is the project that contains the RUI handler the
	 * user wants to deploy
	 */
	private IProject sourceProject;
	/**
	 * The RUI Handlers that the user has selected to deploy (IFile)
	 */
	private List<IFile> sourceRUIHandlers = new ArrayList<IFile>();
	
	/**
	 * A list of the locale codes that the handler should be generated for. This should match
	 * the set of properties files that the user has.
	 */
	private List<DeployLocale> handlerLocales = new ArrayList<DeployLocale>();
	
	/**
	 * The name of the target
	 */
	private String target;
	
	/**
	 * The HTML file name to use for each RUIHandler, indexed by RUIHandler IFile
	 */
	private Map<IResource, String> htmlFileNames = new HashMap<IResource, String>();
	
	/**
	 * The Dynamic Load Handler List for each RUIHandler, indexed by RUIHandler IFile
	 */
	private Map<IResource, String> dynamicLoadHandlersMap = new HashMap<IResource, String>();
	
	/**
	 * The context root to use for this deployment
	 */
	private String contextRoot = "";
	
	/**
	 * The generated HTML file contents to deploy ( one byte[] for each locale, indexed by RUIHandler (IFile))
	 */
	private Map<String, DeployableFile> htmlFileContents;
	
	/**
	 * This map holds the byte arrays of all the generated properties files.The map is keys on the
	 * properties file name that the byte array was genertaed from.
	 */
	private HashMap<String, DeployableFile> propertiesFileByteArrays;
	
	/**
	 * Holds the byute arrays of all the generated runtime properties files.
	 */
	private HashMap<String, DeployableFile> runtimePropertiesFileByteArrays;
	
	private HashMap<String, DeployableFile> bindFileByteArrays;
	
	private IDeploymentResultsCollector resultsCollector;

	List<PartInfo> allHandlers = new ArrayList<PartInfo>();
	IEGLSearchScope projSearchScope = null;
	private String eglddName = null;
	
	/**
	 * Constructor
	 * @param contextRoot2 
	 * @param project 
	 */
	public RUIDeploymentModel(RUIApplication ruiApplication, IProject sourceProject, String target, String contextRoot, IDeploymentResultsCollector resultsCollector) throws CoreException{
		this.sourceProject = sourceProject;
		this.target = target;
		this.contextRoot = contextRoot;
		this.resultsCollector = resultsCollector;
		this.eglddName = eglddName;

		startAllHandlerGeneration();
		
		IEGLProject eglProject = EGLCore.create(sourceProject);
		
		intializeRUIHandlers(ruiApplication, eglProject);
		initializeRUISolutionProperties(ruiApplication);
	}


	private void initializeRUISolutionProperties(RUIApplication ruiApplication) {
		List<Parameter> parameterList = ruiApplication.getParameters();
		Map<String, String> parameters = new HashMap<String, String>();
		for (Iterator<Parameter> iterator = parameterList.iterator(); iterator.hasNext();) {
			Parameter parameter = iterator.next();
			parameters.put(parameter.getName(), parameter.getValue());
		}
		
		initializeLocales(parameters);
	}

	private void initializeLocales(Map<String, String> parameters) {
		String handlerLocalesValue = (String)parameters.get(IDeploymentConstants.PARAMETER_LOCALES);
		if (handlerLocalesValue != null) {
			/**
			 * the list should be pairs of code/description
			 */
			String patternStr = ","; //$NON-NLS-1$
			String[] fields = handlerLocalesValue.split(patternStr);
			if (fields.length % 3 != 0) {
				/**
				 * something is not right so default to the preference values
				 */
				HandlerLocalesList handlerLocaleslist = new HandlerLocalesList();
				handlerLocaleslist.buildLocalesList();
				this.handlerLocales = handlerLocaleslist.getLocales();
			} else {
				int i = fields.length / 3;
				for (int j = 0; j < i; j++) {
					int offset = j * 3;
					String code = fields[0 + offset];
					String description = fields[1 + offset];
					String runtimeLocaleCode = fields[2 + offset];
					DeployLocale locale = new DeployLocale(code, description, runtimeLocaleCode);
					this.handlerLocales.add(locale);
				}
			}
		}
	}


	private void intializeRUIHandlers(RUIApplication ruiApplication, IEGLProject eglProject) throws CoreException {
		List<RUIHandler> handlers = ruiApplication.getRUIHandlers();
		for (Iterator<RUIHandler> iterator = handlers.iterator(); iterator.hasNext();) {
			RUIHandler	ruiHandler = iterator.next();			
			String implementation = ruiHandler.getImplementation();
			try {
				IPart element = eglProject.findPart(implementation);
				if(element != null && element.exists()){
					IResource ruiHandlerFile = getIResourceForElement( element, element.getEGLProject().getProject() );
					
//					DotDeployFile deployFile = getDeployFile((IFile) ruiHandlerFile);
//					if(deployFile != null){
						
						if(validateRUIHandler((IFile)ruiHandlerFile, element)){
							sourceRUIHandlers.add((IFile)ruiHandlerFile);
							intializeRUIHandlerProperties(ruiHandler, ruiHandlerFile, eglProject);
						}
//					}else{
//						IStatus status = DeploymentUtilities.createErrorStatus(Messages.bind(Messages.deployment_model_missing_deploy_file, new String[]{element.getFullyQualifiedName()}));
//						throw new CoreException(status);
//					}
				}else{
					// ignore missing RUIHandlers
				}
			} catch (EGLModelException e) {
				Activator.getDefault().log("Error loading RUIHandler for deployment", e);
			}
		}
	}
	

	private void intializeRUIHandlerProperties(RUIHandler ruiHandler, IResource ruiHandlerFile, IEGLProject eglProject) throws CoreException{
		List<Parameter> parameterList = ruiHandler.getParameters();
		Map<String, String> parameters = new HashMap<String, String>();
		for (Iterator<Parameter> parametersIterator = parameterList.iterator(); parametersIterator.hasNext();) {
			Parameter parameter = (Parameter) parametersIterator.next();
			parameters.put(parameter.getName(), parameter.getValue());
		}
		
		initializeHTMLFileName(ruiHandlerFile, parameters);
	}


	private void initializeHTMLFileName(IResource ruiHandlerFile, Map<String, String> parameters) {
		String htmlFileName = (String)parameters.get(IDeploymentConstants.PARAMETER_HTML_FILE_NAME);
		if (htmlFileName != null) {
			this.htmlFileNames.put(ruiHandlerFile, htmlFileName);
		} else {
			/**
			 * default it to the name of the handler
			 */
			String name = ruiHandlerFile.getName();
			IPath path = new Path( name );
			if ( path.segmentCount() > 1 ) {
				path = path.removeFirstSegments( path.segmentCount() - 1 );
			}
			this.htmlFileNames.put(ruiHandlerFile, path.removeFileExtension().toString());
		}
	}
	
	public IProject getSourceProject() {
		return sourceProject;
	}
	
	public List<IFile> getSourceRUIHandlers() {
		return sourceRUIHandlers;
	}
	
	public Set<IFile> getSourceRUIHandlersAndDynamicLoadingHandlers() {
		Set<IFile> set = new HashSet<IFile>();
		set.addAll(sourceRUIHandlers);
		return set;
	}

	public void setSourceRUIHandlers(List<IFile> sourceRUIHandlers) {
		this.sourceRUIHandlers = sourceRUIHandlers;
	}

	public Map<String, DeployableFile> getHtmlFileContents() {
		return htmlFileContents;
	}

	public void startAllHandlerGeneration() {
		runtimePropertiesFileByteArrays = new HashMap<String, DeployableFile>();
		propertiesFileByteArrays = new HashMap<String, DeployableFile>();
		htmlFileContents = new HashMap<String, DeployableFile>();
		bindFileByteArrays = new HashMap<String, DeployableFile>();
	}
	
	public void generateHandler(IFile ruiHandler,
			IFileLocator iFileLocator, org.eclipse.edt.ide.rui.utils.FileLocator fileLocator, HashMap<String, String> eglProperties, 
			DeploymentResultMessageRequestor messageRequestor, IProgressMonitor monitor) {
		
		/**
		 * Read in the runtime properties files. These are the same for all locales.
		 * These files have nothing to do with the runtime messages
		 */
//		RuntimePropertiesFileGenerator gen = new RuntimePropertiesFileGenerator();
//		for (Iterator<String> it = XmlDeployFileUtil.getAllRuntimePropertiesFiles(relatedDeployFiles).iterator(); it.hasNext() && !monitor.isCanceled(); ) {
//			String propertiesFile = it.next();
//			if(!runtimePropertiesFileByteArrays.containsKey(propertiesFile)){
//				IFile file = iFileLocator.findFile(RuntimePropertiesFileUtil.getPropertiesFileName(propertiesFile));
//				if (file != null) {
//					byte[] bytes = gen.generatePropertiesFile(file, propertiesFile);
//					if (bytes != null) {
//						runtimePropertiesFileByteArrays.put(propertiesFile, new DeployableFile(bytes));
//					}
//					else {
//						messageRequestor.addMessage(EGLMessage.createEGLDeploymentErrorMessage(
//								EGLMessage.EGL_DEPLOYMENT_FAILED_CREATE_RT_PROPS_FILE, 
//								null,
//								new String[] { propertiesFile }));
//					}
//				}
//			}
//		}
//		
//		/**
//		 * generate an HTML file and .js properties files for each handler locale selected by the user
//		 */
		for (Iterator<DeployLocale> localeIterator = handlerLocales.iterator(); localeIterator.hasNext() && !monitor.isCanceled();) {
			boolean errorLocaleProcessing = false;
			DeployLocale locale = localeIterator.next();
			String userLocaleCode = locale.getCode();
//			/**
//			 * deal with the properties file(s). We need to find them and then generate a .js file for them
//			 */
//			Set<String> propertiesFiles = XmlDeployFileUtil.getAllPropertiesFiles(relatedDeployFiles);
//			for (Iterator<String> propertiesFileIterator = propertiesFiles.iterator(); propertiesFileIterator.hasNext();) {
//				String propertiesFileName = propertiesFileIterator.next();
//				/**
//				 * build the properties file name
//				 */
//				PropertiesFileUtil propFile = new PropertiesFileUtil(propertiesFileName, userLocaleCode);
//				errorLocaleProcessing = true;
//				String[] propFileNames = propFile.generatePropertiesFileNames();
//				for ( int i = 0; i < propFileNames.length && !monitor.isCanceled(); i++ ) {
//					String name = propFileNames[ i ];
//					IFile propertiesFile = iFileLocator.findFile( IConstants.PROPERTIES_FOLDER_NAME + File.separator + name  ); 
//					if ( propertiesFile != null && propertiesFile.exists() ) {
//						errorLocaleProcessing = false;
//						NLSPropertiesFileGenerator generator = new NLSPropertiesFileGenerator();
//						byte[] bytes = generator.generatePropertiesFile(propertiesFile, propFile.getBundleName());
//						if( bytes != null ){
//							propertiesFileByteArrays.put(name, new DeployableFile(bytes));
//						}
//						else {
//							messageRequestor.addMessage(EGLMessage.createEGLDeploymentErrorMessage(
//									EGLMessage.EGL_DEPLOYMENT_FAILED_CREATE_NLS_FILE, 
//									null,
//									new String[] { name }));
//						}
//						break;
//					}
//				}
//				
//				if ( errorLocaleProcessing ) {
//					messageRequestor.addMessage(EGLMessage.createEGLDeploymentErrorMessage(
//							EGLMessage.EGL_DEPLOYMENT_LOCALE_PROCESSING_FAILED, 
//							null,
//							new String[] { propFileNames[0] }));
//				}
//			}
//			
//			List<String> deployedDDFiles = new ArrayList<String>();
//			try{
//				for (int i = 0; i < relatedDeployFiles.length && !monitor.isCanceled(); i++) {
//					//DotDeployFile.BuildDescriptor bd = DotDeployFileUtil.getBuildDescriptor(sourceProject.getName(), null, deployFiles[i]);
//					DotDeployFile deployFile = relatedDeployFiles[i];
//					DotDeployFile.BuildDescriptor bd = null;
//					if( deployFile.getBuildDescriptors() != null ){
//						bd = deployFile.getBuildDescriptors().values().iterator().next();
//					}
//					if(bd != null &&
//							bd.deploymentDescriptorName != null && bd.deploymentDescriptorName.length() > 0 && 
//							!deployedDDFiles.contains(bd.deploymentDescriptorName) &&
//							relatedDeployFiles[i].getImports() != null && relatedDeployFiles[i].getImports().size() > 0){
//						DeploymentDescriptor ir = DeploymentDescriptorFileUtil.getDeploymentDescriptor(bd.deploymentDescriptorName, sourceProject, messageRequestor);
//						deployedDDFiles.add(bd.deploymentDescriptorName);
//						
//						byte[] bytes = new DeploymentDescGenerator().generateBindFile(ir);
//						if( bytes != null ) {
//							bindFileByteArrays.put(bd.deploymentDescriptorName, new DeployableFile(bytes));
//						}
//						else {
//							messageRequestor.addMessage(EGLMessage.createEGLDeploymentErrorMessage(
//									EGLMessage.EGL_DEPLOYMENT_FAILED_CREATE_BIND_FILE, 
//									null,
//									new String[] { bd.deploymentDescriptorName + DeploymentDescriptorFileUtil.IR_SUFFIX }));
//						}
//					}
//				}
//			} catch (Exception e) {
//				messageRequestor.addMessage(EGLMessage.createEGLDeploymentErrorMessage(
//						EGLMessage.EGL_DEPLOYMENT_FAILED_LOCATE_CHILD_DOT_DEPLOY_FILES, 
//						null,
//						new String[] { handlerDotDeployFile.getPartName() }));
//				messageRequestor.addMessage(EGLMessage.createEGLDeploymentErrorMessage(
//						EGLMessage.EGL_DEPLOYMENT_EXCEPTION, 
//						null,
//						new String[] { DeploymentUtilities.createExceptionMessage(e) }));
//			}
//
			if(sourceRUIHandlers.contains(ruiHandler)){
				//generate HTML for source handlers, not for dynamic loading handlers
				if (!messageRequestor.isError() && !monitor.isCanceled()) {
					/**
					 * pass the HTML generator the locale that the HTML file will be supporting. This is used as input to see
					 * what set of runtime messages ought to be included
					 */
					eglProperties.put(IConstants.HTML_FILE_LOCALE, userLocaleCode);
					eglProperties.put(IConstants.DEFAULT_LOCALE_PARAMETER_NAME, locale.getRuntimeLocaleCode());
					byte[] htmlFile;
					try {
						htmlFile = generateHandlerHTML(ruiHandler, eglProperties, userLocaleCode, locale.getRuntimeLocaleCode(), eglddName, messageRequestor);
						/**
						 * store the html file bytes into a map for retrieval
						 */
						htmlFileContents.put(userLocaleCode, new DeployableFile(htmlFile));
					} catch (Exception e) {
						messageRequestor.addMessage(EGLMessage.createEGLDeploymentErrorMessage( 
								EGLMessage.EGL_DEPLOYMENT_FAILED_CREATE_HTML_FILE, null, null ));
						messageRequestor.addMessage(EGLMessage.createEGLDeploymentErrorMessage(
								EGLMessage.EGL_DEPLOYMENT_EXCEPTION, 
								null,
								new String[] { DeploymentUtilities.createExceptionMessage(e) }));
					}
				}
			}
		}
//		if(sourceRUIHandlersDynamic.values().contains(ruiHandler)){
//			//proceed dynamic load handlers, put the required resource to a list
//			for (Iterator<String> it = XmlDeployFileUtil.getAllIncludes(relatedDeployFiles).iterator(); it.hasNext() && !monitor.isCanceled(); ) {
//				String resource = it.next();
//				dynamicLoadResource.add(resource);
//			}
//		}

	}
	
	/**
	 * Generates the passed RUI handler into the passed output file. The output being an HTML file that contains the JavaScript 
	 * generated to support the functionality of the RUI handler
	 * 
	 * @param input The RUI handler to generate
	 * @param output The <code>IFile</code> in to which to write the generate code
	 * @param eglParameters A <code>HashMap</code> of key value pairs to include into the generated code as parameters 
	 * @param monitor The progress monitor to use
	 * 
	 * @throws CoreException
	 */
	public static final byte[] generateHandlerHTML(IFile input, HashMap eglParameters, 
			String userMsgLocale, String runtimeMsgLocale, String eglddName, IGenerationMessageRequestor messageRequestor) throws CoreException  {
		GenerateHTMLFile op = new GenerateHTMLFile(input, eglParameters, userMsgLocale, runtimeMsgLocale, eglddName);
		return op.execute(messageRequestor);
	}
	
	private boolean validateRUIHandler(IFile ruiHandler, IPart element) {
		//TODO - EDT
/*		try{
			IEGLBuildDescriptorLocator bdLocator = EGLBasePlugin.getPlugin().getBdLocator();
			IEGLPartWrapper wrapper = bdLocator.locateDefaultBuildDescriptor( IEGLBuildDescriptorLocator.RUNTIME_DEFAULT_JAVASCRIPT_BUILD_DESCRIPTOR_TYPE, ruiHandler );
			if (wrapper != null) {
				final CommandRequestor req = new IDECommandRequestor();
				GeneratePartsOperation dummyOp  = new GeneratePartsOperation() {
					public Generator[] getGenerators() {
						return new Generator[0];
					}
					public boolean isGenerateThruLibs() {
						return true;
					}
					public boolean isDebug() {
						return false;
					}
					public CommandRequestor getRequestor() {
						return req;
					}
				};
				
				GenerateEnvironment environment = GenerateEnvironmentManager.getInstance().getGenerateEnvironment(ruiHandler.getProject(), false);
				DotDeployFile deployFile = getDeployFile(ruiHandler);
				
				try{
					Part part = environment.findPart(InternUtil.intern(DeploymentUtilities.convertPackage(deployFile.getPackageName())), InternUtil.intern(deployFile.getPartName()));
						
					if(part != null){
						ValidatorMessageRequestor messageRequestor = new ValidatorMessageRequestor();
							
						IPath path = new Path(wrapper.getPartPath());
				
						BuildDescriptor bd = dummyOp.createBuildDescriptor(ResourcesPlugin.getWorkspace().getRoot().getFile(path), wrapper.getPartName(), messageRequestor);
						bd.setGenerationMessageRequestor( messageRequestor );
						dummyOp.createNooverrideBuildDescriptor(req, messageRequestor);
						bd.setCommandRequestor(req);
						bd.setPart(part);
						bd.setPartFileName(ruiHandler.getFullPath().toString());
						bd.setProjectName(ruiHandler.getProject().getName());
						bd.setGenerationStatusRequestor(new IGenerationStatusRequestor() {
							public void status(String status) {
							}
	
							public boolean isCanceled() {
								return false;
							}
						});
						bd.setEnvironment(environment);
						
						dummyOp.generate(part, bd, messageRequestor);
						
						if(messageRequestor.isError()){
							if(messageRequestor.isCompileError()){
								addDeploymentMessage(DeploymentUtilities.createDeployMessage(IStatus.ERROR, Messages.bind(Messages.deployment_invalid_rui_handler_compile_error, element.getFullyQualifiedName())));
							}else if(messageRequestor.isGenerationError()){
								addDeploymentMessage(DeploymentUtilities.createDeployMessage(IStatus.ERROR, Messages.bind(Messages.deployment_invalid_rui_handler_generation_error, element.getFullyQualifiedName())));
							}
							return false;
						}else{
							return true;
						}					
					}else{
						addDeploymentMessage(DeploymentUtilities.createDeployMessage(IStatus.ERROR, Messages.bind(Messages.deployment_invalid_rui_handler_missing_ir, element.getFullyQualifiedName())));
					}
				}catch(PartNotFoundException e){
					addDeploymentMessage(DeploymentUtilities.createDeployMessage(IStatus.ERROR, Messages.bind(Messages.deployment_invalid_rui_handler_missing_ir, element.getFullyQualifiedName())));
				}
			}else{
				addDeploymentMessage(DeploymentUtilities.createDeployMessage(IStatus.ERROR, Messages.bind(Messages.deployment_invalid_rui_handler_missing_bd, element.getFullyQualifiedName())));
			}
		}catch(Exception e){
			addDeploymentMessage(DeploymentUtilities.createDeployMessage(IStatus.ERROR, Messages.bind(Messages.deployment_invalid_rui_handler_exception, element.getFullyQualifiedName())));
			Activator.getDefault().log("RUIDeploymentModel.validateRUIHandler - Exception thrown while validating RUIHandler " + element.getFullyQualifiedName(), e);
		}
*/
		return true;
	}
	
	private IResource getIResourceForElement(IPart element, IProject eglProject) {
		IResource ruiHandlerFile = null;
//		if ( element instanceof BinaryPart ) {
//			ClassFile file = (ClassFile) ((BinaryPart)element).getClassFile();
//			IEGLElement parent = file.getParent();
//			while( ! (parent != null && parent instanceof EglarPackageFragmentRoot) ) {
//				parent = parent.getParent();
//			}
//			try {
//				String eglarPath = "";
//				if ( parent.getResource() != null ) {
//					eglarPath = parent.getResource().getLocation().toString();
//				} else {
//					eglarPath = parent.getPath().toString();
//				}
//				EglarFile eglarFile = EglarFileCache.instance.getEglarFile( eglarPath );
//				//String path = eglarFile.getManifest().getGeneratedJavascriptFolder() + EGLProjectInfoUtility.getTargetJavaScriptFolder() + IPath.SEPARATOR + element.getFullyQualifiedName( '.' ).replaceAll("\\.", "/") + ".deploy";
//				String path = (element.getFullyQualifiedName( '.' ).replaceAll("\\.", "/") + ".ir").toLowerCase();
//				ruiHandlerFile = new EglarFileResource( eglarFile, new ZipEntry( path  ), "", eglProject );
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else {
			ruiHandlerFile = element.getResource();
//		}
		return ruiHandlerFile;
	}


	public HashMap<String, DeployableFile> getPropertiesFileByteArrays() {
		return propertiesFileByteArrays;
	}
	
	/**
	 * Add a message to the message list. This list is displayed at the end of the wizard operation.
	 * See <code>RUIDeployUtilities.createDeployMessage(int severity, String message)</code>
	 * 
	 * @param status The message wrapped in a n IStatus object
	 */
	public void addDeploymentMessage(IStatus status) {
		this.resultsCollector.addMessage(status);
	}
	
	public List<DeployLocale> getHandlerLocales() {
		return handlerLocales;
	}
	
	public HashMap<String, DeployableFile> getRuntimePropertiesFileByteArrays() {
		return runtimePropertiesFileByteArrays;
	}
	
	public HashMap<String, DeployableFile> getBindFileByteArrays() {
		return bindFileByteArrays;
	}

	public String getTarget() {
		return target;
	}
	
	public String getContextRoot(){
		return contextRoot;
	}
	
	public void setContextRoot(String contextRoot) {
		this.contextRoot = contextRoot;	
	}
	
	public Map<IResource, String> getHTMLFileNames(){
		return htmlFileNames;
	}
	
	public Map<IResource, String> getDynamicLoadHandlers(){
		return dynamicLoadHandlersMap;
	}
}
