package controllers;


import java.lang.reflect.Constructor;
import java.util.Date;

import models.SMSLog;
import play.exceptions.TemplateNotFoundException;
import play.mvc.With;
import utils.DateUtil;
import utils.SendSMS;

@Check("admin")
@With(Secure.class)
public class SMSLogs extends CRUD {
	
	public static void create() throws Exception {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        SMSLog object = new SMSLog();
        object.content = params.get("content");
        String mobiles = "";
        for(String tmp : params.getAll("selMobile")){
        	mobiles += tmp+",";
        }
        object.phoneNos = mobiles;
        validation.valid(object);
        
        int[] flags = SendSMS.sendMsg(params.getAll("selMobile"), object.content);
        
        object.updatetime = DateUtil.reverseDate(new Date(), 0);
        if (validation.hasErrors()) {
            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/blank.html", type, object);
            } catch (TemplateNotFoundException e) {
                render("CRUD/blank.html", type, object);
            }
        }
        object._save();
        flash.success(play.i18n.Messages.get("crud.created", type.modelName));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        if (params.get("_saveAndAddAnother") != null) {
            redirect(request.controller + ".blank");
        }
        redirect(request.controller + ".show", object._key());
    }
	
}