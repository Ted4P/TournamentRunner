public class Match {		//IF YOU NEED FUNCTIONALITY FROM THIS CLASS, ASK AND I'LL ADD A GETTER. THE CODE IS AWFUL AND WILL MAKE YOU HATE LIFE ITSELF
	private Match left, right, parent;
	private Person lPer, rPer, wPer;
	private String note;
	private int val, numLeft, numRight;
	public Match(int size, Match parent, int i){
		val=i;
		lPer = new Person("TBD", "TBD");
		rPer = new Person("TBD", "TBD");
		wPer = new Person("No Winner", "No School");
		note = "No information availible yet";
		this.parent = parent;
		if(size<=2) return;
		left = new Match(size/2, this,i*2);
		right = new Match(size/2, this,i*2+1);
	}
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
		
	public boolean advancePerson(Person person, String notes){
		if(left==null&&right==null){
			if(lPer!=null && lPer.equals(person)){ promoteWinner(lPer,notes); return true;}
			if(rPer!=null && rPer.equals(person)){ promoteWinner(rPer,notes); return true;}
			return false;
		}
		if(lPer!=null && lPer.equals(person)){
			return promoteWinner(lPer, notes);
		}
		if(rPer!=null && rPer.equals(person)){
				return promoteWinner(rPer, notes);
		}
		if(left.advancePerson(person, notes)) return true;
		if(right.advancePerson(person, notes)) return true;
		return false;
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
	public String getNotes(){return note;}
	public Person getWinner() {
		return wPer;
	}
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
	public void restoreState(String[] data) {
		if(Integer.parseInt(data[4])==val){
			if(data[0]!="TBD/")
				lPer = new Person(data[0].substring(0,data[0].indexOf('/')),data[0].substring(data[0].indexOf('/')+1));
			if(data[1]!="TBD/")
				rPer = new Person(data[1].substring(0,data[1].indexOf('/')),data[1].substring(data[1].indexOf('/')+1));
			if(data[2]!="TBD/")
				wPer = new Person(data[2].substring(0,data[2].indexOf('/')),data[2].substring(data[2].indexOf('/')+1));
			note = data[3];
			return;
		}
		else{
			if(left!=null) left.restoreState(data);
			if(right!=null) right.restoreState(data);
		}
		
	}
}
