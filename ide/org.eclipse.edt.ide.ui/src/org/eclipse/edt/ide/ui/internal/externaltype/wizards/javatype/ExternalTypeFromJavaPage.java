/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.externaltype.wizards.javatype;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusUtil;
import org.eclipse.edt.ide.ui.internal.externaltype.NewExternalTypeWizardMessages;
import org.eclipse.edt.ide.ui.internal.externaltype.conversion.javatype.JavaTypeConstants;
import org.eclipse.edt.ide.ui.internal.externaltype.util.ImagePath;
import org.eclipse.edt.ide.ui.internal.externaltype.util.ReflectionUtil;
import org.eclipse.edt.ide.ui.internal.util.SWTUtil;
import org.eclipse.edt.ide.ui.internal.util.ViewerPane;
import org.eclipse.edt.ide.ui.internal.wizards.EGLPackageWizard;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.DialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.LayoutUtil;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.edt.ide.ui.templates.wizards.TemplateWizard;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class ExternalTypeFromJavaPage extends WizardPage 
               implements ICheckStateListener {
	private static final String WIZPAGENAME_ExternalTypeWizardPage = "WIZPAGENAME_ExternalTypeFromJavaPage"; //$NON-NLS-1$
	private IStatus fCurrStatus;
	private boolean fPageVisible;
	private StatusInfo memberStatus;
	private Button generatedAllSuperTypesCheckbox;
	private Button generatedAllReferencedTypesCheckbox;
	
	private StringButtonDialogField fSelectedClassDialogField;
	private CheckboxTreeViewer javaTypeViewer;
	private Object fInput;
	private ITreeContentProvider fContentProvider;
	private ExternalTypeFromJavaWizardConfiguration configuration;
	
	private IJavaProject javaProject;
	private URLClassLoader urlClassLoader;
	
	public ExternalTypeFromJavaPage(ExternalTypeFromJavaWizardConfiguration config) {
		super(WIZPAGENAME_ExternalTypeWizardPage);
		setTitle(NewExternalTypeWizardMessages.ExternalTypeFromJavaTypePage_Title);
		setDescription(NewExternalTypeWizardMessages.ExternalTypeFromJavaTypePage_Description);
		
		this.configuration = config;
		
		TypeFieldsAdapter adapter= new TypeFieldsAdapter();
		fSelectedClassDialogField= new StringButtonDialogField(adapter);
		fSelectedClassDialogField.setDialogFieldListener(adapter);
		fSelectedClassDialogField.setLabelText(getSuperClassLabel());
		fSelectedClassDialogField.setButtonLabel(NewExternalTypeWizardMessages.NewExternalTypeWizardPage_selectedclass_button);
		
		fInput = null;
		fPageVisible = false;
		fCurrStatus = new StatusInfo();
		memberStatus = new StatusInfo();
	}
	
	private IJavaProject getJavaProject() {
		if(javaProject == null) {
			TemplateWizard wizard = (TemplateWizard)this.getWizard();
			EGLPackageWizard parentWizard = (EGLPackageWizard)wizard.getParentWizard();
			String projectName = parentWizard.getConfiguration().getProjectName();
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject( projectName );
			javaProject = JavaCore.create(project);
		}
		
		return javaProject;
	}
	
	private ClassLoader getURLClassLoader() {
		if(urlClassLoader == null) {
			List<URL> classPathURLs = new ArrayList<URL>();
			IPath  proRoot = javaProject.getProject().getLocation();
			
			try{//Add Java class path.
				IPackageFragmentRoot[] roots = javaProject.getPackageFragmentRoots();
				IPath outputRelPath =  javaProject.getOutputLocation().removeFirstSegments(1);
				
				for(IPackageFragmentRoot pRoot : roots) {
					if(pRoot.isArchive() && pRoot.isExternal()) {
						classPathURLs.add(pRoot.getResolvedClasspathEntry().getPath().toFile().toURI().toURL());
					} else {
						classPathURLs.add(proRoot.append(outputRelPath).toFile().toURI().toURL());
					}
				}
			} catch(Throwable ee) {
				ee.printStackTrace();
			}
			
			ClassLoader parent = Thread.currentThread().getContextClassLoader();
			URL[] urlPaths = new URL[classPathURLs.size()];
			urlClassLoader = new URLClassLoader(classPathURLs.toArray(urlPaths), parent);
		}
		
		return urlClassLoader;
	}
	
	@Override
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		// Create main composite
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		
		int nColumns= 4;
		GridLayout layout= new GridLayout();
		layout.numColumns= nColumns;
		composite.setLayout(layout);
		
		createSelectedClassControls(composite, nColumns);
		createTreeViewer(composite,3);
		createSelectionButtons(composite,1);
		
		generatedAllSuperTypesCheckbox = new Button(composite, SWT.CHECK);
		generatedAllSuperTypesCheckbox.setText(NewExternalTypeWizardMessages.NewExternalTypeWizardPage_GeneratedAllSuperTypes_message);
		GridData data = new GridData();
		data.horizontalSpan = nColumns;
		generatedAllSuperTypesCheckbox.setLayoutData(data);
		generatedAllSuperTypesCheckbox.setSelection(true);
		generatedAllSuperTypesCheckbox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
 				boolean isSelected = ((Button)e.widget).getSelection();
				configuration.setAllSuperTypesGenerated(isSelected);
			}
		});
		
		generatedAllReferencedTypesCheckbox = new Button(composite, SWT.CHECK);
		generatedAllReferencedTypesCheckbox.setText(NewExternalTypeWizardMessages.NewExternalTypeWizardPage_GeneratedAllReferencedTypes_message);
		data = new GridData();
		data.horizontalSpan = nColumns;
		generatedAllReferencedTypesCheckbox.setLayoutData(data);
		generatedAllReferencedTypesCheckbox.setSelection(true);
		generatedAllReferencedTypesCheckbox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean isSelected = ((Button)e.widget).getSelection();
				configuration.setAllReferencedTypesGenerated(isSelected);
			}
		});
		
		setErrorMessage(null);
		setMessage(null);
		setControl(composite);

		// validatePage();
		Dialog.applyDialogFont(parent);
		getJavaProject();
	}
	
	protected void createSelectedClassControls(Composite composite, int nColumns) {
		fSelectedClassDialogField.doFillIntoGrid(composite, nColumns);
		fSelectedClassDialogField.getTextControl(composite).setEditable(false);
		Text text= fSelectedClassDialogField.getTextControl(null);
		LayoutUtil.setWidthHint(text, getMaxFieldWidth());
	}
	
	protected String getSuperClassLabel() {
		return NewExternalTypeWizardMessages.ExternalTypeFromJavaPage_SelectedClass_label;
	}
	
	private class TypeFieldsAdapter implements IStringButtonAdapter, IDialogFieldListener {
		// -------- IStringButtonAdapter
		public void changeControlPressed(DialogField field) {
			typePageChangeControlPressed(field);
		}
		
		// -------- IDialogFieldListener
		public void dialogFieldChanged(DialogField field) {
			typePageDialogFieldChanged(field);
		}
	}
	
	private void typePageChangeControlPressed(DialogField field) {
		if (field == fSelectedClassDialogField) {
			IType type= chooseSelectedClass();
			if (type != null) {
				String fullPath = type.getFullyQualifiedName();
				
				if(JavaTypeConstants.JavaToEglMapping.containsKey(fullPath)) {
					//Does not generate for certain Java class
					memberStatus.setError(NewExternalTypeWizardMessages.bind(
							 NewExternalTypeWizardMessages.ExternalTypeFromJavaPage_Validation_EGLBuiltinType,
							 new String[] {fullPath}));
					updateStatus(memberStatus);
				} else {
					fSelectedClassDialogField.setText(fullPath);
					configuration.setSelectedClazz(ReflectionUtil.getClass(getURLClassLoader(),fullPath));
					javaTypeViewer.setInput(fullPath);
					
					storeSelectedClass();
					validatePage();
				}
			}
		}
	}
	
	private void typePageDialogFieldChanged(DialogField field) {
		String fieldName= null;
	}
	
	protected IType chooseSelectedClass() {
		if (getJavaProject() == null) {
			return null;
		}

		IJavaElement[] elements= new IJavaElement[] { javaProject };
		IJavaSearchScope scope= SearchEngine.createJavaSearchScope(elements);

		FilteredTypesSelectionDialog dialog= new FilteredTypesSelectionDialog(getShell(), false,
			getWizard().getContainer(), scope, IJavaSearchConstants.CLASS);
		
		dialog.setTitle(NewExternalTypeWizardMessages.NewExternalTypeWizardPage_SelectedClassDialog_title);
		dialog.setMessage(NewExternalTypeWizardMessages.NewExternalTypeWizardPage_SelectedClassDialog_message);
		dialog.setInitialPattern(getSelectedClass());

		if (dialog.open() == Window.OK) {
			return (IType) dialog.getFirstResult();
		}
		return null;
	}
	
	public String getSelectedClass() {
		return fSelectedClassDialogField.getText();
	}
	
	protected int getMaxFieldWidth() {
		return convertWidthInCharsToPixels(40);
	}
	
	protected void createTreeViewer(Composite parent,int nColumns) {
		initializeDialogUnits(parent);
		ViewerPane pane= new ViewerPane(parent, SWT.BORDER | SWT.FLAT);//
		pane.setText(NewExternalTypeWizardMessages.NewExternalTypeWizardPage_PaneTitle_message);
		
		javaTypeViewer = new CheckboxTreeViewer(pane);
		fContentProvider = new ExternalTypeContentProvider();
		javaTypeViewer.setContentProvider(fContentProvider);
		javaTypeViewer.setLabelProvider(new ExternalTypeLabelProvider());
		javaTypeViewer.setInput(fInput);
		
		pane.setContent(javaTypeViewer.getControl());
		javaTypeViewer.addCheckStateListener(this);
		
		GridLayout paneLayout= new GridLayout();
		paneLayout.marginHeight= 0;
		paneLayout.marginWidth= 0;
		pane.setLayout(paneLayout);
		GridData gd= new GridData(GridData.FILL_BOTH);
		
		gd.horizontalAlignment= GridData.FILL;
		gd.grabExcessHorizontalSpace= true;
		gd.verticalAlignment= GridData.FILL;
		gd.grabExcessVerticalSpace= true;
		gd.horizontalSpan= nColumns;
		
		gd.widthHint= convertWidthInCharsToPixels(55);
		gd.heightHint= convertHeightInCharsToPixels(15);
		pane.setLayoutData(gd);
		
		ToolBarManager manager= pane.getToolBarManager();
		manager.update(true);
		javaTypeViewer.getTree().setFocus();
	}
	
    protected Composite createSelectionButtons(Composite parent,int nColumns) {
    	Composite buttonComposite = new Composite(parent, SWT.RIGHT);
    	buttonComposite.setFont(parent.getFont());
    	 
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.marginHeight= 0;
		layout.marginWidth= 0;
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		buttonComposite.setLayout(layout);
       
		GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		gd.horizontalSpan= nColumns;
		buttonComposite.setLayoutData(gd);
		
        SelectionListener listener = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            	Object[] viewerElements = fContentProvider.getElements(fInput);
            	for (int i = 0; i < viewerElements.length; i++) {
            		javaTypeViewer.setSubtreeChecked(viewerElements[i], true);
				}
            	
            	storeSelectedClass();
            	validatePage();
            }
        };
        
        createButton(buttonComposite,NewExternalTypeWizardMessages.NewExternalTypeWizardPage_SelectAll_message,listener);
        
        listener = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            	javaTypeViewer.setCheckedElements(new Object[0]);
            	
            	storeSelectedClass();
            	validatePage();
            }
        };
        createButton(buttonComposite,NewExternalTypeWizardMessages.NewExternalTypeWizardPage_DeSelectAll_message,listener);
        
        return buttonComposite;
	}
	
	protected Button createButton(Composite parent, String label, SelectionListener listener) {
		Button button= new Button(parent, SWT.PUSH);
		button.setFont(parent.getFont());
		button.setText(label);
		button.addSelectionListener(listener);
		GridData gd= new GridData();
		gd.horizontalAlignment= GridData.FILL;
		gd.grabExcessHorizontalSpace= true;
		gd.verticalAlignment= GridData.BEGINNING;
		gd.widthHint = SWTUtil.getButtonWidthHint(button);

		button.setLayoutData(gd);

		return button;
	}
	
	class ExternalTypeContentProvider implements ITreeContentProvider {
		private Object[] fTypes;
		private final Object[] fEmpty= new Object[0];
		private String inputFullyQualifiedName;
		
		public ExternalTypeContentProvider() {
		}
		
		@Override
		public Object[] getElements(Object inputElement) {
			if(fTypes != null) {//return selected class and all its super class and interface
				return fTypes;
			}
			return fEmpty;
		}
		
		@Override
		public Object[] getChildren(Object parentElement) {
			if(parentElement instanceof Class) {//return public methods of a Java Class
				Class<?> clazz = (Class<?>)parentElement;
				
				HashSet<Field> fields = new HashSet<Field>();
				for(Field field : clazz.getDeclaredFields()) {
					 if (Modifier.isPublic(field.getModifiers())) {
						 fields.add(field);
					 }
				}
				
				HashSet<Constructor<?>> pubConstructors = new HashSet<Constructor<?>>();
				for(Constructor<?> pubCon : clazz.getDeclaredConstructors()) {
					if(Modifier.isPublic(pubCon.getModifiers())) {
						pubConstructors.add(pubCon);
					}
				}
				
				boolean skipped;
				HashSet<Method> publicMethods = new HashSet<Method>(16);
				for (Method m : clazz.getDeclaredMethods()) {
					 if (Modifier.isPublic(m.getModifiers())) {
						 skipped = false;
						 Class<?>[] pTypes = m.getParameterTypes();
						 Class<?> componentType;
						 for(Class<?> pType : pTypes) {
							 componentType = ReflectionUtil.getComponentClass(pType);
							 if(Byte.TYPE.equals(componentType)
								  || Byte.class.equals(componentType)) {
								 skipped = true;
							 }
						 }
						 
						 componentType = ReflectionUtil.getComponentClass(m.getReturnType());
						 if(Byte.TYPE.equals(componentType)
							  || Byte.class.equals(componentType)) {
							 skipped = true;
						 }
						 
						 if(!skipped) {
							 publicMethods.add(m);
						 }
					 }
				}
				
				int memLen = fields.size() + pubConstructors.size() + publicMethods.size();
				Object[] members = new Object[memLen];
				int counter = 0;
				for(Field field : fields) {
					members[counter++] = field;
				}
				
				for(Constructor<?> pubCon : pubConstructors) {
					members[counter++] = pubCon;
				}
				
				for(Method m : publicMethods) {
					members[counter++] = m;
				}
				 
				
				return members;
			}
			return fEmpty;
		}
		
		@Override
		public Object getParent(Object element) {
			if (element instanceof java.lang.reflect.Method) {
				return ((java.lang.reflect.Method)element).getDeclaringClass();
			} else if (element instanceof java.lang.reflect.Constructor) {
				return ((java.lang.reflect.Constructor<?>)element).getDeclaringClass();
			} else if (element instanceof java.lang.reflect.Field) {
				return ((java.lang.reflect.Field)element).getDeclaringClass();
			}
			return null;
		}
		
		@Override
		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}
		
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if(newInput instanceof String) {
				inputFullyQualifiedName = (String)newInput;
				updateJavaTypeTree();
			}
		}

		@Override
		public void dispose() {
		}
		
		public void updateJavaTypeTree() {
			if(inputFullyQualifiedName != null) {
				HashSet<Class<?>> types = new HashSet<Class<?>>(10);
				
				Class<?> selectedClazz = ExternalTypeFromJavaPage.this.configuration.getSelectedClazz();
				if(selectedClazz == null) {
					selectedClazz = ReflectionUtil.getClass(ExternalTypeFromJavaPage.this.getURLClassLoader(),inputFullyQualifiedName);
				}
				types.add(selectedClazz);
				fTypes = types.toArray(new Class<?>[types.size()]);
			}
		}

	}//class ExternalTypeContentProvider
	
	public class ExternalTypeLabelProvider implements ILabelProvider {
		private Image classImage;
		private Image methpubImage;
		private Image fieldpubImage;
		
		public ExternalTypeLabelProvider() {
			try {
				URL url = new URL(ImagePath.JDT_UI_ICONS_FOLDER_URL + ImagePath.CLASS);
				classImage = ImageDescriptor.createFromURL(url).createImage();

				url = new URL(ImagePath.JDT_UI_ICONS_FOLDER_URL + ImagePath.METHPUB);
				methpubImage = ImageDescriptor.createFromURL(url).createImage();
				
				url = new URL(ImagePath.JDT_UI_ICONS_FOLDER_URL + ImagePath.FIELDPUBLIC);
				fieldpubImage = ImageDescriptor.createFromURL(url).createImage();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		public void addListener(ILabelProviderListener listener) {
		}

		public void removeListener(ILabelProviderListener listener) {
		}
		
		public boolean isLabelProperty(Object element, String property) {
			return true;
		}
		
		public String getText(Object element) {
			String labelName="";//$NON-NLS-1$
			if(element instanceof Class) {
				labelName = ((Class<?>)element).getSimpleName();
			} else if (element instanceof java.lang.reflect.Method) {
				labelName = ReflectionUtil.getMethodLabel((java.lang.reflect.Method)element);
			} else if (element instanceof java.lang.reflect.Constructor) {
				labelName = ReflectionUtil.getConstructorLabel((java.lang.reflect.Constructor<?>)element);
			} else if (element instanceof java.lang.reflect.Field) {
				labelName = ReflectionUtil.getJavaFieldLabel((java.lang.reflect.Field)element);
			}
			return labelName;
		}
		
		public void dispose() {
			if (classImage != null)
				classImage.dispose();
			if (methpubImage != null)
				methpubImage.dispose();
			if (fieldpubImage != null)
				fieldpubImage.dispose();
		}
		
		public Image getImage(Object element) {
			if (element instanceof Class) {
				return classImage;
			} else if (element instanceof java.lang.reflect.Method || 
					   element instanceof java.lang.reflect.Constructor) {
				return methpubImage;
			} else if (element instanceof java.lang.reflect.Field) {
				return fieldpubImage;
			} else {
				return null;
			}
		}
	}//ExternalTypeLabelProvider
	
	@Override
	public void checkStateChanged(CheckStateChangedEvent event) {//ICheckStateListener
		if (event.getSource() == javaTypeViewer) {
			if (event.getChecked()) {
				// . . . check all its children
				javaTypeViewer.setSubtreeChecked(event.getElement(), true);
			} else {
				javaTypeViewer.setSubtreeChecked(event.getElement(), false);
			}

			storeSelectedClass();
		}
		
		validatePage();
	}
	
	private void storeSelectedClass() {
		List<Method> selectedMethods = handleMethodsSelected();
		List<Constructor<?>> selectedCons = handleConstructorsSelected();
		List<Field> selectedFields = handleFieldsSelected();
		
		JavaType javaType = configuration.getToBeGenerated().get(configuration.getSelectedClazz());
		if(javaType == null) {
			javaType = new JavaType();
			javaType.setSource(JavaType.SelectedType);
		}
		javaType.setFields(selectedFields);
		javaType.setConstructors(selectedCons);
		javaType.setMethods(selectedMethods);
		configuration.getToBeGenerated().put(configuration.getSelectedClazz(), javaType);
	}
	
	private List<Field> handleFieldsSelected() {
		List<Field> selectedFields = new ArrayList<Field>();
		for (Object o : javaTypeViewer.getCheckedElements()) {
			if (o instanceof Field) {
				selectedFields.add((Field)o);
			}
		}
		return selectedFields;
	}
	
	private List<Method> handleMethodsSelected() {
		List<Method> selectedMethods = new ArrayList<Method>();
		for (Object o : javaTypeViewer.getCheckedElements()) {
			if (o instanceof Method) {
				selectedMethods.add((Method)o);
			}
		}
		return selectedMethods;
	}
	
	private List<Constructor<?>> handleConstructorsSelected() {
		List<Constructor<?>> selectedCons = new ArrayList<Constructor<?>>();
		for (Object o : javaTypeViewer.getCheckedElements()) {
			if (o instanceof Constructor<?>) {
				selectedCons.add((Constructor<?>)o);
			}
		}
		return selectedCons;
	}
	
	private void validatePage() {
		memberStatus.setOK();
		JavaType javaType = configuration.getToBeGenerated().get(configuration.getSelectedClazz());
		
		if(javaType == null ) {
			memberStatus.setError(NewExternalTypeWizardMessages.ExternalTypeFromJavaPage_Validation_NoMember);
		} else {
			List<Method> selectedMethods = javaType.getMethods();
			List<Field>  selectedFields = javaType.getFields();
			List<Constructor<?>> selectedCons = javaType.getConstructors();
			
			
			if ( (selectedMethods == null || selectedMethods.isEmpty())
				  && (selectedFields == null || selectedFields.isEmpty())	
				  && (selectedCons == null || selectedCons.isEmpty())) {
				memberStatus.setError(NewExternalTypeWizardMessages.ExternalTypeFromJavaPage_Validation_NoMember);
			}
		}
		
		updateStatus(memberStatus);
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		fPageVisible = visible;
		
		updateStatus(fCurrStatus);
	}
	
	protected void updateStatus(IStatus status) {
		fCurrStatus = status;
		setPageComplete(!status.matches(IStatus.ERROR));
		if (fPageVisible) {
			StatusUtil.applyToStatusLine(this, status);	
		}
	}
}