/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.outline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.PrimitiveType;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.ide.core.internal.utils.EGLSourceFormatUtil;
import org.eclipse.edt.ide.ui.internal.EGLElementImageDescriptor;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.edt.ide.ui.internal.viewsupport.ElementImageProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.graphics.Image;

public abstract class AbstractOutlineAdapter implements IOutlineAdapter {

	// protected members
	protected ImageDescriptor nodeIcon;
	protected int fImageFlags;
	protected ElementImageProvider fImageLabelProvider = new ElementImageProvider();
	protected EGLEditor editor;

	public AbstractOutlineAdapter(EGLEditor editor) {
		this.editor = editor;
	}

	// This is only for implementing getParent
	protected static OutlineAdapterFactory factory = new OutlineAdapterFactory(null, null);

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		return new Object[0];
	}

	// Default is not to highlight anything special in the source
	public IRegion getHighlightRange(Object element) {
		return new Region(0, 0);
	}
	
	protected IRegion getPartNameHighlightRange(Part part)
	{
		return new Region(part.getName().getOffset(), part.getName().getLength());		
	}

	//	/* (non-Javadoc)
	//	 * @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#getImage(java.lang.Object)
	//	 */
	//	public Image getImage(Object element) {
	//		return null;
	//	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		Node node = (Node) element;
		do {
			node = node.getParent();
		} while (node != null && !factory.isDisplayableElement(node));

		return node;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		return element.getClass().toString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.outline.IOutlineAdapter#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		Object[] children = getChildren(element);
		return children != null && children.length > 0;
	}

	private String formatArrayType(ArrayType arraytype){
		Type elemType = arraytype.getElementType();
		StringBuffer typeDisplay = new StringBuffer(formatType(elemType));

		typeDisplay.append('[');
		if(arraytype.hasInitialSize()){
			Expression expr = arraytype.getInitialSize();
			typeDisplay.append(expr.getCanonicalString());
		}
		typeDisplay.append(']');
		
		return typeDisplay.toString();
	}
	
	// The following methods are generic formatting methods available to subclasses
	public String formatType(Type type) {
		StringBuffer typeDisplay = new StringBuffer();
		if(type instanceof ArrayType){
			typeDisplay.append(formatArrayType((ArrayType)type));
		}
		else{
			typeDisplay.append(type.getCanonicalName());
			
			if(type instanceof PrimitiveType){
				PrimitiveType primType = (PrimitiveType)type;
				if(primType.hasPrimLength()){
					typeDisplay.append('(');
					typeDisplay.append(primType.getPrimLength());
					if(primType.hasPrimDecimals()){
						typeDisplay.append(',');
						typeDisplay.append(primType.getPrimDecimals());
					}
					typeDisplay.append(')');
				}
				else if(primType.hasPrimPattern()){
					typeDisplay.append('(');
					typeDisplay.append(primType.getPrimPattern());
					typeDisplay.append(')');
				}			
			}
		}

		
		return EGLSourceFormatUtil.getInstance().formatForGenerator(typeDisplay.toString());
	}
	
	// this method repopulates the children list to not include any property blocks
	public List filterOutProperties(List children) {
		List modified = new ArrayList();
		for (int i = 0; i < children.size(); i++) {
			if (!(children.get(i) instanceof SettingsBlock))
				modified.add(children.get(i));
		}
		return modified;
	}

	// Return the appropriate icon for the node with an overlay, if necessary,
	// to indicate the highest severity of error or warning. 
	public Image getImage(Object element) {

		fImageFlags = 0;
		Image result = null;

		if (element instanceof Node) {

			Node node = (Node) element;

			if (hasErrorsOrNestedErrors(node))
				fImageFlags = EGLElementImageDescriptor.ERROR;
			else if (hasWarningsOrNestedWarnings(node))
				fImageFlags = EGLElementImageDescriptor.WARNING;
		}

		// Defensive programming -- if the node doesn't have an icon set, just return null
		if (nodeIcon != null)
			result = fImageLabelProvider.getImageLabel(nodeIcon, fImageFlags);

		return result;

	}

	public void dispose() {
	}

	protected boolean hasErrorsOrNestedErrors(Node node) {
		if (editor != null && editor.getNodesWithSavedErrors() != null)
			return editor.getNodesWithSavedErrors().containsKey(node);
		else 
			return false;
	}

	protected boolean hasWarningsOrNestedWarnings(Node node) {
		if (editor != null && editor.getNodesWithSavedWarnings() != null)
			return editor.getNodesWithSavedWarnings().containsKey(node);
		else
			return false;	
	}

	protected String getNameIterationText(Iterator iterator) {
		Object next = iterator.next();
		if (next instanceof Name) {
			Name name = (Name) next;
			String names = name.getCanonicalName();
			while (iterator.hasNext())
				names =  names + ", " + ((Name) iterator.next()).getCanonicalName(); //$NON-NLS-1$
			return names;
		}
		return ""; //$NON-NLS-1$
	}

	protected int getNameIterationLength(Iterator iterator, int beginOffset) {
		Name nameNode = null;
		while (iterator.hasNext())
			nameNode = (Name) iterator.next();
		if (nameNode == null) return 0;
		Node node = (Node) nameNode;
		return node.getOffset() - beginOffset + node.getLength();
	}
}
