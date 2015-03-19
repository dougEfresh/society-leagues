var t = require('tcomb-form');
var React = require('react');
var Form = t.form.Form;

// define a model
var Person = t.struct({
  name: t.Str, // two string properties
  surname: t.Str
});

var App = React.createClass({

  onClick: function () {
    var value = this.refs.form.getValue();
    // getValue returns null if validation failed
    if (value) {
      console.log(value);
    }
  },

  render: function () {
    return (
      <div>
        <Form ref="form" type={Person} />
        <button className="btn btn-primary" onClick={this.onClick}>Click me</button>
      </div>
    );
  }

});

React.render(
    <App />,document.getElementById('content'));


