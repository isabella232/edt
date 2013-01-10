/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

import org.eclipse.edt.ide.ui.internal.deployment.DeployExt;
import org.eclipse.edt.ide.ui.internal.deployment.Deployment;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentProject;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentTarget;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;

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
 * An implementation of the model object '<em><b>EGL Deployment Root</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLDeploymentRootImpl#getMixed <em>Mixed</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLDeploymentRootImpl#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLDeploymentRootImpl#getXSISchemaLocation <em>XSI Schema Location</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLDeploymentRootImpl#getDeployExt <em>Deploy Ext</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLDeploymentRootImpl#getDeployment <em>Deployment</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLDeploymentRootImpl#getTarget <em>Target</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLDeploymentRootImpl#getTargetProject <em>Target Project</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EGLDeploymentRootImpl extends EObjectImpl implements EGLDeploymentRoot
{
	/**
	 * The cached value of the '{@link #getMixed() <em>Mixed</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMixed()
	 * @generated
	 * @ordered
	 */
	protected FeatureMap mixed;

	/**
	 * The cached value of the '{@link #getXMLNSPrefixMap() <em>XMLNS Prefix Map</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getXMLNSPrefixMap()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, String> xMLNSPrefixMap;

	/**
	 * The cached value of the '{@link #getXSISchemaLocation() <em>XSI Schema Location</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getXSISchemaLocation()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, String> xSISchemaLocation;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EGLDeploymentRootImpl()
	{
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass()
	{
		return DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureMap getMixed()
	{
		if (mixed == null)
		{
			mixed = new BasicFeatureMap(this, DeploymentPackage.EGL_DEPLOYMENT_ROOT__MIXED);
		}
		return mixed;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EMap<String, String> getXMLNSPrefixMap()
	{
		if (xMLNSPrefixMap == null)
		{
			xMLNSPrefixMap = new EcoreEMap<String,String>(EcorePackage.Literals.ESTRING_TO_STRING_MAP_ENTRY, EStringToStringMapEntryImpl.class, this, DeploymentPackage.EGL_DEPLOYMENT_ROOT__XMLNS_PREFIX_MAP);
		}
		return xMLNSPrefixMap;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EMap<String, String> getXSISchemaLocation()
	{
		if (xSISchemaLocation == null)
		{
			xSISchemaLocation = new EcoreEMap<String,String>(EcorePackage.Literals.ESTRING_TO_STRING_MAP_ENTRY, EStringToStringMapEntryImpl.class, this, DeploymentPackage.EGL_DEPLOYMENT_ROOT__XSI_SCHEMA_LOCATION);
		}
		return xSISchemaLocation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeployExt getDeployExt()
	{
		return (DeployExt)getMixed().get(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__DEPLOY_EXT, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetDeployExt(DeployExt newDeployExt, NotificationChain msgs)
	{
		return ((FeatureMap.Internal)getMixed()).basicAdd(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__DEPLOY_EXT, newDeployExt, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Deployment getDeployment()
	{
		return (Deployment)getMixed().get(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__DEPLOYMENT, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetDeployment(Deployment newDeployment, NotificationChain msgs)
	{
		return ((FeatureMap.Internal)getMixed()).basicAdd(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__DEPLOYMENT, newDeployment, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDeployment(Deployment newDeployment)
	{
		((FeatureMap.Internal)getMixed()).set(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__DEPLOYMENT, newDeployment);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeploymentTarget getTarget()
	{
		return (DeploymentTarget)getMixed().get(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__TARGET, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetTarget(DeploymentTarget newTarget, NotificationChain msgs)
	{
		return ((FeatureMap.Internal)getMixed()).basicAdd(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__TARGET, newTarget, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeploymentProject getTargetProject()
	{
		return (DeploymentProject)getMixed().get(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__TARGET_PROJECT, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetTargetProject(DeploymentProject newTargetProject, NotificationChain msgs)
	{
		return ((FeatureMap.Internal)getMixed()).basicAdd(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__TARGET_PROJECT, newTargetProject, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTargetProject(DeploymentProject newTargetProject)
	{
		((FeatureMap.Internal)getMixed()).set(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__TARGET_PROJECT, newTargetProject);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
	{
		switch (featureID)
		{
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__MIXED:
				return ((InternalEList<?>)getMixed()).basicRemove(otherEnd, msgs);
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__XMLNS_PREFIX_MAP:
				return ((InternalEList<?>)getXMLNSPrefixMap()).basicRemove(otherEnd, msgs);
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__XSI_SCHEMA_LOCATION:
				return ((InternalEList<?>)getXSISchemaLocation()).basicRemove(otherEnd, msgs);
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__DEPLOY_EXT:
				return basicSetDeployExt(null, msgs);
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__DEPLOYMENT:
				return basicSetDeployment(null, msgs);
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__TARGET:
				return basicSetTarget(null, msgs);
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__TARGET_PROJECT:
				return basicSetTargetProject(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType)
	{
		switch (featureID)
		{
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__MIXED:
				if (coreType) return getMixed();
				return ((FeatureMap.Internal)getMixed()).getWrapper();
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__XMLNS_PREFIX_MAP:
				if (coreType) return getXMLNSPrefixMap();
				else return getXMLNSPrefixMap().map();
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__XSI_SCHEMA_LOCATION:
				if (coreType) return getXSISchemaLocation();
				else return getXSISchemaLocation().map();
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__DEPLOY_EXT:
				return getDeployExt();
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__DEPLOYMENT:
				return getDeployment();
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__TARGET:
				return getTarget();
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__TARGET_PROJECT:
				return getTargetProject();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue)
	{
		switch (featureID)
		{
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__MIXED:
				((FeatureMap.Internal)getMixed()).set(newValue);
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__XMLNS_PREFIX_MAP:
				((EStructuralFeature.Setting)getXMLNSPrefixMap()).set(newValue);
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__XSI_SCHEMA_LOCATION:
				((EStructuralFeature.Setting)getXSISchemaLocation()).set(newValue);
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__DEPLOYMENT:
				setDeployment((Deployment)newValue);
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__TARGET_PROJECT:
				setTargetProject((DeploymentProject)newValue);
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
	public void eUnset(int featureID)
	{
		switch (featureID)
		{
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__MIXED:
				getMixed().clear();
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__XMLNS_PREFIX_MAP:
				getXMLNSPrefixMap().clear();
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__XSI_SCHEMA_LOCATION:
				getXSISchemaLocation().clear();
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__DEPLOYMENT:
				setDeployment((Deployment)null);
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__TARGET_PROJECT:
				setTargetProject((DeploymentProject)null);
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
	public boolean eIsSet(int featureID)
	{
		switch (featureID)
		{
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__MIXED:
				return mixed != null && !mixed.isEmpty();
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__XMLNS_PREFIX_MAP:
				return xMLNSPrefixMap != null && !xMLNSPrefixMap.isEmpty();
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__XSI_SCHEMA_LOCATION:
				return xSISchemaLocation != null && !xSISchemaLocation.isEmpty();
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__DEPLOY_EXT:
				return getDeployExt() != null;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__DEPLOYMENT:
				return getDeployment() != null;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__TARGET:
				return getTarget() != null;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__TARGET_PROJECT:
				return getTargetProject() != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString()
	{
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (mixed: ");
		result.append(mixed);
		result.append(')');
		return result.toString();
	}

} //EGLDeploymentRootImpl
