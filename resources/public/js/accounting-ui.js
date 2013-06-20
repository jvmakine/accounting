"use strict";

var accountingUi = (function() {
  //Public interface
  var publicInterface = {};
  
  //UI Elements
  var accountContainer    = "#account_container",
      addAccountButton    = "#new_account_button",
      deleteAccountButton = ".delete-account-button",
      deleteAccountDialog = "#delete_account_dialog",
      newAccountDialog    = "#new_account_dialog",
      addEventButton      = ".add-event-button",
      showCalendarButton  = ".show-calendar-button",
      newEventDialog      = "#new_event_dialog",
      deleteEventDialog   = "#delete_event_dialog",
      deleteEventButton   = ".delete-event-button",
      showCalendarDialog  = "#calendar_dialog",
      calendar            = "#full_calendar";
  
  // Selection status
  var activeAccountId = null;
  var activeEventId = null;
  
  var moneyRound = function(val) {
    return Math.round(val*100)/100
  }
  
  var makeAccountAccordion = function() {
    $(accountContainer).accordion({ collapsible: true, active: null,
      beforeActivate: function( event, ui ) {
        var panel = ui.newPanel;
        accounting.renderAccountEvents($(panel).children('.account-events'), $(panel).attr("account-id"));
      },
      heightStyle: "content"});
    // Delete account button
    $(deleteAccountButton).unbind('click');
    $(deleteAccountButton).click(function(event) {
      event.stopPropagation();
      event.preventDefault();
      activeAccountId = $(this).closest("h3").attr("account-id");
      $(deleteAccountDialog).dialog("open");
    });
    $(deleteAccountButton).button({
      icons: { primary: "ui-icon-trash" }
    });
    // New event button
    $(addEventButton).button({
      icons: { primary: "ui-icon-plusthick" }
    });
    $(addEventButton).click(function(event) {
      event.stopPropagation();
      event.preventDefault();
      activeAccountId = $(this).closest("h3").attr("account-id");
      clearNewEventForm();
      $(newEventDialog).dialog("open");
    });
    // Show calendar button
    $(showCalendarButton).unbind('click');
    $(showCalendarButton).click(function(event) {
      event.stopPropagation();
      event.preventDefault();
      activeAccountId = $(this).closest("h3").attr("account-id");
      var height = $(window).height()*0.9;
      var width = $(window).width()*0.9;
      $(showCalendarDialog).dialog({
        height: height,
        width: width
      });
      $(calendar).fullCalendar('option', 'aspectRatio', width/(height - 200));
      $(calendar).fullCalendar('today');
      $(showCalendarDialog).dialog("open");
      $(calendar).fullCalendar('render');
    });
    $(showCalendarButton).button({
      icons: { primary: "ui-icon-calendar" }
    });
  }
  

  var makeNewAccountDialog = function() {
    $(newAccountDialog).dialog({
      autoOpen : false,
      height : 360,
      width : 350,
      modal : true,
      draggable : false,
      resizable : false,
      buttons : {
        "Create" : function() {
          accounting.createAccount({
            name : $("#new_account_name").val(),
            description : $("#new_account_description").val()
          });
          $(newAccountDialog).dialog("close");
        },
        "Cancel" : function() {
          $(newAccountDialog).dialog("close");
        }
      }
    });
  }
  

  var makeNewEventDialog = function() {
    $(newEventDialog).dialog({
      autoOpen : false,
      height : 370,
      width : 400,
      modal : true,
      draggable : false,
      resizable : false,
      buttons : {
        "Create" : function() {
          var isTransfer = $('#event-transfer').is(':checked')
          accounting.createEvent({
            account_id : activeAccountId,
            description : $("#new_event_description").val(),
            amount : $("#new_event_amount").val(),
            change_type : (isTransfer ? "transfer" : "change"),
            event_date : $("#new_event_date").val()
          })
          $(newEventDialog).dialog("close");
        },
        "Cancel" : function() {
          $(newEventDialog).dialog("close");
        }
      }
    });
    $("#new_event_date").datepicker({
      dateFormat : "yy-mm-dd"
    });
    $("#new_event_date").datepicker("setDate", "0");
  }
  
  var makeDeleteConfirmationDialog = function(elem, delete_fn) {
    elem.dialog({
      autoOpen: false,
        height: 200,
        width: 300,
        modal: true,
        draggable: false,
        resizable: false,
        buttons: {
          "Delete": function() {
            delete_fn();
            elem.dialog("close");
          },
          "Cancel": function() {
            elem.dialog("close");
          }
         }  
    });
  }
  
  var makeDeleteAccountDialog = function() {
    makeDeleteConfirmationDialog($(deleteAccountDialog), function() {
      accounting.deleteAccount(activeAccountId);
    });
  }
  
  var makeDeleteEventDialog = function() {
    makeDeleteConfirmationDialog($(deleteEventDialog), function() {
      accounting.deleteEvent(activeAccountId, activeEventId);
    });
  }
  
  var makeCalendarDialog = function() {
    $(showCalendarDialog).dialog({
      autoOpen : false,
      modal : true,
      draggable : false,
      resizable : false,
      buttons : {
        "Close" : function() {
          $(showCalendarDialog).dialog("close");
        }
      }
    });
  }
  
  var clearNewAccountForm = function() {
    $("#new_account_name").val("");
    $("#new_account_description").val("");
  }
  
  var clearNewEventForm = function() {
    $("#new_event_amount").val("");
    $("#new_event_description").val("");
    $('#event-transfer').attr('checked', false);
    $("#new_event_date").datepicker( "setDate", "0" );
  }
  
  var formatDate = function(date) {
    var curr_date = date.getDate();
    var curr_month = date.getMonth() + 1;
    var curr_year = date.getFullYear();
    return curr_year + "-" + curr_month + "-" + curr_date;
  }
  
  publicInterface.init = function() {
    makeAccountAccordion();
    makeNewAccountDialog();
    makeDeleteAccountDialog();
    makeDeleteEventDialog();
    makeNewEventDialog();
    makeCalendarDialog();
    $(addAccountButton).click(function(e){
      clearNewAccountForm();
      $(newAccountDialog).dialog("open");
    })
    accounting.init({
      onAccountRefresh: function() {
        $(accountContainer).accordion('destroy');
        makeAccountAccordion();
      },
      onEventListRendered: function() {
        $(deleteEventButton).button({
        icons: { primary: "ui-icon-trash" },
        });
        $(deleteEventButton).click(function(e) {
            $(deleteEventDialog).dialog("open");
            var eventId = $(this).closest("tr").attr("event-id");
            var accountId = $(this).closest("tr").attr("account-id");
          activeEventId = eventId;
          activeAccountId = accountId;
        });
      }
    });
    $(calendar).fullCalendar({
      theme: true,
      header: {
        left: 'prev,next today',
        center: 'title',
        right: ''
      },
      lazyFetching: false,
      editable: false,
      events: function(start, end, callback) {
        end.setDate(end.getDate()-1);
        accounting.getEventsForTimeRange(activeAccountId, formatDate(start), formatDate(end), function(events) {
          var dailyMinus = {},
              dailyPlus = {},
              dailyTransfer = {};
          _.each(events, function(event) {
            var date = event.event_date;
            if(event.change_type === 'change' && event.amount < 0) {
              if(dailyMinus[date]) dailyMinus[date] += event.amount;
              else dailyMinus[date] = event.amount;
            } else if(event.change_type === 'change' && event.amount >= 0) {
              if(dailyPlus[date]) dailyPlus[date] += event.amount;
              else dailyPlus[date] = event.amount;
            } else if(event.change_type === 'transfer') {
              if(dailyTransfer[date]) dailyTransfer[date] += event.amount;
              else dailyTransfer[date] = event.amount;
            }
          });
          var calEvents = [];
          _.each(_.pairs(dailyMinus), function(pair) {
            calEvents.push({
                title: "" + moneyRound(pair[1]),
                start: new Date(Date.parse(pair[0])),
                color: 'red'
            });
          });
          _.each(_.pairs(dailyPlus), function(pair) {
            calEvents.push({
                title: "" + moneyRound(pair[1]),
                start: new Date(Date.parse(pair[0])),
                color: 'green'
            });
          });
          _.each(_.pairs(dailyTransfer), function(pair) {
            calEvents.push({
                title: "" + moneyRound(pair[1]),
                start: new Date(Date.parse(pair[0])),
                color: 'orange'
            });
          });
          callback(calEvents);
        });
      }
    });
  };
  
  return publicInterface;
}());