package edu.oregonstate.cs361.battleship;

import com.google.gson.Gson;
import spark.Request;
import java.util.List;
import static spark.Spark.*;

public class Main {

    public static void main(String[] args) {
        //This will allow us to server the static pages such as index.html, app.js, etc.
        staticFiles.location("/public");

        //This will listen to GET requests to /model and return a clean new model
        get("/model", (req, res) -> newModel());
        //This will listen to GET requests to /model and return a clean new model
        get("/modelUpdated", (req, res) -> newModelUpdated());
        //This will listen to POST requests and expects to receive a game model, as well as location to fire to
        post("/fire/:row/:col/:num", (req, res) -> fireAt(req));
        //This will listen to POST requests and expects to receive a game model, as well as location to fire to
        post("/fire/:row/:col", (req, res) -> fireAtNew(req));
        //This will handle the scan feature
        post("/scan/:row/:col", (req, res) -> scan(req));

        //This will listen to POST requests and expects to receive a game model, as well as location to place the ship
        post("/placeShip/:id/:row/:col/:orientation/:num", (req, res) -> placeShip(req));
        //This will listen to POST requests and expects to receive a game model, as well as location to place the ship
        post("/placeShip/:id/:row/:col/:orientation", (req, res) -> placeShipUpdated(req));
    }

    //This function should return a new model
    static String newModel() {

        // make new model, make gson object, convert model to json using gson
        BattleshipModelNormal test = new BattleshipModelNormal();
        Gson gson = new Gson();
        String model = gson.toJson(test);
        return model;
    }

    //This function should return a new model
    static String newModelUpdated() {

        // make new model, make gson object, convert model to json using gson
        BattleshipModelUpdated test = new BattleshipModelUpdated();
        Gson gson = new Gson();
        String model = gson.toJson(test);
        return model;
    }

    //This function should accept an HTTP request and deserialize it into an actual Java object.
    public static BattleshipModel getModelFromReq(Request req){

        String data = req.body();
        Gson gson = new Gson();
        BattleshipModelNormal game = gson.fromJson(data, BattleshipModelNormal.class);
        return game;
    }

    //This function should accept an HTTP request and deserialize it into an actual Java object.
    public static BattleshipModel getModelUpdatedFromReq(Request req){

        String data = req.body();
        Gson gson = new Gson();
        BattleshipModelUpdated game = gson.fromJson(data, BattleshipModelUpdated.class);
        return game;
    }

