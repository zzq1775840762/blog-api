package com.ziuch.blog.api.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ziuch.blog.api.domain.Content;
import com.ziuch.blog.api.domain.Doc;
import com.ziuch.blog.api.domain.DocExample;
import com.ziuch.blog.api.mapper.ContentMapper;
import com.ziuch.blog.api.mapper.DocMapper;
import com.ziuch.blog.api.req.DocQueryReq;
import com.ziuch.blog.api.req.DocSaveReq;
import com.ziuch.blog.api.resp.DocQueryResp;
import com.ziuch.blog.api.resp.PageResp;
import com.ziuch.blog.api.util.CopyUtil;
import com.ziuch.blog.api.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DocService {

    @Resource
    private DocMapper docMapper;

    @Resource
    private DocServiceCust docCustService;

    @Resource
    private ContentMapper contentMapper;

    @Resource
    private SnowFlake snowFlake;

    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);

    public List<DocQueryResp> all(String ebookId){

        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");

        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andEbookIdEqualTo(ebookId);

        List<Doc> docList = docMapper.selectByExample(docExample);

        //列表copy
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);
        return list;
    }

    public PageResp<DocQueryResp> list(DocQueryReq req){

        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        DocExample.Criteria criteria = docExample.createCriteria();

        if(!ObjectUtils.isEmpty(req.getName()))
            criteria.andNameLike("%" + req.getName() + "%");

        PageHelper.startPage(req.getPage(), req.getSize());
        List<Doc> docList = docMapper.selectByExample(docExample);

        PageInfo<Doc> info = new PageInfo<>(docList);
        LOG.info("总行数：{}", info.getTotal());
        LOG.info("总页数：{}", info.getPages());

//        List<DocResp> docRespList = new ArrayList<>();

//        for(Doc doc : list) {
////            DocResp docResp = new DocResp();
////            BeanUtils.copyProperties(doc, docResp);
//            //对象copy
//            DocResp docResp = CopyUtil.copy(doc, DocResp.class);
//            docRespList.add(docResp);
//        }

        //列表copy
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        PageResp<DocQueryResp>  pageResp = new PageResp();
        pageResp.setTotal(info.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void save(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        Content content = CopyUtil.copy(req, Content.class);

        if(ObjectUtils.isEmpty(req.getId())) {
            doc.setId(snowFlake.nextId());
            doc.setViewCount(0);
            doc.setVoteCount(0);
            docMapper.insert(doc);

            content.setId(doc.getId());
            contentMapper.insert(content);
        }
        else {
            docMapper.updateByPrimaryKey(doc);
            int count = contentMapper.updateByPrimaryKeyWithBLOBs(content);
            if(count == 0) {
                contentMapper.insert(content);
            }
        }
    }

    public void delete(Long id){
        docMapper.deleteByPrimaryKey(id);
    }

    public void delete(List<String> ids){
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
    }

    public String content(Long id){
        Content content = contentMapper.selectByPrimaryKey(id);
        docCustService.viewDoc(id.toString());
        if(content == null){
            return "";
        }
        return content.getContent();
    }
}
