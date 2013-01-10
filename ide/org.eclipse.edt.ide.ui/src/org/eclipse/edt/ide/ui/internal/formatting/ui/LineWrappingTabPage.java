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
package org.eclipse.edt.ide.ui.internal.formatting.ui;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Category;
import org.eclipse.edt.ide.ui.internal.formatting.profile.DefaultProfile;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Preference;
import org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

public class LineWrappingTabPage extends ModifyDialogTabPage {
	
	protected class OptionTreeMultiColumnLabelProvider extends OptionTreeLabelProvider implements ITableLabelProvider{
		private String[] fAllWrappingPolicys = null;
		public OptionTreeMultiColumnLabelProvider(String[] wrappingPolicyChoices){
			fAllWrappingPolicys = wrappingPolicyChoices;
		}
		
		public Image getColumnImage(Object element, int columnIndex) {
			return null ;
		}

		public String getColumnText(Object element, int columnIndex) {
			if(columnIndex == COLINDEX_EGLELEM)
				return getText(element);
			else if(columnIndex == COLINDEX_WRAPPOLICY){
				if(element instanceof OptionLeafNode){
					String strWrappingPolicy = getCurrentValue(((OptionLeafNode)element).fPreferenceKey);
					int wrappingPolicy = Integer.parseInt(strWrappingPolicy);
					if(fAllWrappingPolicys != null){
						return fAllWrappingPolicys[wrappingPolicy];
					}
				}
					
			}
			return ""; //$NON-NLS-1$
		}
		
	}
	
	private class WrappingNodeComponent extends TreeControlPreference{
		private final static String PREF_Wrapping_KEY= EDTUIPlugin.PLUGIN_ID + "formatter_page.line_wrapping_tab_page.node"; //$NON-NLS-1$
		private final String[] HEADER_TITLES = {NewWizardMessages.Col_EGLLang, NewWizardMessages.Col_CurrWrappingPolicy};
		private Combo fCombo;
		private Label fComboLabel;
		private String fInitialComboLabel ;
		public static final int DEFAULT_COLUMN_WIDTH = 80;
		
		protected WrappingNodeComponent() {
			super(fDialogSettings, PREF_Wrapping_KEY) ;
			fCombo = null;
			fComboLabel = null;
		}
		
		protected TreeViewer createTreeViewer(Composite composite, int numColumns) {
			TreeViewer treeViewer = new TreeViewer(composite, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);			
			
			//create 2 columns for the tree
			Tree tree = treeViewer.getTree();
			tree.setLinesVisible(true);
			tree.setHeaderVisible(true);			
			
			TableLayout tableLayout = new TableLayout();
			int maxWidth = DEFAULT_COLUMN_WIDTH;
			TreeColumn treeColumn = new TreeColumn(tree, SWT.LEFT, COLINDEX_EGLELEM);
			treeColumn.setText(HEADER_TITLES[COLINDEX_EGLELEM]);
			treeColumn.pack();
			
			int treeColWidth = Math.max(DEFAULT_COLUMN_WIDTH, treeColumn.getWidth());
			maxWidth = Math.max(maxWidth, treeColWidth);
			ColumnWeightData colData = new ColumnWeightData(treeColWidth, treeColWidth, true);
			tableLayout.addColumnData(colData);

			treeColumn = new TreeColumn(tree, SWT.LEFT, COLINDEX_WRAPPOLICY);
			treeColumn.setText(HEADER_TITLES[COLINDEX_WRAPPOLICY]);
			treeColumn.pack();
			treeColWidth = Math.max(DEFAULT_COLUMN_WIDTH, treeColumn.getWidth());
			maxWidth = Math.max(maxWidth, treeColWidth);
			colData = new ColumnWeightData(treeColWidth, treeColWidth, true);
			tableLayout.addColumnData(colData);		

			tree.setLayout(tableLayout);
			
			return treeViewer;
		}
		
