// Internal action code for project CM30174_50206-master

package actions;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class calc_start_point extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
    	int mapWidth = (int) ((NumberTerm) args[0]).solve();
        int mapHeight = (int) ((NumberTerm) args[1]).solve();
        int numAgents = (int) ((NumberTerm) args[2]).solve() - 2;
        int position = (int) ((NumberTerm) args[3]).solve();

        int widthDis = (int) Math.ceil(mapWidth / numAgents);
        int heightDis = (int) Math.ceil(mapHeight / numAgents);
        
        int x;
        int y;
        
        int halfNumAgents = (int) Math.ceil(numAgents / 2);
        
        if(position <= halfNumAgents) {
        	x = widthDis * position;
        	y = heightDis * position;
        } else {
        	x = mapWidth - (widthDis * (position - halfNumAgents));
        	y = mapHeight - (heightDis * (position - halfNumAgents));
        }
        
        return un.unifies(new NumberTermImpl(x), args[4]) && un.unifies(new NumberTermImpl(y), args[5]);
    }
}
