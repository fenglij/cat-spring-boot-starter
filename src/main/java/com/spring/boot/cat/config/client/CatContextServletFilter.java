package com.spring.boot.cat.config.client;

import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.spring.boot.cat.config.util.ServletUtil;
import com.spring.boot.cat.config.wrapper.DefaultHttpServletRequestWrapper;
import com.spring.boot.cat.config.wrapper.DefaultHttpServletResponseWrapper;
import com.spring.boot.cat.constants.CatConstantsExt;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * http协议传输，远程调用链目标端接收context的filter，
 * 通过header接收rootId、parentId、childId并放入CatContextImpl中，调用Cat.logRemoteCallServer()进行调用链关联
 * 注:若不涉及调用链，则直接使用cat-client.jar中提供的filter即可
 * 使用方法（视项目框架而定）：
 * 1、web项目：在web.xml中引用此filter
 * 2、Springboot项目，通过注入bean的方式注入此filter
 *
 * @author fenglijian
 * @date 2022-10-26 09:54
 */
public class CatContextServletFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //HttpServletRequest request = (HttpServletRequest) servletRequest;
        DefaultHttpServletRequestWrapper request = new DefaultHttpServletRequestWrapper((HttpServletRequest) servletRequest);

        String uri = request.getRequestURI();

        //若header中有context相关属性，则生成调用链，若无，仅统计请求的Transaction
        if(null != request.getHeader(CatConstantsExt.CAT_HTTP_HEADER_ROOT_MESSAGE_ID)){
            CatContextImpl catContext = new CatContextImpl();
            catContext.addProperty(Cat.Context.ROOT, request.getHeader(CatConstantsExt.CAT_HTTP_HEADER_ROOT_MESSAGE_ID));
            catContext.addProperty(Cat.Context.PARENT, request.getHeader(CatConstantsExt.CAT_HTTP_HEADER_PARENT_MESSAGE_ID));
            catContext.addProperty(Cat.Context.CHILD, request.getHeader(CatConstantsExt.CAT_HTTP_HEADER_CHILD_MESSAGE_ID));
            Cat.logRemoteCallServer(catContext);
        }

        Transaction t = Cat.newTransaction(CatConstants.TYPE_URL, uri);

        try{
            Cat.logEvent(CatConstantsExt.Type_URL_METHOD, request.getMethod(), Message.SUCCESS,request.getRequestURL().toString());
            Cat.logEvent(CatConstantsExt.Type_URL_CLIENT, request.getRemoteHost());
            Cat.logEvent(CatConstantsExt.Type_URL_PARAM, ServletUtil.getParameter(request));

            filterChain.doFilter(request, servletResponse);

            t.setStatus(Transaction.SUCCESS);
        }catch (Exception e){
            t.setStatus(e);
            Cat.logError(e);
        }finally {
            t.complete();
        }
    }


    @Override
    public void destroy() {

    }
}

