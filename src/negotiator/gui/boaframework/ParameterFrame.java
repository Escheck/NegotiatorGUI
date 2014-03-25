package negotiator.gui.boaframework;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import negotiator.boaframework.BOAparameter;

public class ParameterFrame extends JDialog {

	private static final long serialVersionUID = -8510562424350715921L;
	private int higherBorder = 10;
	private int lowerBorder = 20;
	private int elementHeight = 50;
	private int nameWidth = 100;
	private int descriptionWidth = 400;
	private int lowerBoundWidth = 100;
	private int stepSizeWidth = 100;
	private int higherBoundWidth = 100;
	private int leftSideBorder = 15;
	private int spacing = 50;
	private int labelHeight = 30;
	private int buttonHeight = 30;
	private int buttonWidth = 80;
	private JTextField[] descriptions;
	private JTextField[] lowerbounds;
	private JTextField[] stepsizes;
	private JTextField[] upperbounds;

	private ArrayList<BOAparameter> result;

	public ParameterFrame(Frame frame) {
		super(frame, "Edit parameters", true);
		this.setLocation(frame.getLocation().x + frame.getWidth() / 4,
				frame.getLocation().y + frame.getHeight() / 4);
	}

	public Set<BOAparameter> getResult(ArrayList<BOAparameter> input) {
		this.result = input;
		generateFrame();
		generateLabels();
		generateInput();
		generateButtons();
		pack();
		setVisible(true);
		return new HashSet<BOAparameter>(result);
	}

