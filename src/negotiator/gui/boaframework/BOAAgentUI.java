package negotiator.gui.boaframework;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import misc.Pair;
import misc.SetTools;
import negotiator.boaframework.BOAagentInfo;
import negotiator.boaframework.BOAcomponent;
import negotiator.boaframework.BOAparameter;
import negotiator.boaframework.ComponentsEnum;
import negotiator.boaframework.repository.BOAagentRepository;
import negotiator.boaframework.repository.BOArepItem;
import negotiator.gui.ExtendedComboBoxModel;
import negotiator.gui.NegoGUIApp;

import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

/**
 * 
 * Show a UI for modifying the BOA parameters of a {@link BOAagentInfo} object
 * 
 * @author W.Pasman original code refactored from BOAagentsFrame
 * 
 */
public class BOAAgentUI extends JDialog {

	// BIDDING STRATEGY
	private JLabel biddingStrategyLabel;
	private JComboBox biddingStrategyCB;
	private BOATextField biddingStrategyTF;
	private ExtendedComboBoxModel biddingStrategyModel;

	// OPPONENT MODEL
	private JLabel opponentModelLabel;
	private JComboBox opponentModelCB;
	private BOATextField opponentModelTF;
	private ExtendedComboBoxModel opponentModelModel;

	// ACCEPTANCE STRATEGY
	private JLabel acceptanceStrategyLabel;
	private JComboBox acceptanceStrategyCB;
	private BOATextField acceptanceStrategyTF;
	private ExtendedComboBoxModel acceptanceStrategyModel;

	// OPPONENT MODEL STRATEGY
	private JLabel omStrategyLabel;
	private JComboBox omStrategyCB;
	private BOATextField omStrategyTF;
	private ExtendedComboBoxModel<String> omStrategyModel;

	// the null parameter is a parameter added to strategies without
	// parameters. This is done, such to ensure that the Cartesian product
	// code can still be applied. If this was not the case, a lot of side-cases
	// are needed, which considerably reduces the readability of the code.
	private BOAparameter nullParam = new BOAparameter("null", BigDecimal.ONE,
			BigDecimal.ONE, BigDecimal.ONE);

	/**
	 * This field will contain the result when we finish the dialog. Stays null
	 * if user cancels the dialog
	 */
	private Set<Set<BOAcomponent>> result = null;

	/**
	 * Creates the dialog. Does not yet set it to visible, call getResult for
	 * that.
	 * 
	 * @param info
	 *            the {@link BOAagentInfo} info to be used for initial setting.
	 */
	public BOAAgentUI(BOAagentInfo info) {
		initFrame();
		initBiddingStrategyUI();
		initOpponentModelUI();
		initAcceptanceStrategyUI();
		initOpponentModelStrategyUI();
		addButtons();
		loadLists();
		if (info != null) {
			loadInfo(info);
		}
	}

