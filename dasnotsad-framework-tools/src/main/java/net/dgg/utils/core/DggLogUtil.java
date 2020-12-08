package net.dgg.utils.core;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import net.dgg.utils.constant.CharConstant;
import net.dgg.utils.constant.StringPool;
import net.dgg.utils.exception.DggUtilException;
import net.dgg.utils.string.DggStringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FastByteArrayOutputStream;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 日志工具类
 * @Version 1.0.0
 * @Author Created by yan.x on 2019-06-20 .
 * @Copyright © HOT SUN group.All Rights Reserved.
 **/
public final class DggLogUtil {

    private final static Logger log = LoggerFactory.getLogger(DggLogUtil.class);

    private DggLogUtil() {
    }

    /**
     * 返回一个新的异常，统一构建，方便统一处理
     *
     * @param msg 消息
     * @param t   异常信息
     * @return 返回异常
     */
    public static DggUtilException build(String msg, Throwable t, Object... params) {
        return new DggUtilException(DggStringUtil.format(msg, params), t);
    }

    /**
     * 重载的方法
     *
     * @param msg 消息
     * @return 返回异常
     */
    public static DggUtilException build(String msg, Object... params) {
        return new DggUtilException(DggStringUtil.format(msg, params));
    }

    /**
     * 重载的方法
     *
     * @param t 异常
     * @return 返回异常
     */
    public static DggUtilException build(Throwable t) {
        return new DggUtilException(t);
    }

    /**
     * 获得完整消息，包括异常名
     *
     * @param e 异常
     * @return 完整消息
     */
    public static String getMessage(Throwable e) {
        return DggStringUtil.format("{}: {}", e.getClass().getSimpleName(), e.getMessage());
    }

