/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.preferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.edt.compiler.IGenerator;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.EDTCorePreferenceConstants;
import org.eclipse.edt.ide.core.IIDECompiler;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.util.PixelConverter;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.PageBook;

public class CompilerAndGeneratorControls {
	/**
	 * Wrapper around a Combo box.
	 */
	protected class ComboPreference {
		private Label fLabel;
		private Combo fCombo;
		
		/**
		 * Create a new ComboPreference.
		 * @param composite The composite on which the SWT widgets are added.
		 * @param numColumns The number of columns in the composite's GridLayout.
		 * @param labelText The label text for this Preference.
		 * @param displayItems An array of n elements indicating the text to be written in the combo box.
		 */
		public ComboPreference(Composite composite, 
									int numColumns,
									String labelText, 
									String [] displayItems) {
			
		    if( displayItems == null || labelText == null ) {
		        throw new IllegalArgumentException(NewWizardMessages.ArgsNotAssigned);
		    }
		    fLabel = createLabel(numColumns, composite, labelText);
			fCombo= new Combo(composite, SWT.SINGLE | SWT.READ_ONLY);
			fCombo.setFont(composite.getFont());
			fCombo.setItems(displayItems);
			fCombo.setLayoutData(createGridData(1, GridData.HORIZONTAL_ALIGN_FILL, fCombo.computeSize(SWT.DEFAULT, SWT.DEFAULT).x));			
			updateWidget();
		}
		
		protected void updateWidget() {
			int selIndex = fCombo.indexOf( parentPage.getSelectedCompiler() );
			if( selIndex < 0 ) {
				selIndex = 0;
			}
			fCombo.select(selIndex);	
		}
						
		public Control getControl() {
			return fCombo;
		}
		
		public Label getLabel(){
			return fLabel;
		}
	}

	protected static class OptionTreeContentProvider implements ITreeContentProvider{
	
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
			return ((OptionTreeNode)element).getParent();
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

	protected static class OptionTreeLabelProvider extends LabelProvider implements ITableLabelProvider{
		public String getColumnText(Object element, int columnIndex) {
			switch( columnIndex ) {
			case 0: 
				return ((OptionTreeNode)element).getDisplay();
			case 1:
				return ((OptionTreeNode)element).getLanguage();
			case 2:
				return ((OptionTreeNode)element).getVersion();
			case 3:
				return ((OptionTreeNode)element).getProvider();
			}
			return "";
		}
		 public Image getColumnImage(Object element, int columnIndex) {
			 return null;
		 }		 
	}

	protected abstract class TreeControlPreference {		
		
		protected TreeViewer fTreeViewer;		
		protected IIDECompiler fCompiler;		
		/**
		 * key is the white space position display name,
		 * value is a sorted map of OptionTreeNode, sort based on the OptionTreeNode's display value
		 *  
		 */
		protected SortedMap<String, OptionTreeNode> fOptionNodePreferenceTreeMap;		
		
		protected TreeControlPreference(IIDECompiler compiler){			
			fCompiler = compiler;
			fOptionNodePreferenceTreeMap = new TreeMap<String, OptionTreeNode>();
		}
		
		protected abstract TreeViewer createTreeViewer(Composite composite, int numColumns);
		
		public void initialize(){
			fTreeViewer.setInput(fOptionNodePreferenceTreeMap.values());
			fTreeViewer.expandAll();
		}
		
		protected void createContents( int numColumns, Composite parent){
			fTreeViewer = createTreeViewer(parent, numColumns);
			fTreeViewer.setContentProvider(new OptionTreeContentProvider());
			fTreeViewer.setLabelProvider(new OptionTreeLabelProvider());
			fTreeViewer.getControl().setLayoutData(createGridData(numColumns, GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL, SWT.DEFAULT));
			Tree tree = fTreeViewer.getTree();
			tree.setHeaderVisible(true);
			tree.setLinesVisible(true);
			TreeColumn nameColumn = new TreeColumn(tree, SWT.LEFT);
			TreeColumn languageColumn = new TreeColumn(tree, SWT.LEFT);
			TreeColumn versionColumn = new TreeColumn(tree, SWT.LEFT); 
			TreeColumn providerColumn = new TreeColumn(tree, SWT.LEFT); 
			nameColumn.setWidth(200);
			nameColumn.setText( UINlsStrings.CompilerPreferencePage_nameLabel );
			languageColumn.setWidth(150);
			languageColumn.setText( UINlsStrings.CompilerPreferencePage_languageLabel );
			versionColumn.setWidth(100);
			versionColumn.setText( UINlsStrings.CompilerPreferencePage_versionLabel );
			providerColumn.setWidth(200);
			providerColumn.setText( UINlsStrings.CompilerPreferencePage_ProviderLabel );
		}
		
