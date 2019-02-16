var refreshRate = 1000; //mili seconds
var allocateRate = 500;
var computerRate = 3000;
var SINGLE_GAME_URL = buildUrlWithContextPath("singlegameinfo");
var SINGLE_USER_SESSION_URL = buildUrlWithContextPath("singleusersession");
var PLAY_TURN_URL = "playmove";
var WAITING_GAME_TURN = "  Waiting...  ";
var urlSearchParams = new URLSearchParams(window.location.search);
var gameTitle = urlSearchParams.get("gametitle");
var mode = urlSearchParams.get("mode");
var gameTitleWithSpaces = gameTitle.replace("%20", " ");
var discsColor = ["red", "blue", "green", "yellow", "pink", "gray"];
var PLAYER = "player";
var isClicked = false;
var updateInterval;
var turnsIndex = 0;
var chatIndex = 0;
var colFactor = 1;

$(function() {

    ($("#gameTitleHeader")[0]).innerText = gameTitleWithSpaces;
    setTimeout(allocateBoard, allocateRate);
    setTimeout(allocateTurnsHistory,allocateRate);
    setTimeout(allocateChat, allocateRate);
    setTimeout(allocateWatchers, allocateRate);

    updateInterval = setInterval(updateGameDetails, refreshRate);
    setInterval(checkForComputerTurn, computerRate);

    $("#chatform").submit(function(e) {
        var message = document.forms["chatform"].elements["chatMessage"].value;

        if (mode === PLAYER) {
            $.ajax({
                data: {
                    "gameTitle": gameTitleWithSpaces,
                    "chatMessage": message
                } ,
                url: "sendChat",
                type: "POST",

                success: function(r) {
                }
            });
        }

        return false;
    });

});

function allocateBoard() {
    $.ajax({
        url: SINGLE_GAME_URL,
        data: "gameTitle=" + gameTitleWithSpaces,
        success: function (data) {
            /*
             data is of the next form:
             {
                "m_BoardRows" = 7,
                "m_BoardCols" = 5,
                "m_Sequence" = 3,
                "m_Variant" = "CIRCULAR",
                "m_Status" = "RUNNING",
                "m_Players" = [....., ....],
                "m_TotalPlayers" = 2,
                "m_GameTitle" = "sample",
             }
             */

            allocateBoardEnableButtons(data.m_BoardCols, 'insert');
            allocateBoardBody(data.m_BoardRows, data.m_BoardCols);
            if (data.m_Variant === "POPOUT") {
                allocateBoardEnableButtons(data.m_BoardCols, 'popout');
            }
        }
    });

}

function allocateBoardBody(boardRows, boardCols) {
    var boardBody = ($("#boardbody")[0]);
    for (var i = 0; i < boardRows; i++) {
        var bodyRow = boardBody.insertRow(i);
        bodyRow.classList.add('boardRow');

        var rowIndexcell = bodyRow.insertCell(0);
        rowIndexcell.classList.add('boardRowIndexCell');
        var button = document.createElement("button");
        button.classList.add('boardRowIndexButton');
        button.innerText = (i + 1 + "");
        button.disabled = true;
        rowIndexcell.appendChild(button);

        for (var j = 1; j < boardCols+1; j++) {
            var cell = bodyRow.insertCell(j);
            cell.classList.add('boardcell');
            var button = document.createElement("button");
            button.classList.add('boarbutton');
            button.disabled = true;
            cell.appendChild(button);

        }
    }
}


function allocateBoardEnableButtons(boardCols, type) {
    /* type = 'insert' or 'popout' */

    var boardPart;
    if (type === 'insert') {
        boardPart = ($("#boardhead")[0]);
    }
    else {
        boardPart = ($("#boardfoot")[0]);
    }

    var row = boardPart.insertRow(0);
    row.classList.add('boardRow');

    var rowIndexCell = row.insertCell(0);
    rowIndexCell.classList.add('boardRowIndexCellUnvisible');
    var rowIndexButton = document.createElement("button");
    rowIndexButton.classList.add('boardRowIndexButtonUnvisible');
    rowIndexButton.disabled = true;
    rowIndexCell.appendChild(rowIndexButton);

    for (var i = 1; i < boardCols+1; i++) {
        var cell = row.insertCell(i);
        cell.classList.add('actioncell');
        var button = document.createElement("button");
        button.innerText = i + "";
        button.setAttribute('onclick', type + 'Disc(' + (i-1) + ')');
        button.classList.add('actionbutton');
        if (mode !== PLAYER) {
            button.disabled = true;
        }
        cell.appendChild(button);
    }
}

