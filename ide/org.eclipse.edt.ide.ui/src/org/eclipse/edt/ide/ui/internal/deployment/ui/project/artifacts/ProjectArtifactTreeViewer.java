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
package org.eclipse.edt.ide.ui.internal.deployment.ui.project.artifacts;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 *
 */
public class ProjectArtifactTreeViewer {

	private CheckboxTreeViewer viewer;
	
	private List treeNodeStateListeners;

	public ProjectArtifactTreeViewer() {
		this.treeNodeStateListeners = new ArrayList();
	}
	
	public void addTreeNodeStateListener(ITreeNodeStateListener listener) {
		treeNodeStateListeners.add( listener );
	}
	
	public void removeTreeNodeStateListener(ITreeNodeStateListener listener) {
		treeNodeStateListeners.remove( listener );
	}
	
	public void createControl(Composite parent) {
		this.viewer = new CheckboxTreeViewer(parent, SWT.BORDER){
			public void doUpdateItem(final Item item, Object element) {
				super.doUpdateItem( item, element );
				((TreeItem)item).setChecked( ((TreeNode)element).isChecked() );
				((TreeItem)item).setGrayed( ((TreeNode)element).isGrayed() );
			}
			protected void preservingSelection(Runnable updateCode) {
		        updateCode.run();
		    }
		};
		this.viewer.setContentProvider(new ProjectArtifactTreeViewerContentProvider());
		this.viewer.setLabelProvider(new ProjectArtifactTreeViewerLabelProvider());
		this.viewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(final CheckStateChangedEvent event) {
				final IWorkbench wb = PlatformUI.getWorkbench();
				IProgressService ps = wb.getProgressService();
				try {
					ps.busyCursorWhile(new IRunnableWithProgress() {
						public void run(final IProgressMonitor pm) {
							wb.getDisplay().asyncExec(new Runnable() {
								public void run() {
									pm.beginTask( "Progress Label", 23 );
									
									TreeNode node = (TreeNode)event.getElement();
									boolean state = event.getChecked();
									node.setChecked(state);
									
									Set changedNodes = new HashSet( 50 );
									changedNodes.add( node );
									pm.worked( 1 );
									
									walkDownTree(node, state, changedNodes);
									pm.worked( 10 );
									walkUpTree(node, state, changedNodes);
									pm.worked( 10 );
									
									for (Iterator it = treeNodeStateListeners.iterator(); it.hasNext();) {
										((ITreeNodeStateListener)it.next()).changed(
												(TreeNode[])changedNodes.toArray( new TreeNode[ changedNodes.size() ] ) );
									}
									pm.worked( 2 );
									pm.done();
								}
							} );
						}
					} );
				}
				catch (InvocationTargetException ioe) {
					EDTUIPlugin.log( ioe );
				}
				catch (InterruptedException ie) {
					EDTUIPlugin.log( ie );
				}
			}
		});
	}
	
	public void initializeTree(TreeNodeRoot root, List resourceOmissions) {
		buildTree(root, resourceOmissions);
		this.viewer.setInput(root);
	}

	/**
	 * Walking down the tree is the easy way to go. All children have to be either set checked 
	 * and not grayed or unchecked to match the setting of the seed. This method will recursively 
	 * walk down the entire tree beneath the seed node.
	 * 
	 * @param seed
	 */
	private void walkDownTree(TreeNode seed, boolean checked, Set changedNodes) {
		if (seed instanceof TreeNodeFolder) {
			if (!checked) {
				setChecked(seed, false, false, changedNodes);
			}
			for (Iterator iterator = seed.getChildren().iterator(); iterator.hasNext();) {
				TreeNode node = (TreeNode) iterator.next();
				setChecked(node, checked, false, changedNodes);
				if (node instanceof TreeNodeFolder) {
					walkDownTree(node, checked, changedNodes);
				}
			}
		}
	}
	
	private void setChecked(TreeNode node, boolean checked, boolean gray, Set changedNodes) {
		if ( node.isChecked != checked )
		{
			changedNodes.add( node );
		}
		node.setChecked(checked);
		node.setGrayed(gray);
		this.viewer.update( node, null );
	}
	
	/**
	 * Walking up the tree is a little more complex then down the tree because in
	 * certain situations we have to look at all the children of a parent to be able
	 * to deduce what setting the parent should put into. I have tried to be as smart 
	 * as possible and only check all children of a parent when I absolutely have to.
	 *  
	 * @param seed
	 */
	private void walkUpTree(TreeNode seed, boolean checked, Set changedNodes) {
		boolean isGray = seed.isGrayed();
		boolean isChecked = checked;
		TreeNode parent = seed.getParent();
		if (parent instanceof TreeNodeFolder) {
			boolean isParentGray = parent.isGrayed();
			boolean isParentChecked = parent.isChecked();
			if (isGray && isChecked) {
				if(!(isParentGray && isParentChecked)){
					setChecked( parent, true, true, changedNodes );
					walkUpTree(parent, isChecked, changedNodes);
				}
			} else {
				if (!isGray && isChecked) {
					if (!isParentChecked) {
						if (parent.getChildren().size() == 1) {
							setChecked(parent, true, false, changedNodes);
							walkUpTree(parent, true, changedNodes);
						} else {
							setChecked(parent, true, true, changedNodes);
							walkUpTree(parent, true, changedNodes);
						}
					} else {
						checkParent(parent, changedNodes);
						walkUpTree(parent, parent.isChecked(), changedNodes);
					}
				} else {
					if (!isGray && !isChecked) {
						if (isParentChecked) {
							if (!isParentGray) {
								checkParent(parent, changedNodes);
								walkUpTree(parent, parent.isChecked(), changedNodes);
							} else {
								checkParent(parent, changedNodes);
								walkUpTree(parent, parent.isChecked(), changedNodes);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Look at the settings of all the children of the passed parent to figure
	 * out what settings the parent should be put into.
	 * 
	 * @param seed
	 */
	private void checkParent(TreeNode seed, Set changedNodes) {
		if (seed instanceof TreeNodeFolder) {
			boolean foundCheckedChild = false;
			boolean foundUncheckedChild = false;
			for (Iterator iterator = seed.getChildren().iterator(); iterator.hasNext();) {
				TreeNode child = (TreeNode) iterator.next();
				if (child.isChecked()) {
					foundCheckedChild = true;
					if (child.isGrayed()) {
						foundUncheckedChild = true;
					}
				} else {
					foundUncheckedChild = true;
				}
			}
			if (! foundCheckedChild) {
				setChecked(seed, false, false, changedNodes);
			} else{
				if (foundUncheckedChild) {
					setChecked(seed, true, true, changedNodes);
				} else {
					setChecked(seed, true, false, changedNodes);
				}
			}
		}
	}

	public CheckboxTreeViewer getViewer() {
		return viewer;
	}
	
	public void buildTree(TreeNode node, List resourceOmissions) {
		for (Iterator iterator = node.getChildren().iterator(); iterator.hasNext();) {
			TreeNode child = (TreeNode) iterator.next();
			checkAgainstOmissions(child, resourceOmissions);
			buildTree(child, resourceOmissions);
		}
		
		initializeFolderSelections( node );
	}

	private void checkAgainstOmissions(TreeNode node, List resourceOmissions) {
		node.setChecked(resourceOmissions == null || 
				!(resourceOmissions.contains(node.getResource().getFullPath().toString()) || 
				!node.getParent().isChecked()));
	}
	
	private void initializeFolderSelections(TreeNode sourceNode) {
		/**
		 * iterate through until we hit the bottom of a tree branch
		 */
		for (Iterator iterator = sourceNode.getChildren().iterator(); iterator.hasNext();) {
			TreeNode node = (TreeNode) iterator.next();
			if (node instanceof TreeNodeFolder) {
				initializeFolderSelections(node);
			}
		}
		if (sourceNode instanceof TreeNodeFolder) {
			/**
			 * Now we can take a look at the folder
			 */
			boolean selected = false;
			boolean unselected = false;
			boolean grey = false;
			List children = sourceNode.getChildren();
			int size = children.size();
			if ( size == 0 ) {
				selected = true; // the folder will be created during deploy even though there's no kids, so select it.
			} else {
				for (int i = 0; !grey && i < size; i++) {
					TreeNode node = (TreeNode) children.get( i );
					if (node.isGrayed()) {
						grey = true;
					} else {
						if (node.isChecked()) {
							selected = true;
						} else {
							unselected = true;
						}
					}
				}
			}
			if (grey) {
				sourceNode.setGrayed( true );
				sourceNode.setChecked( true );
			} else {
				if (selected) {
					if (unselected) {
						sourceNode.setGrayed( true );
						sourceNode.setChecked( true );
					} else {
						sourceNode.setGrayed( false );
						sourceNode.setChecked( true );
					}
				} else {
					sourceNode.setGrayed( false );
					sourceNode.setChecked( false );
				}
			}
		}
	}
	
	public static interface ITreeNodeStateListener
	{
		public void changed( TreeNode[] nodes );
	}
}
