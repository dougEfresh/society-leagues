var React = require('react/addons');
var Requests = require('./challenges/request.jsx');

console.log(window.location.search.indexOf('request'));

if (window.location.search.indexOf('request') !== -1) {
    <Requests />
}