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
import org.eclipse.edt.ide.ui.internal.deployment.Protocol;
import org.eclipse.edt.ide.ui.internal.deployment.Protocols;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Protocols</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.ProtocolsImpl#getProtocolGroup <em>Protocol Group</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.ProtocolsImpl#getProtocol <em>Protocol</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ProtocolsImpl extends EObjectImpl implements Protocols {
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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ProtocolsImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DeploymentPackage.Literals.PROTOCOLS;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureMap getProtocolGroup() {
		if (protocolGroup == null) {
			protocolGroup = new BasicFeatureMap(this, DeploymentPackage.PROTOCOLS__PROTOCOL_GROUP);
		}
		return protocolGroup;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Protocol> getProtocol() {
		return getProtocolGroup().list(DeploymentPackage.Literals.PROTOCOLS__PROTOCOL);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DeploymentPackage.PROTOCOLS__PROTOCOL_GROUP:
				return ((InternalEList<?>)getProtocolGroup()).basicRemove(otherEnd, msgs);
			case DeploymentPackage.PROTOCOLS__PROTOCOL:
				return ((InternalEList<?>)getProtocol()).basicRemove(otherEnd, msgs);
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
			case DeploymentPackage.PROTOCOLS__PROTOCOL_GROUP:
				if (coreType) return getProtocolGroup();
				return ((FeatureMap.Internal)getProtocolGroup()).getWrapper();
			case DeploymentPackage.PROTOCOLS__PROTOCOL:
				return getProtocol();
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
			case DeploymentPackage.PROTOCOLS__PROTOCOL_GROUP:
				((FeatureMap.Internal)getProtocolGroup()).set(newValue);
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
			case DeploymentPackage.PROTOCOLS__PROTOCOL_GROUP:
				getProtocolGroup().clear();
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
			case DeploymentPackage.PROTOCOLS__PROTOCOL_GROUP:
				return protocolGroup != null && !protocolGroup.isEmpty();
			case DeploymentPackage.PROTOCOLS__PROTOCOL:
				return !getProtocol().isEmpty();
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
		result.append(')');
		return result.toString();
	}

} //ProtocolsImpl
