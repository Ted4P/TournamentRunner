
public class Bracket {//Represents a single bracket, with given size and elimination style
	private Match top;
	public Bracket(int size) {
		top = new Match(size, null);
	}
	public boolean addPerson(Person per){return top.addPerson(per);}
	
	public void recordWin(Person winner, String notes){
		top.advancePerson(winner);
	}
	
	public static void main(String[] args){		//Test method for Person and Bracket
		Bracket ne138 = new Bracket(2);
		ne138.addPerson(new Person("TED", "MX"));
		ne138.addPerson(new Person("HARRY", "MRN"));
		ne138.recordWin(new Person("TED", "MX"), "PIN");
		System.out.println(ne138.top.getPerson());
	}
}
