package server;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

import client.Client;

public interface Server extends Remote{
	//tutti metodi in cui l'oggetto di invocazione è un remote reference
	public String getNomeServerRemoto() throws RemoteException;
	public Vector<Client> ricerca(String id, String nomeClientInvocRic) throws RemoteException;
	public void aggiornaListaClientGUIServerRemota(boolean conn) throws RemoteException;
	public void aggiornaListaServerGUIServerRemota(String[] listaServerRMI) throws RemoteException, MalformedURLException, NotBoundException;
	public Client getClientRemoto(String id) throws RemoteException;
	public void registraClient(Client c) throws RemoteException;
	public void rimuoviClient(Client c) throws RemoteException;
	public String notificaRegistrazione(String n, String s) throws RemoteException;
	public String notificaConnesso(String p) throws RemoteException;
	public void rimuoviServerDaRemoto(String s)throws RemoteException;
	public Vector<Client> getListaClientDaRemoto() throws RemoteException;
	public Vector<Server> getListaServerDaRemoto() throws RemoteException;
}