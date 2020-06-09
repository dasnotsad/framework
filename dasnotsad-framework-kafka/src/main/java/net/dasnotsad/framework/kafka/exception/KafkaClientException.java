package net.dasnotsad.framework.kafka.exception;

public class KafkaClientException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 4542506854695451067L;

    public KafkaClientException(String message) {
        super(message);
    }
}