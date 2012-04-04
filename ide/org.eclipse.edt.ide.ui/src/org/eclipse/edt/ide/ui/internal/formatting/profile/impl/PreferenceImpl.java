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

import org.eclipse.edt.ide.ui.internal.formatting.profile.Control;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Preference;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Preview;
import org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Preference</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.PreferenceImpl#getPreview <em>Preview</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.PreferenceImpl#getControlGroup <em>Control Group</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.PreferenceImpl#getControl <em>Control</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.PreferenceImpl#getAltDisplay <em>Alt Display</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.PreferenceImpl#getDisplay <em>Display</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.PreferenceImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.PreferenceImpl#getValue <em>Value</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.PreferenceImpl#isVisible <em>Visible</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PreferenceImpl extends EObjectImpl implements Preference {
	/**
	 * The cached value of the '{@link #getPreview() <em>Preview</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPreview()
	 * @generated
	 * @ordered
	 */
	protected Preview preview = null;

	/**
	 * The cached value of the '{@link #getControlGroup() <em>Control Group</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getControlGroup()
	 * @generated
	 * @ordered
	 */
	protected FeatureMap controlGroup = null;

	/**
	 * The default value of the '{@link #getAltDisplay() <em>Alt Display</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAltDisplay()
	 * @generated
	 * @ordered
	 */
	protected static final String ALT_DISPLAY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getAltDisplay() <em>Alt Display</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAltDisplay()
	 * @generated
	 * @ordered
	 */
	protected String altDisplay = ALT_DISPLAY_EDEFAULT;

	/**
	 * The default value of the '{@link #getDisplay() <em>Display</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDisplay()
	 * @generated
	 * @ordered
	 */
	protected static final String DISPLAY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDisplay() <em>Display</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDisplay()
	 * @generated
	 * @ordered
	 */
	protected String display = DISPLAY_EDEFAULT;

	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getValue() <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValue()
	 * @generated
	 * @ordered
	 */
	protected static final String VALUE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getValue() <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValue()
	 * @generated
	 * @ordered
	 */
	protected String value = VALUE_EDEFAULT;

	/**
	 * The default value of the '{@link #isVisible() <em>Visible</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isVisible()
	 * @generated
	 * @ordered
	 */
	protected static final boolean VISIBLE_EDEFAULT = true;

	/**
	 * The cached value of the '{@link #isVisible() <em>Visible</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isVisible()
	 * @generated
	 * @ordered
	 */
	protected boolean visible = VISIBLE_EDEFAULT;

	/**
	 * This is true if the Visible attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean visibleESet = false;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PreferenceImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return ProfilePackage.Literals.PREFERENCE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Preview getPreview() {
		return preview;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetPreview(Preview newPreview, NotificationChain msgs) {
		Preview oldPreview = preview;
		preview = newPreview;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ProfilePackage.PREFERENCE__PREVIEW, oldPreview, newPreview);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPreview(Preview newPreview) {
		if (newPreview != preview) {
			NotificationChain msgs = null;
			if (preview != null)
				msgs = ((InternalEObject)preview).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ProfilePackage.PREFERENCE__PREVIEW, null, msgs);
			if (newPreview != null)
				msgs = ((InternalEObject)newPreview).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ProfilePackage.PREFERENCE__PREVIEW, null, msgs);
			msgs = basicSetPreview(newPreview, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProfilePackage.PREFERENCE__PREVIEW, newPreview, newPreview));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureMap getControlGroup() {
		if (controlGroup == null) {
			controlGroup = new BasicFeatureMap(this, ProfilePackage.PREFERENCE__CONTROL_GROUP);
		}
		return controlGroup;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Control getControl() {
		return (Control)getControlGroup().get(ProfilePackage.Literals.PREFERENCE__CONTROL, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetControl(Control newControl, NotificationChain msgs) {
		return ((FeatureMap.Internal)getControlGroup()).basicAdd(ProfilePackage.Literals.PREFERENCE__CONTROL, newControl, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getAltDisplay() {
		return altDisplay;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAltDisplay(String newAltDisplay) {
		String oldAltDisplay = altDisplay;
		altDisplay = newAltDisplay;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProfilePackage.PREFERENCE__ALT_DISPLAY, oldAltDisplay, altDisplay));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDisplay() {
		return display;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDisplay(String newDisplay) {
		String oldDisplay = display;
		display = newDisplay;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProfilePackage.PREFERENCE__DISPLAY, oldDisplay, display));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProfilePackage.PREFERENCE__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getValue() {
		return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setValue(String newValue) {
		String oldValue = value;
		value = newValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProfilePackage.PREFERENCE__VALUE, oldValue, value));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setVisible(boolean newVisible) {
		boolean oldVisible = visible;
		visible = newVisible;
		boolean oldVisibleESet = visibleESet;
		visibleESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProfilePackage.PREFERENCE__VISIBLE, oldVisible, visible, !oldVisibleESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetVisible() {
		boolean oldVisible = visible;
		boolean oldVisibleESet = visibleESet;
		visible = VISIBLE_EDEFAULT;
		visibleESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, ProfilePackage.PREFERENCE__VISIBLE, oldVisible, VISIBLE_EDEFAULT, oldVisibleESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetVisible() {
		return visibleESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ProfilePackage.PREFERENCE__PREVIEW:
				return basicSetPreview(null, msgs);
			case ProfilePackage.PREFERENCE__CONTROL_GROUP:
				return ((InternalEList)getControlGroup()).basicRemove(otherEnd, msgs);
			case ProfilePackage.PREFERENCE__CONTROL:
				return basicSetControl(null, msgs);
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
			case ProfilePackage.PREFERENCE__PREVIEW:
				return getPreview();
			case ProfilePackage.PREFERENCE__CONTROL_GROUP:
				if (coreType) return getControlGroup();
				return ((FeatureMap.Internal)getControlGroup()).getWrapper();
			case ProfilePackage.PREFERENCE__CONTROL:
				return getControl();
			case ProfilePackage.PREFERENCE__ALT_DISPLAY:
				return getAltDisplay();
			case ProfilePackage.PREFERENCE__DISPLAY:
				return getDisplay();
			case ProfilePackage.PREFERENCE__ID:
				return getId();
			case ProfilePackage.PREFERENCE__VALUE:
				return getValue();
			case ProfilePackage.PREFERENCE__VISIBLE:
				return isVisible() ? Boolean.TRUE : Boolean.FALSE;
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
			case ProfilePackage.PREFERENCE__PREVIEW:
				setPreview((Preview)newValue);
				return;
			case ProfilePackage.PREFERENCE__CONTROL_GROUP:
				((FeatureMap.Internal)getControlGroup()).set(newValue);
				return;
			case ProfilePackage.PREFERENCE__ALT_DISPLAY:
				setAltDisplay((String)newValue);
				return;
			case ProfilePackage.PREFERENCE__DISPLAY:
				setDisplay((String)newValue);
				return;
			case ProfilePackage.PREFERENCE__ID:
				setId((String)newValue);
				return;
			case ProfilePackage.PREFERENCE__VALUE:
				setValue((String)newValue);
				return;
			case ProfilePackage.PREFERENCE__VISIBLE:
				setVisible(((Boolean)newValue).booleanValue());
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
			case ProfilePackage.PREFERENCE__PREVIEW:
				setPreview((Preview)null);
				return;
			case ProfilePackage.PREFERENCE__CONTROL_GROUP:
				getControlGroup().clear();
				return;
			case ProfilePackage.PREFERENCE__ALT_DISPLAY:
				setAltDisplay(ALT_DISPLAY_EDEFAULT);
				return;
			case ProfilePackage.PREFERENCE__DISPLAY:
				setDisplay(DISPLAY_EDEFAULT);
				return;
			case ProfilePackage.PREFERENCE__ID:
				setId(ID_EDEFAULT);
				return;
			case ProfilePackage.PREFERENCE__VALUE:
				setValue(VALUE_EDEFAULT);
				return;
			case ProfilePackage.PREFERENCE__VISIBLE:
				unsetVisible();
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
			case ProfilePackage.PREFERENCE__PREVIEW:
				return preview != null;
			case ProfilePackage.PREFERENCE__CONTROL_GROUP:
				return controlGroup != null && !controlGroup.isEmpty();
			case ProfilePackage.PREFERENCE__CONTROL:
				return getControl() != null;
			case ProfilePackage.PREFERENCE__ALT_DISPLAY:
				return ALT_DISPLAY_EDEFAULT == null ? altDisplay != null : !ALT_DISPLAY_EDEFAULT.equals(altDisplay);
			case ProfilePackage.PREFERENCE__DISPLAY:
				return DISPLAY_EDEFAULT == null ? display != null : !DISPLAY_EDEFAULT.equals(display);
			case ProfilePackage.PREFERENCE__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
			case ProfilePackage.PREFERENCE__VALUE:
				return VALUE_EDEFAULT == null ? value != null : !VALUE_EDEFAULT.equals(value);
			case ProfilePackage.PREFERENCE__VISIBLE:
				return isSetVisible();
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
		result.append(" (controlGroup: ");
		result.append(controlGroup);
		result.append(", altDisplay: ");
		result.append(altDisplay);
		result.append(", display: ");
		result.append(display);
		result.append(", id: ");
		result.append(id);
		result.append(", value: ");
		result.append(value);
		result.append(", visible: ");
		if (visibleESet) result.append(visible); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //PreferenceImpl
