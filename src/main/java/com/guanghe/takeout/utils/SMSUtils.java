package com.guanghe.takeout.utils;


import com.guanghe.takeout.common.CustomException;
import com.guanghe.takeout.config.SmsConfig;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 短信发送工具类
 */
@Slf4j
public class SMSUtils {

	/**
	 * 发送短信
	 * @param phoneNumbers 手机号
	 * @param param 参数
	 */
	public static int sendMessage(SmsConfig smsConfig,String phoneNumbers,String param){
		try{
			// 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
			// 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
			Credential cred = new Credential(smsConfig.getSecretId(), smsConfig.getSecretKey());
			// 实例化一个http选项，可选的，没有特殊需求可以跳过
			HttpProfile httpProfile = new HttpProfile();
			httpProfile.setEndpoint("sms.tencentcloudapi.com");
			// 实例化一个client选项，可选的，没有特殊需求可以跳过
			ClientProfile clientProfile = new ClientProfile();
			clientProfile.setHttpProfile(httpProfile);
			// 实例化要请求产品的client对象,clientProfile是可选的
			SmsClient client = new SmsClient(cred, "ap-nanjing", clientProfile);
			// 实例化一个请求对象,每个接口都会对应一个request对象
			SendSmsRequest req = new SendSmsRequest();
			String[] phoneNumberSet1 = {phoneNumbers};
			req.setPhoneNumberSet(phoneNumberSet1);

			req.setSmsSdkAppId(smsConfig.getSmsSdkAppId());
			req.setSignName(smsConfig.getSignName());
			req.setTemplateId(smsConfig.getTemplateId());

			String[] templateParamSet1 = {param};
			req.setTemplateParamSet(templateParamSet1);

			// 返回的resp是一个SendSmsResponse的实例，与请求对象对应
			SendSmsResponse resp = client.SendSms(req);
			// 输出json格式的字符串回包
			String json = SendSmsResponse.toJsonString(resp);
			if (json.contains("\"Code\":\"Ok\"")) {
				log.info(json);
				return 1;
			} else {
				log.info(json);
				return 0;
			}
		} catch (TencentCloudSDKException e) {
			System.out.println(e.toString());
		}
		return 0;
	}


}
