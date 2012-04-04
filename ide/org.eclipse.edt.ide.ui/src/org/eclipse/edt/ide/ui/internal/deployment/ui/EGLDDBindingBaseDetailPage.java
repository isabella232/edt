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
package org.eclipse.edt.ide.ui.internal.deployment.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;


public abstract class EGLDDBindingBaseDetailPage extends EGLDDBaseDetailPage{
	protected Text fNameText;
	
	
	protected void createControlsInTopSection(FormToolkit toolkit, Composite parent)
	{
		createNameControl(toolkit, parent);
	}

	protected void createNameControl(FormToolkit toolkit, Composite parent) {
		createSpacer(toolkit, parent, nColumnSpan);
		toolkit.createLabel(parent, SOAMessages.NameLabel);
		fNameText = toolkit.createText(parent, "", SWT.SINGLE); //$NON-NLS-1$
		fNameText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				HandleNameChanged();				
			}			
		});

		GridData gd = new GridData(GridData.FILL_HORIZONTAL|GridData.VERTICAL_ALIGN_BEGINNING);
		gd.widthHint = 10;
		gd.horizontalSpan = nColumnSpan-1;
		fNameText.setLayoutData(gd);
	}	
	
	protected abstract void HandleNameChanged();
	
	
	protected void refreshMainTableViewer(){
		FormPage containedFormPage = getContainerFormPage();
		if(containedFormPage instanceof EGLDDBindingFormPage)
			((EGLDDBindingFormPage)containedFormPage).refreshBlockTableViewer();
	}		

/*	public static String configInterfaceEglFrInterfacePart(IPart interfacePart) throws CoreException
	{
		String interfacePartName = interfacePart.getElementName();
		
		//bind the ast tree with live env and scope
		IWorkingCopy[] currRegedWCs = EGLCore.getSharedWorkingCopies(EGLUI.getBufferFactory());
		
		IProject proj = interfacePart.getEGLProject().getProject();		
		IEGLFile eglFile = interfacePart.getEGLFile();
		IFile file = (IFile)(eglFile.getCorrespondingResource());
		String packageName = interfacePart.getPackageFragment().getElementName();
		Path pkgPath = new Path(packageName.replace('.', '\\')); 					
		String[] pkgName = Util.pathToStringArray(pkgPath);

		final String[] alias = new String[]{""};
		//visit AST part tree(already bound)		
		WorkingCopyCompiler.getInstance().compilePart(proj, pkgName, file, currRegedWCs, interfacePartName, 		
					new IWorkingCopyCompileRequestor(){			
						public void acceptResult(WorkingCopyCompilationResult result) {
							Part boundPart = (Part)result.getBoundPart();
							final IBinding interfacePartBinding = result.getPartBinding();							
							boundPart.accept(new AbstractASTVisitor(){
								public boolean visit(Interface interface1){
									//make sure it is the one we care about
									IAnnotationBinding aliasAnnotationBinding = interfacePartBinding.getAnnotation(SystemEnvironmentPackageNames.EGL_CORE, IEGLConstants.PROPERTY_ALIAS);
									if(aliasAnnotationBinding != null)
									{
										alias[0] = aliasAnnotationBinding.getValue().toString();
									}									
									return false;											
								}								
							});
						}
				}
		);	
		return alias[0];
	}
*/	
}