		protected void populatePreferenceMapData(Map<IGenerator, List<IGenerator>> genMap){
			for ( IGenerator genMapKey : genMap.keySet() ) {
				if( genMapKey.getParentGeneratorId() == null ||
						genMapKey.getParentGeneratorId().length() == 0 ) {
					List<IGenerator> genList = (List<IGenerator>)genMap.get( genMapKey );

					Object obj = fOptionNodePreferenceTreeMap.get( genMapKey.getName() );
					OptionTreeNode wsnode = null;
					if(obj == null){
						wsnode = new OptionTreeNode( genMapKey.getName(), 
														null, 
														genMapKey.getVersion(),
														genMapKey.getProvider(),
														genMapKey.getLanguage());				
						fOptionNodePreferenceTreeMap.put( genMapKey.getName(), wsnode );
					}
					else {
						wsnode = (OptionTreeNode)obj;
					}
					for( int i = 0; i < genList.size(); i++ ) {
						processChild( genMap, wsnode, genList.get(i) );
					}		
				}
			}
		}
		
		protected void processChild(Map<IGenerator, List<IGenerator>> genMap, 
									OptionTreeNode wsnode, IGenerator child ) {
			if( genMap.keySet().contains( child ) ) {
				wsnode = wsnode.addChild(child.getName(), 
											child.getVersion(),
											child.getProvider(),
											child.getLanguage());
				List<IGenerator> genList = (List<IGenerator>)genMap.get( child );
				for( int i = 0; i < genList.size(); i++ ) {
					processChild( genMap, wsnode, genList.get(i) );
				}
			} else {
				wsnode.addChild(child.getName(), child.getVersion(), child.getProvider(), child.getLanguage());
			}			
		}
	}
		
	protected class WSOptionNodeComponent extends TreeControlPreference implements ICheckStateListener{			
		private Composite fComposite;
				
		public WSOptionNodeComponent(IIDECompiler compiler){
			super(compiler);
		}
		
		public void createContents(  int numColumns, Composite parent){
			fComposite= new Composite(parent, SWT.NONE);
			fComposite.setLayoutData(createGridData(numColumns, GridData.HORIZONTAL_ALIGN_FILL, SWT.DEFAULT));
			fComposite.setLayout(createGridLayout(numColumns, false));
			
			super.createContents(numColumns, fComposite);
		}
		
		protected TreeViewer createTreeViewer(Composite composite, int numColumns){
			return new CheckboxTreeViewer(fComposite, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);	
		}		
		
		public void initialize(){
			((CheckboxTreeViewer)fTreeViewer).addCheckStateListener(this);
			super.initialize();
		    refreshState();			
		}
		
		public void refreshState(){	
			CheckboxTreeViewer checkedTreeViewer = (CheckboxTreeViewer)fTreeViewer;
			if( checkedTreeViewer != null ) {
				List<OptionTreeNode> checked = getCheckElements(fOptionNodePreferenceTreeMap);
				checkedTreeViewer.setCheckedElements(checked.toArray());
			}
		}
				
		private List<OptionTreeNode> getCheckElements( Map<String, OptionTreeNode> map ) {
			ArrayList<OptionTreeNode> checked= new ArrayList<OptionTreeNode>(100);		
		    for (Iterator<OptionTreeNode> it=map.values().iterator(); it.hasNext();){
		    	Object val = it.next();
	    		OptionTreeNode wsnode = (OptionTreeNode)val;
	    		checked.addAll(getCheckElements(wsnode.getChildren()));
	    		if( isSelectedGenerator( wsnode.getDisplay() ) )
	    			checked.add(wsnode);
		    }
		    return checked;
		}
		
		public void checkStateChanged(CheckStateChangedEvent event) {
			Object obj = event.getElement();
			String currValue = Boolean.toString(event.getChecked());
			changeCheckState(obj, currValue);
		}
		
