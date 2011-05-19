/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.lookup.generate;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.edt.mof.egl.Part;

public class PartCache {

    private static final boolean DEBUG = false;
    
    private static class SoftReferenceWithKey extends SoftReference {
        Object key;
        
        SoftReferenceWithKey(Object key, Object referent, ReferenceQueue q) {
            super(referent, q);
        }
    }

    private static class PartKey {
        String[] packageName;
        String partName;
        
        PartKey(String[] packageName, String partName) {
            this.packageName = packageName;
            this.partName = partName;
        }
        
        public int hashCode() {
            return partName.hashCode();
        }
        
        public boolean equals(Object obj) {
            PartKey anotherPartKey = (PartKey) obj;
            return (anotherPartKey.packageName == this.packageName) && (anotherPartKey.partName == this.partName);
        }
    }

    private Map softReferences;

    private ReferenceQueue referenceQueue;

    public PartCache() {
        this.softReferences = new LinkedHashMap(16, 0.75f, true);
        this.referenceQueue = new ReferenceQueue();
    }

    public PartCache(final int maxSize) {
        this.softReferences = new LinkedHashMap((int)(maxSize * 0.75f), 0.75f, true) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > maxSize;
            }
        };
        this.referenceQueue = new ReferenceQueue();
    }

    /**
     * @param packageName -- INTERNED string array
     * @param partName -- INTERNED string
     */
    public void put(String[] packageName, String partName, Part part) {
        if(DEBUG) System.out.println("Put: " + partName); //$NON-NLS-1$

        removeClearedReferences();
        
        PartKey partKey = new PartKey(packageName, partName);
        
        softReferences.put(partKey, new SoftReferenceWithKey(partKey, part, referenceQueue));
    }
    
    /**
     * @param packageName -- INTERNED string array
     * @param partName -- INTERNED string
     */
    public Part get(String[] packageName, String partName) {
        removeClearedReferences();

        SoftReferenceWithKey ref = (SoftReferenceWithKey) softReferences.get(new PartKey(packageName, partName));

        if(ref == null) {
            return null;
        }

        Object result = ref.get();

        if(DEBUG && result == null) System.out.println("Key " + partName + " has been cleared"); //$NON-NLS-1$ //$NON-NLS-2$

        return (Part) result;
    }
    
    /**
     * @param packageName -- INTERNED string array
     * @param partName -- INTERNED string
     */
    public void remove(String[] packageName, String partName) {
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
