package cn.esign.demo.develop.rest;

import cn.esign.demo.base.constants.OrgCertTypeEnum;
import cn.esign.demo.base.facade.FileFacade;
import cn.esign.demo.base.http.OkHttp3Client;
import cn.esign.demo.base.model.BaseResult;
import cn.esign.demo.base.model.ResultSupport;
import cn.esign.demo.base.provider.*;
import cn.esign.demo.base.provider.request.DocumentBatchAddRequest;
import cn.esign.demo.base.provider.request.DocumentBatchAddRequest.DocumentBean;
import cn.esign.demo.base.provider.request.FlowCreateRequest;
import cn.esign.demo.base.provider.request.FlowCreateRequest.FlowConfigBean;
import cn.esign.demo.base.provider.request.OrganizationAccountCreateRequest;
import cn.esign.demo.base.provider.request.SignfieldHandSignBatchAddRequest;
import cn.esign.demo.base.provider.request.SignfieldHandSignBatchAddRequest.SignfieldHandSignBean;
import cn.esign.demo.base.provider.response.FlowCreateResponse;
import cn.esign.demo.base.provider.response.FlowGetExecuteUrlResponse;
import cn.esign.demo.base.provider.response.OrganizationAccountCreateResponse;
import cn.esign.demo.base.provider.response.SignfieldAddResponse;
import cn.esign.demo.base.utils.CollectionUtils;
import cn.esign.demo.base.utils.FileUtils;
import cn.esign.demo.base.utils.StringUtils;
import cn.esign.demo.base.utils.UUIDUtils;
import cn.esign.demo.develop.constant.DocCache;
import cn.esign.demo.develop.constant.DocEnum;
import cn.esign.demo.develop.model.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@Api(tags = {"流程接口"})
@RequestMapping(value = {"/flow"}, produces = {"application/json;charset=UTF-8"})
public class FlowRest extends BaseRest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowRest.class);


    @ApiOperation(value = "创建手动签署任务", notes = "接口版本说明</br>v1.0.0版本 初始版本")
    @RequestMapping(value = {"handSign"}, method = {RequestMethod.POST}, consumes = {"*/*"})
    public BaseResult<HandSignResponse> handSign(@RequestBody HandSignRequest request)  {
        long startTime = System.currentTimeMillis();
        List<String> fileIds = this.checkAndCreateDoc(request);
        String flowId = this.createFlow(request);
        LOGGER.info("flowId:{}", flowId);
        this.addDocFlow(flowId, fileIds);
        List<Person> persons = request.getPersons();
        List<Organization> organizations = request.getOrganizations();
        List<SignerDTO> personSigners = new ArrayList();
        List<SignerDTO> organizationSigners = new ArrayList();
        String signerAccountId;
        if (CollectionUtils.isNotEmpty(persons)) {
            Iterator iterator = request.getPersons().iterator();

            while(iterator.hasNext()) {
                Person person = (Person)iterator.next();
                signerAccountId = this.createPersonAccount(person);
                this.addTask(flowId, signerAccountId, signerAccountId, fileIds, request);
                personSigners.add(new SignerDTO(signerAccountId, signerAccountId));
            }
        }

        if (CollectionUtils.isNotEmpty(organizations)) {
            Iterator iterator = request.getOrganizations().iterator();
            while(iterator.hasNext()) {
                Organization organization = (Organization)iterator.next();
                signerAccountId = this.createPersonAccount(organization.getCreator());
                String authorizedAccountId = this.createOrganizationAccount(signerAccountId, organization);
                this.addTask(flowId, signerAccountId, authorizedAccountId, fileIds, request);
                organizationSigners.add(new SignerDTO(signerAccountId, authorizedAccountId));
            }
        }

        this.startFlow(flowId);
        HandSignResponse response = this.getSignUrl(flowId, personSigners, organizationSigners);
        long endTime = System.currentTimeMillis();
        LOGGER.info("cost time {}ms", endTime - startTime);
        return BaseResult.success(response);
    }

    private List<String> checkAndCreateDoc(HandSignRequest request) {
        for(String docId : request.getDocIds()) {
            DocEnum.valid(docId);
        }

        List<String> fileIds = new ArrayList();
        FileFacade fileFacade = new FileFacade();

        for(String docId : request.getDocIds()) {
            DocEnum docEnum = DocEnum.getDoc(docId);
            String fileId =  DocCache.DOC.get(docEnum);
            if (StringUtils.isNoneBlank(new CharSequence[]{fileId})) {
                fileIds.add(fileId);
            } else {
                InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(docEnum.getFileName());

                try {
                    byte[] body = FileUtils.readInputStream(inputStream);
                    fileId = fileFacade.uploadFile(body, docEnum.getDesc());
                    LOGGER.info("add fileId:{}", fileId);
                    DocCache.DOC.put(docEnum, fileId);
                    fileIds.add(fileId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return fileIds;
    }

    private String createFlow(HandSignRequest request) {
        FlowProvider flowProvider =  OkHttp3Client.getApi(FlowProvider.class);
        FlowCreateRequest createRequest = new FlowCreateRequest();
        createRequest.setAutoArchive(true);
        createRequest.setBusinessScene("合同签署");
        FlowConfigBean configInfo = new FlowConfigBean();
        createRequest.setConfigInfo(configInfo);
        if (request.isNotice()) {
            configInfo.setNoticeType("1,2");
        } else {
            configInfo.setNoticeType("");
        }

        configInfo.setSignPlatform(request.getSignPlatform());
        configInfo.setRedirectUrl(request.getReturnUrl());
        BaseResult<FlowCreateResponse> result = flowProvider.createFlow(createRequest);
        if (ResultSupport.isFail(result)) {
            throw new RuntimeException("创建流程失败:" + result.getMessage());
        } else {
            return ( result.getData()).getFlowId();
        }
    }

    private void addDocFlow(String flowId, List<String> fileIds) {
        FlowDocProvider docProvider =  OkHttp3Client.getApi(FlowDocProvider.class);
        DocumentBatchAddRequest request = new DocumentBatchAddRequest();
        List<DocumentBean> docs = new ArrayList();
        request.setDocs(docs);
        Iterator fileIdIterator = fileIds.iterator();

        while(fileIdIterator.hasNext()) {
            String fileId = (String)fileIdIterator.next();
            DocumentBean documentBean = new DocumentBean();
            documentBean.setFileId(fileId);
            docs.add(documentBean);
        }

        BaseResult result = docProvider.addDocuments(flowId, request);
        if (ResultSupport.isFail(result)) {
            throw new RuntimeException("添加流程文档失败:" + result.getMessage());
        }
    }

    private void startFlow(String flowId) {
        FlowProvider flowProvider =  OkHttp3Client.getApi(FlowProvider.class);
        BaseResult result = flowProvider.startFlow(flowId);
        if (ResultSupport.isFail(result)) {
            throw new RuntimeException("启动流程失败:" + result.getMessage());
        }
    }

    private String createOrganizationAccount(String creatorId, Organization organization) {
        OrganizationAccountProvider accountProvider =  OkHttp3Client.getApi(OrganizationAccountProvider.class);
        OrganizationAccountCreateRequest request = new OrganizationAccountCreateRequest();
        request.setThirdPartyUserId(UUIDUtils.getUUID());
        request.setCreator(creatorId);
        request.setName(organization.getName());
        request.setIdType(OrgCertTypeEnum.CRED_ORG_USCC.getCertType());
        request.setIdNumber(organization.getIdNumber());
        BaseResult<OrganizationAccountCreateResponse> result = accountProvider.accountCreate(request);
        if (result.getCode() != 0 && result.getCode() != 53000000) {
            throw new RuntimeException("创建企业账号失败:" + result.getMessage());
        } else {
            return ( result.getData()).getOrgId();
        }
    }

    private void addTask(String flowId, String signerAccountId, String authorizedAccountId, List<String> fileIds, HandSignRequest request) {
        FlowSignFieldProvider fieldProvider =  OkHttp3Client.getApi(FlowSignFieldProvider.class);
        SignfieldHandSignBatchAddRequest signBatchAddRequest = new SignfieldHandSignBatchAddRequest();
        List<SignfieldHandSignBean> signfields = new ArrayList();
        signBatchAddRequest.setSignfields(signfields);
        Integer actorIndentityType = signerAccountId.equals(authorizedAccountId) ? 0 : 1;
        Iterator iterator = fileIds.iterator();

        while(iterator.hasNext()) {
            String fileId = (String)iterator.next();
            SignfieldHandSignBean signBean = new SignfieldHandSignBean();
            signBean.setActorIndentityType(actorIndentityType);
            signBean.setFileId(fileId);
            signBean.setSignType(Integer.valueOf(0));
            signBean.setSealType(request.getSealType());
            signBean.setSignerAccountId(signerAccountId);
            signBean.setAuthorizedAccountId(authorizedAccountId);
            signfields.add(signBean);
        }

        BaseResult<SignfieldAddResponse> result = fieldProvider.handDosign(flowId, signBatchAddRequest);
        if (ResultSupport.isFail(result)) {
            throw new RuntimeException("创建签署任务失败:" + result.getMessage());
        }
    }

    private HandSignResponse getSignUrl(String flowId, List<SignerDTO> personSigners, List<SignerDTO> organizationSigners) {
        FlowSignerProvider signerProvider =  OkHttp3Client.getApi(FlowSignerProvider.class);
        List<String> personUrls = new ArrayList();
        Iterator personIterator = personSigners.iterator();

        while(personIterator.hasNext()) {
            SignerDTO signerDTO = (SignerDTO)personIterator.next();
            BaseResult<FlowGetExecuteUrlResponse> result = signerProvider.getFlowExecuteUrl(flowId, signerDTO.getSignerAccountId(),  null, null);
            if (ResultSupport.isFail(result)) {
                throw new RuntimeException("获取签署地址失败:" + result.getMessage());
            }

            personUrls.add((result.getData()).getUrl());
        }

        List<String> organizationUrls = new ArrayList();
        Iterator organizationIterator = organizationSigners.iterator();

        while(organizationIterator.hasNext()) {
            SignerDTO signerDTO = (SignerDTO)organizationIterator.next();
            BaseResult<FlowGetExecuteUrlResponse> result = signerProvider.getFlowExecuteUrl(flowId, signerDTO.getSignerAccountId(), signerDTO.getAuthorizedAccountId(), null);
            if (ResultSupport.isFail(result)) {
                throw new RuntimeException("获取签署地址失败:" + result.getMessage());
            }

            organizationUrls.add((result.getData()).getUrl());
        }

        return new HandSignResponse(personUrls, organizationUrls);
    }
}
