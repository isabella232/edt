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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.formatting.CodeFormatterConstants;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Category;
import org.eclipse.edt.ide.ui.internal.formatting.profile.DefaultProfile;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Preference;
import org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.eclipse.ui.part.PageBook;

public class WhiteSpaceTabPage extends ModifyDialogTabPage {
		
	private class WSOptionNodeComponent extends TreeControlPreference implements ICheckStateListener{			
		private Composite fComposite;
						
		public WSOptionNodeComponent(String dlgSettingKey){
			super(fDialogSettings, dlgSettingKey);
		}
		
		public void createContents(final int numColumns, final Composite parent){
			fComposite= new Composite(parent, SWT.NONE);
			fComposite.setLayoutData(createGridData(numColumns, GridData.HORIZONTAL_ALIGN_FILL, SWT.DEFAULT));
			fComposite.setLayout(createGridLayout(numColumns, false));
			
			super.createContents(numColumns, fComposite);
		}
		
		protected TreeViewer createTreeViewer(Composite composite, int numColumns){
			return new ContainerCheckedTreeViewer(fComposite, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);			
		}		
		
		public void initialize(){
			((ContainerCheckedTreeViewer)fTreeViewer).addCheckStateListener(this);
			super.initialize();
		    refreshState();			
		}
		
		public void refreshState(){	
			ContainerCheckedTreeViewer checkedTreeViewer = (ContainerCheckedTreeViewer)fTreeViewer;
			List checked = getCheckElements(fOptionNodePreferenceTreeMap);
			checkedTreeViewer.setGrayedElements(new Object[0]);
			checkedTreeViewer.setCheckedElements(checked.toArray());
		    fPreview.setPreviewText(""); //$NON-NLS-1$
		    if (fLastSelected != null) {
		    	fPreview.setPreviewText(getPreviewText(fLastSelected));
		    }
		    doUpdatePreview();
			
		}
				
		private List getCheckElements(Map map) {
			final ArrayList checked= new ArrayList(100);		
		    for (Iterator it=map.values().iterator(); it.hasNext();){
		    	Object val = it.next();
		    	if(val instanceof ModifyDialogTabPage.OptionTreeNode){
		    		ModifyDialogTabPage.OptionTreeNode wsnode = (ModifyDialogTabPage.OptionTreeNode)val;
		    		checked.addAll(getCheckElements(wsnode.getChildren()));
		    	}
		    	else if(val instanceof ModifyDialogTabPage.OptionLeafNode){
		    		ModifyDialogTabPage.OptionLeafNode optionLeafNode = (ModifyDialogTabPage.OptionLeafNode)val;
		    		if(Boolean.parseBoolean(getCurrentValue(optionLeafNode.fPreferenceKey)))
		    			checked.add(optionLeafNode);		    		
		    	}
		    }
		    return checked;
		}
		
		public void checkStateChanged(CheckStateChangedEvent event) {
			Object obj = event.getElement();
			String currValue = Boolean.toString(event.getChecked());
			changeCheckState(obj, currValue);
			doUpdatePreview();
			notifyValuesModified();			
		}
		
		private void changeCheckState(Object obj, String currStateValue){
			if(obj instanceof ModifyDialogTabPage.OptionTreeNode){
				ModifyDialogTabPage.OptionTreeNode wsnode = (ModifyDialogTabPage.OptionTreeNode)obj;
				for(Iterator it = wsnode.getChildren().values().iterator(); it.hasNext();){
					changeCheckState(it.next(), currStateValue);
				}
			}
			else if(obj instanceof ModifyDialogTabPage.OptionLeafNode){
				ModifyDialogTabPage.OptionLeafNode optNode = (ModifyDialogTabPage.OptionLeafNode)obj;
				setCurrentValue(optNode.fPreferenceKey, currStateValue);
			}			
		}
		
		public Control getControl(){
			return fComposite;
		}				
	}
	
	private final class SwitchComponent extends SelectionAdapter {
	    private Combo fSwitchCombo; 
	    private PageBook fPageBook;
	    private final WSOptionNodeComponent fWSPosComponent;
	    private final WSOptionNodeComponent fEGLSyntaxElemComponent;
	    
