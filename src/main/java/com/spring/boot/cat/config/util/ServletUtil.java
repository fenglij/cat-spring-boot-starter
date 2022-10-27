package com.spring.boot.cat.config.util;

import com.google.gson.Gson;
import com.spring.boot.cat.config.wrapper.DefaultHttpServletRequestWrapper;

import javax.servlet.http.HttpServletRequest;

/**
 * @author fenglijian
 * @date 2022-10-27 13:48
 */
public class ServletUtil {

    public static String getParameter(DefaultHttpServletRequestWrapper request) {
        String parameter = "";
        if("GET".equals(request.getMethod().toUpperCase())){
            parameter = new Gson().toJson(request.getQueryString());
        }else{
            String body = request.getBody();
            if (body == null || "".equals(body.trim())) {
                parameter = request.getQueryString();
            } else {
                parameter = new Gson().toJson(request.getBody());
            }
        }

        return parameter;
    }

    public static String getParameter(HttpServletRequest request) {
        DefaultHttpServletRequestWrapper wrapper = new DefaultHttpServletRequestWrapper(request);
        return getParameter(wrapper);
    }
}
