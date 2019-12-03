package cn.esign.demo.develop.constant;

/**
 * 合同类型
 */
public enum DocEnum {
    TEST_PDF(DocTypeEnum.FILE, "show.pdf", "测试文件.pdf");

    private DocTypeEnum docTypeEnum;
    private String fileName;
    private String desc;

    private DocEnum(DocTypeEnum docTypeEnum, String fileName, String desc) {
        this.docTypeEnum = docTypeEnum;
        this.fileName = fileName;
        this.desc = desc;
    }

    public DocTypeEnum getDocTypeEnum() {
        return this.docTypeEnum;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getDesc() {
        return this.desc;
    }

    public static boolean valid(String name) {
        for(DocEnum docEnum :  values()) {
            if (docEnum.name().equals(name)) {
                return true;
            }
        }

        return false;
    }

    public static DocEnum getDoc(String name) {
        for(DocEnum docEnum:  values()) {
            if (docEnum.name().equals(name)) {
                return docEnum;
            }
        }

        return TEST_PDF;
    }
}
