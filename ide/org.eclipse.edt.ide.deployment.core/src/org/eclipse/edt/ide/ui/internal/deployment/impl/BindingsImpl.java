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

import java.util.Collection;

import org.eclipse.edt.ide.ui.internal.deployment.Bindings;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage;
import org.eclipse.edt.ide.ui.internal.deployment.EGLBinding;
import org.eclipse.edt.ide.ui.internal.deployment.NativeBinding;
import org.eclipse.edt.ide.ui.internal.deployment.RestBinding;
import org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding;
import org.eclipse.edt.ide.ui.internal.deployment.WebBinding;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Bindings</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.BindingsImpl#getEglBinding <em>Egl Binding</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.BindingsImpl#getWebBinding <em>Web Binding</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.BindingsImpl#getNativeBinding <em>Native Binding</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.BindingsImpl#getRestBinding <em>Rest Binding</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.BindingsImpl#getSqlDatabaseBinding <em>Sql Database Binding</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class BindingsImpl extends EObjectImpl implements Bindings {
	/**
	 * The cached value of the '{@link #getEglBinding() <em>Egl Binding</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEglBinding()
	 * @generated
	 * @ordered
	 */
	protected EList<EGLBinding> eglBinding;

	/**
	 * The cached value of the '{@link #getWebBinding() <em>Web Binding</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWebBinding()
	 * @generated
	 * @ordered
	 */
	protected EList<WebBinding> webBinding;

	/**
	 * The cached value of the '{@link #getNativeBinding() <em>Native Binding</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNativeBinding()
	 * @generated
	 * @ordered
	 */
	protected EList<NativeBinding> nativeBinding;

	/**
	 * The cached value of the '{@link #getRestBinding() <em>Rest Binding</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRestBinding()
	 * @generated
	 * @ordered
	 */
	protected EList<RestBinding> restBinding;

	/**
	 * The cached value of the '{@link #getSqlDatabaseBinding() <em>Sql Database Binding</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSqlDatabaseBinding()
	 * @generated
	 * @ordered
	 */
	protected EList<SQLDatabaseBinding> sqlDatabaseBinding;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected BindingsImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DeploymentPackage.Literals.BINDINGS;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<EGLBinding> getEglBinding() {
		if (eglBinding == null) {
			eglBinding = new EObjectContainmentEList<EGLBinding>(EGLBinding.class, this, DeploymentPackage.BINDINGS__EGL_BINDING);
		}
		return eglBinding;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<WebBinding> getWebBinding() {
		if (webBinding == null) {
			webBinding = new EObjectContainmentEList<WebBinding>(WebBinding.class, this, DeploymentPackage.BINDINGS__WEB_BINDING);
		}
		return webBinding;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<NativeBinding> getNativeBinding() {
		if (nativeBinding == null) {
			nativeBinding = new EObjectContainmentEList<NativeBinding>(NativeBinding.class, this, DeploymentPackage.BINDINGS__NATIVE_BINDING);
		}
		return nativeBinding;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<RestBinding> getRestBinding() {
		if (restBinding == null) {
			restBinding = new EObjectContainmentEList<RestBinding>(RestBinding.class, this, DeploymentPackage.BINDINGS__REST_BINDING);
		}
		return restBinding;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<SQLDatabaseBinding> getSqlDatabaseBinding() {
		if (sqlDatabaseBinding == null) {
			sqlDatabaseBinding = new EObjectContainmentEList<SQLDatabaseBinding>(SQLDatabaseBinding.class, this, DeploymentPackage.BINDINGS__SQL_DATABASE_BINDING);
		}
		return sqlDatabaseBinding;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DeploymentPackage.BINDINGS__EGL_BINDING:
				return ((InternalEList<?>)getEglBinding()).basicRemove(otherEnd, msgs);
			case DeploymentPackage.BINDINGS__WEB_BINDING:
				return ((InternalEList<?>)getWebBinding()).basicRemove(otherEnd, msgs);
			case DeploymentPackage.BINDINGS__NATIVE_BINDING:
				return ((InternalEList<?>)getNativeBinding()).basicRemove(otherEnd, msgs);
			case DeploymentPackage.BINDINGS__REST_BINDING:
				return ((InternalEList<?>)getRestBinding()).basicRemove(otherEnd, msgs);
			case DeploymentPackage.BINDINGS__SQL_DATABASE_BINDING:
				return ((InternalEList<?>)getSqlDatabaseBinding()).basicRemove(otherEnd, msgs);
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
			case DeploymentPackage.BINDINGS__EGL_BINDING:
				return getEglBinding();
			case DeploymentPackage.BINDINGS__WEB_BINDING:
				return getWebBinding();
			case DeploymentPackage.BINDINGS__NATIVE_BINDING:
				return getNativeBinding();
			case DeploymentPackage.BINDINGS__REST_BINDING:
				return getRestBinding();
			case DeploymentPackage.BINDINGS__SQL_DATABASE_BINDING:
				return getSqlDatabaseBinding();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case DeploymentPackage.BINDINGS__EGL_BINDING:
				getEglBinding().clear();
				getEglBinding().addAll((Collection<? extends EGLBinding>)newValue);
				return;
			case DeploymentPackage.BINDINGS__WEB_BINDING:
				getWebBinding().clear();
				getWebBinding().addAll((Collection<? extends WebBinding>)newValue);
				return;
			case DeploymentPackage.BINDINGS__NATIVE_BINDING:
				getNativeBinding().clear();
				getNativeBinding().addAll((Collection<? extends NativeBinding>)newValue);
				return;
			case DeploymentPackage.BINDINGS__REST_BINDING:
				getRestBinding().clear();
				getRestBinding().addAll((Collection<? extends RestBinding>)newValue);
				return;
			case DeploymentPackage.BINDINGS__SQL_DATABASE_BINDING:
				getSqlDatabaseBinding().clear();
				getSqlDatabaseBinding().addAll((Collection<? extends SQLDatabaseBinding>)newValue);
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
			case DeploymentPackage.BINDINGS__EGL_BINDING:
				getEglBinding().clear();
				return;
			case DeploymentPackage.BINDINGS__WEB_BINDING:
				getWebBinding().clear();
				return;
			case DeploymentPackage.BINDINGS__NATIVE_BINDING:
				getNativeBinding().clear();
				return;
			case DeploymentPackage.BINDINGS__REST_BINDING:
				getRestBinding().clear();
				return;
			case DeploymentPackage.BINDINGS__SQL_DATABASE_BINDING:
				getSqlDatabaseBinding().clear();
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
			case DeploymentPackage.BINDINGS__EGL_BINDING:
				return eglBinding != null && !eglBinding.isEmpty();
			case DeploymentPackage.BINDINGS__WEB_BINDING:
				return webBinding != null && !webBinding.isEmpty();
			case DeploymentPackage.BINDINGS__NATIVE_BINDING:
				return nativeBinding != null && !nativeBinding.isEmpty();
			case DeploymentPackage.BINDINGS__REST_BINDING:
				return restBinding != null && !restBinding.isEmpty();
			case DeploymentPackage.BINDINGS__SQL_DATABASE_BINDING:
				return sqlDatabaseBinding != null && !sqlDatabaseBinding.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //BindingsImpl
