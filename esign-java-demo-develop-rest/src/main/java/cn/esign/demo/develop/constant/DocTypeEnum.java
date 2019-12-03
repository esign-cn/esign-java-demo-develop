package cn.esign.demo.develop.constant;

public enum DocTypeEnum {
    FILE("文件"),
    TEMPLATE("模版");

    private String desc;

    private DocTypeEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return this.desc;
    }
}