		private void changeCheckState(Object obj, String currStateValue){
			if(obj instanceof OptionTreeNode){
				OptionTreeNode wsnode = (OptionTreeNode)obj;
				if( currStateValue.equalsIgnoreCase( "true") ) {
					if( parentPage.getResource() == null ) {
						IGenerator gen = parentPage.getGeneratorFromId( parentPage.convertGeneratorNameToId( wsnode.getDisplay() ) );
						if( gen.getCompiler().getId().equalsIgnoreCase( parentPage.getPreferencePageCompilerId() ) ) {
							parentPage.addSelectedGenerator( wsnode.getDisplay() );
							parentPage.addGeneratorTab( wsnode.getDisplay() ); 
						}
					} else {
						parentPage.addSelectedGenerator( wsnode.getDisplay() );
						parentPage.addGeneratorTab( wsnode.getDisplay() ); 
					}
				} else {
					parentPage.removeSelectedGenerator( wsnode.getDisplay() );
					parentPage.removeGeneratorTab( wsnode.getDisplay() ); //%
				}
			}
		}
		
		public Control getControl(){
			return fComposite;
		}				
	}

	protected static class OptionTreeNode{
		private String fDisplay;
		private String fVersion;
		private String fProvider;
		private String fLanguage;
	
		/**
		 * key is String, the display name
		 * value is OptionTreeNode 
		 */
		private SortedMap<String, OptionTreeNode> fChildren = new TreeMap<String, OptionTreeNode>();
		private OptionTreeNode fParent;
		
		public OptionTreeNode(String display, 
								OptionTreeNode parent,
								String version,
								String provider,
								String language){
			fDisplay = display;
			fParent = parent;
			fVersion = version;
			fProvider = provider;
			fLanguage = language;
		}
		
		public String getDisplay(){
			return fDisplay;
		}
		
		public OptionTreeNode getParent(){
			return fParent;
		}
		
		public String getVersion(){
			return fVersion;
		}
		
		public String getProvider(){
			return fProvider;
		}
		
		public String getLanguage(){
			return fLanguage;
		}
		
		public Map<String, OptionTreeNode> getChildren(){
			return fChildren;
		}
		
		public boolean hasChildren(){
			return !fChildren.isEmpty();
		}
		
		public OptionTreeNode addChild(String display, String version, String provider, String language){
			OptionTreeNode addedChild = null;
			//try to find it, see if it's already a child
			Object obj = fChildren.get(display);
			
			if(obj == null){
				OptionTreeNode newWSNode = new OptionTreeNode(display, this, version, provider, language);
				fChildren.put(display, newWSNode);
				addedChild = newWSNode;
			}
			else if(obj instanceof OptionTreeNode)
				addedChild = (OptionTreeNode)obj;
	
			return addedChild;
		}
		
	}
	
	protected class CompilerComponent extends SelectionAdapter {
	    private Combo fCompilerCombo; 
	    private PageBook fPageBook;
	
	    private List<WSOptionNodeComponent> compilerNodeComponents = new ArrayList<WSOptionNodeComponent>();
		// genMap key is parent generator id, value is a list of child generators
		protected Map<IGenerator, List<IGenerator>> genMap = new HashMap<IGenerator, List<IGenerator>>();
  
	    public CompilerComponent(){
	    	IIDECompiler[] compilers = getCompilersToDisplay();
	    	WSOptionNodeComponent node;
	    	// Add a node for each compiler
	    	for( IIDECompiler compiler : compilers ) {
	    		node = new WSOptionNodeComponent( compiler );
	    		compilerNodeComponents.add( node );
	    	}
	    } 
	    
        public void widgetSelected(SelectionEvent e) {
            int index= fCompilerCombo.getSelectionIndex();
            String compilerName = fCompilerCombo.getItem( index );
            if( !compilerName.equalsIgnoreCase( parentPage.getSelectedCompiler() ) ) {
            	// Compiler changed
            	parentPage.updateGeneratorControlEnablement( true );
            	parentPage.setSelectedCompiler( compilerName );

            	// For project, only save generators corresponding to the selected compiler
            	if( parentPage.getResource() instanceof IProject ) {
            		Control control  = compilerNodeComponents.get(index).getControl();
            		if( control != null ) {
            			// Update generator selection
            			compilerNodeComponents.get(index).refreshState();
            			fPageBook.showPage( control );
 
            			// Update tabs
            			parentPage.removeCurrentCompilerTabs();
            			parentPage.addCompilerTabs( compilerName );
            		}
            	}
            	
            	parentPage.setGeneratorOverrideCheckboxState( false );
            }
         }
        
