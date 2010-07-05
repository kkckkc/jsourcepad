package kkckkc.jsourcepad.theme;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import kkckkc.jsourcepad.ui.WindowViewImpl;

public class OsxWindowViewImpl extends WindowViewImpl {

	@Override
	protected JScrollPane createTreeScrollPane(JComponent tree) {
		JScrollPane jsp = super.createTreeScrollPane(tree);
		jsp.setBorder(BorderFactory.createEmptyBorder());
		return jsp;
	}

	@Override
	protected JSplitPane createSplitPane() {
		JSplitPane jsp = super.createSplitPane();
		jsp.setBorder(BorderFactory.createEmptyBorder());
		jsp.setDividerSize(1);
		jsp.setBackground(Color.BLACK);
		return jsp;
	}

	@Override
	protected JPanel createStatusBar() {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.TRAILING, 15, 0)) {
			protected void paintComponent( Graphics g ) {
				Graphics2D g2d = (Graphics2D)g;
				
				int w = getWidth( );
				int h = getHeight( );
				 
				Color color1 = new Color(187, 187, 187);
				Color color2 = new Color(154, 154, 154);
				
				// Paint a gradient from top to bottom
				GradientPaint gp = new GradientPaint(
				    0, 0, color1,
				    0, h, color2 );

				g2d.setPaint( gp );
				g2d.fillRect( 0, 0, w, h );
				
				g2d.setColor(new Color(85, 85, 85));
				g2d.drawLine(0, 0, w, 0);

				g2d.setColor(new Color(227, 227, 227));
				g2d.drawLine(0, 1, w, 1);
			}
		};
		p.setBackground(new Color(187, 187, 187));
		p.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
		return p;
	}

	@Override
	protected Component processStatusBarView(JComponent view) {
		Component c = super.processStatusBarView(view);
		c.setFont(c.getFont().deriveFont(11f));
		return c;
	}
	
	
}
