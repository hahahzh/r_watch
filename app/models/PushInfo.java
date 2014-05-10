package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.data.validation.URL;
import play.db.jpa.Model;

@Table(name = "push_info")
@Entity
public class PushInfo extends Model {

	@Required
	public String pi_name;
	
	@Required
	@URL
	public String pi_url;
	
	@Column(columnDefinition="TEXT")
	@MaxSize(500)
	public String pi_desc;
	
	public String toString(){
		return pi_name;
	}
}