package windows;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import tournamentRunner.Brackets;
import tournamentRunner.TournamentView;


/*
*Represent a dialog to add a person to a bracket
*/
public class AddPersonWindow extends JDialog implements ActionListener {
	private TournamentView view;
	private JButton confirm, cancel;
	private JTextField name, school, seed;
	private JComboBox<String> bracketOptions;
	private JCheckBox noSchool,unseeded;
	
	public AddPersonWindow(TournamentView tournamentView) {		//Retain a refrence to view to allow method calls
		super(tournamentView, "Add new competitor");
		view = tournamentView;
		name = new JTextField("Name");
		name.setColumns(10);
		school = new JTextField("School");
		school.setColumns(10);
		seed = new JTextField("Seed");
		seed.setColumns(10);
		String[] brackets = new String[Brackets.getNum()];
		for(int i=0;i<brackets.length;i++)
			brackets[i] = Brackets.getBracket(i).getName();
		bracketOptions = new JComboBox<String>(brackets);
		confirm = new JButton("Add");
		confirm.addActionListener(this);
		confirm.setEnabled(false);
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		cancel.setEnabled(true);
		noSchool = new JCheckBox("Unaffiliated");
		noSchool.addActionListener(this);
		unseeded = new JCheckBox("Unseeded");
		unseeded.addActionListener(this);
		DocumentListener docListen = new DocumentListener(){		//Listen for changes in text fields
			public void changedUpdate(DocumentEvent e){
				enableButtons();
			}
			public void removeUpdate(DocumentEvent e){
				enableButtons();
			}
			public void insertUpdate(DocumentEvent e){
				enableButtons();
			}
			private void enableButtons(){		//If any fields are default or empty, do not allow confirm
				if(name.getText().equals("Name") || name.getText().equals("") || 
						((school.getText().equals("School") || school.getText().equals("")) && !noSchool.isSelected()) || 
						((seed.getText().equals("Seed") || school.getText().equals("")) && !noSchool.isSelected())){
					confirm.setEnabled(false);
				}
				else{
					confirm.setEnabled(true);
				}
			}
		};
		name.getDocument().addDocumentListener(docListen);
		school.getDocument().addDocumentListener(docListen);
		seed.getDocument().addDocumentListener(docListen);
		
		this.setLayout(new GridLayout(3,1));
		this.setSize(600,300);
		JPanel options = new JPanel();
		options.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridheight = 10;
		constraints.gridwidth = this.getWidth();
		constraints.fill = GridBagConstraints.BOTH;
		options.add(name);
		options.add(school);
		options.add(noSchool);
		options.add(seed);
		options.add(unseeded);
		
		JPanel bracketSelect = new JPanel();
		bracketSelect.setLayout(new GridBagLayout());
		bracketSelect.add(new JLabel("Select bracket: "));
		bracketSelect.add(bracketOptions);
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridBagLayout());
		buttons.add(confirm);
		buttons.add(cancel);
		Toolkit tlkt = Toolkit.getDefaultToolkit();
		this.add(options);
		this.add(bracketSelect);
		this.add(buttons);
		this.setModal(true);
		this.setResizable(false);
		this.setLocation((int)(tlkt.getScreenSize().getWidth()-this.getWidth())/2,(int)(tlkt.getScreenSize().getHeight()-this.getHeight())/2);
		this.setAlwaysOnTop(true);
		this.setVisible(true);
		this.pack();
	}

	public void actionPerformed(ActionEvent arg0) {
		Object source = arg0.getSource();
		if(source==confirm){		//If confirm, attempt to add a person to the chosen bracket
			dispose();
			view.addPerson(name.getText(),school.getText(), seed.getText(), (String) bracketOptions.getSelectedItem(), noSchool.isSelected(), unseeded.isSelected());
		}
		else if(source==cancel){		//If cancel, remove dialog
			dispose();
		}
		else if(source == noSchool){				//If no school checkbox clicked, recheck if all fields are filles
			if(name.getText().equals("Name") || name.getText().equals("") || ((school.getText()==null || school.getText().equals("School") || school.getText().equals("")) && !noSchool.isSelected())){
				confirm.setEnabled(false);
				cancel.setEnabled(false);
			}
			else{
				confirm.setEnabled(true);
				cancel.setEnabled(true);
			}
			if(noSchool.isSelected()){
				school.setText("Unaffiliated");
				school.setEditable(false);
			}
			else{
				school.setText("School");
				school.setEditable(true);
			}
		}
		else if(source == unseeded){
			if(name.getText().equals("Name") || name.getText().equals("") || ((school.getText()==null || school.getText().equals("Seed") || school.getText().equals("")) && !unseeded.isSelected())){
				confirm.setEnabled(false);
				cancel.setEnabled(false);
			}
			else{
				confirm.setEnabled(true);
				cancel.setEnabled(true);
			}
			if(unseeded.isSelected()){
				seed.setText("Unseeded");
				seed.setEditable(false);
			}
			else{
				seed.setText("Seed");
				seed.setEditable(true);
			}
		}
	}

}
