package windows;
import java.awt.event.*;
import java.awt.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import tournamentRunner.TournamentView;

/*
*Represents a Dialog box to create a new Tournament, with Name, Size and # of brackets
*/
public class NewTournamentWindow extends JDialog implements ActionListener{ //Retain reference to view to allow method calls
	private TournamentView view;
	private JButton confirm,cancel;
	private JTextField name, number, size;
	public NewTournamentWindow(TournamentView view){
		super(view, "New Tournament");
		this.view = view;
		DocListener dl = new DocListener(); //Listen for changes to text fields
		
		GridBagConstraints cons = new GridBagConstraints();
		cons.gridheight = 10;
		cons.gridwidth = this.getWidth();
		JPanel namePanel = new JPanel();
		namePanel.setLayout(new GridBagLayout());
		name = new JTextField("Tournament Name");
		name.setColumns(15);
		name.setHorizontalAlignment(JTextField.CENTER);
		namePanel.add(name);
		
		JPanel numberAndSize = new JPanel();
		numberAndSize.setLayout(new GridBagLayout());
		number = new JTextField("Number of Brackets");
		number.setColumns(10);
		number.setHorizontalAlignment(JTextField.CENTER);
		size = new JTextField("Bracket Size");
		size.setColumns(10);
		size.setHorizontalAlignment(JTextField.CENTER);
		numberAndSize.add(number);
		numberAndSize.add(size);
		
		name.getDocument().addDocumentListener(dl);
		number.getDocument().addDocumentListener(dl);
		size.getDocument().addDocumentListener(dl);
		
		JPanel closeOptions = new JPanel();
		confirm = new JButton("Confirm");
		confirm.addActionListener(this);
		confirm.setEnabled(false);
		closeOptions.add(confirm);
		
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		cancel.setEnabled(true);
		closeOptions.add(cancel);
		
		this.setLayout(new GridLayout(3,1));
		this.setSize(400,200);
		
		add(namePanel);
		add(numberAndSize);
		add(closeOptions);
		
		Toolkit tlkt = Toolkit.getDefaultToolkit();
		this.setLocation((int)(tlkt.getScreenSize().getWidth()-this.getWidth())/2,(int)(tlkt.getScreenSize().getHeight()-this.getHeight())/2);
		this.setModal(true);
		this.setResizable(false);
		this.setAlwaysOnTop(true);
		this.setVisible(true);
		this.pack();
	}
	
	public void actionPerformed(ActionEvent e) { //If cancel, close the window, if confirm, attempt to create new bracket
		if(e.getSource() == cancel) this.dispose();	
		else{
			dispose();
			view.createNewBracket(name.getText(),number.getText(),size.getText());
		}
	}
	
	private class DocListener implements DocumentListener{ //If any fields are default text or blank, do not allow confirm
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
			if(name.getText().equals("Tournament Name") || name.getText().equals("") || 
					number.getText().equals("Number of Brackets") || number.getText().equals("") ||
						size.getText().equals("Bracket Size") || size.getText().equals("")){
				confirm.setEnabled(false);
			}
			else{
				confirm.setEnabled(true);
			}
		}
		
	}

}
