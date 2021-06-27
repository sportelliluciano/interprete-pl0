(ns interprete-pl0.core-test
  (:require [clojure.test :refer :all]
            [interprete-pl0.core :refer :all]))

(deftest a-mayusculas-salvo-strings-test
  (testing "Sin strings"
    (is (= (a-mayusculas-salvo-strings "const Y = 2;") "CONST Y = 2;"))
  )
  (testing "Un solo string"
    (is (= 
      (a-mayusculas-salvo-strings "writeln ('Se ingresa un valor, se muestra su doble.');") 
      "WRITELN ('Se ingresa un valor, se muestra su doble.');")
    )
  )
  (testing "Dos strings"
    (is (= 
      (a-mayusculas-salvo-strings "writeln ('El valor es ', var, ' centímetros. (', pul, ' pulgadas).');") 
      "WRITELN ('El valor es ', VAR, ' centímetros. (', PUL, ' pulgadas).');")
    )
  )
  (testing "Comillas al principio y al final"
    (is (= (a-mayusculas-salvo-strings "'Test'") "'Test'"))
  )
  (testing "Tres strings"
    (is (= (a-mayusculas-salvo-strings "'Uno', 'Dos', 'Tres'") "'Uno', 'Dos', 'Tres'"))
  )
  (testing "String sin cerrar"
    (is (= (a-mayusculas-salvo-strings "'Uno") "'Uno"))
  )
)

