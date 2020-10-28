package accommodation;

import accommodation.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{

    @Autowired
    ReservationRepository reservationManagementrepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentCompleted_ChangeResvStatus(@Payload PaymentCompleted paymentcompleted){
        System.out.println(paymentcompleted.toJson());
        if(paymentcompleted.isMe()){
            System.out.println("====================================결제완료 1차====================================");
            if(reservationManagementrepository.findById(paymentcompleted.getReservationNumber()) != null){
                System.out.println("==================================결제완료====================================");
                System.out.println("========= ReservationNumber = " + paymentcompleted.getReservationNumber());
                /*
                Reservation reservationManagement = reservationManagementrepository.findById(paymentcompleted.getReservationNumber()).get();
                reservationManagement.setReserveStatus("paymentComp");
                reservationManagementrepository.save(reservationManagement);
                 */
            }

        }

    }

}
