var gameModel;

//Get each board
var playerTable =   document.getElementById("PlayerBoard");
var computerTable = document.getElementById("ComputerBoard");

//hovering.onmouseover = (function displayAllRules)

//If the table exists, iterate to get each square in (j+1,i+1) format and set an onclick
if (playerTable != null) {
    for (var i = 0; i < playerTable.rows.length; i++) {
        for (var j = 0; j < playerTable.rows[i].cells.length; j++) {
            playerTable.rows[i].cells[j].onclick = (function (i, j) {


                return function clickPlace() {
                    // This ajax call will asnychonously call the back end, and tell it where to place the ship,
                    // then get back a game model with the ship placed, and display the new model.
                    var placeDiff = document.getElementById("placeDiffSelec");
                    var placeDiffValue = placeDiff.options[placeDiff.selectedIndex].value;

                    var request = $.ajax({
                        url: "/placeShipUpdated/"+$( "#shipSelec" ).val()+"/"+(j+1)+"/"+(i+1)+"/"+$( "#orientationSelec" ).val()+"/"+placeDiffValue,
                        method: "post",
                        data: JSON.stringify(gameModel),
                        contentType: "application/json; charset=utf-8",
                        dataType: "json"
                    });

                    //Checks if the Computer has fired at all and if so then alert the user that you can't place or move ships
                    //after you have fired a single shot.
                    if(gameModel.computerMisses.length > 0 || gameModel.computerHits.length > 0){
                        alert("You have started the game.\nYou can't place down or move ships now.");

                    }else if($( "#orientationSelec" ).val() == "horizontal"){

                        if($( "#shipSelec" ).val() == "aircraftCarrier" && j > 5){
                            alert("Place ship within the board.")

                        }else if( $( "#shipSelec" ).val() == "battleship" && j > 6){
                            alert("Place ship within the board.")

                        }else if( $( "#shipSelec" ).val() == "clipper" && j > 7){
                            alert("Place ship within the board.")

                        }else if( $( "#shipSelec" ).val() == "submarine" && j > 7){
                            alert("Place ship within the board.")
                        }

                    }else if($( "#orientationSelec" ).val() == "vertical"){

                        if($( "#shipSelec" ).val() == "aircraftCarrier" && i > 5){
                            alert("Place ship within the board.")

                        }else if( $( "#shipSelec" ).val() == "battleship" && i > 6){
                            alert("Place ship within the board.")

                        }else if( $( "#shipSelec" ).val() == "clipper" && i > 7){
                            alert("Place ship within the board.")

                        }else if( $( "#shipSelec" ).val() == "submarine" && i > 7){
                            alert("Place ship within the board.")

                        }
                    }

                    //This will be called when the call is returned from the server.
                    request.done(function( currModel ) {
                        displayGameState(currModel);
                        gameModel = currModel;

                    });

                    // if there is a problem, and the back end does not respond, then an alert will be shown.
                    request.fail(function( jqXHR, textStatus ) {
                        alert( "Request failed: " + textStatus );
                    });
                };


            }(i, j));
            playerTable.rows[i].cells[j].onmouseover = (function (i, j) {
                return function onHover() {
                    if(gameModel.computerMisses.length == 0 && gameModel.computerHits.length == 0) {
                        var l = 5;
                        switch ($("#shipSelec").val()) {
                            case "aircraftCarrier":
                                l = 5;
                                break;
                            case "battleship":
                                l = 4;
                                break;
                            case "submarine":
                                l = 3;
                                break;
                            case "clipper":
                                l = 3;
                                break;
                            case "dinghy":
                                l = 1;
                                break;
                        }
                        if ($("#orientationSelec").val() == "horizontal" && ((l + j) > 10)) {
                            for (var k = 0; k < l; k++) {
                                $('#MyBoard #' + (i + 1) + '_' + (j + 1 + k)).css("border-color", "red");
                            }
                        }
                        else if ($("#orientationSelec").val() == "horizontal") {
                            for (var k = 0; k < l; k++) {
                                $('#MyBoard #' + (i + 1) + '_' + (j + 1 + k)).css("border-color", "green");
                            }
                        }
                        if ($("#orientationSelec").val() == "vertical" && ((i + l) > 10)) {
                            for (var k = 0; k < l; k++) {
                                $('#MyBoard #' + (i + 1 + k) + '_' + (j + 1)).css("border-color", "red");
                            }
                        }
                        else if ($("#orientationSelec").val() == "vertical") {
                            for (var k = 0; k < l; k++) {
                                $('#MyBoard #' + (i + 1 + k) + '_' + (j + 1)).css("border-color", "green");
                            }
                        }
                    }
                };
            }(i,j));
            playerTable.rows[i].cells[j].onmouseout = (function (i, j) {
                return function onRemove() {
                    if(gameModel.computerMisses.length == 0 && gameModel.computerHits.length == 0) {
                        var l = 5;
                        switch ($("#shipSelec").val()) {
                            case "aircraftCarrier":
                                l = 5;
                                break;
                            case "battleship":
                                l = 4;
                                break;
                            case "submarine":
                                l = 3;
                                break;
                            case "clipper":
                                l = 3;
                                break;
                            case "dinghy":
                                l = 1;
                                break;
                        }
                        if ($("#orientationSelec").val() == "horizontal") {
                            for (var k = 0; k < l; k++) {
                                $('#MyBoard #' + (i + 1) + '_' + (j + 1 + k)).css("border-color", "white");
                            }
                        }
                        else if ($("#orientationSelec").val() == "vertical") {
                            for (var k = 0; k < l; k++) {
                                $('#MyBoard #' + (i + 1 + k) + '_' + (j + 1)).css("border-color", "white");
                            }
                        }
                    }
                };
            }(i,j));
        }
    }
}

