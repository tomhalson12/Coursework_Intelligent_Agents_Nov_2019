! plan.

//setup
+! plan : true <-
		rover.ia.get_config(C, R, S, P);
		rover.ia.get_map_size(W, H);
		actions.set_map_size(W, H);
		+ carrying(0);
		+ max_capacity(C);
		+ max_speed(S);
		+ max_scan(R);
		actions.calc_searches_to_do(W, H, 6, 5, N);
		+ searches_to_do(N);
		actions.calc_start_point(W, H, 6, 2, S_X, S_Y);
		+ prev_scan_loc(S_X, S_Y);
		+ reverse(0);
		+ free.

+ free : true <-
		- collecting;
		- depositing;
		- collecting_res(_,_);
		- searching;
		- others(_);
		- movement_obs;
		- evading_move(_, _);
		- goal(_, _);
		- target(_, _);
		! choose_task.

+ searches_to_do(S) : S == 0 <-
		+ scanned_area.

//carrying resource, so deposit it
+! choose_task : carrying(C) & C > 0 <-
		- free
		! deposit_load.

//known resource, go get it		
+! choose_task : gold(X, Y, _) & not collecting_res(_, _) & not gold_being_collected(X, Y) <-
		- free;
		! handle_res(X, Y, Q).
		
+! choose_task : scanned_area <-
		- free;
		- scanned_area;
		- searches_to_do(_);
		+ searches_to_do(1);
		! searching.

//no known res, start searching
+! choose_task : true <-
		- free;
		! searching.
		
+! searching : scanned_area <-
		+ free.
		
//use the previous scan location to work out the next scan location and move to it
+! searching : prev_scan_loc(X, Y) <-
		+ searching;
		? reverse(R);
		actions.get_next_scan_location(X, Y, 5, R, SCAN_LOC_X, SCAN_LOC_Y);
		- next_scan_loc(_, _);
		+ next_scan_loc(SCAN_LOC_X, SCAN_LOC_Y);
		actions.distance_from_current_location(SCAN_LOC_X, SCAN_LOC_Y, X2, Y2);
		actions.add_move(X2, Y2);
		? max_speed(S);
		move(X2, Y2, S).
		
//if no previous scan location, start from the base
+! searching : not prev_scan_loc(_, _) <-
		+ searching;
		- next_scan_loc(_, _);
		+ next_scan_loc(0, 0);
		actions.get_base(X, Y);
		actions.clear_moves;
		? max_speed(S);
		move(X, Y, S).
		
-! searching : true <-
		.print('Searching failed - movement obstructed').
		
+ action_completed(move) : movement_obs <-
		- action_completed(move)[source(percept)];
		- movement_obs;
		! move_to_target.

+ action_completed(move) : collecting <-
		! pickup_res.
		
+ action_completed(move) : depositing <-
		! deposit_res.
		
//scan after moving to scan location		
+ action_completed(move) : searching <-
		actions.get_base(BASE_X, BASE_Y);
		actions.convert_from_base_to_coord(BASE_X, BASE_Y, X, Y);
		- last_scan(_, _);
		+ last_scan(X, Y);
		- next_scan_loc(_, _);
		- prev_scan_loc(_, _);
		+ prev_scan_loc(X, Y);
		- searches_to_do(S);
		+ searches_to_do(S - 1);
		? max_scan(R);
		scan(R).
		
+ found_gold(X, Y, Q) : gold(X, Y, _) <-
		- found_gold(X, Y, Q)[source(_)].
		
+ found_gold(X, Y, Q) : true <-
		- found_gold(X, Y, Q)[source(_)];
		+ gold(X, Y, Q).
		
+ found_diamond(X, Y, Q) : true <-
		- found_diamond(X, Y, Q)[source(_)].	
		
+ picked_gold(X, Y) : true <-
		- gold(X, Y, _);
		- picked_gold(X, Y)[source(_)].
		
+ collected_gold(X, Y) : true <-
		- gold_being_collected(X, Y)[source(_)];
		- collected_gold(X, Y)[source(_)].
		
+ collected_diamond(X, Y) : true <-
		- collected_diamond(X, Y)[source(_)].
		
+ picked_diamond(X, Y) : true <-
		- picked_diamond(X, Y)[source(_)].
		
+ diamond_being_collected(X, Y) : true <-
		- diamond_being_collected(X, Y)[source(_)].

+ resource_not_found : movement_obs <-
		! move_around_obs.

//if not resource found continue searching
+ resource_not_found : true <- ! searching.

