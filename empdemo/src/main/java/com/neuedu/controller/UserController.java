package com.neuedu.controller;

import com.neuedu.entity.User;
import com.neuedu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

@Controller
@RequestMapping(value = {"/user"})
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = {"/registView"})
    public String regitView(){
        return "regist";
    }

    @RequestMapping(value = {"/checkUsername"})
    public void checkUsername(String username, HttpServletResponse resp) throws IOException {
        //根据用户名查询用户
        User user = userService.getUserByUsername(username);
        PrintWriter out = resp.getWriter();
        if (user != null){
            out.print(true);
        }else{
            out.print(false);
        }
    }

    @RequestMapping(value = {"/regist"})
    public String regist(String username, String password, MultipartFile headimg, HttpServletRequest request) throws IOException {
        //把上传逻辑做好
        //1.获取服务器路径
        String uploadPath =  request.getServletContext().getRealPath("/upload/");
        File uploadFile = new File(uploadPath);
        if (!uploadFile.exists()){
            uploadFile.mkdirs();
        }
        //2.抓取上传的文件名
        String fileName = headimg.getOriginalFilename();
        String filePath = uploadPath + UUID.randomUUID().toString() + fileName.substring(fileName.indexOf("."));
        File file = new File(filePath);
        //效率一般
        headimg.transferTo(file);
        //3.获得需要存储到数据库中的路径
        String imgPath = filePath.substring(filePath.lastIndexOf("\\upload"));
        //4.构建一个user对象
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setHeadimg(imgPath);
        userService.saveUser(user);
        return "redirect:/user/loginView";
    }

    @RequestMapping(value = {"/loginView"})
    public String loginView(){
        return "login";
    }

    @RequestMapping(value = {"/login"})
    public void login(String username, String password, HttpServletResponse resp,HttpSession httpSession) throws IOException {
        User user = userService.getUserByUsername(username);
        PrintWriter out = resp.getWriter();
        if (user != null && user.getPassword().equals(password)){
            //登陆成功将用户存入到session中
            httpSession.setAttribute("user",user);
            out.print(true);
        }else {
            out.print(false);
        }
    }
}


