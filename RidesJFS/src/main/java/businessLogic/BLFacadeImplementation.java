package businessLogic;

import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import configuration.ConfigXML;
import dataAccess.DataAccess;
import dataAccess.HibernateDataAccess;
import domain.Ride;
import domain.Traveler;
import domain.User;
import domain.Driver;
import domain.Eskaera;
import exceptions.RideMustBeLaterThanTodayException;
import exceptions.RideAlreadyExistException;

/**
 * It implements the business logic as a web service.
 */
public class BLFacadeImplementation implements BLFacade {
	HibernateDataAccess dbManager;

	public BLFacadeImplementation() {
		System.out.println("Creating BLFacadeImplementation instance");

		dbManager = new HibernateDataAccess();

		// dbManager.close();

	}

	public BLFacadeImplementation(HibernateDataAccess da) {

		System.out.println("Creating BLFacadeImplementation instance with DataAccess parameter");
		ConfigXML c = ConfigXML.getInstance();

		dbManager = da;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getDepartCities() {

		List<String> departLocations = dbManager.getDepartCities();

		return departLocations;

	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getDestinationCities(String from) {

		List<String> targetCities = dbManager.getArrivalCities(from);

		return targetCities;
	}

	/**
	 * {@inheritDoc}
	 */
	public Ride createRide(String from, String to, Date date, int nPlaces, float price, String driverEmail)
			throws RideMustBeLaterThanTodayException, RideAlreadyExistException {

		Ride ride = dbManager.createRide(from, to, date, nPlaces, price, driverEmail);
		return ride;
	};

	/**
	 * {@inheritDoc}
	 */
	public List<Ride> getRides(String from, String to, Date date) {
		List<Ride> rides = dbManager.getRides(from, to, date);
		return rides;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Date> getThisMonthDatesWithRides(String from, String to, Date date) {
		List<Date> dates = dbManager.getThisMonthDatesWithRides(from, to, date);
		return dates;
	}

	public void close() {
		DataAccess dB4oManager = new DataAccess();

		dB4oManager.close();

	}

	/**
	 * {@inheritDoc}
	 */
	public void initializeBD() {
		dbManager.initializeDB();
	}

	public User Erregistratu(String izena, String pasahitza1, String mota) {
		return dbManager.Erregistratu(izena, pasahitza1, mota);
	}

	public User Login(String izena, String pasahitza) {
		return dbManager.Login(izena, pasahitza);
	}

	public void SartuDirua(User u, float zenbat) {
		dbManager.SartuDirua(u, zenbat);
	}

	public Eskaera EskaeraEgin(Traveler u, int eserKop, Ride bidaia) {
		return dbManager.EskaeraEgin(u, eserKop, bidaia);
	}

	public List<Eskaera> EskaerakBegiratu(Ride bidaia) {
		return dbManager.EskaerakBegiratu(bidaia);
	}

	public List<Ride> BidaiakBegiratu(Driver dr) {
		return dbManager.BidaiakBegiratu(dr);
	}

	public void EskaeraOnartuBaztertu(Eskaera e, String zer) {
		dbManager.EskaeraOnartuBaztertu(e, zer);
	}

	public void EskaeraEzabatu(Eskaera e) {
		dbManager.EskaeraEzabatu(e);
	}



	public User getUser(String name) {
		return dbManager.getUser(name);
	}

	public Ride getRideById(int id) {
		return dbManager.getRideById(id);
	}
	
	public Eskaera getEskaeraById(int id) {
		return dbManager.getEskaeraById(id);
	}
	
	public List<String> getArrivalCities2() {
		return dbManager.getArrivalCities2();
	}
	
	public List<Ride> getRidesByArrivalCity(String arrivalCity) {
		return dbManager.getRidesByArrivalCity(arrivalCity);
	}

}