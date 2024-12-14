package domain;

import javax.persistence.*;

@Entity
public class Eskaera {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int erreserbaZenbakia;
	@ManyToOne(fetch=FetchType.EAGER)
	private Ride bidaia;
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "traveler_name")
	private Traveler traveler;

	private int eserKop;
	private boolean onartuaDa;
	private float prezioTotala;

	public Eskaera() {}
	
	public Eskaera(int erreserbaZenbakia, Ride bidaia, int eserKop, Traveler traveler) {
		this.bidaia = bidaia;
		this.eserKop = eserKop;
		this.traveler = traveler;
		this.prezioTotala = bidaia.getPrice() * eserKop;
		this.onartuaDa = false;
		this.erreserbaZenbakia = erreserbaZenbakia;
	}

	public Ride getBidaia() {
		return bidaia;
	}

	public void setBidaia(Ride bidaia) {
		this.bidaia = bidaia;
	}

	public Traveler getTraveler() {
		return traveler;
	}

	public void setTraveler(Traveler traveler) {
		this.traveler = traveler;
	}

	public int getEserKop() {
		return eserKop;
	}

	public void setEserKop(int eserKop) {
		this.eserKop = eserKop;
	}

	public boolean isOnartuaDa() {
		return onartuaDa;
	}

	public void setOnartuaDa(boolean onartuaDa) {
		this.onartuaDa = onartuaDa;
	}

	public float getPrezioTotala() {
		return prezioTotala;
	}

	public void setPrezioTotala(float prezioTotala) {
		this.prezioTotala = prezioTotala;
	}

	public int getErreserbaZenbakia() {
		return erreserbaZenbakia;
	}

	public void setErreserbaZenbakia(int erreserbaZenbakia) {
		this.erreserbaZenbakia = erreserbaZenbakia;
	}

	@Override
	public String toString() {
		return traveler + " / " + bidaia.getDepartCity() + "-" + bidaia.getArrivalCity() + " / " + eserKop + " / " + bidaia.getPrice()
				+ "€";
	}
}
