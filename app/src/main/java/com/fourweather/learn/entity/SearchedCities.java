package com.fourweather.learn.entity;

import java.util.List;

/**
 * Create on 2019/01/23
 *
 * @author Four
 * @description 模糊搜索返回的json
 */
public class SearchedCities {


    private List<HeWeather6Bean> HeWeather6;

    public List<HeWeather6Bean> getHeWeather6() {
        return HeWeather6;
    }

    public void setHeWeather6(List<HeWeather6Bean> HeWeather6) {
        this.HeWeather6 = HeWeather6;
    }

    public static class HeWeather6Bean {
        /**
         * basic : [{"cid":"CN101180701","location":"南阳","parent_city":"南阳","admin_area":"河南","cnty":"中国","lat":"32.99908066","lon":"112.54091644","tz":"+8.00","type":"city"},{"cid":"CN101190101","location":"南京","parent_city":"南京","admin_area":"江苏","cnty":"中国","lat":"32.04154587","lon":"118.76741028","tz":"+8.00","type":"city"},{"cid":"CN101120101","location":"济南","parent_city":"济南","admin_area":"山东","cnty":"中国","lat":"36.67580795","lon":"117.00092316","tz":"+8.00","type":"city"},{"cid":"CN101300101","location":"南宁","parent_city":"南宁","admin_area":"广西","cnty":"中国","lat":"22.82402039","lon":"108.32000732","tz":"+8.00","type":"city"},{"cid":"CN101190501","location":"南通","parent_city":"南通","admin_area":"江苏","cnty":"中国","lat":"32.01621246","lon":"120.86460876","tz":"+8.00","type":"city"},{"cid":"CN101240101","location":"南昌","parent_city":"南昌","admin_area":"江西","cnty":"中国","lat":"28.67649269","lon":"115.89215088","tz":"+8.00","type":"city"},{"cid":"CN101180702","location":"南召","parent_city":"南阳","admin_area":"河南","cnty":"中国","lat":"33.48861694","lon":"112.43558502","tz":"+8.00","type":"city"},{"cid":"CN101220401","location":"淮南","parent_city":"淮南","admin_area":"安徽","cnty":"中国","lat":"32.64757538","lon":"117.01832581","tz":"+8.00","type":"city"},{"cid":"CN101110501","location":"渭南","parent_city":"渭南","admin_area":"陕西","cnty":"中国","lat":"34.49938202","lon":"109.50288391","tz":"+8.00","type":"city"}]
         * status : ok
         */

        private String status;
        private List<BasicBean> basic;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<BasicBean> getBasic() {
            return basic;
        }

        public void setBasic(List<BasicBean> basic) {
            this.basic = basic;
        }

        public static class BasicBean {

            /**
             * cid : CN101180701
             * location : 南阳
             * parent_city : 南阳
             * admin_area : 河南
             * cnty : 中国
             * lat : 32.99908066
             * lon : 112.54091644
             * tz : +8.00
             * type : city
             */

            private String cid;
            private String location;
            private String admin_area;
            private String cnty;

            public String getCid() {
                return cid;
            }

            public void setCid(String cid) {
                this.cid = cid;
            }

            public String getLocation() {
                return location;
            }

            public void setLocation(String location) {
                this.location = location;
            }

            public String getAdmin_area() {
                return admin_area;
            }

            public void setAdmin_area(String admin_area) {
                this.admin_area = admin_area;
            }

            public String getCnty() {
                return cnty;
            }

            public void setCnty(String cnty) {
                this.cnty = cnty;
            }
        }
    }
}
