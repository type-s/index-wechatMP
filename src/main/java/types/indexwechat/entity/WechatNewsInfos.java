package types.indexwechat.entity;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 设备配置信息表
 * @author Allen
 *
 */
@Entity
@Table(name = "INDEX_WECHAT_NEWS_INFOS")
@Data
public class WechatNewsInfos implements Serializable {
    private static final long serialVersionUID = 1L;
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Id
    @Column(name = "ID")
    private Integer id; //设备Id
    @Column(name = "FORUM",length = 100)
    private String forum; //所属板块
    @Column(name = "TITLE",length = 500)
    private String title; //文章标题
    @Column(name = "AUTHOR",length = 100)
    private String author; //文章作者
    @Column(name = "ARCTICLE_ID",length = 200)
    private String arcticleId; //微信文章Id
    @Column(name = "DIGEST",columnDefinition="text")
    private String digest; //图文消息的摘要，仅有单图文消息才有摘要，多图文此处为空
    @Column(name = "CONTENT",columnDefinition="text")
    private String content; //图文消息的具体内容，支持 HTML 标签，必须少于2万字符，小于1M，且此处会去除JS
    @Column(name = "CONTENT_SOURCE_URL",length = 2000)
    private String contentSourceUrl; //图文消息的原文地址，即点击“阅读原文”后的URL
    @Column(name = "THUMB_MEDIA_ID",length = 500)
    private String thumbMediaId; //图文消息的原文地址，即点击“阅读原文”后的URL
    @Column(name = "THUMB_URL",length = 2000)
    private String thumbUrl; //图文消息的原文地址，即点击“阅读原文”后的URL
    @Column(name = "URL",length = 2000)
    private String url; //图文消息的URL
    @Column(name = "UPDATE_TIME")
    private Date updateTime; //发布、更新时间
    @Column(name = "SYNC_TIME")
    private Date syncTime; //抓取时间
}
