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
package org.eclipse.edt.ide.ui.internal;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

public class EGLPerspectiveFactory implements IPerspectiveFactory {

	public static String ID_NEW_EGL_PROJECT_WIZARD 	= "org.eclipse.edt.ide.ui.newProjectWizard"; //$NON-NLS-1$	
	public static String ID_NEW_EGL_PACKAGE_WIZARD 	= "org.eclipse.edt.ide.ui.newPackageWizard"; //$NON-NLS-1$
	public static String ID_NEW_EGL_PGM_WIZARD 		= "org.eclipse.edt.ide.ui.newProgramWizard"; //$NON-NLS-1$
	public static String ID_NEW_EGL_LIB_WIZARD 		= "org.eclipse.edt.ide.ui.newLibraryWizard"; //$NON-NLS-1$
	public static String ID_NEW_EGL_SERVICE_WIZARD 	= "org.eclipse.edt.ide.ui.newServiceWizard"; //$NON-NLS-1$
	public static String ID_NEW_EGL_INTERFACE_WIZARD = "org.eclipse.edt.ide.ui.newInterfaceWizard"; //$NON-NLS-1$
	public static String ID_NEW_EGL_RECORD_WIZARD 	= "org.eclipse.edt.ide.ui.newRecordWizard"; //$NON-NLS-1$
	public static String ID_NEW_EGL_SOURCE_FILE_WIZARD 	= "org.eclipse.edt.ide.ui.newSourceFileWizard"; //$NON-NLS-1$
	public static String ID_NEW_EGL_RUIHANDLER_WIZARD = "org.eclipse.edt.ide.ui.newRuiHandlerWizard"; //$NON-NLS-1$	
	public static String ID_NEW_EGL_RUIWIDGET_WIZARD = "org.eclipse.edt.ide.ui.newRuiWidgetWizard"; //$NON-NLS-1$	

	public static String ID_SERVERS_VIEW 			= "org.eclipse.wst.server.ui.ServersView"; //$NON-NLS-1$

	public static String ID_EGL_DATA_VIEW 			= "org.eclipse.edt.ide.rui.visualeditor.views.PageDataView"; //$NON-NLS-1$
	public static String ID_PARTS_REFERENCE_VIEW 	= "org.eclipse.edt.ide.ui.views.partsReference.EGLPartsReference"; //$NON-NLS-1$
	public static String ID_PARTS_BROWSER_VIEW 		= "org.eclipse.edt.ide.ui.views.partsbrowser.EGLPartsBrowser"; //$NON-NLS-1$
	public static String ID_SQL_RESULTS_VIEW 		= "org.eclipse.edit.ide.ui.sql.view.SQLResultsViewPart"; //$NON-NLS-1$

	public static String ID_DATABASE_DEV_PERSPECTIVE = "org.eclipse.datatools.sqltools.sqleditor.perspectives.EditorPerspective";
	public static String ID_WEB_PERSPECTIVE 		= "org.eclipse.wst.web.ui.webDevPerspective";

	// add new EGL wizard to the wizard menu	
	public static final String[] EGL_PROJECT_WIZARDIDS = new String[] {
		EGLPerspectiveFactory.ID_NEW_EGL_INTERFACE_WIZARD,
		EGLPerspectiveFactory.ID_NEW_EGL_LIB_WIZARD,
		EGLPerspectiveFactory.ID_NEW_EGL_PACKAGE_WIZARD,
		EGLPerspectiveFactory.ID_NEW_EGL_PGM_WIZARD,
		EGLPerspectiveFactory.ID_NEW_EGL_RECORD_WIZARD,
		EGLPerspectiveFactory.ID_NEW_EGL_RUIHANDLER_WIZARD,
		EGLPerspectiveFactory.ID_NEW_EGL_RUIWIDGET_WIZARD,
		EGLPerspectiveFactory.ID_NEW_EGL_SERVICE_WIZARD,
		EGLPerspectiveFactory.ID_NEW_EGL_SOURCE_FILE_WIZARD	 
	};

	/**
	 * Constructs a new Default layout engine.
	 */
	public EGLPerspectiveFactory() {
		super();
	}
	public void createInitialLayout(IPageLayout layout) {
		// Editors are placed for free.
		String editorArea = layout.getEditorArea();

		// Top left.
		IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, (float)0.26, editorArea);//$NON-NLS-1$
		topLeft.addView(IPageLayout.ID_PROJECT_EXPLORER);
		topLeft.addPlaceholder(IPageLayout.ID_RES_NAV);
//		topLeft.addPlaceholder(ID_PARTS_REFERENCE_VIEW);

		// Bottom left.
		IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, (float)0.50, "topLeft");//$NON-NLS-1$
		bottomLeft.addView(IPageLayout.ID_OUTLINE);
		bottomLeft.addView(ID_EGL_DATA_VIEW);

		// Bottom right.
		IFolderLayout bottomRight = layout.createFolder("bottomRight", IPageLayout.BOTTOM, (float)0.66, editorArea); //$NON-NLS-1$
		bottomRight.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottomRight.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		bottomRight.addView(IPageLayout.ID_PROP_SHEET);
		bottomRight.addView(ID_SERVERS_VIEW);
//		bottomRight.addPlaceholder(ID_SQL_RESULTS_VIEW);
//		bottomRight.addPlaceholder(ID_PARTS_BROWSER_VIEW);

		// add EGL views to the show view menu
//		layout.addShowViewShortcut(ID_EGL_DATA_VIEW);
//		layout.addShowViewShortcut(ID_PARTS_BROWSER_VIEW);
//		layout.addShowViewShortcut(ID_PARTS_REFERENCE_VIEW);	
//		layout.addShowViewShortcut(ID_SQL_RESULTS_VIEW);	

		layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(IPageLayout.ID_PROJECT_EXPLORER);
		layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
		layout.addShowViewShortcut(ID_SERVERS_VIEW);
		layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);	

		// add related perspectives to the perspective menu
		layout.addPerspectiveShortcut(ID_DATABASE_DEV_PERSPECTIVE); 
		layout.addPerspectiveShortcut(IDebugUIConstants.ID_DEBUG_PERSPECTIVE);
		layout.addPerspectiveShortcut(JavaUI.ID_PERSPECTIVE);
		layout.addPerspectiveShortcut(ID_WEB_PERSPECTIVE);

		// add new EGL wizard to the wizard menu
		layout.addNewWizardShortcut(ID_NEW_EGL_PROJECT_WIZARD);
		for(int i=0; i<EGL_PROJECT_WIZARDIDS.length; i++)
			layout.addNewWizardShortcut(EGL_PROJECT_WIZARDIDS[i]);

		// add open part button to toolbar and action to Navigate menu
		layout.addActionSet(EGLUI.ID_ACTION_SET);
		layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);

		// add wizard action set to toolbar
		layout.addActionSet(EGLUI.ID_NON_WEB_WIZARD_ACTION_SET);
	}

}