		protected void createContents(int numColumns, Composite parent) {
			super.createContents(numColumns, parent) ;

			//set the tree control size
			GridData gd = (GridData)fTreeViewer.getControl().getLayoutData();
			gd.heightHint = 250;
			gd.horizontalAlignment = SWT.FILL;
			fTreeViewer.getControl().setLayoutData(gd);
		}		
		
		public void initialize() {
			fCombo.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e) {
					comboSelected(((Combo)e.widget).getSelectionIndex());
				}
			});
			//use the label provider that knows how to return column text - current wrapping policy
			fTreeViewer.setLabelProvider(new OptionTreeMultiColumnLabelProvider(fCombo.getItems()));
			
			super.initialize() ;
			
			//expand the tree
			if(fLastSelected instanceof OptionTreeNode)
				fTreeViewer.setExpandedElements(new Object[]{fLastSelected});
		}
		
		protected void comboSelected(int selectionIndex) {
			//get the current selection in the treeview
			IStructuredSelection sels = (IStructuredSelection)fTreeViewer.getSelection();			
			setWrappingPolicyValue(sels.toList(), selectionIndex);
			fTreeViewer.refresh();		//refresh the tree, wrapping policy value column
			doUpdatePreview();
			notifyValuesModified();
		}
		
		private void setWrappingPolicyValue(Collection nodes, int wrappingPolicy){
			if(nodes != null){
				for(Iterator it = nodes.iterator(); it.hasNext();){
					Object node = it.next();
					if(node instanceof OptionTreeNode){
						OptionTreeNode treeNode = (OptionTreeNode)node;
						setWrappingPolicyValue(treeNode.getChildren().values(), wrappingPolicy);
					}
					else if(node instanceof OptionLeafNode){
						OptionLeafNode leafNode = (OptionLeafNode)node;
						setCurrentValue(leafNode.fPreferenceKey, Integer.toString(wrappingPolicy));
					}
				}
			}
		}

		public void setComboPreference(Combo combo, Label label){
			fCombo = combo;
			fComboLabel = label;
			fInitialComboLabel = label.getText();
		}		
		
		public void selectionChanged(SelectionChangedEvent event) {
			super.selectionChanged(event);
			
			if(!event.getSelection().isEmpty() && fLastSelected != null){
				if(fLastSelected instanceof OptionLeafNode){
					OptionLeafNode node = (OptionLeafNode)fLastSelected;
					String currWrappingPolicy = getCurrentValue(node.fPreferenceKey);
					
					//need to update the combo selection
					fCombo.select(Integer.parseInt(currWrappingPolicy));
					fComboLabel.setText(NewWizardMessages.bind(NewWizardMessages.LineWrappingSettingFor, node.fDisplay));
				}
				else
					fComboLabel.setText(fInitialComboLabel);
			}
		}

	}
	
	protected final IDialogSettings fDialogSettings;
	private final WrappingNodeComponent fWrappingComponent;
	private static final int COLINDEX_EGLELEM= 0;
	private static final int COLINDEX_WRAPPOLICY = 1;
	
	
	public LineWrappingTabPage(ModifyDialog modifyDialog, DefaultProfile defaultProfile, Category category, Map allPreferenceSettings) {
		super(modifyDialog, defaultProfile, category, allPreferenceSettings);
		fDialogSettings = EDTUIPlugin.getDefault().getDialogSettings();
		fWrappingComponent = new WrappingNodeComponent();
	}
	
	protected void initializePage() {
		fWrappingComponent.initialize();
	}

	protected void createTreePref(Preference pref) {
		fWrappingComponent.populatePreferenceMapData(pref, ProfilePackage.eINSTANCE.getPreference_Display());
	}
	
	protected ComboPreference createComboPref(Composite composite, int numColumns, String labelText, String categoryID, String prefID, String[] values, String[] items, String previewTextPerPreference) {
		fWrappingComponent.createContents(numColumns, composite);
		ComboPreference comboPref =  super.createComboPref(composite, numColumns, labelText, categoryID,
				prefID, values, items, previewTextPerPreference) ;
		fWrappingComponent.setComboPreference((Combo)comboPref.getControl(), comboPref.getLabel());
		return comboPref;
	}
}
