package cn.esign.demo.develop.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("个人信息")
public class Person {
    @ApiModelProperty(
            value = "姓名",
            required = true
    )
    private String name;
    @ApiModelProperty(
            value = "身份证号码",
            required = true
    )
    private String idNo;
    @ApiModelProperty(
            value = "手机号",
            required = true
    )
    private String mobile;

    public Person() {
    }


}
