package com.atgmall.webutil.config;

import com.alibaba.fastjson.JSON;
import config.HttpClientUtil;
import io.jsonwebtoken.impl.Base64UrlCodec;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.atgmall.webutil.config.WebConst.LOGIN_ADDRESS;
import static com.atgmall.webutil.config.WebConst.VERIFY_ADDRESS;

/**
 * @作者: 崔航铭 手机: 18704311678
 * @Email:1870431678@163.com
 * @date 2019/8/4 14:46
 */
@Component  //TODO: 需要扫描该组件
public class AuthInterceptor extends HandlerInterceptorAdapter {

    //用户进入控制器之前
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取登陆成功之后的newToken
        String token = request.getParameter("newToken");
        if(token!=null){
//把Token放到cookie中                                                       cookie的有效时间
            CookieUtil.setCookie(request,response,"token",token,WebConst.COOKIE_MAXAGE,false);
         }






        //登陆成功之后，进入其他页面时，如何保持登陆状态，cookie里已经有token，但在其他页面获取不到token值时
        //从Cookie中获取token
        if(token==null){
             token = CookieUtil.getCookieValue(request, "token", false);
        }
//将Cookie中用户的信息放到域中
        if(token!=null){
            Map map=getUserMapByToken(token);
            String name = (String) map.get("nickName");

            request.setAttribute("nickName",name);
        }

        //使用登陆注解.  判断该控制器是否必须登陆,是--》认证中心认证《redis--是否有数据》
        HandlerMethod handlerMethod= (HandlerMethod) handler;
        LoginRequire loginannotation = handlerMethod.getMethodAnnotation(LoginRequire.class);
        if(loginannotation!=null){
            //去认证中心，判断是否登陆
            String sale = request.getHeader("X-forwarded-for"); //此ip服务器地址是，哪个《哪个控制器》请求服务器的ip地址
            String result = HttpClientUtil.doGet(VERIFY_ADDRESS + "?token=" + token + "&sale=" + sale);
            if(result.equals("success")){
               //认证成功，redis有该用户
                Map map=getUserMapByToken(token);
                String userId = (String) map.get("userId");
        //TODO：存放userID 以后支付系统会用到
                request.setAttribute("userId",userId);
                return true;
            }else {
        //认证失败，redis没有该用户,根据情况，是否要跳转登陆页面
                if(loginannotation.autoRedirect()){     //登陆注解为true时
                    String stringurl = request.getRequestURL().toString();
                    System.out.println("跳转的地址:"+stringurl);
                    //将登陆成功之后要跳转的页面的Url:    stringurl进行编码
                    String encode = URLEncoder.encode(stringurl, "UTF-8");  //编码后的登陆跳转地址
       //跳转登陆页面
                    response.sendRedirect(LOGIN_ADDRESS+"?originUrl="+encode);
                    return false;   //拦截器拦截
                }
            }
        }
        return true;//拦截器放行
    }




    //根据usertoken获取map集合
    private Map getUserMapByToken(String token) {
        //对token 进行解密
        String userToken = StringUtils.substringBetween(token, ".");
        //对token中间私有部分，进行base64解码
        Base64UrlCodec UrlCodec = new Base64UrlCodec();
        byte[] bytes = UrlCodec.decode(userToken);
        //bytes的二进制user信息，装成字符串
        String tokenJson=null;
        Map map= new HashMap<>();
        try {
            tokenJson=new String(bytes,"UTF-8");
             map = JSON.parseObject(tokenJson, Map.class);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }




    //进入控制器，视图渲染之前
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {


    }

    //视图渲染之后
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {


    }

}
