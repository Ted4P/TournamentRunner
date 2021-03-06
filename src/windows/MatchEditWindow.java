package windows;
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
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import tournamentRunner.Bracket;
import tournamentRunner.Brackets;
import tournamentRunner.Match;
import tournamentRunner.Person;
import tournamentRunner.TournamentView;
/*
 * Represents a window for editing the match details, including the winner and the contestents
 * 5.30.16
 * Ben Kelly and Ted Pyne
 */
public class MatchEditWindow extends JDialog implements ActionListener{
	private Bracket bracket;
	private JButton save, cancel, close, edit;
	private JTextField name1, school1, name2, school2, notes;
	private JComboBox<String> winnerEdit;
	private JPanel comboBox, options;
	private TournamentView view;
	private int match;
	private JComponent boxCompo;
	private boolean isFinal;
	//Creates a new editing window corresponding to the given bracket with the given matchInfo and num.
	//Also given whether the represented match is a match is in the first round
	public MatchEditWindow(TournamentView view, Bracket bracket, String[] matchInfo, int match, boolean isFinal){
		super(view, "Match Info");
		this.view = view;
		this.bracket = bracket;
		this.match = match;
		this.isFinal = isFinal;
		name1 = new JTextField(matchInfo[0]);
		name1.setColumns(10);
		name1.setHorizontalAlignment(JTextField.CENTER);
		school1 = new JTextField(matchInfo[1]);
		school1.setColumns(10);
		school1.setHorizontalAlignment(JTextField.CENTER);
		name2 = new JTextField(matchInfo[2]);
		name2.setColumns(10);
		name2.setHorizontalAlignment(JTextField.CENTER);
		school2 = new JTextField(matchInfo[3]);
		school2.setColumns(10);
		school2.setHorizontalAlignment(JTextField.CENTER);
		notes = new JTextField(matchInfo[6]);
		String player1 = name1.getText() + "(" + school1.getText() + ")";
		String player2 = name2.getText() + "(" + school2.getText() + ")";
		String winner = matchInfo[4] + "(" + matchInfo[5] + ")";
		if(name1.getText().equals("BYE") && !name2.getText().equals(Match.DEFAULT_BLANK_NAME))
			winner = player2;
		winnerEdit = new JComboBox<String>();
		winnerEdit = new JComboBox<String>(new String[]{"Unplayed",player1,player2});
		if(winnerEdit.getItemAt(1).equals(winner))
			winnerEdit.setSelectedIndex(1);
		else if(winnerEdit.getItemAt(2).equals(winner))
			winnerEdit.setSelectedIndex(2);
		else
			winnerEdit.setSelectedIndex(0);
		winnerEdit.setEnabled(false);
		JPanel labels = new JPanel();
		labels.setLayout(new GridBagLayout());
		labels.add(new JLabel("Player A          vs          Player B"));
		JPanel comps = new JPanel();
		comps.setLayout(new GridBagLayout());
		GridBagConstraints con = new GridBagConstraints();
		con.gridheight = 5;
		con.gridwidth = this.getWidth();
		con.fill = GridBagConstraints.BOTH;
		comps.add(name1);
		comps.add(name2);
		JPanel schools = new JPanel();
		schools.setLayout(new GridBagLayout());
		schools.add(school1);
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
		comboBox.add(new JLabel("Winner: "));
		boxCompo = new JLabel("" + winnerEdit.getSelectedItem());
		comboBox.add(boxCompo);
		setLayout(new GridLayout(6,1));
		add(comboBox);
		add(labels);
		add(comps);
		add(schools);
		add(notes);
		add(options);
		DocumentListener docListen = new DocumentListener(){
			public void changedUpdate(DocumentEvent e){
				updateWhileEditing();
			}
			public void removeUpdate(DocumentEvent e){
				updateWhileEditing();
			}
			public void insertUpdate(DocumentEvent e){
				updateWhileEditing();
			}
		};
		name1.getDocument().addDocumentListener(docListen);
		name2.getDocument().addDocumentListener(docListen);
		school1.getDocument().addDocumentListener(docListen);
		school2.getDocument().addDocumentListener(docListen);
		notes.getDocument().addDocumentListener(docListen);
		viewMode();
		Toolkit tlkt = Toolkit.getDefaultToolkit();
		setModal(true);
		setResizable(true);
		setSize(400,200);
		setLocation((int)(tlkt.getScreenSize().getWidth()-getWidth())/2,(int)(tlkt.getScreenSize().getHeight()-getHeight())/2);
		setAlwaysOnTop(true);
		setVisible(true);
	}
	//Sets this window to viewing mode, and no fields can be changed
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
		winnerEdit.setEnabled(false);
		options.removeAll();
		options.add(edit);
		options.add(close);
		comboBox.remove(boxCompo);
		boxCompo = new JLabel("" + winnerEdit.getSelectedItem());
		comboBox.add(boxCompo);
		comboBox.repaint();
		repaint();
		setSize(getWidth()+1,getHeight());
		repaint();
	}
	//Sets this window to edit mode, and some field, depending on whether the match is in the first
	//round can be edited.
	private void editMode(){
		Border drawBorder = BorderFactory.createLineBorder(Color.black, 1);
		if(isFinal){
			if(!name1.getText().equals("BYE")){
				name1.setEditable(true);
				name1.setBorder(drawBorder);
				school1.setEditable(true);
				school1.setBorder(drawBorder);
			}
			if(!name2.getText().equals("BYE")){
				name2.setEditable(true);
				name2.setBorder(drawBorder);
				school2.setEditable(true);
				school2.setBorder(drawBorder);
			}
		}
		notes.setEditable(true);
		winnerEdit.setEnabled(true);
		options.removeAll();
		options.add(save);
		options.add(cancel);
		if(!name1.getText().equals("BYE") && !name2.getText().equals("BYE")){
			comboBox.remove(boxCompo);
			boxCompo = winnerEdit;
			comboBox.add(boxCompo);
		}
		notes.setBorder(drawBorder);
		setSize(getWidth()-1,getHeight());
		updateWhileEditing();
	}
	//Adjusts what buttons are enabled based on what people enter in the fields during
	//edit mode
	public void updateWhileEditing(){
		int index = winnerEdit.getSelectedIndex();
		winnerEdit.removeAllItems();
		winnerEdit.addItem("Unplayed");
		if((name1.getText()+school1.getText()).equals(name2.getText()+school2.getText()) ||
				name1.getText().equals("") || name1.getText().equals("TBD") || name1.getText().equals(Match.DEFAULT_WINNER_NAME) ||
				name2.getText().equals("") || name2.getText().equals("TBD") || name2.getText().equals(Match.DEFAULT_WINNER_NAME) ||
				school1.getText().equals("") || school1.getText().equals("TBD") || school1.getText().equals(Match.DEFAULT_WINNER_SCHOOL) ||
				school2.getText().equals("") || school2.getText().equals("TBD") || school2.getText().equals(Match.DEFAULT_WINNER_SCHOOL))
			save.setEnabled(false);
		else{
			winnerEdit.addItem(name1.getText()+"("+school1.getText()+")");
			winnerEdit.addItem(name2.getText()+"("+school2.getText()+")");
			winnerEdit.setSelectedIndex(index);
			save.setEnabled(true);
			repaint();
		}
		if(notes.getText().equals(""))
			save.setEnabled(false);
		comboBox.repaint();
	}
	//Responds to action events in this window and resolves them based on their source
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() == save){
			Person p1 = new Person(name1.getText(),school1.getText());
			Person p2 = new Person(name2.getText(), school2.getText());
			Person toBePromoted;
			String winner = (String)winnerEdit.getSelectedItem();
			if(name1.getText().equals("BYE")){
				winner = p2.toString();
				winnerEdit.setSelectedIndex(2);
			}
			if(name2.getText().equals("BYE")){
				winner = p1.toString();
				winnerEdit.setSelectedIndex(1);
			}
			if(winner.equals(p1.toString()))
				toBePromoted = p1;
			else if(winner.equals(p2.toString()))
				toBePromoted = p2;
			else
				toBePromoted = new Person(Match.DEFAULT_WINNER_NAME,Match.DEFAULT_WINNER_SCHOOL);
			winnerEdit.setEnabled(false);
			view.setMatch(p1,p2,toBePromoted,notes.getText(),Brackets.indexOf(bracket),match);
			view.promotePerson(toBePromoted,Brackets.indexOf(bracket),notes.getText(),match);
			viewMode();
		}
		if(arg0.getSource() == cancel){
			winnerEdit.setEnabled(false);
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
