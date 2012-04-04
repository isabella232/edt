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
package org.eclipse.edt.ide.ui.internal.formatting.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.formatting.CodeFormatterConstants;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Category;
import org.eclipse.edt.ide.ui.internal.formatting.profile.CheckControl;
import org.eclipse.edt.ide.ui.internal.formatting.profile.ComboControl;
import org.eclipse.edt.ide.ui.internal.formatting.profile.DefaultProfile;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Preference;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Preview;
import org.eclipse.edt.ide.ui.internal.formatting.profile.RadioControl;
import org.eclipse.edt.ide.ui.internal.formatting.profile.ReferenceControl;
import org.eclipse.edt.ide.ui.internal.formatting.profile.TextControl;
import org.eclipse.edt.ide.ui.internal.formatting.profile.TreeControl;
import org.eclipse.edt.ide.ui.internal.formatting.ui.ProfileManager.PreferenceSettingValue;
import org.eclipse.edt.ide.ui.internal.util.PixelConverter;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ModifyDialogTabPage {
		
	/**
	 * This is the default listener for any of the Preference
	 * classes. It is added by the respective factory methods and
	 * updates the page's preview on each change. 
	 */
	protected final Observer fUpdater= new Observer() {
		public void update(Observable o, Object arg) {
			doUpdatePreview();
			notifyValuesModified();
		}
	};
	
	
	/**
	 * The base class of all PreferenceWidget classes. A preference class provides a wrapper
	 * around one or more SWT widgets and handles the input of values for some key.
	 * On each change, the new value is written to the map and the listeners are notified.
	 */
	protected abstract class PreferenceWidget extends Observable {
	    private boolean fEnabled;
	    private String fCategoryID;
	    private String fPrefID;
	    protected String fPrefPreviewCode;
	    
	    /**
	     * Create a new Preference.
	     * @param preferences The map where the value is written.
	     * @param prefID The key for which a value is managed.
	     */
	    public PreferenceWidget(String catID, String prefID, String previewTextPerPreference) {
	        fEnabled= true;
	        fCategoryID = catID;
	        fPrefID= prefID;
	        fPrefPreviewCode = previewTextPerPreference;
	    }

	    /**
	     * Set the enabled state of all SWT widgets of this preference. 
	     * @param enabled new value
	     */
	    public final void setEnabled(boolean enabled) {
	        fEnabled= enabled;
	        updateWidget();
	    }
	    
	    /**
	     * @return Gets the enabled state of all SWT widgets of this Preference.
	     */
	    public final boolean getEnabled() {
	        return fEnabled;
	    }
	    
	    /**
	     * @return Gets the currently used key which is used to store the value.
	     */	    
	    protected final String getKey() {
	        return CodeFormatterConstants.getPreferenceSettingKey(fCategoryID, fPrefID);
	    }
	    
	    /**
	     * Returns the main control of a preference, which is mainly used to 
	     * manage the focus. This may be <code>null</code> if the preference doesn't
	     * have a control which is able to have the focus. 
	     * @return The main control
	     */
	    public abstract Control getControl();
	    
	    /**
	     * To be implemented in subclasses. Update the SWT widgets when the state 
	     * of this object has changed (enabled, key, ...).
	     */
	    protected abstract void updateWidget();
	}
	
	/**
	 * Wrapper around a checkbox and a label. 
	 */	
	protected final class CheckboxPreference extends PreferenceWidget {
		private final Button fCheckbox;
		
		/**
		 * Create a new CheckboxPreference.
		 * @param composite The composite on which the SWT widgets are added.
		 * @param numColumns The number of columns in the composite's GridLayout.
		 * @param preferences The map to store the values.
		 * @param prefID The key to store the values.
		 * @param values An array of two elements indicating the values to store on unchecked/checked.
		 * @param labelText The label text for this Preference.
		 */
		public CheckboxPreference(Composite composite, int numColumns, String categoryID, String prefID, 
								  String labelText, String previewTextPerPreference) {
		    super(categoryID, prefID, previewTextPerPreference);

			fCheckbox= new Button(composite, SWT.CHECK);
			fCheckbox.setText(labelText);
			fCheckbox.setLayoutData(createGridData(numColumns, GridData.FILL_HORIZONTAL, SWT.DEFAULT));
			fCheckbox.setFont(composite.getFont());
			
			updateWidget();

			fCheckbox.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					checkboxChecked(((Button)e.widget).getSelection());
				}
			});
		}
		
		protected void checkboxChecked(boolean state) {
			setCurrentValue(getKey(), String.valueOf(state));
//			if(fPrefPreviewCode != null)
//				fPreview.setPreviewText(fPrefPreviewCode);
			setChanged();
			notifyObservers();
		}
		
		protected void updateWidget() {
			String key = getKey();
			if (key != null) {
				fCheckbox.setEnabled(getEnabled());
				fCheckbox.setSelection(Boolean.parseBoolean(getCurrentValue(key)));
			} else {
				fCheckbox.setSelection(false);
				fCheckbox.setEnabled(false);
			}
		}
				
		public Control getControl() {
			return fCheckbox;
		}
	}
	
	
	/**
	 * Wrapper around a Combo box.
	 */
	protected final class ComboPreference extends PreferenceWidget {
		private Label fLabel;
		private final Combo fCombo;
		
		/**
		 * Create a new ComboPreference.
		 * @param composite The composite on which the SWT widgets are added.
		 * @param numColumns The number of columns in the composite's GridLayout.
		 * @param preferences The map to store the values.
		 * @param prefID The key to store the values.
		 * @param values An array of n elements indicating the values to store for each selection.
		 * @param labelText The label text for this Preference.
		 * @param displayItems An array of n elements indicating the text to be written in the combo box.
		 */
		public ComboPreference(Composite composite, int numColumns,
								  String categoryID, String prefID, 
								  String [] values, String labelText, String [] displayItems, String previewTextPerPreference) {
		    super(categoryID, prefID, previewTextPerPreference);
		    if (values == null || displayItems == null || labelText == null) 
		        throw new IllegalArgumentException(NewWizardMessages.ArgsNotAssigned); 
		    fLabel = createLabel(numColumns - 1, composite, labelText);
			fCombo= new Combo(composite, SWT.SINGLE | SWT.READ_ONLY);
			fCombo.setFont(composite.getFont());
			fCombo.setItems(displayItems);
			
			int max= 0;
			for (int i= 0; i < displayItems.length; i++)
			    if (displayItems[i].length() > max) max= displayItems[i].length();
			
			fCombo.setLayoutData(createGridData(1, GridData.HORIZONTAL_ALIGN_FILL, fCombo.computeSize(SWT.DEFAULT, SWT.DEFAULT).x));			

			updateWidget();

			fCombo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					comboSelected(((Combo)e.widget).getSelectionIndex());
				}
			});
		}
		
		protected void comboSelected(int index) {
			setCurrentValue(getKey(), String.valueOf(index));
//			if(fPrefPreviewCode != null)
//				fPreview.setPreviewText(fPrefPreviewCode);			
			setChanged();
			notifyObservers(new Integer(index));
		}
		
		protected void updateWidget() {
			String key = getKey();
			if (key != null) {
				fCombo.setEnabled(getEnabled());
				int selIndex = Integer.parseInt(getCurrentValue(key));
				fCombo.select(selIndex);
			} else {
				fCombo.setText(""); //$NON-NLS-1$
				fCombo.setEnabled(false);
			}
		}
						
		public Control getControl() {
			return fCombo;
		}
		
		public Label getLabel(){
			return fLabel;
		}
	}
	
	/**
	 * Wrapper around a textfied which requests an integer input of a given range.
	 */
	protected final class NumberPreference extends PreferenceWidget {
		
		private final int fMinValue, fMaxValue;
		private final Label fNumberLabel;
		private final Text fNumberText;

		protected int fSelected;
        protected int fOldSelected;
        
        
		/**
		 * Create a new NumberPreference.
		 * @param composite The composite on which the SWT widgets are added.
		 * @param numColumns The number of columns in the composite's GridLayout.
		 * @param preferences The map to store the values.
		 * @param categoryID the ID of the category
		 * @param prefID The key to store the values.
		 * @param minValue The minimum value which is valid input.
		 * @param maxValue The maximum value which is valid input.
		 * @param labelText The label text for this Preference.
		 */
		public NumberPreference(Composite composite, int numColumns, String categoryID, String prefID, 
							   int minValue, int maxValue, String labelText, String previewTextPerPreference) {
		    super(categoryID, prefID, previewTextPerPreference);
		    
			fNumberLabel= createLabel(numColumns - 1, composite, labelText, GridData.FILL_HORIZONTAL);
			fNumberText= new Text(composite, SWT.SINGLE | SWT.BORDER | SWT.RIGHT);
			fNumberText.setFont(composite.getFont());

			final int length= Integer.toString(maxValue).length() + 3; 
			fNumberText.setLayoutData(createGridData(1, GridData.HORIZONTAL_ALIGN_END, fPixelConverter.convertWidthInCharsToPixels(length)));
			
			fMinValue= minValue;
			fMaxValue= maxValue;
			
			updateWidget();
			
			fNumberText.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) {
				    NumberPreference.this.focusGained();
				}
                public void focusLost(FocusEvent e) {
				    NumberPreference.this.focusLost();
				}
			});
			
			fNumberText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					fieldModified();
				}
			});
		}
		
		private IStatus createErrorStatus() {
		    return new Status(IStatus.ERROR, EDTUIPlugin.getPluginId(), 0, NewWizardMessages.bind(NewWizardMessages.InvalidNumber, new String [] {Integer.toString(fMinValue), Integer.toString(fMaxValue)}), null); 
		    
		}

		protected void focusGained() {
		    fOldSelected= fSelected;
		    fNumberText.setSelection(0, fNumberText.getCharCount());
		}
		
		protected void focusLost() {
		    updateStatus(null);
		    final String input= fNumberText.getText();
		    if (!validInput(input))
		        fSelected= fOldSelected;
		    else
		        fSelected= Integer.parseInt(input);
		    if (fSelected != fOldSelected) {
		    	saveSelected();		    	
		    }
		    fNumberText.setText(Integer.toString(fSelected));
		}
		
		
		protected void fieldModified() {
		    final String trimInput= fNumberText.getText().trim();
		    final boolean valid= validInput(trimInput);
		    
		    updateStatus(valid ? null : createErrorStatus());

		    if (valid) {
		        final int number= Integer.parseInt(trimInput);
		        if (fSelected != number) {
		            fSelected= number;
		            saveSelected();
		        }
		    }
//			if(fPrefPreviewCode != null)
//				fPreview.setPreviewText(fPrefPreviewCode);		    
		}
		
		private boolean validInput(String trimInput) {
		    int number;
		    
		    try {
		        number= Integer.parseInt(trimInput);
		    } catch (NumberFormatException x) {
		        return false;
		    }
		    
		    if (number < fMinValue) return false;
		    if (number > fMaxValue) return false;
		    return true;
		}
		
		private void saveSelected() {
			fOldSelected = fSelected;
			setCurrentValue(getKey(), String.valueOf(fSelected));
			setChanged();
			notifyObservers();
		}
		
		protected void updateWidget() {
			String key = getKey();
		    final boolean hasKey= key != null;

		    fNumberLabel.setEnabled(hasKey && getEnabled());
			fNumberText.setEnabled(hasKey && getEnabled());

			if (hasKey) {
			    String s= getCurrentValue(key);
			    try {
			        fSelected= Integer.parseInt(s);
			    } catch (NumberFormatException e) {
			        final String message= NewWizardMessages.bind(NewWizardMessages.InvalidIntergerValue, getKey()); 
			        EDTUIPlugin.log(new Status(IStatus.ERROR, EDTUIPlugin.getPluginId(), IStatus.OK, message, e));
			        s= ""; //$NON-NLS-1$
			    }
			    fNumberText.setText(s);
			} else {
			    fNumberText.setText(""); //$NON-NLS-1$
			}
		}
		
		public Control getControl() {
			return fNumberText;
		}
	}

	
	/**
	 * This class provides the default way to preserve and re-establish the focus
	 * over multiple modify sessions. Each ModifyDialogTabPage has its own instance,
	 * and it should add all relevant controls upon creation, always in the same sequence.
	 * This established a mapping of controls to indexes, which allows to restore the focus
	 * in a later session. 
	 * The index is saved in the dialog settings, and there is only one common preference for 
	 * all tab pages. It is always the currently active tab page which stores its focus
	 * index. 
	 */
	protected final static class DefaultFocusManager extends FocusAdapter {
		
		private final static String PREF_LAST_FOCUS_INDEX= EDTUIPlugin.PLUGIN_ID + "formatter_page.modify_dialog_tab_page.last_focus_index"; //$NON-NLS-1$ 
		
		private final IDialogSettings fDialogSettings;
		
		private final Map fItemMap;
		private final List fItemList;
		
		private int fIndex;
		
		public DefaultFocusManager() {
			fDialogSettings= EDTUIPlugin.getDefault().getDialogSettings();
			fItemMap= new HashMap();
			fItemList= new ArrayList();
			fIndex= 0;
		}

		public void focusGained(FocusEvent e) {
			fDialogSettings.put(PREF_LAST_FOCUS_INDEX, ((Integer)fItemMap.get(e.widget)).intValue());
		}
		
		public void add(Control control) {
			control.addFocusListener(this);
			fItemList.add(fIndex, control);
			fItemMap.put(control, new Integer(fIndex++));
		}
		
		public void add(PreferenceWidget preferenceWidget) {
			final Control control= preferenceWidget.getControl();
			if (control != null) 
				add(control);
		}
		
		public boolean isUsed() {
			return fIndex != 0;
		}
		
		public void restoreFocus() {
			int index= 0;
			try {
				index= fDialogSettings.getInt(PREF_LAST_FOCUS_INDEX);
				// make sure the value is within the range
				if ((index >= 0) && (index <= fItemList.size() - 1)) {
					((Control)fItemList.get(index)).setFocus();
				}
			} catch (NumberFormatException ex) {
				// this is the first time
			}
		}
		
		public void resetFocus() {
			fDialogSettings.put(PREF_LAST_FOCUS_INDEX, -1);
		}
	}

	//sorted based on the fDisplay value
	//currently used by tree control's model
	protected static class OptionLeafNode implements Comparable{
		Object fParent;
		String fPreferenceKey=""; //$NON-NLS-1$
		String fDisplay=""; //$NON-NLS-1$
		String fPreviewText=""; //$NON-NLS-1$
		
		/**
		 * this is a resolved reference, in other words
		 * if A->B->C
		 * fResolvedRef = C, even though the "ref" attribute on A is B
		 */
		private String fResolvedRef="";  //$NON-NLS-1$
		
		//since we're sorting based on the display name, so this field should be inited at the constructor
		public OptionLeafNode(String display, Object parent){
			fDisplay = display;
			fParent = parent;
		}
		
		//we want the sort to be based on the fDisplay value
		public int compareTo(Object arg0) {
			OptionLeafNode other = (OptionLeafNode)arg0;
			return fDisplay.compareTo(other.fDisplay);
		}
		
		public boolean isPreviewAReference(){
			return (fResolvedRef != null && fResolvedRef.length()>0);
		}
		
		public String getResolvedPreviewRef(){
			return fResolvedRef;
		}
		
		public void setResolvedPreviewRef(String ref){
			fResolvedRef = ref;
		}
	}

	protected static class OptionTreeNode{
		private String fDisplay;		
	
		/**
		 * key is String, the display name
		 * value is OptionTreeNode for non leaf node, or OptionLeafNode(leaf node)
		 */
		private SortedMap fChildren = new TreeMap();
		private OptionTreeNode fParent;
		
		public OptionTreeNode(String display, OptionTreeNode parent){
			fDisplay = display;
			fParent = parent;
		}
		
		public String getDisplay(){
			return fDisplay;
		}
		
		public OptionTreeNode getParent(){
			return fParent;
		}
		
		public Map getChildren(){
			return fChildren;
		}
		
		public boolean hasChildren(){
			return !fChildren.isEmpty();
		}
		
		public OptionTreeNode addChild(String display){
			OptionTreeNode addedChild = null;
			//try to find it, see if it's already a child
			Object obj = fChildren.get(display);
			
			if(obj == null){
				OptionTreeNode newWSNode = new OptionTreeNode(display, this);
				fChildren.put(display, newWSNode);
				addedChild = newWSNode;
			}
			else if(obj instanceof OptionTreeNode)
				addedChild = (OptionTreeNode)obj;
	
			return addedChild;
		}
		
		public OptionLeafNode addChild(String display, String preferenceKey, Preference pref){
			//try to find it, see if it's already a child
			Object obj = fChildren.get(display);
			
			OptionLeafNode optionLeafNode = null;
			if(obj == null){
				optionLeafNode = new OptionLeafNode(display, this);
				fChildren.put(display, optionLeafNode);
			}
			else if(obj instanceof OptionLeafNode)
				optionLeafNode = (OptionLeafNode)obj;
			optionLeafNode.fDisplay = display;
			optionLeafNode.fParent = this;
			optionLeafNode.fPreferenceKey = preferenceKey;
			
			setPreviewForOptionLeafNode(optionLeafNode, pref);						
			return optionLeafNode;
		}
		
		/**
		 * set the preview text for the option leaf node using the passed in pref
		 * this pref has a <preview> sub node, which could have "code" attribute or "ref" attribute
		 * 
		 * @param optionLeafNode
		 * @param pref
		 */
		private void setPreviewForOptionLeafNode(OptionLeafNode optionLeafNode, Preference pref){
			Preview preview = pref.getPreview();
			if(preview != null){
				//check to see if this is a ref
				String previewRef = preview.getRef();
				if(previewRef != null && previewRef.length()>0){
					//try to find the actual preview code by the reference name
					optionLeafNode.setResolvedPreviewRef(previewRef);
					
					//try to get the <DefaultProfile> node by going up the parent chain of pref
					EObject parentNode = pref.eContainer();
					while(parentNode != null && !(parentNode instanceof DefaultProfile))
						parentNode = parentNode.eContainer();
					
					if(parentNode instanceof DefaultProfile){
						DefaultProfile defaultProfile = (DefaultProfile)parentNode;
						//ref is a preference setting key
						String[] ids = CodeFormatterConstants.getCategoryIDnPrefID(previewRef);
						Preference refedPref = FormatProfileRootHelper.getPreferenceByID(defaultProfile, ids[0], ids[1]);
						
						setPreviewForOptionLeafNode(optionLeafNode, refedPref);						
					}
				}
				else{
					String previewCode = preview.getCode();
					if(previewCode != null)
						optionLeafNode.fPreviewText = previewCode;
				}
			}
			
		}
	}

	protected static class OptionTreeContentProvider implements ITreeContentProvider{
	
		public OptionTreeContentProvider(){
		}
	
		public Object[] getChildren(Object parentElement) {
			List children = new ArrayList();
			if(parentElement instanceof Collection){
				children.addAll(((Collection)parentElement));
			}
			else if(parentElement instanceof OptionTreeNode){
				OptionTreeNode wsnode = (OptionTreeNode)parentElement;
				children.addAll(wsnode.getChildren().values());
			}
			return children.toArray() ;
		}
	
		public Object getParent(Object element) {
			if(element instanceof OptionLeafNode)
				return ((OptionLeafNode)element).fParent;
			else if(element instanceof OptionTreeNode)
				return ((OptionTreeNode)element).getParent();
			return null ;
		}
	
		public boolean hasChildren(Object element) {
			return getChildren(element).length>0;
		}
	
		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}
	
		public void dispose() {	}
	
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	protected static class OptionTreeLabelProvider extends LabelProvider{
		public String getText(Object element) {
			if(element instanceof OptionTreeNode)
				return ((OptionTreeNode)element).getDisplay();
			if(element instanceof OptionLeafNode)
				return ((OptionLeafNode)element).fDisplay;
			return super.getText(element) ;
		}
	}

	protected abstract class TreeControlPreference implements ISelectionChangedListener, IDoubleClickListener{
		
		protected Object fLastSelected = null;
		private final List fIndexedNodeList;
		private IDialogSettings fDialogSettings;
		private String fDialogSettingKey;
		protected TreeViewer fTreeViewer;
		
		/**
		 * key is the white space position display name,
		 * value is a sorted map of OptionTreeNode, sort based on the OptionTreeNode's display value
		 *  
		 */
		protected SortedMap fOptionNodePreferenceTreeMap;		
		
		protected TreeControlPreference(IDialogSettings dlgSetting, String dlgSettingKey){
			fDialogSettings = dlgSetting;
			fDialogSettingKey = dlgSettingKey;
			fIndexedNodeList = new ArrayList();
			fOptionNodePreferenceTreeMap = new TreeMap();
		}
		
		protected abstract TreeViewer createTreeViewer(Composite composite, int numColumns);
		
		protected String getPreviewText(Object selectedTreeObj){
			Set previewSet = new HashSet();
			return getNodePreviewText(selectedTreeObj, previewSet);
		}
		
		protected String getNodePreviewText(Object selectedTreeObj, Set set){			
			String delimiter = fPreview.getDefaultLineDelimiter();
			
			if(selectedTreeObj instanceof ModifyDialogTabPage.OptionTreeNode){
				ModifyDialogTabPage.OptionTreeNode selNode = (ModifyDialogTabPage.OptionTreeNode)selectedTreeObj;
				StringBuffer buffer = new StringBuffer();

				for(Iterator it = selNode.getChildren().values().iterator(); it.hasNext();){
					Object val = it.next();
					addPreviewText2Buffer(buffer, delimiter, val, set);
				}
				return buffer.toString();
			}
			else if(selectedTreeObj instanceof ModifyDialogTabPage.OptionLeafNode){				
				OptionLeafNode optionLeafNode = ((ModifyDialogTabPage.OptionLeafNode)selectedTreeObj);
				String prefKey2Check = optionLeafNode.isPreviewAReference() ? optionLeafNode.getResolvedPreviewRef() : optionLeafNode.fPreferenceKey;
				
				//check to see if the prefKey2Check has already been added to the preview set, if so, do not add the duplicate preview text
				if(!set.contains(prefKey2Check)){
					set.add(prefKey2Check);
					return optionLeafNode.fPreviewText;
				}
			}
			return ""; //$NON-NLS-1$
		}		
		
		public void initialize(){
			fTreeViewer.addSelectionChangedListener(this);
			fTreeViewer.addDoubleClickListener(this);
			fTreeViewer.setInput(fOptionNodePreferenceTreeMap.values());
		    restoreSelection();
		}
		
		protected void createContents(final int numColumns, final Composite parent){
			fTreeViewer = createTreeViewer(parent, numColumns);
			fTreeViewer.setContentProvider(new OptionTreeContentProvider());
			fTreeViewer.setLabelProvider(new OptionTreeLabelProvider());
			fTreeViewer.getControl().setLayoutData(createGridData(numColumns, GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL, SWT.DEFAULT));
			fDefaultFocusManager.add(fTreeViewer.getControl());
			makeIndexedNodeList(fOptionNodePreferenceTreeMap);
		}
		
		protected void makeIndexedNodeList(Map map){
			for(Iterator it = map.values().iterator(); it.hasNext();){
				//each value is OptionTreeNode
				Object obj = it.next();				
				fIndexedNodeList.add(obj);
				if(obj instanceof ModifyDialogTabPage.OptionTreeNode){
					ModifyDialogTabPage.OptionTreeNode wsnode = (ModifyDialogTabPage.OptionTreeNode)obj;
					makeIndexedNodeList(wsnode.getChildren());
				}
			}
		}
		
		protected int getNodeIndex(Object node){
			int index = 0;
			for(Iterator it=fIndexedNodeList.iterator(); it.hasNext();){
				if(node.equals(it.next()))
					return index; 
				index++;	
			}
			return -1;
		}		
		
		protected void restoreSelection() {
			int index;
			try {
				index= fDialogSettings.getInt(fDialogSettingKey);
			} catch (NumberFormatException ex) {
				index= -1;
			}
			if (index < 0 || index > fIndexedNodeList.size() - 1) {
				index= 0;
			}
			if(!fIndexedNodeList.isEmpty()){
				final Object node= fIndexedNodeList.get(index);
				if (node != null) {
					fTreeViewer.expandToLevel(node, 0);
					fTreeViewer.setSelection(new StructuredSelection(new Object [] {node}));
				    fLastSelected= node;
				}
			}
		}
		
		public void selectionChanged(SelectionChangedEvent event) {
		    final IStructuredSelection selection= (IStructuredSelection)event.getSelection();
		    if (selection.isEmpty())
		        return;
		    final Object obj = selection.getFirstElement();
		    if(obj == fLastSelected)
		    	return;
		    fDialogSettings.put(fDialogSettingKey, getNodeIndex(obj));
		    fPreview.setPreviewText(getPreviewText(obj));		    
		    doUpdatePreview();
		    fLastSelected= obj;
		}		
		
		public void doubleClick(DoubleClickEvent event) {
            final ISelection selection= event.getSelection();
            if (selection instanceof IStructuredSelection) {
            	final Object obj = ((IStructuredSelection)selection).getFirstElement();
            	fTreeViewer.setExpandedState(obj, !fTreeViewer.getExpandedState(obj));
            }			
		}
		
		
		//helper method
		protected void addPreviewText2Buffer(StringBuffer buffer, String delimiter, Object obj, Set set){
			String nodePreviewText = getNodePreviewText(obj, set);
			if(nodePreviewText.trim().length()>0){
				buffer.append(delimiter);
				buffer.append(nodePreviewText);
				buffer.append(delimiter);							
			}
		}		
		
		protected void populatePreferenceMapData(Preference pref, EAttribute preferenceAttribute){
			Object attribVal = pref.eGet(preferenceAttribute);
			if(attribVal != null){
				String display = (String)attribVal;
				display = FormatProfileRootHelper.getFormattingProfileNLSString(display, CodeFormatterConstants.DISPLAY_TREE_DELIMITER);
				String[] displays = FormatProfileRootHelper.parseTokenizedString(display, CodeFormatterConstants.DISPLAY_TREE_DELIMITER);
				
				String prefKey = CodeFormatterConstants.getPreferenceSettingKey(fCategory.getId(), pref.getId());
				
				String key = displays[0];
				//key is string, value is OptionTreeNode
				Object obj = fOptionNodePreferenceTreeMap.get(key);
				ModifyDialogTabPage.OptionTreeNode wsnode = null;
				if(obj == null){
					wsnode = new ModifyDialogTabPage.OptionTreeNode(key, null);				
					fOptionNodePreferenceTreeMap.put(key, wsnode);
				}
				else
					wsnode = (ModifyDialogTabPage.OptionTreeNode)obj;
				
				for(int i=1; i<displays.length; i++){
					key = displays[i];
					if(i!=displays.length-1){
						wsnode = wsnode.addChild(key);					
					}
					else{ //last element, which is a leaf child 
						wsnode.addChild(key, prefKey, pref);					
					}
				}		
			}
		}
				
	}
	
	/**
	 * The default focus manager. This widget knows all widgets which can have the focus
	 * and listens for focusGained events, on which it stores the index of the current
	 * focus holder. When the dialog is restarted, <code>restoreFocus()</code> sets the 
	 * focus to the last control which had it.
	 * 
	 * The standard Preference object are managed by this focus manager if they are created
	 * using the respective factory methods. Other SWT widgets can be added in subclasses 
	 * when they are created.
	 */
	protected final DefaultFocusManager fDefaultFocusManager;
	
	/**
	 * A pixel converter for layout calculations
	 */
	protected PixelConverter fPixelConverter;	
	
	/**
	 * The modify dialog where we can display status messages.
	 */
	private final ModifyDialog fModifyDialog;

    /**
     * key is a string in the format: "categoryID.prefID"
     * value is initValue in the profile and currentValue from UI 
     * 
     * this map holds ALL the preference setting values from a profile(or DefaultProfile)
     * this is passed in as an input parameter to the each of the tab page 
     * to set initial value of the control and record changes of current value from the UI control
     */
	private Map fAllPreferenceSettings;
		

	protected EGLPreview fPreview ;
	private DefaultProfile fDefaultProfile;
	protected Category fCategory;


	/*
	 * Create a new <code>ModifyDialogTabPage</code>
	 */
	public ModifyDialogTabPage(ModifyDialog modifyDialog, DefaultProfile defaultProfile, Category category, Map allPreferenceSettings) {
		fModifyDialog= modifyDialog;
		fDefaultProfile = defaultProfile;
		fCategory = category;
		fAllPreferenceSettings = allPreferenceSettings;
		fDefaultFocusManager= new DefaultFocusManager();
	}
	
	/**
	 * Create the contents of this tab page. Subclasses cannot override this, 
	 * instead they must implement <code>doCreatePreferences</code>. <code>doCreatePreview</code> may also
	 * be overridden as necessary.
	 * @param parent The parent composite
	 * @return Created content control
	 */
	public final Composite createContents(Composite parent) {
		final int numColumns= 4;
		
		if (fPixelConverter == null) {
		    fPixelConverter= new PixelConverter(parent);
		}
		
		final SashForm fSashForm = new SashForm(parent, SWT.HORIZONTAL);
		fSashForm.setFont(parent.getFont());
		
		final Composite settingsPane= new Composite(fSashForm, SWT.NONE);
		settingsPane.setFont(fSashForm.getFont());
		
		final GridLayout layout= new GridLayout(numColumns, false);
		layout.verticalSpacing= (int)(1.5 * fPixelConverter.convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING));
		layout.horizontalSpacing= fPixelConverter.convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.marginHeight= fPixelConverter.convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth= fPixelConverter.convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		settingsPane.setLayout(layout);
		doCreatePreferences(settingsPane, numColumns);

		final Composite previewPane= new Composite(fSashForm, SWT.NONE);
		previewPane.setLayout(createGridLayout(numColumns, true));
		previewPane.setFont(fSashForm.getFont());
		doCreatePreviewPane(previewPane, numColumns);

		initializePage();
	
		fSashForm.setWeights(new int [] {3, 3});
		return fSashForm;
	}
	
	/**
	 * This method is called after all controls have been alloated, including the preview. 
	 * It can be used to set the preview text and to create listeners.
	 *
	 */
	protected void initializePage(){
		String previewCode = ""; //$NON-NLS-1$
		Preview preview = fCategory.getPreview();
		if(preview != null){
			previewCode = preview.getCode();
		}
			
		fPreview.setPreviewText(previewCode);
	}
	
	public void dispose(){
		if(fPreview != null){
			fPreview.dispose();
			fPreview = null;
		}
	}

	/**
	 * Create the left side of the modify dialog. This is meant to be implemented by subclasses. 
	 * @param composite Composite to create in
	 * @param numColumns Number of columns to use
	 */
	protected void doCreatePreferences(Composite composite, int numColumns){
		EList groups = fCategory.getGroup();
		String categoryID = fCategory.getId();
		for(Iterator it=groups.iterator(); it.hasNext();){
			org.eclipse.edt.ide.ui.internal.formatting.profile.Group group = (org.eclipse.edt.ide.ui.internal.formatting.profile.Group)it.next();
			String grpTitle = FormatProfileRootHelper.getFormattingProfileNLSString(group.getDisplay());
			Composite grpComposite = composite;
			if(grpTitle != null && grpTitle.length()>0){
				grpComposite = createGroup(numColumns, composite, grpTitle);				
			}
			
			EList prefs = group.getPref();
			for(Iterator pIt=prefs.iterator(); pIt.hasNext();){
				org.eclipse.edt.ide.ui.internal.formatting.profile.Preference pref = (org.eclipse.edt.ide.ui.internal.formatting.profile.Preference)pIt.next();
				org.eclipse.edt.ide.ui.internal.formatting.profile.Control control = pref.getControl();
				org.eclipse.edt.ide.ui.internal.formatting.profile.Control controlInstance = control;
				if (control instanceof ReferenceControl) {
					ReferenceControl refControl = (ReferenceControl)control;
					controlInstance = FormatProfileRootHelper.getReferencedControl(fDefaultProfile, refControl);					
				}

				String labelText = FormatProfileRootHelper.getFormattingProfileNLSString(pref.getDisplay(), CodeFormatterConstants.DISPLAY_TREE_DELIMITER);
				String prefID = pref.getId();
				Preview preview = pref.getPreview();
				String previewTextPerPreference = (preview != null) ? preview.getCode() : null;
				
				boolean isVisible = pref.isVisible();				
				if(isVisible){
					//create the actual ui - swt wiget control
					if(controlInstance instanceof TextControl){
						createNumberPref(grpComposite, numColumns, labelText, categoryID, prefID, 0, 255, previewTextPerPreference);
					}
					else if(controlInstance instanceof ComboControl){
						ComboControl comboControl = (ComboControl)controlInstance;
						String[] displayItems = FormatProfileRootHelper.parseChoices(
								FormatProfileRootHelper.getFormattingProfileNLSString(comboControl.getChoices(), FormatProfileRootHelper.DELIMITER_COMMA));
						createComboPref(grpComposite, numColumns, labelText, categoryID, prefID, displayItems, displayItems, previewTextPerPreference);
					}
					else if(controlInstance instanceof CheckControl){					
						createCheckboxPref(grpComposite, numColumns, labelText, categoryID, prefID, previewTextPerPreference);
					}
					else if(controlInstance instanceof RadioControl){
						
					}
					else if(controlInstance instanceof TreeControl){
						createTreePref(pref);
					}
				}
			}
		}
	}
	
	protected String getCurrentValue(String key){
		Object valObj = fAllPreferenceSettings.get(key);
		if(valObj != null)
			return ((PreferenceSettingValue)valObj).getCurrentValue();
		return ""; //$NON-NLS-1$
	}
	
	protected void setCurrentValue(String key, String currValue){
		Object obj = fAllPreferenceSettings.get(key);
		if(obj != null){
			ProfileManager.PreferenceSettingValue settingValue = (ProfileManager.PreferenceSettingValue)obj;
			settingValue.setCurrValue(currValue);
		}
	}

	/**
	 * Create the right side of the modify dialog. By default, the preview is displayed there.
	 * Subclasses can override this method in order to customize the right-hand side of the 
	 * dialog.
	 * @param composite Composite to create in
	 * @param numColumns Number of columns to use
	 * @return Created composite
	 */
	protected Composite doCreatePreviewPane(Composite composite, int numColumns) {
		
		createLabel(numColumns, composite, NewWizardMessages.Preview);  
		
		final EGLPreview preview= doCreateEGLPreview(composite);
		fDefaultFocusManager.add(preview.getControl());
		
		final GridData gd= createGridData(numColumns, GridData.FILL_BOTH, 0);
		gd.widthHint= 0;
		gd.heightHint=0;
		preview.getControl().setLayoutData(gd);
		
		return composite;
	}


	/**
	 * To be implemented by subclasses. This method should return an instance of JavaPreview.
	 * Currently, the choice is between CompilationUnitPreview which contains a valid compilation
	 * unit, or a SnippetPreview which formats several independent code snippets and displays them 
	 * in the same window.
	 * @param parent Parent composite
	 * @return Created preview
	 */
	protected EGLPreview doCreateEGLPreview(Composite parent){
		fPreview = new EGLPreview(parent, fAllPreferenceSettings);
		return fPreview;
	}

	
	/**
	 * This is called when the page becomes visible. 
	 * Common tasks to do include:
	 * <ul><li>Updating the preview.</li>
	 * <li>Setting the focus</li>
	 * </ul>
	 */
	final public void makeVisible() {
		fDefaultFocusManager.resetFocus();
		doUpdatePreview();
	}
	
	/**
	 * Update the preview. To be implemented by subclasses.
	 */
	protected void doUpdatePreview(){
		fPreview.update();
	}
	
	protected void notifyValuesModified() {
		fModifyDialog.valuesModified();
	}
    /**
     * Each tab page should remember where its last focus was, and reset it
     * correctly within this method. This method is only called after
     * initialization on the first tab page to be displayed in order to restore
     * the focus of the last session.
     */
    public void setInitialFocus() {
		if (fDefaultFocusManager.isUsed()) {
			fDefaultFocusManager.restoreFocus();
		}
	}
	

    /**
     * Set the status field on the dialog. This can be used by tab pages to report 
     * inconsistent input. The OK button is disabled if the kind is IStatus.ERROR. 
     * @param status Status describing the current page error state
     */
	protected void updateStatus(IStatus status) {
	    fModifyDialog.updateStatus(status);
	}
	
	/*
	 * Factory methods to make GUI construction easier
	 */
	
	/*
	 * Create a GridLayout with the default margin and spacing settings, as
	 * well as the specified number of columns.
	 */
	protected GridLayout createGridLayout(int numColumns, boolean margins) {
		final GridLayout layout= new GridLayout(numColumns, false);
		layout.verticalSpacing= fPixelConverter.convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing= fPixelConverter.convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		if (margins) {
			layout.marginHeight= fPixelConverter.convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
			layout.marginWidth= fPixelConverter.convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		} else {
			layout.marginHeight= 0;
			layout.marginWidth= 0;
		}
		return layout;
	}

	/*
	 * Convenience method to create a GridData.
	 */
	protected static GridData createGridData(int numColumns, int style, int widthHint) {
		final GridData gd= new GridData(style);
		gd.horizontalSpan= numColumns;
		gd.widthHint= widthHint;
		return gd;		
	}


	/* 
	 * Convenience method to create a label.  
	 */
	protected static Label createLabel(int numColumns, Composite parent, String text) {
		return createLabel(numColumns, parent, text, GridData.FILL_HORIZONTAL);
	}
	
	/*
	 * Convenience method to create a label
	 */
	protected static Label createLabel(int numColumns, Composite parent, String text, int gridDataStyle) {
		final Label label= new Label(parent, SWT.WRAP);
		label.setFont(parent.getFont());
		label.setText(text);
		label.setLayoutData(createGridData(numColumns, gridDataStyle, SWT.DEFAULT));
		return label;
	}

	/*
	 * Convenience method to create a group
	 */
	protected Group createGroup(int numColumns, Composite parent, String text ) {
		final Group group= new Group(parent, SWT.NONE);
		group.setFont(parent.getFont());
		group.setLayoutData(createGridData(numColumns, GridData.FILL_HORIZONTAL, SWT.DEFAULT));
		
		final GridLayout layout= new GridLayout(numColumns, false);
		layout.verticalSpacing=  fPixelConverter.convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing= fPixelConverter.convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.marginHeight=  fPixelConverter.convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);

		//layout.marginHeight= fPixelConverter.convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		//layout.marginWidth= fPixelConverter.convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		
		group.setLayout(layout);//createGridLayout(numColumns, true));
		group.setText(text);
		return group;
	}
	

	/*
	 * Convenience method to create a NumberPreference. The widget is registered as 
	 * a potential focus holder, and the default updater is added.
	 */
	protected NumberPreference createNumberPref(Composite composite, int numColumns, String labelText, String categoryID, String prefID,
												int minValue, int maxValue, String previewTextPerPreference) {
		final NumberPreference pref= new NumberPreference(composite, numColumns, categoryID, prefID, minValue, maxValue, labelText, previewTextPerPreference);
		fDefaultFocusManager.add(pref);
		pref.addObserver(fUpdater);
		return pref;
	}
	
	/*
	 * Convenience method to create a ComboPreference. The widget is registered as 
	 * a potential focus holder, and the default updater is added.
	 */
	protected ComboPreference createComboPref(Composite composite, int numColumns, String labelText, 
			String categoryID, String prefID, String [] values, String [] items, String previewTextPerPreference) {
		final ComboPreference pref= new ComboPreference(composite, numColumns, categoryID, prefID, values, labelText, items, previewTextPerPreference);
		fDefaultFocusManager.add(pref);
		pref.addObserver(fUpdater);
		return pref;
	}

	/*
	 * Convenience method to create a CheckboxPreference. The widget is registered as 
	 * a potential focus holder, and the default updater is added.
	 */
	protected CheckboxPreference createCheckboxPref(Composite composite, int numColumns, String labelText, String categoryID, String prefID, String previewTextPerPreference) {
		final CheckboxPreference pref= new CheckboxPreference(composite, numColumns, categoryID, prefID, labelText, previewTextPerPreference);
		fDefaultFocusManager.add(pref);
		pref.addObserver(fUpdater);
		return pref;
	}
	
	protected void createTreePref(Preference pref){}
}






















