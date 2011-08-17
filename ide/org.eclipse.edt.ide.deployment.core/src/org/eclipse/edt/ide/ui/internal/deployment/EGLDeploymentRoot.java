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
package org.eclipse.edt.ide.ui.internal.deployment;

import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EGL Deployment Root</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getMixed <em>Mixed</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getDeployExt <em>Deploy Ext</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getDeployment <em>Deployment</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocol <em>Protocol</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolCicseci <em>Protocol Cicseci</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolCicsj2c <em>Protocol Cicsj2c</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolCicsssl <em>Protocol Cicsssl</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolCicsws <em>Protocol Cicsws</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolImsj2c <em>Protocol Imsj2c</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolImstcp <em>Protocol Imstcp</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolJava400 <em>Protocol Java400</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolJava400j2c <em>Protocol Java400j2c</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolLocal <em>Protocol Local</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolRef <em>Protocol Ref</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolSystemILocal <em>Protocol System ILocal</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolTcpip <em>Protocol Tcpip</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getTarget <em>Target</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getTargetBuildDescriptor <em>Target Build Descriptor</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getTargetProject <em>Target Project</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getEGLDeploymentRoot()
 * @model extendedMetaData="name='' kind='mixed'"
 * @generated
 */
public interface EGLDeploymentRoot extends EObject {
	/**
	 * Returns the value of the '<em><b>Mixed</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Mixed</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mixed</em>' attribute list.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getEGLDeploymentRoot_Mixed()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='elementWildcard' name=':mixed'"
	 * @generated
	 */
	FeatureMap getMixed();

	/**
	 * Returns the value of the '<em><b>XMLNS Prefix Map</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link java.lang.String},
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>XMLNS Prefix Map</em>' map isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>XMLNS Prefix Map</em>' map.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getEGLDeploymentRoot_XMLNSPrefixMap()
	 * @model mapType="org.eclipse.emf.ecore.EStringToStringMapEntry<org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString>" transient="true"
	 *        extendedMetaData="kind='attribute' name='xmlns:prefix'"
	 * @generated
	 */
	EMap<String, String> getXMLNSPrefixMap();

	/**
	 * Returns the value of the '<em><b>XSI Schema Location</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link java.lang.String},
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>XSI Schema Location</em>' map isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>XSI Schema Location</em>' map.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getEGLDeploymentRoot_XSISchemaLocation()
	 * @model mapType="org.eclipse.emf.ecore.EStringToStringMapEntry<org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString>" transient="true"
	 *        extendedMetaData="kind='attribute' name='xsi:schemaLocation'"
	 * @generated
	 */
	EMap<String, String> getXSISchemaLocation();

	/**
	 * Returns the value of the '<em><b>Deploy Ext</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Deploy Ext</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Deploy Ext</em>' containment reference.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getEGLDeploymentRoot_DeployExt()
	 * @model containment="true" upper="-2" transient="true" changeable="false" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='deploy-ext' namespace='##targetNamespace'"
	 * @generated
	 */
	DeployExt getDeployExt();

	/**
	 * Returns the value of the '<em><b>Deployment</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Deployment</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Deployment</em>' containment reference.
	 * @see #setDeployment(Deployment)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getEGLDeploymentRoot_Deployment()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='deployment' namespace='##targetNamespace'"
	 * @generated
	 */
	Deployment getDeployment();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getDeployment <em>Deployment</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Deployment</em>' containment reference.
	 * @see #getDeployment()
	 * @generated
	 */
	void setDeployment(Deployment value);

	/**
	 * Returns the value of the '<em><b>Protocol</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Protocol</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Protocol</em>' containment reference.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getEGLDeploymentRoot_Protocol()
	 * @model containment="true" upper="-2" transient="true" changeable="false" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='protocol' namespace='##targetNamespace'"
	 * @generated
	 */
	Protocol getProtocol();

	/**
	 * Returns the value of the '<em><b>Protocol Cicseci</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Protocol Cicseci</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Protocol Cicseci</em>' containment reference.
	 * @see #setProtocolCicseci(CICSECIProtocol)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getEGLDeploymentRoot_ProtocolCicseci()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='protocol.cicseci' namespace='##targetNamespace' affiliation='protocol'"
	 * @generated
	 */
	CICSECIProtocol getProtocolCicseci();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolCicseci <em>Protocol Cicseci</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Protocol Cicseci</em>' containment reference.
	 * @see #getProtocolCicseci()
	 * @generated
	 */
	void setProtocolCicseci(CICSECIProtocol value);

