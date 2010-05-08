package kkckkc.jsourcepad;

import java.awt.Component;

public interface ComponentView<T extends Component> extends View {
	public T getComponent();
}
