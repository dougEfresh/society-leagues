#!/bin/bash 
node_modules/.bin/watchify -t reactify  -o src/main/resources/public/app/js/bundle.js  src/main/resources/public/jsx/*.jsx 