function allocateWatchers() {
    var title = ($("#watchersTitle"))[0];
    title.innerHTML = "Watchers List";

}


function updateGameDetails() {
    $.ajax({
        url: SINGLE_GAME_URL,
        data: "gameTitle=" + gameTitleWithSpaces,
        success: function (data) {
            /*
             data is of the next form:
             {
                "m_BoardRows" = 7,
                "m_BoardCols" = 5,
                "m_Sequence" = 3,
                "m_Variant" = "CIRCULAR",
                "m_Board" = [[-1,-1,-1,-1,-1,-1,-1],[-1,-1,-1,-1,-1,-1,-1],[-1,-1,-1,-1,-1,-1,-1],[-1,-1,-1,-1,-1,-1,-1],[-1,-1,-1,-1,-1,-1,-1],[-1,-1,-1,-1,-1,-1,-1]],
                "m_Status" = "RUNNING",
                "m_Players" = [....., ....],
                "m_Watchers" = ["moshe", "dani"],
                "m_TotalPlayers" = 2,
                "m_GameTitle" = "sample",
                "m_CurrentTurn" = 0;
             }
             */
            updateGamePlayers(data.m_Players);
            updateGameWatchers(data.m_Watchers);
            updateGameVariant(data.m_Variant);
            updateGameTarget(data.m_Sequence);
            updateRetireButton(data.m_Status);
            updateCurrentTurn(data.m_Status, data.m_Players[data.m_CurrentTurn]);
            paintBoard(data.m_Board, data.m_BoardRows, data.m_BoardCols);
            updateTurnsHistory();
            updateChat();
            checkGameStatus(data);
        }
    });
}

function updateRetireButton(status) {
    var button = ($("#retireButton"))[0];
    if (status === "RUNNING" && mode === PLAYER) {
        button.innerHTML = "Retire";
    }
}

function updateGamePlayers(players) {
    $("#playersDetailsList").empty();
    for (var i = 0; i < players.length; i++) {
        var header = document.createElement("p");
        header.classList.add("playerIndexTitle");
        header.appendChild(document.createTextNode('Player #' + (i+1)));
        ($("#playersDetailsList")[0]).appendChild(header);
        addPlayerInfo(players[i], i);
    }
}

