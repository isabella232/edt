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
import org.eclipse.edt.ide.ui.internal.deployment.Parameters;
import org.eclipse.edt.ide.ui.internal.deployment.Restservice;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Restservice</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RestserviceImpl#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RestserviceImpl#isEnableGeneration <em>Enable Generation</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RestserviceImpl#getImplementation <em>Implementation</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RestserviceImpl#getImplType <em>Impl Type</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RestserviceImpl#getProtocol <em>Protocol</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RestserviceImpl#isStateful <em>Stateful</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RestserviceImpl#getUri <em>Uri</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RestserviceImpl extends EObjectImpl implements Restservice {
	/**
	 * The cached value of the '{@link #getParameters() <em>Parameters</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParameters()
	 * @generated
	 * @ordered
	 */
	protected Parameters parameters;

	/**
	 * The default value of the '{@link #isEnableGeneration() <em>Enable Generation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isEnableGeneration()
	 * @generated
	 * @ordered
	 */
	protected static final boolean ENABLE_GENERATION_EDEFAULT = true;

	/**
	 * The cached value of the '{@link #isEnableGeneration() <em>Enable Generation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isEnableGeneration()
	 * @generated
	 * @ordered
	 */
	protected boolean enableGeneration = ENABLE_GENERATION_EDEFAULT;

	/**
	 * This is true if the Enable Generation attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean enableGenerationESet;

	/**
	 * The default value of the '{@link #getImplementation() <em>Implementation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getImplementation()
	 * @generated
	 * @ordered
	 */
	protected static final String IMPLEMENTATION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getImplementation() <em>Implementation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getImplementation()
	 * @generated
	 * @ordered
	 */
	protected String implementation = IMPLEMENTATION_EDEFAULT;

	/**
	 * The default value of the '{@link #getImplType() <em>Impl Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getImplType()
	 * @generated
	 * @ordered
	 */
	protected static final int IMPL_TYPE_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getImplType() <em>Impl Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getImplType()
	 * @generated
	 * @ordered
	 */
	protected int implType = IMPL_TYPE_EDEFAULT;

	/**
	 * This is true if the Impl Type attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean implTypeESet;

	/**
	 * The default value of the '{@link #getProtocol() <em>Protocol</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProtocol()
	 * @generated
	 * @ordered
	 */
	protected static final String PROTOCOL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getProtocol() <em>Protocol</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProtocol()
	 * @generated
	 * @ordered
	 */
	protected String protocol = PROTOCOL_EDEFAULT;

	/**
	 * The default value of the '{@link #isStateful() <em>Stateful</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isStateful()
	 * @generated
	 * @ordered
	 */
	protected static final boolean STATEFUL_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isStateful() <em>Stateful</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isStateful()
	 * @generated
	 * @ordered
	 */
	protected boolean stateful = STATEFUL_EDEFAULT;

	/**
	 * This is true if the Stateful attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean statefulESet;

	/**
	 * The default value of the '{@link #getUri() <em>Uri</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUri()
	 * @generated
	 * @ordered
	 */
	protected static final String URI_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getUri() <em>Uri</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUri()
	 * @generated
	 * @ordered
	 */
	protected String uri = URI_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RestserviceImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DeploymentPackage.Literals.RESTSERVICE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Parameters getParameters() {
		return parameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetParameters(Parameters newParameters, NotificationChain msgs) {
		Parameters oldParameters = parameters;
		parameters = newParameters;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DeploymentPackage.RESTSERVICE__PARAMETERS, oldParameters, newParameters);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setParameters(Parameters newParameters) {
		if (newParameters != parameters) {
			NotificationChain msgs = null;
			if (parameters != null)
				msgs = ((InternalEObject)parameters).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - DeploymentPackage.RESTSERVICE__PARAMETERS, null, msgs);
			if (newParameters != null)
				msgs = ((InternalEObject)newParameters).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - DeploymentPackage.RESTSERVICE__PARAMETERS, null, msgs);
			msgs = basicSetParameters(newParameters, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.RESTSERVICE__PARAMETERS, newParameters, newParameters));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isEnableGeneration() {
		return enableGeneration;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setEnableGeneration(boolean newEnableGeneration) {
		boolean oldEnableGeneration = enableGeneration;
		enableGeneration = newEnableGeneration;
		boolean oldEnableGenerationESet = enableGenerationESet;
		enableGenerationESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.RESTSERVICE__ENABLE_GENERATION, oldEnableGeneration, enableGeneration, !oldEnableGenerationESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetEnableGeneration() {
		boolean oldEnableGeneration = enableGeneration;
		boolean oldEnableGenerationESet = enableGenerationESet;
		enableGeneration = ENABLE_GENERATION_EDEFAULT;
		enableGenerationESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, DeploymentPackage.RESTSERVICE__ENABLE_GENERATION, oldEnableGeneration, ENABLE_GENERATION_EDEFAULT, oldEnableGenerationESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetEnableGeneration() {
		return enableGenerationESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getImplementation() {
		return implementation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setImplementation(String newImplementation) {
		String oldImplementation = implementation;
		implementation = newImplementation;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.RESTSERVICE__IMPLEMENTATION, oldImplementation, implementation));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getImplType() {
		return implType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setImplType(int newImplType) {
		int oldImplType = implType;
		implType = newImplType;
		boolean oldImplTypeESet = implTypeESet;
		implTypeESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.RESTSERVICE__IMPL_TYPE, oldImplType, implType, !oldImplTypeESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetImplType() {
		int oldImplType = implType;
		boolean oldImplTypeESet = implTypeESet;
		implType = IMPL_TYPE_EDEFAULT;
		implTypeESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, DeploymentPackage.RESTSERVICE__IMPL_TYPE, oldImplType, IMPL_TYPE_EDEFAULT, oldImplTypeESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetImplType() {
		return implTypeESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProtocol(String newProtocol) {
		String oldProtocol = protocol;
		protocol = newProtocol;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.RESTSERVICE__PROTOCOL, oldProtocol, protocol));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isStateful() {
		return stateful;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setStateful(boolean newStateful) {
		boolean oldStateful = stateful;
		stateful = newStateful;
		boolean oldStatefulESet = statefulESet;
		statefulESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.RESTSERVICE__STATEFUL, oldStateful, stateful, !oldStatefulESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetStateful() {
		boolean oldStateful = stateful;
		boolean oldStatefulESet = statefulESet;
		stateful = STATEFUL_EDEFAULT;
		statefulESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, DeploymentPackage.RESTSERVICE__STATEFUL, oldStateful, STATEFUL_EDEFAULT, oldStatefulESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetStateful() {
		return statefulESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setUri(String newUri) {
		String oldUri = uri;
		uri = newUri;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.RESTSERVICE__URI, oldUri, uri));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DeploymentPackage.RESTSERVICE__PARAMETERS:
				return basicSetParameters(null, msgs);
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
			case DeploymentPackage.RESTSERVICE__PARAMETERS:
				return getParameters();
			case DeploymentPackage.RESTSERVICE__ENABLE_GENERATION:
				return isEnableGeneration();
			case DeploymentPackage.RESTSERVICE__IMPLEMENTATION:
				return getImplementation();
			case DeploymentPackage.RESTSERVICE__IMPL_TYPE:
				return getImplType();
			case DeploymentPackage.RESTSERVICE__PROTOCOL:
				return getProtocol();
			case DeploymentPackage.RESTSERVICE__STATEFUL:
				return isStateful();
			case DeploymentPackage.RESTSERVICE__URI:
				return getUri();
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
			case DeploymentPackage.RESTSERVICE__PARAMETERS:
				setParameters((Parameters)newValue);
				return;
			case DeploymentPackage.RESTSERVICE__ENABLE_GENERATION:
				setEnableGeneration((Boolean)newValue);
				return;
			case DeploymentPackage.RESTSERVICE__IMPLEMENTATION:
				setImplementation((String)newValue);
				return;
			case DeploymentPackage.RESTSERVICE__IMPL_TYPE:
				setImplType((Integer)newValue);
				return;
			case DeploymentPackage.RESTSERVICE__PROTOCOL:
				setProtocol((String)newValue);
				return;
			case DeploymentPackage.RESTSERVICE__STATEFUL:
				setStateful((Boolean)newValue);
				return;
			case DeploymentPackage.RESTSERVICE__URI:
				setUri((String)newValue);
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
			case DeploymentPackage.RESTSERVICE__PARAMETERS:
				setParameters((Parameters)null);
				return;
			case DeploymentPackage.RESTSERVICE__ENABLE_GENERATION:
				unsetEnableGeneration();
				return;
			case DeploymentPackage.RESTSERVICE__IMPLEMENTATION:
				setImplementation(IMPLEMENTATION_EDEFAULT);
				return;
			case DeploymentPackage.RESTSERVICE__IMPL_TYPE:
				unsetImplType();
				return;
			case DeploymentPackage.RESTSERVICE__PROTOCOL:
				setProtocol(PROTOCOL_EDEFAULT);
				return;
			case DeploymentPackage.RESTSERVICE__STATEFUL:
				unsetStateful();
				return;
			case DeploymentPackage.RESTSERVICE__URI:
				setUri(URI_EDEFAULT);
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
			case DeploymentPackage.RESTSERVICE__PARAMETERS:
				return parameters != null;
			case DeploymentPackage.RESTSERVICE__ENABLE_GENERATION:
				return isSetEnableGeneration();
			case DeploymentPackage.RESTSERVICE__IMPLEMENTATION:
				return IMPLEMENTATION_EDEFAULT == null ? implementation != null : !IMPLEMENTATION_EDEFAULT.equals(implementation);
			case DeploymentPackage.RESTSERVICE__IMPL_TYPE:
				return isSetImplType();
			case DeploymentPackage.RESTSERVICE__PROTOCOL:
				return PROTOCOL_EDEFAULT == null ? protocol != null : !PROTOCOL_EDEFAULT.equals(protocol);
			case DeploymentPackage.RESTSERVICE__STATEFUL:
				return isSetStateful();
			case DeploymentPackage.RESTSERVICE__URI:
				return URI_EDEFAULT == null ? uri != null : !URI_EDEFAULT.equals(uri);
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
		result.append(" (enableGeneration: ");
		if (enableGenerationESet) result.append(enableGeneration); else result.append("<unset>");
		result.append(", implementation: ");
		result.append(implementation);
		result.append(", implType: ");
		if (implTypeESet) result.append(implType); else result.append("<unset>");
		result.append(", protocol: ");
		result.append(protocol);
		result.append(", stateful: ");
		if (statefulESet) result.append(stateful); else result.append("<unset>");
		result.append(", uri: ");
		result.append(uri);
		result.append(')');
		return result.toString();
	}

} //RestserviceImpl
