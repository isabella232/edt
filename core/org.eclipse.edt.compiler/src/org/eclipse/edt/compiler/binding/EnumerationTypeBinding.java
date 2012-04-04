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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Harmon
 */
public class EnumerationTypeBinding extends PartBinding {
    
	private List enumerations = Collections.EMPTY_LIST;
	private transient Comparator commaListComparator;
	
	private transient EnumerationTypeDataBinding staticEnumerationTypeDataBinding;


    /**
     * @param simpleName
     * @param packageName
     */
    public EnumerationTypeBinding(String[] packageName, String caseSensitiveInternedName) {
        super(packageName, caseSensitiveInternedName);
    }
    
    protected EnumerationTypeBinding(EnumerationTypeBinding old) {
        super(old);
        if (old.enumerations == Collections.EMPTY_LIST) {
        	old.enumerations = new ArrayList();
        }
        this.enumerations = old.enumerations;
        this.commaListComparator = old.commaListComparator;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.binding.ITypeBinding#getKind()
     */
    public int getKind() {
        return ENUMERATION_BINDING;
    }
    
	public IDataBinding getStaticEnumerationTypeDataBinding() {
		if(staticEnumerationTypeDataBinding == null) {
		    staticEnumerationTypeDataBinding = new EnumerationTypeDataBinding(getCaseSensitiveName(), this, this);
		}
		return staticEnumerationTypeDataBinding;
	}

    public void addEnumeration(EnumerationDataBinding enumeration) {
        if (enumerations == Collections.EMPTY_LIST) {
            enumerations = new ArrayList();
        }
        enumerations.add(enumeration);
    }
    
    protected IDataBinding primFindData(String simpleName) {
        for(Iterator iter = enumerations.iterator(); iter.hasNext();) {
            IDataBinding binding = (IDataBinding) iter.next();
            if(binding.getName() == simpleName) {
                return binding;
            }
        }
        return IBinding.NOT_FOUND_BINDING;
     }
    
    public List getEnumerations() {
        return enumerations;
    }

    public void clear() {
    	super.clear();
    	enumerations = Collections.EMPTY_LIST;
    	staticEnumerationTypeDataBinding = null;
    }

    public boolean isStructurallyEqual(IPartBinding anotherPartBinding) {
        return false;
    }
    
    public boolean isSystemEnumerationType() {
        return false;
    }
    
    public String getEnumerationsAsCommaList() {
    	StringBuffer sb = new StringBuffer();
    	Set enumerationNames = new TreeSet(getCommaListComparator()); 
    	for(Iterator iter = enumerations.iterator(); iter.hasNext();) {
    		enumerationNames.add(((EnumerationDataBinding) iter.next()).getCaseSensitiveName());
    	}
    	
    	for(Iterator iter = enumerationNames.iterator(); iter.hasNext();) {
    		sb.append((String) iter.next());
    		if(iter.hasNext()) {
    			sb.append(", ");
    		}
    	}
    	return sb.toString();
    }
    
    private Comparator getCommaListComparator() {
    	if(commaListComparator == null) {
    		commaListComparator = new Comparator() {
    			public int compare(Object o1, Object o2) {
    				return ((String) o1).compareTo((String)o2);
    			}
        	};
    	}
    	return commaListComparator;
    }
    
    public boolean isDeclarablePart() {
		return false;
	}
    
    public void setCommaListComparator(Comparator c) {
    	commaListComparator = c;
    }

	public StaticPartDataBinding getStaticPartDataBinding() {
		return (StaticPartDataBinding)getStaticEnumerationTypeDataBinding();
	}
	
	@Override
	public ITypeBinding primGetNullableInstance() {
		EnumerationTypeBinding nullable = new EnumerationTypeBinding(this);
		nullable.setNullable(true);
		return nullable;
	}
	
	@Override
	public boolean isInstantiable() {
		return false;
	}
	
	@Override
	public boolean isReference() {
		return true;
	}

}
