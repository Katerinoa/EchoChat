package cn.edu.xmu.echochat.config;

import cn.edu.xmu.echochat.Bo.Msg;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import jakarta.jms.BytesMessage;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;

public class CustomMessageConverter implements MessageConverter {

    @Override
    public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
        if (object instanceof Msg) {
            Msg msg = (Msg) object;
            BytesMessage bytesMessage = session.createBytesMessage();
            bytesMessage.setLongProperty("id", msg.getId());
            bytesMessage.setLongProperty("senderId", msg.getSenderId());
            bytesMessage.setLongProperty("receiverId", msg.getReceiverId());
            bytesMessage.setByteProperty("messageType", msg.getMessageType());
            bytesMessage.setStringProperty("content", msg.getContent());
            bytesMessage.setStringProperty("fileType", msg.getFileType());
            if (msg.getFileContent() != null)
                bytesMessage.writeBytes(msg.getFileContent());
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
            msg.setId(bytesMessage.getLongProperty("id"));
            msg.setSenderId(bytesMessage.getLongProperty("senderId"));
            msg.setReceiverId(bytesMessage.getLongProperty("receiverId"));
            msg.setMessageType(bytesMessage.getByteProperty("messageType"));
            msg.setContent(bytesMessage.getStringProperty("content"));
            msg.setFileType(bytesMessage.getStringProperty("fileType"));
            byte[] fileContent = new byte[(int) bytesMessage.getBodyLength()];
            bytesMessage.readBytes(fileContent);
            msg.setFileContent(fileContent);
            return msg;
        } else {
            throw new MessageConversionException("Unsupported message type: " + message.getClass());
        }
    }
}
