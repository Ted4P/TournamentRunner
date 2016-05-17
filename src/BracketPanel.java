import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JPanel;

public class BracketPanel extends JPanel {
	private Bracket bracket;
	private TournamentView tView;
	private String[][] info;
	private final int MATCH_WIDTH = 100, MATCH_HEIGHT = MATCH_WIDTH*2/5;
	public BracketPanel(Bracket bracket, TournamentView tView){
		this.bracket = bracket;
		this.tView = tView;
		info = new String[Brackets.getSize()][5];
		String bracketInfo = bracket.getInfo();
		Scanner scan = new Scanner(bracketInfo);
		int i=0;
		while(i<Brackets.getSize() && scan.hasNext()){
			String line = scan.nextLine();
			info[i] = line.split(",");
			i++;
		}
	}

	public void paintComponent(Graphics g){
		removeAll();
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
		int shift = (int)(Math.pow(2, level-1)*(MATCH_WIDTH*12.0/100));
		int totShift = (int)(2 * shift * (1-Math.pow(0.5, level)));
		paintOneMatch(2*MATCH_WIDTH*level-(int)(MATCH_WIDTH*1.5),totShift-MATCH_HEIGHT/2+MATCH_HEIGHT,shift, 0, 1, g);
		tView.setSize(1500,1500);
	}
	private void paintOneMatch(int x, int y, int shift, int level, int match, Graphics g){
		g.drawRect(x, y, MATCH_WIDTH, MATCH_HEIGHT);
		int index = 0;
		while(index < Brackets.getSize() && !info[index][4].equals("" + match)){
			index++;
		}
		String[] splitSlash = null;
		for(int i=0;i<2;i++){
			if(info[index][2].equals(info[index][i])){
				g.setFont(new Font(g.getFont().getFontName(), Font.BOLD, g.getFont().getSize()));
			}
			splitSlash = info[index][i].split("/");
			g.drawString(splitSlash[0], x+5, y+(5*i+4)*MATCH_HEIGHT/10);
			g.drawString(splitSlash[1], x+MATCH_WIDTH/2, y+(5*i+4)*MATCH_HEIGHT/10);
			g.setFont(new Font(g.getFont().getFontName(),Font.PLAIN,g.getFont().getSize()));
		}
		g.drawLine(x-MATCH_WIDTH/2,y+MATCH_HEIGHT/2,x-MATCH_WIDTH,y+MATCH_HEIGHT/2-shift);
		g.drawLine(x-MATCH_WIDTH/2,y+MATCH_HEIGHT/2,x-MATCH_WIDTH,y+MATCH_HEIGHT/2+shift);
		if(Math.pow(2, level+1) + 1 < Brackets.getSize()-1){
			g.drawLine(x-MATCH_WIDTH/2,y+MATCH_HEIGHT/2,x+MATCH_WIDTH,y+MATCH_HEIGHT/2);
			paintOneMatch(x-MATCH_WIDTH*2,y+shift,shift/2,level+1,match*2,g);
			paintOneMatch(x-MATCH_WIDTH*2,y-shift,shift/2,level+1,match*2+1,g);
		}
		else{
			g.drawLine(x,y+MATCH_HEIGHT/2,x+MATCH_WIDTH,y+MATCH_HEIGHT/2);
		}
	}
}
