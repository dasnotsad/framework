package net.dasnotsad.framework.elasticsearch.exception;

public class EsException extends RuntimeException {

    private static final long serialVersionUID = 4542506854695451067L;

    /**
     * 异常类型
     */
    private String type;

    /**
     * 异常原因
     */
    private String reason;

    /**
     * 异常堆栈信息
     */
    private String stack;

    public EsException() {
        buildMessage();
    }

    public EsException(String message) {
        super(message);
        buildMessage();
    }

    public EsException(Throwable cause) {
        super(cause);
        buildMessage();
    }

    public EsException(String message, Throwable cause) {
        super(message, cause);
        buildMessage();
    }

    public EsException(Throwable cause, String message) {
        super(message, cause);
        buildMessage();
    }

    /**
     * 编译ES异常信息
     */
    private void buildMessage() {
        if (this.getMessage() != null && this.getMessage().contains("Elasticsearch")) {
            int start = this.getMessage().indexOf('[');
            int end = this.getMessage().lastIndexOf(']');
            if (start > 0 && end > 0) {
                String[] str = this.getMessage().substring(start + 1, end).split(",");
                if (str.length >= 2) {
                    String[] s_type = str[0].split("=");
                    if (s_type.length == 2 && "type".equals(s_type[0].trim())) {
                        this.type = s_type[1];
                    }

                    String[] s_reason = str[1].split("=");
                    if (s_reason.length == 2 && "reason".equals(s_reason[0].trim())) {
                        this.reason = s_reason[1];
                    }

                    if (str.length == 3) {
                        String[] s_stack = str[2].split("=");
                        if (s_stack.length == 2 && "stack_trace".equals(s_stack[0].trim())) {
                            this.stack = s_stack[1];
                        }
                    }
                }
            }
        }
    }

    public String getType() {
        return type;
    }

    public String getReason() {
        return reason != null ? reason : this.getMessage();
    }

    public String getStack() {
        return stack;
    }
}