//if res is found and not already collecting a some, move to the res
@resource[atomic]
+ resource_found("Gold", Q, X, Y) : true <-
		! count_others;
		actions.convert_distance(X, Y, X2, Y2);
		? last_scan(SCAN_X, SCAN_Y);
		actions.convert_to_coord(SCAN_X + X2, SCAN_Y + Y2, RES_X, RES_Y);
		! handle_res(RES_X, RES_Y, Q).
		
@resource_obstacle[atomic]
+ resource_found(T, Q, X, Y) : T \== "Gold" <-
		! count_others;
		actions.convert_distance(X, Y, X2, Y2);
		? last_scan(SCAN_X, SCAN_Y);
		actions.convert_to_coord(SCAN_X + X2, SCAN_Y + Y2, OTH_X, OTH_Y);
		! handle_other(T, Q, OTH_X, OTH_Y).	
		
+! handle_res(X, Y, Q) : movement_obs & others(0) <-
		- gold(X, Y, _);
		+ gold(X, Y, Q);
		.broadcast(tell, found_gold(X, Y, Q));
		! move_around_obs.	
		
+! handle_res(X, Y, Q) : not collecting_res(_, _) & gold(X, Y, Q) & not movement_obs & not gold_being_collected(X, Y) <-
		- searching;
		+ collecting_res(X, Y);
		.broadcast( tell, gold_being_collected(X, Y));
		!! move_to_res(X, Y).

+! handle_res(X, Y, Q) : not collecting_res(_, _) & not gold(X, Y, _) & not movement_obs & not gold_being_collected(X, Y) <-
		+ gold(X, Y, Q);
		- searching;
		+ collecting_res(X, Y);
		.broadcast(tell, found_gold(X, Y, Q));
		.broadcast( tell, gold_being_collected(X, Y));
		!! move_to_res(X, Y).
		
+! handle_res(X, Y, Q) : true <-
		- gold(X, Y, _);
		+ gold(X, Y, Q);
		.broadcast(tell, found_gold(X, Y, Q)).

+! move_to_res(X, Y) : true <-
		+ collecting;
		actions.distance_from_current_location(X, Y, X2, Y2);
		actions.add_move(X2, Y2);
		? max_speed(S);
		move(X2, Y2, S).

-! move_to_res(X, Y) : true <-
		.print('Moving to res failed - nagivating obstacle').
		
+! pickup_res : collecting_res(X, Y) & gold(X, Y, 0) <-
		- gold(X, Y, 0);
		.broadcast( tell, picked_gold(X, Y));
		- collecting;
		!! deposit_load.
		
+! pickup_res : carrying(C) & max_capacity(C) <-
		- collecting
		!! deposit_load.
		
+! pickup_res : collecting_res(X, Y) & gold(X, Y, Q) <-
		collect(G).

-! pickup_res : true <-
		- collecting.
		
+ action_completed(collect) : collecting_res(X, Y) & gold(X, Y, Q) & Q > 0 <-
		- action_completed(collect)[source(percept)];
		- carrying(C);
		+ carrying(C + 1);
		- gold(X, Y, Q);
		+ gold(X, Y, Q - 1);
		! pickup_res.
		
+ invalid_action(collect, unmet_requirement) : collecting <-
		- invalid_action(collect, unmet_requirement)[source(percept)];
		? collecting_res(X, Y);
		- gold(X, Y, _);
		.broadcast( tell, picked_gold(X, Y));
		- collecting;
		+ free.	
	
+! deposit_load : carrying(0) <-
		+ free.
		
+! deposit_load : true <-
		+ depositing;
		! return_to_base.	
		
+! return_to_base : true <-
		actions.get_base(BASE_X, BASE_Y);
		actions.clear_moves;
		? max_speed(S);
		move(BASE_X, BASE_Y, S).
		
-! return_to_base : true <-
		.print('Returning to base failed - Navigating obstacle').
		
+! deposit_res : carrying(0) <-
		- depositing;
		- collecting_res(X, Y);
		.broadcast( tell, collected_gold(X, Y));
		+ free.

+! deposit_res : true <-
		deposit(G).
		
-! deposit_res : invalid_action(deposit, unmet_requirement) <-
		- depositing;
		- collecting_res(X, Y);
		.broadcast( tell, collected_gold(X, Y)).
		
-! deposit_res : true <-
		- depositing;
		- collecting_res(X, Y);
		.broadcast( tell, collected_gold(X, Y));
		+free.

+ action_completed(deposit) : depositing & carrying(C) & C > 0 <-
		- action_completed(deposit)[source(percept)];
		- carrying(C);
		+ carrying(C - 1);
		! deposit_res.
		
+ invalid_action(deposit, unmet_requirement) : depositing <-
		- invalid_action(deposit, unmet_requirement)[source(percept)];
		- carrying(C);
		+ carrying(0);
		- depositing;
		+ free.	

