package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

@Entity
@DiscriminatorValue("Driver")
public class Driver extends User{

	private static final long serialVersionUID = 1L;

	@OneToMany(targetEntity=Ride.class, mappedBy="driver",fetch=FetchType.EAGER,
			 cascade=CascadeType.REMOVE)
	private Set<Ride> rides = new HashSet<Ride>();

	public Driver() {}
	
	public Driver(String name, String password) {
		super(name, password);
	}

	@Override
	public String toString() {
		return this.getName() + rides;
	}


	public Ride addRide(String from, String to, Date date, int nPlaces, float price) {
		Ride ride = new Ride(from, to, date, nPlaces, price, this);
		rides.add(ride);
		return ride;
	}


	public boolean doesRideExists(String from, String to, Date date) {
		for (Ride r : rides)
			if ((r.getDepartCity().equals(from)) && (r.getArrivalCity().equals(to)) && (r.getDateRide().equals(date)))
				return true;

		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Driver other = (Driver) obj;
		if (!this.getName().equals(other.getName()))
			return false;
		return true;
	}


	public Ride removeRide(String from, String to, Date date) {
	    for (Iterator<Ride> iterator = rides.iterator(); iterator.hasNext(); ) {
	        Ride r = iterator.next();
	        if ((r.getDepartCity().equals(from)) && (r.getArrivalCity().equals(to)) && (r.getDateRide().equals(date))) {
	            iterator.remove(); 
	            return r; 
	        }
	    }

	    return null; 
	}



	public Set<Ride> getRides() {
		return rides;
	}

	public void setRides(Set<Ride> rides) {
		this.rides = rides;
	}
}
