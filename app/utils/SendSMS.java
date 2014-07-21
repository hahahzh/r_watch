package utils;

import play.Play;

import com.wondertek.esmp.esms.empp.EMPPConnectResp;
import com.wondertek.esmp.esms.empp.EmppApi;

public class SendSMS {

	public static int[] sendMsg(String[] phoneNos, String content) throws Exception {

		String host = Play.configuration.getProperty("sms.host");
		String accountId = Play.configuration.getProperty("sms.accountId");
		String password = Play.configuration.getProperty("sms.password");
		String serviceId = Play.configuration.getProperty("sms.serviceId");
		
//		String host = "211.136.163.68";
//		String accountId = "10657109030507";
//		String password = "Cmcc10086";
//		String serviceId = "2171088551";

		EmppApi emppApi = new EmppApi();
		RecvListener listener = RecvListener.getInstance(emppApi);

		EMPPConnectResp response = emppApi.connect(host, 9981, accountId, password, listener);
		if (response == null || !emppApi.isConnected() || !emppApi.isSubmitable()) {
			return null;
		}
        String s1 = UTF2GBK.GBK2Unicode(content);
        String s2 = UTF2GBK.unicodeToUtf8(content);
        
		return emppApi.submitMsgAsync(s1+" 正文："+s2, phoneNos, serviceId);
	}
	
	public static void main(String[] args) {
		try {
			String str = "这是一个测试ddd六七八九十";

			String s0 = UTF2GBK.GBK2Unicode("测试");
            String s1 = UTF2GBK.GBK2Unicode(str);
            String s2 = UTF2GBK.unicodeToUtf8(str);

			SendSMS.sendMsg(new String[]{"15000993473"}, s0+s2);
			
//			String tt = IOUtils.toString(IOUtils.toInputStream(utf8, "GBK"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    
	}
}