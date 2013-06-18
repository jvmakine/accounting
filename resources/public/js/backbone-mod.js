$(function() {
  
  var oldSync = Backbone.sync;
  
  Backbone.sync = function(method, model, opts) {
    var oldError = opts.error;
    oldSync(method, model, _.extend(opts, {
      error: function(xhr, text_status, error_thrown) {
        if(xhr.status == 401) { //Session expired, redirect to login
          window.location.replace("/login");
        } else if(oldError) {
          oldError(xhr, text_status, error_thrown);
        }
      }
    }));
  }
  
});