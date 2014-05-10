package controllers;

import java.util.Date;
import java.util.UUID;

import models.CheckDigit;
import models.ClientVersion;
import models.Customer;
import models.Session;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import play.cache.Cache;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.db.Model;
import play.db.jpa.Blob;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http.Header;
import utils.Coder;
import utils.JSONUtil;
import controllers.CRUD.ObjectType;

/**
 * ......
 * 
 * @author hanzhao
 * 
 */
//@With(Compress.class)
public class Master extends Controller {

	public static final String SUCCESS = "1";
	public static final String FAIL = "0";
	
	public static final int ONE = 1;
	public static final int TWO = 2;
	public static final int THREE = 3;
	public static final int FOUR = 4;
	public static final int FIVE = 5;
	
	public static final int upgrade_flag = 1;// .......
	public static final int error_parameter_required = 1;// .......
	public static final int error_username_already_used = 2;// ........
	public static final int error_username_not_exist = 3;// .......
	public static final int error_userid_not_exist = 4;// ..id....
	public static final int error_not_owner = 5;// &{%s} .........
	public static final int error_unknown = 6;// ....,......
	public static final int error_locator_not_exist = 7;// .......
	public static final int error_both_email_phonenumber_empty = 8;// ..............
	public static final int error_username_or_password_not_match = 9;// ..........
	public static final int error_session_expired = 10;// .....,......
	public static final int error_mail_resetpassword = 11;// ..........,.........
															// &{%s} .
	public static final int error_locator_bind_full = 12;// ... &{%s} ..........
	public static final int error_locator_already_bind = 13;// ... &{%s} .......
	public static final int error_unknown_waring_format = 14;// .............
	public static final int error_unknown_command = 15;// ..... &{%s}.
	public static final int error_locator_not_confirmed = 16;// ..........,...........
	public static final int error_dateformat = 17;// .......
	public static final int error_locator_max = 18;// ......
	public static final int error_download = 19;// ....
	public static final int error_send_mail_fail = 20;
	public static final int error_already_exists = 21;// ........
	/**
	 * ...............
	 */
	private static ThreadLocal<Session> sessionCache = new ThreadLocal<Session>();
	
	/**
	 * ....
	 * 
	 * @param sessionID
	 */
	@Before(unless={"checkDigit", "register", "login", "sendResetPasswordMail", "update"},priority=1)
	public static void validateSessionID(@Required String z) {
		
		Session s = Session.find("bySessionID",z).first();
		sessionCache.set(s);
		if (s == null) {
			renderFail("error_session_expired");
		}
	}
	
//	public static void checkDigit(@Required String m) {
//		// ....
//		if (Validation.hasErrors()) {
//			renderFail("error_parameter_required");
//		}
//		if(!Validation.phone(SUCCESS, m).ok){
//			renderFail("error_parameter_required");
//		}
//		Random r = new Random();
//		int n = Math.abs(r.nextInt())/10000;
//		
//		try {
//			String s = SendSMS.send(m, "您的验证码是：" + n + "。请不要把验证码泄露给其他人。");
//			if(!"2".equals(s)){
//				play.Logger.error("checkDigit: result="+s+" PNumber="+m+" digit="+n);
//				if(!"OK".equals(s)){
//					s = "验证码获取失败请稍后再试";
//				}
//				renderText(s);
//			}
//			CheckDigit cd = new CheckDigit();
//			cd.d = n;
//			cd.updatetime = new Date().getTime();
//			cd.m = m;
//			cd._save();
//		} catch (Exception e) {
//			play.Logger.error("checkDigit: PNumber="+m+" digit="+n);
//			play.Logger.error(e.getMessage());
//			renderText("系统繁忙发送失败请再次获取");
//		}
//		renderText("OK");
//	}
	
	// 注册
	public static void register(@Required String z) {
		// ....
		if (Validation.hasErrors()) {
			renderFail("error_parameter_required");
		}

		try {			
			byte[] b = Coder.decryptBASE64(z);
			String src = new String(b);
			String[] arr = src.split("\\|");
		
			int i = Integer.parseInt(arr[7]);
			CheckDigit c = CheckDigit.find("d=?", i).first();
			if(c == null){
				renderFail("error_checkdigit");
			}
			if(!c.m.equals(arr[6])){
				renderFail("error_checkdigit");
			}
			if(new Date().getTime() - c.updatetime > 1800000){
				c.delete();
				renderFail("error_checkdigit");
			}
			c.delete();

			Customer m = Customer.find("byM_number", arr[6]).first();
			if(m != null){
				play.Logger.info("register:error_username_already_used");
				renderFail("error_username_already_used");
			}
			
			m = new Customer();
			m.cid = arr[1];
			m.mac = arr[2];
			m.imei = arr[3];
			m.imsi = arr[4];
			m.os = Integer.parseInt(arr[5]);
			m.m_number = arr[6];
			m.pwd = arr[8];
			m.updatetime = new Date();
			m.save();
			
			Session s = Session.find("byC", m).first();
			if(s == null)s = new Session();
			s.c = m;
			s.updatetime = new Date();
			s.sessionID = UUID.randomUUID().toString();
			s.save();
			
			JSONObject results = initResultJSON();
			results.put("uid", m.getId());
			results.put("phone", m.m_number);
			results.put("name", m.nickname);
			results.put("session", s.sessionID);
			play.Logger.info("register:OK "+m.m_number+" "+m.mac);
			renderSuccess(results);
		} catch (Exception e) {
			play.Logger.info("register:error "+e.getMessage());
			renderFail("error_unknown");
		}
		
	}
		