+! count_others : not others(_) & .count(resource_found("Diamond",_, _,_), D) & .count(resource_found("Obstacle", _, _, _), O) & D + O > 0 <-
		+ others(D + O).
		
+! count_others : true <-
		? max_scan(R).
		
+! handle_other(T, Q, X, Y) : T == "Diamond" <-
		- others(O);
		+ others(O - 1);
		.broadcast(tell, found_diamond(X, Y, Q));
		! choose_action.
		
+! handle_other(T, Q, X, Y) : T == "Obstacle" & movement_obs <-
		- others(O);
		+ others(O - 1);
		actions.add_obstacle(X, Y);
		! choose_action.
		
+! handle_other(T, Q, X, Y) : true <-
		- others(O);
		+ others(O - 1);
		! choose_action.
		
+! choose_action : not others(0) <-
		? max_scan(R).
		
+! choose_action : movement_obs <-
		- others(_);
		! move_around_obs.

+! choose_action : collecting_res(_, _) | .count(resource_found("Gold", _, _, _) ,C) & C > 0 <-
		- others(_).
		
+! choose_action : true <-
		- others(_);
		! searching.
		
+ movement_obstructed(MOVED_X, MOVED_Y, TO_GOAL_X, TO_GOAL_Y) : searching & TO_GOAL_X <= 1 &  TO_GOAL_X >= -1 & TO_GOAL_Y <= 1 &  TO_GOAL_Y >= -1 <-
		- evading_move(_, _);
		actions.convert_distance(TO_GOAL_X, TO_GOAL_Y, X_NOT_MOVED, Y_NOT_MOVED);
		actions.add_move(-X_NOT_MOVED, -Y_NOT_MOVED);
		! find_new_scan_loc.
		
+ movement_obstructed(MOVED_X, MOVED_Y, TO_GOAL_X, TO_GOAL_Y) : true <-
		+ movement_obs;
		actions.convert_distance(TO_GOAL_X, TO_GOAL_Y, X, Y);
		+ goal(X, Y);
		actions.add_move(-X, -Y);
		? max_scan(R);
		actions.get_base(BASE_X, BASE_Y);
		actions.convert_from_base_to_coord(BASE_X, BASE_Y, SCAN_X, SCAN_Y);
		- last_scan(_, _);
		+ last_scan(SCAN_X, SCAN_Y);
		scan(R).
		
+! move_around_obs : evading_move(EVADING_X, EVADING_Y) & goal(TO_GOAL_X, TO_GOAL_Y) <-
		! evading_move(EVADING_X, EVADING_Y).
		
+! move_around_obs : goal(TO_GOAL_X, TO_GOAL_Y) <-
		! evading_move(-10, -10).
		
-! move_around_obs : true <-
		+ free.
		
+! evading_move(PREV_EVADING_X, PREV_EVADING_Y) : goal(TO_GOAL_X, TO_GOAL_Y) <-
		- goal(_, _);
		actions.get_base(BASE_X, BASE_Y)
		actions.move_around_obstacle(BASE_X, BASE_Y, PREV_EVADING_X, PREV_EVADING_Y, TO_GOAL_X, TO_GOAL_Y, X, Y);
		actions.add_move(X, Y);
		- evading_move(_, _);
		+ evading_move(X, Y);
		+ target(TO_GOAL_X - X, TO_GOAL_Y - Y);
		? max_speed(S);
		move(X, Y, S).
		
+! move_to_target : target(X, Y) <-
		- target(X, Y);
		actions.add_move(X, Y);
		? max_speed(S);
		move(X, Y, S);
		- evading_move(_, _).
		
-! move_to_target : true <-
		.print('Moving to target failed - Navigating obstacle').
		
+! find_new_scan_loc : true <-
		- evading_move(_, _);
		- goal(_, _);
		- target(_,_)
		? reverse(R);
		? prev_scan_loc(PREV_SCAN_X, PREV_SCAN_Y);
		? next_scan_loc(NEXT_SCAN_X, NEXT_SCAN_Y);
		actions.scan_loc_blocked(NEXT_SCAN_X, NEXT_SCAN_Y, 5, PREV_SCAN_X, PREV_SCAN_Y, R, NEW_X, NEW_Y);
		actions.distance_from_current_location(NEW_X, NEW_Y, DIS_X, DIS_Y);
		- next_scan_loc(NEXT_SCAN_X, NEXT_SCAN_Y);
		+ next_scan_loc(NEW_X, NEW_Y);
		? max_speed(S);
		actions.add_move(DIS_X, DIS_Y);
		move(DIS_X, DIS_Y, S).
		
-! find_new_scan_loc : true <-
		.print('navigating obstacle while moving to new scan loc').