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
import org.eclipse.edt.ide.ui.internal.deployment.EGLBinding;
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
 * An implementation of the model object '<em><b>EGL Binding</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLBindingImpl#getProtocolGroup <em>Protocol Group</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLBindingImpl#getProtocol <em>Protocol</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLBindingImpl#getAlias <em>Alias</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLBindingImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLBindingImpl#getServiceName <em>Service Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EGLBindingImpl extends EObjectImpl implements EGLBinding {
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
	 * The default value of the '{@link #getAlias() <em>Alias</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAlias()
	 * @generated
	 * @ordered
	 */
	protected static final String ALIAS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getAlias() <em>Alias</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAlias()
	 * @generated
	 * @ordered
	 */
	protected String alias = ALIAS_EDEFAULT;

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
	 * The default value of the '{@link #getServiceName() <em>Service Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getServiceName()
	 * @generated
	 * @ordered
	 */
	protected static final String SERVICE_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getServiceName() <em>Service Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getServiceName()
	 * @generated
	 * @ordered
	 */
	protected String serviceName = SERVICE_NAME_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EGLBindingImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DeploymentPackage.Literals.EGL_BINDING;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureMap getProtocolGroup() {
		if (protocolGroup == null) {
			protocolGroup = new BasicFeatureMap(this, DeploymentPackage.EGL_BINDING__PROTOCOL_GROUP);
		}
		return protocolGroup;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Protocol getProtocol() {
		return (Protocol)getProtocolGroup().get(DeploymentPackage.Literals.EGL_BINDING__PROTOCOL, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetProtocol(Protocol newProtocol, NotificationChain msgs) {
		return ((FeatureMap.Internal)getProtocolGroup()).basicAdd(DeploymentPackage.Literals.EGL_BINDING__PROTOCOL, newProtocol, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAlias(String newAlias) {
		String oldAlias = alias;
		alias = newAlias;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.EGL_BINDING__ALIAS, oldAlias, alias));
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
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.EGL_BINDING__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setServiceName(String newServiceName) {
		String oldServiceName = serviceName;
		serviceName = newServiceName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.EGL_BINDING__SERVICE_NAME, oldServiceName, serviceName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DeploymentPackage.EGL_BINDING__PROTOCOL_GROUP:
				return ((InternalEList<?>)getProtocolGroup()).basicRemove(otherEnd, msgs);
			case DeploymentPackage.EGL_BINDING__PROTOCOL:
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
			case DeploymentPackage.EGL_BINDING__PROTOCOL_GROUP:
				if (coreType) return getProtocolGroup();
				return ((FeatureMap.Internal)getProtocolGroup()).getWrapper();
			case DeploymentPackage.EGL_BINDING__PROTOCOL:
				return getProtocol();
			case DeploymentPackage.EGL_BINDING__ALIAS:
				return getAlias();
			case DeploymentPackage.EGL_BINDING__NAME:
				return getName();
			case DeploymentPackage.EGL_BINDING__SERVICE_NAME:
				return getServiceName();
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
			case DeploymentPackage.EGL_BINDING__PROTOCOL_GROUP:
				((FeatureMap.Internal)getProtocolGroup()).set(newValue);
				return;
			case DeploymentPackage.EGL_BINDING__ALIAS:
				setAlias((String)newValue);
				return;
			case DeploymentPackage.EGL_BINDING__NAME:
				setName((String)newValue);
				return;
			case DeploymentPackage.EGL_BINDING__SERVICE_NAME:
				setServiceName((String)newValue);
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
			case DeploymentPackage.EGL_BINDING__PROTOCOL_GROUP:
				getProtocolGroup().clear();
				return;
			case DeploymentPackage.EGL_BINDING__ALIAS:
				setAlias(ALIAS_EDEFAULT);
				return;
			case DeploymentPackage.EGL_BINDING__NAME:
				setName(NAME_EDEFAULT);
				return;
			case DeploymentPackage.EGL_BINDING__SERVICE_NAME:
				setServiceName(SERVICE_NAME_EDEFAULT);
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
			case DeploymentPackage.EGL_BINDING__PROTOCOL_GROUP:
				return protocolGroup != null && !protocolGroup.isEmpty();
			case DeploymentPackage.EGL_BINDING__PROTOCOL:
				return getProtocol() != null;
			case DeploymentPackage.EGL_BINDING__ALIAS:
				return ALIAS_EDEFAULT == null ? alias != null : !ALIAS_EDEFAULT.equals(alias);
			case DeploymentPackage.EGL_BINDING__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case DeploymentPackage.EGL_BINDING__SERVICE_NAME:
				return SERVICE_NAME_EDEFAULT == null ? serviceName != null : !SERVICE_NAME_EDEFAULT.equals(serviceName);
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
		result.append(", alias: ");
		result.append(alias);
		result.append(", name: ");
		result.append(name);
		result.append(", serviceName: ");
		result.append(serviceName);
		result.append(')');
		return result.toString();
	}

} //EGLBindingImpl