        public void createPageBookContents( int numColumns, Composite parent ) {
        	if( parentPage.getResource() == null ) {
        		createLabel( 1, parent, UINlsStrings.CompilerPreferencePage_defaultGeneratorsLabel );
        	} else {
        		createLabel( 1, parent, UINlsStrings.CompilerPreferencePage_selectedGeneratorsLabel );
        	}
            fPageBook= new PageBook( parent, SWT.NONE );
            fPageBook.setLayoutData( createGridData( numColumns, GridData.FILL_BOTH, SWT.DEFAULT ) );
            
            for( WSOptionNodeComponent node : compilerNodeComponents ) {
            	node.createContents(numColumns, fPageBook);
            }
        }
        
        public void initialize() {
        	if( parentPage.getResource() == null ) {
        		showCompilerPreferencePageBookPage();
        	} else {
        		showCompilerPropertyPageBookPage( parentPage.getSelectedCompiler() );
        	}
         	for( WSOptionNodeComponent node : compilerNodeComponents ) {	
             	node.initialize();
            }         	
        }
        
        public void showCompilerPreferencePageBookPage() {
        	String id = parentPage.getPreferencePageCompilerId();
        	for( WSOptionNodeComponent node : compilerNodeComponents ) {
        		if( node.fCompiler.getId().equalsIgnoreCase( id ) ) {
        			node.refreshState();
        			fPageBook.showPage(node.getControl());
        		}
        	}
         }
        
         protected void showCompilerPropertyPageBookPage( String compilerName ) {
        	int selectWSPos = fCompilerCombo.indexOf( compilerName );
        	fCompilerCombo.select( selectWSPos );
        	compilerNodeComponents.get(selectWSPos).refreshState();
        	fPageBook.showPage(compilerNodeComponents.get(selectWSPos).getControl());  
         }    
         
         protected void selectCompiler( String compilerName ) {
         	int selectWSPos = fCompilerCombo.indexOf( compilerName );
         	fCompilerCombo.select( selectWSPos );
         }
        
        public void populatePreferenceMapData(){
        	for( WSOptionNodeComponent node : compilerNodeComponents ) {	
        		createParentGeneratorMap( node );
       			node.populatePreferenceMapData( genMap );
       			genMap.clear();
             }  
        }
        
    	private void createParentGeneratorMap( WSOptionNodeComponent node ) {
    		List<IGenerator> availableGens = node.fCompiler.getGenerators();
   			for( IGenerator gen : availableGens ) {
   				if( (gen.getParentGeneratorId() == null || gen.getParentGeneratorId().length() == 0) // root generator
   						&& (gen.getEnabledWith() == null || gen.getEnabledWith().length() == 0)) { // not implicitly enabled
   					addGeneratorToMap( gen );
   				}
     		}
    	}
    	
    	private void addGeneratorToMap( IGenerator gen ) {
    		List<IGenerator> children = getChildren( gen );
     		genMap.put( gen, children );
    		for( IGenerator child : children ) {
    			if( getChildren( child ).size() > 0 ) {
    				addGeneratorToMap( child );
    			}
    		}
      	}    		

    	protected IIDECompiler getCompilerFromName( String name ) {
    		if( name != null && name.length() > 0 ) {
    			IIDECompiler[] compilers = EDTCoreIDEPlugin.getPlugin().getCompilers();
    			for( IIDECompiler currentCompiler : compilers ) {
    				if( currentCompiler.getName().equalsIgnoreCase( name ) )  {
    					return currentCompiler;
    				}
    			}
    		}
    		return null;
    	}
    	
    	protected List<WSOptionNodeComponent> getCompilerNodeComponents() {
    		return compilerNodeComponents;
    	}
	}	
	
	protected CompilerPropertyAndPreferencePage parentPage;
	private CompilerComponent compilerComponent;
	
	/**
	 * A pixel converter for layout calculations
	 */
	protected PixelConverter fPixelConverter;
	
	public CompilerAndGeneratorControls( CompilerPropertyAndPreferencePage parentPage ) {
		this.parentPage = parentPage;
		compilerComponent = new CompilerComponent();
	}

