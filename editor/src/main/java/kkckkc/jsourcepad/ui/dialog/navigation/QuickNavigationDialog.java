package kkckkc.jsourcepad.ui.dialog.navigation;

import java.awt.Dialog.ModalityType;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.annotation.PostConstruct;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import kkckkc.jsourcepad.Dialog;
import kkckkc.jsourcepad.model.Window;

import org.springframework.beans.factory.annotation.Autowired;

public class QuickNavigationDialog implements Dialog<QuickNavigationDialogView>, 
	KeyListener, MouseListener {

	private Window window;
	private QuickNavigationDialogView view;

	@Autowired
	public void setWindow(Window window) {
	    this.window = window;
    }
	
	@Autowired
    public void setView(QuickNavigationDialogView view) {
	    this.view = view;
    }
	
	@PostConstruct
	public void init() {
		view.getJDialog().setModalityType(ModalityType.DOCUMENT_MODAL);
		
		view.getResult().setCellRenderer(new DefaultListCellRenderer() {
			public void setText(String text) {
				super.setText(new File(text).getName());
			}
		});

		view.getResult().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				File obj = (File) view.getResult().getSelectedValue();
				if (obj == null) {
					view.getPath().setText("Path: ");
				} else {
					view.getPath().setText("Path: " + 
							window.getProject().getProjectRelativePath(obj.getParent()));
				}
			}
		});
		
		view.getTextField().addKeyListener(this);
		view.getResult().addKeyListener(this);
		view.getResult().addMouseListener(this);
	}
	
	public void show() {
		view.getTextField().requestFocusInWindow();
		view.getTextField().selectAll();

		view.getJDialog().setVisible(true);
	}

	@Override
    public void close() {
	    view.getJDialog().dispose();
    }

	@Override
    public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			close();
			return;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			openSelectedFile();
			return;
		}

		if (e.getSource() instanceof JTextField) {
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				view.getResult().setSelectedIndex(0);
				view.getResult().requestFocusInWindow();
			}
		}
    }

	@Override
    public void keyReleased(KeyEvent e) {
		if (e.getSource() instanceof JTextField) {
			if (view.getTextField().getText().length() > 0) {
				view.getResult().setListData(
						window.getProject().findFile(view.getTextField().getText()).toArray());
				
				view.getResult().setSelectedIndex(0);
			} else {
				view.getResult().setListData(new Object[] {});
			}
		}
    }

	@Override
    public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() > 1) {
			openSelectedFile();
			return;
		}
    }

	private void openSelectedFile() {
	    if (view.getResult().getSelectedValue() == null) return;
	    
	    window.getDocList().open((File) view.getResult().getSelectedValue());
	    close();
    }

	@Override
    public void keyTyped(KeyEvent e) {
    }

	@Override
    public void mouseEntered(MouseEvent e) {
    }

	@Override
    public void mouseExited(MouseEvent e) {
    }

	@Override
    public void mousePressed(MouseEvent e) {
    }

	@Override
    public void mouseReleased(MouseEvent e) {
    }
	
}
