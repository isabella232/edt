/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.deployment.impl;

import org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage;
import org.eclipse.edt.ide.ui.internal.deployment.NativeBinding;
import org.eclipse.edt.ide.ui.internal.deployment.Protocol;

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
 * An implementation of the model object '<em><b>Native Binding</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.NativeBindingImpl#getProtocolGroup <em>Protocol Group</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.NativeBindingImpl#getProtocol <em>Protocol</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.NativeBindingImpl#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class NativeBindingImpl extends EObjectImpl implements NativeBinding {
	/**
	 * The cached value of the '{@link #getProtocolGroup() <em>Protocol Group</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProtocolGroup()
	 * @generated
	 * @ordered
	 */
	protected FeatureMap protocolGroup;

	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected NativeBindingImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DeploymentPackage.Literals.NATIVE_BINDING;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureMap getProtocolGroup() {
		if (protocolGroup == null) {
			protocolGroup = new BasicFeatureMap(this, DeploymentPackage.NATIVE_BINDING__PROTOCOL_GROUP);
		}
		return protocolGroup;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Protocol getProtocol() {
		return (Protocol)getProtocolGroup().get(DeploymentPackage.Literals.NATIVE_BINDING__PROTOCOL, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetProtocol(Protocol newProtocol, NotificationChain msgs) {
		return ((FeatureMap.Internal)getProtocolGroup()).basicAdd(DeploymentPackage.Literals.NATIVE_BINDING__PROTOCOL, newProtocol, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.NATIVE_BINDING__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DeploymentPackage.NATIVE_BINDING__PROTOCOL_GROUP:
				return ((InternalEList<?>)getProtocolGroup()).basicRemove(otherEnd, msgs);
			case DeploymentPackage.NATIVE_BINDING__PROTOCOL:
				return basicSetProtocol(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DeploymentPackage.NATIVE_BINDING__PROTOCOL_GROUP:
				if (coreType) return getProtocolGroup();
				return ((FeatureMap.Internal)getProtocolGroup()).getWrapper();
			case DeploymentPackage.NATIVE_BINDING__PROTOCOL:
				return getProtocol();
			case DeploymentPackage.NATIVE_BINDING__NAME:
				return getName();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case DeploymentPackage.NATIVE_BINDING__PROTOCOL_GROUP:
				((FeatureMap.Internal)getProtocolGroup()).set(newValue);
				return;
			case DeploymentPackage.NATIVE_BINDING__NAME:
				setName((String)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case DeploymentPackage.NATIVE_BINDING__PROTOCOL_GROUP:
				getProtocolGroup().clear();
				return;
			case DeploymentPackage.NATIVE_BINDING__NAME:
				setName(NAME_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case DeploymentPackage.NATIVE_BINDING__PROTOCOL_GROUP:
				return protocolGroup != null && !protocolGroup.isEmpty();
			case DeploymentPackage.NATIVE_BINDING__PROTOCOL:
				return getProtocol() != null;
			case DeploymentPackage.NATIVE_BINDING__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (protocolGroup: ");
		result.append(protocolGroup);
		result.append(", name: ");
		result.append(name);
		result.append(')');
		return result.toString();
	}

} //NativeBindingImpl
