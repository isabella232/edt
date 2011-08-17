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
import org.eclipse.edt.ide.ui.internal.deployment.IMSJ2CProtocol;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>IMSJ2C Protocol</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.IMSJ2CProtocolImpl#getAttribute1 <em>Attribute1</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class IMSJ2CProtocolImpl extends ProtocolImpl implements IMSJ2CProtocol {
	/**
	 * The default value of the '{@link #getAttribute1() <em>Attribute1</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAttribute1()
	 * @generated
	 * @ordered
	 */
	protected static final String ATTRIBUTE1_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getAttribute1() <em>Attribute1</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAttribute1()
	 * @generated
	 * @ordered
	 */
	protected String attribute1 = ATTRIBUTE1_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected IMSJ2CProtocolImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DeploymentPackage.Literals.IMSJ2C_PROTOCOL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getAttribute1() {
		return attribute1;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAttribute1(String newAttribute1) {
		String oldAttribute1 = attribute1;
		attribute1 = newAttribute1;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.IMSJ2C_PROTOCOL__ATTRIBUTE1, oldAttribute1, attribute1));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DeploymentPackage.IMSJ2C_PROTOCOL__ATTRIBUTE1:
				return getAttribute1();
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
			case DeploymentPackage.IMSJ2C_PROTOCOL__ATTRIBUTE1:
				setAttribute1((String)newValue);
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
			case DeploymentPackage.IMSJ2C_PROTOCOL__ATTRIBUTE1:
				setAttribute1(ATTRIBUTE1_EDEFAULT);
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
			case DeploymentPackage.IMSJ2C_PROTOCOL__ATTRIBUTE1:
				return ATTRIBUTE1_EDEFAULT == null ? attribute1 != null : !ATTRIBUTE1_EDEFAULT.equals(attribute1);
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
		result.append(" (attribute1: ");
		result.append(attribute1);
		result.append(')');
		return result.toString();
	}

} //IMSJ2CProtocolImpl
