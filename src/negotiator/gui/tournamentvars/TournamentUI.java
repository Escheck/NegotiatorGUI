/*
 * TournamentUI.java
 *
 * Created on September 5, 2008, 10:05 AM
 */

package negotiator.gui.tournamentvars;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import misc.Serializer;
import negotiator.boaframework.BOAagentInfo;
import negotiator.distributedtournament.DBController;
import negotiator.exceptions.Warning;
import negotiator.gui.NegoGUIApp;
import negotiator.gui.boaframework.BOAagentsFrame;
import negotiator.gui.progress.ProgressUI2;
import negotiator.gui.progress.TournamentProgressUI2;
import negotiator.repository.AgentRepItem;
import negotiator.repository.DomainRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.ProtocolRepItem;
import negotiator.repository.RepItem;
import negotiator.repository.Repository;
import negotiator.tournament.Tournament;
import negotiator.tournament.TournamentConfiguration;
import negotiator.tournament.TournamentRunner;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.tournament.VariablesAndValues.AgentValue;
import negotiator.tournament.VariablesAndValues.AgentVariable;
import negotiator.tournament.VariablesAndValues.BOAagentValue;
import negotiator.tournament.VariablesAndValues.BOAagentVariable;
import negotiator.tournament.VariablesAndValues.DBLocationValue;
import negotiator.tournament.VariablesAndValues.DBLocationVariable;
import negotiator.tournament.VariablesAndValues.DBPasswordValue;
import negotiator.tournament.VariablesAndValues.DBPasswordVariable;
import negotiator.tournament.VariablesAndValues.DBSessionValue;
import negotiator.tournament.VariablesAndValues.DBSessionVariable;
import negotiator.tournament.VariablesAndValues.DBUserValue;
import negotiator.tournament.VariablesAndValues.DBUserVariable;
import negotiator.tournament.VariablesAndValues.MultipleAgentsVariable;
import negotiator.tournament.VariablesAndValues.ProfileValue;
import negotiator.tournament.VariablesAndValues.ProfileVariable;
import negotiator.tournament.VariablesAndValues.ProtocolValue;
import negotiator.tournament.VariablesAndValues.ProtocolVariable;
import negotiator.tournament.VariablesAndValues.TotalSessionNumberValue;
import negotiator.tournament.VariablesAndValues.TotalSessionNumberVariable;
import negotiator.tournament.VariablesAndValues.TournamentOptionsValue;
import negotiator.tournament.VariablesAndValues.TournamentOptionsVariable;
import negotiator.tournament.VariablesAndValues.TournamentValue;
import negotiator.tournament.VariablesAndValues.TournamentVariable;

import org.jdesktop.application.Action;

/**
 * @author dmytro
 */
public class TournamentUI extends javax.swing.JPanel {
	private static final long serialVersionUID = -3629136950558234574L;

	/** this contains the variables and their possible values. */
	Tournament tournament;

	public static Serializer<Tournament> previousTournament;

	AbstractTableModel dataModel;

	Repository domainrepository; // contains all available domains and profiles
									// to pick from.
	Repository agentrepository; // contains all available agents to pick from.

	boolean distributed = false;

	/** Creates new form TournamentUI */
	public TournamentUI(boolean distributed) {
		this.distributed = distributed;
		initComponents();
		// Tournament t=new TournamentTwoPhaseAuction(); // bit stupid to
		// correct an empty one, but will be useful later.

		Tournament t;

		String name = "previousTournament";
		if (distributed) {
			name += "Distributed";
		}
		previousTournament = new Serializer<Tournament>(name);

		final Tournament readFromDisk = previousTournament.readFromDisk();
		if (readFromDisk == null)
			t = new Tournament();
		else {
			System.out.println("Using the tournament setup from "
					+ previousTournament.getFileName() + ".");
			t = readFromDisk;
		}

		correct_tournament(t);

		tournament = t;
		try {
			domainrepository = Repository.get_domain_repos();
			agentrepository = Repository.get_agent_repository();
		} catch (Exception e) {
			e.printStackTrace();
		}

		dataModel = new AbstractTableModel() {
			private static final long serialVersionUID = 818546692511677395L;
			final String columnnames[] = { "Variable", "Values" };

			public int getColumnCount() {
				return columnnames.length;
			}

			public int getRowCount() {
				return tournament.getVariables().size();
			}

			public Object getValueAt(int row, int col) {
				TournamentVariable var = tournament.getVariables().get(row);
				switch (col) {
				case 0: {
					String res = var.varToString();
					return res;
				}
				case 1:
					return var.getValues().toString();
				default:
					new Warning("Illegal column in table " + col);
				}
				return col;
			}

			public String getColumnName(int column) {
				return columnnames[column];
			}
		};
		jTable1.setModel(dataModel);
		jTable1.getColumnModel().getColumn(0).setMaxWidth(150);
		// jTable1.getColumnModel().getColumn(0).setWidth(150);
		jTable1.getColumnModel().getColumn(0).setMinWidth(140);
		jTable1.getColumnModel().getColumn(0).setPreferredWidth(150);

	}

