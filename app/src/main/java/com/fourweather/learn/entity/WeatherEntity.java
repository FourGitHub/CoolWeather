package com.fourweather.learn.entity;

import org.litepal.crud.LitePalSupport;

import java.util.List;

/**
 * Create on 2019/01/25
 *
 * @author Four
 * @description 某个城市的天气信息json
 */
public class WeatherEntity extends LitePalSupport {

    private List<HeWeather6Bean> HeWeather6;

    public List<HeWeather6Bean> getHeWeather6() {
        return HeWeather6;
    }

    public void setHeWeather6(List<HeWeather6Bean> HeWeather6) {
        this.HeWeather6 = HeWeather6;
    }

    public static class HeWeather6Bean {
        /**
         * basic : {"cid":"CN101040100","location":"重庆","parent_city":"重庆","admin_area":"重庆","cnty":"中国","lat":"29.56376076","lon":"106.55046082","tz":"+8.00"}
         * update : {"loc":"2019-01-25 11:57","utc":"2019-01-25 03:57"}
         * status : ok
         * now : {"cloud":"100","cond_code":"104","cond_txt":"阴","fl":"9","hum":"73","pcpn":"0.0","pres":"1024","tmp":"10","vis":"10","wind_deg":"135","wind_dir":"东南风","wind_sc":"1","wind_spd":"2"}
         * daily_forecast : [{"cond_code_d":"104","cond_code_n":"305","cond_txt_d":"阴","cond_txt_n":"小雨","date":"2019-01-25","hum":"48","mr":"23:03","ms":"10:51","pcpn":"0.0","pop":"22","pres":"1026","sr":"07:46","ss":"18:26","tmp_max":"11","tmp_min":"9","uv_index":"1","vis":"20","wind_deg":"-1","wind_dir":"无持续风向","wind_sc":"1-2","wind_spd":"7"},{"cond_code_d":"104","cond_code_n":"104","cond_txt_d":"阴","cond_txt_n":"阴","date":"2019-01-26","hum":"56","mr":"00:00","ms":"11:30","pcpn":"1.0","pop":"55","pres":"1026","sr":"07:46","ss":"18:27","tmp_max":"10","tmp_min":"8","uv_index":"1","vis":"20","wind_deg":"-1","wind_dir":"无持续风向","wind_sc":"1-2","wind_spd":"4"},{"cond_code_d":"104","cond_code_n":"104","cond_txt_d":"阴","cond_txt_n":"阴","date":"2019-01-27","hum":"53","mr":"00:05","ms":"12:07","pcpn":"1.0","pop":"55","pres":"1021","sr":"07:45","ss":"18:28","tmp_max":"10","tmp_min":"8","uv_index":"1","vis":"20","wind_deg":"-1","wind_dir":"无持续风向","wind_sc":"1-2","wind_spd":"6"},{"cond_code_d":"305","cond_code_n":"305","cond_txt_d":"小雨","cond_txt_n":"小雨","date":"2019-01-28","hum":"72","mr":"01:06","ms":"12:44","pcpn":"0.0","pop":"25","pres":"1022","sr":"07:45","ss":"18:29","tmp_max":"11","tmp_min":"8","uv_index":"1","vis":"19","wind_deg":"-1","wind_dir":"无持续风向","wind_sc":"1-2","wind_spd":"11"},{"cond_code_d":"305","cond_code_n":"305","cond_txt_d":"小雨","cond_txt_n":"小雨","date":"2019-01-29","hum":"85","mr":"02:05","ms":"13:22","pcpn":"1.0","pop":"55","pres":"1021","sr":"07:44","ss":"18:29","tmp_max":"11","tmp_min":"8","uv_index":"1","vis":"16","wind_deg":"-1","wind_dir":"无持续风向","wind_sc":"1-2","wind_spd":"9"},{"cond_code_d":"104","cond_code_n":"104","cond_txt_d":"阴","cond_txt_n":"阴","date":"2019-01-30","hum":"90","mr":"03:02","ms":"14:02","pcpn":"0.0","pop":"25","pres":"1019","sr":"07:44","ss":"18:30","tmp_max":"11","tmp_min":"9","uv_index":"1","vis":"15","wind_deg":"-1","wind_dir":"无持续风向","wind_sc":"1-2","wind_spd":"11"},{"cond_code_d":"104","cond_code_n":"104","cond_txt_d":"阴","cond_txt_n":"阴","date":"2019-01-31","hum":"89","mr":"03:58","ms":"14:45","pcpn":"1.0","pop":"55","pres":"1024","sr":"07:43","ss":"18:31","tmp_max":"10","tmp_min":"7","uv_index":"1","vis":"16","wind_deg":"-1","wind_dir":"无持续风向","wind_sc":"1-2","wind_spd":"8"}]
         * hourly : [{"cloud":"98","cond_code":"101","cond_txt":"多云","dew":"6","hum":"60","pop":"6","pres":"1023","time":"2019-01-25 13:00","tmp":"10","wind_deg":"223","wind_dir":"西南风","wind_sc":"1-2","wind_spd":"6"},{"cloud":"94","cond_code":"100","cond_txt":"晴","dew":"5","hum":"52","pop":"6","pres":"1021","time":"2019-01-25 16:00","tmp":"10","wind_deg":"110","wind_dir":"东南风","wind_sc":"1-2","wind_spd":"10"},{"cloud":"93","cond_code":"101","cond_txt":"多云","dew":"6","hum":"55","pop":"7","pres":"1023","time":"2019-01-25 19:00","tmp":"10","wind_deg":"80","wind_dir":"东风","wind_sc":"1-2","wind_spd":"3"},{"cloud":"97","cond_code":"101","cond_txt":"多云","dew":"6","hum":"74","pop":"7","pres":"1024","time":"2019-01-25 22:00","tmp":"9","wind_deg":"95","wind_dir":"东风","wind_sc":"1-2","wind_spd":"7"},{"cloud":"99","cond_code":"104","cond_txt":"阴","dew":"6","hum":"86","pop":"51","pres":"1024","time":"2019-01-26 01:00","tmp":"9","wind_deg":"88","wind_dir":"东风","wind_sc":"1-2","wind_spd":"8"},{"cloud":"99","cond_code":"305","cond_txt":"小雨","dew":"7","hum":"90","pop":"55","pres":"1023","time":"2019-01-26 04:00","tmp":"9","wind_deg":"100","wind_dir":"东风","wind_sc":"1-2","wind_spd":"10"},{"cloud":"100","cond_code":"305","cond_txt":"小雨","dew":"6","hum":"91","pop":"17","pres":"1024","time":"2019-01-26 07:00","tmp":"9","wind_deg":"165","wind_dir":"东南风","wind_sc":"1-2","wind_spd":"8"},{"cloud":"100","cond_code":"104","cond_txt":"阴","dew":"7","hum":"88","pop":"17","pres":"1026","time":"2019-01-26 10:00","tmp":"9","wind_deg":"129","wind_dir":"东南风","wind_sc":"1-2","wind_spd":"4"}]
         * lifestyle : [{"type":"comf","brf":"较舒适","txt":"白天天气晴好，早晚会感觉偏凉，午后舒适、宜人。"},{"type":"drsg","brf":"较冷","txt":"建议着厚外套加毛衣等服装。年老体弱者宜着大衣、呢外套加羊毛衫。"},{"type":"flu","brf":"较易发","txt":"天气转凉，空气湿度较大，较易发生感冒，体质较弱的朋友请注意适当防护。"},{"type":"sport","brf":"较适宜","txt":"阴天，较适宜进行各种户内外运动。"},{"type":"trav","brf":"适宜","txt":"天气较好，温度适宜，总体来说还是好天气哦，这样的天气适宜旅游，您可以尽情地享受大自然的风光。"},{"type":"uv","brf":"最弱","txt":"属弱紫外线辐射天气，无需特别防护。若长期在户外，建议涂擦SPF在8-12之间的防晒护肤品。"},{"type":"cw","brf":"不宜","txt":"不宜洗车，未来24小时内有雨，如果在此期间洗车，雨水和路上的泥水可能会再次弄脏您的爱车。"},{"type":"air","brf":"较差","txt":"气象条件较不利于空气污染物稀释、扩散和清除，请适当减少室外活动时间。"}]
         */

