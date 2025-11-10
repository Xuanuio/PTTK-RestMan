package com.pttk.restman.model;

import java.time.LocalDate;

public class TableReservation {
    private Integer id;
    private String timeSlot;
    private LocalDate reservationDate;
    private boolean checkedIn;
    private String bookingType;
    private Integer invoiceId;
    private int customerId;

    public TableReservation() {}

    public TableReservation(String timeSlot, LocalDate reservationDate, boolean checkedIn, String bookingType, Integer invoiceId, int customerId) {
        this.timeSlot = timeSlot;
        this.reservationDate = reservationDate;
        this.checkedIn = checkedIn;
        this.bookingType = bookingType;
        this.invoiceId = invoiceId;
        this.customerId = customerId;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public LocalDate getReservationDate() { return reservationDate; }
    public void setReservationDate(LocalDate reservationDate) { this.reservationDate = reservationDate; }

    public boolean isCheckedIn() { return checkedIn; }
    public void setCheckedIn(boolean checkedIn) { this.checkedIn = checkedIn; }

    public String getBookingType() { return bookingType; }
    public void setBookingType(String bookingType) { this.bookingType = bookingType; }

    public Integer getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Integer invoiceId) { this.invoiceId = invoiceId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
}
