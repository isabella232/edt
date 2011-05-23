/*******************************************************************************
 * Copyright © 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.preferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.ICompiler;
import org.eclipse.edt.ide.core.IGenerator;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.util.TabFolderLayout;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.osgi.service.prefs.BackingStoreException;

public class CompilerPropertyAndPreferencePage extends PropertyAndPreferencePage {
	
	public static final String PROP_ID= "org.eclipse.edt.ide.ui.projectCompilerPropertyPage"; //$NON-NLS-1$
	
	public static final String EXTENSIONPOINT_GENERATOR_TABS = org.eclipse.edt.ide.ui.EDTUIPlugin.PLUGIN_ID + ".edtGeneratorTabs"; //$NON-NLS-1$
	public static final String GENERATOR_TAB = "generatorTab"; //$NON-NLS-1$
	public static final String GENERAL_TAB = "generalTab"; //$NON-NLS-1$
	public static final String GENERATOR_TAB_GENERATOR_ID = "generatorId"; //$NON-NLS-1$
	public static final String GENERAL_TAB_NAME = "tabName"; //$NON-NLS-1$
	public static final String GENERAL_TAB_COMPILER_ID = "compilerId"; //$NON-NLS-1$
	public static final String TAB_CLASS = "class"; //$NON-NLS-1$

	// Previously selected compiler. Used to prevent adding more generator
	// tabs if selected compiler is reselected.
	protected String  previouslySelectedCompiler = new String();
	
	// Currently selected compiler
	protected String  selectedCompiler = new String();
	
	// Currently selected generator names
	protected List<String> selectedGenerators = new ArrayList<String>();
	
	// Current tabs in the folder.  Should stay in sync with currTabProviders.
	protected List<TabItem> currentTabItems = new ArrayList<TabItem>();
	
	// Current generator tab providers.  Should stay in sync with currentTabItems.
	protected List<IGeneratorTabProvider> currentTabProviders =  new ArrayList<IGeneratorTabProvider>();
	
	// Titles of current tabs.  Should stay in sync wit currentTabItems
	// and currentTabProviders.
	protected List<String> currentTabTitles = new ArrayList<String>();
	
	// Store generator buttons corresponding to each compiler button.
	// Key is compiler button.  Values are lists of generator buttons.
	protected HashMap<Button, List<Button>> buttonMap = new HashMap<Button, List<Button>>();

	// Folder and composite to update
	protected TabFolder tabFolder;
	protected Composite tabComposite;	
	
	// The resource for a property page (project, folder, or file)
	protected IResource resource;
	
	/**
	 * Create the content for this page.
	 * 
	 * @param Composite
	 * @return Composite
	 */
	protected Composite createPreferenceContent( Composite parent ) {
		initialize();
		Composite composite = new Composite(parent, SWT.NULL);

		//GridLayout
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setFont(parent.getFont());

		//GridData
		GridData data = new GridData(GridData.FILL);
		data.horizontalIndent = 0;
		data.verticalAlignment = GridData.FILL;
		composite.setLayoutData(data);		
		
		// Preference page
		if( this.resource == null ) {
			// Compiler selection preference page
			if(	getCompilerId() == CompilerSelectionPreferencePage.COMPILER_SELECTION_ID ) {
				createCompilerAndGeneratorComposite( composite );
				initPreferencePageSelectionValues();
			// Compiler preference page
			} else if( getCompilerId().length() > 0 ){
				createGeneratorTabsComposite( composite );
				initPreferencePageTabs();
			}
		} else {
			// Property page
			createCompilerAndGeneratorComposite( composite );
			createGeneratorTabsComposite( composite );
			initPropertyPageVaues();
		}

		Dialog.applyDialogFont(composite);
		return composite;
	}
	
	/**
	 * Initialize
	 */
	protected void initialize() {
		setDescription( UINlsStrings.CompilerPreferencePage_description );
	}
	
	/**
	 * Create a radio button for each compiler and check box for each 
	 * generator that supports the compiler.   
	 * 
	 * @param composite
	 */
	protected void createCompilerAndGeneratorComposite( Composite composite ) {
		ICompiler[] compilers = getCompilersToDisplay(); 
		if( compilers != null ) {
			for( final ICompiler currentCompiler : compilers ) {			
				String currentCompilerName = currentCompiler.getName();
				final Button compilerButton = new Button( composite, SWT.RADIO );
				compilerButton.setText( currentCompilerName );	
				compilerButton.setLayoutData(new GridData());		
				compilerButton.addSelectionListener(new SelectionListener(){
					public void widgetDefaultSelected(SelectionEvent e) {
					}
					public void widgetSelected(SelectionEvent e) {
						// Save selected compiler
						if( ((Button)e.widget).getSelection() ) {	
							// Add the new compiler if not already selected
							previouslySelectedCompiler = selectedCompiler;
							selectedCompiler = ((Button)e.widget).getText();
							if( !previouslySelectedCompiler.equalsIgnoreCase( selectedCompiler ) ) {
								addCompilerTabsAndProviders( selectedCompiler );
							}
						} else {
							selectedGenerators.clear();
							removeCurrentCompilerTabsAndProviders();
						}
						// Update generator buttons
						List<Button> buttonsInCompiler = buttonMap.get(((Button)e.widget));
						if( buttonsInCompiler != null  ) {
							for( Button currButton : buttonsInCompiler ) {
								if( ((Button)e.widget).getSelection() ) {	
									currButton.setEnabled( true );
								} else {
									currButton.setEnabled( false );
									currButton.setSelection( false );
								}
							}
						}
					}
				});		

				Group group = new Group(composite, SWT.NULL);
				GridLayout groupLayout = new GridLayout();
				groupLayout.numColumns = 3;
				group.setLayout( groupLayout );

				GridData groupData = new GridData();
				groupData.grabExcessHorizontalSpace = true;
				groupData.horizontalAlignment = GridData.FILL;
				group.setLayoutData( groupData );

				List<Button> generatorButtonsInCompiler = new ArrayList<Button>();
				List<IGenerator> availableGens = currentCompiler.getGenerators();
				if( availableGens.size() ==  0 ) {
					// Add the compiler button without generator buttons
					this.buttonMap.put( compilerButton, generatorButtonsInCompiler );
				} else {
					for( IGenerator currentGenerator : availableGens ) {
						String currentGeneratorName = currentGenerator.getName();

						final Button generatorButton = new Button( group, SWT.CHECK );
						generatorButton.setText( currentGeneratorName );	
						generatorButton.setLayoutData(groupData);
						generatorButtonsInCompiler.add( generatorButton );
						this.buttonMap.put(compilerButton, generatorButtonsInCompiler);
						generatorButton.addSelectionListener(new SelectionListener(){
							public void widgetDefaultSelected(SelectionEvent e) {
							}
							public void widgetSelected(SelectionEvent e) {
								String text = ((Button)e.widget).getText();
								if( ((Button)e.widget).getSelection() ) {
									selectedGenerators.add( text );
									addGeneratorTabAndProvider( text );
								} else {
									selectedGenerators.remove( ((Button)e.widget).getText());
									removeGeneratorTabAndProvider( text );
								}
							}
						});
						generatorButton.setEnabled(false);
					} 
				}
			}
		}
	}
	
	/**
	 * Return an array of compilers to display on the page.  For
	 * workspace preferences and projects, display all compilers.
	 * For folders and files, display the project's selected compiler.
	 * 
	 * @return ICompiler[]
	 */
	protected ICompiler[] getCompilersToDisplay() {
		if( ( this.resource == null ) || ( this.resource instanceof IProject ) ) {
			return EDTCoreIDEPlugin.getPlugin().getCompilers();
		} else {
			// Use the project's selected compiler
			ICompiler comp = ProjectSettingsUtility.getCompiler( getProject() );
			if( comp != null ) {
				ICompiler[] compilerArray = new ICompiler[]{ comp };
				return compilerArray;
			} else {
				return null;
			}
		}
	}

	/**
	 * Create a composite to hold generator tabs for a property
	 * page or a preference page for a compiler.
	 * 
	 * @param composite
	 */
	protected void createGeneratorTabsComposite( Composite composite ) {
		this.tabComposite = new Composite(composite, SWT.NONE);	

		GridLayout tabLayout = new GridLayout();
		tabLayout.marginWidth = 0;
		tabLayout.numColumns = 1;		
		this.tabComposite.setLayout(tabLayout);
		this.tabComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.tabFolder = new TabFolder( this.tabComposite, SWT.NONE );
		this.tabFolder.setLayout(new TabFolderLayout());	
		this.tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	/**
	 * Initialize a property page for a resource.
	 */
	protected void initPropertyPageVaues() {
		String savedCompilerId = ProjectSettingsUtility.getCompilerId( getProject() );
		if( savedCompilerId != null ) {
			String savedCompiler = convertCompilerIdToName( savedCompilerId );
			String[] savedGenerators = ProjectSettingsUtility.getGeneratorIds( this.resource );
			List<String> savedGeneratorsList = new ArrayList<String>();
			if (savedGenerators != null) {
				for( String generatorId : savedGenerators ) {
					savedGeneratorsList.add( convertGeneratorIdToName( generatorId ) );
				}
			}

			Set<Button> availableCompilers = this.buttonMap.keySet();
			for( Button currCompiler : availableCompilers ){
				if( currCompiler.getText().equalsIgnoreCase( savedCompiler ) ) {
					currCompiler.setSelection( true );
					this.selectedCompiler = currCompiler.getText();					
					List<Button> availableGens = this.buttonMap.get( currCompiler );
					for( Button currGenerator : availableGens ) {
						if( savedGeneratorsList.contains( currGenerator.getText() ) ) {
							currGenerator.setSelection( true );
							selectedGenerators.add( currGenerator.getText() );
						}
						currGenerator.setEnabled( true );
					}
					addCompilerTabsAndProviders( this.selectedCompiler );
				}					
			}
		}
	}

	/**
	 * Initialize the compiler selection preference page.
	 */
	protected void initPreferencePageSelectionValues() {
		// Initialize compiler buttons
		Set<Button> availableCompilers = this.buttonMap.keySet();
		for( Button currCompiler : availableCompilers ) {
			currCompiler.setSelection(true);
			currCompiler.setEnabled(false);
		}
		Collection<List<Button>> availableGens = this.buttonMap.values();
		for( List<Button> currList : availableGens ) {
			for( Button currGenerator : currList ) {
				currGenerator.setSelection(true);
				currGenerator.setEnabled(false);
			}
		}
	}
	
	/**
	 * Initialize the preference page for a compiler with its
	 * generator tabs.
	 */
	protected void initPreferencePageTabs() {
		String compilerName = convertCompilerIdToName( getCompilerId() );
		addCompilerTabsAndProviders( compilerName );
	}
	
	/**
	 * Add providers and tabs for each generator in a compiler.
	 * 
	 * @param compilerName
	 */
	protected void addCompilerTabsAndProviders( String compilerName ) {		
		addCompilerProviders();
		for( IGeneratorTabProvider provider: this.currentTabProviders ){
			addTabItemForProvider( provider );
		}
		// Select the first generator tab that was added
		if( currentTabItems.size() > 1 ) {
			tabFolder.setSelection( 0 );
		}
	}
	
	/**
	 * Adds a tab for the generator named genName.
	 * 
	 * @param genName
	 */
	protected void addGeneratorTabAndProvider( String genName ) {
		IGeneratorTabProvider provider = addGeneratorProvider( genName );
		if( provider != null ) {
			addTabItemForProvider( provider );
		}
	}
	
	/**
	 * Creates a tab using a generator tab provider.
	 * 
	 * @param provider
	 */
	protected void addTabItemForProvider( IGeneratorTabProvider provider ) {
		TabItem item = new TabItem( this.tabFolder, SWT.NONE );

		int index = currentTabProviders.indexOf( provider );
		item.setText( currentTabTitles.get( index ) );
		if( provider.getImage() != null ) {
			item.setImage( provider.getImage() );
		}
		// Resource must be set in provider when the tab is displayed on
		// a properties page.
		if( this.resource != null ) {
			provider.setResource( this.resource );
		}
		provider.setStatusChangeListener( getNewStatusChangedListener() );
		item.setControl( provider.getTabContent( this.tabFolder ) );
		this.currentTabItems.add( item );
		this.tabFolder.setSelection( item );
	}
	
	protected IGeneratorTabProvider addCompilerProviders() {
		String generatorId;
		IGeneratorTabProvider newProvider;
		List<IConfigurationElement> tabContrs = getTabContributionsForSelectedCompiler();
		for( IConfigurationElement currContribution : tabContrs ) {
			// If a generatorId is specified, only show the tab on a property page
			// if the resource has that generator selected.  Ignore generatorId for
			// preference pages.
			generatorId = currContribution.getAttribute( GENERATOR_TAB_GENERATOR_ID );
			if( ( this.resource != null ) && ( generatorId != null ) ) {
				if( this.selectedGenerators.contains( convertGeneratorIdToName( generatorId ) ) ) {
					newProvider = addProviderToCurrentTabProviders( currContribution );
					if( newProvider != null ) {
						addTitleToCurrentTabTitles( currContribution );
					}
				}
			} else {
				newProvider = addProviderToCurrentTabProviders( currContribution );
				if( newProvider != null ) {
					addTitleToCurrentTabTitles( currContribution );
				}
			}
		}		
		return null;
	}
	
	/**
	 * Add the title associated with currContribution to currentTabTitles.
	 * If a generatorId is specified, use the name of the generator.
	 * Otherwise, use the tab name specified on the tab extension.
	 * 
	 * @param currContribution
	 */
	protected void addTitleToCurrentTabTitles( IConfigurationElement currContribution ) {
		String generatorId = currContribution.getAttribute( GENERATOR_TAB_GENERATOR_ID );
		if( generatorId == null ) {
			// Use name from tab
			String name = currContribution.getAttribute( GENERAL_TAB_NAME );
			currentTabTitles.add( name );
		} else {
			// User generator id
			currentTabTitles.add( convertGeneratorIdToName( generatorId ) );
		}
	}
		
	protected IGeneratorTabProvider addGeneratorProvider( String genName ) {
		String generatorId;
		IGeneratorTabProvider newProvider;
		List<IConfigurationElement> tabContrs = getTabContributionsForSelectedCompiler();
		for( IConfigurationElement currContribution : tabContrs ) {
			// Get generatorId from the tab extension.  If no generatorId is
			// specified, show the tab.  If a generatorId is specified, only
			// show the tab if the generatorId is selected in this project
			generatorId = currContribution.getAttribute( GENERATOR_TAB_GENERATOR_ID );
			if( generatorId != null ) {
				if( convertGeneratorIdToName( generatorId).equalsIgnoreCase( genName )) {
					newProvider = addProviderToCurrentTabProviders( currContribution );
					if( newProvider != null ) {
						addTitleToCurrentTabTitles( currContribution );
					}
					return newProvider;
				}
			}
		}		
		return null;
	}
	
	/**
	 * Add a tab provider to the currTabProviders list.
	 * 
	 * @param currContribution
	 */
	protected IGeneratorTabProvider addProviderToCurrentTabProviders( IConfigurationElement currContribution ) {
		try {
			Object o = currContribution.createExecutableExtension( TAB_CLASS );
			if( o instanceof IGeneratorTabProvider ) {
				this.currentTabProviders.add( (IGeneratorTabProvider)o );
				return ( (IGeneratorTabProvider)o );
			} 		}
		catch (CoreException ce) {
			EDTUIPlugin.log( ce );
		}
		return null;
	}
		
	protected List<IConfigurationElement> getTabContributionsForSelectedCompiler() {
		List<IConfigurationElement> tabContrs = new ArrayList<IConfigurationElement>();
		// Get all tab extensions
		IConfigurationElement[] generatorTabContributions = Platform.getExtensionRegistry().getConfigurationElementsFor( EXTENSIONPOINT_GENERATOR_TABS );
		if( generatorTabContributions != null ) {
			String compilerId;
			// Loop through generator/general tab extensions
			for( IConfigurationElement currContribution : generatorTabContributions ) {
				compilerId = currContribution.getAttribute( GENERAL_TAB_COMPILER_ID );
				if( compilerId == null ) {
					// Must be a generatorTab extension
					String generatorId = currContribution.getAttribute( GENERATOR_TAB_GENERATOR_ID);
					IGenerator[] gens = EDTCoreIDEPlugin.getPlugin().getGenerators();
					for( IGenerator gen : gens ) {
						if( gen.getId().equalsIgnoreCase( generatorId ) ) {
							compilerId = gen.getCompiler().getId();
							break;
						}
					}
				}
				if( this.resource != null ) {	
					// If property page, return tab if compilerId equals selected compiler
					if( convertCompilerNameToId( this.selectedCompiler ).equalsIgnoreCase( compilerId ) ) 
					{
						tabContrs.add( currContribution );
					} 
				}  else {
						// Check if the compiler id on the tab matches the compiler id
						// of this preference page.
						if( getCompilerId().equalsIgnoreCase( compilerId ) ) {
							tabContrs.add( currContribution );
						}
				}
			}
		}
		return tabContrs;
	}	
			
	/**
	 * Dispose of all current tab items.  Clear the lists containing
	 * the current tab providers and tab items.
	 */
	protected void removeCurrentCompilerTabsAndProviders() {
		// Notify each generator tab provider that it is being disposed
		for( IGeneratorTabProvider currProvider : this.currentTabProviders ) {
			currProvider.dispose();
		}
		// Dispose of each tab item
		for( TabItem currItem : this.currentTabItems ) {
			currItem.dispose();
		}
		// Clear the lists
		this.currentTabProviders.clear();
		this.currentTabItems.clear();
		this.currentTabTitles.clear();
	}
	
	/**
	 * Dispose of the tab item and provider for a specific generator.
	 * 
	 * @param genName
	 */
	protected void removeGeneratorTabAndProvider( String genName ) {
		TabItem currItem;
		for( int i=0; i < currentTabItems.size(); i++ ) {
			currItem = currentTabItems.get( i );
			if( currItem.getText().equalsIgnoreCase( genName )) {
				currItem.dispose();
				this.currentTabItems.remove( currItem );
				this.currentTabTitles.remove( i );
				this.currentTabProviders.get( i ).dispose();
				this.currentTabProviders.remove( i );
				break;
			}									
		}
	}	

	/**
	 * Return the id of the preference page corresponding to the selected
	 * compiler from the compiler's extension.
	 * 
	 * @return String
	 */
	protected String getPreferencePageID() {
		String compilerId = convertCompilerNameToId( selectedCompiler );
		ICompiler[] compilers = EDTCoreIDEPlugin.getPlugin().getCompilers();
		for( ICompiler currCompiler : compilers ) {
			if( currCompiler.getId().equalsIgnoreCase( compilerId ) ) {
				return currCompiler.getPreferencePageId();
			}
		}
		return null;
	}

	/**
	 * Return the id of the compilers property page.
	 * 
	 * @return String
	 */
	protected String getPropertyPageID() {
		return PROP_ID;
	}

	protected boolean hasProjectSpecificOptions(IProject project) {
		if( project != null ) {
			return true;
		} else {
			return false;
		}
	}	
	
	public void performApply() {
		for( IGeneratorTabProvider currProvider : this.currentTabProviders ) {
			currProvider.performApply();
		}
	}
	
	public void performDefaults() {
		for( IGeneratorTabProvider currProvider : this.currentTabProviders ) {
			currProvider.performDefaults();
		}
	}
	
	public boolean performCancel() {
		boolean retValue = true;
		for( IGeneratorTabProvider currProvider : this.currentTabProviders ) {
			if( !currProvider.performCancel() ) {
				retValue = false;
			}				
		}
		return retValue;
	}
	
	public boolean performOk() {
		boolean retValue = true;
		for( IGeneratorTabProvider currProvider : this.currentTabProviders ) {
			if( !currProvider.performOk() ) {
				retValue = false;
			}				
		}
		storeValues();
		return retValue;
	}
	
	/**
	 * If on a property page, save the compilers and generators that 
	 * were selected on the project's preferences.
	 */
	protected void storeValues() {
		if( this.resource != null ) {
			try {				
				ProjectSettingsUtility.setCompiler( getProject(), convertCompilerNameToId( this.selectedCompiler ) );
				String[] genIds = new String[this.selectedGenerators.size()];
				int index = 0;
				for( String genName : this.selectedGenerators ) {
					genIds[index] = convertGeneratorNameToId( genName );
					index++;
				}
				ProjectSettingsUtility.setGeneratorIds( this.resource, genIds );
			}
			catch( BackingStoreException e ) {
				EDTUIPlugin.log( e );
 			}
		}
	}

	protected String convertCompilerIdToName( String id ) {
		if( id != null && id.length() > 0 ) {
			ICompiler[] compilers = EDTCoreIDEPlugin.getPlugin().getCompilers();
			for( final ICompiler currentCompiler : compilers ) {
				if( currentCompiler.getId().equalsIgnoreCase( id ) )  {
					return currentCompiler.getName();
				}
			}
		}
		return "";
	}
	
	protected String convertGeneratorIdToName( String id ) {
		if( id != null && id.length() > 0 ) {
			IGenerator[] generators = EDTCoreIDEPlugin.getPlugin().getGenerators();
			for( final IGenerator currentGenerator : generators ) {
				if( currentGenerator.getId().equalsIgnoreCase( id ) )  {
					return currentGenerator.getName();
				}
			}
		}
		return "";
	}
	
	protected String convertCompilerNameToId( String name ) {
		if( name != null && name.length() > 0 ) {
			ICompiler[] compilers = EDTCoreIDEPlugin.getPlugin().getCompilers();
			for( final ICompiler currentCompiler : compilers ) {
				if( currentCompiler.getName().equalsIgnoreCase( name ) )  {
					return currentCompiler.getId();
				}
			}
		}
		return "";
	}
	
	protected String convertGeneratorNameToId( String name ) {
		if( name != null && name.length() > 0 ) {
			IGenerator[] generators = EDTCoreIDEPlugin.getPlugin().getGenerators();
			for( final IGenerator currentGenerator : generators ) {
				if( currentGenerator.getName().equalsIgnoreCase( name ) )  {
					return currentGenerator.getId();
				}
			}
		}
		return "";
	}

	protected String getCompilerId() {
		return "";
	}

	/**
	 * Return the project for the selected resource.
	 * 
	 * @return IProject
	 */
	protected IProject getProject() {
		if( this.resource != null ) {
			if( this.resource instanceof IProject ) {
				return (IProject)this.resource;
			} else {
				return this.resource.getProject();
			}	
		}
		return null;
	}
	
	public IAdaptable getElement() {
		return this.resource;
	}

	public void setElement(IAdaptable element) {
		this.resource = (IResource) element.getAdapter(IResource.class);
		// Set project in superclass or it will think this is a preference page
		super.setElement( getProject() );
	}
	
}
