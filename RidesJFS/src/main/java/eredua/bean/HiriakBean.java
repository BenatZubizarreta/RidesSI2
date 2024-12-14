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

@ManagedBean(name = "hiriakBEan")
@SessionScoped
public class HiriakBean {
	private String arrivalCity;
	private List<String> availableArrivalCities;
	private List<Ride> rides;
	private BLFacade facade = FacadeBean.getBusinessLogic();

	public HiriakBean() {
		initializeArrivalCities();
	}

	public String getArrivalCity() {
		return arrivalCity;
	}

	public void setArrivalCity(String arrivalCity) {
		this.arrivalCity = arrivalCity;
	}

	public List<String> getAvailableArrivalCities() {
		return availableArrivalCities;
	}

	public void setAvailableArrivalCities(List<String> availableArrivalCities) {
		this.availableArrivalCities = availableArrivalCities;
	}

	public List<Ride> getRides() {
		return rides;
	}

	public void setRides(List<Ride> rides) {
		this.rides = rides;
	}

	public String aukeratu() {
		try {
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("selectedCity", arrivalCity);
			rides = facade.getRidesByArrivalCity(arrivalCity);
			return "bidaiak";
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Errorea",
					"Ezin izan da hiria aukeratu, saiatu berriro"));
			return null;
		}
	}

	private void initializeArrivalCities() {
		try {
			availableArrivalCities = facade.getArrivalCities2();
		} catch (Exception e) {
			System.err.println("Error retrieving departure cities: " + e.getMessage());
		}
	}

	/*
	 * public String aukeratu(String arrivalCity) { try {
	 * System.out.println(arrivalCity);
	 * FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(
	 * "selectedCity", arrivalCity); return "aukeratu"; } catch (Exception e) {
	 * FacesContext.getCurrentInstance().addMessage(null, new
	 * FacesMessage(FacesMessage.SEVERITY_ERROR, "Errorea",
	 * "Ezin izan da hiria aukeratu, saiatu berriro")); return null; } }
	 */

	@Override
	public int hashCode() {
		return Objects.hash(arrivalCity, availableArrivalCities, facade);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HiriakBean other = (HiriakBean) obj;
		return Objects.equals(arrivalCity, other.arrivalCity)
				&& Objects.equals(availableArrivalCities, other.availableArrivalCities)
				&& Objects.equals(facade, other.facade);
	}

}
