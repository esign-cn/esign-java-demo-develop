package cn.esign.demo.develop.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("手动签署")
public class HandSignRequest {
    @ApiModelProperty("重定向地址")
    private String returnUrl;
    @ApiModelProperty("是否发送通知，默认不发送")
    private boolean notice = false;
    @ApiModelProperty("签署平台，逗号分割，1-开放服务h5，2-支付宝小程序，3-微信小程序。 默认1")
    private String signPlatform;
    @ApiModelProperty("印章类型，支持多种类型时逗号分割，0-手绘印章，1-模版印章，为空不限制")
    private String sealType;
    @ApiModelProperty("合同文档ID列表")
    private List<String> docIds;
    @ApiModelProperty("个人签署人列表")
    private List<Person> persons;
    @ApiModelProperty("企业签署人列表")
    private List<Organization> organizations;

    public HandSignRequest() {
    }


}
