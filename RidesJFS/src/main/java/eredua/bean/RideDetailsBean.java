package eredua.bean;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import businessLogic.BLFacade;
import domain.Ride;
import domain.Traveler;

@ManagedBean
@ViewScoped
public class RideDetailsBean {
	private Ride ride;
	private int selectedSeats;
	private List<Integer> availableSeats;
	private BLFacade facade = FacadeBean.getBusinessLogic();

	@PostConstruct
	public void init() {
		ride = (Ride) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("selectedRide");
		this.availableSeats = IntStream.rangeClosed(1, ride.getnPlaces()).boxed().collect(Collectors.toList());

	}

	public String bookRide() {
		if (selectedSeats > ride.getnPlaces()) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ezin dira hainbeste eserleku erreserbatu",
					null));
			return null;
		}
		try {
		String traveler = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("name");
		Traveler t = (Traveler) facade.getUser(traveler);
		facade.EskaeraEgin(t, selectedSeats, this.ride);
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bidaiaren eskaera burutu duzu", null));
		return "erreserbatuta";
		}catch(IllegalArgumentException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ez duzu diru nahikoa bidaia hau erreserbatzeko",
					null));
			return null;
		}
	}

	public Ride getRide() {
		return ride;
	}

	public void setRide(Ride ride) {
		this.ride = ride;
	}

	public int getSelectedSeats() {
		return selectedSeats;
	}

	public void setSelectedSeats(int selectedSeats) {
		this.selectedSeats = selectedSeats;
	}

	public List<Integer> getAvailableSeats() {
		return availableSeats;
	}

	public void setAvailableSeats(List<Integer> availableSeats) {
		this.availableSeats = availableSeats;
	}
}
