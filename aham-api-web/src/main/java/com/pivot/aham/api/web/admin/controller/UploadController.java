package com.pivot.aham.api.web.admin.controller;

import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.base.MessageStandardCode;
import com.pivot.aham.common.core.util.UploadUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 上传
 *
 * @author addison
 * @since 2018年11月19日
 */
@RestController
@Api(value = "文件上传接口", description = "文件上传接口")
@RequestMapping(value = "/upload", method = RequestMethod.POST)
public class UploadController extends AbstractController {

    /**
     * 文件上传到临时目录
     * @param request
     * @param modelMap
     * @return
     */
    @RequestMapping("/temp/file")
    @ApiOperation(value = "上传文件")
    public Object uploadFile(HttpServletRequest request, ModelMap modelMap) {
        List<String> fileNames = UploadUtil.uploadFile(request);
        return genRes(modelMap, fileNames);
    }

    /**
     * 封装返回
     * @param modelMap
     * @param fileNames
     * @return
     */
    private Object genRes(ModelMap modelMap, List<String> fileNames) {
        if (fileNames.size() > 0) {
            modelMap.put("fileNames", fileNames);
            return setSuccessModelMap(modelMap);
        } else {
            setModelMap(modelMap, MessageStandardCode.BAD_REQUEST);
            modelMap.put("msg", "请选择要上传的文件！");
            return modelMap;
        }
    }

    /**
     * 上传图片并自动缩放
     * @param request
     * @param modelMap
     * @return
     */
    @RequestMapping("/temp/image")
    @ApiOperation(value = "上传图片")
    public Object uploadImage(HttpServletRequest request, ModelMap modelMap) {
        List<String> fileNames = UploadUtil.uploadImage(request, false);
        return genRes(modelMap, fileNames);
    }

    /**
     * 上传文件(base64)
     * @param request
     * @param modelMap
     * @return
     */
    @RequestMapping("/temp/imageData")
    @ApiOperation(value = "上传图片")
    public Object uploadImageData(HttpServletRequest request, ModelMap modelMap) {
        List<String> fileNames = UploadUtil.uploadStrData(request);
        return genRes(modelMap, fileNames);
    }

}