	/**
	 * Login
	 * 
	 * @param username
	 * @param password
	 * @param type
	 *            .....,......,iphone.......push..
	 * @param serialNumber
	 *            iphone.......,push..
	 */
	public static void login(@Required String phone,
			@Required String pwd, @Required Integer os,
			String serialNumber, String ip, String imei, String mac) {
		// ....
		if (Validation.hasErrors()) {
			renderFail("error_parameter_required");
		}

		if (os != null && os == 1 && (serialNumber == null || serialNumber.isEmpty())) {
			renderFail("error_parameter_required");
		}
		Customer c = Customer.find("byM_number", phone).first();
			
		if(c == null || !c.pwd.equals(pwd)){
			renderFail("error_username_or_password_not_match");
		}
		
		Session s = Session.find("byC", c).first();
		if(s == null){
			s = new Session();
			s.c = c;
			s.sessionID = UUID.randomUUID().toString();
			s.updatetime = new Date();
			s.save();
		}
		c.os = os;
		c.save();
		sessionCache.set(s);
		
		JSONObject results = initResultJSON();
		results.put("uid", c.getId());
		results.put("phone", c.m_number);
		results.put("name", c.nickname);
		results.put("session", s.sessionID);
		play.Logger.info("login:OK "+c.m_number+" "+c.mac);
		renderSuccess(results);
	}
	
	
	
	/**
	 * ....
	 * 
	 * @param username
	 * @param password
	 * @param sessionID
	 */
	public static void logout(@Required String z) {
		// ....
		if (Validation.hasErrors()) {
			renderFail("error_parameter_required");
		}
		Session s = sessionCache.get();
		if(s != null && s.id != 1 && s.id != 2){
			s.delete();
		}
		renderSuccess(initResultJSON());
	}

//	/**
//	 * .............
//	 * 
//	 * @param username
//	 * @param password
//	 * @param sessionID
//	 * @throws UnsupportedEncodingException
//	 */
//	@SuppressWarnings("deprecation")
//	public static void sendResetPasswordMail(@Required String userName)
//			throws UnsupportedEncodingException {
//		Document doc = initResultJSON();
//		// ....
//		if (Validation.hasErrors()) {
//			renderFail("error_parameter_required", doc,
//					error_parameter_required);
//		}
//
//		// .......
//		Member member = Member.find("byUsername", userName).first();
//		if (member == null) {
//			renderFail("error_username_not_exist", doc,
//					error_username_not_exist);
//		}
//
//		if(member.updateTime != null && (new Date().getDate() != member.updateTime.getDate())){
//			member.sendPasswordCount = 1;
//		}
//		// ..........10.
//		if(member.sendPasswordCount > 10){
//			renderFail("error_send_mail_fail",doc,error_send_mail_fail);
//		}
//
//		SendMail mail = new SendMail(
//				Play.configuration.getProperty("mail.smtp.host"),
//				Play.configuration.getProperty("mail.smtp.user"),
//				Play.configuration.getProperty("mail.smtp.pass"));
//
//		mail.setSubject(Messages.get("mail_resetpassword_title"));
//		mail.setBodyAsText(Messages.get("mail_resetpassword_content",
//				member.username, member.username, member.password,
//				DateUtil.toDate(new Date())));
//
//		// ..........
//		String nick = Messages.get("mail_show_name");
//		try {
//			nick = javax.mail.internet.MimeUtility.encodeText(nick);
//			mail.setFrom(new InternetAddress(nick + " <"
//					+ Play.configuration.getProperty("mail.smtp.from") + ">")
//					.toString());
//			mail.setTo(member.email);
//			mail.send();
//			member.sendPasswordCount++;
//			member.updateTime = new Date();
//			member.save();
//		} catch (Exception e) {
//			renderFail("error_mail_resetpassword", doc,
//					error_mail_resetpassword);
//		}
//		renderSuccess("mail_resetpassword_success", doc);
//	}
//

