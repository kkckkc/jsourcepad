package kkckkc.jsourcepad.util.messagebus;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.google.common.collect.Maps;



public abstract class AbstractMessageBus implements MessageBus {
	private Map<Class<?>, Topic<?, ?>> topics = Maps.newHashMap();
	
	private Set<MessageBus> children = Collections.newSetFromMap(new WeakHashMap<MessageBus, Boolean>());
	
	@Override
	public Set<MessageBus> getChildren() {
	    return children;
	}
	
	@SuppressWarnings("unchecked")
    public synchronized <T> TopicImpl<T> topic(Class<? extends T> topic) {
		Topic<?, ?> t = topics.get(topic);
		if (t == null) {
			t = new TopicImpl<T>(topic, this);
			topics.put(topic, t);
		}
		return (TopicImpl<T>) t;
	}
}
