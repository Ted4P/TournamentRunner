import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class MatchEditWindow extends JDialog implements ActionListener{
	private Bracket bracket;
	private String[] matchInfo;
	private JButton save, cancel, close, edit;
	private JTextField name1, school1, name2, school2, notes;
	private JComboBox<String> winnerEdit;
	private JPanel comboBox, options;
	private GridBagConstraints con;
	private TournamentView view;
	public MatchEditWindow(TournamentView view, Bracket bracket, String[] matchInfo){
		super(view, "Match Info");
		this.view = view;
		this.bracket = bracket;
		this.matchInfo = matchInfo;
		name1 = new JTextField(matchInfo[0]);
		name1.setColumns(10);
		school1 = new JTextField(matchInfo[1]);
		school1.setColumns(10);
		name2 = new JTextField(matchInfo[2]);
		name2.setColumns(10);
		school2 = new JTextField(matchInfo[3]);
		school2.setColumns(10);
		notes = new JTextField(matchInfo[6]);
		name1.setEditable(false);
		name2.setEditable(false);
		school1.setEditable(false);
		school2.setEditable(false);
		notes.setEditable(false);
		winnerEdit = new JComboBox(new String[]{"Unplayed",name1.getText(),name2.getText()});
		JPanel comps = new JPanel();
		comps.setLayout(new GridBagLayout());
		GridBagConstraints con = new GridBagConstraints();
		con.gridheight = 10;
		con.gridwidth = this.getWidth();
		con.fill = GridBagConstraints.BOTH;
		comps.add(name1);
		comps.add(new JLabel(" vs "));
		comps.add(name2);
		JPanel schools = new JPanel();
		schools.setLayout(new GridBagLayout());
		schools.add(school1);
		schools.add(new JLabel("  "));
		schools.add(school2);
		options = new JPanel();
		options.setLayout(new GridBagLayout());
		save = new JButton("Save");
		save.addActionListener(this);
		save.setEnabled(false);
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		close = new JButton("Close");
		close.addActionListener(this);
		edit = new JButton("Edit");
		edit.addActionListener(this);
		options.add(edit);
		options.add(close);
		comboBox = new JPanel();
		comboBox.setLayout(new GridBagLayout());
		setLayout(new GridLayout(5,1));
		add(comboBox);
		add(comps);
		add(schools);
		add(notes);
		add(options);
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
				int index = winnerEdit.getSelectedIndex();
				winnerEdit.removeAllItems();
				winnerEdit.addItem("Unplayed");
				if(name1.equals(name2) || name1.getText().equals("") || name1.getText().equals("TBD") || name1.getText().equals(Match.DEFAULT_WINNER_NAME) || name2.getText().equals("") || name2.getText().equals("TBD") || name2.getText().equals(Match.DEFAULT_WINNER_NAME)){
					save.setEnabled(false);
			}
				else{
					winnerEdit.addItem(name1.getText());
					winnerEdit.addItem(name2.getText());
					winnerEdit.setSelectedIndex(index);
					save.setEnabled(true);
					repaint();
				}
			}
		};
		name1.getDocument().addDocumentListener(docListen);
		name2.getDocument().addDocumentListener(docListen);
		name1.setText(name1.getText());
		Toolkit tlkt = Toolkit.getDefaultToolkit();
		setModal(true);
		setResizable(true);
		setSize(400,200);
		setLocation((int)(tlkt.getScreenSize().getWidth()-getWidth())/2,(int)(tlkt.getScreenSize().getHeight()-getHeight())/2);
		setAlwaysOnTop(true);
		setVisible(true);
	}
	
	private void viewMode(){
		Border emptyBorder = BorderFactory.createEmptyBorder();
		name1.setEditable(false);
		name1.setBorder(emptyBorder);
		name2.setEditable(false);
		name2.setBorder(emptyBorder);
		school1.setEditable(false);
		school1.setBorder(emptyBorder);
		school2.setEditable(false);
		school2.setBorder(emptyBorder);
		notes.setEditable(false);
		notes.setBorder(emptyBorder);
		comboBox.removeAll();
		comboBox.add(new JLabel("Winner: " + winnerEdit.getSelectedItem()));
		options.removeAll();
		options.add(edit);
		options.add(close);
		repaint();
	}
	
	private void editMode(){
		if(Integer.parseInt(matchInfo[7]) > Brackets.getSize()/2){
			Border drawBorder = BorderFactory.createLineBorder(Color.black, 1);
			name1.setEditable(true);
			name1.setBorder(drawBorder);
			name2.setEditable(true);
			name2.setBorder(drawBorder);
			school1.setEditable(true);
			school1.setBorder(drawBorder);
			school2.setEditable(true);
			school2.setBorder(drawBorder);
			notes.setBorder(drawBorder);
		}
		notes.setEditable(true);
		comboBox.removeAll();
		comboBox.add(winnerEdit);
		options.removeAll();
		options.add(save);
		options.add(cancel);
		repaint();
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() == save){
			Person toBePromoted;
			if(winnerEdit.getSelectedItem().equals(name1.getText()))
				toBePromoted = new Person(name1.getText(), school1.getText());
			else if(winnerEdit.getSelectedItem().equals(name2.getText()))
				toBePromoted = new Person(name2.getText(), school2.getText());
			else
				toBePromoted = new Person(Match.DEFAULT_WINNER_NAME,Match.DEFAULT_WINNER_SCHOOL);
			view.promotePerson(toBePromoted,Brackets.indexOf(bracket),notes.getText());
			viewMode();
		}
		if(arg0.getSource() == cancel){
			viewMode();
		}
		if(arg0.getSource() == close){
			dispose();
		}
		if(arg0.getSource() == edit){
			editMode();
		}
	}

}
