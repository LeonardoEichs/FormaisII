package GUI;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Set;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import ContextFreeGrammar.ContextFreeGrammar;
import ContextFreeGrammar.Operator;

public class Properties extends JFrame {

	// Auto-generated UID:
	private static final long serialVersionUID = -9024968109058945031L;
	
	private MainFrame mainFrame;
	private JComboBox<ContextFreeGrammar> cbPropertiesCFG;
	private JPanel contentPane;
	private JTextField textField;

	
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
	public Properties(MainFrame mainFrame) {
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
		setTitle("Context Free Grammar Properties");
		this.setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 200, 750, 190);
		contentPane = new JPanel();

		this.getContentPane().setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel propertiesFramePanel = new JPanel();
		
		JLabel lblGrammarSelection = new JLabel("Select the Grammar:");
		JLabel lblSetentialForm = new JLabel("Enter Setential Form:");
		JLabel lblPropertySelection = new JLabel("Select the Verification:");
		
		
		cbPropertiesCFG = new JComboBox<ContextFreeGrammar>();
		HashMap<String, ContextFreeGrammar> languages = mainFrame.getLanguages();
		for (String id : languages.keySet()) {
			cbPropertiesCFG.addItem(languages.get(id));
		}
		
		JComboBox<String> cbPropertiesProp = new JComboBox<String>();
		cbPropertiesProp.addItem("First of:");
		cbPropertiesProp.addItem("First Non-Terminal of:");
		cbPropertiesProp.addItem("Follow of:");
		cbPropertiesProp.addItem("Has Left Recursion?");
		cbPropertiesProp.addItem("Is Factored?");
		cbPropertiesProp.addItem("Is Empty?");
		cbPropertiesProp.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	String selected = String.valueOf(cbPropertiesProp.getSelectedItem());
		    	if (selected.equals("Has Left Recursion?") || selected.equals("Is Factored?") || selected.equals("Is Empty?")) {
		    		textField.setEnabled(false);
		    		lblSetentialForm.setEnabled(false);
		    	} else {
		    		textField.setEnabled(true);
		    		lblSetentialForm.setEnabled(true);
		    	}
		    }
		});
		
		JButton btnVerify = new JButton("Verify");
		btnVerify.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ContextFreeGrammar cfg = (ContextFreeGrammar) cbPropertiesCFG.getSelectedItem();
				Operator op = new Operator(cfg);
				String property = String.valueOf(cbPropertiesProp.getSelectedItem());
				if (cfg == null) { 
					return;
				}
				String response = "";
				if (property.equals("Has Left Recursion?")) {
					if (op.hasLeftRecursion()) {
						response += cfg.getId() + " does have left recursion.\n"; 
					} else {
						response += cfg.getId() + " does not have left recursion.\n";
					}
				}
				else if (property.equals("Is Factored?")) {
					if (op.isFactored()) {
						response += cfg.getId() + " is factored.\n"; 
					} else {
						response += cfg.getId() + " is not factored.\n";
					}
				}
				else if (property.equals("First of:")) {
					String input = textField.getText();
					response += "First(" + input + ") in " + cfg.getId() + " is:\n";
					Set<String> set = cfg.getFirst(input);
					if (set == null) {
						response = "There are symbols that do not belong to Vn U Vt!";
					} else {
						for (String first : set) {
							response += first + "\n";
						}
					}
				}
				else if (property.equals("First Non-Terminal of:")) {
					String input = textField.getText();
					response += "FirstNT(" + input + ") in " + cfg.getId() + " is:\n";
					Set<String> set = cfg.getFirstNT(input);
					if (set == null) {
						response = "There are symbols that do not belong to Vn U Vt!";
					} else {
						for (String first : set) {
							response += first + "\n";
						}
					}
				}
				else if (property.equals("Follow of:")) {
					String input = textField.getText();
					response += "Follow(" + input + ") in " + cfg.getId() + " is:\n";
					Set<String> set = cfg.getFollow(input);
					if (set == null) {
						response = "There are symbols that do not belong to Vn U Vt!";
					} else {
						for (String follow : set) {
							response += follow+ "\n";
						}
					}
				}
				else if (property.equals("Is Empty?")) {
					if (cfg.isEmptyGrammar()) {
						response += cfg.getId() + " is empty.\n"; 
					} else {
						response += cfg.getId() + " is not empty.\n";
					}
				}
				JOptionPane.showMessageDialog(Properties.this, response);
			}
		});	
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Properties.this.exit();
			}
		});
		
		textField = new JTextField();
		textField.setColumns(10);
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(propertiesFramePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblGrammarSelection, GroupLayout.PREFERRED_SIZE, 221, GroupLayout.PREFERRED_SIZE)
									.addGap(6))
								.addComponent(cbPropertiesCFG, 0, 274, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
										.addComponent(cbPropertiesProp, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblPropertySelection, GroupLayout.PREFERRED_SIZE, 221, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
										.addComponent(textField, GroupLayout.PREFERRED_SIZE, 207, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblSetentialForm)))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(btnCancel, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnVerify, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)))
							.addGap(12)))
					.addGap(0))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(propertiesFramePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(24))
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblGrammarSelection)
							.addComponent(lblSetentialForm)
							.addComponent(lblPropertySelection)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(cbPropertiesCFG, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
						.addComponent(cbPropertiesProp, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnVerify)
						.addComponent(btnCancel))
					.addContainerGap())
		);
		contentPane.setLayout(gl_contentPane);
		
	}
}
