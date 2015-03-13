var dt = function(d) {
    var date  =  new Date(d.year, d.monthValue, d.dayOfMonth, d.hour, d.minute, 0, 0);
    return date.toLocaleString();
};