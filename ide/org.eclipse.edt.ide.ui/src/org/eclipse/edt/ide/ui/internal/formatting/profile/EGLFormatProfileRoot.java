/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.formatting.profile;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EGL Format Profile Root</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getMixed <em>Mixed</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getXSISchemaLocation <em>XSI Schema Location</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getControl <em>Control</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getControlCheck <em>Control Check</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getControlCombo <em>Control Combo</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getControlRadio <em>Control Radio</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getControlRef <em>Control Ref</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getControlText <em>Control Text</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getControlTree <em>Control Tree</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getFormatProfiles <em>Format Profiles</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getEGLFormatProfileRoot()
 * @model extendedMetaData="name='' kind='mixed'"
 * @generated
 */
public interface EGLFormatProfileRoot extends EObject {
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
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getEGLFormatProfileRoot_Mixed()
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
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getEGLFormatProfileRoot_XMLNSPrefixMap()
	 * @model mapType="org.eclipse.emf.ecore.EStringToStringMapEntry" keyType="java.lang.String" valueType="java.lang.String" transient="true"
	 *        extendedMetaData="kind='attribute' name='xmlns:prefix'"
	 * @generated
	 */
	EMap getXMLNSPrefixMap();

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
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getEGLFormatProfileRoot_XSISchemaLocation()
	 * @model mapType="org.eclipse.emf.ecore.EStringToStringMapEntry" keyType="java.lang.String" valueType="java.lang.String" transient="true"
	 *        extendedMetaData="kind='attribute' name='xsi:schemaLocation'"
	 * @generated
	 */
	EMap getXSISchemaLocation();

	/**
	 * Returns the value of the '<em><b>Control</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Control</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Control</em>' containment reference.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getEGLFormatProfileRoot_Control()
	 * @model containment="true" upper="-2" transient="true" changeable="false" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='control' namespace='##targetNamespace'"
	 * @generated
	 */
	Control getControl();

	/**
	 * Returns the value of the '<em><b>Control Check</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Control Check</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Control Check</em>' containment reference.
	 * @see #setControlCheck(CheckControl)
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getEGLFormatProfileRoot_ControlCheck()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='control.check' namespace='##targetNamespace' affiliation='control'"
	 * @generated
	 */
	CheckControl getControlCheck();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getControlCheck <em>Control Check</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Control Check</em>' containment reference.
	 * @see #getControlCheck()
	 * @generated
	 */
	void setControlCheck(CheckControl value);

	/**
	 * Returns the value of the '<em><b>Control Combo</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Control Combo</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Control Combo</em>' containment reference.
	 * @see #setControlCombo(ComboControl)
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getEGLFormatProfileRoot_ControlCombo()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='control.combo' namespace='##targetNamespace' affiliation='control'"
	 * @generated
	 */
	ComboControl getControlCombo();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getControlCombo <em>Control Combo</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Control Combo</em>' containment reference.
	 * @see #getControlCombo()
	 * @generated
	 */
	void setControlCombo(ComboControl value);

	/**
	 * Returns the value of the '<em><b>Control Radio</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Control Radio</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Control Radio</em>' containment reference.
	 * @see #setControlRadio(RadioControl)
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getEGLFormatProfileRoot_ControlRadio()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='control.radio' namespace='##targetNamespace' affiliation='control'"
	 * @generated
	 */
	RadioControl getControlRadio();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getControlRadio <em>Control Radio</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Control Radio</em>' containment reference.
	 * @see #getControlRadio()
	 * @generated
	 */
	void setControlRadio(RadioControl value);

	/**
	 * Returns the value of the '<em><b>Control Ref</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Control Ref</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Control Ref</em>' containment reference.
	 * @see #setControlRef(ReferenceControl)
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getEGLFormatProfileRoot_ControlRef()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='control.ref' namespace='##targetNamespace' affiliation='control'"
	 * @generated
	 */
	ReferenceControl getControlRef();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getControlRef <em>Control Ref</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Control Ref</em>' containment reference.
	 * @see #getControlRef()
	 * @generated
	 */
	void setControlRef(ReferenceControl value);

	/**
	 * Returns the value of the '<em><b>Control Text</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Control Text</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Control Text</em>' containment reference.
	 * @see #setControlText(TextControl)
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getEGLFormatProfileRoot_ControlText()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='control.text' namespace='##targetNamespace' affiliation='control'"
	 * @generated
	 */
	TextControl getControlText();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getControlText <em>Control Text</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Control Text</em>' containment reference.
	 * @see #getControlText()
	 * @generated
	 */
	void setControlText(TextControl value);

	/**
	 * Returns the value of the '<em><b>Control Tree</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Control Tree</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Control Tree</em>' containment reference.
	 * @see #setControlTree(TreeControl)
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getEGLFormatProfileRoot_ControlTree()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='control.tree' namespace='##targetNamespace' affiliation='control'"
	 * @generated
	 */
	TreeControl getControlTree();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getControlTree <em>Control Tree</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Control Tree</em>' containment reference.
	 * @see #getControlTree()
	 * @generated
	 */
	void setControlTree(TreeControl value);

	/**
	 * Returns the value of the '<em><b>Format Profiles</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Format Profiles</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Format Profiles</em>' containment reference.
	 * @see #setFormatProfiles(FormatProfiles)
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getEGLFormatProfileRoot_FormatProfiles()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='format_profiles' namespace='##targetNamespace'"
	 * @generated
	 */
	FormatProfiles getFormatProfiles();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getFormatProfiles <em>Format Profiles</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Format Profiles</em>' containment reference.
	 * @see #getFormatProfiles()
	 * @generated
	 */
	void setFormatProfiles(FormatProfiles value);

} // EGLFormatProfileRoot
