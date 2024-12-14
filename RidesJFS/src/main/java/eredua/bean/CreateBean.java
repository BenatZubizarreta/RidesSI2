package eredua.bean;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import businessLogic.BLFacade;
import domain.Ride;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;


public class CreateBean {

    private String departCity;
    private String arrivalCity;
    private Date date;
    private int seats;
    private float price;
    private String name; 

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getNAme() {
        return name;
    }

    public void setName(String driverEmail) {
        this.name = driverEmail;
    }


    public String create() {
        BLFacade facade = FacadeBean.getBusinessLogic();
        try {
            String loggedInDriver = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                    .get("name");
            if (loggedInDriver == null) {
                loggedInDriver = name;
            }
            Ride newRide = facade.createRide(departCity, arrivalCity, date, seats, price, loggedInDriver);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Bidaia sortu da", null));

            return "ongi"; 
        } catch (RideAlreadyExistException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bidaia dagoeneko existitzen da.", null));
            return "existitzenDa";
        } catch (RideMustBeLaterThanTodayException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Datak ezin du gaur baino lehenagokoa izan.", null));
            return "gaurBainoLehen";
        } catch (Exception e) {
        	System.out.println(e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Errorea bidaia sortzerakoan.", null));
            return "errorea";
        }
    }
}
