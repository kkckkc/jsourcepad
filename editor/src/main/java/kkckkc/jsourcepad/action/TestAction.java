package kkckkc.jsourcepad.action;

import java.awt.EventQueue;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import javax.swing.AbstractAction;

import kkckkc.jsourcepad.Dialog;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.ui.dialog.ProgressDialogViewImpl;
import kkckkc.jsourcepad.util.io.ScriptExecutor;
import kkckkc.jsourcepad.util.io.ScriptExecutor.Callback;
import kkckkc.jsourcepad.util.io.ScriptExecutor.Execution;

public class TestAction extends AbstractAction {

	private Window w;

	public TestAction(Window w) {
		this.w = w;
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
//		
//		
//		JWindow jWindow = new JWindow(w.getJFrame());
//		jWindow.setBackground(Color.pink);
//		jWindow.setSize(400, 300);
//		jWindow.setLocationRelativeTo(null);
//		jWindow.setVisible(true);
//		
//		JTextField jtf = new JTextField("Test");
//		jWindow.getContentPane().add(jtf);
    }

}
