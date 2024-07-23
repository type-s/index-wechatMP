package types.indexwechat.service;

import types.indexwechat.dao.WechatNewsInfosDao;
import types.indexwechat.entity.WechatNewsInfos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class NewsService {

    @Resource
    WechatNewsInfosDao wechatNewsInfosDao;
    @Resource
    types.indexwechat.service.WechatInfoService wechatInfoService;

    /**
     * 获取新闻列表
     * @param newsType 新闻类型
     * @param start 开始条数
     * @param length 获取长度
     * @return
     */
    public Page<WechatNewsInfos> getNewsList(String newsType, Integer start, Integer length) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updateTime"); //创建时间降序排序
        Pageable pageable = PageRequest.of(start.intValue(),length.intValue(),sort);
        Page<WechatNewsInfos> wechatNewsInfosPage = wechatNewsInfosDao.findByForum(newsType,pageable);
        return wechatNewsInfosPage;
    }

    /**
     * 同步新闻信息
     */
    public void syncNewsInfo(Integer start, Integer count, Integer content) {
        Map<String,Object> wechatNews = wechatInfoService.getWechatNewsList(start, count, content);
        if((Double) wechatNews.get("total_count") != 0
        && (Double) wechatNews.get("item_count") != 0) {
            List<Map<String,Object>> itemList = (List<Map<String, Object>>) wechatNews.get("item");
            for(Map<String,Object> itemMap : itemList) {
                String arcticleId = itemMap.get("article_id").toString();
                List<WechatNewsInfos> wechatNewsInfosList = wechatNewsInfosDao.findByArcticleId(arcticleId);
                WechatNewsInfos wechatNewsInfos = new WechatNewsInfos();
                if(wechatNewsInfosList.size() > 0) { //更新
                    wechatNewsInfos = wechatNewsInfosList.get(0);
                }
                Map<String,Object> contentMap = (Map<String, Object>) itemMap.get("content");
                Long updateTime = ((Double) contentMap.get("create_time")).longValue();
                List<Map<String,Object>> newsItemList = (List<Map<String, Object>>) contentMap.get("news_item");
                for (Map<String,Object> newsItem : newsItemList) {
                    wechatNewsInfos.setForum(newsItem.get("author").toString()); //板块和运营商量使用作者代替
                    wechatNewsInfos.setArcticleId(arcticleId);
                    wechatNewsInfos.setTitle(newsItem.get("title").toString());
                    wechatNewsInfos.setAuthor(newsItem.get("author").toString());
                    wechatNewsInfos.setDigest(newsItem.get("digest").toString());
                    wechatNewsInfos.setContentSourceUrl(newsItem.get("content_source_url").toString());
                    wechatNewsInfos.setThumbMediaId(newsItem.get("thumb_media_id").toString());
                    wechatNewsInfos.setThumbUrl(newsItem.get("thumb_url").toString());
                    wechatNewsInfos.setUrl(newsItem.get("url").toString());
                    wechatNewsInfos.setUpdateTime(new Date(updateTime*1000));
                    wechatNewsInfos.setSyncTime(new Date()); //更新时间
                }
                wechatNewsInfosDao.save(wechatNewsInfos);
            }
            if( ((Double) wechatNews.get("total_count")).longValue() == count) { //递归
                syncNewsInfo(start + count, count, content);
            }
        }
    }

}
