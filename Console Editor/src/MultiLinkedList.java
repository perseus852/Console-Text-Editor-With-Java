public class MultiLinkedList {
	private Linenode head;	

	public Linenode getHead() {
		return head;
	}
	public void addLine(int satirnumarasi) {
		Linenode temp;
		if (head == null) {
			temp = new Linenode(satirnumarasi); 
			head = temp;
		}
		else {		     
			temp = head;
			while (temp.getDown() != null)
				temp = temp.getDown();
			Linenode newnode = new Linenode(satirnumarasi);
			temp.setDown(newnode);
		}
	}
	public Linenode getLineNode(int y){
		Linenode temp = head;
		int i = 0;
		Linenode line = null;
		while(temp != null){
			i++;
			if(i == y) {
				line = temp;
				break;
			}
			temp = temp.getDown();
		}
		return line;
	}
	public void addLetter(char kelime,int x,int y) {
		if (head == null)    
			System.out.println("Add a line before adding a letter.");
		else {
			Linenode temp = head;
			while (temp != null) 
			{	    	 
				if (y==temp.getSatirnumber()) {
					Letternode temp2 = temp.getRight(); 
					if (temp2 == null) {
						temp2 = new Letternode(kelime); 
						temp.setRight(temp2);
					}
					else { 
						while (temp2.getNext() != null){
							temp2 = temp2.getNext();
						}
						Letternode newnode = new Letternode(kelime);
						temp2.setNext(newnode);
						newnode.setPrevious(temp2);
					}
				}
				temp = temp.getDown();
			}
		}
	}
	public Letternode getLetterN(int x,int y){
		Linenode temp = head;
		for(int i=1;i<y;i++) {
			if(temp != null)temp = temp.getDown();
		}
		Letternode temp2 = temp.getRight();
		for(int i=1;i<x;i++) {
			if(temp2 != null)temp2 = temp2.getNext();
		}
		return temp2;
	}
	public void setLetter(char kelime,int x,int y) {
		Letternode newLetter = null;
		if(getLetterN(x, y) == null && x != 1){//bir önceki eleman boþsa arada boþluk var * koymak lazým demek
			Letternode start = null;
			if(getLineNode(y).getRight() != null) start = getlastletter(y);//* koymaya baþlanýlýcak node satýr boþ deðilse en son node
			else {//satýr boþsa ilk satýra * koyup ordan baþla
				getLineNode(y).setRight(new Letternode('*'));
				start = getLineNode(y).getRight();
			}
			while(getLetterPos(start) != x ){//start'tan baþlayýp harfin ekleneceði yerin bir eksiðine kadar yýldýz koy 
				start.setNext(new Letternode('*'));
				start = start.getNext();
			}
			newLetter = start;//son eklenen yýldýz = addAfter
		}
		else newLetter = getLetterN(x, y);//seçilen yer boþluk deðilse direk harfin ekleneceði yer olarak ata
		if(newLetter == null) getLineNode(y).setRight(new Letternode(kelime));
		else newLetter.setLetter(kelime);
	}
	public void insertLetterToRight(char letter,int x,int y){
		Letternode newnode = new Letternode(letter);
		Linenode temp = head;
		if (head == null)    
			System.out.println("Add a satir before kelime");
		else {			
			temp = getLineNode(y);
			Letternode addAfter = null;
			if(getLetterN(x-1, y) == null && x != 1){//bir önceki eleman boþsa arada boþluk var * koymak lazým demek
				Letternode start = null;
				if(getLineNode(y).getRight() != null) start = getlastletter(y);//* koymaya baþlanýlýcak node satýr boþ deðilse en son node
				else {//satýr boþsa ilk satýra * koyup ordan baþla
					getLineNode(y).setRight(new Letternode('*'));
					start = getLineNode(y).getRight();
				}
				while(getLetterPos(start) != x - 1){//start'tan baþlayýp harfin ekleneceði yerin bir eksiðine kadar yýldýz koy 
					start.setNext(new Letternode('*'));
					start = start.getNext();
				}
				addAfter = start;//son eklenen yýldýz = addAfter
			}
			else addAfter = getLetterN(x-1, y);//seçilen yer boþluk deðilse direk harfin ekleneceði yer olarak ata
			Letternode next = null;
			if(addAfter != null)next = addAfter.getNext();
			if(x == 1) {
				newnode.setNext(addAfter);
				temp.setRight(newnode);
			}
			else {
				addAfter.setNext(newnode);
				if(next != null) newnode.setNext(next);
			}
			while(temp != null){
				if(getLetterPos(getlastletter(temp.getSatirnumber()) ) == 61) {//satýrýn son harfinibi satýr aþaðý kaydýr
					Letternode lastLetter = getlastletter(temp.getSatirnumber());//son harfi ata
					lastLetter.getPrevious().setNext(null);//son harfi sil
					Linenode nextLine = getLineNode(temp.getSatirnumber()+1);//sonraki satýrý ata
					if(nextLine == null){//sonraki satýr yoksa 
						addLine(temp.getSatirnumber()+1);//sonraki satýrý oluþtur
						nextLine = getLineNode(temp.getSatirnumber()+1);//sonraki satýrý ata
						nextLine.setRight(lastLetter);//son harfi sonraki satýrýn baþýna ekle
						lastLetter.setPrevious(null);//son harfin previouslarý temizle
					}
					else{//sonraki satýr varsa
						addToLineStart(lastLetter.getLetter(), temp.getSatirnumber()+1);//o satýrýn baþýna ekle
					}
				}
				temp = temp.getDown();
			}
		}
	}
	public void insertModeDelete(int x,int y){
		Letternode letterToDelete = getLetterN(x, y);
		if(letterToDelete == null) return;
		if(x==1){
			Letternode nextLetter = null;
			if(letterToDelete.getNext() != null)  nextLetter = letterToDelete.getNext();
			Linenode line = getLineNode(y);
			line.setRight(nextLetter);
		}
		else{
			Letternode previousLetter = letterToDelete.getPrevious();
			Letternode nextLetter = null;
			if(letterToDelete.getNext() != null) nextLetter = letterToDelete.getNext();
			previousLetter.setNext(nextLetter);
		} 
	}
	public void addToLineStart(char letter,int y){
		Linenode chosenLineN = getLineNode(y);
		Letternode newLetterN = new Letternode(letter);
		Letternode firstLetterOfLine = chosenLineN.getRight();
		chosenLineN.setRight(newLetterN);
		newLetterN.setNext(firstLetterOfLine);
	}
	public int getLineCount(){
		Linenode temp = head;
		int i = 1;
		while(temp.getDown() != null){
			i++;
			temp = temp.getDown();
		}
		return i;
	}
	public int getlastletterPos(int y) {
		Linenode temp = head;
		for(int i=1;i<y;i++) {
			temp = temp.getDown();
		}
		int i=1;
		Letternode temp2 = temp.getRight();
		while(temp2 != null){
			i++;
			temp2 = temp2.getNext();
		}
		return i;
	}
	
	public int getFirstLetterPos(int y) {
		Linenode temp= head;
		for(int i=1;i<y;i++) {
			temp = temp.getDown();
		}
		Letternode temp2 = temp.getRight();
		int x = 1;
		while(temp2.getLetter() == '*' && temp2.getNext() != null) {
			temp2 = temp2.getNext();
			x++;
			if(temp2.getLetter() != '*')
				break;
		}
		return x;
	}
	public Letternode getlastletter(int y) {
		try{//burasý sürekli hata veriyor diye ekledim
			Linenode temp= getLineNode(y);
			Letternode temp2 = temp.getRight();
			while(temp2.getNext() != null){
				temp2 = temp2.getNext();
			}
			return temp2;
		}catch (Exception NullPointerException){
		}
		return null;
	}
	public int getLetterPos(Letternode node){
		int i = 1;
		if(node != null)while(node.getPrevious() != null){
			i++;
			 node = node.getPrevious();
		}
		return i;
	}
	public boolean isLineEmpty(int y) {
		Linenode line = getLineNode(y);
		if(line.getRight() == null) return true;
		else{
			Letternode temp = line.getRight();
			while(temp != null){
				if(temp.getLetter() != '*') return false;
				temp = temp.getNext();
			}
			return true;
		}
	}
	public int[] getCoordinates(Letternode lettern){
		Linenode line = head;
		int x = 0;
		int y = 1;
		while(line != null){
			Letternode letter = line.getRight();
			while(letter != null){
				x++;
				if(letter == lettern) return new int[]{x,y};
				letter = letter.getNext();
			}
			x=0;
			y++;
			line = line.getDown();
		}
		return new int[]{x,y};
	}
}
