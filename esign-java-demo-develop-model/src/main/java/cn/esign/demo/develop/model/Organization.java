package cn.esign.demo.develop.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("企业信息")
public class Organization {
    @ApiModelProperty(
            value = "创建人",
            required = true
    )
    private Person creator;
    @ApiModelProperty(
            value = "企业名称",
            required = true
    )
    private String name;
    @ApiModelProperty(
            value = "企业组织信用代码",
            required = true
    )
    private String idNumber;

    public Organization() {
    }


}