	/**
	 * Returns the value of the '<em><b>Protocol Cicsj2c</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Protocol Cicsj2c</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Protocol Cicsj2c</em>' containment reference.
	 * @see #setProtocolCicsj2c(CICSJ2CProtocol)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getEGLDeploymentRoot_ProtocolCicsj2c()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='protocol.cicsj2c' namespace='##targetNamespace' affiliation='protocol'"
	 * @generated
	 */
	CICSJ2CProtocol getProtocolCicsj2c();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolCicsj2c <em>Protocol Cicsj2c</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Protocol Cicsj2c</em>' containment reference.
	 * @see #getProtocolCicsj2c()
	 * @generated
	 */
	void setProtocolCicsj2c(CICSJ2CProtocol value);

	/**
	 * Returns the value of the '<em><b>Protocol Cicsssl</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Protocol Cicsssl</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Protocol Cicsssl</em>' containment reference.
	 * @see #setProtocolCicsssl(CICSSSLProtocol)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getEGLDeploymentRoot_ProtocolCicsssl()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='protocol.cicsssl' namespace='##targetNamespace' affiliation='protocol'"
	 * @generated
	 */
	CICSSSLProtocol getProtocolCicsssl();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolCicsssl <em>Protocol Cicsssl</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Protocol Cicsssl</em>' containment reference.
	 * @see #getProtocolCicsssl()
	 * @generated
	 */
	void setProtocolCicsssl(CICSSSLProtocol value);

	/**
	 * Returns the value of the '<em><b>Protocol Cicsws</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Protocol Cicsws</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Protocol Cicsws</em>' containment reference.
	 * @see #setProtocolCicsws(CICSWSProtocol)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getEGLDeploymentRoot_ProtocolCicsws()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='protocol.cicsws' namespace='##targetNamespace' affiliation='protocol'"
	 * @generated
	 */
	CICSWSProtocol getProtocolCicsws();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolCicsws <em>Protocol Cicsws</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Protocol Cicsws</em>' containment reference.
	 * @see #getProtocolCicsws()
	 * @generated
	 */
	void setProtocolCicsws(CICSWSProtocol value);

	/**
	 * Returns the value of the '<em><b>Protocol Imsj2c</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Protocol Imsj2c</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Protocol Imsj2c</em>' containment reference.
	 * @see #setProtocolImsj2c(IMSJ2CProtocol)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getEGLDeploymentRoot_ProtocolImsj2c()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='protocol.imsj2c' namespace='##targetNamespace' affiliation='protocol'"
	 * @generated
	 */
	IMSJ2CProtocol getProtocolImsj2c();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolImsj2c <em>Protocol Imsj2c</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Protocol Imsj2c</em>' containment reference.
	 * @see #getProtocolImsj2c()
	 * @generated
	 */
	void setProtocolImsj2c(IMSJ2CProtocol value);

	/**
	 * Returns the value of the '<em><b>Protocol Imstcp</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Protocol Imstcp</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Protocol Imstcp</em>' containment reference.
	 * @see #setProtocolImstcp(IMSTCPProtocol)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getEGLDeploymentRoot_ProtocolImstcp()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='protocol.imstcp' namespace='##targetNamespace' affiliation='protocol'"
	 * @generated
	 */
	IMSTCPProtocol getProtocolImstcp();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolImstcp <em>Protocol Imstcp</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Protocol Imstcp</em>' containment reference.
	 * @see #getProtocolImstcp()
	 * @generated
	 */
	void setProtocolImstcp(IMSTCPProtocol value);

	/**
	 * Returns the value of the '<em><b>Protocol Java400</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Protocol Java400</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Protocol Java400</em>' containment reference.
	 * @see #setProtocolJava400(Java400Protocol)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getEGLDeploymentRoot_ProtocolJava400()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='protocol.java400' namespace='##targetNamespace' affiliation='protocol'"
	 * @generated
	 */
	Java400Protocol getProtocolJava400();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolJava400 <em>Protocol Java400</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Protocol Java400</em>' containment reference.
	 * @see #getProtocolJava400()
	 * @generated
	 */
	void setProtocolJava400(Java400Protocol value);

	/**
	 * Returns the value of the '<em><b>Protocol Java400j2c</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Protocol Java400j2c</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Protocol Java400j2c</em>' containment reference.
	 * @see #setProtocolJava400j2c(Java400J2cProtocol)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getEGLDeploymentRoot_ProtocolJava400j2c()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='protocol.java400j2c' namespace='##targetNamespace' affiliation='protocol'"
	 * @generated
	 */
	Java400J2cProtocol getProtocolJava400j2c();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolJava400j2c <em>Protocol Java400j2c</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Protocol Java400j2c</em>' containment reference.
	 * @see #getProtocolJava400j2c()
	 * @generated
	 */
	void setProtocolJava400j2c(Java400J2cProtocol value);

