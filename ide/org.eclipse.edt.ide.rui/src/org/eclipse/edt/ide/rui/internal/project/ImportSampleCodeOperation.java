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
package org.eclipse.edt.ide.rui.internal.project;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.rui.internal.wizards.RuiNewWizardMessages;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class ImportSampleCodeOperation extends WorkspaceModifyOperation {

    private String resourcePluginName = "org.eclipse.edt.ide.ui.resources";
    private String libraryResourceFolder = "/org.eclipse.edt.ide.ui.rui/";
    private String sampleProjectName;
    private String destinationProjectName;

    public ImportSampleCodeOperation(ISchedulingRule rule, String sampleProjectName, String destinationProjectName) {
        super(rule);
        this.sampleProjectName = sampleProjectName;
        this.destinationProjectName = destinationProjectName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.actions.WorkspaceModifyOperation#execute(org.eclipse.core
     * .runtime.IProgressMonitor)
     */
    protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
        if (sampleProjectName != null && destinationProjectName != null) {
            importSampleCode(monitor);
        }
    }

    private void importSampleCode(IProgressMonitor monitor) {
        final IProject destProject = ResourcesPlugin.getWorkspace().getRoot().getProject(destinationProjectName);
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }

        // Only import the sample if the destination project already exists
        if (destProject.exists()) {
            try {
                URL url = CommonUtilities.getWidgetProjectURL(resourcePluginName, libraryResourceFolder, sampleProjectName);

                if (url != null) {
                    ZipFile sampleSource = new ZipFile(url.getFile());
                    try {
                        Enumeration<? extends ZipEntry> entries = sampleSource.entries();

                        monitor.beginTask(RuiNewWizardMessages.ImportTask + destinationProjectName, sampleSource.size());

                        while (entries.hasMoreElements()) {
                            ZipEntry entry = entries.nextElement();
                            
                            monitor.subTask(entry.getName());

                            try {
                                if (entry.isDirectory()) {
                                    destProject.getFolder(new Path(entry.getName())).create(true, true, monitor);
                                } else {
                                    IFile file = destProject.getFile(new Path(entry.getName()));

                                    if (!file.getName().equals(".project")) {                                    
                                        if (!file.exists()) {
                                            file.create(sampleSource.getInputStream(entry), true, monitor);
                                        } else {
                                            file.setContents(sampleSource.getInputStream(entry), true, true, monitor);
                                        }
                                    }
                                }
                            } catch (Exception ex) {

                            }
                            
                            monitor.worked(1);
                        }
                    } finally {
                        sampleSource.close();
                    }
                }
            } catch (Exception e) {
                EGLLogger.log(this, e);
            } finally {
                monitor.done();
            }
        }
    }
    
}
