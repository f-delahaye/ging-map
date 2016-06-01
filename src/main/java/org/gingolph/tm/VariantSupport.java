package org.gingolph.tm;

import org.tmapi.core.Topic;


public interface VariantSupport extends ConstructSupport, ScopedSupport, DatatypeAwareSupport {

  Topic getReifier();

  void setReifier(Topic reifier);
}