	/**
	 * Returns the value of the '<em><b>Protocol Local</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Protocol Local</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Protocol Local</em>' containment reference.
	 * @see #setProtocolLocal(LocalProtocol)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getEGLDeploymentRoot_ProtocolLocal()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='protocol.local' namespace='##targetNamespace' affiliation='protocol'"
	 * @generated
	 */
	LocalProtocol getProtocolLocal();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolLocal <em>Protocol Local</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Protocol Local</em>' containment reference.
	 * @see #getProtocolLocal()
	 * @generated
	 */
	void setProtocolLocal(LocalProtocol value);

	/**
	 * Returns the value of the '<em><b>Protocol Ref</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Protocol Ref</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Protocol Ref</em>' containment reference.
	 * @see #setProtocolRef(ReferenceProtocol)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getEGLDeploymentRoot_ProtocolRef()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='protocol.ref' namespace='##targetNamespace' affiliation='protocol'"
	 * @generated
	 */
	ReferenceProtocol getProtocolRef();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolRef <em>Protocol Ref</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Protocol Ref</em>' containment reference.
	 * @see #getProtocolRef()
	 * @generated
	 */
	void setProtocolRef(ReferenceProtocol value);

	/**
	 * Returns the value of the '<em><b>Protocol System ILocal</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Protocol System ILocal</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Protocol System ILocal</em>' containment reference.
	 * @see #setProtocolSystemILocal(SystemIProtocol)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getEGLDeploymentRoot_ProtocolSystemILocal()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='protocol.system-i.local' namespace='##targetNamespace' affiliation='protocol'"
	 * @generated
	 */
	SystemIProtocol getProtocolSystemILocal();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolSystemILocal <em>Protocol System ILocal</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Protocol System ILocal</em>' containment reference.
	 * @see #getProtocolSystemILocal()
	 * @generated
	 */
	void setProtocolSystemILocal(SystemIProtocol value);

	/**
	 * Returns the value of the '<em><b>Protocol Tcpip</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Protocol Tcpip</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Protocol Tcpip</em>' containment reference.
	 * @see #setProtocolTcpip(TCPIPProtocol)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getEGLDeploymentRoot_ProtocolTcpip()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='protocol.tcpip' namespace='##targetNamespace' affiliation='protocol'"
	 * @generated
	 */
	TCPIPProtocol getProtocolTcpip();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getProtocolTcpip <em>Protocol Tcpip</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Protocol Tcpip</em>' containment reference.
	 * @see #getProtocolTcpip()
	 * @generated
	 */
	void setProtocolTcpip(TCPIPProtocol value);

	/**
	 * Returns the value of the '<em><b>Target</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Target</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Target</em>' containment reference.
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getEGLDeploymentRoot_Target()
	 * @model containment="true" upper="-2" transient="true" changeable="false" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='target' namespace='##targetNamespace'"
	 * @generated
	 */
	DeploymentTarget getTarget();

	/**
	 * Returns the value of the '<em><b>Target Build Descriptor</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Target Build Descriptor</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Target Build Descriptor</em>' containment reference.
	 * @see #setTargetBuildDescriptor(DeploymentBuildDescriptor)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getEGLDeploymentRoot_TargetBuildDescriptor()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='target.buildDescriptor' namespace='##targetNamespace' affiliation='target'"
	 * @generated
	 */
	DeploymentBuildDescriptor getTargetBuildDescriptor();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getTargetBuildDescriptor <em>Target Build Descriptor</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target Build Descriptor</em>' containment reference.
	 * @see #getTargetBuildDescriptor()
	 * @generated
	 */
	void setTargetBuildDescriptor(DeploymentBuildDescriptor value);

	/**
	 * Returns the value of the '<em><b>Target Project</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Target Project</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Target Project</em>' containment reference.
	 * @see #setTargetProject(DeploymentProject)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getEGLDeploymentRoot_TargetProject()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='target.project' namespace='##targetNamespace' affiliation='target'"
	 * @generated
	 */
	DeploymentProject getTargetProject();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot#getTargetProject <em>Target Project</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target Project</em>' containment reference.
	 * @see #getTargetProject()
	 * @generated
	 */
	void setTargetProject(DeploymentProject value);

} // EGLDeploymentRoot
