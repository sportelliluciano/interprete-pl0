## Extensión del intérprete: OPERADOR TERNARIO

El operador ternario es un tipo de expresión nuevo ya que devuelve un valor.
Para la implementación se modificó la gramática de la siguiente forma:

```
expresion ::= 
    ("+" | "-")? termino (("+" | "-") termino)* |
    "¿" condicion "?" expresion ":" expresion
```

Notar que se agregó el símbolo "¿" para simplificar las modificaciones al parser. Esto es para evitar tener una recursión izquierda (ya que la regla `condicion` requiere una `expresion` como primer elemento de la izquierda), y no necesitar un *look-ahead* de más de un token.


