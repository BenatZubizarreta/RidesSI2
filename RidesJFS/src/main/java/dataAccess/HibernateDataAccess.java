package dataAccess;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import configuration.ConfigXML;
import configuration.UtilDate;
import domain.Driver;
import domain.Eskaera;
import domain.Ride;
import domain.Traveler;
import domain.User;
import eredua.HibernateUtil;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;

public class HibernateDataAccess {

	ConfigXML c = ConfigXML.getInstance();

	public HibernateDataAccess() {
	};
	/*
	 * public HibernateDataAccess() { if (c.isDatabaseInitialized()) { String
	 * fileName = c.getDbFilename();
	 * 
	 * File fileToDelete = new File(fileName); if (fileToDelete.delete()) { File
	 * fileToDeleteTemp = new File(fileName + "$"); fileToDeleteTemp.delete();
	 * 
	 * System.out.println("File deleted"); } else {
	 * System.out.println("Operation failed"); } } Session session =
	 * HibernateUtil.getSessionFactory().getCurrentSession(); if
	 * (c.isDatabaseInitialized()) initializeDB();
	 * 
	 * System.out.println("DataAccess created => isDatabaseLocal: " +
	 * c.isDatabaseLocal() + " isDatabaseInitialized: " +
	 * c.isDatabaseInitialized());
	 * 
	 * session.close();; }
	 */

	public void initializeDB() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = session.beginTransaction();

