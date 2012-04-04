/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.edt.compiler.internal.util.Encoder;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.State;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.activities.IActivity;
import org.eclipse.ui.activities.IActivityManager;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;



/**
 * Insert the type's description here.
 * Creation date: (1/27/2002 6:27:36 PM)
 */
public class EGLBasePlugin extends AbstractUIPlugin {

	/**
	 * The following are constants defined for the key elements of the 
	 * Package Version extension point defined in this plugin's plugin.xml file
	 */
	public static final String EGL_BASE_PLUGIN_ID = "org.eclipse.edt.compiler"; //$NON-NLS-1$

	public static final String EGL_AST_PLUGIN_ID = "com.ibm.etools.egl.java.ast"; //$NON-NLS-1$

	public static final String EGL_BUILD_PARTS_DTD_KEY_51 = "-//IBM//DTD EGL 5.1//EN"; //$NON-NLS-1$
	public static final String EGL_BUILD_PARTS_DTD_KEY = "-//IBM Corporation, Inc.//DTD EGL Build Parts 6.0//EN"; //$NON-NLS-1$
	
	public static final String EGL_BASE_CONTRIBUTION_ID = "com.ibm.etools.egl.buildparts.model.eglcontributions"; //$NON-NLS-1$
		
	public static final String EGL_PACKAGE_VERSION_EXTENSION_POINT_ID = "org.eclipse.edt.compiler.internal.packages"; //$NON-NLS-1$
	public static final String EGL_PACKAGE_VERSION_ELEMENT_ID = "packageVersion"; //$NON-NLS-1$
	public static final String EGL_PACKAGE_VERSION_ATTRIBUTE_ID = "versionId"; //$NON-NLS-1$
	public static final String EGL_BASE_PRODUCT_ID = "baseproduct"; //$NON-NLS-1$

	public static final String EGL_CORE_ACTIVITY = "com.ibm.etools.egl.core.activity"; //$NON-NLS-1$
	public static final String EGL_JSF_ACTIVITY = "com.ibm.etools.egl.jsf.activity"; //$NON-NLS-1$
	public static final String EGL_DLI_ACTIVITY = "com.ibm.etools.egl.dli.activity"; //$NON-NLS-1$
	public static final String EGL_RCP_ACTIVITY = "com.ibm.etools.egl.rcp.activity"; //$NON-NLS-1$
	public static final String EGL_CUI_ACTIVITY = "com.ibm.etools.egl.cui.activity"; //$NON-NLS-1$
	public static final String EGL_REPORTS_ACTIVITY = "com.ibm.etools.egl.reports.activity"; //$NON-NLS-1$
	public static final String EGL_TUI_ACTIVITY = "com.ibm.etools.egl.tui.activity"; //$NON-NLS-1$
	public static final String EGL_RUI_ACTIVITY = "com.ibm.etools.egl.rui.activity"; //$NON-NLS-1$
	public static final String EGL_VGUI_ACTIVITY = "com.ibm.etools.egl.vgui.activity"; //$NON-NLS-1$
	public static final String EGL_BIRT_ACTIVITY = "com.ibm.etools.egl.birt.activity"; //$NON-NLS-1$
	public static final String EGL_MQ_ACTIVITY = "com.ibm.etools.egl.mq.activity"; //$NON-NLS-1$
	public static final String EGL_FILEIO_ACTIVITY = "com.ibm.etools.egl.fileio.activity"; //$NON-NLS-1$
	
	public static final String PORTAL_DEVELOPMENT_ACTIVITY = "com.ibm.etools.portaledit.development"; //$NON-NLS-1$
	
//	public static final String EGL_HELP_ID_PREFIX_WSED = "com.ibm.etools.zos.egl.cshelp"; //$NON-NLS-1$
//	public static final String EGL_HELP_ID_PREFIX_WDSC = "com.ibm.etools.iseries.egl.cshelp"; //$NON-NLS-1$
	public static final String EGL_HELP_ID_PREFIX_WSAD = "com.ibm.etools.egl.cshelp"; //$NON-NLS-1$

	public static final String VAGCOMPATIBILITY_OPTION = "VAGCompatibilityOption"; //$NON-NLS-1$
    public static final String ALIAS_JSF_HANDLER_NAMES = "aliasJSFHandlerNames"; //$NON-NLS-1$
	public static final String RUIGENERATIONMODE_OPTION = "ruiGenerationMode"; //$NON-NLS-1$
	//	public static final String FULL_BUILD_OPTION = "FullBuildOption"; //$NON-NLS-1$
	public static final String DESTINATION_USERID = "DestinationUserID"; //$NON-NLS-1$
	public static final String DESTINATION_PASSWORD = "DestinationPassword"; //$NON-NLS-1$
	public static final String OUTPUT_CODESET = "outputCodeset";//$NON-NLS-1$
	public static final String INCOMPLETE_BUILD_PATH = "incompleteBuildPath";//$NON-NLS-1$
	
