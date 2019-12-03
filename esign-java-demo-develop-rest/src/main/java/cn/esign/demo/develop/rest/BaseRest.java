package cn.esign.demo.develop.rest;

import cn.esign.demo.base.constants.PersonCertTypeEnum;
import cn.esign.demo.base.http.OkHttp3Client;
import cn.esign.demo.base.model.BaseResult;
import cn.esign.demo.base.provider.PersonAccountProvider;
import cn.esign.demo.base.provider.request.PersonAccountCreateRequest;
import cn.esign.demo.base.provider.response.PersonAccountCreateResponse;
import cn.esign.demo.base.utils.UUIDUtils;
import cn.esign.demo.develop.model.Person;

public abstract class BaseRest {
    public BaseRest() {
    }

    protected String createPersonAccount(Person person) {
        PersonAccountProvider accountProvider =  OkHttp3Client.getApi(PersonAccountProvider.class);
        PersonAccountCreateRequest request = new PersonAccountCreateRequest();
        request.setThirdPartyUserId(UUIDUtils.getUUID());
        request.setName(person.getName());
        request.setIdType(PersonCertTypeEnum.CRED_PSN_CH_IDCARD.getCertType());
        request.setIdNumber(person.getIdNo());
        request.setMobile(person.getMobile());
        BaseResult<PersonAccountCreateResponse> result = accountProvider.accountCreate(request);
        if (result.getCode() != 0 && result.getCode() != 53000000) {
            throw new RuntimeException("创建个人账号失败:" + result.getMessage());
        } else {
            return (result.getData()).getAccountId();
        }
    }
}
