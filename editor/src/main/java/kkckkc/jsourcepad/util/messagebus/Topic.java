package kkckkc.jsourcepad.util.messagebus;


public interface Topic<L, M> {
	void subscribe(DispatchStrategy dispatchStrategy, L listener);
	void subscribeUntyped(DispatchStrategy strategy, UntypedListener untypedListener);
	void post(M message);
	L post();
}