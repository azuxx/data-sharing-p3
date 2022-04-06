JC= javac
SOURCES_SERVER=main/MainServer.java interfacciaGrafica/GUIServer.java server/ServerImpl.java server/Server.java client/Client.java interfacciaGrafica/AggiungiComponente.java			
SOURCES_CLIENT=main/MainClient.java interfacciaGrafica/GUIMusharilla.java server/Server.java client/ClientImpl.java client/Client.java risorsa/Risorsa.java interfacciaGrafica/AggiungiComponente.java

	
server: main.MainServer.class
	rmic server.ServerImpl
	java main.MainServer&
	java main.MainServer&
	java main.MainServer&

client: main.MainClient.class
	rmic client.ClientImpl
	java main.MainClient&
	java main.MainClient&
	java main.MainClient&


main.MainServer.class:$(SOURCES_SERVER)
	#REMIND: prima di fare make server, fare rmiregistry&
	$(JC) $(SOURCES_SERVER)
	
main.MainClient.class:$(SOURCES_CLIENT)
	$(JC) $(SOURCES_CLIENT)


clean:
	rm main/*.class interfacciaGrafica/*.class server/*.class client/*.class risorsa/*.class
	