	public void createCompilersComposite( Composite parent ) {
		IIDECompiler[] compilers = getCompilersToDisplay();
		String[] displayItems = new String[compilers.length];
		for( int i = 0; i < compilers.length; i++ ) {
			displayItems[i] = compilers[i].getName();
		}
		String compilerLabel;
		if( parentPage.getResource() == null ) {
			compilerLabel = UINlsStrings.CompilerPreferencePage_defaultCompilerLabel;
		} else {
			compilerLabel = UINlsStrings.CompilerPreferencePage_selectedCompilerLabel;
		}
		ComboPreference comboPref= new ComboPreference(parent, 2, compilerLabel, displayItems );
		compilerComponent.fCompilerCombo = (Combo)comboPref.getControl();
		compilerComponent.fCompilerCombo.addSelectionListener( compilerComponent );
		
		if (this.parentPage.getResource() != null && this.parentPage.getResource().getType() != IResource.PROJECT) {
			compilerComponent.fCompilerCombo.setEnabled( false );
		}
	}
	
	/**
	 * Return an array of compilers to display on the page.  For
	 * workspace preferences and projects, display all compilers.
	 * For folders and files, display the project's selected compiler.
	 * 
	 * @return ICompiler[]
	 */
	protected IIDECompiler[] getCompilersToDisplay() {
		IIDECompiler[] availableCompilers = EDTCoreIDEPlugin.getPlugin().getCompilers();
		if( (parentPage.getResource() == null) || (parentPage.getResource() instanceof IProject) ) {
			return availableCompilers;
		} else {
			// Only return 1 compiler
			IIDECompiler comp = ProjectSettingsUtility.getCompiler( parentPage.getResource().getProject() );
			if( comp == null ) {
				String wsComp = EDTCoreIDEPlugin.getPlugin().getPreferenceStore().getString( EDTCorePreferenceConstants.COMPILER_ID );
				for( IIDECompiler currCompiler : availableCompilers ) { 
					if( currCompiler.getId().equalsIgnoreCase( wsComp ) ) {
						comp = currCompiler;
						break;
					}
				} 
			}
			return new IIDECompiler[] {comp };
		}			
	}
	
	public void createGeneratorsComposite( Composite parent ) {
		if (fPixelConverter == null) {
			fPixelConverter = new PixelConverter(parent);
		}
		Composite settingsPane = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = (int)(1.5 * fPixelConverter.convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING));
		layout.horizontalSpacing = fPixelConverter.convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.marginHeight = 0;
		layout.marginWidth = fPixelConverter.convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		settingsPane.setLayout(layout);
		settingsPane.setLayoutData(new GridData(GridData.FILL_BOTH));

		createPageBookAndTree( settingsPane );
		initializePage();
	}
	
	/**
	 * Create the left side of the modify dialog. This is meant to be implemented by subclasses. 
	 * @param composite Composite to create in
	 * @param numColumns Number of columns to use
	 */
	protected void createPageBookAndTree(Composite composite){
		compilerComponent.createPageBookContents( 2, composite );
		createTreePref();
	}

	protected void createTreePref(){
		compilerComponent.populatePreferenceMapData();
	}
	
	protected void initializePage() {
		compilerComponent.initialize();
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
		Label label= new Label(parent, SWT.WRAP);
		label.setFont(parent.getFont());
		label.setText(text);
		label.setLayoutData(createGridData(numColumns, gridDataStyle, SWT.DEFAULT));
		return label;
	}

	/*
	 * Create a GridLayout with the default margin and spacing settings, as
	 * well as the specified number of columns.
	 */
	protected GridLayout createGridLayout(int numColumns, boolean margins) {
		GridLayout layout= new GridLayout(numColumns, false);
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
		GridData gd= new GridData(style);
		gd.horizontalSpan= numColumns;
		gd.widthHint= widthHint;
		return gd;		
	}
	
	/**
	 * Return if genName is a selected generator for the resource
	 * 
	 * @param genId
	 * @return
	 */
	protected boolean isSelectedGenerator( String genName ) {		
		if( parentPage.getSelectedGenerators().contains( genName ) ) {
			return true;
		}
		return false;
	}
	
	protected List<IGenerator> getChildren( IGenerator generator ) {
		List<IGenerator> children = new ArrayList<IGenerator>();
		IGenerator[] availableGens = EDTCoreIDEPlugin.getPlugin().getGenerators();
		for( IGenerator availableGen : availableGens ) {
			if( availableGen.getParentGeneratorId() != null && 
					availableGen.getParentGeneratorId().equalsIgnoreCase( generator.getId() ) ) {
				children.add( availableGen );
			}
		}
		return children;
	}
	
	protected CompilerComponent getCompilerComponent() {
		return compilerComponent;
	}	
	
}
