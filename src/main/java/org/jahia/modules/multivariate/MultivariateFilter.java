package org.jahia.modules.multivariate;


import com.sun.xml.internal.rngom.ast.builder.Include;
import org.apache.commons.lang.StringUtils;
import org.jahia.services.content.JCRContentUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.jahia.taglibs.template.include.ModuleTag;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.util.List;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: toto
 * Date: 11/28/11
 * Time: 10:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class MultivariateFilter extends AbstractFilter {
    private Random random = new Random();
    private volatile int current = 0;

    @Override
    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {

        List<JCRNodeWrapper> items = JCRContentUtils.getChildrenOfType(resource.getNode(), "jmix:droppableContent");

        String[] config = resource.getNode().getProperty("configuration").getString().split(",");
        double total = 0.;

        for (String s : config) {
            total += Double.parseDouble(s);
        }

        double rand = random.nextFloat();
        double add = 0.f;
        int index;
        for (index = 0; index < config.length; index++) {
            String s = config[index];
            add += Double.parseDouble(s);
            if (add / total > rand) {
                break;
            }
        }

        if (!items.isEmpty()) {

            HttpServletResponse origResponse = renderContext.getResponse();
////            if (!(/renderContext.getResponse() instanceof  MyHttpServletResponseWrapper)) {
//            HttpServletResponseWrapper wrappedResponse = new MyHttpServletResponseWrapper(renderContext);
//            renderContext.setResponse(wrappedResponse);
////            }
//
//            Integer i = (Integer) renderContext.getRequest().getSession().getAttribute("abtest-"+resource.getNode().getIdentifier());
////        if (i == null) {
//            current ++;
//            current %= (int) items.size();
//            i = current; //random.nextInt((int) resource.getNode().getProperty("j:numberOfVersions").getLong());
////            renderContext.getRequest().getSession().setAttribute("abtest-"+resource.getNode().getIdentifier(), i);
////        }
            renderContext.getRequest().setAttribute("multivariate", index);
            renderContext.getRequest().setAttribute("multivariateItem",items.get(index));
//
            Resource r = new Resource(items.get(index), resource.getTemplateType(), null, "module");
            Integer level = (Integer) renderContext.getRequest().getAttribute("org.jahia.modules.level");
            renderContext.getRequest().setAttribute("org.jahia.modules.level", level != null ? level - 1 : 1);

            String out =  service.render(r, renderContext);
            renderContext.getRequest().setAttribute("org.jahia.modules.level", level);
            renderContext.setResponse(origResponse);

            if (renderContext.getRequest().getAttribute("analytics-path") == null) {
                renderContext.getRequest().setAttribute("analytics-path", renderContext.getMainResource().getNode().getUrl());
            }
            renderContext.getRequest().setAttribute("analytics-path", renderContext.getRequest().getAttribute("analytics-path")+"."+resource.getNode().getName()+"="+index );

            return out;
        }
        return null;
    }

    @Override
    public String execute(String previousOut, RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {

        String out = super.execute(previousOut, renderContext, resource, chain);
//        if (renderContext.getResponse() instanceof  MyHttpServletResponseWrapper) {
//            renderContext.setResponse((HttpServletResponse) ((MyHttpServletResponseWrapper) renderContext.getResponse()).getResponse());
//        }
        return out;
    }

//    private static class MyHttpServletResponseWrapper extends HttpServletResponseWrapper {
//        public MyHttpServletResponseWrapper(RenderContext renderContext) {
//            super(renderContext.getResponse());
//        }
//
//        @Override
//        public String encodeURL(String url) {
//            String encodedUrl = super.encodeURL(url);
//            String uri = StringUtils.substringBefore(encodedUrl, "?");
//            String params = StringUtils.substringAfter(encodedUrl, "?");
//            if (params == null) {
//                params =
//            } else {
//                params = "?zz=yy&"+params;
//            }
//            if (params)
//            return url;
//        }
//    }
}