        private BasicBean basic;
        private UpdateBean update;
        private String status;
        private NowBean now;
        private List<DailyForecastBean> daily_forecast;
        private List<HourlyBean> hourly;
        private List<LifestyleBean> lifestyle;

        public BasicBean getBasic() {
            return basic;
        }

        public void setBasic(BasicBean basic) {
            this.basic = basic;
        }

        public UpdateBean getUpdate() {
            return update;
        }

        public void setUpdate(UpdateBean update) {
            this.update = update;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public NowBean getNow() {
            return now;
        }

        public void setNow(NowBean now) {
            this.now = now;
        }

        public List<DailyForecastBean> getDaily_forecast() {
            return daily_forecast;
        }

        public void setDaily_forecast(List<DailyForecastBean> daily_forecast) {
            this.daily_forecast = daily_forecast;
        }

        public List<HourlyBean> getHourly() {
            return hourly;
        }

        public void setHourly(List<HourlyBean> hourly) {
            this.hourly = hourly;
        }

        public List<LifestyleBean> getLifestyle() {
            return lifestyle;
        }

        public void setLifestyle(List<LifestyleBean> lifestyle) {
            this.lifestyle = lifestyle;
        }

        public static class BasicBean {
            /**
             * cid : CN101040100
             * location : 重庆
             * parent_city : 重庆
             * admin_area : 重庆
             * cnty : 中国
             * lat : 29.56376076
             * lon : 106.55046082
             * tz : +8.00
             */

            private String cid;
            private String location;

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
        }

        public static class UpdateBean {
            /**
             * loc : 2019-01-25 11:57
             * utc : 2019-01-25 03:57
             */

            private String loc;

            public String getLoc() {
                return loc;
            }

            public void setLoc(String loc) {
                this.loc = loc;
            }
        }

        public static class NowBean {
            /**
             * cloud : 100
             * cond_code : 104
             * cond_txt : 阴
             * fl : 9
             * hum : 73
             * pcpn : 0.0
             * pres : 1024
             * tmp : 10
             * vis : 10
             * wind_deg : 135
             * wind_dir : 东南风
             * wind_sc : 1
             * wind_spd : 2
             */

            private String cond_code;
            private String cond_txt;
            private String fl;
            private String hum;
            private String pcpn;
            private String pres;
            private String tmp;

            public String getCond_code() {
                return cond_code;
            }

            public void setCond_code(String cond_code) {
                this.cond_code = cond_code;
            }

            public String getCond_txt() {
                return cond_txt;
            }

            public void setCond_txt(String cond_txt) {
                this.cond_txt = cond_txt;
            }

            public String getFl() {
                return fl;
            }

            public void setFl(String fl) {
                this.fl = fl;
            }

            public String getHum() {
                return hum;
            }

            public void setHum(String hum) {
                this.hum = hum;
            }

            public String getPcpn() {
                return pcpn;
            }

            public void setPcpn(String pcpn) {
                this.pcpn = pcpn;
            }

            public String getPres() {
                return pres;
            }

            public void setPres(String pres) {
                this.pres = pres;
            }

            public String getTmp() {
                return tmp;
            }

            public void setTmp(String tmp) {
                this.tmp = tmp;
            }
        }

        public static class DailyForecastBean {
            /**
             * cond_code_d : 104
             * cond_code_n : 305
             * cond_txt_d : 阴
             * cond_txt_n : 小雨
             * date : 2019-01-25
             * hum : 48
             * mr : 23:03
             * ms : 10:51
             * pcpn : 0.0
             * pop : 22
             * pres : 1026
             * sr : 07:46
             * ss : 18:26
             * tmp_max : 11
             * tmp_min : 9
             * uv_index : 1
             * vis : 20
             * wind_deg : -1
             * wind_dir : 无持续风向
             * wind_sc : 1-2
             * wind_spd : 7
             */

            private String cond_code_d;
            private String cond_code_n;
            private String cond_txt_d;
            private String cond_txt_n;
            private String date;
            private String hum;
            private String mr;
            private String ms;
            private String pcpn;
            private String pop;
            private String pres;
            private String sr;
            private String ss;
            private String tmp_max;
            private String tmp_min;
            private String uv_index;

            public String getCond_code_d() {
                return cond_code_d;
            }

            public void setCond_code_d(String cond_code_d) {
                this.cond_code_d = cond_code_d;
            }

            public String getCond_code_n() {
                return cond_code_n;
            }

            public void setCond_code_n(String cond_code_n) {
                this.cond_code_n = cond_code_n;
            }

            public String getCond_txt_d() {
                return cond_txt_d;
            }

            public void setCond_txt_d(String cond_txt_d) {
                this.cond_txt_d = cond_txt_d;
            }

            public String getCond_txt_n() {
                return cond_txt_n;
            }

            public void setCond_txt_n(String cond_txt_n) {
                this.cond_txt_n = cond_txt_n;
            }

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getHum() {
                return hum;
            }

            public void setHum(String hum) {
                this.hum = hum;
            }

            public String getMr() {
                return mr;
            }

            public void setMr(String mr) {
                this.mr = mr;
            }

            public String getMs() {
                return ms;
            }

            public void setMs(String ms) {
                this.ms = ms;
            }

            public String getPcpn() {
                return pcpn;
            }

            public void setPcpn(String pcpn) {
                this.pcpn = pcpn;
            }

            public String getPop() {
                return pop;
            }

            public void setPop(String pop) {
                this.pop = pop;
            }

            public String getPres() {
                return pres;
            }

            public void setPres(String pres) {
                this.pres = pres;
            }

            public String getSr() {
                return sr;
            }

            public void setSr(String sr) {
                this.sr = sr;
            }

            public String getSs() {
                return ss;
            }

            public void setSs(String ss) {
                this.ss = ss;
            }

            public String getTmp_max() {
                return tmp_max;
            }

            public void setTmp_max(String tmp_max) {
                this.tmp_max = tmp_max;
            }

            public String getTmp_min() {
                return tmp_min;
            }

            public void setTmp_min(String tmp_min) {
                this.tmp_min = tmp_min;
            }

            public String getUv_index() {
                return uv_index;
            }

            public void setUv_index(String uv_index) {
                this.uv_index = uv_index;
            }
        }

        public static class HourlyBean {
            /**
             * cloud : 98
             * cond_code : 101
             * cond_txt : 多云
             * dew : 6
             * hum : 60
             * pop : 6
             * pres : 1023
             * time : 2019-01-25 13:00
             * tmp : 10
             * wind_deg : 223
             * wind_dir : 西南风
             * wind_sc : 1-2
             * wind_spd : 6
             */

            private String cond_code;
            private String cond_txt;
            private String pop;
            private String time;
            private String tmp;

            public String getCond_code() {
                return cond_code;
            }

            public void setCond_code(String cond_code) {
                this.cond_code = cond_code;
            }

            public String getCond_txt() {
                return cond_txt;
            }

            public void setCond_txt(String cond_txt) {
                this.cond_txt = cond_txt;
            }

            public String getPop() {
                return pop;
            }

            public void setPop(String pop) {
                this.pop = pop;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public String getTmp() {
                return tmp;
            }

            public void setTmp(String tmp) {
                this.tmp = tmp;
            }
        }

        public static class LifestyleBean {
            /**
             * type : comf
             * brf : 较舒适
             * txt : 白天天气晴好，早晚会感觉偏凉，午后舒适、宜人。
             */

            private String type;
            private String brf;
            private String txt;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getBrf() {
                return brf;
            }

            public void setBrf(String brf) {
                this.brf = brf;
            }

            public String getTxt() {
                return txt;
            }

            public void setTxt(String txt) {
                this.txt = txt;
            }
        }
    }
}
