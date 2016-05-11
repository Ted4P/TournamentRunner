
public class Brackets {
	private Brackets(){}
	private static Bracket[] brackets;
	public static void setBrackets(int num, int size, boolean doubleElim) {
		if(brackets!=null) return;
		brackets = new Bracket[num];
		for(int i = 0; i < num; i++) brackets[i] = new Bracket(size, doubleElim);
	}
	
}