//If the table exists, iterate to get each square in (j+1,i+1) format and set an onclick
if (computerTable != null) {
    for (var i = 0; i < computerTable.rows.length; i++) {
        for (var j = 0; j < computerTable.rows[i].cells.length; j++) {
            computerTable.rows[i].cells[j].onclick = (function (i, j) {


                return function clickFire() {
                    if(gameModel.playerAircraftCarrier.start.Across < 1){
                        alert( "Place Aircraft Carrier!!!\nBefore you can fire.");
                    }
                    if(gameModel.playerBattleship.start.Across < 1){
                        alert( "Place Battleship!!!\nBefore you can fire.");
                    }
                    if(gameModel.playerClipper.start.Across < 1){
                        alert( "Place Clipper!!!\nBefore you can fire.");
                    }
                    if(gameModel.playerDinghy.start.Across < 1){
                        alert( "Place Dinghy!!!\nBefore you can fire.");
                    }
                    if(gameModel.playerSubmarine.start.Across < 1){
                        alert( "Place Submarine!!!\nBefore you can fire.");
                    }

                    var fireDiff = document.getElementById("fireDiffSelec");
                    var fireDiffValue = fireDiff.options[fireDiff.selectedIndex].value;

                    var request = $.ajax({
                        url: "/"+$('input:radio[name=fire]:checked').val() +"/"+(j+1)+"/"+(i+1)+"/"+fireDiffValue,
                        method: "post",
                        data: JSON.stringify(gameModel),
                        contentType: "application/json; charset=utf-8",
                        dataType: "json"
                    });

                    request.done(function( currModel ) {

                        gameModel = currModel;
                        SunkShipsAlert(gameModel);
                        displayGameState(currModel);
                        if(gameModel.playerAircraftCarrier.start.Across > 0 && gameModel.playerBattleship.start.Across > 0 &&
                            gameModel.playerClipper.start.Across > 0 && gameModel.playerDinghy.start.Across > 0 &&
                            gameModel.playerSubmarine.start.Across > 0 && $('input:radio[name=fire]:checked').val() == "scan") {
                            if(gameModel.scanned){
                                alert("Scanned a ship.");
                                gameModel.scanned = false;
                            }
                            else
                                alert("Didn't scan a ship.");
                        }

                    });

                    request.fail(function( jqXHR, textStatus ) {
                        alert( "Request failed: " + textStatus );
                    });


                };


            }(i, j));
            computerTable.rows[i].cells[j].onmouseover = (function (i, j) {
                return function onHover() {
                    if(gameModel.playerAircraftCarrier.start.Across > 0 && gameModel.playerBattleship.start.Across > 0 &&
                        gameModel.playerClipper.start.Across > 0 && gameModel.playerDinghy.start.Across > 0 &&
                        gameModel.playerSubmarine.start.Across > 0 && $('input:radio[name=fire]:checked').val()  == "fireUpdated") {
                        $('#TheirBoard #' + (i + 1) + '_' + (j + 1)).css("border-color", "orange");
                    }
                    else if(gameModel.playerAircraftCarrier.start.Across > 0 && gameModel.playerBattleship.start.Across > 0 &&
                        gameModel.playerClipper.start.Across > 0 && gameModel.playerDinghy.start.Across > 0 &&
                        gameModel.playerSubmarine.start.Across > 0 && $('input:radio[name=fire]:checked').val()  == "scan") {
                        $('#TheirBoard #' + (i + 1) + '_' + (j + 1)).css("border-color", "orange");
                        $('#TheirBoard #' + (i + 2) + '_' + (j + 1)).css("border-color", "orange");
                        $('#TheirBoard #' + (i) + '_' + (j + 1)).css("border-color", "orange");
                        $('#TheirBoard #' + (i + 1) + '_' + (j + 2)).css("border-color", "orange");
                        $('#TheirBoard #' + (i + 1) + '_' + (j)).css("border-color", "orange");
                    }
                };
            }(i,j));
            computerTable.rows[i].cells[j].onmouseout = (function (i, j) {
                return function onHover() {
                    if(gameModel.playerAircraftCarrier.start.Across > 0 && gameModel.playerBattleship.start.Across > 0 &&
                        gameModel.playerClipper.start.Across > 0 && gameModel.playerDinghy.start.Across > 0 &&
                        gameModel.playerSubmarine.start.Across > 0 && $('input:radio[name=fire]:checked').val()  == "fireUpdated") {
                        $('#TheirBoard #' + (i + 1) + '_' + (j + 1)).css("border-color", "white");
                    }
                    else if(gameModel.playerAircraftCarrier.start.Across > 0 && gameModel.playerBattleship.start.Across > 0 &&
                        gameModel.playerClipper.start.Across > 0 && gameModel.playerDinghy.start.Across > 0 &&
                        gameModel.playerSubmarine.start.Across > 0 && $('input:radio[name=fire]:checked').val()  == "scan") {
                        $('#TheirBoard #' + (i + 1) + '_' + (j + 1)).css("border-color", "white");
                        $('#TheirBoard #' + (i + 2) + '_' + (j + 1)).css("border-color", "white");
                        $('#TheirBoard #' + (i) + '_' + (j + 1)).css("border-color", "white");
                        $('#TheirBoard #' + (i + 1) + '_' + (j + 2)).css("border-color", "white");
                        $('#TheirBoard #' + (i + 1) + '_' + (j)).css("border-color", "white");
                    }
                };
            }(i,j));
        }
    }
}

