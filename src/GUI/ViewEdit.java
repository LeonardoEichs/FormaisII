package GUI;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

import ContextFreeGrammar.ContextFreeGrammar;

public class ViewEdit extends JFrame{
	
	// Auto-generated UID
	private static final long serialVersionUID = -3107653144045018225L;
	
	private MainFrame mainFrame;
	private JTextArea txtaViewEdit;
	
	private ContextFreeGrammar grammar = null;
	


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
	public ViewEdit(MainFrame f, ContextFreeGrammar g) {
		setTitle("View and Edit Grammar");
		try {
			this.mainFrame = f;
			this.grammar = g;
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
		if (this.grammar == null ) {
			this.setTitle("View and Edit Context Free Grammar");
		} else {
			this.setTitle("View and Edit - " + this.grammar.getId());
		}
		this.setResizable(false);
		this.setBounds(100, 100, 500, 500);
		this.setMinimumSize(new Dimension(475, 400));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// JButtons:
		
		JButton btnViewEditSave = new JButton("Save");
		btnViewEditSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String input = txtaViewEdit.getText(); // Gets text from pane
				ContextFreeGrammar l; // Gets RL object
				l = ContextFreeGrammar.isValidCFG(input);
				if(l == null ) { // If type is not valid
					JOptionPane.showMessageDialog(ViewEdit.this, "Invalid input!");
					return;
				}
					
				l.setId(grammar.toString());
				
				int answer = JOptionPane.showConfirmDialog(
						ViewEdit.this,
						"Replace '" + grammar.toString()+ "' by this new " + grammar + "?",
						"Overwrite?",
						JOptionPane.YES_NO_OPTION
				);
				if (answer != JOptionPane.YES_OPTION) {
					return;
				}
				// add CFG to Main Panel
				ViewEdit.this.mainFrame.addToPanel(l);
				ViewEdit.this.exit();
			}
		});
		
		JButton btnViewEditCancel = new JButton("Cancel");
		btnViewEditCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ViewEdit.this.exit();
			}
		});
		btnViewEditCancel.setVerticalAlignment(SwingConstants.BOTTOM);
		btnViewEditCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ViewEdit.this.exit();
			}
		});
		
		// Close Window action:

		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				ViewEdit.this.exit();
			}
		});
		
		JLabel lblViewEdit = new JLabel("Edit the Context Free Grammar below:");
		
		JScrollPane sclPaneViewEdit = new JScrollPane();

		// Layout definitions:
		
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap(242, Short.MAX_VALUE)
					.addComponent(btnViewEditCancel, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnViewEditSave, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(sclPaneViewEdit, GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
					.addContainerGap())
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblViewEdit, GroupLayout.PREFERRED_SIZE, 474, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(16)
					.addComponent(lblViewEdit)
					.addGap(12)
					.addComponent(sclPaneViewEdit, GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE)
					.addGap(12)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnViewEditSave, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnViewEditCancel, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		
		txtaViewEdit = new JTextArea();
		txtaViewEdit.setText(grammar.getDefinition());
		sclPaneViewEdit.setViewportView(txtaViewEdit);
		getContentPane().setLayout(groupLayout);
	}
	
}