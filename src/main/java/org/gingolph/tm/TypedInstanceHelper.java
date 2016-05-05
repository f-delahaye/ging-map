package org.gingolph.tm;

import java.util.function.Consumer;
import org.tmapi.core.Construct;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;
import org.tmapi.core.Typed;


public class TypedInstanceHelper {
    public static <T extends Construct & TypedConstruct> void setType(T typed, Topic type, Consumer<Topic> setter) {
        if (type == null) {
            throw new ModelConstraintException(typed, "Null type not allowed");
        }
        if (typed.getTopicMap() != type.getTopicMap()) {
            throw new ModelConstraintException(typed, "Different topicmaps not allowed");
        }
        Topic oldType = typed instanceof Typed?((Typed)typed).getType():null;
        setter.accept(type);
        ((TopicMapImpl)typed.getTopicMap()).notifyListeners(listener->listener.onTypeChanged(typed, type, oldType));        
    }
}
