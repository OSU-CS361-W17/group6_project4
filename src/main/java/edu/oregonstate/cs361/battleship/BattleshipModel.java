package edu.oregonstate.cs361.battleship;

import java.util.ArrayList;
import java.util.List;


public class BattleshipModel {

    //                  Member variables
    protected Ship[] playerArray;
    protected Ship playerAircraftCarrier;

    protected Ship[] computerArray;
    protected Ship computerAircraftCarrier;

    protected boolean hard;

    // all ships for player or computer are collected in these lists
    protected List<Point> playerHits;
    protected List<Point> playerMisses;
    protected List<Point> computerHits;
    protected List<Point> computerMisses;

    //                  Constructor
    public BattleshipModel () {

        playerAircraftCarrier = new Ship("playerAircraftCarrier");

        computerAircraftCarrier = new Ship("computerAircraftCarrier");

        playerHits = new ArrayList<Point>();
        playerMisses = new ArrayList<Point>();
        computerHits = new ArrayList<Point>();
        computerMisses = new ArrayList<Point>();

        playerArray = new Ship[5];
        playerArray[0] = playerAircraftCarrier;

        computerArray = new Ship[5];
        computerArray[0] = computerAircraftCarrier;

        setHard(true);
    }


    //          Add Point object to an array function
    protected boolean addPointtoArray(Point somePoint, List someArray) {

        if (somePoint.getAcross() > 10 || somePoint.getAcross() < 1 || somePoint.getDown() > 10 || somePoint.getDown() < 1)

            return false;

        else {

            someArray.add(somePoint);
            return true;

        }
    }

    //          Getters
    protected Ship getPlayerAircraftCarrier() {
        return playerAircraftCarrier;
    }

    protected Ship getComputerAircraftCarrier() {
        return computerAircraftCarrier;
    }

    public List<Point> getPlayerHits() {
        return playerHits;
    }

    public List<Point> getPlayerMisses() {
        return playerMisses;
    }

    public List<Point> getComputerHits() {
        return computerHits;
    }

    public List<Point> getComputerMisses() {
        return computerMisses;
    }

    public boolean isHard() {
        return hard;
    }

    public void setHard(boolean hard) {
        this.hard = hard;
    }
}
