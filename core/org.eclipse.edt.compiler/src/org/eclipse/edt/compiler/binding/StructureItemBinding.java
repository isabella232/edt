/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.binding;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.core.ast.Primitive;


/**
 * Data binding class for items in fixed records and dataTables.
 */
public class StructureItemBinding extends DataBinding {

	private Object initialValue;

    private List children = Collections.EMPTY_LIST;

    private transient Map unqualifiedNamesToDataBindings;

    transient StructureItemBinding parentItem;

    transient FixedStructureBinding enclosingStructureBinding;

    private int occurs = -1;
    
    private transient List occursDimensions;

    private transient int length = -1;
    
    private boolean readOnly;
    
    protected transient DataItemBinding dataItemReference;


    public StructureItemBinding(String caseSensitiveInternedName, IPartBinding declarer, ITypeBinding typeBinding) {
        super(caseSensitiveInternedName, declarer, typeBinding);
    }

    public int getKind() {
        return STRUCTURE_ITEM_BINDING;
    }

    /**
     * @return A list of StructureItemBinding objects.
     */
    public List getChildren() {
        return children;
    }

    public void addChild(StructureItemBinding itemBinding) {
        if (children == Collections.EMPTY_LIST) {
            children = new ArrayList();
        }
        children.add(itemBinding);
        itemBinding.setParentItem(this);
    }

    public StructureItemBinding getParentItem() {
        return parentItem;
    }

    void setParentItem(StructureItemBinding parentItem) {
        this.parentItem = parentItem;
    }

    /**
     * @return The fixed record or data table binding which contains this
     *         structure item.
     */
    public FixedStructureBinding getEnclosingStructureBinding() {
        if (enclosingStructureBinding == null && parentItem != null) {
            enclosingStructureBinding = parentItem.getEnclosingStructureBinding();
        }
        return enclosingStructureBinding;
    }
    
    public IPartBinding getDeclaringPart() {
    	if (super.getDeclaringPart() == null && parentItem != null) {
    		return parentItem.getDeclaringPart();
    	}
    	return super.getDeclaringPart();
    }

    void setEnclosingStructureBinding(FixedStructureBinding structureBinding) {
        enclosingStructureBinding = structureBinding;
    }

    public boolean isMultiplyOccuring() {
        return hasOccurs() || parentItem != null && parentItem.isMultiplyOccuring();
    }
    
    public boolean isReadOnly() {
		return readOnly;
	}
    
    public void setIsReadOnly(boolean readOnly) {
    	this.readOnly = readOnly;
    }

    public int getOccurs() {
        return occurs;
    }
    
    public int numOccursDimensions() {
    	return getOccursDimensions().size();
    }
    
    public List getOccursDimensions() {
    	if(occursDimensions == null) {
    		if(parentItem != null) {
    			occursDimensions = new ArrayList(parentItem.getOccursDimensions());
    		}
    		else {
    			occursDimensions = new ArrayList();
    		}
    		if(hasOccurs()) {
    			occursDimensions.add(new Integer(getOccurs()));
    		}
    	}
    	return occursDimensions;
    }

    public void setOccurs(int occurs) {
        this.occurs = occurs;
    }

    /**
     * @hasOccurs will return true if the item was explicitly defined with an occurs greater than 1
     */
    public boolean hasOccurs() {
        return occurs > 1;
    }

    /**
     * @defineWithOccurs will return true if the item was explicitly defined with an occurs of 1 or more
     */
    public boolean definedWithOccurs() {
        return occurs > 0;
    }

    public ITypeBinding getType() {
        if (Binding.isValidBinding(typeBinding) && typeBinding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING && ((PrimitiveTypeBinding)typeBinding).getLength() == -1) {
            // Force length in bytes to be calculated, which sets the type
            // binding to a primtive with the appropriate length
            getLengthInBytes();
        }
        return typeBinding;
    }

    /**
     * @return The length of this field in bytes.
     */
    public int getLengthInBytes() {
        if (length == -1) {

            if (Binding.isValidBinding(typeBinding) && typeBinding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING ) {
                if (((PrimitiveTypeBinding)typeBinding).getLength() == -1) {
                    // (CHAR, -1) signals an untyped item or typedef to another
                    // fixed record. A length of -1 means that a type was specified with no length.
                	// In this case the length is the sum of the children structure items
                	
                    length = 0;
                    for (Iterator iter = children.iterator(); iter.hasNext();) {
                    	
                		StructureItemBinding siBinding = (StructureItemBinding) iter.next();
                		
                		//Do not count redefined storage in the size
                		if (siBinding.getAnnotation(new String[] { "egl", "core" }, "Redefines") == null) {
                            length += siBinding.getLengthInBytes();
                		}                    	
                    }
                    typeBinding = createTypeBinding(((PrimitiveTypeBinding)typeBinding).getPrimitive(), length);
                } else {
                    length = ((PrimitiveTypeBinding) typeBinding).getBytes();
                }
            }

            if (hasOccurs()) {
                length *= occurs;
            }
        }
        return length;
    }

    
    private ITypeBinding createTypeBinding(Primitive prim, int bytes) { 
    	return PrimitiveTypeBinding.getInstance(prim, getLength(prim, bytes));
    }  			

