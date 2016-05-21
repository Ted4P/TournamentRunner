package windows;
import java.awt.event.*;
import java.awt.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import tournamentRunner.TournamentView;


public class NewTournamentWindow extends JDialog implements ActionListener{
	private TournamentView view;
	private JButton confirm,cancel;
	private JTextField name, number, size;
	public NewTournamentWindow(TournamentView view){
		super(view, "New Tournament");
		this.view = view;
		DocListener dl = new DocListener();
		name = new JTextField("Tournament Name");
		name.setColumns(15);
		number = new JTextField("Number of Brackets");
		number.setColumns(10);
		size = new JTextField("Bracket Size");
		size.setColumns(10);
		
		name.getDocument().addDocumentListener(dl);
		number.getDocument().addDocumentListener(dl);
		size.getDocument().addDocumentListener(dl);
		
		confirm = new JButton("Confirm");
		confirm.addActionListener(this);
		confirm.setEnabled(false);
		
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		cancel.setEnabled(false);
		
		this.setLayout(new GridLayout(5,1));
		this.setSize(600,300);
		
		add(name);
		add(number);
		add(size);
		add(confirm);
		add(cancel);
		Toolkit tlkt = Toolkit.getDefaultToolkit();
		this.setLocation((int)(tlkt.getScreenSize().getWidth()-this.getWidth())/2,(int)(tlkt.getScreenSize().getHeight()-this.getHeight())/2);
		this.setModal(true);
		this.setResizable(false);
		this.setAlwaysOnTop(true);
		this.setVisible(true);
		this.pack();
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() ==cancel) this.dispose();
		else{
			dispose();
			view.createNewBracket(name.getText(),number.getText(),size.getText());
		}
	}
	
	private class DocListener implements DocumentListener{
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
				cancel.setEnabled(false);
			}
			else{
				confirm.setEnabled(true);
				cancel.setEnabled(true);
			}
		}
		
	}

}