	public static final String BIDI_ENABLED_OPTION = "BidiEnabledOption"; //$NON-NLS-1$
	public static final String BIDI_VISUAL_SOURCEASSISTANT_OPTION = "BidiVisualSourceAssistantOption"; //$NON-NLS-1$
	public static final String BIDI_RTL_SOURCEASSISTANT_OPTION = "BidiRTLSourceAssistantOption"; //$NON-NLS-1$
	public static final String BIDI_VISUAL_EDITING_OPTION = "BidiVisualEditingOption"; //$NON-NLS-1$
	
	public static final String EGLFEATURE_MASK = "EGLFeatureMask";//$NON-NLS-1$
	public static final int EGLFEATURE_SOACLIENT_MASK = 1 << 1;
	public static final int EGLFEATURE_JASPER_MASK = 1 << 2;
	public static final int EGLFEATURE_BIRT_MASK = 1 << 3;
	public static final int EGLFEATURE_EGLDD = 1 << 4;
	public static final int EGLFEATURE_MQ = 1 << 5;
	public static final int EGLFAETURE_LDAP = 1 << 6;
	public static final int EGLFEATURE_iSeries = 1 << 7;
	
	public static final String EGL_Default_Web_Runtime = "MigrationDefaultWebRuntime"; //$NON-NLS-1$
	public static final String EGL_Project_Type = "EGLProjectType"; //$NON-NLS-1$
	
	public static final String EGLWEBFEATURE_WJSF = "EGLWebWithJSF";//$NON-NLS-1$
	public static final String EGLWEBFEATURE_WEBTRANS = "EGLWebWithWebTrans";//$NON-NLS-1$
	public static final String EGLWEBFEATURE_JAVAOBJINTERFACE = "EGLWebWithJavaObjInterface";  //$NON-NLS-1$
	public static final QualifiedName VALUESTOREKEY_EGLFEATURE= new QualifiedName(null,"EGLProjectFeatureMask"); //$NON-NLS-1$
	
	public static final String EGLPROJECTMIGRATIONVERSION75 = "7.5.0";
	public static final String EGLPROJECTMIGRATIONVERSION80 = "8.0.0";
	public static final QualifiedName VALUESTOREKEY_EGLPROJECTMIGRATIONVERSION = new QualifiedName(null,"EGLProjectMigrationVersion");
	
//	update build descriptor with runtime data source change constants 
	public static final String BD_UPDATE_ON_DATA_SOURCE_CHANGE_PROMPT= "promptBeforeUpdatingBuildDescAfterDataSourceChange"; //$NON-NLS-1$
	public static final String BD_UPDATE_ON_DATA_SOURCE_CHANGE_ALWAYS = "alwaysUpdateBuildDescAfterDataSourceChange"; //$NON-NLS-1$
	public static final String BD_UPDATE_ON_DATA_SOURCE_CHANGE_NEVER = "neverUpdateBuildDescAfterDataSourceChange"; //$NON-NLS-1$
	
	public static String[] defaultArchitectureOptionNonMnemonicStrings;
	
	private IEGLBuildDescriptorLocator bdLocator = null;
	public static final String PT_BUILDDESCRIPTORLOCATOR = "com.ibm.etools.egl.buildDescriptorLocator"; //$NON-NLS-1$


	/**
	 * the name of the egl validation resource bundle
	 */
	public static final String EGL_VALIDATION_RESOURCE_BUNDLE = "org.eclipse.edt.compiler.internal.core.builder.EGLValidationResources"; //$NON-NLS-1$

	public static final QualifiedName GENERATED_JAVA_FOLDER = new QualifiedName( null, "GeneratedJavaFolder" );
	public static final QualifiedName CUSTOM_JAVA_FOLDER = new QualifiedName( null, "CustomJavaFolder" );
	public static final QualifiedName DEBUG_JAVASCRIPT_FOLDER = new QualifiedName( null, "DebugJavaScriptFolder" );
	public static final QualifiedName TARGET_JAVASCRIPT_FOLDER = new QualifiedName( null, "TargetJavaScriptFolder" );

	public static final int TARGET_RUNTIME_JAVA_MASK = 1 << 0;
	public static final int TARGET_RUNTIME_JAVASCRIPT_MASK = 1 << 1;
	public static final int TARGET_RUNTIME_COBOL_MASK = 1 << 2;
	public static final QualifiedName VALUESTOREKEY_TARGETRUNTIME= new QualifiedName(null,"EGLTargetRuntimeMask"); //$NON-NLS-1$
	
	/**
	 * Keep track of the singleton.
	 */
	protected static EGLBasePlugin plugin;
	/**
	 * EGLBasePlugin constructor comment.
	 * @param descriptor org.eclipse.core.runtime.IPluginDescriptor
	 */
	public EGLBasePlugin() {
		super();
		plugin = this;

	}
	/**
	 * Get the singleton instance.
	 */
	public static EGLBasePlugin getPlugin() {
		return plugin;
	}
	

	public static String getDestinationUserIDPreference() {
		String userIDPreference = EGLBasePlugin.getPlugin().getPreferenceStore().getString(DESTINATION_USERID);
		if (userIDPreference == null)
			return ""; //$NON-NLS-1$
		else
			return userIDPreference;
	}
	