//This function will be called once the page is loaded.  It will get a new game model from the back end, and display it.
$( document ).ready(function() {
    $.getJSON("modelUpdated", function( json ) {
        displayGameState(json);
        gameModel = json;
    });
});


function placeShip() {

    var placeDiff = document.getElementById("placeDiffSelec");
    var placeDiffValue = placeDiff.options[placeDiff.selectedIndex].value;
    // This ajax call will asnychonously call the back end, and tell it where to place the ship, then get back a game model with the ship placed, and display the new model.
    var request = $.ajax({
        url: "/placeShipUpdated/"+$( "#shipSelec" ).val()+"/"+$( "#rowSelec" ).val()+"/"+$( "#colSelec" ).val()+"/"+$( "#orientationSelec" ).val()+"/"+placeDiffValue,
        method: "post",
        data: JSON.stringify(gameModel),
        contentType: "application/json; charset=utf-8",
        dataType: "json"
    });


    //Checks if the Computer has fired at all and if so then alert the user that you can't place or move ships
    //after you have fired a single shot.
    if(gameModel.computerMisses.length > 0 || gameModel.computerHits.length > 0){
        alert("You have started the game.\nYou can't place down or move ships now.");

    }else if($( "#orientationSelec" ).val() == "horizontal"){
        if($( "#shipSelec" ).val() == "aircraftCarrier" && $( "#rowSelec" ).val() > 6){
            alert("Place ship within the board.")

        }else if( $( "#shipSelec" ).val() == "battleship" && $( "#rowSelec" ).val() > 7){
            alert("Place ship within the board.")

        }else if( $( "#shipSelec" ).val() == "clipper" && $( "#rowSelec" ).val() > 7){
            alert("Place ship within the board.")

        }else if( $( "#shipSelec" ).val() == "submarine" && $( "#rowSelec" ).val() > 7){
            alert("Place ship within the board.")

        }

    }else if($( "#orientationSelec" ).val() == "vertical"){
        if($( "#shipSelec" ).val() == "aircraftCarrier" && $( "#colSelec" ).val() > 5){
            alert("Place ship within the board.")

        }else if( $( "#shipSelec" ).val() == "battleship" && $( "#colSelec" ).val() > 6){
            alert("Place ship within the board.")

        }else if( $( "#shipSelec" ).val() == "clipper" && $( "#colSelec" ).val() > 9){
            alert("Place ship within the board.")

        }else if( $( "#shipSelec" ).val() == "submarine" && $( "#colSelec" ).val() > 7){
            alert("Place ship within the board.")
        }
    }

    //This will be called when the call is returned from the server.
    request.done(function( currModel ) {
        displayGameState(currModel);
        gameModel = currModel;

    });

    // if there is a problem, and the back end does not respond, then an alert will be shown.
    request.fail(function( jqXHR, textStatus ) {
        alert( "Request failed: " + textStatus );
    });
}

