package kkckkc.jsourcepad.util.messagebus;


public interface Topic<L, M> {
	void subscribeWeak(DispatchStrategy dispatchStrategy, L listener);
    void subscribe(DispatchStrategy dispatchStrategy, L listener);
	void post(M message);
	L post();
}