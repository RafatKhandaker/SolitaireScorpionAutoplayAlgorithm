import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * John Hopkins University
 * Data Structures Lab 3
 * Solitaire (Scorpion) Algorithm 
 * @author Rafat Khandaker
 * 11/15/2021
 *
 */
public class Scorpion {

	/**
	 * @param args
	 * initiate Card Values And Groups
	 */
	private static char[] CARD_VALUES = (new char[] {'A','2', '3', '4', '5', '6', '7', '8', '9', '1', 'J', 'Q', 'K'});
	private static char[] CARD_GROUPS = (new char[] {'D','H', 'C', 'S'});
	
	// priority calculation weights will determine how the algorithm will make decisions within the game
	// you may adjust this priority to obtain different results
	// the weights are reflected in order associated and determined by 4 factors :
	/*
	 *           1. # face down cards
	 *           2. # cards below selected option card
	 *           3. # of consecutive matches in column
	 *           4. the position of the card within the column
	 * */
	private static double[] PRIORITY_CALCULATION_WEIGHTS = { 10.0, 1.0, 2.0, 1.0 };
		
	// Main Function
	public static void main(String[] args) {
		// Prompt user to enter file name
		System.out.println("Please enter Scorpion Card File name ex:('ScorpionRequiredInput.txt'): ");
		
		int timer = 3000;

		
		// initiate loop start 
		try {
			
			LinkedList<Card>[] llistCardsArray = new LinkedList[8]; // Linked List Array Implementation of Scorpion structure

			// Parse file into Linked List Array Data-structure in memory
			setCardsScorpion(
					new BufferedReader(new FileReader( new File(new Scanner(System.in).nextLine()))), llistCardsArray
					);
	
			 // Uncomment segment below if you desire to print entire output into text file
			/*
				timer = 0;
				System.out.println("output will be printed to file: output.txt");
		    	System.setOut(new PrintStream(new File("test_output3.txt")));
			*/
			
			// print the card layout on console
			printCardStack(llistCardsArray, null);
			
			// priority cards are playable options to move into location
			PriorityCard[] priorityCards = checkCurrentPlayOptions(llistCardsArray);

			do {
				// inform user the playable option cards
				System.out.println("\n\nChecking Playable Options... (options highlighted within [..]) ...");
				
				// print playable options on card structure
				printCardStack(llistCardsArray, priorityCards);
				
				// inform user that algorithm will auto swap cards based on a priority calculation
				System.out.println("\n\nSwapping based on priority...");

				// re-initalize data-structure after a swap is made by algorithm
				llistCardsArray = priorityBasedSwap(priorityCards, llistCardsArray);
				
				// print new layout of cards after a move has been made by algorithm
				printCardStack(llistCardsArray, null);
				
				// check new play options inside loop
				priorityCards = checkCurrentPlayOptions(llistCardsArray);
				
				// if no more playable options exist, condition will determine if un-vealing 3 face down cards are available
				if(priorityCards == null && !llistCardsArray[7].isEmpty()) {
					llistCardsArray = unveal3FaceDownCards(llistCardsArray); // unveal 3 face down cards re-initialize data-structure
					
					// inform user that algorithm will unveal face-downs
					System.out.println("\n\nUnvealing 3 Face-Down Cards...");
					printCardStack(llistCardsArray, priorityCards);  // print the new data structure

					// check for playable options after 3 facedown cards are unveiled
					priorityCards = checkCurrentPlayOptions(llistCardsArray);
					printCardStack(llistCardsArray, null); // print the card layout

				}
				
				Thread.sleep(timer); // timer to prevent console from overwhelming user *adjustable for preference* set to 3 seconds
				
			}while(priorityCards != null);
		
			// if no more plays are left, exit program with message
			System.out.println("\nNo More Plays Left !! Thank you for playing Scorpion");
			System.out.println("\nThis has been a demonstration algorithm which will play scorpion based on a priority calculation to a user playable option");

			
		}catch(Exception e)  // capture and print any errors associated to program execution
		{
			e.printStackTrace();
		}
	}
	
