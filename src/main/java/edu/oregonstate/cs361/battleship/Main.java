package edu.oregonstate.cs361.battleship;

import spark.Request;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

public class Main {

    public static void main(String[] args) {
        //This will allow us to server the static pages such as index.html, app.js, etc.
        staticFiles.location("/public");

        //This will listen to GET requests to /model and return a clean new model
        get("/model", (req, res) -> newModel());
        //This will listen to POST requests and expects to receive a game model, as well as location to fire to
        post("/fire/:row/:col", (req, res) -> fireAt(req));
        //This will listen to POST requests and expects to receive a game model, as well as location to place the ship
        post("/placeShip/:id/:row/:col/:orientation", (req, res) -> placeShip(req));
    }

    public Main(){
        setPlayerHits(0);
        setPlayerMisses(0);

        setComputerHits(0);
        setComputerMisses(0);
    }

    //Member variables
    private int playerMisses;
    private int playerHits;

    private int computerMisses;
    private int computerHits;

    //////////////////////////////////////////////////////////////////
    //Player gets and sets for hitting and missing.
    public int getPlayerMisses(){
        return playerMisses;
    }

    public int getPlayerHits(){
        return playerHits;
    }

    public void setPlayerMisses(int a){
        this.playerMisses = a;
    }

    public void setPlayerHits(int a){
        this.playerHits = a;
    }
    //////////////////////////////////////////////////////////////////


    //////////////////////////////////////////////////////////////////
    //Computer gets and sets for hitting and missing.
    public int getComputerMisses(){
        return computerMisses;
    }

    public int getComputerHits(){
        return computerHits;
    }

    public void setComputerMisses(int a){
        this.computerMisses = a;
    }

    public void setComputerHits(int a){
        this.computerHits = a;
    }
    //////////////////////////////////////////////////////////////////


    //This function should return a new model
    static String newModel() {
        BattleshipModel aircraftCarrier;
        BattleshipModel battleship;
        BattleshipModel cruiser;
        BattleshipModel destroyer;
        BattleshipModel submarine;

        BattleshipModel computer_aircraftCarrier;
        BattleshipModel computer_battleship;
        BattleshipModel computer_cruiser;
        BattleshipModel computer_destroyer;
        BattleshipModel computer_submarine;
        
        return "MODEL";
    }

    //This function should accept an HTTP request and deseralize it into an actual Java object.
    private static BattleshipModel getModelFromReq(Request req){
        return null;
    }

    //This controller should take a json object from the front end, and place the ship as requested, and then return the object.
    private static String placeShip(Request req) {



        return "SHIP";
    }


    //Similar to placeShip, but with firing.
    private static String fireAt(Request req) {
        //System.out.println(req);
        return null;
    }



}