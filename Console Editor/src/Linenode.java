public class Linenode {
	private int satirnumber;
	private Linenode down;
	private Letternode right;
	private Linenode up;
	private Letternode left;
	
	public Linenode(int dataToAdd) {
		satirnumber = dataToAdd;
		down = null;
		right = null;
	}

	public Linenode getUp() {
		return up;
	}

	public Letternode getLeft() {
		return left;
	}

	public void setUp(Linenode up) {
		this.up = up;
		if(up != null)up.down = this;
	}

	public void setLeft(Letternode left) {
		this.left = left;
	}

	public int getSatirnumber() {
		return satirnumber;
	}

	public void setSatirnumber(int satirnumber) {
		this.satirnumber = satirnumber;
	}

	public Linenode getDown() {
		return down;
	}

	public void setDown(Linenode down) {
		this.down = down;
		if(down != null)down.up = this;
	}

	public Letternode getRight() {
		return right;
	}

	public void setRight(Letternode right) {
		this.right = right;
	}
	public int getLength(){
		Letternode temp2 = this.getRight();
		int i = 1;
		while(temp2.getNext() != null){
			temp2 = temp2.getNext();
			i++;
		}
		return i;
	}
	public int getStarCount(){
		Letternode temp2 = this.getRight();
		int i = 0;
		while(temp2.getNext() != null){
			temp2 = temp2.getNext();
			if(temp2.getLetter() == '*')i++;
			if(temp2.getLetter() != '*') break;
		}
		return i;
	}
	public int getLengthOnlyLetters(){
		Letternode temp2 = this.getRight();
		int i = 1;
		while(temp2.getNext() != null){
			temp2 = temp2.getNext();
			if(temp2.getLetter() != '*')i++;
		}
		return i;
	}
}