	// function to print card stack layout in scorpion
	private static void printCardStack(LinkedList<Card>[] llistCardsArray,  Card[] match) {

		for( int i = 0 ; i < 52; i++) 
		{
			int h = 0;
			//System.out.println();
			if( i <= 40-llistCardsArray[h].size() ) System.out.println(); 
			for(LinkedList<Card> lc : llistCardsArray)
			{
				
				if(h == 7 && (i > 2)) { continue;}
				
				Card c = null;
				
				try{ 
					c = lc.get(i);
				}catch(Exception ex) 
				{ 
					System.out.print("      "); 
					continue; 
				}
				
				if(c.isFaceDown()) 
				{
					System.out.print( "**    " );
				}
				else if(match != null && isCardMatch(c, match)) 
				{
					System.out.print(
							((c.getValue() == '1')? "["+(c.getValue()+"0"+c.getGroup()+"] ") : "["+(c.getValue())+c.getGroup()+"]  ")	
						);
				}
				else {
					System.out.print(
							((c.getValue() == '1')? ""+(c.getValue()+"0"+c.getGroup()+"   ") : ""+(c.getValue())+c.getGroup()+"    ")	
						);
				}
				h++;
				
			}
			
		}
		
	}
	
	// function to create new datastructure after a swap is initiated by algorithm based on optional plays
	private static LinkedList<Card>[] priorityBasedSwap(PriorityCard[] pc, LinkedList<Card>[] llistCardsArray) {
		PriorityCard greatest = pc[0];
		for(PriorityCard c : pc) 
		{
			if(c == null) { break;}
			greatest = (greatest.getPriority() < c.getPriority())? c : greatest;
		}
		
		System.out.println("Best Priority Card Determined: "+
				((greatest.getValue() == '1')? ""+(greatest.getValue()+"0"+greatest.getGroup()+"   ") : ""+(greatest.getValue())+greatest.getGroup()+"    ")		);
		
		int listIndex = findListIndex(getMatchAbove(greatest), llistCardsArray);
		
		int listSize = llistCardsArray[greatest.getHorizontal()].size();
		for( int i = greatest.getVertical(); i < listSize; i++ ) 
		{
			llistCardsArray[listIndex].add(
					llistCardsArray[greatest.getHorizontal()].get(greatest.getVertical())
					);
			
			llistCardsArray[greatest.getHorizontal()].remove(greatest.getVertical());
		}
		
		return flipLastFaceDownCard(llistCardsArray); // if card reaches a facedown, flip the face down card in data-struture
	}
	
	// find card in list array and return the index column within linked List array
	private static int findListIndex(Card c, LinkedList<Card>[] llcArray ) {
		for(int i = 0 ; i < llcArray.length; i++) {
			
			LinkedList<Card> checkList = llcArray[i];
			if(c == null && checkList.isEmpty()) return i;
			if( c != null)
				for(Card x : checkList) 
					if ( x.getGroup() == c.getGroup() && x.getValue() == c.getValue() && x.isFaceDown() == c.isFaceDown()) 
						return i;
		}
		
		return -1;
	}
	
	// function to check playable user option cards
	private static PriorityCard[] checkCurrentPlayOptions(LinkedList<Card>[] llistCardsArray) {
		int i = 0;
		int x = 0;
		Card[] matchCards = new Card[20];
		
		for(LinkedList<Card> llc : llistCardsArray) {
			if(x == 7) continue;
			Card checkLast = null;
			if(llc.isEmpty()) {
				for(char c : CARD_GROUPS)
					matchCards[i++] = new Card('K', c, false);
				
				continue;
			}
				 
			checkLast = llc.getLast();

			
			Card cMatch = (checkLast.isFaceDown())? null: getMatchBelow(checkLast);
			
			if((cMatch != null)) matchCards[i] = cMatch;
			i++;
			x++;
		}
		
		return findPriorityListMatch(llistCardsArray, matchCards);
		
		
	}
	
