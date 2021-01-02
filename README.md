# SeminarPlayScala

Dies ist eine Play-Webanwendung zu Demonstrationszwecken. Es wird gezeigt wie Play und Scala eingesetzt werden können, um eine einfache Webanwendung zu erstellen.

Dieses Beispiel ist stark angelehnt an die Beispiele aus [1] und [2].

Um die Anwendungen inkl. Authentifizierungslogik ausführen zu können, muss auf dem lokalen Rechner eine Datenbank "scaladb" über einen
Postgresql-Datenbankserver unter "jdbc:postgresql://localhost:5432/scaladb" erreichbar sein. Die Zugangsdaten müssen

username:  scalauser
password:  scalapass

lauten.

Das folgende Video https://www.youtube.com/watch?v=xaWlS9HtWYw gibt dazu eine knappe und ausreichende Einführung.


Anschließend kann die Anwendung gestartet werden, indem im Wurzelverzeichnis über die Konsole "sbt run" eingegeben wird. Die Anwendung ist dann im Browser unter der URL
http://localhost:9000/ verfügbar.


[1] Denis Kalinin, Modern Web Development With Scala

[2] Shiti Saxena, Mastering Play Framework for Scala