	private void generateButtons() {
		JButton okButton = new JButton("Ok");
		okButton.setFont(new java.awt.Font("Tahoma", 1, 13));
		getContentPane().add(
				okButton,
				new org.netbeans.lib.awtextra.AbsoluteConstraints(
						leftSideBorder, (result.size() * elementHeight)
								+ lowerBorder, buttonWidth, buttonHeight));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<BOAparameter> temp = prepareResults();
				if (temp != null && temp.size() > 0) {
					result = temp;
					dispose();
				}
			}
		});

		JButton cancelButton = new JButton("Cancel");
		getContentPane().add(
				cancelButton,
				new org.netbeans.lib.awtextra.AbsoluteConstraints(
						leftSideBorder + buttonWidth + 20,
						(result.size() * elementHeight) + lowerBorder,
						buttonWidth, buttonHeight));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}

	private ArrayList<BOAparameter> prepareResults() {
		ArrayList<BOAparameter> parameters = new ArrayList<BOAparameter>();
		for (int i = 0; i < result.size(); i++) {
			try {
				BigDecimal lowerBound = new BigDecimal(lowerbounds[i].getText());

				if (!stepsizes[i].getText().equals("")
						&& !upperbounds[i].getText().equals("")) {
					BigDecimal stepSize = new BigDecimal(stepsizes[i].getText());
					BigDecimal upperBound = new BigDecimal(
							upperbounds[i].getText());

					if (lowerBound.compareTo(upperBound) > 0) {
						JOptionPane
								.showMessageDialog(
										null,
										"Each upper bound must be higher or equal to the lower bound.",
										"Parameter error", 0);
						break;
					}

					if (stepSize.compareTo(BigDecimal.ZERO) <= 0) {
						JOptionPane.showMessageDialog(null,
								"Each step size must be positive.",
								"Parameter error", 0);
						break;
					}
					parameters.add(new BOAparameter(result.get(i).getName(),
							lowerBound, upperBound, stepSize, descriptions[i]
									.getText()));
				} else {
					parameters.add(new BOAparameter(result.get(i).getName(),
							lowerBound, lowerBound, BigDecimal.ONE,
							descriptions[i].getText()));
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,
						"All values should be numeric.", "Parameter error", 0);
				break;
			}
		}
		return parameters;
	}

	private void generateLabels() {
		JLabel description = new JLabel("Description");
		description.setFont(new java.awt.Font("Tahoma", 1, 13));
		getContentPane().add(
				description,
				new org.netbeans.lib.awtextra.AbsoluteConstraints(
						leftSideBorder + nameWidth, higherBorder,
						descriptionWidth, labelHeight));

		JLabel lowerBound = new JLabel("Lower bound");
		lowerBound.setFont(new java.awt.Font("Tahoma", 1, 13));
		getContentPane().add(
				lowerBound,
				new org.netbeans.lib.awtextra.AbsoluteConstraints(
						leftSideBorder + nameWidth + descriptionWidth,
						higherBorder, lowerBoundWidth, labelHeight));

		JLabel stepSize = new JLabel("Step size");
		stepSize.setFont(new java.awt.Font("Tahoma", 1, 13));
		getContentPane().add(
				stepSize,
				new org.netbeans.lib.awtextra.AbsoluteConstraints(
						leftSideBorder + nameWidth + descriptionWidth
								+ lowerBoundWidth, higherBorder, stepSizeWidth,
						labelHeight));

		JLabel upperBound = new JLabel("Upper bound");
		upperBound.setFont(new java.awt.Font("Tahoma", 1, 13));
		getContentPane().add(
				upperBound,
				new org.netbeans.lib.awtextra.AbsoluteConstraints(
						leftSideBorder + nameWidth + descriptionWidth
								+ lowerBoundWidth + stepSizeWidth,
						higherBorder, higherBoundWidth, labelHeight));
	}

	private void generateInput() {
		descriptions = new JTextField[result.size()];
		lowerbounds = new JTextField[result.size()];
		stepsizes = new JTextField[result.size()];
		upperbounds = new JTextField[result.size()];

		for (int i = 0; i < result.size(); i++) {
			JLabel label = new JLabel(result.get(i).getName());
			label.setFont(new java.awt.Font("Tahoma", 1, 13));
			getContentPane().add(
					label,
					new org.netbeans.lib.awtextra.AbsoluteConstraints(
							leftSideBorder, labelHeight + higherBorder + i
									* elementHeight, nameWidth - spacing, -1));

			JTextField descriptionTF = new JTextField(result.get(i)
					.getDescription() + "");
			descriptionTF.setFont(new java.awt.Font("Tahoma", 1, 13));
			descriptionTF.setEditable(false);
			getContentPane().add(
					descriptionTF,
					new org.netbeans.lib.awtextra.AbsoluteConstraints(
							leftSideBorder + nameWidth, labelHeight
									+ higherBorder + i * elementHeight,
							descriptionWidth - spacing, -1));
			descriptions[i] = descriptionTF;

			JTextField lowerBoundTF = new JTextField(result.get(i).getLow()
					+ "");
			lowerBoundTF.setFont(new java.awt.Font("Tahoma", 1, 13));
			getContentPane().add(
					lowerBoundTF,
					new org.netbeans.lib.awtextra.AbsoluteConstraints(
							leftSideBorder + nameWidth + descriptionWidth,
							labelHeight + higherBorder + i * elementHeight,
							lowerBoundWidth - spacing, -1));
			lowerbounds[i] = lowerBoundTF;

			JTextField stepSizeTF = new JTextField();
			if (!result.get(i).getLow().equals(result.get(i).getHigh())) {
				stepSizeTF.setText(result.get(i).getStep() + "");
			}
			stepSizeTF.setFont(new java.awt.Font("Tahoma", 1, 13));
			getContentPane().add(
					stepSizeTF,
					new org.netbeans.lib.awtextra.AbsoluteConstraints(
							leftSideBorder + nameWidth + descriptionWidth
									+ lowerBoundWidth, labelHeight
									+ higherBorder + i * elementHeight,
							stepSizeWidth - spacing, -1));
			stepsizes[i] = stepSizeTF;

			JTextField upperBoundTF = new JTextField();
			if (!result.get(i).getLow().equals(result.get(i).getHigh())) {
				upperBoundTF.setText(result.get(i).getHigh() + "");
			}
			upperBoundTF.setFont(new java.awt.Font("Tahoma", 1, 13));
			getContentPane().add(
					upperBoundTF,
					new org.netbeans.lib.awtextra.AbsoluteConstraints(
							leftSideBorder + nameWidth + descriptionWidth
									+ lowerBoundWidth + stepSizeWidth,
							labelHeight + higherBorder + i * elementHeight,
							higherBoundWidth - spacing, -1));
			upperbounds[i] = upperBoundTF;
		}
	}

	private void generateFrame() {
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		int height = (result.size() * elementHeight) + higherBorder
				+ lowerBorder + labelHeight + buttonHeight;
		int width = leftSideBorder + nameWidth + descriptionWidth
				+ lowerBoundWidth + stepSizeWidth + higherBoundWidth
				+ leftSideBorder;
		setMaximumSize(new java.awt.Dimension(width, height));
		setMinimumSize(new java.awt.Dimension(width, height));
		setPreferredSize(new java.awt.Dimension(width, height));
		setResizable(false);
		getContentPane().setLayout(
				new org.netbeans.lib.awtextra.AbsoluteLayout());
	}
}