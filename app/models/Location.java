package models;

import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.jpa.JPA;
import play.db.jpa.Model;

@Entity
@Table(name="locations")
public class Location extends Model{

	@ManyToOne(cascade = CascadeType.ALL, optional = false)
	public RWatch rwatch;
	/**
	 * 定位器纬度，格式为DD.FFFFFF
	 */
	public double latitude;
	/**
	 * 定位器经度,格式为DDD.FFFFFF
	 */
	public double longitude;
	
	public double height;
	
	/**
	 * 该位置信息的时间，最大长度16（YYYYMMDDTHHMMSSZ），标准时间，例如20110712T054025Z
	 */
	public Date dateTime;
	
	/**
	 * 服务器收到坐标的时间
	 */
	public Date receivedTime;

	/**
	 * 国家码
	 */
	public int mcc;
	/**
	 * 移动网号码
	 */
	public int mnc;
	/**
	 * 位置区域码
	 */
	public int lac;

	@Override
	public String toString(){
		return "latitude = "+latitude+" longitude = "+longitude;
	}
	
}