//Similar to placeShip, but instead it will fire at a location the user selects.
function fire(){

    //Checks if each ship has been placed and if not then alert the user that he/she should place
    //the individual ship before he/she can fire.
    if(gameModel.playerAircraftCarrier.start.Across < 1){
        alert( "Place Aircraft Carrier!!!\nBefore you can fire.");
    }else if(gameModel.playerBattleship.start.Across < 1){
        alert( "Place Battleship!!!\nBefore you can fire.");
    }else if(gameModel.playerClipper.start.Across < 1){
        alert( "Place Clipper!!!\nBefore you can fire.");
    }else if(gameModel.playerDinghy.start.Across < 1){
        alert( "Place Dinghy!!!\nBefore you can fire.");
    }else if(gameModel.playerSubmarine.start.Across < 1){
        alert( "Place Submarine!!!\nBefore you can fire.");
    }

    //Get the value of the fire diff so as to send it to req
    var fireDiff = document.getElementById("fireDiffSelec");
    var fireDiffValue = fireDiff.options[fireDiff.selectedIndex].value;

    var request = $.ajax({
        url: "/fireUpdated/"+$( "#rowFire" ).val()+"/"+$( "#colFire" ).val()+"/"+fireDiffValue,
        method: "post",
        data: JSON.stringify(gameModel),
        contentType: "application/json; charset=utf-8",
        dataType: "json"
    });

    request.done(function( currModel ) {
        displayGameState(currModel);
        gameModel = currModel;

    });

    request.fail(function( jqXHR, textStatus ) {
        alert( "Request failed: " + textStatus );
    });

}

//This function will display the game model.  It displays the ships on the users board, and then shows where there have been hits and misses on both boards.
function displayGameState(gameModel){

    if(countDownedShipsPlayer(gameModel) == 5){
        $( '#lost' ).css( "display", "none" );

        $( '#computerWin' ).css( "display", "inline" );
        $( '#computerWin' ).css( "float", "middle" );

        alert("Computer has won!");

    }else if(countDownedShipsComputer(gameModel) == 5){
        $( '#lost' ).css( "display", "none" );

        $( '#playerWin' ).css( "display", "inline" );
        $( '#playerWin' ).css( "float", "middle" );

        alert("Player has won!");

    }else{
        $( '#MyBoard td'  ).css("background-color", "blue");
        $( '#TheirBoard td'  ).css("background-color", "blue");


        displayShip(gameModel.playerAircraftCarrier);
        displayShip(gameModel.playerBattleship);
        displayShip(gameModel.playerClipper);
        displayShip(gameModel.playerDinghy);
        displayShip(gameModel.playerSubmarine);


        for (var i = 0; i < gameModel.computerMisses.length; i++) {
            $( '#TheirBoard #' + gameModel.computerMisses[i].Down + '_' + gameModel.computerMisses[i].Across ).css("background-color", "green");
        }

        for (var i = 0; i < gameModel.computerHits.length; i++) {
            $( '#TheirBoard #' + gameModel.computerHits[i].Down + '_' + gameModel.computerHits[i].Across ).css("background-color", "red");
        }

        for (var i = 0; i < gameModel.playerMisses.length; i++) {
            $( '#MyBoard #' + gameModel.playerMisses[i].Down + '_' + gameModel.playerMisses[i].Across ).css("background-color", "green");
        }

        for (var i = 0; i < gameModel.playerHits.length; i++) {
            $( '#MyBoard #' + gameModel.playerHits[i].Down + '_' + gameModel.playerHits[i].Across ).css("background-color", "red");
        }
    }
}