function addPlayerInfo(player, index) {

    /*
          player is of the next form:
          {
             "m_Name" = "yossi",
             "m_IsHuman" = "true",
             "m_IsRetire" = "false",
             "m_TurnsCounter" = 0,
             "m_IsWinner" = "false",
          }
          */

    var table = document.createElement("table");
    table.classList.add("playerTable");
    if (player.m_IsRetire === true) {
        table.classList.add("retirePlayerTable");
    }

    var nameRow = table.insertRow(table.rows.length);

    var playerNameKey = nameRow.insertCell(0);
    playerNameKey.appendChild(document.createElement("th"));
    playerNameKey.classList.add("playerInfoKey");
    playerNameKey.innerText = "Name";

    var playerNameValue = nameRow.insertCell(1);
    playerNameValue.appendChild(document.createElement("td"));
    playerNameValue.innerText = player.m_Name;
    playerNameValue.classList.add("playerInfoValue");


    var typeRow = table.insertRow(table.rows.length);

    var playerTypeKey = typeRow.insertCell(0);
    playerTypeKey.appendChild(document.createElement("th"));
    playerTypeKey.classList.add("playerInfoKey");
    playerTypeKey.innerText = "Is human";

    var playerTypeValue = typeRow.insertCell(1);
    playerTypeValue.appendChild(document.createElement("td"));
    playerTypeValue.innerText = player.m_IsHuman;
    playerTypeValue.classList.add("playerInfoValue");


    var discColorRow = table.insertRow(table.rows.length);

    var playerDiscColorKey = discColorRow.insertCell(0);
    playerDiscColorKey.appendChild(document.createElement("th"));
    playerDiscColorKey.classList.add("playerInfoKey");
    playerDiscColorKey.innerText = "Disc color";

    var playerDiscColorValue = discColorRow.insertCell(1);
    playerDiscColorValue.appendChild(document.createElement("td"));
    playerDiscColorValue.innerText = discsColor[index];
    playerDiscColorValue.classList.add("playerInfoValue");


    var turnsCounterRow = table.insertRow(table.rows.length);

    var playerTurnsCounterKey = turnsCounterRow.insertCell(0);
    playerTurnsCounterKey.appendChild(document.createElement("th"));
    playerTurnsCounterKey.classList.add("playerInfoKey");
    playerTurnsCounterKey.innerText = "Turns played";

    var playerTurnsCounterValue = turnsCounterRow.insertCell(1);
    playerTurnsCounterValue.appendChild(document.createElement("td"));
    playerTurnsCounterValue.innerText = player.m_TurnsCounter;
    playerTurnsCounterValue.classList.add("playerInfoValue");


    var isActiveRow = table.insertRow(table.rows.length);

    var playerIsActiveKey = isActiveRow.insertCell(0);
    playerIsActiveKey.appendChild(document.createElement("th"));
    playerIsActiveKey.classList.add("playerInfoKey");
    playerIsActiveKey.innerText = "Is active";

    var playerIsActiveValue = isActiveRow.insertCell(1);
    playerIsActiveValue.appendChild(document.createElement("td"));
    playerIsActiveValue.innerText = !player.m_IsRetire;
    playerIsActiveValue.classList.add("playerInfoValue");


    ($("#playersDetailsList")[0]).appendChild(table);

}

function updateGameWatchers(watchers) {
    ($("#watchersList")).empty();
    var watchersList = ($("#watchersList"))[0];
    for (var i = 0; i < watchers.length; i++) {
        var listItem = document.createElement("li");
        listItem.innerHTML = watchers[i];
        listItem.classList.add("watcherItem");
        watchersList.appendChild(listItem);
    }
}

function updateGameVariant(variant) {
    ($("#gameVariantHeader")[0]).innerText = variant;
}

function updateGameTarget(target) {
    ($("#gameTargetHeader")[0]).innerText = "Target: " + target;
}

function updateCurrentTurn(status, currentTurnPlayer) {
    if (status !== 'RUNNING') {
        ($("#currentTurnValue")[0]).innerText = WAITING_GAME_TURN;
    }
    else {
        ($("#currentTurnValue")[0]).innerText = currentTurnPlayer.m_Name;
    }

        $.ajax({
            url: SINGLE_USER_SESSION_URL,
            success: function (data) {
                /*
                data is:
                    "m_Name" = "shiko";
                    "m_IsHuman" = true;
                    "m_IsRetire" = false;
                    "m_TurnsCounter" = 0;
                    "m_IsWinner" = false;

                */
                if (($("#currentTurnValue")[0]).innerHTML === data.m_Name) {
                    isClicked = false;
                }
            }
        });
}

function insertDisc(colIndex) {
    playMove('INSERT_DISC', colIndex);
}

function popoutDisc(colIndex) {
    playMove('POPOUT_DISC', colIndex);
}

function playMove(moveType, columnIndex) {
    $.ajax({
        url: SINGLE_USER_SESSION_URL,
        success: function (data) {
            /*
            data is:
                "m_Name" = "shiko";
                "m_IsHuman" = "true";
                "m_IsRetire" = false;
                "m_TurnsCounter" = 0;
                "m_IsWinner" = false;

             */
            var currentTurn = ($("#currentTurnValue")[0]).innerHTML;

            if (currentTurn !== WAITING_GAME_TURN) {
                if (currentTurn === data.m_Name) { // its your turn
                    if (!isClicked && data.m_IsHuman === true) { // its your first temp
                        $.ajax({
                            url: PLAY_TURN_URL,
                            data: {
                                "gameTitle":gameTitleWithSpaces,
                                "playername":($("#currentTurnValue")[0]).innerHTML,
                                "movetype": moveType,
                                "colindex": columnIndex
                            },
                            type: "POST",

                            success: function (data) {
                                if (data !== "") {
                                    alert(data);
                                }
                            }
                        });

                        isClicked = true;
                    }
                }
                else {
                    alert("It is not your turn !");
                }
            }
            else {
                alert("Game is not running yet.")
            }
        }
    });
}

