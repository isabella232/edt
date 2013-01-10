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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Preference</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#getPreview <em>Preview</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#getControlGroup <em>Control Group</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#getControl <em>Control</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#getAltDisplay <em>Alt Display</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#getDisplay <em>Display</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#getValue <em>Value</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#isVisible <em>Visible</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getPreference()
 * @model extendedMetaData="name='Preference' kind='elementOnly'"
 * @generated
 */
public interface Preference extends EObject {
	/**
	 * Returns the value of the '<em><b>Preview</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Preview</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Preview</em>' containment reference.
	 * @see #setPreview(Preview)
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getPreference_Preview()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='preview'"
	 * @generated
	 */
	Preview getPreview();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#getPreview <em>Preview</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Preview</em>' containment reference.
	 * @see #getPreview()
	 * @generated
	 */
	void setPreview(Preview value);

	/**
	 * Returns the value of the '<em><b>Control Group</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Control Group</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Control Group</em>' attribute list.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getPreference_ControlGroup()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry"
	 *        extendedMetaData="kind='group' name='control:group' namespace='##targetNamespace'"
	 * @generated
	 */
	FeatureMap getControlGroup();

	/**
	 * Returns the value of the '<em><b>Control</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Control</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Control</em>' containment reference.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getPreference_Control()
	 * @model containment="true" transient="true" changeable="false" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='control' namespace='##targetNamespace' group='control:group'"
	 * @generated
	 */
	Control getControl();

	/**
	 * Returns the value of the '<em><b>Alt Display</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Alt Display</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Alt Display</em>' attribute.
	 * @see #setAltDisplay(String)
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getPreference_AltDisplay()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='altDisplay'"
	 * @generated
	 */
	String getAltDisplay();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#getAltDisplay <em>Alt Display</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Alt Display</em>' attribute.
	 * @see #getAltDisplay()
	 * @generated
	 */
	void setAltDisplay(String value);

	/**
	 * Returns the value of the '<em><b>Display</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Display</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Display</em>' attribute.
	 * @see #setDisplay(String)
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getPreference_Display()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='display'"
	 * @generated
	 */
	String getDisplay();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#getDisplay <em>Display</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Display</em>' attribute.
	 * @see #getDisplay()
	 * @generated
	 */
	void setDisplay(String value);

	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getPreference_Id()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.NCName"
	 *        extendedMetaData="kind='attribute' name='id'"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Value</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value</em>' attribute.
	 * @see #setValue(String)
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getPreference_Value()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='value'"
	 * @generated
	 */
	String getValue();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#getValue <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' attribute.
	 * @see #getValue()
	 * @generated
	 */
	void setValue(String value);

	/**
	 * Returns the value of the '<em><b>Visible</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Visible</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Visible</em>' attribute.
	 * @see #isSetVisible()
	 * @see #unsetVisible()
	 * @see #setVisible(boolean)
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage#getPreference_Visible()
	 * @model default="true" unique="false" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='visible'"
	 * @generated
	 */
	boolean isVisible();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#isVisible <em>Visible</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Visible</em>' attribute.
	 * @see #isSetVisible()
	 * @see #unsetVisible()
	 * @see #isVisible()
	 * @generated
	 */
	void setVisible(boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#isVisible <em>Visible</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetVisible()
	 * @see #isVisible()
	 * @see #setVisible(boolean)
	 * @generated
	 */
	void unsetVisible();

	/**
	 * Returns whether the value of the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#isVisible <em>Visible</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Visible</em>' attribute is set.
	 * @see #unsetVisible()
	 * @see #isVisible()
	 * @see #setVisible(boolean)
	 * @generated
	 */
	boolean isSetVisible();

} // Preference
