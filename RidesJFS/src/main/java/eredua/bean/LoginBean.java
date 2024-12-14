package eredua.bean;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import com.mysql.cj.jdbc.Driver;

import businessLogic.BLFacade;
import domain.User;

@ManagedBean
@SessionScoped
public class LoginBean {

	private String name;
	private String password;

	public LoginBean() {
		super();
	}

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

	public String egiaztatu() {
		try {
			BLFacade facade = FacadeBean.getBusinessLogic();
			User erabiltzaile = facade.Login(name, password);

			if (erabiltzaile != null) {
				System.out.println(erabiltzaile.getClass());
				FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("name", erabiltzaile.getName());
				if (domain.Driver.class.equals(erabiltzaile.getClass())) {
					return "gidari";
				} else {
					return "bidaiari";
				}
			} else {
				 FacesContext.getCurrentInstance().addMessage(null,
		                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Errorea saioa irekitzean, saiatu berriro.", null));
				System.out.println("Errorea saioa irekitzean, saiatu berriro.");
				return "errorea";
			}
		} catch (Exception e) {
			 FacesContext.getCurrentInstance().addMessage(null,
	                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Errorea saioa irekitzean, saiatu berriro.", null));
			System.err.println("Errorea saioa irekitzean: " + e.getMessage());
			return "errorea";
		}
	}
}
