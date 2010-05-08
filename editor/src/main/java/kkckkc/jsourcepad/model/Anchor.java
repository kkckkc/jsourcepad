package kkckkc.jsourcepad.model;

public class Anchor {
	public static enum Bias { LEFT, RIGHT }

	private int position;
	private Bias bias;
	
	public Anchor(int position, Bias bias) {
		this.position = position;
		this.bias = bias;
	}

	public int getPosition() {
    	return position;
    }

	public Bias getBias() {
    	return bias;
    }

	public void move(int delta) {
	    this.position += delta;
    }
	
	public String toString() {
		return "Anchor <" + position + ", " + bias + ">";
	}
}