    //This controller should take a json object from the front end, and place the ship as requested, and then return the object.
    public static String placeShipUpdated(Request req) {

        BattleshipModelUpdated model = (BattleshipModelUpdated) getModelUpdatedFromReq(req);
        String id, orientation, row, col, num;
        id = req.params("id");  //name of what ship with which player in front of it
        orientation = req.params("orientation"); //horizontal/vertical
        row = req.params("row");    //row #
        col = req.params("col");    //col #

        List PlayerFireMiss = model.getPlayerMisses();
        List PlayerFireHit = model.getPlayerHits();


        if (!PlayerFireHit.isEmpty() || !PlayerFireMiss.isEmpty()) {//list is empty
            Gson gson = new Gson();
            return gson.toJson(model);
        }

        int rows = Integer.parseInt(row);
        int column = Integer.parseInt(col);

        model.getShipByID(id).setEnd(0, 0);
        model.getShipByID(id).setStart(0, 0);

        Ship[] PArray = model.resetArrayUpdated( model, true);

        int size = model.getShipByID(id).getLength();
        int stop = 0;
        Point cord = new Point();

        if (orientation.equals("horizontal") && (rows + size - 1) < 11 && rows > 0 && column < 11 && column > 0) {
            for (int i = rows; i < (rows + size); i++) {
                cord.setAcross(i);
                cord.setDown(column);

                //if ship lands on another ship then
                for(int l = 0; l < 5; l++) {
                    if(Hit(PArray[l].getStart(), PArray[l].getEnd(), cord)) {
                        stop = 1;
                    }
                }
            }

            if (stop == 0) {
                model.getShipByID(id).setStart(rows, column);
                model.getShipByID(id).setEnd(rows + size - 1, column);
            }

        } else if (orientation.equals("vertical") && rows < 11 && rows > 0 && column + size - 1 < 11 && column > 0) {
            for (int k = column; k < (column + size); k++) {
                cord.setAcross(rows);
                cord.setDown(k);

                //if ship lands on another ship then
                for(int l = 0; l < 5; l++) {
                    if(Hit(PArray[l].getStart(), PArray[l].getEnd(), cord)) {
                        stop = 1;
                    }
                }
            }

            if (stop == 0) {
                model.getShipByID(id).setStart(rows , column);
                model.getShipByID(id).setEnd(rows , column + size - 1);
            }
        }

        //If the player sets down a ship the computer sets down the same ship
        Point computerStart;
        Point computerEnd;
        int computer_x = 0;
        int computer_y = 0;
        int horizontal = 0;
        int computer_length_increase = 0;
        int computerLength = model.getShipByID(id).getLength() - 1;

        model.getShipByID("computer" + id).setStart( 0, 0);
        model.getShipByID("computer" + id).setEnd( 0, 0);
        Ship[] CArray = model.resetArrayUpdated( model, false);


        int movingPoint = 0, stoppedPoint = 0;

        while (stop == 0) {
            computer_x = (int) (Math.random() * 10 + 1);
            computer_y = (int) (Math.random() * 10 + 1);
            horizontal = (int) (Math.random() * 2 + 1);

            computerStart = new Point(computer_x, computer_y);
            computerEnd = new Point(0, 0);
            stop = 1;

            if (horizontal == 1 && computer_x + computerLength < 11) { //horizontal
                computerEnd = new Point(computer_x + computerLength, computer_y);
                computer_length_increase = computerEnd.getAcross();

                movingPoint = computer_x;
                stoppedPoint = computer_y;

            } else if (horizontal == 2 && computer_y + computerLength < 11) { //vertical
                computerEnd = new Point(computer_x, computer_y + computerLength);
                computer_length_increase = computerEnd.getDown();

                movingPoint = computer_y;
                stoppedPoint = computer_x;

            } else {
                stop = 0;
            }


            if (stop == 1) {
                for (movingPoint = movingPoint - 1; movingPoint < (computer_length_increase + 2); movingPoint++) {
                    if (horizontal == 1) {
                        cord.setAcross(movingPoint);
                        cord.setDown(stoppedPoint);

                    } else {
                        cord.setAcross(stoppedPoint);
                        cord.setDown(movingPoint);
                    }

                    //if ship lands on another ship then
                    for(int i = 0; i < 5; i++){
                        if( Hit(CArray[i].getStart(), CArray[i].getEnd(), cord)){
                            stop = 0;
                        }
                    }
                }

                if (stop == 1) {
                    model.getShipByID("computer" + id).setStart( computerStart.getAcross(), computerStart.getDown());
                    model.getShipByID("computer" + id).setEnd( computerEnd.getAcross(), computerEnd.getDown());
                }
            }
        }

        Gson gson = new Gson();
        return gson.toJson(model);

    }

