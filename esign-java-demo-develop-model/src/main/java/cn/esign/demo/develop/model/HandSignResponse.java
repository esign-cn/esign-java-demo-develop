package cn.esign.demo.develop.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("手动签署返回")
public class HandSignResponse {
    @ApiModelProperty("个人签署地址列表")
    private List<String> persons;
    @ApiModelProperty("企业签署地址列表")
    private List<String> organizations;

    public HandSignResponse(List<String> persons, List<String> organizations) {
        this.persons = persons;
        this.organizations = organizations;
    }


}
