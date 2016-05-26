package windows;

import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;

import tournamentRunner.Brackets;
import tournamentRunner.TournamentView;

/*
 * Represents a window to prompt which bracket to fill with Byes
 */
public class FillByeWindow extends JDialog implements ActionListener {
	private TournamentView view;
	private JButton confirm, cancel;
	private JComboBox<String> bracketOptions;
	private JCheckBox fillAll;
	
	
	public FillByeWindow(TournamentView view){
		this.view = view;
		confirm = new JButton("Confirm");
		confirm.addActionListener(this);
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		
		String[] brackets = new String[Brackets.getNum()];
		for(int i=0;i<brackets.length;i++)
			brackets[i] = Brackets.getBracket(i).getName();
		bracketOptions = new JComboBox<String>(brackets);
		fillAll = new JCheckBox("Select All");
		fillAll.addActionListener(this);
		
		this.setLayout(new GridLayout(4,1));
		this.add(bracketOptions);
		this.add(fillAll);
		this.add(confirm);
		this.add(cancel);
		
		this.setSize(600,300);
		Toolkit tlkt = Toolkit.getDefaultToolkit();
		this.setModal(true);
		this.setResizable(false);
		this.setLocation((int)(tlkt.getScreenSize().getWidth()-this.getWidth())/2,(int)(tlkt.getScreenSize().getHeight()-this.getHeight())/2);
		this.setAlwaysOnTop(true);
		this.setVisible(true);
		this.pack();
	}
	
	public void actionPerformed(ActionEvent arg0) {
		Object source = arg0.getSource();
		if(source == fillAll){
			if(fillAll.getModel().isSelected()){
				bracketOptions.setEnabled(false);
			}
			else{
				bracketOptions.setEnabled(true);
			}
		}
		else if(source == cancel){
			dispose();
		}
		else if(source == confirm){
			dispose();
			view.addByes((String) bracketOptions.getSelectedItem(), fillAll.isSelected());
		}

	}

}
