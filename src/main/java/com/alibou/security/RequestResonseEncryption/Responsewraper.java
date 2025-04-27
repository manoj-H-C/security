//package com.alibou.security.RequestResonseEncryption;
//
//import com.alibou.security.constants.CommonConstants;
//import jakarta.servlet.ServletOutputStream;
//import jakarta.servlet.WriteListener;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpServletResponseWrapper;
//import org.json.JSONObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//
//public class Responsewraper extends HttpServletResponseWrapper {
//  static Logger logger = LoggerFactory.getLogger(Responsewraper.class);
//  private ServletOutputStream out;
//  StringBuffer body = new StringBuffer();
//  HttpServletResponse response;
//
//  public Responsewraper(HttpServletResponse response) {
//    super(response);
//    this.response = response;
//    out =
//        new ServletOutputStream() {
//
//          @Override
//          public void write(int b) throws IOException {
//            // TODO Auto-generated method stub
//            body.append((char) b);
//          }
//
//          @Override
//          public void setWriteListener(WriteListener listener) {
//            // TODO Auto-generated method stub
//
//          }
//
//          @Override
//          public boolean isReady() {
//            // TODO Auto-generated method stub
//            return false;
//          }
//        };
//    // TODO Auto-generated constructor stub
//  }
//
//  @Override
//  public ServletOutputStream getOutputStream() throws IOException {
//    // TODO Auto-generated method stub
//    // System.out.println("out stream calleds");
//    return out;
//  }
//
//  @Override
//  public void flushBuffer() throws IOException {
//    // TODO Auto-generated method stub
//
//    response.resetBuffer();
//  }
//
//  public void flushBuffer1() throws IOException {
//    // TODO Auto-generated method stub
//    logger.info("flush buffer");
//    super.flushBuffer();
//  }
//
//  public String getEncryptedResponse(boolean debugFlag, boolean initialrequest) throws Exception {
//    logger.info("getEncryptedResponse method started {} {} ",debugFlag,initialrequest);
//    JSONObject jresp = null;
//    if (body.toString().isEmpty()) {
//      logger.info("empty response");
//      jresp = new JSONObject();
//      jresp.put(CommonConstants.MESSAGE, "Some Technical error please try after some Time");
//      jresp.put(CommonConstants.STATUS, "400");
//      jresp.put(CommonConstants.DATA, "");
//      this.setStatus(SC_BAD_REQUEST);
//    } else {
//      jresp = new JSONObject(body.toString().trim());
//    }
//    if (initialrequest && jresp.has(CommonConstants.DATA)) {
//      JSONObject obj = null;
//      try {
//        obj = jresp.getJSONObject(CommonConstants.DATA);
//      } catch (Exception e) {
//
//      }
//      JSONObject dataObject = null;
//      if (obj == null) {
//        dataObject = new JSONObject();
//
//      } else {
//        dataObject = obj;
//      }
//      dataObject.put(CommonConstants.DEBUG, debugFlag);
//      jresp.put(CommonConstants.DATA, dataObject);
//    }
//
//    if (initialrequest || (!debugFlag)) {
//      logger.info("before encrypting the response : {} ",jresp.toString());
//      return InternalEncryptionDecryption.encryptCBC(jresp.toString());
//    } else {
//      return jresp.toString();
//    }
//  }
//
//  public String toString() {
//    return out.toString();
//  }
//}
