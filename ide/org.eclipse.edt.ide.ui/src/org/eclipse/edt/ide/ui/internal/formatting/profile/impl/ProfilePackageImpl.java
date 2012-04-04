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
package org.eclipse.edt.ide.ui.internal.formatting.profile.impl;

import static org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage.SETTING;

import org.eclipse.edt.ide.ui.internal.formatting.profile.Category;
import org.eclipse.edt.ide.ui.internal.formatting.profile.CheckControl;
import org.eclipse.edt.ide.ui.internal.formatting.profile.ComboControl;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Control;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Controls;
import org.eclipse.edt.ide.ui.internal.formatting.profile.DefaultProfile;
import org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot;
import org.eclipse.edt.ide.ui.internal.formatting.profile.FormatProfiles;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Group;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Preference;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Preview;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Profile;
import org.eclipse.edt.ide.ui.internal.formatting.profile.ProfileFactory;
import org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage;
import org.eclipse.edt.ide.ui.internal.formatting.profile.RadioControl;
import org.eclipse.edt.ide.ui.internal.formatting.profile.ReferenceControl;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Setting;
import org.eclipse.edt.ide.ui.internal.formatting.profile.TextControl;
import org.eclipse.edt.ide.ui.internal.formatting.profile.TreeControl;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ProfilePackageImpl extends EPackageImpl implements ProfilePackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass categoryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass checkControlEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass comboControlEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass controlEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass controlsEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass defaultProfileEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eglFormatProfileRootEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass formatProfilesEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass groupEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass preferenceEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass previewEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass profileEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass radioControlEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass referenceControlEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass settingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass textControlEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass treeControlEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private ProfilePackageImpl() {
		super(eNS_URI, ProfileFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this
	 * model, and for any others upon which it depends.  Simple
	 * dependencies are satisfied by calling this method on all
	 * dependent packages before doing anything else.  This method drives
	 * initialization for interdependent packages directly, in parallel
	 * with this package, itself.
	 * <p>Of this package and its interdependencies, all packages which
	 * have not yet been registered by their URI values are first created
	 * and registered.  The packages are then initialized in two steps:
	 * meta-model objects for all of the packages are created before any
	 * are initialized, since one package's meta-model objects may refer to
	 * those of another.
	 * <p>Invocation of this method will not affect any packages that have
	 * already been initialized.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static ProfilePackage init() {
		if (isInited) return (ProfilePackage)EPackage.Registry.INSTANCE.getEPackage(ProfilePackage.eNS_URI);

		// Obtain or create and register package
		ProfilePackageImpl theProfilePackage = (ProfilePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof ProfilePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new ProfilePackageImpl());

		isInited = true;

		// Initialize simple dependencies
		XMLTypePackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theProfilePackage.createPackageContents();

		// Initialize created meta-data
		theProfilePackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theProfilePackage.freeze();

		return theProfilePackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getCategory() {
		return categoryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getCategory_Preview() {
		return (EReference)categoryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getCategory_Group() {
		return (EReference)categoryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCategory_Display() {
		return (EAttribute)categoryEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCategory_Id() {
		return (EAttribute)categoryEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getCheckControl() {
		return checkControlEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getComboControl() {
		return comboControlEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getComboControl_Choices() {
		return (EAttribute)comboControlEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getControl() {
		return controlEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getControl_Class() {
		return (EAttribute)controlEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getControl_Name() {
		return (EAttribute)controlEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getControl_Style() {
		return (EAttribute)controlEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getControls() {
		return controlsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getControls_ControlGroup() {
		return (EAttribute)controlsEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getControls_Control() {
		return (EReference)controlsEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDefaultProfile() {
		return defaultProfileEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDefaultProfile_Preview() {
		return (EReference)defaultProfileEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDefaultProfile_Controls() {
		return (EReference)defaultProfileEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDefaultProfile_Category() {
		return (EReference)defaultProfileEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDefaultProfile_Name() {
		return (EAttribute)defaultProfileEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEGLFormatProfileRoot() {
		return eglFormatProfileRootEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEGLFormatProfileRoot_Mixed() {
		return (EAttribute)eglFormatProfileRootEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLFormatProfileRoot_XMLNSPrefixMap() {
		return (EReference)eglFormatProfileRootEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLFormatProfileRoot_XSISchemaLocation() {
		return (EReference)eglFormatProfileRootEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLFormatProfileRoot_Control() {
		return (EReference)eglFormatProfileRootEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLFormatProfileRoot_ControlCheck() {
		return (EReference)eglFormatProfileRootEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLFormatProfileRoot_ControlCombo() {
		return (EReference)eglFormatProfileRootEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLFormatProfileRoot_ControlRadio() {
		return (EReference)eglFormatProfileRootEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLFormatProfileRoot_ControlRef() {
		return (EReference)eglFormatProfileRootEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLFormatProfileRoot_ControlText() {
		return (EReference)eglFormatProfileRootEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLFormatProfileRoot_ControlTree() {
		return (EReference)eglFormatProfileRootEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLFormatProfileRoot_FormatProfiles() {
		return (EReference)eglFormatProfileRootEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getFormatProfiles() {
		return formatProfilesEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getFormatProfiles_DefaultProfile() {
		return (EReference)formatProfilesEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getFormatProfiles_Profile() {
		return (EReference)formatProfilesEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getFormatProfiles_Selection() {
		return (EAttribute)formatProfilesEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getFormatProfiles_Version() {
		return (EAttribute)formatProfilesEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getGroup() {
		return groupEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getGroup_Pref() {
		return (EReference)groupEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGroup_Display() {
		return (EAttribute)groupEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getPreference() {
		return preferenceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getPreference_Preview() {
		return (EReference)preferenceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPreference_ControlGroup() {
		return (EAttribute)preferenceEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getPreference_Control() {
		return (EReference)preferenceEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPreference_AltDisplay() {
		return (EAttribute)preferenceEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPreference_Display() {
		return (EAttribute)preferenceEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPreference_Id() {
		return (EAttribute)preferenceEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPreference_Value() {
		return (EAttribute)preferenceEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPreference_Visible() {
		return (EAttribute)preferenceEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getPreview() {
		return previewEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPreview_Code() {
		return (EAttribute)previewEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPreview_Ref() {
		return (EAttribute)previewEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getProfile() {
		return profileEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getProfile_Setting() {
		return (EReference)profileEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getProfile_Base() {
		return (EAttribute)profileEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getProfile_IsBuildIn() {
		return (EAttribute)profileEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getProfile_Name() {
		return (EAttribute)profileEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRadioControl() {
		return radioControlEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRadioControl_Choices() {
		return (EAttribute)radioControlEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getReferenceControl() {
		return referenceControlEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getReferenceControl_Ref() {
		return (EAttribute)referenceControlEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSetting() {
		return settingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSetting_Category() {
		return (EAttribute)settingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSetting_Pref() {
		return (EAttribute)settingEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSetting_Value() {
		return (EAttribute)settingEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getTextControl() {
		return textControlEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getTreeControl() {
		return treeControlEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ProfileFactory getProfileFactory() {
		return (ProfileFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		categoryEClass = createEClass(CATEGORY);
		createEReference(categoryEClass, CATEGORY__PREVIEW);
		createEReference(categoryEClass, CATEGORY__GROUP);
		createEAttribute(categoryEClass, CATEGORY__DISPLAY);
		createEAttribute(categoryEClass, CATEGORY__ID);

		checkControlEClass = createEClass(CHECK_CONTROL);

		comboControlEClass = createEClass(COMBO_CONTROL);
		createEAttribute(comboControlEClass, COMBO_CONTROL__CHOICES);

		controlEClass = createEClass(CONTROL);
		createEAttribute(controlEClass, CONTROL__CLASS);
		createEAttribute(controlEClass, CONTROL__NAME);
		createEAttribute(controlEClass, CONTROL__STYLE);

		controlsEClass = createEClass(CONTROLS);
		createEAttribute(controlsEClass, CONTROLS__CONTROL_GROUP);
		createEReference(controlsEClass, CONTROLS__CONTROL);

		defaultProfileEClass = createEClass(DEFAULT_PROFILE);
		createEReference(defaultProfileEClass, DEFAULT_PROFILE__PREVIEW);
		createEReference(defaultProfileEClass, DEFAULT_PROFILE__CONTROLS);
		createEReference(defaultProfileEClass, DEFAULT_PROFILE__CATEGORY);
		createEAttribute(defaultProfileEClass, DEFAULT_PROFILE__NAME);

		eglFormatProfileRootEClass = createEClass(EGL_FORMAT_PROFILE_ROOT);
		createEAttribute(eglFormatProfileRootEClass, EGL_FORMAT_PROFILE_ROOT__MIXED);
		createEReference(eglFormatProfileRootEClass, EGL_FORMAT_PROFILE_ROOT__XMLNS_PREFIX_MAP);
		createEReference(eglFormatProfileRootEClass, EGL_FORMAT_PROFILE_ROOT__XSI_SCHEMA_LOCATION);
		createEReference(eglFormatProfileRootEClass, EGL_FORMAT_PROFILE_ROOT__CONTROL);
		createEReference(eglFormatProfileRootEClass, EGL_FORMAT_PROFILE_ROOT__CONTROL_CHECK);
		createEReference(eglFormatProfileRootEClass, EGL_FORMAT_PROFILE_ROOT__CONTROL_COMBO);
		createEReference(eglFormatProfileRootEClass, EGL_FORMAT_PROFILE_ROOT__CONTROL_RADIO);
		createEReference(eglFormatProfileRootEClass, EGL_FORMAT_PROFILE_ROOT__CONTROL_REF);
		createEReference(eglFormatProfileRootEClass, EGL_FORMAT_PROFILE_ROOT__CONTROL_TEXT);
		createEReference(eglFormatProfileRootEClass, EGL_FORMAT_PROFILE_ROOT__CONTROL_TREE);
		createEReference(eglFormatProfileRootEClass, EGL_FORMAT_PROFILE_ROOT__FORMAT_PROFILES);

		formatProfilesEClass = createEClass(FORMAT_PROFILES);
		createEReference(formatProfilesEClass, FORMAT_PROFILES__DEFAULT_PROFILE);
		createEReference(formatProfilesEClass, FORMAT_PROFILES__PROFILE);
		createEAttribute(formatProfilesEClass, FORMAT_PROFILES__SELECTION);
		createEAttribute(formatProfilesEClass, FORMAT_PROFILES__VERSION);

		groupEClass = createEClass(GROUP);
		createEReference(groupEClass, GROUP__PREF);
		createEAttribute(groupEClass, GROUP__DISPLAY);

		preferenceEClass = createEClass(PREFERENCE);
		createEReference(preferenceEClass, PREFERENCE__PREVIEW);
		createEAttribute(preferenceEClass, PREFERENCE__CONTROL_GROUP);
		createEReference(preferenceEClass, PREFERENCE__CONTROL);
		createEAttribute(preferenceEClass, PREFERENCE__ALT_DISPLAY);
		createEAttribute(preferenceEClass, PREFERENCE__DISPLAY);
		createEAttribute(preferenceEClass, PREFERENCE__ID);
		createEAttribute(preferenceEClass, PREFERENCE__VALUE);
		createEAttribute(preferenceEClass, PREFERENCE__VISIBLE);

		previewEClass = createEClass(PREVIEW);
		createEAttribute(previewEClass, PREVIEW__CODE);
		createEAttribute(previewEClass, PREVIEW__REF);

		profileEClass = createEClass(PROFILE);
		createEReference(profileEClass, PROFILE__SETTING);
		createEAttribute(profileEClass, PROFILE__BASE);
		createEAttribute(profileEClass, PROFILE__IS_BUILD_IN);
		createEAttribute(profileEClass, PROFILE__NAME);

		radioControlEClass = createEClass(RADIO_CONTROL);
		createEAttribute(radioControlEClass, RADIO_CONTROL__CHOICES);

		referenceControlEClass = createEClass(REFERENCE_CONTROL);
		createEAttribute(referenceControlEClass, REFERENCE_CONTROL__REF);

		settingEClass = createEClass(SETTING);
		createEAttribute(settingEClass, SETTING__CATEGORY);
		createEAttribute(settingEClass, SETTING__PREF);
		createEAttribute(settingEClass, SETTING__VALUE);

		textControlEClass = createEClass(TEXT_CONTROL);

		treeControlEClass = createEClass(TREE_CONTROL);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		XMLTypePackage theXMLTypePackage = (XMLTypePackage)EPackage.Registry.INSTANCE.getEPackage(XMLTypePackage.eNS_URI);

		// Add supertypes to classes
		checkControlEClass.getESuperTypes().add(this.getControl());
		comboControlEClass.getESuperTypes().add(this.getControl());
		radioControlEClass.getESuperTypes().add(this.getControl());
		referenceControlEClass.getESuperTypes().add(this.getControl());
		textControlEClass.getESuperTypes().add(this.getControl());
		treeControlEClass.getESuperTypes().add(this.getControl());

		// Initialize classes and features; add operations and parameters
		initEClass(categoryEClass, Category.class, "Category", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getCategory_Preview(), this.getPreview(), null, "preview", null, 0, 1, Category.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCategory_Group(), this.getGroup(), null, "group", null, 0, -1, Category.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCategory_Display(), theXMLTypePackage.getString(), "display", null, 0, 1, Category.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCategory_Id(), theXMLTypePackage.getNCName(), "id", null, 0, 1, Category.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(checkControlEClass, CheckControl.class, "CheckControl", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(comboControlEClass, ComboControl.class, "ComboControl", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getComboControl_Choices(), theXMLTypePackage.getString(), "choices", null, 0, 1, ComboControl.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(controlEClass, Control.class, "Control", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getControl_Class(), theXMLTypePackage.getString(), "class", null, 0, 1, Control.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getControl_Name(), theXMLTypePackage.getNCName(), "name", null, 0, 1, Control.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getControl_Style(), theXMLTypePackage.getString(), "style", null, 0, 1, Control.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(controlsEClass, Controls.class, "Controls", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getControls_ControlGroup(), ecorePackage.getEFeatureMapEntry(), "controlGroup", null, 0, -1, Controls.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getControls_Control(), this.getControl(), null, "control", null, 0, -1, Controls.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		initEClass(defaultProfileEClass, DefaultProfile.class, "DefaultProfile", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDefaultProfile_Preview(), this.getPreview(), null, "preview", null, 0, 1, DefaultProfile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDefaultProfile_Controls(), this.getControls(), null, "controls", null, 0, 1, DefaultProfile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDefaultProfile_Category(), this.getCategory(), null, "category", null, 0, -1, DefaultProfile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDefaultProfile_Name(), theXMLTypePackage.getString(), "name", null, 1, 1, DefaultProfile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eglFormatProfileRootEClass, EGLFormatProfileRoot.class, "EGLFormatProfileRoot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getEGLFormatProfileRoot_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 0, -1, null, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEGLFormatProfileRoot_XMLNSPrefixMap(), ecorePackage.getEStringToStringMapEntry(), null, "xMLNSPrefixMap", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEGLFormatProfileRoot_XSISchemaLocation(), ecorePackage.getEStringToStringMapEntry(), null, "xSISchemaLocation", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEGLFormatProfileRoot_Control(), this.getControl(), null, "control", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEGLFormatProfileRoot_ControlCheck(), this.getCheckControl(), null, "controlCheck", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEGLFormatProfileRoot_ControlCombo(), this.getComboControl(), null, "controlCombo", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEGLFormatProfileRoot_ControlRadio(), this.getRadioControl(), null, "controlRadio", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEGLFormatProfileRoot_ControlRef(), this.getReferenceControl(), null, "controlRef", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEGLFormatProfileRoot_ControlText(), this.getTextControl(), null, "controlText", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEGLFormatProfileRoot_ControlTree(), this.getTreeControl(), null, "controlTree", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEGLFormatProfileRoot_FormatProfiles(), this.getFormatProfiles(), null, "formatProfiles", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		initEClass(formatProfilesEClass, FormatProfiles.class, "FormatProfiles", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getFormatProfiles_DefaultProfile(), this.getDefaultProfile(), null, "defaultProfile", null, 0, 1, FormatProfiles.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getFormatProfiles_Profile(), this.getProfile(), null, "profile", null, 0, -1, FormatProfiles.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFormatProfiles_Selection(), theXMLTypePackage.getString(), "selection", null, 0, 1, FormatProfiles.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFormatProfiles_Version(), theXMLTypePackage.getString(), "version", null, 0, 1, FormatProfiles.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(groupEClass, Group.class, "Group", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getGroup_Pref(), this.getPreference(), null, "pref", null, 0, -1, Group.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGroup_Display(), theXMLTypePackage.getString(), "display", null, 0, 1, Group.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(preferenceEClass, Preference.class, "Preference", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getPreference_Preview(), this.getPreview(), null, "preview", null, 0, 1, Preference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPreference_ControlGroup(), ecorePackage.getEFeatureMapEntry(), "controlGroup", null, 0, 1, Preference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPreference_Control(), this.getControl(), null, "control", null, 0, 1, Preference.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getPreference_AltDisplay(), theXMLTypePackage.getString(), "altDisplay", null, 0, 1, Preference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPreference_Display(), theXMLTypePackage.getString(), "display", null, 0, 1, Preference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPreference_Id(), theXMLTypePackage.getNCName(), "id", null, 0, 1, Preference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPreference_Value(), theXMLTypePackage.getString(), "value", null, 0, 1, Preference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPreference_Visible(), theXMLTypePackage.getBoolean(), "visible", "true", 0, 1, Preference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(previewEClass, Preview.class, "Preview", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getPreview_Code(), theXMLTypePackage.getString(), "code", null, 0, 1, Preview.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPreview_Ref(), theXMLTypePackage.getString(), "ref", null, 0, 1, Preview.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(profileEClass, Profile.class, "Profile", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getProfile_Setting(), this.getSetting(), null, "setting", null, 0, -1, Profile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getProfile_Base(), theXMLTypePackage.getNCName(), "base", null, 0, 1, Profile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getProfile_IsBuildIn(), theXMLTypePackage.getBoolean(), "isBuildIn", "false", 0, 1, Profile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getProfile_Name(), theXMLTypePackage.getString(), "name", null, 1, 1, Profile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(radioControlEClass, RadioControl.class, "RadioControl", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getRadioControl_Choices(), theXMLTypePackage.getString(), "choices", null, 0, 1, RadioControl.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(referenceControlEClass, ReferenceControl.class, "ReferenceControl", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getReferenceControl_Ref(), theXMLTypePackage.getNCName(), "ref", null, 0, 1, ReferenceControl.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(settingEClass, Setting.class, "Setting", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSetting_Category(), theXMLTypePackage.getString(), "category", null, 1, 1, Setting.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSetting_Pref(), theXMLTypePackage.getString(), "pref", null, 1, 1, Setting.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSetting_Value(), theXMLTypePackage.getString(), "value", null, 1, 1, Setting.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(textControlEClass, TextControl.class, "TextControl", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(treeControlEClass, TreeControl.class, "TreeControl", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// http:///org/eclipse/emf/ecore/util/ExtendedMetaData
		createExtendedMetaDataAnnotations();
	}

	/**
	 * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createExtendedMetaDataAnnotations() {
		String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";		
		addAnnotation
		  (categoryEClass, 
		   source, 
		   new String[] {
			 "name", "Category",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getCategory_Preview(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "preview"
		   });		
		addAnnotation
		  (getCategory_Group(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "group"
		   });		
		addAnnotation
		  (getCategory_Display(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "display"
		   });		
		addAnnotation
		  (getCategory_Id(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "id"
		   });		
		addAnnotation
		  (checkControlEClass, 
		   source, 
		   new String[] {
			 "name", "CheckControl",
			 "kind", "empty"
		   });		
		addAnnotation
		  (comboControlEClass, 
		   source, 
		   new String[] {
			 "name", "ComboControl",
			 "kind", "empty"
		   });		
		addAnnotation
		  (getComboControl_Choices(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "choices"
		   });		
		addAnnotation
		  (controlEClass, 
		   source, 
		   new String[] {
			 "name", "Control",
			 "kind", "empty"
		   });		
		addAnnotation
		  (getControl_Class(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "class"
		   });		
		addAnnotation
		  (getControl_Name(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "name"
		   });		
		addAnnotation
		  (getControl_Style(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "style"
		   });		
		addAnnotation
		  (controlsEClass, 
		   source, 
		   new String[] {
			 "name", "Controls",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getControls_ControlGroup(), 
		   source, 
		   new String[] {
			 "kind", "group",
			 "name", "control:group",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getControls_Control(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "control",
			 "namespace", "##targetNamespace",
			 "group", "control:group"
		   });		
		addAnnotation
		  (defaultProfileEClass, 
		   source, 
		   new String[] {
			 "name", "DefaultProfile",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getDefaultProfile_Preview(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "preview"
		   });		
		addAnnotation
		  (getDefaultProfile_Controls(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "controls"
		   });		
		addAnnotation
		  (getDefaultProfile_Category(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "category"
		   });		
		addAnnotation
		  (getDefaultProfile_Name(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "name"
		   });		
		addAnnotation
		  (eglFormatProfileRootEClass, 
		   source, 
		   new String[] {
			 "name", "",
			 "kind", "mixed"
		   });		
		addAnnotation
		  (getEGLFormatProfileRoot_Mixed(), 
		   source, 
		   new String[] {
			 "kind", "elementWildcard",
			 "name", ":mixed"
		   });		
		addAnnotation
		  (getEGLFormatProfileRoot_XMLNSPrefixMap(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "xmlns:prefix"
		   });		
		addAnnotation
		  (getEGLFormatProfileRoot_XSISchemaLocation(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "xsi:schemaLocation"
		   });		
		addAnnotation
		  (getEGLFormatProfileRoot_Control(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "control",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getEGLFormatProfileRoot_ControlCheck(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "control.check",
			 "namespace", "##targetNamespace",
			 "affiliation", "control"
		   });		
		addAnnotation
		  (getEGLFormatProfileRoot_ControlCombo(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "control.combo",
			 "namespace", "##targetNamespace",
			 "affiliation", "control"
		   });		
		addAnnotation
		  (getEGLFormatProfileRoot_ControlRadio(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "control.radio",
			 "namespace", "##targetNamespace",
			 "affiliation", "control"
		   });		
		addAnnotation
		  (getEGLFormatProfileRoot_ControlRef(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "control.ref",
			 "namespace", "##targetNamespace",
			 "affiliation", "control"
		   });		
		addAnnotation
		  (getEGLFormatProfileRoot_ControlText(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "control.text",
			 "namespace", "##targetNamespace",
			 "affiliation", "control"
		   });		
		addAnnotation
		  (getEGLFormatProfileRoot_ControlTree(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "control.tree",
			 "namespace", "##targetNamespace",
			 "affiliation", "control"
		   });		
		addAnnotation
		  (getEGLFormatProfileRoot_FormatProfiles(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "format_profiles",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (formatProfilesEClass, 
		   source, 
		   new String[] {
			 "name", "FormatProfiles",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getFormatProfiles_DefaultProfile(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "defaultProfile"
		   });		
		addAnnotation
		  (getFormatProfiles_Profile(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "profile"
		   });		
		addAnnotation
		  (getFormatProfiles_Selection(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "selection"
		   });		
		addAnnotation
		  (getFormatProfiles_Version(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "version"
		   });		
		addAnnotation
		  (groupEClass, 
		   source, 
		   new String[] {
			 "name", "Group",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getGroup_Pref(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "pref"
		   });		
		addAnnotation
		  (getGroup_Display(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "display"
		   });		
		addAnnotation
		  (preferenceEClass, 
		   source, 
		   new String[] {
			 "name", "Preference",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getPreference_Preview(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "preview"
		   });		
		addAnnotation
		  (getPreference_ControlGroup(), 
		   source, 
		   new String[] {
			 "kind", "group",
			 "name", "control:group",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getPreference_Control(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "control",
			 "namespace", "##targetNamespace",
			 "group", "control:group"
		   });		
		addAnnotation
		  (getPreference_AltDisplay(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "altDisplay"
		   });		
		addAnnotation
		  (getPreference_Display(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "display"
		   });		
		addAnnotation
		  (getPreference_Id(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "id"
		   });		
		addAnnotation
		  (getPreference_Value(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "value"
		   });		
		addAnnotation
		  (getPreference_Visible(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "visible"
		   });		
		addAnnotation
		  (previewEClass, 
		   source, 
		   new String[] {
			 "name", "Preview",
			 "kind", "empty"
		   });		
		addAnnotation
		  (getPreview_Code(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "code"
		   });		
		addAnnotation
		  (getPreview_Ref(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "ref"
		   });		
		addAnnotation
		  (profileEClass, 
		   source, 
		   new String[] {
			 "name", "Profile",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getProfile_Setting(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "setting"
		   });		
		addAnnotation
		  (getProfile_Base(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "base"
		   });		
		addAnnotation
		  (getProfile_IsBuildIn(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "isBuildIn"
		   });		
		addAnnotation
		  (getProfile_Name(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "name"
		   });		
		addAnnotation
		  (radioControlEClass, 
		   source, 
		   new String[] {
			 "name", "RadioControl",
			 "kind", "empty"
		   });		
		addAnnotation
		  (getRadioControl_Choices(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "choices"
		   });		
		addAnnotation
		  (referenceControlEClass, 
		   source, 
		   new String[] {
			 "name", "ReferenceControl",
			 "kind", "empty"
		   });		
		addAnnotation
		  (getReferenceControl_Ref(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "ref"
		   });		
		addAnnotation
		  (settingEClass, 
		   source, 
		   new String[] {
			 "name", "Setting",
			 "kind", "empty"
		   });		
		addAnnotation
		  (getSetting_Category(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "category"
		   });		
		addAnnotation
		  (getSetting_Pref(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "pref"
		   });		
		addAnnotation
		  (getSetting_Value(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "value"
		   });		
		addAnnotation
		  (textControlEClass, 
		   source, 
		   new String[] {
			 "name", "TextControl",
			 "kind", "empty"
		   });		
		addAnnotation
		  (treeControlEClass, 
		   source, 
		   new String[] {
			 "name", "TreeControl",
			 "kind", "empty"
		   });
	}

} //ProfilePackageImpl