	void editVariable(TournamentVariable v) throws Exception {
		// numerous classes here result in highly duplicate code and pretty
		// unreadable code as well.....
		// IMHO the strong typechecking gives maybe even more problems than it
		// resolves...
		if (v instanceof ProfileVariable) {
			ArrayList<ProfileRepItem> items = getProfileRepItems();
			ArrayList<ProfileRepItem> newv = (ArrayList<ProfileRepItem>) new RepItemVarUI<ProfileRepItem>(
					NegoGUIApp.negoGUIView.getFrame(), "Select profiles")
					.getResult(items, tournament.getProfiles());// (AgentVariable)v);
			if (newv == null)
				return; // cancel pressed.
			ArrayList<TournamentValue> newtvs = new ArrayList<TournamentValue>();
			for (ProfileRepItem profitem : newv)
				newtvs.add(new ProfileValue(profitem));
			v.setValues(newtvs);
		} else if (v instanceof ProtocolVariable) {
			if (Repository.getProtocolRepository().getItems().size() > 0) {
				// #881 always show GUI
				ArrayList<ProtocolRepItem> newv = (ArrayList<ProtocolRepItem>) new ProtocolVarUI(
						NegoGUIApp.negoGUIView.getFrame()).getResult();
				System.out.println("result new vars=" + newv);
				if (newv == null)
					return; // cancel pressed.
				// make agentvalues for each selected agent and add to the
				// agentvariable
				ArrayList<TournamentValue> newtvs = new ArrayList<TournamentValue>();
				for (ProtocolRepItem protocolItem : newv)
					newtvs.add(new ProtocolValue(protocolItem));
				v.setValues(newtvs);
			}
		} else if (v instanceof AgentVariable) {
			ArrayList<AgentRepItem> items = getAgentRepItems();
			ArrayList<AgentRepItem> prevSelected = new ArrayList<AgentRepItem>();

			for (TournamentValue wrappedAgent : v.getValues()) {
				AgentValue agentValue = ((AgentValue) wrappedAgent);
				prevSelected.add(agentValue.getValue());
			}

			ArrayList<AgentRepItem> newv = (ArrayList<AgentRepItem>) new RepItemVarUI<AgentRepItem>(
					NegoGUIApp.negoGUIView.getFrame(), "Select agents")
					.getResult(items, prevSelected);
			if (newv == null)
				return; // cancel pressed.
			ArrayList<TournamentValue> newtvs = new ArrayList<TournamentValue>();

			for (AgentRepItem profitem : newv) {
				newtvs.add(new AgentValue(profitem));
			}
			v.setValues(newtvs);
		} else if (v instanceof BOAagentVariable) {
			ArrayList<BOAagentInfo> newv;
			if (((BOAagentVariable) v).getSide().equals("A")) {
				newv = (ArrayList<BOAagentInfo>) new BOAagentsFrame(
						NegoGUIApp.negoGUIView.getFrame()).getResult(tournament
						.getBOAagentA());
				if (newv == null)
					return;
				tournament.setBOAagentA(newv);
				ArrayList<TournamentValue> newtvs = new ArrayList<TournamentValue>();
				for (BOAagentInfo item : newv)
					newtvs.add(new BOAagentValue(item));
				v.setValues(newtvs);
			} else {
				newv = (ArrayList<BOAagentInfo>) new BOAagentsFrame(
						NegoGUIApp.negoGUIView.getFrame()).getResult(tournament
						.getBOAagentB());
				if (newv == null)
					return;
				tournament.setBOAagentB(newv);
				ArrayList<TournamentValue> newtvs = new ArrayList<TournamentValue>();
				for (BOAagentInfo item : newv)
					newtvs.add(new BOAagentValue(item));
				v.setValues(newtvs);
			}
		} else if (v instanceof TotalSessionNumberVariable) {
			TotalSessionNumberValue value = (TotalSessionNumberValue) (new SingleValueVarUI(
					NegoGUIApp.negoGUIView.getFrame())).getResult();
			if (value == null)
				return;
			ArrayList<TournamentValue> newtvs = new ArrayList<TournamentValue>();
			newtvs.add(value);
			v.setValues(newtvs);
		} else if (v instanceof TournamentOptionsVariable) {
			HashMap<String, Integer> optionsMap = (new TournamentOptionsUI(
					NegoGUIApp.negoGUIView.getFrame())).getResult(tournament
					.getOptions());
			TournamentValue value = new TournamentOptionsValue(optionsMap);
			ArrayList<TournamentValue> newtvs = new ArrayList<TournamentValue>();
			newtvs.add(value);
			v.setValues(newtvs);
		} else if (distributed) {

			if (v instanceof DBLocationVariable) {
				SingleStringVarUI gui = new SingleStringVarUI(
						NegoGUIApp.negoGUIView.getFrame());
				gui.setTitle("Enter DB address");
				Object result = gui.getResult();
				if (result == null)
					return;
				DBLocationValue value = new DBLocationValue(result.toString());
				ArrayList<TournamentValue> newtvs = new ArrayList<TournamentValue>();
				newtvs.add(value);
				v.setValues(newtvs);
			} else if (v instanceof DBUserVariable) {
				SingleStringVarUI gui = new SingleStringVarUI(
						NegoGUIApp.negoGUIView.getFrame());
				gui.setTitle("Enter DB username");
				Object result = gui.getResult();
				if (result == null)
					return;
				DBUserValue value = new DBUserValue(result.toString());
				ArrayList<TournamentValue> newtvs = new ArrayList<TournamentValue>();
				newtvs.add(value);
				v.setValues(newtvs);
			} else if (v instanceof DBPasswordVariable) {
				SingleStringVarUI gui = new SingleStringVarUI(
						NegoGUIApp.negoGUIView.getFrame());
				gui.setTitle("Enter DB password");
				Object result = gui.getResult();
				if (result == null)
					return;
				DBPasswordValue value = new DBPasswordValue(result.toString());
				ArrayList<TournamentValue> newtvs = new ArrayList<TournamentValue>();
				newtvs.add(value);
				v.setValues(newtvs);
			} else if (v instanceof DBSessionVariable) {
				SingleStringVarUI gui = new SingleStringVarUI(
						NegoGUIApp.negoGUIView.getFrame());
				gui.setTitle("Enter DB sessionname");
				Object result = gui.getResult();
				if (result == null)
					return;
				DBSessionValue value = new DBSessionValue(result.toString());
				ArrayList<TournamentValue> newtvs = new ArrayList<TournamentValue>();
				newtvs.add(value);
				v.setValues(newtvs);
			}
		} else if (v instanceof AgentParameterVariable) {
			ArrayList<TournamentValue> newvalues = null;
			String newvaluestr = new String("" + v.getValues()); // get old
																	// list,
																	// using
																	// ArrayList.toString.
			// remove the [ and ] that ArrayList will add
			newvaluestr = newvaluestr.substring(1, newvaluestr.length() - 1);
			double minimum = ((AgentParameterVariable) v).getAgentParam().min;
			double maximum = ((AgentParameterVariable) v).getAgentParam().max;

			// repeat asking the numbers until cancel or correct list was
			// entered.
			boolean error_occured;
			do {
				error_occured = false;
				try {
					newvaluestr = (String) new ParameterValueUI(
							NegoGUIApp.negoGUIView.getFrame(), "" + v,
							newvaluestr).getResult();
					if (newvaluestr == null)
						break;
					// System.out.println("new value="+newvaluestr);
					String[] newstrings = newvaluestr.split(",");
					newvalues = new ArrayList<TournamentValue>();
					for (int i = 0; i < newstrings.length; i++) {
						Double val = Double.valueOf(newstrings[i]);
						if (val < minimum)
							throw new IllegalArgumentException("value " + val
									+ " is smaller than minimum " + minimum);
						if (val > maximum)
							throw new IllegalArgumentException("value " + val
									+ " is larger than maximum " + maximum);
						newvalues.add(new AgentParamValue(Double
								.valueOf(newstrings[i])));
					}
					v.setValues(newvalues);
				} catch (Exception err) {
					error_occured = true;
					new Warning("your numbers are not accepted: " + err);
				}
			} while (error_occured);
		} else if (v instanceof MultipleAgentsVariable) {
			ArrayList<AgentRepItem> items = getAgentRepItems();
			ArrayList<AgentRepItem> prevSelected = new ArrayList<AgentRepItem>();

			for (TournamentValue wrappedAgent : v.getValues()) {
				AgentValue agentValue = ((AgentValue) wrappedAgent);
				prevSelected.add(agentValue.getValue());
			}

			ArrayList<AgentRepItem> newv = (ArrayList<AgentRepItem>) new RepItemVarUI<AgentRepItem>(
					NegoGUIApp.negoGUIView.getFrame(), "Select agents")
					.getResult(items, prevSelected);
			if (newv == null)
				return; // cancel pressed.
			ArrayList<TournamentValue> newtvs = new ArrayList<TournamentValue>();

			for (AgentRepItem profitem : newv) {
				newtvs.add(new AgentValue(profitem));
			}
			v.setValues(newtvs);
			
		} else
			throw new IllegalArgumentException("Unknown tournament variable "
					+ v);
	}

