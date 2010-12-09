package kkckkc.jsourcepad.util.messagebus;

import kkckkc.utils.Pair;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class TopicImpl<T> implements Topic<T, Pair<Method, Object[]>> {
	protected List<Pair<DispatchStrategy, T>> listeners;

	private Class<? extends T> topic;
	private T dispatcher;

	public TopicImpl(Class<? extends T> topic, MessageBus messageBus) {
		this.topic = topic;
	}

    public Subscription subscribe(DispatchStrategy dispatchStrategy, T listener) {
        if (listeners == null) listeners = new CopyOnWriteArrayList<Pair<DispatchStrategy,T>>();

        final Pair<DispatchStrategy, T> pair = new Pair<DispatchStrategy, T>(dispatchStrategy, listener);
        listeners.add(pair);
        return new Subscription() {
            @Override
            public void unsubscribe() {
                listeners.remove(pair);
            }
        };
    }


    @SuppressWarnings("unchecked")
	public T post() {
		if (dispatcher == null) {
			dispatcher = (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { topic }, new InvocationHandler() {
				public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
					post(new Pair(method, args));
					return null;
				}
			});
		}
		return dispatcher;
	}

	@Override
	public void post(final Pair<Method, Object[]> message) {
		if (listeners != null) {
			for (final Pair<DispatchStrategy, T> p : listeners) {
				p.getFirst().execute(new Runnable() {
					public void run() {
						try {
							message.getFirst().invoke(p.getSecond(), message.getSecond());
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				});
			}
		}
	}
}