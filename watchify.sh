#!/bin/bash 
../node_modules/.bin/watchify -v -d -t reactify  -o src/main/resources/public/app/js/bundle.js  src/main/resources/public/jsx/*.jsx  src/main/resources/public/jsx/*/*.jsx  