    private int getLength(Primitive prim, int bytes) {
		switch (prim.getType()) {
		case Primitive.BIN_PRIMITIVE:
			if (bytes == 2)
				return 4;
			else if (bytes == 4)
				return 9;
			else if (bytes == 8)
				return 18;
			break;
		case Primitive.MONEY_PRIMITIVE:
		case Primitive.DECIMAL_PRIMITIVE:
		case Primitive.PACF_PRIMITIVE:
			return bytes * 2 - 1;
		case Primitive.UNICODE_PRIMITIVE:
		case Primitive.STRING_PRIMITIVE:
		case Primitive.DBCHAR_PRIMITIVE:
			return bytes / 2;
		case Primitive.HEX_PRIMITIVE:
			return bytes * 2;

		default:
			break;
		}

		return bytes;
	}

    	
    public IDataBinding copyDataBinding(HashMap itemMapping) {
        StructureItemBinding newItem = new StructureItemBinding(getCaseSensitiveName(), null, typeBinding.copyTypeBinding());
        newItem.occurs = occurs;
        newItem.length = length;
        newItem.initialValue = initialValue;
        newItem.dataItemReference = dataItemReference;
        if (annotations != Collections.EMPTY_LIST) {
        	
            newItem.annotations = new ArrayList(annotations);
        }
        else {
            newItem.annotations = annotations;
        }
        itemMapping.put(this, newItem);
        if (children == Collections.EMPTY_LIST) {
            newItem.children = Collections.EMPTY_LIST;
        } else {
            newItem.children = new ArrayList();
            for (Iterator iter = children.iterator(); iter.hasNext();) {
                StructureItemBinding copiedChild = (StructureItemBinding) ((StructureItemBinding) iter.next()).copyDataBinding(itemMapping);
                newItem.children.add(copiedChild);
                copiedChild.setParentItem(newItem);
            }
        }
        return newItem;
    }
    
    public IDataBinding findData(String simpleName) {
    	//First look for top-level child
    	for(Iterator iter = children.iterator(); iter.hasNext();) {
            IDataBinding binding = (IDataBinding) iter.next();
            if(binding.getName() == simpleName) {
                return binding;
            }
        }
    	
    	//Then look in children of children
        IDataBinding result = (IDataBinding) getSimpleNamesToDataBindingsMap().get(simpleName);
        if(result == null) {
        	result = IBinding.NOT_FOUND_BINDING;
        }
        return result;
    }
    
    public void clearSimpleNamesToDataBindingsMap() {
    	unqualifiedNamesToDataBindings = null;
    	for(Iterator iter = children.iterator(); iter.hasNext();) {
    		((StructureItemBinding) iter.next()).clearSimpleNamesToDataBindingsMap();
    	}
	}

    public Map getSimpleNamesToDataBindingsMap() {
        if (unqualifiedNamesToDataBindings == null) {
            if (children == Collections.EMPTY_LIST) {
                unqualifiedNamesToDataBindings = Collections.EMPTY_MAP;
            } else {
                unqualifiedNamesToDataBindings = new HashMap();
                for (Iterator iter = children.iterator(); iter.hasNext();) {
                    StructureItemBinding nextItem = (StructureItemBinding) iter.next();
                    BindingUtilities.addToUnqualifiedBindingNameMap(unqualifiedNamesToDataBindings, null, nextItem);
                    BindingUtilities.addAllToUnqualifiedBindingNameMap(unqualifiedNamesToDataBindings, null, nextItem
                            .getSimpleNamesToDataBindingsMap());
                }
            }
        }
        return unqualifiedNamesToDataBindings;
    }
    
    /*
     * Returns the name of the item, qualified with the name of all of it's parent item names. 
     */
    
    public String getParentQualifiedName() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getCaseSensitiveName());
        StructureItemBinding curParent = getParentItem();
        while (curParent != null) {
            if (!("*".equals(curParent.getName()))) {
                buffer.insert(0, curParent.getCaseSensitiveName() + ".");
            }
            curParent = curParent.getParentItem();
        }
        return buffer.toString();
    }
    
    public boolean hasInitialValue() {
    	return initialValue != null;
    }
    
    public Object getInitialValue() {
    	return initialValue;    	
    }
    
    public void setInitialValue(Object initialValue) {
    	this.initialValue = initialValue;
    }    
    
    public void setDataItemReference(DataItemBinding item) {
    	dataItemReference = item;
    }
    
    public DataItemBinding getDataItemReference() {
    	return dataItemReference;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
    	out.defaultWriteObject();
        writeTypeBindingReference(out, dataItemReference);
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    	in.defaultReadObject();
        dataItemReference = (DataItemBinding)readTypeBindingReference(in);
        length = -1;
    }
    
    public void resetChildren() {
    	children = Collections.EMPTY_LIST;
    }
}
