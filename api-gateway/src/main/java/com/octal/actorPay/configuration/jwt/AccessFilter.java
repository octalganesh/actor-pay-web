package com.octal.actorPay.configuration.jwt;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.http.HttpStatus;

public class AccessFilter {
        //extends ZuulFilter {
//
//    @Override
//    public String filterType() {
//        return "pre";
//    }
//
//    @Override
//    public int filterOrder() {
//        return 0;
//    }
//
//    @Override
//    public boolean shouldFilter() {
//        return true;
//    }
//
//    @Override
//    public Object run() {
//        RequestContext requestContext = RequestContext.getCurrentContext();
//        String request = requestContext.getRequest().getHeader("Authorization");
//        if(request == null) {
//            requestContext.setSendZuulResponse(false);
//            requestContext.setResponseStatusCode(HttpStatus.SC_UNAUTHORIZED);
//            return null;
//        }
//        return null;
//    }
}