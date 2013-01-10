/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.util;

import org.eclipse.datatools.connectivity.ui.dse.dialogs.ConnectionDisplayProperty;
import org.eclipse.edt.ide.sql.SQLConstants;
import org.eclipse.edt.ide.ui.wizards.BindingSQLDatabaseConfiguration;

public class UISQLUtility {
	public static void setBindingSQLDatabaseConfiguration(BindingSQLDatabaseConfiguration config, 
			ConnectionDisplayProperty[] properties) {
		if(properties != null && properties.length == SQLConstants.DATABASE_PROFILE_PROPERTY_LENGTH) {
			config.setDbms(properties[0].getValue());
			config.setDbName(properties[1].getValue());
			config.setDriverClass(properties[2].getValue());
			config.setConnLocation(properties[3].getValue());
			config.setConnUrl(properties[4].getValue());
			
			config.setUserName(properties[5].getValue());
			config.setPassword(properties[6].getValue());
			config.setBindingName(properties[8].getValue());
			config.setDefaultSchema(properties[9].getValue());
		}
	}
}
