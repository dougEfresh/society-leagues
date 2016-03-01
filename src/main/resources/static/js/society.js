var qs = (function(a) {
    if (a == "") return {};
    var b = {};
    for (var i = 0; i < a.length; ++i)
    {
        var p=a[i].split('=', 2);
        if (p.length == 1)
            b[p[0]] = "";
        else
            b[p[0]] = decodeURIComponent(p[1].replace(/\+/g, " "));
    }
    return b;
})(window.location.search.substr(1).split('&'));

function selectDate(e,s) {
    var myselect = document.getElementById("select-date");
    console.log(myselect.options[myselect.selectedIndex].value);
    window.location = window.location.pathname + '?date=' + myselect.options[myselect.selectedIndex].value;
}

function challengeChange() {
 var date = $('#challenge-date').val();
 var userId = $('#challenge-users').val();
 if (userId == null || userId == undefined)
     window.location = '/app/challenge?date=' + date ;
 else
     window.location = '/app/challenge?date=' + date + '&userId=' + userId;

    $('.society-well-slots').hide();
}
function challengeAcceptSlot() {
 var date = $('#challenge-date').val();
 var userId = $('#challenge-users').val();
 var slotId = $('#challenge-slot').val();
 if (userId == null || userId == undefined)
     window.location = '/app/challenge?date=' + date + '&slotId=' + slotId;
 else
     window.location = '/app/challenge?date=' + date + '&userId=' + userId;

}

function changeTeamSeason(e) {
    var id = $('#team-seasons').val();
    window.location = '/app/team/season/' + id;
}

function changeAdminSeason(e) {
    var id = $('#admin-seasons').val();
    console.log('Changing cookie to '  + id);
    //window.adminSeason = id;
    Cookies.set('admin-season',id);
    $('#admin-scores').attr("href",'/app/scores/' + id);
    $('#admin-schedules').attr("href",'/app/schedule/' + id);
    $('#admin-leaders').attr("href",'/app/leaders/' + id);
    $('#admin-standings').attr("href",'/app/display/' + id);
    $('#admin-season').attr("href",'/app/season/' + id);
    $('#admin-teams').attr("href",'/app/teams/season/' + id);
}

function searchUser(e) {
    var id = $('#users-search').val();
    window.location = '/app/stats/' + id;
}

function searchAdminUser(e) {
    var id = $('#users-search').val();
    window.location = '/app/user/' + id;
}

function seasonChange(e) {
    var id = $('#season-select').val();
    window.location = '/app/season?seasonId=' + id;
}


function changeChallengeDate(e) {
    var id = $('#challenge-date').val();
    window.location = '/app/challenge?date=' + id;
}


function changeChallengeUser(e) {
    var id = $('#challenge-users').val();
    window.location = '/app/challenge?userId=' + id;
}

function selectTeam(e) {
    var id = $('#teams-select').val();
    window.location = '/app/schedule/team/' + id;
}

function searchUserStats(e) {
    var id = $('#users-stats-search').val();
    window.location = '/app/stats/' + id;
}

