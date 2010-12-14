package kkckkc.jsourcepad;

import java.awt.*;

public interface ComponentView<T extends Component> extends View {
	public T getComponent();
}
