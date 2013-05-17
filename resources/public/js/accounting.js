"use strict";

var accounting = (function() {
  
  //Public interface
  var publicInterface = {};
  
  //Options
  var onAccountRefresh;
  var onEventListRendered;
  
  var Accounts = newCollection('/rest/account');
  var Events = {};
  
  var AccountModel = Backbone.Model.extend({
    urlRoot: '/rest/account',
    defaults: {}
  });
  
  var EventModel = Backbone.Model.extend({
    urlRoot: '/rest/event',
    defaults: {}
  });
  
  var moneyRound = function(val) {
    return Math.round(val*100)/100
  }
  
  var AccountView = Backbone.View.extend({
    render: function(elem) {
      var totTmpl = _.template( $("#account_template").html(), {
        name: "Total",
        description: "Sum of all events on all accounts",
        id: 0,
        total: 0,
        total_class: 0 < 0 ? "negative" : "positive",
        tool_display: "none"
      });
      elem.append(totTmpl);
      _.each(this.collection.models, function(model) {
        var template = _.template( $("#account_template").html(), {
          name: model.get("name"),
          description: model.get("description"),
          id: model.get("id"),
          total: model.get("total"),
          total_class: model.get("total") < 0 ? "negative" : "positive",
          tool_display: "block"
        });
        elem.append(template);
        onAccountRefresh();
      });
      return this;
    },
    initialize: function() {}
  });
  
  var amountClassFromModel = function(model) {
    if(model.get("change_type") === "transfer") return "transfer";
    return model.get("amount") < 0 ? "negative" : "positive";
  }
  
  var EventView = Backbone.View.extend({ 
    render: function(elem) {
      elem.html("");
      _.each(this.collection.models, function(model) {
        var template = _.template( $("#event_template").html(), {
          description: model.get("description"),
          id: model.get("id"),
          amount: model.get("amount"),
          amount_class: amountClassFromModel(model),
          event_date: model.get("event_date"),
          account_id: model.get("account_id"),
          cumulative_amount: model.get("cumulative_amount"),
          cumulative_class: model.get("cumulative_amount") < 0 ? "negative" : "positive"
        });
        elem.append(template);
      });
      return this;
    },
    initialize: function() {}
  });
  
  function newCollection(url) {
    var result = new Backbone.Collection;
    result.url = url;
    return result;
  }
  
  function parseOptions(opts) {
    opts = opts || {}
    onAccountRefresh = opts.onAccountRefresh || function() {};
    onEventListRendered = opts.onEventListRendered || function() {};
  }
    
  publicInterface.createAccount = function(accountDetails) {
    var account = new AccountModel();
    account.save(accountDetails, {
      success: function (account) {
        Accounts.add(account);
        var id = account.get("id");
        Events[id] = newCollection('/rest/account/' + id + '/events');
      }
    });
  }
  
  publicInterface.deleteAccount = function(id) {
    var model = Accounts.get(id);
    model.destroy();
    Accounts.remove(model);
    delete Events[id];
  }
  
  publicInterface.renderAccountEvents = function(element, account_id) {
    if(account_id !== undefined) {
      var events = Events[account_id];
      events.fetch({
        success: function() {
          var event_view = new EventView({ el: element, collection: events });
          event_view.render(event_view.$el);
          onEventListRendered();
        }
      });
    }
  }
  
  publicInterface.init = function(opts) {
    parseOptions(opts);
    var account_view = new AccountView({ el: $('#account_container'), collection: Accounts });
    Accounts.fetch({
      success: function() {
        _.each(Accounts.models, function(account) {
          var id = account.get("id");
          Events[id] = newCollection('/rest/account/' + id + '/events');
        });
        Events[0] = newCollection('/rest/event');
        account_view.render(account_view.$el);
        Accounts.bind( "add", function() { account_view.render(account_view.$el); } );
        Accounts.bind( "remove", function() { account_view.render(account_view.$el); } );
        Accounts.bind( "change", function() { account_view.render(account_view.$el); } );
      }
    });
  }
  
  publicInterface.createEvent = function(eventDetails) {
    var event = new EventModel();
    event.save(eventDetails, {
      success: function (event) {
        var account = Accounts.get(event.get("account_id"));
        var old_total = account.get("total") || 0;
        account.set("total", moneyRound(old_total + event.get("amount")));
      }
    });
  }
  
  publicInterface.deleteEvent = function(accountId, eventId) {
    var model = Events[accountId].get(eventId);
    var account = Accounts.get(accountId); 
    var old_total = account.get("total")
    account.set("total", moneyRound(old_total - model.get("amount")));
    model.destroy();
    Events[accountId].remove(model);
  }
  
  return publicInterface;
}());