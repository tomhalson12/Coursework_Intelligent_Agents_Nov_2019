// Internal action code for project CM30174_50206-master

package actions;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class calc_searches_to_do extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
    	int mapWidth = (int) ((NumberTerm) args[0]).solve();
        int mapHeight = (int) ((NumberTerm) args[1]).solve();
        int numAgents = (int) ((NumberTerm) args[2]).solve();
        int scanWidth = (int) ((NumberTerm) args[3]).solve();
        
        int num = (int) Math.ceil(((mapWidth / scanWidth) * (mapHeight / scanWidth)) / numAgents);

        return un.unifies(new NumberTermImpl(num), args[4]);
    }
}
