package server;

import interfacciaGrafica.GUIServer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;
import client.Client;

public class ServerImpl extends UnicastRemoteObject implements Server{
	/*variabili statiche*/
	private static final String HOST="localhost";
	/*variabili d'istanza*/
	private String nomeServer;
	private Vector<Client> listaClient=new Vector<Client>();//ad 1 server si connettono(registrano) N client
	private Vector<Server> listaServer=new Vector<Server>();
	private GUIServer serverGui;
	private boolean ricerca;//per determinare che sta facendo la ricerca di una risorsa
	private boolean connessione;//per determinare se qualcuno si sta connettendo
	public ServerImpl(String n, GUIServer g) throws RemoteException, MalformedURLException {
		super();
		nomeServer=n;
		serverGui=g;
		Naming.rebind("rmi://"+HOST+"/Servers/"+nomeServer, this);//registro su rmi il server
	}
	public static final String getHOST(){
		return HOST;
	}
	public String aggiorna() throws RemoteException, MalformedURLException, NotBoundException{
		String s="\n";
		String [] listaServerRMI=Naming.list(HOST+"/Servers/");//vettore che contiene gli indirizzi dei server su RMI
		if(listaServerRMI.length<=1){
			s="Nessun server registrato!\n";
		}
		else{
			//aggiornamento liste logica e grafica del server corrente
			serverGui.aggiornaListaServer(listaServerRMI);
			
			//aggiornamento liste grafiche server remoti
			for(int i=0;i<listaServer.size();i++){//ogni server remoto a cui è stato connesso
				listaServer.get(i).aggiornaListaServerGUIServerRemota(listaServerRMI);
			}
		}
		return s;
	}
	public void chiudiServer() throws RemoteException, MalformedURLException, NotBoundException{
		Naming.unbind("rmi://"+HOST+"/Servers/"+nomeServer);
    	System.out.println("Fatto unbind.");
    	String [] listaServerRMI=Naming.list(HOST+"/Servers/");
    	for(int i=0;i<listaServer.size();i++){
    		listaServer.get(i).rimuoviServerDaRemoto(nomeServer);
    		listaServer.get(i).aggiornaListaServerGUIServerRemota(listaServerRMI);
    	}
    	System.out.println("Rimosso dalla lista dei server ancora attivi");
	}
	public String getNomeServer(){
		return nomeServer;
	}
	public String getNomeServerRemoto() throws RemoteException {
		return nomeServer;
	}
	public void registraClient(Client c) throws RemoteException{
		try{
			synchronized(listaClient){
				//impostare a vero la connessione e mandare in wait se cè ricerca
				connessione=true;
				while(ricerca==true){
					listaClient.wait();
				}
				listaClient.add(c);
				connessione=false;
				listaClient.notifyAll();
			}
		}
		catch(InterruptedException ecc1){
			System.err.println("interrotto wait");
		}
	}
	public void rimuoviClient(Client c) throws RemoteException{
		try{
			synchronized(listaClient){
				while(ricerca==true || connessione==true){
					listaClient.wait();
				}
				listaClient.remove(c);
			}
		}
		catch(InterruptedException ecc1){
			System.err.println("interrotto wait");
		}
	}
	/*remote exception sulla segnatura del metodo perche' il chiamante gestisce la caduta al server a cui il client che fa la ricerca è connesso.
	dentro il metodo opportuni try e catch dentro i for perchè nel caso un client o un server non sia raggiungibile questo deve essere ignorato e la ricerca non deve interrompersi ma proseguire.*/
	public Vector<Client> ricerca(String id, String nomeClientInvocRic) throws RemoteException{
		Vector<Client> client=new Vector<Client>();
		//mettere sincronized su listaclient per controllare se cè connessione e fare wait e ricerca=true
		try{
			synchronized (listaClient) {
				ricerca=true;
				while(connessione==true){
					listaClient.wait();
				}
			}
		}
		catch(InterruptedException ecc1){
			System.err.println("interrotto wait");
		}
		//ricerca locale
		for(int i=0;i<listaClient.size();i++){
			try{
				if((listaClient.get(i).getNomeClientRemoto().equals(nomeClientInvocRic))==false){
					if(listaClient.get(i).hasRisorsa(id)){
						client.add(listaClient.get(i));
					}
				}
			}
			catch(RemoteException ecc1){
				System.out.println("client remoto non raggiungibile. Ignorato, continuazione ricerca...");
			}
		}
		if(client.isEmpty()){
			//non è stato trovato niente in locale. quindi ricerca remota
			for(int i=0;i<listaServer.size();i++){
				try{
					for(int j=0;j<listaServer.get(i).getListaClientDaRemoto().size();j++){
						try{
							if(listaServer.get(i).getListaClientDaRemoto().get(j).hasRisorsa(id)){
								client.add(listaServer.get(i).getListaClientDaRemoto().get(j));
							}
						}
						catch (RemoteException ecc1) {
							System.out.println("client remoto non raggiungibile. Ignorato, continuazione ricerca...");
						}
					}
				}
				catch(RemoteException ecc1){
					System.out.println("server remoto non raggiungibile. Ignorato, continuazione ricerca...");
				}
			}
		}
		//fine ricerca, notificare lista client in wait
		synchronized (listaClient) {
			ricerca=false;
			listaClient.notifyAll();
		}
		return client;
	}
	public String notificaRegistrazione(String n,String s) throws RemoteException {
			for (int i = 0; i < listaClient.size(); i++) {
				try{
					if(listaClient.get(i).getNomeClientRemoto().equals(n))
						return listaClient.get(i).notificaRegistrato(s);
				}
				catch (RemoteException e) {
					System.out.println("impossibile notificare il client");
				}
			}
			return null;
	}
	public String notificaConnesso(String p) throws RemoteException {
		 return "Notifica del client: " + p;
	}
	public void connettiAServer(Server s){
			listaServer.add(s);
	}
	public void rimuoviServerDaRemoto(String s)throws RemoteException{
		synchronized(listaServer){
			for(int i=0;i<listaServer.size();i++){
				if(listaServer.get(i).getNomeServerRemoto().equals(s)){
					listaServer.remove(i);
				}
			}
		}
	}
	public Vector<Client> getListaClientDaRemoto() throws RemoteException {
			return listaClient;
	}
	public Vector<Client> getListaClient(){
			return listaClient;
	}
	public Vector<Server> getListaServer(){
		return listaServer;
	}
	public Vector<Server> getListaServerDaRemoto() throws RemoteException{
			return listaServer;
	}
	public boolean isConnessoA(Server s){
		synchronized (listaServer) {
			boolean stato=false;
			for(int i=0;i<listaServer.size();i++){
				try{
					if(listaServer.get(i).getNomeServerRemoto().equals(s.getNomeServerRemoto())){
						stato=true;
					}
				}
				catch (RemoteException e) {
					System.out.println("impossibile ricercare tale server!. rimozione del suo riferimento dalla lista");
					listaServer.remove(i);
				}
			}
			return stato;
		}
	}
	public Client getClientRemoto(String id) throws RemoteException {
		synchronized(listaClient){
			for (int i = 0; i < listaClient.size(); i++) {
				if(listaClient.get(i).getNomeClientRemoto().equals(id)){
					return listaClient.get(i);
				}
			}
			return null;
		}
	}
	@Override
	public void aggiornaListaServerGUIServerRemota(String[] listaServerRMI) throws RemoteException, MalformedURLException, NotBoundException{
		serverGui.aggiornaListaServer(listaServerRMI);
	}
	@Override
	public void aggiornaListaClientGUIServerRemota(boolean conn) throws RemoteException {
		serverGui.aggiornaListaClient(conn);
	}
}