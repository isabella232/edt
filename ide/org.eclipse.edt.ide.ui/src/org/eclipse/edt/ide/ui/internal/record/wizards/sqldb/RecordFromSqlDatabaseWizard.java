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
package org.eclipse.edt.ide.ui.internal.record.wizards.sqldb;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.IManagedConnection;
import org.eclipse.datatools.connectivity.sqm.core.connection.ConnectionInfo;
import org.eclipse.datatools.connectivity.sqm.internal.core.util.ConnectionUtil;
import org.eclipse.datatools.connectivity.ui.dse.dialogs.ConnectionDisplayProperty;
import org.eclipse.edt.compiler.internal.EGLBasePlugin;
import org.eclipse.edt.gen.EglContext;
import org.eclipse.edt.gen.generator.eglsource.EglSourceGenerator;
import org.eclipse.edt.ide.internal.sql.util.EGLSQLUtility;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.deployment.Binding;
import org.eclipse.edt.ide.ui.internal.deployment.Bindings;
import org.eclipse.edt.ide.ui.internal.deployment.Deployment;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.deployment.Parameter;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDRootHelper;
import org.eclipse.edt.ide.ui.internal.record.NewRecordSummaryPage;
import org.eclipse.edt.ide.ui.internal.record.NewRecordWizard;
import org.eclipse.edt.ide.ui.internal.record.conversion.IMessageHandler;
import org.eclipse.edt.ide.ui.internal.record.conversion.sqldb.DataToolsObjectsToEglSource;
import org.eclipse.edt.ide.ui.internal.util.CoreUtility;
import org.eclipse.edt.ide.ui.internal.util.UISQLUtility;
import org.eclipse.edt.ide.ui.internal.wizards.EGLFileWizard;
import org.eclipse.edt.ide.ui.templates.wizards.TemplateWizard;
import org.eclipse.edt.ide.ui.wizards.BindingSQLDatabaseConfiguration;
import org.eclipse.edt.javart.resources.egldd.SQLDatabaseBinding;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

public class RecordFromSqlDatabaseWizard extends TemplateWizard implements IWorkbenchWizard, IPageChangingListener,IMessageHandler {

	private RecordFromSqlDatabaseWizardConfiguration config;

	protected RecordFromSqlDatabasePage sqlDbPage;
	protected NewRecordSummaryPage summaryPage;
	protected IStructuredSelection selection;
	
	protected List<String> messages = new ArrayList<String>();