function SunkShipsAlert(gameModel){

    if(gameModel.playerAircraftCarrier.health == 0){
        alert("Player AircraftCarrier has sunk.");
    }else if(gameModel.playerSubmarine.health == 0){
        alert("Player Submarine has sunk.");
    }else if(gameModel.playerDinghy.health == 0){
        alert("Player Dinghy has sunk.");
    }else if(gameModel.playerBattleship.health == 0){
        alert("Player Battleship has sunk.");
    }else if(gameModel.playerClipper.health == 0){
        alert("Player Clipper has sunk.");
    }

    if(gameModel.computerAircraftCarrier.health == 0){
        alert("Computer AircraftCarrier has sunk.");
    }else if(gameModel.computerSubmarine.health == 0){
        alert("Computer Submarine has sunk.");
    }else if(gameModel.computerDinghy.health == 0){
        alert("Computer Dinghy has sunk.");
    }else if(gameModel.computerBattleship.health == 0){
        alert("Computer Battleship has sunk.");
    }else if(gameModel.computerClipper.health == 0){
        alert("Computer Clipper has sunk.");
    }

}

function countDownedShipsPlayer(gameModel){
    var downedShips = 0;

    if(gameModel.playerAircraftCarrier.health <= 0){
        downedShips += 1;
    }

    if(gameModel.playerSubmarine.health <= 0){
        downedShips += 1;
    }

    if(gameModel.playerDinghy.health <= 0){
        downedShips += 1;
    }

    if(gameModel.playerBattleship.health <= 0){
        downedShips += 1;
    }

    if(gameModel.playerClipper.health <= 0){
        downedShips += 1;
    }

    return downedShips;
}

function countDownedShipsComputer(gameModel){
    var downedShips = 0;

    if(gameModel.computerAircraftCarrier.health <= 0){
        downedShips += 1;
    }

    if(gameModel.computerSubmarine.health <= 0){
        downedShips += 1;
    }

    if(gameModel.computerDinghy.health <= 0){
        downedShips += 1;
    }

    if(gameModel.computerBattleship.health <= 0){
        downedShips += 1;
    }

    if(gameModel.computerClipper.health <= 0){
        downedShips += 1;
    }

    return downedShips;
}

//This function will display a ship given a ship object in JSON
function displayShip(ship){
    startCoordAcross = ship.start.Across;
    startCoordDown = ship.start.Down;
    endCoordAcross = ship.end.Across;
    endCoordDown = ship.end.Down;
    if(startCoordAcross > 0){

        if(startCoordAcross == endCoordAcross){
            for (i = startCoordDown; i <= endCoordDown; i++) {
                $( '#MyBoard #'+i+'_'+startCoordAcross  ).css("background-color", "yellow");
            }

        } else {
            for (i = startCoordAcross; i <= endCoordAcross; i++) {
                $( '#MyBoard #'+startCoordDown+'_'+i  ).css("background-color", "yellow");
            }
        }

    }
}

/*======================
 Modal Js stuffs
 ======================*/
// Get the modal
var modal = document.getElementById('myModal');

// Get the button that opens the modal
var btn = document.getElementById("myBtn");

// Get the <span> element that closes the modal
var span = document.getElementsByClassName("close")[0];

// When the user clicks the button, open the modal
btn.onclick = function() {
    modal.style.display = "block";
}

// When the user clicks on <span> (x), close the modal
span.onclick = function() {
    modal.style.display = "none";
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
    if (event.target == modal) {
        modal.style.display = "none";
    }
}
