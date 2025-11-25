Cosas que quedan por hacer:

-Cuando un respondedor no responde a la pregunta, hay que seguir la misma lógica que cuando falla. Ahora mismo simplemente se muestra un mensaje y se para el juego

-Hacer una selección de cartas de poder relativamente sencillas de implementar (cambiar las del use a la selección)

-Expandir los invariantes del use. Añadir invariantes relativos a las cartas seleccionadas

-Reflejar todos los invariantes en Java con asserts, como en una de las prácticas que hicimos

-Hacer lo del soil y probar los invariantes y tal

-Aumentar los bancos de preguntas (cada .csv). Es muy importante que sigáis exactamente el mismo formato que hay ahora mismo para que la lógica funcione. Si sois capaces de cambiarlo a una base de datos en condiciones mejor pero no es prioritario, con los .csv funciona

-Implementar alguna forma de registrar los perfiles de los usuarios (ahora mismo no se pide el nombre, ni hay un historial de efectividad para cada categoría) en una base de datos. Si lo veis muy complicado hacemos unos .csv de mock que se guarden en el propio repositorio y ya, como los de las preguntas. No me cambieis los IDs que casi todo depende de que se correspondan con posiciones en listas

-Añadir la lógica para actualizar esos perfiles con cada respuesta (clase respondedor)

-Implementar todo lo relativo a las cartas de poder. Yo haría primero que para todos los estados del jugador se checkee el inventario y establezca si una carta es utilizable para ese jugador o no (por ejemplo, no tiene sentido que el preguntador use la carta que reduce las posibles respuestas a la mitad). Usar el patrón estrategia, está a medias

-Hacer patrón iterador para sacar cartas de poder según rareza? No tengo muy claro como implementarlo la verdad

-Hacer algún patrón para los tipos de pregunta? Al principio cuando estaba haciendo el conceptual lo pensé pero al implementarlo me di cuenta de que todas funcionan exactamente igual así que lo veo un poco tontería

-Testeo, debugging, refactorización, etc.

-Visual paradigm. Yo esto lo dejaría para el final 

-La memoria

-Cualquier cosa que se haya hecho en las prácticas y/o se me haya olvidado mencionar

Intentad usar ramas según lo que vayáis a implementar. Por ejemplo para lo de las cartas, crear la rama feature/power_cards
