package br.com.spring.batch.demo.exceptions;


public class ConnectTimeoutThrowable extends Throwable {
    private static final long serialVersionUID = -1L;

    public ConnectTimeoutThrowable(String s) {
        super(s);
    }
}

