package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

// see http://code.google.com/p/bookjar-utils/source/browse/BookJar-utils/src/util/swing/components/FullScreenFrame.java?r=152&spec=svn194
public class ViewFullscreenAction extends BaseAction {

	private Window w;

	Rectangle applicationPosition = new Rectangle();
    boolean isUndecorated = true;

	public ViewFullscreenAction(Window w) {
		this.w = w;
	}

	boolean isMaximized = false;

	@Override
	public void performAction(ActionEvent e) {
        final JFrame frame = w.getContainer();

        final boolean wasVisible = frame.isVisible();

        frame.setVisible(false);
        frame.dispose();

        if (! isMaximized) {
            // Store old position
            applicationPosition = frame.getBounds();
            isUndecorated = frame.isUndecorated();

            Toolkit toolkit = Toolkit.getDefaultToolkit();

            Insets insets = toolkit.getScreenInsets(frame.getGraphicsConfiguration());

            Rectangle max = new Rectangle(toolkit.getScreenSize());
            max.x += insets.left;
            max.y += insets.top;
            max.width -= (insets.left + insets.right);
            max.height -= (insets.top + insets.bottom);

            frame.setBounds(max);

            isMaximized = true;
            frame.setUndecorated(true);
        } else {
            frame.setBounds(applicationPosition);
            isMaximized = false;
            frame.setUndecorated(isUndecorated);
        }

        frame.setResizable(!isMaximized);
        frame.validate();
        frame.setVisible(wasVisible);

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                frame.repaint();
            }
        });
	}

}
