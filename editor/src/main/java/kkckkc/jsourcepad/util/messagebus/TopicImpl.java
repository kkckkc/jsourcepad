package kkckkc.jsourcepad.util.messagebus;

import kkckkc.utils.Pair;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class TopicImpl<T> implements Topic<T, Pair<Method, Object[]>> {
	protected List<Pair<DispatchStrategy, WeakReference<T>>> listeners;
	protected List<Pair<DispatchStrategy, WeakReference<UntypedListener>>> untypedListeners;
	
	private Class<? extends T> topic;
	private T dispatcher;
	
	private MessageBus messageBus;
	
	public TopicImpl(Class<? extends T> topic, MessageBus messageBus) {
		this.topic = topic;
		this.messageBus = messageBus;
	}

	public void subscribe(DispatchStrategy dispatchStrategy, T listener) {
		if (listeners == null) listeners = new CopyOnWriteArrayList<Pair<DispatchStrategy,WeakReference<T>>>();
		listeners.add(new Pair<DispatchStrategy, WeakReference<T>>(dispatchStrategy, new WeakReference(listener)));
		return;
	}

	public void subscribeUntyped(DispatchStrategy strategy,
			UntypedListener untypedListener) {
		if (untypedListeners == null) untypedListeners = new CopyOnWriteArrayList<Pair<DispatchStrategy,WeakReference<UntypedListener>>>();
		untypedListeners.add(new Pair<DispatchStrategy, WeakReference<UntypedListener>>(strategy, new WeakReference(untypedListener)));
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
			for (final Pair<DispatchStrategy, WeakReference<T>> p : listeners) {
                if (p.getSecond().get() == null) continue;
				p.getFirst().execute(new Runnable() {
					public void run() {
						try {
							message.getFirst().invoke(p.getSecond().get(), message.getSecond());
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				});
			}
		}

		if (untypedListeners != null) {
			for (final Pair<DispatchStrategy, WeakReference<UntypedListener>> p : untypedListeners) {
                if (p.getSecond().get() == null) return;
				p.getFirst().execute(new Runnable() {
					public void run() {
						try {
							p.getSecond().get().onEvent(message);
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