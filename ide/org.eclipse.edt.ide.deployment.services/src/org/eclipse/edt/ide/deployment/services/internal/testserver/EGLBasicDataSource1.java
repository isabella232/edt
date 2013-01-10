/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.services.internal.testserver;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

/**
 * Version that subclasses BasicDataSource from Apache Tomcat.
 */
public class EGLBasicDataSource1 extends BasicDataSource
{
	private SimpleDataSource simpleDataSource;
	
	@Override
	public Connection getConnection(String userName, String password) throws SQLException {
		if (simpleDataSource == null) {
			simpleDataSource = new SimpleDataSource();
			simpleDataSource.setUrl(getUrl());
			simpleDataSource.setDriverClassName(getDriverClassName());
		}
		return simpleDataSource.getConnection(userName, password);
	}
}
