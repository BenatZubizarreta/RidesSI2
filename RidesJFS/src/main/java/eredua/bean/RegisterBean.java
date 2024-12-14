package eredua.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import businessLogic.BLFacade;
import domain.User;

public class RegisterBean {
	private String name;
	private String password;
	private String role;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}


	public String erregistratu() {
		BLFacade facade = FacadeBean.getBusinessLogic(); 
		try {
			User newUser = facade.Erregistratu(name, password, role); 
			if (newUser != null) {
				FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("name", newUser.getName());
				if (role.equals("Gidari"))
					return "gidari"; 
				else
					return "bidaiari";
			} else {
				FacesContext.getCurrentInstance().addMessage(null,
						new javax.faces.application.FacesMessage("Errorea: Izena erabilita dago edo huts egina."));
				return "failure";
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new javax.faces.application.FacesMessage("Errorea: Zerbitzuarekin konexioan arazo bat egon da."));
			return "failure";
		}
	}
}
