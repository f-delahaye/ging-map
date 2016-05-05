package org.gingolph.tm;

import org.tmapi.core.Locator;



public interface DatatypeAwareSupport extends ConstructSupport {
    public String getValue();
    
    public void setValue(String value);
    
    public Locator getDatatype();
    
    public void setDatatype(Locator locator);
}
