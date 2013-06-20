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
      showCalendarDialog  = "#calendar_dialog";
  
  // Selection status
  var activeAccountId = null;
  var activeEventId = null;
  
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
      $(showCalendarDialog).dialog({
        height: $(window).height()*0.9,
        width: $(window).width()*0.9
      });
      $(showCalendarDialog).dialog("open");
    });
    $(showCalendarButton).button({
      icons: { primary: "ui-icon-calendar" }
    });
  }
  
  var makeNewAccountDialog = function() {
    $(newAccountDialog).dialog({
          autoOpen: false,
          height: 360,
          width: 350,
          modal: true,
          draggable: false,
          resizable: false,
          buttons: {
            "Create": function() {
                accounting.createAccount({
                  name: $("#new_account_name").val(),
                  description: $("#new_account_description").val()
              });
              $(newAccountDialog).dialog("close");
            },
            "Cancel": function() { 
                $(newAccountDialog).dialog("close"); 
            }
          }
        });
  }
  
  var makeNewEventDialog = function() {
    $(newEventDialog).dialog({
          autoOpen: false,
          height: 370,
          width: 400,
          modal: true,
          draggable: false,
          resizable: false,
          buttons: {
            "Create": function() {
                var isTransfer = $('#event-transfer').is(':checked')
                accounting.createEvent({
                  account_id: activeAccountId,
                  description: $("#new_event_description").val(),
                  amount: $("#new_event_amount").val(),
                  change_type: (isTransfer ? "transfer" : "change"),
                  event_date: $("#new_event_date").val()
                })
              $(newEventDialog).dialog("close");
            },
            "Cancel": function() { 
                $(newEventDialog).dialog("close"); 
            }
          }
        });
    $("#new_event_date").datepicker({ dateFormat: "yy-mm-dd" });
    $("#new_event_date").datepicker( "setDate", "0" );
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
    makeDeleteConfirmationDialog(
        $(deleteAccountDialog),
        function() {
          accounting.deleteAccount(activeAccountId);
        }
    );
  }
  
  var makeDeleteEventDialog = function() {
    makeDeleteConfirmationDialog(
        $(deleteEventDialog),
        function() {
          accounting.deleteEvent(activeAccountId, activeEventId);
        }
    );
  }
  
  var makeCalendarDialog = function() {
    $(showCalendarDialog).dialog({
      autoOpen: false,
      modal: true,
      draggable: false,
      resizable: false,
      buttons: {
        "Close": function() { 
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
  };
  
  return publicInterface;
}());