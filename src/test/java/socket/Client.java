package socket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

public class Client extends WebSocketClient {
    /**
     * Контекст вебсокета с нужной информацией
     */
    private final SocketContext context;
    /**
     * Время запуска вебсокета
     */
    private Date openedTime;

    /**
     * Создаем клиента и берем ссылку на подключение из контекста
     * @param context контекст вебсокета
     * @throws URISyntaxException
     */
    public Client(SocketContext context) throws URISyntaxException {
        super(new URI(context.getURI()));
        this.context = context;
    }

    /**
     * Получаем промежуток подключения к вебсокету в секундах
     * @return время в секундах
     */
    public int getAliveTime(){
        Date closeDate = new Date();
        int timeInSeconds = (int) (closeDate.getTime() - openedTime.getTime()) / 1000;
        context.setTimeTaken(timeInSeconds);
        return timeInSeconds;
    }

    /**
     * Логика при подключении к вебсокету
     * @param handshakedata The handshake of the websocket instance
     */
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        openedTime = new Date();
        System.out.println("Opened Connection " + context.getURI());
    }

    /**
     * Логика при получении нового сообщения с вебсокета
     * @param message The UTF-8 decoded message that was received.
     */
    @Override
    public void onMessage(String message) {
        System.out.println("Received new message " + message);
        context.getMessageList().add(message); //сохраняем все новые сообщения в список
        String expectedMessage = context.getExpectedMessage();
        if(expectedMessage != null && expectedMessage.equals(message)){//завершаем подключение к сокету если ожидаемое сообщение пришло
            closeConnection(1000,"Received expected message");
        }
    }

    /**
     * Логика при закрытии вебсокета
     * @param code   The codes can be looked up here: {@link CloseFrame}
     * @param reason Additional information string
     * @param remote Returns whether or not the closing of the connection was initiated by the remote
     *               host.
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Close socket with code " + code + ", reason is " + reason);
        context.setStatusCode(code);
    }

    @Override
    public void onError(Exception ex) {

    }


}
