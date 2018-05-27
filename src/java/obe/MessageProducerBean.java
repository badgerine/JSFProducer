/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obe;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 *
 * @author sibusiso
 */
@Named(value = "messageProducerBean")
@RequestScoped
public class MessageProducerBean {
    @Resource(mappedName = "jms/prisonQueue")
    private Queue prisonQueue;
    @Resource(mappedName = "jms/prisonQueueFactory")
    private ConnectionFactory prisonQueueFactory;
    
    private String message;

    /**
     * Creates a new instance of MessageProducerBean
     */
    public MessageProducerBean() {
    }
    
    public void send(){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        
        try {    
            sendJMSMessageToPrisonQueue(message);
            FacesMessage facesMessage = new FacesMessage("Message sent");
            facesMessage.setSeverity(FacesMessage.SEVERITY_INFO);
            facesContext.addMessage(null, facesMessage);
            
        } catch (JMSException ex) {
            FacesMessage facesMessage = new FacesMessage("Message NOT sent!");
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            facesContext.addMessage(null, facesMessage);
            Logger.getLogger(MessageProducerBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private Message createJMSMessageForPrisonQueue(Session session, Object messageData) throws JMSException{
        TextMessage msg = session.createTextMessage();
        msg.setText(messageData.toString());
        return msg;
    }
    
    private void sendJMSMessageToPrisonQueue(Object messageData) throws JMSException{
        Connection connection = null;
        Session session = null;
        try{
            connection = prisonQueueFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(prisonQueue);
            producer.send(createJMSMessageForPrisonQueue(session, messageData));
        }
        finally{
            if(session != null){
                session.close();
            }
            if(connection != null){
                connection.close();
            }
        }
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
