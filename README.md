# matsmaker

FIXME: description

## Installation

`git clone` this repo.

## Usage

It works best from a REPL. So:

    lein repl

And then:
  
    matsmaker.core> (price-for {:copper-setting 10 :copper-hook 5 :copper-band 5})
    1190

Modify `+recipes+` and `+mats+` to add recipes and mats.

### Bugs

There aren't any tests. Huge bug there.

`+recipes+` and `+mats+` are globals. Even though they are immutable they should probably be passed as an environment, or some kind of dynamic var or some such.
`+recipes+` and `+mats+` could be in the same map, perhaps with a type annotation, instead of segregating them.

Could probably do some stronger validation of recipes/mat relationships.

I'm sure there are more.

## License

Copyright © 2013 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
