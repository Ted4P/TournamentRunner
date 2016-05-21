package tournamentRunner;
/*
 * Represents a bracket using a tree data structure
 */
public class Match {
	private Match left, right, parent;
	private Person lPer, rPer, wPer;
	private String note;
	private int val, numLeft, numRight;
	public static final String DEFAULT_WINNER_NAME = "No Winner", DEFAULT_WINNER_SCHOOL = "No School", DEFAULT_BLANK_NAME = "TBD", DEFAULT_BLANK_SCHOOL = "TBD";
	public Match(int size, Match parent, int i){
		val=i;
		lPer = new Person(DEFAULT_BLANK_NAME, DEFAULT_BLANK_SCHOOL);
		rPer = new Person(DEFAULT_BLANK_NAME, DEFAULT_BLANK_SCHOOL);
		wPer = new Person(DEFAULT_WINNER_NAME,DEFAULT_WINNER_SCHOOL);
		note = "No information availible yet";
		this.parent = parent;
		if(size<=2) return;
		left = new Match(size/2, this,i*2);
		right = new Match(size/2, this,i*2+1);
	}
	/*
	 * Add a new Person to the bracket, attempting to "balance" by adding people to the side with fewer entrants. 
	 * Returns false if no free spaces exist in the given bracket, otherwise true
	 */
	public boolean addPerson(Person person){
		if(left==null&&right==null){
			if(lPer.getName().equals("TBD")){lPer = person; return true;}
			else if(rPer.getName().equals("TBD")){rPer = person; return true;}
		}
		if(numLeft<numRight){
			if(left!=null && left.addPerson(person)){ numLeft++; return true;}
			else if(right!=null && right.addPerson(person)){ numRight++; return true;}
			return false;
		}
		if(right!=null && right.addPerson(person)){ numRight++; return true;}
		else if(left!=null && left.addPerson(person)){ numLeft++; return true;}
		return false;
	}
	
	/*
	 * Attempt to advance the given person one match in the bracket. 
	 * Find the match with a contestant that .equals() the submitted person, then:
	 * 			Move that person to the "winner" slot in the match
	 * 			Advance that person, if possible, to the next round of competition
	 * 			Set the match notes to the submitted notes string
	 */
	public boolean advancePerson(Person person, String notes, int matchID){
		if(matchID == val){
			if(lPer.equals(person))
				return promoteWinner(person, notes);
			else if(rPer.equals(person))
				return promoteWinner(person, notes);
			else
				return false;
		}
		else if(left != null && left.advancePerson(person, notes, matchID)) return true;
		else if(right != null && right.advancePerson(person, notes, matchID)) return true;
		else return false;
	}
	private boolean promoteWinner(Person per, String notes) {
		note = notes;
		wPer = per;
		if(parent!=null){
			parent.setPerson(per, val);
		}
		return true;
	}
	private void setPerson(Person per, int childVal){
		if(val*2 == childVal)
			rPer = per;
		else
			lPer = per;
	}
	/*
	 * Return a string containing the information for this bracket in postfix notation in the given format:
	 * 			Name/school of the left, right, and winner, followed by match notes and the value of the match, all comma separated
	 * 			One match per line, with children having values val*2 and val*2+1
	 */
	public String getInfo(){
		String result = "";
		if(left != null)
			result += left.getInfo() + "\n";
		if(right != null)
			result += right.getInfo() + "\n";
		result += lPer.getName() + "/" + lPer.getSchool() + ","
			+ rPer.getName() + "/" + rPer.getSchool() + ","
			+ wPer.getName() + "/" + wPer.getSchool() + "," + note + "," + val;
		return result;
	}
	/*
	 * Given a Match toString, recursively determine if the given match is equal to the current, 
	 * 			then load information into the Match using the format specified in getInfo
	 */
	public void restoreState(String[] data) {
		if(Integer.parseInt(data[4])==val){
			lPer = new Person(data[0].substring(0,data[0].indexOf('/')),data[0].substring(data[0].indexOf('/')+1));
			rPer = new Person(data[1].substring(0,data[1].indexOf('/')),data[1].substring(data[1].indexOf('/')+1));
			wPer = new Person(data[2].substring(0,data[2].indexOf('/')),data[2].substring(data[2].indexOf('/')+1));
			note = data[3];
			return;
		}
		else{
			if(left!=null) left.restoreState(data);
			if(right!=null) right.restoreState(data);
		}
		
	}
	/*
	 * For all competitors facing Byes, promote until a match featuring two humans is found
	 */
	public void promoteBye() {
		if(left!=null) left.promoteBye();
		if(right!=null) right.promoteBye();
		if(lPer instanceof Bye && rPer instanceof Bye) return;
		if(lPer instanceof Bye) advancePerson(rPer, "Bye", val);
		if(rPer instanceof Bye) advancePerson(lPer, "Bye", val);
	}
	public void setMatch(Person left2, Person right2, Person winner, String notes, int index) {
		if(val==index){
			lPer= left2;
			rPer = right2;
			wPer = winner;
			note = notes;
			return;
		}
		if(left!=null) left.setMatch(left2, right2, winner, notes, index);
		if(right!=null) right.setMatch(left2, right2, winner, notes, index);
	}
}
