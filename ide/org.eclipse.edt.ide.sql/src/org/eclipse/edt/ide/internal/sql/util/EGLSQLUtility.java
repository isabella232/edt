/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
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
import java.util.List;
import java.util.Properties;

import org.eclipse.datatools.connectivity.ConnectionProfileConstants;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.IConnectionProfileProvider;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.drivers.DriverInstance;
import org.eclipse.datatools.connectivity.drivers.DriverManager;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCDriverDefinitionConstants;
import org.eclipse.datatools.connectivity.internal.ConnectionProfileManager;
import org.eclipse.datatools.connectivity.ui.actions.AddProfileViewAction;
import org.eclipse.datatools.connectivity.ui.dse.dialogs.ConnectionDisplayProperty;
import org.eclipse.datatools.modelbase.sql.schema.Database;
import org.eclipse.datatools.modelbase.sql.schema.Schema;
import org.eclipse.datatools.modelbase.sql.schema.helper.DatabaseHelper;
import org.eclipse.datatools.modelbase.sql.schema.helper.SchemaHelper;
import org.eclipse.datatools.modelbase.sql.tables.Table;
import org.eclipse.edt.compiler.internal.util.Encoder;
import org.eclipse.edt.ide.sql.SQLConstants;
import org.eclipse.edt.ide.sql.SQLNlsStrings;
import org.eclipse.edt.ide.sql.SQLPlugin;

public class EGLSQLUtility {
	
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
	
	public static IConnectionProfile getCurrentConnectionProfile() {
		String conName = SQLPlugin.getPlugin().getNamedConnection();
		return getConnectionProfile(conName);
	}
	 
	public static IConnectionProfile getConnectionProfile(String connectionName) {
		IConnectionProfile profile = null;
		if (connectionName != null && connectionName.length() > 0) {
			profile = ProfileManager.getInstance().getProfileByName(connectionName);
		}
		return profile;
	}
	 
	public static List<String> getExistingConnectionNamesList() {
		IConnectionProfile[] profiles = ProfileManager.getInstance().getProfiles();
		List<String> nameList = new ArrayList<String>(profiles.length);
		for (int i = 0, n = profiles.length; i < n; i++) {
			if (!nameList.contains(profiles[i].getName())) {
				nameList.add(profiles[i].getName());
			}
		}
		return nameList;
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
		AddProfileViewAction action = new AddProfileViewAction();
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
	
	public static String getConnectionProviderProfile(String vendorName) {
		for (Object next : ConnectionProfileManager.getInstance().getProviders().values()) {
			IConnectionProfileProvider provider = (IConnectionProfileProvider)next;
			if (provider != null && vendorName.equals(provider.getName())) {
				return provider.getId();
			}
		}
		
		return null;
	}
}
