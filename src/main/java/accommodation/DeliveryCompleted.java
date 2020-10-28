package accommodation;

/*
* 배송이 완료되면, 메시지 수신하여..완료 처리 함. Reservation 에서 이벤트 메시지 수신.
* */
public class DeliveryCompleted extends AbstractEvent {
    private Integer deliveryNumber;
    private String deliveryStatus;

    // 예약 정보
    private Integer reservationNumber;
    // 고객 정보
    private Integer customerId;
    private String customerName;
    private String customerAddress;
    private String customerContract;

    public DeliveryCompleted(){
        super();
    }

    // Getter & Setters

    public Integer getDeliveryNumber() {
        return deliveryNumber;
    }

    public void setDeliveryNumber(Integer deliveryNumber) {
        this.deliveryNumber = deliveryNumber;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public Integer getReservationNumber() {
        return reservationNumber;
    }

    public void setReservationNumber(Integer reservationNumber) {
        this.reservationNumber = reservationNumber;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerContract() {
        return customerContract;
    }

    public void setCustomerContract(String customerContract) {
        this.customerContract = customerContract;
    }
}
