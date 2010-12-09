package kkckkc.jsourcepad.util.messagebus;

import com.google.common.collect.Maps;

import java.util.Map;



public abstract class AbstractMessageBus implements MessageBus {
	private Map<Class<?>, Topic<?, ?>> topics = Maps.newHashMap();

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
