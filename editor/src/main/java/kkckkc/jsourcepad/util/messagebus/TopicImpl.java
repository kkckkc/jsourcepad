package kkckkc.jsourcepad.util.messagebus;

import kkckkc.utils.Pair;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class TopicImpl<T> implements Topic<T, Pair<Method, Object[]>> {
	protected List<Pair<DispatchStrategy, Reference<T>>> listeners;
	protected List<Pair<DispatchStrategy, Reference<UntypedListener>>> untypedListeners;
	
	private Class<? extends T> topic;
	private T dispatcher;
	
	private MessageBus messageBus;
	
	public TopicImpl(Class<? extends T> topic, MessageBus messageBus) {
		this.topic = topic;
		this.messageBus = messageBus;
	}

	public void subscribeWeak(DispatchStrategy dispatchStrategy, T listener) {
		if (listeners == null) listeners = new CopyOnWriteArrayList<Pair<DispatchStrategy,Reference<T>>>();
		listeners.add(new Pair<DispatchStrategy, Reference<T>>(dispatchStrategy, new WeakReference(listener)));
		return;
	}

    public void subscribe(DispatchStrategy dispatchStrategy, T listener) {
        if (listeners == null) listeners = new CopyOnWriteArrayList<Pair<DispatchStrategy,Reference<T>>>();
        listeners.add(new Pair<DispatchStrategy, Reference<T>>(dispatchStrategy, new HardReference(listener)));
        return;
    }

    public class HardReference<T> extends WeakReference<T> {
        private final T strongRef;

        public HardReference(T referent) {
            super(null);
            strongRef = referent;
        }

        public T get() {
            return strongRef;
        }
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
			for (final Pair<DispatchStrategy, Reference<T>> p : listeners) {
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
			for (final Pair<DispatchStrategy, Reference<UntypedListener>> p : untypedListeners) {
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