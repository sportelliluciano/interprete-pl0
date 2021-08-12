# TP Final - Lenguajes Formales: Intérprete de PL/0

Intérprete de PL/0 escrito en Clojure como trabajo práctico final de la
materia 75.14 - Lenguajes Formales, a cargo de Diego Corsi y Pablo Bergman.

## Finales y práctica 

- [Final del 11/08/2021 - Implementar POW(base, exp)](https://github.com/sportelliluciano/interprete-pl0/commit/4a8b8f8da3e08d078b64b1e333df31893edddc3b)
- [Práctica - Implementar operador ternario](https://github.com/sportelliluciano/interprete-pl0/commit/6c8926e1d6df11141f5fb8866dd4b5cfe8f6834b)

## Para correr / buildear

Este proyecto utiliza [Leiningen](https://github.com/technomancy/leiningen/blob/stable/README.md). Para instalarlo en Ubuntu/Debian: `sudo apt install leiningen`.

- Para correr el proyecto: `lein run`
- Para correr los tests: `lein test`
- Para generar el `jar`: `lein ubergen`
- Para arrancar un intérprete interactivo con las funciones del proyecto cargadas: `lein repl`. El `jar` generado quedará en `target/uberjar/interprete-pl0-version-standalone.jar`.