function lifeTimeStats(v) {

}

 $(document).ready(function() {
     $('#team-members').selectize({
         persist: false,
         plugins: ['remove_button'],
         maxItems: 8
});
     $('#challenge-slots').selectize({
         persist: false,
           plugins: ['remove_button'],
         maxItems: 8
       });

     $('#challenge-broadcast-slots').selectize({
         persist: false,
           plugins: ['remove_button'],
         maxItems: 8
       });

     $('#challenge-block-dates').selectize({
         persist: false,
           plugins: ['remove_button'],
         maxItems: 8
       });



     var available = $('.users-available');
     var availableSelectize = [];
     var notAvailableSelectize = [];
     for (var i = 0; i< available.length ; i++ ) {
         var id = available[i].id;
         if (!id) {
             continue
         }

         var a = $('#' + id ).selectize({
             persist: false,
             plugins: ['remove_button'],
             maxItems: 12,
             index: i,
             id: id,
             onChange: function(v) {
                 var notIdSelect= '#'+ this.settings.id.replace('users-available','users-not-available-select');
                 var notId = '#'+ this.settings.id.replace('users-available','users-not-available');
                 this.items.forEach(function(item){
                     $(notIdSelect+ '  option[value="' + item + '"]').prop('selected',false);
                 }.bind(this));

                 var options = $(notIdSelect+ ' option');
                 var txt = "";
                 for (var j = 0; j < options.length; j++ ) {
                     var o = options[j];
                     var available = false;
                     this.items.forEach(function(item){
                         if (item == o.value) {
                             available = true;
                         }
                     }.bind(this));
                     if (!available) {
                         txt += '<div data-value="' + o.value + '" style="cursor: none" class="item no-cursor" >' +  o.innerHTML + '</div>';
                     }
                 }
                 $(notId).html(txt);

                 //console.log($('#'+ this.settings.id.replace('users-available','users-not-available') + '  option[value="' + v + '"]')
                 //console.log(notAvailableSelectize[this.settings.index].getOption(v));
                 //console.log(notAvailableSelectize[this.settings.index].getOption(v));
                 //notAvailableSelectize[this.settings.index].addItem(v,true);
                 //console.log(notAvailableSelectize[this.settings.index]);
             }
         });

         availableSelectize.push(a[0].selectize);
         /*
         notAvailableSelectize.push($('#' + id.replace('users-available','users-not-available')).selectize({
             persist: false,
             plugins: ['remove_button'],
             maxItems: 12,
             index: i,
             onChange: function(v) {
                 console.log(availableSelectize[this.settings.index].getOption(v));

             }
         })[0].selectize);
         */

     }
     notAvailableSelectize.forEach(function(a){
//         a.disable();
     });
     /*
     $('.users-available').selectize({
         persist: false,
         plugins: ['remove_button'],
         maxItems: 12,
         other: {id: "asdas"},
         onChange: function(v) {
             console.log(this);
         }
     });
     $('.users-not-available').selectize({
         persist: false,
         plugins: ['remove_button'],
         maxItems: 12,
         onChange: function(v) {
             console.log(this.id);
         }
//         sortField: 'text'
     });
      */

     $('#users-search').selectize({
         persist: false,
         maxItems: 1,
         onChange: function(v) {  window.location = '/app/stats/' + v;}
        });


     $('#users-search-admin').selectize({
         persist: false,
         maxItems: 1,
         onChange: function(v) {  window.location = '/app/user/' + v;}
        });

     $('#users-stats-search').selectize({
         persist: false,
         maxItems: 1,
         plugins: ['remove_button'],
         onChange: function(v) {  if (v != null && v != undefined && v.length > 0 ) window.location = '/app/stats/' + v;}
        });


/*
     $('#users-search').multiselect({
         enableFiltering: true,
         enableCaseInsensitiveFiltering: true,
         filterPlaceholder: 'Search...',
         nonSelectedText: 'Search Users...',
         onChange: function(option, checked, select) {
                 window.location = '/app/stats/' +  $(option).val();
         }
     });
     */

 /*
     $.dynatableSetup({
           dataset: {
               perPageDefault: 20,
               perPageOptions: [10,20,50,100]
           }
     });
     */

      $('.season-date').datetimepicker({
         dateFormat: 'Y-m-d',
         format: "Y-m-d",
         formatDate: 'Y-m-d',
         timepicker: false
      });

     $('.match-date').datetimepicker({
         dateFormat: 'Y-m-d',
         format: 'Y-m-d',
         formatDate: 'Y-m-d',
         timepicker: false
    });
     /*
     $('.match-time').datetimepicker({
         datepicker: false,
         format:'H:i',
         allowTimes:[
             '11:00', '11:30',
             '12:00',
             '12:30',
             '13:00',
             '13:30',
             '14:00',
             '14:30',
             '15:00',
             '15:30',
             '16:00',
             '16:30',
             '17:00',
             '18:00'
         ]
     }
     );
     */

     //$("#team-members").select2({ width: 600 });
     /*$("#users-search").select2();*/
     $('#table-player-results').dynatable( {
         dataset : {
              perPageDefault: 10
         }
     });
     /*
     $('.table-top-gun-standings').dynatable( {
      dataset : {
              perPageDefault: 10
         }
     });
     */

      $('.table-top-gun-results').dynatable( {
      dataset : {
              perPageDefault: 10
         }
     });

     /*
     $('#table-team-schedule').dynatable( {

     dataset: {
               perPageDefault: 20,
               perPageOptions: [10,20,50,100]

           },
           features: {
           paginate: false,
           search: false,
           sort: true
           }

           });
      */
  $('#table-season').dynatable( {
     dataset: {
               perPageDefault: 5,
               perPageOptions: [10,20,50,100]

           },
           features: {
           paginate: true,
           search: false,
           sort: true
           }

           });

     /*
     $('#table-team-standings').dynatable( {
     dataset: {
               perPageDefault: 50,
               perPageOptions: [10,20,50,100]

           },
           features: {
           paginate: false,
           search: false,
           sort: true
           }

           });
           */

     $('#table-leaders').dynatable();
     $('#table-teams').dynatable(  {
             dataset: {
               perPageDefault: 20,
               perPageOptions: [10,20,50,100]

           }
     });

     $('#table-team-members').dynatable(
         {
             features: {
             paginate: false,
             sort: true,
             pushState: true,
             search: false,
             recordCount: false,
             perPageSelect: false
             }
         });
 });
