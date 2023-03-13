# TO-DOs

## Löschen vorbestehender Strukturen im generierten SQL-Skript

Das Löschen vorbestehender Tabellen und Relationen im generierten SQL-Skript ist aus den folgen zwei Gründen
unvollständig:

* Es ist unvollständig, da es sich ausschließlich auf Entitäten bezieht, die im letzten Lauf des Generators konfiguriert waren.
Andere Entitäten werden nicht gelöscht.
* Das Ausschalten und Wiedereinschalten der referenziellen Integrität funktioniert nur in MySQL, in H2 jedoch offenbar nicht vollständig.

Daher:

* Funktionalität grundsätzlich beibehalten, aber per default nicht ausführen, sondern nur mit entsprechender Aufrufkonfiguration.
* Oder besser: Durch einen unabhängigen anderen Mechanismus ersetzen, der eine vollstänidge Löschung vornimmt. Diesen inhaltlich
vom Skript trennen.