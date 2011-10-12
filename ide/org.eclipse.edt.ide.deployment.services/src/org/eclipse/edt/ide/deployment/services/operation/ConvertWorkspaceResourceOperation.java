/*******************************************************************************
 * Copyright �2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.services.operation;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.ui.dse.dialogs.ConnectionDisplayProperty;
import org.eclipse.edt.compiler.internal.util.Encoder;
import org.eclipse.edt.ide.deployment.core.model.DeploymentDesc;
import org.eclipse.edt.ide.deployment.operation.AbstractDeploymentOperation;
import org.eclipse.edt.ide.deployment.results.IDeploymentResultsCollector;
import org.eclipse.edt.ide.deployment.solution.DeploymentContext;
import org.eclipse.edt.ide.internal.sql.util.EGLSQLUtility;
import org.eclipse.edt.ide.sql.SQLNlsStrings;
import org.eclipse.edt.javart.resources.egldd.Binding;
import org.eclipse.edt.javart.resources.egldd.Parameter;
import org.eclipse.edt.javart.resources.egldd.SQLDatabaseBinding;

public class ConvertWorkspaceResourceOperation extends AbstractDeploymentOperation {
	
	public static final String SEPERATOR = ";";
	
	private String targetProjectName;
	private DeploymentDesc ddModel;
	private DeploymentContext context;
	
	public void execute(DeploymentContext context, IDeploymentResultsCollector resultsCollector, IProgressMonitor monitor)
			throws CoreException {
		this.context = context;
		ddModel = context.getDeploymentDesc();
		
			IConnectionProfile[] profiles = ProfileManager.getInstance().getProfiles();
			for ( Binding binding: ddModel.getBindings() ) {
				if ( binding instanceof SQLDatabaseBinding ) {
					SQLDatabaseBinding sqlBinding = (SQLDatabaseBinding)binding;
					if ( !sqlBinding.isUseURI() && sqlBinding.getUri() == null ) {
						continue;
					}
					IConnectionProfile profile = ProfileManager.getInstance().getProfileByName( sqlBinding.getUri().substring( 12 ) ); //Remove the "workspace://"
					if ( profile == null ) {
						continue;
					}
					sqlBinding.addParameter( new Parameter( SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_dbms, 
							EGLSQLUtility.getSQLVendorProperty(profile) + " "
									+ EGLSQLUtility.getSQLProductVersion(profile) ) );
					
					sqlBinding.addParameter( new Parameter( SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_sqlJDBCDriverClass, 
										EGLSQLUtility.getSQLJDBCDriverClassPreference(profile) ) );

					sqlBinding.addParameter( new Parameter( SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_jarList, 
							EGLSQLUtility.getLoadingPath(profile) ) );

					sqlBinding.addParameter( new Parameter( SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_sqlID, 
							EGLSQLUtility.getSQLUserId(profile) ) );

					sqlBinding.addParameter( new Parameter( SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_sqlPassword, 
							Encoder.encode( EGLSQLUtility.getSQLPassword(profile) ) ) );

					sqlBinding.addParameter( new Parameter( SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_sqlDB, 
							EGLSQLUtility.getSQLConnectionURLPreference(profile) ) );
					
					sqlBinding.addParameter( new Parameter( SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_sqlSchema, 
							EGLSQLUtility.getDefaultSchema(profile) ) );

					sqlBinding.setUseURI( false );

				}
			}

	}

}
