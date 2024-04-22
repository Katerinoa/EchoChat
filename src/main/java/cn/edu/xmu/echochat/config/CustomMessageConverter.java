package cn.edu.xmu.echochat.config;

import cn.edu.xmu.echochat.Bo.Msg;
import jakarta.jms.BytesMessage;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import java.time.LocalDateTime;

public class CustomMessageConverter implements MessageConverter {

    @Override
    public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
        if (object instanceof Msg) {
            Msg msg = (Msg) object;
            BytesMessage bytesMessage = session.createBytesMessage();
            bytesMessage.setByteProperty("messageType", msg.getMessageType());
            bytesMessage.setStringProperty("messageName", msg.getMessageName());
            bytesMessage.setStringProperty("sender", msg.getSender());
            bytesMessage.setStringProperty("receiver", msg.getReceiver());
            bytesMessage.setByteProperty("receiverType", msg.getReceiverType());
            bytesMessage.setStringProperty("sentAt", msg.getSentAt().toString());
            if (msg.getContent() != null)
                bytesMessage.writeBytes(msg.getContent());
            return bytesMessage;
        } else {
            throw new MessageConversionException("Unsupported message type: " + object.getClass());
        }
    }

    @Override
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        if (message instanceof BytesMessage) {
            BytesMessage bytesMessage = (BytesMessage) message;
            Msg msg = new Msg();
            msg.setMessageType(bytesMessage.getByteProperty("messageType"));
            msg.setMessageName(bytesMessage.getStringProperty("messageName"));
            msg.setSender(bytesMessage.getStringProperty("sender"));
            msg.setReceiver(bytesMessage.getStringProperty("receiver"));
            msg.setReceiverType(bytesMessage.getByteProperty("receiverType"));
            msg.setSentAt(LocalDateTime.parse(bytesMessage.getStringProperty("sentAt")));
            byte[] content = new byte[(int) bytesMessage.getBodyLength()];
            bytesMessage.readBytes(content);
            msg.setContent(content);
            return msg;
        } else {
            throw new MessageConversionException("Unsupported message type: " + message.getClass());
        }
    }
}
