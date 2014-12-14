function teamHistory($) {
    var dataHandler = function (data) {
        var response = "<div class=playerHistory>";
        var header = [
            'league_name','team_name','season_year',
            'match_count','match_wins','percentage'
        ];
        for (var i=0; i<header.length; i++) {
            response += "<div id=teamHistory" + header[i] + "class=teamHistoryField>" + header[i] + "</div>";
            response += "<div class=teamHistoryData>" + data[header[i]]+ "</div>";
        }
        return response + "</div>";
    };
    postRequest($,'#playerInfo','player/teamHistory',dataHandler);
}