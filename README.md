# data-sharing-p3

Progetto di Programmazione Concorrente e Distribuita (A.A 2009/2010) - Project of Concurrent and Distributed Programming (A.A 2009/2010)

Titolo: Data Sharing

Il progetto consiste nella realizzazione di un sistema di sharing per la condivisione e lo scambio di un certo numero di risorse. Il sistema è costituito da un certo numero di Server, che supponiamo sempre attivi, e da un certo numero di Client che si connettono e si disconnettono dal sistema. 
Un cliente che si connette al sistema mette a disposizione l’insieme di risorse che possiede.
Un cliente "C" connesso può richiedere al sistema una risorsa "R" di cui ha bisogno; il server a cui "C" è connesso dovrà indicare a "C" l'elenco dei clienti che sono attualmente connessi al sistema e in possesso di una copia di "R". "C" quindi comunicherà direttamente con uno dei clienti dell’elenco per ottenere una copia di "R" e durante la fase di scaricamento di "R", il cliente
contattato si disconnette dal sistema, "C" deve poter reindirizzare la richiesta di "R" ad un altro dei clienti che la possiedono.
Il programma deve gestire richieste e scambi concorrenti di diverse risorse. L’applicazione inoltre deve essere distribuita, cioè i vari client e server devono essere rappresentati da programmi distinti che possono risiedere su JVM distinte.
