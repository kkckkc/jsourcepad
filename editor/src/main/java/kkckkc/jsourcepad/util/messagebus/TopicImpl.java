package kkckkc.jsourcepad.util.messagebus;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import kkckkc.syntaxpane.util.Pair;


public class TopicImpl<T> implements Topic<T, Pair<Method, Object[]>> {
	protected List<Pair<DispatchStrategy, T>> listeners;
	protected List<Pair<DispatchStrategy, UntypedListener>> untypedListeners;
	
	private Class<? extends T> topic;
	private T dispatcher;
	
	private MessageBus messageBus;
	
	public TopicImpl(Class<? extends T> topic, MessageBus messageBus) {
		this.topic = topic;
		this.messageBus = messageBus;
	}

	public void subscribe(DispatchStrategy dispatchStrategy, T listener) {
		if (listeners == null) listeners = new CopyOnWriteArrayList<Pair<DispatchStrategy,T>>();
		listeners.add(new Pair<DispatchStrategy, T>(dispatchStrategy, listener));
		return;
	}

	public void subscribeUntyped(DispatchStrategy strategy,
			UntypedListener untypedListener) {
		if (untypedListeners == null) untypedListeners = new CopyOnWriteArrayList<Pair<DispatchStrategy,UntypedListener>>();
		untypedListeners.add(new Pair<DispatchStrategy, UntypedListener>(strategy, untypedListener));
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

		if (untypedListeners != null) {
			for (final Pair<DispatchStrategy, UntypedListener> p : untypedListeners) {
				p.getFirst().execute(new Runnable() {
					public void run() {
						try {
							p.getSecond().onEvent(message);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				});
			}
		}
		
		// Post to all children as well
		for (MessageBus mb : messageBus.getChildren()) {
			mb.topic(topic).post(message);
		}
		
	}

}