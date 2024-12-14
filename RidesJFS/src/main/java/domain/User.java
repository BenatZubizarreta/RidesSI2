package domain;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DTYPE", discriminatorType = DiscriminatorType.STRING)
public abstract class User   {

	@Id
	private String name;
	private String password;

	private float diruKop;

	public User() {}
	
	public User(String name, String password) {
		this.name = name;
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getDiruKop() {
		return diruKop;
	}

	public void setDiruKop(float diruKop) {
		this.diruKop = diruKop;
	}

	public void gehituDiruKop(float gehitu) {
		this.diruKop += gehitu;
	}

	@Override
	public String toString() {
		return name;
	}
}
