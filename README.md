# audyx-toolbet [![Circle CI](https://circleci.com/gh/Audyx/audyx-toolbet.svg?style=svg)](https://circleci.com/gh/Audyx/audyx-toolbet)

##Usage

Add to your `project.clj` list of dependencies:

[![Clojars Project](http://clojars.org/viebel/audyx-toolbet/latest-version.svg)](http://clojars.org/viebel/audyx-toolbet)

## Development
### run localy
```bash
lein pdo midje :autotest target/classes test, cljx auto
```

### deploy 
```bash
lein install

lein deploy clojars
```
