package org.gingolph.tm;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.tmapi.core.Construct;
import org.tmapi.core.DatatypeAware;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;


public abstract class AbstractDatatypeAware<P extends Construct, S extends DatatypeAwareSupport>
    extends TopicMapItem<P, S> implements DatatypeAware, Valued {

  public AbstractDatatypeAware(TopicMapImpl topicMap, P parent) {
    super(topicMap, parent);
  }

  @Override
  public Locator getDatatype() {
    return support.getDatatype();
  }

  @Override
  public String getValue() {
    return support.getValue();
  }

  @Override
  public void setValue(String value) throws ModelConstraintException {
    setValue(value, LocatorImpl.XSD_STRING);
  }

  @Override
  public void setValue(Locator value) throws ModelConstraintException {
    setValue(LocatorImpl.XSD_ANY_URI, value);
  }

  @Override
  public void setValue(String value, Locator datatype) throws ModelConstraintException {
    setValue(datatype, value);
  }

  public void setValue(Locator datatype, Object value) throws ModelConstraintException {
    if (value == null) {
      throw new ModelConstraintException(this, "Null value not allowed");
    }
    if (datatype == null) {
      throw new ModelConstraintException(this, "Null datatype not allowed");
    }
    support.setValue(value.toString());
    support.setDatatype(datatype);
  }

  @Override
  public void setValue(BigDecimal value) throws ModelConstraintException {
    setValue(LocatorImpl.XSD_DECIMAL, value);
  }

  @Override
  public void setValue(BigInteger value) throws ModelConstraintException {
    setValue(LocatorImpl.XSD_INTEGER, value);
  }

  @Override
  public void setValue(long value) {
    setValue(LocatorImpl.XSD_LONG, value);
  }

  @Override
  public void setValue(float value) {
    setValue(LocatorImpl.XSD_FLOAT, value);
  }

  @Override
  public void setValue(int value) {
    setValue(LocatorImpl.XSD_INT, value);
  }

  @Override
  public int intValue() {
    return decimalValue().intValue();
  }

  @Override
  public BigInteger integerValue() {
    return decimalValue().toBigInteger();
  }

  @Override
  public float floatValue() {
    String value = support.getValue();
    return Float.parseFloat(value);
  }

  @Override
  public BigDecimal decimalValue() {
    String value = support.getValue();
    return new BigDecimal(value);
  }

  @Override
  public long longValue() {
    return decimalValue().longValue();
  }

  @Override
  public Locator locatorValue() {
    String value = support.getValue();
    return new LocatorImpl(value);
  }
}
