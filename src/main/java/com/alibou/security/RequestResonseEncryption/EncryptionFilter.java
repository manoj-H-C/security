package com.alibou.security.RequestResonseEncryption;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@Service
public class EncryptionFilter implements Filter {
    static Logger logger = LoggerFactory.getLogger(EncryptionFilter.class);

    private final ServiceExtraFlagDao extraflageDao;

    public EncryptionFilter(ServiceExtraFlagDao extraflageDao) {
        this.extraflageDao = extraflageDao;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        logger.info("doFilter of EncryptionFilter just started ");
        List<String> initialService = new ArrayList<>();
        initialService.add("startNewSession");
        initialService.add("login");
        String reqUrl = ((HttpServletRequest) request).getServletPath();
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        logger.info("httpRequest {} ", httpRequest);

        RequestWraper requestWrapper = null;
        try {
            requestWrapper = new RequestWraper(httpRequest, extraflageDao, initialService.contains(reqUrl.replace("/", "")) ? reqUrl.replace("/", "") : "login");
            logger.info("requestwrapper : {} ", requestWrapper);
        } catch (Exception e) {
            logger.error("error in requestWrapper {} ", e.getStackTrace());
        }
        Responsewraper responseWrapper = new Responsewraper(httpResponse);

        try {
            chain.doFilter(requestWrapper, responseWrapper);

            String encryptedResponse = responseWrapper.getEncryptedResponse(requestWrapper.getDebugFlag(), requestWrapper.getInitialrequest());
            logger.info("encryptedResponse : {} ", encryptedResponse);
            httpResponse.setContentType("application/json");
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            httpResponse.getWriter().write(encryptedResponse);
            httpResponse.getWriter().flush();
            httpResponse.getWriter().close();
        } catch (Exception e) {
            logger.error("error in encrypted response {} ", e.getStackTrace());
        }
        logger.info("doFilter of EncryptionFilter just finished ");
    }

    @Override
    public void destroy() {
        // Cleanup logic if needed
    }
}
