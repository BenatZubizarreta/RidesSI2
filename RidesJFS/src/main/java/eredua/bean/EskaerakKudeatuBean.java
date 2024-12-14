package eredua.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import businessLogic.BLFacade;
import domain.Driver;
import domain.Eskaera;
import domain.Ride;

@SessionScoped
public class EskaerakKudeatuBean {

	private Integer selectedRide;
	private Integer selectedReservation;

	private List<Ride> availableRides;
	private List<Eskaera> reservations;

	private String action;
	private BLFacade facade = FacadeBean.getBusinessLogic();
	List<Eskaera> removedReservations = new ArrayList<>();

	public EskaerakKudeatuBean() {
		updateRides();
		System.out.println(availableRides);
	}

	public Integer getSelectedRide() {
		return selectedRide;
	}

	public void setSelectedRide(Integer selectedRide) {
		this.selectedRide = selectedRide;
		updateReservations();
	}

	public Integer getSelectedReservation() {
		return selectedReservation;
	}

	public void setSelectedReservation(Integer selectedReservation) {
		this.selectedReservation = selectedReservation;
	}

	public List<Ride> getAvailableRides() {
		return availableRides;
	}

	public List<Eskaera> getReservations() {
		return reservations;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void updateRides() {
		String driver = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("name");
		Driver d = (Driver) facade.getUser(driver);
		if (d != null) {
			availableRides = facade.BidaiakBegiratu(d);
		}
	}

	public void updateReservations() {
		Ride r = facade.getRideById(selectedRide);
		if (selectedRide != null && r != null && !availableRides.isEmpty()) {
			reservations = facade.EskaerakBegiratu(r);
			reservations.removeIf(Eskaera::isOnartuaDa);
			reservations.removeIf(e -> {
				if (e.getEserKop() > r.getnPlaces()) {
					removedReservations.add(e);
					return true;
				}
				return false;
			});
		}
		this.ezabatuEskaerak();
	}

	public void ezabatuEskaerak() {
		for(Eskaera e:removedReservations) {
			facade.EskaeraEzabatu(e);
		}
		removedReservations.clear();
	}
	public String confirmAction() {

		if (selectedReservation != null) {
			System.out.println(action);

			if (action != null) {

				Eskaera esk = facade.getEskaeraById(selectedReservation);
				if (esk != null) {
					facade.EskaeraOnartuBaztertu(esk, action);
					updateRides();
					updateReservations();
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "Eskaeraren egoera eguneratu da.", null));
				}
			}
		}
		return "berretsita";
	}

}
