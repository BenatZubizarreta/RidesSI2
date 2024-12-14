package domain;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

@Entity
public class Ride {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer rideNumber;

	private String departCity;
	private String arrivalCity;
	private int nPlaces;
	private Date dateRide;
	private float price;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "driver_name")
	private Driver driver;

	@OneToMany(targetEntity = Eskaera.class, mappedBy = "bidaia", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private Set<Eskaera> eskaerak = new HashSet<Eskaera>();

	public Set<Eskaera> getEskaerak() {
		return eskaerak;
	}

	public void setEskaerak(Set<Eskaera> eskaerak) {
		this.eskaerak = eskaerak;
	}

	public void setDepartCity(String departCity) {
		this.departCity = departCity;
	}

	public void setnPlaces(int nPlaces) {
		this.nPlaces = nPlaces;
	}

	public Ride() {
		super();
	}

	public Ride(Integer rideNumber, String departCity, String arrivalCity, Date dateRide, int nPlaces, float price, Driver driver) {
		super();
		this.rideNumber = rideNumber;
		this.departCity = departCity;
		this.arrivalCity = arrivalCity;
		this.nPlaces = nPlaces;
		this.dateRide = dateRide;
		this.price = price;
		this.driver = driver;
	}

	public Ride(String departCity, String arrivalCity, Date dateRide, int nPlaces, float price, Driver driver) {
		super();
		this.departCity = departCity;
		this.arrivalCity = arrivalCity;
		this.nPlaces = nPlaces;
		this.dateRide = dateRide;
		this.price = price;
		this.driver = driver;
	}

	public Integer getRideNumber() {
		return rideNumber;
	}

	public void setRideNumber(Integer rideNumber) {
		this.rideNumber = rideNumber;
	}

	public String getDepartCity() {
		return departCity;
	}

	public void setdepartCity(String origin) {
		this.departCity = origin;
	}

	public String getArrivalCity() {
		return arrivalCity;
	}

	public void setArrivalCity(String destination) {
		this.arrivalCity = destination;
	}

	public Date getDateRide() {
		return dateRide;
	}

	public void setDateRide(Date date) {
		this.dateRide = date;
	}

	public int getnPlaces() {
		return nPlaces;
	}

	public void setNplaces(int nPlaces) {
		this.nPlaces = nPlaces;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	public String bidaiaInprimatu() {
		return rideNumber + ";" + ";" + departCity + ";" + arrivalCity + ";" + dateRide;
	}

	@Override
	public String toString() {
		return rideNumber + ";" + ";" + departCity + ";" + arrivalCity + ";" + dateRide;
	}
}
