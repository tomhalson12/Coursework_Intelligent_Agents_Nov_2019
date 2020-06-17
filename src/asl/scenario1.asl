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
		+ searches_to_do(500);
		+ reverse(0);
		+ free.

+ free : true <-
		- collecting;
		- depositing;
		- collecting_res(_,_);
		- searching;
		! choose_task.

+ searches_to_do(S) : S == 0 <-
		+ scanned_area.

//carrying resource, so deposit it
+! choose_task : carrying(C) & C > 0 <-
		- free
		! deposit_load.

//known resource, go get it		
+! choose_task : gold(X, Y, _) & not collecting_res(_, _) <-
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

//if not resource found continue searching
+ resource_not_found : true <- ! searching.

//if res is found and not already collecting a some, move to the res
@resource[atomic]
+ resource_found("Gold", Q, X, Y) : true <-
		actions.convert_distance(X, Y, X2, Y2);
		? last_scan(SCAN_X, SCAN_Y);
		actions.convert_to_coord(SCAN_X + X2, SCAN_Y + Y2, RES_X, RES_Y);
		! handle_res(RES_X, RES_Y, Q).
		
+! handle_res(X, Y, Q) : not collecting_res(_, _) & gold(X, Y, Q) <-
		- searching;
		+ collecting_res(X, Y);
		!! move_to_res(X, Y).

+! handle_res(X, Y, Q) : not collecting_res(_, _) & not gold(X, Y, _) <-
		+ gold(X, Y, Q);
		- searching;
		+ collecting_res(X, Y);
		!! move_to_res(X, Y).
		
+! handle_res(X, Y, Q) : true <-
		- gold(X, Y, _);
		+ gold(X, Y, Q).

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
		- collecting_res(_, _);
		+ free.

+! deposit_res : true <-
		deposit(G).
		
-! deposit_res : invalid_action(deposit, unmet_requirement) <-
		- depositing;
		- collecting_res(_, _).
		
-! deposit_res : true <-
		- depositing;
		- collecting_res(_, _);
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