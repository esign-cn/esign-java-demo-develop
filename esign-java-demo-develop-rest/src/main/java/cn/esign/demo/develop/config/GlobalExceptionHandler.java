package cn.esign.demo.develop.config;

import cn.esign.demo.base.model.BaseResult;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Component
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public GlobalExceptionHandler() {
    }

    @ResponseBody
    @ExceptionHandler({Throwable.class})
    public BaseResult defaultErrorHandler(HttpServletRequest req, Throwable e) {
        log.error("Controller called error!Catch in defaultErrorHandler", e);
        log.error("Url:{},Method:{}", req.getRequestURL(), req.getMethod());
        return new BaseResult(-1, "系统错误", (Object)null);
    }
}
