package kkckkc.jsourcepad.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.annotation.PostConstruct;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.ui.statusbar.CurrentPosition;
import kkckkc.jsourcepad.ui.statusbar.LanguageView;
import kkckkc.jsourcepad.ui.statusbar.ScopeView;
import kkckkc.jsourcepad.ui.statusbar.TabView;

import org.springframework.beans.factory.annotation.Autowired;

public class WindowViewImpl implements WindowView {

	private Window window;
	private JMenuBar menubar;
	
	@Autowired
	public void setWindow(Window window) {
	    this.window = window;
    }
	
	@PostConstruct
	public void init() {
		JFrame frame = window.getJFrame();
		
		frame.setSize(900, 650);
		
		JSplitPane splitpane = createSplitPane();
		frame.add(splitpane, BorderLayout.CENTER);

		ProjectPresenter tree = window.getPresenter(ProjectPresenter.class);
		
		JScrollPane st = createTreeScrollPane(tree.getJComponent());
		st.setPreferredSize(new Dimension(200, 0));
		splitpane.add(st);
		
		DocListPresenter blv = window.getPresenter(DocListPresenter.class);
		splitpane.add(blv.getJComponent());
		
		menubar = new JMenuBar();
		frame.setJMenuBar(menubar);
		
		JPanel statusBarPanel = createStatusBar();
		
		statusBarPanel.add(processStatusBarView(new ScopeView(window)));
		statusBarPanel.add(delimiter());
		statusBarPanel.add(processStatusBarView(new LanguageView(window)));
		statusBarPanel.add(delimiter());
		statusBarPanel.add(processStatusBarView(new CurrentPosition(window)));
		statusBarPanel.add(delimiter());
		statusBarPanel.add(processStatusBarView(new TabView(window)));
		
		frame.add(statusBarPanel, BorderLayout.SOUTH);
	}

	protected Component processStatusBarView(JComponent view) {
		return view;
	}

	protected JPanel createStatusBar() {
		JPanel statusBarPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 15, 0));
		statusBarPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		return statusBarPanel;
	}

	protected JScrollPane createTreeScrollPane(JComponent tree) {
	    JScrollPane st = new JScrollPane(tree);
	    return st;
    }
	
	public JMenuBar getMenubar() {
	    return menubar;
    }
	
	protected JLabel delimiter() {
	    JLabel jl = new JLabel("");
		jl.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		jl.setPreferredSize(new Dimension(1, 20));
	    return jl;
    }

	protected JSplitPane createSplitPane() {
		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		return sp;
	}	
}
