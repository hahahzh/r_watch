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

	// 1:运动 2:睡眠 3:室内 4:室外 5:慢跑 6:长跑 7:浅睡 8:深睡
	@Required
	public Integer type;
	
	public Integer actualstep;
	
	public Integer targetstep;
	
	public Integer turnover;
	
	public String km;
	
	public String calories;
	
	public Date sleeptime;
	
	public Date waketime;
	
	public Integer deepsleep;
	
	public Integer lightsleep;
	
	public float actiontime;
	
	public Integer wakenum;
	
	public Integer high;
	
	public Integer heartrate;
	
	public Date sports_start_time;
	
	public Date sports_end_time;
	
	@Required
	@ManyToOne(fetch=FetchType.LAZY,cascade=javax.persistence.CascadeType.REFRESH)
	public Customer c;
	
	public String toString(){
		return c.nickname;
	}
}