    /**
     * 使用运行时异常包装编译异常
     *
     * @param throwable 异常
     * @return 运行时异常
     */
    public static RuntimeException wrapRuntime(Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            return (RuntimeException) throwable;
        } else {
            return new RuntimeException(throwable);
        }
    }

    /**
     * 剥离反射引发的InvocationTargetException、UndeclaredThrowableException中间异常，返回业务本身的异常
     *
     * @param wrapped 包装的异常
     * @return 剥离后的异常
     */
    public static Throwable unwrap(Throwable wrapped) {
        Throwable unwrapped = wrapped;
        while (true) {
            if (unwrapped instanceof InvocationTargetException) {
                unwrapped = ((InvocationTargetException) unwrapped).getTargetException();
            } else if (unwrapped instanceof UndeclaredThrowableException) {
                unwrapped = ((UndeclaredThrowableException) unwrapped).getUndeclaredThrowable();
            } else {
                return unwrapped;
            }
        }
    }

    /**
     * 获取当前栈信息
     *
     * @return 当前栈信息
     */
    public static StackTraceElement[] getStackElements() {
        return Thread.currentThread().getStackTrace();
    }


    /**
     * 堆栈转为单行完整字符串
     *
     * @param throwable 异常对象
     * @return 堆栈转为的字符串
     */
    public static String stacktraceToOneLineString(Throwable throwable) {
        return stacktraceToOneLineString(throwable, 3000);
    }

    /**
     * 堆栈转为单行完整字符串
     *
     * @param throwable 异常对象
     * @param limit     限制最大长度
     * @return 堆栈转为的字符串
     */
    public static String stacktraceToOneLineString(Throwable throwable, int limit) {
        Map<Character, String> replaceCharToStrMap = new HashMap<>();
        replaceCharToStrMap.put(CharConstant.CR, StringPool.SPACE);
        replaceCharToStrMap.put(CharConstant.LF, StringPool.SPACE);
        replaceCharToStrMap.put(CharConstant.TAB, StringPool.SPACE);
        return stacktraceToString(throwable, limit, replaceCharToStrMap);
    }

    /**
     * 堆栈转为完整字符串
     *
     * @param throwable 异常对象
     * @return 堆栈转为的字符串
     */
    public static String stacktraceToString(Throwable throwable) {
        return stacktraceToString(throwable, 3000);
    }

    /**
     * 堆栈转为完整字符串
     *
     * @param throwable 异常对象
     * @param limit     限制最大长度
     * @return 堆栈转为的字符串
     */
    public static String stacktraceToString(Throwable throwable, int limit) {
        return stacktraceToString(throwable, limit, null);
    }

    /**
     * 堆栈转为完整字符串
     *
     * @param throwable           异常对象
     * @param limit               限制最大长度
     * @param replaceCharToStrMap 替换字符为指定字符串
     * @return 堆栈转为的字符串
     */
    public static String stacktraceToString(Throwable throwable, int limit, Map<Character, String> replaceCharToStrMap) {
        final FastByteArrayOutputStream baos = new FastByteArrayOutputStream();
        throwable.printStackTrace(new PrintStream(baos));
        String exceptionStr = baos.toString();
        int length = exceptionStr.length();
        if (limit > 0 && limit < length) {
            length = limit;
        }

        if (!CollectionUtils.isEmpty(replaceCharToStrMap)) {
            final StringBuilder sb = new StringBuilder();
            char c;
            String value;
            for (int i = 0; i < length; i++) {
                c = exceptionStr.charAt(i);
                value = replaceCharToStrMap.get(c);
                if (null != value) {
                    sb.append(value);
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        } else {
            return exceptionStr;
        }
    }

    /**
     * info日志记录
     *
     * @param mark   : 标志信息
     * @param params : 请求参数
     */
    public static void info(Logger logger, String mark, Object... params) {
        try {
            if (logger.isInfoEnabled()) {
                logger.info("\n<================== [ mark：{} ][ params：{} ]", mark, JSONObject.toJSONString(params, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect));
            }
        } catch (Exception e1) {
        }
    }

    /**
     * info日志记录
     *
     * @param mark   : 标志信息
     * @param params : 请求参数
     */
    public static void infoFor(Logger logger, String mark, Object... params) {
        try {
            if (logger.isInfoEnabled()) {
                if (null == params || params.length < 1) {
                    logger.info("<================== [ mark：{} ]", mark);
                } else {
                    logger.info("\n<================== [ mark：{} ][ params：{} ]", mark, JSONObject.toJSONString(params, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue))
                    ;
                }
            }
        } catch (Exception e1) {
        }
    }

    /**
     * info日志记录
     *
     * @param mark   : 标志信息
     * @param params : 请求参数
     */
    public static void info(String mark, Object... params) {
        try {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
            String className = stackTraceElement.getClassName();
            String methodName = stackTraceElement.getMethodName();
            Logger logger = LoggerFactory.getLogger(className);
            if (logger.isInfoEnabled()) {
                logger.info("\n<====== {}.{} [ mark：{} ][ params：{} ]", className, methodName, mark, JSONObject.toJSONString(params, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect));
            }
        } catch (Exception e1) {
        }
    }

    /**
     * info日志记录
     *
     * @param mark   : 标志信息
     * @param params : 请求参数
     */
    public static void debugFor(Logger logger, String mark, Object... params) {
        try {
            if (logger.isDebugEnabled()) {
                if (null == params || params.length < 1) {
                    logger.debug("<================== [ mark：{} ]", mark);
                } else {
                    logger.debug("\n<================== [ mark：{} ] {}", mark, JSONObject.toJSONString(params, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue));
                }
            }
        } catch (Exception e1) {
        }
    }

    /**
     * debug日志记录
     *
     * @param mark   : 标志信息
     * @param params : 请求参数
     */
    public static void debug(Logger logger, String mark, Object... params) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("\n[ ************************* {} ************************* ]\n[ params：{} ]", mark, JSONObject.toJSONString(params, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue));
            }
        } catch (Exception e1) {
        }
    }

    /**
     * debug日志记录
     *
     * @param format : 消息模版
     * @param params : 请求参数
     */
    public static void debug(String format, Object... params) {
        try {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
            Logger logger = LoggerFactory.getLogger(stackTraceElement.getClassName());
            if (logger.isDebugEnabled()) {
                logger.debug(format, params);
            }
        } catch (Exception e1) {
        }
    }

    /**
     * 异常日志记录
     *
     * @param mark : 标志/请求api信息
     */
    public static void error(String mark) {
        try {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
            String className = stackTraceElement.getClassName();
            Logger logger = LoggerFactory.getLogger(className);
            if (logger.isErrorEnabled()) {
                logger.error(mark);
            }
        } catch (Exception e1) {
            log.error(mark);
        }
    }

    /**
     * 异常日志记录
     *
     * @param e    : 异常信息
     * @param mark : 标志/请求api信息
     */
    public static void error(String mark, Exception e) {
        try {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
            String className = stackTraceElement.getClassName();
            String methodName = stackTraceElement.getMethodName();
            String message = getMessage(e);
            Logger logger = LoggerFactory.getLogger(className);
            if (logger.isErrorEnabled()) {
                logger.error("\n<== {}.{} [ time：{} ][ mark：{} ][ excption: {} ] ", className, methodName, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), mark, message, e);
            }
        } catch (Exception e1) {
            log.error(getMessage(e));
        }
    }

    /**
     * 异常日志记录
     *
     * @param e      : 异常信息
     * @param mark   : 标志/请求api信息
     * @param params : 请求参数
     */
    public static void error(Exception e, String mark, Object... params) {
        try {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
            String className = stackTraceElement.getClassName();
            String methodName = stackTraceElement.getMethodName();
            String message = getMessage(e);
            Logger logger = LoggerFactory.getLogger(className);
            if (logger.isErrorEnabled()) {
                logger.error("\n<== {}.{} [ time：{} ][ mark：{} ][ params：{} ][ excption: {} ] ", className, methodName, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), mark, JSONObject.toJSONString(params, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue), message, e);
            }
        } catch (Exception e1) {
            log.error(getMessage(e));
        }
    }

    /**
     * 异常日志记录
     *
     * @param mark   : 标志/请求api信息
     * @param params : 请求参数
     */
    public static void error(String mark, String message, Object... params) {
        try {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
            String className = stackTraceElement.getClassName();
            String methodName = stackTraceElement.getMethodName();
            Logger logger = LoggerFactory.getLogger(className);
            if (logger.isErrorEnabled()) {
                logger.error("\n<== {}.{} [ time：{} ][ mark：{} ][ params：{} ][ excption: {} ] ", className, methodName, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), mark, JSONObject.toJSONString(params, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue), message);
            }
        } catch (Exception e1) {
            log.error(getMessage(e1));
        }
    }

    /**
     * 异常日志记录
     *
     * @param mark   : 标志/请求api信息
     * @param params : 请求参数
     */
    public static void error(String mark, String message, List params) {
        try {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
            String className = stackTraceElement.getClassName();
            String methodName = stackTraceElement.getMethodName();
            Logger logger = LoggerFactory.getLogger(className);
            if (logger.isErrorEnabled()) {
                logger.error("\n<== {}.{} [ time：{} ][ mark：{} ][ params：{} ][ excption: {} ] ", className, methodName, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), mark, JSONObject.toJSONString(params, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue), message);
            }
        } catch (Exception e1) {
            log.error(getMessage(e1));
        }
    }

    public static void error(Logger logger, String path, String mark, Object... params) {
        try {
            if (logger.isErrorEnabled()) {
                if (null != params && params.length >= 1) {
                    logger.error("\n\n\n<== [ mark：{} ][ path：{} ][ params：{} ] \n\n\n", mark, path, JSONObject.toJSONString(params, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue));
                } else {
                    logger.error("\n\n\n<== [ mark：{} ][ path：{} ] \n\n\n", mark, path);
                }
            }
        } catch (Exception e1) {
            logger.error("", e1);
        }
    }

    public static void error(Logger logger, Exception e, String path, String mark, Object... params) {
        try {
            if (logger.isErrorEnabled()) {
                if (null != params && params.length >= 1) {
                    logger.error("\n\n\n<== [ mark：{} ][ path：{} ][ params：{} ][ excption: {} ] \n\n\n", mark, path, JSONObject.toJSONString(params, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue), getMessage(e), e);
                } else {
                    logger.error("\n\n\n<== [ mark：{} ][ path：{} ][ excption: {} ] \n\n\n", mark, path, getMessage(e), e);
                }
            }
        } catch (Exception e1) {
            logger.error("", e1);
        }
    }


    /**
     * 调用异常日志记录
     *
     * @param mark   : 标志/请求api信息
     * @param params : 请求参数
     */
    public static void callError(Exception e, String mark, String apiPath, Object... params) {
        try {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
            String className = stackTraceElement.getClassName();
            Logger logger = LoggerFactory.getLogger(className);
            if (logger.isErrorEnabled()) {
                if (null == e) {
                    logger.error("\n<== 请求 {} 失败 [ time：{} ][ mark：{} ][ api：{} ][ params：{} ]", mark, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), mark, apiPath, null == params ? null : JSONObject.toJSONString(params, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue));
                } else {
                    logger.error("\n<== 请求 {} 失败 [ time：{} ][ mark：{} ][ api：{} ][ params：{} ][ excption: {} ] ", mark, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), mark, apiPath, null == params ? null : JSONObject.toJSONString(params, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue), e);
                }
            }
        } catch (Exception e1) {
            log.error(getMessage(e1));
        }
    }
}
