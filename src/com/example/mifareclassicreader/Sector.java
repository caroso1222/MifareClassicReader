package com.example.mifareclassicreader;

public class Sector {

	private boolean autenticado;
	private int numero;
	private String bloque0;
	private String bloque1;
	private String bloque2;
	private String bloque3;
	
	public boolean isAutenticado() {
		return autenticado;
	}
	public void setAutenticado(boolean autenticado) {
		this.autenticado = autenticado;
	}
	public int getNumero() {
		return numero;
	}
	public void setNumero(int numero) {
		this.numero = numero;
	}
	public String getBloque0() {
		return bloque0;
	}
	public void setBloque0(String bloque0) {
		this.bloque0 = bloque0;
	}
	public String getBloque1() {
		return bloque1;
	}
	public void setBloque1(String bloque1) {
		this.bloque1 = bloque1;
	}
	public String getBloque2() {
		return bloque2;
	}
	public void setBloque2(String bloque2) {
		this.bloque2 = bloque2;
	}
	public String getBloque3() {
		return bloque3;
	}
	public void setBloque3(String bloque3) {
		this.bloque3 = bloque3;
	}
	
	public void setBloqueValue(int numBloque,String val){
		if(numBloque==0){
			setBloque0(val);
		}else if(numBloque==1){
			setBloque1(val);
		}else if(numBloque==2){
			setBloque2(val);
		}else if(numBloque==3){
			setBloque3(val);
		}
	}
	
	
}
