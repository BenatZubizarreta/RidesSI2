package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

@Entity
@DiscriminatorValue("Traveler")
public class Traveler extends User  {

	private static final long serialVersionUID = 1L;

	@OneToMany(targetEntity=Eskaera.class, mappedBy="traveler",fetch=FetchType.EAGER,
			 cascade=CascadeType.REMOVE)
	private Set<Eskaera> eskaerak = new HashSet<Eskaera>();

	public Traveler() {
	}

	public Set<Eskaera> getEskaerak() {
		return eskaerak;
	}

	public void setEskaerak(Set<Eskaera> eskaerak) {
		this.eskaerak = eskaerak;
	}

	public Traveler(String izena, String pasahitza) {
		super(izena, pasahitza);
	}

	public boolean doesRideExists(Ride bidaia, int eserKop) {
		for (Eskaera r : eskaerak) {
			if ((java.util.Objects.equals(r.getBidaia(), bidaia))
					&& (java.util.Objects.equals(r.getEserKop(), eserKop))) {
				return true;
			}
		}
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
		Traveler other = (Traveler) obj;
		return java.util.Objects.equals(this.getName(), other.getName());
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(this.getName());
	}
}
