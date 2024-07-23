package types.indexwechat.controller;

import types.indexwechat.entity.WechatNewsInfos;
import types.indexwechat.service.NewsService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 页面请求控制器
 */
@Controller
public class PageController {

    @Resource
    NewsService newsService;

    /**
     * 获取新闻列表
     * @param newsType 新闻类型
     * @param start 开始条数
     * @param length 获取长度
     * @return
     * total - 总数
     * data - 数据
     */
    @ResponseBody
    @RequestMapping(value = "getNewsList", produces = "application/json; charset=utf-8")
    public Map<String,Object> getNewsList(@RequestParam String newsType,
                                          @RequestParam Integer start, @RequestParam Integer length) {
        Page<WechatNewsInfos> wechatNewsInfosPage = newsService.getNewsList(newsType, start-1, length);
        Map<String,Object> result = new HashMap<>();
        result.put("total",wechatNewsInfosPage.getTotalElements());
        result.put("data",wechatNewsInfosPage.getContent());
        return result;
    }

    /**
     * 手动同步
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "sync", produces = "application/json; charset=utf-8")
    public String sync() {
        newsService.syncNewsInfo(0,15,1);
        return "success";
    }



}
