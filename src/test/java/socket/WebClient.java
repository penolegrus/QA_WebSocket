package socket;

import java.net.URISyntaxException;
import java.util.Map;

public class WebClient {
    /**
     * Основной клиент с переопределением методов слушателей
     */
    private Client client;

    private WebClient() {
    }

    /**
     * Статичный метод чтобы одноразово создать экземпляр и вызвать метод подключения
     * @return
     */
    public static WebClient getInstance() {
        return new WebClient();
    }

    /**
     * Подключение к сокету
     * @param context необходимая информация во время выполнения подключения
     */
    public void connectToSocket(SocketContext context){
        boolean isBodySent = false;
        boolean isRunnableSent = false;
        try {
            client = new Client(context);
            if (!context.getRequestHeaders().isEmpty()) { //добавляем хэдеры из контекста в подключение к вебсокету
                final Map<String, String> requestHeaderParams = context.getRequestHeaders();
                requestHeaderParams.forEach((key, value) -> {
                    client.addHeader(key, value);
                });
            }
            client.connectBlocking(); //подключаемся к вебсокету до тех пор пока не будет ошибка или сообщение любое
            while (!client.isClosed()) { //пока сообщение активно
                if (client.getAliveTime() >= context.getTimeOut()) { //если общее время подключения сокета превышает таймаут
                    client.close(1006, "Time Out"); //закрываем сокет и завершаем поток
                }
                if (context.getRunnable() != null && !isRunnableSent) { //если есть запускаемая задача, то она запускается
                    context.getRunnable().run();
                    isRunnableSent = true;
                }
                if (context.getBody() != null && !isBodySent) { //есть есть тело, которое нужно отправить, то отправляем
                    client.send(context.getBody());
                    isBodySent = true;
                }
            }
        } catch (URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
    }

    }
}
