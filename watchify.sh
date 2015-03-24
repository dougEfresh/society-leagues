#!/bin/bash 
watchify -t reactify --exclude 'bundle.js' -o src/main/resources/public/app/js/bundle.js src/main/resources/public/jsx/*.jsx