	//更新用户信息
	public static void updateMemberInfo(Integer os, String nickname, String pwd, String gender, String email,
			Blob portrait, @Required String z) {

		// ....
		if (Validation.hasErrors()) {
			renderFail("error_parameter_required");
		}

		Session s = sessionCache.get();
		Customer c = s.c;
		
		if(os != null){
			c.os = os;
		}
		if(nickname != null && !nickname.isEmpty()){
			c.nickname = nickname;
		}
		if(email != null && !email.isEmpty()){
			c.email = email;
		}
		if(pwd != null && !pwd.isEmpty()){
			c.pwd = pwd;
		}
		if(gender != null){
			c.gender = Byte.valueOf(gender);
		}
		if(portrait != null){
			if(c.portrait.exists()){
				c.portrait.getFile().delete();
			}
			c.portrait = portrait;
		}
		c.save();
		renderSuccess(initResultJSON());
	}

	// 得到用户信息
	public static void getMemberInfo(@Required String z) {
		
		if (Validation.hasErrors()) {
			renderFail("error_parameter_required");
		}
		
		Session s = sessionCache.get();
		if(s == null){
			renderFail("error_session_expired");
		}
		
		Customer c = s.c;
		JSONObject results = initResultJSON();
	
		results.put("phonenumber", c.m_number+"");
		results.put("nickname", c.nickname);
		results.put("email", c.email);
		results.put("os", c.os+"");
		results.put("gender", c.gender+"");
		
		if(c.portrait != null && c.portrait.exists()){
			results.put("portrait", "/c/download?id=" + c.id + "&fileID=portrait&entity=" + c.getClass().getName() + "&z=" + z);
		}else{
			if(c.gender == null || c.gender == 0){
				results.put("portrait", "/public/images/boy.jpg");
			}else{
				results.put("portrait", "/public/images/girl.jpg");
			}
		}
		renderSuccess(results);
	}

	public static void clearCache(@Required String z) {
		JSONObject results = initResultJSON();
		// ....
		if (Validation.hasErrors()) {
			renderFail("error_parameter_required");
		}

		Cache.clear();
		renderSuccess(results);

	}

	/**
	 * ......
	 * 
	 * @param username
	 *            ...
	 * @param password
	 *            ..
	 * @param sessionID
	 *            ..
	 * @param fileID
	 *            ..uuid
	 * @param fileName
	 *            ....
	 */
	public static void download(@Required String id, @Required String fileID, @Required String entity, @Required String z) {

		if (Validation.hasErrors()) {
			renderFail("error_parameter_required");
		}
		ObjectType type;
		try {
			type = new ObjectType(entity);
			notFoundIfNull(type);

			Model object = type.findById(id);
			notFoundIfNull(object);
			Object att = object.getClass().getField(fileID).get(object);
			if (att instanceof Model.BinaryField) {
				Model.BinaryField attachment = (Model.BinaryField) att;
				if (attachment == null || !attachment.exists()) {
					renderFail("error_download");
				}
				long p = 0;
				Header h = request.headers.get("Range");
				play.Logger.info("download header:", h);
				if(h != null){
					p = Long.parseLong(h.value().replaceAll("bytes=", "").replaceAll("-", ""));
				}
				play.Logger.info("download header:", p);
				response.contentType = attachment.type();
				if(p > 0){
					renderBinary(attachment.get(), attachment.get().skip(p));
				}else{
					renderBinary(attachment.get(), attachment.length());
				}
				
			}
		} catch (Exception e) {
			renderText("Download failed");
		}
		renderFail("error_download");
	}

	/**
	 * 版本校验
	 * 
	 * @param version
	 * @param type
	 * @throws JSONException 
	 */
	public static void update(@Required String version,
			@Required Integer p_id, @Required Integer m_id) {
		if (Validation.hasErrors()) {
			renderFail("error_parameter_required");
		}
		ClientVersion cv = ClientVersion.find("production_id = ? and mobiletype_id = ?", p_id, m_id).first();
		if (cv != null && !cv.version.equals(version)) {
			
			JSONObject results = initResultJSON();
			results.put("url", cv.url);
			results.put("update", cv.update_desc);
			renderSuccess(results);
		} else {
			renderFail("OK");
		}
	}


	protected static JSONObject initResultJSON() {
		return JSONUtil.getNewJSON();
	}
	
	protected static JSONArray initResultJSONArray() {
		return JSONUtil.getNewJSONArray();
	}


	protected static void renderSuccess(JSONObject results) {
		JSONObject jsonDoc = new JSONObject();
		jsonDoc.put("state", SUCCESS);
		jsonDoc.put("results",results);
		renderJSON(jsonDoc.toString());
	}

	protected static void renderFail(String key, Object... objects) {
		JSONObject jsonDoc = new JSONObject();
		jsonDoc.put("state", FAIL);
		jsonDoc.put("msg", Messages.get(key));
		renderJSON(jsonDoc.toString());
	}

}