    //Similar to placeShip, but with firing.
    private static String fireAtNew(Request req) {

        // Generate model from json, get coordinates from fire request
        BattleshipModelUpdated model = (BattleshipModelUpdated) getModelUpdatedFromReq(req);

        String X = req.params("row");
        String Y = req.params("col");

        int row = Integer.parseInt(X);
        int col = Integer.parseInt(Y);
        int j = 0;

        // Make point object from coordinates
        Point FireSpot = new Point(row, col);

        // Grab player and computer ships from current model
        Ship[] PArray = model.resetArrayUpdated(model, true);
        Ship[] CArray = model.resetArrayUpdated(model, false);

        for (int i = 0; i < 5; i++) {
            if (PArray[i].getHealth() == 0) {
                model.getShipByID(PArray[i].getName()).setHealth(-1);
            }
            if (CArray[i].getHealth() == 0) {
                model.getShipByID(CArray[i].getName()).setHealth(-1);
            }
        }

        //Player has shot at this place already
        if ( alreadyShot(FireSpot, model, true)) {
            Gson gson = new Gson();
            return gson.toJson(model);
        }

        //If the player hasn't placed all the ships
        for (int i = 0; i < 5; i++) {
            if (PArray[i].getStart().getAcross() < 1) {
                Gson gson = new Gson();
                return gson.toJson(model);
            }
        }

        j = model.getComputerHits().size();
        Point Clipper = new Point((PArray[3].getStart().getAcross() + PArray[3].getEnd().getAcross())/2,(PArray[3].getStart().getDown() + PArray[3].getEnd().getDown())/2);


        // The following branch tree checks if a point fired at
        // BY A PLAYER has hit a COMPUTER ship and adds the point to the array of hits if so
        for (int i = 0; i < 5; i++) {
            if (Hit(CArray[i].getStart(), CArray[i].getEnd(), FireSpot)) {
                /*if(i == 2){
                    model.addPointtoArray(PArray[i].getStart(), model.getComputerHits());
                    model.addPointtoArray(PArray[i].getEnd(), model.getComputerHits());
                    model.addPointtoArray(Clipper, model.getComputerHits());

                }else {*/
                    model.addPointtoArray(FireSpot, model.getComputerHits());
                //}

                model.getShipByID(CArray[i].getName()).setHealth(CArray[i].getHealth() - 1);
            }
        }

        if (j == model.getComputerHits().size()) {
            model.addPointtoArray(FireSpot, model.getComputerMisses());
        }

        model = computerFire(model);

        Gson gson = new Gson();
        return gson.toJson(model);
    }

    private static BattleshipModelUpdated computerFire(BattleshipModelUpdated model) {

        // Create two random coordinates for computer to shoot at and make a point object of them
        Ship[] PArray = model.resetArrayUpdated( model, true);
        Point FireSpotComputer;
        int shootX, shootY, j;

        do{ // Keeps getting random location until it hasn't fired at that spot
            shootX = (int )(Math.random() * 10 + 1);
            shootY = (int )(Math.random() * 10 + 1);
            FireSpotComputer = new Point(shootX, shootY);
        } while ( alreadyShot( FireSpotComputer, model,false));

        j = model.getPlayerHits().size();
        Point Clipper = new Point((PArray[3].getStart().getAcross() + PArray[3].getEnd().getAcross())/2,(PArray[3].getStart().getDown() + PArray[3].getEnd().getDown())/2);


        // The following branch tree checks if a point fired at
        // BY A PLAYER has hit a COMPUTER ship and adds the point to the array of hits if so
        for (int i = 0; i < 5; i++) {
            if (Hit(PArray[i].getStart(), PArray[i].getEnd(), FireSpotComputer)) {
                /*if(i == 2){
                    model.addPointtoArray(PArray[i].getStart(), model.getPlayerHits());
                    model.addPointtoArray(PArray[i].getEnd(), model.getPlayerHits());
                    model.addPointtoArray(Clipper, model.getPlayerHits());

                }else {*/
                    model.addPointtoArray(FireSpotComputer, model.getPlayerHits());
                //}

                model.getShipByID(PArray[i].getName()).setHealth(PArray[i].getHealth() - 1);
            }
        }

        if(j == model.getPlayerHits().size()){
            model.addPointtoArray(FireSpotComputer, model.getPlayerMisses());
        }

        return model;
    }

