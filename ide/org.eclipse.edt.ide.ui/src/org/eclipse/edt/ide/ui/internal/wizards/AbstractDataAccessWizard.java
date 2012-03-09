/*******************************************************************************
 * Copyright 漏 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

package org.eclipse.edt.ide.ui.internal.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.IManagedConnection;
import org.eclipse.datatools.connectivity.sqm.core.connection.ConnectionInfo;
import org.eclipse.datatools.connectivity.sqm.internal.core.util.ConnectionUtil;
import org.eclipse.datatools.connectivity.ui.dse.dialogs.ConnectionDisplayProperty;
import org.eclipse.edt.gen.generator.eglsource.EglSourceContext;
import org.eclipse.edt.gen.generator.eglsource.EglSourceGenerator;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.edt.ide.internal.sql.util.EGLSQLUtility;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.dataaccess.conversion.sqldb.DTO2EglSource;
import org.eclipse.edt.ide.ui.internal.dataaccess.conversion.sqldb.DataToolsSqlTemplateConstants;
import org.eclipse.edt.ide.ui.internal.deployment.Binding;
import org.eclipse.edt.ide.ui.internal.deployment.Bindings;
import org.eclipse.edt.ide.ui.internal.deployment.Deployment;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDRootHelper;
import org.eclipse.edt.ide.ui.internal.record.conversion.IMessageHandler;
import org.eclipse.edt.ide.ui.internal.util.CoreUtility;
import org.eclipse.edt.ide.ui.internal.util.UISQLUtility;
import org.eclipse.edt.ide.ui.templates.wizards.TemplateWizard;
import org.eclipse.edt.ide.ui.wizards.BindingSQLDatabaseConfiguration;
import org.eclipse.edt.ide.ui.wizards.EGLPartConfiguration;
import org.eclipse.edt.ide.ui.wizards.MultiEGLFileOperation;
import org.eclipse.edt.ide.ui.wizards.ProjectConfiguration;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.osgi.service.prefs.BackingStoreException;

public abstract class AbstractDataAccessWizard extends TemplateWizard implements IWorkbenchWizard, IPageChangingListener, IPageChangedListener, IMessageHandler {

	protected DTOConfiguration config;
	protected DTOConfigPage sqlDbPage;
	protected EGLCodePreviewPage summaryPage;
	protected IStructuredSelection selection;
	protected List<String> messages = new ArrayList<String>();
	protected Hashtable<String, String> sourceFileContents;
	
	protected String javaPackageName;
	protected String javaJsPackageName;
	
	protected String generatingProgressMonitorText;
	protected String dto2EGLContributor;
	protected boolean needConfigGenerator = false;
	protected int steps;

	public AbstractDataAccessWizard() {
		super();

		setNeedsProgressMonitor(true);
		setDialogSettings(EDTUIPlugin.getDefault().getDialogSettings());

		config = new DTOConfiguration();

		initPages();
	}

	protected abstract void initPages();

	public void addPages() {
		addPage(sqlDbPage);
		addPage(summaryPage);
	}

	public void init(IWorkbench arg0, IStructuredSelection selection) {
		this.selection = selection;
	}

	public boolean performFinish() {
		try {
			getContainer().run(true, true, createFinishOperation());
		} catch (InterruptedException ex) {
			ex.printStackTrace();
			EGLLogger.log(this, ex.toString());	
			return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			EGLLogger.log(this, ex.toString());	
			return false;
		}
		return true;
	}

	public void setContainer(IWizardContainer wizardContainer) {
		super.setContainer(wizardContainer);

		if (wizardContainer != null) {
			((WizardDialog) wizardContainer).addPageChangingListener(this);
			((WizardDialog) wizardContainer).addPageChangedListener(this);
		}
	}

	public void handlePageChanging(PageChangingEvent event) {
		if (event.getTargetPage() == summaryPage) {
			IRunnableWithProgress op = createPreviewOperation();
			try {
				getContainer().run(true, true, op);
			} catch (InterruptedException ex) {
				event.doit = false;
				ex.printStackTrace();
				EGLLogger.log(this, ex.toString());	
			} catch (Exception ex) {
				ex.printStackTrace();
				EGLLogger.log(this, ex.toString());	
			}
		}
	}
	
	public void pageChanged(PageChangedEvent event){
		if (event.getSelectedPage() == summaryPage) {
			summaryPage.formatSourceDocument();
		}else if(event.getSelectedPage() == sqlDbPage){
			sourceFileContents = null;
		}
	}

	protected IRunnableWithProgress createPreviewOperation() {
		return new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				if (messages != null) {
					messages.clear();
				}
				ConnectionInfo connection = getConnectionInfo();
				if (connection != null) {
					sourceFileContents = generateEGLCode(monitor, connection);

					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							summaryPage.setsourceFileContentTable(sourceFileContents);
							summaryPage.setDeafultSelectFile(getMainEGLFile());
							List<String> messages = getMessages();
							if (messages != null && messages.size() > 0) {
								summaryPage.setMessages(messages);
							}
						}
					});
				} else {
					throw new InterruptedException("Can not get the data base connection!");
				}

			}
		};
	}

	protected ConnectionInfo getConnectionInfo() {
		IManagedConnection managedConnection = config.getDatabaseConnection().getManagedConnection(ConnectionUtil.CONNECTION_TYPE);
		if (managedConnection != null) {
			return (ConnectionInfo) managedConnection.getConnection().getRawConnection();
		} else {
			return null;
		}
	}

	protected IRunnableWithProgress createFinishOperation() {
		return new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				
				if(sourceFileContents == null){
					ConnectionInfo connection = getConnectionInfo();
					sourceFileContents = generateEGLCode(monitor, connection);
				}
				
				
				EGLPartConfiguration epc = getConfiguration();

				MultiEGLFileOperation mefo = new MultiEGLFileOperation(epc, sourceFileContents);
				try {
					mefo.execute(monitor);
					epc.setFile(mefo.getFile(getMainEGLFile()));
				} catch (CoreException e) {
					throw new InterruptedException(e.toString());
				}
				
				
				if(needConfigGenerator){
					setupPackageGenerator();
				}
				if (config.isSaveConnectionToDeploymentDescriptor()) {
					setupSqlBinding(getConnectionInfo(), true);
				}
			}
		};
	}

	public abstract String getMainEGLFile();
	
	public void setupPackageGenerator() {
		EGLPartConfiguration epc = getConfiguration();
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(epc.getProjectName());
		IEGLProject eproject = EGLCore.create(project);
		IPath sourcePath = new Path(epc.getContainerName());
		IPackageFragmentRoot root;
		try {
			root = eproject.findPackageFragmentRoot(sourcePath.makeAbsolute());
			IPackageFragment frag = root.getPackageFragment(javaPackageName);
			ProjectSettingsUtility.setGeneratorIds(frag.getResource(), new String[] { ProjectConfiguration.JAVA_GENERATOR_ID });

			frag = root.getPackageFragment(javaJsPackageName);
			ProjectSettingsUtility.setGeneratorIds(frag.getResource(), new String[] { ProjectConfiguration.JAVA_GENERATOR_ID,  ProjectConfiguration.JAVASCRIPT_GENERATOR_ID, ProjectConfiguration.JAVASCRIPT_DEV_GENERATOR_ID});

		} catch (EGLModelException ex) {
			ex.printStackTrace();
			EGLLogger.log(this, ex.toString());	
		} catch (BackingStoreException ex) {
			ex.printStackTrace();
			EGLLogger.log(this, ex.toString());	
		}
	}

	public EGLPartConfiguration getConfiguration() {
		EGLPartConfiguration configuration = (EGLPartConfiguration) ((EGLPartWizard) getParentWizard()).getConfiguration();
		return configuration;
	}

	protected Hashtable<String, String> generateEGLCode(IProgressMonitor monitor, ConnectionInfo connection) throws InterruptedException {

		DTO2EglSource d = new DTO2EglSource(dto2EGLContributor);

		monitor.beginTask(generatingProgressMonitorText, config.getSelectedTables().size() + steps);

		EglSourceGenerator generator = new EglSourceGenerator(d);
		EglSourceContext context = generator.makeContext(d);
		setupEGLSourceContext(context, connection, monitor);

		try {
			d.generate(config, generator, null);
		} catch (Exception ex) {
			addMessage(ex.getMessage());
			EGLLogger.log(this, ex.toString());	
		}

		if (monitor.isCanceled()) {
			throw new InterruptedException();
		}
		return generator.getResult();
	}

	protected String setupSqlBinding(ConnectionInfo connection, boolean isSaved) {
		String bindingName = null;
		BindingSQLDatabaseConfiguration sqlConfig = new BindingSQLDatabaseConfiguration();
		IFile eglddFile = CoreUtility.getOrCreateEGLDDFileHandle(sqlConfig);// getEGLDDFileHandle(sqlConfig);

		if (eglddFile != null) {
			EGLDeploymentRoot deploymentRoot = null;
			try {
				deploymentRoot = EGLDDRootHelper.getEGLDDFileSharedWorkingModel(eglddFile, false);

				IConnectionProfile profile = connection.getConnectionProfile();
				ConnectionDisplayProperty[] properties = EGLSQLUtility.getConnectionDisplayProperties(profile);
				UISQLUtility.setBindingSQLDatabaseConfiguration(sqlConfig, properties);
				sqlConfig.setUseUri(true);
				Deployment deployment = deploymentRoot.getDeployment();
				DeploymentFactory factory = DeploymentFactory.eINSTANCE;

				Bindings bindings = deployment.getBindings();
				if (bindings == null) {
					bindings = factory.createBindings();
					deployment.setBindings(bindings);
				}
				EList<Binding> existedBindings = bindings.getBinding();

				boolean existed = false;
				for (Binding binding : existedBindings) {
					if (org.eclipse.edt.javart.resources.egldd.Binding.BINDING_DB_SQL.equals(binding.getType())) {
						if(binding.isUseURI()){
							if(binding.getUri()!=null && binding.getUri().equals("workspace://" + profile.getName())){
								existed = true;
								bindingName = binding.getName();
							}
						}
					}

					if (existed) {
						break;
					}
				}

				if (!existed) {
					if (isSaved) {
						sqlConfig.setUri("workspace://" + profile.getName());
						Binding b = (Binding) sqlConfig.executeAddBinding(bindings);
						bindingName = b.getName();
					} else {
						bindingName = sqlConfig.getValidBindingName();
					}
				}

				// persist the file if we're the only client
				if (!EGLDDRootHelper.isWorkingModelSharedByUserClients(eglddFile))
					EGLDDRootHelper.saveEGLDDFile(eglddFile, deploymentRoot);
			} finally {
				if (deploymentRoot != null)
					EGLDDRootHelper.releaseSharedWorkingModel(eglddFile, false);
			}
		}
		return bindingName;
	}

	public void addMessage(String message) {
		if (this.messages == null) {
			messages = new ArrayList<String>();
		}
		this.messages.add(message);
	}

	public List<String> getMessages() {
		return this.messages;
	}

	public void setupEGLSourceContext(EglSourceContext context, ConnectionInfo connection, IProgressMonitor monitor) {

		context.put(DTO2EglSource.DATA_DEFINITION_OBJECT, connection.getDatabaseDefinition());
		context.put(DTO2EglSource.TABLE_NAME_QUALIFIED, config.isQualifiedTableNames());
		context.put(DTO2EglSource.DB_MESSAGE_HANDLER, this);
		context.put(DTO2EglSource.PROGRESS_MONITOR, monitor);

		String bindingName = "sqlBindingName";
		if (config.isSaveConnectionToDeploymentDescriptor()) {
			bindingName = setupSqlBinding(connection, false);
		}
		context.getVariables().put(DataToolsSqlTemplateConstants.SQL_BINDING_NAME, bindingName);

		String basePackage = getConfiguration().getFPackage();
		context.getVariables().put(DataToolsSqlTemplateConstants.BASE_PACKAGE_NAME, basePackage);
		context.getVariables().put(DataToolsSqlTemplateConstants.SUB_PACKAGE_NAME, "common");
		context.getVariables().put(DataToolsSqlTemplateConstants.DATABASE_NAME, config.getDatabaseName());
		

		if(needConfigGenerator){
			if (basePackage == null || basePackage.equals("")) {
				javaPackageName = config.getDatabaseName();
			} else {
				javaPackageName = basePackage + "." + config.getDatabaseName();
			}
			javaJsPackageName = javaPackageName + ".common";
			context.getVariables().put(DataToolsSqlTemplateConstants.JAVA_PACKAGE_NAME, javaPackageName);
			context.getVariables().put(DataToolsSqlTemplateConstants.JAVA_JS_PACKAGE_NAME, javaJsPackageName);
		}
	}

}