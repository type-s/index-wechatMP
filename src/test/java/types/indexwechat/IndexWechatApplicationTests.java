package types.indexwechat;

import types.indexwechat.dao.WechatNewsInfosDao;
import types.indexwechat.service.NewsService;
import types.indexwechat.service.WechatInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class IndexWechatApplicationTests {

    @Resource
    NewsService newsService;
    @Resource
    WechatNewsInfosDao wechatNewsInfosDao;
    @Resource
    WechatInfoService wechatInfoService;

    @Test
    void contextLoads() {
        //newsService.getNewsList("",1,10);
        //newsService.syncNewsInfo(0,10,1);
        //System.out.println(newsService.getNewsList("",1,1));
        System.out.println(wechatInfoService.getWechatNewsList(0,10,1));
    }

}
