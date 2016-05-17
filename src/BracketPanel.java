import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Scanner;

import javax.swing.JPanel;

public class BracketPanel extends JPanel {
	private Bracket bracket;
	private TournamentView tView;
	private String[][] info;
	private final int MATCH_WIDTH = 50, MATCH_HEIGHT = MATCH_WIDTH*2/5;
	public BracketPanel(Bracket bracket, TournamentView tView){
		this.bracket = bracket;
		this.tView = tView;
		info = new String[Brackets.getSize()-1][6];
		String bracketInfo = bracket.getInfo();
		Scanner scan = new Scanner(bracketInfo);
		for(int i=0; scan.hasNext();i++){
			String line = scan.nextLine();
			for(int j=0;j<6;j++){
				if(j%2 == 0){
					info[i][j] = line.substring(0,line.indexOf('/'));
					line = line.substring(line.indexOf('/')+1);
				}
				else if(j != 5){
					info[i][j] = line.substring(0,line.indexOf(','));
					line = line.substring(line.indexOf(',')+1);
				}
				else{
					info[i][5] = line.substring(line.indexOf(',')+1, line.indexOf(',', line.indexOf(',')+1));
					line = "Finished";
				}
			}
		}
	}

	public void paintComponent(Graphics g){
		if(g instanceof Graphics2D)
			g = (Graphics2D)g;
		super.paintComponent(g);
		/*for(int i=0;i<Brackets.getSize()-1;i++){
			for(int j=0;j<6;j++){
				g.drawString(info[i][j], 100*j,20*i);
			}
		}*/
		int level = 0;
		for(;Math.pow(2, level) < Brackets.getSize();level++);
		int shift = (int)Math.pow(2, level-1)*10;
		int totShift = (int)(2 * shift * (1-Math.pow(0.5, level)));
		paintOneMatch(2*MATCH_WIDTH*level-(int)(MATCH_WIDTH*1.5),totShift-MATCH_HEIGHT/2+MATCH_HEIGHT,shift, 0, Brackets.getSize()-1, g);
		tView.setSize(1500,1500);
	}
	private void paintOneMatch(int x, int y, int shift, int level, int match, Graphics g){
		/*To be used for adding normal input
		for(int j=0;j<6;j++){}*/
		g.drawRect(x, y, MATCH_WIDTH, MATCH_HEIGHT);
		g.drawLine(x-MATCH_WIDTH/2,y+MATCH_HEIGHT/2,x-MATCH_WIDTH,y+MATCH_HEIGHT/2-shift);
		g.drawLine(x-MATCH_WIDTH/2,y+MATCH_HEIGHT/2,x-MATCH_WIDTH,y+MATCH_HEIGHT/2+shift);
		if(Math.pow(2, level+1) + 1 < Brackets.getSize()-1){
			g.drawLine(x-MATCH_WIDTH/2,y+MATCH_HEIGHT/2,x+MATCH_WIDTH,y+MATCH_HEIGHT/2);
			paintOneMatch(x-MATCH_WIDTH*2,y+shift,shift/2,level+1,match-1,g);
			paintOneMatch(x-MATCH_WIDTH*2,y-shift,shift/2,level+1,match-2,g);
		}
		else{
			g.drawLine(x,y+MATCH_HEIGHT/2,x+MATCH_WIDTH,y+MATCH_HEIGHT/2);
		}
	}
}
