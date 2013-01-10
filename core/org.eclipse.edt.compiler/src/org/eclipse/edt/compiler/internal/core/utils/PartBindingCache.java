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
package org.eclipse.edt.compiler.internal.core.utils;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.mof.utils.NameUtile;


public class PartBindingCache {

    private static final boolean DEBUG = false;
    
    private static class SoftReferenceWithKey extends SoftReference {
        Object key;
        
        SoftReferenceWithKey(Object key, Object referent, ReferenceQueue q) {
            super(referent, q);
        }
    }

    private static class PartKey {
        String packageName;
        String partName;
        
        PartKey(String packageName, String partName) {
            this.packageName = packageName;
            this.partName = partName;
        }
        
        public int hashCode() {
            return partName.hashCode();
        }
        
        public boolean equals(Object obj) {
            PartKey anotherPartKey = (PartKey) obj;
            return (NameUtile.equals(anotherPartKey.packageName, this.packageName)) && (NameUtile.equals(anotherPartKey.partName, this.partName));
        }
    }

    private Map softReferences;

    private ReferenceQueue referenceQueue;

    public PartBindingCache() {
        this.softReferences = new LinkedHashMap(16, 0.75f, true);
        this.referenceQueue = new ReferenceQueue();
    }

    public PartBindingCache(final int maxSize) {
        this.softReferences = new LinkedHashMap((int)(maxSize * 0.75f), 0.75f, true) {
			private static final long serialVersionUID = 343884753499895404L;

			protected boolean removeEldestEntry(Entry eldest) {
                return size() > maxSize;
            }
        };
        this.referenceQueue = new ReferenceQueue();
    }

    /**
     * @param packageName 
     * @param partName
     */
    public void put(String packageName, String partName, IPartBinding partBinding) {
        if(DEBUG) System.out.println("Put: " + partName); //$NON-NLS-1$

        removeClearedReferences();
        
        PartKey partKey = new PartKey(packageName, partName);
        
        softReferences.put(partKey, new SoftReferenceWithKey(partKey, partBinding, referenceQueue));
    }
    
    /**
     * @param packageName 
     * @param partName 
     */
    public IPartBinding get(String packageName, String partName) {
        removeClearedReferences();

        SoftReferenceWithKey ref = (SoftReferenceWithKey) softReferences.get(new PartKey(packageName, partName));

        if(ref == null) {
            return null;
        }

        Object result = ref.get();

        if(DEBUG && result == null) System.out.println("Key " + partName + " has been cleared"); //$NON-NLS-1$ //$NON-NLS-2$

        return (IPartBinding) result;
    }
    
    /**
     * @param packageName 
     * @param partName 
     */
    public void remove(String packageName, String partName) {
        softReferences.remove(new PartKey(packageName, partName));
    }

    private void removeClearedReferences() {
        SoftReferenceWithKey ref;
        while((ref = (SoftReferenceWithKey) referenceQueue.poll()) != null) {
            if(DEBUG) System.out.println("Removing cleared soft reference"); //$NON-NLS-1$
            softReferences.remove(ref.key);
        }
    }
}
