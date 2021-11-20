This Program Is an algorithm for a not-so popular card game extension of solitaire known as Scorpion.

Instructions:

	PLEASE EXPAND TERMINAL TO SEE FULL OUTPUT OF THE PROGRAM !
	
	YOU MAY ALSO Un-Comment (Lines 54-59) in order to view the output step by step in an output file : output.txt 

    You may execute this program by inserting an object file within the same directory as program, 
    containing a list of ordered card pairs similar to text file
    given as :  ScorpionRequiredInput.txt
    
    The text file is expected to be similar to the one above, you may re-arrange order 
    but format and spacing must be THE SAME! in order to execute this program
    
    Some test files has been created for this program:
          testfile1.txt
          testfile2.txt
          testfile3.txt
    
    with a resulting output files:
    
    		test_output1.txt
    		test_output2.txt
    		test_output3.txt
    		
   Analysis has been conducted in Document Scorpion_Analysis.pdf
    

The algorithm is designed to auto-play the game until no more moves are available.

The algorithm is not designed to win the game, rather it's a demonstration on how to play the game.

The algorithm is a best attempt effort solution

As a developer, I am not familiar with the game but decided to program a good alternative solution 

This algorithm will make decisions based on a priority weight of the optional plays a user can make

The decision is made in a function : calculatePriority   

You can adjust the priority of different weights by modifying the multipliers to each weight inside the function.

For example:

1) The default weight calculation on return is based on static object: PRIORITY_CALCULATION_WEIGHTS

	private static double[] PRIORITY_CALCULATION_WEIGHTS = { 10.0, 1.0, 2.0, 1.0 };

          .... return of calculatePriority Function 
      		return (
				faceDownCards* PRIORITY_CALCULATION_WEIGHTS[0] + 
				numberOfBottomCards* PRIORITY_CALCULATION_WEIGHTS[1]+ 
				(numberOfConsecutiveMatches*PRIORITY_CALCULATION_WEIGHTS[2]) + 
				(llistCards.size() - position)* PRIORITY_CALCULATION_WEIGHTS[3]
		);
		
		
		return (
				faceDownCards* 10.0 + 
				numberOfBottomCards* 1.0+ 
				(numberOfConsecutiveMatches*2.0) + 
				(llistCards.size() - position)* 1.0
		);
		
      You may adjust the weights of the multipliers to each object within the return to obtain a different result
      
      .. for example adjusting the weights to 
	private static double[] PRIORITY_CALCULATION_WEIGHTS = { 7.0, 1.5, 1.0, 2.0 };
	
	will reduce the priority of # of faceDownCard & # of consecutive matches property within each list and give 
	slightly more priority to position of card on the list.
	
 	
 	
 Thank you !
 
 Rafat Khandaker
 