	public RecordFromSqlDatabaseWizard() {
		super();

		setNeedsProgressMonitor(true);

		setDialogSettings(EDTUIPlugin.getDefault().getDialogSettings());

		// @bd1a start
		boolean isBidi = EGLBasePlugin.getPlugin().getPreferenceStore().getBoolean(EGLBasePlugin.BIDI_ENABLED_OPTION);
		if (isBidi)
			config = new RecordFromSqlDatabaseWizardConfigurationBidi();
		else
			config = new RecordFromSqlDatabaseWizardConfiguration();

		sqlDbPage = new RecordFromSqlDatabasePage(config);
		summaryPage = new NewRecordSummaryPage(selection);
	}

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
			return false;
		} catch (Exception ex) {
			// ex.printStackTrace();
		}

		return true;
	}

	public void setContainer(IWizardContainer wizardContainer) {
		super.setContainer(wizardContainer);

		if (wizardContainer != null) {
			((WizardDialog) wizardContainer).addPageChangingListener(this);
		}
	}

	public void handlePageChanging(PageChangingEvent event) {
		if (event.getTargetPage() == summaryPage) {
			IRunnableWithProgress op = createPreviewOperation();
			try {
				getContainer().run(true, true, op);
			} catch (InterruptedException ex) {
				event.doit = false;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	protected IRunnableWithProgress createPreviewOperation() {
		return new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				if(messages != null){
					messages.clear();
				}
				final String results = generateRecords(monitor,false);

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						summaryPage.setContent(results);
						List<String> messages = getMessages();
					    if(messages!=null && messages.size() > 0) {
					    	summaryPage.setMessages(messages);
					    }
					}
				});

			}
		};
	}

	protected IRunnableWithProgress createFinishOperation() {
		return new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				final String results = generateRecords(monitor,true);
				((NewRecordWizard) getParentWizard()).setContentObj(results);
			}
		};

	}

	protected String generateRecords(IProgressMonitor monitor,boolean isFinished) throws InterruptedException {
		List tables = config.getSelectedTables();
		monitor.beginTask("Generating record parts", tables.size());

		final StringBuffer buffer = new StringBuffer();

		IManagedConnection managedConnection = config.getDatabaseConnection().getManagedConnection(ConnectionUtil.CONNECTION_TYPE);
		if (managedConnection != null) {
			ConnectionInfo connection = (ConnectionInfo) managedConnection.getConnection().getRawConnection();

			DataToolsObjectsToEglSource d = new DataToolsObjectsToEglSource();
		
			for (Object object : tables) {
				if (monitor.isCanceled()) {
					break;
				} else {
					try {
						org.eclipse.datatools.modelbase.sql.tables.Table table = (org.eclipse.datatools.modelbase.sql.tables.Table) object;
						monitor.subTask(table.getName());

						EglSourceGenerator generator = new EglSourceGenerator(d);
						EglContext context = generator.makeContext(d);
						context.put(DataToolsObjectsToEglSource.DATA_DEFINITION_OBJECT, connection.getDatabaseDefinition());
						context.put(DataToolsObjectsToEglSource.TABLE_NAME_QUALIFIED, config.isQualifiedTableNames());
						context.put(DataToolsObjectsToEglSource.DB_MESSAGE_HANDLER, this);
						d.generate(table, generator, null);
						buffer.append(generator.getResult());
						
						monitor.worked(1);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
			
			if(isFinished && config.isSaveConnectionToDeploymentDescriptor()) {
				IFile eglddFile = CoreUtility.getOrCreateEGLDDFileHandle(((EGLFileWizard)this.getParentWizard()).getConfiguration());
				
				if(eglddFile != null) {
					EGLDeploymentRoot deploymentRoot = null;
					try {
						deploymentRoot = EGLDDRootHelper.getEGLDDFileSharedWorkingModel(eglddFile, false);
						
						IConnectionProfile profile = connection.getConnectionProfile();
						ConnectionDisplayProperty[] properties = EGLSQLUtility.getConnectionDisplayProperties(profile);
						BindingSQLDatabaseConfiguration sqlConfig = new BindingSQLDatabaseConfiguration();
						UISQLUtility.setBindingSQLDatabaseConfiguration(sqlConfig, properties);
						
						Deployment deployment = deploymentRoot.getDeployment();
						DeploymentFactory factory = DeploymentFactory.eINSTANCE;
						
						Bindings bindings = deployment.getBindings();
						if(bindings == null) {
							bindings = factory.createBindings();
							deployment.setBindings(bindings);
						}
						EList<Binding> existedBindings = bindings.getBinding();
						
						boolean existed = false;
						for(Binding binding : existedBindings) {
							if (org.eclipse.edt.javart.resources.egldd.Binding.BINDING_DB_SQL.equals(binding.getType())) {
								if (binding.getParameters() != null) {
									for (Parameter p : binding.getParameters().getParameter()) {
										if(SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_dbms.equals(p.getName()) && sqlConfig.getDbms().equals(p.getValue())) {
											existed = true;
											break;
										}
									}
								}
							}
							
							if (existed) {
								break;
							}
						}
						
						if(!existed) {
							sqlConfig.executeAddBinding(bindings);
						}
						
						//persist the file if we're the only client 
						if(!EGLDDRootHelper.isWorkingModelSharedByUserClients(eglddFile))
							EGLDDRootHelper.saveEGLDDFile(eglddFile, deploymentRoot);
					}finally{
						if(deploymentRoot != null)
							EGLDDRootHelper.releaseSharedWorkingModel(eglddFile, false);
					}
				}
			}
		}

		if (monitor.isCanceled()) {
			throw new InterruptedException();
		}

		return buffer.toString();
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
}
