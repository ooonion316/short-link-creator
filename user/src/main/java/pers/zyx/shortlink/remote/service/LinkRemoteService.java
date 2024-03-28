package pers.zyx.shortlink.remote.service;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import pers.zyx.shortlink.remote.req.ShortLinkCreateReqDTO;
import pers.zyx.shortlink.remote.resp.ShortLinkCreateRespDTO;
import pers.zyx.shortlink.result.Result;

/**
 * 链接中台调用, 后期使用 SpringCloud 代替
 */
public interface LinkRemoteService {

    default Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateReqDTO requestParam) {
        String resultBodyStr = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/create", JSON.toJSONString(requestParam));
        return JSON.parseObject(resultBodyStr, new TypeReference<>() {});
    }
}