function paintBoard(board, rows, cols) {
    var boardTable = ($("#boardbody"))[0];
    for (var i = 0; i < rows; i++) {
        for (var j = 0; j < cols; j++) {
            var cell = boardTable.rows[i].cells[j+colFactor].childNodes[0];
            if (board[i][j] > -1) {
               cell.style.background = discsColor[board[i][j]];
            }
            else {
                cell.style.background = "";
            }
        }
    }
}

function checkGameStatus(engine) {
    /*engine is of the next form:
             {
                "m_BoardRows" = 7,
                "m_BoardCols" = 5,
                "m_Sequence" = 3,
                "m_Variant" = "CIRCULAR",
                "m_Board" = [[-1,-1,-1,-1,-1,-1,-1],[-1,-1,-1,-1,-1,-1,-1],[-1,-1,-1,-1,-1,-1,-1],[-1,-1,-1,-1,-1,-1,-1],[-1,-1,-1,-1,-1,-1,-1],[-1,-1,-1,-1,-1,-1,-1]],
                "m_Status" = "RUNNING",
                "m_Players" = [....., ....],
                "m_TotalPlayers" = 2,
                "m_GameTitle" = "sample,
                "m_CurrentTurn = 0;
             }
       */
    if (engine.m_Status !== "RUNNING" && engine.m_Status !== 'WAITING') {
        $.ajax({
            url: "gameStatusCheck",
            data: "gameTitle=" + gameTitleWithSpaces,
            success: function (statusmessage) {
                alert(statusmessage);
                var statusHeader = ($("#statusHeader"))[0];
                statusHeader.innerText = statusmessage;
            }
        });

        clearInterval(updateInterval);
        ($("#submitChatMessage")[0]).disabled = true;
        disableActionButtons("insertButtons");
        if (engine.m_Variant === "POPOUT") {
            disableActionButtons("popoutButtons");
        }

        setTimeout(function () {$.ajax({
            url: "resetGame",
            data: "gameTitle=" + gameTitleWithSpaces,
            type: "POST",
            success: function (data) {
            }
        });

        }, 3000);

        ($("#retireButton")[0]).remove();
        addBackToLobbyButton();
    }
}

function disableActionButtons(buttonsType) {
    var boardPart;
    if (buttonsType === "insertButtons") {
        boardPart = ($("#boardhead"))[0];
    }
    else {
        boardPart = ($("#boardfoot"))[0];
    }
    var row = boardPart.rows[0];

    for (var i = 0; i < row.childNodes.length; i++ ) {
        var cell = row.childNodes[i];
        var button = cell.childNodes[0];
        button.disabled = true;
    }
}

function addBackToLobbyButton() {
    var backButton = document.createElement("button");
    backButton.setAttribute('onclick', "window.location.href='../lobby/lobby.html'");
    backButton.setAttribute('id', "backToLobbyButton");
    backButton.innerHTML = "Back to lobby";
    var div = ($("#maindiv"))[0];
    div.appendChild(backButton);
}

function checkForComputerTurn() {
    var currentPlayerName = ($("#currentTurnValue")[0]).innerText;

    $.ajax({
        url: SINGLE_USER_SESSION_URL,
        success: function (data) {
            /*
            data is:
                "m_Name" = "shiko";
                "m_IsHuman" = "true";
                "m_IsRetire" = false;
                "m_TurnsCounter" = 0;
                "m_IsWinner" = false;

             */

            if (data.m_Name === currentPlayerName && data.m_IsHuman === false) {
                setTimeout(function () {
                    $.ajax({
                        url: "playComputerTurn",
                        data: "gameTitle=" + gameTitleWithSpaces,
                        type: "POST",
                        success: function (e) {

                        }

                    });
                }, refreshRate);

            }

        }
    });
}