	public static void setDestinationUserIDPreference(String value) {
		EGLBasePlugin.getPlugin().getPreferenceStore().setValue(DESTINATION_USERID, value);
	}
	
	public static void setDestinationPasswordPreference(String value) {
		
		String destPassword = value;
		if (destPassword.trim().length() > 0 && !Encoder.isEncoded(destPassword)) {
			destPassword = Encoder.encode(destPassword);
		}

		EGLBasePlugin.getPlugin().getPreferenceStore().setValue(DESTINATION_PASSWORD, destPassword);
	}

	public static String getDestinationPasswordPreference() {
		
		String passwordPreference = EGLBasePlugin.getPlugin().getPreferenceStore().getString(DESTINATION_PASSWORD);
		if (passwordPreference == null)
			return ""; //$NON-NLS-1$
		else {
			if (passwordPreference.trim().length() > 0 && Encoder.isEncoded(passwordPreference)) {
				passwordPreference = Encoder.decode(passwordPreference);
			}
			return passwordPreference;
		}	
	}
	
	public static boolean isASTInstalled() {
		State state = Platform.getPlatformAdmin().getState(false); // Have to call getState(false) to get an immutable state.
		BundleDescription bundle = state.getBundle(EGL_AST_PLUGIN_ID, null);
		if(bundle != null)
			return bundle.getResolvedRequires().length == bundle.getRequiredBundles().length;
		return false;
	}
	
	private static boolean isEGLComponent(String id) {
		IActivityManager activityManager = PlatformUI.getWorkbench().getActivitySupport().getActivityManager();
		IActivity activity = activityManager.getActivity(id);
		return activity.isEnabled();
	}
	
	public static boolean isCore() {
		return isEGLComponent(EGL_CORE_ACTIVITY);
	}
	
	public static boolean isJSF() {
		return isEGLComponent(EGL_JSF_ACTIVITY);
	}
	
	public static boolean isDLI() {
		return isEGLComponent(EGL_DLI_ACTIVITY);
	}
	
	public static boolean isRCP() {
		return isEGLComponent(EGL_RCP_ACTIVITY);
	}
	
	public static boolean isCUI() {
		return isEGLComponent(EGL_CUI_ACTIVITY);
	}
	
	public static boolean isReports() {
		return isEGLComponent(EGL_REPORTS_ACTIVITY);
	}
	
	public static boolean isTUI() {
		return isEGLComponent(EGL_TUI_ACTIVITY);
	}
	
	public static boolean isRUI() {
		return isEGLComponent(EGL_RUI_ACTIVITY);
	}
	
	public static boolean isVGUI() {
		return isEGLComponent(EGL_VGUI_ACTIVITY);
	}
	
	public static boolean isBIRT() {
		return isEGLComponent(EGL_BIRT_ACTIVITY);
	}
	
	
	public static boolean isMQ() {
		return isEGLComponent(EGL_MQ_ACTIVITY);
	}
	
	public static String getHelpIDPrefix() {
		return EGL_HELP_ID_PREFIX_WSAD;
	}

	/** 
	 * Sets default preference values. These values will be used
	 * until some preferences are actually set using Preference dialog.
	 */
	protected void initializeDefaultPreferences(IPreferenceStore store) {
//		// These settings are needed for default text preferences (such as
//		// red squiggles)!
//		TextEditorPreferenceConstants.initializeDefaultValues(store);

		// These settings will show up when Preference dialog
		// opens up for the first time.
		store.setDefault(OUTPUT_CODESET, "UTF-8"); //$NON-NLS-1$
		store.setDefault(VAGCOMPATIBILITY_OPTION, false);
		store.setDefault(EGLFEATURE_MASK, EGLBasePlugin.EGLFEATURE_EGLDD);
		store.setDefault(EGLWEBFEATURE_WJSF, true);
		store.setDefault(EGLWEBFEATURE_JAVAOBJINTERFACE, true);
		store.setDefault(BD_UPDATE_ON_DATA_SOURCE_CHANGE_PROMPT, true);
//		store.setDefault(FULL_BUILD_OPTION, false);
		
		store.setDefault(BIDI_ENABLED_OPTION, false);
		store.setDefault(BIDI_VISUAL_SOURCEASSISTANT_OPTION, false);
		store.setDefault(BIDI_RTL_SOURCEASSISTANT_OPTION, false);
		store.setDefault(BIDI_VISUAL_EDITING_OPTION, false);
		
		store.setDefault(ALIAS_JSF_HANDLER_NAMES, true);
		//No defaults supplied for destination user id and password
	}
	

	public IEGLBuildDescriptorLocator getBdLocator() {
		if (bdLocator == null) {
			initializeBDLocator();
		}
		return bdLocator;
	}
	private void initializeBDLocator() {
			try {
				IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(PT_BUILDDESCRIPTORLOCATOR);
				if (extensionPoint != null) {
					IConfigurationElement[] extensions = extensionPoint.getConfigurationElements();
					if (extensions.length > 0) {
						bdLocator = (IEGLBuildDescriptorLocator) extensions[extensions.length - 1].createExecutableExtension("class"); //$NON-NLS-1$
					}
				}
			} catch (CoreException e) {
			}

	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
	}
}
