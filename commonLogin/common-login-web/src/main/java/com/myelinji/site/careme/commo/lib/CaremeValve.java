package com.myelinji.site.careme.commo.lib;

import com.alibaba.citrus.service.pipeline.PipelineContext;
import com.alibaba.citrus.service.pipeline.support.AbstractValve;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class CaremeValve extends AbstractValve{

    @Autowired
    HttpSession session;

    String action="init";

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public void invoke(PipelineContext pipelineContext) throws Exception {
        String userId=(String)session.getAttribute("userIDNum");
        System.out.println("User Is Here!..."+userId);
        pipelineContext.invokeNext();
    }
}