	private ArrayList<AgentRepItem> getAgentRepItems() {
		Repository agentrep = Repository.get_agent_repository();
		ArrayList<AgentRepItem> items = new ArrayList<AgentRepItem>();
		for (RepItem agt : agentrep.getItems()) {
			if (!(agt instanceof AgentRepItem))
				new Warning("there is a non-AgentRepItem in agent repository:"
						+ agt);
			items.add((AgentRepItem) agt);
		}
		return items;
	}

	private ArrayList<ProfileRepItem> getProfileRepItems() {
		Repository domainrep = null;
		ArrayList<ProfileRepItem> items = new ArrayList<ProfileRepItem>();
		try {
			domainrep = Repository.get_domain_repos();
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (RepItem domain : domainrep.getItems()) {
			for (ProfileRepItem profile : ((DomainRepItem) domain)
					.getProfiles()) {
				items.add(profile);
			}
		}
		return items;
	}

	/** remove selected row from table */
	void removerow() throws Exception {
		int row = checkParameterSelected("You can not remove the Profile and Agent vars.");
		tournament.getVariables().remove(row);
		dataModel.fireTableRowsDeleted(row, row);
	}

	void up() throws Exception {
		int row = checkParameterSelected("You can not move Profile and Agent vars");
		if (row == 3)
			throw new IllegalArgumentException(
					"You can not move Profile and Agent vars"); // you can not
																// move the
																// highest one
																// up.
		// swap row with row-1
		ArrayList<TournamentVariable> vars = tournament.getVariables();
		TournamentVariable tmp = vars.get(row);
		vars.set(row, vars.get(row - 1));
		vars.set(row - 1, tmp);
		dataModel.fireTableRowsUpdated(row - 1, row);
	}

	void down() throws Exception {
		int row = checkParameterSelected("You can not move Profile and Agent vars");
		ArrayList<TournamentVariable> vars = tournament.getVariables();
		if (row == vars.size() - 1)
			return; // you can not move the last one down.
		// swap row with row+1
		TournamentVariable tmp = vars.get(row);
		vars.set(row, vars.get(row + 1));
		vars.set(row + 1, tmp);
		dataModel.fireTableRowsUpdated(row, row + 1);
	}

	/**
	 * returns selected parameter row number, or throws if not. The throw error
	 * message is "Please select a Parameter to be moved."+detailerrormessage.
	 */
	int checkParameterSelected(String detailerrormessage) throws Exception {
		int row = jTable1.getSelectedRow();
		if (row <= 2 || row > tournament.getVariables().size())
			throw new IllegalArgumentException(
					"Please select a Parameter to be moved. "
							+ detailerrormessage);
		return row;
	}

	/**
	 * start negotiation. Run it in different thread, so that we can return
	 * control to AWT/Swing That is important to avoid deadlocks in case any
	 * negosession wants to open a frame.
	 */

	public void start(boolean distributed, String sessionname) throws Exception {
		if (distributed) {
			tournament = DBController.getInstance().getTournament(
					DBController.getInstance().getJobID(sessionname));
		}
		// it may have already been set if we used START instead of JOIN,
		// however, the overhead is minimal
		TournamentConfiguration.setConfiguration(tournament.getOptions());

		ProgressUI2 progressUI = new ProgressUI2();
		TournamentProgressUI2 tournamentProgressUI = new TournamentProgressUI2(
				progressUI);
		NegoGUIApp.negoGUIView.replaceTab("Tour." + tournament.TournamentNumber
				+ " Progress", this, tournamentProgressUI);

		// required for distributed, this sets the sessions to null (sessions
		// are unserializable)
		tournament.resetTournament();
		previousTournament.writeToDisk(tournament);

		// new Thread(new TournamentRunnerTwoPhaseAutction
		// (tournament,tournamentProgressUI)).start();

		if (distributed) {
			TournamentRunner runner = new TournamentRunner(
					tournamentProgressUI, distributed, sessionname);
			new Thread(runner).start();
		} else {

			TournamentRunner runner = new TournamentRunner(tournament,
					tournamentProgressUI);
			new Thread(runner).start();
		}

	}

	/*************************** CODE FOR RUNNING DEMO AND LOADING & CORRECTING EXAMPLE ********************/
	/**
	 * make sure first three rows are Profile, AgentA, AgentB Tournaments
	 * setings tab
	 * 
	 */
	private void correct_tournament(Tournament t) {
		ArrayList<TournamentVariable> vars = t.getVariables();

		ProtocolVariable protocol = new ProtocolVariable();
		if (Repository.getProtocolRepository().getItems().size() == 1) {
			try {
				protocol.addValue(new ProtocolValue(
						(ProtocolRepItem) Repository.getProtocolRepository()
								.getItems().get(0)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		fillposition(vars, Tournament.VARIABLE_PROTOCOL, protocol);
		fillposition(vars, Tournament.VARIABLE_PROFILE, new ProfileVariable());

		AgentVariable agentVar = new AgentVariable();
		agentVar.setSide("A");
		fillposition(vars, Tournament.VARIABLE_AGENT_A, agentVar);
		agentVar = new AgentVariable();
		agentVar.setSide("B");
		fillposition(vars, Tournament.VARIABLE_AGENT_B, agentVar);
		TotalSessionNumberVariable nrsessions = new TotalSessionNumberVariable();
		ArrayList<TournamentValue> newvals = new ArrayList<TournamentValue>();
		newvals.add(new TotalSessionNumberValue());
		nrsessions.setValues(newvals);
		fillposition(vars, Tournament.VARIABLE_NUMBER_OF_RUNS, nrsessions);
		fillposition(vars, Tournament.VARIABLE_TOURNAMENT_OPTIONS,
				new TournamentOptionsVariable());

		// Ignore possible dead code warning displayed here, it is has to do
		// with the
		// values of the global variables.
		BOAagentVariable decoupledAgentVarA = new BOAagentVariable();
		decoupledAgentVarA.setSide("A");
		fillposition(vars, Tournament.VARIABLE_DECOUPLED_A, decoupledAgentVarA);
		BOAagentVariable decoupledAgentVarB = new BOAagentVariable();
		decoupledAgentVarB.setSide("B");
		fillposition(vars, Tournament.VARIABLE_DECOUPLED_B, decoupledAgentVarB);

		// createFrom fields for connecting to a database
		if (distributed) {
			fillposition(vars, Tournament.VARIABLE_DB_LOCATION,
					new DBLocationVariable());
			fillposition(vars, Tournament.VARIABLE_DB_USER,
					new DBUserVariable());
			fillposition(vars, Tournament.VARIABLE_DB_PASSWORD,
					new DBPasswordVariable());
			fillposition(vars, Tournament.VARIABLE_DB_SESSIONNAME,
					new DBSessionVariable());
		}

		//---------------DEBUG CODE-------------------//
			
		fillposition(vars, 8, new MultipleAgentsVariable());
		
		//---------------END OF DEBUG CODE----------------//
		
		// vars.add(new AgentParameterVariable(new
		// AgentParam(BayesianAgent.class.getName(), "pi", 3.14, 3.15)));
	}

	/**
	 * Check that variable of type given in stub is at expected position. Or
	 * createFrom new instance of that type if there is none.
	 * 
	 * @param vars
	 *            is array of TournamentVariables.
	 * @param pos
	 *            expected position
	 */
	static void fillposition(ArrayList<TournamentVariable> vars,
			int expectedpos, TournamentVariable stub) {
		TournamentVariable var = null;
		if (expectedpos < vars.size())
			var = vars.get(expectedpos);

		// This var is not set yet
		if (var == null) {
			if (expectedpos < vars.size())
				vars.set(expectedpos, stub);
			else
				vars.add(expectedpos, stub);
			return;
		}

		if (!var.getClass().equals(stub.getClass())) {
			new Warning(
					"tournament has "
							+ stub.getClass()
							+ " variable not on expected place. Replacing it by a stub.");
			vars.set(expectedpos, stub);
			return;
		}

		// System.out.println("Read " + var);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		jScrollPane1 = new javax.swing.JScrollPane();
		jTable1 = new javax.swing.JTable();
		btnStart = new javax.swing.JButton();

		setName("Form"); // NOI18N

		org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application
				.getInstance(negotiator.gui.NegoGUIApp.class).getContext()
				.getResourceMap(TournamentUI.class);
		jPanel1.setBorder(javax.swing.BorderFactory
				.createTitledBorder(resourceMap
						.getString("jPanel1.border.title"))); // NOI18N
		jPanel1.setName("jPanel1"); // NOI18N

		jScrollPane1.setName("jScrollPane1"); // NOI18N

		jTable1.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][] { { null, null, null, null },
						{ null, null, null, null }, { null, null, null, null },
						{ null, null, null, null } }, new String[] { "Title 1",
						"Title 2", "Title 3", "Title 4" }));
		jTable1.setName("jTable1"); // NOI18N
		jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				jTable1MouseClicked(evt);
			}
		});
		jScrollPane1.setViewportView(jTable1);

