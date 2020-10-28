package accommodation;

import javax.persistence.*;

import accommodation.external.Payment;
import accommodation.external.PaymentManagementService;
import accommodation.external.Delivery;
import accommodation.external.DeliveryService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import accommodation.config.kafka.KafkaProcessor;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeTypeUtils;

@Entity
@Table(name="ReservationManagement_table")
public class Reservation {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer reservationNumber;
    private String customerName;
    private Integer customerId;
    private String reserveStatus;
    private Integer roomNumber;
    private Integer PaymentPrice;
    // 추가, 배송 요청
    private String deliveryStatus;

    @PrePersist
    public void onPrePersist(){

        // 배송 요청 추가 -> Rest
        if ("DeliveryRequested".equals(deliveryStatus) ) {
            System.out.println("=============배송 요청 진행=============");
            System.out.println("====== 2 : deliveryStatus    = " + this.getDeliveryStatus());
            System.out.println("====== 3 : reservationNumber = " + this.getReservationNumber());
            System.out.println("====== 4 : customerId        = " + this.getCustomerId());
            System.out.println("====== 5 : customerName      = " + this.getCustomerName());
            Delivery delivery = new Delivery();

            delivery.setDeliveryStatus(this.getDeliveryStatus());
            delivery.setReservationNumber(this.getReservationNumber());
            delivery.setCustomerId(this.getCustomerId());
            delivery.setCustomerName(this.getCustomerName());

            Application.applicationContext.getBean(DeliveryService.class).RequestDelivery(delivery);
            System.out.println("RequestCompleted ReserReservationNumber= " + getReservationNumber());
        }

        setReserveStatus("reserve");
        Reserved reserved = new Reserved();
        reserved.setReservationNumber(this.getReservationNumber());
        reserved.setReserveStatus(this.getReserveStatus());
        reserved.setCustomerName(this.getCustomerName());
        reserved.setCustomerId(this.getCustomerId());
        reserved.setRoomNumber(this.getRoomNumber());
        reserved.setPaymentPrice(this.getPaymentPrice());

        reserved.publishAfterCommit();
    }

    @PreUpdate
    public void onPreUpdate(){

        // 배송 요청 추가 DeliveryCompleted -> 이벤트 메시지 발송..
        if ("DeliveryCompleted".equals(deliveryStatus) ) {
            System.out.println("=============배송 완료 진행=============");
            System.out.println("====== 2 : deliveryStatus    = " + this.getDeliveryStatus());
            System.out.println("====== 3 : reservationNumber = " + this.getReservationNumber());
            System.out.println("====== 4 : customerId        = " + this.getCustomerId());
            System.out.println("====== 5 : customerName      = " + this.getCustomerName());

            DeliveryCompleted deliveryCompleted = new DeliveryCompleted();

            deliveryCompleted.setDeliveryStatus(deliveryStatus);
            deliveryCompleted.setReservationNumber(reservationNumber);
            deliveryCompleted.setCustomerId(customerId);
            deliveryCompleted.setCustomerName(customerName);

            ObjectMapper objectMapper = new ObjectMapper();
            String json = null;

            try {
                json = objectMapper.writeValueAsString(deliveryCompleted);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("JSON format exception", e);
            }

            KafkaProcessor processor = Application.applicationContext.getBean(KafkaProcessor.class);
            MessageChannel outputChannel = processor.outboundTopic();

            outputChannel.send(MessageBuilder
                    .withPayload(json)
                    .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                    .build());
            System.out.println(deliveryCompleted.toJson());
            deliveryCompleted.publishAfterCommit();
        }

        if("payment".equals(this.getReserveStatus())){
            Reserved reserved = new Reserved();
            reserved.setReservationNumber(this.getReservationNumber());
            reserved.setReserveStatus(this.getReserveStatus());
            reserved.setCustomerName(this.getCustomerName());
            reserved.setCustomerId(this.getCustomerId());
            reserved.setRoomNumber(this.getRoomNumber());
            reserved.setPaymentPrice(this.getPaymentPrice());
            ObjectMapper objectMapper = new ObjectMapper();
            String json = null;

            try {
                json = objectMapper.writeValueAsString(reserved);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("JSON format exception", e);
            }

            KafkaProcessor processor = Application.applicationContext.getBean(KafkaProcessor.class);
            MessageChannel outputChannel = processor.outboundTopic();

            outputChannel.send(MessageBuilder
                    .withPayload(json)
                    .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                    .build());


            //Following code causes dependency to external APIs
            // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

            //fooddelivery.external.결제이력 결제이력 = new fooddelivery.external.결제이력();

            Payment payment = new Payment();
            System.out.println("1 : " + getReservationNumber());
            System.out.println("2 : " + getPaymentPrice());
            System.out.println("3 : " + getReserveStatus());

            payment.setPaymentPrice(getPaymentPrice());
            payment.setReservationNumber(getReservationNumber());
            payment.setReservationStatus(getReserveStatus());
            //paymentManagement.CompletePayment(getReservationNumber(), getPaymentPrice(), getReserveStatus());

            Application.applicationContext.getBean(PaymentManagementService.class).CompletePayment(payment);
            //setReserveStatus("paymentComp");
            System.out.println("PaymentCompleted ReserReservationNumber= " + getReservationNumber());
        }
        else if("checkOut".equals(this.getReserveStatus())){
            CheckedOut checkedOut = new CheckedOut();
            checkedOut.setReservationNumber(this.getReservationNumber());
            checkedOut.setReserveStatus(this.getReserveStatus());
            checkedOut.setCustomerName(this.getCustomerName());
            checkedOut.setCustomerId(this.getCustomerId());
            checkedOut.setRoomNumber(this.getRoomNumber());
            ObjectMapper objectMapper = new ObjectMapper();
            String json = null;

            try {
                json = objectMapper.writeValueAsString(checkedOut);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("JSON format exception", e);
            }

            KafkaProcessor processor = Application.applicationContext.getBean(KafkaProcessor.class);
            MessageChannel outputChannel = processor.outboundTopic();

            outputChannel.send(MessageBuilder
                    .withPayload(json)
                    .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                    .build());
            System.out.println(checkedOut.toJson());
            checkedOut.publishAfterCommit();
            setReserveStatus("End");
        }
    }


    public Integer getReservationNumber() {
        return reservationNumber;
    }

    public void setReservationNumber(Integer reservationNumber) {
        this.reservationNumber = reservationNumber;
    }
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
    public String getReserveStatus() {
        return reserveStatus;
    }

    public void setReserveStatus(String reserveStatus) {
        this.reserveStatus = reserveStatus;
    }
    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Integer getPaymentPrice() {
        return PaymentPrice;
    }

    public void setPaymentPrice(Integer paymentPrice) {
        PaymentPrice = paymentPrice;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}
