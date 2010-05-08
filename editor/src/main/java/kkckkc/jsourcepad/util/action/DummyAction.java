package kkckkc.jsourcepad.util.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class DummyAction extends AbstractAction {

	private String name;

	public DummyAction(String name) {
		super(name);
		this.name = name;
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
	    System.out.println("Performed: " + name);
    }
	
	public String toString() {
		return "DummyAction: " + name;
	}

}
