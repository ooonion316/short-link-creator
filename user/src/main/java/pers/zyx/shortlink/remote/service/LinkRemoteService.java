package pers.zyx.shortlink.remote.service;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import pers.zyx.shortlink.remote.req.ShortLinkCreateReqDTO;
import pers.zyx.shortlink.remote.req.ShortLinkPageReqDTO;
import pers.zyx.shortlink.remote.resp.ShortLinkCreateRespDTO;
import pers.zyx.shortlink.remote.resp.ShortLinkPageRespDTO;
import pers.zyx.shortlink.result.Result;

import java.util.HashMap;
import java.util.Map;

/**
 * 链接中台调用, 后期使用 SpringCloud 代替
 */
public interface LinkRemoteService {

    default Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateReqDTO requestParam) {
        String resultBodyStr = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/create", JSON.toJSONString(requestParam));
        return JSON.parseObject(resultBodyStr, new TypeReference<>() {});
    }

    default Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gid", requestParam.getGid());
        requestMap.put("current", requestParam.getCurrent());
        requestMap.put("size", requestParam.getSize());
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/page", requestMap);
        return JSON.parseObject(resultPageStr, new TypeReference<>() {});
    }
}
