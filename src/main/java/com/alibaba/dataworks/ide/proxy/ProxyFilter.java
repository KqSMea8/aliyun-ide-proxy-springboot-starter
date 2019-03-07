package com.alibaba.dataworks.ide.proxy;

import com.alibaba.dataworks.dataservice.model.api.protocol.ApiProtocol;
import com.alibaba.dataworks.dataservice.sdk.facade.DataApiClient;
import com.alibaba.dataworks.dataservice.sdk.loader.http.Request;
import com.alibaba.dataworks.dataservice.sdk.loader.http.enums.Method;
import com.alibaba.dataworks.ide.proxy.common.Result;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ProxyFilter extends OncePerRequestFilter {

    Logger logger = LoggerFactory.getLogger(ProcessBuilder.class) ;

    static String dsApiIdKey = "dSaPiId";
    static String dsApiPath = "dsApiPath" ;
    static String dsApiMethod = "dsApiMethod" ;

    ProxyProperties proxyProperties ;

    DataApiClient dataApiClient ;

    public ProxyFilter(ProxyProperties proxyProperties ,
                       DataApiClient dataApiClient){
        this.proxyProperties = proxyProperties ;
        this.dataApiClient = dataApiClient ;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        logger.info("DataServiceProxyFilter.doFilterInternal:: dsproxy request found [{}] - {}"
                , request.getSession().getId(), request.getRequestURI());

        String apiPath = request.getParameter(dsApiPath) ;
        String method = request.getParameter(dsApiMethod) ;

        Gson gson = new Gson();
        Request dsRequest = new Request();
        if (method.equals("POST")) {
            dsRequest.setMethod(Method.POST);
        } else {
            dsRequest.setMethod(Method.GET);
        }

        dsRequest.setAppKey(proxyProperties.getAppKey());
        dsRequest.setAppSecret(proxyProperties.getAppSecret());
        dsRequest.setHost(proxyProperties.getHost());
        dsRequest.setPath(apiPath);
        dsRequest.setApiProtocol(ApiProtocol.HTTP);

        // HTTP GET
        HashMap<String, String> requestParams = new HashMap<>();
        request.getParameterMap().keySet().forEach(key -> {
            if (!key.equals(dsApiIdKey) && !key.equals(dsApiPath) && !key.equals(dsApiMethod)) {
                String val = request.getParameterMap().get(key).length > 0 ? request.getParameterMap().get(key)[0] : "";
                requestParams.put(key, val);
                logger.info("DataServiceProxyFilter.doFilterInternal:: getParameterMap =  [{} -> {}]"
                        , key, val);
            }
        });
        dsRequest.getQuerys().putAll(requestParams);

        if (method.equals("POST")) {
            JSONObject requestBody = JSON.parseObject(IOUtils.toString(request.getInputStream(),
                    StandardCharsets.UTF_8.name()));
            for (Map.Entry<String, Object> item : requestBody.entrySet()) {
                dsRequest.getBodys().put(item.getKey(), item.getValue());
            }
        }

        logger.info("DataServiceProxyFilter.doFilterInternal:: send request =  [{}]"
                , new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(dsRequest));
        logger.info("DataServiceProxyFilter.doFilterInternal:: send request.getQuerys =  [{}]"
                , new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(dsRequest.getQuerys()));
        logger.info("DataServiceProxyFilter.doFilterInternal:: send request.getBodys =  [{}]"
                , new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(dsRequest.getBodys()));

        HashMap payload = null;
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            payload = dataApiClient.dataLoad(dsRequest);
            if (payload.get("errCode").equals(0)) {
                response.getOutputStream().write(gson.toJson(payload).getBytes());
            } else {
                payload.put("success", false);
                response.getOutputStream().write(gson.toJson(payload).getBytes());
            }
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e1) {
            logger.error("error: " , e1);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getOutputStream().write(gson.toJson(Result.ofError(e1.getMessage())).getBytes());
        }
        response.getOutputStream().flush();
    }
}
