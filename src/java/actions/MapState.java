package actions;

import java.util.ArrayList;
import java.util.HashMap;

public class MapState {
	
	static HashMap<String, int[]> movementMap = new HashMap<String, int[]>();
	static HashMap<String, boolean[][]> obstaclesMap = new HashMap<String, boolean[][]>();
	static int mapWidth = 0;
	static int mapHeight = 0;
	
	static void setMapDimensions(int width, int height, String agentName) {
		mapWidth = width;
		mapHeight = height;
		
		int[] moves = new int[2];
		moves[0] = 0;
		moves[1] = 0;
		
		movementMap.put(agentName, moves);
		
		obstaclesMap.put(agentName, new boolean[width][height]);
	}
	
	static int getMapWidth() {
		return mapWidth;
	}
	
	static int getMapHeight() {
		return mapHeight;
	}
	
	static void addMove(int x, int y, String agentName) {
		int[] moves = movementMap.get(agentName);
		
		moves[0] = moves[0] + x;
		moves[1] = moves[1] + y;
		
		movementMap.replace(agentName, moves);
	}
	
	static void clearMoves(String agentName) {
		int[] moves = movementMap.get(agentName);
		
		moves[0] = 0;
		moves[1] = 0;
		
		movementMap.replace(agentName, moves);
	}
	
	static void addObstacle(int x, int y, String agentName) {
		int pX = convertToPositive(x, mapWidth);
		int pY = convertToPositive(y, mapHeight);
		
		boolean[][] obstacles = obstaclesMap.get(agentName);
		
		if(!obstacles[pX][pY]) {
			obstacles[pX][pY] = true;
		}
		
		obstaclesMap.replace(agentName, obstacles);
	}
	
	static boolean isObstacle(int x, int y, String agentName) {
		int pX = convertToPositive(x, mapWidth);
		int pY = convertToPositive(y, mapHeight);
		
		return obstaclesMap.get(agentName)[pX][pY];
	}
	
	static int convertToPositive(int d, int limit) {
		if(d < 0) {
			return limit + d;
		} else if(d % limit == 0) {
			return 0;
		}
		
		return d;
	}
	
	static ArrayList<Integer> getBase(String agentName){
		int[] moves = movementMap.get(agentName);
		
		int x = MapState.convertDistance(moves[0], mapWidth);
		int y = MapState.convertDistance(moves[1], mapHeight);
		
		ArrayList<Integer> base = new ArrayList<Integer>();
		base.add(x);
		base.add(y);
		
		return base;
	}
	
	static int convertDistance(int distance, int limit) {
		if(distance == 0) {
			return 0;
		}
		
		int newD = distance;
		
        if(Math.abs(distance) > limit) {
        	newD = Math.abs(distance) % limit;
        	
        	if(distance < 0) {
        		newD = newD * -1;
        	}
        }
        
        if(Math.abs(newD) > (limit / 2)) {
        	if(newD < 0) {
        		return (limit + newD) * -1;
        	} else {
        		return limit - newD;
        	}
        }
        
        return newD * -1;
	}
}
