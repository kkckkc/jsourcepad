package kkckkc.jsourcepad.theme.osx;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;

public class TigerTreeUI extends BasicTreeUI {

	/** Creates a new instance of TreeUI */
	public TigerTreeUI() {
		super();
	}

	protected void paintHorizontalPartOfLeg(Graphics g, Rectangle clipBounds,
			Insets insets, Rectangle bounds, TreePath path, int param,
			boolean param6, boolean param7, boolean param8) {

		boolean selected = treeSelectionModel.isPathSelected(path);

		if (selected) {
			Graphics2D g2d = (Graphics2D) g;
			
			Color c1 = new Color(146, 163, 179);
			Color c2 = new Color(82, 108, 133);
			
			GradientPaint gradient = new GradientPaint(0,bounds.y,c1,0, bounds.y + bounds.height,c2,true);
		    g2d.setPaint(gradient);
		    g2d.fillRect(0, bounds.y, tree.getWidth(), bounds.height);
		    
		    Color lineColor = new Color(123, 139, 157);
		    g2d.setColor(lineColor);
		    g2d.drawLine(0, bounds.y, tree.getWidth(), bounds.y);
		}
		// Do not call super. We dont want the lines.
	}

	
	
	protected void paintVerticalPartOfLeg(Graphics g, Rectangle clipBounds,
			Insets insets, TreePath path) {
		// Do not call super. We dont want the lines.
	}

	protected java.awt.event.MouseListener createMouseListener() {
		return new FittsMouseListener();
	}

	protected class FittsMouseListener extends MouseAdapter {
		public void mousePressed(java.awt.event.MouseEvent mouseEvent) {
			handleSelection(mouseEvent);
		}

		protected void handleSelection(java.awt.event.MouseEvent evt) {
			if (tree != null && tree.isEnabled()) {
				if (isEditing(tree) && tree.getInvokesStopCellEditing()
						&& !stopEditing(tree)) {
					return;
				}

				if (tree.isRequestFocusEnabled()) {
					tree.requestFocus();
				}
				TreePath path = getClosestPathForLocation(tree, evt.getX(), evt
						.getY());

				if (path != null) {
					Rectangle bounds = getPathBounds(tree, path);
					if (evt.getY() > (bounds.y + bounds.height)) {
						return;
					}
					if (SwingUtilities.isLeftMouseButton(evt))
						checkForClickInExpandControl(path, evt.getX(), evt
								.getY());
					if (!startEditing(path, evt)) {
						selectPathForEvent(path, evt);
					}
				}
			}
		}
	}
}
