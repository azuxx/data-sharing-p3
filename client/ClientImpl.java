package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;
import risorsa.Risorsa;
import server.Server;


public class ClientImpl extends UnicastRemoteObject implements Client{//estende unicastremoteobject perchè devono essere invocati i metodi remoti dell'interfaccia implementati
	private String nomeClient;
	private Vector<Risorsa> listaRisorse=new Vector<Risorsa>();//le risorse che possiede il client
	private Server server;//il server a cui si connette il client( 1 client si connette ad 1 server)
	public ClientImpl(String n) throws RemoteException {
		super();
		nomeClient=n;
	}
	public String getNomeClient(){
		return nomeClient;
	}
	public String getNomeClientRemoto() throws RemoteException{
		return nomeClient;
	}
	public Server getServerConnesso(){
		return server;
	}
	public Risorsa getRisorsaLocale(String id){
		for (int i = 0; i < listaRisorse.size(); i++) {
			if(listaRisorse.get(i).getNomeRisorsa().equals(id)==true){
				return listaRisorse.get(i);
			}
		}
		return null;
	}
	public Risorsa getRisorsaRemota(String id) throws RemoteException{
			for (int i = 0; i < listaRisorse.size(); i++) {
				if(listaRisorse.get(i).getNomeRisorsa().equals(id)==true){
					return listaRisorse.get(i);
				}
			}
			return null;
	}
	public boolean hasRisorsa(String id) throws RemoteException{
			for (int i = 0; i < listaRisorse.size(); i++) {
				if(listaRisorse.get(i).getNomeRisorsa().equals(id)==true){
					return true;
				}
			}
			return false;
	}
	public void aggiungiRisorsaInLocale(String r, Integer dim,String c){
			listaRisorse.add(new Risorsa(r,dim.intValue(),c));
	}
	public void aggiungiRisorsaScaricata(Risorsa r){
			listaRisorse.add(new Risorsa(r));
	}
	public String notificaRegistrato(String p) throws RemoteException {
		  return "Notifica del server: " + p+"\n";
		
	}
	public String notificaConnessione(String s) throws RemoteException {
		return server.notificaConnesso(s);
	}
	public String connetti(Object obj) throws RemoteException, MalformedURLException, NotBoundException{
		server = (Server)Naming.lookup("rmi:"+obj.toString());
	    server.registraClient(this);
	    String s=server.notificaRegistrazione(nomeClient,"connesso a"+obj.toString());
	    server.aggiornaListaClientGUIServerRemota(true);
	    return s;
	}
	public void disconnetti() throws RemoteException{
		if(server!=null){
			server.rimuoviClient(this);
			server.aggiornaListaClientGUIServerRemota(false);
			server=null;//tolgo riferimento
			System.out.println("client disconnesso automaticamente dal server a cui era connesso");
		}
	}
	public Vector<String> getListaRisorseRemota() throws RemoteException {
			Vector<String> vs=new Vector<String>();
			for(int i=0;i<listaRisorse.size();i++){
				vs.add(listaRisorse.get(i).getNomeRisorsa());
			}
			return vs;
	}
	public Vector<Risorsa> getListaRisorseLocale(){
		return listaRisorse;
	}
	@Override
	public String[] getInfoRisorsaRemota(String id) throws RemoteException {
			for(int i=0;i<listaRisorse.size();i++){
				if(listaRisorse.get(i).getNomeRisorsa().equals(id)){
					return new String[]{listaRisorse.get(i).getNomeRisorsa(),nomeClient,new Integer(listaRisorse.get(i).getDimensione()).toString()};
				}
			}
			return null;
	}
	public Vector<Client> cercaRisorsa(String id) throws RemoteException{
		return server.ricerca(id,this.nomeClient);
	}
	/*scarica la risorsa selezionata del client remoto selezionato. è passato il vector di riferimenti ai client remoti che possiedono la risorsa che si vuole scaricare*/
	public boolean scaricaRisorsa(Vector<Client> possessoriRemRis, String nomeRisSelez, String nomeCliRemSelez) throws RemoteException{
		boolean erroreRem=false;//per il controllo quando avviene un errore remoto, dovuto al fatto che il client remoto che possiede la risorsa non è più raggiungibile
		boolean[] vClientDisconnessi=new boolean[possessoriRemRis.size()];
		for(int i=0;i<vClientDisconnessi.length;i++){//inizializzo a false perchè non ho avuto ancora errori remoti
			vClientDisconnessi[i]=false;
		}
		int i=0;
		while(i<possessoriRemRis.size()){
			try{
				if(erroreRem){
					//in questo if si entra quando avviene almeno una disconnessione di un client. in questo caso si scarica dagli altri eventuali client non disconnessi che la possiedono
					if(vClientDisconnessi[i]==false){
						aggiungiRisorsaScaricata(possessoriRemRis.get(i).getRisorsaRemota(nomeRisSelez));
						possessoriRemRis.get(i).aggiornaDatiRisorsaRemota(nomeRisSelez, nomeClient);
						return true;
					}
				}
				else{
					/*getRisorsaRemota(s) puo' anche sollevare una remote exception. Succede quando il client remoto si disconnette prima che il metodo ritorni la copia serializzata della risorsa del client remoto*/
					if(possessoriRemRis.get(i).getNomeClientRemoto().equals(nomeCliRemSelez)){
						aggiungiRisorsaScaricata(possessoriRemRis.get(i).getRisorsaRemota(nomeRisSelez));
						possessoriRemRis.get(i).aggiornaDatiRisorsaRemota(nomeRisSelez, nomeClient);
						return true;
					}
				}
				i++;
			}
			catch (RemoteException ecc1) {
				System.out.println("Impossibile Scaricare! E' possibile che il client si sia disconnesso. Scaricamento da altri client che la possiedono..." );
				vClientDisconnessi[i]=true;
				i=0;
				erroreRem=true;
				
			}
			//a questo punto o è stato incrementato i o è stato rinizializzato a 0 a causa della disconnessione del client i-esimo
		}
		return false;
	}
	public void aggiornaDatiRisorsaRemota(String nomeRis, String nomeClientCheScarica) throws RemoteException {
			for (int i = 0; i < listaRisorse.size(); i++) {
				if(listaRisorse.get(i).getNomeRisorsa().equals(nomeRis)==true){
					listaRisorse.get(i).incrementaContaDownload();
					listaRisorse.get(i).scaricataDalClient(nomeClientCheScarica);
				}
			}
	}
}
