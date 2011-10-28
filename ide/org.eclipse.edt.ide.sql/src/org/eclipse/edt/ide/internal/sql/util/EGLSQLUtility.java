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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.datatools.connectivity.ConnectionProfileConstants;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.drivers.DriverInstance;
import org.eclipse.datatools.connectivity.drivers.DriverManager;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCDriverDefinitionConstants;
import org.eclipse.datatools.connectivity.internal.ui.wizards.CPWizardNode;
import org.eclipse.datatools.connectivity.internal.ui.wizards.ProfileWizardProvider;
import org.eclipse.datatools.connectivity.ui.actions.AddProfileViewAction;
import org.eclipse.datatools.connectivity.ui.dse.dialogs.ConnectionDisplayProperty;
import org.eclipse.datatools.modelbase.sql.schema.Database;
import org.eclipse.datatools.modelbase.sql.schema.Schema;
import org.eclipse.datatools.modelbase.sql.schema.helper.DatabaseHelper;
import org.eclipse.datatools.modelbase.sql.schema.helper.SchemaHelper;
import org.eclipse.datatools.modelbase.sql.tables.Table;
import org.eclipse.edt.compiler.internal.sql.util.SQLUtility;
import org.eclipse.edt.compiler.internal.util.Encoder;
import org.eclipse.edt.ide.sql.SQLConstants;
import org.eclipse.edt.ide.sql.SQLNlsStrings;
import org.eclipse.edt.ide.sql.SQLPlugin;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class EGLSQLUtility {
	public static final IEGLEDTSQLUtil eglEDTSQLUtil = createEDTSQLUtil();
	private static final String EDT_SQL_UTIL_EXTENSION_ID = "eglEDTSQLUtil";
	
	
	/*
	 * These are the ids for all of the supported EGL database connection types. They are a 
	 * subset of those supported by Eclipse/RAD datatools. The source of these strings is 
	 * the plugin.xml file for the plugin com.ibm.datatools.connection.ui. They can be found 
	 * as values for the connectionProfileID attribute of the driverDefinitionMapping property 
	 * to the com.ibm.datatools.connection.ui.driverMapping extension point.
	 */
	public static Map<String,String> profileIds = new HashMap<String,String>(20);
	
	static {
	    //For DB2
		profileIds.put(SQLConstants.DB2UDB_NAME_EXPANSION.toLowerCase(), "org.eclipse.datatools.enablement.ibm.db2.luw.connectionProfile");
		profileIds.put(SQLConstants.DB2ZOS_NAME.toLowerCase(), "org.eclipse.datatools.enablement.ibm.db2.zseries.connectionProfile");
		profileIds.put(SQLConstants.DB2IOS_NAME.toLowerCase(), "org.eclipse.datatools.enablement.ibm.db2.iseries.connectionProfile");
		
		//For Derby
		profileIds.put(SQLConstants.DERBY_NAME.toLowerCase(),"org.eclipse.datatools.connectivity.db.derby.embedded.connectionProfile");
		profileIds.put(SQLConstants.EMBEDDED_DERBY_NAME.toLowerCase(),"org.eclipse.datatools.connectivity.db.derby.embedded.connectionProfile");
		
		//For Generic JDBC
		profileIds.put(SQLConstants.GENERIC_JDBC_NAME.toLowerCase(), "org.eclipse.datatools.connectivity.db.generic.connectionProfile");
		
		//For HSQLDB
		profileIds.put(SQLConstants.HSQLDB_NAME.toLowerCase(), "org.eclipse.datatools.enablement.hsqldb.connectionProfile");
		
		//For Ingres
		profileIds.put(SQLConstants.INGRES_NAME.toLowerCase(), "org.eclipse.datatools.enablement.ingres.connectionProfile");
		
		//For Informix
		profileIds.put(SQLConstants.INFORMIX_NAME.toLowerCase(), "org.eclipse.datatools.enablement.ibm.informix.connectionProfile");
		
		//For MaxDB
		profileIds.put(SQLConstants.MAXDB_NAME.toLowerCase(), "org.eclipse.datatools.enablement.sap.maxdb.connectionProfile");
		
		//For MySQL
		profileIds.put(SQLConstants.MYSQL_NAME.toLowerCase(), "org.eclipse.datatools.enablement.mysql.connectionProfile");
		
		//For Oracle
		profileIds.put(SQLConstants.ORACLE_NAME.toLowerCase(), "org.eclipse.datatools.enablement.oracle.connectionProfile");
		
		//For PostgreSQL
		profileIds.put(SQLConstants.POSTGRESQL_NAME.toLowerCase(), "org.eclipse.datatools.enablement.postgresql.connectionProfile");
		
		//For SQL Server
		profileIds.put(SQLConstants.MSSQLSERVER_NAME.toLowerCase(), "org.eclipse.datatools.enablement.msft.sqlserver.connectionProfile");
		
		//For SQLite
		profileIds.put(SQLConstants.SQLITE_NAME.toLowerCase(), "org.eclipse.datatools.enablement.sqlite.connectionProfile");
		
		//For Sybase
		profileIds.put(SQLConstants.SYBASE_ASA_NAME.toLowerCase(), "org.eclipse.datatools.enablement.sybase.asa.connectionProfile");
		profileIds.put(SQLConstants.SYBASE_ASE_NAME.toLowerCase(), "com.sybase.stf.servers.jdbc.ase2.embedded.connectionProfile");
	}
	
	public static boolean isEqualIdentifiers(String id1, String id2, String vendorType) {

		switch (Integer.parseInt(vendorType)) {
			case SQLConstants.MSSQLSERVER:
				//Schema object identifiers are case insensitive for MS SQL Server 
				return getIdentifierString(id1, vendorType).equalsIgnoreCase(getIdentifierString(id2, vendorType));
			default:
				return getIdentifierString(id1, vendorType).equals(getIdentifierString(id2, vendorType));
		}
	}

	/*
	 * Generate a vendor specific rendered string identifiers for the given name
	 */
	public static String generateIdentifier(String name, String vendorType) {
		// do not change a delimited name
		if (isDelimitedIdentifier(name))
			return name;

		switch (Integer.parseInt(vendorType)) {
			case SQLConstants.DB2UDB:				
			case SQLConstants.DB2UDBAS400:	
			case SQLConstants.DB2UDBOS390:
			case SQLConstants.ORACLE:
			case SQLConstants.DERBY:
			case SQLConstants.GENERIC:
				return name.toUpperCase();
			case SQLConstants.INFORMIX:
				return name.toLowerCase();
			default:
				return name;
		}
	}

	public static String[] tokenizeName(String name) {

		char[] nameChars = name.toCharArray();

		char[][] tokens = new char[][] { new char[nameChars.length],
				new char[nameChars.length], new char[nameChars.length] };
		boolean delimiter = false;
		int token = 0;
		int index = 0;
		for (int i = 0; i < nameChars.length; i++) {
			if (nameChars[i] == '"') {
				// Open/close delimiter
				delimiter = !delimiter;
				tokens[token][index++] = nameChars[i];
				continue;
			}
			if (nameChars[i] == '.' && delimiter == false) {
				// Name is qualified, increment token
				++token;
				index = 0;
				// Safety check for invalid names, ie more than 3 part names
				if (token > 2)
					break;
				continue;
			}
			// Save character
			tokens[token][index++] = nameChars[i];
		}

		String[] result = new String[3];
		// one part name means table name
		// two part name means schema qualified
		// three part name means database qualified
		result[2] = new String(tokens[token]).trim();// table
		result[1] = (token > 0 ? new String(tokens[token - 1]).trim() : null);
		result[0] = (token > 1 ? new String(tokens[token - 2]).trim() : null);

		return result;
	}

	public static boolean isDelimitedIdentifier(String id) {
		String del = "\"";
		return id.startsWith(del) && id.endsWith(del) && id.length() > 1;
	}

	public static Table findTable(Database database, String tableName) {

		String[] nameTokens = tokenizeName(tableName);
		String schName = nameTokens[1];
		String tblName = nameTokens[2];

		// if schema name is not null, ensure that schema exists in this
		// database, and find table
		if (schName != null) {
			Schema sch = findSchema(database, schName);
			if (sch != null)
//				return findAbstractTable(sch, tblName, database.getVendor());
				
				return (Table)SchemaHelper.findTable(sch, tblName);
		}

		return null;
	}

	public static Schema findSchema(Database database, String schemaName) {
		return DatabaseHelper.findSchema(database, schemaName);
	}
	
	 private static String getIdentifierString(String name, String vendorType) {
		 if(isDelimitedIdentifier(name)) {
			 return name.substring(1, name.length() - 1);
		 }
		 return generateIdentifier(name, vendorType);
     }
	 
	 public static IConnectionProfile getCurrentConnectionProfile() {
		 	String conName = SQLPlugin.getPlugin().getNamedConnection();
		 	return getConnectionProfile(conName);
	 }
	 
	 public static IConnectionProfile getConnectionProfile(String connectionName) {
		 	IConnectionProfile profile = null;
		 	if (connectionName != null && !connectionName.equals("")) {
				profile = ProfileManager.getInstance().getProfileByName(connectionName);
		 	}
		 	return profile;
	 }
	 
	public static List<String> getExistingConnectionNamesList() {
		IConnectionProfile[] profiles = getProfilesToDisplay();
		List<String> nameList = new ArrayList<String>(profiles.length);
		for (int i = 0, n = profiles.length; i < n; i++) {
			if (!nameList.contains(profiles[i].getName())) {
				nameList.add(profiles[i].getName());
			}
		}
		return nameList;
	}
	
	public static IConnectionProfile[] getProfilesToDisplay() {
		IConnectionProfile[] profiles = ProfileManager.getInstance().getProfiles();
		List<IConnectionProfile> filteredProfiles = new ArrayList<IConnectionProfile>();	
		int totalProfiles = profiles.length;
		for (int profileCount = 0 ; profileCount < totalProfiles ; profileCount++) {
			IConnectionProfile profile = profiles[profileCount];
			Map factories =  profiles[profileCount].getProvider().getConnectionFactories();
			if ((factories == null) || (!factories.containsKey("java.sql.Connection"))) {
				continue;
			}
			String name = profile.getBaseProperties().getProperty(IJDBCDriverDefinitionConstants.DATABASE_VENDOR_PROP_ID);
			String version = profile.getBaseProperties().getProperty(IJDBCDriverDefinitionConstants.DATABASE_VERSION_PROP_ID);
			if (!EGLConnectibleDatabases.isSupportedProduct(name))
				continue;
			if (name.equals(SQLConstants.DB2UDB_NAME) && version.equals(SQLConstants.DB2UDB_V_7_2))
				continue;
			if (name.equals(SQLConstants.GENERIC_JDBC_NAME) && !SQLUtility.isTerraDataSupported()) {
				continue;
			}
			filteredProfiles.add(profile);
		}
		return filteredProfiles.toArray(new IConnectionProfile[filteredProfiles.size()]);
	}
	 
	 public static String getSQLDatabaseVendorPreference(IConnectionProfile profile) {
		 String name = "";
		 if (profile != null) {
			 name = (String)profile.getBaseProperties().get(IJDBCDriverDefinitionConstants.DATABASE_VENDOR_PROP_ID);
		 }
		 return name;
	 }
	 
	 public static String getSQLJDBCDriverClassPreference(IConnectionProfile profile) {
		 String name = "";
		 if (profile != null) {
			 name = profile.getBaseProperties().getProperty(IJDBCDriverDefinitionConstants.DRIVER_CLASS_PROP_ID);
		 }
		 return name;
	 }
	 
	 public static String getSQLDatabasePreference(IConnectionProfile profile) {
		 String name = "";
		 if (profile != null) {
			 name = profile.getBaseProperties().getProperty(IJDBCDriverDefinitionConstants.DATABASE_NAME_PROP_ID);
		 }
		 return name;
	 }
	 
	 public static String getSQLConnectionURLPreference(IConnectionProfile profile) {
		 String name = "";
		 if (profile != null) {
			 name = profile.getBaseProperties().getProperty(IJDBCDriverDefinitionConstants.URL_PROP_ID);
		 }
		 return name;
	 }
	 
	 public static String getSQLJDBCLoadingPath() {
		 String path = "";
		 IConnectionProfile profile = getCurrentConnectionProfile();
		 if (profile != null) {
			 path = EGLSQLUtility.getLoadingPath(profile);
		 }
		 return path;
	 }
	 
	 public static String getSQLUserId(IConnectionProfile profile) {
		 return profile.getBaseProperties().getProperty(IJDBCDriverDefinitionConstants.USERNAME_PROP_ID);
	 }
	 
	 public static String getSQLPassword(IConnectionProfile profile) {
		 return profile.getBaseProperties().getProperty(IJDBCDriverDefinitionConstants.PASSWORD_PROP_ID);
	 }
	 
	 public static String getSQLVendorProperty(IConnectionProfile profile) {
		 return profile.getBaseProperties().getProperty(IJDBCDriverDefinitionConstants.DATABASE_VENDOR_PROP_ID);
	 }
	 
	 public static String getSQLProductVersion(IConnectionProfile profile) {
		 return profile.getBaseProperties().getProperty(IJDBCDriverDefinitionConstants.DATABASE_VERSION_PROP_ID);
	 }
	 
	 public static String getDBProfileProviderName(IConnectionProfile profile) {
		 return profile.getProviderName();
	 }
	 
	 public static String getDBNameProperty(IConnectionProfile profile) {
		 return profile.getBaseProperties().getProperty(IJDBCDriverDefinitionConstants.DATABASE_NAME_PROP_ID);
	 }
	 
	 public static String getDefaultSchema(IConnectionProfile profile) {
		 return profile.getBaseProperties().getProperty(SQLConstants.DATABASE_DEFAULT_SCHEMA_ID);
	 }
	 
	 public static String getDecodedConnectionPassword(IConnectionProfile connection) {
		 String password = "";
		 if (connection != null) {
			 Properties props = connection.getProperties(SQLConstants.EGL_CUSTOM_PROPERTIES);
			 password = props.getProperty(SQLConstants.EGL_DB_PASSWORD_CUSTOM_PROPERTY, "");
			 if (password.length() > 0 && Encoder.isEncoded(password))
				 password = Encoder.decode(password);
		 }
		 return password;
	 }
	 
	 public static void shutdownConnection(IConnectionProfile profile) {
		String url = EGLSQLUtility.getSQLConnectionURLPreference(profile);
		if (url != null && url.startsWith("jdbc:derby:")) {
			profile.disconnect( null );
		}
	}
	 
	public static String getLoadingPath(IConnectionProfile connectionProfile) {
		String jarList = null;
		String driverID = connectionProfile.getBaseProperties().getProperty(ConnectionProfileConstants.PROP_DRIVER_DEFINITION_ID);
		if (driverID != null) {
		    DriverInstance driverInstance = DriverManager.getInstance().getDriverInstanceByID(driverID);
		    if (driverInstance != null) {
		        jarList = driverInstance.getJarList();
		    }
		}
		return jarList;
	}
	
	public static String getSecondaryID(IConnectionProfile profile) {
		String id = "";
		Properties props = profile.getProperties(SQLConstants.EGL_CUSTOM_PROPERTIES);
		if (props != null) {
			id = props.getProperty(SQLConstants.EGL_SECONDARY_ID_CUSTOM_PROPERTY, "");
		}
		return id;
	}
	
	public static IConnectionProfile createNewProfile() {
		// The RBD wizard is a little better - use it if it's available.
		if ( eglEDTSQLUtil != null ) {
			return eglEDTSQLUtil.createNewProfile();
		}
		
		AddProfileViewAction action = new AddProfileViewAction() {
			protected ViewerFilter[] getWizardSelectionFilters() {
				return new ViewerFilter[] {
					new ViewerFilter() {
						public boolean select(Viewer viewer, Object parentElement, Object element) {
							CPWizardNode wizardNode = (CPWizardNode)element;
							if (wizardNode.getProvider() instanceof ProfileWizardProvider) {
								String profile = ((ProfileWizardProvider)wizardNode.getProvider()).getProfile();
								if(profileIds.values().contains(profile)) {
									return true;
								}
							}
							return false;
						}
					}
				};
			}
		};
		action.run();
		return action.getAddedProfile();
	}
	
	public static ConnectionDisplayProperty[] getConnectionDisplayProperties(IConnectionProfile profile) {
		ConnectionDisplayProperty[] properties = new ConnectionDisplayProperty[SQLConstants.DATABASE_PROFILE_PROPERTY_LENGTH];

		properties[0] = new ConnectionDisplayProperty(
				SQLNlsStrings.SQL_CONNECTION_DATABASE_PROPERTY,EGLSQLUtility.getDBProfileProviderName(profile));
				//EGLSQLUtility.getSQLVendorProperty(profile) + " " + EGLSQLUtility.getSQLProductVersion(profile));
		properties[1] = new ConnectionDisplayProperty(
				SQLNlsStrings.SQL_CONNECTION_DBNAME_PROPERTY,
				EGLSQLUtility.getDBNameProperty(profile));
		properties[2] = new ConnectionDisplayProperty(
				SQLNlsStrings.SQL_CONNECTION_JDBC_PROPERTY, EGLSQLUtility
						.getSQLJDBCDriverClassPreference(profile));
		properties[3] = new ConnectionDisplayProperty(
				SQLNlsStrings.SQL_CONNECTION_LOCATION_PROPERTY,
				EGLSQLUtility.getLoadingPath(profile));
		properties[4] = new ConnectionDisplayProperty(
				SQLNlsStrings.SQL_CONNECTION_URL_PROPERTY, EGLSQLUtility
						.getSQLConnectionURLPreference(profile));
		properties[5] = new ConnectionDisplayProperty(
				SQLNlsStrings.SQL_CONNECTION_USER_ID_PROPERTY, EGLSQLUtility
						.getSQLUserId(profile));
		properties[6] = new ConnectionDisplayProperty(
				SQLNlsStrings.SQL_CONNECTION_USER_PASSWORD_PROPERTY, EGLSQLUtility
						.getSQLPassword(profile));
		properties[7] = new ConnectionDisplayProperty(
				SQLNlsStrings.SQL_CONNECTION_JNDI_PROPERTY,"jdbc/" + EGLSQLUtility.getDBNameProperty(profile));
		properties[8] = new ConnectionDisplayProperty(
				SQLNlsStrings.SQL_BINDING_NAME_PROPERTY,profile.getName());
		properties[9] = new ConnectionDisplayProperty(
				SQLNlsStrings.SQL_CONNECTION_DEFAULT_SCHEMA_PROPERTY,
				EGLSQLUtility.getDefaultSchema(profile));

		return properties;
	}
	
	private static IEGLEDTSQLUtil createEDTSQLUtil() {
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(SQLPlugin.PLUGIN_ID, EDT_SQL_UTIL_EXTENSION_ID);
		if ( extensionPoint != null ) {
			IExtension[] extensions = extensionPoint.getExtensions();
			for ( int i = 0; i < extensions.length; i++ ) {
				IExtension extension = extensions[ i ];
				IConfigurationElement[] elements = extension.getConfigurationElements();
				for ( int j = 0; j < elements.length; j++ ) {
					IConfigurationElement element = elements[j];
					try {
						// Go with the first one.
						return (IEGLEDTSQLUtil)element.createExecutableExtension("class"); //$NON-NLS-1$
					} catch ( Exception e ) {
						e.printStackTrace();
					}
				}
			}
    	}
		return null;
	}
	
	public static String getConnectionProviderProfile(String profileName) {
		if(profileName == null) {
			return null;
		} else {
			return profileIds.get(profileName.toLowerCase());
		}
		
	}
}