	// function to unveal 3-isolated face down cards and place them face up in the first 3 Linked Lists within array 
	private static LinkedList<Card>[] unveal3FaceDownCards(LinkedList<Card>[] llistCardsArray){
		if(llistCardsArray[7].isEmpty()) return llistCardsArray;
				
		Card c = llistCardsArray[7].removeFirst();
		c.setFaceDown(false);
		
		llistCardsArray[0].add(c);
		
		c = llistCardsArray[7].removeFirst();
		c.setFaceDown(false);
		
		llistCardsArray[1].add(c);
		
		c = llistCardsArray[7].removeFirst();
		c.setFaceDown(false);
		
		llistCardsArray[2].add(c);
		
		return llistCardsArray;
	}
	
	// function to flip last face-down card within the list, if the last card in list is a face-down unless it's within the deck
	private static LinkedList<Card>[] flipLastFaceDownCard(LinkedList<Card>[] llistCardsArray){
		int i = 0;
		for(LinkedList<Card> llc :llistCardsArray ) {
			if(llc.isEmpty() || i == 7) { continue; }
			if(llc.peekLast().isFaceDown()) {llistCardsArray[i].getLast().setFaceDown(false); }
			i++;
		}
		
		return llistCardsArray;
	}
	
	// function to create a priority object based on playable cards, containing information about algorithms choice
	private static PriorityCard[] findPriorityListMatch(LinkedList<Card>[] llistCardsArray, Card[] cards) {
		int i = 0;
		int h = 0;
		int v = 0;
		PriorityCard[] result = new PriorityCard[20];
		for(LinkedList<Card> l : llistCardsArray) {
			for(Card c : cards) {
				if(c == null) {break;}
				
				v = 0;
				for(Card lc : l) {
					if(lc.getValue() == c.getValue() && lc.getGroup() == c.getGroup() && lc.isFaceDown() == c.isFaceDown()) 
					{ 
						PriorityCard pc = new PriorityCard(c); 
						pc.setPriority( calculatePriority(c, l) );
						pc.setHorizontal(h);
						pc.setVertical(v);
						result[i] = pc;
						i++;
					}
					v++;
				}
				
			}
			h++;
		}
		
		return (result[0] == null)? null: result;

	}
	
	// function to calculate the priority of a play based on properties of face down cards, bottom cards, position in list & consecutive matches in list
	private static double calculatePriority( Card card, LinkedList<Card> llistCards ) {
		int faceDownCards = 0;
		int numberOfBottomCards = 0;
		int numberOfConsecutiveMatches = 0;
		int position = 0;
		
		Card nextCard = null;
		
		int i = 0;
		for(Card c : llistCards) {
			if(c.equals(card)) position = i;
			else if(c.equals(nextCard)) numberOfConsecutiveMatches++;
			else if(c.isFaceDown()) faceDownCards++;
			if(position > 0) numberOfBottomCards++;
			
			nextCard = getMatchBelow(c);
			i++;
		}
		
		return (
				faceDownCards* PRIORITY_CALCULATION_WEIGHTS[0] + 
				numberOfBottomCards* PRIORITY_CALCULATION_WEIGHTS[1]+ 
				(numberOfConsecutiveMatches*PRIORITY_CALCULATION_WEIGHTS[2]) + 
				(llistCards.size() - position)* PRIORITY_CALCULATION_WEIGHTS[3]
		);
	}
	
	
	// find card match in an array
	private static boolean isCardMatch(Card card, Card[] cards) {
		if(cards == null) { return false; }
		for(Card c : cards) { 
			if(c == null) {break;}
			if(
				card.getValue() == c.getValue() && 
				card.getGroup() == c.getGroup() && 
				card.isFaceDown() == c.isFaceDown()
				) 
			{
				return true;
			}
		}
		
		return false;
	}
	
