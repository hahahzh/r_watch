package controllers;


import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import controllers.CRUD.ObjectType;
import models.Customer;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.With;
import utils.ExportExcel;

@Check("admin")
@With(Secure.class)
public class Customers extends CRUD {
	
    public static void exportAllData(String startDate, String endDate, String search) {
    	
    	ObjectType type = ObjectType.get(getControllerClass());
    	List<Model> objects = type.findPage(1, search, null, null, null, null);
		String[] title = {"手机号","昵称","性别","email","操作系统","IMEI","IMSI","MAC","城市","出生日期","身高","体重"};
		
		List<String[]> datas = new ArrayList<String[]>();
		for(Model l:objects){
			Customer c = (Customer)l;
			String[] data = new String[12];
			data[0] = c.m_number;
			data[1] = c.nickname;
			if(c.gender==null){
				data[2] = "女";
			}else if(0==c.gender){
				data[2] = "男";
			}else if(1==c.gender){
				data[2] = "女";
			}
 			data[3] = c.email;
 			if(1==c.os){
				data[4] = "Android";
			}else if(2==c.os){
				data[4] = "IOS";
			}
 			data[5] = c.imei;
 			data[6] = c.imsi;
 			data[7] = c.mac;
 			data[8] = c.city;
 			data[9] = c.birthday+"";
 			data[10] = c.height;
 			data[11] = c.weight;
			datas.add(data);
		}
		OutputStream os = response.out;// 取得输出流
        response.reset();// 清空输出流
        try {
			response.setHeader("Content-disposition", "attachment; filename=" + new String("Customers".getBytes("GB2312"), "iso8859_1") + ".xls");
			response.setContentTypeIfNotSet("application/msexcel");// 定义输出类型
			ExportExcel.createExcel(os,"Customers",title,datas);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}