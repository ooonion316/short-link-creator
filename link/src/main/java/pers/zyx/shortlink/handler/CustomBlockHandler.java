package pers.zyx.shortlink.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import pers.zyx.shortlink.dto.req.ShortLinkCreateReqDTO;
import pers.zyx.shortlink.dto.resp.ShortLinkCreateRespDTO;
import pers.zyx.shortlink.result.Result;

/**
 * 自定义流控策略
 */
public class CustomBlockHandler {
    public static Result<ShortLinkCreateRespDTO> createShortLinkBlockHandlerMethod(ShortLinkCreateReqDTO requestParam, BlockException ex) {
        return new Result<ShortLinkCreateRespDTO>().setCode("B100000").setMessage("当前访问人数过多，请稍后再试");
    }
}