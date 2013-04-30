"use strict";

var accounting = (function() {
  
  //Public interface
  var publicInterface = {};
  
  //Options
  var onAccountRefresh;
  
  var Accounts = newCollection('/rest/account');
  
  var AccountModel = Backbone.Model.extend({
    urlRoot: '/rest/account',
    defaults: {}
  });
  
  var AccountView = Backbone.View.extend({
    render: function(elem) {
      elem.html("");
      _.each(this.collection.models, function(model) {
        var template = _.template( $("#account_template").html(), {
          name: model.get("name"),
          description: model.get("description"),
          id: model.get("id")
        });
        elem.append(template);
        onAccountRefresh();
      });
      return this;
    },
    initialize: function() {
      var _this = this;
      this.collection.bind( "add", function() { _this.render(_this.$el); } );
    }
  });
  
  function newCollection(url) {
    var result = new Backbone.Collection;
    result.url = url;
    return result;
  }
  
  function collectionToJSON(collection) {
    return _.map(collection.models, function(model) { return model.toJSON(); } );
  }
  
  function parseOptions(opts) {
    opts = opts || {}
    onAccountRefresh = opts.onAccountRefresh || function() {};
  }
  
  publicInterface.listAccounts = function() {
    return collectionToJSON(Accounts);
  }
  
  publicInterface.createAccount = function(accountDetails) {
    var account = new AccountModel();
    account.save(accountDetails, {
      success: function (account) {
        Accounts.add(account);
      }
    });
  }
  
  publicInterface.init = function(opts) {
    parseOptions(opts);
    Accounts.fetch();
    var account_view = new AccountView({ el: $('#account_container'), collection: Accounts });
  }
  
  return publicInterface;
}());