package negotiator.gui.boaframework;

import java.awt.Frame;
import java.util.ArrayList;

import javax.swing.JTextField;

import negotiator.boaframework.BOAparameter;

public class BOATextField extends JTextField {

	private static final long serialVersionUID = -2878208768469052209L;
	private ArrayList<BOAparameter> boaParameters;

	public BOATextField(final Frame frame) {
		super();
		this.setEditable(false);
		this.setEnabled(false);
	}

	public void setText(ArrayList<BOAparameter> boaParameters) {
		this.boaParameters = boaParameters;
		setText(boaParameters.toString());
	}

	public ArrayList<BOAparameter> getBOAparameters() {
		return boaParameters;
	}
}