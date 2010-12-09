package kkckkc.jsourcepad.util.messagebus;


public interface Topic<L, M> {
    Subscription subscribe(DispatchStrategy dispatchStrategy, L listener);
	void post(M message);
	L post();
}