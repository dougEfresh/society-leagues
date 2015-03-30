node_modules/.bin/browserify -t reactify  src/main/resources/public/jsx/*.jsx | node_modules/.bin/uglifyjs  --stats > src/main/resources/public/app/js/bundle.js
