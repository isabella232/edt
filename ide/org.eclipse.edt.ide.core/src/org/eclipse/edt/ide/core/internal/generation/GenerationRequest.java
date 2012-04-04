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
package org.eclipse.edt.ide.core.internal.generation;


public class GenerationRequest {
	private IEGLPartWrapper[] genUnits;
	//General options
//	private String system = ""; //$NON-NLS-1$
//	private String destHost = ""; //$NON-NLS-1$
//	private String destPort = ""; //$NON-NLS-1$
//	private String destUserID = ""; //$NON-NLS-1$
//	private String destPassword = ""; //$NON-NLS-1$
//	private String genDirectory = ""; //$NON-NLS-1$
//	//SQL options
//	private String dbUserID = ""; //$NON-NLS-1$
//	private String dbPassword = ""; //$NON-NLS-1$
//	private String sqlDB = ""; //$NON-NLS-1$
//	private String sqlJNDIName = ""; //$NON-NLS-1$
//	//Java options
//	private String destDirectory = ""; //$NON-NLS-1$
//	private String genProject = ""; //$NON-NLS-1$
//	private String tempDirectory = ""; //$NON-NLS-1$
//	//COBOL options
//	private String templateDir = ""; //$NON-NLS-1$
//	private String reservedWord = ""; //$NON-NLS-1$
//	private String projectID = ""; //$NON-NLS-1$
//	private String destLibrary = ""; //$NON-NLS-1$
//	private HashMap symparms = new HashMap();
		
	public GenerationRequest() {
		super();
	}

	public GenerationRequest(IEGLPartWrapper[] units
//								,
//								String dbID,
//								String dbPass,
//								String destID,
//								String destPass,
//								String destHost,
//								String destPort
								)
	{
		genUnits = units;
//		dbUserID = dbID;
//		dbPassword = dbPass;
//		destUserID = destID;
//		destPassword = destPass;
//		this.destHost = destHost;
//		this.destPort = destPort;
	}
	
	public IEGLPartWrapper[] getGenerationUnits() {
		return genUnits;
	}

	public void setGenUnits(IEGLPartWrapper[] genUnits) {
		this.genUnits = genUnits;
	}
	
//	public String getDBPassword() {
//		return dbPassword;
//	}
//
//	public void setDBPassword(String dbPassword) {
//		this.dbPassword = dbPassword;
//	}
//
//	public String getDBUserID() {
//		return dbUserID;
//	}
//
//	public void setDBUserID(String dbUserID) {
//		this.dbUserID = dbUserID;
//	}
//
//	public String getDestDirectory() {
//		return destDirectory;
//	}
//
//	public void setDestDirectory(String destDirectory) {
//		this.destDirectory = destDirectory;
//	}
//
//	public String getDestHost() {
//		return destHost;
//	}
//
//	 
//	public HashMap getSymparms() {
//		return symparms;
//	}
//
//	public void setDestHost(String destHost) {
//		this.destHost = destHost;
//	}
//
//	public void setSymparms(HashMap symparms) {
//		this.symparms = symparms;
//	}
//
//	public String getDestLibrary() {
//		return destLibrary;
//	}
//
//	public void setDestLibrary(String destLibrary) {
//		this.destLibrary = destLibrary;
//	}
//
//	public String getDestPassword() {
//		return destPassword;
//	}
//
//	public void setDestPassword(String destPassword) {
//		this.destPassword = destPassword;
//	}
//
//	public String getDestPort() {
//		return destPort;
//	}
//
//	public void setDestPort(String destPort) {
//		this.destPort = destPort;
//	}
//
//	public String getDestUserID() {
//		return destUserID;
//	}
//
//	public void setDestUserID(String destUserID) {
//		this.destUserID = destUserID;
//	}
//
//	public String getGenDirectory() {
//		return genDirectory;
//	}
//
//	public void setGenDirectory(String genDirectory) {
//		this.genDirectory = genDirectory;
//	}
//
//	public String getGenProject() {
//		return genProject;
//	}
//
//	public void setGenProject(String genProject) {
//		this.genProject = genProject;
//	}
//
//	public String getProjectID() {
//		return projectID;
//	}
//
//	public void setProjectID(String projectID) {
//		this.projectID = projectID;
//	}
//
//	public String getReservedWord() {
//		return reservedWord;
//	}
//
//	public void setReservedWord(String reservedWord) {
//		this.reservedWord = reservedWord;
//	}
//
//	public String getSqlDB() {
//		return sqlDB;
//	}
//
//	public void setSqlDB(String sqlDB) {
//		this.sqlDB = sqlDB;
//	}
//
//	public String getSqlJNDIName() {
//		return sqlJNDIName;
//	}
//
//	public void setSqlJNDIName(String sqlJNDIName) {
//		this.sqlJNDIName = sqlJNDIName;
//	}
//
//	public String getSystem() {
//		return system;
//	}
//
//	public void setSystem(String system) {
//		this.system = system;
//	}
//
//	public String getTempDirectory() {
//		return tempDirectory;
//	}
//
//	public void setTempDirectory(String tempDirectory) {
//		this.tempDirectory = tempDirectory;
//	}
//
//	public String getTemplateDir() {
//		return templateDir;
//	}
//
//	public void setTemplateDir(String templateDir) {
//		this.templateDir = templateDir;
//	}
}
