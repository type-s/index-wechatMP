package types.indexwechat.service;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 微信信息获取
 */
@Service
public class WechatInfoService {

    @Resource
    private Environment env;
    @Resource
    private Gson gson;

    private static final Map<String, Object> wxAuthToken = new HashMap<String, Object>(); //access_token - 获取到的票据 expires_in - 有效时间（时间戳，已经加上当前时间）

    /**
     * 获取微信新闻列表数据
     * @param start 从全部素材的该偏移位置开始返回，0表示从第一个素材返回
     * @param count 返回素材的数量，取值在1到20之间
     * @param content 1 表示不返回 content 字段，0 表示正常返回，默认为 0
     * @return
     */
    public Map<String,Object> getWechatNewsList(Integer start, Integer count, Integer content) {
        Map<String,Object> resultMap = new HashMap<>();
        String url = "https://api.weixin.qq.com/cgi-bin/freepublish/batchget?access_token=" + getAccessToken();
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> httpMessageConverters = restTemplate.getMessageConverters();
        httpMessageConverters.stream().forEach(httpMessageConverter -> {
                    if (httpMessageConverter instanceof StringHttpMessageConverter) {
                        StringHttpMessageConverter messageConverter = (StringHttpMessageConverter) httpMessageConverter;
                        messageConverter.setDefaultCharset(Charset.forName("UTF-8"));
                    }
                }
        );
        HttpHeaders httpHandlers = new HttpHeaders();
        httpHandlers.setContentType(MediaType.APPLICATION_JSON); //设定媒体类型
        Map<String,Integer> body = new HashMap<String,Integer>();
        body.put("offset", start);
        body.put("count", count);
        body.put("no_content", content);
        String bodyData = gson.toJson(body);
        HttpEntity httpEntity = new HttpEntity(bodyData,httpHandlers);
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        if(exchange!=null){
            //System.out.println(exchange.getBody());
            resultMap = gson.fromJson(exchange.getBody(), Map.class);
        }
        return resultMap;
    }

    /**
     * 获取jsapi_ticket
     * @return
     */
    public String getAccessToken() {
        if(wxAuthToken.get("expires_in") != null && (Double)wxAuthToken.get("expires_in") > new Date().getTime()
                && wxAuthToken.get("access_token") != null) {
            return wxAuthToken.get("access_token").toString();
        }
        String corpid = env.getProperty("wechat.corpid");
        String corpsecret = env.getProperty("wechat.corpsecret");
        String accType = env.getProperty("wechat.type");
        //服务号 sub
        String accessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token";
        String accessTokenParam = "grant_type=client_credential&appid=" + corpid + "&secret=" + corpsecret;
        String jsapiTicketUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";
        if("co".equals(accType)) { //企业号co
            accessTokenUrl = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
            accessTokenParam = "corpid=" + corpid + "&corpsecret=" + corpsecret;
            jsapiTicketUrl = "https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket";
        }
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> exchange = restTemplate.exchange(accessTokenUrl + "?" + accessTokenParam,
                HttpMethod.GET, new HttpEntity(null,null), String.class);
        String accessTokenResult = exchange.getBody();
        Map<String, Object> accessTokenMap = gson.fromJson(accessTokenResult, Map.class);
        if(accessTokenMap.containsKey("errcode") && accessTokenMap.get("errcode") instanceof Double && (Double)accessTokenMap.get("errcode") != 0) {
            System.out.println("access_token获取调用失败："+accessTokenResult);
            return null;
        }
        String access_token = accessTokenMap.get("access_token").toString();
        wxAuthToken.clear(); //清空
        wxAuthToken.put("access_token", access_token); //票据
        wxAuthToken.put("expires_in",  1.8*3600*1000 + new Date().getTime() ); //有效时间，时间戳
        return access_token;
    }

}
