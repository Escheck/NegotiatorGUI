package negotiator.gui.boaframework;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import negotiator.boaframework.BOAagentInfo;
import negotiator.boaframework.BOAcomponent;
import negotiator.boaframework.BOAparameter;
import negotiator.boaframework.repository.BOAagentRepository;
import negotiator.gui.ExtendedListModel;
import negotiator.gui.MultiListSelectionModel;
import negotiator.gui.NegoGUIApp;
import misc.Pair;
import misc.SetTools;

/**
 * This form is used to input the decoupled agents.
 * The GUI was originally created using Netbeans, and is therefore probably still
 * compatible with their form editor.
 * 
 * @author Mark Hendrikx (m.j.c.hendrikx@student.tudelft.nl)
 * @version 11-12-11
 */
public class BOAagentsFrame extends JDialog {

	private static final long serialVersionUID = 5946539859382260420L;
	
	private JLabel decoupledLabel;
	
	// GUI elements of the bidding strategy
	private JLabel osLabel;
	private JScrollPane osListPane;
    private JList<String> osList;
    private ExtendedListModel<String> osModel;
    private JScrollPane osParamsPane;
	private JList<BOAparameter> osParams;
	private DefaultListModel<BOAparameter> osParamsModel;
	private JButton addOsParam;
	private JButton removeOsParam;
	
	// GUI elements of the acceptance strategy
    private JLabel asLabel;
    private JScrollPane asListPane;
    private JList<String> asList;
    private ExtendedListModel<String> asModel;
    private JScrollPane asParamsPane;
    private JList<BOAparameter> asParams;
    private DefaultListModel<BOAparameter> asParamsModel;
    private JButton addAsParam;
    private JButton removeAsParam;
    
    // GUI elements of the opponent model
    private JLabel omLabel;
    private JScrollPane omListPane;
    private JList<String> omList;
    private ExtendedListModel<String> omModel;
    private JScrollPane omParamsPane;
    private JList<BOAparameter> omParams;
    private DefaultListModel<BOAparameter> omParamsModel;
    private JButton addOmParam;
    private JButton removeOmParam;
    
    // GUI elements of the opponent model strategy
	private JLabel omsLabel;
	private JScrollPane omsListPane;
    private JList<String> omsList;
    private ExtendedListModel<String> omsModel;
    private JScrollPane omsParamsPane;
	private JList<BOAparameter> omsParams;
	private DefaultListModel<BOAparameter> omsParamsModel;
	private JButton addOmsParam;
	private JButton removeOmsParam;
    
	// GUI elements of the overview of BOA agents
    private JScrollPane agentsListPane;
    private JList<BOAagentInfo> agentsList;
    private DefaultListModel<BOAagentInfo> agentsModel;
    private JButton addAgents;
    private JButton clearSelection;
    private JButton clearAll;
    private JButton saveButton;
    private JButton cancelButton;
    private JSeparator seperator;
    
    // variable which indicates if the some parameters of the four components
    // should be preset.
    private final static boolean DEBUG = false;
    
    // the null parameter is a parameter added to strategies without
    // parameters. This is done, such to ensure that the Cartesian product
    // code can still be applied. If this was not the case, a lot of side-cases
    // are needed, which considerably reduces the readability of the code.
    private BOAparameter nullParam;
    private ArrayList<BOAagentInfo> result;
    
    public BOAagentsFrame(Frame frame) {
    	super(frame, "Select decoupled agents", true);
    	this.setLocation(frame.getLocation().x + frame.getWidth() / 4, frame.getLocation().y + frame.getHeight() / 4);
    	nullParam = new BOAparameter("null", 1, 1, 1);
    }

