package dev.gabul.pagseguro_flutter;


public interface TaskHandler {

    void onTaskStart();

    void onProgressPublished(String progress, Object transactionInfo);

    void onTaskFinished(Object result);

}