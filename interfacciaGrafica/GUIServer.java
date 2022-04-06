package interfacciaGrafica;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import client.Client;

import server.Server;
import server.ServerImpl;

public class GUIServer extends JFrame{
	/*variabili oggetti logici*/
	private ServerImpl server;
	/*variabili oggetti grafici*/
	private JTextField nome;
	private JTextPane pinfo;
	private JTextArea 	info,
						aiuto;
	private JLabel	labNome,
					labNomeServer,
					labListaS,
					labListaC;
	private JPanel	contentPane,
					pannelloAvvio,
					pannelloLista;
	private JTabbedPane pannelloTab;
	private JButton avvia;
	private JList listaClientConnessi,
				  listaServerConnessi;
	private JScrollPane scroll1,
						scroll2,
						scroll3;
	/*variabili oggetti di supporto alla grafica*/
	String[] vc;//vettore dei nomi dei client remoti
	Vector<String> vs=new Vector<String>();//vettore dei nomi dei server remoti
	/*costruttore*/
	public GUIServer() {
		super("Musharilla server");
		contentPane = (JPanel)this.getContentPane();//ottiene il contenitore di JFrame per poi aggiungere gli oggetti grafici
		pannelloAvvio=new JPanel();
		nome=new JTextField(10);
		labNome=new JLabel("Nome Server");
		avvia=new JButton("Avvia server");
		avvia.addActionListener(new AvviaServer());
		pannelloAvvio.add(labNome);
		pannelloAvvio.add(nome);
		pannelloAvvio.add(avvia);
		contentPane.setLayout(null);//non fisso un layout così ho la libertà di posizionare in modo assoluto gli oggetti
		setSize(640,480);
		AggiungiComponente.aggiungi(contentPane, pannelloAvvio, 165,340,350,50);
		setVisible(true);
		setDefaultCloseOperation(setWindowActions());//quando si clicca sul icona di uscita si rimanda la gestione dell'uscita al metodo setWindowActions()
	}
	private int setWindowActions() {
	    this.addWindowListener(new WindowAdapter() {//classe interna anonima
	        public void windowClosing(WindowEvent event) {//evento di chiusura
	        	if(server!=null){//se si è connessi ad un server
		        	int op=JOptionPane.showConfirmDialog(GUIServer.this, "Vuoi uscire chiudendo correttamente il server?", "Uscita", JOptionPane.YES_NO_OPTION,1);
		        	if(op==0){//opzione uscita corretta : unbind dal registro RMI e rimozione del suo riferimento dalle liste dei server remoti
			            try {
			            	server.chiudiServer();
						}catch (RemoteException e) {
							System.err.println("Errore remoto. Non è stato possibile rimuovere il server che si sta chiudendo dalla lista dei server remoti. Questi sono irraggiungibili!");
						} catch (MalformedURLException e) {
							System.err.println("Url di unbind errato");
						} catch (NotBoundException e) {
							System.err.println("Impossibile fare unbind");
						}
						finally{
							System.out.println("Server chiuso");
						}
		        	}
		        	else if(op==1){//opzione chiusura scorretta
		        		System.out.println("Chiusura non corretta del server. Potrebbero verificarsi problemi negli altri server e/o client a cui era connesso.");
		        	}
	        	}
	        }
	    });
	    return JFrame.EXIT_ON_CLOSE;// in entrambe le opzioni, si chiude la finestra GUIServer
	}
	private class AvviaServer implements ActionListener{
		public void actionPerformed(ActionEvent e) {//evento azionato quando si clicca il bottone avvia
			if(nome.getText().isEmpty() || controlloEsisteNomeUguale(nome.getText())==true){//controllo parametri
				nome.setText("nome?");
				JOptionPane.showMessageDialog(GUIServer.this, "Non è stato inserito un nome od era uguale a server già registrati.", "Nome?",JOptionPane.WARNING_MESSAGE);
			}
			else{
				try{
					server= new ServerImpl(nome.getText(),GUIServer.this);//creazione oggetto logico che rappresenta il server
					System.out.println("avviato "+server.getNomeServer());
					visualizzaConnessioni();//metodo che inizializza i vari oggetti grafici e li visualizza nascondendo il menu d'avvio
					info.append(server.aggiorna());
				}catch(RemoteException ecc1){
					JOptionPane.showMessageDialog(GUIServer.this,"Impossibile avviare il server o avvio non corretto.","Avvio non riuscito!",JOptionPane.ERROR_MESSAGE);
				}catch (MalformedURLException ecc2) {
					JOptionPane.showMessageDialog(GUIServer.this,"rebind errato","Avvio non riuscito!",JOptionPane.ERROR_MESSAGE);
				} catch (NotBoundException ecc3) {
					JOptionPane.showMessageDialog(GUIServer.this,"lookup errato","Avvio non riuscito!",JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	private boolean controlloEsisteNomeUguale(String nomeDaControllare){
		boolean uguale=false;
		String[] listaServerRMI;
		try {
			listaServerRMI = Naming.list(ServerImpl.getHOST()+"/Servers/");
			for(int i=0;i<listaServerRMI.length;i++){
					String[] s = listaServerRMI[i].split("//:1099/Servers/");
					for(int j=0;j<s.length && !uguale;j++){
						if(s[j].equals(nomeDaControllare)){
							uguale=true;
						}
					}
					if(uguale)
						return true;
			}
		}catch (RemoteException e) {
			System.err.println("Errore remoto su listaServerRMI");
		} catch (MalformedURLException e) {
			System.err.println("errore url listaServerRMI");
		}
		return false;
	}
	private void visualizzaConnessioni(){
		pannelloAvvio.setVisible(false);
		contentPane = (JPanel)this.getContentPane();
		labNomeServer=new JLabel(server.getNomeServer()+" avviato!");
		labNomeServer.setFont(new Font("SansSerif-100",100 , 16));
		labListaS=new JLabel("Lista server");
		labListaC=new JLabel("Lista client");
		aiuto=new JTextArea("Per visualizzare le risorse\n di un client, clicca su di \n esso.");
		aiuto.setEditable(false);
		aiuto.setBackground(null);
		pannelloLista=new JPanel();
		pannelloTab= new JTabbedPane();
		listaClientConnessi=new JList();
		listaClientConnessi.addListSelectionListener(new VisualizzaInfoClient());
		listaServerConnessi=new JList();
		info=new JTextArea();
		pinfo=new JTextPane();
		pinfo.add(info);
		scroll1=new JScrollPane();
		scroll2=new JScrollPane();
		scroll3=new JScrollPane();
		scroll1.setViewportView(listaClientConnessi);
		scroll3.setViewportView(listaServerConnessi);
		scroll2.setViewportView(info);
		pannelloTab.addTab("Lista client e server connessi", pannelloLista);
		pannelloLista.setLayout(null);
		AggiungiComponente.aggiungi(pannelloLista,labListaC,130,25,150,10);
		AggiungiComponente.aggiungi(pannelloLista,scroll1,80,50,200,150);
		AggiungiComponente.aggiungi(pannelloLista,labListaS,380,25,150,10);
		AggiungiComponente.aggiungi(pannelloLista,scroll3,320,50,200,150);
		AggiungiComponente.aggiungi(pannelloLista,aiuto,100,250,160,50);
		AggiungiComponente.aggiungi(pannelloLista,scroll2,320,220,200,100);
		AggiungiComponente.aggiungi(contentPane,labNomeServer,20,20,250,17);
		AggiungiComponente.aggiungi(contentPane,pannelloTab,25,50,580,380);
		repaint();
	}
	public void aggiornaListaServer(String[] listaServerRMI) throws MalformedURLException, RemoteException, NotBoundException{
		vs.clear();
		for(int i=0;i<listaServerRMI.length;i++){//scorro la lista server rmi per aggiornare con il nuovo server che si è registrato
			boolean uguale=false;
			String[] s = listaServerRMI[i].split("//:1099/Servers/");
			for(int j=0;j<s.length && !uguale;j++){
				if(s[j].equals(server.getNomeServer())){
					uguale=true;
				}
			}
			if(!uguale){
				Server ref=(Server)Naming.lookup("rmi:"+listaServerRMI[i]);//riferimento agli altri server remoti, per potere aggiungere il loro nome sulla lista grafica dell-i-esimo server remoto
				if(server.isConnessoA(ref)==false){//controllo se c'è già il suo riferimento nell'i-esimo server remoto
					server.connettiAServer(ref);
				}
				vs.add(ref.getNomeServerRemoto());
			}
			
		}
		if(listaServerRMI.length>1){
			info.append("Si sono registrati server \n");
		}
		listaServerConnessi.setListData(vs);
	}
	public void aggiornaListaClient(boolean conn) throws RemoteException{//rimanda al chiamante la gestione dell'eventuale eccezione (client remoto non raggiung.)
		int numeroClientConnessi=server.getListaClient().size();
		vc=new String[numeroClientConnessi];
		if(numeroClientConnessi==0){
			info.append("Nessun client connesso!\n");
		}
		else{
			for(int i=0;i<numeroClientConnessi;i++){
				vc[i]=server.getListaClient().get(i).getNomeClientRemoto();
			}
			if(conn){
				Client c=server.getListaClient().get(numeroClientConnessi-1);
				info.append(c.notificaConnessione(c.getNomeClientRemoto()+ " connesso \n"));//notifica remota del client che si è connesso
			}
		}
		listaClientConnessi.setListData(vc);
	}
	private class VisualizzaInfoClient implements ListSelectionListener{
		public void valueChanged(ListSelectionEvent e) {//se clicco sul nome del client ottengo i nomi delle risorse che possiede
			try{
				if (e.getValueIsAdjusting() == false) {//gestione del fatto dell'aggiornamento del valore (senza questo controllo l'evento sarebbe invocato due volte, sia con adjusting true sia con false)
			        if (listaClientConnessi.getSelectedIndex() != -1) {//controllo se è possibile selezionare un client
						Client c=server.getClientRemoto(listaClientConnessi.getSelectedValue().toString());
						info.append("Lista risorse del client "+c.getNomeClientRemoto()+": \n");
						int size=c.getListaRisorseRemota().size();
						for (int j = 0; j < size; j++) {
							info.append(c.getListaRisorseRemota().get(j)+"\n");
						}
						if(server.getListaClient().size()==1){//in questo caso alla fine rimuovo la selezione per poi poter riselezionare l'unico elemento della lista
							listaClientConnessi.clearSelection();
						}
			        }
					
				}
			}
			catch (RemoteException ecc1) {
				JOptionPane.showMessageDialog(GUIServer.this,"client irraggiungibile!");
			}
			
		}
	}
}