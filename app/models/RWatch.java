package models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;

@Table(name = "rwatch")
@Entity
public class RWatch extends Model {

	@MaxSize(30)
	public String rcode;
	
	public String mac;
	
	public String m_number;
	
	public String guardian_number;
	
	public String nickname;
	
	public Date bindDate;
	
	//M28 M26
	@OneToOne(optional = false, cascade = CascadeType.ALL)
	public Production production;
	
	@Required
	@ManyToOne(fetch=FetchType.LAZY,cascade=javax.persistence.CascadeType.REFRESH, optional = true)
	public Customer c;
	
	public String toString(){
		return production.p_name;
	}
}