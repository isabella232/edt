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

import org.eclipse.edt.ide.ui.internal.deployment.CICSWSProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>CICSWS Protocol</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.CICSWSProtocolImpl#getTransaction <em>Transaction</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.CICSWSProtocolImpl#getUserID <em>User ID</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class CICSWSProtocolImpl extends ProtocolImpl implements CICSWSProtocol {
	/**
	 * The default value of the '{@link #getTransaction() <em>Transaction</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTransaction()
	 * @generated
	 * @ordered
	 */
	protected static final String TRANSACTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTransaction() <em>Transaction</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTransaction()
	 * @generated
	 * @ordered
	 */
	protected String transaction = TRANSACTION_EDEFAULT;

	/**
	 * The default value of the '{@link #getUserID() <em>User ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUserID()
	 * @generated
	 * @ordered
	 */
	protected static final String USER_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getUserID() <em>User ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUserID()
	 * @generated
	 * @ordered
	 */
	protected String userID = USER_ID_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CICSWSProtocolImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DeploymentPackage.Literals.CICSWS_PROTOCOL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTransaction() {
		return transaction;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTransaction(String newTransaction) {
		String oldTransaction = transaction;
		transaction = newTransaction;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.CICSWS_PROTOCOL__TRANSACTION, oldTransaction, transaction));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setUserID(String newUserID) {
		String oldUserID = userID;
		userID = newUserID;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.CICSWS_PROTOCOL__USER_ID, oldUserID, userID));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DeploymentPackage.CICSWS_PROTOCOL__TRANSACTION:
				return getTransaction();
			case DeploymentPackage.CICSWS_PROTOCOL__USER_ID:
				return getUserID();
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
			case DeploymentPackage.CICSWS_PROTOCOL__TRANSACTION:
				setTransaction((String)newValue);
				return;
			case DeploymentPackage.CICSWS_PROTOCOL__USER_ID:
				setUserID((String)newValue);
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
			case DeploymentPackage.CICSWS_PROTOCOL__TRANSACTION:
				setTransaction(TRANSACTION_EDEFAULT);
				return;
			case DeploymentPackage.CICSWS_PROTOCOL__USER_ID:
				setUserID(USER_ID_EDEFAULT);
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
			case DeploymentPackage.CICSWS_PROTOCOL__TRANSACTION:
				return TRANSACTION_EDEFAULT == null ? transaction != null : !TRANSACTION_EDEFAULT.equals(transaction);
			case DeploymentPackage.CICSWS_PROTOCOL__USER_ID:
				return USER_ID_EDEFAULT == null ? userID != null : !USER_ID_EDEFAULT.equals(userID);
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
		result.append(" (transaction: ");
		result.append(transaction);
		result.append(", userID: ");
		result.append(userID);
		result.append(')');
		return result.toString();
	}

} //CICSWSProtocolImpl
