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
        post("/fire/:row/:col/:hard", (req, res) -> fireAt(req));

        //This will listen to POST requests and expects to receive a game model, as well as location to fire to
        post("/fireUpdated/:row/:col/:hard", (req, res) -> fireAtNew(req));
        //This will handle the scan feature
        post("/scan/:row/:col/:hard", (req, res) -> scan(req));

        //This will listen to POST requests and expects to receive a game model, as well as location to place the ship
        post("/placeShip/:id/:row/:col/:orientation/:hard", (req, res) -> placeShip(req));
        //This will listen to POST requests and expects to receive a game model, as well as location to place the ship
        post("/placeShipUpdated/:id/:row/:col/:orientation/:hard", (req, res) -> placeShipUpdated(req));
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
        String id, orientation, row, col;
        id = req.params("id");  //name of what ship with which player in front of it
        orientation = req.params("orientation"); //horizontal/vertical
        row = req.params("row");    //row #
        col = req.params("col");    //col #
        String hard = req.params("hard");

        if (hard.equals("0")){
            model.setHard(true);
        }else{
            model.setHard(false);
        }

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

        Ship[] PArray = model.resetArrayUpdated(model, true);

        int size = model.getShipByID(id).getLength();
        int stop = 0;
        Point cord = new Point();

        if (orientation.equals("horizontal") && (rows + size - 1) < 11 && rows > 0 && column < 11 && column > 0) {
            for (int i = rows; i < (rows + size); i++) {
                cord.setAcross(i);
                cord.setDown(column);

                //if ship lands on another ship then
                for (int l = 0; l < 5; l++) {
                    if (Hit(PArray[l].getStart(), PArray[l].getEnd(), cord)) {
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
                for (int l = 0; l < 5; l++) {
                    if (Hit(PArray[l].getStart(), PArray[l].getEnd(), cord)) {
                        stop = 1;
                    }
                }
            }

            if (stop == 0) {    //Sets the start and end location of the ship
                model.getShipByID(id).setStart(rows, column);
                model.getShipByID(id).setEnd(rows, column + size - 1);
            }
        }

        if(stop == 0) {
            if (model.isHard()) {
                model = placeShipUpdatedHard(model);
            } else{
                model = placeShipUpdatedEasy(model);
            }
        }

        Gson gson = new Gson();
        return gson.toJson(model);
    }

    //Places the ships at random locations
    public static BattleshipModelUpdated placeShipUpdatedHard(BattleshipModelUpdated model) {

        Ship[] CArray = model.resetArrayUpdated(model, false);
        Point computerStart;
        Point computerEnd;
        Point cord = new Point(0,0);

        int computer_x = 0;
        int computer_y = 0;
        int horizontal = 0;

        for(int id = 0; id < 5; id++) {
            int stop = 0;

            int computer_length_increase = 0;
            int computerLength = model.getShipByID(CArray[id].getName()).getLength() - 1;

            model.getShipByID(CArray[id].getName()).setStart(0, 0);
            model.getShipByID(CArray[id].getName()).setEnd(0, 0);
            CArray = model.resetArrayUpdated(model, false);

            int movingPoint = 0, stoppedPoint = 0;

            while (stop == 0) {
                //Gets a random starting location and horizontal/vertical
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
                        if (horizontal == 1) {  //Horizontal
                            cord.setAcross(movingPoint);
                            cord.setDown(stoppedPoint);

                        } else {                //Vertical
                            cord.setAcross(stoppedPoint);
                            cord.setDown(movingPoint);
                        }

                        //if ship lands on another ship then
                        for (int i = 0; i < 5; i++) {
                            if (Hit(CArray[i].getStart(), CArray[i].getEnd(), cord)) {
                                stop = 0;
                            }
                        }
                    }

                    if (stop == 1) {
                        model.getShipByID(CArray[id].getName()).setStart(computerStart.getAcross(), computerStart.getDown());
                        model.getShipByID(CArray[id].getName()).setEnd(computerEnd.getAcross(), computerEnd.getDown());
                        CArray = model.resetArrayUpdated(model, false);
                    }
                }
            }
        }

        return model;

    }

    public static BattleshipModelUpdated placeShipUpdatedEasy(BattleshipModelUpdated model) {

        for(int id = 0; id < 5; id++) {
            Ship[] CArray = model.resetArrayUpdated(model, false);
            int computerLength = model.getShipByID(CArray[id].getName()).getLength() - 1;

            model.getShipByID(CArray[id].getName()).setStart(id + 1, id + 1);
            model.getShipByID(CArray[id].getName()).setEnd(id + computerLength + 1, id + 1);
        }

        return model;

    }

    //Similar to placeShip, but with firing.
    private static String fireAtNew(Request req) {

        // Generate model from json, get coordinates from fire request
        BattleshipModelUpdated model = (BattleshipModelUpdated) getModelUpdatedFromReq(req);

        String X = req.params("row");
        String Y = req.params("col");
        String hard = req.params("hard");

        if (hard.equals("0")){
            model.setHard(true);
        }else{
            model.setHard(false);
        }

        int row = Integer.parseInt(X);
        int col = Integer.parseInt(Y);
        int sizeOfHitArray = 0;

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

        sizeOfHitArray = model.getComputerHits().size();

        // The following branch tree checks if a point fired at
        // BY A PLAYER has hit a COMPUTER ship and adds the point to the array of hits if so
        for (int i = 0; i < 5; i++) {
            if (Hit(CArray[i].getStart(), CArray[i].getEnd(), FireSpot)) {


                model.addPointtoArray(FireSpot, model.getComputerHits());
                model.getShipByID(CArray[i].getName()).setHealth(CArray[i].getHealth() - 1);

                //This is for sinking the ship.
                if(model.getShipByID(CArray[i].getName()).getHealth() == 0){
                    model = Sink(model.getShipByID(CArray[i].getName()).getStart(), model.getShipByID(CArray[i].getName()).getEnd(), true, model);
                }
            }
        }

        if (sizeOfHitArray == model.getComputerHits().size()) {
            model.addPointtoArray(FireSpot, model.getComputerMisses());
        }

        //returns the model with computer having fired
        if(model.isHard() == true) {
            model = computerFireHard(model);
        }else{
            model = computerFireEasy(model);
        }

        Gson gson = new Gson();
        return gson.toJson(model);
    }

    private static BattleshipModelUpdated computerFireHard(BattleshipModelUpdated model) {

        // Create two random coordinates for computer to shoot at and make a point object of them
        Ship[] PArray = model.resetArrayUpdated( model, true);
        Point FireSpotComputer;
        int shootX, shootY, sizeOfHitArray;

        do{ // Keeps getting random location until it hasn't fired at that spot
            shootX = (int )(Math.random() * 10 + 1);
            shootY = (int )(Math.random() * 10 + 1);
            FireSpotComputer = new Point(shootX, shootY);
        } while ( alreadyShot( FireSpotComputer, model,false));

        for(int i = 0; i < model.getPlayerHits().size(); i++){
            Point checkPoint = model.getPlayerHits().get(i);

            for (int k = 0; k < 5; k++) {
                if (Hit(PArray[k].getStart(), PArray[k].getEnd(), checkPoint)) { //Finds the ship
                    if(PArray[k].getHealth() > 0){ //Makes sure that the ship hasn't been sunk before (user knows this)

                        Point Left = new Point(checkPoint.getAcross() - 1, checkPoint.getDown());
                        Point Right = new Point(checkPoint.getAcross() + 1, checkPoint.getDown());
                        Point Down = new Point(checkPoint.getAcross(), checkPoint.getDown() - 1);
                        Point Up = new Point(checkPoint.getAcross(), checkPoint.getDown() + 1);

                        if( !alreadyShot( Left, model,false) && Left.getAcross() > 0){
                            FireSpotComputer = new Point(Left.getAcross(), Left.getDown());

                        }else if( !alreadyShot( Right, model,false ) && Right.getAcross() < 11){
                            FireSpotComputer = new Point( Right.getAcross(), Right.getDown());

                        }else if( !alreadyShot( Down, model,false ) && Down.getDown() > 0){
                            FireSpotComputer = new Point(Down.getAcross(), Down.getDown());

                        }else if( !alreadyShot( Up, model,false ) && Up.getDown() < 11){
                            FireSpotComputer = new Point(Up.getAcross(), Up.getDown());
                        }
                    }
                }
            }
        }

        sizeOfHitArray = model.getPlayerHits().size();

        // The following branch tree checks if a point fired at
        // BY A PLAYER has hit a COMPUTER ship and adds the point to the array of hits if so
        for (int i = 0; i < 5; i++) {
            if (Hit(PArray[i].getStart(), PArray[i].getEnd(), FireSpotComputer)) {

                model.addPointtoArray(FireSpotComputer, model.getPlayerHits());
                model.getShipByID(PArray[i].getName()).setHealth(PArray[i].getHealth() - 1);

                //This is for sinking the ship.
                if(model.getShipByID(PArray[i].getName()).getHealth() == 0){
                    model = Sink(model.getShipByID(PArray[i].getName()).getStart(), model.getShipByID(PArray[i].getName()).getEnd(), false, model);
                }
            }
        }

        if(sizeOfHitArray == model.getPlayerHits().size()){
            model.addPointtoArray(FireSpotComputer, model.getPlayerMisses());
        }

        return model;
    }

    private static BattleshipModelUpdated computerFireEasy(BattleshipModelUpdated model) {

        // Create two random coordinates for computer to shoot at and make a point object of them
        Ship[] PArray = model.resetArrayUpdated( model, true);
        Point FireSpotComputer = new Point( 0, 0);
        int shootX, shootY, sizeOfHitArray;

        // Keeps getting random location until it hasn't fired at that spot
        for(int x = 1; x < 11; x++){
            for(int y = 1; y < 11; y++){

                FireSpotComputer = new Point( x, y);

                if(!alreadyShot( FireSpotComputer, model,false)){
                    x = 12;
                    y = 12;
                    break;
                }
            }
        }

        sizeOfHitArray = model.getPlayerHits().size();

        // The following branch tree checks if a point fired at
        // BY A PLAYER has hit a COMPUTER ship and adds the point to the array of hits if so
        for (int i = 0; i < 5; i++) {
            if (Hit(PArray[i].getStart(), PArray[i].getEnd(), FireSpotComputer)) {

                model.addPointtoArray(FireSpotComputer, model.getPlayerHits());
                model.getShipByID(PArray[i].getName()).setHealth(PArray[i].getHealth() - 1);

                //This is for sinking the ship.
                if(model.getShipByID(PArray[i].getName()).getHealth() == 0){
                    model = Sink(model.getShipByID(PArray[i].getName()).getStart(), model.getShipByID(PArray[i].getName()).getEnd(), false, model);
                }
            }
        }

        if(sizeOfHitArray == model.getPlayerHits().size()){
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
        String hard = req.params("hard");

        if (hard.equals("0")){
            model.setHard(true);
        }else{
            model.setHard(false);
        }

        int row = Integer.parseInt(X);
        int col = Integer.parseInt(Y);

        // Make point object from coordinates
        Point[] FireSpot = new Point[] {new Point(row, col), new Point((row-1), col), new Point((row+1), col), new Point(row,(col-1)), new Point(row, (col+1))};

        // Grab player and computer ships from current model
        Ship[] PArray = model.resetArrayUpdated( model, true);
        Ship[] CArray = model.resetArrayUpdated( model, false);

        //Won't scan unless all ships are placed down
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
        if(model.isHard() == true) {
            model = computerFireHard(model);
        }else{
            model = computerFireEasy(model);
        }

        Gson gson = new Gson();
        return gson.toJson(model);
    }

    public static String placeShip(Request req) {

        BattleshipModelNormal model = (BattleshipModelNormal) getModelFromReq(req);
        String id, orientation, row, col;
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

        int sizeOfShip = model.getShipByID(id).getLength();
        int stop = 0;
        Point cord = new Point();

        if (orientation.equals("horizontal") && (rows + sizeOfShip - 1) < 11 && rows > 0 && column < 11 && column > 0) {
            for (int i = rows; i < ( rows + sizeOfShip ); i++) {
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
                model.getShipByID(id).setEnd(rows + sizeOfShip - 1, column);
            }

        } else if (orientation.equals("vertical") && rows < 11 && rows > 0 && column + sizeOfShip - 1 < 11 && column > 0) {
            for (int k = column; k < ( column + sizeOfShip ); k++) {
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
                model.getShipByID(id).setEnd(rows, column + sizeOfShip - 1);
            }
        }

        //Computer placement of the same ship
        Point computerStart;
        Point computerEnd;
        int computer_x, computer_y, horizontal;

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

            if (horizontal == 1 && computer_x + sizeOfShip - 1 < 11) { //horizontal

                computerEnd = new Point(computer_x + sizeOfShip - 1, computer_y);
                movingPoint = computer_x;
                stoppedPoint = computer_y;

            } else if (horizontal == 2 && computer_y + sizeOfShip - 1 < 11) { //vertical

                computerEnd = new Point(computer_x, computer_y + sizeOfShip - 1);
                movingPoint = computer_y;
                stoppedPoint = computer_x;

            } else {
                stop = 0;
            }

            if (stop == 1) {
                for (; movingPoint < (computerEnd.getDown()); movingPoint++) {
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

        int j = 0;

        // The following branch tree checks if a point fired at
        // BY A PLAYER has hit a COMPUTER ship and adds the point to the array of hits if so
        for( int i = 0; i < 5; i++){
            if( Hit( CArray[i].getStart(), CArray[i].getEnd(), FireSpot ) ){
                model.addPointtoArray(FireSpot, model.getComputerHits());
                model.getShipByID(CArray[i].getName()).setHealth(CArray[i].getHealth()-1);
                j = 1;
            }
        }

        if(j == 0){
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

        j = 0;

        // Following branch tree checks if a point fired at BY THE COMPUTER has hit a PLAYER ship
        // And adds the point to the array of hits if so
        for(int i = 0; i < 5; i++){
            if( Hit( PArray[i].getStart(), PArray[i].getEnd(), FireSpotComputer ) ){
                model.addPointtoArray(FireSpotComputer, model.getPlayerHits());
                model.getShipByID(PArray[i].getName()).setHealth(PArray[i].getHealth()-1);
                j = 1;
            }
        }

        //if player missed
        if(j == 0){
            model.addPointtoArray(FireSpotComputer, model.getPlayerMisses());
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

        } else if (shipStart.getAcross() == shipEnd.getAcross()) { // if start and end on same x coordinate, ship is vertical
            int x = shipStart.getAcross();
            for (int y = shipStart.getDown(); y <= shipEnd.getDown(); y++) {

                if (x == shotPoint.getAcross() && y == shotPoint.getDown())
                    return true;   // if the coordinates of current point match shot, you hit!
            }
        }

        return false; // points given are not horizontal or vertical and not valid, can't hit diagonally
    }

    public static BattleshipModelUpdated Sink( Point shipStart, Point shipEnd, boolean player, BattleshipModelUpdated model ){

        if( shipStart.getDown() == shipEnd.getDown() ){
            int y = shipStart.getDown();

            for ( int x = shipStart.getAcross(); x <= shipEnd.getAcross(); x++ ){
                Point Shot = new Point(x, y);

                if( !(alreadyShot(Shot, model, player)) ){
                    if(player){
                        model.addPointtoArray( Shot, model.getComputerHits() );
                    }else{
                        model.addPointtoArray( Shot, model.getPlayerHits() );
                    }
                }
            }

        } else if ( shipStart.getAcross() == shipEnd.getAcross() ) {
            int x = shipStart.getAcross();

            for ( int y = shipStart.getDown(); y <= shipEnd.getDown(); y++ ) {
                Point Shot = new Point( x, y);

                if( !(alreadyShot(Shot, model, player)) ){
                    if(player){
                        model.addPointtoArray( Shot, model.getComputerHits() );
                    }else{
                        model.addPointtoArray( Shot, model.getPlayerHits() );
                    }
                }
            }
        }

        return model;
    }
}