(deftest palabra-reservada?-test
  (testing "Palabras reservadas"
    (is (palabra-reservada? "CALL"))
    (is (palabra-reservada? 'CALL))
    (is (palabra-reservada? "CONST"))
    (is (palabra-reservada? 'CONST))
    (is (palabra-reservada? "VAR"))
    (is (palabra-reservada? 'VAR))
    (is (palabra-reservada? "PROCEDURE"))
    (is (palabra-reservada? 'PROCEDURE))
    (is (palabra-reservada? "CALL"))
    (is (palabra-reservada? 'CALL))
    (is (palabra-reservada? "BEGIN"))
    (is (palabra-reservada? 'BEGIN))
    (is (palabra-reservada? "END"))
    (is (palabra-reservada? 'END))
    (is (palabra-reservada? "IF"))
    (is (palabra-reservada? 'IF))
    (is (palabra-reservada? "THEN"))
    (is (palabra-reservada? 'THEN))
    (is (palabra-reservada? "WHILE"))
    (is (palabra-reservada? 'WHILE))
    (is (palabra-reservada? "DO"))
    (is (palabra-reservada? 'DO))
    (is (palabra-reservada? "ODD"))
    (is (palabra-reservada? 'ODD))
    (is (palabra-reservada? "READLN"))
    (is (palabra-reservada? 'READLN))
    (is (palabra-reservada? "WRITELN"))
    (is (palabra-reservada? 'WRITELN))
    (is (palabra-reservada? "WRITE"))
    (is (palabra-reservada? 'WRITE))
  )
  (testing "Palabras no reservadas"
    (is (= false (palabra-reservada? "ASIGNAR")))
    (is (= false (palabra-reservada? 'ASIGNAR)))
    (is (= false (palabra-reservada? "35")))
    (is (= false (palabra-reservada? 35)))
  )
)

(deftest identificador?-test
  (testing "Identificadores válidos"
    (is (identificador? "V2"))
    (is (identificador? 'V2))
    (is (identificador? "LETRAS"))
    (is (identificador? 'LETRAS))
    (is (identificador? "LETRAS1NUMEROS2"))
    (is (identificador? 'LETRAS1NUMEROS2))
  )
  (testing "Número no es un identificador válido"
    (is (= false (identificador? 17)))
    (is (= false (identificador? "17")))
  )
  (testing "Identificador no puede empezar con números"
    (is (= false (identificador? "1abc")))
  )
  (testing "Identificador no puede ser palabra reservada"
    (is (= false (identificador? "CALL")))
    (is (= false (identificador? 'CALL)))
  )
)

(deftest cadena?-test
  (testing "Cadena válida"
    (is (cadena? "'Hola'"))
  )
  (testing "Cadena con comillas no balanceadas"
    (is (= false (cadena? "Hola'")))
    (is (= false (cadena? "'Hola")))
    (is (= false (cadena? 'Hola')))
  )
  (testing "Cadena sin comillas"
    (is (= false (cadena? "Hola")))
    (is (= false (cadena? 'Hola)))
  )
)

(deftest ya-declarado-localmente?-test
  (testing "Casos en documentación"
    (is (ya-declarado-localmente? 'Y '[[0] [[X VAR 0] [Y VAR 1]]]))
    (is (= false (ya-declarado-localmente? 'Z '[[0] [[X VAR 0] [Y VAR 1]]])))
    (is (= false (ya-declarado-localmente? 'Y '[[0 3 5] [[X VAR 0] [Y VAR 1] [INICIAR PROCEDURE 1] [Y CONST 2] [ASIGNAR PROCEDURE 2]]])))
    (is (ya-declarado-localmente? 'Y '[[0 3 5] [[X VAR 0] [Y VAR 1] [INICIAR PROCEDURE 1] [Y CONST 2] [ASIGNAR PROCEDURE 2] [Y CONST 6]]]))
  )
)

(deftest cargar-var-en-tabla-test
  (testing "Casos en documentación"
    (is (=
      (cargar-var-en-tabla '[nil () [VAR X] :error [[0] []] 0 [[JMP ?]]])
      '[nil () [VAR X] :error [[0] []] 0 [[JMP ?]]]
    ))
    (is (=
      (cargar-var-en-tabla '[nil () [VAR X] :sin-errores [[0] []] 0 [[JMP ?]]])
      '[nil () [VAR X] :sin-errores [[0] [[X VAR 0]]] 1 [[JMP ?]]]
    ))
    (is (=
      (cargar-var-en-tabla '[nil () [VAR X , Y] :sin-errores [[0] [[X VAR 0]]] 1 [[JMP ?]]])
      '[nil () [VAR X Y] :sin-errores [[0] [[X VAR 0] [Y VAR 1]]] 2 [[JMP ?]]]
    ))
  )
)

(deftest inicializar-contexto-local-test
  (testing "Casos en documentación"
    (is (=
      (inicializar-contexto-local '[nil () [] :error [[0] [[X VAR 0] [Y VAR 1] [INI PROCEDURE 1]]] 2 [[JMP ?]]])
      '[nil () [] :error [[0] [[X VAR 0] [Y VAR 1] [INI PROCEDURE 1]]] 2 [[JMP ?]]]
    ))
    (is (=
      (inicializar-contexto-local '[nil () [] :sin-errores [[0] [[X VAR 0] [Y VAR 1] [INI PROCEDURE 1]]] 2 [[JMP ?]]])
      '[nil () [] :sin-errores [[0 3] [[X VAR 0] [Y VAR 1] [INI PROCEDURE 1]]] 2 [[JMP ?]]]
    ))
  )
)

(deftest declaracion-var-test
  (testing "Casos en documentación"
    (is (= 
      (declaracion-var ['VAR (list 'X (symbol ",") 'Y (symbol ";") 'BEGIN 'X (symbol ":=") 7 (symbol ";") 'Y (symbol ":=") 12 (symbol ";") 'END (symbol ".")) [] :error [[0] []] 0 '[[JMP ?]]])
      ['VAR (list 'X (symbol ",") 'Y (symbol ";") 'BEGIN 'X (symbol ":=") 7 (symbol ";") 'Y (symbol ":=") 12 (symbol ";") 'END (symbol ".")) [] :error [[0] []] 0 '[[JMP ?]]]
    ))
    (is (=
      (declaracion-var ['VAR (list 'X (symbol ",") 'Y (symbol ";") 'BEGIN 'X (symbol ":=") 7 (symbol ";") 'Y (symbol ":=") 12 (symbol ";") 'END (symbol ".")) [] :sin-errores [[0] []] 0 '[[JMP ?]]])
      ['BEGIN (list 'X (symbol ":=") 7 (symbol ";") 'Y (symbol ":=") 12 (symbol ";") 'END (symbol ".")) ['VAR 'X (symbol ",") 'Y (symbol ";")] :sin-errores '[[0] [[X VAR 0] [Y VAR 1]]] 2 '[[JMP ?]]]
    ))
  )
)

(deftest procesar-signo-unitario-test
  (testing "Casos en documentación"
    (is (=
      (procesar-signo-unario ['+ (list 7 (symbol ";") 'Y ':= '- 12 (symbol ";") 'END (symbol ".")) ['VAR 'X (symbol ",") 'Y (symbol ";") 'BEGIN 'X (symbol ":=")] :error '[[0] [[X VAR 0] [Y VAR 1]]] 2 []])
      ['+ (list 7 (symbol ";") 'Y ':= '- 12 (symbol ";") 'END (symbol ".")) ['VAR 'X (symbol ",") 'Y (symbol ";") 'BEGIN 'X (symbol ":=")] :error '[[0] [[X VAR 0] [Y VAR 1]]] 2 []]
    ))
    (is (=
      (procesar-signo-unario [7 (list (symbol ";") 'Y ':= '- 12 (symbol ";") 'END (symbol ".")) ['VAR 'X (symbol ",") 'Y (symbol ";") 'BEGIN 'X (symbol ":=")] :sin-errores '[[0] [[X VAR 0] [Y VAR 1]]] 2 []])
      [7 (list (symbol ";") 'Y ':= '- 12 (symbol ";") 'END (symbol ".")) ['VAR 'X (symbol ",") 'Y (symbol ";") 'BEGIN 'X (symbol ":=")] :sin-errores '[[0] [[X VAR 0] [Y VAR 1]]] 2 []]
    ))
    (is (=
      (procesar-signo-unario ['+ (list 7 (symbol ";") 'Y ':= '- 12 (symbol ";") 'END (symbol ".")) ['VAR 'X (symbol ",") 'Y (symbol ";") 'BEGIN 'X (symbol ":=")] :sin-errores '[[0] [[X VAR 0] [Y VAR 1]]] 2 []])
      [7 (list (symbol ";") 'Y ':= '- 12 (symbol ";") 'END (symbol ".")) ['VAR 'X (symbol ",") 'Y (symbol ";") 'BEGIN 'X (symbol ":=") '+] :sin-errores '[[0] [[X VAR 0] [Y VAR 1]]] 2 []]
    ))
    (is (=
      (procesar-signo-unario ['- (list 7 (symbol ";") 'Y ':= '- 12 (symbol ";") 'END (symbol ".")) ['VAR 'X (symbol ",") 'Y (symbol ";") 'BEGIN 'X (symbol ":=")] :sin-errores '[[0] [[X VAR 0] [Y VAR 1]]] 2 []])
      [7 (list (symbol ";") 'Y ':= '- 12 (symbol ";") 'END (symbol ".")) ['VAR 'X (symbol ",") 'Y (symbol ";") 'BEGIN 'X (symbol ":=") '-] :sin-errores '[[0] [[X VAR 0] [Y VAR 1]]] 2 []]
    ))
  )
)

(deftest termino-test
  (testing "Casos en documentación"
    (is (=
      (termino ['X (list '* 2 'END (symbol ".")) ['VAR 'X (symbol ";") 'BEGIN 'X (symbol ":=")] :error '[[0] [[X VAR 0]]] 1 []])
      ['X (list '* 2 'END (symbol ".")) ['VAR 'X (symbol ";") 'BEGIN 'X (symbol ":=")] :error '[[0] [[X VAR 0]]] 1 []]
    ))
    (is (=
      (termino ['X (list '* 2 'END (symbol ".")) ['VAR 'X (symbol ";") 'BEGIN 'X (symbol ":=")] :sin-errores '[[0] [[X VAR 0]]] 1 []])
      ['END (list (symbol ".")) ['VAR 'X (symbol ";") 'BEGIN 'X (symbol ":=") 'X '* 2] :sin-errores '[[0] [[X VAR 0]]] 1 [['PFM 0] ['PFI 2] 'MUL]]
    ))
  )
)

(deftest expresion-test
  (testing "Casos en documentación"
    (is (=
      (expresion ['- (list (symbol "(") 'X '* 2 '+ 1 (symbol ")") 'END (symbol ".")) ['VAR 'X (symbol ";") 'BEGIN 'X (symbol ":=")] :error '[[0] [[X VAR 0]]] 1 []])
      ['- (list (symbol "(") 'X '* 2 '+ 1 (symbol ")") 'END (symbol ".")) ['VAR 'X (symbol ";") 'BEGIN 'X (symbol ":=")] :error '[[0] [[X VAR 0]]] 1 []]
    ))
    (is (=
      (expresion ['+ (list (symbol "(") 'X '* 2 '+ 1 (symbol ")") 'END (symbol ".")) ['VAR 'X (symbol ";") 'BEGIN 'X (symbol ":=")] :sin-errores '[[0] [[X VAR 0]]] 1 []])
      ['END (list (symbol ".")) ['VAR 'X (symbol ";") 'BEGIN 'X (symbol ":=") '+ (symbol "(") 'X '* 2 '+ 1 (symbol ")")] :sin-errores '[[0] [[X VAR 0]]] 1 '[[PFM 0] [PFI 2] MUL [PFI 1] ADD]]
    ))
    (is (=
      (expresion ['- (list (symbol "(") 'X '* 2 '+ 1 (symbol ")") 'END (symbol ".")) ['VAR 'X (symbol ";") 'BEGIN 'X (symbol ":=")] :sin-errores '[[0] [[X VAR 0]]] 1 []])
      ['END (list (symbol ".")) ['VAR 'X (symbol ";") 'BEGIN 'X (symbol ":=") '- (symbol "(") 'X '* 2 '+ 1 (symbol ")")] :sin-errores '[[0] [[X VAR 0]]] 1 '[[PFM 0] [PFI 2] MUL [PFI 1] ADD NEG]]
    ))
  )
)

(deftest aplicar-aritmetico-test
  (testing "Casos en documentación"
    (is (=
      (aplicar-aritmetico + [1 2])
      '[3]
    ))
    (is (=
      (aplicar-aritmetico - [1 4 1])
      '[1 3]
    ))
    (is (=
      (aplicar-aritmetico * [1 2 4])
      '[1 8]
    ))
    (is (=
      (aplicar-aritmetico / [1 2 4])
      '[1 0]
    ))
    (is (=
      (aplicar-aritmetico + nil)
      'nil
    ))
    (is (=
      (aplicar-aritmetico + [])
      '[]
    ))
    (is (=
      (aplicar-aritmetico + [1])
      '[1]
    ))
    (is (=
      (aplicar-aritmetico 'hola [1 2 4])
      '[1 2 4]
    ))
    (is (=
      (aplicar-aritmetico count [1 2 4])
      '[1 2 4]
    ))
    (is (=
      (aplicar-aritmetico + '[a b c])
      '[a b c]
    ))
  )
)

(deftest aplicar-relacional-test
  (testing "Casos en documentación"
  (is (=
    (aplicar-relacional > [7 5])
    '[1]
  ))
  (is (=
    (aplicar-relacional > [4 7 5])
    '[4 1]
  ))
  (is (=
    (aplicar-relacional = [4 7 5])
    '[4 0]
  ))
  (is (=
    (aplicar-relacional not= [4 7 5])
    '[4 1]
  ))
  (is (=
    (aplicar-relacional < [4 7 5])
    '[4 0]
  ))
  (is (=
    (aplicar-relacional <= [4 6 6])
    '[4 1]
  ))
  (is (=
    (aplicar-relacional <= '[a b c])
    '[a b c]
  ))
  )
)

(deftest generar-test
  (testing "Casos en documentación"
    (is (=
      (generar '[nil () [VAR X] :sin-errores [[0] []] 0 [[JMP ?]]] 'HLT)
      '[nil () [VAR X] :sin-errores [[0] []] 0 [[JMP ?] HLT]]
    ))
    (is (=
      (generar '[nil () [VAR X] :sin-errores [[0] []] 0 [[JMP ?]]] 'PFM 0)
      '[nil () [VAR X] :sin-errores [[0] []] 0 [[JMP ?] [PFM 0]]]
    ))
    (is (=
      (generar '[nil () [VAR X] :error [[0] []] 0 [[JMP ?]]] 'HLT)
      '[nil () [VAR X] :error [[0] []] 0 [[JMP ?]]]
    ))
    (is (=
      (generar '[nil () [VAR X] :error [[0] []] 0 [[JMP ?]]] 'PFM 0)
      '[nil () [VAR X] :error [[0] []] 0 [[JMP ?]]]
    ))
  )
)

(deftest fixup-test
  (testing "Casos en documentación"
    (is (=
      (fixup ['WRITELN (list 'END (symbol ".")) [] :error [[0 3] []] 6 '[[JMP ?] [JMP ?] [CAL 1] RET]] 1)
      ['WRITELN (list 'END (symbol ".")) [] :error [[0 3] []] 6 '[[JMP ?] [JMP ?] [CAL 1] RET]]
    ))
    (is (=
      (fixup ['WRITELN (list 'END (symbol ".")) [] :sin-errores [[0 3] []] 6 '[[JMP ?] [JMP ?] [CAL 1] RET]] 1)
      ['WRITELN (list 'END (symbol ".")) [] :sin-errores [[0 3] []] 6 '[[JMP ?] [JMP 4] [CAL 1] RET]]
    ))
    (is (=
      (fixup ['WRITELN (list 'END (symbol ".")) [] :sin-errores [[0 3] []] 6 '[[JMP ?] [JMP 4] [CAL 1] RET [PFM 2] OUT NL RET]] 0)
      ['WRITELN (list 'END (symbol ".")) [] :sin-errores [[0 3] []] 6 '[[JMP 8] [JMP 4] [CAL 1] RET [PFM 2] OUT NL RET]]
    ))
  )
)

(deftest generar-operador-relacional-test
  (testing "Casos en documentación"
    (is (=
      (generar-operador-relacional ['WRITELN (list 'END (symbol ".")) [] :error [[0 3] []] 6 '[[JMP ?] [JMP ?] [CAL 1] RET]] '=)
      '[WRITELN (END .) [] :error [[0 3] []] 6 [[JMP ?] [JMP ?] [CAL 1] RET]]
    ))
    (is (=
      (generar-operador-relacional ['WRITELN (list 'END (symbol ".")) [] :sin-errores [[0 3] []] 6 '[[JMP ?] [JMP ?] [CAL 1] RET]] '+)
      '[WRITELN (END .) [] :sin-errores [[0 3] []] 6 [[JMP ?] [JMP ?] [CAL 1] RET]]
    ))
    (is (=
      (generar-operador-relacional ['WRITELN (list 'END (symbol ".")) [] :sin-errores [[0 3] []] 6 '[[JMP ?] [JMP ?] [CAL 1] RET]] '=)
      '[WRITELN (END .) [] :sin-errores [[0 3] []] 6 [[JMP ?] [JMP ?] [CAL 1] RET EQ]]
    ))
    (is (=
      (generar-operador-relacional ['WRITELN (list 'END (symbol ".")) [] :sin-errores [[0 3] []] 6 '[[JMP ?] [JMP ?] [CAL 1] RET]] '>=)
      '[WRITELN (END .) [] :sin-errores [[0 3] []] 6 [[JMP ?] [JMP ?] [CAL 1] RET GTE]]
    ))
  )
  (testing "El tipo de secuencia del bytecode actualizado es vector []"
    (is (vector? 
          (bytecode
            (generar-operador-relacional ['WRITELN (list 'END (symbol ".")) [] :sin-errores [[0 3] []] 6 '[[JMP ?] [JMP ?] [CAL 1] RET]] '>=)
          )
    ))
    (is (vector? 
          (bytecode
            (generar-operador-relacional ['WRITELN (list 'END (symbol ".")) [] :sin-errores [[0 3] []] 6 '[]] '>=)
          )
    ))
  )
)

(deftest generar-signo-test
  (testing "Casos en documentación"
    (is (=
      (generar-signo [nil () [] :error '[[0] [[X VAR 0]]] 1 '[MUL ADD]] '-)
      [nil () [] :error '[[0] [[X VAR 0]]] 1 '[MUL ADD]]
    ))
    (is (=
      (generar-signo [nil () [] :error '[[0] [[X VAR 0]]] 1 '[MUL ADD]] '+)
      [nil () [] :error '[[0] [[X VAR 0]]] 1 '[MUL ADD]]
    ))
    (is (=
      (generar-signo [nil () [] :sin-errores '[[0] [[X VAR 0]]] 1 '[MUL ADD]] '+)
      [nil () [] :sin-errores '[[0] [[X VAR 0]]] 1 '[MUL ADD]]
    ))
    (is (=
      (generar-signo [nil () [] :sin-errores '[[0] [[X VAR 0]]] 1 '[MUL ADD]] '*)
      [nil () [] :sin-errores '[[0] [[X VAR 0]]] 1 '[MUL ADD]]
    ))
    (is (=
      (generar-signo [nil () [] :sin-errores '[[0] [[X VAR 0]]] 1 '[MUL ADD]] '-)
      [nil () [] :sin-errores '[[0] [[X VAR 0]]] 1 '[MUL ADD NEG]]
    ))
  )
)