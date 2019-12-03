## 对接demo
演示产品

##### 文档
1. 签署
http://open.esign.cn/docs/wk/


##### 部署
1. 构建
mvn clean package -Dmaven.test.skip

2. 启动命令
nohup java -jar -Dspring.profiles.active=test -Denv=TEST esign-demo-develop-rest-1.0.0-SNAPSHOT.jar >demo.file 2>&1 &