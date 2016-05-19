//Represents a single bracket, with size set in the constructor
public class Bracket {
	private Match top;
	private String name;
	public Bracket(int size,  String name) {
		top = new Match(size, null,1);
		this.name = name;
	}
	//Set the bracket name
	public void setName(String nm){name = nm;}
	/*
	 * Attempt to add a new Person to the bracket, and return false if no space can be found
	 */
	public boolean addPerson(Person per){return top.addPerson(per);}
	
	public void recordWin(Person winner, String notes, int matchID){
		top.advancePerson(winner, notes, matchID);
	}
	/*
	 * Returns all state information about the bracket in postfix notation
	 */
	public String getInfo(){
		return top.getInfo();
	}
	//Return the bracket name
	public String getName(){
		return name;
	}
	public void restoreState(String[] data) {
		top.restoreState(data);
	}
	public void promoteBye() {
		top.promoteBye();
	}
	public void setMatch(Person left, Person right, Person winner,String notes, int index) {
		top.setMatch(left,right,winner,notes,index);
	}
}
