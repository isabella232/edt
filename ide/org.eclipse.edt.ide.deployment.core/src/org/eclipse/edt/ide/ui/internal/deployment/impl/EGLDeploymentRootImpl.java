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

import org.eclipse.edt.ide.ui.internal.deployment.CICSECIProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.CICSJ2CProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.CICSWSProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.DeployExt;
import org.eclipse.edt.ide.ui.internal.deployment.Deployment;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentBuildDescriptor;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentProject;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentTarget;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.deployment.IMSJ2CProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.IMSTCPProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.Java400Protocol;
import org.eclipse.edt.ide.ui.internal.deployment.LocalProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.Protocol;
import org.eclipse.edt.ide.ui.internal.deployment.ReferenceProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.SystemIProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.TCPIPProtocol;

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
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLDeploymentRootImpl#getProtocol <em>Protocol</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLDeploymentRootImpl#getProtocolCicseci <em>Protocol Cicseci</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLDeploymentRootImpl#getProtocolCicsj2c <em>Protocol Cicsj2c</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLDeploymentRootImpl#getProtocolCicsssl <em>Protocol Cicsssl</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLDeploymentRootImpl#getProtocolCicsws <em>Protocol Cicsws</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLDeploymentRootImpl#getProtocolImsj2c <em>Protocol Imsj2c</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLDeploymentRootImpl#getProtocolImstcp <em>Protocol Imstcp</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLDeploymentRootImpl#getProtocolJava400 <em>Protocol Java400</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLDeploymentRootImpl#getProtocolJava400j2c <em>Protocol Java400j2c</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLDeploymentRootImpl#getProtocolLocal <em>Protocol Local</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLDeploymentRootImpl#getProtocolRef <em>Protocol Ref</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLDeploymentRootImpl#getProtocolSystemILocal <em>Protocol System ILocal</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLDeploymentRootImpl#getProtocolTcpip <em>Protocol Tcpip</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLDeploymentRootImpl#getTarget <em>Target</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLDeploymentRootImpl#getTargetBuildDescriptor <em>Target Build Descriptor</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.EGLDeploymentRootImpl#getTargetProject <em>Target Project</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EGLDeploymentRootImpl extends EObjectImpl implements EGLDeploymentRoot {
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
	protected EGLDeploymentRootImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureMap getMixed() {
		if (mixed == null) {
			mixed = new BasicFeatureMap(this, DeploymentPackage.EGL_DEPLOYMENT_ROOT__MIXED);
		}
		return mixed;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EMap<String, String> getXMLNSPrefixMap() {
		if (xMLNSPrefixMap == null) {
			xMLNSPrefixMap = new EcoreEMap<String,String>(EcorePackage.Literals.ESTRING_TO_STRING_MAP_ENTRY, EStringToStringMapEntryImpl.class, this, DeploymentPackage.EGL_DEPLOYMENT_ROOT__XMLNS_PREFIX_MAP);
		}
		return xMLNSPrefixMap;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EMap<String, String> getXSISchemaLocation() {
		if (xSISchemaLocation == null) {
			xSISchemaLocation = new EcoreEMap<String,String>(EcorePackage.Literals.ESTRING_TO_STRING_MAP_ENTRY, EStringToStringMapEntryImpl.class, this, DeploymentPackage.EGL_DEPLOYMENT_ROOT__XSI_SCHEMA_LOCATION);
		}
		return xSISchemaLocation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeployExt getDeployExt() {
		return (DeployExt)getMixed().get(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__DEPLOY_EXT, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetDeployExt(DeployExt newDeployExt, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__DEPLOY_EXT, newDeployExt, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Deployment getDeployment() {
		return (Deployment)getMixed().get(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__DEPLOYMENT, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetDeployment(Deployment newDeployment, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__DEPLOYMENT, newDeployment, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDeployment(Deployment newDeployment) {
		((FeatureMap.Internal)getMixed()).set(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__DEPLOYMENT, newDeployment);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Protocol getProtocol() {
		return (Protocol)getMixed().get(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetProtocol(Protocol newProtocol, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL, newProtocol, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CICSECIProtocol getProtocolCicseci() {
		return (CICSECIProtocol)getMixed().get(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSECI, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetProtocolCicseci(CICSECIProtocol newProtocolCicseci, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSECI, newProtocolCicseci, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProtocolCicseci(CICSECIProtocol newProtocolCicseci) {
		((FeatureMap.Internal)getMixed()).set(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSECI, newProtocolCicseci);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CICSJ2CProtocol getProtocolCicsj2c() {
		return (CICSJ2CProtocol)getMixed().get(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSJ2C, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetProtocolCicsj2c(CICSJ2CProtocol newProtocolCicsj2c, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSJ2C, newProtocolCicsj2c, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProtocolCicsj2c(CICSJ2CProtocol newProtocolCicsj2c) {
		((FeatureMap.Internal)getMixed()).set(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSJ2C, newProtocolCicsj2c);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CICSSSLProtocol getProtocolCicsssl() {
		return (CICSSSLProtocol)getMixed().get(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSSSL, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetProtocolCicsssl(CICSSSLProtocol newProtocolCicsssl, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSSSL, newProtocolCicsssl, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProtocolCicsssl(CICSSSLProtocol newProtocolCicsssl) {
		((FeatureMap.Internal)getMixed()).set(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSSSL, newProtocolCicsssl);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CICSWSProtocol getProtocolCicsws() {
		return (CICSWSProtocol)getMixed().get(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSWS, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetProtocolCicsws(CICSWSProtocol newProtocolCicsws, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSWS, newProtocolCicsws, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProtocolCicsws(CICSWSProtocol newProtocolCicsws) {
		((FeatureMap.Internal)getMixed()).set(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSWS, newProtocolCicsws);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IMSJ2CProtocol getProtocolImsj2c() {
		return (IMSJ2CProtocol)getMixed().get(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSJ2C, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetProtocolImsj2c(IMSJ2CProtocol newProtocolImsj2c, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSJ2C, newProtocolImsj2c, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProtocolImsj2c(IMSJ2CProtocol newProtocolImsj2c) {
		((FeatureMap.Internal)getMixed()).set(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSJ2C, newProtocolImsj2c);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IMSTCPProtocol getProtocolImstcp() {
		return (IMSTCPProtocol)getMixed().get(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSTCP, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetProtocolImstcp(IMSTCPProtocol newProtocolImstcp, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSTCP, newProtocolImstcp, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProtocolImstcp(IMSTCPProtocol newProtocolImstcp) {
		((FeatureMap.Internal)getMixed()).set(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSTCP, newProtocolImstcp);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Java400Protocol getProtocolJava400() {
		return (Java400Protocol)getMixed().get(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetProtocolJava400(Java400Protocol newProtocolJava400, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400, newProtocolJava400, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProtocolJava400(Java400Protocol newProtocolJava400) {
		((FeatureMap.Internal)getMixed()).set(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400, newProtocolJava400);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Java400J2cProtocol getProtocolJava400j2c() {
		return (Java400J2cProtocol)getMixed().get(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400J2C, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetProtocolJava400j2c(Java400J2cProtocol newProtocolJava400j2c, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400J2C, newProtocolJava400j2c, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProtocolJava400j2c(Java400J2cProtocol newProtocolJava400j2c) {
		((FeatureMap.Internal)getMixed()).set(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400J2C, newProtocolJava400j2c);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LocalProtocol getProtocolLocal() {
		return (LocalProtocol)getMixed().get(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_LOCAL, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetProtocolLocal(LocalProtocol newProtocolLocal, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_LOCAL, newProtocolLocal, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProtocolLocal(LocalProtocol newProtocolLocal) {
		((FeatureMap.Internal)getMixed()).set(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_LOCAL, newProtocolLocal);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ReferenceProtocol getProtocolRef() {
		return (ReferenceProtocol)getMixed().get(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_REF, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetProtocolRef(ReferenceProtocol newProtocolRef, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_REF, newProtocolRef, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProtocolRef(ReferenceProtocol newProtocolRef) {
		((FeatureMap.Internal)getMixed()).set(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_REF, newProtocolRef);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SystemIProtocol getProtocolSystemILocal() {
		return (SystemIProtocol)getMixed().get(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_SYSTEM_ILOCAL, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetProtocolSystemILocal(SystemIProtocol newProtocolSystemILocal, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_SYSTEM_ILOCAL, newProtocolSystemILocal, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProtocolSystemILocal(SystemIProtocol newProtocolSystemILocal) {
		((FeatureMap.Internal)getMixed()).set(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_SYSTEM_ILOCAL, newProtocolSystemILocal);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TCPIPProtocol getProtocolTcpip() {
		return (TCPIPProtocol)getMixed().get(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_TCPIP, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetProtocolTcpip(TCPIPProtocol newProtocolTcpip, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_TCPIP, newProtocolTcpip, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProtocolTcpip(TCPIPProtocol newProtocolTcpip) {
		((FeatureMap.Internal)getMixed()).set(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_TCPIP, newProtocolTcpip);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeploymentTarget getTarget() {
		return (DeploymentTarget)getMixed().get(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__TARGET, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetTarget(DeploymentTarget newTarget, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__TARGET, newTarget, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeploymentBuildDescriptor getTargetBuildDescriptor() {
		return (DeploymentBuildDescriptor)getMixed().get(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__TARGET_BUILD_DESCRIPTOR, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetTargetBuildDescriptor(DeploymentBuildDescriptor newTargetBuildDescriptor, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__TARGET_BUILD_DESCRIPTOR, newTargetBuildDescriptor, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTargetBuildDescriptor(DeploymentBuildDescriptor newTargetBuildDescriptor) {
		((FeatureMap.Internal)getMixed()).set(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__TARGET_BUILD_DESCRIPTOR, newTargetBuildDescriptor);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeploymentProject getTargetProject() {
		return (DeploymentProject)getMixed().get(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__TARGET_PROJECT, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetTargetProject(DeploymentProject newTargetProject, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__TARGET_PROJECT, newTargetProject, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTargetProject(DeploymentProject newTargetProject) {
		((FeatureMap.Internal)getMixed()).set(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__TARGET_PROJECT, newTargetProject);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
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
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL:
				return basicSetProtocol(null, msgs);
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSECI:
				return basicSetProtocolCicseci(null, msgs);
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSJ2C:
				return basicSetProtocolCicsj2c(null, msgs);
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSSSL:
				return basicSetProtocolCicsssl(null, msgs);
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSWS:
				return basicSetProtocolCicsws(null, msgs);
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSJ2C:
				return basicSetProtocolImsj2c(null, msgs);
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSTCP:
				return basicSetProtocolImstcp(null, msgs);
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400:
				return basicSetProtocolJava400(null, msgs);
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400J2C:
				return basicSetProtocolJava400j2c(null, msgs);
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_LOCAL:
				return basicSetProtocolLocal(null, msgs);
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_REF:
				return basicSetProtocolRef(null, msgs);
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_SYSTEM_ILOCAL:
				return basicSetProtocolSystemILocal(null, msgs);
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_TCPIP:
				return basicSetProtocolTcpip(null, msgs);
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__TARGET:
				return basicSetTarget(null, msgs);
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__TARGET_BUILD_DESCRIPTOR:
				return basicSetTargetBuildDescriptor(null, msgs);
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
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
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
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL:
				return getProtocol();
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSECI:
				return getProtocolCicseci();
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSJ2C:
				return getProtocolCicsj2c();
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSSSL:
				return getProtocolCicsssl();
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSWS:
				return getProtocolCicsws();
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSJ2C:
				return getProtocolImsj2c();
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSTCP:
				return getProtocolImstcp();
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400:
				return getProtocolJava400();
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400J2C:
				return getProtocolJava400j2c();
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_LOCAL:
				return getProtocolLocal();
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_REF:
				return getProtocolRef();
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_SYSTEM_ILOCAL:
				return getProtocolSystemILocal();
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_TCPIP:
				return getProtocolTcpip();
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__TARGET:
				return getTarget();
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__TARGET_BUILD_DESCRIPTOR:
				return getTargetBuildDescriptor();
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
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
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
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSECI:
				setProtocolCicseci((CICSECIProtocol)newValue);
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSJ2C:
				setProtocolCicsj2c((CICSJ2CProtocol)newValue);
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSSSL:
				setProtocolCicsssl((CICSSSLProtocol)newValue);
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSWS:
				setProtocolCicsws((CICSWSProtocol)newValue);
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSJ2C:
				setProtocolImsj2c((IMSJ2CProtocol)newValue);
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSTCP:
				setProtocolImstcp((IMSTCPProtocol)newValue);
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400:
				setProtocolJava400((Java400Protocol)newValue);
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400J2C:
				setProtocolJava400j2c((Java400J2cProtocol)newValue);
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_LOCAL:
				setProtocolLocal((LocalProtocol)newValue);
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_REF:
				setProtocolRef((ReferenceProtocol)newValue);
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_SYSTEM_ILOCAL:
				setProtocolSystemILocal((SystemIProtocol)newValue);
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_TCPIP:
				setProtocolTcpip((TCPIPProtocol)newValue);
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__TARGET_BUILD_DESCRIPTOR:
				setTargetBuildDescriptor((DeploymentBuildDescriptor)newValue);
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
	public void eUnset(int featureID) {
		switch (featureID) {
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
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSECI:
				setProtocolCicseci((CICSECIProtocol)null);
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSJ2C:
				setProtocolCicsj2c((CICSJ2CProtocol)null);
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSSSL:
				setProtocolCicsssl((CICSSSLProtocol)null);
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSWS:
				setProtocolCicsws((CICSWSProtocol)null);
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSJ2C:
				setProtocolImsj2c((IMSJ2CProtocol)null);
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSTCP:
				setProtocolImstcp((IMSTCPProtocol)null);
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400:
				setProtocolJava400((Java400Protocol)null);
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400J2C:
				setProtocolJava400j2c((Java400J2cProtocol)null);
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_LOCAL:
				setProtocolLocal((LocalProtocol)null);
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_REF:
				setProtocolRef((ReferenceProtocol)null);
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_SYSTEM_ILOCAL:
				setProtocolSystemILocal((SystemIProtocol)null);
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_TCPIP:
				setProtocolTcpip((TCPIPProtocol)null);
				return;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__TARGET_BUILD_DESCRIPTOR:
				setTargetBuildDescriptor((DeploymentBuildDescriptor)null);
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
	public boolean eIsSet(int featureID) {
		switch (featureID) {
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
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL:
				return getProtocol() != null;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSECI:
				return getProtocolCicseci() != null;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSJ2C:
				return getProtocolCicsj2c() != null;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSSSL:
				return getProtocolCicsssl() != null;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSWS:
				return getProtocolCicsws() != null;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSJ2C:
				return getProtocolImsj2c() != null;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSTCP:
				return getProtocolImstcp() != null;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400:
				return getProtocolJava400() != null;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400J2C:
				return getProtocolJava400j2c() != null;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_LOCAL:
				return getProtocolLocal() != null;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_REF:
				return getProtocolRef() != null;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_SYSTEM_ILOCAL:
				return getProtocolSystemILocal() != null;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__PROTOCOL_TCPIP:
				return getProtocolTcpip() != null;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__TARGET:
				return getTarget() != null;
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT__TARGET_BUILD_DESCRIPTOR:
				return getTargetBuildDescriptor() != null;
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
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (mixed: ");
		result.append(mixed);
		result.append(')');
		return result.toString();
	}

} //EGLDeploymentRootImpl
