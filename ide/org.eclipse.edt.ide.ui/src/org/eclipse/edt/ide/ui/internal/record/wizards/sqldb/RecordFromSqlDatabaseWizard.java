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
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.datatools.connectivity.IManagedConnection;
import org.eclipse.datatools.connectivity.sqm.core.connection.ConnectionInfo;
import org.eclipse.datatools.connectivity.sqm.internal.core.util.ConnectionUtil;
import org.eclipse.edt.compiler.internal.EGLBasePlugin;
import org.eclipse.edt.gen.generator.eglsource.EglSourceGenerator;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.record.NewRecordSummaryPage;
import org.eclipse.edt.ide.ui.internal.record.NewRecordWizard;
import org.eclipse.edt.ide.ui.internal.record.conversion.sqldb.DataToolsObjectsToEglSource;
import org.eclipse.edt.ide.ui.templates.wizards.TemplateWizard;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

public class RecordFromSqlDatabaseWizard extends TemplateWizard implements IWorkbenchWizard, IPageChangingListener {

	private RecordFromSqlDatabaseWizardConfiguration config;

	protected RecordFromSqlDatabasePage sqlDbPage;
	protected NewRecordSummaryPage summaryPage;
	protected IStructuredSelection selection;

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
				final String results = generateRecords(monitor);

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						summaryPage.setContent(results);
					}
				});

			}
		};
	}

	protected IRunnableWithProgress createFinishOperation() {
		return new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				final String results = generateRecords(monitor);
				((NewRecordWizard) getParentWizard()).setContentObj(results);
			}
		};

	}

	protected String generateRecords(IProgressMonitor monitor) throws InterruptedException {
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
						generator.getContext().put(DataToolsObjectsToEglSource.DATA_DEFINITION_OBJECT, connection.getDatabaseDefinition());
						generator.generate(table);
						buffer.append(generator.getResult());
						
						monitor.worked(1);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}

		if (monitor.isCanceled()) {
			throw new InterruptedException();
		}

		return buffer.toString();
	}
}
