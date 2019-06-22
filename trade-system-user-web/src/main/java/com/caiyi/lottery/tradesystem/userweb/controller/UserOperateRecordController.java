package com.caiyi.lottery.tradesystem.userweb.controller;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.annotation.CheckLogin;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.Result;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.usercenter.client.UserBaseInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by XQH on 2017/12/7.
 */
@Slf4j
@RestController
public class UserOperateRecordController {

    @Autowired
    UserBaseInterface userBaseInterface;
    @Autowired
    private HttpServletRequest request;
    /**
     *
     * 产品操作记录
     */
    @CheckLogin(sysCode = SysCodeConstant.USERWEB)
    @RequestMapping("/user/product_opertion_info.api")
    Result productOperationInfo(UserBean bean){
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        BaseResp<UserBean> response = userBaseInterface.productOperationInfo(baseReq);
        Result result = new Result();
        result.setCode(response.getCode());
        result.setDesc(response.getDesc());
       return  result;
    }

    /**
     * 产品反馈文件上传
     * @param bean
     * @return
     * @throws Exception
     */
    @CheckLogin(sysCode = SysCodeConstant.USERWEB)
    @RequestMapping("/user/product_feedback_info.api")
    public  Result check_login_feedback_multipart(UserBean bean) throws Exception {
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        String enctype = request.getHeader("content-type");
        if (enctype != null && enctype.indexOf("multipart/form-data") > -1) {
            try {
                String tempfile="feedback";
                log.info("产品反馈文件上传开始   feedback");
                handleMultiparRequest(request,bean,tempfile);
            } catch (Exception e) {
                log.info("产品反馈文件上传错误   feedback",e);
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("只能上传.jpg或.png格式图片!");
            }
        }
        BaseResp<UserBean> resp = userBaseInterface.check_login_feedback_multipart(baseReq);
        if ("1".equals(resp.getCode())){
            return new Result(BusiCode.SUCCESS,"反馈成功");
        }else {
            return new Result(BusiCode.FAIL,"反馈失败");
        }
    }


    private  void handleMultiparRequest(HttpServletRequest request, UserBean bean,String tempfile) throws Exception {
        log.info("进入上传文件内容,tempfile:"+tempfile);
        request.setCharacterEncoding("UTF-8");
        // 转型为MultipartHttpRequest
        MultipartHttpServletRequest multipartRequest = new StandardMultipartHttpServletRequest(request);
        log.info("上传文件二进制文件处理,uid="+bean.getUid());
        MultipartFile file = multipartRequest.getFile("img0");
        MultipartFile file2 = multipartRequest.getFile("img1");
        MultipartFile file3 = multipartRequest.getFile("bankCardPosiUrl");
        MultipartFile [] multipartFiles = {file,file2,file3};
        for (MultipartFile file1 : multipartFiles){
            if (file1!=null && !file1.isEmpty()){
                    String photoPath = "/opt/export/data/pupload" + File.separator + tempfile;
                    List<String> exts = new ArrayList<String>();
                    exts.add(".jpg");
                    exts.add(".JPG");
                    exts.add(".PNG");
                    exts.add(".png");
                    String tmpName = file1.getOriginalFilename();
                    String fieldName = file1.getName();
                    log.info("上传文件二进制文件处理,tmpName:"+tmpName);
                    String ext = "";
                    if("blob".equals(tmpName)){
                        String contentType = file1.getContentType();
                        log.info("上传文件二进制文件处理,contentType:"+contentType+",uid="+bean.getUid());
                        if("image/png".equals(contentType)){
                            ext = ".png";
                        }else if("image/jpg".equals(contentType)){
                            ext = ".jpg";
                        }else{
                            throw new Exception("只能上传.jpg或.png格式图片");
                        }
                    }else{
                        ext = tmpName.substring(tmpName.lastIndexOf("."));
                    }
                    if(!exts.contains(ext)) {
                        throw new Exception("只能上传.jpg或.png格式图片");
                    }
                    Random random = new Random();
                    BigInteger big = new BigInteger(64, random);
                    String imgName = big.toString() + ext;
                    File originalImg = new File(photoPath, imgName);
                    OutputStream outStream = new FileOutputStream(originalImg);
                    log.info("上传文件--》写文件开始，name:" + originalImg.getPath() + originalImg.getName()+",uid="+bean.getUid());
                    InputStream inStream = file1.getInputStream();
                    byte[] buffer = new byte[4096];
                    int size = inStream.read(buffer);
                    while(size != -1) {
                        outStream.write(buffer, 0, size);
                        size = inStream.read(buffer);
                    }
                    inStream.close();
                    outStream.close();
                    inStream = null;
                    outStream = null;
                    StringBuilder builder = new StringBuilder();
                    builder.append("/data/pupload");
                    builder.append("/");
                    builder.append(tempfile);
                    builder.append("/");
                    builder.append(imgName);
                    log.info("上传文件--》传递文件名，fieldName:" + fieldName+",uid="+bean.getUid());
                    log.info("存放路径:" + builder.toString());
                    if("img0".equalsIgnoreCase(fieldName)) {
                        bean.setPicone(builder.toString());
                    }
                    if("img1".equalsIgnoreCase(fieldName)){
                        bean.setPictwo(builder.toString());
                    }
                    if("bankCardPosiUrl".equalsIgnoreCase(fieldName)){
                        bean.setPicthree(builder.toString());
                    }
            }
        }
    }
}
