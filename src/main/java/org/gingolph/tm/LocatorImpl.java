package org.gingolph.tm;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Objects;
import org.tmapi.core.Locator;
import org.tmapi.core.MalformedIRIException;
import org.tmapi.core.TMAPIRuntimeException;


public class LocatorImpl implements Locator, Serializable {

  private static final long serialVersionUID = 1L;
  
  public static final String XSD_BASE_URL = "http://www.w3.org/2001/XMLSchema#";
  public static final String TOPIC_MAP_BASE_URL = "http://psi.topicmaps.org/iso13250/model/";

  public static final Locator DEFAULT_NAME_TYPE = new LocatorImpl(TOPIC_MAP_BASE_URL + "topic-name");

  public static final Locator XSD_STRING = new LocatorImpl(XSD_BASE_URL + "string");
  public static Locator XSD_ANY_URI = new LocatorImpl(XSD_BASE_URL + "anyURI");
  public static Locator XSD_DECIMAL = new LocatorImpl(XSD_BASE_URL + "decimal");
  public static Locator XSD_INTEGER = new LocatorImpl(XSD_BASE_URL + "integer");
  public static Locator XSD_FLOAT = new LocatorImpl(XSD_BASE_URL + "float");
  public static Locator XSD_LONG = new LocatorImpl(XSD_BASE_URL + "long");
  public static Locator XSD_INT = new LocatorImpl(XSD_BASE_URL + "int");

  private String reference;
  private URI uri;

  public LocatorImpl() {}

  public LocatorImpl(String reference) {
    if (reference.length() == 0 || reference.charAt(0) == '#') {
      throw new MalformedIRIException("Illegal absolute IRI: '" + reference + "'");
    }

    try {
      setReference(URLDecoder.decode(reference, "utf-8"));
    } catch (UnsupportedEncodingException ex) {
      throw new TMAPIRuntimeException(ex);
    }
    setUri(URI.create(this.reference.replace(" ", "%20")));

  }

  private LocatorImpl(URI uri) {
    try {
      setReference(URLDecoder.decode(uri.toString(), "utf-8"));
    } catch (UnsupportedEncodingException ex) {
      throw new TMAPIRuntimeException(ex);
    }
    setUri(uri);
  }

  @Override
  public String getReference() {
    return reference;
  }

  public final void setReference(final String reference) {
    this.reference = reference;
  }

  protected URI getUri() {
    return uri;
  }

  protected final void setUri(URI uri) {
    this.uri = uri;
  }

  @Override
  public String toExternalForm() {
    return uri.toASCIIString();
  }

  @Override
  public Locator resolve(String reference) throws MalformedIRIException {
    if (reference.length() == 0) {
      return this;
    }
    return new LocatorImpl(uri.resolve(reference.replace(" ", "%20")));
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof Locator && ((Locator) other).getReference().equals(getReference());
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 29 * hash + Objects.hashCode(this.reference);
    return hash;
  }

  @Override
  public String toString() {
    return reference;
  }
}
