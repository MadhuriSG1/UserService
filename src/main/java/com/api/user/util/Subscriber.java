package com.api.user.util;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class Subscriber {

	@RabbitListener(queues="${jsa.rabbitmq.queue}")
    public void receivedMessage(EmailBody msg) {
        System.out.println("Received Message: " + msg);
        EmailUtil.mailSend(msg.getTo(), msg.getSubject(), msg.getBody());
        System.out.println("Mail sended");

}
}
