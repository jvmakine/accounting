"use strict";

var accountingUi = (function() {
  //Public interface
  var publicInterface = {};
  
  var activeAccountId = null;
  var activeEventId = null;

  var makeAccountAccordion = function() {
    $("#account_container").accordion({ collapsible: true, active: null,
      beforeActivate: function( event, ui ) {
        var panel = ui.newPanel;
        accounting.renderAccountEvents($(panel).children('.account-events'), $(panel).attr("account-id"));
      },
      heightStyle: "content"});
    $(".delete-account-button").unbind('click');
    $(".delete-account-button").click(function(event) {
      event.stopPropagation();
      event.preventDefault();
      var accountId = $(this).closest("h3").attr("account-id");
      activeAccountId = accountId;
      $("#delete_account_dialog").dialog("open");
      });
    $(".delete-account-button").button({
      icons: { primary: "ui-icon-trash" }
    });
    $(".add-event-button").tooltip({
      content: "Delete the account" 
    });
    $(".add-event-button").button({
      icons: { primary: "ui-icon-plusthick" }
    });
    $(".add-event-button").click(function(event) {
      event.stopPropagation();
      event.preventDefault();
      var accountId = $(this).closest("h3").attr("account-id");
      activeAccountId = accountId;
      clearNewEventForm();
      $("#new_event_dialog").dialog("open");
      });
  }
  
  var makeNewAccountDialog = function() {
    $("#new_account_dialog").dialog({
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
              $("#new_account_dialog").dialog("close");
            },
            "Cancel": function() { 
                $("#new_account_dialog").dialog("close"); 
            }
          }
        });
  }
  
  var makeNewEventDialog = function() {
    $("#new_event_dialog").dialog({
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
              $("#new_event_dialog").dialog("close");
            },
            "Cancel": function() { 
                $("#new_event_dialog").dialog("close"); 
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
        $("#delete_account_dialog"),
        function() {
          accounting.deleteAccount(activeAccountId);
        }
    );
  }
  
  var makeDeleteEventDialog = function() {
    makeDeleteConfirmationDialog(
        $("#delete_event_dialog"),
        function() {
          accounting.deleteEvent(activeAccountId, activeEventId);
        }
    );
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
    $("#new_account_button").click(function(e){
      clearNewAccountForm();
      $("#new_account_dialog").dialog("open");
    })
    accounting.init({
      onAccountRefresh: function() {
        $("#account_container").accordion('destroy');
        makeAccountAccordion();
      },
      onEventListRendered: function() {
        $(".delete-event-button").button({
        icons: { primary: "ui-icon-trash" },
        });
        $(".delete-event-button").click(function(e) {
            $("#delete_event_dialog").dialog("open");
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