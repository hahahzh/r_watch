package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

import controllers.CRUD.Hidden;
import play.data.validation.Match;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Password;
import play.data.validation.Phone;
import play.data.validation.Required;
import play.data.validation.URL;
import play.data.validation.Unique;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import utils.DateUtil;

@Table(name = "clientversion")
@Entity
public class ClientVersion extends Model {

//	@Required
	@URL
	public String url;
	
	@MaxSize(200)
	public String update_desc;
	
	@Required
	@Index(name = "idx_version")
	public String version;
	
	@Required
	public Blob apk;
	
	@Hidden
	public String updatetime = DateUtil.reverseDate(new Date(), 0);
	
//	@Required
//	@Unique
//	@ManyToOne(fetch=FetchType.LAZY,cascade=javax.persistence.CascadeType.REFRESH, optional = true)
//	public Production production;
	
	@Required
	@ManyToOne(fetch=FetchType.LAZY,cascade=javax.persistence.CascadeType.REFRESH, optional = true)
	public MobileType mobiletype;
	
	public String toString() {
		return url;
	}
}