	    private final static String PREF_WSPos_KEY= EDTUIPlugin.PLUGIN_ID + "formatter_page.white_space_wspos_view.node"; //$NON-NLS-1$
		private final static String PREF_EGLSyn_KEY= EDTUIPlugin.PLUGIN_ID + "formatter_page.white_space_egl_view.node"; //$NON-NLS-1$
	    
	    public SwitchComponent(){
	    	fWSPosComponent = new WSOptionNodeComponent(PREF_WSPos_KEY);
	    	fEGLSyntaxElemComponent = new WSOptionNodeComponent(PREF_EGLSyn_KEY);
	    }
	    
        public void widgetSelected(SelectionEvent e) {
            final int index= fSwitchCombo.getSelectionIndex();
            switch(index){
            case CodeFormatterConstants.FORMATTER_PREF_WS_SORTBY_WSPOSITION:
    		    fWSPosComponent.refreshState();
                fPageBook.showPage(fWSPosComponent.getControl());            	
            	break;
            case CodeFormatterConstants.FORMATTER_PREF_WS_SORTBY_EGLSYNTAX:
    		    fEGLSyntaxElemComponent.refreshState();
                fPageBook.showPage(fEGLSyntaxElemComponent.getControl());            	
            	break;
            }
        }
        
        public void createContents(int numColumns, Composite parent, Combo switchCombo) {
            fPageBook= new PageBook(parent, SWT.NONE);
            fPageBook.setLayoutData(createGridData(numColumns, GridData.FILL_BOTH, SWT.DEFAULT));
            
            fWSPosComponent.createContents(numColumns, fPageBook);
            fEGLSyntaxElemComponent.createContents(numColumns, fPageBook);
	    	fSwitchCombo = switchCombo;
        }
        
        public void initialize() {
        	fSwitchCombo.addSelectionListener(this);
        	fWSPosComponent.initialize();
        	fEGLSyntaxElemComponent.initialize();
        	restoreSelection();
        }
        
        public void populatePreferenceMapData(Preference pref){
        	fWSPosComponent.populatePreferenceMapData(pref, ProfilePackage.eINSTANCE.getPreference_Display());
        	fEGLSyntaxElemComponent.populatePreferenceMapData(pref, ProfilePackage.eINSTANCE.getPreference_AltDisplay());
        }
        
        private void restoreSelection() {
        	int selectWSPos = Integer.parseInt(getCurrentValue(CodeFormatterConstants.FROMATTER_PREF_WS_SORTBY));
        	fSwitchCombo.select(selectWSPos);
        	switch(selectWSPos){
        	case CodeFormatterConstants.FORMATTER_PREF_WS_SORTBY_WSPOSITION:
            	fWSPosComponent.refreshState();
                fPageBook.showPage(fWSPosComponent.getControl());        		
        		break;
        	case CodeFormatterConstants.FORMATTER_PREF_WS_SORTBY_EGLSYNTAX:
            	fEGLSyntaxElemComponent.refreshState();
			    fPageBook.showPage(fEGLSyntaxElemComponent.getControl());        		
        		break;
        	}
        }        
	}	
	

	
	private final SwitchComponent fSwitchComponent;
	protected final IDialogSettings fDialogSettings; 

	public WhiteSpaceTabPage(ModifyDialog modifyDialog, DefaultProfile defaultProfile, Category category, Map allPreferenceSettings) {
		super(modifyDialog, defaultProfile, category, allPreferenceSettings) ;
		fDialogSettings = EDTUIPlugin.getDefault().getDialogSettings();
		fSwitchComponent = new SwitchComponent();
	}

	protected void initializePage() {
		fSwitchComponent.initialize();
	}
	
	protected void createTreePref(Preference pref){
		fSwitchComponent.populatePreferenceMapData(pref);
	}
			
	protected ComboPreference createComboPref(Composite composite, int numColumns, String labelText, String categoryID, String prefID, String[] values, String[] items, String previewTextPerPreference) {
		ComboPreference comboPref = super.createComboPref(composite, numColumns, labelText, categoryID, prefID, values, items, previewTextPerPreference) ;
		
		fSwitchComponent.createContents(numColumns, composite, (Combo)comboPref.getControl());
		
		return comboPref;
	}	
}
