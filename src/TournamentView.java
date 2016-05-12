import java.awt.Container;
import java.awt.Panel;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;


public class TournamentView extends JFrame implements Observer{
	TournamentModel model;
	
	public TournamentView(){		//Start popup with 
		Container pane = getContentPane();
		
		/*
		 * Startup prompt for number and size of brackets
		 */
		 String numBrackets = JOptionPane.showInputDialog(this,
                 "Enter number of brackets", null);
		 if(numBrackets!=null){
			 String bracketSize = JOptionPane.showInputDialog(this,
                 "Enter size of each bracket", null);
			 model = new TournamentModel(Integer.parseInt(numBrackets),Integer.parseInt(bracketSize));
			 model.addObserver(this);
		 }
		 else {}//If canceled out, load file?
		 
		 
		 
		//GUI FILLER
		pane.add(new JLabel("HELLO WORLD"));
		setTitle("Tournament Runner");
		setSize(700,400);
		setVisible(true);
	}


	public void update(Observable arg0, Object arg1) {
		//UPDATE BRACKETS
	}
	
	public static void main(String[] args){
		TournamentView view = new TournamentView();
	}
}
