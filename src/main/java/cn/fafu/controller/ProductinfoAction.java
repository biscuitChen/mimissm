package cn.fafu.controller;

import cn.fafu.pojo.ProductInfo;
import cn.fafu.pojo.vo.ProductInfoVo;
import cn.fafu.service.ProductinfoService;
import cn.fafu.utils.FileNameUtil;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/prod")
public class ProductinfoAction {

    //每页显示的记录数
    public static final int PAGE_SIZE = 5;
    //异步上传的图片名称
    String saveFileName = "";

    //界面层中一定有业务逻辑层的对象
    @Autowired
    ProductinfoService productinfoService;

    //显示全部商品不分页
    @RequestMapping("/getAll")
    public String getAll(HttpServletRequest request) {

        List<ProductInfo> list = productinfoService.getAll();
        request.setAttribute("list", list);
        return "product";
    }

    //显示第一页的五条记录
    @RequestMapping("/split")
    public String split(HttpServletRequest request) {

        //得到第一页的数据
        PageInfo info = productinfoService.splitPage(1, PAGE_SIZE);
        request.setAttribute("info", info);
        return "product";
    }

    //ajax分页翻页处理
    @ResponseBody
    @RequestMapping("/ajaxSplit")
    public void ajaxSplit(ProductInfoVo vo, HttpSession session) {

        //取得当前page参数页面的数据
        PageInfo info = productinfoService.splitPageVo(vo, PAGE_SIZE);
        session.setAttribute("info", info);
    }

    //异步ajax文件上传处理
    @ResponseBody
    @RequestMapping("/ajaxImg")
    public Object ajaxImg(MultipartFile pimage, HttpServletRequest request) {

        //提取生成文件名UUID+上传图片的后缀.jpg .png
        saveFileName = FileNameUtil.getUUIDFileName() + FileNameUtil.getFileType(pimage.getOriginalFilename());

        //得到项目中图片存储的路径
        String path = request.getServletContext().getRealPath("/image_big");

        //转存
        try {
            pimage.transferTo(new File(path + File.separator + saveFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //返回客户端JSON对象，封装图片的路径，为了在页面实现立即回显
        JSONObject object = new JSONObject();
        object.put("imgurl", saveFileName);

        return object.toString();
    }

    @RequestMapping("/save")
    public String save(ProductInfo info, HttpServletRequest request) {

        info.setpImage(saveFileName);
        info.setpDate(new Date());

        int num = -1;

        num = productinfoService.save(info);

        if (num > 0) {
            request.setAttribute("msg", "增加成功");
        } else {
            request.setAttribute("msg", "增加失败");
        }
        //清空saveFileName变量中的内容，为了下次增加或修改的异步ajax的上传处理
        saveFileName = "";
        //增加成功后一个重新查询数据库，所以跳转到分页显示的action上
        return "forward:/prod/split.action";
    }

    @RequestMapping("/one")
    public String one(HttpSession session ,int pid, ProductInfoVo vo ,Model model) {

        ProductInfo info = productinfoService.getByID(pid);
        model.addAttribute("prod", info);

        session.setAttribute("prodVo", vo);
        return "update";
    }

    @RequestMapping("/update")
    public String update(ProductInfo info, HttpServletRequest request){

        //因为ajax异步图片上传，如果有上传过，则saveFileName里有上传上来的图片，
        // 如果没有异步ajax上传过图片，则saveFileName="",
        // 实体类info使用隐藏表单域提供上来的pImage原始图片名称
        if (!saveFileName.equals("")){
            info.setpImage(saveFileName);
        }

        int num = -1;
        try {
            num = productinfoService.update(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (num > 0) {
            //此时说明更新成功
            request.setAttribute("msg", "更新成功");
        } else {
            //此时说明更新失败
            request.setAttribute("msg", "更新失败");
        }

        saveFileName = "";

        return "forward:/prod/split.action";
    }

    @RequestMapping("/delete")
    public String delete(Integer pid, HttpServletRequest request) {

        int num = -1;

        try {
            num = productinfoService.delete(pid);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (num > 0) {
            request.setAttribute("msg", "更新成功");
        } else {
            request.setAttribute("msg", "删除失败");
        }

        //删除结束后跳到分页显示
        return "forward:/prod/deleteAjaxSplit.action";
    }

    @ResponseBody
    @RequestMapping(value = "/deleteAjaxSplit", produces = "text/html;charset = UTF-8")
    public Object deleteAjaxSplit( HttpServletRequest request) {

        //取得第一页的数据
        PageInfo info = productinfoService.splitPage(1, PAGE_SIZE);
        request.getSession().setAttribute("info", info);
        return request.getAttribute("msg");
    }

    @RequestMapping("/deleteBatch")
    public String deleteBatch(String pids, HttpServletRequest request) {

        String[] ps = pids.split(",");
        int num = 0;
        try {
            num = productinfoService.deleteBatch(ps);
        } catch (Exception e) {
            request.setAttribute("msg", "商品不可删除");
        }
        if (num > 0) {
            request.setAttribute("msg", "批量删除成功");
        } else  {
            request.setAttribute("msg", "批量删除失败");
        }


        return "forward:/prod/deleteAjaxSplit.action";
    }

    @ResponseBody
    @RequestMapping("/condition")
    public void condition(ProductInfoVo vo, HttpSession session) {

        List<ProductInfo> list = productinfoService.selectCondition(vo);
        session.setAttribute("list", list);
    }

}
