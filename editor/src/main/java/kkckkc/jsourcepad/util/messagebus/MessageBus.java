package kkckkc.jsourcepad.util.messagebus;

import kkckkc.utils.Pair;

import java.lang.reflect.Method;

public interface MessageBus {

	public abstract <T> Topic<T, Pair<Method, Object[]>> topic(Class<? extends T> topic);

}