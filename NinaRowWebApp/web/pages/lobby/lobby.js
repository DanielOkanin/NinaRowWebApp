var refreshRate = 1000; //mili seconds
var USER_LIST_URL = buildUrlWithContextPath("userslist");
var GAME_ROOMS_LIST_URL = buildUrlWithContextPath("gamerooms");
var JOIN_GAME_URL = buildUrlWithContextPath("joingame");
var WATCH_GAME_URL = buildUrlWithContextPath("watchGame");

$(function() { // onload...do

    //prevent IE from caching ajax calls
    $.ajaxSetup({cache: false});
    //The users list is refreshed automatically every second
    setInterval(ajaxUsersList, refreshRate);
    setInterval(ajaxRoomsContent, refreshRate);

    var form = $("#uploadFileForm")[0];

    //add a function to the submit event

    $("#uploadFileForm").submit(function(e) {
        e.preventDefault();
        $.ajax({
            data: new FormData(form),
            url: this.action,
            type: 'POST',
            timeout: 2000,
            enctype: "multipart/form-data",
            contentType: false,
            processData : false,

            error: function() {
                alert("error");
            },
            success: function(r) {
                //do not add the user string to the chat area
                //since it's going to be retrieved from the server
                //$("#result h1").text(r);
                alert(r);
            }
        });

        return false;
    });
});

function ajaxRoomsContent() {
    $.ajax({
        url: GAME_ROOMS_LIST_URL,
        dataType: 'json',
        success: function (data) {
            /*
             data is of the next form:
             {
                "m_EnginesList": [
                    {
                        "m_BoardRows" = 7,
                        "m_BoardCols" = 5,
                        "m_Sequence" = 3,
                        "m_Variant" = "CIRCULAR",
                        "m_Status" = "RUNNING",
                        "m_Players" = [....., ....],
                        "m_TotalPlayers" = 2,
                        "m_GameTitle" = "sample,
                    },
                    {
                        "m_BoardRows" = 10,
                        "m_BoardCols" = 8,
                        "m_Sequence" = 5,
                        "m_Variant" = "REGULAR",
                        "m_Status" = "RUNNING",
                        "m_TotalPlayers" = 3,
                        "m_GameTitle" = "hard",
                    }
                ]
             },
             "m_UploadGamesUsersList": [

                "moshiko",
                "yossi"

               ]

             */
            appendGameRoom(data.m_EnginesList, data.m_UploadGamesUsersList);
        }
    });
}

function appendGameRoom(rooms, users) {
    $("#roomstable tbody").empty();

    $.each(rooms || [], function (index, room) {
        //room.m_GameTitle = room.m_GameTitle;
        var user = users[index];
        // room.m_BoardRows = room.m_BoardRows;
        // room.m_BoardCols = room.m_BoardCols;
        // room.m_Sequence = room.m_Sequence;
        // room.m_Status = room.m_Status;
        // room.m_Variant = room.m_Variant;
        // room.m_TotalPlayers = room.m_TotalPlayers;
        // room.m_Players = room.m_Players;

        var tableRef = ($("#tablebody"))[0];
        var newRow   = tableRef.insertRow(tableRef.rows.length);
        newRow.classList.add("gameInfoRow");

        var newCell1  = newRow.insertCell(0);
        newCell1.appendChild(document.createTextNode(room.m_GameTitle));

        var newCell2  = newRow.insertCell(1);
        newCell2.appendChild(document.createTextNode(user));

        var newCell3  = newRow.insertCell(2);
        newCell3.appendChild(document.createTextNode(room.m_BoardRows + " X " + room.m_BoardCols));

        var newCell4  = newRow.insertCell(3);
        newCell4.appendChild(document.createTextNode(room.m_Sequence));

        var newCell5 = newRow.insertCell(4);
        newCell5.appendChild(document.createTextNode(room.m_Variant));

        var newCell6  = newRow.insertCell(5);
        newCell6.appendChild(document.createTextNode(room.m_Status));

        var newCell7  = newRow.insertCell(6);
        var playersActive = 0;
        $.each(room.m_Players || [] , function (index, player) {
            if (player.m_IsRetire === false) {
                playersActive ++;
            }
        });
        newCell7.appendChild(document.createTextNode(playersActive + " / " + room.m_TotalPlayers));

        var newCell8 = newRow.insertCell(7);
        var butt = document.createElement('button'); // create a button
        butt.classList.add('buttonjoin');
        butt.setAttribute('id', 'joinbutton'+ room.m_GameTitle);
        butt.setAttribute('onclick', "addPlayer('" + room.m_GameTitle + "')");
        butt.innerHTML = 'Join';
        newCell8.appendChild(butt);

        var newCell9 = newRow.insertCell(8);
        var watchButton = document.createElement('button'); // create a button
        watchButton.classList.add('buttonWatch');
        watchButton.setAttribute('id', 'watchButton'+ room.m_GameTitle);
        watchButton.setAttribute('onclick', "addWatcher('" + room.m_GameTitle + "')");
        watchButton.innerHTML = 'Watch';
        newCell9.appendChild(watchButton);


        updateJoinDisability(room.m_GameTitle, room.m_Status);

    });
}

function addPlayer(gameTitle) {
    $.ajax({
        url: JOIN_GAME_URL,
        data: "gameTitle=" + gameTitle,
        success: function (message) {

            window.location='../game/game.html?gametitle=' + gameTitle + "&mode=player";
        }
    });
}

function addWatcher(gameTitle) {
    $.ajax({
        url: WATCH_GAME_URL,
        data: "gameTitle=" + gameTitle,
        success: function (message) {

            window.location='../game/game.html?gametitle=' + gameTitle + "&mode=watcher";
        }
    });
}

function updateJoinDisability(gameTitle, gameStatus) {

    if (gameStatus !== "WAITING") {
        var b = document.getElementById('joinbutton' + gameTitle);
        b.disabled = true;
    }

}

function refreshUsersList(users) {
    //clear all current users
    $("#userslist").empty();

    // rebuild the list of users: scan all users and add them to the list of users
    $.each(users || [], function(index, username) {
        console.log("Adding user #" + index + ": " + username);
        //create a new <option> tag with a value in it and
        //appeand it to the #userslist (div with id=userslist) element
        $('<li class="userOnlineName">' + username + '</li>').appendTo($("#userslist"));
    });
}

function ajaxUsersList() {
    $.ajax({
        url: USER_LIST_URL,
        success: function(users) {
            refreshUsersList(users);
        }
    });
}