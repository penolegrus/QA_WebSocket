package test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import socket.SocketContext;
import models.SocketMessageModel;
import models.SubscribeModel;
import socket.WebClient;

import java.util.Random;

import static com.codeborne.selenide.Selenide.$x;
import static io.restassured.RestAssured.given;

public class SocketTests {
    private SocketContext context;

    @BeforeEach
    public void initContext(){
        context = new SocketContext();
    }

    /**
     * Генерация рандомной строчки для KuCoin
     * @return
     */
    private String getRandomStringId() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 7;
        Random random = new Random();
        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    /**
     * Получение Socket URL для KuCoin
     * @return
     */
    private String getKucoinSocketConnectUrl() {
        JsonPath response = given()
                .post("https://api.kucoin.com/api/v1/bullet-public")
                .then().log().body().extract()
                .response().jsonPath();
        String token = response.getString("data.token");
        String socketBaseEndpoint = response.getString("data.instanceServers[0].endpoint");
        return socketBaseEndpoint + "?token=" + token + "&connectId=" + getRandomStringId();
    }

    /**
     * Получает информацию об изменении цен BTC-USDT в течении 10 секунд и проверяет, что временые промежутки отличаются
     * @throws JsonProcessingException
     */
    @Test
    public void socketKucoin_DifferentMessageSequenceDataValues() throws JsonProcessingException {
        SubscribeModel subscribeModel = new SubscribeModel(); //создаем тело для отправки подписки на события к вебсокету
        subscribeModel.setId(Math.abs(new Random().nextInt()));
        subscribeModel.setResponse(true);
        subscribeModel.setType("subscribe");
        subscribeModel.setTopic("/market/ticker:BTC-USDT");
        subscribeModel.setPrivateChannel(false);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(subscribeModel); //превращаем объект в строку

        context.setTimeOut(10); //заполняем контекст вебсокета
        context.setBody(json);
        context.setURI(getKucoinSocketConnectUrl());
        WebClient.getInstance().connectToSocket(context); //подключаемся к сокету и ждем таймаут

        //получаем все сообщения и берем первый подходящий
        String firstNormalMessage = context.getMessageList()
                .stream().filter(x -> x.contains("\"type\":\"message\""))
                .findFirst().orElseThrow(() -> new RuntimeException("No normal message found"));

        String lastNormalMessage = context.getMessageList().get(context.getMessageList().size() - 1); //получаем последнее сообщение

        //превращаем сообщение в объект
        SocketMessageModel messageOne = objectMapper.readValue(firstNormalMessage, SocketMessageModel.class);
        SocketMessageModel lastMessage = objectMapper.readValue(lastNormalMessage, SocketMessageModel.class);

        //сравниваем, что временной промежуток первого сообщения и последнего отличаются
        Assertions.assertNotEquals(messageOne.getData().getSequence(), lastMessage.getData().getSequence());
    }

    /**
     * Проверяет что с UI части отправляется сообщение к вебсокету и вебсокет получил его
     */
    @Test
    public void socketUI_SendText() {
        Selenide.open("https://www.piesocket.com/websocket-tester");
        SelenideElement input = $x("//input[@id='email']");
        SelenideElement button = $x("//button[@type='submit']");
        String expectedMessage = "ThreadQA Message";

        String url = input.getValue(); //получаем ссылку на подключение к вебсокету
        button.click();

        Runnable sendUIMessage = new Runnable() { //запускаемый объект который будет запущен во время взаимодействия с вебсокетом
            @Override
            public void run() {
                input.clear();
                input.sendKeys(expectedMessage);
                button.click();
            }
        };
        context.setURI(url);
        context.setExpectedMessage(expectedMessage);
        context.setTimeOut(5);
        context.setRunnable(sendUIMessage);

        WebClient.getInstance().connectToSocket(context);
    }

    /**
     * Проверяет что с Websocket отправляется сообщение и оно появляется в UI
     */
    @Test
    public void socketUI_ReceiveText() {
        Selenide.open("https://www.piesocket.com/websocket-tester");
        SelenideElement input = $x("//input[@id='email']");
        SelenideElement button = $x("//button[@type='submit']");
        SelenideElement consoleLogs = $x("//*[@id='consoleLog']");
        String expectedMessage = "ThreadQA Message";

        String url = input.getValue();
        button.click();

        context = new SocketContext();
        context.setURI(url);
        context.setBody(expectedMessage);
        context.setExpectedMessage(expectedMessage);
        context.setTimeOut(5);

        WebClient.getInstance().connectToSocket(context);
        consoleLogs.shouldHave(Condition.partialText(expectedMessage));
    }


}
