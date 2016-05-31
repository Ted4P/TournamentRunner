package tournamentRunner;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import windows.*;


public class TournamentView extends JFrame implements Observer, ActionListener{
	private static final String 
	ERROR_NAME_NOT_FOUND = "Error: No bracket with specified name found", 
	ERROR_NO_BRACKET_SPECIFIED = "Error: No bracket specified";
	TournamentModel model;
	private final JMenuItem newBracket, fromFile, save, saveAs, add, changeName, zoomIn, zoomOut, addRoster, print, fillByes;
	private String currPathway;
	private JTabbedPane brackets;
	private BracketPanel currPanel;
	private ArrayList<BracketPanel> bracketArray;


	public TournamentView(){//Start popup with
		currPathway = null; 
		Container pane = getContentPane();
		JMenuBar bar = new JMenuBar();
		JMenu file = new JMenu("File");
		newBracket = new JMenuItem("New Bracket");
		addCtrlKey(newBracket, 'N');
		newBracket.addActionListener(this);
		file.add(newBracket);
		fromFile = new JMenuItem("Open");
		addCtrlKey(fromFile,'O');
		fromFile.addActionListener(this);
		file.add(fromFile);
		save = new JMenuItem("Save");
		addCtrlKey(save,'S');
		save.addActionListener(this);
		file.add(save);
		saveAs = new JMenuItem("Save As");
		saveAs.addActionListener(this);
		file.add(saveAs);
		JMenu edit = new JMenu("Edit");
		add = new JMenuItem("Add Competitor");
		addCtrlKey(add,'A');
		add.addActionListener(this);
		print = new JMenuItem("Print");
		addCtrlKey(print,'P');
		print.addActionListener(this);
		file.add(print);
		edit.add(add);
		addRoster = new JMenuItem("Add Team Roster");
		addCtrlKey(addRoster,'T');
		addRoster.addActionListener(this);
		edit.add(addRoster);
		changeName = new JMenuItem("Change Bracket Name");
		addCtrlKey(changeName,'R');
		changeName.addActionListener(this);
		fillByes = new JMenuItem("Fill Bracket");
		fillByes.addActionListener(this);
		addCtrlKey(fillByes, 'F');
		edit.add(changeName);
		edit.add(fillByes);
		JMenu view = new JMenu("View");
		zoomIn = new JMenuItem("Zoom In");
		zoomOut = new JMenuItem("Zoom Out");
		addCtrlKey(zoomIn,'=');
		addCtrlKey(zoomOut,'-');
		zoomIn.addActionListener(this);
		zoomOut.addActionListener(this);
		view.add(zoomIn);
		view.add(zoomOut);
		bar.add(file);
		bar.add(edit);
		bar.add(view);
		update(null,null);
		brackets = new JTabbedPane();
		brackets.setTabPlacement(JTabbedPane.LEFT);
		bracketArray = new ArrayList<BracketPanel>();
		pane.add(brackets);
		super.setJMenuBar(bar);
		setTitle("Bracket Master");
		setSize(1600,800);
		Toolkit tlkt = Toolkit.getDefaultToolkit();
		this.setLocation((int)(tlkt.getScreenSize().getWidth()-this.getWidth())/2,(int)(tlkt.getScreenSize().getHeight()-this.getHeight())/2);
		setVisible(true);
		super.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void addCtrlKey(JMenuItem item, char c) {
		item.setAccelerator(KeyStroke.getKeyStroke(c,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}
	//Attempt to create a new bracket, and check for size and number
	public void createNewBracket(String name, String sNum, String sSize){
		try{
			int num = Integer.parseInt(sNum), size = Integer.parseInt(sSize);
			if(num < 1){ 
				JOptionPane.showMessageDialog(this, "Error: A new tournament must contain at least 1 bracket", "Whoops!", JOptionPane.ERROR_MESSAGE);
			}
			model = new TournamentModel(num,size,name);
			model.addObserver(this);
			update(model,null);
		}
		catch(NumberFormatException e){
			JOptionPane.showMessageDialog(this, "Error: Invalid Input", "Whoops!", JOptionPane.ERROR_MESSAGE);
		}
	}

	//Add a new person to the specified bracket, with the specified seed
	public void addPerson(String name, String school, String seed, String bracket, boolean noSchool, boolean unseeded) {
		int brackNum = findBrackNum(bracket);
		if(noSchool)
			school = "Unaffiliated";
		if(name.equals(""))
			JOptionPane.showMessageDialog(this, "Error: Please enter a name");
		else if(name.equals(Match.DEFAULT_BLANK_NAME) || school.equals(Match.DEFAULT_BLANK_SCHOOL))
			JOptionPane.showMessageDialog(this, "Error: Invalid name or school");
		else if(bracket.equals(""))
			JOptionPane.showMessageDialog(this, ERROR_NO_BRACKET_SPECIFIED);
		else if(brackNum==-1)
			JOptionPane.showMessageDialog(this, ERROR_NAME_NOT_FOUND);
		else addPerson(name,school,brackNum, (unseeded)? -1 : Integer.parseInt(seed));
	}
	private void addPerson(String name, String school, int bracket, int seed){	
		String bracketName = Brackets.getBracket(bracket).getName();
		boolean success = model.addPerson(new Person(name, school),bracket,seed);
		if(success){
			JOptionPane.showMessageDialog(this, name + " from " + school + " successfully added to bracket " + bracketName);
		}
		else JOptionPane.showMessageDialog(this, "Error: Could not add to bracket " + bracketName);
		update(model, null);	
	}
	private int findBrackNum(String bracketName){		//Return the bracket index given its name
		for(int i = 0; i < Brackets.getNum(); i++)
			if(Brackets.getBracket(i).getName().equals(bracketName)) return i;
		return -1;
	}
	public void addByes(String bracket, boolean setAll){		//Fill a bracket, or all brackets, with Byes, and promote human past Byes
		int num = findBrackNum(bracket);
		if(setAll){
			for(int i = 0; i < Brackets.getNum(); i++){
				while(model.addPerson(new Bye(), i));
			}
		}
		else
			while(model.addPerson(new Bye(), num));
		Brackets.getBracket(num).promoteByes(true);
	}
	
	public void setMatch(Person p1, Person p2, Person win, String notes, int bracket, int index){		//Set all associated data from a relevant match
		model.setMatch(p1,p2,win,notes,bracket,index);
	}
	
	public void promotePerson(Person person, int bracket, String notes, int matchID){		//Promote the person at matchID, and save notes
		model.advancePerson(person, bracket, notes, matchID);
	}
	
	private void newBracketFromFile(){			//Attempt to load a saved file, with the format specified in the manual
		String pathName = System.getProperty("user.dir") + "/";
		JFileChooser fileChooser = new JFileChooser(pathName);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(new FileNameExtensionFilter(null, "bracket"));
		int result = fileChooser.showOpenDialog(null);
		if(result == JFileChooser.APPROVE_OPTION){
			File file = fileChooser.getSelectedFile();
			try{
				model = new TournamentModel(file,this);
				update(model,null);
			}
			catch(FileNotFoundException e){
				JOptionPane.showMessageDialog(this, "Error: Invalid File", "Whoops!", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private void addRoster(){		//Add a team roster, with the file format described in the manual
		String school = JOptionPane.showInputDialog(this, "Enter the team name");
		if(school==null||school.equals("")) return;
		if(school.equals(Match.DEFAULT_BLANK_SCHOOL) || school.equals(Match.DEFAULT_WINNER_SCHOOL)){ 	//Do not allow default input
			JOptionPane.showMessageDialog(this, "Error: Invalid school name");
			return;
		}
		JOptionPane.showMessageDialog(this, "Select the text file containing names, with one name per line and per bracket");
		String pathName = System.getProperty("user.dir") + "/";
		JFileChooser fileChooser = new JFileChooser(pathName);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(new FileNameExtensionFilter(null, "txt"));
		int result = fileChooser.showOpenDialog(null);
		if(result == JFileChooser.APPROVE_OPTION){
			File file = fileChooser.getSelectedFile();
			try {
				model.addRoster(school, file);
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(this, "Error: Invalid File", "Whoops!", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private void loadTabs() {		//Render the tabbed layout of the program, with one tab for each bracket
		brackets.removeAll();
		for(int i = 0; i < Brackets.getNum(); i++){
			BracketPanel panel = new BracketPanel(Brackets.getBracket(i),this);
			JScrollPane scroll = new JScrollPane(panel);
			bracketArray.add(panel);
			brackets.addTab(Brackets.getBracket(i).getName(), scroll);
		}
		currPanel = new BracketPanel(Brackets.getBracket(brackets.getSelectedIndex()),this);
		JScrollPane pane = new JScrollPane(currPanel);
		pane.setFocusable(true);
		brackets.setComponentAt(brackets.getSelectedIndex(), pane);
	}

	private void changeBracketName() {		//Prompt for the previous bracket name, and new bracket name
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
			saveAs();		//If the file has not been saved before, call saveAs
		else{
			try {
				model.writeFile(new File(currPathway));		//Attempt to write the current .bracket file
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "An error has occured, the file has not been saved", "Whoops!", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void saveAs(){		//Save the current bracket and all associated state, while prompting for a file location
		String fileName = JOptionPane.showInputDialog(this, "Save as:");
		if(fileName != null){
			if(fileName.contains(".") || fileName.contains("\\") || fileName.equals(""))
				JOptionPane.showMessageDialog(this, "Error: Invalid file name", "Whoops!", JOptionPane.ERROR_MESSAGE);
			else{
				JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir") + "/");		//Only allow the user to select directories to place the file in
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = fileChooser.showOpenDialog(null);
				if(result == JFileChooser.APPROVE_OPTION){
					File dir = fileChooser.getSelectedFile();
					currPathway = dir.getAbsolutePath() + "/" + fileName + ".bracket";		//Write the file with the .bracket extension
					try {
						model.writeFile(new File(currPathway));
					} catch (IOException e) {
						JOptionPane.showMessageDialog(this, "An error has occured, the file has not been saved", "Whoops!", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
	}
	/*
	 * Update the TournamentView with information contained in the model, or disable various menu options if the model is currently null
	 */
	public void update(Observable arg0, Object arg1) {
		if(model == null){
			add.setEnabled(false);
			save.setEnabled(false);
			saveAs.setEnabled(false);
			changeName.setEnabled(false);
			fillByes.setEnabled(false);
			zoomIn.setEnabled(false);
			zoomOut.setEnabled(false);
			addRoster.setEnabled(false);
			setTitle("Tournament Runner");
		}
		else{
			loadTabs();
			setTitle(((TournamentModel) arg0).getName());
			add.setEnabled(true);
			save.setEnabled(true);
			saveAs.setEnabled(true);
			changeName.setEnabled(true);
			fillByes.setEnabled(true);
			currPanel.paint(this.getGraphics());
			zoomIn.setEnabled(true);
			zoomOut.setEnabled(true);
			addRoster.setEnabled(true);
		}
	}
	/*private void print(){
		PrinterJob job = PrinterJob.getPrinterJob();
		PageFormat pf = job.defaultPage();
		pf.setOrientation(PageFormat.LANDSCAPE);
		job.setPrintable(new BracketPrinter(bracketArray.get(brackets.getSelectedIndex())));
		boolean doPrint = job.printDialog();
		if(doPrint){
			try{
				job.print();
			}
			catch(PrinterException e){
				JOptionPane.showMessageDialog(this, "Error: Failed to Print", "Whoops!", JOptionPane.ERROR_MESSAGE);
			}
		}
	}*/

	public static void main(String[] args){		//Central runnable core of the program- initialize a new TournamentView to spawn GUI code
		new TournamentView();
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {		//Handle menu item calls, from clicks or hotkeys
		Object source = arg0.getSource();
		if(source == newBracket){
			new NewTournamentWindow(this);
		}
		else if(source == add){
			new AddPersonWindow(this);
		}
		else if(source == fromFile)
			newBracketFromFile();
		else if(source == save)
			save();
		else if(source == saveAs)
			saveAs();
		else if(source == changeName)
			changeBracketName();
		else if(source == addRoster){
			addRoster();
		}
		else if(source == fillByes){
			new FillByeWindow(this);
		}
		else if(source == zoomIn){
			BracketPanel.zoomIn();
			update(model,null);
		}
		else if(source == zoomOut){
			BracketPanel.zoomOut();
			update(model,null);
		}
		else if(source == print){
			//print();
		}
	}
}
