package client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

import risorsa.Risorsa;

public interface Client extends Remote{
	//tutti metodi in cui l'oggetto di invocazione è un remote reference
	public String getNomeClientRemoto() throws RemoteException;
	public String notificaConnessione(String s) throws RemoteException;
	public String notificaRegistrato(String p) throws RemoteException;
	public boolean hasRisorsa(String id) throws RemoteException;
	public String[] getInfoRisorsaRemota(String id) throws RemoteException;
	public Risorsa getRisorsaRemota(String id) throws RemoteException;
	public Vector<String> getListaRisorseRemota() throws RemoteException;
	public void aggiornaDatiRisorsaRemota(String nomeRis, String nomeClientCheScarica) throws RemoteException;
	
}