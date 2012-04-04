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

import org.eclipse.edt.ide.ui.internal.formatting.profile.CheckControl;
import org.eclipse.edt.ide.ui.internal.formatting.profile.ComboControl;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Control;
import org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot;
import org.eclipse.edt.ide.ui.internal.formatting.profile.FormatProfiles;
import org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage;
import org.eclipse.edt.ide.ui.internal.formatting.profile.RadioControl;
import org.eclipse.edt.ide.ui.internal.formatting.profile.ReferenceControl;
import org.eclipse.edt.ide.ui.internal.formatting.profile.TextControl;
import org.eclipse.edt.ide.ui.internal.formatting.profile.TreeControl;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.impl.EStringToStringMapEntryImpl;
import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EGL Format Profile Root</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.EGLFormatProfileRootImpl#getMixed <em>Mixed</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.EGLFormatProfileRootImpl#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.EGLFormatProfileRootImpl#getXSISchemaLocation <em>XSI Schema Location</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.EGLFormatProfileRootImpl#getControl <em>Control</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.EGLFormatProfileRootImpl#getControlCheck <em>Control Check</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.EGLFormatProfileRootImpl#getControlCombo <em>Control Combo</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.EGLFormatProfileRootImpl#getControlRadio <em>Control Radio</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.EGLFormatProfileRootImpl#getControlRef <em>Control Ref</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.EGLFormatProfileRootImpl#getControlText <em>Control Text</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.EGLFormatProfileRootImpl#getControlTree <em>Control Tree</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.EGLFormatProfileRootImpl#getFormatProfiles <em>Format Profiles</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EGLFormatProfileRootImpl extends EObjectImpl implements EGLFormatProfileRoot {
	/**
	 * The cached value of the '{@link #getMixed() <em>Mixed</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMixed()
	 * @generated
	 * @ordered
	 */
	protected FeatureMap mixed = null;

	/**
	 * The cached value of the '{@link #getXMLNSPrefixMap() <em>XMLNS Prefix Map</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getXMLNSPrefixMap()
	 * @generated
	 * @ordered
	 */
	protected EMap xMLNSPrefixMap = null;

	/**
	 * The cached value of the '{@link #getXSISchemaLocation() <em>XSI Schema Location</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getXSISchemaLocation()
	 * @generated
	 * @ordered
	 */
	protected EMap xSISchemaLocation = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EGLFormatProfileRootImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return ProfilePackage.Literals.EGL_FORMAT_PROFILE_ROOT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureMap getMixed() {
		if (mixed == null) {
			mixed = new BasicFeatureMap(this, ProfilePackage.EGL_FORMAT_PROFILE_ROOT__MIXED);
		}
		return mixed;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EMap getXMLNSPrefixMap() {
		if (xMLNSPrefixMap == null) {
			xMLNSPrefixMap = new EcoreEMap(EcorePackage.Literals.ESTRING_TO_STRING_MAP_ENTRY, EStringToStringMapEntryImpl.class, this, ProfilePackage.EGL_FORMAT_PROFILE_ROOT__XMLNS_PREFIX_MAP);
		}
		return xMLNSPrefixMap;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EMap getXSISchemaLocation() {
		if (xSISchemaLocation == null) {
			xSISchemaLocation = new EcoreEMap(EcorePackage.Literals.ESTRING_TO_STRING_MAP_ENTRY, EStringToStringMapEntryImpl.class, this, ProfilePackage.EGL_FORMAT_PROFILE_ROOT__XSI_SCHEMA_LOCATION);
		}
		return xSISchemaLocation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Control getControl() {
		return (Control)getMixed().get(ProfilePackage.Literals.EGL_FORMAT_PROFILE_ROOT__CONTROL, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetControl(Control newControl, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(ProfilePackage.Literals.EGL_FORMAT_PROFILE_ROOT__CONTROL, newControl, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CheckControl getControlCheck() {
		return (CheckControl)getMixed().get(ProfilePackage.Literals.EGL_FORMAT_PROFILE_ROOT__CONTROL_CHECK, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetControlCheck(CheckControl newControlCheck, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(ProfilePackage.Literals.EGL_FORMAT_PROFILE_ROOT__CONTROL_CHECK, newControlCheck, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setControlCheck(CheckControl newControlCheck) {
		((FeatureMap.Internal)getMixed()).set(ProfilePackage.Literals.EGL_FORMAT_PROFILE_ROOT__CONTROL_CHECK, newControlCheck);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ComboControl getControlCombo() {
		return (ComboControl)getMixed().get(ProfilePackage.Literals.EGL_FORMAT_PROFILE_ROOT__CONTROL_COMBO, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetControlCombo(ComboControl newControlCombo, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(ProfilePackage.Literals.EGL_FORMAT_PROFILE_ROOT__CONTROL_COMBO, newControlCombo, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setControlCombo(ComboControl newControlCombo) {
		((FeatureMap.Internal)getMixed()).set(ProfilePackage.Literals.EGL_FORMAT_PROFILE_ROOT__CONTROL_COMBO, newControlCombo);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RadioControl getControlRadio() {
		return (RadioControl)getMixed().get(ProfilePackage.Literals.EGL_FORMAT_PROFILE_ROOT__CONTROL_RADIO, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetControlRadio(RadioControl newControlRadio, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(ProfilePackage.Literals.EGL_FORMAT_PROFILE_ROOT__CONTROL_RADIO, newControlRadio, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setControlRadio(RadioControl newControlRadio) {
		((FeatureMap.Internal)getMixed()).set(ProfilePackage.Literals.EGL_FORMAT_PROFILE_ROOT__CONTROL_RADIO, newControlRadio);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ReferenceControl getControlRef() {
		return (ReferenceControl)getMixed().get(ProfilePackage.Literals.EGL_FORMAT_PROFILE_ROOT__CONTROL_REF, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetControlRef(ReferenceControl newControlRef, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(ProfilePackage.Literals.EGL_FORMAT_PROFILE_ROOT__CONTROL_REF, newControlRef, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setControlRef(ReferenceControl newControlRef) {
		((FeatureMap.Internal)getMixed()).set(ProfilePackage.Literals.EGL_FORMAT_PROFILE_ROOT__CONTROL_REF, newControlRef);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TextControl getControlText() {
		return (TextControl)getMixed().get(ProfilePackage.Literals.EGL_FORMAT_PROFILE_ROOT__CONTROL_TEXT, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetControlText(TextControl newControlText, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(ProfilePackage.Literals.EGL_FORMAT_PROFILE_ROOT__CONTROL_TEXT, newControlText, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setControlText(TextControl newControlText) {
		((FeatureMap.Internal)getMixed()).set(ProfilePackage.Literals.EGL_FORMAT_PROFILE_ROOT__CONTROL_TEXT, newControlText);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TreeControl getControlTree() {
		return (TreeControl)getMixed().get(ProfilePackage.Literals.EGL_FORMAT_PROFILE_ROOT__CONTROL_TREE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetControlTree(TreeControl newControlTree, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(ProfilePackage.Literals.EGL_FORMAT_PROFILE_ROOT__CONTROL_TREE, newControlTree, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setControlTree(TreeControl newControlTree) {
		((FeatureMap.Internal)getMixed()).set(ProfilePackage.Literals.EGL_FORMAT_PROFILE_ROOT__CONTROL_TREE, newControlTree);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FormatProfiles getFormatProfiles() {
		return (FormatProfiles)getMixed().get(ProfilePackage.Literals.EGL_FORMAT_PROFILE_ROOT__FORMAT_PROFILES, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetFormatProfiles(FormatProfiles newFormatProfiles, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(ProfilePackage.Literals.EGL_FORMAT_PROFILE_ROOT__FORMAT_PROFILES, newFormatProfiles, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFormatProfiles(FormatProfiles newFormatProfiles) {
		((FeatureMap.Internal)getMixed()).set(ProfilePackage.Literals.EGL_FORMAT_PROFILE_ROOT__FORMAT_PROFILES, newFormatProfiles);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__MIXED:
				return ((InternalEList)getMixed()).basicRemove(otherEnd, msgs);
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__XMLNS_PREFIX_MAP:
				return ((InternalEList)getXMLNSPrefixMap()).basicRemove(otherEnd, msgs);
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__XSI_SCHEMA_LOCATION:
				return ((InternalEList)getXSISchemaLocation()).basicRemove(otherEnd, msgs);
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL:
				return basicSetControl(null, msgs);
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_CHECK:
				return basicSetControlCheck(null, msgs);
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_COMBO:
				return basicSetControlCombo(null, msgs);
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_RADIO:
				return basicSetControlRadio(null, msgs);
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_REF:
				return basicSetControlRef(null, msgs);
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_TEXT:
				return basicSetControlText(null, msgs);
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_TREE:
				return basicSetControlTree(null, msgs);
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__FORMAT_PROFILES:
				return basicSetFormatProfiles(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__MIXED:
				if (coreType) return getMixed();
				return ((FeatureMap.Internal)getMixed()).getWrapper();
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__XMLNS_PREFIX_MAP:
				if (coreType) return getXMLNSPrefixMap();
				else return getXMLNSPrefixMap().map();
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__XSI_SCHEMA_LOCATION:
				if (coreType) return getXSISchemaLocation();
				else return getXSISchemaLocation().map();
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL:
				return getControl();
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_CHECK:
				return getControlCheck();
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_COMBO:
				return getControlCombo();
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_RADIO:
				return getControlRadio();
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_REF:
				return getControlRef();
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_TEXT:
				return getControlText();
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_TREE:
				return getControlTree();
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__FORMAT_PROFILES:
				return getFormatProfiles();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__MIXED:
				((FeatureMap.Internal)getMixed()).set(newValue);
				return;
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__XMLNS_PREFIX_MAP:
				((EStructuralFeature.Setting)getXMLNSPrefixMap()).set(newValue);
				return;
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__XSI_SCHEMA_LOCATION:
				((EStructuralFeature.Setting)getXSISchemaLocation()).set(newValue);
				return;
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_CHECK:
				setControlCheck((CheckControl)newValue);
				return;
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_COMBO:
				setControlCombo((ComboControl)newValue);
				return;
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_RADIO:
				setControlRadio((RadioControl)newValue);
				return;
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_REF:
				setControlRef((ReferenceControl)newValue);
				return;
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_TEXT:
				setControlText((TextControl)newValue);
				return;
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_TREE:
				setControlTree((TreeControl)newValue);
				return;
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__FORMAT_PROFILES:
				setFormatProfiles((FormatProfiles)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eUnset(int featureID) {
		switch (featureID) {
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__MIXED:
				getMixed().clear();
				return;
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__XMLNS_PREFIX_MAP:
				getXMLNSPrefixMap().clear();
				return;
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__XSI_SCHEMA_LOCATION:
				getXSISchemaLocation().clear();
				return;
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_CHECK:
				setControlCheck((CheckControl)null);
				return;
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_COMBO:
				setControlCombo((ComboControl)null);
				return;
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_RADIO:
				setControlRadio((RadioControl)null);
				return;
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_REF:
				setControlRef((ReferenceControl)null);
				return;
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_TEXT:
				setControlText((TextControl)null);
				return;
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_TREE:
				setControlTree((TreeControl)null);
				return;
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__FORMAT_PROFILES:
				setFormatProfiles((FormatProfiles)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__MIXED:
				return mixed != null && !mixed.isEmpty();
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__XMLNS_PREFIX_MAP:
				return xMLNSPrefixMap != null && !xMLNSPrefixMap.isEmpty();
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__XSI_SCHEMA_LOCATION:
				return xSISchemaLocation != null && !xSISchemaLocation.isEmpty();
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL:
				return getControl() != null;
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_CHECK:
				return getControlCheck() != null;
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_COMBO:
				return getControlCombo() != null;
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_RADIO:
				return getControlRadio() != null;
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_REF:
				return getControlRef() != null;
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_TEXT:
				return getControlText() != null;
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__CONTROL_TREE:
				return getControlTree() != null;
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT__FORMAT_PROFILES:
				return getFormatProfiles() != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (mixed: ");
		result.append(mixed);
		result.append(')');
		return result.toString();
	}

} //EGLFormatProfileRootImpl