	// function to get card next match, below value
	private static Card getMatchBelow(Card c) {
		int i = 0;
		if(c.getValue() == 'A' ) return null;
		for(char v : CARD_VALUES ) {
			if(c.getValue() == v ) { return new Card(CARD_VALUES[--i], c.getGroup(), false); }
			i++;
		}
		
		return null;
	}
	
	// function to get card previous match, above value
	private static Card getMatchAbove(Card c) {
		int i = 0;
		if( c.getValue() == 'K') return null;
		for(char v : CARD_VALUES ) {
			if(c.getValue() == v ) { return new Card(CARD_VALUES[++i], c.getGroup(), false); }
			i++;
		}
		return null;
	}
	
	// function to read input text and configure LinkdListArray object
	private static void setCardsScorpion(BufferedReader reader, LinkedList<Card>[] llistCardsArray) throws Exception {
		
		int ch;
		int arrayPosition = 0;
		int cardPosition = 0;
		
		LinkedList<Card> llistCard = new LinkedList<Card>();
		Card card = new Card();
		
		while((ch = reader.read()) != -1) {
			char character = (char)ch;
			
			if( character == ' ' ) 
			{ 
				if( cardPosition == 6 ) 
				{
					llistCard = new LinkedList<Card>();
					arrayPosition++;
					cardPosition = 0;
				}
				else 
				{
					cardPosition++;
				}
				
				card = new Card();
				character = (char)reader.read();
			}
			
			if( isValue(character) ) 
			{
				card.setFaceDown(setFaceDownOnStart(arrayPosition, cardPosition));
				card.setValue(character);
				
				while(!isGroup(character)) { character = (char) reader.read(); }
				
				card.setGroup( character );
				llistCard.add(cardPosition,card);
				llistCardsArray[arrayPosition] =  llistCard;

			}
			
			
		}
	}
	
	// function to validate if character in text file is a card group 
	private static boolean isGroup(char g) {
		for( char c : CARD_GROUPS ) {
			if(g == c) { return true; }
			continue;
		}
	
		return false;
	}
	
	// function to validate if character in text file is a card value
	private static boolean isValue(char v) {
		for( char c : CARD_VALUES ) {
			if(v == c) { return true; }
			continue;
		}
		return false;
	}
	
	// function to face first 3 cards within the 4 column of cards face-down
	private static boolean setFaceDownOnStart(int arrayPos, int cardPos ) {
		if(arrayPos == 7) return true;
		if( (arrayPos == 0 || arrayPos == 1 || arrayPos == 2 || arrayPos == 3) && (cardPos == 0 || cardPos == 1 || cardPos == 2) )
				return true;
		else return false;	
	}

}

// class object card
class Card{
	
	public Card() {}
	public Card(char v, char g, boolean f) {
		this.value = v;
		this.group = g;
		this.facedown = f;
	}
	private char value;
	private char group;
	private boolean facedown;
	
	public char getValue(){ return this.value; }
	public char getGroup() { return this.group; }
	public boolean isFaceDown() { return this.facedown; }
	
	public void setValue(char v) { this.value = v; }
	public void setGroup(char g) { this.group = g; }
	public void setFaceDown(boolean f) { this.facedown = f; }

	
}

// class object priority card
class PriorityCard extends Card{
	
	private double priority;
	private int horizontal;
	private int vertical;
	
	public PriorityCard() { super();}
	public PriorityCard(Card c) { 
		super.setValue( c.getValue());
		super.setGroup(c.getGroup());
		super.setFaceDown(c.isFaceDown());
  }

	public PriorityCard(char v, char g, boolean f) {
		super.setValue(v);
		super.setGroup(g);
		super.setFaceDown(f);
	}
	
	public void setPriority(double p ) { this.priority = p;}
	public void setHorizontal(int h ) { this.horizontal = h;}
	public void setVertical(int v ) { this.vertical = v;}

	public double getPriority() { return this.priority; }
	public int getHorizontal() { return this.horizontal; }
	public int getVertical() { return this.vertical; }


	
}



