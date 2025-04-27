//package com.alibou.security.RequestResonseEncryption;
//
//import com.alibou.security.constants.CommonConstants;
//import jakarta.servlet.ReadListener;
//import jakarta.servlet.ServletInputStream;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletRequestWrapper;
//import org.json.JSONObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.util.stream.Collectors;
//
//// @Component
//public class RequestWraper extends HttpServletRequestWrapper {
//  static Logger logger = LoggerFactory.getLogger(RequestWraper.class);
//  @Autowired
//  private  CommonConstants constants;
//  private final String body;
//  private final boolean DEBUG_FLAGE;
//  private final boolean INITIAL_REQUEST;
//
//  public RequestWraper(
//      HttpServletRequest request, ServiceExtraFlagDao extraFlagDao, String initialService)
//      throws Exception {
//    super(request);
//    logger.info("RequestWraper started");
//    String receivedRequest = request.getReader().lines().collect(Collectors.joining());
//    logger.info("receivedRequest value : {} ", receivedRequest);
//    JSONObject jrequest = new JSONObject(receivedRequest.trim());
//    String[] initialPath = initialService.split("/");
//    String[] actualPath = ((HttpServletRequest) request).getServletPath().trim().split("/");
//    boolean initialCall = false;
//    int differenceInPathLength = actualPath.length - initialPath.length;
//    if (initialPath.length > 0) {
//      initialCall = true;
//      for (int i = 0; i < initialPath.length; i++) {
//        if (!(initialPath[i].equalsIgnoreCase(actualPath[differenceInPathLength + i]))) {
//          initialCall = false;
//          break;
//        }
//      }
//    }
//
//    logger.info("initialcall value : {} ",initialCall);
//    if (initialCall) {
//      ServiceExtraFlag sef = extraFlagDao.findByService(CommonConstants.DEBUG);
//      int flag = 0;
//      if (sef != null) {
//        flag = sef.getFlag();
//      }
//      jrequest.put(CommonConstants.DEBUG, flag == 1 ? true : false);
//      logger.info("initial request  ===");
//      INITIAL_REQUEST = true;
//    } else {
//      INITIAL_REQUEST = false;
//    }
//
//    if (jrequest.toString().contains(CommonConstants.DEBUG)) {
//      DEBUG_FLAGE = jrequest.getBoolean(CommonConstants.DEBUG);
//      if (INITIAL_REQUEST || !DEBUG_FLAGE) {
//        body = InternalEncryptionDecryption.decryptCBC(jrequest.getString(CommonConstants.REQUEST_BODY));
//      } else {
//        body = jrequest.get(CommonConstants.REQUEST_BODY).toString();
//      }
//
//    } else {
//      ////// commented for common api request formate will be same as send
//      body = jrequest.toString(); // .getString("request_body").toString();
//      DEBUG_FLAGE = true;
//    }
//    // System.out.println(body);
//    if (body.contains("&LT;\\S*SCRIPT")
//        || body.contains("<\\S*SCRIPT")
//        || body.contains("S*DOCTYPE")
//        || body.contains("&APOS;")
//        || body.contains("&GT;")
//        || body.contains("&gt;")
//        || body.contains("&LT;")
//        || body.contains("&lt;")
//        // || body.contains("\"")
//        || body.contains("$")
//        || body.contains("#")
//        || body.contains("%")
//        // || body.contains("&")
//        // || body.contains("-")
//        // || body.contains("_")
//        || body.contains("=")
//        || body.contains("+")
//        || body.contains("*")
//        // || body.contains("'")
//        // || body.contains("(")
//        // || body.contains(")")
//        || body.contains("<")
//        || body.contains(">")
//        // || body.contains("@")
//        || body.contains("?")
//        || body.contains("\\")
//        // || body.contains("/")
//        || body.contains("`")
//        || body.contains("~")
//        // || body.contains("(")
//        // || body.contains(")")
//
//        || body.contains("!")
//        || body.contains("^")
//    // || body.contains("]")
//    // || body.contains("[")
//
//    // || body.contains(";")
//    ) {
//      logger.info("something went wrong with the body");
//      String[] uri = request.getRequestURI().split("/");
//      if (!(uri[uri.length - 1].equalsIgnoreCase("docUploadWithEsign")
//          || uri[uri.length - 1].equalsIgnoreCase("saveUploadDocument")
//          || uri[uri.length - 1].equalsIgnoreCase("login")
//          || uri[uri.length - 1].equalsIgnoreCase("perfiosCallback"))) {
//
//        //        throw new IllegalArgumentException("Special character not allowed");
//      }
//      // throw new IllegalArgumentException("Special character not allowed");
//    }
//  }
//
//  @Override
//  public ServletInputStream getInputStream() throws IOException {
//    ByteArrayInputStream byteArray = new ByteArrayInputStream(body.getBytes());
//    ServletInputStream sis =
//        new ServletInputStream() {
//
//          @Override
//          public int read() throws IOException {
//            // TODO Auto-generated method stub
//            return byteArray.read();
//          }
//
//          @Override
//          public void setReadListener(ReadListener listener) {
//            // TODO Auto-generated method stub
//
//          }
//
//          @Override
//          public boolean isReady() {
//            // TODO Auto-generated method stub
//            return false;
//          }
//
//          @Override
//          public boolean isFinished() {
//            // TODO Auto-generated method stub
//            return false;
//          }
//        };
//    return sis;
//  }
//
//  public boolean getDebugFlag() {
//    return DEBUG_FLAGE;
//  }
//
//  public boolean getInitialrequest() {
//    return INITIAL_REQUEST;
//  }
//}
