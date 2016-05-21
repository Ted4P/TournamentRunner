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

public class AddPersonWindow extends JDialog implements ActionListener {
	private TournamentView view;
	private JButton confirm, cancel;
	private JTextField name, school;
	private JComboBox<String> bracketOptions;
	private JCheckBox noSchool;
	
	public AddPersonWindow(TournamentView tournamentView) {
		super(tournamentView, "Add new competitor");
		view = tournamentView;
		name = new JTextField("Name");
		name.setColumns(10);
		school = new JTextField("School");
		school.setColumns(10);
		String[] brackets = new String[Brackets.getNum()];
		for(int i=0;i<brackets.length;i++)
			brackets[i] = Brackets.getBracket(i).getName();
		bracketOptions = new JComboBox<String>(brackets);
		confirm = new JButton("Add");
		confirm.addActionListener(this);
		confirm.setEnabled(false);
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		cancel.setEnabled(false);
		noSchool = new JCheckBox("Unaffiliated");
		noSchool.addActionListener(this);
		DocumentListener docListen = new DocumentListener(){
			public void changedUpdate(DocumentEvent e){
				enableButtons();
			}
			public void removeUpdate(DocumentEvent e){
				enableButtons();
			}
			public void insertUpdate(DocumentEvent e){
				enableButtons();
			}
			private void enableButtons(){
				if(name.getText().equals("Name") || name.getText().equals("") || ((school.getText().equals("School") || school.getText().equals("")) && !noSchool.isSelected())){
					confirm.setEnabled(false);
					cancel.setEnabled(false);
				}
				else{
					confirm.setEnabled(true);
					cancel.setEnabled(true);
				}
			}
		};
		name.getDocument().addDocumentListener(docListen);
		school.getDocument().addDocumentListener(docListen);
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
		if(source==confirm){
			dispose();
			view.addPerson(name.getText(),school.getText(), (String) bracketOptions.getSelectedItem(), noSchool.isSelected());
		}
		else if(source==cancel){
			dispose();
		}
		else if(source == noSchool){
			if(name.getText().equals("Name") || name.getText().equals("") || ((school.getText().equals("School") || school.getText().equals("")) && !noSchool.isSelected())){
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
	}

}
