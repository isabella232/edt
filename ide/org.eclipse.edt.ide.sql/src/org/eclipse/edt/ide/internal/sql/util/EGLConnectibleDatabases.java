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
package org.eclipse.edt.ide.internal.sql.util;

import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.datatools.connectivity.sqm.core.definition.DatabaseDefinitionRegistry;
import org.eclipse.datatools.connectivity.sqm.internal.core.RDBCorePlugin;
import org.eclipse.edt.compiler.internal.sql.util.SQLUtility;
import org.eclipse.edt.ide.sql.SQLConstants;

public class EGLConnectibleDatabases {
	private static EGLConnectibleDatabases INSTANCE = new EGLConnectibleDatabases();

	private static final HashSet<DatabaseProductVersion> filteredDatabases = new HashSet<DatabaseProductVersion>();
	private static final HashSet<String> validVendors = new HashSet<String>(8);
	static {
		validVendors.add(SQLConstants.DB2UDB_NAME);
		validVendors.add(SQLConstants.DB2UDBAS400_NAME);
		validVendors.add(SQLConstants.DB2UDBOS390_NAME);
		validVendors.add(SQLConstants.INFORMIX_NAME);
		validVendors.add(SQLConstants.ORACLE_NAME);
		validVendors.add(SQLConstants.MSSQLSERVER_NAME);
		validVendors.add(SQLConstants.DERBY_NAME);
		validVendors.add(SQLConstants.GENERIC_JDBC_NAME);
		validVendors.add(SQLConstants.DB2UDBVSE_NAME);
		validVendors.add(SQLConstants.DB2_ALIAS_NAME);
		
		DatabaseDefinitionRegistry dbDefReg = RDBCorePlugin.getDefault()
				.getDatabaseDefinitionRegistry();
		for (Iterator productIterator = dbDefReg.getConnectibleProducts(); productIterator
				.hasNext();) {
			String productName = (String) productIterator.next();
			if (!validVendors.contains(productName)) {
				continue;
			}
			
			if (productName.equals(SQLConstants.GENERIC_JDBC_NAME) && !SQLUtility.isTerraDataSupported()) {
				continue;
			}
			
			for (Iterator versionIterator = dbDefReg
					.getConnectibleVersions(productName); versionIterator
					.hasNext();) {
				String version = (String) versionIterator.next();
				if (productName.equals(SQLConstants.DB2UDB_NAME)
						&& version.equals(SQLConstants.DB2UDB_V_7_2)) {
					continue;
				}
				filteredDatabases.add(new DatabaseProductVersion(productName, version));
			}
		}
	}

	public static EGLConnectibleDatabases getInstance() {
		return INSTANCE;
	}

	public DatabaseProductVersion[] getEGLDatabases() {
		return filteredDatabases
				.toArray(new DatabaseProductVersion[filteredDatabases.size()]);
	}
	
	public static boolean isSupportedProduct(String vendor) {
		return validVendors.contains(vendor);
	}
}
