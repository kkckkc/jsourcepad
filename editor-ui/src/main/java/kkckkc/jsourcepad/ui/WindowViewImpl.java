package kkckkc.jsourcepad.ui;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.settings.WindowSettings;
import kkckkc.jsourcepad.ui.statusbar.*;
import kkckkc.jsourcepad.util.Null;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;

public class WindowViewImpl implements WindowView {

	private Window window;
	private JMenuBar menubar;
	private JFrame frame;
    private ProjectPresenter tree;
    private DocListPresenter blv;

    private boolean showProjectDrawer;
    private JSplitPane splitpane;

    @Autowired
	public void setWindow(Window window) {
	    this.window = window;
    }
	
	@PostConstruct
	public void init() {
		frame.setSize(900, 650);

        splitpane = createSplitPane();

		tree = window.getPresenter(ProjectPresenter.class);
        blv = window.getPresenter(DocListPresenter.class);

        WindowSettings ws = Application.get().getSettingsManager().get(WindowSettings.class);
        showProjectDrawer = ws.isShowProjectDrawer() && Null.Utils.isNotNull(window.getProject());

        if (showProjectDrawer) {
            frame.add(splitpane, BorderLayout.CENTER);

		    JScrollPane st = createTreeScrollPane(tree.getJComponent());
		    st.setPreferredSize(new Dimension(200, 0));
		    splitpane.add(st);
    		splitpane.add(blv.getJComponent());
        } else {
            frame.add(blv.getJComponent(), BorderLayout.CENTER);
        }
		
		menubar = new JMenuBar();
		frame.setJMenuBar(menubar);
		
		JPanel statusBarPanel = createStatusBar();

        statusBarPanel.add(processStatusBarView(new LanguageView(window)));
		statusBarPanel.add(delimiter());
		statusBarPanel.add(processStatusBarView(new CurrentPosition(window)));
		statusBarPanel.add(delimiter());
		statusBarPanel.add(processStatusBarView(new TabView(window)));
		statusBarPanel.add(delimiter());
		statusBarPanel.add(processStatusBarView(new SymbolView(window)));
        statusBarPanel.add(delimiter());
        statusBarPanel.add(processStatusBarView(new MacroRecordingView(window)));

		frame.add(statusBarPanel, BorderLayout.SOUTH);

        FixedGlassPane glass = new FixedGlassPane(frame);
        glass.setBorder(BorderFactory.createEmptyBorder());
        glass.setOpaque(false);
        frame.setGlassPane(glass);
	}

    class FixedGlassPane extends JPanel implements AWTEventListener {
        private java.awt.Window window;

        public FixedGlassPane(java.awt.Window window) {
            this.window = window;
            addMouseListener(new MouseAdapter() { });
            addKeyListener(new KeyAdapter() { });
        }

        public void eventDispatched(AWTEvent event) {
            Object source = event.getSource();

            // discard the event if its source is not from the correct type
            boolean sourceIsComponent = (event.getSource() instanceof Component);

            if ((event instanceof KeyEvent) && sourceIsComponent) {
                // If the event originated from the window w/glass pane, consume the event
                if ((SwingUtilities.windowForComponent((Component) source) == window)) {
                    ((KeyEvent) event).consume();
                }
            }
        }

        @Override
        public void paint(Graphics g) {
            if (getBackground().getAlpha() == 0f) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(getBackground());
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        public void setVisible(boolean value) {
            if (value) {
                // Sets the mouse cursor to hourglass mode
                getTopLevelAncestor().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                // Start receiving all events and consume them if necessary
                Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);

                this.requestFocus();

                // Activate the glass pane capabilities
                super.setVisible(value);
            } else {
                // Stop receiving all events
                Toolkit.getDefaultToolkit().removeAWTEventListener(this);

                // Deactivate the glass pane capabilities
                super.setVisible(value);

                // Sets the mouse cursor back to the regular pointer
                if (getTopLevelAncestor() != null) {
                    getTopLevelAncestor().setCursor(null);
                }
            }
        }
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
	    return new JScrollPane(tree);
    }
	
	public JMenuBar getMenubar() {
	    return menubar;
    }

    @Override
    @Autowired
    public void setJFrame(JFrame frame) {
        this.frame = frame;
    }

    @Override
    public void setShowProjectDrawer(boolean showProjectDrawer) {
        if (this.showProjectDrawer == showProjectDrawer) return;
        
        this.showProjectDrawer = showProjectDrawer;
                
        if (showProjectDrawer) {
            frame.remove(blv.getJComponent());

            splitpane = createSplitPane();
            frame.add(splitpane, BorderLayout.CENTER);

		    JScrollPane st = createTreeScrollPane(tree.getJComponent());
		    st.setPreferredSize(new Dimension(200, 0));
            splitpane.add(st);
    		splitpane.add(blv.getJComponent());

            frame.validate();
            frame.repaint();
        } else {
            frame.remove(splitpane);
            splitpane.removeAll();

            frame.add(blv.getJComponent(), BorderLayout.CENTER);

            frame.validate();
            frame.repaint();
        }
    }

    protected JLabel delimiter() {
	    JLabel jl = new JLabel("");
		jl.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		jl.setPreferredSize(new Dimension(1, 20));
		jl.setMaximumSize(new Dimension(1, 200));
	    return jl;
    }

	protected JSplitPane createSplitPane() {
		return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	}
}
