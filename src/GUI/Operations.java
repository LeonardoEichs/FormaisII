package GUI;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.util.HashMap;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;

import ContextFreeGrammar.Operator;
import ContextFreeGrammar.ContextFreeGrammar;

public class Operations extends JFrame {

	// Auto-generated UID
	private static final long serialVersionUID = -1372510623291211881L;
	
	private JComboBox<ContextFreeGrammar> cbOpCFG;
	private MainFrame mainFrame = null;
	
	/**
	 * Exit back to main frame
	 */
	public void exit() {
		mainFrame.setVisible(true);
		this.dispose();
	}

	/**
	 * Create the application.
	 */
	public Operations(MainFrame mainFrame) {
		try {
			this.mainFrame = mainFrame;
			initialize();
			mainFrame.setVisible(true);
			this.setVisible(true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		this.setTitle("CFG Operations");
		this.setResizable(false);
		this.setBounds(100, 200, 650, 190);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel operationsFramePanel = new JPanel();
		this.getContentPane().add(operationsFramePanel, BorderLayout.CENTER);
		
		JSpinner spinnerMaxSteps = new JSpinner();
		spinnerMaxSteps.setValue(10);
		
		// JLabels:
		
		JLabel lbOpSelectCFG = new JLabel("Select CFG:");
		JLabel lbOpSelectOp = new JLabel("Select Operation:");
		JLabel lbOpMaxSteps = new JLabel("Set Max Steps:");
		
		// JComboBoxes:
		cbOpCFG = new JComboBox<ContextFreeGrammar>();
		
		JComboBox<String> cbOpOperations = new JComboBox<String>();
		cbOpOperations.addItem("Factor");
		cbOpOperations.addItem("Eliminate Left Recursion");
		cbOpOperations.addItem("Eliminate Cycles");
		cbOpOperations.addItem("Eliminate Epsilons");
		cbOpOperations.addItem("Eliminate Useless Symbols");
		cbOpOperations.addItem("Eliminate Infertile");
		cbOpOperations.addItem("Eliminate Unreachables");
		cbOpOperations.addItem("Proper CFG");


		cbOpOperations.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	String selected = String.valueOf(cbOpOperations.getSelectedItem());
		    	if (selected.equals("Factor")) {
		    		spinnerMaxSteps.setEnabled(true);
		    		lbOpMaxSteps.setEnabled(true);
		    	} 
		    	else {
		    		spinnerMaxSteps.setEnabled(false);
		    		lbOpMaxSteps.setEnabled(false);
		    	}
		    }
		});
		
		// JButtons:
		
		JButton btnOpCancel = new JButton("Cancel");
		btnOpCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Operations.this.exit();
			}
		});
		
		JButton btnOpSave = new JButton("Save");
		btnOpSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ContextFreeGrammar cfg = (ContextFreeGrammar) cbOpCFG.getSelectedItem();
				String operation = String.valueOf(cbOpOperations.getSelectedItem());
				
				try {
					spinnerMaxSteps.commitEdit();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
				int maxSteps = (Integer) spinnerMaxSteps.getValue();
				
				if (saveOperation(operation, cfg, maxSteps)) {
					Operations.this.exit();
				}
			}
		});
		
