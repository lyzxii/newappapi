package com.caiyi.lottery.tradesystem.userweb.service;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.util.DateTimeUtil;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import constant.UserConstants;
import dto.UserPhotoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.math.BigInteger;
import java.util.*;

/**
 * 文件上传到服务器
 * @author wxy
 * @create 2017-11-30 16:03
 **/
@Service("userService")
public class UploadService {
    private Logger logger = LoggerFactory.getLogger(UploadService.class);

    /**
     * 主要路径拼装
     * @param request
     * @return
     */
    public BaseResp photoMultipart(HttpServletRequest request) {
        List<String> list = new ArrayList<>();
        list.add(UserConstants.USERPHOTO);
        BaseResp baseResp = commonUpload(list,request,UserConstants.USERPHOTO_SUB_PATH);
        return baseResp;
    }

    /**
     * 上传图片
     * @param list
     * @param request
     * @param subPath
     * @return
     */
    private BaseResp commonUpload(List<String> list, HttpServletRequest request, String subPath) {
        UserPhotoDTO userPhotoDTO = new UserPhotoDTO();
        BaseResp<UserPhotoDTO> baseResp = new BaseResp<>();
        baseResp.setData(userPhotoDTO);
        // 转型为MultipartHttpRequest
        MultipartHttpServletRequest multipartRequest = new StandardMultipartHttpServletRequest(request);
        for (String key : list){
            //  根据前台的name名称得到上传的文件 
            MultipartFile file = multipartRequest.getFile(key);
            if (file == null || file.isEmpty()) {
                baseResp.setCode(BusiCode.USER_UPLOADPHOTO_NULL);
                baseResp.setDesc("上传文件为空");
                return baseResp;
            }
        }
        try {
            String tempfile;
            String tempString;
            //每周一存放一个文件夹
            String currentDate = DateTimeUtil.formatDate(new Date(), DateTimeUtil.DATE_FORMAT);
            int dayForWeek = DateTimeUtil.dayForWeek(currentDate);
            if(dayForWeek == 1){
                tempfile = UserConstants.PUPLOAD_PATH + subPath + currentDate;
                tempString = subPath + currentDate;
            }else{
                String dayOfLastWeek = DateTimeUtil.getDayOfLastWeek(0);
                tempfile = UserConstants.PUPLOAD_PATH + subPath + dayOfLastWeek;
                tempString = subPath + dayOfLastWeek;
            }
            userPhotoDTO.setFilePath(tempfile);
            File file1 = new File(tempfile);
            if(!file1 .exists()  && !file1 .isDirectory()){
                logger.info(tempfile + ";文件夹不存在，创建文件夹");
                file1.mkdirs();
            }
            logger.info("用户图像上传开始");
            boolean flag = handleMultiparRequest(request, userPhotoDTO, tempString);
            if (!flag) {
                baseResp.setCode(BusiCode.USER_UPLOADPHOTO_FORMAT_ERROR);
                baseResp.setDesc("只能上传.jpg或.png格式图片");
                return baseResp;
            }
            baseResp.setDesc("图片上传成功");
            baseResp.setCode(BusiCode.SUCCESS);
        } catch (Exception e) {
            logger.info("用户图像上传错误",e);
            baseResp.setCode(ErrorCode.USER_UPLOADPHOTO_FILE_ERROR);
            baseResp.setDesc("图片上传失败");
        }
        return baseResp;
    }

    /**
     * 文件上传
     * @param request
     * @param userPhotoDTO
     * @param tempfile
     * @return
     * @throws Exception
     */
    public boolean handleMultiparRequest(HttpServletRequest request, UserPhotoDTO userPhotoDTO, String tempfile) throws Exception {
        logger.info("进入上传文件内容,tempfile:"+tempfile);

        // 转型为MultipartHttpRequest
        MultipartHttpServletRequest multipartRequest = new StandardMultipartHttpServletRequest(request);
        Iterator<String> files = multipartRequest.getFileNames();
        while (files.hasNext()) {
            String fildName = files.next();
            //  根据前台的name名称得到上传的文件 
            MultipartFile file = multipartRequest.getFile(fildName);
            // 取得文件类型
            String contentType = file.getContentType();
            // 扩展名
            String expandedName;

            if ("image/pjpeg".equals(contentType)
                        || "image/jpeg".equals(contentType)) {
                // IE6上传jpg图片的headimageContentType是image/pjpeg，而IE9以及火狐上传的jpg图片是image/jpeg
                expandedName = ".jpg";
            } else if ("image/png".equals(contentType)
                               || "image/x-png".equals(contentType)) {
                // IE6上传的png图片的headimageContentType是"image/x-png"
                expandedName = ".png";
            } else {
                return false;
            }

            Random random = new Random();
            BigInteger big = new BigInteger(64, random);
            String imgName = big.toString() + expandedName;

            File originalImg = new File(userPhotoDTO.getFilePath(), imgName);

            logger.info("上传文件--》写文件开始，name:" + originalImg.getPath());

            FileCopyUtils.copy(file.getBytes(), originalImg);

            StringBuilder builder = new StringBuilder();
            builder.append("/data/pupload");
            builder.append("/");
            builder.append(tempfile);
            builder.append("/");
            builder.append(imgName);
            logger.info("上传文件--》传递文件名，fileName:" + imgName);
            logger.info("存放路径:" + builder.toString());

            if(("userPhoto").equalsIgnoreCase(fildName)) {
                userPhotoDTO.setPhotoPath(builder.toString());
            } else if(("frontPhoto").equalsIgnoreCase(fildName)){
                userPhotoDTO.setFrontPath(builder.toString());
            } else if(("backPhoto").equalsIgnoreCase(fildName)){
                userPhotoDTO.setBackPath(builder.toString());
            }
        }
        return true;
    }

    /**
     * 上传身份证
     * @param request
     */
    public BaseResp checkLoginAndMutilPart(HttpServletRequest request) {
        List<String> list = new ArrayList<>();
        list.add(UserConstants.USERBANK_FRONT);
        list.add(UserConstants.USERBANK_BACKPHOTO);
        BaseResp baseResp = commonUpload(list,request,UserConstants.USERPHOTO_SUB_IDCARDPATH);
        return baseResp;
    }
}