		try {
			Calendar today = Calendar.getInstance();

			int month = today.get(Calendar.MONTH);
			int year = today.get(Calendar.YEAR);
			if (month == 12) {
				month = 1;
				year += 1;
			}

			Driver driver1 = new Driver("driver1@gmail.com", "Aitor Fernandez");
			Driver driver2 = new Driver("driver2@gmail.com", "Ane Gaztañaga");
			Driver driver3 = new Driver("Test driver", "driver3@gmail.com");

			driver1.addRide("Donostia", "Bilbo", UtilDate.newDate(year, month, 15), 4, 7);
			driver1.addRide("Donostia", "Gazteiz", UtilDate.newDate(year, month, 6), 4, 8);
			driver1.addRide("Bilbo", "Donostia", UtilDate.newDate(year, month, 25), 4, 4);

			driver1.addRide("Donostia", "Iruña", UtilDate.newDate(year, month, 7), 4, 8);

			driver2.addRide("Donostia", "Bilbo", UtilDate.newDate(year, month, 15), 3, 3);
			driver2.addRide("Bilbo", "Donostia", UtilDate.newDate(year, month, 25), 2, 5);
			driver2.addRide("Eibar", "Gasteiz", UtilDate.newDate(year, month, 6), 2, 5);

			driver3.addRide("Bilbo", "Donostia", UtilDate.newDate(year, month, 14), 1, 3);

			session.persist(driver1);
			session.persist(driver2);
			session.persist(driver3);

			tx.commit();
			System.out.println("Db initialized");
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		}
	}

	public List<String> getDepartCities() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Query query = session.createQuery("SELECT DISTINCT r.departCity FROM Ride r ORDER BY r.departCity");
		@SuppressWarnings("unchecked")
		List<String> cities = query.list();
		session.getTransaction().commit();
		return cities;
	}
	public List<String> getArrivalCities2() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Query query = session.createQuery("SELECT DISTINCT r.arrivalCity FROM Ride r ORDER BY r.arrivalCity");
		@SuppressWarnings("unchecked")
		List<String> cities = query.list();
		session.getTransaction().commit();
		return cities;
	}
	public List<String> getArrivalCities(String departCity) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Query query = session.createQuery(
				"SELECT DISTINCT r.arrivalCity FROM Ride r WHERE r.departCity = :departCity ORDER BY r.arrivalCity");
		query.setParameter("departCity", departCity);
		@SuppressWarnings("unchecked")
		List<String> arrivingCities = query.list();
		session.getTransaction().commit();
		return arrivingCities;
	}

	public Ride createRide(String from, String to, Date date, int nPlaces, float price, String driverEmail)
			throws RideAlreadyExistException, RideMustBeLaterThanTodayException {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		if (new Date().compareTo(date) > 0) {
			throw new RideMustBeLaterThanTodayException("Ride date must be in the future.");
		}

		session.beginTransaction();
		try {
			Driver driver = (Driver) session.get(Driver.class, driverEmail);

			if (driver.doesRideExists(from, to, date)) {
				throw new RideAlreadyExistException("Ride already exists for this driver.");
			}

			Ride ride = driver.addRide(from, to, date, nPlaces, price);
			session.persist(ride);
			session.getTransaction().commit();

			return ride;
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		}
	}

	public List<Ride> getRides(String departCity, String to, Date date) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Query query = session.createQuery(
				"SELECT r FROM Ride r WHERE r.departCity = :departCity AND r.arrivalCity = :to AND r.dateRide = :date ORDER BY r.id");
		query.setParameter("departCity", departCity);
		query.setParameter("to", to);
		query.setParameter("date", date);
		@SuppressWarnings("unchecked")
		List<Ride> rides = query.list();
		session.getTransaction().commit();
		return rides;
	}

	public List<Date> getThisMonthDatesWithRides(String departCity, String to, Date date) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		System.out.println(">> DataAccess: getEventsMonth");
		List<Date> res = new ArrayList<>();

		Date firstDayMonthDate = UtilDate.firstDayMonth(date);
		Date lastDayMonthDate = UtilDate.lastDayMonth(date);

		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			String hql = "SELECT DISTINCT r.dateRide FROM Ride r WHERE r.departCity = :from AND r.arrivalCity = :to AND r.dateRide BETWEEN :start AND :end";
			Query query = session.createQuery(hql);
			query.setParameter("departCity", departCity);
			query.setParameter("to", to);
			query.setParameter("start", firstDayMonthDate);
			query.setParameter("end", lastDayMonthDate);

			res = query.list();

			tx.commit();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} /*
			 * finally { session.close(); }
			 */

		return res;
	}

	public User Erregistratu(String izena, String pasahitza, String mota) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		User berria = null;
		try {
			session.beginTransaction();

			if (mota.equals("Gidari")) {
				berria = new Driver(izena, pasahitza);
			} else {
				berria = new Traveler(izena, pasahitza);
			}
			session.save(berria);

			session.getTransaction().commit();
		} catch (Exception e) {
			if (session.getTransaction() != null)
				session.getTransaction().rollback();
			berria = null;
			e.printStackTrace();
		}
		return berria;
	}

	public User Login(String izena, String pasahitza) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		User berria = null;
		try {
			session.beginTransaction();
			String hql = "FROM User u WHERE u.name = :name AND u.password = :password";
			Query query = session.createQuery(hql);
			query.setParameter("name", izena);
			query.setParameter("password", pasahitza);

			berria = (User) query.uniqueResult();

			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return berria;
	}

	public void SartuDirua(User u, float zenbat) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			User user = (User) session.get(User.class, u.getName());
			if (user != null) {
				user.setDiruKop(user.getDiruKop() + zenbat);
				session.update(user);
			}

			tx.commit();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		}
	}

	public Eskaera EskaeraEgin(Traveler u, int eserKop, Ride bidaia) throws IllegalArgumentException {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		Transaction tx = null;
		Eskaera berria = null;

		tx = session.beginTransaction();

		User user = (User) session.get(User.class, u.getName());
		if (user == null) {
			throw new IllegalArgumentException("Ez da erabiltzailea aurkitu: " + u.getName());
		}

		double totalPrice = bidaia.getPrice() * eserKop;
		if (user.getDiruKop() < totalPrice) {
			throw new IllegalArgumentException("Ez duzu diru nahikoa erreserba gauzatzeko.");
		}
		user.setDiruKop((float) (user.getDiruKop() - totalPrice));
		berria = new Eskaera(1, bidaia, eserKop, (Traveler) user);

		session.save(user);

		session.save(berria);

		tx.commit();

		return berria;
	}

	public List<Ride> BidaiakBegiratu(Driver dr) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		List<Ride> emaitza = null;

		try {
			session.beginTransaction();
			String hql = "FROM Ride r WHERE r.driver = :driver";
			Query query = session.createQuery(hql);
			query.setParameter("driver", dr);

			emaitza = query.list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		session.getTransaction().commit();
		return emaitza;
	}

	public List<Eskaera> EskaerakBegiratu(Ride bidaia) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		List<Eskaera> emaitza = new ArrayList<>();
		session.beginTransaction();
		try {

			String hql = "FROM Eskaera e WHERE e.bidaia = :ride";
			Query query = session.createQuery(hql);
			query.setParameter("ride", bidaia);

			emaitza.addAll(query.list());

		} catch (Exception e) {
			e.printStackTrace();
		}
		session.getTransaction().commit();
		return emaitza;
	}

	public void EskaeraOnartuBaztertu(Eskaera e, String zer) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = session.beginTransaction();

		try {
			if (zer.equals("b")) {
				Traveler aldatu = (Traveler) session.get(Traveler.class, e.getTraveler().getName());
				aldatu.gehituDiruKop(e.getPrezioTotala());
				session.update(aldatu);

				Eskaera eskaeraToRemove = (Eskaera) session.get(Eskaera.class, e.getErreserbaZenbakia());
				session.delete(eskaeraToRemove);
			} else {
				Ride aldatu = (Ride) session.createQuery("SELECT r FROM Ride r WHERE r = :ride")
						.setParameter("ride", e.getBidaia()).uniqueResult();

				aldatu.setNplaces((int) (aldatu.getnPlaces() - e.getEserKop()));
				session.update(aldatu);

				Driver diruaGehitu = (Driver) session.createQuery("SELECT d FROM Driver d WHERE d = :driver")
						.setParameter("driver", aldatu.getDriver()).uniqueResult();
				diruaGehitu.gehituDiruKop(e.getPrezioTotala());
				session.update(diruaGehitu);

				/*
				 * if (aldatu.getnPlaces() <= 0) { session.delete(aldatu); }
				 */

				Eskaera eskaeraToAccept = (Eskaera) session
						.createQuery("SELECT e FROM Eskaera e WHERE e.erreserbaZenbakia = :id")
						.setParameter("id", e.getErreserbaZenbakia()).uniqueResult();

				eskaeraToAccept.setOnartuaDa(true);
				session.update(eskaeraToAccept);
			}

			tx.commit();
		} catch (Exception ex) {
			if (tx != null)
				tx.rollback();
			ex.printStackTrace();
		}
	}

	public void EskaeraEzabatu(Eskaera e) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = session.beginTransaction();

		try {
			Eskaera eskaeraToDelete = (Eskaera) session.get(Eskaera.class, e.getErreserbaZenbakia());
			Traveler aldatu = (Traveler) session.get(Traveler.class, e.getTraveler().getName());
			aldatu.gehituDiruKop(e.getPrezioTotala());
			session.update(aldatu);
			if (eskaeraToDelete != null) {
				session.delete(eskaeraToDelete);
			}

			tx.commit();
		} catch (Exception ex) {
			if (tx != null)
				tx.rollback();
			ex.printStackTrace();
		}
	}

	public User getUser(String name) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		User user = null;
		try {
			session.beginTransaction();
			String hql = "FROM User u WHERE u.name = :name";
			Query query = session.createQuery(hql);
			query.setParameter("name", name);

			user = (User) query.uniqueResult();
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}

	public Ride getRideById(int id) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Ride ride = null;
		try {
			session.beginTransaction();
			String hql = "FROM Ride r WHERE r.rideNumber = :id";
			Query query = session.createQuery(hql);
			query.setParameter("id", id);

			ride = (Ride) query.uniqueResult();
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ride;
	}

	public Eskaera getEskaeraById(int id) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Eskaera eskaera = null;
		try {
			session.beginTransaction();
			String hql = "FROM Eskaera e WHERE e.erreserbaZenbakia = :id";
			Query query = session.createQuery(hql);
			query.setParameter("id", id);

			eskaera = (Eskaera) query.uniqueResult();
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return eskaera;
	}
	public List<Ride> getRidesByArrivalCity(String arrivalCity) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Query query = session.createQuery(
				"SELECT r FROM Ride r WHERE r.arrivalCity = :arrivalCity");
		query.setParameter("arrivalCity", arrivalCity);
		@SuppressWarnings("unchecked")
		List<Ride> rides = query.list();
		session.getTransaction().commit();
		return rides;
	}

}