    //Scans the area for ships
    private static String scan(Request req) {

        // Generate model from json, get coordinates from fire request
        BattleshipModelUpdated model = (BattleshipModelUpdated) getModelUpdatedFromReq(req);

        String X = req.params("row");
        String Y = req.params("col");

        int row = Integer.parseInt(X);
        int col = Integer.parseInt(Y);

        // Make point object from coordinates
        Point[] FireSpot = new Point[] {new Point(row, col), new Point((row-1), col), new Point((row+1), col), new Point(row,(col-1)), new Point(row, (col+1))};

        // Grab player and computer ships from current model
        Ship[] PArray = model.resetArrayUpdated( model, true);
        Ship[] CArray = model.resetArrayUpdated( model, false);

        //Won't fire unless all ships are placed down
        for(int i = 0; i < 5; i++){
            if(PArray[i].getStart().getAcross() < 1){
                Gson gson = new Gson();
                return gson.toJson(model);
            }
        }

        for(int i = 0; i < 5; i++){
            if(PArray[i].getHealth() == 0){
                model.getShipByID( PArray[i].getName() ).setHealth(-1);
            }
            if(CArray[i].getHealth() == 0){
                model.getShipByID( CArray[i].getName() ).setHealth(-1);
            }
        }

        // The following branch tree checks if a point fired at
        // BY A PLAYER has hit a COMPUTER ship and adds the point to the array of hits if so
        for(int j=0; j < 5; j++) {
            if (Hit(CArray[0].getStart(), CArray[0].getEnd(), FireSpot[j])){
                model.setScanned(true);

            } else if (Hit(CArray[3].getStart(), CArray[3].getEnd(), FireSpot[j])) {
                model.setScanned(true);

            } else if (Hit(CArray[4].getStart(), CArray[4].getEnd(), FireSpot[j])) {
                model.setScanned(true);

            }
        }

        //returns the model with computer having fired
        model = computerFire(model);

        Gson gson = new Gson();
        return gson.toJson(model);
    }