function retireButtonWasClick() {
    $.ajax({
        url: "gameStatus",
        data: "gameTitle=" + gameTitleWithSpaces,
        success: function (status) {
            if (status === "WAITING" || mode !== PLAYER) {
                getOutAndBackToLobby();
            }
            else {
                retireRunningGame();
            }
        }
    });
}

function getOutAndBackToLobby() {
    var playersTables = document.getElementsByClassName("playerTable");
    if (playersTables.length === 1) {
        $.ajax({
            url: "clearChat",
            data: "gameTitle=" + gameTitleWithSpaces,
            success: function (e) {
                console.log("chat clear");
            }
        });
    }
    $.ajax({
        url: "retireWaitingGame",
        data: {
            "gameTitle": gameTitleWithSpaces,
            "mode": mode
        },
        success: function (e) {
            console.log("player back to lobby");
            window.location.href="../lobby/lobby.html";
        }
    });
}

function retireRunningGame() {

    var currentPlayerName = ($("#currentTurnValue")[0]).innerText;

    $.ajax({
        url: SINGLE_USER_SESSION_URL,
        success: function (player) {
            /*
            player is:
                "m_Name" = "yossi";
                "m_IsHuman" = true;
                "m_IsRetire" = false;
                "m_TurnsCounter" = 0;
                "m_IsWinner" = false;

             */

            if (player.m_Name === currentPlayerName && player.m_IsHuman === true) {

                $.ajax({
                    url: "retireRunningGame",
                    data: "gameTitle=" + gameTitleWithSpaces,
                    success: function (message) {
                        console.log(message);
                        window.location.href = "../lobby/lobby.html";
                    }
                });
            }
            else {
                alert("Wait for your turn to retire.")
            }
        }
    });
}

function allocateTurnsHistory() {
    var title = ($("#turnsHistoryTitle"))[0];
    title.innerHTML = "Turns History";
}

function updateTurnsHistory() {

        $.ajax({
            url: "turnsHistory",
            data: {
                "gameTitle": gameTitleWithSpaces,
                "turnsIndex": turnsIndex
            },
            success: function (data) {
                /* data is :
                    [ "moshe, Insert, Column 4, Row 6" ,
                      "yossi, Insert, Column 4, Row 5
                    ]
                 */

                if (data) {
                    var turnsList = ($("#turnsHistoryList"))[0];
                    for (var i = 0; i < data.length; i++) {
                        var listItem = document.createElement("li");
                        listItem.innerHTML = data[i];
                        listItem.classList.add("turnItem");
                        turnsList.appendChild(listItem);
                    }

                    turnsIndex = document.getElementsByClassName("turnItem").length;
                    var height = turnsList.scrollHeight - $(turnsList).height();
                    $(turnsList).stop().animate({ scrollTop: height }, "slow");
                }
            }
        });
}

function allocateChat() {
    var title = ($("#chatTitle"))[0];
    title.innerHTML = "Chat";
    if (mode !== PLAYER) {
        var sendChat = document.getElementById("submitChatMessage");
        sendChat.disabled = true;
    }
}

function updateChat() {
    $.ajax({
        url: "chat",
        data: {
            "gameTitle": gameTitleWithSpaces,
            "index": chatIndex
        },
        success: function (data) {
            /* data is :
                [ "moshe: hi" ,
                  "yossi: bye"
                ]
             */

            if (data) {
                var chat = ($("#chatMessagesList"))[0];
                for (var i = 0; i < data.length; i++) {
                    var listItem = document.createElement("li");
                    listItem.innerHTML = data[i];
                    listItem.classList.add("chatItem");
                    chat.appendChild(listItem);
                }

                chatIndex = document.getElementsByClassName("chatItem").length;
                var height = chat.scrollHeight - $(chat).height();
                $(chat).stop().animate({ scrollTop: height }, "slow");
            }
        }
    });
}