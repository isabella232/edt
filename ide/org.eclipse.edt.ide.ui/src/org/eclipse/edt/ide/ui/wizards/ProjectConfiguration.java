/*******************************************************************************
 * Copyright © 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.compiler.internal.EGLBasePlugin;
import org.eclipse.edt.ide.core.model.PPListElement;
import org.eclipse.edt.ide.core.utils.EGLProjectInfoUtility;

public class ProjectConfiguration {
	
	/** The Project Name */
	private String projectName;
	
	/** Default Location */
	private boolean useDefaults;
	
	/** Initial Project Location */
	private String initialProjectLocation;
	
	/** Custom Project location */
	private String customProjectLocation;
	
	/**
	 * Possible values are:
	 * JAVA_PLATFORM = 1
	 * JAVASCRIPT_PLATFORM = 2
	 * JAVA_JAVASCRIPT_PLATFORMS = 3
	 * COBOL_PLATFORM = 4
	 * JAVA_COBOL_PLATFORMS = 5
	 * JAVASCRIPT_COBOL_PLATFORMS = 6
	 * JAVA_JAVASCRIPT_COBOL_PLATFORMS = 7
	 */
	private int targetRuntimeValue;
	
	/** Source Folder name */
	private String sourceFolderName;
	
	/** Required projects */
	private List <PPListElement>requiredProjects;
	
	private List<String> selectedWidgetLibraries;
	
	private boolean configureEGLPathOnly;
	
	public void setDefaultAttributes() {
		useDefaults = true;
		projectName = ""; //$NON-NLS-1$
		initialProjectLocation = Platform.getLocation().toOSString();
		customProjectLocation = ""; //$NON-NLS-1$
		targetRuntimeValue = EGLProjectInfoUtility.JAVA_PLATFORM;
		requiredProjects = new ArrayList<PPListElement>();
		selectedWidgetLibraries = new ArrayList<String>();
	}

	/**
	 * @return
	 */
	public String getCustomProjectLocation() {
		return customProjectLocation;
	}

	/**
	 * @return
	 */
	public String getInitialProjectLocation() {
		return initialProjectLocation;
	}

	/**
	 * @return
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @return
	 */
	public List <PPListElement> getRequiredProjects() {
		return requiredProjects;
	}

	/**
	 * @return
	 */
	public String getSourceFolderName() {
		return sourceFolderName;
	}

	/**
	 * @param string
	 */
	public void setCustomProjectLocation(String string) {
		customProjectLocation = string;
	}

	/**
	 * @param string
	 */
	public void setInitialProjectLocation(String string) {
		initialProjectLocation = string;
	}

	/**
	 * @param string
	 */
	public void setProjectName(String string) {
		projectName = string;
	}

	/**
	 * @param projects
	 */
	public void setRequiredProjects(List <PPListElement>projects) {
		requiredProjects = projects;
	}

	/**
	 * @param string
	 */
	public void setSourceFolderName(String string) {
		sourceFolderName = string;
	}

	/**
	 * @return
	 */
	public boolean isUseDefaults() {
		return useDefaults;
	}

	/**
	 * @param b
	 */
	public void setUseDefaults(boolean b) {
		useDefaults = b;
	}
	
	public void addTargetRuntime( int platformMask ) {
		targetRuntimeValue |= platformMask;
	}
	
	/**
	 * @return
	 */
	public int getTargetRuntimeValue() {
		return targetRuntimeValue;
	}

	/**
	 * @param newValue
	 */
	public void setTargetRuntimeValue( int newValue ) {
		targetRuntimeValue = newValue;
	}
	
	public void addCobolPlatform() {
		targetRuntimeValue |= EGLBasePlugin.TARGET_RUNTIME_COBOL_MASK;
	}
	
	public void addJavaPlatform() {
		targetRuntimeValue |= EGLBasePlugin.TARGET_RUNTIME_JAVA_MASK;
	}
	
	public void addJavaScriptPlatform() {
		targetRuntimeValue |= EGLBasePlugin.TARGET_RUNTIME_JAVASCRIPT_MASK;
	}
	
	public boolean isCobolPlatform() {
		return ((targetRuntimeValue & EGLBasePlugin.TARGET_RUNTIME_COBOL_MASK) != 0);
	}
		
	public boolean isJavaPlatform() {
		return ((targetRuntimeValue & EGLBasePlugin.TARGET_RUNTIME_JAVA_MASK) != 0);
	}
	
	public boolean isJavaScriptPlatform() {
		return ((targetRuntimeValue & EGLBasePlugin.TARGET_RUNTIME_JAVASCRIPT_MASK) != 0);
	}
	
	public List<String> getSelectedWidgetLibraries() {
		return selectedWidgetLibraries;
	}
	
	public void setSelectedWidgetLibraries( List<String> list ) {
		selectedWidgetLibraries = list;
	}
	
	public boolean configureEGLPathOnly() {
		return configureEGLPathOnly;
	}
	
	public void setConfigureEGLPathOnly( boolean flag ) {
		configureEGLPathOnly = flag;
	}
}