    public static String placeShip(Request req) {

        BattleshipModelNormal model = (BattleshipModelNormal) getModelFromReq(req);
        String id, orientation, row, col, num;
        orientation = req.params("orientation"); //horizontal/vertical
        id = req.params("id");  //name of what ship with which player in front of it
        row = req.params("row");    //row #
        col = req.params("col");    //col #

        List PlayerFireMiss = model.getPlayerMisses();
        List PlayerFireHit = model.getPlayerHits();

        if(!PlayerFireHit.isEmpty() || !PlayerFireMiss.isEmpty()) {//list is empty
            Gson gson = new Gson();
            return gson.toJson(model);
        }


        int rows = Integer.parseInt(row);
        int column = Integer.parseInt(col);

        model.getShipByID(id).setEnd(0, 0);
        model.getShipByID(id).setStart(0, 0);

        Ship[] PArray = model.resetArrayNormal( model, true);

        int size = model.getShipByID(id).getLength();
        int stop = 0;
        Point cord = new Point();

        if (orientation.equals("horizontal") && (rows + size - 1) < 11 && rows > 0 && column < 11 && column > 0) {
            for (int i = rows; i < (rows + size); i++) {
                cord.setAcross(i);
                cord.setDown(column);

                //if ship lands on another ship then
                for(int j = 0; j < 5; j++){
                    if( Hit(PArray[j].getStart(), PArray[j].getEnd(), cord) ){
                        stop = 1;
                    }
                }
            }
            //Sets the placement of the ship
            if (stop == 0) {
                model.getShipByID(id).setStart(rows, column);
                model.getShipByID(id).setEnd(rows + size - 1, column);
            }

        } else if (orientation.equals("vertical") && rows < 11 && rows > 0 && column + size - 1 < 11 && column > 0) {
            for (int k = column; k < (column + size); k++) {
                cord.setAcross(rows);
                cord.setDown(k);

                //if ship lands on another ship then
                for(int i = 0; i < 5; i++){
                    if(Hit(PArray[i].getStart(), PArray[i].getEnd(), cord)){
                        stop = 1;
                    }
                }
            }

            if (stop == 0) {
                model.getShipByID(id).setStart(rows, column);
                model.getShipByID(id).setEnd(rows, column + size - 1);
            }
        }

        //Computer placement of the same ship
        Point computerStart;
        Point computerEnd;
        int computer_x = 0;
        int computer_y = 0;
        int horizontal = 0;
        int computer_length_increase = 0;
        int computerLength = model.getShipByID(id).getLength() - 1;

        //If the player sets down a ship the computer sets down the same ship
        model.getShipByID("computer" + id).setStart( 0, 0);
        model.getShipByID("computer" + id).setEnd( 0, 0);

        Ship[] CArray = model.resetArrayNormal( model, false);

        int movingPoint = 0, stoppedPoint = 0;

        while(stop == 0) {
            computer_x = (int) (Math.random() * 10 + 1);
            computer_y = (int) (Math.random() * 10 + 1);
            horizontal = (int) (Math.random() * 2 + 1);

            computerStart = new Point(computer_x, computer_y);
            computerEnd = new Point(0, 0);
            stop = 1;

            if (horizontal == 1 && computer_x + computerLength < 11) { //horizontal
                computerEnd = new Point(computer_x + computerLength, computer_y);
                computer_length_increase = computerEnd.getAcross();

                movingPoint = computer_x;
                stoppedPoint = computer_y;

            } else if (horizontal == 2 && computer_y + computerLength < 11) { //vertical
                computerEnd = new Point(computer_x, computer_y + computerLength);
                computer_length_increase = computerEnd.getDown();

                movingPoint = computer_y;
                stoppedPoint = computer_x;

            } else {
                stop = 0;
            }

            if (stop == 1) {
                for (movingPoint = movingPoint - 1; movingPoint < (computer_length_increase + 2); movingPoint++) {
                    if (horizontal == 1) {
                        cord.setAcross(movingPoint);
                        cord.setDown(stoppedPoint);

                    } else {
                        cord.setAcross(stoppedPoint);
                        cord.setDown(movingPoint);
                    }

                    //if ship lands on another ship then
                    for(int i = 0; i < 5; i++){
                        if(Hit( CArray[i].getStart(), CArray[i].getEnd(), cord)){
                            stop = 0;
                        }
                    }
                }

                //Sets placement of the ship
                if(stop == 1) {
                    model.getShipByID("computer" + id).setStart( computerStart.getAcross(), computerStart.getDown());
                    model.getShipByID("computer" + id).setEnd( computerEnd.getAcross(), computerEnd.getDown());
                }
            }
        }

        Gson gson = new Gson();
        return gson.toJson(model);

    }

