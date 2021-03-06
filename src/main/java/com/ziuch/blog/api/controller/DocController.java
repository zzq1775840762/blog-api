package com.ziuch.blog.api.controller;

import com.ziuch.blog.api.req.DocQueryReq;
import com.ziuch.blog.api.req.DocSaveReq;
import com.ziuch.blog.api.resp.CommonResp;
import com.ziuch.blog.api.resp.DocQueryResp;
import com.ziuch.blog.api.resp.PageResp;
import com.ziuch.blog.api.service.DocService;
import com.ziuch.blog.api.service.DocServiceCust;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/doc")
@Api(value = "测试接口", tags = "文档管理相关的接口")
public class DocController {

    @Resource
    private DocService docService;

    @Resource
    private DocServiceCust docServiceCust;

    @GetMapping("/all/{ebookId}")
    public CommonResp all(@PathVariable String ebookId){
        CommonResp<List<DocQueryResp>> resp = new CommonResp<>();
        List<DocQueryResp> list = docService.all(ebookId);
        resp.setContent(list);
        return resp;
    }

    @GetMapping("/list")
    public CommonResp list(@Valid DocQueryReq req){
        CommonResp<PageResp<DocQueryResp>> resp = new CommonResp<>();
        PageResp<DocQueryResp> list = docService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody DocSaveReq req){
        CommonResp resp = new CommonResp<>();
        docService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{idsStr}")
    public CommonResp delete(@PathVariable String idsStr){
        CommonResp resp = new CommonResp<>();
        List<String> list = Arrays.asList(idsStr.split(","));
        docService.delete(list);
        return resp;
    }

    @GetMapping("/content/{id}")
    public CommonResp content(@PathVariable Long id){
        CommonResp<String> resp = new CommonResp<>();
        String content = docService.content(id);
        resp.setContent(content);
        return resp;
    }

    @GetMapping("/vote/{id}")
    public CommonResp vote(@PathVariable Long id){
        CommonResp<String> resp = new CommonResp<>();
        docServiceCust.voteDoc(id.toString());
        return resp;
    }
}
