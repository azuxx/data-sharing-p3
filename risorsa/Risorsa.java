package risorsa;

import java.io.Serializable;
import java.util.Vector;

public class Risorsa implements Serializable{
	private String nome;
	private int dimensione;
	private String contenuto;
	private int contaDownload;
	private Vector<String> vettScaricataDa;
	public Risorsa(String n,int d,String c){
		nome=n;
		dimensione=d;
		contenuto=c;
		contaDownload=0;
		vettScaricataDa=new Vector<String>();
	}
	public Risorsa(Risorsa r){
		this.nome=r.nome;
		this.dimensione=r.dimensione;
		this.contenuto=r.contenuto;
		this.contaDownload=0;
		this.vettScaricataDa=new Vector<String>();
	}
	public String getNomeRisorsa() {
		return nome;
	}
	public int getDimensione(){
		return dimensione;
	}
	public int getContaDownload(){
		return contaDownload;
	}
	public String getContenuto(){
		return contenuto;
	}
	public String getScaricataDaiClient(){
		String s = "";
		for (int i = 0; i < vettScaricataDa.size(); i++) {
			s=s+vettScaricataDa.get(i)+"\n";
		}
		return s;
	}
	public void incrementaContaDownload(){
		contaDownload++;
	}
	public void scaricataDalClient(String s){
		vettScaricataDa.add(s);
	}
}