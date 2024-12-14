package dataAccess;

import java.io.File;
import java.net.NoRouteToHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.jdo.annotations.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;

import configuration.ConfigXML;
import configuration.UtilDate;
import domain.Driver;
import domain.Eskaera;
import domain.Ride;
import domain.Traveler;
import domain.User;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;

/**
 * It implements the data access to the objectDb database
 */
public class DataAccess {
	private EntityManager db;
	private EntityManagerFactory emf;

	ConfigXML c = ConfigXML.getInstance();

	public DataAccess() {
		if (c.isDatabaseInitialized()) {
			String fileName = c.getDbFilename();

			File fileToDelete = new File(fileName);
			if (fileToDelete.delete()) {
				File fileToDeleteTemp = new File(fileName + "$");
				fileToDeleteTemp.delete();

				System.out.println("File deleted");
			} else {
				System.out.println("Operation failed");
			}
		}
		open();
		if (c.isDatabaseInitialized())
			initializeDB();

		System.out.println("DataAccess created => isDatabaseLocal: " + c.isDatabaseLocal() + " isDatabaseInitialized: "
				+ c.isDatabaseInitialized());

		close();

	}

	public DataAccess(EntityManager db) {
		this.db = db;
	}

	/**
	 * This is the data access method that initializes the database with some events
	 * and questions. This method is invoked by the business logic (constructor of
	 * BLFacadeImplementation) when the option "initialize" is declared in the tag
	 * dataBaseOpenMode of resources/config.xml file
	 */
	public void initializeDB() {

		db.getTransaction().begin();

		try {

			Calendar today = Calendar.getInstance();

			int month = today.get(Calendar.MONTH);
			int year = today.get(Calendar.YEAR);
			if (month == 12) {
				month = 1;
				year += 1;
			}

			// Create drivers
			Driver driver1 = new Driver("driver1@gmail.com", "Aitor Fernandez");
			Driver driver2 = new Driver("driver2@gmail.com", "Ane Gaztañaga");
			Driver driver3 = new Driver("driver3@gmail.com", "Test driver");

			// Create rides
			driver1.addRide("Donostia", "Bilbo", UtilDate.newDate(year, month, 15), 4, 7);
			driver1.addRide("Donostia", "Gazteiz", UtilDate.newDate(year, month, 6), 4, 8);
			driver1.addRide("Bilbo", "Donostia", UtilDate.newDate(year, month, 25), 4, 4);

			driver1.addRide("Donostia", "Iruña", UtilDate.newDate(year, month, 7), 4, 8);

			driver2.addRide("Donostia", "Bilbo", UtilDate.newDate(year, month, 15), 3, 3);
			driver2.addRide("Bilbo", "Donostia", UtilDate.newDate(year, month, 25), 2, 5);
			driver2.addRide("Eibar", "Gasteiz", UtilDate.newDate(year, month, 6), 2, 5);

			driver3.addRide("Bilbo", "Donostia", UtilDate.newDate(year, month, 14), 1, 3);

			db.persist(driver1);
			db.persist(driver2);
			db.persist(driver3);

			db.getTransaction().commit();
			System.out.println("Db initialized");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method returns all the cities where rides depart
	 * 
	 * @return collection of cities
	 */
	public List<String> getDepartCities() {
		TypedQuery<String> query = db.createQuery("SELECT DISTINCT r.from FROM Ride r ORDER BY r.from", String.class);
		List<String> cities = query.getResultList();
		return cities;

	}

	/**
	 * This method returns all the arrival destinations, from all rides that depart
	 * from a given city
	 * 
	 * @param from the depart location of a ride
	 * @return all the arrival destinations
	 */
	public List<String> getArrivalCities(String from) {
		TypedQuery<String> query = db.createQuery("SELECT DISTINCT r.to FROM Ride r WHERE r.from=?1 ORDER BY r.to",
				String.class);
		query.setParameter(1, from);
		List<String> arrivingCities = query.getResultList();
		return arrivingCities;

	}

	/**
	 * This method creates a ride for a driver
	 * 
	 * @param from        the origin location of a ride
	 * @param to          the destination location of a ride
	 * @param date        the date of the ride
	 * @param nPlaces     available seats
	 * @param driverEmail to which ride is added
	 * 
	 * @return the created ride, or null, or an exception
	 * @throws RideMustBeLaterThanTodayException if the ride date is before today
	 * @throws RideAlreadyExistException         if the same ride already exists for
	 *                                           the driver
	 */
	public Ride createRide(String from, String to, Date date, int nPlaces, float price, String driverEmail)
			throws RideAlreadyExistException, RideMustBeLaterThanTodayException {
		System.out.println(">> DataAccess: createRide=> from= " + from + " to= " + to + " driver=" + driverEmail
				+ " date " + date);
		try {
			if (new Date().compareTo(date) > 0) {
				throw new RideMustBeLaterThanTodayException(
						ResourceBundle.getBundle("Etiquetas").getString("CreateRideGUI.ErrorRideMustBeLaterThanToday"));
			}
			db.getTransaction().begin();

			Driver driver = db.find(Driver.class, driverEmail);
			if (driver.doesRideExists(from, to, date)) {
				db.getTransaction().commit();
				throw new RideAlreadyExistException(
						ResourceBundle.getBundle("Etiquetas").getString("DataAccess.RideAlreadyExist"));
			}
			Ride ride = driver.addRide(from, to, date, nPlaces, price);
			// next instruction can be obviated
			db.persist(driver);
			db.getTransaction().commit();

			return ride;
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			db.getTransaction().commit();
			return null;
		}

	}

	/**
	 * This method retrieves the rides from two locations on a given date
	 * 
	 * @param from the origin location of a ride
	 * @param to   the destination location of a ride
	 * @param date the date of the ride
	 * @return collection of rides
	 */
	public List<Ride> getRides(String from, String to, Date date) {
		System.out.println(">> DataAccess: getRides=> from= " + from + " to= " + to + " date " + date);

		List<Ride> res = new ArrayList<>();
		TypedQuery<Ride> query = db.createQuery("SELECT r FROM Ride r WHERE r.from=?1 AND r.to=?2 AND r.date=?3",
				Ride.class);
		query.setParameter(1, from);
		query.setParameter(2, to);
		query.setParameter(3, date);
		List<Ride> rides = query.getResultList();
		for (Ride ride : rides) {
			res.add(ride);
		}
		return res;
	}

	/**
	 * This method retrieves from the database the dates a month for which there are
	 * events
	 * 
	 * @param from the origin location of a ride
	 * @param to   the destination location of a ride
	 * @param date of the month for which days with rides want to be retrieved
	 * @return collection of rides
	 */
	public List<Date> getThisMonthDatesWithRides(String from, String to, Date date) {
		System.out.println(">> DataAccess: getEventsMonth");
		List<Date> res = new ArrayList<>();

		Date firstDayMonthDate = UtilDate.firstDayMonth(date);
		Date lastDayMonthDate = UtilDate.lastDayMonth(date);

		TypedQuery<Date> query = db.createQuery(
				"SELECT DISTINCT r.date FROM Ride r WHERE r.from=?1 AND r.to=?2 AND r.date BETWEEN ?3 and ?4",
				Date.class);

		query.setParameter(1, from);
		query.setParameter(2, to);
		query.setParameter(3, firstDayMonthDate);
		query.setParameter(4, lastDayMonthDate);
		List<Date> dates = query.getResultList();
		for (Date d : dates) {
			res.add(d);
		}
		return res;
	}

	public User Erregistratu(String izena, String pasahitza, String mota) {
		this.open();
		db.getTransaction().begin();
		User berria = null;
		try {
			if (mota.equals("Gidari")) {
				berria = new Driver(izena, pasahitza);
			} else {
				berria = new Traveler(izena, pasahitza);
			}
			db.persist(berria);
			db.getTransaction().commit();
			this.close();
			return berria;
		} catch (RollbackException e) {
			return null;
		}

	}

	public User Login(String izena, String pasahitza) {
		this.open();
		db.getTransaction().begin();
		User berria;
		TypedQuery<User> query = db.createQuery("SELECT DISTINCT u FROM User u WHERE u.name=?1 AND u.password=?2",
				User.class);
		// TypedQuery<Traveler> query2 = db.createQuery("SELECT DISTINCT u FROM Traveler
		// u WHERE u.name=?1 AND u.password=?2",Traveler.class);
		query.setParameter(1, izena);
		query.setParameter(2, pasahitza);
		// query2.setParameter(1, izena);
		// query2.setParameter(2, pasahitza);
		try {
			// if(query.getResultList().size()<=0)
			// berria=query2.getResultList().get(0);
			// else
			berria = query.getResultList().get(0);
		} catch (IndexOutOfBoundsException ei) {
			berria = null;
		}
		db.getTransaction().commit();
		this.close();
		return berria;

	}

	public void SartuDirua(User u, float zenbat) {
		this.open();
		User gehitu;
		db.getTransaction().begin();
		TypedQuery<User> query = db.createQuery("SELECT DISTINCT u FROM User u WHERE u.name=?1", User.class);
		query.setParameter(1, u.getName());
		gehitu = query.getSingleResult();
		gehitu.setDiruKop(zenbat);
		db.getTransaction().commit();
		this.close();
	}

	public Eskaera EskaeraEgin(Traveler u, int eserKop, Ride bidaia) {
		Eskaera berria = new Eskaera(this.AzkenKodea() + 1, bidaia, eserKop, u);
		this.open();
		db.getTransaction().begin();
		User aldatu;
		TypedQuery<User> query = db.createQuery("SELECT DISTINCT u FROM User u WHERE u.name=?1", User.class);
		query.setParameter(1, u.getName());
		aldatu = query.getSingleResult();
		aldatu.setDiruKop(-(bidaia.getPrice() * eserKop));
		Traveler gehituEskaera = (Traveler) aldatu;
		gehituEskaera.getEskaerak().add(berria);
		db.persist(berria);
		db.getTransaction().commit();
		this.close();
		return berria;
	}

	public List<Ride> BidaiakBegiratu(Driver dr) {
		this.open();
		List<Ride> emaitza = new ArrayList<Ride>();
		db.getTransaction().begin();
		TypedQuery<Ride> query = db.createQuery("SELECT DISTINCT r FROM Ride r WHERE r.driver=?1", Ride.class);
		query.setParameter(1, dr);
		emaitza = query.getResultList();
		db.getTransaction().commit();
		this.close();
		return emaitza;
	}

	public List<Eskaera> EskaerakBegiratu(List<Ride> bidaiak) {
		this.open();
		List<Eskaera> emaitza = new ArrayList<Eskaera>();
		db.getTransaction().begin();
		for (Ride r : bidaiak) {
			TypedQuery<Eskaera> query = db.createQuery("SELECT DISTINCT e FROM Eskaera e WHERE e.bidaia=?1",
					Eskaera.class);
			query.setParameter(1, r);
			emaitza.addAll(query.getResultList());
		}
		db.getTransaction().commit();
		this.close();
		return emaitza;
	}

	public void EskaeraOnartuBaztertu(Eskaera e, String zer) {
		this.open();
		db.getTransaction().begin();
		if (zer.equals("b")) {
			User aldatu = e.getTraveler();
			TypedQuery<Traveler> query = db.createQuery("SELECT DISTINCT e.traveler FROM Eskaera e WHERE e=?1",
					Traveler.class);
			query.setParameter(1, e);
			aldatu.setDiruKop(e.getPrezioTotala());
			TypedQuery<Eskaera> query3 = db.createQuery("SELECT DISTINCT e FROM Eskaera e WHERE e.erreserbaZenbakia=?1",
					Eskaera.class);
			query3.setParameter(1, e.getErreserbaZenbakia());
			db.remove(query3.getSingleResult());
		} else {
			Ride aldatu;
			Driver diruaGehitu;
			TypedQuery<Ride> query = db.createQuery("SELECT DISTINCT e.bidaia FROM Eskaera e WHERE e=?1", Ride.class);
			query.setParameter(1, e);
			aldatu = query.getSingleResult();
			aldatu.setNplaces((int) aldatu.getnPlaces() - (int) e.getEserKop());
			TypedQuery<Driver> query2 = db.createQuery("SELECT DISTINCT r.driver FROM Ride r WHERE r=?1", Driver.class);
			query2.setParameter(1, aldatu);
			diruaGehitu = query2.getSingleResult();
			diruaGehitu.setDiruKop(e.getPrezioTotala());
			if (aldatu.getnPlaces() <= 0) {
				db.remove(aldatu);
			}
			TypedQuery<Eskaera> query3 = db.createQuery("SELECT DISTINCT e FROM Eskaera e WHERE e.erreserbaZenbakia=?1",
					Eskaera.class);
			query3.setParameter(1, e.getErreserbaZenbakia());
			query3.getSingleResult().setOnartuaDa(true);
		}
		db.getTransaction().commit();
		this.close();
	}

	public void EskaeraEzabatu(Eskaera e) {
		this.open();
		db.getTransaction().begin();
		TypedQuery<Eskaera> query = db.createQuery("SELECT DISTINCT e FROM Eskaera e WHERE e.erreserbaZenbakia=?1",
				Eskaera.class);
		query.setParameter(1, e.getErreserbaZenbakia());
		db.remove(query.getSingleResult());
		db.getTransaction().commit();
		this.close();
	}

	public int AzkenKodea() {
		List<Integer> kodeak;
		Integer emaitza = -1;
		this.open();
		db.getTransaction().begin();
		TypedQuery<Integer> query = db.createQuery("SELECT DISTINCT e.erreserbaZenbakia FROM Eskaera e", Integer.class);
		kodeak = query.getResultList();
		if (kodeak.size() > 0) {
			for (int i : kodeak) {
				if (emaitza < i)
					emaitza = i;
			}
		} else
			emaitza = 0;
		db.getTransaction().commit();
		this.close();
		System.out.println();

		return emaitza;

	}

	public void open() {

		String fileName = c.getDbFilename();
		if (c.isDatabaseLocal()) {
			emf = Persistence.createEntityManagerFactory("objectdb:" + fileName);
			db = emf.createEntityManager();
		} else {
			Map<String, String> properties = new HashMap<>();
			properties.put("javax.persistence.jdbc.user", c.getUser());
			properties.put("javax.persistence.jdbc.password", c.getPassword());

			emf = Persistence.createEntityManagerFactory(
					"objectdb://" + c.getDatabaseNode() + ":" + c.getDatabasePort() + "/" + fileName, properties);
			db = emf.createEntityManager();
		}
		System.out.println("DataAccess opened => isDatabaseLocal: " + c.isDatabaseLocal());

	}

	public void close() {
		db.close();
		System.out.println("DataAcess closed");
	}

}