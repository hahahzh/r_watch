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

@Table(name = "health_record")
@Entity
public class HealthRecord extends Model {

	public Integer actualstep;
	
	public Integer targetstep;
	
	public String km;
	
	public String calories;
	
	public Date sleeptime;
	
	public Date waketime;
	
	public Integer wakenum;
	
	public Float lightsleep;
	
	public Float deepsleep;
	
	public Date createDate;
	
	@Required
	@ManyToOne(fetch=FetchType.LAZY,cascade=javax.persistence.CascadeType.REFRESH)
	public Customer c;
	
	public String toString(){
		return c.nickname;
	}
}