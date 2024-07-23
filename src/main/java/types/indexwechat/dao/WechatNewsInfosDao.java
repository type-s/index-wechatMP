package types.indexwechat.dao;

import types.indexwechat.entity.WechatNewsInfos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface WechatNewsInfosDao extends JpaRepository<WechatNewsInfos, Long> {

    List<WechatNewsInfos> findByArcticleId(String arcticleId);
    Page<WechatNewsInfos> findByForum(String forum, Pageable pageable);

}
