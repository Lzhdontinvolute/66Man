package com.lzh.mq;

import com.lzh.financial.code.domain.dto.MailProp;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MailReceiver  {

    @Autowired
    MailSendUtil mailSendUtil;

    @RabbitListener(queuesToDeclare  = @Queue("send.mail"))
    public void handleMail(MailProp mail){
        mailSendUtil.sendTextMailMessage(mail.getTo(),mail.getSubject(),mail.getText());
    }
}
