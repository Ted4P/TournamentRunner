import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class TournamentView extends JFrame implements Observer, ActionListener, KeyListener{
	private static final String 
	ERROR_NAME_NOT_FOUND = "Error: No bracket with specified name found", 
	ERROR_NO_BRACKET_SPECIFIED = "Error: No bracket specified";

	public static final int MATCH_WIDTH = 15, MATCH_HEIGHT = 6;

	TournamentModel model;
	private final JMenuItem newBracket, fromFile, save, saveAs, add, changeName;
	private JTextField name2, school2;
	private JComboBox<String> bracketOptions;
	private JButton confirm, cancel;
	private String currPathway;
	private JTabbedPane brackets;
	private JDialog dialog;
	private JCheckBox noSchool;
	private BracketPanel currPanel;


	public TournamentView(){//Start popup with
		currPathway = null; 
		Container pane = getContentPane();
		JMenuBar bar = new JMenuBar();
		JMenu file = new JMenu("File");
		newBracket = new JMenuItem("New Bracket");
		newBracket.addActionListener(this);
		file.add(newBracket);
		fromFile = new JMenuItem("Open");
		fromFile.addActionListener(this);
		file.add(fromFile);
		save = new JMenuItem("Save");
		save.addActionListener(this);
		file.add(save);
		saveAs = new JMenuItem("Save As");
		saveAs.addActionListener(this);
		file.add(saveAs);
		JMenu edit = new JMenu("Edit");
		add = new JMenuItem("Add Competitor");
		add.addActionListener(this);
		edit.add(add);
		changeName = new JMenuItem("Change Bracket Name");
		changeName.addActionListener(this);
		edit.add(changeName);
		bar.add(file);
		bar.add(edit);
		update(null,null);
		brackets = new JTabbedPane();
		brackets.setTabPlacement(JTabbedPane.LEFT);
		brackets.addKeyListener(this);
		pane.add(brackets);
		super.setJMenuBar(bar);
		setTitle("Tournament Runner");
		setSize(1600,800);
		setVisible(true);
		super.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void createNewBracket(){
		/*
		 * Startup prompt for number and size of brackets
		 */
		String currPathway = null;
		String numBrackets = JOptionPane.showInputDialog(this,
				"Enter number of brackets", null);
		if(numBrackets!=null){
			try{
				Integer intBracketNum = Integer.parseInt(numBrackets);
				if(intBracketNum < 1)
					JOptionPane.showMessageDialog(this, "Error: A new tournament must contain at least 1 bracket", "Whoops!", JOptionPane.ERROR_MESSAGE);
				else{
					String bracketSize = JOptionPane.showInputDialog(this, "Enter size of each bracket", null);
					if(bracketSize != null){
						Integer intBracketSize = Integer.parseInt(bracketSize);
						if(intBracketSize < 2)
							JOptionPane.showMessageDialog(this, "Error: Each bracket must contain at least 2 competitors", "Whoops!", JOptionPane.ERROR_MESSAGE);
						else{
							model = new TournamentModel(intBracketNum,intBracketSize, "New Tournament");
							model.addObserver(this);
							update(model,null);
						}
					}
				}
			}
			catch(NumberFormatException e){
				JOptionPane.showMessageDialog(this, "Error: Invalid Input", "Whoops!", JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	private void addPersonWindow(){
		if(model == null)
			return;
		name2 = new JTextField("Name");
		name2.setColumns(10);
		school2 = new JTextField("School");
		school2.setColumns(10);
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
				if(name2.getText().equals("Name") || name2.getText().equals("") || ((school2.getText().equals("School") || school2.getText().equals("")) && !noSchool.isSelected())){
					confirm.setEnabled(false);
					cancel.setEnabled(false);
				}
				else{
					confirm.setEnabled(true);
					cancel.setEnabled(true);
				}
			}
		};
		name2.getDocument().addDocumentListener(docListen);
		school2.getDocument().addDocumentListener(docListen);
		dialog = new JDialog(this, "Add new competitor");
		dialog.setLayout(new GridLayout(3,1));
		dialog.setSize(600,300);
		JPanel options = new JPanel();
		options.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridheight = 10;
		constraints.gridwidth = dialog.getWidth();
		constraints.fill = GridBagConstraints.BOTH;
		options.add(name2);
		options.add(school2);
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
		dialog.add(options);
		dialog.add(bracketSelect);
		dialog.add(buttons);
		dialog.setModal(true);
		dialog.setResizable(false);
		dialog.setLocation((int)(tlkt.getScreenSize().getWidth()-dialog.getWidth())/2,(int)(tlkt.getScreenSize().getHeight()-dialog.getHeight())/2);
		dialog.setAlwaysOnTop(true);
		dialog.setVisible(true);
		dialog.pack();
	}

	//Add "which bracket" w/ dropdown menu for names of each

	private void addPerson() {
		String name = name2.getText();
		String school = school2.getText();
		String bracket = (String)bracketOptions.getSelectedItem();
		int brackNum = findBrackNum(bracket);
		if(noSchool.isSelected())
			school = "Unaffiliated";
		if(name.equals(""))
			JOptionPane.showMessageDialog(this, "Error: Please enter a name");
		else if(name.equals("TBD"))
			JOptionPane.showMessageDialog(this, "Error: Invalid name");
		else if(bracket.equals(""))
			JOptionPane.showMessageDialog(this, ERROR_NO_BRACKET_SPECIFIED);
		else if(brackNum==-1)
			JOptionPane.showMessageDialog(this, ERROR_NAME_NOT_FOUND);
		else addPerson(name,school,brackNum);
	}
	private void addPerson(String name, String school, int bracket){	
		String bracketName = Brackets.getBracket(bracket).getName();
		boolean success = model.addPerson(new Person(name, school),bracket);
		if(success){
			JOptionPane.showMessageDialog(this, name + " from " + school + " successfully added to bracket " + bracketName);
		}
		else JOptionPane.showMessageDialog(this, "Error: Could not add to bracket " + bracketName);
		for(Person p: model.getCompetitors(0)){
			System.out.println(p.getName());
			model.advancePerson(p, bracket, "None");
		}
		update(model, null);	
	}
	private int findBrackNum(String bracketName){
		for(int i = 0; i < Brackets.getNum(); i++)
			if(Brackets.getBracket(i).getName().equals(bracketName)) return i;
		return -1;
	}
	
	public void promotePerson(Person person, int bracket, String notes){
		model.advancePerson(person, bracket, notes);
	}
	
	private void newBracketFromFile(){
		String pathName = System.getProperty("user.dir") + "/";
		JFileChooser fileChooser = new JFileChooser(pathName);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(new FileNameExtensionFilter(null, "bracket"));
		int result = fileChooser.showOpenDialog(null);
		if(result == JFileChooser.APPROVE_OPTION){
			File file = fileChooser.getSelectedFile();
			try{
				model = new TournamentModel(file,this);
			}
			catch(FileNotFoundException e){
				JOptionPane.showMessageDialog(this, "Error: Invalid File", "Whoops!", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void loadTabs() {
		brackets.removeAll();
		for(int i = 0; i < Brackets.getNum(); i++)
			brackets.addTab(Brackets.getBracket(i).getName(), new JScrollPane(new BracketPanel(Brackets.getBracket(i),this)));
		currPanel = new BracketPanel(Brackets.getBracket(brackets.getSelectedIndex()),this);
		JScrollPane pane = new JScrollPane(currPanel);
		pane.setFocusable(true);
		brackets.setComponentAt(brackets.getSelectedIndex(), pane);
	}

	private void changeBracketName() {
		String name = JOptionPane.showInputDialog(this, "Enter the bracket to rename");
		if(name==null||name.equals("")) return;
		int index = findBrackNum(name);
		if(index==-1){ 
			JOptionPane.showMessageDialog(this, "Error: Bracket name not found");
			return;
		}
		String newName = JOptionPane.showInputDialog(this, "Enter the new name");
		if(newName==null||newName.equals("")) return;
		model.setBracketName(index,newName);
	}

	private void save(){
		if(currPathway == null)
			saveAs();
		else{
			try {
				model.writeFile(new File(currPathway));
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "An error has occured, the file has not been saved", "Whoops!", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void saveAs(){
		String fileName = JOptionPane.showInputDialog(this, "Save as:");
		if(fileName != null){
			if(fileName.contains(".") || fileName.contains("\\") || fileName.equals(""))
				JOptionPane.showMessageDialog(this, "Error: Invalid file name", "Whoops!", JOptionPane.ERROR_MESSAGE);
			else{
				JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir") + "/");
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = fileChooser.showOpenDialog(null);
				if(result == JFileChooser.APPROVE_OPTION){
					File dir = fileChooser.getSelectedFile();
					currPathway = dir.getAbsolutePath() + "/" + fileName + ".bracket";
					try {
						model.writeFile(new File(currPathway));
					} catch (IOException e) {
						JOptionPane.showMessageDialog(this, "An error has occured, the file has not been saved", "Whoops!", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
	}

	public void update(Observable arg0, Object arg1) {
		if(model == null){
			add.setEnabled(false);
			save.setEnabled(false);
			saveAs.setEnabled(false);
			changeName.setEnabled(false);
			setTitle("Tournament Runner");
		}
		else{
			loadTabs();
			setTitle(((TournamentModel) arg0).getName());
			add.setEnabled(true);
			save.setEnabled(true);
			saveAs.setEnabled(true);
			changeName.setEnabled(true);
			currPanel.paint(this.getGraphics());
			
		}
	}


	public static void main(String[] args){
		TournamentView view = new TournamentView();
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object source = arg0.getSource();
		if(source == newBracket)
			createNewBracket();
		else if(source == add)
			addPersonWindow();
		else if(source == fromFile)
			newBracketFromFile();
		else if(source == save)
			save();
		else if(source == saveAs)
			saveAs();
		else if(source == changeName)
			changeBracketName();
		else if(source == confirm){
			dialog.dispose();
			addPerson();
		}
		else if(source == cancel){
			dialog.dispose();
		}
		else if(source == noSchool){
			if(name2.getText().equals("Name") || name2.getText().equals("") || ((school2.getText().equals("School") || school2.getText().equals("")) && !noSchool.isSelected())){
				confirm.setEnabled(false);
				cancel.setEnabled(false);
			}
			else{
				confirm.setEnabled(true);
				cancel.setEnabled(true);
			}
			if(noSchool.isSelected()){
				school2.setText("Unaffiliated");
				school2.setEditable(false);
			}
			else{
				school2.setText("School");
				school2.setEditable(true);
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode() == KeyEvent.VK_MINUS){
			BracketPanel.zoomOut();
		}
		else if(arg0.getKeyCode() == KeyEvent.VK_EQUALS){
			BracketPanel.zoomIn();
		}
		update(model,null);
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}
}
