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
package org.eclipse.edt.ide.ui.internal.formatting.profile.util;

import java.util.List;

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
import org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage;
import org.eclipse.edt.ide.ui.internal.formatting.profile.RadioControl;
import org.eclipse.edt.ide.ui.internal.formatting.profile.ReferenceControl;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Setting;
import org.eclipse.edt.ide.ui.internal.formatting.profile.TextControl;
import org.eclipse.edt.ide.ui.internal.formatting.profile.TreeControl;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage
 * @generated
 */
public class ProfileSwitch {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static ProfilePackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ProfileSwitch() {
		if (modelPackage == null) {
			modelPackage = ProfilePackage.eINSTANCE;
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	public Object doSwitch(EObject theEObject) {
		return doSwitch(theEObject.eClass(), theEObject);
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected Object doSwitch(EClass theEClass, EObject theEObject) {
		if (theEClass.eContainer() == modelPackage) {
			return doSwitch(theEClass.getClassifierID(), theEObject);
		}
		else {
			List eSuperTypes = theEClass.getESuperTypes();
			return
				eSuperTypes.isEmpty() ?
					defaultCase(theEObject) :
					doSwitch((EClass)eSuperTypes.get(0), theEObject);
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected Object doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case ProfilePackage.CATEGORY: {
				Category category = (Category)theEObject;
				Object result = caseCategory(category);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ProfilePackage.CHECK_CONTROL: {
				CheckControl checkControl = (CheckControl)theEObject;
				Object result = caseCheckControl(checkControl);
				if (result == null) result = caseControl(checkControl);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ProfilePackage.COMBO_CONTROL: {
				ComboControl comboControl = (ComboControl)theEObject;
				Object result = caseComboControl(comboControl);
				if (result == null) result = caseControl(comboControl);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ProfilePackage.CONTROL: {
				Control control = (Control)theEObject;
				Object result = caseControl(control);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ProfilePackage.CONTROLS: {
				Controls controls = (Controls)theEObject;
				Object result = caseControls(controls);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ProfilePackage.DEFAULT_PROFILE: {
				DefaultProfile defaultProfile = (DefaultProfile)theEObject;
				Object result = caseDefaultProfile(defaultProfile);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT: {
				EGLFormatProfileRoot eglFormatProfileRoot = (EGLFormatProfileRoot)theEObject;
				Object result = caseEGLFormatProfileRoot(eglFormatProfileRoot);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ProfilePackage.FORMAT_PROFILES: {
				FormatProfiles formatProfiles = (FormatProfiles)theEObject;
				Object result = caseFormatProfiles(formatProfiles);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ProfilePackage.GROUP: {
				Group group = (Group)theEObject;
				Object result = caseGroup(group);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ProfilePackage.PREFERENCE: {
				Preference preference = (Preference)theEObject;
				Object result = casePreference(preference);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ProfilePackage.PREVIEW: {
				Preview preview = (Preview)theEObject;
				Object result = casePreview(preview);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ProfilePackage.PROFILE: {
				Profile profile = (Profile)theEObject;
				Object result = caseProfile(profile);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ProfilePackage.RADIO_CONTROL: {
				RadioControl radioControl = (RadioControl)theEObject;
				Object result = caseRadioControl(radioControl);
				if (result == null) result = caseControl(radioControl);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ProfilePackage.REFERENCE_CONTROL: {
				ReferenceControl referenceControl = (ReferenceControl)theEObject;
				Object result = caseReferenceControl(referenceControl);
				if (result == null) result = caseControl(referenceControl);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ProfilePackage.SETTING: {
				Setting setting = (Setting)theEObject;
				Object result = caseSetting(setting);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ProfilePackage.TEXT_CONTROL: {
				TextControl textControl = (TextControl)theEObject;
				Object result = caseTextControl(textControl);
				if (result == null) result = caseControl(textControl);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ProfilePackage.TREE_CONTROL: {
				TreeControl treeControl = (TreeControl)theEObject;
				Object result = caseTreeControl(treeControl);
				if (result == null) result = caseControl(treeControl);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpretting the object as an instance of '<em>Category</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpretting the object as an instance of '<em>Category</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseCategory(Category object) {
		return null;
	}

	/**
	 * Returns the result of interpretting the object as an instance of '<em>Check Control</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpretting the object as an instance of '<em>Check Control</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseCheckControl(CheckControl object) {
		return null;
	}

	/**
	 * Returns the result of interpretting the object as an instance of '<em>Combo Control</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpretting the object as an instance of '<em>Combo Control</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseComboControl(ComboControl object) {
		return null;
	}

	/**
	 * Returns the result of interpretting the object as an instance of '<em>Control</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpretting the object as an instance of '<em>Control</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseControl(Control object) {
		return null;
	}

	/**
	 * Returns the result of interpretting the object as an instance of '<em>Controls</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpretting the object as an instance of '<em>Controls</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseControls(Controls object) {
		return null;
	}

	/**
	 * Returns the result of interpretting the object as an instance of '<em>Default Profile</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpretting the object as an instance of '<em>Default Profile</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseDefaultProfile(DefaultProfile object) {
		return null;
	}

	/**
	 * Returns the result of interpretting the object as an instance of '<em>EGL Format Profile Root</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpretting the object as an instance of '<em>EGL Format Profile Root</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseEGLFormatProfileRoot(EGLFormatProfileRoot object) {
		return null;
	}

	/**
	 * Returns the result of interpretting the object as an instance of '<em>Format Profiles</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpretting the object as an instance of '<em>Format Profiles</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseFormatProfiles(FormatProfiles object) {
		return null;
	}

	/**
	 * Returns the result of interpretting the object as an instance of '<em>Group</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpretting the object as an instance of '<em>Group</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseGroup(Group object) {
		return null;
	}

	/**
	 * Returns the result of interpretting the object as an instance of '<em>Preference</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpretting the object as an instance of '<em>Preference</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object casePreference(Preference object) {
		return null;
	}

	/**
	 * Returns the result of interpretting the object as an instance of '<em>Preview</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpretting the object as an instance of '<em>Preview</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object casePreview(Preview object) {
		return null;
	}

	/**
	 * Returns the result of interpretting the object as an instance of '<em>Profile</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpretting the object as an instance of '<em>Profile</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseProfile(Profile object) {
		return null;
	}

	/**
	 * Returns the result of interpretting the object as an instance of '<em>Radio Control</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpretting the object as an instance of '<em>Radio Control</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseRadioControl(RadioControl object) {
		return null;
	}

	/**
	 * Returns the result of interpretting the object as an instance of '<em>Reference Control</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpretting the object as an instance of '<em>Reference Control</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseReferenceControl(ReferenceControl object) {
		return null;
	}

	/**
	 * Returns the result of interpretting the object as an instance of '<em>Setting</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpretting the object as an instance of '<em>Setting</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseSetting(Setting object) {
		return null;
	}

	/**
	 * Returns the result of interpretting the object as an instance of '<em>Text Control</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpretting the object as an instance of '<em>Text Control</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseTextControl(TextControl object) {
		return null;
	}

	/**
	 * Returns the result of interpretting the object as an instance of '<em>Tree Control</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpretting the object as an instance of '<em>Tree Control</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public Object caseTreeControl(TreeControl object) {
		return null;
	}

	/**
	 * Returns the result of interpretting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpretting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	public Object defaultCase(EObject object) {
		return null;
	}

} //ProfileSwitch
