"use strict";
var accounting = (function() {
  
  var publicInterface = {};
  
  var Accounts = newCollection('/rest/account');
  
  var AccountModel = Backbone.Model.extend({
    urlRoot: '/rest/account',
    defaults: {}
  });
  
  function newCollection(url) {
    var result = new Backbone.Collection;
    result.url = url;
    return result;
  }
  
  function collectionToJSON(collection) {
    return _.map(collection.models, function(model) { return model.toJSON(); } );
  }
  
  publicInterface.listAccounts = function() {
    return collectionToJSON(Accounts);
  }
  
  publicInterface.init = function() {
    Accounts.fetch();
  }
  
  return publicInterface;
}());