<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>

<template:addResources type="css" resources="multivariate.css"/>
<c:set value="${fn:split(currentNode.properties['configuration'].string, ',')}" var="config"/>

    <c:set value="${0}" var="total"/>
    <c:forEach items="${config}" var="i" varStatus="status">
        <c:set value="${total + i}" var="total"/>
    </c:forEach>

    <c:set value="${jcr:getChildrenOfType(currentNode,'jmix:droppableContent')}" var="nodes"/>

    <c:forEach items="${nodes}" var="subchild" varStatus="status">
        <c:if test="${not empty config[status.index]}">
        <div class="multivariate">
            <div class="multivariate-header">
                Multivariate, variation ${status.index+1} - ${config[status.index]*100/total}% of hits
            </div>
            <template:module node="${subchild}" />
        </div>
        </c:if>
    </c:forEach>

    <c:if test="${renderContext.editMode and fn:length(nodes)< fn:length(config)}">
        <template:module path="*"/>
    </c:if>
