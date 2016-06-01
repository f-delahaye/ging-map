package org.gingolph.tm;

import java.util.function.Consumer;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Topic;


public class ReifierHelper {

  public static void setReifier(Reifiable reified, Topic reifier, Consumer<TopicImpl> setter)
      throws ModelConstraintException {
    if (reifier == null) {
      if (reified.getReifier() != null) {
        ((Reifier) reified.getReifier()).setReified(null);
      }
      setter.accept(null);
    } else {
      if (reified.getTopicMap() != reifier.getTopicMap()) {
        throw new ModelConstraintException(reified, "Different topic maps not allowed");
      }
      if (!(reifier instanceof TopicImpl)) {
        throw new IllegalArgumentException(reifier.getClass() + " not supported");
      }

      TopicImpl reifierImpl = (TopicImpl) reifier;
      if (reifier.getReified() != null && (!reifier.getReified().equals(reified))) {
        throw new ModelConstraintException(reified,
            reifier + " already reifies " + reifier.getReified());
      }
      setter.accept(reifierImpl);
      ((Reifier) reified.getReifier()).setReified(reified);
    }
  }

}
