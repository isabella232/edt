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

/**
 * @author Dave Murray
 */
public abstract class FixedStructureBinding extends PartBinding {
	
	protected List structureItems = Collections.EMPTY_LIST;
	protected transient Map unqualifiedNamesToDataBindings;	
	
	//Used to detect circularity in embeds and typedefs
    protected transient List referencedStructures = Collections.EMPTY_LIST;
	
	
    public FixedStructureBinding(String[] packageName, String caseSensitiveInternedName) {
        super(packageName, caseSensitiveInternedName);
    }
    
    protected FixedStructureBinding(FixedRecordBinding old) {
    	super(old);
    	
    	if (old.structureItems == Collections.EMPTY_LIST) {
    		old.structureItems = new ArrayList();
    	}
        if (old.referencedStructures == Collections.EMPTY_LIST) {
        	old.referencedStructures = new ArrayList();
        }

    	structureItems = old.structureItems;
    	unqualifiedNamesToDataBindings = old.unqualifiedNamesToDataBindings;	
    	referencedStructures = old.referencedStructures;
    }
	
    /**
     * @return A list of StructureItemBinding objects for the top-level items
     *         in this structure.
     */
    public List getStructureItems() {
		return structureItems;
    }
    
    public void addStructureItem(StructureItemBinding structureItem) {
    	if(structureItems == Collections.EMPTY_LIST) {
    		structureItems = new ArrayList();
    	}
    	structureItems.add(structureItem);
    	structureItem.setEnclosingStructureBinding(this);
    	unqualifiedNamesToDataBindings = null;
    }
    
    public void clear() {
    	super.clear();
    	structureItems = Collections.EMPTY_LIST;
        referencedStructures = Collections.EMPTY_LIST;
        unqualifiedNamesToDataBindings = null;
    }
    
    public boolean containsReferenceTo(FixedStructureBinding structure) {
        List processedStructures = new ArrayList();
        return containsReferenceTo(structure, processedStructures);
    }

    public List getReferencedStructures() {
        return referencedStructures;
    }

    private boolean containsReferenceTo(FixedStructureBinding structure, List processedStructures) {
        Iterator i = getReferencedStructures().iterator();
        while (i.hasNext()) {
            FixedStructureBinding currentStructure = (FixedStructureBinding) i.next();
            if (primContainsReferenceTo(structure, processedStructures, currentStructure)) {
                return true;
            }
            
            FixedStructureBinding resolvedCurrentStructure = (FixedStructureBinding) realizeTypeBinding(currentStructure, getEnvironment());
            if (resolvedCurrentStructure != currentStructure) {
                if (primContainsReferenceTo(structure, processedStructures, resolvedCurrentStructure)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean primContainsReferenceTo(FixedStructureBinding structure, List processedStructures, FixedStructureBinding currentStructure) {
        if (currentStructure == structure) {
            return true;
        }
        if (!processedStructures.contains(currentStructure)) {
            processedStructures.add(currentStructure);
            if (currentStructure.containsReferenceTo(structure, processedStructures)) {
                return true;
            }
        }
        return false;
    }

    protected IDataBinding primFindData(String simpleName) {    	
        for(Iterator iter = structureItems.iterator(); iter.hasNext();) {
            IDataBinding binding = (IDataBinding) iter.next();
            if(binding.getName() == simpleName) {
                return binding;
            }
        }
        return IBinding.NOT_FOUND_BINDING;
    }
    
    public void clearSimpleNamesToDataBindingsMap() {
    	unqualifiedNamesToDataBindings = null;
    	for(Iterator iter = structureItems.iterator(); iter.hasNext();) {
    		((StructureItemBinding) iter.next()).clearSimpleNamesToDataBindingsMap();
    	}
    }
    
    public Map getSimpleNamesToDataBindingsMap() {
    	if(unqualifiedNamesToDataBindings == null || unqualifiedNamesToDataBindings == Collections.EMPTY_MAP) {
    		if(structureItems == Collections.EMPTY_LIST) {
    			unqualifiedNamesToDataBindings = Collections.EMPTY_MAP;
    		}
    		else {
    			unqualifiedNamesToDataBindings = new HashMap();
    			for(Iterator iter = structureItems.iterator(); iter.hasNext();) {
    				StructureItemBinding nextItem = (StructureItemBinding) iter.next();
    				BindingUtilities.addToUnqualifiedBindingNameMap(unqualifiedNamesToDataBindings, null, nextItem);
    				BindingUtilities.addAllToUnqualifiedBindingNameMap(unqualifiedNamesToDataBindings, null, nextItem.getSimpleNamesToDataBindingsMap());
    			}
    		}
    	}    
    	return unqualifiedNamesToDataBindings;
    }
    
    public void addReferencedStructure(FixedStructureBinding structure) {
        if (referencedStructures == Collections.EMPTY_LIST) {
            referencedStructures = new ArrayList();
        }
        if (!referencedStructures.contains(structure)) {
            referencedStructures.add(structure);
        }
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(getReferencedStructures().size());
        Iterator i = getReferencedStructures().iterator();
        while (i.hasNext()) {
            FixedStructureBinding currentStructure = (FixedStructureBinding) i.next();
            writeTypeBindingReference(out, currentStructure);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int size = in.readInt();
        if (size == 0) {
            referencedStructures = Collections.EMPTY_LIST;
        } else {
            referencedStructures = new ArrayList();
            for (int i = 0; i < size; i++) {
                FixedStructureBinding currentStructure = (FixedStructureBinding) readTypeBindingReference(in);
                referencedStructures.add(currentStructure);
            }
        }
        
        for(Iterator iter = structureItems.iterator(); iter.hasNext();) {
        	((StructureItemBinding) iter.next()).setEnclosingStructureBinding(this);
        }
    }
    
    public int getSizeInBytes() {
    	int size = 0;
    	for(Iterator iter = structureItems.iterator(); iter.hasNext();) {
    		StructureItemBinding siBinding = (StructureItemBinding) iter.next();
    		
    		//Do not count redefined storage in the size
    		if (siBinding.getAnnotation(new String[] { "egl", "core" }, "Redefines") == null) {
    			size += siBinding.getLengthInBytes();
    		}
    	}
    	return size;
    }
}
