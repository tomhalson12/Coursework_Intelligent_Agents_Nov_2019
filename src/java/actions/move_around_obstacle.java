// Internal action code for project CM30174_50206-master

package actions;

import java.util.ArrayList;
import java.util.Arrays;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class move_around_obstacle extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
    	int toBaseX = (int) ((NumberTerm) args[0]).solve();
    	int toBaseY = (int) ((NumberTerm) args[1]).solve();
    	
    	int movedXDistance = (int) ((NumberTerm) args[2]).solve();
    	int movedYDistance = (int) ((NumberTerm) args[3]).solve();
    	
    	int toGoalXDistance = (int) ((NumberTerm) args[4]).solve();
    	int toGoalYDistance = (int) ((NumberTerm) args[5]).solve();
    	
    	int toGoalXDirection = calcDirection(toGoalXDistance);
    	int toGoalYDirection = calcDirection(toGoalYDistance);
    	
    	int currentCoordX = MapState.convertToPositive(-toBaseX, MapState.getMapWidth());
    	int currentCoordY = MapState.convertToPositive(-toBaseY, MapState.getMapHeight());
    	
    	int shortestDistance = 10000;
		int[] shortestSquare = null;
		
		ArrayList<int[]> squaresToCheck = new ArrayList<int[]>();
		ArrayList<int[]> squaresChecked = new ArrayList<int[]>();
		
		int[] start = new int[2];
		start[0] = toGoalXDirection;
		start[1] = toGoalYDirection;
		squaresToCheck.add(start);
		
		while(true) {
			ArrayList<int[]> adjacentSquares = new ArrayList<int[]>();

			for(int i = 0; i < squaresToCheck.size(); i++) {
				int[] squareToCheck = squaresToCheck.get(i);
				squaresToCheck.remove(i);
				squaresChecked.add(squareToCheck);

				for(int[] adjSq : getAdjacentSquares(squareToCheck[0], squareToCheck[1])){
					boolean alreadyChecked = false;
			
					for(int[] prevSq : squaresChecked){
						if(Arrays.equals(adjSq, prevSq)){
							alreadyChecked = true;
						} else if (adjSq[0] == -movedXDistance && adjSq[1] == -movedYDistance) {
							alreadyChecked = true;
						}
					}

					if(!alreadyChecked){
						adjacentSquares.add(adjSq);
					}
				}
	    	
				for(int[] adjSquare : adjacentSquares) {
					int[] coord = getCoord(adjSquare, currentCoordX, currentCoordY);
					
					if(!MapState.isObstacle(coord[0], coord[1], ts.getUserAgArch().getAgName())) {
						int stepsToGoal = Math.abs(squareToCheck[0] - adjSquare[0] + toGoalXDistance) + Math.abs(squareToCheck[1] - adjSquare[1] + toGoalYDistance);
	    				if(stepsToGoal < shortestDistance) {
	    					shortestSquare = adjSquare;
	    				}
					}
				}	
			}

			if( shortestSquare != null) {
				return un.unifies(new NumberTermImpl(shortestSquare[0]), args[6]) && un.unifies(new NumberTermImpl(shortestSquare[1]), args[7]);					
			}
			squaresToCheck.addAll(adjacentSquares);
		}
		
    }
   
    private ArrayList<int[]> getAdjacentSquares(int x, int y){
    	int[] adj1 = new int[2];
    	int[] adj2 = new int[2];
    	
    	if(x == 0) {
    		adj1[0] = -1;
    		adj1[1] = y;
    		
    		adj2[0] = 1;
    		adj2[1] = y;
    	} else if (y == 0) {
    		adj1[0] = x;
    		adj1[1] = -1;
    		
    		adj2[0] = x;
    		adj2[1] = 1;
    	} else {
    		adj1[0] = 0;
    		adj1[1] = y;
    		
    		adj2[0] = x;
    		adj2[1] = 0;
    	}
    	
    	ArrayList<int[]> adjSqs = new ArrayList<int[]>();
    	adjSqs.add(adj1);
    	adjSqs.add(adj2);
    	
    	return adjSqs;
    }
    
    private int calcDirection(int d) {
    	if(d < 0) {
    		return -1;
    	} else if (d > 0) {
    		return 1;
    	}
    	
    	return 0;
    }
    
    private int[] getCoord(int[] direction, int originalX, int originalY) {
    	int[] coord = new int[2];
    	coord[0] = MapState.convertToPositive(originalX + direction[0], MapState.getMapWidth());
    	coord[1] = MapState.convertToPositive(originalY + direction[1], MapState.getMapHeight());
    	
    	return coord;
    }
}