	private void addButtons() {
		JButton okbutton = new JButton("Ok");
		getContentPane()
				.add(okbutton, new AbsoluteConstraints(20, 120, 75, -1));
		okbutton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				result = generateAgents();
				dispose();
			}
		});

		JButton cancel = new JButton("Cancel");
		getContentPane().add(cancel, new AbsoluteConstraints(120, 120, 75, -1));
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});

	}

	/**
	 * show the GUI and wait till user made selection.
	 * 
	 * @return a Set of Set of {@link BOAcomponent}s. Each of the elements in
	 *         the outer set will be quadruple: a set of four BOAcomponents,
	 *         having the four {@link ComponentsEnum} types required for a BOA
	 *         agent. May return null if the user selected cancel button.
	 */
	public Set<Set<BOAcomponent>> getResult() {
		pack();
		setVisible(true);
		return result;
	}

	private void initFrame() {
		getContentPane().setLayout(new AbsoluteLayout());
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);

		setMaximumSize(new Dimension(1010, 200));
		setMinimumSize(new Dimension(1010, 200));
		setPreferredSize(new Dimension(1010, 200));
		setResizable(false);
		setTitle("Select BOA agent components and parameters");
	}

	/**
	 * Generate all {@link BOAcomponent}s as set by the parameter ranges. The
	 * user can set for each parameter minimum, max and step size. For each
	 * possible parameter value we create another BOA component.
	 * 
	 * @return a Set of Set of {@link BOAcomponent}s. Each of the elements in
	 *         the outer set will be quadruple: a set of four BOAcomponents,
	 *         having the four {@link ComponentsEnum} types required for a BOA
	 *         agent.
	 */
	private Set<Set<BOAcomponent>> generateAgents() {
		Set<BOAcomponent> osStrat = generateStrategies(
				(String) biddingStrategyCB.getSelectedItem(),
				biddingStrategyTF.getBOAparameters(),
				ComponentsEnum.BIDDINGSTRATEGY);
		Set<BOAcomponent> asStrat = generateStrategies(
				(String) acceptanceStrategyCB.getSelectedItem(),
				acceptanceStrategyTF.getBOAparameters(),
				ComponentsEnum.ACCEPTANCESTRATEGY);
		Set<BOAcomponent> omStrat = generateStrategies(
				(String) opponentModelCB.getSelectedItem(),
				opponentModelTF.getBOAparameters(),
				ComponentsEnum.OPPONENTMODEL);
		Set<BOAcomponent> omsStrat = generateStrategies(
				(String) omStrategyCB.getSelectedItem(),
				omStrategyTF.getBOAparameters(), ComponentsEnum.OMSTRATEGY);

		Set<Set<BOAcomponent>> result = SetTools.cartesianProduct(osStrat,
				asStrat, omStrat, omsStrat);
		return result;
	}

	/**
	 * Generates a "strategy" which is a set of {@link BOAcomponent}s, using the
	 * ginve {@link BOAparameter}s. Each parameter can have a range of values,
	 * and what we generate is one {@link BOAcomponent} for each possible
	 * parameter combinatin.
	 * 
	 * @param classname
	 * @param parameters
	 *            the {@link BOAparameter} containing the parameter values to be
	 *            used
	 * @param type
	 *            toe {@link ComponentsEnum} type
	 * @return a set of {@link BOAcomponent}s, one for each possible combination
	 *         of parameters
	 */
	private Set<BOAcomponent> generateStrategies(String classname,
			ArrayList<BOAparameter> parameters, ComponentsEnum type) {

		if (parameters == null || parameters.size() == 0) {
			parameters = new ArrayList<BOAparameter>();
			parameters.add(nullParam);
		}
		Set<Pair<String, BigDecimal>>[] params = new Set[parameters.size()];
		for (int i = 0; i < parameters.size(); i++) {
			params[i] = parameters.get(i).getValuePairs();
		}
		Set<Set<Pair<String, BigDecimal>>> result = SetTools
				.cartesianProduct(params);

		Set<BOAcomponent> strategies = new HashSet<BOAcomponent>();
		Iterator<Set<Pair<String, BigDecimal>>> combinationsIterator = result
				.iterator();

		while (combinationsIterator.hasNext()) {
			// all combinations
			Set<Pair<String, BigDecimal>> set = combinationsIterator.next();
			parameters.remove(nullParam);
			BOAcomponent strat = new BOAcomponent(classname, type, parameters);
			Iterator<Pair<String, BigDecimal>> paramIterator = set.iterator();
			// a set of
			ArrayList<BOAparameter> param = new ArrayList<BOAparameter>();
			while (paramIterator.hasNext()) {
				Pair<String, BigDecimal> pair = (Pair<String, BigDecimal>) paramIterator
						.next();
				strat.addParameter(pair.getFirst(), pair.getSecond());
				param.add(new BOAparameter(pair.getFirst(), pair.getSecond(),
						pair.getSecond(), BigDecimal.ONE));
			}
			strat.setOriginalParameter(param);
			strat.getFullParameters().remove("null");
			strategies.add(strat);
		}
		return strategies;
	}

	/**
	 * load previous info settings to the panels.
	 * 
	 * @param s
	 */
	private void loadInfo(BOAagentInfo s) {
		biddingStrategyCB.setSelectedItem(s.getOfferingStrategy()
				.getClassname());
		biddingStrategyCB.updateUI();
		if (s.getOfferingStrategy().getOriginalParameters() != null
				&& s.getOfferingStrategy().getOriginalParameters().size() > 0) {
			biddingStrategyTF.setText(s.getOfferingStrategy()
					.getOriginalParameters());
			biddingStrategyTF.updateUI();
		}

		acceptanceStrategyCB.setSelectedItem(s.getAcceptanceStrategy()
				.getClassname());
		acceptanceStrategyCB.updateUI();
		if (s.getAcceptanceStrategy().getOriginalParameters() != null
				&& s.getAcceptanceStrategy().getOriginalParameters().size() > 0) {
			acceptanceStrategyTF.setText(s.getAcceptanceStrategy()
					.getOriginalParameters());
			acceptanceStrategyTF.updateUI();
		}

		opponentModelCB.setSelectedItem(s.getOpponentModel().getClassname());
		opponentModelCB.updateUI();
		if (s.getOpponentModel().getOriginalParameters() != null
				&& s.getOpponentModel().getOriginalParameters().size() > 0) {
			opponentModelTF.setText(s.getOpponentModel()
					.getOriginalParameters());
			opponentModelTF.updateUI();
		}

		omStrategyCB.setSelectedItem(s.getOMStrategy().getClassname());
		omStrategyCB.updateUI();
		if (s.getOMStrategy().getOriginalParameters() != null
				&& s.getOMStrategy().getOriginalParameters().size() > 0) {
			omStrategyTF.setText(s.getOMStrategy().getOriginalParameters());
			omStrategyTF.updateUI();
		}

	}

	private void loadLists() {
		BOAagentRepository dar = BOAagentRepository.getInstance();
		ArrayList<String> offeringStrategies = dar.getOfferingStrategies();
		ArrayList<String> acceptanceConditions = dar.getAcceptanceStrategies();
		ArrayList<String> opponentModels = dar.getOpponentModels();
		ArrayList<String> omStrategies = dar.getOMStrategies();

		biddingStrategyModel = new ExtendedComboBoxModel<String>();
		Collections.sort(offeringStrategies);
		biddingStrategyModel.setInitialContent(offeringStrategies);
		biddingStrategyCB.setModel(biddingStrategyModel);
		if (offeringStrategies.size() > 0) {
			biddingStrategyCB.setSelectedIndex(0);
		}

		acceptanceStrategyModel = new ExtendedComboBoxModel<String>();
		Collections.sort(acceptanceConditions);
		acceptanceStrategyModel.setInitialContent(acceptanceConditions);
		acceptanceStrategyCB.setModel(acceptanceStrategyModel);
		if (acceptanceConditions.size() > 0) {
			acceptanceStrategyCB.setSelectedIndex(0);
		}

		opponentModelModel = new ExtendedComboBoxModel<String>();
		Collections.sort(opponentModels);
		opponentModelModel.setInitialContent(opponentModels);
		opponentModelCB.setModel(opponentModelModel);
		if (opponentModels.size() > 0) {
			opponentModelCB.setSelectedIndex(0);
		}

		omStrategyModel = new ExtendedComboBoxModel<String>();
		Collections.sort(omStrategies);
		omStrategyModel.setInitialContent(omStrategies);
		omStrategyCB.setModel(omStrategyModel);
		if (omStrategies.size() > 0) {
			omStrategyCB.setSelectedIndex(0);
		}
	}

	private void initBiddingStrategyUI() {
		final JButton change = new JButton("Change");

		biddingStrategyLabel = new JLabel();
		biddingStrategyLabel.setFont(new Font("Tahoma", 1, 13));
		biddingStrategyLabel.setText("Bidding Strategy");
		getContentPane().add(biddingStrategyLabel,
				new AbsoluteConstraints(10, 15, 230, -1));

		biddingStrategyTF = new BOATextField(NegoGUIApp.negoGUIView.getFrame());
		getContentPane().add(biddingStrategyTF,
				new AbsoluteConstraints(10, 70, 150, -1));

		biddingStrategyCB = new JComboBox();
		getContentPane().add(biddingStrategyCB,
				new AbsoluteConstraints(10, 40, 230, -1));
		biddingStrategyCB
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						BOArepItem item = BOAagentRepository.getInstance()
								.getBiddingStrategyRepItem(
										biddingStrategyCB.getSelectedItem()
												.toString());
						biddingStrategyTF.setText(item.getParameters());
						change.setEnabled(!(item.getParameters().isEmpty()));
					}
				});

		getContentPane().add(change, new AbsoluteConstraints(165, 70, 75, -1));
		change.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				if (biddingStrategyTF.getBOAparameters().size() == 0) {
					JOptionPane.showMessageDialog(null,
							"This item has no parameters.",
							"Item notification", 1);
				} else {
					ArrayList<BOAparameter> result = new ParameterFrame(
							NegoGUIApp.negoGUIView.getFrame())
							.getResult(biddingStrategyTF.getBOAparameters());
					biddingStrategyTF.setText(result);
				}
			}
		});
	}

	private void initOpponentModelUI() {
		final JButton change = new JButton("Change");

		opponentModelLabel = new JLabel();
		opponentModelLabel.setFont(new Font("Tahoma", 1, 13));
		opponentModelLabel.setText("Opponent Model");
		getContentPane().add(opponentModelLabel,
				new AbsoluteConstraints(510, 15, 230, -1));

		opponentModelTF = new BOATextField(NegoGUIApp.negoGUIView.getFrame());
		getContentPane().add(opponentModelTF,
				new AbsoluteConstraints(510, 70, 150, -1));

		opponentModelCB = new JComboBox();
		getContentPane().add(opponentModelCB,
				new AbsoluteConstraints(510, 40, 230, -1));
		opponentModelCB.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				BOArepItem item = BOAagentRepository.getInstance()
						.getOpponentModelRepItem(
								opponentModelCB.getSelectedItem().toString());
				opponentModelTF.setText(item.getParameters());
				change.setEnabled(!(item.getParameters().isEmpty()));
			}
		});

		getContentPane().add(change, new AbsoluteConstraints(665, 70, 75, -1));
		change.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				if (opponentModelTF.getBOAparameters().size() == 0) {
					JOptionPane.showMessageDialog(null,
							"This item has no parameters.",
							"Item notification", 1);
				} else {
					ArrayList<BOAparameter> result = new ParameterFrame(
							NegoGUIApp.negoGUIView.getFrame())
							.getResult(opponentModelTF.getBOAparameters());
					opponentModelTF.setText(result);
				}
			}
		});
	}

	private void initAcceptanceStrategyUI() {
		final JButton change = new JButton("Change");

		acceptanceStrategyLabel = new JLabel();
		acceptanceStrategyLabel.setFont(new Font("Tahoma", 1, 13));
		acceptanceStrategyLabel.setText("Acceptance Strategy");
		getContentPane().add(acceptanceStrategyLabel,
				new AbsoluteConstraints(260, 15, 230, 21));

		acceptanceStrategyTF = new BOATextField(
				NegoGUIApp.negoGUIView.getFrame());
		getContentPane().add(acceptanceStrategyTF,
				new AbsoluteConstraints(260, 70, 150, -1));

		acceptanceStrategyCB = new JComboBox();
		getContentPane().add(acceptanceStrategyCB,
				new AbsoluteConstraints(260, 40, 230, -1));
		acceptanceStrategyCB
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						BOArepItem item = BOAagentRepository.getInstance()
								.getAcceptanceStrategyRepItem(
										acceptanceStrategyCB.getSelectedItem()
												.toString());
						acceptanceStrategyTF.setText(item.getParameters());
						change.setEnabled(!(item.getParameters().isEmpty()));
					}
				});

		getContentPane().add(change, new AbsoluteConstraints(415, 70, 75, -1));
		change.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				if (acceptanceStrategyTF.getBOAparameters().size() == 0) {
					JOptionPane.showMessageDialog(null,
							"This item has no parameters.",
							"Item notification", 1);
				} else {
					ArrayList<BOAparameter> result = new ParameterFrame(
							NegoGUIApp.negoGUIView.getFrame())
							.getResult(acceptanceStrategyTF.getBOAparameters());
					acceptanceStrategyTF.setText(result);
				}
			}
		});
	}

	private void initOpponentModelStrategyUI() {
		final JButton change = new JButton("Change");

		omStrategyLabel = new JLabel();
		omStrategyLabel.setFont(new Font("Tahoma", 1, 13));
		omStrategyLabel.setText("Opponent Model Strategy");
		getContentPane().add(omStrategyLabel,
				new AbsoluteConstraints(760, 15, 230, -1));

		omStrategyTF = new BOATextField(NegoGUIApp.negoGUIView.getFrame());
		getContentPane().add(omStrategyTF,
				new AbsoluteConstraints(760, 70, 150, -1));

		omStrategyCB = new JComboBox();
		getContentPane().add(omStrategyCB,
				new AbsoluteConstraints(760, 40, 230, -1));
		omStrategyCB.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				BOArepItem item = BOAagentRepository.getInstance()
						.getOpponentModelStrategyRepItem(
								omStrategyCB.getSelectedItem().toString());
				omStrategyTF.setText(item.getParameters());
				change.setEnabled(!(item.getParameters().isEmpty()));
			}
		});

		getContentPane().add(change, new AbsoluteConstraints(915, 70, 75, -1));
		change.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				if (omStrategyTF.getBOAparameters().size() == 0) {
					JOptionPane.showMessageDialog(null,
							"This item has no parameters.",
							"Item notification", 1);
				} else {
					ArrayList<BOAparameter> result = new ParameterFrame(
							NegoGUIApp.negoGUIView.getFrame())
							.getResult(omStrategyTF.getBOAparameters());
					omStrategyTF.setText(result);
				}
			}
		});
	}

}
