package com.fourweather.learn.entity;

import java.util.List;

/**
 * Create on 2019/01/17
 *
 * @author Four
 * @description 获取必应每日一图的URL
 */
public class BiYing {
    /**
     * images : [{"startdate":"20190116","fullstartdate":"201901161600","enddate":"20190117","url":"/az/hprichbg/rb/UKSomerset_ZH-CN2587621995_1920x1080.jpg","urlbase":"/az/hprichbg/rb/UKSomerset_ZH-CN2587621995","copyright":"英格兰，萨默塞特 (© Guy Edwardes/Minden Pictures)","copyrightlink":"http://www.bing.com/search?q=%E8%8B%B1%E6%A0%BC%E5%85%B0%E8%90%A8%E9%BB%98%E5%A1%9E%E7%89%B9&form=hpcapt&mkt=zh-cn","title":"","quiz":"/search?q=Bing+homepage+quiz&filters=WQOskey:%22HPQuiz_20190116_UKSomerset%22&FORM=HPQUIZ","wp":false,"hsh":"cb69af32903bd4a7e77505504df90df9","drk":1,"top":1,"bot":1,"hs":[]}]
     * tooltips : {"loading":"正在加载...","previous":"上一个图像","next":"下一个图像","walle":"此图片不能下载用作壁纸。","walls":"下载今日美图。仅限用作桌面壁纸。"}
     */

    private List<ImagesBean> images;

    public List<ImagesBean> getImages() {
        return images;
    }

    public void setImages(List<ImagesBean> images) {
        this.images = images;
    }

    public static class ImagesBean {
        /**
         * startdate : 20190116
         * fullstartdate : 201901161600
         * enddate : 20190117
         * url : /az/hprichbg/rb/UKSomerset_ZH-CN2587621995_1920x1080.jpg
         * urlbase : /az/hprichbg/rb/UKSomerset_ZH-CN2587621995
         * copyright : 英格兰，萨默塞特 (© Guy Edwardes/Minden Pictures)
         * copyrightlink : http://www.bing.com/search?q=%E8%8B%B1%E6%A0%BC%E5%85%B0%E8%90%A8%E9%BB%98%E5%A1%9E%E7%89%B9&form=hpcapt&mkt=zh-cn
         * title :
         * quiz : /search?q=Bing+homepage+quiz&filters=WQOskey:%22HPQuiz_20190116_UKSomerset%22&FORM=HPQUIZ
         * wp : false
         * hsh : cb69af32903bd4a7e77505504df90df9
         * drk : 1
         * top : 1
         * bot : 1
         * hs : []
         */

        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

}