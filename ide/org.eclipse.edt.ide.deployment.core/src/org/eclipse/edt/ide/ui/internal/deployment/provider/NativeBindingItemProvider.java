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
package org.eclipse.edt.ide.ui.internal.deployment.provider;


import java.util.Collection;
import java.util.List;

import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage;
import org.eclipse.edt.ide.ui.internal.deployment.NativeBinding;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.ResourceLocator;

import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.ecore.util.FeatureMapUtil;

import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;

/**
 * This is the item provider adapter for a {@link org.eclipse.edt.ide.ui.internal.deployment.NativeBinding} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class NativeBindingItemProvider
	extends ItemProviderAdapter
	implements
		IEditingDomainItemProvider,
		IStructuredItemContentProvider,
		ITreeItemContentProvider,
		IItemLabelProvider,
		IItemPropertySource {
	/**
	 * This constructs an instance from a factory and a notifier.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NativeBindingItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * This returns the property descriptors for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
		if (itemPropertyDescriptors == null) {
			super.getPropertyDescriptors(object);

			addNamePropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Name feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addNamePropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_NativeBinding_name_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_NativeBinding_name_feature", "_UI_NativeBinding_type"),
				 DeploymentPackage.Literals.NATIVE_BINDING__NAME,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
	 * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
	 * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object) {
		if (childrenFeatures == null) {
			super.getChildrenFeatures(object);
			childrenFeatures.add(DeploymentPackage.Literals.NATIVE_BINDING__PROTOCOL_GROUP);
		}
		return childrenFeatures;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EStructuralFeature getChildFeature(Object object, Object child) {
		// Check the type of the specified child object and return the proper feature to use for
		// adding (see {@link AddCommand}) it as a child.

		return super.getChildFeature(object, child);
	}

	/**
	 * This returns NativeBinding.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(object, getResourceLocator().getImage("full/obj16/NativeBinding"));
	}

	/**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getText(Object object) {
		String label = ((NativeBinding)object).getName();
		return label == null || label.length() == 0 ?
			getString("_UI_NativeBinding_type") :
			getString("_UI_NativeBinding_type") + " " + label;
	}

	/**
	 * This handles model notifications by calling {@link #updateChildren} to update any cached
	 * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void notifyChanged(Notification notification) {
		updateChildren(notification);

		switch (notification.getFeatureID(NativeBinding.class)) {
			case DeploymentPackage.NATIVE_BINDING__NAME:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
				return;
			case DeploymentPackage.NATIVE_BINDING__PROTOCOL_GROUP:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
				return;
		}
		super.notifyChanged(notification);
	}

	/**
	 * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
	 * that can be created under this object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
		super.collectNewChildDescriptors(newChildDescriptors, object);

		newChildDescriptors.add
			(createChildParameter
				(DeploymentPackage.Literals.NATIVE_BINDING__PROTOCOL_GROUP,
				 FeatureMapUtil.createEntry
					(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSECI,
					 DeploymentFactory.eINSTANCE.createCICSECIProtocol())));

		newChildDescriptors.add
			(createChildParameter
				(DeploymentPackage.Literals.NATIVE_BINDING__PROTOCOL_GROUP,
				 FeatureMapUtil.createEntry
					(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSJ2C,
					 DeploymentFactory.eINSTANCE.createCICSJ2CProtocol())));

		newChildDescriptors.add
			(createChildParameter
				(DeploymentPackage.Literals.NATIVE_BINDING__PROTOCOL_GROUP,
				 FeatureMapUtil.createEntry
					(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSSSL,
					 DeploymentFactory.eINSTANCE.createCICSSSLProtocol())));

		newChildDescriptors.add
			(createChildParameter
				(DeploymentPackage.Literals.NATIVE_BINDING__PROTOCOL_GROUP,
				 FeatureMapUtil.createEntry
					(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSWS,
					 DeploymentFactory.eINSTANCE.createCICSWSProtocol())));

		newChildDescriptors.add
			(createChildParameter
				(DeploymentPackage.Literals.NATIVE_BINDING__PROTOCOL_GROUP,
				 FeatureMapUtil.createEntry
					(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSJ2C,
					 DeploymentFactory.eINSTANCE.createIMSJ2CProtocol())));

		newChildDescriptors.add
			(createChildParameter
				(DeploymentPackage.Literals.NATIVE_BINDING__PROTOCOL_GROUP,
				 FeatureMapUtil.createEntry
					(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSTCP,
					 DeploymentFactory.eINSTANCE.createIMSTCPProtocol())));

		newChildDescriptors.add
			(createChildParameter
				(DeploymentPackage.Literals.NATIVE_BINDING__PROTOCOL_GROUP,
				 FeatureMapUtil.createEntry
					(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400,
					 DeploymentFactory.eINSTANCE.createJava400Protocol())));

		newChildDescriptors.add
			(createChildParameter
				(DeploymentPackage.Literals.NATIVE_BINDING__PROTOCOL_GROUP,
				 FeatureMapUtil.createEntry
					(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400J2C,
					 DeploymentFactory.eINSTANCE.createJava400J2cProtocol())));

		newChildDescriptors.add
			(createChildParameter
				(DeploymentPackage.Literals.NATIVE_BINDING__PROTOCOL_GROUP,
				 FeatureMapUtil.createEntry
					(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_LOCAL,
					 DeploymentFactory.eINSTANCE.createLocalProtocol())));

		newChildDescriptors.add
			(createChildParameter
				(DeploymentPackage.Literals.NATIVE_BINDING__PROTOCOL_GROUP,
				 FeatureMapUtil.createEntry
					(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_REF,
					 DeploymentFactory.eINSTANCE.createReferenceProtocol())));

		newChildDescriptors.add
			(createChildParameter
				(DeploymentPackage.Literals.NATIVE_BINDING__PROTOCOL_GROUP,
				 FeatureMapUtil.createEntry
					(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_SYSTEM_ILOCAL,
					 DeploymentFactory.eINSTANCE.createSystemIProtocol())));

		newChildDescriptors.add
			(createChildParameter
				(DeploymentPackage.Literals.NATIVE_BINDING__PROTOCOL_GROUP,
				 FeatureMapUtil.createEntry
					(DeploymentPackage.Literals.EGL_DEPLOYMENT_ROOT__PROTOCOL_TCPIP,
					 DeploymentFactory.eINSTANCE.createTCPIPProtocol())));
	}

	/**
	 * Return the resource locator for this item provider's resources.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ResourceLocator getResourceLocator() {
		return EglddEditPlugin.INSTANCE;
	}

}
