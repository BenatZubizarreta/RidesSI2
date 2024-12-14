package eredua.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import businessLogic.BLFacade;
import domain.Ride;

@ManagedBean(name = "book")
@SessionScoped
public class BookBean {
	private String departCity;
	private String arrivalCity;
	private Integer seats;
	private Double price;
	private List<Date> ridesDates;
	private Date date;
	private List<Ride> rides;
	private boolean searchPerformed = false;
	private Ride selectedRide;

	private List<String> availableDepartCities;
	private List<String> availableArrivalCities;
	private List<Integer> availableSeats;
	private BLFacade facade = FacadeBean.getBusinessLogic();

	public BookBean() {
		initializeDepartCities();
		availableSeats = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		rides = new ArrayList<>();
		price = 150.0;
	}

	public String getDepartCity() {
		return departCity;
	}

	public void setDepartCity(String departCity) {
		this.departCity = departCity;
	}

	public String getArrivalCity() {
		return arrivalCity;
	}

	public void setArrivalCity(String arrivalCity) {
		this.arrivalCity = arrivalCity;
	}

	public Integer getSeats() {
		return seats;
	}

	public void setSeats(Integer seats) {
		this.seats = seats;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<String> getAvailableDepartCities() {
		return availableDepartCities;
	}

	public void setAvailableDepartCities(List<String> availableDepartCities) {
		this.availableDepartCities = availableDepartCities;
	}

	public List<String> getAvailableArrivalCities() {
		return availableArrivalCities;
	}

	public void setAvailableArrivalCities(List<String> availableArrivalCities) {
		this.availableArrivalCities = availableArrivalCities;
	}

	public List<Integer> getAvailableSeats() {
		return availableSeats;
	}

	public void setAvailableSeats(List<Integer> availableSeats) {
		this.availableSeats = availableSeats;
	}

	public List<Ride> getRides() {
		return rides;
	}

	public void setRides(List<Ride> rides) {
		this.rides = rides;
	}

	public boolean isSearchPerformed() {
		return searchPerformed;
	}

	public void setSearchPerformed(boolean searchPerformed) {
		this.searchPerformed = searchPerformed;
	}

	public Ride getSelectedRide() {
		return selectedRide;
	}

	public void setSelectedRide(Ride selectedRide) {
		this.selectedRide = selectedRide;
	}

	public String aukeratu() {
		try {
			if (selectedRide.getnPlaces() <= 0) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, " Ez dira eserlekuak gelditzen.",null));
				return null;
			} else {
				FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("selectedRide",
						selectedRide);
				return "aukeratu";
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ezin izan da bidaia aukeratu.", null));
			return null;
		}
	}

	private void initializeDepartCities() {
		try {
			availableDepartCities = facade.getDepartCities();
			availableArrivalCities = List.of();
		} catch (Exception e) {
			System.err.println("Error retrieving departure cities: " + e.getMessage());
		}
	}

	public void updateArrivalCities() {
		if (departCity != null && !departCity.isEmpty()) {
			try {
				availableArrivalCities = facade.getDestinationCities(departCity);
			} catch (Exception e) {
				System.err.println("Error retrieving arrival cities: " + e.getMessage());
				availableArrivalCities = List.of();
			}
		} else {
			availableArrivalCities = List.of();
		}
	}

	public List<Date> getRidesDates() {
		Date d = new Date();
		if (this.date != null) {
			d = this.date;
		}
		ridesDates = facade.getThisMonthDatesWithRides(departCity, arrivalCity, d);
		return ridesDates;
	}

	public String loadAvailableRides() {
		if (departCity != null && arrivalCity != null && date != null) {
			this.rides = facade.getRides(departCity, arrivalCity, date);
		} else {
			this.rides = new ArrayList<>();
		}
		searchPerformed = true;
		return "ok";
	}

	public String aukeratu(Ride selectedRide) {
		try {
			System.out.println(selectedRide);
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("selectedRide", selectedRide);
			return "aukeratu";
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Errorea",
					"Ezin izan da bidaia aukeratu, saiatu berriro"));
			return null;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(arrivalCity, availableArrivalCities, availableDepartCities, availableSeats, date,
				departCity, price, seats);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BookBean other = (BookBean) obj;
		return Objects.equals(arrivalCity, other.arrivalCity)
				&& Objects.equals(availableArrivalCities, other.availableArrivalCities)
				&& Objects.equals(availableDepartCities, other.availableDepartCities)
				&& Objects.equals(availableSeats, other.availableSeats) && Objects.equals(date, other.date)
				&& Objects.equals(departCity, other.departCity) && Objects.equals(price, other.price)
				&& Objects.equals(seats, other.seats);
	}
}
