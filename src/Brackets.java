
public class Brackets {
	private Brackets(){}
	private static Bracket[] brackets;
	public static void setBrackets(int num, int size) {
		if(brackets!=null) return;
		brackets = new Bracket[num];
		for(int i = 0; i < num; i++) brackets[i] = new Bracket(size);
	}
	public static Bracket getBracket(int num) {
		if(num>=brackets.length) return null;
		return brackets[num];
	}
	
}