    //Similar to placeShip, but with firing.
    private static String fireAt(Request req) {

        // Generate model from json, get coordinates from fire request
        BattleshipModelNormal model = (BattleshipModelNormal) getModelFromReq(req);

        String X = req.params("row");
        String Y = req.params("col");

        int row = Integer.parseInt(X);
        int col = Integer.parseInt(Y);

        // Make point object from coordinates
        Point FireSpot = new Point(row,col);

        // Grab player and computer ships from current model
        Ship[] PArray = model.resetArrayNormal( model, true);
        Ship[] CArray = model.resetArrayNormal( model, false);

        //Sets the health of sunk ships to -1
        //So that the view doesn't keep saying it sunk
        for (int i = 0; i < 5; i++) {
            if (PArray[i].getHealth() == 0) {
                model.getShipByID(PArray[i].getName()).setHealth(-1);
            }
            if (CArray[i].getHealth() == 0) {
                model.getShipByID(CArray[i].getName()).setHealth(-1);
            }
        }

        //Player has shot at this place already
        if( alreadyShot( FireSpot, model, true) ){
            Gson gson = new Gson();
            String jsonobject = gson.toJson(model);
            return jsonobject;
        }

        //Won't fire unless all ships are placed down
        for(int i = 0; i < 5; i++){
            if(PArray[i].getStart().getAcross() < 1){
                Gson gson = new Gson();
                return gson.toJson(model);
            }
        }

        int j = model.computerHits.size();

        // The following branch tree checks if a point fired at
        // BY A PLAYER has hit a COMPUTER ship and adds the point to the array of hits if so
        for( int i = 0; i < 5; i++){
            if( Hit( CArray[i].getStart(), CArray[i].getEnd(), FireSpot ) ){
                model.addPointtoArray(FireSpot, model.getComputerHits());
                model.getShipByID(CArray[i].getName()).setHealth(CArray[i].getHealth()-1);
            }
        }

        if(j == model.computerHits.size()){
            model.addPointtoArray(FireSpot, model.getComputerMisses());
        }

        // Create two random coordinates for computer to shoot at and make a point object of them
        int shootX, shootY;
        Point FireSpotComputer;

         do {
            shootX = (int )(Math.random() * 10 + 1);
            shootY = (int )(Math.random() * 10 + 1);
            FireSpotComputer = new Point(shootX, shootY);
        } while( alreadyShot( FireSpotComputer, model,false) );

        j = model.playerHits.size();

        // Following branch tree checks if a point fired at BY THE COMPUTER has hit a PLAYER ship
        // And adds the point to the array of hits if so
        for(int i = 0; i < 5; i++){
            if( Hit( PArray[i].getStart(), PArray[i].getEnd(), FireSpotComputer ) ){
                model.addPointtoArray(FireSpotComputer, model.getPlayerHits());
                model.getShipByID(PArray[i].getName()).setHealth(PArray[i].getHealth()-1);
            }
        }

        //if player missed
        if(j == model.playerHits.size()){
            model.addPointtoArray(FireSpot, model.getPlayerMisses());
        }

        Gson gson = new Gson();
        return gson.toJson(model);
    }

    public static boolean alreadyShot(Point shotPoint, BattleshipModel model, boolean player){
        List<Point> checkHits;
        List<Point> checkMisses;

        int sizeHits;
        int sizeMisses;

        //if player
        if(player) {
            checkHits = model.getComputerHits();
            checkMisses = model.getComputerMisses();

            sizeHits = model.getComputerHits().size();
            sizeMisses = model.getComputerMisses().size();

        }else{
            checkHits = model.getPlayerHits();
            checkMisses = model.getPlayerMisses();

            sizeHits = model.getPlayerHits().size();
            sizeMisses = model.getPlayerMisses().size();

        }

        for(int i = 0; i < sizeHits; i++){
            if(shotPoint.getAcross() == checkHits.get(i).getAcross() && shotPoint.getDown() == checkHits.get(i).getDown()){
                return true;
            }
        }

        for(int i = 0; i < sizeMisses; i++){
            if(shotPoint.getAcross() == checkMisses.get(i).getAcross() && shotPoint.getDown() == checkMisses.get(i).getDown() ){
                return true;
            }
        }

        return false;
    }

    public static boolean Hit(Point shipStart, Point shipEnd, Point shotPoint){

        if(shipStart.getDown() == shipEnd.getDown()){     // if start and end on same y coordinate, ship is horizontal
            int y = shipStart.getDown();
            for (int x = shipStart.getAcross(); x <= shipEnd.getAcross(); x++){  // loop from left to right of ship position

                if(x == shotPoint.getAcross() && y == shotPoint.getDown())
                    return true;   // if the coordinates of current point match shot, you hit!
            }

            return false; // check all points ship lies on, found no match to shot point

        } else if (shotPoint.getAcross() == shipStart.getAcross()) { // if start and end on same x coordinate, ship is vertical
            int x = shipStart.getAcross();
            for (int y = shipStart.getDown(); y <= shipEnd.getDown(); y++) {

                if (x == shotPoint.getAcross() && y == shotPoint.getDown())
                    return true;   // if the coordinates of current point match shot, you hit!
            }
            return false; // check all points ship lies on, found no match to shot point
        }

        return false; // points given are not horizontal or vertical and not valid, can't hit diagonally
    }
}