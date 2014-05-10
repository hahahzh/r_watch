package models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Table(name = "rwatch")
@Entity
public class RWatch extends Model {

	@MaxSize(30)
	public String rcode;
	
	public String mac;
	
	public String m_number;
	
	public String nickname;
	
	//M28 M26
	@Required
	@OneToOne(optional = false, cascade = CascadeType.ALL)
	public Production production;
	
	@Required
	@ManyToOne(fetch=FetchType.LAZY,cascade=javax.persistence.CascadeType.REFRESH)
	public Customer c;
	
	public String toString(){
		return nickname;
	}
}