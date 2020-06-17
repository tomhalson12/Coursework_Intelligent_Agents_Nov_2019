// Internal action code for project CM30174_50206-master

package actions;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class set_map_size extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        int width = (int) ((NumberTerm) args[0]).solve();
        int height = (int) ((NumberTerm) args[1]).solve();
     	
        MapState.setMapDimensions(width, height, ts.getUserAgArch().getAgName());
       
        return true;
    }
}
