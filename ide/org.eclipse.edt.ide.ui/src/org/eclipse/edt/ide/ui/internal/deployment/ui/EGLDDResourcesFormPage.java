/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.deployment.ui.project.artifacts.ProjectArtifactTreeViewer;
import org.eclipse.edt.ide.ui.internal.deployment.ui.project.artifacts.TreeNode;
import org.eclipse.edt.ide.ui.internal.deployment.ui.project.artifacts.TreeNodeFile;
import org.eclipse.edt.ide.ui.internal.deployment.ui.project.artifacts.TreeNodeFolder;
import org.eclipse.edt.ide.ui.internal.deployment.ui.project.artifacts.TreeNodeRoot;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class EGLDDResourcesFormPage extends EGLDDBaseFormPage
{
	private ProjectArtifactTreeViewer fArtifactTreeViewer;
	private TreeNodeRoot fTreeRoot;
	private Button fRefreshButton;
	
	public EGLDDResourcesFormPage( FormEditor editor, String id, String title )
	{
		super( editor, id, title );
	}
	
	protected void createFormContent( IManagedForm managedForm )
	{
		super.createFormContent( managedForm );
		final ScrolledForm form = managedForm.getForm();
		form.setText( SOAMessages.ArtifactsTitle );
		managedForm.setInput( getModelRoot() );
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		form.getBody().setLayout( layout );
		
		FormToolkit toolkit = managedForm.getToolkit();
		
		Composite client = createNonExpandableSection( form, toolkit, SOAMessages.ArtifactsTitle, SOAMessages.ArtifactsDetailPageDesc, 2 );
		createAdditionalArtifactsSection( toolkit, client );
		
		init();
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp( form.getBody(), getHelpID() );
	}
	
	private void init()
	{
		fTreeRoot.children = null;
		initializeArtifactTree();
		
		// On smaller displays, the locales section will not be visible. This
		// will cause the form to display a vertical scrollbar if needed.
		getManagedForm().reflow( true );
	}
	
	private void createAdditionalArtifactsSection( FormToolkit toolkit, Composite parent )
	{		
		Composite fArtifactSection = toolkit.createComposite( parent, SWT.WRAP );
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		fArtifactSection.setLayout( layout );
		GridData gd = new GridData( GridData.FILL_BOTH );
		gd.verticalSpan = 2;
		fArtifactSection.setLayoutData( gd );
		
		fArtifactTreeViewer = new ProjectArtifactTreeViewer();
		fArtifactTreeViewer.createControl( fArtifactSection );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		int layoutColumn = 1;
		gd.horizontalSpan = layoutColumn;
		gd.heightHint = 200;
		gd.widthHint = 60;
		fArtifactTreeViewer.getViewer().getTree().setLayoutData( gd );
		fArtifactTreeViewer.addTreeNodeStateListener( new ProjectArtifactTreeViewer.ITreeNodeStateListener() {
			public void changed( TreeNode[] nodes )
			{
				
				//pick up the folders that have changed 
				//rebuild the ommitted resources for each folder that has changed
				Set<TreeNodeFolder> changedFolders = new LinkedHashSet<TreeNodeFolder>( nodes.length );
				for ( int i = 0; i < nodes.length; i++ )
				{
					if ( nodes[ i ] instanceof TreeNodeFile && nodes[ i ].isDeployable() )
					{
						if(((TreeNodeFile)nodes[ i ]).getParent() instanceof TreeNodeFolder){
							changedFolders.add((TreeNodeFolder)((TreeNodeFile)nodes[ i ]).getParent());
						}
					}
					else if ( nodes[ i ] instanceof TreeNodeFolder )
					{
						changedFolders.add((TreeNodeFolder)nodes[ i ]);
					}
				}
				Set<String> omissionsToAdd = new LinkedHashSet<String>( nodes.length );
				Set<String> omissionsToRemove = new LinkedHashSet<String>( nodes.length );
				
				List<TreeNodeFolder> processedNodes = new ArrayList<TreeNodeFolder>();
				for(TreeNodeFolder folder : changedFolders){
					assembleOmissionList(folder, omissionsToAdd, omissionsToRemove, processedNodes);
				}
				EGLDDRootHelper.processResourceOmissionChanges( getModelRoot(), new ArrayList<String>(omissionsToAdd),  new ArrayList<String>(omissionsToRemove) );
			}
		} );
		fTreeRoot = new TreeNodeRoot( null, getEditorProject() );

		Composite buttonComposite = toolkit.createComposite( fArtifactSection );
		layoutColumn = 2;
		layout = new GridLayout( layoutColumn, false );
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		gd = new GridData(GridData.FILL_VERTICAL);
		buttonComposite.setLayout( layout );
		buttonComposite.setLayoutData(gd);
		
		fRefreshButton = toolkit.createButton(buttonComposite, SOAMessages.Refresh, SWT.PUSH);
		fRefreshButton.addSelectionListener(new SelectionListener(){

			public void widgetSelected(SelectionEvent e) {
				init();				
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}});
	}
	
	private void assembleOmissionList(TreeNodeFolder folder, Set<String> omissionsToAdd, Set<String> omissionsToRemove, List<TreeNodeFolder> processedFolders){
		if(!processedFolders.contains(folder)){
			processedFolders.add(folder);
			//ignore the root folder
			boolean folderIsChecked = folder instanceof TreeNodeRoot || folder.isChecked();
			for(Object node2 : folder.getChildren()){
				if(node2 instanceof TreeNode){
					if(node2 instanceof TreeNodeFolder){
						assembleOmissionList((TreeNodeFolder)node2, omissionsToAdd, omissionsToRemove, processedFolders);
					}
					else{
						//if the folder is checked then add each child based on it's settings
						if(folderIsChecked){
							//if the folder is checked make sure children are in the correct list
							if(((TreeNode)node2).isChecked()){
								//if the child is checked add it to the list to be removed from omissions
								omissionsToRemove.add(((TreeNode)node2).getResource().getFullPath().toString());
							}
							else{
								//if the child is not checked add it to the list to be added to omissions 
								omissionsToAdd.add(((TreeNode)node2).getResource().getFullPath().toString());
							}
						}
						else{
							//since the folder is not checked remove the children, the folder will be listed in the omissions
							omissionsToRemove.add(((TreeNode)node2).getResource().getFullPath().toString());
						}
					}
				}
			}
			if(!(folder instanceof TreeNodeRoot)){
				if(folder.isChecked()){
					//since this folder is checked remove it from omissions
					omissionsToRemove.add(folder.getResource().getFullPath().toString());
				}
				else{
					//treat treenoderoot as being checked
					//since the parent is checked this must be the first unchecked folder, it should be what is omitted
					if(folder.getParent() instanceof TreeNodeRoot || folder.getParent().isChecked()){
						omissionsToAdd.add(folder.getResource().getFullPath().toString());
					}
					else{
						//since the parent is unchecked remove this folder from omissions because the parent should be listed in omissions (or maybe its parent)
						omissionsToRemove.add(folder.getResource().getFullPath().toString());
					}
				}
			}
		}

	}
	private void initializeArtifactTree()
	{
		// No application in the model means no omissions. Don't cause an application to be created.
		List omissions = EGLDDRootHelper.getResourceOmissionsAsStrings( getModelRoot() );
		
		fArtifactTreeViewer.initializeTree( fTreeRoot, omissions );
	}

	protected void createSpacer( FormToolkit toolkit, Composite parent, int span )
	{
		Label spacer = toolkit.createLabel( parent, "" ); //$NON-NLS-1$
		GridData gd = new GridData();
		gd.horizontalSpan = span;
		spacer.setLayoutData( gd );
	}
	
	public void clear()
	{
	}
	
	public void setFocus() {
		if (fArtifactTreeViewer != null && fArtifactTreeViewer.getViewer() != null) {
			init();
			fArtifactTreeViewer.getViewer().getTree().setFocus();
		}
	}
	
	protected String getHelpID() {
		return IUIHelpConstants.EGLDD_EDITOR_RESOURCESPAGE;
	}
}