		javax.swing.ActionMap actionMap = org.jdesktop.application.Application
				.getInstance(negotiator.gui.NegoGUIApp.class).getContext()
				.getActionMap(TournamentUI.class, this);
		btnStart.setAction(actionMap.get("startTournament")); // NOI18N
		btnStart.setText(resourceMap.getString("btnStart.text")); // NOI18N
		btnStart.setName("btnStart"); // NOI18N

		JButton saveButton = new JButton("Save tournament");
		saveButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saveSetup();
			}
		});
		JButton loadButton = new JButton("Load tournament");
		loadButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				loadSetup();
			}
		});

		btnStartNewDT = new javax.swing.JButton();
		btnJoinDS = new javax.swing.JButton();

		if (distributed) {
			btnStartNewDT
					.setAction(actionMap.get("startDistributedTournament")); // NOI18N
			btnStartNewDT.setText(resourceMap.getString("btnStartNewDT.text")); // NOI18N
			btnStartNewDT.setName("btnStartNewDT"); // NOI18N

			btnJoinDS.setAction(actionMap.get("joinDistributedTournament")); // NOI18N
			btnJoinDS.setText(resourceMap.getString("btnJoinDS.text")); // NOI18N
			btnJoinDS.setName("btnJoinDS"); // NOI18N
		} else {
			btnStartNewDT.setVisible(false);
			btnJoinDS.setVisible(false);
		}

		org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout
				.setHorizontalGroup(jPanel1Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(org.jdesktop.layout.GroupLayout.TRAILING,
								jPanel1Layout
										.createSequentialGroup()
										.addContainerGap()
										.add(jPanel1Layout
												.createParallelGroup(
														org.jdesktop.layout.GroupLayout.TRAILING)
												.add(org.jdesktop.layout.GroupLayout.HORIZONTAL,
														jScrollPane1,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														40, Short.MAX_VALUE)
												.add(btnStart)
												.add(btnStartNewDT)
												.add(btnJoinDS).add(saveButton)
												.add(loadButton))
										.addContainerGap()));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				org.jdesktop.layout.GroupLayout.TRAILING,
				jPanel1Layout
						.createSequentialGroup()
						.addContainerGap()
						.add(jScrollPane1,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								328, Short.MAX_VALUE).add(18, 18, 18)
						.add(btnStart).add(btnStartNewDT).add(btnJoinDS)
						.add(saveButton).add(loadButton).addContainerGap()));

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				layout.createSequentialGroup()
						.addContainerGap()
						.add(jPanel1,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				layout.createSequentialGroup()
						.addContainerGap()
						.add(jPanel1,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE).addContainerGap()));
	}// </editor-fold>//GEN-END:initComponents

	private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jTable1MouseClicked
		if (evt.getClickCount() > 1) {
			TournamentVariable tv = tournament.getVariables().get(
					jTable1.getSelectedRow());
			try {
				editVariable(tv);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Action
	public void startTournament() {
		try {
			start(false, "");
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	/**
	 * Join a distributed tournament by retrieving the tournament from the DB. A
	 * subset of the sessions of the tournament are executed, after which a new
	 * subset is requested. This process continues until the full job has been
	 * processed.
	 */
	@Action
	public void joinDistributedTournament() {
		startDTournament(false);
	}

	/**
	 * Start a distributed tournament by storing the tournament and its jobs in
	 * the database. Following, the steps are identical to joining a distributed
	 * tournament.
	 */
	@Action
	public void startDistributedTournament() {
		startDTournament(true);
	}

	/**
	 * Starts a distributed tournament.
	 * 
	 * @param storeJobs
	 *            true if startTournament, false if join
	 */
	public void startDTournament(boolean storeJobs) {

		// 1. Load the database parameters
		String url = tournament.getVariables()
				.get(Tournament.VARIABLE_DB_LOCATION).getValues().get(0)
				.toString();
		String user = tournament.getVariables()
				.get(Tournament.VARIABLE_DB_USER).getValues().get(0).toString();
		String password = tournament.getVariables()
				.get(Tournament.VARIABLE_DB_PASSWORD).getValues().get(0)
				.toString();
		String sessionname = tournament.getVariables()
				.get(Tournament.VARIABLE_DB_SESSIONNAME).getValues().get(0)
				.toString();

		// 2. Try to connect
		if (DBController.connect(url, user, password)) {
			try {
				// 3. Print a tutorial on how to configure DT
				System.out.println(DBController.getDistributedTutorial());

				// 4. Generate jobs and store them in the DB if a tournament was
				// started
				int response = 0;
				int randomTest = 0;
				if (storeJobs) {
					// 5. Check that the user does not accidentally start a new
					// session when he actually wanted to join
					if (DBController.getInstance().existsSessionName(
							sessionname)) {
						response = JOptionPane
								.showConfirmDialog(
										null,
										"This session name already exists.\n"
												+ "Are you sure you want to use the same name?\n"
												+ "Note that if the other session was still running,\n"
												+ "this will shift the priority to this session.",
										"Input", JOptionPane.YES_NO_OPTION);
					}
					if (response == 0) { // yes
						if (tournament.getOptions().get("generationMode") == 1) {
							randomTest = JOptionPane
									.showConfirmDialog(
											null,
											"The session order has been randomized by using generation mode: \"random\".\n"
													+ "In the distributed setting the run numbers are no longer usable.\n"
													+ "Do you want to continue?",
											"Input", JOptionPane.YES_NO_OPTION);

						}
						if (randomTest == 0) {
							TournamentConfiguration.setConfiguration(tournament
									.getOptions());
							DBController.getInstance().createJob(sessionname,
									tournament);
						}
					}
				}
				// the user STARTED and ACCEPTED or the user JOINED
				if (response == 0 && randomTest == 0) {
					start(true, sessionname);
				}
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Error while creating tournament.", "Tournament error",
						0);
			}
		} else {
			JOptionPane.showMessageDialog(null,
					"Could not connect to database.", "Database error", 0);
		}
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton btnStart;
	private javax.swing.JButton btnStartNewDT;
	private javax.swing.JButton btnJoinDS;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTable jTable1;

	// End of variables declaration//GEN-END:variables

	public void saveSetup() {
		JFileChooser fc = new JFileChooser();

		// Open the file picker
		int returnVal = fc.showSaveDialog(this);

		// If file selected
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String path = fc.getSelectedFile().getPath();

			Serializer<Tournament> tournamentSerializer = new Serializer<Tournament>(
					path);
			tournamentSerializer.writeToDisk(tournament);
		}
	}

	public void loadSetup() {
		JFileChooser fc = new JFileChooser();

		// Open the file picker
		int returnVal = fc.showOpenDialog(this);

		// If file selected
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String path = fc.getSelectedFile().getPath();

			Serializer<Tournament> tournamentSerializer = new Serializer<Tournament>(
					path);
			Tournament t = tournamentSerializer.readFromDisk();

			try {
				correct_tournament(t);
				tournament = t;
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,
						"Invalid or outdated tournament specification.",
						"Tournament error", 0);
			}
		}
		this.updateUI();
	}

	public void removeAction() {
		try {
			removerow();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Tournament getTournament() {
		return tournament;
	}
}