//		JButton btnOpView = new JButton("View");
		
		// Close Window action:
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Operations.this.exit();
			}
		});
		
		
		// Layout definitions:
		
		GroupLayout gl_operationsFramePanel = new GroupLayout(operationsFramePanel);
		gl_operationsFramePanel.setHorizontalGroup(
			gl_operationsFramePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_operationsFramePanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_operationsFramePanel.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_operationsFramePanel.createSequentialGroup()
							.addGroup(gl_operationsFramePanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_operationsFramePanel.createSequentialGroup()
									.addComponent(cbOpCFG, 0, 297, Short.MAX_VALUE)
									.addGap(12))
								.addGroup(gl_operationsFramePanel.createSequentialGroup()
									.addGap(6)
									.addComponent(lbOpSelectCFG)
									.addGap(41)))
							.addGroup(gl_operationsFramePanel.createParallelGroup(Alignment.LEADING)
								.addComponent(lbOpSelectOp)
								.addComponent(cbOpOperations, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_operationsFramePanel.createParallelGroup(Alignment.LEADING)
								.addComponent(lbOpMaxSteps)
								.addComponent(spinnerMaxSteps, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_operationsFramePanel.createSequentialGroup()
							.addComponent(btnOpCancel, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnOpSave, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_operationsFramePanel.setVerticalGroup(
			gl_operationsFramePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_operationsFramePanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_operationsFramePanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lbOpSelectCFG)
						.addComponent(lbOpSelectOp)
						.addComponent(lbOpMaxSteps))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_operationsFramePanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(cbOpCFG, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
						.addComponent(cbOpOperations, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
						.addComponent(spinnerMaxSteps, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
					.addGroup(gl_operationsFramePanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnOpCancel, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnOpSave, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		operationsFramePanel.setLayout(gl_operationsFramePanel);
		this.populateComboBoxes();
	}
	

	// Save new language based on selections
	private boolean saveOperation(String operation, ContextFreeGrammar cfg, int maxSteps) {
		if (cfg == null) {
			return false;
		}
		String warning = null;
		Operator op = new Operator(cfg);
		if (operation.equals("Factor")) {
			if (op.isFactored()) {
				warning = cfg.getId() + " is factored.\n"
						+ "Nothing will be done."; 
			} else if (op.hasLeftRecursion()) {
				warning = cfg.getId() + " has left recursion.\n"
						+ "Please, eliminate the left recursion first "
						+ "and then try factoring the grammar.";
			} else {
				for (ContextFreeGrammar newCFG : op.factorGrammar(maxSteps)) {
					mainFrame.addToPanel(newCFG);
				}
			}
			
		} 
		else if (operation.equals("Eliminate Left Recursion")) {
			if (!op.hasLeftRecursion()) {
				warning = cfg.getId() + " does not have left recursion.\n"
						+ "Nothing will be done."; 
			} else {
				ContextFreeGrammar cfgLeftRec = op.eliminateLeftRecursion();
				cfgLeftRec.setId(cfg.getId() + " [No Left Rec.]");
				mainFrame.addToPanel(cfgLeftRec);
			}
			
		}
		else if (operation.equals("Eliminate Cycles")) {
			ContextFreeGrammar cfgNew = cfg.removeSimpleProductions();
			cfgNew.setId(cfg.getId() + " [No Cyc.]");
			mainFrame.addToPanel(cfgNew);
		}
		else if (operation.equals("Eliminate Epsilons")) {
			ContextFreeGrammar cfgNew = cfg.removeEpsilon();
			cfgNew.setId(cfg.getId() + " [&-free]");
			mainFrame.addToPanel(cfgNew);			
		}
		else if (operation.equals("Eliminate Useless Symbols")) {
			ContextFreeGrammar cfgNew = cfg.removeInfertile().removeUnreachable();
			cfgNew.setId(cfg.getId() + " [No Useless Symbols]");
			mainFrame.addToPanel(cfgNew);
		}
		else if (operation.equals("Eliminate Infertile")) {
			ContextFreeGrammar cfgNew = cfg.removeInfertile();
			cfgNew.setId(cfg.getId() + " [No Infert.]");
			mainFrame.addToPanel(cfgNew);
			
		} 
		else if (operation.equals("Eliminate Unreachable")) {
			ContextFreeGrammar cfgNew = cfg.removeUnreachable();
			cfgNew.setId(cfg.getId() + " [No Unrea.]");
			mainFrame.addToPanel(cfgNew);
		} 
		else if (operation.equals("Proper CFG")) {
			ContextFreeGrammar epsilon = cfg.removeEpsilon();
			System.out.println("&-free Production -----------");
			System.out.println(epsilon.getDefinition());
			epsilon.setId(cfg.getId() + " [&-free]");
			mainFrame.addToPanel(epsilon);
			ContextFreeGrammar cycles = epsilon.removeSimpleProductions();
			System.out.println("Cycles-free Production -----------");
			System.out.println(cycles.getDefinition());
			cycles.setId(cfg.getId() + " [No Cyc.]");
			mainFrame.addToPanel(cycles);
			ContextFreeGrammar infertile = cycles.removeInfertile();
			System.out.println("Infertile-free Production -----------");
			System.out.println(infertile.getDefinition());
			infertile.setId(cfg.getId() + " [No Infert.]");
			mainFrame.addToPanel(infertile);
			ContextFreeGrammar unreachable = infertile.removeUnreachable();
			System.out.println("Unreachable-free Production -----------");
			System.out.println(unreachable.getDefinition());
			unreachable.setId(cfg.getId() + " [No Unrea.]");
			mainFrame.addToPanel(unreachable);
		}
		else {
			return false;
		}
		
		if (warning != null) {
			JOptionPane.showMessageDialog(this, warning);
		}
		
		return true;
		
	}

	// Populate combo boxes with regular languages from the list
	private void populateComboBoxes() {
		HashMap<String, ContextFreeGrammar> languages = mainFrame.getLanguages();
		for (String id : languages.keySet()) {
			cbOpCFG.addItem(languages.get(id));
		}
	}
}
