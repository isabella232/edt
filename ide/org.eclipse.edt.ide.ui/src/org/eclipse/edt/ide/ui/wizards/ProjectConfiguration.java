/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
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
import org.eclipse.edt.ide.ui.project.templates.IProjectTemplate;

public class ProjectConfiguration {
	
	public final static int JAVA_PLATFORM = 1;
	public final static int JAVASCRIPT_PLATFORM = 2;
	public final static int JAVA_JAVASCRIPT_PLATFORMS = 3;
	public final static int COBOL_PLATFORM = 4;
	public final static int JAVA_COBOL_PLATFORMS = 5;
	public final static int JAVASCRIPT_COBOL_PLATFORMS = 6;
	public final static int JAVA_JAVASCRIPT_COBOL_PLATFORMS = 7;	

	public static final String EDT_COMPILER_ID = "org.eclipse.edt.ide.compiler.edtCompiler";
	public static final String JAVA_GENERATOR_ID = "org.eclipse.edt.ide.gen.JavaGenProvider";
	public static final String JAVACORE_GENERATOR_ID = "org.eclipse.edt.ide.gen.JavaCoreGenProvider";
	public static final String JAVASCRIPT_DEV_GENERATOR_ID = "org.eclipse.edt.ide.gen.JavaScriptDevGenProvider";
	public static final String JAVASCRIPT_GENERATOR_ID = "org.eclipse.edt.ide.gen.JavaScriptGenProvider";
	
	/** The Project Name */
	private String projectName;
	
	/** Default Location */
	private boolean useDefaults;
	
	/** Initial Project Location */
	private String initialProjectLocation;
	
	/** Custom Project location */
	private String customProjectLocation;
	
	/** Custome base package name, optional */
	private String basePackageName;
	
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
	
	/** Selected widget libraries, only use for web client project **/
	private List<String> selectedWidgetLibraries;
	
	/** Selected compiler id **/
	private String selectedCompiler;	

	/** Selected generators **/
	private String[] selectedGenerators;	

	private boolean configureEGLPathOnly;
	
	private IProjectTemplate selectedProjectTemplate;
	
	public void setDefaultAttributes() {
		useDefaults = true;
		projectName = ""; //$NON-NLS-1$
		initialProjectLocation = Platform.getLocation().toOSString();
		customProjectLocation = ""; //$NON-NLS-1$
		basePackageName = "";
		targetRuntimeValue = JAVA_PLATFORM;
		requiredProjects = new ArrayList<PPListElement>();
		selectedWidgetLibraries = new ArrayList<String>();
		selectedCompiler = EDT_COMPILER_ID;
		selectedGenerators = new String[]{JAVA_GENERATOR_ID, JAVASCRIPT_GENERATOR_ID, JAVASCRIPT_DEV_GENERATOR_ID};
	}
	
	public void setEmptyCompilerProperty(){
		selectedGenerators = new String[]{};
		selectedCompiler = null;
	}
	public String getSelectedCompiler() {
		return selectedCompiler;
	}

	public void setSelectedCompiler(String selectedCompiler) {
		this.selectedCompiler = selectedCompiler;
	}
	
	public String[] getSelectedGenerators() {
		return selectedGenerators;
	}

	public void setSelectedGenerators(String[] selectedGenerators) {
		boolean hasDevJS = false;
		boolean hasJS = false;
		for(String gen : selectedGenerators){
			if(gen.equals(JAVASCRIPT_GENERATOR_ID)){
				hasJS = true;
			}else if(gen.equals(JAVASCRIPT_DEV_GENERATOR_ID)){
				hasDevJS = true;
			}
		}
		if(hasJS && !hasDevJS){
			this.selectedGenerators = new String[selectedGenerators.length + 1];
			for(int i=0;i<selectedGenerators.length;i++){
				this.selectedGenerators[i] = selectedGenerators[i];
			}
			this.selectedGenerators[selectedGenerators.length] = JAVASCRIPT_DEV_GENERATOR_ID;
		}else{
			this.selectedGenerators = selectedGenerators;
		}		
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

	public String getBasePackageName() {
		return basePackageName;
	}

	public void setBasePackageName(String basePackageName) {
		this.basePackageName = basePackageName;
	}

	public IProjectTemplate getSelectedProjectTemplate() {
		return selectedProjectTemplate;
	}

	public void setSelectedProjectTemplate(IProjectTemplate selectedProjectTemplate) {
		this.selectedProjectTemplate = selectedProjectTemplate;
	}
}
