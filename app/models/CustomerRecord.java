package models;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Table(name = "customer_record")
@Entity
public class CustomerRecord extends Model {

	//体重
	public String weight;
	//身高
	public String height;
	//步数
	public String step_number;
	//里熟
	public String mileage;
	//心跳频率
	public String heartbeat;
	//消耗卡路里
	public String kaluri;
	
	@Required
	@ManyToOne(fetch=FetchType.LAZY,cascade=javax.persistence.CascadeType.REFRESH)
	public Customer c;
	
	public String toString(){
		return weight;
	}
}