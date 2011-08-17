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


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>CICSSSL Protocol</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol#getConversionTable <em>Conversion Table</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol#getCtgKeyStore <em>Ctg Key Store</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol#getCtgKeyStorePassword <em>Ctg Key Store Password</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol#getCtgLocation <em>Ctg Location</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol#getCtgPort <em>Ctg Port</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol#getLocation <em>Location</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol#getServerID <em>Server ID</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getCICSSSLProtocol()
 * @model extendedMetaData="name='CICSSSLProtocol' kind='empty'"
 * @generated
 */
public interface CICSSSLProtocol extends Protocol {
	/**
	 * Returns the value of the '<em><b>Conversion Table</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Conversion Table</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Conversion Table</em>' attribute.
	 * @see #setConversionTable(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getCICSSSLProtocol_ConversionTable()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='conversionTable'"
	 * @generated
	 */
	String getConversionTable();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol#getConversionTable <em>Conversion Table</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Conversion Table</em>' attribute.
	 * @see #getConversionTable()
	 * @generated
	 */
	void setConversionTable(String value);

	/**
	 * Returns the value of the '<em><b>Ctg Key Store</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ctg Key Store</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ctg Key Store</em>' attribute.
	 * @see #setCtgKeyStore(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getCICSSSLProtocol_CtgKeyStore()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='ctgKeyStore'"
	 * @generated
	 */
	String getCtgKeyStore();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol#getCtgKeyStore <em>Ctg Key Store</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ctg Key Store</em>' attribute.
	 * @see #getCtgKeyStore()
	 * @generated
	 */
	void setCtgKeyStore(String value);

	/**
	 * Returns the value of the '<em><b>Ctg Key Store Password</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ctg Key Store Password</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ctg Key Store Password</em>' attribute.
	 * @see #setCtgKeyStorePassword(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getCICSSSLProtocol_CtgKeyStorePassword()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='ctgKeyStorePassword'"
	 * @generated
	 */
	String getCtgKeyStorePassword();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol#getCtgKeyStorePassword <em>Ctg Key Store Password</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ctg Key Store Password</em>' attribute.
	 * @see #getCtgKeyStorePassword()
	 * @generated
	 */
	void setCtgKeyStorePassword(String value);

	/**
	 * Returns the value of the '<em><b>Ctg Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ctg Location</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ctg Location</em>' attribute.
	 * @see #setCtgLocation(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getCICSSSLProtocol_CtgLocation()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='ctgLocation'"
	 * @generated
	 */
	String getCtgLocation();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol#getCtgLocation <em>Ctg Location</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ctg Location</em>' attribute.
	 * @see #getCtgLocation()
	 * @generated
	 */
	void setCtgLocation(String value);

	/**
	 * Returns the value of the '<em><b>Ctg Port</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ctg Port</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ctg Port</em>' attribute.
	 * @see #setCtgPort(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getCICSSSLProtocol_CtgPort()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='ctgPort'"
	 * @generated
	 */
	String getCtgPort();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol#getCtgPort <em>Ctg Port</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ctg Port</em>' attribute.
	 * @see #getCtgPort()
	 * @generated
	 */
	void setCtgPort(String value);

	/**
	 * Returns the value of the '<em><b>Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Location</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Location</em>' attribute.
	 * @see #setLocation(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getCICSSSLProtocol_Location()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='location'"
	 * @generated
	 */
	String getLocation();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol#getLocation <em>Location</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Location</em>' attribute.
	 * @see #getLocation()
	 * @generated
	 */
	void setLocation(String value);

	/**
	 * Returns the value of the '<em><b>Server ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Server ID</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Server ID</em>' attribute.
	 * @see #setServerID(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getCICSSSLProtocol_ServerID()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='serverID'"
	 * @generated
	 */
	String getServerID();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol#getServerID <em>Server ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Server ID</em>' attribute.
	 * @see #getServerID()
	 * @generated
	 */
	void setServerID(String value);

} // CICSSSLProtocol
