package interfacciaGrafica;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import server.Server;

import client.Client;
import client.ClientImpl;


public class GUIMusharilla extends JFrame{
	private static final String HOST="localhost";
	//oggetti grafici
	private JPanel	pannello,
					pannelloConnDisc,
					pannelloDownR,
					pannelloIncomR,
					contentPane;
	private JButton start,
					connetti,
					disconnetti,
					cerca,
					scarica,
					pulisci,
					aggiungi,
					dettagli;
	private JTextField casNome,
						casCerca;
	private JTextArea	info,
						statistiche;
	private JTextPane	pinfo,
						pstatistiche;
	private JLabel	labClient,
					labBenvenuto,
					labNomeClient,
					labInfo,
					labStat;
	private JTabbedPane pannelloTab;
	private JScrollPane scroll1,
						scroll2,
						scroll3,
						scroll4,
						scroll5;
	private JList	lista,
					risorse;
	private JProgressBar barraScaricamento;
	private JTable tabRisultati;
	private DefaultTableModel model;
	private ListSelectionModel listSelectionModel;
	//oggetti di supporto per la grafica
	private Vector<String> vs=new Vector<String>();
	private Vector<String> vr=new Vector<String>();
	Vector<Client> risultatiClient=new Vector<Client>();
	//oggetti logici per la GUI del client
	private ClientImpl client;
	private VisualizzaAggiornaRisorse var;
	//costruttore
	public GUIMusharilla(){
		super("Musharilla");
		contentPane = (JPanel)this.getContentPane();
		pannello=new JPanel();
		start=new JButton("Start");
		casNome=new JTextField(10);
		labClient=new JLabel("Nome client");
		labBenvenuto=new JLabel("Benvenuto in Musharilla!");
		labBenvenuto.setFont(new Font("SansSerif-100",50 , 30));
		start.addActionListener(new AvviaGestore());
		pannello.add(labClient);
		pannello.add(casNome);
		pannello.add(start);
		contentPane.setLayout(null);
		setSize(640, 480);
		AggiungiComponente.aggiungi(contentPane, labBenvenuto, (getWidth()-350)/2,(getHeight()-50)/2,400,50);
		AggiungiComponente.aggiungi(contentPane, pannello, 165,340,300,50);
		setVisible(true);
		setDefaultCloseOperation(setWindowActions());//importante, altrimenti rimane il processo!
	}
	private int setWindowActions() {
		    this.addWindowListener(new WindowAdapter() {
		        public void windowClosing(WindowEvent event) {
		        	if(client!=null){
			            try {
			            	if(disconnetti.isEnabled()){
			            		client.disconnetti();
			            	}
						} catch (RemoteException ecc1) {
							System.err.println("è stato impossibile disconnettersi automaticamente dal server a cui si era connessi!");
						}
		        	}
		        }
		    });
	    return JFrame.EXIT_ON_CLOSE;
	}
	private void inizializzaGestore(){
		if(casNome.getText().isEmpty()){
			JOptionPane.showMessageDialog(GUIMusharilla.this, "Inserire nome del client", "Nome?",JOptionPane.WARNING_MESSAGE);
		}
		else{
			pannello.setVisible(false);
			labBenvenuto.setVisible(false);
			contentPane = (JPanel)this.getContentPane();
			pannelloTab= new JTabbedPane();
			try {
				//System.setSecurityManager(new RMISecurityManager());
				client = new ClientImpl(casNome.getText());
			}
			catch (RemoteException ecc1) {
				info.append("Impossibile avviare il client. Registro RMI non presente!\n");
			}
			costruisciTabConnettiDisconnetti();
			costruisciTabCercaScaricaR();
			costruisciTabIncomingR();
			AggiungiComponente.aggiungi(contentPane,pannelloTab,10,60,600,350);
			AggiungiComponente.aggiungi(contentPane,labNomeClient,20,10,150,30);
		}
	}
	private void costruisciTabConnettiDisconnetti(){
		try {
			String[] s = Naming.list(HOST+"/Servers/");
			for(int i=0;i<s.length;i++){
				vs.add(s[i]);
			}
		} catch (RemoteException ecc1) {
			info.append("Impossibile creare la lista server. Registro RMI non presente\n");
		} catch (MalformedURLException ecc2) {
			info.append("URL di list non corretta\n");
		}
		pannelloConnDisc=new JPanel();
		labNomeClient=new JLabel();
		labInfo=new JLabel("Informazioni generali");
		lista=new JList();
		lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		info=new JTextArea();
		pinfo=new JTextPane();
		pinfo.setEditable(false);
		pinfo.add(info);
		scroll1=new JScrollPane();
		scroll2=new JScrollPane();
		connetti=new JButton("Connetti");
		disconnetti=new JButton("Disconnetti");
		disconnetti.addActionListener(new ChiudiConnessione());
		connetti.addActionListener(new AvviaConnessione());
		lista.addListSelectionListener(new SelezioneServer());
		scroll1.setViewportView(lista);
		scroll2.setViewportView(info);
		labNomeClient.setText("Benvenuto "+client.getNomeClient()+"!");
		labNomeClient.setFont(new Font("SansSerif-100",20 , 15));
		lista.setListData(vs);
		pannelloTab.addTab("Connetti/Disconnetti", pannelloConnDisc);
		pannelloConnDisc.setLayout(null);
		AggiungiComponente.aggiungi(pannelloConnDisc, scroll1, 40,20,320,250);
		AggiungiComponente.aggiungi(pannelloConnDisc, scroll2, 410,140,160,130);
		AggiungiComponente.aggiungi(pannelloConnDisc, labInfo, 425,115,170,20);
		AggiungiComponente.aggiungi(pannelloConnDisc, connetti, 420,50,140,35);
		AggiungiComponente.aggiungi(pannelloConnDisc, disconnetti, 420,50,140,35);
		connetti.setEnabled(false);
		disconnetti.setVisible(false);
	}
	private void costruisciTabCercaScaricaR() {
		pannelloDownR=new JPanel();
		scroll3=new JScrollPane();
		cerca=new JButton("Cerca risorsa");
		scarica=new JButton("Scarica");
		pulisci=new JButton("Pulisci ricerca");
		casCerca=new JTextField(10);
		model=new DefaultTableModel();
		tabRisultati=new JTable(model);
		model.addColumn("Nome risorsa");
		model.addColumn("Client remoto");
		model.addColumn("Dimensione");
		listSelectionModel = tabRisultati.getSelectionModel();
        listSelectionModel.addListSelectionListener(new SelezioneScaricamento());
    	tabRisultati.setSelectionModel(listSelectionModel);
		scroll3.setViewportView(tabRisultati);
		cerca.addActionListener(new AvviaRicerca());
		scarica.addActionListener(new AvviaScaricamento());
		pulisci.addActionListener(new AvviaPulizia());
		cerca.setEnabled(false);
		scarica.setEnabled(false);
		pulisci.setEnabled(false);
		pannelloTab.addTab("Cerca/Scarica risorse",pannelloDownR);
		pannelloDownR.setLayout(null);
		AggiungiComponente.aggiungi(pannelloDownR, scroll3, 40,20,320,250);
		AggiungiComponente.aggiungi(pannelloDownR, casCerca, 420,50,140,35);
		AggiungiComponente.aggiungi(pannelloDownR, cerca, 420,100,140,35);
		AggiungiComponente.aggiungi(pannelloDownR, scarica, 420,150,140,35);
		AggiungiComponente.aggiungi(pannelloDownR, pulisci, 420,200,140,35);
	}
	private void costruisciTabIncomingR() {
		pannelloIncomR=new JPanel();
		aggiungi=new JButton("Aggiungi");
		aggiungi.addActionListener(new AggiungiRisorsa());
		dettagli=new JButton("Dettagli risorsa");
		dettagli.addActionListener(new VisualizzaDettagliRisorsa());
        dettagli.setEnabled(false);
		labStat=new JLabel("Statistiche risorsa");
		statistiche=new JTextArea();
		pstatistiche=new JTextPane();
		pstatistiche.add(statistiche);
		scroll4=new JScrollPane();
		scroll5=new JScrollPane();
		risorse=new JList(vr);
		risorse.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		risorse.addListSelectionListener(new SelezioneRisorsa());
		scroll4.setViewportView(risorse);
		scroll5.setViewportView(statistiche);
		pannelloTab.addTab("Incoming risorse", pannelloIncomR);
		pannelloIncomR.setLayout(null);
		AggiungiComponente.aggiungi(pannelloIncomR, scroll4, 40,20,275,270);
		AggiungiComponente.aggiungi(pannelloIncomR, aggiungi, 410,20,145,35);
		AggiungiComponente.aggiungi(pannelloIncomR, dettagli, 410,65,145,35);
		AggiungiComponente.aggiungi(pannelloIncomR, labStat, 415,155,140,20);
		AggiungiComponente.aggiungi(pannelloIncomR, scroll5, 400,175,170,110);
		var=new VisualizzaAggiornaRisorse();
		var.start();
	}
	private class AvviaGestore implements ActionListener{
		public void actionPerformed(ActionEvent e) {
				inizializzaGestore();
				repaint();//fa un bel refresh	
		}
	}	
	private class SelezioneServer implements ListSelectionListener{
		public void valueChanged(ListSelectionEvent e) {
			connetti.setEnabled(true);
		}
	}
	private class AvviaConnessione implements ActionListener{
		public void actionPerformed(ActionEvent e){
			//Avvio connessione del client al server
			try{
				info.append("connessione al server...\n");
			    info.append(client.connetti(lista.getSelectedValue()));
				connetti.setVisible(false);
				lista.setEnabled(false);
				disconnetti.setVisible(true);
				cerca.setEnabled(true);
			}
			catch (RemoteException ecc1) {
				info.append("Errore di connessione al server!\n");
			}
			catch (MalformedURLException ecc2) {
				info.append("URL di lookup non corretta\n");
			}
			catch (NotBoundException ecc3) {
				info.append("Non e' possibile fare lookup\n");
			}
		}
	}
	private class ChiudiConnessione implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			lista.setEnabled(true);
			disconnetti.setVisible(false);
			cerca.setEnabled(false);
			scarica.setEnabled(false);
			pulisci.setEnabled(false);
			connetti.setVisible(true);
			try {
				client.disconnetti();
				info.append("disconnesso dal server!\n");
				
			} catch (RemoteException ecc1) {
				info.append("non e' possibile disconnettersi dal server!. Connessione persa. \n");
			}
		}
	}
	private class AvviaRicerca implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			pulisci.setEnabled(true);
			cerca.setEnabled(false);
			//pulitura tabella
			if(!risultatiClient.isEmpty()){
				risultatiClient.clear();
				for(int i=0;i< model.getRowCount();i++){
					model.removeRow(i);
				}
			}
			//vector di array di string per i dati risultanti dalla ricerca
			Vector<String[]> risultato=new Vector<String[]>();
			try {
					risultatiClient=client.cercaRisorsa(casCerca.getText());
					if(risultatiClient.isEmpty()){
						cerca.setEnabled(true);
						JOptionPane.showMessageDialog(GUIMusharilla.this, "Nessun client remoto possiede la risorsa cercata!", "Ricerca completata",JOptionPane.INFORMATION_MESSAGE);
					}
					else{
						for(int i=0;i<risultatiClient.size();i++){
							risultato.add(risultatiClient.get(i).getInfoRisorsaRemota(casCerca.getText()));
						}
						for(int i=0;i<risultato.size();i++){
							model.addRow(risultato.get(i));
						}
						scarica.setEnabled(false);
					}
			} 
			catch (RemoteException ecc1) {
				JOptionPane.showMessageDialog(GUIMusharilla.this, "E' caduto il server a cui si era connessi o si e' disconnesso un client remoto","Ricerca fallita!",JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	private class SelezioneScaricamento implements ListSelectionListener{
		public void valueChanged(ListSelectionEvent e) {
			scarica.setEnabled(true);
			
		}
	}
	private class AvviaScaricamento implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			new Thread(){
				public void run(){
					pulisci.setEnabled(false);
					scarica.setEnabled(false);
					int rigaSelez=tabRisultati.getSelectedRow();
					String nomeRisSelez=(String) tabRisultati.getValueAt(rigaSelez, 0);
					String nomeClient= (String) tabRisultati.getValueAt(rigaSelez, 1);
					int dimensione=new Integer((String) tabRisultati.getValueAt(rigaSelez, 2)).intValue();
					boolean presente=false;
					for(int i=0;i<client.getListaRisorseLocale().size();i++){
						if(client.getListaRisorseLocale().get(i).getNomeRisorsa().equals(nomeRisSelez)){
							presente=true;
						}
					}
					if(!presente){
						barraScaricamento = new JProgressBar(0, dimensione);
						barraScaricamento.setValue(0);
						barraScaricamento.setStringPainted(true);
						AggiungiComponente.aggiungi(pannelloDownR, barraScaricamento, 420,250,145,35);
						int n=0;//contatore espresso in secondi
						while (n < dimensione) {
					            try {
					                Thread.sleep(1000);//sleep espresso in millisecondi
					            } catch (InterruptedException ecc1) { }
					            n =n+1 ;
					            barraScaricamento.setValue(n);
					    }
						try{
							boolean scaricata=client.scaricaRisorsa(risultatiClient,nomeRisSelez,nomeClient);
							if(scaricata){
								JOptionPane.showMessageDialog(GUIMusharilla.this,"Risorsa "+nomeRisSelez+" scaricata!","Scaricamento completato!",JOptionPane.INFORMATION_MESSAGE);
								cerca.setEnabled(true);
								synchronized (var) {
									var.sospendi=false;
									var.notify();
								}
							}
							else{
								JOptionPane.showMessageDialog(GUIMusharilla.this,"Tutti i client remoti che possedevano la risorsa si sono disconnessi","Scaricamento fallito!",JOptionPane.WARNING_MESSAGE);
							}
						}
						catch (RemoteException ecc1) {
							JOptionPane.showMessageDialog(GUIMusharilla.this,"E' caduto il server a cui si era connessi.","Scaricamento fallito!",JOptionPane.ERROR_MESSAGE);
						}
					}
					else{
						JOptionPane.showMessageDialog(GUIMusharilla.this,"Possiedi già questa risorsa!","Scaricamento non avviato!",JOptionPane.INFORMATION_MESSAGE);
					}
					scarica.setEnabled(true);
					pulisci.setEnabled(true);
				}
			}.start();
			
		}
	}
	private class AvviaPulizia implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			risultatiClient.clear();
			for(int i=0;i< model.getRowCount();i++){
				model.removeRow(i);
			}
			casCerca.setText("");
			if(barraScaricamento!=null){
				barraScaricamento.setValue(0);
			}
			scarica.setEnabled(false);
			pulisci.setEnabled(false);
			cerca.setEnabled(true);
		}
	}
	private class AggiungiRisorsa implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			Object ob1=JOptionPane.showInputDialog(GUIMusharilla.this, "Inserisci nome risorsa");
			Object ob2;
			int dim=0;
			boolean ok=false;
			while(!ok){
				try{
					ob2=JOptionPane.showInputDialog(GUIMusharilla.this, "Inserisci dimensione risorsa");
					Integer i=Integer.parseInt(ob2.toString());
					dim=i.intValue();
					ok=true;
				}
				catch (Exception ecc1) {
					JOptionPane.showMessageDialog(GUIMusharilla.this,"Inserire un valore numerico!","Errore di input",JOptionPane.ERROR_MESSAGE);
				}
			}
			Object ob3=JOptionPane.showInputDialog(GUIMusharilla.this, "Inserisci contenuto risorsa");
			client.aggiungiRisorsaInLocale(ob1.toString(),dim,ob3.toString());
			JOptionPane.showMessageDialog(GUIMusharilla.this,"Aggiunta risorsa "+client.getRisorsaLocale(ob1.toString()).getNomeRisorsa());
			synchronized (var) {
				var.sospendi=false;
				var.notify();
			}
			
		}
	}
	private class VisualizzaDettagliRisorsa implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			String s=risorse.getSelectedValue().toString();
			statistiche.append("\nDettagli risorsa "+s+":\n");
			statistiche.append("Dimensione: "+client.getRisorsaLocale(s).getDimensione()+"\n");
			statistiche.append("Contenuto: "+client.getRisorsaLocale(s).getContenuto()+"\n");
			statistiche.append("Scaricata: "+client.getRisorsaLocale(s).getContaDownload()+" volte \n");
			statistiche.append("Scaricata: "+client.getRisorsaLocale(s).getScaricataDaiClient());
		}
	}
	private class VisualizzaAggiornaRisorse extends Thread{
		boolean sospendi=false;
		int numeroRis=0;
		public void run() {
			try{
				while(true){
					synchronized(this){
						while(sospendi==true){
							wait();
						}
						if(!client.getListaRisorseLocale().isEmpty()){
							if(client.getListaRisorseLocale().size()>numeroRis){
								vr.add(client.getListaRisorseLocale().lastElement().getNomeRisorsa());
							}
						}
						else{
							vr.clear();
						}
						risorse.setListData(vr);
						numeroRis=client.getListaRisorseLocale().size();
						sospendi=true;
					}
				}
			}
			catch (InterruptedException ecc1) {
				System.err.println("thread di aggiormento lista risorse interrotto");
			}
		}
	}
	private class SelezioneRisorsa implements ListSelectionListener{
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() == false) {
		        if (risorse.getSelectedIndex() == -1) {
		            dettagli.setEnabled(false);
		        } else {
		            dettagli.setEnabled(true);
		        }
		    }
		}
	}
}