    public ArrayList<BOAagentInfo> getResult(ArrayList<BOAagentInfo> BOAagentList) {
    	initFrameGUI();
        initOfferingStrategyGUI();
        initAcceptanceStrategyGUI();
        initOpponentModelGUI();
        initOMStrategyGUI();
        initAgentsGUI();
        loadLists();
        initControls();
        
        for(BOAagentInfo agent: BOAagentList){
			agentsModel.addElement(agent);
        }
        
        if (DEBUG) {
        	asParamsModel.addElement(new BOAparameter("a", 1, 1, 1));
        	asParamsModel.addElement(new BOAparameter("b", 0, 0, 1));
        	asParamsModel.addElement(new BOAparameter("ad", 1, 1, 1));
        	asParamsModel.addElement(new BOAparameter("bd", 0, 0, 1));
        	asParamsModel.addElement(new BOAparameter("t", 0.99, 0.99, 1));
        	asParamsModel.addElement(new BOAparameter("c", 0.98, 0.98, 1));
        	omsParamsModel.addElement(new BOAparameter("t", 0.35, 0.35, 1));
        }
        pack();
        setVisible(true);
        return result;
    }

	private void initControls() {
    	addOsParam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BOAparameter param = (BOAparameter)new ParameterInput(NegoGUIApp.negoGUIView.getFrame(), "Input parameter").getResult();
				if (param != null) {
					osParamsModel.addElement(param);
				}
			}
		});
    	addAsParam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BOAparameter param = (BOAparameter)new ParameterInput(NegoGUIApp.negoGUIView.getFrame(), "Input parameter").getResult();
				if (param != null) {
					asParamsModel.addElement(param);
				}
			}
		});
    	addOmParam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BOAparameter param = (BOAparameter)new ParameterInput(NegoGUIApp.negoGUIView.getFrame(), "Input parameter").getResult();
				if (param != null) {
					omParamsModel.addElement(param);
				}
			}
		});
    	
    	addOmsParam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BOAparameter param = (BOAparameter)new ParameterInput(NegoGUIApp.negoGUIView.getFrame(), "Input parameter").getResult();
				if (param != null) {
					omsParamsModel.addElement(param);
				}
			}
		});
    	
    	removeOsParam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeSelectedIndices(osParams, osParamsModel);
			}
		});
    	
    	removeAsParam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeSelectedIndices(asParams, asParamsModel);
			}
		});
    	
    	removeOmParam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeSelectedIndices(omParams, omParamsModel);
			}
		});
    	
    	removeOmsParam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeSelectedIndices(omsParams, omsParamsModel);
			}
		});
    	
    	clearSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeSelectedIndices(agentsList, agentsModel);
			}
		});
    	
    	clearAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				agentsList.clearSelection();
				agentsModel.clear();
			}
		});
    	
    	cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
    	
    	/*addAgentListButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<BOAagentInfo> agentList = new AgentListFrame(NegoGUIApp.negoGUIView.getFrame(), "test").getReuslt();
				/*
				for(BOAagentInfo agent: agentList){
					agentsModel.addElement(agent);

				}
				
			}
		});
    	*/    	addAgents.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (osList.getSelectedIndex() >= 0 && asList.getSelectedIndex() >= 0 && 
						omList.getSelectedIndex() >= 0 && omsList.getSelectedIndex() >= 0) {
					generateAgentCombinations();
				} else {
					JOptionPane.showMessageDialog(null, "Make sure that you selected an element in every list.", "Configuration error", 0);
				}
			}
		});
    	
    	saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<BOAagentInfo> agents = new ArrayList<BOAagentInfo>();
				for (int i = 0; i < agentsModel.getSize(); i++) {
					agents.add(agentsModel.getElementAt(i));
				}
				result = agents;
				dispose();
			}
		});
    	
	}

    private <A> void removeSelectedIndices(JList<A> list, DefaultListModel<A> model) {
    	if (list.getSelectedIndices().length > 0) {
    		int[] tmp = list.getSelectedIndices();
    		int[] selectedIndices = list.getSelectedIndices();

    		for (int i = tmp.length-1; i >=0; i--) {
    			selectedIndices = list.getSelectedIndices();
    			model.removeElementAt(selectedIndices[i]);
    		}
    	}
	}
    
	private void loadLists() {
		BOAagentRepository dar = BOAagentRepository.getInstance();
    	ArrayList<String> offeringStrategies = dar.getOfferingStrategies();
    	ArrayList<String> acceptanceConditions = dar.getAcceptanceStrategies();
    	ArrayList<String> opponentModels = dar.getOpponentModels();
    	ArrayList<String> omStrategies = dar.getOMStrategies();
    	
    	osModel = new ExtendedListModel<String>();
    	Collections.sort(offeringStrategies);
    	osModel.setInitialContent(offeringStrategies);
		osList.setModel(osModel);
		
		asModel = new ExtendedListModel<String>();
		Collections.sort(acceptanceConditions);
		asModel.setInitialContent(acceptanceConditions);
		asList.setModel(asModel);
		
		omModel = new ExtendedListModel<String>();
		Collections.sort(opponentModels);
		omModel.setInitialContent(opponentModels);
		omList.setModel(omModel);
		
		omsModel = new ExtendedListModel<String>();
		Collections.sort(omStrategies);
		omsModel.setInitialContent(omStrategies);
		omsList.setModel(omsModel);
		
		agentsModel = new DefaultListModel<BOAagentInfo>();
		agentsList.setModel(agentsModel);
    }
    
    private void initFrameGUI() {
    	setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        setTitle("Select decoupled agents");
    }
    
    private void initAgentsGUI() {
    	// Create the seperators
    	seperator = new JSeparator();
        getContentPane().add(seperator, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 355, 1020, -1));
        seperator = new JSeparator();
        getContentPane().add(seperator, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 620, 1020, -1));
    	// Instantiate the label
    	decoupledLabel = new JLabel("Decoupled agents");
        getContentPane().add(decoupledLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 365, 170, 20));
        // Instantiate the list pane
        agentsListPane = new JScrollPane();
        getContentPane().add(agentsListPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 395, 1000, 180));
        // Instantiate the list
        agentsList = new JList<BOAagentInfo>();
        agentsList.setSelectionModel(new MultiListSelectionModel());
        agentsListPane.setViewportView(agentsList);
        // Instantiate the buttons
        addAgents = new JButton();
        addAgents.setText("Add agents");
        getContentPane().add(addAgents, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 580, 140, 30));
        //addAgentListButton = new JButton();
        //addAgentListButton.setText("Add AgentList");
       // getContentPane().add(addAgentListButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 580, 140, 30));
        clearSelection = new JButton();
        clearSelection.setText("Clear selection");
        getContentPane().add(clearSelection, new org.netbeans.lib.awtextra.AbsoluteConstraints(152, 580, 140, 30));
        
        clearAll = new JButton();
        clearAll.setText("Clear all");
        getContentPane().add(clearAll, new org.netbeans.lib.awtextra.AbsoluteConstraints(294, 580, 140, 30));
        
        saveButton = new JButton();
        saveButton.setText("Save");
        getContentPane().add(saveButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(436, 580, 140, 30));
        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        getContentPane().add(cancelButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(578, 580, 140, 30));
        
	}

    private void initOfferingStrategyGUI() {
    	// Instantiate the label
    	osLabel = new JLabel("Bidding Strategy");
    	getContentPane().add(osLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 220, -1));
    	// Instantiate the list pane
    	osListPane = new JScrollPane();
    	getContentPane().add(osListPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 220, 200));
    	// Instantiate the list
    	osList =  new JList<String>() {
			private static final long serialVersionUID = 1L;

			public String getToolTipText(MouseEvent evt) {
                int index = locationToIndex(evt.getPoint());
                String bs = (String)getModel().getElementAt(index);
                return BOAagentRepository.getOfferingStrategyTooltip(bs);
            }
        };
    	osList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        osListPane.setViewportView(osList);
    	// Instantiate the params pane
        osParamsPane = new JScrollPane();
        getContentPane().add(osParamsPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 235, 220, 80));
        // Instantiate the params list
        osParamsModel = new DefaultListModel<BOAparameter>();
        osParams = new JList<BOAparameter>(osParamsModel);
        osParams.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        osParamsPane.setViewportView(osParams);
    	// Instantiate the buttons
        addOsParam = new JButton();
        addOsParam.setText("Add");
        getContentPane().add(addOsParam, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 315, 108, -1));
        removeOsParam = new JButton();
        removeOsParam.setText("Remove");
        getContentPane().add(removeOsParam, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 315, 107, -1));
    }
    
    private void initAcceptanceStrategyGUI() {
    	// Instantiate the label
    	asLabel = new JLabel("Acceptance Strategy");
        getContentPane().add(asLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 10, 220, -1));
    	// Instantiate the list pane
        asListPane = new JScrollPane();
        getContentPane().add(asListPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 35, 220, 200));
    	// Instantiate the list
        asList = new JList<String>() {
			private static final long serialVersionUID = 1L;

			public String getToolTipText(MouseEvent evt) {
                int index = locationToIndex(evt.getPoint());
                String as = (String)getModel().getElementAt(index);
                return BOAagentRepository.getAcceptanceStrategyTooltip(as);
            }
        };
        asList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        asListPane.setViewportView(asList);
    	// Instantiate the params pane
        asParamsPane = new JScrollPane();
        getContentPane().add(asParamsPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 235, 220, 80));
    	// Instantiate the params list
        asParamsModel = new DefaultListModel<BOAparameter>();
        asParams = new JList<BOAparameter>(asParamsModel);
        asParams.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        asParamsPane.setViewportView(asParams);
    	// Instantiate the button
        addAsParam = new JButton();
        addAsParam.setText("Add");
        getContentPane().add(addAsParam, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 315, 108, -1));
        removeAsParam = new JButton();
        removeAsParam.setText("Remove");
        getContentPane().add(removeAsParam, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 315, 107, -1));
    }
	
    private void initOMStrategyGUI() {
        // Instantiate the label
        omsLabel = new JLabel("Opponent Model Strategy");
        getContentPane().add(omsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 10, 220, -1));
        // Instantiate the list pane
        omsListPane = new JScrollPane();
        getContentPane().add(omsListPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 35, 220, 200));
        // Instantiate the list
        omsList = new JList<String>() {
			private static final long serialVersionUID = 1L;

			public String getToolTipText(MouseEvent evt) {
                int index = locationToIndex(evt.getPoint());
                String oms = (String)getModel().getElementAt(index);
                return BOAagentRepository.getOpponentModelStrategyTooltip(oms);
            }
        };
        omsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        omsListPane.setViewportView(omsList);
        // Instantiate the params pane
        omsParamsPane = new JScrollPane();
        getContentPane().add(omsParamsPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 235, 220, 80));
        // Instantiate the params list
        omsParamsModel = new DefaultListModel<BOAparameter>();
        omsParams = new JList<BOAparameter>(omsParamsModel);
        omsParams.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        omsParamsPane.setViewportView(omsParams);
        // Instantiate the button
        addOmsParam = new JButton();
        addOmsParam.setText("Add");
        getContentPane().add(addOmsParam, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 315, 108, -1));
        removeOmsParam = new JButton();
        removeOmsParam.setText("Remove");
        getContentPane().add(removeOmsParam, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 315, 107, -1));
	}
    
    private void initOpponentModelGUI() {
        // Instantiate the label
        omLabel = new JLabel("Opponent Model");
        getContentPane().add(omLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 10, 220, -1));
        // Instantiate the list pane
        omListPane = new JScrollPane();
        getContentPane().add(omListPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 35, 220, 200));
        // Instantiate the list
        omList = new JList<String>() {
			private static final long serialVersionUID = 1L;

			public String getToolTipText(MouseEvent evt) {
                int index = locationToIndex(evt.getPoint());
                String om = (String)getModel().getElementAt(index);
                return BOAagentRepository.getOpponentModelTooltip(om);
            }
        };
        omList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        omListPane.setViewportView(omList);
        // Instantiate the params pane
        omParamsPane = new JScrollPane();
        getContentPane().add(omParamsPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 235, 220, 80));
        // Instantiate the params list
        omParamsModel = new DefaultListModel<BOAparameter>();
        omParams = new JList<BOAparameter>(omParamsModel);
        omParams.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        omParamsPane.setViewportView(omParams);
        // Instantiate the button
        addOmParam = new JButton();
        addOmParam.setText("Add");
        getContentPane().add(addOmParam, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 315, 108, -1));
        removeOmParam = new JButton();
        removeOmParam.setText("Remove");
        getContentPane().add(removeOmParam, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 315, 107, -1));
	}
    
	private void generateAgentCombinations() {
		Set<BOAcomponent> osStrat = generateStrategies(osModel.getElementAt(osList.getSelectedIndex()), getParameters(osParamsModel), "bs");
		Set<BOAcomponent> asStrat = generateStrategies(asModel.getElementAt(asList.getSelectedIndex()), getParameters(asParamsModel), "as");
		Set<BOAcomponent> omStrat = generateStrategies(omModel.getElementAt(omList.getSelectedIndex()), getParameters(omParamsModel), "om");
		Set<BOAcomponent> omsStrat = generateStrategies(omsModel.getElementAt(omsList.getSelectedIndex()), getParameters(omsParamsModel), "oms");
		
		Set<Set<BOAcomponent>> result = SetTools.cartesianProduct(osStrat, asStrat, omStrat, omsStrat);
		Iterator<Set<BOAcomponent>> strategyIterator = result.iterator();
		while (strategyIterator.hasNext()) {
			Set<BOAcomponent> fullStrat = strategyIterator.next();
			Iterator<BOAcomponent> strat = fullStrat.iterator();
			BOAcomponent os = null, as = null, om = null, oms = null;
			while (strat.hasNext()) {
				BOAcomponent strategy = strat.next();
				if (strategy.getType().equals("bs")) {
					os = strategy;
				} else if (strategy.getType().equals("as")) {
					as = strategy;
				} else if (strategy.getType().equals("om")){
					om = strategy;
				} else if (strategy.getType().equals("oms")) {
					oms = strategy;
				}
			}
			BOAagentInfo agent = new BOAagentInfo(os, as, om, oms);
			agentsModel.addElement(agent);

			
		}
	}
	
	private Set<BOAcomponent> generateStrategies(String classname,
			ArrayList<BOAparameter> parameters, String type) {

		// retrieve all sets of parameters
		@SuppressWarnings("unchecked") // something strange in Java, google "Cannot create a generic array of"
		Set<Pair<String, Double>>[] params = new Set[parameters.size()];
		for (int i = 0; i < parameters.size(); i++) {
			params[i] = parameters.get(i).getValuePairs();
		}
		Set<Set<Pair<String, Double>>> result = SetTools.cartesianProduct(params);
		
		Set<BOAcomponent> strategies = new HashSet<BOAcomponent>();
		Iterator<Set<Pair<String, Double>>> combinationsIterator = result.iterator();

		while (combinationsIterator.hasNext()) {
			// all combinations
			Set<Pair<String, Double>> set = combinationsIterator.next();
			BOAcomponent strat = new BOAcomponent(classname, type);
			Iterator<Pair<String, Double>> paramIterator = set.iterator();
			// a set of 
			while (paramIterator.hasNext()) {
				Pair<String, Double> pair = (Pair<String, Double>) paramIterator.next();
				strat.addParameter(pair.getFirst(), pair.getSecond());
			}
			strat.getParameters().remove("null");
			strategies.add(strat);
		}
		return strategies;
	}

	private ArrayList<BOAparameter> getParameters(DefaultListModel<BOAparameter> model) {
		ArrayList<BOAparameter> profiles = new ArrayList<BOAparameter>();

		if (model != null && model.size() > 0) {
			for (int i = 0; i < model.getSize(); i++) {
				profiles.add(model.getElementAt(i));
			}
		} else {
			profiles.add(nullParam);
		}
		return profiles;
	}
}