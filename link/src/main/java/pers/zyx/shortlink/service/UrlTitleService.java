package pers.zyx.shortlink.service;

public interface UrlTitleService {
    /**
     * 根据url获取网站标题
     *
     * @param url 网址
     * @return 网站标题
     */
    String getTitleByUrl(String url);
}