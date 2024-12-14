package eredua.bean;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import com.mysql.cj.jdbc.Driver;

import businessLogic.BLFacade;
import domain.User;

@ManagedBean
@RequestScoped
public class DiruaBean {

	private float kop;
	private String actionType; 
	private float zurea;
	
	@PostConstruct
	    public void init() {
			BLFacade facade = FacadeBean.getBusinessLogic();
			String user = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("name");
			User u = facade.getUser(user);
			zurea=u.getDiruKop();
	    }
	public float getZurea() {
		return zurea;
	}

	public void setZurea(float zurea) {
		this.zurea = zurea;
	}

	public float getKop() {
		return kop;
	}

	public void setKop(float diruKopurua) {
		this.kop = diruKopurua;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String atzera() {
		BLFacade facade = FacadeBean.getBusinessLogic();
		String user = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("name");
		if (user == null) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erabiltzailea saioa hasi gabe dago.", null));
			return "login";
		}
		User u = facade.getUser(user);
		if(u.getClass().equals(domain.Driver.class)) return "itxi";
		else if (u.getClass().equals(domain.Traveler.class))return "atzera";
		else return "errorea";
	}
	
	public String confirm() {
		BLFacade facade = FacadeBean.getBusinessLogic();
		try {
			String user = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("name");
			if (user == null) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erabiltzailea saioa hasi gabe dago.", null));
				return "login";
			}
			User u = facade.getUser(user);

			if ("sartu".equals(actionType)) {
				facade.SartuDirua(u, kop);
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, kop + "€ gehitu dira zure kontura.", null));
			} else if ("atera".equals(actionType)) {
				if(zurea>kop) {
				facade.SartuDirua(u, -kop);

				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, kop + "€ atera dira zure kontutik.", null));
				}else {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage("Ezin duzu duzuna baino diru gehiago atera", null));
				}
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Aukeratu baliozko ekintza: sartu edo atera.", null));
			}
			zurea = facade.getUser(user).getDiruKop();
			return "ongi";

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Errorea diru ekintzan.", null));
			return "errorea";
		}
	}
}
