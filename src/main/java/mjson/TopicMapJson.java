package mjson;

// Dummy class which makes ObjectJson available to classes outside the mjson package
public class TopicMapJson extends Json.ObjectJson {

  private static final long serialVersionUID = 1L;

  public TopicMapJson() {
    super();
  }

  public TopicMapJson(Json parent) {
    super(parent);
  }
  
}
